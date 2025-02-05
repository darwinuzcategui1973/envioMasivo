package com.focus.qweb.to;

import com.desige.webDocuments.sacop.forms.ProcesosSacop;
import com.desige.webDocuments.utils.ToolsHTML;
import com.focus.qweb.interfaces.ObjetoTO;

public class ProcesoSacopTO implements ObjetoTO {

	private static final long serialVersionUID = 1L;

	private String numproceso; // numproceso
	private String proceso; // proceso
	private String active; // active
	
	public ProcesoSacopTO() {
		
	}

	public ProcesoSacopTO(ProcesosSacop o) {
		this.numproceso = String.valueOf(o.getNumproceso());
		this.proceso = o.getProceso();
		this.active = String.valueOf(o.getActive());
	}

	public String getNumproceso() {
		return numproceso;
	}

	public int getNumprocesoInt() {
		return ToolsHTML.parseInt(numproceso);
	}


	public void setNumproceso(String numproceso) {
		this.numproceso = numproceso;
	}




	public String getProceso() {
		return proceso;
	}




	public void setProceso(String proceso) {
		this.proceso = proceso;
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
		builder.append("ProcesoSacopTO [");
		builder.append("  numproceso=").append(numproceso);
		builder.append(", proceso=").append(proceso);
		builder.append(", active=").append(active);
		builder.append("]");

		return builder.toString();
	}

}
