package com.desige.webDocuments.utils.beans;

import java.io.Serializable;

/**
 * Title: BeanCheckOut.java <br/>
 * Copyright: (c) 2004 Focus Consulting C.A.<br/>
 *
 * @author Ing. Nelson Crespo
 * @version WebDocuments v3.0
 * <br/>
 *       Changes:<br/>
 * <ul>
 *       <li> 06/02/2005 (NC) Creation </li>
 * </ul>
 */
public class BeanCheckOut implements Serializable {
    private boolean checkOut;
    private String doneBy;

    public boolean isCheckOut() {
        return checkOut;
    }

    public void setCheckOut(boolean checkOut) {
        this.checkOut = checkOut;
    }

    public String getDoneBy() {
        return doneBy;
    }

    public void setDoneBy(String doneBy) {
        this.doneBy = doneBy;
    }
}
