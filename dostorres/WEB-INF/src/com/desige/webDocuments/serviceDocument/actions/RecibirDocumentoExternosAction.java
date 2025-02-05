package com.desige.webDocuments.serviceDocument.actions;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.desige.webDocuments.serviceDocument.request.DocumentoExternosRequest;
import com.desige.webDocuments.utils.ToolsHTML;
import com.focus.qwds.ondemand.server.documentos.servicios.ServicioDocumento;
import com.google.gson.Gson;


public class RecibirDocumentoExternosAction extends Action {
	
	public synchronized ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {

		Gson gson = new Gson();
		ServicioDocumento servicioDocumento = null;

		try {
			ToolsHTML.getUserToken(request);
			
			servicioDocumento = new ServicioDocumento();
			
			DocumentoExternosRequest docReqExte = gson.fromJson(ToolsHTML.getBody(request), DocumentoExternosRequest.class);
			
			servicioDocumento.recibirDocumentoExternos( docReqExte.getArchivo(),docReqExte.getNombreArchivo(),docReqExte.getEmailUsuarioRecibe(), docReqExte.getDescript(), docReqExte.getPalabraClave(), docReqExte.getExtension() );
					
			ToolsHTML.sendResponse(response, HttpServletResponse.SC_OK, "true");
			
		} catch(Exception e) {
			
			// ToolsHTML.sendResponse(response, HttpServletResponse.SC_FORBIDDEN, "false");
			ToolsHTML.sendResponse(response, HttpServletResponse.SC_FORBIDDEN, e.getMessage());
			e.printStackTrace();
		}
		
		return null;
	}

	
}
