 package com.desige.webDocuments.users.actions;

import java.util.ResourceBundle;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.desige.webDocuments.perfil.forms.PerfilActionForm;
import com.desige.webDocuments.persistent.managers.HandlerPerfil;
import com.desige.webDocuments.utils.ApplicationExceptionChecked;
import com.desige.webDocuments.utils.ToolsHTML;
import com.desige.webDocuments.utils.beans.SuperAction;
import com.desige.webDocuments.utils.beans.Users;

/**
 * Title: EditClaveAction.java <br/>
 * Copyright: (c) 2004 Focus Consulting C.A.<br/>
 * @author Ing. Roger Rodríguez
 * @version WebDocuments v3.0
 * <br/>
 * Changes:<br/> 
 * <ul>
 *      <li>12/07/2004 (RR) Creation </li>
 </ul>
 */

public class EditClaveAction extends SuperAction {
	private static Logger log = LoggerFactory.getLogger(EditClaveAction.class);
	
	public ActionForward execute(ActionMapping actionMapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) {
       
		log.info("Iniciando cambio de clave");
		try {
			super.init(actionMapping,form,request,response);
			
			Users user = getUserSession();
			
			if (ToolsHTML.checkValue(user.getUser())) {
				((PerfilActionForm)form).setUser(user.getUser());
				
				ResourceBundle rb = ToolsHTML.getBundle(request);
                	if (HandlerPerfil.updatePassWord((PerfilActionForm)form)) {
                		request.setAttribute("info",rb.getString("app.editOk"));
                	} else {
                		StringBuffer mensaje = new StringBuffer(rb.getString("app.notEdit"));
                		mensaje.append("<br/>").append(HandlerPerfil.getMensaje());
                		request.setAttribute("info",mensaje.toString());
                	}
                	
                	return goSucces();
			}
		} catch (ApplicationExceptionChecked ae) {
			ae.printStackTrace();
			return goError(ae.getKeyError());
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			log.info("Finalizado cambio de clave");
		}
		
		return goError();

//      Users user = (Users)httpServletRequest.getSession().getAttribute("user");
//        if (user!=null){
//            if (ToolsHTML.checkValue(user.getUser())){
//                //System.out.println("Chequeado el user");
//                ((PerfilActionForm)form).setUser(user.getUser());
//                try {
//                    ResourceBundle rb = ToolsHTML.getBundle(httpServletRequest);
//                    if (HandlerPerfil.updatePassWord((PerfilActionForm)form)){
//                        httpServletRequest.setAttribute("info",rb.getString("app.editOk"));
//                    } else {
//                        StringBuffer mensaje = new StringBuffer(rb.getString("app.notEdit"));
//                        mensaje.append("<br/>").append(HandlerPerfil.getMensaje());
//                        httpServletRequest.setAttribute("info",mensaje.toString());
//                    }
//                    return actionMapping.findForward("success");
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }else{//Enviar Mensaje Session Cerrada
//
//            }
//        }
//        return actionMapping.findForward("error");
    }
}
