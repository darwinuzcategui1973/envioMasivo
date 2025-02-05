package com.desige.webDocuments.state.actions;

import java.util.Collection;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.desige.webDocuments.persistent.managers.HandlerState;
import com.desige.webDocuments.state.forms.BaseStateForm;
import com.desige.webDocuments.utils.ApplicationExceptionChecked;
import com.desige.webDocuments.utils.ToolsHTML;
import com.desige.webDocuments.utils.beans.SuperAction;
import com.desige.webDocuments.utils.beans.SuperActionForm;

/**
 * Title: LoadStateAction.java <br/>
 * Copyright: (c) 2004 Focus Consulting C.A.<br/>
 * @author Ing. Nelson Crespo (NC)
 * @version WebDocuments v3.0
 * <br/>
 * Changes:<br/> 
 * <ul>
 *      <li>29/04/2004 (NC) Creation </li>
 </ul>
 */
public class LoadStateAction extends SuperAction {
    public ActionForward execute(ActionMapping actionMapping,
                                 ActionForm form, HttpServletRequest request,
                                 HttpServletResponse response) {
        super.init(actionMapping,form,request,response);
        String descript = request.getParameter("SearchDescrip");
        try {
            getUserSession();
            Collection states = HandlerState.getAllState(descript);
            removeDataFieldEdit();
            putObjectSession("dataTable",states);
            putObjectSession("sizeParam",String.valueOf(states.size()));

//            String input = request.getParameter("input");
//            String value = request.getParameter("value");
//            String nameForm = request.getParameter("nameForma");

            String input = (String)ToolsHTML.getAttribute(request,"input");
            String value = (String)ToolsHTML.getAttribute(request,"value");
            String nameForm = (String)ToolsHTML.getAttribute(request,"nameForma");

            putObjectSession("input",getDataFormResponse(nameForm,input,true));
            putObjectSession("value",getDataFormResponse(nameForm,value,false));
            putObjectSession("editManager","/editState.do");
            putObjectSession("newManager","/newState.do");
            putObjectSession("loadManager","loadState.do");
            putObjectSession("loadEditManager","/loadStateEdit.do");
            putObjectSession("formEdit","state");
            String cmd = getCmd(request,false);
            BaseStateForm forma = (BaseStateForm)form;
            if (forma==null){
                //System.out.println("Forma Nula........");
                form = new BaseStateForm();
            }
            if (ToolsHTML.checkValue(cmd)){
                ((BaseStateForm)form).setCmd(cmd);
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
