package com.desige.webDocuments.cargo.forms;

import java.io.Serializable;

import com.desige.webDocuments.area.forms.Area;
import com.desige.webDocuments.utils.beans.SuperActionForm;
import com.focus.qweb.to.CargoTO;

/**
 * Created by IntelliJ IDEA. User: srodriguez Date: 02-ago-2006 Time: 14:23:02
 * To change this template use File | Settings | File Templates.
 */
public class Cargo extends SuperActionForm implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -7018304610193855320L;
	private long idarea;
	private long idcargo;
	private String cargo;
	private byte active;
	private String id;
	private Area area;

	public Cargo() {
		
	}
	
	public Cargo(CargoTO o) {
		setIdarea(Long.parseLong(o.getIdArea()));
		setIdcargo(Long.parseLong(o.getIdCargo()));
		setCargo(o.getCargo());
		setActive(Byte.parseByte(o.getActivec()));
		setId(o.getIdCargo());
		setArea(null);
	}

	public void load(CargoTO o) {
		setIdarea(Long.parseLong(o.getIdArea()));
		setIdcargo(Long.parseLong(o.getIdCargo()));
		setCargo(o.getCargo());
		setActive(Byte.parseByte(o.getActivec()));
		setId(o.getIdCargo());
		setArea(null);
	}

	public Area getArea() {
		return area;
	}

	public void setArea(Area area) {
		this.area = area;
	}

	public void cleanForm() {
		// idarea=0;
		idcargo = 0;
		cargo = "";
		active = 0;

	}

	public byte getActive() {
		return active;
	}

	public void setActive(byte active) {
		this.active = active;
	}

	public String getCargo() {
		return cargo;
	}

	public void setCargo(String cargo) {
		this.cargo = cargo;
	}

	public long getIdarea() {
		return idarea;
	}

	public void setIdarea(long idarea) {
		this.idarea = idarea;
	}

	public long getIdcargo() {
		return idcargo;
	}

	public void setIdcargo(long idcargo) {
		this.idcargo = idcargo;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
 
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Cargo [idarea=").append(idarea).append(", idcargo=")
				.append(idcargo).append(", cargo=").append(cargo)
				.append(", active=").append(active).append(", id=").append(id)
				.append(", area=").append(area).append("]");
		return builder.toString();
	}

}
