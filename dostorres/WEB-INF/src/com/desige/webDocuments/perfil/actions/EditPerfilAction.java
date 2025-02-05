package com.desige.webDocuments.perfil.actions;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ResourceBundle;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.desige.webDocuments.perfil.forms.PerfilActionForm;
import com.desige.webDocuments.persistent.managers.HandlerDBUser;
import com.desige.webDocuments.persistent.managers.HandlerPerfil;
import com.desige.webDocuments.persistent.utils.JDBCUtil;
import com.desige.webDocuments.utils.ApplicationExceptionChecked;
import com.desige.webDocuments.utils.Constants;
import com.desige.webDocuments.utils.ToolsHTML;
import com.desige.webDocuments.utils.beans.SuperAction;
import com.desige.webDocuments.utils.beans.Users;

/**
 * Title: EditPerfilAction.java <br/>
 * Copyright: (c) 2004 Focus Consulting C.A.<br/>
 * @author Ing. Nelson Crespo (NC)
 * @version WebDocuments v3.0
 * <br/>
 * Changes:<br/> 
 * <ul>
 *      <li>28/03/2004 (NC) Creation </li>
 </ul>
 */
public class EditPerfilAction extends SuperAction {
    public ActionForward execute(ActionMapping mappping,
                                 ActionForm form, HttpServletRequest request,
                                 HttpServletResponse response) {
        super.init(mappping,form,request,response);
        try {


            Users user = getUserSession();
            if (ToolsHTML.checkValue(user.getUser())) {
                if (form==null) {
                    form = new PerfilActionForm();
                    putObjectSession("perfil",form);
                }
                ((PerfilActionForm)form).setUser(user.getUser());
                ResourceBundle rb = ToolsHTML.getBundle(request);

                //Luis Cisneros
                //12/03/07
                //Se valida que el correo no este repetido
                //Pude hacerlo en el Handler de perfil, pero era un cambio más invasivo que aqui.
                StringBuffer check = new StringBuffer("SELECT nameUser FROM ").append(HandlerDBUser.nameUser);
                check.append(" WHERE accountActive = '").append(Constants.permission).append("'");
                check.append(" AND ( nameUser <> ? AND email='").append(((PerfilActionForm)form).getEmail()).append("')");

                Connection con = JDBCUtil.getConnection(Thread.currentThread().getStackTrace()[1].getMethodName());
                PreparedStatement st = con.prepareStatement(JDBCUtil.replaceCastMysql(check.toString()));
                st.setString(1,((PerfilActionForm)form).getUser());
                ResultSet rs = st.executeQuery();
                if (rs.next()) {
                    putAtributte("info",rb.getString("E0038"));
                    rs.close();
                    st.close();
                    con.close();
                    return mappping.findForward("noSuccess");
                }
                // Fin 12/03/07
                
                if (HandlerPerfil.save((PerfilActionForm)form)) {
                    removeObjectSession("primeravez");
                    int numRecords = Integer.parseInt(((PerfilActionForm)form).getNumRecordPages());
                    user.setNumRecord(numRecords);
                    putAtributte("info",rb.getString("app.editOk"));
                    if (getSessionObject("mustChange")!=null) {
                        removeObjectSession("mustChange");
                        putAtributte("error",rb.getString("app.editOk"));
                        return goTo("successChange");
                    }
                } else {
                    StringBuffer mensaje = new StringBuffer(rb.getString("app.notEdit"));
                    mensaje.append(HandlerPerfil.getMensaje());
                    putAtributte("info",mensaje.toString());
                }
                return goSucces();
            }
        } catch (ApplicationExceptionChecked ae) {
            ae.printStackTrace();
            return goError(ae.getKeyError());
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return goError();
    }
}
