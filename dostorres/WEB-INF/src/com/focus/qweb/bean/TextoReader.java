package com.focus.qweb.bean;

import java.util.ArrayList;

import com.focus.util.Archivo;

public class TextoReader implements Reader {

	private String[][] data = new String[0][0];
	private String[] header = new String[0];
	
	public void load(String fileName) {
		Archivo arc = new Archivo();
		ArrayList lista = arc.leerPorLinea(fileName);
		data = new String[lista.size()][];
		for(int i=0; i<lista.size();i++) {
			data[i] = String.valueOf(lista.get(i)).split(",");
		}
		
		header = new String[getColumnsCount()];
		for(int i=0; i<header.length;i++) {
			header[i] = "Columna ".concat(String.valueOf(i+1));
		}
	}

	public int getColumnsCount() {
		return data[0].length;
	}
	public int getRowsCount() {
		return data.length;
	}

	public String[] getHeaders() {
		return header;
	}

	public String[] getRow(int row) {
		return data[row];
	}

	public String getRowString(int row) {
		StringBuffer sb = new StringBuffer();
		String sep = "";
		String coma = ",";
		for(int i=0;i<data[row].length;i++) {
			sb.append(sep).append(data[row][i]);
			sep = coma;
		}
		return sb.toString();
	}

	public String getValue(int row, int col) {
		return data[row][col];
	}

	public int getRowBegin() {
		return 0;
	}
	
}
