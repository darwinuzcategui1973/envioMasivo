package com.focus.qweb.to;

import com.desige.webDocuments.utils.ToolsHTML;
import com.focus.qweb.interfaces.ObjetoTO;
import com.focus.wonderware.intocuh_sacop.forms.Sacop_Intouchh_frm;

public class SacopIntouchHijoTO implements ObjetoTO {

	private static final long serialVersionUID = 1L;

	private String idTagName;
	private String tagName;
	private String disparadaSacop;
	private String active;
	private String idPlanillaSacop1;
	
	public SacopIntouchHijoTO() {
		
	}
	
	public SacopIntouchHijoTO(Sacop_Intouchh_frm scp) {
		idTagName = String.valueOf(scp.getIdtagname());
		tagName = scp.getTagname();
		disparadaSacop = Byte.toString(scp.getDisparadasacop());
		active = Byte.toString(scp.getActive());
		idPlanillaSacop1 = getIdPlanillaSacop1();
	}
	
	
	
	public String getIdTagName() {
		return idTagName;
	}

	public int getIdTagNameInt() {
		return ToolsHTML.parseInt(idTagName);
	}

	public void setIdTagName(String idTagName) {
		this.idTagName = idTagName;
	}


	public String getTagName() {
		return tagName;
	}


	public void setTagName(String tagName) {
		this.tagName = tagName;
	}


	public String getDisparadaSacop() {
		return disparadaSacop;
	}

	public int getDisparadaSacopInt() {
		return ToolsHTML.parseInt(disparadaSacop);
	}


	public void setDisparadaSacop(String disparadaSacop) {
		this.disparadaSacop = disparadaSacop;
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


	public String getIdPlanillaSacop1() {
		return idPlanillaSacop1;
	}

	public int getIdPlanillaSacop1Int() {
		return ToolsHTML.parseInt(idPlanillaSacop1);
	}

	public void setIdPlanillaSacop1(String idPlanillaSacop1) {
		this.idPlanillaSacop1 = idPlanillaSacop1;
	}


	@Override
	public String toString() {

		StringBuilder builder = new StringBuilder();
		builder.append("SacopIntouchHijoTO [");
		builder.append("  idTagName=").append(idTagName);
		builder.append(", tagName=").append(tagName);
		builder.append(", disparadaSacop=").append(disparadaSacop);
		builder.append(", active=").append(active);
		builder.append(", idPlanillaSacop1=").append(idPlanillaSacop1);
		builder.append("]");

		return builder.toString();
	}

}
