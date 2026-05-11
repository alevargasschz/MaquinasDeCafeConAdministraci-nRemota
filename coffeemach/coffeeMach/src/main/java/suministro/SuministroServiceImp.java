package suministro;

import java.util.ArrayList;
import java.util.List;

import ingrediente.Ingrediente;
import ingrediente.IngredienteRepositorio;

public class SuministroServiceImp implements SuministroService {

    private final IngredienteRepositorio ingredientes = IngredienteRepositorio.getInstance();

    @Override
    public boolean verificatExistenciaSuministro(String sumId) {
        if (sumId == null || sumId.trim().isEmpty()) {
            return false;
        }

        String key = sumId.trim();
        Ingrediente directo = ingredientes.findByKey(key);
        if (directo != null) {
            return directo.getCantidad() > 0;
        }

        for (Ingrediente ing : ingredientes.getValues()) {
            if (key.equalsIgnoreCase(ing.getNombre()) || key.equals(ing.getCodAlarma())) {
                return ing.getCantidad() > 0;
            }
        }

        return false;
    }

    @Override
    public String[] darInsumos() {
        List<String> insumos = new ArrayList<>();
        for (Ingrediente ing : ingredientes.getValues()) {
            insumos.add(ing.getNombre() + "-" + ing.getCantidad());
        }
        return insumos.toArray(new String[0]);
    }

    @Override
    public void dispensarSuministro(String sumId) {
        if (sumId == null || sumId.trim().isEmpty()) {
            return;
        }

        String key = sumId.trim();
        Ingrediente ing = ingredientes.findByKey(key);
        if (ing == null) {
            for (Ingrediente candidato : ingredientes.getValues()) {
                if (key.equalsIgnoreCase(candidato.getNombre()) || key.equals(candidato.getCodAlarma())) {
                    ing = candidato;
                    break;
                }
            }
        }

        if (ing != null && ing.getCantidad() > 0) {
            ing.setCantidad(ing.getCantidad() - 1);
            ingredientes.addElement(ing.getNombre(), ing);
        }
    }

}
