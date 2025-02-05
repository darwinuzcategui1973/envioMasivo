package com.desige.webDocuments.sacop.actions;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.sql.BatchUpdateException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.upload.FormFile;

import com.desige.webDocuments.mail.forms.MailForm;
import com.desige.webDocuments.persistent.managers.HandlerDocuments;
import com.desige.webDocuments.persistent.managers.HandlerParameters;
import com.desige.webDocuments.persistent.managers.HandlerProcesosSacop;
import com.desige.webDocuments.persistent.managers.HandlerStruct;
import com.desige.webDocuments.persistent.managers.HandlerWorkFlows;
import com.desige.webDocuments.persistent.utils.JDBCUtil;
import com.desige.webDocuments.sacop.forms.Plantilla1BD;
import com.desige.webDocuments.sacop.forms.plantilla1;
import com.desige.webDocuments.utils.ApplicationExceptionChecked;
import com.desige.webDocuments.utils.Constants;
import com.desige.webDocuments.utils.DesigeConf;
import com.desige.webDocuments.utils.NumberSacop;
import com.desige.webDocuments.utils.ToolsHTML;
import com.desige.webDocuments.utils.beans.SuperAction;
import com.desige.webDocuments.utils.beans.SuperActionForm;
import com.focus.qweb.dao.PlanillaSacop1DAO;
import com.focus.qweb.dao.PlanillaSacop1EsqueletoDAO;
import com.focus.qweb.dao.SacopIntouchHijoDAO;
import com.focus.qweb.dao.SacopIntouchPadreDAO;
import com.focus.qweb.to.PlanillaSacop1EsqueletoTO;
import com.focus.qweb.to.PlanillaSacop1TO;
import com.focus.qweb.to.SacopIntouchHijoTO;
import com.focus.qweb.to.SacopIntouchPadreTO;
import com.focus.wonderware.actions.HandlerProcesosWonderWare;

import sun.jdbc.rowset.CachedRowSet;

/**
 * Created by IntelliJ IDEA.
 * User: Administrador
 * Date: 12/12/2005
 * Time: 10:09:46 PM
 * To change this template use File | Settings | File Templates.
 */

/**
 * Created by IntelliJ IDEA.
 * User: srodriguez
 * Date: 12/12/2005
 * Time: 04:31:08 PM
 * To change this template use File | Settings | File Templates.
 */

/**
 * Created by IntelliJ IDEA. User: srodriguez Date: 08/12/2005 Time: 03:10:41 PM To change this template use File | Settings | File Templates.
 */

public class CrearPlanillaSacop extends SuperAction {
	static Logger log = LoggerFactory.getLogger(CrearPlanillaSacop.class.getName());

	/**
	 * 
	 */
	public synchronized ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
		Locale defaultLocale = new Locale(DesigeConf.getProperty("language.Default"), DesigeConf.getProperty("country.Default"));
		ResourceBundle rb = ResourceBundle.getBundle("LoginBundle", defaultLocale);
		super.init(mapping, form, request, response);
		boolean swemitida = false;
		boolean swactualizandoborrador = false;
		boolean swPreconfiguradoWonderWare = false;

		// _______________________________WonderWare___________________________________________________________________//
		// si vienen tagname, quiere decir que es una sacop preconfoigurada por
		// WonderWare
		Object[] tagsnamesSeleccionados = (Object[]) getSessionObject("tagsnamesSeleccionados");
		if (tagsnamesSeleccionados != null) {
			swPreconfiguradoWonderWare = true;
		}
		removeObjectSession("tagsnamesSeleccionados");
		// _____________________________________________________________________________________________________//

		String edodelsacop = request.getParameter("edodelsacop") != null ? request.getParameter("edodelsacop") : "0";
		if (edodelsacop.equalsIgnoreCase("1")) { // emitida
			swemitida = true;
		}

		plantilla1 forma = (plantilla1) form;
		Plantilla1BD aux = new Plantilla1BD();

		// recuperamos algunos datos si ya existe
		if (forma.getId() != null && !forma.getId().trim().equals("")) {
			aux = new Plantilla1BD();
			try {
				CachedRowSet crs = null;
				crs = JDBCUtil.executeQuery(new StringBuffer("SELECT sacopnum, trackingSacop, numberTrackingSacop  FROM tbl_planillasacop1 WHERE idplanillasacop1=").append(forma.getId()), Thread.currentThread().getStackTrace()[1].getMethodName());
				if (crs.next()) {
					aux.setSacopnum(crs.getString("sacopnum"));
					aux.setTrackingSacop(crs.getString("trackingSacop"));
					aux.setNumberTrackingSacop(crs.getString("numberTrackingSacop"));
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		try {
			String cmd = request.getParameter("cmd");

			int idplanillasacop1 = 0;

			if (!ToolsHTML.isEmptyOrNull(forma.getId())) {
				swactualizandoborrador = true;
				idplanillasacop1 = Integer.parseInt(forma.getId());
			} else {
				// la planilla del esqueleto de wonderware o auditoria, no debe
				// alterar el id de
				// planillasSacops
				if (swPreconfiguradoWonderWare) {
					idplanillasacop1 = HandlerStruct.proximo("idplanillasacop1esqueleto", "idplanillasacop1esqueleto", "idplanillasacop1esqueleto");
				} else {
					idplanillasacop1 = HandlerStruct.proximo("idplanillasacop1", "idplanillasacop1", "idplanillasacop1");
				}

			}
			String v = request.getParameter("correcpreven") != null ? request.getParameter("correcpreven") : "0";
			Plantilla1BD formaBD = new Plantilla1BD();
			// HibernateUtil.saveOrUpdate(formaBD);

			StringBuffer requisitosaplicable = new StringBuffer();
			StringBuffer procesosafectados = new StringBuffer();
			StringBuffer solicitudinforma = new StringBuffer();
			if (forma.getNormsisoSelected() != null) {
				Object[] datos = forma.getNormsisoSelected();
				int pos = 0;
				for (int row = 0; row < datos.length; row++) {
					Object dato = datos[row];
					requisitosaplicable.append(dato.toString());
					if (row + 1 < datos.length) {
						requisitosaplicable.append(",");
					}
				}
				forma.setNormsisoSelected(null);
			}

			if (forma.getProceafectadosSelected() != null) {
				Object[] datos = forma.getProceafectadosSelected();
				int pos = 0;
				for (int row = 0; row < datos.length; row++) {
					Object dato = datos[row];
					procesosafectados.append(dato.toString());
					if (row + 1 < datos.length) {
						procesosafectados.append(",");
					}
				}
				forma.setProceafectadosSelected(null);
			}
			if (forma.getUsersSelected() != null) {
				Object[] datos = forma.getUsersSelected();
				int pos = 0;
				for (int row = 0; row < datos.length; row++) {
					Object dato = datos[row];
					solicitudinforma.append(HandlerDocuments.getField("idperson", "person", "nameUser", dato.toString(), "=", 1,Thread.currentThread().getStackTrace()[1].getMethodName()));
					if (row + 1 < datos.length) {
						solicitudinforma.append(",");
					}
				}
			}

			String correcpreven = request.getParameter("correcpreven") != null ? request.getParameter("correcpreven") : "0";
			String emisorName = request.getParameter("user") != null ? request.getParameter("user") : "0";
			String nomEmisor = request.getParameter("nombres") != null ? request.getParameter("nombres") : "";
			String cargoEmisor = request.getParameter("cargo") != null ? request.getParameter("cargo") : "";
			String respbleareaName = request.getParameter("responsable") != null ? request.getParameter("responsable") : "0";

			String emisor = null;
			String respblearea = null;
			String CorreoEmisor = null;
			String CorreoRespblearea = null;
			String nomRespblearea = null;
			// String idAreaInCampoUsuarioNotificado=null;
			String idCargoInCampoUsuarioNotificado = null;
			String values[] = HandlerDocuments.getFields(new String[] { "idperson", "email" }, "person", "nameUser", "'" + String.valueOf(emisorName) + "'");

			if (values != null) {
				emisor = values[0];
				CorreoEmisor = values[1];
			}
			values = null;
			values = HandlerDocuments.getFields(new String[] { "idperson", "email", "nombres", "apellidos", "cargo" }, "person", "nameUser",
					"'" + String.valueOf(respbleareaName) + "'");
			if (values != null) {
				respblearea = values[0];
				CorreoRespblearea = values[1];
				nomRespblearea = values[2] + " " + values[3];
				idCargoInCampoUsuarioNotificado = values[4];
			}
			String estado = request.getParameter("edodelsacop") != null ? request.getParameter("edodelsacop") : "0";
			String origensacop = request.getParameter("origensacop") != null ? request.getParameter("origensacop") : "0";
			String fechaemision = request.getParameter("fechaEmision") != null ? request.getParameter("fechaEmision") : "0";
			String fechasacops1 = request.getParameter("fechaSacops1") != null ? request.getParameter("fechaSacops1") : "0";
			String fechaCulminar = request.getParameter("fechaculminar") != null ? request.getParameter("fechaculminar") : "0";
			String descripcion = request.getParameter("descripcion") != null ? request.getParameter("descripcion") : "0";
			String causasnoconformidad = request.getParameter("causasnoconformidad") != null ? request.getParameter("causasnoconformidad") : "0";
			String accionrecomendada = request.getParameter("accionrecomendada") != null ? request.getParameter("accionrecomendada") : "0";
			String sacopRelacionados = request.getParameter("sacop_relacionadas") != null ? request.getParameter("sacop_relacionadas") : "0";
			String noconformidades = request.getParameter("noconformidades") != null ? request.getParameter("noconformidades") : "";
			String noconformidadesref = request.getParameter("noconformidadesref") != null ? request.getParameter("noconformidadesref") : "";
			String idClasificacion = request.getParameter("clasificacionSACOP") != null ? request.getParameter("clasificacionSACOP") : "0";
			String idDocumentRelated = request.getParameter("idDocumentRelated") != null ? request.getParameter("idDocumentRelated") : "0";
			String usuarioSacops1 = request.getParameter("usuarioSacops1") != null ? request.getParameter("usuarioSacops1") : null;
			
			getUserSession();

			// define el prefijo de la numeracion de la sacop
			String siglasNum = Constants.ACTION_TYPE[Integer.parseInt(correcpreven)];

			String numByLocation = HandlerParameters.PARAMETROS.getLengthDocNumber();
			NumberSacop numberSacop = ToolsHTML.numberSacop();
			String idNumero = "";
			// si no es borrador, mandamos a ingresar el numero de la sacop,
			// en caso que sea borrador,
			// no asignamos numero
			// System.out.println(" =============================1=================================================== ");
			if (!numberSacop.isRenumerar()) {
				if (swemitida) {
					if (swPreconfiguradoWonderWare) {
						// System.out.println(" =============================2=================================================== ");
						// njumero para las sacops preconfiguradas
						long numerosacopintouch = HandlerStruct.proximo("numerosacopintouch", "numerosacopintouch", "numerosacopintouch");
						idNumero = String.valueOf(numerosacopintouch);
					} else {
						// System.out.println(" =============================3=================================================== ");
						// este numero es de la sacop verdadera
						long numerosacop = HandlerStruct.proximo("numerosacop", "numerosacop", "numerosacop");
						idNumero = String.valueOf(numerosacop);
					}

				}
			}

			// System.out.println(idNumero +
			// "=idNumero =============================4=================================================== ");
			String numeroSacop = null;
			if(aux.getTrackingSacop()!=null && aux.getTrackingSacop().equals("1")) {
				numeroSacop = numSacopDoTracking(aux.getNumberTrackingSacop()); // correlativo
			} else {
				if (swemitida) {
					numeroSacop = numSacopDo(siglasNum, idNumero, numByLocation, respblearea, numberSacop, true);
				} else {
					// formaBD.setSacopnum(aux.getSacopnum());
					numeroSacop = numSacopDo(siglasNum, "0", numByLocation, respblearea, numberSacop, false);
				}
			}
			formaBD.setSacopnum(numeroSacop);
			formaBD.setIdplanillasacop1(idplanillasacop1);
			if (!ToolsHTML.isEmptyOrNull(idCargoInCampoUsuarioNotificado)) {
				long idcargo = idCargoInCampoUsuarioNotificado != null ? Long.parseLong(idCargoInCampoUsuarioNotificado) : (long) 0;
				formaBD.setUsernotificado(idcargo);
			} else {
				formaBD.setUsernotificado((long) 0);
			}

			formaBD.setEmisor(Long.parseLong(emisor.toString()));
			if (!ToolsHTML.isEmptyOrNull(respblearea)) {
				if (ToolsHTML.isNumeric(respblearea)) {
					formaBD.setRespblearea(Long.parseLong(respblearea));
				}
			}

			java.util.Date fechaWhenDiscovered = null;
			java.util.Date fechaEmitir = new java.util.Date();
			if (!ToolsHTML.isEmptyOrNull(request.getParameter("fechaWhenDiscovered"))) {
				if (request.getParameter("fechaWhenDiscovered").indexOf("-") > 0) {
					DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
					fechaWhenDiscovered = df.parse(request.getParameter("fechaWhenDiscovered"));
				} else {
					DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
					fechaWhenDiscovered = df.parse(request.getParameter("fechaWhenDiscovered"));
				}
			}

			formaBD.setOrigensacop(Integer.parseInt(origensacop));
			formaBD.setFechaemision(fechaEmitir);
			
			if(fechasacops1!=null && !fechasacops1.equals("")) {
				DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
				formaBD.setFechasacops1(df.parse(fechasacops1));
			}
			
			formaBD.setFechaculminar(fechaEmitir.toString());
			formaBD.setRequisitosaplicable(requisitosaplicable.toString());
			formaBD.setProcesosafectados(procesosafectados.toString());
			formaBD.setSolicitudinforma(solicitudinforma.toString());
			formaBD.setDescripcion(descripcion);
			formaBD.setCausasnoconformidad(causasnoconformidad);
			formaBD.setAccionesrecomendadas(accionrecomendada);
			formaBD.setActive((byte) 1);
			formaBD.setCorrecpreven(correcpreven);
			formaBD.setSacop_relacionadas(sacopRelacionados);
			formaBD.setNoconformidades(noconformidades);
			formaBD.setNoconformidadesref(noconformidadesref);
			formaBD.setIdClasificacion(Integer.parseInt(idClasificacion));
			formaBD.setFechaWhenDiscovered(fechaWhenDiscovered);
			formaBD.setIdDocumentRelated(ToolsHTML.parseInt(idDocumentRelated,0));
			formaBD.setUsuarioSacops1(ToolsHTML.parseInt(usuarioSacops1,0));
			formaBD.setTrackingSacop(aux.getTrackingSacop());
			formaBD.setNumberTrackingSacop(aux.getNumberTrackingSacop());
			formaBD.setIdRegisterGenerated(aux.getIdRegisterGenerated());
			
			// traemos este procedimiento para ingresar un archivo
			boolean swBorrarArchivo = true;
			// String noseusa = "";
			String noseusa = HandlerProcesosSacop.getSacopPlanillaFolder(String.valueOf(formaBD.getIdplanillasacop1()));

			// String noseusa = ToolsHTML.getRepository() + File.separator +
			// "evidenciasSacops" + File.separator +
			// String.valueOf(formaBD.getIdplanillasacop1());
			procesaFicheros(request, forma, swBorrarArchivo, noseusa);

			// Blob blob = null;
			// File fichero = null;
			if ((forma.getFileNameFisico() != null) && (forma.getContextType() != null) && (forma.getPath() != null)) {
				formaBD.setNameFile(forma.getFileNameFisico());
				formaBD.setContentType(forma.getContextType());

				// forma de almacenar el archivo para sqlserver
				// blob = Hibernate.createBlob(fis);
				// formaBD.setData(blob);
				// nueva forma de almacenar el archivo
				// fichero = new File(forma.getPath() + File.separator
				// + forma.getFileNameFisico());
				// FileInputStream fis = new FileInputStream(fichero);
				// ByteArrayOutputStream arrayOutputStream = new
				// ByteArrayOutputStream();
				// byte buffer[] = new byte[4096];
				// int bytesRead = 0;
				// while ((bytesRead = fis.read(buffer)) != -1) {
				// arrayOutputStream.write(buffer, 0, bytesRead);
				// }
				// arrayOutputStream.close();
				//
				// formaBD.setData(arrayOutputStream.toByteArray());

				// StringBuffer data = new StringBuffer();
				// int c;
				// while ((c = fis.read()) != -1) {
				// data.append((char) c);
				// }
				// fis.close();
				// formaBD.setData(data.toString().getBytes());
			}

			StringBuffer correcpreventxt = new StringBuffer("");
			if (LoadSacop.Correctiva.equalsIgnoreCase(correcpreven)) {
				correcpreventxt.append(rb.getString("scp.ac"));
			} else if (LoadSacop.Preventiva.equalsIgnoreCase(correcpreven)) {
				correcpreventxt.append(rb.getString("scp.ap"));
			}
			// if (!swPreconfiguradoWonderWare){
			if (swemitida) {
				formaBD.setEstado(Integer.parseInt(LoadSacop.edoEmitida));
			} else {
				formaBD.setEstado(Integer.parseInt(LoadSacop.edoBorrador));
			}
			// }else{
			// formaBD.setEstado(Integer.parseInt(LoadSacop.edoWonderWare));
			// }

			// ____________________________________TRANSACCION
			// BD__INICIO____________________________________________________________________________
			boolean swVerificarSiActualizaOInsertaequaltrue = false;
			// ES EMITIR...PARA INSERTAR EMN SACOP O
			// ESQUELETO.....................................

			if (!swactualizandoborrador) {

				// actualiamos tabla esqueleto , que servira para auditorias
				// tambien
				if (swPreconfiguradoWonderWare) {
					// verificamos si esqueleto va a ser borrador o emitida,
					// segun configuracion.
					// Realmente esto se valida en
					// HiloChecheaSacop_Intouch.DisparadaPorAlarmaSacop_Intouch(tagnames)
					String estadoEsqueletoConfiguradoSacop = request.getParameter("estadoEsqueletoConfiguradoSacop").toString().trim();
					if (!ToolsHTML.isEmptyOrNull(estadoEsqueletoConfiguradoSacop)) {
						if ("1".equalsIgnoreCase(estadoEsqueletoConfiguradoSacop)) {
							formaBD.setEstado(Integer.parseInt(LoadSacop.edoEmitida));
						} else {
							formaBD.setEstado(Integer.parseInt(LoadSacop.edoBorrador));
						}
					}

					// llenamos el esqueleto de wonderware
					PlanillaSacop1EsqueletoDAO oPlanillaSacop1EsqueletoDAO = new PlanillaSacop1EsqueletoDAO();
					PlanillaSacop1EsqueletoTO oPlanillaSacop1EsqueletoTO = new PlanillaSacop1EsqueletoTO();

					oPlanillaSacop1EsqueletoTO = new PlanillaSacop1EsqueletoTO(formaBD);
					
					oPlanillaSacop1EsqueletoDAO.insertar(oPlanillaSacop1EsqueletoTO);

					// este sw me indica que si pasa por
					// swPreconfiguradoWonderWare, me va esa a
					// insertar las tablas padre y el hijo de intouch

					swVerificarSiActualizaOInsertaequaltrue = true;
					// _______________________________WonderWare___________________________________________________________________//
					// si vienen tagname, quiere decir que es una sacop
					// preconfoigurada por
					// WonderWare
					// y se usa el swVerificarSiVieneDeWonderware
					ActualizaSacop_Intouch(formaBD, tagsnamesSeleccionados, swVerificarSiActualizaOInsertaequaltrue);
				} else {
					// emite la sacop, de manera normal primitiva sin intouch ni
					// auditoria
					// formaBD.setDescripcion(formaBD.getDescripcion()==null ||
					// formaBD.getDescripcion().trim().equals("")?null:formaBD.getDescripcion());
					// formaBD.setSacop_relacionadas(formaBD.getSacop_relacionadas()==null
					// ||
					// formaBD.getSacop_relacionadas().trim().equals("")?null:formaBD.getSacop_relacionadas());
					// formaBD.setNoconformidades(formaBD.getNoconformidades()==null
					// ||
					// formaBD.getNoconformidades().trim().equals("")?null:formaBD.getNoconformidades());
					// formaBD.setNoconformidadesref(formaBD.getNoconformidadesref()==null
					// ||
					// formaBD.getNoconformidadesref().trim().equals("")?null:formaBD.getNoconformidadesref());
					// byte[] data = new byte[0];
					// formaBD.setData(data);

					
					PlanillaSacop1DAO oPlanillaSacop1DAO = new PlanillaSacop1DAO();
					PlanillaSacop1TO oPlanillaSacop1TO = new PlanillaSacop1TO(formaBD);
					
					oPlanillaSacop1DAO.insertar(oPlanillaSacop1TO);
				}

			}
			// ES BORADOR O ACTUALIZAR PARA ESQUELETO
			// WONDERWARE........................................
			else {
				// actualiamos tabla wonderware, que servira para auditorias
				// tambien
				if (swPreconfiguradoWonderWare) {
					// actualiza el esqueleto de wonderware

					// actualizamos sacop-intouch
					// configuracoion del edo del esqueleto (emitido o
					// borrador.)
					String estadoEsqueletoConfiguradoSacop = request.getParameter("estadoEsqueletoConfiguradoSacop").toString().trim();
					if (!ToolsHTML.isEmptyOrNull(estadoEsqueletoConfiguradoSacop)) {
						formaBD.setEstado(Byte.parseByte(estadoEsqueletoConfiguradoSacop));
					}

					// en caso que no este deshabilitado la configuracion del
					// esqueleto
					// esto lo sabemos si esta varfiable Deshabilitar_o_Eliminar
					// es diferente a 1
					// String
					// Deshabilitar_o_Eliminar=request.getParameter("deshabilitar_Sacop_Intouch");

					// en caso que se quiera deshabilitar o eliminar logicamente
					// una sacop
					// preconfiurada

					String continuar_Sacop_Intouch = request.getParameter("continuar_Sacop_Intouch");
					String deshabilitar_Sacop_Intouch = request.getParameter("deshabilitar_Sacop_Intouch");
					String Deshabilitar_o_Eliminar = request.getParameter("Deshabilitar_o_Eliminar");

					if (!"1".equalsIgnoreCase(Deshabilitar_o_Eliminar)) {

						PlanillaSacop1EsqueletoTO oPlanillaSacop1EsqueletoTO = new PlanillaSacop1EsqueletoTO(formaBD);
						PlanillaSacop1EsqueletoDAO oPlanillaSacop1EsqueletoDAO = new PlanillaSacop1EsqueletoDAO();
						
						oPlanillaSacop1EsqueletoDAO.actualizar(oPlanillaSacop1EsqueletoTO);
						
						// actualizamos el esqueleto de wonderware sacop
						// este sw me indica que si pasa por
						// swPreconfiguradoWonderWare, me va esa a
						// actualizar las tablas padre y el hijo de intouch
						swVerificarSiActualizaOInsertaequaltrue = false;
						// si vienen tagname, quiere decir que es una sacop
						// preconfoigurada por
						// WonderWare
						// y se usa el swVerificarSiVieneDeWonderware
						ActualizaSacop_Intouch(formaBD, tagsnamesSeleccionados, swVerificarSiActualizaOInsertaequaltrue);

					} // else{
					HandlerProcesosWonderWare handlerProcesosWonderWare = new HandlerProcesosWonderWare();
					handlerProcesosWonderWare.eliminamosSaco_Intouch(formaBD.getIdplanillasacop1(), continuar_Sacop_Intouch, deshabilitar_Sacop_Intouch);
					// }

					// }
				} else {
					// actualiza el borrador sacop de manera normal primitiva
					// sin intouch ni
					// auditoria
					// Por si acaso es un borrador de una emitida de una
					// sacop_preconfigurada
					// se guarda el id
					String idplanillasacop1esqueleto = request.getParameter("idplanillasacop1esqueleto") != null
							? request.getParameter("idplanillasacop1esqueleto") : "-1";
					formaBD.setIdplanillasacop1esqueleto(Long.parseLong(idplanillasacop1esqueleto));
					// ______________________________________________________________________________//

					PlanillaSacop1DAO oPlanillaSacop1DAO = new PlanillaSacop1DAO();
					PlanillaSacop1TO oPlanillaSacop1TO = new PlanillaSacop1TO(formaBD);
					
					oPlanillaSacop1DAO.actualizar(oPlanillaSacop1TO);
					
				}

			}
			// ____________________________________TRANSACCION
			// BD__FIN____________________________________________________________________________

			// notificaciones
			StringBuffer comentarios = new StringBuffer("");
			comentarios.append(rb.getString("scp.mailc1")).append(" ").append(correcpreventxt.toString());
			comentarios.append("<br>").append(rb.getString("scp.mailc2")).append(" ").append(correcpreventxt.toString());
			comentarios.append("<br>").append(rb.getString("scp.mailc3")).append(" ").append(nomEmisor);
			comentarios.append(rb.getString("scp.mailc4")).append(" ").append(cargoEmisor);
			comentarios.append("<br>").append(rb.getString("scp.mailc5")).append(" ").append(nomRespblearea);

			Hashtable CorreosYaEnviados = new Hashtable();
			CorreosYaEnviados.put(CorreoEmisor, "enviado");

			if (!ToolsHTML.isEmptyOrNull(CorreoRespblearea)) {
				CorreosYaEnviados.put(CorreoRespblearea, "enviado");
			}

			// mandamos un correo a los usuarios que se les informa
			if (forma.getUsersSelected() != null) {
				Object[] datos = forma.getUsersSelected();
				int pos = 0;
				String emailInformado = null;
				String nombres = null;
				StringBuffer emailInformadoCad = new StringBuffer();
				StringBuffer nombresc = new StringBuffer();
				MailForm formaMail = new MailForm();
				for (int row = 0; row < datos.length; row++) {
					Object dato = datos[row];
					values = null;
					values = HandlerDocuments.getFields(new String[] { "email", "nombres", "apellidos" }, "person", "nameUser",
							"'" + String.valueOf(dato.toString()) + "'");
					if (values != null) {
						emailInformado = values[0];
						nombres = values[1] + " " + values[2];
					}

					if (!CorreosYaEnviados.containsKey(dato.toString())) {
						CorreosYaEnviados.put(dato.toString(), "enviado");
						emailInformadoCad.append(emailInformado.toString()).append(";");
						nombresc.append(nombres).append(",");
					}
				}
				// leagregamos el correo emisor y Responsable del Proceso
				emailInformadoCad.append(CorreoEmisor).append(";");
				emailInformadoCad.append(CorreoRespblearea);

				// si no es preconfigurada wonderware y
				// si la sacop se emitio, mandamos correos
				if ((swemitida) && ((!swPreconfiguradoWonderWare))) {
					HandlerWorkFlows.notifiedUsers(correcpreventxt + " " + numeroSacop, rb.getString("mail.nameUser"),
							HandlerParameters.PARAMETROS.getMailAccount(), emailInformadoCad.toString(),
							nombresc.substring(0, nombresc.lastIndexOf(",")) + "<br>" + comentarios.toString());
				}
				// borramos la variable que contiene los mails enviados.
				Enumeration enume = CorreosYaEnviados.keys();
				while (enume.hasMoreElements()) {
					String id = (String) enume.nextElement();
					CorreosYaEnviados.remove(id);
				}
				CorreosYaEnviados = null;
				forma.setUsersSelected(null);
			}
			removeAttribute("sacoplantilla");

			if (swPreconfiguradoWonderWare) {
				ActionForward LoadSacop_Intouch = new ActionForward("/loadsacop_intouch.do");
				return LoadSacop_Intouch;
			} else {
				return goSucces();
			}
		} catch (ApplicationExceptionChecked ye) {
			log.error(ye.getMessage());
		} catch (BatchUpdateException e) {
			log.error("Excepcion anidada de tipo batch:", e.getNextException());
		} catch (Exception e) {
			e.printStackTrace();
			//log.error(e.getMessage());
		}

		return goError();
	}

	/**
	 * 
	 * @param numberSacop
	 * @param siglas
	 * @param area
	 * @param fecha
	 * @param largo
	 * @return
	 */
	public static String proximoNumero(NumberSacop numberSacop, String siglas, String area, Calendar fecha, String largo) {
		CachedRowSet crs = null;
		try {
			int nLargo = Integer.parseInt(largo);
			StringBuffer sb = new StringBuffer();
			StringBuffer order = new StringBuffer();

			// query
			StringBuffer where = new StringBuffer();
			where.append("WHERE sacopnum like '").append(siglas).append("-%' ");
			if (numberSacop.isPrefijoSistema() || numberSacop.isRenumerarArea() || numberSacop.isRenumerarAno() || numberSacop.isRenumerarMes()
					|| numberSacop.isRenumerarDia()) {
				if (area != null && !area.trim().equals("")) {
					where.append("and sacopnum like '%-").append(area).append("-%' ");
				}
				switch (Constants.MANEJADOR_ACTUAL) {
				case Constants.MANEJADOR_MSSQL:
					if (numberSacop.isRenumerarAno() || numberSacop.isRenumerarMes() || numberSacop.isRenumerarDia()) {
						where.append("and DATEPART(YYYY,fechaemision)='").append(fecha.get(Calendar.YEAR)).append("' ");
						if (numberSacop.isRenumerarMes() || numberSacop.isRenumerarDia()) {
							where.append("and RIGHT('0'+CONVERT(VARCHAR,DATEPART(MM,fechaemision)),2)='").append(zero(((fecha.get(Calendar.MONTH) + 1)), 2))
									.append("' ");
							if (numberSacop.isRenumerarDia()) {
								where.append("and RIGHT('0'+CONVERT(VARCHAR,DATEPART(DD,fechaemision)),2)='").append(zero(fecha.get(Calendar.DATE), 2))
										.append("' ");
							}
						}
					}
					sb.append("SELECT TOP 1 RIGHT(sacopnum,").append(nLargo).append(") As numero from tbl_planillasacop1 ");
					sb.append(where);
					sb.append("ORDER BY RIGHT(sacopnum,").append(nLargo).append(") desc ");
					break;
				case Constants.MANEJADOR_POSTGRES:
					if (numberSacop.isRenumerarAno() || numberSacop.isRenumerarMes() || numberSacop.isRenumerarDia()) {
						where.append("and to_char(fechaemision,'YYYY')='").append(fecha.get(Calendar.YEAR)).append("' ");
						if (numberSacop.isRenumerarMes() || numberSacop.isRenumerarDia()) {
							where.append("and to_char(fechaemision,'MM')='").append(zero((fecha.get(Calendar.MONTH) + 1), 2)).append("' ");
							if (numberSacop.isRenumerarDia()) {
								where.append("and to_char(fechaemision,'DD')='").append(zero(fecha.get(Calendar.DATE), 2)).append("' ");
							}
						}
					}
					sb.append("SELECT substring(sacopnum from (char_length(sacopnum) - ").append((nLargo - 1)).append(") for ").append(nLargo)
							.append(") As numero from tbl_planillasacop1 ");
					sb.append(where);
					sb.append("ORDER BY substring(sacopnum from (char_length(sacopnum) - ").append((nLargo - 1)).append(") for ").append(nLargo)
							.append(") desc ");
					sb.append("limit 1");
					break;
				case Constants.MANEJADOR_MYSQL:
					if (numberSacop.isRenumerarAno() || numberSacop.isRenumerarMes() || numberSacop.isRenumerarDia()) {
						where.append("and date_format(fechaemision,'%Y')='").append(fecha.get(Calendar.YEAR)).append("' ");
						if (numberSacop.isRenumerarMes() || numberSacop.isRenumerarDia()) {
							where.append("and date_format(fechaemision,'%m')='").append(zero((fecha.get(Calendar.MONTH) + 1), 2)).append("' ");
							if (numberSacop.isRenumerarDia()) {
								where.append("and date_format(fechaemision,'%d')='").append(zero(fecha.get(Calendar.DATE), 2)).append("' ");
							}
						}
					}
					sb.append("SELECT substring(sacopnum from (char_length(sacopnum) - ").append((nLargo - 1)).append(") for ").append(nLargo)
							.append(") As numero from tbl_planillasacop1 ");
					sb.append(where);
					sb.append("ORDER BY substring(sacopnum from (char_length(sacopnum) - ").append((nLargo - 1)).append(") for ").append(nLargo)
							.append(") desc ");
					sb.append("limit 1");
					break;
				}
			}

			crs = JDBCUtil.executeQuery(sb, Thread.currentThread().getStackTrace()[1].getMethodName());
			if (crs.next()) {
				String cad = crs.getString("numero");
				return String.valueOf(Integer.parseInt(cad) + 1);
			} else {
				return "1";
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 
	 * @param siglas
	 * @param num
	 * @param largo
	 * @param responsabeDeArea
	 * @param numberSacop
	 * @param proximo
	 * @return
	 */
	public static synchronized String numSacopDo(String siglas, String num, String largo, String responsabeDeArea, NumberSacop numberSacop, boolean proximo) {
		boolean guion = false;
		Calendar fecha = Calendar.getInstance();
		String prefijo = "";

		StringBuilder numero = new StringBuilder("").append(siglas);

		if (numberSacop.isPrefijoArea()) { // colocamos el prefijo del area
			try {
				StringBuffer sb = new StringBuffer(2048).append("SELECT prefijo FROM person a, tbl_area b WHERE a.idArea = b.idArea AND a.idPerson=")
						.append(responsabeDeArea);
				CachedRowSet crs = JDBCUtil.executeQuery(sb, Thread.currentThread().getStackTrace()[1].getMethodName());
				if (crs.next()) {
					prefijo = crs.getString("prefijo");
					if (prefijo != null && !prefijo.trim().equals("")) {
						numero.append("-");
						numero.append(prefijo);
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		if (numberSacop.isPrefijoAno()) { // colocamos el prefijo por a�o
			numero.append(guion ? "" : "-");
			guion = true;
			numero.append(fecha.get(Calendar.YEAR));
		}
		if (numberSacop.isPrefijoMes()) { // colocamos el prefijo por mes
			numero.append(guion ? "" : "-");
			guion = true;
			numero.append(zero(String.valueOf((fecha.get(Calendar.MONTH) + 1)), 2));
		}
		if (numberSacop.isPrefijoDia()) { // colocamos el prefijo por dia
			numero.append(guion ? "" : "-");
			guion = true;
			numero.append(zero(String.valueOf(fecha.get(Calendar.DATE)), 2));
		}
		if (proximo && numberSacop.isRenumerar()) {
			num = proximoNumero(numberSacop, siglas, prefijo, fecha, largo);
		}

		numero.append("-");
		numero.append(zero(num, Integer.parseInt(largo)));
		return numero.toString();
	}
	
	public static synchronized String numSacopDoTracking(String numberSacopAsociate) {
		String number = "";
		StringBuilder query = null;
		ArrayList<String> parametros = new ArrayList<String>();
		
		parametros.add(numberSacopAsociate);
		query = new StringBuilder("select COUNT(*) from tbl_planillasacop1 where trackingSacop=1 and numberTrackingSacop=? and estado!=0");
		
		try {
			CachedRowSet crs = JDBCUtil.executeQuery(query, parametros, "numSacopDoTracking()");
			
			if(crs!=null && crs.next()) {
				int num = crs.getInt(1);
				number = numberSacopAsociate.concat("-").concat(zero(String.valueOf(num+1),4));
			} else {
				number = numberSacopAsociate.concat("-0001");
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return number;
	}
	

	// private static String numSacopDo(String siglas, String num, String
	// areaAfectada){
	public static String numSacopDo(String siglas, String num, String largo) {

		NumberSacop numberSacop = ToolsHTML.numberSacop();

		StringBuffer numero = new StringBuffer("");
		numero.append(siglas);
		numero.append("-");

		if (numberSacop.isPrefijoArea()) { // colocamos el prefijo del area

		}

		numero.append(zero(num, Integer.parseInt(largo)));
		return numero.toString();
	}

	/**
	 * 
	 * @param cad
	 * @param largo
	 * @return
	 */
	public static String zero(int cad, int largo) {
		return zero(String.valueOf(cad), largo);
	}

	/**
	 * 
	 * @param cad
	 * @param largo
	 * @return
	 */
	public static String zero(String cad, int largo) {
		if (cad.length() < largo) {
			cad = "0".concat(cad);
			cad = zero(cad, largo);
		}
		return cad;
	}

	/**
	 * 
	 * @param forma
	 * @param request
	 * @return
	 * @throws ApplicationExceptionChecked
	 */
	private static boolean processCmd(plantilla1 forma, HttpServletRequest request) throws ApplicationExceptionChecked {
		// System.out.println("forma = " + forma.getCmd());
		boolean resp = false;
		StringBuffer mensaje = null;
		ResourceBundle rb = ToolsHTML.getBundle(request);
		if (forma.getCmd().equalsIgnoreCase(SuperActionForm.cmdInsert)) {
			// System.out.println("Insertar Registro...");
			try {
				// resp = HandlerProcesosSacop.insert(forma);
			} catch (Exception e) {
				e.printStackTrace();
			}
			if (resp) {
				request.setAttribute("info", rb.getString("app.editOk"));
				forma.cleanForm();
				return true;
			} else {
				mensaje = new StringBuffer(rb.getString("app.notEdit"));
				// mensaje.append(" ").append(HandlerTypeDoc.getMensaje());
			}
		} else {
			if (forma.getCmd().equalsIgnoreCase(SuperActionForm.cmdEdit)) {
				// System.out.println("Editando Registro...");
				PlanillaSacop1DAO oPlanillaSacop1DAO = new PlanillaSacop1DAO();
				PlanillaSacop1TO oPlanillaSacop1TO = new PlanillaSacop1TO(forma);

				try {
					oPlanillaSacop1DAO.actualizar(oPlanillaSacop1TO);
					request.setAttribute("info", rb.getString("app.editOk"));
					return true;
				} catch (Exception e) {
					e.printStackTrace();
					return false;
				}

				// if (HandlerProcesosSacop.edit(forma)){
				// request.setAttribute("info",rb.getString("app.editOk"));
				// return true;
				// }else{
				// mensaje = new StringBuffer(rb.getString("app.notEdit"));
				// mensaje.append(" ").append(HandlerTypeDoc.getMensaje());
				// }
			} else {
				if (forma.getCmd().equalsIgnoreCase(SuperActionForm.cmdDelete)) {
					// System.out.println("Elimando registro....");
					/*
					 * if (HandlerProcesosSacop.delete(forma)){ forma.cleanForm(); request.setAttribute("info",rb.getString("app.delete")); } else{ mensaje =
					 * new StringBuffer(rb.getString("app.notDelete")); }
					 */
				}
			}
		}
		if (mensaje != null) {
			// System.out.println("mensaje = " + mensaje);
			request.setAttribute("info", mensaje.toString());
		}
		return false;
	}

	/**
	 * 
	 * @param req
	 * @param forma
	 * @param swBorrarArchivo
	 * @param pathAfuera
	 * @return
	 */
	public boolean procesaFicheros(HttpServletRequest req, plantilla1 forma, boolean swBorrarArchivo, String pathAfuera) {
		boolean resp = false;
		try {
			String path = (!ToolsHTML.isEmptyOrNull(pathAfuera)) ? pathAfuera : HandlerProcesosSacop.getSacopPath();
			forma.setPath(path);

			if (forma.getNameFile() != null && forma.getNameFile().getFileSize() > 0) {
				FormFile file = forma.getNameFile();
				String fileName = file.getFileName();
				String contentType = file.getContentType();

				// Crea subdirectorios si es el caso
				File dir = new File(path);
				if (!dir.exists()) {
					try {
						dir.mkdirs();
					} catch (SecurityException se) {
						se.printStackTrace();
					}
				}

				InputStream inStream = file.getInputStream();
				String destino = path;
				if (!destino.endsWith(File.separator)) {
					destino = destino.concat(File.separator);
				}
				destino = destino.concat(fileName);
				FileOutputStream outStream = new FileOutputStream(destino);
				IOUtils.copy(inStream, outStream);
				IOUtils.closeQuietly(outStream);

				forma.setContextType(contentType);
				forma.setFileNameFisico(fileName);
				if (swBorrarArchivo) {
					file.destroy();
				}
			}

			resp = true;

		} catch (Exception e) {
			e.printStackTrace();
			resp = true;
		}
		return resp;
	}

	/**
	 * 
	 * @param formaBD
	 * @param tagsnamesSeleccionados
	 * @param swInserta
	 */
	private void ActualizaSacop_Intouch(Plantilla1BD formaBD, Object[] tagsnamesSeleccionados, boolean swInserta) {
		long idtagname = 0;
		HandlerProcesosWonderWare handlerProcesosWonderWare = new HandlerProcesosWonderWare();
		if (swInserta) {
			// cargamos el padre del sacop_intouch
			SacopIntouchPadreDAO oSacopIntouchPadreDAO = new SacopIntouchPadreDAO();
			SacopIntouchPadreTO oSacopIPTO = new SacopIntouchPadreTO();

			oSacopIPTO.setIdSacopIntouchPadre(null);
			oSacopIPTO.setIdPlanillaSacop1(String.valueOf(formaBD.getIdplanillasacop1()));
			oSacopIPTO.setEnable(String.valueOf(Constants.permission));
			oSacopIPTO.setActive(String.valueOf(Constants.permission));
			
			try {
				oSacopIntouchPadreDAO.insertar(oSacopIPTO);
			} catch (Exception e) {
				e.printStackTrace();
			}
			
		} else {
			// borramos todos los hijos de tbl_sacop_intouch, para ingresarlos
			// nuevamente.
			Collection buscarParaEliminar = handlerProcesosWonderWare.getSacopIntouchWonderware(formaBD.getIdplanillasacop1());

			SacopIntouchHijoDAO oSacopIntouchHijoDAO = new SacopIntouchHijoDAO();
			SacopIntouchHijoTO oSacopIntouchHijoTO = null;

			for (Iterator it = buscarParaEliminar.iterator(); it.hasNext();) {
				try {
					oSacopIntouchHijoTO = new SacopIntouchHijoTO();

					oSacopIntouchHijoTO = (SacopIntouchHijoTO) it.next();

					oSacopIntouchHijoDAO.eliminar(oSacopIntouchHijoTO);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		// _______________________________WonderWare___________________________________________________________________//
		// si vienen tagname, quiere decir que es una sacop preconfoigurada por
		// WonderWare
		if (tagsnamesSeleccionados != null) {
			Object[] datos = tagsnamesSeleccionados;
			for (int row = 0; row < datos.length; row++) {
				Object dato = datos[row];
				SacopIntouchHijoTO sacop_Intouchh_frm = new SacopIntouchHijoTO();
				sacop_Intouchh_frm.setIdPlanillaSacop1(String.valueOf(formaBD.getIdplanillasacop1()));
				sacop_Intouchh_frm.setTagName(dato.toString());
				sacop_Intouchh_frm.setDisparadaSacop(String.valueOf(Constants.notPermission));
				sacop_Intouchh_frm.setActive(String.valueOf(Constants.permission));
				idtagname = HandlerStruct.proximo("idtagname", "idtagname", "idtagname");
				sacop_Intouchh_frm.setIdTagName(String.valueOf(idtagname));
				// inserta
				
				SacopIntouchHijoDAO oSacopIntouchHijoDAO = new SacopIntouchHijoDAO();
				try {
					oSacopIntouchHijoDAO.insertar(sacop_Intouchh_frm);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		// _____________________________________________________________________________________________________//
	}

}
