package com.desige.webDocuments.users.actions;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.desige.webDocuments.persistent.managers.HandlerDBUser;
import com.desige.webDocuments.utils.ToolsHTML;
import com.desige.webDocuments.utils.beans.SuperAction;
import com.desige.webDocuments.utils.beans.Users;

/**
 * Title: LogoutUserAction.java <br/> 
 * Copyright: (c) 2004 Focus Consulting C.A.<br/>
 * @author Ing. Nelson Crespo (NC)
 * @version WebDocuments v3.0
 * <br/>
 * Changes:<br/> 
 * <ul>
 *      <li> 08/04/2004 (NC) Creation </li>
 </ul>
 */
public class LogoutUserAction extends SuperAction {
    public ActionForward execute(ActionMapping actionMapping,
                                 ActionForm actionForm, HttpServletRequest request,
                                 HttpServletResponse response) {
        super.init(actionMapping,actionForm,request,response,false);
        try {
            Users user = (Users)getSessionObject("user");
            if (user!=null) {
            	if( request.getSession().getId().equals(HandlerDBUser.userConnectSession.get(user.getUser().toLowerCase())) ) {
            		HandlerDBUser.logoutUser(user.getUser());
            	}
            }
            ToolsHTML.clearSession(request.getSession());
            request.getSession().invalidate();
            if (getParameter("info")!=null) {
                putObjectSession("error",getMessage("user.changePassOK"));
            }
            String urlActiveFactorynoExiste=request.getParameter("urlActiveFactorynoExiste");
            putAtributte("urlActiveFactorynoExiste",urlActiveFactorynoExiste);
            putAtributte("target","_parent");
            putAtributte("redirect","index.jsp");
            if(request.getParameter("pag")!=null ) {
                if(request.getParameter("pag").equals("1")) {
                	return goTo("successClose");
                } else if(request.getParameter("pag").equals("2")) {
                    putObjectSession("error",getMessage("user.changePassOK"));
                	return goTo("success");
                }
            }
            return goSucces();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        putAtributte("target","info");
        putAtributte("redirect","info.jsp");
        return goError("E0056");
    }
}
