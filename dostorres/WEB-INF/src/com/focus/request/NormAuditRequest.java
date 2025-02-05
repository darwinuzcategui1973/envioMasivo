package com.focus.request;

public class NormAuditRequest {
	
	private String idNormAudit;
	private String nameNorma;
	private String idNormIndiceCero;
	
	
	public NormAuditRequest(String idNormAudit, String nameNorma, String idNormIndiceCero) {
		super();
		this.idNormAudit = idNormAudit;
		this.nameNorma = nameNorma;
		this.idNormIndiceCero = idNormIndiceCero;
	}
	public String getIdNormAudit() {
		return idNormAudit;
	}
	public void setIdNormAudit(String idNormAudit) {
		this.idNormAudit = idNormAudit;
	}
	public String getNameNorma() {
		return nameNorma;
	}
	public void setNameNorma(String nameNorma) {
		this.nameNorma = nameNorma;
	}
	public String getidNormIndiceCero() {
		return idNormIndiceCero;
	}
	public void setidNormIndiceCero(String idNormIndiceCero) {
		this.idNormIndiceCero = idNormIndiceCero;
	}
	

}
