package com.desige.webDocuments.workFlows.forms;

import java.io.Serializable;

import com.desige.webDocuments.utils.beans.SuperActionForm;
import com.focus.qweb.bean.Person;

/**
 * Title: DataUserWorkFlowForm.java <br/>
 * Copyright: (c) 2004 Focus Consulting C.A.<br/>
 * @author Ing. Nelson Crespo
 * @version WebDocuments v3.0
 * <br/>
 * Changes:<br/> 
 * <ul>
 *      <li> 29-08-2004 (NC) Creation </li>
 </ul>
 */
public class DataUserWorkFlowForm extends SuperActionForm implements Serializable, Comparable<DataUserWorkFlowForm> {
    private int idWorkFlow;
    private String nameDocument;
    private String idUser;
    private String nameUser;
    private int typeWF;
    //ydavila Ticket 001-00-003023
    private int subTypeWF;
    private String subTypeWFdesc;    
    private int typeDOC;
    private String dateExpire;
	private String row;
	private int idMovement;
    private boolean isResponse;
    private boolean owner;
    private String idVersion;
    private int idDocument;
    private String prefix;
    private String number;
    private boolean flexFlow;
    private String nameWorkFlow;
    private String statuAnt;
    private long idFlexFlow;
    private int toImpresion;
    private String dateCreation;
    
    /**
     * Bean del tipo persona para almacenar los datos de tipo persona
     * para el usuario que actualmente esta trabajando en este flujo
     */
    private Person personBean;
    
    /**
     * Bean del tipo persona para almacenar los datos de tipo persona
     * para el usuario que esta solicitando dicho flujo
     */
    private Person flowRequieredByPersonBean;

    /**
     * Representa la fecha de Completacion del flujo
     *
     * @author Luis Cisneros
     * 01/03/2007
     */
     private String dateCompleted;

    public DataUserWorkFlowForm() {
        setFlexFlow(false);
    }

    public int getTypeDOC() {
        return typeDOC;
    }

    public void setTypeDOC(int typeDOC) {
        this.typeDOC = typeDOC;
    }

    public int getIdWorkFlow() {
        return idWorkFlow;
    }

    public void setIdWorkFlow(int idWorkFlow) {
        this.idWorkFlow = idWorkFlow;
    }

    public String getNameDocument() {
        return nameDocument;
    }

    public void setNameDocument(String nameDocument) {
        this.nameDocument = nameDocument;
    }

    public String getIdUser() {
        return idUser;
    }

    public void setIdUser(String idUser) {
        this.idUser = idUser;
    }

    public String getNameUser() {
        return nameUser;
    }

    public void setNameUser(String nameUser) {
        this.nameUser = nameUser;
    }

    public int getTypeWF() {
        return typeWF;
    }

    public void setTypeWF(int typeWF) {
        this.typeWF = typeWF;
    }
    
    //ydavila Ticket 001-00-003023
    public int getsubTypeWF() {
        return subTypeWF;
    }
    public void setsubTypeWF(int subTypeWF) {
        this.subTypeWF = subTypeWF;
    }
    public String getsubTypeWFdesc() {
        return subTypeWFdesc;
    }
    public void setsubTypeWFdesc(String subTypeWFdesc) {
        this.subTypeWFdesc = subTypeWFdesc;
    }    

    public String getDateExpire() {
        return dateExpire;
    }

    public void setDateExpire(String dateExpire) {
        this.dateExpire = dateExpire;
    }

    public String getRow() {
        return row;
    }

    public void setRow(String row) {
        this.row = row;
    }

    public int getIdMovement() {
        return idMovement;
    }

    public void setIdMovement(int idMovement) {
        this.idMovement = idMovement;
    }

    public boolean isResponse() {
        return isResponse;
    }

    public void setResponse(boolean response) {
        isResponse = response;
    }

    public boolean isOwner() {
        return owner;
    }

    public void setOwner(boolean owner) {
        this.owner = owner;
    }

    public String getIdVersion() {
        return idVersion;
    }

    public void setIdVersion(String idVersion) {
        this.idVersion = idVersion;
    }

    public int getIdDocument() {
        return idDocument;
    }

    public void setIdDocument(int idDocument) {
        this.idDocument = idDocument;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public boolean isFlexFlow() {
        return flexFlow;
    }

    public void setFlexFlow(boolean flexFlow) {
        this.flexFlow = flexFlow;
    }

    public String getNameWorkFlow() {
        return nameWorkFlow;
    }

    public void setNameWorkFlow(String nameWorkFlow) {
        this.nameWorkFlow = nameWorkFlow;
    }

    public String getStatuAnt() {
        return statuAnt;
    }

    public void setStatuAnt(String statuAnt) {
        this.statuAnt = statuAnt;
    }

    public long getIdFlexFlow() {
        return idFlexFlow;
    }

    public void setIdFlexFlow(long idFlexFlow) {
        this.idFlexFlow = idFlexFlow;
    }

    public String getDateCompleted() {
        return dateCompleted;
    }

    public void setDateCompleted(String dateCompleted) {
        this.dateCompleted = dateCompleted;
    }

	public int getToImpresion() {
		return toImpresion;
	}

	public void setToImpresion(int toImpresion) {
		this.toImpresion = toImpresion;
	}
	
	public Person getPersonBean() {
		return personBean;
	}
	
	public void setPersonBean(Person personBean) {
		this.personBean = personBean;
	}

	public Person getFlowRequieredByPersonBean() {
		return flowRequieredByPersonBean;
	}
	
	public void setFlowRequieredByPersonBean(Person flowRequieredByPersonBean) {
		this.flowRequieredByPersonBean = flowRequieredByPersonBean;
	}
	
	public String getDateCreation() {
		return dateCreation;
	}
	
	public void setDateCreation(String dateCreation) {
		this.dateCreation = dateCreation;
	}
	
	public int compareTo(DataUserWorkFlowForm other) {
		// TODO Auto-generated method stub
		if((this.personBean != null) && (other.personBean != null)){
			int compare1 = this.personBean.getApellido().compareTo(
					other.personBean.getApellido());
			if (compare1 != 0) {
				return compare1;
			} else {
				return this.personBean.getNombre().compareTo(other.personBean.getNombre());
			}
		} else {
			if (nameUser != null){
				return this.nameUser.compareTo(other.nameUser);
			} else {
				return Integer.valueOf(this.hashCode()).compareTo(
						other.hashCode());
			}
		}
	}    
}
