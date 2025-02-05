package com.desige.webDocuments.city.actions;

import java.util.Collection;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.desige.webDocuments.city.forms.BaseCityForm;
import com.desige.webDocuments.persistent.managers.HandlerCity;
import com.desige.webDocuments.utils.ApplicationExceptionChecked;
import com.desige.webDocuments.utils.ToolsHTML;
import com.desige.webDocuments.utils.beans.SuperAction;
import com.desige.webDocuments.utils.beans.SuperActionForm;

/**
 * Title: LoadCityAction.java <br/>
 * Copyright: (c) 2004 Focus Consulting C.A.<br/>
 * 
 * @author Ing. Nelson Crespo (NC)
 * @version WebDocuments v3.0 <br/>
 *          Changes:<br/>
 *          <ul>
 *          <li>25/04/2004 (NC) Creation</li>
 *          </ul>
 */
public class LoadCityAction extends SuperAction {
	public ActionForward execute(ActionMapping actionMapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) {
		super.init(actionMapping, form, request, response);
		String descript = request.getParameter("SearchDescrip");
		try {
			getUserSession();
			Collection citys = HandlerCity.getAllCitys(descript);
			request.getSession().setAttribute("dataTable", citys);
			request.getSession().setAttribute("sizeParam",
					String.valueOf(citys.size()));
			// String input = request.getParameter("input");
			// String value = request.getParameter("value");
			// String nameForm = request.getParameter("nameForma");

			String input = (String) ToolsHTML.getAttribute(request, "input");
			String value = (String) ToolsHTML.getAttribute(request, "value");
			String nameForm = (String) ToolsHTML.getAttribute(request,
					"nameForma");

			request.getSession().setAttribute("input",
					getDataFormResponse(nameForm, input, true));
			request.getSession().setAttribute("value",
					getDataFormResponse(nameForm, value, false));
			request.getSession().setAttribute("editManager", "/editCity.do");
			request.getSession().setAttribute("newManager", "/newCity.do");
			request.getSession().setAttribute("loadManager", "loadCity.do");
			request.getSession().setAttribute("loadEditManager",
					"/loadCityEdit.do");
			request.getSession().setAttribute("formEdit", "city");
			String cmd = getCmd(request, false);
			BaseCityForm forma = (BaseCityForm) form;
			if (forma == null) {
				// System.out.println("Forma Nula........");
				form = new BaseCityForm();
			}
			if (ToolsHTML.checkValue(cmd)) {
				((SuperActionForm) form).setCmd(cmd);
				request.setAttribute("cmd", cmd);
			} else {
				request.setAttribute("cmd", SuperActionForm.cmdLoad);
			}
			return goSucces();// (actionMapping.findForward("success"));
		} catch (ApplicationExceptionChecked ae) {
			ae.printStackTrace();
			return goError(ae.getKeyError());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return goError();// (actionMapping.findForward("error"));
	}
}
