package com.focus.qweb.to;

import java.io.Serializable;

import com.desige.webDocuments.accion.forms.NormAuditCero;
import com.desige.webDocuments.utils.ToolsHTML;
import com.desige.webDocuments.utils.beans.SuperActionForm;
import com.focus.qweb.interfaces.ObjetoTO;

public class NormCeroAuditTO extends SuperActionForm implements Serializable,ObjetoTO {

	private static final long serialVersionUID = 1L;
	
	private String idNormAudit;
	private String nameNorma;
	private String idNormIndiceCero;
	

	public NormCeroAuditTO() {
		setIdNormAudit("");
		setNameNorma("");
		setIdNormIndiceCero("");
    }
	public NormCeroAuditTO(String idNormAudit) {
		super();
		this.idNormAudit = idNormAudit;
	}

	public NormCeroAuditTO(NormAuditCero forma) {
		setIdNormAudit(String.valueOf(forma.getIdNormAudit()));
		setNameNorma(forma.getNameNorma());
		setIdNormIndiceCero(String.valueOf(forma.getIdNormIndiceCero()));
		}
	
	
	
	public String getNameNorma() {
		return nameNorma;
	}

	public void setNameNorma(String nameNorma) {
		this.nameNorma = nameNorma;
	}

	public void cleanForm(){
		setIdNormAudit("");
		setNameNorma("");
		setIdNormIndiceCero("");
		    }

	public String getIdNormAudit() {
		return idNormAudit;
	}

	public int getIdNormAuditInt() {
		return ToolsHTML.parseInt(idNormAudit);
	}

	public void setIdNormAudit(String idNormAudit) {
		this.idNormAudit = idNormAudit;
	}

	public String getIdNormIndiceCero() {
		return idNormIndiceCero;
	}

	public int getIdNormIndiceCeroInt() {
		return ToolsHTML.parseInt(idNormIndiceCero);
	}

	public void setIdNormIndiceCero(String idNormIndiceCero) {
		this.idNormIndiceCero = idNormIndiceCero;
	}


	public void load(NormAuditCero oNormCeroAudit) {
		setIdNormAudit(String.valueOf(oNormCeroAudit.getIdNormAudit()));
		setNameNorma(oNormCeroAudit.getNameNorma());
		setIdNormIndiceCero(String.valueOf(oNormCeroAudit.getIdNormIndiceCero()));
		
	}
	

}