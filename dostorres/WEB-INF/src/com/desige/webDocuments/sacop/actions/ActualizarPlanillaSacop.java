package com.desige.webDocuments.sacop.actions;

import java.util.Locale;
import java.util.ResourceBundle;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.desige.webDocuments.persistent.managers.HandlerProcesosSacop;
import com.desige.webDocuments.sacop.forms.Plantilla1BD;
import com.desige.webDocuments.sacop.forms.plantilla1;
import com.desige.webDocuments.utils.DesigeConf;
import com.desige.webDocuments.utils.ToolsHTML;
import com.desige.webDocuments.utils.beans.SuperAction;
import com.focus.qweb.dao.PlanillaSacop1DAO;
import com.focus.qweb.to.PlanillaSacop1TO;

/**
 * Created by IntelliJ IDEA. User: srodriguez Date: 16/12/2005 Time: 03:17:24 PM To change this template use File | Settings | File Templates.
 */
public class ActualizarPlanillaSacop extends SuperAction {
	// static Logger log = LoggerFactory.getLogger(ActualizarPlanillaSacop.class.getName());
	public synchronized ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
		try {

			Locale defaultLocale = new Locale(DesigeConf.getProperty("language.Default"), DesigeConf.getProperty("country.Default"));
			ResourceBundle rb = ResourceBundle.getBundle("LoginBundle", defaultLocale);
			super.init(mapping, form, request, response);
			plantilla1 forma = (plantilla1) form;
			Plantilla1BD formaBD = new Plantilla1BD();
			String resultado = "";
			// buscamos el numero de planilla del sacop
			// Collection sacop= HandlerProcesosSacop.getInfResponsable("idplanillasacop1",forma.getIdplanillasacop1(),false);

			// Iterator it = sacop.iterator();
			// if(it.hasNext()){
			// Plantilla1BD formabd = (Plantilla1BD) it.next();
			PlanillaSacop1DAO oPlanillaSacop1DAO = new PlanillaSacop1DAO();
			PlanillaSacop1TO oPlanillaSacop1TO = new PlanillaSacop1TO();
			
			//Plantilla1BD formabd = new Plantilla1BD();
			//formabd = (Plantilla1BD) session.load(Plantilla1BD.class, new Long(forma.getIdplanillasacop1()));
			
			oPlanillaSacop1TO.setIdplanillasacop1(forma.getIdplanillasacop1());
			oPlanillaSacop1DAO.cargar(oPlanillaSacop1TO);
			
			if (!LoadSacop.acpetadoNo.equalsIgnoreCase(forma.getRechazoapruebo())) {
				oPlanillaSacop1TO.setEstado(LoadSacop.edoAprobado);
				oPlanillaSacop1TO.setRechazoapruebo(LoadSacop.acpetadoSi);
				
				// de la plantilla dos, se va a la plantilla 3.0 en LoadSacop.do automaticamente.
				// Se coloca la session Idplanillasacop1 para que no pierda su valor.
				putObjectSession("Idplanillasacop1", forma.getIdplanillasacop1());
				oPlanillaSacop1TO.setNoaceptada("");
				// mandar mails
				resultado = rb.getString("scp.aprobado1");
			} else if (LoadSacop.acpetadoNo.equalsIgnoreCase(forma.getRechazoapruebo())) {
				// no aceptada es el comentario que se hace en caso de ser negativa bla respuesta ..
				oPlanillaSacop1TO.setNoaceptada(forma.getNoaceptada());
				oPlanillaSacop1TO.setEstado(LoadSacop.edoRechazado);
				oPlanillaSacop1TO.setRechazoapruebo(LoadSacop.acpetadoNo);
				resultado = rb.getString("scp.rechazado1");
				// mandar mails
			}
			HandlerProcesosSacop.mandarMailsUsuariosdelSacop(forma.getIdplanillasacop1(), forma.getSacopnum(), resultado);
			// solo modifica el usuario emisor o responsable y uno de ellos le toca firmar en x momento en
			// del satus
			if (!ToolsHTML.isEmptyOrNull((String) getSessionObject("modificando"))) {
				if (getSessionObject("modificando").equals("1")) {
					oPlanillaSacop1DAO.actualizar(oPlanillaSacop1TO);
				}
			}
			// }
			// }catch (ApplicationExceptionChecked ae) {
			// return goError(ae.getKeyError());
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return goSucces();
	}
}
