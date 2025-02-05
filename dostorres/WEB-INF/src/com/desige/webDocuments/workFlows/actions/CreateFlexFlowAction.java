package com.desige.webDocuments.workFlows.actions;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.desige.webDocuments.activities.forms.Activities;
import com.desige.webDocuments.activities.forms.SubActivities;
import com.desige.webDocuments.document.forms.BaseDocumentForm;
import com.desige.webDocuments.document.forms.BeanVersionForms;
import com.desige.webDocuments.document.forms.DocumentsCheckOutsBean;
import com.desige.webDocuments.persistent.managers.ActivityBD;
import com.desige.webDocuments.persistent.managers.HandlerDocuments;
import com.desige.webDocuments.persistent.managers.HandlerGrupo;
import com.desige.webDocuments.persistent.managers.HandlerStruct;
import com.desige.webDocuments.structured.forms.BaseStructForm;
import com.desige.webDocuments.utils.ApplicationExceptionChecked;
import com.desige.webDocuments.utils.Constants;
import com.desige.webDocuments.utils.ToolsHTML;
import com.desige.webDocuments.utils.beans.Search;
import com.desige.webDocuments.utils.beans.SuperAction;
import com.desige.webDocuments.utils.beans.Users;
import com.desige.webDocuments.workFlows.forms.BaseWorkFlow;
import com.focus.qweb.dao.ActivityDAO;
import com.focus.qweb.to.ActivityTO;

/**
 * Title: CreateFlexFlowAction.java <br/>
 * Copyright: (c) 2004 Desige Servicios de Computación<br/>
 *
 * @author Ing. Nelson Crespo
 * @version WebDocuments v1.0
 * <br/>
 *      Changes:<br/>
 * <ul>
 *      <li> 19/12/2005 (NC) Creation </li>
 * </ul>
 */
public class CreateFlexFlowAction extends SuperAction {
    static Logger log = LoggerFactory.getLogger(CreateFlexFlowAction.class.getName());
    public ActionForward execute(ActionMapping mapping,
                                 ActionForm form, HttpServletRequest request,
                                 HttpServletResponse response) {
        super.init(mapping,form,request,response);
        try {
            Users user = getUserSession();
            //System.out.println(request.getSession().getAttribute(Constants.MODULO_ACTIVO));
            BaseWorkFlow frmWF = (BaseWorkFlow)getSessionObject("newWF");
            if (frmWF!=null) {
                log.debug("Expira: " + frmWF.getDateExpireWF());
                if (ToolsHTML.isEmptyOrNull(frmWF.getDateExpireWF())) {
                    frmWF.setDateExpireWF("");
                }
            }
            String idDocument = getParameter("idDocument");
            String isSendToFlexWF = getParameter("isSendToFlexWF");
            if(isSendToFlexWF!=null) {
            	request.setAttribute("isSendToFlexWF",isSendToFlexWF);
            }
            log.debug("idDocument: " + idDocument);
            int numDoc = 0;
            if (ToolsHTML.isNumeric(idDocument.trim())) {
                //Verificamos que el documento no se encuentre Bloqueado
                numDoc = Integer.parseInt(idDocument.trim());
                DocumentsCheckOutsBean isCheckOut = HandlerDocuments.isCheckOutDoc(idDocument.trim());
                if (isCheckOut!=null&&isCheckOut.isCheckOut()) {
                    throw new ApplicationExceptionChecked("E0028");
                }
            } else {
                throw new ApplicationExceptionChecked("err.invalidDocument");
            }
            BaseDocumentForm forma = new BaseDocumentForm();
            forma.setIdDocument(idDocument);
            forma.setNumberGen(idDocument);
            putAtributte("idDocument",idDocument);

            Hashtable tree = (Hashtable)getSessionObject("tree");
            tree = ToolsHTML.checkTree(tree,user);
            HandlerStruct.loadDocument(forma,true,false,tree, request);
            log.debug("idNode " + forma.getIdNode());

            Collection versiones = HandlerStruct.getAllVersionForDocument(forma.getIdDocument(), null, null);
            Iterator version = versiones.iterator();
            if(version.hasNext()) {
                BeanVersionForms bean =(BeanVersionForms)version.next();
            	putObjectSession("dateDeadDocTimeFlex",ToolsHTML.sdfShowWithoutHour.parse(bean.getDateExpiresDrafts()).getTime());
            }

			//Se Calcula la Fecha de Expiración del FC
            //en base a la parametrización de la Localidad

            String idLocation = HandlerStruct.getIdLocationToNode(tree,forma.getIdNode());
            log.debug("[idLocation] " + idLocation);
            if (ToolsHTML.isEmptyOrNull(idLocation)) {
                tree = ToolsHTML.checkTree(null,user);
                putObjectSession("tree",tree);
                idLocation = HandlerStruct.getIdLocationToNode(tree,forma.getIdNode());
            }
            BaseStructForm location = HandlerStruct.loadStruct(idLocation);
//            BaseStructForm location = (BaseStructForm)HibernateUtil.loadObject(BaseStructForm.class,new Long(idLocation));
            if (Constants.notPermission == location.getExpireWF()) {
                int nums = 0;
                if (ToolsHTML.isNumeric(location.getDaysWF())) {
                    nums = Integer.parseInt(location.getDaysWF().trim());
                    java.util.Date hoy = new java.util.Date();
                    hoy = ToolsHTML.sumUnitsToDate(Calendar.DAY_OF_MONTH,hoy,nums);
                    String dateEndWF = ToolsHTML.sdfShowConvert.format(hoy);
                    log.debug("dateEndWF " + dateEndWF);
                    putObjectSession("dateEndWF",dateEndWF);
//                    forma.setDateExpireWF(dateEndWF);
//                    forma.setExpire(0);
                } else {
                    putObjectSession("dateEndWF","");
                }
            }
            //Carga del Flujo seleccionado por el Usuario
            String actNumber = getParameter("number");
            
            ActivityDAO oActivityDAO = new ActivityDAO();
            ActivityTO oActivityTO = new ActivityTO();
            
            oActivityTO.setActNumber(actNumber);
            oActivityDAO.cargar(oActivityTO);
            
            Activities actividad = new Activities(oActivityTO);
            
            
            log.debug("Act Num: " + actividad.getNumber());
            getDataFlexFlow(frmWF,numDoc,forma.getNumVer(),forma.getIdNode(),location);
            if (actividad!=null) {
                //Carga de las Actividades Asociadas.
                Collection sub = ActivityBD.getSubActivities(actividad.getSubActivitys());
                Vector actividades = new Vector();
//                Collection groups = HandlerGrupo.getAllGroupsToDoc(forma.getIdNode(),idDocument,subNumber);
                Collection groups = HandlerGrupo.getAllGroupsToDoc(forma.getIdNode(),idDocument,null,true);
//                Collection usuarios = HandlerGrupo.getSecurityForNode(forma.getIdNode(),true,forma.getIdDocument(),true);
                Collection<Search> usuarios = HandlerGrupo.getSecurityForNode(forma.getIdNode(),true,null);
                
                // cargamos los usuarios viewers
                Collection<Search> usuariosViewer = HandlerGrupo.getUsersViewers(false,"1");
                
                //removemos los viewers de la lista completa de usuarios
                usuarios.removeAll(usuariosViewer);
                
                log.debug("Usuarios: " + usuarios);
                for (Iterator iterator = sub.iterator(); iterator.hasNext();) {
                    //Carga de la seguridad según el tipo de SubActividad
                	SubActivities subActivities = (SubActivities) iterator.next();
                    String subNumber = String.valueOf(subActivities.getNumber());
                    //Copia de los Valores del Flujo Seleccionado por Cada SubActividad
                    BaseWorkFlow frmSub = ToolsHTML.getCopyBean(frmWF);

//                    Collection groups = HandlerGrupo.getAllGroupsToDoc(forma.getIdNode(),idDocument,subNumber);
//                    Collection usuarios = HandlerGrupo.getSecurityForNode(forma.getIdNode(),true);

                    Collection previousWF = ToolsHTML.getUsersCollection(subActivities.getActUser());
                    String usrs = ToolsHTML.getUsrs(previousWF);
                    frmSub.setUsrSug(usrs);
                    log.debug("Usuarios: " + usrs);
                    log.debug("previousWF: " + previousWF);
                    frmSub.setIdNodeDocument(forma.getIdNode());
                     if (previousWF!=null && previousWF.size() > 0) {
                        Collection items = ToolsHTML.copyCollectionItems(usuarios);
                        items.removeAll(previousWF);
                        Collection copyGroups = ToolsHTML.copyCollectionItems(groups);
                        putObjectSession("usuarios"+subNumber,items);
                        putObjectSession("usuariosViewer"+subNumber,usuariosViewer);
                        putObjectSession("groups"+subNumber,copyGroups);
//                         putObjectSession("previousWF"+subNumber,previousWF);
//                         frmSub.setNameAct(subActivities.getName());
//                         frmSub.setUserSelecteds(previousWF);
//                         frmSub.setSubNumber(subActivities.getNumber());
//                         frmSub.setnAct(subActivities.getActivityID());
                    } else {
                        putObjectSession("usuarios"+subNumber,usuarios);
                        putObjectSession("usuariosViewer"+subNumber,usuariosViewer);
                         putObjectSession("groups"+subNumber,groups);
                    }
                    frmSub.setNameAct(subActivities.getNameAct());
                    frmSub.setUserSelecteds(previousWF);
                    frmSub.setUsersSelected(previousWF.toArray());
                    frmSub.setSubNumber(subActivities.getNumber());
                    frmSub.setnAct(subActivities.getActivityID());
                    frmSub.setTypeWF((int)subActivities.getNumber());
                    //Descripción de la Actividad
                    frmSub.setComments(subActivities.getDescription());
                    actividades.add(frmSub);
                    //Usuarios Previos?
//                    Collection previousWF = HandlerWorkFlows.getUserInWorkFlow(idDocument,subNumber);
//                    if (previousWF!=null && previousWF.size() > 0) {
//                        usuarios.removeAll(previousWF);
//                        putObjectSession("previousWF"+actNumber,previousWF);
//                    }
                }
                putObjectSession("actividadesFlex",actividades);
//                Hashtable h = new Hashtable();
//                putObjectSession("subactivities",sub);
                putObjectSession("sizeSub",String.valueOf(sub.size()));
                if (actividades.size() > 0) {
                    putObjectSession("newWF",actividades.get(0));
                    putObjectSession("posAct","0");

                }
            }
            //System.out.println(request.getSession().getAttribute(Constants.MODULO_ACTIVO));
            return goSucces();
        } catch (ApplicationExceptionChecked ae) {
            ae.printStackTrace();
            log.error("Error: " + ae);
            return goError(ae.getKeyError());
        } catch (Exception ex) {
            log.error("Error: " + ex);
            ex.printStackTrace();
        }
        return goError();
    }

    private void getDataFlexFlow(BaseWorkFlow wfForm,int numDoc,int version,
                                 String idNodeDocument,BaseStructForm location) throws Exception {
        //Información sobre el Flujo a Crear
        if (wfForm==null) {
            wfForm = new BaseWorkFlow();
        }
        wfForm.setTypeWF(0);
        wfForm.setNumDocument(numDoc);
        String versionAnt = HandlerDocuments.getIdVersionAntForDoc(numDoc);
        wfForm.setNumVersion(version);
        wfForm.setLastVersion(versionAnt);
        wfForm.setMayorVersionDocument(version);
        wfForm.setComments("");
        wfForm.setExpire(1);
        Hashtable tree = (Hashtable)getSessionObject("tree");
        Users user = getUserSession();
        tree = ToolsHTML.checkTree(tree,user);
//        String idLocation = HandlerStruct.getIdLocationToNode(tree,idNodeDocument);
//        BaseStructForm location = (BaseStructForm)HibernateUtil.loadObject(BaseStructForm.class,new Long(idLocation));
        if (location!=null) {
            log.debug("localidad.getShowCharge() = " + location.getShowCharge());
            if (location.getShowCharge()==1) {
                putObjectSession("showCharge","true");
            }
            log.debug("location.getExpireWF(): " + location.getExpireWF());
            if (Constants.notPermission == location.getExpireWF()) {
                int nums;
                log.debug("location.getDaysWF() " + location.getDaysWF());
                if (ToolsHTML.isNumeric(location.getDaysWF())) {
                    nums = Integer.parseInt(location.getDaysWF().trim());
                    java.util.Date hoy = new java.util.Date();
                    hoy = ToolsHTML.sumUnitsToDate(Calendar.DAY_OF_MONTH,hoy,nums);
                    String dateEndWF = ToolsHTML.sdfShowConvert.format(hoy);
                    putObjectSession("dateEndWF",dateEndWF);
                    wfForm.setDateExpireWF(dateEndWF);
                    wfForm.setExpire(0);
                }
            }
        }
        if (location!=null) {
            wfForm.setConditional(location.getConditional());
            wfForm.setCopy(location.getCopy());
            wfForm.setSecuential(location.getSequential());
            wfForm.setNotified(location.getNotify());
        }
    }

}
