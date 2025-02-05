package com.focus.wonderware.intocuh_sacop.actions;

import java.util.ResourceBundle;

import javax.mail.Session;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.desige.webDocuments.persistent.utils.HibernateUtil;
import com.desige.webDocuments.utils.ApplicationExceptionChecked;
import com.desige.webDocuments.utils.ToolsHTML;
import com.desige.webDocuments.utils.beans.SuperAction;
import com.focus.qweb.dao.ConfTagNameDAO;
import com.focus.qweb.to.ConfTagNameTO;
import com.focus.wonderware.intocuh_sacop.forms.Sacop_Intouch_Conftagname;

/**
 * Created by IntelliJ IDEA.
 * User: srodriguez
 * Date: Mar 9, 2007
 * Time: 3:16:39 PM
 * To change this template use File | Settings | File Templates.
 */
public class loadTagnameConfEdit extends SuperAction {
    public ActionForward execute(ActionMapping actionMapping,
                                 ActionForm form, HttpServletRequest request,
                                 HttpServletResponse response) {
        super.init(actionMapping,form,request,response);
        try {
             ResourceBundle rb = ToolsHTML.getBundle(request);
            getUserSession();
            //System.out.println("cargando Forma.............");

          //  HandlerProcesosSacop.load((Sacop_Intouch_Conftagname)form);
            ConfTagNameDAO oConfTagNameDAO = new ConfTagNameDAO();
            ConfTagNameTO oConfTagNameTO = new ConfTagNameTO();
            
            Sacop_Intouch_Conftagname forma=(Sacop_Intouch_Conftagname)form;
            oConfTagNameTO.setIdTagName(String.valueOf(forma.getIdtagname()));
            
            oConfTagNameDAO.cargar(oConfTagNameTO);
            
            forma = new Sacop_Intouch_Conftagname(oConfTagNameTO);
            form=forma;
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
