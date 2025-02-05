
package com.focus.qwds.ondemand.server.documentos.ws.jaxws;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement(name = "obtenerUltimoDocumentoCreado", namespace = "http://ws.documentos.server.ondemand.qwds.focus.com/")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "obtenerUltimoDocumentoCreado", namespace = "http://ws.documentos.server.ondemand.qwds.focus.com/")
public class ObtenerUltimoDocumentoCreado {

    @XmlElement(name = "u", namespace = "")
    private com.focus.qwds.ondemand.server.usuario.entidades.Usuario u;

    /**
     * 
     * @return
     *     returns Usuario
     */
    public com.focus.qwds.ondemand.server.usuario.entidades.Usuario getU() {
        return this.u;
    }

    /**
     * 
     * @param u
     *     the value for the u property
     */
    public void setU(com.focus.qwds.ondemand.server.usuario.entidades.Usuario u) {
        this.u = u;
    }

}
