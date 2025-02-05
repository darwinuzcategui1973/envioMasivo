package com.desige.webDocuments.seguridad.actions;

import java.util.ResourceBundle;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.desige.webDocuments.perfil.forms.PerfilActionForm;
import com.desige.webDocuments.persistent.managers.HandlerSeguridad;
import com.desige.webDocuments.seguridad.forms.SeguridadUserForm;
import com.desige.webDocuments.utils.ApplicationExceptionChecked;
import com.desige.webDocuments.utils.ToolsHTML;
import com.desige.webDocuments.utils.beans.SuperAction;
import com.desige.webDocuments.utils.beans.Users;

/**
 * Title: LoadSeguridadAction.java <br/>
 * Copyright: (c) 2004 Focus Consulting C.A.<br/>
 * @author Ing. Roger Rodríguez
 * @version WebDocuments v3.0
 * <br/>
 * Changes:<br/> 
 * <ul>
 *      <li>09/08/2004 (RR) Creation </li>
 </ul>
 */
public class LoadSeguridadAction extends SuperAction {
    public ActionForward execute(ActionMapping actionMapping,
                                 ActionForm form, HttpServletRequest request,
                                 HttpServletResponse httpServletResponse) {
        super.init(actionMapping,form,request,httpServletResponse);
        try {
            Users user = getUserSession();
            if (ToolsHTML.checkValue(user.getUser())) {
                String userseg = request.getParameter("userseg");
                ((SeguridadUserForm)form).setNameUser(userseg);
                ResourceBundle rb = ToolsHTML.getBundle(request);
                if (HandlerSeguridad.save((SeguridadUserForm)form)) {
                    int numRecords = Integer.parseInt(((PerfilActionForm)form).getNumRecordPages());
                    user.setNumRecord(numRecords);
                    request.setAttribute("info",rb.getString("app.editOk"));
                } else{
                    StringBuffer mensaje = new StringBuffer(rb.getString("app.notEdit"));
                    mensaje.append("<br/>").append(HandlerSeguridad.getMensaje());
                    request.setAttribute("info",mensaje.toString());
                }
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
//                String userseg = httpServletRequest.getParameter("userseg");
//                ((SeguridadUserForm)form).setNameUser(userseg);
//                try {
//                    ResourceBundle rb = ToolsHTML.getBundle(request);
//                    if (HandlerSeguridad.save((SeguridadUserForm)form)){
//                        int numRecords = Integer.parseInt(((PerfilActionForm)form).getNumRecordPages());
//                        user.setNumRecord(numRecords);
//                        httpServletRequest.setAttribute("info",rb.getString("app.editOk"));
//                    } else{
//                        StringBuffer mensaje = new StringBuffer(rb.getString("app.notEdit"));
//                        mensaje.append("<br/>").append(HandlerSeguridad.getMensaje());
//                        httpServletRequest.setAttribute("info",mensaje.toString());
//                    }
//                    return actionMapping.findForward("success");
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }else{//Enviar Mensaje Session Cerrada
//
//            }
//        }
//        return actionMapping.findForward("error");
    }
}
