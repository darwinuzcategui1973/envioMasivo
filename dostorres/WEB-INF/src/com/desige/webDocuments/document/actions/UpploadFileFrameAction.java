package com.desige.webDocuments.document.actions;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.desige.webDocuments.files.facade.DigitalFacade;
import com.desige.webDocuments.to.DigitalTO;
import com.desige.webDocuments.utils.ToolsHTML;

/**
 * Title: FuncionesEndosos.java<br>
 * Copyright: (c) 2003 Consis International<br>
 * Company: Consis International (CON)<br>
 * 
 * @author Nelson Crespo (NC)
 * @version Acsel-e v2.2 <br>
 *          Changes:<br>
 *          <ul>
 *          <li> 05/07/2004 (NC) Creation </li>
 *          <ul>
 */
public class UpploadFileFrameAction extends Action {

	public ActionForward execute(ActionMapping actionMapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
		try {
			
			// buscamos el anterior y el proximo del id dado
			DigitalFacade digital = new DigitalFacade(request);
			DigitalTO digitalTO = new DigitalTO();
			
			digitalTO.setIdDigital(request.getParameter("idDigital"));
			
			if(digitalTO.getIdDigital()==null || digitalTO.getIdDigital().equals("") || digitalTO.getIdDigital().equals("0")) { 
				return actionMapping.findForward("successLista");
			}
			
			digitalTO = digital.findById(digitalTO);
			
			String previous = digital.getPrevious(digitalTO);
			String next = digital.getNext(digitalTO);
			
			request.getSession().setAttribute("digitalTO", digitalTO);
			request.getSession().setAttribute("idDigital", digitalTO.getIdDigital());
			request.getSession().setAttribute("anterior", previous);
			request.getSession().setAttribute("siguiente", next);
			request.getSession().setAttribute("nodeActive", digitalTO.getIdNode());
			
			if(request.getParameter("pdf")!=null && request.getParameter("pdf").equals("true")) {
				response.sendRedirect(ToolsHTML.getFileBasePathDigitalizados(request,digitalTO.getIdDigital()));
			} else {
				return actionMapping.findForward("success");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return (actionMapping.findForward("error"));
	}

}
