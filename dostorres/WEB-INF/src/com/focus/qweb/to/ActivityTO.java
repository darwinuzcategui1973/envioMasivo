package com.focus.qweb.to;

import java.util.ArrayList;

import com.desige.webDocuments.activities.forms.Activities;
import com.desige.webDocuments.utils.ToolsHTML;
import com.focus.qweb.interfaces.ObjetoTO;

public class ActivityTO implements ObjetoTO {

	private static final long serialVersionUID = 1L;

	private String actNumber; //number
	private String actName; // name
	private String actDescription; // description
	private String actActive; // active
	private String actTypeDocument; // idTypeDoc
	
	ArrayList<SubActivitiesTO> subActivitiesTO = new ArrayList<SubActivitiesTO>();
	
	public ActivityTO() {
		
	}
	
	public ActivityTO(Activities forma) {
		setActNumber(String.valueOf(forma.getNumber()));
		setActName(forma.getName());
		setActDescription(forma.getDescription());
		setActActive(String.valueOf(forma.getActive()));
		setActTypeDocument(String.valueOf(forma.getIdTypeDoc()));
	}

	public String getActNumber() {
		return actNumber;
	}

	public int getActNumberInt() {
		return ToolsHTML.parseInt(actNumber);
	}

	public void setActNumber(String actNumber) {
		this.actNumber = actNumber;
	}


	public String getActName() {
		return actName;
	}


	public void setActName(String actName) {
		this.actName = actName;
	}


	public String getActDescription() {
		return actDescription;
	}


	public void setActDescription(String actDescription) {
		this.actDescription = actDescription;
	}


	public String getActActive() {
		return actActive;
	}
	
	public int getActActiveInt() {
		return ToolsHTML.parseInt(actActive);
	}
	


	public void setActActive(String actActive) {
		this.actActive = actActive;
	}


	public String getActTypeDocument() {
		return actTypeDocument;
	}
	
	public int getActTypeDocumentInt() {
		return ToolsHTML.parseInt(actTypeDocument);
	}


	public void setActTypeDocument(String actTypeDocument) {
		this.actTypeDocument = actTypeDocument;
	}

	public ArrayList<SubActivitiesTO> getSubActivitiesTO() {
		return subActivitiesTO;
	}

	public void setSubActivitiesTO(ArrayList<SubActivitiesTO> subActivitiesTO) {
		this.subActivitiesTO = subActivitiesTO;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("ActivityTO [");
		builder.append("  number=").append(actNumber);
		builder.append(", actName=").append(actName);
		builder.append(", actDescription=").append(actDescription);
		builder.append(", actActive=").append(actActive);
		builder.append(", actTypeDocument=").append(actTypeDocument);
		builder.append("]");
		return builder.toString();
	}
	
	
	
	
}
