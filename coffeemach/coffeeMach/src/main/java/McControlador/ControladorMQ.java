package McControlador;

import servicios.*;
import monedero.DepositoMonedas;
import monedero.MonedasRepositorio;
import productoReceta.Receta;
import productoReceta.RecetaRepositorio;

import java.util.*;
import java.io.*;
import java.util.Map.Entry;
import javax.swing.JFrame;
import java.awt.event.*;
import interfazUsuario.Interfaz;
import com.zeroc.Ice.Current;

import alarma.Alarma;
import alarma.AlarmaRepositorio;
import ingrediente.Ingrediente;
import ingrediente.IngredienteRepositorio;

public class ControladorMQ implements Runnable, ServicioAbastecimiento {

	private AlarmaServicePrx alarmaServicePrx;
	private VentaServicePrx ventasService;

	// @Reference
	private AlarmaRepositorio alarmas = AlarmaRepositorio.getInstance();
	// @Reference
	private IngredienteRepositorio ingredientes = IngredienteRepositorio.getInstance();
	// @Reference
	private MonedasRepositorio monedas = MonedasRepositorio.getInstance();
	// @Reference
	private RecetaRepositorio recetas = RecetaRepositorio.getInstance();
	// @Referenc
	private VentaRepositorio ventas = VentaRepositorio.getInstance();

	/**
	 * @param ventas the ventas to set
	 */
	public void setVentas(VentaServicePrx ventasS) {
		this.ventasService = ventasS;
	}

	public void setAlarmaService(AlarmaServicePrx a) {
		alarmaServicePrx = a;
	}

	private RecetaServicePrx recetaServicePrx;

	/**
	 * @param recetaServicePrx the recetaServicePrx to set
	 */
	public void setRecetaServicePrx(RecetaServicePrx recetaServicePrx) {
		this.recetaServicePrx = recetaServicePrx;
	}

	private Interfaz frame;
	private int codMaquina;
	private double suma;

	public void run() {

		try {
			frame = new Interfaz();
			frame.setLocationRelativeTo(null);
			frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
			frame.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}

		arrancarMaquina();
		eventos();
	}

	@Override
	public void abastecer(int codMaquina, int idAlarma, Current current) {
		// TODO Auto-generated method stub
		int cantidad = 0;
		System.out.println("Entra a abastecer");

		System.out.println(codMaquina + "-" + idAlarma + "-" + this.codMaquina);

		if (codMaquina == this.codMaquina) {

			System.out.println("Entra al primer if");

			if (idAlarma == 1) {
				// Habilita Interfaz
			}

			else if (idAlarma == 2 | idAlarma == 3) {
				// Depositos Monedas
				DepositoMonedas moneda = monedas.findByKey("100");
				moneda.setCantidad(20);
				monedas.addElement("100", moneda);

				if (idAlarma == 3) {

				}

			} else if (idAlarma == 4 | idAlarma == 5) {
				// Depositos Monedas
				DepositoMonedas moneda = monedas.findByKey("200");
				moneda.setCantidad(20);
				monedas.addElement("200", moneda);
			}

			else if (idAlarma == 6 | idAlarma == 7) {
				// Depositos Monedas
				DepositoMonedas moneda = monedas.findByKey("500");
				moneda.setCantidad(20);
				monedas.addElement("500", moneda);
			}

			else if (idAlarma == 8 | idAlarma == 12) {
				recargarIngredienteEspecifico("Agua");
			}

			else if (idAlarma == 9 | idAlarma == 13) {
				recargarIngredienteEspecifico("Cafe");
			}

			else if (idAlarma == 10 | idAlarma == 14) {
				recargarIngredienteEspecifico("Azucar");
			}

			else if (idAlarma == 11 | idAlarma == 15) {
				recargarIngredienteEspecifico("Vaso");
			}

			quitarAlarma(idAlarma + "");

			if (alarmas.getValues().isEmpty()) {
				frame.setEnabled(true);
				frame.interfazHabilitada();

				System.out.println("Entra al if de habilitacion");
			}

			// Respaldo
			respaldarMaq();
			actualizarRecetasGraf();
			actualizarInsumosGraf();
			actualizarAlarmasGraf();

			// ResetAlarmas

			// Envio a Servidor
			alarmaServicePrx.recibirNotificacionAbastesimiento(codMaquina, idAlarma + "", cantidad);
		}
	}

	public void quitarAlarma(String tipo) {
		alarmas.removeElement(tipo);
	}

	public void recargarIngredienteEspecifico(String ingrediente) {
		Ingrediente ing = ingredientes.findByKey(ingrediente);
		ing.setCantidad(ing.getMaximo());
		ingredientes.addElement(ingrediente, ing);
	}

	public void eventos() {

		frame.getBtnIngresar100().addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int saldo = Integer.parseInt(frame.getTextAreaSaldo().getText());
				frame.getTextAreaSaldo().setText((saldo + 100) + "");
				suma += 100;
				DepositoMonedas moneda = monedas.findByKey("100");
				moneda.setCantidad(moneda.getCantidad() + 1);
				monedas.addElement("100", moneda);
				actualizarInsumosGraf();

			}
		});

		frame.getBtnIngresar200().addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int saldo = Integer.parseInt(frame.getTextAreaSaldo().getText());
				frame.getTextAreaSaldo().setText((saldo + 200) + "");
				suma += 200;
				DepositoMonedas moneda = monedas.findByKey("200");
				moneda.setCantidad(moneda.getCantidad() + 1);
				monedas.addElement("200", moneda);
				actualizarInsumosGraf();

			}
		});

		frame.getBtnIngresar500().addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int saldo = Integer.parseInt(frame.getTextAreaSaldo().getText());
				frame.getTextAreaSaldo().setText((saldo + 500) + "");
				suma += 500;
				DepositoMonedas moneda = monedas.findByKey("500");
				moneda.setCantidad(moneda.getCantidad() + 1);
				monedas.addElement("500", moneda);
				actualizarInsumosGraf();

			}
		});

		frame.getBtnCancelar().addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				frame.getTextAreaSaldo().setText("0");

				if (suma > 0) {

					frame.getTextAreaDevuelta().setText(
							frame.getTextAreaDevuelta().getText()
									+ "Se devolvio: " + suma + "\n");

					devolverMonedas();

				}

			}
		});

		frame.getBtnVerificar().addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				int precio = 0;
				List<Receta> rec = recetas.getValues();
				for (int i = 0; i < rec.size(); i++) {

					if (frame
							.getComboBoxProducto()
							.getSelectedItem()
							.equals(rec.get(i)
									.getDescripcion())) {
						precio = rec.get(i).getValor();
					}

				}

				frame.getTextAreaInfo().setText(
						frame.getTextAreaInfo().getText()
								+ "El producto cuesta: " + precio + "\n");
				frame.getTextAreaInfo().repaint();
			}
		});

		frame.getBtnOrdenar().addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				int precio = 0;
				Receta temp = null;
				List<Receta> rec = recetas.getValues();
				for (int i = 0; i < rec.size(); i++) {

					temp = rec.get(i);

					if (frame.getComboBoxProducto().getSelectedItem()
							.equals(temp.getDescripcion())) {
						precio = rec.get(i).getValor();

						if (Integer.valueOf(frame.getTextAreaSaldo().getText()) >= precio) {

							frame.getTextAreaInfo().setText(
									frame.getTextAreaInfo().getText()
											+ "Se ordeno: "
											+ frame.getComboBoxProducto()
													.getSelectedItem()
											+ "\n");

							frame.getTextAreaSaldo().setText(
									Integer.valueOf(frame.getTextAreaSaldo()
											.getText()) - precio + "");

							suma -= precio;

							disminuirInsumos(temp);

							devolverMonedas();
							verificarProductos();
							// TODO: corregir el idVenta
							String idV = rec.get(i).getId();
							ventas.addElement(idV, new Venta(frame.getComboBoxProducto()
									.getSelectedItem().toString(), idV,
									precio, new Date()));

							respaldarMaq();

							frame.getTextAreaSaldo().setText("0");

						} else {
							frame.getTextAreaInfo().setText(
									frame.getTextAreaInfo().getText()
											+ "Saldo insuficiente \n");

						}

					}

				}

			}

		});

		frame.getBtnMantenimiento().addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				// Enviar Alarma por SCA

				Alarma temp = new Alarma("1", "Se requiere mantenimiento",
						new Date());

				frame.getTextAreaAlarmas().setText(
						frame.getTextAreaAlarmas().getText()
								+ "Se genero una alarma de: Mantenimiento"
								+ "\n");

				alarmaServicePrx.recibirNotificacionMalFuncionamiento(codMaquina, "Se requiere mantenimiento");

				alarmas.addElement("1", temp);

				frame.interfazDeshabilitada();

			}
		});

		frame.getBtnEnviarReporte().addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				List<Venta> vents = ventas.getValues();
				String[] arregloVentas = new String[vents.size()];
				for (int i = 0; i < arregloVentas.length; i++) {
					arregloVentas[i] = vents.get(i).getId() + "#"
							+ vents.get(i).getValor();
					System.out.println(arregloVentas[i]);
				}

				ventasService.registrarVenta(codMaquina, arregloVentas);

			}
		});

		frame.getBtnActualizar().addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				cargarRecetaMaquinas();

			}
		});
	}

	public void cargarRecetaMaquinas() {

		recetas.setElements(new HashMap<String, Receta>());

		String[] recetasServer = recetaServicePrx.consultarProductos();

		for (int i = 0; i < recetasServer.length; i++) {

			String[] splitInicial = recetasServer[i].split("#");

			String[] receta = splitInicial[0].split("-");

			HashMap<Ingrediente, Double> listaIngredientes = new HashMap<Ingrediente, Double>();

			for (int i2 = 1; i2 < splitInicial.length; i2++) {

				String[] splitdeIng = splitInicial[i2].split("-");

				Ingrediente ingred = ingredientes.findByKey(splitdeIng[1]);
				if (ingred == null) {
					ingred = new Ingrediente(splitdeIng[1], splitdeIng[2], 500, 50, 1000, 1000);
				}
				listaIngredientes.put(ingred, Double.parseDouble(splitdeIng[4]));

			}

			Receta r = new Receta(receta[1], receta[0],
					Integer.parseInt(receta[2]), listaIngredientes);

			recetas.addElement(receta[0], r);
		}

		// Actualizar Archivo Plano
		recetas.saveData();
		actualizarInsumosGraf();
		actualizarRecetasGraf();
		actualizarRecetasCombo();
	}

	public void respaldarMaq() {
		alarmas.saveData();
		ingredientes.saveData();
		monedas.saveData();
		recetas.saveData();
		ventas.saveData();
	}

	public void verificarProductos() {

		Iterator<Ingrediente> itIng = ingredientes.getValues().iterator();

		while (itIng.hasNext()) {
			Ingrediente ing = itIng.next();

			if (ing.getCantidad() <= ing.getMinimo()
					&& ing.getCantidad() > ing.getCritico()) {

				Alarma alIng = new Alarma(ing.getCodAlarma(),
						ing.getNombre(), new Date());

				if (alarmas.findByKey(ing.getCodAlarma()) == null) {

					alarmas.addElement(ing.getCodAlarma(), alIng);

					// Enviar SCA

					alarmaServicePrx.recibirNotificacionEscasezIngredientes(ing.getNombre(), codMaquina);

					frame.getTextAreaAlarmas().setText(
							frame.getTextAreaAlarmas().getText()
									+ "Se genero una alarma de Ingrediente: "
									+ alIng.getMensaje() + "\n");

				}
			}

			if (ing.getCantidad() <= ing.getCritico()) {

				int codAlarma = Integer.parseInt(ing.getCodAlarma()) + 4;

				Alarma alIng = new Alarma(codAlarma + "", ing.getNombre(), new Date());

				alarmas.addElement(codAlarma + "", alIng);

				// Enviar SCA

				alarmaServicePrx.recibirNotificacionEscasezIngredientes(ing.getNombre(), codMaquina);

				frame.getTextAreaAlarmas().setText(
						frame.getTextAreaAlarmas().getText()
								+ "Se genero una alarma de: Critico de "
								+ alIng.getMensaje() + "\n");

				frame.interfazDeshabilitada();
			}

		}
	}

	public void disminuirInsumos(Receta r) {
		Iterator<Entry<Ingrediente, Double>> receta = r.getListaIngredientes()
				.entrySet().iterator();
		while (receta.hasNext()) {
			Map.Entry<Ingrediente, Double> ingRec = (Map.Entry<Ingrediente, Double>) receta.next();
			Ingrediente ingrediente = ingredientes.findByKey(ingRec.getKey().getNombre());
			ingrediente.setCantidad(ingrediente.getCantidad() - ingRec.getValue());
			ingredientes.addElement(ingrediente.getNombre(), ingrediente);
		}
		// Modificar XML
		actualizarInsumosGraf();
	}

	public void arrancarMaquina() {
		codMaquina = quemarCodMaquina();
		// Interfaz
		actualizarRecetasCombo();
		actualizarRecetasGraf();
		actualizarInsumosGraf();
		actualizarAlarmasGraf();

	}

	public void actualizarAlarmasGraf() {

		frame.getTextAreaAlarmas().setText("");

	}

	public void actualizarInsumosGraf() {

		frame.getTextAreaInsumos().setText("");

		// LLenar Insumos
		Iterator<Ingrediente> it = ingredientes.getValues().iterator();
		while (it.hasNext()) {
			Ingrediente ing = it.next();

			frame.getTextAreaInsumos().setText(
					frame.getTextAreaInsumos().getText()
							+ ing.getNombre() + ": "
							+ ing.getCantidad() + "\n");

		}
		DepositoMonedas dep = monedas.findByKey("100");
		frame.getTextAreaInsumos().setText(
				frame.getTextAreaInsumos().getText() + "Deposito "
						+ dep.getTipo() + ": "
						+ dep.getCantidad() + "\n");
		dep = monedas.findByKey("200");
		frame.getTextAreaInsumos().setText(
				frame.getTextAreaInsumos().getText() + "Deposito "
						+ dep.getTipo() + ": "
						+ dep.getCantidad() + "\n");
		dep = monedas.findByKey("500");
		frame.getTextAreaInsumos().setText(
				frame.getTextAreaInsumos().getText() + "Deposito "
						+ dep.getTipo() + ": "
						+ dep.getCantidad() + "\n");

	}

	public void actualizarRecetasGraf() {

		frame.getTextAreaRecetas().setText("");

		// Llenar Recetas

		Iterator<Receta> it2 = recetas.getValues().iterator();
		while (it2.hasNext()) {

			Receta temp = it2.next();
			frame.getTextAreaRecetas().setText(
					frame.getTextAreaRecetas().getText()
							+ temp.getDescripcion() + "\n");

		}

	}

	public void actualizarRecetasCombo() {

		// Reestablece Combobox
		frame.getComboBoxProducto().removeAllItems();

		// LLenar Combo
		List<Receta> rec = recetas.getValues();
		for (int i = 0; i < rec.size(); i++) {

			frame.getComboBoxProducto().addItem(
					rec.get(i).getDescripcion());
		}
	}

	private int quemarCodMaquina() {
		int retorno = -2;

		FileInputStream fstream;
		try {
			String path = "codMaquina.cafe";
			File file = new File(path);

			fstream = new FileInputStream(file);

			DataInputStream entrada = new DataInputStream(fstream);

			BufferedReader buffer = new BufferedReader(new InputStreamReader(
					entrada));

			retorno = Integer.parseInt(buffer.readLine());

			entrada.close();

		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		System.out.println(retorno + " RETORRRNO");

		return retorno;
	}

	public void devolverMonedas() {
		// Metodo para devolver monedas
		int monedas100 = 0;
		int monedas200 = 0;
		int monedas500 = 0;
		if (suma / 500 > 0) {
			monedas500 += (int) suma / 500;
			DepositoMonedas moneda = monedas.findByKey("500");
			moneda.setCantidad(moneda.getCantidad() - monedas500);
			monedas.addElement("500", moneda);
			suma -= 500 * ((int) suma / 500);

		}

		if (suma / 200 > 0) {

			monedas200 += (int) suma / 200;
			DepositoMonedas moneda = monedas.findByKey("200");
			moneda.setCantidad(moneda.getCantidad() - monedas200);
			monedas.addElement("200", moneda);
			suma -= 200 * ((int) suma / 200);

		}
		if (suma / 100 > 0) {
			monedas100 += (int) suma / 100;
			DepositoMonedas moneda = monedas.findByKey("100");
			moneda.setCantidad(moneda.getCantidad() - monedas100);
			monedas.addElement("100", moneda);
			suma -= 100 * ((int) suma / 100);
		}
		if (suma != 0) {
			System.out.println("Ocurrio un error en dar devueltas: " + suma);
		}

		frame.getTextAreaDevuelta().setText(
				frame.getTextAreaDevuelta().getText() + "Se devolvieron: "
						+ monedas500 + " monedas de 500, " + monedas200
						+ " monedas de 200 y " + monedas100
						+ " monedas de 100 \n");

		actualizarInsumosGraf();
		verificarMonedas();

	}

	public void verificarMonedas() {

		// Alarma (Generada por Uso)
		DepositoMonedas moneda = monedas.findByKey("100");
		if (moneda.getCantidad() <= moneda.getMinimo()
				&& moneda.getCantidad() > moneda.getCritico()) {

			Alarma alMon = new Alarma("2", "Faltan monedas de 100", new Date());

			if (alarmas.findByKey("2") == null) {
				alarmas.addElement("2", alMon);

				alarmaServicePrx.recibirNotificacionInsuficienciaMoneda(Moneda.CIEN, codMaquina);
				frame.getTextAreaAlarmas().setText(
						frame.getTextAreaAlarmas().getText()
								+ "Se genero una alarma de: Monedas de 100"
								+ "\n");

			}
		}

		if (moneda.getCantidad() <= moneda.getCritico()) {

			Alarma alMon = new Alarma("3",
					"ESTADO CRITICO: Faltan monedas de 100", new Date());
			alarmas.addElement("3", alMon);

			// Enviar SCA
			alarmaServicePrx.recibirNotificacionInsuficienciaMoneda(Moneda.CIEN, codMaquina);

			frame.getTextAreaAlarmas().setText(
					frame.getTextAreaAlarmas().getText()
							+ "Se genero una alarma de: Critica Monedas de 100"
							+ "\n");

			frame.interfazDeshabilitada();

		}
		moneda = monedas.findByKey("200");
		if (moneda.getCantidad() <= moneda.getMinimo()
				&& moneda.getCantidad() > moneda.getCritico()) {

			Alarma alMon = new Alarma("4", "Faltan monedas de 200", new Date());

			if (alarmas.findByKey("4") == null) {
				alarmas.addElement("4", alMon);

				// Enviar SCA

				alarmaServicePrx.recibirNotificacionInsuficienciaMoneda(Moneda.DOCIENTOS, codMaquina);

				frame.getTextAreaAlarmas().setText(
						frame.getTextAreaAlarmas().getText()
								+ "Se genero una alarma de: Mondedas de 200"
								+ "\n");

			}
		}

		if (moneda.getCantidad() <= moneda.getCritico()) {

			Alarma alMon = new Alarma("5",
					"ESTADO CRITICO: Faltan monedas de 200", new Date());
			alarmas.addElement("5", alMon);

			// Enviar SCA

			alarmaServicePrx.recibirNotificacionInsuficienciaMoneda(Moneda.DOCIENTOS, codMaquina);

			frame.getTextAreaAlarmas()
					.setText(
							frame.getTextAreaAlarmas().getText()
									+ "Se genero una alarma de: Critica de Monedas de 200"
									+ "\n");

			frame.interfazDeshabilitada();

		}
		moneda = monedas.findByKey("500");

		if (moneda.getCantidad() <= moneda.getMinimo()
				&& moneda.getCantidad() > moneda.getCritico()) {

			Alarma alMon = new Alarma("6", "Faltan monedas de 500", new Date());
			if (alarmas.findByKey("6") == null) {
				alarmas.addElement("6", alMon);

				// Enviar SCA

				alarmaServicePrx.recibirNotificacionInsuficienciaMoneda(Moneda.QUINIENTOS, codMaquina);

				frame.getTextAreaAlarmas().setText(
						frame.getTextAreaAlarmas().getText()
								+ "Se genero una alarma de: Monedas de 500"
								+ "\n");

			}
		}
		if (moneda.getCantidad() <= moneda.getCritico()) {

			Alarma alMon = new Alarma("7",
					"ESTADO CRITICO: Faltan monedas de 500", new Date());
			alarmas.addElement("7", alMon);

			alarmaServicePrx.recibirNotificacionInsuficienciaMoneda(Moneda.QUINIENTOS, codMaquina);

			frame.getTextAreaAlarmas().setText(
					frame.getTextAreaAlarmas().getText()
							+ "Se genero una alarma de: Critica Monedas de 500"
							+ "\n");

			frame.interfazDeshabilitada();

		}

	}

}
