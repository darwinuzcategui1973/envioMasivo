package com.focus.qweb.to;

import java.io.Serializable;
import java.text.ParseException;
import java.util.Date;

import com.desige.webDocuments.accion.forms.ProgramAudit;
import com.desige.webDocuments.utils.ToolsHTML;
import com.desige.webDocuments.utils.beans.SuperActionForm;
import com.focus.qweb.interfaces.ObjetoTO;

public class ProgramAuditTO extends SuperActionForm implements Serializable, ObjetoTO {

	private static final long serialVersionUID = 1L;

	private String idProgramAudit;
	private String nameProgram;
	private String idNorm;
	private String idPersonResponsible;
	private String dateFrom;
	private String dateUntil;
	private String status;
	private String idNormCheck;
	private String dateCreated;
	private String idPersonCreator;

	public ProgramAuditTO() {
    }
	
	public ProgramAuditTO(String idProgramAudit) {
		super();
		this.idProgramAudit = idProgramAudit;
    }

	public ProgramAuditTO(ProgramAudit forma) {
		setIdProgramAudit(String.valueOf(forma.getIdProgramAudit()));
		setNameProgram(forma.getNameProgram());
		setIdNorm(String.valueOf(forma.getIdNorm()));
		setIdPersonResponsible(String.valueOf(forma.getIdPersonResponsible()));
		setDateFrom(forma.getDateFrom());
		setDateUntil(forma.getDateUntil());
		setStatus(forma.getStatus());
		setIdNormCheck(forma.getIdNormCheck());
		setDateCreated(forma.getDateCreated());
		setIdPersonCreator(String.valueOf(forma.getIdPersonCreator()));
		
	}

	public void cleanForm(){
		setIdProgramAudit("");
		setNameProgram("");
		setIdNorm("");
		setIdPersonResponsible("");
		setDateFrom("");
		setDateUntil("");
		setStatus("");
		setIdNormCheck("");
		setDateCreated("");
		setIdPersonCreator("");
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

	public String getNameProgram() {
		return nameProgram;
	}

	public void setNameProgram(String nameProgram) {
		this.nameProgram = nameProgram;
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

	public String getIdPersonResponsible() {
		return idPersonResponsible;
	}

	public int getIdPersonResponsibleInt() {
		return ToolsHTML.parseInt(idPersonResponsible);
	}

	public void setIdPersonResponsible(String idPersonResponsible) {
		this.idPersonResponsible = idPersonResponsible;
	}

	public String getDateFrom() {
		return dateFrom;
	}

	public Date getDateFromDATE() {
		try {
			return ToolsHTML.date.parse(dateFrom);
		} catch (ParseException e) {
			return null;
		}
	}

	public void setDateFrom(String dateFrom) {
		this.dateFrom = dateFrom;
	}

	public String getDateUntil() {
		return dateUntil;
	}

	public Date getDateUntilDATE() {
		try {
			return ToolsHTML.date.parse(dateUntil);
		} catch (ParseException e) {
			return null;
		}
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

	public Date getDateCreatedDATE() {
		try {
			return ToolsHTML.date.parse(dateCreated);
		} catch (ParseException e) {
			return null;
		}
	}
	
	public void setDateCreated(String dateCreated) {
		this.dateCreated = dateCreated;
	}

	public String getIdPersonCreator() {
		return idPersonCreator;
	}

	public int getIdPersonCreatorInt() {
		return ToolsHTML.parseInt(idPersonCreator);
	}

	public void setIdPersonCreator(String idPersonCreator) {
		this.idPersonCreator = idPersonCreator;
	}


}