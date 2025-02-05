package com.desige.webDocuments.sacop.actions;

import java.util.Collection;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.desige.webDocuments.persistent.managers.HandlerDBUser;
import com.desige.webDocuments.persistent.managers.HandlerDocuments;
import com.desige.webDocuments.persistent.managers.HandlerParameters;
import com.desige.webDocuments.persistent.managers.HandlerProcesosSacop;
import com.desige.webDocuments.persistent.utils.JDBCUtil;
import com.desige.webDocuments.sacop.forms.Plantilla1BD;
import com.desige.webDocuments.sacop.forms.PlantillaAccion;
import com.desige.webDocuments.utils.ApplicationExceptionChecked;
import com.desige.webDocuments.utils.Constants;
import com.desige.webDocuments.utils.ToolsHTML;
import com.desige.webDocuments.utils.beans.SuperAction;
import com.desige.webDocuments.utils.beans.SuperActionForm;
import com.desige.webDocuments.utils.beans.Users;
import com.focus.qweb.dao.PlanillaSacop1DAO;
import com.focus.qweb.dao.PossibleCauseDAO;
import com.focus.qweb.to.PlanillaSacop1TO;

/**
 * Created by IntelliJ IDEA. User: srodriguez Date: 02/03/2006 Time: 02:50:11 PM
 * To change this template use File | Settings | File Templates.
 */

public class LoadAccionesTomarSacopAction extends SuperAction {
	private static final Logger log = LoggerFactory.getLogger(PlantillaAccion.class
			.getName());

	/**
	 * 
	 */
	public ActionForward execute(ActionMapping actionMapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) {
		super.init(actionMapping, form, request, response);
		try {
			String idplanillasacop = null;
			// String descript = request.getParameter("SearchDescrip");
			if (!ToolsHTML.isEmptyOrNull(request
					.getParameter("idplanillasacop"))) {
				idplanillasacop = request.getParameter("idplanillasacop") != null ? request
						.getParameter("idplanillasacop") : "";
				putAtributte("idplanillasacop", idplanillasacop);

			} else {
				putAtributte("idplanillasacop",
						getSessionObject("idplanillasacop"));
				idplanillasacop = (String) getSessionObject("idplanillasacop");
				removeObjectSession("idplanillasacop");
			}

			Users user = getUserSession();
			removeDataFieldEdit();
			// cargamos los datos de la planilla
			// Collection
			// planilla=HandlerProcesosSacop.getInfResponsable("idplanillasacop1",idplanillasacop,false);
			// Iterator it = planilla.iterator();
			// if(it.hasNext()){
			// todas stas variables son usadas en accionestomarsacop.jsp
			// Plantilla1BD obj = (Plantilla1BD) it.next();

			PlanillaSacop1TO oPlanillaSacop1TO = new PlanillaSacop1TO();
			PlanillaSacop1DAO oPlanillaSacop1DAO = new PlanillaSacop1DAO();
			
			oPlanillaSacop1TO.setIdplanillasacop1(idplanillasacop);
			oPlanillaSacop1DAO.cargar(oPlanillaSacop1TO);
			
			Plantilla1BD obj = new Plantilla1BD(oPlanillaSacop1TO); 
			
			putAtributte("fechaEstimada",obj.getFechaEstimada() != null ? obj.getFechaEstimada():"");
			
			putAtributte("fechaSacops1",obj.getFechasacops1()!= null ? ToolsHTML.getFechaAMD(obj.getFechasacops1()):"");
						
			putAtributte("Accionobservacion",obj.getAccionobservacion() != null ? obj.getAccionobservacion() : "");
			putAtributte("descripcionAccionPrincipal",obj.getDescripcionAccionPrincipal() != null ? obj.getDescripcionAccionPrincipal() : "");
			putAtributte("archivoTecnica",obj.getArchivoTecnica() != null ? obj.getArchivoTecnica():"");
			putAtributte("causasnoconformidad",obj.getCausasnoconformidad() != null ? obj.getCausasnoconformidad() : "");
			
			// causas
			PossibleCauseDAO oPossibleCauseDAO = new PossibleCauseDAO();
			putAtributte("listPossibleCause", oPossibleCauseDAO.listarPossibleCauseAlls(null));
			putAtributte("ListOrderPossibleCause", oPossibleCauseDAO.listarOrderPossibleCauseAlls(null));
			
			putAtributte("idDocumentAssociate",String.valueOf(obj.getIdDocumentAssociate()));
			putAtributte("numVerDocumentAssociate",String.valueOf(obj.getNumVerDocumentAssociate()));
			putAtributte("nameDocumentAssociate",obj.getNameDocumentAssociate());
			putAtributte("requireTracking",obj.getRequireTracking());
			putAtributte("requireTrackingDate",obj.getRequireTrackingDate());
			

			// }
			String editar = "/editAccionesTomarSacop.do";
			// la variable userponecomentario es utilizada para cada persona que
			// tiene que colocar
			// un comentario a su accion asignada
			if ((!ToolsHTML.isEmptyOrNull(request
					.getParameter("userponecomentario")))
					|| ((!ToolsHTML
							.isEmptyOrNull((String) getSessionObject("userponecomentario"))))) {
				byte firmo = Constants.permission;
				Object idaccion = null;
				String comentarios = "", evidencia = "";
				// en caso que pulse una accion para editar en la pag
				// accionestomar.jsp
				if (request.getAttribute("idaccion") != null) {
					idaccion = request.getAttribute("idaccion");
					// buscamos el comentario de esta accion a traves de su
					// idaccion y su firma es igual a 1
					// es decir que ha firmado
					Properties result = accionesPendientes(
							String.valueOf(user.getIdPerson()),
							idplanillasacop, idaccion.toString(), firmo);
					comentarios = result.getProperty("comentario");
					evidencia = result.getProperty("evidencia");
					if (!ToolsHTML.isEmptyOrNull(evidencia)) {
						// colocamos el link completo a la evidencia
						String evidenciaPrefix = idplanillasacop + "/"
								+ idaccion.toString() + "/"
								+ user.getNameUser() + "/" + user.getNameUser()
								+ "_";
						putAtributte("evidenciaPrefix", evidenciaPrefix);
					}
				}
				editar = "/editAccionesporpersona.do";
				putAtributte("comentario", comentarios.toString());
				putAtributte("evidencia", evidencia);
				// variable que se utiliza en accionestomarsacop.jsp
				// desahibilito el texto accion que me trae que tipo de accion a
				// seguir y habilito el texto
				// comentario para que el usuario pueda introducir un comentario
				putObjectSession("userponecomentario", "1");
			}

			Collection titulosaccionsacop = null;
			if ("/editAccionesporpersona.do".equalsIgnoreCase(editar)) {
				// si edito, me manda todos los titulos de las acciones de dicho
				// usuario para esa plantilla
				titulosaccionsacop = HandlerProcesosSacop
						.getPlanillasacopaccionFilterUser((String) request
								.getAttribute("idplanillasacop"), String
								.valueOf(user.getIdPerson()));
			} else {
				// edito todas las acciones existentes de la planilla
				titulosaccionsacop = HandlerProcesosSacop
						.getPlanillasacopaccion((String) request
								.getAttribute("idplanillasacop"));
			}
			boolean showCharge = false;
			removeObjectSession("showCharge");
			String cargosacop = String.valueOf(HandlerParameters.PARAMETROS.getCargosacop());
			if (cargosacop.equalsIgnoreCase(Constants.typeNumByLocationVacio)
					|| (ToolsHTML.isEmptyOrNull(cargosacop))) {
				showCharge = true;
			}
			// agarra los usuarios para la solicitud de la planilla sacop
			// esta session existe (editTypeDoc) siempre y cuando este editando
			// la forma
			PlantillaAccion pusuariosEscojidos = (PlantillaAccion) getSessionObject("editTypeDoc");
			removeAttribute("usuariosEscojidos");
			boolean usuariosSinEscojer = true;
			Collection usuarios = null;
			// en caso que este editando la forma
			if (pusuariosEscojidos != null) {
				usuarios = HandlerDBUser.getAllUsersFilterAccion(
						pusuariosEscojidos.getResponsables(),
						usuariosSinEscojer, showCharge);
				if (!ToolsHTML.isEmptyOrNull(pusuariosEscojidos
						.getResponsables())) {
					usuariosSinEscojer = false;
					Collection usuariosEscojidos = HandlerDBUser
							.getAllUsersFilterAccion(
									pusuariosEscojidos.getResponsables(),
									usuariosSinEscojer, showCharge);
					putAtributte("usuariosEscojidos", usuariosEscojidos);
				}
			} else {
				usuarios = HandlerDBUser.getAllUsersFilter(null, showCharge);
			}

			putAtributte("usuarios", usuarios);
			removeObjectSession("dataTable");
			putObjectSession("dataTable", titulosaccionsacop);
			putObjectSession("sizeParam",
					String.valueOf(titulosaccionsacop.size()));
			putObjectSession("editManager", editar);
			putObjectSession("newManager", "/newAccionesTomarSacop.do");
			putObjectSession("editGeneral", "/editGnrlAccionesTomarSacop.do");
			putObjectSession("loadManager", "LoadAccionesTomarSacop.do");
			putObjectSession("loadEditManager",
					"/loadAccionesTomarSacopEdit.do");
			putObjectSession("formEdit", "accionTomarSacop");
			String cmd = getCmd(request, false);
			PlantillaAccion forma = (PlantillaAccion) form;
			// TitulosPlanillasSacop forma = (TitulosPlanillasSacop)form;
			if (forma == null) {
				// System.out.println("Forma Nula........");
				form = new PlantillaAccion();
			}
			if (ToolsHTML.checkValue(cmd)) {
				((SuperActionForm) form).setCmd(cmd);
				request.setAttribute("cmd", cmd);
			} else {
				request.setAttribute("cmd", SuperActionForm.cmdLoad);
			}
			return goSucces();
		} catch (ApplicationExceptionChecked ae) {
			log.error(ae.getMessage());
			return goError(ae.getKeyError());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return goError();
	}

	/**
	 * 
	 * @param idPerson
	 * @param idPlanilla
	 * @param idplanillasacopaccion
	 * @param pendiente
	 * @return
	 */
	public static Properties accionesPendientes(String idPerson,
			String idPlanilla, String idplanillasacopaccion, byte pendiente) {

		Properties result = new Properties();

		try {
			StringBuilder sql = new StringBuilder(
					" SELECT p.firmo, p.comentario, p.evidencia from tbl_sacopaccionporpersona p,")
					.append("  tbl_planillasacopaccion pa,")
					.append("  tbl_planillasacop1 p1 where ")
					.append("  p1.idplanillasacop1=pa.idplanillasacop1 and ")
					.append("  pa.idplanillasacopaccion=p.idplanillasacopaccion and ")
					.append("  p.idperson=").append(idPerson)
					.append(" and ")
					// sql.append("  p.firmo=").append(pendiente).append(" and ");
					.append("  p1.idplanillasacop1=").append(idPlanilla)
					.append(" and ").append("  pa.idplanillasacopaccion=")
					.append(idplanillasacopaccion);
			// System.out.println("accionesPendiaentes="+sql.toString());
			result = JDBCUtil.doQueryOneRow(sql.toString(),Thread.currentThread().getStackTrace()[1].getMethodName());
		} catch (Exception e) {
			// System.out.println(e);
		}

		return result;
	}

	/**
	 * 
	 * @param idPerson
	 * @param idPlanilla
	 * @param pendiente
	 * @param swPorUsuario
	 * @return
	 */
	public static String accionesPendientes(String idPerson, String idPlanilla,
			byte pendiente, boolean swPorUsuario) {
		String retornar = "";
		try {
			StringBuilder sql = new StringBuilder(
					" from tbl_sacopaccionporpersona p,")
					.append("  tbl_planillasacopaccion pa,")
					.append("  tbl_planillasacop1 p1 where ")
					.append("  p1.idplanillasacop1=pa.idplanillasacop1 and ")
					.append("  pa.idplanillasacopaccion=p.idplanillasacopaccion and ")
					.append("  firmo=0 and ");
			if (swPorUsuario) {
				sql.append("  p.idperson=").append(idPerson).append(" and ");
			}
			// sql.append("  p.firmo=").append(pendiente).append(" and ");
			sql.append("  p1.idplanillasacop1=").append(idPlanilla);
			// System.out.println("accionesPendiaentes="+sql.toString());

			if (pendiente == 0) {
				retornar = HandlerDocuments.getField2("firmo", sql.toString());
			} else {
				retornar = HandlerDocuments.getField2("comentario",
						sql.toString());
			}
		} catch (Exception e) {
			// System.out.println(e);
		}

		// System.out.println("retornar="+retornar);
		return retornar;
	}
}
