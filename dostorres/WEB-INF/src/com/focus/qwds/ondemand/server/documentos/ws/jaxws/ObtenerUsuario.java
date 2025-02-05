
package com.focus.qwds.ondemand.server.documentos.ws.jaxws;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement(name = "obtenerUsuario", namespace = "http://ws.documentos.server.ondemand.qwds.focus.com/")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "obtenerUsuario", namespace = "http://ws.documentos.server.ondemand.qwds.focus.com/", propOrder = {
    "clave",
    "usuario"
})
public class ObtenerUsuario {

    @XmlElement(name = "clave", namespace = "")
    private String clave;
    @XmlElement(name = "usuario", namespace = "")
    private String usuario;

    /**
     * 
     * @return
     *     returns String
     */
    public String getClave() {
        return this.clave;
    }

    /**
     * 
     * @param clave
     *     the value for the clave property
     */
    public void setClave(String clave) {
        this.clave = clave;
    }

    /**
     * 
     * @return
     *     returns String
     */
    public String getUsuario() {
        return this.usuario;
    }

    /**
     * 
     * @param usuario
     *     the value for the usuario property
     */
    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

}
