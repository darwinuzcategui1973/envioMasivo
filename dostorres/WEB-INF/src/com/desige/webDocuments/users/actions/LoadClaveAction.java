package com.desige.webDocuments.users.actions;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.desige.webDocuments.perfil.forms.PerfilActionForm;
import com.desige.webDocuments.persistent.managers.HandlerParameters;
import com.desige.webDocuments.utils.ApplicationExceptionChecked;
import com.desige.webDocuments.utils.ToolsHTML;
import com.desige.webDocuments.utils.beans.SuperAction;
import com.desige.webDocuments.utils.beans.Users;

/**
 * Title: LoadClaveAction.java <br/>
 * Copyright: (c) 2004 Focus Consulting C.A.<br/>
 * @author Ing. Roger Rodríguez
 * @version WebDocuments v3.0
 * <br/>
 * Changes:<br/> 
 * <ul>
 *      <li>12/07/2004 (RR) Creation </li>
 </ul>
 */
public class LoadClaveAction extends SuperAction {
    public ActionForward execute(ActionMapping actionMapping,
                                 ActionForm form, HttpServletRequest httpServletRequest,
                                 HttpServletResponse response) {
        super.init(actionMapping,form,httpServletRequest,response);
        try {
            Users user = getUserSession();
            if (ToolsHTML.checkValue(user.getUser())) {
                ((PerfilActionForm)form).setUser(user.getUser());
                PerfilActionForm forma = (PerfilActionForm)form;
                String lengthPass = HandlerParameters.PARAMETROS.getLengthPass();
                forma.setLengthPass(lengthPass);
                return goSucces();
            }
        } catch (ApplicationExceptionChecked ae) {
            return goError(ae.getKeyError());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return goError();

//        Users user = (Users)httpServletRequest.getSession().getAttribute("user");
//        //System.out.println("user = " + user);
//        if (user!=null){
//            if (ToolsHTML.checkValue(user.getUser())){
//                ((PerfilActionForm)form).setUser(user.getUser());
//                try {
//                    PerfilActionForm forma = (PerfilActionForm)form;
//                    String lengthPass = HandlerParameters.PARAMETROS.getLengthPass();
//                    forma.setLengthPass(lengthPass);
//                    return actionMapping.findForward("success");
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }else{
//
//            }
//        }
//        return actionMapping.findForward("error");
    }

}
