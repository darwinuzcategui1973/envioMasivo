package com.focus.qweb.to;

import com.desige.webDocuments.utils.ToolsHTML;
import com.focus.qweb.interfaces.ObjetoTO;
import com.focus.wonderware.forms.ActiveFactory_frm;

public class ActiveFactoryTO implements ObjetoTO {

	private static final long serialVersionUID = 1L;

	private String idActivefactorydocument;
	private String descripcion;
	private String url;
	private String active;
	private String numgen;
	
	public ActiveFactoryTO() {
		
	}

	public ActiveFactoryTO(ActiveFactory_frm act) {
		idActivefactorydocument = String.valueOf(act.getIdactivefactorydocument());
		descripcion = act.getDescripcion();
		url = act.getUrl();
		active = Byte.toString(act.getActive());
		numgen = act.getNumgen();
	}
	
	
	public String getIdActivefactorydocument() {
		return idActivefactorydocument;
	}

	public int getIdActivefactorydocumentInt() {
		return ToolsHTML.parseInt(idActivefactorydocument);
	}



	public void setIdActivefactorydocument(String idActivefactorydocument) {
		this.idActivefactorydocument = idActivefactorydocument;
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




	public String getActive() {
		return active;
	}

	public int getActiveInt() {
		return ToolsHTML.parseInt(active);
	}



	public void setActive(String active) {
		this.active = active;
	}




	public String getNumgen() {
		return numgen;
	}




	public void setNumgen(String numgen) {
		this.numgen = numgen;
	}




	@Override
	public String toString() {

		StringBuilder builder = new StringBuilder();
		builder.append("ActiveFactoryTO [");
		builder.append("  idActivefactorydocument=").append(idActivefactorydocument);
		builder.append(", descripcion=").append(descripcion);
		builder.append(", url=").append(url);
		builder.append(", active=").append(active);
		builder.append(", numgen=").append(numgen);
		builder.append("]");

		return builder.toString();
	}

}
