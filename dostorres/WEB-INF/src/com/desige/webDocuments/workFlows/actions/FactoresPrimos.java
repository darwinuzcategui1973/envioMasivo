package com.desige.webDocuments.workFlows.actions;

/*
 * Ejemplo de descomposici�n en factores primos.
 */

import java.util.LinkedList;

/**
 * Descomposici�n en factores primos.
 * @author chuidiang
 */
public class FactoresPrimos {
	/**
	 * Un bucle de 2 a 100 para ir descomponiendo todos esos n�meros en factores primos.
	 * Escribe todos ellos con su descomposici�n por pantalla.
	 * @param args
	 */
	public static void main(String[] args) {
		   Long i =  1000000000000000000L;
		   LinkedList<Long> factores = FactoresPrimos.descomponEnFactoresPrimos(i);
		   System.out.print(i+"=");
		   for (Long factor: factores)
		   {
			   System.out.print(factor);
			   
			   // Si no es el �ltimo, escribe el signo *
			   if (factor != factores.getLast())
				   System.out.print("*");
		   }
		   return;
	}

	/**
	 * Se le pasa un valor entero superior a 1 y devuelve una lista de factores primos
	 * en los que ha descompuesto el n�mero. 
	 * @param valor N�mero de descomponer
	 * @return Lista de factores primos.
	 */
	public static LinkedList<Long> descomponEnFactoresPrimos(long valor)
	{
		assert valor>1;
		
		// Se empieza probando como posible factor primo el 2.
		int factor = 2;
		LinkedList<Long> factores=new LinkedList<Long>();
		
		// Ultimo factor que debemos probar.
		int factorLimite = (int)Math.sqrt(valor);
		
		while (factor <= factorLimite)
		{
			// Mientras es divisible, se a�ade el factor a la lista de factores primos
			// y se realiza la divisi�n.
			while (valor % factor == 0 )
			{
				factores.add(new Long(factor));
				valor = valor/factor;
			}
			
			// Si no es divisible, se pasa al posible siguiente factor.
			if (factor==2)
				factor++;
			else
				factor+=2;
		}
		
		// Si no se ha logrado la descomposici�n total, a�adimos el �ltimo valor que
		// queda a la lista.
		if (valor!=1)
			factores.add(new Long(valor));
		
		return factores;
	}
}