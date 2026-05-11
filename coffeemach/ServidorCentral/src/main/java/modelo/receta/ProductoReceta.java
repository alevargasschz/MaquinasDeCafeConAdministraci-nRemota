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

        if (listaAsociada == null) {
            return new String[0];
        }

        String[] retorno = new String[listaAsociada.size()];
        for (int i = 0; i < listaAsociada.size(); i++) {
            retorno[i] = listaAsociada.get(i);
        }

        return retorno;
    }

    @Override
    public void definirProducto(String nombre, int precio, Map<String, Integer> ingredientes, Current current) {
        if (nombre == null || nombre.trim().isEmpty()) {
            return;
        }

        ConexionBD cbd = new ConexionBD(communicator);
        cbd.conectarBaseDatos();
        ManejadorDatos md = new ManejadorDatos();
        md.setConexion(cbd.getConnection());

        try {
            String registro = md.registrarReceta(nombre.trim(), precio);
            int idReceta = extraerIdReceta(registro, nombre.trim(), precio, md);
            if (idReceta <= 0) {
                return;
            }

            if (ingredientes != null) {
                for (Map.Entry<String, Integer> entry : ingredientes.entrySet()) {
                    int idIngrediente = resolverIdIngrediente(entry.getKey(), md);
                    Integer valor = entry.getValue();
                    if (idIngrediente > 0 && valor != null && valor > 0) {
                        md.registrarRecetaIngrediente(idReceta, idIngrediente, valor);
                    }
                }
            }
        } finally {
            cbd.cerrarConexion();
        }
    }

    private int extraerIdReceta(String registro, String nombre, int precio, ManejadorDatos md) {
        if (registro != null && !registro.trim().isEmpty()) {
            String[] partes = registro.split("-");
            if (partes.length > 0) {
                try {
                    return Integer.parseInt(partes[0].trim());
                } catch (NumberFormatException e) {
                    // Continua con busqueda por nombre/precio.
                }
            }
        }

        String[] recetas = md.consultarRecetas();
        if (recetas == null) {
            return -1;
        }

        for (String receta : recetas) {
            if (receta == null || receta.trim().isEmpty()) {
                continue;
            }
            String[] partes = receta.split("-");
            if (partes.length < 3) {
                continue;
            }
            try {
                int id = Integer.parseInt(partes[0].trim());
                String nombreReceta = partes[1].trim();
                int precioReceta = Integer.parseInt(partes[2].trim());
                if (nombreReceta.equalsIgnoreCase(nombre) && precioReceta == precio) {
                    return id;
                }
            } catch (NumberFormatException e) {
                // Ignora filas mal formadas.
            }
        }

        return -1;
    }

    private int resolverIdIngrediente(String llave, ManejadorDatos md) {
        if (llave == null || llave.trim().isEmpty()) {
            return -1;
        }

        String valor = llave.trim();
        try {
            return Integer.parseInt(valor);
        } catch (NumberFormatException e) {
            // Si no es numerico, se intentara resolver por nombre.
        }

        String[] ingredientes = md.consultarIngredientes();
        if (ingredientes == null) {
            return -1;
        }

        for (String ingrediente : ingredientes) {
            if (ingrediente == null || ingrediente.trim().isEmpty()) {
                continue;
            }
            String[] partes = ingrediente.split("-");
            if (partes.length < 2) {
                continue;
            }
            if (partes[1].trim().equalsIgnoreCase(valor)) {
                try {
                    return Integer.parseInt(partes[0].trim());
                } catch (NumberFormatException e) {
                    return -1;
                }
            }
        }

        return -1;
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
