package com.desige.webDocuments.document.actions;

import java.util.Collection;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.desige.webDocuments.persistent.managers.HandlerDBUser;
import com.desige.webDocuments.utils.ApplicationExceptionChecked;
import com.desige.webDocuments.utils.beans.SuperAction;

/**
 * Created by IntelliJ IDEA. User: lcisneros Date: Mar 26, 2007 Time: 9:57:47 AM
 * To change this template use File | Settings | File Templates.
 */
public class FiltroUsuariosSession extends SuperAction {

	public synchronized ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request,
			HttpServletResponse response) {
		super.init(mapping, form, request, response);

		String apellidoNombre = request.getParameter("apellidoNombre");
		String nombreCargo = request.getParameter("nombreCargo");
		System.out.println("apellidoNombre =" + String.valueOf(apellidoNombre));
		System.out.println("nombreCargo =" + String.valueOf(nombreCargo));
		try {
			
			Collection usuarios = null;
			
			if(nombreCargo!=null) {
				usuarios = HandlerDBUser.getAllUsersByCargo(null, nombreCargo);
			} else {
				usuarios = HandlerDBUser.getAllUsers(null, apellidoNombre);
			}

			if (usuarios.size() == 0) {
				// si la busqueda no trabjo nada, busco todos y le mando un msg.
				request.setAttribute("noHayUsuarios", new Boolean(true));
				usuarios = HandlerDBUser.getAllUsers(null, null);
			}
			putObjectSession("usuarios", usuarios);
			putObjectSession("size", String.valueOf(usuarios.size()));

			return goSucces();
		} catch (ApplicationExceptionChecked ae) {
			return goError(ae.getKeyError());
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return goError("E0052");
	}
}
