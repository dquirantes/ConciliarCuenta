enum Estado{
		SIN_PROCESAR, CONCILIADA, SIN_CONCILIACION, CONCILIZADA_MULTIPLE
		};
public class Asiento{

		
		Float importe;
		String id;
		String asiento_concilia = "";
		Estado estado; 
	
		public String toString()
		{
			return "ID: " + id + " importe:" + importe + " " + asiento_concilia;
		}
}
