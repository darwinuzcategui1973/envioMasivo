package com.desige.webDocuments.utils.beans;

import java.io.Serializable;

/**
 * Title: Search.java <br/> Copyright: (c) 2004 Focus Consulting C.A.<br/>
 * 
 * @author Ing. Nelson Crespo (NC)
 * @version WebDocuments v3.0 <br/> Changes:<br/>
 *          <ul>
 *          <li>29/03/2004 (NC) Creation </li>
 *          </ul>
 */

public class Search implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -6621368705119936170L;
	private String id;
	//ydavila Ticket 001-00-003023
	private String respsolcambio;
	private String respsolelimin;
	private String respsolimpres;
	
	private String descripcalidad;
	private String descript;
	private String comment;
	private String aditionalInfo;
	private String contact;
	private String idWorkFlow;
	private String sendToFlexWF;
	private String actNumber;
	private String typesetter;
	private String ownerTypeDoc;
	private String idNode;
	private String checker;
	private String lote;
	private String publicDoc;
	private String focusFlag;
	private String sistemaGestion;
	private String indice; 
	private String documentRequired; 
	private String auditProcess; 
    
	private String field1; 
	private String field2; 
	private String field3; 
	private String field4; 
	private String field5; 
	private String field6; 
	private String field7; 
	private String field8; 
	private String field9; 
	private String field10; 
	private String idNodeService;

	public Search() {
	}

	public Search(String id, String descrip) {
		this.id = id;
		setDescript(descrip);
	}
	
	public Search( String descrip) {
		this.id = descrip;
		setDescript(descrip);
	}
	public Search(String id, String descrip, String aditionalInfo) {
		this.id = id;
		setDescript(descrip);
		setAditionalInfo(aditionalInfo);
	}
	
	public String getFocusFlag() {
		return focusFlag;
	}
	
	public void setFocusFlag(String focusFlag) {
		this.focusFlag = focusFlag;
	}
	
	public boolean isFocusFlag() {
		return "S".equalsIgnoreCase(focusFlag);
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
	
	//ydavila Ticket 001-00-003023
	public String getrespsolcambio() {
		return this.respsolcambio;
	}
	public String getrespsolelimin() {
		return respsolelimin;
	}
	public String getrespsolimpres() {
		return respsolimpres;
	}
	public void setrespsolcambio(String respsolcambio) {
		this.respsolcambio = respsolcambio;
	}
	public void setrespsolelimin(String respsolelimin) {
		this.respsolelimin = respsolelimin;
	}
	public void setrespsolimpres(String respsolimpres) {
		this.respsolimpres = respsolimpres;
	}
	//ydavila Ticket 001-00-003023
	
	public String getDescripCalidad() {
		return descripcalidad;
	}
	public void setDescripCalidad(String descripcalidad) {
		this.descripcalidad = descripcalidad;
	}
	
	
	
	public String getDescript() {
		return descript;
	}
	
	public String getComment() {
		return comment;
	}
	

	public void setDescript(String descript) {
		this.descript = descript;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}
	
	public boolean equals(Object o) {
		if (o == null) {
			return false;
		}
		if (this == o) {
			return true;
		}
		if (o instanceof Search) {
			Search oParam = (Search) o;
			if (oParam.getId().equalsIgnoreCase(this.getId())) {
				return true;
			}
		}
		return false;
	}

	public int hashCode() {
		return this.getId().hashCode();
	}

	public String toString() {
		return this.getId();
	}

	public String getAditionalInfo() {
		return aditionalInfo;
	}

	public void setAditionalInfo(String aditionalInfo) {
		this.aditionalInfo = aditionalInfo;
	}

	public String getContact() {
		return contact;
	}

	public void setContact(String contact) {
		this.contact = contact;
	}

	public String getIdWorkFlow() {
		return idWorkFlow;
	}

	public void setIdWorkFlow(String idWorkFlow) {
		this.idWorkFlow = idWorkFlow;
	}

	public String getSendToFlexWF() {
		return sendToFlexWF;
	}

	public void setSendToFlexWF(String sendToFlexWF) {
		this.sendToFlexWF = sendToFlexWF;
	}

	public String getActNumber() {
		return actNumber;
	}

	public void setActNumber(String actNumber) {
		this.actNumber = actNumber;
	}


	public String getTypesetter() {
		return typesetter;
	}

	public void setTypesetter(String typesetter) {
		this.typesetter = typesetter;
	}

	public String getIdNode() {
		return idNode;
	}

	public void setIdNode(String idNode) {
		this.idNode = idNode;
	}

	public String getOwnerTypeDoc() {
		return ownerTypeDoc;
	}

	public void setOwnerTypeDoc(String ownerTypeDoc) {
		this.ownerTypeDoc = ownerTypeDoc;
	}

	public String getChecker() {
		return checker;
	}

	public void setChecker(String checker) {
		this.checker = checker;
	}

	public String getLote() {
		return lote;
	}

	public void setLote(String lote) {
		this.lote = lote;
	}

	public String getPublicDoc() {
		return publicDoc;
	}

	public void setPublicDoc(String publicDoc) {
		this.publicDoc = publicDoc;
	}

	public String getSistemaGestion() {
		return sistemaGestion;
	}

	public void setSistemaGestion(String sistemaGestion) {
		this.sistemaGestion = sistemaGestion;
	}

	public String getIndice() {
		return indice;
	}

	public void setIndice(String indice) {
		this.indice = indice;
	}

	public String getDocumentRequired() {
		return documentRequired;
	}

	public void setDocumentRequired(String documentRequired) {
		this.documentRequired = documentRequired;
	}

	public String getField1() {
		return field1;
	}

	public void setField1(String field1) {
		this.field1 = field1;
	}

	public String getField2() {
		return field2;
	}

	public void setField2(String field2) {
		this.field2 = field2;
	}

	public String getField3() {
		return field3;
	}

	public void setField3(String field3) {
		this.field3 = field3;
	}

	public String getField4() {
		return field4;
	}

	public void setField4(String field4) {
		this.field4 = field4;
	}

	public String getField5() {
		return field5;
	}

	public void setField5(String field5) {
		this.field5 = field5;
	}

	public String getField6() {
		return field6;
	}

	public void setField6(String field6) {
		this.field6 = field6;
	}

	public String getField7() {
		return field7;
	}

	public void setField7(String field7) {
		this.field7 = field7;
	}

	public String getField8() {
		return field8;
	}

	public void setField8(String field8) {
		this.field8 = field8;
	}

	public String getField9() {
		return field9;
	}

	public void setField9(String field9) {
		this.field9 = field9;
	}

	public String getField10() {
		return field10;
	}

	public void setField10(String field10) {
		this.field10 = field10;
	}

	public String getAuditProcess() {
		return auditProcess;
	}

	public void setAuditProcess(String auditProcess) {
		this.auditProcess = auditProcess;
	}
	
	public String getIdNodeService() {
		return idNodeService;
	}

	public void setIdNodeService(String idNodeService) {
		this.idNodeService = idNodeService;
	}
	
	
}
