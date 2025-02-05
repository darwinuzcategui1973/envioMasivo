package com.desige.webDocuments.sacop.actions;

import java.util.ResourceBundle;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.desige.webDocuments.persistent.managers.HandlerProcesosSacop;
import com.desige.webDocuments.sacop.forms.ClasificacionPlanillasSacop;
import com.desige.webDocuments.utils.ApplicationExceptionChecked;
import com.desige.webDocuments.utils.ToolsHTML;
import com.desige.webDocuments.utils.beans.SuperAction;

/**
 * 
 * Title: LoadEditClasificacionPlanillasSacopAction.java <br>
 * Copyright: (c) 2012 Focus Consulting C.A.<br>
 * @author Ing. Felipe Rojas (FJR)
 * 
 * @version WebDocuments v4.5.2
 * <br>
 *     Changes:<br>
 * <ul>
 *     <li> 24/04/2012 (FJR) Creation </li>
 * <ul>
 */
public class LoadEditClasificacionPlanillasSacopAction extends SuperAction {
	
	public ActionForward execute(ActionMapping actionMapping, ActionForm form, HttpServletRequest request, 
			HttpServletResponse response) {
		
		super.init(actionMapping, form, request, response);

		try {
			ResourceBundle rb = ToolsHTML.getBundle(request);
			getUserSession();
			//System.out.println("cargando Forma.............");
			HandlerProcesosSacop.load((ClasificacionPlanillasSacop) form);
			removeObjectSession("valor");
			putObjectSession("editTypeDoc", form);
			putObjectSession("typeOtroFormato", form);
			removeObjectSession("descript");
			return goSucces();
		} catch (ApplicationExceptionChecked ae) {
			return goError(ae.getKeyError());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return goError();
	}
}
