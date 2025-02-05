package com.desige.webDocuments.activities.actions;

import java.util.Collection;
import java.util.Hashtable;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.desige.webDocuments.activities.forms.SubActivities;
import com.desige.webDocuments.persistent.managers.HandlerDBUser;
import com.desige.webDocuments.persistent.managers.HandlerProcesosSacop;
import com.desige.webDocuments.persistent.utils.HibernateUtil;
import com.desige.webDocuments.utils.ApplicationExceptionChecked;
import com.desige.webDocuments.utils.ToolsHTML;
import com.desige.webDocuments.utils.beans.SuperAction;
import com.focus.qweb.dao.SubActivitiesDAO;
import com.focus.qweb.to.SubActivitiesTO;

/**
 * Title: LoadUserActivitiesAction.java <br/> Copyright: (c) 2004 Desige Servicios de Computación<br/>
 * 
 * @author Ing. Nelson Crespo
 * @version WebDocuments v1.0 <br/> Changes:<br/>
 *          <ul>
 *          <li> 21/11/2005 (NC) Creation </li>
 *          </ul>
 */
public class LoadUserActivitiesAction extends SuperAction {
	static Logger log = LoggerFactory.getLogger(LoadUserActivitiesAction.class.getName());

	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
		super.init(mapping, form, request, response);
		SubActivities subActivity = null;
		try {
			boolean after = false; // modificar firmantes luego de creado el flujo
			removeObjectSession("usuarios");
			removeObjectSession("from");
		
			log.debug("Begin LoadUserActivitiesAction");
			String number = getParameter("number");
			String idWorkFlow = getParameter("idWorkFlow");
			log.debug("Número: " + number);
			String type = getParameter("type");
			log.debug("type: " + type);
			// subAct = (SubActivities) HibernateUtil.loadObject(SubActivities.class,Long.parseLong(String.valueOf(number)));
			subActivity = (SubActivities) getSessionObject("subActivity");
			log.debug("" + subActivity);
			if (subActivity != null) {
				log.debug("Número: " + subActivity.getNumber());
			}
			long numero = 0;
			if (ToolsHTML.isNumeric(number)){
				numero =  new Long(number);
			}
			
			SubActivitiesDAO oSubActivitiesDAO = new SubActivitiesDAO();
			SubActivitiesTO oSubActivitiesTO = new SubActivitiesTO();
			
			oSubActivitiesTO.setNumber(String.valueOf(numero));
			oSubActivitiesDAO.cargar(oSubActivitiesTO);
			
			SubActivities subAct = new SubActivities(oSubActivitiesTO);
			putObjectSession("subActivity", subAct);
			Collection usuarios = null;
			// if (Constants.notPermissionSt.equalsIgnoreCase(type)) {
			log.debug("Ejecutante");
			Hashtable userAct = new Hashtable();
			// Si estoy Editando la Actividad
			if (subAct != null && subAct.getNumber() != 0) {
				log.debug("subAct.getActUser() =" + subAct.getActUser());
				userAct = ToolsHTML.setUserToHash(subAct.getActUser());
			}
			
			if (request.getParameter("cambiarFirmante") != null && String.valueOf(request.getParameter("cambiarFirmante")).equals("true") ) {
				usuarios = HandlerDBUser.getAllUserInSubActivitiesWithOutTemp(idWorkFlow, number);
				putObjectSession("idWorkFlow",idWorkFlow);
				after = true;
			} else {
				usuarios = HandlerDBUser.getAllsUsers(userAct);
			}
			// } else {
			// log.debug("Próximo Ejecutante");
			// Hashtable userAct = ToolsHTML.setUserToHash(subActivity.getNextUser());
			// usuarios = HandlerDBUser.getAllsUsers(userAct);
			// }

			// Collection usuarios = HandlerDBUser.getAllsUsers(userAct);
			// putObjectSession("usuario","usuarios");

			String input = request.getParameter("input");
			String value = request.getParameter("value");
			String nameForm = request.getParameter("nameForma");
			
			//Nuevo 29/nov/2007
			String userAssociate = request.getParameter("userAssociate");
			putObjectSession("number",number);

			String porArea = getParameter("porArea");
			String idArea = getParameter("idarea");
			if(porArea==null || porArea.equals("null")) {
				idArea = null;
			}
			
			if(!ToolsHTML.isEmptyOrNull(porArea) && "1".equals(porArea) && !ToolsHTML.isEmptyOrNull(idArea)){
				usuarios = HandlerDBUser.getAllsUsersInArea(idArea, numero);
			}

			putObjectSession("userAssociate", userAssociate);
			putObjectSession("idarea", idArea);
			putObjectSession("inputNew", input);
			putObjectSession("valueNew", value);
			putObjectSession("nameFormaNew", nameForm);
			//Fin Nuevo 29/nov/2007
			
			putObjectSession("usuarios", usuarios);

			if (!usuarios.isEmpty()) {
				putObjectSession("size", String.valueOf(usuarios.size()));
			}
			
			putObjectSession("input", getDataFormResponse(nameForm, input, true));
			putObjectSession("value", getDataFormResponse(nameForm, value, false));
			putAtributte("type", type);

			//Nuevo 29/nov/2007
			Collection area = HandlerProcesosSacop.getArea(null);
            if (area.size()<=0){
              throw new ApplicationExceptionChecked("scp.noexistearea");
            }
            putObjectSession("area",area);
			//Fin Nuevo 29/nov/2007
            
			log.debug("End");
			if(after) {
				return mapping.findForward("successAfter");
			} else {
				return goSucces();
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return goError();
	}
}
