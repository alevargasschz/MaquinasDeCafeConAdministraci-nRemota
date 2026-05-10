package interfaz;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.JFrame;

import modelo.receta.ProductoReceta;
import servicios.*;

public class ControladorRecetas implements Runnable {

	private ProductoReceta recetaService;

	/**
	 * @param recetaService the recetaService to set
	 */
	public void setRecetaService(ProductoReceta recetaService) {
		this.recetaService = recetaService;
	}

	private InterfazRecetas iR;

	ArrayList<String> listaIng = new ArrayList<String>();

	ArrayList<String> listaReceta = new ArrayList<String>();

	ArrayList<String> listaAsociada = new ArrayList<String>();

	@Override
	public void run() {

		try {
			iR = new InterfazRecetas();
			iR.setLocationRelativeTo(null);
			iR.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
			iR.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
		actualizarVista();
		eventos();

	}

	public void actualizarVista() {

		iR.getTextAreaRecetas().setText("");
		iR.getTextAreaIngredientes().setText("");

		String[] listadoRecetas = recetaService.consultarRecetas(null);

		for (int i = 0; i < listadoRecetas.length; i++) {

			iR.getTextAreaRecetas().setText(
					iR.getTextAreaRecetas().getText() + listadoRecetas[i]
							+ "\n");
		}

		String[] listadoIngredientes = recetaService.consultarIngredientes(null);

		for (int i = 0; i < listadoIngredientes.length; i++) {

			iR.getTextAreaIngredientes().setText(
					iR.getTextAreaIngredientes().getText()
							+ listadoIngredientes[i] + "\n");
		}

	}

	public void eventos() {

		iR.getBtnAgregarReceta().addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				if (!iR.getTextFieldNombreRec().getText().isEmpty()
						&& !iR.getTextFieldPrecioRec().getText().isEmpty()) {

					String listadoRec = recetaService.registrarReceta(iR.getTextFieldNombreRec()
							.getText(),
							Integer.parseInt(iR
									.getTextFieldPrecioRec().getText()),
							null);

					listaReceta.add(listadoRec);

					actualizarVista();
				}

				iR.getTextFieldNombreRec().setText("");
				iR.getTextFieldPrecioRec().setText("");

			}
		});

		iR.getBtnBorrarReceta().addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				if (!iR.getTextFieldNombreRec().getText().isEmpty()) {

					recetaService.borrarReceta(Integer.parseInt(iR
							.getTextFieldNombreRec().getText()), null);

					actualizarVista();
				}

				iR.getTextFieldNombreRec().setText("");

			}
		});

		iR.getBtnRIC().addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				if (!iR.getTextFieldAsociacion().getText().isEmpty()) {

					String cadenaA = iR.getTextFieldAsociacion().getText();

					String[] split = cadenaA.split("-");

					int idReceta = Integer.parseInt(split[0]);
					int idIngrediente = Integer.parseInt(split[1]);
					int valor = Integer.parseInt(split[2]);

					recetaService.definirRecetaIngrediente(
							idReceta, idIngrediente, valor, null);

					asociar(idReceta, idIngrediente, valor);

				}

				iR.getTextFieldAsociacion().setText("");
			}
		});

	}

	public void asociar(int idReceta, int idIngrediente, int valor) {

		String concat = "";

		for (int i = 0; i < listaReceta.size(); i++) {

			String[] splitReceta = listaReceta.get(i).split("-");

			if (Integer.parseInt(splitReceta[0]) == idReceta) {

				concat += splitReceta[0] + "-" + splitReceta[1] + "-"
						+ splitReceta[2];

				for (int i2 = 0; i2 < listaIng.size(); i2++) {

					String[] splitIngrediente = listaIng.get(i2).split("-");

					if (Integer.parseInt(splitIngrediente[0]) == idIngrediente) {

						concat += "#";

						concat += splitIngrediente[0] + "-"
								+ splitIngrediente[1] + "-"
								+ splitIngrediente[2] + "-"
								+ splitIngrediente[3];

						listaAsociada.add(concat);
					}

				}

			}

		}

	}

}
