package com.focus.qweb.bean;

import java.util.ArrayList;

import org.xml.sax.SAXException;

import com.focus.util.Archivo;

public class XMLReader implements Reader {

	private String[][] data = new String[0][0];
	private String[] header = new String[0];

	public void load(String fileName) {
		LectorXML lector;
		try {
			lector = new LectorXML();
			lector.leer(fileName);
			
			ArrayList lista = lector.getData(); 
			data = new String[lector.getData().size()][];
			for (int i = 0; i < lista.size(); i++) {
				data[i] = String.valueOf(lista.get(i)).split(";");
			}
	
			header = new String[lector.getDataHeader().size()];
			for (int i = 0; i < header.length; i++) {
				header[i] = String.valueOf(lector.getDataHeader().get(i));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public int getColumnsCount() {
		try {
			return data[0].length;
		} catch(ArrayIndexOutOfBoundsException e) {
			return 0;
		}
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
		for (int i = 0; i < data[row].length; i++) {
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