package com.desige.webDocuments.record.actions;

import java.io.PrintWriter;
import java.util.Collection;
import java.util.Iterator;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.desige.webDocuments.persistent.managers.HandlerDBUser;
import com.desige.webDocuments.utils.ApplicationExceptionChecked;
import com.desige.webDocuments.utils.beans.Search;
import com.desige.webDocuments.utils.beans.Users;

/**
 * Title: MarkFiltersAjaxAction.java<br>
 * Copyright: (c) 2007 Focus Consulting<br>
 * Company:Focus Consulting (CON)<br>
 * 
 * @author YSA <br>
 *         Changes:<br>
 *         <ul>
 *         <li>17/12/2007 (YSA) Creation</li>
 *         <ul>
 */

public class MarkFiltersAjaxAction extends Action {
	private static final String CONTENT_TYPE = "text/xml";

	public synchronized ActionForward execute(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) {

		try {
			Users usuario = (Users) request.getSession().getAttribute("user");
			if (usuario == null
					|| !HandlerDBUser.isValidSessionUser(usuario.getUser(),
							request.getSession())) {
				throw new ApplicationExceptionChecked("E0035");
			}
			response.setContentType(CONTENT_TYPE);
			PrintWriter out = response.getWriter();

			String schCharges = request.getParameter("schCharges");
			String areas = request.getParameter("areas");

			if (schCharges != null && schCharges.equals("true")
					&& areas != null) {
				Collection cargos = HandlerDBUser.getCargosConAreas(areas);
				if (cargos != null) {
					String areaOld = "";
					out.println("<?xml version=\"1.0\"?>");
					out.println("<cargos>");
					for (Iterator iter = cargos.iterator(); iter.hasNext();) {
						Search cargo = (Search) iter.next();
						String id = cargo.getId();
						String desc = cargo.getDescript();
						String area = cargo.getAditionalInfo();
						if (!areaOld.equals(area)) {
							areaOld = area;
							area = area.replace("�", "a");
							area = area.replace("�", "e");
							area = area.replace("�", "i");
							area = area.replace("�", "o");
							area = area.replace("�", "u");
							area = area.replace("�", "A");
							area = area.replace("�", "E");
							area = area.replace("�", "I");
							area = area.replace("�", "O");
							area = area.replace("�", "U");
							area = area.replace("�", "n");
							area = area.replace("�", "N");
							area = area.replace("\\^w", " ");
							out.println("<cargo id=\"\" >--- " + area
									+ " ---</cargo>");
						}
						desc = desc.replace("�", "a");
						desc = desc.replace("�", "e");
						desc = desc.replace("�", "i");
						desc = desc.replace("�", "o");
						desc = desc.replace("�", "u");
						desc = desc.replace("�", "A");
						desc = desc.replace("�", "E");
						desc = desc.replace("�", "I");
						desc = desc.replace("�", "O");
						desc = desc.replace("�", "U");
						desc = desc.replace("�", "n");
						desc = desc.replace("�", "N");
						desc = desc.replace("^\\w", " ");
						
						out.println("<cargo id=\"" + id + "\" >" + desc
								+ "</cargo>");
					} // end for
					out.println("</cargos>");
				}
			}
			return null;
		} catch (Exception e) {
			// System.out.println(e.getMessage());
			e.printStackTrace();
		}

		// System.out.println("Ejecutando codigo ajax");
		return null;
	}
}
