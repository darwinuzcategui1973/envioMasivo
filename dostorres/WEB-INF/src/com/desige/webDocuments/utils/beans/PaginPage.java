package com.desige.webDocuments.utils.beans;

import java.io.Serializable;

/**
 * Title: PaginPage.java <br/> 
 * Copyright: (c) 2004 Focus Consulting C.A.<br/>
 * @author Ing. Nelson Crespo (NC)
 * @version WebDocuments v3.0
 * <br/>
 * Changes:<br/> 
 * <ul>
 *      <li>19/04/2004 (NC) Creation </li>
 </ul>
 */
public class PaginPage implements Serializable {
    private String desde;
    private String cuantos;
    private String pages;
    private String from;
    private int numPages;

    public String getDesde() {
        return desde;
    }

    public void setDesde(String desde) {
        this.desde = desde;
    }

    public String getCuantos() {
        return cuantos;
    }

    public void setCuantos(String cuantos) {
        this.cuantos = cuantos;
    }

    public String getPages() {
        return pages;
    }

    public void setPages(String pages) {
        this.pages = pages;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public int getNumPages() {
        return numPages;
    }

    public void setNumPages(int numPages) {
        this.numPages = numPages;
    }
}
