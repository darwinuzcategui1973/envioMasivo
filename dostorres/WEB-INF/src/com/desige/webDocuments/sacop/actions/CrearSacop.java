package com.desige.webDocuments.sacop.actions;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.desige.webDocuments.utils.beans.SuperAction;
import com.focus.qweb.dao.PlanillaSacop1DAO;
import com.focus.qweb.to.PlanillaSacop1TO;

/**
 * Created by IntelliJ IDEA.
 * User: Administrador
 * Date: 31/03/2006
 * Time: 09:43:13 AM
 * To change this template use File | Settings | File Templates.
 */

public class CrearSacop extends SuperAction {
    private static Logger log = LoggerFactory.getLogger(CrearSacop.class.getName());
    
    public ActionForward execute(ActionMapping mapping,
    		ActionForm form, HttpServletRequest request,
    		HttpServletResponse response) {

        super.init(mapping,form,request,response);

        try{
        	if(request.getAttribute("id")!=null) {
        		// carga la sacop
        		PlanillaSacop1DAO oPlanillaSacop1DAO = new PlanillaSacop1DAO();
        		PlanillaSacop1TO oPlanillaSacop1TO = new PlanillaSacop1TO();
        		
        		oPlanillaSacop1TO.setIdplanillasacop1((String)request.getAttribute("id"));
        		oPlanillaSacop1DAO.cargar(oPlanillaSacop1TO);
        		
        		request.setAttribute("oPlanillaSacop1TO",oPlanillaSacop1TO);
        		
        		oPlanillaSacop1TO.setIdDocumentRelated(String.valueOf(request.getParameter("idDocRelated")));
        	}
        	
        	request.setAttribute("crearSacop","crearSacop");
        	System.out.println(request.getParameter("idDocRelated"));
        	putObjectSession("idDocRelated", request.getParameter("idDocRelated"));
        	return goSucces();
        }catch(Exception e){
        	log.error(e.getMessage());
            e.printStackTrace();
        } finally {
        	/**/
        }

        return goError();
    }
}
