package com.desige.webDocuments.files.actions;

import java.util.Collection;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.desige.webDocuments.persistent.managers.HandlerGrupo;
import com.desige.webDocuments.utils.ApplicationExceptionChecked;
import com.desige.webDocuments.utils.ToolsHTML;
import com.desige.webDocuments.utils.beans.SuperAction;

/**
 * Title: LoadAllGroupsAction.java <br/>
 * Copyright: (c) 2004 Focus Consulting C.A.<br/>
 * @author Ing. Nelson Crespo
 * @version WebDocuments v3.0
 * <br/>
 *      Changes:<br/>
 * <ul>
 *      <li> 21/12/2004 (NC) Creation </li>
 * </ul>
 */
public class LoadAllGroupsFilesAction extends SuperAction {
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
            removeAttribute("groupsWithSec");
            removeAttribute("groups");

            String idStruct = getParameter("idNodeSelected");
            String toSearch = getParameter("toSearch");
            String idDocument = getParameter("idDocument");
            String nodeType = getParameter("nodeType");
            //System.out.println("idStruct = " + idStruct);
            //System.out.println("toSearch = " + toSearch);
            //System.out.println("idDocument = " + idDocument);
            String command = getParameter("command");
            removeObjectSession("securityUser");
            putObjectSession("nodeType",nodeType);
            String toStruct = request.getParameter("toStruct");
            Collection secPreviuos = new Vector();
            secPreviuos=HandlerGrupo.getSecutityForAllGroupsInFiles();
                
            if (!ToolsHTML.isEmptyOrNull(toSearch)) {
                secPreviuos = new Vector();
            }
            Collection groups = HandlerGrupo.getAllGroups(toSearch);
            groups.removeAll(secPreviuos);
            putObjectSession("groupsWithSec",secPreviuos);
            putObjectSession("idStruct",idStruct);
            putObjectSession("groups",groups);
            putObjectSession("idDocument",idDocument);
            return goSucces();
        } catch (ApplicationExceptionChecked ae) {
            return goError(ae.getKeyError());
        } catch (Exception e) {
            e.printStackTrace();
            return goError("E0100");
        }
    }
}
