package com.desige.webDocuments.norms.actions;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.desige.webDocuments.persistent.managers.HandlerNorms;
import com.desige.webDocuments.utils.beans.SuperAction;

/**
 * 
 * @author frojas
 * 
 */
public class LoadNormsToPopUp extends SuperAction {
	private static final Logger log = LoggerFactory.getLogger(LoadNormsToPopUp.class);

	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) {
		try {
			String selected = request.getParameter("selected");
			request.setAttribute("allNorms", HandlerNorms.getSelectedNorms(selected, false));	//HandlerNorms.getAllNorms()
			request.setAttribute("norms1", HandlerNorms.getSelectedNorms(selected, true));
			request.setAttribute("selected",selected );
			request.setAttribute("hiddenField", request.getParameter("field"));

			log.info("Colocadas las normas en el request");
			log.info("selected='" + request.getParameter("selected") + "'");
			log.info("hiddenField='" + request.getParameter("field") + "'");
		} catch (Exception e) {
			log.error("Error: " + e.getLocalizedMessage(), e);
		}

		log.info("Retornando con mapping 'succes'");
		return mapping.findForward("success");
	}
}
