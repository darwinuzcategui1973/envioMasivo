package com.desige.webDocuments.activities.forms;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import com.desige.webDocuments.utils.beans.SuperActionForm;
import com.focus.qweb.to.SubActivitiesTO;

/**
 * Title: SubActivities.java <br/>
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
public class SubActivities extends SuperActionForm implements Serializable, Comparable {
    /**
	 * 
	 */
	private static final long serialVersionUID = 7705258760437725934L;
	private long number;
    private long activityID;
    private String nameAct;
    private String description;
    private String executant;
    private byte active;
    private Set actUser = new HashSet();
    private int orden;
    
    public SubActivities() {
    	
    }

    public SubActivities(SubActivitiesTO sub) {
    	number = Long.parseLong(sub.getNumber());
    	if(number>0) {
	        activityID = Long.parseLong(sub.getActivityID());
	        nameAct = sub.getNameAct();
	        description = sub.getDescription();
	        active = Byte.parseByte(sub.getActive());
	        orden = Byte.parseByte(sub.getOrden());
	        
	        actUser = sub.getIdPersons();
    	}
    }

    public long getNumber() {
        return number;
    }

    public void setNumber(long number) {
        this.number = number;
    }

    public String getNameAct() {
        return nameAct;
    }

    public void setNameAct(String nameAct) {
        this.nameAct = nameAct;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getExecutant() {
        return executant;
    }

    public void setExecutant(String executant) {
        this.executant = executant;
    }
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

//    public long getIdTypeDoc() {
//        return idTypeDoc;
//    }
//
//    public void setIdTypeDoc(long idTypeDoc) {
//        this.idTypeDoc = idTypeDoc;
//    }

    public Set getActUser() {
        return actUser;
    }

    public void setActUser(Set actUser) {
        this.actUser = actUser;
    }

    public boolean equals(Object object) {
        if (!(object instanceof SubActivities)) {
            return false;
        }
        SubActivities act = (SubActivities) object;
        return (act.getNumber() == this.getNumber());
    }

    public int hashCode() {
        return new Long(getNumber()).hashCode();
    }

    public String toString() {
        return getNumber() + "_" + getNameAct() + "_"+getDescription() + "_" + getActUser().size();
    }

//    public Set getNextUser() {
//        return nextUser;
//    }
//
//    public void setNextUser(Set nextUser) {
//        this.nextUser = nextUser;
//    }
    public long getActivityID() {
        return activityID;
    }

    public void setActivityID(long activityID) {
        this.activityID = activityID;
    }

    public int getOrden() {
        return orden;
    }

    public void setOrden(int orden) {
        this.orden = orden;
    }

	public int compareTo(Object o) {
		// TODO Auto-generated method stub
		return 0;
	}
}
