package com.desige.webDocuments.persistent.managers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Vector;

import org.slf4j.*;

import com.desige.webDocuments.activities.forms.Activities;
import com.desige.webDocuments.activities.forms.SubActivities;
import com.desige.webDocuments.utils.ApplicationExceptionChecked;
import com.desige.webDocuments.utils.Constants;
import com.desige.webDocuments.utils.ToolsHTML;
import com.focus.qweb.dao.ActivityDAO;
import com.focus.qweb.dao.SubActivitiesDAO;
import com.focus.qweb.to.ActivityTO;
import com.focus.qweb.to.SubActivitiesTO;

/**
 * Title: ActivityBD.java <br/>
 * Copyright: (c) 2004 Desige Servicios de Computación<br/>
 * 
 * @author Ing. Nelson Crespo
 * @version WebDocuments v1.0 <br/>
 *          Changes:<br/>
 *          <ul>
 *          <li>09/11/2005 (NC) Creation</li>
 *          </ul>
 */
public class ActivityBD extends HandlerBD {
	static Logger log = LoggerFactory.getLogger(ActivityBD.class.getName());

	public static void save(Activities forma)
			throws ApplicationExceptionChecked {
		try {
			ActivityDAO oActivityDAO = new ActivityDAO();
			
			ActivityTO oActivityTO = new ActivityTO(forma);
			oActivityDAO.insertar(oActivityTO);
			
			forma.setNumber(Long.parseLong(oActivityTO.getActNumber()));
			
		} catch (Exception ex) {
			ex.printStackTrace();
			throw new ApplicationExceptionChecked("E0100");
		}
	}

	public static void save(SubActivities forma)
			throws ApplicationExceptionChecked {
		try {
			
			SubActivitiesDAO oSubActivitiesDAO = new SubActivitiesDAO();
			
			SubActivitiesTO oSubActivitiesTO = new SubActivitiesTO(forma);
			oSubActivitiesDAO.insertar(oSubActivitiesTO);
			
			forma.setNumber(Long.parseLong(oSubActivitiesTO.getNumber()));
			
		} catch (Exception ex) {
			ex.printStackTrace();
			throw new ApplicationExceptionChecked("E0100");
		}
	}

	public static Collection getAllActivities(String typeDoc) {
		ActivityDAO oActivityDAO = new ActivityDAO();
		
		ArrayList<ActivityTO> lista = null;
		
		Vector datos = new Vector();
		
		try {
			if (ToolsHTML.isEmptyOrNull(typeDoc)) {
					lista = oActivityDAO.listarActAlls();
			} else {
				log.debug("typeDoc: " + typeDoc);
				lista = oActivityDAO.listarActByTypeDoc(typeDoc);
			}
			
			Activities act = null;
			if (lista != null && lista.size()>0) {
				for (Iterator iter = lista.iterator(); iter.hasNext();) {
					act = new Activities((ActivityTO) iter.next());
					datos.add(act);
				}
				return datos;
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public static Collection getSubActivities(ArrayList dataLoad) {
		
		Vector datos = new Vector();
		if (dataLoad != null) {
			try {
				Iterator items = dataLoad.iterator();
				while (items.hasNext()) {
					SubActivities subactivities = (SubActivities) items.next();
					if (subactivities.getActive() == Constants.permission) {
						log.debug("Act Name: " + subactivities.getNameAct());
						datos.add(subactivities);
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return datos;
	}


}
