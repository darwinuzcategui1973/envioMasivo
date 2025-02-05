package com.focus.qweb.to;

import com.desige.webDocuments.sacop.forms.TitulosPlanillasSacop;
import com.desige.webDocuments.utils.ToolsHTML;
import com.focus.qweb.interfaces.ObjetoTO;

public class TitulosPlanillasSacopTO implements ObjetoTO {

	private static final long serialVersionUID = 1L;

	private String numtitulosplanillas;
	private String titulosplanillas;
	private String active;
	
	public TitulosPlanillasSacopTO() {
		
	}

	public TitulosPlanillasSacopTO(TitulosPlanillasSacop o) {
		this.numtitulosplanillas = String.valueOf(o.getNumtitulosplanillas());
		this.titulosplanillas = o.getTitulosplanillas();
		this.active = String.valueOf(o.getActive());
	}
	

	public String getNumtitulosplanillas() {
		return numtitulosplanillas;
	}

	public int getNumtitulosplanillasInt() {
		return ToolsHTML.parseInt(numtitulosplanillas);
	}


	public void setNumtitulosplanillas(String numtitulosplanillas) {
		this.numtitulosplanillas = numtitulosplanillas;
	}



	public String getTitulosplanillas() {
		return titulosplanillas;
	}



	public void setTitulosplanillas(String titulosplanillas) {
		this.titulosplanillas = titulosplanillas;
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
		builder.append("TitulosPlanillasSacopTO [");
		builder.append(", numtitulosplanillas=").append(numtitulosplanillas);
		builder.append(", titulosplanillas=").append(titulosplanillas);
		builder.append(", active=").append(active);
		builder.append("]");

		return builder.toString();
	}

}
