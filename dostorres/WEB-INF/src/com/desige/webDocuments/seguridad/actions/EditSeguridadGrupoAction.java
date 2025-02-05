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
 * Title: EditSeguridadGrupoAction.java <br/>
 * Copyright: (c) 2004 Focus Consulting C.A.<br/>
 * @author Ing. Roger Rodríguez
 * @version WebDocuments v3.0
 * <br/>
 * Changes:<br/> 
 * <ul>
 *      <li>11/08/2004 (RR) Creation </li>
 </ul>
 */
public class EditSeguridadGrupoAction extends SuperAction {
    public ActionForward execute(ActionMapping actionMapping,
            ActionForm form, HttpServletRequest request,
            HttpServletResponse httpServletResponse) {
        super.init(actionMapping,form,request,httpServletResponse);
        String cmd = request.getParameter("cmd");
        String id = request.getParameter("idGrupo");
        if (cmd==null){
            cmd = SuperActionForm.cmdLoad;
        }
        try {
            getUserSession();
            SeguridadUserForm forma = (SeguridadUserForm)form;
            if (ToolsHTML.checkValue(cmd)){
                if ((cmd.equalsIgnoreCase(SuperActionForm.cmdNew))||
                    (cmd.equalsIgnoreCase(SuperActionForm.cmdLoad))){
                    if (form==null){
                        forma = new SeguridadUserForm();
                    }
                    if ((cmd.equalsIgnoreCase(SuperActionForm.cmdLoad))&&(id!=null)) {
                        forma.setIdGrupo(id);
                        HandlerSeguridad.load1(forma);
                        cmd = SuperActionForm.cmdEdit;
                    } else {
                        forma = new SeguridadUserForm();
                        cmd = SuperActionForm.cmdInsert;
                    }
                    forma.setCmd(cmd);
                    putObjectSession("SeguridadForm",forma);
                } else{
                    //System.out.println("[EditUserAction] Procesando comando "+cmd);
                    processCmd(forma,request);
                }
                //System.out.println("cmd [II] = " + cmd);
                ((SeguridadUserForm)form).setCmd(cmd);
                 putObjectSession("cmd",cmd);
            }
            return goSucces();
        } catch (ApplicationExceptionChecked ae) {
            return goError(ae.getKeyError());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return goError();
    }

    public static boolean processCmd (SeguridadUserForm forma,HttpServletRequest request){
        //System.out.println("forma = " + forma.getCmd());
        boolean resp = false;
        StringBuffer mensaje = null;
        ResourceBundle rb = ToolsHTML.getBundle(request);
        if (forma.getCmd().equalsIgnoreCase(SuperActionForm.cmdInsert)){
            //System.out.println("Insertar Registro...");
            try {
                resp = HandlerSeguridad.insert1(forma);
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
                //System.out.println("Editando Registro...");
                if (HandlerSeguridad.edit1(forma)){
                    request.setAttribute("info",rb.getString("app.editOk"));
                    return true;
                }else{
                    mensaje = new StringBuffer(rb.getString("app.notEdit"));
                    mensaje.append(" ").append(HandlerDBUser.getMensaje());
                }
            } else{
                if (forma.getCmd().equalsIgnoreCase(SuperActionForm.cmdDelete)){
                    //System.out.println("Elimando registro....");
                    if (HandlerSeguridad.delete1(forma)){
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
            //System.out.println("mensaje = " + mensaje);
            request.setAttribute("info",mensaje.toString());
        }
        return false;
    }
}
