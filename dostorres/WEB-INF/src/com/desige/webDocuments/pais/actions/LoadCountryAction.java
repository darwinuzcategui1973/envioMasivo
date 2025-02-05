package com.desige.webDocuments.pais.actions;

import java.util.Collection;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.desige.webDocuments.pais.forms.BaseCountryForm;
import com.desige.webDocuments.persistent.managers.HandlerPais;
import com.desige.webDocuments.utils.ApplicationExceptionChecked;
import com.desige.webDocuments.utils.ToolsHTML;
import com.desige.webDocuments.utils.beans.SuperAction;
import com.desige.webDocuments.utils.beans.SuperActionForm;

/**
 * Title: LoadCountryAction.java <br/>
 * Copyright: (c) 2004 Focus Consulting C.A.<br/>
 * @author Ing. Nelson Crespo (NC)
 * @version WebDocuments v3.0
 * <br/>
 * Changes:<br/> 
 * <ul>
 *      <li>18/04/2004 (NC) Creation </li>
 </ul>
 */
public class LoadCountryAction extends SuperAction {
    public ActionForward execute(ActionMapping actionMapping,
                                 ActionForm form, HttpServletRequest request,
                                 HttpServletResponse response) {
        super.init(actionMapping,form,request,response);
        try {
            getUserSession();
            removeDataFieldEdit();
            //System.out.println("LoadCountryAction.execute");
            String descript = request.getParameter("SearchDescrip");
            Collection paises = HandlerPais.getAllPaises(descript);
            putObjectSession("dataTable",paises);
            putObjectSession("sizeParam",String.valueOf(paises.size()));

            String input = (String)ToolsHTML.getAttribute(request,"input");
            String value = (String)ToolsHTML.getAttribute(request,"value");//request.getParameter("value");
            String nameForm = (String)ToolsHTML.getAttribute(request,"nameForma");//request.getParameter("nameForma");

            putAtributte(request,"input",input);
            putAtributte(request,"value",value);
            putAtributte(request,"nameForma",nameForm);

            putObjectSession("input",getDataFormResponse(nameForm,input,true));
            putObjectSession("value",getDataFormResponse(nameForm,value,false));

            putObjectSession("editManager","/editCountry.do");
            putObjectSession("newManager","/newCountry.do");
            putObjectSession("loadManager","loadCountries.do");
            putObjectSession("loadEditManager","/loadPaisEdit.do");
            putObjectSession("formEdit","country");

            String cmd = getCmd(request,false);
            BaseCountryForm forma = (BaseCountryForm)form;
            if (forma==null){
                //System.out.println("Forma Nula........");
                form = new BaseCountryForm();
            }
            if (ToolsHTML.checkValue(cmd)){
                ((BaseCountryForm)form).setCmd(cmd);
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
