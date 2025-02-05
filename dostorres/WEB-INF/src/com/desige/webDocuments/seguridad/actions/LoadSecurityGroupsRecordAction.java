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

/**
 * Title: LoadSecurityGroupsAction.java <br/>
 * Copyright: (c) 2004 Focus Consulting C.A.<br/>
 *
 * @author Ing. Nelson Crespo
 * @version WebDocuments v3.0
 * <br/>
 *      Changes:<br/>
 * <ul>
 *      <li> 21/12/2004 (NC) Creation </li>
 * </ul>
 */
public class LoadSecurityGroupsRecordAction extends SuperAction {
    public ActionForward execute(ActionMapping actionMapping,
            ActionForm form, HttpServletRequest request,
            HttpServletResponse response) {
        super.init(actionMapping,form,request,response);
        try {
            getUserSession();
            PermissionUserForm forma = (PermissionUserForm)form;
            
            forma.setCommand(Constants.cmdToStruct);
            forma.setRout(HandlerGrupo.getNameGroup(forma.getIdGroup()));
            
            HandlerGrupo.loadSecurityStructGroupRecord(forma);
            
            return goSucces();
        } catch (ApplicationExceptionChecked ae) {
            return goError(ae.getKeyError());
        } catch (Exception e) {
            e.printStackTrace();
            return goError("E0019");
        }
    }
}
