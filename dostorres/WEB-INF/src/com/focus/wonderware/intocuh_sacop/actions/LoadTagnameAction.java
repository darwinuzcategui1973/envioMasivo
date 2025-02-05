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
import com.focus.wonderware.intocuh_sacop.forms.Tagname;

/**
 * Created by IntelliJ IDEA.
 * User: srodriguez
 * Date: Mar 12, 2007
 * Time: 10:10:55 AM
 * To change this template use File | Settings | File Templates.
 */
public class LoadTagnameAction extends SuperAction {
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

            String noUsoFiltro="";
            Collection tagname = handlerProcesosWonderWare.getIntouch(noUsoFiltro);
            removeObjectSession("dataTable");
            putObjectSession("dataTable",tagname);
            putObjectSession("sizeParam",String.valueOf(tagname.size()));
            putObjectSession("editManager","/editPTagname.do");
            putObjectSession("newManager","/newTagname.do");
            putObjectSession("loadManager","loadTagname.do");
            putObjectSession("loadEditManager","/loadTagnameEdit.do");
            putObjectSession("formEdit","tagname1");
            String cmd = getCmd(request,false);
            Tagname forma = (Tagname)form;

             if (forma==null){
                //System.out.println("Forma Nula........");
                form = new Tagname();
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
