package com.desige.webDocuments.users.actions;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.desige.webDocuments.grupo.forms.grupoForm;
import com.desige.webDocuments.persistent.managers.HandlerGrupo;
import com.desige.webDocuments.utils.ApplicationExceptionChecked;
import com.desige.webDocuments.utils.beans.SuperAction;

/**
 * Title: LoadEditClaveAction.java <br/>
 * Copyright: (c) 2004 Focus Consulting C.A.<br/>
 * @author Ing. Roger Rodríguez
 * @version WebDocuments v3.0
 * <br/>
 * Changes:<br/> 
 * <ul>
 *      <li>13/07/2004 (RR) Creation </li>
 </ul>
 */
public class LoadEditClaveAction extends SuperAction {
    public ActionForward execute(ActionMapping actionMapping,
                                 ActionForm form, HttpServletRequest request,
                                 HttpServletResponse response) {
        super.init(actionMapping,form,request,response);
        try {
            getUserSession();
            HandlerGrupo.load((grupoForm)form);
            putObjectSession("editGrupoForm",form);
            return goSucces();
        } catch (ApplicationExceptionChecked ae) {
            return goError(ae.getKeyError());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return goError();

//        Users user = (Users)request.getSession().getAttribute("user");
//        if (user!=null){
//            try {
//                HandlerGrupo.load((grupoForm)form);
//                request.getSession().setAttribute("editGrupoForm",form);
//                return (actionMapping.findForward("success"));
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
//        return (actionMapping.findForward("error"));
    }
 }
