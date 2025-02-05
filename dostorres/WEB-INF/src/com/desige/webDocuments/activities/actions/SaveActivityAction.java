package com.desige.webDocuments.activities.actions;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.desige.webDocuments.activities.forms.Activities;
import com.desige.webDocuments.activities.forms.SubActivities;
import com.desige.webDocuments.persistent.managers.ActivityBD;
import com.desige.webDocuments.utils.IDDBFactorySql;
import com.desige.webDocuments.utils.beans.SuperAction;
import com.desige.webDocuments.utils.beans.SuperActionForm;
import com.focus.qweb.dao.ActivityDAO;
import com.focus.qweb.to.ActivityTO;

/**
 * Title: SaveActivityAction.java <br/>
 * Copyright: (c) 2004 Desige Servicios de Computación<br/>
 * @author Ing. Nelson Crespo
 * @version WebDocuments v1.0
 * <br/>
 *      Changes:<br/>
 * <ul>
 *      <li> 09/11/2005 (NC) Creation </li>
 * </ul>
 */
public class SaveActivityAction extends SuperAction {
    static Logger log = LoggerFactory.getLogger(SaveActivityAction.class.getName());
    public ActionForward execute(ActionMapping mapping,
                             ActionForm form,
                             HttpServletRequest request,
                             HttpServletResponse response) {
        super.init(mapping,form,request,response);
        log.debug("BEGIN");
        try {
        	Activities activity = (Activities)form;
            if (SuperActionForm.cmdInsert.equalsIgnoreCase(activity.getCmd())) {
                long id = IDDBFactorySql.getNextIDLong("activity");
                activity.setNumber(id);
                
                // agregamos la actividad
                ActivityDAO oActivityDAO = new ActivityDAO();
                ActivityTO oActivityTO = new ActivityTO(activity);
                
                oActivityDAO.insertar(oActivityTO);
                
//                ActionForward resp = new ActionForward("/newAct.do",false);
                activity.setCmd(SuperActionForm.cmdInsert);
                putObjectSession("activitiesForm",activity);
                SubActivities subAct = new SubActivities();
                subAct.setCmd(SuperActionForm.cmdInsert);
                putObjectSession("subActivity",subAct);
                return goSucces();
            } else {
                if (SuperActionForm.cmdDelete.equalsIgnoreCase(activity.getCmd())) {
                    activity.setActive((byte)0);
                }
                ActivityBD.saveOrUpdate(activity);
            }

            //Si se está Eliminando el Flujo de Trabajo se redirige la carga a la
            //Pantalla Principal de los Flujos Paramétricos
            if (SuperActionForm.cmdDelete.equalsIgnoreCase(activity.getCmd())) {
                putObjectSession("info",getMessage("act.deleteOK"));
                return goTo("successDel");
            }
            if (SuperActionForm.cmdEdit.equalsIgnoreCase(activity.getCmd())) {
                putObjectSession("info",getMessage("act.saveOk"));
                log.debug("Número: " + activity.getNumber());
                return new ActionForward("/loadActivity.do?number="+activity.getNumber());
//                return goTo("successEdit");
            }
            return goSucces("act.saveOk");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return goError();
    }
}
