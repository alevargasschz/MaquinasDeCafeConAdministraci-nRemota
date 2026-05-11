import bodega.ServicioBodegaImp;
import com.zeroc.Ice.Communicator;
import com.zeroc.Ice.ObjectAdapter;
import com.zeroc.Ice.Util;
import guiInventario.Interfaz;

import javax.swing.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class BodegaCentral {

    public static void main(String[] args) {
        String configPath = resolveConfigPath();
        try (Communicator communicator = Util.initialize(args, configPath)) {

            System.out.println("[BodegaCentral] Usando configuracion: " + configPath);

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

    private static String resolveConfigPath() {
        Path[] candidates = new Path[] {
                Paths.get("bodega.cfg"),
                Paths.get("src", "main", "java", "resources", "bodega.cfg"),
                Paths.get("coffeemach", "bodegaCentral", "src", "main", "java", "resources", "bodega.cfg"),
                Paths.get("bodegaCentral", "src", "main", "java", "resources", "bodega.cfg")
        };

        for (Path candidate : candidates) {
            if (Files.exists(candidate)) {
                return candidate.toString();
            }
        }

        // Mantiene el comportamiento original si no encuentra una ruta alternativa.
        return "bodega.cfg";
    }
}
