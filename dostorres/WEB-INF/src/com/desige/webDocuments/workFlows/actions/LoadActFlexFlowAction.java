package com.desige.webDocuments.workFlows.actions;

import java.util.Collection;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.desige.webDocuments.persistent.managers.HandlerGrupo;
import com.desige.webDocuments.persistent.managers.HandlerWorkFlows;
import com.desige.webDocuments.utils.ToolsHTML;
import com.desige.webDocuments.utils.beans.Search;
import com.desige.webDocuments.utils.beans.SuperAction;
import com.desige.webDocuments.workFlows.forms.BaseWorkFlow;


/**
 * Title: LoadActFlexFlowAction.java <br/>
 * Copyright: (c) 2004 Desige Servicios de Computación<br/>
 *
 * @author Ing. Nelson Crespo
 * @version WebDocuments v1.0
 * <br/>
 *      Changes:<br/>
 * <ul>
 *      <li> 14/03/2006 (NC) Creation </li>
 * </ul>
 */
public class LoadActFlexFlowAction extends SuperAction {
    static Logger log = LoggerFactory.getLogger(LoadActFlexFlowAction.class.getName());
    public ActionForward execute(ActionMapping actionMapping,
                                 ActionForm form, HttpServletRequest request,
                                 HttpServletResponse response) {
        super.init(actionMapping,form,request,response);
        try {
            int posItem = Integer.parseInt(getParameter("posItem"));
            int posNew  = Integer.parseInt(getParameter("posNew"));
            Collection actividadesFlex = (Collection)getSessionObject("actividadesFlex");
            //Se Toma la Fecha de Expiración del Flujo de Trabajo y se actualiza en la session del Usuario

//            String dateExpireWF = getParameter("dateExpireWF");
//            log.debug("dateExpireWF " + dateExpireWF);
//            if (ToolsHTML.isEmptyOrNull(dateExpireWF)) {
//                putObjectSession("dateEndWF","");
//            } else {
//                putObjectSession("dateEndWF",dateExpireWF);
//            }
            if (actividadesFlex!=null) {
                String idDocument = getParameter("idDocument");
                if (!ToolsHTML.isEmptyOrNull(idDocument)) {
                    putAtributte("idDocument",idDocument);
                }
                BaseWorkFlow bean = (BaseWorkFlow)form;
                if (bean!=null) {
                    String[] usuarios = request.getParameterValues("usersSelected");
                    String[] usuariosExpire = request.getParameterValues("usersSelectedExpire");
                    log.debug("usuarios" + usuarios);
                    //Se Recargan los Usuarios autorizados a Participar en el Flujo de Trabajo
                    String idDoc = String.valueOf(bean.getNumDocument());
                    String typeWF = String.valueOf(bean.getTypeWF());
                    Collection groups = HandlerGrupo.getAllGroupsToDoc(bean.getIdNodeDocument(),idDoc,
                                                                       typeWF,true);
                    Collection<Search> allUser = HandlerGrupo.getSecurityForNode(bean.getIdNodeDocument(),true,null);
                    
                    // cargamos los usuarios viewers
                    Collection<Search> usuariosViewer = HandlerGrupo.getUsersViewers(false,"1");
                    
                    //removemos los viewers de la lista completa de usuarios
                    allUser.removeAll(usuariosViewer);
                    
                    //Se actualizan los Usuarios seleccionados en la Actividad Asociada al Flujo
                    if (usuarios!=null) {
                        Collection data = HandlerWorkFlows.getUsersSelectedCollection(usuarios);
                        Collection dataExpire = HandlerWorkFlows.getCollection(usuariosExpire);
                        bean.setUserSelecteds(data);
                        bean.setUserSelectedsExpire(dataExpire);
                        bean.setUsersSelected(usuarios);
                        bean.setUsersSelectedExpire(usuariosExpire);
                        allUser.removeAll(data);
                    }
                    usuarios = request.getParameterValues("groupsSelected");
                    if (usuarios!=null) {
                        log.debug("grupos[0] = " + usuarios[0]);
                        bean.setGroupsSelected(usuarios);
                    }
                    actividadesFlex.toArray()[posItem] = bean;
                    putObjectSession("newWF",actividadesFlex.toArray()[posNew]);
                    putObjectSession("posAct",getParameter("posNew"));
                    //Se Actualizan los Usuarios y los grupos en la Vista del Usuario
                    putObjectSession("usuarios"+typeWF,allUser);
                    putObjectSession("usuariosViewer"+typeWF,usuariosViewer);
                    putObjectSession("usuariosViewer"+"7",usuariosViewer);
                    putObjectSession("groups"+typeWF,groups);
                }
            }
            return goSucces();
        } catch (Exception ex) {
            log.error(ex.getMessage());
            ex.printStackTrace();
        }
        return goError();
    }
}
