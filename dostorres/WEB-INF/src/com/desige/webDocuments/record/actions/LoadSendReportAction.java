package com.desige.webDocuments.record.actions;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.desige.webDocuments.mail.forms.MailForm;
import com.desige.webDocuments.utils.ApplicationExceptionChecked;
import com.desige.webDocuments.utils.beans.SuperAction;
import com.desige.webDocuments.utils.beans.Users;

/**
 * Title: LoadSendReportAction.java<br>
 * Copyright: (c) 2008 Focus Consulting<br>
 * Company:Focus Consulting (CON)<br>
 * 
 * @author YSA <br>
 *         Changes:<br>
 *         <ul>
 *         <li>03/01/2008 (YSA) Creation</li>
 *         <ul>
 */

public class LoadSendReportAction extends SuperAction {
	
	/**
	 * 
	 */
	public ActionForward execute(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) {
		try {
			super.init(mapping, form, request, response);
			MailForm forma = new MailForm();
			Users usuario = getUserSession();
			forma.setFrom(usuario.getEmail());
			forma.setNameFrom(usuario.getNamePerson());
			request.getSession().setAttribute("sendMail", forma);

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
