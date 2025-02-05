package com.desige.webDocuments.sacop.actions;

import java.util.ResourceBundle;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transaction;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.desige.webDocuments.persistent.managers.HandlerDocuments;
import com.desige.webDocuments.persistent.managers.HandlerProcesosSacop;
import com.desige.webDocuments.persistent.managers.HandlerTypeDoc;
import com.desige.webDocuments.persistent.utils.HibernateUtil;
import com.desige.webDocuments.sacop.forms.PlantillaAccion;
import com.desige.webDocuments.sacop.forms.TitulosPlanillasSacop;
import com.desige.webDocuments.utils.ApplicationExceptionChecked;
import com.desige.webDocuments.utils.ToolsHTML;
import com.desige.webDocuments.utils.beans.SuperAction;
import com.desige.webDocuments.utils.beans.SuperActionForm;

/**
 * Created by IntelliJ IDEA. User: srodriguez Date: 02/03/2006 Time: 02:53:32 PM
 * To change this template use File | Settings | File Templates.
 */

public class UpdateAccionesTomarSacopAction extends SuperAction {

	/**
	 * 
	 */
	public synchronized ActionForward execute(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) {
		super.init(mapping, form, request, response);
		
		try {
			String cmd = request.getParameter("cmd");
			PlantillaAccion formaAccion = (PlantillaAccion) form;
			request.getSession().setAttribute("otroProcesoSacop", form);
			getUserSession();
			formaAccion.setIdplanillasacop1(Long.parseLong(request
					.getParameter("idplanillasacop")));
			formaAccion.setActive((byte) 1);
			formaAccion
					.setAccion(request.getParameter("numerodeaccion") != null ? request
							.getParameter("numerodeaccion").trim() : "Acción");
			formaAccion.setFecha(request.getParameter("anio") + "-"
					+ request.getParameter("mes") + "-"
					+ request.getParameter("dia"));
			formaAccion.setCmd(cmd);
			if ((request.getParameterValues("usersSelected") != null)) {
				String[] usuariosSeleccionados = request
						.getParameterValues("usersSelected");
				StringBuffer usuariosResponsables = new StringBuffer("");
				for (int j = 0; j < usuariosSeleccionados.length; j++) {
					usuariosResponsables.append(HandlerDocuments.getField2(
							"idperson",
							" from person where accountActive='1' and nameuser='"
									+ usuariosSeleccionados[j] + "'"));
					if (j + 1 < usuariosSeleccionados.length) {
						usuariosResponsables.append(",");
					}
				}
				formaAccion.setResponsables(usuariosResponsables.toString());
			}

			if (ToolsHTML.checkValue(cmd)) {
				if ((cmd.equalsIgnoreCase(SuperActionForm.cmdNew))
						|| (cmd.equalsIgnoreCase(SuperActionForm.cmdLoad))) {
					if (cmd.equalsIgnoreCase(SuperActionForm.cmdNew)) {
						removeObjectSession("valor");
						removeObjectSession("editTypeDoc");

					}
					form = new TitulosPlanillasSacop();
					cmd = SuperActionForm.cmdInsert;
				} else {
					processCmd(formaAccion, request);
					cmd = SuperActionForm.cmdLoad;
					if (cmd.equalsIgnoreCase(SuperActionForm.cmdDelete)) {
						// /borramos la data que trae esta variable para que no
						// aparezca en el formulario
						removeObjectSession("editTypeDoc");
						removeObjectSession("valor");
					}
				}
				((SuperActionForm) form).setCmd(cmd);
				request.setAttribute("cmd", cmd);
			}
			putObjectSession(
					"idplanillasacop",
					request.getParameter("idplanillasacop") != null ? request
							.getParameter("idplanillasacop") : "");
			return goSucces();
		} catch (ApplicationExceptionChecked ae) {
			ae.printStackTrace();
			return goError(ae.getKeyError());
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return goError();
	}

	/**
	 * 
	 * @param forma
	 * @param request
	 * @return
	 * @throws ApplicationExceptionChecked
	 */
	private static boolean processCmd(PlantillaAccion forma,
			HttpServletRequest request) throws ApplicationExceptionChecked {
		// System.out.println("forma = " + forma.getCmd());
		boolean resp = false;
		StringBuilder mensaje = null;
		ResourceBundle rb = ToolsHTML.getBundle(request);

		if (forma.getCmd().equalsIgnoreCase(SuperActionForm.cmdInsert)) {
			// System.out.println("Insertar Registro...");
			try {
				resp = HandlerProcesosSacop.insert(forma);
			} catch (Exception e) {
				e.printStackTrace();
			}
			if (resp) {
				request.setAttribute("info", rb.getString("app.editOk"));
				// forma.cleanForm();
				return true;
			} else {
				mensaje = new StringBuilder(rb.getString("app.notEdit"));
				mensaje.append(" ").append(HandlerTypeDoc.getMensaje());
			}
		} else {
			if (forma.getCmd().equalsIgnoreCase(SuperActionForm.cmdEdit)) {
				// System.out.println("Editando Registro...");
				resp = HandlerProcesosSacop.updateAccion(forma);
				// HibernateUtil.saveOrUpdate(forma);
				request.setAttribute("info", rb.getString("app.editOk"));
				return true;
			} else {
				if (forma.getCmd().equalsIgnoreCase(SuperActionForm.cmdDelete)) {
					// System.out.println("Elimando registro....");
					if (HandlerProcesosSacop.deleteAccion(forma)) {
						// forma.cleanForm();
						request.setAttribute("info", rb.getString("app.delete"));
					} else {
						mensaje = new StringBuilder(
								rb.getString("app.notDelete")).append(" ")
								.append(HandlerTypeDoc.getMensaje());
					}
				}
			}
		}
		if (mensaje != null) {
			// System.out.println("mensaje = " + mensaje);
			request.setAttribute("info", mensaje.toString());
		}
		return false;
	}

}
