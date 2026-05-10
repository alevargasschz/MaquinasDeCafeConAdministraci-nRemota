package bodega;

import com.zeroc.Ice.Current;
import servicios.ServicioBodega;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * ServicioBodegaI
 *
 * Implementa Tanto la interfaz remota Ice (ServicioBodega) COMO la
 * interfaz local (Bodega). De este modo el mismo objeto sirve como
 * servant ICE que recibe llamadas de CmLogistics, y como modelo de
 * datos para la GUI Swing que corre localmente en BodegaCentral.
 *
 * El inventario se mantiene en un ConcurrentHashMap para ser seguro
 * ante accesos concurrentes de multiples tecnicos.
 */
public class ServicioBodegaImp implements ServicioBodega, Bodega {

    // -------------------------------------------------------------------
    // Inventario interno (nombre -> cantidad)
    // -------------------------------------------------------------------
    private final Map<String, Integer> inventario = new ConcurrentHashMap<>();

    public ServicioBodegaImp() {
        // Stock inicial para pruebas
        inventario.put("Cafe",        1000);
        inventario.put("Azucar",      2000);
        inventario.put("Leche",       1500);
        inventario.put("Vasos",        500);
        inventario.put("Moneda100",     50);
        inventario.put("Moneda200",     50);
        inventario.put("Moneda500",     50);
        inventario.put("KitReparacion",  5);
    }

    // Implementacion de la interfaz REMOTA ServicioBodega (ZeroC Ice)
    @Override
    public Map<String, Integer> consultarMonedas(Current current) {
        Map<String, Integer> result = new ConcurrentHashMap<>();
        inventario.forEach((k, v) -> {
            if (k.startsWith("Moneda")) result.put(k, v);
        });
        return result;
    }

    @Override
    public Map<String, Integer> consultarIngredientes(Current current) {
        Map<String, Integer> result = new ConcurrentHashMap<>();
        for (String ing : new String[]{"Cafe", "Azucar", "Leche"}) {
            result.put(ing, inventario.getOrDefault(ing, 0));
        }
        return result;
    }

    @Override
    public Map<String, Integer> consultarSuministros(Current current) {
        Map<String, Integer> result = new ConcurrentHashMap<>();
        result.put("Vasos",         inventario.getOrDefault("Vasos", 0));
        result.put("KitReparacion", inventario.getOrDefault("KitReparacion", 0));
        return result;
    }

    @Override
    public void retirarExistencias(String idItem, int cantidad, Current current) {
        if (!inventario.containsKey(idItem)) {
            System.err.println("ADVERTENCIA: item desconocido: " + idItem);
            return;
        }
        inventario.computeIfPresent(idItem, (k, v) -> Math.max(0, v - cantidad));
        System.out.println("[Bodega] Retirados " + cantidad + " unidades de '" + idItem + "'"
                + " | Nuevo stock: " + inventario.get(idItem));
    }

    @Override
    public void abastecerExistencia(String idItem, int cantidad, Current current) {
        inventario.merge(idItem, cantidad, Integer::sum);
        System.out.println("[Bodega] Abastecidos " + cantidad + " unidades de '" + idItem + "'"
                + " | Nuevo stock: " + inventario.get(idItem));
    }

    @Override
    public void entregaKitReparacion(int idTecnico, Current current) {
        int stock = inventario.getOrDefault("KitReparacion", 0);
        if (stock <= 0) {
            System.err.println("[Bodega] Sin kits de reparacion disponibles para tecnico " + idTecnico);
            return;
        }
        retirarExistencias("KitReparacion", 1, current);
        System.out.println("[Bodega] Kit de reparacion entregado al tecnico ID: " + idTecnico);
    }

    @Override
    public void separarExistencias(String idItem, int cantidad, Current current) {
        // En el prototipo solo registramos la operacion en consola.
        // En produccion se marcaria como "reservado" en la BD.
        System.out.println("[Bodega] Existencias separadas/reservadas: "
                + idItem + " (" + cantidad + " unidades)");
    }

    // Implementacion de la interfaz LOCAL Bodega (para la GUI Swing)
    // Delegan en los metodos Ice pasando null como Current.

    @Override
    public void consultarMonedas() {
        System.out.println("[Bodega-Local] Monedas: " + consultarMonedas(null));
    }

    @Override
    public void consultarIngredientes() {
        System.out.println("[Bodega-Local] Ingredientes: " + consultarIngredientes(null));
    }

    @Override
    public void consultarSuministros() {
        System.out.println("[Bodega-Local] Suministros: " + consultarSuministros(null));
    }

    @Override
    public void entregaKitReparacion() {
        entregaKitReparacion(0, null);
    }

    @Override
    public void retirarExistencias() {
        // La GUI provee los parametros directamente; este metodo es un placeholder.
        System.out.println("[Bodega-Local] Llame a retirarExistencias(String, int) desde la GUI.");
    }

    @Override
    public void abastecerExistencia() {
        System.out.println("[Bodega-Local] Llame a abastecerExistencia(String, int) desde la GUI.");
    }

    @Override
    public void separarExistencias() {
        System.out.println("[Bodega-Local] Llame a separarExistencias(String, int) desde la GUI.");
    }

    // Metodo de conveniencia para la GUI: retorna todo el inventario
    public Map<String, Integer> getInventarioCompleto() {
        return new ConcurrentHashMap<>(inventario);
    }

    /**
     * Variante sin Current para uso directo desde la GUI.
     */
    public void retirarExistencias(String idItem, int cantidad) {
        retirarExistencias(idItem, cantidad, null);
    }

    /**
     * Variante sin Current para uso directo desde la GUI.
     */
    public void abastecerExistencia(String idItem, int cantidad) {
        abastecerExistencia(idItem, cantidad, null);
    }
}
