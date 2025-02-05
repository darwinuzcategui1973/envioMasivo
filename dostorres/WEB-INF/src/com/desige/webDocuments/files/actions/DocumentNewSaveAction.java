package com.desige.webDocuments.files.actions;

import javax.servlet.Servlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.desige.webDocuments.files.facade.DocumentFacade;
import com.desige.webDocuments.files.forms.DocumentForm;
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
public class DocumentNewSaveAction extends SuperAction {
	private static Logger log = LoggerFactory.getLogger(DocumentNewSaveAction.class.getName());

	public synchronized ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
		try {
			super.init(mapping, form, request, response);
			
			Users usuario = getUserSession();
			log.info("Inciando creacion de archivos de edicion y visualizacion de campos adicionales de documentos");
			DocumentFacade oDocumentFacade = new DocumentFacade(request);
			oDocumentFacade.storeStructure(null, form, this, false, true); // genero el archivo jsp para la edicion
			oDocumentFacade.saveTiposDeDocumentosAsociados(form);
			oDocumentFacade.storeStructure(null, form, this, true, true);  // genero el archivo jsp para la visualizacion
			
			
			DocumentForm document = (DocumentForm) form;
			
			ActionForward destino = new ActionForward("/documentNew.do?id=".concat(document.getId()).concat("&showInfoMsg=true"));
			
			log.info("Finalizada creacion de archivos de edicion y visualizacion de campos adicionales de documentos");
			log.info("Retornando a destino: " + destino.getPath());
			return destino;
		} catch (ApplicationExceptionChecked ae) {
			return goError(ae.getKeyError());
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return goError();
	}
	
	public static void updateFieldsFromServlet(Servlet servlet) throws Exception{
		DocumentFacade oDocumentFacade = new DocumentFacade(null);
		
		oDocumentFacade.storeStructure(servlet, null, null, false, false); // genero el archivo jsp para la edicion
		oDocumentFacade.storeStructure(servlet, null, null, true, false);  // genero el archivo jsp para la visualizacion
	}
}
