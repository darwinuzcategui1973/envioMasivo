package com.desige.webDocuments.seguridad.actions;

import java.util.ResourceBundle;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.desige.webDocuments.persistent.managers.HandlerDBUser;
import com.desige.webDocuments.persistent.managers.HandlerSeguridad;
import com.desige.webDocuments.seguridad.forms.SeguridadUserForm;
import com.desige.webDocuments.utils.ApplicationExceptionChecked;
import com.desige.webDocuments.utils.ToolsHTML;
import com.desige.webDocuments.utils.beans.SuperAction;
import com.desige.webDocuments.utils.beans.SuperActionForm;

/**
 * Title: EditSeguridadAction.java <br/>
 * Copyright: (c) 2004 Focus Consulting C.A.<br/>
 * @author Ing. Roger Rodríguez
 * @version WebDocuments v3.0
 * <br/>
 * Changes:<br/>
 * <ul>
 *      <li>09/08/2004 (RR) Creation </li>
 </ul>
 */
public class EditSeguridadAction extends SuperAction {
    
	public ActionForward execute(ActionMapping actionMapping,
            ActionForm form, HttpServletRequest request,
            HttpServletResponse httpServletResponse) {
        
        try {
        	super.init(actionMapping,form,request,httpServletResponse);
            String cmd = request.getParameter("cmd");
            String id = request.getParameter("user");
            
            if (cmd==null){
                cmd = SuperActionForm.cmdLoad;
            }
            
            getUserSession();
            SeguridadUserForm forma = (SeguridadUserForm)form;
            if (ToolsHTML.checkValue(cmd)){
                if ((cmd.equalsIgnoreCase(SuperActionForm.cmdNew))||
                    (cmd.equalsIgnoreCase(SuperActionForm.cmdLoad))){
                    if (forma==null) {
                        forma = new SeguridadUserForm();
                    }
                    if ((cmd.equalsIgnoreCase(SuperActionForm.cmdLoad))&&(id!=null)) {
                        forma.setNameUser(id);
                        HandlerSeguridad.load(forma);
                        cmd = SuperActionForm.cmdEdit;
                    } else {
                        forma = new SeguridadUserForm();
                        cmd = SuperActionForm.cmdInsert;
                    }
                    forma.setCmd(cmd);
                } else{
                    processCmd(forma,request);
                    request.setAttribute("grupo",forma.getIdGrupo());
                    return goTo("successLista");
                }
                ((SeguridadUserForm)form).setCmd(cmd);
                 putObjectSession("cmd",cmd);
            }
            return goSucces();
        } catch (ApplicationExceptionChecked ae) {
        	ae.printStackTrace();
            return goError(ae.getKeyError());
        } catch (Throwable e) {
            e.printStackTrace();
        }
        
        return goError();
    }

    /**
     * 
     * @param forma
     * @param request
     * @return
     * @throws Exception
     */
    public static boolean processCmd (SeguridadUserForm forma,HttpServletRequest request) throws Exception {
        boolean resp = false;
        StringBuffer mensaje = null;
        ResourceBundle rb = ToolsHTML.getBundle(request);
        if (forma.getCmd().equalsIgnoreCase(SuperActionForm.cmdInsert)){
            try {
                resp = HandlerSeguridad.insertUserSecurity(forma,null);
            } catch (ApplicationExceptionChecked ae) {

            } catch (Exception e) {
                e.printStackTrace();
            }
            if (resp){
                request.setAttribute("info",rb.getString("app.editOk"));
                forma.cleanForm();
                return true;
            } else{
                mensaje = new StringBuffer(rb.getString("app.notEdit"));
                mensaje.append(" ").append(HandlerDBUser.getMensaje());
            }
        } else{
            if (forma.getCmd().equalsIgnoreCase(SuperActionForm.cmdEdit)){
                if (HandlerSeguridad.edit(forma)) {
                    request.setAttribute("info",rb.getString("app.editOk"));
                    return true;
                } else {
                    mensaje = new StringBuffer(rb.getString("app.notEdit"));
                    mensaje.append(" ").append(HandlerDBUser.getMensaje());
                }
            } else{
                if (forma.getCmd().equalsIgnoreCase(SuperActionForm.cmdDelete)){
                    if (HandlerSeguridad.delete(forma)){
                        forma.cleanForm();
                        request.setAttribute("info",rb.getString("app.delete"));
                    } else{
                        mensaje = new StringBuffer(rb.getString("app.notDelete"));
                        mensaje.append(" ").append(HandlerDBUser.getMensaje());
                    }
                }
            }
        }
        if (mensaje!=null){
            request.setAttribute("info",mensaje.toString());
        }
        return false;
    }
}
