package com.desige.webDocuments.workFlows.actions;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.desige.webDocuments.persistent.managers.HandlerWorkFlows;
import com.desige.webDocuments.utils.ApplicationExceptionChecked;
import com.desige.webDocuments.utils.ToolsHTML;
import com.desige.webDocuments.utils.beans.SuperAction;

/**
 * Title: DeleteWFsAction.java <br/>
 * Copyright: (c) 2004 Focus Consulting C.A.<br/>
 * @author Ing. Nelson Crespo
 * @version WebDocuments v3.0
 * <br/>
 *      Changes:<br/>
 * <ul>
 *      <li> 19/02/2005 (NC) Creation </li>
 * </ul>
 */
public class DeleteWFsAction extends SuperAction {
    public ActionForward execute(ActionMapping mapping,
                                 ActionForm form, HttpServletRequest request,
                                 HttpServletResponse response) {
        super.init(mapping,form,request,response);
        try {
            getUserSession();
            String[] myTask = request.getParameterValues("myTask");
//            String idWorkFlowsToDel = ToolsHTML.getIds(myTask);
            //Flujos de Trabajo
            String idWorkFlowsToDel = ToolsHTML.getIds(myTask,"false");
            if (idWorkFlowsToDel!=null) {
//                HandlerWorkFlows.deleteWFReadingOwner(idWorkFlowsToDel);
                HandlerWorkFlows.deleteWFReadingOwner(idWorkFlowsToDel,"workflows");
            }
            //Eliminando Flujos de Trabajo Paramétrico
            idWorkFlowsToDel = ToolsHTML.getIds(myTask,"true");
            if (idWorkFlowsToDel!=null) {
                HandlerWorkFlows.deleteWFReadingOwner(idWorkFlowsToDel,"flexworkflow");
            }
            String[] taskReq = request.getParameterValues("taskReq");
            String idRowsToDel = ToolsHTML.getIds(taskReq,"false");
            if (idRowsToDel!=null) {
                HandlerWorkFlows.deleteWFReading(idRowsToDel,"user_workflows");
            }
            //Flujos Parametricos Requeridos
//            String idRowsToDel = ToolsHTML.getIds(taskReq);
            idRowsToDel = ToolsHTML.getIds(taskReq,"true");
            if (idRowsToDel!=null) {
//                HandlerWorkFlows.deleteWFReading(idRowsToDel);
                HandlerWorkFlows.deleteWFReading(idRowsToDel,"user_flexworkflows");
            }
            if(request.getParameter("myTask")!=null && request.getParameter("myTask").equals("true")) {
            	return goTo("successMyTask");
            } else if(request.getParameter("ordersTask")!=null && request.getParameter("ordersTask").equals("true")) {
            	return goTo("successOrdersTask");
            }
            return goSucces();
        } catch (ApplicationExceptionChecked ae) {
            return goError(ae.getKeyError());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return goError("E0047");
    }
}
