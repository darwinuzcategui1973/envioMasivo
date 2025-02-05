package com.desige.webDocuments.utils.beans;

import java.io.Serializable;

/**
 * Title: BeanMenu.java <br/>
 * Copyright: (c) 2004 Focus Consulting C.A.<br/>
 *
 * @author Ing. Nelson Crespo
 * @version WebDocuments v3.0
 * <br/>
 *      Changes:<br/>
 * <ul>
 *      <li> 2005-01-30 (NC) Creation </li>
 * </ul>
 */
public class BeanMenu implements Serializable {
    private String txtLink;
    private String target;
    private String link;
    private String icono;

    public BeanMenu() {

    }

    public BeanMenu(String txtLink,String link,String target) {
        setTxtLink(txtLink);
        setTarget(target);
        setLink(link);
    }
    
    public BeanMenu(String txtLink,String link,String target,String icono) {
        setTxtLink(txtLink);
        setTarget(target);
        setLink(link);
        setIcono(icono);
    }
    

    public String getTxtLink() {
        return txtLink;
    }

    public void setTxtLink(String txtLink) {
        this.txtLink = txtLink;
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

	public String getIcono() {
		return icono;
	}

	public void setIcono(String icono) {
		this.icono = icono;
	}
    
    
}
