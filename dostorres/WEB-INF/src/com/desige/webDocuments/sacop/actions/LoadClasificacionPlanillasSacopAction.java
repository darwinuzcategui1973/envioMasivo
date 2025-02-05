package com.desige.webDocuments.sacop.actions;

import java.util.Collection;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.desige.webDocuments.persistent.managers.HandlerProcesosSacop;
import com.desige.webDocuments.persistent.utils.HibernateUtil;
import com.desige.webDocuments.sacop.forms.ClasificacionPlanillasSacop;
import com.desige.webDocuments.sacop.forms.TitulosPlanillasSacop;
import com.desige.webDocuments.utils.ApplicationExceptionChecked;
import com.desige.webDocuments.utils.ToolsHTML;
import com.desige.webDocuments.utils.beans.SuperAction;
import com.desige.webDocuments.utils.beans.SuperActionForm;

/**
 * 
 * Title: LoadClasificacionPlanillasSacopAction.java <br>
 * Copyright: (c) 2012 Focus Consulting C.A.<br>
 * @author Ing. Felipe Rojas (FJR)
 * 
 * @version WebDocuments v4.5.2
 * <br>
 *     Changes:<br>
 * <ul>
 *     <li> 20/04/2012 (FJR) Creation </li>
 * <ul>
 */
public class LoadClasificacionPlanillasSacopAction extends SuperAction {
    private static Logger log = LoggerFactory.getLogger(LoadClasificacionPlanillasSacopAction.class.getName());
    
    public ActionForward execute(ActionMapping actionMapping,
    		ActionForm form, HttpServletRequest request, HttpServletResponse response) {
    	
        super.init(actionMapping, form, request, response);
        String descript = request.getParameter("SearchDescrip");
        
        try {
            getUserSession();
            removeDataFieldEdit(); //  getTitulosPlanillasSacop
            Collection clasificacionSacop = HandlerProcesosSacop.getClasificacionPlanillasSacop(null);
            removeObjectSession("dataTable");
            putObjectSession("dataTable", clasificacionSacop);
            putObjectSession("sizeParam", String.valueOf(clasificacionSacop.size()));
            putObjectSession("editManager", "/editPClasificacionSacop.do");
            putObjectSession("newManager", "/newClasificacionPlanillasSacop.do");
            putObjectSession("loadManager", "LoadClasificacionPlanillasSacop.do");
            putObjectSession("loadEditManager", "/loadClasificacionPlanillasSacopEdit.do");
            putObjectSession("formEdit", "editPClasificacionSacop");
            
            String cmd = getCmd(request,false);
            ClasificacionPlanillasSacop forma = (ClasificacionPlanillasSacop) form;
            if (forma==null){
                //System.out.println("Forma Nula........");
                form = new ClasificacionPlanillasSacop();
            }
            if (ToolsHTML.checkValue(cmd)){
                ((SuperActionForm)form).setCmd(cmd);
                request.setAttribute("cmd",cmd);
            } else{
                request.setAttribute("cmd",SuperActionForm.cmdLoad);
            }
            return goSucces();
        } catch (ApplicationExceptionChecked ae) {
            log.error(ae.getMessage());
            return goError(ae.getKeyError());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return goError();
    }
}
