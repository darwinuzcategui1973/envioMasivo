package com.desige.webDocuments.mail.actions;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.desige.webDocuments.mail.forms.AddressForm;
import com.desige.webDocuments.persistent.managers.HandlerMessages;
import com.desige.webDocuments.utils.ApplicationExceptionChecked;
import com.desige.webDocuments.utils.beans.SuperAction;
import com.desige.webDocuments.utils.beans.SuperActionForm;

/**
 * Title: LoadContactForEditAction.java <br/>
 * Copyright: (c) 2004 Focus Consulting C.A.<br/>
 *
 * @author Ing. Nelson Crespo
 * @version WebDocuments v3.0
 * <br/>
 *      Changes:<br/>
 * <ul>
 *      <li> 2005-02-03 (NC) Creation </li>
 * </ul>
 */
public class LoadContactForEditAction extends SuperAction {
    public ActionForward execute(ActionMapping mapping,
                                 ActionForm form, HttpServletRequest request,
                                 HttpServletResponse response) {
        super.init(mapping,form,request,response);
        String idAddress = request.getParameter("idAddress");
        //System.out.println("[LoadContactForEditAction] idAddress = " + idAddress);
        removeObjectSession("addressMail");
        try {
            getUserSession();
            AddressForm forma = HandlerMessages.loadContact(idAddress);
            putObjectSession("addressMail",forma);
            putAtributte("cmd",SuperActionForm.cmdEdit);
            forma.setCmd(SuperActionForm.cmdEdit);
            return goSucces();
        } catch (ApplicationExceptionChecked ae) {
            ae.printStackTrace();
            return goError(ae.getKeyError());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return goError("E0026");
    }
}
