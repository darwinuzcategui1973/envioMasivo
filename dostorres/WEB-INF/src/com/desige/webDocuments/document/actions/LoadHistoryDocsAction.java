package com.desige.webDocuments.document.actions;

import java.util.Collection;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.desige.webDocuments.dao.PreviewDAO;
import com.desige.webDocuments.persistent.managers.HandlerStruct;
import com.desige.webDocuments.structured.forms.DataHistoryStructForm;
import com.desige.webDocuments.to.PreviewTO;
import com.desige.webDocuments.utils.ApplicationExceptionChecked;
import com.desige.webDocuments.utils.ToolsHTML;
import com.desige.webDocuments.utils.beans.SuperAction;

/**
 * Title: LoadHistoryDocsAction.java <br/>
 * Copyright: (c) 2004 Focus Consulting C.A.<br/>
 * @author Ing. Nelson Crespo
 * @version WebDocuments v3.0
 * <br/>
 * Changes:<br/>
 * <ul>
 *      <li> 31/08/2004 (NC) Creation </li>
 </ul>
 */
public class LoadHistoryDocsAction extends SuperAction {
    public ActionForward execute(ActionMapping mapping,
                                 ActionForm form, HttpServletRequest request,
                                 HttpServletResponse response) {
        super.init(mapping,form,request,response);
        
        request.getSession().removeAttribute("f1"); // para indicar que no estamos en expedientes
        
        String idNode = request.getParameter("idDocument")!=null?request.getParameter("idDocument"):
                        (String)request.getSession().getAttribute("idDocument");
        String histSelect = request.getParameter("histSelect");
        putObjectSession("idDocument",idNode);
        try{
            getUserSession();
            Collection history = HandlerStruct.getHistoryDoc(idNode);
            putObjectSession("historystruct",history);
            String name = getParameter("nameDocument")!=null?getParameter("nameDocument"):
                                                (String)getSessionObject("nameStruct");
            if (!ToolsHTML.isEmptyOrNull(name)) {
                putObjectSession("nameStruct",name);
            }

            if (request.getSession().getAttribute("dataHistoryStruct")!=null) {
                removeAttribute("dataHistoryStruct");
            }

            if ((!ToolsHTML.isEmptyOrNull(histSelect))&&(!"-1".equalsIgnoreCase(histSelect))) {
                DataHistoryStructForm forma = HandlerStruct.loadHistoryDoc(histSelect,null);
                putObjectSession("dataHistoryStruct",forma);
            }
            removeObjectSession("historyimpresion");
            if ("-1".equalsIgnoreCase(histSelect)){
                DataHistoryStructForm forma = HandlerStruct.loadHistoryDoc(null,idNode);
                putObjectSession("dataHistoryStruct",forma);
                putObjectSession("historyimpresion","1");
            }
            if ("-2".equalsIgnoreCase(histSelect)){
                DataHistoryStructForm forma = HandlerStruct.loadHistoryDoc(null,idNode);
                putObjectSession("dataHistoryStruct",forma);

                PreviewDAO previewDAO = new PreviewDAO();
            	PreviewTO previewTO = new PreviewTO();
            	
            	previewTO.setIdDocument(idNode);
            	
                request.setAttribute("historyViews", "true");
                putObjectSession("historyListDist", "false");
                putObjectSession("dataHistoryViews",previewDAO.findAllByOrder(previewTO));
            }
            if ("-3".equalsIgnoreCase(histSelect)){
                DataHistoryStructForm forma = HandlerStruct.loadHistoryDoc(null,idNode);
                putObjectSession("dataHistoryStruct",forma);

                PreviewDAO previewDAO = new PreviewDAO();
            	PreviewTO previewTO = new PreviewTO();
            	
            	previewTO.setIdDocument(idNode);
            	
                request.setAttribute("historyViews", "true");
                putObjectSession("historyListDist", "true");
                putObjectSession("dataHistoryViews",previewDAO.findAllByOrderListDist(previewTO));
            }

            String historicoImpreso=HandlerStruct.loadHistoryImpresion(idNode);
            putObjectSession("historicoImpreso",historicoImpreso);
            putObjectSession("loadDocs","true");
            return goSucces();
        } catch (ApplicationExceptionChecked ae) {
            return goError(ae.getKeyError());
        } catch(Exception e){
            e.printStackTrace();
        }
        return goError();
    }
}
