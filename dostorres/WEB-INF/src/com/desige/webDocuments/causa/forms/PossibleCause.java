package com.desige.webDocuments.causa.forms;

import java.io.Serializable;

import com.desige.webDocuments.utils.beans.SuperActionForm;

/**
 * Created by IntelliJ IDEA.
 * User: Administrador
 * Date: 20/03/2006
 * Time: 10:29:06 AM
 * To change this template use File | Settings | File Templates.
 */

public class PossibleCause extends SuperActionForm implements Serializable {
	private String id;
    private long idPossibleCause;
    private String descPossibleCause;
    
    public PossibleCause() {
    	
    }

	public long getIdPossibleCause() {
		return idPossibleCause;
	}

	public void setIdPossibleCause(long idPossibleCause) {
		this.idPossibleCause = idPossibleCause;
	}

	public String getDescPossibleCause() {
		return descPossibleCause;
	}

	public void setDescPossibleCause(String descPossibleCause) {
		this.descPossibleCause = descPossibleCause;
	}

	public void cleanForm(){
        setIdPossibleCause(0);
        setDescPossibleCause("");
    }

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	
}