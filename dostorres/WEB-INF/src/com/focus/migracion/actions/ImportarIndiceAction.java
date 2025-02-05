package com.focus.migracion.actions;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.desige.webDocuments.utils.ApplicationExceptionChecked;
import com.desige.webDocuments.utils.ToolsHTML;
import com.desige.webDocuments.utils.beans.SuperAction;
import com.desige.webDocuments.utils.beans.Users;
import com.focus.qweb.dao.IndiceDAO;
import com.focus.qweb.facade.MigracionFacade;
import com.focus.qweb.to.IndiceTO;


public class ImportarIndiceAction extends SuperAction {
	static Logger log = LoggerFactory.getLogger(ImportarIndiceAction.class.getName());

	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
		super.init(mapping, form, request, response);
		
        try {
        	Users user = getUserSession();
            if (!ToolsHTML.checkValue(user.getUser())) {
            	return goError();
            }
        	MigracionFacade mf = new MigracionFacade();
    		ArrayList equivalencias  = (ArrayList)mf.listarEquivalencias();
    		request.setAttribute("equivalencias", equivalencias);
            
            if(request.getParameter("cmd")!=null) {
            	String cmd = request.getParameter("cmd");
            	String numero = request.getParameter("numero");
            	IndiceDAO indiceDAO = new IndiceDAO();
            	IndiceTO indiceTO = new IndiceTO();
            	if(cmd.equals("listar")) {
            		if(!ToolsHTML.isEmptyOrNull(numero)) {
            			ArrayList lista = indiceDAO.listar(numero);
            			request.setAttribute("lista", lista);
            			request.setAttribute("numero", numero);
            		}
            	} else if(cmd.equals("actualizar")) {
                	String clave = request.getParameter("clave");
                	String valor = request.getParameter("valor");
                	indiceTO.setClave(clave);
                	indiceTO.setValor(valor);
                	indiceTO.setIndice(numero);
            		if(indiceDAO.actualizar(indiceTO)>0) {
                		response.getWriter().print("update");
            		} else if(indiceDAO.insertar(indiceTO)>0){
            				response.getWriter().print("insert");
            		} else {
        				response.getWriter().print("error");
            		}
            		return null;
            	} else if(cmd.equals("eliminar")) {
                	String clave = request.getParameter("clave");
                	String valor = request.getParameter("valor");
                	indiceTO.setClave(clave);
                	indiceTO.setValor(valor);
                	indiceTO.setIndice(numero);
            		indiceDAO.eliminar(indiceTO);
               		response.getWriter().print("delete");
            		return null;
            	}
            }
            	
        	return goSucces();
        } catch (ApplicationExceptionChecked ae) {
    		try {
				response.getWriter().print(ae.getMessage());
			} catch (IOException e) {
				e.printStackTrace();
			}
            return goError(ae.getKeyError());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return goError();
	}
}
