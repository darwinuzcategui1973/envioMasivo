package com.focus.util;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;

public class Conector {

	public final static String FILE_PROPERTIES = "BD.properties";
	
	public static String FULL_PATH_PROPERTIES = null;

	private String driver = "";
	private String servidor = "";
	private String basededatos = "";
	private String usuario = "";
	private String password = "";
	private String puerto = "";
	private String url = "";

	public Conector() {

	}

	public void iniciarConector() {
		/*
		ManejoProperties manejoproperties = new ManejoProperties(getFileNameProperties());
		if (!existsProperties()) {
			driver = "org.apache.derby.jdbc.EmbeddedDriver";
			servidor = "localhost";
			basededatos = "qnetfiles";
			usuario = "qnetfiles";
			password = DataBase.CONTRASENA_DB;
			puerto = "puerto=";
			url = "jdbc:derby:DATABASE;create=true;user=USER;password=PASSWORD;";
		} else {
			manejoproperties.cargarArchivoPropertie();
			driver = manejoproperties.getProperty("driver");
			servidor = manejoproperties.getProperty("servidor");
			basededatos = manejoproperties.getProperty("basededatos");
			usuario = manejoproperties.getProperty("usuario");
			password = manejoproperties.getProperty("password");
			puerto = manejoproperties.getProperty("puerto");
			url = manejoproperties.getProperty("url");
		}
		*/
	}

	public String getFileNameProperties() {
		File prop = null;
		if(FULL_PATH_PROPERTIES==null){
			prop = new File("");
			StringBuffer nameFile = new StringBuffer();
			nameFile.append(prop.getAbsolutePath());
			nameFile.append(File.separator).append("bin").append(File.separator).append(FILE_PROPERTIES);
			FULL_PATH_PROPERTIES = nameFile.toString();
		}
		return FULL_PATH_PROPERTIES;
	}
	public boolean existsProperties() {
		File prop = new File(getFileNameProperties());
		return prop.exists();
	}

	public void writeFile() {
		StringBuffer texto = new StringBuffer();
		texto.append("driver=").append(this.getDriver()).append("\n");
		texto.append("servidor=").append(this.getServidor()).append("\n");
		texto.append("basededatos=").append(this.getBasededatos()).append("\n");
		texto.append("usuario=").append(this.getUsuario()).append("\n");
		texto.append("password=").append(this.getPassword()).append("\n");
		texto.append("puerto=").append(this.getPuerto()).append("\n");
		texto.append("url=").append(this.url).append("\n");

		FileWriter fichero = null;
		PrintWriter pw = null;
		try {
			File prop = new File("");
			prop = new File(getFileNameProperties());
			System.out.println(prop.getAbsolutePath());

			fichero = new FileWriter(prop);
			pw = new PrintWriter(fichero);

			System.out.println(texto.toString());
			pw.println(texto.toString());

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				// Nuevamente aprovechamos el finally para
				// asegurarnos que se cierra el fichero.
				if (null != fichero)
					fichero.close();
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}
	}

	public String getBasededatos() {
		return basededatos;
	}

	public void setBasededatos(String basededatos) {
		this.basededatos = basededatos;
	}

	public String getDriver() {
		return driver;
	}

	public void setDriver(String driver) {
		this.driver = driver;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getPuerto() {
		return puerto;
	}

	public void setPuerto(String puerto) {
		this.puerto = puerto;
	}

	public String getServidor() {
		return servidor;
	}

	public void setServidor(String servidor) {
		this.servidor = servidor;
	}

	public String getUrl() {
		String cad = url.replaceAll("SERVIDOR", servidor);
		cad = cad.replaceAll("PORT", puerto);
		cad = cad.replaceAll("DATABASE", basededatos);
		cad = cad.replaceAll("USER", usuario);
		cad = cad.replaceAll("PASSWORD", password);
		// System.out.println(cad);
		return cad;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getUsuario() {
		return usuario;
	}

	public void setUsuario(String usuario) {
		this.usuario = usuario;
	}

}