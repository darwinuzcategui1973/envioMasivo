package com.desige.webDocuments.digital.forms;

import java.io.Serializable;
import java.util.Date;

import com.desige.webDocuments.utils.beans.SuperActionForm;

/**
 * Title: DigitalForm.java <br/>
 * Copyright: (c) 2004 Focus Consulting C.A.<br/>
 * 
 * @author Ing. Roger Rodríguez
 * @version WebDocuments v3.0 <br/>
 *          Changes:<br/>
 *          <ul>
 *          <li>25/06/2004 (RR) Creation</li>
 *          </ul>
 */
public class DigitalForm extends SuperActionForm implements Serializable {

	private int idDigital;
	private String nameFile;
	private int type;
	private Date dateCreation;
	private int numberTest;
	private int idPerson;
	private int idStatusDigital;

	public void reset() {
		setIdDigital(0);
		setNameFile("");
		setType(0);
		setDateCreation(new Date());
		setNumberTest(0);
		setIdPerson(0);
		setIdStatusDigital(1);
	}

	public Date getDateCreation() {
		return dateCreation;
	}

	public void setDateCreation(Date dateCreation) {
		this.dateCreation = dateCreation;
	}

	public int getIdDigital() {
		return idDigital;
	}

	public void setIdDigital(int idDigital) {
		this.idDigital = idDigital;
	}

	public int getIdPerson() {
		return idPerson;
	}

	public void setIdPerson(int idPerson) {
		this.idPerson = idPerson;
	}

	public int getIdStatusDigital() {
		return idStatusDigital;
	}

	public void setIdStatusDigital(int idStatusDigital) {
		this.idStatusDigital = idStatusDigital;
	}

	public String getNameFile() {
		return nameFile;
	}

	public void setNameFile(String nameFile) {
		this.nameFile = nameFile;
	}

	public int getNumberTest() {
		return numberTest;
	}

	public void setNumberTest(int numberTest) {
		this.numberTest = numberTest;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("DigitalForm [idDigital=").append(idDigital)
				.append(", nameFile=").append(nameFile).append(", type=")
				.append(type).append(", dateCreation=").append(dateCreation)
				.append(", numberTest=").append(numberTest)
				.append(", idPerson=").append(idPerson)
				.append(", idStatusDigital=").append(idStatusDigital)
				.append("]");
		return builder.toString();
	}

}
