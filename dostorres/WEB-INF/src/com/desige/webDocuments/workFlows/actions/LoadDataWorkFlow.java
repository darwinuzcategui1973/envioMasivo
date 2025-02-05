package com.desige.webDocuments.workFlows.actions;

import java.util.Calendar;
import java.util.Collection;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.ResourceBundle;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.desige.webDocuments.document.forms.BeanVersionForms;
import com.desige.webDocuments.document.forms.DocumentsCheckOutsBean;
import com.desige.webDocuments.persistent.managers.HandlerBD;
import com.desige.webDocuments.persistent.managers.HandlerDocuments;
import com.desige.webDocuments.persistent.managers.HandlerGrupo;
//Ticket 001-00-003023 
import com.desige.webDocuments.persistent.managers.HandlerParameters;
import com.desige.webDocuments.persistent.managers.HandlerStruct;
import com.desige.webDocuments.persistent.managers.HandlerWorkFlows;
import com.desige.webDocuments.structured.forms.BaseStructForm;
import com.desige.webDocuments.utils.ApplicationExceptionChecked;
import com.desige.webDocuments.utils.Constants;
import com.desige.webDocuments.utils.ToolsHTML;
import com.desige.webDocuments.utils.beans.Search;
import com.desige.webDocuments.utils.beans.SuperAction;
import com.desige.webDocuments.utils.beans.Users;
import com.desige.webDocuments.workFlows.forms.BaseWorkFlow;

/**
 * Title: ${name}.java<br>
 * Copyright: (c) 2004 Focus Consulting C.A.<br/>
 * @author Ing. Nelson Crespo (NC)
 * @version WebDocuments v3.0
 * <br>
 * Changes:<br>
 * <ul>
 *      <li> 28-07-2004 (NC) Creation </li>
 *      <li> 14-05-2005 (NC) Cambios para mostrar la fecha de expiración de los flujos de trabajo
 *                           y control de la estructura </li>
 *      <li> 12-07-2005 (SR) Se creo esta session statuelim para eliminar en newWorkFlow.jsp solo los documentos aprobados.</li> 
 * <ul>
 */
public class LoadDataWorkFlow extends SuperAction {
    static Logger log = LoggerFactory.getLogger("[V4.0 FTP] " + LoadDataWorkFlow.class.getName());
    public ActionForward execute(ActionMapping mapping,
                                 ActionForm form, HttpServletRequest request,
                                 HttpServletResponse response) {
        super.init(mapping,form,request,response);
//        ResourceBundle rb = ToolsHTML.getBundle(request);
        removeAttribute("dateEndWF");
        removeAttribute("previousWF");
        removeAttribute("RespCambElimDocAdm");
        removeAttribute("RespCambElimDocEOC");
        
        BaseWorkFlow newWF = (BaseWorkFlow)getSessionObject("newWF");
        if (newWF!=null) {
            newWF.setDateExpireWF("");
        }
        String target = getParameter("target");
        String next = getParameter("nexPage");
        String idDocument = getParameter("idDocument");
        String typeWF = getParameter("typeWF");
        String getDoneBy = "";
        String links = HandlerDocuments.getDocumentsLinks(idDocument);
        request.getSession().removeAttribute("infoEliminar");
        if (!ToolsHTML.isEmptyOrNull(links)) {
            ResourceBundle rb = ToolsHTML.getBundle(request);
            request.getSession().setAttribute("infoEliminar",rb.getString("chk.linksInfo"));
        }
        //ydavila Ticket 001-00-003023
        request.getSession().removeAttribute("infoCambiar");
        if (!ToolsHTML.isEmptyOrNull(links)) {
            ResourceBundle rb = ToolsHTML.getBundle(request);
            request.getSession().setAttribute("infoCambiar",rb.getString("chk.linksInfo"));
        }
        
        if (!ToolsHTML.checkValue(target)) {
            target = "";
        }
        if (!ToolsHTML.checkValue(next)) {
            next = "";
        }
        BaseWorkFlow forma = (BaseWorkFlow)form;
        if (forma==null) {
            forma = new BaseWorkFlow();
        }
        forma.setEliminar(Constants.notPermission);
        try {
            Users user = getUserSession();
            forma.setTypeWF(Integer.parseInt(typeWF.trim()));
            int numDoc = 0;
            //Verificamos que el documento no se encuentre Bloqueado
            if (ToolsHTML.isNumeric(idDocument.trim())) {
                numDoc = Integer.parseInt(idDocument.trim());
                //ydavila aqu� va la ratificaci�n de versi�n en vez de deshacer bloqueo
                DocumentsCheckOutsBean isCheckOut = HandlerDocuments.isCheckOutDoc(idDocument.trim());
                if (isCheckOut!=null&&isCheckOut.isCheckOut()) {
                     String[] values = HandlerDocuments.getFields(new String[] {"nombres","apellidos"},"person",
                                                                  "nameuser","'"+isCheckOut.getDoneBy()+"'");
                    if (values!=null) {
                        getDoneBy = values[0];
                        getDoneBy +=" " + values[1];
                    }
                    throw new ApplicationExceptionChecked("E0028");
                }
            } else {
//                throw new Exception(rb.getString("err.invalidDocument"));
                throw new ApplicationExceptionChecked("err.invalidDocument");
            }
            forma.setNumDocument(numDoc);
            //Cargamos la Versi�n Mayor del Documento ya q sobre ella es q se realizar� el Flujo
            String mayorVer = HandlerStruct.getMayorVersionDocument(numDoc);
            //SIMON 12 DE JULIO 2005 INICIO
            removeObjectSession("statuelim");
            //ydavila Ticket 001-00-003023
            removeObjectSession("statucamb");
            String statu = HandlerStruct.getMayorStatusVersionDocument(Integer.parseInt(mayorVer));
            putObjectSession("statuelim",statu);
            //ydavila Ticket 001-00-003023
            putObjectSession("statucamb",statu);
            //SIMON 12 DE JULIO 2005 FIN
            String versionAnt = HandlerDocuments.getIdVersionAntForDoc(numDoc);
            forma.setLastVersion(versionAnt);
            if (ToolsHTML.isNumeric(mayorVer)) {
                numDoc = Integer.parseInt(mayorVer);
            } else {
                //throw new Exception(rb.getString("err.invalidDocument"));
                throw new ApplicationExceptionChecked("err.invalidDocument");
            }
            forma.setComments("");
            forma.setMayorVersionDocument(numDoc);
            String Owner  = HandlerDocuments.getFieldToDocument("Owner",forma.getNumDocument());
//            Hashtable secGroups = HandlerDocuments.loadSecurityDocument(false,forma.getNumDocument());
//            Hashtable secUsers = HandlerDocuments.loadSecurityDocument(true,forma.getNumDocument());
//            Hashtable security = (Hashtable)getSessionObject("security");
//            if (security==null) {
//                security = HandlerGrupo.getAllSecurityForGroup(user.getIdGroup());
//                HandlerDBUser.getAllSecurityForUser(user.getIdPerson(),security);
//                boolean loadMyFolder = false;
//                HandlerStruct.loadAllNodes(security,user.getUser(),user.getIdGroup(),loadMyFolder);
//            }

            Collection previousWF = HandlerWorkFlows.getUserInWorkFlow(idDocument,forma.getTypeWF());
            putObjectSession("previousWF",previousWF);
           
           //Carga de los datos de la estructura en donde se encuentra el documento
            String idNodeDocument = HandlerBD.getField("idNode","documents","numGen",idDocument,"=",HandlerBD.typeInt,Thread.currentThread().getStackTrace()[1].getMethodName());
            Hashtable tree = (Hashtable)getSessionObject("tree");
            tree = ToolsHTML.checkTree(tree,user);
            //System.out.println("idNodeDocument="+ idNodeDocument);
            String idLocation = HandlerStruct.getIdLocationToNode(tree,idNodeDocument);


            //Collection usuarios = HandlerDBUser.getAllUsers(secUsers,secGroups,Owner);
            //aqui seleccionamos todos los usuarios que va a poder participar en un flujo
            //Collection usuarios = HandlerDBUser.getAllUsersFilter(secUsers,secGroups,Owner);
//            Collection usuarios = HandlerDBUser.getAllUsersFilter(secUsers,secGroups,Owner,idNodeDocument);
//            Collection usuarios = HandlerGrupo.getSecurityForNode(idNodeDocument,idDocument,typeWF);

            //ydavila Ticket 001-00-003023
            String subtypeWF=typeWF;
            
            if (!"1".equals(typeWF)) { 
    			typeWF="0";
    		}
            Collection<Search> usuarios = HandlerGrupo.getSecurityForNode(idNodeDocument,false,typeWF);

            // cargamos los usuarios viewers
            Collection<Search> usuariosViewer = HandlerGrupo.getUsersViewers(false,typeWF);
            
            //removemos los viewers de la lista completa de usuarios
            usuarios.removeAll(usuariosViewer);
            //ydavila Ticket 001-00-003023  
            //        para flujos de revisi�n de tipo cambio o eliminaci�n no mostrar usuarios del flujo anterior
            if("3".equals(subtypeWF)) {
            	newWF.setCambiar((byte)1);
            }
            if ((!"3".equals(subtypeWF))&&(!"4".equals(subtypeWF))) { 
            	if (previousWF!=null&&previousWF.size()>0) {
            		log.debug("usuarios = " + usuarios);
            		log.debug("previousWF = " + previousWF);
            		usuarios.removeAll(previousWF);
            	}
            }else{
            	//ydavila Ticket 001-00-003023 - si hay responsable en Admin se env�a al propietario primero 
            	Collection RespCambElimDocEOC=HandlerWorkFlows.getRespCambElimDocEOC(idDocument,forma.getTypeWF(),subtypeWF); 
            	// putObjectSession("RespCambElimDocEOC",RespCambElimDocEOC);
            	usuarios.removeAll(RespCambElimDocEOC);
            	//busco responsable de cambio en Administraci�n
            	Collection RespCambElimDocAdm=HandlerParameters.getRespCambElimDocAdm(subtypeWF);
            	if(RespCambElimDocAdm.size()>0) {
            		removeAttribute("previousWF");
            		removeAttribute("RespCambElimDocEOC");
            	}
            	if (!RespCambElimDocAdm.equals(RespCambElimDocEOC)) {
            		Search bean = HandlerDocuments.getOwnerDocumentSearch(Integer.parseInt(idDocument));
            		if(bean!=null) {
            			boolean isFoundOwner = true;
	            		for (Iterator iterator = RespCambElimDocAdm.iterator(); iterator.hasNext();) {
	            			Search object = (Search) iterator.next();
	                    	if (!object.getDescript().equals(bean.getDescript())) {
	                    		isFoundOwner = false;
	                    	}
						}
	            		if(!isFoundOwner) {
	            			// RespCambElimDocAdm.add(bean); // agregamos al propietario
	            		}
            		}
            		//propietario
            		putObjectSession("RespCambElimDocEOC",RespCambElimDocEOC);
            		//responsable
            		putObjectSession("RespCambElimDocAdm",RespCambElimDocAdm);
            		
            	}else{
            		if (RespCambElimDocAdm!=null) {
            			RespCambElimDocAdm=null; 
            		}
            	//usuarios.removeAll(RespCambElimDocAdm);
            	//usuarios.removeAll(usuarios);
            	}
            } 
//            Collection groups = HandlerGrupo.getAllGroupsWithUsers(secGroups);
//            Collection groups = HandlerGrupo.getAllGroupsToDoc(idNodeDocument,idDocument,typeWF);
            Collection groups = HandlerGrupo.getAllGroupsWithPerm(idNodeDocument,false);

            //System.out.println("idLocation = " + idLocation);
            removeObjectSession("showCharge");
            BaseStructForm location = HandlerStruct.loadStruct(idLocation);
            if (location!=null) {
                log.debug("localidad.getShowCharge() = " + location.getShowCharge());
                if (location.getShowCharge()==1) {
                    putObjectSession("showCharge","true");
                }
            }
            log.debug("idLocation = " + idLocation);
            forma.setExpire(1);
            if (Constants.notPermission == location.getExpireWF()) {
                int nums = 0;
                if (ToolsHTML.isNumeric(location.getDaysWF())) {
                    location.setDaysWF(location.getDaysWF().trim());
                    nums = Integer.parseInt(location.getDaysWF());
                    java.util.Date hoy = new java.util.Date();
                    hoy = ToolsHTML.sumUnitsToDate(Calendar.DAY_OF_MONTH,hoy,nums);
                    String dateEndWF = ToolsHTML.sdfShowConvert.format(hoy);
                    putObjectSession("dateEndWF",dateEndWF);
                    forma.setDateExpireWF(dateEndWF);
                    forma.setExpire(0);
                }
            }
            removeAttribute("changeWF");
            if (Constants.notPermission == location.getDisableUserWF()) {
                putObjectSession("changeWF","yes");
            }

            if (location!=null) {
                forma.setConditional(location.getConditional());
                forma.setCopy(location.getCopy());
                forma.setSecuential(location.getSequential());
                forma.setNotified(location.getNotify());
            }

            String value = String.valueOf(HandlerParameters.PARAMETROS.getAllowWF());
            log.debug("value = " + value);
            removeAttribute("permitWF");
            if (value!=null){
                if (value.equalsIgnoreCase("1")) {
                    putObjectSession("permitWF","0");
                } else {
                	putObjectSession("permitWF","1");
                }
            } else {
                putObjectSession("permitWF","1");
            }
            putObjectSession("usuarios",usuarios);
            putObjectSession("usuariosViewer",usuariosViewer);
            
            putObjectSession("groups",groups);
            putAtributte("target",target);
            putAtributte("redirect",next);
            
            forma.setUsersSelected(null);
            
            Collection versiones = HandlerStruct.getAllVersionForDocument(idDocument, null, null);
            Iterator version = versiones.iterator();
            if(version.hasNext()) {
                BeanVersionForms bean =(BeanVersionForms)version.next();
            	putObjectSession("dateDeadDocTime",ToolsHTML.sdfShowWithoutHour.parse(bean.getDateExpiresDrafts()).getTime());
            }
          
            return goSucces();
        } catch (ApplicationExceptionChecked ae) {
            return goErrorII(ae.getKeyError()," "+getDoneBy);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return goError();
    }
}
