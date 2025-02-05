package com.desige.webDocuments.sacop.actions;

import java.util.ResourceBundle;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.desige.webDocuments.persistent.managers.HandlerProcesosSacop;
import com.desige.webDocuments.sacop.forms.PlantillaAccion;
import com.desige.webDocuments.utils.ApplicationExceptionChecked;
import com.desige.webDocuments.utils.ToolsHTML;
import com.desige.webDocuments.utils.beans.SuperAction;

/**
 * Created by IntelliJ IDEA.
 * User: srodriguez
 * Date: 02/03/2006
 * Time: 02:56:10 PM
 * To change this template use File | Settings | File Templates.
 */

public class LoadEditAccionesTomarSacopAction extends SuperAction {
	
	/**
	 * 
	 */
    public ActionForward execute(ActionMapping actionMapping,
                                 ActionForm form, HttpServletRequest request,
                                 HttpServletResponse response) {
        super.init(actionMapping,form,request,response);
        try {
            ResourceBundle rb = ToolsHTML.getBundle(request);
            getUserSession();
            putAtributte("idplanillasacop",request.getParameter("idplanillasacop")!=null?request.getParameter("idplanillasacop"):"");
            PlantillaAccion form1 = (PlantillaAccion)form;
            putAtributte("idaccion",new Long(form1.getIdplanillasacopaccion()));
            HandlerProcesosSacop.load((PlantillaAccion)form);
            HandlerProcesosSacop.loadUserAccioneSacop((PlantillaAccion)form);
            removeObjectSession("valor");
            putObjectSession("editTypeDoc",form);
            removeObjectSession("descript");
            return goSucces();
        } catch (ApplicationExceptionChecked ae) {
            return goError(ae.getKeyError());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return goError();
    }

}
