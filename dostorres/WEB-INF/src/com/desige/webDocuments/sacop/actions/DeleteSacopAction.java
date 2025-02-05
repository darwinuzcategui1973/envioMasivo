package com.desige.webDocuments.sacop.actions;

import java.util.ArrayList;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transaction;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.desige.webDocuments.persistent.managers.HandlerProcesosSacop;
import com.desige.webDocuments.persistent.managers.HandlerStruct;
import com.desige.webDocuments.persistent.utils.HibernateUtil;
import com.desige.webDocuments.persistent.utils.JDBCUtil;
import com.desige.webDocuments.sacop.forms.Plantilla1BD;
import com.desige.webDocuments.utils.Constants;
import com.desige.webDocuments.utils.DesigeConf;
import com.desige.webDocuments.utils.ToolsHTML;
import com.desige.webDocuments.utils.beans.SuperAction;
import com.focus.qweb.dao.PlanillaSacop1DAO;
import com.focus.qweb.to.PlanillaSacop1TO;
import com.focus.wonderware.actions.HandlerProcesosWonderWare;

import sun.jdbc.rowset.CachedRowSet;

/**
 * Created by IntelliJ IDEA.
 * User: Administrador
 * Date: 05/04/2006
 * Time: 03:53:21 PM
 * To change this template use File | Settings | File Templates.
 */

public class DeleteSacopAction extends SuperAction {
    private static Logger log = LoggerFactory.getLogger(DeleteSacopAction.class.getName());

    public synchronized ActionForward execute(ActionMapping mapping,
                                 ActionForm form, HttpServletRequest request,
                                 HttpServletResponse response) {
        Locale defaultLocale = new Locale(DesigeConf.getProperty("language.Default"),DesigeConf.getProperty("country.Default"));
        ResourceBundle rb = ResourceBundle.getBundle("LoginBundle",defaultLocale);
        super.init(mapping,form,request,response);
        Plantilla1BD formaBDS=new Plantilla1BD();
        try{
            String cmd = request.getParameter("cmd");
            //si es 1->borrarAtodos, la sacop esta en edo borrador  y se quita fisicamente de la tabla
            String borrarAtodos=request.getParameter("borrarAtodos");
            if (ToolsHTML.isEmptyOrNull(borrarAtodos)){
               borrarAtodos=(String)ToolsHTML.getAttribute(request,"borrarAtodos");
            }

            com.desige.webDocuments.sacop.forms.DeleteSacop formaBD=new com.desige.webDocuments.sacop.forms.DeleteSacop();

            //buscamos que planilla de sacop es
            String Idplanillasacop1=request.getParameter("idpdlt")!=null?request.getParameter("idpdlt"):"";
            if (ToolsHTML.isEmptyOrNull(Idplanillasacop1)){
                Idplanillasacop1=(String)ToolsHTML.getAttribute(request,"idpdlt",true);
            }
            formaBD.setIdplanillasacop1(Idplanillasacop1);

             //si es 1, la sacop esta en edo borrador y se quita fisicamente de la tabla
            if("1".equalsIgnoreCase(borrarAtodos)){
                     HandlerProcesosWonderWare handlerProcesosWonderWare = new HandlerProcesosWonderWare();
                     boolean noSeUsa=false;

                     PlanillaSacop1DAO oPlanillaSacop1DAO = new PlanillaSacop1DAO();
                     PlanillaSacop1TO oPlanillaSacop1TO = new PlanillaSacop1TO();

                     oPlanillaSacop1TO.setIdplanillasacop1(formaBD.getIdplanillasacop1());
                     oPlanillaSacop1DAO.eliminar(oPlanillaSacop1TO);

            }else{
                //es una sacop que a sido borrada y va al historicoacop que va a la tabla deletesacop
                 //buscamos el id de persona ..
                    String Idperson=request.getParameter("userdlt")!=null?request.getParameter("userdlt"):"";
                    HandlerProcesosSacop.loadDeleteSacop(formaBD);
                    
                    if(HandlerProcesosSacop.isTrackinSacop(formaBD)) {
                    	request.getSession().setAttribute("MESSAGE_TRACKING_SACOP", rb.getString("scp.messageTrackingSacopChild"));
                    } else if(HandlerProcesosSacop.isHasTrackinSacop(formaBD)) {
                    	request.getSession().setAttribute("MESSAGE_TRACKING_SACOP", rb.getString("scp.messageTrackingSacop"));
                    } else {
	                    //pregunbtamos si el suaurio anteriormente a sido borrado ..,
	                    //Si no ha sido borrado, lo borramos logicamente, lo almacenamos en la tabla tbl_deletesacop
	                    if (formaBD.getId()==0){
	                        if (ToolsHTML.isEmptyOrNull(Idperson)){
	                            Idperson=(String)ToolsHTML.getAttribute(request,"userdlt",true);
	                        }
	                        CachedRowSet listSacopTracking = HandlerProcesosSacop.listTrackinSacop(formaBD);

	                        long id = HandlerStruct.proximo("deleteSacop","deleteSacop","deleteSacop");
	                        formaBD.setId(id);
	                        formaBD.setIdplanillasacop1(Idplanillasacop1);
	                        formaBD.setIdperson(Idperson);
	                        formaBD.setActive(Constants.permissionSt);
	                        //emite la sacop, ya deja de ser borrador
	                         //HibernateUtil.saveObject(formaBD);  // no funciona con postgres ???
	                        StringBuffer query = new StringBuffer("insert into tbl_deletesacop (id, idplanillasacop1, idperson, active) values (?, ?, ?, ?)");
	                        ArrayList<Object> parametros = new ArrayList<Object>();
	                        parametros.add(formaBD.getId());
	                        parametros.add(new Integer(formaBD.getIdplanillasacop1()));
	                        parametros.add(formaBD.getIdperson());
	                        parametros.add(new Integer(formaBD.getActive()));
	                        JDBCUtil.executeUpdate(query, parametros);
	
	                        log.info("Almacenando nueva SACOP eliminada en tbl_deletesacop, id=" + formaBD.getId() + ", idperson=" + formaBD.getIdperson());
	                        
	                        
	                        
	                        if(listSacopTracking!=null) {
	                        	while(listSacopTracking.next()) {
	                        		parametros.removeAll(parametros);
	                        		id = HandlerStruct.proximo("deleteSacop","deleteSacop","deleteSacop");
			                        parametros.add(id);
			                        parametros.add(listSacopTracking.getInt("idplanillasacop1"));
			                        parametros.add(formaBD.getIdperson());
			                        parametros.add(new Integer(formaBD.getActive()));
			                        JDBCUtil.executeUpdate(query, parametros);
	                        	}
	                        }
	                        
	                        
	                        //
	                    }else{
	                        formaBD.setActive(Constants.permissionSt);
	                        formaBD.setIdperson(formaBD.getIdperson()+","+Idperson);
	                        //HibernateUtil.saveOrUpdate(formaBD); //.saveObject(formaBD);  // no funciona con postgres ???
	
	                        StringBuffer query = new StringBuffer("update tbl_deletesacop set idperson=?, active=? where id=?");
	                        ArrayList<Object> parametros = new ArrayList<Object>();
	                        parametros.add(formaBD.getIdperson());
	                        parametros.add(new Integer(formaBD.getActive()));
	                        parametros.add(formaBD.getId());
	
	                        log.info("Actualizando SACOP eliminada en tbl_deletesacop, id=" + formaBD.getId()
	                        		+ ", idperson=" + formaBD.getIdperson());
	                        JDBCUtil.executeUpdate(query, parametros);
	                    }
                    }
            }

            log.info("Return to success");
            return goSucces();
        }catch(Exception e){
               log.error(e.getMessage());
               e.printStackTrace();
        }

        log.info("Return to error");
        return goError();
    }
}
