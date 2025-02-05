package com.desige.webDocuments.utils.beans;

import java.io.Serializable;

/**
 * Title: BeanNotifiedWF.java <br/>
 * Copyright: (c) 2004 Focus Consulting C.A.<br/>
 * 
 * @author Ing. Nelson Crespo
 * @version WebDocuments v3.0 <br/>
 *          Changes:<br/>
 *          <ul>
 *          <li>27/02/2005 (NC) Creation</li>
 *          </ul>
 */
public class BeanNotifiedWF implements Serializable {
	private String row;
	private String idUser;
	private String email;
	private String comments;
	private String owner;
	private boolean ownerWF;
	private String notified;
	private String nameUser;
	private String nameDocument;
	private int idWorkFlow;

	public BeanNotifiedWF(String row, String idUser, String email) {
		setRow(row);
		setIdUser(idUser);
		setEmail(email);
	}

	public BeanNotifiedWF() {
		setRow(null);
	}

	public String getRow() {
		return row;
	}

	public void setRow(String row) {
		this.row = row != null ? row.trim() : row;
	}

	public String getIdUser() {
		return idUser;
	}

	public void setIdUser(String idUser) {
		this.idUser = idUser != null ? idUser.trim() : idUser;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email != null ? email.trim() : email;
	}

	public String getComments() {
		return comments;
	}

	public void setComments(String comments) {
		this.comments = comments;
	}

	public String getOwner() {
		return owner;
	}

	public void setOwner(String owner) {
		this.owner = owner;
	}

	public boolean isOwnerWF() {
		return ownerWF;
	}

	public void setOwnerWF(boolean ownerWF) {
		this.ownerWF = ownerWF;
	}

	public String getNotified() {
		return notified;
	}

	public void setNotified(String notified) {
		this.notified = notified;
	}

	public String getNameUser() {
		return nameUser;
	}

	public void setNameUser(String nameUser) {
		this.nameUser = nameUser;
	}

	public String getNameDocument() {
		return nameDocument;
	}

	public void setNameDocument(String nameDocument) {
		this.nameDocument = nameDocument;
	}

	public int getIdWorkFlow() {
		return idWorkFlow;
	}

	public void setIdWorkFlow(int idWorkFlow) {
		this.idWorkFlow = idWorkFlow;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("BeanNotifiedWF [row=").append(row).append(", idUser=")
				.append(idUser).append(", email=").append(email)
				.append(", comments=").append(comments).append(", owner=")
				.append(owner).append(", ownerWF=").append(ownerWF)
				.append(", notified=").append(notified).append(", nameUser=")
				.append(nameUser).append(", nameDocument=")
				.append(nameDocument).append(", idWorkFlow=")
				.append(idWorkFlow).append("]");
		return builder.toString();
	}

}
