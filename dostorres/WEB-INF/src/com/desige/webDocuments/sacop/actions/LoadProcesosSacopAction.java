package com.desige.webDocuments.sacop.actions;

import java.util.Collection;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.desige.webDocuments.persistent.managers.HandlerProcesosSacop;
import com.desige.webDocuments.persistent.utils.HibernateUtil;
import com.desige.webDocuments.sacop.forms.ProcesosSacop;
import com.desige.webDocuments.utils.ApplicationExceptionChecked;
import com.desige.webDocuments.utils.ToolsHTML;
import com.desige.webDocuments.utils.beans.SuperAction;
import com.desige.webDocuments.utils.beans.SuperActionForm;

/**
 * Created by IntelliJ IDEA.
 * User: srodriguez
 * Date: 07/12/2005
 * Time: 08:59:38 AM
 * To change this template use File | Settings | File Templates.
 */
public class LoadProcesosSacopAction extends SuperAction {
    static Logger log = LoggerFactory.getLogger(LoadProcesosSacopAction.class.getName());
    public ActionForward execute(ActionMapping actionMapping,
                                 ActionForm form, HttpServletRequest request,
                                 HttpServletResponse response) {
        super.init(actionMapping,form,request,response);
        String descript = request.getParameter("SearchDescrip");
        try {
            getUserSession();
            removeDataFieldEdit();
            Collection procesosSacop = HandlerProcesosSacop.getProcesosSacop(null);
            removeObjectSession("dataTable");
            putObjectSession("dataTable",procesosSacop);
            putObjectSession("sizeParam",String.valueOf(procesosSacop.size()));
            putObjectSession("editManager","/editPSacop.do");
            putObjectSession("newManager","/newProcesosSacop.do");
            putObjectSession("loadManager","loadProcesosSacop.do");
            putObjectSession("loadEditManager","/loadProcesosSacopEdit.do");
            putObjectSession("formEdit","editPSacop");
            String cmd = getCmd(request,false);
            ProcesosSacop forma = (ProcesosSacop)form;
            if (forma==null){
                //System.out.println("Forma Nula........");
                form = new ProcesosSacop();
            }
            if (ToolsHTML.checkValue(cmd)){
                ((SuperActionForm)form).setCmd(cmd);
                request.setAttribute("cmd",cmd);
            } else{
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
