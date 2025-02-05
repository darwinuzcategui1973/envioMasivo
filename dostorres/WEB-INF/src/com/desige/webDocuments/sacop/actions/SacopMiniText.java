package com.desige.webDocuments.sacop.actions;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.desige.webDocuments.utils.ApplicationExceptionChecked;
import com.desige.webDocuments.utils.beans.SuperAction;

/**
 * Created by IntelliJ IDEA.
 * User: Administrador
 * Date: 06/02/2006
 * Time: 08:54:27 PM
 * To change this template use File | Settings | File Templates.
 */

public class SacopMiniText extends SuperAction {
	public ActionForward execute(ActionMapping mapping,
                                 ActionForm form, HttpServletRequest request,
                                 HttpServletResponse response) {
		try {
            super.init(mapping,form,request,response);
            getUserSession();
			String input = request.getParameter("input");
            String value = request.getParameter("value");
            if (input.contentEquals("<FONT ")){
                input="";
            }
             if (value.startsWith("<FONT ")){
                value="";
                input="";
            }
            String nameForm = request.getParameter("nameForma");
            putObjectSession("input",getDataFormResponse(nameForm,input,true));
            putObjectSession("value",getDataFormResponse(nameForm,value,false));
            return goSucces();
        } catch (ApplicationExceptionChecked ae) {
                return goError(ae.getKeyError());
		} catch (Exception ex){
		    ex.printStackTrace();
		}
		return mapping.findForward("error");
	}

}
