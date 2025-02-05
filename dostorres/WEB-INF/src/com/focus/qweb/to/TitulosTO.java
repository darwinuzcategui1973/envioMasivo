package com.focus.qweb.to;

import com.desige.webDocuments.utils.ToolsHTML;
import com.focus.qweb.interfaces.ObjetoTO;

public class TitulosTO implements ObjetoTO {

	private static final long serialVersionUID = 1L;

	private String posicion;
	private String titulo;

	public String getPosicion() {
		return posicion;
	}

	public int getPosicionInt() {
		return ToolsHTML.parseInt(posicion);
	}

	public void setPosicion(String posicion) {
		this.posicion = posicion;
	}

	public String getTitulo() {
		return titulo;
	}

	public void setTitulo(String titulo) {
		this.titulo = titulo;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("TitulosTO [posicion=").append(posicion)
				.append(", titulo=").append(titulo).append("]");
		return builder.toString();
	}

}
