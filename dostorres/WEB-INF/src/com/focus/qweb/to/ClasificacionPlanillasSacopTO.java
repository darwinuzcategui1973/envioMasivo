package com.focus.qweb.to;

import com.desige.webDocuments.sacop.forms.ClasificacionPlanillasSacop;
import com.desige.webDocuments.utils.ToolsHTML;
import com.focus.qweb.interfaces.ObjetoTO;

public class ClasificacionPlanillasSacopTO implements ObjetoTO {

	private static final long serialVersionUID = 1L;

	private String id; 
	private String descripcion;
	private String active;
	
	public ClasificacionPlanillasSacopTO() {
		
	}
	
	public ClasificacionPlanillasSacopTO(ClasificacionPlanillasSacop o) {
		this.id = String.valueOf(o.getId());
		this.descripcion = o.getDescripcion();
		this.active = String.valueOf(o.getActive());
	}


	public String getId() {
		return id;
	}

	public int getIdInt() {
		return ToolsHTML.parseInt(id);
	}

	public void setId(String id) {
		this.id = id;
	}




	public String getDescripcion() {
		return descripcion;
	}




	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
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
		builder.append("ClasificacionPlanillasSacopTO [");
		builder.append("  id=").append(id);
		builder.append(", descripcion=").append(descripcion);
		builder.append(", active=").append(active);
		builder.append("]");

		return builder.toString();
	}

}
