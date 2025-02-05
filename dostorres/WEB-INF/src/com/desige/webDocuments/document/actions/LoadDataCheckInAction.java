package com.desige.webDocuments.document.actions;

import java.util.ResourceBundle;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.desige.webDocuments.document.forms.CheckOutDocForm;
import com.desige.webDocuments.persistent.managers.HandlerDocuments;
import com.desige.webDocuments.persistent.managers.HandlerParameters;
import com.desige.webDocuments.utils.ApplicationExceptionChecked;
import com.desige.webDocuments.utils.Constants;
import com.desige.webDocuments.utils.StringUtil;
import com.desige.webDocuments.utils.ToolsHTML;
import com.desige.webDocuments.utils.beans.SuperAction;
import com.desige.webDocuments.utils.beans.Users;

/**
 * Title: LoadDataCheckInAction.java <br/>
 * Copyright: (c) 2004 Focus Consulting C.A.<br/>
 *
 * @author Ing. Nelson Crespo
 * @version WebDocuments v3.0
 * <br/>
 *      Changes:<br/>
 * <ul>
 *      <li> 23/10/2004 (NC) Creation </li>
 *      <li> 27/07/2005 (NC) Add condition "HandlerDocuments.docTrash.equalsIgnoreCase(forma.getStatu())" </li>
 *      <li> 30/06/2006 (NC) se agreg� el uso del Log y cambios para mostrar los documentos vinculados.</li> 
 * </ul>
 */
public class LoadDataCheckInAction extends SuperAction {
    public ActionForward execute(ActionMapping mapping,
                                 ActionForm form, HttpServletRequest request,
                                 HttpServletResponse response) {
        super.init(mapping,form,request,response);
        String idDocument = getParameter("idDocument");
		try {
            Users usuario = getUserSession();
			CheckOutDocForm forma = HandlerDocuments.getDataCheckOutDocument(idDocument,null,true);
            forma.setDraftVersion(false);
            if (HandlerDocuments.docApproved.equalsIgnoreCase(forma.getStatu())) {
                forma.setChangeMinorVersion(Constants.notPermissionSt);
            } else {
                if (HandlerDocuments.docTrash.equalsIgnoreCase(forma.getStatu())) {
                    forma.setDraftVersion(true);
                }
                forma.setChangeMinorVersion(Constants.permissionSt);
            }
            String links = HandlerDocuments.getDocumentsLinksIDs(idDocument);
            if (!ToolsHTML.isEmptyOrNull(links)) {
                ResourceBundle rb = ToolsHTML.getBundle(request);
                putObjectSession("info",rb.getString("chk.linksInfo"));
                forma.setDocRelations(links);
            } else {
                forma.setDocRelations(null);
            }
            String versionCom = String.valueOf(HandlerParameters.PARAMETROS.getVersionCom());
            
            String beforeComments = HandlerDocuments.getBeforeLocksCommentsFromDocuments(idDocument, forma.getMayorVer(), forma.getMinorVer());

            putAtributte("beforeComments",StringUtil.serialize(beforeComments));
            putAtributte("deserialize",true);

            putAtributte("versionCom",versionCom);
            putObjectSession("checkInDoc",forma);
            
			return goSucces();
        } catch (ApplicationExceptionChecked ae) {
            return goError(ae.getKeyError());
		} catch (Exception e) {
			e.printStackTrace();
		}
        return goError(); 
    }
}

