package com.desige.webDocuments.seguridad.actions;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.desige.webDocuments.persistent.managers.HandlerDocuments;
import com.desige.webDocuments.persistent.managers.HandlerGrupo;
import com.desige.webDocuments.persistent.managers.HandlerParameters;
import com.desige.webDocuments.seguridad.forms.PermissionUserForm;
import com.desige.webDocuments.utils.ApplicationExceptionChecked;
import com.desige.webDocuments.utils.Constants;
import com.desige.webDocuments.utils.DesigeConf;
import com.desige.webDocuments.utils.ToolsHTML;
import com.desige.webDocuments.utils.beans.SuperAction;

/**
 * Title: LoadSecurityGroupsAction.java <br/>
 * Copyright: (c) 2004 Focus Consulting C.A.<br/>
 *
 * @author Ing. Nelson Crespo
 * @version WebDocuments v3.0
 * <br/>
 *      Changes:<br/>
 * <ul>
 *      <li> 21/12/2004 (NC) Creation </li>
 * </ul>
 */
public class LoadSecurityGroupsAction extends SuperAction {
    public ActionForward execute(ActionMapping actionMapping,
            ActionForm form, HttpServletRequest request,
            HttpServletResponse response) {
        super.init(actionMapping,form,request,response);
        try {
            getUserSession();
            PermissionUserForm forma = (PermissionUserForm)form;
            if (forma.getCommand().equalsIgnoreCase(Constants.cmdToStruct)) {
                HandlerGrupo.loadSecurityStructGroup(forma);
            } else {
                HandlerDocuments.loadSecurityDocumentAndId(false,forma);
                String nameDocument = getParameter("nameDocument");
                if (!ToolsHTML.isEmptyOrNull(nameDocument)) {
                    putAtributte("nameDocument",nameDocument);
                    putAtributte("nameDocument",nameDocument);
                }
            }
            //Verificamos el tipo del Nodo Para redirigir :D
            removeAttribute("toGroup");
            if (DesigeConf.getProperty("siteType").equalsIgnoreCase(forma.getNodeType())) {
                putObjectSession("toGroup","yes");
                return goTo("secLocation");
            }

            // Colocaremos una variable que nos indique si tiene activo el proceso de completarFlujo para este sistema
            boolean completeFTP = String.valueOf(HandlerParameters.PARAMETROS.getCompleteFTP()).equals("1");
           	putObjectSession("completeFTP", (completeFTP?"true":"false") );
            return goSucces();
        } catch (ApplicationExceptionChecked ae) {
            return goError(ae.getKeyError());
        } catch (Exception e) {
            e.printStackTrace();
            return goError("E0019");
        }
    }
}
