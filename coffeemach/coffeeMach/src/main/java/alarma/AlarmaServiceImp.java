package alarma;

import servicios.AlarmaServicePrx;
import servicios.Moneda;
import java.io.*;
import com.zeroc.Ice.LocalException;

public class AlarmaServiceImp implements AlarmaService {

    private AlarmaServicePrx alarmaServicePrx;

    public void setAlarmaService(AlarmaServicePrx a) {
        alarmaServicePrx = a;
    }

    private int leerCodMaquina() {
        int retorno = -1;
        try (BufferedReader br = new BufferedReader(new FileReader("codMaquina.cafe"))) {
            String line = br.readLine();
            if (line != null) retorno = Integer.parseInt(line.trim());
        } catch (Exception e) {
            System.err.println("No se pudo leer codMaquina.cafe: " + e.getMessage());
        }
        return retorno;
    }

    @Override
    public void notificarAbastecimiento() {
        try {
            int idMaq = leerCodMaquina();
            alarmaServicePrx.recibirNotificacionAbastesimiento(idMaq, "", 0);
        } catch (LocalException e) {
            System.err.println("Error notificando abastecimiento: " + e.getMessage());
        }
    }

    @Override
    public void notificarReparacion() {
        try {
            int idMaq = leerCodMaquina();
            alarmaServicePrx.recibirNotificacionMalFuncionamiento(idMaq, "Solicitud de reparacion");
        } catch (LocalException e) {
            System.err.println("Error notificando reparacion: " + e.getMessage());
        }
    }

    @Override
    public void notificarEscasezSuministros() {
        try {
            int idMaq = leerCodMaquina();
            alarmaServicePrx.recibirNotificacionEscasezSuministro("Desconocido", idMaq);
        } catch (LocalException e) {
            System.err.println("Error notificando escasez suministros: " + e.getMessage());
        }
    }

    @Override
    public void notificarError() {
        try {
            int idMaq = leerCodMaquina();
            alarmaServicePrx.recibirNotificacionMalFuncionamiento(idMaq, "Error en maquina");
        } catch (LocalException e) {
            System.err.println("Error notificando error: " + e.getMessage());
        }
    }

    @Override
    public void notificarAusenciaMoneda() {
        try {
            int idMaq = leerCodMaquina();
            alarmaServicePrx.recibirNotificacionInsuficienciaMoneda(Moneda.CIEN, idMaq);
        } catch (LocalException e) {
            System.err.println("Error notificando ausencia moneda: " + e.getMessage());
        }
    }

    @Override
    public void notificarEscazesIngredientes() {
        try {
            int idMaq = leerCodMaquina();
            alarmaServicePrx.recibirNotificacionEscasezIngredientes("Desconocido", idMaq);
        } catch (LocalException e) {
            System.err.println("Error notificando escasez ingredientes: " + e.getMessage());
        }
    }

    @Override
    public void notificarMalFuncionamiento() {
        try {
            int idMaq = leerCodMaquina();
            alarmaServicePrx.recibirNotificacionMalFuncionamiento(idMaq, "Mal funcionamiento");
        } catch (LocalException e) {
            System.err.println("Error notificando mal funcionamiento: " + e.getMessage());
        }
    }

}
