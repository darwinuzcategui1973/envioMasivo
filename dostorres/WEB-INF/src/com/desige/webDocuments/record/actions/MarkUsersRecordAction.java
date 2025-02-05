package com.desige.webDocuments.record.actions;

import java.util.Collection;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.desige.webDocuments.persistent.managers.HandlerDBUser;
import com.desige.webDocuments.utils.beans.SuperAction;
import com.desige.webDocuments.utils.beans.Users;

/**
 * Title: MMarkUsersRecordAction.java<br>
 * Copyright: (c) 2007 Focus Consulting<br>
 * Company:Focus Consulting (CON)<br>
 * @author YSA
 * <br>
 * Changes:<br>
 * <ul>
 *      <li> 19/12/2007 (YSA) Creation </li>
 * <ul>
 */

public class MarkUsersRecordAction extends SuperAction {
    public ActionForward execute(ActionMapping mapping,
                                 ActionForm form, HttpServletRequest request,
                                 HttpServletResponse response) {
        try{
        	super.init(mapping,form,request,response);
        	Users usuario = getUserSession();
        	if(usuario!=null){
				String areas = request.getParameter("areas");
				String cargos = request.getParameter("cargos");
				
				//removeAttribute("listUsersRecord");
				Collection users = HandlerDBUser.getUsers(areas,cargos,null,1);
				request.getSession().removeAttribute("listUsersRecord");
				request.getSession().setAttribute("listUsersRecord",users);         
	           
	            return goSucces();
        	}else{
        		//ActionForward result = new ActionForward("infoIframe.jsp", false);
				//return result;
        		return goError();
        	}
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return goError();
    }

}
