package com.desige.webDocuments.files.actions;

import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.desige.webDocuments.dao.ConfExpedienteDAO;
import com.desige.webDocuments.files.forms.FilesForm;
import com.desige.webDocuments.utils.ApplicationExceptionChecked;
import com.desige.webDocuments.utils.beans.SuperAction;
import com.desige.webDocuments.utils.beans.Users;

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
public class FilesNewAction extends SuperAction {
	private static Logger log = LoggerFactory.getLogger(FilesNewAction.class.getName());
	
    public synchronized ActionForward execute(ActionMapping mapping,
                                 ActionForm form, HttpServletRequest request,
                                 HttpServletResponse response) {
        try {
            super.init(mapping,form,request,response);
            
			Users usuario = getUserSession();

			// obtenemos la lista de campos definidos
			ConfExpedienteDAO oConfExpedienteDAO = new ConfExpedienteDAO();
			ArrayList lista = (ArrayList)oConfExpedienteDAO.findAll();
			
			FilesForm file = new FilesForm();
			if(request.getParameter("id")==null){
				file=(FilesForm)lista.get(0);
			} else {
				if(request.getParameter("id").toString().startsWith("f")) {
					for(int i=0; i< lista.size(); i++) {
						file=(FilesForm)lista.get(i);
						if(file.getId().equals(String.valueOf(request.getParameter("id")))){
							break;
						}
					}
				} else if(request.getParameter("id").toString().equals("nuevo")) {
					file.setId("f".concat(String.valueOf(lista.size()+1)));
					file.setVisible(1);
					file.setEditable(1);
				}
			}
				
			request.getSession().setAttribute("filesNew","true");
			request.setAttribute("campo",file);
			request.setAttribute("lista",lista);
			request.setAttribute("eliminable",((FilesForm)lista.get(lista.size()-1)).getId());
            return goSucces();
        } catch (ApplicationExceptionChecked ae){
            return goError(ae.getKeyError());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return goError();
    }
}
