package alarma;

import java.util.Date;

import com.zeroc.Ice.Current;

import servicios.AlarmaService;
import servicios.Moneda;

public class Alarma implements AlarmaService {

    public static final int ALARMA_INGREDIENTE = 1;
    public static final int ALARMA_MONEDA_CIEN = 2;
    public static final int ALARMA_MONEDA_DOS = 3;
    public static final int ALARMA_MONEDA_QUI = 4;
    public static final int ALARMA_SUMINISTRO = 5;
    public static final int ALARMA_MAL_FUNCIONAMIENTO = 6;

    private AlarmasManager manager;

    public Alarma(AlarmasManager manager) {
        this.manager = manager;
    }

    @Override
    public void recibirNotificacionEscasezIngredientes(String iDing, int idMaq, Current current) {
        manager.alarmaMaquina(ALARMA_INGREDIENTE, idMaq, new Date());
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
        // suministro
        manager.alarmaMaquina(ALARMA_SUMINISTRO, idMaq, new Date());
    }

    @Override
    public void recibirNotificacionAbastesimiento(int idMaq, String idInsumo, int cantidad, Current current) {
        int tipoAlarma = mapearTipoAlarmaAbastecimiento(idInsumo);
        manager.desactivarAlarma(tipoAlarma, idMaq, new Date());
    }

    @Override
    public void recibirNotificacionMalFuncionamiento(int idMaq, String descri, Current current) {
        manager.alarmaMaquina(ALARMA_MAL_FUNCIONAMIENTO, idMaq, new Date());
    }

    private int mapearTipoAlarmaAbastecimiento(String idInsumo) {
        if (idInsumo == null || idInsumo.trim().isEmpty()) {
            return ALARMA_INGREDIENTE;
        }

        int id;
        try {
            id = Integer.parseInt(idInsumo.trim());
        } catch (NumberFormatException e) {
            return ALARMA_INGREDIENTE;
        }

        if (id == 1 || id == 8 || id == 9 || id == 10 || id == 11 || id == 12 || id == 13 || id == 14 || id == 15) {
            return ALARMA_INGREDIENTE;
        }
        if (id == 2 || id == 3) {
            return ALARMA_MONEDA_CIEN;
        }
        if (id == 4 || id == 5) {
            return ALARMA_MONEDA_DOS;
        }
        if (id == 6) {
            return ALARMA_MAL_FUNCIONAMIENTO;
        }
        if (id == 7) {
            return ALARMA_MONEDA_QUI;
        }

        return ALARMA_INGREDIENTE;
    }

}
