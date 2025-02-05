package com.desige.webDocuments.record.actions;

import java.util.Collection;
import java.util.ResourceBundle;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.desige.webDocuments.persistent.managers.HandlerDBUser;
import com.desige.webDocuments.persistent.managers.HandlerProcesosSacop;
import com.desige.webDocuments.record.forms.RecordFiltersForm;
import com.desige.webDocuments.seguridad.forms.PermissionUserForm;
import com.desige.webDocuments.seguridad.forms.SeguridadUserForm;
import com.desige.webDocuments.utils.ApplicationExceptionChecked;
import com.desige.webDocuments.utils.Constants;
import com.desige.webDocuments.utils.ToolsHTML;
import com.desige.webDocuments.utils.beans.SuperAction;
import com.desige.webDocuments.utils.beans.Users;

/**
 * Title: MarkFiltersRecordAction.java<br>
 * Copyright: (c) 2007 Focus Consulting<br>
 * Company:Focus Consulting (CON)<br>
 * @author YSA
 * <br>
 * Changes:<br>
 * <ul>
 *      <li> 10/12/2007 (YSA) Creation </li>
 * <ul>
 */

public class MarkFiltersRecordAction extends SuperAction {
    public ActionForward execute(ActionMapping mapping,
                                 ActionForm form, HttpServletRequest request,
                                 HttpServletResponse response) {
    	super.init(mapping,form,request,response);

        // seteamos el modulo activo
//        request.getSession().setAttribute(Constants.MODULO_ACTIVO, Constants.MODULO_ESTADISTICAS);

        try{
        	ResourceBundle rb = ToolsHTML.getBundle(request);
            Users usuario = getUserSession();
            
            
			SeguridadUserForm securityForUser = ToolsHTML.getSeguridadModuloUser(usuario);
			SeguridadUserForm securityForGroup = ToolsHTML.getSeguridadModuloGroup(usuario);

			if (securityForUser.getRecord() == 0) {
				putAtributte("visible", "true");
			} else if (securityForUser.getRecord() == 2) {
				if (securityForGroup.getRecord() == 0) {
					putAtributte("visible", "true");
				} else {
					return mapping.findForward("errorNotAuthorized");
				}
			} else {
				return mapping.findForward("errorNotAuthorized");
			}

			
	        RecordFiltersForm forma = new RecordFiltersForm();
	        request.getSession().removeAttribute("recordForm");
	        request.getSession().setAttribute("recordForm",forma);
            
            Collection areas = HandlerProcesosSacop.getArea(null);
            Collection cargos = HandlerDBUser.getCargosConAreas(null);

			PermissionUserForm security = HandlerDBUser.getAllSecurityRecord(usuario.getIdPerson());

	        request.getSession().removeAttribute("areas");
	        request.getSession().removeAttribute("cargos");
			
			request.getSession().setAttribute("areas",areas);
			request.getSession().setAttribute("cargos",cargos);
            
            // para el request
	        request.getSession().removeAttribute("securityRecord");
			request.getSession().setAttribute("securityRecord",security);

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
