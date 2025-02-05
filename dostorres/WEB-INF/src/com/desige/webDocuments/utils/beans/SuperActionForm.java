package com.desige.webDocuments.utils.beans;

import java.io.Serializable;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

/**
 * Title: SuperActionForm.java <br/> 
 * Copyright: (c) 2004 Focus Consulting C.A.<br/>
 * @author Ing. Nelson Crespo (NC)
 * @version WebDocuments v3.0
 * <br/>
 * Changes:<br/> 
 * <ul>
 *      <li>28/03/2004 (NC) Creation </li>
 </ul>
 */
public class SuperActionForm extends ActionForm implements Serializable {
    public static final String cmdReLoad = "reload";
    public static final String cmdPublic = "public";
    public static final String cmdLoad = "load";
    public static final String cmdEdit = "edit";
    public static final String cmdDelete = "delete";
    public static final String cmdNew = "new";
    public static final String cmdInsert = "insert";
    public static final String cmdClosed = "cerrar";
    public static final String cmdPaste = "paste";
    public static final String cmdUpdatePrint = "actualizarImprimir";
    static final long serialVersionUID = 4018646087924705138L;

    private String cmd;
    public ActionErrors validate(ActionMapping actionMapping, HttpServletRequest httpServletRequest) {
        return null;
    }
    public void reset(ActionMapping actionMapping, HttpServletRequest httpServletRequest) {
    }
    public String getCmd() {
        return cmd;
    }

    public void setCmd(String cmd) {
        this.cmd = cmd;
    }
}
