package com.desige.webDocuments.cargo.actions;

import java.util.ResourceBundle;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.desige.webDocuments.cargo.forms.Cargo;
import com.desige.webDocuments.persistent.managers.HandlerProcesosSacop;
import com.desige.webDocuments.utils.ApplicationExceptionChecked;
import com.desige.webDocuments.utils.ToolsHTML;
import com.desige.webDocuments.utils.beans.SuperAction;

/**
 * Created by IntelliJ IDEA.
 * User: srodriguez
 * Date: 02-ago-2006
 * Time: 15:27:48
 * To change this template use File | Settings | File Templates.
 */


/**
 * Created by IntelliJ IDEA.
 * User: Administrador
 * Date: 20/03/2006
 * Time: 11:02:10 AM
 * To change this template use File | Settings | File Templates.
 */

public class LoadEditCargoAction extends SuperAction {
    public ActionForward execute(ActionMapping actionMapping,
                                 ActionForm form, HttpServletRequest request,
                                 HttpServletResponse response) {
        super.init(actionMapping,form,request,response);
        try {
             ResourceBundle rb = ToolsHTML.getBundle(request);
            getUserSession();
            //System.out.println("cargando Forma.............");


            HandlerProcesosSacop.load((Cargo)form);

            removeObjectSession("valor");
            putObjectSession("editTypeDoc",form);
            putObjectSession("typeOtroFormato",form);
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
