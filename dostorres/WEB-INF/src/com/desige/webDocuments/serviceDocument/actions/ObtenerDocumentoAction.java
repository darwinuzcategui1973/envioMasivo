package com.desige.webDocuments.serviceDocument.actions;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.desige.webDocuments.serviceDocument.request.CheckOutRequest;
import com.desige.webDocuments.utils.ToolsHTML;
import com.focus.qwds.ondemand.server.documentos.servicios.ServicioDocumento;
import com.google.gson.Gson;

public class ObtenerDocumentoAction extends Action { 

	public synchronized ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {

		Gson gson = new Gson();
		ServicioDocumento servicioDocumento = null;

		try {
			ToolsHTML.getUserToken(request);
			
			servicioDocumento = new ServicioDocumento();
			
			CheckOutRequest checkOutRequest = gson.fromJson(ToolsHTML.getBody(request), CheckOutRequest.class);

			byte[] bytesDocumento = servicioDocumento.obtenerDocumento(checkOutRequest.getIdCheckOut());
			
			ToolsHTML.sendResponse(response, HttpServletResponse.SC_OK, gson.toJson(bytesDocumento));
		} catch(Exception e) {
			ToolsHTML.sendResponse(response, HttpServletResponse.SC_FORBIDDEN, null);
			e.printStackTrace();
		}
		
		return null;
	}

	
}
