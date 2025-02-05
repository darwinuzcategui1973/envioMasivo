package com.focus.wonderware.intocuh_sacop.forms;

import java.io.Serializable;

import com.desige.webDocuments.utils.beans.SuperActionForm;
import com.focus.qweb.to.SacopIntouchHijoTO;

/**
 * Created by IntelliJ IDEA.
 * User: srodriguez
 * Date: Mar 5, 2007
 * Time: 10:33:51 AM
 * To change this template use File | Settings | File Templates.
 */
public class Sacop_Intouchh_frm extends SuperActionForm implements Serializable {
   private long idtagname;
   private String tagname;
   private byte disparadasacop;
   private byte active;
   private long idplanillasacop1;
   
   public Sacop_Intouchh_frm() {
	   
   }

   public Sacop_Intouchh_frm(SacopIntouchHijoTO scp) {
	   idtagname = Long.parseLong(scp.getIdTagName());
	   tagname = scp.getTagName();
	   disparadasacop = Byte.parseByte(scp.getDisparadaSacop());
	   active = Byte.parseByte(scp.getActive());
	   idplanillasacop1 = Byte.parseByte(scp.getIdPlanillaSacop1());
   }

   private Object[] sacop_intouch;
    public long getIdtagname() {
        return idtagname;
    }

    public void setIdtagname(long idtagname) {
        this.idtagname = idtagname;
    }

    public String getTagname() {
        return tagname;
    }

    public void setTagname(String tagname) {
        this.tagname = tagname;
    }

    public byte getActive() {
        return active;
    }

    public void setActive(byte active) {
        this.active = active;
    }

    public long getIdplanillasacop1() {
        return idplanillasacop1;
    }

    public void setIdplanillasacop1(long idplanillasacop1) {
        this.idplanillasacop1 = idplanillasacop1;
    }

    public Object[] getSacop_intouch() {
        return sacop_intouch;
    }

    public void setSacop_intouch(Object[] sacop_intouch) {
        this.sacop_intouch = sacop_intouch;
    }

    public byte getDisparadasacop() {
        return disparadasacop;
    }

    public void setDisparadasacop(byte disparadasacop) {
        this.disparadasacop = disparadasacop;
    }
}
