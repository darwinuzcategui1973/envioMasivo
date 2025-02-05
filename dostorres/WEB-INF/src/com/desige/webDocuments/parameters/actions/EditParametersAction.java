package com.desige.webDocuments.parameters.actions;

import java.util.ResourceBundle;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import sun.jdbc.rowset.CachedRowSet;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.desige.webDocuments.parameters.forms.BaseParametersForm;
import com.desige.webDocuments.persistent.managers.HandlerParameters;
import com.desige.webDocuments.utils.ApplicationExceptionChecked;
import com.desige.webDocuments.utils.ToolsHTML;
import com.desige.webDocuments.utils.beans.SuperAction;
import com.desige.webDocuments.utils.beans.Users;
import com.focus.qweb.facade.AuditFacade;
import com.focus.qweb.to.AuditTO;

/**
 * Title: EditParametersAction.java <br/> 
 * Copyright: (c) 2004 Focus Consulting C.A.<br/>
 * @author Ing. Nelson Crespo (NC)
 * @version WebDocuments v3.0
 * <br/>
 * Changes:<br/> 
 * <ul>
 *      <li>21/05/2004 (NC) Creation </li>
 </ul>
 */
public class EditParametersAction extends SuperAction {
    public ActionForward execute(ActionMapping actionMapping,
                                 ActionForm form, HttpServletRequest request,
                                 HttpServletResponse httpServletResponse) {
        super.init(actionMapping,form,request,httpServletResponse);
        try {
            Users user = getUserSession();
            ResourceBundle rb = ToolsHTML.getBundle(request);
            BaseParametersForm formNewValue = (BaseParametersForm)form;
            CachedRowSet parameterOld = HandlerParameters.getParameters();
            
            if (HandlerParameters.save(formNewValue)) {
            	request.setAttribute("info",rb.getString("app.editOk"));
            	
            	// Comparamos los attributos que han cambiado
            	AuditFacade.insertarCambiosAuditFacade(rb, user, parameterOld, HandlerParameters.getParameters(), AuditTO.getClientIpAddress(request));
            } else{
                StringBuffer mensaje = new StringBuffer(rb.getString("app.notEdit"));
                mensaje.append("<br/>").append(HandlerParameters.getMensaje());
                request.setAttribute("info",mensaje.toString());
            }
            return goSucces();
        } catch (ApplicationExceptionChecked ae) {
            ae.printStackTrace();
            return goError(ae.getKeyError());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return goError();
    }
}
