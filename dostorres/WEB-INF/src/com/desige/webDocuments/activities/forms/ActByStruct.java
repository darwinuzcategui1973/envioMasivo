package com.desige.webDocuments.activities.forms;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import com.desige.webDocuments.utils.beans.SuperActionForm;

/**
 * Title: ActByStruct.java <br/>
 * Copyright: (c) 2004 Desige Servicios de Computación<br/>
 * @author Ing. Nelson Crespo
 * @version WebDocuments v1.0
 * <br/>
 *      Changes:<br/>
 * <ul>
 *      <li> 12/12/2005 (NC) Creation </li>
 * </ul>
 */

public class ActByStruct extends SuperActionForm implements Serializable {
    private long idNode;
    private long act_Number;
    private Set users = new HashSet();

    public long getIdNode() {
        return idNode;
    }

    public void setIdNode(long idNode) {
        this.idNode = idNode;
    }

    public long getAct_Number() {
        return act_Number;
    }

    public void setAct_Number(long act_Number) {
        this.act_Number = act_Number;
    }

    public Set getUsers() {
        return users;
    }

    public void setUsers(Set users) {
        this.users = users;
    }
}
