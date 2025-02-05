package com.focus.wonderware.intocuh_sacop.actions;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.StringTokenizer;

import com.focus.wonderware.actions.HandlerProcesosWonderWare;

/**
 * Created by IntelliJ IDEA. User: srodriguez Date: Mar 7, 2007 Time: 2:32:55 PM
 * To change this template use File | Settings | File Templates.
 */
public class ArchivoPlanoTagname {
	public void procesarArchivoPlanoTagname(String fileArch) {
		ArchivoPlanoTagname archivoPlanoTagname = new ArchivoPlanoTagname();
		char dosPuntos = ':';
		try {
			File f = new File(fileArch.trim());

			if (f.isFile()) {
				BufferedReader in = new BufferedReader(new FileReader(
						fileArch.toString()));
				String str;
				boolean swOtraPrimeracadena = false;
				boolean swPrimeraVez = true;

				ArrayList Arraycadenas1 = new ArrayList();
				ArrayList Arraycadenas2 = new ArrayList();
				ArrayList ArrayTemp = new ArrayList();
				while ((str = in.readLine()) != null) {
					// leemos la primera linea, no sabemos si va hacer la
					// primera fila con los valores
					// o si va hacer la segunda linea que estan los tags
					// involucrados....
					StringTokenizer strk = new StringTokenizer(str, ",;");
					Arraycadenas2.clear();
					String cad1 = "";
					// _________________________________________LLenamos los
					// ArrayList ___inicio_______________________________
					ArrayTemp.clear();
					int i = 0;
					while (strk.hasMoreTokens()) {
						String st = (String) strk.nextToken();
						ArrayTemp.add(st);
					}

					Arraycadenas2.clear();
					String primeraCad = ArrayTemp.get(0) != null ? (String) ArrayTemp
							.get(0) : "";

					if (dosPuntos == primeraCad.trim().charAt(0)) {
						Arraycadenas1.clear();
						Arraycadenas1.addAll(ArrayTemp);
					} else {
						Arraycadenas2.addAll(ArrayTemp);
						// String tagNameBD=(String)Arraycadenas2.get(0);
						HandlerProcesosWonderWare handlerProcesosWonderWare = new HandlerProcesosWonderWare();
						handlerProcesosWonderWare.getSacop_Intouch_Conftagname(
								Arraycadenas1, Arraycadenas2, str);
					}
					// _________________________________________LLenamos los
					// ArrayList ____fin______________________________
				}
				in.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
