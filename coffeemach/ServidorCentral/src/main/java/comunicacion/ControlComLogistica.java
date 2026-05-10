package comunicacion;

import ServerControl.*;
import servicios.ServicioComLogistica;
import com.zeroc.Ice.*;
import java.util.*;

public class ControlComLogistica implements ServicioComLogistica{
 
	private ServerControl control;

	public ControlComLogistica(ServerControl con) {
		control = con;
	}

    @Override
	public List<String> asignacionMaquina(int codigoOperador, Current current) {

		return control.listaAsignaciones(codigoOperador);
	}

	// Funciona correctamente
	@Override
	public List<String> asignacionMaquinasDesabastecidas(int codigoOperador, Current current) {

		return control.listaAsignacionesMDanada(codigoOperador);
	}

	@Override
	public boolean inicioSesion(int codigoOperador, String password, Current current) {

		return control.existeOperador(codigoOperador, password);
	}

}
