package com.desige.webDocuments.sacop.actions;

import java.util.Locale;
import java.util.ResourceBundle;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transaction;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

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
 * Date: 10/01/2006
 * Time: 10:47:23 AM
 * To change this template use File | Settings | File Templates.
 */
/**
 * Created by IntelliJ IDEA.
 * User: srodriguez
 * Date: 23/12/2005
 * Time: 11:42:15 AM
 * To change this template use File | Settings | File Templates.
 */

public class ActualizarPlanilla6Sacop extends SuperAction {
    //static Logger log = LoggerFactory.getLogger(ActualizarPlanillaSacop.class.getName());
    public synchronized ActionForward execute(ActionMapping mapping,
                                 ActionForm form, HttpServletRequest request,
                                 HttpServletResponse response) {
        try{
            Locale defaultLocale = new Locale(DesigeConf.getProperty("language.Default"),DesigeConf.getProperty("country.Default"));
            ResourceBundle rb = ResourceBundle.getBundle("LoginBundle",defaultLocale);
            super.init(mapping,form,request,response);
            plantilla1 forma = (plantilla1)form;
            PlantillaAccion formaBD=new PlantillaAccion();
            boolean swModificarBD=false;
              //solo modifica el usuario emisor o responsable y uno de ellos le toca firmar en x momento en
            //del satus
            if(!ToolsHTML.isEmptyOrNull((String)getSessionObject("modificando"))){
                if (getSessionObject("modificando").equals("1")){
                    swModificarBD=true;
                }
            }
            //buscamos el numero de planilla del sacop
            
            if( swModificarBD){
            	PlanillaSacop1DAO oPlanillaSacop1DAO = new PlanillaSacop1DAO();
            	PlanillaSacop1TO oPlanillaSacop1TO = new PlanillaSacop1TO();

            	oPlanillaSacop1TO.setIdplanillasacop1(forma.getIdplanillasacop1());
            	oPlanillaSacop1DAO.cargar(oPlanillaSacop1TO);
            	
            	oPlanillaSacop1TO.setEstado(LoadSacop.edoCerrado);
            	oPlanillaSacop1TO.setAccionesEstablecidas(request.getParameter("accionesEstablecidas")!=null?request.getParameter("accionesEstablecidas"):"");
            	oPlanillaSacop1TO.setEliminarcausaraiz(request.getParameter("eliminarcausaraiz")!=null?request.getParameter("eliminarcausaraiz"):"");
            	oPlanillaSacop1TO.setEliminarcausaraiztxt(forma.getEliminarcausaraiztxt());
            	oPlanillaSacop1TO.setAccionesestablecidastxt(forma.getAccionesEstablecidastxt());

            	oPlanillaSacop1DAO.actualizar(oPlanillaSacop1TO);
            }

        } catch (Exception ex){
        	ex.printStackTrace();
        }finally{

        }
        return goSucces();
    }



}
