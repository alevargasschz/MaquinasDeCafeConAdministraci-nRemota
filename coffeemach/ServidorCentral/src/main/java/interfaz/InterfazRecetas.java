package interfaz;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.border.EmptyBorder;

public class InterfazRecetas extends JFrame {

	private JPanel contentPane;
	private JTextField textFieldNombreRec, textFieldPrecioRec, textFieldAsociacion;
	private JButton btnAgregarReceta, btnBorrarReceta, btnRIC;
	private JTextArea textAreaRecetas, textAreaIngredientes;

	public JTextArea getTextAreaRecetas() {
		return textAreaRecetas;
	}

	public JTextArea getTextAreaIngredientes() {
		return textAreaIngredientes;
	}

	/**
	 * Create the frame.
	 */
	public InterfazRecetas() {
		setTitle("Interfaz Recetas");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 404, 435);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);

		JLabel lblNewLabel = new JLabel("Recetas");
		lblNewLabel.setBounds(198, 11, 178, 14);
		contentPane.add(lblNewLabel);

		JPanel panel = new JPanel();
		panel.setBounds(198, 28, 178, 145);
		contentPane.add(panel);
		panel.setLayout(null);

		JScrollPane scrollPane = new JScrollPane();
		scrollPane
				.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		scrollPane.setBounds(10, 11, 157, 123);
		panel.add(scrollPane);

		textAreaRecetas = new JTextArea();
		scrollPane.setViewportView(textAreaRecetas);

		JLabel lblNombre = new JLabel("Nombre");
		lblNombre.setBounds(10, 185, 86, 14);
		contentPane.add(lblNombre);

		textFieldNombreRec = new JTextField();
		textFieldNombreRec.setBounds(127, 184, 249, 20);
		contentPane.add(textFieldNombreRec);
		textFieldNombreRec.setColumns(10);

		JLabel lblPrecio = new JLabel("Precio");
		lblPrecio.setBounds(10, 210, 86, 14);
		contentPane.add(lblPrecio);

		JLabel lblIngrediente_1 = new JLabel("Ingredientes");
		lblIngrediente_1.setBounds(10, 11, 178, 14);
		contentPane.add(lblIngrediente_1);

		JPanel panel_1 = new JPanel();
		panel_1.setBounds(10, 28, 178, 145);
		contentPane.add(panel_1);
		panel_1.setLayout(null);

		JScrollPane scrollPane_1 = new JScrollPane();
		scrollPane_1
				.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		scrollPane_1.setBounds(10, 11, 158, 123);
		panel_1.add(scrollPane_1);

		textAreaIngredientes = new JTextArea();
		scrollPane_1.setViewportView(textAreaIngredientes);

		textFieldPrecioRec = new JTextField();
		textFieldPrecioRec.setBounds(127, 207, 249, 20);
		contentPane.add(textFieldPrecioRec);
		textFieldPrecioRec.setColumns(10);

		JLabel lblAsociarRecetaingrediente = new JLabel(
				"Asociar Receta-Ingrediente-Cantidad");
		lblAsociarRecetaingrediente.setBounds(10, 304, 366, 14);
		contentPane.add(lblAsociarRecetaingrediente);

		textFieldAsociacion = new JTextField();
		textFieldAsociacion.setBounds(10, 329, 366, 20);
		contentPane.add(textFieldAsociacion);
		textFieldAsociacion.setColumns(10);

		btnAgregarReceta = new JButton("Agregar Receta");
		btnAgregarReceta.setBounds(10, 236, 366, 23);
		contentPane.add(btnAgregarReceta);

		btnBorrarReceta = new JButton("Borrar Receta");
		btnBorrarReceta.setBounds(10, 270, 366, 23);
		contentPane.add(btnBorrarReceta);

		btnRIC = new JButton("Asociar R-I-C");
		btnRIC.setBounds(10, 360, 366, 23);
		contentPane.add(btnRIC);
	}

	public JTextField getTextFieldNombreRec() {
		return textFieldNombreRec;
	}

	public JTextField getTextFieldPrecioRec() {
		return textFieldPrecioRec;
	}

	public JTextField getTextFieldAsociacion() {
		return textFieldAsociacion;
	}

	public JButton getBtnAgregarReceta() {
		return btnAgregarReceta;
	}

	public JButton getBtnBorrarReceta() {
		return btnBorrarReceta;
	}

	public JButton getBtnRIC() {
		return btnRIC;
	}

}
