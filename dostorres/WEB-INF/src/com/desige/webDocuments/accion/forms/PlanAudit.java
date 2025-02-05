package com.desige.webDocuments.accion.forms;

import java.io.Serializable;
import java.text.ParseException;
import java.util.Date;

import com.desige.webDocuments.utils.ToolsHTML;
import com.desige.webDocuments.utils.beans.SuperActionForm;
import com.focus.qweb.to.PlanAuditTO;

/**
 * Created by IntelliJ IDEA.
 * User: Administrador
 * Date: 20/03/2006
 * Time: 10:29:06 AM
 * To change this template use File | Settings | File Templates.
 */

public class PlanAudit extends SuperActionForm implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 6972051536904149420L;

	private long idPlanAudit;
	private String namePlan;
	private long idProgramAudit;
	private int idPersonPlan;
	private int typeAudit;
	private String dateFromPlan;
	private String dateUntilPlan;
	private String statusPlan;
	private int idNorm;
	private String idNormPlanCheck;
	private String dateCreatedPlan;
	private int idPersonCreatorPlan;

	public PlanAudit() {
    	
    }

	public void cleanForm(){
		setIdPlanAudit(0);
		setNamePlan("");
		setIdProgramAudit(0);
		setIdPersonPlan(0);
		setTypeAudit(0);
		setDateFromPlan("");
		setDateUntilPlan("");
		setStatusPlan("");
		setIdNorm(0);
		setIdNormPlanCheck("");
		setDateCreatedPlan("");
		setIdPersonCreatorPlan(0);
	}

	public long getId() {
		return idPlanAudit;
	}

	public void setId(long idPlanAudit) {
		this.idPlanAudit = idPlanAudit;
	}


	public String getNamePlan() {
		return namePlan;
	}

	public void setNamePlan(String namePlan) {
		this.namePlan = namePlan;
	}

	public long getIdPlanAudit() {
		return idPlanAudit;
	}

	public void setIdPlanAudit(long idPlanAudit) {
		this.idPlanAudit = idPlanAudit;
	}

	public long getIdProgramAudit() {
		return idProgramAudit;
	}

	public void setIdProgramAudit(long idProgramAudit) {
		this.idProgramAudit = idProgramAudit;
	}

	public int getIdPersonPlan() {
		return idPersonPlan;
	}

	public void setIdPersonPlan(int idPersonPlan) {
		this.idPersonPlan = idPersonPlan;
	}

	public int getTypeAudit() {
		return typeAudit;
	}

	public void setTypeAudit(int typeAudit) {
		this.typeAudit = typeAudit;
	}

	public String getDateFromPlan() {
		return dateFromPlan;
	}

	public void setDateFromPlan(String dateFromPlan) {
		this.dateFromPlan = dateFromPlan;
	}

	public String getDateUntilPlan() {
		return dateUntilPlan;
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

	public int getIdNorm() {
		return idNorm;
	}

	public void setIdNorm(int idNorm) {
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

	public int getIdPersonCreatorPlan() {
		return idPersonCreatorPlan;
	}

	public void setIdPersonCreatorPlan(int idPersonCreatorPlan) {
		this.idPersonCreatorPlan = idPersonCreatorPlan;
	}

	public void load(PlanAuditTO oPlanAuditTO) throws ParseException {
		setIdPlanAudit(oPlanAuditTO.getIdPlanAuditInt());
		setNamePlan(oPlanAuditTO.getNamePlan());
		setIdProgramAudit(oPlanAuditTO.getIdProgramAuditInt());
		setIdPersonPlan(oPlanAuditTO.getIdPersonPlanInt());
		setTypeAudit(oPlanAuditTO.getTypeAuditInt());
		setDateFromPlan(oPlanAuditTO.getDateFromPlan());
		setDateUntilPlan(oPlanAuditTO.getDateUntilPlan());
		setStatusPlan(oPlanAuditTO.getStatusPlan());
		setIdNorm(oPlanAuditTO.getIdNormInt());
		setIdNormPlanCheck(oPlanAuditTO.getIdNormPlanCheck());
		setDateCreatedPlan(oPlanAuditTO.getDateCreatedPlan());
		setIdPersonCreatorPlan(oPlanAuditTO.getIdPersonCreatorPlanInt());
	}

}