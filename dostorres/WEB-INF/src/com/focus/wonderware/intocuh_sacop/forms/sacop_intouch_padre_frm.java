package com.focus.wonderware.intocuh_sacop.forms;

import java.io.Serializable;

import com.desige.webDocuments.utils.beans.SuperActionForm;
import com.focus.qweb.to.SacopIntouchPadreTO;

/**
 * Created by IntelliJ IDEA.
 * User: srodriguez
 * Date: Mar 6, 2007
 * Time: 3:30:13 PM
 * To change this template use File | Settings | File Templates.
 */
public class sacop_intouch_padre_frm  extends SuperActionForm implements Serializable {
 private long idsacopintouchpadre;
 private long idplanillasacop1;
 private byte enable;
 private byte active;
 
 	public sacop_intouch_padre_frm() {
 		
 	}
 	
 	public sacop_intouch_padre_frm(SacopIntouchPadreTO scp) {
 		 idsacopintouchpadre = Long.parseLong(scp.getIdSacopIntouchPadre());
 		 idplanillasacop1 = Long.parseLong(scp.getIdPlanillaSacop1());
 		 enable = Byte.parseByte(scp.getEnable());
 		 active = Byte.parseByte(scp.getActive());
 	}

    public long getIdsacopintouchpadre() {
        return idsacopintouchpadre;
    }

    public void setIdsacopintouchpadre(long idsacopintouchpadre) {
        this.idsacopintouchpadre = idsacopintouchpadre;
    }

    public long getIdplanillasacop1() {
        return idplanillasacop1;
    }

    public void setIdplanillasacop1(long idplanillasacop1) {
        this.idplanillasacop1 = idplanillasacop1;
    }

    public byte getActive() {
        return active;
    }

    public void setActive(byte active) {
        this.active = active;
    }

    public byte getEnable() {
        return enable;
    }

    public void setEnable(byte enable) {
        this.enable = enable;
    }
}
