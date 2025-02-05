package com.focus.qweb.to;

import com.desige.webDocuments.utils.ToolsHTML;
import com.focus.qweb.interfaces.ObjetoTO;

public class DeleteSacopTO implements ObjetoTO {

	private static final long serialVersionUID = 1L;

	private String id;
	private String idplanillasacop1;
	private String idperson;
	private String active;

	
	public String getId() {
		return id;
	}

	public int getIdInt() {
		return ToolsHTML.parseInt(id);
	}

	public void setId(String id) {
		this.id = id;
	}


	public String getIdplanillasacop1() {
		return idplanillasacop1;
	}

	public int getIdplanillasacop1Int() {
		return ToolsHTML.parseInt(idplanillasacop1);
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

	public int getActiveInt() {
		return ToolsHTML.parseInt(active);
	}

	public void setActive(String active) {
		this.active = active;
	}


	@Override
	public String toString() {

		StringBuilder builder = new StringBuilder();
		builder.append("DeleteSacopTO [");
		builder.append("  id=").append(id);
		builder.append(", idplanillasacop1=").append(idplanillasacop1);
		builder.append(", idperson=").append(idperson);
		builder.append(", active=").append(active);
		builder.append("]");

		return builder.toString();
	}

}
