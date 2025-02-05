package com.focus.custom.delsur.util;

import java.util.Properties;

public class CustomProperties {
	private static final Properties props = new Properties();
	private static final Properties ubicaciones = new Properties();
	
	static {
		try {
			props.load(CustomProperties.class.getClassLoader().getResourceAsStream(
					"customProperties/DelSur.properties"));
			ubicaciones.load(CustomProperties.class.getClassLoader().getResourceAsStream(
					"customProperties/ubicacionRecaudos.properties"));
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
	
	/**
	 * 
	 * @return
	 */
	public String getDelSurCoreJdbcUrl(){
		return props.getProperty("jdbc.url");
	}
	
	/**
	 * 
	 * @return
	 */
	public int getHoraEjecucionProcesoBatch(){
		return Integer.parseInt(props.getProperty("hora.batch", "1"));
	}
	
	/**
	 * 
	 * @return
	 */
	public String getLocationNameToCredits(){
		return props.getProperty("locationToCredits");
	}
	
	/**
	 * 
	 * @return
	 */
	public String getRemotePathPrefix(){
		return props.getProperty("remotePathPrefix");
	}
	
	/**
	 * 
	 * @return
	 */
	public String getAgenciaPrefix(){
		return props.getProperty("agenciaPrefix");
	}
	
	/**
	 * 
	 * @return
	 */
	public String getRemotePathSeparator(){
		return props.getProperty("remotePathSeparator");
	}
	
	/**
	 * 
	 * @return
	 */
	public int getInitialDelayEjecucionesBatch(){
		return Integer.parseInt(props.getProperty("initial.delay.ejecuciones.batch"));
	}
	
	/**
	 * 
	 * @return
	 */
	public int getDelayEjecucionesBatch(){
		return Integer.parseInt(props.getProperty("delay.ejecuciones.batch"));
	}
	
	/**
	 * Basados en el id del tipo de documento (Abanks)
	 * obtenemos el nombre del nodo al que ira en la estructura
	 * 
	 * @param docId
	 * @return
	 */
	public String getNodeNameToDocId(int docId){
		return ubicaciones.getProperty("documento." + docId);
	}
	
	/**
	 * Listado de cuentas de correo a las cuales se les enviara cualquier detalle
	 * relacionado con errores criticos.
	 * 
	 * @return
	 */
	public String getMailSupportAccounts(){
		return props.getProperty("mailSupportAccounts");
	}
	
	/**
	 * Numero de segundos maximos de espera para la lectura de los archivos remotos
	 * Si este tiempo es alcanzado y no se esta realizando proceso alguno
	 * se debe avanzar a la siguiente solicitud.
	 * 
	 * @return
	 */
	public long getMaxWaitReading(){
		return Long.parseLong(props.getProperty("maxWaitReading", "420")) * 1000;
	}
}
