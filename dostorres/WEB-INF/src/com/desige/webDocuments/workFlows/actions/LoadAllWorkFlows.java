package com.desige.webDocuments.workFlows.actions;

import java.util.Collection;
import java.util.ResourceBundle;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.desige.webDocuments.persistent.managers.HandlerWorkFlows;
import com.desige.webDocuments.seguridad.forms.SeguridadUserForm;
import com.desige.webDocuments.utils.ApplicationExceptionChecked;
import com.desige.webDocuments.utils.Constants;
import com.desige.webDocuments.utils.ToolsHTML;
import com.desige.webDocuments.utils.beans.SuperAction;
import com.desige.webDocuments.utils.beans.Users;

/**
 * Title: LoadAllWorkFlows.java <br/>
 * Copyright: (c) 2004 Focus Consulting C.A.<br/>
 *
 * @author Ing. Nelson Crespo
 * @version WebDocuments v3.0
 * <br/>
 *      Changes:<br/>
 * <ul>
 *      <li> 2005-01-29 (NC) Creation </li>
 * </ul>
 */
public class LoadAllWorkFlows extends SuperAction {
	
	private static Logger log = LoggerFactory.getLogger(LoadAllWorkFlows.class.getName());
	
    public ActionForward execute(ActionMapping mapping,
                                 ActionForm form, HttpServletRequest request,
                                 HttpServletResponse response) {
        super.init(mapping,form,request,response);

        // seteamos el modulo activo
      //  request.getSession().setAttribute(Constants.MODULO_ACTIVO, Constants.MODULO_FLUJO);
        
        ResourceBundle rb = ToolsHTML.getBundle(request);
        try {
            Users user = getUserSession();
            
			Users usuario = user;
			SeguridadUserForm securityForUser = ToolsHTML.getSeguridadModuloUser(usuario);
			SeguridadUserForm securityForGroup = ToolsHTML.getSeguridadModuloGroup(usuario);

			if (securityForUser.getFlujos() == 0) {
				putAtributte("visible", "true");
			} else if (securityForUser.getFlujos() == 2) {
				if (securityForGroup.getFlujos() == 0) {
					putAtributte("visible", "true");
				} else {
					log.error("ACCESO ILEGAL:loadInboxWF.do user:"+usuario.getIdPerson()+" / nombre:"+usuario.getNamePerson());
					return mapping.findForward("errorNotAuthorized");
				}
			} else {
				log.error("ACCESO ILEGAL:loadInboxWF.do user:"+usuario.getIdPerson()+" / nombre:"+usuario.getNamePerson());
				return mapping.findForward("errorNotAuthorized");
			}
            
            Collection myTask = HandlerWorkFlows.getAllWorkFlows(user.getUser(),false,rb);
            //Agregando los Flujos Param�tricos
            myTask.addAll(HandlerWorkFlows.getAllFlexWorkFlows(user.getUser(),false,rb));
            Collection taskRequest = HandlerWorkFlows.getAllWorkFlows(user.getUser(),true,rb);
            taskRequest.addAll(HandlerWorkFlows.getAllFlexWorkFlows(user.getUser(),true,rb));
            putObjectSession("myTask",myTask);
            putObjectSession("taskRequest",taskRequest);
            request.setAttribute("myTaskSize",myTask.size());
            request.setAttribute("taskRequestSize",taskRequest.size());
            return goSucces();
        } catch (ApplicationExceptionChecked ap) {
            return goError(ap.getKeyError());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return goError();
    }
}
