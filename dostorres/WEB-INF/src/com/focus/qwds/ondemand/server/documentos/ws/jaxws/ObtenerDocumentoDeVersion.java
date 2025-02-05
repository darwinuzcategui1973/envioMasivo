
package com.focus.qwds.ondemand.server.documentos.ws.jaxws;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement(name = "obtenerDocumentoDeVersion", namespace = "http://ws.documentos.server.ondemand.qwds.focus.com/")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "obtenerDocumentoDeVersion", namespace = "http://ws.documentos.server.ondemand.qwds.focus.com/")
public class ObtenerDocumentoDeVersion {

    @XmlElement(name = "idVersion", namespace = "")
    private int idVersion;

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
