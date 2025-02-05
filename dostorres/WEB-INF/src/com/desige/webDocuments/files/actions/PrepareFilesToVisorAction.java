package com.desige.webDocuments.files.actions;

import java.io.File;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.desige.webDocuments.bean.ExpedienteRequest;
import com.desige.webDocuments.dao.ExpedienteDAO;
import com.desige.webDocuments.dao.ExpedienteDetalleDAO;
import com.desige.webDocuments.dao.ExpedienteVersionDAO;
import com.desige.webDocuments.document.forms.BaseDocumentForm;
import com.desige.webDocuments.files.facade.FilesFacade;
import com.desige.webDocuments.files.forms.ExpedienteForm;
import com.desige.webDocuments.persistent.managers.HandlerDocuments;
import com.desige.webDocuments.persistent.managers.HandlerStruct;
import com.desige.webDocuments.persistent.managers.HandlerVisor;
import com.desige.webDocuments.utils.ApplicationExceptionChecked;
import com.desige.webDocuments.utils.DocumentsNotFoundException;
import com.desige.webDocuments.utils.FilesDocumentNotValidException;
import com.desige.webDocuments.utils.FilesNotFoundInDiskException;
import com.desige.webDocuments.utils.ToolsHTML;
import com.desige.webDocuments.utils.beans.SuperAction;
import com.desige.webDocuments.utils.beans.Users;
import com.focus.util.ConcatPDFFile;

import sun.jdbc.rowset.CachedRowSet;

/**
 * 
 * @author Usuario
 *
 */
public class PrepareFilesToVisorAction extends SuperAction{
	private static Logger log = LoggerFactory.getLogger(PrepareFilesToVisorAction.class);
	
	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) {
		// TODO Auto-generated method stub
		super.init(mapping, form, request, response);

		//parametros a recibir en el request
		//codigo del expediente
		//version
		//imprimir
		String f1 = request.getParameter("f1");
		String numVersion = request.getParameter("numVersion");
		String imprimir = request.getParameter("imprimir");
		ArrayList<BaseDocumentForm> documentos = new ArrayList<BaseDocumentForm>();
		boolean isOldVersion = false;
		
		try {
			Users usuario = getUserSession();
			
			ExpedienteForm files = new ExpedienteForm();
			FilesFacade filesFacade = new FilesFacade();
			
			if(numVersion!=null && ToolsHTML.parseInt(numVersion)>0) {
				isOldVersion = true;
				// recuperamos la version almacenada
				files.setF1(ToolsHTML.parseInt(f1));
				files.setFilesVersion(ToolsHTML.parseInt(numVersion));
				filesFacade.findByIdAndNumVersionFromHistory(files);
				files.setNumVer(files.getFilesVersion());
				filesFacade.recuperarVersionAlmacenada(request, usuario, files);
			} else {
				// generamos un pdf con los pdf de cada documento
				filesFacade.construirDocumentoPdfFromFiles(request, usuario, f1, numVersion, imprimir, documentos, files, null);
			}
			
			HandlerVisor handlerVisor = new HandlerVisor(request,response,null);
			
			handlerVisor.generateJNLP(ToolsHTML.getPath(),files.isPrinting(),files.isPrinting(),true,null);
				
			
		} catch(FilesNotFoundInDiskException e) {
			log.info(e.getMessage());
			request.setAttribute("info", ToolsHTML.getBundle(request).getObject("files.document.pdf.notexist"));
			return mapping.findForward("showMessage");
		} catch(DocumentsNotFoundException e) {
			log.info("Ninguno de los archivos relacionados al expediente puede ser agregado al mimso por no cumplir las condiciones minimas");
			request.setAttribute("info", ToolsHTML.getBundle(request).getObject("files.notDocumentFound"));
			return mapping.findForward("showMessage");
		} catch (FilesDocumentNotValidException e) {
			putObjectSession("info",getMessage("E0121"));
			return mapping.findForward("showMessage");
		} catch (Exception e) {
			log.error("Error preparando expediente f1=" + f1 + " para ser visualizada"
					+ " su version " + numVersion, e);
		}
		
		log.info("Finalizado proceso de creacion del visor para expediente f1=" + f1 
				+ " para ser visualizada su version " + numVersion);
		
		if(documentos.size()>0 || isOldVersion){
			return null;
		} else {
			return mapping.findForward("showMessage");
		}
	}
	
	
}
