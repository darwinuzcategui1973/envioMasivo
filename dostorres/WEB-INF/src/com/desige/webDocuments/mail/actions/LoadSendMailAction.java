package com.desige.webDocuments.mail.actions;

import java.util.Hashtable;
import java.util.ResourceBundle;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.desige.webDocuments.document.forms.BaseDocumentForm;
import com.desige.webDocuments.mail.forms.MailForm;
import com.desige.webDocuments.persistent.managers.HandlerStruct;
import com.desige.webDocuments.utils.ApplicationExceptionChecked;
import com.desige.webDocuments.utils.ToolsHTML;
import com.desige.webDocuments.utils.beans.SuperAction;
import com.desige.webDocuments.utils.beans.Users;
import com.desige.webDocuments.workFlows.actions.EditWorkFlowAction;

/**
 * Title: LoadSendMailAction.java <br/>
 * Copyright: (c) 2004 Focus Consulting C.A.<br/>
 *
 * @author Ing. Nelson Crespo
 * @version WebDocuments v3.0
 * <br/>
 * 		Changes:<br/>
 * <ul>
 * 		<li> 25/06/2004 (NC) Creation </li>
 * </ul>
 */
public class LoadSendMailAction extends SuperAction {
    public ActionForward execute(ActionMapping mapping,
                                 ActionForm form, HttpServletRequest request,
                                 HttpServletResponse response) {
        try {
            super.init(mapping,form,request,response);
            removeAttribute("allUsers");
            MailForm forma = new MailForm();
            Users usuario = getUserSession();
            forma.setFrom(usuario.getEmail());
            forma.setNameFrom(usuario.getNamePerson());
            //System.out.println("usuario.getEmail() = " + usuario.getEmail());
            String to = request.getParameter("to");
            if (to!=null) {
                forma.setTo(to);
                forma.setIdTo(to);
                forma.setNames(to);
            }
            String mensaje = request.getParameter("mensaje");
            //System.out.println("mensaje = " + mensaje);
            if (!ToolsHTML.isEmptyOrNull(mensaje)) {
                forma.setMensaje(mensaje);
            }
            String subject = request.getParameter("subject");
            if (!ToolsHTML.isEmptyOrNull(subject)) {
                forma.setSubject(subject);
            }
            String idDocument = request.getParameter("idDocument");
            //System.out.println("[LoadSendMailAction] idDocument = " + idDocument);
            if (!ToolsHTML.isEmptyOrNull(idDocument)) {
                BaseDocumentForm dataDoc = new BaseDocumentForm();
                dataDoc.setIdDocument(idDocument);
                dataDoc.setNumberGen(idDocument);
                //todo Preguntar
                Hashtable tree = (Hashtable)getSessionObject("tree");
                tree = ToolsHTML.checkTree(tree,usuario);
                HandlerStruct.loadDocument(dataDoc,true,false,tree, request);
                ResourceBundle rb = ToolsHTML.getBundle(request);
                forma.setMensaje(EditWorkFlowAction.getRestDataNotified(request,dataDoc,rb,null));
                putObjectSession("width","true");
                putObjectSession("idDocument",idDocument);
                //System.out.println("forma.getMensaje() = " + forma.getMensaje());
            }
            putObjectSession("sendMail",forma);
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
