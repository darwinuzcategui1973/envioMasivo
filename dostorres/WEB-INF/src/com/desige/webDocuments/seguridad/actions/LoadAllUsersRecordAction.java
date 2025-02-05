package com.desige.webDocuments.seguridad.actions;

import java.util.Collection;
import java.util.Hashtable;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.desige.webDocuments.persistent.managers.HandlerDBUser;
import com.desige.webDocuments.utils.ApplicationExceptionChecked;
import com.desige.webDocuments.utils.ToolsHTML;
import com.desige.webDocuments.utils.beans.SuperAction;

/**
 * Title: LoadAllUsersAction.java <br/>
 * Copyright: (c) 2004 Focus Consulting C.A.<br/>
 * @author Ing. Nelson Crespo
 * @version WebDocuments v3.0
 * <br/>
 *      Changes:<br/>
 * <ul>
 *     <li> 14/12/2004 (NC) Creation </li>
 *     <li> 11/08/2005 (SR) HandlerDBUser.getAllUserWithSecInDocs, Se utiliza para validar la seguridad de los documentos</li>
 * </ul>
 */
public class LoadAllUsersRecordAction extends SuperAction {
    public ActionForward execute(ActionMapping actionMapping,
            ActionForm form, HttpServletRequest request,
            HttpServletResponse response) {
        super.init(actionMapping,form,request,response);
        try {
            getUserSession();
            removeAttribute("idStruct");
            removeAttribute("usuarios");
            removeAttribute("idDocument");
            removeAttribute("command");
            removeAttribute("rout");
            removeAttribute("nodeType");
            removeAttribute("usersWithSec");
            String idStruct = request.getParameter("idNodeSelected");
            String toSearch = request.getParameter("toSearch");
            String idDocument = request.getParameter("idDocument");
            String toStruct = request.getParameter("toStruct");
            String command = request.getParameter("command");
            String nodeType = getParameter("nodeType");
            putObjectSession("nodeType",nodeType);
            removeObjectSession("securityUser");
            Hashtable tree = (Hashtable)getSessionObject("tree");
            Collection usersWithSec = new Vector();
            usersWithSec = HandlerDBUser.getAllUserWithSecInRecord();

            if (!ToolsHTML.isEmptyOrNull(toSearch)) {
                usersWithSec = new Vector();
            }
            Collection users = HandlerDBUser.getAllUsersForGrups(toSearch,false);
            users.removeAll(usersWithSec);

            putObjectSession("usersWithSec",usersWithSec);
            putObjectSession("idStruct",idStruct);
            putObjectSession("usuarios",users);
            putObjectSession("idDocument",idDocument);
            return goSucces();
        } catch (ApplicationExceptionChecked ae) {
            return goError(ae.getKeyError());
        } catch (Exception e) {
            e.printStackTrace();
            return goError("E0019");
        }
    }
}
