package com.desige.webDocuments.sacop.actions;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ResourceBundle;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.desige.webDocuments.persistent.managers.HandlerProcesosSacop;
import com.desige.webDocuments.persistent.managers.HandlerTypeDoc;
import com.desige.webDocuments.persistent.utils.JDBCUtil;
import com.desige.webDocuments.sacop.forms.TitulosPlanillasSacop;
import com.desige.webDocuments.utils.ApplicationExceptionChecked;
import com.desige.webDocuments.utils.Constants;
import com.desige.webDocuments.utils.ToolsHTML;
import com.desige.webDocuments.utils.beans.SuperAction;
import com.desige.webDocuments.utils.beans.SuperActionForm;
import com.focus.qweb.dao.TitulosPlanillasSacopDAO;
import com.focus.qweb.to.TitulosPlanillasSacopTO;

/**
 * Created by IntelliJ IDEA.
 * User: srodriguez
 * Date: 30/01/2006
 * Time: 02:52:12 PM
 * To change this template use File | Settings | File Templates.
 */

public class UpdateTitulosPlanillasSacopAction extends SuperAction {
    public ActionForward execute(ActionMapping mapping,
                                 ActionForm form, HttpServletRequest request,
                                 HttpServletResponse response) {
        super.init(mapping,form,request,response);
        String cmd = request.getParameter("cmd");
        TitulosPlanillasSacop forma = (TitulosPlanillasSacop)form;
        request.getSession().setAttribute("otroProcesoSacop",form);

        try {
            getUserSession();
            if (ToolsHTML.checkValue(cmd)){
                if ((cmd.equalsIgnoreCase(SuperActionForm.cmdNew))||
                    (cmd.equalsIgnoreCase(SuperActionForm.cmdLoad))){
                    if (cmd.equalsIgnoreCase(SuperActionForm.cmdNew)){
                        removeObjectSession("valor");
                    }
                    form = new TitulosPlanillasSacop();
                    cmd = SuperActionForm.cmdInsert;
                } else{
                    processCmd(forma,request);
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
        }
        return goError();
    }

    private static boolean processCmd (TitulosPlanillasSacop forma,HttpServletRequest request) throws ApplicationExceptionChecked{
        //System.out.println("forma = " + forma.getCmd());
        boolean resp = false;
        StringBuffer mensaje = null;
        ResourceBundle rb = ToolsHTML.getBundle(request);
        
        TitulosPlanillasSacopTO oTitulosPlanillasSacopTO = null;
        
        if (!SuperActionForm.cmdDelete.equalsIgnoreCase(forma.getCmd())&&!(noExisteOrigen(forma))) {
            request.setAttribute("info",rb.getString("reg.existe"));
            forma.cleanForm();
            return true;
        }
        if (forma.getCmd().equalsIgnoreCase(SuperActionForm.cmdInsert)){
            //System.out.println("Insertar Registro...");
            try {
//                 if (noExisteOrigen(forma)){
                     resp = HandlerProcesosSacop.insert(forma);
//                 }else{
//
//                 }
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
                if (noExisteOrigenEnSacop(forma)){
                    //System.out.println("Editando Registro...");
                    forma.setActive((byte)1);
                    
                    oTitulosPlanillasSacopTO = new TitulosPlanillasSacopTO(forma);
                    TitulosPlanillasSacopDAO oTitulosPlanillasSacopDAO = new TitulosPlanillasSacopDAO();
                    
                    try {
						oTitulosPlanillasSacopDAO.actualizar(oTitulosPlanillasSacopTO);
					} catch (Exception e) {
						e.printStackTrace();
					}
                    
                    request.setAttribute("info",rb.getString("app.editOk"));
                }else{
                     request.setAttribute("info",rb.getString("scp.origenensacop"));
                }
                return true;
            } else{
                if (forma.getCmd().equalsIgnoreCase(SuperActionForm.cmdDelete)){
                    //System.out.println("Elimando registro....");
                    if (HandlerProcesosSacop.delete(forma)){
                        forma.cleanForm();
                        request.setAttribute("info",rb.getString("app.delete"));
                    } else{
                        mensaje = new StringBuffer(rb.getString("app.notDelete"));
                        mensaje.append(" ").append(HandlerTypeDoc.getMensaje());
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
    private static boolean noExisteOrigen(TitulosPlanillasSacop forma){
           Connection con;
           PreparedStatement st;
           ResultSet rs;
           boolean swNoExiste=true;
           try{
                con= JDBCUtil.getConnection(Thread.currentThread().getStackTrace()[1].getMethodName());
                    StringBuffer sql = new StringBuffer("");

                    sql.append("SELECT titulosplanillas FROM tbl_titulosplanillassacop WHERE titulosplanillas='").append(forma.getTitulosplanillas().trim()).append("'");
                    sql.append(" and active='").append(Constants.permission).append("'");
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

    private static boolean noExisteOrigenEnSacop(TitulosPlanillasSacop forma){
            Connection con;
            PreparedStatement st;
            ResultSet rs;
            boolean swNoExiste=true;
            try{
                 con= JDBCUtil.getConnection(Thread.currentThread().getStackTrace()[1].getMethodName());
                     StringBuffer sql = new StringBuffer("");

                     sql.append("SELECT titulosplanillas FROM tbl_titulosplanillassacop pt1,tbl_planillasacop1 p1  WHERE pt1.numtitulosplanillas=").append(forma.getNumtitulosplanillas()).append("");
                     sql.append(" AND pt1.numtitulosplanillas=p1.origensacop");
                     sql.append(" and p1.active=").append(Constants.permission);
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
