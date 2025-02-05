package com.desige.webDocuments.seguridad.actions;

import java.util.Hashtable;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.desige.webDocuments.persistent.managers.HandlerDBUser;
import com.desige.webDocuments.persistent.managers.HandlerDocuments;
import com.desige.webDocuments.persistent.managers.HandlerGrupo;
import com.desige.webDocuments.persistent.managers.HandlerStruct;
import com.desige.webDocuments.seguridad.forms.PermissionUserForm;
import com.desige.webDocuments.structured.forms.BaseStructForm;
import com.desige.webDocuments.utils.ApplicationExceptionChecked;
import com.desige.webDocuments.utils.Constants;
import com.desige.webDocuments.utils.DesigeConf;
import com.desige.webDocuments.utils.ToolsHTML;
import com.desige.webDocuments.utils.beans.SuperAction;
import com.desige.webDocuments.utils.beans.Users;
import com.focus.qweb.facade.AuditFacade;
import com.focus.qweb.to.AuditTO;
import com.focus.qweb.to.TransactionTO;

/**
 * Title: EditSecurityStructUserAction.java <br/>
 * Copyright: (c) 2004 Focus Consulting C.A.<br/>
 * 
 * @author Ing. Nelson Crespo
 * @version WebDocuments v3.0 <br/>
 *          Changes:<br/>
 *          <ul>
 *          <li>15/12/2004 (NC) Creation</li>
 *          <li>29/05/2005 (NC) Cambios en el manejo de la Seguridad</li>
 *          <li>27/04/2006 (NC) Nueva llamada para actualizar la Seguridad</li>
 *          <li>30/06/2006 (NC) uso del Log</li>
 *          </ul>
 */
public class EditSecurityStructUserAction extends SuperAction {
	static Logger log = LoggerFactory.getLogger(EditSecurityStructUserAction.class);

	/**
	 * 
	 */
	public ActionForward execute(ActionMapping actionMapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) {
		super.init(actionMapping, form, request, response);
		try {
			String rout = getParameter("rout");
			Users user = getUserSession();
			PermissionUserForm forma = (PermissionUserForm) form;

			boolean statu = false;
			String idNodeSelected = getParameter("idNodeSelected");
			String idDocument = getParameter("idDocument");
			String command = getParameter("command");
			if (forma.getCommand().equalsIgnoreCase(Constants.cmdToStruct)) {
				// Actualiza la seguridad del documento por grupo

				Hashtable tree = (Hashtable) getSessionObject("tree");
				// obtenemos todos los nodos dependiendo de la permisologia del
				// grupo y del usuario al que pertenece dicho usuario
				Hashtable subNodos = new Hashtable();
				tree = ToolsHTML.checkTree(tree, user, subNodos);
				BaseStructForm carpeta = (BaseStructForm) tree.get(forma
						.getIdStruct());

				// actualizamos la seguidad del usuario
				// // statu =
				// HandlerDBUser.updateSecurityStructUser(forma,isFolderOrProcess,isSite);
				statu = HandlerDBUser.updateSecurityStructUser(forma,
						carpeta.getNodeType(), user.getUser());
				boolean isAdmon = user.getIdGroup().equalsIgnoreCase(
						DesigeConf.getProperty("application.admon"));
				Hashtable security = null;
				if (!isAdmon) {
					// obtenemos la seguridad del grupo
					StringBuffer idStructs = new StringBuffer(50);
					idStructs.append("1");
					security = HandlerGrupo.getAllSecurityForGroup(
							user.getIdGroup(), idStructs);
					// obtenemos la seguridad del usuario
					HandlerDBUser.getAllSecurityForUser(user.getIdPerson(),
							security, idStructs);
					// obtenemos todos los nodos dependiendo de la permisologia
					// del grupo y del usuario al que pertenece dicho usuario
					tree = HandlerStruct.loadAllNodes(security, user.getUser(),
							user.getIdGroup(), subNodos, idStructs.toString());
				} else {
					tree = HandlerStruct.loadAllNodes(security, user.getUser(),
							user.getIdGroup(), subNodos, null);
				}
			} else {
				// Actualiza la seguridad del documento por usuario

				statu = HandlerDocuments
						.updateSecurityDocumentUser(forma, true);
				String nameDocument = getParameter("nameDocument");
				if (!ToolsHTML.isEmptyOrNull(nameDocument)) {
					putAtributte("nameDocument", nameDocument);
				}
				AuditFacade.insertarAuditFacade(ToolsHTML.getBundle(request), TransactionTO.PERMISOS_USUARIO_SOBRE_DOCUENTO, "", user.getIdPerson(),user.getNameUser(), "", "", AuditTO.getClientIpAddress(request) );
			}
			if (statu) {
				try {
					ActionForward toOk = actionMapping.findForward("success");

					putObjectSession("info", getMessage("seguridad.editOk"));
					StringBuffer parameters = new StringBuffer(80);
					parameters.append(toOk.getPath())
							.append("?idNodeSelected=").append(idNodeSelected);
					parameters.append("&idDocument=").append(idDocument)
							.append("&command=").append(command);
					log.debug(parameters.toString());

					// toOk.setPath(parameters.toString()); // esto da error ya
					// que el action es compartido por hilos y no se puede
					// modificar

					return new ActionForward(parameters.toString());
				} catch (Exception ex) {
					ex.printStackTrace();
				}
				if (!ToolsHTML.isEmptyOrNull(rout)) {
					putObjectSession("rout", rout);
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
