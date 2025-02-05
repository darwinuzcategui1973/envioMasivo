package com.desige.webDocuments.sacop.actions;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.desige.webDocuments.utils.beans.SuperAction;
import com.desige.webDocuments.utils.beans.Users;

/**
 * Created by IntelliJ IDEA. User: srodriguez Date: Jan 31, 2007 Time: 10:27:36
 * AM To change this template use File | Settings | File Templates.
 */
public class VisualizarTodos extends SuperAction {
	static Logger log = LoggerFactory.getLogger(CrearSacop.class.getName());

	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {

		super.init(mapping, form, request, response);

		try {
			Users user = getUserSession();
			
			ServletContext ctx = request.getSession().getServletContext();
			String nameAtt = "viewAll_".concat(String.valueOf(user.getIdPerson()));
			if (ctx.getAttribute(nameAtt) == null) {
				ctx.setAttribute(nameAtt, "1");
				request.getSession().setAttribute("viewAll", "1");
			} else {
				ctx.removeAttribute(nameAtt);
				request.getSession().removeAttribute("viewAll");
			}

			if (request.getParameter("goTo") != null) {
				return goTo(request.getParameter("goTo"));
			}
			return goSucces();
		} catch (Exception e) {
			log.error(e.getMessage());
			e.printStackTrace();
		} finally {
		}

		return goError();
	}
}
