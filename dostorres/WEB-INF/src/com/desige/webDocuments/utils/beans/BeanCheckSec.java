package com.desige.webDocuments.utils.beans;

import java.io.Serializable;

/**
 * Title: BeanCheckSec.java <br/>
 * Copyright: (c) 2004 Focus Consulting C.A.<br/>
 *
 * @author Ing. Nelson Crespo
 * @version WebDocuments v3.0
 * <br/>
 *      Changes:<br/>
 * <ul>
 *      <li> 29/05/2005 (NC) Creation </li>
 * </ul>
 */
public class BeanCheckSec implements Serializable {
    private String idNode;
    private boolean exits;
    private int permisoModificado;

    public BeanCheckSec() {

    }

    public BeanCheckSec(String idNode,boolean exits,int permisoModificado) {
        setIdNode(idNode);
        setExits(exits);
        setPermisoModificado(permisoModificado);
    }

    public String getIdNode() {
        return idNode;
    }

    public void setIdNode(String idNode) {
        this.idNode = idNode;
    }

    public boolean isExits() {
        return exits;
    }

    public void setExits(boolean exits) {
        this.exits = exits;
    }

    public String toString() {
        return "[id] " + getIdNode() + " [exist] " + isExits();
    }

	public int getPermisoModificado() {
		return permisoModificado;
	}

	public void setPermisoModificado(int permisoModificado) {
		this.permisoModificado = permisoModificado;
	}


}
