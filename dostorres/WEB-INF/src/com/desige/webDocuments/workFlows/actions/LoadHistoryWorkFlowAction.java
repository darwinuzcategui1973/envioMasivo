package com.desige.webDocuments.workFlows.actions;

import java.util.ArrayList;
import java.util.Collection;

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
import com.desige.webDocuments.workFlows.forms.DataWorkFlowForm;

/**
 * Title: LoadHistoryWorkFlowAction.java <br/>
 * Copyright: (c) 2004 Focus Consulting C.A.<br/>
 * @author Ing. Roger Rodríguez
 * @version WebDocuments v3.0
 * <br/>
 * Changes:<br/> 
 * <ul>
 *      <li>29/08/2004 (RR) Creation </li>
 </ul>
 */
public class LoadHistoryWorkFlowAction extends SuperAction {
    static Logger log = LoggerFactory.getLogger(LoadHistoryWorkFlowAction.class.getName());
    public ActionForward execute(ActionMapping mapping,
                                 ActionForm form, HttpServletRequest request,
                                 HttpServletResponse response) {
        super.init(mapping,form,request,response);
        String idDocument = request.getParameter("idDocument");
        String idWorkFlow = request.getParameter("idWorkFlow");
		String idMovement = request.getParameter("idMovement");
		String typeMovement = request.getParameter("typeMovement");
		//ydavila Ticket 001-00-003023
		String typeWF = request.getParameter("typeWF");
		String subtypeWF = request.getParameter("subtypeWF");
        boolean isFlexFlow  = "true".equalsIgnoreCase(getParameter("isFlexFlow"));
        log.debug("idDocument = " + idDocument);
		log.debug("idWorkFlow = " + idWorkFlow);
		log.debug("idMovement = " + idMovement);
		log.debug("typeMovement = " + typeMovement);
        log.debug("isFlexFlow = " + isFlexFlow);
        log.debug("typeWF = " + typeWF);
        log.debug("subtypeWF = " + subtypeWF);
        try{
            getUserSession();
			removeAttribute("comments",request);
			removeAttribute("showDataWF",request);
			removeAttribute("usersWF",request);
            removeAttribute("pages");
            removeAttribute("loadDocs");
            removeObjectSession("flujos");
			removeAttribute("documentOfRejection");

			DataWorkFlowForm dataWF = null;
            if (!ToolsHTML.isEmptyOrNull(idWorkFlow)&&!"0".equalsIgnoreCase(idWorkFlow)) {
                dataWF = new DataWorkFlowForm();
                dataWF.setIdWorkFlow(idWorkFlow);
//				dataWF.setIdMovement(Integer.parseInt(idMovement));
//                HandlerWorkFlows.loadDataWorkFlow("historywf",true,dataWF);
                if (isFlexFlow) {
                    putObjectSession("flujos",HandlerWorkFlows.loadDataFlexFlow("flexworkflow",dataWF.getIdWorkFlow(),request));
//                    Collection usersWF = HandlerWorkFlows.getAllUserInFlexFlow(dataWF.getIdWorkFlow());
//                    putObjectSession("users",usersWF);
                    putAtributte("pages","showDataHistFlexFlow.jsp");
                    
        			// buscamos los documentos de rechazo relacionados
        			Collection documentOfRejection = new ArrayList();
        			if (isFlexFlow) {
        				dataWF.setIdFlexFlow(Long.parseLong(idWorkFlow));
        				documentOfRejection = HandlerWorkFlows.getDocumentsFlexFlowOfRejection(dataWF);
        				putObjectSession("documentOfRejection", documentOfRejection);
        			}
        		    //System.out.println(getSessionObject("documentOfRejection"));                    
                } else {
                    HandlerWorkFlows.loadDataWorkFlow("workflows",false,dataWF);
                    putObjectSession("showDataWF",dataWF);
                    Collection usersWF = HandlerWorkFlows.getAllUserInWorkFlow(idWorkFlow);
                    putObjectSession("usersWF",usersWF);
                    putAtributte("pages","showDataWorkFlow.jsp");
                }
                dataWF.setComments(ToolsHTML.updateURLVerDocumento(dataWF.getComments(), request, null));
                putObjectSession("showDataWF",dataWF);
//                Collection usersWF = HandlerWorkFlows.getAllUserInWorkFlow(idWorkFlow);
//                putObjectSession("usersWF",usersWF);
				if (!isFlexFlow&&ToolsHTML.isNumeric(idMovement)) {
                    dataWF.setIdMovement(Integer.parseInt(idMovement));
					Collection comments = HandlerWorkFlows.getCommentsUser(dataWF,!typeMovement.equalsIgnoreCase("1"));
                    putObjectSession("comments",comments);
				}
            } else {
                putAtributte("pages","showDataWorkFlow.jsp");
            }
            Collection history = HandlerWorkFlows.getHistoryDocument(idDocument);
            history.addAll(HandlerWorkFlows.getHistoryFlexDoc(idDocument,getMessage("wf.wfStatu5"),getMessage("wf.wfStatu3"),getMessage("wf.wfStatu2")));
            putObjectSession("history",history);
            //ydavila
            putObjectSession("vengoDeBuscar","historico");
            return goSucces();
        } catch (ApplicationExceptionChecked ap) {
            return goError(ap.getKeyError());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return goError();
    }
}
