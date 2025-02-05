package com.focus.qweb.to;

import com.desige.webDocuments.utils.ToolsHTML;
import com.focus.qweb.interfaces.ObjetoTO;

public class ConfTagNameTO implements ObjetoTO {

	private static final long serialVersionUID = 1L;

	private String idTagName;
	private String tipoTag;
	private String tipoAlarma;
	private String valor;
	private String active;


	public String getIdTagName() {
		return idTagName;
	}

	public int getIdTagNameInt() {
		return ToolsHTML.parseInt(idTagName);
	}

	public void setIdTagName(String idTagName) {
		this.idTagName = idTagName;
	}


	public String getTipoTag() {
		return tipoTag;
	}


	public void setTipoTag(String tipoTag) {
		this.tipoTag = tipoTag;
	}


	public String getTipoAlarma() {
		return tipoAlarma;
	}


	public void setTipoAlarma(String tipoAlarma) {
		this.tipoAlarma = tipoAlarma;
	}


	public String getValor() {
		return valor;
	}


	public void setValor(String valor) {
		this.valor = valor;
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
		builder.append("ConfTagNameTO [");
		builder.append("  idTagName=").append(idTagName);
		builder.append(", tipoTag=").append(tipoTag);
		builder.append(", tipoAlarma=").append(tipoAlarma);
		builder.append(", valor=").append(valor);
		builder.append(", active=").append(active);
		builder.append("]");

		return builder.toString();
	}

}
