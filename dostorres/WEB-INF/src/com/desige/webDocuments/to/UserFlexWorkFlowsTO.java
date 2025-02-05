package com.desige.webDocuments.to;

import java.io.Serializable;
import java.util.Date;

public class UserFlexWorkFlowsTO implements Serializable, Cloneable {

	private long idFlexWF;
	private int orden;
	private String idUser;
	private int idWorkFlow;
	private byte type;
	private String result;
	private String statu;
	private Date dateReplied;
	private String comments;
	private byte isOwner;
	private byte reading;
	private byte active;
	private byte pending;
	private byte wfActive;
	private long IdFather;
	private byte uw_Circle;

	public byte getActive() {
		return active;
	}

	public void setActive(byte active) {
		this.active = active;
	}

	public String getComments() {
		return comments;
	}

	public void setComments(String comments) {
		this.comments = comments;
	}

	public Date getDateReplied() {
		return dateReplied;
	}

	public void setDateReplied(Date dateReplied) {
		this.dateReplied = dateReplied;
	}

	public long getIdFather() {
		return IdFather;
	}

	public void setIdFather(long idFather) {
		IdFather = idFather;
	}

	public long getIdFlexWF() {
		return idFlexWF;
	}

	public void setIdFlexWF(long idFlexWF) {
		this.idFlexWF = idFlexWF;
	}

	public String getIdUser() {
		return idUser;
	}

	public void setIdUser(String idUser) {
		this.idUser = idUser;
	}

	public int getIdWorkFlow() {
		return idWorkFlow;
	}

	public void setIdWorkFlow(int idWorkFlow) {
		this.idWorkFlow = idWorkFlow;
	}

	public byte getIsOwner() {
		return isOwner;
	}

	public void setIsOwner(byte isOwner) {
		this.isOwner = isOwner;
	}

	public int getOrden() {
		return orden;
	}

	public void setOrden(int orden) {
		this.orden = orden;
	}

	public byte getPending() {
		return pending;
	}

	public void setPending(byte pending) {
		this.pending = pending;
	}

	public byte getReading() {
		return reading;
	}

	public void setReading(byte reading) {
		this.reading = reading;
	}

	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
	}

	public String getStatu() {
		return statu;
	}

	public void setStatu(String statu) {
		this.statu = statu;
	}

	public byte getType() {
		return type;
	}

	public void setType(byte type) {
		this.type = type;
	}

	public byte getUw_Circle() {
		return uw_Circle;
	}

	public void setUw_Circle(byte uw_Circle) {
		this.uw_Circle = uw_Circle;
	}

	public byte getWfActive() {
		return wfActive;
	}

	public void setWfActive(byte wfActive) {
		this.wfActive = wfActive;
	}

	public Object clone() throws CloneNotSupportedException {
		// get initial bit-by-bit copy, which handles all immutable fields
		UserFlexWorkFlowsTO result = (UserFlexWorkFlowsTO) super.clone();

		// mutable fields need to be made independent of this object, for reasons
		// similar to those for defensive copies - to prevent unwanted access to
		// this object's internal state
		//result.fBestBeforeDate = new Date(this.fBestBeforeDate.getTime());

		return result;
	}
}
