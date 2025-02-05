package com.desige.webDocuments.users.actions;

import java.util.Collection;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.desige.webDocuments.persistent.managers.HandlerDBUser;
import com.desige.webDocuments.persistent.managers.HandlerGrupo;
import com.desige.webDocuments.utils.ApplicationExceptionChecked;
import com.desige.webDocuments.utils.ToolsHTML;
import com.desige.webDocuments.utils.beans.SuperAction;

/**
 * Title: LoadUsersToEditAction.java <br/>
 * Copyright: (c) 2004 Focus Consulting C.A.<br/>
 *
 * @author Ing. Nelson Crespo
 * @version WebDocuments v3.0
 * <br/>
 *       Changes:<br/>
 * <ul>
 *       <li> 30/01/2005 (NC) Creation </li>
 * </ul>
 */
public class LoadUsersToEditAction extends SuperAction {
    public ActionForward execute(ActionMapping actionMapping,
            ActionForm form, HttpServletRequest request,
            HttpServletResponse response) {
        super.init(actionMapping,form,request,response);
        try {
            getUserSession();
            String toSearch = getParameter("toSearch");
            String loadUser = getParameter("loadUser");
            removeAttribute("usersToEdit");
            //System.out.println("toSearch = " + toSearch);
            //System.out.println("loadUser = " + loadUser);
            if (ToolsHTML.isEmptyOrNull(loadUser)) {
                Collection groups = HandlerGrupo.getAllGroups(toSearch);
                putObjectSession("groupsToEdit",groups);
            } else {
                Collection users = HandlerDBUser.getAllUsersForGrups(toSearch,false);
                if (users!=null)
                    putObjectSession("usersToEdit",users);
            }
            return goTo(request.getParameter("goTo"));
        } catch (ApplicationExceptionChecked ae) {
            return goError(ae.getKeyError());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return goError("E0021");
    }

    //
}
