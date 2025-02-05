package com.desige.webDocuments.workFlows.actions;

import java.util.Collection;
import java.util.Collections;
import java.util.Hashtable;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.desige.webDocuments.document.forms.DocumentsCheckOutsBean;
import com.desige.webDocuments.persistent.managers.HandlerDocuments;
import com.desige.webDocuments.utils.ApplicationExceptionChecked;
import com.desige.webDocuments.utils.Constants;
import com.desige.webDocuments.utils.beans.SuperAction;
import com.desige.webDocuments.utils.beans.Users;
import com.focus.qweb.to.PlanAuditTO;
import com.focus.qweb.to.ProgramAuditTO;
import com.focus.util.PerfilAdministrador;

/**
 * Title: LoadWorkFlowsAction.java<br>
 * Copyright: (c) 2004 Focus Consulting C.A.<br/>
 * 
 * @author Ing. Nelson Crespo (NC)
 * @author Ing. Simón Rodriguéz (SR)
 * @version WebDocuments v3.0 <br>
 *          Changes:<br>
 *          <ul>
 *          <li>06-09-2004 (NC) Creation</li>
 *          <li>03/08/2005 (SR) Se valida la variable primeravez, en caso de que sea la primeravez que entra el usuario para informarle que cambie su password
 *          por seguridad.</li>
 *          <ul>
 */
public class LoadDocumentRequiredAction extends SuperAction {
	static Logger log = LoggerFactory.getLogger("[V4.0] " + LoadDocumentRequiredAction.class.getName());

	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {

		try {
			super.init(mapping, form, request, response);
			Hashtable nodos = new Hashtable();

			Collection<DocumentsCheckOutsBean> docRequired = null;
			ProgramAuditTO oProgramAuditTO= new ProgramAuditTO();
			PlanAuditTO oPlanAuditTO= new PlanAuditTO();
			

			if (request.getParameter("tipoConsulta") != null) {
				if (request.getParameter("tipoConsulta").equals("programa")) {
					log.debug("Cargando documents Pendientes en las Normas por program....");
					oProgramAuditTO.setIdProgramAudit(request.getParameter("idProgram"));
					oPlanAuditTO.setIdPlanAudit(request.getParameter("idPlan"));
					
					docRequired = HandlerDocuments.getAllDocumentsCheckOutsRequiredUser(null, nodos, 1,	oProgramAuditTO, oPlanAuditTO);
					
					request.setAttribute("programAuditTO", oProgramAuditTO);
					request.setAttribute("planAuditTO", oPlanAuditTO);

				} else if (request.getParameter("tipoConsulta").equals("plan")) {
					log.debug("Cargando documents Pendientes en las Normas por plan....");
					oPlanAuditTO.setIdPlanAudit(request.getParameter("idPlan"));
					
					docRequired = HandlerDocuments.getAllDocumentsCheckOutsRequiredUser(null, nodos, 2,	oProgramAuditTO, oPlanAuditTO);

					request.setAttribute("planAuditTO", oPlanAuditTO);
				}
			} else {
				log.debug("Cargando documents Pendientes en las Normas....");
				docRequired = HandlerDocuments.getAllDocumentsCheckOutsRequiredUser(null, nodos, 0, null, null);
			}


			request.setAttribute("docRequired", docRequired);

			log.debug("Retornando....");
			return goSucces();
		} catch (ApplicationExceptionChecked ae) {
			return goError(ae.getKeyError());
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return goError();
	}

}
