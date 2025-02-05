package com.desige.webDocuments.serviceDocument.request;

public class WhatsAppRequest {
	
	private byte[] archivo;
	private String nombreArchivo;
	private String mensaje;
	private String telefono;
	
	public WhatsAppRequest() {
	}
	
	public WhatsAppRequest(byte[] archivo, String nombreArchivo, String mensaje, String telefono) {
		super();
		this.archivo = archivo;
		this.nombreArchivo = nombreArchivo;
		this.mensaje = mensaje;
		this.telefono = telefono;
	}
	

	public WhatsAppRequest(String mensaje, String telefono) {
		super();
		this.mensaje = mensaje;
		this.telefono = telefono;
	}



	public byte[] getArchivo() {
		return archivo;
	}

	public void setArchivo(byte[] archivo) {
		this.archivo = archivo;
	}

	public String getNombreArchivo() {
		return nombreArchivo;
	}

	public void setNombreArchivo(String nombreArchivo) {
		this.nombreArchivo = nombreArchivo;
	}

	public String getMensaje() {
		return mensaje;
	}

	public void setMensaje(String mensaje) {
		this.mensaje = mensaje;
	}

	public String getTelefono() {
		return telefono;
	}

	public void setTelefono(String telefono) {
		this.telefono = telefono;
	}

	

}
