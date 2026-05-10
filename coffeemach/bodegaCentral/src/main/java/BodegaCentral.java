import bodega.ServicioBodegaImp;
import com.zeroc.Ice.Communicator;
import com.zeroc.Ice.ObjectAdapter;
import com.zeroc.Ice.Util;
import guiInventario.Interfaz;

import javax.swing.*;

public class BodegaCentral {

    public static void main(String[] args) {
        try (Communicator communicator = Util.initialize(args, "bodega.cfg")) {

            // 1. Crear el servant (logica de negocio + inventario en memoria)
            ServicioBodegaImp servant = new ServicioBodegaImp();

            // 2. Crear y configurar el ObjectAdapter
            ObjectAdapter adapter = communicator.createObjectAdapter("BodegaAdapter");
            adapter.add((com.zeroc.Ice.Object) servant, Util.stringToIdentity("BodegaService"));
            adapter.activate();

            System.out.println("==============================================");
            System.out.println(" BodegaCentral iniciado y escuchando...");
            System.out.println(" Identidad Ice: BodegaService");
            System.out.println("==============================================");

            // 3. Lanzar la GUI en el Event Dispatch Thread de Swing
            SwingUtilities.invokeLater(() -> new Interfaz(servant));

            // 4. Mantener el servidor en ejecucion
            communicator.waitForShutdown();

        } catch (Exception e) {
            System.err.println("[BodegaCentral] Error critico al iniciar:");
            e.printStackTrace();
            System.exit(1);
        }
    }
}
