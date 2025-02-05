package com.focus.qweb.bean;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;

import com.desige.webDocuments.utils.ToolsHTML;

public class ExcelReaderComplete implements Reader {

	private String[][] data = new String[0][0];
	private Date[][] dataDate = new Date[0][0];
	private String[] header = new String[0];
	private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

	public void load(String fileName) {

		// EL sistema de archivo de poi de donde se va a cargar el libro de
		// excel
		POIFSFileSystem fs;
		try {
			fs = new POIFSFileSystem(new FileInputStream(fileName));
		} catch (FileNotFoundException e) {
			//System.out.println("El archivo no existe");
			return;
		} catch (IOException e) {
			//System.out.println("error procesando el archivo");
			e.printStackTrace();
			return;
		}

		// Cargo el libro de excel.
		HSSFWorkbook wb;
		try {
			wb = new HSSFWorkbook(fs);
		} catch (IOException e) {
			//System.out.println("Error cargando el archivo EXCEL");
			e.printStackTrace();
			return;
		}

		// Obtengo la Hoja de Excel.
		HSSFSheet hoja = wb.getSheetAt(0);

		// Declaro las variables a usar.
		HSSFRow filaActual = null;

		int numeroFilaActual = -1;
		int totalColumnas = 0;
		int totalFilas = 0;
		while (true) {
			numeroFilaActual++;

			filaActual = hoja.getRow(numeroFilaActual);
			//if (filaActual == null)
			//	break;
			if(numeroFilaActual > 500)
				break;

			if (numeroFilaActual == 0) {
				for (int i = 0; i < 100; i++) {
					try {
						String valor = filaActual.getCell((short) totalColumnas).getStringCellValue();
						if (ToolsHTML.isEmptyOrNull(valor)) {
							break;
						}
						totalColumnas++;
					} catch (java.lang.NullPointerException ex) {
						break;
					}
				}
			}
			totalFilas++;
		}
		
		totalColumnas=22; // NUMERO FIJO DE COLUMNAS;

		data = new String[totalFilas][totalColumnas];
		dataDate = new Date[totalFilas][totalColumnas];
		header = new String[totalColumnas];
		for (int i = 0; i < totalFilas; i++) {
			filaActual = hoja.getRow(i);
			if(filaActual==null) 
				continue;
			for (int k = 0; k < totalColumnas; k++) {
				HSSFCell cell = filaActual.getCell((short) k);
				
				if (i == 0) {
					if(cell!=null) {
						header[k] = cell.getStringCellValue();
					} else {
						header[k] = "Titulo ".concat(String.valueOf(k));
					}
				}
				

				if(cell==null) {
					data[i][k] = "";
				} else if (cell.getCellType() == HSSFCell.CELL_TYPE_STRING) {
					data[i][k] = cell.getStringCellValue();
				} else if (cell.getCellType() == HSSFCell.CELL_TYPE_BLANK) {
					data[i][k] = "";
				} else if (cell.getCellType() == HSSFCell.CELL_TYPE_BOOLEAN) {
					data[i][k] = String.valueOf(cell.getBooleanCellValue());
				} else if (cell.getCellType() == HSSFCell.CELL_TYPE_FORMULA) {
					//data[i][k] = String.valueOf(cell.getCellFormula());
					data[i][k] = String.valueOf(cell.getNumericCellValue());
				} else if (cell.getCellType() == HSSFCell.CELL_TYPE_NUMERIC) {
					try {
						if (HSSFDateUtil.isCellDateFormatted(cell)) {
							if (cell.getCellStyle().getDataFormat() == (short) 0) {
								data[i][k] = String.valueOf((int) (cell.getNumericCellValue()));
							} else {
								Date pub = cell.getDateCellValue();
								data[i][k] = dateFormat.format(pub);
							}
						} else {
							data[i][k] = String.valueOf((int) (cell.getNumericCellValue()));
						}
					} catch (java.lang.NumberFormatException e) {
						data[i][k] = String.valueOf((int) (cell.getNumericCellValue()));
					}
				} else {
					data[i][k] = cell.getStringCellValue();
				}
				if(cell!=null) {
					System.out.println("(" + i + "-" + k + ") : " + cell.getCellType() + " -> " + data[i][k] + " - " + cell.getCellStyle().getDataFormat() + " -> ");
				} else {
					System.out.println("(" + i + "-" + k + ") : Null -> " + data[i][k] + " - Null -> ");
				}

			}
		}
		wb = null;
		System.gc();
	}

	public int getColumnsCount() {
		return (data != null && data.length > 0 ? data[0].length : 0);
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
		return 1;
	}

}
