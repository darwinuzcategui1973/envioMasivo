package com.desige.webDocuments.files.actions;

import javax.servlet.Servlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.desige.webDocuments.dao.ConfExpedienteDAO;
import com.desige.webDocuments.files.facade.FilesFacade;
import com.desige.webDocuments.files.forms.FilesForm;
import com.desige.webDocuments.utils.ApplicationExceptionChecked;
import com.desige.webDocuments.utils.beans.SuperAction;
import com.desige.webDocuments.utils.beans.Users;

/**
 * Title: FilesNewAction.java <br/> Copyright: (c) 2008 Focus Consulting C.A.
 * <br/>
 * 
 * @author JRivero <br/> Changes:<br/>
 *         <ul>
 *         <li> 05/11/2008 (JR) Creation </li>
 *         </ul>
 */
public class FilesNewSaveAction extends SuperAction {
	private static Logger log = LoggerFactory.getLogger(FilesNewSaveAction.class.getName());

	public synchronized ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
		try {
			super.init(mapping, form, request, response);

			Users usuario = getUserSession();

			FilesFacade oFilesFacade = new FilesFacade(request);
			oFilesFacade.storeStructure(null, form, this, false, true); // genero el archivo jsp para la edicion
			oFilesFacade.storeStructure(null, form, this, true, true);  // genero el archivo jsp para la visualizacion
			
			FilesForm files = (FilesForm) form;
			
			ActionForward destino = new ActionForward("/filesNew.do?id=".concat(files.getId()));
			
			return destino;
		} catch (ApplicationExceptionChecked ae) {
			return goError(ae.getKeyError());
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return goError();
	}
	
	public static void updateFieldsFromServlet(Servlet servlet) throws Exception{
		FilesFacade oFilesFacade = new FilesFacade(null);
		
		oFilesFacade.storeStructure(servlet, null, null, false, false); // genero el archivo jsp para la edicion
		oFilesFacade.storeStructure(servlet, null, null, true, false);  // genero el archivo jsp para la visualizacion
	}
}
