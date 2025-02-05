package com.desige.webDocuments.accion.forms;

import java.io.Serializable;

import com.desige.webDocuments.utils.beans.SuperActionForm;

/**
 * Created by IntelliJ IDEA.
 * User: Administrador
 * Date: 20/03/2006
 * Time: 10:29:06 AM
 * To change this template use File | Settings | File Templates.
 */

public class ActionRecommended extends SuperActionForm implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 6972051536904149420L;
	private String id;
    private long idActionRecommended;
    private String descActionRecommended;
    private int idRegisterClass;
    
    public ActionRecommended() {
    	
    }

	public void cleanForm(){
		setIdActionRecommended(0);
		setDescActionRecommended("");
		setIdRegisterClass(0);
    }

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public long getIdActionRecommended() {
		return idActionRecommended;
	}

	public void setIdActionRecommended(long idActionRecommended) {
		this.idActionRecommended = idActionRecommended;
	}

	public String getDescActionRecommended() {
		return descActionRecommended;
	}

	public void setDescActionRecommended(String descActionRecommended) {
		this.descActionRecommended = descActionRecommended;
	}

	public int getIdRegisterClass() {
		return idRegisterClass;
	}

	public void setIdRegisterClass(int idRegisterClass) {
		this.idRegisterClass = idRegisterClass;
	}

}