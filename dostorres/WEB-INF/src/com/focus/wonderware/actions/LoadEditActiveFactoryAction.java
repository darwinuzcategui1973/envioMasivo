package com.focus.wonderware.actions;

import java.util.ResourceBundle;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.desige.webDocuments.utils.ApplicationExceptionChecked;
import com.desige.webDocuments.utils.ToolsHTML;
import com.desige.webDocuments.utils.beans.SuperAction;
import com.focus.qweb.dao.ActiveFactoryDAO;
import com.focus.qweb.to.ActiveFactoryTO;
import com.focus.wonderware.forms.ActiveFactory_frm;

/**
 * Created by IntelliJ IDEA.
 * User: srodriguez
 * Date: Feb 15, 2007
 * Time: 11:22:56 AM
 * To change this template use File | Settings | File Templates.
 */
public class LoadEditActiveFactoryAction extends SuperAction {
    public ActionForward execute(ActionMapping actionMapping,
                                 ActionForm form, HttpServletRequest request,
                                 HttpServletResponse response) {
        super.init(actionMapping,form,request,response);
        HandlerProcesosWonderWare handlerProcesosWonderWare= new HandlerProcesosWonderWare();
        try {
             ResourceBundle rb = ToolsHTML.getBundle(request);
            getUserSession();
            
            ActiveFactoryDAO oActiveFactoryDAO = new ActiveFactoryDAO();
            ActiveFactoryTO oActiveFactoryTO = new ActiveFactoryTO();
            
            oActiveFactoryTO.setIdActivefactorydocument(String.valueOf(((ActiveFactory_frm)form).getIdactivefactorydocument()));
            oActiveFactoryDAO.cargar(oActiveFactoryTO);
            
            form = new ActiveFactory_frm(oActiveFactoryTO);
            
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
