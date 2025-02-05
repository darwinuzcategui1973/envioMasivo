package com.desige.webDocuments.state.actions;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.desige.webDocuments.persistent.managers.HandlerState;
import com.desige.webDocuments.state.forms.BaseStateForm;
import com.desige.webDocuments.utils.ApplicationExceptionChecked;
import com.desige.webDocuments.utils.beans.SuperAction;

/**
 * Title: LoadEditStateAction.java <br/>
 * Copyright: (c) 2004 Focus Consulting C.A.<br/>
 * @author Ing. Nelson Crespo (NC)
 * @version WebDocuments v3.0
 * <br/>
 * Changes:<br/> 
 * <ul>
 *      <li>01/05/2004 (NC) Creation </li>
 </ul>
 */
public class LoadEditStateAction extends SuperAction {
    public ActionForward execute(ActionMapping actionMapping,
                                 ActionForm form, HttpServletRequest request,
                                 HttpServletResponse response) {
        super.init(actionMapping,form,request,response);
        try {
            HandlerState.load((BaseStateForm)form);
            request.getSession().setAttribute("editStateForm",form);
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
