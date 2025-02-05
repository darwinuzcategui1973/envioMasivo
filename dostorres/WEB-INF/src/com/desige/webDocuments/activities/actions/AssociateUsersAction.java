package com.desige.webDocuments.activities.actions;

import java.util.Collection;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Set;
import java.util.StringTokenizer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.desige.webDocuments.activities.forms.SubActivities;
import com.desige.webDocuments.perfil.forms.PerfilActionForm;
import com.desige.webDocuments.persistent.utils.HibernateUtil;
import com.desige.webDocuments.utils.ToolsHTML;
import com.desige.webDocuments.utils.beans.SuperAction;

/**
 * Title: AssociateUsersAction.java <br/> Copyright: (c) 2004 Desige Servicios de Computación<br/>
 * 
 * @author Ing. Nelson Crespo
 * @version WebDocuments v1.0 <br/> Changes:<br/>
 *          <ul>
 *          <li> 22/11/2005 (NC) Creation </li>
 *          </ul>
 */
public class AssociateUsersAction extends SuperAction {
	static Logger log = LoggerFactory.getLogger(AssociateUsersAction.class.getName());

	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
		log.debug("Begin");
		super.init(mapping, form, request, response);
		try {
			String type = getParameter("type");
			if (getSessionObject("subActivity") != null || true) {
				SubActivities activitiesForm = (SubActivities) getSessionObject("subActivity");
				if (activitiesForm==null) {
					activitiesForm = new SubActivities();
					putObjectSession("subActivity", activitiesForm);
				}
				String valuesSelecteds = getParameter("userAssociate");
				StringBuffer items = new StringBuffer(100);
				String value = (String) getSessionObject("value");
				if (!ToolsHTML.isEmptyOrNull(value)) {
					int posI = value.indexOf("=");
					items.append(value.substring(0, posI)).append("='");
				}
				if (!ToolsHTML.isEmptyOrNull(valuesSelecteds)) {
					StringTokenizer st = new StringTokenizer(valuesSelecteds, ",");
					Set set = new HashSet();
					Collection usuarios = (Collection) getSessionObject("usuarios");
					Hashtable userAct = ToolsHTML.setUserToHash(new HashSet(usuarios));
					PerfilActionForm perfil = null;
					boolean isFirts = true;
					while (st.hasMoreTokens()) {
						String item = st.nextToken();
						if (ToolsHTML.isNumeric(item)) {
							perfil = (PerfilActionForm) userAct.get(new Long(item));
							if (perfil != null && !set.contains(perfil)) {
								log.debug("Associate: " + perfil.getApellidos());
								items.append(perfil.getApellidos()).append(" ").append(perfil.getNombres()).append(", ");
								set.add(perfil);
							}
						}
					}
					// if (Constants.notPermissionSt.equalsIgnoreCase(type)) {
					if (activitiesForm != null) {
						activitiesForm.setActUser(set);
					}
					// } else {

					// activitiesForm.setNextUser(set);
					// }
					if (items.length() > 0) {
						items.replace(items.length() - 2, items.length(), "';");
						// items.append("';");
						putObjectSession("value", items.toString());
						log.debug("Items: " + items);
					}
					putAtributte("closeWindow", "true");
				} else {
					log.debug("Por el Else");
					Set set = new HashSet();
					if (activitiesForm != null) {
						activitiesForm.setActUser(set);
					}
					items.append("';");
					putObjectSession("value", items.toString());
					putAtributte("closeWindow", "true");
				}
			}
			log.debug("End");
			return goSucces();
		} catch (Exception ex) {
			log.error(ex.getMessage());
			ex.printStackTrace();
		}
		log.debug("End");
		return goError();
	}
}
