package monedero;

import java.io.Serializable;

public class DepositoMonedas implements Serializable {

	private String tipo;
	private int cantidad, minimo, critico, recarga;

	public DepositoMonedas(String tipo, int cantidad, int minimo, int critico, int recarga) {
		super();
		this.tipo = tipo;
		this.cantidad = cantidad;
		this.minimo = minimo;
		this.critico = critico;
		this.recarga = recarga;
	}

	public int getRecarga() {
		return recarga;
	}

	public void setRecarga(int recarga) {
		this.recarga = recarga;
	}

	public int getMinimo() {
		return minimo;
	}

	public void setMinimo(int minimo) {
		this.minimo = minimo;
	}

	public int getCritico() {
		return critico;
	}

	public void setCritico(int critico) {
		this.critico = critico;
	}

	public String getTipo() {
		return tipo;
	}

	public void setTipo(String tipo) {
		this.tipo = tipo;
	}

	public int getCantidad() {
		return cantidad;
	}

	public void setCantidad(int cantidad) {
		this.cantidad = cantidad;
	}

}
