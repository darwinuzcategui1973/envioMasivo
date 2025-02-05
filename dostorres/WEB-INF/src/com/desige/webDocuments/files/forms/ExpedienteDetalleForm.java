package com.desige.webDocuments.files.forms;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.desige.webDocuments.utils.beans.SuperActionForm;

/**
 * Title: grupoForm.java <br/>
 * Copyright: (c) 2004 Focus Consulting C.A.<br/>
 * @author Ing. Roger Rodríguez
 * @version WebDocuments v3.0
 * <br/>
 * Changes:<br/> 
 * <ul>
 *      <li>25/06/2004 (RR) Creation </li>
 </ul>
 */
public class ExpedienteDetalleForm extends SuperActionForm implements Serializable {
	
    private int f1;
    private int numgen;
    private int orden;
    
    private int numver;
    private int filesVersion;
    private String nameUser;
    private Date datePrint;
    private String ownerFiles;
    
    
    public void reset(){
    	setF1(0);
    	setNumgen(0);
    	setOrden(0);
    }

	public int getF1() {
		return f1;
	}

	public void setF1(int f1) {
		this.f1 = f1;
	}

	public int getNumgen() {
		return numgen;
	}

	public void setNumgen(int numgen) {
		this.numgen = numgen;
	}

	public int getOrden() {
		return orden;
	}

	public void setOrden(int orden) {
		this.orden = orden;
	}

	public Date getDatePrint() {
		return datePrint;
	}

	public void setDatePrint(Date datePrint) {
		this.datePrint = datePrint;
	}

	public int getFilesVersion() {
		return filesVersion;
	}

	public void setFilesVersion(int filesVersion) {
		this.filesVersion = filesVersion;
	}

	public String getNameUser() {
		return nameUser;
	}

	public void setNameUser(String nameUser) {
		this.nameUser = nameUser;
	}

	public int getNumver() {
		return numver;
	}

	public void setNumver(int numver) {
		this.numver = numver;
	}

	public String getOwnerFiles() {
		return ownerFiles;
	}

	public void setOwnerFiles(String ownerFiles) {
		this.ownerFiles = ownerFiles;
	}
    
    
    
}
