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
import com.desige.webDocuments.seguridad.forms.SeguridadUserForm;
import com.desige.webDocuments.structured.forms.BaseStructForm;
import com.desige.webDocuments.utils.ApplicationExceptionChecked;
import com.desige.webDocuments.utils.Constants;
import com.desige.webDocuments.utils.DesigeConf;
import com.desige.webDocuments.utils.ToolsHTML;
import com.desige.webDocuments.utils.beans.SuperAction;
import com.desige.webDocuments.utils.beans.SuperActionForm;
import com.desige.webDocuments.utils.beans.Users;

/**
 * Title: LoadAllStructAction.java <br/>
 * Copyright: (c) 2004 Focus Consulting C.A.<br/>
 * 
 * @author Ing. Nelson Crespo (NC)
 * @version WebDocuments v3.0 <br/>
 *          Changes:<br/>
 *          <ul>
 *          <li>18/04/2004 (NC) Creation</li>
 *          <li>13/07/2006 (NC) Cambios para heredar o no los Prefijos de las Carpetas</li>
 *          </ul>
 */
public class LoadAllStructAction extends SuperAction {
	static Logger log = LoggerFactory.getLogger("[V4.0] " + LoadAllStructAction.class.getName());

	/**
	 * 
	 */
	public synchronized ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
		super.init(mapping, form, request, response);
		log.debug("LoadAllStructAction.execute");

		try {
			// seteamos el modulo activo
//			if (request.getParameter("activarModulo") != null && request.getParameter("activarModulo").equals("true")) {
//				request.getSession().setAttribute(Constants.MODULO_ACTIVO, Constants.MODULO_ESTRUCTURA);
//
				removeObjectSession("vengoDeBuscar");
	//		}
//
	//		String idNode = (String) ToolsHTML.getAttribute(request, "idNodeSelected");
			Users user = getUserSession();

			Users usuario = user;
			SeguridadUserForm securityForUser = ToolsHTML.getSeguridadModuloUser(usuario);
			SeguridadUserForm securityForGroup = ToolsHTML.getSeguridadModuloGroup(usuario);

			if (request.getParameter("toSelectValue") == null) {
				if (securityForUser.getEstructura() == 0) {
					putAtributte("visible", "true");
				} else if (securityForUser.getEstructura() == 2) {
					if (securityForGroup.getEstructura() == 0) {
						putAtributte("visible", "true");
					} else {
						log.error("ACCESO ILEGAL:loadAllStruct.do user:" + usuario.getIdPerson() + " / nombre:" + usuario.getNamePerson());
						return mapping.findForward("errorNotAuthorized");
					}
				} else {
					log.error("ACCESO ILEGAL:loadAllStruct.do user:" + usuario.getIdPerson() + " / nombre:" + usuario.getNamePerson());
					return mapping.findForward("errorNotAuthorized");
				}
			}

			String cmd = getCmd(request, true);
			String idNodeRoot = HandlerStruct.getValueField("IdNode", "0");
	//		if (idNode == null) {
	//			idNode = idNodeRoot;
	//		}
			String selecIdGroup = "";
			String selecUser = "";
			long selecIdPerson;

			if (request.getParameter("selecIdGroup") == null) {
				selecIdGroup = user.getIdGroup();
				selecUser = user.getUser();
				selecIdPerson = user.getIdPerson();
			} else {
				selecIdGroup = request.getParameter("selecIdGroup").trim();
				selecUser = request.getParameter("selecUser").trim();
				Users usuarioSelec = HandlerDBUser.getUser(selecUser);
				selecIdPerson = usuarioSelec.getIdPerson();
			}
			boolean isAdmon = selecIdGroup.equalsIgnoreCase(DesigeConf.getProperty("application.admon"));
			Hashtable subNodos = new Hashtable();
			Hashtable tree = null;// new Hashtable();
			Hashtable security = null;
			if (!isAdmon) {
				StringBuffer idStructs = new StringBuffer(1024).append("1");
				security = HandlerGrupo.getAllSecurityForGroup(selecIdGroup, idStructs);
				// Se Carga la Seguridad por Usuario Filtrando aquellos Nodos en
				// Donde el Usuario no
				// puede ver Carpetas
				log.info("Iniciando HandlerStruct.loadAllNodes... as no admon");
				HandlerDBUser.getAllSecurityForUser(selecIdPerson, security, idStructs);
				tree = HandlerStruct.loadAllNodes(security, selecUser, selecIdGroup, subNodos, idStructs.toString());
				putObjectSession("security", security);
				log.info("Finalizando HandlerStruct.loadAllNodes... as no admon");
			} else {
				log.info("Iniciando HandlerStruct.loadAllNodes... as admon");
				tree = HandlerStruct.loadAllNodes(security, selecUser, selecIdGroup, subNodos, null);
				log.info("Finalizando HandlerStruct.loadAllNodes... as admon");
			}
			putObjectSession("subNodos", subNodos);
			BaseStructForm principal = (BaseStructForm) tree.get(idNodeRoot);
			if (principal == null) {
				throw new ApplicationExceptionChecked("E0020");
			}

	//		BaseStructForm folder = (BaseStructForm) tree.get(idNode);
	//		// ydavila 001-00-003243 Estructura - Delay al Crear un nodo y consultar documentos en Lista maestra
	//		if (idNode == "") {
	//			idNode = "1"; // forzo valor "1" cuando viene en blanco para traer s�lo primer nivel
		//	} // si no se forza barre la estructura/permisos completos
			log.info("Iniciando ToolsHTML.setNodes...");
		//	boolean setData = ToolsHTML.setNodes(idNode, security, selecUser, selecIdGroup, request, null, folder != null ? folder.getNodeType() : null);
			log.info("Terminando ToolsHTML.setNodes...");
/*
			if (setData) {
				putObjectSession("emptyNodes", "F");
			} else {
				putObjectSession("emptyNodes", "T");
			}
			*/
			putObjectSession("idNodeRoot", idNodeRoot);
			putObjectSession("tree", tree);
			BaseStructForm forma = (BaseStructForm) form;
			if (forma == null) {
				form = new BaseStructForm();
			}
			if (ToolsHTML.checkValue(cmd)) {
				((BaseStructForm) form).setCmd(cmd);
			} else {
				putObjectSession("cmd", SuperActionForm.cmdLoad);
			}
			int emails = HandlerMessages.getTotalInbox(user.getEmail());

			int wfPendings = HandlerWorkFlows.countAllWorkFlows(false, HandlerWorkFlows.pending, selecUser, HandlerWorkFlows.wfuPending, false);
			int wfExpires = HandlerWorkFlows.countAllWorkFlows(true, HandlerWorkFlows.expires, selecUser, HandlerWorkFlows.wfuPending, false);
			int wfs = wfPendings + wfExpires;
			putObjectSession("emails", new Integer(emails));
			putObjectSession("wfs", new Integer(wfs));
			log.info("getParameter(\"toSelectValue\") = " + getParameter("toSelectValue"));
			if (getParameter("toSelectValue") != null) {
				return goTo("selectStruct");
			}
			return goSucces();
		} catch (ApplicationExceptionChecked ae) {
			log.debug("[ApplicationExceptionChecked]");
			log.error(ae.getMessage());
			ae.printStackTrace();
			return goError(ae.getKeyError());
		} catch (Exception e) {
			log.error(e.getMessage());
			log.debug("[Exception]");
			e.printStackTrace();
		}
		return goError();
	}
}
