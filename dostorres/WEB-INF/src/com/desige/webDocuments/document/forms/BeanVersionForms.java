package com.desige.webDocuments.document.forms;

import java.io.Serializable;

import com.desige.webDocuments.utils.beans.SuperActionForm;

/**
 * Title: ${name}.java<br>
 * Copyright: (c) 2003 Consis International<br>
 * Company: Consis International (CON)<br>
 * @author Nelson Crespo (NC)
 * @author Simón Rodriguez. (SR)
 * @version Acsel-e v2.2
 * <br>
 * Changes:<br>
 * <ul>
 *      <li> 06/08/2004 (NC) Creation </li>
 *      <li> 17/06/2005 (SR) Creacion parametro dateExpiresDrafts </li>
 * <ul>
 */
public class BeanVersionForms extends SuperActionForm implements Serializable {
    private int numVersion;
    private String numDocument;
    private String mayorVersion;
    private String minorVersion;
    private String owner;
    private String charge;
    private String area;
    private String dateCreated;
    private String dateApproved;
    private String dateExpires;
    private String dateExpiresDrafts;
	private String statu;
	private String nameDocVersion;
    private String ownerVersion;

    private byte toRead;
    private byte toDelete;
    private byte toEdit;
    private byte toMove;
    private byte toAdmon;
    private byte toViewDocs;
    private byte toReview;
    private byte toApproved;
    private byte toMoveDocs;
    private byte toCheckOut;
    private byte toEditRegister;
    private byte toImpresion;
    private byte toCheckTodos;
    private byte toDownload;
    private String statuhist;

     public String getStatuhist() {
        return statuhist;
    }

    public void setStatuhist (String statuhist) {
        this.statuhist = statuhist;
    }
    
     public byte getToCheckTodos() {
         return toCheckTodos;
     }

     public void setToCheckTodos(byte toCheckTodos) {
         this.toCheckTodos = toCheckTodos;
     }


     public byte getToImpresion() {
         return toImpresion;
     }

     public void setToImpresion(byte toImpresion) {
         this.toImpresion = toImpresion;
     }


     public byte getToEditRegister() {
         return toEditRegister;
     }

     public void setToEditRegister(byte toEditRegister) {
         this.toEditRegister = toEditRegister;
     }

     public byte getToCheckOut() {
         return toCheckOut;
     }

     public void setToCheckOut(byte toCheckOut) {
         this.toCheckOut = toCheckOut;
     }


     public byte getToMoveDocs() {
         return toMoveDocs;
     }

     public void setToMoveDocs(byte toMoveDocs) {
         this.toMoveDocs = toMoveDocs;
     }

    public byte getToApproved() {
         return toApproved;
     }

     public void setToApproved(byte toApproved) {
         this.toApproved = toApproved;
     }

    public byte getToReview() {
         return toReview;
     }

     public void setToReview(byte toReview) {
         this.toReview = toReview;
     }

    public byte getToViewDocs() {
         return toViewDocs;
     }

     public void setToViewDocs(byte toViewDocs) {
         this.toViewDocs = toViewDocs;
     }

    public byte getToAdmon() {
         return toAdmon;
     }

     public void setToAdmon(byte toAdmon) {
         this.toAdmon = toAdmon;
     }


    public byte getToMove() {
         return toMove;
     }

     public void setToMove(byte toMove) {
         this.toMove = toMove;
     }

    public byte getToEdit() {
         return toEdit;
     }

     public void setToEdit(byte toEdit) {
         this.toEdit = toEdit;
     }

     public byte getToDelete() {
          return toDelete;
      }

      public void setToDelete(byte toDelete) {
          this.toDelete = toDelete;
      }

    public byte getToRead() {
          return toRead;
      }

      public void setToRead(byte toRead) {
          this.toRead = toRead;
      }


    public String getMayorVersion() {
        return mayorVersion;
    }

    public void setMayorVersion(String mayorVersion) {
        this.mayorVersion = mayorVersion;
    }

    public String getMinorVersion() {
        return minorVersion;
    }

    public void setMinorVersion(String minorVersion) {
        this.minorVersion = minorVersion;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(String dateCreated) {
        this.dateCreated = dateCreated;
    }

    public String getDateApproved() {
        return dateApproved;
    }

    public void setDateApproved(String dateApproved) {
        this.dateApproved = dateApproved;
    }

    public String getDateExpires() {
        return dateExpires;
    }

    public void setDateExpires(String dateExpires) {
        this.dateExpires = dateExpires;
    }
    //SIMON 17 DE MAYO 2005 INICIO
    public String getDateExpiresDrafts() {
            return dateExpiresDrafts;
        }

    public void setDateExpiresDrafts(String dateExpiresDrafts) {
            this.dateExpiresDrafts= dateExpiresDrafts;
        }

    //SIMON 17 DE MAYO 2005 FIN
    public int getNumVersion() {
        return numVersion;
    }

    public void setNumVersion(int numVersion) {
        this.numVersion = numVersion;
    }

    public String getNumDocument() {
        return numDocument;
    }

    public void setNumDocument(String numDocument) {
        this.numDocument = numDocument;
    }

	public String getStatu() {
		return statu;
	}

	public void setStatu(String statu) {
		this.statu = statu;
	}

	public String getNameDocVersion() {
		return nameDocVersion;
	}

	public void setNameDocVersion(String nameDocVersion) {
		this.nameDocVersion = nameDocVersion;
	}

	public String getOwnerVersion() {
		return ownerVersion;
	}

	public void setOwnerVersion(String ownerVersion) {
		this.ownerVersion = ownerVersion;
	}

	public String getCharge() {
		return charge;
	}

	public void setCharge(String charge) {
		this.charge = charge;
	}

	public String getArea() {
		return area;
	}

	public void setArea(String area) {
		this.area = area;
	}

	public byte getToDownload() {
		return toDownload;
	}

	public void setToDownload(byte toDownload) {
		this.toDownload = toDownload;
	}
	
	
	
}
