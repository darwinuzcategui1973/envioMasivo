package com.desige.webDocuments.sacop.actions;

import java.util.Locale;
import java.util.ResourceBundle;

import javax.mail.Session;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.desige.webDocuments.persistent.managers.HandlerProcesosSacop;
import com.desige.webDocuments.persistent.utils.HibernateUtil;
import com.desige.webDocuments.sacop.forms.Plantilla1BD;
import com.desige.webDocuments.sacop.forms.PlantillaAccion;
import com.desige.webDocuments.sacop.forms.plantilla1;
import com.desige.webDocuments.utils.DesigeConf;
import com.desige.webDocuments.utils.ToolsHTML;
import com.desige.webDocuments.utils.beans.SuperAction;
import com.focus.qweb.dao.PlanillaSacop1DAO;
import com.focus.qweb.to.PlanillaSacop1TO;

/**
 * Created by IntelliJ IDEA.
 * User: srodriguez
 * Date: 20/12/2005
 * Time: 04:04:23 PM
 * To change this template use File | Settings | File Templates.
 */
/**
 * Created by IntelliJ IDEA.
 * User: srodriguez
 * Date: 16/12/2005
 * Time: 03:17:24 PM
 * To change this template use File | Settings | File Templates.
 */
public class ActualizarPlanilla3Sacop extends SuperAction {
    //static Logger log = LoggerFactory.getLogger(ActualizarPlanillaSacop.class.getName());
    public synchronized ActionForward execute(ActionMapping mapping,
                                 ActionForm form, HttpServletRequest request,
                                 HttpServletResponse response) {
        boolean exito=true;
        try{
            Locale defaultLocale = new Locale(DesigeConf.getProperty("language.Default"),DesigeConf.getProperty("country.Default"));
            ResourceBundle rb = ResourceBundle.getBundle("LoginBundle",defaultLocale);
            super.init(mapping,form,request,response);
            plantilla1 forma = (plantilla1)form;
            PlantillaAccion formaBD=new PlantillaAccion();
            //buscamos el numero de planilla del sacop
            //en caso que venga null, nos aseguramos que traiga un valor
            if (ToolsHTML.isEmptyOrNull(forma.getIdplanillasacop1())){
               forma.setIdplanillasacop1(request.getParameter("idplanillasacop3_1"));
            }
            //buscamos
//            Collection sacop= HandlerProcesosSacop.getInfResponsable("idplanillasacop1",forma.getIdplanillasacop1(),false);
//            Iterator it = sacop.iterator();
            //if(it.hasNext() ){
              //Plantilla1BD formabd = (Plantilla1BD) it.next();
            PlanillaSacop1DAO oPlanillaSacop1DAO = new PlanillaSacop1DAO();
            PlanillaSacop1TO oPlanillaSacop1TO = new PlanillaSacop1TO();
            
            oPlanillaSacop1TO.setIdplanillasacop1(forma.getIdplanillasacop1());
            oPlanillaSacop1DAO.cargar(oPlanillaSacop1TO);
            
              //Plantilla1BD formabd=null;
              
              
//              formabd=(Plantilla1BD)session.load(Plantilla1BD.class,new Long(forma.getIdplanillasacop1()));
              //si no existe fecha estimada, significa que no hay acciones tomadas ....  
              if (!ToolsHTML.isEmptyOrNull(oPlanillaSacop1TO.getFechaestimada())){

            	  oPlanillaSacop1TO.setAccionobservacion(ToolsHTML.ponerEnterInCadena(oPlanillaSacop1TO.getAccionobservacion()));
            	  oPlanillaSacop1TO.setEstado(LoadSacop.edoEnEjecucion);
            	  
            	  oPlanillaSacop1DAO.actualizar(oPlanillaSacop1TO);
            	  

                  //mandar mails
                  HandlerProcesosSacop.mandarMailsUsuariosdelSacop(forma.getIdplanillasacop1(),forma.getSacopnum(),rb.getString("scp.inejecucion"));
                  //Mandamos correos a todos los usuarios que tienen acciones
                  HandlerProcesosSacop.mandarMailsUsuariosdelSacopPorAccion(forma.getIdplanillasacop1());
                  String ir=request.getParameter("goTo")!=null?request.getParameter("goTo"):"";
             }else{
                   exito=false;
              }
           //}

//        }catch (ApplicationExceptionChecked ae) {
//            return goError(ae.getKeyError());
        } catch (Exception ex){
        	ex.printStackTrace();
        }
        if (exito){
           return goSucces();
        }else{

           return goError("scp.colocaraccion");
        }
    }



}
