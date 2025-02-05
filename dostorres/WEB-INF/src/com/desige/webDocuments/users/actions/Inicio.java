package com.desige.webDocuments.users.actions;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.desige.webDocuments.utils.Constants;
import com.focus.util.ContextBean;

/**
 * Title: Inicio.java <br/>
 * Copyright: (c) 2004 Focus Consulting C.A.<br/>
 * 
 * @author Ing. Nelson Crespo (NC)
 * @version WebDocuments v3.0 <br/>
 *          Changes:<br/>
 *          <ul>
 *          <li>23/03/2004 (NC) Creation</li>
 *          </ul>
 */

public class Inicio extends Action {

	/**
	 * 
	 */
	public ActionForward execute(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {

		// esta variable, solo se usa en activeFactory...
		// para que veal el documento sin pedir usuario y password

		if (!Constants.JDBC_CONNECTION
				|| (request.getParameter("config") != null && request
						.getParameter("config").equals("true"))){
			// si no hay conexion con
			// pedimos los datos del context.xml actual
			ContextBean context = new ContextBean(request);
			context.load();

			request.setAttribute("context", context);

			return mapping.findForward("configure");
		} else {
			String usuario = request.getParameter("usuarioActFactory") != null ? request
					.getParameter("usuarioActFactory") : "";
			String password = request.getParameter("passwordActFactory") != null ? request
					.getParameter("passwordActFactory") : "";
			request.setAttribute("usuarioActFactory", usuario);
			request.setAttribute("passwordActFactory", password);
			return mapping.findForward("home");
		}
	}

}