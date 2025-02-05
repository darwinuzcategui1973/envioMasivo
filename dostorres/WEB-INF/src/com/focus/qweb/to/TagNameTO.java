package com.focus.qweb.to;

import com.desige.webDocuments.utils.ToolsHTML;
import com.focus.qweb.interfaces.ObjetoTO;

public class TagNameTO implements ObjetoTO {

	private static final long serialVersionUID = 1L;

	private String idTagName2;
	private String tagName;
	private String active;
	private String idTipo;


	public String getIdTagName2() {
		return idTagName2;
	}


	public int getIdTagName2Int() {
		return ToolsHTML.parseInt(idTagName2);
	}


	public void setIdTagName2(String idTagName2) {
		this.idTagName2 = idTagName2;
	}



	public String getTagName() {
		return tagName;
	}



	public void setTagName(String tagName) {
		this.tagName = tagName;
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



	public String getIdTipo() {
		return idTipo;
	}



	public void setIdTipo(String idTipo) {
		this.idTipo = idTipo;
	}



	@Override
	public String toString() {

		StringBuilder builder = new StringBuilder();
		builder.append("TagNameTO [");
		builder.append("  idTagName2=").append(idTagName2);
		builder.append(", tagName=").append(tagName);
		builder.append(", active=").append(active);
		builder.append(", idTipo=").append(idTipo);
		builder.append("]");

		return builder.toString();
	}

}
