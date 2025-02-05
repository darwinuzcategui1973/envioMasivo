package com.desige.webDocuments.files.actions;

import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.desige.webDocuments.dao.ConfExpedienteDAO;
import com.desige.webDocuments.files.forms.FilesForm;
import com.desige.webDocuments.utils.ApplicationExceptionChecked;
import com.desige.webDocuments.utils.beans.SuperAction;
import com.desige.webDocuments.utils.beans.Users;

/**
 * Title: DigitalNewAction.java <br/>
 * Copyright: (c) 2008 Focus Consulting C.A. <br/>
 * @author JRivero
 * <br/>
 *      Changes:<br/>
 * <ul>
 *      <li> 05/11/2008 (JR) Creation </li>
 * </ul>
 */
public class DigitalNewAction extends SuperAction {
	private static Logger log = LoggerFactory.getLogger(DigitalNewAction.class.getName());
	
    public synchronized ActionForward execute(ActionMapping mapping,
                                 ActionForm form, HttpServletRequest request,
                                 HttpServletResponse response) {
        try {
            super.init(mapping,form,request,response);
            
			Users usuario = getUserSession();

            return goSucces();
        } catch (ApplicationExceptionChecked ae){
            return goError(ae.getKeyError());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return goError();
    }
}
