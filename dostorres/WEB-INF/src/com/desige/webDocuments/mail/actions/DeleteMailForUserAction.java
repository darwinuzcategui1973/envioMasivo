package com.desige.webDocuments.mail.actions;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.desige.webDocuments.persistent.managers.HandlerMessages;
import com.desige.webDocuments.utils.ApplicationExceptionChecked;
import com.desige.webDocuments.utils.ToolsHTML;
import com.desige.webDocuments.utils.beans.SuperAction;

/**
 * Title: DeleteMailForUserAction.java <br/>
 * Copyright: (c) 2004 Focus Consulting C.A.<br/>
 *
 * @author Ing. Nelson Crespo
 * @version WebDocuments v3.0
 * <br/>
 *          Changes:<br/>
 * <ul>
 *          <li> 22/02/2005 (NC) Creation </li>
 * </ul>
 */
public class DeleteMailForUserAction extends SuperAction {
    public ActionForward execute(ActionMapping mapping,
                                 ActionForm form, HttpServletRequest request,
                                 HttpServletResponse response) {
        super.init(mapping,form,request,response);
        try {
            getUserSession();
            String[] items = request.getParameterValues("mail");
            String goTo = getParameter("goTo");
            //System.out.println("goTo = " + goTo);
            //System.out.println("items = " + items);
            if (ToolsHTML.isEmptyOrNull(goTo)){
                if (items!=null&&items.length > 0) {
                    boolean result = HandlerMessages.deleteMessageUser(items);
                    if (result) {
                        return goSucces();
                    } else {
                        return goError();
                    }
                }
            } else {
                if (items!=null&&items.length > 0) {
                    boolean result = HandlerMessages.deleteMessageSendUser(items);
                    if (result) {
                        return goTo(goTo);
                    } else {
                        return goError();
                    }
                }
            }
        } catch (ApplicationExceptionChecked ae) {
            return goError(ae.getKeyError());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return goError();
    }
}
