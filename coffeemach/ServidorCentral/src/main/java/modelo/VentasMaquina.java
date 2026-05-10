package modelo;

import java.util.Date;
import java.util.List;

public class VentasMaquina {

	private int consecutivo;
	private int idMaquina;
	private Date fechaInicial;
	private Date fechaFinal;
	private List<VentasReceta> detalle;
	private double valor;
	
	public VentasMaquina() {
		super();
	}

	public VentasMaquina(int consecutivo, int idMaquina, Date fechaInicial,
			Date fechaFinal, List<VentasReceta>detalle, double valor) {
		super();
		this.consecutivo = consecutivo;
		this.idMaquina = idMaquina;
		this.fechaInicial = fechaInicial;
		this.fechaFinal = fechaFinal;
		this.detalle=detalle;
		this.valor=valor;
	}

	public int getConsecutivo() {
		return consecutivo;
	}

	public void setConsecutivo(int consecutivo) {
		this.consecutivo = consecutivo;
	}

	public int getIdMaquina() {
		return idMaquina;
	}

	public void setIdMaquina(int idMaquina) {
		this.idMaquina = idMaquina;
	}

	public Date getFechaInicial() {
		return fechaInicial;
	}

	public void setFechaInicial(Date fechaInicial) {
		this.fechaInicial = fechaInicial;
	}

	public Date getFechaFinal() {
		return fechaFinal;
	}

	public void setFechaFinal(Date fechaFinal) {
		this.fechaFinal = fechaFinal;
	}

	public List<VentasReceta> getDetalle() {
		return detalle;
	}

	public void setDetalle(List<VentasReceta> detalle) {
		this.detalle = detalle;
	}

	public double getValor() {
		return valor;
	}

	public void setValor(double valor) {
		this.valor = valor;
	}
	
	
	
}
