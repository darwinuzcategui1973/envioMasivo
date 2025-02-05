package com.desige.webDocuments.accion.actions;

import java.util.ResourceBundle;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.desige.webDocuments.accion.forms.PlanAudit;
import com.desige.webDocuments.accion.forms.ProgramAudit;
import com.desige.webDocuments.persistent.managers.HandlerDBUser;
import com.desige.webDocuments.persistent.managers.HandlerNorms;
import com.desige.webDocuments.utils.ApplicationExceptionChecked;
import com.desige.webDocuments.utils.ToolsHTML;
import com.desige.webDocuments.utils.beans.SuperAction;
import com.desige.webDocuments.utils.beans.SuperActionForm;

/**
 * Created by IntelliJ IDEA. User: Administrador Date: 20/03/2006 Time: 11:02:10
 * AM To change this template use File | Settings | File Templates.
 */

public class LoadEditPlanAuditAction extends SuperAction {
	public ActionForward execute(ActionMapping actionMapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) {
		super.init(actionMapping, form, request, response);
		try {
			ResourceBundle rb = ToolsHTML.getBundle(request);
			getUserSession();
			HandlerNorms.load((PlanAudit) form);
			removeObjectSession("valorPlan");
			putObjectSession("editTypeDocPlan", form);
			putObjectSession("typeOtroFormatoPlan", form);
			removeObjectSession("descriptPlan");

			ProgramAudit formaProgram = (ProgramAudit)getSessionObject("editTypeDoc");

			putObjectSession("normasPadrePlan",HandlerNorms.getAllNormasPrincipalesProgram(null, formaProgram.getIdNormCheck()).values());
			putObjectSession("normasPadrePlanDetalle",HandlerNorms.getAllNormasPrincipalesProgramDetalle(null, formaProgram.getIdNormCheck()).values());
			putObjectSession("usuariosPlan",HandlerDBUser.getAllUsers(null,null));
			request.setAttribute("cmd", SuperActionForm.cmdEdit);
			
			return goSucces();
		} catch (ApplicationExceptionChecked ae) {
			return goError(ae.getKeyError());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return goError();
	}

}
