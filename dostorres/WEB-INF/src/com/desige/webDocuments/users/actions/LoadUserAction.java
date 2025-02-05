package com.desige.webDocuments.users.actions;

import java.util.Collection;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.desige.webDocuments.perfil.forms.PerfilActionForm;
import com.desige.webDocuments.persistent.managers.HandlerGrupo;
import com.desige.webDocuments.utils.ApplicationExceptionChecked;
import com.desige.webDocuments.utils.ToolsHTML;
import com.desige.webDocuments.utils.beans.SuperAction;
import com.desige.webDocuments.utils.beans.Users;

/**
 * Title: LoadUserAction.java <br/>
 * Copyright: (c) 2004 Focus Consulting C.A.<br/>
 * @author Ing. Roger Rodríguez
 * @version WebDocuments v3.0
 * <br/>
 * Changes:<br/> 
 * <ul>
 *      <li>23/06/2004 (RR) Creation </li>
 </ul>
 */

public class LoadUserAction extends SuperAction {
    public ActionForward execute(ActionMapping actionMapping,
                                 ActionForm form, HttpServletRequest httpServletRequest,
                                 HttpServletResponse httpServletResponse) {
        super.init(actionMapping,form,httpServletRequest,httpServletResponse);
        try {
            Users user = getUserSession();
            if (ToolsHTML.checkValue(user.getUser())) {
                ((PerfilActionForm)form).setUser(user.getUser());
                Collection grupo = HandlerGrupo.getAllGrupos();
                if (grupo!=null){
                    putAtributte("grupo",grupo);
//                    httpServletRequest.setAttribute("grupo",grupo);
                }
                return goSucces();
            }
        } catch (ApplicationExceptionChecked ae) {
            return goError(ae.getKeyError());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return goError();
    }
}
