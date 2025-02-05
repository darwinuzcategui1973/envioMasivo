package com.desige.webDocuments.structured.actions;

import java.util.Collection;
import java.util.Hashtable;
import java.util.Iterator;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.desige.webDocuments.persistent.managers.HandlerDBUser;
import com.desige.webDocuments.persistent.managers.HandlerDocuments;
import com.desige.webDocuments.persistent.managers.HandlerProcesosSacop;
import com.desige.webDocuments.persistent.managers.HandlerStruct;
import com.desige.webDocuments.structured.forms.BaseStructForm;
import com.desige.webDocuments.utils.ApplicationExceptionChecked;
import com.desige.webDocuments.utils.ToolsHTML;
import com.desige.webDocuments.utils.beans.Search;
import com.desige.webDocuments.utils.beans.SuperAction;
import com.desige.webDocuments.utils.beans.SuperActionForm;
import com.desige.webDocuments.utils.beans.Users;

/**
 * Title: LoadEditStructAction.java <br/> 
 * Copyright: (c) 2004 Focus Consulting C.A.<br/>
 * @author Ing. Nelson Crespo (NC)
 * @version WebDocuments v3.0
 * <br/>
 * Changes:<br/> 
 * <ul>
 *      <li> 18/04/2004 (NC) Creation </li>
 *      <li> 22/05/2005 (NC) Se modificó para el manejo dinámico de los prefijos </li>
 *      <li> 20/09/2005 (NC) Cambios para cargar los usuarios del Sistema </li>
 *      <li> 13/07/2006 (NC) Cambios para heredar o no los Prefijos de las Carpetas </li>
 </ul>
 */
public class LoadEditStructAction extends SuperAction {
    static Logger log = LoggerFactory.getLogger("[V4.0] " + LoadEditStructAction.class.getName());
    public ActionForward execute(ActionMapping actionMapping,
                                 ActionForm form, HttpServletRequest request,
                                 HttpServletResponse response) {
        super.init(actionMapping,form,request,response);
        log.debug("[LoadEditStructAction]");
        try {
            HandlerStruct handlerStruct = new HandlerStruct();
            Users user = getUserSession();
            String idNode = ((BaseStructForm)form).getIdNode();
            log.debug("idNode = " + idNode);
            long idStruct = Long.parseLong(idNode);
            log.debug("idStruct = " + idStruct);
            Hashtable secUsers = HandlerStruct.loadSecurityStruct(true,idStruct);
            Hashtable secGroups = HandlerStruct.loadSecurityStruct(false,idStruct);
            Collection<Search> allUsers = HandlerDBUser.getAllUsers(secUsers,secGroups,user.getUser());
            HandlerStruct.load(user.getUser(),(BaseStructForm)form);
            
            // validamos si el responsable del area no tienes sacops pendientes
            String nameResponsible = ((BaseStructForm)form).getOwnerResponsible();
            for (Iterator<Search> iterator = allUsers.iterator(); iterator.hasNext();) {
				Search search = (Search) iterator.next();
				if(search.getDescript().equals(nameResponsible)) {
					nameResponsible = search.getId();
					break;
				}
			}
            request.setAttribute("tieneSacopsAbiertas", HandlerProcesosSacop.isSacopOpenByNameUser(nameResponsible));

            Hashtable tree = (Hashtable)getSessionObject("tree");
            tree = ToolsHTML.checkTree(tree,user);
            //Se Chequea como se debe construir los prefijos
            byte typePrefix = 0;
//            byte heredarPrefijo = 0;
            String idLocation = HandlerStruct.getIdLocationToNode(tree,idNode);
            //Se Carga los Datos de la Localidad a la Que pertenece la Carpeta para Determinar
            //Si Aplica o No la Herencia de Prefijos
            if (idLocation!=null) {
                BaseStructForm localidad = (BaseStructForm)tree.get(idLocation);
                if (localidad!=null) {
                    typePrefix = localidad.getTypePrefix();
//                    heredarPrefijo = localidad.getHeredarPrefijo();
                }
            }
//            if (Constants.notPermission == heredarPrefijo) {
//                ((BaseStructForm)form).setParentPrefix("");
//            } else {
                String prefix = handlerStruct.getParentPrefixinst(tree,idNode,0==typePrefix);
                log.debug("[LoadEditStructAction] prefix = " + prefix);
                ((BaseStructForm)form).setParentPrefix(prefix);
//            }
            request.setAttribute("nodeType",((BaseStructForm)form).getNodeType().trim());
            putObjectSession("cmd",SuperActionForm.cmdEdit);
            putObjectSession("editarStruct",form);
            putObjectSession("allUsers",allUsers);

            //si no es el nodo padre
            if (!"1".equalsIgnoreCase(idNode)){
                //verificamos si y hay documentos para habilitaro desabilitar el preijo ascendente o descendente
                 //Si la Búsqueda es por Carpetas, se deben buscar todos los nodos hijos a partir del Nodo Indicado
                String hijos ="";
                if (!ToolsHTML.isEmptyOrNull(idNode)) {
                   // sql.append(" AND d.idNode = ").append(idNode).append("");
                    hijos = HandlerStruct.getAllNodesChilds(idNode);
                    log.debug("Hijos: " + hijos);
                }
                //buscamos todos los hijos si tiene documentos en documents
                boolean hayDocsHijos=false;
                if (!ToolsHTML.isEmptyOrNull(hijos)) {
                    hayDocsHijos=HandlerDocuments.isNodeDocuments(hijos);
                }
                //buscamos el padre si tiene documentos en documents
                boolean hayDocs1Padres=false;
                if (!ToolsHTML.isEmptyOrNull(idNode)) {
                    hayDocs1Padres=HandlerDocuments.isNodeDocuments(idNode);
                }
                putObjectSession("hayDocs",new Boolean((hayDocs1Padres || hayDocsHijos )));
            }else{
                //el nodo padre siempre va a ser creada por primera vez una localidad.
                putObjectSession("hayDocs",new Boolean(false));
            }
          
            log.debug("[Return]");
            return goSucces();
        } catch (ApplicationExceptionChecked ae) {
            ae.printStackTrace();
            return goError(ae.getKeyError());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return goError();
    }


}
