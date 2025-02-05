package com.desige.webDocuments.activities.forms;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;

import com.desige.webDocuments.utils.beans.SuperActionForm;
import com.focus.qweb.to.ActivityTO;
import com.focus.qweb.to.SubActivitiesTO;

/**
 * Title: Activities.java <br/>
 * Copyright: (c) 2004 Desige Servicios de Computación<br/>
 *
 * @author Ing. Nelson Crespo
 * @version WebDocuments v1.0
 * <br/>
 *      Changes:<br/>
 * <ul>
 *      <li> 31/10/2005 (NC) Creation </li>
 * </ul>
 */
public class Activities extends SuperActionForm implements Serializable {
    private long number;
    private String name;
    private String description;
    private long idTypeDoc;
    private byte active;
    private ArrayList subActivitys = new ArrayList();
    
    public Activities() {
    	
    }
    
    public Activities(ActivityTO oActivityTO) {
        this.number = Long.parseLong(oActivityTO.getActNumber());
        this.name = oActivityTO.getActName();
        this.description = oActivityTO.getActDescription();
        this.idTypeDoc = Long.parseLong(oActivityTO.getActTypeDocument()==null?"0":oActivityTO.getActTypeDocument());
        this.active = Byte.parseByte(oActivityTO.getActActive()==null?"0":oActivityTO.getActActive());
        
        this.subActivitys = new ArrayList();
        
        Iterator it = oActivityTO.getSubActivitiesTO().iterator();
        SubActivitiesTO sub;
        while(it.hasNext()) {
        	sub = (SubActivitiesTO) it.next();

        	this.subActivitys.add(new SubActivities(sub));
        }
        
        
    }

    public long getNumber() {
        return number;
    }

    public void setNumber(long number) {
        this.number = number;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

//    public String getExecutant() {
//        return executant;
//    }
//
//    public void setExecutant(String executant) {
//        this.executant = executant;
//    }
//
//    public String getNextExecutant() {
//        return nextExecutant;
//    }
//
//    public void setNextExecutant(String nextExecutant) {
//        this.nextExecutant = nextExecutant;
//    }

    public byte getActive() {
        return active;
    }

    public void setActive(byte active) {
        this.active = active;
    }

    public long getIdTypeDoc() {
        return idTypeDoc;
    }

    public void setIdTypeDoc(long idTypeDoc) {
        this.idTypeDoc = idTypeDoc;
    }

//    public Set getActUser() {
//        return actUser;
//    }
//
//    public void setActUser(Set actUser) {
//        this.actUser = actUser;
//    }

    public boolean equals(Object object) {
        if (!(object instanceof Activities)) {
            return false;
        }
        Activities act = (Activities) object;
        return (act.getNumber() == this.getNumber());
    }

    public int hashCode() {
        return new Long(getNumber()).hashCode();
    }

//    public Set getNextUser() {
//        return nextUser;
//    }
//
//    public void setNextUser(Set nextUser) {
//        this.nextUser = nextUser;
//    }
    public ArrayList getSubActivitys() {
        return subActivitys;
    }

    public void setSubActivitys(ArrayList subActivitys) {
        this.subActivitys = subActivitys;
    }
}
