package com.desige.webDocuments.workFlows.actions;

import java.util.Collection;
import java.util.Hashtable;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.desige.webDocuments.document.forms.BaseDocumentForm;
import com.desige.webDocuments.document.forms.DocumentsCheckOutsBean;
import com.desige.webDocuments.persistent.managers.ActivityBD;
import com.desige.webDocuments.persistent.managers.HandlerDocuments;
import com.desige.webDocuments.persistent.managers.HandlerStruct;
import com.desige.webDocuments.utils.ApplicationExceptionChecked;
import com.desige.webDocuments.utils.Constants;
import com.desige.webDocuments.utils.ToolsHTML;
import com.desige.webDocuments.utils.beans.SuperAction;
import com.desige.webDocuments.utils.beans.Users;

/**
 * Title: LoadActNewWFAction.java <br/>
 * Copyright: (c) 2004 Desige Servicios de Computación<br/>
 *
 * @author Ing. Nelson Crespo
 * @version WebDocuments v1.0 <br/>
 *          Changes:<br/>
 *          <ul>
 *          <li>19/12/2005 (NC) Creation</li>
 *          </ul>
 */
public class LoadActNewWFAction extends SuperAction {
	static Logger log = LoggerFactory.getLogger(LoadActNewWFAction.class.getName());

	public ActionForward execute(ActionMapping actionMapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
		super.init(actionMapping, form, request, response);
		StringBuffer blockBy = new StringBuffer(30);
		try {
			Users user = getUserSession();
			String idDocument = getParameter("idDocument");
			log.debug("idDocument: " + idDocument);
			System.out.println(request.getSession().getAttribute(Constants.MODULO_ACTIVO));
			if (ToolsHTML.isNumeric(idDocument.trim())) {
				int numDoc = 0;
				// Verificamos que el documento no se encuentre Bloqueado
				numDoc = Integer.parseInt(idDocument.trim());
				DocumentsCheckOutsBean isCheckOut = HandlerDocuments.isCheckOutDoc(idDocument.trim());
				if (isCheckOut != null && isCheckOut.isCheckOut()) {
					String[] values = HandlerDocuments.getFields(new String[] { "nombres", "apellidos" }, "person", "nameuser",
							"'" + isCheckOut.getDoneBy() + "'");
					if (values != null) {
						blockBy.append(" ").append(values[0]);
						blockBy.append(" ").append(values[1]);
					}
					throw new ApplicationExceptionChecked("E0028");
				}
			} else {
				throw new ApplicationExceptionChecked("err.invalidDocument");
			}
			BaseDocumentForm forma = new BaseDocumentForm();
			forma.setIdDocument(idDocument);
			forma.setNumberGen(idDocument);
			Hashtable tree = (Hashtable) getSessionObject("tree");
			tree = ToolsHTML.checkTree(tree, user);
			HandlerStruct.loadDocument(forma, true, false, tree, request);
			
			// Se Cargan las Actividades según el tipo de Documento Seleccionado
			Collection acti = ActivityBD.getAllActivities(forma.getTypeDocument());
			if (acti != null) {
				putObjectSession("activities", acti);
				putObjectSession("size", String.valueOf(acti.size()));
				putAtributte("idDocument", idDocument);
				request.getSession().setAttribute("idDocument", idDocument);
				System.out.println(request.getSession().getAttribute(Constants.MODULO_ACTIVO));
				return goSucces();
			} else {
				throw new ApplicationExceptionChecked("E0107");
			}
		} catch (ApplicationExceptionChecked ae) {
			if ("E0028".compareTo(ae.getKeyError()) == 0) {
				return goErrorII(ae.getKeyError(), blockBy.toString());
			}
			return goError(ae.getKeyError());
		} catch (Exception ex) {
			ex.printStackTrace();
			return goError("E0008");
		}
		// return goError();
	}
}
