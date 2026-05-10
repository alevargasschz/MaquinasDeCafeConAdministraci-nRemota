package interfazUsuario;

import java.awt.Color;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;
import javax.swing.JButton;
import javax.swing.border.BevelBorder;
import javax.swing.JComboBox;
import javax.swing.JTextArea;

public class Interfaz extends JFrame {

	private JButton btnIngresar100, btnIngresar200, btnIngresar500, btnOrdenar,
			btnMantenimiento, btnDevolver, btnVerificar, btnEnviarReporte, btnActualizar;
	private JTextArea textAreaSaldo, textAreaInfo, textAreaAlarmas,
			textAreaInsumos, textAreaRecetas, textAreaDevuelta;
	private JComboBox comboBoxProducto;

	/**
	 * Create the frame
	 */
	public Interfaz() {

		setTitle("Maquina de Cafe");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 900, 430);
		setLocationRelativeTo(null);

		JPanel contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);

		JLabel lblMaquinaDeCafe = new JLabel("Maquina de Cafe");
		lblMaquinaDeCafe.setHorizontalAlignment(SwingConstants.CENTER);
		lblMaquinaDeCafe.setBounds(5, 5, 564, 14);
		contentPane.add(lblMaquinaDeCafe);
		lblMaquinaDeCafe.setVisible(true);

		JPanel panel = new JPanel();
		panel.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null,
				null));
		panel.setBounds(15, 30, 393, 145);
		contentPane.add(panel);
		panel.setLayout(null);
		panel.setVisible(true);

		JLabel lblMonedas = new JLabel("Monedas");
		lblMonedas.setHorizontalAlignment(SwingConstants.CENTER);
		lblMonedas.setBounds(10, 11, 118, 14);
		panel.add(lblMonedas);

		btnIngresar100 = new JButton("Ingresar 100");
		btnIngresar100.setBounds(10, 36, 118, 23);
		panel.add(btnIngresar100);

		btnIngresar200 = new JButton("Ingresar 200");
		btnIngresar200.setBounds(10, 63, 118, 23);
		panel.add(btnIngresar200);

		btnIngresar500 = new JButton("Ingresar 500");
		btnIngresar500.setBounds(10, 87, 118, 23);
		panel.add(btnIngresar500);

		btnDevolver = new JButton("Devolver");
		btnDevolver.setBounds(10, 111, 118, 23);
		panel.add(btnDevolver);

		JLabel lblInformacion = new JLabel("Informacion");
		lblInformacion.setHorizontalAlignment(SwingConstants.CENTER);
		lblInformacion.setBounds(138, 11, 245, 14);
		panel.add(lblInformacion);

		textAreaSaldo = new JTextArea("0");
		textAreaSaldo.setEditable(false);
		textAreaSaldo.setBounds(138, 35, 245, 24);
		panel.add(textAreaSaldo);

		JLabel lblEventos = new JLabel("Eventos");
		lblEventos.setHorizontalAlignment(SwingConstants.CENTER);
		lblEventos.setBounds(138, 67, 245, 14);
		panel.add(lblEventos);

		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(138, 85, 245, 49);
		panel.add(scrollPane);

		textAreaDevuelta = new JTextArea();
		scrollPane.setViewportView(textAreaDevuelta);
		textAreaDevuelta.setEditable(false);

		JPanel panel_1 = new JPanel();
		panel_1.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null,
				null, null));
		panel_1.setBounds(418, 28, 456, 147);
		contentPane.add(panel_1);
		panel_1.setLayout(null);

		/*
		 * JLabel lblAlarmas = new JLabel("Alarmas");
		 * lblAlarmas.setHorizontalAlignment(SwingConstants.CENTER);
		 * lblAlarmas.setBounds(10, 11, 172, 14); panel_1.add(lblAlarmas);
		 */

		btnMantenimiento = new JButton("Mantenimiento");
		btnMantenimiento.setBounds(10, 7, 139, 23);
		panel_1.add(btnMantenimiento);

		btnEnviarReporte = new JButton("Reporte Ventas");
		btnEnviarReporte.setBounds(148, 7, 146, 23);
		panel_1.add(btnEnviarReporte);

		JScrollPane scrollPane_3 = new JScrollPane();
		scrollPane_3.setBounds(10, 36, 436, 100);
		panel_1.add(scrollPane_3);

		textAreaAlarmas = new JTextArea();
		scrollPane_3.setViewportView(textAreaAlarmas);
		textAreaAlarmas.setEditable(false);
		
		btnActualizar = new JButton("Actualizar Rec");
		btnActualizar.setBounds(293, 7, 153, 23);
		panel_1.add(btnActualizar);

		JPanel panel_2 = new JPanel();
		panel_2.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null,
				null, null));
		panel_2.setBounds(15, 197, 482, 184);
		contentPane.add(panel_2);
		panel_2.setLayout(null);

		JLabel lblProductos = new JLabel("Productos");
		lblProductos.setHorizontalAlignment(SwingConstants.CENTER);
		lblProductos.setBounds(10, 11, 132, 14);
		panel_2.add(lblProductos);

		comboBoxProducto = new JComboBox();
		comboBoxProducto.setBounds(10, 33, 221, 20);
		panel_2.add(comboBoxProducto);

		btnOrdenar = new JButton("Ordenar");
		btnOrdenar.setBounds(10, 126, 221, 34);
		panel_2.add(btnOrdenar);

		JLabel lblInformacion_1 = new JLabel("Informacion");
		lblInformacion_1.setHorizontalAlignment(SwingConstants.CENTER);
		lblInformacion_1.setBounds(241, 11, 231, 14);
		panel_2.add(lblInformacion_1);

		btnVerificar = new JButton("Verificar Precio");
		btnVerificar.setBounds(10, 74, 221, 34);
		panel_2.add(btnVerificar);

		JScrollPane scrollPane_4 = new JScrollPane();
		scrollPane_4.setBounds(241, 33, 231, 140);
		panel_2.add(scrollPane_4);

		textAreaInfo = new JTextArea();
		scrollPane_4.setViewportView(textAreaInfo);
		textAreaInfo.setEditable(false);

		JPanel panel_3 = new JPanel();
		panel_3.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null,
				null, null));
		panel_3.setBounds(507, 197, 367, 184);
		contentPane.add(panel_3);
		panel_3.setLayout(null);

		JLabel lblInsumosDisponibles = new JLabel("Insumos Disponibles");
		lblInsumosDisponibles.setHorizontalAlignment(SwingConstants.CENTER);
		lblInsumosDisponibles.setBounds(20, 11, 155, 14);
		panel_3.add(lblInsumosDisponibles);

		JLabel lblRecetasDisponibles = new JLabel("Recetas Disponibles");
		lblRecetasDisponibles.setHorizontalAlignment(SwingConstants.CENTER);
		lblRecetasDisponibles.setBounds(199, 11, 158, 14);
		panel_3.add(lblRecetasDisponibles);

		JScrollPane scrollPane_1 = new JScrollPane();
		scrollPane_1.setBounds(199, 35, 158, 138);
		panel_3.add(scrollPane_1);

		textAreaRecetas = new JTextArea();
		scrollPane_1.setViewportView(textAreaRecetas);

		JScrollPane scrollPane_2 = new JScrollPane();
		scrollPane_2.setBounds(20, 36, 155, 137);
		panel_3.add(scrollPane_2);

		textAreaInsumos = new JTextArea();
		scrollPane_2.setViewportView(textAreaInsumos);
		textAreaInsumos.setEditable(false);
	}

	public void interfazDeshabilitada() {

		this.setBackground(Color.BLACK);

		btnIngresar100.setBackground(Color.BLACK);
		btnIngresar200.setBackground(Color.BLACK);
		btnIngresar500.setBackground(Color.BLACK);
		btnOrdenar.setBackground(Color.BLACK);
		btnMantenimiento.setBackground(Color.BLACK);
		btnDevolver.setBackground(Color.BLACK);
		btnVerificar.setBackground(Color.BLACK);
		btnEnviarReporte.setBackground(Color.BLACK);
		textAreaSaldo.setBackground(Color.BLACK);
		textAreaInfo.setBackground(Color.BLACK);
		textAreaAlarmas.setBackground(Color.BLACK);
		textAreaInsumos.setBackground(Color.BLACK);
		textAreaRecetas.setBackground(Color.BLACK);
		textAreaDevuelta.setBackground(Color.BLACK);
		comboBoxProducto.setBackground(Color.BLACK);
		btnActualizar.setBackground(Color.BLACK);

		this.setEnabled(false);
	}

	public void interfazHabilitada() {

		this.setBackground(Color.WHITE);

		btnIngresar100.setBackground(Color.WHITE);
		btnIngresar200.setBackground(Color.WHITE);
		btnIngresar500.setBackground(Color.WHITE);
		btnOrdenar.setBackground(Color.WHITE);
		btnMantenimiento.setBackground(Color.WHITE);
		btnDevolver.setBackground(Color.WHITE);
		btnVerificar.setBackground(Color.WHITE);
		btnEnviarReporte.setBackground(Color.WHITE);
		textAreaSaldo.setBackground(Color.WHITE);
		textAreaInfo.setBackground(Color.WHITE);
		textAreaAlarmas.setBackground(Color.WHITE);
		textAreaInsumos.setBackground(Color.WHITE);
		textAreaRecetas.setBackground(Color.WHITE);
		textAreaDevuelta.setBackground(Color.WHITE);
		comboBoxProducto.setBackground(Color.WHITE);
		this.setEnabled(true);
	}

	public JButton getBtnIngresar100() {
		return btnIngresar100;
	}

	public void setBtnIngresar100(JButton btnIngresar100) {
		this.btnIngresar100 = btnIngresar100;
	}

	public JButton getBtnIngresar200() {
		return btnIngresar200;
	}

	public void setBtnIngresar200(JButton btnIngresar200) {
		this.btnIngresar200 = btnIngresar200;
	}

	public JButton getBtnIngresar500() {
		return btnIngresar500;
	}

	public void setBtnIngresar500(JButton btnIngresar500) {
		this.btnIngresar500 = btnIngresar500;
	}

	public JButton getBtnOrdenar() {
		return btnOrdenar;
	}

	public void setBtnOrdenar(JButton btnOrdenar) {
		this.btnOrdenar = btnOrdenar;
	}

	public JButton getBtnMantenimiento() {
		return btnMantenimiento;
	}

	public void setBtnMantenimiento(JButton btnMantenimiento) {
		this.btnMantenimiento = btnMantenimiento;
	}

	public JTextArea getTextAreaSaldo() {
		return textAreaSaldo;
	}

	public void setTextAreaSaldo(JTextArea textAreaSaldo) {
		this.textAreaSaldo = textAreaSaldo;
	}

	public JTextArea getTextAreaInfo() {
		return textAreaInfo;
	}

	public void setTextAreaInfo(JTextArea textAreaInfo) {
		this.textAreaInfo = textAreaInfo;
	}

	public JTextArea getTextAreaAlarmas() {
		return textAreaAlarmas;
	}

	public void setTextAreaAlarmas(JTextArea textAreaAlarmas) {
		this.textAreaAlarmas = textAreaAlarmas;
	}

	public JTextArea getTextAreaInsumos() {
		return textAreaInsumos;
	}

	public void setTextAreaInsumos(JTextArea textAreaInsumos) {
		this.textAreaInsumos = textAreaInsumos;
	}

	public JTextArea getTextAreaRecetas() {
		return textAreaRecetas;
	}

	public void setTextAreaRecetas(JTextArea textAreaRecetas) {
		this.textAreaRecetas = textAreaRecetas;
	}

	public JComboBox getComboBoxProducto() {
		return comboBoxProducto;
	}

	public void setComboBoxProducto(JComboBox comboBoxProducto) {
		this.comboBoxProducto = comboBoxProducto;
	}

	public JButton getBtnCancelar() {
		return btnDevolver;
	}

	public void setBtnCancelar(JButton btnCancelar) {
		this.btnDevolver = btnCancelar;
	}

	public JTextArea getTextAreaDevuelta() {
		return textAreaDevuelta;
	}

	public void setTextAreaDevuelta(JTextArea textAreaDevuelta) {
		this.textAreaDevuelta = textAreaDevuelta;
	}

	public JButton getBtnVerificar() {
		return btnVerificar;
	}

	public void setBtnVerificar(JButton btnVerificar) {
		this.btnVerificar = btnVerificar;
	}

	public JButton getBtnDevolver() {
		return btnDevolver;
	}

	public void setBtnDevolver(JButton btnDevolver) {
		this.btnDevolver = btnDevolver;
	}

	public JButton getBtnEnviarReporte() {
		return btnEnviarReporte;
	}

	public void setBtnEnviarReporte(JButton btnEnviarReporte) {
		this.btnEnviarReporte = btnEnviarReporte;
	}

	public JButton getBtnActualizar() {
		return btnActualizar;
	}
	
	
}
