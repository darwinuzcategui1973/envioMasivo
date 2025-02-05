package com.desige.webDocuments.seguridad.actions;

import java.util.Hashtable;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.desige.webDocuments.persistent.managers.HandlerDBUser;
import com.desige.webDocuments.seguridad.forms.PermissionUserForm;
import com.desige.webDocuments.structured.forms.BaseStructForm;
import com.desige.webDocuments.utils.ApplicationExceptionChecked;
import com.desige.webDocuments.utils.Constants;
import com.desige.webDocuments.utils.ToolsHTML;
import com.desige.webDocuments.utils.beans.SuperAction;
import com.desige.webDocuments.utils.beans.Users;

/**
 * Title: DeleteSecurityStructUser.java <br/>
 * Copyright: (c) 2004 Focus Consulting C.A.<br/>
 * @author Ing. Nelson Crespo
 * @version WebDocuments v3.0
 * <br/>
 *      Changes:<br/>
 * <ul>
 *      <li> 13/04/2005 (NC) Creation </li>
 * </ul>
 */
public class DeleteSecurityStructUser extends SuperAction {
    public ActionForward execute(ActionMapping actionMapping,
            ActionForm form, HttpServletRequest request,
            HttpServletResponse response) {
        super.init(actionMapping,form,request,response);
        try {
            Users user = getUserSession();
            getUserSession();
            PermissionUserForm forma = (PermissionUserForm)form;
            //02 de septiembre 2005 inicio
              boolean statu = false;
              String idNodeSelected = getParameter("idNodeSelected");
              String idDocument = getParameter("idDocument");
              String command = getParameter("command");
            ///*
            //  sql.append("DELETE FROM permissionstructuser WHERE idStruct = ").append(forma.getIdStruct());
        ///sql.append(" AND idPerson = ").append(forma.getIdPerson());
            // */


                Hashtable tree = (Hashtable)getSessionObject("tree");
                //obtenemos todos los nodos dependiendo de la permisologia del grupo y del usuario al que pertenece dicho usuario
                tree = ToolsHTML.checkTree(tree,user);
                BaseStructForm carpeta = (BaseStructForm)tree.get(forma.getIdStruct());
                boolean isFolderOrProcess = false;
                boolean isSite = false;
                //System.out.println("carpeta="+carpeta);
//                if (carpeta!=null) {
//                    isFolderOrProcess = DesigeConf.getProperty("processType").equalsIgnoreCase(carpeta.getNodeType())
//                                        ||DesigeConf.getProperty("folderType").equalsIgnoreCase(carpeta.getNodeType());
//                    isSite = DesigeConf.getProperty("siteType").equalsIgnoreCase(carpeta.getNodeType());
//                }
                //actualizamos la seguidad del usuario
                statu = HandlerDBUser.deleteSecurityStructUser(forma,user.getUser());
                /*Hashtable security = HandlerGrupo.getAllSecurityForGroup(user.getIdGroup());
                HandlerDBUser.getAllSecurityForUser(user.getIdPerson(),security);
                tree = HandlerStruct.loadAllNodes(security,user.getUser(),user.getIdGroup(),false);
                putObjectSession("security",security);
                putObjectSession("tree",tree);
            */
            //02 de septembre 2005 fin
            if (forma.getCommand().equalsIgnoreCase(Constants.cmdToStruct)) {
              //   statu = HandlerDBUser.deleteSecurityStructUser(forma);
            }
            if (statu) {
                ActionForward toOk = actionMapping.findForward("success");
                if (toOk!=null) {
                    //System.out.println("toOk.getPath() = " + toOk.getPath());
                }
                putObjectSession("info",getMessage("seguridad.editOk"));
                StringBuffer parameters = new StringBuffer(60);
                parameters.append(toOk.getPath()).append("?idNodeSelected=").append(idNodeSelected);
                parameters.append("&idDocument=").append(idDocument).append("&command=").append(command);
                try {
                	ActionForward oActionForward = new ActionForward();
                	oActionForward.setPath(parameters.toString());
                    return oActionForward;
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                return goSucces("seguridad.editOk");
            }
        } catch (ApplicationExceptionChecked ae) {
            return goError(ae.getKeyError());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return goError();
    }
}
