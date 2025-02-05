package com.desige.webDocuments.document.actions;

public class TotalTablaDetalle {
	private String titulo;
	private String columna1;
	private String columna2;
	private String columna3;
	private String columna4;
	private String columna5;
	
	public TotalTablaDetalle() {
		//titulo="titulo1";
		//columna1="columna1";
		//columna2="columna2";
		
		
	}
	
	public TotalTablaDetalle(String titulo, String columna1, String columna2, String columna3,String columna4) {
		setTitulo(titulo);
		setColumna1(columna1);
		setColumna2(columna2);
		setColumna3(columna3);
		setColumna4(columna4);
	}
	
	public TotalTablaDetalle(String titulo, String columna1, String columna2, String columna3,String columna4,String columna5) {
		setTitulo(titulo);
		setColumna1(columna1);
		setColumna2(columna2);
		setColumna3(columna3);
		setColumna4(columna4);
		setColumna5(columna5);
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
	public String getTitulo() {
		return titulo;
	}
	public void setTitulo(String titulo) {
		this.titulo = titulo;
	}
	
	
	
	
}
