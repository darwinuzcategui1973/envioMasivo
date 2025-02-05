package com.desige.webDocuments.mail.actions;

import java.util.Collection;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.desige.webDocuments.activities.actions.AssociateUsersAction;
import com.desige.webDocuments.persistent.managers.HandlerMessages;
import com.desige.webDocuments.seguridad.forms.SeguridadUserForm;
import com.desige.webDocuments.utils.ApplicationExceptionChecked;
import com.desige.webDocuments.utils.Constants;
import com.desige.webDocuments.utils.ToolsHTML;
import com.desige.webDocuments.utils.beans.SuperAction;
import com.desige.webDocuments.utils.beans.Users;

/**
 * Title: LoadMailsInbox.java<br>
 * Copyright: (c) 2004 Desige Servicios de Computación<br/>
 *
 * @author Ing. Nelson Crespo (NC)
 * @version WebDocuments v1.0
 * <br>
 *     Changes:<br>
 * <ul>
 *     <li> 16/07/2004 (NC) Creation </li>
 *     <li> 11/04/2006 (SR) modifico el metodo getAllInBoxMessagesForUser </li>
 *
 * <ul>
 */
public class LoadMailsInbox extends SuperAction {
	private static Logger log = LoggerFactory.getLogger(AssociateUsersAction.class.getName());

	public ActionForward execute(ActionMapping mapping,
                                 ActionForm form, HttpServletRequest request,
                                 HttpServletResponse response) {
        super.init(mapping,form,request,response);

        // seteamos el modulo activo
        request.getSession().setAttribute(Constants.MODULO_ACTIVO, Constants.MODULO_MENSAJES);
		try{
            removeAttribute("inbox",request);
            Users usuario = getUserSession();
            
			SeguridadUserForm securityForUser = ToolsHTML.getSeguridadModuloUser(usuario);
			SeguridadUserForm securityForGroup = ToolsHTML.getSeguridadModuloGroup(usuario);

			if (securityForUser.getMensajes() == 0) {
				putAtributte("visible", "true");
			} else if (securityForUser.getMensajes() == 2) {
				if (securityForGroup.getMensajes() == 0) {
					putAtributte("visible", "true");
				} else {
					log.error("ACCESO ILEGAL:loadInbox.do user:"+usuario.getIdPerson()+" / nombre:"+usuario.getNamePerson());
					return mapping.findForward("errorNotAuthorized");
				}
			} else {
				log.error("ACCESO ILEGAL:loadInbox.do user:"+usuario.getIdPerson()+" / nombre:"+usuario.getNamePerson());
				return mapping.findForward("errorNotAuthorized");
			}
            
            //Collection inbox = HandlerMessages.getAllInBoxMessagesForUser(usuario.getEmail());
            Collection inbox = HandlerMessages.getAllInBoxMessagesForUser(usuario.getUser());
            if (inbox!=null&&inbox.size()>0) {
                putObjectSession("size",String.valueOf(inbox.size()));
            }
            putObjectSession("inbox",inbox);
            return goSucces();
        } catch (ApplicationExceptionChecked ae) {
            ae.printStackTrace();
            return goError(ae.getKeyError());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return goError();
    }
}
