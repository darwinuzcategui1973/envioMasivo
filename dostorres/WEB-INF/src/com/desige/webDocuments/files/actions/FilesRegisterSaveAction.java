package com.desige.webDocuments.files.actions;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.desige.webDocuments.files.facade.FilesFacade;
import com.desige.webDocuments.files.forms.ExpedienteForm;
import com.desige.webDocuments.utils.ApplicationExceptionChecked;
import com.desige.webDocuments.utils.ToolsHTML;
import com.desige.webDocuments.utils.beans.SuperAction;
import com.desige.webDocuments.utils.beans.Users;

/**
 * Title: FilesRegisterSaveAction.java <br/>
 * Copyright: (c) 2008 Focus Consulting C.A. <br/>
 * @author JRivero
 * <br/>
 *      Changes:<br/>
 * <ul>
 *      <li> 05/11/2008 (JR) Creation </li>
 * </ul>
 */
public class FilesRegisterSaveAction extends SuperAction {
	private static Logger log = LoggerFactory.getLogger(FilesRegisterSaveAction.class.getName());
	
    public synchronized ActionForward execute(ActionMapping mapping,
                                 ActionForm form, HttpServletRequest request,
                                 HttpServletResponse response) {
        try {
        	log.info("Iniciando guardado de propiedades de expediente");
            super.init(mapping,form,request,response);
            
			Users usuario = getUserSession();

			FilesFacade facade = new FilesFacade(request);
			
			facade.storeFiles(form,usuario);
			
			if(!ToolsHTML.isEmptyOrNull(String.valueOf(((ExpedienteForm)form).getF1()))){
				request.setAttribute("f1",((ExpedienteForm)form).getF1());
			}
			
			log.info("Finalizando guardado de propiedades de expediente");
            return goSucces();
        } catch (ApplicationExceptionChecked ae){
            return goError(ae.getKeyError());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return goError();
    }
}
