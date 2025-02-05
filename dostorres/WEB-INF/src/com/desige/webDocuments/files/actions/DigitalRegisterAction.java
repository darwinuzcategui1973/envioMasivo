package com.desige.webDocuments.files.actions;

import java.util.Collection;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.desige.webDocuments.persistent.managers.ActivityBD;
import com.desige.webDocuments.persistent.managers.HandlerTypeDoc;
import com.desige.webDocuments.utils.ApplicationExceptionChecked;
import com.desige.webDocuments.utils.beans.SuperAction;
import com.desige.webDocuments.utils.beans.Users;

/**
 * Title: DigitalRegisterAction.java <br/> Copyright: (c) 2008 Focus Consulting C.A.
 * <br/>
 * 
 * @author JRivero <br/> Changes:<br/>
 *         <ul>
 *         <li> 05/11/2008 (JR) Creation </li>
 *         </ul>
 */
public class DigitalRegisterAction extends SuperAction {
	private static Logger log = LoggerFactory.getLogger(DigitalRegisterAction.class.getName());

	public synchronized ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
		try {
			super.init(mapping, form, request, response);

			Users usuario = getUserSession();

            Collection typeDoc = HandlerTypeDoc.getAllTypeDocs(null,false);
            if (typeDoc!=null) {
            	request.setAttribute("typeDoc",typeDoc);
            } else {
            	request.setAttribute("typeDoc",new Vector());
            }
			
			return goSucces();
		} catch (ApplicationExceptionChecked ae) {
			return goError(ae.getKeyError());
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return goError();
	}
}
