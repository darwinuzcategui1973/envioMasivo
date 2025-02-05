package com.desige.webDocuments.files.actions;

import java.util.ArrayList;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.desige.webDocuments.document.forms.BaseDocumentForm;
import com.desige.webDocuments.files.facade.FilesFacade;
import com.desige.webDocuments.files.forms.ExpedienteDetalleForm;
import com.desige.webDocuments.files.forms.ExpedienteForm;
import com.desige.webDocuments.utils.ApplicationExceptionChecked;
import com.desige.webDocuments.utils.FilesDocumentNotValidException;
import com.desige.webDocuments.utils.ToolsHTML;
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
public class FilesSaveVersionAction extends SuperAction {
	private static Logger log = LoggerFactory.getLogger(FilesDeleteAction.class.getName());

	public synchronized ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
		
		String numVersion = request.getParameter("numVersion");
		
		try {
			super.init(mapping, form, request, response);

			Users usuario = getUserSession();

			ExpedienteForm files = new ExpedienteForm();
			FilesFacade oFilesFacade = new FilesFacade();

			files.setF2(new Date());
			if (!ToolsHTML.isEmptyOrNull(request.getParameter("f1")) || !ToolsHTML.isEmptyOrNull(String.valueOf(request.getAttribute("f1")))) {

				// obtenemos la lista de campos definidos
				if (!ToolsHTML.isEmptyOrNull(String.valueOf(request.getAttribute("f1")))) {
					files.setF1(Integer.parseInt(String.valueOf(request.getAttribute("f1"))));
				} else {
					files.setF1(Integer.parseInt(String.valueOf(request.getParameter("f1"))));
				}
				
				try {
					FilesFacade filesFacade = new FilesFacade();
					ArrayList<BaseDocumentForm> documentos = new ArrayList<BaseDocumentForm>();
					
					files = filesFacade.findById(files);
					
					ExpedienteDetalleForm exp = oFilesFacade.saveVersion(files, usuario);
					if(exp != null){

						filesFacade.construirDocumentoPdfFromFiles(request, usuario, String.valueOf(files.getF1()), String.valueOf(files.getNumVer()), "false", documentos, files, exp);
						
						putObjectSession("info",getMessage("files.version.save"));
					}
				} catch(FilesDocumentNotValidException ex) {
					putObjectSession("info",getMessage("E0121"));
				}
			}
			request.setAttribute("f1", files.getF1());
            //putObjectSession("info",getMessage("app.delete"));
			return goSucces();
		} catch (ApplicationExceptionChecked ae) {
			return goError(ae.getKeyError());
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return goError();
	}
}
