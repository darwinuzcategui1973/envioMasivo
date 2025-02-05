package com.focus.action;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.DiskFileUpload;
import org.apache.commons.fileupload.FileItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.desige.webDocuments.persistent.managers.HandlerParameters;
import com.desige.webDocuments.utils.DesigeConf;
import com.desige.webDocuments.utils.beans.SuperAction;
import com.focus.util.Archivo;

public class UploadCacheAction extends SuperAction {
	private static final Logger log = LoggerFactory.getLogger(UploadCacheAction.class);
	
	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {

		super.init(mapping, form, request, response);

		procesaFicheros(request);

		try {
			response.getWriter().write("Finalizado");
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null; // mapping.findForward("success");
	}

	private synchronized boolean procesaFicheros(HttpServletRequest req) {
		boolean resp = true;
		try {
			String idVersion = req.getParameter("idVersion");

			String nameFileCacheTemp = Archivo.getNameFileEncripted(null, "versiondocview", Integer.parseInt(idVersion), null);
			
			nameFileCacheTemp = nameFileCacheTemp.concat("_");
			File cache = new File(nameFileCacheTemp);
			boolean existeCache = cache.exists();

			log.info("Cache file: '" + cache.getAbsolutePath() + "' exists? " + existeCache);
			
			if("1".equals(String.valueOf(HandlerParameters.PARAMETROS.getDisabledCache())) ) { //&& !isDobleVersion) {
				existeCache = false;
			}
			log.info("Cache status after DB check: " + existeCache);
			
			if (!existeCache) {
				DiskFileUpload fu = new DiskFileUpload();
				int lim = Integer.parseInt(DesigeConf.getProperty("application.limFiles"));
				fu.setSizeMax(1024 * 1024 * lim);
				fu.setSizeThreshold(4096);
				StringBuffer ruta = new StringBuffer(nameFileCacheTemp);
				fu.setRepositoryPath(cache.getParent());
				
				log.info("Cache parent: " + cache.getParent());
				
				List fileItems = fu.parseRequest(req);

				Iterator i = fileItems.iterator();
				FileItem actual = null;

				i = fileItems.iterator();
				actual = null;
				File fichero = null;
				while (i.hasNext()) {
					actual = (FileItem) i.next();
					String name = actual.getFieldName();
					if (name.equalsIgnoreCase("nameCache")) {
						String fileName = actual.getName();
						fichero = new File(fileName);
						
						log.info("Fichero Ori:" + fichero.getAbsolutePath());
						log.info("Fichero Des:" + ruta.toString());
						
						if (fileName != null && !fileName.trim().equals("") 
								&& fileName.toLowerCase().endsWith(".pdf")) {
							fichero = new File(ruta.toString());
							if (fichero.exists()) {
								//borramos el archivo
								log.info("Borramos el archivo: " + fichero.getAbsolutePath());
								fichero.delete();
							}
							
							log.info("Escribimos ahora : " + fichero.getAbsolutePath()
									+ " en " + actual.getName());
							actual.write(fichero);
						}
					}
				}
				
				if(fichero!=null) {
					FileInputStream inputStream = new FileInputStream(fichero);
					
					Archivo.writeDocumentToDisk("versiondocview", Integer.parseInt(idVersion), inputStream);
					
					if (fichero.exists()) {
						log.info("Borramos el archivo: " + fichero.getAbsolutePath());
						fichero.delete();
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
