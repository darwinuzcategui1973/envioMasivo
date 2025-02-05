package com.desige.webDocuments.parameters.actions;

import java.io.File;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.desige.webDocuments.parameters.forms.BaseParametersForm;
import com.desige.webDocuments.utils.ApplicationExceptionChecked;
import com.desige.webDocuments.utils.StringUtil;
import com.desige.webDocuments.utils.ToolsHTML;
import com.desige.webDocuments.utils.beans.SuperAction;
import com.desige.webDocuments.utils.beans.Users;
import com.focus.util.Archivo;

/**
 * Title: LoadParametersAction.java <br/> Copyright: (c) 2004 Focus Consulting
 * C.A.<br/>
 * 
 * @author Ing. Nelson Crespo (NC)
 * @version WebDocuments v3.0 <br/> Changes:<br/>
 *          <ul>
 *          <li>20/05/2004 (NC) Creation </li>
 *          </ul>
 */
public class EnLineaAction extends SuperAction {
	private static Logger log = LoggerFactory.getLogger(EnLineaAction.class.getName());

	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {

		super.init(mapping, form, request, response);

		BaseParametersForm forma = (BaseParametersForm) form;
		if (forma == null) {
			forma = new BaseParametersForm();
		}
		try {
			Users usuario = getUserSession();
			
			// consultamos el documento
			//System.out.println(request.getParameter("fichero"));
			StringBuffer nameFile = new StringBuffer();
			nameFile.append(ToolsHTML.getPathTmp());
			nameFile.append(StringUtil.getOnlyNameFile(String.valueOf(request.getParameter("fichero"))));
			//System.out.println(nameFile.toString());
			
			File f = new File(nameFile.toString());
			StringBuffer contenido = new StringBuffer();
			if(f.exists()) {
				Archivo a = new Archivo();
				contenido = a.leer(nameFile.toString());
			}
			request.setAttribute("documentoEnLinea", contenido.toString());

			return goSucces();
		} catch (ApplicationExceptionChecked ae) {
			ae.printStackTrace();
			return goError(ae.getKeyError());
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return goError();
	}
}
