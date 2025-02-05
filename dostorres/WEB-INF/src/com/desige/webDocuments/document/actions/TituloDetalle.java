package com.desige.webDocuments.document.actions;

public class TituloDetalle {

	private String titulo;
	private String columna1;
	private String columna2;
	
	public TituloDetalle() {
		titulo="titulo1";
		columna1="columna1";
		columna2="columna2";
		
		
	}
	
	public TituloDetalle(String titulo, String columna1, String columna2) {
		setTitulo(titulo);
		setColumna1(columna1);
		setColumna2(columna2);
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
	public String getTitulo() {
		return titulo;
	}
	public void setTitulo(String titulo) {
		this.titulo = titulo;
	}
	
	
	
	
}
