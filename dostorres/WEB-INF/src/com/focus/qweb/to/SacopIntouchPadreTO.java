package com.focus.qweb.to;

import com.desige.webDocuments.utils.ToolsHTML;
import com.focus.qweb.interfaces.ObjetoTO;

public class SacopIntouchPadreTO implements ObjetoTO {

	private static final long serialVersionUID = 1L;

	private String idSacopIntouchPadre;
	private String idPlanillaSacop1;
	private String active;
	private String enable;

	
	public String getIdSacopIntouchPadre() {
		return idSacopIntouchPadre;
	}

	public int getIdSacopIntouchPadreInt() {
		return ToolsHTML.parseInt(idSacopIntouchPadre);
	}

	public void setIdSacopIntouchPadre(String idSacopIntouchPadre) {
		this.idSacopIntouchPadre = idSacopIntouchPadre;
	}


	public String getIdPlanillaSacop1() {
		return idPlanillaSacop1;
	}

	public int getIdPlanillaSacop1Int() {
		return ToolsHTML.parseInt(idPlanillaSacop1);
	}

	public void setIdPlanillaSacop1(String idPlanillaSacop1) {
		this.idPlanillaSacop1 = idPlanillaSacop1;
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


	public String getEnable() {
		return enable;
	}

	public int getEnableInt() {
		return ToolsHTML.parseInt(enable);
	}


	public void setEnable(String enable) {
		this.enable = enable;
	}


	@Override
	public String toString() {

		StringBuilder builder = new StringBuilder();
		builder.append("SacopIntouchPadreTO [");
		builder.append("  idSacopIntouchPadre=").append(idSacopIntouchPadre);
		builder.append(", idPlanillaSacop1=").append(idPlanillaSacop1);
		builder.append(", active=").append(active);
		builder.append(", enable=").append(enable);
		builder.append("]");

		return builder.toString();
	}

}
