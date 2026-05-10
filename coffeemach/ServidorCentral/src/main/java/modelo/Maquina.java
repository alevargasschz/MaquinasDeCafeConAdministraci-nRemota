package modelo;

public class Maquina {

	private int id;
	private String ubicacion;
	
	public Maquina() {
		super();
	}
	public Maquina(int id, String ubicacion) {
		super();
		this.id = id;
		this.ubicacion = ubicacion;
	}
	public int peticioncodigo() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getUbicacion() {
		return ubicacion;
	}
	public void setUbicacion(String ubicacion) {
		this.ubicacion = ubicacion;
	}
	
	
	
}
