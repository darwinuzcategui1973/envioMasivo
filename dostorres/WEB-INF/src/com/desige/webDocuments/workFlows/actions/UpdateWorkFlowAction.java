package com.desige.webDocuments.workFlows.actions;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Hashtable;
import java.util.ResourceBundle;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.desige.webDocuments.document.forms.BaseDocumentForm;
import com.desige.webDocuments.persistent.managers.HandlerBD;
import com.desige.webDocuments.persistent.managers.HandlerDBUser;
import com.desige.webDocuments.persistent.managers.HandlerDocuments;
import com.desige.webDocuments.persistent.managers.HandlerParameters;
import com.desige.webDocuments.persistent.managers.HandlerStruct;
import com.desige.webDocuments.persistent.managers.HandlerWorkFlows;
import com.desige.webDocuments.structured.forms.BaseStructForm;
import com.desige.webDocuments.users.forms.LoginUser;
import com.desige.webDocuments.utils.ApplicationExceptionChecked;
import com.desige.webDocuments.utils.DesigeConf;
import com.desige.webDocuments.utils.ToolsHTML;
import com.desige.webDocuments.utils.beans.BeanNotifiedWF;
import com.desige.webDocuments.utils.beans.SuperAction;
import com.desige.webDocuments.utils.beans.Users;
import com.desige.webDocuments.workFlows.forms.ParticipationForm;
import com.focus.jndi.ldapfastbind;
import com.focus.qwds.ondemand.server.usuario.excepciones.LoginInvalidoException;

/**
 * Title: UpdateWorkFlowAction.java<br>
 * Copyright: (c) 2004 Focus Consulting C.A.<br/>
 *
 * @author Ing. Nelson Crespo (NC)
 * @version WebDocuments v3.0
 * <br>
 *     Changes:<br>
 * <ul>
 *      <li> 06-09-2004 (NC) Creation </li>
 *      <li> 12-07-2005 (SR) Se valido para eliminar los documentos aprobados y para dar mensajes de cerrar el flujo exitosamente</li>
 *      <li> 03/05/2006 La bandeja de entrada de los mails no funcionaba, era vulnerable en
 *                      caso que un usuario cambiara su mail, y en cada mail se manda el numero del documento en el titulo (SR) </li>
 *      <li> 30/06/2006 (NC) Cambios en los Documentos Vinculados </li>
 *      <li> 13/07/2006 (NC) Cambios para heredar o no los Prefijos de las Carpetas </li>
 * <ul>
 */
public class UpdateWorkFlowAction extends SuperAction {
	private static Logger log = LoggerFactory.getLogger(UpdateWorkFlowAction.class);
	
	public synchronized ActionForward execute(ActionMapping mapping,
                                 ActionForm form, HttpServletRequest request,
                                 HttpServletResponse response) {
        super.init(mapping,form,request,response);
        ResourceBundle rb = ToolsHTML.getBundle(request);
        ParticipationForm forma = null;
		try{
            Users usuario = getUserSession();
            forma = (ParticipationForm) form;
            BeanNotifiedWF beanNotified = new BeanNotifiedWF();
            BaseDocumentForm docForm = new BaseDocumentForm();
            String idDoc = String.valueOf(forma.getIdDocument());
            docForm.setIdDocument(idDoc);
            docForm.setNumberGen(idDoc);
            
            //System.out.println("usuario="+String.valueOf(request.getParameter("pass")));
            
            // ydavila Ticket - Validar check de Autenticaci�n de clave  
            Boolean validado=false;
            if ("1".equals(String.valueOf(HandlerParameters.PARAMETROS.getAutenticarflujo()))) {   
            	
            	if (HandlerParameters.getLDAPurl().equals(null) || (HandlerParameters.getLDAPurl().equals(""))){  
        			String a=ToolsHTML.encripterPass(request.getParameter("pass"));
        			String b=HandlerDBUser.getClaveUser(usuario);
            		if (a.equals(b)) {
            			System.out.println("No usa LDAP");
            			validado=true;
            		}else{
            			validado=false;
            			throw new LoginInvalidoException("badPass");
            		}
            	} else {
            			String c=ToolsHTML.encripterPass(request.getParameter("pass"));
            		    System.out.println("Usa LDAP");
            		Boolean a=ldapfastbind.Authenticate("admin",request.getParameter("pass"));           		
            		/* 
            		    if (request.getParameter("pass")!=HandlerDBUser.getClaveUser(usuario)){               
            			  if (request.getParameter("pass")==""){
            			  }else{
            			  }
            		    }
            		 */                    
            	}
            }
            //ToolsHTML.deleteVersionCache(String.valueOf(forma.getNumVersion()));
            //	ydavila
            if (validado=true){
            Hashtable tree = null;//(Hashtable)getSessionObject("tree");
            if (tree==null) {
                //Desde Aqui
//                StringBuffer idStructs = new StringBuffer(50);
//                idStructs.append("1");
//                Hashtable security =  HandlerGrupo.getAllSecurityForGroup(usuario.getIdGroup(),idStructs);
//                //Se Carga la Seguridad por Usuario Filtrando aquellos Nodos en Donde el Usuario no
//                //puede ver Carpetas
//                HandlerDBUser.getAllSecurityForUser(usuario.getIdPerson(),security,idStructs);
                Hashtable subNodos = new Hashtable();
//                tree = HandlerStruct.loadAllNodes(security,usuario.getUser(),usuario.getIdGroup(),subNodos,idStructs.toString());
                //Hasta Aqui
                tree = HandlerStruct.loadAllNodes(null,usuario.getUser(),DesigeConf.getProperty("application.admon"),
                                                  subNodos,null);
//                Hashtable security = HandlerGrupo.getAllSecurityForGroup(usuario.getIdGroup(),idStructs);
//                HandlerDBUser.getAllSecurityForUser(usuario.getIdPerson(),security,idStructs);
//                Hashtable subNodos = new Hashtable();
//                tree = HandlerStruct.loadAllNodes(security,usuario.getUser(),usuario.getIdGroup(),subNodos,idStructs.toString());
            }

            HandlerStruct.loadDocument(docForm,true,false,null, request);
//            HandlerStruct.loadDocument(docForm,true,false,tree);
//            docForm.setPrefix(ToolsHTML.getPrefixToDoc(getSession(),usuario,docForm.getIdNode()));
            String PrefixNumber = docForm.getPrefix() + docForm.getNumber();            
            String idLocation = HandlerStruct.getIdLocationToNode(tree,docForm.getIdNode());
            int minorKeep = 0;
            int mayorKeep = 0;
            if (idLocation!=null) {
                BaseStructForm localidad = (BaseStructForm)tree.get(idLocation);
                if (localidad!=null) {
                    if (ToolsHTML.isNumeric(localidad.getMinorKeep())) {
                        minorKeep = Integer.parseInt(localidad.getMinorKeep().trim());
                    }
                    if (ToolsHTML.isNumeric(localidad.getMajorKeep())) {
                        mayorKeep = Integer.parseInt(localidad.getMajorKeep().trim());
                    }
                }
            }
          //  forma.setEliminar("0");
            forma.setEnd("0");
            forma.setIdLocation(idLocation);
            forma.setIdNode(docForm.getIdNode());
                        
            //hacemos todo el proceso en la respuesta del flujo.
            //iniciamos el proceso de envio de notificaciones para el flujo en cuestion
			//ydavila login Ticket 001-00-003023
               /*getSession().setAttribute("url",
					request.getServletPath() + "?" + request.getQueryString()
					+ Constants.PARAMETER_ALLOW_SESSION_CONNECTION
					+ Constants.OPEN_URL_PETITION_IN_A_NEW_WINDOW);
                 return mapping.findForward("forcedLoginPage");*/            
            boolean flowWasProcessed = false;
            if (HandlerWorkFlows.wfuCanceled.equalsIgnoreCase(forma.getResult())){
                //vemos el tipo de flujo
            	if(HandlerWorkFlows.wfTypeAprobacion.equals(forma.getTypeWF())){
            		//sea condicional o no, si alguien rechaza un flujo de aprobacion, el mismo debe finalizar
            		//vemos si es secuencial o no, para saber a quien se le debe notificar sobre esto
            		if(! HandlerWorkFlows.wfSecuential.equals(forma.getSecuential())){
            			//no es secuencial, debemos enviarle la notificacion de rechazo a los pendientes en el flujo
            			HandlerWorkFlows.getPendingUsersInWF(forma.getIdWorkFlow(), forma.getRow(), beanNotified);
            			notifyFlowCancel(beanNotified, rb, forma, PrefixNumber);
            		} else {
            			//hacemos todo el proceso en la respuesta del flujo.
                        HandlerWorkFlows.setResponseWF(forma,beanNotified,minorKeep,mayorKeep,request,docForm.getDocRelations());
                        putAtributte("idWorkFlow",String.valueOf(forma.getIdWorkFlow()));
                        putAtributte("row", String.valueOf(forma.getRow()));
                        flowWasProcessed = true;
            		}
            		
            		//notificamos siempre al usuario originador de la cancelacion
            		LoginUser user = new LoginUser();
                    user.setUser(beanNotified.getOwner());
                    HandlerDBUser.load(user,true);
                    beanNotified.setEmail(user.getEmail());
                    notifyFlowCancel(beanNotified, rb, forma, PrefixNumber);
            	} else if(HandlerWorkFlows.wfTypeRevision.equals(forma.getTypeWF())){
            		//cuando se cancela un flujo de revision, si es condicional el mismo no continua
            		//y solo se le notifica al owner si es secuencial, o al grupo de pendientes si es no secuencial
            		if(HandlerWorkFlows.wfConditional.equals(forma.getConditional())){
            			//no es condicional, vemos si es no secuencial para notificar a los involucrados pendientes
            			if(! HandlerWorkFlows.wfSecuential.equals(forma.getSecuential())){
                			//no es secuencial, debemos enviarle la notificacion de rechazo a los pendientes en el flujo
            				HandlerWorkFlows.getPendingUsersInWF(forma.getIdWorkFlow(), forma.getRow(), beanNotified);
                			notifyFlowCancel(beanNotified, rb, forma, PrefixNumber);
                		} else {
                			//hacemos todo el proceso en la respuesta del flujo.
                            HandlerWorkFlows.setResponseWF(forma,beanNotified,minorKeep,mayorKeep,request,docForm.getDocRelations());
                            putAtributte("idWorkFlow",String.valueOf(forma.getIdWorkFlow()));
                            putAtributte("row", String.valueOf(forma.getRow()));
                            flowWasProcessed = true;
                		}
            			
            			//notificamos siempre al usuario originador de la cancelacion
                		LoginUser user = new LoginUser();
                        user.setUser(beanNotified.getOwner());
                        HandlerDBUser.load(user,true);
                        beanNotified.setEmail(user.getEmail());
                        notifyFlowCancel(beanNotified, rb, forma, PrefixNumber);
            		}
            	}
            } 
            
            if(! flowWasProcessed){
            	HandlerWorkFlows.setResponseWF(forma,beanNotified,minorKeep,mayorKeep,request,docForm.getDocRelations());
                putAtributte("idWorkFlow",String.valueOf(forma.getIdWorkFlow()));
                putAtributte("row", String.valueOf(forma.getRow()));
            
                //Se Env�a la Notificaci�n el flujo fu� respondido Aceptado � el mismo no es condicional
                if (!HandlerWorkFlows.wfuCanceled.equalsIgnoreCase(forma.getResult())||
                    (!forma.getConditional().equalsIgnoreCase(HandlerWorkFlows.wfConditional))) {//si es cero , es condicional
                    
                	//se esta continuando el proceso normal del flujo, pero si el mismo es no secuencial, no debo notificar al siguiente
                	//firmante, ya que el fue notificado de este flujo al momento de ser creado el mismo
                	if(HandlerWorkFlows.wfSecuential.equals(forma.getSecuential())){
                		log.info("El flujo es secuencial, debo indicarle al siguiente firmante que tiene un nuevo flujo el cual responder");
                		HandlerWorkFlows.notifiedUsers(rb.getString("wf.newWFTitle")+" "+PrefixNumber,
                        		rb.getString("mail.nameUser"),
                        		HandlerParameters.PARAMETROS.getMailAccount(),beanNotified.getEmail(),
                        		beanNotified.getComments());
                	} else {
                		log.info("El flujo es no secuencial, verifico si el siguiente firmante es el iniciador");
                		if(! forma.isOwner()){
                			if(beanNotified.getIdUser().equals(forma.getOwnerWF())){
                    			log.info("El siguiente firmante es el iniciador, se le notifica que debe cerrar el flujo");
                    			HandlerWorkFlows.notifiedUsers(rb.getString("wf.newWFTitle")+" "+PrefixNumber,
                                		rb.getString("mail.nameUser"),
                                		HandlerParameters.PARAMETROS.getMailAccount(),beanNotified.getEmail(),
                                		beanNotified.getComments());
                    		} else {
                    			log.info("El siguiente firmante no es el iniciador, como el flujo es no secuencial, no se le vuelve a notificar, ya que se le notifico al momento de ser creado el flujo");
                    		}
                		}
                	}
                }
            }
            
            if (!HandlerWorkFlows.wfuCanceled.equalsIgnoreCase(forma.getResult())) {
                if ("0".equalsIgnoreCase(beanNotified.getNotified())) {
                    LoginUser user = new LoginUser();
                    user.setUser(beanNotified.getOwner());
                    HandlerDBUser.load(user,true);
                    StringBuffer mensaje = new StringBuffer(100);
                    mensaje.append(rb.getString("wf.user")).append(" ").append(forma.getNameUser()).append("<br/>");
                    mensaje.append(rb.getString("wf.response")).append("<br/>");
                    mensaje.append(rb.getString("wf.overDoc")).append(" ").append(beanNotified.getNameDocument()).append("<br/>");
                    HandlerWorkFlows.notifiedUsers(rb.getString("wf.newWFTitle") + " " + PrefixNumber,
                    		rb.getString("mail.nameUser"),
                    		HandlerParameters.PARAMETROS.getMailAccount(),
                    		user.getEmail(),
                    		mensaje.toString());
                }
            }
            
            /*
            if (! HandlerWorkFlows.wfuCanceled.equalsIgnoreCase(forma.getResult())){
                //hacemos todo el proceso en la respuesta del flujo.
                //Se Env�a la Notificaci�n el flujo fu� respondido Aceptado � el mismo no es condicional
                if(HandlerWorkFlows.wfSecuential.equals(forma.getSecuential())){
            		HandlerWorkFlows.notifiedUsers(rb.getString("wf.newWFTitle")+" "+PrefixNumber,
                            rb.getString("mail.nameUser"),
                            HandlerParameters.PARAMETROS.getMailAccount(),
                            beanNotified.getEmail(),
                            beanNotified.getComments());
            	}
            }
            */
            /*
            //Se Env�a la Notificaci�n el flujo fu� respondido Aceptado � el mismo no es condicional
            if (!HandlerWorkFlows.wfuCanceled.equalsIgnoreCase(forma.getResult())||
                (!forma.getConditional().equalsIgnoreCase(HandlerWorkFlows.wfConditional))) {//si es cero , es condicional
                HandlerWorkFlows.notifiedUsers(rb.getString("wf.newWFTitle")+" "+PrefixNumber,
                                               rb.getString("mail.nameUser"),
                                               HandlerParameters.PARAMETROS.getMailAccount(),beanNotified.getEmail(),
                                               beanNotified.getComments());
            }
            if (!HandlerWorkFlows.wfuCanceled.equalsIgnoreCase(forma.getResult())) {
                if ("0".equalsIgnoreCase(beanNotified.getNotified())) {
                    LoginUser user = new LoginUser();
                    user.setUser(beanNotified.getOwner());
                    HandlerDBUser.load(user,true);
                    StringBuffer mensaje = new StringBuffer(100);
                    mensaje.append(rb.getString("wf.user")).append(" ").append(forma.getNameUser()).append("<br/>");
                    mensaje.append(rb.getString("wf.response")).append("<br/>");
                    mensaje.append(rb.getString("wf.overDoc")).append(" ").append(beanNotified.getNameDocument()).append("<br/>");
                    HandlerWorkFlows.notifiedUsers(rb.getString("wf.newWFTitle")+" " + PrefixNumber,
                                                   rb.getString("mail.nameUser"),
                                                   HandlerParameters.PARAMETROS.getMailAccount(),user.getEmail(),mensaje.toString());
                }
            } else {
                //Se env�a Notificaci�n de Cancelaci�n del Flujo de Trabajo
                    LoginUser user = new LoginUser();
                    user.setUser(beanNotified.getOwner());
                    HandlerDBUser.load(user,true);
                    StringBuffer mensaje = new StringBuffer(100);
                    mensaje.append(rb.getString("wf.user")).append(" ").append(forma.getNameUser()).append("<br/>");
                    mensaje.append(rb.getString("wf.respCanceled")).append("<br/>");
                    mensaje.append(rb.getString("wf.overDoc")).append(" ").append(beanNotified.getNameDocument()).append("<br/>");
                    HandlerWorkFlows.notifiedUsers(rb.getString("wf.canceledWFTitle")+" " +PrefixNumber,
                                                   rb.getString("mail.nameUser"),
                                                   HandlerParameters.PARAMETROS.getMailAccount(),beanNotified.getEmail(),mensaje.toString());
            }
            */
            
            removeAttribute("showEditReg");
            //SIMON 12 DE JULIO 2005 INICIO
            String str=null;
            //se le da prioridad para la eliminacion deldocumento y el mensaje a relucir
            if ("1".equalsIgnoreCase(forma.getEliminar()) ){
                  forma.setNameUser(usuario.getUser());
                  str= HandlerDocuments.deleteDocstr(forma.getIdDocument(),0,forma.getNameUser(),false);
                  return goSucces(str);
              	 //ydavila Ticket 001-00-003023  
            } else if ("1".equalsIgnoreCase(forma.getCambiar())){
            	//forma.setNameUser(usuario.getUser());  en caso de que el documento quiera ser bloqueado por el originador del FT
        		// String success = "doc.workflows";
        		String success = "doc.solCambioEnviado";
        		// se cambio subtypeWF que igual a 3 para flujo de cambio
        		String soloFlujoAprobacion ="3";
        		// solo se bloquea si es flujo se aprobracion
        		// de fujo de aprobacion forma.getTypeWF = 1 es de aprobacion
        		
            	if(HandlerWorkFlows.wfuAcepted.equalsIgnoreCase(forma.getResult()) && soloFlujoAprobacion.equalsIgnoreCase(forma.getSubtypeWF())) {
            	//if(HandlerWorkFlows.wfuAcepted.equalsIgnoreCase(forma.getResult())) {
	        		forma.setNameUser(docForm.getOwner()); // 03/10/2020 documento queda bloqueado para el propietario
	        		
	        		try {
		        		HandlerDocuments.checkDocument(String.valueOf(forma.getIdDocument()));
		        		HandlerDocuments.checkOutDocument(forma.getIdDocument(),HandlerDocuments.docCheckOut,forma.getNameUser(),docForm.getNumVer());
		        		success = "doc.bloqueadoSolCambio";
	        		} catch(ApplicationExceptionChecked e) {
	        			// el documento no se puede bloquear porque esta en flujo
	        		}
            	}
            	return goSucces(success);
              //SIMON 12 DE JULIO 2005 FIN
             }else if (forma.getEnd().equalsIgnoreCase("1")){  //se finaliza el flujo..
            	 if(forma.isPrintWF()) {
            		 if(forma.getResult().equals(HandlerWorkFlows.wfuCanceled) && forma.getStatu()==4) {
            			 str="doc.workflowsPrintReject";  // rechazado
            		 } else {
            			 str="doc.workflows";  // completado
            		 }
            	 } else {
            		 str="doc.workflows";
            	 }
            	 return goSucces(str);
           	 }else{
           		 return goSucces();
           	 }
    		}//ydavila
		} catch(LoginInvalidoException ex) {
			ex.printStackTrace();
			request.setAttribute("ERROR_LOGIN", rb.getString("badPass"));
			if(forma!=null) {
				request.setAttribute("comentariosDelUsuario", forma.getCommentsUser());
			}
			return goSucces();
        } catch (ApplicationExceptionChecked ap) {
            ap.printStackTrace();
            return goError(ap.getKeyError());
		} catch (Exception ex){
			ex.printStackTrace();
            putObjectSession("info",rb.getString("E0017"));
		}

		return goError();
	}
    
    /**
     * 
     * @param beanNotified
     * @param rb
     * @param forma
     * @param PrefixNumber
     * @throws Exception
     */
    private static void notifyFlowCancel(BeanNotifiedWF beanNotified, ResourceBundle rb, ParticipationForm forma, String PrefixNumber) 
    		throws Exception{
    	//Se env�a Notificaci�n de Cancelaci�n del Flujo de Trabajo
        StringBuffer mensaje = new StringBuffer(100);
        String str = "";
        
        try {
            str = HandlerParameters.PARAMETROS.getMsgWFCancelados();
        } catch(Exception e) {
            e.printStackTrace();
        }
        
        if (! ToolsHTML.isEmptyOrNull(forma.getCommentsUser())) {
            str += "<br />" + forma.getCommentsUser();
        }
        
        mensaje.append(str).append("<br/><br/>").append(rb.getString("wf.user")).append(" ").append(forma.getNameUser());
        mensaje.append("<br/>").append(rb.getString("wf.respCanceled")).append("<br/>");
        mensaje.append(rb.getString("wf.overDoc")).append(" ").append(beanNotified.getNameDocument()).append("<br/>");
        
        log.info("Enviando mensaje de cancelacion a: " + beanNotified.getEmail());
        
        HandlerWorkFlows.notifiedUsers(rb.getString("wf.canceledWFTitle") + " " + PrefixNumber,
        		rb.getString("mail.nameUser"),
        		HandlerParameters.PARAMETROS.getMailAccount(),
        		beanNotified.getEmail(),
        		mensaje.toString());
    }
}
