package com.desige.webDocuments.seguridad.actions;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.desige.webDocuments.persistent.managers.HandlerGrupo;
import com.desige.webDocuments.seguridad.forms.PermissionUserForm;
import com.desige.webDocuments.utils.ApplicationExceptionChecked;
import com.desige.webDocuments.utils.Constants;
import com.desige.webDocuments.utils.beans.SuperAction;
import com.desige.webDocuments.utils.beans.Users;

/**
 * Title: DeleteSecurityGroupStruct.java <br/>
 * Copyright: (c) 2004 Focus Consulting C.A.<br/>
 * @author Ing. Nelson Crespo
 * @version WebDocuments v3.0
 * <br/>
 *      Changes:<br/>
 * <ul>
 *      <li> 12/04/2005 (NC) Creation </li>
 * </ul>
 */
public class DeleteSecurityGroupStruct extends SuperAction {
    public ActionForward execute(ActionMapping actionMapping,
            ActionForm form, HttpServletRequest request,
            HttpServletResponse response) {
        super.init(actionMapping,form,request,response);
        try {
            Users user = getUserSession();
            getUserSession();
            PermissionUserForm forma = (PermissionUserForm)form;
            String idNodeSelected = getParameter("idNodeSelected");
            String idDocument = getParameter("idDocument");
            String command = getParameter("command");
            boolean statu = false;

            if (forma.getCommand().equalsIgnoreCase(Constants.cmdToStruct)) {
//                Hashtable tree = (Hashtable)getSessionObject("tree");
//                tree = ToolsHTML.checkTree(tree,user);
//                BaseStructForm carpeta = (BaseStructForm)tree.get(forma.getIdStruct());
//                boolean isFolderOrProcess = false;
//                boolean isSite = false;
//                if (carpeta!=null) {
//                    isFolderOrProcess = DesigeConf.getProperty("processType").equalsIgnoreCase(carpeta.getNodeType())
//                                        ||DesigeConf.getProperty("folderType").equalsIgnoreCase(carpeta.getNodeType());
//                    isSite = DesigeConf.getProperty("siteType").equalsIgnoreCase(carpeta.getNodeType());
//                }
                statu = HandlerGrupo.deleteSecurityStructGroups(forma);
            } /*else {
                statu = HandlerDocuments.updateSecurityDocumentGroup(forma);
            }*/
          /*  if (forma.getCommand().equalsIgnoreCase(Constants.cmdToStruct)) {
                 statu = HandlerGrupo.deleteSecurityStructGroup(forma);
            }*/
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
                	ActionForward actionForward = new ActionForward();
                	actionForward.setPath(parameters.toString());
                    return actionForward;
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
