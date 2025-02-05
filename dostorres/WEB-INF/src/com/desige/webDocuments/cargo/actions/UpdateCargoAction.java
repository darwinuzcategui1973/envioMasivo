package com.desige.webDocuments.cargo.actions;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ResourceBundle;

import javax.mail.Session;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transaction;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.desige.webDocuments.area.forms.Area;
import com.desige.webDocuments.cargo.forms.Cargo;
import com.desige.webDocuments.persistent.managers.HandlerProcesosSacop;
import com.desige.webDocuments.persistent.managers.HandlerTypeDoc;
import com.desige.webDocuments.persistent.utils.HibernateUtil;
import com.desige.webDocuments.persistent.utils.JDBCUtil;
import com.desige.webDocuments.utils.ApplicationExceptionChecked;
import com.desige.webDocuments.utils.Constants;
import com.desige.webDocuments.utils.ToolsHTML;
import com.desige.webDocuments.utils.beans.SuperAction;
import com.desige.webDocuments.utils.beans.SuperActionForm;
import com.focus.qweb.dao.CargoDAO;
import com.focus.qweb.to.CargoTO;

/**
 * Created by IntelliJ IDEA.
 * User: srodriguez
 * Date: 02-ago-2006
 * Time: 14:54:09
 * To change this template use File | Settings | File Templates.
 */


/**
 * Created by IntelliJ IDEA.
 * User: Administrador
 * Date: 20/03/2006
 * Time: 10:45:52 AM
 * To change this template use File | Settings | File Templates.
 */

public class UpdateCargoAction extends SuperAction {
    public ActionForward execute(ActionMapping mapping,
                                 ActionForm form, HttpServletRequest request,
                                 HttpServletResponse response) {
        super.init(mapping,form,request,response);
        String cmd = request.getParameter("cmd");
        Cargo forma = (Cargo)form;
        request.getSession().setAttribute("otroProcesoSacop",form);
       // Transaction tx = HibernateUtil.createTransaction();
        try {
            getUserSession();
            if (ToolsHTML.checkValue(cmd)){
                if ((cmd.equalsIgnoreCase(SuperActionForm.cmdNew))||
                    (cmd.equalsIgnoreCase(SuperActionForm.cmdLoad))){
                    if (cmd.equalsIgnoreCase(SuperActionForm.cmdNew)){
                        removeObjectSession("valor");
                    }
                    form = new Cargo();
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
        } finally {
           // HibernateUtil.txCommit(tx);
        }
        return goError();
    }

     private static boolean processCmd (Cargo forma,HttpServletRequest request) throws ApplicationExceptionChecked{
        //System.out.println("forma = " + forma.getCmd());
        boolean resp = false;
        StringBuffer mensaje = null;
        ResourceBundle rb = ToolsHTML.getBundle(request);
        //colocamo esta variable pra que loadCargo la use y cargue el area que le corresponde
        request.setAttribute("idarea",String.valueOf(forma.getIdarea()));
        if (forma.getCmd().equalsIgnoreCase(SuperActionForm.cmdInsert)){
            //System.out.println("Insertar Registro...");
            try {
                 if (noExisteCargo(forma)){
                     resp = HandlerProcesosSacop.insert(forma);
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
                mensaje.append(" ").append(HandlerTypeDoc.getMensaje());
            }
        } else{
            if (forma.getCmd().equalsIgnoreCase(SuperActionForm.cmdEdit)){
                if (noExisteCargo(forma)){
                    //System.out.println("Editando Registro...");
                	CargoDAO oCargoDAO = new CargoDAO();
                	CargoTO oCargoTO = new CargoTO();
                	
                	oCargoTO.setIdCargo(null);
                	oCargoTO.setActivec(Constants.permissionSt);
                	oCargoTO.setIdArea(String.valueOf(forma.getIdarea()));

                	try {
						oCargoDAO.actualizar(oCargoTO);
					} catch (Exception e) {
						e.printStackTrace();
					}

                    request.setAttribute("info",rb.getString("app.editOk"));
                }else{
                    request.setAttribute("info",rb.getString("reg.existe"));
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

    private static boolean noExisteCargo(Cargo forma){
       Connection con;
       PreparedStatement st;
       ResultSet rs;
       boolean swNoExiste=true;
       try{
            con= JDBCUtil.getConnection(Thread.currentThread().getStackTrace()[1].getMethodName());
                StringBuffer sql = new StringBuffer("");
                sql.append("SELECT cargo FROM tbl_cargo WHERE cargo='").append(forma.getCargo().trim()).append("'");
                sql.append(" and idarea=").append(forma.getIdarea());
                sql.append(" and activec=").append(Constants.permission);
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

//    private static boolean noExisteAreaenSacop(Area forma){
//       Connection con;
//       PreparedStatement st;
//       ResultSet rs;
//       boolean swNoExiste=true;
//       try{
//            con= JDBCUtil.getConnection(Thread.currentThread().getStackTrace()[1].getMethodName());
//                StringBuffer sql = new StringBuffer("");
//                sql.append("SELECT area FROM tbl_area ta , tbl_planillasacop1 p1 WHERE ta.idarea=").append(forma.getIdarea()).append("");
//                sql.append(" AND ta.idarea=p1.usernotificado");
//                sql.append(" and p1.active=").append(Constants.permission);
//                //System.out.println("sql="+sql.toString());
//                st=con.prepareStatement(sql.toString());
//                rs=st.executeQuery();
//                if (rs.next()){
//                        swNoExiste=false;
//                }else{
//                        swNoExiste=true;
//                }
//                if (con!=null){
//                    con.close();
//                }
//                if (st!=null){
//                   st.close();
//                }
//                if (rs!=null){
//                    rs.close();
//                }
//
//
//       }catch(Exception e){
//            e.printStackTrace();
//       }
//       return swNoExiste;
//   }
//



}
