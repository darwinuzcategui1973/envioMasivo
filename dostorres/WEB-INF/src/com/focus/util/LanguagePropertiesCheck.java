package com.focus.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Properties;

public class LanguagePropertiesCheck {

	public static void compare(String ruta) {

		String[] files = new String[] { "LoginBundle_es.properties", "LoginBundle_es_VE.properties", "LoginBundle_en.properties",
				"LoginBundle_en_US.properties", "LoginBundle_po_BR.properties" };

		InputStream[] input = new InputStream[files.length];
		Properties[] prop = new Properties[files.length];
		ArrayList<String> todos = new ArrayList<String>();
		ArrayList<String> texto = null;
		@SuppressWarnings("rawtypes")
		ArrayList[] etiqueta = new ArrayList[files.length];
		boolean isOk = true;

		try {
			String ele = null;
			for (int i = 0; i < files.length; i++) {
				System.out.println(ruta.concat(files[i]));
				input[i] = new FileInputStream(ruta.concat(files[i]));
				// load a properties file
				prop[i] = new Properties();
				prop[i].load(input[i]);

				Enumeration<Object> enu = prop[i].keys();

				texto = new ArrayList<String>();
				while (enu.hasMoreElements()) {
					// System.out.println(enu.nextElement());
					ele = (String) enu.nextElement();
					if (!todos.contains(ele)) {
						todos.add(ele);
					}
					if (!texto.contains(ele)) {
						texto.add(ele);
					}
				}
				etiqueta[i] = texto;
			}

			boolean entro = false;
			for (int i = 0; i < files.length; i++) {
				entro = false;
				for (int x = 0; x < todos.size(); x++) {
					if (!etiqueta[i].contains(todos.get(x))) {
						if (!entro) {
							System.out.println("\nEtiquetas faltantes en ".concat(files[i]));
							entro = true;
						}
						System.err.println("\t".concat(todos.get(x)));
						isOk = false;
					}
				}
			}

			if(isOk) {
				System.out.println("FINALIZADO COMPARACION DE PROPERTIES EN ESTADO SUCCESS");
			} else {
				System.err.println("FINALIZADO COMPARACION DE PROPERTIES EN ESTADO FAIL");
			}

			/*
			 * // get the property value and print it out System.out.println(prop.getProperty("database")); System.out.println(prop.getProperty("dbuser"));
			 * System.out.println(prop.getProperty("dbpassword"));
			 */

		} catch (IOException ex) {
			ex.printStackTrace();
		} finally {
			for (int i = 0; i < files.length; i++) {
				if (input[i] != null) {
					try {
						input[i].close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}

	}

	public static void main(String[] args) {
		File f = new File("");
		String path = f.getAbsolutePath().concat(File.separator).concat("WEB-INF").concat(File.separator).concat("classes").concat(File.separator);
		// System.out.println(path);

		LanguagePropertiesCheck.compare(path);

		System.exit(0);
	}
}