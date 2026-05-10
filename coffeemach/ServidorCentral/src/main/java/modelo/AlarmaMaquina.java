package modelo;

import java.util.Date;

public class AlarmaMaquina {

	private int IdAlarma;
	private int IdMaquina;
	private Date fechaInicialAlarma;
	
	public AlarmaMaquina(int idAlarma, int idMaquina,
			Date fechaInicialAlarma) {
		super();
		IdAlarma = idAlarma;
		IdMaquina = idMaquina;
		this.fechaInicialAlarma = fechaInicialAlarma;
	}

	public AlarmaMaquina() {
		super();
	}

	public int getIdAlarma() {
		return IdAlarma;
	}

	public void setIdAlarma(int idAlarma) {
		IdAlarma = idAlarma;
	}

	public int getIdMaquina() {
		return IdMaquina;
	}

	public void setIdMaquina(int idMaquina) {
		IdMaquina = idMaquina;
	}

	public Date getFechaInicialAlarma() {
		return fechaInicialAlarma;
	}

	public void setFechaInicialAlarma(Date fechaInicialAlarma) {
		this.fechaInicialAlarma = fechaInicialAlarma;
	}
	
	
	
}
