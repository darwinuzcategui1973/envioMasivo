package com.desige.webDocuments.parameters.actions;

import java.util.Collection;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.desige.webDocuments.parameters.forms.BaseParametersForm;
import com.desige.webDocuments.persistent.managers.HandlerDBUser;
import com.desige.webDocuments.persistent.managers.HandlerParameters;
import com.desige.webDocuments.seguridad.forms.SeguridadUserForm;
import com.desige.webDocuments.utils.ApplicationExceptionChecked;
import com.desige.webDocuments.utils.Constants;
import com.desige.webDocuments.utils.ToolsHTML;
import com.desige.webDocuments.utils.beans.SuperAction;
import com.desige.webDocuments.utils.beans.Users;

/**
 * Title: LoadParametersAction.java <br/> Copyright: (c) 2004 Focus Consulting
 * C.A.<br/>
 * 
 * @author Ing. Nelson Crespo (NC)
 * @version WebDocuments v3.0 <br/> Changes:<br/>
 *          <ul>
 *          <li>20/05/2004 (NC) Creation </li>
 *          </ul>
 */
public class AdministracionAction extends SuperAction {
	private static Logger log = LoggerFactory.getLogger(AdministracionAction.class.getName());

	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {

		super.init(mapping, form, httpServletRequest, httpServletResponse);

        // seteamos el modulo activo
		httpServletRequest.getSession().setAttribute(Constants.MODULO_ACTIVO, Constants.MODULO_ADMINISTRACION);
		
		BaseParametersForm forma = (BaseParametersForm) form;
		if (forma == null) {
			forma = new BaseParametersForm();
		}
		try {
			Users usuario = getUserSession();
			SeguridadUserForm securityForUser = ToolsHTML.getSeguridadModuloUser(usuario);
			SeguridadUserForm securityForGroup = ToolsHTML.getSeguridadModuloGroup(usuario);

			Collection allUsers = HandlerDBUser.getAllUsers();
			putObjectSession("allUsers",allUsers);
			
			if (securityForUser.getAdministracion() == 0) {
				putAtributte("visible", "true");
			} else if (securityForUser.getAdministracion() == 2) {
				if (securityForGroup.getAdministracion() == 0) {
					putAtributte("visible", "true");
				} else {
					log.error("ACCESO ILEGAL:administracion.do user:"+usuario.getIdPerson()+" / nombre:"+usuario.getNamePerson());
					return mapping.findForward("errorNotAuthorized");
				}
			} else {
				log.error("ACCESO ILEGAL:administracion.do user:"+usuario.getIdPerson()+" / nombre:"+usuario.getNamePerson());
				return mapping.findForward("errorNotAuthorized");
			}
			
			putObjectSession("idNormAudit",HandlerParameters.PARAMETROS.getIdNormAudit());

			return goSucces();
		} catch (ApplicationExceptionChecked ae) {
			ae.printStackTrace();
			return goError(ae.getKeyError());
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return goError();
	}
}
