package com.focus.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class ModuloBean {

	private String empresa = "";
	private String rif = "";
	private String mac = "";
	private String licencia = "";
	
	private String edicion = "";
	private String usuarios = "";
	private String viewers =  "";
	private String modulo =  "";
	private String inicio =  "";
	private String caduca =  "";
	private String gracia =  "";
	
	public static String[] ediciones = new String[]{"No especificada",
		"Qwebdocuments Lite",
		"Qwebdocuments Microempresa",
		"Qwebdocuments Pyme",
		"Qwebdocuments Profesional",
		"Qwebdocuments E-Gob",
		"Qnetfiles Free",
		"Qnetfiles NetWork"};
	public static String[] sistemas = new String[]{"No especificado","Windows","Linux"};
	
	public static String EDICION_LITE = "1";
	public static String EDICION_MICROEMPRESA = "2";
	public static String EDICION_PYME = "3";
	public static String EDICION_PROFESIONAL = "4";
	public static String EDICION_E_GOB = "5";
	
	public static boolean IS_EDICION_LITE = false;
	public static boolean IS_EDICION_MICROEMPRESA = false;
	public static boolean IS_EDICION_PYME = false;
	public static boolean IS_EDICION_PROFESIONAL = false;
	public static boolean IS_EDICION_E_GOB = false;

	public ModuloBean() {
		
	}

	public ModuloBean(String cad) {
		load(cad);
	}
	
	public void load(String cad) {
		if(cad!=null && cad.length()==(11+9)) { // sumamos la fecha mas un guion -yyyymmdd
			String licencia = cad.split("-")[0];
			String activacion = cad.split("-")[1];
			
			setEdicion(licencia.substring(0,1));
			setUsuarios(licencia.substring(1,5));
			setViewers(licencia.substring(5,9));
			setModulo(licencia.substring(9,11));
			
			// vamos a calcular las fechas de caducidad y de gracia
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
			SimpleDateFormat sdf2 = new SimpleDateFormat("dd/MM/yyyy");
			try {
				Date ini = sdf.parse(activacion);
				Calendar c = Calendar.getInstance();
				c.setTime(ini);

				c.add(Calendar.YEAR, 1);
				Date fin = c.getTime(); // vigencia de la clave 1 año
				
				c.add(Calendar.MONTH, 3); // periodo de gracia de tres meses
				c.add(Calendar.DATE, -1);
				Date ext = c.getTime();
				
				setInicio(sdf2.format(ini));
				setCaduca(sdf2.format(fin));
				setGracia(sdf2.format(ext));
				
			} catch (ParseException e) {
				e.printStackTrace();
			}
			
		}
	}
	
	public static void main(String[] args) {
		String activacion = "20180101";
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		SimpleDateFormat sdf2 = new SimpleDateFormat("dd/MM/yyyy");
		try {
			Date ini = sdf.parse(activacion);
			Calendar c = Calendar.getInstance();
			c.setTime(ini);

			c.add(Calendar.YEAR, 1);
			Date fin = c.getTime(); // vigencia de la clave 1 año
			
			c.add(Calendar.MONTH, 3); // periodo de gracia de tres meses
			c.add(Calendar.DATE, -1);
			Date ext = c.getTime();
			
			System.out.println(sdf2.format(ini));
			System.out.println(sdf2.format(fin));
			System.out.println(sdf2.format(ext));
			
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
	}

	public String getEdicion() {
		return edicion==null || edicion.equals("")?"1":edicion;
	}

	public String getEdicionFull() {
		try {
			return ediciones[Integer.parseInt(getEdicion())];
		} catch(Exception e) {
			return "";
		}
	}

	public void setEdicion(String edicion) {
		this.edicion = edicion;
	}

	public String getModulo() {
		return modulo;
	}

	public void setModulo(String modulo) {
		this.modulo = modulo;
	}

	public String getUsuarios() {
		return usuarios==null || usuarios.trim().equals("")?"0001":usuarios;
	}

	public void setUsuarios(String usuarios) {
		this.usuarios = usuarios;
	}

	public String getViewers() {
		return viewers==null || viewers.trim().equals("")?"0000":viewers;
	}

	public void setViewers(String viewers) {
		this.viewers = viewers;
	}

	
	
	public String getEmpresa() {
		return empresa==null || empresa.trim().equals("")?"Focus Consulting C.A.":empresa;
	}

	public void setEmpresa(String empresa) {
		this.empresa = empresa;
	}

	public String getLicencia() {
		return licencia;
	}

	public void setLicencia(String licencia) {
		this.licencia = licencia;
	}

	public String getRif() {
		return rif==null || rif.trim().equals("")?"J-30955730-0":rif;
	}

	public void setRif(String rif) {
		this.rif = rif;
	}
	
	

	public String getMac() {
		return mac;
	}

	public void setMac(String mac) {
		this.mac = mac;
	}
	
	

	public String getInicio() {
		return inicio;
	}

	public void setInicio(String inicio) {
		this.inicio = inicio;
		
	}

	public String getCaduca() {
		return caduca;
	}

	public void setCaduca(String caduca) {
		this.caduca = caduca;
	}

	public String getGracia() {
		return gracia;
	}

	public void setGracia(String gracia) {
		this.gracia = gracia;
	}

	public static void mainxx(String[] args) {
		ModuloBean mo = new ModuloBean("12345678933");
		System.out.println(mo.getEdicion());
		System.out.println(mo.getUsuarios());
		System.out.println(mo.getViewers());
		System.out.println(mo.getModulo());
	}

}
