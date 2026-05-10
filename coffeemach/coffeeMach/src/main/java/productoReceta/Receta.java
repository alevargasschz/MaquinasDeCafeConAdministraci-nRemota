package productoReceta;

import java.io.Serializable;
import java.util.HashMap;

import ingrediente.Ingrediente;

public class Receta implements Serializable {

	private String descripcion, id;
	private int valor;
	private HashMap<Ingrediente, Double> listaIngredientes;

	public Receta(String descripcion, String id, int valor,
			HashMap<Ingrediente, Double> listaIngredientes) {
		super();
		this.descripcion = descripcion;
		this.id = id;
		this.valor = valor;
		this.listaIngredientes = listaIngredientes;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	public int getValor() {
		return valor;
	}

	public void setValor(int valor) {
		this.valor = valor;
	}

	public HashMap<Ingrediente, Double> getListaIngredientes() {
		return listaIngredientes;
	}

	public void setListaIngredientes(
			HashMap<Ingrediente, Double> listaIngredientes) {
		this.listaIngredientes = listaIngredientes;

	}

}
