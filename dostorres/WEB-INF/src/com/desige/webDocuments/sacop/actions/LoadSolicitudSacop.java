package com.desige.webDocuments.sacop.actions;

import java.util.ArrayList;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.TreeMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.desige.webDocuments.document.forms.BaseDocumentForm;
import com.desige.webDocuments.persistent.managers.HandlerDBUser;
import com.desige.webDocuments.persistent.managers.HandlerParameters;
import com.desige.webDocuments.persistent.managers.HandlerStruct;
import com.desige.webDocuments.utils.DesigeConf;
import com.desige.webDocuments.utils.ToolsHTML;
import com.desige.webDocuments.utils.beans.SuperAction;
import com.desige.webDocuments.utils.beans.Users;
import com.focus.qweb.dao.PlanillaSacop1DAO;
import com.focus.qweb.dao.TitulosPlanillasSacopDAO;
import com.focus.qweb.to.PlanillaSacop1TO;
import com.focus.qweb.to.TitulosPlanillasSacopTO;

/**
 * Created by IntelliJ IDEA.
 * User: Administrador
 * Date: 05/04/2006
 * Time: 03:53:21 PM
 * To change this template use File | Settings | File Templates.
 */

public class LoadSolicitudSacop extends SuperAction {
    private static Logger log = LoggerFactory.getLogger(LoadSolicitudSacop.class.getName());
    
    public synchronized ActionForward execute(ActionMapping mapping,
                                 ActionForm form, HttpServletRequest request,
                                 HttpServletResponse response) {
        Locale defaultLocale = new Locale(DesigeConf.getProperty("language.Default"),DesigeConf.getProperty("country.Default"));
        ResourceBundle rb = ResourceBundle.getBundle("LoginBundle",defaultLocale);
        super.init(mapping,form,request,response);

        Users user = (Users)request.getSession().getAttribute("user");
        
        try{
            log.info("Iniciando LoadSolicitudSacop");
            
            // cargamos el documento
   	        BaseDocumentForm forma = new BaseDocumentForm();
            forma.setIdDocument(getParameter("idDocument"));
            forma.setNumberGen(getParameter("idDocument"));

			// consultamos si tiene una sacop pendiente
			PlanillaSacop1TO oPlanillaSacop1TO = new PlanillaSacop1TO();
			oPlanillaSacop1TO.setIdDocumentRelated(forma.getNumberGen());
			
			PlanillaSacop1DAO oPlanillaSacop1DAO = new PlanillaSacop1DAO();
			
			if(oPlanillaSacop1DAO.cargarSacopPendienteDelDocumento(oPlanillaSacop1TO)) {
				request.setAttribute("id",oPlanillaSacop1TO.getIdplanillasacop1());
				request.setAttribute("numSacop",oPlanillaSacop1TO.getSacopnum());
				request.setAttribute("fechaEmision",ToolsHTML.formatDateShow(oPlanillaSacop1TO.getFechaemision(),true));
				request.setAttribute("fechaSolicitud",ToolsHTML.formatDateShow(oPlanillaSacop1TO.getFechaSacops1(),true));
				request.setAttribute("fechaOcurrencia",ToolsHTML.formatDateShow(oPlanillaSacop1TO.getFechaWhenDiscovered(),true));
				request.setAttribute("descripcion",oPlanillaSacop1TO.getDescripcion());
				
	            return goTo("created");
			} else {
				if(request.getParameter("master")!=null) {
					request.setAttribute("master", request.getParameter("master"));
				}
            
	            HandlerStruct.loadDocument(forma,true,false,null, request);
	            
	            // cargamos los datos de los origines
	            TitulosPlanillasSacopDAO oTitulosPlanillasSacopDAO = new TitulosPlanillasSacopDAO();
	            ArrayList<TitulosPlanillasSacopTO> listaOrigen = oTitulosPlanillasSacopDAO.listarActive();
	            
	            // buscamos el usuario seleccionado como destinatario de sacop
	            String responsable = HandlerParameters.PARAMETROS.getListUserAddressee();
	            TreeMap<String, Users> listaResponsable = null;
	            
	            if(!ToolsHTML.isEmptyOrNull(responsable)) {
	            	listaResponsable = new TreeMap<String, Users>();
	            	// buscamos los usuarios seleccionado
	            	String[] lista = responsable.split(",");
	            	
	            	Users usu = null;
	            	
	            	for(int i=0; i<lista.length; i++) {
	            	
		        		usu = HandlerDBUser.load(new Integer(lista[i]),true);;
		
		        		listaResponsable.put(usu.getNameUser(),usu);
	            	}
	            } else {
	            	// buscamos todos los usuarios
	            	listaResponsable = HandlerDBUser.getAllUsersMap(); 
	            }
	
	            request.setAttribute("doc", forma);
	            request.setAttribute("listaOrigen", listaOrigen);
	            request.setAttribute("listaResponsable", listaResponsable);

	            log.info("Return to success");
	            return goSucces();
			}            
                        
        }catch(Exception e){
               log.error(e.getMessage());
               e.printStackTrace();
        }
        
        log.info("Return to error");
        return goError();
    }
}
