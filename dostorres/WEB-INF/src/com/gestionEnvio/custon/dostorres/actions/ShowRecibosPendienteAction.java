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
            	 
            	//otro hilo de ejecucion;
            	//String resul = hd.sendMessageWhasat("https://gate.whapi.cloud/messages/text");
            	//System.out.println(resul);
            	//System.out.println("*********************accion es uno pulso el boton buscar*********************************************");	
            	//System.out.println(accion);
            	//rec = new BaseDocumentForm();
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
            
            //System.out.println("***************************datos3333**********************");
            //System.out.println(datos);
            //System.out.println(datos.size());
            //System.out.println("***************************datos3333**********************");
            putAtributte("accion",accion);
            putAtributte("botonWhatsapp",botonWhatsapp);
            //putAtributte("queryReporte",frmReporte.getQueryReporte());
            //putAtributte("idRout",request.getParameter("idRout")!=null?idRout:"");
            //putAtributte("nameRout",request.getParameter("nameRout")!=null?nameRout:"");
            //putAtributte("ordenApproved",request.getParameter("ordenApproved")!=null?"checked":"");
            //putAtributte("ordenVersion",request.getParameter("ordenVersion")!=null?"checked":"");
            putAtributte("ordenNombre",request.getParameter("ordenNombre")!=null?"checked":"");
            //putAtributte("ordenTypeDocument",request.getParameter("ordenTypeDocument")!=null?"checked":"");
            putAtributte("ordenNumber",request.getParameter("ordenNumber")!=null?"checked":"");
            putAtributte("ordenPropietario",request.getParameter("ordenPropietario")!=null?"checked":"");
            putAtributte("unNombre",unNombre);
            putAtributte("unLocal",unLocal);
            putAtributte("version",version);
            putAtributte("aviso",aviso);
            putAtributte("codigo",codigo);
            putAtributte("local",local);
            putAtributte("nombre",name);
            //putAtributte("typeDocument",typeDoc);
            //putAtributte("public",publicDoc);
            //putAtributte("prefix",prefix);
            //putAtributte("normISO",normISO);
            putObjectSession("moreP",request.getParameter("moreP"));
        // aqui veo
            //putAtributte("comment",comment);
            //putAtributte("owner",owner);
            //putAtributte("idProgramAudit",idProgramAudit);
            //putAtributte("idPlanAudit",idPlanAudit);
            
            // atributos que estaban en la pagina
            /*
        	request.getSession().setAttribute("isAttached",ToolsHTML.getAttachedField()!=null && !ToolsHTML.getAttachedField().equals(""));
        	request.getSession().setAttribute("isAttachedField0",ToolsHTML.getAttachedField()!=null && ToolsHTML.getAttachedField().equals("0"));
        	request.getSession().setAttribute("isAttachedField1",ToolsHTML.getAttachedField()!=null && ToolsHTML.getAttachedField().equals("1"));
            */
            
			//Luis Cisneros
            //Busqueda por fecha de emsion
            putAtributte("emisionFromHIDDEN",emisionFrom);
            putAtributte("emisionToHIDDEN",emisionTo);
            putAtributte("periodo",periodo);
/*
			if(lista!=null && doc!=null) {
				DocumentForm obj = null;
				for(int i=0;i<lista.size();i++) {
					obj = (DocumentForm)lista.get(i);
		            putAtributte(obj.getId(),doc.get(obj.getId().toUpperCase()));
				}
			}
*/			
            // datos de los programas
            /*
            AuditFacade oAuditFacade = new AuditFacade(request);
            List<ProgramAuditRequest> listProgram = oAuditFacade.listarProgramFacade(false); 
            List<PlanAuditRequest> listPlan = oAuditFacade.listarPlanFacade();
            List<NormAuditRequest> listNormasCero = oAuditFacade.listarNormaCeroFacade();
            putObjectSession("listProgramAudit",listProgram);
            putObjectSession("listPlanAudit",listPlan);
            putObjectSession("listNormasCeroAudit",listNormasCero);
            request.setAttribute("listProgramAudit",listProgram);
            request.setAttribute("listPlanAudit",listPlan);
            request.setAttribute("listNormasCeroAudit",listNormasCero);
            */



			Connection conn = null;

			try {
				conn = JDBCUtil.getConnection(Thread.currentThread().getStackTrace()[1].getMethodName());

	          /*
				//Sim�n 31 mayo 2005 inicio
	            String idNodeDocument = HandlerBD.getField(conn, "numGen","documents","owner",usuario.getUser(),"=",HandlerBD.typeString,Thread.currentThread().getStackTrace()[1].getMethodName());
	            Hashtable tree = (Hashtable)getSessionObject("tree");
	            tree = ToolsHTML.checkTree(tree,usuario);
	            if (tree!=null) {
	                putObjectSession("tree",tree);
	            }
	            Enumeration e = tree.keys();
	            if (e.hasMoreElements()) {
	                idNodeDocument = String.valueOf(e.nextElement());
	            }
	            removeObjectSession("showCharge");
	            String idLocation = HandlerStruct.getIdLocationToNode(tree,idNodeDocument);
	            if (!ToolsHTML.isEmptyOrNull(idLocation)) {
	                 BaseStructForm location = HandlerStruct.loadStruct(conn, idLocation);
	                 if (location!=null) {
	                     if (location.getShowCharge()==1) {
	                         putObjectSession("showCharge","true");
	                     }
	                 }
	            }
	            */
	            Collection<Search> allUsers = HandlerDBRecibos.getAllNombresConRecibos(conn);
	            Collection<Search> allLocales = HandlerDBRecibos.getAllLocalesConRecibos(conn);
	            Collection<BaseReciboForm> datos1 = HandlerDBRecibos.getRecibosCondominio(aviso,codigo,local,nombre,unNombre,unLocal,emisionFrom,emisionTo,periodo,orderBy,rec,2);
	            //Collection typesDocuments = HandlerTypeDoc.getAllTypeDocs(null,true);//HandlerWorkFlows.getAllTypesDocuments();
	            //putObjectSession("typesDocuments",typesDocuments);
	            putObjectSession("allUsers",allUsers);
	            putObjectSession("allLocales",allLocales);
	            putObjectSession("recibo",datos1);
	            putObjectSession("doc",datos1);
				
	          
	            /*
	            System.out.println("***************************datos1**********************");
	            System.out.println(datos1);
	            System.out.println(datos1.size());
	            System.out.println(periodo);
	            System.out.println("***************************datos1**********************");
	            */
	            //putObjectSession("TypeStatuSession",TypeStatuSession);
	            putObjectSession("moreP",request.getParameter("moreP"));
	            
	            //Validacion para determinar si aparece o no link desde Publicados
	            //removeObjectSession("showLink");
	        	//SeguridadUserForm forma = new SeguridadUserForm();
	        	//SeguridadUserForm securityForGroup = new SeguridadUserForm();
	        	//HandlerGrupo.getFieldUser(conn, forma, "seguridaduser", true, usuario.getUser());
	        	//HandlerGrupo.getFieldUser(conn, securityForGroup, "seguridadgrupo", false, usuario.getIdGroup());
	        	//String showLink = "false";
	        	/*
	        	if (forma.getEstructura() == 0) {
	        		showLink = "true";
	        	} else {
	        		if (forma.getEstructura() == 2) {
	        			if (securityForGroup.getEstructura() == 0) {
	        				showLink = "true";
	        			}
	        		}
	        	}
	            putObjectSession("showLink",showLink);
	                        
	           // putObjectSession("doc",doc);
	            * */
	           
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
