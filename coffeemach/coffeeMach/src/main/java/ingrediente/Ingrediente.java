package ingrediente;

import java.io.Serializable;

public class Ingrediente implements Serializable {

	private String nombre, codAlarma;
	private double minimo, critico, maximo;
	private double cantidad;

	public Ingrediente(String nombre, String codAlarma, double minimo,
			double critico, double maximo, double cantidad) {
		super();
		this.nombre = nombre;
		this.codAlarma = codAlarma;
		this.minimo = minimo;
		this.critico = critico;
		this.maximo = maximo;
		this.cantidad = cantidad;
	}

	public double getCantidad() {
		return cantidad;
	}

	public void setCantidad(double cantidad) {
		this.cantidad = cantidad;
	}

	public String getCodAlarma() {
		return codAlarma;
	}

	public void setCodAlarma(String codAlarma) {
		this.codAlarma = codAlarma;
	}

	public double getMaximo() {
		return maximo;
	}

	public void setMaximo(double maximo) {
		this.maximo = maximo;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public double getMinimo() {
		return minimo;
	}

	public void setMinimo(double minimo) {
		this.minimo = minimo;
	}

	public double getCritico() {
		return critico;
	}

	public void setCritico(double critico) {
		this.critico = critico;
	}

}
