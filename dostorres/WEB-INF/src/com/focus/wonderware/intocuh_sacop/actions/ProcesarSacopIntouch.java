package com.focus.wonderware.intocuh_sacop.actions;

import java.util.Collection;
import java.util.Iterator;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.desige.webDocuments.sacop.forms.Plantilla1BDesqueleto;
import com.desige.webDocuments.utils.ApplicationExceptionChecked;
import com.desige.webDocuments.utils.Constants;
import com.desige.webDocuments.utils.beans.SuperAction;
import com.desige.webDocuments.utils.beans.Users;
import com.focus.wonderware.actions.HandlerProcesosWonderWare;

/**
 * Created by IntelliJ IDEA. User: srodriguez Date: Mar 5, 2007 Time: 4:22:16 PM
 * To change this template use File | Settings | File Templates.
 */
public class ProcesarSacopIntouch extends SuperAction {
	public ActionForward execute(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) {
		super.init(mapping, form, request, response);
		try {

			Users user = getUserSession();
			com.desige.webDocuments.sacop.forms.plantilla1 forma = (com.desige.webDocuments.sacop.forms.plantilla1) form;
			putObjectSession("tagsnamesSeleccionados", forma.getSacop_intouch());
			// en que estado se quiere configurar el esqueleto..(Emitido o
			// borrador)
			String estadoEsqueletoConfiguradoSacop = request
					.getParameter("edodelsacop");
			String correcpreven = request.getParameter("correcpreven");

			Plantilla1BDesqueleto planillasacop1Wonderware = (Plantilla1BDesqueleto) getSessionObject("planillasacop1Wonderware");

			boolean swBorrador = false;
			if (planillasacop1Wonderware != null) {
				if (planillasacop1Wonderware.getIdplanillasacop1() != 0) {
					swBorrador = true;
				}
			}

			// buscamos si esa planilla getIdplanillasacop1 que es de esqueleto,
			// se encuentra registrada
			// en el campo idplanillasacop1esqueleto de tbl_planillasacop1, si
			// es asi, esa planilla esqueleto no c puede
			// modificar
			boolean swNoModificar = false;
			if (swBorrador) {

				HandlerProcesosWonderWare handlerProcesosWonderWare = new HandlerProcesosWonderWare();
				Collection collection = handlerProcesosWonderWare
						.getAllPlanillasSacop1(String
								.valueOf(planillasacop1Wonderware
										.getIdplanillasacop1()));
				Iterator itPlanillaSacop1 = collection.iterator();
				if (itPlanillaSacop1.hasNext()) {
					swNoModificar = true;
				}

			}

			StringBuffer url = new StringBuffer();
			url.append("/loadSACOPMain.do?goTo=planillauno")
					.append("&correcpreven=")
					.append(request.getParameter("correcpreven"));
			url.append("&origensacop=").append(
					request.getParameter("origensacop"));
			if (swBorrador) {
				url.append("&edodelsacop=").append("0").append("&")
						.append("Idplanillasacop1=")
						.append(planillasacop1Wonderware.getIdplanillasacop1());
			}
			if (swNoModificar) {
				url.append("&NoModificarSacopIntouch=").append("1");
			}
			url.append("&generarSacop_Intouch=").append(Constants.permissionSt);
			// en que estado se quiere configurar el esqueleto..(Emitido o
			// borrador)
			url.append("&estadoEsqueletoConfiguradoSacop=").append(
					estadoEsqueletoConfiguradoSacop);
			// si es correctiva o preventiva
			url.append("&correcpreven=").append(correcpreven);

			ActionForward crearSacop_Intouch = new ActionForward(url.toString());
			// loadSacop.getBorradorPrimeravez(request,showCharge,user);
			return crearSacop_Intouch;
			// return goSucces();
		} catch (ApplicationExceptionChecked ae) {
			ae.printStackTrace();
			return goError(ae.getKeyError());
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
		}
		return goError();
	}

}
