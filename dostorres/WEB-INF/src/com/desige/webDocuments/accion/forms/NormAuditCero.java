package com.desige.webDocuments.accion.forms;

import java.io.Serializable;
import java.text.ParseException;

import com.desige.webDocuments.utils.beans.SuperActionForm;
import com.focus.qweb.to.NormCeroAuditTO;

public class NormAuditCero extends SuperActionForm implements Serializable {
		/**
		 * 
		 */
		private static final long serialVersionUID = 6972051536904149420L;

		private int idNormAudit;
		private String nameNorma;
		private int idNormIndiceCero;
	
		public NormAuditCero() {
	    	
	    }

		public void cleanForm(){
			setIdNormAudit(0);
			setNameNorma("");
			setIdNormIndiceCero(0);
				}
		
		
		public int getId() {
			return idNormAudit;
		}

		public void setId(int idNormAudit) {
			this.idNormAudit = idNormAudit;
		}


		public int getIdNormAudit() {
			return idNormAudit;
		}

		public void setIdNormAudit(int idNormAudit) {
			this.idNormAudit = idNormAudit;
		}


		public String getNameNorma() {
			return nameNorma;
		}

		public void setNameNorma(String nameNorma) {
			this.nameNorma = nameNorma;
		}

		public int getIdNormIndiceCero() {
			return idNormIndiceCero;
		}

		public void setIdNormIndiceCero(int idNormIndiceCero) {
			this.idNormIndiceCero = idNormIndiceCero;
		}

		public void load(NormCeroAuditTO oNormCeroAuditTo) throws ParseException {
			setIdNormAudit(oNormCeroAuditTo.getIdNormAuditInt());
			setNameNorma(oNormCeroAuditTo.getNameNorma());
			setIdNormIndiceCero(oNormCeroAuditTo.getIdNormIndiceCeroInt());
		
				}

	}