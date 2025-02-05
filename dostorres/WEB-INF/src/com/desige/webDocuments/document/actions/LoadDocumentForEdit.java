package com.desige.webDocuments.document.actions;

import java.util.Collection;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.desige.webDocuments.document.forms.BaseDocumentForm;
import com.desige.webDocuments.persistent.managers.HandlerDBUser;
import com.desige.webDocuments.persistent.managers.HandlerNorms;
import com.desige.webDocuments.utils.ApplicationExceptionChecked;
import com.desige.webDocuments.utils.beans.SuperAction;
import com.desige.webDocuments.utils.beans.SuperActionForm;

/**
 * Title: ${name}.java<br>
 * Copyright: (c) 2003 Consis International<br>
 * Company: Consis International (CON)<br>
 * @author Nelson Crespo (NC)
 * @version Acsel-e v2.2
 * <br>
 * Changes:<br>
 * <ul>
 *      <li> 27/07/2004 (NC) Creation </li>
 * <ul>
 */
public class LoadDocumentForEdit extends SuperAction {
    public ActionForward execute(ActionMapping mapping,
                                 ActionForm form, HttpServletRequest request,
                                 HttpServletResponse response) {
        super.init(mapping,form,request,response);
        try {
            getUserSession();
            Collection norms = HandlerNorms.getAllNorms();
            Collection users = HandlerDBUser.getAllUsers();
            BaseDocumentForm forma  = (BaseDocumentForm)request.getSession().getAttribute("showDocument");
            if (forma!=null){
                forma.setCmd(SuperActionForm.cmdEdit);
                if (norms!=null){
                    request.getSession().setAttribute("norms",norms);
                }
                if (users!=null){
                    request.getSession().setAttribute("userSystem",users);
                }
                return goSucces();
            }
        } catch (ApplicationExceptionChecked ae) {
            return goError(ae.getKeyError());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return goError();
    }
}
