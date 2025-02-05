
package com.focus.qwds.ondemand.server.documentos.ws.jaxws;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement(name = "obtenerUsuarioResponse", namespace = "http://ws.documentos.server.ondemand.qwds.focus.com/")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "obtenerUsuarioResponse", namespace = "http://ws.documentos.server.ondemand.qwds.focus.com/")
public class ObtenerUsuarioResponse {

    @XmlElement(name = "return", namespace = "")
    private com.focus.qwds.ondemand.server.usuario.entidades.Usuario _return;

    /**
     * 
     * @return
     *     returns Usuario
     */
    public com.focus.qwds.ondemand.server.usuario.entidades.Usuario getReturn() {
        return this._return;
    }

    /**
     * 
     * @param _return
     *     the value for the _return property
     */
    public void setReturn(com.focus.qwds.ondemand.server.usuario.entidades.Usuario _return) {
        this._return = _return;
    }

}
