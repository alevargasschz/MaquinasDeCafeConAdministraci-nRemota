package modelo;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class ManejadorDatos {

	private Connection conexion;

	/**
	 * <b>Descripción:</b>Este método crea una nueva alarma en el sistema <br>
	 * <b>Pre:</b>Se debe enviar un id válido de alarma, un id válido de maquina
	 * en el objeto enviado por parámetro
	 * 
	 * @param aM
	 *           Corresponde a la alarma de una máquina en específico.
	 */
	public void registrarAlarma(AlarmaMaquina aM) {
		try {

			// Verificar que no exista esa alarma en el sistema

			String busAlaCoincidente = "SELECT * FROM ALARMA_MAQUINA alx WHERE alx.FECHA_FINAL is null AND alx.ID_ALARMA = ? AND alx.ID_MAQUINA = ?";
			PreparedStatement psx = conexion
					.prepareStatement(busAlaCoincidente);
			psx.setInt(1, aM.getIdAlarma());
			psx.setInt(2, aM.getIdMaquina());
			ResultSet rsx = psx.executeQuery();

			if (rsx.next()) {
				System.out
						.println("Alarma ya agregada previamente, se notificará al operador encargado");
			} else {

				Statement st = conexion.createStatement();
				st.execute("SELECT NEXTVAL('CONSECALARMA')");
				ResultSet rs = st.getResultSet();
				int consecutivo = 0;
				if (rs.next()) {
					consecutivo = rs.getInt(1);
				}

				String insertnuevaA = "INSERT INTO ALARMA_MAQUINA (ID_ALARMA,ID_MAQUINA,FECHA_INICIAL,CONSECUTIVO) VALUES (?,?,?,?)";
				PreparedStatement pst = conexion
						.prepareStatement(insertnuevaA);
				pst.setInt(1, aM.getIdAlarma());
				pst.setInt(2, aM.getIdMaquina());
				pst.setDate(3, new Date(aM.getFechaInicialAlarma().getTime()));
				pst.setInt(4, consecutivo);
				pst.executeUpdate();

			}

		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

	/**
	 * 
	 * @param idMaquina
	 * @param idAlarma
	 * @param fechafinal
	 */
	public void desactivarAlarma(int idMaquina, int idAlarma,
			java.util.Date fechafinal) {
		String updateAlarma = "UPDATE ALARMA_MAQUINA SET FECHA_FINAL = ? WHERE ID_ALARMA = ? AND ID_MAQUINA = ?";

		try {
			PreparedStatement ps = conexion.prepareStatement(updateAlarma);
			ps.setDate(1, new Date(fechafinal.getTime()));
			ps.setInt(2, idAlarma);
			ps.setInt(3, idMaquina);

			ps.executeUpdate();

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * <b>Descripcion:</b>Este método retorna un conjunto de asignaciones que
	 * tiene un operador de logística<br>
	 * <b>Pre:</b>El código del operador debe corresponder con un registro
	 * existente en la BD
	 * 
	 * @param codigooperador
	 *                       Corresponde al código del operador.
	 * @return List<AsignacionMaquina> Es la lista de asignaciones de un
	 *         operador.
	 */
	public List<AsignacionMaquina> listaAsignaciones(int codigooperador) {
		try {
			String queryAsign = "SELECT * FROM ASIGNACION_MAQUINA a, MAQUINA m WHERE a.ID_OPERADOR = ? AND a.ID_MAQUINA = m.IDMAQUINA";
			PreparedStatement pst = conexion.prepareStatement(queryAsign);
			pst.setInt(1, codigooperador);
			pst.executeQuery();
			ResultSet rs = pst.getResultSet();

			List<AsignacionMaquina> asm = new ArrayList<AsignacionMaquina>();

			while (rs.next()) {
				int idOperador = codigooperador;
				int idaquina = rs.getInt("IDMAQUINA");
				String ubicacion = rs.getString("UBICACION");

				Operador op = new Operador();
				op.setId(idOperador);
				Maquina maquina = new Maquina(idaquina, ubicacion);
				AsignacionMaquina asmtemp = new AsignacionMaquina(maquina,
						op);
				asm.add(asmtemp);
			}

			return asm;

		} catch (SQLException e) {
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * 
	 * @param codigooperador
	 * @return
	 */
	public List<String> listaAsignacionMaquinasDanadas(int codigooperador) {
		try {

			String queryasign = "select m.idmaquina, m.ubicacion, am.fecha_inicial, am.id_alarma from asignacion_maquina ax, alarma_maquina am, maquina m "
					+ "where ax.id_operador = ? and ax.id_maquina = am.id_maquina and am.fecha_final is null and am.id_maquina = m.idmaquina";

			PreparedStatement ps = conexion.prepareStatement(queryasign);
			ps.setInt(1, codigooperador);
			ResultSet rs = ps.executeQuery();

			List<String> asignaciones = new ArrayList<String>();

			while (rs.next()) {

				int idAlarma = rs.getInt(4);
				String querydescripAlarma = "select nombre from alarma where idalarma = ?";
				PreparedStatement ps3 = conexion
						.prepareStatement(querydescripAlarma);
				ps3.setInt(1, idAlarma);
				ResultSet rs3 = ps3.executeQuery();
				rs3.next();

				int idMaquina = rs.getInt(1);
				String ubicacion = rs.getString(2);
				Date fechaIni = rs.getDate(3);
				String descrip = rs3.getString(1);
				String dato = idMaquina + "#" + ubicacion + "#" + fechaIni
						+ "#" + idAlarma + "#" + descrip;
				asignaciones.add(dato);

			}

			return asignaciones;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * <b>Descripcion:</b> Registra un reporte de ventas de una máquina de café<br>
	 * <b>Pre:</b> Se debe enviar las ventas de la maquina donde cada venta debe
	 * contener:<br />
	 * 1) El identificador de la máquina <br>
	 * 2) La fecha en que se hizo el último corte <br>
	 * 3) La fecha de este corte <br>
	 * 4) El identificador de la receta (debe corresponder con el de la BD) <br>
	 * 5) El valor de la receta
	 * 
	 * @param vM
	 *           Corresponde a las ventas de la máquina en un periodo
	 *           determinado
	 */
	public void registrarReporteVentas(VentasMaquina vM) {
		try {

			int idMaquina = vM.getIdMaquina();
			Date fechaInicial = new Date(vM.getFechaInicial().getTime());
			Date fechaFinal = new Date(vM.getFechaFinal().getTime());
			double valorVenta = 0;

			Statement st = conexion.createStatement();
			st.execute("SELECT NEXTVAL('CONSECUTIVO')");
			ResultSet rs = st.getResultSet();
			int consecutivo = 0;
			if (rs.next()) {
				consecutivo = rs.getInt(1);
			}

			String insert = "INSERT INTO VENTAS (CONSECUTIVO,IDMAQUINA,VALOR,FECHA_INICIAL,FECHA_FINAL) "
					+ "VALUES (?,?,?,?,?)";

			PreparedStatement pst = conexion.prepareStatement(insert);
			pst.setInt(1, consecutivo);
			pst.setInt(2, idMaquina);
			pst.setDouble(3, 0);
			pst.setDate(4, fechaInicial);
			pst.setDate(5, fechaFinal);
			pst.executeUpdate();

			List<VentasReceta> lR = vM.getDetalle();

			for (VentasReceta vR : lR) {
				int idReceta = vR.getIdReceta();
				double valor = vR.getValorreceta();
				valorVenta += valor;

				int consecutivo1 = 0;
				Statement st1 = conexion.createStatement();
				st1.execute("SELECT NEXTVAL('CONSECUTIVO1')");
				ResultSet rs1 = st1.getResultSet();
				if (rs1.next()) {
					consecutivo1 = rs1.getInt(1);
				}

				String insert2 = "INSERT INTO VENTAS_RECETA (CONSECUTIVO,ID_RECETA,CONSECUTIVO_VENTAS) VALUES(?,?,?)";
				PreparedStatement pst1 = conexion.prepareStatement(insert2);
				pst1.setInt(1, consecutivo1);
				pst1.setInt(2, idReceta);
				pst1.setInt(3, consecutivo);
				pst1.executeUpdate();

			}

			String updateventa = "UPDATE VENTAS SET VALOR = ? WHERE CONSECUTIVO = ?";
			PreparedStatement pst2 = conexion.prepareStatement(updateventa);
			pst2.setDouble(1, valorVenta);
			pst2.setInt(2, consecutivo);
			pst2.executeUpdate();

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * <b>Descripción:</b>Asigna un operador a una máquina de café
	 * <b>Pre:</b>Tanto el código del operador, como el código de la máquina
	 * deben existir en la base de datos
	 * 
	 * @param idOperador
	 *                   Corresponde al identificador del operador
	 * @param idMaquina
	 *                   Corresponde al identificador de la máquina
	 * @return boolean Confirma o rechaza la asignación de una máquina a un
	 *         operador. <b>Pre:<b/> Tanto el id del operdor como el de la
	 *         maquina deben existir
	 */
	public boolean asignarOperador(int idOperador, int idMaquina) {
		boolean retorno = false;

		try {

			String insert = "INSERT INTO ASIGNACION_MAQUINA (ID_MAQUINA,ID_OPERADOR) VALUES (?,?)";
			PreparedStatement pst = conexion.prepareStatement(insert);
			pst.setInt(1, idMaquina);
			pst.setInt(2, idOperador);
			pst.executeUpdate();
			retorno = true;

		} catch (SQLException e) {
			e.printStackTrace();
		}

		return retorno;
	}

	/**
	 * <b>Descripción:</b>Retorna el nombre del operador y la ubicación de la
	 * maquin de café <b>Pre:</b> El identificador de la máquina debe
	 * corresponder con uno existente en la base de datos
	 * 
	 * @param idMaquina
	 *                  Corresponde al identificador de la máquina
	 * @return String Retorna el nombre del operador y la ubicación de la
	 *         máquina
	 */
	public String darOperador(int idMaquina) {

		try {

			String query = "SELECT o.NOMBRE, m.UBICACION, o.CORREO FROM OPERADORES o, MAQUINA m, ASIGNACION_MAQUINA am"
					+ " WHERE m.IDMAQUINA = ? AND m.IDMAQUINA = am.ID_MAQUINA AND am.ID_OPERADOR = o.IDOPERADOR";
			PreparedStatement st = conexion.prepareStatement(query);
			st.setInt(1, idMaquina);
			st.executeQuery();

			ResultSet rs = st.getResultSet();

			if (rs.next()) {
				String nombre = rs.getString(1);
				String ubicacion = rs.getString(2);
				String correo = rs.getString(3);
				return nombre + "-" + ubicacion + "#" + correo;

			}

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	public String darCorreoOperador(int codigoOperador) {

		try {

			String querycorreo = "select correo from operadores where idoperador = ?";
			PreparedStatement ps2 = conexion.prepareStatement(querycorreo);

			ps2.setInt(1, codigoOperador);
			ResultSet rs2 = ps2.executeQuery();
			rs2.next();
			return rs2.getString(1);

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * <b>Descripción:</b>Retorna el nombre de la alarma generada <b>Pre:</b>El
	 * identificador de la alarma debe corresponder con uno existente en la BD.
	 * 
	 * @param idAlarma
	 *                 Corresponde al identificador de la alarma.
	 * @return String Retorna el nombre de la alarma.
	 */
	public String darNombreAlarma(int idAlarma) {
		try {

			String query = "SELECT * FROM ALARMA a WHERE a.IDALARMA = ?";
			PreparedStatement ps = conexion.prepareStatement(query);
			ps.setInt(1, idAlarma);
			ps.executeQuery();

			ResultSet rs = ps.getResultSet();

			if (rs.next()) {
				String nombre = rs.getString("NOMBRE");
				return nombre;
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 
	 * @param codigoOperador
	 * @param password
	 * @return
	 */
	public boolean existeOperador(int codigoOperador, String password) {
		try {
			String query = "SELECT * FROM OPERADORES o WHERE IDOPERADOR = ? AND CONTRASENA = ?";
			PreparedStatement ps = conexion.prepareStatement(query);
			ps.setInt(1, codigoOperador);
			ps.setString(2, password);
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				return true;
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

	public String registrarIngrediente(String nombre) {

		String retorno = "";

		try {

			// Cuenta de Ingredientes Existentes

			int cuentaIng = 0;

			String q = "SELECT COUNT(*) FROM INGREDIENTE";

			PreparedStatement p = conexion.prepareStatement(q);

			ResultSet resultado = p.executeQuery();

			if (resultado.next()) {

				cuentaIng = resultado.getInt(1);
			}

			// Verificar que no exista ese ingrediente en el sistema

			String query = "SELECT * FROM INGREDIENTE i WHERE i.NOMBRE = ?";

			PreparedStatement psx = conexion.prepareStatement(query);

			psx.setString(1, nombre);

			ResultSet rs = psx.executeQuery();

			if (rs.next()) {

				System.out.println("Ingrediente Existe");

			} else {

				// Secuencia para los ingredientes

				String valor = "SELECT NEXTVAL('SEQ_INGREDIENTES')";

				Statement st = conexion.createStatement();

				st.execute(valor);

				ResultSet rss = st.getResultSet();

				int consec = 0;

				if (rss.next()) {

					consec = rss.getInt(1);

				}

				// Insertado del ingrediente

				String insert = "INSERT INTO INGREDIENTE (IDINGREDIENTE,NOMBRE) values (?,?)";

				PreparedStatement psx2 = conexion.prepareStatement(insert);

				psx2.setInt(1, consec + cuentaIng);
				psx2.setString(2, nombre);
				psx2.executeUpdate();

				retorno += consec + "-" + nombre + "-";

				// Secuencia para las alarmas

				String valorSec = "SELECT NEXTVAL('SEQ_ALARMAS')";

				Statement sta = conexion.createStatement();

				sta.execute(valorSec);

				ResultSet rss2 = st.getResultSet();

				int consec2 = 0;

				if (rss2.next()) {

					consec2 = rss2.getInt(1);

				}

				// Insertado de alarmas asociadas al ingrediente

				String insertAlarma = "INSERT INTO ALARMA (IDALARMA, NOMBRE) values (?,?)";

				PreparedStatement psx3 = conexion
						.prepareStatement(insertAlarma);

				psx3.setInt(1, consec2);
				psx3.setString(2, "nivel bajo de " + nombre);

				String insertAlarmaCrit = "INSERT INTO ALARMA (IDALARMA, NOMBRE) values (?,?)";

				PreparedStatement psx4 = conexion
						.prepareStatement(insertAlarmaCrit);

				int consec3 = consec2 + 4;

				psx4.setInt(1, consec3);
				psx4.setString(2, "Nivel critico de " + nombre);

				retorno += consec2 + "-" + consec3;

			}

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return retorno;

	}

	public String registrarReceta(String nombre, int precio) {

		String retorno = "";

		try {

			// Cuenta de Recetas Existentes

			int cuentaRecetas = 0;

			String q = "SELECT COUNT(*) FROM RECETA";

			PreparedStatement p = conexion.prepareStatement(q);

			ResultSet resultado = p.executeQuery();

			if (resultado.next()) {

				cuentaRecetas = resultado.getInt(1);
			}

			// Verificar que no exista esa receta en el sistema

			String query = "SELECT * FROM RECETA i WHERE i.NOMBRE = ?";

			PreparedStatement psx = conexion.prepareStatement(query);

			psx.setString(1, nombre);

			ResultSet rs = psx.executeQuery();

			if (rs.next()) {

				System.out.println("Receta Existe");

			} else {

				String valor = "SELECT NEXTVAL('SEQ_RECETA')";

				Statement st = conexion.createStatement();

				st.execute(valor);

				ResultSet rss = st.getResultSet();

				int consec = 0;

				if (rss.next()) {

					consec = rss.getInt(1);

				}

				String insert = "INSERT INTO RECETA (IDRECETA,NOMBRE,PRECIO) values (?,?,?)";

				PreparedStatement psx2 = conexion.prepareStatement(insert);

				psx2.setInt(1, consec + cuentaRecetas);
				psx2.setString(2, nombre);
				psx2.setInt(3, precio);
				psx2.executeUpdate();

				retorno += consec + "-" + nombre + "-" + precio;

			}

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return retorno;

	}

	public void registrarRecetaIngrediente(int idReceta, int idIngrediente,
			int valor) {

		try {

			String insert = "INSERT INTO RECETA_INGREDIENTE (IDRECETA,IDINGREDIENTE,UNIDADES) values (?,?,?)";

			PreparedStatement psx2 = conexion.prepareStatement(insert);

			psx2.setInt(1, idReceta);
			psx2.setInt(2, idIngrediente);
			psx2.setInt(3, valor);
			psx2.executeUpdate();

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void borrarReceta(int cod) {

		try {

			// delete from tabla t where t.vALOR = ?

			String delete = "DELETE FROM RECETA_INGREDIENTE R WHERE R.IDRECETA = ?";

			PreparedStatement psx2 = conexion.prepareStatement(delete);

			psx2.setInt(1, cod);

			psx2.executeUpdate();

			String deleteReceta = "DELETE FROM RECETA R WHERE R.IDRECETA = ?";

			PreparedStatement psx = conexion.prepareStatement(deleteReceta);

			psx.setInt(1, cod);

			psx.executeUpdate();

		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

	public String[] consultarRecetas() {

		String[] retorno = null;

		try {
			// Cuenta de Recetas Existentes

			int cuenta = 0;

			String q = "SELECT COUNT(*) FROM RECETA";

			PreparedStatement p = conexion.prepareStatement(q);

			ResultSet resultado = p.executeQuery();

			if (resultado.next()) {

				cuenta = resultado.getInt(1);
			}

			retorno = new String[cuenta];

			String query = "SELECT * FROM RECETA i";

			PreparedStatement psx = conexion.prepareStatement(query);

			ResultSet rs = psx.executeQuery();

			int cont = 0;

			while (rs.next() & cont < cuenta) {

				int cod = rs.getInt(1);
				String nombre = rs.getString(2);
				int precio = rs.getInt(3);

				retorno[cont] = cod + "-" + nombre + "-" + precio;
				cont++;
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}

		return retorno;

	}

	public String[] consultarIngredientes() {

		String[] retorno = null;

		try {
			// Cuenta de Recetas Existentes

			int cuenta = 0;

			String q = "SELECT COUNT(*) FROM INGREDIENTE";

			PreparedStatement p = conexion.prepareStatement(q);

			ResultSet resultado = p.executeQuery();

			if (resultado.next()) {

				cuenta = resultado.getInt(1);
			}

			retorno = new String[cuenta];

			String query = "SELECT * FROM INGREDIENTE i";

			PreparedStatement psx = conexion.prepareStatement(query);

			ResultSet rs = psx.executeQuery();

			int cont = 0;

			while (rs.next() & cont < cuenta) {

				int cod = rs.getInt(1);
				String nombre = rs.getString(2);

				retorno[cont] = cod + "-" + nombre;
				cont++;
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}

		return retorno;

	}

	/**
	 * <b>Descripción:</b>Modifica el Objeto Connection para realizar la
	 * conexion a la BD.
	 * 
	 * @param conexion
	 *                 Es el objeto Connection
	 */
	public void setConexion(Connection conexion) {
		this.conexion = conexion;
	}

	public String[] consultarRecetaIngrediente() {

		String[] retorno = null;

		try {

			// Cuenta de RecetaIngrediente

			int cuenta = 0;

			String q = "SELECT COUNT(*) FROM RECETA_INGREDIENTE";

			PreparedStatement p = conexion.prepareStatement(q);

			ResultSet resultado = p.executeQuery();

			if (resultado.next()) {

				cuenta = resultado.getInt(1);

			}

			retorno = new String[cuenta];

			String query = "SELECT * FROM RECETA_INGREDIENTE i";

			PreparedStatement psx = conexion.prepareStatement(query);

			ResultSet rs = psx.executeQuery();

			int cont = 0;

			while (rs.next()) {

				int codReceta = rs.getInt(1);
				int codIngrediente = rs.getInt(2);
				int cantidad = rs.getInt(3);

				retorno[cont] = codReceta + "-" + codIngrediente + "-"
						+ cantidad;
				cont++;
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}

		return retorno;
	}

	public ArrayList<String> consultaRecetasCompleta() {

		ArrayList<String> listaRecetas = new ArrayList<String>();

		ArrayList<String> listaRecetaIngrediente = new ArrayList<String>();

		ArrayList<String> retorno = new ArrayList<String>();

		try {

			// Recetas

			String query = "SELECT * FROM RECETA i";

			PreparedStatement psx = conexion.prepareStatement(query);

			ResultSet rs = psx.executeQuery();

			while (rs.next()) {

				int cod = rs.getInt(1);
				String nombre = rs.getString(2);
				int precio = rs.getInt(3);

				listaRecetas.add(cod + "-" + nombre + "-" + precio);

			}

			// Receta-Ingrediente

			String query3 = "SELECT * FROM RECETA_INGREDIENTE i";

			PreparedStatement psx3 = conexion.prepareStatement(query3);

			ResultSet rs3 = psx3.executeQuery();

			while (rs3.next()) {

				int codReceta = rs3.getInt(1);
				int codIngrediente = rs3.getInt(2);
				int cantidad = rs3.getInt(3);

				listaRecetaIngrediente.add(codReceta + "-" + codIngrediente
						+ "-" + cantidad);

			}

			for (int i = 0; i < listaRecetas.size(); i++) {

				String concat = "";

				String[] splitReceta = listaRecetas.get(i).split("-");

				String anterior = "";
				String actual = "";

				if (i == 0) {
					anterior = splitReceta[0].trim();

					concat += listaRecetas.get(i);

				} else {

					concat += listaRecetas.get(i);

					actual = splitReceta[0].trim();

					if (!anterior.equals(actual)) {

						anterior = actual;
					}

				}

				for (int i2 = 0; i2 < listaRecetaIngrediente.size(); i2++) {

					String[] splitRecetaIngrediente = listaRecetaIngrediente
							.get(i2).split("-");

					if (splitRecetaIngrediente[0].equals(anterior)) {

						String cantidadProducto = splitRecetaIngrediente[2];

						String idNombreIng = consultarIngrediente(Integer
								.parseInt(splitRecetaIngrediente[1].trim()));

						String alarmas = validarAlarma(Integer
								.parseInt(splitRecetaIngrediente[1].trim()));

						concat += "#" + idNombreIng + "-" + alarmas + "-"
								+ cantidadProducto;

					}

				}

				retorno.add(concat);

			}

		} catch (SQLException e) {
			e.printStackTrace();
		}

		return retorno;

	}

	private String consultarIngrediente(int id) throws SQLException {

		String retorno = "";

		// Ingredientes

		String query2 = "SELECT * FROM INGREDIENTE i WHERE i.IDINGREDIENTE = ?";

		PreparedStatement psx2 = conexion.prepareStatement(query2);

		psx2.setInt(1, id);

		ResultSet rs2 = psx2.executeQuery();

		while (rs2.next()) {

			int cod = rs2.getInt(1);
			String nombre = rs2.getString(2);

			retorno = cod + "-" + nombre;

		}

		return retorno;
	}

	public String validarAlarma(int codIng) {

		String retorno = "";

		if (codIng == 1) {

			retorno = "8" + "-" + "12";
		} else if (codIng == 2) {
			retorno = "9" + "-" + "13";
		} else if (codIng == 3) {
			retorno = "10" + "-" + "14";
		} else if (codIng == 4) {
			retorno = "11" + "-" + "15";
		}

		return retorno;

	}
}
