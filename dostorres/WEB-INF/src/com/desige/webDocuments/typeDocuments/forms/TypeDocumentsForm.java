package com.desige.webDocuments.typeDocuments.forms;

import java.io.Serializable;

import org.hibernate.id.SelectGenerator;

import com.desige.webDocuments.utils.beans.SuperActionForm;

/**
 * Title: TypeDocumentsForm.java<br>
 * Copyright: (c) 2003 Consis International<br>
 * Company: Consis International (CON)<br>
 * 
 * @author Nelson Crespo (NC)
 * @author Simón Rodriguéz (SR)
 * @version Acsel-e v2.2 <br>
 *          Changes:<br>
 *          <ul>
 *          <li> 22/08/2004 (NC) Creation </li>
 *          <li> 21/06/2005 (SR) Se agrego un parametro type</li>
 *          <ul>
 */
public class TypeDocumentsForm extends SuperActionForm implements Serializable {
	private String id;
	private String name;
	private String type;

	private String nameFile;
	
    private byte expireDoc;
    private byte expireDrafts;
    private byte deadDoc;
    private String monthsExpireDrafts;
    private String unitTimeExpireDrafts;
    private String monthsExpireDocs;
    private String unitTimeExpire;
    private String monthsDeadDocs;
    private String unitTimeDead;
    private byte firstPage;
    private byte sendToFlexWF;
    private int actNumber;
    private int ownerTypeDoc;
    private int idNodeTypeDoc;
    private int typesetter;
    private int checker;
    private String lote;
    private byte publicDoc;
    private String equivalencia;
    private int generateRequestSacop;
	

	public TypeDocumentsForm() {
		setType("0");
		setFirstPage((byte)1);
		setSendToFlexWF((byte)1);
		setPublicDoc((byte)1);
		setGenerateRequestSacop(0);
		
	}

	public String getNameFile() {
		return nameFile;
	}

	public void setNameFile(String nameFile) {
		this.nameFile = nameFile;
	}

	// SIMON 21 DE JUNIO 2005 INICIO
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	// SIMON 21 DE JUNIO 2005 FIN
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getMonthsExpireDrafts() {
		return monthsExpireDrafts;
	}

	public void setMonthsExpireDrafts(String monthsExpireDrafts) {
		this.monthsExpireDrafts = monthsExpireDrafts;
	}

	public String getUnitTimeExpireDrafts() {
		return unitTimeExpireDrafts;
	}

	public void setUnitTimeExpireDrafts(String unitTimeExpireDrafts) {
		this.unitTimeExpireDrafts = unitTimeExpireDrafts;
	}
	
	public String getMonthsExpireDocs() {
		return monthsExpireDocs;
	}

	public void setMonthsExpireDocs(String monthsExpireDocs) {
		this.monthsExpireDocs = monthsExpireDocs;
	}

	public String getUnitTimeExpire() {
		return unitTimeExpire;
	}

	public void setUnitTimeExpire(String unitTimeExpire) {
		this.unitTimeExpire = unitTimeExpire;
	}

	public byte getExpireDoc() {
		return expireDoc;
	}

	public void setExpireDoc(byte expireDoc) {
		this.expireDoc = expireDoc;
	}

	public byte getExpireDrafts() {
		return expireDrafts;
	}

	public void setExpireDrafts(byte expireDrafts) {
		this.expireDrafts = expireDrafts;
	}
	
	public byte getFirstPage() {
		return firstPage;
	}

	public void setFirstPage(byte firstPage) {
		this.firstPage = firstPage;
	}

	public int getActNumber() {
		return actNumber;
	}

	public void setActNumber(int actNumber) {
		this.actNumber = actNumber;
	}

	public byte getSendToFlexWF() {
		return sendToFlexWF;
	}

	public void setSendToFlexWF(byte sendToFlexWF) {
		this.sendToFlexWF = sendToFlexWF;
	}
	
	public int getOwnerTypeDoc() {
		return ownerTypeDoc;
	}

	public void setOwnerTypeDoc(int ownerTypeDoc) {
		this.ownerTypeDoc = ownerTypeDoc;
	}

	public int getTypesetter() {
		return typesetter;
	}

	public void setTypesetter(int typesetter) {
		this.typesetter = typesetter;
	}
	

	public int getIdNodeTypeDoc() {
		return idNodeTypeDoc;
	}

	public void setIdNodeTypeDoc(int idNodeTypeDoc) {
		this.idNodeTypeDoc = idNodeTypeDoc;
	}

	public int getChecker() {
		return checker;
	}

	public void setChecker(int checker) {
		this.checker = checker;
	}
	
	
	public String getLote() {
		return lote;
	}

	public void setLote(String lote) {
		this.lote = lote;
	}

	public String getMonthsDeadDocs() {
		return monthsDeadDocs;
	}

	public void setMonthsDeadDocs(String monthsDeadDocs) {
		this.monthsDeadDocs = monthsDeadDocs;
	}

	public String getUnitTimeDead() {
		return unitTimeDead;
	}

	public void setUnitTimeDead(String unitTimeDead) {
		this.unitTimeDead = unitTimeDead;
	}

	public byte getDeadDoc() {
		return deadDoc;
	}

	public void setDeadDoc(byte deadDoc) {
		this.deadDoc = deadDoc;
	}

	public byte getPublicDoc() {
		return publicDoc;
	}

	public void setPublicDoc(byte publicDoc) {
		this.publicDoc = publicDoc;
	}
	
	public String getEquivalencia() {
		return equivalencia;
	}
	
	public void setEquivalencia(String equivalencia) {
		this.equivalencia = equivalencia;
	}
	
	public int getGenerateRequestSacop() {
		return 1; //generateRequestSacop; // valor colocado para que sea siempre positivo
	}

	public void setGenerateRequestSacop(int generateRequestSacop) {
		this.generateRequestSacop = generateRequestSacop;
	}

	public void cleanForm() {
		setId("");
		setName("");
		setType("");
		setTypesetter(0);
		setOwnerTypeDoc(0);
		setIdNodeTypeDoc(0);
		setChecker(0);
		setLote("");
		setGenerateRequestSacop(1);
	}
	
}
