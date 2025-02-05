package com.desige.webDocuments.structured.forms;

import java.io.File;
import java.io.Serializable;

import com.desige.webDocuments.utils.beans.SuperActionForm;

/**
 * Title: BaseStructForm.java <br/> 
 * Copyright: (c) 2004 Focus Consulting C.A.<br/>
 * @author Ing. Nelson Crespo (NC)
 * @version WebDocuments v3.0
 * <br/>
 * Changes:<br/> 
 * <ul>
 *      <li> 18/04/2004 (NC) Creation </li>
 *      <li> 13/07/2006 (NC) Cambios para heredar o no los Prefijos de las Carpetas </li>
 </ul>
 */
public class BaseStructForm extends SuperActionForm implements Serializable, Cloneable {
    private String idNode;
    private String name;
    private String idNodeParent;
    private String nameIcon;
    private String owner;
    //ydavila Ticket 001-00-003023
    private String respsolcambio;
    private String respsolelimin;
    private String respsolimpres;
    private String ownerResponsible;
    private String dateCreation;
    private String nodeType;
    private String rout;
    private String description;
    private String prefix;
    private int number;
    private byte majorId;
    private String majorKeep;
    private byte minorId;
    private String minorKeep;
    private byte disableUserWF;
    private byte copy;
    private byte sequential;
    private byte conditional;
    private byte notify;
    private byte expireWF;
    private String cantExpDoc;
    private String unitExpDoc;


    private String daysWF;
    private String ownerStruct;
    private boolean isOwnerStruct;
    private String parentPrefix;
    private int showCharge;
    private int timeDocVenc;
    private String txttimeDocVenc;
    private String chargeOwner;
    private byte typePrefix;
    private byte checkvijenToprint;
    private int vijenToprint;
    private int toImpresion;
    private byte checkborradorCorrelativo;
    private byte heredarPrefijo;
    private int toListDist;
    private String toListDistNameUser="";

    public byte getCheckborradorCorrelativo() {
       return checkborradorCorrelativo;
   }
   public void setCheckborradorCorrelativo(byte checkborradorCorrelativo) {
       this.checkborradorCorrelativo = checkborradorCorrelativo;
   }
    public int getToImpresion() {
        return toImpresion;
    }
    public void setToImpresion(int toImpresion) {
        this.toImpresion = toImpresion;
    }
    public int getVijenToprint() {
        return vijenToprint;
    }
    public void setVijenToprint(int vijenToprint) {
        this.vijenToprint = vijenToprint;
    }
    public byte getCheckvijenToprint() {
       return checkvijenToprint;
   }
   public void setCheckvijenToprint(byte checkvijenToprint) {
       this.checkvijenToprint = checkvijenToprint;
   }

    public String getUnitExpDoc() {
       return unitExpDoc;
   }

   public void setUnitExpDoc(String unitExpDoc) {
       this.unitExpDoc = unitExpDoc;
   }

     public String getCantExpDoc() {
        return cantExpDoc;
    }

    public void setCantExpDoc(String cantExpDoc) {
        this.cantExpDoc = cantExpDoc;
    }

    public String getTxttimeDocVenc() {
        return txttimeDocVenc;
    }

    public void setTxttimeDocVenc(String txttimeDocVenc) {
        this.txttimeDocVenc = txttimeDocVenc;
    }

    public int getTimeDocVenc() {
        return timeDocVenc;
    }

    public void setTimeDocVenc(int timeDocVenc) {
        this.timeDocVenc = timeDocVenc;
    }

    public String toString() {
        return idNode + "_" + name;
    }

    public String getIdNode() {
        return idNode;
    }

    public void setIdNode(String idNode) {
        this.idNode = idNode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIdNodeParent() {
        return idNodeParent;
    }

    public void setIdNodeParent(String idNodeParent) {
        this.idNodeParent = idNodeParent;
    }

    public String getNameIcon() {
        return nameIcon;
    }

    public void setNameIcon(String nameIcon) {
        this.nameIcon = nameIcon;
    }

    public BaseStructForm(){
    }

    public BaseStructForm(String idNode,String name,String idNodeParent,String nameIcon,String respsolcambio,String respsolelimin,String respsolimpres){
        setIdNode(idNode);
        setName(name);
        setIdNodeParent(idNodeParent);
        setNameIcon(nameIcon);
        //ydavila Ticket 001-00-003023
        setrespsolcambio(respsolcambio);
        setrespsolelimin(respsolelimin);
        setrespsolimpres(respsolimpres);
    }
//    public void cleanForm(){
//        setIdNode("");
//        setName("");
//        setIdNodeParent("");
//        setNameIcon("");
//    }


	public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    //ydavila Ticket 001-00-003023
    public String getrespsolcambio() {
        return respsolcambio;
    }
    public void setrespsolcambio(String respsolcambio) {
        this.respsolcambio = respsolcambio;
    } 
    public String getrespsolelimin() {
        return respsolelimin;
    }
	public void setrespsolelimin(String respsolelimin) {
        this.respsolelimin = respsolelimin;
    }  
    public String getrespsolimpres() {
        return respsolimpres;
    }
    public void setrespsolimpres(String respsolimpres) {
        this.respsolimpres = respsolimpres;
    }
  //ydavila Ticket 001-00-003023
    
    public String getDateCreation() {
        return dateCreation;
    }

    public void setDateCreation(String dateCreation) {
        this.dateCreation = dateCreation;
    }

    public String getNodeType() {
        return nodeType;
    }

    public void setNodeType(String nodeType) {
        this.nodeType = nodeType;
    }

    public String getRout() {
        return rout;
    }
    
	public String getRoutWithoutLinks() {
		String ruta = getRout();
		String[] tokenArray = ruta.split("\">");
		StringBuilder sb = new StringBuilder(1024);
		for (int i = 1, len = tokenArray.length, ultimo = len-1; i < tokenArray.length; i++) {
			String str = tokenArray[i];
			int pos = str.indexOf("</a>");
			if (pos != -1) {
				sb.append(str.substring(0, pos));
				if ( i != ultimo) {
					sb.append("&nbsp;").append(File.separator).append("&nbsp;");
				}
			}
		}
		
		return sb.toString();
	}

	public String getRoutWithoutLinksNoHTML() {
		String ruta = getRout();
		String[] tokenArray = ruta.split("\">");
		StringBuilder sb = new StringBuilder(1024);
		for (int i = 1, len = tokenArray.length, ultimo = len-1; i < tokenArray.length; i++) {
			String str = tokenArray[i];
			int pos = str.indexOf("</a>");
			if (pos != -1) {
				sb.append(str.substring(0, pos));
				if ( i != ultimo) {
					sb.append(File.separator);
				}
			}
		}
		
		return sb.toString();
	}
    

    public void setRout(String rout) {
        this.rout = rout;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public byte getMajorId() {
        return majorId;
    }

    public void setMajorId(byte majorId) {
        this.majorId = majorId;
    }

    public String getMajorKeep() {
        return majorKeep;
    }

    public void setMajorKeep(String majorKeep) {
        this.majorKeep = majorKeep;
    }

    public byte getMinorId() {
        return minorId;
    }

    public void setMinorId(byte minorId) {
        this.minorId = minorId;
    }

    public String getMinorKeep() {
        return minorKeep;
    }

    public void setMinorKeep(String minorKeep) {
        this.minorKeep = minorKeep;
    }

    public byte getDisableUserWF() {
        return disableUserWF;
    }

    public void setDisableUserWF(byte disableUserWF) {
        this.disableUserWF = disableUserWF;
    }

    public byte getSequential() {
        return sequential;
    }

    public void setSequential(byte sequential) {
        this.sequential = sequential;
    }

    public byte getConditional() {
        return conditional;
    }

    public void setConditional(byte conditional) {
        this.conditional = conditional;
    }

    public byte getNotify() {
        return notify;
    }

    public void setNotify(byte notify) {
        this.notify = notify;
    }

    public byte getExpireWF() {
        return expireWF;
    }

    public void setExpireWF(byte expireWF) {
        this.expireWF = expireWF;
    }

    public String getDaysWF() {
        return daysWF;
    }

    public void setDaysWF(String daysWF) {
        this.daysWF = daysWF;
    }

    public void clearForm(){
//        setIdNode("");
        setName("");
        setIdNodeParent("");
        setNameIcon("");
        setOwner("");
        //ydavila Ticket 001-00-003023
        setrespsolcambio("");
        setrespsolelimin("");
        setrespsolimpres("");
        setDateCreation("");
        setNodeType("");
        setRout("");
        setDescription("");
        setPrefix("");
        setNumber(0);
        setMajorId((byte)1);
        setMajorKeep("");
        setMinorId((byte)1);
        setMinorKeep("");
        setDisableUserWF((byte)1);
        setSequential((byte)1);
        setConditional((byte)1);
        setNotify((byte)1);
        setExpireWF((byte)1);
        setDaysWF("");
        setToListDist(0);
        setToListDistNameUser("");
    }

    public String getOwnerStruct() {
        return ownerStruct;
    }

    public void setOwnerStruct(String ownerStruct) {
        this.ownerStruct = ownerStruct;
    }

    public boolean isOwnerStruct() {
        return isOwnerStruct;
    }

    public void setOwnerStruct(boolean ownerStruct) {
        isOwnerStruct = ownerStruct;
    }

    public String getParentPrefix() {
        return parentPrefix;
    }

    public void setParentPrefix(String parentPrefix) {
        this.parentPrefix = parentPrefix;
    }

    public int getShowCharge() {
        return showCharge;
    }

    public void setShowCharge(int showCharge) {
        this.showCharge = showCharge;
    }

    public String getChargeOwner() {
        return chargeOwner;
    }

    public void setChargeOwner(String chargeOwner) {
        this.chargeOwner = chargeOwner;
    }

    public byte getTypePrefix() {
        return typePrefix;
    }

    public void setTypePrefix(byte typePrefix) {
        this.typePrefix = typePrefix;
    }
//    public String getUnitExpDoc() {
//        return unitExpDoc;
//    }

//    public void setUnitExpDoc(String unitExpDoc) {
//        this.unitExpDoc = unitExpDoc;
//    }

//    public String getCantExpDoc() {
//        return cantExpDoc;
//    }

//    public void setCantExpDoc(String cantExpDoc) {
//        this.cantExpDoc = cantExpDoc;
//    }

    public byte getHeredarPrefijo() {
        return heredarPrefijo;
    }

    public void setHeredarPrefijo(byte heredarPrefijo) {
        this.heredarPrefijo = heredarPrefijo;
    }
    
    public byte getCopy() {
		return copy;
	}
	public void setCopy(byte copy) {
		this.copy = copy;
	}

	public String getToListDistNameUser() {
		return toListDistNameUser;
	}
	public void setToListDistNameUser(String toListDistNameUser) {
		this.toListDistNameUser = toListDistNameUser;
	}
	public int getToListDist() {
		return toListDist;
	}
	public void setToListDist(int toListDist) {
		this.toListDist = toListDist;
	}
	
	
	public String getOwnerResponsible() {
		return ownerResponsible;
	}
	public void setOwnerResponsible(String ownerResponsible) {
		this.ownerResponsible = ownerResponsible;
	}
	public Object clone(){
        Object obj=null;
        try{
            obj=super.clone();
        }catch(CloneNotSupportedException ex){
            //System.out.println(" no se puede duplicar");
        }
        return obj;
    }    


}
