package com.desige.webDocuments.causa.actions;

import java.util.Collection;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.desige.webDocuments.area.forms.Area;
import com.desige.webDocuments.causa.forms.PossibleCause;
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

public class LoadPossibleCauseAction extends SuperAction {
    static Logger log = LoggerFactory.getLogger(LoadPossibleCauseAction.class.getName());
    public ActionForward execute(ActionMapping actionMapping,
                                 ActionForm form, HttpServletRequest request,
                                 HttpServletResponse response) {
        super.init(actionMapping,form,request,response);
        //String descript = request.getParameter("SearchDescrip");
        try {
            getUserSession();
            removeDataFieldEdit();
            Collection possibleCause = HandlerProcesosSacop.getPossibleCause(null);
            removeObjectSession("dataTable");
            putObjectSession("dataTable",possibleCause);
            putObjectSession("sizeParam",String.valueOf(possibleCause.size()));
            putObjectSession("editManager","/editPPossibleCause.do");
            putObjectSession("newManager","/newPossibleCause.do");
            putObjectSession("loadManager","loadPossibleCause.do");
            putObjectSession("loadEditManager","/loadPossibleCauseEdit.do");
            putObjectSession("formEdit","frmPossibleCause1");
            String cmd = getCmd(request,false);
            Area forma = (Area)form;
            if (forma==null){
            	log.debug("Forma Nula........");
                form = new PossibleCause();
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
