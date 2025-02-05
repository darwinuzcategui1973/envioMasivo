package com.desige.webDocuments.activities.actions;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.TreeMap;

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
import com.desige.webDocuments.utils.ApplicationExceptionChecked;
import com.desige.webDocuments.utils.beans.SuperAction;
import com.desige.webDocuments.utils.beans.SuperActionForm;
import com.focus.qweb.dao.ActivityDAO;
import com.focus.qweb.dao.SubActivitiesDAO;
import com.focus.qweb.to.ActivityTO;
import com.focus.qweb.to.SubActivitiesTO;

/**
 * Title: SaveSubActivityAction.java <br/> Copyright: (c) 2004 Desige Servicios
 * de Computación<br/>
 * 
 * @author Ing. Nelson Crespo
 * @version WebDocuments v1.0 <br/> Changes:<br/>
 *          <ul>
 *          <li> 14/12/2005 (NC) Creation </li>
 *          </ul>
 */
public class SaveSubActivityAction extends SuperAction {
	static Logger log = LoggerFactory.getLogger(SaveSubActivityAction.class.getName());

	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
		super.init(mapping, form, request, response);
		log.debug("BEGIN");
		try {
			ActivityDAO oActivityDAO = new ActivityDAO();
			SubActivitiesDAO oSubActivitiesDAO = new SubActivitiesDAO();

			SubActivities sub = (SubActivities) form;
			
			String msg = "subAct.saveOK";
			log.debug("Command " + sub.getCmd());
			if (getSessionObject("activitiesForm") != null) {
				Activities activity = (Activities) getSessionObject("activitiesForm");
				activity.setActive((byte) 1);
				log.debug("activity.getCmd() = " + activity.getCmd());
				
				if (SuperActionForm.cmdInsert.equalsIgnoreCase(activity.getCmd())) {
					// Se Asocia la Actividad al Nuevo Flujo que se está Creando
					ArrayList lista = oActivityDAO.listarFtpByName(activity.getName());
					
					if (lista.size() == 0) {
						ActivityBD.save(sub);
					} else {
						throw new ApplicationExceptionChecked("E0110");
					}
					activity.getSubActivitys().add(sub);
					
					// Se guarda la Actividad
					oSubActivitiesDAO = new SubActivitiesDAO();
					
					ArrayList listaSub = oSubActivitiesDAO.listarByNumberAndName(String.valueOf(sub.getActivityID()),sub.getNameAct());
					
					if (listaSub == null || listaSub.size() == 0) {
						ActivityBD.save(activity);
					} else {
						ActivityBD.saveOrUpdate(activity);
						// throw new ApplicationExceptionChecked("E0109");
					}

					StringBuilder ret = new StringBuilder("/loadActivity.do?isSub=true&number=").append(activity.getNumber());
					ActionForward result = new ActionForward(ret.toString(), false);
					return result;
				} else {
					// Si estoy Editando la Actividad se actualiza antes de la
					// Carga
					if (SuperActionForm.cmdEdit.equalsIgnoreCase(sub.getCmd())) {
						
						ArrayList lista = oSubActivitiesDAO.listarByNameAndId(sub.getNameAct(), String.valueOf(sub.getActivityID()), String.valueOf(sub.getNumber()) );
						
						if (lista.size() == 0) {
							oSubActivitiesDAO.actualizar(new SubActivitiesTO(sub));
						} else {
							log.debug("ERROR");
							throw new ApplicationExceptionChecked("E0109");
						}
					}
					if (SuperActionForm.cmdDelete.equalsIgnoreCase(sub.getCmd())) {
						sub.setActive((byte) 0);
						msg = "subAct.deleteOK";
						oSubActivitiesDAO.actualizar(new SubActivitiesTO(sub));
					}
				}
			} else {
				// Si estoy Editando la Actividad se actualiza antes de la Carga
				if (SuperActionForm.cmdEdit.equalsIgnoreCase(sub.getCmd())) {
					log.debug("Usrs " + sub.getActUser());
					oSubActivitiesDAO.actualizar(new SubActivitiesTO(sub));
				}
			}

			// Carga Padre
			ActivityTO oActivityTO = new ActivityTO();
			oActivityTO.setActNumber(String.valueOf(sub.getActivityID()));
			oActivityDAO.cargar(oActivityTO);
			
			Activities activity = new Activities(oActivityTO);
			
			

			if (SuperActionForm.cmdInsert.equalsIgnoreCase(sub.getCmd())) {
				// if (SuperActionForm.cmdEdit.equalsIgnoreCase(sub.getCmd()) ||
				// SuperActionForm.cmdInsert.equalsIgnoreCase(sub.getCmd()) ) {
				try {
					log.debug("[Save] sub = " + sub);
					ActivityBD.save(sub);
					//activity.getSubActivitys().add(sub);
				} catch (Exception e) {

				}
			}
			
			// Carga Hijos
			//Collection datos = ActivityBD.getSubActivities(activity.getSubActivitys());
			ArrayList datos = oActivityTO.getSubActivitiesTO();
			
			

			log.debug("Actualizando Actividades si Aplica.... size = " + datos.size());
			// ActivityBD.getSubActivities(activity.getSubActivitys());
			Iterator items = datos.iterator();
			if (items != null) {
				int sumar = 0;
				int orden = 0;
				String posicion = String.valueOf(sub.getOrden());
				sub.setOrden(10000); // colocamos un numero de actividad alta
										// para que quede de ultimo al ordenar
										// la primera vez
				int cont = 1;
				TreeMap<Integer, SubActivities> subOrdenado;

				SubActivities subActivities = null;
				for (int i = 0; i < 2; i++) {
					subOrdenado = new TreeMap<Integer, SubActivities>();

					SubActivitiesTO aux = null;
					while (items.hasNext()) {
						aux = (SubActivitiesTO)items.next();
						subActivities = new SubActivities(aux);
						
						sumar = ((sub.getNumber() != subActivities.getNumber() && new Integer(subActivities.getOrden()).intValue() >= new Integer(sub.getOrden()).intValue()) ? 1 : 0);
						orden = new Integer(subActivities.getOrden()).intValue() + sumar;
						subActivities.setOrden(orden);
						subOrdenado.put(new Integer(orden), subActivities);
					}

					cont = 1;
					for (Iterator it = subOrdenado.keySet().iterator(); it.hasNext();) {
						Integer clave = (Integer) it.next();
						subActivities = (SubActivities) subOrdenado.get(clave);
						subActivities.setOrden(cont++);
						if (i == 1) {
							//ActivityBD.saveOrUpdate(subActivities);
							oSubActivitiesDAO.actualizar(new SubActivitiesTO(subActivities));
						}
					}
					//items = ActivityBD.getSubActivities(activity.getSubActivitys()).iterator();
					if (i == 0) {
						sub.setOrden(Integer.parseInt(posicion)); // ahora le colocamos la
												// posicion donde queremos que
												// quede realmente
					}
				}

			}

			activity.getSubActivitys().add(sub);
			ActivityBD.saveOrUpdate(activity);

			// Fin Prueba
			// if (SuperActionForm.cmdInsert.equalsIgnoreCase(sub.getCmd())) {
			// ActivityBD.save(sub);
			// } else {
			// if (SuperActionForm.cmdDelete.equalsIgnoreCase(sub.getCmd())) {
			// sub.setActive((byte)0);
			// msg = "subAct.deleteOK";
			// }
			// ActivityBD.saveOrUpdate(sub);
			// }

			
			putObjectSession("info", getMessage(msg));
			ActionForward result = new ActionForward("/loadActivity.do?isSub=true&number=" + sub.getActivityID(), false);
			return result;
		} catch (ApplicationExceptionChecked ae) {
			//System.out.println("[ApplicationExceptionChecked] " + ae.getKeyError());
			ae.printStackTrace();
			return goError(ae.getKeyError());
		} catch (Exception ex) {
			//System.out.println("[Exception]");
			ex.printStackTrace();
		}
		return goError();
	}
}
