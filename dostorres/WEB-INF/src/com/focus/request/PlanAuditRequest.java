package com.focus.request;

public class PlanAuditRequest {

	private String idPlanAudit;
	private String namePlan;
	private String idProgramAudit;
	private String idNorm;
	
	public PlanAuditRequest(String idPlanAudit, String namePlan, String idProgramAudit, String idNorm) {
		super();
		this.idPlanAudit = idPlanAudit;
		this.namePlan = namePlan;
		this.idProgramAudit = idProgramAudit;
		this.idNorm = idNorm;
	}

	public String getIdPlanAudit() {
		return idPlanAudit;
	}

	public void setIdPlanAudit(String idPlanAudit) {
		this.idPlanAudit = idPlanAudit;
	}

	public String getNamePlan() {
		return namePlan;
	}

	public void setNamePlan(String namePlan) {
		this.namePlan = namePlan;
	}

	public String getIdProgramAudit() {
		return idProgramAudit;
	}

	public void setIdProgramAudit(String idProgramAudit) {
		this.idProgramAudit = idProgramAudit;
	}

	public String getIdNorm() {
		return idNorm;
	}

	public void setIdNorm(String idNorm) {
		this.idNorm = idNorm;
	}
	
}
