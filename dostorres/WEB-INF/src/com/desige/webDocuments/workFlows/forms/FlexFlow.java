package com.desige.webDocuments.workFlows.forms;

import java.io.Serializable;

/**
 * Title: FlexFlow <br/>
 * Copyright: (c) 2006 Focus Consulting C.A.<br/>
 * @author Ing. Nelson Crespo
 * @version WebDocuments v3.0
 * <br/>
 * Changes:<br/>
 * <ul>
 * <li> 04/06/2006 (NC) Creation </li>
 * </ul>
 */
public class FlexFlow implements Serializable {
    private long idFlexWF;
    private int orden;
    private String idUser;
    private long idWorkFlow;
    private byte type;
    private String result;
    private String statu;
    private long idFather;
    private byte secuential;

    public long getIdFlexWF() {
        return idFlexWF;
    }

    public void setIdFlexWF(long idFlexWF) {
        this.idFlexWF = idFlexWF;
    }

    public int getOrden() {
        return orden;
    }

    public void setOrden(int orden) {
        this.orden = orden;
    }

    public String getIdUser() {
        return idUser;
    }

    public void setIdUser(String idUser) {
        this.idUser = idUser;
    }

    public long getIdWorkFlow() {
        return idWorkFlow;
    }

    public void setIdWorkFlow(long idWorkFlow) {
        this.idWorkFlow = idWorkFlow;
    }

    public byte getType() {
        return type;
    }

    public void setType(byte type) {
        this.type = type;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getStatu() {
        return statu;
    }

    public void setStatu(String statu) {
        this.statu = statu;
    }

    public long getIdFather() {
        return idFather;
    }

    public void setIdFather(long idFather) {
        this.idFather = idFather;
    }

    public byte getSecuential() {
        return secuential;
    }

    public void setSecuential(byte secuential) {
        this.secuential = secuential;
    }


    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("idWorkFlow: ");
        sb.append(idWorkFlow);
        sb.append("- idUser: ");
        sb.append(this.idUser);

        sb.append("- idFlexWF: ");
        sb.append(this.idFlexWF);

        return sb.toString();
    }
}
