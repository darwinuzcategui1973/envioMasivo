package com.desige.webDocuments.utils.beans;

import java.io.Serializable;

/**
 * Title: LoginUserDoc.java <br/> 
 * Copyright: (c) 2004 Focus Consulting C.A.<br/>
 * @author Ing. Nelson Crespo (NC)
 * @version WebDocuments v3.0
 * <br/>
 * Changes:<br/> 
 * <ul>
 *      <li>24/06/2004 (NC) Creation </li>
 </ul>
 */
public class LoginUserDoc implements Serializable {
    static final long serialVersionUID = 2688638536723501931L;

    private String clave;
    private String nameUser;

    public LoginUserDoc(){

    }

    public LoginUserDoc(String nameUser,String clave){
        setClave(clave);
        setNameUser(nameUser);
    }

    public String getClave() {
        return clave;
    }

    public void setClave(String clave) {
        this.clave = clave;
    }

    public String getNameUser() {
        return nameUser;
    }

    public void setNameUser(String nameUser) {
        this.nameUser = nameUser;
    }

}
