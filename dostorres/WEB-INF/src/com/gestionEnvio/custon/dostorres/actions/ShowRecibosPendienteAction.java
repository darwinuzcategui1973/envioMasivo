package com.gestionEnvio.custon.dostorres.actions;

import java.sql.Connection;
import java.util.Collection;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.desige.webDocuments.persistent.utils.JDBCUtil;
import com.desige.webDocuments.utils.ApplicationExceptionChecked;
import com.desige.webDocuments.utils.Constants;
import com.desige.webDocuments.utils.beans.Search;
import com.desige.webDocuments.utils.beans.SuperAction;
import com.desige.webDocuments.utils.beans.Users;
import com.gestionEnvio.custon.dostorres.forms.BaseReciboForm;
import com.gestionEnvio.custon.dostorres.persistent.managers.HandlerDBRecibos;

public class ShowRecibosPendienteAction  extends SuperAction {
	private static Logger log = LoggerFactory
			.getLogger("[V****DARWINUZCATEGUI****Pendiente*****1.0] " + ShowRecibosPendienteAction.class.getName());
	
	 
    public synchronized ActionForward execute(ActionMapping mapping,
                                 ActionForm form, HttpServletRequest request,
                                 HttpServletResponse response) {
    	super.init(mapping,form,request,response);

    	// seteamos el modulo activo despues deteamos el modulo activo
    	request.getSession().setAttribute(Constants.MODULO_ACTIVO, Constants.MODULO_AVISO_PENDIENTE);
         
    	try {
    		Users usuario = getUserSession();
            
        	//BaseDocumentForm doc = null;
        	BaseReciboForm rec = null;
            
            String toSearch = getParameter("toSearch"); //parametro de buscar
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
            System.out.println(name);
            System.out.println(nombre);
           System.out.println(aviso);
            
            System.out.println("******parametros------------*************************");
            
            if ("0".equalsIgnoreCase(aviso)){
                aviso=null;
            }
          
            request.removeAttribute("cmd");
            request.getSession().removeAttribute("cmd");
            
			
            removeAttribute("size");
            
            //realizamos si manda a ordenar
            HandlerDBRecibos hd = new  HandlerDBRecibos();
            String orderBy = hd.getOrdenRecibos(getParameter("ordenVersion"),getParameter("ordenNombre"),getParameter("ordenTypeDocument"),getParameter("ordenNumber"),getParameter("ordenPropietario"),getParameter("ordenApproved"));
            Collection datos = null;
            
			request.getSession().removeAttribute("orderBy");
        	datos = hd.getRecibosCondominio(aviso,codigo,local,nombre,unNombre,unLocal,emisionFrom,emisionTo,periodo,orderBy ,rec,2);
        	System.out.println(accion);
        	System.out.println("hola ---------------------------------------pulsaste accion  ----   *** ---"+accion);
        	if ("55".equalsIgnoreCase(accion)) {
        		System.out.println("hola pulsaste 55 "+accion);
        	}
        	
            if ("1".equalsIgnoreCase(accion)) {
            //	 EnviarWhastAppTread whastApp = new EnviarWhastAppTread();
            //	 whastApp.start();
            	System.out.println("hola pulsaste accion  Pendiente----   *** ---"+accion);
            	 
            	datos = hd.getRecibosCondominio(aviso,codigo,local,nombre,unNombre,unLocal,emisionFrom,emisionTo,periodo,orderBy,rec,2);
            
            	System.out.println("*********************accion****cuando pasa aqui*******name*******222222*********************");
                System.out.println(name);
                System.out.println(nombre);
                System.out.println(aviso);
                System.out.println("**************accion******************name****************************");
                
          		
    					
            }
            
            
            removeObjectSession("recibo");
            removeObjectSession("size");
            if (datos!=null&&datos.size() > 0) {
                putObjectSession("recibo",datos);
                putObjectSession("size",new Integer(datos.size()));
            }
            
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
            //Busqueda por fecha de emsion
            putAtributte("emisionFromHIDDEN",emisionFrom);
            putAtributte("emisionToHIDDEN",emisionTo);
            putAtributte("periodo",periodo);

			Connection conn = null;

			try {
				conn = JDBCUtil.getConnection(Thread.currentThread().getStackTrace()[1].getMethodName());

	            Collection<Search> allUsers = HandlerDBRecibos.getAllNombresConRecibos(conn);
	            Collection<Search> allLocales = HandlerDBRecibos.getAllLocalesConRecibos(conn);
	            Collection<BaseReciboForm> datos1 = HandlerDBRecibos.getRecibosCondominio(aviso,codigo,local,nombre,unNombre,unLocal,emisionFrom,emisionTo,periodo,orderBy,rec,2);
	            putObjectSession("allUsers",allUsers);
	            putObjectSession("allLocales",allLocales);
	            putObjectSession("recibo",datos1);
	            putObjectSession("doc",datos1);
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
