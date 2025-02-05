package com.desige.webDocuments.accion.forms;

import java.io.Serializable;
import java.text.ParseException;
import java.util.Date;

import com.desige.webDocuments.utils.ToolsHTML;
import com.desige.webDocuments.utils.beans.SuperActionForm;
import com.focus.qweb.to.ProgramAuditTO;

/**
 * Created by IntelliJ IDEA.
 * User: Administrador
 * Date: 20/03/2006
 * Time: 10:29:06 AM
 * To change this template use File | Settings | File Templates.
 */

public class ProgramAudit extends SuperActionForm implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 6972051536904149420L;

	private long idProgramAudit;
	private String nameProgram;
	private int idNorm;
	private int idPersonResponsible;
	private String dateFrom;
	private String dateUntil;
	private String status;
	private String idNormCheck;
	private String dateCreated;
	private int idPersonCreator;

	public ProgramAudit() {
    	
    }

	public void cleanForm(){
		setIdProgramAudit(0);
		setNameProgram("");
		setIdNorm(0);
		setIdPersonResponsible(0);
		setDateFrom("");
		setDateUntil("");
		setStatus("");
		setIdNormCheck("");
		setDateCreated("");
		setIdPersonCreator(0);
	}

	public long getId() {
		return idProgramAudit;
	}

	public void setId(long idProgramAudit) {
		this.idProgramAudit = idProgramAudit;
	}

	public long getIdProgramAudit() {
		return idProgramAudit;
	}

	public void setIdProgramAudit(long idProgramAudit) {
		this.idProgramAudit = idProgramAudit;
	}

	public String getNameProgram() {
		return nameProgram;
	}

	public void setNameProgram(String nameProgram) {
		this.nameProgram = nameProgram;
	}

	public int getIdNorm() {
		return idNorm;
	}

	public void setIdNorm(int idNorm) {
		this.idNorm = idNorm;
	}

	public int getIdPersonResponsible() {
		return idPersonResponsible;
	}

	public void setIdPersonResponsible(int idPersonResponsible) {
		this.idPersonResponsible = idPersonResponsible;
	}

	public String getDateFrom() {
		return dateFrom;
	}

	public void setDateFrom(String dateFrom) {
		this.dateFrom = dateFrom;
	}

	public String getDateUntil() {
		return dateUntil;
	}

	public void setDateUntil(String dateUntil) {
		this.dateUntil = dateUntil;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getIdNormCheck() {
		return idNormCheck;
	}

	public void setIdNormCheck(String idNormCheck) {
		this.idNormCheck = idNormCheck;
	}

	public String getDateCreated() {
		return dateCreated;
	}

	public void setDateCreated(String dateCreated) {
		this.dateCreated = dateCreated;
	}

	public int getIdPersonCreator() {
		return idPersonCreator;
	}

	public void setIdPersonCreator(int idPersonCreator) {
		this.idPersonCreator = idPersonCreator;
	}

	public void load(ProgramAuditTO oProgramAuditTO) throws ParseException {
		setIdProgramAudit(oProgramAuditTO.getIdProgramAuditInt());
		setNameProgram(oProgramAuditTO.getNameProgram());
		setIdNorm(oProgramAuditTO.getIdNormInt());
		setIdPersonResponsible(oProgramAuditTO.getIdPersonResponsibleInt());
		setDateFrom(oProgramAuditTO.getDateFrom());
		setDateUntil(oProgramAuditTO.getDateUntil());
		setStatus(oProgramAuditTO.getStatus());
		setIdNormCheck(oProgramAuditTO.getIdNormCheck());
		setDateCreated(oProgramAuditTO.getDateCreated());
		setIdPersonCreator(oProgramAuditTO.getIdPersonCreatorInt());
	}

}