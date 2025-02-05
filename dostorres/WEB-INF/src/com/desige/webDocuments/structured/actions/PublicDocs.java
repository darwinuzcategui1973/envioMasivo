package com.desige.webDocuments.structured.actions;

import java.util.Properties;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.desige.webDocuments.persistent.managers.HandlerDocuments;
import com.desige.webDocuments.persistent.managers.HandlerStruct;
import com.desige.webDocuments.utils.ApplicationExceptionChecked;
import com.desige.webDocuments.utils.ToolsHTML;
import com.desige.webDocuments.utils.beans.SuperAction;

/**
 * Title: PublicDocs.java <br/>
 * Copyright: (c) 2004 Focus Consulting C.A.<br/>
 * @author Ing. Nelson Crespo
 * @version WebDocuments v3.0
 * <br/>
 *      Changes:<br/>
 * <ul>
 *      <li> 15/03/2005 (NC) Creation </li>
 * </ul>
 */
public class PublicDocs extends SuperAction {
    public ActionForward execute(ActionMapping actionMapping,
                                 ActionForm form, HttpServletRequest request,
                                 HttpServletResponse response) {
        super.init(actionMapping,form,request,response);
        //System.out.println("PublicDocs.execute");
        String idNode = (String)ToolsHTML.getAttribute(request,"idNodeSelected");
        if (ToolsHTML.isEmptyOrNull(idNode)) {
            idNode = (String)getSessionObject("nodeActive");
        }
        try {
            getUserSession();
            Vector showOptionPublic = HandlerStruct.getDocsAprovedAndNotPublicForNode(idNode);
            StringBuffer ids = new StringBuffer(60);
            String id = "";
            boolean firts = true;
            for (int row = 0; row < showOptionPublic.size(); row++) {
                Properties properties = (Properties) showOptionPublic.elementAt(row);
                id = properties.getProperty("numGen");
                if (firts) {
                    ids.append("(");
                    firts = false;
                } else {
                    ids.append(",");
                }
                ids.append(id);
            }
            if (ids.length() > 0) {
                ids.append(")");
                //System.out.println("ids.toString() = " + ids.toString());
                if (HandlerDocuments.publicDocsforNode(ids.toString())) {
                    return goSucces();
                } else {
                    return goError("E0044");
                }
            }
        } catch (ApplicationExceptionChecked ae) {
            //System.out.println("[ApplicationExceptionChecked]");
            return goError(ae.getKeyError());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return goError("E0044");
    }
}
