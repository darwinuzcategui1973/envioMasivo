package com.desige.webDocuments.utils.beans;

import java.io.Serializable;

/**
 * Title: BeanWFRejectes.java <br/>
 * Copyright: (c) 2005 Focus Consulting C.A.<br/>
 * @author Ing. Nelson Crespo
 * @version WebDocuments v3.0
 * <br/>
 *      Changes:<br/>
 * <ul>
 *      <li> 25/10/2005 (NC) Creation </li>
 * </ul>
 */
public class BeanWFRejectes implements Serializable {
    private byte statu;
    private long row;
    private String idUser;
    private long idPending;

    public BeanWFRejectes(byte statu,long row,String idUser,
                          long idPending) {
        setStatu(statu);
        setRow(row);
        setIdUser(idUser);
        setIdPending(idPending);
    }

    public BeanWFRejectes() {

    }

    public byte getStatu() {
        return statu;
    }

    public void setStatu(byte statu) {
        this.statu = statu;
    }

    public long getRow() {
        return row;
    }

    public void setRow(long row) {
        this.row = row;
    }

    public String getIdUser() {
        return idUser;
    }

    public void setIdUser(String idUser) {
        this.idUser = idUser;
    }

    public long getIdPending() {
        return idPending;
    }

    public void setIdPending(long idPending) {
        this.idPending = idPending;
    }
}
