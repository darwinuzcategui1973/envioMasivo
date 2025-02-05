import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;


public class Resta {
	
	public static void main(String[] args) throws IOException {
		
		BufferedReader lectura = new BufferedReader(new InputStreamReader(System.in));
		int minuendo = 0;
		int sustraendo = 0;
		int entero = 0;
		int decimal = 0;
		
		System.out.println("DIVISION POR RESTAS SUCESIVAS DE DOS NUMEROS ENTEROS");
		
		System.out.println("Ingrese el Minuendo: ");
		minuendo = Integer.parseInt(lectura.readLine());
		
		System.out.println("Ingrese el Sustraendo: ");
		sustraendo = Integer.parseInt(lectura.readLine());
		
		while(minuendo>=sustraendo) {
			minuendo = minuendo - sustraendo;
			entero++;
		}
		
		if(minuendo>0) {
			minuendo=minuendo*10;
			while(minuendo>=sustraendo) {
				minuendo = minuendo - sustraendo;
				decimal++;
			}
		}
		
		System.out.println("Resultado "+entero+"."+decimal); {
			
		}
	}
	
}
