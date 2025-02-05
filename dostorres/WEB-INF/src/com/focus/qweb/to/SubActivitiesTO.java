package com.focus.qweb.to;

import java.util.HashSet;
import java.util.Set;

import com.desige.webDocuments.activities.forms.SubActivities;
import com.desige.webDocuments.utils.ToolsHTML;
import com.focus.qweb.interfaces.ObjetoTO;

public class SubActivitiesTO implements ObjetoTO {

	private static final long serialVersionUID = 1L;

	private String number;  //sAct_number
	private String activityID;  //Act_Number
	private String nameAct;  //sAct_Name
	private String description;  //sAct_Description
	private String active;  //sAct_Active
	private String orden;  //sAct_Order
	
	private Set<String> idPersons = new HashSet<String>();
	
	
	public SubActivitiesTO() {
		
	}

	public SubActivitiesTO(SubActivities forma) {
		setNumber(String.valueOf(forma.getNumber()));
		setActivityID(String.valueOf(forma.getActivityID()));
		setNameAct(forma.getNameAct());
		setDescription(forma.getDescription());
		setActive(String.valueOf(forma.getActive()));
		setOrden(String.valueOf(forma.getOrden()));
		
		setIdPersons(forma.getActUser());
		
	}
	

	public String getNumber() {
		return number;
	}

	public int getNumberInt() {
		return ToolsHTML.parseInt(number);
	}

	public void setNumber(String number) {
		this.number = number;
	}


	public String getActivityID() {
		return activityID;
	}

	public int getActivityIDInt() {
		return ToolsHTML.parseInt(activityID);
	}

	public void setActivityID(String activityID) {
		this.activityID = activityID;
	}


	public String getNameAct() {
		return nameAct;
	}


	public void setNameAct(String nameAct) {
		this.nameAct = nameAct;
	}


	public String getDescription() {
		return description;
	}


	public void setDescription(String description) {
		this.description = description;
	}


	public String getActive() {
		return active;
	}

	public int getActiveInt() {
		return ToolsHTML.parseInt(active);
	}

	public void setActive(String active) {
		this.active = active;
	}


	public String getOrden() {
		return orden;
	}

	public int getOrdenInt() {
		return ToolsHTML.parseInt(orden);
	}

	public void setOrden(String orden) {
		this.orden = orden;
	}

	
	public Set getIdPersons() {
		return idPersons;
	}

	public void setIdPersons(Set<String> idPersons) {
		this.idPersons = idPersons;
	}

	@Override
	public String toString() {

		StringBuilder builder = new StringBuilder();
		builder.append("SubActivitiesTO [");
		builder.append("  number=").append(number);
		builder.append(", activityID=").append(activityID);
		builder.append(", nameAct=").append(nameAct);
		builder.append(", description=").append(description);
		builder.append(", active=").append(active);
		builder.append(", orden=").append(orden);
		builder.append("]");
		
		return builder.toString();
	}
	
	
	
	
}
