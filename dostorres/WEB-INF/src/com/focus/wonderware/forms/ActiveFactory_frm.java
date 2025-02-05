package com.focus.wonderware.forms;

import java.io.Serializable;

import com.desige.webDocuments.utils.beans.SuperActionForm;
import com.focus.qweb.to.ActiveFactoryTO;

/**
 * Created by IntelliJ IDEA.
 * User: srodriguez
 * Date: Feb 15, 2007
 * Time: 9:45:10 AM
 * To change this template use File | Settings | File Templates.
 */
public class ActiveFactory_frm extends SuperActionForm implements Serializable {
	private long idactivefactorydocument;
	private String numgen;
	private String id;
	private String descripcion;
	private String url;
	private byte active;
	
	public ActiveFactory_frm() {
		
	}
	
	public ActiveFactory_frm(ActiveFactoryTO oActiveFactoryTO) {
		idactivefactorydocument = Long.parseLong(oActiveFactoryTO.getIdActivefactorydocument());
		numgen = oActiveFactoryTO.getNumgen();
		id = oActiveFactoryTO.getIdActivefactorydocument();
		descripcion = oActiveFactoryTO.getDescripcion();
		url = oActiveFactoryTO.getUrl();
		active = Byte.parseByte(oActiveFactoryTO.getActive());
	}


	public void cleanForm() {
		setDescripcion("");
	}

	public long getIdactivefactorydocument() {
		return idactivefactorydocument;
	}

	public void setIdactivefactorydocument(long idactivefactorydocument) {
		this.idactivefactorydocument = idactivefactorydocument;
	}

	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public byte getActive() {
		return active;
	}

	public void setActive(byte active) {
		this.active = active;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getNumgen() {
		return numgen;
	}

	public void setNumgen(String numgen) {
		this.numgen = numgen;
	}

	public ActiveFactory_frm copiarA(ActiveFactory_frm or) {

		or.setIdactivefactorydocument(this.getIdactivefactorydocument());
		or.setNumgen(this.getNumgen());
		or.setDescripcion(this.getDescripcion());
		or.setUrl(this.getUrl());
		or.setActive(this.getActive());
		return or;
	}
}
