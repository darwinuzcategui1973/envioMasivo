
package com.focus.qwds.ondemand.server.documentos.ws.jaxws;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement(name = "guardarDocumento", namespace = "http://ws.documentos.server.ondemand.qwds.focus.com/")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "guardarDocumento", namespace = "http://ws.documentos.server.ondemand.qwds.focus.com/", propOrder = {
    "archivo",
    "idCheckOut",
    "isFileView"
})
public class GuardarDocumento {

    @XmlElement(name = "archivo", namespace = "", nillable = true)
    private byte[] archivo;
    @XmlElement(name = "idCheckOut", namespace = "")
    private int idCheckOut;
    @XmlElement(name = "isFileView", namespace = "")
    private boolean isFileView;

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
    public int getIdCheckOut() {
        return this.idCheckOut;
    }

    /**
     * 
     * @param idCheckOut
     *     the value for the idCheckOut property
     */
    public void setIdCheckOut(int idCheckOut) {
        this.idCheckOut = idCheckOut;
    }

    /**
     * 
     * @return
     *     returns boolean
     */
    public boolean isIsFileView() {
        return this.isFileView;
    }

    /**
     * 
     * @param isFileView
     *     the value for the isFileView property
     */
    public void setIsFileView(boolean isFileView) {
        this.isFileView = isFileView;
    }

}
