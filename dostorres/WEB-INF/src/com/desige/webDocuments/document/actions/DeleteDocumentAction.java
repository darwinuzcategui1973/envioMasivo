package com.desige.webDocuments.document.actions;

import java.util.ResourceBundle;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.desige.webDocuments.persistent.managers.HandlerDocuments;
import com.desige.webDocuments.utils.ApplicationExceptionChecked;
import com.desige.webDocuments.utils.ToolsHTML;
import com.desige.webDocuments.utils.beans.SuperAction;
import com.desige.webDocuments.utils.beans.Users;

/**
 * Title: DeleteDocumentAction.java <br/>
 * Copyright: (c) 2004 Focus Consulting C.A.<br/>
 *
 * @author Ing. Nelson Crespo
 * @version WebDocuments v3.0
 * <br/>
 *      Changes:<br/>
 * <ul>
 *      <li> 10/02/2005 (NC) Creation </li>
 * </ul>
 */
public class DeleteDocumentAction extends SuperAction {
    public ActionForward execute(ActionMapping mapping,
                                 ActionForm form, HttpServletRequest request,
                                 HttpServletResponse response) {
        ResourceBundle rb = ToolsHTML.getBundle(request);
        super.init(mapping,form,request,response,rb);
        int idDoc = Integer.parseInt(getParameter("idDocument"));
        String version = getParameter("version");
        String cmd = getParameter("cmd");
        try {
            Users usuario = getUserSession();
           // if (HandlerDocuments.deleteDoc(idDoc,0,usuario.getUser())) {
            //SIMON 08 DE JULIO 2005 INICIO
            String idTypeDoc  = HandlerDocuments.getFieldToDocument("type",idDoc);
            boolean isPrintRequest = (idTypeDoc.equalsIgnoreCase(HandlerDocuments.TypeDocumentsImpresion));
            if (("0".equalsIgnoreCase(cmd))|| isPrintRequest) {
                String str= HandlerDocuments.deleteDocstr(idDoc,0,usuario.getUser(),isPrintRequest);
                return goSucces(str);
            } else {
                if (HandlerDocuments.deleteVersionDoc(idDoc,version,usuario.getUser())) {
                    putObjectSession("idDocument",String.valueOf(idDoc));
                    putObjectSession("info",getMessage("docDelTrashOK"));
                    return goTo("showDoc");
                }
            }
        } catch (ApplicationExceptionChecked ae) {
            return goError(ae.getKeyError());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return goError("doc.errDelete");
    }
}
