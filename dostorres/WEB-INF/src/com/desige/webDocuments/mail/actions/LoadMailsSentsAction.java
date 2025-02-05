package com.desige.webDocuments.mail.actions;

import java.util.Collection;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.desige.webDocuments.persistent.managers.HandlerMessages;
import com.desige.webDocuments.utils.ApplicationExceptionChecked;
import com.desige.webDocuments.utils.beans.SuperAction;
import com.desige.webDocuments.utils.beans.Users;

/**
 * Title: LoadMailsSentsAction.java <br/>
 * Copyright: (c) 2004 Focus Consulting C.A.<br/>
 * @author Ing. Nelson Crespo
 * @version WebDocuments v3.0
 * <br/>
 *   Changes:<br/>
 * <ul>
 *      <li> 08/11/2004 (NC) Creation </li>
 * </ul>
 */
public class LoadMailsSentsAction extends SuperAction {

    public ActionForward execute(ActionMapping mapping,
                                 ActionForm form, HttpServletRequest request,
                                 HttpServletResponse response) {
        try{
            super.init(mapping,form,request,response);
            removeAttribute("mailSents",request);
            Users usuario = getUserSession();
            Collection mailSents = HandlerMessages.getAllSentsMessagesForUser(usuario.getUser());
            putObjectSession("mailSents",mailSents);
            removeAttribute("size");
            if (mailSents!=null&&mailSents.size() > 0) {
                putObjectSession("size",String.valueOf(mailSents.size()));
            }
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
