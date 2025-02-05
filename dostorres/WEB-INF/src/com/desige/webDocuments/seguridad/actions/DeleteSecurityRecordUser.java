package com.desige.webDocuments.seguridad.actions;

/**
 * Created by IntelliJ IDEA.
 * User: srodriguez
 * Date: 11/08/2005
 * Time: 05:23:54 PM
 * To change this template use File | Settings | File Templates.
 */

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.desige.webDocuments.persistent.managers.HandlerDBUser;
import com.desige.webDocuments.seguridad.forms.PermissionUserForm;
import com.desige.webDocuments.utils.ApplicationExceptionChecked;
import com.desige.webDocuments.utils.beans.SuperAction;

/**
 * Created by IntelliJ IDEA.
 * User: srodriguez
 * Date: 11/08/2005
 * Time: 05:13:17 PM
 * To change this template use File | Settings | File Templates.
 */
public class DeleteSecurityRecordUser extends SuperAction{
      public ActionForward execute(ActionMapping actionMapping,
            ActionForm form, HttpServletRequest request,
            HttpServletResponse response) {
        super.init(actionMapping,form,request,response);
        try {
            getUserSession();

            PermissionUserForm forma = (PermissionUserForm)form;
            boolean statu = false;

            String idNodeSelected = getParameter("idNodeSelected");
            String idDocument = getParameter("idDocument");
            String command = getParameter("command");
            
            statu = HandlerDBUser.deleteSecurityRecordUser(forma);
            return goSucces();
           /* if (statu) {
                ActionForward toOk = actionMapping.findForward("success");
            }*/
        } catch (ApplicationExceptionChecked ae) {
            return goError(ae.getKeyError());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return goError();
    }


}
