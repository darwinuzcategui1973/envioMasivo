package com.desige.webDocuments.typeDocuments.actions;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.desige.webDocuments.persistent.managers.HandlerTypeDoc;
import com.desige.webDocuments.typeDocuments.forms.TypeDocumentsForm;
import com.desige.webDocuments.utils.ApplicationExceptionChecked;
import com.desige.webDocuments.utils.beans.SuperAction;

/**
 * Title: ${name}.java<br>
 * Copyright: (c) 2003 Consis International<br>
 * Company: Consis International (CON)<br>
 * @author Nelson Crespo (NC)
 * @author Simon Ropdriguez. (SR)
 * @version Acsel-e v2.2
 * <br>
 * Changes:<br>
 * <ul>
 *      <li> 22/08/2004 (NC) Creation </li>
 *      <li> 16/05/2005 (SR) Se agrego un atributo valor, para desaparecer el boton delete de editField.jsp si vas a eliminar registro
 *                           o Formato </li>
 * <ul>
 */
public class LoadEditTypeDocAction extends SuperAction {
    public ActionForward execute(ActionMapping actionMapping,
                                 ActionForm form, HttpServletRequest request,
                                 HttpServletResponse response) {
        super.init(actionMapping,form,request,response);
        try {
//             ResourceBundle rb = ToolsHTML.getBundle(request);
            getUserSession();
            //System.out.println("cargando Forma.............");
            HandlerTypeDoc.load((TypeDocumentsForm)form);
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
