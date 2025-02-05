package com.desige.webDocuments.registers.forms;

import java.io.Serializable;

import com.desige.webDocuments.utils.beans.SuperActionForm;

/**
 * Title: Register.java <br/>
 * Copyright: (c) 2004 Focus Consulting C.A.<br/>
 * @author Ing. Nelson Crespo
 * @version WebDocuments v3.0
 * <br/>
 *       Changes:<br/>
 * <ul>
 *       <li> 17/02/2005 (NC) Creation </li>
 * </ul>
 */
public class Register  extends SuperActionForm implements Serializable {
    private String idDocument;
    private String nameDocument;
    private String idRout;
    private String nameRout;
    private String idNodeActual;
    private String typeDocument;
    private String mayorVer;
    private String minorVer;
    private String idRegisterClass;

    public String getIdDocument() {
        return idDocument;
    }

    public void setIdDocument(String idDocument) {
        this.idDocument = idDocument;
    }

    public String getNameDocument() {
        return nameDocument;
    }

    public void setNameDocument(String nameDocument) {
        this.nameDocument = nameDocument;
    }

    public String getIdRout() {
        return idRout;
    }

    public void setIdRout(String idRout) {
        this.idRout = idRout;
    }

    public String getNameRout() {
        return nameRout;
    }

    public void setNameRout(String nameRout) {
        this.nameRout = nameRout;
    }

    public String getIdNodeActual() {
        return idNodeActual;
    }

    public void setIdNodeActual(String idNodeActual) {
        this.idNodeActual = idNodeActual;
    }

    public String getTypeDocument() {
        return typeDocument;
    }

    public void setTypeDocument(String typeDocument) {
        this.typeDocument = typeDocument;
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
    
	public String getIdRegisterClass() {
		return idRegisterClass;
	}

	public void setIdRegisterClass(String idRegisterClass) {
		this.idRegisterClass = idRegisterClass;
	}

}
