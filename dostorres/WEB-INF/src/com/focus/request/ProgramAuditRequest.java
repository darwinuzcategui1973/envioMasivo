package com.focus.request;

public class ProgramAuditRequest {

	private String idProgramAudit;
	private String nameProgram;
	private String idNorm;
	
	public ProgramAuditRequest(String idProgramAudit, String nameProgram, String idNorm) {
		super();
		this.idProgramAudit = idProgramAudit;
		this.nameProgram = nameProgram;
		this.idNorm = idNorm;
	}

	public String getIdProgramAudit() {
		return idProgramAudit;
	}

	public void setIdProgramAudit(String idProgramAudit) {
		this.idProgramAudit = idProgramAudit;
	}

	public String getNameProgram() {
		return nameProgram;
	}

	public void setNameProgram(String nameProgram) {
		this.nameProgram = nameProgram;
	}

	public String getIdNorm() {
		return idNorm;
	}

	public void setIdNorm(String idNorm) {
		this.idNorm = idNorm;
	}
	
	

}
