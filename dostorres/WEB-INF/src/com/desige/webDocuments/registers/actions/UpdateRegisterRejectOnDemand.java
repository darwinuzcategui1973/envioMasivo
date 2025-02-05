package com.desige.webDocuments.registers.actions;

import java.sql.Connection;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.desige.webDocuments.persistent.managers.HandlerDocuments;
import com.desige.webDocuments.persistent.utils.JDBCUtil;
import com.desige.webDocuments.utils.ApplicationExceptionChecked;
import com.desige.webDocuments.utils.ToolsHTML;
import com.desige.webDocuments.utils.beans.SuperAction;
import com.desige.webDocuments.utils.beans.Users;

/**
 * Title: UpdateRegister.java <br/>
 * Copyright: (c) 2004 Focus Consulting C.A.<br/>
 * 
 * @author Ing. Nelson Crespo
 * @version WebDocuments v3.0 <br/>
 *          Changes:<br/>
 *          <ul>
 *          <li>19/02/2005 (NC) Creation</li>
 *          </ul>
 */
public class UpdateRegisterRejectOnDemand extends SuperAction {
	public ActionForward execute(ActionMapping actionMapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) {
		super.init(actionMapping, form, request, response);
		Connection con = null;

		try {
			Users usuario = getUserSession();
			if (!ToolsHTML.isEmptyOrNull(String
					.valueOf(getSessionObject("rejectDocument")))) {

				// buscamos la version del documento
				int idVersion = 0;

				Object rejectVerbose = request.getSession().getAttribute(
						"documentRejectVebose");
				if (rejectVerbose != null
						&& !rejectVerbose.toString().equals("")) {
					idVersion = Integer.parseInt(rejectVerbose.toString());
				} else {
					throw new Exception(
							"La version del documento rechazado no esta en session");
				}
				request.getSession().removeAttribute("documentRejectVebose");

				HandlerDocuments.approvedDocumentRejection(
						String.valueOf(idVersion),
						String.valueOf(getSessionObject("rejectDocument")),
						usuario,
						String.valueOf(getSessionObject("rejectFlexFlow")));
				putAtributte("closeWindow", "true");
				return goTo("closeAndReject");
			}

			ActionForward toStruct = new ActionForward(
					"/loadStructMain.do?idNodeSelected="
							+ getParameter("idNodeSelected"), false);
			return toStruct;

		} catch (ApplicationExceptionChecked ae) {
			ae.printStackTrace();
			return goError(ae.getKeyError());
		} catch (NumberFormatException e) {
			e.printStackTrace();
			return goError();
		} catch (Exception e) {
			e.printStackTrace();
			return goError();
		} finally {
			JDBCUtil.closeConnection(con, null);
		}
	}
}
