package com.desige.webDocuments.grupo.actions;

import java.util.ResourceBundle;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.desige.webDocuments.grupo.forms.grupoForm;
import com.desige.webDocuments.persistent.managers.HandlerBD;
import com.desige.webDocuments.persistent.managers.HandlerGrupo;
import com.desige.webDocuments.utils.ApplicationExceptionChecked;
import com.desige.webDocuments.utils.ToolsHTML;
import com.desige.webDocuments.utils.beans.SuperAction;
import com.desige.webDocuments.utils.beans.SuperActionForm;

/**
 * Title: EditGrupoAction.java <br/>
 * Copyright: (c) 2004 Focus Consulting C.A.<br/>
 * @author Ing. Roger Rodríguez
 * @version WebDocuments v3.0
 * <br/>
 * Changes:<br/> 
 * <ul>
 *      <li>25/06/2004 (RR) Creation </li>
 </ul>
 */
public class EditGrupoAction extends SuperAction {
    public ActionForward execute(ActionMapping actionMapping,
                                 ActionForm form, HttpServletRequest request,
                                 HttpServletResponse response) {
        super.init(actionMapping,form,request,response);
        String cmd = request.getParameter("cmd");
        String idGrupo = request.getParameter("idGrupo");
        boolean proceso = false;
        if (cmd==null) {
            cmd = SuperActionForm.cmdLoad;
        }
        try {
            getUserSession();
            String goBack = getParameter("goBack");
            putAtributte("goBack",goBack);
            grupoForm forma = (grupoForm)form;
            if (ToolsHTML.checkValue(cmd)) {
                if ((cmd.equalsIgnoreCase(SuperActionForm.cmdNew))||
                    (cmd.equalsIgnoreCase(SuperActionForm.cmdLoad))) {
                    if (form==null) {
                        forma = new grupoForm();
                    }
                    if ((cmd.equalsIgnoreCase(SuperActionForm.cmdLoad))&&(idGrupo!=null)) {
                        forma.setIdGrupo(idGrupo);
                        HandlerGrupo.load(forma);
                        cmd = SuperActionForm.cmdEdit;
                        //System.out.println("en el edit..."+cmd);
                    } else {
                        forma.cleanForm();
                        //System.out.println("Se va a agregar un nuevo Grupo....");
                        forma = new grupoForm();
                        cmd = SuperActionForm.cmdInsert;
                    }
                    forma.setCmd(cmd);
                    request.getSession().setAttribute("newGrupo",forma);
                } else{
                    //System.out.println("[EditGrupoAction] Procesando comando "+cmd);
                    proceso = processCmd(forma,request);
                }
                //System.out.println("cmd [II] = " + cmd);
                ((grupoForm)form).setCmd(cmd);
//                request.setAttribute("cmd",cmd);
                request.getSession().setAttribute("cmd",cmd);

                if (forma.getCmd().equalsIgnoreCase(SuperActionForm.cmdDelete)){
                    return (actionMapping.findForward("successMain"));
            	}
            }
            return (actionMapping.findForward("success"));
        } catch (ApplicationExceptionChecked ae) {
            return goError(ae.getKeyError());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return (actionMapping.findForward("error"));
    }

    private static boolean processCmd (grupoForm forma,HttpServletRequest request) throws ApplicationExceptionChecked,Exception {
        //System.out.println("forma = " + forma.getCmd());
        boolean resp = false;
        StringBuffer mensaje = null;
        ResourceBundle rb = ToolsHTML.getBundle(request);
        //Si el Comando no es Eliminar...Se verifica que el Grupo No Exista
        if (SuperActionForm.cmdInsert.equalsIgnoreCase(forma.getCmd())) {
            String[] items = HandlerBD.getField(
            		new String[]{"nombreGrupo"},
            		"groupusers",
            		new String[]{"nombreGrupo","accountActive"},
            		new String[]{forma.getNombreGrupo(),"1"},
            		new String[]{"=","="},
            		new Object[]{"",new Byte("1")});
            
            if (items!=null) {
                throw new ApplicationExceptionChecked("E0058");
            }
        }

        if (SuperActionForm.cmdEdit.equalsIgnoreCase(forma.getCmd())) {
            String[] items = HandlerBD.getField(
            		new String[]{"nombreGrupo"},
            		"groupusers",
            		new String[]{"nombreGrupo","accountActive","idGrupo"},
            		new String[]{forma.getNombreGrupo(),"1",forma.getIdGrupo()},
            		new String[]{"=","=","<>"},
            		new Object[]{"",new Byte("1"),new Integer(1)});
            if (items!=null) {
                throw new ApplicationExceptionChecked("E0058");
            }
        }

        if (forma.getCmd().equalsIgnoreCase(SuperActionForm.cmdInsert)){
            //System.out.println("Insertar Registro...");
            try {
                resp = HandlerGrupo.insert(forma);
            } catch (ApplicationExceptionChecked ae) {
                throw ae;
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (resp){
                request.setAttribute("info",rb.getString("app.editOk"));
                forma.cleanForm();
                return true;
            } else{
                mensaje = new StringBuffer(rb.getString("app.notEdit"));
                mensaje.append(" ").append(HandlerGrupo.getMensaje());
            }
        } else{
            if (forma.getCmd().equalsIgnoreCase(SuperActionForm.cmdEdit)){
                //System.out.println("Editando Registro...");
                if (HandlerGrupo.edit(forma)) {
                    //System.out.println("Entro en el edit");
                    request.setAttribute("info",rb.getString("app.editOk"));
                    return true;
                }else{
                    mensaje = new StringBuffer(rb.getString("app.notEdit"));
                    mensaje.append(" ").append(HandlerGrupo.getMensaje());
                }
            } else{
                if (forma.getCmd().equalsIgnoreCase(SuperActionForm.cmdDelete)){
                    //System.out.println("Elimando registro....");
                    if (HandlerGrupo.delete(forma)){
                        forma.cleanForm();
                        request.setAttribute("info",rb.getString("app.delete"));
                    } else{
                        mensaje = new StringBuffer(rb.getString("app.notDelete"));
                        mensaje.append(" ").append(HandlerGrupo.getMensaje());
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
