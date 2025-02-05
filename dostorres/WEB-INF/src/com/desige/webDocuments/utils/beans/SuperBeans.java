package com.desige.webDocuments.utils.beans;

import java.io.Serializable;

/**
 * Title: SuperBeans.java <br/>
 * Copyright: (c) 2004 Focus Consulting C.A.<br/>
 * @author Ing. Nelson Crespo (NC)
 * @version WebDocuments v3.0
 * <br/>
 * Changes:<br/>
 * <ul>
 *      <li>23/03/2004 (NC) Creation </li>
 </ul>
 */

public class SuperBeans implements Serializable {
    static final long serialVersionUID = -2987253412899494265L;
    private String mensaje;

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

}
