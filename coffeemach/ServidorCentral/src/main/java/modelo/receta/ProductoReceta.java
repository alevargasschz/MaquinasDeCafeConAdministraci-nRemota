package modelo.receta;

import java.util.List;
import java.util.Map;

import com.zeroc.Ice.Communicator;
import com.zeroc.Ice.Current;

import modelo.ConexionBD;
import modelo.ManejadorDatos;
import servicios.RecetaService;

public class ProductoReceta implements RecetaService {

    private Communicator communicator;

    /**
     * @param communicator the communicator to set
     */
    public void setCommunicator(Communicator communicator) {
        this.communicator = communicator;
    }

    @Override
    public String[] consultarIngredientes(Current current) {
        ConexionBD cbd = new ConexionBD(communicator);
        cbd.conectarBaseDatos();
        ManejadorDatos md = new ManejadorDatos();
        md.setConexion(cbd.getConnection());

        String[] ret = md.consultarIngredientes();

        cbd.cerrarConexion();

        return ret;
    }

    @Override
    public String[] consultarRecetas(Current current) {
        ConexionBD cbd = new ConexionBD(communicator);
        cbd.conectarBaseDatos();
        ManejadorDatos md = new ManejadorDatos();
        md.setConexion(cbd.getConnection());

        String[] ret = md.consultarRecetas();

        cbd.cerrarConexion();

        return ret;
    }

    @Override
    public String[] consultarProductos(Current current) {
        ConexionBD cbd = new ConexionBD(communicator);
        cbd.conectarBaseDatos();
        ManejadorDatos md = new ManejadorDatos();
        md.setConexion(cbd.getConnection());

        List<String> listaAsociada = md.consultaRecetasCompleta();

        cbd.cerrarConexion();

        if (!listaAsociada.equals(null)) {

            String[] retorno = new String[listaAsociada.size()];

            for (int i = 0; i < listaAsociada.size(); i++) {

                retorno[i] = listaAsociada.get(i);
            }

            return retorno;
        }

        return null;
    }

    @Override
    public void definirProducto(String nombre, int precio, Map<String, Integer> ingredientes, Current current) {
        throw new UnsupportedOperationException("Unimplemented method 'definirProducto'");
    }

    @Override
    public void borrarReceta(int cod, Current current) {
        ConexionBD cbd = new ConexionBD(communicator);
        cbd.conectarBaseDatos();
        ManejadorDatos md = new ManejadorDatos();
        md.setConexion(cbd.getConnection());

        md.borrarReceta(cod);

        cbd.cerrarConexion();
    }

    @Override
    public void definirRecetaIngrediente(int idReceta, int idIngrediente, int valor, Current current) {

        ConexionBD cbd = new ConexionBD(communicator);
        cbd.conectarBaseDatos();
        ManejadorDatos md = new ManejadorDatos();
        md.setConexion(cbd.getConnection());

        md.registrarRecetaIngrediente(idReceta, idIngrediente, valor);

        cbd.cerrarConexion();
    }

    @Override
    public String registrarReceta(String nombre, int precio, Current current) {
        ConexionBD cbd = new ConexionBD(communicator);
        cbd.conectarBaseDatos();
        ManejadorDatos md = new ManejadorDatos();
        md.setConexion(cbd.getConnection());

        String ret = md.registrarReceta(nombre, precio);

        cbd.cerrarConexion();

        return ret;
    }

    @Override
    public String registrarIngrediente(String nombre, Current current) {
        ConexionBD cbd = new ConexionBD(communicator);
        cbd.conectarBaseDatos();
        ManejadorDatos md = new ManejadorDatos();
        md.setConexion(cbd.getConnection());

        String ret = md.registrarIngrediente(nombre);

        cbd.cerrarConexion();

        return ret;
    }

}
