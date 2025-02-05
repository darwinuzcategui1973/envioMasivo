package com.focus.qwds.ondemand.server.documentos.ws.jaxws;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement(name = "recibirDocumentoExternos", namespace = "http://ws.documentos.server.ondemand.qwds.focus.com/")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "recibirDocumentoExternos", namespace = "http://ws.documentos.server.ondemand.qwds.focus.com/", propOrder = {
		"archivo",
		"nombreArchivo",
		"emailUsuarioRecibe",
		"descript",
		"palabraClave",
		"extension"
		
})

// parametros archivo nombre usuarioemail

public class RecibirDocumentoExternos {

	@XmlElement(name = "archivo", namespace = "", nillable = true)
	private byte[] archivo;
	@XmlElement(name = "nombreArchivo", namespace = "")
	private String nombreArchivo;
	@XmlElement(name = "emailUsuarioRecibe", namespace = "")
	private String emailUsuarioRecibe;
	@XmlElement(name = "descript", namespace = "")
	private String descript;
	@XmlElement(name = "palabraClave", namespace = "")
	private String palabraClave;
	@XmlElement(name = "extension", namespace = "")
	private String extension;


	/**
	 * 
	 * @return 
	 * 		returns byte[]
	 */
	public byte[] getArchivo() {
		return this.archivo;
	}

	/**
	 * 
	 * @param archivo
	 *            the value for the archivo property
	 */
	public void setArchivo(byte[] archivo) {
		this.archivo = archivo;
	}

	/**
	 * 
	 * @return 
	 * 		returns String
	 */
	public String getNombreArchivo() {
		return this.nombreArchivo;
	}

	/**
	 * 
	 * @param nameFile
	 *            the value for the nameFile property
	 */
	public void setNombreArchivo(String nombreArchivo) {
		this.nombreArchivo = nombreArchivo;
	}

	/**
	 * 
	 * @return 
	 * 		returns String
	 */
	public String getEmailUsuarioRecibe() {
		return this.emailUsuarioRecibe;
	}

	/**
	 * 
	 * @param nameFile
	 *            the value for the nameFile property
	 */
	public void setEmailUsuarioRecibe(String emailUsuarioRecibe) {
		this.emailUsuarioRecibe = emailUsuarioRecibe;
	}
	
	/**
	 * 
	 * @return 
	 * 		returns String
	 */
	public String getDescript() {
		return this.descript;
	}

	/**
	 * 
	 * @param nameFile
	 *            the value for the nameFile property
	 */
	public void setDescript(String descript) {
		this.descript = descript;
	}
	
	/**
	 * 
	 * @return 
	 * 		returns String
	 */
	public String getPalabraClave() {
		return this.palabraClave;
	}

	/**
	 * 
	 * @param nameFile
	 *            the value for the nameFile property
	 */
	public void setPalabraClave(String palabraClave) {
		this.palabraClave = palabraClave;
	}
	/**
	 * 
	 * @return 
	 * 		returns String
	 */
	public String getExtension() {
		return this.extension;
	}

	/**
	 * 
	 * @param nameFile
	 *            the value for the nameFile property
	 */
	public void setExtension(String extension) {
		this.extension = extension;
	}

}
