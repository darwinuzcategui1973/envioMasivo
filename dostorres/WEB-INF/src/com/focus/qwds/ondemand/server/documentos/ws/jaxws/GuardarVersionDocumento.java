
package com.focus.qwds.ondemand.server.documentos.ws.jaxws;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement(name = "guardarVersionDocumento", namespace = "http://ws.documentos.server.ondemand.qwds.focus.com/")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "guardarVersionDocumento", namespace = "http://ws.documentos.server.ondemand.qwds.focus.com/", propOrder = {
    "archivo",
    "idVersion"
})
public class GuardarVersionDocumento {

    @XmlElement(name = "archivo", namespace = "", nillable = true)
    private byte[] archivo;
    @XmlElement(name = "idVersion", namespace = "")
    private int idVersion;

    /**
     * 
     * @return
     *     returns byte[]
     */
    public byte[] getArchivo() {
        return this.archivo;
    }

    /**
     * 
     * @param archivo
     *     the value for the archivo property
     */
    public void setArchivo(byte[] archivo) {
        this.archivo = archivo;
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
