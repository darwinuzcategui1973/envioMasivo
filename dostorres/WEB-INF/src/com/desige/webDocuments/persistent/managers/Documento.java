package com.desige.webDocuments.persistent.managers;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Date;


public class Documento implements Serializable {


	/**
	 * 
	 */
	private static final long serialVersionUID = -1948121065863315346L;

	/**
	 * Documents.nameDocument
	 */
	private String nombre;
	
	/**
	 * Documents.number
	 */
	private String numero;
	
	/**
	 * 	  DocCheckOut.dateCheckOut
	 */			
	private Date fechaCheckOut;
	
	
	/**
	 * DocCheckOut.dataFile
	 */
	private byte[] bytes;
	
	/**
	 * DocCheckOut.idCheckOut
	 */
	private int idCheckOut;

	/**
	 * DocCheckOut.idDocument
	 */
	private int idDocument;   
	
	/**
	 * Documents.nameFile
	 */
	private String nombreArchivo;

	/**
	 * Documents.contextType
	 */
	private String mimeType;

        
        private int idVersion;
        
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

	public String getMimeType() {
		return mimeType;
	}

	public void setMimeType(String mimeType) {
		this.mimeType = mimeType;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public String getNombreArchivo() {
		return nombreArchivo;
	}

	public void setNombreArchivo(String nombreArchivo) {
		this.nombreArchivo = nombreArchivo;
	}

	public String getNumero() {
		return numero;
	}

	public void setNumero(String numero) {
		this.numero = numero;
	}

	@Override
	public int hashCode() {
		final int PRIME = 31;
		int result = 1;
		result = PRIME * result + ((mimeType == null) ? 0 : mimeType.hashCode());
		result = PRIME * result + Arrays.hashCode(bytes);
		result = PRIME * result + ((fechaCheckOut == null) ? 0 : fechaCheckOut.hashCode());
		result = PRIME * result + idCheckOut;
		result = PRIME * result + idDocument;
		result = PRIME * result + ((nombre == null) ? 0 : nombre.hashCode());
		result = PRIME * result + ((nombreArchivo == null) ? 0 : nombreArchivo.hashCode());
		result = PRIME * result + ((numero == null) ? 0 : numero.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final Documento other = (Documento) obj;
		if (mimeType == null) {
			if (other.mimeType != null)
				return false;
		} else if (!mimeType.equals(other.mimeType))
			return false;
		if (!Arrays.equals(bytes, other.bytes))
			return false;
		if (fechaCheckOut == null) {
			if (other.fechaCheckOut != null)
				return false;
		} else if (!fechaCheckOut.equals(other.fechaCheckOut))
			return false;
		if (idCheckOut != other.idCheckOut)
			return false;
		if (idDocument != other.idDocument)
			return false;
		if (nombre == null) {
			if (other.nombre != null)
				return false;
		} else if (!nombre.equals(other.nombre))
			return false;
		if (nombreArchivo == null) {
			if (other.nombreArchivo != null)
				return false;
		} else if (!nombreArchivo.equals(other.nombreArchivo))
			return false;
		if (numero == null) {
			if (other.numero != null)
				return false;
		} else if (!numero.equals(other.numero))
			return false;
		return true;
	}

	/**
		 * toString methode: creates a String representation of the object
		 * @return the String representation
		 * @author info.vancauwenberge.tostring plugin
	
		 */
		public String toString() {
			StringBuffer buffer = new StringBuffer();
			buffer.append("Documento[");
			buffer.append("serialVersionUID = ").append(serialVersionUID);
			buffer.append(", nombre = ").append(nombre);
			buffer.append(", numero = ").append(numero);
			buffer.append(", fechaCheckOut = ").append(fechaCheckOut);
			if (bytes == null) {
				buffer.append(", bytes = ").append("null");
			} else {
				buffer.append(", bytes = ").append("[");
				for (int i = 0; i < bytes.length; i++) {
					if (i != 0)
						buffer.append(", ");
					buffer.append(bytes[i]);
				}
				buffer.append("]");
			}
			buffer.append(", idCheckOut = ").append(idCheckOut);
			buffer.append(", idDocument = ").append(idDocument);
			buffer.append(", nombreArchivo = ").append(nombreArchivo);
			buffer.append(", mimeType = ").append(mimeType);
			buffer.append("]");
			return buffer.toString();
		}

    public int getIdVersion() {
        return idVersion;
    }

    public void setIdVersion(int idVersion) {
        this.idVersion = idVersion;
    }

	
}
