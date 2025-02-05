package com.desige.webDocuments.workFlows.actions;

import java.sql.Timestamp;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.desige.webDocuments.persistent.managers.HandlerDocuments;
import com.desige.webDocuments.persistent.managers.HandlerParameters;
import com.desige.webDocuments.persistent.managers.HandlerWorkFlows;
import com.desige.webDocuments.utils.ApplicationExceptionChecked;
import com.desige.webDocuments.utils.ToolsHTML;
import com.desige.webDocuments.utils.beans.SuperAction;
import com.desige.webDocuments.utils.beans.Users;
import com.desige.webDocuments.workFlows.forms.DataUserWorkFlowForm;

/**
 * Title: CloseWorkFlowAction.java <br/>
 * Copyright: (c) 2004 Focus Consulting C.A.<br/>
 *
 * @author Ing. Nelson Crespo(NC) 
 * @author Ing. Simón Rodriguez(SR)
 * @version WebDocuments v3.0
 * <br/>
 *      Changes:<br/>
 * <ul>
 *      <li> 2005-02-28 (NC) Creation </li>
 *      <li> 2005-05-26 (SR) StringBuffer msg =  new StringBuffer(HandlerParameters.PARAMETROS.getMsgWFCancelados()); </li>
 * </ul>
 */
public class CloseWorkFlowAction extends SuperAction {
    static Logger log = LoggerFactory.getLogger("[V4.0 FTP] " + CloseWorkFlowAction.class.getName());
    public ActionForward execute(ActionMapping mapping,
                                 ActionForm form, HttpServletRequest request,
                                 HttpServletResponse response) {
        super.init(mapping,form,request,response);
        try {
            Users usuario = getUserSession();
            String idWF = getParameter("idWorkFlow");
            String idDocument = getParameter("idDocument");
            Timestamp time = new Timestamp(new java.util.Date().getTime());
            DataUserWorkFlowForm forma = new DataUserWorkFlowForm();
            forma.setIdWorkFlow(Integer.parseInt(idWF));
            forma.setIdDocument(Integer.parseInt(idDocument));
            HandlerDocuments.loadDataDocument(forma);
         //   StringBuffer msg = new StringBuffer(getMessage("wf.canceled")).append(" ");
            //simon 25 mayo 2005 inicio
             StringBuffer msg =  new StringBuffer("");
             String varTemp = HandlerParameters.PARAMETROS.getMsgWFCancelados();
             if (ToolsHTML.isEmptyOrNull(varTemp)){
                 msg = new StringBuffer(getMessage("wf.canceled")).append(" ");
            }else{
                   msg.append(varTemp);
             }
           //simon 25 mayo 2005 fin
            msg.append(forma.getPrefix()).append(forma.getNumber()).append("<br/>");
            msg.append(getMessage("wf.canceledName")).append(" ").append(forma.getNameDocument()).append("<br/>");
            msg.append(getMessage("wf.canceledUser")).append(" ").append(usuario.getNamePerson()).append("<br/>");
            msg.append(getMessage("wf.cancelCause")).append(": ").append(getParameter("commentsUser"));
            int row = Integer.parseInt(getParameter("row"));
            if (getParameter("isFlexFlow")!=null && "true".compareTo(getParameter("isFlexFlow"))==0) {
                log.debug("Cerrando Flujo de Trabajo Paramétrico");
                if (!ToolsHTML.isEmptyOrNull(getParameter("idFlexFlow"))){
                    forma.setIdFlexFlow(Long.parseLong(getParameter("idFlexFlow")));
                }
                HandlerWorkFlows.canceledFlexFlow(time,forma,msg.toString(),
                                                  HandlerWorkFlows.canceled,HandlerWorkFlows.wfuCanceled,row,
                                                  getParameter("commentsUser"),HandlerDocuments.lastCanceledWF,
                                                  usuario.getEmail(),usuario.getNamePerson(),getMessage("wf.canceledTitle"),
                                                  usuario.getUser(),null);
            } else {
//                if (getParameter("idFlexFlow")==null) {
//                    throw new ApplicationExceptionChecked("E0108");
//                }
                HandlerWorkFlows.changeStatuWF(time,forma,msg.toString(),
                                               HandlerWorkFlows.ownercancelcloseflow,HandlerWorkFlows.ownercancelcloseflow,row,
                                               getParameter("commentsUser"),HandlerDocuments.lastCanceledWF,
                                               usuario.getEmail(),usuario.getNamePerson(),
                                               getMessage("wf.canceledTitle") + " " + forma.getPrefix()+forma.getNumber() 
                                               ,usuario.getUser(),null);
            }
            ActionForward resp = new ActionForward("/showDataDocument.do?idDocument="+idDocument,false);
            putObjectSession("info",getMessage("closeWF.ok"));
            resp.setName("success");
            return resp;
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
