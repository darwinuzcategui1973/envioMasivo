package com.desige.webDocuments.sacop.actions;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.dbutils.DbUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.upload.FormFile;

import com.desige.webDocuments.document.forms.BaseDocumentForm;
import com.desige.webDocuments.document.forms.DocumentsCheckOutsBean;
import com.desige.webDocuments.persistent.managers.HandlerBD;
import com.desige.webDocuments.persistent.managers.HandlerDBUser;
import com.desige.webDocuments.persistent.managers.HandlerDocuments;
import com.desige.webDocuments.persistent.managers.HandlerGrupo;
import com.desige.webDocuments.persistent.managers.HandlerParameters;
import com.desige.webDocuments.persistent.managers.HandlerProcesosSacop;
import com.desige.webDocuments.persistent.managers.HandlerSacop;
import com.desige.webDocuments.persistent.utils.JDBCUtil;
import com.desige.webDocuments.sacop.forms.Plantilla1BD;
import com.desige.webDocuments.sacop.forms.Plantilla1BDesqueleto;
import com.desige.webDocuments.sacop.forms.plantilla1;
import com.desige.webDocuments.seguridad.forms.SeguridadUserForm;
import com.desige.webDocuments.utils.ApplicationExceptionChecked;
import com.desige.webDocuments.utils.Constants;
import com.desige.webDocuments.utils.DesigeConf;
import com.desige.webDocuments.utils.ToolsHTML;
import com.desige.webDocuments.utils.beans.Search;
import com.desige.webDocuments.utils.beans.SuperAction;
import com.desige.webDocuments.utils.beans.Users;
import com.focus.qweb.dao.ActionRecommendedDAO;
import com.focus.qweb.dao.AreaDAO;
import com.focus.qweb.dao.CargoDAO;
import com.focus.qweb.dao.PlanillaSacop1DAO;
import com.focus.qweb.dao.PossibleCauseDAO;
import com.focus.qweb.to.AreaTO;
import com.focus.qweb.to.CargoTO;
import com.focus.qweb.to.PlanillaSacop1TO;
import com.focus.util.PerfilAdministrador;
import com.focus.wonderware.actions.HandlerProcesosWonderWare;

import sun.jdbc.rowset.CachedRowSet;

/**
 * Created by IntelliJ IDEA. User: srodriguez Date: 05/12/2005 Time: 09:47:32 AM
 * 
 * Title: LoadSacop.java<br>
 * Copyright: (c) 2004 Desige Servicios de Computaci�n<br/>
 * 
 * @author Ing. Nelson Crespo (NC)
 * @author Ing. Sim�n Rodrigu�z (SR)
 * @version WebDocuments v1.0 <br>
 *          Changes:<br>
 *          <ul>
 *          <li>06-09-2004 (NC) Creation</li>
 *          <li>03/08/2005 (SR) Se valida la variable primeravez, en caso de que sea la primeravez que entra el usuario para informarle que cambie su password
 *          por seguridad.</li>
 *          <ul>
 */
public class LoadSacop extends SuperAction {

	private static Logger log = LoggerFactory.getLogger(LoadSacop.class.getName());

	Locale defaultLocale = new Locale(DesigeConf.getProperty("language.Default"), DesigeConf.getProperty("country.Default"));
	ResourceBundle rb = ResourceBundle.getBundle("LoginBundle", defaultLocale);
	public static final String Correctiva = "0";
	public static final String Preventiva = "1";
	/*
	 * public static final String Correctivatxt ="Acci�n Correctiva"; public static final String Preventivatxt = "Acci�n Preventiva";
	 */

	public static final String Auditoria = "0";
	public static final String Deproceso = "1";
	public static final String Deproducto = "2";
	public static final String Sistemacalidad = "3";
	public static final String edoBorrador = "0";
	public static final String edoEmitida = "1";
	public static final String edoAprobado = "2";
	public static final String edoEnEjecucion = "3";
	public static final String edoPendienteVerifSeg = "4";
	public static final String edoVerificacion = "5";
	public static final String edoRechazado = "6";
	public static final String edoCerrado = "7";
	public static final String edoWonderWare = "8";
	public static final String edoWonderWareCerradaPreconfigurada = "9";
	public static final String edoVerificado = "10";
	public static final String acpetadoSi = "0";
	public static final String acpetadoNo = "1";

	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
		super.init(mapping, form, request, response);

		// seteamos el modulo activo
//		request.getSession().setAttribute(Constants.MODULO_ACTIVO, Constants.MODULO_SACOP);
		String getList = request.getParameter("getList") != null ? request.getParameter("getList") : "";
		
		try {
			Users user = getUserSession();
			String usuarioVerificacion = null;
			String info = request.getParameter("info");

			Users usuario = user;
			SeguridadUserForm securityForUser = ToolsHTML.getSeguridadModuloUser(usuario);
			SeguridadUserForm securityForGroup = ToolsHTML.getSeguridadModuloGroup(usuario);

			if (securityForUser.getSacop() == 0) {
				putAtributte("visible", "true");
			} else if (securityForUser.getSacop() == 2) {
				if (securityForGroup.getSacop() == 0) {
					putAtributte("visible", "true");
				} else {
					log.error("ACCESO ILEGAL:loadSACOP.do user:" + usuario.getIdPerson() + " / nombre:" + usuario.getNamePerson());
					return mapping.findForward("errorNotAuthorized");
				}
			} else {
				log.error("ACCESO ILEGAL:loadSACOP.do user:" + usuario.getIdPerson() + " / nombre:" + usuario.getNamePerson());
				return mapping.findForward("errorNotAuthorized");
			}

			// chequeamos si la sacop viene del vinculo de sacop relacionadas,
			// qiue es solo para visualizar
			// se valida en cada uno de los formularios, si es relacionada, solo
			// tiene cancelar para cerrar la ventana
			// viene de la forma sacop_relacionadas.jsp
			// en caso de que venga, trae el valor de 1 la variable
			// sacoprelacionada
			String sacoprelacionada = request.getParameter("sacoprelacionada") != null ? request.getParameter("sacoprelacionada") : "";
			putAtributte("sacoprelacionada", sacoprelacionada);

			if (ToolsHTML.checkValue(user.getUser())) {
				plantilla1 forma = (plantilla1) form;
				
				if(request.getParameter("idDocRelated")!=null) {
					forma.setIdDocumentRelated(request.getParameter("idDocRelated"));
				}

				((plantilla1) form).setUser(user.getUser());
				// inicializakmos
				// form=new plantilla1();

				String Idplanillasacop = null;
				String Idplanillasacop2 = null;

				String Idplanillasacop1 = request.getParameter("Idplanillasacop1") != null ? request.getParameter("Idplanillasacop1") : "";
			
				if (ToolsHTML.isEmptyOrNull(Idplanillasacop1)) {
					Idplanillasacop1 = forma.getIdplanillasacop1();
				}
				
				String idUser = String.valueOf(user.getIdPerson());// HandlerDocuments.getField("idperson","person","nameUser",user.getUser(),"=",1);
				putAtributte("idPerson", String.valueOf(user.getIdPerson()));

				// es el area que tenemos que buscar del responsable del sacop
				boolean showCharge = false;
				removeObjectSession("showCharge");
				String cargosacop = String.valueOf(HandlerParameters.PARAMETROS.getCargosacop());
				if (cargosacop.equalsIgnoreCase(Constants.typeNumByLocationVacio) || (ToolsHTML.isEmptyOrNull(cargosacop))) {
					showCharge = true;
					putObjectSession("showCharge", new Integer(1));
				}

				// cargamos los datos de la planilla
				PlanillaSacop1TO oPlanillaSacop1TO = new PlanillaSacop1TO();
				if(Idplanillasacop1!=null && !Idplanillasacop1.trim().equals("")) {
					PlanillaSacop1DAO oPlanillaSacop1DAO = new PlanillaSacop1DAO();
					oPlanillaSacop1TO.setIdplanillasacop1(Idplanillasacop1);
					if(oPlanillaSacop1DAO.cargar(oPlanillaSacop1TO)) {
					
						if(oPlanillaSacop1TO.getNoconformidadesref()==null || oPlanillaSacop1TO.getNoconformidadesref().replaceAll("[^0-9]","").length()==0) {
							oPlanillaSacop1TO.setNoconformidadesref(oPlanillaSacop1TO.getIdDocumentRelated());
							oPlanillaSacop1DAO.actualizar(oPlanillaSacop1TO);
						}

						String idUsuarioSolicitante = oPlanillaSacop1TO.getUsuarioSacops1();
						idUsuarioSolicitante = ToolsHTML.parseInt(idUsuarioSolicitante)>0?idUsuarioSolicitante:oPlanillaSacop1TO.getEmisor();
						/* SOLICITANTE EMISOR */
						String[] values = HandlerDocuments.getFields(new String[] { "nombres", "apellidos", "cargo" }, "person", "idperson",
								idUsuarioSolicitante , false);
						
						String nombre = null;
						String cargo = null;
						if (!showCharge) {
							if (values != null) {
								nombre = values[0] + " " + values[1];
								cargo = values[2];
							}
						} else {
							if (values != null) {
								nombre = values[0] + " " + values[1];
								cargo = values[2];
							}
						}
						CargoTO oCargoTO = new CargoTO();
						CargoDAO oCargoDAO = new CargoDAO();
						
						AreaTO oAreaTO = new AreaTO();
						AreaDAO oAreaDAO = new AreaDAO();
						
						oCargoTO.setIdCargo(cargo);
						oCargoDAO.cargar(oCargoTO);
						
						oAreaTO.setIdarea(oCargoTO.getIdArea());
						oAreaDAO.cargar(oAreaTO);
					
						forma.setSolicitantetxt(nombre);
						forma.setCargoSolicitante(oCargoTO.getCargo() + " -" + oAreaTO.getArea() + "-");
						/* fin solicitante */
					}
				}

				String ir = request.getParameter("goTo") != null ? request.getParameter("goTo") : "";

				log.info("Valor del parametro goTo es: '" + ir + "'");
				log.info("Valor del parametro getList es: '" + getList + "'");

				if (Boolean.parseBoolean(getList)) {
					// colocamos en sesion la bandeja desde la cual estamos
					// accesando
					request.getSession().setAttribute("bandejaToReload", ir);
					putObjectSession("bandejaToReload", ir);
					log.info("En sesion bandejaToReload: '" + ir + "'");
				}

				String crearSacop = ToolsHTML.getAttribute(request, "crearSacop") != null ? (String) ToolsHTML.getAttribute(request, "crearSacop") : "";
				
				// //System.out.println("crearSacop="+crearSacop);
				removeAttribute("crearSacop");
				if ("crearSacop".equalsIgnoreCase(crearSacop)) {
					putAtributte("crearSacop", "1");
					request.getSession().removeAttribute("bandejaToReload");
					
				}
				if (request.getParameter("mainAccess") != null) {
					request.getSession().removeAttribute("bandejaToReload");
				}

				// variable que me permite mostrar de tres en tres los textos de
				// las formas
				putAtributte("mostrardetrestext", request.getParameter("mostrardetrestext") != null ? request.getParameter("mostrardetrestext") : "0");

				// si seleccionaron visualizar todas las sacop incluyendo los
				// historicos.., se ve en estas variables
				ServletContext ctx = request.getSession().getServletContext();
				String nameAtt = "viewAll_".concat(String.valueOf(user.getIdPerson()));

				// System.out.println(ctx.getAttribute(nameAtt));
				String verTodos = ctx.getAttribute(nameAtt) != null ? (String) ctx.getAttribute(nameAtt) : "";
				// //removeObjectSession("verTodos");
				boolean verHistoricosTambien = false;
				putAtributte("verHistoricosTambien", "false");
				if ("1".equalsIgnoreCase(verTodos)) {
					verHistoricosTambien = true;
					putAtributte("verHistoricosTambien", "true");
				}

				forma.setNoconformidadesref(oPlanillaSacop1TO.getIdDocumentRelated());
				forma.setNoConformidadesDetail(getNoConformidadesDetail(oPlanillaSacop1TO.getNoconformidadesref(),Idplanillasacop1));

				//putAtributte("sacoplantillaInicio", forma);
				putAtributte("noConformidadesDetail", forma.getNoConformidadesDetail());


				// Esta variable solo es en intouch o auditoria, si la sacop
				// esqueleto va a ir preconfigurada como
				// borrador o emitido
				String NoModificarSacopIntouch = "";
				Plantilla1BDesqueleto formaBDesqueleto = new Plantilla1BDesqueleto();
				String generarSacop_Intouch = request.getParameter("generarSacop_Intouch") != null ? request.getParameter("generarSacop_Intouch") : "";
				if (!"1".equalsIgnoreCase(generarSacop_Intouch)) {
					removeObjectSession("tagsnamesSeleccionados");
					// esto cumple para la pagina principalSacop.jsp y reload
					// Collection borrador =
					// HandlerProcesosSacop.getUsuarioSacopPendientes(user,
					// idUser, LoadSacop.edoBorrador, verHistoricosTambien);
					// Collection emitido =
					// HandlerProcesosSacop.getUsuarioSacopPendientes(user,
					// idUser, LoadSacop.edoEmitida, verHistoricosTambien);
					// obtenemos los procesos seleccionados
					
					
					String queryUser = "";
					String areaAfectada = "";
					String idGrupoArea = "";
					String responsableArea = "";
					String idPersonArea = "";
					String cargo = "";
					
					queryUser = " from person where nameuser='" + user.getUser()  + "'" + " and accountActive='" + Constants.permission + "'";
					idPersonArea = HandlerDocuments.getField2("idperson", queryUser.toString());
					idGrupoArea = HandlerDocuments.getField2("idGrupo", queryUser.toString());
					cargo = HandlerDocuments.getField2("cargo", queryUser.toString());
					areaAfectada = HandlerProcesosSacop.findAreaByCargo(cargo);

					StringBuffer idStructs = new StringBuffer(50);
					idStructs.append("1");
					Hashtable security = HandlerGrupo.getAllSecurityForGroup(idGrupoArea, idStructs);
					HandlerDBUser.getAllSecurityForUser(Long.parseLong(idPersonArea.toString()), security, idStructs);
					Collection procesosSacop = HandlerProcesosSacop.getProcesosSacop(security, responsableArea, idGrupoArea);
					//Collection procesosSacop = HandlerProcesosSacop.getProcesosSacop(null);
					putAtributte("procesosSacop", procesosSacop);
					
					// responsable de area
					Collection usuariosArea1 = HandlerProcesosSacop.getSolicitudResponsableAll("idPerson", null, showCharge, true);
					putAtributte("usuariosArea1", usuariosArea1);
					
					Hashtable nodos = new Hashtable();
					boolean userIsLikeAnAdmin = PerfilAdministrador.userIsInAdminGroup(usuario);
					
					boolean saveQueryPendientes = ir.equalsIgnoreCase("Pendiente");
					boolean saveQueryCerrado    = ir.equalsIgnoreCase("Cerrado");
					boolean saveQueryRechazadas = ir.equalsIgnoreCase("Rechazado");
					
					Collection pendientes = HandlerProcesosSacop.getUsuarioSacopPendientes(request, user, idUser, LoadSacop.edoPendienteVerifSeg, verHistoricosTambien, null, saveQueryPendientes);
					Collection cerrado = HandlerProcesosSacop.getUsuarioSacopPendientes(request, user, idUser, LoadSacop.edoCerrado, verHistoricosTambien, request.getParameter("idDocumentRelatedFilter"), saveQueryCerrado);
					Collection rechazadas = HandlerProcesosSacop.getUsuarioSacopPendientes(request, user, idUser, LoadSacop.edoRechazado, verHistoricosTambien, null, saveQueryRechazadas);
					// hallazgos pendientes
		            Collection<DocumentsCheckOutsBean> registrosPendiente = HandlerDocuments.getPendingFindings(idUser, usuario.getUser(), null,nodos, userIsLikeAnAdmin);

					// putObjectSession("borrador", borrador);
					// putObjectSession("emitido", emitido);
					putObjectSession("pendientes", pendientes);
					putObjectSession("cerrado", cerrado);
					putObjectSession("rechazadas", rechazadas);
					putObjectSession("registrosPendiente", registrosPendiente);
					
					request.setAttribute("areaAfectada", areaAfectada);

					// request.setAttribute("borradorSize", borrador.size());
					// request.setAttribute("emitidoSize", emitido.size());
					request.setAttribute("rechazadasSize", rechazadas.size());
					request.setAttribute("pendientesSize", pendientes.size());
					request.setAttribute("cerradoSize", cerrado.size());
					request.setAttribute("registrosPendienteSize", registrosPendiente.size());

					// verificamos si debemos buscar sacops en base a los
					// filtros de busqueda
					if (request.getParameter("buscar") != null) {
						// tenemos peticion de busqueda de SACOPS
						log.info("Obtenemos SACOPs en base a filtros de busqueda");
						Collection sacopBuscadas = HandlerProcesosSacop.searchUsuarioSacop(request, user, idUser, verHistoricosTambien);
						putAtributte("sacopsBuscadas", sacopBuscadas);
					}

					Collection allUsers = HandlerDBUser.getAllUsersFilter(null, showCharge);
					// System.out.println("usuarios: " + allUsers);
					// por alguna razon aunque esto se guarde en session no esta
					// llegando a la pagina realmente
					// putObjectSession("todosUsuarios", allUsers);
					putAtributte("todosUsuarios", allUsers);

					Collection allAreas = HandlerProcesosSacop.loadAllAreas();
					putAtributte("todasAreas", allAreas);
					
					// nuevos todos los tipos
					Collection allTipos = HandlerProcesosSacop.loadAllTipos();
					putAtributte("todasTipos", allTipos);
					
					// nuevos todos los origenes
					Collection allOrigens = HandlerProcesosSacop.loadAllOrigens();
					putAtributte("todasOrigens", allOrigens);


					Collection busquedaStatus = HandlerProcesosSacop.getBusquedaStatus();
					putObjectSession("busquedaStatus", busquedaStatus);
					
					// se obtienen los origen de la SACOP
					Collection titulosplanillassacop = HandlerProcesosSacop.getTitulosPlanillasSacop(null);
					putObjectSession("titulosplanillassacop", titulosplanillassacop);
					
					// se obtienen los clasificcaion de la SACOP
					Collection clasificacionPlanillassacop = HandlerProcesosSacop.getClasificacionPlanillasSacop(null);
					putAtributte("clasificacionPlanillasSacop", clasificacionPlanillassacop);
					
					
				} else {
					// _____________con variables usando tabla
					// esqueleto___________________________________________________________
					// verificamos que si no viene de wonderware, borramos la
					// session de generarSacop_Intouch
					// Solo WonderWare(Proximamente auditoria) usa la planilla 1
					// , si viene de wonderware o auditoria.. leemos de la tabla
					// tbl_planillasacop1esqueleto
					String estadoEsqueletoConfiguradoSacop = request.getParameter("estadoEsqueletoConfiguradoSacop") != null ? request
							.getParameter("estadoEsqueletoConfiguradoSacop") : "";
					putAtributte("estadoEsqueletoConfiguradoSacop", estadoEsqueletoConfiguradoSacop);

					NoModificarSacopIntouch = request.getParameter("NoModificarSacopIntouch") != null ? request.getParameter("NoModificarSacopIntouch") : "";
					// String
					// estadoEsqueletoConfiguradoSacop=request.getParameter("estadoEsqueletoConfiguradoSacop")!=null?request.getParameter("estadoEsqueletoConfiguradoSacop"):"";
					String correcpreven = request.getParameter("correcpreven") != null ? request.getParameter("correcpreven") : "";

					formaBDesqueleto.setCorrecpreven(correcpreven);
					if (!ToolsHTML.isEmptyOrNull(estadoEsqueletoConfiguradoSacop)) {
						formaBDesqueleto.setEstado(Integer.parseInt(estadoEsqueletoConfiguradoSacop.toString()));
					}

					// _____________fin con variables usando tabla
					// esqueleto___________________________________________________________

				}
				// empezamos el tratamiento de cada una de las planillas
				// se procesa la primera planilla, o la planilla para llenar la
				// solicitud del sacop
				// luego que complete planillauno, se va a la pagina
				// planillasacop1.jsp para llenar el formato de solicitud
				if (ir.equalsIgnoreCase("planillauno")) {
					// la planilla se originara a travez de la base de datos o
					// se creara por primera vez
					// inicializamos por si acaso vienen valores de otras
					// planillas
					forma.cleanForm();
					// _______________________________________________________________________________________________________

					// System.out.println("execute planillauno");
					String edoBorradorenbd = request.getParameter("edodelsacop") != null ? request.getParameter("edodelsacop") : "";
					putAtributte("edoBorradorenbd", edoBorradorenbd);

					if (!ToolsHTML.isEmptyOrNull(edoBorradorenbd) && (edoBorradorenbd.equalsIgnoreCase("0"))) {
						// System.out.println("la planilla viene de base de datos y es borrador");
						// , si viene de wonderware o auditoria.. leemos de la
						// tabla tbl_planillasacop1esqueleto
						boolean esAuditoria_o_Wonderware = "1".equalsIgnoreCase(generarSacop_Intouch);
						removeAttribute("esAuditoria_o_Wonderware");
						if (esAuditoria_o_Wonderware) {
							putAtributte("esAuditoria_o_Wonderware", "1");
						}
						// si swNoModificarSacopIntouch trae 1, implica que la
						// tabla planillasacop1 contiene registrada
						// una planilla esqueleto, entonces la planillaesqueleto
						// no puede ser modificada.
						boolean swNoModificarSacopIntouch = "1".equalsIgnoreCase(NoModificarSacopIntouch);

						// generarSacop_Intouch
						removeAttribute("swNoModificarSacopIntouch");
						if (swNoModificarSacopIntouch) {
							putAtributte("NoModificarSacopIntouch", "1");

						}
						// si el id de planillaesqueleto en planillassacop1 se
						// encuentra cerrada o no existe,
						// se puede colocar la opcion de borrarla logicamente de
						// esqueleto para que no siga generando planillas sacop
						removeAttribute("sacopAbiertasSacopIntouch");
						HandlerProcesosWonderWare handlerProcesosWonderWare = new HandlerProcesosWonderWare();
						if (handlerProcesosWonderWare.getExisteIdEsqueletoEn_Planillasacop1(Long.parseLong(Idplanillasacop1))) {
							putAtributte("sacopAbiertasSacopIntouch", "1");
						}
						// hh
						getBorradordeBaseDatos(showCharge, Idplanillasacop1, esAuditoria_o_Wonderware, formaBDesqueleto, request);
					} else {
						// System.out.println("la planilla es Completamente nueva");
						// si por casualidad es una planilla sacop_intouch, y es
						// nueva, no sacar el check
						// para eliminar la sacop_intouch preonigurada
						putAtributte("EsnuevanoSacop", "1");
						getPrimeravez(request, showCharge, user);
					}
					// _______________________________________________________________________________________________________
					// nos vamos a la pagina planillasacop1.jsp, esta genera un
					// edo en donde la proxima accion sera
					// ir a planillados
				} else if (ir.equalsIgnoreCase("planillados")) {
					// System.out.println("execute planillados");
					// Luego de generar los scripts abajo, nos vamos por
					// el forward de planillados es planillasacop2.jsp
					Idplanillasacop2 = Idplanillasacop1;
					// ....se verifica si el suuario puede modificar la
					// plantilla inicio
					plantilla1 valorporReferencia = new plantilla1();
					valorporReferencia.setCmd("0");
					removeObjectSession("modificando");
					HandlerProcesosSacop.getUsuarioSacopPendientes(idUser, valorporReferencia, Idplanillasacop2);
					// al usuario le permite modificar
					if (!valorporReferencia.getCmd().equalsIgnoreCase("0")) {
						putObjectSession("modificando", "1");
					}
					// ....se verifica si el suuario puede modificar la
					// plantilla fin
					// el forward planillados envia a la pagina
					// planillasacop2.jsp
					// este parametro lo agarra planillasacop2.jsp
					putAtributte("Idplanillasacop1", Idplanillasacop2);
					// esta coleccion trae la informacion del especifico id del
					// sacop y complementa
					// la informacion txt de cada uno de los usuarios
					// ivolucrados
					boolean esAuditoria_o_Wonderware = false;
					Collection responsable = HandlerProcesosSacop.getInfResponsable("idplanillasacop1", Idplanillasacop2, showCharge, false,
							esAuditoria_o_Wonderware);
					Iterator it = responsable.iterator();
					if (it.hasNext()) {
						// esto cumple para la pagina planillasacop2.jsp
						Plantilla1BD obj = (Plantilla1BD) it.next();
						String id = obj.getProcesosafectados();
						String fechaEmision = ToolsHTML.sdfShowWithoutHour.format(obj.getFechaemision());
						// obtenemos los procesos seleccionados
						Collection procesosSacop = HandlerProcesosSacop.getProcesosSacop(id);
						// obtenemos las normas seleccionados
						Collection norms = null;
						if (!ToolsHTML.isEmptyOrNull(obj.getRequisitosaplicable())) {
							norms = HandlerProcesosSacop.getRequisitosAplicableSacop("idNorm", obj.getRequisitosaplicable());
						}

						// obtenemos los usuarios seleccionados
						Collection usuarios = HandlerProcesosSacop.getSolicitudinforma("idPerson", obj.getSolicitudinforma(), showCharge);
						plantilla1 formaResponsable = new plantilla1();
						cargarDataDeSacop(formaResponsable, forma, obj, request);
						// en caso que halla un archivo asociado en la sacop
						removeAttribute("sacopfile");
						if (!ToolsHTML.isEmptyOrNull(obj.getContentType())) {
							putAtributte("sacopfile", "1");
						}
						// en caso que sea nuevo el formaResponsable , borramos
						// el atributo
						if (!"1".equals(formaResponsable.getRechazoapruebo())) {
							formaResponsable.setNoaceptada("");
							forma.setNoaceptada("");
							removeAttribute("rechazoapruebo");
						} else {
							// en caso que ya sea rechazado y guardado .. te lo
							// habilita
							putAtributte("rechazoapruebo", request.getParameter("rechazoapruebo") != null ? request.getParameter("rechazoapruebo") : "");
						}
						// removeAttribute("rechazoapruebo");
						// formaResponsable.setNoaceptada("");
						// formaResponsable.setNoaceptada("esto e ssaymon");
						putAtributte("fechaEmision", fechaEmision);
						putAtributte("norms", norms);
						putAtributte("procesosSacop", procesosSacop);
						putAtributte("usuarioResponsable", formaResponsable);
						putAtributte("usuarios", usuarios);
						putAtributte("sacoplantilla", forma);
					}
				} else if (ir.equalsIgnoreCase("planilla30")) {
					// System.out.println("execute planilla30");
					// rescatamos el valor de Idplanillasacop1 que se origino en
					// ActualizarPlanillaSacop
					if (ToolsHTML.isEmptyOrNull(Idplanillasacop1)) {
						Idplanillasacop2 = request.getParameter("idplanilla2");// (String)getSessionObject("Idplanillasacop1");
					} else {
						Idplanillasacop2 = Idplanillasacop1;
					}
					// ....se verifica si el suuario puede modificar la
					// plantilla inicio
					// se usa como variables por referencias esta forma, para
					// traer valores de este metodo
					plantilla1 valorporReferencia = new plantilla1();
					valorporReferencia.setCmd("0");
					removeObjectSession("modificando");
					HandlerProcesosSacop.getUsuarioSacopPendientes(idUser, valorporReferencia, Idplanillasacop2);
					// al usuario le permite modificar
					if (!valorporReferencia.getCmd().equalsIgnoreCase("0")) {
						putObjectSession("modificando", "1");
					}
					// ....se verifica si el suuario puede modificar la
					// plantilla fin

					// borramos inmediatamente la session Idplanillasacop1 para
					// que no heche broma
					removeObjectSession("Idplanillasacop1");

					putAtributte("Idplanillasacop1", Idplanillasacop2);

					// esta coleccion trae un solo usuario
					boolean esAuditoria_o_Wonderware = false;
					Collection responsable = HandlerProcesosSacop.getInfResponsable("idplanillasacop1", Idplanillasacop2, showCharge, false,
							esAuditoria_o_Wonderware);
					// Collection
					// responsable=HandlerProcesosSacop.getInfResponsable("idplanillasacop1",Idplanillasacop2,showCharge);
					Iterator it = responsable.iterator();
					if (it.hasNext()) {
						Plantilla1BD obj = (Plantilla1BD) it.next();
						String id = obj.getProcesosafectados();
						String fechaEmision = ToolsHTML.sdfShowWithoutHour.format(obj.getFechaemision());
						Collection procesosSacop = HandlerProcesosSacop.getProcesosSacop(id);
						// obtenemos las normas seleccionados
						Collection norms = null;
						if (!ToolsHTML.isEmptyOrNull(obj.getRequisitosaplicable())) {
							norms = HandlerProcesosSacop.getRequisitosAplicableSacop("idNorm", obj.getRequisitosaplicable());
						}
						Collection usuarios = HandlerProcesosSacop.getSolicitudinforma("idPerson", obj.getSolicitudinforma());
						Collection tomarAcciones = HandlerProcesosSacop.getTomarAcciones();
						plantilla1 formaResponsable = new plantilla1();
						// en caso que no sea aceptado, me trae una descripcion
						// de porque no fue aceptado

						cargarDataDeSacop(formaResponsable, forma, obj, request);

						// en caso que halla un archivo asociado en la sacop
						removeAttribute("sacopfile");
						if (!ToolsHTML.isEmptyOrNull(obj.getContentType())) {
							putAtributte("sacopfile", "1");
						}
						putAtributte("fechaEmision", fechaEmision);
						putAtributte("norms", norms);
						putAtributte("procesosSacop", procesosSacop);
						putAtributte("usuarioResponsable", formaResponsable);
						putAtributte("usuarios", usuarios);
						putAtributte("tomarAcciones", tomarAcciones);
						putAtributte("sacoplantilla", forma);
										}
				} else if (ir.equalsIgnoreCase("planilla4")) {
					// System.out.println("execute planilla4");

					// borramos esta session por si acaso
					// removeObjectSession("Idplanillasacop1");
					Idplanillasacop = Idplanillasacop1;
					String tomarAcciones = request.getParameter("tomarAcciones") != null ? request.getParameter("tomarAcciones") : "";
					// variable que me permite mostrar comentariosGenerales en
					// caso que se agregue y me valla a la sig pagina
					putAtributte("comentariosGenerales", request.getParameter("comentariosGenerales") != null ? request.getParameter("comentariosGenerales")
							: "");
					// ....se verifica si el suuario puede modificar la
					// plantilla inicio
					// se usa como variables por referencias esta forma, para
					// traer valores de este metodo
					plantilla1 valorporReferencia = new plantilla1();
					valorporReferencia.setCmd("0");
					removeObjectSession("modificando");
					HandlerProcesosSacop.getUsuarioSacopPendientes(idUser, valorporReferencia, Idplanillasacop);
					// al usuario le permite modificar
					if (!valorporReferencia.getCmd().equalsIgnoreCase("0")) {
						putObjectSession("modificando", "1");
					}
					// ....se verifica si el suuario puede modificar la
					// plantilla fin
					// esta coleccion trae un solo usuario
					boolean esAuditoria_o_Wonderware = false;
					Collection responsable = HandlerProcesosSacop.getInfResponsable("idplanillasacop1", Idplanillasacop, showCharge, false,
							esAuditoria_o_Wonderware);
					Iterator it = responsable.iterator();
					if (it.hasNext()) {
						// esto cumple para la pagina planillasacop2.jsp
						Plantilla1BD obj = (Plantilla1BD) it.next();
						String id = obj.getProcesosafectados();
						String fechaEmision = ToolsHTML.sdfShowWithoutHour.format(obj.getFechaemision());
						Collection procesosSacop = HandlerProcesosSacop.getProcesosSacop(id);
						// obtenemos las normas seleccionados
						Collection norms = null;
						if (!ToolsHTML.isEmptyOrNull(obj.getRequisitosaplicable())) {
							norms = HandlerProcesosSacop.getRequisitosAplicableSacop("idNorm", obj.getRequisitosaplicable());
						}
						Collection usuarios = HandlerDBUser.getAllUsersFilter(null, showCharge);
						Collection usuarios1 = HandlerProcesosSacop.getSolicitudinforma("idPerson", obj.getSolicitudinforma());// HandlerDBUser.getAllUsersFilter(null);
						plantilla1 formaResponsable = new plantilla1();
						usuarioVerificacion = String.valueOf(obj.getEmisor());
						// cargamos datos del responsable
						removeAttribute("tieneAccionesPendientes");
						// verifica si tiene acciones pendiente este usuario
						byte noFirmo = Constants.notPermission;
						boolean swPorUsuario = true;
						String accPend = LoadAccionesTomarSacopAction.accionesPendientes(String.valueOf(user.getIdPerson()), Idplanillasacop, noFirmo,
								swPorUsuario);
						if (!ToolsHTML.isEmptyOrNull(accPend)) {
							if (!"1".equalsIgnoreCase(accPend)) {
								putAtributte("tieneAccionesPendientes", "1");
							}
						}
						removeAttribute("nocompletarsacop");
						swPorUsuario = false;
						accPend = LoadAccionesTomarSacopAction.accionesPendientes(String.valueOf(user.getIdPerson()), Idplanillasacop, noFirmo, swPorUsuario);
						if (!ToolsHTML.isEmptyOrNull(accPend)) {
							if (!"1".equalsIgnoreCase(accPend)) {
								putAtributte("nocompletarsacop", "1");
							}
						}
						String fechaEst = "";
						if (!ToolsHTML.isEmptyOrNull(obj.getFechaEstimada().toString())) {
							// System.out.println("obj.getFechaEstimada().toString()="
							// + obj.getFechaEstimada().toString());
							fechaEst = ToolsHTML.sdfShowWithoutHour.format(ToolsHTML.date.parse(obj.getFechaEstimada().toString()));
						}
						// en caso que halla un archivo asociado en la sacop
						removeAttribute("sacopfile");
						if (!ToolsHTML.isEmptyOrNull(obj.getContentType())) {
							putAtributte("sacopfile", "1");
						}
						cargarDataDeSacop(formaResponsable, forma, obj, request);
						putAtributte("fechaEstimada", fechaEst);
						putAtributte("fechaEmision", fechaEmision);
						putAtributte("norms", norms);
						putAtributte("procesosSacop", procesosSacop);
						putAtributte("usuarioResponsable", formaResponsable);
						putAtributte("usuarios", usuarios);
						putAtributte("usuarios1", usuarios1);
						putAtributte("tomarAcciones", tomarAcciones);
						putAtributte("sacoplantilla", forma);
					}
				} else if (ir.equalsIgnoreCase("planilla5") || ir.equalsIgnoreCase("planilla10")) {
					// System.out.println("execute planilla5");
					Idplanillasacop = Idplanillasacop1;
					String tomarAcciones = request.getParameter("tomarAcciones") != null ? request.getParameter("tomarAcciones") : "";
					putAtributte("accionesEstablecidas", request.getParameter("accionesEstablecidas") != null ? request.getParameter("accionesEstablecidas")
							: "");
					putAtributte("eliminarcausaraiz", request.getParameter("eliminarcausaraiz") != null ? request.getParameter("eliminarcausaraiz") : "");
					// ....se verifica si el suuario puede modificar la
					// plantilla inicio
					// se usa como variables por referencias esta forma, para
					// traer valores de este metodo
					plantilla1 valorporReferencia = new plantilla1();
					valorporReferencia.setCmd("0");
					removeObjectSession("modificando");
					HandlerProcesosSacop.getUsuarioSacopPendientes(idUser, valorporReferencia, Idplanillasacop);
					// al usuario le permite modificar
					if (!valorporReferencia.getCmd().equalsIgnoreCase("0")) {
						putObjectSession("modificando", "1");
					}
					// ....se verifica si el suuario puede modificar la
					// plantilla fin
					// esta coleccion trae un solo usuario
					boolean esAuditoria_o_Wonderware = false;
					Collection responsable = HandlerProcesosSacop.getInfResponsable("idplanillasacop1", Idplanillasacop, showCharge, false,
							esAuditoria_o_Wonderware);
					Iterator it = responsable.iterator();
					if (it.hasNext()) {
						// esto cumple para la pagina planillasacop2.jsp
						// La planilla 5 se procesa ya para el que emitio el
						// sacop
						// y se direge al planillasacop5.jsp
						Plantilla1BD obj = (Plantilla1BD) it.next();
						String id = obj.getProcesosafectados();
						String fechaEmision = ToolsHTML.sdfShowWithoutHour.format(obj.getFechaemision());
						Collection procesosSacop = HandlerProcesosSacop.getProcesosSacop(id);
						// obtenemos las normas seleccionados
						Collection norms = null;
						if (!ToolsHTML.isEmptyOrNull(obj.getRequisitosaplicable())) {
							norms = HandlerProcesosSacop.getRequisitosAplicableSacop("idNorm", obj.getRequisitosaplicable());
						}
						Collection usuarios = HandlerDBUser.getAllUsersFilter(null, showCharge);
						Collection usuarios1 = HandlerProcesosSacop.getSolicitudinforma("idPerson", obj.getSolicitudinforma());// HandlerDBUser.getAllUsersFilter(null);
						plantilla1 formaResponsable = new plantilla1();
						usuarioVerificacion = String.valueOf(obj.getEmisor());
						// obtenemos el numero del sacop
						cargarDataDeSacop(formaResponsable, forma, obj, request);

						// formaResponsable.setAccionobservaciontxt(formaResponsable.getAccionobservaciontxt()
						// + "    " + rb.getString("scp.observacion") + ":" +
						// obj.getComntresponsablecerrar());
						formaResponsable.setAccionobservaciontxt(formaResponsable.getAccionobservaciontxt());
					
						// modifique aqui para ver sii darwinuzcategui
						formaResponsable.setAccionesEstablecidastxt("");
						formaResponsable.setEliminarcausaraiztxt("");
						/*
						 * formaResponsable.setAccionesEstablecidastxt(request.getParameter("accionesEstablecidastxt"));
						// formaResponsable.setEliminarcausaraiztxt(request.getParameter("eliminarcausaraiztxt"));

						 */

						
						// en caso que halla un archivo asociado en la sacop
						removeAttribute("sacopfile");
						if (!ToolsHTML.isEmptyOrNull(obj.getContentType())) {
							putAtributte("sacopfile", "1");
						}
						putAtributte("fechaEmision", fechaEmision);
						putAtributte("norms", norms);
						putAtributte("procesosSacop", procesosSacop);
						putAtributte("usuarioResponsable", formaResponsable);
						putAtributte("usuarios", usuarios);
						putAtributte("usuarios1", usuarios1);
						putAtributte("tomarAcciones", tomarAcciones);
						putAtributte("sacoplantilla", forma);
					}
				} else if (ir.equalsIgnoreCase("planilla6") || ir.equalsIgnoreCase("planilla7")) {
					// System.out.println("execute planilla6");
					// La planilla 6 se ya al sacop cerrado
					// borramos esta session por si acaso
					removeObjectSession("Idplanillasacop1");
					Idplanillasacop = Idplanillasacop1;
					String tomarAcciones = request.getParameter("tomarAcciones") != null ? request.getParameter("tomarAcciones") : "";
					// ....se verifica si el suuario puede modificar la
					// plantilla inicio
					// se usa como variables por referencias esta forma, para
					// traer valores de este metodo
					plantilla1 valorporReferencia = new plantilla1();
					valorporReferencia.setCmd("0");
					removeObjectSession("modificando");
					HandlerProcesosSacop.getUsuarioSacopPendientes(idUser, valorporReferencia, Idplanillasacop);
					// al usuario le permite modificar
					if (!valorporReferencia.getCmd().equalsIgnoreCase("0")) {
						putObjectSession("modificando", "1");
					}
					// ....se verifica si el suuario puede modificar la
					// plantilla fin
					// esta coleccion trae un solo usuario
					boolean esAuditoria_o_Wonderware = false;
					Collection responsable = HandlerProcesosSacop.getInfResponsable("idplanillasacop1", Idplanillasacop, showCharge, false,
							esAuditoria_o_Wonderware);
					Iterator it = responsable.iterator();
					if (it.hasNext()) {
						// esto cumple para la pagina planillasacop2.jsp
						Plantilla1BD obj = (Plantilla1BD) it.next();
						String id = obj.getProcesosafectados();
						String fechaEmision = ToolsHTML.sdfShowWithoutHour.format(obj.getFechaemision());
						Collection procesosSacop = HandlerProcesosSacop.getProcesosSacop(id);
						// obtenemos las normas seleccionados
						Collection norms = null;
						if (!ToolsHTML.isEmptyOrNull(obj.getRequisitosaplicable())) {
							norms = HandlerProcesosSacop.getRequisitosAplicableSacop("idNorm", obj.getRequisitosaplicable());
						}

						Collection usuarios = HandlerDBUser.getAllUsersFilter(null, showCharge);
						Collection usuarios1 = HandlerProcesosSacop.getSolicitudinforma("idPerson", obj.getSolicitudinforma());// HandlerDBUser.getAllUsersFilter(null);
						plantilla1 formaResponsable = new plantilla1();
						usuarioVerificacion = String.valueOf(obj.getEmisor());
						cargarDataDeSacop(formaResponsable, forma, obj, request);
						// formaResponsable.setAccionobservaciontxt(formaResponsable.getAccionobservaciontxt()
						// + "<br><br>" + rb.getString("scp.observacion") + ":"
						// + obj.getComntresponsablecerrar());
						formaResponsable.setAccionobservaciontxt(formaResponsable.getAccionobservaciontxt());

						// en caso que halla un archivo asociado en la sacop
						removeAttribute("sacopfile");
						if (!ToolsHTML.isEmptyOrNull(obj.getContentType())) {
							putAtributte("sacopfile", "1");
						}
						putAtributte("fechaEmision", fechaEmision);
						putAtributte("norms", norms);
						putAtributte("procesosSacop", procesosSacop);
						putAtributte("usuarioResponsable", formaResponsable);
						putAtributte("usuarios", usuarios);
						putAtributte("usuarios1", usuarios1);
						putAtributte("tomarAcciones", tomarAcciones);
						putAtributte("sacoplantilla", forma);
					}
				}

			}

			// causas
			PossibleCauseDAO oPossibleCauseDAO = new PossibleCauseDAO();
			putAtributte("listPossibleCause", oPossibleCauseDAO.listarPossibleCauseAlls(null));
			putAtributte("ListOrderPossibleCause", oPossibleCauseDAO.listarOrderPossibleCauseAlls(null));
			
			
			// acciones recomendadas
			//ActionRecommendedDAO oActionRecommendedDAO = new ActionRecommendedDAO();
			//putAtributte("listActionRecommended", oActionRecommendedDAO.listarActionRecommendedAlls(null));
			//putAtributte("listOrderActionRecommended", oActionRecommendedDAO.listarOrderActionRecommendedAlls(null));


			// ActionForward crearSacop_Intouch = new
			// ActionForward("/padonde.do?");

			// return crearSacop_Intouch;
			if ("reload".equals(request.getParameter("goTo"))) {
				log.info("sesion value [bandejaToReload]=" + getSessionObject("bandejaToReload"));

				if (request.getSession().getAttribute("bandejaToReload") != null) {
					log.info("Retornando con goTo=" + request.getSession().getAttribute("bandejaToReload"));
					return goTo((String) request.getSession().getAttribute("bandejaToReload"));
				}
			}

			log.info("Retornando con goTo=" + request.getParameter("goTo"));
			return goTo(request.getParameter("goTo"));
		} catch (ApplicationExceptionChecked ae) {
			return goError(ae.getKeyError());
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return goError();
	}

	/**
	 * 
	 * @param formaResponsable
	 * @param forma
	 * @param obj
	 * @param request
	 */
	public void cargarDataDeSacop(plantilla1 formaResponsable, plantilla1 forma, final Plantilla1BD obj, HttpServletRequest request) {
		try {
			// ___________________________________________________
			// cargamos el dato si es de Sacop_Intouch
			formaResponsable.setIdplanillasacop1esqueleto(obj.getIdplanillasacop1esqueleto());
			forma.setIdplanillasacop1esqueleto(obj.getIdplanillasacop1esqueleto());
			// ___________________________________________________
			forma.setSacopnum(obj.getSacopnum());
			forma.setUsernotificadotxt(obj.getUsernotificadotxt());
			forma.setUser(HandlerDocuments.getField("nameUser", "person", "idperson", String.valueOf(obj.getEmisor()), "=", 1, false,Thread.currentThread().getStackTrace()[1].getMethodName()));
			HandlerSacop.load(forma);
			HandlerSacop.load(formaResponsable);
			formaResponsable.setUser(HandlerDocuments.getField("nameUser", "person", "idperson", String.valueOf(obj.getRespblearea()), "=", 1, false,Thread.currentThread().getStackTrace()[1].getMethodName()));
			String areaAfectada = HandlerProcesosSacop.findAreaByUsuario(String.valueOf(obj.getUsernotificado()));
			formaResponsable.setAreafectadatxt(areaAfectada);
			HandlerSacop.load(formaResponsable);
			formaResponsable.setOrigensacop(String.valueOf(obj.getOrigensacop()));
			formaResponsable.setOrigensacoptxt(obtenerOrigenSacop(formaResponsable.getOrigensacop()));
			formaResponsable.setCorrecpreven(obj.getCorrecpreven());
			formaResponsable.setDescripcion(obj.getDescripcion());
			formaResponsable.setCausasnoconformidad(obj.getCausasnoconformidad());
			formaResponsable.setAccionrecomendada(obj.getAccionesrecomendadas());
			formaResponsable.setIdplanillasacop1(String.valueOf(obj.getIdplanillasacop1()));
			formaResponsable.setNoaceptada(obj.getNoaceptada());
			formaResponsable.setEdodelsacop(String.valueOf(obj.getEstado()));
			formaResponsable.setEdodelsacoptxt(obj.getEstadotxt());
			formaResponsable.setAccionobservaciontxt(HandlerProcesosSacop.obtenerDatosResponsablesAccionesSacop(String.valueOf(obj.getIdplanillasacop1())));
			formaResponsable.setRechazoapruebo(obj.getRechazoapruebo());
			formaResponsable.setNumerodeaccion(request.getParameter("tomarAcciones"));
			formaResponsable.setAccionobservacion(obj.getAccionobservacion());
			formaResponsable.setFechaEstimada(obj.getFechaEstimada().toString());
			formaResponsable.setFechaEmision(ToolsHTML.date.format(obj.getFechaemision()));
			formaResponsable.setFechaSacops1(ToolsHTML.date.format(obj.getFechasacops1()));
			formaResponsable.setRechazoapruebo(obj.getRechazoapruebo());
			formaResponsable.setIdplanillasacop1(String.valueOf(obj.getIdplanillasacop1()));
			formaResponsable.setEliminarcausaraiztxt("");
			formaResponsable.setAccionesEstablecidastxt("");
			formaResponsable.setAccionesEstablecidas(obj.getAccionesEstablecidas());
			formaResponsable.setAccionesEstablecidastxt(obj.getAccionesEstablecidastxt());
			formaResponsable.setEliminarcausaraiz(obj.getEliminarcausaraiz());
			formaResponsable.setEliminarcausaraiztxt(obj.getEliminarcausaraiztxt());
			formaResponsable.setAccionobservacion(obj.getAccionobservacion());
			formaResponsable.setAccionrecomendada(obj.getAccionesrecomendadas());
			formaResponsable.setIdplanillasacop1(String.valueOf(obj.getIdplanillasacop1()));
			formaResponsable.setSacop_relacionadas(obj.getSacop_relacionadas());
			formaResponsable.setNoconformidades(obj.getNoconformidades());
			formaResponsable.setNoconformidadesref(obj.getNoconformidadesref());
			formaResponsable.setNameRelatedDocument(obj.getNameRelatedDocument());
			formaResponsable.setComntresponsablecerrar(obj.getComntresponsablecerrar());
			formaResponsable.setUsuarioSacops1(String.valueOf(obj.getUsuarioSacops1()));
			formaResponsable.setSolicitantetxt(HandlerDocuments.getField("nameUser", "person", "idperson", formaResponsable.getUsuarioSacops1(), "=", 1, false,Thread.currentThread().getStackTrace()[1].getMethodName()));
			formaResponsable.setIdDocumentRelated(String.valueOf(obj.getIdDocumentRelated()));
			formaResponsable.setIdDocumentAssociate(String.valueOf(obj.getIdDocumentAssociate()));
			formaResponsable.setNumVerDocumentAssociate(String.valueOf(obj.getNumVerDocumentAssociate()));
			formaResponsable.setNameDocumentAssociate(obj.getNameDocumentAssociate());
			formaResponsable.setRequireTracking(obj.getRequireTracking());
			formaResponsable.setRequireTrackingDate(obj.getRequireTrackingDate());
			formaResponsable.setTrackingSacop(obj.getTrackingSacop());
			formaResponsable.setNumberTrackingSacop(obj.getNumberTrackingSacop());
			formaResponsable.setIdRegisterGenerated(obj.getIdRegisterGenerated());
			formaResponsable.setDescripcionAccionPrincipal(obj.getDescripcionAccionPrincipal());
			
			// ini: cargo del solicitante
			
			// es el area que tenemos que buscar del responsable del sacop
			boolean showCharge = false;
			removeObjectSession("showCharge");
			String cargosacop = String.valueOf(HandlerParameters.PARAMETROS.getCargosacop());
			if (cargosacop.equalsIgnoreCase(Constants.typeNumByLocationVacio) || (ToolsHTML.isEmptyOrNull(cargosacop))) {
				showCharge = true;
				putObjectSession("showCharge", new Integer(1));
			}

			String[] values = HandlerDocuments.getFields(new String[] { "nombres", "apellidos", "cargo" }, "person", "idperson",
					formaResponsable.getUsuarioSacops1(), false);
			
			String nombre = null;
			String cargo = null;
			if (!showCharge) {
				if (values != null) {
					nombre = values[0] + " " + values[1];
					cargo = values[2];
				}
			} else {
				if (values != null) {
					nombre = values[0] + " " + values[1];
					cargo = values[2];
				}
			}
			CargoTO oCargoTO = new CargoTO();
			CargoDAO oCargoDAO = new CargoDAO();
			
			AreaTO oAreaTO = new AreaTO();
			AreaDAO oAreaDAO = new AreaDAO();
			
			oCargoTO.setIdCargo(cargo);
			oCargoDAO.cargar(oCargoTO);
			
			oAreaTO.setIdarea(oCargoTO.getIdArea());
			oAreaDAO.cargar(oAreaTO);
		
			formaResponsable.setSolicitantetxt(nombre);
			formaResponsable.setCargoSolicitante(oCargoTO.getCargo() + " -" + oAreaTO.getArea() + "-");
			// fin: cargo del solicitante
			
			// solo se utiliza en planilla1
			forma.setSacop_relacionadas(obj.getSacop_relacionadas());
			forma.setNoconformidades(obj.getNoconformidades());
			forma.setNoconformidadesref(obj.getNoconformidadesref());
			forma.setNoConformidadesDetail(getNoConformidadesDetail(obj.getNoconformidadesref(),String.valueOf(obj.getIdplanillasacop1())));
			forma.setSacopRelacionadasDetail(getSacopRelacionadasDetail(obj.getSacop_relacionadas()));
			forma.setNameRelatedDocument(obj.getNameRelatedDocument());

			// sw que me indica si muestro o no la etiqueta en que la sacop
			// tiene
			removeObjectSession("existesacops_noconformidades");
			if (!ToolsHTML.isEmptyOrNull(formaResponsable.getNoconformidades())) {
				putObjectSession("existesacops_noconformidades", "1");
			}
			removeObjectSession("existesacops_noconformidadesref");
			if (!ToolsHTML.isEmptyOrNull(formaResponsable.getNoconformidadesref())) {
				putObjectSession("existesacops_noconformidadesref", "1");
			}
			// relaciones con otras sacops, se implementa en las
			// .jsp(planilla1.jsp .. planilla6.jsp)
			removeObjectSession("existesacops_relacionadas");
			if (!ToolsHTML.isEmptyOrNull(formaResponsable.getSacop_relacionadas())) {
				putObjectSession("existesacops_relacionadas", "1");
			}
			if (!ToolsHTML.isEmptyOrNull(obj.getFechaEstimada().toString())) {
				formaResponsable.setFechaEstimada(ToolsHTML.sdfShowWithoutHour.format(ToolsHTML.date.parse(obj.getFechaEstimada().toString())));
			}
			if (!ToolsHTML.isEmptyOrNull(obj.getFechaReal().toString())) {
				formaResponsable.setFechaReal(ToolsHTML.sdfShowWithoutHour.format(ToolsHTML.date.parse(obj.getFechaReal().toString())));
			}
			if (obj.getFechaWhenDiscovered() != null) {
				formaResponsable.setFechaWhenDiscovered(ToolsHTML.sdfShowWithoutHour.format(obj.getFechaWhenDiscovered()));
			}
			if (obj.getFechaVerificacion() != null) {
				formaResponsable.setFechaVerificacion(ToolsHTML.sdfShowWithoutHour.format(obj.getFechaVerificacion()));
			}
			if (obj.getFechaCierre() != null) {
				formaResponsable.setFechaCierre(ToolsHTML.sdfShowWithoutHour.format(obj.getFechaCierre()));
			}

			FormFile archivoTecnica = new FormFile() {

				public void setFileSize(int arg0) {
				}

				public void setFileName(String arg0) {
				}

				public void setContentType(String arg0) {
				}

				public InputStream getInputStream() throws FileNotFoundException, IOException {
					return null;
				}

				public int getFileSize() {
					return 0;
				}

				public String getFileName() {
					return obj.getArchivoTecnica();
				}

				public byte[] getFileData() throws FileNotFoundException, IOException {
					return null;
				}

				public String getContentType() {
					return null;
				}

				public void destroy() {
				}
			};

			formaResponsable.setArchivoTecnica(archivoTecnica);
			formaResponsable.setIdClasificacion(obj.getIdClasificacion());
			formaResponsable.setNombreClasificacionSACOP(HandlerDocuments.getField("descripcion", "tbl_clasificacionplanillassacop", "id",
					String.valueOf(obj.getIdClasificacion()), "=", 2,Thread.currentThread().getStackTrace()[1].getMethodName()));

			// HandlerProcesosSacop handlerProcesosSacop=new
			// HandlerProcesosSacop();
			// boolean swFiltrar=false;
			// Collection sacops_relacionar =
			// handlerProcesosSacop.getSacops_ParaRelacionar("",swFiltrar);
			// putObjectSession("sacops_relacionar",sacops_relacionar);
			// if (!sacops_relacionar.isEmpty()) {
			// putObjectSession("size",String.valueOf(sacops_relacionar.size()));
			// }
		} catch (Exception e) {
			e.printStackTrace();
			// System.out.println("e = " + e);
		}
	}

	public static String obtenerEdoBorrador(String edo) {
		String salida = null;
		Locale defaultLocale = new Locale(DesigeConf.getProperty("language.Default"), DesigeConf.getProperty("country.Default"));
		ResourceBundle rb = ResourceBundle.getBundle("LoginBundle", defaultLocale);
		if (LoadSacop.edoBorrador.equalsIgnoreCase(edo)) {
			salida = rb.getString("scp.borrador");
		} else if (LoadSacop.edoEmitida.equalsIgnoreCase(edo)) {
			salida = rb.getString("scp.emitido");
		} else if (LoadSacop.edoAprobado.equalsIgnoreCase(edo)) {
			salida = rb.getString("scp.aprobado1");
		} else if (LoadSacop.edoEnEjecucion.equalsIgnoreCase(edo)) {
			salida = rb.getString("scp.inejecucion");
		} else if (LoadSacop.edoPendienteVerifSeg.equalsIgnoreCase(edo)) {
			salida = rb.getString("scp.pendiente");
		} else if (LoadSacop.edoVerificacion.equalsIgnoreCase(edo)) {
			salida = rb.getString("scp.verificacion");
		} else if (LoadSacop.edoCerrado.equalsIgnoreCase(edo)) {
			salida = rb.getString("scp.cerrado1");
		} else if (LoadSacop.edoRechazado.equalsIgnoreCase(edo)) {
			salida = rb.getString("scp.rechazado1");
		} else if (LoadSacop.edoWonderWareCerradaPreconfigurada.equalsIgnoreCase(edo)) {
			salida = rb.getString("scp.sacopPreconfiguradacerrada");
		} else if (LoadSacop.edoVerificado.equalsIgnoreCase(edo)) {
			salida = rb.getString("scp.verificado");
		}

		return salida;
	}

	private String obtenerOrigenSacop(String orig) {
		String salida = null;

		salida = HandlerProcesosSacop.getOrigen(orig);

		return salida;
	}

	/*
	 * if (pusuariosEscojidos!=null){ usuarios = HandlerDBUser.getAllUsersFilterAccion (pusuariosEscojidos.getResponsables(),usuariosSinEscojer);
	 * usuariosSinEscojer=false; Collection usuariosEscojidos = HandlerDBUser.getAllUsersFilterAccion (pusuariosEscojidos.getResponsables(),usuariosSinEscojer);
	 * putAtributte("usuariosEscojidos",usuariosEscojidos); }else{ usuarios = HandlerDBUser.getAllUsersFilter(null); }
	 */

	private void getBorradordeBaseDatos(boolean showCharge, String Idplanillasacop1, boolean esAuditoria_o_Wonderware, Plantilla1BDesqueleto formaBDesqueleto,
			HttpServletRequest request) {
		String queryUser = "";
		String areaAfectada = "";
		String Idplanillasacop2;
		try {
			// System.out.println("execute getBorradordeBaseDatos");
			// Luego de generar los scripts abajo, nos vamos por
			// el forward de planillados es planillasacop2.jsp
			Idplanillasacop2 = Idplanillasacop1;
			// ....se verifica si el suuario puede modificar la plantilla fin
			// el forward planillados envia a la pagina planillasacop2.jsp
			// este parametro lo agarra planillasacop2.jsp
			putAtributte("Idplanillasacop1", Idplanillasacop2);
			// esta coleccion trae la informacion del especifico id del sacop y
			// complementa
			// la informacion txt de cada uno de los usuarios ivolucrados

			Collection responsable = HandlerProcesosSacop.getInfResponsable("idplanillasacop1", Idplanillasacop2, showCharge, false, esAuditoria_o_Wonderware);
			Iterator it = responsable.iterator();
			if (it.hasNext()) {
				// esto cumple para la pagina planillasacop2.jsp
				Plantilla1BD obj = (Plantilla1BD) it.next();

				String fechaEmision = ToolsHTML.sdfShowWithoutHour.format(obj.getFechaemision());

				// buscamos los procesos
				Collection<Search> procesosSacop = null;
				Collection<Search> procesosSacop1 = new ArrayList<Search>();
				procesosSacop = HandlerProcesosSacop.getProcesosSacopNotIn("numproceso", obj.getProcesosafectados());
				if(obj.getProcesosafectados()!=null && !obj.getProcesosafectados().trim().equals("")) {
					procesosSacop1 = HandlerProcesosSacop.getProcesosSacop(obj.getProcesosafectados());
				} 

				// responsable de area
				Collection usuariosArea = HandlerProcesosSacop.getSolicitudResponsable("idPerson", null, showCharge, true, obj.getProcesosafectados()==null?"":obj.getProcesosafectados() );
				Collection usuariosArea1 = HandlerProcesosSacop.getSolicitudResponsableAll("idPerson", null, showCharge, true);


				/*
				if (!ToolsHTML.isEmptyOrNull(id)) {
					// obtenemos los procesos no seleccionados
					// boolean noEnsacop=true;
					// procesosSacop =
					// HandlerProcesosSacop.getProcesosSacopNotIn(security,idGrupoArea,noEnsacop,id);
					procesosSacop = HandlerProcesosSacop.getProcesosSacopNotIn("numproceso", id);
					// obtenemos los procesos seleccionados
					// System.out.println("id = " + id);
					procesosSacop1 = HandlerProcesosSacop.getProcesosSacop(id);
					// noEnsacop=false;
					// procesosSacop1 =
					// HandlerProcesosSacop.getProcesosSacopNotIn(security,idGrupoArea,noEnsacop,id);
				} else {
					String resp = String.valueOf(obj.getRespblearea());
					if (!ToolsHTML.isEmptyOrNull(resp) && !resp.equals("0")) {
						// String responsableArea=responsable;
						String query = " from person where idPerson=cast(" + resp + " as int) and accountActive='" + Constants.permission + "'";
						String idPersonArea = HandlerDocuments.getField2("idperson", query.toString());
						String idGrupoArea = HandlerDocuments.getField2("idGrupo", query.toString());
						StringBuffer idStructs = new StringBuffer(50);
						idStructs.append("1");
						Hashtable security = HandlerGrupo.getAllSecurityForGroup(idGrupoArea, idStructs);
						HandlerDBUser.getAllSecurityForUser(Long.parseLong(idPersonArea.toString()), security, idStructs);
						procesosSacop = HandlerProcesosSacop.getProcesosSacop(security, resp, idGrupoArea);
					} else {
						procesosSacop = null; // HandlerProcesosSacop.getProcesosSacop(null);
					}
					// procesosSacop =
					// HandlerProcesosSacop.getProcesosSacop(security,String.valueOf(obj.getRespblearea()),idGrupoArea);
				}
				*/
				
				Collection norms = null;
				Collection norms1 = null;
				if (!ToolsHTML.isEmptyOrNull(obj.getRequisitosaplicable())) {
					// obtenemos las normas no seleccionados
					norms = HandlerProcesosSacop.getRequisitosAplicableSacopNotIn("idNorm", obj.getRequisitosaplicable());
					// obtenemos las normas seleccionados
					norms1 = HandlerProcesosSacop.getRequisitosAplicableSacop("idNorm", obj.getRequisitosaplicable());
				} else {
					norms = HandlerProcesosSacop.getRequisitosAplicableSacop("idNorm", null);
				}
				Collection usuarios = null;
				Collection usuarios1 = null;
				if (!ToolsHTML.isEmptyOrNull(obj.getSolicitudinforma())) {
					// obtenemos los usuarios no seleccionados
					usuarios = HandlerProcesosSacop.getSolicitudinformaNotIn("idPerson", obj.getSolicitudinforma(), showCharge);
					// obtenemos los usuarios seleccionados
					usuarios1 = HandlerProcesosSacop.getSolicitudinforma("idPerson", obj.getSolicitudinforma(), showCharge, true);
				} else {
					usuarios = HandlerProcesosSacop.getSolicitudinforma("idPerson", null, showCharge, true);
				}

				plantilla1 forma = new plantilla1();
				// el usuario notificado se valido paraque sacara el area
				areaAfectada = HandlerProcesosSacop.findAreaByUsuario(String.valueOf(obj.getUsernotificado()));
				if (ToolsHTML.isEmptyOrNull(areaAfectada)) {
					areaAfectada = "-1";
				}
				removeAttribute("areaAfectada");
				removeAttribute("responsable");
				putAtributte("areaAfectada", areaAfectada);
				putAtributte("responsable", obj.getRespblearea());
				putAtributte("borradorbd", "1");
				// cargamos nuevamenete datos del emisor
				// obtenemos el numero del sacop
				forma.setSacopnum(obj.getSacopnum());
				// obtenemos el usuario que se esta notificando del sacop, esta
				// inf viene procesada de HandlerProcesosSacop.getInfResponsable
				forma.setUsernotificadotxt(HandlerDocuments.getField("nameUser", "person", "idperson", String.valueOf(obj.getUsernotificado()), "=", 1,Thread.currentThread().getStackTrace()[1].getMethodName()));
				// obtenemos el id del emisor
				forma.setUser(HandlerDocuments.getField("nameUser", "person", "idperson", String.valueOf(obj.getEmisor()), "=", 1,Thread.currentThread().getStackTrace()[1].getMethodName()));
				HandlerSacop.load(forma);

				// en caso que no sea aceptado, me trae una descripcion de
				// porque no fue aceptado
				forma.setEdodelsacop(obtenerEdoBorrador("0"));
				forma.setEdodelsacoptxt(obtenerEdoBorrador(forma.getEdodelsacop()));

				forma.setFechaEmision(ToolsHTML.sdfShowWithoutHour.format(obj.getFechaemision()));
				forma.setFechaSacops1(ToolsHTML.sdfShowWithoutHour.format(obj.getFechasacops1()));
				forma.setOrigensacop(String.valueOf(obj.getOrigensacop()));
				forma.setOrigensacoptxt(obtenerOrigenSacop(forma.getOrigensacop()));

				forma.setCorrecpreven(obj.getCorrecpreven());
				forma.setDescripcion(obj.getDescripcion());
				forma.setCausasnoconformidad(obj.getCausasnoconformidad());
				forma.setAccionrecomendada(obj.getAccionesrecomendadas());
				forma.setId(Idplanillasacop2);
				forma.setIdplanillasacop1(Idplanillasacop2);
				forma.setSacop_relacionadas(obj.getSacop_relacionadas());
				forma.setNoconformidades(obj.getNoconformidades());
				forma.setNoconformidadesref(obj.getNoconformidadesref());
				forma.setIdClasificacion(obj.getIdClasificacion());
				if (obj.getFechaWhenDiscovered() != null) {
					forma.setFechaWhenDiscovered(ToolsHTML.sdfShowWithoutHour.format(obj.getFechaWhenDiscovered()));
				}
				
				forma.setUsuarioSacops1(String.valueOf(obj.getUsuarioSacops1()));
				forma.setSolicitantetxt(obj.getSolicitantetxt());
				forma.setCargoSolicitante(obj.getCargoSolicitante());

				// ___________________________________________________________
				// WonderWare o Auditoria
				// en al caso si viene con wonderware o auditorias
				forma.setHabilitadoEsqueleto(obj.getHabilitadoEsqueleto());
				forma.setIdplanillasacop1esqueleto(obj.getIdplanillasacop1esqueleto());
				forma.setDescripcion(obj.getDescripcion() != null ? obj.getDescripcion() : "");
				if (esAuditoria_o_Wonderware) {
					// si viene de auditorias o de intouch
					if (!ToolsHTML.isEmptyOrNull(request.getParameter("origensacop"))) {
						forma.setOrigensacop(request.getParameter("origensacop"));
						forma.setOrigensacoptxt(HandlerProcesosSacop.getOrigen(request.getParameter("origensacop")));
					}

					forma.setCorrecpreven(formaBDesqueleto.getCorrecpreven());
					forma.setEstado(formaBDesqueleto.getEstado());
					Object[] tagsnamesSeleccionados = (Object[]) getSessionObject("tagsnamesSeleccionados");
					// si viene de una sacop esqueleto,verificamos que tagnames
					// agarro
					// los tagnames son colocados en la descripcion, en caso que
					// vengan de sacop-intouch o auditorias
					tagsNamesAgarrados(forma);

				} else {
					forma.setCorrecpreven(obj.getCorrecpreven());
					forma.setEstado(obj.getEstado());
				}

				forma.setIdClasificacion(obj.getIdClasificacion());
				forma.setNombreClasificacionSACOP(HandlerDocuments.getField("descripcion", "tbl_clasificacionplanillassacop", "id",
						String.valueOf(obj.getIdClasificacion()), "=", 2,Thread.currentThread().getStackTrace()[1].getMethodName()));

				forma.setIdDocumentRelated(ToolsHTML.isEmptyOrNull(obj.getIdDocumentRelated(), "0"));

				// cargamos el documento relacionado
				
				//DataUserWorkFlowForm documentoRelacionado = new DataUserWorkFlowForm();
				//if (forma.getIdDocumentRelated() != null) {
				if (forma.getNoconformidadesref() != null && !forma.getNoconformidadesref().trim().equals("")) {
					//documentoRelacionado.setIdDocument(ToolsHTML.parseInt(forma.getIdDocumentRelated()));
					//HandlerDocuments.loadDataDocument(documentoRelacionado);
					//forma.setNoconformidadesref(obj.getNoconformidadesref());
					forma.setNoConformidadesDetail(getNoConformidadesDetail(forma.getNoconformidadesref(),forma.getIdplanillasacop1()));
				}
				
				
				

				// ___________________________________________________________
				putAtributte("areaAfectada", areaAfectada);

				putAtributte("responsable", HandlerDocuments.getField("nameUser", "person", "idperson", String.valueOf(obj.getRespblearea()), "=", 1,Thread.currentThread().getStackTrace()[1].getMethodName()));
				putAtributte("borradorbd", "1");
				putAtributte("fechaEmision", fechaEmision);
				putAtributte("norms", norms);
				putAtributte("norms1", norms1);
				if(request.getAttribute("procesosSacop")==null) {
					putAtributte("procesosSacop", procesosSacop);
				}
				putAtributte("procesosSacop1", procesosSacop1);
				putAtributte("usuarioResponsable", forma);
				
				Users user = getUserSession();
				user.setPlanillaSacop(forma);

				putAtributte("usuarios", usuarios);
				putAtributte("usuarios1", usuarios1);
				putAtributte("usuariosArea", usuariosArea);
				putAtributte("usuariosArea1", usuariosArea1);
				putAtributte("sacoplantilla", forma);
				
				//request.setAttribute("documentoRelacionado", documentoRelacionado);

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}// end getBorradorPrimeravez

	// plantilla1 forma
	public void getPrimeravez(HttpServletRequest request, boolean showCharge, Users user) {
		String queryUser = "";
		String areaAfectada = "";
		String idGrupoArea = "";
		String responsableArea = "";
		String idPersonArea = "";
		String cargo = "";
		try {
			plantilla1 forma = new plantilla1();
			forma.setUser(user.getUser());
			HandlerSacop.load(forma);
			Collection norms = null;
			Collection usuarios = null;
			// agarra las normas para la solicitud de la planilla sacop
			norms = HandlerProcesosSacop.getRequisitosAplicableSacop("idNorm", null);
			// agarra los usuarios para la solicitud de la planilla sacop
			usuarios = HandlerDBUser.getAllUsersFilter(null, showCharge);
			Collection usuariosArea = HandlerDBUser.getAllUsersFilter(null, showCharge);
			
			
			if(request.getParameter("idDocRelated")!=null) {
				forma.setIdDocumentRelated(request.getParameter("idDocRelated"));
			}
			
			
			// en caso que seleccione un responsable de la planillasacop1.jsp,
			// este debe hacer un load y buscar
			// el area a la cual le pertenece
			if (!ToolsHTML.isEmptyOrNull(request.getParameter("responsable"))) {
				responsableArea = request.getParameter("responsable");
				queryUser = " from person where nameuser='" + responsableArea + "'" + " and accountActive='" + Constants.permission + "'";
				idPersonArea = HandlerDocuments.getField2("idperson", queryUser.toString());
				idGrupoArea = HandlerDocuments.getField2("idGrupo", queryUser.toString());
				cargo = HandlerDocuments.getField2("cargo", queryUser.toString());
				areaAfectada = HandlerProcesosSacop.findAreaByCargo(cargo);
				// areaAfectada=HandlerProcesosSacop.AreaUsuario(idPersonArea);
			}
			// en caso que sea la primera vez, el area afectyada debe agarrarse
			// del primer registro del
			// usuario.. o es -1 para identificar que no tiene area
			if (ToolsHTML.isEmptyOrNull(areaAfectada)) {
				Iterator it = usuarios.iterator();
				if (it.hasNext()) {
					Search u = (Search) it.next();

					queryUser = " from person where nameuser='" + u.getId() + "'" + " and accountActive='" + Constants.permission + "'";
					idPersonArea = HandlerDocuments.getField2("idperson", queryUser.toString());
					idGrupoArea = HandlerDocuments.getField2("idGrupo", queryUser.toString());
					cargo = HandlerDocuments.getField2("cargo", queryUser.toString());
					areaAfectada = HandlerProcesosSacop.findAreaByCargo(cargo);
				}
			}
			// } //TODO OJO

			// agarra los procesos para la solicitud de la planilla sacop
			StringBuffer idStructs = new StringBuffer(50);
			idStructs.append("1");
			Hashtable security = HandlerGrupo.getAllSecurityForGroup(idGrupoArea, idStructs);
			HandlerDBUser.getAllSecurityForUser(Long.parseLong(idPersonArea.toString()), security, idStructs);
			Collection procesosSacop = null;
			procesosSacop = HandlerProcesosSacop.getProcesosSacop(security, responsableArea, idGrupoArea);
			// obtenemos la fecha de hoy para llenar la fecha de emision
			java.util.Date fech = new java.util.Date();
			String fechaEmision = ToolsHTML.sdfShowWithoutHour.format(fech);
			forma.setFechaEmision(fechaEmision.toString());
			String fechaCulminacion = ToolsHTML.date.format(fech);
			// si esta realizando una nueva accion correctiva o preventiva
			// Verificamos que tipo de planilla de sacop es
			if (!ToolsHTML.isEmptyOrNull(request.getParameter("origensacop"))) {
				forma.setOrigensacop(request.getParameter("origensacop"));
				forma.setOrigensacoptxt(HandlerProcesosSacop.getOrigen(request.getParameter("origensacop")));
			}
			// el primetr edo de cualquier planilla a solicitar es tipo borrador
			forma.setEdodelsacop(obtenerEdoBorrador(LoadSacop.edoBorrador));
			if (!ToolsHTML.isEmptyOrNull(request.getParameter("correcpreven"))) {

				// Verificamos que origen de planilla sacop es
				forma.setCorrecpreven(request.getParameter("correcpreven"));
				putAtributte("correcpreventxt", rb.getString("scp.actionType".concat(forma.getCorrecpreven())));

			}
			// inicializamos estas variables por ser la primera vez..

			// si viene de una sacop esqueleto,verificamos que tagnames agarro
			// los tagnames son colocados en la descripcion, en caso que vengan
			// de sacop-intouch o auditorias
			forma.setDescripcion("");
			tagsNamesAgarrados(forma);
			// Object[]
			// tagsnamesSeleccionados=(Object[])getSessionObject("tagsnamesSeleccionados");
			// if (tagsnamesSeleccionados!=null) {
			// Object[] datos = tagsnamesSeleccionados;
			// for (int row = 0; row < datos.length; row++) {
			// Object dato = datos[row];
			// forma.setDescripcion(forma.getDescripcion()!=null?forma.getDescripcion():""+"<br>"+dato.toString());
			// }
			// }else{
			// forma.setDescripcion("");
			// }
			
			
			forma.setCausasnoconformidad("");
			forma.setAccionrecomendada("");
			forma.setSacop_relacionadas("");
			forma.setNoconformidades("");
			forma.setNoconformidadesref("");

			// colocamos el documento relacionado que ya esta preseleccionado
			forma.setNoconformidadesref(String.valueOf(request.getSession().getAttribute("idDocRelated")));
			forma.setNoConformidadesDetail(getNoConformidadesDetail(forma.getNoconformidadesref(),forma.getIdplanillasacop1()));

			String[] values = HandlerDocuments.getFields(new String[] { "nombres", "apellidos", "cargo" }, "person", "idperson",
					String.valueOf(user.getIdPerson()), false);
			
			String nombre = null;
			if (!showCharge) {
				if (values != null) {
					nombre = values[0] + " " + values[1];
					cargo = values[2];
				}
			} else {
				if (values != null) {
					nombre = values[0] + " " + values[1];
					cargo = values[2];
				}
			}
			CargoTO oCargoTO = new CargoTO();
			CargoDAO oCargoDAO = new CargoDAO();
			
			AreaTO oAreaTO = new AreaTO();
			AreaDAO oAreaDAO = new AreaDAO();
			
			oCargoTO.setIdCargo(cargo);
			oCargoDAO.cargar(oCargoTO);
			
			oAreaTO.setIdarea(oCargoTO.getIdArea());
			oAreaDAO.cargar(oAreaTO);
		
			forma.setUsuarioSacops1(String.valueOf(user.getIdPerson()));
			forma.setSolicitantetxt(nombre);
			forma.setCargoSolicitante(oCargoTO.getCargo() + " -" + oAreaTO.getArea() + "-");
			
			
			if(forma.getEstado()==0) {
				forma.setFechaSacops1(forma.getFechaEmision());
			}
			
			
			putAtributte("usuariosArea", usuariosArea);
			putAtributte("responsable", responsableArea);
			putAtributte("areaAfectada", areaAfectada);
			putAtributte("norms", norms);
			putAtributte("procesosSacop", procesosSacop);
			putAtributte("usuarios", usuarios);
			putAtributte("sacoplantilla", forma);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}// end getBorradorPrimeravez

	/**
	 * 
	 * @param forma
	 */
	public void tagsNamesAgarrados(plantilla1 forma) {
		try {
			Object[] tagsnamesSeleccionados = (Object[]) getSessionObject("tagsnamesSeleccionados");
			if (tagsnamesSeleccionados != null) {
				String variable = rb.getString("scpintouch.tagname") + "s:";
				Object[] datos = tagsnamesSeleccionados;
				if (forma.getDescripcion().indexOf(variable) != -1) {
					forma.setDescripcion(forma.getDescripcion().substring(0, forma.getDescripcion().indexOf(variable)));
				}
				boolean swPrimeravez = true;
				for (int row = 0; row < datos.length; row++) {
					Object dato = datos[row];
					if (swPrimeravez) {
						swPrimeravez = false;
						forma.setDescripcion(forma.getDescripcion() + "<br>" + variable + dato.toString());
					} else {
						forma.setDescripcion(forma.getDescripcion() + "," + dato.toString());
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 
	 * @param noConformidades
	 * @return
	 */
	public static Collection<BaseDocumentForm> getNoConformidadesDetail(String noConformidadesIds, String idPlanillaSacop) { 
		Collection<BaseDocumentForm> detail = new LinkedList<BaseDocumentForm>();

		if (!ToolsHTML.isEmptyOrNull(noConformidadesIds)) {
			Connection con = null;
			CachedRowSet rowSet = null;

			try {
				String[] ids = noConformidadesIds.split(",");
				String idOrigen = ids[ids.length-1];
				
				if(idPlanillaSacop!=null && idPlanillaSacop.trim().length()!=0) {
					StringBuffer sb = new StringBuffer("SELECT idDocumentRelated FROM tbl_planillasacop1 WHERE idplanillasacop1 = ").append(idPlanillaSacop);
					CachedRowSet crs1 = JDBCUtil.executeQuery(sb, Thread.currentThread().getStackTrace()[1].getMethodName());
					if(crs1.next()) {
						idOrigen = crs1.getString(1);
					}
				}
				
				
				final StringBuffer query = new StringBuffer("SELECT DISTINCT(d.numGen),d.number,d.nameDocument,d.prefix,td.typeDoc, vd.numVer ")
						.append("FROM documents d,typedocuments td,versiondoc vd ")
						.append("WHERE td.idTypeDoc = CAST(d.type as int) ")
						.append("AND vd.numDoc = d.numGen ")
						.append("AND d.active = '1' ")
						.append("AND CAST(d.type as int)<>1001 ")
						.append("AND vd.numVer = (SELECT MAX(numVer) FROM versiondoc vdi WHERE vdi.numDoc = d.numGen ")
						//.append(" AND vdi.statu='").append(HandlerDocuments.docApproved
						.append(") ")
						.append(" AND d.numgen IN (" + noConformidadesIds + ") ");
						//.append("AND vd.statu = '" + HandlerDocuments.docApproved).append("'");

				con = JDBCUtil.getConnection(Thread.currentThread().getStackTrace()[1].getMethodName());
				rowSet = JDBCUtil.executeQuery(query, Thread.currentThread().getStackTrace()[1].getMethodName());

				while (rowSet.next()) {
					BaseDocumentForm form = new BaseDocumentForm();
					form.setNameDocument(rowSet.getString("namedocument"));
					form.setNumber(rowSet.getString("number"));
					form.setNumVer(rowSet.getInt("numver"));
					form.setNumberGen(rowSet.getString("numgen"));
					form.setTypeDocument(rowSet.getString("typedoc"));
					if (rowSet.getString("prefix")!=null) {
						form.setPrefix((idOrigen.equals(rowSet.getString("numgen"))?"(O)-> ":"(R)-> ").concat(rowSet.getString("prefix")));
						
					} else{
						form.setPrefix((idOrigen.equals(rowSet.getString("numgen"))?"(O)-> ":"(R)-> "));
						
					}
				      
					detail.add(form);
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				try {
					rowSet.close();
				} catch (Exception e2) {
				}

				DbUtils.closeQuietly(con);
			}
		}

		return detail;
	}

	/**
	 * 
	 * @param sacopsRelacionadas
	 * @return
	 */
	private Collection<Plantilla1BD> getSacopRelacionadasDetail(String sacopsRelacionadas) {
		Collection<Plantilla1BD> detail = new LinkedList<Plantilla1BD>();

		if (!ToolsHTML.isEmptyOrNull(sacopsRelacionadas)) {
			String[] sacopIds = sacopsRelacionadas.split(",");

			try {
				for (String id : sacopIds) {
					detail.addAll(HandlerProcesosSacop.getInfResponsable("idplanillasacop1", id, false, false, false));
				}
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}
		}

		return detail;
	}
	
	public static void main(String[] args) {
		
		int[] lista = new int[]{4,5,3,2};
		
		int aux =0;
		for (int i = 0; i < lista.length; i++) {
			for (int x = 0; x < lista.length; x++) {
				if(lista[i]<lista[x]) {
					aux=lista[i];
					lista[i]=lista[x];
					lista[x]=aux;
					System.out.println(lista[0]+","+lista[1]+","+lista[2]+","+lista[3]);
				}
			}
		}
	}
}
