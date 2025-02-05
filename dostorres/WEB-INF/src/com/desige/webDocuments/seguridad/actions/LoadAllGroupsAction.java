package com.desige.webDocuments.seguridad.actions;

import java.io.File;
import java.util.Collection;
import java.util.Hashtable;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.desige.webDocuments.persistent.managers.HandlerGrupo;
import com.desige.webDocuments.structured.forms.BaseStructForm;
import com.desige.webDocuments.utils.ApplicationExceptionChecked;
import com.desige.webDocuments.utils.Constants;
import com.desige.webDocuments.utils.ToolsHTML;
import com.desige.webDocuments.utils.beans.SuperAction;

/**
 * Title: LoadAllGroupsAction.java <br/>
 * Copyright: (c) 2004 Focus Consulting C.A.<br/>
 * @author Ing. Nelson Crespo
 * @version WebDocuments v3.0
 * <br/>
 *      Changes:<br/>
 * <ul>
 *      <li> 21/12/2004 (NC) Creation </li>
 * </ul>
 */
public class LoadAllGroupsAction extends SuperAction {
    public ActionForward execute(ActionMapping actionMapping,
            ActionForm form, HttpServletRequest request,
            HttpServletResponse response) {
        super.init(actionMapping,form,request,response);
        try {
            getUserSession();
            removeAttribute("idStruct");
            removeAttribute("usuarios");
            removeAttribute("idDocument");
            removeAttribute("command");
            removeAttribute("rout");
            removeAttribute("nodeType");
            removeAttribute("groupsWithSec");
            removeAttribute("groups");

            String idStruct = getParameter("idNodeSelected");
            String toSearch = getParameter("toSearch");
            String idDocument = getParameter("idDocument");
            String nodeType = getParameter("nodeType");
            //System.out.println("idStruct = " + idStruct);
            //System.out.println("toSearch = " + toSearch);
            //System.out.println("idDocument = " + idDocument);
            String command = getParameter("command");
            removeObjectSession("securityUser");
            putObjectSession("nodeType",nodeType);
            String toStruct = request.getParameter("toStruct");
            Collection secPreviuos = new Vector();
            //si hay documentos en permisiondocGroup, me los trae
            if (!ToolsHTML.isEmptyOrNull(idDocument)){
                secPreviuos=HandlerGrupo.getSecutityForAllGroupsInDocs(idDocument);
            }
            if (!ToolsHTML.isEmptyOrNull(toStruct)||Constants.cmdToStruct.equalsIgnoreCase(command)) {
                secPreviuos=null;
                secPreviuos = HandlerGrupo.getSecutityForAllGroupsInNode(idStruct,toSearch);
                putObjectSession("command",Constants.cmdToStruct);
                Hashtable tree = (Hashtable)getSessionObject("tree");
                if (tree!=null) {
                    BaseStructForm dataForm = (BaseStructForm)tree.get(idStruct);
                    //System.out.println("dataForm.getRout() = " + dataForm.getRout());

                    putObjectSession("rout",dataForm.getRout());
                    putObjectSession("routClean",dataForm.getRoutWithoutLinksNoHTML());
                }
            } else {
                putObjectSession("command",Constants.cmdToDocument);
                Hashtable tree = (Hashtable)getSessionObject("tree");
                if (tree!=null) {
                    BaseStructForm dataForm = (BaseStructForm)tree.get(idStruct);
                    String nameDocument = getParameter("nameDocument");
                    if (nameDocument!=null&&dataForm!=null) {
                        putObjectSession("rout",dataForm.getRout()+File.separator+nameDocument);
                        putObjectSession("routClean",dataForm.getRoutWithoutLinksNoHTML() + File.separator+nameDocument);
                        putAtributte("nameDocument",nameDocument);
                    }
                }
            }
            if (!ToolsHTML.isEmptyOrNull(toSearch)) {
                secPreviuos = new Vector();
            }
            Collection groups = HandlerGrupo.getAllGroups(toSearch);
            groups.removeAll(secPreviuos);
            putObjectSession("groupsWithSec",secPreviuos);
            putObjectSession("idStruct",idStruct);
            putObjectSession("groups",groups);
            putObjectSession("idDocument",idDocument);
            return goSucces();
        } catch (ApplicationExceptionChecked ae) {
            return goError(ae.getKeyError());
        } catch (Exception e) {
            e.printStackTrace();
            return goError("E0100");
        }
    }
}
