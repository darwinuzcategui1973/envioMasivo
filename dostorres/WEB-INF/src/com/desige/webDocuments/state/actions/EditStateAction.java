package com.desige.webDocuments.state.actions;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ResourceBundle;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.desige.webDocuments.persistent.managers.HandlerBD;
import com.desige.webDocuments.persistent.managers.HandlerState;
import com.desige.webDocuments.persistent.utils.JDBCUtil;
import com.desige.webDocuments.state.forms.BaseStateForm;
import com.desige.webDocuments.state.forms.InsertStateForm;
import com.desige.webDocuments.utils.ApplicationExceptionChecked;
import com.desige.webDocuments.utils.ToolsHTML;
import com.desige.webDocuments.utils.beans.SuperAction;
import com.desige.webDocuments.utils.beans.SuperActionForm;

/**
 * Title: EditStateAction.java <br/> 
 * Copyright: (c) 2004 Focus Consulting C.A.<br/>
 * @author Ing. Nelson Crespo (NC)
 * @version WebDocuments v3.0
 * <br/>
 * Changes:<br/> 
 * <ul>
 *      <li>29/04/2004 (NC) Creation </li>
 *      <li>26/06/2006 (SR) Se valido que insertara o actualizara sin repetir el contenido del registro </li> 
 </ul>
 */
public class EditStateAction extends SuperAction {
    public ActionForward execute(ActionMapping actionMapping,
                                 ActionForm form, HttpServletRequest request,
                                 HttpServletResponse response) {
        super.init(actionMapping,form,request,response);
        String cmd = request.getParameter("cmd");
        try {
            getUserSession();
            BaseStateForm forma = (BaseStateForm)form;
            if (ToolsHTML.checkValue(cmd)){
                if ((cmd.equalsIgnoreCase(SuperActionForm.cmdNew))||
                    (cmd.equalsIgnoreCase(SuperActionForm.cmdLoad))) {
                    //System.out.println("Se va a agregar un nuevo Estado....");
                    form = new InsertStateForm();
                    request.getSession().setAttribute("insertState",form);
                    cmd = SuperActionForm.cmdInsert;
                } else{
                    //System.out.println("[EditStateAction] Procesando comando "+cmd);
                    processCmd(forma,request);
                    cmd =SuperActionForm.cmdLoad;
                }
                //System.out.println("cmd [II] = " + cmd);
                ((BaseStateForm)form).setCmd(cmd);
                request.setAttribute("cmd",cmd);
            }
            String input = request.getParameter("input");
            String value = request.getParameter("value");
            String nameForm = request.getParameter("nameForma");
            if (!ToolsHTML.isEmptyOrNull(input)&&!ToolsHTML.isEmptyOrNull(value)&&!ToolsHTML.isEmptyOrNull(nameForm)) {
                putAtributte("input",input);
                putAtributte("value",value);
                putAtributte("nameForma",nameForm);
            }
            return goSucces();
        } catch (ApplicationExceptionChecked ae) {
            ae.printStackTrace();
            return goError(ae.getKeyError());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return goError();
    }

    private static boolean processCmd (BaseStateForm forma,HttpServletRequest request) throws Exception {
        //System.out.println("forma = " + forma.getCmd());
        boolean resp = false;
        StringBuffer mensaje = null;
        ResourceBundle rb = ToolsHTML.getBundle(request);
        if (forma.getCmd().equalsIgnoreCase(SuperActionForm.cmdInsert)){
            //System.out.println("Insertar Registro...");
            try {
                if (noExisteEstado(forma)){
                     resp = HandlerState.insert(forma);
                }else{
                     request.setAttribute("info",rb.getString("reg.existe"));
                     forma.cleanForm();
                     return true;
                 }


            } catch (Exception e) {
                e.printStackTrace();
            }
            if (resp){
                request.setAttribute("info",rb.getString("app.editOk"));
                forma.cleanForm();
                return true;
            } else{
                mensaje = new StringBuffer(rb.getString("app.notEdit"));
                mensaje.append(" ").append(HandlerState.getMensaje());
            }
        } else{
            if (forma.getCmd().equalsIgnoreCase(SuperActionForm.cmdEdit)){
               if (noExisteEstado(forma)){
                    //System.out.println("Editando Registro...");
                    if (HandlerState.edit(forma)){
                        request.setAttribute("info",rb.getString("app.editOk"));
                        return true;
                    }else{
                        mensaje = new StringBuffer(rb.getString("app.notEdit"));
                        mensaje.append(" ").append(HandlerState.getMensaje());
                    }
                }else{
                     request.setAttribute("info",rb.getString("reg.existe"));
                     forma.cleanForm();
                     return true;
                 }
            }else{
                if (forma.getCmd().equalsIgnoreCase(SuperActionForm.cmdDelete)){
                    //System.out.println("Elimando registro....");
                    //Se verifica que la Ciudad no esté asociada a ningún Usuario...
                    String[] items = HandlerBD.getField(new String[]{"idPerson"},"person",new String[]{"Estado","accountActive"},
                                           new String[]{forma.getId(),"1"},new String[]{"=","="},new Object[]{"",new Integer(1)});
                    if (items!=null) {
                        throw new ApplicationExceptionChecked("E0061");
                    }
                    if (HandlerState.delete(forma)){
                        forma.cleanForm();
                        request.setAttribute("info",rb.getString("app.delete"));
                    } else{
                        mensaje = new StringBuffer(rb.getString("app.notDelete"));
                        mensaje.append(" ").append(HandlerState.getMensaje());
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

        private static boolean noExisteEstado(BaseStateForm forma){
       Connection con;
       PreparedStatement st;
       ResultSet rs;
       boolean swNoExiste=true;
       try{
            con= JDBCUtil.getConnection(Thread.currentThread().getStackTrace()[1].getMethodName());
                StringBuffer sql = new StringBuffer("");
                sql.append("SELECT NOMBRE FROM State WHERE NOMBRE='").append(forma.getName().trim()).append("'");
                //System.out.println("sql="+sql.toString());
                st=con.prepareStatement(JDBCUtil.replaceCastMysql(sql.toString()));
                rs=st.executeQuery();
                if (rs.next()){
                        swNoExiste=false;
                }else{
                        swNoExiste=true;
                }
                if (con!=null){
                    con.close();
                }
                if (st!=null){
                   st.close();
                }
                if (rs!=null){
                    rs.close();
                }


       }catch(Exception e){
            e.printStackTrace();
       }
       return swNoExiste;
   }


}

