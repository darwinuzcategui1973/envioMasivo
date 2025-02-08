package com.desige.webDocuments.parameters.actions;

import java.util.Collection;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.desige.webDocuments.parameters.forms.BaseParametersForm;
import com.desige.webDocuments.persistent.managers.HandlerDBUser;
import com.desige.webDocuments.persistent.managers.HandlerIdiomas;
import com.desige.webDocuments.persistent.managers.HandlerNorms;
import com.desige.webDocuments.persistent.managers.HandlerParameters;
import com.desige.webDocuments.utils.ApplicationExceptionChecked;
import com.desige.webDocuments.utils.beans.SuperAction;

/**
 * Title: LoadParametersAction.java <br/> Copyright: (c) 2004 Focus Consulting C.A.<br/>
 * 
 * @author Ing. Nelson Crespo (NC)
 * @version WebDocuments v3.0 <br/> Changes:<br/>
 *          <ul>
 *          <li>20/05/2004 (NC) Creation </li>
 *          </ul>
 */
public class LoadParametersAction extends SuperAction {
	public ActionForward execute(ActionMapping actionMapping, ActionForm form, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {
		
		super.init(actionMapping, form, httpServletRequest, httpServletResponse);
		
		BaseParametersForm forma = (BaseParametersForm) form;
		if (forma == null) {
			forma = new BaseParametersForm();
		}
		try {
			getUserSession();
			HandlerParameters.load(forma);
			Collection idiomas = HandlerIdiomas.getAllLanguage();
			if (idiomas != null) {
				putAtributte("idiomas", idiomas);
			}
			
			// necesitamos la lista de usuarios
			Collection listUsers = HandlerDBUser.getListUserSelect();
			putAtributte("listUserAddressee", listUsers);
			//putAtributte("normasPadre",HandlerNorms.getAllNormasPrincipales(null).values());
			
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
