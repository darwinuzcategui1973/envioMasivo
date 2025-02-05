package com.desige.webDocuments.workFlows.actions;

import java.util.ResourceBundle;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.desige.webDocuments.persistent.managers.HandlerWorkFlows;
import com.desige.webDocuments.utils.ApplicationExceptionChecked;
import com.desige.webDocuments.utils.ToolsHTML;
import com.desige.webDocuments.utils.beans.SuperAction;

/**
 * Title: ShowUsersFlexFlowAction.java.java <br/>
 * Copyright: (c) 2004 Desige Servicios de Computación<br/>
 *
 * @author YSA
 * @version WebDocuments v4.0
 * <br/>
 *      Changes:<br/>
 * <ul>
 *      <li> 02/07/2007 (YS) Creation </li>
 * </ul>
 */
public class ShowUsersFlexFlowAction extends SuperAction {
    static Logger log = LoggerFactory.getLogger(ShowUsersFlexFlowAction.class.getName());
    public ActionForward execute(ActionMapping mapping,
                                 ActionForm form, HttpServletRequest request,
                                 HttpServletResponse response) {
        ResourceBundle rb = ToolsHTML.getBundle(request);
        super.init(mapping,form,request,response,rb);

        try {
        	//removeAttribute("usersWF");
            //removeAttribute("showDataWF");
        	//removeAttribute("info");
            removeAttribute("flujosReject");
            removeAttribute("commentsReject");
            removeAttribute("idFlexFlowReject");
            removeAttribute("numVersionReject");
            removeAttribute("idWorkFlowReject");
            removeAttribute("idDocumentReject");
            removeAttribute("resultReject");
            removeAttribute("statuReject");
            removeAttribute("idMovementReject");
            removeAttribute("rowReject");
            
        	String idDocument = (String)getParameter("idDocument");
        	String idWorkFlow = (String)getParameter("idWorkFlow");
            String numVersion = (String)getParameter("numVersion");
        	String idFlexFlow = (String)getParameter("idFlexFlow");
        	String comments = (String)getParameter("comments");
        	String result = (String)getParameter("result");
        	String statu = (String)getParameter("statu");
        	String idMovement = (String)getParameter("idMovement");
        	String row =  (String)getParameter("row");
        	

        	if(!ToolsHTML.isEmptyOrNull(idFlexFlow)){
	        	putObjectSession("flujosReject",HandlerWorkFlows.loadDataFlexFlowReasigne("flexworkflow",idFlexFlow));
	        	putObjectSession("commentsReject", comments);
	        	putObjectSession("idFlexFlowReject", idFlexFlow);
	        	putObjectSession("numVersionReject", numVersion);
	        	putObjectSession("idWorkFlowReject", idWorkFlow);
	        	putObjectSession("idDocumentReject", idDocument);
	        	putObjectSession("resultReject", result);
	        	putObjectSession("statuReject", statu);
	        	putObjectSession("idMovementReject", idMovement);
	        	putObjectSession("rowReject", row);
        	}
        	removeAttribute("recargar");
            return goSucces();
        } catch (ApplicationExceptionChecked ae) {
            log.error(ae.getMessage());
            return goError(ae.getKeyError());
        } catch (Exception ex) {
            log.error(ex.getMessage());
            ex.printStackTrace();
        }
        return goError();
    }

}
