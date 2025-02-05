package com.desige.webDocuments.pais.actions;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.desige.webDocuments.pais.forms.BaseCountryForm;
import com.desige.webDocuments.persistent.managers.HandlerPais;
import com.desige.webDocuments.utils.ApplicationExceptionChecked;
import com.desige.webDocuments.utils.beans.SuperAction;

/**
 * Title: LoadEditCountry.java   <br>
 * Copyright: (c) 2003 Consis International<br>
 * Company: Consis International (CON)<br>
 * @author Nelson Crespo (NC)
 * @version Acsel-e v2.2
 * <br>
 * Changes:<br>
 * <ul>
 *      <li> 21/04/2004 (NC) Creation </li>
 * <ul>
 */
public class LoadEditCountry extends SuperAction {
    public ActionForward execute(ActionMapping actionMapping,
                                 ActionForm form, HttpServletRequest request,
                                 HttpServletResponse response) {
        super.init(actionMapping,form,request,response);
        try {
            getUserSession();
            HandlerPais.load((BaseCountryForm)form);
            //System.out.println("Seteando valor....");
            request.getSession().setAttribute("editCountry",form);
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
