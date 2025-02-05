package com.desige.webDocuments.users.actions;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.desige.webDocuments.utils.beans.SuperAction;
import com.desige.webDocuments.utils.beans.Users;

/**
 * Title: RedirectAction <br/>
 * Copyright: (c) 2006 Focus Consulting C.A.<br/>
 *
 * @author Ing. Nelson Crespo
 * @version WebDocuments v3.0
 * <br/>
 * Changes:<br/>
 * <ul>
 * <li> 17/07/2006 (NC) Creation </li>
 * </ul>
 */
public class RedirectAction extends SuperAction {
    public ActionForward execute(ActionMapping mapping,
                                 ActionForm form, HttpServletRequest request,
                                 HttpServletResponse response) {
        super.init(mapping,form,request,response);
        Users usuario = (Users)getSessionObject("user");
        if (usuario!=null) {
            //System.out.println("usuario = " + usuario);
        } else {
            //System.out.println("IS Null the User in Session...");
        }
        return goSucces();
    }
}
