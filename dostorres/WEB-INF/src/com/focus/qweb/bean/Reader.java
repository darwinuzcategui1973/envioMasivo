package com.focus.qweb.bean;

public interface Reader {
	
	public void load(String fileName);
	
	public int getRowsCount();
	public int getColumnsCount();
	public String getValue(int row, int col);
	
	public String[] getHeaders();
	public String[] getRow(int row);
	public String getRowString(int row);
	public int getRowBegin();
	
}
