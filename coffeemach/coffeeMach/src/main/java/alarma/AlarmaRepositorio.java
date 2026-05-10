package alarma;

import interfaces.Repositorio;

public class AlarmaRepositorio extends Repositorio<String, Alarma> {

    private static AlarmaRepositorio instance;

    public static AlarmaRepositorio getInstance() {
        if (instance == null) {
            instance = new AlarmaRepositorio();
        }
        return instance;
    }

    private AlarmaRepositorio() {
        super("alarmas.bd");
    }

    public void loadDataP() {

    }

}
