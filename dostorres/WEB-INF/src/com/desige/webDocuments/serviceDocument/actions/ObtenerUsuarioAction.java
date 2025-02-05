package com.desige.webDocuments.serviceDocument.actions;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.desige.webDocuments.persistent.managers.HandlerDBUser;
import com.desige.webDocuments.serviceDocument.request.UsuarioRequest;
import com.desige.webDocuments.users.forms.LoginUser;
import com.desige.webDocuments.utils.Constants;
import com.desige.webDocuments.utils.ToolsHTML;
import com.desige.webDocuments.utils.beans.Users;
import com.focus.qwds.ondemand.cliente.utils.Encryptor;
import com.focus.qwds.ondemand.server.documentos.servicios.ServicioDocumento;
import com.focus.qwds.ondemand.server.usuario.entidades.Usuario;
import com.google.gson.Gson;


public class ObtenerUsuarioAction extends Action { 

	public synchronized ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {

		Gson gson = new Gson();
		ServicioDocumento servicioDocumento = null;
		

		try {
			servicioDocumento = new ServicioDocumento();
			
			UsuarioRequest usuarioRequest = gson.fromJson(ToolsHTML.getBody(request), UsuarioRequest.class);
			
			Users usuario = new Users();
			usuario.setUser(usuarioRequest.getUsuario());
			usuario.setPass(usuarioRequest.getClave());
			
			LoginUser login = new LoginUser();
			login.setUser(usuarioRequest.getUsuario());
			login.setClave(usuarioRequest.getClave());
			
			
			if(ToolsHTML.isActiveDirectoryConfigurated()) {
				if(!HandlerDBUser.checkUser(usuario, login, true, request)) {
					throw new Exception("Datos de acceso incorrectos");
				}
			} else {
				usuario.setUser(usuarioRequest.getUsuario());
				usuario.setClave(usuarioRequest.getClave());
			}
			
			Usuario user = servicioDocumento.obtenerUsuario(usuario.getClave(), usuario.getUser());
			
			user.setToken(Encryptor.generateKey());
			
			Constants.TOKEN_USUARIOS_ONDEMAND.put(user.getToken(),user);
			ToolsHTML.sendResponse(response, HttpServletResponse.SC_OK, gson.toJson(user));
		} catch(Exception e) {
			ToolsHTML.sendResponse(response, HttpServletResponse.SC_FORBIDDEN, null);
			e.printStackTrace();
		}
		
		return null;
	}

	
}
