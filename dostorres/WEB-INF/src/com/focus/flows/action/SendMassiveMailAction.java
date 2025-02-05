package com.focus.flows.action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.desige.webDocuments.utils.ToolsHTML;
import com.desige.webDocuments.utils.beans.SuperAction;
import com.desige.webDocuments.utils.beans.Users;
import com.focus.util.SendMassiveMailThread;

/**
 * Action para gestionar la peticion de envio masivo del correo asociado a los flujos existentes en el sistema.
 * 
 * Title: SendMassiveMailAction.java <br>
 * Copyright: (c) 2012 Focus Consulting C.A.<br/>
 * @author Ing. Felipe Rojas (FJR)
 * 
 * @version WebDocuments v4.5.2
 * <br>
 *     Changes:<br>
 * <ul>
 *     <li> 26/03/2012 (FJR) Creation </li>
 * <ul>
 */
public final class SendMassiveMailAction extends SuperAction{
	private static Logger log = LoggerFactory.getLogger(SendMassiveMailAction.class.getName());
	
	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
		// TODO Auto-generated method stub
		final String method = "SendMassiveMailAction.execute()";
		try {
			log.info("Iniciando " + method);
			
			super.init(mapping, form, request, response);
			
			SendMassiveMailThread mailThread = new SendMassiveMailThread(request.getSession(),
					ToolsHTML.getBundle(request),
					getUserSession());
			
			log.info(method + ": instanciado objeto para ejecutar thread de envio de correos");
			
			Thread th = new Thread(mailThread);
			log.info(method + ": Creado objeto Thread como tal");
			
			th.start();
			log.info(method + ": invocado metodo start en el thread de envio masivo");
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			log.error("Error en SendMassiveMailAction.execute(). Error fue: "
					+ e.getMessage(), e);
		}
		
		log.info("Finalizando " + method);
		return null;
	}

}
