package com.desige.webDocuments.pais.actions;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ResourceBundle;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.desige.webDocuments.pais.forms.BaseCountryForm;
import com.desige.webDocuments.persistent.managers.HandlerBD;
import com.desige.webDocuments.persistent.managers.HandlerPais;
import com.desige.webDocuments.persistent.utils.JDBCUtil;
import com.desige.webDocuments.utils.ApplicationExceptionChecked;
import com.desige.webDocuments.utils.ToolsHTML;
import com.desige.webDocuments.utils.beans.SuperAction;
import com.desige.webDocuments.utils.beans.SuperActionForm;

/**
 * Title: EditCountryAction.java   <br>
 * Copyright: (c) 2003 Consis International<br>
 * Company: Consis International (CON)<br>
 * @author Nelson Crespo (NC)
 * @version Acsel-e v2.2
 * <br>
 * Changes:<br>
 * <ul>
 *      <li> 21/04/2004 (NC) Creation </li>
 *      <li>26/06/2006 (SR) Se valido que insertara o actualizara sin repetir el contenido del registro </li>
 * <ul>
 */
public class EditCountryAction extends SuperAction {
    static Logger log = LoggerFactory.getLogger("[V4.0 FTP] " + EditCountryAction.class.getName());
    public ActionForward execute(ActionMapping actionMapping,
                                 ActionForm form, HttpServletRequest request,
                                 HttpServletResponse response) {
        super.init(actionMapping,form,request,response);
        String cmd = request.getParameter("cmd");

        try {
            getUserSession();
            BaseCountryForm forma = (BaseCountryForm)form;
            if (ToolsHTML.checkValue(cmd)){
                if ((cmd.equalsIgnoreCase(SuperActionForm.cmdNew))||
                    (cmd.equalsIgnoreCase(SuperActionForm.cmdLoad))){
                    form = new BaseCountryForm();
                    request.getSession().setAttribute("insertCountry",form);
                    cmd = SuperActionForm.cmdInsert;
                } else{
                    log.debug("[EditCountryAction] Procesando comando "+cmd);
                    processCmd(forma,request);
                    cmd =SuperActionForm.cmdLoad;
                }
                log.debug("cmd [II] = " + cmd);
                ((BaseCountryForm)form).setCmd(cmd);
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
            updateParemeters(request);
            return goSucces();
        } catch (ApplicationExceptionChecked ae) {
            ae.printStackTrace();
            return goError(ae.getKeyError());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return goError();
    }
    private static boolean processCmd (BaseCountryForm forma,HttpServletRequest request) throws Exception {
        log.debug("forma = " + forma.getCmd());
        boolean resp = false;
        StringBuffer mensaje = null;
        ResourceBundle rb = ToolsHTML.getBundle(request);
        if (forma.getCmd().equalsIgnoreCase(SuperActionForm.cmdInsert)){
            log.debug("Insertar Registro...");
            try {
                if (noExistePais(forma)){
                    resp = HandlerPais.insert(forma);
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
                mensaje.append(" ").append(HandlerPais.getMensaje());
            }
        } else{
            if (forma.getCmd().equalsIgnoreCase(SuperActionForm.cmdEdit)){
               if (noExistePais(forma)){
                    log.debug("Editando Registro...");
                    if (HandlerPais.edit(forma)){
                        request.setAttribute("info",rb.getString("app.editOk"));
                        return true;
                    }else{
                        mensaje = new StringBuffer(rb.getString("app.notEdit"));
                        mensaje.append(" ").append(HandlerPais.getMensaje());
                    }
               }else{
                     request.setAttribute("info",rb.getString("reg.existe"));
                     forma.cleanForm();
                     return true;
                 }
            } else{
                if (forma.getCmd().equalsIgnoreCase(SuperActionForm.cmdDelete)){
                    log.debug("Elimando registro....");
                    //Se verifica que la Ciudad no esté asociada a ningún Usuario...
                    String[] items = HandlerBD.getField(new String[]{"idPerson"},"person",new String[]{"CodPais","accountActive"},
                                           new String[]{forma.getId(),"1"},new String[]{"=","="},new Object[]{"",new Integer(1)});
                    if (items!=null) {
                        throw new ApplicationExceptionChecked("E0060");
                    }
                    if (HandlerPais.delete(forma)){
                        forma.cleanForm();
                        request.setAttribute("info",rb.getString("app.delete"));
                    } else{
                        mensaje = new StringBuffer(rb.getString("app.notDelete"));
                        mensaje.append(" ").append(HandlerPais.getMensaje());
                    }
                }
            }
        }
        if (mensaje!=null){
            log.debug("mensaje = " + mensaje);
            request.setAttribute("info",mensaje.toString());
        }
        return false;
    }
    private static boolean noExistePais(BaseCountryForm forma){
      Connection con;
      PreparedStatement st;
      ResultSet rs;
      boolean swNoExiste=true;
      try{
           con= JDBCUtil.getConnection(Thread.currentThread().getStackTrace()[1].getMethodName());
               StringBuffer sql = new StringBuffer("");
               sql.append("SELECT NOMBRE FROM pais WHERE NOMBRE='").append(forma.getName().trim()).append("'");
               log.debug("sql="+sql.toString());
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
