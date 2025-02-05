package com.desige.webDocuments.document.forms;

import java.io.Serializable;

import com.desige.webDocuments.utils.beans.SuperActionForm;

/**
 * Title: CheckOutDocForm.java <br/>
 * Copyright: (c) 2004 Focus Consulting C.A.<br/>
 *
 * @author Ing. Nelson Crespo
 * @version WebDocuments v3.0
 * <br/>
 *          Changes:<br/>
 * <ul>
 *     <li> 2004-10-19 (NC) Creation </li>
 *     <li> 2006-04-17 (NC) Add field idTypeDoc </li>
 *     <li> 30/06/2006 (NC) se agregó el uso del Log y cambios para mostrar los documentos vinculados.</li> 
 * </ul>
 */
public class CheckOutDocForm extends SuperActionForm implements Serializable {
	private String idDocument;
	private String nameDocument;
	private String version;
	private String number;
	private String typeDocument;
	private String numVersion;
    private String comments;
    private String dateCheckOut;
    private String idNode;
    private String nameFile;
    // TODO: CAMBIO PARA DOBLE VERSION
    private String nameFileParalelo;
    // TODO: CAMBIO PARA DOBLE VERSION
    private String contextTypeParalelo;
    // TODO: CAMBIO PARA DOBLE VERSION
    private String path;
    
    private String expires;
    private String dateExpires;
    private String dateExpiresDrafts;
    private String mayorVer;
    private String minorVer;
    private String approved;
    private String dateApproved;
    private int numVer;
    private String idCheckOut;
    private String changeMinorVersion;
    private String statu;
    private boolean draftVersion;
    private String dateCreated;
    private boolean changeMinor;
    private String idTypeDoc;
    private String docRelations;
    private byte typeWF;
    
    private String owner;

    public CheckOutDocForm(){

	}

	public CheckOutDocForm(String idDocument,String nameDocument,String version,String number,String typeDocument) {
        setIdDocument(idDocument);
		setNameDocument(nameDocument);
		setVersion(version);
		setNumber(number);
		setTypeDocument(typeDocument);
	}

	public String getIdDocument() {
		return idDocument;
	}

	public void setIdDocument(String idDocument) {
		this.idDocument = idDocument;
	}

	public String getNameDocument() {
		return nameDocument;
	}

	public void setNameDocument(String nameDocument) {
		this.nameDocument = nameDocument;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = number;
	}

	public String getTypeDocument() {
		return typeDocument;
	}

	public void setTypeDocument(String typeDocument) {
		this.typeDocument = typeDocument;
	}

	public String getNumVersion() {
		return numVersion;
	}

	public void setNumVersion(String numVersion) {
		this.numVersion = numVersion;
	}

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public String getDateCheckOut() {
        return dateCheckOut;
    }

    public void setDateCheckOut(String dateCheckOut) {
        this.dateCheckOut = dateCheckOut;
    }

    public String getIdNode() {
        return idNode;
    }

    public void setIdNode(String idNode) {
        this.idNode = idNode;
    }

    public String getNameFile() {
        return nameFile;
    }

    public void setNameFile(String nameFile) {
        this.nameFile = nameFile;
    }

    public String getDateExpires() {
        return dateExpires;
    }

    public void setDateExpires(String dateExpires) {
        this.dateExpires = dateExpires;
    }

    public String getExpires() {
        return expires;
    }

    public void setExpires(String expires) {
        this.expires = expires;
    }

    public String getMayorVer() {
        return mayorVer;
    }

    public void setMayorVer(String mayorVer) {
        this.mayorVer = mayorVer;
    }

    public String getMinorVer() {
        return minorVer;
    }

    public void setMinorVer(String minorVer) {
        this.minorVer = minorVer;
    }

    public String getApproved() {
        return approved;
    }

    public void setApproved(String approved) {
        this.approved = approved;
    }

    public String getDateApproved() {
        return dateApproved;
    }

    public void setDateApproved(String dateApproved) {
        this.dateApproved = dateApproved;
    }

    public int getNumVer() {
        return numVer;
    }

    public void setNumVer(int numVer) {
        this.numVer = numVer;
    }

    public String getIdCheckOut() {
        return idCheckOut;
    }

    public void setIdCheckOut(String idCheckOut) {
        this.idCheckOut = idCheckOut;
    }

    public String getChangeMinorVersion() {
        return changeMinorVersion;
    }

    public void setChangeMinorVersion(String changeMinorVersion) {
        this.changeMinorVersion = changeMinorVersion;
    }

    public String getStatu() {
        return statu;
    }

    public void setStatu(String statu) {
        this.statu = statu;
    }
    public boolean isDraftVersion() {
        return draftVersion;
    }

    public void setDraftVersion(boolean draftVersion) {
        this.draftVersion = draftVersion;
    }
    public String getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(String dateCreated) {
        this.dateCreated = dateCreated;
    }

    public boolean isChangeMinor() {
        return changeMinor;
    }

    public void setChangeMinor(boolean changeMinor) {
        this.changeMinor = changeMinor;
    }

    public String getIdTypeDoc() {
        return idTypeDoc;
    }

    public void setIdTypeDoc(String idTypeDoc) {
        this.idTypeDoc = idTypeDoc;
    }

    public String getDocRelations() {
        return docRelations;
    }

    public void setDocRelations(String docRelations) {
        this.docRelations = docRelations;
    }

    public byte getTypeWF() {
        return typeWF;
    }

    public void setTypeWF(byte typeWF) {
        this.typeWF = typeWF;
    }

    // TODO: CAMBIO PARA DOBLE VERSION
	public String getNameFileParalelo() {
		return nameFileParalelo;
	}

    // TODO: CAMBIO PARA DOBLE VERSION
	public void setNameFileParalelo(String nameFileParalelo) {
		this.nameFileParalelo = nameFileParalelo;
	}

    // TODO: CAMBIO PARA DOBLE VERSION
	public String getContextTypeParalelo() {
		return contextTypeParalelo;
	}

    // TODO: CAMBIO PARA DOBLE VERSION
	public void setContextTypeParalelo(String contextTypeParalelo) {
		this.contextTypeParalelo = contextTypeParalelo;
	}

    // TODO: CAMBIO PARA DOBLE VERSION
	public String getPath() {
		return path;
	}

    // TODO: CAMBIO PARA DOBLE VERSION
	public void setPath(String path) {
		this.path = path;
	}

    public String getDateExpiresDrafts() {
        return dateExpiresDrafts;
    }

    public void setDateExpiresDrafts(String dateExpiresDrafts) {
        this.dateExpiresDrafts = dateExpiresDrafts;
    }

	public String getOwner() {
		return owner;
	}

	public void setOwner(String owner) {
		this.owner = owner;
	}

    
}
