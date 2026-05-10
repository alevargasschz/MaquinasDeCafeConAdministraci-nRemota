package modelo;

public class RecetaIngrediente {

	private Receta receta;
	private Ingrediente ingrediente;
	private int cantidad;
	
	public RecetaIngrediente() {
		super();
	}

	public RecetaIngrediente(Receta receta, Ingrediente ingrediente,
			int cantidad) {
		super();
		this.receta = receta;
		this.ingrediente = ingrediente;
		this.cantidad = cantidad;
	}

	public Receta getReceta() {
		return receta;
	}

	public void setReceta(Receta receta) {
		this.receta = receta;
	}

	public Ingrediente getIngrediente() {
		return ingrediente;
	}

	public void setIngrediente(Ingrediente ingrediente) {
		this.ingrediente = ingrediente;
	}

	public int getCantidad() {
		return cantidad;
	}

	public void setCantidad(int cantidad) {
		this.cantidad = cantidad;
	}
	
	
	
}
