package com.desige.webDocuments.sacop.actions;

import java.util.Calendar;

import javax.mail.Session;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transaction;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.desige.webDocuments.persistent.managers.HandlerProcesosSacop;
import com.desige.webDocuments.persistent.utils.HibernateUtil;
import com.desige.webDocuments.sacop.forms.Plantilla1BD;
import com.desige.webDocuments.sacop.forms.plantilla1;
import com.desige.webDocuments.utils.ToolsHTML;
import com.desige.webDocuments.utils.beans.SuperAction;
import com.focus.qweb.dao.PlanillaSacop1DAO;
import com.focus.qweb.to.PlanillaSacop1TO;

/**
 * Created by IntelliJ IDEA. User: srodriguez Date: 23/12/2005 Time: 11:42:15 AM
 * To change this template use File | Settings | File Templates.
 */

public class ActualizarPlanilla10Sacop extends SuperAction {
	private static Logger log = LoggerFactory.getLogger(ActualizarPlanillaSacop.class);

	/**
	 * 
	 */
	public synchronized ActionForward execute(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) {

		try {
			super.init(mapping, form, request, response);

			plantilla1 forma = (plantilla1) form;
			boolean swModificarBD = false;

			// solo modifica el usuario emisor o responsable y uno de ellos le
			// toca firmar en x momento en
			// del satus
			if (!ToolsHTML
					.isEmptyOrNull((String) getSessionObject("modificando"))) {
				if (getSessionObject("modificando").equals("1")) {
					swModificarBD = true;
				}
			}

			if (swModificarBD) {
				PlanillaSacop1TO oPlanillaSacop1TO = new PlanillaSacop1TO();
				PlanillaSacop1DAO oPlanillaSacop1DAO = new PlanillaSacop1DAO();
				
				oPlanillaSacop1TO.setIdplanillasacop1(forma.getIdplanillasacop1());
				oPlanillaSacop1DAO.cargar(oPlanillaSacop1TO);

				oPlanillaSacop1TO.setEstado(LoadSacop.edoVerificado);
				oPlanillaSacop1TO.setFechaVerificacion(ToolsHTML.calendarToTimeStampString(Calendar.getInstance()));

				oPlanillaSacop1DAO.actualizar(oPlanillaSacop1TO);

				HandlerProcesosSacop.mandarMailsUsuariosdelSacop(
						forma.getIdplanillasacop1(), forma.getSacopnum(),
						"Verificado.");
			}
		} catch (Exception ex) {
			log.error("Error: " + ex.getLocalizedMessage(), ex);
		} finally {
			/**/
		}

		log.info("Retornando con el forward 'success'");
		return goSucces();
	}
}
