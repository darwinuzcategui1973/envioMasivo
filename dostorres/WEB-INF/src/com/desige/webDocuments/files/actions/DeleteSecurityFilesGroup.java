package com.desige.webDocuments.files.actions;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.desige.webDocuments.persistent.managers.HandlerDBUser;
import com.desige.webDocuments.seguridad.forms.PermissionUserForm;
import com.desige.webDocuments.utils.ApplicationExceptionChecked;
import com.desige.webDocuments.utils.beans.SuperAction;
import com.desige.webDocuments.utils.beans.Users;

/**
 * Created by IntelliJ IDEA.
 * User: srodriguez
 * Date: 24/08/2005
 * Time: 04:10:28 PM
 * To change this template use File | Settings | File Templates.
 */
public class DeleteSecurityFilesGroup extends SuperAction {
    public ActionForward execute(ActionMapping actionMapping,
            ActionForm form, HttpServletRequest request,
            HttpServletResponse response) {
        super.init(actionMapping,form,request,response);
        try {
            Users usuario =getUserSession();
            PermissionUserForm forma = (PermissionUserForm)form;
            //System.out.println("forma.getIdGroup()="+forma.getIdGroup());
            boolean statu = false;
            forma.setIdPerson(usuario.getIdPerson());
            statu = HandlerDBUser.deleteSecurityFilesGroup(forma);

            return goSucces();
        } catch (ApplicationExceptionChecked ae) {
            return goError(ae.getKeyError());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return goError();
    }
}
