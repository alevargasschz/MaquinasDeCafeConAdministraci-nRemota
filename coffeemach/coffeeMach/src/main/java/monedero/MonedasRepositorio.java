package monedero;

import interfaces.Repositorio;

public class MonedasRepositorio extends Repositorio<String, DepositoMonedas> {

    private static MonedasRepositorio instance;

    public static MonedasRepositorio getInstance() {
        if (instance == null) {
            instance = new MonedasRepositorio();
        }
        return instance;
    }

    private MonedasRepositorio() {
        super("monedas.bd");
    }

    public void loadDataP() {
        DepositoMonedas deposito100 = new DepositoMonedas("100", 12, 10, 5, 30);
        DepositoMonedas deposito200 = new DepositoMonedas("200", 12, 10, 5, 30);
        DepositoMonedas deposito500 = new DepositoMonedas("500", 12, 10, 5, 30);
        addElement("100", deposito100);
        addElement("200", deposito200);
        addElement("500", deposito500);

    }
}
