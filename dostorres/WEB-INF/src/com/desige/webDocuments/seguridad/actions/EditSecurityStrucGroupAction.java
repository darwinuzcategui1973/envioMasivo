package com.desige.webDocuments.seguridad.actions;

import java.util.Hashtable;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.desige.webDocuments.persistent.managers.HandlerDocuments;
import com.desige.webDocuments.persistent.managers.HandlerGrupo;
import com.desige.webDocuments.seguridad.forms.PermissionUserForm;
import com.desige.webDocuments.structured.forms.BaseStructForm;
import com.desige.webDocuments.utils.ApplicationExceptionChecked;
import com.desige.webDocuments.utils.Constants;
import com.desige.webDocuments.utils.ToolsHTML;
import com.desige.webDocuments.utils.beans.SuperAction;
import com.desige.webDocuments.utils.beans.Users;
import com.focus.qweb.facade.AuditFacade;
import com.focus.qweb.to.AuditTO;
import com.focus.qweb.to.TransactionTO;

/**
 * Title: EditSecurityStrucGroupAction.java <br/> Copyright: (c) 2004 Focus Consulting C.A.<br/>
 * 
 * @author Ing. Nelson Crespo
 * @version WebDocuments v3.0 <br/> Changes:<br/>
 *          <ul>
 *          <li> 21/12/2004 (NC) Creation </li>
 *          <li> 29/05/2005 (NC) Cambios en el manejo de la Seguridad </li>
 *          <li> 27/04/2006 (NC) Nueva llamada para actualizar la Seguridad </li>
 *          <li> 30/06/2006 (NC) Uso del Log </li>
 *          </ul>
 */
public class EditSecurityStrucGroupAction extends SuperAction {
	static Logger log = LoggerFactory.getLogger(EditSecurityStrucGroupAction.class);

	public ActionForward execute(ActionMapping actionMapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
		super.init(actionMapping, form, request, response);
		try {
			Users user = getUserSession();
			PermissionUserForm forma = (PermissionUserForm) form;
			boolean statu = false;
			String idNodeSelected = getParameter("idNodeSelected");
			String idDocument = getParameter("idDocument");
			String command = getParameter("command");
			// String nodeType = getParameter("nodeType");
			log.debug("idDocument = " + idDocument);
			log.debug("idNodeSelected = " + idNodeSelected);
			//forma.setPermisoModificado(HandlerStruct.);

			
			if (Constants.cmdToStruct.equalsIgnoreCase(forma.getCommand())) {
				/* Actualiza la seguridad de la estructura por grupo */
				Hashtable tree = (Hashtable) getSessionObject("tree");
				tree = ToolsHTML.checkTree(tree, user);
				BaseStructForm carpeta = (BaseStructForm) tree.get(forma.getIdStruct());
				boolean isFolderOrProcess = false;
				boolean isSite = false;
				statu = HandlerGrupo.updateSecurityStructGroup(forma, carpeta.getNodeType());
			} else {
				/* Actualiza la seguridad por grupo del documento por usuario */
				statu = HandlerDocuments.updateSecurityDocumentGroup(forma, true);
				String nameDocument = getParameter("nameDocument");
				if (!ToolsHTML.isEmptyOrNull(nameDocument)) {
					putAtributte("nameDocument", nameDocument);
				}
				AuditFacade.insertarAuditFacade(ToolsHTML.getBundle(request), TransactionTO.PERMISOS_GRUPOS_SOBRE_DOCUMENTO, "", user.getIdPerson(),user.getNameUser() , "", "", AuditTO.getClientIpAddress(request) );
			}
			if (statu) {
				ActionForward toOk = actionMapping.findForward("success");
				if (toOk != null) {
					log.debug("toOk.getPath() = " + toOk.getPath());
				}
				putObjectSession("info", getMessage("seguridad.editOk"));
				StringBuffer parameters = new StringBuffer(60);
				parameters.append(toOk.getPath()).append("?idNodeSelected=").append(idNodeSelected);
				parameters.append("&idDocument=").append(idDocument).append("&command=").append(command);
				try {
					ActionForward actionForward = new ActionForward();
					actionForward.setPath(parameters.toString());
					return actionForward;
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
