package com.focus.wonderware.intocuh_sacop.actions;

import java.util.ResourceBundle;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.desige.webDocuments.utils.ApplicationExceptionChecked;
import com.desige.webDocuments.utils.ToolsHTML;
import com.desige.webDocuments.utils.beans.SuperAction;
import com.focus.qweb.dao.TagNameDAO;
import com.focus.qweb.to.TagNameTO;
import com.focus.wonderware.intocuh_sacop.forms.Tagname;

/**
 * Created by IntelliJ IDEA.
 * User: srodriguez
 * Date: Mar 12, 2007
 * Time: 10:48:15 AM
 * To change this template use File | Settings | File Templates.
 */
public class LoadEditTagnameAction extends SuperAction {
    public ActionForward execute(ActionMapping actionMapping,
                                 ActionForm form, HttpServletRequest request,
                                 HttpServletResponse response) {
        super.init(actionMapping,form,request,response);
        try {
             ResourceBundle rb = ToolsHTML.getBundle(request);
            getUserSession();
            //System.out.println("cargando Forma.............");


            Tagname forma=(Tagname)form;
            
            TagNameDAO oTagNameDAO = new TagNameDAO();
            TagNameTO oTagNameTO = new TagNameTO();
            
            oTagNameTO.setIdTagName2(String.valueOf(forma.getIdtagname2()));
            oTagNameDAO.cargar(oTagNameTO);
            
            forma = new Tagname(oTagNameTO);
            
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
 