package com.desige.webDocuments.sacop.forms;

import java.io.Serializable;

import com.desige.webDocuments.utils.beans.SuperActionForm;

/**
 * Created by IntelliJ IDEA. User: Administrador Date: 05/04/2006 Time: 03:19:48 PM To change this template use File | Settings | File Templates.
 */

public class DeleteSacop extends SuperActionForm implements Serializable {
	private long id;
	private String idplanillasacop1;
	private String idperson;
	private String active;
	private String trackingSacop;
	
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getIdplanillasacop1() {
		return idplanillasacop1;
	}

	public void setIdplanillasacop1(String idplanillasacop1) {
		this.idplanillasacop1 = idplanillasacop1;
	}

	public String getIdperson() {
		return idperson;
	}

	public void setIdperson(String idperson) {
		this.idperson = idperson;
	}

	public String getActive() {
		return active;
	}

	public void setActive(String active) {
		this.active = active;
	}

	public String getTrackingSacop() {
		return trackingSacop;
	}

	public void setTrackingSacop(String trackingSacop) {
		this.trackingSacop = trackingSacop;
	}

}
