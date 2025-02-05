
package com.focus.qwds.ondemand.server.documentos.ws.jaxws;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement(name = "obtenerDocumento", namespace = "http://ws.documentos.server.ondemand.qwds.focus.com/")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "obtenerDocumento", namespace = "http://ws.documentos.server.ondemand.qwds.focus.com/")
public class ObtenerDocumento {

    @XmlElement(name = "idCheckOut", namespace = "")
    private int idCheckOut;

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

}
