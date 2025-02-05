package com.desige.webDocuments.persistent.managers;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.io.StringReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.servlet.jsp.tagext.Tag;
import javax.swing.text.html.HTMLEditorKit.ParserCallback;
import javax.swing.text.html.parser.ParserDelegator;

import org.apache.commons.dbutils.DbUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.desige.webDocuments.dao.ConfDocumentoDAO;
import com.desige.webDocuments.document.actions.EditDocumentAction;
import com.desige.webDocuments.document.actions.loadsolicitudImpresion;
import com.desige.webDocuments.document.forms.BaseDocumentForm;
import com.desige.webDocuments.document.forms.BeanFirmsDoc;
import com.desige.webDocuments.document.forms.BeanRazonCambioDoc;
import com.desige.webDocuments.document.forms.CheckOutDocForm;
import com.desige.webDocuments.document.forms.DocumentsCheckOutsBean;
import com.desige.webDocuments.document.forms.DocumentsRelation;
import com.desige.webDocuments.document.forms.MoveDocForm;
import com.desige.webDocuments.files.forms.DocumentForm;
import com.desige.webDocuments.mail.actions.SendMailTread;
import com.desige.webDocuments.mail.forms.MailForm;
import com.desige.webDocuments.persistent.utils.JDBCUtil;
import com.desige.webDocuments.seguridad.forms.PermissionUserForm;
import com.desige.webDocuments.structured.forms.BaseStructForm;
import com.desige.webDocuments.structured.forms.DataHistoryStructForm;
import com.desige.webDocuments.to.UserFlexWorkFlowsTO;
import com.desige.webDocuments.utils.ApplicationExceptionChecked;
import com.desige.webDocuments.utils.Constants;
import com.desige.webDocuments.utils.DesigeConf;
import com.desige.webDocuments.utils.IDDBFactorySql;
import com.desige.webDocuments.utils.StringUtil;
import com.desige.webDocuments.utils.ToolsHTML;
import com.desige.webDocuments.utils.beans.BeanNotifiedWF;
import com.desige.webDocuments.utils.beans.Search;
import com.desige.webDocuments.utils.beans.Users;
import com.desige.webDocuments.workFlows.forms.DataUserWorkFlowForm;
import com.desige.webDocuments.workFlows.forms.ParticipationForm;
import com.focus.qweb.bean.Person;
import com.focus.qweb.dao.PlanAuditDAO;
import com.focus.qweb.dao.ProgramAuditDAO;
import com.focus.qweb.to.PlanAuditTO;
import com.focus.qweb.to.ProgramAuditTO;
import com.focus.util.Archivo;
import com.sun.star.uno.Any;

import sun.jdbc.rowset.CachedRowSet;

/**
 * Title: HandlerDocuments.java <br/>
 * Copyright: (c) 2004 Focus Consulting C.A.<br/>
 * 
 * @author Ing. Nelson Crespo
 * @author Ing. Simón Rodriguéz. (SR)
 * @version WebDocuments v3.0 <br/>
 *          Changes:<br/>
 *          <ul>
 *          <li>27-08-2004 (NC) Creation</li>
 *          <li>22-05-2005 (NC) Cambios en el manejo del Check In & el Check Out
 *          </li>
 *          <li>01/06/2005 (NC) Cambios para agregar algún comentario al hacer
 *          roll Back</li>
 *          <li>01/07/2005 (SR) Se creo y uso un procedimiento getAllNormas para
 *          obetner todas las normas</li>
 *          <li>04/07/2005 (SR) Se agrego un filtro AND en el query para obtener
 *          las fechas de publicacion menor o igual a las fecha de aprovados.</li>
 *          <li>07/07/2005 (SR) Se valido el query para validar que los
 *          documentos publicados sean solo tipo formato, politica y
 *          procedimiento</li>
 *          <li>07/07/2005 (SR) Se creo TypeDocumentsProcedimiento,Type
 *          documents Formato,TypeDocumentsPolitica</li>
 *          <li>13/07/2005 (SR) Se llama a este proc
 *          HandlerWorkFlows.eraseWorkFlowsDocuments(String.valueOf(idDocument))
 *          para eliminar la informacion de flujos de trabajo en menu principal
 *          ligada al documento a eliminar</li>
 *          <li>14/07/2005 (SR) La busqueda se arreglo para todos los estatus
 *          (Rechazado,Obsoleto,Borrador,revisado,aprobado, en aprobación, en
 *          revision)</li>
 *          <li>14/07/2005 (SR) La busqueda se arreglo para todos los estatus
 *          (Eliminados)</li>
 *          <li>22/07/2005 (SR) Se creo el metodo getAllDocumentsExpiresUser</li>
 *          <li>25/07/2005 (SR) Se creo un nuevo metodo polimorfismo
 *          getDocumentsPublished</li>
 *          <li>27/07/2005 (SR) Se hizo un metodo para actualizar el num
 *          correlativo updateNumCorrelativo</li>
 *          <li>28/07/2005 (SR) Se modifico el metodo movementDocument para que
 *          los archivos se muevan con su prefijo nuevo y su numero relativo sea
 *          acorde a su localidad.</li>
 *          <li>16/11/2005 (NC) se modificó el método getLastVersions</li>
 *          <li>21/02/2006 (SR) se modifico updateStateDoc para agregar un valor
 *          a un campo nuevo de versiondoc, que es el historico</li>
 *          <li>21/04/2006 (SR) Se coloco synchronized algunos metodos para no
 *          bloquear la bd.</li>
 *          <li>21/04/2006 (SR) Se cambio el commit en
 *          getAllNotifDocumentsExpires.</li>
 *          <li>24/04/2006 (SR) Se cambio la busquedad de publicados para buscar
 *          tambien por nodo padre.</li>
 *          <li>03/05/2006 La bandeja de entrada de los mails no funcionaba, era
 *          vulnerable en caso que un usuario cambiara su mail, y en cada mail
 *          se manda el numero del documento en el titulo (SR)</li>
 *          <li>04/05/2006 (SR) Por peticion de vicson, los documentos tipo
 *          registros si se deben publicar.</li>
 *          <li>30/06/2006 (NC) uso del Log y cambios para documentos Vinculados
 *          </li>
 *          <li>03/07/2006 (SR) Se quito el comentario a deleteDocstr para
 *          eliminar docs Oboletos y Borradores.</li>
 *          <li>13/07/2006 (NC) Cambios para heredar o no los Prefijos de las
 *          Carpetas</li>
 *          <li>27/07/2006 (SR) Se corrigio bugs en checkOutCheckIn</li>
 *          </ul>
 */
public class HandlerDocuments extends HandlerBD {
	/**
	 * 
	 */
	private static final long serialVersionUID = 6204555810060599149L;
	static Logger log = LoggerFactory.getLogger("[V4.0 FTP] "
			+ HandlerDocuments.class.getName());
	public static final String docTrash = "1";
	public static final String docInReview = "2";
	public static final String docReview = "3";
	public static final String docInApproved = "4";
	public static final String docApproved = "5";
	// public static final String docInReviewEnd = "6"; // El Documento se
	// encuentra en Revisi�n
	// // pero ya sido respondido el flujo x todos los Usuarios
	// public static final String docInApprovedEnd = "7"; // El Documento se
	// encuentra en Revisi�n
	// pero ya sido respondido el flujo x todos los Usuarios
	public static final String docBorrador = "1";

	public static final String docObs = "6";
	public static final String docRejected = "7";
	public static final String docErase = "8";
	public static final String docCreate = "1";
	public static final String docMovement = "2";
	public static final String docDelete = "3";
	public static final String docCheckOut = "4";
	public static final String docCheckIn = "5";
	public static final String docBackCheckOut = "6";
	public static final String docBackCheckIn = "7";
	public static final String deleteTrash = "11";
	public static final String versionDownload = "21";

	// Operaciones aplicadas sobre un Documento
	public static final String lastCreate = "0";
	public static final String lastCheckOut = "1";
	public static final String lastBackCheckOut = "2";
	public static final String lastCheckIn = "3";
	public static final String lastBackCheckIn = "4";
	public static final String lastInReview = "5";
	public static final String lastBackInReview = "6";
	public static final String lastInApproved = "7";
	public static final String lastBackInApproved = "8";
	public static final String lastDelete = "9";
	public static final String lastReview = "10";
	public static final String lastApproved = "11";
	public static final String lastCanceledWF = "12";
	public static final String lastExpireWF = "13";
	public static final String lastDeleteTrash = "14";
	public static final String lastRejectedWF = "14";
	public static final String inFlewFlow = "15";
	public static final String lastCompletedWF = "20";

	public static final String docActives = "1";
	public static final String docInactives = "0";

	// Tipos de procedimientos.
	public static final String TypeDocumentsProcedimiento = "3";
	public static final String TypeDocumentsImpresion = "1001"; // (Tipo
																// Impresion)
	public static final String TypeDocumentsPolitica = "1";
	public static final String TypeDocumentsRegistro = "1002"; // Registro(Tipo 
																// Registro) 

	// TYPOS DE DOCUMENTOS (se refieren al campo type_Formato del la tabla
	// typeDocuments)
	public static final String TYPE_DOCUMENT_NINGUNO = "0";
	public static final String TYPE_DOCUMENT_PLANTILLA = "1";
	public static final String TYPE_DOCUMENT_FORMATO = "2";
	
	/* ydavila CLASIFICACI�N DE NORMAS ISO
	   Ticket 001-00-003168 Lista Maestra. Secci�n Filtros. Ampliar el filtro tipo 
	    de documentos para que aparezca la palabra reservada "Registro Tipo Registro" */
	/*
  //public static final int ISO90012000  = 1000; obsoleta
	public static final int BPM82332     = 1000; // se reutiliza la numeraci�n de la ISO90012000
	public static final int ISO90012008  = 1500;
	public static final int ISO90012015  = 2000; 
	public static final int ISO140012004 = 2500;
	public static final int ISO151892007 = 3000; 
	public static final int ISO180012008 = 3500;
	public static final int ISO220002005 = 4000; 
	public static final int ISO270012013 = 4500; 
	public static final int ISO171892003 = 5000;
	public static final int ISO169492009 = 5500;
	public static final int ISO170252005 = 6000;
	public static final int BASC2012     = 6500;
	public static final int COL10722015  = 7000;
	public static final int PER29783     = 7500;
	*/
	
	public static int valorIsoIni = 0;
	public static int valorIsoFin = 0;

	// 26 DE JULIO 2005 INICIO SIMON
	/**
	 * Este metodo me coloca el numero correlatio a un documento.
	 */
	public static void updateNumCorrelativo(Connection con,
			String numCorrelativo, int numDoc) throws SQLException {
		try {
			StringBuilder sql = new StringBuilder(
					"UPDATE documents SET number = ? ");
			sql.append(" WHERE numGen =").append(numDoc);
			log.debug("[updateNumCorrelativo]" + sql.toString());
			PreparedStatement st = con.prepareStatement(JDBCUtil.replaceCastMysql(sql.toString()));
			st.setString(1, numCorrelativo);
			st.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 
	 * @param con
	 * @param numDoc
	 * @param statusimpresion
	 * @throws SQLException
	 */
	public static void verificarDocImpresosNuevaVersion(Connection con,
			int numDoc, int statusimpresion) throws SQLException {
		PreparedStatement st = null;
		ResultSet rs = null;
		try {
			Locale defaultLocale = new Locale(
					DesigeConf.getProperty("language.Default"),
					DesigeConf.getProperty("country.Default"));
			ResourceBundle rb = ResourceBundle.getBundle("LoginBundle",
					defaultLocale);

			StringBuilder sql = new StringBuilder(2048)
					.append("SELECT tblsimp.numsolicitud,tblsimp.datesolicitud,d.numGen,d.prefix,d.number,d.IdNode,d.nameDocument,vd.MayorVer,vd.MinorVer, ")
					.append("psolicitud.emailsolicitante,psolicitud.namePersonSolicitante, ")
					.append("pautorizante.emailautorizante,pautorizante.namePersonautorizante,tblsimp.comments ")
					.append("FROM documents d,versiondoc vd,tbl_solicitudimpresion tblsimp , ");
			switch (Constants.MANEJADOR_ACTUAL) {
			case Constants.MANEJADOR_MSSQL:
				sql.append(
						"     (select  p.email as emailsolicitante,(p.Apellidos+' '+p.Nombres) AS namePersonSolicitante,p.idperson from person p )psolicitud, ")
						.append("     (select  p.email as emailautorizante ,(p.Apellidos+' '+p.Nombres) AS namePersonautorizante,p.idperson from person p )pautorizante ");
				break;
			case Constants.MANEJADOR_POSTGRES:
				sql.append(
						"     (select  p.email as emailsolicitante,(p.Apellidos||' '||p.Nombres) AS namePersonSolicitante,p.idperson from person p )psolicitud, ")
						.append("     (select  p.email as emailautorizante ,(p.Apellidos||' '||p.Nombres) AS namePersonautorizante,p.idperson from person p )pautorizante ");
				break;
			case Constants.MANEJADOR_MYSQL:
				sql.append(
						"     (select  p.email as emailsolicitante,CONCAT(p.Apellidos,' ',p.Nombres) AS namePersonSolicitante,p.idperson from person p )psolicitud, ")
						.append("     (select  p.email as emailautorizante ,CONCAT(p.Apellidos,' ',p.Nombres) AS namePersonautorizante,p.idperson from person p )pautorizante ");
				break;
			}
			sql.append(" WHERE  d.numGen = vd.numDoc ")
					.append(" AND vd.numVer = tblsimp.numVer ")
					.append(" AND d.numGen = tblsimp.numgen ")
					.append(" AND psolicitud.idPerson = tblsimp.solicitante ")
					.append(" AND pautorizante.idPerson = tblsimp.autorizante ")
					.append(" AND tblsimp.numgen= ").append(numDoc)
					.append(" AND tblsimp.statusimpresion= ")
					.append(statusimpresion).append(" AND tblsimp.active= '")
					.append(Constants.permission).append("'")
					.append(" AND tblsimp.destinatarios IS NOT NULL "); // solo
																		// las
																		// controladas
			log.debug("[verificarDocImpresosNuevaVersion]" + sql.toString());
			st = con.prepareStatement(JDBCUtil.replaceCastMysql(sql.toString()));
			rs = st.executeQuery();
			StringBuilder mensaje = new StringBuilder("");
			while (rs.next()) {
				BeanNotifiedWF userMailExp = new BeanNotifiedWF();
				userMailExp
						.setEmail((rs.getString("emailsolicitante") != null ? rs
								.getString("emailsolicitante") : HandlerParameters.PARAMETROS.getMailAccount()));
				String nombre = rs.getString("namePersonSolicitante") != null ? rs
						.getString("namePersonSolicitante") : "";
				String nombreAutorizante = rs
						.getString("namePersonautorizante") != null ? rs
						.getString("namePersonautorizante") : "";
				String namedocument = rs.getString("namedocument") != null ? rs
						.getString("namedocument") : "";
				String version = rs.getString("MayorVer") + "."
						+ rs.getString("MinorVer");
				String fechaSolicitud = ToolsHTML.formatDateShow(
						rs.getString("datesolicitud").toString(), false);

				String respuesta = "";// rb.getString("imp.mail.nuevaversiondocumento");
				String titleimp = rb.getString("imp.title");
				String prefixNumber = rs.getString("prefix") != null ? rs
						.getString("prefix") : "";
				String number = rs.getString("number") != null ? rs
						.getString("number") : "";
				prefixNumber = prefixNumber + number;

				mensaje.setLength(0);
				mensaje.append(rb.getString("imp.mailcaducosolicitante"))
						.append(" ")
						.append(rb.getString("imp.sr_a"))
						.append(" ")
						.append(nombre)
						.append(" ")
						.append("<br/>")
						.append(rb.getString("imp.mailcaducoautorizante"))
						.append(" ")
						.append(nombreAutorizante)
						.append(" ")
						.append("<br/>")
						// mensaje.append(respuesta).append(" ").append("<br/>");
						.append(rb.getString("imp.versanterdocimpreso"))
						.append(" ")
						.append(namedocument)
						.append(" ")
						.append(prefixNumber)
						.append(" ")
						.append(rb.getString("mail.docsversion"))
						.append(" ")
						.append(version)
						.append("<br>")
						.append(rs.getString("comments") != null ? rs
								.getString("comments") : "")
						.append(rb.getString("im.fechasolicitud")).append(" ")
						.append(fechaSolicitud);
				userMailExp.setComments(mensaje.toString());

				// manda mensaje por correo al solicitante
				HandlerWorkFlows.notifiedUsers(titleimp + " - " + prefixNumber,
						rb.getString("mail.nameUser"), HandlerParameters.PARAMETROS.getMailAccount(), userMailExp.getEmail(), userMailExp
								.getComments());

				// manda mensaje por correo al autorizante
				if (rs.getString("emailautorizante") != null
						&& !rs.getString("emailautorizante").equals(
								userMailExp.getEmail())) {
					userMailExp.setEmail(rs.getString("emailautorizante"));
					HandlerWorkFlows.notifiedUsers(titleimp + " - "
							+ prefixNumber, rb.getString("mail.nameUser"),
							HandlerParameters.PARAMETROS.getMailAccount(), userMailExp
									.getEmail(), userMailExp.getComments());
				}
			}

			sql.setLength(0);
			sql.append("UPDATE tbl_solicitudimpresion SET active=? ")
					.append(" WHERE numgen =").append(numDoc)
					.append(" AND statusimpresion= '").append(statusimpresion)
					.append("'").append(" AND active= '")
					.append(Constants.permission).append("'");
			log.debug("[updatetblSolicitudImpresion]" + sql.toString());
			st = con.prepareStatement(JDBCUtil.replaceCastMysql(sql.toString()));
			st.setInt(1, Constants.notPermission);
			st.executeUpdate();
		} catch (Exception e) {
			log.error(e.getMessage());
			e.printStackTrace();
		} finally {
			JDBCUtil.closeQuietly(st, rs);
		}
	}

	/**
	 * 
	 * @param con
	 * @param numCorrelativo
	 * @param numDoc
	 * @param statusautorizante
	 * @param statusimpresion
	 * @throws SQLException
	 */
	public static void updatetblSolicitudImpresion(Connection con,
			String numCorrelativo, int numDoc, int statusautorizante,
			int statusimpresion) throws SQLException {

		PreparedStatement st = null;
		ResultSet rs = null;
		try {
			Locale defaultLocale = new Locale(
					DesigeConf.getProperty("language.Default"),
					DesigeConf.getProperty("country.Default"));
			ResourceBundle rb = ResourceBundle.getBundle("LoginBundle",
					defaultLocale);
			StringBuilder sql = new StringBuilder(
					"UPDATE tbl_solicitudimpresion SET statusautorizante=? ,statusimpresion= ?");
			sql.append(" WHERE numsolicitud =").append(numDoc);
			log.debug("[updatetblSolicitudImpresion]" + sql.toString());
			st = con.prepareStatement(JDBCUtil.replaceCastMysql(sql.toString()));
			st.setInt(1, statusautorizante);
			st.setInt(2, statusimpresion);
			st.executeUpdate();

			sql.setLength(0);
			sql.append(
					"SELECT tblsimp.numsolicitud,tblsimp.datesolicitud,d.numGen,d.prefix,d.number,d.IdNode,d.nameDocument,vd.MayorVer,vd.MinorVer, ")
					.append("psolicitud.emailsolicitante,psolicitud.namePersonSolicitante, ")
					.append("pautorizante.emailautorizante,pautorizante.namePersonautorizante,tblsimp.copias ")
					.append("FROM documents d,versiondoc vd,tbl_solicitudimpresion tblsimp , ");
			switch (Constants.MANEJADOR_ACTUAL) {
			case Constants.MANEJADOR_MSSQL:
				sql.append(
						"     (select  p.email as emailsolicitante,(p.Apellidos+' '+p.Nombres) AS namePersonSolicitante,p.idperson from person p )psolicitud, ")
						.append("     (select  p.email as emailautorizante ,(p.Apellidos+' '+p.Nombres) AS namePersonautorizante,p.idperson from person p )pautorizante ");
				break;
			case Constants.MANEJADOR_POSTGRES:
				sql.append(
						"     (select  p.email as emailsolicitante,(p.Apellidos||' '||p.Nombres) AS namePersonSolicitante,p.idperson from person p )psolicitud, ")
						.append("     (select  p.email as emailautorizante ,(p.Apellidos||' '||p.Nombres) AS namePersonautorizante,p.idperson from person p )pautorizante ");
				break;
			case Constants.MANEJADOR_MYSQL:
				sql.append(
						"     (select  p.email as emailsolicitante,CONCAT(p.Apellidos,' ',p.Nombres) AS namePersonSolicitante,p.idperson from person p )psolicitud, ")
						.append("     (select  p.email as emailautorizante ,CONCAT(p.Apellidos,' ',p.Nombres) AS namePersonautorizante,p.idperson from person p )pautorizante ");
				break;
			}
			sql.append(" WHERE  d.numGen = vd.numDoc ")
					.append(" AND vd.numVer = tblsimp.numVer ")
					.append(" AND d.numGen = tblsimp.numgen ")
					.append(" AND psolicitud.idPerson = tblsimp.solicitante ")
					.append(" AND pautorizante.idPerson = tblsimp.autorizante ")
					.append(" AND tblsimp.numsolicitud= ").append(numDoc);
			log.debug("[querySolicitudImpresion]" + sql.toString());

			st = con.prepareStatement(JDBCUtil.replaceCastMysql(sql.toString()));
			rs = st.executeQuery();
			if (rs.next()) {
				BeanNotifiedWF userMailExp = new BeanNotifiedWF();
				userMailExp
						.setEmail((rs.getString("emailsolicitante") != null ? rs
								.getString("emailsolicitante") : HandlerParameters.PARAMETROS.getMailAccount()));
				String nombre = rs.getString("namePersonSolicitante") != null ? rs
						.getString("namePersonSolicitante") : "";
				String nombreAutorizante = rs
						.getString("namePersonautorizante") != null ? rs
						.getString("namePersonautorizante") : "";
				String namedocument = rs.getString("namedocument") != null ? rs
						.getString("namedocument") : "";
				String version = rs.getString("MayorVer") + "."
						+ rs.getString("MinorVer");
				String fechaSolicitud = ToolsHTML.formatDateShow(
						rs.getString("datesolicitud").toString(), false);
				String respuesta = null;
				String titleimp = null;

				String prefixNumber = rs.getString("prefix") != null ? rs
						.getString("prefix") : "";
				String number = rs.getString("number") != null ? rs
						.getString("number") : "";
				String numberCopies = rs.getString("copias") != null ? rs
						.getString("copias") : "1";
				int numCopies = 1;
				numCopies = HandlerDocuments.getNumeroCopias(numberCopies);

				if (statusautorizante == Integer
						.parseInt(loadsolicitudImpresion.rechazadoprintln)) {
					respuesta = rb.getString("imp.mail.rechazado_autorizante");
					titleimp = rb
							.getString("imp.respuest.solicitudimpresionrechaz");
				} else if (statusautorizante == Integer
						.parseInt(loadsolicitudImpresion.aprobadoprintln)) {
					respuesta = rb.getString("imp.mail.aceptado_autorizante");
					titleimp = rb
							.getString("imp.respuest.solicitudimpresionaprob");
				}
				prefixNumber = prefixNumber + number;

				StringBuilder mensaje = new StringBuilder(2048)
						.append(rb.getString("imp.sr_a")).append(" ")
						.append(nombre).append(" <br>")
						.append(rb.getString("imp.resultado")).append(" ")
						.append(respuesta).append(" <br>")
						.append(rb.getString("imp.por")).append(" ")
						.append(nombreAutorizante).append(" <br>")
						.append(rb.getString("imp.paradoc")).append("<br>")
						.append(namedocument).append(" ").append(prefixNumber)
						.append(" ").append(rb.getString("mail.docsversion"))
						.append(" ").append(version).append("<br>")
						.append(rb.getString("im.fechasolicitud")).append(" ")
						.append(fechaSolicitud).append("<br>")
						.append(rb.getString("imp.copias")).append(": ")
						.append(numCopies);
				userMailExp.setComments(mensaje.toString());

				// manda mensaje por correo al solicitante
				HandlerWorkFlows.notifiedUsers(titleimp + " " + prefixNumber,
						rb.getString("mail.nameUser"), HandlerParameters.PARAMETROS.getMailAccount(), userMailExp.getEmail(), userMailExp
								.getComments());
				// manda mensaje por correo al autorizante
				userMailExp
						.setEmail((rs.getString("emailautorizante") != null ? rs
								.getString("emailautorizante") : HandlerParameters.PARAMETROS.getMailAccount()));
				HandlerWorkFlows.notifiedUsers(titleimp + " " + prefixNumber,
						rb.getString("mail.nameUser"), HandlerParameters.PARAMETROS.getMailAccount(), userMailExp.getEmail(), userMailExp
								.getComments());
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			JDBCUtil.closeQuietly(st, rs);
		}
	}

	/**
	 * 
	 * @param sCopias
	 * @return
	 */
	public static int getNumeroCopias(String sCopias) {
		int numeroCopias = 0;

		try {
			if (sCopias != null && !sCopias.trim().equals("")
					&& !sCopias.trim().equals("0")) {
				for (StringTokenizer copias = new StringTokenizer(sCopias, ","); copias
						.hasMoreElements();) {
					String numero = (String) copias.nextElement();
					if (ToolsHTML.isNumeric(numero)) {
						numeroCopias = numeroCopias + Integer.parseInt(numero);
					}
				}
			} else {
				numeroCopias = 1;
			}
		} catch (Exception e) {
			// System.out.println("Numero de copias no se pudo calcular correctacmente getNumberoCopias(): "
			// + e);
			numeroCopias = 1;
		}
		return numeroCopias;
	}

	/**
	 * 
	 * @param forma
	 * @throws SQLException
	 */
	public static void verifWokflowCerroConExito(BaseDocumentForm forma)
			throws SQLException {
		Connection con = null;
		PreparedStatement st = null;
		ResultSet rs = null;
		try {
			StringBuilder sql = new StringBuilder(
					"SELECT uw.statu,uw.result FROM user_workflows uw WHERE ")
					.append(" uw.idWorkFlow = (SELECT MAX(idworkflow) FROM workflows WHERE idDocument = ")
					.append(forma.getIdDocument())
					.append(") ORDER BY uw.isowner");

			con = JDBCUtil.getConnection(Thread.currentThread().getStackTrace()[1].getMethodName());
			con.setAutoCommit(false);
			st = con.prepareStatement(JDBCUtil.replaceCastMysql(sql.toString()));
			rs = st.executeQuery();
			boolean Sw = false;

			while ((rs.next()) && !Sw) {
				// siempre empezamos en falso para ver si el ultimo resultset
				// cumple con las
				// condiciones siguientes
				// res.getString("numgen");
				if ((rs.getString("statu")
						.equalsIgnoreCase(HandlerWorkFlows.wfuAcepted))
						|| (rs.getString("statu")
								.equalsIgnoreCase(HandlerWorkFlows.wfuCanceled))
						|| (rs.getString("statu")
								.equalsIgnoreCase(HandlerWorkFlows.wfuClosed))
						|| (rs.getString("statu")
								.equalsIgnoreCase(HandlerWorkFlows.wfuExpired))
						|| (rs.getString("statu")
								.equalsIgnoreCase(HandlerWorkFlows.wfuVencido))
						&& (rs.getString("statu") == rs.getString("result"))) {
					// el resultset esta cerrado o expirado o ... y estamos
					// buscando que sea el
					// ultimo
					// Sw = false;
				} else {
					// si es verdadero, quiere decir que el flujo se esta
					// desarrollando normalmente
					Sw = true;
				}
			}

			JDBCUtil.closeQuietly(st, rs);

			// el flujo ya se completo, ahora chequeamos si el flujo a cerrado
			// con exito para las
			// demas tablas
			if (!Sw) {
				sql.setLength(0);
				sql.append(
						"select type, statu,result from workflows where iddocument=")
						.append(forma.getIdDocument()).append(" and statu='")
						.append(HandlerWorkFlows.pending).append("' ")
						.append(" and statu=result");
				st = con.prepareStatement(JDBCUtil.replaceCastMysql(sql.toString()));
				rs = st.executeQuery();
				// si esta pendiente el flujo, y en user workflow esta ya
				// completado, quiere decir
				// que el flujo
				// cerro mal
				if (rs.next()) {
					// actualizamos la table Userworkflows
					StringBuilder userWF = new StringBuilder(
							"Update user_workflows set statu=")
							.append(HandlerWorkFlows.wfuCanceled).append(",")
							.append("result=")
							.append(HandlerWorkFlows.wfuCanceled)
							.append(" where idworkflow in ")
							.append(" (select idworkflow from workflows ")
							.append(" where iddocument=")
							.append(forma.getIdDocument()).append(")");
					log.debug("actualizamos table Userworkflows="
							+ userWF.toString());
					st = con.prepareStatement(JDBCUtil.replaceCastMysql(userWF.toString()));
					st.executeUpdate();
					// actualizamos la table workflows
					String type = rs.getString("type");
					StringBuilder updateWF = new StringBuilder(
							"Update workflows set statu=")
							.append(HandlerWorkFlows.wfuAcepted).append(",")
							.append("result=")
							.append(HandlerWorkFlows.wfuAcepted)
							.append(" where iddocument=")
							.append(forma.getIdDocument());
					log.debug("actualizamos table workflows="
							+ updateWF.toString());
					st = con.prepareStatement(JDBCUtil.replaceCastMysql(updateWF.toString()));
					st.executeUpdate();

					// actualizamos la tabla documents
					StringBuilder actStado = new StringBuilder(
							!"0".equalsIgnoreCase(type) ? HandlerDocuments.docReview
									: HandlerDocuments.docApproved);
					StringBuilder doc = new StringBuilder(
							"update documents set statu=").append(actStado);
					if (actStado.toString().equalsIgnoreCase(
							HandlerDocuments.docReview)) {
						doc.append(" ,lastOperation=").append(
								HandlerDocuments.lastReview);
					} else {
						doc.append(" ,lastOperation=").append(
								HandlerDocuments.lastApproved);
					}
					doc.append(" where numgen=").append(forma.getIdDocument());
					log.debug("actualizamos table documents="
							+ updateWF.toString());
					st = con.prepareStatement(JDBCUtil.replaceCastMysql(doc.toString()));
					st.executeUpdate();
				}
			}
			con.setAutoCommit(true);
		} catch (Exception e) {
			log.error(e.getMessage());
			e.printStackTrace();
		} finally {
			JDBCUtil.closeQuietly(con, st, rs);
		}
	}

	// 26 DE JULIO 2005 FIN SIMON

	/**
	 * Este m�todo permite actualizar el estado del documento, indicando cuando
	 * ha sido sometido a un flujo, o cuando ha finalizado el transcurso del
	 * mismo
	 * 
	 * @param con
	 * @param numDoc
	 * @param newState
	 * @throws SQLException
	 */
	// updateStateDoc(con,idDocument,docObs,statuDoc,lastDelete,0,null);
	public static void updateStateDoc(Connection con, int numDoc,
			String newState, String statuAnt, String lastOperation,
			long idVersion, Timestamp datePublic) throws SQLException {
		StringBuilder sql = new StringBuilder(
				"UPDATE documents SET statu = ?,statuAnt = ? ");
		String statu = "";
		if (newState != null) {
			statu = newState.trim();
		}
		if ((HandlerDocuments.docApproved.equalsIgnoreCase(statu))
				&& (datePublic != null)) {
			sql.append(",docPublic = '0',datePublic =  ? , versionPublic = ?, lastVersionApproved = ?");
		}
		if (!ToolsHTML.isEmptyOrNull(lastOperation)) {
			sql.append(",lastOperation = ?");
		}
		sql.append(" WHERE numGen = ? ");
		PreparedStatement st = con.prepareStatement(JDBCUtil.replaceCastMysql(sql.toString()));
		st.setString(1, newState);
		st.setString(2, statuAnt);
		int pos = 3;
		if (HandlerDocuments.docApproved.equalsIgnoreCase(newState)
				&& datePublic != null) {
			st.setTimestamp(pos++, datePublic);
			st.setLong(pos++, idVersion);
			st.setLong(pos++, idVersion);
		}
		if (!ToolsHTML.isEmptyOrNull(lastOperation)) {
			st.setString(pos++, lastOperation);
			st.setInt(pos++, numDoc);
		} else {
			st.setInt(pos++, numDoc);
		}
		// //System.out.println("updateStateDoc sql: " + sql.toString() +
		// " ///newState " + newState + " statuAnt " + statuAnt);
		st.executeUpdate();

		// 2005-09-14 de septiembre inicio
		if (statuAnt != null
				&& statuAnt.equalsIgnoreCase(HandlerDocuments.docRejected)) {
			StringBuilder sql1 = new StringBuilder(
					"UPDATE versiondoc  SET statu =  ")
					.append(HandlerDocuments.docTrash);
			sql1.append(",statuhist=").append(newState);
			sql1.append(
					" WHERE numVer=(Select max(numVer) from versiondoc where numDoc=")
					.append(numDoc).append(")").append(" AND statu='")
					.append(HandlerDocuments.docRejected).append("' ");
			log.debug("[sql1]" + sql1);
			st = con.prepareStatement(JDBCUtil.replaceCastMysql(sql1.toString()));
			st.executeUpdate();
		}
		// 2005-09-14 de septiembre fin
	}
	
	// ydavila Ticket 001-00-002916 Principal - Lista de Distribucion aparecen documentos eliminados y obsoletos
	// Elimina pendientes de visualizaci�n en Lista de Distribuci�n cuando el documento se elimina o pasa a obsoleto
	public static void deletelistdistdocument(Connection con, int numDoc) 
			throws SQLException {
		StringBuilder sql = new StringBuilder(
				"DELETE from listdistdocument where iddocument = ");
				sql.append(numDoc);
		PreparedStatement st = con.prepareStatement(JDBCUtil.replaceCastMysql(sql.toString()));
		st.executeUpdate();
	}
	
	/**
	 * Este m�todo permite obtener el Sttu Actual del documento indicado
	 * 
	 * @param idDocument
	 * @return
	 */
	public static String getFieldToDocument(String field, int idDocument)
			throws ApplicationExceptionChecked {
		StringBuilder sql = new StringBuilder(50);
		sql.append("SELECT ").append(field)
				.append(" FROM documents WHERE numGen = ").append(idDocument);
		log.debug("[getStatuDocWorkFlow] = " + sql);
		try {
			Properties datos = JDBCUtil.doQueryOneRow(sql.toString(),Thread.currentThread().getStackTrace()[1].getMethodName());
			if (datos.getProperty("isEmpty").equalsIgnoreCase("false")) {
				return datos.getProperty(field);
			} else {
				throw new ApplicationExceptionChecked("E0014");
			}
		} catch (ApplicationExceptionChecked ae) {
			log.error(ae.getMessage());
			throw ae;
		} catch (Exception ex) {
			log.error(ex.getMessage());
			ex.printStackTrace();
			throw new ApplicationExceptionChecked("E0014");
		}
	}

	public static String getDateExpiresDoc(int idDocument)
			throws ApplicationExceptionChecked {
		StringBuilder sql = new StringBuilder(50);
		sql.append(
				"SELECT dateExpires FROM versiondoc WHERE dateExpires is NOT NULL AND numDoc = ")
				.append(idDocument);
		try {
			log.debug("[getDateExpiresDoc] = " + sql.toString());
			Vector datos = JDBCUtil.doQueryVector(sql.toString(),Thread.currentThread().getStackTrace()[1].getMethodName());
			if (datos.size() > 0) {
				Properties prop = (Properties) datos.get(0);
				if (!ToolsHTML.isEmptyOrNull(prop.getProperty("dateExpires"))) {
					return prop.getProperty("dateExpires");
				}
				return null;
			} else {
				return null;
			}
		} catch (Exception ex) {
			log.error(ex.getMessage());
			ex.printStackTrace();
			throw new ApplicationExceptionChecked("E0014");
		}
	}

	public static boolean isDocumentRelations(int idDoc) throws Exception {
		StringBuilder query = new StringBuilder(
				"SELECT numGenLink FROM documentslinks");
		query.append(" WHERE numGenLink = ").append(idDoc)
				.append(" OR numGen=").append(idDoc);
		Vector datos = JDBCUtil.doQueryVector(query.toString(),Thread.currentThread().getStackTrace()[1].getMethodName());
		return datos.size() > 0;
	}

	public static boolean isDocumentStatuVersionEraser(int idDoc)
			throws Exception {
		StringBuilder query = new StringBuilder(
				"select statu from versiondoc where numdoc=? and statu=? and numver=(select max(numver) from versiondoc where numdoc=?)");
		ArrayList parametros = new ArrayList();
		parametros.add(new Integer(idDoc));
		parametros.add(HandlerDocuments.docBorrador);
		parametros.add(new Integer(idDoc));
		return JDBCUtil.executeQuery(query, parametros, Thread.currentThread().getStackTrace()[1].getMethodName()).next();
	}

	public static boolean isDocumentStatuVersionEraser(int idDoc, int idVersion)
			throws Exception {
		StringBuilder query = new StringBuilder(
				"select statu from versiondoc where numdoc=? and statu=? and numver=? ");
		ArrayList parametros = new ArrayList();
		parametros.add(new Integer(idDoc));
		parametros.add(new Integer(HandlerDocuments.docBorrador));
		parametros.add(new Integer(idVersion));
		return JDBCUtil.executeQuery(query, parametros, Thread.currentThread().getStackTrace()[1].getMethodName()).next();
	}

	public static boolean isDocumentStatuVersionPrintable(int idDoc,
			int idVersion) throws Exception {
		StringBuilder query = new StringBuilder(
				"select statu from versiondoc where numdoc=? and statu=? and numver=? ");
		ArrayList parametros = new ArrayList();
		parametros.add(new Integer(idDoc));
		parametros.add(HandlerDocuments.docApproved);
		parametros.add(new Integer(idVersion));
		return JDBCUtil.executeQuery(query, parametros, Thread.currentThread().getStackTrace()[1].getMethodName()).next();
	}

	public static boolean isDocumentStatuVersionCopyContentsWF(int idDoc,
			int idVersion, String user) throws Exception {
		StringBuilder query = new StringBuilder();

		query.append("SELECT a.copy ");
		query.append("FROM workflows a, user_workflows b ");
		query.append("WHERE a.idWorkFlow=b.idWorkFlow ");
		query.append("AND a.idDocument=? AND cast(a.statu as int)=? AND cast(b.statu as int)=? AND a.idVersion=? "); // and
																														// a.owner=?
																														// ");

		ArrayList parametros = new ArrayList();
		parametros.add(new Integer(idDoc));
		parametros.add(new Integer(HandlerWorkFlows.wfuPending));
		parametros.add(new Integer(HandlerWorkFlows.wfuPending));
		parametros.add(new Integer(idVersion));
		// parametros.add(user);
		CachedRowSet crs = JDBCUtil.executeQuery(query, parametros, Thread.currentThread().getStackTrace()[1].getMethodName());
		if (crs.next()) {
			try {
				if (crs.getString("copy") != null
						&& (crs.getString("copy").equals("0") || crs.getString(
								"copy").equals("false"))) {
					return true;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return false;
	}

	public static boolean isDocumentStatuVersionCopyContentsFlexWF(int idDoc,
			int idVersion, String user) throws Exception {
		StringBuilder query = new StringBuilder();

		query.append("SELECT a.copy ");
		query.append("FROM flexworkflow a, user_flexworkflows b ");
		query.append("WHERE a.idWorkFlow=b.idWorkFlow ");
		query.append("AND a.idDocument=? AND a.statu=? AND a.idVersion=? "); // AND
																				// a.owner=?
																				// ");

		ArrayList parametros = new ArrayList();
		parametros.add(new Integer(idDoc));
		parametros.add(HandlerWorkFlows.wfuPending);
		parametros.add(new Integer(idVersion));
		// parametros.add(user);
		CachedRowSet crs = JDBCUtil.executeQuery(query, parametros, Thread.currentThread().getStackTrace()[1].getMethodName());
		if (crs.next()) {
			try {
				if (crs.getString("copy") != null
						&& (crs.getString("copy").equals("0") || crs.getString(
								"copy").equals("false"))) {
					return true;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return false;
	}

	public static boolean isFirstVersionDocument(int idDoc) throws Exception {
		StringBuilder query = new StringBuilder(
				"SELECT count(numver) AS aprobadas FROM versiondoc WHERE cast(numdoc as int)=? AND statu=? ");
		ArrayList parametros = new ArrayList();
		parametros.add(new Integer(idDoc));
		parametros.add(HandlerDocuments.docApproved);
		CachedRowSet crs = JDBCUtil.executeQuery(query, parametros, Thread.currentThread().getStackTrace()[1].getMethodName());
		crs.next();
		return (crs.getInt(1) == 0);
	}

	public static boolean isUserFlexFlowPendding(
			UserFlexWorkFlowsTO userFlexWorkFlow) throws Exception {
		StringBuilder query = new StringBuilder();
		ArrayList<Object> parametros = new ArrayList<Object>();
		CachedRowSet crs = null;

		parametros.add(userFlexWorkFlow.getIdUser());
		parametros.add(userFlexWorkFlow.getIdWorkFlow());
		parametros.add(HandlerWorkFlows.wfuPending);

		query.append("SELECT a.* from user_flexworkflows a, flexworkflow b ");
		query.append("WHERE a.idWorkFlow=b.idWorkFlow ");
		query.append("AND a.idUser=? ");
		query.append("AND b.idWorkFlow = ? ");
		query.append("AND a.statu=? ");

		crs = JDBCUtil.executeQuery(query, parametros, Thread.currentThread().getStackTrace()[1].getMethodName());

		return crs.next();
	}

	public static Collection getAllDocuments(ArrayList docLinks) {
		StringBuilder sql = new StringBuilder(100);
		sql.append(
				"SELECT numGen,number,nameDocument,prefix FROM documents WHERE statu = ")
				.append(docApproved);
		sql.append(" AND active = '").append(docActives).append("'");
		ArrayList resp = new ArrayList();
		try {
			Vector datos = JDBCUtil.doQueryVector(sql.toString(),Thread.currentThread().getStackTrace()[1].getMethodName());
			String numGen = null;
			for (int row = 0; row < datos.size(); row++) {
				Properties prop = (Properties) datos.elementAt(row);
				numGen = prop.getProperty("numGen");
				String prefix = prop.getProperty("prefix");
				String number = prop.getProperty("number");
				String all = "";
				if (!ToolsHTML.isEmptyOrNull(prefix)) {
					all = prefix.trim() + number.trim();
				} else {
					all = number.trim();
				}
				DocumentsRelation bean = new DocumentsRelation(numGen, all,
						prop.getProperty("nameDocument"));
				if (docLinks.contains(numGen)) {
					bean.setSelected(true);
				} else {
					bean.setSelected(false); // Por defecto toma false pero
												// si alg�n d�a cambian
					// ese estandar :D
				}
				resp.add(bean);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return resp;
	}

	public static Collection getAllDocuments(ArrayList docLinks, String numero,
			String prefijo, String nombre, Hashtable security,
			Hashtable secInDocs, boolean isAdmon, boolean statuAnt) {
		StringBuilder sql = new StringBuilder(100);
		StringBuilder sql2 = new StringBuilder(100);
		boolean swnumeroExiste = true;
		sql.append("SELECT d.numGen,d.number,d.nameDocument,d.prefix,d.LastVersionApproved,d.idNode,vd.MayorVer,vd.MinorVer FROM documents d,versiondoc vd");
		sql.append(" WHERE d.numGen = vd.numDoc");
		sql.append("   AND vd.numVer = d.LastVersionApproved ");
		sql.append(" AND vd.numVer = (SELECT MAX(vdi.numVer) FROM versiondoc vdi");
		sql.append(" WHERE vdi.numDoc = d.numGen and vdi.statu = '")
				.append(HandlerDocuments.docApproved).append("')");
		sql.append(" AND d.type <> '")
				.append(HandlerDocuments.TypeDocumentsImpresion).append("' ");
		if (!ToolsHTML.isEmptyOrNull(numero)) {
			sql2.append(" AND ( ");
			if (!ToolsHTML.isEmptyOrNull(numero)) {
				sql2.append(" d.number IN (");
				StringTokenizer num = new StringTokenizer(numero, ",;:");
				while (num.hasMoreElements()) {
					String numero1 = (String) num.nextElement();
					sql2.append("'").append(numero1).append("'");
					if (num.hasMoreElements()) {
						sql2.append(",");
					}
				}
				sql2.append(")");
			}
			sql2.append(" OR d.number LIKE '").append(numero).append("%'");
			sql2.append("  )");
		}
		sql.append(" AND d.active = '").append(docActives).append("'");

		// Se completo query consultando statuAnt para obtener documentos que
		// tienen version anterior aprobada
		if (statuAnt) {
			sql.append(" AND ((d.statu  = '").append(docApproved)
					.append("' OR d.statuAnt  = '").append(docApproved)
					.append("')");
			// agregamos aqui los status de documentos en flujos de revision,
			// los cuales deberian formar parte del listado en caso de tener una
			// version aprobada anterior
			sql.append(" OR (d.statu  = '").append(docReview)
					.append("' AND approved='0') ");
			sql.append(" OR (d.statu  = '").append(docInApproved)
					.append("' AND approved='0') ");
			sql.append(" OR (d.statu  = '").append(docInReview)
					.append("' AND approved='0')) ");
		} else {
			sql.append(" AND d.statu  = '").append(docApproved).append("'");
		}

		if (!ToolsHTML.isEmptyOrNull(nombre)) {
			sql2.append(" AND (d.nameDocument LIKE '%" + nombre + "%') ");
		}

		if (!ToolsHTML.isEmptyOrNull(prefijo)) {
			sql2.append(" AND (d.prefix LIKE '%" + prefijo + "%') ");
		}

		log.debug("[getAllDocuments] " + sql.toString());
		// //System.out.println("[getAllDocuments] " + sql.toString());
		ArrayList resp = new ArrayList();
		try {
			Vector datos = null;
			// probamos primero si la busqueda trae datos
			if (!ToolsHTML.isEmptyOrNull(numero)
					|| !ToolsHTML.isEmptyOrNull(nombre)
					|| !ToolsHTML.isEmptyOrNull(prefijo)) {
				datos = JDBCUtil
						.doQueryVector(sql.toString() + sql2.toString(),Thread.currentThread().getStackTrace()[1].getMethodName());
				if (datos.size() == 0) {
					swnumeroExiste = false;
				}
			}

			// si la busqueda no trae datos, hacemos un query completo, para que
			// siempre lleve
			// datos.
			if ((datos == null) || (datos.size() == 0)) {
				datos = JDBCUtil.doQueryVector(sql.toString(),Thread.currentThread().getStackTrace()[1].getMethodName());
			}
			String numGen;
			String idNode;
			PermissionUserForm forma;
			for (int row = 0; row < datos.size(); row++) {
				Properties prop = (Properties) datos.elementAt(row);
				numGen = prop.getProperty("numGen");
				idNode = prop.getProperty("idNode");
				forma = (PermissionUserForm) secInDocs.get(numGen);
				// Si el Usuario No Tiene seguridad definida para el Documento
				// Se procede a verificar la Seguridad para la Carpeta
				if (forma == null) {
					forma = (PermissionUserForm) security.get(idNode);
				}
				if ((forma != null && forma.getToViewDocs() == Constants.permission)
						|| isAdmon) {
					String prefix = prop.getProperty("prefix");
					String number = prop.getProperty("number");
					String all = "";
					if (!ToolsHTML.isEmptyOrNull(prefix)) {
						all = prefix.trim() + number.trim();
					} else {
						all = number.trim();
					}
					// all = all + " Ver. " +
					// prop.getProperty("MayorVer").trim() + "." +
					// prop.getProperty("MinorVer");
					DocumentsRelation bean = new DocumentsRelation(numGen, all,
							prop.getProperty("nameDocument"));
					bean.setNumVer(prop.getProperty("LastVersionApproved"));
					bean.setVer(prop.getProperty("MayorVer").trim() + "."
							+ prop.getProperty("MinorVer"));
					if (docLinks.contains(numGen)) {
						bean.setSelected(true);
					} else {
						bean.setSelected(false);
					}
					bean.setExitoBusqueda(swnumeroExiste);
					resp.add(bean);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return resp;
	}

	public static String getQueryRelations(String id, String type) {
		StringBuilder sql = new StringBuilder(100);
		sql.append("SELECT DISTINCT d.numGen,d.number,d.nameDocument,d.prefix,dl.numVerLink,d.nameFile,vd.MayorVer,vd.MinorVer");
		sql.append(" FROM documentslinks dl,documents d,versiondoc vd");
		sql.append(" WHERE d.numGen = dl.numGenLink");
		sql.append("   AND vd.numVer = dl.numVerLink");
		// Se deben Mostrar s�lo los Documentos Cuya Versi�n est� Aprobada
		sql.append("   AND vd.statu  = '").append(docApproved).append("'");
		sql.append("   AND dl.numGen = ").append(id);
		if (!ToolsHTML.isEmptyOrNull(type)) {
			sql.append(" AND d.type = '").append(type).append("'");
		}
		sql.append(" AND d.active = '").append(docActives).append("'");
		sql.append(" AND  d.type <> '")
				.append(HandlerDocuments.TypeDocumentsImpresion).append("'");
		sql.append(" AND dl.numVerLink = (SELECT MAX(dli.numVerLink) FROM documentslinks dli,versiondoc vdi");
		sql.append(" WHERE dli.numVerLink = vdi.numVer AND vdi.statu = '")
				.append(docApproved).append("' ");
		sql.append(" AND dli.numGen = dl.numGen AND dli.numGenLink = dl.numGenLink)");
		sql.append(
				" AND dl.numVer = (SELECT MAX(numVer) FROM versiondoc WHERE numDoc = ")
				.append(id).append(" AND active = '1')");
		log.debug("[getQueryRelations] " + sql.toString());
		return sql.toString();
	}

	public static Collection getDocumentsLinks(String id, String type,
			String idGroup, String idUser, boolean chkSec) {
		// StringBuilder sql = new StringBuilder(100);
		StringBuilder idDocs = new StringBuilder("");
		boolean isFirst = true;
		// sql.append("SELECT DISTINCT
		// d.numGen,d.number,d.nameDocument,d.prefix,dl.numVerLink,d.nameFile,vd.MayorVer,vd.MinorVer");
		// sql.append(" FROM documentslinks dl,documents d,versiondoc vd");
		// sql.append(" WHERE d.numGen = dl.numGenLink");
		// sql.append(" AND vd.numVer = dl.numVerLink");
		// //Se deben Mostrar s�lo los Documentos Cuya Versi�n est� Aprobada
		// sql.append(" AND vd.statu = ").append(docApproved);
		// sql.append(" AND dl.numGen = ").append(id);
		// if (!ToolsHTML.isEmptyOrNull(type)) {
		// sql.append(" AND d.type = ").append(type);
		// }
		// sql.append(" AND d.active = ").append(docActives);
		// sql.append(" AND d.type <>
		// ").append(HandlerDocuments.TypeDocumentsImpresion);
		// sql.append(" AND dl.numVerLink = (SELECT MAX(dli.numVerLink) FROM
		// DocumentsLinks
		// dli,versiondoc vdi");
		// sql.append(" WHERE dli.numVerLink = vdi.numVer AND vdi.statu =
		// ").append(docApproved);
		// sql.append(" AND dli.numGen = dl.numGen AND dli.numGenLink =
		// dl.numGenLink)");
		//
		// sql.append(" AND dl.numVer = (SELECT MAX(numVer) FROM versiondoc
		// WHERE numDoc =
		// ").append(id).append(" AND active = '1')");
		// log.debug("getDocumentsLinks = "+sql.toString());
		boolean isAdmon = DesigeConf.getProperty("application.admon")
				.equalsIgnoreCase(idGroup);
		Vector resp = new Vector();
		try {
			// Vector datos = JDBCUtil.doQueryVector(sql.toString(),Thread.currentThread().getStackTrace()[1].getMethodName());
			Vector datos = JDBCUtil.doQueryVector(getQueryRelations(id, type),Thread.currentThread().getStackTrace()[1].getMethodName());

			for (int row = 0; row < datos.size(); row++) {
				if (!isFirst) {
					idDocs.append(",");
				}
				Properties prop = (Properties) datos.elementAt(row);
				String prefix = prop.getProperty("prefix");
				String number = prop.getProperty("number");
				String all = "";
				if (!ToolsHTML.isEmptyOrNull(prefix)) {
					all = prefix.trim() + number.trim();
				} else {
					all = number.trim();
				}
				// all = all + " Ver. " + prop.getProperty("MayorVer").trim() +
				// "." +
				// prop.getProperty("MinorVer");
				DocumentsRelation bean = new DocumentsRelation(
						prop.getProperty("numGen"), all,
						prop.getProperty("nameDocument"));
				bean.setNumVer(prop.getProperty("numVerLink"));
				bean.setNameFile(prop.getProperty("nameFile"));
				bean.setVer(prop.getProperty("MayorVer").trim() + "."
						+ prop.getProperty("MinorVer"));
				bean.setExitoBusqueda(true);
				if (isAdmon) {
					bean.setToPrintDoc(Constants.permission);
					bean.setToViewDocs(Constants.permission);
				}
				// Se concatenan los Id de los Documentos Relacionados para
				// chequear si los Mismos
				// tienen permiso
				// para ser vistos /Impreso
				idDocs.append(bean.getId());
				isFirst = false;
				resp.add(bean);
			}
			// Si hay documentos relacionados se procede a Verificar la
			// Seguridad de los Mismos
			if (!isAdmon && (chkSec && (resp.size() > 0))) {
				// :SEGURIDAD:
				Hashtable sec = null;
				// Hashtable sec =
				// HandlerGrupo.getSecurityForGroupInDoc(idGroup,
				// idDocs.toString());
				// HandlerDBUser.getSecurityForUserInDocs(idUser,
				// idDocs.toString(), sec);
				Users user = new Users();
				user.setNameUser(idUser);
				StringBuilder query = new StringBuilder(
						"SELECT idPerson, idgrupo FROM person WHERE accountactive='1' AND nameuser=? ");
				ArrayList parametros = new ArrayList();
				parametros.add(idUser);
				CachedRowSet rs = JDBCUtil.executeQuery(query, parametros, Thread.currentThread().getStackTrace()[1].getMethodName());
				if (rs.next()) {
					user.setIdPerson(rs.getInt("idperson"));
					user.setIdGroup(rs.getString("idgrupo"));
					sec = ToolsHTML.checkDocsSecurity(sec, user,
							idDocs.toString());

					for (int i = 0; i < resp.size(); i++) {
						DocumentsRelation documentsRelation = (DocumentsRelation) resp
								.elementAt(i);
						PermissionUserForm perm = (PermissionUserForm) sec
								.get(documentsRelation.getId());
						if (perm != null) {
							documentsRelation.setToPrintDoc(perm
									.getToImpresion());
							documentsRelation.setToViewDocs(perm
									.getToViewDocs());
						}
					}
				}
			}
		} catch (Exception e) {
			log.error(e.getMessage());
			e.printStackTrace();
		}
		return resp;
	}

	public static CheckOutDocForm getDataCheckOutDocument(String idDocument,
			String idVersion, boolean loadForCheckIn) throws Exception {
		StringBuilder sql = new StringBuilder(100);
		sql.append("SELECT idNode,numGen,number,nameDocument,nameFile,");
		sql.append(" type,prefix,vd.numVer,vd.MayorVer,vd.MinorVer,t.typeDoc,d.comments,vd.statu");
		if (loadForCheckIn) {
			sql.append(",dc.dateCheckOut,dc.idCheckOut,vd.dateExpires,vd.dateCreated ");
		}
		sql.append(" FROM documents d,versiondoc vd,typedocuments t");
		if (loadForCheckIn) {
			sql.append(",doccheckout dc");
		}
		sql.append(" WHERE vd.numDoc  = d.numGen AND d.numGen = ").append(
				idDocument);
		sql.append(" AND t.idTypeDoc = cast(d.type as int)");
		if (loadForCheckIn) {
			sql.append(" AND dc.idDocument = ").append(idDocument);
		}
		if (ToolsHTML.isEmptyOrNull(idVersion)) {
			sql.append(
					" AND vd.numVer = (SELECT MAX(numVer) FROM versiondoc WHERE numDoc=")
					.append(idDocument).append(")");
		} else {
			sql.append(" AND vd.numVer = ").append(idVersion);
		}
		if (loadForCheckIn) {
			sql.append(" AND dc.active = '0'");
		}
		log.debug("[getDataCheckOutDocument] = " + sql.toString());
		Properties prop = JDBCUtil.doQueryOneRow(sql.toString(),Thread.currentThread().getStackTrace()[1].getMethodName());
		if (prop.getProperty("isEmpty").equalsIgnoreCase("false")) {
			String numGen = prop.getProperty("numGen");
			String number = prop.getProperty("number");
			String nameDocument = prop.getProperty("nameDocument");
			String typeDoc = prop.getProperty("typeDoc");
			String prefix = prop.getProperty("prefix");
			String numVer = prop.getProperty("numVer");
			String MayorVer = prop.getProperty("MayorVer");
			String MinorVer = prop.getProperty("MinorVer");
			String numVersion = MayorVer + "." + MinorVer;
			String numero = "";
			if (ToolsHTML.isEmptyOrNull(prefix)) {
				numero = number;
			} else {
				numero = prefix + number;
			}
			CheckOutDocForm forma = new CheckOutDocForm(numGen, nameDocument,
					numVersion, numero, typeDoc);
			forma.setIdTypeDoc(prop.getProperty("type"));
			forma.setComments(prop.getProperty("comments"));
			forma.setNumVersion(numVer);
			forma.setMayorVer(MayorVer);
			if (!ToolsHTML.isEmptyOrNull(MinorVer)) {
				forma.setMinorVer(MinorVer);
			} else {
				forma.setMinorVer("");
			}
			forma.setIdNode(prop.getProperty("idNode"));
			forma.setExpires(Constants.permissionSt);
			forma.setNameFile(prop.getProperty("nameFile"));
			forma.setDateExpires("");
			if (loadForCheckIn) {
				forma.setDateCheckOut(ToolsHTML.formatDateShow(
						prop.getProperty("dateCheckOut"), true));
				forma.setIdCheckOut(prop.getProperty("idCheckOut"));
				String dateExpires = prop.getProperty("dateExpires");
				if (!ToolsHTML.isEmptyOrNull(dateExpires)) {
					forma.setExpires(Constants.notPermissionSt);
					forma.setDateExpires(ToolsHTML.date
							.format(ToolsHTML.sdfShowConvert.parse(dateExpires)));
				}
				forma.setDateCreated(ToolsHTML.formatDateShow(
						prop.getProperty("dateCreated"), true));
				forma.setComments("");
				forma.setStatu(prop.getProperty("statu").trim());
			}
			return forma;
		}
		return null;
	}

	// public static boolean upDateStatuDoc(String idDocument,String
	// checkOut,String owner) throws
	// Exception {
	// StringBuilder update = new StringBuilder(100);
	// boolean resp = false;
	// update.append("UPDATE documents SET CheckOut =
	// '").append(checkOut).append("', dateCheckOut =
	// ? ");
	// update.append(" WHERE numGen = ").append(idDocument);
	// Connection con = null;
	// PreparedStatement st = null;
	// try {
	// con = JDBCUtil.getConnection(Thread.currentThread().getStackTrace()[1].getMethodName());
	// con.setAutoCommit(false);
	// st = con.prepareStatement(update.toString());
	// Timestamp time = new Timestamp(new Date().getTime());
	// st.setTimestamp(1,time);
	// st.executeUpdate();
	// HandlerStruct.updateHistoryDocs(con,Integer.parseInt(idDocument),0,0,owner,time,checkOut);
	// con.commit();
	// resp = true;
	// } catch (Exception e) {
	// applyRollback(con);
	// setMensaje(e.getMessage());
	// resp = false;
	// } finally{
	// setFinally(con,st);
	// }
	// return resp;
	// }

	public static synchronized void upLastOperationDoc(int idDocument,
			String operation, Connection con) throws Exception {
		StringBuilder update = new StringBuilder(100);
		update.append("UPDATE documents SET lastOperation = ? ");
		update.append(" WHERE numGen = ").append(idDocument);
		PreparedStatement st = null;
		// con = JDBCUtil.getConnection(Thread.currentThread().getStackTrace()[1].getMethodName());
		st = con.prepareStatement(JDBCUtil.replaceCastMysql(update.toString()));
		st.setString(1, operation);
		st.executeUpdate();
	}

	// if
	// (HandlerDocuments.checkOutDocument(Integer.parseInt(forma.getIdDocument()),HandlerDocuments.docCheckOut,
	// user.getUser(),forma.getNumVer())) {
	public static synchronized int checkOutDocument(int idDocument,
			String checkOut, String owner, int idVersionToCopy)
			throws Exception {
		StringBuilder update = new StringBuilder();
		int resp = 0;
		StringBuilder check = new StringBuilder();
		check.append("SELECT idCheckOut FROM doccheckout WHERE idDocument = ")
				.append(idDocument);
		check.append(" AND active = '0'");
		// update.append("INSERT INTO doccheckout(idCheckOut,idDocument,checkOutBy,dateCheckOut,dataFile) VALUES(?,?,?,?,?) ");
		update.append("INSERT INTO doccheckout(idCheckOut,idDocument,checkOutBy,dateCheckOut) VALUES(?,?,?,?) ");
		/*//ydavila Flujo de solicitud de cambio - Notificaci�n al Propietario de bloqueo de documento
		Locale defaultLocale = new Locale(
				DesigeConf.getProperty("language.Default"),
				DesigeConf.getProperty("country.Default"));
		ResourceBundle rb = ResourceBundle.getBundle("LoginBundle",
				defaultLocale);
		
		try {
			rb = ResourceBundle.getBundle("LoginBundle", defaultLocale);
		} catch (java.lang.IllegalStateException e) {
			rb = ResourceBundle.getBundle("LoginBundle",
					new Locale(DesigeConf.getProperty("language.Default"), ""));
		} catch (java.util.MissingResourceException e) {
			rb = ResourceBundle.getBundle("LoginBundle",
					new Locale(DesigeConf.getProperty("language.Default"),
							DesigeConf.getProperty("country.Default")));
		}
		//ydavila necesito el email del propietario, versi�n del documento		
		StringBuilder sql = new StringBuilder(100);
		sql.append("SELECT owner FROM documents WHERE numgen=");
		sql.append(idDocument);		
		log.debug("[buscar owner] = " + sql.toString());
		Properties prop1 = JDBCUtil.doQueryOneRow(sql.toString(),Thread.currentThread().getStackTrace()[1].getMethodName());
		
		//ydavila necesito el email del propietario
		StringBuilder sql1 = new StringBuilder(100);
		sql1.append("SELECT email FROM person WHERE nameuser='");
		sql1.append(prop1.getProperty("owner")).append("'");
		log.debug("[buscar email] = " + sql1.toString());
		prop1 = JDBCUtil.doQueryOneRow(sql1.toString());
		BeanNotifiedWF userMailExp = new BeanNotifiedWF();
		
		HandlerWorkFlows.notifiedUsers(
				rb.getString("imp.title_solcambio") + " - "
						+ prop1.getProperty("vd.mayorver") + prop1.getProperty("vd.minorver"), rb
						.getString("mail.nameUser"), prop1.getProperty("email"),
								prop1.getProperty("email"), userMailExp
						.getComments());*/
		try {
			log.debug("[checkOutDocument] = " + check.toString());

			// necesitamos el nombre y propietario actual
			StringBuilder query = new StringBuilder(
					"select nameDocument,owner from documents where numgen=?");
			ArrayList parametros = new ArrayList();
			parametros.add(idDocument);
			CachedRowSet crs = JDBCUtil.executeQuery(query, parametros, Thread.currentThread().getStackTrace()[1].getMethodName());
			if (!crs.next())
				throw new ApplicationExceptionChecked(
						"No se econtro el nombre original del documento y propietario");

			query.setLength(0);
			query.append("select nameDocVersion,ownerVersion from versiondoc where numver=(select max(numver) from versiondoc where numdoc=?)");
			CachedRowSet crsV = JDBCUtil.executeQuery(query, parametros, Thread.currentThread().getStackTrace()[1].getMethodName());
			boolean haveVersion = crsV.next();

			Properties prop = JDBCUtil.doQueryOneRow(check.toString(),Thread.currentThread().getStackTrace()[1].getMethodName());
			log.debug("[I] " + prop.getProperty("isEmpty"));
			if (prop.getProperty("isEmpty").equalsIgnoreCase("true")) {
				// Carga de la Versi�n del Documento
				String[] datosVersion = getFieldsToVersionMayor(new String[] {
						"MayorVer", "MinorVer" }, idDocument);

				// aqui el archivo no se descomprime porque va a otra tabla, no
				// es necesario
				// InputStream dataVersion = HandlerStruct.recuperarFileBD(null,
				// idVersionToCopy, false);

				checkDocument(String.valueOf(idDocument));
				int idCheckOut = HandlerStruct.proximo("doccheckout","doccheckout", "idCheckOut");
				Timestamp time = new Timestamp(new Date().getTime());
				
				parametros = new ArrayList();
				parametros.add(new Integer(idCheckOut));
				parametros.add(new Integer(idDocument));
				parametros.add(owner); // usuario que bloquea //HandlerDocuments.getOwnerDocument(idDocument));
				parametros.add(time);
				JDBCUtil.executeUpdate(update, parametros);

				// copiamos el archivo de un directorio a otro
				Archivo.copyDocumentInDisk("versiondoc", idVersionToCopy,"doccheckout", idCheckOut);

				Connection con = null;
				try {
					con = JDBCUtil.getConnection(Thread.currentThread().getStackTrace()[1].getMethodName());
					con.setAutoCommit(false);
					upLastOperationDoc(idDocument, lastCheckOut, con);
					updateHistoryDocs(con, idDocument, 0, 0, owner, time, checkOut,null, datosVersion);
					con.commit();
				} catch(Exception e) {
					if(con!=null) {
						con.rollback();
					}
				} finally {
					if(con!=null) {
						con.close();
					}
				}
				
				resp = idCheckOut;			
/*				//ydavila enviar correo de bloque de documento
				MailForm formaMail = new MailForm();
				MailForm formaMailOwner = null;
				String titleMail1 = "titulo=documento bloqueado por solicitud de cambio";
				formaMail.setUserName("german");
				formaMail.setFrom("yajaira.davila@gmail.com");
				formaMail.setNameFrom("admin");
				formaMail.setTo("german");
				formaMail.setSubject("titulo=documento bloqueado por solicitud de cambio");
				formaMail.setMensaje("mensaje de blqueo de flujos");
				SendMailTread mail = new SendMailTread(formaMail);*/
			} else {
				throw new ApplicationExceptionChecked("E0028");
			}
		} catch (ApplicationExceptionChecked ae) {
			log.error(ae.getMessage());
			ae.printStackTrace();
			throw ae;
		} catch (Exception e) {
			log.error(e.getMessage());
			e.printStackTrace();
			setMensaje(e.getMessage());
			resp = 0;
		}
		return resp;
	}

	public static String checkDocument(String idDocument)
			throws ApplicationExceptionChecked {
		String lastOpe = "";
		try {
			lastOpe = getField("lastOperation", "documents", "numGen",
					String.valueOf(idDocument), "=", 2,Thread.currentThread().getStackTrace()[1].getMethodName());
			lastOpe = lastOpe.trim();
		} catch (Exception ex) {
			log.error(ex.getMessage());
			ex.printStackTrace();
			throw new ApplicationExceptionChecked("err.checkLastOpe");
		}
		if (lastInReview.equalsIgnoreCase(lastOpe)) {
			throw new ApplicationExceptionChecked("err.docInReview");
		}
		if (lastInApproved.equalsIgnoreCase(lastOpe)) {
			throw new ApplicationExceptionChecked("err.docInApproved");
		}
		return lastOpe.trim();
	}

	// SIMON 08 DE JULIO 2005 INICIO
	public static synchronized String deleteDocstr(int idDocument, int statu,
			String doneBy, boolean isPrintRequest)
			throws ApplicationExceptionChecked {
		StringBuilder update = new StringBuilder(100);
		boolean resp = false;
		String str = null;
		update.append("UPDATE documents SET active = CAST(? as bit),lastOperation = ? WHERE numGen = ?");
		Connection con = null;
		PreparedStatement st = null;
		try {
			String idTypeDoc = HandlerDocuments.getFieldToDocument("type",
					idDocument);
			// si el archivo no es de impresion, lo chequeamos
			if (!idTypeDoc
					.equalsIgnoreCase(HandlerDocuments.TypeDocumentsImpresion)) {
				// si esta en revision o aprobacion, no lo deja eliminar. 08 de
				// julio 2005 Sim�n
				checkDocument(String.valueOf(idDocument));
			}
			StringBuilder std = new StringBuilder(50);
			std.append("'").append(HandlerDocuments.docApproved).append("'");

			// JULIO 13 DEL 2005 INICIO
			HandlerWorkFlows
					.eraseWorkFlowsDocuments(String.valueOf(idDocument));
			// JULIO 13 DEL 2005 INICIO

			// obtengo todos las versiones del documento que esten aprobadas
			Vector histApproved = (Vector) HandlerDocuments
					.getAllVersionIdsToDocumentAndStatu(idDocument,
							std.toString(), false);
			// obtengo el status real del documento de la tabla Documents
			// false);
			String statuDoc = HandlerDocuments.getFieldToDocument("statu",
					idDocument);
			// Datos de la Versi�n
			String[] datosVersion = getFieldsToVersionMayor(new String[] {
					"MayorVer", "MinorVer" }, idDocument);
			con = JDBCUtil.getConnection(Thread.currentThread().getStackTrace()[1].getMethodName());
			con.setAutoCommit(false);
			// borro los links relacionados
			isDocumentRelationsDelete(idDocument, con);

			if (!(idTypeDoc
					.equalsIgnoreCase(HandlerDocuments.TypeDocumentsImpresion))
					&& (histApproved != null && !histApproved.isEmpty())) {
				if (histApproved.size() > 0) {
					// obtengo la ultima version aprobada..
					String id = (String) histApproved
							.get(histApproved.size() - 1);
					// actualiza la tabla versiondoc, coloca el estatus en
					// obsoleto
					// donde el status este aprobado y la numversion= iddocument
					updateStatuDoc(docObs, id, docApproved, con);
					// actualizo la tabla documents, coloco el status en
					// obsoleto, actualizo el
					// campo statusanterior en el que se
					// encontraba el status de la tabla documents y coloco la
					// ultima operacion en
					// delete
					updateStateDoc(con, idDocument, docObs, statuDoc,
							lastDelete, 0, null);
					// ydavila Ticket 001-00-002916 - Principal-Lista de Distribucion aparecen documentos eliminados y obsoletos
					deletelistdistdocument(con, idDocument);
					con.commit();
					// resp = true;
					str = "doc.obs";
				}
			} else {
				st = con.prepareStatement(JDBCUtil.replaceCastMysql(update.toString()));
				Timestamp time = new Timestamp(new Date().getTime());
				st.setInt(1, statu);
				st.setString(2, lastDelete);
				st.setInt(3, idDocument);
				st.executeUpdate();
				updateHistoryDocs(con, idDocument, 0, 0, doneBy, time,
						docDelete, null, datosVersion);
				con.commit();
				// resp = true;
				if (isPrintRequest) {
					str = "doc.deleteRequest";
				} else {
					str = "doc.delete";
				}
			}

		} catch (ApplicationExceptionChecked ae) {
			log.error(ae.getMessage());
			applyRollback(con);
			throw ae;
		} catch (Exception e) {
			log.error(e.getMessage());
			applyRollback(con);
			setMensaje(e.getMessage());
			str = null;
		} finally {
			setFinally(con, st);
		}

		return str;
	}

	//ydavila Elmor
	public static synchronized String cambiarDocstr(int idDocument, int statu,
			String doneBy, boolean isPrintRequest)
					throws Exception {
		String str = null;
		Connection con = null;
		con = JDBCUtil.getConnection(Thread.currentThread().getStackTrace()[1].getMethodName());
		upLastOperationDoc(idDocument, lastCheckOut, con);
		return str;
	}
	
	public static boolean isDocumentRelationsDelete(int idDoc, Connection con)
			throws Exception {
		boolean sw = false;
		try {
			StringBuilder query = new StringBuilder(
					"DELETE FROM documentslinks");
			query.append(" WHERE numGenLink = ").append(idDoc)
					.append(" OR numGen=").append(idDoc);
			log.debug("[isDocumentRelationsDelete]" + query.toString());
			PreparedStatement st = con.prepareStatement(JDBCUtil.replaceCastMysql(query.toString()));
			sw = st.execute();
		} catch (Exception e) {
			log.error(e.getMessage());
			e.printStackTrace();
		}
		return sw;
	}

	// SIMON 08 DE JULIO 2005 FIN
	public static synchronized boolean deleteDoc(int idDocument, int statu,
			String doneBy) throws ApplicationExceptionChecked {
		StringBuilder update = new StringBuilder(100);
		boolean resp = false;
		// String statuDoc =
		// HandlerDocuments.getFieldToDocument("statu",idDocument);
		update.append("UPDATE documents SET active = CAST(? as bit),lastOperation = ? WHERE numGen = ?");
		Connection con = null;
		PreparedStatement st = null;
		try {

			// si esta en revision o aprobacion, no lo deja eliminar. 08 de
			// julio 2005 Sim�n
			checkDocument(String.valueOf(idDocument));

			// si tiene links, no lo deja eliminar.
			if (isDocumentRelations(idDocument)) {
				// Observacion:si tiene links, deberia eliminarse, y tener un
				// campo de estatus en la
				// tabla documentslink
				throw new ApplicationExceptionChecked("E0039");
			}

			StringBuilder std = new StringBuilder(50);
			std.append("'").append(HandlerDocuments.docApproved).append("'");
			// obtengo todos las versiones del documento que esten aprobadas
			Vector histApproved = (Vector) HandlerDocuments
					.getAllVersionIdsToDocumentAndStatu(idDocument,
							std.toString(), false);
			// obtengo el status real del documento de la tabla Documents
			// false);
			String statuDoc = HandlerDocuments.getFieldToDocument("statu",
					idDocument);
			// Datos de la Versi�n
			String[] datosVersion = getFieldsToVersionMayor(new String[] {
					"MayorVer", "MinorVer" }, idDocument);
			con = JDBCUtil.getConnection(Thread.currentThread().getStackTrace()[1].getMethodName());
			con.setAutoCommit(false);

			if (histApproved != null && !histApproved.isEmpty()) {
				if (histApproved.size() > 0) {
					// obtengo la ultima version aprobada..
					String id = (String) histApproved
							.get(histApproved.size() - 1);
					// actualiza la tabla versiondoc, coloca el estatus en
					// obsoleto
					// donde el status este aprobado y la numversion= iddocument
					updateStatuDoc(docObs, id, docApproved, con);
					// actualizo la tabla documents, coloco el status en
					// obsoleto, actualizo el
					// campo statusanterior en el que se
					// encontraba el status de la tabla documents y coloco la
					// ultima operacion en
					// delete
					updateStateDoc(con, idDocument, docObs, statuDoc,
							lastDelete, 0, null);
					con.commit();
					resp = true;
				}
			} else {
				st = con.prepareStatement(JDBCUtil.replaceCastMysql(update.toString()));
				Timestamp time = new Timestamp(new Date().getTime());
				st.setInt(1, statu);
				st.setString(2, lastDelete);
				st.setInt(3, idDocument);
				st.executeUpdate();
				updateHistoryDocs(con, idDocument, 0, 0, doneBy, time,
						docDelete, null, datosVersion);
				con.commit();
				resp = true;
			}
		} catch (ApplicationExceptionChecked ae) {
			log.error(ae.getMessage());
			applyRollback(con);
			throw ae;
		} catch (Exception e) {
			log.error(e.getMessage());
			applyRollback(con);
			setMensaje(e.getMessage());
			resp = false;
		} finally {
			setFinally(con, st);
		}
		return resp;
	}

	public static Collection getDocumentsExpire(String owner) {
		Connection con = null;
		PreparedStatement st = null;
		ResultSet rs = null;
		ArrayList resp = new ArrayList();
		StringBuilder sql = new StringBuilder(100);
		sql.append("SELECT d.numGen,d.nameDocument,vd.dateExpires,vd.MayorVer,vd.MinorVer");
		sql.append(" FROM documents d,versiondoc vd");
		sql.append(" WHERE  vd.dateExpires >= ?");
		sql.append("  AND   vd.numDoc = d.numGen  AND  d.owner = ?");
		try {
			Timestamp fecha = new Timestamp(new Date().getTime());
			con = JDBCUtil.getConnection(Thread.currentThread().getStackTrace()[1].getMethodName());
			st = con.prepareStatement(JDBCUtil.replaceCastMysql(sql.toString()));
			st.setTimestamp(1, fecha);
			st.setString(2, owner);
			rs = st.executeQuery();
			while (rs.next()) {
				DocumentsCheckOutsBean docCheck = new DocumentsCheckOutsBean();
				docCheck.setIdDocument(rs.getString("numGen"));
				docCheck.setNameDocument(rs.getString("nameDocument"));
				docCheck.setMayorVer(rs.getString("MayorVer").trim());
				docCheck.setMinorVer(rs.getString("MinorVer").trim());
				docCheck.setDateCheckOut(ToolsHTML.formatDateShow(
						rs.getString("dateExpires"), false));
				resp.add(docCheck);
			}
		} catch (Exception ex) {
			log.error(ex.getMessage());
			ex.printStackTrace();
		} finally {
			setFinally(con, st, rs);
		}
		return resp;
	}

	/**
	 * 
	 * @param owner
	 * @param active
	 * @param struct
	 * @param prefijos
	 * @param searchAsAdmin
	 * @return
	 * @throws Exception
	 */
	//Bandeja Principal - Documentos Pendientes
	public static synchronized Collection<DocumentsCheckOutsBean> getAllDocumentsCheckOutsUser(
			String owner, String active, Hashtable struct, Hashtable prefijos,
			boolean searchAsAdmin) throws Exception {
		StringBuilder sql = new StringBuilder(100);
		sql.append("SELECT d.numGen,d.nameDocument,cd.dateCheckOut,vd.MayorVer,vd.MinorVer");
		sql.append(" ,d.number,d.prefix,d.idNode,");
		sql.append(" p.idperson, p.nombres, p.apellidos, p.email,vd.statu");
		sql.append(" FROM person p, doccheckout cd,documents d,versiondoc vd");
		sql.append(" WHERE cd.idDocument = d.numGen");
		sql.append(" AND cd.active = '").append(active).append("'");
		if (!searchAsAdmin) {
			// los usuarios que no sean del tipo administrador, solo podran ver
			// archivos y flujos
			// de los cuales ellos sean los propietarios
			sql.append(" AND cd.checkOutBy = '").append(owner).append("'");
		}
		sql.append(" AND p.nameuser = cd.checkOutBy");
		sql.append(" AND d.active =  '").append(Constants.permission)
				.append("'");
		sql.append(" AND vd.numDoc = cd.idDocument");
		sql.append(" AND vd.numVer = (SELECT MAX(vdi.numVer) FROM versiondoc vdi WHERE vdi.numDoc = vd.numDoc)");
		Vector resp = new Vector();
		Vector datos = JDBCUtil.doQueryVector(sql.toString(),Thread.currentThread().getStackTrace()[1].getMethodName());
		String prefixDoc = null;
		String idNode = "";
		log.info("[getAllDocumentsCheckOutsUser] " + sql.toString());
		for (int row = 0; row < datos.size(); row++) {
			Properties properties = (Properties) datos.elementAt(row);
			DocumentsCheckOutsBean docCheck = new DocumentsCheckOutsBean();
			docCheck.setIdDocument(properties.getProperty("numGen"));
			docCheck.setNameDocument(properties.getProperty("nameDocument"));
			// docCheck.setMayorVer(properties.getProperty("MayorVer").trim());
			// docCheck.setMinorVer(properties.getProperty("MinorVer").trim());
			String minorVer = properties.getProperty("MinorVer").trim();
			if (ToolsHTML.isEmptyOrNull(minorVer)) {
				minorVer = "";
			}
			String mayorVer = properties.getProperty("MayorVer").trim();
			if (ToolsHTML.isEmptyOrNull(mayorVer)) {
				mayorVer = "";
			}
			String status = properties.getProperty("statu").trim();
			//nuevo status en badeja de entrada documentos pendiente
			if (ToolsHTML.isEmptyOrNull(status)) {
				status = "";
			}
			if (status.equalsIgnoreCase("1")) {
				status = "Borrador Bloqueado";
			}
			if (status.equalsIgnoreCase("5")) {
				status = "Aprobado Bloqueado";
			}
			docCheck.setStatuBeanDocPendiente(status);
			docCheck.setMayorVer(mayorVer);
			docCheck.setMinorVer(minorVer);
			docCheck.setDateCheckOut(ToolsHTML.formatDateShow(
					properties.getProperty("dateCheckOut"), true));
			docCheck.setNumber(properties.getProperty("number") != null ? properties
					.getProperty("number").trim() : "");
			// docCheck.setPrefix(properties.getProperty("prefix")!=null?properties.getProperty("prefix").trim():"");
			idNode = properties.getProperty("idNode");
			// Se Busca el Prefijo del Documento seg�n como se encuentre
			// Definido el Mismo
			if (struct != null) {
				if (!prefijos.containsKey(idNode)) {
					prefixDoc = ToolsHTML.getPrefixToDoc(struct, idNode);
					prefijos.put(idNode, prefixDoc);
				} else {
					prefixDoc = (String) prefijos.get(idNode);
				}
			} else {
				prefixDoc = properties.getProperty("prefix") != null ? properties
						.getProperty("prefix").trim() : "";
			}
			if (!ToolsHTML.isEmptyOrNull(prefixDoc)) {
				docCheck.setPrefix(prefixDoc.trim());
			} else {
				docCheck.setPrefix("");
			}

			Person p = new Person(Integer.parseInt(properties
					.getProperty("idperson")),
					properties.getProperty("nombres"),
					properties.getProperty("apellidos"),
					properties.getProperty("email"));

			docCheck.setPersonBean(p);

			resp.add(docCheck);
		}
		return resp;
	}

	//Bandeja Principal - Documentos Pendientes requeridos en las normas
	public static synchronized Collection<DocumentsCheckOutsBean> getAllDocumentsCheckOutsRequiredUser(
			Hashtable struct, Hashtable prefijos, int tipoConsulta, 
			ProgramAuditTO oProgramAuditTO, PlanAuditTO oPlanAuditTO) throws Exception {
		
		StringBuilder sql = new StringBuilder(100);
		//int tipoConsulta = 0; // 0 -> General - 1 -> Programa - 2 -> Plan
		boolean isPlan = false;
		boolean isProgram = false;
		StringBuilder normsAudit = new StringBuilder();
		StringBuilder normIds = new StringBuilder();
		PlanAuditDAO oPlanAuditDAO= new PlanAuditDAO();
		ProgramAuditDAO oProgramAuditDAO= new ProgramAuditDAO();
		StringBuilder query = new StringBuilder();
		String sistemaGestionAuditoria = "";
		String seleccionados = "";

		switch(tipoConsulta) {
			case 0:
				// consultamos la norma guia
				String normaAudit = String.valueOf(HandlerParameters.PARAMETROS.getIdNormAudit());

				// buscamos el sistema de Gestion
				query.setLength(0);
				query.append("select sistema_gestion from norms where idNorm = ").append(normaAudit);
				CachedRowSet sga = JDBCUtil.executeQuery(query, Thread.currentThread().getStackTrace()[1].getMethodName());
				if(sga.next()) {
					sistemaGestionAuditoria = sga.getString("sistema_gestion");
				}
				break;
			case 1:
				oProgramAuditDAO.cargar(oProgramAuditTO);
				
				oPlanAuditDAO.cargar(oPlanAuditTO);

				// buscamos el sistema de Gestion
				query.setLength(0);
				query.append("select sistema_gestion from norms where idNorm = ").append(oProgramAuditTO.getIdNorm());
				CachedRowSet sg = JDBCUtil.executeQuery(query, Thread.currentThread().getStackTrace()[1].getMethodName());
				if(sg.next()) {
					// ahora cargaremos las normas hijas de la norma del programa
					query.setLength(0);
					query.append("select idNorm from norms where sistema_gestion = '").append(sg.getString("sistema_gestion")).append("' ");
					query.append("and idNorm != ").append(oProgramAuditTO.getIdNorm());
					CachedRowSet lista = JDBCUtil.executeQuery(query, Thread.currentThread().getStackTrace()[1].getMethodName());
					String sep = "";
					String coma = ",";
					while(lista.next()) {
						if(lista.getString("idNorm")!=null && !lista.getString("idNorm").trim().equals("")) {
							normIds.append(sep).append(lista.getString("idNorm"));
							sep = coma;
						}
					}
				}
				
				isProgram = true;
				break;
			case 2:
				oPlanAuditDAO.cargar(oPlanAuditTO);
				isPlan = true;
				break;
		}
		
		sql.setLength(0);
		sql.append("SELECT d.numGen,d.nameDocument,vd.MayorVer,vd.MinorVer,d.number,d.prefix,d.idNode, ");
		sql.append("p.idNorm, p.titleNorm, p.sistema_gestion, p.indice ");
		sql.append("FROM norms p LEFT JOIN documents d ON d.normISO LIKE ").append(JDBCUtil.concatSql(new String[]{"'%'","CAST(p.idNorm AS VARCHAR)","'%'"},null)).append(" ");
		if(isProgram) { // filtramos las normas de auditoria
			sql.append("AND d.dateCreation >= '").append(oPlanAuditTO.getDateFromPlan()).append(" 00:00:00' ");
			sql.append("AND d.dateCreation <= '").append(oPlanAuditTO.getDateUntilPlan()).append(" 23:59:59' ");
		}
		sql.append("LEFT OUTER JOIN versiondoc vd ON vd.numDoc = d.numGen ");
		sql.append("AND vd.numVer = (SELECT MAX(vdi.numVer) FROM versiondoc vdi WHERE vdi.numDoc = vd.numDoc) ");
		
		if(isProgram) { // filtramos las normas de auditoria
			sql.append("WHERE p.idNorm IN (").append(normIds.length()>0?normIds.substring(0, normIds.length()-1):"").append(") ");
			sql.append("AND p.documentRequired = 1 ");
		} else if(isPlan) { // filtramos los requisitos seleccionados
			seleccionados = oPlanAuditTO.getIdNormPlanCheck();
			if(seleccionados!=null && seleccionados.endsWith(",")) {
				seleccionados = seleccionados.substring(0, seleccionados.length()-1);
			}
			if(seleccionados!=null && !seleccionados.trim().equals("")) {
				sql.append("WHERE p.idNorm IN (").append(seleccionados).append(") ");
			}
		} else {
			sql.append("WHERE p.documentRequired = 1 ");
			sql.append("AND p.sistema_gestion != '").append(sistemaGestionAuditoria).append("' ");
			
		}
		
		// union de querys
		sql.append("UNION ");
		
		// query de sacops
		sql.append("SELECT d.idPlanillaSacop1 As numGen, 'SACOP' As nameDocument,' ' As MayorVer,' ' As MinorVer, d.sacopNum As number, ");
		sql.append("' ' As prefix,0 As idNode, p.idNorm, p.titleNorm, p.sistema_gestion, p.indice "); 
		sql.append("FROM norms p ");
		sql.append("LEFT JOIN tbl_planillasacop1 d ON d.requisitosAplicable LIKE CONCAT( '%' , p.idNorm , '%' ) ");
		if(isProgram) {
			sql.append("AND d.fechaEmision >= '").append(oPlanAuditTO.getDateFromPlan()).append(" 00:00:00' ");
			sql.append("AND d.fechaEmision <= '").append(oPlanAuditTO.getDateUntilPlan()).append(" 23:59:59' ");
			sql.append("WHERE p.idNorm IN (").append(normIds.length()>0?normIds.substring(0, normIds.length()-1):"").append(") ");
		} else if(isPlan && seleccionados!=null && !seleccionados.trim().equals("")) { // filtramos los requisitos seleccionados
			sql.append("WHERE p.idNorm IN (").append(seleccionados).append(") ");
		} else {
			sql.append("WHERE p.documentRequired = 1 ");
			sql.append("AND p.sistema_gestion != '").append(sistemaGestionAuditoria).append("' ");
		}
		sql.append("AND d.idPlanillaSacop1 IS NOT NULL ");

		// Ordenamiento
		sql.append("ORDER BY idNorm ");
		
		Vector resp = new Vector();
		Vector datos = JDBCUtil.doQueryVector(sql.toString(),Thread.currentThread().getStackTrace()[1].getMethodName());
		String prefixDoc = null;
		String idNode = "";
		log.debug("[getAllDocumentsCheckOutsRequiredUser] " + sql.toString());
		// //System.out.println("[getAllDocumentsCheckOutsUser] " +
		// sql.toString());
		for (int row = 0; row < datos.size(); row++) {
			Properties properties = (Properties) datos.elementAt(row);
			DocumentsCheckOutsBean docCheck = new DocumentsCheckOutsBean();
			docCheck.setIdDocument(properties.getProperty("numGen"));
			docCheck.setNameDocument(properties.getProperty("nameDocument"));
			// docCheck.setMayorVer(properties.getProperty("MayorVer").trim());
			// docCheck.setMinorVer(properties.getProperty("MinorVer").trim());
			String minorVer = properties.getProperty("MinorVer").trim();
			if (ToolsHTML.isEmptyOrNull(minorVer)) {
				minorVer = "";
			}
			String mayorVer = properties.getProperty("MayorVer").trim();
			if (ToolsHTML.isEmptyOrNull(mayorVer)) {
				mayorVer = "";
			}
			docCheck.setMayorVer(mayorVer);
			docCheck.setMinorVer(minorVer);
			docCheck.setDateCheckOut(ToolsHTML.formatDateShow(
					properties.getProperty("dateCheckOut"), true));
			docCheck.setNumber(properties.getProperty("number") != null ? properties
					.getProperty("number").trim() : "");
			// docCheck.setPrefix(properties.getProperty("prefix")!=null?properties.getProperty("prefix").trim():"");
			idNode = properties.getProperty("idNode");
			// Se Busca el Prefijo del Documento seg�n como se encuentre
			// Definido el Mismo
			if (struct != null) {
				if (!prefijos.containsKey(idNode)) {
					prefixDoc = ToolsHTML.getPrefixToDoc(struct, idNode);
					prefijos.put(idNode, prefixDoc);
				} else {
					prefixDoc = (String) prefijos.get(idNode);
				}
			} else {
				prefixDoc = properties.getProperty("prefix") != null ? properties
						.getProperty("prefix").trim() : "";
			}
			if (!ToolsHTML.isEmptyOrNull(prefixDoc)) {
				docCheck.setPrefix(prefixDoc.trim());
			} else {
				docCheck.setPrefix("");
			}

			Person p = new Person(Integer.parseInt(properties
					.getProperty("idNorm")),
					properties.getProperty("titleNorm"),
					properties.getProperty("sistema_gestion"),
					properties.getProperty("indice"));

			docCheck.setPersonBean(p);

			resp.add(docCheck);
		}
		return resp;
	}

	// SIMON 22 DE JULIO 2005 INICIO
	public static Collection getAllDocumentsExpiresUser(Users usuario,
			HttpServletRequest request) throws Exception {
		ResourceBundle rb = ToolsHTML.getBundle(request);
		Hashtable docsExpireMsg = new Hashtable();
		StringBuilder mensaje = new StringBuilder(HandlerParameters.PARAMETROS.getMsgDocExpirado());
		mensaje.append("<br>");
		StringBuilder sql = new StringBuilder(100);
		sql.append("SELECT d.numGen,d.nameDocument,vd.MayorVer,vd.MinorVer,vd.dateExpires,vd.ControldateExpires,vd.numver ");
		sql.append(",d.number as number1,vd.dateExpiresDrafts,vd.statu,d.prefix ");
		sql.append(" FROM documents d,versiondoc vd");
		sql.append(" WHERE d.numGen = vd.numDoc");
		sql.append(" AND vd.numVer = (SELECT MAX(vdi.numVer) FROM versiondoc vdi WHERE vdi.numDoc = vd.numDoc)");
		String dateSystem = ToolsHTML.sdfShowConvert1.format(new Date());
		// sql.append(" AND vd.dateExpires <= CONVERT(datetime,'");
		// sql.append(dateSystem).append("',120)");
		if (!ToolsHTML.isEmptyOrNull(usuario.getUser())) {
			sql.append(" AND d.owner = '").append(usuario.getUser())
					.append("'");
		}
		// SIMON 8 DE JULIO 2005 INICIO
		// Todo documento expirado tienbe que estar aprobado para relucirlo en
		// pantalla principal.
		// sql.append(" AND
		// vd.statu='").append(HandlerDocuments.docApproved).append("'");
		// SIMON 8 DE JULIO 2005 FIN

		// YSA 19 DE JUNIO 2007 INICIO
		// Todo documento expirado puede estar aprobado o en borrador para
		// relucirlo en pantalla
		// principal.
		sql.append(" AND ((vd.statu='").append(HandlerDocuments.docApproved)
				.append("'");
		sql.append(" AND vd.dateExpires <= CONVERT(datetime,'");
		sql.append(dateSystem).append("',120))");

		sql.append(" OR (vd.statu='").append(HandlerDocuments.docTrash)
				.append("'");
		sql.append(" AND vd.dateExpiresDrafts <= CONVERT(datetime,'");
		sql.append(dateSystem).append("',120)  ");
		sql.append(" )");

		/*
		 * sql.append(" OR
		 * (vd.statu='").append(HandlerDocuments.docTrash).append("'");
		 * sql.append(" AND vd.dateExpires <= CONVERT(datetime,'");
		 * sql.append(dateSystem).append("',120) "); sql.append(" )");
		 */
		sql.append(" )");

		sql.append(" AND d.active = '").append(Constants.permission)
				.append("'  ");
		sql.append(" AND d.type <> ")
				.append(HandlerDocuments.TypeDocumentsImpresion).append(" ");
		// YSA 19 DE JUNIO 2007 FIN

		String prefixNumber = "";
		log.debug("[getAllDocumentsExpiresUser] " + sql.toString());
		Vector resp = new Vector();
		Vector datos = JDBCUtil.doQueryVector(sql.toString(),Thread.currentThread().getStackTrace()[1].getMethodName());
		boolean swEnviaMensaje = false;
		for (int row = 0; row < datos.size(); row++) {
			Properties properties = (Properties) datos.elementAt(row);
			String statusDoc = properties.getProperty("statu");
			DocumentsCheckOutsBean docCheck = new DocumentsCheckOutsBean();
			String fechaHoy = ToolsHTML.sdfShowWithoutHour.format(new Date());
			String ControldateExpires = properties
					.getProperty("ControldateExpires") != null ? properties
					.getProperty("ControldateExpires") : "";
			prefixNumber = properties.getProperty("prefix") != null ? properties
					.getProperty("prefix") : "";
			String number = properties.getProperty("number1") != null ? properties
					.getProperty("number1") : "";
			prefixNumber = prefixNumber + number;

			if ((ToolsHTML.isEmptyOrNull(ControldateExpires))
					|| (fechaHoy.compareToIgnoreCase(ControldateExpires)) >= 0) {
				// adelantamos un dia a fecha de control , para que ma�ana nos
				// mande un mail de
				// documento expirado.
				long ahora = new Date().getTime() + (24 * 60 * 60 * 1000);
				String fecha_maniana = ToolsHTML.sdfShowWithoutHour
						.format(new Date(ahora));
				log.debug("Fechas expiro dateSystem:" + ControldateExpires
						+ " fechaHoy:" + fechaHoy);
				log.debug("fecha_maniana:" + fecha_maniana);
				StringBuilder sqlUpdate = new StringBuilder(100);
				sqlUpdate.append("UPDATE  versiondoc set ControldateExpires='")
						.append(fecha_maniana).append("'");
				sqlUpdate
						.append(" ")
						.append("where numver=")
						.append(properties.getProperty("numver") != null ? properties
								.getProperty("numver") : "");
				JDBCUtil.doUpdate(sqlUpdate.toString());
				log.debug("[UpdateDocumentsExpiresUser] "
						+ sqlUpdate.toString());
				swEnviaMensaje = true;
			}
			// 05 agosto 2005
			// fin*************************************************************************************
			docCheck.setIdDocument(properties.getProperty("numGen"));
			docCheck.setNameDocument(properties.getProperty("nameDocument"));
			String minorVer = properties.getProperty("MinorVer").trim();
			if (ToolsHTML.isEmptyOrNull(minorVer)) {
				minorVer = "";
			}
			String mayorVer = properties.getProperty("MayorVer").trim();
			if (ToolsHTML.isEmptyOrNull(mayorVer)) {
				mayorVer = "";
			}
			docCheck.setMayorVer(mayorVer);
			docCheck.setMinorVer(minorVer);

			// YSA 13 DE AGOSTO 2007 Si el documento es Borrador se muestra
			// dateExpiresDrafts, si
			// esta probado se muestra dateExpires
			// if ((ToolsHTML.isEmptyOrNull(statusDoc) &&
			// statusDoc.equals(HandlerDocuments.docApproved))
			// ||(ToolsHTML.isEmptyOrNull(properties.getProperty("dateExpiresDrafts"))))
			// {
			if (!ToolsHTML.isEmptyOrNull(statusDoc)
					&& statusDoc.equals(HandlerDocuments.docApproved)) {
				docCheck.setDateCheckOut(ToolsHTML.formatDateShow(
						properties.getProperty("dateExpires"), true));
			} else {
				docCheck.setDateCheckOut(ToolsHTML.formatDateShow(
						properties.getProperty("dateExpiresDrafts"), true));
			}

			// para mandar los nombres del documento expirado por mail
			mensaje.append(rb.getString("mail.docsmessage"))
					.append(":")
					.append(docCheck.getNameDocument())
					.append("<br>")
					.append(rb.getString("mail.docsversion"))
					.append(":")
					.append(docCheck.getMayorVer() + "."
							+ docCheck.getMinorVer());
			mensaje.append("<br>").append(rb.getString("mail.docsdateexpire"))
					.append(":").append(docCheck.getDateCheckOut())
					.append("<br><br>");
			resp.add(docCheck);
		}
		if (swEnviaMensaje) {
			HandlerWorkFlows.notifiedUsers(rb.getString("title_expir") + " "
					+ prefixNumber, rb.getString("mail.nameUser"),
					HandlerParameters.PARAMETROS.getMailAccount(), usuario.getEmail(), mensaje.toString());
		}
		return resp;
	}

	// SIMON 22 DE JULIO 2005 FIN
    // Bandeja Princiapl - Documentos Expirados
	public static Collection<DocumentsCheckOutsBean> getAllDocumentsExpiresUser(
			String owner, Hashtable struct, Hashtable prefijos,
			boolean searchAsAdmin) throws Exception {

		StringBuilder sql = new StringBuilder(100);
		sql.append("SELECT d.numGen,d.nameDocument,vd.MayorVer,vd.MinorVer,vd.dateExpires,");
		sql.append(" d.number,d.prefix,d.idNode,vd.dateExpiresDrafts,vd.statu,");
		sql.append(" p.idperson, p.nombres, p.apellidos, p.email, d.idRegisterClass, vd.numVer ");
		sql.append(" FROM person p, documents d,versiondoc vd");
		sql.append(" WHERE d.numGen = vd.numDoc");
		sql.append(" AND p.nameuser = d.owner");
		sql.append(" AND vd.numVer = (SELECT MAX(vdi.numVer) FROM versiondoc vdi WHERE vdi.numDoc = vd.numDoc)");
		String dateSystem = ToolsHTML.sdfShowConvert1.format(new Date());
		if (!ToolsHTML.isEmptyOrNull(owner) && !searchAsAdmin) {
			sql.append(" AND d.owner = '").append(owner).append("'");
		}
		sql.append(" AND ( "); // inicio de condicion OR
		sql.append(" (vd.statu='").append(HandlerDocuments.docApproved).append("'");
		sql.append(" AND vd.dateExpires <= ").append(ToolsHTML.getTimestampConvert(dateSystem)).append(")");
		// colocamos el filtro de fechas de vencimiento para los documentos del
		// tipo borrador
		sql.append(" OR (vd.statu='").append(HandlerDocuments.docTrash).append("'");
		sql.append(" AND vd.dateExpiresDrafts <= ").append(ToolsHTML.getTimestampConvert(dateSystem)).append(")");
		// colocamos el filtro de fechas para los documentos tipo borrador
		// cuya ultima version aprobada se encuentra vencida
		sql.append(" OR (vd.statu='").append(HandlerDocuments.docTrash).append("'");
		sql.append(" AND (SELECT vdi.dateExpires");
		sql.append(" FROM versiondoc vdi");
		sql.append(" WHERE statu = '").append(HandlerDocuments.docApproved).append("' ");
		sql.append(" AND vdi.numDoc = vd.numDoc ");
		sql.append(" GROUP BY vdi.numVer, vdi.dateExpires");
		sql.append(" HAVING vdi.numVer = MAX(vdi.numVer)) <= ").append(ToolsHTML.getTimestampConvert(dateSystem)).append(")");
		sql.append(" ) "); // fin condicion OR 

		sql.append(" AND d.active = '").append(Constants.permission).append("' ");
		sql.append(" AND d.type <> '").append(HandlerDocuments.TypeDocumentsImpresion).append("' ");
		

		log.debug("[getAllDocumentsExpiresUser] " + sql.toString());
		// //System.out.println("[getAllDocumentsExpiresUser]: " +
		// sql.toString());
		Vector resp = new Vector();
		Vector datos = JDBCUtil.doQueryVector(sql.toString(),Thread.currentThread().getStackTrace()[1].getMethodName());
		String prefixDoc = null;
		String idNode = "";
		for (int row = 0; row < datos.size(); row++) {
			idNode = "";
			Properties properties = (Properties) datos.elementAt(row);
			String statusDoc = properties.getProperty("statu");
			DocumentsCheckOutsBean docCheck = new DocumentsCheckOutsBean();
			docCheck.setIdDocument(properties.getProperty("numGen"));
			docCheck.setNameDocument(properties.getProperty("nameDocument"));
			String minorVer = properties.getProperty("MinorVer").trim();
			if (ToolsHTML.isEmptyOrNull(minorVer)) {
				minorVer = "";
			}
			String mayorVer = properties.getProperty("MayorVer").trim();
			if (ToolsHTML.isEmptyOrNull(mayorVer)) {
				mayorVer = "";
			}
			docCheck.setMayorVer(mayorVer);
			docCheck.setMinorVer(minorVer);

			// YSA 13 DE AGOSTO 2007 Si el documento es Borrador se muestra
			// dateExpiresDrafts, si
			// esta probado se muestra dateExpires
			// if ((!ToolsHTML.isEmptyOrNull(statusDoc) &&
			// statusDoc.equals(HandlerDocuments.docApproved))
			// ||(ToolsHTML.isEmptyOrNull(properties.getProperty("dateExpiresDrafts"))))
			// {
			if (!ToolsHTML.isEmptyOrNull(statusDoc)
					&& statusDoc.equals(HandlerDocuments.docApproved)) {
				docCheck.setDateCheckOut(ToolsHTML.formatDateShow(
						properties.getProperty("dateExpires"), true));
			} else {
				// el documento no es una version aprobada, tal vez es un
				// borrador
				// debemos ver si el vencimiento es del borrador como tal
				// o de la ultima version aprobada de dicho documento
				// no necesariamente las 2 fechas tienen valor al mismo tiempo
				// perpo siempre al menos una debe tener un valor valido
				Date dateApproved = ToolsHTML.getDateFromString(properties
						.getProperty("dateExpires"));
				Date dateDraft = ToolsHTML.getDateFromString(properties
						.getProperty("dateExpires"));
				if (dateApproved != null) {
					if (dateDraft == null) {
						docCheck.setDateCheckOut(ToolsHTML.formatDateShow(
								properties.getProperty("dateExpires"), true));
					} else {
						if (dateApproved.after(dateDraft)) {
							docCheck.setDateCheckOut(ToolsHTML.formatDateShow(
									properties.getProperty("dateExpiresDrafts"),
									true));
						} else {
							docCheck.setDateCheckOut(ToolsHTML.formatDateShow(
									properties.getProperty("dateExpires"), true));
						}
					}
				} else {
					docCheck.setDateCheckOut(ToolsHTML.formatDateShow(
							properties.getProperty("dateExpiresDrafts"), true));
				}
			}

			docCheck.setNumber(properties.getProperty("number") != null ? properties
					.getProperty("number") : "");
			// docCheck.setPrefix(properties.getProperty("prefix")!=null?properties.getProperty("prefix"):"");
			idNode = properties.getProperty("idNode");
			// Se Busca el Prefijo del Documento seg�n como se encuentre
			// Definido el Mismo
			if (struct != null) {
				if (!prefijos.containsKey(idNode)) {
					prefixDoc = ToolsHTML.getPrefixToDoc(struct, idNode);
					prefijos.put(idNode, prefixDoc);
				} else {
					prefixDoc = (String) prefijos.get(idNode);
				}
			} else {
				prefixDoc = properties.getProperty("prefix") != null ? properties
						.getProperty("prefix") : "";
			}

			if (!ToolsHTML.isEmptyOrNull(prefixDoc)) {
				docCheck.setPrefix(prefixDoc.trim());
			} else {
				docCheck.setPrefix("");
			}
			
			docCheck.setIdRegisterClass(properties.getProperty("idRegisterClass") != null ? properties
					.getProperty("idRegisterClass") : "0");

			Person p = new Person(Integer.parseInt(properties
					.getProperty("idperson")),
					properties.getProperty("nombres"),
					properties.getProperty("apellidos"),
					properties.getProperty("email"));

			docCheck.setPersonBean(p);
			
			docCheck.setNumVer(properties.getProperty("numVer"));

			resp.add(docCheck);
		}
		return resp;
	}

	// Hallagos pendientes - Registro del tipo 2 en adelante para sacop
	public static Collection<DocumentsCheckOutsBean> getPendingFindings (
			String idPerson, String owner, Hashtable struct, Hashtable prefijos,
			boolean searchAsAdmin) throws Exception {
		
		String sacopManager = HandlerParameters.PARAMETROS.getListUserAddressee();
		
		if(!ToolsHTML.isEmptyOrNull(sacopManager)) {
			String[] listManager = sacopManager.split(",");
			boolean isAutorized = false;
			for (int i = 0; i < listManager.length; i++) {
				if(listManager[i].equals(idPerson)) {
					isAutorized = true;
					break;
				}
			}
			if(!isAutorized) {
				return new Vector();
			}
		}

		StringBuilder sql = new StringBuilder(100);
		sql.append("SELECT d.numGen,d.nameDocument,vd.MayorVer,vd.MinorVer,vd.dateExpires,");
		sql.append(" d.number,d.prefix,d.idNode,vd.dateExpiresDrafts,vd.statu,");
		sql.append(" p.idperson, p.nombres, p.apellidos, p.email, d.idRegisterClass, vd.numVer ");
		sql.append(" FROM person p, documents d,versiondoc vd");
		sql.append(" WHERE d.numGen = vd.numDoc");
		sql.append(" AND p.nameuser = d.owner");
		sql.append(" AND vd.numVer = (SELECT MAX(vdi.numVer) FROM versiondoc vdi WHERE vdi.numDoc = vd.numDoc) ");
		String dateSystem = ToolsHTML.sdfShowConvert1.format(new Date());
		if (!ToolsHTML.isEmptyOrNull(owner) && !searchAsAdmin) {
			sql.append(" AND d.owner = '").append(owner).append("'");
		}
		sql.append(" AND d.idRegisterClass > 1 "); // es un registro para sacop
		sql.append(" AND ( "); // inicio de OR
		sql.append(" (vd.statu='").append(HandlerDocuments.docApproved).append("'");
		sql.append(" AND vd.dateExpires <= ").append(ToolsHTML.getTimestampConvert(dateSystem)).append(") ");
		// colocamos el filtro de fechas de vencimiento para los documentos del
		// tipo borrador
		sql.append(" OR (vd.statu='").append(HandlerDocuments.docTrash).append("'");
		sql.append(" AND vd.dateExpiresDrafts <= ").append(ToolsHTML.getTimestampConvert(dateSystem)).append(") ");

		// colocamos el filtro de fechas para los documentos tipo borrador
		// cuya ultima version aprobada se encuentra vencida
		sql.append(" OR (vd.statu='").append(HandlerDocuments.docTrash).append("'");
		sql.append(" AND (SELECT vdi.dateExpires");
		sql.append(" FROM versiondoc vdi");
		sql.append(" WHERE statu = '").append(HandlerDocuments.docApproved).append("' ");
		sql.append(" AND vdi.numDoc = vd.numDoc ");
		sql.append(" GROUP BY vdi.numVer, vdi.dateExpires");
		sql.append(" HAVING vdi.numVer = MAX(vdi.numVer)) <= ").append(ToolsHTML.getTimestampConvert(dateSystem)).append(") ");
		sql.append(" ) "); // fin de OR

		sql.append(" AND d.active = '").append(Constants.permission).append("' ");
		sql.append(" AND d.type <> '").append(HandlerDocuments.TypeDocumentsImpresion).append("' ");
		
		sql.append(" AND d.idRegisterClass > 1 "); // es un registro para sacop
		sql.append(" AND d.numGen NOT IN (select idDocumentRelated from tbl_planillasacop1 where idDocumentRelated>0 and estado not in (6,7) and idDocumentRelated=d.numGen group by idDocumentRelated) ");
		
		sql.append(" UNION ");
		sql.append("SELECT d.numGen,d.nameDocument,vd.MayorVer,vd.MinorVer,vd.dateExpires, d.number,d.prefix,d.idNode,vd.dateExpiresDrafts ");
		sql.append(",vd.statu, p.idperson, p.nombres, p.apellidos, p.email, d.idRegisterClass, vd.numVer ");
		sql.append("FROM person p, documents d,versiondoc vd ");
		sql.append("WHERE d.numGen = vd.numDoc AND p.nameuser = d.owner "); 
		sql.append("AND vd.numVer = (SELECT MAX(vdi.numVer) FROM versiondoc vdi WHERE vdi.numDoc = vd.numDoc) "); 
		sql.append("AND d.numGen NOT IN (SELECT idDocumentRelated FROM tbl_planillasacop1 WHERE idDocumentRelated IS NOT NULL) "); // AND estado NOT IN (6,7)) ");
		sql.append("AND d.active = '1'  AND d.type <> '1001'  AND d.idRegisterClass > 1 ");
		sql.append("AND CAST(d.statu AS INT) = ").append(HandlerDocuments.docApproved);
			

		log.debug("[getPendingFindings] " + sql.toString());
		// //System.out.println("[getAllDocumentsExpiresUser]: " +
		// sql.toString());
		Vector resp = new Vector();
		Vector datos = JDBCUtil.doQueryVector(sql.toString(),Thread.currentThread().getStackTrace()[1].getMethodName());
		String prefixDoc = null;
		String idNode = "";
		for (int row = 0; row < datos.size(); row++) {
			idNode = "";
			Properties properties = (Properties) datos.elementAt(row);
			String statusDoc = properties.getProperty("statu");
			DocumentsCheckOutsBean docCheck = new DocumentsCheckOutsBean();
			docCheck.setIdDocument(properties.getProperty("numGen"));
			docCheck.setNameDocument(properties.getProperty("nameDocument"));
			String minorVer = properties.getProperty("MinorVer").trim();
			if (ToolsHTML.isEmptyOrNull(minorVer)) {
				minorVer = "";
			}
			String mayorVer = properties.getProperty("MayorVer").trim();
			if (ToolsHTML.isEmptyOrNull(mayorVer)) {
				mayorVer = "";
			}
			docCheck.setMayorVer(mayorVer);
			docCheck.setMinorVer(minorVer);

			// YSA 13 DE AGOSTO 2007 Si el documento es Borrador se muestra
			// dateExpiresDrafts, si
			// esta probado se muestra dateExpires
			// if ((!ToolsHTML.isEmptyOrNull(statusDoc) &&
			// statusDoc.equals(HandlerDocuments.docApproved))
			// ||(ToolsHTML.isEmptyOrNull(properties.getProperty("dateExpiresDrafts"))))
			// {
			if (!ToolsHTML.isEmptyOrNull(statusDoc)
					&& statusDoc.equals(HandlerDocuments.docApproved)) {
				docCheck.setDateCheckOut(ToolsHTML.formatDateShow(
						properties.getProperty("dateExpires"), true));
			} else {
				// el documento no es una version aprobada, tal vez es un
				// borrador
				// debemos ver si el vencimiento es del borrador como tal
				// o de la ultima version aprobada de dicho documento
				// no necesariamente las 2 fechas tienen valor al mismo tiempo
				// perpo siempre al menos una debe tener un valor valido
				Date dateApproved = ToolsHTML.getDateFromString(properties
						.getProperty("dateExpires"));
				Date dateDraft = ToolsHTML.getDateFromString(properties
						.getProperty("dateExpires"));
				if (dateApproved != null) {
					if (dateDraft == null) {
						docCheck.setDateCheckOut(ToolsHTML.formatDateShow(
								properties.getProperty("dateExpires"), true));
					} else {
						if (dateApproved.after(dateDraft)) {
							docCheck.setDateCheckOut(ToolsHTML.formatDateShow(
									properties.getProperty("dateExpiresDrafts"),
									true));
						} else {
							docCheck.setDateCheckOut(ToolsHTML.formatDateShow(
									properties.getProperty("dateExpires"), true));
						}
					}
				} else {
					docCheck.setDateCheckOut(ToolsHTML.formatDateShow(
							properties.getProperty("dateExpiresDrafts"), true));
				}
			}

			docCheck.setNumber(properties.getProperty("number") != null ? properties
					.getProperty("number") : "");
			// docCheck.setPrefix(properties.getProperty("prefix")!=null?properties.getProperty("prefix"):"");
			idNode = properties.getProperty("idNode");
			// Se Busca el Prefijo del Documento seg�n como se encuentre
			// Definido el Mismo
			if (struct != null) {
				if (!prefijos.containsKey(idNode)) {
					prefixDoc = ToolsHTML.getPrefixToDoc(struct, idNode);
					prefijos.put(idNode, prefixDoc);
				} else {
					prefixDoc = (String) prefijos.get(idNode);
				}
			} else {
				prefixDoc = properties.getProperty("prefix") != null ? properties
						.getProperty("prefix") : "";
			}

			if (!ToolsHTML.isEmptyOrNull(prefixDoc)) {
				docCheck.setPrefix(prefixDoc.trim());
			} else {
				docCheck.setPrefix("");
			}
			
			docCheck.setIdRegisterClass(properties.getProperty("idRegisterClass") != null ? properties.getProperty("idRegisterClass") : "0");

			Person p = new Person(Integer.parseInt(properties
					.getProperty("idperson")),
					properties.getProperty("nombres"),
					properties.getProperty("apellidos"),
					properties.getProperty("email"));

			docCheck.setPersonBean(p);
			
			docCheck.setNumVer(properties.getProperty("numVer"));

			resp.add(docCheck);
		}
		return resp;
	}

	/*
	 * Env�a correo de notificaci�n de documentos expirados al propietario. Se
	 * valida que est� en 1 el campo timeDocVenc de la tabla location � la fecha
	 * de vencimiento sea la fecha actual. Se env�a el correo en los primeros n
	 * d�as (campos txttimeDocVenc de la tabla Location). Env�a correo si est en
	 * 0 el campo notifyEmail de la tabla parameters. Debe estar en 0 el campo
	 * expireDoc o el campo expireDrafts de la tabla parameters (indican que se
	 * toma en cuenta Vigencia de Documentos y Vigencia de Borradores
	 * respectivamente) @param statusDocumento Indica si el documento esta
	 * Aprobado o Rechazado
	 */
	public static Collection getAllNotifDocumentsExpires(Hashtable tree,
			Hashtable prefijos, boolean notifyEmail, String statusDocumento)
			throws Exception {
		Locale defaultLocale = new Locale(
				DesigeConf.getProperty("language.Default"),
				DesigeConf.getProperty("country.Default"));
		ResourceBundle rb = null;
		try {
			rb = ResourceBundle.getBundle("LoginBundle", defaultLocale);
		} catch (java.lang.IllegalStateException e) {
			rb = ResourceBundle.getBundle("LoginBundle",
					new Locale(DesigeConf.getProperty("language.Default"), ""));
		} catch (java.util.MissingResourceException e) {
			rb = ResourceBundle.getBundle("LoginBundle",
					new Locale(DesigeConf.getProperty("language.Default"),
							DesigeConf.getProperty("country.Default")));
		}
		String campo = "";
		String expireDoc = String.valueOf(HandlerParameters.PARAMETROS.getExpireDoc());
		String expireDrafts = String.valueOf(HandlerParameters.PARAMETROS.getExpireDrafts());
		int numVer = 0;
		String dateSystem = ToolsHTML.sdfShowConvert1.format(new Date());
		Vector resp = new Vector();
		String msgDocExpire = "";

		StringBuilder sql = new StringBuilder(100);
		String dateSystemOnlyDate = ToolsHTML.sdf.format(new Date());
		if (dateSystem != null && dateSystem.length() > 9)
			dateSystemOnlyDate = dateSystem.substring(0, 10);

		if ("0".equalsIgnoreCase(expireDoc)
				|| "0".equalsIgnoreCase(expireDrafts)) {
			if (statusDocumento == HandlerDocuments.docApproved) {
				campo = "statuaprovado";
				sql.append("SELECT p.email,");
				switch (Constants.MANEJADOR_ACTUAL) {
				case Constants.MANEJADOR_MSSQL:
					sql.append("(p.Apellidos+' '+p.Nombres) AS namePerson, ");
					break;
				case Constants.MANEJADOR_POSTGRES:
					sql.append("(p.Apellidos||' '||p.Nombres) AS namePerson, ");
					break;
				case Constants.MANEJADOR_MYSQL:
					sql.append("CONCAT(p.Apellidos,' ',p.Nombres) AS namePerson, ");
					break;
				}
				sql.append("d.numGen,d.IdNode,d.nameDocument,vd.MayorVer,vd.MinorVer,vd.dateExpires");
				sql.append(",d.number AS number1,d.prefix,d.dateDocVenc,vd.numVer,vd.emailsExpires ");
				sql.append(" FROM documents d,versiondoc vd,person p");
				sql.append(" WHERE d.owner = p.nameuser AND d.numGen = vd.numDoc");
				sql.append(" AND vd.statuaprovado='0' ");
				sql.append(" AND d.type <> '")
						.append(HandlerDocuments.TypeDocumentsImpresion)
						.append("' ");
				sql.append(" AND vd.numVer = (SELECT MAX(vdi.numVer) FROM versiondoc vdi WHERE vdi.numDoc = vd.numDoc");
				sql.append(" AND vdi.statu = '")
						.append(HandlerDocuments.docApproved).append("')");
				switch (Constants.MANEJADOR_ACTUAL) {
				case Constants.MANEJADOR_MSSQL:
					sql.append(" AND vd.dateExpires <= CONVERT(datetime,'")
							.append(dateSystem).append("',120)");
					break;
				case Constants.MANEJADOR_POSTGRES:
					sql.append(" AND vd.dateExpires <= CAST('")
							.append(dateSystem).append("' as timestamp) ");
					break;
				case Constants.MANEJADOR_MYSQL:
					sql.append(" AND vd.dateExpires <= TIMESTAMP('")
							.append(dateSystem).append("') ");
					break;
				}
				sql.append(" AND vd.DateApproved is not null ");
				sql.append(" AND vd.active = '").append(Constants.permission)
						.append("'  ");
				sql.append(" AND d.active = '").append(Constants.permission)
						.append("'");
				sql.append(" AND (vd.dayEmailExpireSend is null OR ");
				switch (Constants.MANEJADOR_ACTUAL) {
				case Constants.MANEJADOR_MSSQL:
					sql.append(" vd.dayEmailExpireSend < CONVERT(datetime,'")
							.append(dateSystemOnlyDate).append("',120) ");
					break;
				case Constants.MANEJADOR_POSTGRES:
					sql.append(" vd.dayEmailExpireSend <= CAST('")
							.append(dateSystemOnlyDate)
							.append("' as timestamp) ");
					break;
				case Constants.MANEJADOR_MYSQL:
					sql.append(" vd.dayEmailExpireSend <= TIMESTAMP('")
							.append(dateSystemOnlyDate)
							.append("') ");
					break;
				}
				sql.append(") ORDER BY vd.numVer DESC");
				msgDocExpire = HandlerParameters.PARAMETROS.getMsgDocExpirado();
			} else if (statusDocumento == HandlerDocuments.docTrash) {
				campo = "statudraft";
				sql.append("SELECT p.email,");
				switch (Constants.MANEJADOR_ACTUAL) {
				case Constants.MANEJADOR_MSSQL:
					sql.append("(p.Apellidos+' '+p.Nombres) AS namePerson, ");
					break;
				case Constants.MANEJADOR_POSTGRES:
					sql.append("(p.Apellidos||' '||p.Nombres) AS namePerson, ");
					break;
				case Constants.MANEJADOR_MYSQL:
					sql.append("CONCAT(p.Apellidos,' ',p.Nombres) AS namePerson, ");
					break;
				}
				sql.append("d.numGen,d.IdNode,d.nameDocument,vd.MayorVer,vd.MinorVer,vd.dateExpiresDrafts");
				sql.append(",d.number as number1,d.prefix,d.dateDocVenc,vd.numVer,vd.emailsExpires ");
				sql.append(" FROM documents d,versiondoc vd,person p");
				sql.append(" WHERE d.owner = p.nameuser AND d.numGen = vd.numDoc ");
				sql.append(" AND vd.statudraft='0' ");
				sql.append(" AND d.statu= '").append(HandlerDocuments.docTrash)
						.append("'").append(" ");
				sql.append(" AND d.type !='")
						.append(HandlerDocuments.TypeDocumentsImpresion)
						.append("' ");
				sql.append(" AND vd.numVer = (SELECT MAX(vdi.numVer) FROM versiondoc vdi WHERE vdi.numDoc = vd.numDoc");
				sql.append(" AND vd.statu='").append(HandlerDocuments.docTrash)
						.append("')");
				switch (Constants.MANEJADOR_ACTUAL) {
				case Constants.MANEJADOR_MSSQL:
					sql.append(
							" AND vd.dateExpiresDrafts <= CONVERT(datetime,'")
							.append(dateSystem).append("',120)  ");
					break;
				case Constants.MANEJADOR_POSTGRES:
					sql.append(" AND vd.dateExpiresDrafts <= CAST('")
							.append(dateSystem).append("' as timestamp) ");
					break;
				case Constants.MANEJADOR_MYSQL:
					sql.append(" AND vd.dateExpiresDrafts <= TIMESTAMP('")
							.append(dateSystem).append("') ");
					break;
				}
				sql.append(" AND vd.dateExpiresDrafts is not null ");
				sql.append(" AND vd.active = '").append(Constants.permission)
						.append("'  ");
				sql.append(" AND d.active = '").append(Constants.permission)
						.append("'  ");
				switch (Constants.MANEJADOR_ACTUAL) {
				case Constants.MANEJADOR_MSSQL:
					sql.append(
							" AND (vd.dayEmailExpireSend is null OR vd.dayEmailExpireSend < CONVERT(datetime,'")
							.append(dateSystemOnlyDate).append("',120))");
					break;
				case Constants.MANEJADOR_POSTGRES:
					sql.append(
							" AND (vd.dayEmailExpireSend is null OR vd.dayEmailExpireSend < CAST('")
							.append(dateSystemOnlyDate)
							.append("' as timestamp) ) ");
					break;
				case Constants.MANEJADOR_MYSQL:
					sql.append(
							" AND (vd.dayEmailExpireSend is null OR vd.dayEmailExpireSend < TIMESTAMP('")
							.append(dateSystemOnlyDate)
							.append("') ) ");
					break;
				}
				sql.append(" ORDER BY vd.numVer DESC");
				msgDocExpire = HandlerParameters.PARAMETROS.getMsgWFBorrador();
			}	
			String normISO = " ";
			String prefixNumber;
			//System.out.println(sql.toString());
			Vector datos = JDBCUtil.doQueryVector(sql.toString(),Thread.currentThread().getStackTrace()[1].getMethodName());
			PreparedStatement st;
			log.debug("[getAllNotifDocumentsExpires] " + sql.toString());
			boolean notified;
			String fch = null;
			try {
				for (int row = 0; row < datos.size(); row++) {
					Properties properties = (Properties) datos.elementAt(row);
					DocumentsCheckOutsBean docCheck = new DocumentsCheckOutsBean();
					docCheck.setIdDocument(properties.getProperty("numGen"));
					String dateDocVenc = properties.getProperty("dateDocVenc");
					if (!ToolsHTML.isEmptyOrNull(dateDocVenc)) {
						dateDocVenc = ToolsHTML.sdf.format(ToolsHTML.sdf
								.parse(dateDocVenc));
					}

					String idNodeDocument = properties.getProperty("IdNode") != null ? properties
							.getProperty("IdNode") : "0";
					String idLocation = HandlerStruct.getIdLocationToNode(tree,
							idNodeDocument);
					// BaseStructForm location =
					// HandlerStruct.loadStruct(idLocation);
					BaseStructForm location = (BaseStructForm) tree
							.get(idLocation);
					java.util.Date range = new java.util.Date();
					int nums = 0;
					int correosEnviados = 0;
					if (location != null
							&& Constants.permission == location
									.getTimeDocVenc()) {
						if (ToolsHTML.isNumeric(location.getTxttimeDocVenc())) {
							nums = Integer.parseInt(location
									.getTxttimeDocVenc());
							range = ToolsHTML.sumUnitsToDate(
									Calendar.DAY_OF_MONTH, range, -1 * nums);
							fch = ToolsHTML.sdf.format(range);
						}
					}
					if (ToolsHTML.isNumeric(properties
							.getProperty("emailsExpires"))) {
						correosEnviados = Integer.parseInt(properties
								.getProperty("emailsExpires"));
					}
					// SOLO SE ENVIA CORREO SI EL NUMERO DE CORREOS QUE SE DEBE
					// ENVIAR SEGUN LA
					// LOCACION ES MAYOR AL NUMERO DE CORREOS ENVIADOS
					if (nums > correosEnviados) {
						docCheck.setNameDocument(properties
								.getProperty("nameDocument"));
						String minorVer = properties.getProperty("MinorVer")
								.trim();
						if (ToolsHTML.isEmptyOrNull(minorVer)) {
							minorVer = "";
						}
						String mayorVer = properties.getProperty("MayorVer")
								.trim();
						if (ToolsHTML.isEmptyOrNull(mayorVer)) {
							mayorVer = "";
						}
						docCheck.setMayorVer(mayorVer);
						docCheck.setMinorVer(minorVer);

						if (ToolsHTML.isNumeric(properties
								.getProperty("numVer"))) {
							numVer = Integer.parseInt(properties
									.getProperty("numVer"));
						}

						Date fecha = new java.util.Date();
						StringBuilder str = new StringBuilder(
								"UPDATE documents SET dateDocVenc = ? WHERE numGen = ? ");
						try {
							notified = false;
							if (ToolsHTML.isEmptyOrNull(dateDocVenc)) {
								// System.out.println("El Documento se vencio en el dia de Hoy :D");
								ArrayList parametro = new ArrayList();
								parametro.add(new Timestamp(fecha.getTime()));
								parametro.add(Integer.parseInt(docCheck.getIdDocument()));
								JDBCUtil.executeUpdate(str,parametro);
								notified = true;
							} else {
								// El Documento tiene Fecha de Expiraci�n, se
								// procede a Calcular si
								// desde la Fecha de Expiraci�n a la Fecha el
								// mismo ha cumplido con
								// la cantidad de d�as
								// para el env�o de Alertas de Documentos
								// Expirados
								// System.out.println("Documento vencido se verifica la fecha de expiracion del Mismo");
								// //System.out.println("fch = " + fch);

								// //System.out.println("dateDocVenc = " +
								// dateDocVenc);
								if (fch != null) {
									if (dateDocVenc != null
											&& dateDocVenc.compareTo(fch) >= 0) {
										notified = true;
									}
								}
							}
							// //System.out.println("notifyEmail = " +
							// notifyEmail);
							// //System.out.println("notified = " + notified);
							if (notified && notifyEmail) {
								BeanNotifiedWF userMailExp = new BeanNotifiedWF();
								userMailExp
										.setEmail((properties
												.getProperty("email") != null ? properties
												.getProperty("email")
												: HandlerParameters.PARAMETROS.getMailAccount()));
								String nombre = properties
										.getProperty("namePerson") != null ? properties
										.getProperty("namePerson") : "";
								String namedocument = properties
										.getProperty("namedocument") != null ? properties
										.getProperty("namedocument") : "";
								String version = docCheck.getMayorVer() + "."
										+ docCheck.getMinorVer();
								StringBuilder mensaje = new StringBuilder(100);

								if (!ToolsHTML.isEmptyOrNull(msgDocExpire)) {
									mensaje.append(msgDocExpire)
											.append("<br/>");
								}

								mensaje.append(nombre).append(" ")
										.append("<br/>");
								mensaje.append(rb.getString("mail.docVencido"))
										.append(" ").append("<br/>");
								mensaje.append(namedocument)
										.append(" ")
										.append(rb
												.getString("mail.docsversion"))
										.append(" ").append(version);
								userMailExp.setComments(mensaje.toString());

								log.debug("Enviando Notificaci�n de Doc. Expirado a "
										+ userMailExp.getEmail());
								log.debug("Mensaje = "
										+ userMailExp.getComments());
								// Se Busca el Prefijo del Documento...
								String prefixDoc = null;
								if (tree != null) {
									if (!prefijos.containsKey(idNodeDocument)) {
										prefixDoc = ToolsHTML.getPrefixToDoc(
												tree, idNodeDocument);
										prefijos.put(idNodeDocument, prefixDoc);
									} else {
										prefixDoc = (String) prefijos
												.get(idNodeDocument);
									}
								} else {
									prefixDoc = properties
											.getProperty("prefix");
								}
								prefixNumber = prefixDoc != null ? prefixDoc
										: "";
								String number = properties
										.getProperty("number1") != null ? properties
										.getProperty("number1") : "";
								prefixNumber = prefixNumber + number;
								HandlerWorkFlows.notifiedUsers(
										rb.getString("title_expir") + " "
												+ prefixNumber, rb
												.getString("mail.nameUser"),
												HandlerParameters.PARAMETROS.getMailAccount(), userMailExp.getEmail(),
										userMailExp.getComments());

								// //System.out.println("-actualizando en
								// versiondoc emailsExpires=" +
								// (correosEnviados+1) + " para
								// numVer="+numVer);
								// SE AUMENTA EN 1 EL CAMPO emailsExpires DE LA
								// TABLA versiondoc
								HandlerStruct.actualizarVersionDoc(numVer,
										"emailsExpires", correosEnviados + 1);

								// SE ACTUALIZA LA FECHA EN LA QUE SE ENVIO EL
								// CORREO
								// //System.out.println("-actualizando en
								// versiondoc
								// dayEmailExpireSend=" + dateSystem + " para
								// numVer="+numVer);
								// SE AUMENTA EN 1 EL CAMPO dayEmailExpireSend
								// DE LA TABLA
								// versiondoc
								HandlerStruct.actualizarVersionDoc(numVer,
										"dayEmailExpireSend", dateSystem, 1);

							}// if (notified &&
								// Constants.notPermissionSt.compareTo(notifyEmail)==0)

						} catch (Exception e) {
							log.error(e.getMessage());
							e.printStackTrace();
						}
						resp.add(docCheck);

					} else {
						// Se actualiza el campo de aviso de expiracion de
						// version de documento en 1
						HandlerStruct.actualizarVersionDoc(numVer, campo,
								(byte) 1);
					} // end if(nums>correosEnviados)
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		} else {
			log.debug("Ninguno de los mensajes para enviar notificacion de expirados en borrador y aprovados esta activado ");
		} // end if ("0".equalsIgnoreCase(expireDoc) ||
			// "0".equalsIgnoreCase(expireDrafts))

		return resp;
	}

	/*
	 * Modifica el status de una solicitud de impresi�n expirada (en status
	 * solicitada � autorizada). Env�a correo al solicitante y al autorizante.
	 * Toma en cuenta los campos CheckvijenToprint (1 � 0), vijenToprint (n�mero
	 * de d�as) de la tabla Location. Para enviar los correos debe estar en 0 el
	 * campo notifyEmail de la tabla parameters.
	 */
	public static Collection getAllNotifDocumentsPrintlnExpires(Hashtable tree,
			Hashtable prefijos, boolean notifyEmail) throws Exception {
		PreparedStatement st = null;
		Connection con = null;
		Vector resp = new Vector();

		try {
			con = JDBCUtil.getConnection(Thread.currentThread().getStackTrace()[1].getMethodName());
			con.setAutoCommit(false);

			Locale defaultLocale = new Locale(
					DesigeConf.getProperty("language.Default"),
					DesigeConf.getProperty("country.Default"));
			ResourceBundle rb = ResourceBundle.getBundle("LoginBundle",
					defaultLocale);
			StringBuilder sql = new StringBuilder(100);

			sql.append("SELECT tblsimp.numsolicitud,tblsimp.datesolicitud,d.numGen,d.prefix,d.number,d.IdNode,d.nameDocument,vd.MayorVer,vd.MinorVer, ");
			sql.append("psolicitud.emailsolicitante,psolicitud.namePersonSolicitante, ");
			sql.append("pautorizante.emailautorizante,pautorizante.namePersonautorizante ");
			sql.append("FROM documents d,versiondoc vd,tbl_solicitudimpresion tblsimp , ");
			switch (Constants.MANEJADOR_ACTUAL) {
			case Constants.MANEJADOR_MSSQL:
				sql.append("     (select  p.email as emailsolicitante,(p.Apellidos+' '+p.Nombres) AS namePersonSolicitante,p.idperson from person p )psolicitud, ");
				sql.append("     (select  p.email as emailautorizante ,(p.Apellidos+' '+p.Nombres) AS namePersonautorizante,p.idperson from person p )pautorizante ");
				break;
			case Constants.MANEJADOR_POSTGRES:
				sql.append("     (select  p.email as emailsolicitante,(p.Apellidos||' '||p.Nombres) AS namePersonSolicitante,p.idperson from person p )psolicitud, ");
				sql.append("     (select  p.email as emailautorizante ,(p.Apellidos||' '||p.Nombres) AS namePersonautorizante,p.idperson from person p )pautorizante ");
				break;
			case Constants.MANEJADOR_MYSQL:
				sql.append("     (select  p.email as emailsolicitante,CONCAT(p.Apellidos,' ',p.Nombres) AS namePersonSolicitante,p.idperson from person p )psolicitud, ");
				sql.append("     (select  p.email as emailautorizante ,CONCAT(p.Apellidos,' ',p.Nombres) AS namePersonautorizante,p.idperson from person p )pautorizante ");
				break;
			}
			sql.append(" WHERE  d.numGen = vd.numDoc ");
			sql.append(" AND vd.numVer = tblsimp.numVer ");
			sql.append(" AND d.numGen = tblsimp.numgen ");
			sql.append(" AND psolicitud.idPerson = tblsimp.solicitante ");
			sql.append(" AND pautorizante.idPerson = tblsimp.autorizante ");
			sql.append(" AND ");
			sql.append("( tblsimp.statusimpresion='")
					.append(loadsolicitudImpresion.solicitadoprintln)
					.append("' OR ");
			sql.append("  tblsimp.statusimpresion='")
					.append(loadsolicitudImpresion.aprobadoprintln)
					.append("' ) ");

			// para llenar el tree, colocamos el usuario como administrador,el
			// grupo
			// como administrador,
			// la seguridad nula para que carque toda la estructura
			// Hashtable tree =
			// HandlerStruct.loadAllNodes(null,DesigeConf.getProperty("application.admon"),DesigeConf.getProperty("application.admon"),false);
			// Todo documento expirado tienbe que estar aprobado para relucirlo
			// en
			// pantalla principal.
			Vector datos = JDBCUtil.doQueryVector(sql.toString(),Thread.currentThread().getStackTrace()[1].getMethodName());
			String datesolicitud = null;
			String mailSolicitante = "";
			String mailAutorizante = "";
			for (int row = 0; row < datos.size(); row++) {
				Properties properties = (Properties) datos.elementAt(row);
				DocumentsCheckOutsBean docCheck = new DocumentsCheckOutsBean();
				datesolicitud = properties.getProperty("datesolicitud");
				String numsolicitud = properties.getProperty("numsolicitud") != null ? properties
						.getProperty("numsolicitud") : "0";
				docCheck.setIdDocument(properties.getProperty("numGen"));
				docCheck.setMayorVer(properties.getProperty("MayorVer") != null ? properties
						.getProperty("MayorVer") : "");
				docCheck.setMinorVer(properties.getProperty("MinorVer") != null ? properties
						.getProperty("MinorVer") : "");
				String idNodeDocument = properties.getProperty("IdNode") != null ? properties
						.getProperty("IdNode") : "0";
				// String prefixNumber =
				// properties.getProperty("prefix")!=null?properties.getProperty("prefix"):"";
				String prefixNumber = null;
				if (tree != null) {
					if (!prefijos.containsKey(idNodeDocument)) {
						prefixNumber = ToolsHTML.getPrefixToDoc(tree,
								idNodeDocument);
						prefijos.put(idNodeDocument, prefixNumber);
					} else {
						prefixNumber = (String) prefijos.get(idNodeDocument);
					}
				} else {
					prefixNumber = properties.getProperty("prefix");
				}
				String number = properties.getProperty("number") != null ? properties
						.getProperty("number") : "";
				prefixNumber = prefixNumber + number;

				// chequeamos si esta activado el vencimeinto de documentos para
				// ser
				// impresos
				String idLocation = HandlerStruct.getIdLocationToNode(tree,
						idNodeDocument);
				BaseStructForm location = HandlerStruct.loadStruct(idLocation);
				java.util.Date fechalimite = new Date();
				java.util.Date FechaHoy1 = new Date();
				if (Constants.permission == location.getCheckvijenToprint()) {
					int nums = 0;
					// sumamos los dias para obtener la fecha que se debe vencer
					// dicho documento
					if (ToolsHTML.isNumeric(String.valueOf(location
							.getVijenToprint()))) {
						nums = location.getVijenToprint();
						fechalimite = ToolsHTML.sumUnitsToDate(
								Calendar.DAY_OF_MONTH,
								ToolsHTML.sdf.parse(datesolicitud), nums);
					}
				}
				// fechaImpresionlimite es igual a la fecha de solicitud + el
				// numero
				// de dias que se vencera
				// String fechaImpresionlimite =
				// ToolsHTML.date.format(fechalimite);
				String fechaImpresionlimite = ToolsHTML.sdfShowConvert1
						.format(fechalimite);
				// obtenemos la fecha de hoy
				// String FechaHoy = ToolsHTML.date.format(FechaHoy1);
				String FechaHoy = ToolsHTML.sdfShowConvert1.format(FechaHoy1);
				if (fechaImpresionlimite.compareTo(FechaHoy) >= 0) {
					log.debug("fecha util");
				} else {
					log.debug("fecha vencida");
					try {
						// actualizamos la table tbl_solicitudimpresion
						StringBuilder str = new StringBuilder(
								"UPDATE tbl_solicitudimpresion ");
						str.append(" SET statusimpresion= ").append(
								loadsolicitudImpresion.anuladoprintln);
						str.append(" WHERE numGen = ").append(
								docCheck.getIdDocument());
						str.append(" and numsolicitud=").append(numsolicitud);
						st = con.prepareStatement(JDBCUtil.replaceCastMysql(str.toString()));
						st.executeUpdate();
						// actualizamos la table Userworkflows
						StringBuilder userWF = new StringBuilder(
								"Update user_workflows set statu=")
								.append(HandlerWorkFlows.wfuCanceled);
						userWF.append(",").append("result=")
								.append(HandlerWorkFlows.wfuCanceled);
						userWF.append(" where idworkflow in ");
						userWF.append(" (select idworkflow from workflows ")
								.append(" where iddocument=")
								.append(numsolicitud).append(")");
						st = con.prepareStatement(JDBCUtil.replaceCastMysql(userWF.toString()));
						st.executeUpdate();
						// actualizamos la table workflows
						StringBuilder updateWF = new StringBuilder(
								"Update workflows set statu=")
								.append(HandlerWorkFlows.wfuAcepted);
						updateWF.append(",").append("result=")
								.append(HandlerWorkFlows.wfuAcepted);
						updateWF.append(" where iddocument=").append(
								numsolicitud);
						st = con.prepareStatement(JDBCUtil.replaceCastMysql(updateWF.toString()));
						st.executeUpdate();
						StringBuilder actStado = new StringBuilder(
								HandlerDocuments.docObs);
						StringBuilder doc = new StringBuilder(
								"update documents set statu=").append(actStado);
						doc.append(" ,lastOperation=").append(
								HandlerDocuments.lastReview);
						doc.append(" where numgen=").append(numsolicitud);
						st = con.prepareStatement(JDBCUtil.replaceCastMysql(doc.toString()));
						st.executeUpdate();

						BeanNotifiedWF userMailExp = new BeanNotifiedWF();
						String nombre = properties
								.getProperty("namePersonSolicitante") != null ? properties
								.getProperty("namePersonSolicitante") : "";
						String nombreAutorizante = properties
								.getProperty("namePersonautorizante") != null ? properties
								.getProperty("namePersonautorizante") : "";
						String namedocument = properties
								.getProperty("namedocument") != null ? properties
								.getProperty("namedocument") : "";
						String version = docCheck.getMayorVer() + "."
								+ docCheck.getMinorVer();
						StringBuilder mensaje = new StringBuilder(100);

						// mensaje.append(rb.getString("imp.mailcaducosolicitante")).append(nombre).append(" ").append("<br/>");
						mensaje.append(
								rb.getString("imp.mailcaducoautorizante"))
								.append(nombreAutorizante).append(" ")
								.append("<br/>");
						mensaje.append(
								rb.getString("imp.mail.docVencido_autorizante"))
								.append(" ").append("<br/>");
						mensaje.append(namedocument).append(" ")
								.append(prefixNumber).append(" ")
								.append(rb.getString("mail.docsversion"))
								.append(" ").append(version).append("<br>");
						mensaje.append(rb.getString("im.fechasolicitud"))
								.append(ToolsHTML.formatDateShow(
										properties.getProperty("datesolicitud"),
										false));
						userMailExp.setComments(mensaje.toString());
						// manda mensaje por correo al solicitante
						mailSolicitante = properties
								.getProperty("emailsolicitante") != null ? properties
								.getProperty("emailsolicitante") : HandlerParameters.PARAMETROS.getMailAccount();
						userMailExp.setEmail(mailSolicitante);
						// prefixNumber
						// =properties.getProperty("prefix")!=null?properties.getProperty("prefix"):""+properties.getProperty("number")!=null?properties.getProperty("number"):"";
						HandlerWorkFlows.notifiedUsers(
								rb.getString("imp.title_expir") + " - "
										+ prefixNumber, rb
										.getString("mail.nameUser"), HandlerParameters.PARAMETROS.getMailAccount(),
								userMailExp.getEmail(), userMailExp
										.getComments());
						// manda mensaje por correo al autorizante
						mailAutorizante = properties
								.getProperty("emailautorizante") != null ? properties
								.getProperty("emailautorizante") : HandlerParameters.PARAMETROS.getMailAccount();
						userMailExp.setEmail(mailAutorizante);
						if (notifyEmail) {
							HandlerWorkFlows.notifiedUsers(
									rb.getString("imp.title_expir") + " - "
											+ prefixNumber, rb
											.getString("mail.nameUser"),
											HandlerParameters.PARAMETROS.getMailAccount(),
									userMailExp.getEmail(), userMailExp
											.getComments());
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				resp.add(docCheck);
			}
		} catch (Exception e) {
			// TODO: handle exception
			log.info("Error: " + e.getLocalizedMessage(), e);
		} finally {
			try {
				con.setAutoCommit(true);
			} catch (Exception e2) {
				// TODO: handle exception
			}

			log.info("Cerrando conexion a base de datos");
			setFinally(con, st);
		}

		return resp;
	}

	public static void BuscarCheckOutDaniados(BaseDocumentForm forma)
			throws Exception {
		StringBuilder update = new StringBuilder(100);
		StringBuilder update2 = new StringBuilder(100);
		StringBuilder sql = new StringBuilder("");
		// el status lastOperation del documento no puede ser igual a estos
		// estados
		// HandlerDocuments.lastDeleteTrash
		// , si la ultima version sta aprobada
		sql.append("SELECT checkOutBy,lastOperation FROM doccheckout dch,versiondoc vda,documents d");
		sql.append(" WHERE dch.active = '0' AND dch.idDocument = ")
				.append(forma.getIdDocument()).append(" AND d.active='")
				.append(Constants.permissionSt).append("'");
		sql.append("  AND d.numgen=dch.iddocument ");
		sql.append("AND vda.numDoc=dch.idDocument ");
		sql.append(" AND vda.numVer = (SELECT MAX(vdi.numVer) FROM versiondoc vdi");
		sql.append(" WHERE vdi.numDoc = dch.idDocument and vdi.statu='")
				.append(HandlerDocuments.docApproved).append("')");
		log.debug("[BuscarCheckOutDaniados]" + sql.toString());
		Connection con = null;
		PreparedStatement st = null;
		ResultSet rs = null;
		try {
			con = JDBCUtil.getConnection(Thread.currentThread().getStackTrace()[1].getMethodName());
			java.util.Date date = new Date();
			log.debug("[BuscarCheckOutDaniados] = " + update.toString());
			st = con.prepareStatement(JDBCUtil.replaceCastMysql(sql.toString()));
			rs = st.executeQuery();
			if (rs.next()) {
				if (!ToolsHTML.isEmptyOrNull(rs.getString("lastOperation"))) {
					if (rs.getString("lastOperation").equalsIgnoreCase(
							HandlerDocuments.lastDeleteTrash)) {
						String owner = rs.getString("checkOutBy"); // 1
						update.append("UPDATE documents SET lastOperation = ")
								.append(HandlerDocuments.lastCheckIn);
						update.append(" WHERE numgen = ").append(
								forma.getIdDocument());
						update.append(" AND active=").append(
								Constants.permission);
						log.debug("[UpdateCheckOutDaniados]"
								+ update.toString());
						st = con.prepareStatement(JDBCUtil.replaceCastMysql(update.toString()));
						st.executeUpdate();
						forma.setLastOperation(HandlerDocuments.lastCheckIn);
						// atualizamos docCheckOut y el active lo ponemos a cero
						update2.append("UPDATE doccheckout set active=")
								.append(Constants.permission);
						update2.append(" WHERE iddocument=").append(
								forma.getIdDocument());
						st = con.prepareStatement(JDBCUtil.replaceCastMysql(update2.toString()));
						st.executeUpdate();
					}
				}
			}
			// hay una incogruencia.... si el status del documento no sea
			// obsoleta y el status de la
			// ultima version sea obsoleta
			if ((!HandlerDocuments.docObs.equalsIgnoreCase(String.valueOf(forma
					.getStatuDoc())))
					&& (HandlerDocuments.docObs.equalsIgnoreCase(String
							.valueOf(forma.getStatu())))) {
				StringBuilder sqlIncongruencia = new StringBuilder("");
				sqlIncongruencia.append("UPDATE documents set statu=").append(
						HandlerDocuments.docObs);
				sqlIncongruencia.append(" WHERE numgen=").append(
						forma.getIdDocument());
				sqlIncongruencia.append(" AND active=").append(
						Constants.permission);
				st = con.prepareStatement(JDBCUtil.replaceCastMysql(sqlIncongruencia.toString()));
				st.executeUpdate();
				// actualizamos los valores que si deberian ser ..
				forma.setStatuDoc(HandlerDocuments.docObs);
			}
			if (rs != null) {
				rs.close();
			}
		} catch (Exception e) {
			// applyRollback(con);
			setMensaje(e.getMessage());
		} finally {
			setFinally(con, st);
		}
	}

	public static DocumentsCheckOutsBean isCheckOutDoc(String idDocument) {
		StringBuilder sql = new StringBuilder(100);
		// sql.append("SELECT * FROM doccheckout");
		sql.append("SELECT idCheckOut,idDocument,checkOutBy,dateCheckOut,active FROM doccheckout");
		sql.append(" WHERE active = '0' AND idDocument = ").append(idDocument);
		try {
			log.debug("[isCheckOutDoc] = " + sql.toString());
			DocumentsCheckOutsBean bean = new DocumentsCheckOutsBean();
			bean.setDoneBy("");
			bean.setCheckOut(false);
			Connection con = JDBCUtil.connect();
			PreparedStatement ps = null;
			ResultSet rs = null;
			ps = con.prepareStatement(JDBCUtil.replaceCastMysql(sql.toString()));
			rs = ps.executeQuery();
			if (rs.next()) {
				bean.setDoneBy(rs.getString("checkOutBy"));
				bean.setCheckOut(true);
				bean.setDateCheckOut(ToolsHTML.formatDateShow(
						rs.getString("dateCheckOut"), true));
				bean.setIdCheckOut(rs.getString("idCheckOut"));
			}
			if (rs != null)
				rs.close();
			if (ps != null)
				ps.close();
			if (con != null)
				con.close();
			/*
			 * Properties prop = JDBCUtil.doQueryOneRow(sql.toString(),Thread.currentThread().getStackTrace()[1].getMethodName()); if
			 * (prop.getProperty("isEmpty").equalsIgnoreCase("false")) {
			 * bean.setDoneBy(prop.getProperty("checkOutBy"));
			 * bean.setCheckOut(true);
			 * bean.setDateCheckOut(ToolsHTML.formatDateShow
			 * (prop.getProperty("dateCheckOut"),true));
			 * bean.setIdCheckOut(prop.getProperty("idCheckOut")); }
			 */
			return bean;
		} catch (Exception e) {
			log.error(e.getMessage());
			e.printStackTrace();
		}
		return null;
	}

	public static DocumentsCheckOutsBean loadDataLastCheckOutDoc(
			String idDocument) {
		StringBuilder sql = new StringBuilder(100);
		sql.append("SELECT idCheckOut,idDocument,checkOutBy,dateCheckOut,active  FROM doccheckout");
		sql.append(" WHERE active = '1' AND idDocument = ").append(idDocument);
		sql.append(" ORDER BY idCheckOut DESC");
		try {
			log.debug("[isCheckOutDoc] = " + sql.toString());
			DocumentsCheckOutsBean bean = new DocumentsCheckOutsBean();
			bean.setDoneBy("");
			bean.setCheckOut(false);
			Connection con = JDBCUtil.getConnection(Thread.currentThread().getStackTrace()[1].getMethodName());
			PreparedStatement ps = null;
			ResultSet rs = null;
			ps = con.prepareStatement(JDBCUtil.replaceCastMysql(sql.toString()));
			rs = ps.executeQuery();
			if (rs.next()) {
				bean.setDoneBy(rs.getString("checkOutBy") != null ? rs
						.getString("checkOutBy") : "");
				bean.setCheckOut(false);
				bean.setDateCheckOut(ToolsHTML.formatDateShow(
						rs.getString("dateCheckOut"), true));
				bean.setIdCheckOut(rs.getString("idCheckOut") != null ? rs
						.getString("idCheckOut") : "");
			}
			if (rs != null)
				rs.close();
			if (ps != null)
				ps.close();
			if (con != null)
				con.close();
			/*
			 * Vector datos = JDBCUtil.doQueryVector(sql.toString(),Thread.currentThread().getStackTrace()[1].getMethodName()); Properties
			 * prop = (Properties)datos.get(0);
			 * 
			 * if (prop!=null) {
			 * 
			 * bean.setDoneBy(prop.getProperty("checkOutBy")!=null?prop.getProperty
			 * ("checkOutBy"):""); bean.setCheckOut(false);
			 * 
			 * if (!ToolsHTML.isEmptyOrNull(prop.getProperty("dateCheckOut"))){
			 * bean.setDateCheckOut(ToolsHTML.formatDateShow(prop.getProperty(
			 * "dateCheckOut"),true)); }
			 * bean.setIdCheckOut(prop.getProperty("idCheckOut"
			 * )!=null?prop.getProperty("idCheckOut"):""); }
			 */
			return bean;
		} catch (Exception e) {
			log.error(e.getMessage());
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Este m�todo carga los datos necesarios para visualizarlos al usuario de
	 * todos aquellos documentos que se encuentran Relacionados al Documento Id
	 * dado como par�metro
	 * 
	 */
	public static String getDocumentsLinks(String idDocument) {
		StringBuilder sql = new StringBuilder(100);
		// sql.append("SELECT d.numGen,number,nameDocument");
		// sql.append(" FROM documents d,documentslinks dl");
		// sql.append(" WHERE d.numGen = dl.numGen");
		// sql.append(" AND numGenLink = ").append(idDocument);
		sql.append("SELECT d.numGen,number,nameDocument, dl.numVerLink");
		sql.append("  FROM documents d,documentslinks dl");
		sql.append(" WHERE d.numGen = dl.numGenLink");
		sql.append("   AND dl.numGen =  ").append(idDocument);
		sql.append("   AND dl.numVer = (SELECT MAX(dli.numVer) FROM documentslinks dli");
		sql.append("                     WHERE dli.numGen = dl.numGen)");
		StringBuilder data = new StringBuilder(100);
		try {
			Vector datos = JDBCUtil.doQueryVector(sql.toString(),Thread.currentThread().getStackTrace()[1].getMethodName());
			for (int row = 0; row < datos.size(); row++) {
				Properties properties = (Properties) datos.elementAt(row);
				data.append(properties.getProperty("nameDocument"));
				data.append("\n");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (data.length() > 0) {
			return data.toString();
		}
		return null;
	}

	public static Map<String,String> getNameFilesDocuments(String[] idDocuments) {

		Map<String,String> lista = new HashMap<String,String>();
		StringBuilder sql = new StringBuilder();
		
		sql.append("SELECT d.numGen,d.nameFile ");
		sql.append("FROM documents d ");
		sql.append("WHERE d.numGen IN (").append(String.join(",",idDocuments)).append(")");
		try {
			CachedRowSet crs = JDBCUtil.executeQuery(sql, Thread.currentThread().getStackTrace()[1].getMethodName());
			while(crs.next()) {
				lista.put(crs.getString("numGen"), crs.getString("nameFile"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return lista;
	}

	public static String getDocumentsLinksIDs(String idDocument) {
		StringBuilder sql = new StringBuilder(100);
		// sql.append("SELECT d.numGen,number,nameDocument");
		// sql.append(" FROM documents d,documentslinks dl");
		// sql.append(" WHERE d.numGen = dl.numGen");
		// sql.append(" AND numGenLink = ").append(idDocument);
		sql.append("SELECT d.numGen,number,nameDocument, dl.numVerLink");
		sql.append("  FROM documents d,documentslinks dl");
		sql.append(" WHERE d.numGen = dl.numGenLink");
		sql.append("   AND dl.numGen =  ").append(idDocument);
		sql.append("   AND dl.numVer = (SELECT MAX(dli.numVer) FROM documentslinks dli");
		sql.append("                     WHERE dli.numGen = dl.numGen)");
		StringBuilder data = new StringBuilder(100);
		boolean isFirst = true;
		try {
			Vector datos = JDBCUtil.doQueryVector(sql.toString(),Thread.currentThread().getStackTrace()[1].getMethodName());
			for (int row = 0; row < datos.size(); row++) {
				Properties properties = (Properties) datos.elementAt(row);
				if (!isFirst) {
					data.append(",");
				}
				data.append(properties.getProperty("numGen")).append("_")
						.append(properties.getProperty("numVerLink"));
				isFirst = false;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (data.length() > 0) {
			return data.toString();
		}
		return null;
	}

	public static void loadVersion(CheckOutDocForm forma) throws Exception {
		StringBuilder sql = new StringBuilder(100);
		sql.append("SELECT vd.numDoc,vd.MayorVer,vd.MinorVer,vd.approved,vd.dateCreated,vd.dateApproved,vd.expires,");
		sql.append("vd.dateExpires,vd.statu,d.namefile,vd.comments FROM versiondoc vd, documents d WHERE d.numgen = vd.numdoc ");
		sql.append("AND numVer = ").append(forma.getNumVersion());

		log.debug("[loadVersion] = " + sql.toString());
		Properties prop = JDBCUtil.doQueryOneRow(sql.toString(),Thread.currentThread().getStackTrace()[1].getMethodName());
		if (prop.getProperty("isEmpty").equalsIgnoreCase("false")) {
			forma.setNumber(prop.getProperty("numDoc"));
			forma.setMayorVer(prop.getProperty("MayorVer"));
			forma.setMinorVer(prop.getProperty("MinorVer"));
			forma.setApproved(prop.getProperty("approved"));
			forma.setExpires(prop.getProperty("expires"));
			forma.setStatu(prop.getProperty("statu").trim());
			forma.setNameFile(prop.getProperty("namefile").trim());
			forma.setComments(prop.getProperty("comments"));			String dateExpires = prop.getProperty("dateExpires");
			if (!ToolsHTML.isEmptyOrNull(dateExpires)) {
				forma.setDateExpires(ToolsHTML.sdfShowConvert
						.format(ToolsHTML.sdfShowConvert.parse(dateExpires)));
			}
			String dateApproved = prop.getProperty("dateApproved");
			if (!ToolsHTML.isEmptyOrNull(dateApproved)) {
				forma.setDateApproved(ToolsHTML.sdfShowConvert
						.format(ToolsHTML.sdfShowConvert.parse(dateApproved)));
			}
		}
	}

	public synchronized static boolean publicDocsforNode(String ids)
			throws Exception {
		StringBuilder update = new StringBuilder(100);
		update.append("UPDATE documents SET docPublic = CAST(0 as bit) , datePublic = ?");
		update.append(", versionPublic = lastVersionApproved WHERE numGen IN (")
				.append(ids).append(")");
		Connection con = null;
		PreparedStatement st = null;
		boolean resp = false;
		try {
			con = JDBCUtil.getConnection(Thread.currentThread().getStackTrace()[1].getMethodName());
			java.util.Date date = new Date();
			log.debug("[publicDocsforNode] = " + update.toString());
			st = con.prepareStatement(JDBCUtil.replaceCastMysql(update.toString()));
			st.setTimestamp(1, new Timestamp(date.getTime()));
			resp = st.executeUpdate() > 0;
		} catch (Exception e) {
			log.error(e.getMessage());
			applyRollback(con);
			setMensaje(e.getMessage());
			resp = false;
		} finally {
			setFinally(con, st);
		}
		return resp;
	}

	public synchronized static boolean publicDocumentEraser(String id, String comments) throws Exception {
		return publicDocumentEraser(id, comments, null, null);
	}

	public synchronized static boolean publicDocumentEraser(String id, String comments, java.util.Date datePublicDocumentParameter, java.util.Date dateApprovedParameter) throws Exception {
		StringBuilder updateDoc = new StringBuilder();
		StringBuilder updateVer = new StringBuilder();
		StringBuilder updateCheck = new StringBuilder();

		java.util.Date datePublicDocument = new Date();
		if(datePublicDocumentParameter!=null) {
			datePublicDocument = datePublicDocumentParameter;
		}

		updateVer.setLength(0);
		updateVer.append("UPDATE versiondoc SET approved = ");
		updateVer.append(JDBCUtil.getCastAsBitString("0")).append(", dateapproved= ?, expires = ");
		updateVer.append(JDBCUtil.getCastAsBitString("1")).append(", dateexpires=?, statu=?, ");
		updateVer.append("mayorver=?, minorver=? ");
		updateVer.append("WHERE numver = ");
		updateVer.append(JDBCUtil.executeQueryRetornaIds(new StringBuffer("select max(numver) from versiondoc where numdoc=").append(id))).append(" ");

		updateDoc.setLength(0);
		updateDoc.append("UPDATE documents SET docPublic = CAST(0 as bit) , datePublic = ?, statu=?, ");
		updateDoc.append("versionPublic = (select max(numver) from versiondoc where numdoc = ?), ");
		updateDoc.append("lastVersionApproved = (select max(numver) from versiondoc where numdoc = ?), ");
		updateDoc.append("number=?, lastoperation=? ");
		updateDoc.append("WHERE numGen = ?");

		updateCheck.setLength(0);
		updateCheck.append("UPDATE doccheckout SET active=CAST(1 as bit) where idDocument=?");
				
		Connection con = null;
		PreparedStatement st = null;
		boolean resp = false;
		try {
			// antes que todo obtenemos los datos del documento que necesitamos
			StringBuilder sb = new StringBuilder();
			sb.append("SELECT numgen,number,idnode,owner, ");
			sb.append("(select mayorver from versiondoc where numdoc=? and numver=(select max(numver) from versiondoc where numdoc=?)) as mayorver, ");
			sb.append("(select minorver from versiondoc where numdoc=? and numver=(select max(numver) from versiondoc where numdoc=?)) as menorver ");
			sb.append("FROM documents WHERE numgen=? ");

			ArrayList parametros = new ArrayList();
			parametros.add(new Integer(id));
			parametros.add(new Integer(id));
			parametros.add(new Integer(id));
			parametros.add(new Integer(id));
			parametros.add(new Integer(id));
			CachedRowSet crs = JDBCUtil.executeQuery(sb, parametros, Thread.currentThread().getStackTrace()[1].getMethodName());

			BaseDocumentForm forma = new BaseDocumentForm();
			if (!crs.next()) {
				throw new Exception(
						"Documento no existe en la tabla documents -> numgen="
								.concat(id));
			}
			forma.setIdDocument(crs.getString("numgen"));
			forma.setNumber(crs.getString("number"));
			forma.setIdNode(crs.getString("idnode"));
			forma.setOwner(crs.getString("owner"));
			forma.setDocPublic("0");
			forma.setMayorVer(crs.getString("mayorver"));
			forma.setMinorVer(crs.getString("menorver"));

			// INI BUSQUEDA DEL NUMERO DE DOCUMENTO
			if (ToolsHTML.isEmptyOrNull(forma.getNumber())) {
				Hashtable subNodos = new Hashtable();
				Hashtable tree = HandlerStruct.loadAllNodes(null,
						DesigeConf.getProperty("application.admon"),
						DesigeConf.getProperty("application.admon"), subNodos,
						null);

				String idLocation = HandlerStruct.getIdLocation(forma
						.getIdNode());

				String number = HandlerParameters.getLengthNumberDocuments();
				StringBuilder numero = new StringBuilder("");
				// se genera el n�mero del documento
				if (number != null) {
					String numByLocation = String.valueOf(HandlerParameters.PARAMETROS.getNumDocLoc());
					String numberDoc = EditDocumentAction.numCorrelativo(
							forma.getIdNode(), idLocation, numByLocation, null,
							tree);
					for (int cont = numberDoc.length(); cont < Integer
							.parseInt(number); cont++) {
						numero.append("0");
					}
					numero.append(numberDoc);
				}
				forma.setNumber(numero.toString());
			}
			// FIN BUSQUEDA DEL NUMERO DE DOCUMENTO

			// INI FECHA DE VENCIMIENTO DEL DOCUMENTO
			forma.setDateExpires(ToolsHTML.calculateDateExpires(
					ToolsHTML.sdf.format(datePublicDocument), forma.getDocPublic(),
					HandlerDocuments.getTypeDocument(forma.getIdDocument())));
			if (forma.getDateExpires() == null) {
				forma.setDateExpires(ToolsHTML.sdf.format(datePublicDocument));
			}
			// FIN FECHA DE VENCIMIENTO DEL DOCUMENTO

			// INI INCREMENTA LA NUEVA VERSION DEL DOCUMENTO
			boolean isFirstVersion = isFirstVersionDocument(Integer
					.parseInt(id));
			if (ToolsHTML.isNumeric(forma.getMayorVer())) {
				int versionMayor = Integer.parseInt(forma.getMayorVer().trim());
				// Buscamos si es la primera version
				forma.setMayorVer(String.valueOf(versionMayor == 0
						&& isFirstVersion ? versionMayor : versionMayor + 1));
			} else {
				String versionMayor = forma.getMayorVer().trim().toUpperCase();

				forma.setMayorVer(String.valueOf(versionMayor.equals("A")
						&& isFirstVersion ? versionMayor : ToolsHTML
						.incVersion(forma.getMayorVer().trim())));
			}
			if (!ToolsHTML.isEmptyOrNull(forma.getMinorVer())) {
				if (ToolsHTML.isNumeric(forma.getMinorVer().trim())) {
					forma.setMinorVer("0");
				} else {
					forma.setMinorVer("");
				}
			}
			// FIN INCREMENTA LA NUEVA VERSION DEL DOCUMENTO

			con = JDBCUtil.getConnection(Thread.currentThread().getStackTrace()[1].getMethodName());
			con.setAutoCommit(false);

			st = con.prepareStatement(JDBCUtil.replaceCastMysql(updateVer.toString()));
	
			if(comments=="Registro proveniente de Sacop") {
				
				st.setTimestamp(1,new Timestamp(datePublicDocumentParameter.getTime()));					
			} else {
				st.setTimestamp(1, new Timestamp( dateApprovedParameter!=null?  dateApprovedParameter.getTime() : datePublicDocument.getTime()));
				
			}
			
			st.setTimestamp(2,new Timestamp(ToolsHTML.sdf.parse(forma.getDateExpires()).getTime()));
			st.setInt(3, new Integer(HandlerDocuments.docApproved));
			st.setString(4, forma.getMayorVer());
			st.setString(5, forma.getMinorVer());
			resp = st.executeUpdate() > 0;
			if (!resp)
				throw new Exception(
						"No se ejecuto correctamente la actualizacion de la version");

			st = con.prepareStatement(JDBCUtil.replaceCastMysql(updateDoc.toString()));
			st.setTimestamp(1, new Timestamp(datePublicDocument.getTime()));
			st.setInt(2, new Integer(HandlerDocuments.docApproved));
			st.setInt(3, new Integer(id));
			st.setInt(4, new Integer(id));
			st.setString(5, forma.getNumber());
			st.setString(6, HandlerDocuments.lastApproved);
			st.setInt(7, new Integer(id));
			resp = st.executeUpdate() > 0;
			if (!resp)
				throw new Exception(
						"No se ejecuto correctamente la actualizacion del documento");

			st = con.prepareStatement(JDBCUtil.replaceCastMysql(updateCheck.toString()));
			st.setInt(1, new Integer(id));
			st.executeUpdate();
			
			forma.setComments(comments);
			HandlerDocuments.updateHistoryDocs(con,
					Integer.parseInt(forma.getIdDocument()), 0, 0,
					forma.getOwner(), new Timestamp(datePublicDocument.getTime()), "14",
					forma.getComments() != null ? forma.getComments() : null,
					new String[] { forma.getMayorVer(), forma.getMinorVer() });

		} catch (Exception e) {
			applyRollback(con);
			try {
				setMensaje(e.getMessage());
			} catch (Exception exc) {
				// no hacemos nada
			}
			resp = false;
			e.printStackTrace();
		} finally {
			setFinally(con, st);
		}
		return resp;
	}
	
	public synchronized static boolean ratifyDocumentExpired(String id, String comments) throws Exception {
		StringBuilder updateVer = new StringBuilder();
		StringBuilder queryExpire = new StringBuilder();

		java.util.Date date = new Date();

		updateVer.setLength(0);
		updateVer.append("UPDATE versiondoc SET ");
		updateVer.append("approved = ").append(JDBCUtil.getCastAsBitString("0")).append(",");
		updateVer.append("expires = ").append(JDBCUtil.getCastAsBitString("1")).append(",");
		updateVer.append("dateexpires=?, statu=? ");
		updateVer.append("WHERE numver = ");
		updateVer.append(JDBCUtil.executeQueryRetornaIds(new StringBuffer("select max(numver) from versiondoc where numdoc=").append(id))).append(" ");

		queryExpire.setLength(0);
		queryExpire.append("SELECT dateApproved,dateExpires FROM versiondoc WHERE numDoc=? ORDER BY numVer DESC ");
				
		Connection con = null;
		PreparedStatement st = null;
		boolean resp = false;
		try {
			
			ArrayList parametros = new ArrayList();
			parametros.add(new Integer(id));
			CachedRowSet crsExpire = JDBCUtil.executeQuery(queryExpire, parametros, Thread.currentThread().getStackTrace()[1].getMethodName());
			
			// antes que todo obtenemos los datos del documento que necesitamos
			StringBuilder sb = new StringBuilder();
			sb.append("SELECT numgen,number,idnode,owner,nameDocument, ");
			sb.append("(select mayorver from versiondoc where numdoc=? and numver=(select max(numver) from versiondoc where numdoc=?)) as mayorver, ");
			sb.append("(select minorver from versiondoc where numdoc=? and numver=(select max(numver) from versiondoc where numdoc=?)) as menorver ");
			sb.append("FROM documents WHERE numgen=? ");

			parametros = new ArrayList();
			parametros.add(new Integer(id));
			parametros.add(new Integer(id));
			parametros.add(new Integer(id));
			parametros.add(new Integer(id));
			parametros.add(new Integer(id));
			CachedRowSet crs = JDBCUtil.executeQuery(sb, parametros, Thread.currentThread().getStackTrace()[1].getMethodName());

			BaseDocumentForm forma = new BaseDocumentForm();
			if (!crs.next()) {
				throw new Exception("Documento no existe en la tabla documents -> numgen=".concat(id));
			}
			forma.setIdDocument(crs.getString("numgen"));
			forma.setNumber(crs.getString("number"));
			forma.setIdNode(crs.getString("idnode"));
			forma.setOwner(crs.getString("owner"));
			forma.setDocPublic("0");
			forma.setMayorVer(crs.getString("mayorver"));
			forma.setMinorVer(crs.getString("menorver"));
			forma.setNameDocument(crs.getString("nameDocument"));
			
			//buscamos el correo del propietario
			ArrayList parametrosOwner = new ArrayList();
			parametrosOwner.add(forma.getOwner());
			StringBuffer sbOwner = new StringBuffer("SELECT email FROM person WHERE accountActive = 1 AND nameUser = ?");
			CachedRowSet crsOwner = JDBCUtil.executeQuery(sbOwner, parametrosOwner, Thread.currentThread().getStackTrace()[1].getMethodName());
			String emailOwner = null;
			if(crsOwner.next()) {
				emailOwner = crsOwner.getString("email");
			}

			
			// INI FECHA DE VENCIMIENTO DEL DOCUMENTO
			forma.setDateExpires(ToolsHTML.calculateDateExpires(
					ToolsHTML.sdf.format(date), forma.getDocPublic(),
					HandlerDocuments.getTypeDocument(forma.getIdDocument())));
			
			if (forma.getDateExpires() == null) {
				forma.setDateExpires(ToolsHTML.sdf.format(date));
			}
			// FIN FECHA DE VENCIMIENTO DEL DOCUMENTO

			con = JDBCUtil.getConnection(Thread.currentThread().getStackTrace()[1].getMethodName());
			con.setAutoCommit(false);

			st = con.prepareStatement(JDBCUtil.replaceCastMysql(updateVer.toString()));
			st.setTimestamp(1,new Timestamp(ToolsHTML.sdf.parse(forma.getDateExpires()).getTime()));
			st.setInt(2, new Integer(HandlerDocuments.docApproved));
			resp = st.executeUpdate() > 0;
			if (!resp)
				throw new Exception("No se ejecuto correctamente la actualizacion de la version");
						
			String dateExpireCurrent = "";
			if(crsExpire.next()) {
				System.out.println(crsExpire.getTimestamp("dateExpires").getTime());
				dateExpireCurrent = ToolsHTML.sdf.format(new Date(crsExpire.getTimestamp("dateExpires").getTime()));
			}
			String dateExpireNew = forma.getDateExpires();

			StringBuilder commentsFinal = new StringBuilder();
			commentsFinal.append("Exp.: <b>").append(dateExpireCurrent).append("</b> -> <b>").append(dateExpireNew).append("</b><br/><br/>");
			commentsFinal.append(comments);
			
			// actualiza el historico
			forma.setComments(commentsFinal.toString());
			HandlerDocuments.updateHistoryDocs(con,
					Integer.parseInt(forma.getIdDocument()), 0, 0,
					forma.getOwner(), new Timestamp(date.getTime()), "25",
					forma.getComments() != null ? forma.getComments() : null,
					new String[] { forma.getMayorVer(), forma.getMinorVer() });
			
			// lo eliminamos del vector de proximos a vencer
			if(HandlerParameters.DOCUMENTOS_POR_VENCER!=null) {
				int idDocument = Integer.parseInt(forma.getIdDocument());
				if(HandlerParameters.DOCUMENTOS_POR_VENCER.contains(idDocument)) {
					for(int x=0; x<HandlerParameters.DOCUMENTOS_POR_VENCER.size();x++) {
						if(HandlerParameters.DOCUMENTOS_POR_VENCER.get(x)==idDocument) {
							HandlerParameters.DOCUMENTOS_POR_VENCER.remove(x); // eliminado
						}
					}
				}
			}
			
			// enviamos el correo
			if(emailOwner!=null) {
				Locale defaultLocale = new Locale(DesigeConf.getProperty("language.Default"),DesigeConf.getProperty("country.Default"));
				ResourceBundle rb = ResourceBundle.getBundle("LoginBundle",	defaultLocale);			
				String titleMail = rb.getString("rat.outTitleMail").concat(" - ").concat(forma.getMayorVer()).concat(".").concat(forma.getMinorVer());
				String userMail = rb.getString("mail.nameUser");
				String account = HandlerParameters.PARAMETROS.getMailAccount();
				String email = emailOwner;
				String commentsMain = forma.getNumber().concat(" - ").concat(forma.getNameDocument()).concat("<br><br>").concat(forma.getComments());
				HandlerWorkFlows.notifiedUsers(titleMail, userMail, account , email, commentsMain);
			}
		} catch (Exception e) {
			applyRollback(con);
			try {
				setMensaje(e.getMessage());
			} catch (Exception exc) {
				// no hacemos nada
			}
			resp = false;
			e.printStackTrace();
		} finally {
			setFinally(con, st);
		}
		return resp;
	}
	
	

	public synchronized static void createNewVersionDoc(CheckOutDocForm forma,
			Connection con, Timestamp time, InputStream stream)
			throws Exception {
		int numVer = HandlerStruct
				.proximo("versiondoc", "versiondoc", "numVer");
		String[] items = new String[] { "monthExpireDoc", "unitTimeExpire" };
		String[] values = HandlerParameters.getFieldsExpired(items,
				HandlerDocuments.getTypeDocument(String.valueOf(forma
						.getIdDocument())));
		String monthExpireDoc = values != null ? values[0] : null;
		String unit = values != null ? values[1].trim() : null;
		int value = 0;
		forma.setNumVer(numVer);
		getPreparementStatementVersionInsert(forma, con, value, unit, time,
				forma.getDateExpires());
		HandlerStruct.saveNewVersionBD(con, numVer, stream, forma);
	}

	private static void getPreparementStatementVersionInsert(
			CheckOutDocForm forma, Connection con, int value, String unitTime,
			Timestamp time, String dateExpireVersionAnt) throws Exception {
		PreparedStatement st = null;
		log.debug("[getPreparementStatementVersionInsert]");
		java.util.Date dateExpire = ToolsHTML.getDateExpireDocument(
				forma.getDateApproved(), forma.getDateExpires(), value,
				unitTime);
		String dateExp = null;
		if (dateExpireVersionAnt != null) {
			log.debug("dateExpireVersionAnt = " + dateExpireVersionAnt);
			dateExp = dateExpireVersionAnt;
		}
		if (dateExpire != null) {
			dateExp = ToolsHTML.sdf.format(dateExpire);
		}

		// YSA 09 DE OCTUBRE 2007 INICIO
		String[] itemsDrafts = new String[] { "monthsExpireDrafts",
				"unitTimeExpireDrafts" };
		String[] valuesDrafts = HandlerParameters.getFieldsExpired(itemsDrafts,
				HandlerDocuments.getTypeDocument(String.valueOf(forma
						.getIdDocument())));
		String monthsExpireDrafts = valuesDrafts != null ? valuesDrafts[0]
				: null;
		String unitDrafts = valuesDrafts != null ? valuesDrafts[1].trim()
				: null;
		int valueDrafts = 0;
		if (ToolsHTML.isNumeric(monthsExpireDrafts.trim())) {
			valueDrafts = Integer.parseInt(monthsExpireDrafts.trim());
		}

		String dateDrafts = (String) time.toString();
		java.util.Date dateExpireDrafts = ToolsHTML.getDateExpireDocument(
				dateDrafts, forma.getDateExpiresDrafts(), valueDrafts,
				unitDrafts);
		String dateExpDrafts = null;
		if (dateExpireDrafts != null) {
			dateExpDrafts = ToolsHTML.sdf.format(dateExpireDrafts);
		}

		// YSA 09 DE OCTUBRE 2007 FIN
		StringBuffer fields = new StringBuffer("?,?,?,?,CAST(? as bit)");
		StringBuffer update = new StringBuffer(
				"INSERT INTO versiondoc (numVer,numDoc,dateCreated,statu,typeWF");
		ToolsHTML.setFieldInQuery(update, fields, forma.getMayorVer(),
				"MayorVer");
		ToolsHTML.setFieldInQuery(update, fields, forma.getMinorVer(),
				"MinorVer");
		ToolsHTML.setFieldInQuery(update, fields,
				JDBCUtil.getByte(forma.getApproved(), true), "approved");
		ToolsHTML.setFieldInQuery(update, fields, forma.getDateApproved(),
				"dateApproved");
		ToolsHTML.setFieldInQuery(update, fields,
				JDBCUtil.getByte(forma.getExpires(), true), "expires");
		ToolsHTML.setFieldInQuery(update, fields, dateExp, "dateExpires");
		ToolsHTML.setFieldInQuery(update, fields, dateExpDrafts,
				"dateExpiresDrafts");
		ToolsHTML.setFieldInQuery(update, fields, forma.getComments(),
				"comments");
		update.append(") Values (").append(fields.toString()).append(")");
		st = con.prepareStatement(JDBCUtil.replaceCastMysql(update.toString()));
		st.setInt(1, forma.getNumVer());
		log.info("[int]numver='" + forma.getNumVer() + "'");
		st.setInt(2, Integer.parseInt(forma.getIdDocument()));
		log.info("[int]numdoc='" + Integer.parseInt(forma.getIdDocument())
				+ "'");
		st.setTimestamp(3, time);
		log.info("[timestamp]datecreated='" + time.toString() + "'");
		st.setString(4, forma.getStatu());
		log.info("[String]statu='" + forma.getStatu() + "'");
		st.setInt(5, forma.getTypeWF());
		log.info("[int]typewf='" + forma.getTypeWF() + "'");

		int pos = 6;
		Integer values = null;
		pos = ToolsHTML.setValueInQuery(st, forma.getMayorVer(), pos);
		log.info("[String]MayorVer='" + forma.getMayorVer() + "'");
		pos = ToolsHTML.setValueInQuery(st, forma.getMinorVer(), pos);
		log.info("[String]MinorVer='" + forma.getMinorVer() + "'");

		if (forma.getApproved() != null) {
			if (ToolsHTML.isNumeric(forma.getApproved().trim())) {
				values = new Integer(forma.getApproved().trim());
				pos = ToolsHTML.setValueInQuery(st, values, pos);
				log.info("[bit]approved='" + values + "'");

				if ((values.intValue() == 0)
						&& ToolsHTML.isEmptyOrNull(forma.getDateApproved())) {
					throw new Exception(
							"La Fecha de Aprobaci�n debe contener un Valor V�lido: "
									+ forma.getDateApproved());
				}
			}
		}
		pos = ToolsHTML.setValueInQuery(st, forma.getDateApproved(), 4, pos);
		log.info("[datetime]dateApproved='" + forma.getDateApproved() + "'");
		if (forma.getExpires() != null) {
			if (ToolsHTML.isNumeric(forma.getExpires().trim())) {
				values = new Integer(forma.getExpires().trim());
				pos = ToolsHTML.setValueInQuery(st, values, pos);
				log.info("[bit]expires='" + values + "'");
				if ((values.intValue() == 0)
						&& ToolsHTML.isEmptyOrNull(forma.getExpires())) {
					throw new Exception(
							"La Fecha de Expiraci�n debe contener un Valor V�lido");
				}
			}
		}
		pos = ToolsHTML.setValueInQuery(st, dateExp, 4, pos);
		log.info("[datetime]dateExpires='" + dateExp + "'");
		pos = ToolsHTML.setValueInQuery(st, dateExpDrafts, 4, pos);
		log.info("[datetime]dateExpiresDrafts='" + dateExpDrafts + "'");
		pos = ToolsHTML.setValueInQuery(st, forma.getComments(), pos);
		log.info("[String]comments='"
				+ forma.getComments()
				+ "', length("
				+ (forma.getComments() == null ? 0 : forma.getComments()
						.length()) + ")");
		st.executeUpdate();

		// actualizamos el nombre del documento y el propietario en la tabla
		// version doc
		StringBuilder query = new StringBuilder();
		query.append("UPDATE versiondoc SET ");
		query.append("nameDocVersion=(select namedocument from documents where numgen=?), ");
		query.append("ownerVersion=(select owner from documents where numgen=?) ");
		query.append("WHERE numver=? ");
		st = con.prepareStatement(JDBCUtil.replaceCastMysql(query.toString()));
		st.setInt(1, Integer.parseInt(forma.getIdDocument()));
		st.setInt(2, Integer.parseInt(forma.getIdDocument()));
		st.setInt(3, forma.getNumVer());
		st.executeUpdate();
	}

	public static void setActiveCheckOut(String idCheckOut, String active,
			Connection con) throws Exception {
		StringBuilder sql = new StringBuilder(50);
		sql.append("UPDATE doccheckout SET active = '").append(active)
				.append("'");
		sql.append(" WHERE idCheckOut = ").append(idCheckOut);
		log.debug("[setActiveCheckOut] = " + sql.toString());
		PreparedStatement st = con.prepareStatement(JDBCUtil.replaceCastMysql(sql.toString()));
		st.executeUpdate();
	}

	public static String[] getFieldsToVersionMayor(String[] campos, int numDoc) {
		return getFieldsToVersionMayor(null, campos, numDoc);
	}
	
	public static String[] getFieldsToVersionMayor(Connection con, String[] campos, int numDoc) {
		StringBuilder sql = new StringBuilder();
		if (campos != null) {
			String result[] = new String[campos.length];
			sql.append("SELECT ");
			for (int pos = 0; pos < campos.length; pos++) {
				sql.append(campos[pos]);
				// Si no es el �ltimo Campo Solicitado Agrego una coma
				if (pos + 1 != campos.length) {
					sql.append(",");
				}
			}
			sql.append("  FROM versiondoc ");
			sql.append(" WHERE numDoc = ").append(numDoc)
					.append(" AND active = '1' ");
			sql.append(
					"   AND numVer = (SELECT MAX(numVer) FROM versiondoc WHERE numDoc = ")
					.append(numDoc).append(")");
			try {
				Properties prop = JDBCUtil.doQueryOneRow(sql.toString(),Thread.currentThread().getStackTrace()[1].getMethodName());
				if ("false".equalsIgnoreCase(prop.getProperty("isEmpty"))) {
					for (int pos = 0; pos < campos.length; pos++) {
						result[pos] = prop.getProperty(campos[pos]);
					}
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}
			return result;
		}
		return null;
	}

	// updateHistoryDocs(con,idDocument,0,0,owner,time,checkOut,null);
	public static void updateHistoryDocs(Connection con, int idDoc,
			int idNodeAnt, int idNodeNew, String nameUser,
			Timestamp dateChange, String type, String comments,
			String[] datosVersion) throws Exception {
		// int idHistory = IDDBFactorySql.getNextID("historydocs");
		int idHistory = HandlerStruct.proximo(con, "historydocs",
				"historydocs", "idHistory");
		PreparedStatement st = null;
		StringBuilder insert = new StringBuilder(100);
		// insert.append("INSERT INTO historydocs
		// (idHistory,idNode,idNodeFatherAnt,idNodeFatherNew,nameUser,dateChange,type,comments)");
		// insert.append(" VALUES(?,?,?,?,?,?,?,?)");
		// String[] datosVersion = getFieldsToVersionMayor(new String[]
		// {"MayorVer","MinorVer"},idDoc);
		if (datosVersion != null) {
			insert.append("INSERT INTO historydocs (idHistory,idNode,idNodeFatherAnt,idNodeFatherNew,nameUser,");
			insert.append("dateChange,type,comments,MayorVer,MinorVer)");
			insert.append(" VALUES(?,?,?,?,?,?,?,?,?,?)");
			st = con.prepareStatement(JDBCUtil.replaceCastMysql(insert.toString()));
			st.setInt(1, idHistory);
			st.setInt(2, idDoc);
			st.setInt(3, idNodeAnt);
			st.setInt(4, idNodeNew);
			st.setString(5, nameUser);
			st.setTimestamp(6, dateChange);
			st.setString(7, type);
			st.setString(8, comments);
			st.setString(9, datosVersion[0]);
			st.setString(10, datosVersion[1]);
			st.executeUpdate();

			if (type.equals("14")) { // 14 es el codigo de aprobacion en el
										// historico de documento
				// enviamos a los correos de la lista de distribucion
				HandlerDocuments.sendMailToListDist(con, idDoc);
			}
		} else {
			throw new ApplicationExceptionChecked("0056");
		}
	}

	public synchronized static boolean checkInDocumen(CheckOutDocForm forma,
			String path, String owner) {
		boolean resp = false;
		Connection con = null;
		PreparedStatement st = null;
		try {
			boolean esDobleVersion = forma.getNameFileParalelo() != null;
			log.info("Check-in de doble version " + esDobleVersion);

			if (forma != null && !forma.isDraftVersion()) {
				log.info("I forma.getMinorVer() = " + forma.getMinorVer());
				log.info("I forma.getNumVersion() = " + forma.getNumVersion());
				
				// Se abre la conexion a la base de datos
				con = JDBCUtil.getConnection(Thread.currentThread().getStackTrace()[1].getMethodName());
				con.setAutoCommit(false);

				int originalNumVer = Integer.parseInt(forma.getNumVersion());
				int numVer = HandlerStruct.proximo(con, "versiondoc", "versiondoc",
						"numVer");
				
				String monthExpireDoc = HandlerParameters.PARAMETROS.getMonthsExpireDocs();
				String unit = HandlerParameters.PARAMETROS.getUnitTimeExpire();
				
				// InputStream imagenFile = getFileBDCheckOut(con,
				// Integer.parseInt(forma.getIdCheckOut()));
				InputStream vAnt = null;
				// TODO: CAMBIO PARA DOBLE VERSION
				if (HandlerStruct.isVersionDocView(con,
						Integer.parseInt(forma.getNumVersion())) != null) {
					vAnt = HandlerStruct.recuperarFileBDView(con, Integer
							.parseInt(forma.getNumVersion()));
				} else {
					vAnt = HandlerStruct.recuperarFileBD(con, Integer.parseInt(forma
							.getNumVersion()));
				}
				int value = 0;
				Timestamp timeCreate = new Timestamp(
						new java.util.Date().getTime());
				if (ToolsHTML.isNumeric(monthExpireDoc.trim())) {
					value = Integer.parseInt(monthExpireDoc.trim());
				}
				forma.setNumVer(numVer);
				forma.setStatu(docTrash);
				// TODO: CAMBIO PARA DOBLE VERSION
				forma.setPath(path);

				long idBackUp = IDDBFactorySql.getNextIDLong(con, "BackUp");
				java.util.Date date = ToolsHTML.sdfShow.parse(forma
						.getDateCreated());
				Timestamp created = new Timestamp(date.getTime());

				if (vAnt != null) {
					backUpFile(con, idBackUp,
							Integer.parseInt(forma.getIdDocument()),
							forma.getMayorVer(), forma.getMinorVer(), vAnt,
							created, null);
				}
				if (forma.isChangeMinor()) {
					if (ToolsHTML.isNumeric(forma.getMinorVer())) {
						int minor = Integer
								.parseInt(forma.getMinorVer().trim()) + 1;
						forma.setMinorVer(String.valueOf(minor));
					} else {
						if (!ToolsHTML.isEmptyOrNull(forma.getMinorVer())) {
							forma.setMinorVer(ToolsHTML.incVersion(forma
									.getMinorVer().trim()));
						} else {
							// forma.setMinorVer("");
							forma.setMinorVer("A");
						}
					}
				}

				getPreparementStatementVersionInsert(forma, con, value, unit,
						timeCreate, null);

				setActiveCheckOut(forma.getIdCheckOut(), "1", con);
				upLastOperationDoc(Integer.parseInt(forma.getIdDocument()),
						lastCheckIn, con);
				if ("0".equalsIgnoreCase(DesigeConf.getProperty("serverType"))) {
					// HandlerStruct.copyVersionDoc(con, imagenFile,
					// forma.getNumVer());
					HandlerStruct.copyVersionDocFromCheckOut(con,
							Integer.parseInt(forma.getIdCheckOut()),
							forma.getNumVer());
					// TODO: CAMBIO PARA DOBLE VERSION
					//ydavila Ticket 001-00-003196
					if (!esDobleVersion) {
						ToolsHTML.deleteVersionCache(con, String.valueOf(forma.getNumVersion()));
						
						/*
						 * Copiamos el archivo cache de docchekout a la nueva version
						 */
						HandlerStruct.restorePdfFromCachedCheckOut(con, numVer,Integer.parseInt(forma.getIdCheckOut()));
						
					} else {
						HandlerStruct.saveDocView(con, forma);
					}
					
					
					log.info("Creando version, sin un borrador previo.");
				} else {
					HandlerStruct.saveBD(con, forma.getNameFile(), path,
							forma.getNumVer());
					// TODO: CAMBIO PARA DOBLE VERSION
					HandlerStruct.saveDocView(con, forma);
				}
				// Se crean los Documentos Relacionados para la Nueva Versi�n
				HandlerStruct.getPreparedStatementDocumentLinks(
						forma.getIdDocument(), forma.getNumVer(),
						forma.getDocRelations(), con);

				if (!esDobleVersion) {
					ToolsHTML.deleteVersionCache(con, String.valueOf(forma
							.getNumVersion()));
				} else {
					// para llegar a este punto, la cache de la version primaria
					// fue respaldada
					// para garantizar el correcto funcionamiento de un posible
					// reverso
					// debemos dejar la cache como estaba
						log.info("Restaurando la cache de la version anterior ("
								+ originalNumVer
								+ "), previniendo posibles reversos");
						//ydavila 
					//	if (originalNumVer==numVer){
					//	if (!esDobleVersion) {
						Archivo.recoverDocumentCachedInDisk(con, originalNumVer);
					//	}
				//	}
				}
				
				con.commit();
				con.setAutoCommit(true);

				// Datos de la Versi�n
				String[] datosVersion = getFieldsToVersionMayor(con, new String[] {
						"MayorVer", "MinorVer" },
						Integer.parseInt(forma.getIdDocument()));

				updateHistoryDocs(con, Integer.parseInt(forma.getIdDocument()),
						0, 0, owner, timeCreate, docCheckIn,
						forma.getComments(), datosVersion);
				
				resp = true;
			} else {
				log.info("II MinorVer = " + forma.getMinorVer());
				
				// abrimos la conexion a la base de datos
				con = JDBCUtil.getConnection(Thread.currentThread().getStackTrace()[1].getMethodName());
				con.setAutoCommit(false);

				// Obtenemos la Versi�n Anterior del Documento
				InputStream vAnt = HandlerStruct.recuperarFileBD(con, Integer.parseInt(forma.getNumVersion()));
				
				InputStream vAntView = null;
				if(esDobleVersion) {
					vAntView = HandlerStruct.recuperarFileBDView(con, Integer.parseInt(forma.getNumVersion()));
				}

				long idBackUp = IDDBFactorySql.getNextIDLong(con, "BackUp");
				
				// Datos de la Versi�n
				String[] datosVersion = getFieldsToVersionMayor(con, new String[] {
						"MayorVer", "MinorVer" },
						Integer.parseInt(forma.getIdDocument()));

				Timestamp created = new Timestamp(ToolsHTML.sdfShow.parse(forma.getDateCreated()).getTime());

				backUpFile(con, idBackUp,
						Integer.parseInt(forma.getIdDocument()),
						forma.getMayorVer(), forma.getMinorVer(), vAnt, created, vAntView);

				setActiveCheckOut(forma.getIdCheckOut(), "1", con);

				Timestamp timeCreate = new Timestamp(new java.util.Date().getTime());
				updateHistoryDocs(con, Integer.parseInt(forma.getIdDocument()),
						0, 0, owner, timeCreate, docCheckIn,
						forma.getComments(), datosVersion);
				
				int numVer = Integer.parseInt(forma.getNumVersion());

				if (forma.isChangeMinor()) {
					if (ToolsHTML.isNumeric(forma.getMinorVer())) {
						int minor = Integer
								.parseInt(forma.getMinorVer().trim()) + 1;
						forma.setMinorVer(String.valueOf(minor));
					} else {
						if (!ToolsHTML.isEmptyOrNull(forma.getMinorVer())) {
							forma.setMinorVer(ToolsHTML.incVersion(forma
									.getMinorVer().trim()));
						} else {
							// forma.setMinorVer("");
							forma.setMinorVer("A");
						}
					}
				}

				int borrarVariable = 0;
				updateStatuVersionDoc(numVer, con, HandlerDocuments.docTrash,
						null, null, timeCreate, null, forma.getMinorVer(),
						true, Constants.notChange, forma.getComments());
				// TODO: CAMBIO PARA DOBLE VERSION
				forma.setPath(path);
				forma.setNumVer(numVer);
				// HandlerStruct.saveBDFromCheckOut(con, numVer, imagenFile,
				// forma);

				HandlerStruct.saveBDFromCheckOut(con, numVer,
						Integer.parseInt(forma.getIdCheckOut()), forma);
				
				upLastOperationDoc(Integer.parseInt(forma.getIdDocument()),
						lastCheckIn, con);

				if (!esDobleVersion) {
					ToolsHTML.deleteVersionCache(con, String.valueOf(forma
							.getNumVersion()));
				}

				// COLOCAMOS EL PDF CONVERTIDO DEL CHECKOUT SI EXISTE
				if(!esDobleVersion) {
					HandlerStruct.restorePdfFromCachedCheckOut(con, numVer,Integer.parseInt(forma.getIdCheckOut()));
				}

				con.commit();
				con.setAutoCommit(true);
				resp = true;
			}
		} catch (SQLException sqle) {
			log.error(sqle.getMessage());
			sqle.printStackTrace();
			applyRollback(con);
			resp = false;
		} catch (Exception ex) {
			log.error(ex.getMessage());
			ex.printStackTrace();
			applyRollback(con);
			resp = false;
		} finally {
			setFinally(con, st);
		}
		return resp;
	}

	public static synchronized boolean undoCheckOutDoc(String idDocument,
			String owner, String idCheckOut, String comments)
			throws ApplicationExceptionChecked {
		boolean resp = false;
		Connection con = null;
		PreparedStatement st = null;
		try {
			String lastOpe = checkDocument(String.valueOf(idDocument));
			log.debug("lastOpe = '" + lastOpe + "'");
			if (lastCheckOut.equalsIgnoreCase(lastOpe)
					|| lastBackCheckIn.equalsIgnoreCase(lastOpe)) {
				// Datos de la Versi�n
				int idDoc = Integer.parseInt(idDocument);
				String[] datosVersion = getFieldsToVersionMayor(new String[] {
						"MayorVer", "MinorVer" }, idDoc);
				con = JDBCUtil.getConnection(Thread.currentThread().getStackTrace()[1].getMethodName());
				con.setAutoCommit(false);
				Timestamp timeCreate = new Timestamp(
						new java.util.Date().getTime());
				setActiveCheckOut(idCheckOut, "1", con);
				// int idDoc = Integer.parseInt(idDocument);
				updateHistoryDocs(con, idDoc, 0, 0, owner, timeCreate,
						docBackCheckOut, comments, datosVersion);
				upLastOperationDoc(idDoc, lastBackCheckOut, con);
				con.commit();
				resp = true;
			} else {
				throw new ApplicationExceptionChecked("E0032");
			}
		} catch (ApplicationExceptionChecked ae) {
			log.error(ae.getMessage());
			applyRollback(con);
			throw ae;
		} catch (SQLException sqle) {
			log.error(sqle.getMessage());
			sqle.printStackTrace();
			applyRollback(con);
			resp = false;
		} catch (Exception ex) {
			log.error(ex.getMessage());
			ex.printStackTrace();
			applyRollback(con);
			resp = false;
		} finally {
			setFinally(con, st);
		}
		return resp;
	}

	public static String getMayorVersion(String idDoc) throws Exception {
		StringBuilder query = new StringBuilder(100);
		query.append("SELECT MAX(numVer) AS numVer FROM versiondoc WHERE numDoc = ");
		query.append(idDoc);
		Properties prop = JDBCUtil.doQueryOneRow(query.toString(),Thread.currentThread().getStackTrace()[1].getMethodName());
		if ("false".equalsIgnoreCase(prop.getProperty("isEmpty"))) {
			return prop.getProperty("numVer");
		}
		return "0";
	}

	public static Documento obtenerUltimoDocumentoCreado(String nameUser,
			boolean bloquear) throws Exception {
		// System.out.println("Obteniendo ultimo Documento de:" + nameUser);

		StringBuilder sql = new StringBuilder();

		sql.append("SELECT versiondoc.numVer, Documents.nameDocument, Documents.number, Documents.nameFile,  ");
		sql.append("Documents.contextType, Documents.numGen, versiondoc.createdBy ");
		sql.append("FROM versiondoc INNER JOIN ");
		sql.append("documents ON versiondoc.numDoc = Documents.numGen ");
		sql.append("WHERE (versiondoc.numVer = ");
		sql.append("(SELECT MAX(versiondoc_1.numVer) AS Expr1 ");
		sql.append("FROM versiondoc AS versiondoc_1 INNER JOIN ");
		sql.append("documents AS Documents_1 ON versiondoc_1.numDoc = Documents_1.numGen ");
		sql.append("WHERE (versiondoc_1.createdBy = ?) AND (versiondoc_1.newRegisterToEdit ='1' )))");

		Connection con = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;

		Documento doc = null;

		PreparedStatement pUpdate = null;

		try {
			// System.out.println("SQL:" + sql.toString());
			// //System.out.println("NombreUsuario:"+.getNombreUsuario());

			con = JDBCUtil.getConnection(Thread.currentThread().getStackTrace()[1].getMethodName());

			pstm = con.prepareStatement(JDBCUtil.replaceCastMysql(sql.toString()));
			pstm.setString(1, nameUser);

			rs = pstm.executeQuery();

			boolean crearDoc = rs.next();

			if (!crearDoc) {
				// trataremos de buscar el registro de otra manera
				pstm = con
						.prepareStatement(JDBCUtil.replaceCastMysql("SELECT ps.EditDocumentCheckOut FROM person ps WHERE ps.nameuser=?"));
				pstm.setString(1, nameUser);
				rs = pstm.executeQuery();

				if (rs.next()) {
					sql.setLength(0);
					sql.append("SELECT versiondoc.numVer, Documents.nameDocument, Documents.number, Documents.nameFile,  ");
					sql.append("Documents.contextType, Documents.numGen, versiondoc.createdBy ");
					sql.append("FROM versiondoc INNER JOIN ");
					sql.append("documents ON versiondoc.numDoc = Documents.numGen ");
					sql.append("WHERE (versiondoc.numVer = ");
					sql.append("(SELECT MAX(versiondoc_1.numVer) AS Expr1 ");
					sql.append("FROM versiondoc AS versiondoc_1 INNER JOIN ");
					sql.append("documents AS Documents_1 ON versiondoc_1.numDoc = Documents_1.numGen ");
					sql.append("WHERE (versiondoc.numVer = ?) AND (versiondoc_1.newRegisterToEdit ='1' )))");

					// System.out.println("SQL:" + sql.toString());
					// System.out.println("Numero de version:"+rs.getInt(1));

					pstm = con.prepareStatement(JDBCUtil.replaceCastMysql(sql.toString()));
					pstm.setInt(1, rs.getInt(1));
					rs = pstm.executeQuery();

					crearDoc = rs.next();
				}
			}

			if (crearDoc) {
				doc = new Documento();

				doc.setIdDocument(rs.getInt("numGen"));
				doc.setIdVersion(rs.getInt("numVer"));
				doc.setNombre(rs.getString("nameDocument"));
				doc.setNombreArchivo(rs.getString("nameFile"));
				doc.setMimeType(rs.getString("contextType"));
				doc.setNumero(rs.getString("number"));
			}

			if (doc != null && bloquear) {
				// Luego de que obtengo el documento, le quito el flag de
				// 'newRegisterToEdit'
				StringBuilder update = new StringBuilder();
				update.append("UPDATE versiondoc SET newRegisterToEdit='0' ");
				update.append("WHERE numVer=").append(doc.getIdVersion());

				pUpdate = con.prepareStatement(JDBCUtil.replaceCastMysql(update.toString()));

				pUpdate.executeUpdate();
			} else {
				throw new Exception(
						"El ultimo documento bloqueado para el usuario no ha sido encontrado");
			}
		} catch (SQLException e) {
			e.printStackTrace();
			throw new Exception(e);
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
				if (pstm != null) {
					pstm.close();
				}
				if (con != null) {
					con.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
				throw new Exception(e);
			}
		}

		return doc;
	}

	public static String checkIsUpd(String numVer) throws Exception {
		StringBuilder query = new StringBuilder(100);
		query.append("SELECT isUpdate FROM versiondoc WHERE numVer = ");
		query.append(numVer);
		Properties prop = JDBCUtil.doQueryOneRow(query.toString(),Thread.currentThread().getStackTrace()[1].getMethodName());
		if ("false".equalsIgnoreCase(prop.getProperty("isEmpty"))) {
			return prop.getProperty("isUpdate");
		}
		return "0";
	}

	public static synchronized void deleteVersionDoc(String numVer,
			Connection con) throws Exception {
		PreparedStatement st = null;
		StringBuilder delete = new StringBuilder(100);
		delete.append("DELETE FROM versiondoc WHERE numVer = ");
		// delete.append("UPDATE versiondoc set active='0' WHERE numVer = ");
		delete.append(numVer);
		st = con.prepareStatement(JDBCUtil.replaceCastMysql(delete.toString()));
		st.executeUpdate();
		st = null;
	}

	public static void deleteDocRelationsToVersion(String numVer, Connection con)
			throws SQLException {
		PreparedStatement st = null;
		String delete = "DELETE FROM documentslinks WHERE numVer = " + numVer
				+ " OR numVerLink = " + numVer;
		st = con.prepareStatement(JDBCUtil.replaceCastMysql(delete));
		st.executeUpdate();
	}

	public static synchronized boolean deleteVersionDoc(int idDoc,
			String numVer, String owner) throws Exception {
		Connection con = null;
		PreparedStatement st = null;
		StringBuilder delete = new StringBuilder(100);
		delete.append("DELETE FROM versiondoc WHERE numVer = ");
		// delete.append("UPDATE versiondoc set active='0' WHERE numVer = ");
		delete.append(numVer);
		try {
			// Datos de la Versi�n
			String[] datosVersion = getFieldsToVersionMayor(new String[] {
					"MayorVer", "MinorVer" }, idDoc);
			Timestamp time = new Timestamp(new java.util.Date().getTime());
			con = JDBCUtil.getConnection(Thread.currentThread().getStackTrace()[1].getMethodName());
			con.setAutoCommit(false);
			st = con.prepareStatement(JDBCUtil.replaceCastMysql(delete.toString()));
			st.executeUpdate();
			// Se elimina los Documentos Relacionados con la Versi�n
			deleteDocRelationsToVersion(numVer, con);
			upLastOperationDoc(idDoc, lastDeleteTrash, con);
			updateHistoryDocs(con, idDoc, 0, 0, owner, time, deleteTrash, null,
					datosVersion);
			con.commit();
			st = null;
			return true;
		} catch (Exception ex) {
			return false;
		} finally {
			setFinally(con, st);
		}
	}

	public static boolean undoCheckInDoc(String idDocument, String owner,
			String idCheckOut, String comments)
			throws ApplicationExceptionChecked {
		boolean resp = false;
		Connection con = null;
		PreparedStatement st = null;
		boolean commentsUpdate = false;
		Timestamp created = null;
		int version = -1;
		try {
			boolean esDobleVersion = false;

			String lastOpe = checkDocument(String.valueOf(idDocument));
			if (!lastCheckIn.equalsIgnoreCase(lastOpe)) {
				throw new ApplicationExceptionChecked("E0031");
			}
			String numVer = getMayorVersion(idDocument);
			log.info("[undoCheckInDoc] numVer = " + numVer);
			CheckOutDocForm dataVersion = new CheckOutDocForm();
			dataVersion.setNumVersion(numVer);
			loadVersion(dataVersion);

			esDobleVersion = ToolsHTML.extensionIsDobleVersion(ToolsHTML
					.getExtension(dataVersion.getNameFile()));

			String isUpd = checkIsUpd(numVer);
			String idBackUp = getMaxBackUpFile(idDocument);
			InputStream imagenFile = null;
			InputStream imagenFileII = null;
			InputStream imagenFileView = null;
			CheckOutDocForm forma = null;
			// Si la Versi�n del Documento no fu� una Actualizaci�n de la
			// Versi�n Anterior
			// o la misma se encuentra en Borrador, se procede a Eliminar la
			// Misma

			// PENDIENTE: El manejo de InputStream puede dar error, es posible
			// sustituirlo con select anidado
			//ydavila Ticket 001-00-003196 - Bot�n Deshacer Modificaci�n: indica el error: Ocurrio un error al procesar su solicitud
			if (idBackUp != "") {
				imagenFile = getFileBDBackUp(null, idBackUp);
				if (!"0".equalsIgnoreCase(idBackUp)	|| (dataVersion != null && docTrash.equalsIgnoreCase(dataVersion.getStatu()))) {
					// imagenFile = getFileBDBackUp(null,idBackUp);
					imagenFileII = getFileBDBackUp(null, idBackUp);
					imagenFileView = getFileBDBackUpView(null, idBackUp);
					forma = getDataBackUpFile(idBackUp);
				}
			}
			// Datos de la Versi�n
			String[] datosVersion = getFieldsToVersionMayor(new String[] {
					"MayorVer", "MinorVer" }, Integer.parseInt(idDocument));
			con = JDBCUtil.getConnection(Thread.currentThread().getStackTrace()[1].getMethodName());
			con.setAutoCommit(false);
			Timestamp timeCreate = new Timestamp(new java.util.Date().getTime());
			setActiveCheckOut(idCheckOut, "0", con);
			int idDoc = Integer.parseInt(idDocument);
			// Si la Versi�n del Documento no fu� una Actualizaci�n de la
			// Versi�n Anterior
			// o la misma se encuentra en Borrador, se procede a Eliminar la
			// Misma
			// Adicionalmente se debe dejar el Documento Bloqueado seg�n la
			// informaci�n
			// Manejada antes de hacer el Check IN (DocCheck OUT)
			if ("1".equalsIgnoreCase(isUpd)) {
				version = Integer.parseInt(numVer);
				HandlerStruct.copyVersionDoc(con, imagenFile, version);
				updateDocCheckOut(con, imagenFileII,Integer.parseInt(idCheckOut));
				//if(esDobleVersion && imagenFileView!=null) {
					//updateDocCheckOutView(con, imagenFileView,Integer.parseInt(idCheckOut));
				//}
				
				java.util.Date date = ToolsHTML.sdfShow.parse(forma
						.getDateCreated());
				created = new Timestamp(date.getTime());
				
				updateStatuVersionDoc(version, con, HandlerDocuments.docTrash,
						null, null, created, forma.getMayorVer(),
						forma.getMinorVer(), true, Constants.notChange, null);
				commentsUpdate = true;
				
				// eliminamos el archivo de cache de la version de este
				// documento, sera generado en su proxima visualizacion
				if (esDobleVersion) {
					if(imagenFileView!=null) {
						//Archivo.recoverDocumentCachedInDisk(version);
						HandlerStruct.copyVersionDocView(con, imagenFileView, version);
					}
				} else {
					Archivo.deleteDocumentCachedInDisk(version);
				}
			} else {
				if (idBackUp != "") {
					if ("0".equalsIgnoreCase(isUpd)
							|| (dataVersion != null && docTrash
									.equalsIgnoreCase(dataVersion.getStatu()))) {
						log.info("Eliminando Version " + numVer + "?");
						deleteVersionDoc(numVer, con);
						updateDocCheckOut(con, imagenFile,
								Integer.parseInt(idCheckOut));
					} else {
						version = Integer.parseInt(numVer);
						HandlerStruct.copyVersionDoc(con, imagenFile, version);
						updateDocCheckOut(con, imagenFileII,
								Integer.parseInt(idCheckOut));
						java.util.Date date = ToolsHTML.sdfShow.parse(forma
								.getDateCreated());
						created = new Timestamp(date.getTime());
						updateStatuVersionDoc(version, con,
								HandlerDocuments.docTrash, null, null, created,
								forma.getMayorVer(), forma.getMinorVer(), true,
								Constants.notChange, null);
					}
				}
			}
			updateHistoryDocs(con, idDoc, 0, 0, owner, timeCreate,
					docBackCheckIn, comments, datosVersion);
			upLastOperationDoc(idDoc, lastBackCheckIn, con);
			con.commit();
			
			if(commentsUpdate) {
				String beforeComments = getBeforeLocksCommentsFromDocuments(idDocument, forma.getMayorVer(), forma.getMinorVer());
				updateStatuVersionDoc(version, con, HandlerDocuments.docTrash,
						null, null, created, forma.getMayorVer(),
						forma.getMinorVer(), true, Constants.notChange, beforeComments);
			}
			
			resp = true;
		} catch (ApplicationExceptionChecked ae) {
			log.error(ae.getMessage());
			applyRollback(con);
			throw ae;
		} catch (SQLException sqle) {
			log.error(sqle.getMessage());
			sqle.printStackTrace();
			applyRollback(con);
			resp = false;
		} catch (Exception ex) {
			log.error(ex.getMessage());
			ex.printStackTrace();
			applyRollback(con);
			resp = false;
		} finally {
			setFinally(con, st);
		}
		return resp;
	}

	/**
	 *
	 */
	public static Collection searchDocumentsII(Connection con, String keys, byte criterio,
			String name, String number, String type, String owner,
			String publicDoc, String normISO, String prefix,
			String TypesStatus, String cargo, String orderBy, String idNode,
			Hashtable struct, HttpServletRequest request, BaseDocumentForm doc,
			String lote, 
			String comment) {    //ydavila Ticket 001-00-003265 Se agrega b�squeda de comentarios
		StringBuilder sql = new StringBuilder(2048);
		ArrayList result = new ArrayList();
		Hashtable routs = new Hashtable();
		Hashtable prefijos = new Hashtable();
		String routDoc = "";
		boolean swcancelado = false;
		String dateSystem = ToolsHTML.sdfShowConvert1.format(new Date());
		
		if (HandlerWorkFlows.wfuCanceled2.equalsIgnoreCase(TypesStatus)) {
			TypesStatus = HandlerWorkFlows.wfuCanceled;
			swcancelado = true;
		}

		// Si el tipo de documento es 9 se refiere a documentos que estan en
		// FTP, pero su tipo es 15
		if (TypesStatus.equalsIgnoreCase("9")) {
			TypesStatus = HandlerDocuments.inFlewFlow;
		}

		if ((!HandlerWorkFlows.ownercancelcloseflow
				.equalsIgnoreCase(TypesStatus))
				&& (!HandlerWorkFlows.wfuCanceled.equalsIgnoreCase(TypesStatus))
				&& (!HandlerDocuments.docInReview.equalsIgnoreCase(TypesStatus))
				&& ((!HandlerDocuments.docInApproved
						.equalsIgnoreCase(TypesStatus)))
				&& (!HandlerDocuments.docReview.equalsIgnoreCase(TypesStatus))
				&& (!HandlerDocuments.docRejected.equalsIgnoreCase(TypesStatus))) {
			//ydavila Ticket 001-00-003265
			sql.append("SELECT DISTINCT(d.numGen),d.number,d.nameDocument,d.IdNode,d.normISO,d.type,d.owner,d.dateCreation,d.prefix,d.statu,td.typeDoc, vd.statu as statuVer, vd.dateExpiresDrafts, vd.dateExpires, ");
			//sql.append("SELECT DISTINCT(d.numGen),d.number,d.nameDocument,d.IdNode,d.normISO,d.type,d.owner,d.dateCreation,d.prefix,d.statu,d.comments,td.typeDoc, vd.statu as statuVer, vd.dateExpiresDrafts, vd.dateExpires, ");
			switch (Constants.MANEJADOR_ACTUAL) {
			case Constants.MANEJADOR_MSSQL:
				sql.append(" (p.apellidos + ' ' + p.nombres) AS Nombre, ");
				break;
			case Constants.MANEJADOR_POSTGRES:
				sql.append(" (p.apellidos || ' ' || p.nombres) AS Nombre, ");
				break;
			case Constants.MANEJADOR_MYSQL:
				sql.append(" CONCAT(p.apellidos , ' ' , p.nombres) AS Nombre, ");
				break;
			}

			sql.append("vd.numVer, vd.MayorVer, vd.MinorVer, p.idPerson FROM documents d,person p,typedocuments td,versiondoc vd");
			sql.append(" WHERE p.nameUser = d.owner AND td.idTypeDoc = cast(d.type as int) AND vd.numDoc = d.numGen AND d.active = '1' ");
			sql.append(" AND cast(d.type as int)<>").append(
					HandlerDocuments.TypeDocumentsImpresion);
			sql.append(" AND p.accountActive='1' ");

			// Tambien los resultados de obsoletos no son correctos, esta
			// buscando documentos que
			// tengan versiones obsoletas
			// no los documentos que estan obsoletos.
			if (TypesStatus.equals(HandlerDocuments.docObs)) {
				sql.append(" AND d.statu='").append(HandlerDocuments.docObs)
						.append("'");
			}

			sql.append(" AND vd.numVer = (SELECT MAX(numVer) FROM versiondoc vdi WHERE vdi.numDoc = d.numGen ");

			// Luis Cisneros
			// 18/04/07
			// SI es publicado ( TypesStatus == 5 ) es necesario colocar este
			// otro par�mero para que
			// el listado traiga los mismo resultados de la p�gina de publicados
			// sql.append(" AND vd.numVer = (SELECT MAX(numVer) FROM versiondoc
			// vdi WHERE vdi.numDoc
			// = d.numGen AND vdi.statu=").append(HandlerDocuments.docApproved);
			if (TypesStatus.equals(HandlerDocuments.docApproved)) {
				sql.append(" AND vdi.statu='")
						.append(HandlerDocuments.docApproved).append("'");
			}
			// Fin 18/04/07

		} else {
			//ydavila Ticket 001-00-003265
		    sql.append("SELECT DISTINCT(d.numGen),d.number,d.nameDocument,d.IdNode,d.normISO,d.type,d.owner,d.dateCreation,d.prefix,d.statu,td.typeDoc, vd.statu as statuVer, vd.dateExpiresDrafts, vd.dateExpires, ");
			//sql.append("SELECT DISTINCT(d.numGen),d.number,d.nameDocument,d.IdNode,d.normISO,d.type,d.owner,d.dateCreation,d.prefix,d.statu,d.comments,td.typeDoc, vd.statu as statuVer, vd.dateExpiresDrafts, vd.dateExpires, ");
			switch (Constants.MANEJADOR_ACTUAL) {
			case Constants.MANEJADOR_MSSQL:
				sql.append(" (p.apellidos + ' ' + p.nombres) AS Nombre, ");
				break;
			case Constants.MANEJADOR_POSTGRES:
				sql.append(" (p.apellidos || ' ' || p.nombres) AS Nombre, ");
				break;
			case Constants.MANEJADOR_MYSQL:
				sql.append(" CONCAT(p.apellidos , ' ' , p.nombres) AS Nombre, ");
				break;
			}
			sql.append(" vd.numVer FROM documents d,person p,typedocuments td,versiondoc vd");
			sql.append(" ,workflows wf,user_workflows uw");
			sql.append(" WHERE p.nameUser = d.owner AND td.idTypeDoc = cast(d.type as int) AND vd.numDoc = d.numGen AND d.active = '1' ");
			sql.append(" AND cast(d.type as int) <> ").append(
					HandlerDocuments.TypeDocumentsImpresion);
			sql.append(" AND p.accountActive='1' ");

			// Tambien los resultados de obsoletos no son correctos, esta
			// buscando documentos que
			// tengan versiones obsoletas
			// no los documentos que estan obsoletos.
			if (TypesStatus.equals(HandlerDocuments.docObs)) {
				sql.append(" AND d.statu='").append(HandlerDocuments.docObs)
						.append("'");
			}

			sql.append(" AND vd.numVer = (SELECT MAX(numVer) FROM versiondoc vdi WHERE vdi.numDoc = d.numGen ");
			// sql.append(" AND vd.numVer = (SELECT MAX(numVer) FROM versiondoc
			// vdi WHERE vdi.numDoc
			// = d.numGen AND vdi.statu=").append(HandlerDocuments.docApproved);
			// Luis Cisneros
			// 18/04/07
			// SI es publicado ( TypesStatus == 5 ) es necesario colocar este
			// otro par�mero para que
			// el listado traiga los mismo resultados de la p�gina de publicados
			// sql.append(" AND vd.numVer = (SELECT MAX(numVer) FROM versiondoc
			// vdi WHERE vdi.numDoc
			// = d.numGen AND vdi.statu=").append(HandlerDocuments.docApproved);

			if (TypesStatus.equals(HandlerDocuments.docApproved)) {
				sql.append(" AND vdi.statu='")
						.append(HandlerDocuments.docApproved).append("'");
			}
			// Fin 18/04/07

			sql.append(") AND wf.idWorkFlow = (SELECT MAX(idWorkFlow) FROM workflows wf1 WHERE wf1.idDocument = d.numGen )");
			sql.append(" AND wf.iddocument = d.numgen ");
			sql.append(" AND wf.idWorkFlow = uw.idWorkFlow AND wf.idVersion = vd.numVer ");
			// si el status es cancelado no hay que hcer el filtro de abajo ...
			// if (!swcancelado){
			// // sql.append(" AND uw.idUser = d.owner");
			// }
		}
		if (!ToolsHTML.isEmptyOrNull(TypesStatus)
				&& (!"0".equalsIgnoreCase(TypesStatus))) {
			if ((HandlerWorkFlows.ownercancelcloseflow
					.equalsIgnoreCase(TypesStatus))
					|| (HandlerWorkFlows.wfuCanceled
							.equalsIgnoreCase(TypesStatus))
					|| (HandlerDocuments.docInReview
							.equalsIgnoreCase(TypesStatus))
					|| ((HandlerDocuments.docInApproved
							.equalsIgnoreCase(TypesStatus)))
					|| ((HandlerDocuments.docReview
							.equalsIgnoreCase(TypesStatus)))
					|| ((HandlerDocuments.docRejected
							.equalsIgnoreCase(TypesStatus)))) {

				if (!swcancelado) {
					sql.append(" AND ( ");
					if ((HandlerDocuments.docReview
							.equalsIgnoreCase(TypesStatus))
							|| (HandlerDocuments.docApproved
									.equalsIgnoreCase(TypesStatus))) {
						sql.append(" d.statu = '").append(TypesStatus)
								.append("'");
						// El Documento se encuentre Revisado (�ltima Versi�n)
						if (HandlerDocuments.docReview
								.equalsIgnoreCase(TypesStatus)) {
							sql.append(" AND vd.statuhist = '")
									.append(TypesStatus).append("'");
						}
					} else {
						if (HandlerWorkFlows.ownercancelcloseflow
								.equalsIgnoreCase(TypesStatus)) {
							// Si se buscan los Flujos Cancelados
							// se Busca Seg�n el statu del Mismo
							sql.append(" wf.statu = '").append(TypesStatus)
									.append("'");
						} else {
							// if
							// (HandlerWorkFlows.canceled.equalsIgnoreCase(TypesStatus))
							// {
							// sql.append(" wf.statu =
							// ").append(HandlerWorkFlows.canceled);
							// } else {
							sql.append(" wf.statu = '1'"); // El FLUJO ESTE
															// ACTIVO
							sql.append(" AND d.statu = '").append(TypesStatus)
									.append("'");
							// }
						}
						// sql.append()
						// sql.append(" wf.statu =
						// ").append(TypesStatus).append(" ");
						// sql.append(" OR ");
						// sql.append(" d.statu = ").append(TypesStatus);
					}
					sql.append(" ) ");
				} else {
					if (HandlerWorkFlows.canceled.equalsIgnoreCase(TypesStatus)) {
						sql.append(" AND wf.statu = '")
								.append(HandlerWorkFlows.canceled).append("'");
					}
				}
			} else {
				// Documentos Aprobados, �ltima Versi�n
				if (HandlerDocuments.docApproved.equalsIgnoreCase(TypesStatus)) {
					sql.append(") AND vd.statu = '").append(TypesStatus)
							.append("'");
				} else {

					if (HandlerWorkFlows.canceled.equalsIgnoreCase(TypesStatus)) {
						sql.append(" wf.statu = '")
								.append(HandlerWorkFlows.canceled).append("'");
					} else if ("10".equalsIgnoreCase(TypesStatus)) {
						// Borradores expirados
						sql.append(") AND vd.statu = '")
								.append(HandlerDocuments.docTrash).append("' ");
						switch (Constants.MANEJADOR_ACTUAL) {
						case Constants.MANEJADOR_MSSQL:
							sql.append(
									" AND vd.dateExpiresDrafts <= Convert(datetime,'")
									.append(dateSystem).append("',120) ");
							break;
						case Constants.MANEJADOR_POSTGRES:
							sql.append(" AND vd.dateExpiresDrafts <= CAST('")
									.append(dateSystem)
									.append("' as timestamp) ");
							break;
						case Constants.MANEJADOR_MYSQL:
							sql.append(" AND vd.dateExpiresDrafts <= TIMESTAMP('")
									.append(dateSystem)
									.append("') ");
							break;
						}
					} else if ("11".equalsIgnoreCase(TypesStatus)) {
						// Aprobados expirados
						sql.append(" AND vdi.statu = '")
								.append(HandlerDocuments.docApproved)
								.append("') ");
						switch (Constants.MANEJADOR_ACTUAL) {
						case Constants.MANEJADOR_MSSQL:
							sql.append(
									" AND vd.dateExpires <= Convert(datetime,'")
									.append(dateSystem).append("',120) ");
							break;
						case Constants.MANEJADOR_POSTGRES:
							sql.append(" AND vd.dateExpires <= CAST('")
									.append(dateSystem)
									.append("' as timestamp) ");
							break;
						case Constants.MANEJADOR_MYSQL:
							sql.append(" AND vd.dateExpires <= TIMESTAMP('")
									.append(dateSystem)
									.append("') ");
							break;
						}

					} else if ("15".equalsIgnoreCase(TypesStatus)) {
						// Documentos en FTP
						sql.append(") AND d.statu = '").append(TypesStatus)
								.append("'");
					} else {
						sql.append(" AND vdi.statu = '").append(TypesStatus)
								.append("')");
					}
				}
			}
		} else {
			if (ToolsHTML.isEmptyOrNull(keys)) {
				sql.append(")");
				// sql.append(" AND vdi.statu =
				// ").append(docApproved).append(")");
			} else {
				sql.append(" ) ");
			}
		}
		String field = null;
		boolean valida = false;
		switch (criterio) {
		case 1:
			field = " (nameDocument ";
			valida = true;
			break;
		case 2:
			field = " (number ";
			valida = true;
			break;
		case 3:
			field = " (lower(keysDoc) ";
			valida = true;
			break;
		}
		if (!ToolsHTML.isEmptyOrNull(keys) && valida) {
			StringBuilder searchTo = new StringBuilder(100);
			StringTokenizer st = new StringTokenizer(keys, ",");
			boolean first = true;
			while (st.hasMoreTokens()) {
				if (first) {
					searchTo.append(" AND ( ");
					first = false;
				} else {
					searchTo.append(" OR ");
				}
				searchTo.append(field).append(" ").append("LIKE '%")
						.append(st.nextToken().trim().toLowerCase())
						.append("%') ");
			}
			if (valida) {
				if (!ToolsHTML.isEmptyOrNull(keys) && valida) {
					sql.append(searchTo.toString()).append(" )");
				} else {
					sql.append(searchTo.toString());
				}
			}
		}
		if (!ToolsHTML.isEmptyOrNull(name)) {
			sql.append(" AND (lower(d.nameDocument) LIKE '%")
					.append(name.toLowerCase()).append("%') ");
		}
		if (!ToolsHTML.isEmptyOrNull(lote)) {
			sql.append(" AND (LOWER(d.loteDoc) LIKE '%")
					.append(lote.toLowerCase()).append("%') ");
		}
		if (!ToolsHTML.isEmptyOrNull(number)) {
			sql.append(" AND d.number = '").append(number).append("' ");
		}

		if (doc != null && doc.getListaCampos() != null) {
			DocumentForm obj = null;
			for (int i = 0; i < doc.getListaCampos().size(); i++) {
				obj = (DocumentForm) doc.getListaCampos().get(i);
				try {
					String valor = (String) doc.get(obj.getId().toUpperCase());
					if (!ToolsHTML.isEmptyOrNull(valor)) {
						sql.append(" AND d.").append(obj.getId())
								.append(" like '").append(valor).append("' ");
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}

		if (!ToolsHTML.isEmptyOrNull(type) && !"0".equalsIgnoreCase(type)) {
			// sql.append(" AND (d.type = ").append(type).append(") ");
			// Si es un Documento de Libre Impresi�n se procede a hacer la
			// B�squeda as�...
			// if
			// (DesigeConf.getProperty("typeDocs.docPrintFreeID").compareTo(type.trim())==0)
			// {
			// sql.append(" AND d.keys LIKE
			// '%").append(DesigeConf.getProperty("typeDocs.docPrintFree")).append("%'");
			// } else {
			sql.append(" AND d.type = '").append(type).append("'");
			// }
		}

		if (!ToolsHTML.isEmptyOrNull(owner) && !"0".equalsIgnoreCase(owner)) {
			sql.append(" AND (d.owner = '").append(owner).append("') ");
		}

		if (!ToolsHTML.isEmptyOrNull(prefix)) {
			sql.append(" AND (d.prefix LIKE '%").append(prefix).append("%') ");
		}

		List<String> normasList = new ArrayList<String>();
		if (!ToolsHTML.isEmptyOrNull(normISO) && !"0".equalsIgnoreCase(normISO)) {
			// Chequea si la norma selecciona es cabecera (Indice = 0)
			normasList = HandlerNorms.getNormsBySistemaGestion(normISO);
			if (normasList.size() == 0) {
				sql.append(" AND (normISO LIKE '%").append(normISO).append("%') ");
			} else {
				int prefijoIso = Integer.parseInt(normISO.substring(0,normISO.length()-2));
					
				sql.append(" AND (d.normiso LIKE '").append(prefijoIso+0).append("%' or d.normiso LIKE '%,").append(prefijoIso+0).append("%'"); 
	            sql.append("  OR  d.normiso LIKE '").append(prefijoIso+1).append("%' or d.normiso LIKE '%,").append(prefijoIso+1).append("%'");
	            sql.append("  OR  d.normiso LIKE '").append(prefijoIso+2).append("%' or d.normiso LIKE '%,").append(prefijoIso+2).append("%'");
	            sql.append("  OR  d.normiso LIKE '").append(prefijoIso+3).append("%' or d.normiso LIKE '%,").append(prefijoIso+3).append("%'");
	            sql.append("  OR  d.normiso LIKE '").append(prefijoIso+4).append("%' or d.normiso LIKE '%,").append(prefijoIso+4).append("%')"); 
				
			}
			//	ydavila	Se coloca en comentario para que muestra tambi�n registros de tabla norms con indice='0'
			//List<String> indice;
			//indice = HandlerNorms.getIndiceBySistemaGestion(normISO);
			//sql.append(" AND (indice <> '0' )");
		}

		if (!ToolsHTML.isEmptyOrNull(publicDoc)
				&& !"2".equalsIgnoreCase(publicDoc)) {
			sql.append(" AND (d.docPublic = ").append(publicDoc).append(")");
		}
		// SIMON 1 DE JUNIO 2005 INICIO
		if (!ToolsHTML.isEmptyOrNull(cargo) && (!"0".equalsIgnoreCase(cargo))) {
			sql.append(" AND (p.cargo LIKE '%").append(cargo).append("%') ");
		}
		// Buscando Documentos por Carpetas
		if (!ToolsHTML.isEmptyOrNull(idNode) && (!"0".equalsIgnoreCase(idNode))) {
			String hijos = HandlerStruct.getAllNodesChilds(idNode);
			log.debug("Hijos: " + hijos);
			sql.append(" AND d.idNode IN (").append(idNode);
			if (!ToolsHTML.isEmptyOrNull(hijos)) {
				sql.append(",").append(hijos);
			}
			sql.append(")");
			// sql.append(" AND (d.idNode=").append(idNode).append(") ");
		}
		// SIMON 1 DE JUNIO 2005 FIN
		if (!ToolsHTML.isEmptyOrNull(orderBy)) {
			sql.append(" ORDER BY ").append(orderBy);
			// sql.append(" asc");
		}

		log.debug("[searchDocuments] = " + sql.toString());
		System.out.println("[searchDocuments] = " + sql.toString());

		Locale defaultLocale = new Locale(
				DesigeConf.getProperty("language.Default"),
				DesigeConf.getProperty("country.Default"));
		ResourceBundle rb = ResourceBundle.getBundle("LoginBundle",
				defaultLocale);

		//Connection conn1 = null;

		try {
			
		
			// Hashtable norms = HandlerNorms.getDescriptNorms();
			// SIMON 01 DE JULIO 2005 INICIO
			Hashtable norms1 = HandlerNorms.getAllNormas(con);
			// SIMON 01 DE JULIO 2005 FIN
			if (request != null) {
				request.getSession().setAttribute("querySearchReport",
						sql.toString());
				// si hay campos de ubicacion almacenamos el query de busqueda
				ConfDocumentoDAO conf = new ConfDocumentoDAO();

				// verificamos si se editaran todos los campos adicionales
				if ("1".equals(HandlerParameters.PARAMETROS.getChangeAditionalField())) {
					if (conf.findFieldAditional()) { // bucamos si hay campos
														// adicionales visibles
						request.getSession().setAttribute(
								"querySearchLocation", sql.toString());
					}
				} else {
					if (conf.findFieldLocation(con)) { // bucamos si hay campos
													// adicionales de ubicacion
													// visibles
						request.getSession().setAttribute(
								"querySearchLocation", sql.toString());
					}
				}
			}

			Vector datos = JDBCUtil.doQueryVector(con, sql.toString(),Thread.currentThread().getStackTrace()[1].getMethodName());
			String statuDoc;
			int listLen = normasList.size();
			for (int row = 0; row < datos.size(); row++) {
				statuDoc = null;
				Properties properties = (Properties) datos.elementAt(row);

				// Si se filtra por Normas
				if (!ToolsHTML.isEmptyOrNull(normISO)
						&& !"0".equalsIgnoreCase(normISO)) {

					String normaIso = properties.getProperty("normISO");
					if (normaIso.length() == 0) {
						continue;
					}

					if (listLen > 0) {
						String[] normasDocumento = normaIso.split(",");
						boolean okFlag = false;
						for (String norma : normasDocumento) {
							//ydavila Ticket 001-00-003260
							String strnorm = norma.substring(0,2);
							if(norma.indexOf(strnorm)!=-1) {
							//if (normasList.contains(norma)) {
								okFlag = true;
								break;
							//}
							}
						}
						if (!okFlag) {
							continue;
						}
					}
				}

				BaseDocumentForm forma = new BaseDocumentForm();
				forma.setIdDocument(properties.getProperty("numGen"));
				// comprobamos la seguridad del documento
				statuDoc = properties.getProperty("statu").trim();
				forma.setIsflowusuario("-1");
				// Se cargan los Usuarios Pendientes en el Flujo si el Documento
				// se encuentra
				// Sometido a un Flujo de Revisi�n o Aprobaci�n
				if ((docInReview.compareTo(statuDoc) == 0)
						|| (docInApproved.compareTo(statuDoc) == 0)
						|| (inFlewFlow.compareTo(statuDoc) == 0)
						|| (docObs.compareTo(statuDoc) == 0)) {
					// 25/04/07 Luis Cisneros
					// No se estaban tomando en cuenta los documentos obsoletos
					// en flujo
					// Colocar Opci�n para Flujos Param�tricos
					// Collection userPending =
					// HandlerWorkFlows.getUserInWorkFlowPending(forma.getIdDocument(),HandlerWorkFlows.wfuPending,false);
					Collection userPending = null;
					// Colocar Opci�n para Flujos Param�trico :D
					if (inFlewFlow.compareTo(statuDoc) != 0) {
						userPending = HandlerWorkFlows
								.getUserInWorkFlowPending(
										forma.getIdDocument(),
										HandlerWorkFlows.wfuPending, false);
					} else {
						// Es un Flujo Param�trico se procede a Cargar los
						// Usuarios del Mismo
						userPending = HandlerWorkFlows
								.getUserInWorkFlowPending(con,
										forma.getIdDocument(),
										HandlerWorkFlows.wfuPending, true);
					}
					if (userPending != null) {
						Iterator it = userPending.iterator();
						int i = 0;
						String nameUsuario = "-1";
						while ((it.hasNext())) {
							ParticipationForm g = (ParticipationForm) it.next();
							++i;
							// si hay mas de uno pendiente, es no secuencial el
							// flujo
							if (i == 1) {
								nameUsuario = g.getNameUser() + " ("
										+ g.getCharge() + ")";
								forma.setIsNotflowsecuencial("0");
								forma.setIsflowcargo(g.getCharge());
							} else {
								nameUsuario += g.getNameUser() + "("
										+ g.getCharge() + ")";
								forma.setIsNotflowsecuencial("1");
							}
							forma.setIsflowusuario(nameUsuario);
						}
					}
				}
				// cargamos toda la seguridad del documento
				// forma.setToViewDocs(securityDocsfrm.getToViewDocs());
				// forma.setIsflowusuario("-1");
				// Collection userPending =
				// HandlerWorkFlows.getUserInFlowPending(forma.getIdDocument(),HandlerWorkFlows.wfuPending,"lista
				// los usarios con flujos pendientes");
				// if (userPending!=null){
				// Iterator it = userPending.iterator();
				// int i=0;
				// String nameUsuario="-1";
				// while((it.hasNext())){
				// ParticipationForm g =(ParticipationForm) it.next();
				// ++i;
				// if (i==1){
				// nameUsuario = g.getNameUser()+"("+g.getCharge()+")";
				// forma.setIsNotflowsecuencial("0");
				// forma.setIsflowcargo(g.getCharge());
				// }else{
				// nameUsuario+=g.getNameUser()+"("+g.getCharge()+")";
				// forma.setIsNotflowsecuencial("1");
				// }
				// forma.setIsflowusuario(nameUsuario);
				// }
				// }
				//
				forma.setNumber(properties.getProperty("number"));
				forma.setNameDocument(properties.getProperty("nameDocument"));
				forma.setIdNode(properties.getProperty("IdNode"));
				String idNorm = properties.getProperty("normISO");
				if (ToolsHTML.isEmptyOrNull(idNorm)) {
					forma.setNormISO("");
				} else {
					forma.setNormISO(idNorm);
				}

				Search titleNorm = null;
				String tempString = forma.getNormISO();
				int pos = tempString.indexOf(',');
				if (pos != -1) {
					titleNorm = (Search) norms1.get(normISO);
				} else {
					titleNorm = (Search) norms1.get(tempString);
				}

				// log.debug("forma.getNormISO() = " + forma.getNormISO());
				// log.debug("titleNorm = " + titleNorm);
				if (titleNorm != null) {
					forma.setNormISODescript(titleNorm.getDescript());
				} else {
					titleNorm = (Search) norms1.get(normISO);
					if (titleNorm != null) {
						forma.setNormISODescript(titleNorm.getDescript());
					} else {
						if (pos == -1) {
							forma.setNormISODescript("");
						} else {
							forma.setNormISODescript("<b>Documento con multiples requisitos</b>");

						}
					}
				}
				String prefixDoc = null;// properties.getProperty("prefix");
				// byte heredarPrefijos = 0;
				// //Se Busca la Localidad a la Cual pertenece el Documento
				// if (!localidades.containsKey(forma.getIdNode())) {
				// String idLocation =
				// HandlerStruct.getIdLocationToNode(struct,forma.getIdNode());
				// if (idLocation!=null) {
				// BaseStructForm localidad =
				// (BaseStructForm)struct.get(idLocation);
				// if (localidad!=null) {
				// heredarPrefijos = localidad.getHeredarPrefijo();
				// }
				// }
				// }
				// //Si est� activado el Heredar Prefijo se reconstruye el Mismo
				// if (Constants.permission == heredarPrefijos) {
				if (struct != null) {
					if (!prefijos.containsKey(forma.getIdNode())) {
						prefixDoc = ToolsHTML.getPrefixToDoc(struct,
								forma.getIdNode());
						prefijos.put(forma.getIdNode(), prefixDoc);
					} else {
						prefixDoc = (String) prefijos.get(forma.getIdNode());
					}
				} else {
					prefixDoc = properties.getProperty("prefix");
				}
				// } else {
				// //En Caso Contrario se coloca como Prefijo s�lo el prefijo de
				// la Carpeta
				// BaseStructForm folder =
				// (BaseStructForm)struct.get(forma.getIdNode());
				// if (folder!=null) {
				// prefixDoc = folder.getPrefix();
				// }
				// }

				if (!ToolsHTML.isEmptyOrNull(prefixDoc)) {
					forma.setPrefix(prefixDoc.trim());
				} else {
					forma.setPrefix("");
				}
				forma.setTypeDocument(properties.getProperty("type"));
				forma.setDescriptTypeDoc(properties.getProperty("typeDoc"));
				forma.setOwner(properties.getProperty("Nombre"));
				String date = properties.getProperty("dateCreation");
				if (!ToolsHTML.isEmptyOrNull(date)) {
					forma.setDateCreation(ToolsHTML.formatDateShow(date, true));
				} else {
					forma.setDateCreation("");
				}
				forma.setNumVer(Integer.parseInt(properties
						.getProperty("numVer")));
				if (!routs.containsKey(forma.getIdNode().trim())) {
					routDoc = HandlerStruct.getRout(con, "", forma.getIdNode(),
							forma.getIdNode(), null, null, false);
					// routDoc = routDoc + forma.getNameDocument();
					routs.put(forma.getIdNode(), routDoc);
					forma.setRout(routDoc + forma.getNameDocument());
				} else {
					routDoc = (String) routs.get(forma.getIdNode());
					forma.setRout(routDoc + forma.getNameDocument());
				}
				forma.setStatu(Integer.parseInt(properties.getProperty(
						"statuVer").trim()));
				String statusDocumento = "";
				int statuDocuments = 0;
				String sStatu = properties.getProperty("statu");
				if (!ToolsHTML.isEmptyOrNull(sStatu)
						&& ToolsHTML.isNumeric(sStatu))
					statuDocuments = Integer.parseInt(sStatu.trim());
				switch (forma.getStatu()) {
				case 1: // Borrador 1
					statusDocumento = rb.getString("wf.statuDoc1");
					break;
				case 3: // Revisado 3
					// statusDocumento = rb.getString("wf.statuDoc5");
					statusDocumento = rb.getString("wf.statuDoc3");
					break;
				case 5: // Aprobado 5
					statusDocumento = rb.getString("wf.statuDoc5");
					break;
				case 6: // Obsoleto 6
					statusDocumento = rb.getString("wf.statuDoc6");
					break;
				default:
					break;
				}

				if (!ToolsHTML.isEmptyOrNull(dateSystem)
						&& dateSystem.length() > 9) {
					if (forma.getStatu() == 1
							&& !ToolsHTML.isEmptyOrNull(properties
									.getProperty("dateExpiresDrafts"))
							&& properties.getProperty("dateExpiresDrafts")
									.length() > 9
							&& dateSystem.substring(0, 10).compareTo(
									properties.getProperty("dateExpiresDrafts")
											.substring(0, 10)) >= 0) {
						// Borradores expirados
						statusDocumento += " Expirado";

					} else if (forma.getStatu() == 5
							&& !ToolsHTML.isEmptyOrNull(properties
									.getProperty("dateExpires"))
							&& properties.getProperty("dateExpires").length() > 9
							&& dateSystem.substring(0, 10).compareTo(
									properties.getProperty("dateExpires")
											.substring(0, 10)) >= 0) {
						// Aprobados expirados
						statusDocumento += " Expirado";
					}
				}

				switch (statuDocuments) {
				case 2: // En Revision
					statusDocumento += " / " + rb.getString("wf.statuDoc2");
					break;
				case 4: // En Aprobacion
					statusDocumento += " / " + rb.getString("wf.statuDoc4");
					break;
				case 15: // En FTP
					statusDocumento += " / " + rb.getString("wf.statuDoc9");
					break;
				default:
					break;
				}
				forma.setStatuDoc(statusDocumento);

				result.add(forma);
				
				
				comment="";
				forma.setcomment(comment);

				//ydavila Ticket 001-00-003265
				PreparedStatement pst1 = null;
				ResultSet rs1 = null;
				StringBuffer sb = new StringBuffer();
				//System.out.println("documento = " + forma.idDocument);
								switch (Constants.MANEJADOR_ACTUAL) {
								case Constants.MANEJADOR_MSSQL:
									sb.append("SELECT TOP 1 comments FROM historydocs WHERE idnode = ")
							        .append(properties.getProperty("numGen"))
									.append(" and type = '5'")
									.append(" order by idhistory desc ");			 
									break;
								case Constants.MANEJADOR_POSTGRES:
									sb.append("SELECT comments FROM historydocs WHERE idnode = ")
							        .append(properties.getProperty("numGen"))
									.append(" and type = '5'")
									.append(" order by idhistory desc LIMIT 1")
									;				 
									break;
								case Constants.MANEJADOR_MYSQL:
									sb.append("SELECT comments FROM historydocs WHERE idnode = ")
							        .append(properties.getProperty("numGen"))
									.append(" and type = '5'")
									.append(" order by idhistory desc LIMIT 1")
									;				 
									break;
								}
				
				pst1 = con.prepareStatement(JDBCUtil.replaceCastMysql(sb.toString()));
				rs1 = pst1.executeQuery();

				while (rs1.next()) {
					comment = rs1.getString("comments");
				 	//System.out.println("comentario = " +  comment);
					Collection history = HandlerStruct.getHistoryDoc(con, idNode);
					comment = rs1.getString("comments");
					if (ToolsHTML.isEmptyOrNull(comment)) {
						forma.setcomment("");
					} else {
					//	forma.setcommentDescript(comment);
					forma.setcomment(comment);
					}
				}
				
			}
				
		} catch (Exception ex) {
			log.error(ex.getMessage());
			ex.printStackTrace();
		}
		return result;
	}

	public String getOrdenPublicados(String ordenVersion, String ordenNombre,
			String ordenTypeDocument, String ordenNumber,
			String ordenPropietario, String ordenApproved) {
		StringBuilder orderBy = new StringBuilder(100);
		boolean sworderBye = false;
		if (!ToolsHTML.isEmptyOrNull(ordenVersion)) {
			if (sworderBye) {
				orderBy.append(",");
			}
			orderBy.append("  vd.MayorVer,vd.MinorVer");
			sworderBye = true;
		}
		if (!ToolsHTML.isEmptyOrNull(ordenNombre)) {
			if (sworderBye) {
				orderBy.append(",");
			}
			orderBy.append("  lower(d.nameDocument)");
			sworderBye = true;
		}
		if (!ToolsHTML.isEmptyOrNull(ordenTypeDocument)) {
			if (sworderBye) {
				orderBy.append(",");
			}
			orderBy.append("  lower(td.typeDoc)");
			sworderBye = true;
		}
		if (!ToolsHTML.isEmptyOrNull(ordenNumber)) {
			if (sworderBye) {
				orderBy.append(",");
			}
			orderBy.append("  lower(d.prefix),d.number ");
			sworderBye = true;
		}
		if (!ToolsHTML.isEmptyOrNull(ordenPropietario)) {
			if (sworderBye) {
				orderBy.append(",");
			}
			orderBy.append(" lower(p.apellidos), lower(p.nombres) ");
			// orderBy.append(" nombre");
			sworderBye = true;
		}
		if (!ToolsHTML.isEmptyOrNull(ordenApproved)) {
			if (sworderBye) {
				orderBy.append(",");
			}
			orderBy.append(" vd.dateApproved");
			sworderBye = true;
		}
		return orderBy.toString();
	}

	public String getQueryPublicados(Connection conn, String propietario, String typeDocument,
			String number, String prefix, String version, String keys,
			String name, Users usuario, String orderBy, String idNode,
			String dateExpiredFrom, String dateExpiredTo,
			String dateApprovedFrom, String dateApprovedTo,
			BaseDocumentForm doc, String lote, String normISO,
			String comment, String idProgramAudit, String idPlanAudit) { //ydavila Ticket 001-00-003265 Se agrega b�squeda de comentarios	

		//String dateSystem = ToolsHTML.date.format(new Date()) + " 11:59:59 PM";
		
		StringBuilder sql = new StringBuilder(2048);
		switch (Constants.MANEJADOR_ACTUAL) {
		case Constants.MANEJADOR_MSSQL:
			sql.append(" UNION SELECT vd.MayorVer+'.'+vd.MinorVer AS version, d.prefix + d.number AS correlativo,");
			sql.append("(p.apellidos + ' ' + p.nombres) AS Nombre,");
			sql.append("(CASE WHEN h.comments IS NULL THEN CAST(d.comments AS VARCHAR(4000)) ELSE CAST(h.comments AS VARCHAR(4000)) END) AS comments, ");
			break;
		case Constants.MANEJADOR_POSTGRES:
			sql.append(" UNION SELECT vd.MayorVer||'.'||vd.MinorVer AS version, d.prefix || d.number AS correlativo,");
			sql.append("(p.apellidos || ' ' || p.nombres) AS Nombre,");
			sql.append("(CASE WHEN h.comments IS NULL THEN CAST(d.comments AS VARCHAR(4000)) ELSE  CAST(h.comments AS VARCHAR(4000)) END) AS comments, ");
			break;
		case Constants.MANEJADOR_MYSQL:
			sql.append(" UNION SELECT CONCAT(vd.MayorVer,'.',vd.MinorVer) AS version, CONCAT(d.prefix,d.number) AS correlativo,");
			sql.append("CONCAT(p.apellidos , ' ' , p.nombres) AS Nombre,");
			sql.append("IFNULL( h.comments , d.comments ) AS comments, ");
			break;
		}
		sql.append("d.numGen,d.number,d.nameDocument,d.prefix,d.nameFile");
		sql.append(",d.type,d.normISO,d.owner,d.versionPublic,d.IdNode,vd.dateApproved,vd.dateExpires,d.statu,d.dateCreation,");
		sql.append("p.idperson,td.typeDoc, lower(td.typeDoc) AS tipoDoc,vd.numVer,vd.MayorVer,vd.MinorVer,0 AS statusimpresion, ");
		sql.append(" CAST(h.comments AS VARCHAR(4000)) AS comments ");
		//sql.append(" FROM documents d, person p,typedocuments td,  versiondoc vd ");
		//sql.append(" LEFT OUTER JOIN historydocs h ON h.idNode=vd.numDoc AND h.mayorVer=vd.mayorVer AND h.minorVer=vd.minorVer AND h.dateChange=(select max(hi.dateChange) from historydocs hi where hi.type IN (5,14) and hi.idNode=vd.numDoc group by hi.idNode )  ");
		sql.append(" FROM documents d, person p, typedocuments td,  versiondoc vd "); 
		sql.append(" LEFT OUTER JOIN historydocs h ON vd.numDoc=h.idNode AND h.idHistory=(select max(hi.idHistory) from historydocs hi where hi.type IN ('5','14') and hi.idNode=vd.numDoc AND hi.mayorVer=vd.mayorVer AND hi.MinorVer=vd.minorVer) ");
		sql.append(" WHERE docPublic = '0' AND d.active = '1' ");
		sql.append(" AND  p.accountactive= '").append(Constants.permissionSt).append("'");
		
		// ydavila Ticket 001-00-003168 Lista Maestra
		if (!ToolsHTML.isEmptyOrNull(normISO) && !"0".equalsIgnoreCase(normISO)) {
			// Chequea si la norma seleccionada es cabecera (Indice = 0)
			List<String> normasList = HandlerNorms.getNormsBySistemaGestion(conn, normISO);
			if (normasList.size() == 0) {
				sql.append(" AND (d.normiso LIKE '%").append(normISO).append("%') ");
			} else {
				int prefijoIso = Integer.parseInt(normISO.substring(0,normISO.length()-2));
				
				sql.append(" AND (d.normiso LIKE '").append(prefijoIso+0).append("%' or d.normiso LIKE '%,").append(prefijoIso+0).append("%'"); 
	            sql.append("  OR  d.normiso LIKE '").append(prefijoIso+1).append("%' or d.normiso LIKE '%,").append(prefijoIso+1).append("%'");
	            sql.append("  OR  d.normiso LIKE '").append(prefijoIso+2).append("%' or d.normiso LIKE '%,").append(prefijoIso+2).append("%'");
	            sql.append("  OR  d.normiso LIKE '").append(prefijoIso+3).append("%' or d.normiso LIKE '%,").append(prefijoIso+3).append("%'");
	            sql.append("  OR  d.normiso LIKE '").append(prefijoIso+4).append("%' or d.normiso LIKE '%,").append(prefijoIso+4).append("%')"); 

	            List<String> indice;
				indice = HandlerNorms.getIndiceBySistemaGestion(conn, normISO);
				//ydavila sql.append(" AND (indice <> '0' )");
			}	
		}
	
		/*
		switch (Constants.MANEJADOR_ACTUAL) {
		case Constants.MANEJADOR_MSSQL:
			sql.append(" AND d.datePublic <= CONVERT(datetime,'")
					.append(dateSystem).append("',120)  ");
			break;
		case Constants.MANEJADOR_POSTGRES:
			sql.append(" AND d.datePublic <= CAST('").append(dateSystem)
					.append("' as timestamp) ");
			break;
		case Constants.MANEJADOR_MYSQL:
			sql.append(" AND d.datePublic <= '").append(dateSystem).append("' ");
			break;
		}*/
		
		//sql.append("AND h.idnode = d.numGen AND h.type = '5'");
		//sql.append("AND h.idnode = d.numGen");
		//sql.append(" AND vd.numVer = (SELECT MAX(vdi.numVer) FROM versiondoc vdi  WHERE vdi.numDoc = d.numGen and vdi.statu='").append(HandlerDocuments.docApproved).append("')");
		sql.append(" AND vd.numDoc = d.numGen ");
		sql.append(" AND p.nameUser = d.owner AND td.idTypeDoc = cast(d.type as int)");
				
		//ydavila la b�squeda muestra documentos no consistentes con la b�squeda por PREFIJO
		if (!ToolsHTML.isEmptyOrNull(prefix)) {
			sql.append(" AND (d.prefix LIKE '%").append(prefix).append("%') ");
		}
		// sql.append(" AND n.idnorm = d.owner AND td.idTypeDoc = cast(d.type as int)");

		// campos adicionales
		if (doc != null && doc.getListaCampos() != null) {
			for (int i = 0; i < doc.getListaCampos().size(); i++) {
				try {
					DocumentForm obj = (DocumentForm) doc.getListaCampos().get(
							i);
					String valor = (String) doc.get(obj.getId().toUpperCase());
					if (!ToolsHTML.isEmptyOrNull(valor)) {
						sql.append(" AND d.").append(obj.getId())
								.append(" like '").append(valor).append("' ");
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}

		if (!ToolsHTML.isEmptyOrNull(prefix)) {
			sql.append(" AND (d.prefix LIKE '%").append(prefix).append("%') ");
		}
		sql.append(" AND vd.statu='").append(HandlerDocuments.docApproved).append("'");
		sql.append(" AND d.versionPublic=vd.numVer ");
		// Esta condicion no permite visualizar documentos en flujo de impresion en la lista maestra
		//sql.append(" AND d.numGen NOT IN (SELECT numGen FROM tbl_solicitudimpresion im WHERE im.numGen = d.numGen AND im.numVer = vd.numVer");
		//sql.append(" AND im.solicitante = ").append(usuario.getIdPerson());
		//sql.append(" AND im.statusimpresion = '").append(loadsolicitudImpresion.aprobadoprintln).append("')");
		sql.append(" AND ").append("(");

		// Parte Inicial del Query
		StringBuilder query = new StringBuilder(2048);
		switch (Constants.MANEJADOR_ACTUAL) {
		case Constants.MANEJADOR_MSSQL:
			query.append("SELECT vd.MayorVer+'.'+vd.MinorVer AS version, d.prefix + d.number AS correlativo,");
			query.append("(p.apellidos + ' ' + p.nombres) AS Nombre,");
			query.append(" CAST(h.comments AS VARCHAR(4000)) AS comments, ");
			break;
		case Constants.MANEJADOR_POSTGRES:
			query.append("SELECT vd.MayorVer||'.'||vd.MinorVer AS version, d.prefix || d.number AS correlativo,");
			query.append("(p.apellidos || ' ' || p.nombres) AS Nombre,");
			query.append("h.comments, ");
			break;
		case Constants.MANEJADOR_MYSQL:
			query.append("SELECT CONCAT(vd.MayorVer,'.',vd.MinorVer) AS version, CONCAT(d.prefix , d.number) AS correlativo,");
			query.append("CONCAT(p.apellidos , ' ' , p.nombres) AS Nombre,");
			query.append("h.comments, ");

			break;
		}
		query.append("d.numGen,d.number,d.nameDocument,d.prefix,d.nameFile,");
		query.append("d.type,d.normISO,d.owner,d.versionPublic,d.IdNode,vd.dateApproved,vd.dateExpires,d.statu,d.dateCreation,");
		
		
		//query.append(" d.comments, "); //ydavila Ticket 001-00-003265 Se agrega b�squeda de comentarios
		query.append("p.idperson,td.typeDoc, lower(td.typeDoc) AS tipoDoc,vd.numVer,vd.MayorVer,vd.MinorVer,im.statusimpresion, ");
		query.append(" CAST(h.comments AS VARCHAR(4000)) AS comments ");
		//"p.idperson,td.typeDoc, lower(td.typeDoc) AS tipoDoc,vd.numVer,vd.MayorVer,vd.MinorVer,im.statusimpresion,CAST(h.comments AS VARCHAR(4000))")
		query.append(" FROM documents d, ");
		query.append(" (SELECT MAX(vdi.numVer) AS numVer, vdi.numDoc, vdi.statu, vdi.mayorVer, vdi.minorVer,vdi.dateApproved,vdi.dateExpires FROM versiondoc vdi WHERE vdi.statu='").append(HandlerDocuments.docApproved).append("' group by vdi.numDoc, vdi.statu, vdi.mayorVer, vdi.minorVer,vdi.dateApproved,vdi.dateExpires) vd, ");
		query.append(" person p,typedocuments td,tbl_solicitudimpresion im, historydocs h ");
		query.append(" WHERE docPublic = '0' AND d.active = '1' ");
		query.append(" AND  p.accountactive= '").append(Constants.permissionSt).append("' ");
		query.append(" AND h.idNode=vd.numDoc AND h.mayorVer=vd.mayorVer AND h.minorVer=vd.minorVer ");
		query.append(" AND h.type='14' ");
		if (!ToolsHTML.isEmptyOrNull(prefix)) {
			query.append(" AND (d.prefix LIKE '%").append(prefix).append("%') ");
		}
		//ydavila Ticket 001-00-003168 Lista Maestra. 
		if (!ToolsHTML.isEmptyOrNull(normISO) && !"0".equalsIgnoreCase(normISO)) {
			// Chequea si la norma seleccionada es cabecera (Indice = 0)
			List<String> normasList = HandlerNorms.getNormsBySistemaGestion(conn, normISO);
			if (normasList.size() == 0) {
				query.append(" AND (d.normiso LIKE '%").append(normISO).append("%') ");
			} else {
				int prefijoIso = Integer.parseInt(normISO.substring(0,normISO.length()-2));
				
				query.append(" AND (d.normiso LIKE '").append(prefijoIso+0).append("%' or d.normiso LIKE '%,").append(prefijoIso+0).append("%'"); 
				query.append("  OR  d.normiso LIKE '").append(prefijoIso+1).append("%' or d.normiso LIKE '%,").append(prefijoIso+1).append("%'");
				query.append("  OR  d.normiso LIKE '").append(prefijoIso+2).append("%' or d.normiso LIKE '%,").append(prefijoIso+2).append("%'");
				query.append("  OR  d.normiso LIKE '").append(prefijoIso+3).append("%' or d.normiso LIKE '%,").append(prefijoIso+3).append("%'");
				query.append("  OR  d.normiso LIKE '").append(prefijoIso+4).append("%' or d.normiso LIKE '%,").append(prefijoIso+4).append("%')"); 
			}	
		}
		
		/*
		switch (Constants.MANEJADOR_ACTUAL) {
		case Constants.MANEJADOR_MSSQL:
			query.append(" AND d.datePublic <= CONVERT(datetime,'").append(dateSystem).append("',120)  ");
			break;
		case Constants.MANEJADOR_POSTGRES:
			query.append(" AND d.datePublic <= CAST('").append(dateSystem).append("' as timestamp) ");
			break;
		case Constants.MANEJADOR_MYSQL:
			query.append(" AND d.datePublic <= TIMESTAMP('").append(dateSystem).append("') ");
			break;
		}*/
		
		
		//query.append(" AND h.idnode = d.numGen AND h.type = '5'");
		//query.append("AND h.idnode = d.numGen");
		//query.append(" AND vd.numVer = (SELECT MAX(vdi.numVer) FROM versiondoc vdi ").append(" WHERE vdi.numDoc = d.numGen ").append(" AND vdi.statu='").append(HandlerDocuments.docApproved).append("')");
		query.append(" AND vd.numDoc = d.numGen ");
		query.append(" AND p.nameUser = d.owner AND td.idTypeDoc = cast(d.type as int)");
		query.append(" AND vd.statu='").append(HandlerDocuments.docApproved).append("' "); 
						
		// NEW
		// campos adicionales
		
		if (doc != null && doc.getListaCampos() != null) {
			for (int i = 0; i < doc.getListaCampos().size(); i++) {
				try {
					DocumentForm obj = (DocumentForm) doc.getListaCampos().get(
							i);
					String valor = (String) doc.get(obj.getId().toUpperCase());
					if (!ToolsHTML.isEmptyOrNull(valor)) {
						query.append(" AND d.").append(obj.getId())
								.append(" like '").append(valor).append("' ");
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}

		query.append(" AND d.numGen = im.numGen AND vd.numVer = im.numVer")
				.append(" AND im.solicitante = ").append(usuario.getIdPerson())
				.append(" AND im.statusimpresion = '")
				.append(loadsolicitudImpresion.aprobadoprintln).append("'");
		// NEW
		query.append(" AND ").append("(");
		sql.append("td.idTypeDoc <> ")
				.append(HandlerDocuments.TypeDocumentsImpresion).append("")
				.append(")");
		// Query II
		query.append("td.idTypeDoc <> ")
				.append(HandlerDocuments.TypeDocumentsImpresion).append("")
				.append(")");
		if (!ToolsHTML.isEmptyOrNull(propietario)
				&& (!"0".equalsIgnoreCase(propietario))) {
			// sql.append(" AND d.owner LIKE '%").append(propietario).append("%'");
			// query.append(" AND d.owner LIKE '%").append(propietario).append("%'");
			sql.append(" AND d.owner = '").append(propietario).append("'");
			query.append(" AND d.owner = '").append(propietario).append("'");
		}
		
		if (!ToolsHTML.isEmptyOrNull(typeDocument)
				&& (!"0".equalsIgnoreCase(typeDocument))) {
			// Si es un Documento de Libre Impresi�n se procede a hacer la
			// B�squeda as�...
			// if
			// (DesigeConf.getProperty("typeDocs.docPrintFreeID").compareTo(typeDocument.trim())==0)
			// {
			// sql.append(" AND d.keys LIKE
			// '%").append(DesigeConf.getProperty("typeDocs.docPrintFree")).append("%'");
			// query.append(" AND d.keys LIKE
			// '%").append(DesigeConf.getProperty("typeDocs.docPrintFree")).append("%'");
			// } else {
			sql.append(" AND d.type = '").append(typeDocument).append("'");
			query.append(" AND d.type = '").append(typeDocument).append("'");
			// }
		}
		if (!ToolsHTML.isEmptyOrNull(number)) {
			sql.append(" AND d.number = '").append(number).append("'");
			query.append(" AND d.number = '").append(number).append("'");
		}
		if (ToolsHTML.isNumeric(idProgramAudit)) {
			sql.append(" AND d.idProgramAudit = ").append(idProgramAudit).append("");
			query.append(" AND d.idProgramAudit = ").append(idProgramAudit).append("");
		}
		if (ToolsHTML.isNumeric(idPlanAudit)) {
			sql.append(" AND d.idPlanAudit = ").append(idPlanAudit).append("");
			query.append(" AND d.idPlanAudit = ").append(idPlanAudit).append("");
		}
		if (!ToolsHTML.isEmptyOrNull(version)) {
			// int pos = nameFile.indexOf(".");
			int pos = version.indexOf(".");
			if (pos > 0) {
				String MayorVer = version.substring(0, pos);
				String MinorVer = version.substring(pos + 1);
				sql.append(" AND vd.MinorVer = '").append(MinorVer).append("'");
				sql.append(" AND vd.MayorVer = '").append(MayorVer).append("'");
				//
				query.append(" AND vd.MinorVer = '").append(MinorVer)
						.append("'");
				query.append(" AND vd.MayorVer = '").append(MayorVer)
						.append("'");
			}
		}
		if (!ToolsHTML.isEmptyOrNull(name)) {
			sql.append(" AND lower(d.nameDocument) LIKE '%")
					.append(name.toLowerCase()).append("%'");
			//
			query.append(" AND lower(d.nameDocument) LIKE '%")
					.append(name.toLowerCase()).append("%'");
		}

		if (!ToolsHTML.isEmptyOrNull(lote)) {
			sql.append(" AND LOWER(d.loteDoc) LIKE '%")
					.append(lote.toLowerCase()).append("%'");
			//
			query.append(" AND LOWER(d.loteDoc) LIKE '%")
					.append(lote.toLowerCase()).append("%'");
		}

		// Si la B�squeda es por Carpetas, se deben buscar todos los nodos hijos
		// a partir del Nodo
		// Indicado

		if (!ToolsHTML.isEmptyOrNull(idNode)) {
			// sql.append(" AND d.idNode = ").append(idNode).append("");
			String hijos = HandlerStruct.getAllNodesChilds(conn, idNode);
			sql.append(" AND d.idNode IN (").append(idNode);
			//
			query.append(" AND d.idNode IN (").append(idNode);
			if (!ToolsHTML.isEmptyOrNull(hijos)) {
				sql.append(",").append(hijos);
				query.append(",").append(hijos);
			}
			sql.append(")");
			query.append(")");
			// sql.append(" AND (d.idNode =").append(idNode);
			// sql.append(" or d.idnode in (select idnode from struct where
			// idnodeparent=").append(idNode).append(")) ");
		}
				
		String field = null;
		boolean valida = false;
		switch (3) {
		case 1:
			field = " (nameDocument ";
			valida = true;
			break;
		case 2:
			;
			field = " (number ";
			valida = true;
			break;
		case 3:
			//field = " (lower(keys) ";
			field = " (lower(keysdoc) ";
			valida = true;
			break;
		}
		if (!ToolsHTML.isEmptyOrNull(keys) && valida) {
			StringBuilder searchTo = new StringBuilder(100);
			StringTokenizer st = new StringTokenizer(keys, ",");
			boolean first = true;
			while (st.hasMoreTokens()) {
				if (first) {
					searchTo.append(" AND ( ");
					first = false;
				} else {
					searchTo.append(" OR ");
				}
				searchTo.append(field).append(" LIKE '%")
						.append(st.nextToken().trim().toLowerCase())
						.append("%') ");
			}
			if (valida) {
				if (!ToolsHTML.isEmptyOrNull(keys) && valida) {
					sql.append(searchTo.toString()).append(" )");
					query.append(searchTo.toString()).append(" )");
				} else {
					sql.append(searchTo.toString());
					query.append(searchTo.toString());
				}
			}
		}

		// INI:busqueda por fecha de aprobacion
		// Filtro por fecha de aprobacion
		// Puede fitrar por cualquiera de las dos fechas
		// o por el rango
		if (!ToolsHTML.isEmptyOrNull(dateApprovedFrom)
				&& !ToolsHTML.isEmptyOrNull(dateApprovedTo)
				&& dateApprovedFrom.length() > 9 && dateApprovedTo.length() > 9) {
			switch (Constants.MANEJADOR_ACTUAL) {
			case Constants.MANEJADOR_MSSQL:
				query.append(" AND (vd.dateApproved BETWEEN CONVERT(datetime, '"
						+ dateApprovedFrom + " 0:00:00 AM', 120) ");
				query.append(" AND CONVERT(datetime, '" + dateApprovedTo
						+ " 11:59:59 PM', 120) ) ");

				sql.append(" AND (vd.dateApproved BETWEEN CONVERT(datetime, '"
						+ dateApprovedFrom + " 0:00:00 AM', 120) ");
				sql.append(" AND CONVERT(datetime, '" + dateApprovedTo
						+ " 11:59:59 PM', 120) ) ");
				break;
			case Constants.MANEJADOR_POSTGRES:
				query.append(" AND (vd.dateApproved BETWEEN CAST('")
						.append(dateApprovedFrom).append("' as timestamp)  ");
				query.append(" AND CAST('").append(dateApprovedTo)
						.append("' as timestamp) ) ");

				sql.append(" AND (vd.dateApproved BETWEEN CAST('")
						.append(dateApprovedFrom).append("' as timestamp)  ");
				sql.append(" AND CAST('").append(dateApprovedTo)
						.append("' as timestamp)  ) ");
				break;
			case Constants.MANEJADOR_MYSQL:
				query.append(" AND (vd.dateApproved BETWEEN TIMESTAMP('")
						.append(dateApprovedFrom).append("')  ");
				query.append(" AND TIMESTAMP('").append(dateApprovedTo)
						.append("') ) ");

				sql.append(" AND (vd.dateApproved BETWEEN TIMESTAMP('")
						.append(dateApprovedFrom).append("')  ");
				sql.append(" AND TIMESTAMP('").append(dateApprovedTo)
						.append("')  ) ");
				break;
			}
		}
		if (!ToolsHTML.isEmptyOrNull(dateApprovedFrom)
				&& dateApprovedFrom.length() > 9) {
			switch (Constants.MANEJADOR_ACTUAL) {
			case Constants.MANEJADOR_MSSQL:
				query.append(" AND (vd.dateApproved >= CONVERT(datetime, '"
						+ dateApprovedFrom + " 0:00:00 AM', 120) ) ");

				sql.append(" AND (vd.dateApproved >= CONVERT(datetime, '"
						+ dateApprovedFrom + " 0:00:00 AM', 120) ) ");
				break;
			case Constants.MANEJADOR_POSTGRES:
				query.append(" AND (vd.dateApproved >= CAST('")
						.append(dateApprovedFrom).append("' as timestamp) ) ");

				sql.append(" AND (vd.dateApproved >= CAST('")
						.append(dateApprovedFrom).append("' as timestamp) ) ");
				break;
			case Constants.MANEJADOR_MYSQL:
				query.append(" AND (vd.dateApproved >= TIMESTAMP('")
						.append(dateApprovedFrom).append("') ) ");

				sql.append(" AND (vd.dateApproved >= TIMESTAMP('")
						.append(dateApprovedFrom).append("') ) ");
				break;
			}
		}
		// FIN:busqueda por fecha de aprobacion

		// Luis Cisneros
		// 23-03-07
		// Filtro por fecha de vencimiento.
		// Modificado 26/oct/2007. Puede fitrar por cualquiera de las dos fechas
		// o por el rango
		if (!ToolsHTML.isEmptyOrNull(dateExpiredFrom)
				&& !ToolsHTML.isEmptyOrNull(dateExpiredTo)
				&& dateExpiredFrom.length() > 9 && dateExpiredTo.length() > 9) {
			switch (Constants.MANEJADOR_ACTUAL) {
			case Constants.MANEJADOR_MSSQL:
				query.append(" AND (vd.dateExpires BETWEEN CONVERT(datetime, '"
						+ dateExpiredFrom + " 0:00:00 AM', 120) ");
				query.append(" AND CONVERT(datetime, '" + dateExpiredTo
						+ " 11:59:59 PM', 120) ) ");

				sql.append(" AND (vd.dateExpires BETWEEN CONVERT(datetime, '"
						+ dateExpiredFrom + " 0:00:00 AM', 120) ");
				sql.append(" AND CONVERT(datetime, '" + dateExpiredTo
						+ " 11:59:59 PM', 120) ) ");
				break;
			case Constants.MANEJADOR_POSTGRES:
				query.append(" AND (vd.dateExpires BETWEEN CAST('")
						.append(dateExpiredFrom).append("' as timestamp)  ");
				query.append(" AND CAST('").append(dateExpiredTo)
						.append("' as timestamp) ) ");

				sql.append(" AND (vd.dateExpires BETWEEN CAST('")
						.append(dateExpiredFrom).append("' as timestamp)  ");
				sql.append(" AND CAST('").append(dateExpiredTo)
						.append("' as timestamp)  ) ");
				break;
			case Constants.MANEJADOR_MYSQL:
				query.append(" AND (vd.dateExpires BETWEEN TIMESTAMP('")
						.append(dateExpiredFrom).append("')  ");
				query.append(" AND TIMESTAMP('").append(dateExpiredTo)
						.append("') ) ");

				sql.append(" AND (vd.dateExpires BETWEEN TIMESTAMP('")
						.append(dateExpiredFrom).append("')  ");
				sql.append(" AND TIMESTAMP('").append(dateExpiredTo)
						.append("')  ) ");
				break;
			}
		}

		if (!ToolsHTML.isEmptyOrNull(dateExpiredFrom)
				&& dateExpiredFrom.length() > 9) {
			switch (Constants.MANEJADOR_ACTUAL) {
			case Constants.MANEJADOR_MSSQL:
				query.append(" AND (vd.dateExpires >= CONVERT(datetime, '"
						+ dateExpiredFrom + " 0:00:00 AM', 120) ) ");

				sql.append(" AND (vd.dateExpires >= CONVERT(datetime, '"
						+ dateExpiredFrom + " 0:00:00 AM', 120) ) ");
				break;
			case Constants.MANEJADOR_POSTGRES:
				query.append(" AND (vd.dateExpires >= CAST('")
						.append(dateExpiredFrom).append("' as timestamp) ) ");

				sql.append(" AND (vd.dateExpires >= CAST('")
						.append(dateExpiredFrom).append("' as timestamp) ) ");
				break;
			case Constants.MANEJADOR_MYSQL:
				query.append(" AND (vd.dateExpires >= TIMESTAMP('")
						.append(dateExpiredFrom).append("') ) ");

				sql.append(" AND (vd.dateExpires >= TIMESTAMP('")
						.append(dateExpiredFrom).append("') ) ");
				break;
			}
		}

		if (!ToolsHTML.isEmptyOrNull(dateExpiredTo)
				&& dateExpiredTo.length() > 9) {
			switch (Constants.MANEJADOR_ACTUAL) {
			case Constants.MANEJADOR_MSSQL:
				query.append(" AND (vd.dateExpires <= CONVERT(datetime, '"
						+ dateExpiredTo + " 11:59:59 PM', 120) ) ");

				sql.append(" AND (vd.dateExpires <= CONVERT(datetime, '"
						+ dateExpiredTo + " 11:59:59 PM', 120) ) ");
				break;
			case Constants.MANEJADOR_POSTGRES:
				query.append(" AND (vd.dateExpires <= CAST('")
						.append(dateExpiredTo).append("' as timestamp) ) ");

				sql.append(" AND (vd.dateExpires <=  CAST('")
						.append(dateExpiredTo).append("' as timestamp) ) ");
				break;
			case Constants.MANEJADOR_MYSQL:
				query.append(" AND (vd.dateExpires <= TIMESTAMP('")
						.append(dateExpiredTo).append("') ) ");

				sql.append(" AND (vd.dateExpires <=  TIMESTAMP('")
						.append(dateExpiredTo).append("') ) ");
				break;
			}
		}
	 
//   if ((!ToolsHTML.isEmptyOrNull(normISO)) && (!"0".equals(normISO))) {
//sql.append(" AND n.key_search LIKE '" + normISO + "%' ");
//}
		
		query.append(sql);
		if (!ToolsHTML.isEmptyOrNull(orderBy)) {
			query.append(" ORDER BY ");
			query.append(orderBy.toString());
		} else {
			switch (Constants.MANEJADOR_ACTUAL) {
			case Constants.MANEJADOR_MSSQL:
				query.append(" ORDER BY ").append(
						DesigeConf.getProperty("orderBy"));
				break;
			case Constants.MANEJADOR_POSTGRES:
				query.append(" ORDER BY ").append(
						DesigeConf.getProperty("orderByPostgres"));
				break;
			case Constants.MANEJADOR_MYSQL:
				query.append(" ORDER BY ").append(
						DesigeConf.getProperty("orderByMysql"));
				break;
			}
		}
		return query.toString();
	}

	public static Collection getDocumentsPublished(String propietario,
			String typeDocument, String number, String prefix, String version,
			String keys, String name, Users usuario, String orderBy,
			String idNode, Hashtable struct, String dateExpiredFrom,
			String dateExpiredTo, String dateApprovedFrom,
			String dateApprovedTo, BaseDocumentForm doc, String lote) {

		return getDocumentsPublished(propietario, typeDocument, number, prefix,
				version, keys, name, usuario, orderBy, idNode, struct,
				dateExpiredFrom, dateExpiredTo, dateApprovedFrom,
				dateApprovedTo, doc, lote, null, null, null, null);
	}

	public static Collection getDocumentsPublished(String propietario,
			String typeDocument, String number, String prefix, String version,
			String keys, String name, Users usuario, String orderBy,
			String idNode, Hashtable struct, String dateExpiredFrom,
			String dateExpiredTo, String dateApprovedFrom,
			String dateApprovedTo, BaseDocumentForm doc, String lote,
			String normISO,String comment, String idProgramAudit, String idPlanAudit) {
		Connection conn = null;
		PreparedStatement stquery = null;
		ResultSet rs = null;
		Vector result = new Vector();
		Hashtable routs = new Hashtable();
		Hashtable prefijos = new Hashtable();
		HandlerDocuments hd = new HandlerDocuments();
		String routDoc = "";
		//String commenttrunc="";
		
		
		try {
			List<String> normasList = new ArrayList<String>();
			if (!ToolsHTML.isEmptyOrNull(normISO) && !"0".equalsIgnoreCase(normISO)) {
				// Chequea si la norma selecciona es cabecera (Indice = 0)
				normasList = HandlerNorms.getNormsBySistemaGestion(normISO);
			}
			int listLen = normasList.size();

			conn = JDBCUtil.getConnection(Thread.currentThread().getStackTrace()[1].getMethodName());
			
			// query completo
			String query = hd.getQueryPublicados(conn, propietario, typeDocument, number,
					prefix, version, keys, name, usuario, orderBy, idNode,
					dateExpiredFrom, dateExpiredTo, dateApprovedFrom,
					dateApprovedTo, doc, lote, normISO, comment, idProgramAudit, idPlanAudit);
			log.debug("[getDocumentsPublished] = " + query);
			// //System.out.println("[getDocumentsPublished] = " + query);
			

			Hashtable norms1 = HandlerNorms.getAllNormas(conn);
			
			stquery = conn.prepareStatement(JDBCUtil.replaceCastMysql(query.toString()));
  			rs = stquery.executeQuery();
			
			while (rs.next()) {
				
	
				
				String statuDoc = null;
				BaseDocumentForm forma = new BaseDocumentForm();
				forma.setIdNode(String.valueOf(rs.getInt("IdNode")));

				forma.setIdDocument(String.valueOf(rs.getInt("numGen")));
				forma.setIsflowusuario("-1");
				statuDoc = rs.getString("statu").trim();
				// Se cargan los Usuarios Pendientes en el Flujo si el Documento
				// se encuentra
				// Sometido a un Flujo de Revisi�n o Aprobaci�n
				if ((docInReview.compareTo(statuDoc) == 0)
						|| (docInApproved.compareTo(statuDoc) == 0)
						|| (inFlewFlow.compareTo(statuDoc) == 0)
						|| docObs.compareTo(statuDoc) == 0) {
					Collection userPending = null;
					// Colocar Opci�n para Flujos Param�trico :D
					if (inFlewFlow.compareTo(statuDoc) != 0) {
						userPending = HandlerWorkFlows.getUserInWorkFlowPending(conn, forma.getIdDocument(),HandlerWorkFlows.wfuPending, false);
					} else {
						// Es un Flujo Param�trico se procede a Cargar los
						// Usuarios del Mismo
						userPending = HandlerWorkFlows.getUserInWorkFlowPending(conn,forma.getIdDocument(),HandlerWorkFlows.wfuPending, true);
					}
					// userPending =
					// HandlerWorkFlows.getUserInWorkFlowPending(forma.getIdDocument(),HandlerWorkFlows.wfuPending,false);
					if (userPending != null) {
						int i = 0;
						String nameUsuario = "-1";
						for (Iterator it = userPending.iterator(); it.hasNext();) {
							ParticipationForm g = (ParticipationForm) it.next();
							++i;
							// si hay mas de uno pendiente, es no secuencial el
							// flujo
							if (i == 1) {
								nameUsuario = g.getNameUser() + " ("
										+ g.getCharge() + ")";
								forma.setIsNotflowsecuencial("0");
								forma.setIsflowcargo(g.getCharge());
							} else {
								nameUsuario += g.getNameUser() + "("
										+ g.getCharge() + ")";
								forma.setIsNotflowsecuencial("1");
							}
							forma.setIsflowusuario(nameUsuario);
						}
					}
				}
				// Impresi�n sobre el Documento Aprobadas
				int statusimpresion = rs.getInt("statusimpresion");
				if (loadsolicitudImpresion.aprobadoprintlnInt == statusimpresion) {
					forma.setImprimir("imprimir");
				} else {
					forma.setImprimir("");
				}
				// Fin de la Impresi�n

				forma.setNumber(rs.getString("number") != null ? rs
						.getString("number") : "");
				forma.setNameDocument(rs.getString("nameDocument") != null ? rs
						.getString("nameDocument") : "");
				String prefixDoc = null;
				if (struct != null) {
					if (!prefijos.containsKey(forma.getIdNode())) {
						prefixDoc = ToolsHTML.getPrefixToDoc(struct,
								forma.getIdNode());
						prefijos.put(forma.getIdNode(), prefixDoc);
					} else {
						prefixDoc = (String) prefijos.get(forma.getIdNode());
					}
				} else {
					prefixDoc = rs.getString("prefix");
				}
				if (!ToolsHTML.isEmptyOrNull(prefixDoc)) {
					forma.setPrefix(prefixDoc.trim());
				} else {
					forma.setPrefix("");
				}
				
				String idNorm = rs.getString("normiso");
				if (ToolsHTML.isEmptyOrNull(idNorm)) {
					forma.setNormISO("");
				} else {
					forma.setNormISO(idNorm);
				}

				Search titleNorm = null;
				String tempString = forma.getNormISO();
				int pos = tempString.indexOf(',');
				if (pos != -1) {
					titleNorm = (Search) norms1.get(normISO);
				} else {
					titleNorm = (Search) norms1.get(tempString);
				}

				// log.debug("forma.getNormISO() = " + forma.getNormISO());
				// log.debug("titleNorm = " + titleNorm);
				if (titleNorm != null) {
					forma.setNormISODescript(titleNorm.getDescript());
				} else {
					titleNorm = (Search) norms1.get(normISO);
					if (titleNorm != null) {
						forma.setNormISODescript(titleNorm.getDescript());
					} else {
						if (pos == -1) {
							forma.setNormISODescript("");
						} else {
							forma.setNormISODescript("<b>Documento con multiples requisitos</b>");

						}
					}
				}
				
				forma.setComments(rs.getString("comments")!=null?rs.getString("comments"):"");
				
				forma.setTypeDocument(rs.getString("type") != null ? rs
						.getString("type") : "");
				forma.setDescriptTypeDoc(rs.getString("typeDoc") != null ? rs
						.getString("typeDoc") : "");
				forma.setOwner(rs.getString("Nombre") != null ? rs
						.getString("Nombre") : "");
				forma.setIdperson(rs.getString("idperson") != null ? rs
						.getString("idperson") : "");
				forma.setMayorVer(rs.getString("MayorVer") != null ? rs
						.getString("MayorVer") : "");
				forma.setMinorVer(rs.getString("MinorVer") != null ? rs
						.getString("MinorVer") : "");
				String dateApprove = rs.getString("dateApproved") != null ? rs
						.getString("dateApproved") : "";
				String dateExpires = rs.getString("dateExpires") != null ? rs
						.getString("dateExpires") : "";

				forma.setDateApproved(ToolsHTML.formatDateShow(!ToolsHTML
						.isEmptyOrNull(dateApprove) ? dateApprove : "", false));
				forma.setDateExpires(ToolsHTML.formatDateShow(!ToolsHTML
						.isEmptyOrNull(dateExpires) ? dateExpires : "", false));

				forma.setNumVer(Integer.parseInt(rs.getString("numVer") != null ? rs
						.getString("numVer") : "0"));
				if (!routs.containsKey(forma.getIdNode().trim())) {
					routDoc = HandlerStruct.getRout(conn, "", forma.getIdNode(),
							forma.getIdNode(),null, null, false);
					routs.put(forma.getIdNode(), routDoc);
					forma.setRout(routDoc + forma.getNameDocument());
				} else {
					routDoc = (String) routs.get(forma.getIdNode());
					forma.setRout(routDoc + forma.getNameDocument());
				}
				forma.setNameFile(rs.getString("nameFile"));
				result.add(forma);
			}
			rs.close();
			conn.close();
		} catch (Exception ex) {
			log.error(ex.getMessage());
			ex.printStackTrace();
		} finally {
			if(conn!=null) {
				try {
					conn.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return result;
	}

	public static String getHistoryComment(String idDocument) { //ydavila Ticket 001-00-003265 cambi� visibilidad Private a Public 
		// TODO Auto-generated method stub
		return null;
	}

	// SIMON 25 DE JULIO 2005 INICIO
	public static Collection getDocumentsPublished(String toSearch,
			Users usuario, Hashtable securityDocs) {
		Vector result = new Vector();

		try {
			String dateSystem = ToolsHTML.sdfShowConvert1.format(new Date());
			StringBuilder sql = new StringBuilder(2048)
					.append("SELECT d.numGen,d.number,d.nameDocument,d.prefix")
					.append(",d.type,d.normISO,d.owner,d.versionPublic,d.IdNode,vd.dateApproved");
			switch (Constants.MANEJADOR_ACTUAL) {
			case Constants.MANEJADOR_MSSQL:
				sql.append(",(p.apellidos + ' ' + p.nombres) AS Nombre,");
				break;
			case Constants.MANEJADOR_POSTGRES:
				sql.append(",(p.apellidos || ' ' || p.nombres) AS Nombre,");
				break;
			case Constants.MANEJADOR_MYSQL:
				sql.append(",CONCAT(p.apellidos , ' ' , p.nombres) AS Nombre,");
				break;
			}
			sql.append(
					"td.typeDoc, lower(td.typeDoc) AS tipoDoc,vd.numVer,vd.MayorVer,vd.MinorVer")
					.append(" FROM documents d, versiondoc vd,person p,typedocuments td")
					.append(" WHERE docPublic = 0 AND d.active = '1' AND d.versionPublic = vd.numVer")
					.append(" AND p.nameUser = d.owner AND td.idTypeDoc = d.type")
					.append(" AND vd.statu=")
					.append(HandlerDocuments.docApproved).append(" ")
					.append(" AND ").append("(").append("td.idTypeDoc<>")
					.append(HandlerDocuments.TypeDocumentsRegistro).append(" ")
					.append(")"); 
			if (!ToolsHTML.isEmptyOrNull(toSearch)) {
				sql.append(" AND d.nameDocument LIKE '%").append(toSearch)
						.append("%'");
			}
			
			sql.append(" ORDER BY lower(nameDocument),d.type,d.numgen");
			log.debug("[getDocumentsPublished] = " + sql.toString());
			Hashtable security = null;
			if (security == null) {
				StringBuffer idStructs = new StringBuffer("1");
				security = HandlerGrupo.getAllSecurityForGroup(
						usuario.getIdGroup(), idStructs);
				HandlerDBUser.getAllSecurityForUser(usuario.getIdPerson(),
						security, idStructs);
			}

			Vector datos = JDBCUtil.doQueryVector(sql.toString(),Thread.currentThread().getStackTrace()[1].getMethodName());
			for (int row = 0; row < datos.size(); row++) {
				Properties properties = (Properties) datos.elementAt(row);
				BaseDocumentForm forma = new BaseDocumentForm();
				forma.setIdDocument(properties.getProperty("numGen"));
				// ****************************************//
				// comprobamos la seguridad del documento
				PermissionUserForm securityDocsfrm = null;
				if (!ToolsHTML.isEmptyOrNull(forma.getIdDocument())) {
					securityDocsfrm = securityDocs.get(forma.getIdDocument()) != null ? (PermissionUserForm) securityDocs
							.get(forma.getIdDocument()) : null;
				}
				if (securityDocsfrm != null) {
					// carga toda la permisologia a la forma
					ToolsHTML.checkDocsSecurityLoad(securityDocsfrm, forma);
				}
				// ****************************************//
				// SIMON 25 DE JULIO 2005 INICIO
				forma.setIdNode(properties.getProperty("IdNode"));
				PermissionUserForm perm = (PermissionUserForm) security
						.get(forma.getIdNode());
				if (perm != null
						&& perm.getToImpresion() == Constants.permission) {
					forma.setImprimir("imprimir");
				} else {
					forma.setImprimir("");
				}
				// SIMON 25 DE JULIO 2005 FIN
				forma.setNumber(properties.getProperty("number"));
				forma.setNameDocument(properties.getProperty("nameDocument"));
				String prefixDoc = properties.getProperty("prefix");
				if (!ToolsHTML.isEmptyOrNull(prefixDoc)) {
					forma.setPrefix(prefixDoc.trim());
				} else {
					forma.setPrefix("");
				}
				forma.setTypeDocument(properties.getProperty("type"));
				forma.setDescriptTypeDoc(properties.getProperty("typeDoc"));
				forma.setOwner(properties.getProperty("Nombre"));
				forma.setMayorVer(properties.getProperty("MayorVer"));
				forma.setMinorVer(properties.getProperty("MinorVer"));
				String dateApprove = properties.getProperty("dateApproved");
				if (!ToolsHTML.isEmptyOrNull(dateApprove)) {
					forma.setDateApproved(ToolsHTML.formatDateShow(dateApprove,
							false));
				} else {
					forma.setDateApproved("");
				}
				forma.setNumVer(Integer.parseInt(properties
						.getProperty("numVer")));
				forma.setDocRelations(getDocumentsLinks(forma.getIdDocument()));
				result.add(forma);
			}
		} catch (Exception ex) {
			log.error(ex.getMessage());
			ex.printStackTrace();
		}
		return result;
	}

	// ***************************************************************************************************************
	/**
	 * 
	 * @param session
	 * @param usuario
	 * @param formulario
	 * @param idDocument
	 * @param newStruct
	 * @param nodeAnt
	 * @param user
	 * @param comments
	 * @param struct
	 * @return
	 * @throws ApplicationExceptionChecked
	 */
	public static synchronized boolean movementDocument(HttpSession session,
			Users usuario, MoveDocForm formulario, String idDocument,
			String newStruct, String nodeAnt, String user, String comments,
			Hashtable struct, boolean isEraserWhitoutCode)
			throws ApplicationExceptionChecked {

		boolean resp = false;
		Connection con = null;
		PreparedStatement st = null;
		String nuevoNumCorrelativo = null;
		String prefix = null;
		String numCorrelativExiste = null;
		// boolean corrGenSw = true;
		String numByLocation = null;
		String resetNumber = null;
		try {
			
			numByLocation = String.valueOf(HandlerParameters.PARAMETROS.getNumDocLoc());
			resetNumber = HandlerParameters.PARAMETROS.getResetDocNumber();

			// numByLocation =
			// HandlerParameters.PARAMETROS.getNumByLocation();
		} catch (Exception ex) {
			throw new ApplicationExceptionChecked("E0068");
		}

		/*
		 * Antes de cualquier cambio vamos a verificar que las localidades sean
		 * compatibles
		 */
		BaseStructForm localidadNew = HandlerStruct.getLocationFromNode(struct,
				newStruct);
		BaseStructForm localidadOld = HandlerStruct.getLocationFromNode(struct,
				nodeAnt);
		if (localidadNew.getMajorId() != localidadOld.getMajorId()
				|| localidadNew.getMinorId() != localidadOld.getMinorId()) {
			throw new ApplicationExceptionChecked("E0118");
		}

		try {
			log.debug("formulario.getTypeNumber(): "
					+ formulario.getTypeNumber());
			switch (formulario.getTypeNumber()) {
			case 0:
				// nuevoNumCorrelativo = formulario.getNumberDocAct();
				nuevoNumCorrelativo = EditDocumentAction.chkNumCorrelativo(
						newStruct, formulario.getNumberDocAct(), numByLocation,
						struct);
				if (!isEraserWhitoutCode && nuevoNumCorrelativo == null) {
					throw new ApplicationExceptionChecked("E0071");
				}
				// Chequear que el N�mero no exista en la Localidad Seg�n la
				// N�meraci�n Configurada
				// por el Usuario
				break;
			case 1:
				log.debug("Se genera el nuevo n�mero");
				String idLocation = HandlerStruct.getIdLocationToNode(struct,
						newStruct.trim());
				nuevoNumCorrelativo = EditDocumentAction.numCorrelativo(
						newStruct.trim(), idLocation, numByLocation,
						resetNumber, struct);
				break;
			case 2:
				nuevoNumCorrelativo = EditDocumentAction.chkNumCorrelativo(
						newStruct, formulario.getNumberDocNew(), numByLocation,
						struct);
				if (nuevoNumCorrelativo == null) {
					throw new ApplicationExceptionChecked("E0072");
				}
				break;
			}
			// si existe un numero con este query, quiere decir que hay que
			// asignarle un nuevo
			// numero al documento ya que
			// existe en esa localidad y hay un documento que ya tiene ese
			// numero en esa localidad.
			// StringBuilder buscarNumCorrExist = new StringBuilder(" FROM
			// documents WHERE idnode =
			// ");
			// buscarNumCorrExist.append(newStruct.trim()).append(" AND number
			// IN (SELECT number
			// FROM documents WHERE numgen = ").append(idDocument).append(")");
			// numCorrelativExiste =
			// HandlerBD.getField2("number",buscarNumCorrExist.toString());
			// formulario.setComments("");
			// if (!ToolsHTML.isEmptyOrNull(numCorrelativExiste)) {
			// obtenemos el numero correlativo a esa structura
			// nuevoNumCorrelativo =
			// EditDocumentAction.numCorrelativo(newStruct.trim(),newStruct.trim());
			// String numByLocation =
			// HandlerParameters.PARAMETROS.getNumByLocation()
			// String idLocation =
			// HandlerStruct.getIdLocationToNode(struct,newStruct.trim());
			// nuevoNumCorrelativo =
			// EditDocumentAction.numCorrelativo(newStruct.trim(),idLocation,numByLocation,struct);
			// el mensaje es enviado al usuario notificandole que el numero
			// correlativo ya existe en
			// la otra estructura
			// y se ha sustituido por este nuevo numero
			// formulario.setComments(rb.getString("mensage1correlativo")+ " " +
			// numCorrelativExiste
			// + rb.getString("mensage2correlativo")+" "+nuevoNumCorrelativo);
			// formulario.setComments(rb.getString("mensage2correlativo") + " "
			// +
			// nuevoNumCorrelativo);
			// }
		} catch (ApplicationExceptionChecked ae) {
			throw ae;
		} catch (Exception e) {
			log.error(e.getMessage());
			e.printStackTrace();
			throw new ApplicationExceptionChecked("E0068");
		}
		if (!isEraserWhitoutCode
				&& ToolsHTML.isEmptyOrNull(nuevoNumCorrelativo)) {
			throw new ApplicationExceptionChecked("E0071");
		}
		// StringBuilder history = new StringBuilder("INSERT INTO historystruct
		// (idHistory,idNode,idNodeFatherAnt,idNodeFatherNew,");
		// history.append("nameUser,dateChange,type) VALUES(?,?,?,?,?,?,?)");
		
		try {

			Vector securityGroup = (Vector) HandlerGrupo
					.getAllSecurityForGroupAndIdNode(newStruct);
			Vector securityUser = (Vector) HandlerDBUser
					.getAllSecurityForUsersAndIdNode(newStruct);

			// obtenemos el nuevo prefijo
			prefix = ToolsHTML.getPrefixToDoc(session, usuario,
					newStruct.trim());
			StringBuilder sql = new StringBuilder(
					"UPDATE documents SET idNode = ").append(newStruct.trim());
			if (!ToolsHTML.isEmptyOrNull(nuevoNumCorrelativo)) {
				sql.append(",number='").append(nuevoNumCorrelativo).append("'");
			}
			if (!ToolsHTML.isEmptyOrNull(prefix)) {
				sql.append(",prefix='").append(prefix).append("'");
			} else {
				sql.append(",prefix=NULL ");
			}

			sql.append(" WHERE numGen = ").append(idDocument);
			// Datos de la Versi�n
			int docID = Integer.parseInt(idDocument);
			String[] datosVersion = getFieldsToVersionMayor(new String[] {
					"MayorVer", "MinorVer" }, docID);
			con = JDBCUtil.getConnection(Thread.currentThread().getStackTrace()[1].getMethodName());
			con.setAutoCommit(false);
			log.debug("sql.toString() = " + sql.toString());
			st = con.prepareStatement(JDBCUtil.replaceCastMysql(sql.toString()));
			st.executeUpdate();
			updateHistoryDocs(con, docID, Integer.parseInt(nodeAnt),
					Integer.parseInt(newStruct), user,
					new Timestamp(new Date().getTime()),
					DataHistoryStructForm.move, comments, datosVersion);
			// Eliminando la Seguridad
			HandlerGrupo.deleteSecurityDoc(con, idDocument);
			
			/**
			 * No se actualizara la seguridad porque la seguridad que arrastra no es correcta, no se elimina 
			 * el codigo para su posterior analisis mas detallado si asi se requiere (JR) 24/06/2018
			 */
			boolean isUpdateSecurity = false;
			if(isUpdateSecurity) {
				// Actualizamos la Seguridad...
				PermissionUserForm permissionUserForm = null;
				for (int row = 0; row < securityGroup.size(); row++) {
					permissionUserForm = (PermissionUserForm) securityGroup
							.elementAt(row);
					permissionUserForm.setIdDocument(idDocument);
					updateSecurityDocumentGroup(permissionUserForm, false, con);
				}
				for (int row = 0; row < securityUser.size(); row++) {
					permissionUserForm = (PermissionUserForm) securityUser
							.elementAt(row);
					permissionUserForm.setIdDocument(idDocument);
					updateSecurityDocumentUser(permissionUserForm, false, con);
				}
			}

			con.commit();
			resp = true;
		} catch (Exception e) {
			log.error(e.getMessage());
			applyRollback(con);
			setMensaje(e.getMessage());
			e.printStackTrace();
			resp = false;
		} finally {
			setFinally(con, st);
		}
		return resp;
	}

	/**
	 * 
	 * @param isUser
	 * @param forma
	 * @throws Exception
	 */
	public static void loadSecurityDocumentAndId(boolean isUser,
			PermissionUserForm forma) throws Exception {
		StringBuilder sql = new StringBuilder(1024);
		if (isUser) {
			sql.append("SELECT * FROM permisiondocuser WHERE idDocument = ")
					.append(forma.getIdDocument()).append(" AND idUser = '")
					.append(forma.getIdUser()).append("'");
		} else {
			sql.append("SELECT * FROM permisiondocgroup WHERE idDocument = ")
					.append(forma.getIdDocument()).append(" AND idGroup = '")
					.append(forma.getIdGroup()).append("'");
		}

		log.debug("[loadSecurityDocumentAndId] = " + sql.toString());
		Properties prop = JDBCUtil.doQueryOneRow(sql.toString(),Thread.currentThread().getStackTrace()[1].getMethodName());

		if (prop.getProperty("isEmpty").equalsIgnoreCase("true")) {
			sql.setLength(0);
			if (isUser) {
				sql.append("SELECT pd.*, doc.numgen ")
						.append("FROM person p,permissionstructuser pd, documents doc ")
						.append("WHERE pd.idPerson = p.idPerson ")
						.append("AND pd.idstruct = doc.idNode ")
						.append("AND  p.idperson=").append(forma.getIdPerson())
						.append(" AND doc.numgen IN ('")
						.append(forma.getIdDocument().replaceAll(",", "','"))
						.append("')");

				prop = JDBCUtil.doQueryOneRow(sql.toString(),Thread.currentThread().getStackTrace()[1].getMethodName());
				if (prop.getProperty("isEmpty").equalsIgnoreCase("true")) {
					log.info("El usuario '"
							+ forma.getIdPerson()
							+ "' no tiene permisos sobre el documento, vemos su grupo");
					sql.setLength(0);
					sql.append(
							"SELECT distinct pg.*, d.numgen FROM permissionstructgroups pg,groupusers gu,documents d ")
							.append("WHERE gu.idGrupo = pg.idGroup ")
							.append("AND gu.accountActive = '1' ")
							.append("AND cast(pg.idGroup as int) = cast(")
							.append(forma.getIdGroup())
							.append(" as int) ")
							.append("AND d.idNode = pg.idstruct ")
							.append(" AND d.numgen IN ('")
							.append(forma.getIdDocument()
									.replaceAll(",", "','")).append("')");
				}
			} else {
				sql.append(
						"SELECT distinct pg.*, d.numgen FROM permissionstructgroups pg,groupusers gu,documents d ")
						.append("WHERE gu.idGrupo = pg.idGroup ")
						.append("AND gu.accountActive = '1' ")
						.append("AND cast(pg.idGroup as int) = cast(")
						.append(forma.getIdGroup()).append(" as int) ")
						.append("AND d.idNode = pg.idstruct ")
						.append(" AND d.numgen IN ('")
						.append(forma.getIdDocument().replaceAll(",", "','"))
						.append("')");
			}

			prop = JDBCUtil.doQueryOneRow(sql.toString(),Thread.currentThread().getStackTrace()[1].getMethodName());
		}

		if (prop.getProperty("isEmpty").equalsIgnoreCase("false")) {
			setDataSecurityDoc(forma, prop, null);
		} else {
			byte initValue = Constants.notPermission;
			forma.setToRead(initValue);
			forma.setToDelete(initValue);
			forma.setToEdit(initValue);
			forma.setToMove(initValue);
			forma.setToPrint(initValue);
			forma.setToReview(initValue);
			forma.setToAprove(initValue);
			forma.setCheckOut(initValue);
			forma.setToAdmon(initValue);
			forma.setToImpresion(initValue);
			forma.setToEditRegister(initValue);
			forma.setToMoveDocs(initValue);
			forma.setToViewDocs(initValue);
			forma.setPermisoModificado(0);
			forma.setToDownload(initValue);
			forma.setToCompleteFlow(initValue);
		}
	}

	/**
	 * 
	 * @param forma
	 * @return
	 * @throws Exception
	 */
	public static synchronized boolean deleteSecurityDocumentUser(
			PermissionUserForm forma) throws Exception {
		StringBuilder sql = new StringBuilder(1024)
				.append("DELETE FROM permisiondocuser WHERE idDocument = ")
				.append(forma.getIdDocument()).append(" AND idUser = '")
				.append(forma.getIdUser()).append("'");
		log.debug("[deleteSecurityDocumentUser] = " + sql.toString());
		return JDBCUtil.doUpdate(sql.toString()) > 0;
	}

	/**
	 * 
	 * @param forma
	 * @param chk
	 * @return
	 * @throws Exception
	 */
	public static synchronized boolean updateSecurityDocumentUser(
			PermissionUserForm forma, boolean chk) throws Exception {
		StringBuilder sql = new StringBuilder(1024);
		Properties prop = null;
		if (chk) {
			sql.append(
					"SELECT idDocument FROM permisiondocuser WHERE idDocument = ")
					.append(forma.getIdDocument()).append(" AND idUser = '")
					.append(forma.getIdUser()).append("'");
			prop = JDBCUtil.doQueryOneRow(sql.toString(),Thread.currentThread().getStackTrace()[1].getMethodName());
		}
		StringBuilder update = new StringBuilder(60);
		if (prop != null
				&& prop.getProperty("isEmpty").equalsIgnoreCase("false")) {
			update.append("UPDATE permisiondocuser SET toRead = '")
					.append(forma.getToRead())
					.append("' ")
					.append(",toDelete = '")
					.append(forma.getToDelDocs())
					.append("' ")
					// .getToDelete());
					.append(",toEdit = '").append(forma.getToEditDocs())
					.append("' ").append(",toMove = '")
					.append(forma.getToMove()).append("' ")
					.append(",toReview = '").append(forma.getToReview())
					.append("' ").append(",toAprove = '")
					.append(forma.getToAprove()).append("' ")
					.append(",toCheckOut = '").append(forma.getCheckOut())
					.append("' ").append(",toAdmon = '")
					.append(forma.getToAdmonDocs()).append("' ")
					.append(",toImpresion = '").append(forma.getToImpresion())
					.append("' ").append(",toEditRegister = '")
					.append(forma.getToEditRegister()).append("' ")
					.append(",toViewDocs = '").append(forma.getToViewDocs())
					.append("' ").append(",toMoveDocs = '")
					.append(forma.getToMoveDocs()).append("' ")
					.append(",toFlexFlow = '").append(forma.getToFlexFlow())
					.append("' ").append(",toChangeUsr = '")
					.append(forma.getToChangeUsr()).append("' ")
					.append(",toCompleteFlow = '")
					.append(forma.getToCompleteFlow()).append("' ")
					.append(",toPublicEraser = '")
					.append(forma.getToPublicEraser()).append("' ")
					.append(",toDownload = '").append(forma.getToDownload())
					.append("' ").append(" WHERE idDocument = ")
					.append(forma.getIdDocument()).append(" AND idUser = '")
					.append(forma.getIdUser()).append("'");
		} else {
			update.append(
					"INSERT INTO permisiondocuser (idDocument,idUser,toRead,toDelete,toEdit,toMove,toReview,toAprove,toCheckOut")
					.append(",toAdmon,toImpresion,toEditRegister,toViewDocs,toMoveDocs,toFlexFlow,toChangeUsr,toCompleteFlow,toPublicEraser,toDownload) VALUES(")
					.append(forma.getIdDocument())
					.append(",'")
					.append(forma.getIdUser())
					.append("','")
					// update.append(forma.getToRead()).append("','").append(forma.getToDelete()).append("','");
					.append(forma.getToRead()).append("','")
					.append(forma.getToDelDocs()).append("','")
					.append(forma.getToEditDocs()).append("','")
					.append(forma.getToMove()).append("','")
					.append(forma.getToReview()).append("','")
					.append(forma.getToAprove()).append("','")
					.append(forma.getCheckOut()).append("','")
					.append(forma.getToAdmonDocs()).append("','")
					.append(forma.getToImpresion()).append("','")
					.append(forma.getToEditRegister()).append("','")
					.append(forma.getToViewDocs()).append("','")
					.append(forma.getToMoveDocs()).append("','")
					.append(forma.getToFlexFlow()).append("','")
					.append(forma.getToChangeUsr()).append("','")
					.append(forma.getToCompleteFlow()).append("','")
					.append(forma.getToPublicEraser()).append("','")
					.append(forma.getToDownload()).append("')");
		}
		log.debug("HandlerDBUser.updateSecurityDocumentUser "
				+ update.toString());
		return JDBCUtil.doUpdate(update.toString()) > 0;
	}

	/**
	 * 
	 * @param forma
	 * @param chk
	 * @return
	 * @throws Exception
	 */
	public static synchronized boolean updateSecurityRecordUser(
			PermissionUserForm forma, boolean chk) throws Exception {
		StringBuilder sql = new StringBuilder(1024);
		Properties prop = null;
		if (chk) {
			sql.append("SELECT idUser FROM permissionrecorduser ")
					.append("WHERE idUser = '").append(forma.getIdUser())
					.append("'");
			prop = JDBCUtil.doQueryOneRow(sql.toString(),Thread.currentThread().getStackTrace()[1].getMethodName());
		}

		sql.setLength(0);
		if (prop != null
				&& prop.getProperty("isEmpty").equalsIgnoreCase("false")) {
			sql.append("UPDATE permissionrecorduser SET ")
					.append(" toGenerate = '").append(forma.getToGenerate())
					.append("' ").append(",toUpdate = '")
					.append(forma.getToUpdate()).append("' ")
					.append(",toSend = '").append(forma.getToSend())
					.append("' ").append(",toExport = '")
					.append(forma.getToExport()).append("' ")
					.append(",toPrint = '").append(forma.getToPrint())
					.append("' ").append("WHERE idUser = '")
					.append(forma.getIdUser()).append("'");
		} else {
			sql.append(
					"INSERT INTO permissionrecorduser (idUser,toGenerate,toUpdate,toSend,toExport,toPrint) VALUES(")
					.append(" '").append(forma.getIdUser()).append("' ")
					.append(",'").append(forma.getToGenerate()).append("' ")
					.append(",'").append(forma.getToUpdate()).append("' ")
					.append(",'").append(forma.getToSend()).append("' ")
					.append(",'").append(forma.getToExport()).append("' ")
					.append(",'").append(forma.getToPrint()).append("' ")
					.append(")");
		}
		log.debug("HandlerDBUser.updateSecurityRecordUser " + sql.toString());
		return JDBCUtil.doUpdate(sql.toString()) > 0;
	}

	/**
	 * 
	 * @param forma
	 * @param chk
	 * @return
	 * @throws Exception
	 */
	public static synchronized boolean updateSecurityFilesUser(
			PermissionUserForm forma, boolean chk) throws Exception {
		StringBuilder sql = new StringBuilder(60);
		Properties prop = null;
		if (chk) {
			sql.append("SELECT idUser FROM permissionfilesuser ");
			sql.append("WHERE idUser = '").append(forma.getIdUser())
					.append("'");
			prop = JDBCUtil.doQueryOneRow(sql.toString(),Thread.currentThread().getStackTrace()[1].getMethodName());
		}
		StringBuilder update = new StringBuilder(60);
		if (prop != null
				&& prop.getProperty("isEmpty").equalsIgnoreCase("false")) {
			update.append("UPDATE permissionfilesuser SET ");
			update.append("  toFilesSecurity = '")
					.append(forma.getToFilesSecurity()).append("' ");
			update.append(", toFilesCreate = '")
					.append(forma.getToFilesCreate()).append("' ");
			update.append(", toFilesExport = '")
					.append(forma.getToFilesExport()).append("' ");
			update.append(", toFilesEdit = '").append(forma.getToFilesEdit())
					.append("' ");
			update.append(", toFilesDelete = '")
					.append(forma.getToFilesDelete()).append("' ");
			update.append(", toFilesRelated = '")
					.append(forma.getToFilesRelated()).append("' ");
			update.append(", toFilesVersion = '")
					.append(forma.getToFilesVersion()).append("' ");
			update.append(", toFilesView = '").append(forma.getToFilesView())
					.append("' ");
			update.append(", toFilesPrint = '").append(forma.getToFilesPrint())
					.append("' ");
			update.append(", toFilesHistory = '")
					.append(forma.getToFilesHistory()).append("' ");
			update.append(", toFilesDownload = '")
					.append(forma.getToFilesDownload()).append("' ");
			update.append(", toFilesSave = '").append(forma.getToFilesSave())
					.append("' ");
			update.append("WHERE idUser = '").append(forma.getIdUser())
					.append("'");
		} else {
			update.append("INSERT INTO permissionfilesuser (idUser,toFilesSecurity,toFilesCreate,toFilesExport,toFilesEdit,toFilesDelete,toFilesRelated,toFilesVersion,toFilesView,toFilesPrint,toFilesHistory,toFilesDownload,toFilesSave) VALUES(");
			update.append(" '").append(forma.getIdUser()).append("' ");
			update.append(",'").append(forma.getToFilesSecurity()).append("' ");
			update.append(",'").append(forma.getToFilesCreate()).append("' ");
			update.append(",'").append(forma.getToFilesExport()).append("' ");
			update.append(",'").append(forma.getToFilesEdit()).append("' ");
			update.append(",'").append(forma.getToFilesDelete()).append("' ");
			update.append(",'").append(forma.getToFilesRelated()).append("' ");
			update.append(",'").append(forma.getToFilesVersion()).append("' ");
			update.append(",'").append(forma.getToFilesView()).append("' ");
			update.append(",'").append(forma.getToFilesPrint()).append("' ");
			update.append(",'").append(forma.getToFilesHistory()).append("' ");
			update.append(",'").append(forma.getToFilesDownload()).append("' ");
			update.append(",'").append(forma.getToFilesSave()).append("' ");
			update.append(")");
		}
		log.debug("HandlerDBUser.updateSecurityFilesUser " + update.toString());
		return JDBCUtil.doUpdate(update.toString()) > 0;
	}

	public static synchronized boolean updateSecurityRecordGroup(
			PermissionUserForm forma, boolean chk) throws Exception {
		StringBuilder sql = new StringBuilder(60);
		Properties prop = null;
		if (chk) {
			sql.append("SELECT idGroup FROM permissionrecordgroup ");
			sql.append("WHERE idGroup = '").append(forma.getIdGroup())
					.append("'");
			prop = JDBCUtil.doQueryOneRow(sql.toString(),Thread.currentThread().getStackTrace()[1].getMethodName());
		}
		StringBuilder update = new StringBuilder(60);
		if (prop != null
				&& prop.getProperty("isEmpty").equalsIgnoreCase("false")) {
			update.append("UPDATE permissionrecordgroup SET ");
			update.append(" toGenerate = '").append(forma.getToGenerate())
					.append("' ");
			update.append(",toUpdate = '").append(forma.getToUpdate())
					.append("' ");
			update.append(",toSend = '").append(forma.getToSend()).append("' ");
			update.append(",toExport = '").append(forma.getToExport())
					.append("' ");
			update.append(",toPrint = '").append(forma.getToPrint())
					.append("' ");
			update.append("WHERE idGroup = '").append(forma.getIdGroup())
					.append("'");
		} else {
			update.append("INSERT INTO permissionrecordgroup (idGroup,toGenerate,toUpdate,toSend,toExport,toPrint) VALUES(");
			update.append(" '").append(forma.getIdGroup()).append("' ");
			update.append(",'").append(forma.getToGenerate()).append("' ");
			update.append(",'").append(forma.getToUpdate()).append("' ");
			update.append(",'").append(forma.getToSend()).append("' ");
			update.append(",'").append(forma.getToExport()).append("' ");
			update.append(",'").append(forma.getToPrint()).append("' ");
			update.append(")");
		}
		log.debug("HandlerDBUser.updateSecurityRecordGroup "
				+ update.toString());
		return JDBCUtil.doUpdate(update.toString()) > 0;
	}

	public static synchronized boolean updateSecurityFilesGroup(
			PermissionUserForm forma, boolean chk) throws Exception {
		StringBuilder sql = new StringBuilder(60);
		Properties prop = null;
		if (chk) {
			sql.append("SELECT idGroup FROM permissionfilesgroup ");
			sql.append("WHERE idGroup = '").append(forma.getIdGroup())
					.append("'");
			prop = JDBCUtil.doQueryOneRow(sql.toString(),Thread.currentThread().getStackTrace()[1].getMethodName());
		}
		StringBuilder update = new StringBuilder(60);
		if (prop != null
				&& prop.getProperty("isEmpty").equalsIgnoreCase("false")) {
			update.append("UPDATE permissionfilesgroup SET ");
			update.append("  toFilesSecurity = '")
					.append(forma.getToFilesSecurity()).append("' ");
			update.append(", toFilesCreate = '")
					.append(forma.getToFilesCreate()).append("' ");
			update.append(", toFilesExport = '")
					.append(forma.getToFilesExport()).append("' ");
			update.append(", toFilesEdit = '").append(forma.getToFilesEdit())
					.append("' ");
			update.append(", toFilesDelete = '")
					.append(forma.getToFilesDelete()).append("' ");
			update.append(", toFilesRelated = '")
					.append(forma.getToFilesRelated()).append("' ");
			update.append(", toFilesVersion = '")
					.append(forma.getToFilesVersion()).append("' ");
			update.append(", toFilesView = '").append(forma.getToFilesView())
					.append("' ");
			update.append(", toFilesPrint = '").append(forma.getToFilesPrint())
					.append("' ");
			update.append(", toFilesHistory = '")
					.append(forma.getToFilesHistory()).append("' ");
			update.append(", toFilesDownload = '")
					.append(forma.getToFilesDownload()).append("' ");
			update.append(", toFilesSave = '").append(forma.getToFilesSave())
					.append("' ");
			update.append("WHERE idGroup = '").append(forma.getIdGroup())
					.append("'");
		} else {
			update.append("INSERT INTO permissionfilesgroup (idGroup,toFilesSecurity,toFilesCreate,toFilesExport,toFilesEdit,toFilesDelete,toFilesRelated,toFilesVersion,toFilesView,toFilesPrint,toFilesHistory,toFilesDownload,toFilesSave) VALUES(");
			update.append(" '").append(forma.getIdGroup()).append("' ");
			update.append(",'").append(forma.getToFilesSecurity()).append("' ");
			update.append(",'").append(forma.getToFilesCreate()).append("' ");
			update.append(",'").append(forma.getToFilesExport()).append("' ");
			update.append(",'").append(forma.getToFilesEdit()).append("' ");
			update.append(",'").append(forma.getToFilesDelete()).append("' ");
			update.append(",'").append(forma.getToFilesRelated()).append("' ");
			update.append(",'").append(forma.getToFilesVersion()).append("' ");
			update.append(",'").append(forma.getToFilesView()).append("' ");
			update.append(",'").append(forma.getToFilesPrint()).append("' ");
			update.append(",'").append(forma.getToFilesHistory()).append("' ");
			update.append(",'").append(forma.getToFilesDownload()).append("' ");
			update.append(",'").append(forma.getToFilesSave()).append("' ");
			update.append(")");
		}
		log.debug("HandlerDBUser.updateSecurityFilesGroup " + update.toString());
		return JDBCUtil.doUpdate(update.toString()) > 0;
	}

	public static synchronized void updateSecurityDocumentUser(
			PermissionUserForm forma, boolean chk, Connection con)
			throws Exception {
		StringBuilder sql = new StringBuilder(60);
		Properties prop = null;
		if (chk) {
			sql.append(
					"SELECT idDocument FROM permisiondocuser WHERE idDocument = ")
					.append(forma.getIdDocument());
			sql.append(" AND idUser = '").append(forma.getIdUser()).append("'");
			prop = JDBCUtil.doQueryOneRow(sql.toString(),Thread.currentThread().getStackTrace()[1].getMethodName());
		}
		StringBuilder update = new StringBuilder(60);
		if (prop != null
				&& prop.getProperty("isEmpty").equalsIgnoreCase("false")) {
			update.append("UPDATE permisiondocuser SET toRead = '")
					.append(forma.getToRead()).append("'");
			update.append(",toDelete = '").append(forma.getToDelDocs())
					.append("'");// .getToDelete());
			update.append(",toEdit = '").append(forma.getToEdit()).append("'");
			update.append(",toMove = '").append(forma.getToMove()).append("'");
			update.append(",toReview = '").append(forma.getToReview())
					.append("'");
			update.append(",toAprove = '").append(forma.getToAprove())
					.append("'");
			update.append(",toCheckOut = '").append(forma.getCheckOut())
					.append("'");
			update.append(",toAdmon = '").append(forma.getToAdmon())
					.append("'");
			update.append(",toImpresion = '").append(forma.getToImpresion())
					.append("'");
			update.append(",toEditRegister = '")
					.append(forma.getToEditRegister()).append("'");
			update.append(",toViewDocs = '").append(forma.getToViewDocs())
					.append("'");
			update.append(",toMoveDocs = '").append(forma.getToMoveDocs())
					.append("'");
			update.append(",toDownload = '").append(forma.getToDownload())
					.append("'");
			update.append(" WHERE idDocument = ").append(forma.getIdDocument());
			update.append(" AND idUser = '").append(forma.getIdUser())
					.append("'");
		} else {
			update.append("INSERT INTO permisiondocuser (idDocument,idUser,toRead,toDelete,toEdit,toMove,toReview,toAprove,toCheckOut,toAdmon,toImpresion,toEditRegister,toViewDocs,toMoveDocs,toDownload) VALUES(");
			update.append(forma.getIdDocument()).append(",'")
					.append(forma.getIdUser()).append("','");
			// update.append(forma.getToRead()).append("','").append(forma.getToDelete()).append("','");
			update.append(forma.getToRead()).append("','")
					.append(forma.getToDelDocs()).append("','");
			update.append(forma.getToEdit()).append("','")
					.append(forma.getToMove()).append("','");
			update.append(forma.getToReview()).append("','")
					.append(forma.getToAprove()).append("','");
			update.append(forma.getCheckOut()).append("','")
					.append(forma.getToAdmon()).append("','");
			update.append(forma.getToImpresion()).append("','")
					.append(forma.getToEditRegister()).append("','");
			update.append(forma.getToViewDocs()).append("','")
					.append(forma.getToMoveDocs()).append("','")
					.append(forma.getToDownload());
			update.append("')");
		}
		log.debug("HandlerDBUser.updateSecurityDocumentUser "
				+ update.toString());
		PreparedStatement st = con.prepareStatement(JDBCUtil.replaceCastMysql(update.toString()));
		st.executeUpdate();
	}

	public static synchronized boolean deleteSecurityDocumentGroup(
			PermissionUserForm forma) throws Exception {
		/*
		 * StringBuilder sql = new StringBuilder(60); sql.append("SELECT
		 * idDocument FROM permisiondocgroup WHERE idDocument =
		 * ").append(forma.getIdDocument()); sql.append(" AND idGroup =
		 * '").append(forma.getIdGroup()).append("'"); Properties prop =
		 * JDBCUtil.doQueryOneRow(sql.toString(),Thread.currentThread().getStackTrace()[1].getMethodName());
		 */
		StringBuilder update = new StringBuilder(60);
		/* if (prop.getProperty("isEmpty").equalsIgnoreCase("false")) { */
		update.append("DELETE FROM permisiondocgroup ");
		update.append(" WHERE idDocument = ").append(forma.getIdDocument());
		update.append(" AND idGroup = '").append(forma.getIdGroup())
				.append("'");
		// }
		log.debug("HandlerDBUser.deleteSecurityDocumentGroup "
				+ update.toString());
		return JDBCUtil.doUpdate(update.toString()) > 0;
	}

	public static synchronized boolean updateSecurityDocumentGroup(
			PermissionUserForm forma, boolean chq) throws Exception {
		StringBuilder sql = new StringBuilder(60);
		Properties prop = null;
		if (chq) {
			sql.append(
					"SELECT idDocument FROM permisiondocgroup WHERE idDocument = ")
					.append(forma.getIdDocument());
			sql.append(" AND idGroup = '").append(forma.getIdGroup())
					.append("'");
			prop = JDBCUtil.doQueryOneRow(sql.toString(),Thread.currentThread().getStackTrace()[1].getMethodName());
		}
		StringBuilder update = new StringBuilder(60);
		if (prop != null
				&& prop.getProperty("isEmpty").equalsIgnoreCase("false")) {
			update.append("UPDATE permisiondocgroup SET toRead = '")
					.append(forma.getToRead()).append("' ");
			update.append(",toDelete = '").append(forma.getToDelDocs())
					.append("' ");// .getToDelete());
			update.append(",toEdit = '").append(forma.getToEditDocs())
					.append("' ");
			update.append(",toMove = '").append(forma.getToMove()).append("' ");
			update.append(",toReview = '").append(forma.getToReview())
					.append("' ");
			update.append(",toAprove = '").append(forma.getToAprove())
					.append("' ");
			update.append(",toCheckOut = '").append(forma.getCheckOut())
					.append("' ");
			update.append(",toAdmon = '").append(forma.getToAdmonDocs())
					.append("' ");
			update.append(",toImpresion = '").append(forma.getToImpresion())
					.append("' ");
			update.append(",toEditRegister = '")
					.append(forma.getToEditRegister()).append("' ");
			update.append(",toViewDocs = '").append(forma.getToViewDocs())
					.append("' ");
			update.append(",toMoveDocs = '").append(forma.getToMoveDocs())
					.append("' ");
			update.append(",toFlexFlow = '").append(forma.getToFlexFlow())
					.append("' ");
			update.append(",toChangeUsr = '").append(forma.getToChangeUsr())
					.append("' ");
			update.append(",toCompleteFlow = '")
					.append(forma.getToCompleteFlow()).append("' ");
			update.append(",toPublicEraser = '")
					.append(forma.getToPublicEraser()).append("' ");
			update.append(",toDownload = '").append(forma.getToDownload())
					.append("' ");
			update.append(" WHERE idDocument = ").append(forma.getIdDocument());
			update.append(" AND idGroup = '").append(forma.getIdGroup())
					.append("'");
		} else {
			update.append("INSERT INTO permisiondocgroup (idDocument,idGroup,toRead,toDelete,toEdit,toMove,toReview,toAprove,toCheckOut");
			update.append(",toAdmon,toImpresion,toEditRegister,toViewDocs,toMoveDocs,toFlexFlow,toChangeUsr,toCompleteFlow,toPublicEraser,toDownload) VALUES(");
			update.append(forma.getIdDocument()).append(",'");
			update.append(forma.getIdGroup()).append("','");
			update.append(forma.getToRead()).append("','");
			update.append(forma.getToDelDocs()).append("','");
			update.append(forma.getToEditDocs()).append("','");
			update.append(forma.getToMove()).append("','");
			update.append(forma.getToReview()).append("','");
			update.append(forma.getToAprove()).append("','");
			update.append(forma.getCheckOut()).append("','");
			update.append(forma.getToAdmonDocs()).append("','");
			update.append(forma.getToImpresion()).append("','");
			update.append(forma.getToEditRegister()).append("','");
			update.append(forma.getToViewDocs()).append("','");
			update.append(forma.getToMoveDocs()).append("','");
			update.append(forma.getToFlexFlow()).append("','");
			update.append(forma.getToChangeUsr()).append("','");
			update.append(forma.getToCompleteFlow()).append("','");
			update.append(forma.getToPublicEraser()).append("','");
			update.append(forma.getToDownload());
			update.append("')");
		}
		log.debug("HandlerDBUser.updateSecurityDocumentGroup "
				+ update.toString());
		return JDBCUtil.doUpdate(update.toString()) > 0;
	}

	/*
	 * public static synchronized boolean
	 * updateSecurityDocumentGroup(PermissionUserForm forma, boolean chq) throws
	 * Exception { StringBuilder sql = new StringBuilder(60); Properties prop =
	 * null; if (chq) {
	 * sql.append("SELECT idDocument FROM permisiondocgroup WHERE idDocument = "
	 * ).append(forma.getIdDocument());
	 * sql.append(" AND idGroup = '").append(forma.getIdGroup()).append("'");
	 * prop = JDBCUtil.doQueryOneRow(sql.toString(),Thread.currentThread().getStackTrace()[1].getMethodName()); } StringBuilder update =
	 * new StringBuilder(60); if (prop != null &&
	 * prop.getProperty("isEmpty").equalsIgnoreCase("false")) {
	 * update.append("UPDATE permisiondocgroup SET toRead = '"
	 * ).append(forma.getToRead()).append("'");
	 * update.append(",toDelete = '").append(forma.getToDelete()).append("'");
	 * update.append(",toEdit = '").append(forma.getToEdit()).append("'");
	 * update.append(",toMove = '").append(forma.getToMove()).append("'");
	 * update.append(",toReview = '").append(forma.getToReview()).append("'");
	 * update.append(",toAprove = '").append(forma.getToAprove()).append("'");
	 * update.append(",toCheckOut = '").append(forma.getCheckOut()).append("'");
	 * update.append(",toAdmon = '").append(forma.getToAdmon()).append("'"); //
	 * 30 agosto 2005 inicio
	 * update.append(",toImpresion = '").append(forma.getToImpresion
	 * ()).append("'");
	 * update.append(",toEditRegister = '").append(forma.getToEditRegister
	 * ()).append("'");
	 * 
	 * update.append(",toViewDocs = '").append(forma.getToViewDocs()).append("'")
	 * ;
	 * update.append(",toMoveDocs = '").append(forma.getToMoveDocs()).append("'"
	 * );
	 * update.append(",toDownload = '").append(forma.getToDownload()).append("'"
	 * ); // 30 agosto 2005 fin
	 * update.append(" WHERE idDocument = ").append(forma.getIdDocument());
	 * update.append(" AND idGroup = '").append(forma.getIdGroup()).append("'");
	 * } else { update.append(
	 * "INSERT INTO permisiondocgroup (idDocument,idGroup,toRead,toDelete,toEdit,toMove,toReview,toAprove,toCheckOut,toAdmon,toImpresion,toEditRegister,toViewDocs,toMoveDocs,toDownload) VALUES("
	 * ); update.append(forma.getIdDocument()).append(",'");
	 * update.append(forma.getIdGroup()).append("','");
	 * update.append(forma.getToRead()).append("','");
	 * update.append(forma.getToDelete()).append("','");
	 * update.append(forma.getToEdit()).append("','");
	 * update.append(forma.getToMove()).append("','");
	 * update.append(forma.getToReview()).append("','");
	 * update.append(forma.getToAprove()).append("','");
	 * update.append(forma.getCheckOut()).append("','");
	 * update.append(forma.getToAdmon()).append("','");
	 * update.append(forma.getToImpresion()).append("','");
	 * update.append(forma.getToEditRegister()).append("','");
	 * update.append(forma.getToViewDocs()).append("','");
	 * update.append(forma.getToMoveDocs()).append("','");
	 * update.append(forma.getToDownload()); update.append("')"); }
	 * log.debug("HandlerDBUser.updateSecurityDocumentGroup " +
	 * update.toString()); return JDBCUtil.doUpdate(update.toString()) > 0; }
	 */

	public static synchronized void updateSecurityDocumentGroup(
			PermissionUserForm forma, boolean chq, Connection con)
			throws Exception {
		StringBuilder sql = new StringBuilder(60);
		Properties prop = null;
		if (chq) {
			sql.append(
					"SELECT idDocument FROM permisiondocgroup WHERE idDocument = ")
					.append(forma.getIdDocument());
			sql.append(" AND idGroup = '").append(forma.getIdGroup())
					.append("'");
			prop = JDBCUtil.doQueryOneRow(sql.toString(),Thread.currentThread().getStackTrace()[1].getMethodName());
		}
		StringBuilder update = new StringBuilder(60);
		if (prop != null
				&& prop.getProperty("isEmpty").equalsIgnoreCase("false")) {
			update.append("UPDATE permisiondocgroup SET toRead = ").append(
					forma.getToRead());
			update.append(",toDelete = '").append(forma.getToDelete())
					.append("'");
			update.append(",toEdit = '").append(forma.getToEdit()).append("'");
			update.append(",toMove = '").append(forma.getToMove()).append("'");
			update.append(",toReview = '").append(forma.getToReview())
					.append("'");
			update.append(",toAprove = '").append(forma.getToAprove())
					.append("'");
			update.append(",toCheckOut = '").append(forma.getCheckOut())
					.append("'");
			update.append(",toAdmon = '").append(forma.getToAdmon())
					.append("'");
			// 30 agosto 2005 inicio
			update.append(",toImpresion = '").append(forma.getToImpresion())
					.append("'");
			update.append(",toEditRegister = '")
					.append(forma.getToEditRegister()).append("'");

			update.append(",toViewDocs = '").append(forma.getToViewDocs())
					.append("'");
			update.append(",toMoveDocs = '").append(forma.getToMoveDocs())
					.append("'");
			update.append(",toDownload = '").append(forma.getToDownload())
					.append("'");
			// 30 agosto 2005 fin
			update.append(" WHERE idDocument = ").append(forma.getIdDocument());
			update.append(" AND idGroup = '").append(forma.getIdGroup())
					.append("'");
		} else {
			update.append("INSERT INTO permisiondocgroup (idDocument,idGroup,toRead,toDelete,toEdit,toMove,toReview,toAprove,toCheckOut,toAdmon,toImpresion,toEditRegister,toViewDocs,toMoveDocs,toDownload) VALUES(");
			update.append(forma.getIdDocument()).append(",'")
					.append(forma.getIdGroup()).append("','");
			update.append(forma.getToRead()).append("','")
					.append(forma.getToDelete()).append("','");
			update.append(forma.getToEdit()).append("','")
					.append(forma.getToMove()).append("','");
			update.append(forma.getToReview()).append("','")
					.append(forma.getToAprove()).append("','");
			update.append(forma.getCheckOut()).append("','")
					.append(forma.getToAdmon()).append("','");
			update.append(forma.getToImpresion()).append("','")
					.append(forma.getToEditRegister()).append("','");
			update.append(forma.getToViewDocs()).append("','")
					.append(forma.getToMoveDocs()).append("','");
			update.append(forma.getToDownload()).append("'");
			update.append(")");
		}
		log.debug("HandlerDBUser.updateSecurityDocumentGroup "
				+ update.toString());
		PreparedStatement st = con.prepareStatement(JDBCUtil.replaceCastMysql(update.toString()));
		st.executeUpdate();
	}

	// hereda en el documento los permisos del usuario y del grupo al que
	// pertenece el usuario
	public synchronized static void permisosHeredadosDocs(
			BaseDocumentForm forma, int numeGen, Connection con)
			throws SQLException {
		PreparedStatement ins = null;
		StringBuilder select = new StringBuilder(
				"SELECT * FROM permissionstructgroups WHERE idStruct=")
				.append(forma.getIdNode());
		log.debug("[permisosHeredadosDocs]groups=" + select.toString());
		PreparedStatement st = con.prepareStatement(JDBCUtil.replaceCastMysql(select.toString()));
		ResultSet rs = st.executeQuery();

		StringBuilder update = new StringBuilder("");
		while (rs.next()) {
			update.setLength(0);
			update.append(
					"INSERT INTO permisiondocgroup (idDocument,idGroup,toRead,toDelete,toEdit,toMove,toReview,toAprove,toCheckOut,toAdmon,toImpresion,toEditRegister,toViewDocs,toMoveDocs,toDownload) VALUES(")
					.append(numeGen).append(",'")
					.append(rs.getString("idGroup")).append("','")
					.append(rs.getString("toRead")).append("','")
					.append(rs.getString("toDelete")).append("','")
					.append(rs.getString("toEdit")).append("','")
					.append(rs.getString("toMove")).append("','")
					.append(rs.getString("toReview")).append("','")
					.append(rs.getString("toApproved")).append("','")
					.append(rs.getString("toCheckOut")).append("','")
					.append(rs.getString("toAdmon")).append("','")
					.append(rs.getString("toImpresion")).append("','")
					.append(rs.getString("toEditRegister")).append("','")
					.append(rs.getString("toViewDocs")).append("','")
					.append(rs.getString("toMoveDocs")).append("','")
					.append(rs.getString("toDownload")).append("'").append(")");
			ins = con.prepareStatement(JDBCUtil.replaceCastMysql(update.toString()));
			ins.executeUpdate();
		}
		rs.close();

		StringBuilder select1 = new StringBuilder(1024)
				.append("SELECT *,p.nameuser FROM permissionstructuser psu,person p  WHERE psu.idperson=p.idperson   AND idStruct=")
				.append(forma.getIdNode());
		log.debug("[permisosHeredadosDocs]selectUser=" + select1.toString());

		st = con.prepareStatement(JDBCUtil.replaceCastMysql(select1.toString()));
		ResultSet rs2 = st.executeQuery();
		ResultSet rsChequeo;

		StringBuilder update1 = new StringBuilder("");
		StringBuilder validaSql = new StringBuilder("");
		while (rs2.next()) {
			validaSql.setLength(0);
			validaSql
					.append("SELECT idDocument FROM permisiondocuser WHERE idDocument=")
					.append(numeGen).append(" AND idUser='")
					.append(rs2.getString("nameuser")).append("'");
			st = con.prepareStatement(JDBCUtil.replaceCastMysql(validaSql.toString()));
			rsChequeo = st.executeQuery();

			update1.setLength(0);
			if (rsChequeo.next()) {
				update1.append("UPDATE permisiondocuser SET toRead='")
						.append(rs2.getString("toRead")).append("',")
						.append("toDelete='").append(rs2.getString("toDelete"))
						.append("',").append("toEdit='")
						.append(rs2.getString("toEdit")).append("',")
						.append("toMove='").append(rs2.getString("toMove"))
						.append("',").append("toReview='")
						.append(rs2.getString("toReview")).append("',")
						.append("toAprove='")
						.append(rs2.getString("toApproved")).append("',")
						.append("toCheckOut='")
						.append(rs2.getString("toCheckOut")).append("',")
						.append("toAdmon='").append(rs2.getString("toAdmon"))
						.append("',").append("toImpresion='")
						.append(rs2.getString("toImpresion")).append("',")
						.append("toEditRegister='")
						.append(rs2.getString("toEditRegister")).append("',")
						.append("toViewDocs='")
						.append(rs2.getString("toViewDocs")).append("',")
						.append("toMoveDocs='")
						.append(rs2.getString("toMoveDocs")).append("',")
						.append("toFlexFlow='")
						.append(rs2.getString("toFlexFlow")).append("',")
						.append("toChangeUsr='")
						.append(rs2.getString("toChangeUsr")).append("',")
						.append("toCompleteFlow='")
						.append(rs2.getString("toCompleteFlow")).append("',")
						.append("toPublicEraser='")
						.append(rs2.getString("toPublicEraser")).append("'")
						.append("toDownload='")
						.append(rs2.getString("toDownload")).append("'")
						.append(" WHERE idDocument=").append(numeGen)
						.append(" AND idUser='")
						.append(rs2.getString("nameuser")).append("'");
			} else {
				update1.append(
						"INSERT INTO permisiondocuser (idDocument,idUser,toRead,toDelete,toEdit,toMove,toReview,toAprove,toCheckOut,")
						.append("toAdmon,toImpresion,toEditRegister,toViewDocs,toMoveDocs,toFlexFlow,toChangeUsr,toCompleteFlow,toPublicEraser,toDownload) VALUES(")
						.append(numeGen).append(",'")
						.append(rs2.getString("nameuser")).append("','")
						.append(rs2.getString("toRead")).append("','")
						.append(rs2.getString("toDelete")).append("','")
						.append(rs2.getString("toEdit")).append("','")
						.append(rs2.getString("toMove")).append("','")
						.append(rs2.getString("toReview")).append("','")
						.append(rs2.getString("toApproved")).append("','")
						.append(rs2.getString("toCheckOut")).append("','")
						.append(rs2.getString("toAdmon")).append("','")
						.append(rs2.getString("toImpresion")).append("','")
						.append(rs2.getString("toEditRegister")).append("','")
						.append(rs2.getString("toViewDocs")).append("','")
						.append(rs2.getString("toMoveDocs")).append("','")
						.append(rs2.getString("toFlexFlow")).append("','")
						.append(rs2.getString("toChangeUsr")).append("','")
						.append(rs2.getString("toCompleteFlow")).append("','")
						.append(rs2.getString("toPublicEraser")).append("','")
						.append(rs2.getString("toDownload")).append("'")
						.append(")");
			}
			rsChequeo.close();
			ins = con.prepareStatement(JDBCUtil.replaceCastMysql(update1.toString()));
			ins.executeUpdate();
		}
		rs2.close();
	}

	/**
	 * 
	 * @param idGroup
	 * @param userName
	 * @return
	 * @throws Exception
	 */
	public static Hashtable getAllSecurity(String idGroup, String userName)
			throws Exception {
		Hashtable result = new Hashtable();
		StringBuilder sql = new StringBuilder(1024)
				.append("SELECT * FROM permisiondocgroup WHERE idGroup = '")
				.append(idGroup).append("' ");
		Vector datos = JDBCUtil.doQueryVector(sql.toString(),Thread.currentThread().getStackTrace()[1].getMethodName());
		PermissionUserForm forma = null;
		for (int row = 0; row < datos.size(); row++) {
			Properties properties = (Properties) datos.elementAt(row);
			forma = new PermissionUserForm();
			setDataSecurityDoc(forma, properties, null);
			forma.setIdDocument(properties.getProperty("idDocument"));
			result.put(forma.getIdDocument(), forma);
		}

		sql.setLength(0);
		sql.append("SELECT * FROM permisiondocuser WHERE idUser = '")
				.append(userName).append("'");
		datos = JDBCUtil.doQueryVector(sql.toString(),Thread.currentThread().getStackTrace()[1].getMethodName());
		for (int row = 0; row < datos.size(); row++) {
			Properties properties = (Properties) datos.elementAt(row);
			forma = new PermissionUserForm();
			setDataSecurityDoc(forma, properties, null);
			forma.setIdDocument(properties.getProperty("idDocument"));
			result.put(forma.getIdDocument(), forma);
		}
		return result;
	}

	/**
	 * 
	 * @param idUser
	 * @param idStruct
	 * @param isUser
	 * @param result
	 * @throws Exception
	 */
	public static void getAllSecurityForDocAndIdStruct(String idUser,
			String idStruct, boolean isUser, Hashtable result) throws Exception {
		StringBuilder sql = new StringBuilder(1024);
		if (!ToolsHTML.isEmptyOrNull(idStruct)) {
			if (isUser) {
				sql.append("SELECT pdu.* FROM permisiondocuser pdu,documents d")
						.append("  WHERE d.numGen = pdu.idDocument AND d.idNode = ")
						.append(idStruct).append(" AND pdu.idUser = '")
						.append(idUser).append("'");
			} else {
				sql.append(
						"SELECT pdu.* FROM permisiondocgroup pdu,documents d")
						.append("  WHERE d.numGen = pdu.idDocument AND d.idNode = ")
						.append(idStruct).append(" AND pdu.idGroup = '")
						.append(idUser).append("'");
			}
			log.debug("[getAllSecurityForDocAndIdStruct] = " + sql.toString());
			Vector datos = JDBCUtil.doQueryVector(sql.toString(),Thread.currentThread().getStackTrace()[1].getMethodName());
			for (int row = 0; row < datos.size(); row++) {
				Properties properties = (Properties) datos.elementAt(row);
				PermissionUserForm forma = new PermissionUserForm();
				setDataSecurityDoc(forma, properties, null);
				forma.setIdDocument(properties.getProperty("idDocument"));
				result.put(forma.getIdDocument(), forma);
			}
		}
	}

	/**
	 * 
	 * @param isUser
	 * @param forma
	 * @return
	 * @throws Exception
	 */
	public static boolean getSecurityForDocAndId(boolean isUser,
			PermissionUserForm forma) throws Exception {
		StringBuilder sql = new StringBuilder(1024);
		if (isUser) {
			sql.append("SELECT pdu.* FROM permisiondocuser pdu")
					.append("  WHERE idDocument = ")
					.append(forma.getIdDocument())
					.append(" AND pdu.idUser = '").append(forma.getIdUser())
					.append("'");
		} else {
			sql.append("SELECT pdu.* FROM permisiondocgroup pdu")
					.append("  WHERE idDocument = ")
					.append(forma.getIdDocument())
					.append(" AND pdu.idGroup = '").append(forma.getIdUser())
					.append("'");
		}
		log.debug("[getSecurityForDocAndId] = " + sql.toString());
		boolean isLoad = false;
		Properties properties = JDBCUtil.doQueryOneRow(sql.toString(),Thread.currentThread().getStackTrace()[1].getMethodName());
		if (properties.getProperty("isEmpty").equalsIgnoreCase("false")) {
			setDataSecurityDoc(forma, properties, null);
			forma.setIdDocument(properties.getProperty("idDocument"));
			isLoad = true;
		}
		return isLoad;
	}

	/**
	 * 
	 * @param prop
	 * @param p1
	 * @return
	 */
	public static String getProperty(Properties prop, String p1) {
		return prop.getProperty(p1);
	}

	/**
	 * 
	 * @param prop
	 * @param p1
	 * @param p2
	 * @return
	 */
	public static String getProperty(Properties prop, String p1, String p2) {
		return (!ToolsHTML.isEmptyOrNull(prop.getProperty(p1)) ? prop
				.getProperty(p1) : prop.getProperty(p2));
	}

	/**
	 * 
	 * @param forma
	 * @param prop
	 * @param id
	 */
	public static void setDataSecurityDoc(PermissionUserForm forma,
			Properties prop, String id) {
		if (!ToolsHTML.isEmptyOrNull(id)) {
			forma.setIdUser(prop.getProperty(id));
		}
		forma.setToViewDocs(Byte.parseByte(getProperty(prop, "toRead")));
		forma.setToView(Byte.parseByte(getProperty(prop, "toRead")));

		forma.setToDelDocs(Byte.parseByte(getProperty(prop, "toDelDocs",
				"toDelete")));
		forma.setToDelete(Byte.parseByte(getProperty(prop, "toDelete")));

		forma.setToEditDocs(Byte.parseByte(getProperty(prop, "toEditDocs",
				"toEdit")));
		forma.setToEdit(Byte.parseByte(getProperty(prop, "toEdit")));

		forma.setToMoveDocs(Byte.parseByte(getProperty(prop, "toMoveDocs")));
		forma.setToMove(Byte.parseByte(getProperty(prop, "toMove")));

		if (!ToolsHTML.isEmptyOrNull(prop.getProperty("toPrint"))) {
			forma.setToPrint(Byte.parseByte(prop.getProperty("toPrint")));
			forma.setToAprove(Byte.parseByte(prop.getProperty("toAprove")));
		} else {
			forma.setToPrint(Byte.parseByte(prop.getProperty("toImpresion")));
			forma.setToAprove(Byte.parseByte(prop.getProperty("toApproved")));
		}

		forma.setToImpresion(Byte.parseByte(prop.getProperty("toImpresion")));
		forma.setToReview(Byte.parseByte(prop.getProperty("toReview")));
		forma.setCheckOut(Byte.parseByte(prop.getProperty("toCheckOut")));
		forma.setToEditRegister(Byte.parseByte(prop
				.getProperty("toEditRegister")));

		forma.setToAdmonDocs(Byte.parseByte(getProperty(prop, "toAdmonDocs",
				"toAdmon")));
		forma.setToAdmon(Byte.parseByte(prop.getProperty("toAdmon")));

		forma.setToViewDocs(Byte.parseByte(prop.getProperty("toViewDocs")));

		String toFlexFlow = prop.getProperty("toFlexFlow");
		if (!ToolsHTML.isEmptyOrNull(toFlexFlow)) {
			forma.setToFlexFlow(Byte.parseByte(toFlexFlow));
			forma.setToChangeUsr(Byte.parseByte(prop.getProperty("toChangeUsr")));
			forma.setToCompleteFlow(Byte.parseByte(!ToolsHTML
					.isEmptyOrNull(prop.getProperty("toCompleteFlow")) ? prop
					.getProperty("toCompleteFlow") : "0"));
		}

		if (!ToolsHTML.isEmptyOrNull(prop.getProperty("toPublicEraser"))) {
			forma.setToPublicEraser(Byte.parseByte(prop
					.getProperty("toPublicEraser")));
		}

		if (!ToolsHTML.isEmptyOrNull(prop.getProperty("toDownload"))) {
			forma.setToDownload(Byte.parseByte(prop.getProperty("toDownload")));
		}

		if (!ToolsHTML.isEmptyOrNull(prop.getProperty("toCompleteFlow"))) {
			forma.setToCompleteFlow(Byte.parseByte(prop
					.getProperty("toCompleteFlow")));
		}
	}

	/**
	 * 
	 * @param isUser
	 * @param idDocument
	 * @return
	 * @throws Exception
	 */
	public static Hashtable loadSecurityDocument(boolean isUser, int idDocument)
			throws Exception {
		String id = "";
		StringBuilder sql = new StringBuilder(1024);
		if (isUser) {
			sql.append("SELECT * FROM permisiondocuser WHERE idDocument = ")
					.append(idDocument);
			id = "idUser";
		} else {
			sql.append("SELECT * FROM permisiondocgroup WHERE idDocument = ")
					.append(idDocument);
			id = "idGroup";
		}
		Vector datos = JDBCUtil.doQueryVector(sql.toString(),Thread.currentThread().getStackTrace()[1].getMethodName());
		Hashtable result = new Hashtable();
		for (int row = 0; row < datos.size(); row++) {
			Properties prop = (Properties) datos.elementAt(row);
			PermissionUserForm forma = new PermissionUserForm();
			setDataSecurityDoc(forma, prop, id);
			result.put(forma.getIdUser(), forma);
		}
		return result;
	}

	/**
	 * 
	 * @param idDocument
	 * @return
	 * @throws Exception
	 */
	public static boolean isUniqueVersionEraser(int idDocument)
			throws Exception {
		StringBuilder sql = new StringBuilder(1024).append(
				"SELECT numver, statu FROM versiondoc WHERE numdoc = ").append(
				idDocument);

		CachedRowSet crs = JDBCUtil.executeQuery(sql, Thread.currentThread().getStackTrace()[1].getMethodName());

		if (crs.size() > 1) {
			return false;
		}

		if (crs.next()) {
			if (!crs.getString("statu").equals(HandlerDocuments.docTrash)) {
				return false;
			}
		}

		return true;
	}

	/**
	 * 
	 * @param type
	 * @return
	 * @throws Exception
	 */
	public static boolean isDocumentType(String type) throws Exception {
		StringBuilder sql = new StringBuilder(1024)
				.append("SELECT nameDocument FROM documents WHERE active = '1' AND type ='")
				.append(type).append("'");
		Vector datos = JDBCUtil.doQueryVector(sql.toString(),Thread.currentThread().getStackTrace()[1].getMethodName());
		return datos.size() > 0;
	}

	/**
	 * 
	 * @param node
	 * @return
	 * @throws Exception
	 */
	public static boolean isNodeParent(String node) throws Exception {
		boolean existeProcesoSacop = false;
		if (HandlerProcesosSacop.getProcesoOrigenEnUsoSacop(node)) {
			existeProcesoSacop = true;
		}

		StringBuilder sql = new StringBuilder(1024)
				.append("SELECT numGen FROM documents WHERE idNode =")
				.append(node).append(" AND active = '")
				.append(Constants.permission).append("'");
		log.debug("[isNodeParent] = " + sql.toString());
		Vector datos = JDBCUtil.doQueryVector(sql.toString(),Thread.currentThread().getStackTrace()[1].getMethodName());
		return ((datos.size() > 0) || (existeProcesoSacop));
	}

	/**
	 * 
	 * @param node
	 * @return
	 * @throws Exception
	 */
	public static boolean isNodeDocuments(String node) throws Exception {
		StringBuilder sql = new StringBuilder(1024)
				.append("SELECT numGen FROM documents WHERE idNode  in ( ")
				.append(node).append(" )").append(" AND active = '")
				.append(Constants.permission).append("'");
		// System.out.println("sql = " + sql.toString());
		log.debug("[isNodeParent] = " + sql.toString());

		Vector datos = JDBCUtil.doQueryVector(sql.toString(),Thread.currentThread().getStackTrace()[1].getMethodName());
		return ((datos.size() > 0));

	}

	/**
	 * 
	 * @param normISO
	 * @return
	 * @throws Exception
	 */
	public static boolean isDocumentNorms(String normISO) throws Exception {
		StringBuilder sql = new StringBuilder(1024)
				.append("SELECT nameDocument FROM documents WHERE normISO ='")
				.append(normISO).append("'").append(" AND active = '")
				.append(Constants.permission).append("'");
		Vector datos = JDBCUtil.doQueryVector(sql.toString(),Thread.currentThread().getStackTrace()[1].getMethodName());
		return datos.size() > 0;
	}


	/**
	 * 
	 * @param idDocument
	 * @return
	 * @throws Exception
	 */
	public static boolean isFlexWorkFlowPending(String idDocument)
			throws Exception {
		StringBuilder sql = new StringBuilder(1024)
				.append("SELECT statu FROM flexworkflow WHERE idDocument =")
				.append(idDocument).append(" AND statu ='")
				.append(HandlerWorkFlows.pending).append("' ");
		Vector datos = JDBCUtil.doQueryVector(sql.toString(),Thread.currentThread().getStackTrace()[1].getMethodName());
		return datos.size() > 0;
	}

	/**
	 * 
	 * @param idDoc
	 * @return
	 */
	public static String getIdVersionAntForDoc(int idDoc) {
		String result = "";
		try {
			StringBuilder sql = new StringBuilder(1024)
					.append("SELECT lastVersionApproved FROM documents WHERE numGen = ")
					.append(idDoc);
			Properties prop = JDBCUtil.doQueryOneRow(sql.toString(),Thread.currentThread().getStackTrace()[1].getMethodName());
			if (prop.getProperty("isEmpty").equalsIgnoreCase("false")) {
				return prop.getProperty("lastVersionApproved");
			}

			sql.setLength(0);
			sql.append("SELECT numVer FROM versiondoc")
					.append(" WHERE statu = ").append(docReview)
					.append(" AND numDoc = ").append(idDoc)
					.append(" ORDER by NumVer DESC");
			prop = JDBCUtil.doQueryOneRow(sql.toString(),Thread.currentThread().getStackTrace()[1].getMethodName());
			if (prop.getProperty("isEmpty").equalsIgnoreCase("false")) {
				return prop.getProperty("numVer");
			}

			sql.setLength(0);
			sql.append("SELECT numVer FROM versiondoc")
					.append(" WHERE statu = ").append(docTrash)
					.append(" AND numDoc = ").append(idDoc)
					.append(" ORDER by NumVer DESC");
			prop = JDBCUtil.doQueryOneRow(sql.toString(),Thread.currentThread().getStackTrace()[1].getMethodName());
			if (prop.getProperty("isEmpty").equalsIgnoreCase("false")) {
				return prop.getProperty("numVer");
			}
		} catch (Exception ex) {
			log.error(ex.getMessage());
			ex.printStackTrace();
		}
		return result;
	}

	/**
	 * 
	 * @param forma
	 * @throws ApplicationExceptionChecked
	 */
	public static void loadDataDocument(DataUserWorkFlowForm forma)
			throws ApplicationExceptionChecked {
		try {
			StringBuilder sql = new StringBuilder(1024)
					.append("SELECT number,prefix,nameDocument,statuAnt")
					.append("  FROM documents WHERE numGen = ")
					.append(forma.getIdDocument());
			Properties prop = JDBCUtil.doQueryOneRow(sql.toString(),Thread.currentThread().getStackTrace()[1].getMethodName());
			if (prop.getProperty("isEmpty").equalsIgnoreCase("false")) {
				forma.setNameDocument(prop.getProperty("nameDocument"));
				forma.setPrefix(prop.getProperty("prefix"));
				forma.setNumber(prop.getProperty("number"));
				forma.setStatuAnt(prop.getProperty("statuAnt"));
			}
		} catch (Exception ex) {
			log.error(ex.getMessage());
			ex.printStackTrace();
			throw new ApplicationExceptionChecked("E0052");
		}

	}

	/*
	 * HandlerDocuments.getAllVersionIdsToDocumentAndStatu(idDocument,
	 * std.toString(), false);
	 */
	public static Collection getAllVersionIdsToDocumentAndStatu(int idDocument,
			String statu, boolean useIn) {
		Vector result = new Vector();
		try {
			StringBuilder sql = new StringBuilder(2048)
					.append("SELECT numVer FROM versiondoc WHERE numDoc = ");
			if (useIn) {
				sql.append(idDocument)
						.append(" AND cast(statu as int) IN (")
						.append(statu.replace('(', ' ').replace(')', ' ')
								.trim()).append(") ");
			} else {
				sql.append(idDocument).append(" AND cast(statu as int) = ")
						.append(statu.replaceAll("'", "")).append(" ");
			}
			sql.append(" AND active = '").append(Constants.permission)
					.append("'").append(" ORDER by numVer");
			Vector datos = JDBCUtil.doQueryVector(sql.toString(),Thread.currentThread().getStackTrace()[1].getMethodName());
			for (int row = 0; row < datos.size(); row++) {
				Properties properties = (Properties) datos.elementAt(row);
				result.add(properties.getProperty("numVer"));
			}
		} catch (Exception ex) {
			// System.out.println(sql.toString());
			log.error(ex.getMessage());
			ex.printStackTrace();
		}
		return result;
	}

	/**
	 * 
	 * @param newStatu
	 * @param numVer
	 * @param statu
	 * @param con
	 * @throws SQLException
	 */
	public static synchronized void deleteLogicVersion(byte newStatu,
			String numVer, String statu, Connection con) throws SQLException {
		StringBuilder edit = new StringBuilder(1024)
				.append("UPDATE versiondoc SET active = '").append(newStatu)
				.append("'").append(" WHERE numVer = ").append(numVer);
		// edit.append(" AND statu = '").append(statu).append("'");
		PreparedStatement st = con.prepareStatement(JDBCUtil.replaceCastMysql(edit.toString()));
		st.executeUpdate();
		DbUtils.closeQuietly(st);
	}

	// updateStatuDoc(docObs,id,docApproved,con);
	/**
	 * 
	 * @param newStatu
	 * @param numVer
	 * @param statu
	 * @param con
	 * @throws SQLException
	 */
	public static synchronized void updateStatuDoc(String newStatu,
			String numVer, String statu, Connection con) throws SQLException {
		StringBuilder edit = new StringBuilder(1024)
				.append("UPDATE versiondoc SET statu = '").append(newStatu)
				.append("' WHERE numVer = ").append(numVer)
				.append(" AND statu = '").append(statu).append("'");
		PreparedStatement st = con.prepareStatement(JDBCUtil.replaceCastMysql(edit.toString()));
		st.executeUpdate();
		DbUtils.closeQuietly(st);
	}

	// 26 de agosto 2005 inicio
	// Actualiza la version de obsoleto a borrador
	public static synchronized void updateStatuVersionDocObsoletoAborrador(
			int numVer, Connection con, String wfuStatu) throws SQLException {

		StringBuilder update = new StringBuilder(1024)
				.append("UPDATE versiondoc SET statu = ").append(wfuStatu)
				.append(" WHERE numVer = ").append(numVer);
		PreparedStatement st = con.prepareStatement(JDBCUtil.replaceCastMysql(update.toString()));
		st.executeUpdate();

		StringBuilder updateDocuments = new StringBuilder(2048)
				.append("UPDATE documents set statu=")
				.append(wfuStatu)
				.append(" WHERE numgen=(SELECT numDoc FROM versiondoc WHERE numVer=")
				.append(numVer).append(")");
		st = con.prepareStatement(JDBCUtil.replaceCastMysql(updateDocuments.toString()));
		st.executeUpdate();

		DbUtils.closeQuietly(st);
	}

	// 26 de agosto 2005 fin
	public static synchronized void updateStatuVersionDoc(int numVer,
			Connection con, String wfuStatu, Timestamp time,
			Timestamp dateExpire, Timestamp dateCreated, String mayorVer,
			String minorVer, boolean updateV, byte typeWF, String comments) throws SQLException {

		StringBuilder update = new StringBuilder(1024).append(
				"UPDATE versiondoc SET statu = ").append(wfuStatu);

		int pos = 1;
		if (time != null) {
			update.append(",dateApproved = ?");
		}
		if (updateV) {
			update.append(",isUpdate = ").append(JDBCUtil.getCastAsBitString("1"));
		}
		if (dateCreated != null) {
			update.append(",dateCreated = ?");
		}
		if (dateExpire != null) {
			update.append(",dateExpires = ?");
		}
		if (mayorVer != null) {
			update.append(", MayorVer = ? ");
		}
		if (minorVer != null) {
			update.append(", MinorVer = ? ");
		}
		if (typeWF != -1) {
			update.append(", typeWF = CAST(? as bit) ");
		}
		if (comments != null && comments.trim().length()>0) {
			update.append(", comments = ? ");
		}

		update.append(" WHERE numVer = ").append(numVer);
		PreparedStatement st = con.prepareStatement(JDBCUtil.replaceCastMysql(update.toString()));

		if (time != null) {
			st.setTimestamp(pos++, time);
		}

		if (dateCreated != null) {
			st.setTimestamp(pos++, dateCreated);
		}

		if (dateExpire != null) {
			st.setTimestamp(pos++, dateExpire);
		}

		if (mayorVer != null) {
			st.setString(pos++, mayorVer);
		}

		if (minorVer != null) {
			st.setString(pos++, minorVer);
		}

		if (typeWF != -1) {
			st.setInt(pos++, typeWF);
		}
		
		if (comments != null && comments.trim().length()>0) {
			st.setString(pos++, comments.replaceAll("'", ""));
		}

		st.executeUpdate();
		DbUtils.closeQuietly(st);
	}

	/**
	 * 
	 * @param numVer
	 * @param con
	 * @throws Exception 
	 */
	public static synchronized void updateStatuhistVersionDoc(int numVer,
			Connection con) throws Exception {

		String ids = JDBCUtil.executeQueryRetornaIds(new StringBuffer("select numdoc from versiondoc where numVer=").append(numVer), null, con, Thread.currentThread().getStackTrace()[1].getMethodName());
		
		StringBuilder update = new StringBuilder(1024);
		update.append("UPDATE versiondoc SET statuhist = ");
		update.append("(Select statu from documents where numgen=");
		update.append(ids);
		update.append(") WHERE numVer = ").append(numVer);
		log.debug("updateStatuhistVersionDoc=" + update.toString());
		PreparedStatement st = con.prepareStatement(JDBCUtil.replaceCastMysql(update.toString()));
		st.executeUpdate();
		DbUtils.closeQuietly(st);
	}

	/**
	 * Este m�todo permite verificar si en el nodo Dado existe un documento
	 * activo que contega la el mismo n�mero del documento que se indica como
	 * par�metro
	 */
	// 03 agosto 2005 inicio
	public static String exitsDocInLocationByNumber(String idNode) {
		String resp = null;
		StringBuilder sql = new StringBuilder(100);
		sql.append("SELECT number FROM documents WHERE ");
		sql.append("  idNode = ").append(idNode);
		// sql.append(" AND active =
		// '").append(Constants.permission).append("'");
		log.debug("[exitsDocInLocationByNumber]" + sql.toString());
		try {
			Properties prop = JDBCUtil.doQueryOneRow(sql.toString(),Thread.currentThread().getStackTrace()[1].getMethodName());
			if (prop.getProperty("isEmpty").equalsIgnoreCase("false")) {
				resp = prop.getProperty("number");
			}
		} catch (Exception ex) {
			log.error(ex.getMessage());
			ex.printStackTrace();
		}
		return resp;
	}

	// 03 agosto 2005 fin

	public static String exitsDocInLocation(String numberDoc, String idNodes) {
		return exitsDocInLocation(null, numberDoc, idNodes);
	}

	/**
	 * 
	 * @param con
	 * @param numberDoc
	 * @param idNodes
	 * @return
	 */
	public static String exitsDocInLocation(Connection con, String numberDoc,
			String idNodes) {
		String result = null;
		try {
			StringBuilder sql = new StringBuilder(2048)
					.append("SELECT idNode FROM documents WHERE number = '")
					.append(numberDoc).append("'");
			if (!ToolsHTML.isEmptyOrNull(idNodes)) {
				sql.append(" AND idNode = ").append(idNodes);
				// sql.append(" AND idNode IN (").append(idNodes).append(")");
			}
			// sql.append(" AND (active <>
			// ").append(Constants.notPermission).append(")");
			sql.append(" AND (active = '").append(Constants.permission)
					.append("')");
			log.debug("[exitsDocInLocation] " + sql.toString());
			Properties prop = JDBCUtil.doQueryOneRow(con, sql.toString(),Thread.currentThread().getStackTrace()[1].getMethodName());
			if (prop.getProperty("isEmpty").equalsIgnoreCase("false")) {
				result = prop.getProperty("idNode");
			}
		} catch (Exception ex) {
			log.error(ex.getMessage());
			ex.printStackTrace();
		}
		log.debug("[exitsDocInLocation] result " + result);
		return result;
	}

	// public static String exitsDocInLocation(String numberDoc,String
	// idNodes,BaseDocumentForm
	// forma) {
	// String result = null;
	// StringBuilder sql = new StringBuilder(100);
	// sql.append("SELECT idNode FROM documents WHERE number =
	// '").append(numberDoc).append("'");
	// if (!ToolsHTML.isEmptyOrNull(idNodes)) {
	// sql.append(" AND idNode = ").append(idNodes);
	// // sql.append(" AND idNode IN (").append(idNodes).append(")");
	// }
	// sql.append(" AND (numgen <> ").append(forma.getNumberGen()).append(")");
	// sql.append(" AND (active <>
	// ").append(Constants.notPermission).append(")");
	// log.debug("[exitsDocInLocationByNumber]"+sql.toString());
	// //System.out.println("sql = " + sql.toString());
	// try {
	// Properties prop = JDBCUtil.doQueryOneRow(sql.toString(),Thread.currentThread().getStackTrace()[1].getMethodName());
	// if (prop.getProperty("isEmpty").equalsIgnoreCase("false")) {
	// result = prop.getProperty("idNode");
	// }
	// } catch (Exception ex) {
	// log.error(ex.getMessage());
	// ex.printStackTrace();
	// }
	// log.debug("[exitsDocInLocation] result " + result);
	// return result;
	// }

	public static String exitsDocInLocation(String numberDoc, String idNodes,
			BaseDocumentForm forma) {
		String result = null;
		try {
			StringBuilder sql = new StringBuilder(12048)
					.append("SELECT idNode FROM documents WHERE number = '")
					.append(numberDoc).append("'");
			if (!ToolsHTML.isEmptyOrNull(idNodes)) {
				sql.append(" AND idNode = ").append(idNodes);
				// sql.append(" AND idNode IN (").append(idNodes).append(")");
			}
			if (forma != null && forma.getNumberGen() != null
					&& ToolsHTML.isNumeric(forma.getNumberGen())) {
				sql.append(" AND (numgen <> '").append(forma.getNumberGen())
						.append("')");
			}
			sql.append(" AND (active <> '").append(Constants.notPermission)
					.append("')");
			log.debug("[exitsDocInLocationByNumber]" + sql.toString());
			// System.out.println("sql = " + sql.toString());
			Properties prop = JDBCUtil.doQueryOneRow(sql.toString(),Thread.currentThread().getStackTrace()[1].getMethodName());
			if (prop.getProperty("isEmpty").equalsIgnoreCase("false")) {
				result = prop.getProperty("idNode");
			}
		} catch (Exception ex) {
			log.error(ex.getMessage());
			ex.printStackTrace();
		}
		log.debug("[exitsDocInLocation] result " + result);
		return result;
	}

	/**
	 * 
	 * @param numberDoc
	 * @param idNode
	 * @param prefix
	 * @return
	 */
	public static boolean exitsDocInLocationByNumber(String numberDoc,
			String idNode, String prefix) {
		boolean resp = false;
		try {
			StringBuilder sql = new StringBuilder(2048)
					.append("SELECT number FROM documents WHERE number = '")
					.append(numberDoc).append("'");
			if (!ToolsHTML.isEmptyOrNull(prefix)) {
				sql.append(" AND prefix = '").append(prefix).append("'");
			}
			sql.append(" AND (active <> ").append(Constants.notPermission)
					.append(")");
			// sql.append(" AND (statu <>
			// ").append(HandlerDocuments.docObs).append(")");
			/*
			 * sql.append(" OR prefix='").append(prefix).append("'").append(")");
			 */

			log.debug("[exitsDocInLocationByNumber]" + sql.toString());
			Properties prop = JDBCUtil.doQueryOneRow(sql.toString(),Thread.currentThread().getStackTrace()[1].getMethodName());
			if (prop.getProperty("isEmpty").equalsIgnoreCase("false")) {
				resp = true;
			}
		} catch (Exception ex) {
			log.error(ex.getMessage());
			ex.printStackTrace();
		}
		return resp;
	}

	/**
	 * 
	 * @param numberDoc
	 * @param idNode
	 * @return
	 */
	public static boolean exitsDocInLocationByNumber(String numberDoc,
			String idNode) {
		boolean resp = false;
		try {
			StringBuilder sql = new StringBuilder(2048)
					.append("SELECT number FROM documents WHERE number = '")
					.append(numberDoc).append("'").append(" AND idNode = ")
					.append(idNode);
			// sql.append(" AND active =
			// '").append(Constants.permission).append("'");
			log.debug("[exitsDocInLocationByNumber]" + sql.toString());
			Properties prop = JDBCUtil.doQueryOneRow(sql.toString(),Thread.currentThread().getStackTrace()[1].getMethodName());
			if (prop.getProperty("isEmpty").equalsIgnoreCase("false")) {
				resp = true;
			}
		} catch (Exception ex) {
			log.error(ex.getMessage());
			ex.printStackTrace();
		}
		return resp;
	}

	/**
	 * 
	 * @param numberDoc
	 * @return
	 */
	public static boolean exitsDocInLocationByNumberUnico(String numberDoc) {
		return exitsDocInLocationByNumberUnico(null, numberDoc);
	}

	/**
	 * 
	 * @param con
	 * @param numberDoc
	 * @return
	 */
	public static boolean exitsDocInLocationByNumberUnico(Connection con,
			String numberDoc) {
		boolean resp = false;
		try {
			StringBuilder sql = new StringBuilder(2048)
					.append("SELECT number FROM documents WHERE number = '")
					.append(numberDoc)
					.append("'")
					// sql.append(" AND statu <> ").append(HandlerDocuments.docObs);
					// sql.append(" AND active <> ").append(Constants.notPermission);
					.append(" AND active = '").append(Constants.permission)
					.append("'");
			log.debug("[exitsDocInLocationByNumber]" + sql.toString());
			Properties prop = JDBCUtil.doQueryOneRow(con, sql.toString(),Thread.currentThread().getStackTrace()[1].getMethodName());
			if (prop.getProperty("isEmpty").equalsIgnoreCase("false")) {
				resp = true;
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return resp;
	}

	/**
	 * 
	 * @param numberDoc
	 * @param forma
	 * @return
	 */
	public static boolean exitsDocInLocationByNumberUnico(String numberDoc,
			BaseDocumentForm forma) {
		boolean resp = false;
		try {
			StringBuilder sql = new StringBuilder(2048)
					.append("SELECT number FROM documents WHERE number = '")
					.append(numberDoc).append("'").append(" and numgen <> ")
					.append(forma.getNumberGen())
					// sql.append(" AND statu <> ").append(HandlerDocuments.docObs);
					.append(" AND active <> '").append(Constants.notPermission)
					.append("' ");
			log.debug("[exitsDocInLocationByNumber]" + sql.toString());
			Properties prop = JDBCUtil.doQueryOneRow(sql.toString(),Thread.currentThread().getStackTrace()[1].getMethodName());
			if (prop.getProperty("isEmpty").equalsIgnoreCase("false")) {
				resp = true;
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return resp;
	}

	/**
	 * 
	 * @param imagenBuffer
	 * @param path
	 * @param nameFile
	 */
	public static void saveFileInDisk(InputStream imagenBuffer, String path,
			String nameFile) {

		BufferedInputStream in = null;
		BufferedOutputStream out = null;
		try {
			String ruta = path + File.separator + nameFile;
			log.debug("Salvando Archivo: " + ruta);
			ruta = ruta.replace((char) 92, '/');
			ruta = ruta.replaceAll("//", "/");
			File fichero = new File(ruta);

			in = new BufferedInputStream(imagenBuffer);
			out = new BufferedOutputStream(new FileOutputStream(fichero));

			byte[] bytes = new byte[8096];
			int len = 0;
			while ((len = in.read(bytes)) > 0) {
				out.write(bytes, 0, len);
			}

			out.flush();
		} catch (Exception e) {
			log.error(e.getMessage());
			e.printStackTrace();
		} finally {
			IOUtils.closeQuietly(out);
			IOUtils.closeQuietly(in);
		}
	}

	/**
	 * 
	 * @param forma
	 * @param path
	 * @return
	 * @throws ApplicationExceptionChecked
	 */
	public static BaseDocumentForm createFile(BaseDocumentForm forma,
			String path) throws ApplicationExceptionChecked {
		Connection con = JDBCUtil.getConnection(Thread.currentThread().getStackTrace()[1].getMethodName());
		InputStream imagenRecuperada = getFileBDCheckOut(con,
				forma.getCheckOut());
		if (imagenRecuperada != null) {
			saveFileInDisk(imagenRecuperada, path, forma.getNameFile());
		} else {
			throw new ApplicationExceptionChecked("E0009");
		}

		DbUtils.closeQuietly(con);
		return forma;
	}

	/**
	 * M�todo para recuperar el archivo bloqueado por el Usuario
	 * 
	 * @param con
	 * @param idCheckOut
	 */
	public static InputStream getFileBDCheckOut(Connection con, int idCheckOut) {
		try {
			return Archivo.readDocumentFromDisk("doccheckout", idCheckOut);
		} catch (Exception e) {
			log.error(e.getMessage());
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * M�todo para recuperar el archivo bloqueado por el Usuario
	 * 
	 * @param con
	 * @param idCheckOut
	 */
	public static String[] getNameDocAndOwnerCheckOut(Connection con,
			int idCheckOut) {
		Connection conLocal = null;
		PreparedStatement pst = null;
		ResultSet rs = null;

		String[] datos = new String[2];
		try {
			conLocal = (con == null) ? JDBCUtil.getConnection(Thread.currentThread().getStackTrace()[1].getMethodName()) : con;
			String consultaGenerada = "SELECT nameDocCheckOut, ownerCheckOut FROM doccheckout WHERE idCheckOut = "
					+ idCheckOut;
			pst = conLocal.prepareStatement(JDBCUtil.replaceCastMysql(consultaGenerada));
			rs = pst.executeQuery();
			if (rs.next()) {
				datos[0] = rs.getString(1);
				datos[1] = rs.getString(2);
			}
			return datos;
		} catch (Exception e) {
			log.error(e.getMessage());
			e.printStackTrace();
		} finally {
			JDBCUtil.closeQuietly(pst, rs);
			if (con == null) {
				setFinally(conLocal);
			}
		}
		return null;
	}

	/**
	 * 
	 * @param nameFile
	 * @param path
	 * @param id
	 * @throws Exception
	 */
	public static synchronized void saveBD(String nameFile, String path, int id)
			throws Exception {
		if (ToolsHTML.isEmptyOrNull(nameFile)) {
			FileOutputStream out = new FileOutputStream(path + File.separator
					+ DesigeConf.getProperty("enlinea"));
			PrintStream p = new PrintStream(out);
			p.close();
			nameFile = DesigeConf.getProperty("enlinea");
		}
		log.debug("Salvando Archivo");
		File fichero = new File(path + File.separator + nameFile);
		FileInputStream streamEntrada = new FileInputStream(fichero);

		Archivo.writeDocumentToDisk("doccheckout", id, streamEntrada);

		streamEntrada.close();

		System.gc();
		fichero.delete();
	}

	/**
	 * Este m�todo permite obtener el elaborador del documento dado
	 */
	public static BeanFirmsDoc getCreatorDocument(String idDoc, String numVer) {
		BeanFirmsDoc bean = null;
		try {
			StringBuilder sql = new StringBuilder(2048)
			///ydavila Ticket 001-00-003367 Creador debe ser el propietario
			//.append(" select vd.createdBy ");
			.append(" select vd.ownerversion ");
			
			switch (Constants.MANEJADOR_ACTUAL) {
			case Constants.MANEJADOR_MSSQL:
				sql.append(",(p.Apellidos + ' ' + p.nombres) as usuario,");
				break;
			case Constants.MANEJADOR_POSTGRES:
				sql.append(",(p.Apellidos || ' ' || p.nombres) as usuario,");
				break;
			case Constants.MANEJADOR_MYSQL:
				sql.append(",CONCAT(p.Apellidos , ' ' , p.nombres) as usuario,");
				break;
			}
			sql.append("c.cargo,p.accountActive, d.dateCreation")
					.append(" FROM person p,documents d,versiondoc vd,tbl_cargo c")
					///ydavila Ticket 001-00-003367 Creador debe ser el propietario
					//.append(" WHERE vd.createdBy = p.nameuser")
					.append(" WHERE vd.ownerversion = p.nameuser")
					.append(" AND cast(p.cargo as int) = c.idCargo");
			if (!ToolsHTML.isEmptyOrNull(idDoc)) {
				sql.append(" AND vd.numDoc=").append(idDoc);
			}
			/*
			 * if(!ToolsHTML.isEmptyOrNull(numVer)){
			 * sql.append(" AND vd.numVer=").append(numVer); }
			 */
			sql.append(
					" AND vd.numVer = (select min(numver) from versiondoc where numdoc = ")
					.append(idDoc).append(" and active = '")
					.append(Constants.permission).append("')")
					.append(" AND vd.active = '").append(Constants.permission)
					.append("'").append(" AND d.numGen = vd.numDoc ");
			// sql.append(" AND p.accountActive = '").append(Constants.permission).append("'");

			log.debug("[getCreatorDocument] sql = " + sql);
			Vector data = JDBCUtil.doQueryVector(sql.toString(),Thread.currentThread().getStackTrace()[1].getMethodName());
			if (data != null && data.size() > 0) {
				Properties properties = (Properties) data.elementAt(0);
				bean = new BeanFirmsDoc(properties.getProperty("createdBy"),
						properties.getProperty("usuario"),
						properties.getProperty("cargo"));
				bean.setDateReplied(ToolsHTML.formatDateShow(
						properties.getProperty("dateCreation"), true));
			}

		} catch (Exception ex) {
			log.error(ex.getMessage());
			ex.printStackTrace();
		}
		
		return bean;
	}

	/**
	 * Este metodo permite obtener la razon de cambio en el documento
	 * se omite el comentario en la primera version del documento.
	 * 
	 * @throws Exception
	 */
	public static String getReasonToChange(String idDoc, String numVer)
			throws Exception {
		String reasonToChange = "";
		String numVerRazonCambio  = "";
		int versionQueCambio = 0;
		boolean procesar = false;
		boolean esObsoleto = false;
		//primera validadcion solo los documentos obsoletos fueron cambiados
		
		try {
			StringBuilder q = new StringBuilder("select numVer,statu,MayorVer from versiondoc where numVer=? ");
			ArrayList p = new ArrayList();
			p.add(Integer.parseInt(numVer));
			CachedRowSet crs = JDBCUtil.executeQuery(q, p, Thread.currentThread().getStackTrace()[1].getMethodName());
			if(crs.next()) {
				//if(crs.getString(2).equals("6") && !crs.getString(3).equals("0") ) { // si la version pasada no es la primera buscamos la razon de cambio
				if(crs.getString(2).equals("6")) { // si la version pasada no es la primera buscamos la razon de cambio	
					esObsoleto = true;
					
				}
				versionQueCambio =Integer.parseInt(crs.getString(3))+1;
			}
		
			
			StringBuilder q1 = new StringBuilder("select numVer,statu,MayorVer,MinorVer from versiondoc where numDoc=? and MayorVer=?  order by numVer");
			ArrayList p1 = new ArrayList();
			p1.add(Integer.parseInt(idDoc));
			p1.add(versionQueCambio);
			CachedRowSet crs1 = JDBCUtil.executeQuery(q1, p1, Thread.currentThread().getStackTrace()[1].getMethodName());
			while (crs1.next()) {    
	
				if( esObsoleto && crs1.getString(4).equals("0"))  { 
					numVerRazonCambio = crs1.getString(1);
					procesar = true;
						
				}
			}
				
			if(procesar) {
				
				StringBuilder q2 = new StringBuilder(2028);
						
				switch (Constants.MANEJADOR_ACTUAL) {
				case Constants.MANEJADOR_MSSQL:
					q2.append("select CAST(comments AS nvarchar(max)) from versiondoc where numver=?");
					break;
				case Constants.MANEJADOR_POSTGRES:
					q2.append("select comments  from versiondoc where numver=?");
					break;
				case Constants.MANEJADOR_MYSQL:
					q2.append("select comments  from versiondoc where numver=?");
					break;
				}
				
				//StringBuilder q2 = new StringBuilder("select CAST(comments AS nvarchar(max)) from versiondoc where numver=?"  );
				ArrayList p2 = new ArrayList();
				p2.add(Integer.parseInt(numVerRazonCambio));
				CachedRowSet crs2 = JDBCUtil.executeQuery(q2, p2, Thread.currentThread().getStackTrace()[1].getMethodName());
				if(crs2.next()) {
					 //System.out.println("valor del comentario :");
					 if (!ToolsHTML.isEmptyOrNull(crs2.getString(1))) {
						 
						 switch (Constants.MANEJADOR_ACTUAL) {
							case Constants.MANEJADOR_MSSQL:
								 reasonToChange=org.apache.commons.io.IOUtils.toString(crs2.getClob(1).getCharacterStream());
								break;
							case Constants.MANEJADOR_POSTGRES:
								reasonToChange=crs2.getString(1);
								break;
							case Constants.MANEJADOR_MYSQL:
								reasonToChange=crs2.getString(1);
								break;
							}
					 
						 // reasonToChange=org.apache.commons.io.IOUtils.toString(crs2.getClob(1).getCharacterStream());
					
						}
				}
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
		return reasonToChange==null?"":reasonToChange;
	}
	
	
	// nuevo metodo para un bean
	
	public static BeanRazonCambioDoc getRazonCambio(String idDoc, String numVer)
			throws Exception {
		BeanRazonCambioDoc  bean = null;
		String reasonToChange = "";
		String numVerRazonCambio  = "";
		int versionQueCambio = 0;
		boolean procesar = false;
		boolean esObsoleto = false;
		//primera validadcion solo los documentos obsoletos fueron cambiados
		
		try {
			StringBuilder q = new StringBuilder("select numVer,statu,MayorVer from versiondoc where numVer=? ");
			ArrayList p = new ArrayList();
			p.add(Integer.parseInt(numVer));
			CachedRowSet crs = JDBCUtil.executeQuery(q, p, Thread.currentThread().getStackTrace()[1].getMethodName());
			if(crs.next()) {
				//if(crs.getString(2).equals("6") && !crs.getString(3).equals("0") ) { // si la version pasada no es la primera buscamos la razon de cambio
				if(crs.getString(2).equals("6")) { // si la version pasada no es la primera buscamos la razon de cambio	
					esObsoleto = true;
					
				}
				versionQueCambio =Integer.parseInt(crs.getString(3))+1;
			}
		
			
			StringBuilder q1 = new StringBuilder("select numVer,statu,MayorVer,MinorVer from versiondoc where numDoc=? and MayorVer=?  order by numVer");
			ArrayList p1 = new ArrayList();
			p1.add(Integer.parseInt(idDoc));
			p1.add(versionQueCambio);
			CachedRowSet crs1 = JDBCUtil.executeQuery(q1, p1, Thread.currentThread().getStackTrace()[1].getMethodName());
			while (crs1.next()) {    
			
			if( esObsoleto && crs1.getString(4).equals("0"))  { 
				numVerRazonCambio = crs1.getString(1);
				procesar = true;
				
					
				}
			}
				
			if(procesar) {
				
				StringBuilder q2 = new StringBuilder(2028);
				
				switch (Constants.MANEJADOR_ACTUAL) {
				case Constants.MANEJADOR_MSSQL:
					q2.append("select CAST(comments AS nvarchar(max)),dateApproved, ownerVersion from versiondoc where numver=?");
					break;
				case Constants.MANEJADOR_POSTGRES:
					q2.append("select comments,dateApproved, ownerVersion from versiondoc where numver=?");
					break;
				case Constants.MANEJADOR_MYSQL:
					q2.append("select comments,dateApproved, ownerVersion from versiondoc where numver=?");
					break;
				}
				//StringBuilder q2 = new StringBuilder("select CAST(comments AS nvarchar(max)),dateApproved, ownerVersion from versiondoc where numver=? "  );
				ArrayList p2 = new ArrayList();
				p2.add(Integer.parseInt(numVerRazonCambio));
				
				CachedRowSet crs2 = JDBCUtil.executeQuery(q2, p2, Thread.currentThread().getStackTrace()[1].getMethodName());
				 if(crs2.next()) {
			
					 if (!ToolsHTML.isEmptyOrNull(crs2.getString(1))) {
					 
						 //reasonToChange=org.apache.commons.io.IOUtils.toString(crs2.getClob(1).getCharacterStream());
						 switch (Constants.MANEJADOR_ACTUAL) {
							case Constants.MANEJADOR_MSSQL:
								 reasonToChange=org.apache.commons.io.IOUtils.toString(crs2.getClob(1).getCharacterStream());
								break;
							case Constants.MANEJADOR_POSTGRES:
								reasonToChange=crs2.getString(1);
								break;
							case Constants.MANEJADOR_MYSQL:
								reasonToChange=crs2.getString(1);
								break;
							}
						 
						 reasonToChange = reasonToChange.replaceAll("\"","”");
						 reasonToChange = reasonToChange.replaceAll("<br>","\n\n");
						 reasonToChange = reasonToChange.replaceAll("&nbsp;"," ");
						 reasonToChange = reasonToChange.replaceAll("\\<[^>]*>","");
						 
							bean = new BeanRazonCambioDoc();
							bean.setFechaCambio(ToolsHTML.formatDateShow(crs2.getString(2), true));
							bean.setRazonCambio(reasonToChange);
							bean.setVersionCambio(versionQueCambio+".0");
							bean.setUsuarioCambio(crs2.getString(3));
							

					
						}
				}
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		return bean;
	}
	/**
	 * Este m�todo permite obtener los colaboradores del documento segun el
	 * historico de cambios
	 * 
	 * @throws Exception
	 */
	public static Collection getCollaboratorDocument(String idDoc, String numVer)
			throws Exception {
		Vector result = new Vector();
		Vector resultFiltrado = new Vector();
		try {
			// consultamos el numero de version
			StringBuilder q = new StringBuilder(
					"select mayorver,minorver from versiondoc where numver=?");
			ArrayList p = new ArrayList();
			p.add(Integer.parseInt(numVer));
			CachedRowSet crs = JDBCUtil.executeQuery(q, p, Thread.currentThread().getStackTrace()[1].getMethodName());
			crs.next();
			String mayorVer = null;
			// System.out.println(crs.getString(2));
			if (crs.getString(1) != null
					&& crs.getString(2) != null
					&& crs.getString(1).replaceAll("[0-9]", "").trim().length() > 0
					&& (crs.getString(2).trim().equals("")
							|| crs.getString(2).equals("0") || crs.getString(2)
							.replaceAll("[0-9]", "").trim().length() > 0)) {
				mayorVer = ToolsHTML.beforeVersion(crs.getString(1));
			} else if (crs.getString(2) != null
					&& !String.valueOf(crs.getString(2)).trim().equals("0")) {
				mayorVer = crs.getString(1);
			} else {
				mayorVer = ToolsHTML.beforeVersion(crs.getString(1));
			}

			// 5 modificado 7 deshacer modificacion
			StringBuilder query = new StringBuilder(2028)
					.append("select p.nameUser As idUser, h.nameUser, ")
					.append(JDBCUtil.concatSql(new String[] { "p.Apellidos",
							"p.nombres" }, "usuario"))
					.append("c.cargo, h.type, max(h.dateChange) As dateReplied, count(h.type) As repeticion ")
					.append("from historydocs h, person p,tbl_cargo c ")
					.append("where h.nameUser=p.nameUser ")
					.append("and cast(p.cargo as int) = c.idCargo ")
					.append("and h.idnode=")
					.append(idDoc)
					.append(" ")
					.append("and (h.type='5' OR h.type='7') and h.mayorver='")
					.append(mayorVer)
					.append("' ")
					.append("group by p.nameUser,h.nameuser, ")
					.append(JDBCUtil.concatSql(new String[] { "p.Apellidos",
							"p.nombres" }, null)).append(", c.cargo, h.type ");

			log.debug("[getCollaboratorDocument] query = " + query);
			Vector items = new Vector();
			Vector data = JDBCUtil.doQueryVector(query.toString(),Thread.currentThread().getStackTrace()[1].getMethodName());
			BeanFirmsDoc bean = null;

			for (int i = 0; i < data.size(); i++) {
				Properties properties = (Properties) data.elementAt(i);
				String idUser = properties.getProperty("idUser");
				if (items.contains(idUser)) {
					bean = (BeanFirmsDoc) result.get(items.indexOf(idUser));
				} else {
					bean = new BeanFirmsDoc(idUser,
							properties.getProperty("usuario"),
							properties.getProperty("cargo"));
				}
				bean.setDateReplied(ToolsHTML.formatDateShow(
						properties.getProperty("dateReplied"), true));
				int repet = Integer.parseInt(properties
						.getProperty("repeticion"));
				bean.setContador(bean.getContador()
						+ (properties.getProperty("type").equals("5") ? repet
								: (-1) * repet));

				items.add(idUser);
				result.add(bean);
			}

			items = new Vector();
			for (int i = 0; i < result.size(); i++) {
				bean = (BeanFirmsDoc) result.get(i);
				if (!items.contains(bean.getIdUser())) {
					items.add(bean.getIdUser());
					if (bean.getContador() > 0) {
						resultFiltrado.add(bean);
					}
				}
			}

		} catch (Exception ex) {
			log.error(ex.getMessage());
			ex.printStackTrace();
		}
		return resultFiltrado;
	}

	/**
	 * Este m�todo permite obtener los revisores del documento dado
	 */
	public static Collection getReviewsDocument(String idDoc, String numVer,
			String idLastVersion) {
		Vector result = new Vector();
		try {
			StringBuilder sql = new StringBuilder(2048)
					.append(" SELECT uwf.idUser,");
			switch (Constants.MANEJADOR_ACTUAL) {
			case Constants.MANEJADOR_MSSQL:
				sql.append("(p.Apellidos + ' ' + p.nombres) as usuario,");
				break;
			case Constants.MANEJADOR_POSTGRES:
				sql.append("(p.Apellidos || ' ' || p.nombres) as usuario,");
				break;
			case Constants.MANEJADOR_MYSQL:
				sql.append("CONCAT(p.Apellidos , ' ' , p.nombres) as usuario,");
				break;
			}
			sql.append("c.cargo,dateReplied,p.accountActive")
					.append(" FROM user_workflows uwf,person p,workflows wf,documents d,versiondoc vd,tbl_cargo c")
					.append(" WHERE uwf.idUser = p.nameUser AND uwf.idWorkFlow  = wf.idWorkFlow")
					.append(" AND cast(p.cargo as int) = c.idCargo")
					.append(" AND wf.idDocument = vd.numDoc AND vd.numDoc=")
					.append(idDoc)
					.append(" AND vd.statu = '")
					.append(HandlerDocuments.docApproved)
					.append("' ")
					.append(" AND d.numGen = vd.numDoc AND ")
					.append(" uwf.statu = '")
					.append(HandlerWorkFlows.wfuAcepted)
					.append("' ")
					.append(" AND wf.idVersion = ")
					.append(numVer)
					.append(" AND uwf.idWorkFlow  =  (SELECT max(idworkflow) FROM workflows wf1 WHERE wf1.type = '0' AND wf1.result = '")
					.append(HandlerWorkFlows.response).append("' ")
					.append(" AND wf1.idDocument = cast(").append(idDoc)
					.append(" as int) ");
			if (!ToolsHTML.isEmptyOrNull(idLastVersion)) {
				sql.append("  AND wf1.idLastVersion = ").append(idLastVersion);
			} else {
				sql.append("  AND idLastVersion <> ").append(numVer);
			}
			sql.append(")");

			/*
			 * Se consulta el ultimo flujo de REVISION y el id de este flujo
			 * debe coincidir con el flujo anterior inmediato del ultimo flujo
			 * de aprobacion
			 */
			String limiteMsSql = "";
			String limitePostgres = "";
			switch (Constants.MANEJADOR_ACTUAL) {
			case Constants.MANEJADOR_MSSQL:
				limiteMsSql = " top 1 ";
				break;
			case Constants.MANEJADOR_POSTGRES:
				limitePostgres = " limit 1 ";
				break;
			case Constants.MANEJADOR_MYSQL:
				limitePostgres = " limit 1 ";
				break;
			}
			sql.append(" AND uwf.idWorkFlow  = (SELECT ")
					.append(limiteMsSql)
					.append(" wf1.idworkflow FROM workflows wf1 WHERE wf1.result = '")
					.append(HandlerWorkFlows.response).append("' ")
					.append(" AND wf1.idDocument = ").append(idDoc);
			if (!ToolsHTML.isEmptyOrNull(idLastVersion)) {
				sql.append("  AND wf1.idLastVersion = ").append(idLastVersion);
			} else {
				sql.append("  AND idLastVersion <> ").append(numVer);
			}
			sql.append(
					" AND wf1.idWorkFlow  <> (SELECT max(wf2.idworkflow) FROM workflows wf2 WHERE wf2.type = '1' AND wf2.result = '")
					.append(HandlerWorkFlows.response).append("' ")
					.append(" AND wf2.idDocument = cast(").append(idDoc)
					.append(" as int) ");
			if (!ToolsHTML.isEmptyOrNull(idLastVersion)) {
				sql.append("  AND wf2.idLastVersion = ").append(idLastVersion);
			} else {
				sql.append("  AND idLastVersion <> ").append(numVer);
			}
			sql.append(")").append(" ORDER BY wf1.idworkflow desc ")
					.append(limitePostgres).append(")")
					.append(" AND p.accountActive = '")
					.append(Constants.permission).append("'");

			StringBuilder query = new StringBuilder(2048)
					.append(sql.toString()).append(" UNION ")
					.append(sql.toString()).append(" ORDER BY dateReplied ");
			log.debug("[getReviewsDocument] query = " + query);

			Vector items = new Vector();
			Vector data = JDBCUtil.doQueryVector(query.toString(),Thread.currentThread().getStackTrace()[1].getMethodName());
			for (int i = 0; i < data.size(); i++) {
				Properties properties = (Properties) data.elementAt(i);
				String idUser = properties.getProperty("idUser");
				if (Constants.notPermissionSt.compareTo(properties
						.getProperty("accountActive")) == 0
						&& items.contains(idUser)) {
					continue;
				} else {
					items.add(idUser);
					BeanFirmsDoc bean = new BeanFirmsDoc(idUser,
							properties.getProperty("usuario"),
							properties.getProperty("cargo"));
					bean.setDateReplied(ToolsHTML.formatDateShow(
							properties.getProperty("dateReplied"), true));
					result.add(bean);
				}
			}

		} catch (Exception ex) {
			log.error(ex.getMessage());
			ex.printStackTrace();
		}
		return result;
	}

	/**
	 * Este m�todo permite obtener los firmantes del documento dado
	 */
	public static Collection<BeanFirmsDoc> getSignatureOwnerDocument(String idDoc,
			String numVer, String statu) {
		Vector result = new Vector();
		try {
			StringBuilder sql = new StringBuilder(2048)
					.append("SELECT p.idPerson, ");
			switch (Constants.MANEJADOR_ACTUAL) {
			case Constants.MANEJADOR_MSSQL:
				sql.append("(p.Apellidos + ' ' + p.nombres) as usuario,");
				break;
			case Constants.MANEJADOR_POSTGRES:
				sql.append("(p.Apellidos || ' ' || p.nombres) as usuario,");
				break;
			case Constants.MANEJADOR_MYSQL:
				sql.append("CONCAT(p.Apellidos , ' ' , p.nombres) as usuario,");
				break;
			}
			sql.append(
					" c.cargo,dateApproved,p.accountActive, vd.statu as estado ")
					.append(" FROM person p,documents d,versiondoc vd,tbl_cargo c ")
					.append(" WHERE cast(p.cargo as int) = c.idCargo AND vd.numDoc=")
					.append(idDoc);
			if (!ToolsHTML.isEmptyOrNull(numVer))
				sql.append(" AND vd.numVer = ").append(numVer);
			sql.append(" AND d.numGen = vd.numDoc ")
					.append(" AND p.accountActive = '")
					.append(Constants.permission).append("'")
					.append(" AND p.nameUser = d.owner ");
			log.debug("[getFirmOwnerDocument] sql = " + sql);
			Vector data = JDBCUtil.doQueryVector(sql.toString(),Thread.currentThread().getStackTrace()[1].getMethodName());
			if (data != null && data.size() > 0) {
				Properties properties = (Properties) data.elementAt(0);
				String idUser = properties.getProperty("idPerson");
				String estado = properties.getProperty("estado");
				// Solo si el documento esta aprobado u obsoleto se muestra el
				// propietario
				if (estado.equals(HandlerDocuments.docApproved)
						|| estado.equals(HandlerDocuments.docObs)) {
					BeanFirmsDoc bean = new BeanFirmsDoc(idUser,
							properties.getProperty("usuario"),
							properties.getProperty("cargo"));
					bean.setDateReplied(ToolsHTML.formatDateShow(
							properties.getProperty("dateApproved"), true));
					result.add(bean);
				}
			}

		} catch (Exception ex) {
			log.error(ex.getMessage());
			ex.printStackTrace();
		}
		return result;
	}

	/**
	 * Este m�todo permite obtener los firmantes del documento dado
	 */
	public static BeanFirmsDoc getSignatureResponsibleSacop(String idDoc,
			String numVer, String statu) {
		BeanFirmsDoc bean = null;
		try {
			StringBuilder sql = new StringBuilder(2048)
					.append("SELECT p.idPerson, ");
			switch (Constants.MANEJADOR_ACTUAL) {
			case Constants.MANEJADOR_MSSQL:
				sql.append("(p.Apellidos + ' ' + p.nombres) as usuario,");
				break;
			case Constants.MANEJADOR_POSTGRES:
				sql.append("(p.Apellidos || ' ' || p.nombres) as usuario,");
				break;
			case Constants.MANEJADOR_MYSQL:
				sql.append("CONCAT(p.Apellidos , ' ' , p.nombres) as usuario,");
				break;
			}
			sql.append(
					" c.cargo,dateApproved,p.accountActive, vd.statu as estado ")
					.append(" FROM person p,documents d,versiondoc vd,tbl_cargo c, tbl_planillasacop1 sp ")
					.append(" WHERE cast(p.cargo as int) = c.idCargo AND vd.numDoc=")
					.append(idDoc);
			if (!ToolsHTML.isEmptyOrNull(numVer))
				sql.append(" AND vd.numVer = ").append(numVer);
			sql.append(" AND d.numGen = vd.numDoc ")
					.append(" AND p.accountActive = '")
					.append(Constants.permission).append("'")
					.append(" AND p.idPerson  = sp.respblearea ")
					.append(" AND sp.idRegisterGenerated = vd.numDoc ");
			log.debug("[getFirmOwnerDocument] sql = " + sql);
			Vector data = JDBCUtil.doQueryVector(sql.toString(),Thread.currentThread().getStackTrace()[1].getMethodName());
			if (data != null && data.size() > 0) {
				Properties properties = (Properties) data.elementAt(0);
				String idUser = properties.getProperty("idPerson");
				String estado = properties.getProperty("estado");
				// Solo si el documento esta aprobado u obsoleto se muestra el
				// propietario
				if (estado.equals(HandlerDocuments.docApproved)
						|| estado.equals(HandlerDocuments.docObs)) {
					bean = new BeanFirmsDoc(idUser,
							properties.getProperty("usuario"),
							properties.getProperty("cargo"));
					bean.setDateReplied(ToolsHTML.formatDateShow(
							properties.getProperty("dateApproved"), true));
				}
			}

		} catch (Exception ex) {
			log.error(ex.getMessage());
			ex.printStackTrace();
		}
		return bean;
	}
	
	/**
	 * Este m�todo permite obtener los firmantes del documento dado
	 */
	public static Collection getFirmsDocument(String idDoc, String numVer,
			String idLastVersion) {
		Vector result = new Vector();
		try {
			StringBuilder sql = new StringBuilder(2048)
					.append(" SELECT uwf.idUser,");
			switch (Constants.MANEJADOR_ACTUAL) {
			case Constants.MANEJADOR_MSSQL:
				sql.append("(p.Apellidos + ' ' + p.nombres) as usuario,");
				break;
			case Constants.MANEJADOR_POSTGRES:
				sql.append("(p.Apellidos || ' ' || p.nombres) as usuario,");
				break;
			case Constants.MANEJADOR_MYSQL:
				sql.append("CONCAT(p.Apellidos , ' ' , p.nombres) as usuario,");
				break;
			}
			sql.append("c.cargo,dateReplied,p.accountActive,uwf.editor ")
					.append(" FROM user_workflows uwf,person p,workflows wf,documents d,versiondoc vd,tbl_cargo c")
					.append(" WHERE uwf.idUser = p.nameUser AND uwf.idWorkFlow  = wf.idWorkFlow")
					.append(" AND cast(p.cargo as int) = c.idCargo")
					.append(" AND wf.idDocument = vd.numDoc AND vd.numDoc=")
					.append(idDoc)
					.append(" AND vd.statu = '")
					.append(HandlerDocuments.docApproved)
					.append("' ")
					.append(" AND d.numGen = vd.numDoc AND ")
					.append(" uwf.statu = '")
					.append(HandlerWorkFlows.wfuAcepted)
					.append("' ")
					.append(" AND wf.idVersion =")
					.append(numVer)
					//TODO ydavila Flujos de cambio y eliminaci�n no deben visualizarse en la hoja de firmantes
					.append(" AND uwf.idWorkFlow  =  (SELECT max(idworkflow) FROM workflows wf1 WHERE wf1.type = '1' AND wf1.result = '")
					//.append(" AND uwf.idWorkFlow  =  (SELECT max(idworkflow) FROM workflows wf1 WHERE wf1.type = '1' AND wf1.subtype < '3' AND wf1.result = '")
					.append(HandlerWorkFlows.response).append("' ")
					.append(" AND wf1.idDocument = cast(").append(idDoc)
					.append(" as int) ");
			if (!ToolsHTML.isEmptyOrNull(idLastVersion)) {
				sql.append("  AND wf1.idLastVersion = ").append(idLastVersion);
			} else {
				sql.append("  AND idLastVersion <> ").append(numVer);
			}
			sql.append(")").append(" AND p.accountActive = '")
					.append(Constants.permission).append("'");

			StringBuilder query = new StringBuilder(2048)
					.append(sql.toString()).append(" UNION ")
					.append(sql.toString()).append(" ORDER BY dateReplied ");
			log.debug("[getFirmsDocument] query = " + query);
			Vector items = new Vector();
			Vector data = JDBCUtil.doQueryVector(query.toString(),Thread.currentThread().getStackTrace()[1].getMethodName());
			for (int i = 0; i < data.size(); i++) {
				Properties properties = (Properties) data.elementAt(i);
				String idUser = properties.getProperty("idUser");
				if (Constants.notPermissionSt.compareTo(properties
						.getProperty("accountActive")) == 0
						&& items.contains(idUser)) {
					continue;
				} else {
					items.add(idUser);
					BeanFirmsDoc bean = new BeanFirmsDoc(idUser,
							properties.getProperty("usuario"),
							properties.getProperty("cargo"),
							properties.getProperty("editor"));
					bean.setDateReplied(ToolsHTML.formatDateShow(
							properties.getProperty("dateReplied"), true));
					result.add(bean);
				}
			}

		} catch (Exception ex) {
			log.error(ex.getMessage());
			ex.printStackTrace();
		}
		return result;
	}

	/**
	 * 
	 * @param idDoc
	 * @param numVer
	 * @param idLastVersion
	 * @return
	 */
	public static Collection getFirmsFlexFlow(String idDoc, String numVer,
			String idLastVersion) {
		Vector result = new Vector();
		try {
			StringBuilder sql = new StringBuilder(2048)
					.append(" SELECT uwf.idUser,");
			switch (Constants.MANEJADOR_ACTUAL) {
			case Constants.MANEJADOR_MSSQL:
				sql.append("(p.Apellidos + ' ' + p.nombres) As usuario,");
				break;
			case Constants.MANEJADOR_POSTGRES:
				sql.append("(p.Apellidos || ' ' || p.nombres) As usuario,");
				break;
			case Constants.MANEJADOR_MYSQL:
				sql.append("CONCAT(p.Apellidos , ' ' , p.nombres) As usuario,");
				break;
			}
			sql.append("c.cargo,dateReplied,p.accountActive")
					.append(" FROM user_flexworkflows uwf,person p,flexworkflow wf,documents d,versiondoc vd,tbl_cargo c")
					.append(" WHERE uwf.idUser = p.nameUser AND uwf.idWorkFlow  = wf.idWorkFlow")
					.append(" AND cast(p.cargo as int) = c.idCargo")
					.append(" AND wf.idDocument = vd.numDoc AND vd.numDoc=")
					.append(idDoc)
					.append(" AND vd.statu = '")
					.append(HandlerDocuments.docApproved)
					.append("' ")
					.append(" AND d.numGen = vd.numDoc AND ")
					// sql.append(" uwf.statu IN
					// (").append(HandlerWorkFlows.wfuAcepted).append(",");
					// sql.append(HandlerWorkFlows.wfuRejected).append(",").append(HandlerWorkFlows.wfuReInit).append(")");
					.append(" uwf.statu = '")
					.append(HandlerWorkFlows.wfuAcepted)
					.append("' ")
					.append(" AND wf.idVersion = ")
					.append(numVer)
					.append(" AND uwf.idWorkFlow  IN (SELECT idworkflow FROM flexworkflow wfi WHERE wfi.result = '")
					.append(HandlerWorkFlows.response)
					.append("' ")
					.append(" AND wfi.idDocument = ")
					.append(idDoc)
					.append(" AND uwf.wfactive = '1'")
					.append(" AND wfi.IDFlexFlow = (SELECT MAX(IDFlexFlow) FROM flexworkflow wfii")
					.append(" WHERE wfii.idDocument = wfi.idDocument");
			if (!ToolsHTML.isEmptyOrNull(idLastVersion)) {
				sql.append("  AND wfii.idLastVersion = ").append(idLastVersion);
			} else {
				sql.append("  AND wfii.idLastVersion <> ").append(numVer);
			}
			sql.append("))");

			StringBuilder query = new StringBuilder(2048)
					.append(sql.toString()).append(" AND p.accountActive = '")
					.append(Constants.permission).append("'").append(" UNION ")
					.append(sql.toString()).append(" ORDER BY dateReplied ");
			log.debug("[getFirmsDocument] query = " + query);
			Vector items = new Vector();
			Vector data = JDBCUtil.doQueryVector(query.toString(),Thread.currentThread().getStackTrace()[1].getMethodName());

			for (int i = 0; i < data.size(); i++) {
				Properties properties = (Properties) data.elementAt(i);
				String idUser = properties.getProperty("idUser");
				if (Constants.notPermissionSt.compareTo(properties
						.getProperty("accountActive")) == 0
						&& items.contains(idUser)) {
					continue;
				} else {
					items.add(idUser);
					BeanFirmsDoc bean = new BeanFirmsDoc(idUser,
							properties.getProperty("usuario"),
							properties.getProperty("cargo"));
					bean.setDateReplied(ToolsHTML.formatDateShow(
							properties.getProperty("dateReplied"), true));
					result.add(bean);
				}
			}

		} catch (Exception ex) {
			log.error(ex.getMessage());
			ex.printStackTrace();
		}
		return result;
	}

	/**
	 * 
	 * @param userFlexWorkFlowsTO
	 * @return
	 */
	public static Collection getFirmsFlexFlow(
			UserFlexWorkFlowsTO userFlexWorkFlowsTO) {

		Vector result = new Vector();
		try {
			StringBuilder sql = new StringBuilder(1024).append(" SELECT * ")
					.append(" FROM user_flexworkflows uwf ")
					.append(" WHERE uwf.idWorkFlow= ")
					.append(userFlexWorkFlowsTO.getIdWorkFlow());
			Vector items = new Vector();
			Vector data = JDBCUtil.doQueryVector(sql.toString(),Thread.currentThread().getStackTrace()[1].getMethodName());
			for (int i = 0; i < data.size(); i++) {
				Properties properties = (Properties) data.elementAt(i);
				String idUser = properties.getProperty("idUser");
				if (Constants.notPermissionSt.compareTo(properties
						.getProperty("accountActive")) == 0
						&& items.contains(idUser)) {
					continue;
				} else {
					items.add(idUser);
					BeanFirmsDoc bean = new BeanFirmsDoc(idUser,
							properties.getProperty("usuario"),
							properties.getProperty("cargo"));
					bean.setDateReplied(ToolsHTML.formatDateShow(
							properties.getProperty("dateReplied"), true));
					result.add(bean);
				}
			}

		} catch (Exception ex) {
			log.error(ex.getMessage());
			ex.printStackTrace();
		}
		return result;
	}

	/**
	 * M�todo que respalda las versiones del documento
	 * 
	 * @param con
	 * @param id
	 * @param stream
	 * @throws Exception
	 */
	public static synchronized void backUpFile(Connection con, long id,
			int idDocument, String mayorVer, String minorVer,
			InputStream stream, Timestamp dateCreated, InputStream streamView) throws Exception {
		
		StringBuffer query = new StringBuffer();
		query.append("INSERT INTO backupfiles (IDBackup,IdDocument,MayorVer,MinorVer,dateCreated) VALUES(?,?,?,?,?) ");
		PreparedStatement pstmt = con.prepareStatement(JDBCUtil.replaceCastMysql(query.toString()));
		pstmt.setLong(1, id);
		pstmt.setLong(2, idDocument);
		pstmt.setString(3, mayorVer);
		pstmt.setString(4, minorVer);
		pstmt.setTimestamp(5, dateCreated);
		pstmt.executeUpdate();

		DbUtils.closeQuietly(pstmt);

		System.out.println("Haciendo backup del archivo");
		Archivo.writeDocumentToDisk(con, null, "backupfiles", (int) id, stream);

		if(streamView!=null) {
			// crea un respado del archivo view
			System.out.println("Haciendo backup del archivo de visualizacion");
			Archivo.writeDocumentToDisk(con, null, "BackUpFilesView", (int) id, streamView);
		}
	}

	/**
	 * 
	 * @param con
	 * @param IDBackup
	 * @return
	 */
	public static InputStream getFileBDBackUp(Connection con, String IDBackup) {
		try {
			return Archivo.readDocumentFromDisk("backupfiles",
					Integer.parseInt(IDBackup));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 
	 * @param con
	 * @param IDBackup
	 * @return
	 */
	public static InputStream getFileBDBackUpView(Connection con, String IDBackup) {
		try {
			return Archivo.readDocumentFromDisk("BackUpFilesView",
					Integer.parseInt(IDBackup));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 
	 * @param idDoc
	 * @return
	 * @throws Exception
	 */
	public static String getMaxBackUpFile(String idDoc) throws Exception {
		StringBuilder query = new StringBuilder(1024)
				.append("SELECT MAX(IDBackup) AS IDBackup FROM backupfiles WHERE IdDocument = ")
				.append(idDoc);
		Properties prop = JDBCUtil.doQueryOneRow(query.toString(),Thread.currentThread().getStackTrace()[1].getMethodName());
		if ("false".equalsIgnoreCase(prop.getProperty("isEmpty"))) {
			return prop.getProperty("IDBackup");
		}
		return "0";
	}

	/**
	 * 
	 * @param IDBackup
	 * @return
	 * @throws Exception
	 */
	public static CheckOutDocForm getDataBackUpFile(String IDBackup)
			throws Exception {
		StringBuilder query = new StringBuilder(1024)
				.append("SELECT MayorVer,MinorVer,dateCreated FROM backupfiles WHERE IDBackup = ")
				.append(IDBackup);
		Properties prop = JDBCUtil.doQueryOneRow(query.toString(),Thread.currentThread().getStackTrace()[1].getMethodName());
		if ("false".equalsIgnoreCase(prop.getProperty("isEmpty"))) {
			CheckOutDocForm forma = new CheckOutDocForm();
			forma.setMinorVer(prop.getProperty("MinorVer"));
			forma.setMayorVer(prop.getProperty("MayorVer"));
			forma.setDateCreated(ToolsHTML.formatDateShow(
					prop.getProperty("dateCreated"), true));
			return forma;
		}
		return null;
	}

	/**
	 * 
	 * @param con
	 * @param dataFile
	 * @param idCheckOut
	 * @throws Exception
	 */
	public static synchronized void updateDocCheckOut(Connection con,
			InputStream dataFile, int idCheckOut) throws Exception {
		Archivo.writeDocumentToDisk("doccheckout", idCheckOut, dataFile);
	}
	public static synchronized void updateDocCheckOutView(Connection con,
			InputStream dataFile, int idCheckOut) throws Exception {
		Archivo.writeDocumentToDisk("doccheckoutview", idCheckOut, dataFile);
	}

	/**
	 * 
	 * @param idDoc
	 * @param numVer
	 * @return
	 */
	public static Collection getLastVersions(String idDoc, String numVer) {
		Vector result = new Vector();
		try {
			StringBuilder sql = new StringBuilder(
					"SELECT numVer FROM versiondoc WHERE numVer < ")
					.append(numVer).append(" AND numDoc = ").append(idDoc)
					.append(" ORDER BY numVer DESC");
			Vector datos = JDBCUtil.doQueryVector(sql.toString(),Thread.currentThread().getStackTrace()[1].getMethodName());
			for (int row = 0; row < datos.size(); row++) {
				Properties properties = (Properties) datos.elementAt(row);
				String idVer = properties.getProperty("numVer");
				result.add(idVer);
			}
		} catch (Exception ex) {
			log.error(ex.getMessage());
			ex.printStackTrace();
		}
		return result;
	}

	/**
	 * Este procedimiento permite verificar la versi�n del documento (el estatu)
	 * y carga los firmantes del mismo en el caso que corresponde (Documento
	 * Aprobado)
	 */
	public static Collection loadFirmsToVersionDoc(String idDoc, String numVer,
			String statu, boolean isFlexFlow) {
		Collection result = null;
		if (!ToolsHTML.isEmptyOrNull(numVer)) {
			try {
				if (docApproved.equalsIgnoreCase(statu)
						|| docObs.equalsIgnoreCase(statu)) {
					Vector data = (Vector) getLastVersions(idDoc, numVer);
					String idLastVersion = null;
					if (!data.isEmpty()) {
						idLastVersion = (String) data.get(0);
					}
					if (isFlexFlow) {
						result = getFirmsFlexFlow(idDoc, numVer, idLastVersion);
					} else {
						result = getFirmsDocument(idDoc, numVer, idLastVersion);
					}
					if (result.size() == 0) {
						if (!data.isEmpty()) {
							String idVerAnt = (String) data.get(0);
							if (isFlexFlow) {
								log.debug("idVerAnt: " + idVerAnt);
								result = getFirmsFlexFlow(idDoc, idVerAnt,
										idVerAnt);
							} else {
								result = getFirmsDocument(idDoc, idVerAnt,
										idVerAnt);
							}
						}
					}
				}
			} catch (Exception ex) {
				log.error(ex.getMessage());
				ex.printStackTrace();
			}
		}
		return result;
	}

	/**
	 * Este procedimiento permite verificar la versi�n del documento (el estatu)
	 * y carga los revisores del mismo en el caso que corresponde (Documento
	 * Aprobado). No aplica para FTP
	 */
	public static Collection loadFirmsReviewsToVersionDoc(String idDoc,
			String numVer, String statu) {
		Collection result = null;
		if (!ToolsHTML.isEmptyOrNull(numVer)) {
			try {
				if (docApproved.equalsIgnoreCase(statu)
						|| docObs.equalsIgnoreCase(statu)) {
					Vector data = (Vector) getLastVersions(idDoc, numVer);
					String idLastVersion = null;
					if (!data.isEmpty()) {
						idLastVersion = (String) data.get(0);
					}

					result = getReviewsDocument(idDoc, numVer, idLastVersion);

					if (result.size() == 0) {
						if (!data.isEmpty()) {
							String idVerAnt = (String) data.get(0);
							result = getReviewsDocument(idDoc, idVerAnt,
									idVerAnt);
						}
					}
				}
			} catch (Exception ex) {
				log.error(ex.getMessage());
				ex.printStackTrace();
			}
		}
		return result;
	}

	/**
	 * Este procedimiento permite verificar la versi�n del documento (el estatu)
	 * y carga los colaboradores del mismo en el caso que corresponde (Documento
	 * Aprobado). No aplica para FTP
	 */
	public static Collection loadFirmsCollaboratorToVersionDoc(String idDoc,
			String numVer, String statu) {
		Collection result = null;
		if (!ToolsHTML.isEmptyOrNull(numVer)) {
			try {
				if (docApproved.equalsIgnoreCase(statu)
						|| docObs.equalsIgnoreCase(statu)) {
					Vector data = (Vector) getLastVersions(idDoc, numVer);
					String idLastVersion = null;
					if (!data.isEmpty()) {
						idLastVersion = (String) data.get(0);
					}

					result = getCollaboratorDocument(idDoc, numVer);

					if (result.size() == 0) {
						if (!data.isEmpty()) {
							String idVerAnt = (String) data.get(0);
							result = getCollaboratorDocument(idDoc, idVerAnt);
						}
					}
				}
			} catch (Exception ex) {
				log.error(ex.getMessage());
				ex.printStackTrace();
			}
		}
		return result;
	}

	/*
	 * Env�a correos de notificaci�n de documentos APROBADOS que est�n pr�ximos
	 * a expirar, al propietario del documento. Se env�a el correo n unidades de
	 * tiempo antes que expire (campos cantExpDoc y unitExpDoc de la tabla
	 * Location). Unidades: minutos, horas, d�a, mes, a�o. Se env�a el correo si
	 * est� en 0 el campo notifyEmail de la tabla parameters. Se valida que no
	 * se haya enviado ya un correo (campo dayEmailNearExpireSendde la tabla
	 * versiondoc en null)
	 */
	public static ArrayList<Integer> getAllDocNearExpiration(Hashtable tree, Hashtable prefijos, boolean notifyEmail) {
		Connection con = null;
		PreparedStatement st = null;
		ResultSet rs = null;
		ArrayList<Integer> result = new ArrayList<Integer>();
		String PrefixNumber = "";

		try {
			String dateSystem = ToolsHTML.sdfShowConvert1.format(new Date());
			// String dateSystemDateOnly = "";
			// if(dateSystem!=null && dateSystem.length()>9) dateSystemDateOnly
			// =
			// dateSystem.substring(0,10);
			StringBuilder sql = new StringBuilder(2048);
			sql.append("SELECT DISTINCT d.numGen,d.idNode,vd.numVer,vd.dateExpires, p.email,d.nameDocument,");
			switch (Constants.MANEJADOR_ACTUAL) {
			case Constants.MANEJADOR_MSSQL:
				sql.append(" (p.Apellidos+' '+p.Nombres) AS namePerson,");
				break;
			case Constants.MANEJADOR_POSTGRES:
				sql.append(" (p.Apellidos||' '||p.Nombres) AS namePerson,");
				break;
			case Constants.MANEJADOR_MYSQL:
				sql.append(" CONCAT(p.Apellidos,' ',p.Nombres) AS namePerson,");
				break;
			}
			sql.append(" vd.MayorVer,vd.MinorVer,d.number as number1,d.prefix")
					.append(" FROM versiondoc vd,documents d,person p")
					.append(" WHERE d.numGen = vd.numDoc")
					.append(" AND d.type <>'")
					.append(HandlerDocuments.TypeDocumentsImpresion)
					.append("'  ")
					.append(" AND vd.numVer = (SELECT MAX(vdi.numVer) FROM versiondoc vdi")
					.append(" WHERE vdi.numDoc = d.numGen and vdi.statu='")
					.append(HandlerDocuments.docApproved).append("')")
					.append(" AND d.owner= p.nameuser ")
					.append(" AND vd.dateExpires BETWEEN ? AND ?")
					.append(" AND d.active = '").append(Constants.permission)
					.append("'")
					.append(" AND vd.dayEmailNearExpireSend is null");
			Locale defaultLocale = new Locale(
					DesigeConf.getProperty("language.Default"),
					DesigeConf.getProperty("country.Default"));

			// Hashtable tree =
			// HandlerStruct.loadAllNodes(null,DesigeConf.getProperty("application.admon"),DesigeConf.getProperty("application.admon"),false);
			Hashtable locations = HandlerStruct.getAllLocationsIds();
			Date dateMinor = ToolsHTML.calculateDateMinor(locations);
			Date dateMayor = ToolsHTML.calculateDateMayor(locations);
			log.info("DateMinor: " + dateMinor);
			log.info("DateMayor: " + dateMayor);
			con = JDBCUtil.getConnection(Thread.currentThread().getStackTrace()[1].getMethodName());
			// st = con.prepareStatement(sql.toString());
			// st.setTimestamp(1, new Timestamp(dateMinor.getTime()));
			// st.setTimestamp(2, new Timestamp(dateMayor.getTime()));
			// rs = st.executeQuery();

			ArrayList params = new ArrayList();
			params.add(new Timestamp(dateMinor.getTime()));
			params.add(new Timestamp(dateMayor.getTime()));

			rs = JDBCUtil.executeQuery(sql, params, con, Thread.currentThread().getStackTrace()[1].getMethodName());

			int units = 0;
			java.util.Date hoy = new Date();
			String hoySt = ToolsHTML.sdfShowConvert.format(hoy);
			while (rs.next()) {
				// int idDoc = rs.getInt("numGen");
				int idNode = rs.getInt("idNode");
				// int idVer = rs.getInt("numVer");

				// Date dateExp = rs.getDate("dateExpires");
				Timestamp dateExp = rs.getTimestamp("dateExpires");
				String dateExpDoc = ToolsHTML.sdfShowConvert.format(new Date(
						dateExp.getTime()));

				// Se Busca el Id de la Localidad en donde se encuentra el
				// Documento
				String idLocation = HandlerStruct.getIdLocationToNode(tree,
						String.valueOf(idNode));
				// Obtenemos los datos de dicha localidad
				BaseStructForm localidad = (BaseStructForm) locations
						.get(idLocation);
				units = ToolsHTML.getUnits(localidad.getUnitExpDoc());
				// Calculamos cual es la Fecha desde la cual el documento debe
				// ser verificado
				Date calculada = ToolsHTML.sumUnitsToDate(units, hoy, Integer.parseInt(localidad.getCantExpDoc()));
				// Si la Fecha de Expiracion del Documento es mayor a la Fecha
				// Arriba calculada
				// entonces de debe proceder a enviar el mail al propietario del
				// Documento

				result.add(rs.getInt("numGen")); // tomamos el documento por vencer

				
				if ((dateExpDoc.compareTo(ToolsHTML.sdfShowConvert.format(calculada)) < 0)
						&& (dateExpDoc.compareTo(hoySt) >= 0)) {
					// //System.out.println("-la Fecha de Expiracion del
					// Documento
					// es mayor a la Fecha
					// Arriba calculada");
					BeanNotifiedWF userMailExp = new BeanNotifiedWF();
					String email = rs.getString("email");
					String namePerson = rs.getString("namePerson");
					userMailExp.setEmail((email != null ? email : HandlerParameters.PARAMETROS.getMailAccount()));
					String namedocument = rs.getString("nameDocument");
					String version = rs.getString("MayorVer") + "."
							+ rs.getString("MinorVer");
					int numVer = rs.getInt("numVer");
					
					if (notifyEmail) {
						String idNodeST = String.valueOf(idNode);
						if (tree != null) {
							if (!prefijos.containsKey(idNodeST)) {
								PrefixNumber = ToolsHTML.getPrefixToDoc(tree,
										idNodeST);
								if (!ToolsHTML.isEmptyOrNull(PrefixNumber))
									try {
										prefijos.put(
												new Integer(idNode),
												new Long(
														Long.parseLong(PrefixNumber)));
									} catch (java.lang.NumberFormatException e) {
										//
									}
							} else {
								PrefixNumber = (String) prefijos.get(idNodeST);
							}
						} else {
							PrefixNumber = rs.getString("prefix");
						}
						String docVencidoI = "";
						String docsversion = "";
						String docVencidoII = "";
						String title_toExp = "";
						String nameUser = "";
						try {
							ResourceBundle rb = ResourceBundle.getBundle(
									"LoginBundle", defaultLocale);
							docVencidoI = rb.getString("mail.docVencidoI");
							docsversion = rb.getString("mail.docsversion");
							docVencidoII = rb.getString("mail.docVencidoII");
							nameUser = rb.getString("mail.nameUser");
							title_toExp = rb.getString("title_toExp");
						} catch (Exception e) {
						}

						String number = rs.getString("number1") != null ? rs
								.getString("number1") : "";
						PrefixNumber = PrefixNumber + number;
						StringBuilder mensaje = new StringBuilder(1024)
								.append(namePerson).append(" ").append("<br/>")
								.append(docVencidoI).append(" ")
								.append("<br/>").append(namedocument)
								.append(" ").append(docsversion).append(" ")
								.append(version).append("<br/>")
								.append(docVencidoII).append(" ")
								.append(ToolsHTML.sdfShow.format(dateExp));
						userMailExp.setComments(mensaje.toString());
						log.debug("userMailExp.getComments()="
								+ userMailExp.getComments());
						log.debug("userMailExp.getEmail()="
								+ userMailExp.getEmail());
						HandlerWorkFlows.notifiedUsers(title_toExp + " "
								+ PrefixNumber, nameUser, HandlerParameters.PARAMETROS.getMailAccount(), userMailExp.getEmail(), userMailExp
								.getComments());
					}

					// Se actualiza el campo dayEmailNearExpireSend de la tabla
					// versiondoc con la
					// fecha actual
					HandlerStruct.actualizarVersionDoc(numVer,
							"dayEmailNearExpireSend", dateSystem, 1);
					// //System.out.println("-se actualiza en versiondoc
					// dayEmailNearExpireSend con: "
					// + dateSystem);
				}
			}

			// BaseStructForm location = HandlerStruct.loadStruct(idLocation);
		} catch (Exception ex) {
			// log.error(ex.getMessage());
			ex.printStackTrace();
		} finally {
			setFinally(con, st, rs);
		}
		return result;
	}

	/**
	 * 
	 * @param usuario
	 * @param forma
	 * @throws Exception
	 */
	public static void isPrinting(Users usuario, BaseDocumentForm forma)
			throws Exception {

		// Se Procede a Verificar los Permisos sobre el Documento

		ToolsHTML tools = new ToolsHTML();
		PermissionUserForm perm = tools.getSecurityUserInDoc(usuario,
				forma.getIdDocument(), forma.getIdNode());

		boolean isAdmon = DesigeConf.getProperty("application.userAdmon")
				.compareTo(usuario.getUser()) == 0;
		
         // String respsolimpres = HandlerParameters.PARAMETROS.getRespsolimpres();
         // boolean isRespsolimpres = (respsolimpres!=null && !respsolimpres.trim().equals(""));
         // boolean isRespsolimpresFilterUser = false;
         // if(isRespsolimpres && usuario.getUser()!=null && usuario.getUser().equals(respsolimpres)) {
         // 	isRespsolimpresFilterUser = true;
         // }
		
		
        // isAdmon = (isAdmon || isRespsolimpresFilterUser);

		// Luis Cisneros
		// 24-03-07
		// La posibilidad de que el admin y el dueno del documento impriman sin
		// someter a un flujo, es conrolado por un parametro en la
		// configuracion.

		// Si es el Admin, es el propietario del Documento o tiene permiso de
		// Libre
		// Impresi�n se le asigna la Misma
		// if (isAdmon || (forma!=null &&
		// (forma.getOwner().trim().compareTo(usuario.getUser())==0))||

		boolean printOwnerAdmin = String.valueOf(HandlerParameters.PARAMETROS.getPrintOwnerAdmin()).equals("0");
		
		// printOwnerAdmin = (printOwnerAdmin || isRespsolimpresFilterUser);
		
		boolean isPrintable = false;
		// Si es el Admin, es el propietario del Documento o tiene permiso de
		// Libre
		// Impresi�n se le asigna la Misma
		forma.setPrinting(false);

		isPrintable = HandlerDocuments.isDocumentStatuVersionPrintable(
				Integer.parseInt(forma.getIdDocument()), forma.getNumVer());
		if (isPrintable) {
			if (((isAdmon || (forma != null && (forma.getOwner().trim()
					.compareTo(usuario.getUser()) == 0))) && printOwnerAdmin)
					|| (perm != null && perm.getToImpresion() == Constants.permission)) {
	
				forma.setPrinting(true);
			}
			
			
            // SOLO SI HAY NO HAY RESPONSABLE DE IMPRESION
			  String respsolimpres = HandlerParameters.PARAMETROS.getRespsolimpres();
	          boolean isRespsolimpres = (respsolimpres!=null && !respsolimpres.trim().equals(""));
	         // boolean isRespsolimpresFilterUser = false;
	         // if(isRespsolimpres && usuario.getUser()!=null && usuario.getUser().equals(respsolimpres)) {
	         // 	isRespsolimpresFilterUser = true;
	         // }
			
			
			
			if(!isRespsolimpres) {
				if (!forma.isPrinting()  ) {
					forma.setPrinting(loadsolicitudImpresion
							.existepermisoImpresion(forma.getIdDocument(),
									String.valueOf(usuario.getIdPerson())));
				}
			}
			
			
		}
	}

	/**
	 * 
	 * @param idVersionRejection
	 * @param idDocumentReject
	 * @param usuario
	 * @param idFlexFlow
	 * @return
	 * @throws ApplicationExceptionChecked
	 */
	public static synchronized boolean approvedDocumentRejection(
			String idVersionRejection, String idDocumentReject, Users usuario,
			String idFlexFlow) throws ApplicationExceptionChecked {
		boolean resp = false;
		Connection con = null;
		PreparedStatement st = null;
		try {
			ArrayList parametros = new ArrayList();

			if (true) {
				// buscamos el numero de documento
				StringBuilder query = new StringBuilder(
						"SELECT numdoc FROM versiondoc WHERE numver = ? ");
				parametros.add(Integer.valueOf(idVersionRejection));
				CachedRowSet crs = JDBCUtil.executeQuery(query, parametros, Thread.currentThread().getStackTrace()[1].getMethodName());
				if (!crs.next()) {
					throw new ApplicationExceptionChecked("E0028"); // :JAIRO:
																	// falta por
																	// catalogar
																	// el error
				}
				String idDocumentOfRejection = crs.getString("numdoc");

				// buscamos la version del documento rechazado
				StringBuilder queryVersionReject = new StringBuilder(
						"SELECT max(numver) as numver FROM versiondoc WHERE numdoc = ?");
				parametros.removeAll(parametros);
				parametros.add(Integer.valueOf(idDocumentReject));
				crs = JDBCUtil.executeQuery(queryVersionReject, parametros, Thread.currentThread().getStackTrace()[1].getMethodName());
				if (!crs.next()) {
					throw new ApplicationExceptionChecked("E0028"); // :JAIRO:
																	// falta por
																	// catalogar
																	// el error
				}
				String idVersionReject = crs.getString("numver");

				// Carga de la Versi�n del Documento
				con = JDBCUtil.getConnection(Thread.currentThread().getStackTrace()[1].getMethodName());
				con.setAutoCommit(false);

				// borramos si existe una relacion ya de este documento
				StringBuilder delete = new StringBuilder(
						"DELETE FROM docofrejection WHERE numgen=? ");
				st = con.prepareStatement(JDBCUtil.replaceCastMysql(delete.toString()));
				st.setInt(1, new Integer(idDocumentOfRejection));
				st.executeUpdate();

				// insertamos la relacion de documentos de rechazo
				StringBuilder insert = new StringBuilder(
						"INSERT INTO docofrejection(dateCreate,idFlexFlow,idUser,numgen,documentReject) VALUES(?,?,?,?,?) ");
				st = con.prepareStatement(JDBCUtil.replaceCastMysql(insert.toString()));
				st.setTimestamp(1,
						new Timestamp(new java.util.Date().getTime()));
				st.setInt(2, new Integer(idFlexFlow));
				st.setLong(3, usuario.getIdPerson());
				st.setInt(4, new Integer(idDocumentOfRejection));
				st.setInt(5, new Integer(idDocumentReject));
				st.executeUpdate();
				con.commit();

				// insertamos la relacion a documentos relacionados
				StringBuilder insertLink = new StringBuilder(
						"INSERT INTO documentslinks(numgen,numgenlink,numver,numverlink) VALUES(?,?,?,?) ");
				st = con.prepareStatement(JDBCUtil.replaceCastMysql(insertLink.toString()));
				st.setInt(1, new Integer(idDocumentOfRejection));
				st.setInt(2, new Integer(idDocumentReject));
				st.setInt(3, new Integer(idVersionRejection));
				st.setInt(4, new Integer(idVersionReject));
				st.executeUpdate();

				// st = con.prepareStatement(insertLink.toString());
				st.setInt(1, new Integer(idDocumentReject));
				st.setInt(2, new Integer(idDocumentOfRejection));
				st.setInt(3, new Integer(idVersionReject));
				st.setInt(4, new Integer(idVersionRejection));
				st.executeUpdate();

				con.commit();

				// vamos a publicar el documento
				// PENDIENTE: para registros generados desde flujo parametrico hay que modificar
				// el metodo ya que se publica la version original y no el archivo bloqueado
				publicDocumentEraser(idDocumentOfRejection, "");
				resp = true;
			} else {
				throw new ApplicationExceptionChecked("E0028"); // :JAIRO: falta
																// por catalogar
																// el error
			}
		} catch (ApplicationExceptionChecked ae) {
			log.error(ae.getMessage());
			ae.printStackTrace();
			applyRollback(con);
			throw ae;
		} catch (Exception e) {
			log.error(e.getMessage());
			e.printStackTrace();
			applyRollback(con);
			setMensaje(e.getMessage());
			resp = false;
		} finally {
			setFinally(con, st);
		}
		return resp;
	}

	/**
	 * 
	 * @param idDocument
	 * @return
	 */
	public static String getTypeDocument(String idDocument) {
		return getTypeDocument(null, idDocument);
	}
	public static String getTypeDocument(Connection con, String idDocument) {
		try {
			StringBuilder query = new StringBuilder(
					"SELECT type FROM documents WHERE numgen = cast(? as int)");

			ArrayList<Object> parametro = new ArrayList<Object>();
			parametro.add(idDocument);

			CachedRowSet crs = JDBCUtil.executeQuery(query, parametro, con, Thread.currentThread().getStackTrace()[1].getMethodName());
			if (crs.next()) {
				return crs.getString("type");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		return "0";
	}

	public static String getTypeRegisterClass(String idDocument) {
		try {
			StringBuilder query = new StringBuilder(
					"select r.descRegisterClass from documents d, registerclass r where d.idRegisterClass=r.idRegisterClass and numGen= cast(? as int) ");

			ArrayList<Object> parametro = new ArrayList<Object>();
			parametro.add(idDocument);

			CachedRowSet crs = JDBCUtil.executeQuery(query, parametro, Thread.currentThread().getStackTrace()[1].getMethodName());
			if (crs.next()) {
				return crs.getString("descRegisterClass");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		return "N/A";
	}

	/**
	 * 
	 * @param idDocument
	 * @return
	 */
	public static boolean isLastVersionIsPublic(String idDocument) {
		try {
			StringBuilder query = new StringBuilder(
					"select statu from versiondoc where numver=(select max(numver) from versiondoc where numdoc=?)");

			ArrayList<Object> parametro = new ArrayList<Object>();
			parametro.add(new Integer(idDocument));

			CachedRowSet crs = JDBCUtil.executeQuery(query, parametro, Thread.currentThread().getStackTrace()[1].getMethodName());
			if (crs.next()) {
				return crs.getString("statu").equals(
						HandlerDocuments.docApproved);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		return false;
	}

	/*
	 * llamado desde el jsp para saber el nombre del documento y extension
	 */
	public static String getNameDocumentFromId(String idDocument) {
		try {
			StringBuilder sb = new StringBuilder(
					"select nameFile from documents where numgen=")
					.append(idDocument);
			CachedRowSet crs = JDBCUtil.executeQuery(sb, Thread.currentThread().getStackTrace()[1].getMethodName());
			if (crs.next()) {
				return crs.getString("nameFile");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Envia los correos a la lista de distribucion se�alada o heredada
	 * 
	 * @throws Exception
	 */
	public static void sendMailToListDist(Connection con, int idDocument)
			throws Exception {
		// buscamos el nodo del documento
		String sep = "";
		StringBuilder query = new StringBuilder(
				"SELECT numgen,nameDocument,idNode FROM documents WHERE numgen=")
				.append(idDocument);
		CachedRowSet crsDoc = JDBCUtil.executeQuery(query, null, con, Thread.currentThread().getStackTrace()[1].getMethodName());
		crsDoc.next(); // Avanzamos al primer registro

		// buscamos la ultima version del documento que debe ser la aprobada
		query.setLength(0);
		query.append(
				"SELECT numVer,mayorVer,minorVer FROM versiondoc WHERE numdoc=")
				.append(idDocument).append(" ORDER BY numver desc"); // ERROR: mayorVer es alfanumerica '9' > que '10'
		CachedRowSet crsVer = JDBCUtil.executeQuery(query, null, con, Thread.currentThread().getStackTrace()[1].getMethodName());
		crsVer.next(); // Avanzamos al primer registro

		// buscamos el nodo que contiene la lista de distribucion
		int idNodoList = HandlerStruct.findIdNodeParentToListDist(crsDoc
				.getInt("idNode"));

		// buscamos los correos que deben ser notificados
		query.setLength(0);
		query.append("SELECT a.idNode, a.nameUser, b.email, b.idPerson ")
				.append("FROM listdist a, person b ")
				.append("WHERE a.nameUser=b.nameUser ").append("AND a.idNode=")
				.append(idNodoList);
		CachedRowSet crsList = JDBCUtil.executeQuery(query, null, con, Thread.currentThread().getStackTrace()[1].getMethodName());

		// registramos los usuarios que seran notificados
		StringBuilder mails = new StringBuilder(1024);
		while (crsList.next()) {
			try {
				query.setLength(0);
				query.append("INSERT INTO listdistdocument VALUES(")
						.append(idDocument).append(",")
						.append(crsVer.getInt("numVer")).append(",")
						.append(crsList.getInt("idPerson")).append(")");
				// ydavila Ticket 001-00-003160 paso null en la conexi�n para que abra una temporal
				// TODO ydavila queda pendiente reubicar el cierre l�gico de la conexi�n dentro de la transacci�n.
				// JDBCUtil.executeUpdate(query, null, con);   
	        	   JDBCUtil.executeUpdate(query, null, null);
			} catch (Exception e) {
				// TODO: handle exception
				log.error(
						"Error controlado: '" + e.getLocalizedMessage() + "'",
						e);
			}
			// enviamos el correo al miembro de la lista.
			mails.append(sep).append(crsList.getString("email"));
			sep = Constants.PUNTO_COMA;
		}
		try {
			// Enviamos las notificaciones a los usuarios de la lista
			Locale defaultLocale = new Locale(
					DesigeConf.getProperty("language.Default"),
					DesigeConf.getProperty("country.Default"));
			ResourceBundle rb = ResourceBundle.getBundle("LoginBundle",
					defaultLocale);
			MailForm formaMail = new MailForm();

			formaMail.setFrom(HandlerParameters.PARAMETROS.getMailAccount());
			formaMail.setNameFrom(rb.getString("mail.system"));
			formaMail.setSubject(rb.getString("mail.nameUser") + " - "
					+ rb.getString("mail.MsgNewVersion"));

			StringBuilder msg = new StringBuilder(1024)
					.append("<b>")
					.append(rb.getString("mail.MsgNewVersionDoc"))
					.append("</b><br>")
					.append(crsDoc.getString("nameDocument"))
					.append("<br>")
					.append("Versi&oacute;n : ")
					.append(crsVer.getString("mayorVer"))
					.append(".")
					.append(ToolsHTML.isEmptyOrNull(
							crsVer.getString("minorVer"), "")).append("<br>");

			formaMail.setMensaje(msg.toString());
			formaMail.setTo(mails.toString());
			SendMailTread mail = new SendMailTread(formaMail);
			mail.start();
		} catch (Exception ex) {
			log.debug("Exception");
			log.error(ex.getMessage());
			ex.printStackTrace();
		}

	}

	/**
	 * 
	 * @param owner
	 * @param searchAsAdmin
	 * @return
	 * @throws Exception
	 */
	public static synchronized Collection<DocumentsCheckOutsBean> getNotifiedDocumentsByUser(
			String owner, boolean searchAsAdmin) throws Exception {
		StringBuilder sql = new StringBuilder(2048)
				.append("SELECT b.numgen, b.nameDocument, b.owner, b.prefix, b.number, b.idNode, c.mayorVer, c.minorVer, ")
				.append(" c.numVer, d.nameUser, ")
				.append(" d.idperson, d.nombres, d.apellidos, d.email ")
				.append(" FROM listdistdocument a LEFT JOIN preview e ON e.idDocument=a.idDocument AND e.idUsuario=a.idUsuario AND e.idVersion=a.idVersion, ")
				.append(" documents b, versiondoc c, person d ")
				.append("WHERE b.numgen=a.idDocument ")
				// ydavila Ticket 001-00-003167
				//Se filtra por documento con n�mero ya que est� mostrando en lista de distribuci�n documentos borradores.
				//Es un error de inconsistencia que estaban dejando unos errores corregidos de FTP (tickets 001-00-003160 y 001-00-003144) 
				//pero igual se coloca la validaci�n	*/
				.append("and b.number>'0' ")
				.append("AND c.numver=a.idVersion ")
				.append("AND d.idPerson=a.idUsuario ")
				.append("AND e.contador IS NULL ")
				.append("AND a.idVersion IN (select max(idVersion) from listdistdocument group by idDocument) ")
				.append("AND b.type != '1001' ");
		if (!searchAsAdmin) {
			// los usuarios que no sean del tipo administrador, solo podran ver
			// archivos y flujos
			// de los cuales ellos sean los propietarios
			sql.append("AND d.nameUser='").append(owner).append("'");
		}

		Vector resp = new Vector();
		Vector datos = JDBCUtil.doQueryVector(sql.toString(),Thread.currentThread().getStackTrace()[1].getMethodName());
		String prefixDoc = null;
		String idNode = "";

		for (int row = 0; row < datos.size(); row++) {
			Properties properties = (Properties) datos.elementAt(row);
			DocumentsCheckOutsBean docCheck = new DocumentsCheckOutsBean();
			docCheck.setIdDocument(properties.getProperty("numGen"));
			docCheck.setNameDocument(properties.getProperty("nameDocument"));
			// docCheck.setMayorVer(properties.getProperty("MayorVer").trim());
			// docCheck.setMinorVer(properties.getProperty("MinorVer").trim());
			String minorVer = properties.getProperty("minorVer").trim();
			if (ToolsHTML.isEmptyOrNull(minorVer)) {
				minorVer = "";
			}
			String mayorVer = properties.getProperty("mayorVer").trim();
			if (ToolsHTML.isEmptyOrNull(mayorVer)) {
				mayorVer = "";
			}
			docCheck.setMayorVer(mayorVer);
			docCheck.setMinorVer(minorVer);
			// docCheck.setDateCheckOut(ToolsHTML.formatDateShow(properties.getProperty("dateCheckOut"),
			// true));
			docCheck.setNumber(properties.getProperty("number") != null ? properties
					.getProperty("number").trim() : "");
			// docCheck.setPrefix(properties.getProperty("prefix")!=null?properties.getProperty("prefix").trim():"");
			idNode = properties.getProperty("idNode");
			// Se Busca el Prefijo del Documento seg�n como se encuentre
			// Definido el Mismo

			docCheck.setNumVer(properties.getProperty("numVer"));

			prefixDoc = properties.getProperty("prefix") != null ? properties
					.getProperty("prefix").trim() : "";
			if (!ToolsHTML.isEmptyOrNull(prefixDoc)) {
				docCheck.setPrefix(prefixDoc.trim());
			} else {
				docCheck.setPrefix("");
			}

			Person p = new Person(Integer.parseInt(properties
					.getProperty("idperson")),
					properties.getProperty("nombres"),
					properties.getProperty("apellidos"),
					properties.getProperty("email"));

			docCheck.setPersonBean(p);

			resp.add(docCheck);
		}
		return resp;
	}

	/**
	 * 
	 * @param request
	 * @return
	 * @throws Exception
	 */
	public synchronized static CachedRowSet getDocumentUnRead(
			HttpServletRequest request) throws Exception {

		// primero buscamos los documentos segun el filtro de publicados
		Collection doc = (Collection) request.getSession().getAttribute(
				"published");
		StringBuilder idDocs = new StringBuilder(1024);
		if (doc != null) {
			String sep = "";
			for (Iterator ite = doc.iterator(); ite.hasNext();) {
				BaseDocumentForm forma = (BaseDocumentForm) ite.next();
				idDocs.append(sep).append(forma.getIdDocument());
				sep = Constants.COMA;
			}
		}

		// consultamos las ocurrencias de descargas

		StringBuilder query = new StringBuilder(1024)
				.append("SELECT a.numgen, a.nameDocument, a.prefix, a.number, e.mayorVer, e.minorVer, a.owner, d.nameUser, c.contador, ")
				.append("c.registra, c.actualiza, ")
				.append("(select count(*) from historydocs where historydocs.type='21' and historydocs.idNode=a.numgen AND historydocs.nameUser=d.nameUser GROUP BY historydocs.idNode, historydocs.nameUser) As descargas ")
				.append("FROM documents a ")
				.append("LEFT OUTER JOIN listdistdocument b ON b.idDocument=a.numgen AND b.idVersion=a.lastVersionApproved ")
				.append("LEFT OUTER JOIN preview c ON b.idDocument=c.idDocument AND b.idVersion=c.idVersion AND b.idUsuario=c.idUsuario ")
				.append("LEFT OUTER JOIN person d ON d.idPerson=b.idUsuario ")
				.append("LEFT OUTER JOIN versiondoc e ON e.numVer=lastVersionApproved ")
				.append("WHERE b.idDocument IS NOT NULL ");
		if (idDocs.length() > 0) {
			query.append("AND a.numgen in (").append(idDocs).append(") ");
		}
		query.append("ORDER BY a.numgen ");

		return JDBCUtil.executeQuery(query, Thread.currentThread().getStackTrace()[1].getMethodName());

	}
	
	//ydavila Ticket 001-00-003265 Se agrega rutina para b�squeda de comentarios
	public static String getDescriptcommentBuscar(String id,String numdoc,int numver,String estatus) throws Exception {
		Connection con1,con2 = null;
		PreparedStatement pst1,pst2 = null;
		ResultSet rs1,rs2 = null;
		String img="<img";		
		String xml="/xml";
		String qry1,qry2 = "";
		id=" ";
		//TODO cuando validen los estatus de los comentarios ordeno los queries y unifico rutinas
		qry1 = "SELECT numgen,versionpublic,comments FROM documents WHERE numgen = " + numdoc ;
		con1 = JDBCUtil.getConnection(Thread.currentThread().getStackTrace()[1].getMethodName());
		pst1 = con1.prepareStatement(JDBCUtil.replaceCastMysql(qry1));	
		rs1 = pst1.executeQuery();
		if (rs1.next()) {
			if (estatus.equals("Aprobado")){
				switch (Constants.MANEJADOR_ACTUAL) {
				case Constants.MANEJADOR_MSSQL:
					qry2 = "SELECT TOP 1 comments, type FROM historydocs WHERE ((idnode= " + numdoc +
					" AND comments NOT LIKE '% %' AND type = '5')) ORDER BY idhistory ASC ";
					break;
				case Constants.MANEJADOR_POSTGRES:
					qry2 = "SELECT comments, type FROM historydocs WHERE ((idnode= " + numdoc +
					" AND comments <> '' AND type = '5')) ORDER BY idhistory ASC LIMIT 1";
					break;
				case Constants.MANEJADOR_MYSQL:
					qry2 = "SELECT comments, type FROM historydocs WHERE ((idnode= " + numdoc +
					" AND comments <> '' AND type = '5')) ORDER BY idhistory ASC LIMIT 1";
					break;
				}
			}else{
				switch (Constants.MANEJADOR_ACTUAL) {
				case Constants.MANEJADOR_MSSQL:
					qry2 = "SELECT TOP 1 comments, type FROM historydocs WHERE ((idnode= " + numdoc +
					" AND comments NOT LIKE '% %' AND type = '5')) ORDER BY idhistory DESC ";	
					break;
				case Constants.MANEJADOR_POSTGRES:
					qry2 = "SELECT comments, type FROM historydocs WHERE ((idnode= " + numdoc +
					" AND comments <> '' AND type = '5')) ORDER BY idhistory DESC LIMIT 1";	
					break;
				case Constants.MANEJADOR_MYSQL:
					qry2 = "SELECT comments, type FROM historydocs WHERE ((idnode= " + numdoc +
					" AND comments <> '' AND type = '5')) ORDER BY idhistory DESC LIMIT 1";	
					break;
				};
			}		 
			con2 = JDBCUtil.getConnection(Thread.currentThread().getStackTrace()[1].getMethodName());
			pst2 = con2.prepareStatement(qry2);	
			rs2 = pst2.executeQuery();
			if (rs2.next()) {
				if (!ToolsHTML.isEmptyOrNull(rs2.getString("comments"))) {
					id=(rs2.getString("comments"));
				}
			}else{
				switch (Constants.MANEJADOR_ACTUAL) {
				case Constants.MANEJADOR_MSSQL:
					qry2 = "SELECT TOP 1 comments, type FROM historydocs WHERE ((idnode= " + numdoc +
					" AND comments NOT LIKE '% %' AND type = '1')) ORDER BY idhistory ASC";
					break;
				case Constants.MANEJADOR_POSTGRES:
					qry2 = "SELECT comments, type FROM historydocs WHERE ((idnode= " + numdoc +
					" AND comments <> '' AND type = '1')) ORDER BY idhistory ASC LIMIT 1";
					break;
				case Constants.MANEJADOR_MYSQL:
					qry2 = "SELECT comments, type FROM historydocs WHERE ((idnode= " + numdoc +
					" AND comments <> '' AND type = '1')) ORDER BY idhistory ASC LIMIT 1";
					break;
			}
				con2 = JDBCUtil.getConnection(Thread.currentThread().getStackTrace()[1].getMethodName());
				pst2 = con2.prepareStatement(JDBCUtil.replaceCastMysql(qry2));	
				rs2 = pst2.executeQuery();
				if (rs2.next()) {
					if (!ToolsHTML.isEmptyOrNull(rs2.getString("comments"))) {
						id=(rs2.getString("comments"));
					}
				}
			}
			if (id.contains(xml)) {
				String text=extractText(id);
				id=text;
			}	
		}
		con1.close();
		con2.close();
		return id.toString();
	}

	//ydavila Ticket 001-00-003265 Se agrega rutina para b�squeda de comentarios
	public static String getDescriptCommentContextBuscar(Connection con1, String id,String numdoc,int numver, String estatus) throws Exception {
		PreparedStatement pst1 = null;
		ResultSet rs1 = null;
		String img="<img";		
		String qry1,qry2 = "";
		id=" ";
		
		try {
			//TODO cuando validen los estatus de los comentarios ordeno los queries y unifico rutinas
			qry1 = "SELECT numgen,versionpublic,comments FROM documents WHERE numgen = " + numdoc ;
			pst1 = con1.prepareStatement(qry1);	
			rs1 = pst1.executeQuery();
			if (rs1.next()) {
				if (estatus.equals("Aprobado")){
					switch (Constants.MANEJADOR_ACTUAL) {
					case Constants.MANEJADOR_MSSQL:
						qry2 = "SELECT TOP 1 comments, type FROM historydocs WHERE ((idnode= " + numdoc +
						" AND comments NOT LIKE '% %' AND type = '5')) ORDER BY idhistory ASC";
						break;
					case Constants.MANEJADOR_POSTGRES:
						qry2 = "SELECT comments, type FROM historydocs WHERE ((idnode= " + numdoc +
						" AND comments <> '' AND type = '5')) ORDER BY idhistory ASC limit 1";
						//" AND CONVERT(NVARCHAR(MAX), comments) <> '' AND type = '5')) ORDER BY idhistory ASC limit 1";
						break;
					}
				}else{
					switch (Constants.MANEJADOR_ACTUAL) {
					case Constants.MANEJADOR_MSSQL:
						qry2 = "SELECT TOP 1 comments, type FROM historydocs WHERE ((idnode= " + numdoc +
						" AND comments NOT LIKE '% %' AND type = '5')) ORDER BY idhistory DESC";	
						break;
					case Constants.MANEJADOR_POSTGRES:
						qry2 = "SELECT comments, type FROM historydocs WHERE ((idnode= " + numdoc +
						" AND comments <> '' AND type = '5')) ORDER BY idhistory DESC limit 1";	
						//" AND CONVERT(NVARCHAR(MAX), comments) <> '' AND type = '5')) ORDER BY idhistory DESC limit 1";
						break;
					}
				}

				pst1 = con1.prepareStatement(qry2);	
				rs1 = pst1.executeQuery();
				if (rs1.next()) {
					if (!ToolsHTML.isEmptyOrNull(rs1.getString("comments"))) {
						id=(rs1.getString("comments"));
					}
				}else{
					switch (Constants.MANEJADOR_ACTUAL) {
					case Constants.MANEJADOR_MSSQL:
						qry2 = "SELECT TOP 1 comments, type FROM historydocs WHERE ((idnode= " + numdoc +
						" AND comments NOT LIKE '% %' AND type = '1')) ORDER BY idhistory ASC";
						break;
					case Constants.MANEJADOR_POSTGRES:
						qry2 = "SELECT comments, type FROM historydocs WHERE ((idnode= " + numdoc +
						" AND comments <> '' AND type = '1')) ORDER BY idhistory ASC limit 1";
						//" AND CONVERT(NVARCHAR(MAX), comments) <> '' AND type = '1')) ORDER BY idhistory ASC limit 1";
						break;
					}
					pst1 = con1.prepareStatement(qry2);	
					rs1 = pst1.executeQuery();
					if (rs1.next()) {
						if (!ToolsHTML.isEmptyOrNull(rs1.getString("comments"))) {
							id=(rs1.getString("comments"));
						}
					}
				}
				if (id.contains(img)) {
					id="IMAGEN";
				}else{
					String text=extractText(id);
					id=text;
					if (id.length()>40) {
						id=text.substring(0, 40);
					}
				}	
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
		return id.toString();
	}
	
	//ydavila Ticket 001-00-003265 Se agrega rutina para b�squeda de comentarios
			public static String getDescriptcommentLM(String numdoc,int numver) throws Exception {
				Connection con1=null;
				PreparedStatement pst1,pst2 = null;
				ResultSet rs1,rs2 = null;
				String img="<img";		
				String xml="/xml";
				String qry1,qry2 = "";
				String id=" ";
				//TODO cuando validen los estatus de los comentarios ordeno los queries y unifico rutinas
				qry1 = "SELECT numgen,versionpublic,comments FROM documents WHERE numgen = " + numdoc ;
				con1 = JDBCUtil.getConnection(Thread.currentThread().getStackTrace()[1].getMethodName());
				pst1 = con1.prepareStatement(qry1);	
				rs1 = pst1.executeQuery();
				if (rs1.next()) {
					switch (Constants.MANEJADOR_ACTUAL) {
					case Constants.MANEJADOR_MSSQL:		
						qry2 = "SELECT TOP 1 comments, type FROM historydocs WHERE ((idnode= " + numdoc +
						" AND comments NOT LIKE '% %' AND type = '5')) ORDER BY idhistory ASC";	
						break;
					case Constants.MANEJADOR_POSTGRES:
						qry2 = "SELECT comments, type FROM historydocs WHERE ((idnode= " + numdoc +
						" AND comments <> '' AND type = '5')) ORDER BY idhistory ASC limit 1";		 
						break;
					case Constants.MANEJADOR_MYSQL:
						qry2 = "SELECT comments, type FROM historydocs WHERE ((idnode= " + numdoc +
						" AND comments <> '' AND type = '5')) ORDER BY idhistory ASC limit 1";		 
						break;
					}
					con1 = JDBCUtil.getConnection(Thread.currentThread().getStackTrace()[1].getMethodName());
					pst2 = con1.prepareStatement(qry2);	
					rs2 = pst2.executeQuery();
					if (rs2.next()) {
						if (!ToolsHTML.isEmptyOrNull(rs2.getString("comments"))) {
							id=(rs2.getString("comments"));
						}
					}else{
						switch (Constants.MANEJADOR_ACTUAL) {
						case Constants.MANEJADOR_MSSQL:	
							qry2 = "SELECT TOP 1 comments, type FROM historydocs WHERE ((idnode= " + numdoc +
							" AND comments NOT LIKE '% %' AND type = '1')) ORDER BY idhistory ASC";
							break;
						case Constants.MANEJADOR_POSTGRES:
							qry2 = "SELECT comments, type FROM historydocs WHERE ((idnode= " + numdoc +
							" AND comments <> '' AND type = '1')) ORDER BY idhistory ASC limit 1";
							break;
						case Constants.MANEJADOR_MYSQL:
							qry2 = "SELECT comments, type FROM historydocs WHERE ((idnode= " + numdoc +
							" AND comments <> '' AND type = '1')) ORDER BY idhistory ASC limit 1";
							break;
						}
						con1 = JDBCUtil.getConnection(Thread.currentThread().getStackTrace()[1].getMethodName());
						pst2 = con1.prepareStatement(qry2);	
						rs2 = pst2.executeQuery();
						if (rs2.next()) {
							if (!ToolsHTML.isEmptyOrNull(rs2.getString("comments"))) {
								id=(rs2.getString("comments"));
							}
						}
					}
					if (id.contains(xml)) {
						String text=extractText(id);
						id=text;
					}else if (id.contains(img)) {
						id="IMAGEN";
					}	
				}
				return id.toString();
			}

		//ydavila Ticket 001-00-003265 Se agrega rutina para b�squeda de comentarios
		public static String getDescriptCommentContextLM(String numdoc,int numver) throws Exception {
			Connection con1,con2 = null;
			PreparedStatement pst1,pst2 = null;
			ResultSet rs1,rs2 = null;
			String img="<img";		
			String qry1,qry2 = "";
			String id=" ";
			//TODO cuando validen los estatus de los comentarios ordeno los queries y unifico rutinas
			qry1 = "SELECT numgen,versionpublic,comments FROM documents WHERE numgen = " + numdoc ;
			con1 = JDBCUtil.getConnection(Thread.currentThread().getStackTrace()[1].getMethodName());
			pst1 = con1.prepareStatement(JDBCUtil.replaceCastMysql(qry1));	
			rs1 = pst1.executeQuery();
			if (rs1.next()) {
				switch (Constants.MANEJADOR_ACTUAL) {
				case Constants.MANEJADOR_MSSQL:		
					qry2 = "SELECT TOP 1 comments, type FROM historydocs WHERE ((idnode= " + numdoc +
					" AND comments NOT LIKE '% %' AND type = '5')) ORDER BY idhistory ASC";	
					break;
				case Constants.MANEJADOR_POSTGRES:
					qry2 = "SELECT comments, type FROM historydocs WHERE ((idnode= " + numdoc +
					" AND comments <> '' AND type = '5')) ORDER BY idhistory ASC limit 1";		 
					break;
				case Constants.MANEJADOR_MYSQL:
					qry2 = "SELECT comments, type FROM historydocs WHERE ((idnode= " + numdoc +
					" AND comments <> '' AND type = '5')) ORDER BY idhistory ASC limit 1";		 
					break;
				}
				con1 = JDBCUtil.getConnection(Thread.currentThread().getStackTrace()[1].getMethodName());
				pst2 = con1.prepareStatement(JDBCUtil.replaceCastMysql(qry2));	
				rs2 = pst2.executeQuery();
				if (rs2.next()) {
					if (!ToolsHTML.isEmptyOrNull(rs2.getString("comments"))) {
						id=(rs2.getString("comments"));
					}
				}else{
					switch (Constants.MANEJADOR_ACTUAL) {
					case Constants.MANEJADOR_MSSQL:		
						qry2 = "SELECT TOP 1 comments, type FROM historydocs WHERE ((idnode= " + numdoc +
						" AND comments NOT LIKE '% %' AND type = '1')) ORDER BY idhistory ASC";
						break;
					case Constants.MANEJADOR_POSTGRES:
						qry2 = "SELECT comments, type FROM historydocs WHERE ((idnode= " + numdoc +
						" AND comments <> '' AND type = '1')) ORDER BY idhistory ASC limit 1"; 
						break;
					case Constants.MANEJADOR_MYSQL:
						qry2 = "SELECT comments, type FROM historydocs WHERE ((idnode= " + numdoc +
						" AND comments <> '' AND type = '1')) ORDER BY idhistory ASC limit 1"; 
						break;
					}
					con1 = JDBCUtil.getConnection(Thread.currentThread().getStackTrace()[1].getMethodName());
					pst2 = con1.prepareStatement(JDBCUtil.replaceCastMysql(qry2));	
					rs2 = pst2.executeQuery();
					if (rs2.next()) {
						if (!ToolsHTML.isEmptyOrNull(rs2.getString("comments"))) {
							id=(rs2.getString("comments"));
						}
					}
				}
				if (id.contains(img)) {
					id="IMAGEN";
				}else{
					String text=extractText(id);
					id=text;
					if (id.length()>40) {
						id=text.substring(0, 40);
					}
				}	
			}
			return id.toString();
		}

	//ydavila Ticket 001-00-003265 Se agrega rutina para b�squeda de comentarios para reporte Excel
		public static String getDescriptCommentExcel(Connection con, String numdoc,String estatus) throws Exception {
			/*StringBuilder sb = new StringBuilder("");
			String imagen="<img";
			if (id.contains(imagen)) {
				id="Imagen no puede ser mostrada";
				//TODO pendiente resolver el error del loginbundle
			//	ResourceBundle rb = ResourceBundle.getBundle("lst.imagecomment");
			//	id=rb.toString();
			}else{
				if (!ToolsHTML.isEmptyOrNull(id)) {
					String text=extractText(id);
					id=text;
				}else{
					id=" ";
				}
			}
			return id.toString();*/
			boolean isNewConnect = false;
			String id=" ";
			PreparedStatement pst1,pst2 = null;
			ResultSet rs1,rs2 = null;
			String img="<img";	
			String xml="/xml";
			String qry1,qry2 = "";
			//TODO cuando validen los estatus de los comentarios ordeno los queries y unifico rutinas
			qry1 = "SELECT numgen,versionpublic,comments FROM documents WHERE numgen = " + numdoc ;

			if(con==null) {
				con = JDBCUtil.getConnection(Thread.currentThread().getStackTrace()[1].getMethodName());
				isNewConnect = true;
			}
			
			try {
				pst1 = con.prepareStatement(JDBCUtil.replaceCastMysql(qry1));	
				rs1 = pst1.executeQuery();
				if (rs1.next()) {
					switch (Constants.MANEJADOR_ACTUAL) {
					case Constants.MANEJADOR_MSSQL:		
						qry2 = "SELECT TOP 1 comments, type FROM historydocs WHERE ((idnode= " + numdoc +
						" AND CAST(comments AS VARCHAR) <> '' AND type = '5')) ORDER BY idhistory ASC";
						break;
					case Constants.MANEJADOR_POSTGRES:
						qry2 = "SELECT comments, type FROM historydocs WHERE ((idnode= " + numdoc +
						" AND comments <> '' AND type = '5')) ORDER BY idhistory ASC limit 1";	 
						break;
					case Constants.MANEJADOR_MYSQL:
						qry2 = "SELECT comments, type FROM historydocs WHERE ((idnode= " + numdoc +
						" AND comments <> '' AND type = '5')) ORDER BY idhistory ASC limit 1";	 
						break;
					}	
					pst2 = con.prepareStatement(JDBCUtil.replaceCastMysql(qry2));	
					rs2 = pst2.executeQuery();
					if (rs2.next()) {
						if (!ToolsHTML.isEmptyOrNull(rs2.getString("comments"))) {
							id=(rs2.getString("comments"));
						}
					}else{
						switch (Constants.MANEJADOR_ACTUAL) {
						case Constants.MANEJADOR_MSSQL:	
							qry2 = "SELECT TOP 1 comments, type FROM historydocs WHERE ((idnode= " + numdoc +
							" AND CAST(comments AS VARCHAR) <> '' AND type = '1')) ORDER BY idhistory ASC";
							break;
						case Constants.MANEJADOR_POSTGRES:
							qry2 = "SELECT comments, type FROM historydocs WHERE ((idnode= " + numdoc +
							" AND comments <> '' AND type = '1')) ORDER BY idhistory ASC limit 1";	 
							break;
						case Constants.MANEJADOR_MYSQL:
							qry2 = "SELECT comments, type FROM historydocs WHERE ((idnode= " + numdoc +
							" AND comments <> '' AND type = '1')) ORDER BY idhistory ASC limit 1";	 
							break;
						}

						pst2 = con.prepareStatement(JDBCUtil.replaceCastMysql(qry2));	
						rs2 = pst2.executeQuery();
						if (rs2.next()) {
							if (!ToolsHTML.isEmptyOrNull(rs2.getString("comments"))) {
								id=(rs2.getString("comments"));
							}
						}
					}
					if (id.contains(img)) {
						id="IMAGEN";
					}	
					if (id.contains(xml)) {
						String text=extractText(id);
						id=text;
					}	
				}
			} finally {
				if(isNewConnect) {
					con.close();
				}
			}
			return id;
		}
		
		//ydavila Ticket 001-00-003265 Se agrega rutina para b�squeda de comentarios para reporte Excel
		public static String getDescriptCommentExcel1(Connection con, String numdoc,String estatus) throws Exception {
			PreparedStatement pst1,pst2 = null;
			ResultSet rs1,rs2 = null;
			String img="<img";	
			String xml="/xml";
			String qry1,qry2 = "";
			String id=" ";
			boolean isNewConnect = false;
			//TODO cuando validen los estatus de los comentarios ordeno los queries y unifico rutinas
			qry1 = "SELECT numgen,versionpublic,comments FROM documents WHERE numgen = " + numdoc ;
			if(con==null) {
				con = JDBCUtil.getConnection(Thread.currentThread().getStackTrace()[1].getMethodName());
				isNewConnect = true;
			}
			try {
				pst1 = con.prepareStatement(JDBCUtil.replaceCastMysql(qry1));	
				rs1 = pst1.executeQuery();
				if (rs1.next()) {
					if (estatus.equals("Aprobado")){
						switch (Constants.MANEJADOR_ACTUAL) {
						case Constants.MANEJADOR_MSSQL:	
							qry2 = "SELECT TOP 1 comments, type FROM historydocs WHERE ((idnode= " + numdoc +
							" AND CAST(comments AS VARCHAR) <> '' AND type = '5')) ORDER BY idhistory ASC";
							break;
						case Constants.MANEJADOR_POSTGRES:
							qry2 = "SELECT comments, type FROM historydocs WHERE ((idnode= " + numdoc +
							" AND comments <> '' AND type = '5')) ORDER BY idhistory ASC limit 1";	 
							break;
						case Constants.MANEJADOR_MYSQL:
							qry2 = "SELECT comments, type FROM historydocs WHERE ((idnode= " + numdoc +
							" AND comments <> '' AND type = '5')) ORDER BY idhistory ASC limit 1";	 
							break;
						}	
					}else{
						switch (Constants.MANEJADOR_ACTUAL) {
						case Constants.MANEJADOR_MSSQL:	
							qry2 = "SELECT TOP 1 comments, type FROM historydocs WHERE ((idnode= " + numdoc +
							" AND CAST(comments AS VARCHAR) <> '' AND type = '5')) ORDER BY idhistory DESC";
							break;
						case Constants.MANEJADOR_POSTGRES:
							qry2 = "SELECT comments, type FROM historydocs WHERE ((idnode= " + numdoc +
							" AND comments <> '' AND type = '5')) ORDER BY idhistory DESC limit 1";	 
							break;
						case Constants.MANEJADOR_MYSQL:
							qry2 = "SELECT comments, type FROM historydocs WHERE ((idnode= " + numdoc +
							" AND comments <> '' AND type = '5')) ORDER BY idhistory DESC limit 1";	 
							break;
						}	
					}
					pst2 = con.prepareStatement(qry2);	
					rs2 = pst2.executeQuery();
					if (rs2.next()) {
						if (!ToolsHTML.isEmptyOrNull(rs2.getString("comments"))) {
							id=(rs2.getString("comments"));
						}
					}else{
						switch (Constants.MANEJADOR_ACTUAL) {
						case Constants.MANEJADOR_MSSQL:		
							qry2 = "SELECT TOP 1 comments, type FROM historydocs WHERE ((idnode= " + numdoc +
							" AND CAST(comments AS VARCHAR) <> '' AND type = '1')) ORDER BY idhistory ASC";
							break;
						case Constants.MANEJADOR_POSTGRES:
							qry2 = "SELECT comments, type FROM historydocs WHERE ((idnode= " + numdoc +
							" AND comments <> '' AND type = '1')) ORDER BY idhistory ASC limit 1";	 
							break;
						case Constants.MANEJADOR_MYSQL:
							qry2 = "SELECT comments, type FROM historydocs WHERE ((idnode= " + numdoc +
							" AND comments <> '' AND type = '1')) ORDER BY idhistory ASC limit 1";	 
							break;
						}
						pst2 = con.prepareStatement(qry2);	
						rs2 = pst2.executeQuery();
						if (rs2.next()) {
							if (!ToolsHTML.isEmptyOrNull(rs2.getString("comments"))) {
								id=(rs2.getString("comments"));
							}
						}
					}
					if (id.contains(img)) {
						id="IMAGEN";
					}	
					if (id.contains(xml)) {
						String text=extractText(id);
						id=text;
					}	
				}
			} finally {
				if(isNewConnect) {
					con.close();
				}
			}
			return id;
		}

	public static String getHistoryComment(int id, String type) throws Exception {
		
		System.out.println("handlerdocuments 9378 getHistoryComment, id= " + id);
		StringBuilder sb = new StringBuilder("");
			sb = new StringBuilder(
				"SELECT comments FROM historydocs WHERE idnode = ").append(id);
			//ydavila Ticket 001-00-003265	 Se agrega salida para columna Comentarios
			if (type=="5"){
				sb.append(" and type = '").append(type).append("'");
			}
			Vector<Properties> props = JDBCUtil.doQueryVector(sb.toString(),Thread.currentThread().getStackTrace()[1].getMethodName());
			sb.setLength(0);
			for (Properties comment : props) {
				sb.append(comment.getProperty("comments")).append(" ").append("<br />");
				}
		return sb.toString();
	}
	
	//ydavila Ticket 001-00-003265 Se agrega rutina para extraer texto del HTML
	//http://www.javamexico.org/blogs/rodrigo_salado_anaya/quitar_etiquetas_html
	public static String extractText(String id) throws IOException {
	    final ArrayList<String> list = new ArrayList<String>();
	    String MutableAttributeSet="";
	    ParserDelegator parserDelegator = new ParserDelegator();
	    ParserCallback parserCallback = new ParserCallback() {
	        public void handleText(final char[] data, final int pos) { 
	            list.add(new String(data));
	        }
	        public <MutableAttributeSet> void handleStartTag(Tag tag, MutableAttributeSet attribute, int pos) { }
	        public void handleEndTag(Tag t, final int pos) {  }
	        public <MutableAttributeSet> void handleSimpleTag(Tag t, MutableAttributeSet a, final int pos) { }
	        public void handleComment(final char[] data, final int pos) { }
	        public void handleError(final java.lang.String errMsg, final int pos) { }
	    };
	    parserDelegator.parse(new StringReader(id), parserCallback, true);
	    String text = "";
	    for(String s : list) {
	        text += " " + s;
	    }
	    return text;
	}

	//ydavila Ticket 001-00-003023
	public static void updateStatDocSolCambio(Connection con,int numgen,String statu,String statuant) throws ApplicationExceptionChecked, SQLException{
		try {
			StringBuilder sql = new StringBuilder("UPDATE documents SET statu = ");
			sql.append(statu);sql.append(", ");
			sql.append(" statuant=");
			sql.append(statuant);
			sql.append(" WHERE numgen=");
			sql.append(numgen);
			PreparedStatement st = con.prepareStatement(sql.toString());
			st.executeUpdate();
		} catch (Exception ex) {
			log.error(ex.getMessage());
			ex.printStackTrace();
		}
	}
	
	//ydavila
	public static String getOwnerDocument(int idDocument) {
		try {
			StringBuilder query = new StringBuilder(
					"SELECT owner FROM documents WHERE numgen = ?");

			ArrayList<Object> parametro = new ArrayList<Object>();
			parametro.add(idDocument);

			CachedRowSet crs = JDBCUtil.executeQuery(query, parametro, Thread.currentThread().getStackTrace()[1].getMethodName());
			if (crs.next()) {
				return crs.getString("owner");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		return "0";
	}
	
	public static Search getOwnerDocumentSearch(int idDocument) {
		Search bean = null;

		try {
			StringBuilder sql = new StringBuilder();
			
			String owner = getOwnerDocument(idDocument);

			switch (Constants.MANEJADOR_ACTUAL) {
			case Constants.MANEJADOR_MSSQL:
				sql.append("SELECT top 1 idPerson,nameUser, c.cargo, (apellidos + ' ' +nombres) AS nombre");
				sql.append(" FROM person p, tbl_cargo c ");
				sql.append(" WHERE nameuser='").append(owner).append("' ");
				sql.append(" AND cast(p.cargo as int) = c.idcargo ");
				sql.append(" AND accountactive = '1' ");
				log.debug("sql HandleParameters.getRespCambElimDocAdm = " + sql);
				break;
			case Constants.MANEJADOR_POSTGRES:
				sql.append("SELECT idPerson,nameUser,c.cargo, (apellidos || ' ' || nombres) AS nombre ");
				sql.append(" FROM person p, tbl_cargo c ");
				sql.append(" WHERE nameuser='").append(owner).append("' ");
				sql.append(" AND cast(p.cargo as int) = c.idcargo ");
				sql.append(" AND accountactive = '1' LIMIT 1");
				log.debug("sql HandleParameters.getRespCambElimDocAdm = " + sql);
				break;
			case Constants.MANEJADOR_MYSQL:
				sql.append("SELECT idPerson,nameUser,c.cargo, CONCAT(apellidos , ' ' , nombres) AS nombre ");
				sql.append(" FROM person p, tbl_cargo c ");
				sql.append(" WHERE nameuser='").append(owner).append("' ");
				sql.append(" AND cast(p.cargo as int) = c.idcargo ");
				sql.append(" AND accountactive = '1' LIMIT 1");
				log.debug("sql HandleParameters.getRespCambElimDocAdm = " + sql);
				break;
			}

			Vector datos = JDBCUtil.doQueryVector(sql.toString(),Thread.currentThread().getStackTrace()[1].getMethodName());
			Properties prop = null;

			for (int row = 0; row < datos.size(); row++) {
				prop = (Properties) datos.elementAt(row);
				bean = new Search(prop.getProperty("nombre"), prop.getProperty("nombre"));
				bean.setId(prop.getProperty("nameUser"));
				bean.setAditionalInfo(prop.getProperty("cargo"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		return bean;
	}

	public static String getnameuser(String searchName) throws Exception {
		String resp = "";
		String query = "SELECT distinct nameUser,Nombres,Apellidos,email,lower(Apellidos),lower(Nombres)";
		query += " FROM person WHERE accountActive = '1' ";
        if(!ToolsHTML.isEmptyOrNull(searchName)){
        	query += " AND (Nombres like '%"+searchName.trim()+"%' ";
            query += " OR Apellidos like '%"+searchName.trim()+"%' ";
			switch (Constants.MANEJADOR_ACTUAL) {
			case Constants.MANEJADOR_MSSQL:
                query += " OR Nombres+' '+Apellidos like '%"+searchName.trim()+"%' ";
                query += " OR Apellidos+' '+Nombres like '%"+searchName.trim()+"%') ";
				break;
			case Constants.MANEJADOR_POSTGRES:
	            query += " OR Nombres|| ' ' ||Apellidos like '%"+searchName.trim()+"%' ";
	            query += " OR Apellidos|| ' ' ||Nombres like '%"+searchName.trim()+"%') ";
				break;
			}
        }
        query += "  ORDER BY lower(Apellidos) asc, lower(Nombres) asc ";
		Vector datos = JDBCUtil.doQueryVector(query,Thread.currentThread().getStackTrace()[1].getMethodName());
		for (int row = 0; row < datos.size(); row++) {
			Properties properties = (Properties) datos.elementAt(row);
			resp = properties.getProperty("nameUser");
		}
		return resp;
	}
 
	// cambiamos en servidor la extencion del archivo mandando al servdor
	//aqui vamos colorar los metodos y cllase nueva
	
	//ydavila
	public static String getSolicitanteImpresion(String idDocument) throws ApplicationExceptionChecked, Exception {
		String solicitante=null;
		StringBuilder sql = new StringBuilder(100);
		switch (Constants.MANEJADOR_ACTUAL) {
		case Constants.MANEJADOR_POSTGRES:
			sql.append(" SELECT (apellidos || ' ' || nombres) AS nombre ");
			sql.append(" FROM person p ");
			sql.append(" WHERE idPerson = (SELECT solicitante FROM tbl_solicitudimpresion t WHERE t.numgen = ");
			sql.append(  idDocument);
			sql.append(" and t.statusimpresion = 5 and p.accountactive='1') LIMIT 1");
			break;
		case Constants.MANEJADOR_MSSQL:		
			sql.append(" SELECT (apellidos +' '+ nombres) AS nombre ");
			sql.append(" FROM person p ");
			sql.append(" WHERE idPerson = (SELECT TOP 1 solicitante FROM tbl_solicitudimpresion t WHERE t.numgen = ");
			sql.append(  idDocument);
			sql.append(" and t.statusimpresion = 5 and p.accountactive='1')");
			break;
		}
		log.debug("sql getSolicitanteImpresion = " + sql);
		Properties prop = JDBCUtil.doQueryOneRow(sql.toString(),Thread.currentThread().getStackTrace()[1].getMethodName());	
		if (!ToolsHTML.isEmptyOrNull(prop.getProperty("nombre"))) {
			solicitante=prop.getProperty("nombre");
		}
		return solicitante;
	}
	
	public static void main(String[] args) {
		String cad = "454,545,64,64,65,54,54,56,46,545,";
		
		System.out.println(cad);
		if(cad.endsWith(",")) {
			cad = cad.substring(0, cad.length()-1);
		}
		System.out.println(cad);
		
	}

	// codigo Nuevo de actulizar el Nombre de la extesion
	public synchronized static boolean updateExtensionFile(int idDocument, String extension, String contextType) throws Exception {
		StringBuffer query = new StringBuffer();
		int reg = 0;
		
		System.out.println("Actualizando extension de archivo"+ idDocument+" extesion  "+extension+" tipo "+contextType);
	 
		ArrayList parametros = new ArrayList();
		try {
			// buscamos el archivo
			query.setLength(0);
			query.append("SELECT nameFile FROM documents WHERE numGen = ").append(idDocument);
			System.out.println("query "+query);
			
			CachedRowSet crs = JDBCUtil.executeQuery(query, contextType);

			if (crs.next()) {
				String nameFile = crs.getString("nameFile");
				nameFile = crs.getString("nameFile").split("\\.")[0];
				nameFile = nameFile.concat(".").concat(extension);
				System.out.println("nombre en -- query------> "+nameFile);

				// update
				parametros.add(nameFile);
				parametros.add(contextType);

				query.setLength(0);
				query.append("UPDATE documents SET namefile = ? , contexttype = ? ");
				query.append("WHERE  numGen = ").append(idDocument).append(" ");

				reg = JDBCUtil.executeUpdate(query, parametros);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return reg > 0;
	}

	public static String getBeforeLocksCommentsFromDocuments(String idDocument, String mayorVer, String minorVer) throws SQLException {
		StringBuffer query = new StringBuffer();
		ArrayList parameters = new ArrayList();
		String comments = new String();
		try {
			query.append("SELECT comments, `type` FROM historydocs ")
			.append("WHERE idNode = ? AND `type` IN (?,?)  AND MayorVer = ? AND MinorVer = ? ")
			.append("ORDER by dateChange DESC");
			
			parameters.add(idDocument);
			parameters.add(HandlerDocuments.docCheckIn);
			parameters.add(HandlerDocuments.docBackCheckIn);
			parameters.add(mayorVer);
			parameters.add(minorVer);
			
			CachedRowSet crs = JDBCUtil.executeQuery(query, parameters, "getBeforeLocksCommentsFromDocuments");

			int cont = 1;
			int docBackCheckIn = Integer.parseInt(HandlerDocuments.docBackCheckIn);
			int docCheckIn = Integer.parseInt(HandlerDocuments.docCheckIn);
			boolean commentsValid = true;
			while(crs.next()) {
				if(crs.getInt(2)==docBackCheckIn) {
					commentsValid = false;
				}
				if(commentsValid) {
					comments = crs.getString("comments");
					break;
				}
				if(crs.getInt(2)==docCheckIn) {
					commentsValid = true;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return comments;
}	
	
	// para badeja principal todos lo borradores huerfano
		public static synchronized Collection<DocumentsCheckOutsBean> getAllDocumentsCheckOutsUserBorradoresUnicos(
				String owner, String active, Hashtable struct, Hashtable prefijos,
				boolean searchAsAdmin) throws Exception {
			StringBuilder sql = new StringBuilder(100);
			sql.append("SELECT d.numGen,d.nameDocument,vd.MayorVer,vd.MinorVer");
			sql.append(" ,d.number,d.prefix,d.idNode,");
			sql.append(" p.idperson, p.nombres, p.apellidos, p.email,d.statu");
			sql.append(" FROM  documents d,versiondoc vd, person p ");
			sql.append(" WHERE vd.numDoc = d.numGen");
			sql.append(" AND d.lastOperation != '1' "); //documento no bloqueado
			sql.append(" AND d.statu = '1' ");
			if (!searchAsAdmin) {
				// los usuarios que no sean del tipo administrador, solo podran ver
				// archivos y flujos
				// de los cuales ellos sean los propietarios
				sql.append(" AND d.owner = '").append(owner).append("'");
			}
			sql.append(" AND p.nameuser = d.owner");
			sql.append(" AND d.active =  '").append(Constants.permission)
					.append("'"); 
			sql.append(" and d.versionPublic ='0' ");
			sql.append(" and vd.MinorVer ='0' ");
			sql.append(" and d.type <> '1001' and d.type <> '1002' ");
			Vector resp = new Vector();
			Vector datos = JDBCUtil.doQueryVector(sql.toString(),Thread.currentThread().getStackTrace()[1].getMethodName());
			String prefixDoc = null;
			String idNode = "";
			log.info("[getAllDocumentsCheckOutsUserBorradoresUnicos] "+ sql.toString());
			for (int row = 0; row < datos.size(); row++) {
				Properties properties = (Properties) datos.elementAt(row);
				DocumentsCheckOutsBean docCheck = new DocumentsCheckOutsBean();
				docCheck.setIdDocument(properties.getProperty("numGen"));
				docCheck.setNameDocument(properties.getProperty("nameDocument"));
				docCheck.setStatuBeanDocPendiente(properties.getProperty("statu") != null ? "Borrador":"");
				String minorVer = properties.getProperty("MinorVer").trim();
				if (ToolsHTML.isEmptyOrNull(minorVer)) {
					minorVer = "";
				}
				String mayorVer = properties.getProperty("MayorVer").trim();
				if (ToolsHTML.isEmptyOrNull(mayorVer)) {
					mayorVer = "";
				}
				docCheck.setMayorVer(mayorVer);
				docCheck.setMinorVer(minorVer);
				docCheck.setDateCheckOut(ToolsHTML.formatDateShow(
						properties.getProperty("dateCheckOut"), true));
				docCheck.setNumber(properties.getProperty("number") != null ? properties
						.getProperty("number").trim() : "");
				// docCheck.setPrefix(properties.getProperty("prefix")!=null?properties.getProperty("prefix").trim():"");
				idNode = properties.getProperty("idNode");
				// Se Busca el Prefijo del Documento seg�n como se encuentre
				// Definido el Mismo
				if (struct != null) {
					if (!prefijos.containsKey(idNode)) {
						prefixDoc = ToolsHTML.getPrefixToDoc(struct, idNode);
						prefijos.put(idNode, prefixDoc);
					} else {
						prefixDoc = (String) prefijos.get(idNode);
					}
				} else {
					prefixDoc = properties.getProperty("prefix") != null ? properties
							.getProperty("prefix").trim() : "";
				}
				if (!ToolsHTML.isEmptyOrNull(prefixDoc)) {
					docCheck.setPrefix(prefixDoc.trim());
				} else {
					docCheck.setPrefix("");
				}

				Person p = new Person(Integer.parseInt(properties
						.getProperty("idperson")),
						properties.getProperty("nombres"),
						properties.getProperty("apellidos"),
						properties.getProperty("email"));

				docCheck.setPersonBean(p);

				 resp.add(docCheck);
		    }
			// Ordenar la lista por nombre mayor version 
		    Collections.sort(resp, new Comparator<DocumentsCheckOutsBean>() {
		        @Override
		        public int compare(DocumentsCheckOutsBean doc1, DocumentsCheckOutsBean doc2) {
		        	// Comparar primero por Codigo
		            int compareMayorCodigo = doc2.getNumber().compareTo(doc1.getNumber());
		            if (compareMayorCodigo != 0) {
		                return compareMayorCodigo;
		            }
		            // Si MayorCodigo es igual, comparar por MayorVer
		            return doc1.getMayorVer().compareTo(doc2.getMayorVer());
		        }        	

		    
		    });

		    log.info("FIN getAllDocumentsCheckOutsUserBorradoresUnicos " +resp.size());
		    return resp;
		}
		
	// DOCUMNETOS PARA LA BANDEJA POR VENCER
	// Bandeja Princiapl - Documentos pendiente
	public static Collection<DocumentsCheckOutsBean> getAllDocumentsAprobadosPendientePorVencerUser(String owner, Hashtable struct, Hashtable prefijos,
			boolean searchAsAdmin) throws Exception {

		StringBuilder sql = new StringBuilder(100);
		sql.append("SELECT d.numGen,d.nameDocument,vd.MayorVer,vd.MinorVer,vd.dateExpires,");
		sql.append(" d.number,d.prefix,d.idNode,vd.dateExpiresDrafts,vd.statu,");
		sql.append(" p.idperson, p.nombres, p.apellidos, p.email, d.idRegisterClass, vd.numVer ");
		sql.append(" FROM person p, documents d,versiondoc vd");
		sql.append(" WHERE d.numGen = vd.numDoc");
		sql.append(" AND p.nameuser = d.owner");
	    sql.append(" AND d.lastOperation != '1' "); //documento no bloqueado 
		sql.append(" AND vd.numVer = (SELECT MAX(vdi.numVer) FROM versiondoc vdi WHERE vdi.numDoc = vd.numDoc)");
		
		String dateSystem = ToolsHTML.sdfShowConvert1.format(new Date());
		
		if (!ToolsHTML.isEmptyOrNull(owner) && !searchAsAdmin) {
			sql.append(" AND d.owner = '").append(owner).append("'");
		}
		
		sql.append(" AND "); // inicio de condicion OR
		sql.append(" vd.statu='").append(HandlerDocuments.docApproved).append("'");
		// sql.append("AND  vd.dateExpires ");
		sql.append(" AND d.active = '").append(Constants.permission).append("' ");
		sql.append(" AND d.type <> '").append(HandlerDocuments.TypeDocumentsImpresion).append("' ");

		log.debug("[getAllDocumentsAprobadosPendientePorVencerUser] " + sql.toString());
		Vector resp = new Vector();
		Vector datos = JDBCUtil.doQueryVector(sql.toString(), Thread.currentThread().getStackTrace()[1].getMethodName());
		String prefixDoc = null;
		String idNode = "";
		
		for (int row = 0; row < datos.size(); row++) {
			idNode = "";
			Properties properties = (Properties) datos.elementAt(row);
			String statusDoc = properties.getProperty("statu");
			DocumentsCheckOutsBean docCheck = new DocumentsCheckOutsBean();
			docCheck.setIdDocument(properties.getProperty("numGen"));
			docCheck.setNameDocument(properties.getProperty("nameDocument"));
			idNode = properties.getProperty("idNode");
			if (!ToolsHTML.isEmptyOrNull(statusDoc) && statusDoc.equals(HandlerDocuments.docApproved)) {
				docCheck.setDateCheckOut(ToolsHTML.formatDateShow(properties.getProperty("dateExpires"), true));
			}

			
			String minorVer = properties.getProperty("MinorVer").trim();
			
			
			// calcular fecha por vencer
			int units = 0;
			java.util.Date hoy = new Date();
			String hoySt = ToolsHTML.sdfShowConvert.format(hoy);
			units = ToolsHTML.getUnits("D");

			int ndias = 0;
			ndias = HandlerStruct.getIdLocalidad(idNode);
			SimpleDateFormat formato = new SimpleDateFormat("dd/MM/yyyy");
			Date dataExp = formato.parse(docCheck.getDateCheckOut());
			String dateExp1 = ToolsHTML.sdfShowConvert.format(new Date(dataExp.getTime()));
			String dateExpDoc = ToolsHTML.sdfShowConvert.format(new Date(dataExp.getTime()));

			Date calculada = ToolsHTML.sumUnitsToDate(units, dataExp, ndias);
		
			if (ToolsHTML.isEmptyOrNull(minorVer)) {
				minorVer = "";
			}
			String mayorVer = properties.getProperty("MayorVer").trim();
			if (ToolsHTML.isEmptyOrNull(mayorVer)) {
				mayorVer = "";
			}
			
			String status = properties.getProperty("statu").trim();
			if (ToolsHTML.isEmptyOrNull(status)) {
				status = "";
			}
			if (status.equalsIgnoreCase("5")) {
				docCheck.setIdRegisterClass(properties.getProperty("idRegisterClass") != null ? properties.getProperty("idRegisterClass") : "0");
				status = "Aprobado por Vencer";
			}
				
				
			docCheck.setStatuBeanDocPendiente(status);
			docCheck.setMayorVer(mayorVer);
			docCheck.setMinorVer(minorVer);

			
			docCheck.setNumber(properties.getProperty("number") != null ? properties.getProperty("number") : "");
			// Se Busca el Prefijo del Documento seg�n como se encuentre
			// Definido el Mismo
			if (struct != null) {
				if (!prefijos.containsKey(idNode)) {
					prefixDoc = ToolsHTML.getPrefixToDoc(struct, idNode);
					prefijos.put(idNode, prefixDoc);
				} else {
					prefixDoc = (String) prefijos.get(idNode);
				}
			} else {
				prefixDoc = properties.getProperty("prefix") != null ? properties.getProperty("prefix") : "";
			}

			if (!ToolsHTML.isEmptyOrNull(prefixDoc)) {
				docCheck.setPrefix(prefixDoc.trim());
			} else {
				docCheck.setPrefix("");
			}

			docCheck.setIdRegisterClass(properties.getProperty("idRegisterClass") != null ? properties.getProperty("idRegisterClass") : "0");

			Person p = new Person(Integer.parseInt(properties.getProperty("idperson")), properties.getProperty("nombres"), properties.getProperty("apellidos"),
					properties.getProperty("email"));

			docCheck.setPersonBean(p);

			docCheck.setNumVer(properties.getProperty("numVer"));

			if ((dateExp1.compareTo(hoySt) >= 0) && (hoySt.compareTo(ToolsHTML.sdfShowConvert.format(calculada)) >= 0)) {

				resp.add(docCheck);

			}
		}
		return resp;
	}
		
	public static Collection getRazonesDeCambioDocumentAcual(String idDoc)
			throws Exception {
		Vector result = new Vector();
		Vector resultFiltrado = new Vector();
		try {
			
			StringBuilder query = new StringBuilder(2028);
					
					switch (Constants.MANEJADOR_ACTUAL) {
					case Constants.MANEJADOR_MSSQL:
						query.append("select  numver,CAST(comments AS nvarchar(max)) as comments, ownerVersion,MayorVer,MinorVer");
						break;
					case Constants.MANEJADOR_POSTGRES:
						query.append("select numver, comments,dateApproved, ownerVersion,MayorVer,MinorVer");
						break;
					case Constants.MANEJADOR_MYSQL:
						query.append("select numver, comments,dateApproved, ownerVersion,MayorVer,MinorVer");
						break;
					}
					//.append("select numver, comments,dateApproved, ownerVersion,MayorVer,MinorVer")			
					query.append(" from versiondoc ")
						.append("where numDoc=  ")
						.append(idDoc)
						.append(" order by MayorVer desc ");
					

			log.debug("[getRazonesDeCambioDocumentAcual] query = " + query);
			Vector items = new Vector();
			Vector data = JDBCUtil.doQueryVector(query.toString(),Thread.currentThread().getStackTrace()[1].getMethodName());
			BeanRazonCambioDoc bean = null;
			String reasonToChange = "";

			for (int i = 0; i < data.size(); i++) {
				
				Properties properties = (Properties) data.elementAt(i);
				String menorVer = properties.getProperty("MinorVer");
				String mayorVer = properties.getProperty("MayorVer");
				reasonToChange=properties.getProperty("comments");
			
				 reasonToChange = reasonToChange.replaceAll("\"","”");
				 reasonToChange = reasonToChange.replaceAll("<br>","\n\n");
				 reasonToChange = reasonToChange.replaceAll("&nbsp;"," ");
				 reasonToChange = reasonToChange.replaceAll("\\<[^>]*>","");
				 
				if (menorVer.equals("0") && !mayorVer.equals("0")) {
					bean = new BeanRazonCambioDoc();
					bean.setRazonCambio(reasonToChange);
					bean.setFechaCambio(ToolsHTML.formatDateShow(properties.getProperty("dateApproved"), true));
					bean.setVersionCambio(properties.getProperty("MayorVer"));
					bean.setUsuarioCambio(properties.getProperty("ownerVersion"));
					result.add(bean);
				}
			}

		} catch (Exception ex) {
			log.error(ex.getMessage());
			ex.printStackTrace();
		}
		return result;
	}

	
	
	
	

}