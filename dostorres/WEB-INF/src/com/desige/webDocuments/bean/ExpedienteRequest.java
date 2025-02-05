package com.desige.webDocuments.bean;

import java.sql.SQLException;

import sun.jdbc.rowset.CachedRowSet;

public class ExpedienteRequest {
	
	private String numgen;
	private String namedocument;
	private String mayorver;
	private String minorver;
	private String prefix;
	private String number;
	private String type;
	private String owner;
	private String orden;
	private String nombres;
	private String apellidos;
	private String typedoc;
	private String statu;
	private String statuVer;
	private String dateExpiresDrafts;
	private String dateExpires;
	private String nameFile;
	private String folder;
	private String numver;
	private String toForFiles;
	private boolean active;
	private String realVer;
	private boolean pdfGenerated;

	public ExpedienteRequest() {
		
	}

	public ExpedienteRequest(CachedRowSet item) throws SQLException {
		this.numgen = item.getString("numgen");
		this.namedocument = item.getString("namedocument");
		this.mayorver = item.getString("mayorver");
		this.minorver = item.getString("minorver");
		this.prefix = item.getString("prefix");
		this.number = item.getString("number");
		this.type = item.getString("type");
		this.owner = item.getString("owner");
		this.orden = item.getString("orden");
		this.nombres = item.getString("nombres");
		this.apellidos = item.getString("apellidos");
		this.typedoc = item.getString("typedoc");
		this.statu = item.getString("statu");
		this.statuVer = item.getString("statuVer");
		this.dateExpiresDrafts = item.getString("dateExpiresDrafts");
		this.dateExpires = item.getString("dateExpires");
		this.nameFile = item.getString("nameFile");
		this.folder = item.getString("folder");
		this.numver = item.getString("numver");
		this.toForFiles = item.getString("toForFiles");
		this.active = item.getBoolean("active");
		this.realVer = item.getString("realVer");
		this.pdfGenerated = false;
	}
	public String getNumgen() {
		return numgen;
	}
	public void setNumgen(String numgen) {
		this.numgen = numgen;
	}
	public String getNamedocument() {
		return namedocument;
	}
	public void setNamedocument(String namedocument) {
		this.namedocument = namedocument;
	}
	public String getMayorver() {
		return mayorver;
	}
	public void setMayorver(String mayorver) {
		this.mayorver = mayorver;
	}
	public String getMinorver() {
		return minorver;
	}
	public void setMinorver(String minorver) {
		this.minorver = minorver;
	}
	public String getPrefix() {
		return prefix;
	}
	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}
	public String getNumber() {
		return number;
	}
	public void setNumber(String number) {
		this.number = number;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getOwner() {
		return owner;
	}
	public void setOwner(String owner) {
		this.owner = owner;
	}
	public String getOrden() {
		return orden;
	}
	public void setOrden(String orden) {
		this.orden = orden;
	}
	public String getNombres() {
		return nombres;
	}
	public void setNombres(String nombres) {
		this.nombres = nombres;
	}
	public String getApellidos() {
		return apellidos;
	}
	public void setApellidos(String apellidos) {
		this.apellidos = apellidos;
	}
	public String getTypedoc() {
		return typedoc;
	}
	public void setTypedoc(String typedoc) {
		this.typedoc = typedoc;
	}
	public String getStatu() {
		return statu;
	}
	public void setStatu(String statu) {
		this.statu = statu;
	}
	public String getStatuVer() {
		return statuVer;
	}
	public void setStatuVer(String statuVer) {
		this.statuVer = statuVer;
	}
	public String getDateExpiresDrafts() {
		return dateExpiresDrafts;
	}
	public void setDateExpiresDrafts(String dateExpiresDrafts) {
		this.dateExpiresDrafts = dateExpiresDrafts;
	}
	public String getDateExpires() {
		return dateExpires;
	}
	public void setDateExpires(String dateExpires) {
		this.dateExpires = dateExpires;
	}
	public String getNameFile() {
		return nameFile;
	}
	public void setNameFile(String nameFile) {
		this.nameFile = nameFile;
	}
	public String getFolder() {
		return folder;
	}
	public void setFolder(String folder) {
		this.folder = folder;
	}
	public String getNumver() {
		return numver;
	}
	public void setNumver(String numver) {
		this.numver = numver;
	}
	public String getToForFiles() {
		return toForFiles;
	}
	public void setToForFiles(String toForFiles) {
		this.toForFiles = toForFiles;
	}
	public boolean isActive() {
		return active;
	}
	public void setActive(boolean active) {
		this.active = active;
	}
	public String getRealVer() {
		return realVer;
	}
	public void setRealVer(String realVer) {
		this.realVer = realVer;
	}
	public boolean isPdfGenerated() {
		return pdfGenerated;
	}
	public void setPdfGenerated(boolean pdfGenerated) {
		this.pdfGenerated = pdfGenerated;
	}

	
	
}
