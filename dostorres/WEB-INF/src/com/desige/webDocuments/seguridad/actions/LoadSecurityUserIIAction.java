package com.desige.webDocuments.seguridad.actions;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.desige.webDocuments.persistent.managers.HandlerDBUser;
import com.desige.webDocuments.persistent.managers.HandlerDocuments;
import com.desige.webDocuments.seguridad.forms.SecurityFolderForm;
import com.desige.webDocuments.utils.ApplicationExceptionChecked;
import com.desige.webDocuments.utils.Constants;
import com.desige.webDocuments.utils.DesigeConf;
import com.desige.webDocuments.utils.ToolsHTML;
import com.desige.webDocuments.utils.beans.SuperAction;

/**
 * Title: LoadSecurityUserIIAction.java <br/>
 * Copyright: (c) 2006 Focus Consulting C.A.<br/>
 * @author Ing. Nelson Crespo
 * @version WebDocuments v3.0
 * <br/>
 *      Changes:<br/>
 * <ul>
 *      <li> 15/04/2006 (NC) Creation </li>
 * </ul>
 */

public class LoadSecurityUserIIAction extends SuperAction {
    public ActionForward execute(ActionMapping actionMapping,
                                 ActionForm form, HttpServletRequest request,
                                 HttpServletResponse response) {
        super.init(actionMapping,form,request,response);
        try {
            getUserSession();
            String rout = getParameter("rout");
            removeAttribute("toGroup");
//            PermissionUserForm forma = (PermissionUserForm)form;
            SecurityFolderForm forma = (SecurityFolderForm)form;

            removeObjectSession("CreaLocidad");
             if (DesigeConf.getProperty("application.admon").equalsIgnoreCase(String.valueOf(forma.getIdGroup()))) {
                putObjectSession("CreaLocidad","CreaLocidad");
             }
            String securityPorStructura  = getParameter("securityPorStructura");
            putObjectSession("securityPorStructura",securityPorStructura);
            //System.out.println("forma.getCommand() = " + forma.getCommand());
            if (Constants.cmdToStruct.equalsIgnoreCase(forma.getCommand())) {
                HandlerDBUser.loadSecurityStructUser(forma);
            } else {
                HandlerDocuments.loadSecurityDocumentAndId(true,forma);
                String nameDocument = getParameter("nameDocument");
                if (!ToolsHTML.isEmptyOrNull(nameDocument)) {
                    putAtributte("nameDocument",nameDocument);
                }
            }
            //System.out.println("[LoadSecurityUserAction] rout = " + rout);
            if (!ToolsHTML.isEmptyOrNull(rout)) {
                putObjectSession("rout",rout);
            }
            //Verificamos el tipo del Nodo Para redirigir :D
            if (DesigeConf.getProperty("siteType").equalsIgnoreCase(forma.getNodeType())) {
                return goTo("secLocation");
            }
            return goSucces();
        } catch (ApplicationExceptionChecked ae) {
            return goError(ae.getKeyError());
        } catch (Exception e) {
            e.printStackTrace();
            return goError("E0019");
        }
    }
}
