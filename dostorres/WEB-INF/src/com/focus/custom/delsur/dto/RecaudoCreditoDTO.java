package com.focus.custom.delsur.dto;

/**
 * 
 * @author felipe.rojas
 *
 */
public class RecaudoCreditoDTO {
	private int codigoRecaudo;
	private int codigoDocumento;
	private String nombreDocumento;
	private String idNode;
	
	public RecaudoCreditoDTO() {
		// TODO Auto-generated constructor stub
	}

	public int getCodigoRecaudo() {
		return codigoRecaudo;
	}

	public void setCodigoRecaudo(int codigoRecaudo) {
		this.codigoRecaudo = codigoRecaudo;
	}

	public int getCodigoDocumento() {
		return codigoDocumento;
	}

	public void setCodigoDocumento(int codigoDocumento) {
		this.codigoDocumento = codigoDocumento;
	}

	public String getNombreDocumento() {
		return nombreDocumento;
	}

	public void setNombreDocumento(String nombreDocumento) {
		this.nombreDocumento = nombreDocumento;
	}

	public String getIdNode() {
		return idNode;
	}
	
	public void setIdNode(String idNode) {
		this.idNode = idNode;
	}
	
	@Override
	public String toString() {
		return "RecaudoCreditoDTO [codigoRecaudo=" + codigoRecaudo
				+ ", codigoDocumento=" + codigoDocumento
				+ ", nombreDocumento=" + nombreDocumento
				+ ", idNode=" + idNode + "]";
	}
}
