package com.desige.webDocuments.serviceDocument.request;

public class DocumentoExternosRequest {
	private byte[] archivo;
	private String nombreArchivo;
	private String emailUsuarioRecibe;
	private String descript;
	private String palabraClave;
	private String extension;
	
	
	public DocumentoExternosRequest() {
	}

	public DocumentoExternosRequest(byte[] archivo, String  nombreArchivo, String emailUsuarioRecibe, String descript,String palabraClave, String extension  ) {
		super();
		this.archivo = archivo;
		this.nombreArchivo = nombreArchivo;
		this.emailUsuarioRecibe = emailUsuarioRecibe;
		this.descript = descript;
		this.palabraClave = palabraClave;
		this.extension = extension;

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

	
	public String  getEmailUsuarioRecibe() {
		return emailUsuarioRecibe;
	}

	public void setEmailUsuarioRecibe(String  emailUsuarioRecibe) {
		this.emailUsuarioRecibe = emailUsuarioRecibe;
	}
	
	public String  getDescript() {
		return descript;
	}

	public void setDescript(String  descript) {
		this.descript = descript;
	}
	
	public String  getPalabraClave() {
		return palabraClave;
	}

	public void setPalabraClave(String  palabraClave) {
		this.palabraClave = palabraClave;
	}
	public String  getExtension() {
		return extension;
	}

	public void setExtension(String  extension) {
		this.extension = extension;
	}
	

}
