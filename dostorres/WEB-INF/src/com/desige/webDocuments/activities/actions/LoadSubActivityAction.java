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
import com.desige.webDocuments.persistent.utils.HibernateUtil;
import com.desige.webDocuments.utils.ToolsHTML;
import com.desige.webDocuments.utils.beans.SuperAction;
import com.desige.webDocuments.utils.beans.SuperActionForm;
import com.focus.qweb.dao.ActivityDAO;
import com.focus.qweb.dao.SubActivitiesDAO;
import com.focus.qweb.to.ActivityTO;
import com.focus.qweb.to.SubActivitiesTO;

/**
 * Title: LoadSubActivityAction.java <br/>
 * Copyright: (c) 2004 Desige Servicios de Computación<br/>
 * @author Ing. Nelson Crespo
 * @version WebDocuments v1.0
 * <br/>
 *      Changes:<br/>
 * <ul>
 *      <li> 15/12/2005 (NC) Creation </li>
 * </ul>
 */
public class LoadSubActivityAction extends SuperAction {
    static Logger log = LoggerFactory.getLogger(LoadActivityAction.class.getName());
    public ActionForward execute(ActionMapping mapping,
                             ActionForm form,
                             HttpServletRequest request,
                             HttpServletResponse response) {
        super.init(mapping,form,request,response);
        log.debug("BEGIN");
        String number = getParameter("number");
        log.debug("Número: " + number);
        Activities activity = null;
        try {
            //Carga de la Actividad
            ActivityDAO oActivityDAO = new ActivityDAO();
            ActivityTO oActivityTO = new ActivityTO();

            oActivityTO.setActNumber(getParameter("activityID"));
            oActivityDAO.cargar(oActivityTO);
            
            activity = new Activities(oActivityTO);
        	
            if (activity!=null) {
                activity.setCmd(SuperActionForm.cmdEdit);
            }
            putObjectSession("activitiesForm",activity);
            //Carga de la Sub Actividad
            SubActivities subAct = null;
//            subAct = new SubActivities();
            SubActivitiesDAO oSubActivitiesDAO = new SubActivitiesDAO();
            SubActivitiesTO oSubActivitiesTO = new SubActivitiesTO();
            
            oSubActivitiesTO.setNumber(number);
            oSubActivitiesDAO.cargar(oSubActivitiesTO);
            
            subAct = new SubActivities(oSubActivitiesTO);
            
            if (subAct!=null) {
                subAct.setCmd(SuperActionForm.cmdEdit);
                log.debug("Usurios: "+subAct.getActUser());
                subAct.setExecutant(ToolsHTML.getUsers(subAct.getActUser()));
            }
            putObjectSession("subActivity",subAct);
            
            return goSucces();
        } catch (Exception ex) {
            log.error("Error: " + ex);
            ex.printStackTrace();
        }
        return goError();
    }
}
