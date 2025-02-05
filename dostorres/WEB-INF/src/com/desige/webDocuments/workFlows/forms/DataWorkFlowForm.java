package com.desige.webDocuments.workFlows.forms;

import java.io.Serializable;
import java.util.Collection;

import com.desige.webDocuments.utils.beans.SuperActionForm;

/**
 * Title: DataWorkFlowForm.java <br/>
 * Copyright: (c) 2004 Focus Consulting C.A.<br/>
 * 
 * @author Ing. Roger Rodríguez
 * @author Ing. Simon Rodríguez (SR)
 * @version WebDocuments v3.0 <br/>
 *          Changes:<br/>
 *          <ul>
 *          <li>26/08/2004 (RR) Creation</li>
 *          <li>19/05/2005 Agregar idnode(SR)</li>
 *          <li>11/07/2005 Agregar bean eliminar(SR)</li>
 *          <li>03/08/2005 Agregar bean idVersion(SR)</li>
 * 
 *          </ul>
 */
public class DataWorkFlowForm extends SuperActionForm implements Serializable {
	private String number;
	private String prefix;
	private String idWorkFlow;
	private String nameDocument;
	private String request;
	private String dateExpire;
	private String dateBegin;
	private String dateEnd;
	private String copy;
	private String sequential;
	private String conditional;
	private byte eliminar;
	private String notified;
	private String statu;
	private String result;
	private int numDocument;
	private int version;
	private String idVersion;
	private String typeWF;
	//ydavila
	private String subtypeWF;
	private byte cambiar;
	
	private String comments;
	private int idMovement;
	private int typeMovement;
	private String idPK;
	private String statuDoc;
	private int numVersion;
	private String idHistory;
	private String nameFile;
	private String typeDoc;
	private String charge;
	private int idnode;
	// Nombre del Flujo Parametrico
	private String nameWF;
	// Usuarios participantes en la Actividad
	private Collection users;
	// Comentarios de los Usurios participantes en el Flujo de Trabajo
	private Collection commentsUsrs;
	// Id del Flujo Parametrico
	private long idFlexFlow;
	// Número del Flujo
	private long numAct;
	// Indica si es un Flex Flow o No
	private boolean flexFlow;

	public DataWorkFlowForm() {
		setFlexFlow(false);
	}

	public String getIdVersion() {
		return idVersion;
	}

	public void setIdVersion(String idVersion) {
		this.idVersion = idVersion;
	}

	public byte getEliminar() {
		return eliminar;
	}

	public void setEliminar(byte eliminar) {
		this.eliminar = eliminar;
	}
	
	//ydavila Ticket 001-00-003023
	public byte getCambiar() {
		return cambiar;
	}
	public void setCambiar(byte cambiar) {
		this.cambiar = cambiar;
	}
	public int getIdnode() {
		return idnode;
	}

	public void setIdnode(int idnode) {
		this.idnode = idnode;
	}

	public String getNameDocument() {
		return nameDocument;
	}

	public void setNameDocument(String nameDocument) {
		this.nameDocument = nameDocument;
	}

	public String getRequest() {
		return request;
	}

	public void setRequest(String request) {
		this.request = request;
	}

	public String getDateExpire() {
		return dateExpire;
	}

	public void setDateExpire(String dateExpire) {
		this.dateExpire = dateExpire;
	}

	public String getDateBegin() {
		return dateBegin;
	}

	public void setDateBegin(String dateBegin) {
		this.dateBegin = dateBegin;
	}

	public String getDateEnd() {
		return dateEnd;
	}

	public void setDateEnd(String dateEnd) {
		this.dateEnd = dateEnd;
	}

	public String getSequential() {
		return sequential;
	}

	public void setSequential(String sequential) {
		this.sequential = sequential;
	}

	public String getConditional() {
		return conditional;
	}

	public void setConditional(String conditional) {
		this.conditional = conditional;
	}

	public String getStatu() {
		return statu;
	}

	public void setStatu(String statu) {
		this.statu = statu;
	}

	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
	}

	public int getNumDocument() {
		return numDocument;
	}

	public void setNumDocument(int numDocument) {
		this.numDocument = numDocument;
	}

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	public String getTypeWF() {
		return typeWF;
	}

	public void setTypeWF(String typeWF) {
		this.typeWF = typeWF;
	}
	
	//ydavila Elmor
	public String getSubtypeWF() {
		return subtypeWF;
	}
	public void setSubtypeWF(String subtypeWF) {
		this.subtypeWF = subtypeWF;
	}

	public String getIdWorkFlow() {
		return idWorkFlow;
	}

	public void setIdWorkFlow(String idWorkFlow) {
		this.idWorkFlow = idWorkFlow;
	}

	public String getComments() {
		return comments;
	}

	public void setComments(String comments) {
		this.comments = comments;
	}

	public int getIdMovement() {
		return idMovement;
	}

	public void setIdMovement(int idMovement) {
		this.idMovement = idMovement;
	}

	public int getTypeMovement() {
		return typeMovement;
	}

	public void setTypeMovement(int typeMovement) {
		this.typeMovement = typeMovement;
	}

	public String getIdPK() {
		return idPK;
	}

	public void setIdPK(String idPK) {
		this.idPK = idPK;
	}

	public String getStatuDoc() {
		return statuDoc;
	}

	public void setStatuDoc(String statuDoc) {
		this.statuDoc = statuDoc;
	}

	public int getNumVersion() {
		return numVersion;
	}

	public void setNumVersion(int numVersion) {
		this.numVersion = numVersion;
	}

	public String getIdHistory() {
		return idHistory;
	}

	public void setIdHistory(String idHistory) {
		this.idHistory = idHistory;
	}

	public String getNotified() {
		return notified;
	}

	public void setNotified(String notified) {
		this.notified = notified;
	}

	public String getNameFile() {
		return nameFile;
	}

	public void setNameFile(String nameFile) {
		this.nameFile = nameFile;
	}

	public String getTypeDoc() {
		return typeDoc;
	}

	public void setTypeDoc(String typeDoc) {
		this.typeDoc = typeDoc;
	}

	public String getCharge() {
		return charge;
	}

	public void setCharge(String charge) {
		this.charge = charge;
	}

	public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = number;
	}

	public String getPrefix() {
		return prefix;
	}

	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}

	public String getNameWF() {
		return nameWF;
	}

	public void setNameWF(String nameWF) {
		this.nameWF = nameWF;
	}

	public Collection getUsers() {
		return users;
	}

	public void setUsers(Collection users) {
		this.users = users;
	}

	public long getIdFlexFlow() {
		return idFlexFlow;
	}

	public void setIdFlexFlow(long idFlexFlow) {
		this.idFlexFlow = idFlexFlow;
	}

	public long getNumAct() {
		return numAct;
	}

	public void setNumAct(long numAct) {
		this.numAct = numAct;
	}

	public boolean isFlexFlow() {
		return flexFlow;
	}

	public void setFlexFlow(boolean flexFlow) {
		this.flexFlow = flexFlow;
	}

	public Collection getCommentsUsrs() {
		return commentsUsrs;
	}

	public void setCommentsUsrs(Collection commentsUsrs) {
		this.commentsUsrs = commentsUsrs;
	}

	public String getCopy() {
		return copy;
	}

	public void setCopy(String copy) {
		this.copy = copy;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("DataWorkFlowForm [number=").append(number)
				.append(", prefix=").append(prefix)
				.append(", idWorkFlow=").append(idWorkFlow)
				.append(", nameDocument=").append(nameDocument)
				.append(", request=").append(request)
				.append(", dateExpire=").append(dateExpire)
				.append(", dateBegin=").append(dateBegin)
				.append(", dateEnd=").append(dateEnd)
				.append(", copy=").append(copy)
				.append(", sequential=").append(sequential)
				.append(", conditional=").append(conditional)
				.append(", eliminar=").append(eliminar)
				.append(", notified=").append(notified)
				.append(", statu=").append(statu)
				.append(", result=").append(result)
				.append(", numDocument=").append(numDocument)
				.append(", version=").append(version)
				.append(", idVersion=").append(idVersion)
				.append(", typeWF=").append(typeWF)
				//ydavila Ticket 001-00-003023			
				.append(", comments=").append(comments)
				.append(", subtypeWF=").append(subtypeWF)
				.append(", cambiar=").append(cambiar)
				.append(", eliminar=").append(eliminar)
				.append(", idMovement=").append(idMovement)
				.append(", typeMovement=").append(typeMovement)
				.append(", idPK=").append(idPK)
				.append(", statuDoc=").append(statuDoc)
				.append(", numVersion=").append(numVersion)
				.append(", idHistory=").append(idHistory)
				.append(", nameFile=").append(nameFile)
				.append(", typeDoc=").append(typeDoc)
				.append(", charge=").append(charge)
				.append(", idnode=").append(idnode)
				.append(", nameWF=").append(nameWF)
				.append(", users=").append(users)
				.append(", commentsUsrs=").append(commentsUsrs)
				.append(", idFlexFlow=").append(idFlexFlow)
				.append(", numAct=").append(numAct)
				.append(", flexFlow=").append(flexFlow).append("]");
		return builder.toString();
	}

}
