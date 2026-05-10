package ServerControl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import com.zeroc.Ice.Communicator;

import modelo.*;

public class ServerControl {

	ArrayList<String> listaAsociada = new ArrayList<String>();
	private Communicator comunicator;

	public ServerControl(Communicator com) {
		this.comunicator = com;
		// ConsolaAdministracion cAdmin=new ConsolaAdministracion(this);
		// Thread th=new Thread(cAdmin);
		// th.start();

	}

	public boolean asignarOperador(int idMaquina, int idOperador) {
		if (idMaquina == 0 || idOperador == 0) {
			return false;
		} else {
			ConexionBD cbd = new ConexionBD(comunicator);
			cbd.conectarBaseDatos();
			ManejadorDatos md = new ManejadorDatos();
			md.setConexion(cbd.getConnection());
			md.asignarOperador(idOperador, idMaquina);
			cbd.cerrarConexion();
			return true;
		}
	}

	public List<String> listaAsignaciones(int codigooperador) {
		List<String> lista = new ArrayList<String>();

		ConexionBD cbd = new ConexionBD(comunicator);
		cbd.conectarBaseDatos();
		ManejadorDatos md = new ManejadorDatos();
		md.setConexion(cbd.getConnection());

		List<AsignacionMaquina> asmL = md.listaAsignaciones(codigooperador);

		for (AsignacionMaquina asm : asmL) {
			int idMaq = asm.getMaquina().peticioncodigo();
			String ubicacion = asm.getMaquina().getUbicacion();

			String dato = "" + idMaq + "-" + ubicacion;
			lista.add(dato);
		}
		cbd.cerrarConexion();
		return lista;
	}

	public List<String> listaAsignacionesMDanada(int codigoOperador) {
		ConexionBD cbd = new ConexionBD(comunicator);
		cbd.conectarBaseDatos();
		ManejadorDatos md = new ManejadorDatos();
		md.setConexion(cbd.getConnection());

		List<String> listaAsign = md
				.listaAsignacionMaquinasDanadas(codigoOperador);
		cbd.cerrarConexion();
		return listaAsign;
	}

	public String darCorreoOperador(int codigoOperador) {
		ConexionBD cbd = new ConexionBD(comunicator);
		cbd.conectarBaseDatos();
		ManejadorDatos md = new ManejadorDatos();
		md.setConexion(cbd.getConnection());
		String correo = md.darCorreoOperador(codigoOperador);
		cbd.cerrarConexion();
		return correo;
	}

	public boolean existeOperador(int codigoOperador, String contrasena) {

		if (codigoOperador != 0 && contrasena != null) {
			ConexionBD cbd = new ConexionBD(comunicator);
			cbd.conectarBaseDatos();
			ManejadorDatos md = new ManejadorDatos();
			md.setConexion(cbd.getConnection());
			boolean resultado = md.existeOperador(codigoOperador, contrasena);
			cbd.cerrarConexion();
			return resultado;
		}
		return false;

	}

}
