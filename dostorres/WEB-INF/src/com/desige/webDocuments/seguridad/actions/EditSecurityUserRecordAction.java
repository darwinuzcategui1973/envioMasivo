package com.desige.webDocuments.seguridad.actions;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.desige.webDocuments.persistent.managers.HandlerDocuments;
import com.desige.webDocuments.seguridad.forms.PermissionUserForm;
import com.desige.webDocuments.utils.ApplicationExceptionChecked;
import com.desige.webDocuments.utils.beans.SuperAction;
import com.desige.webDocuments.utils.beans.Users;

/**
 * Title: EditSecurityStructUserAction.java <br/> Copyright: (c) 2004 Focus
 * Consulting C.A.<br/>
 * 
 * @author Ing. Nelson Crespo
 * @version WebDocuments v3.0 <br/> Changes:<br/>
 *          <ul>
 *          <li> 15/12/2004 (NC) Creation </li>
 *          <li> 29/05/2005 (NC) Cambios en el manejo de la Seguridad </li>
 *          <li> 27/04/2006 (NC) Nueva llamada para actualizar la Seguridad</li>
 *          <li> 30/06/2006 (NC) uso del Log </li>
 *          </ul>
 */
public class EditSecurityUserRecordAction extends SuperAction {
	static Logger log = LoggerFactory.getLogger(EditSecurityStructUserAction.class);

	public ActionForward execute(ActionMapping actionMapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
		super.init(actionMapping, form, request, response);
		try {
			Users user = getUserSession();
			PermissionUserForm forma = (PermissionUserForm) form;

			boolean statu = false;

			statu = HandlerDocuments.updateSecurityRecordUser(forma, true);

			if (statu) {
				try {
					putObjectSession("info", getMessage("seguridad.editOk"));
					return goSucces();
				} catch (Exception ex) {
					ex.printStackTrace();
				}
				return goSucces("seguridad.editOk");
			}
		} catch (ApplicationExceptionChecked ae) {
			return goError(ae.getKeyError());
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return goError("error.general");
	}
}
