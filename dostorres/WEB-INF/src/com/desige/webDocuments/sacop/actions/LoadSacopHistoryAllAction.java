package com.desige.webDocuments.sacop.actions;

import java.util.Collection;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.desige.webDocuments.persistent.managers.HandlerProcesosSacop;
import com.desige.webDocuments.sacop.forms.Plantilla1BD;
import com.desige.webDocuments.utils.DesigeConf;
import com.desige.webDocuments.utils.beans.SuperAction;
import com.desige.webDocuments.utils.beans.Users;

public class LoadSacopHistoryAllAction extends SuperAction {
	
    public synchronized ActionForward execute(ActionMapping mapping,
            ActionForm form, HttpServletRequest request,
            HttpServletResponse response) {

    	super.init(mapping,form,request,response);
	
	    Users user = (Users)request.getSession().getAttribute("user");
	
		//Collection<Plantilla1BD> cerrado = HandlerProcesosSacop.getUsuarioSacopPendientes(user, String.valueOf(user.getIdPerson()), LoadSacop.edoCerrado, false, request.getParameter("idDocumentRelatedFilter"));
	    Collection<Plantilla1BD> cerrado = HandlerProcesosSacop.getUsuarioSacopPendientes(request, user, String.valueOf(user.getIdPerson()), null, false, request.getParameter("idDocumentRelatedFilter"), false);
		
	    
		putObjectSession("cerrado", cerrado);
		
		return mapping.findForward("cerrado");
    }
    

}
