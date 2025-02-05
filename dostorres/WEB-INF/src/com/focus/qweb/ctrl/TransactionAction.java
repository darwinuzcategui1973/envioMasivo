package com.focus.qweb.ctrl;

import java.util.ResourceBundle;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.desige.webDocuments.utils.ApplicationExceptionChecked;
import com.desige.webDocuments.utils.beans.SuperAction;
import com.desige.webDocuments.utils.beans.Users;
import com.focus.action.ListDocUnReadAction;
import com.focus.qweb.facade.TransactionFacade;

public class TransactionAction extends SuperAction {
	private static Logger log = LoggerFactory.getLogger(ListDocUnReadAction.class.getName());

	private Users usuario = null;
	private ResourceBundle rb = null;

	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {

		super.init(mapping, form, request, response);

		try {
			usuario = getUserSession();
			TransactionFacade oTransactionFacade = new TransactionFacade(request); 

			request.getSession().setAttribute("listTransactionSession", oTransactionFacade.listarTransactionMapFacade());

		} catch (ApplicationExceptionChecked e) {
			e.printStackTrace();
			return goError(e.getKeyError());
		} catch (Exception e) {
			e.printStackTrace();
		}

		return goSucces();
	}

}
