package com.desige.webDocuments.excel;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.desige.webDocuments.utils.beans.SuperAction;

/**
 * Clase de La Action Reporte Principal de Flujo de Trabajo <br>
 * 
 * Title:CrearReportePrincipalAction.java <br>
 * Copyright: (c) 2020 Focus Consulting C.A.<br/>
 * 
 * @author Darwin Felipe Uzcategui Gonzalez (DFUG)
 * 
 * @version WebDocuments v5.0 <br>
 *          Changes:<br>
 *          <ul>
 *          <li>16/11/2020 (DFUG) Creation</li>
 *          <ul>
 */
public class CrearReportePrincipalAction extends SuperAction {

	public String error;
	private static Logger log = LoggerFactory.getLogger(CrearReportePrincipalAction.class.getName());

	/**
	 * Clase para ActionForward execute Struts
	 */
	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
		super.init(mapping, form, request, response);

		try {

			GenerarLibroExcelPrincipal.generarLibroExcel(request, response);
			

		} catch (Exception e) {

			log.error(e.getMessage());
			e.printStackTrace();
			return goError();

		} 
		
		return null;
		
	}

}
