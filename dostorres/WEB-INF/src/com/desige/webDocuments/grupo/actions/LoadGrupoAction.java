package com.desige.webDocuments.grupo.actions;

import java.util.Collection;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.desige.webDocuments.grupo.forms.grupoForm;
import com.desige.webDocuments.persistent.managers.HandlerGrupo;
import com.desige.webDocuments.utils.ApplicationExceptionChecked;
import com.desige.webDocuments.utils.ToolsHTML;
import com.desige.webDocuments.utils.beans.SuperAction;
import com.desige.webDocuments.utils.beans.SuperActionForm;

/**
 * Title: LoadGrupoAction.java <br/>
 * Copyright: (c) 2004 Focus Consulting C.A.<br/>
 * @author Ing. Roger Rodríguez
 * @version WebDocuments v3.0
 * <br/>
 * Changes:<br/> 
 * <ul>
 *      <li>25/06/2004 (RR) Creation </li>
 </ul>
 */
public class LoadGrupoAction extends SuperAction {
    public ActionForward execute(ActionMapping actionMapping,
                                 ActionForm form, HttpServletRequest request,
                                 HttpServletResponse response) {
        super.init(actionMapping,form,request,response);
        try {
            getUserSession();
            //System.out.println("LoadGrupoAction.execute");
            Collection grupos = HandlerGrupo.getAllGrupos();
            Collection desgrupos = HandlerGrupo.getDescriptionGrupos();
            putObjectSession("dataTable",grupos);
            putObjectSession("dataTable",desgrupos);
            putObjectSession("sizeParam",String.valueOf(grupos.size()));
//            request.getSession().setAttribute("dataTable",grupos);
//            request.getSession().setAttribute("dataTable",desgrupos);
//            request.getSession().setAttribute("sizeParam",String.valueOf(grupos.size()));

            String input = (String)ToolsHTML.getAttribute(request,"input");
            String value = (String)ToolsHTML.getAttribute(request,"value");//request.getParameter("value");
            String nameForm = (String)ToolsHTML.getAttribute(request,"nameForma");//request.getParameter("nameForma");

            putAtributte(request,"input",input);
            putAtributte(request,"value",value);
            putAtributte(request,"nameForma",nameForm);

            putObjectSession("input",getDataFormResponse(nameForm,input,true));
            putObjectSession("value",getDataFormResponse(nameForm,value,false));

            putObjectSession("editManager","/editGrupo.do");
            putObjectSession("newManager","/newGrupo.do");
            putObjectSession("loadManager","/loadGrupo.do");
            putObjectSession("loadEditManager","/loadGrupoEdit.do");
            putObjectSession("formEdit","grupo");

            String cmd = getCmd(request,false);
            grupoForm forma = (grupoForm)form;
            if (forma==null){
                //System.out.println("Forma Nula........");
                form = new grupoForm();
            }
            if (ToolsHTML.checkValue(cmd)){
                ((grupoForm)form).setCmd(cmd);
                request.setAttribute("cmd",cmd);
            } else{
                request.setAttribute("cmd",SuperActionForm.cmdLoad);
            }
            return goSucces();
        } catch (ApplicationExceptionChecked ae) {
            return goError(ae.getKeyError());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return goError();
    }

}
