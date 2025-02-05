package com.desige.webDocuments.document.actions;

import java.util.ResourceBundle;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.desige.webDocuments.persistent.managers.HandlerDocuments;
import com.desige.webDocuments.utils.ApplicationExceptionChecked;
import com.desige.webDocuments.utils.ToolsHTML;
import com.desige.webDocuments.utils.beans.SuperAction;
import com.desige.webDocuments.utils.beans.Users;

/**
 * Title: BackCheckOutDocAction.java <br/>
 * Copyright: (c) 2004 Focus Consulting C.A.<br/>
 * 
 * @author Ing. Nelson Crespo
 * @version WebDocuments v3.0 <br/>
 *          Changes:<br/>
 *          <ul>
 *          <li>08/02/2005 (NC) Creation</li>
 *          <li>01/06/2005 (NC) Cambios para agregar alg�n comentario al hacer
 *          roll Back</li>
 *          </ul>
 */
public class BackCheckOutDocAction extends SuperAction {
	public ActionForward execute(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) {
		ResourceBundle rb = ToolsHTML.getBundle(request);
		super.init(mapping, form, request, response, rb);
		try {
			String idDocument = getParameter("idDocument");
			String idCheckOut = getParameter("idCheckOut");
			String comments = getParameter("comments");
			Users usuario = getUserSession();
			boolean result = HandlerDocuments.undoCheckOutDoc(idDocument,
					usuario.getUser(), idCheckOut, comments);
			if (result) {
				return goSucces("chk.backCOOK");
			} else {
				return goError("E0117");
			}
		} catch (ApplicationExceptionChecked ae) {
			ae.printStackTrace();
			return goError(ae.getKeyError());
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return goError();
	}
}
