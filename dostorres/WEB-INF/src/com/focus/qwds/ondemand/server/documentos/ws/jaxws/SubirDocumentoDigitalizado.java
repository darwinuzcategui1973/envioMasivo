
package com.focus.qwds.ondemand.server.documentos.ws.jaxws;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement(name = "subirDocumentoDigitalizado", namespace = "http://ws.documentos.server.ondemand.qwds.focus.com/")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "subirDocumentoDigitalizado", namespace = "http://ws.documentos.server.ondemand.qwds.focus.com/", propOrder = {
    "archivo",
    "nameFile"
})
public class SubirDocumentoDigitalizado {

    @XmlElement(name = "archivo", namespace = "", nillable = true)
    private byte[] archivo;
    @XmlElement(name = "nameFile", namespace = "")
    private String nameFile;

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
     *     returns String
     */
    public String getNameFile() {
        return this.nameFile;
    }

    /**
     * 
     * @param nameFile
     *     the value for the nameFile property
     */
    public void setNameFile(String nameFile) {
        this.nameFile = nameFile;
    }

}
