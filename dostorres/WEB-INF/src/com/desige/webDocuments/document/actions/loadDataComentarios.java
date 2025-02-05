package com.desige.webDocuments.document.actions;

import java.sql.Connection;
import java.util.Collection;
import java.util.Hashtable;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.desige.webDocuments.document.forms.BaseDocumentForm;
import com.desige.webDocuments.persistent.managers.HandlerDocuments;
import com.desige.webDocuments.persistent.managers.HandlerStruct;
import com.desige.webDocuments.utils.Constants;
import com.desige.webDocuments.utils.ToolsHTML;
import com.desige.webDocuments.utils.beans.SuperAction;
import com.desige.webDocuments.utils.beans.Users;

/**
 * Title: loadDataComentarios.java <br/>
 * Copyright: (c) 2004 Focus Consulting C.A.<br/>
 * ydavila
 */
public class loadDataComentarios  extends SuperAction {
    public ActionForward execute(ActionMapping mapping,
                                 ActionForm form, HttpServletRequest request, 
                                 HttpServletResponse response) {
        super.init(mapping,form,request,response);
        BaseDocumentForm forma = new BaseDocumentForm();
        forma.setIdDocument(getParameter("idDocument"));
        forma.setNumberGen(getParameter("idDocument"));
        forma.setNumVer(Integer.parseInt(getParameter("idVersion")));
        try {
            Users usuario = (Users)getSessionObject("user");
            Hashtable tree = (Hashtable)getSessionObject("tree");
            tree = ToolsHTML.checkTree(tree,usuario);
            HandlerDocuments.getDescriptCommentContextLM(getParameter("idDocument"), Integer.parseInt(getParameter("idVersion"))); 
            putObjectSession("showComentarios",forma);
           

            StringBuffer item = new StringBuffer("/showComentarios.jsp?userFirmantes=");
            ActionForward nextPage = new ActionForward(item.toString(),false);

            forma.setPrefix(ToolsHTML.getPrefixToDoc(getSession(),usuario,forma.getIdNode()));
            return nextPage;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return goError("E0052");
    }
}
