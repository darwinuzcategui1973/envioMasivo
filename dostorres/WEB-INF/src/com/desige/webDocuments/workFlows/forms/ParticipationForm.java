package com.desige.webDocuments.workFlows.forms;

import java.io.Serializable;

import com.desige.webDocuments.utils.beans.SuperActionForm;

/**
 * Title: ParticipationForm.java <br/>
 * Copyright: (c) 2004 Focus Consulting C.A.<br/>
 * 
 * @author Ing. Roger Rodríguez (RR)
 * @author Ing. Simón Rodríguez (SR)
 * @version WebDocuments v3.0 <br/>
 *          Changes:<br/>
 *          <ul>
 *          <li>30/08/2004 (RR) Creation</li>
 *          <li>12/07/2004 (SR) Se agrego bean eliminar,end.</li>
 *          <li>27/07/2004 (SR) Se agrego bean Location,IdNodee.</li>
 *          </ul>
 */
public class ParticipationForm extends SuperActionForm implements Serializable {
	private int row;
	private String idParticipation;
	private String nameUser;
	private String claveUser;
	private int statu;
	private String dateReply;
	private String result;
	private String commentsUser;
	private int idWorkFlow;
	private String idMovement;
	private String secuential;
	private String conditional;
	private String expire;
	private String notified;
	private int idDocument;
	private String statuDoc;
	private boolean isOwner;
	private int numVersion;
	private String charge;
	private String eliminar;
	//ydavila Ticket 001-00-003023
	private String cambiar;
	
	private String end;
	private boolean borradorIsAprobado;
	private String idLocation;
	private String idNode;
	private long idFather;
	// ID unico del Flujo de Trabajo
	private long idFlexFlow;
	// ID del Flujo Parametrico
	private long numAct;
	// UserName Owner WF
	private String ownerWF;
	// Nombre de la Actividad en la Cual Participa el usuario
	private String activity;
	// email del Usuario
	private String email;
	private String typeReasigne; // Se usa para saber si se reasigna un FTP a
									// una actividad (1) o un usuario (2)
	private String idUser; // Contiene el id del usuario donde se va a reiniciar
							// el FTP
	private String idAct; // Contiene el id de la actividad donde se va a
							// reiniciar el FTP
	private boolean isPrintWF = false;
	private String typeWF; // contiene el tipo de flujo, aprobacion, revision,
							// etc
	//ydavila Ticket 001-00-003023
	private String subtypeWF; // contiene el subtipode flujo de revisión (3=cambio, 4=eliminación)
	private boolean isCompleteFlexFlow = false;

	public String getIdNode() {
		return idNode;
	}

	public void setIdNode(String idNode) {
		this.idNode = idNode;
	}

	public String getIdLocation() {
		return idLocation;
	}

	public void setIdLocation(String idLocation) {
		this.idLocation = idLocation;
	}

	public boolean getBorradorIsAprobado() {
		return borradorIsAprobado;
	}

	public void setBorradorIsAprobado(boolean borradorIsAprobado) {
		this.borradorIsAprobado = borradorIsAprobado;
	}

	public String getEnd() {
		return end;
	}

	public void setEnd(String end) {
		this.end = end;
	}

	public String getEliminar() {
		return eliminar;
	}

	public void setEliminar(String eliminar) {
		this.eliminar = eliminar;
	}
	
	//ydavila Ticket 001-00-003023
	public String getCambiar() {
		return cambiar;
	}
	public void setCambiar(String cambiar) {
		this.cambiar = cambiar;
	}

	public ParticipationForm() {
	}

	public ParticipationForm(String idParticipation, String nameUser) {
		setIdParticipation(idParticipation);
		setNameUser(nameUser);
	}

	public String getIdParticipation() {
		return idParticipation;
	}

	public void setIdParticipation(String idParticipation) {
		this.idParticipation = idParticipation;
	}

	public String getNameUser() {
		return nameUser;
	}

	public void setNameUser(String nameUser) {
		this.nameUser = nameUser;
	}

	public void setClaveUser(String clavUser) {
		this.claveUser = claveUser;
	}

	public int getStatu() {
		return statu;
	}

	public void setStatu(int statu) {
		this.statu = statu;
	}

	public String getDateReply() {
		return dateReply;
	}

	public void setDateReply(String dateReply) {
		this.dateReply = dateReply;
	}

	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
	}

	// public String getComments() {
	// return comments;
	// }
	//
	// public void setComments(String comments) {
	// this.comments = comments;
	// }

	public int getRow() {
		return row;
	}

	public void setRow(int row) {
		this.row = row;
	}

	public int getIdWorkFlow() {
		return idWorkFlow;
	}

	public void setIdWorkFlow(int idWorkFlow) {
		this.idWorkFlow = idWorkFlow;
	}

	public String getCommentsUser() {
		return commentsUser;
	}

	public void setCommentsUser(String commentsUser) {
		this.commentsUser = commentsUser;
	}

	public String getIdMovement() {
		return idMovement;
	}

	public void setIdMovement(String idMovement) {
		this.idMovement = idMovement;
	}

	public String getSecuential() {
		if (secuential != null) {
			secuential = secuential.toLowerCase().trim();
			if (secuential.length() == 0) {
				secuential = "0";
			}
			if ("true".equalsIgnoreCase(secuential)) {
				secuential = "0";
			}
			if ("false".equalsIgnoreCase(secuential)) {
				secuential = "1";
			}
		}

		return secuential;
	}

	public void setSecuential(String secuential) {
		this.secuential = secuential;
	}

	public String getConditional() {
		return conditional;
	}

	public void setConditional(String conditional) {
		this.conditional = conditional;
	}

	public String getExpire() {
		return expire;
	}

	public void setExpire(String expire) {
		this.expire = expire;
	}

	public String getNotified() {
		return notified;
	}

	public void setNotified(String notified) {
		this.notified = notified;
	}

	public int getIdDocument() {
		return idDocument;
	}

	public void setIdDocument(int idDocument) {
		this.idDocument = idDocument;
	}

	public String getStatuDoc() {
		return statuDoc;
	}

	public void setStatuDoc(String statuDoc) {
		this.statuDoc = statuDoc;
	}

	public boolean isOwner() {
		return isOwner;
	}

	public void setisOwner(boolean owner) {
		isOwner = owner;
	}

	public int getNumVersion() {
		return numVersion;
	}

	public void setNumVersion(int numVersion) {
		this.numVersion = numVersion;
	}

	public String getCharge() {
		return charge;
	}

	public void setCharge(String charge) {
		this.charge = charge;
	}

	public long getIdFather() {
		return idFather;
	}

	public void setIdFather(long idFather) {
		this.idFather = idFather;
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

	public String getID() {
		return this.idParticipation + "_" + this.row;
	}

	public String getOwnerWF() {
		return ownerWF;
	}

	public void setOwnerWF(String ownerWF) {
		this.ownerWF = ownerWF;
	}

	public String getActivity() {
		return activity;
	}

	public void setActivity(String activity) {
		this.activity = activity;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getTypeReasigne() {
		return typeReasigne;
	}

	public void setTypeReasigne(String typeReasigne) {
		this.typeReasigne = typeReasigne;
	}

	public String getIdUser() {
		return idUser;
	}

	public void setIdUser(String idUser) {
		this.idUser = idUser;
	}

	public String getIdAct() {
		return idAct;
	}

	public void setIdAct(String idAct) {
		this.idAct = idAct;
	}

	public boolean isCompleteFlexFlow() {
		return isCompleteFlexFlow;
	}

	public void setCompleteFlexFlow(boolean isCompleteFlexFlow) {
		this.isCompleteFlexFlow = isCompleteFlexFlow;
	}

	public boolean isPrintWF() {
		return isPrintWF;
	}

	public void setPrintWF(boolean isPrintWF) {
		this.isPrintWF = isPrintWF;
	}

	public void setTypeWF(String typeWF) {
		this.typeWF = typeWF;
	}

	public String getTypeWF() {
		return typeWF;
	}
	
	//ydavila Ticket 001-00-003023
	public void setSubtypeWF(String subtypeWF) {
		this.subtypeWF = subtypeWF;
	}
	public String getSubtypeWF() {
		return subtypeWF;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("ParticipationForm [row=").append(row)
				.append(", idParticipation=").append(idParticipation)
				.append(", nameUser=").append(nameUser)
				.append(", statu=").append(statu)
				.append(", dateReply=").append(dateReply)
				.append(", result=").append(result)
				.append(", commentsUser=").append(commentsUser)
				.append(", idWorkFlow=").append(idWorkFlow)
				.append(", idMovement=").append(idMovement)
				.append(", secuential=").append(secuential)
				.append(", conditional=").append(conditional)
				.append(", expire=").append(expire)
				.append(", notified=").append(notified)
				.append(", idDocument=").append(idDocument)
				.append(", statuDoc=").append(statuDoc)
				.append(", isOwner=").append(isOwner)
				.append(", numVersion=").append(numVersion)
				.append(", charge=").append(charge)
				.append(", eliminar=").append(eliminar)
				.append(", end=").append(end)
				.append(", borradorIsAprobado=").append(borradorIsAprobado)
				.append(", idLocation=").append(idLocation)
				.append(", idNode=").append(idNode)
				.append(", idFather=").append(idFather)
				.append(", idFlexFlow=").append(idFlexFlow)
				.append(", numAct=").append(numAct)
				.append(", ownerWF=").append(ownerWF)
				.append(", activity=").append(activity)
				.append(", email=").append(email)
				.append(", typeReasigne=").append(typeReasigne)
				.append(", idUser=").append(idUser)
				.append(", idAct=").append(idAct)
				.append(", isPrintWF=").append(isPrintWF)
				.append(", typeWF=").append(typeWF)
				.append(", isCompleteFlexFlow=").append(isCompleteFlexFlow)
				//ydavila Ticket 001-00-003023
				.append(", cambiar=").append(cambiar)
				.append("]");
		return builder.toString();
	}
	
	
}
