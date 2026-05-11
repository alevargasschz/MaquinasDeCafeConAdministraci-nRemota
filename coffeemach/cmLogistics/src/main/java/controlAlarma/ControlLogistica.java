package controlAlarma;

import com.zeroc.Ice.Communicator;

import servicios.ServicioAbastecimientoPrx;
import servicios.ServicioBodegaPrx;
import servicios.ServicioComLogisticaPrx;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * ControlLogistica — CORREGIDO
 *
 * BUGS CORREGIDOS EN ESTA VERSION:
 *
 * [BUG CRITICO] La version anterior llamaba normalizarTipo() sobre los tipos
 * centrales (1-6) que vienen de la BD, convirtiendo tipo 1 (Ingredientes)
 * en tipo 6 (Mantenimiento). Eso causaba que:
 *   - La maquina borrara la alarma de mantenimiento (local "1") en vez de
 *     recargar ingredientes.
 *   - La GUI se re-habilitara sin haber recargado nada.
 *   - El cafe siguiera bajando y llegara a valores negativos.
 *
 * SOLUCION: los tipos 1-6 que vienen de la BD ya SON los tipos centrales
 * correctos. No se normalizan. En cambio, se convierten al codigo LOCAL
 * que entiende ControladorMQ.abastecer() usando COD_LOCAL_POR_TIPO_CENTRAL.
 *
 * Tabla de correspondencia:
 *   Central 1 (Ingredientes)       → local 8  → machine case 1: recargar ingredientes
 *   Central 2 (Moneda $100)        → local 2  → machine case 2: recargar moneda 100
 *   Central 3 (Moneda $200)        → local 4  → machine case 3: recargar moneda 200
 *   Central 4 (Moneda $500)        → local 6  → machine case 4: recargar moneda 500
 *   Central 5 (Suministros)        → local 5  → machine case 5: recargar vasos
 *   Central 6 (Mal funcionamiento) → local 1  → machine case 6: mantenimiento
 */
public class ControlLogistica {

    private static final String CTX_TRACE_ID = "traceId";
    private static final String CTX_OPERADOR  = "operadorId";
    private static final String CTX_MAQUINA   = "maquinaId";
    private static final String CTX_ALARMA    = "tipoAlarma";

    // Tipos CENTRALES (como estan en la BD y en ServidorCentral/alarma/Alarma.java)
    public static final int ALARMA_INGREDIENTE         = 1;
    public static final int ALARMA_MONEDA_CIEN         = 2;
    public static final int ALARMA_MONEDA_DOSCIENTOS   = 3;
    public static final int ALARMA_MONEDA_QUINIENTOS   = 4;
    public static final int ALARMA_SUMINISTRO          = 5;
    public static final int ALARMA_MAL_FUNCIONAMIENTO  = 6;

    // Item a retirar de Bodega segun tipo CENTRAL
    private static final Map<Integer, String> ITEM_POR_ALARMA = Map.of(
            ALARMA_INGREDIENTE,        "Cafe",
            ALARMA_MONEDA_CIEN,        "Moneda100",
            ALARMA_MONEDA_DOSCIENTOS,  "Moneda200",
            ALARMA_MONEDA_QUINIENTOS,  "Moneda500",
            ALARMA_SUMINISTRO,         "Vasos",
            ALARMA_MAL_FUNCIONAMIENTO, "KitReparacion"
    );

    // Cantidad a retirar de Bodega segun tipo CENTRAL
    private static final Map<Integer, Integer> CANTIDAD_POR_ALARMA = Map.of(
            ALARMA_INGREDIENTE,        100,
            ALARMA_MONEDA_CIEN,         20,
            ALARMA_MONEDA_DOSCIENTOS,   20,
            ALARMA_MONEDA_QUINIENTOS,   20,
            ALARMA_SUMINISTRO,          50,
            ALARMA_MAL_FUNCIONAMIENTO,   1
    );

    /**
     * Mapeo tipo CENTRAL → codigo LOCAL de la maquina.
     * Estos son los valores que entiende ControladorMQ.abastecer()
     * y que luego normalizaTipoAlarma() convierte de vuelta al tipo central.
     *
     * La maquina espera codigos locales (1-15), no tipos centrales (1-6).
     *   - Local 1  → normalizaTipoAlarma → central 6 (mantenimiento)
     *   - Local 2  → normalizaTipoAlarma → central 2 (moneda 100)
     *   - Local 4  → normalizaTipoAlarma → central 3 (moneda 200)
     *   - Local 6  → normalizaTipoAlarma → central 4 (moneda 500)
     *   - Local 5  → normalizaTipoAlarma → central 5 (suministro)
     *   - Local 8  → normalizaTipoAlarma → central 1 (ingrediente)
     */
    private static final Map<Integer, Integer> COD_LOCAL_POR_TIPO_CENTRAL = Map.ofEntries(
            Map.entry(ALARMA_INGREDIENTE,        8),
            Map.entry(ALARMA_MONEDA_CIEN,        2),
            Map.entry(ALARMA_MONEDA_DOSCIENTOS,  4),
            Map.entry(ALARMA_MONEDA_QUINIENTOS,  6),
            Map.entry(ALARMA_SUMINISTRO,         5),
            Map.entry(ALARMA_MAL_FUNCIONAMIENTO, 1),
            Map.entry(8, 8),
            Map.entry(9, 9),
            Map.entry(10, 10),
            Map.entry(11, 11),
            Map.entry(12, 12),
            Map.entry(13, 13),
            Map.entry(14, 14),
            Map.entry(15, 15)
    );

    private final Communicator            communicator;
    private final ServicioComLogisticaPrx centralPrx;
    private final ServicioBodegaPrx       bodegaPrx;

    private int idOperador = 0;

    public ControlLogistica(Communicator communicator) {
        this.communicator = communicator;

        centralPrx = ServicioComLogisticaPrx.checkedCast(
                communicator.propertyToProxy("ServicioCentral.Proxy"));
        if (centralPrx == null) {
            throw new RuntimeException(
                    "No se pudo conectar con ServidorCentral. " +
                            "Verifique 'ServicioCentral.Proxy' en CmLogistic.cfg.");
        }

        bodegaPrx = ServicioBodegaPrx.checkedCast(
                communicator.propertyToProxy("ServicioBodega.Proxy"));
        if (bodegaPrx == null) {
            throw new RuntimeException(
                    "No se pudo conectar con BodegaCentral. " +
                            "Verifique 'ServicioBodega.Proxy' en CmLogistic.cfg.");
        }
    }

    // ---------------------------------------------------------------
    // Autenticacion
    // ---------------------------------------------------------------

    public boolean login(int id, String password) {
        boolean ok = centralPrx.inicioSesion(id, password);
        if (ok) {
            this.idOperador = id;
            System.out.println("[Logistica] Tecnico " + id + " autenticado.");
        } else {
            System.err.println("[Logistica] Credenciales invalidas para tecnico " + id);
        }
        return ok;
    }

    public int getIdOperador() { return idOperador; }

    /** Expone el Communicator para que la GUI pueda leer propiedades del .cfg. */
    public Communicator getCommunicator() { return communicator; }

    // ---------------------------------------------------------------
    // Consulta de tareas
    // ---------------------------------------------------------------

    /** Retorna todas las maquinas asignadas al tecnico. */
    public List<String> getMaquinasAsignadas() {
        return centralPrx.asignacionMaquina(idOperador);
    }

    /** Retorna las maquinas asignadas que tienen alarmas activas en la BD. */
    public List<String> getMaquinasConAlarmas() {
        return centralPrx.asignacionMaquinasDesabastecidas(idOperador);
    }

    // ---------------------------------------------------------------
    // Consulta de inventario en Bodega
    // ---------------------------------------------------------------

    public Map<String, Integer> getInventarioBodega() {
        Map<String, Integer> total = new HashMap<>();
        total.putAll(bodegaPrx.consultarIngredientes());
        total.putAll(bodegaPrx.consultarMonedas());
        total.putAll(bodegaPrx.consultarSuministros());
        return total;
    }

    // ---------------------------------------------------------------
    // Resolucion de alarmas — con IP/puerto manual
    // ---------------------------------------------------------------

    /**
     * Resuelve una alarma usando IP y puerto ingresados manualmente.
     *
     * @param idMaquina   ID de la maquina (columna 0 de la tabla de alarmas).
     * @param tipoCentral Tipo de alarma CENTRAL (1-6, como aparece en la BD).
     * @param ipMaquina   IP donde corre la CoffeeMach.
     * @param puertoMaq   Puerto de la CoffeeMach.
     */
    public String resolverAlarma(int idMaquina, int tipoCentral,
                                 String ipMaquina, int puertoMaq) {
        String item       = itemPorAlarma(tipoCentral, null);
        int    cantidad   = 0;
        int    codLocal   = COD_LOCAL_POR_TIPO_CENTRAL.getOrDefault(tipoCentral, tipoCentral);
        Map<String, String> ctx = buildTraceContext(idMaquina, tipoCentral);
        String traceId = ctx.get(CTX_TRACE_ID);

        try {
            // La bodega se descuenta despues de consultar la cantidad exacta a la maquina.
            String proxyStr = "abastecer:tcp -h " + ipMaquina + " -p " + puertoMaq;
            ServicioAbastecimientoPrx maqPrx = ServicioAbastecimientoPrx.checkedCast(
                    communicator.stringToProxy(proxyStr));

            if (maqPrx == null) {
                return "ERROR: No se pudo conectar con la maquina " + idMaquina
                        + " en " + ipMaquina + ":" + puertoMaq;
            }

            cantidad = cantidadParaResolver(maqPrx, idMaquina, codLocal, tipoCentral, ctx);
            String errorStock = validarStock(item, cantidad);
            if (errorStock != null) {
                return "ERROR [" + traceId + "]: " + errorStock;
            }

            retirarDeBodega(tipoCentral, item, cantidad, ctx);
            System.out.println("[Logistica][" + traceId + "] Bodega: retirado "
                    + cantidad + " de '" + item + "'.");

            maqPrx.abastecerCantidad(idMaquina, codLocal, cantidad, ctx);
            return "OK — [" + traceId + "] Alarma central " + tipoCentral
                    + " (cod local " + codLocal + ") resuelta en maquina " + idMaquina
                    + ". Bodega: -" + cantidad + " " + item;

        } catch (Exception ex) {
            String msg = "ERROR [" + traceId + "] al resolver alarma en maquina "
                    + idMaquina + ": " + ex.getMessage();
            System.err.println("[Logistica] " + msg);
            return msg;
        }
    }

    // ---------------------------------------------------------------
    // Resolucion de alarmas — usando proxy del .cfg (RECOMENDADO)
    // ---------------------------------------------------------------

    /**
     * Resuelve usando MaquinaCafe.Proxy del CmLogistic.cfg.
     * Metodo principal para uso en laboratorio.
     *
     * @param idMaquina   ID de la maquina (columna 0 de la tabla de alarmas).
     * @param tipoCentral Tipo de alarma CENTRAL (1-6, como aparece en la BD).
     */
    public String resolverAlarmaCfg(int idMaquina, int tipoCentral) {
        String item     = itemPorAlarma(tipoCentral, null);
        int    cantidad = 0;
        int    codLocal = COD_LOCAL_POR_TIPO_CENTRAL.getOrDefault(tipoCentral, tipoCentral);
        Map<String, String> ctx = buildTraceContext(idMaquina, tipoCentral);
        String traceId = ctx.get(CTX_TRACE_ID);

        try {
            ServicioAbastecimientoPrx maqPrx = ServicioAbastecimientoPrx.checkedCast(
                    communicator.propertyToProxy("MaquinaCafe.Proxy"));

            if (maqPrx == null) {
                return "ERROR: 'MaquinaCafe.Proxy' no configurado en CmLogistic.cfg";
            }

            cantidad = cantidadParaResolver(maqPrx, idMaquina, codLocal, tipoCentral, ctx);
            String errorStock = validarStock(item, cantidad);
            if (errorStock != null) {
                return "ERROR [" + traceId + "]: " + errorStock;
            }

            retirarDeBodega(tipoCentral, item, cantidad, ctx);
            System.out.println("[Logistica][" + traceId + "] Bodega: retirado "
                    + cantidad + " de '" + item + "'.");

            maqPrx.abastecerCantidad(idMaquina, codLocal, cantidad, ctx);
            return "OK — [" + traceId + "] Alarma central " + tipoCentral
                    + " (cod local " + codLocal + ") resuelta en maquina " + idMaquina
                    + ". Bodega: -" + cantidad + " " + item;

        } catch (Exception ex) {
            String msg = "ERROR [" + traceId + "]: " + ex.getMessage();
            System.err.println("[Logistica] " + msg);
            return msg;
        }
    }

    // ---------------------------------------------------------------
    // Helpers
    // ---------------------------------------------------------------

    private void retirarDeBodega(int tipoCentral, String item, int cantidad, Map<String, String> ctx) {
        if (tipoCentral == ALARMA_MAL_FUNCIONAMIENTO) {
            bodegaPrx.entregaKitReparacion(idOperador, ctx);
        } else {
            bodegaPrx.retirarExistencias(item, cantidad, ctx);
        }
    }

    private int cantidadParaResolver(ServicioAbastecimientoPrx maqPrx, int idMaquina,
                                     int codLocal, int tipoAlarma, Map<String, String> ctx) {
        int cantidad = maqPrx.cantidadNecesaria(idMaquina, codLocal, ctx);
        if (cantidad > 0) {
            return cantidad;
        }
        return CANTIDAD_POR_ALARMA.getOrDefault(tipoAlarma, 0);
    }

    private String validarStock(String item, int cantidad) {
        int disponible = getInventarioBodega().getOrDefault(item, 0);
        if (disponible < cantidad) {
            return "Bodega no tiene suficiente '" + item + "'. Disponible: "
                    + disponible + ", requerido: " + cantidad + ".";
        }
        return null;
    }

    private String itemPorAlarma(int tipoAlarma, String descripcion) {
        if (tipoAlarma == ALARMA_INGREDIENTE || esAlarmaIngredienteEspecifica(tipoAlarma)) {
            String item = ingredienteDesdeDescripcion(descripcion);
            if (item != null) {
                return item;
            }
            switch (tipoAlarma) {
                case 8:
                case 12:
                    return "Agua";
                case 9:
                case 13:
                    return "Cafe";
                case 10:
                case 14:
                    return "Azucar";
                case 11:
                case 15:
                    return "Vasos";
                default:
                    return "Cafe";
            }
        }
        return ITEM_POR_ALARMA.getOrDefault(tipoAlarma, "Desconocido");
    }

    private boolean esAlarmaIngredienteEspecifica(int tipoAlarma) {
        return tipoAlarma >= 8 && tipoAlarma <= 15;
    }

    private String ingredienteDesdeDescripcion(String descripcion) {
        if (descripcion == null) {
            return null;
        }
        String normalizada = descripcion.trim();
        int idx = normalizada.toLowerCase().lastIndexOf(" de ");
        if (idx >= 0 && idx + 4 < normalizada.length()) {
            normalizada = normalizada.substring(idx + 4).trim();
        }
        if (normalizada.isEmpty()) {
            return null;
        }
        if (normalizada.equalsIgnoreCase("café")) {
            return "Cafe";
        }
        if (normalizada.equalsIgnoreCase("azúcar")) {
            return "Azucar";
        }
        if (normalizada.equalsIgnoreCase("vaso")) {
            return "Vasos";
        }
        return normalizada.substring(0, 1).toUpperCase() + normalizada.substring(1);
    }

    private Map<String, String> buildTraceContext(int idMaquina, int tipoAlarma) {
        Map<String, String> ctx = new HashMap<>();
        ctx.put(CTX_TRACE_ID, UUID.randomUUID().toString());
        ctx.put(CTX_OPERADOR,  String.valueOf(idOperador));
        ctx.put(CTX_MAQUINA,   String.valueOf(idMaquina));
        ctx.put(CTX_ALARMA,    String.valueOf(tipoAlarma));
        return ctx;
    }
}
