package alarma;

import java.util.Date;
import java.util.Map;

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

    private String traceIdFrom(Current current) {
        if (current == null || current.ctx == null) {
            return "sin-trace";
        }
        return current.ctx.getOrDefault("traceId", "sin-trace");
    }

    private int mapearTipoAlarmaAbastecimiento(String idInsumo, int cantidad) {
        if (idInsumo != null) {
            String normalizado = idInsumo.trim();
            try {
                int valor = Integer.parseInt(normalizado);
                if (valor >= ALARMA_INGREDIENTE && valor <= ALARMA_MAL_FUNCIONAMIENTO) {
                    return valor;
                }

                // Compatibilidad con codigos locales historicos de CoffeeMach
                if (valor == 2 || valor == 3) {
                    return ALARMA_MONEDA_CIEN;
                }
                if (valor == 4 || valor == 5) {
                    return ALARMA_MONEDA_DOS;
                }
                if (valor == 6 || valor == 7) {
                    return ALARMA_MONEDA_QUI;
                }
                if (valor == 8 || valor == 9 || valor == 10 || valor == 11 ||
                    valor == 12 || valor == 13 || valor == 14 || valor == 15) {
                    return ALARMA_INGREDIENTE;
                }
                if (valor == 1) {
                    return ALARMA_MAL_FUNCIONAMIENTO;
                }
            } catch (NumberFormatException ignore) {
                String lower = normalizado.toLowerCase();
                if (lower.contains("moneda100") || lower.contains("100")) {
                    return ALARMA_MONEDA_CIEN;
                }
                if (lower.contains("moneda200") || lower.contains("200")) {
                    return ALARMA_MONEDA_DOS;
                }
                if (lower.contains("moneda500") || lower.contains("500")) {
                    return ALARMA_MONEDA_QUI;
                }
                if (lower.contains("sumin") || lower.contains("vaso")) {
                    return ALARMA_SUMINISTRO;
                }
                if (lower.contains("repar") || lower.contains("funcion")) {
                    return ALARMA_MAL_FUNCIONAMIENTO;
                }
            }
        }

        // Fallback por cantidad reportada
        if (cantidad == 20) {
            return ALARMA_MONEDA_CIEN;
        }
        if (cantidad == 50) {
            return ALARMA_SUMINISTRO;
        }
        if (cantidad == 1) {
            return ALARMA_MAL_FUNCIONAMIENTO;
        }
        return ALARMA_INGREDIENTE;
    }

    @Override
    public void recibirNotificacionEscasezIngredientes(String iDing, int idMaq, Current current) {
        System.out.println("[SCA][" + traceIdFrom(current) + "] Escasez ingredientes maq=" + idMaq + " insumo=" + iDing);
        manager.alarmaMaquina(ALARMA_INGREDIENTE, idMaq, new Date());
    }

    @Override
    public void recibirNotificacionInsuficienciaMoneda(Moneda moneda, int idMaq, Current current) {
        System.out.println("[SCA][" + traceIdFrom(current) + "] Insuficiencia moneda maq=" + idMaq + " moneda=" + moneda);
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
        System.out.println("[SCA][" + traceIdFrom(current) + "] Escasez suministro maq=" + idMaq + " item=" + idSumin);
        manager.alarmaMaquina(ALARMA_SUMINISTRO, idMaq, new Date());
    }

    @Override
    public void recibirNotificacionAbastesimiento(int idMaq, String idInsumo, int cantidad, Current current) {
        int tipoAlarma = mapearTipoAlarmaAbastecimiento(idInsumo, cantidad);
        System.out.println("[SCA][" + traceIdFrom(current) + "] Cierre alarma maq=" + idMaq
                + " tipo=" + tipoAlarma + " idInsumo=" + idInsumo + " cantidad=" + cantidad);
        manager.desactivarAlarma(tipoAlarma, idMaq, new Date());
    }

    @Override
    public void recibirNotificacionMalFuncionamiento(int idMaq, String descri, Current current) {
        System.out.println("[SCA][" + traceIdFrom(current) + "] Mal funcionamiento maq=" + idMaq + " desc=" + descri);
        manager.alarmaMaquina(ALARMA_MAL_FUNCIONAMIENTO, idMaq, new Date());
    }

}
