package productoReceta;

import java.util.*;

import ingrediente.Ingrediente;
import ingrediente.IngredienteRepositorio;
import interfaces.Repositorio;

public class RecetaRepositorio extends Repositorio<String, Receta> {

    private static RecetaRepositorio instance;

    public static RecetaRepositorio getInstance() {
        if (instance == null) {
            instance = new RecetaRepositorio();
        }
        return instance;
    }

    private RecetaRepositorio() {
        super("recetas.bd");
    }

    public void loadDataP() {
        IngredienteRepositorio ingredientes = IngredienteRepositorio.getInstance();

        HashMap<Ingrediente, Double> ingTinto = new HashMap<Ingrediente, Double>();
        ingTinto.put(ingredientes.findByKey("Agua"), (double) 100);
        ingTinto.put(ingredientes.findByKey("Cafe"), (double) 10);
        ingTinto.put(ingredientes.findByKey("Azucar"), (double) 10);
        ingTinto.put(ingredientes.findByKey("Vaso"), (double) 1);
        Receta tinto = new Receta("Tinto", "1", 800, ingTinto);
        addElement("1", tinto);

        HashMap<Ingrediente, Double> ingTintoCargado = new HashMap<Ingrediente, Double>();
        ingTintoCargado.put(ingredientes.findByKey("Agua"), (double) 100);
        ingTintoCargado.put(ingredientes.findByKey("Cafe"), (double) 30);
        ingTintoCargado.put(ingredientes.findByKey("Azucar"), (double) 10);
        ingTintoCargado.put(ingredientes.findByKey("Vaso"), (double) 1);
        Receta tintoCargado = new Receta("Tinto Cargado", "2", 1000, ingTintoCargado);
        addElement("2", tintoCargado);

    }
}
