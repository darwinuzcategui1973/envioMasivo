package com.desige.webDocuments.structured.actions;

import java.util.Collection;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.desige.webDocuments.persistent.managers.HandlerStruct;
import com.desige.webDocuments.structured.forms.DataHistoryStructForm;
import com.desige.webDocuments.utils.ApplicationExceptionChecked;
import com.desige.webDocuments.utils.ToolsHTML;
import com.desige.webDocuments.utils.beans.SuperAction;

/**
 * Title: LoadDataHistoryAction.java <br/>
 * Copyright: (c) 2004 Focus Consulting C.A.<br/>
 * @author Ing. Roger Rodríguez
 * @version WebDocuments v3.0
 * <br/>
 * Changes:<br/> 
 * <ul>
 *      <li>31/08/2004 (RR) Creation </li>
 </ul>
 */
public class LoadDataHistoryAction extends SuperAction {
    public ActionForward execute(ActionMapping mapping,
                                 ActionForm form, HttpServletRequest request,
                                 HttpServletResponse response) {
        super.init(mapping,form,request,response);
        removeObjectSession("loadDocs");
        removeObjectSession("historyimpresion");
        String idNode = request.getParameter("idNodeSelected");
        String histSelect = request.getParameter("histSelect");
        try{
            getUserSession();
            Collection history = HandlerStruct.getHistoryNode(idNode);
            request.getSession().setAttribute("historystruct",history);

            String name = HandlerStruct.getField("Name","struct","IdNode",idNode,"=",2,Thread.currentThread().getStackTrace()[1].getMethodName());
            if (!ToolsHTML.isEmptyOrNull(name)) {
                request.getSession().setAttribute("nameStruct",name);
            }

            if (request.getSession().getAttribute("dataHistoryStruct")!=null) {
                request.getSession().removeAttribute("dataHistoryStruct");
            }

            if (!ToolsHTML.isEmptyOrNull(histSelect)) {
                DataHistoryStructForm forma = HandlerStruct.loadHistory(histSelect);
                request.getSession().setAttribute("dataHistoryStruct",forma);
            }
            request.getSession().removeAttribute("idDocument");
            return mapping.findForward("success");
        } catch (ApplicationExceptionChecked ae) {
            //System.out.println("[ApplicationExceptionChecked]");
            return goError(ae.getKeyError());
        } catch (Exception e) {
            //System.out.println("[Exception]");
            e.printStackTrace();
        }
        return mapping.findForward("error");
    }
}
