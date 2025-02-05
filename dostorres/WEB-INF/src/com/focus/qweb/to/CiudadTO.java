package com.focus.qweb.to;

import com.focus.qweb.interfaces.ObjetoTO;

public class CiudadTO implements ObjetoTO {

	private static final long serialVersionUID = 1L;

	private String id; // IdCiudad
	private String name; // Nombre


	public String getId() {
		return id;
	}


	public void setId(String id) {
		this.id = id;
	}


	public String getName() {
		return name;
	}


	public void setName(String name) {
		this.name = name;
	}


	@Override
	public String toString() {

		StringBuilder builder = new StringBuilder();
		builder.append("CiudadTO [");
		builder.append("  id=").append(id);
		builder.append(", name=").append(name);
		builder.append("]");

		return builder.toString();
	}

}
