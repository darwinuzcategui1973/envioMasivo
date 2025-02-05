package com.focus.qwds.ondemand.server.documentos.entidades;

import java.util.Date;


public class Documento  implements java.io.Serializable {

	private static final long serialVersionUID = 1L;

	private byte[] bytes;

    private Date fechaCheckOut;

    private int idCheckOut;

    private int idDocument;

    private int idVersion;
    
    private boolean isFileView;

    private java.lang.String mimeType;

    private java.lang.String nombre;

    private java.lang.String nombreArchivo;

    private java.lang.String numero;

    public Documento() {
    }

    public Documento(
           byte[] bytes,
           java.util.Date fechaCheckOut,
           int idCheckOut,
           int idDocument,
           int idVersion,
           boolean isFileView,
           java.lang.String mimeType,
           java.lang.String nombre,
           java.lang.String nombreArchivo,
           java.lang.String numero) {
    	
           this.bytes = bytes;
           this.fechaCheckOut = fechaCheckOut;
           this.idCheckOut = idCheckOut;
           this.idDocument = idDocument;
           this.idVersion = idVersion;
           this.mimeType = mimeType;
           this.nombre = nombre;
           this.nombreArchivo = nombreArchivo;
           this.numero = numero;
           this.isFileView = isFileView;
    }

	public byte[] getBytes() {
		return bytes;
	}

	public void setBytes(byte[] bytes) {
		this.bytes = bytes;
	}

	public Date getFechaCheckOut() {
		return fechaCheckOut;
	}

	public void setFechaCheckOut(Date fechaCheckOut) {
		this.fechaCheckOut = fechaCheckOut;
	}

	public int getIdCheckOut() {
		return idCheckOut;
	}

	public void setIdCheckOut(int idCheckOut) {
		this.idCheckOut = idCheckOut;
	}

	public int getIdDocument() {
		return idDocument;
	}

	public void setIdDocument(int idDocument) {
		this.idDocument = idDocument;
	}

	public int getIdVersion() {
		return idVersion;
	}

	public void setIdVersion(int idVersion) {
		this.idVersion = idVersion;
	}

	public boolean isFileView() {
		return isFileView;
	}

	public void setFileView(boolean isFileView) {
		this.isFileView = isFileView;
	}

	public java.lang.String getMimeType() {
		return mimeType;
	}

	public void setMimeType(java.lang.String mimeType) {
		this.mimeType = mimeType;
	}

	public java.lang.String getNombre() {
		return nombre;
	}

	public void setNombre(java.lang.String nombre) {
		this.nombre = nombre;
	}

	public java.lang.String getNombreArchivo() {
		return nombreArchivo;
	}

	public void setNombreArchivo(java.lang.String nombreArchivo) {
		this.nombreArchivo = nombreArchivo;
	}

	public java.lang.String getNumero() {
		return numero;
	}

	public void setNumero(java.lang.String numero) {
		this.numero = numero;
	}



}
