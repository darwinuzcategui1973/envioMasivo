package com.desige.webDocuments.workFlows.actions;

import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigInteger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.desige.webDocuments.utils.ToolsHTML;
import com.desige.webDocuments.utils.beans.Users;

public class ValidateUserAction extends Action {

	/**
	 * Validacion de claves para flujos de trabajo, cuando el usuario ya esta en session, no utilizar para ldap por no estar implementado 
	 * en esta rutina
	 */
	public synchronized ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {

		PrintWriter out = null;

		try {
			out = response.getWriter();
			
			Users usuario = (Users)request.getSession().getAttribute("user");
			
			if(usuario==null) {
				throw new Exception();
			}
			
			String passwd = request.getParameter("token");
			String pass = ToolsHTML.encripterPass(passwd);
			String passUser = usuario.getClave();
			// validamos el usuario y la contrasena en qwebdocuments
			
			boolean isValido = false;
			
			if(ToolsHTML.isActiveDirectoryConfigurated() && !usuario.getUser().equals("admin")) {
				isValido = ToolsHTML.isValidActiveDirectory(usuario.getUser(), passwd);
			} else {
				isValido = pass.equals(passUser); 
			}
			
			if(isValido) {
				out.print("true"); // las claves son iguales
			} else {
				out.print("false"); // las claves son diferentes
			}
			

		} catch (Exception e) {
			out.print("false");
			e.printStackTrace();
		}

		return null;
	}
	
	
}
