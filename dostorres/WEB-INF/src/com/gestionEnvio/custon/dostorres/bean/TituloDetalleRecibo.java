package com.gestionEnvio.custon.dostorres.bean;

public class TituloDetalleRecibo {

	private String tituloCentrado;
	private String subTituloEsquina;
	private String columna1;
	private String columna2;
	private String columna3;
	private String columna4;
	private String columna5;
	private String columna6;
	
	public TituloDetalleRecibo() {
		
	}
	
	public TituloDetalleRecibo(String tituloCentrado,String subTituloEsquina,String columna1, String columna2, String columna3, String columna4,String columna5, String columna6) {
		setTituloCentrado(tituloCentrado);
		setSubTituloEsquina(subTituloEsquina);
		setColumna1(columna1);
		setColumna2(columna2);
		setColumna3(columna3);
		setColumna4(columna4);
		setColumna5(columna5);
		setColumna6(columna6);
	
	}
	
	public TituloDetalleRecibo(String columna1, String columna2, String columna3, String columna4,String columna5, String columna6) {
		setColumna1(columna1);
		setColumna2(columna2);
		setColumna3(columna3);
		setColumna4(columna4);
		setColumna5(columna5);
		setColumna6(columna6);
	
	}
	
	
	public String getTituloCentrado() {
		return tituloCentrado;
	}
	public void setTituloCentrado(String tituloCentrado) {
		this.tituloCentrado = tituloCentrado;
	}
	
	public String getSubTituloEsquina() {
		return subTituloEsquina;
	}
	public void setSubTituloEsquina(String subTituloEsquina) {
		this.subTituloEsquina = subTituloEsquina;
	}

	
	public String getColumna1() {
		return columna1;
	}
	public void setColumna1(String columna1) {
		this.columna1 = columna1;
	}

	
	public String getColumna2() {
		return columna2;
	}
	public void setColumna2(String columna2) {
		this.columna2 = columna2;
	}
	
	
	public String getColumna3() {
		return columna3;
	}
	public void setColumna3(String columna3) {
		this.columna3 = columna3;
	}
	
	
	public String getColumna4() {
		return columna4;
	}
	public void setColumna4(String columna4) {
		this.columna4 = columna4;
	}
	
	
	public String getColumna5() {
		return columna5;
	}
	public void setColumna5(String columna5) {
		this.columna5 = columna5;
	}
	
	public String getColumna6() {
		return columna6;
	}
	public void setColumna6(String columna6) {
		this.columna6 = columna6;
	}
	


}
