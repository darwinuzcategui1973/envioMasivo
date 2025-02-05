package com.desige.webDocuments.enlaces.actions;

import java.util.ResourceBundle;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.desige.webDocuments.enlaces.forms.UrlsActionForm;
import com.desige.webDocuments.persistent.managers.HandlerBD;
import com.desige.webDocuments.persistent.managers.HandlerPerfil;
import com.desige.webDocuments.persistent.managers.HandlerUrls;
import com.desige.webDocuments.utils.ApplicationExceptionChecked;
import com.desige.webDocuments.utils.ToolsHTML;
import com.desige.webDocuments.utils.beans.SuperAction;
import com.desige.webDocuments.utils.beans.SuperActionForm;
import com.desige.webDocuments.utils.beans.Users;

/**
 * Title: LoadUrlsAction.java <br/> 
 * Copyright: (c) 2004 Focus Consulting C.A.<br/>
 * @author Ing. Nelson Crespo (NC)
 * @version WebDocuments v3.0
 * <br/>
 * Changes:<br/> 
 * <ul>
 *      <li>08/04/2004 (NC) Creation </li>
 </ul>
 */
public class EditUrlsAction extends SuperAction {
    public ActionForward execute(ActionMapping actionMapping,
                                 ActionForm form, HttpServletRequest request,
                                 HttpServletResponse response) {
        //System.out.println("[EditUrlsAction] BEGIN");
        super.init(actionMapping,form,request,response);
        try {
            Users user = getUserSession();
            String cmd = request.getParameter("cmd");
            //System.out.println("[EditUrlsAction] cmd = " + cmd);
            UrlsActionForm forma = (UrlsActionForm)form;
            if (ToolsHTML.checkValue(cmd)){
                if ((cmd.equalsIgnoreCase(UrlsActionForm.cmdNew))||
                    (cmd.equalsIgnoreCase(UrlsActionForm.cmdLoad))){
                    form = new UrlsActionForm();
                    request.getSession().setAttribute("newUrls",form);
                    cmd = UrlsActionForm.cmdInsert;
                } else{
                    forma.setUser(user.getUser());
                    processCmd(forma,request);
                }
                ((UrlsActionForm)form).setCmd(cmd);
                request.setAttribute("cmd",cmd);
            }
            return goSucces();
        } catch (ApplicationExceptionChecked ae) {
            //System.out.println("[ApplicationExceptionChecked]");
            return goError(ae.getKeyError());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return goError();
    }

    private static boolean processCmd (UrlsActionForm forma,HttpServletRequest request) throws ApplicationExceptionChecked, Exception {
        //System.out.println("forma = " + forma.getCmd());
        boolean resp = false;
        StringBuffer mensaje = null;
        ResourceBundle rb = ToolsHTML.getBundle(request);
        if (UrlsActionForm.cmdInsert.equalsIgnoreCase(forma.getCmd())) {
            String[] items = HandlerBD.getField(new String[]{"e.IdUrl"},"enlaces e,urlsusers ue",new String[]{"ue.IdUrl","ue.idUserGroup","(Url","[Name]"},
                                                new String[]{"e.idUrl",forma.getUser(),forma.getUrl(),forma.getName()},
                                                new String[]{"AND","AND","OR"},
                                                new String[]{"=","=","=","="},new Object[]{new Integer(1),"","",""});
            if (items!=null) {
                throw new ApplicationExceptionChecked("E0063");
            }
        }
        if (UrlsActionForm.cmdEdit.equalsIgnoreCase(forma.getCmd())) {
            String[] items = HandlerBD.getField(new String[]{"e.IdUrl"},"enlaces e,urlsusers ue",new String[]{"ue.IdUrl","ue.idUserGroup","(Url","[Name]","e.IdUrl"},
                                                new String[]{"e.idUrl",forma.getUser(),forma.getUrl(),forma.getName(),forma.getId()},
                                                new String[]{"AND","AND","OR","AND"},
                                                new String[]{"=","=","=","=","<>"},new Object[]{"","","","",""});
            if (items!=null) {
                throw new ApplicationExceptionChecked("E0063");
            }
        }
        if (forma.getCmd().equalsIgnoreCase(UrlsActionForm.cmdInsert)){
            //System.out.println("Insertar Registro...");
            try {
                resp = HandlerUrls.insert(forma);
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (resp){
                request.setAttribute("info",rb.getString("app.editOk"));
                forma.cleanForm();
                return true;
            } else{
                mensaje = new StringBuffer(rb.getString("app.notEdit"));
                mensaje.append("\n").append(HandlerPerfil.getMensaje());
            }
        } else{
            if (forma.getCmd().equalsIgnoreCase(UrlsActionForm.cmdEdit)){
                //System.out.println("Editando Registro...");
                if (HandlerUrls.edit(forma)){
                    request.setAttribute("info",rb.getString("app.editOk"));
                    return true;
                }else{
                    mensaje = new StringBuffer(rb.getString("app.notEdit"));
                    mensaje.append("\n").append(HandlerPerfil.getMensaje());
                }
            } else{
                if (forma.getCmd().equalsIgnoreCase(SuperActionForm.cmdDelete)){
                    //System.out.println("Elimando registro....");
                    if (HandlerUrls.delete(forma)){
                        forma.cleanForm();
                        request.setAttribute("info",rb.getString("app.delete"));
                        request.setAttribute("back","true");
                    } else{
                        mensaje = new StringBuffer(rb.getString("app.notDelete"));
                        mensaje.append("\n").append(HandlerPerfil.getMensaje());
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
