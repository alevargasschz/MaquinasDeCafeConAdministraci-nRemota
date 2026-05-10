package ServerControl;

import java.util.Scanner;

import modelo.Operador;

public class ConsolaAdministracion implements Runnable{

	private ServerControl control;
	
	public ConsolaAdministracion(ServerControl control)
	{
		this.control=control;
	}

	@Override
	public void run() 
	{
		
		while(true)
		{
			Scanner lector=new Scanner(System.in);
			
			System.out.println("****************************");
			System.out.println("Consola de administración");
			System.out.println("****************************");
			
			System.out.println("\nOpciones:" +
					"\n1.Agregar un nuevo operador" +
					"\n2.Agregar una nueva maquina" +
					"\n3.Asignar maquina a operador" +
					"\n4.Estado de alarmas" +
					"\n5.Agregar nueva receta");
			int valor=lector.nextInt();
			
			switch(valor)
			{
			case 1:
				lector.nextLine();
				System.out.println(">>Digite el código del operador");
				int codOp=lector.nextInt();
				lector.nextLine();
				System.out.println(">>Digite el nombre del operador");
				String nombre=lector.nextLine();
				
				Operador op=new Operador();
				op.setId(codOp);
				op.setNombre(nombre);
				
				//guardarlo
				break;
			case 2:
				break;
			case 3:
				break;
			case 4:
				break;
			case 5:
				break;
			default:
				System.out.println("¡¡¡Opción incorrecta seleccionada!!!");
				break;
			}
		}
		
	}
}
