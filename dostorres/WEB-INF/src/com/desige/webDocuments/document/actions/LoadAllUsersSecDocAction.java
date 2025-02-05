package com.desige.webDocuments.document.actions;

import java.util.Collection;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.desige.webDocuments.persistent.managers.HandlerDBUser;
import com.desige.webDocuments.utils.ApplicationExceptionChecked;
import com.desige.webDocuments.utils.beans.SuperAction;

/**
 * Title: LoadAllUsersSecDocAction.java <br/>
 * Copyright: (c) 2004 Focus Consulting C.A.<br/>
 *
 * @author Ing. Nelson Crespo
 * @version WebDocuments v3.0
 *  <br/>
 *       Changes:<br/>
 *  <ul>
 *       <li> 28/12/2004 (NC) Creation </li>
 *  </ul>
 */
public class LoadAllUsersSecDocAction extends SuperAction {
    public ActionForward execute(ActionMapping actionMapping,
            ActionForm form, HttpServletRequest request,
            HttpServletResponse response) {
        super.init(actionMapping,form,request,response);
        try {
            getUserSession();
            String idStruct = request.getParameter("idNodeSelected");
            String toSearch = request.getParameter("toSearch");
            //System.out.println("idStruct = " + idStruct);
            //System.out.println("toSearch = " + toSearch);
            removeObjectSession("securityUser");
            Collection users = HandlerDBUser.getAllUsersForGrups(toSearch,false);
            putObjectSession("idStruct",idStruct);
            putObjectSession("usuarios",users);
            return goSucces();
        } catch (ApplicationExceptionChecked ae) {
            ae.printStackTrace();
            return goError(ae.getKeyError());
        } catch (Exception e) {
            e.printStackTrace();
            return goError("E0019");
        }
    }
}
