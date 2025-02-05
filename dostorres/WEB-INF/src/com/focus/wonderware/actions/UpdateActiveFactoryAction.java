package com.focus.wonderware.actions;

import java.util.ResourceBundle;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.desige.webDocuments.persistent.managers.HandlerTypeDoc;
import com.desige.webDocuments.utils.ApplicationExceptionChecked;
import com.desige.webDocuments.utils.Constants;
import com.desige.webDocuments.utils.ToolsHTML;
import com.desige.webDocuments.utils.beans.SuperAction;
import com.desige.webDocuments.utils.beans.SuperActionForm;
import com.focus.wonderware.forms.ActiveFactory_frm;

/**
 * Created by IntelliJ IDEA.
 * User: srodriguez
 * Date: Feb 15, 2007
 * Time: 11:21:33 AM
 * To change this template use File | Settings | File Templates.
 */
public class UpdateActiveFactoryAction extends SuperAction {
    public ActionForward execute(ActionMapping mapping,
                                 ActionForm form, HttpServletRequest request,
                                 HttpServletResponse response) {
        super.init(mapping,form,request,response);
        String cmd = request.getParameter("cmd");

        HandlerProcesosWonderWare handlerProcesosWonderWare = new HandlerProcesosWonderWare();

        ActiveFactory_frm forma = (ActiveFactory_frm)form;
        try {
            getUserSession();
            if (ToolsHTML.checkValue(cmd)){
                if ((cmd.equalsIgnoreCase(SuperActionForm.cmdNew))||
                    (cmd.equalsIgnoreCase(SuperActionForm.cmdLoad))){
                    if (cmd.equalsIgnoreCase(SuperActionForm.cmdNew)){
                        removeObjectSession("valor");
                    }
                    form = new ActiveFactory_frm();
                    cmd = SuperActionForm.cmdInsert;
                } else{
                    processCmd(forma,request,handlerProcesosWonderWare);
                    cmd = SuperActionForm.cmdLoad;
                }
                ((SuperActionForm)form).setCmd(cmd);
                request.setAttribute("cmd",cmd);
            }

            return goSucces();
        } catch (ApplicationExceptionChecked ae) {
            ae.printStackTrace();
            return goError(ae.getKeyError());
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
        }                                                                                                                               
        return goError();
    }

    private  boolean processCmd (ActiveFactory_frm forma,HttpServletRequest request,HandlerProcesosWonderWare handlerProcesosWonderWare) throws ApplicationExceptionChecked{
        //System.out.println("forma = " + forma.getCmd());
        boolean resp = false;
        StringBuffer mensaje = null;
        ResourceBundle rb = ToolsHTML.getBundle(request);

        if (forma.getCmd().equalsIgnoreCase(SuperActionForm.cmdInsert)){
            //System.out.println("Insertar Registro...");
            try {
                resp = handlerProcesosWonderWare.insert(forma);

            } catch (Exception e) {
                e.printStackTrace();
            }
            if (resp){
                request.setAttribute("info",rb.getString("app.editOk"));
                forma.cleanForm();
                return true;
            } else{
                mensaje = new StringBuffer(rb.getString("app.notEdit"));
                mensaje.append(" ").append(HandlerTypeDoc.getMensaje());
            }
        } else{
            if (forma.getCmd().equalsIgnoreCase(SuperActionForm.cmdEdit)){
                    forma.setActive(Constants.permission);
                    handlerProcesosWonderWare.update(forma);
                    request.setAttribute("info",rb.getString("app.editOk"));
                    return true;
            } else{
                if (forma.getCmd().equalsIgnoreCase(SuperActionForm.cmdDelete)){
                      //System.out.println("Elimando registro....");
                      handlerProcesosWonderWare.deleted(forma);
                      forma.cleanForm();
                      request.setAttribute("info",rb.getString("app.delete"));
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
