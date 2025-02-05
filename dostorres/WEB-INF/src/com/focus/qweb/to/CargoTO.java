package com.focus.qweb.to;

import com.desige.webDocuments.utils.ToolsHTML;
import com.focus.qweb.interfaces.ObjetoTO;

public class CargoTO implements ObjetoTO {

	private static final long serialVersionUID = 1L;

	private String idCargo;
	private String cargo;
	private String idArea;
	private String activec;

	private AreaTO area;


	public String getIdCargo() {
		return idCargo;
	}

	public int getIdCargoInt() {
		return ToolsHTML.parseInt(idCargo);
	}


	public void setIdCargo(String idCargo) {
		this.idCargo = idCargo;
	}



	public String getCargo() {
		return cargo;
	}



	public void setCargo(String cargo) {
		this.cargo = cargo;
	}



	public String getIdArea() {
		return idArea;
	}

	public int getIdAreaInt() {
		return ToolsHTML.parseInt(idArea);
	}


	public void setIdArea(String idArea) {
		this.idArea = idArea;
	}



	public String getActivec() {
		return activec;
	}

	public int getActivecInt() {
		return ToolsHTML.parseInt(activec);
	}


	public void setActivec(String activec) {
		this.activec = activec;
	}


	public AreaTO getArea() {
		return area;
	}



	public void setArea(AreaTO area) {
		this.area = area;
	}



	@Override
	public String toString() {

		StringBuilder builder = new StringBuilder();
		builder.append("CargoTO [");
		builder.append("  idCargo=").append(idCargo);
		builder.append("  cargo=").append(cargo);
		builder.append("  idArea=").append(idArea);
		builder.append("  activec=").append(activec);
		builder.append("]");

		return builder.toString();
	}

}
