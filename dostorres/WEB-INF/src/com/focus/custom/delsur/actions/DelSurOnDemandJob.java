package com.focus.custom.delsur.actions;

import java.util.ResourceBundle;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.desige.webDocuments.utils.ToolsHTML;
import com.focus.custom.delsur.DelSurBatchProcess;

public class DelSurOnDemandJob extends Action {
	private static final Logger log = LoggerFactory.getLogger(DelSurOnDemandJob.class);
	
	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		// TODO Auto-generated method stub
		log.info("Se va a ejecutar el job de DelSur en demanda");
		
		DelSurBatchProcess.executeJobOnDemand();
		ResourceBundle rb = ToolsHTML.getBundle(request);
		request.setAttribute("info", rb.getString("app.editOk"));
		
		log.info("Fue ejecutado el job de DelSur en demanda");
		
		if(request.getParameter("nS") != null){
			return mapping.findForward("successNoSession");			
		} else {
			return mapping.findForward("success");
		}
	}
}
