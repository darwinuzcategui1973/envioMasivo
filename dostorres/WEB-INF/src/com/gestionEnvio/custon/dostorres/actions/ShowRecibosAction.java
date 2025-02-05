package com.gestionEnvio.custon.dostorres.actions;
//package com.gestionEnvio.custon.dostorres.actions;

import java.io.File;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.desige.webDocuments.dao.ConfDocumentoDAO;
import com.desige.webDocuments.document.actions.PdfPortada;
import com.desige.webDocuments.document.forms.BaseDocumentForm;
import com.gestionEnvio.custon.dostorres.forms.BaseReciboForm;

import com.desige.webDocuments.files.forms.DocumentForm;
import com.desige.webDocuments.mail.actions.SendMailTread;
import com.desige.webDocuments.persistent.managers.HandlerBD;
import com.gestionEnvio.custon.dostorres.persistent.managers.HandlerDBRecibos;
import com.desige.webDocuments.persistent.managers.HandlerDocuments;
import com.desige.webDocuments.persistent.managers.HandlerGrupo;
import com.desige.webDocuments.persistent.managers.HandlerNorms;
import com.desige.webDocuments.persistent.managers.HandlerStruct;
import com.desige.webDocuments.persistent.managers.HandlerTypeDoc;
import com.desige.webDocuments.persistent.managers.HandlerWorkFlows;
import com.desige.webDocuments.persistent.utils.JDBCUtil;
import com.desige.webDocuments.seguridad.forms.SeguridadUserForm;
import com.desige.webDocuments.structured.forms.BaseStructForm;
import com.desige.webDocuments.utils.ApplicationExceptionChecked;
import com.desige.webDocuments.utils.Constants;
import com.desige.webDocuments.utils.ToolsHTML;
import com.desige.webDocuments.utils.beans.Search;
import com.desige.webDocuments.utils.beans.SuperAction;
import com.desige.webDocuments.utils.beans.Users;
import com.focus.qweb.facade.AuditFacade;
import com.focus.request.NormAuditRequest;
import com.focus.request.PlanAuditRequest;
import com.focus.request.ProgramAuditRequest;
/*
 * aqui despues lo quito
 */

public class ShowRecibosAction extends SuperAction {
	private static Logger log = LoggerFactory
			.getLogger("[V****DARWINUZCATEGUI*********1.0] " + ShowRecibosAction.class.getName());
	
	 
    public synchronized ActionForward execute(ActionMapping mapping,
                                 ActionForm form, HttpServletRequest request,
                                 HttpServletResponse response) {
    	super.init(mapping,form,request,response);

    	// seteamos el modulo activo
    	request.getSession().setAttribute(Constants.MODULO_ACTIVO, Constants.MODULO_AVISO);
         
    	try {
    		Users usuario = getUserSession();
            
        	//BaseDocumentForm doc = null;
        	BaseReciboForm rec = null;
            
            String toSearch = getParameter("toSearch"); //parametro de buscar
            //log.debug("[ShowRecibosAction] toSearch = " + toSearch);
           // System.out.println("[ShowRecibosAction] toSearch = ");
            //System.out.println(toSearch);
            String accion = getParameter("accion")!=null?getParameter("accion"):""; //boton buscar
            String botonWhatsapp = getParameter("botonWhatsapp")!=null?getParameter("botonWhatsapp"):""; //boton Whatsapp
            String unNombre = request.getParameter("unNombre"); //1
            String unLocal = request.getParameter("unLocal"); //2
            String version = request.getParameter("version"); 
            String aviso = request.getParameter("aviso"); //3
            String codigo = request.getParameter("codigo"); //4
            String local = request.getParameter("local"); //5
            String name = request.getParameter("nombre");
            String nombre = request.getParameter("nombre"); // 6
             //Permite buscar documentos  fecha de emision y perido
            String emisionTo= request.getParameter("emisionToHIDDEN");//8
            String emisionFrom= request.getParameter("emisionFromHIDDEN");//
            String periodo = request.getParameter("periodo");


           // System.out.println("**********parametros-----****************************");
           // System.out.println(name);
           // System.out.println(nombre);
            //System.out.println(aviso);
            //System.out.println(emisionTo);
            //System.out.println(emisionFrom);
            //System.out.println(periodo);
            
            System.out.println("******parametros------------*************************");
            
            if ("0".equalsIgnoreCase(aviso)){
                aviso=null;
            }
            //String typeDoc = request.getParameter("typeDocument");
            //String prefix = request.getParameter("prefix");
            //String normISO = request.getParameter("normISO");
            //ydavila Ticket 001-00-003265
            //String publicDoc = request.getParameter("public");
            
            
          
            request.removeAttribute("cmd");
            request.getSession().removeAttribute("cmd");
            
			// los datos para los nombre de los campos
            
			//ConfDocumentoDAO oConfDocumentoDAO = new ConfDocumentoDAO();
			//ArrayList lista = (ArrayList) oConfDocumentoDAO.findAll();
			
            removeAttribute("size");
            
            //realizamos si manda a ordenar
            HandlerDBRecibos hd = new  HandlerDBRecibos();
            String orderBy = hd.getOrdenRecibos(getParameter("ordenVersion"),getParameter("ordenNombre"),getParameter("ordenTypeDocument"),getParameter("ordenNumber"),getParameter("ordenPropietario"),getParameter("ordenApproved"));
            Collection datos = null;
           // System.out.println("------------------orden------------------");
            //System.out.println(orderBy);
            //System.out.println( getParameter("ordenNumber"));
            //System.out.println("------------------orden-----------------");
            
			request.getSession().removeAttribute("orderBy");
            //BaseDocumentForm frmReporte = new BaseDocumentForm();
			//aseReciboForm frmReporte = new BaseReciboForm();
            //Si se ejecuta una accion de buscar, pasa por aqui
			//System.out.println("*********************aqui es que carga los datos***normal*********************************************");
        	datos = hd.getRecibosCondominio(aviso,codigo,local,nombre,unNombre,unLocal,emisionFrom,emisionTo,periodo,orderBy ,rec,1);
        	System.out.println(accion);
        	System.out.println("hola ---------------------------------------pulsaste accion  ----   *** ---"+accion);
        	if ("55".equalsIgnoreCase(accion)) {
        		System.out.println("hola pulsaste 55 "+accion);
        	}
        	
            if ("1".equalsIgnoreCase(accion)) {
            //	 EnviarWhastAppTread whastApp = new EnviarWhastAppTread();
            //	 whastApp.start();
            	System.out.println("hola pulsaste accion  ----   *** ---"+accion +"*** ---");
            	datos = hd.getRecibosCondominio(aviso,codigo,local,nombre,unNombre,unLocal,emisionFrom,emisionTo,periodo,orderBy,rec,1);
        				
            }
            
        	
            
            
            removeObjectSession("recibo");
            removeObjectSession("size");
            if (datos!=null&&datos.size() > 0) {
                putObjectSession("recibo",datos);
                putObjectSession("size",new Integer(datos.size()));
            }
            
            System.out.println("***************************datos3333**********************");
            System.out.println(datos);
            System.out.println(datos.size());
            System.out.println("***************************datos3333**********************");
            putAtributte("accion",accion);
            putAtributte("botonWhatsapp",botonWhatsapp);
            putAtributte("ordenNombre",request.getParameter("ordenNombre")!=null?"checked":"");
            putAtributte("ordenNumber",request.getParameter("ordenNumber")!=null?"checked":"");
            putAtributte("ordenPropietario",request.getParameter("ordenPropietario")!=null?"checked":"");
            putAtributte("unNombre",unNombre);
            putAtributte("unLocal",unLocal);
            putAtributte("version",version);
            putAtributte("aviso",aviso);
            putAtributte("codigo",codigo);
            putAtributte("local",local);
            putAtributte("nombre",name);
            putObjectSession("moreP",request.getParameter("moreP"));
            
			//Luis Cisneros
            //Busqueda por fecha de emsion
            putAtributte("emisionFromHIDDEN",emisionFrom);
            putAtributte("emisionToHIDDEN",emisionTo);
            putAtributte("periodo",periodo);
			Connection conn = null;

			try {
				conn = JDBCUtil.getConnection(Thread.currentThread().getStackTrace()[1].getMethodName());

	            Collection<Search> allUsers = HandlerDBRecibos.getAllNombresConRecibos(conn);
	            Collection<Search> allLocales = HandlerDBRecibos.getAllLocalesConRecibos(conn);
	            Collection<BaseReciboForm> datos1 = HandlerDBRecibos.getRecibosCondominio(aviso,codigo,local,nombre,unNombre,unLocal,emisionFrom,emisionTo,periodo,orderBy,rec,1);
	            putObjectSession("allUsers",allUsers);
	            putObjectSession("allLocales",allLocales);
	            putObjectSession("recibo",datos1);
	            putObjectSession("doc",datos1);
				
	          
	           
	            System.out.println("***************************datos1**********************");
	            System.out.println(datos1);
	            System.out.println(datos1.size());
	            System.out.println(periodo);
	            System.out.println("***************************datos1**********************");
	           
	            putObjectSession("moreP",request.getParameter("moreP"));
	            
	           
	            
			} catch(Exception e) {
				e.printStackTrace();
				if(conn!=null) {
					conn.close();
				}
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
    }
