package com.desige.webDocuments.mail.actions;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.desige.webDocuments.mail.forms.MailForm;
import com.desige.webDocuments.persistent.managers.HandlerGrupo;
import com.desige.webDocuments.utils.ApplicationExceptionChecked;
import com.desige.webDocuments.utils.beans.SuperAction;
import com.desige.webDocuments.utils.beans.Users;

/**
 * Title: LoadMailAll.java <br/>
 * Copyright: (c) 2004 Focus Consulting C.A.<br/>
 * @author Ing. Roger Rodríguez
 * @version WebDocuments v3.0
 * <br/>
 * Changes:<br/> 
 * <ul>
 *      <li>03/08/2004 (RR) Creation </li>
 </ul>
 */
public class LoadMailAll extends SuperAction {
	public ActionForward execute(ActionMapping mapping,
                                 ActionForm form, HttpServletRequest request,
                                 HttpServletResponse response) {
        super.init(mapping,form,request,response);
        try {
            Users usuario =getUserSession();
            removeAttribute("sendMail",request);
            MailForm forma = new MailForm();
//            Users usuario = (Users)request.getSession().getAttribute("user");
            forma.setFrom(usuario.getEmail());
            String mails = HandlerGrupo.getEmailGrupos(null);
            forma.setTo(mails);
            forma.setNameFrom(usuario.getNamePerson());
            //System.out.println("usuario.getEmail() = " + usuario.getEmail());
            putObjectSession("sendMail",forma);
            putAtributte("allUsers","true");
            return goSucces();
        } catch (ApplicationExceptionChecked ae) {
            return goError(ae.getKeyError());
		} catch (Exception e) {
			e.printStackTrace();
		}
        return goError();
    }
}
