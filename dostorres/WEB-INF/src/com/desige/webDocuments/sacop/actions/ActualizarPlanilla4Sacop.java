package com.desige.webDocuments.sacop.actions;

import java.util.Locale;
import java.util.ResourceBundle;

import javax.mail.Session;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transaction;

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
 * Date: 22/12/2005
 * Time: 11:07:19 AM
 * To change this template use File | Settings | File Templates.
 */

public class ActualizarPlanilla4Sacop extends SuperAction {
    //static Logger log = LoggerFactory.getLogger(ActualizarPlanillaSacop.class.getName());
    public synchronized ActionForward execute(ActionMapping mapping,
                                 ActionForm form, HttpServletRequest request,
                                 HttpServletResponse response) {
        boolean swfaltaPorFirmarAccionUsuario=false;
        try{
            Locale defaultLocale = new Locale(DesigeConf.getProperty("language.Default"),DesigeConf.getProperty("country.Default"));
            ResourceBundle rb = ResourceBundle.getBundle("LoginBundle",defaultLocale);
            super.init(mapping,form,request,response);

            plantilla1 forma = (plantilla1)form;
            PlantillaAccion formaBD=new PlantillaAccion();
            removeAttribute("nocompletarsacop");
            String cualquierUsuarioDelaPlanilla="-1";
            byte filtroPorFirma=0;
            String accPend=LoadAccionesTomarSacopAction.accionesPendientes(cualquierUsuarioDelaPlanilla.toString(),forma.getIdplanillasacop1(), filtroPorFirma,swfaltaPorFirmarAccionUsuario);

            if (!ToolsHTML.isEmptyOrNull(accPend)){
               if (!"1".equalsIgnoreCase(accPend)){
                   putAtributte("nocompletarsacop","1");
                   swfaltaPorFirmarAccionUsuario = true;
               }
            }
             if (!swfaltaPorFirmarAccionUsuario){
                    //buscamos el numero de planilla del sacop
                    boolean swModificarBD=false;
                      //solo modifica el usuario emisor o responsable y uno de ellos le toca firmar en x momento en
                    //del satus
                    if(!ToolsHTML.isEmptyOrNull((String)getSessionObject("modificando"))){
                        if (getSessionObject("modificando").equals("1")){
                            swModificarBD=true;
                        }
                    }
                    //comentarios general extras del responsable del sacop al cerrar la sacop que esta en estado de ejecucion
                    String comm=request.getParameter("comentariosGenerales")!=null?request.getParameter("comentariosGenerales"):"";
                    if (ToolsHTML.isEmptyOrNull(comm)){
                       comm=(String)ToolsHTML.getAttribute(request,"comentariosGenerales",true);
                    }


                    PlanillaSacop1DAO oPlanillaSacop1DAO = new PlanillaSacop1DAO();
                    PlanillaSacop1TO oPlanillaSacop1TO = new PlanillaSacop1TO();
                    
                    oPlanillaSacop1TO.setIdplanillasacop1(forma.getIdplanillasacop1());
                    oPlanillaSacop1DAO.cargar(oPlanillaSacop1TO);
                    
                    if(forma.getAccionobservacion()!=null && forma.getAccionobservacion().trim().length()>0) {
                    	oPlanillaSacop1TO.setAccionobservacion(forma.getAccionobservacion());
                    }
                    oPlanillaSacop1TO.setComntresponsablecerrar("<br />"+ "<br />"+comm);
                    oPlanillaSacop1TO.setEstado(LoadSacop.edoVerificacion);
                    oPlanillaSacop1TO.setFechareal(forma.getFechaReal().replace("/","-"));
                    
                    oPlanillaSacop1DAO.actualizar(oPlanillaSacop1TO);
                      
                    //mandar mails
                    HandlerProcesosSacop.mandarMailsUsuariosdelSacop(forma.getIdplanillasacop1(),forma.getSacopnum(),"En Verificacion.");
              }

//        }catch (ApplicationExceptionChecked ae) {
//            return goError(ae.getKeyError());
        } catch (Exception ex) {
        	ex.printStackTrace();
        } finally {

        }
        if (!swfaltaPorFirmarAccionUsuario) {
            return goSucces();
        } else {
            putObjectSession("noHanFirmado","1");
            return goError("scp.nocerrarsacop");
        }
    }


}
