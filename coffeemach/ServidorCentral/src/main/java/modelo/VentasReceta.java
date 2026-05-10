package modelo;

public class VentasReceta {

	private int idReceta;
	private int consecutivoVenta;
	private double valorreceta;
	
	public VentasReceta() {
		super();
	}

	public VentasReceta(int idReceta, int consecutivoVenta, double valorreceta) {
		super();
		this.idReceta = idReceta;
		this.consecutivoVenta = consecutivoVenta;
	}

	public int getIdReceta() {
		return idReceta;
	}

	public void setIdReceta(int idReceta) {
		this.idReceta = idReceta;
	}

	public int getConsecutivoVenta() {
		return consecutivoVenta;
	}

	public void setConsecutivoVenta(int consecutivoVenta) {
		this.consecutivoVenta = consecutivoVenta;
	}

	public double getValorreceta() {
		return valorreceta;
	}

	public void setValorreceta(double valorreceta) {
		this.valorreceta = valorreceta;
	}
	
	
	
}
