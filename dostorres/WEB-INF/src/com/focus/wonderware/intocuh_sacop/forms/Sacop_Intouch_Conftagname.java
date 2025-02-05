package com.focus.wonderware.intocuh_sacop.forms;

import java.io.Serializable;

import com.desige.webDocuments.utils.beans.SuperActionForm;
import com.focus.qweb.to.ConfTagNameTO;

/**
 * Created by IntelliJ IDEA.
 * User: srodriguez
 * Date: Mar 7, 2007
 * Time: 3:17:10 PM
 * To change this template use File | Settings | File Templates.
 */
public class Sacop_Intouch_Conftagname extends SuperActionForm implements Serializable {
    private long idtagname;
    private String tipotag;
    private String tipoalarma;
    private String valor;
    private String id;
    private byte active;

    public Sacop_Intouch_Conftagname() {
    	
    }
    
    public Sacop_Intouch_Conftagname(ConfTagNameTO oConfTagNameTO) {
        idtagname = Long.parseLong(oConfTagNameTO.getIdTagName());
        tipotag = oConfTagNameTO.getTipoTag();
        tipoalarma = oConfTagNameTO.getTipoAlarma();
        valor = oConfTagNameTO.getValor();
        id = oConfTagNameTO.getIdTagName();
        active = Byte.parseByte(oConfTagNameTO.getActive());
    }

    public long getIdtagname() {
        return idtagname;
    }

    public void setIdtagname(long idtagname) {
        this.idtagname = idtagname;
    }

    public String getTipotag() {
        return tipotag;
    }

    public void setTipotag(String tipotag) {
        this.tipotag = tipotag;
    }

    public String getTipoalarma() {
        return tipoalarma;
    }

    public void setTipoalarma(String tipoalarma) {
        this.tipoalarma = tipoalarma;
    }

    public String getValor() {
        return valor;
    }

    public void setValor(String valor) {
        this.valor = valor;
    }

    public byte getActive() {
        return active;
    }

    public void setActive(byte active) {
        this.active = active;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
