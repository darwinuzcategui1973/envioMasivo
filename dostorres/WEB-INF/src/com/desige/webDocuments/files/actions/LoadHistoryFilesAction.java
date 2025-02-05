package com.desige.webDocuments.files.actions;

import java.util.Collection;
import java.util.ResourceBundle;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.desige.webDocuments.dao.ExpedienteDAO;
import com.desige.webDocuments.files.forms.ExpedienteForm;
import com.desige.webDocuments.persistent.managers.HandlerStruct;
import com.desige.webDocuments.structured.forms.DataHistoryStructForm;
import com.desige.webDocuments.utils.ApplicationExceptionChecked;
import com.desige.webDocuments.utils.ToolsHTML;
import com.desige.webDocuments.utils.beans.SuperAction;

/**
 * Title: LoadHistoryDocsAction.java <br/> Copyright: (c) 2004 Focus Consulting
 * C.A.<br/>
 * 
 * @author Ing. Nelson Crespo
 * @version WebDocuments v3.0 <br/> Changes:<br/>
 *          <ul>
 *          <li> 31/08/2004 (NC) Creation </li>
 *          </ul>
 */
public class LoadHistoryFilesAction extends SuperAction {
	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
		super.init(mapping, form, request, response);
		
		ExpedienteForm oExpedienteForm = new ExpedienteForm();
		ExpedienteDAO oExpedienteDAO = new ExpedienteDAO();
		
		String idNode = request.getParameter("f1") != null ? request.getParameter("f1") : (String) request.getSession().getAttribute("f1");
		String histSelect = request.getParameter("histSelect");
		putObjectSession("f1", idNode);
	    ResourceBundle rb = ToolsHTML.getBundle(request);
		try {
			getUserSession();
			
			oExpedienteForm.setF1(Integer.parseInt(idNode));
			
			Collection history = HandlerStruct.getHistoryFiles(idNode);
			
			putObjectSession("historystruct", history);
			String name = rb.getString("files.files").concat(" (").concat(String.valueOf(oExpedienteForm.getF1()).concat(")"));
			if (!ToolsHTML.isEmptyOrNull(name)) {
				putObjectSession("nameStruct", name);
			}

			if (request.getSession().getAttribute("dataHistoryStruct") != null) {
				removeAttribute("dataHistoryStruct");
			}

			if ((!ToolsHTML.isEmptyOrNull(histSelect)) && (!"-1".equalsIgnoreCase(histSelect))) {
				DataHistoryStructForm forma = HandlerStruct.loadHistoryFiles(histSelect, null);
				putObjectSession("dataHistoryStruct", forma);
			}
			removeObjectSession("historyimpresion");
			//if ("-1".equalsIgnoreCase(histSelect)) {
			//	DataHistoryStructForm forma = HandlerStruct.loadHistoryDoc(null, idNode);
			//	putObjectSession("dataHistoryStruct", forma);
			//	putObjectSession("historyimpresion", "1");
			//}
			//String historicoImpreso = HandlerStruct.loadHistoryImpresion(idNode);
			//putObjectSession("historicoImpreso", historicoImpreso);
			putObjectSession("loadDocs", "true");
			return goSucces();
		} catch (ApplicationExceptionChecked ae) {
			return goError(ae.getKeyError());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return goError();
	}
}
