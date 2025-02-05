package com.desige.webDocuments.utils.beans;

import java.io.Serializable;

/**
 * Title: BeanStatusUserMov.java<br>
 * Copyright: (c) 2004 Focus Consulting C.A.<br/>
 *
 * @author Ing. Nelson Crespo (NC)
 * @version WebDocuments v3.0
 * <br>
 *     Changes:<br>
 *  <ul>
 *      <li> 13-09-2004 (NC) Creation </li>
 *  <ul>
 */
public class BeanStatusUserMov implements Serializable {
	private int idStatu;
	private int idMovement;
	private String idUser;
	private String Statu;
	private String dateReplied;
	private String comments;

public int getIdStatu() {
	return idStatu;
}

public void setIdStatu(int idStatu) {
	this.idStatu = idStatu;
}

public int getIdMovement() {
	return idMovement;
}

public void setIdMovement(int idMovement) {
	this.idMovement = idMovement;
}

public String getIdUser() {
	return idUser;
}

public void setIdUser(String idUser) {
	this.idUser = idUser;
}

public String getStatu() {
	return Statu;
}

public void setStatu(String statu) {
	Statu = statu;
}

public String getDateReplied() {
	return dateReplied;
}

public void setDateReplied(String dateReplied) {
	this.dateReplied = dateReplied;
}

public String getComments() {
	return comments;
}

public void setComments(String comments) {
	this.comments = comments;
}
}
