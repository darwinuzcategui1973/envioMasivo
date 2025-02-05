package com.focus.qwds.ondemand.server.documentos.ws.jaxws;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement(name = "actualizarNombreDocumento", namespace = "http://ws.documentos.server.ondemand.qwds.focus.com/")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "actualizarNombreDocumento", namespace = "http://ws.documentos.server.ondemand.qwds.focus.com/", propOrder = {
    "idVersion",
    "extension"
})

// parametros idversion, extesion

public class ActualizarNombreDocumento {
	
	    @XmlElement(name = "idVersion", namespace = "")
	    private int idVersion;
	    @XmlElement(name = "extension", namespace = "")
	    private String extension;

	    /**
	     * 
	     * @return
	     *     returns String
	     */
	    public String getExtension() {
	        return this.extension;
	    }

	    /**
	     * 
	     * @param extension
	     *     the value for the extension property
	     */
	    public void setExtension(String extension) {
	        this.extension = extension;
	    }

	    /**
	     * 
	     * @return
	     *     returns int
	     */
	    public int getIdVersion() {
	        return this.idVersion;
	    }

	    /**
	     * 
	     * @param idVersion
	     *     the value for the idVersion property
	     */
	    public void setIdVersion(int idVersion) {
	        this.idVersion = idVersion;
	    }

	}
