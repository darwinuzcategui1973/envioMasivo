package com.desige.webDocuments.document.actions;

import java.util.ResourceBundle;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.desige.webDocuments.document.forms.CheckOutDocForm;
import com.desige.webDocuments.persistent.managers.HandlerDocuments;
import com.desige.webDocuments.utils.ApplicationExceptionChecked;
import com.desige.webDocuments.utils.ToolsHTML;
import com.desige.webDocuments.utils.beans.SuperAction;

/**
 * Title: LoadDataCheckOutDocAction.java <br/>
 * Copyright: (c) 2004 Focus Consulting C.A.<br/>
 *
 * @author Ing. Nelson Crespo
 * @version WebDocuments v3.0
 * <br/>
 *      Changes:<br/>
 * <ul>
 *      <li> 19/10/2004 (NC) Creation </li>
 * </ul>
 */
public class LoadDataCheckOutDocAction extends SuperAction {
    static Logger log = LoggerFactory.getLogger("[V4.0] " + LoadDataCheckOutDocAction.class.getName());
    public ActionForward execute(ActionMapping mapping,
                                 ActionForm form, HttpServletRequest request,
                                 HttpServletResponse response) {
        super.init(mapping,form,request,response);
		String idDocument = request.getParameter("idDocument");
		try {
            getUserSession();
			CheckOutDocForm forma = HandlerDocuments.getDataCheckOutDocument(idDocument,null,false);
            String links = HandlerDocuments.getDocumentsLinks(idDocument);
            if (!ToolsHTML.isEmptyOrNull(links)) {
                ResourceBundle rb = ToolsHTML.getBundle(request);
                putObjectSession("info",rb.getString("chk.linksInfo"));
            }
            putObjectSession("checkOutDoc",forma);
			return goSucces();
        } catch (ApplicationExceptionChecked ae){
            log.error(ae.getMessage());
            return goError(ae.getKeyError());
		} catch (Exception e) {
            log.error(e.getMessage());
            e.printStackTrace();
		}
		return mapping.findForward("error");
	}
}
