package com.desige.webDocuments.document.forms;

import java.io.Serializable;

import com.focus.qweb.bean.Person;

/**
 * Title: DocumentsCheckOutsBean.java <br/>
 * Copyright: (c) 2004 Focus Consulting C.A.<br/>
 *
 * @author Ing. Nelson Crespo
 * @version WebDocuments v3.0
 * <br/>
 *      Changes:<br/>
 * <ul>
 *     <li> 23/10/2004 (NC) Creation </li>
 * </ul>
 */
public class DocumentsCheckOutsBean implements Serializable, Comparable<DocumentsCheckOutsBean> {
	private int numSolicitud;
	private String idDocument;
	private String mayorVer;
	private String minorVer;
	private String nameDocument;
	private String dateCheckOut;
    //	fecha de expirar documento
	// TICKET PENDIENTE private String dateExpires;
    private boolean checkOut;
    private String doneBy;
    private String idCheckOut;
    private String prefix;
    private String number;
    private String numVer;
    private String idWorkFlow;
    private String destinatarios;
    private Person personBean;
    private String idRegisterClass;
    // campo para Statu
    private String statuBeanDocPendiente;
    
    public void setNumSolicitud(int numSolicitud) {
		this.numSolicitud = numSolicitud;
	}
    
    public int getNumSolicitud() {
		return numSolicitud;
	}
    
    public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = number;
	}
    public String getPrefix() {
		return prefix;
	}

	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}

    public String getIdDocument() {
		return idDocument;
	}

	public void setIdDocument(String idDocument) {
		this.idDocument = idDocument;
	}

	public String getMayorVer() {
		return mayorVer;
	}

	public void setMayorVer(String mayorVer) {
		this.mayorVer = mayorVer;
	}

	public String getMinorVer() {
		return minorVer;
	}

	public void setMinorVer(String minorVer) {
		this.minorVer = minorVer;
	}

	public String getNameDocument() {
		return nameDocument;
	}

	public void setNameDocument(String nameDocument) {
		this.nameDocument = nameDocument;
	}

	public String getDateCheckOut() {
		return dateCheckOut;
	}

	public void setDateCheckOut(String dateCheckOut) {
		this.dateCheckOut = dateCheckOut;
	}
	
	// aqui settter y getter
	// TICKET DOCUMENTOS PENDIENTE DARWINUZCATEGUI
	/*
	public String getDateExpires() {
		return dateExpires;
	}

	public void setDateExpires(String dateExpires) {
		this.dateExpires = dateExpires;
	}
   */
    public boolean isCheckOut() {
        return checkOut;
    }

    public void setCheckOut(boolean checkOut) {
        this.checkOut = checkOut;
    }

    public String getDoneBy() {
        return doneBy;
    }

    public void setDoneBy(String doneBy) {
        this.doneBy = doneBy;
    }

    public String getNumVer() {
		return numVer;
	}

	public void setNumVer(String numVer) {
		this.numVer = numVer;
	}
	
	public String getIdWorkFlow() {
		return idWorkFlow;
	}
	
	public void setIdWorkFlow(String idWorkFlow) {
		this.idWorkFlow = idWorkFlow;
	}
	
	public String getDestinatarios() {
		return destinatarios;
	}
	
	public void setDestinatarios(String destinatarios) {
		this.destinatarios = destinatarios;
	}

	public String toString() {
        return this.getIdDocument();
//        StringBuffer result = new StringBuffer(60);
//        result.append("[doneBy] ").append(doneBy).append(" [checkOut] ").append(checkOut);
//        return result.toString();
    }

    public String getIdCheckOut() {
        return idCheckOut;
    }

    public void setIdCheckOut(String idCheckOut) {
        this.idCheckOut = idCheckOut;
    }
    
    public Person getPersonBean() {
		return personBean;
	}
	
	public void setPersonBean(Person personBean) {
		this.personBean = personBean;
	}
	
    public String getIdRegisterClass() {
		return idRegisterClass;
	}

	public void setIdRegisterClass(String idRegisterClass) {
		this.idRegisterClass = idRegisterClass;
	}
	
	public String getStatuBeanDocPendiente() {
		return statuBeanDocPendiente;
	}
	
	public void setStatuBeanDocPendiente(String statuBeanDocPendiente) {
		this.statuBeanDocPendiente = statuBeanDocPendiente;
	}

	public boolean equals(Object o) {
        if (o==null) {
            return false;
        }

        if (this == o) {
            return true;
        }

        if (o instanceof DocumentsCheckOutsBean) {
            DocumentsCheckOutsBean objParam = (DocumentsCheckOutsBean)o;

            ////System.out.println("objParam = " + objParam.getIdDocument());
            ////System.out.println("this.getIdDocument() = " + this.getIdDocument());
            if (objParam.getIdDocument().equalsIgnoreCase(this.getIdDocument())) {
                return true;
            }
        }
        return false;
    }

    public int hashCode() {
        if (this.getIdCheckOut()!=null) {
            return this.getIdCheckOut().hashCode();
        } else {
            return 0;
        }
    }


	public int compareTo(DocumentsCheckOutsBean other) {
		// TODO Auto-generated method stub
		if((this.personBean != null) && (other.personBean != null)){
			int compare1 = this.personBean.getApellido().compareTo(
					other.personBean.getApellido());
			if (compare1 != 0) {
				return compare1;
			} else {
				return this.personBean.getNombre().compareTo(other.personBean.getNombre());
			}
		} else {
			if (idCheckOut != null){
				return this.idCheckOut.compareTo(other.idCheckOut);
			} else {
				return Integer.valueOf(this.hashCode()).compareTo(
						other.hashCode());
			}
		}
	}

}

