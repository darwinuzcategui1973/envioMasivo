package com.desige.webDocuments.to;

import java.text.ParseException;

import com.desige.webDocuments.utils.ToolsHTML;

public class PreviewTO {
	
	private String idDocument;
	private String idVersion;
	private String idUsuario;
	private String contador;
	private String registra;
	private String actualiza;
	
	// attributos que no estan en la tabla
	private String mayorVer;
	private String minorVer;
	private String nameUser;
	
	public PreviewTO() {
		
	}

	public String getActualiza() {
		return actualiza;
	}

	public void setActualiza(String actualiza) {
		this.actualiza = actualiza;
	}

	public String getContador() {
		return contador;
	}

	public void setContador(String contador) {
		this.contador = contador;
	}

	public String getIdDocument() {
		return idDocument;
	}

	public void setIdDocument(String idDocument) {
		this.idDocument = idDocument;
	}

	public String getIdUsuario() {
		return idUsuario;
	}

	public void setIdUsuario(String idUsuario) {
		this.idUsuario = idUsuario;
	}

	public String getIdVersion() {
		return idVersion;
	}

	public void setIdVersion(String idVersion) {
		this.idVersion = idVersion;
	}

	public String getRegistra() {
		return registra;
	}

	public String getRegistraFormat() {
		try {
			return ToolsHTML.sdfShow.format(ToolsHTML.sdfShowConvert1.parse(registra));
		} catch (Exception e) {
			return "";
		}
	}

	public String getActualizaFormat() {
		try {
			return ToolsHTML.sdfShow.format(ToolsHTML.sdfShowConvert1.parse(actualiza));
		} catch (Exception e) {
			return "";
		}
	}
	
	public void setRegistra(String registra) {
		this.registra = registra;
	}

	public String getMayorVer() {
		return mayorVer;
	}

	public void setMayorVer(String mayorVer) {
		this.mayorVer = mayorVer;
	}

	public String getMinorVer() {
		return minorVer;
	}

	public void setMinorVer(String minorVer) {
		this.minorVer = minorVer;
	}

	public String getNameUser() {
		return nameUser;
	}

	public void setNameUser(String nameUser) {
		this.nameUser = nameUser;
	}

}
