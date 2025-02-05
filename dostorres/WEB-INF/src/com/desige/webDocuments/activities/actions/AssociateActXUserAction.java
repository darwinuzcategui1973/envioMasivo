package com.desige.webDocuments.activities.actions;

import java.util.Collection;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.desige.webDocuments.persistent.managers.ActivityBD;
import com.desige.webDocuments.persistent.managers.HandlerDBUser;
import com.desige.webDocuments.utils.DesigeConf;
import com.desige.webDocuments.utils.beans.SuperAction;

/**
 * Title: AssociateActXUserAction.java <br/>
 * Copyright: (c) 2004 Desige Servicios de Computación<br/>
 *
 * @author Ing. Nelson Crespo
 * @version WebDocuments v1.0
 * <br/>
 *      Changes:<br/>
 * <ul>
 *      <li> 04/11/2005 (NC) Creation </li>
 * </ul>
 */
public class AssociateActXUserAction extends SuperAction {
    static Logger log = LoggerFactory.getLogger(AssociateActXUserAction.class.getName());
    public ActionForward execute(ActionMapping mapping,
                                 ActionForm form,
                                 HttpServletRequest request,
                                 HttpServletResponse response) {
        log.debug("Begin");
        super.init(mapping,form,request,response);
        try {
            //Cargamos Todas las Actividades definidas por el Usuario
            Collection acti = ActivityBD.getAllActivities(DesigeConf.getProperty("typeDocs.docRegister"));
            putObjectSession("activities",acti);
            String idStruct = getParameter("idStruct");
            log.debug("idStruct: " + idStruct);
            putAtributte("idStruct",idStruct);
            Collection usuarios = HandlerDBUser.getAllUsersFilter(null);
            putObjectSession("usuarios",usuarios);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return goSucces();
    }
}
