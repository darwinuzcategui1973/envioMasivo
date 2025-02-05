package com.desige.webDocuments.enlaces.actions;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.desige.webDocuments.enlaces.forms.UrlsActionForm;
import com.desige.webDocuments.persistent.managers.HandlerUrls;
import com.desige.webDocuments.utils.ApplicationExceptionChecked;
import com.desige.webDocuments.utils.beans.SuperAction;

/**
 * Title: LoadUrlsAction.java <br/> 
 * Copyright: (c) 2004 Focus Consulting C.A.<br/>
 * @author Ing. Nelson Crespo (NC)
 * @version WebDocuments v3.0
 * <br/>
 * Changes:<br/> 
 * <ul>
 *      <li>08/04/2004 (NC) Creation </li>
 </ul>
 */
public class ShowUrlsAction extends SuperAction {
    public ActionForward execute(ActionMapping actionMapping,
                                 ActionForm form, HttpServletRequest request,
                                 HttpServletResponse response) {
        super.init(actionMapping,form,request,response);
        try {
            getUserSession();
            HandlerUrls.load((UrlsActionForm)form);
            putObjectSession("editarUrls",form);
            return goSucces();
        } catch (ApplicationExceptionChecked ae) {
            return goError(ae.getKeyError());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return goError();
    }
}
