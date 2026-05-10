package alarma;

import java.util.Date;

import com.zeroc.Ice.Communicator;

import modelo.AlarmaMaquina;
import modelo.ConexionBD;
import modelo.ManejadorDatos;

public class AlarmasManager {

    private Communicator comunicator;

    public AlarmasManager(Communicator communicator) {
        this.comunicator = communicator;
    }

    public String alarmaMaquina(int idAlarma, int idMaquina, Date fechainicial) {
        ConexionBD cbd = new ConexionBD(comunicator);
        cbd.conectarBaseDatos();
        ManejadorDatos md = new ManejadorDatos();
        md.setConexion(cbd.getConnection());

        String alarma = md.darNombreAlarma(idAlarma);
        String operador = md.darOperador(idMaquina);

        if (alarma != null && operador != null) {
            AlarmaMaquina aM = new AlarmaMaquina(idAlarma, idMaquina,
                    fechainicial);
            md.registrarAlarma(aM);
            cbd.cerrarConexion();
            return "Fallo de máquina: " + alarma + " - Atención por:"
                    + operador;
        }
        cbd.cerrarConexion();
        return null;
    }

    public void desactivarAlarma(int idAlarma, int idMaquina, Date fechaFinal) {
        ConexionBD cbd = new ConexionBD(comunicator);
        cbd.conectarBaseDatos();
        ManejadorDatos md = new ManejadorDatos();
        md.setConexion(cbd.getConnection());
        md.desactivarAlarma(idMaquina, idAlarma, fechaFinal);
        cbd.cerrarConexion();
    }

}
