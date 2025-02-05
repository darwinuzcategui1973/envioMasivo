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
import com.focus.wonderware.intocuh_sacop.forms.Sacop_Intouchh_frm;

/**
 * Created by IntelliJ IDEA.
 * User: srodriguez
 * Date: Mar 5, 2007
 * Time: 9:57:11 AM
 * To change this template use File | Settings | File Templates.
 */

public class loadsacop_intouch extends SuperAction {
    static Logger log = LoggerFactory.getLogger(loadsacop_intouch.class.getName());
    public ActionForward execute(ActionMapping actionMapping,
                                 ActionForm form, HttpServletRequest request,
                                 HttpServletResponse response) {
        super.init(actionMapping,form,request,response);
        //String descript = request.getParameter("SearchDescrip");
        try {
            getUserSession();
            //____blanqueamos las sessiones de sacop para inicializar ________//
            removeObjectSession("tagsnamesSeleccionados");
            removeObjectSession("planillasacop1Wonderware");
            removeDataFieldEdit();
            HandlerProcesosWonderWare loadAlarmsintouch = new HandlerProcesosWonderWare();
            //_________________________________________________________________________________________//
         
            Collection sacop_intouch = loadAlarmsintouch.getSacopIntouchWonderwarePadre();
            removeObjectSession("dataTable");
            putObjectSession("dataTable",sacop_intouch);
            putObjectSession("sizeParam",String.valueOf(sacop_intouch.size()));
            putObjectSession("editManager","/ProcesarSacopIntouch.do");
            putObjectSession("newManager","/newsacop_intouch.do");
            putObjectSession("loadManager","loadActiveFactory.do");
            putObjectSession("loadEditManager","/loadActiveFactoryEdit.do");
            putObjectSession("formEdit","newsacop_intouch1");
            String cmd = getCmd(request,false);
            Sacop_Intouchh_frm forma = (Sacop_Intouchh_frm)form;
            if (forma==null){
                //System.out.println("Forma Nula........");
                form = new Sacop_Intouchh_frm();
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

