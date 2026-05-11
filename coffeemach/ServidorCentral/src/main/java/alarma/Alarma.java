package alarma;

import java.util.Date;

import com.zeroc.Ice.Current;

import servicios.AlarmaService;
import servicios.Moneda;

public class Alarma implements AlarmaService {

    public static final int ALARMA_INGREDIENTE         = 1;
    public static final int ALARMA_MONEDA_CIEN         = 2;
    public static final int ALARMA_MONEDA_DOS          = 3;
    public static final int ALARMA_MONEDA_QUI          = 4;
    public static final int ALARMA_SUMINISTRO          = 5;
    public static final int ALARMA_MAL_FUNCIONAMIENTO  = 6;

    private AlarmasManager manager;

    public Alarma(AlarmasManager manager) {
        this.manager = manager;
    }

    @Override
    public void recibirNotificacionEscasezIngredientes(String iDing, int idMaq, Current current) {
        manager.alarmaMaquina(idAlarmaIngrediente(iDing), idMaq, new Date());
    }

    @Override
    public void recibirNotificacionInsuficienciaMoneda(Moneda moneda, int idMaq, Current current) {
        switch (moneda) {
            case CIEN:
                manager.alarmaMaquina(ALARMA_MONEDA_CIEN, idMaq, new Date());
                break;
            case DOCIENTOS:
                manager.alarmaMaquina(ALARMA_MONEDA_DOS, idMaq, new Date());
                break;
            case QUINIENTOS:
                manager.alarmaMaquina(ALARMA_MONEDA_QUI, idMaq, new Date());
                break;
            default:
                break;
        }
    }

    @Override
    public void recibirNotificacionEscasezSuministro(String idSumin, int idMaq, Current current) {
        manager.alarmaMaquina(ALARMA_SUMINISTRO, idMaq, new Date());
    }

    @Override
    public void recibirNotificacionAbastesimiento(int idMaq, String idInsumo,
                                                  int cantidad, Current current) {
        int tipoAlarma = ALARMA_INGREDIENTE; // valor por defecto si falla el parseo
        try {
            tipoAlarma = Integer.parseInt(idInsumo);
            if (tipoAlarma < 1 || tipoAlarma > 15) {
                System.err.println("[ServidorCentral][Alarma] tipoAlarma fuera de rango: "
                        + tipoAlarma + ". Se usara ALARMA_INGREDIENTE.");
                tipoAlarma = ALARMA_INGREDIENTE;
            }
        } catch (NumberFormatException e) {
            System.err.println("[ServidorCentral][Alarma] idInsumo no es un entero: '"
                    + idInsumo + "'. Se usara ALARMA_INGREDIENTE.");
        }
        System.out.println("[ServidorCentral][Alarma] Desactivando alarma tipo "
                + tipoAlarma + " para maquina " + idMaq);
        manager.desactivarAlarma(tipoAlarma, idMaq, new Date());
        int alarmaHermana = alarmaHermanaIngrediente(tipoAlarma);
        if (alarmaHermana != tipoAlarma) {
            manager.desactivarAlarma(alarmaHermana, idMaq, new Date());
        }
    }

    @Override
    public void recibirNotificacionMalFuncionamiento(int idMaq, String descri, Current current) {
        manager.alarmaMaquina(ALARMA_MAL_FUNCIONAMIENTO, idMaq, new Date());
    }

    private int idAlarmaIngrediente(String ingrediente) {
        if (ingrediente == null || ingrediente.trim().isEmpty()) {
            return ALARMA_INGREDIENTE;
        }

        String[] partes = ingrediente.split("#", 2);
        if (partes.length == 2) {
            try {
                return Integer.parseInt(partes[1].trim());
            } catch (NumberFormatException ignored) {
            }
        }

        String nombre = partes[0].trim().toLowerCase();
        switch (nombre) {
            case "agua":
                return 8;
            case "cafe":
            case "café":
                return 9;
            case "azucar":
            case "azúcar":
                return 10;
            case "vaso":
            case "vasos":
                return 11;
            case "leche":
                return 8;
            default:
                return ALARMA_INGREDIENTE;
        }
    }

    private int alarmaHermanaIngrediente(int idAlarma) {
        if (idAlarma >= 8 && idAlarma <= 11) {
            return idAlarma + 4;
        }
        if (idAlarma >= 12 && idAlarma <= 15) {
            return idAlarma - 4;
        }
        return idAlarma;
    }
}
