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
import com.desige.webDocuments.utils.beans.SuperAction;
import com.desige.webDocuments.utils.beans.SuperActionForm;

/**
 * Title: CreateActAction.java <br/>
 * Copyright: (c) 2004 Desige Servicios de Computación<br/>
 *
 * @author Ing. Nelson Crespo
 * @version WebDocuments v1.0
 * <br/>
 *      Changes:<br/>
 * <ul>
 *      <li> 29/03/2006 (NC) Creation </li>
 * </ul>
 */
public class CreateActAction extends SuperAction {
    static Logger log = LoggerFactory.getLogger(SaveActivityAction.class.getName());
    public ActionForward execute(ActionMapping mapping,
                             ActionForm form,
                             HttpServletRequest request,
                             HttpServletResponse response) {
        super.init(mapping,form,request,response);
        try {
        	Activities forma = (Activities)form;
            forma.setCmd(SuperActionForm.cmdInsert);
            putObjectSession("activitiesForm",forma);
            SubActivities subAct = new SubActivities();
            subAct.setCmd(SuperActionForm.cmdInsert);
            putObjectSession("subActivity",subAct);
            return goSucces();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return goError();
    }
}
