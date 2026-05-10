package mantenimientoExistencias;

import bodega.ServicioBodegaImp;

/**
 * InventarioImpl
 *
 * Implementa la interfaz local.
 * Delega las operaciones de abastecimiento en el servant
 * ServicioBodegaImp, que es la fuente de verdad del stock.
 *
 * Esta clase puede ser usada tanto desde la consola administrativa
 * como desde la GUI de Bodega para reponer existencias en bloque.
 */
public class InventarioImp implements Inventario {

    // Cantidades por defecto al reabastecer (ajustar segun necesidad)
    private static final int CANTIDAD_INGREDIENTE = 500;
    private static final int CANTIDAD_SUMINISTRO  = 200;
    private static final int CANTIDAD_MONEDA      = 50;

    private final ServicioBodegaImp bodega;

    public InventarioImp(ServicioBodegaImp bodega) {
        this.bodega = bodega;
    }

    /**
     * Abastece todos los suministros (Vasos y KitReparacion) con
     * las cantidades predeterminadas.
     */
    @Override
    public void abastecerSuministros() {
        bodega.abastecerExistencia("Vasos",         CANTIDAD_SUMINISTRO);
        bodega.abastecerExistencia("KitReparacion", 5);
        System.out.println("[Inventario] Suministros reabastecidos.");
    }

    /**
     * Abastece todas las denominaciones de moneda.
     */
    @Override
    public void abastecerMonedas() {
        bodega.abastecerExistencia("Moneda100", CANTIDAD_MONEDA);
        bodega.abastecerExistencia("Moneda200", CANTIDAD_MONEDA);
        bodega.abastecerExistencia("Moneda500", CANTIDAD_MONEDA);
        System.out.println("[Inventario] Monedas reabastecidas.");
    }

    /**
     * Abastece todos los ingredientes (Cafe, Azucar, Leche).
     */
    @Override
    public void abastecerIngredientes() {
        bodega.abastecerExistencia("Cafe",   CANTIDAD_INGREDIENTE);
        bodega.abastecerExistencia("Azucar", CANTIDAD_INGREDIENTE);
        bodega.abastecerExistencia("Leche",  CANTIDAD_INGREDIENTE);
        System.out.println("[Inventario] Ingredientes reabastecidos.");
    }
}
