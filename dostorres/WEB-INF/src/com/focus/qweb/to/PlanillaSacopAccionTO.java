package com.focus.qweb.to;

import com.desige.webDocuments.utils.ToolsHTML;
import com.focus.qweb.interfaces.ObjetoTO;

public class PlanillaSacopAccionTO implements ObjetoTO {

	private static final long serialVersionUID = 1L;

	private String idplanillasacopaccion;
	private String idplanillasacop1;
	private String accion;
	private String fecha;
	private String responsables;
	private String active;

	public String getIdplanillasacopaccion() {
		return idplanillasacopaccion;
	}

	public int getIdplanillasacopaccionInt() {
		return ToolsHTML.parseInt(idplanillasacopaccion);
	}

	public void setIdplanillasacopaccion(String idplanillasacopaccion) {
		this.idplanillasacopaccion = idplanillasacopaccion;
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



	public String getAccion() {
		return accion;
	}



	public void setAccion(String accion) {
		this.accion = accion;
	}



	public String getFecha() {
		return fecha;
	}



	public void setFecha(String fecha) {
		this.fecha = fecha;
	}



	public String getResponsables() {
		return responsables;
	}



	public void setResponsables(String responsables) {
		this.responsables = responsables;
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
		builder.append("PlanillaSacopAccionTO [");
		builder.append("  idplanillasacopaccion=").append(idplanillasacopaccion);
		builder.append(", idplanillasacop1=").append(idplanillasacop1);
		builder.append(", accion=").append(accion);
		builder.append(", fecha=").append(fecha);
		builder.append(", responsables=").append(responsables);
		builder.append(", active=").append(active);
		builder.append("]");

		return builder.toString();
	}

}
