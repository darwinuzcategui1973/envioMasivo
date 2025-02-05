package com.focus.qweb.to;

import java.io.Serializable;
import java.text.ParseException;
import java.util.Date;

import com.desige.webDocuments.accion.forms.PlanAudit;
import com.desige.webDocuments.utils.ToolsHTML;
import com.desige.webDocuments.utils.beans.SuperActionForm;
import com.focus.qweb.interfaces.ObjetoTO;

public class PlanAuditTO extends SuperActionForm implements Serializable,ObjetoTO {

	private static final long serialVersionUID = 1L;
	
	private String idPlanAudit;
	private String namePlan;
	private String idProgramAudit;
	private String idPersonPlan;
	private String typeAudit;
	private String dateFromPlan;
	private String dateUntilPlan;
	private String statusPlan;
	private String idNorm;
	private String idNormPlanCheck;
	private String dateCreatedPlan;
	private String idPersonCreatorPlan;

	public PlanAuditTO() {
		setIdPlanAudit("");
		setNamePlan("");
		setIdProgramAudit("");
		setIdPersonPlan("");
		setTypeAudit("");
		setDateFromPlan("");
		setDateUntilPlan("");
		setStatusPlan("");
		setIdNorm("");
		setIdNormPlanCheck("");
		setDateCreatedPlan("");
		setIdPersonCreatorPlan("");
    }
	public PlanAuditTO(String idPlanAudit) {
		super();
		this.idPlanAudit = idPlanAudit;
	}

	public PlanAuditTO(PlanAudit forma) {
		setIdPlanAudit(String.valueOf(forma.getIdPlanAudit()));
		setNamePlan(forma.getNamePlan());
		setIdProgramAudit(String.valueOf(forma.getIdProgramAudit()));
		setIdPersonPlan(String.valueOf(forma.getIdPersonPlan()));
		setTypeAudit(String.valueOf(forma.getTypeAudit()));
		setDateFromPlan(forma.getDateFromPlan());
		setDateUntilPlan(forma.getDateUntilPlan());
		setStatusPlan(forma.getStatusPlan());
		setIdNorm(String.valueOf(forma.getIdNorm()));
		setIdNormPlanCheck(forma.getIdNormPlanCheck());
		setDateCreatedPlan(forma.getDateCreatedPlan());
		setIdPersonCreatorPlan(String.valueOf(forma.getIdPersonCreatorPlan()));
	}
	
	
	
	public String getNamePlan() {
		return namePlan;
	}

	public void setNamePlan(String namePlan) {
		this.namePlan = namePlan;
	}

	public void cleanForm(){
		setIdPlanAudit("");
		setIdProgramAudit("");
		setIdPersonPlan("");
		setTypeAudit("");
		setDateFromPlan("");
		setDateUntilPlan("");
		setStatusPlan("");
		setIdNorm("");
		setIdNormPlanCheck("");
		setDateCreatedPlan("");
		setIdPersonCreatorPlan("");
    }

	public String getIdPlanAudit() {
		return idPlanAudit;
	}

	public int getIdPlanAuditInt() {
		return ToolsHTML.parseInt(idPlanAudit);
	}

	public void setIdPlanAudit(String idPlanAudit) {
		this.idPlanAudit = idPlanAudit;
	}

	public String getIdProgramAudit() {
		return idProgramAudit;
	}

	public int getIdProgramAuditInt() {
		return ToolsHTML.parseInt(idProgramAudit);
	}

	public void setIdProgramAudit(String idProgramAudit) {
		this.idProgramAudit = idProgramAudit;
	}

	public String getIdPersonPlan() {
		return idPersonPlan;
	}

	public int getIdPersonPlanInt() {
		return ToolsHTML.parseInt(idPersonPlan);
	}

	public void setIdPersonPlan(String idPersonPlan) {
		this.idPersonPlan = idPersonPlan;
	}

	public String getTypeAudit() {
		return typeAudit;
	}

	public int getTypeAuditInt() {
		return ToolsHTML.parseInt(typeAudit);
	}

	public void setTypeAudit(String typeAudit) {
		this.typeAudit = typeAudit;
	}
	/*
	 * public String getDateFrom() {
		return dateFrom;
	}

	
	 */

	public String getDateFromPlan() {
		return dateFromPlan;
	}
	
	public Date getDateFromPlanDATE() {
		try {
			return ToolsHTML.date.parse(dateFromPlan);
		} catch (ParseException e) {
			return null;
		}
	}

	public void setDateFromPlan(String dateFromPlan) {
		this.dateFromPlan = dateFromPlan;
	}

	public String getDateUntilPlan() {
		return dateUntilPlan;
		
	}
	
	public Date getDateUntilPlanDATE() {
		try {
			return ToolsHTML.date.parse(dateUntilPlan);
		} catch (ParseException e) {
			return null;
		}
	}

	public void setDateUntilPlan(String dateUntilPlan) {
		this.dateUntilPlan = dateUntilPlan;
	}

	public String getStatusPlan() {
		return statusPlan;
	}

	public void setStatusPlan(String statusPlan) {
		this.statusPlan = statusPlan;
	}

	public String getIdNorm() {
		return idNorm;
	}

	public int getIdNormInt() {
		return ToolsHTML.parseInt(idNorm);
	}

	public void setIdNorm(String idNorm) {
		this.idNorm = idNorm;
	}

	public String getIdNormPlanCheck() {
		return idNormPlanCheck;
	}

	public void setIdNormPlanCheck(String idNormPlanCheck) {
		this.idNormPlanCheck = idNormPlanCheck;
	}

	public String getDateCreatedPlan() {
		return dateCreatedPlan;
	}

	public void setDateCreatedPlan(String dateCreatedPlan) {
		this.dateCreatedPlan = dateCreatedPlan;
	}
	
	public Date getDateCreatedPlanDATE() {
		try {
			return ToolsHTML.date.parse(dateCreatedPlan);
		} catch (ParseException e) {
			return null;
		}
	}

	public String getIdPersonCreatorPlan() {
		return idPersonCreatorPlan;
	}

	public int getIdPersonCreatorPlanInt() {
		return ToolsHTML.parseInt(idPersonCreatorPlan);
	}

	public void setIdPersonCreatorPlan(String idPersonCreatorPlan) {
		this.idPersonCreatorPlan = idPersonCreatorPlan;
	}

	public void load(PlanAudit oPlanAudit) {
		setIdPlanAudit(String.valueOf(oPlanAudit.getIdPlanAudit()));
		setNamePlan(oPlanAudit.getNamePlan());
		setIdProgramAudit(String.valueOf(oPlanAudit.getIdProgramAudit()));
		setIdPersonPlan(String.valueOf(oPlanAudit.getIdPersonPlan()));
		setTypeAudit(String.valueOf(oPlanAudit.getTypeAudit()));
		setDateFromPlan(oPlanAudit.getDateFromPlan());
		setDateUntilPlan(oPlanAudit.getDateUntilPlan());
		setStatusPlan(oPlanAudit.getStatusPlan());
		setIdNorm(String.valueOf(oPlanAudit.getIdNorm()));
		setIdNormPlanCheck(oPlanAudit.getIdNormPlanCheck());
		setDateCreatedPlan(oPlanAudit.getDateCreatedPlan());
		setIdPersonCreatorPlan(String.valueOf(oPlanAudit.getIdPersonCreatorPlan()));

	}

}