package com.desige.webDocuments.workFlows.actions;

import java.sql.Timestamp;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.desige.webDocuments.persistent.managers.HandlerDocuments;
import com.desige.webDocuments.persistent.managers.HandlerParameters;
import com.desige.webDocuments.persistent.managers.HandlerWorkFlows;
import com.desige.webDocuments.utils.ApplicationExceptionChecked;
import com.desige.webDocuments.utils.ToolsHTML;
import com.desige.webDocuments.utils.beans.SuperAction;
import com.desige.webDocuments.utils.beans.Users;
import com.desige.webDocuments.workFlows.forms.DataUserWorkFlowForm;

/**
 * Title: CompleteWorkFlowAction.java <br/>
 * Copyright: (c) 2004 Focus Consulting C.A.<br/>
 * 
 * @author Ing. Nelson Crespo(NC)
 * @author Ing. Sim�n Rodriguez(SR)
 * @version WebDocuments v3.0 <br/>
 *          Changes:<br/>
 *          <ul>
 *          <li>2005-02-28 (NC) Creation</li>
 *          </ul>
 */
public class CompletedWorkFlowAction extends SuperAction {
	static Logger log = LoggerFactory.getLogger("[V4.0 FTP] "
			+ CompletedWorkFlowAction.class.getName());

	public ActionForward execute(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) {
		super.init(mapping, form, request, response);

		try {
			Users usuario = getUserSession();
			String idWF = getParameter("idWorkFlow");
			String idDocument = getParameter("idDocument");
			Timestamp time = new Timestamp(new java.util.Date().getTime());

			DataUserWorkFlowForm forma = new DataUserWorkFlowForm();
			forma.setIdWorkFlow(Integer.parseInt(idWF));
			forma.setIdDocument(Integer.parseInt(idDocument));
			HandlerDocuments.loadDataDocument(forma);

			// StringBuffer msg = new
			// StringBuffer(getMessage("wf.canceled")).append(" ");
			// simon 25 mayo 2005 inicio
			// Mensaje para la completacion del documento
			String headerMessageCompleted = ""; // String.valueOf(getMessage("wf.headerMessageCompleted")).concat("[").concat(String.valueOf(usuario.getNameUser())).concat("] ");

			StringBuilder msg = new StringBuilder("");
			String varTemp = HandlerParameters.PARAMETROS.getMsgWFCompletados(); // mensaje para los
														// documentos
														// completados
			if (ToolsHTML.isEmptyOrNull(varTemp)) {
				msg = new StringBuilder(
						headerMessageCompleted
								.concat(getMessage("wf.completed")))
						.append(" ");
			} else {
				msg.append(headerMessageCompleted.concat(varTemp));
			}
			// simon 25 mayo 2005 fin

			msg.append(forma.getPrefix()).append(forma.getNumber())
					.append("<br/>").append(getMessage("wf.completedName"))
					.append(" ").append(forma.getNameDocument())
					.append("<br/>").append(getMessage("wf.completedUser"))
					.append(" ").append(usuario.getNamePerson())
					.append("<br/>").append(getMessage("wf.completeCause"))
					.append(": ").append(getParameter("commentsUser"));
			int row = Integer.parseInt(getParameter("row"));
			if (getParameter("isFlexFlow") != null
					&& "true".compareTo(getParameter("isFlexFlow")) == 0) {
				// log.debug("Cerrando Flujo de Trabajo Param�trico");
				log.debug("Completando Flujo de Trabajo Param�trico");
				if (!ToolsHTML.isEmptyOrNull(getParameter("idFlexFlow"))) {
					forma.setIdFlexFlow(Long
							.parseLong(getParameter("idFlexFlow")));
				}
				// Procedemos a completar el flujo seleccionado
				HandlerWorkFlows.completedFlexFlow(time, forma, msg.toString(),
						HandlerWorkFlows.response, HandlerWorkFlows.response,
						row, getParameter("commentsUser"),
						HandlerDocuments.lastCompletedWF, usuario.getEmail(),
						usuario.getNamePerson(),
						getMessage("wf.completeTitle"), usuario.getUser(),
						request);
			}
			ActionForward resp = new ActionForward(
					"/showDataDocument.do?idDocument=" + idDocument, false);
			putObjectSession("info", getMessage("completeWF.ok"));
			resp.setName("success");
			return resp;
		} catch (ApplicationExceptionChecked ae) {
			log.error(ae.getMessage());
			return goError(ae.getKeyError());
		} catch (Exception ex) {
			log.error(ex.getMessage());
			ex.printStackTrace();
		}
		return goError();
	}
}
