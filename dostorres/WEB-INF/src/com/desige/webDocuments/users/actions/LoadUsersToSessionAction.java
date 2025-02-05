package com.desige.webDocuments.users.actions;
import java.util.Collection;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.desige.webDocuments.persistent.managers.HandlerDBUser;
import com.desige.webDocuments.utils.ApplicationExceptionChecked;
import com.desige.webDocuments.utils.beans.SuperAction;

/**
 * Created by IntelliJ IDEA.
 * User: srodriguez
 * Date: 15/07/2005
 * Time: 11:18:49 AM
 * To change this template use File | Settings | File Templates.
 */
public class LoadUsersToSessionAction extends SuperAction{
    public ActionForward execute(ActionMapping actionMapping,
            ActionForm form, HttpServletRequest request,
            HttpServletResponse response) {
        super.init(actionMapping,form,request,response);
        try {
            getUserSession();
       ///*  usersToSession
            // */
            if(request.getParameterValues("usersToSession")!=null){
               String[] usersToSessionErase = request.getParameterValues("usersToSession");
               HandlerDBUser.getEraseUserConnect(usersToSessionErase);
            }

            Collection users = HandlerDBUser.getAllUserConnect();
            putObjectSession("usersToSession",users);
            return goTo("success");
        } catch (ApplicationExceptionChecked ae) {
            return goError(ae.getKeyError());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return goError("E0068");
    }
}
