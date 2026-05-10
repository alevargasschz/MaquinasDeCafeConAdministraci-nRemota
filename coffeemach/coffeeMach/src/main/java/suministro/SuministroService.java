package suministro;

public interface SuministroService {

    boolean verificatExistenciaSuministro(String sumId);

    String[] darInsumos();

    void dispensarSuministro(String sumId);

}
