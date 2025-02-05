package com.focus.qweb.to;

import com.focus.qweb.interfaces.ObjetoTO;

public class PaisTO implements ObjetoTO {

	private static final long serialVersionUID = 1L;

	private String id; // CodPais
	private String name; // Nombre
	private String codigo; // Codigo


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

	public String getCodigo() {
		return codigo;
	}


	public void setCodigo(String codigo) {
		this.codigo = codigo;
	}


	@Override
	public String toString() {

		StringBuilder builder = new StringBuilder();
		builder.append("PaisTO [");
		builder.append("  id=").append(id);
		builder.append(", name=").append(name);
		builder.append(", codigo=").append(codigo);
		builder.append("]");

		return builder.toString();
	}

}
