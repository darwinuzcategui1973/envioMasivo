package com.focus.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;

public class ConvertMsSqlToPostgres {

	public ConvertMsSqlToPostgres() {
	}

	private void start(String rutaFichero, String origen) throws Exception {
		BufferedReader br = null;
		FileOutputStream fos = null;
		StringBuffer contenido = new StringBuffer();

		try {
			br = new BufferedReader(new FileReader(new File(rutaFichero.concat(origen))));
			String linea;
			while ((linea = br.readLine()) != null) {

				// Evaluamos la linea
				linea = linea.replaceAll("BIT","bit");// s/ bit / smallint /g

				linea = linea.replaceAll(" DATETIME", " datetime");
				linea = linea.replaceAll(" SMALLDATETIME", " smalldatetime");
				linea = linea.replaceAll(" TIMESTAMP", " timestamp");
				linea = linea.replaceAll(" NVARCHAR", " nvarchar");
				linea = linea.replaceAll(" VARCHAR", " varchar");
				linea = linea.replaceAll(" DEFAULT", " default");
				linea = linea.replaceAll(" CHAR", " char");
				linea = linea.replaceAll(" NTEXT", " ntext");
				linea = linea.replaceAll(" IMAGE", " image");
				linea = linea.replaceAll(" TINYINT", " tinyint");// s/ tinyint / smallint /g
				linea = linea.replaceAll(" BIGINT", " bigint");// s/ tinyint / smallint /g
				linea = linea.replaceAll(" BIT", " bit");// s/ tinyint / smallint /g
				linea = linea.replaceAll(" INT", " int");// s/ tinyint / smallint /g
				
				linea = linea.replaceAll(" datetime", " timestamp");// s/ datetime / timestamptz /g
				
				linea = linea.replaceAll(" smalldatetime", " timestamp");// s/ smalldatetime / timestamptz /g
				
				linea = linea.replaceAll(" nvarchar", " varchar");// s/ nvarchar / varchar /g
				
				linea = linea.replaceAll(" ntext", " text");// s/ ntext / text /g
				
				linea = linea.replaceAll(" image", " bytea");// s/ image / bytea /g

				if (linea.indexOf(" char(2147483647)")!=-1) {
					linea = linea.replaceAll(" char(.*)", " bytea");// s/ nvarchar / varchar /g
				}

				linea = linea.replaceAll(" char", " varchar");// s/ nvarchar / varchar /g
				
				linea = linea.replaceAll(" tinyint", " smallint");// s/ tinyint / smallint /g

				linea = linea.replaceAll(" varchar(2147483647)", " bytea");// s/ nvarchar / varchar /g
				
				if (linea.indexOf(" default '(0)'")!=-1) {
					linea = linea.replaceAll(" default (.*)", " default '0'");// s/ nvarchar / varchar /g
				}
				if (linea.indexOf(" default (0)")!=-1) {
					linea = linea.replaceAll(" default (.*)", " default '0'");// s/ nvarchar / varchar /g
				}
				if (linea.indexOf(" default (1)")!=-1) {
					linea = linea.replaceAll(" default (.*)", " default '1'");// s/ nvarchar / varchar /g
				}
			
				//System.out.println(linea);
				contenido.append(linea);
				contenido.append("\n");
			}

			try {
				fos = new FileOutputStream(new File(rutaFichero.concat("postgres_").concat(origen)));
				fos.write(contenido.toString().getBytes());
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				if (fos != null) {
					fos.close();
				}
			}

		} catch (Exception e) {
			//System.out.println("Excepcion - " + e.toString());
		} finally {
			if (br != null) {
				br.close();
			}
		}
	}

	public static void main(String[] args) {
		ConvertMsSqlToPostgres obj = new ConvertMsSqlToPostgres();
		try {
			obj.start("C:/appworkspace48/qwebds4/db/", "db.sql");
			//String cadena = "     , data char(2147483647)";
			////System.out.println(cadena.replaceAll(" char(.*)"," bytea"));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
