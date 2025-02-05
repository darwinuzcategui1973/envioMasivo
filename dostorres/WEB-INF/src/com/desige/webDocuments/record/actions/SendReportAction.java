package com.desige.webDocuments.record.actions;

import java.io.File;
import java.util.ResourceBundle;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.desige.webDocuments.mail.forms.MailForm;
import com.desige.webDocuments.persistent.managers.HandlerParameters;
import com.desige.webDocuments.utils.ApplicationExceptionChecked;
import com.desige.webDocuments.utils.ToolsHTML;
import com.desige.webDocuments.utils.beans.SuperAction;
import com.desige.webDocuments.utils.beans.Users;
import com.desige.webDocuments.utils.mail.SendMail;

/**
 * Title: SendReportAction.java<br>
 * Copyright: (c) 2008 Focus Consulting<br>
 * Company:Focus Consulting (CON)<br>
 * @author YSA
 * <br>
 * Changes:<br>
 * <ul>
 *      <li> 03/01/2008 (YSA) Creation </li>
 * <ul>
 */

public class SendReportAction extends SuperAction {
    public ActionForward execute(ActionMapping mapping,
                                 ActionForm form, HttpServletRequest request,
                                 HttpServletResponse response) {
        ResourceBundle rb = ToolsHTML.getBundle(request);
        super.init(mapping,form,request,response,rb);

        try {
            Users usuario = getUserSession();
			MailForm forma = (MailForm)form;
            forma.setUserName(usuario.getUser());
	        
            String path = ToolsHTML.getPath();
	        String fileName = path.concat("reportes").concat(File.separator).concat(usuario.getIdPerson()+"Record.xls");

            forma.setFileName(fileName);
            if (send(forma)) {
            	request.getSession().setAttribute("info",rb.getString("mail.enviar"));
                return goTo(getParameter("goTo"));
            }
		} catch (ApplicationExceptionChecked ae) {
            //System.out.println("[ApplicationExceptionChecked]");
			int pos = ae.getMessage().indexOf(":");
			if (pos > 0) {
				StringBuffer mens = new StringBuffer(100);
				mens.append(rb.getString(ae.getMessage().substring(0,pos)));
				mens.append(" ").append(rb.getString("E0001"));
				mens.append(": ").append(ae.getMessage().substring(pos+1));
				request.getSession().setAttribute("info",mens.toString());
                return goError(null);
			} else {
				request.getSession().setAttribute("info",rb.getString(ae.getMessage()));
                return goError(ae.getKeyError());
			}                        
        } catch (Exception e) {
            //System.out.println("[Exception]");
            StringBuffer info = new StringBuffer(rb.getString("mail.error"));
            request.getSession().setAttribute("info",info.toString());
            e.printStackTrace();
        }
        return goError();
    }
    
    public static boolean send(MailForm forma) throws ApplicationExceptionChecked , Exception{
        String smtp = HandlerParameters.PARAMETROS.getSmtpMail();

        return SendMail.sendMailWithAttach(smtp, forma);
    }
}
