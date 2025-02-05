package com.desige.webDocuments.workFlows.actions;

import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.desige.webDocuments.norms.forms.BaseNormsForm;
import com.desige.webDocuments.persistent.managers.HandlerNorms;

public class UpdateNormAction extends Action {

	public synchronized ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {

		PrintWriter out = null;
		StringBuffer resp = new StringBuffer();
		String nameAttribute = "";
		boolean isCorrect = false;
		try {
			out = response.getWriter();
			
			if(request.getParameter("documentRequired")==null && request.getParameter("auditProcess")==null) {
				throw new Exception("El parametro no es correcto.");
			}

			String idNorm = request.getParameter("idNorm");
			int activo = 0;
			int newActivo = activo==0?1:0;
			
			if(request.getParameter("documentRequired")!=null) {
				nameAttribute = "documentRequired";
			} else if(request.getParameter("auditProcess")!=null) {
				nameAttribute = "auditProcess";
			}
			
			activo = Integer.parseInt(request.getParameter(nameAttribute));
			newActivo = (activo==0?1:0);
			BaseNormsForm forma = new BaseNormsForm();
			
			forma.setId(idNorm);
			
			if(request.getParameter("documentRequired")!=null) {
				forma.setDocumentRequired(newActivo);
				isCorrect = HandlerNorms.documentRequired(forma);
			} else if(request.getParameter("auditProcess")!=null) {
				forma.setAuditProcess(newActivo);
				isCorrect = HandlerNorms.auditProcess(forma);
			}
			
			resp.append("{");
			resp.append("success:'true',");
			resp.append("idNorm:'").append(idNorm).append("',");
			resp.append(nameAttribute).append(":'").append(isCorrect?newActivo:activo).append("'");
			resp.append("}");
			
		} catch (Exception e) {
			resp.append("{");
			resp.append("success:'false'");
			resp.append("}");
		} 

		out.print(resp.toString());
		return null;
	}
	
	
}
