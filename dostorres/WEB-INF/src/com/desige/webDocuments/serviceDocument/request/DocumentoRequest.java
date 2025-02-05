package com.desige.webDocuments.serviceDocument.request;

public class DocumentoRequest {

	private byte[] archivo;
	private int idVersion;
	private int idCheckOut;
	private boolean isFileView;
	private String extension;

	public DocumentoRequest() {
	}

	public DocumentoRequest(byte[] archivo, int idCheckOut, boolean isFileView) {
		super();
		this.archivo = archivo;
		this.idCheckOut = idCheckOut;
		this.isFileView = isFileView;

	}
	


	public DocumentoRequest(byte[] archivo, int idVersion) {
		super();
		this.archivo = archivo;
		this.idVersion = idVersion;
	}
	public DocumentoRequest( String extension ) {
		super();
		
		this.extension = extension;
	}
		
	
	
	public byte[] getArchivo() {
		return archivo;
	}

	public void setArchivo(byte[] archivo) {
		this.archivo = archivo;
	}

	public int getIdCheckOut() {
		return idCheckOut;
	}

	public void setIdCheckOut(int idCheckOut) {
		this.idCheckOut = idCheckOut;
	}

	public boolean isFileView() {
		return isFileView;
	}

	public void setFileView(boolean isFileView) {
		this.isFileView = isFileView;
	}

	public int getIdVersion() {
		return idVersion;
	}

	public void setIdVersion(int idVersion) {
		this.idVersion = idVersion;
	}
	
	public String  getExtension() {
		return extension;
	}

	public void setExtension(String  extension) {
		this.extension =  extension;
	}
	
	

}
