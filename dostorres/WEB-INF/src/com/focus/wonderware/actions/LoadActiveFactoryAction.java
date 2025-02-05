package com.focus.wonderware.actions;

import java.util.Collection;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.desige.webDocuments.persistent.utils.HibernateUtil;
import com.desige.webDocuments.utils.ApplicationExceptionChecked;
import com.desige.webDocuments.utils.ToolsHTML;
import com.desige.webDocuments.utils.beans.SuperAction;
import com.desige.webDocuments.utils.beans.SuperActionForm;
import com.focus.wonderware.forms.ActiveFactory_frm;

/**
 * Created by IntelliJ IDEA.
 * User: srodriguez
 * Date: Feb 15, 2007
 * Time: 11:20:07 AM
 * To change this template use File | Settings | File Templates.
 */
public class LoadActiveFactoryAction extends SuperAction {
    static Logger log = LoggerFactory.getLogger(LoadActiveFactoryAction.class.getName());
    public ActionForward execute(ActionMapping actionMapping,
                                 ActionForm form, HttpServletRequest request,
                                 HttpServletResponse response) {
        super.init(actionMapping,form,request,response);
        //String descript = request.getParameter("SearchDescrip");
        try {
            getUserSession();
            removeDataFieldEdit();
            HandlerProcesosWonderWare handlerProcesosWonderWare = new HandlerProcesosWonderWare();
            ActiveFactory_frm activeFactory_NoSeUsaAqui=new ActiveFactory_frm();
            Collection activeFactoryDocuments = handlerProcesosWonderWare.getActiveFactoryDocuments(null,activeFactory_NoSeUsaAqui);
            removeObjectSession("dataTable");
            putObjectSession("dataTable",activeFactoryDocuments);
            putObjectSession("sizeParam",String.valueOf(activeFactoryDocuments.size()));
            putObjectSession("editManager","/editPActiveFactory.do");
            putObjectSession("newManager","/newActiveFactory.do");
            putObjectSession("loadManager","loadActiveFactory.do");
            putObjectSession("loadEditManager","/loadActiveFactoryEdit.do");
            putObjectSession("formEdit","activefactory1");
            String cmd = getCmd(request,false);
            ActiveFactory_frm forma = (ActiveFactory_frm)form;
            if (forma==null){
                //System.out.println("Forma Nula........");
                form = new ActiveFactory_frm();
            }

            if (ToolsHTML.checkValue(cmd)){
                ((SuperActionForm)form).setCmd(cmd);
                //System.out.println("cmd 2006-03-20 ="+cmd);
                request.setAttribute("cmd",cmd);
               // request.setAttribute("cmd",SuperActionForm.cmdLoad);

            } else{
                //System.out.println("cmd 2006-03-20 ="+cmd);
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

