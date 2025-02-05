package com.gestionEnvio.custon.dostorres.actions;
import java.util.ResourceBundle;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.desige.webDocuments.persistent.managers.HandlerProcesosSacop;
import com.desige.webDocuments.utils.Constants;
import com.desige.webDocuments.utils.ToolsHTML;
import com.desige.webDocuments.utils.beans.SuperAction;
import com.desige.webDocuments.utils.beans.Users;
import com.focus.custom.delsur.DelSurBatchProcess;
import com.gestionEnvio.custon.dostorres.forms.BaseReciboForm;
import com.gestionEnvio.custon.dostorres.persistent.managers.HandlerDBRecibos;

public class EnviarReciboAction extends SuperAction {
	private static final Logger log = LoggerFactory.getLogger(EnviarReciboAction.class);
	public synchronized ActionForward execute(ActionMapping mapping,
            ActionForm form, HttpServletRequest request,
            HttpServletResponse response) {
super.init(mapping,form,request,response);

		// TODO Auto-generated method stub
		log.info("Se va a ejecutar el envio Recibos");
		
		ResourceBundle rb = ToolsHTML.getBundle(request);
		request.getSession().setAttribute(Constants.MODULO_ACTIVO, Constants.MODULO_AVISO_PENDIENTE);
		request.setAttribute("info", rb.getString("app.editOk"));
		//los atributos
//		Users usuario = getUserSession();
        
    	//BaseDocumentForm doc = null;
    	BaseReciboForm rec = null;
        
     //   String toSearch = getParameter("toSearch"); //parametro de buscar
        //log.debug("[ShowRecibosAction] toSearch = " + toSearch);
       // System.out.println("[ShowRecibosAction] toSearch = ");
        //System.out.println(toSearch);
  //      String accion = getParameter("accion")!=null?getParameter("accion"):""; //boton buscar
        String unNombre = request.getParameter("unNombre"); //1
        String unLocal = request.getParameter("unLocal"); //2
        String version = request.getParameter("version"); 
        String aviso = request.getParameter("aviso"); //3
        String codigo = request.getParameter("codigo"); //4
        String local = request.getParameter("local"); //5
        String name = request.getParameter("nombre");
        String nombre = request.getParameter("nombre"); // 6
        String botonWhastsapp2 = getParameter("botonWhastsapp2")!=null?getParameter("botonWhastsapp2"):""; //boton Whatsapp
      ////Luis Cisneros
        //Permite buscar documentos  fecha de expiracion
        String emisionTo= request.getParameter("emisionToHIDDEN");//8
        String emisionFrom= request.getParameter("emisionFromHIDDEN");//
        String periodo = request.getParameter("periodo");


        System.out.println("**********parametros-----*RNVIARrECIBO***************************");
        System.out.println(botonWhastsapp2);
        System.out.println(local);
        System.out.println(nombre);
        System.out.println(aviso);
        System.out.println(emisionTo);
        System.out.println(emisionFrom);
        System.out.println(periodo);
        System.out.println(unNombre);
        System.out.println(unLocal);
        System.out.println("**********parametros-----*RNVIARrECIBO***************************");
        
        System.out.println("******parametros------------*************************");
        
        if ("0".equalsIgnoreCase(aviso)){
            aviso=null;
        }
         if ("2".equalsIgnoreCase(botonWhastsapp2)) {
        	//envio whastsapp
        	System.out.println("enviar whatsapp" );
        	 
        	
        	HandlerDBRecibos.mandarWhatsAppDarwin(aviso, codigo, local, nombre, unNombre, unLocal, emisionFrom, emisionTo, periodo,1); 
        } else {
        	System.out.println("enviar mail" );
        	HandlerDBRecibos.mandarMailsDarwin(aviso, codigo, local, nombre, unNombre, unLocal, emisionFrom, emisionTo, periodo,1); 
        }
        //00000001927
		//public static void mandarMailsDarwin(String avisoPRM, String codcliPRM, String localPRM,String nombrePRM,String unNombrePRM,String unLocalPRM,String emisionFromPRM,String emisionToPRM,String periodoPRM) {
        putAtributte("unNombre",unNombre);
        putAtributte("unLocal",unLocal);
        putAtributte("version",version);
        putAtributte("aviso",aviso);
        putAtributte("codigo",codigo);
        putAtributte("local",local);
        putAtributte("nombre",name);
        putAtributte("botonWhastsapp2",botonWhastsapp2);
        
		//Luis Cisneros
        //Busqueda por fecha de emsion
        putAtributte("emisionFromHIDDEN",emisionFrom);
        putAtributte("emisionToHIDDEN",emisionTo);
        putAtributte("periodo",periodo);
        
        
		log.info("Fue ejecutado accion");
		
		if(request.getParameter("nS") != null){
			return mapping.findForward("successNoSession");			
		} else {
			return mapping.findForward("success");
		}
	}
}



