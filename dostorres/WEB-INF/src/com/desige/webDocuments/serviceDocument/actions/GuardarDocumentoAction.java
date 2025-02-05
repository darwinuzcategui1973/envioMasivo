package com.desige.webDocuments.serviceDocument.actions;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.desige.webDocuments.serviceDocument.request.DocumentoRequest;
import com.desige.webDocuments.utils.ToolsHTML;
import com.focus.qwds.ondemand.server.documentos.entidades.Documento;
import com.focus.qwds.ondemand.server.documentos.servicios.ServicioDocumento;
import com.focus.qwds.ondemand.server.usuario.entidades.Usuario;
import com.google.gson.Gson;

public class GuardarDocumentoAction extends Action { 

	public synchronized ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {

		Gson gson = new Gson();
		ServicioDocumento servicioDocumento = null;

		try {
			ToolsHTML.getUserToken(request);
			
			servicioDocumento = new ServicioDocumento();
			
			DocumentoRequest docReq = gson.fromJson(ToolsHTML.getBody(request), DocumentoRequest.class);
			
			servicioDocumento.guardarDocumento(docReq.getArchivo(), docReq.getIdCheckOut(), docReq.isFileView());
			
			ToolsHTML.sendResponse(response, HttpServletResponse.SC_OK, "true");
		} catch(Exception e) {
			ToolsHTML.sendResponse(response, HttpServletResponse.SC_FORBIDDEN, "false");
			e.printStackTrace();
		}
		
		return null;
	}

	
}
