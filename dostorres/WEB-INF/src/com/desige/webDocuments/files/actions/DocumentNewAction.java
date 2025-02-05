package com.desige.webDocuments.files.actions;

import java.util.ArrayList;
import java.util.Collection;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.desige.webDocuments.dao.ConfDocumentoDAO;
import com.desige.webDocuments.files.forms.DocumentForm;
import com.desige.webDocuments.persistent.managers.HandlerTypeDoc;
import com.desige.webDocuments.utils.ApplicationExceptionChecked;
import com.desige.webDocuments.utils.ToolsHTML;
import com.desige.webDocuments.utils.beans.SuperAction;

/**
 * Title: FilesNewAction.java <br/>
 * Copyright: (c) 2008 Focus Consulting C.A. <br/>
 * @author JRivero
 * <br/>
 *      Changes:<br/>
 * <ul>
 *      <li> 05/11/2008 (JR) Creation </li>
 * </ul>
 */
public class DocumentNewAction extends SuperAction {
	private static Logger log = LoggerFactory.getLogger(DocumentNewAction.class.getName());
	
    public synchronized ActionForward execute(ActionMapping mapping,
                                 ActionForm form, HttpServletRequest request,
                                 HttpServletResponse response) {
        try {
        	super.init(mapping,form,request,response);
            log.info("Iniciando guardado de campos adicionales de documentos");

			// obtenemos la lista de campos definidos
			ConfDocumentoDAO oConfDocumentoDAO = new ConfDocumentoDAO();
			ArrayList lista = (ArrayList) oConfDocumentoDAO.findAll();
			
			DocumentForm document = new DocumentForm();
			
			if(request.getParameter("id")==null){
				if(lista.size()>0) {
					document=(DocumentForm)lista.get(0);
				} else {
					document=new DocumentForm();
					document.setOrden(1);
				}
			} else {
				if(request.getParameter("id").toString().startsWith("d")) {
					for(int i=0; i< lista.size(); i++) {
						document=(DocumentForm)lista.get(i);
						if(document.getId().equals(String.valueOf(request.getParameter("id")))){
							break;
						}
					}
					
					if(Boolean.parseBoolean(request.getParameter("showInfoMsg"))){
						log.info("Colocado mensaje de exito en el request");
						request.setAttribute("info", ToolsHTML.getBundle(request).getObject("app.editOk"));
					}
				} else if(request.getParameter("id").toString().equals("nuevo")) {
					document.setId("d".concat(String.valueOf(lista.size()+1)));
					document.setVisible(1);
					document.setEditable(1);
					document.setOrden(lista.size() + 1);
				}
			}

			// buscamos los tipos de documentos asociados
			request.setAttribute("listaTypeDocs",oConfDocumentoDAO.typeDocumentsAssociate(document.getId()));
			
            Collection tiposDoc = HandlerTypeDoc.getAllTypeDocs(null,true); //tipos de documento
            putObjectSession("tiposDoc",tiposDoc);
			
			request.getSession().setAttribute("documentNew","true");
			request.setAttribute("campo",document);
			request.setAttribute("lista",lista);
			if(lista.size()>0) {
				request.setAttribute("eliminable",((DocumentForm)lista.get(lista.size()-1)).getId());
			} else {
				request.setAttribute("eliminable","d1");
			}
			
			log.info("Finalizando guardado de campos adicionales de documentos");
            return goSucces();
        } catch (ApplicationExceptionChecked ae){
            return goError(ae.getKeyError());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return goError();
    }
}
