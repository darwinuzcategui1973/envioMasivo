package com.desige.webDocuments.workFlows.forms;

import java.io.Serializable; 
import java.util.Collection;
import java.util.Vector;

import com.desige.webDocuments.utils.beans.SuperActionForm;

/**
 * Title: BaseWorkFlow.java<br>
 * Copyright: (c) 2003 Consis International<br>
 * Company: Consis International (CON)<br>
 * @author Nelson Crespo (NC)
 * @version Acsel-e v2.2
 * <br>
 * Changes:<br>
 * <ul>
 *      <li> 23/07/2004 (NC) Creation </li>
 * <ul>
 */
public class BaseWorkFlow extends SuperActionForm implements Serializable {
    private long newID;
    private int numDocument;
    private String owner;
    private boolean ownerCurrent;
    private int copy;
    private int secuential;
    private int conditional;
    private int notified;
    private int expire;
    private String dateExpireWF;
    private Object[] groupsSelected;
    private Object[] usersSelected;
    private Object[] usersSelectedExpire;
    private String statu;
    public int typeWF;    
    //ydavila Ticket 001-00-003023
    private int subtypeWF;    
    private String comments;
    private String titleForMail;
    private String dateCreationWF;
    private int mayorVersionDocument;
    private int numWF;
	private int idMovement;
    private int numVersion;
    private String nameOwner;
    private String dateCompletion;
    private boolean newWF;
    private String row;
    private String lastVersion;
    private byte eliminar;
    //ydavila Ticket 001-00-003023
    private byte cambiar;
    private byte imprimir;
    private long act_Number;
    private String idNodeDocument;
    //Manejo de Datos de la Actividad Relacionada al Flujo de Trabajo
    private Collection userSelecteds;
    private Collection userSelectedsExpire;
    private String nameAct;
    private long nAct;
    private long subNumber;
    //ID Unico para Asociar las Actividades
    private long IDFlexFlow;
    //Id de los Usuarios Sugeridos
    private String usrSug;
    //Indica si es un Flujo Paramétrico o No
    private boolean flexFlow;

    public byte getImprimir() {
       return imprimir;
   }
   public void setImprimir(byte imprimir) {
       this.imprimir = imprimir;
   }

    //SIMON 11 DE JULIO 2005 INICIO
     public byte getEliminar() {
        return eliminar;
    }

    public void setEliminar(byte eliminar) {
        this.eliminar = eliminar;
    }
    
    //ydavila Ticket 001-00-003023
    public byte getCambiar() {
        return cambiar;
    }
    public void setCambiar(byte cambiar) {
        this.cambiar = cambiar;
    }
        //SIMON 11 DE JULIO 2005 FIN

    public int getNumDocument() {
        return numDocument;
    }

    public void setNumDocument(int numDocument) {
        this.numDocument = numDocument;
    }

    public int getSecuential() {
        return secuential;
    }

    public void setSecuential(int secuential) {
        this.secuential = secuential;
    }

    public int getConditional() {
        return conditional;
    }

    public void setConditional(int conditional) {
        this.conditional = conditional;
    }

    public int getNotified() {
        return notified;
    }

    public void setNotified(int notified) {
        this.notified = notified;
    }

    public int getExpire() {
        return expire;
    }

    public void setExpire(int expire) {
        this.expire = expire;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public Object[] getGroupsSelected() {
        return groupsSelected;
    }

    public void setGroupsSelected(Object[] groupsSelected) {
        this.groupsSelected = groupsSelected;
    }

    public Object[] getUsersSelected() {
        return usersSelected;
    }

    public void setUsersSelected(Object[] usersSelected) {
        this.usersSelected = usersSelected;
    }

    public int getTypeWF() {
        return typeWF;
    }

    public void setTypeWF(int typeWF) {
        this.typeWF = typeWF;
    }
    
    //ydavila Ticket 001-00-003023
    public int getSubtypeWF() {
        return subtypeWF;
    }
    public void setSubtypeWF(int subtypeWF) {
        this.subtypeWF = subtypeWF;
    }
    
    public String getStatu() {
        return statu;
    }

    public void setStatu(String statu) {
        this.statu = statu;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public String getTitleForMail() {
        return titleForMail;
    }

    public void setTitleForMail(String titleForMail) {
        this.titleForMail = titleForMail;
    }

    public String getDateExpireWF() {
        return dateExpireWF;
    }

    public void setDateExpireWF(String dateExpireWF) {
        this.dateExpireWF = dateExpireWF;
    }

    public String getDateCreationWF() {
        return dateCreationWF;
    }

    public void setDateCreationWF(String dateCreationWF) {
        this.dateCreationWF = dateCreationWF;
    }

    public int getMayorVersionDocument() {
        return mayorVersionDocument;
    }

    public void setMayorVersionDocument(int mayorVersionDocument) {
        this.mayorVersionDocument = mayorVersionDocument;
    }

    public int getNumWF() {
        return numWF;
    }

    public void setNumWF(int numWF) {
        this.numWF = numWF;
    }

public int getIdMovement() {
	return idMovement;
}

public void setIdMovement(int idMovement) {
	this.idMovement = idMovement;
}

    public int getNumVersion() {
        return numVersion;
    }

    public void setNumVersion(int numVersion) {
        this.numVersion = numVersion;
    }

    public boolean isOwnerCurrent() {
        return ownerCurrent;
    }
    public void setOwnerCurrent(boolean ownerCurrent) {
        this.ownerCurrent=ownerCurrent;
    }

    public String getNameOwner() {
        return nameOwner;
    }

    public void setNameOwner(String nameOwner) {
        this.nameOwner = nameOwner;
    }

    public String getDateCompletion() {
        return dateCompletion;
    }

    public void setDateCompletion(String dateCompletion) {
        this.dateCompletion = dateCompletion;
    }

    public boolean isNewWF() {
        return newWF;
    }

    public void setNewWF(boolean newWF) {
        this.newWF = newWF;
    }

    public String getRow() {
        return row;
    }

    public void setRow(String row) {
        this.row = row;
    }

    public String getLastVersion() {
        return lastVersion;
    }

    public void setLastVersion(String lastVersion) {
        this.lastVersion = lastVersion;
    }

    public long getNewID() {
        return newID;
    }

    public void setNewID(long newID) {
        this.newID = newID;
    }

    public long getAct_Number() {
        return act_Number;
    }

    public void setAct_Number(long act_Number) {
        this.act_Number = act_Number;
    }

    public Collection getUserSelecteds() {
        return userSelecteds;
    }

    public void setUserSelecteds(Collection userSelecteds) {
        this.userSelecteds = userSelecteds;
    }

    public Collection getUserSelectedsExpire() {
    	if(userSelectedsExpire==null) {
    		userSelectedsExpire=new Vector();
    	}
        return userSelectedsExpire;
    }

    public void setUserSelectedsExpire(Collection userSelectedsExpire) {
        this.userSelectedsExpire = userSelectedsExpire;
    }

    public String getNameAct() {
        return nameAct;
    }

    public void setNameAct(String nameAct) {
        this.nameAct = nameAct;
    }

    public long getnAct() {
        return nAct;
    }

    public void setnAct(long nAct) {
        this.nAct = nAct;
    }

    public long getSubNumber() {
        return subNumber;
    }

    public void setSubNumber(long subNumber) {
        this.subNumber = subNumber;
    }

    public String getIdNodeDocument() {
        return idNodeDocument;
    }

    public void setIdNodeDocument(String idNodeDocument) {
        this.idNodeDocument = idNodeDocument;
    }

    public String getUsrSug() {
        return usrSug;
    }

    public void setUsrSug(String usrSug) {
        this.usrSug = usrSug;
    }

    public long getIDFlexFlow() {
        return IDFlexFlow;
    }

    public void setIDFlexFlow(long IDFlexFlow) {
        this.IDFlexFlow = IDFlexFlow;
    }

    public boolean isFlexFlow() {
        return flexFlow;
    }

    public void setFlexFlow(boolean flexFlow) {
        this.flexFlow = flexFlow;
    }
	public int getCopy() {
		return copy;
	}
	public void setCopy(int copy) {
		this.copy = copy;
	}
	public Object[] getUsersSelectedExpire() {
		return usersSelectedExpire;
	}
	public void setUsersSelectedExpire(Object[] usersSelectedExpire) {
		this.usersSelectedExpire = usersSelectedExpire;
	}
    
    
}
