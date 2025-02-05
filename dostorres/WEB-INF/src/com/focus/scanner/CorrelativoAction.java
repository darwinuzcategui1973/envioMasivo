package com.focus.scanner;

import java.util.ArrayList;
import java.util.Collections;
import java.util.ResourceBundle;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.desige.webDocuments.dao.DigitalDAO;
import com.desige.webDocuments.utils.ApplicationExceptionChecked;
import com.desige.webDocuments.utils.ToolsHTML;
import com.desige.webDocuments.utils.beans.SuperAction;
import com.desige.webDocuments.utils.beans.Users;

/**
 * Title: DigitalizarLoadAction.java <br/> Copyright: (c) 2004 Focus Consulting
 * C.A.<br/>
 * 
 */
public class CorrelativoAction extends SuperAction {
	private static Logger log = LoggerFactory.getLogger(CorrelativoAction.class.getName());
	
	private Users usuario = null;
	private ResourceBundle rb = null;

	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {

		super.init(mapping, form, request, response);
		
		try {
			usuario = getUserSession();
		    rb = ToolsHTML.getBundle(request);
		    DigitalDAO digitalDAO = new DigitalDAO();
			
			if(request.getParameter("guardar")!=null && request.getParameter("guardar").equals("true")) {
				if(request.getParameterValues("codigo")!=null) {
					String[] lista = request.getParameterValues("codigo");
					usuario.setConsecutivo(new ArrayList());
					for(int i=0;i<lista.length;i++) {
						usuario.getConsecutivo().add(lista[i]);
					}
					Collections.sort(usuario.getConsecutivo());
				}
				request.setAttribute("MENSAJE", rb.getString("sistema.applied"));
			}
			
			
			request.setAttribute("inicio",request.getParameter("inicio"));
			request.setAttribute("ultimo",request.getParameter("ultimo"));
			request.setAttribute("prefijo",request.getParameter("prefijo"));
			request.setAttribute("NUMERACION_DIGITAL",digitalDAO.findAllNumber(request.getParameter("inicio")));
			
		} catch (ApplicationExceptionChecked e) {
			e.printStackTrace();
			return goError(e.getKeyError());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return goSucces();
	}


}
