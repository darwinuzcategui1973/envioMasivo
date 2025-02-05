package com.desige.webDocuments.document.servlet;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.desige.webDocuments.files.actions.DocumentNewSaveAction;
import com.desige.webDocuments.files.actions.FilesNewSaveAction;
import com.desige.webDocuments.utils.Constants;

public class UpdateExtraFieldsServlet extends HttpServlet{
	/**
	 * 
	 */
	private static final long serialVersionUID = -398929123637614342L;
	
	private static final Logger log = LoggerFactory.getLogger(UpdateExtraFieldsServlet.class);
	
	@Override
	public void init() throws ServletException {
		// TODO Auto-generated method stub
		super.init();
		log.info("Iniciando servlet");
		
		if(Constants.JDBC_CONNECTION) {
			log.info("Iniciando update de JSP dinamico de campos adicionales de documentos y expedientes");
			
			try {
				FilesNewSaveAction.updateFieldsFromServlet(this);
			} catch (Exception e) {
				// TODO: handle exception
				log.error("Error en update de JSP dinamico de campos adicionales de expedientes"
						+ ". Error fue: " + e.getMessage(), e);
			}
			
			try {
				DocumentNewSaveAction.updateFieldsFromServlet(this);
			} catch (Exception e) {
				// TODO: handle exception
				log.error("Error en update de JSP dinamico de campos adicionales de documentos"
						+ ". Error fue: " + e.getMessage(), e);
			}
		}
		
		log.info("Finalizado update de JSP dinamico de campos adicionales de documentos y expedientes");
	}
}
