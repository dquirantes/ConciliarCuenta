import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;


public class ConciliarCuentas {
	
	static final String rutaCSV  = "c:\\Partidas.csv";
	
	static int posicionId = 0;
	static int posicionImporte = 11;
	static char SEPARADOR = ';';
	

	public static void main(String[] args) throws Exception 
	{

		BufferedReader buffer;
		String linea;
		Asiento asiento;
		Asiento asiento_leido;
		Asiento asiento_leido_aux;
		String [] campos;

		

		System.out.println("Inicio Conciliar Cuentas Contables");

		ArrayList <Asiento> lista_partidas = new ArrayList<Asiento>();
		ArrayList <Asiento> lista_abiertas_mas = new ArrayList<Asiento>();
		ArrayList <Asiento> lista_abiertas_menos = new ArrayList<Asiento>();


	
		FileReader reader = new FileReader(rutaCSV);			
		buffer = new BufferedReader(reader);



		while ((linea=buffer.readLine()) != null )
		{				
			campos = linea.split(""+SEPARADOR);

			asiento = new Asiento();
			asiento.id=campos[posicionId];
			asiento.importe=Float.parseFloat(campos[posicionImporte]);
			asiento.estado = Estado.SIN_PROCESAR;


			lista_partidas.add(asiento);
		}

		buffer.close();

		Boolean casaron = false;

		for (int i=0; i<lista_partidas.size();i++)
		{				
			casaron = false;

			asiento_leido = (Asiento)lista_partidas.get(i); 			


			asiento_leido_aux=null;


			/* 
			 * Solo recorro los sin procesar de la Tabla
			 */
			if (asiento_leido.estado==Estado.SIN_PROCESAR)
			{	
				asiento_leido.estado = Estado.SIN_CONCILIACION;



				for (int j=i+1; j<lista_partidas.size() && !casaron;j++)
				{


					asiento_leido_aux = (Asiento)lista_partidas.get(j);

					asiento_leido.estado = Estado.SIN_CONCILIACION;


					if (asiento_leido.importe == - asiento_leido_aux.importe && asiento_leido_aux.estado == Estado.SIN_PROCESAR)
					{
						casaron = true;

						asiento_leido.estado = Estado.CONCILIADA;
						asiento_leido_aux.estado = Estado.CONCILIADA;
						asiento_leido_aux.asiento_concilia = asiento_leido.id;
						asiento_leido.asiento_concilia = asiento_leido_aux.id;
					}									
				}
			}				
		}




		for (Asiento asiento2: lista_partidas)
		{			 	

			// Divide en dos colecciones las no conciliadas
			if (asiento2.estado != Estado.CONCILIADA)				
			{				
				if (asiento2.importe<0)
					lista_abiertas_menos.add(asiento2);
				else
					lista_abiertas_mas.add(asiento2);
			}

		}




		System.out.println("Partidas abiertas positivas: " + lista_abiertas_mas);
		System.out.println("Partidas abiertas negativas: " + lista_abiertas_menos);



		for (Asiento partida_abierta: lista_abiertas_menos)
		{
			System.out.println("Buscando ..... " + partida_abierta.importe);

			List<Asiento> answer = new ArrayList<Asiento>();
			boolean haySolucion = obtenerSolucion(partida_abierta.importe, lista_abiertas_mas, answer, "");
			if(haySolucion)
			{
				System.out.println("Encontrado: " + partida_abierta + " " + " Respuesta: " + answer);
		
				partida_abierta.estado = Estado.CONCILIZADA_MULTIPLE;


				String valor = partida_abierta.id + "@";
				
				for(Asiento asiento3 : answer)				
					valor = valor + asiento3.id + ";";

				
				partida_abierta.asiento_concilia = valor;
				
				for (Asiento asiento3 : answer)
				{
					asiento3.asiento_concilia=valor;									
				}
				
				
				

			}

		}


		for (Asiento asiento_final :lista_partidas)
		{			 			
			System.out.println(asiento_final.estado+ " " + asiento_final.asiento_concilia);		
		}

		System.out.println("Fin!");


	}
	

	
	static boolean obtenerSolucion(float importe, List<Asiento> asientos,List<Asiento> respuesta, String pos) 
	{		
		pos=pos+"1";


		if(importe == 0) 
		{
			return true;
		}
		else if(importe > 0 || pos.length()>4) 
		{
			return false;
		} 
		else 
		{		

			for(Asiento asiento  : asientos) 
			{					

				if(asiento.importe>0 && asiento.estado==Estado.SIN_CONCILIACION && 
						obtenerSolucion(importe + asiento.importe, asientos, respuesta, pos))
				{
					respuesta.add(asiento);

					asiento.estado=Estado.CONCILIZADA_MULTIPLE;
					asientos.remove(asientos.indexOf(asiento));

					return true;
				}
			}
		}
		return false;
	}
}
