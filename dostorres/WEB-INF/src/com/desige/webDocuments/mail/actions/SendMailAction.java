package com.desige.webDocuments.mail.actions;

import java.sql.Connection;
import java.util.ResourceBundle;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.desige.webDocuments.mail.forms.MailForm;
import com.desige.webDocuments.persistent.managers.HandlerBD;
import com.desige.webDocuments.persistent.managers.HandlerParameters;
import com.desige.webDocuments.utils.ApplicationExceptionChecked;
import com.desige.webDocuments.utils.ToolsHTML;
import com.desige.webDocuments.utils.beans.SuperAction;
import com.desige.webDocuments.utils.beans.Users;
import com.desige.webDocuments.utils.mail.SendMail;

/**
 * Title: SendMailAction.java <br/>
 * Copyright: (c) 2004 Focus Consulting C.A.<br/>
 * 
 * @author Ing. Nelson Crespo
 * @version WebDocuments v3.0 <br/>
 *          Changes:<br/>
 *          <ul>
 *          <li>25/06/2004 (NC) Creation</li>
 *          <li>03/05/2006 La bandeja de entrada de los mails no funcionaba, era
 *          vulnerable en caso que un usuario cambiara su mail, y en cada mail
 *          se manda el numero del documento en el titulo (SR)</li>
 * 
 *          </ul>
 */
public class SendMailAction extends SuperAction {
	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request,
			HttpServletResponse response) {
		ResourceBundle rb = ToolsHTML.getBundle(request);
		super.init(mapping, form, request, response, rb);

		try {
			Users usuario = getUserSession();
			MailForm forma = (MailForm) form;
			forma.setUserName(usuario.getUser());
			if (send(forma)) {
				
				putObjectSession("info", rb.getString("mail.enviar"));
				removeObjectSession("width");
				removeObjectSession("idDocument");
				return goTo(getParameter("goTo"));
			}
		} catch (ApplicationExceptionChecked ae) {
			// System.out.println("[ApplicationExceptionChecked]");
			int pos = ae.getMessage().indexOf(":");
			if (pos > 0) {
				StringBuffer mens = new StringBuffer(100);
				mens.append(rb.getString(ae.getMessage().substring(0, pos)));
				mens.append(" ").append(rb.getString("E0001"));
				mens.append(": ").append(ae.getMessage().substring(pos + 1));
				putObjectSession("info", mens.toString());
				String allUsers = getParameter("allUsers");
				putAtributte("allUsers", allUsers);
				return goError(null);
			} else {
				putObjectSession("info", rb.getString(ae.getMessage()));
				return goError(ae.getKeyError());
			}
		} catch (Exception e) {
			// System.out.println("[Exception]");
			StringBuffer info = new StringBuffer(rb.getString("mail.error"));
			putObjectSession("info", info.toString());
			e.printStackTrace();
			String allUsers = getParameter("allUsers");
			putAtributte("allUsers", allUsers);
		}
		return goError();
	}

	public static boolean send(MailForm forma) throws ApplicationExceptionChecked, Exception {
		String smtp = HandlerParameters.PARAMETROS.getSmtpMail();
		

		return SendMail.sendMail(smtp, forma);
	}

	public static boolean send(Connection con, MailForm forma) throws ApplicationExceptionChecked, Exception {
		String smtp = HandlerParameters.PARAMETROS.getSmtpMail();
		int pos = forma.getFileName().indexOf("reci");
		if (pos > 0) {

			return SendMail.sendMailWithAttach(smtp, forma);
		} else

			return SendMail.sendMail(smtp, forma);
	}
}
    