package com.desige.webDocuments.activities.actions;

import java.util.Collection;

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
import com.desige.webDocuments.persistent.utils.HibernateUtil;
import com.desige.webDocuments.utils.ToolsHTML;
import com.desige.webDocuments.utils.beans.SuperAction;
import com.desige.webDocuments.utils.beans.SuperActionForm;
import com.focus.qweb.dao.ActivityDAO;
import com.focus.qweb.to.ActivityTO;

/**
 * Title: LoadActivityAction.java <br/>
 * Copyright: (c) 2004 Desige Servicios de Computación<br/>
 * @author Ing. Nelson Crespo
 * @version WebDocuments v1.0
 * <br/>
 *      Changes:<br/>
 * <ul>
 *      <li> 10/11/2005 (NC) Creation </li>
 * </ul>
 */
public class LoadActivityAction extends SuperAction {
    static Logger log = LoggerFactory.getLogger(LoadActivityAction.class.getName());
    public ActionForward execute(ActionMapping mapping,
                             ActionForm form,
                             HttpServletRequest request,
                             HttpServletResponse response) {
        super.init(mapping,form,request,response);
        log.debug("BEGIN");
        Activities activity = null;
        try {
            String number = getParameter("number");
            log.debug("Número: " + number);
            
            ActivityDAO oActivityDAO = new ActivityDAO();
            ActivityTO oActivityTO = new ActivityTO();

            oActivityTO.setActNumber(number);
            oActivityDAO.cargar(oActivityTO);
            
            activity = new Activities(oActivityTO);
            
            if (activity!=null) {
                log.debug("Flujo Name: " + activity.getName());
                activity.setCmd(SuperActionForm.cmdEdit);
            }
//            log.debug("Sub Act: "+activity.getSubActivitys());
//            activity.setExecutant(ToolsHTML.getUsers(activity.getActUser()));
//            activity.setNextExecutant(ToolsHTML.getUsers(activity.getNextUser()));
            putObjectSession("activitiesForm",activity);
            if (ToolsHTML.isEmptyOrNull(getParameter("isSub"))) {
                Collection acti = ActivityBD.getAllActivities(null);
                putObjectSession("activities",acti);
                return goSucces();
            } else {
//                log.debug("Sub Act: "+activity.getSubActivitys());
            	SubActivities subAct = new SubActivities();
                subAct.setCmd(SuperActionForm.cmdInsert);
                putObjectSession("subActivity",subAct);
                
                Collection sub = ActivityBD.getSubActivities(activity.getSubActivitys());
                	
                putObjectSession("subActivities",sub);
                putObjectSession("size",String.valueOf(sub.size()));
                return goTo("successSub");
            }
        } catch (Exception ex) {
            log.error(ex.getMessage());
            ex.printStackTrace();
        }
        return goError();
    }
}
