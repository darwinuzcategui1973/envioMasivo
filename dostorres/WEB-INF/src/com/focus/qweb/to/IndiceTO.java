package com.focus.qweb.to;

import com.focus.qweb.interfaces.ObjetoTO;

public class IndiceTO implements ObjetoTO {

	private static final long serialVersionUID = 1L;

	private String clave;
	private String valor;
	private String indice;
	
	public String getClave() {
		return clave;
	}
	public void setClave(String clave) {
		this.clave = clave;
	}
	public String getValor() {
		return valor;
	}
	public void setValor(String valor) {
		this.valor = valor;
	}
	public String getIndice() {
		return indice;
	}
	public void setIndice(String indice) {
		this.indice = indice;
	}
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("IndiceTO [clave=").append(clave).append(", valor=")
				.append(valor).append(", indice=").append(indice).append("]");
		return builder.toString();
	}
	
	
	
	
}
