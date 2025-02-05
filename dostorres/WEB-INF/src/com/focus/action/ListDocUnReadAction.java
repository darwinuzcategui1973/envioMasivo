package com.focus.action;

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
import com.desige.webDocuments.persistent.managers.HandlerDBUser;
import com.desige.webDocuments.persistent.managers.HandlerDocuments;
import com.desige.webDocuments.utils.ApplicationExceptionChecked;
import com.desige.webDocuments.utils.ToolsHTML;
import com.desige.webDocuments.utils.beans.SuperAction;
import com.desige.webDocuments.utils.beans.Users;

/**
 * Title: DigitalizarLoadAction.java <br/> Copyright: (c) 2004 Focus Consulting
 * C.A.<br/>
 * 
 */
public class ListDocUnReadAction extends SuperAction {
	private static Logger log = LoggerFactory.getLogger(ListDocUnReadAction.class.getName());
	
	private Users usuario = null;
	private ResourceBundle rb = null;

	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {

		super.init(mapping, form, request, response);
		
		try {
			usuario = getUserSession();
			
			request.setAttribute("listDocUnRead",HandlerDocuments.getDocumentUnRead(request));
			
		} catch (ApplicationExceptionChecked e) {
			e.printStackTrace();
			return goError(e.getKeyError());
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return goSucces();
	}


}
