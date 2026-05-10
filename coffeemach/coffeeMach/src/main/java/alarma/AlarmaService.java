package alarma;

public interface AlarmaService {
    public void notificarAbastecimiento();

    public void notificarReparacion();

    public void notificarEscasezSuministros();

    public void notificarError();

    public void notificarAusenciaMoneda();

    public void notificarEscazesIngredientes();

    public void notificarMalFuncionamiento();
}
