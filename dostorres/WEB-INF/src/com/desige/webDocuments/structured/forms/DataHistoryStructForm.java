package com.desige.webDocuments.structured.forms;

import java.io.Serializable;

import com.desige.webDocuments.utils.beans.SuperActionForm;

/**
 * Title: DataHistoryStructForm.java <br/>
 * Copyright: (c) 2004 Focus Consulting C.A.<br/>
 * @author Ing. Nelson Crespo
 * @version WebDocuments v3.0
 * <br/>
 * Changes:<br/> 
 * <ul>
 *      <li> 31/08/2004 (NC) Creation </li>
 </ul>
 */
public class DataHistoryStructForm extends SuperActionForm implements Serializable {
    private int idHistory;
    private int idNode;
    private int idNodeFatherAnt;
    private String nameNodeFatherAnt;
    private int idNodeFatherNew;
    private String nameNodeFatherNew;
    private String user;
    private String nameUser;
    private String dateChange;
    private String owner;
    private String nameOwner;
    private String dateCreation;
    private String typeMovement;
    private String nodeType;
    private String nameIcon;
    private String name;
    private String comments;
    private String mayorVer;
    private String minorVer;

    public static final String creation = "1";
    public static final String move = "2";
    public static final String delete = "3";
    public static final String approvedPublic = "4";
    

    public int getIdHistory() {
        return idHistory;
    }

    public void setIdHistory(int idHistory) {
        this.idHistory = idHistory;
    }

    public int getIdNode() {
        return idNode;
    }

    public void setIdNode(int idNode) {
        this.idNode = idNode;
    }

    public int getIdNodeFatherAnt() {
        return idNodeFatherAnt;
    }

    public void setIdNodeFatherAnt(int idNodeFatherAnt) {
        this.idNodeFatherAnt = idNodeFatherAnt;
    }

    public int getIdNodeFatherNew() {
        return idNodeFatherNew;
    }

    public void setIdNodeFatherNew(int idNodeFatherNew) {
        this.idNodeFatherNew = idNodeFatherNew;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getNameUser() {
        return nameUser;
    }

    public void setNameUser(String nameUser) {
        this.nameUser = nameUser;
    }

    public String getDateChange() {
        return dateChange;
    }

    public void setDateChange(String dateChange) {
        this.dateChange = dateChange;
    }

    public String getNameNodeFatherAnt() {
        return nameNodeFatherAnt;
    }

    public void setNameNodeFatherAnt(String nameNodeFatherAnt) {
        this.nameNodeFatherAnt = nameNodeFatherAnt;
    }

    public String getNameNodeFatherNew() {
        return nameNodeFatherNew;
    }

    public void setNameNodeFatherNew(String nameNodeFatherNew) {
        this.nameNodeFatherNew = nameNodeFatherNew;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getNameOwner() {
        return nameOwner;
    }

    public void setNameOwner(String nameOwner) {
        this.nameOwner = nameOwner;
    }

    public String getDateCreation() {
        return dateCreation;
    }

    public void setDateCreation(String dateCreation) {
        this.dateCreation = dateCreation;
    }

    public String getTypeMovement() {
        return typeMovement;
    }

    public void setTypeMovement(String typeMovement) {
        this.typeMovement = typeMovement;
    }

    public String getNodeType() {
        return nodeType;
    }

    public void setNodeType(String nodeType) {
        this.nodeType = nodeType;
    }

    public String getNameIcon() {
        return nameIcon;
    }

    public void setNameIcon(String nameIcon) {
        this.nameIcon = nameIcon;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
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
}
