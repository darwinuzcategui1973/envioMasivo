package com.desige.webDocuments.mail.actions;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.desige.webDocuments.mail.forms.MailForm;
import com.desige.webDocuments.persistent.managers.HandlerMessages;
import com.desige.webDocuments.utils.ApplicationExceptionChecked;
import com.desige.webDocuments.utils.ToolsHTML;
import com.desige.webDocuments.utils.beans.SuperAction;
import com.desige.webDocuments.utils.beans.Users;

/**
 * Title: LoadDetailMailAction.java<br>
 * Copyright: (c) 2004 Focus Consulting C.A.<br/>
 *
 * @author Ing. Nelson Crespo (NC)
 * @version WebDocuments v3.0
 *  <br>
 *      Changes:<br>
 *  <ul>
 *       <li> 17-09-2004 (NC) Creation </li>
 *  <ul>
 */
public class LoadDetailMailAction extends SuperAction {
    public ActionForward execute(ActionMapping mapping,
                                 ActionForm form, HttpServletRequest request,
                                 HttpServletResponse response) {
        super.init(mapping,form,request,response);
        removeAttribute("sendMail",request);
        String idMessage = request.getParameter("idMessage");
        String idMessageUser = request.getParameter("idMessageUser");
        MailForm mail = (MailForm) form;
        if (mail==null) {
            mail = new MailForm();
        }
        mail.setIdMessage(idMessage);
        try {
            getUserSession();
            mail.setIdMessageUser(idMessageUser);
            HandlerMessages.loadMail(mail);
            mail.setMensaje(ToolsHTML.updateURLVerDocumento(mail.getMensaje(), request, null));
            Users user = (Users)request.getSession().getAttribute("user");
            HandlerMessages.updateStatuMailUser(Integer.parseInt(idMessage),user.getEmail());
            putObjectSession("sendMail",mail);
            //System.out.println("return" + mapping.findForward("success"));
            return goSucces();
        } catch (ApplicationExceptionChecked ae) {
            return goError(ae.getKeyError());
		} catch (Exception e) {
			e.printStackTrace();
		}
        return goError();
    }
}
