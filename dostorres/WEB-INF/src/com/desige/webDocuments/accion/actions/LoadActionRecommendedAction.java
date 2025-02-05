package com.desige.webDocuments.accion.actions;

import java.util.Collection;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.desige.webDocuments.accion.forms.ActionRecommended;
import com.desige.webDocuments.persistent.managers.HandlerProcesosSacop;
import com.desige.webDocuments.utils.ApplicationExceptionChecked;
import com.desige.webDocuments.utils.ToolsHTML;
import com.desige.webDocuments.utils.beans.SuperAction;
import com.desige.webDocuments.utils.beans.SuperActionForm;

/**
 * Created by IntelliJ IDEA.
 * User: Administrador
 * Date: 20/03/2006
 * Time: 10:26:54 AM
 * To change this template use File | Settings | File Templates.
 */

public class LoadActionRecommendedAction extends SuperAction {
    static Logger log = LoggerFactory.getLogger(LoadActionRecommendedAction.class.getName());
    public ActionForward execute(ActionMapping actionMapping,
                                 ActionForm form, HttpServletRequest request,
                                 HttpServletResponse response) {
        super.init(actionMapping,form,request,response);
        try {
            getUserSession();
            removeDataFieldEdit();
            Collection actionRecommended = HandlerProcesosSacop.getActionRecommended(null);
            removeObjectSession("dataTable");
            putObjectSession("dataTable",actionRecommended);
            putObjectSession("sizeParam",String.valueOf(actionRecommended.size()));
            putObjectSession("editManager","/editPActionRecommended.do");
            putObjectSession("newManager","/newActionRecommended.do");
            putObjectSession("loadManager","loadActionRecommended.do");
            putObjectSession("loadEditManager","/loadActionRecommendedEdit.do");
            putObjectSession("formEdit","frmActionRecommended1");
            String cmd = getCmd(request,false);
            ActionRecommended forma = (ActionRecommended)form;
            if (forma==null){
            	log.debug("Forma Nula........");
                form = new ActionRecommended();
            }

            if (ToolsHTML.checkValue(cmd)){
                ((SuperActionForm)form).setCmd(cmd);
                log.debug("cmd 2006-03-20 ="+cmd);
                request.setAttribute("cmd",cmd);
               // request.setAttribute("cmd",SuperActionForm.cmdLoad);

            } else{
            	log.debug("cmd 2006-03-20 ="+cmd);
                request.setAttribute("cmd",SuperActionForm.cmdLoad);
            }
            return goSucces();
        } catch (ApplicationExceptionChecked ae) {
            log.error(ae.getMessage());
            return goError(ae.getKeyError());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return goError();
    }
}
