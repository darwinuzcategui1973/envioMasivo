package com.desige.webDocuments.workFlows.actions;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.desige.webDocuments.document.actions.loadsolicitudImpresion;
import com.desige.webDocuments.document.forms.DocumentsCheckOutsBean;
import com.desige.webDocuments.persistent.managers.HandlerBD;
import com.desige.webDocuments.persistent.managers.HandlerDocuments;
import com.desige.webDocuments.persistent.managers.HandlerParameters;
import com.desige.webDocuments.persistent.managers.HandlerWorkFlows;
import com.desige.webDocuments.utils.ApplicationExceptionChecked;
import com.desige.webDocuments.utils.Constants;
import com.desige.webDocuments.utils.DesigeConf;
import com.desige.webDocuments.utils.ToolsHTML;
import com.desige.webDocuments.utils.beans.SuperAction;
import com.desige.webDocuments.utils.beans.Users;
import com.desige.webDocuments.workFlows.forms.DataUserWorkFlowForm;
import com.focus.util.PerfilAdministrador;

/**
 * Title: LoadWorkFlowsAction.java<br>
 * Copyright: (c) 2004 Focus Consulting C.A.<br/>
 * @author Ing. Nelson Crespo (NC)
 * @author Ing. Simón Rodriguéz (SR)
 * @version WebDocuments v3.0
 * <br>
 *     Changes:<br>
 * <ul>
 *     <li> 06-09-2004 (NC) Creation </li>
 *     <li>             03/08/2005 (SR) Se valida la variable primeravez, en caso de que sea la primeravez que entra el usuario
 *                       para informarle que cambie su password por seguridad.</li>
 * <ul>
 */
public class LoadWorkFlowsAction extends SuperAction {
    static Logger log = LoggerFactory.getLogger("[V4.0] " + LoadWorkFlowsAction.class.getName());

    public ActionForward execute(ActionMapping mapping,
                                 ActionForm form, HttpServletRequest request,
                                 HttpServletResponse response) {

    	// seteamos el modulo activo
        request.getSession().setAttribute(Constants.MODULO_ACTIVO, Constants.MODULO_PRINCIPAL);
        
        try {
            super.init(mapping,form,request,response);
            HttpSession session = getSession();

            removeObjectSession("wfPendings");
            removeObjectSession("wfExpires");
            removeObjectSession("docCheckOuts");
            removeObjectSession("docExpires");
            removeObjectSession("wfExpiresOwner");
            removeObjectSession("wfPrintApproved");
            removeObjectSession("wfPrintCanceled");
            removeObjectSession("docVersionApproved");
            Users usuario = getUserSession();

            //Carga de los Nodos
            //Carga de los Nodos de la Estructura si los Mismos no han sido Cargado ya :D
//            Hashtable tree = (Hashtable)getSessionObject("tree");
//            tree = ToolsHTML.checkTree(tree,usuario);
            //Se crea un Hash con los Nodos ya revisados
            //Para evitar la Recarga de los Mismos :D
            Hashtable nodos = new Hashtable();
            boolean userIsLikeAnAdmin = PerfilAdministrador.userIsInAdminGroup(usuario);
            
            log.debug("Usuario en el Sistema... " + usuario.getUser()
            		+ (userIsLikeAnAdmin ? "Podra observar los listados en modo administrador" : ""));
            /*
            log.debug("Cargando Flujos Pendientes....");
            Collection<DataUserWorkFlowForm> wfPendings = HandlerWorkFlows.getAllWorkFlowsUserAndStatu(false, HandlerWorkFlows.pending,
                                                                                 usuario.getUser(), HandlerWorkFlows.wfuPending,
                                                                                 false, getRb(), null, nodos, userIsLikeAnAdmin);
            
            wfPendings.addAll(HandlerWorkFlows.getAllFlexWorkFlowsUserAndStatu(false, HandlerWorkFlows.pending,
                                                                               usuario.getUser(),
                                                                               HandlerWorkFlows.wfuPending, null,
                                                                               nodos, getMessage("act.reinit"),
                                                                               null, null, getRb(), userIsLikeAnAdmin));

            log.debug("Cargando Flujos Expirados....");
            Collection<DataUserWorkFlowForm> wfExpires = HandlerWorkFlows.getAllWorkFlowsUserAndStatu(true, HandlerWorkFlows.expires,
                                                                                usuario.getUser(), HandlerWorkFlows.wfuExpired,
                                                                                false, getRb(), null, nodos, userIsLikeAnAdmin);
            
            wfExpires.addAll(HandlerWorkFlows.getAllFlexWorkFlowsUserAndStatu(true,HandlerWorkFlows.expires,
                    													      usuario.getUser(),
														                      HandlerWorkFlows.wfuExpired, null,
														                      nodos, getMessage("act.reinit"), 
														                      HandlerWorkFlows.wfuExpired, 
														                      HandlerWorkFlows.expires,
														                      getRb(),
														                      userIsLikeAnAdmin));

            //YSA 16/07/08
            //Mostrar Flujos expirados en la pagina principal para el originador
            log.debug("Cargando Flujos Expirados para el originador....");
            Collection<DataUserWorkFlowForm> wfExpiresOwner = HandlerWorkFlows.getAllWorkFlowsOwnerUserAndStatu(true,
            		HandlerWorkFlows.expires,
            		usuario.getUser(),
            		HandlerWorkFlows.wfuExpired,
            		true,
            		getRb(),
            		null,
            		nodos,
            		null,
            		null,
            		userIsLikeAnAdmin, 
            		false);
            
            wfExpiresOwner.addAll(HandlerWorkFlows.getAllFlexWorkFlowsOwnerUserAndStatu(true,HandlerWorkFlows.expires,
                    													      usuario.getUser(),
														                      HandlerWorkFlows.wfuExpired,null,nodos,
														                      getMessage("act.reinit"),
														                      HandlerWorkFlows.wfuExpired,
														                      HandlerWorkFlows.expires,
														                      getRb(),
														                      userIsLikeAnAdmin));
            //Luis Cisneros 01/03/07
            //Mostrar Flujos cancelados en la pagina principal
            log.debug("Cargando Flujos Cancelados....");
            Collection<DataUserWorkFlowForm> wfCanceled = HandlerWorkFlows.getAllWorkFlowsOwnerUserAndStatu(
            		 false,HandlerWorkFlows.canceled,
            		 usuario.getUser(),
            		 HandlerWorkFlows.wfuCanceled,
            		 false,getRb(),null,nodos,null,"1",
            		 userIsLikeAnAdmin, true);

            
            log.debug("Cargando documents Pendientes....");
            Collection<DocumentsCheckOutsBean> docCheckOuts = HandlerDocuments.getAllDocumentsCheckOutsUser(usuario.getUser(), "0", null,
            		nodos, userIsLikeAnAdmin);
           
            docCheckOuts.addAll(HandlerDocuments.getAllDocumentsCheckOutsUserBorradoresUnicos(usuario.getUser(), "0", null,nodos, userIsLikeAnAdmin));
            
            docCheckOuts.addAll(HandlerDocuments.getAllDocumentsAprobadosPendientePorVencerUser(usuario.getUser(), null,nodos, userIsLikeAnAdmin)); 
           
            //public static synchronized Collection getAllDocumentsExpiresUser(Users usuario,HttpServletRequest request) throws Exception {
            //este metodo es para mandar mensajes a los documentos expirados y los docs expiardos.
            //  Collection docExpires = HandlerDocuments.getAllDocumentsExpiresUser(usuario,request);
           
            Collection<DocumentsCheckOutsBean> docExpires = HandlerDocuments.getAllDocumentsExpiresUser(usuario.getUser(), null,
            		nodos, userIsLikeAnAdmin);
            
            //YSA 18/08/08
            //Mostrar Flujos de impresion aprobados
            log.debug("Cargando Solicitudes de Impresion Aprobadas....");
            // si hay un responsable de impresion solo el vera los los flujos por imprimir
            String respsolimpres = HandlerParameters.PARAMETROS.getRespsolimpres();
            boolean isRespsolimpres = (respsolimpres!=null && !respsolimpres.trim().equals(""));
            boolean isRespsolimpresFilterUser = false;
            if(isRespsolimpres && usuario.getNameUser().equals(respsolimpres)) {
            	isRespsolimpresFilterUser = true;
            }
            
            Collection<DocumentsCheckOutsBean> wfPrintApproved = HandlerWorkFlows.getAllPrintingOwnerUserAndStatu(usuario.getIdPerson(),
            		loadsolicitudImpresion.aprobadoprintlnInt, null, nodos, userIsLikeAnAdmin || isRespsolimpresFilterUser, false);
            // si hay responsable de impresion no se mostraran las solicitudes aprobadas para impresion
            if(!userIsLikeAnAdmin && isRespsolimpres && !isRespsolimpresFilterUser) {
            	wfPrintApproved = new Vector();
            }

            log.debug("Cargando Solicitudes de Impresion Rechazadas o Canceladas");
            Collection<DocumentsCheckOutsBean> wfPrintCanceled = HandlerWorkFlows.getAllPrintingOwnerUserAndStatu(usuario.getIdPerson(),
            		loadsolicitudImpresion.rechazadoprintlnInt, null, nodos, userIsLikeAnAdmin, true);
            
            log.debug("Cargando documentos aprobados y notificados....");
            Collection<DocumentsCheckOutsBean> docVersionApproved = HandlerDocuments.getNotifiedDocumentsByUser(usuario.getUser(), 
            		userIsLikeAnAdmin);
            
            if (DesigeConf.getProperty("application.admon").equalsIgnoreCase(usuario.getIdGroup())) {
                //22 DE JULIO 2005 INICIO
                //este metodo solo agarra los docs expirados.
                log.debug("Cargando documents Expirados....");
                Collection<DocumentsCheckOutsBean> docsAdmon = HandlerDocuments.getAllDocumentsExpiresUser(usuario.getUser(), null,
                		nodos, userIsLikeAnAdmin);
                //22 DE JULIO 2005 FIN
                if (docsAdmon!=null && docsAdmon.size() > 0) {
                	Collection<DocumentsCheckOutsBean> aux = new Vector<DocumentsCheckOutsBean>();
                    aux.addAll(docExpires);
                    aux.retainAll(docsAdmon);
                    docsAdmon.removeAll(aux);
                    docsAdmon.addAll(docsAdmon);
                }
            }
            Collection<DataUserWorkFlowForm> WFPenndingEndTask = HandlerWorkFlows.getAllDataOwnerWF(false,usuario.getUser(),
            		getRb(), userIsLikeAnAdmin);
            if (WFPenndingEndTask!=null&&WFPenndingEndTask.size() > 0) {
                wfPendings.addAll(WFPenndingEndTask);
            }
//-----------------------------------------SIM�N 20 DE JUNIO 2005 INICIO
//-----------------------------------------MANDAR MENSAJES A LOS DOCUMENTOS APROBADOS Y BORRADOS QUE YA FUERON EXPIRADOS--------------------------------------------------------
            //YSA Fue comentado el 27/06/2007 porque el hilo CheckDocvencThread.java envia los correos de notificacion
            //HandlerWorkFlows.getAllVersionForDocumentExpires(request,HandlerDocuments.docTrash);
            //HandlerWorkFlows.getAllVersionForDocumentExpires(request,HandlerDocuments.docApproved);
//-----------------------------------------SIMON 20 DE JUNIO 2005 FIN
            //02 AGOSTO 2005 INICIO SIMON
            StringBuffer sql = new StringBuffer(" from person where nameUser='").append(usuario.getUser()).append("'");
            sql.append(" AND accountActive = '1'");
            String primeravez = HandlerBD.getField("primeravez",sql.toString(),Thread.currentThread().getStackTrace()[1].getMethodName());
            removeObjectSession("primeravez");
            if (ToolsHTML.isEmptyOrNull(primeravez)){
                putObjectSession("primeravez","true");
            }
            
            //ya teniendo todos los elementos, debemos proceder a ordenarlos
            //para facilitar el proceso de mostrar los datos en la pagina respectiva
            Collections.sort((List<DataUserWorkFlowForm>) wfPendings);
            Collections.sort((List<DataUserWorkFlowForm>) wfExpires);
            Collections.sort((List<DataUserWorkFlowForm>) wfExpiresOwner);
            Collections.sort((List<DocumentsCheckOutsBean>) docCheckOuts);
            Collections.sort((List<DocumentsCheckOutsBean>) docExpires);
            Collections.sort((List<DataUserWorkFlowForm>) wfCanceled);
            Collections.sort((List<DocumentsCheckOutsBean>) wfPrintApproved);
            Collections.sort((List<DocumentsCheckOutsBean>) wfPrintCanceled);
            Collections.sort((List<DocumentsCheckOutsBean>) docVersionApproved);
            
            //02 AGOSTO 2005 FIN SIMON
            putObjectSession("wfPendings",wfPendings);
            putObjectSession("wfExpires",wfExpires);
            putObjectSession("wfExpiresOwner",wfExpiresOwner);
            putObjectSession("docCheckOuts",docCheckOuts);
            putObjectSession("docExpires",docExpires);
            putObjectSession("wfCanceled", wfCanceled);
            putObjectSession("wfPrintApproved", wfPrintApproved);
            putObjectSession("wfPrintCanceled", wfPrintCanceled);
            request.getSession().setAttribute("docVersionApproved", docVersionApproved);
            
            setRealListSizeIntoSession(request, "wfExpiresOwner", wfExpiresOwner);
              */    
            log.debug("Retornando....");
            return goTo(request.getParameter("goTo"));// goSucces();
        } catch (ApplicationExceptionChecked ae) {
            return goError(ae.getKeyError());
        } catch (Exception ex){
            ex.printStackTrace();
        }
        return goError();
    }
    
    /**
     * Metodo para colocar el listado de flujos reales en una lista.
     * La idea es no contar registros duplicados.
     * 
     * @param request
     * @param paramName
     * @param values
     */
    private static void setRealListSizeIntoSession(HttpServletRequest request, String paramName,
    		Collection<DataUserWorkFlowForm> values){
    	try {
    		Map<Integer, Integer> list = new HashMap<Integer, Integer>();
    		int size = 0;
    		
			if (values != null) {
				for (DataUserWorkFlowForm dataUserWorkFlowForm : values) {
					list.put(dataUserWorkFlowForm.getIdWorkFlow(), dataUserWorkFlowForm.getIdWorkFlow());
				}
				
				size = list.size();
			}
			
			log.info("Ajustando total del listado '" + paramName + "' de " + values.size()
					 + " elementos a " + size + " elementos.");
			request.getSession().setAttribute(paramName + "Size", size);
		} catch (Exception e) {
			// TODO: handle exception
		}
    }
}
