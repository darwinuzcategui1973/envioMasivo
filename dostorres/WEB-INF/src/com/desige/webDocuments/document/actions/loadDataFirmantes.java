package com.desige.webDocuments.document.actions;

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
 * Title: loadDataFirmantes.java <br/>
 * Copyright: (c) 2004 Focus Consulting C.A.<br/>
 * @author Ing. Sim�n Rodrigu�z
 * @version WebDocuments v3.0
 * <br/>
 *      Changes:<br/>
 * <ul>
 *      <li> 30/07/2005 (SR) Creation </li>
 *      <li> 13/07/2006 (NC) Cambios para heredar o no los Prefijos de las Carpetas </li>
 * </ul>
 */
public class loadDataFirmantes  extends SuperAction {
    public ActionForward execute(ActionMapping mapping,
                                 ActionForm form, HttpServletRequest request,
                                 HttpServletResponse response) {
        super.init(mapping,form,request,response);
        BaseDocumentForm forma = new BaseDocumentForm();
        forma.setIdDocument(getParameter("idDocument"));
        forma.setNumberGen(getParameter("idDocument"));
        forma.setNumVer(Integer.parseInt(getParameter("idVersion")));
        // prueba 
        forma.setCharge("probando aqui los datos a cargar");
        try {
            Users usuario = (Users)getSessionObject("user");
            Hashtable tree = (Hashtable)getSessionObject("tree");
            tree = ToolsHTML.checkTree(tree,usuario);
            HandlerStruct.loadDocument(forma,getParameter("downFile")!=null,false,tree, request);
            putObjectSession("showDocument",forma);
            Collection firmas = null;
            //Si es Un Flujo parametrico se buscan los datos de los Firmantes
            //en la Tabla de Flujos Parametricos
            if (forma!=null&& forma.getTypeWF() == Constants.permission) {
                firmas = HandlerDocuments.loadFirmsToVersionDoc(request.getParameter("idDocument"),
                                                                request.getParameter("idVersion"),
                                                                String.valueOf(forma.getStatu()),true);
            } else {
                firmas = HandlerDocuments.loadFirmsToVersionDoc(request.getParameter("idDocument"),
                                                                request.getParameter("idVersion"),
                                                                String.valueOf(forma.getStatu()),false);
            }
//            Collection userFirmantes = HandlerWorkFlows.getUserInFlowPending(forma.getIdDocument(),HandlerWorkFlows.wfuAcepted);
            if (firmas==null || firmas.isEmpty()) {
            	firmas = HandlerDocuments.getSignatureOwnerDocument(request.getParameter("idDocument"),
	                       request.getParameter("idVersion"),
	                       String.valueOf(forma.getStatu()));
            }
            
            if (firmas!=null && !firmas.isEmpty()) {
            	putObjectSession("userFirmantes",firmas);
            }
            
            StringBuffer item = new StringBuffer("/showFirmantes.jsp?userFirmantes=");
            ActionForward nextPage = new ActionForward(item.toString(),false);
            if (usuario==null) {
                putObjectSession("optionReturn",nextPage);
                return goTo("login");
            }
            forma.setPrefix(ToolsHTML.getPrefixToDoc(getSession(),usuario,forma.getIdNode()));
            return nextPage;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return goError("E0052");
    }
}
