package com.desige.webDocuments.structured.actions;

import java.util.Hashtable;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.desige.webDocuments.persistent.managers.HandlerDBUser;
import com.desige.webDocuments.persistent.managers.HandlerGrupo;
import com.desige.webDocuments.persistent.managers.HandlerMessages;
import com.desige.webDocuments.persistent.managers.HandlerStruct;
import com.desige.webDocuments.persistent.managers.HandlerWorkFlows;
import com.desige.webDocuments.structured.forms.BaseStructForm;
import com.desige.webDocuments.utils.ApplicationExceptionChecked;
import com.desige.webDocuments.utils.Constants;
import com.desige.webDocuments.utils.DesigeConf;
import com.desige.webDocuments.utils.ToolsHTML;
import com.desige.webDocuments.utils.beans.SuperAction;
import com.desige.webDocuments.utils.beans.Users;

/**
 * Title: LoadStructMain.java <br/>
 * Copyright: (c) 2004 Focus Consulting C.A.<br/>
 * 
 * @author Ing. Nelson Crespo (NC)
 * @version WebDocuments v3.0 <br/>
 *          Changes:<br/>
 *          <ul>
 *          <li>09/05/2004 (NC) Creation</li>
 *          </ul>
 */
public class LoadStructMain extends SuperAction {
	static Logger log = LoggerFactory.getLogger("[V4.0] "
			+ LoadStructMain.class.getName());

	/**
	 * 
	 */
	public ActionForward execute(ActionMapping actionMapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) {
		super.init(actionMapping, form, request, response);
		try {
			// en caso que este visitando la pagina de historicos, esta variable
			// tiene quie eliminarse
			removeObjectSession("history");
			String idNode = (String) ToolsHTML.getAttribute(request,
					"idNodeSelected");
			if (ToolsHTML.isEmptyOrNull(idNode)) { 
				idNode = (String) getSessionObject("nodeActive");
			}
			idNode = (idNode == null ? "1" : idNode);

			// System.out.println("idNode = " + idNode);

			log.debug("idNode = " + idNode);
			String isCopy = getParameter("isCopy");
			String movDocument = getParameter("movDocument");
			String idNodeChange = getParameter("idNodeChange");
			String idDocument = String.valueOf(getParameter("idDocument"));
			log.debug("[LoadStructMain] idDocument = " + idDocument);
			Users user = getUserSession();
			boolean showOptionPublic = HandlerStruct
					.getDocsAprovedAndNotPublicForNode(idNode).size() > 0;
			if (showOptionPublic) {
				putAtributte("showOptionPublic", "true");
			} else {
				putAtributte("showOptionPublic", "false");
			}
			Hashtable arbol = (Hashtable) getSessionObject("arbol");
			Hashtable security = (Hashtable) getSessionObject("security");
			// NC 2.006/11/17
			StringBuffer idStructs = new StringBuffer(1024).append("1");
			if (security == null) {
				security = HandlerGrupo.getAllSecurityForGroup(
						user.getIdGroup(), idStructs);
				HandlerDBUser.getAllSecurityForUser(user.getIdPerson(),
						security, idStructs);
			}
			// boolean loadMyFolder = false;
			Hashtable tree = (Hashtable) getSessionObject("tree");
			log.debug("[TREE] " + tree);
			if (arbol == null) {
				// Hashtable subNodos = new Hashtable();
				// Hashtable tree =
				// HandlerStruct.loadAllNodes(security,user.getUser(),user.getIdGroup(),subNodos,idStructs.toString());
				// putObjectSession("tree",tree);
				// putObjectSession("security",security);
				// putObjectSession("subNodos",subNodos);

				boolean isAdmon = user.getIdGroup().equalsIgnoreCase(
						DesigeConf.getProperty("application.admon"));
				Hashtable subNodos = new Hashtable();
				// Hashtable security = null;
				if (tree == null) {
					if (!isAdmon) {
						// StringBuffer idStructs = new StringBuffer(50);
						idStructs.append("1");
						security = HandlerGrupo.getAllSecurityForGroup(
								user.getIdGroup(), idStructs);
						// Se Carga la Seguridad por Usuario Filtrando aquellos
						// Nodos en Donde el Usuario no
						// puede ver Carpetas
						HandlerDBUser.getAllSecurityForUser(user.getIdPerson(),
								security, idStructs);
						tree = HandlerStruct.loadAllNodes(security,
								user.getUser(), user.getIdGroup(), subNodos,
								idStructs.toString());
						putObjectSession("security", security);
					} else {
						tree = HandlerStruct.loadAllNodes(security,
								user.getUser(), user.getIdGroup(), subNodos,
								null);
					}
					putObjectSession("tree", tree);
					putObjectSession("subNodos", subNodos);
				}
				ToolsHTML.processNode("0", tree, request.getSession());
				arbol = (Hashtable) getSessionObject("arbol");
			}

			String idLocation = HandlerStruct.getIdLocationToNode(tree, idNode);
			removeObjectSession("showCharge");
			if (idLocation != null) {
				BaseStructForm localidad = (BaseStructForm) tree
						.get(idLocation);
				if (localidad != null) {
					if (localidad.getShowCharge() == Constants.permission) {
						putObjectSession("showCharge", "true");
					}
				}
			}
			BaseStructForm forma = new BaseStructForm();
			forma.setIdNode(idNode);
			HandlerStruct.load(user.getUser(), forma);
			if (forma.getIdNode() == null) {
				idNode = "1";
				forma.setIdNode(idNode);
			}
			tree.put(forma.getIdNode().trim(), forma);

			String dataNode;
			try {
				dataNode = (String) arbol.get(idNode);
			} catch(ClassCastException e) {
				dataNode = "parent.frames['code'].menu.items[0].submenu.items[0]";
			}
			StringBuilder data = new StringBuilder(
					"if (parent.frames['code']) {")
					.append(" parent.frames['code'].refreshTree(")
					.append(dataNode).append("); }");
			String prefix = ToolsHTML.getPrefixToDoc(getSession(), user,
					forma.getIdNode());
			boolean setData = ToolsHTML.setNodes(idNode, security,
					user.getUser(), user.getIdGroup(), request, prefix);

			if (setData) {
				putObjectSession("emptyNodes", "F");
			} else {
				putObjectSession("emptyNodes", "T");
			}
			// Se Cargan los Mails enviados por el sistema no revisados por el
			// usuario
			int emails = HandlerMessages.getTotalInbox(user.getEmail());
			// Flujos de Trabajo Pendientes
			int wfPendings = HandlerWorkFlows.countAllWorkFlows(false,
					HandlerWorkFlows.pending, user.getUser(),
					HandlerWorkFlows.wfuPending, false);
			// Flujos de Trabajo Expirados
			int wfExpires = HandlerWorkFlows.countAllWorkFlows(true,
					HandlerWorkFlows.expires, user.getUser(),
					HandlerWorkFlows.wfuPending, false);
			int wfs = wfPendings + wfExpires;
			putObjectSession("wfs", new Integer(wfs));
			putObjectSession("emails", new Integer(emails));
			putObjectSession("nodeActive", idNode);
			putAtributte(request, "isCopy", isCopy);
			putAtributte(request, "movDocument", movDocument);
			putAtributte(request, "idDocument", idDocument);
			putAtributte(request, "idNodeChange", idNodeChange);
			if ("true".equalsIgnoreCase(request.getParameter("expandir"))) {
				putObjectSession("dataNode", data.toString());
			}
			return goSucces();
		} catch (ApplicationExceptionChecked ae) {
			log.debug("[ApplicationExceptionChecked]");
			ae.printStackTrace();
			return goError(ae.getKeyError());
		} catch (Exception e) {
			log.debug("[Exception]");
			e.printStackTrace();
		}
		return goError();
	}
}
