package com.desige.webDocuments.workFlows.actions;

import java.util.Collection;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.ResourceBundle;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.desige.webDocuments.document.forms.BaseDocumentForm;
import com.desige.webDocuments.persistent.managers.HandlerDBUser;
import com.desige.webDocuments.persistent.managers.HandlerGrupo;
import com.desige.webDocuments.persistent.managers.HandlerStruct;
import com.desige.webDocuments.persistent.managers.HandlerWorkFlows;
import com.desige.webDocuments.structured.forms.BaseStructForm;
import com.desige.webDocuments.utils.ApplicationExceptionChecked;
import com.desige.webDocuments.utils.DesigeConf;
import com.desige.webDocuments.utils.ToolsHTML;
import com.desige.webDocuments.utils.beans.SuperAction;
import com.desige.webDocuments.utils.beans.Users;
import com.desige.webDocuments.workFlows.forms.BaseWorkFlow;
import com.desige.webDocuments.workFlows.forms.DataWorkFlowForm;

/**
 * Title: SaveFlexFlowAction.java <br/>
 * Copyright: (c) 2004 Desige Servicios de Computación<br/>
 *
 * @author Ing. Nelson Crespo
 * @version WebDocuments v1.0
 * <br/>
 *      Changes:<br/>
 * <ul>
 *      <li> 04/01/2006 (NC) Creation </li>
 * </ul>
 */
public class SaveFlexFlowAction extends SuperAction {
    static Logger log = LoggerFactory.getLogger(SaveFlexFlowAction.class.getName());
    public ActionForward execute(ActionMapping mapping,
                                 ActionForm form, HttpServletRequest request,
                                 HttpServletResponse response) {
        ResourceBundle rb = ToolsHTML.getBundle(request);
        super.init(mapping,form,request,response,rb);



        try {
            Users user = getUserSession();
            removeAttribute("usersWF");
            removeAttribute("showDataWF");
            removeAttribute("info");
            //Números de SubActividades
            String cuantos = getParameter("cuantos");
            Vector flujos = new Vector();
            if (ToolsHTML.isNumeric(cuantos) ) {
                BaseWorkFlow wfForm = (BaseWorkFlow)form;
                int nCuantos = Integer.parseInt(cuantos);
                String titleWF = rb.getString("wf.newWFTitle");
                log.debug("Salvando");
                String statu = HandlerWorkFlows.pending;
//                subactivities subAct = (SubActivities)getSessionObject("subactivities");
//                log.debug("subAct " + subAct.getActivityID());
                String dateExpireWF = getParameter("dateExpireWF");
                log.debug("dateExpireWF " + dateExpireWF);
                String dateExp = "0";
                if (ToolsHTML.isEmptyOrNull(dateExpireWF)) {
                    dateExpireWF = "";
                    dateExp = "1";
                }
                if ("0".equalsIgnoreCase(DesigeConf.getProperty("showPart"))) {
                    for (int indice=0;indice < nCuantos; indice++) {
                        BaseWorkFlow forma = new BaseWorkFlow();
                        forma.setTitleForMail(titleWF);
                        //Carga de Información del Documento versión Mayor
                        BaseDocumentForm docForm = new BaseDocumentForm();
                        String idDoc = String.valueOf(wfForm.getNumDocument());
                        docForm.setIdDocument(idDoc);
                        docForm.setNumberGen(idDoc);

                        Hashtable tree = (Hashtable)getSessionObject("tree");
                        if (tree==null) {
//                            Hashtable security = HandlerGrupo.getAllSecurityForGroupH(user.getIdGroup(),null);
//                            HandlerDBUser.getAllSecurityForUserH(user.getIdPerson(),security);
//                            tree = HandlerStruct.loadAllNodesH(security,user.getIdGroup());
                            boolean isAdmon = user.getIdGroup().equalsIgnoreCase(DesigeConf.getProperty("application.admon"));
                            Hashtable subNodos = new Hashtable();
                            Hashtable security = null;
                            if (!isAdmon) {
                                StringBuffer idStructs = new StringBuffer(50);
                                idStructs.append("1");
                                security = HandlerGrupo.getAllSecurityForGroup(user.getIdGroup(),idStructs);
                                //Se Carga la Seguridad por Usuario Filtrando aquellos Nodos en Donde el Usuario no
                                //puede ver Carpetas
                                HandlerDBUser.getAllSecurityForUser(user.getIdPerson(),security,idStructs);
                                tree = HandlerStruct.loadAllNodes(security,user.getUser(),user.getIdGroup(),subNodos,idStructs.toString());
                                putObjectSession("security",security);
                            } else {
                                tree = HandlerStruct.loadAllNodes(security,user.getUser(),user.getIdGroup(),subNodos,null);
                            }

                        }

                        HandlerStruct.loadDocument(docForm,true,false,tree, request);
                        forma.setNumDocument(wfForm.getNumDocument());
                        //forma.setCopy(Integer.parseInt(getParameter("secuential"+indice)));
                        forma.setCopy(Integer.parseInt(getParameter("copy"+indice)));
                        forma.setSecuential(Integer.parseInt(getParameter("secuential"+indice)));
                        forma.setConditional(Integer.parseInt(getParameter("conditional"+indice)));
                        forma.setNotified(Integer.parseInt(getParameter("notified"+indice)));
                        forma.setExpire(Integer.parseInt(getParameter("expire"+indice)));
                        forma.setDateExpireWF(getParameter("dateExpireWF"+indice));
                        forma.setTypeWF(Integer.parseInt(getParameter("subNumber"+indice)));
                        forma.setNumVersion(docForm.getNumVer());
                        forma.setLastVersion(wfForm.getLastVersion());
                        String[] usuarios = request.getParameterValues("usersSelected"+indice);
                        String[] usersSelectedExpire = request.getParameterValues("usersSelectedExpire"+indice);
                        if (usuarios!=null) {
                            log.debug("usuarios[0] = " + usuarios[0]);
                            forma.setUsersSelected(usuarios);
                            forma.setUsersSelectedExpire(usersSelectedExpire);
                        }
                        forma.setComments(getParameter("comments"+indice)+getRestDataNotified(request,docForm,rb));
                        forma.setAct_Number(Long.parseLong(getParameter("nAct" + indice)));
//                        if (HandlerWorkFlows.insertII(forma,user,indice,statu)) {
//    //                        if (indice==0) {
//    //                            removeAttribute("comments",request);
//    //                            removeAttribute("showDataWF",request);
//    //                            removeAttribute("usersWF",request);
//                                DataWorkFlowForm dataWF = new DataWorkFlowForm();
//                                dataWF.setNumDocument(forma.getNumDocument());
//                                dataWF.setIdWorkFlow(String.valueOf(forma.getNewID()));
//                                HandlerWorkFlows.loadDataWorkFlow("flexworkflow",false,dataWF);
//                                dataWF.setNameFile(docForm.getNameFile());
//                                dataWF.setNameWF(getParameter("nSub"+indice));
//    //                            Collection usersWF = HandlerWorkFlows.getAllUserInWorkFlow(String.valueOf(forma.getNumWF()));
//                                dataWF.setUsers(HandlerWorkFlows.getAllUserInFlexFlow(forma.getNumWF()));
//    //                            putObjectSession("usersWF" + indice,usersWF);
//                                flujos.add(dataWF);
//    //                            putObjectSession("showDataWF",dataWF);
//                                putObjectSession("info",rb.getString("wf.ok"));
//                                //Code to show Charge User or Name User
//                                Hashtable tree = (Hashtable)getSessionObject("tree");
//                                tree = ToolsHTML.checkTree(tree,user);
//                                String idLocation = HandlerStruct.getIdLocationToNode(tree,new Long(docForm.getIdNode()));
//                                removeObjectSession("showCharge");
//                                if (idLocation!=null) {
//                                    BaseStructForm localidad = (BaseStructForm)tree.get(idLocation);
//                                    if (localidad!=null) {
//                                        if (localidad.getShowCharge()==1) {
//                                            putObjectSession("showCharge","true");
//                                        }
//                                    }
//                                }
//                                statu = HandlerWorkFlows.inQueued;
    //                       }
//                        }
                    }
                } else {
                    Collection actividadesFlex = (Collection)getSessionObject("actividadesFlex");
                    int posItem = Integer.parseInt(getParameter("posItem"));
                    if (actividadesFlex!=null) {
                        BaseWorkFlow bean = (BaseWorkFlow)form;
                        if (bean!=null) {
                            String[] usuarios = request.getParameterValues("usersSelected");
                            String[] usuariosExpire = request.getParameterValues("usersSelectedExpire");
                            log.debug("usuarios" + usuarios);
                            if (usuarios!=null) {
                                log.debug("usuarios[0] = " + usuarios[0]);
                                bean.setUserSelecteds(HandlerWorkFlows.getUsersSelectedCollection(usuarios));
                                bean.setUsersSelected(usuarios);

                                bean.setUserSelectedsExpire(HandlerWorkFlows.getCollection(usuariosExpire));
                                bean.setUsersSelectedExpire(usuariosExpire);
                            }
                            usuarios = request.getParameterValues("groupsSelected");
                            if (usuarios!=null) {
                                log.debug("grupos[0] = " + usuarios[0]);
                                bean.setGroupsSelected(usuarios);
                            }
                            actividadesFlex.toArray()[posItem] = bean;
                            int indice = 0;
                            //Carga del Documento
                            BaseDocumentForm docForm = new BaseDocumentForm();
                            String idDoc = String.valueOf(wfForm.getNumDocument());
                            docForm.setIdDocument(idDoc);
                            docForm.setNumberGen(idDoc);

                            Hashtable tree = (Hashtable)getSessionObject("tree");
                            if (tree==null) {
//                                Hashtable security = HandlerGrupo.getAllSecurityForGroupH(user.getIdGroup(),null);
//                                HandlerDBUser.getAllSecurityForUserH(user.getIdPerson(),security);
//                                tree = HandlerStruct.loadAllNodesH(security,user.getIdGroup());

                                boolean isAdmon = user.getIdGroup().equalsIgnoreCase(DesigeConf.getProperty("application.admon"));
                                Hashtable subNodos = new Hashtable();
                                Hashtable security = null;
                                if (!isAdmon) {
                                    StringBuffer idStructs = new StringBuffer(50);
                                    idStructs.append("1");
                                    security = HandlerGrupo.getAllSecurityForGroup(user.getIdGroup(),idStructs);
                                    //Se Carga la Seguridad por Usuario Filtrando aquellos Nodos en Donde el Usuario no
                                    //puede ver Carpetas
                                    HandlerDBUser.getAllSecurityForUser(user.getIdPerson(),security,idStructs);
                                    tree = HandlerStruct.loadAllNodes(security,user.getUser(),user.getIdGroup(),subNodos,idStructs.toString());
                                    putObjectSession("security",security);
                                } else {
                                    tree = HandlerStruct.loadAllNodes(security,user.getUser(),user.getIdGroup(),subNodos,null);
                                }

                            }
                            log.debug("Cargando Datos del Documento...");
                            HandlerStruct.loadDocument(docForm,true,false,null, request);
                            //se Crean las Actividades Asociadas al Flujo de Trabajo
//                            EditWorkFlowAction eWF = new EditWorkFlowAction();
                            String dataDocument = EditWorkFlowAction.getRestDataNotified(request,docForm,getRb(),user.getNamePerson());
                            log.debug("Data Document: " + dataDocument) ;
                            int cont = 0;
//                            String dateEndAct = "";
                            Vector datePreviuosAct = new Vector();
                            for (Iterator iterator = actividadesFlex.iterator(); iterator.hasNext();) {
                                BaseWorkFlow baseWorkFlow = (BaseWorkFlow) iterator.next();
                                //Si No hay Comentarios para la Actividad Actual
                                if (ToolsHTML.isEmptyOrNull(baseWorkFlow.getComments())) {
                                    putObjectSession("newWF",baseWorkFlow);
                                    putObjectSession("posAct",String.valueOf(cont));
                                    throw new ApplicationExceptionChecked("E0104");
                                }
                                log.debug("Actividad # " + cont);
                                log.debug("Actividad: " + baseWorkFlow.getNameAct());
                                log.debug("comments: " + baseWorkFlow.getComments());
                                log.debug("Grupos: " + baseWorkFlow.getGroupsSelected());
                                log.debug("Usuarios: " + baseWorkFlow.getUsersSelected());
                                //Validar Usuarios y Grupos en la Actividad
                                if ((baseWorkFlow.getGroupsSelected()==null||
                                    baseWorkFlow.getGroupsSelected().length==0)&&
                                     (baseWorkFlow.getUsersSelected()==null||
                                     baseWorkFlow.getUsersSelected().length==0)) {
                                    putObjectSession("newWF",baseWorkFlow);
                                    putObjectSession("posAct",String.valueOf(cont));
                                    throw new ApplicationExceptionChecked("E0103");
                                }
                                //Validando Usuarios
//                                if (baseWorkFlow.getUsersSelected()!=null&&
//                                    baseWorkFlow.getUsersSelected().length==0) {
//                                    putObjectSession("newWF",baseWorkFlow);
//                                    putObjectSession("posAct",String.valueOf(cont));
//                                    throw new ApplicationExceptionChecked("E0061");
//                                }
                                log.debug("" + baseWorkFlow.getDateExpireWF());
                                //Validando Fechas de Culminación de las Actividades
//                                if (dateEndAct.compareToIgnoreCase(baseWorkFlow.getDateExpireWF()) > 0) {
//                                    putObjectSession("newWF",baseWorkFlow);
//                                    putObjectSession("posAct",String.valueOf(cont));
//                                    throw new ApplicationExceptionChecked("E0063");
//                                }
                                for (int pos=0; pos < datePreviuosAct.size(); pos++) {
                                    //Si el Usuario introdujo alguna Fecha
                                    log.debug("Fecha en la Actividad '" + baseWorkFlow.getDateExpireWF() + "'");
                                    if (baseWorkFlow.getDateExpireWF()!=null&&!"".equalsIgnoreCase(baseWorkFlow.getDateExpireWF())) {
                                        //Se Verifica la Fecha con todas las Fechas anteriores Introducidos por el Usuario
                                        //Esto debido a Act A con Fecha XX Act B con Fecha "" y Act C con Fecha YY
                                        //Si YY es menor que XX el Modelo se crearía con Fechas Inválidas en las Actividades
                                        if (((String)datePreviuosAct.get(pos)).compareToIgnoreCase(baseWorkFlow.getDateExpireWF())>0) {
                                            putObjectSession("newWF",baseWorkFlow);
                                            putObjectSession("posAct",String.valueOf(cont));
                                            throw new ApplicationExceptionChecked("E0105");
                                        }
                                    }
                                }
                                baseWorkFlow.setTitleForMail(getMessage("wf.newWFTitle") + " " + docForm.getPrefix() + " " + docForm.getNumber());
                                baseWorkFlow.setComments(baseWorkFlow.getComments());
//                                baseWorkFlow.setComments(baseWorkFlow.getComments()+getRestDataNotified(request,docForm,rb));
                                datePreviuosAct.add(baseWorkFlow.getDateExpireWF()!=null?baseWorkFlow.getDateExpireWF():"");
                                cont++;
                            }

                              //Luis Cisneros
                              //28-03-07
                              //Antes de insertar el flujo, debemos indicarle a todos los demas flujos que al campo de MostarEnCacelados va a ser false
                              //Para que no se muestre en el listado

                               HandlerWorkFlows.updateWFViewInCanceled(docForm.getIdDocument(), "0", "1");

                              //Fin 28-03-07


                            flujos.addAll(HandlerWorkFlows.insertFlexFlow(actividadesFlex,user,0,statu,docForm.getNameFile(),
                                                                          getMessage("wf.pending"),dataDocument));
                            //Se Agrega los Datos del Documento...
                            if  (flujos.size() > 0) {
                                for (Iterator iterator = flujos.iterator(); iterator.hasNext();) {
                                    DataWorkFlowForm dataWorkFlow = (DataWorkFlowForm) iterator.next();
                                    dataWorkFlow.setComments(dataWorkFlow.getComments() + "<br/>" + dataDocument);
                                }
                                if(request.getParameter("isSendToFlexWF")!=null && request.getParameter("isSendToFlexWF").equals("true")) {
                                	request.setAttribute("isSendToFlexWF", "true");
                                } else {
                                	putObjectSession("info",rb.getString("wf.ok"));
                                }
                            }

                            //Code to show Charge User or Name User
//                            Hashtable tree = (Hashtable)getSessionObject("tree");
//                            tree = ToolsHTML.checkTree(tree,user);
                            removeObjectSession("showCharge");
                            String idLocation = HandlerStruct.getIdLocationToNode(tree,docForm.getIdNode());
                            log.debug("idLocation: " + idLocation);
                            if (ToolsHTML.isNumeric(idLocation)) {
                                BaseStructForm localidad = (BaseStructForm)tree.get(new Long(idLocation));
                                log.debug("localidad: " + localidad);
                                if (localidad!=null) {
                                    if (localidad.getShowCharge()==1) {
                                        putObjectSession("showCharge","true");
                                    }
                                }
                            }
                        }
                    }
                }
            } else {
                throw new ApplicationExceptionChecked("E0101");
            }
            if (flujos.size()> 0) {
                log.debug("flujos.size() " + flujos.size());
                putObjectSession("flujos",flujos);
            }
            return goSucces();
        } catch (ApplicationExceptionChecked ap) {
            ap.printStackTrace();
            return goError(ap.getKeyError());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return goError();
    }

    public static synchronized String getRestDataNotified(HttpServletRequest request,BaseDocumentForm docForm,
                                                          ResourceBundle rb) {
        StringBuffer rest = new StringBuffer(100);
        String dirServer = ToolsHTML.getServerPath(ToolsHTML.getServerName(request),request.getServerPort(),request.getContextPath());

        rest.append("<br/>");
        rest.append(rb.getString("doc.name")).append(": ").append(docForm.getNameDocument());
        rest.append("<br/>");
        rest.append(rb.getString("doc.Ver")).append(": ").append(docForm.getMayorVer()).append(".").append(docForm.getMinorVer());
        rest.append("<br/>");
        rest.append("<a href='");
        //rest.append(dirServer).append("/viewDocument.jsp?nameFile=").append(docForm.getNameFile()).append("&idDocument=");
        //rest.append(docForm.getIdDocument()).append("&idVersion=").append(docForm.getNumVer());
        rest.append(dirServer).append(!dirServer.endsWith("/")?"/":"").append("loadDataDoc.do?nameFile=").append(docForm.getNameFile()).append("&idDocument=");
        rest.append(docForm.getIdDocument()).append("&idVersion=").append(docForm.getNumVer());
        rest.append("' target='_blank'>").append(rb.getString("wf.view")).append("</a>");
        
        return rest.toString();
    }

}
