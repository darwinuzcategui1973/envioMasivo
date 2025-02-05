package com.desige.webDocuments.document.forms;

import java.io.Serializable;

/**
 * Title: BeanFirmsDoc.java <br/>
 * Copyright: (c) 2004 Focus Consulting C.A.<br/>
 * @author Ing. Nelson Crespo
 * @version WebDocuments v3.0
 * <br/>
 *       Changes:<br/>
 * <ul>
 *       <li> 06/01/2005 (NC) Creation </li>
 * </ul>
 */
public class BeanFirmsDoc implements Serializable {
    private String idUser;
    private String nameUser;
    private String charge;
    private String dateReplied;
    private String editor;
    private int contador;

    public BeanFirmsDoc(String idUser,String nameUser,String charge) {
        setIdUser(idUser);
        setNameUser(nameUser);
        setCharge(charge);
    }

    public BeanFirmsDoc(String idUser,String nameUser,String charge, String editor) {
        setIdUser(idUser);
        setNameUser(nameUser);
        setCharge(charge);
        setEditor(editor);
    }
    
    public BeanFirmsDoc(String idUser,String nameUser,String charge, String editor, int contador) {
        setIdUser(idUser);
        setNameUser(nameUser);
        setCharge(charge);
        setEditor(editor);
        setContador(contador);
    }
    

    public BeanFirmsDoc() {

    }

    public String getIdUser() {
        return idUser;
    }

    public void setIdUser(String idUser) {
        this.idUser = idUser;
    }

    public String getNameUser() {
        return nameUser;
    }

    public void setNameUser(String nameUser) {
        this.nameUser = nameUser;
    }

    public String getCharge() {
        return charge;
    }

    public void setCharge(String charge) {
        this.charge = charge;
    }

    public String getDateReplied() {
        return dateReplied;
    }

    public void setDateReplied(String dateReplied) {
        this.dateReplied = dateReplied;
    }

	public String getEditor() {
		return editor;
	}

	public void setEditor(String editor) {
		this.editor = editor;
	}

	public int getContador() {
		return contador;
	}

	public void setContador(int contador) {
		this.contador = contador;
	}
    
    
}
