package com.focus.wonderware.intocuh_sacop.forms;

import java.io.Serializable;

import com.desige.webDocuments.utils.beans.SuperActionForm;
import com.focus.qweb.to.TagNameTO;

/**
 * Created by IntelliJ IDEA.
 * User: srodriguez
 * Date: Mar 8, 2007
 * Time: 2:27:27 PM
 * To change this template use File | Settings | File Templates.
 */
public class Tagname extends SuperActionForm implements Serializable {
    /**
	 * 
	 */
	private static final long serialVersionUID = -4637515721768592144L;
	private  long idtagname2;
    private String id;
    private String tagname;
    private byte active;
    private String idtipo ;
    
    public Tagname() {
    	
    }

    public Tagname(TagNameTO t) {
    	setIdtagname2(Long.parseLong(t.getIdTagName2()));
    	setId(t.getIdTagName2());
    	setTagname(t.getTagName());
    	setActive(Byte.parseByte(t.getActive()));
    	setIdtipo(t.getIdTipo());
    }
    
    public long getIdtagname2() {
        return idtagname2;
    }

    public void setIdtagname2(long idtagname2) {
        this.idtagname2 = idtagname2;
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


    public String getIdtipo() {
        return idtipo;
    }

    public void setIdtipo(String idtipo) {
        this.idtipo = idtipo;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
