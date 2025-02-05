package com.desige.webDocuments.persistent.managers;

public class StructWeb {
	
	private String idNode;
	private String idNodeParent;
	private String idGroup;
	private String idPerson;

	public StructWeb() {
		
	}
	public StructWeb(String idNode, String idNodeParent, String idGroup, String idPerson) {
		setIdNode(idNode);
		setIdNodeParent(idNodeParent);
		setIdGroup(idGroup);
		setIdPerson(idPerson);
	}

	public String getIdGroup() {
		return idGroup;
	}

	public void setIdGroup(String idGroup) {
		this.idGroup = idGroup;
	}

	public String getIdNode() {
		return idNode;
	}

	public void setIdNode(String idNode) {
		this.idNode = idNode;
	}

	public String getIdNodeParent() {
		return idNodeParent;
	}

	public void setIdNodeParent(String idNodeParent) {
		this.idNodeParent = idNodeParent;
	}

	public String getIdPerson() {
		return idPerson;
	}

	public void setIdPerson(String idPerson) {
		this.idPerson = idPerson;
	}
	
	
	
	
	
	
}
