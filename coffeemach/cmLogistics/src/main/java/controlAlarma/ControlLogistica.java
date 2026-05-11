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
 * ControlLogistica
 *
 * Orquesta el ciclo de vida de una sesion de tecnico:
 *   1. Autenticacion contra ServidorCentral.
 *   2. Consulta de maquinas asignadas con alarmas activas.
 *   3. Resolucion de alarmas:
 *      a) Retira suministros de BodegaCentral.
 *      b) Llama a abastecer() en la CoffeeMach correspondiente.
 *
 * Los proxies se construyen a partir de las propiedades definidas
 * en CmLogistic.cfg, que el Communicator ya tiene cargadas.
 */
public class ControlLogistica {

    private static final String CTX_TRACE_ID = "traceId";
    private static final String CTX_OPERADOR = "operadorId";
    private static final String CTX_MAQUINA  = "maquinaId";
    private static final String CTX_ALARMA   = "tipoAlarma";

    // Tipos de alarma (deben coincidir con Alarma.java en ServidorCentral)
    public static final int ALARMA_INGREDIENTE         = 1;
    public static final int ALARMA_MONEDA_CIEN         = 2;
    public static final int ALARMA_MONEDA_DOSCIENTOS   = 3;
    public static final int ALARMA_MONEDA_QUINIENTOS   = 4;
    public static final int ALARMA_SUMINISTRO          = 5;
    public static final int ALARMA_MAL_FUNCIONAMIENTO  = 6;

    // Nombres de items en Bodega por tipo de alarma
    private static final Map<Integer, String> ITEM_POR_ALARMA = Map.of(
            ALARMA_INGREDIENTE,       "Cafe",
            ALARMA_MONEDA_CIEN,       "Moneda100",
            ALARMA_MONEDA_DOSCIENTOS, "Moneda200",
            ALARMA_MONEDA_QUINIENTOS, "Moneda500",
            ALARMA_SUMINISTRO,        "Vasos",
            ALARMA_MAL_FUNCIONAMIENTO,"KitReparacion"
    );

    // Cantidad a retirar de Bodega por defecto al resolver cada tipo
    private static final Map<Integer, Integer> CANTIDAD_POR_ALARMA = Map.of(
            ALARMA_INGREDIENTE,       100,
            ALARMA_MONEDA_CIEN,        20,
            ALARMA_MONEDA_DOSCIENTOS,  20,
            ALARMA_MONEDA_QUINIENTOS,  20,
            ALARMA_SUMINISTRO,         50,
            ALARMA_MAL_FUNCIONAMIENTO,  1
    );

    // ---------------------------------------------------------------
    // Proxies Ice
    // ---------------------------------------------------------------
    private final Communicator              communicator;
    private final ServicioComLogisticaPrx   centralPrx;
    private final ServicioBodegaPrx         bodegaPrx;

    /** ID del operador autenticado (0 = no autenticado). */
    private int idOperador = 0;

    public ControlLogistica(Communicator communicator) {
        this.communicator = communicator;

        // Proxy al ServidorCentral (ServicioComLogistica)
        centralPrx = ServicioComLogisticaPrx.checkedCast(
                communicator.propertyToProxy("ServicioCentral.Proxy"));

        if (centralPrx == null) {
            throw new RuntimeException(
                    "No se pudo conectar con ServidorCentral. " +
                            "Verifique 'ServicioCentral.Proxy' en CmLogistic.cfg.");
        }

        // Proxy a BodegaCentral (ServicioBodega)
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

    /**
     * Inicia sesion del tecnico en el ServidorCentral.
     * @return true si las credenciales son validas.
     */
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

    // ---------------------------------------------------------------
    // Consulta de tareas
    // ---------------------------------------------------------------

    /**
     * Retorna todas las maquinas asignadas al tecnico (con y sin alarmas).
     * Formato de cada string: "idMaq-ubicacion"
     */
    public List<String> getMaquinasAsignadas() {
        return centralPrx.asignacionMaquina(idOperador);
    }

    /**
     * Retorna las maquinas asignadas que tienen alarmas activas.
     * Formato de cada string: "idMaq-ubicacion"
     */
    public List<String> getMaquinasConAlarmas() {
        return centralPrx.asignacionMaquinasDesabastecidas(idOperador);
    }

    // ---------------------------------------------------------------
    // Consulta de inventario en Bodega
    // ---------------------------------------------------------------

    public Map<String, Integer> getInventarioBodega() {
        // Combina ingredientes + monedas + suministros
        Map<String, Integer> total = new java.util.HashMap<>();
        total.putAll(bodegaPrx.consultarIngredientes());
        total.putAll(bodegaPrx.consultarMonedas());
        total.putAll(bodegaPrx.consultarSuministros());
        return total;
    }

    // ---------------------------------------------------------------
    // Resolucion de alarmas
    // ---------------------------------------------------------------

    /**
     * Resuelve una alarma en la maquina indicada.
     *
     * Flujo:
     *   1. Determina el item necesario segun el tipo de alarma.
     *   2. Retira la cantidad del item de BodegaCentral.
     *   3. Si es mal funcionamiento entrega un kit al tecnico.
     *   4. Llama a abastecer() en la CoffeeMach correspondiente.
     *
     * @param idMaquina  Codigo de la maquina con alarma.
     * @param tipoAlarma Tipo de alarma (constantes ALARMA_*).
     * @param ipMaquina  IP donde corre la CoffeeMach (para construir el proxy).
     * @param puertoMaq  Puerto de la CoffeeMach.
     * @return Mensaje descriptivo del resultado.
     */
//    public String resolverAlarma(int idMaquina, int tipoAlarma,
//                                 String ipMaquina, int puertoMaq) {
//
//        String item     = ITEM_POR_ALARMA.getOrDefault(tipoAlarma, "Desconocido");
//        int    cantidad = CANTIDAD_POR_ALARMA.getOrDefault(tipoAlarma, 0);
//        Map<String, String> traceCtx = buildTraceContext(idMaquina, tipoAlarma);
//        String traceId = traceCtx.get(CTX_TRACE_ID);
//
//        try {
//            // Paso 1 — Retirar de la Bodega
//            if (tipoAlarma == ALARMA_MAL_FUNCIONAMIENTO) {
//                bodegaPrx.entregaKitReparacion(idOperador, traceCtx);
//            } else {
//                bodegaPrx.retirarExistencias(item, cantidad, traceCtx);
//            }
//            System.out.println("[Logistica][" + traceId + "] Bodega: retirado " + cantidad
//                    + " de '" + item + "'.");
//
//            // Paso 2 — Llamar a abastecer() en la maquina
//            String proxyStr = "abastecer:tcp -h " + ipMaquina + " -p " + puertoMaq;
//            ServicioAbastecimientoPrx maqPrx = ServicioAbastecimientoPrx.checkedCast(
//                    communicator.stringToProxy(proxyStr));
//
//            if (maqPrx == null) {
//                return "ERROR: No se pudo conectar con la maquina " + idMaquina
//                        + " en " + ipMaquina + ":" + puertoMaq;
//            }
//
//                maqPrx.abastecer(idMaquina, tipoAlarma, traceCtx);
//                System.out.println("[Logistica][" + traceId + "] Maquina " + idMaquina
//                    + " abastecida (alarma tipo " + tipoAlarma + ").");
//
//                return "OK — [" + traceId + "] Alarma tipo " + tipoAlarma + " resuelta en maquina "
//                    + idMaquina + ". Item: " + item + " (" + cantidad + " uds.)";
//
//        } catch (Exception ex) {
//            String msg = "ERROR [" + traceId + "] al resolver alarma en maquina " + idMaquina
//                    + ": " + ex.getMessage();
//            System.err.println("[Logistica] " + msg);
//            return msg;
//        }
//    }
    public String resolverAlarma(int idMaquina, int idAlarma,
                                 String ipMaquina, int puertoMaq) {

        // Normalizar SIEMPRE el idAlarma (local 1-15) al tipo central (1-6)
        int tipoCentral = normalizarTipo(idAlarma);

        String item     = ITEM_POR_ALARMA.getOrDefault(tipoCentral, "Desconocido");
        int    cantidad = CANTIDAD_POR_ALARMA.getOrDefault(tipoCentral, 0);
        Map<String, String> traceCtx = buildTraceContext(idMaquina, tipoCentral);
        String traceId = traceCtx.get(CTX_TRACE_ID);

        try {
            // Paso 1 — Bodega (usa tipoCentral para saber qué ítem retirar)
            if (tipoCentral == ALARMA_MAL_FUNCIONAMIENTO) {
                bodegaPrx.entregaKitReparacion(idOperador, traceCtx);
            } else {
                bodegaPrx.retirarExistencias(item, cantidad, traceCtx);
            }

            // Paso 2 — Máquina (le mandamos el idAlarma ORIGINAL para que ella normalice)
            String proxyStr = "abastecer:tcp -h " + ipMaquina + " -p " + puertoMaq;
            ServicioAbastecimientoPrx maqPrx = ServicioAbastecimientoPrx.checkedCast(
                    communicator.stringToProxy(proxyStr));

            if (maqPrx == null) {
                return "ERROR: No se pudo conectar con la maquina " + idMaquina
                        + " en " + ipMaquina + ":" + puertoMaq;
            }

            maqPrx.abastecer(idMaquina, idAlarma, traceCtx); // ← idAlarma original
            return "OK — [" + traceId + "] Alarma " + idAlarma + " (tipo " + tipoCentral
                    + ") resuelta en maquina " + idMaquina + ". Item: " + item
                    + " (" + cantidad + " uds.)";

        } catch (Exception ex) {
            String msg = "ERROR [" + traceId + "] al resolver alarma en maquina "
                    + idMaquina + ": " + ex.getMessage();
            System.err.println("[Logistica] " + msg);
            return msg;
        }
    }

    /**
     * Sobrecarga conveniente: usa el proxy de maquina configurado en
     * CmLogistic.cfg (propiedad MaquinaCafe.Proxy).
     * Util cuando todas las maquinas estan en la misma red y se
     * indica el endpoint por configuracion.
     */
//    public String resolverAlarmaCfg(int idMaquina, int tipoAlarma) {
//        String item     = ITEM_POR_ALARMA.getOrDefault(tipoAlarma, "Desconocido");
//        int    cantidad = CANTIDAD_POR_ALARMA.getOrDefault(tipoAlarma, 0);
//        Map<String, String> traceCtx = buildTraceContext(idMaquina, tipoAlarma);
//        String traceId = traceCtx.get(CTX_TRACE_ID);
//
//        try {
//            if (tipoAlarma == ALARMA_MAL_FUNCIONAMIENTO) {
//                bodegaPrx.entregaKitReparacion(idOperador, traceCtx);
//            } else {
//                bodegaPrx.retirarExistencias(item, cantidad, traceCtx);
//            }
//
//            ServicioAbastecimientoPrx maqPrx = ServicioAbastecimientoPrx.checkedCast(
//                    communicator.propertyToProxy("MaquinaCafe.Proxy"));
//
//            if (maqPrx == null) {
//                return "ERROR: Proxy MaquinaCafe.Proxy no configurado en CmLogistic.cfg";
//            }
//
//            maqPrx.abastecer(idMaquina, tipoAlarma, traceCtx);
//            return "OK — [" + traceId + "] Alarma tipo " + tipoAlarma + " resuelta en maquina "
//                    + idMaquina + ". Item: " + item + " (" + cantidad + " uds.)";
//
//        } catch (Exception ex) {
//            return "ERROR [" + traceId + "]: " + ex.getMessage();
//        }
//    }

    public String resolverAlarmaCfg(int idMaquina, int idAlarma) {
        int tipoCentral = normalizarTipo(idAlarma); // ← mismo fix
        String item     = ITEM_POR_ALARMA.getOrDefault(tipoCentral, "Desconocido");
        int    cantidad = CANTIDAD_POR_ALARMA.getOrDefault(tipoCentral, 0);
        Map<String, String> traceCtx = buildTraceContext(idMaquina, tipoCentral);
        String traceId = traceCtx.get(CTX_TRACE_ID);

        try {
            if (tipoCentral == ALARMA_MAL_FUNCIONAMIENTO) {
                bodegaPrx.entregaKitReparacion(idOperador, traceCtx);
            } else {
                bodegaPrx.retirarExistencias(item, cantidad, traceCtx);
            }

            ServicioAbastecimientoPrx maqPrx = ServicioAbastecimientoPrx.checkedCast(
                    communicator.propertyToProxy("MaquinaCafe.Proxy"));

            if (maqPrx == null) {
                return "ERROR: Proxy MaquinaCafe.Proxy no configurado en CmLogistic.cfg";
            }

            maqPrx.abastecer(idMaquina, idAlarma, traceCtx); // ← idAlarma original
            return "OK — [" + traceId + "] Alarma " + idAlarma + " (tipo " + tipoCentral
                    + ") resuelta. Item: " + item + " (" + cantidad + " uds.)";

        } catch (Exception ex) {
            return "ERROR [" + traceId + "]: " + ex.getMessage();
        }
    }

    // Metodo privado de normalización (espejo del ControladorMQ)
    private int normalizarTipo(int idAlarma) {
        switch (idAlarma) {
            case 1:  return ALARMA_MAL_FUNCIONAMIENTO;
            case 2: case 3: return ALARMA_MONEDA_CIEN;
            case 4: case 5: return ALARMA_MONEDA_DOSCIENTOS;
            case 6: case 7: return ALARMA_MONEDA_QUINIENTOS;
            case 8: case 9: case 10: case 11:
            case 12: case 13: case 14: case 15: return ALARMA_INGREDIENTE;
            default:
                // Ya viene como tipo central (1-6)
                return (idAlarma >= 1 && idAlarma <= 6) ? idAlarma : ALARMA_INGREDIENTE;
        }
    }


    private Map<String, String> buildTraceContext(int idMaquina, int tipoAlarma) {
        Map<String, String> ctx = new HashMap<>();
        ctx.put(CTX_TRACE_ID, UUID.randomUUID().toString());
        ctx.put(CTX_OPERADOR, String.valueOf(idOperador));
        ctx.put(CTX_MAQUINA, String.valueOf(idMaquina));
        ctx.put(CTX_ALARMA, String.valueOf(tipoAlarma));
        return ctx;
    }
}
