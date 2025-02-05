package com.desige.webDocuments.utils.beans;

import java.io.Serializable;

public class DocOfRejection implements Serializable {

	private String datecreate;
	private String idflexflow;
	private String iduser;
	private String numgen;
	private String documentreject;
	
	// estos attributos no son campos de la tabla
	private String numver;

	public String getDatecreate() {
		return datecreate;
	}

	public void setDatecreate(String datecreate) {
		this.datecreate = datecreate;
	}

	public String getDocumentreject() {
		return documentreject;
	}

	public void setDocumentreject(String documentreject) {
		this.documentreject = documentreject;
	}

	public String getIdflexflow() {
		return idflexflow;
	}

	public void setIdflexflow(String idflexflow) {
		this.idflexflow = idflexflow;
	}

	public String getIduser() {
		return iduser;
	}

	public void setIduser(String iduser) {
		this.iduser = iduser;
	}

	public String getNumgen() {
		return numgen;
	}

	public void setNumgen(String numgen) {
		this.numgen = numgen;
	}

	public String getNumver() {
		return numver;
	}

	public void setNumver(String numver) {
		this.numver = numver;
	}

}
