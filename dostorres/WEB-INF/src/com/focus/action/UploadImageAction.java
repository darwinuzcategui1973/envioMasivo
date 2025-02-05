package com.focus.action;

import java.io.File;
import java.util.Iterator;
import java.util.List;
import java.util.ResourceBundle;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.DiskFileUpload;
import org.apache.commons.fileupload.FileItem;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.desige.webDocuments.parameters.actions.AdministracionAction;
import com.desige.webDocuments.utils.DesigeConf;
import com.desige.webDocuments.utils.ToolsHTML;
import com.desige.webDocuments.utils.beans.SuperAction;
import com.desige.webDocuments.utils.beans.Users;
import com.focus.qweb.facade.AuditFacade;
import com.focus.qweb.to.AuditTO;
import com.focus.qweb.to.TransactionTO;

public class UploadImageAction extends SuperAction {
	private static Logger log = LoggerFactory.getLogger(AdministracionAction.class.getName());

	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {

		super.init(mapping, form, request, response);

		procesaFicheros(request);

		return mapping.findForward("success");
	}

	public synchronized boolean procesaFicheros(HttpServletRequest req) {
		boolean resp = true;
		try {

			Users user = getUserSession();
			 
			DiskFileUpload fu = new DiskFileUpload();
			int lim = Integer.parseInt(DesigeConf.getProperty("application.limFiles"));
			fu.setSizeMax(1024 * 1024 * lim);
			fu.setSizeThreshold(4096);
			StringBuffer ruta = new StringBuffer(ToolsHTML.getPath());
			ruta.append("img").append(File.separator).append("logos");
			String path = ruta.toString();
			fu.setRepositoryPath(path);
			ruta.append(File.separator).append("empresa.gif");

			List fileItems = fu.parseRequest(req);
			ResourceBundle rb = ToolsHTML.getBundle(req);

			Iterator i = fileItems.iterator();
			FileItem actual = null;

			i = fileItems.iterator();
			actual = null;
			while (i.hasNext()) {
				actual = (FileItem) i.next();
				String name = actual.getFieldName();
				if (name.equalsIgnoreCase("nameImage")) {
					String fileName = actual.getName();
					File fichero = new File(fileName);
					System.out.println("Fichero Ori:" + fileName);
					System.out.println("Fichero Des:" + ruta.toString());
					if(fileName!=null && !fileName.trim().equals("") && fileName.toLowerCase().endsWith(".gif")) {
						fichero = new File(ruta.toString());
						if (fichero.exists()) {
							fichero.delete();
						}
						actual.write(fichero);
						
						AuditFacade.insertarAuditFacade(rb, TransactionTO.EDITAR_CONFIGURACION_ENCABEZADO_DE_HOJA_DE_IMPRESIONIMAGEN_GIF, "", user.getIdPerson(), user.getNameUser(), 
								"Cargado: " + fileName, "Destino:" + ruta.toString(), AuditTO.getClientIpAddress(req) );
					}
				}
			}
		} catch (Exception e) {
			resp = false;
			e.printStackTrace();
		}
		return resp;
	}

}
