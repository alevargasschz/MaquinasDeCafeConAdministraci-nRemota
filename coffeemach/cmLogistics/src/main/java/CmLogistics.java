import com.zeroc.Ice.Communicator;
import com.zeroc.Ice.Util;

import controlAlarma.ControlLogistica;
import gui.InterfazLogistica;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import java.util.ArrayList;
import java.util.List;

/**
 * CmLogistics — punto de entrada del componente Logistica.
 *
 * Secuencia de arranque:
 *   1. Inicializa el Communicator Ice leyendo CmLogistic.cfg.
 *   2. Crea ControlLogistica, que establece proxies con
 *      ServidorCentral y BodegaCentral.
 *   3. Muestra un dialogo de login.
 *   4. Si las credenciales son validas, lanza la GUI Swing.
 *   5. Bloquea en waitForShutdown() hasta que el tecnico cierre la app.
 *
 * Prerequisitos (orden de despliegue):
 *   1. Base de datos PostgreSQL corriendo.
 *   2. ServidorCentral corriendo (proporciona ServicioComLogistica).
 *   3. BodegaCentral corriendo  (proporciona ServicioBodega).
 *   4. Al menos una CoffeeMach corriendo (para resolver alarmas).
 *   5. Este proceso (CmLogistics).
 */
public class CmLogistics {

    public static void main(String[] args) {
        List<String> extArgs = new ArrayList<>();
        try (Communicator communicator = Util.initialize(args, "CmLogistic.cfg", extArgs)) {

            // Construir el controlador (conecta proxies Ice)
            ControlLogistica control;
            try {
                control = new ControlLogistica(communicator);
            } catch (RuntimeException ex) {
                JOptionPane.showMessageDialog(null,
                        "No se pudo conectar con los servicios remotos:\n" + ex.getMessage(),
                        "Error de conexion", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // --- Flujo de login ---
            boolean autenticado = false;
            int intentos = 0;

            while (!autenticado && intentos < 3) {
                String idStr = JOptionPane.showInputDialog(null,
                        "Ingrese su ID de operador:", "Login — Logistica",
                        JOptionPane.QUESTION_MESSAGE);

                if (idStr == null) {
                    // El tecnico cancelo el dialogo
                    System.out.println("[CmLogistics] Login cancelado por el usuario.");
                    return;
                }

                String password = JOptionPane.showInputDialog(null,
                        "Ingrese su contrasena:", "Login — Logistica",
                        JOptionPane.QUESTION_MESSAGE);

                if (password == null) {
                    System.out.println("[CmLogistics] Login cancelado por el usuario.");
                    return;
                }

                try {
                    int idOperador = Integer.parseInt(idStr.trim());
                    autenticado = control.login(idOperador, password.trim());
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(null,
                            "El ID de operador debe ser un numero entero.",
                            "Error", JOptionPane.ERROR_MESSAGE);
                }

                if (!autenticado) {
                    intentos++;
                    if (intentos < 3) {
                        JOptionPane.showMessageDialog(null,
                                "Credenciales invalidas. Intento " + intentos + "/3.",
                                "Acceso denegado", JOptionPane.WARNING_MESSAGE);
                    }
                }
            }

            if (!autenticado) {
                JOptionPane.showMessageDialog(null,
                        "Demasiados intentos fallidos. Cerrando.",
                        "Acceso denegado", JOptionPane.ERROR_MESSAGE);
                return;
            }

            System.out.println("[CmLogistics] Sesion iniciada para operador #"
                    + control.getIdOperador());

            // Lanzar la GUI en el Event Dispatch Thread
            final ControlLogistica controlFinal = control;
            SwingUtilities.invokeLater(() -> new InterfazLogistica(controlFinal));

            // Mantener el Communicator activo mientras la GUI este abierta
            communicator.waitForShutdown();

        } catch (Exception e) {
            System.err.println("[CmLogistics] Error critico:");
            e.printStackTrace();
            System.exit(1);
        }
    }
}
