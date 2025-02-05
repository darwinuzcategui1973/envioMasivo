package com.desige.webDocuments.norms.actions;

import java.util.Collection;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.desige.webDocuments.norms.forms.BaseNormsForm;
import com.desige.webDocuments.persistent.managers.HandlerNorms;
import com.desige.webDocuments.utils.ApplicationExceptionChecked;
import com.desige.webDocuments.utils.ToolsHTML;
import com.desige.webDocuments.utils.beans.SuperAction;
import com.desige.webDocuments.utils.beans.SuperActionForm;

/**
 * Title: ${name}.java<br>
 * Copyright: (c) 2003 Consis International<br>
 * Company: Consis International (CON)<br>
 * @author Nelson Crespo (NC)
 * @version Acsel-e v2.2
 * <br>
 * Changes:<br>
 * <ul>
 *      <li> 22/08/2004 (NC) Creation </li>
 * <ul>
 */
public class LoadNormsAction extends SuperAction {
    public ActionForward execute(ActionMapping actionMapping,
                                 ActionForm form, HttpServletRequest request,
                                 HttpServletResponse response) {
        //System.out.println("[LoadNormsAction]");
        super.init(actionMapping,form,request,response);
        String descript = request.getParameter("SearchDescrip");
        try {
            getUserSession();
            Collection norms = HandlerNorms.getAllNorms(descript);
            removeDataFieldEdit();
            removeObjectSession("dataTable");
            putObjectSession("descript","0");
            putObjectSession("dataTable",norms);
            putObjectSession("sizeParam",String.valueOf(norms.size()));
            putObjectSession("editManager","/editNorm.do");
            putObjectSession("newManager","/newNorm.do");
            putObjectSession("loadManager","loadNorms.do");
            putObjectSession("loadEditManager","/loadNormsEdit.do");
            putObjectSession("formEdit","editNorms");
            
            request.setAttribute("selected",request.getParameter("selected"));
			request.setAttribute("hiddenField", request.getParameter("field"));


            String cmd = getCmd(request,false);
            //System.out.println("cmd = " + cmd);
            BaseNormsForm forma = (BaseNormsForm)form;
            if (forma==null){
                //System.out.println("Forma Nula........");
                form = new BaseNormsForm();
            }
            if (ToolsHTML.checkValue(cmd)){
                ((SuperActionForm)form).setCmd(cmd);
                request.setAttribute("cmd",cmd);
            } else{
                request.setAttribute("cmd",SuperActionForm.cmdLoad);
            }
            return goSucces();
        } catch (ApplicationExceptionChecked ae) {
            ae.printStackTrace();
            return goError(ae.getKeyError());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return goError();
    }
}
