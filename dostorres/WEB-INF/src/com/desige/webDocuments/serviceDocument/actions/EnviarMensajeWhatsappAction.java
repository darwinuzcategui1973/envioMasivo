package com.desige.webDocuments.serviceDocument.actions;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.http.client.HttpClient;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.desige.webDocuments.serviceDocument.request.WhatsAppRequest;
import com.desige.webDocuments.utils.ToolsHTML;
import com.focus.qwds.ondemand.server.documentos.servicios.ServicioDocumento;
import com.google.gson.Gson;


public class EnviarMensajeWhatsappAction extends Action {
	
	public synchronized ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {

		Gson gson = new Gson();
		ServicioDocumento servicioDocumento = null;
		HttpClient request28 = null;
		try {
			 //ToolsHTML.getUserToken(request);
			
			servicioDocumento = new ServicioDocumento();
			
			WhatsAppRequest whatsAppReq = gson.fromJson(ToolsHTML.getBody(request), WhatsAppRequest.class);
			//DocumentoExternosRequest docReqExte = gson.fromJson(ToolsHTML.getBody(request), DocumentoExternosRequest.class);
			
			 servicioDocumento.enviarMensajeWhatsapp( whatsAppReq.getMensaje(), whatsAppReq.getTelefono() );
			//request28.getConnectionManager();
			//((Object) request28).getPost("https://gate.whapi.cloud/messages/text");
			//request
			//HttpServletRequest request = new HttpServletRequest(this.)
			//HttpWebRequest request = new HttpClientWebRequest(this.simpleClientConnectionManager);
			 System.out.println("------------------servicioDocumento----------------");
				System.out.println(servicioDocumento);
				System.out.println("------------------servicioDocumento----------------");	
			ToolsHTML.sendResponse(response, HttpServletResponse.SC_OK, "true");
			
		} catch(Exception e) {
			
			// ToolsHTML.sendResponse(response, HttpServletResponse.SC_FORBIDDEN, "false");
			ToolsHTML.sendResponse(response, HttpServletResponse.SC_FORBIDDEN, e.getMessage());
			e.printStackTrace();
		}
		
		return null;
	}

	
}
