package com.focus.wonderware.intocuh_sacop.actions;

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
import com.focus.wonderware.actions.HandlerProcesosWonderWare;
import com.focus.wonderware.intocuh_sacop.forms.Sacop_Intouch_Conftagname;

/**
 * Created by IntelliJ IDEA.
 * User: srodriguez
 * Date: Mar 9, 2007
 * Time: 3:10:49 PM
 * To change this template use File | Settings | File Templates.
 */
public class loadTagnameConf extends SuperAction {
    static Logger log = LoggerFactory.getLogger(loadTagnameConf.class.getName());
    public ActionForward execute(ActionMapping actionMapping,
                                 ActionForm form, HttpServletRequest request,
                                 HttpServletResponse response) {
        super.init(actionMapping,form,request,response);
        //String descript = request.getParameter("SearchDescrip");
        try {
            getUserSession();
            removeDataFieldEdit();
            HandlerProcesosWonderWare handlerProcesosWonderWare = new HandlerProcesosWonderWare();

            Collection sacop_Intouch_Conftagname = handlerProcesosWonderWare.getSacop_Intouch_Conftagname();
            removeObjectSession("dataTable");
            putObjectSession("dataTable",sacop_Intouch_Conftagname);
            putObjectSession("sizeParam",String.valueOf(sacop_Intouch_Conftagname.size()));
            putObjectSession("editManager","/editPTagnameConf.do");
            putObjectSession("newManager","/newTagnameConf.do");
            putObjectSession("loadManager","loadTagnameConf.do");
            putObjectSession("loadEditManager","/loadTagnameConfEdit.do");
            putObjectSession("formEdit","sacop_Intouch_Conftagname1");
            String cmd = getCmd(request,false);
            Sacop_Intouch_Conftagname forma = (Sacop_Intouch_Conftagname)form;
            //System.out.println("..........simons 2007-03-09 inicio..........................................");
            if (forma==null){
                //System.out.println("Forma Nula........");
                form = new Sacop_Intouch_Conftagname();
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
