package com.focus.migracion.actions;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.desige.webDocuments.utils.ApplicationExceptionChecked;
import com.desige.webDocuments.utils.ToolsHTML;
import com.desige.webDocuments.utils.beans.SuperAction;
import com.desige.webDocuments.utils.beans.Users;


/**
 * Title: MigracionAction.java <br/> Copyright: (c) 2007 Focus Consulting C.A.<br/>
 * 
 * @author Ing. Nelson Crespo (NC)
 * @version WebDocuments v3.0 <br/> Changes:<br/>
 * <ul>
 *      <li>18/09/2007 (YSA) Creation </li>
 * </ul>
 */

public class MigracionAction extends SuperAction {
	static Logger log = LoggerFactory.getLogger(MigracionAction.class.getName());

	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
		super.init(mapping, form, request, response);
		
        try {
        	Users user = getUserSession();
        	removeObjectSession("statusMigracion");
            if (ToolsHTML.checkValue(user.getUser())) {
            	String type = request.getParameter("type");
            	putObjectSession("typeMigration",type);
            	return goSucces();
            }
        } catch (ApplicationExceptionChecked ae) {
            return goError(ae.getKeyError());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return goError();

	}
}
