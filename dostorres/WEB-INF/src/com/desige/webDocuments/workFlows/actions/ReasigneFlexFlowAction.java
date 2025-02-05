package com.desige.webDocuments.workFlows.actions;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.ResourceBundle;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.desige.webDocuments.document.forms.BaseDocumentForm;
import com.desige.webDocuments.persistent.managers.HandlerDBUser;
import com.desige.webDocuments.persistent.managers.HandlerDocuments;
import com.desige.webDocuments.persistent.managers.HandlerParameters;
import com.desige.webDocuments.persistent.managers.HandlerWorkFlows;
import com.desige.webDocuments.to.UserFlexWorkFlowsTO;
import com.desige.webDocuments.users.forms.LoginUser;
import com.desige.webDocuments.utils.ApplicationExceptionChecked;
import com.desige.webDocuments.utils.ToolsHTML;
import com.desige.webDocuments.utils.beans.BeanNotifiedWF;
import com.desige.webDocuments.utils.beans.SuperAction;
import com.desige.webDocuments.utils.beans.Users;
import com.desige.webDocuments.workFlows.forms.DataUserWorkFlowForm;
import com.desige.webDocuments.workFlows.forms.ParticipationForm;

/**
 * Title: ReasigneFlexFlowAction.java <br/>
 * Copyright: (c) 2007 Focus Consulting<br/>
 *
 * @author Focus
 * @version WebDocuments v4.0
 * <br/>
 *       Changes:<br/>
 * <ul>
 *       <li> 03/07/2007 (YS) Creation </li>
 * </ul>
 */
public class ReasigneFlexFlowAction extends SuperAction {
    static Logger log = LoggerFactory.getLogger(ReasigneFlexFlowAction.class.getName());
    public synchronized ActionForward execute(ActionMapping mapping,
                                 ActionForm form, HttpServletRequest request,
                                 HttpServletResponse response) {
    	log.debug("[BEGIN ReasigneFlexFlowAction]");
        super.init(mapping,form,request,response);
        ResourceBundle rb = ToolsHTML.getBundle(request);
        try {
            Users usuario = getUserSession();
			ParticipationForm forma = (ParticipationForm) form;
            BeanNotifiedWF beanNotified = new BeanNotifiedWF();
            BaseDocumentForm docForm = new BaseDocumentForm();
            
            String idDoc = String.valueOf(forma.getIdDocument());
            String idFlexWF = String.valueOf(forma.getIdUser());
            String idAct = String.valueOf(forma.getIdAct());
            String typeReasigne = String.valueOf(forma.getTypeReasigne());
            String IDFlow = String.valueOf(forma.getIdFlexFlow());
            String comments = String.valueOf(forma.getCommentsUser());
            
            long idWorkFlow = 0; 	// valor de actividad donde se va a reiniciar el flujo
            long IDFlexWF = 0; 		// valor de id de suaurio donde se va a reiniciar el flujo
            long IDFlexFlow = 0; 	//valor de id del FTP que se esta procesando
            
            StringBuffer result = new StringBuffer();
            StringBuffer statu = new StringBuffer();
            ArrayList usuarios = new ArrayList();
            
			DataUserWorkFlowForm formaWF = new DataUserWorkFlowForm();
			formaWF.setIdDocument(Integer.parseInt(idDoc));
			HandlerDocuments.loadDataDocument(formaWF);            
                        
            /*
            //System.out.println("--idUser seleccionado: " + idFlexWF);
            //System.out.println("--idAct seleccionado: " + idAct);
            //System.out.println("--typeReasigne: " + typeReasigne);
            //System.out.println("--IDFlow FTP: " + IDFlow);
            //System.out.println("--comments: " + comments);
            */
            /*
            docForm.setIdDocument(idDoc);
            docForm.setNumberGen(idDoc);
            
            Hashtable tree = (Hashtable)getSessionObject("tree");
            if (tree==null) {
//                Hashtable security = HandlerGrupo.getAllSecurityForGroupH(usuario.getIdGroup(),null);
//                HandlerDBUser.getAllSecurityForUserH(usuario.getIdPerson(),security);
//                tree = HandlerStruct.loadAllNodesH(security,usuario.getIdGroup());
                Users usr = new Users();
                usr.setIdGroup(DesigeConf.getProperty("application.admon"));
                usr.setUser(usuario.getUser());
                tree = ToolsHTML.checkTree(null,usr);
            }
            HandlerStruct.loadDocument(docForm,true,false,tree);
            String idLocation = HandlerStruct.getIdLocationToNode(tree,docForm.getIdNode());
            int minorKeep = 0;
            int mayorKeep = 0;
            if (idLocation!=null) {
//                BaseStructForm localidad = (BaseStructForm)tree.get(new Long(idLocation));
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
            forma.setEliminar("0");
            forma.setEnd("0");
            forma.setIdLocation(idLocation);
            forma.setIdNode(docForm.getIdNode());
            */
            
            //Se guardan los comentarios por el rechazo del usuario
            HandlerWorkFlows.saveResponseUser(forma, HandlerWorkFlows.wfuPending, HandlerWorkFlows.rechazado);
            //System.out.println("---- se salva respuesta de rechazo");
            try{
            	IDFlexFlow = Long.parseLong(IDFlow);
            }catch(Exception e){
            	log.debug("El valor IDFlow no se puede convertir en long: " + IDFlow + ", error: " + e);
            }
            
            try{
            	idWorkFlow = Long.parseLong(idAct);
            }catch(Exception e){
            	log.debug("El valor idAct no se puede convertir en long: " + idAct + ", error: " + e);
            }

            try{
            	IDFlexWF = Long.parseLong(idFlexWF);
            }catch(Exception e){
            	log.debug("El valor idFlexWF no se puede convertir en long: " + idFlexWF + ", error: " + e);
            }
            
            //SE DEFINE SI SE REINICIA EN UNA ACTIVIDAD O USUARIO
            if(!ToolsHTML.isEmptyOrNull(typeReasigne)){
            	statu.append(HandlerWorkFlows.wfuPending+",");
            	statu.append(HandlerWorkFlows.wfuQueued+",");
            	statu.append(HandlerWorkFlows.wfuAcepted);
            	
            	result.append(HandlerWorkFlows.pending+",");
            	result.append(HandlerWorkFlows.response+",");
            	result.append(HandlerWorkFlows.inQueued+",");
            	result.append(HandlerWorkFlows.rechazado);
            	
            	if(typeReasigne.compareTo("1")==0){
            		//SE OBTIENEN TODOS LOS USUARIOS DE ESA ACTIVIDAD Y LAS SIGUIENTES	
            		usuarios = HandlerWorkFlows.getUsersFlexWorkFlowsByIdWorkFlow(">=",idWorkFlow, statu.toString(), IDFlexFlow);
            		//System.out.println("---- SE OBTIENEN TODOS LOS USUARIOS DE ESA ACTIVIDAD Y LAS SIGUIENTES");

            		//Se anulan todos los status que estan pendiente, respondido, en cola y el rechazado (EL RESULTADO SE MANTIENE) 
                	//para el FTP con identificador IDFlexFlow y a usuarios cuyo idWorkFlow es mayor o igual a la actividad seleccionada
                	//System.out.println("---- Se anulan todos los status que estan pendiente, respondido, en cola y el rechazado");
                	HandlerWorkFlows.updateStatuUserFlexFlows(IDFlexFlow, HandlerWorkFlows.wfAnnulled, result.toString(),">=",idWorkFlow,1);
                	            		
            	}else if(typeReasigne.compareTo("2")==0){
            		//SE OBTIENEN TODOS LOS USUARIOS CON ID MAYOR O IGUAL AL ESTE
            		//System.out.println("---- SE OBTIENEN TODOS LOS USUARIOS CON ID MAYOR O IGUAL AL ESTE");
            		usuarios = HandlerWorkFlows.getUsersFlexWorkFlowsByIdFlexWF(">=",IDFlexWF, statu.toString(), IDFlexFlow);

	            	//Se anulan todos los status que estan pendiente, respondido, en cola y el rechazado (EL RESULTADO SE MANTIENE) 
	            	//para el FTP con identificador IDFlexFlow y a usuarios cuyo IDFlexWF es mayor o igual al seleccionado
            		//Se coloca wfactive en cero
	            	//System.out.println("---- Se anulan todos los status que estan pendiente, respondido, en cola y el rechazado");
	            	HandlerWorkFlows.updateStatuUserFlexFlows(IDFlexFlow, HandlerWorkFlows.wfAnnulled, result.toString(),">=",IDFlexWF,2);
            	}
            	
            	//SE DUPLICAN USUARIOS
            	//System.out.println("---- SE DUPLICAN USUARIOS");
            	HandlerWorkFlows.addUsersFlexWorkFlows(usuarios);
            	
            	//Se actualiza status de usuarios duplicados como pendiente o en cola dependiendiendo si la actividad es secuencial o no
            	//System.out.println("---- Se actualiza status de usuarios duplicados como pendiente o en cola");
            	HandlerWorkFlows.updateStatuUsersFlexWorkFlows(usuarios, IDFlexFlow);
            	
            	//A partir de la actividad seleccionada se reinician todas las actividades del FTP con status = 6, result = 1
            	//La actividad seleccionada se coloca con status = 1
            	//System.out.println("---- A partir de la actividad seleccionada se reinician todas las actividades del FTP con status = 6, result = 1");
            	HandlerWorkFlows.reInitActFlexFlow(IDFlexFlow,true,">=",idWorkFlow, false);
            	
            	//SE ENVIA CORREO A TODOS LOS USUARIOS QUE ESTAN CON STATU = 1 (PENDIENTE) DEL FTP
            	//System.out.println("---- SE ENVIA CORREO A TODOS LOS USUARIOS QUE ESTAN CON STATU = 1");
            	usuarios = new ArrayList();
            	usuarios = HandlerWorkFlows.getUsersFlexWorkFlowsByIdFlexWF(">=",IDFlexWF, HandlerWorkFlows.pending, IDFlexFlow);
            	if(usuarios!=null && usuarios.size()>0){
            		for (Iterator iterator = usuarios.iterator(); iterator.hasNext();) {
        				UserFlexWorkFlowsTO dato = (UserFlexWorkFlowsTO) iterator.next();
        				//Con el login se consulta email del usuario
        				LoginUser oLoginUser = new LoginUser();
        				oLoginUser.setUser(dato.getIdUser());
        				HandlerDBUser.load(oLoginUser, true);
        				String email = oLoginUser.getEmail();
        				//System.out.println("---- SE ENVIA CORREO A "+dato.getIdUser() + " email: " + email);
            			StringBuffer msg = new StringBuffer("");
            			String prefijo = "";
            			String number = "";
            			String prefixNumber = "";
            			prefijo = ToolsHTML.isEmptyOrNull(formaWF.getPrefix())?"":formaWF.getPrefix();
            			number = ToolsHTML.isEmptyOrNull(formaWF.getNumber())?"":formaWF.getNumber();
            			prefixNumber = prefijo + number;
            			
            			msg.append(getMessage("wf.reasigned")).append(" ");
            			msg.append(prefixNumber).append("<br/>");
            			msg.append(getMessage("wf.reasignedName")).append(" ").append(formaWF.getNameDocument()).append("<br/>");
            			msg.append(getMessage("wf.reasignedUser")).append(" ").append(usuario.getNamePerson()).append(" ").append(getMessage("wf.reasignedAgain")).append("<br/>");
            			msg.append(getMessage("wf.reasignedCause")).append(": ").append(comments);            			
            			//System.out.println("---MENSAJE: " + msg);
            			HandlerWorkFlows.notifiedUsers(rb.getString("wf.reasignedTitle") + " " + prefixNumber, rb.getString("mail.nameUser"), HandlerParameters.PARAMETROS.getMailAccount(), email, msg.toString());
     			
            		}
            	}
            	
            }
 
            

            
            
            

            //hacemos todo el proceso en la respuesta del flujo.
//   	        HandlerWorkFlows.setResponseFlexFlow(forma,beanNotified,minorKeep,mayorKeep,request);
            
            /*
            HandlerWorkFlows.saveResponseUser(forma,beanNotified,mayorKeep,request);
            putAtributte("idWorkFlow",String.valueOf(forma.getIdWorkFlow()));
            putAtributte("row",String.valueOf(forma.getRow()));
            log.debug("forma.getResult(): " + forma.getResult());
            log.debug("beanNotified.getNotified(): " + beanNotified.getNotified());
            log.debug("beanNotified.getEmail(): " + beanNotified.getEmail());
            if (!HandlerWorkFlows.wfuCanceled.equalsIgnoreCase(forma.getResult())||
                (!forma.getConditional().equalsIgnoreCase(HandlerWorkFlows.wfConditional))) {
                if (Constants.permissionSt.equalsIgnoreCase(beanNotified.getNotified())) {
                    HandlerWorkFlows.notifiedUsers(rb.getString("wf.newWFTitle"),
                                                   rb.getString("mail.nameUser"),
                                                   HandlerParameters.PARAMETROS.getMailAccount(),beanNotified.getEmail(),
                                                   beanNotified.getComments());
                }
            }
            if (!HandlerWorkFlows.wfuCanceled.equalsIgnoreCase(forma.getResult())) {
                //Env�o de Notificaci�n de Respuesta al Usuario que inici� el Flujo de Trabajo
                if (Constants.permissionSt.equalsIgnoreCase(beanNotified.getNotified())) {
                    LoginUser user = new LoginUser();
                    user.setUser(beanNotified.getOwner());
                    HandlerDBUser.load(user,true);
                    StringBuffer mensaje = new StringBuffer(100);
                    mensaje.append(rb.getString("wf.user")).append(" ").append(forma.getNameUser()).append("<br/>");
                    mensaje.append(rb.getString("wf.response")).append("<br/>");
                    mensaje.append(rb.getString("wf.overDoc")).append(" ").append(beanNotified.getNameDocument()).append("<br/>");
                    //System.out.println("mensaje.toString() = " + mensaje.toString());
                    HandlerWorkFlows.notifiedUsers(rb.getString("wf.newWFTitle"),
                                                   rb.getString("mail.nameUser"),
                                                   HandlerParameters.PARAMETROS.getMailAccount(),user.getEmail(),mensaje.toString());
                }
            } else {
                    LoginUser user = new LoginUser();
                    user.setUser(beanNotified.getOwner());
                    HandlerDBUser.load(user,true);
                    StringBuffer mensaje = new StringBuffer(100);
                    mensaje.append(rb.getString("wf.user")).append(" ").append(forma.getNameUser()).append("<br/>");
                    mensaje.append(rb.getString("wf.respCanceled")).append("<br/>");
                    mensaje.append(rb.getString("wf.overDoc")).append(" ").append(beanNotified.getNameDocument()).append("<br/>");
                    HandlerWorkFlows.notifiedUsers(rb.getString("wf.newWFTitle"),
                                                   rb.getString("mail.nameUser"),
                                                   HandlerParameters.PARAMETROS.getMailAccount(),user.getEmail(),mensaje.toString());
            }}*/
            
            //SIMON 12 DE JULIO 2005 INICIO
            /*String str=null;
             if (forma.getEnd().equalsIgnoreCase("1")) {  //se finaliza el flujo..
                str="doc.workflows";
                return goSucces(str);
            } else {
               return goSucces();
           }*/
            //ActionForward resp = new ActionForward("/showDataDocument.do", false);
            putObjectSession("recargar", "1");
			//resp.setName("success");
			return goSucces();
		} catch (ApplicationExceptionChecked ae) {
			log.error(ae.getMessage());
			return goError(ae.getKeyError());
		} catch (Exception ex) {
			log.error(ex.getMessage());
			ex.printStackTrace();
		}
		return goError();
        
    }
}
