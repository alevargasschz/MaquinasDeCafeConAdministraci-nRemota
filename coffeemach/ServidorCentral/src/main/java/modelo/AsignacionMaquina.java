package modelo;

public class AsignacionMaquina {

	private Maquina maquina;
	private Operador operador;
		
	public AsignacionMaquina() {
		super();
	}

	public AsignacionMaquina(Maquina maquina, Operador operador) {
		super();
		this.maquina = maquina;
		this.operador = operador;
	}

	public Maquina getMaquina() {
		return maquina;
	}

	public void setMaquina(Maquina maquina) {
		this.maquina = maquina;
	}

	public Operador getOperador() {
		return operador;
	}

	public void setOperador(Operador operador) {
		this.operador = operador;
	}
	
	
}
