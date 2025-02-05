package com.desige.webDocuments.sacop.actions;

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

import com.desige.webDocuments.persistent.managers.HandlerProcesosSacop;
import com.desige.webDocuments.persistent.managers.HandlerTypeDoc;
import com.desige.webDocuments.persistent.utils.HibernateUtil;
import com.desige.webDocuments.persistent.utils.JDBCUtil;
import com.desige.webDocuments.sacop.forms.ClasificacionPlanillasSacop;
import com.desige.webDocuments.utils.ApplicationExceptionChecked;
import com.desige.webDocuments.utils.Constants;
import com.desige.webDocuments.utils.ToolsHTML;
import com.desige.webDocuments.utils.beans.SuperAction;
import com.desige.webDocuments.utils.beans.SuperActionForm;
import com.focus.qweb.dao.ClasificacionPlanillasSacopDAO;
import com.focus.qweb.to.ClasificacionPlanillasSacopTO;

/**
 * 
 * Title: UpdateClasificacionPlanillasSacopAction.java <br>
 * Copyright: (c) 2012 Focus Consulting C.A.<br>
 * @author Ing. Felipe Rojas (FJR)
 * 
 * @version WebDocuments v4.5.2
 * <br>
 *     Changes:<br>
 * <ul>
 *     <li> 24/04/2012 (FJR) Creation </li>
 * <ul>
 */
public class UpdateClasificacionPlanillasSacopAction extends SuperAction {
	private static Logger log = LoggerFactory.getLogger(UpdateClasificacionPlanillasSacopAction.class.getName());
	
    public ActionForward execute(ActionMapping mapping, ActionForm form, 
    		HttpServletRequest request, HttpServletResponse response) {
    	
        super.init(mapping,form,request,response);
        
        String cmd = request.getParameter("cmd");
        ClasificacionPlanillasSacop forma = (ClasificacionPlanillasSacop) form;
        request.getSession().setAttribute("otroProcesoSacop",form);
        
        try {
            getUserSession();
            if (ToolsHTML.checkValue(cmd)){
                if ((cmd.equalsIgnoreCase(SuperActionForm.cmdNew))||
                    (cmd.equalsIgnoreCase(SuperActionForm.cmdLoad))){
                    if (cmd.equalsIgnoreCase(SuperActionForm.cmdNew)){
                        removeObjectSession("valor");
                    }
                    form = new ClasificacionPlanillasSacop();
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
    
    /**
     * 
     * @param forma
     * @param request
     * @return
     * @throws ApplicationExceptionChecked
     */
    private static boolean processCmd (ClasificacionPlanillasSacop forma,
    		HttpServletRequest request) throws ApplicationExceptionChecked {
        //System.out.println("forma = " + forma.getCmd());
        boolean resp = false;
        StringBuffer mensaje = null;
        ResourceBundle rb = ToolsHTML.getBundle(request);
        
        ClasificacionPlanillasSacopDAO oClasificacionPlanillasSacopDAO = new ClasificacionPlanillasSacopDAO();
        ClasificacionPlanillasSacopTO oClasificacionPlanillasSacopTO = new ClasificacionPlanillasSacopTO(forma);
        
        if (!SuperActionForm.cmdDelete.equalsIgnoreCase(forma.getCmd())&&!(noExisteClasificacion(forma))) {
            request.setAttribute("info",rb.getString("reg.existe"));
            forma.cleanForm();
            return true;
        }
        if (forma.getCmd().equalsIgnoreCase(SuperActionForm.cmdInsert)){
            //System.out.println("Insertar Registro...");
            try {
            	resp = HandlerProcesosSacop.insert(forma);
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
                if (noExisteClasificacionEnSacop(forma)){
                    //System.out.println("Editando Registro...");
                    forma.setActive((byte)1);
                    
                    oClasificacionPlanillasSacopTO = new ClasificacionPlanillasSacopTO(forma);
                    try {
						oClasificacionPlanillasSacopDAO.actualizar(oClasificacionPlanillasSacopTO);
	                    request.setAttribute("info",rb.getString("app.editOk"));
					} catch (Exception e) {
						e.printStackTrace();
					}
                }else{
                     request.setAttribute("info",rb.getString("admin.clasifNoConformidad"));
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
    
    /**
     * 
     * @param forma
     * @return
     */
    private static boolean noExisteClasificacion(ClasificacionPlanillasSacop forma){
    	Connection con = null;
    	PreparedStatement st = null;
    	ResultSet rs = null;
    	boolean swNoExiste=true;
           
    	try{
    		con= JDBCUtil.getConnection(Thread.currentThread().getStackTrace()[1].getMethodName());
    		
    		StringBuffer sql = new StringBuffer("");
    		sql.append("SELECT descripcion FROM tbl_clasificacionplanillassacop WHERE descripcion='").append(forma.getDescripcion().trim()).append("'");
    		sql.append(" and active='").append(Constants.permission).append("'");
    		//System.out.println("sql="+sql.toString());
    		st=con.prepareStatement(JDBCUtil.replaceCastMysql(sql.toString()));
    		rs=st.executeQuery();
    		if (rs.next()){
    			swNoExiste=false;
    		}else{
    			swNoExiste=true;
    		}
    	}catch(Exception e){
    		e.printStackTrace();
    	} finally {
    		try {
				rs.close();
			} catch (Exception e2) {
				// TODO: handle exception
			}
    		try {
				st.close();
			} catch (Exception e2) {
				// TODO: handle exception
			}
    		try {
				con.close();
			} catch (Exception e2) {
				// TODO: handle exception
			}
    	}
    	
    	return swNoExiste;
    }

    /**
     * 
     * @param forma
     * @return
     */
    private static boolean noExisteClasificacionEnSacop(ClasificacionPlanillasSacop forma){
    	Connection con = null;
    	PreparedStatement st = null;
    	ResultSet rs = null;
    	boolean swNoExiste=true;
        
    	try{
    		con= JDBCUtil.getConnection(Thread.currentThread().getStackTrace()[1].getMethodName());
    		StringBuffer sql = new StringBuffer("");
    		sql.append("SELECT pt1.descripcion FROM tbl_clasificacionplanillassacop pt1, tbl_planillasacop1 p1");
    		sql.append(" WHERE pt1.id=").append(forma.getId()).append("");
    		sql.append(" AND pt1.id=p1.clasificacion");
    		sql.append(" and p1.active=").append(Constants.permission);
    		log.debug("SQL: " + sql.toString());
    		
    		st = con.prepareStatement(JDBCUtil.replaceCastMysql(sql.toString()));
    		rs = st.executeQuery();
    		if (rs.next()){
    			swNoExiste=false;
    		}else{
    			swNoExiste=true;
    		}
    	}catch(Exception e){
    		e.printStackTrace();
    	} finally {
    		try {
				rs.close();
			} catch (Exception e2) {
				// TODO: handle exception
			}
    		try {
				st.close();
			} catch (Exception e2) {
				// TODO: handle exception
			}
    		try {
				con.close();
			} catch (Exception e2) {
				// TODO: handle exception
			}
    	}
    	
    	return swNoExiste;
    }
}
