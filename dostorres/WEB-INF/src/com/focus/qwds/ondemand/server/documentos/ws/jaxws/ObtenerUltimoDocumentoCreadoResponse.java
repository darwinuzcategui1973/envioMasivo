
package com.focus.qwds.ondemand.server.documentos.ws.jaxws;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement(name = "obtenerUltimoDocumentoCreadoResponse", namespace = "http://ws.documentos.server.ondemand.qwds.focus.com/")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "obtenerUltimoDocumentoCreadoResponse", namespace = "http://ws.documentos.server.ondemand.qwds.focus.com/")
public class ObtenerUltimoDocumentoCreadoResponse {

    @XmlElement(name = "return", namespace = "")
    private com.focus.qwds.ondemand.server.documentos.entidades.Documento _return;

    /**
     * 
     * @return
     *     returns Documento
     */
    public com.focus.qwds.ondemand.server.documentos.entidades.Documento getReturn() {
        return this._return;
    }

    /**
     * 
     * @param _return
     *     the value for the _return property
     */
    public void setReturn(com.focus.qwds.ondemand.server.documentos.entidades.Documento _return) {
        this._return = _return;
    }

}
