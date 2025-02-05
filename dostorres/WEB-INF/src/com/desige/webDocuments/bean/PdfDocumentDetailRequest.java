package com.desige.webDocuments.bean;

public class PdfDocumentDetailRequest {

	private String prefixNumber;
	private String nameDocument;
	private String typeDoc;
	private String typeRelation; // O rigen / R elacionado

	public String getPrefixNumber() {
		return prefixNumber;
	}

	public void setPrefixNumber(String prefixNumber) {
		this.prefixNumber = prefixNumber;
	}

	public String getNameDocument() {
		return nameDocument;
	}

	public void setNameDocument(String nameDocument) {
		this.nameDocument = nameDocument;
	}

	public String getTypeDoc() {
		return typeDoc;
	}

	public void setTypeDoc(String typeDoc) {
		this.typeDoc = typeDoc;
	}

	public String getTypeRelation() {
		return typeRelation;
	}

	public void setTypeRelation(String typeRelation) {
		this.typeRelation = typeRelation;
	}

}
