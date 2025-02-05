package com.desige.webDocuments.persistent.managers;

import java.io.InputStream;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Locale;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.TreeMap;
import java.util.Vector;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.dbutils.DbUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import sun.jdbc.rowset.CachedRowSet;

import com.desige.webDocuments.document.actions.EditDocumentAction;
import com.desige.webDocuments.document.actions.LoadDataDocument;
import com.desige.webDocuments.document.actions.loadsolicitudImpresion;
import com.desige.webDocuments.document.forms.BaseDocumentForm;
import com.desige.webDocuments.document.forms.CheckOutDocForm;
import com.desige.webDocuments.document.forms.DocumentsCheckOutsBean;
import com.desige.webDocuments.mail.actions.SendMailTread;
import com.desige.webDocuments.mail.forms.MailForm;
import com.desige.webDocuments.persistent.utils.JDBCUtil;
import com.desige.webDocuments.structured.forms.BaseStructForm;
import com.desige.webDocuments.to.UserFlexWorkFlowsTO;
import com.desige.webDocuments.utils.ApplicationExceptionChecked;
import com.desige.webDocuments.utils.ApplicationExceptionUsersEmpty;
import com.desige.webDocuments.utils.Constants;
import com.desige.webDocuments.utils.DesigeConf;
import com.desige.webDocuments.utils.IDDBFactorySql;
import com.desige.webDocuments.utils.ToolsHTML;
import com.desige.webDocuments.utils.UserWithoutPermissionToFlexFlowException;
import com.desige.webDocuments.utils.beans.BeanNotifiedWF;
import com.desige.webDocuments.utils.beans.BeanWFRejectes;
import com.desige.webDocuments.utils.beans.DocOfRejection;
import com.desige.webDocuments.utils.beans.Search;
import com.desige.webDocuments.utils.beans.Search3;
import com.desige.webDocuments.utils.beans.Users;
import com.desige.webDocuments.utils.mail.SendMail;
import com.desige.webDocuments.workFlows.forms.BaseWorkFlow;
import com.desige.webDocuments.workFlows.forms.DataUserWorkFlowForm;
import com.desige.webDocuments.workFlows.forms.DataWorkFlowForm;
import com.desige.webDocuments.workFlows.forms.FlexFlow;
import com.desige.webDocuments.workFlows.forms.ParticipationForm;
import com.focus.qweb.bean.Person;

/**
 * Title: HandlerWorkFlows.java<br>
 * Copyright: (c) 2004 Desige Servicios de Computaci�n<br/>
 *
 * @author Ing. Nelson Crespo (NC)
 * @author Ing. Simon Rodr�guez (SR)
 * @version WebDocuments v1.0 <br>
 *          Changes:<br>
 *          <ul>
 *          <li>25/07/2004 (NC) Creation</li>
 *          <li>14/05/2005 (NC) Cambios para controlar las versiones del documento</li>
 *          <li>19/05/2005 (SR) Se agrego setIdnode</li>
 *          <li>25/05/2005 (NC) Se verific� condici�n al cambiar estado del documento a obsoleto</li>
 *          <li>05/07/2005 (NC) Corregido error al publicar Documento</li>
 *          <li>11/07/2005 (SR) Se agrego el bean eliminar..HandlerWorkFlows.java.setDataInWF</li>
 *          <li>11/07/2005 (SR) Se coloco el query para cargar el bean eliminar con hwf.eliminar HandlerWorkFlows.java.loadDataWorkFlow</li>
 *          <li>12/07/2005 (SR) Se coloco el sw form.setEnd("1"); y form.setEliminar(eliminar); para controlar la eliminacion y el finish de el flujo de
 *          trabajo.</li>
 *          <li>12/07/2005 (SR) Validamos que el flujo de pendientes no este cancelado</li>
 *          <li>13/07/2005 (SR) Se ordeno la salida de flujos de trabao por cracion y de forma descendente(dateCreation).</li>
 *          <li>13/07/2005 (SR) Se crea el proc eraseWorkFlowsDocuments.</li>
 *          <li>19/07/2005 (SR) Se crea el getUserInFlowPending.</li>
 *          <li>28/07/2005 (SR) Se agrega el mnumero correlativo si es aprobado getNumberToDocStatic en el metodo setResponseWF.</li>
 *          <li>03/08/2005 (SR) Se carga la version completa del documento(vermayor.vermenor).</li>
 *          <li>04/08/2005 (SR) Se coloca en el metodo loadDataWorkFlow un HashTable yaExiste para evitar los usuarios repetidos..</li>
 *          <li>20/04/2006 (SR) Se coloco synchronized algunos metodos para no bloquear la bd.</li>
 *          <li>03/05/2006 La bandeja de entrada de los mails no funcionaba, era vulnerable en caso que un usuario cambiara su mail, y en cada mail se manda el
 *          numero del documento en el titulo (SR)</li>
 *          <li>30/05/2006 (NC) Cambios para Correcto Formato de Fecha</li>
 *          <li>30/06/2006 (NC) Cambios para uso del Log y Documentos Vinculados</li>
 *          <ul>
 */
public class HandlerWorkFlows extends HandlerBD implements Serializable {
	private static Logger logger = LoggerFactory.getLogger(LoadDataDocument.class);

	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
		super.init(mapping, form, request, response);
		BaseDocumentForm forma = new BaseDocumentForm();
		forma.setIdDocument(request.getParameter("idDocument"));
		forma.setNumberGen(request.getParameter("idDocument"));
		forma.setNumVer(Integer.parseInt(request.getParameter("idVersion")));
		int numberCopies = 0;
		try {
			Users usuario = (Users) getSessionObject("user");

			if (usuario == null) {
				// no se encontro informacion del usuario en session
				// debemos redirigir esta peticion a la pagina de login
				// indicando el parametro url
				getSession().setAttribute(
						"url",
						request.getServletPath() + "?" + request.getQueryString() + Constants.PARAMETER_ALLOW_SESSION_CONNECTION
								+ Constants.OPEN_URL_PETITION_IN_A_NEW_WINDOW);

				return mapping.findForward("forcedLoginPage");
			}

			boolean swAdmin = false;
			try {
				swAdmin = DesigeConf.getProperty("application.admon").equalsIgnoreCase(usuario.getIdGroup());
			} catch (java.lang.NullPointerException e) {
				swAdmin = "2".equalsIgnoreCase(usuario.getIdGroup());
			}
			StringBuffer item = new StringBuffer("");
			// Se chequea si el usuario est� Logueado en el sistema.....
			// en caso contrario se almacena la direcci�n de petici�n
			// y se redirige el usuario a la p�gina inicial del Sistema
			if (usuario == null) {
				// no se encontro informacion del usuario en session
				// debemos redirigir esta peticion a la pagina de login
				// indicando el parametro url
				getSession().setAttribute(
						"url",
						request.getServletPath() + "?" + request.getQueryString() + Constants.PARAMETER_ALLOW_SESSION_CONNECTION
								+ Constants.OPEN_URL_PETITION_IN_A_NEW_WINDOW);
				return mapping.findForward("forcedLoginPage");
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}

	private ServletRequest getSession() {
		// TODO Auto-generated method stub
		return null;
	}

	private Users getSessionObject(String string) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 *
	 */
	private static final long serialVersionUID = 6449214471594561469L;
	static Logger log = LoggerFactory.getLogger("[V4.0 FTP] " + HandlerWorkFlows.class.getName());
	public static final String pending = "1";
	public static final String expires = "2";
	public static final String response = "3";
	public static final String canceled = "4";
	public static final String closed = "5";
	public static final String inQueued = "6";
	public static final String vencido = "7";
	public static final String rechazado = "11";
	public static final String completed = "30";
	public static final String replaced = "31";

	public static final String wfuPending = "1";
	public static final String wfuQueued = "2";
	public static final String wfuAcepted = "3";
	public static final String wfuCanceled = "4";
	public static final String wfuCanceled2 = "44";
	public static final String wfuClosed = "5";
	public static final String wfuExpired = "6";
	public static final String wfuVencido = "7";
	public static final String wfurechazado = "11";
	public static final String ownercancelcloseflow = "12";
	public static final String wfuRejected = "11";
	public static final String wfuReInit = "14";
	public static final String wfuCompleted = "30";
	public static final String wfuReplaced = "31";

	public static final String wfuAnnulled = "20";

	public static final int wfAcepted = Integer.parseInt(wfuAcepted);
	public static final int wfCanceled = Integer.parseInt(wfuCanceled);
	public static final int wfRechazado = Integer.parseInt(wfurechazado);
	public static final int wfReInit = Integer.parseInt(wfuReInit);
	public static final int ownerCancelCloseFlow = Integer.parseInt(ownercancelcloseflow);
	public static final int wfAnnulled = Integer.parseInt("20");
	public static final int wfCompleted = Integer.parseInt("30");
	public static final int wfReplaced = Integer.parseInt("31");

	public static final String wfSecuential = "0";
	public static final String wfConditional = "0";

	public static final String wfTypeRevision = "0";
	public static final String wfTypeAprobacion = "1";

	// ydavila Elmor - Subtipos de Flujo de Revisi�n (1=Solicitudes de Cambio, 2=Solicitudes de Eliminaci�n)
	public static final String wfSubtypeSolcambio = "3";
	public static final String wfSubtypeSolelimin = "4";

	/**
	 *
	 * @param con
	 * @param time
	 * @param form
	 * @throws Exception
	 */
	private static synchronized void updateUserWorkFlows(Connection con, Timestamp time, ParticipationForm form) throws Exception {
		StringBuilder update = new StringBuilder("UPDATE user_workflows SET result = ? , dateReplied = ?,comments=?").append(",statu = ? WHERE row= ?");
		PreparedStatement st = con.prepareStatement(JDBCUtil.replaceCastMysql(update.toString()));
		st.setString(1, form.getResult());
		st.setTimestamp(2, time);
		st.setString(3, form.getCommentsUser());
		st.setInt(4, form.getStatu());
		st.setInt(5, form.getRow());
		st.executeUpdate();

		DbUtils.closeQuietly(st);
	}

	/**
	 *
	 * @param con
	 * @param time
	 * @param form
	 * @throws Exception
	 */
	private static synchronized void updateUserFlexFlows(Connection con, Timestamp time, ParticipationForm form) throws Exception {
		StringBuilder update = new StringBuilder("UPDATE user_flexworkflows SET result = ? , dateReplied = ?,comments=?")
				.append(",statu = ? WHERE idFlexWF = ?");
		PreparedStatement st = con.prepareStatement(JDBCUtil.replaceCastMysql(update.toString()));
		st.setString(1, form.getResult());
		st.setTimestamp(2, time);
		st.setString(3, form.getCommentsUser());
		st.setInt(4, form.getStatu());
		st.setLong(5, form.getRow());
		st.executeUpdate();

		DbUtils.closeQuietly(st);
	}

	/*
	 * Metodo que permite modificar status, para los usuarios que intervienen en un FTP @param idFlexFlow
	 */
	public static synchronized void updateStatuUserFlexFlows(long idFlexFlow, int statu, String result, String comp, long id, int type) throws Exception {

		Connection con = null;
		PreparedStatement pstm = null;
		String res = result.replaceAll(",", "','");
		try {
			// recuperamos los ids a consultar
			StringBuffer queryIds = new StringBuffer();
			queryIds.append("SELECT uwf.idFlexWF FROM user_flexworkflows AS uwf INNER JOIN flexworkflow AS ff ON uwf.idWorkFlow = ff.idWorkFlow ");
			queryIds.append(" WHERE ff.IDFlexFlow = ").append(idFlexFlow);
			queryIds.append(" AND uwf.isOwner = '0' AND uwf.result IN ('").append(res).append("') ");

			// SE COLOCA EN ANULADO EL STATUS DE CADA USUARIO QUE RESPONDIO
			StringBuilder sql = new StringBuilder(2048);
			sql.append("UPDATE user_flexworkflows SET statu=?, wfactive='0' ");
			sql.append("WHERE idFlexWF in (").append(JDBCUtil.executeQueryRetornaIds(queryIds)).append(") ");
			if (type == 1) {
				sql.append(" AND idWorkFlow " + comp + id);
			} else if (type == 2) {
				sql.append(" AND IDFlexWF " + comp + id);
			}

			log.debug("[anularUser_FlexWF idFlexFlow =" + idFlexFlow + "] " + sql.toString());

			con = JDBCUtil.getConnection(Thread.currentThread().getStackTrace()[1].getMethodName());
			pstm = con.prepareStatement(JDBCUtil.replaceCastMysql(sql.toString()));

			pstm.setInt(1, statu);

			log.debug("Status de usuarios Modificados: " + pstm.executeUpdate());
		} catch (Exception e) {
			log.debug("Error al tratar de ejecutar query en updateStatuUserFlexFlows: " + e);
			e.printStackTrace();
		} finally {
			JDBCUtil.closeQuietly(con, pstm);
		}
	}

	/**
	 *
	 * @param usuario
	 * @param con
	 * @param time
	 * @param form
	 * @throws Exception
	 */
	private static synchronized void updateUserFlexFlowsCompleted(String usuario, Connection con, Timestamp time, ParticipationForm form) throws Exception {
		StringBuilder update = new StringBuilder("UPDATE user_flexworkflows  ").append("SET result = ? , dateReplied = ?, comments=?, statu = ? ")
				.append("WHERE idWorkFlow = ? ").append("AND idUser = ? and comments is null ");

		PreparedStatement st = con.prepareStatement(JDBCUtil.replaceCastMysql(update.toString()));
		st.setString(1, completed);
		st.setTimestamp(2, time);
		st.setString(3, form.getCommentsUser());
		st.setInt(4, form.getStatu());
		st.setLong(5, form.getIdWorkFlow());
		st.setString(6, usuario);

		st.executeUpdate();
		/*
		 * ydavila Ticket 001-00-003044 COMPLETAR FTP da un error "Ocurri� un error al guardar su respuesta" la conexi�n se cierra m�s adelante cuando se
		 * completa el bloque de actualizaci�n, No sdebe estar aqu�
		 */
		// DbUtils.closeQuietly(con);
	}

	/**
	 *
	 * @param IDFlexFlow
	 * @param con
	 * @param time
	 * @param form
	 * @throws Exception
	 */
	private static synchronized void annulledUserFlexFlowsCompleted(long IDFlexFlow, Connection con, Timestamp time, ParticipationForm form) throws Exception {
		StringBuilder update = new StringBuilder("UPDATE user_flexworkflows SET result = ? , dateReplied = ?,comments=null")
				.append(",statu = ? WHERE idWorkFlow IN (select idWorkFlow from flexworkflow where IDFlexFlow = ?) ").append("and ( statu=? or result=? ) ")
				.append(" AND result != '").append(response).append("' ").append(" AND statu != '").append(response).append("' ");

		PreparedStatement st = con.prepareStatement(JDBCUtil.replaceCastMysql(update.toString()));
		st.setString(1, wfuAnnulled); // form.getResult()
		st.setTimestamp(2, time);
		st.setString(3, wfuAnnulled);
		st.setLong(4, IDFlexFlow);
		st.setString(5, wfuPending);
		st.setString(6, wfuPending);

		st.executeUpdate();
		DbUtils.closeQuietly(st);
	}

	/**
	 *
	 * @param con
	 * @param newST
	 * @param idDocument
	 * @param idWF
	 * @param time
	 * @throws Exception
	 */
	private static synchronized void updateFlexFlows(Connection con, String newST, long idDocument, long idWF, Timestamp time) throws Exception {
		StringBuilder update = new StringBuilder("UPDATE flexworkflow SET statu = ? ,DateCreation = ?").append(" WHERE idDocument = ? AND idWorkFlow = ?");
		PreparedStatement st = con.prepareStatement(JDBCUtil.replaceCastMysql(update.toString()));
		st.setString(1, newST);
		st.setTimestamp(2, time);
		st.setLong(3, idDocument);
		st.setLong(4, idWF);
		st.executeUpdate();
		DbUtils.closeQuietly(st);
	}

	/**
	 *
	 * @param con
	 * @param time
	 * @param form
	 * @param idWorkFlow
	 * @throws Exception
	 */
	public static synchronized void updateUserWorkFlows(Connection con, Timestamp time, ParticipationForm form, int idWorkFlow) throws Exception {

		// firmamos todos los usuarios que sean diferentes al due�o de forma
		// manual
		StringBuilder update = new StringBuilder("DELETE FROM user_workflows ").append(" WHERE idWorkFlow = ? AND isOwner <> '1'");
		PreparedStatement st = con.prepareStatement(JDBCUtil.replaceCastMysql(update.toString()));
		st.setInt(1, idWorkFlow);
		st.executeUpdate();

		// dejamos a la firma del due�o como pendiente
		update.setLength(0);
		//ydavila Ticket 001-00-003366 Si no hay responsable de impresi�n de Configuracion ...
		if (HandlerParameters.getRespImpresDocAdm1()!=null) {
			update.append("UPDATE user_workflows SET statu = ? ")
			.append(", iduser = ? ")
			.append(" WHERE idWorkFlow = ? AND isOwner='1'");
			st = con.prepareStatement(JDBCUtil.replaceCastMysql(update.toString()));
			st.setInt(1, Integer.parseInt(HandlerWorkFlows.wfuPending));
			st.setString(2, HandlerParameters.getRespImpresDocAdm1());
			st.setInt(3, idWorkFlow);			 
		//Si hay responsable en administraci�n cambio al propietario por el Responsable configurado en Administraci�n			
		}else{	
			update.append("UPDATE user_workflows SET statu = ? ").append(
					" WHERE idWorkFlow = ? AND isOwner='1'");		
			st = con.prepareStatement(JDBCUtil.replaceCastMysql(update.toString()));
			st.setInt(1, Integer.parseInt(HandlerWorkFlows.wfuPending));
			st.setInt(2, idWorkFlow);	
		}
		//}

		st.executeUpdate();
		DbUtils.closeQuietly(st);
	}

	// private static String getIdsResponse(String idWF) throws
	// ApplicationExceptionChecked {
	// StringBuilder sql = new StringBuilder(100);
	// StringBuilder result = new StringBuilder();
	// sql.append("SELECT row  FROM user_workflows uw WHERE result IN (");
	// sql.append(wfuAcepted).append(",").append(wfuCanceled).append(")");
	// sql.append(" AND idWorkflow = ").append(idWF);
	// try {
	// Vector datos = JDBCUtil.doQueryVector(sql.toString(),Thread.currentThread().getStackTrace()[1].getMethodName());
	// } catch (Exception ex) {
	// ex.printStackTrace();
	// throw new ApplicationExceptionChecked("");
	// }
	// return result.toString();
	// }

	private static void canceledUserWorkFlows(String statuToChange, Connection con, ParticipationForm form) throws Exception {
		StringBuilder update = new StringBuilder("UPDATE user_workflows SET result = ? ").append(",statu = ? ");
		// update.append(",statu = ? WHERE idWorkFlow = ? AND statu IN (");
		// update.append(wfuPending).append(",").append(wfuQueued).append(")");
		if (wfuPending.equalsIgnoreCase(statuToChange)) {
			update.append(",pending = '").append(Constants.permission).append("'");
		}
		update.append(" WHERE idWorkFlow = ? AND statu IN ('").append(statuToChange).append("')");
		log.debug("[canceledUserWorkFlows] Row = " + form.getRow());
		if (form.getRow() > 0) {
			//update.append(" AND `row`> ").append(form.getRow());
			update.append(" AND row> ").append(form.getRow());
		}
		PreparedStatement st = con.prepareStatement(JDBCUtil.replaceCastMysql(update.toString()));
		st.setString(1, form.getResult());
		st.setInt(2, form.getStatu());
		st.setInt(3, form.getIdWorkFlow());
		st.executeUpdate();
	}

	/**
	 * Para Cancelar a los Usuarios Pendientes en el Flujo de Trabajo Param�trico
	 *
	 * @param statuToChange
	 * @param con
	 * @param form
	 * @throws Exception
	 */
	private static void canceledUserFlexFlows(String statuToChange, Connection con, ParticipationForm form) throws Exception {
		PreparedStatement st = null;
		StringBuilder update = new StringBuilder("UPDATE user_flexworkflows SET result = ? ");
		update.append(",statu = ? ");
		if (wfuPending.equalsIgnoreCase(statuToChange)) {
			update.append(",pending = '").append(Constants.permission).append("' ");
		}
		update.append(" WHERE idWorkFlow IN (SELECT idWorkFlow FROM flexworkflow");
		update.append(" WHERE IDFlexFlow = ").append(form.getIdFlexFlow()).append(")");
		update.append("AND statu IN ('").append(statuToChange.replaceAll(",", "','")).append("')");
		log.debug("[canceledUserWorkFlows] Row = " + form.getRow());
		if (form.getRow() > 0) {
			update.append(" AND idFlexWF > ").append(form.getRow());
		}
		st = con.prepareStatement(JDBCUtil.replaceCastMysql(update.toString()));
		log.debug("[canceledUserWorkFlows]: " + update.toString());
		st.setString(1, form.getResult());
		st.setString(2, String.valueOf(form.getStatu()));
		// st.setLong(3,form.getIdFlexFlow());
		st.executeUpdate();
	}

	private static void updateUserWorkFlows(Connection con, String statu, String row) throws Exception {
		PreparedStatement st = null;
		if (!ToolsHTML.isEmptyOrNull(row) && (!"0".equalsIgnoreCase(row))) {
			StringBuilder update = new StringBuilder("UPDATE user_workflows ");
			update.append(" SET statu = ? WHERE row= ?");
			log.debug("[updateUserWorkFlows]" + update.toString());
			st = con.prepareStatement(JDBCUtil.replaceCastMysql(update.toString()));
			st.setString(1, statu.trim());
			st.setInt(2, Integer.parseInt(row));
			st.executeUpdate();
		}
	}

	private static synchronized void updateUserFlexWorkFlows(Connection con, String statu, String row) throws Exception {
		PreparedStatement st = null;
		log.debug("[updateUserWorkFlows] ");
		// //System.out.println("updateUserFlexWorkFlows row, statu: " + row +
		// ", " + statu);
		if (!ToolsHTML.isEmptyOrNull(row) && (!"0".equalsIgnoreCase(row))) {
			StringBuilder update = new StringBuilder("UPDATE user_flexworkflows ");
			update.append(" SET statu = ? WHERE idFlexWF = ?");
			st = con.prepareStatement(JDBCUtil.replaceCastMysql(update.toString()));
			st.setString(1, statu.trim());
			st.setInt(2, Integer.parseInt(row));
			st.executeUpdate();
			// //System.out.println("updateUserFlexWorkFlows sql: " + update);

			updateExpireWF(st, con, 0, Integer.parseInt(row));
		}
	}

	private static synchronized void updateUsersFlexWorkFlows(Connection con, String statu, int idWorkFlow) throws Exception {
		PreparedStatement st = null;
		log.debug("[updateUsersFlexWorkFlows] ");
		if (idWorkFlow > 0) {
			StringBuilder update = new StringBuilder("UPDATE user_flexworkflows ");
			update.append(" SET statu = ? WHERE idWorkFlow = ? and statu = '").append(wfuQueued).append("' ");
			st = con.prepareStatement(JDBCUtil.replaceCastMysql(update.toString()));
			st.setString(1, statu.trim());
			st.setInt(2, idWorkFlow);
			st.executeUpdate();
		}
	}

	// processList(forma.getUsersSelected(),st,con,num,0,forma.getSecuential(),usuario.getUser());
	private static String processList(Object[] datos, PreparedStatement st, Connection con, int idWorkFlow, int type, int secuential, String owner,
			boolean withGroups, int posInit) throws Exception {
		StringBuilder items = new StringBuilder("(");
		if (datos != null) {
			if ((type == 0) && (datos.length > 0)) {
				items.append("'");
			}
			int row = 0;
			for (row = 0; row < datos.length; row++) {
				Object dato = datos[row];
				// int numRecord = IDDBFactorySql.getNextID("userWFs");
				int numRecord = HandlerStruct.proximo("userWFs", "user_workflows", "row");
				StringBuilder insert = new StringBuilder();
				
				switch (Constants.MANEJADOR_ACTUAL) {
				case Constants.MANEJADOR_MSSQL:
					insert.append("INSERT INTO user_workflows (row,idUser,idWorkFlow,type,result,orden,Statu) VALUES(?,?,?,CAST(? as bit),?,?,?)");
					//sql.append("(p.Apellidos + ' ' + p.Nombres) AS nombre,");
					break;
				case Constants.MANEJADOR_POSTGRES:
					insert.append("INSERT INTO user_workflows (row,idUser,idWorkFlow,type,result,orden,Statu) VALUES(?,?,?,CAST(? as bit),?,?,?)");
					//sql.append("(p.Apellidos || ' ' || p.Nombres) AS nombre,");
					break;
				case Constants.MANEJADOR_MYSQL:
					insert.append("INSERT INTO user_workflows (`row`,idUser,idWorkFlow,`type`,`result`,orden,Statu) VALUES(?,?,?,CAST(? as bit),?,?,?)");
					//sql.append("CONCAT(p.Apellidos , ' ' , p.Nombres) AS nombre,");
					//StringBuilder insert = new StringBuilder("INSERT INTO user_workflows (`row`,idUser,idWorkFlow,`type`,`result`,orden,Statu) VALUES(?,?,?,CAST(? as bit),?,?,?)");
					break;
				}
				
				
				
				//StringBuilder insert = new StringBuilder("INSERT INTO user_workflows (`row`,idUser,idWorkFlow,`type`,`result`,orden,Statu) VALUES(?,?,?,CAST(? as bit),?,?,?)");
				st = con.prepareStatement(JDBCUtil.replaceCastMysql(insert.toString()));
				st.setInt(1, numRecord);
				st.setString(2, dato.toString());
				st.setInt(3, idWorkFlow);
				st.setInt(4, type);
				st.setString(5, pending);
				st.setInt(6, row + posInit);
				if (secuential == 0) {// El Flujo de Trabajo es Secuencial
					if (row == 0 && !withGroups) {
						st.setString(7, wfuPending);
						// Se guardan los Usuarios que participan en el Flujo de
						// Trabajo
						if (type == 0) {
							items.append(dato.toString()).append("','");
						} else {
							items.append(dato.toString()).append(",");
						}
					} else {
						st.setString(7, wfuQueued);
					}
				} else {
					st.setString(7, wfuPending);

					if (type == 0) {
						items.append(dato.toString()).append("','");
					} else {
						items.append(dato.toString()).append(",");
					}

				}
				st.executeUpdate();

			}
			// Se crea la entrada para cerrar el Flujo de Trabajo
			// int numRecord = IDDBFactorySql.getNextID("userWFs");
			if (owner != null) {
				int numRecord = HandlerStruct.proximo("userWFs", "user_workflows", "row");
				//StringBuilder insert = new StringBuilder("INSERT INTO user_workflows (`row`,idUser,idWorkFlow,`type`,`result`,orden,Statu,isOwner) VALUES(?,?,?,CAST(? as bit),?,?,?,CAST(? as bit))");
				StringBuilder insert = new StringBuilder();
				
				switch (Constants.MANEJADOR_ACTUAL) {
				case Constants.MANEJADOR_MSSQL:
					insert.append("INSERT INTO user_workflows (row,idUser,idWorkFlow,type,result,orden,Statu,isOwner) VALUES(?,?,?,CAST(? as bit),?,?,?,CAST(? as bit))");
					break;
				case Constants.MANEJADOR_POSTGRES:
					insert.append("INSERT INTO user_workflows (row,idUser,idWorkFlow,type,result,orden,Statu,isOwner) VALUES(?,?,?,CAST(? as bit),?,?,?,CAST(? as bit))");
					break;
				case Constants.MANEJADOR_MYSQL:
					insert.append("INSERT INTO user_workflows (`row`,idUser,idWorkFlow,`type`,`result`,orden,Statu,isOwner) VALUES(?,?,?,CAST(? as bit),?,?,?,CAST(? as bit))");
					break;
				}
				
				st = con.prepareStatement(JDBCUtil.replaceCastMysql(insert.toString()));
				st.setInt(1, numRecord);
				st.setString(2, owner);
				st.setInt(3, idWorkFlow);
				st.setInt(4, type);
				st.setString(5, pending);
				st.setInt(6, row + posInit);// datos.length);
				st.setString(7, wfuQueued);
				st.setInt(8, 1);
				st.executeUpdate();
			}
		}
		if (type == 0) {
			if (items.length() > 1) {
				items.replace(items.length() - 2, items.length(), ")");
			} else {
				items = new StringBuilder("");
			}
		} else {
			if (items.length() > 1) {
				items.replace(items.length() - 1, items.length(), ")");
			} else {
				items = new StringBuilder("");
			}
		}
		return items.toString().trim();
	}

	//ydavila
	private static String processList1(Object[] datos, PreparedStatement st,
			Connection con, int idWorkFlow, int type, int secuential,
			String owner, boolean withGroups, int posInit, int typeWF) throws Exception {
		StringBuilder items = new StringBuilder("(");
		
		if (datos != null) {
			if ((type == 0) && (datos.length > 0)) {
				items.append("'");
			}
			int row = 0;
			for (row = 0; row < datos.length; row++) {
				Object dato = datos[row];
				// int numRecord = IDDBFactorySql.getNextID("userWFs");
				int numRecord = HandlerStruct.proximo("userWFs",
						"user_workflows", "row");
				//StringBuilder insert = new StringBuilder("INSERT INTO user_workflows (`row`,idUser,idWorkFlow,`type`,`result`,orden,Statu) VALUES(?,?,?,CAST(? as bit),?,?,?)");
				StringBuilder insert = new StringBuilder();
				
				switch (Constants.MANEJADOR_ACTUAL) {
				case Constants.MANEJADOR_MSSQL:
					insert.append("INSERT INTO user_workflows (row,idUser,idWorkFlow,type,result,orden,Statu) VALUES(?,?,?,CAST(? as bit),?,?,?)");
					break;
				case Constants.MANEJADOR_POSTGRES:
					insert.append("INSERT INTO user_workflows (row,idUser,idWorkFlow,type,result,orden,Statu) VALUES(?,?,?,CAST(? as bit),?,?,?)");
					break;
				case Constants.MANEJADOR_MYSQL:
					insert.append("INSERT INTO user_workflows (`row`,idUser,idWorkFlow,`type`,`result`,orden,Statu) VALUES(?,?,?,CAST(? as bit),?,?,?)");
					break;
				}
				
				st = con.prepareStatement(JDBCUtil.replaceCastMysql(insert.toString()));
				st.setInt(1, numRecord);
				//ydavila
				//quito validaci�n de Responsable de Cambio o Eliminaci�n en Administraci�n
				st.setString(2, dato.toString());
				/*if (typeWF==3){
					if (HandlerParameters.getRespSolCambioAdm1()!=null) {
						st.setString(2, HandlerParameters.getRespSolCambioAdm1());
					}else{
						st.setString(2, dato.toString());	
					}
				}
				if (typeWF==4){
					if (HandlerParameters.getRespSolEliminAdm1()!=null) {
						st.setString(2, HandlerParameters.getRespSolEliminAdm1());
					}else{
						st.setString(2, dato.toString());	
					}
				}*/
				st.setInt(3, idWorkFlow);
				st.setInt(4, type);
				st.setString(5, pending);
				st.setInt(6, row + posInit);
				if (secuential == 0) {// El Flujo de Trabajo es Secuencial
					if (row == 0 && !withGroups) {
						st.setString(7, wfuPending);
						// Se guardan los Usuarios que participan en el Flujo de
						// Trabajo						
						if (type == 0) {
							items.append(dato.toString()).append("','");
						} else {
							items.append(dato.toString()).append(",");
						}
					} else {
						st.setString(7, wfuQueued);
					}
				} else {
					st.setString(7, wfuPending);

					if (type == 0) {
						items.append(dato.toString()).append("','");
					} else {
						items.append(dato.toString()).append(",");
					}

				}
				st.executeUpdate();
			}			

			// Se crea la entrada para cerrar el Flujo de Trabajo
			// int numRecord = IDDBFactorySql.getNextID("userWFs");
			if (owner != null) {
				int numRecord = HandlerStruct.proximo("userWFs",
						"user_workflows", "row");
				//StringBuilder insert = new StringBuilder("INSERT INTO user_workflows (`row`,idUser,idWorkFlow,`type`,`result`,orden,Statu,isOwner) VALUES(?,?,?,CAST(? as bit),?,?,?,CAST(? as bit))");
				StringBuilder insert = new StringBuilder();
				
				switch (Constants.MANEJADOR_ACTUAL) {
				case Constants.MANEJADOR_MSSQL:
					insert.append("INSERT INTO user_workflows (row,idUser,idWorkFlow,type,result,orden,Statu,isOwner) VALUES(?,?,?,CAST(? as bit),?,?,?,CAST(? as bit))");
					break;
				case Constants.MANEJADOR_POSTGRES:
					insert.append("INSERT INTO user_workflows (row,idUser,idWorkFlow,type,result,orden,Statu,isOwner) VALUES(?,?,?,CAST(? as bit),?,?,?,CAST(? as bit))");
					break;
				case Constants.MANEJADOR_MYSQL:
					insert.append("INSERT INTO user_workflows (`row`,idUser,idWorkFlow,`type`,`result`,orden,Statu,isOwner) VALUES(?,?,?,CAST(? as bit),?,?,?,CAST(? as bit))");
					break;
				}
				
				st = con.prepareStatement(JDBCUtil.replaceCastMysql(insert.toString()));
				st.setInt(1, numRecord);
				st.setString(2, owner);
				st.setInt(3, idWorkFlow);
				st.setInt(4, type);
				st.setString(5, pending);
				st.setInt(6, row + posInit);// datos.length);
				st.setString(7, wfuQueued);
				st.setInt(8, 1);
				st.executeUpdate();
			}
		}
		if (type == 0) {
			if (items.length() > 1) {
				items.replace(items.length() - 2, items.length(), ")");
			} else {
				items = new StringBuilder("");
			}
		} else {
			if (items.length() > 1) {
				items.replace(items.length() - 1, items.length(), ")");
			} else {
				items = new StringBuilder("");
			}
		}
		return items.toString().trim();
	}
	
	public static Collection getCollection(Object[] datos) {
		Vector result = new Vector();
		Search bean = null;
		for (int i = 0; datos != null && i < datos.length; i++) {
			bean = new Search(String.valueOf(datos[i]), String.valueOf(datos[i]));
			result.add(bean);
		}
		return result;
	}

	public static Collection getUsersSelectedCollection(Object[] datos) {
		Vector result = new Vector();
		Vector result2 = new Vector();
		if (datos != null && datos.length > 0) {
			StringBuilder items = new StringBuilder("('");
			for (int row = 0; row < datos.length; row++) {
				Object dato = datos[row];
				items.append(dato.toString()).append("','");
			}
			items.replace(items.length() - 2, items.length(), ")");
			StringBuilder sql = new StringBuilder(100);
			sql.append("SELECT p.nameUser,");
			switch (Constants.MANEJADOR_ACTUAL) {
			case Constants.MANEJADOR_MSSQL:
				sql.append("(p.Apellidos + ' ' + p.Nombres) AS nombre,");
				break;
			case Constants.MANEJADOR_POSTGRES:
				sql.append("(p.Apellidos || ' ' || p.Nombres) AS nombre,");
				break;
			case Constants.MANEJADOR_MYSQL:
				sql.append("CONCAT(p.Apellidos , ' ' , p.Nombres) AS nombre,");
				break;
			}
			sql.append("c.cargo");
			sql.append(" FROM person p,tbl_cargo c WHERE cast(p.cargo as int) = c.idcargo AND p.accountActive = '1' AND nameUser IN ").append(items);
			try {
				Vector info = JDBCUtil.doQueryVector(sql.toString(),Thread.currentThread().getStackTrace()[1].getMethodName());
				for (int row = 0; row < info.size(); row++) {
					Properties properties = (Properties) info.elementAt(row);
					Search bean = new Search(properties.getProperty("nameUser"), properties.getProperty("nombre"));
					bean.setAditionalInfo(properties.getProperty("cargo"));
					result.add(bean);
				}
				String dato = null;
				int size = result.size();
				while (result2.size() != size) {
					for (int row = 0; row < datos.length; row++) {
						dato = (String) datos[row];
						if (dato != null) {
							for (int pos = 0; pos < result.size(); pos++) {
								Search bean = (Search) result.get(pos);
								if (dato.compareTo(bean.getId()) == 0) {
									if (!result2.contains(bean)) {
										result2.add(bean);
										result.remove(pos);
										break;
									}
								}
							}
						}
					}
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
		return result2;
	}

	/* Creacion de flujo parametrico por usuario */
	private static synchronized String processList(Object[] datos, PreparedStatement st, Connection con, long idWorkFlow, int type, int secuential, byte owner,
			long idFather, int orden) throws Exception {
		return processList(datos, st, con, idWorkFlow, type, secuential, owner, idFather, orden, null, null);
	}

	private static synchronized String processList(Object[] datos, PreparedStatement st, Connection con, long idWorkFlow, int type, int secuential, byte owner,
			long idFather, int orden, Object[] datosExpire, BaseWorkFlow baseWorkFlow) throws Exception {
		log.debug("[Begin] processList");
		boolean soloUsuarioTemporal = false;
		StringBuilder items = new StringBuilder("(");
		if (datos != null) {
			if ((type == 0) && (datos.length > 0)) {
				items.append("'");
			}
			for (int row = 0; row < datos.length; row++) {
				Object dato = datos[row];
				if (dato.toString().trim().equals(Constants.ID_USER_TEMPORAL)) {
					soloUsuarioTemporal = true;
				}
			}
			for (int row = 0; row < datos.length; row++) {
				Object dato = datos[row];
				Object datoExpire = null;
				boolean isExpire = false;
				if (datosExpire != null && datosExpire.length > row) {
					datoExpire = datosExpire[row];
					isExpire = (datoExpire != null && !datoExpire.toString().trim().equals(""));
				}
				if (!soloUsuarioTemporal || dato.toString().trim().equals(Constants.ID_USER_TEMPORAL) && soloUsuarioTemporal) {
					long numRecord = IDDBFactorySql.getNextIDLong(con, "user_flexworkflows");
					// int numRecord =
					// HandlerStruct.proximo("userWFs","user_flexworkflows","row");
					StringBuilder insert = new StringBuilder("INSERT INTO user_flexworkflows (idFlexWF,idUser,idWorkFlow,type,result,orden,Statu");
					insert.append(",isOwner,reading,active,pending,wfActive,IdFather,uw_Circle").append(isExpire ? ", dateExpireUser" : "");
					insert.append(
							") VALUES(?,?,?,CAST(? as bit),?,?,?,CAST(? as bit),CAST(? as bit),CAST(? as bit),CAST(? as bit),CAST(? as bit),?,CAST(? as bit)")
							.append(isExpire ? ",?)" : ")");
					log.debug("processList: " + insert);
					st = con.prepareStatement(JDBCUtil.replaceCastMysql(insert.toString()));
					st.setLong(1, numRecord);
					st.setString(2, dato.toString());
					st.setLong(3, idWorkFlow);
					st.setInt(4, type);
					st.setString(5, pending);
					if (idFather == 0) {
						st.setInt(6, orden == 0 ? row : orden);
					} else {
						st.setInt(6, row);
					}
					// st.setInt(6, orden == 0 ? row : orden);
					// st.setInt(6, orden);
					if (secuential == 0) {// El Flujo de Trabajo es Secuencial
						if (row == 0) {
							st.setString(7, wfuPending);
							// Se guardan los Usuarios que participan en el
							// Flujo de Trabajo
							if (type == 0) {
								items.append(dato.toString()).append("','");
							} else {
								items.append(dato.toString()).append(",");
							}
						} else {
							st.setString(7, wfuQueued);
						}
					} else {
						st.setString(7, wfuPending);
						if (type == 0) {
							items.append(dato.toString()).append("','");
						} else {
							items.append(dato.toString()).append(",");
						}
					}
					st.setInt(8, owner);
					st.setInt(9, Constants.permission);
					st.setInt(10, Constants.permission);
					st.setInt(11, Constants.permission);
					st.setInt(12, Constants.permission);
					// if (idFather != 0) {
					st.setLong(13, idFather);
					// }
					st.setInt(14, Constants.permission);
					if (isExpire) {
						Date hoy = new Date();
						String vence = String.valueOf(datoExpire).concat(" ").concat(ToolsHTML.sdfTime24_2.format(hoy));
						Date date = ToolsHTML.sdfShowConvert1.parse(vence);
						st.setTimestamp(15, new Timestamp(date.getTime()));
					}
					st.executeUpdate();

					// la fecha mas proxima sera la fecha de expiracion del
					// flujo
					if (isExpire) {
						String date = updateExpireWF(st, con, idWorkFlow, 0);
						if (baseWorkFlow != null && date != null) {
							baseWorkFlow.setDateExpireWF(date);
						}
					}
				}
			}
			// Se crea la entrada para cerrar el Flujo de Trabajo
			// int numRecord = IDDBFactorySql.getNextID("userWFs");
			/*
			 * if (owner!=null) { int numRecord = HandlerStruct.proximo("userWFs","user_workflows","row"); StringBuilder insert = new StringBuilder(
			 * "INSERT INTO user_workflows (`row`,idUser,idWorkFlow,`type`,`result`,orden,Statu,isOwner) VALUES(?,?,?,?,?,?,?,?)" ); st =
			 * con.prepareStatement(insert.toString()); st.setInt(1,numRecord); st.setString(2,owner); st.setInt(3,idWorkFlow); st.setInt(4,type);
			 * st.setString(5,pending); st.setInt(6,datos.length); st.setString(7,wfuQueued); st.setInt(8,1); st.executeUpdate(); }
			 */
		}
		if (type == 0) {
			if (items.length() > 1) {
				items.replace(items.length() - 2, items.length(), ")");
			} else {
				items = new StringBuilder("");
			}
		} else {
			if (items.length() > 1) {
				items.replace(items.length() - 1, items.length(), ")");
			} else {
				items = new StringBuilder("");
			}
		}
		return items.toString();
	}

	private static int processList(Collection datos, PreparedStatement st, Connection con, int idWorkFlow, int type, int secuential, String owner, int numItem,
			StringBuilder emails, int row, boolean withUsers) throws Exception {
		StringBuilder items = new StringBuilder("(");
		if (datos != null) {
			if ((type == 0) && (datos.size() > 0)) {
				items.append("'");
			}
			for (Iterator iterator = datos.iterator(); iterator.hasNext();) {
				Search3 dato = (Search3) iterator.next();
				if (!owner.equalsIgnoreCase(dato.getId())) {

					int numRecord = HandlerStruct.proximo("userWFs", "user_workflows", "row");
					//StringBuilder insert = new StringBuilder("INSERT INTO user_workflows (`row`,idUser,idWorkFlow,`type`,`result`,orden,Statu) VALUES(?,?,?,cast(? as bit),?,?,?)");
					StringBuilder insert = new StringBuilder();
					
					switch (Constants.MANEJADOR_ACTUAL) {
					case Constants.MANEJADOR_MSSQL:
						insert.append("INSERT INTO user_workflows (row,idUser,idWorkFlow,type,result,orden,Statu) VALUES(?,?,?,cast(? as bit),?,?,?)");
						break;
					case Constants.MANEJADOR_POSTGRES:
						insert.append("INSERT INTO user_workflows (row,idUser,idWorkFlow,type,result,orden,Statu) VALUES(?,?,?,cast(? as bit),?,?,?)");
						break;
					case Constants.MANEJADOR_MYSQL:
						insert.append("INSERT INTO user_workflows (`row`,idUser,idWorkFlow,`type`,`result`,orden,Statu) VALUES(?,?,?,cast(? as bit),?,?,?)");
						break;
					}

					
					st = con.prepareStatement(JDBCUtil.replaceCastMysql(insert.toString()));
					st.setInt(1, numRecord);
					st.setString(2, dato.getId());
					st.setInt(3, idWorkFlow);
					st.setInt(4, type);
					st.setString(5, pending);
					st.setInt(6, row);
					// forzamos a que en grupo sea tipo secuencia solamente
					secuential = 0; // mas adelante cambiar esta arbitrariedad
									// de tal manera que sea dinamico.
					if (secuential == 0) {
						if (numItem == 0) {
							st.setString(7, wfuPending);
						} else {
							st.setString(7, wfuQueued);
						}
					} else {
						st.setString(7, wfuPending);
					}
					st.executeUpdate();
					emails.append(dato.getDescript()).append(";");
					row++;
					// el numItem me indica si es 0 que es la primera vez que
					// insertamos un registro,
					// si es asi,por lo general se le coloca status pendiente
					// sugun condiciones alla arriba
					++numItem;
				}
			}
			// Se crea la entrada para cerrar el Flujo de Trabajo
			// int numRecord = IDDBFactorySql.getNextID("userWFs");
			StringBuilder sqlSelect = new StringBuilder("SELECT idWorkFlow FROM user_workflows WHERE ");
			sqlSelect.append(" idWorkFlow=").append(idWorkFlow).append(" AND iduser='").append(owner).append("' AND isOwner='1'");
			PreparedStatement select = con.prepareStatement(JDBCUtil.replaceCastMysql(sqlSelect.toString()));
			ResultSet rs = select.executeQuery();
			// una sola vez se inserta el due�o en el flujo
			if (!rs.next()) {
				int numRecord = HandlerStruct.proximo("userWFs", "user_workflows", "row");
				//StringBuilder insert = new StringBuilder("INSERT INTO user_workflows (`row`,idUser,idWorkFlow,`type`,`result`,orden,Statu,isOwner) VALUES(?,?,?,cast(? as bit),?,?,?,cast(? as bit))");
				StringBuilder insert = new StringBuilder();
				
				switch (Constants.MANEJADOR_ACTUAL) {
				case Constants.MANEJADOR_MSSQL:
					insert.append("INSERT INTO user_workflows (row,idUser,idWorkFlow,type,result,orden,Statu,isOwner) VALUES(?,?,?,cast(? as bit),?,?,?,cast(? as bit))");
					break;
				case Constants.MANEJADOR_POSTGRES:
					insert.append("INSERT INTO user_workflows (row,idUser,idWorkFlow,type,result,orden,Statu,isOwner) VALUES(?,?,?,cast(? as bit),?,?,?,cast(? as bit))");
					break;
				case Constants.MANEJADOR_MYSQL:
					insert.append("INSERT INTO user_workflows (`row`,idUser,idWorkFlow,`type`,`result`,orden,Statu,isOwner) VALUES(?,?,?,cast(? as bit),?,?,?,cast(? as bit))");
					break;
				}
				
				st = con.prepareStatement(JDBCUtil.replaceCastMysql(insert.toString()));
				st.setInt(1, numRecord);
				st.setString(2, owner);
				st.setInt(3, idWorkFlow);
				st.setInt(4, type);
				st.setString(5, pending);
				st.setInt(6, datos.size());
				st.setString(7, wfuQueued);
				st.setInt(8, 1);
				st.executeUpdate();
			}
			rs.close();
		}
		return row;
	}

	public static synchronized int addUsersFlexWorkFlows(ArrayList datos) throws Exception {
		StringBuilder insert = new StringBuilder();
		ArrayList parametros = new ArrayList();
		int act = 0;
		if (datos != null) {
			for (Iterator iterator = datos.iterator(); iterator.hasNext();) {
				UserFlexWorkFlowsTO dato = (UserFlexWorkFlowsTO) iterator.next();

				long numRecord = IDDBFactorySql.getNextIDLong("user_flexworkflows");
				dato.setIdFlexWF(numRecord);

				insert.setLength(0);
				parametros.removeAll(parametros);

				insert.append("INSERT INTO user_flexworkflows ");
				insert.append("(idFlexWF,Orden,idUser,idWorkFlow,type,result,statu,isOwner,reading,active,pending,wfActive,IdFather,uw_Circle) ");
				insert.append("VALUES(?,?,?,?,CAST(? as bit),?,?,CAST(? as bit),CAST(? as bit),CAST(? as bit),CAST(? as bit),CAST(? as bit),?,CAST(? as bit))");

				parametros.add(new Long(dato.getIdFlexWF()));
				parametros.add(new Integer(dato.getOrden()));
				parametros.add(dato.getIdUser());
				parametros.add(new Integer(dato.getIdWorkFlow()));
				parametros.add(new Integer(dato.getType()));
				parametros.add(dato.getResult());
				parametros.add(dato.getStatu());
				parametros.add(new Integer(dato.getIsOwner()));
				parametros.add(new Integer(dato.getReading()));
				parametros.add(new Integer(dato.getActive()));
				parametros.add(new Integer(dato.getPending()));
				parametros.add(new Integer(dato.getWfActive()));
				parametros.add(new Long(dato.getIdFather()));
				parametros.add(new Integer(dato.getUw_Circle()));

				act += JDBCUtil.executeUpdate(insert, parametros);

			}
		}
		return act;
	}

	/*
	 * Metodo que actualiza el campo statu de la tabla user_flexworkflows a Pendiente o en Cola dependiendo si la actividad es secuencial o no, si es secuencial
	 * se verifica si existe un usuario anterior en la misma actividad con statu pendiente (en este caso se colocaria en cola)
	 */
	public static synchronized int updateStatuUsersFlexWorkFlows(ArrayList datos, long IDFlexFlow) throws Exception {
		StringBuilder update = new StringBuilder();
		StringBuilder consultaAct = new StringBuilder();
		StringBuilder consultaUsr = new StringBuilder();
		Connection con = JDBCUtil.getConnection(Thread.currentThread().getStackTrace()[1].getMethodName());

		int act = 0;
		if (datos != null) {
			for (Iterator iterator = datos.iterator(); iterator.hasNext();) {
				UserFlexWorkFlowsTO dato = (UserFlexWorkFlowsTO) iterator.next();

				update.setLength(0);
				consultaAct.setLength(0);
				consultaUsr.setLength(0);

				consultaAct.append("select * from flexworkflow where idWorkFlow =" + dato.getIdWorkFlow());
				// //System.out.println("select * from flexworkflow where idWorkFlow ="
				// + dato.getIdWorkFlow());

				PreparedStatement pstm = con.prepareStatement(JDBCUtil.replaceCastMysql(consultaAct.toString()));
				log.debug("[updateStatuUsersFlexWorkFlows] " + consultaAct.toString());
				ResultSet rs = pstm.executeQuery();

				Byte bSecuencial = 1;
				// int secuencial = 1;
				if (rs != null && rs.next()) {
					bSecuencial = rs.getByte("secuential");
				}

				// if(secuencial==1){
				// ES NO SENCUENCIAL
				if (bSecuencial != null && bSecuencial == 1) {
					// Se consulta si existen usuarios pendientes en una
					// actividad anterior del FTP
					/*
					 * consultaUsr.append( "select * from user_flexworkflows where statu = " +wfuPending+" and idWorkFlow < " + dato.getIdWorkFlow());
					 * consultaUsr.append( " and idWorkFlow in (select distinct idWorkFlow from flexworkflow where IDFlexFlow = " +IDFlexFlow+")");
					 * //System.out.println("query1: "+consultaUsr.toString()); PreparedStatement pstm2 = con.prepareStatement(consultaUsr.toString());
					 * ResultSet rs2 = pstm2.executeQuery();
					 *
					 * if(rs2!=null && rs2.next()){ //System.out.println( "se modifican los usuarios con status en cola: " + rs2.getString(1));
					 * update.append("update user_flexworkflows set statu = " +wfuQueued+", result = "+pending+" where idFlexWF = " + dato.getIdFlexWF());
					 * JDBCUtil.executeUpdate(update); //System.out.println("query11: "+update.toString()); }else{
					 */
					// //System.out.println("se modifican los usuarios con status pendiente");
					update.append("update user_flexworkflows set statu = " + wfuPending + ", result = " + pending + " where idFlexWF = " + dato.getIdFlexWF());
					JDBCUtil.executeUpdate(update);
					// //System.out.println("query12: " + update.toString());
					// }
				} else {
					// Se consulta si existen usuarios pendientes en esa
					// actividad
					consultaUsr.append("select * from user_flexworkflows where statu = '" + wfuPending + "' and idWorkFlow = " + dato.getIdWorkFlow()
							+ " and idFlexWF<" + dato.getIdFlexWF());
					// //System.out.println("query2: " +
					// consultaUsr.toString());
					PreparedStatement pstm3 = con.prepareStatement(JDBCUtil.replaceCastMysql(consultaUsr.toString()));
					ResultSet rs3 = pstm3.executeQuery();

					if (rs3 != null && rs3.next()) {
						// //System.out.println("se modifican los usuarios con status en cola: "
						// + rs3.getString(1));
						update.append("update user_flexworkflows set statu = '" + wfuQueued + "', result = " + pending + " where idFlexWF = "
								+ dato.getIdFlexWF());
						JDBCUtil.executeUpdate(update);
						// //System.out.println("query21: " +
						// update.toString());
					} else {
						// //System.out.println("se modifican los usuarios con status pendiente");
						update.append("update user_flexworkflows set statu = '" + wfuPending + "', result = " + pending + " where idFlexWF = "
								+ dato.getIdFlexWF());
						JDBCUtil.executeUpdate(update);
						// //System.out.println("query22: " +
						// update.toString());
					}
				}

			}
		}
		return act;
	}

	private static void setDataInWF(BaseWorkFlow forma, PreparedStatement st,
			Timestamp time, Timestamp dateExp, String user, int num)
			throws Exception {	
		//ydavila Ticket 001-00-003023
			st.setInt(1, num);
			st.setTimestamp(2, time);
			st.setString(3, user);
			st.setInt(4, forma.getNumDocument());
			st.setInt(5, forma.getSecuential());
			st.setInt(6, forma.getConditional());
			st.setInt(7, forma.getNotified());
			st.setInt(8, forma.getExpire());
			st.setTimestamp(9, dateExp);
			st.setString(10, pending);
			st.setInt(11, forma.getTypeWF());
			st.setInt(12, 0);
			switch (forma.getTypeWF()) {
			case 3:
				st.setInt(6, 0);
				st.setInt(11, 0);
				st.setString(12, wfSubtypeSolcambio);
				break;
			case 4:
				st.setInt(6, 0);
				st.setInt(11, 0);
				st.setString(12, wfSubtypeSolelimin);
				break;
			}
			if (ToolsHTML.isEmptyOrNull(forma.getComments())) {
				st.setString(13, "");
			} else {
				st.setString(13, forma.getComments());
			}
			st.setString(14, pending);
			st.setLong(15, forma.getNumVersion());
			st.setLong(16, Long.parseLong(forma.getLastVersion()));
			st.setInt(17, (byte) (forma.getEliminar()));
			st.setInt(18, (byte) (forma.getCambiar()));
			switch (forma.getTypeWF()) {
			case 3:		
				st.setInt(6, 0);
				st.setInt(17, 0);
				st.setInt(18, 1);
				break;
			case 4:
				st.setInt(6, 0);
				st.setInt(17, 1);
				st.setInt(18, 0);
				break;
			}
			st.setInt(19, (byte) (forma.getImprimir()));
			st.setInt(20, (byte) (forma.getCopy()));
		System.out.println("query insert workflow " + st);
		st.executeUpdate();
	}

	private static synchronized void setDataInWF(BaseWorkFlow forma, PreparedStatement st, Timestamp time, Timestamp dateExp, String user, int num, int orden,
			String statu) throws Exception {
		st.setInt(1, num);
		st.setTimestamp(2, time);
		st.setString(3, user);
		st.setInt(4, forma.getNumDocument());
		st.setInt(5, forma.getSecuential());
		st.setInt(6, forma.getConditional());
		st.setInt(7, forma.getNotified());
		st.setInt(8, forma.getExpire());
		st.setTimestamp(9, dateExp);
		st.setString(10, statu);
		st.setInt(11, forma.getTypeWF());
		if (ToolsHTML.isEmptyOrNull(forma.getComments())) {
			st.setInt(6, 0);
			st.setString(12, "");
		} else {
			st.setInt(6, 0);
			st.setString(12, forma.getComments());
		}
		st.setString(13, pending); // Resultado Pendiente
		st.setLong(14, forma.getNumVersion());
		st.setLong(15, Long.parseLong(forma.getLastVersion()));
		st.setByte(16, (byte) (forma.getEliminar()));
		st.setByte(17, (byte) (forma.getImprimir()));
		st.setInt(18, orden);
		st.executeUpdate();
	}

	private static synchronized void setDataInFlexFlow(BaseWorkFlow forma, PreparedStatement st, Timestamp time, Timestamp dateExp, String user, long num,
			int orden, String statu, String dataMail) throws Exception {

		byte perm = (byte) 1;
		st.setLong(1, num);
		st.setTimestamp(2, time);
		st.setString(3, user);
		st.setInt(4, forma.getNumDocument());
		st.setInt(5, forma.getSecuential());
		st.setInt(6, forma.getConditional());
		st.setInt(7, forma.getNotified());
		st.setInt(8, forma.getExpire());
		st.setTimestamp(9, dateExp);
		st.setString(10, statu);
		st.setLong(11, forma.getTypeWF());
		log.debug("dataMail: " + dataMail);
		if (ToolsHTML.isEmptyOrNull(forma.getComments())) {
			st.setString(12, "");
		} else {
			if (ToolsHTML.isEmptyOrNull(dataMail)) {
				st.setInt(6, 0);
				st.setString(12, forma.getComments());
			} else {
				st.setInt(6, 0);
				st.setString(12, forma.getComments() + dataMail);
			}
		}
		st.setString(13, pending); // Resultado Pendiente
		st.setLong(14, forma.getNumVersion());
		st.setLong(15, Long.parseLong(forma.getLastVersion()));
		st.setInt(16, orden);
		st.setInt(17, perm);
		st.setInt(18, perm);
		st.setLong(19, forma.getnAct());
		st.setLong(20, forma.getIDFlexFlow());
		st.setLong(21, forma.getCopy());
		st.executeUpdate();
	}

	private static String getQueryChangesWFs(int idMovement, int idWorkFlow, int typeMovement) throws Exception {
		StringBuilder sql = new StringBuilder("INSERT into changeswf (idMovement,idWorkFlow,typeMovement,dateMovement) VALUES(");
		sql.append(idMovement).append(",").append(idWorkFlow).append(",").append(typeMovement).append(",?)");
		return sql.toString();
	}

	private static String getQueryChangesWFs(int idMovement, long idWorkFlow, int typeMovement) throws Exception {
		StringBuilder sql = new StringBuilder("INSERT into changeswf (idMovement,idWorkFlow,typeMovement,dateMovement) VALUES(");
		sql.append(idMovement).append(",").append(idWorkFlow).append(",").append(typeMovement).append(",?)");
		return sql.toString();
	}

	/**
	 *
	 * @param con
	 * @param idWF
	 * @param nameUser
	 * @param time
	 * @param type
	 * @param idDocument
	 * @return
	 * @throws Exception
	 */
	public static boolean updateWFHistory(Connection con, long idWF, String nameUser, Timestamp time, String type, int idDocument) throws Exception {
		PreparedStatement st = null;
		boolean resp = false;
		StringBuilder sql = new StringBuilder("INSERT INTO wfhistory (idHistory,idWorkFlow,nameUser,dateChange,type) ");
		sql.append(" VALUES(?,?,?,?,?) ");
		long idHist = IDDBFactorySql.getNextIDLong("historywf");
		st = con.prepareStatement(JDBCUtil.replaceCastMysql(sql.toString()));
		st.setLong(1, idHist);
		st.setLong(2, idWF);
		st.setString(3, nameUser);
		st.setTimestamp(4, time);
		st.setString(5, type);
		st.executeUpdate();

		if (type.equals("9")) { // 9 es el codigo de aprobacion en el historico
								// de flujos
			// enviamos a los correos de la lista de distribucion
			HandlerDocuments.sendMailToListDist(con, idDocument);
		}

		return resp;
	}

	/**
	 *
	 * @param con
	 * @param idWF
	 * @param nameUser
	 * @param time
	 * @param type
	 * @param idDocument
	 * @param statuWorkFlow
	 * @param resultWorkFlow
	 * @return
	 * @throws Exception
	 */
	public static synchronized boolean updateWFHistoryFlexFlow(Connection con, long idWF, String nameUser, Timestamp time, String type, int idDocument,
			int statuWorkFlow, String resultWorkFlow) throws Exception {
		PreparedStatement st = null;
		boolean resp = false;
		StringBuilder sql = new StringBuilder("INSERT INTO flexflowhistory (idHistory,idWorkFlow,nameUser,dateChange,type) ");
		sql.append(" VALUES(?,?,?,?,?) ");
		long idHist = IDDBFactorySql.getNextIDLong(con, "flexflowhistory");
		st = con.prepareStatement(JDBCUtil.replaceCastMysql(sql.toString()));
		st.setLong(1, idHist);
		st.setLong(2, idWF);
		st.setString(3, nameUser);
		st.setTimestamp(4, time);
		st.setString(5, type);
		st.executeUpdate();

		// type es el id del FTP que esta guardandose
		log.info("Se recibieron valores statuWorkFlow='" + statuWorkFlow + "', resultWorkFlow='" + resultWorkFlow + "'");
		if (wfAcepted == statuWorkFlow && wfuAcepted.equals(resultWorkFlow)) {
			// enviamos a los correos de la lista de distribucion
			log.info("Iniciando envio de correo asociado a la lista de divulgaci�n por completaci�n de un flujo parametrico");
			HandlerDocuments.sendMailToListDist(con, idDocument);
		} else {
			log.info("Se esta actualizando un FTP con type='" + type + "', no se dispara la alerta de lista de divulgaci�n.");
		}

		return resp;
	}

	public static boolean getStatuDoc(int idDocument) throws ApplicationExceptionChecked {
		StringBuilder sql = new StringBuilder("SELECT statu FROM workflows");
		sql.append(" WHERE statu = '").append(pending).append("' AND result = '").append(pending).append("' ");
		sql.append(" AND idDocument = cast(").append(idDocument).append(" as int)");
		boolean resp = false;
		try {
			Properties prop = JDBCUtil.doQueryOneRow(sql.toString(),Thread.currentThread().getStackTrace()[1].getMethodName());
			if (prop.getProperty("isEmpty").equalsIgnoreCase("false")) {
				resp = true;
			}
		} catch (Exception ex) {
			Connection con = JDBCUtil.getConnection(Thread.currentThread().getStackTrace()[1].getMethodName());
			PreparedStatement st = null;
			StringBuilder sql1 = new StringBuilder("update workflows set statu=" + wfuCanceled + ",result =" + wfuCanceled);
			sql1.append(" where  idDocument = ").append(idDocument);
			try {
				st = con.prepareStatement(JDBCUtil.replaceCastMysql(sql1.toString()));
				st.execute();
			} catch (Exception e) {
			} finally {
				try {
					st.close();
					con.close();
				} catch (Exception e) {
				}
			}
			ex.printStackTrace();
			throw new ApplicationExceptionChecked("E0012");
		}
		return resp;
	}

	public static String getInsertWorkFlowsQuery() {
		StringBuffer insert = new StringBuffer();

		insert.append("INSERT INTO workflows (idWorkFlow,DateCreation,owner,idDocument,secuential");
		//ydavila Ticket 001-00-003023
		insert.append(",conditional,notified,expire,dateExpire,statu,type,subtype,");
		insert.append("comments,result,idVersion,idLastVersion,eliminar,cambiar,toImpresion,copy) ");
		insert.append("VALUES ( ?,?,?,?,CAST(? as bit),CAST(? as bit),CAST(? as bit),");
		insert.append("CAST(? as bit),?,?,CAST(? as bit),?,?,?,?,?,CAST(? as bit),CAST(? as bit),?,CAST(? as bit))");


		return insert.toString();
	}

	/*
	 * int numHistory = IDDBFactorySql.getNextID("historywf"); StringBuilder insertHWF = new StringBuilder(
	 * "INSERT INTO historywf (idWorkFlow,DateCreation,owner,idDocument,secuential" ); insertHWF.append(
	 * ",conditional,notified,expire,dateExpire,statu,type,comments,result,idVersion,idHistory) VALUES ( ?,?,?,?,?,?,?,?,?,?,?,?,?,?,? )" );
	 */
	public static synchronized boolean insert(BaseWorkFlow forma, Users usuario) throws Exception {
		MailForm formaMail = new MailForm();
		boolean resp = false;
		// JULIO 11 DEL 2005 INICIO
		StringBuilder insert = new StringBuilder(getInsertWorkFlowsQuery());
		// JULIO 11 DEL 2005 FIN

		if (getStatuDoc(forma.getNumDocument())) {
			throw new ApplicationExceptionChecked("E0013");
		}
		Connection con = null;
		PreparedStatement st = null;
		java.util.Date dateExp = ToolsHTML.getDateExpireDocument(forma.getDateExpireWF());
		String statuDoc = HandlerDocuments.getFieldToDocument("statu", forma.getNumDocument());
		try {
			java.util.Date date = new Date();
			int num = HandlerStruct.proximo("workflows", "workflows", "idWorkFlow");
			con = JDBCUtil.getConnection(Thread.currentThread().getStackTrace()[1].getMethodName());

			// hacemos la consulta de los usuarios antes de iniciar la
			// transaccion
			// ini
			ArrayList emailsUsers = new ArrayList();
			StringBuilder usuarios = new StringBuilder("");
			int posInit = 0;

			boolean withGroups = false;
			if (forma.getGroupsSelected() != null) { // no sera manejado por
														// grupo, si no por
														// usuario
				Object[] datos = forma.getGroupsSelected();
				ArrayList listaUsuarios = new ArrayList();

				for (int row = 0; row < datos.length; row++) {
					Object dato = datos[row];
					Vector data = (Vector) HandlerGrupo.getUsersGrupos(dato.toString());
					for (int i = 0; i < data.size(); i++) {
						Search3 us = (Search3) data.get(i);
						listaUsuarios.add(us.getId());
					}
				}
				if (forma.getUsersSelected() != null && forma.getUsersSelected().length > 0) {
					for (int i = 0; i < forma.getUsersSelected().length; i++) {
						listaUsuarios.add(forma.getUsersSelected()[i]);
					}
				}
				Object[] todos = new Object[listaUsuarios.size()];
				for (int i = 0; i < listaUsuarios.size(); i++) {
					todos[i] = listaUsuarios.get(i);
				}
				forma.setUsersSelected(todos);

				forma.setGroupsSelected(null);
			}

			// :SEGURIDAD PARA USUARIOS DE FLUJOS:
			if (forma.getUsersSelected() != null && forma.getUsersSelected().length > 0) {
				// verificamos que los usuarios del flujo tengan permiso para
				// participar en flujo
				// forma.setUsersSelected(getUserFlowValid(forma.getUsersSelected(),
				// String.valueOf(forma.getNumDocument()) ));
			}
			// fin

			con.setAutoCommit(false); // iniciamos la transaccion
			String stateWF = String.valueOf(BaseDocumentForm.statuInReview);
			String lastOperation = HandlerDocuments.lastInReview;
			if (forma.getTypeWF() == 1) {
				stateWF = String.valueOf(BaseDocumentForm.statuInApproved);
				lastOperation = HandlerDocuments.lastInApproved;
			}
			forma.setNumWF(num);

			// modificamos el statu del documento
			HandlerDocuments.updateStateDoc(con, forma.getNumDocument(), stateWF, statuDoc, lastOperation, 0, null);
			// cerramos todos los flujos de trabajos expirados , ya que este
			// documento ha sido sometido a un nuevo flujo
			HandlerWorkFlows.updateStateFlowExpire(con, forma.getNumDocument());
			// Se colocan como Vencido los FTPs anteriores, que hayan expirado,
			// relacionados al documento
			HandlerWorkFlows.updateStateFTPExpire(con, forma.getNumDocument());

			st = con.prepareStatement(JDBCUtil.replaceCastMysql(insert.toString()));
			Timestamp timeCreation = new Timestamp(date.getTime());
			Timestamp timeExpire = null;
			if (dateExp != null) {
				timeExpire = new Timestamp(dateExp.getTime());
			}
			// llenamos todas las variables del prepareStatement
			if (forma.getTypeWF()==0 ) forma.setCambiar((byte) 0);
			setDataInWF(forma, st, timeCreation, timeExpire, usuario.getUser(), num);
			/* Se establece la relacion de Nuevo Flujo de Trabajo */
			// int idMovement = IDDBFactorySql.getNextID("changeswf");
			// esto supuestamente ya nose
			// usa.....................inicio............................
			int idMovement = HandlerStruct.proximo("changeswf", "changeswf", "idMovement");
			forma.setIdMovement(idMovement);
			st = con.prepareStatement(JDBCUtil.replaceCastMysql(getQueryChangesWFs(idMovement, num, 1)));
			st.setTimestamp(1, timeCreation);
			st.executeUpdate();
			// esto supuestamente ya nose
			// usa.....................fin............................

			/* Se actualizan las participaciones en el Flujo de Trabajo */
			if (forma.getUsersSelected() != null
					&& forma.getUsersSelected().length > 0) {
				//ydavila 
				String userWF =null;
				if (forma.typeWF!=3 && forma.typeWF!=4){
					userWF = processList(forma.getUsersSelected(), st, con,
							num, 0, forma.getSecuential(), usuario.getUser(),
							withGroups, posInit);
				}else{
					userWF = processList1(forma.getUsersSelected(), st, con,
							num, 0, forma.getSecuential(), usuario.getUser(),
							withGroups, posInit, forma.typeWF);				
				}
				if (!ToolsHTML.isEmptyOrNull(userWF) && userWF.length() > 1) {
					// System.out.println("userWF = " + userWF);
					HandlerGrupo.getEmailForUsers(userWF, emailsUsers);
				}
			}

			con.commit(); // finalizamos la transaccion.
			resp = true;

			for (int row = 0; row < emailsUsers.size(); row++) {
				if (usuarios.length() > 1) {
					usuarios.append(";");
				}
				usuarios.append(emailsUsers.get(row)).append(";");
			}
			if (usuarios.length() > 0) {
				usuarios.deleteCharAt(usuarios.length() - 1);
			}
			formaMail.setUserName(usuario.getNameUser());
			formaMail.setFrom(usuario.getEmail());
			formaMail.setNameFrom(usuario.getNamePerson());
			formaMail.setTo(usuarios.toString());
			formaMail.setSubject(forma.getTitleForMail());
			formaMail.setMensaje(forma.getComments());

		} catch (UserWithoutPermissionToFlexFlowException ex) {
			ex.printStackTrace();
			applyRollback(con);
			setMensaje(ex.getMessage());
			resp = false;
			throw ex;
		} catch (Exception ex) {
			ex.printStackTrace();
			applyRollback(con);
			setMensaje(ex.getMessage());
			resp = false;
		} finally {
			setFinally(con, st);
		}
		try {
			// SendMailAction.send(formaMail);
			SendMailTread mail = new SendMailTread(formaMail);
			mail.start();
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return resp;
	}

	/**
	 * Se verifican los permisos de los usuarios, buscando si pueden participar en un flujo.
	 *
	 * @param usuarios
	 * @return lista de usuarios con permisologia
	 */
	public static Object[] getUserFlowValid(Object[] usuarios, String idDocument) throws Exception {

		Hashtable securityDocs = null;
		ArrayList listaUsuarios = new ArrayList();
		ArrayList<Object> listaUsuariosPermitidos = new ArrayList<Object>();
		Users usuario = new Users();
		TreeMap<Integer, Object> unicos = new TreeMap<Integer, Object>();

		// reunimos los datos de los usuarios como idPerson y grupo
		// y validamos que no existan usuarios repetidos;
		StringBuilder sql = new StringBuilder();
		String sep = "";
		sql.append("SELECT idperson,idgrupo,nameUser FROM person WHERE nameUser IN (");
		for (int i = 0; i < usuarios.length; i++) {
			if (!unicos.containsValue(usuarios[i])) {
				sql.append(sep).append("'").append(usuarios[i]).append("'");
				sep = ",";
				unicos.put(new Integer(i), usuarios[i]);
			}
		}
		sql.append(")");

		CachedRowSet crs = JDBCUtil.executeQuery(sql, Thread.currentThread().getStackTrace()[1].getMethodName());
		while (crs.next()) {
			usuario = new Users();
			usuario.setIdPerson(crs.getLong("idperson"));
			usuario.setIdGroup(crs.getString("idgrupo"));
			usuario.setNameUser(crs.getString("nameUser"));
			listaUsuarios.add(usuario);
		}

		listaUsuariosPermitidos.add(listaUsuarios); // colocado temporal
		/*
		 * :PENDIENTE: verificar la seguridad para corpovargas. for (int i =0; i < listaUsuarios.size(); i++ ) { Users element = (Users) listaUsuarios.get(i);
		 * securityDocs = null; securityDocs = ToolsHTML.checkDocsSecurity(securityDocs, element, idDocument); for (Enumeration enu = securityDocs.elements();
		 * enu.hasMoreElements(); ) { PermissionUserForm forma = (PermissionUserForm) enu.nextElement();
		 *
		 * if( forma.getToDoFlows()==(byte)1 ) { listaUsuariosPermitidos.add(element.getNameUser()); break; } } }
		 */

		if (listaUsuariosPermitidos.size() == 0)
			throw new UserWithoutPermissionToFlexFlowException("No hay usuarios con permiso para iniciar el flujos");

		return listaUsuariosPermitidos.toArray();

	}

	public static synchronized Collection insertFlexFlow(Collection actividadesFlex, Users usuario, int orden, String statu, String nameFile,
			String msgPending, String dataDocument) throws ApplicationExceptionChecked, Exception {

		BaseWorkFlow baseWorkFlow = (BaseWorkFlow) actividadesFlex.toArray()[0];

		// Variables Necesarias para crear el Flujo de Trabajo Par�metrico
		StringBuilder insert = new StringBuilder("");
		insert.append("INSERT INTO flexworkflow (idWorkFlow,DateCreation,owner,idDocument,secuential");
		insert.append(",conditional,notified,expire,dateExpire,statu,type,comments,result,idVersion,idLastVersion,");
		insert.append("orden,readingWF,activeWF,Act_Number,IDFlexFlow,copy) VALUES (?,?,?,?,CAST(? as bit),CAST(? as bit),CAST(? as bit),CAST(? as bit)");
		insert.append(",?,?,?,?,?,?,?,?,CAST(? as bit),CAST(? as bit),?,?,CAST(? as bit))");

		Connection con = null;
		PreparedStatement st = null;
		Vector result = new Vector();
		java.util.Date date = new Date();
		MailForm formaMail = new MailForm();
		// Se Procede a Crear C/U de las Actividades del Flujo de Trabajo
		// Param�trico
		String stateWF = HandlerDocuments.inFlewFlow;
		String lastOperation = HandlerDocuments.lastInReview;
		// modificamos el statu del documento

		String statuDoc = HandlerDocuments.getFieldToDocument("statu", baseWorkFlow.getNumDocument());
		Timestamp timeExpire = null;
		Timestamp timeCreation = new Timestamp(date.getTime());
		boolean resp = false;
		try {
			con = JDBCUtil.getConnection(Thread.currentThread().getStackTrace()[1].getMethodName());
			con.setAutoCommit(false);
			if (orden == 0) {
				log.debug("[UPDATE] STATE DOC");
				HandlerDocuments.updateStateDoc(con, baseWorkFlow.getNumDocument(), stateWF, statuDoc, lastOperation, 0, null);
			}
			int indice = 0;
			long IDFlexFlow = IDDBFactorySql.getNextIDLong(con, "IDFlexFlow");
			for (Iterator iterator = actividadesFlex.iterator(); iterator.hasNext();) {
				timeExpire = null;
				baseWorkFlow = (BaseWorkFlow) iterator.next();
				java.util.Date dateExp = ToolsHTML.getDateExpireDocument(baseWorkFlow.getDateExpireWF());
				if (dateExp != null) {
					timeExpire = new Timestamp(dateExp.getTime());
				}
				long num = IDDBFactorySql.getNextIDLong(con, "FlexFlow");
				baseWorkFlow.setNewID(num);
				baseWorkFlow.setIDFlexFlow(IDFlexFlow);
				st = con.prepareStatement(JDBCUtil.replaceCastMysql(insert.toString()));
				// llenamos todas las variables del prepareStatement
				setDataInFlexFlow(baseWorkFlow, st, timeCreation, timeExpire, usuario.getUser(), num, orden, statu, dataDocument);
				/* Se actualizan las participaciones en el Flujo de Trabajo */

				ArrayList emailsUsers = new ArrayList();
				StringBuilder usuarios = new StringBuilder("");
				if (baseWorkFlow.getGroupsSelected() != null) {
					Object[] datos = baseWorkFlow.getGroupsSelected();
					int pos = 0;
					boolean withUsers = false;
					// Si Hay Usuarios Seleccionados o hay m�s de un Grupo se
					// Inicializa la Variable
					// para que s�lo se cree una entrada para el creador del
					// Flujo de Trabajo
					if ((baseWorkFlow.getUsersSelected() != null && baseWorkFlow.getUsersSelected().length > 0) || datos.length > 1) {
						withUsers = true;
					}
					Object[] listUser;
					int cont = 0;
					int cantUsuario = 0;
					if (baseWorkFlow.getUsersSelected() != null) {
						cantUsuario = baseWorkFlow.getUsersSelected().length;
					}
					for (int row = 0; row < datos.length; row++) {
						Object dato = datos[row];
						Vector data = (Vector) HandlerGrupo.getUsersGrupos(dato.toString());

						cont = 0;

						listUser = new Object[data.size() + cantUsuario];

						for (Iterator iterator1 = data.iterator(); iterator1.hasNext();) {
							Search3 obj = (Search3) iterator1.next();
							listUser[cont++] = (Object) obj.getId();
						}
						for (int k = 0; k < cantUsuario; k++) {
							listUser[cont++] = (Object) baseWorkFlow.getUsersSelected()[k];
						}

						String userWF = processList(listUser, st, con, num, 0, baseWorkFlow.getSecuential(), Constants.notPermission, 0, 0);

						if (!ToolsHTML.isEmptyOrNull(userWF)) {
							HandlerGrupo.getEmailForUsers(userWF, emailsUsers);
						}
						if (userWF.length() == 0) {
							throw new ApplicationExceptionUsersEmpty("E0103");
						}

						// if (row == 0) {
						// pos = processList(data, st, con, num, 1, 1,
						// usuario.getUser(), row, usuarios, pos, withUsers);
						// } else {
						// pos = processList(data, st, con, num, 1,
						// baseWorkFlow.getSecuential(), usuario.getUser(), row,
						// usuarios, pos, withUsers);
						// }
						// if(pos==0) {
						// throw new ApplicationExceptionUsersEmpty("E0103");
						// }

					}
					// if(usuarios!=null &&
					// !usuarios.toString().trim().equals("")) {
					// usuarios.replace(usuarios.length() - 1,
					// usuarios.length(), "");
					// }
				} else {
					// pasar como parametro la fecha de vencimiento para la
					// actividad
					String userWF = processList(baseWorkFlow.getUsersSelected(), st, con, num, 0, baseWorkFlow.getSecuential(), Constants.notPermission, 0, 0,
							baseWorkFlow.getUsersSelectedExpire(), baseWorkFlow);

					if (!ToolsHTML.isEmptyOrNull(userWF)) {
						HandlerGrupo.getEmailForUsers(con, userWF, emailsUsers);
					}
				}
				log.debug("I");
				if (indice == 0) {
					for (int row = 0; row < emailsUsers.size(); row++) {
						usuarios.append(emailsUsers.get(row)).append(";");
					}
					if (usuarios.length() > 0) {
						usuarios.deleteCharAt(usuarios.length() - 1);
					}
					formaMail.setFrom(usuario.getEmail());
					formaMail.setNameFrom(usuario.getNamePerson());
					formaMail.setTo(usuarios.toString());
					formaMail.setSubject(baseWorkFlow.getTitleForMail());
					formaMail.setMensaje(baseWorkFlow.getComments() + dataDocument);
					statu = HandlerWorkFlows.inQueued;
				}
				log.debug("II " + indice);
				indice++;
				resp = true;
			}

			// Se colocan como Vencido los flujos de aprobacion y/o revision
			// anteriores, que hayan expirado, relacionados al documento
			HandlerWorkFlows.updateStateFlowExpire(con, baseWorkFlow.getNumDocument());
			// Se modifican status de FTPs Expirados a Vencido, ya que el
			// documento se somete a un nuevo flujo
			HandlerWorkFlows.updateStateFTPExpire(con, baseWorkFlow.getNumDocument());

		} catch (ApplicationExceptionUsersEmpty ex) {
			ex.printStackTrace();
			applyRollback(con);
			setMensaje(ex.getMessage());
			resp = false;
			throw new ApplicationExceptionChecked("E0103");
		} catch (Exception ex) {
			ex.printStackTrace();
			applyRollback(con);
			setMensaje(ex.getMessage());
			resp = false;
			throw new ApplicationExceptionChecked("E0106");
		} finally {
			if (resp) {
				try {
					con.commit();
					// SendMailAction.send(formaMail);
					SendMailTread mail = new SendMailTread(formaMail);
					mail.start();
				} catch (Exception ex) {
					ex.printStackTrace();
				} finally {
					setFinally(con, st);
				}
			}
		}
		// Si el Flujo fu� creado exitosamente se procede a cargar la data del
		// mismo
		// para Mostrarselo al Usuario
		if (resp) {
			int indice = 0;
			for (Iterator iterator = actividadesFlex.iterator(); iterator.hasNext();) {
				baseWorkFlow = (BaseWorkFlow) iterator.next();
				DataWorkFlowForm dataWF = new DataWorkFlowForm();
				dataWF.setNumDocument(baseWorkFlow.getNumDocument());
				dataWF.setIdWorkFlow(String.valueOf(baseWorkFlow.getNewID()));
				HandlerWorkFlows.loadDataWorkFlow("flexworkflow", false, dataWF);
				if (indice != 0) {
					dataWF.setDateBegin("N/A");
				}
				dataWF.setNameFile(nameFile);
				dataWF.setNameWF(baseWorkFlow.getNameAct());
				dataWF.setComments(baseWorkFlow.getComments());
				dataWF.setDateExpire(!ToolsHTML.isEmptyOrNull(baseWorkFlow.getDateExpireWF()) ? ToolsHTML.sdfShowWithoutHour.format(ToolsHTML.date
						.parse(baseWorkFlow.getDateExpireWF())) : "N/A");
				dataWF.setDateEnd(msgPending);
				dataWF.setUsers(HandlerWorkFlows.getAllUserInFlexFlow((int) baseWorkFlow.getNewID()));
				result.add(dataWF);
				indice++;
			}
		}
		return result;
	}

	public static synchronized boolean insert(BaseWorkFlow forma, Users solicitante, Users usuario, Connection con, String statuDoc, java.util.Date dateExp,
			int num) throws Exception {
		MailForm formaMail = new MailForm();
		boolean resp = false;
		StringBuilder insert = new StringBuilder(getInsertWorkFlowsQuery());

		if (getStatuDoc(forma.getNumDocument())) {
			throw new ApplicationExceptionChecked("E0013");
		}
		PreparedStatement st = null;
		try {
			java.util.Date date = new Date();

			String stateWF = String.valueOf(BaseDocumentForm.statuInReview);
			String lastOperation = HandlerDocuments.lastInReview;
			if (forma.getTypeWF() == 1) {
				stateWF = String.valueOf(BaseDocumentForm.statuInApproved);
				lastOperation = HandlerDocuments.lastInApproved;
			}
			forma.setNumWF(num);
			HandlerDocuments.updateStateDoc(con, forma.getNumDocument(), stateWF, statuDoc, lastOperation, 0, null);

			// cerramos todos los flujos de trabajos expirados , ya que este
			// documento a sido sometido a un nuevo flujo
			HandlerWorkFlows.updateStateFlowExpire(con, forma.getNumDocument());

			st = con.prepareStatement(JDBCUtil.replaceCastMysql(insert.toString()));
			Timestamp timeCreation = new Timestamp(date.getTime());
			Timestamp timeExpire = null;
			if (dateExp != null) {
				timeExpire = new Timestamp(dateExp.getTime());
			}
			setDataInWF(forma, st, timeCreation, timeExpire, solicitante.getUser(), num);
			/* Se establece la relacion de Nuevo Flujo de Trabajo */
			// int idMovement = IDDBFactorySql.getNextID("changeswf");
			int idMovement = HandlerStruct.proximo("changeswf", "changeswf", "idMovement");
			forma.setIdMovement(idMovement);
			st = con.prepareStatement(JDBCUtil.replaceCastMysql(getQueryChangesWFs(idMovement, num, 1)));
			st.setTimestamp(1, timeCreation);
			st.executeUpdate();

			/* Se actualizan las participaciones en el Flujo de Trabajo */
			ArrayList emailsUsers = new ArrayList();
			StringBuilder usuarios = new StringBuilder("");
			if (forma.getGroupsSelected() != null) {
				Object[] datos = forma.getGroupsSelected();
				int pos = 0;
				for (int row = 0; row < datos.length; row++) {
					Object dato = datos[row];
					log.debug("dato.toString() = " + dato.toString());
					Vector data = (Vector) HandlerGrupo.getUsersGrupos(dato.toString());
					if (row == 0) {
						pos = processList(data, st, con, num, 1, 1, usuario.getUser(), row, usuarios, pos, false);
					} else {
						pos = processList(data, st, con, num, 1, forma.getSecuential(), usuario.getUser(), row, usuarios, pos, false);
					}
				}
				usuarios.replace(usuarios.length() - 1, usuarios.length(), "");
			} else {
				// Todo Verificar si en las Solicitudes de Impresi�n se manejan
				// Grupos de Usuarios
				// En Caso Afirmativo Modificar el �ltimo par�metro para que se
				// comporte seg�n Valor :D
				String userWF = processList(forma.getUsersSelected(), st, con, num, 0, forma.getSecuential(), usuario.getUser(), false, 0);
				if (!ToolsHTML.isEmptyOrNull(userWF)) {
					HandlerGrupo.getEmailForUsers(userWF, emailsUsers);
				}
			}
			resp = true;

			for (int row = 0; row < emailsUsers.size(); row++) {
				usuarios.append(emailsUsers.get(row)).append(";");
			}
			if (usuarios.length() > 0) {
				usuarios.deleteCharAt(usuarios.length() - 1);
			}

			Locale defaultLocale = new Locale(DesigeConf.getProperty("language.Default"), DesigeConf.getProperty("country.Default"));
			ResourceBundle rb = ResourceBundle.getBundle("LoginBundle", defaultLocale);

			formaMail.setUserName(usuario.getNameUser());
			formaMail.setFrom(usuario.getEmail());
			if (ToolsHTML.isEmptyOrNull(usuario.getNamePerson())) {
				formaMail.setNameFrom(rb.getString("mail.nameUser"));
			} else {
				formaMail.setNameFrom(usuario.getNamePerson());
			}
			formaMail.setTo(usuarios.toString());
			formaMail.setSubject(forma.getTitleForMail());
			formaMail.setMensaje(forma.getComments());

		} catch (Exception ex) {
			ex.printStackTrace();
			applyRollback(con);
			setMensaje(ex.getMessage());
			resp = false;
		}/*
		 * finally { setFinally(con,st); }
		 */
		try {
			log.debug("Sending..... mails.....");
			// SendMailAction.send(formaMail);
			SendMailTread mail = new SendMailTread(formaMail);
			mail.start();
		} catch (Exception ex) {
			log.debug("Exception");
			ex.printStackTrace();
		}

		return resp;
	}

	// actualizamos cerrando todos los flujos de trabajo que fueron expirados ya
	// que el documento es sometido a un nuevo
	// flujo
	public static void updateStateFlowExpire(Connection con, int numDoc) throws SQLException {
		StringBuilder sql = new StringBuilder("UPDATE user_workflows SET result = ?,statu = ? ")
				.append(" WHERE idworkflow in (SELECT MAX(idworkflow) FROM workflows WHERE iddocument=cast(").append(numDoc).append(" as int) ").append(" )")
				.append("AND statu='").append(HandlerWorkFlows.wfuExpired).append("' ");
		PreparedStatement st = con.prepareStatement(JDBCUtil.replaceCastMysql(sql.toString()));
		st.setString(1, HandlerWorkFlows.wfuVencido);
		st.setString(2, HandlerWorkFlows.wfuVencido);
		st.executeUpdate();

		sql.setLength(0);
		sql.append("UPDATE workflows SET statu = ").append(HandlerWorkFlows.vencido).append(",result=").append(HandlerWorkFlows.vencido)
				.append(" WHERE iddocument=cast(").append(numDoc).append(" as int) AND statu='").append(HandlerWorkFlows.expires).append("' ");
		st = con.prepareStatement(JDBCUtil.replaceCastMysql(sql.toString()));
		st.executeUpdate();
	}

	// actualizamos cerrando todos los FTP que fueron expirados ya que el
	// documento es sometido a un nuevo
	// flujo
	public static void updateStateFTPExpire(Connection con, int numDoc) throws SQLException {
		StringBuilder sql = new StringBuilder("UPDATE user_flexworkflows SET result = ?,statu = ? ");
		sql.append(" WHERE idworkflow in (SELECT idworkflow FROM flexworkflow WHERE iddocument=cast(").append(numDoc).append(" as int) ");
		sql.append(" )").append(" AND statu='").append(HandlerWorkFlows.wfuExpired).append("' ");
		sql.append(" AND result='").append(HandlerWorkFlows.expires).append("' ");
		PreparedStatement st = con.prepareStatement(JDBCUtil.replaceCastMysql(sql.toString()));
		st.setString(1, HandlerWorkFlows.wfuVencido);
		st.setString(2, HandlerWorkFlows.wfuVencido);
		st.executeUpdate();

		StringBuilder sql2 = new StringBuilder("UPDATE flexworkflow SET statu = '").append(HandlerWorkFlows.vencido).append("' ");
		sql2.append(",result='").append(HandlerWorkFlows.vencido).append("' WHERE iddocument=cast(").append(numDoc).append(" as int) AND statu='")
				.append(HandlerWorkFlows.expires).append("' ");
		sql2.append(" AND result='").append(HandlerWorkFlows.wfuExpired).append("' ");
		st = con.prepareStatement(JDBCUtil.replaceCastMysql(sql2.toString()));
		st.executeUpdate();
	}

	public static Collection getAllTypesStatus(ResourceBundle rb) throws Exception {
		ArrayList resp = new ArrayList();
		try {
			boolean swCancelar = false;
			for (int i = 1; i <= Integer.parseInt(rb.getString("wf.statuDoc")); i++) {
				// if
				// ((i!=Integer.parseInt(HandlerDocuments.docInReview))&&(i!=Integer.parseInt(HandlerDocuments.docInApproved))){

				if (!ToolsHTML.showFTP() && i == 9) {
					continue;
				}

				String str = rb.getString("wf.statuDoc" + i);
				Search bean = new Search(String.valueOf(i), str);
				// No se toman en cuenta status Rechazado ni Eliminados
				if (i != 7 && i != 8) {
					resp.add(bean);
				}

				// si es rechazado es igual a que fuera cancelado en el flujo en
				// el codigo,
				// solo los cancelados por cerrar el flujo por el duenio, seran
				// los que teoricamente si fue cancelados
				// y tendran
				// un codigo distinto a cancelado y rechazado que es el mismo
				// codigo al editar flujo..
				// el codigo en login bundle en rechazado es el codigo 11 y
				// cancelado el 4 y cerrados por el duenio es 12
				if (!swCancelar) {
					if ((i == 11) || (i == 4)) {
						str = rb.getString("wf.statuUserWF" + i);
						// System.out.println("str=" + str + " i=" + i);
						bean = new Search(HandlerWorkFlows.wfuCanceled2, str);
						resp.add(bean);
						swCancelar = true;

					}
				}
				// este es el que es cerrado por el duenio es 12
				if ((i + 1) == Integer.parseInt(rb.getString("wf.statuDoc"))) {
					str = rb.getString("wf.statuUserWF" + 12);
					bean = new Search(HandlerWorkFlows.ownercancelcloseflow, str);
					resp.add(bean);
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return resp;
	}

	// public static Collection getAllTypesDocuments() throws Exception {
	// String sql = "SELECT * FROM typedocuments";
	// Vector datos = JDBCUtil.doQueryVector(sql);
	// ArrayList resp = new ArrayList();
	// for (int row = 0; row < datos.size(); row++) {
	// Properties properties = (Properties)datos.elementAt(row);
	// Search bean = new
	// Search(properties.getProperty("idTypeDoc"),properties.getProperty("TypeDoc"));
	// resp.add(bean);
	// }
	// return resp;
	// }

	public static Collection getUserInWorkFlow(String idDocument, int typeWF) {
		// ydavila Ticket 001-00-003023
		int typeWFaux = typeWF;
		if (typeWF > 1) {
			typeWF = 0;
		}
		StringBuilder sql = new StringBuilder(100);
		sql.append("SELECT p.nameUser,");
		switch (Constants.MANEJADOR_ACTUAL) {
		case Constants.MANEJADOR_MSSQL:
			sql.append("(p.apellidos + ' ' + p.nombres) AS nombre,");
			break;
		case Constants.MANEJADOR_POSTGRES:
			sql.append("(p.apellidos || ' ' || p.nombres) AS nombre,");
			break;
		case Constants.MANEJADOR_MYSQL:
			sql.append("CONCAT(p.apellidos , ' ' , p.nombres) AS nombre,");
			break;
		}
		sql.append("c.cargo");
		sql.append(" FROM user_workflows uw,workflows w,person p,tbl_cargo c,tbl_area a ");
		sql.append(" WHERE w.idDocument = ").append(idDocument);
		sql.append(" AND c.idcargo=cast(p.cargo as int) and c.idarea=a.idarea");
		sql.append(" AND w.type = '").append(typeWF).append("'");
		sql.append(" AND w.idWorkFlow = uw.idWorkFlow");
		sql.append(" AND uw.idUser = p.nameUser AND p.accountActive = '1'");
		sql.append(" AND uw.type = '0' AND uw.isOwner  = '0' AND cast(w.result as int) <> ").append(wfuExpired);
		sql.append(" AND uw.wfActive = '1'");
		sql.append(" AND uw.idWorkFlow = (SELECT MAX(wi.idWorkFlow) FROM workflows wi WHERE wi.idDocument = ");
		sql.append(idDocument).append(" AND wi.type = '").append(typeWF).append("')");
		Vector result = new Vector();
		try {
			Vector datos = JDBCUtil.doQueryVector(sql.toString(),Thread.currentThread().getStackTrace()[1].getMethodName());
			for (int row = 0; row < datos.size(); row++) {
				Properties properties = (Properties) datos.elementAt(row);
				Search bean = new Search(properties.getProperty("nameUser"), properties.getProperty("nombre"));
				bean.setAditionalInfo(properties.getProperty("cargo"));
				result.add(bean);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return result;
	}

	public static Collection getUserInWorkFlow(String idDocument, String typeWF) {
		StringBuilder sql = new StringBuilder(100);
		sql.append("SELECT p.nameUser,");
		switch (Constants.MANEJADOR_ACTUAL) {
		case Constants.MANEJADOR_MSSQL:
			sql.append("(p.apellidos + ' ' + p.nombres) AS nombre,");
			break;
		case Constants.MANEJADOR_POSTGRES:
			sql.append("(p.apellidos || ' ' || p.nombres) AS nombre,");
			break;
		case Constants.MANEJADOR_MYSQL:
			sql.append("CONCAT(p.apellidos , ' ' , p.nombres) AS nombre,");
			break;
		}
		sql.append("p.cargo");
		sql.append(" FROM user_workflows uw,workflows w,person p");
		sql.append(" WHERE w.idDocument = ").append(idDocument);
		sql.append(" AND w.type = '").append(typeWF).append("' ");
		sql.append(" AND w.idWorkFlow = uw.idWorkFlow");
		sql.append(" AND uw.idUser = p.nameUser AND p.accountActive = '1'");
		sql.append(" AND uw.type = '0' AND uw.isOwner  = '0' AND w.result <> ").append(wfuExpired);
		sql.append(" AND uw.wfActive = '1'");
		sql.append(" AND uw.idWorkFlow = (SELECT MAX(wi.idWorkFlow) FROM workflows wi WHERE wi.idDocument = ");
		sql.append(idDocument).append(" AND wi.type = '").append(typeWF).append("')");
		Vector result = new Vector();
		try {
			Vector datos = JDBCUtil.doQueryVector(sql.toString(),Thread.currentThread().getStackTrace()[1].getMethodName());
			for (int row = 0; row < datos.size(); row++) {
				Properties properties = (Properties) datos.elementAt(row);
				Search bean = new Search(properties.getProperty("nameUser"), properties.getProperty("nombre"));
				bean.setAditionalInfo(properties.getProperty("cargo"));
				result.add(bean);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return result;
	}

	public static String getAllUsersInWF(int idWorkFlow) throws ApplicationExceptionChecked {
		StringBuilder sql = new StringBuilder("SELECT idUser FROM user_workflows");
		sql.append(" WHERE idWorkFlow = ").append(idWorkFlow);
		sql.append(" AND ((result IN ('").append(wfuPending).append("','");
		sql.append(wfuAcepted).append("','").append(wfuCanceled).append("')");
		sql.append(" AND statu IN ('").append(pending).append("','");
		sql.append(wfuAcepted).append("','").append(wfuCanceled).append("'))");
		sql.append(" OR (result = '").append(wfuPending).append("' AND statu = '");
		sql.append(wfuQueued).append("' AND isOwner = '1')) ORDER by orden");
		try {
			Vector datos = JDBCUtil.doQueryVector(sql.toString(),Thread.currentThread().getStackTrace()[1].getMethodName());
			boolean first = true;
			StringBuilder result = new StringBuilder(60);
			for (int row = 0; row < datos.size(); row++) {
				Properties properties = (Properties) datos.elementAt(row);
				if (first) {
					result.append("('");
					first = false;
				} else {
					result.append(",'");
				}
				result.append(properties.getProperty("idUser")).append("'");
			}
			if (result.length() > 0) {
				result.append(")");
				return result.toString();
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			throw new ApplicationExceptionChecked("E0045");
		}
		return null;
	}

	public static String getAllUsersInWF(int idWorkFlow, String sStatus, String sResult) throws ApplicationExceptionChecked {
		StringBuilder sql = new StringBuilder("SELECT idUser FROM user_workflows");
		sql.append(" WHERE idWorkFlow = ").append(idWorkFlow);
		sql.append(" AND result = '").append(sResult).append("'");
		sql.append(" AND statu = '").append(sStatus).append("'");
		sql.append(" ORDER by orden");
		try {
			Vector datos = JDBCUtil.doQueryVector(sql.toString(),Thread.currentThread().getStackTrace()[1].getMethodName());
			boolean first = true;
			StringBuilder result = new StringBuilder(60);
			for (int row = 0; row < datos.size(); row++) {
				Properties properties = (Properties) datos.elementAt(row);
				if (first) {
					result.append("('");
					first = false;
				} else {
					result.append(",'");
				}
				result.append(properties.getProperty("idUser")).append("'");
			}
			if (result.length() > 0) {
				result.append(")");
				return result.toString();
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			throw new ApplicationExceptionChecked("E0045");
		}
		return null;
	}

	public static String getAllUsersInWFP(int[] idWorkFlow) throws ApplicationExceptionChecked {
		String sep = "";
		String coma = ",";
		StringBuilder sql = new StringBuilder("SELECT idUser FROM user_flexworkflows");
		sql.append(" WHERE idWorkFlow IN (");
		for (int i = 0; i < idWorkFlow.length; i++) {
			sql.append(sep);
			sql.append(idWorkFlow[i]);
			sep = coma;
		}
		sql.append(") ");

		sql.append(" AND ((result IN (").append(wfuPending).append(",");
		sql.append(wfuAcepted).append(",").append(wfuCanceled).append(")");
		sql.append(" AND statu IN (").append(pending).append(",");
		sql.append(wfuAcepted).append(",").append(wfuCanceled).append("))");
		sql.append(" OR (result = ").append(wfuPending).append(" AND statu = ");
		sql.append(wfuQueued).append(" AND isOwner = '1')) ORDER by orden");
		try {
			Vector datos = JDBCUtil.doQueryVector(sql.toString(),Thread.currentThread().getStackTrace()[1].getMethodName());
			boolean first = true;
			StringBuilder result = new StringBuilder(60);
			for (int row = 0; row < datos.size(); row++) {
				Properties properties = (Properties) datos.elementAt(row);
				if (first) {
					result.append("('");
					first = false;
				} else {
					result.append(",'");
				}
				result.append(properties.getProperty("idUser")).append("'");
			}
			if (result.length() > 0) {
				result.append(")");
				return result.toString();
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			throw new ApplicationExceptionChecked("E0045");
		}
		return null;
	}

	public static String getAllUsersInWFP(int idWorkFlow, String sStatus, String sResult, boolean wfActive) throws ApplicationExceptionChecked {
		StringBuilder sql = new StringBuilder("SELECT idUser FROM user_flexworkflows");
		sql.append(" WHERE idWorkFlow =").append(idWorkFlow);
		sql.append(" AND result = '").append(sResult).append("'");
		sql.append(" AND statu = '").append(sStatus).append("'");
		if (wfActive) {
			sql.append(" AND wfActive = '").append(Constants.permission).append("' ");
		}
		sql.append(" ORDER by orden");
		try {
			Vector datos = JDBCUtil.doQueryVector(sql.toString(),Thread.currentThread().getStackTrace()[1].getMethodName());
			boolean first = true;
			StringBuilder result = new StringBuilder(60);
			for (int row = 0; row < datos.size(); row++) {
				Properties properties = (Properties) datos.elementAt(row);
				if (first) {
					result.append("('");
					first = false;
				} else {
					result.append(",'");
				}
				result.append(properties.getProperty("idUser")).append("'");
			}
			if (result.length() > 0) {
				result.append(")");
				return result.toString();
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			throw new ApplicationExceptionChecked("E0045");
		}
		return null;
	}

	// public static String getIdWorkFlowsForUserAndStatu(String idUser,String
	// statu,boolean expires){
	// StringBuilder resp = new StringBuilder("(");
	// StringBuilder sql = new
	// StringBuilder("SELECT w.idWorkFlow FROM statususermov su,user_workflows uw,changeswf cwf,workflows w");
	// sql.append(" WHERE su.idUser = '").append(idUser).append("'");
	// sql.append(" AND uw.idUser = su.idUser AND cwf.idMovement = uw.idWorkFlow");
	// if (!expires){
	// sql.append("AND (w.dateExpire is NULL OR w.dateExpire >= ? )");
	// }
	// if (!ToolsHTML.isEmptyOrNull(statu)){
	// sql.append(" AND su.statu = ").append(statu.trim());
	// }
	// Connection con = null;
	// PreparedStatement st = null;
	// ResultSet rs = null;
	// try{
	// con = JDBCUtil.getConnection(Thread.currentThread().getStackTrace()[1].getMethodName());
	// st = con.prepareStatement(JDBCUtil.replaceCastMysql(sql.toString()));
	// st.setTimestamp(1,new Timestamp(new Date().getTime()));
	// rs = st.executeQuery();
	// while(rs.next()) {
	// resp.append(rs.getInt("idWorkFlow")).append(",");
	// }
	// } catch (Exception e){
	// e.printStackTrace();
	// return null;
	// }
	// if (resp.length()>1) {
	// resp.replace(resp.length()-1,resp.length(),")");
	// return resp.toString();
	// } else {
	// return null;
	// }
	// }

	/**
	 *
	 * @param wfExpires
	 * @param statu
	 * @param idUser
	 * @param statuUser
	 * @param owner
	 * @param rb
	 * @param struct
	 * @param prefijos
	 * @param searchAsAdmin
	 * @return
	 * @throws Exception
	 */
	public static synchronized Collection<DataUserWorkFlowForm> getAllWorkFlowsUserAndStatu(boolean wfExpires, String statu, String idUser, String statuUser,
			boolean owner, ResourceBundle rb, Hashtable struct, Hashtable prefijos, boolean searchAsAdmin) throws Exception {
		Vector<DataUserWorkFlowForm> resp = new Vector<DataUserWorkFlowForm>();
		StringBuilder sql = new StringBuilder(100);
		// sql.append("SELECT * ");
		// ydavila Ticket 001-00-003023
		// sql.append("SELECT w.idWorkFlow, w.type, w.dateExpire, uw.idUser, uw.row, d.nameDocument, vd.MayorVer, vd.MinorVer,");
		sql.append("SELECT w.idWorkFlow, w.type, w.subtype, w.dateExpire, uw.idUser, uw.row, d.nameDocument, vd.MayorVer, vd.MinorVer,");
		sql.append(" d.type AS typedoc, d.number, d.prefix,d. idNode, w.dateCompleted, w.owner, w.dateCreation,");
		sql.append(" flowWorker.idperson AS fwIdPerson, flowWorker.nombres AS fwNombres, flowWorker.apellidos AS fwApellidos, flowWorker.email AS fwEmail,");
		sql.append(" askingPerson.idperson AS akIdPerson, askingPerson.nombres AS akNombres, askingPerson.apellidos AS akApellidos, askingPerson.email AS akEmail");
		sql.append(" FROM person flowWorker, person askingPerson, workflows w,user_workflows uw,documents d,versiondoc vd");
		if (HandlerWorkFlows.wfuExpired.equals(statuUser)) {
			// tabla interna para saber si en los flujos expirados, los mismos
			// se le vencieron al originador o a los participantes
			sql.append(" , (SELECT uw1.idworkflow, CASE WHEN (COUNT(*) - 1) = 0 THEN '1' ELSE NULL END AS include FROM user_workflows uw1 WHERE uw1.statu = '")
					.append(statuUser.trim()).append("' GROUP BY uw1.idworkflow) includeOwner");
		}
		sql.append(" WHERE w.idWorkFlow = uw.idWorkFlow");
		if (HandlerWorkFlows.wfuExpired.equals(statuUser)) {
			sql.append(" AND includeOwner.idworkflow = uw.idWorkFlow");
		}
		sql.append(" AND w.idVersion = vd.numVer AND d.active = '").append(Constants.permission).append("'");
		if (!searchAsAdmin) {
			// los usuarios que no sean del tipo administrador, solo podran ver
			// archivos y flujos
			// de los cuales ellos sean los propietarios
			sql.append(" AND uw.idUser = '").append(idUser).append("'");
		}
		sql.append(" AND d.numGen = w.idDocument");
		sql.append(" AND flowWorker.nameuser=uw.idUser");
		sql.append(" AND askingPerson.nameuser=w.owner");
		// 12 DE JULIO 2005 INICIO
		// validamosque el flujo no este cancelado
		// sql.append(" AND uw.active <>").append(Constants.notPermissionSt);
		// 12 DE JULIO 2005 FIN
		if (!ToolsHTML.isEmptyOrNull(statuUser)) {
			sql.append(" AND uw.Statu  = '").append(statuUser.trim()).append("'");
		}
		if (!ToolsHTML.isEmptyOrNull(statu)) {
			sql.append(" AND w.Statu  = '").append(statu.trim()).append("'");
		}
		String dateSystem = ToolsHTML.sdfShowConvert1.format(new Date());
		if (!wfExpires) {
			switch (Constants.MANEJADOR_ACTUAL) {
			case Constants.MANEJADOR_MSSQL:
				sql.append(" AND (dateExpire >= CONVERT(datetime,'").append(dateSystem).append("',120) OR dateExpire IS NULL) ");
				break;
			case Constants.MANEJADOR_POSTGRES:
				sql.append(" AND (dateExpire >= CAST('").append(dateSystem).append("' as timestamp) OR dateExpire IS NULL) ");
				break;
			case Constants.MANEJADOR_MYSQL:
				sql.append(" AND (dateExpire >= TIMESTAMP('").append(dateSystem).append("') OR dateExpire IS NULL) ");
				break;
			}

			if (owner) {
				sql.append(" AND isOwner = '1'");
			} else {
				// sql.append(" AND isOwner = '0'");
			}
		} else {
			// Es expirado
			switch (Constants.MANEJADOR_ACTUAL) {
			case Constants.MANEJADOR_MSSQL:
				sql.append(" AND dateExpire < CONVERT(datetime,'").append(dateSystem).append("',120)");
				// sql.append(" AND pending = ").append(Constants.permission);
				break;
			case Constants.MANEJADOR_POSTGRES:
				sql.append(" AND dateExpire < CAST('").append(dateSystem).append("' as timestamp) ");
				// sql.append(" AND pending = '").append(Constants.permission).append("'");
				break;
			case Constants.MANEJADOR_MYSQL:
				sql.append(" AND dateExpire < TIMESTAMP('").append(dateSystem).append("') ");
				// sql.append(" AND pending = '").append(Constants.permission).append("'");
				break;
			}

			// Si es sencuencial solo le debe aparecer al generador si es el
			// unico que falta por contestar.
			// Lo que es igual a que solo le aparece al de mayor orden
			//sql.append(" AND ((w.Secuential = '0' AND uw.`row`in (select min(row) from user_workflows where idworkflow=w.idWorkFlow AND dateReplied is null ) )");
			sql.append(" AND ((w.Secuential = '0' AND uw.row in (select min(row) from user_workflows where idworkflow=w.idWorkFlow AND dateReplied is null ) )");

			// Si es NO secuencial no importa si es propietario o no, le debe
			// aparecer a todos los que les falto responder
			sql.append(" OR (w.Secuential = '1' AND uw.dateReplied is null)) ");
		}

		if (HandlerWorkFlows.wfuExpired.equals(statuUser)) {
			sql.append(" AND ((uw.idUser = w.owner AND includeOwner.include = '1') OR (uw.idUser <> w.owner AND includeOwner.include IS NULL))");
		}
		if (wfExpires) {
			sql.append(" ORDER BY w.dateCompleted ASC");
		}

		Connection con = null;
		PreparedStatement st = null;
		ResultSet rs = null;
		try {
			con = JDBCUtil.getConnection(Thread.currentThread().getStackTrace()[1].getMethodName());
			log.debug("[getAllWorkFlowsUserAndStatu] = " + sql);
			System.out.println("[getAllWorkFlowsUserAndStatu] = " + sql);
			st = con.prepareStatement(JDBCUtil.replaceCastMysql(sql.toString()));
			rs = st.executeQuery();
			String idNode = null;
			String prefixDoc = "";
			while (rs.next()) {
				idNode = "";
				prefixDoc = "";
				DataUserWorkFlowForm data = new DataUserWorkFlowForm();
				data.setIdWorkFlow(rs.getInt("idWorkFlow"));
				data.setNameDocument(rs.getString("nameDocument"));
				data.setIdUser(rs.getString("idUser"));
				data.setTypeWF(rs.getInt("type"));
				// ydavila Ticket 001-00-003023
				data.setsubTypeWF(rs.getInt("subtype"));
				switch (rs.getInt("subtype")) {
				case 0:
					data.setsubTypeWFdesc(" ");
					break;
				case 1:
					data.setsubTypeWFdesc(" ");
					break;
				case 2:
					data.setsubTypeWFdesc(" "); // verificar si es de impresi�n
					break;
				case 3:
					data.setsubTypeWFdesc(rb.getString("btn.wfSolCambio"));
					break;
				case 4:
					data.setsubTypeWFdesc(rb.getString("btn.wfSolElimin"));
					break;
				}
				data.setRow(rs.getString("row"));
				data.setOwner(rs.getString("idUser").equals(idUser));
				// Nombre y cargo del propietario
				// data.setNameUser(rs.getString("apellidos") + " " +
				// rs.getString("nombres"));
				data.setNameUser(rs.getString("owner"));
				data.setTypeDOC(rs.getString("typedoc") != null ? Integer.parseInt(rs.getString("typedoc")) : 0);
				data.setFlexFlow(false);
				log.debug("Search Name workflow '" + data.getTypeWF() + "'");
				data.setNameWorkFlow(rb.getString("wf.type" + data.getTypeWF()));
				// java.sql.Date date = rs.getDate("dateExpire");
				// if (date!=null) {
				// log.debug("date = " + date);
				// data.setDateExpire(ToolsHTML.formatDateShow(ToolsHTML.sdfShowConvert.format(date),true));
				// } else {
				// data.setDateExpire("N/A");
				// }

				java.sql.Timestamp date = rs.getTimestamp("dateExpire");
				if (date != null) {
					data.setDateExpire(ToolsHTML.formatDateShow(ToolsHTML.sdfShowConvert.format(date), true));
				} else {
					data.setDateExpire("N/A");
				}

				// indicamos la fecha de creacion del flujo
				date = rs.getTimestamp("dateCreation");
				if (date != null) {
					data.setDateCreation(ToolsHTML.formatDateShow(ToolsHTML.sdfShowConvert.format(date), true));
				} else {
					data.setDateCompleted("N/A");
				}

				// Luis Cisneros
				// 01/03/07
				// Fecha de completacion del Flujo
				date = rs.getTimestamp("dateCompleted");
				if (date != null) {
					data.setDateCompleted(ToolsHTML.formatDateShow(ToolsHTML.sdfShowConvert.format(date), true));
				} else {
					data.setDateCompleted("N/A");
				}
				// FIN 01/03/07

				String minorVer = rs.getString("MinorVer");
				if (ToolsHTML.isEmptyOrNull(minorVer)) {
					minorVer = "";
				}
				String mayorVer = rs.getString("MayorVer");
				if (ToolsHTML.isEmptyOrNull(mayorVer)) {
					mayorVer = "";
				}
				String version = mayorVer.trim() + "." + minorVer.trim();
				data.setIdVersion(version);
				data.setNumber(rs.getString("number") != null ? rs.getString("number") : "");
				data.setPrefix(rs.getString("prefix") != null ? rs.getString("prefix") : "");
				if (ToolsHTML.isEmptyOrNull(data.getNumber())) {
					data.setNumber("");
				}
				if (ToolsHTML.isEmptyOrNull(data.getPrefix())) {
					data.setPrefix("");
				}

				// construimos el bean de persona asociado a este elemento
				Person flowWorker = new Person(rs.getInt("fwIdPerson"), rs.getString("fwNombres"), rs.getString("fwApellidos"), rs.getString("fwEmail"));
				data.setPersonBean(flowWorker);

				Person askingPerson = new Person(rs.getInt("akIdPerson"), rs.getString("akNombres"), rs.getString("akApellidos"), rs.getString("akEmail"));
				data.setFlowRequieredByPersonBean(askingPerson);

				resp.add(data);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			setFinally(con, st, rs);
		}
		return resp;
	}

	// *******************************************************************************************
	public static synchronized Collection<DataUserWorkFlowForm> getAllWorkFlowsOwnerUserAndStatu(boolean wfExpires, String statu, String idUser,
			String statuUser, boolean owner, ResourceBundle rb, Hashtable struct, Hashtable prefijos, String wfType, String viewInCanceled,
			boolean searchAsAdmin, boolean applyValidity) throws Exception {
		Vector<DataUserWorkFlowForm> resp = new Vector<DataUserWorkFlowForm>();
		StringBuilder sql = new StringBuilder(100);
		// ydavila Ticket 001-00-003023
		// sql.append("SELECT w.idWorkFlow,w.type,w.dateExpire,w.owner,d.nameDocument,vd.MayorVer,vd.MinorVer,d.type as typedoc");
		sql.append("SELECT w.idWorkFlow,w.type,w.subtype,w.dateExpire,w.owner,d.nameDocument,vd.MayorVer,vd.MinorVer,d.type as typedoc");
		sql.append(",d.number,d.prefix,d.idNode, w.dateCompleted, w.toImpresion, d.numgen,");
		sql.append(" flowWorker.idperson AS fwIdPerson, flowWorker.nombres AS fwNombres, flowWorker.apellidos AS fwApellidos, flowWorker.email AS fwEmail,");
		sql.append(" askingPerson.idperson AS akIdPerson, askingPerson.nombres AS akNombres, askingPerson.apellidos AS akApellidos, askingPerson.email AS akEmail");
		sql.append(" FROM person flowWorker, person askingPerson, workflows w,documents d,versiondoc vd, user_workflows uw");
		sql.append(" WHERE w.idVersion = vd.numVer AND d.active = '").append(Constants.permission).append("'");
		sql.append(" AND d.numGen = w.idDocument");
		sql.append(" AND w.idWorkFlow = uw.idWorkFlow");
		sql.append(" AND flowWorker.nameuser = w.owner");
		sql.append(" AND askingPerson.nameuser = uw.iduser");
		// sql.append(" AND askingPerson.nameuser <> w.owner");
		sql.append(" AND uw.datereplied IS NULL ");
		sql.append(" AND uw.idUser = '").append(idUser).append("'"); // condicion porque debe ser solo el propietario
		if (!searchAsAdmin) {
			// los usuarios que no sean del tipo administrador, solo podran ver
			// archivos y flujos
			// de los cuales ellos sean los propietarios
			sql.append(" AND w.owner = '").append(idUser).append("'");
		}

		if (!ToolsHTML.isEmptyOrNull(statu)) {
			sql.append(" AND w.Statu  = '").append(statu.trim()).append("'");
		}
		String dateSystem = ToolsHTML.sdfShowConvert1.format(new Date());
		if (!wfExpires) {
			switch (Constants.MANEJADOR_ACTUAL) {
			case Constants.MANEJADOR_MSSQL:
				sql.append(" AND (dateExpire >= CONVERT(datetime,'").append(dateSystem).append("',120) OR dateExpire IS NULL) ");
				break;
			case Constants.MANEJADOR_POSTGRES:
				sql.append(" AND (dateExpire >= CAST('").append(dateSystem).append("' as timestamp) OR dateExpire IS NULL) ");
				break;
			case Constants.MANEJADOR_MYSQL:
				sql.append(" AND (dateExpire >= TIMESTAMP('").append(dateSystem).append("') OR dateExpire IS NULL) ");
				break;
			}
		} else {
			switch (Constants.MANEJADOR_ACTUAL) {
			case Constants.MANEJADOR_MSSQL:
				sql.append(" AND uw.pending = 1");
				sql.append(" AND dateExpire < CONVERT(datetime,'").append(dateSystem).append("',120)");
				break;
			case Constants.MANEJADOR_POSTGRES:
				sql.append(" AND uw.pending = ").append(JDBCUtil.getCastAsBitString("1"));
				sql.append(" AND dateExpire < CAST('").append(dateSystem).append("' as timestamp) ");
				break;
			case Constants.MANEJADOR_MYSQL:
				sql.append(" AND uw.pending = 1 ");
				sql.append(" AND dateExpire < TIMESTAMP('").append(dateSystem).append("') ");
				break;
			}
		}

		// Luis Cisneros
		// 27-03-07
		// Puedo filtar por tipo, 1= Aprobaci�n 0=Revisi�n
		if (wfType != null && wfType.length() > 0) {
			sql.append(" AND w.type='");
			sql.append(wfType).append("'");
		}

		// Luis Cisneros
		// 28-03-07
		// Si los flujos tienen el campo de viewInCanceled en true,
		// se muestan en el flujo, si no, es xq se creo un nuevo flujo y ya no
		// deberia estar en los cancelados
		if (viewInCanceled != null && viewInCanceled.length() > 0) {
			sql.append("  AND w.viewInCanceled = '");
			sql.append(viewInCanceled).append("'");

			if (!ToolsHTML.isEmptyOrNull(statu) && statu.equals(HandlerWorkFlows.wfuCanceled) && viewInCanceled.equals("1")) {
				// el IS NULL es para los documentos borradores
				sql.append(" AND (w.dateCompleted>d.datePublic OR d.datePublic IS NULL)");
			}
		}

		// revisar que campo de la tabla es la que debe tener la fecha de
		// cancelacion al comparar
		if (applyValidity) {
			int validity = 15;

			try {
				validity = HandlerParameters.PARAMETROS.getVigenciaFlujosCanceladosPrincipal();
			} catch (Exception e) {
				// TODO: handle exception
			}

			// verificamos el manejador
			switch (Constants.MANEJADOR_ACTUAL) {
			case Constants.MANEJADOR_MSSQL:
				sql.append(" AND DATEDIFF(DAY, w.dateCompleted, getdate()) <= ").append(validity);
				break;
			case Constants.MANEJADOR_POSTGRES:
				sql.append(" AND DATE_PART('day', now() - w.dateCompleted) <= ").append(validity);
				break;
			case Constants.MANEJADOR_MYSQL:
				sql.append(" AND DATEDIFF(now(),w.dateCompleted) <= ").append(validity);
				break;
			}
		}

		// if (!wfExpires) {
		// sql.append(" ORDER BY w.type ASC");
		// }else{
		sql.append(" ORDER BY w.dateCompleted ASC");
		// }

		Connection con = null;
		PreparedStatement st = null;
		ResultSet rs = null;
		try {
			con = JDBCUtil.getConnection(Thread.currentThread().getStackTrace()[1].getMethodName());
			log.debug("[getAllWorkFlowsOwnerUserAndStatu] = " + sql);
			// //System.out.println("[getAllWorkFlowsOwnerUserAndStatu] = " +
			// sql);
			st = con.prepareStatement(JDBCUtil.replaceCastMysql(sql.toString()));
			rs = st.executeQuery();
			String idNode = null;
			String prefixDoc = "";
			while (rs.next()) {
				idNode = "";
				prefixDoc = "";
				DataUserWorkFlowForm data = new DataUserWorkFlowForm();
				data.setIdDocument(rs.getInt("numgen"));
				data.setIdWorkFlow(rs.getInt("idWorkFlow"));
				data.setNameDocument(rs.getString("nameDocument"));
				data.setIdUser(rs.getString("owner"));
				data.setTypeWF(rs.getInt("type"));
				// ydavila Ticket 001-00-003023
				data.setsubTypeWF(rs.getInt("subtype"));
				switch (rs.getInt("subtype")) {
				case 0:
					data.setsubTypeWFdesc(" ");
					break;
				case 1:
					data.setsubTypeWFdesc(" ");
					break;
				case 2:
					data.setsubTypeWFdesc(" "); // verificar si es de impresi�n
					break;
				case 3:
					data.setsubTypeWFdesc(rb.getString("btn.wfSolCambio"));
					break;
				case 4:
					data.setsubTypeWFdesc(rb.getString("btn.wfSolElimin"));
					break;
				}
				data.setRow(null);
				data.setOwner(false);
				data.setTypeDOC(rs.getString("typedoc") != null ? Integer.parseInt(rs.getString("typedoc")) : 0);
				data.setFlexFlow(false);
				log.debug("Search Name workflow '" + data.getTypeWF() + "'");
				data.setNameWorkFlow(rb.getString("wf.type" + data.getTypeWF()));
				// java.sql.Date date = rs.getDate("dateExpire");
				// if (date!=null) {
				// log.debug("date = " + date);
				// data.setDateExpire(ToolsHTML.formatDateShow(ToolsHTML.sdfShowConvert.format(date),true));
				// } else {
				// data.setDateExpire("N/A");
				// }

				java.sql.Timestamp date = rs.getTimestamp("dateExpire");
				if (date != null) {
					data.setDateExpire(ToolsHTML.formatDateShow(ToolsHTML.sdfShowConvert.format(date), true));
				} else {
					data.setDateExpire("N/A");
				}

				// Luis Cisneros
				// 01/03/07
				// Fecha de completacion del Flujo
				date = rs.getTimestamp("dateCompleted");
				if (date != null) {
					data.setDateCompleted(ToolsHTML.formatDateShow(ToolsHTML.sdfShowConvert.format(date), true));
				} else {
					data.setDateCompleted("N/A");
				}
				// FIN 01/03/07

				String minorVer = rs.getString("MinorVer");
				if (ToolsHTML.isEmptyOrNull(minorVer)) {
					minorVer = "";
				}
				String mayorVer = rs.getString("MayorVer");
				if (ToolsHTML.isEmptyOrNull(mayorVer)) {
					mayorVer = "";
				}
				String version = mayorVer.trim() + "." + minorVer.trim();
				data.setIdVersion(version);
				data.setNumber(rs.getString("number") != null ? rs.getString("number") : "");
				data.setPrefix(rs.getString("prefix") != null ? rs.getString("prefix") : "");
				if (ToolsHTML.isEmptyOrNull(data.getNumber())) {
					data.setNumber("");
				}
				if (ToolsHTML.isEmptyOrNull(data.getPrefix())) {
					data.setPrefix("");
				}
				data.setToImpresion(rs.getInt("toImpresion"));

				// construimos el bean de persona asociado a este elemento
				Person flowWorker = new Person(rs.getInt("fwIdPerson"), rs.getString("fwNombres"), rs.getString("fwApellidos"), rs.getString("fwEmail"));
				data.setPersonBean(flowWorker);

				Person askingPerson = new Person(rs.getInt("akIdPerson"), rs.getString("akNombres"), rs.getString("akApellidos"), rs.getString("akEmail"));
				data.setFlowRequieredByPersonBean(askingPerson);

				resp.add(data);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			setFinally(con, st, rs);
		}
		return resp;
	}

	// **************************************************************************************
	// Consulta FTPs de un originador
	public static Collection<DataUserWorkFlowForm> getAllFlexWorkFlowsOwnerUserAndStatu(boolean wfExpires, String statu, String idUser, String statuUser,
			Hashtable struct, Hashtable prefijos, String msgReinit, String result, String resultUser, ResourceBundle rb, boolean searchAsAdmin)
			throws Exception {
		Vector<DataUserWorkFlowForm> resp = new Vector<DataUserWorkFlowForm>();
		StringBuilder sql = new StringBuilder(100);
		sql.append("SELECT DISTINCT w.idWorkFlow,w.type,w.dateExpire,d.nameDocument,vd.MayorVer,vd.MinorVer,sAct.sAct_Name,d.idNode,");
		sql.append(" d.numgen,d.prefix,d.number, w.owner, w.datecompleted, w.datecreation,");
		sql.append(" flowWorker.idperson AS fwIdPerson, flowWorker.nombres AS fwNombres, flowWorker.apellidos AS fwApellidos, flowWorker.email AS fwEmail,");
		sql.append(" askingPerson.idperson AS akIdPerson, askingPerson.nombres AS akNombres, askingPerson.apellidos AS akApellidos, askingPerson.email AS akEmail");
		sql.append(" FROM person flowWorker, person askingPerson, flexworkflow w, documents d,versiondoc vd,subactivities sAct, user_workflows uw ");
		sql.append(" WHERE w.idVersion = vd.numVer");
		sql.append(" AND d.numGen = w.idDocument");
		sql.append(" AND d.active = '").append(Constants.permission).append("'");
		sql.append(" AND w.type = sAct.sAct_Number");
		sql.append(" AND flowWorker.nameuser = w.owner");
		sql.append(" AND askingPerson.nameuser = uw.iduser");
		sql.append(" AND askingPerson.nameuser <> w.owner");
		sql.append(" AND uw.datereplied IS NULL");
		if (!searchAsAdmin) {
			// los usuarios que no sean del tipo administrador, solo podran ver
			// archivos y flujos
			// de los cuales ellos sean los propietarios
			sql.append(" AND w.owner = '").append(idUser).append("'");
		}
		if (!ToolsHTML.isEmptyOrNull(statu)) {
			sql.append(" AND w.Statu = '").append(statu.trim()).append("'");
		}
		String dateSystem = ToolsHTML.sdfShowConvert1.format(new Date());
		if (!wfExpires) {
			switch (Constants.MANEJADOR_ACTUAL) {
			case Constants.MANEJADOR_MSSQL:
				sql.append(" AND (dateExpire >= CONVERT(datetime,'").append(dateSystem).append("',120) OR dateExpire IS NULL) ");
				break;
			case Constants.MANEJADOR_POSTGRES:
				sql.append(" AND (dateExpire >= CAST('").append(dateSystem).append("' as timestamp) OR dateExpire IS NULL) ");
				break;
			case Constants.MANEJADOR_MYSQL:
				sql.append(" AND (dateExpire >= TIMESTAMP('").append(dateSystem).append("') OR dateExpire IS NULL) ");
				break;
			}
		} else {
			switch (Constants.MANEJADOR_ACTUAL) {
			case Constants.MANEJADOR_MSSQL:
				sql.append(" AND uw.pending = 1");
				sql.append(" AND dateExpire < CONVERT(datetime,'").append(dateSystem).append("',120)");
				break;
			case Constants.MANEJADOR_POSTGRES:
				sql.append(" AND uw.pending = ").append(JDBCUtil.getCastAsBitString("1"));
				sql.append(" AND dateExpire < CAST('").append(dateSystem).append("' as timestamp) ");
				break;
			case Constants.MANEJADOR_MYSQL:
				sql.append(" AND uw.pending = 1 ");
				sql.append(" AND dateExpire < TIMESTAMP('").append(dateSystem).append("') ");
				break;
			}
			if (!ToolsHTML.isEmptyOrNull(result)) {
				sql.append(" AND w.result  = '").append(result.trim()).append("'");
			}
			sql.append(" ORDER BY w.dateCompleted ASC");
		}
		Connection con = null;
		PreparedStatement st = null;
		ResultSet rs = null;
		try {
			con = JDBCUtil.getConnection(Thread.currentThread().getStackTrace()[1].getMethodName());
			log.debug("[getAllFlexWorkFlowsOwnerUserAndStatu] = " + sql);
			// //System.out.println("[getAllFlexWorkFlowsOwnerUserAndStatu] = "
			// + sql);
			st = con.prepareStatement(JDBCUtil.replaceCastMysql(sql.toString()));
			rs = st.executeQuery();
			String idNode = null;
			String prefixDoc = "";
			while (rs.next()) {
				idNode = "";
				prefixDoc = "";
				DataUserWorkFlowForm data = new DataUserWorkFlowForm();
				data.setIdWorkFlow(rs.getInt("idWorkFlow"));
				data.setNameDocument(rs.getString("nameDocument"));
				data.setIdUser(rs.getString("owner"));
				data.setTypeWF(rs.getInt("type"));
				// ydavila Ticket 001-00-003023
				/*
				 * data.setsubTypeWF(rs.getInt("subtype")); switch (rs.getInt("subtype")) { case 0: data.setsubTypeWFdesc(" "); break; case 1:
				 * data.setsubTypeWFdesc(" "); break; case 2: data.setsubTypeWFdesc(" "); //verificar si es de impresi�n break; case 3:
				 * data.setsubTypeWFdesc(rb.getString("btn.wfSolCambio")); break; case 4: data.setsubTypeWFdesc(rb.getString("btn.wfSolElimin")); break; }
				 */
				// data.setRow(String.valueOf(rs.getLong("idFlexWF")));
				data.setNameUser(rs.getString("owner"));
				data.setFlexFlow(true);
				data.setIdDocument(rs.getInt("numgen"));

				if (ToolsHTML.isEmptyOrNull(rs.getString("sAct_Name"))) {
					data.setNameWorkFlow(msgReinit);
				} else {
					data.setNameWorkFlow(rb.getString("wf.type.ftp").concat(" - ").concat(rs.getString("sAct_Name")));
				}

				java.sql.Timestamp date = rs.getTimestamp("dateExpire");
				if (date != null) {
					data.setDateExpire(ToolsHTML.formatDateShow(ToolsHTML.sdfShowConvert.format(date), true));
				} else {
					data.setDateExpire("N/A");
				}

				// colocamos la fecha de creacion
				date = rs.getTimestamp("dateCreation");
				if (date != null) {
					data.setDateCreation(ToolsHTML.formatDateShow(ToolsHTML.sdfShowConvert.format(date), true));
				} else {
					data.setDateCreation("N/A");
				}

				// YSA 16/07/08
				// Fecha de completacion del Flujo
				date = rs.getTimestamp("dateCompleted");
				if (date != null) {
					data.setDateCompleted(ToolsHTML.formatDateShow(ToolsHTML.sdfShowConvert.format(date), true));
				} else {
					data.setDateCompleted("N/A");
				}
				// FIN 16/07/08

				String minorVer = rs.getString("MinorVer");
				if (ToolsHTML.isEmptyOrNull(minorVer)) {
					minorVer = "";
				}
				String mayorVer = rs.getString("MayorVer");
				if (ToolsHTML.isEmptyOrNull(mayorVer)) {
					mayorVer = "";
				}
				String version = mayorVer.trim() + "." + minorVer.trim();
				data.setIdVersion(version);
				data.setNumber(rs.getString("number") != null ? rs.getString("number") : "");
				idNode = rs.getString("idNode");
				// Se Busca el Prefijo del Documento seg�n como se encuentre
				// Definido el Mismo
				if (struct != null) {
					if (!prefijos.containsKey(idNode)) {
						prefixDoc = ToolsHTML.getPrefixToDoc(struct, idNode);
						prefijos.put(idNode, prefixDoc);
					} else {
						prefixDoc = (String) prefijos.get(idNode);
					}

					if (!ToolsHTML.isEmptyOrNull(prefixDoc)) {
						data.setPrefix(prefixDoc.trim());
					} else {
						data.setPrefix("");
					}
				} else {
					data.setPrefix(rs.getString("prefix") != null ? rs.getString("prefix") : "");
				}
				if (ToolsHTML.isEmptyOrNull(data.getNumber())) {
					data.setNumber("");
				}
				if (ToolsHTML.isEmptyOrNull(data.getPrefix())) {
					data.setPrefix("");
				}

				// construimos el bean de persona asociado a este elemento
				Person flowWorker = new Person(rs.getInt("fwIdPerson"), rs.getString("fwNombres"), rs.getString("fwApellidos"), rs.getString("fwEmail"));
				data.setPersonBean(flowWorker);

				Person askingPerson = new Person(rs.getInt("akIdPerson"), rs.getString("akNombres"), rs.getString("akApellidos"), rs.getString("akEmail"));
				data.setFlowRequieredByPersonBean(askingPerson);

				resp.add(data);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			setFinally(con, st, rs);
		}
		return resp;
	}

	// **************************************************************************************
	/**
	 *
	 * @param wfExpires
	 * @param statu
	 * @param idUser
	 * @param statuUser
	 * @param struct
	 * @param prefijos
	 * @param msgReinit
	 * @param result
	 * @param resultUser
	 * @param searchAsAdmin
	 * @return
	 * @throws Exception
	 */
	public static Collection<DataUserWorkFlowForm> getAllFlexWorkFlowsUserAndStatu(boolean wfExpires, String statu, String idUser, String statuUser,
			Hashtable struct, Hashtable prefijos, String msgReinit, String result, String resultUser, ResourceBundle rb, boolean searchAsAdmin)
			throws Exception {
		Vector resp = new Vector();
		StringBuilder sql = new StringBuilder(100);
		sql.append("SELECT w.idWorkFlow,w.type,w.dateExpire,uw.idUser,MAX(uw.idFlexWF) AS idFlexWF,d.nameDocument,vd.MayorVer,vd.MinorVer,");
		sql.append(" sAct.sAct_Name,uw.isOwner,d.idNode,d.prefix,d.number, w.owner, w.datecompleted, w.dateCreation,");
		sql.append(" flowWorker.idperson AS fwIdPerson, flowWorker.nombres AS fwNombres, flowWorker.apellidos AS fwApellidos, flowWorker.email AS fwEmail,");
		sql.append(" askingPerson.idperson AS akIdPerson, askingPerson.nombres AS akNombres, askingPerson.apellidos AS akApellidos, askingPerson.email AS akEmail");
		sql.append(" FROM person flowWorker, person askingPerson, flexworkflow w,user_flexworkflows uw,documents d,versiondoc vd,subactivities sAct");
		if (HandlerWorkFlows.wfuExpired.equals(statuUser)) {
			// tabla interna para saber si en los flujos expirados, los mismos
			// se le vencieron al originador o a los participantes
			// en los flujos de tipo parametrico, los involucrados (menos el
			// originador) estan registrados en la tabla User_FlexWorkFlows
			sql.append(" , (SELECT uw1.idworkflow, CASE WHEN (COUNT(*)) = 0 THEN '1' ELSE NULL END AS include FROM user_flexworkflows uw1 WHERE uw1.statu = '")
					.append(statuUser.trim()).append("' GROUP BY uw1.idworkflow) includeOwner");
		}
		sql.append(" WHERE w.idWorkFlow = uw.idWorkFlow");
		if (HandlerWorkFlows.wfuExpired.equals(statuUser)) {
			sql.append(" AND includeOwner.idworkflow = uw.idWorkFlow");
		}
		sql.append(" AND w.idVersion = vd.numVer");
		sql.append(" AND flowWorker.nameuser = uw.idUser");
		sql.append(" AND askingPerson.nameuser=w.owner");
		if (!searchAsAdmin) {
			// los usuarios que no sean del tipo administrador, solo podran ver
			// archivos y flujos
			// de los cuales ellos sean los propietarios
			sql.append(" AND uw.idUser = '").append(idUser).append("'");
		}
		sql.append(" AND d.numGen = w.idDocument");
		sql.append(" AND d.active = '").append(Constants.permission).append("'");
		sql.append(" AND w.type = sAct.sAct_Number");
		if (!ToolsHTML.isEmptyOrNull(statuUser)) {
			sql.append(" AND uw.Statu  = '").append(statuUser.trim()).append("'");
		}
		if (!ToolsHTML.isEmptyOrNull(statu)) {
			sql.append(" AND w.Statu  = '").append(statu.trim()).append("'");
		}
		String dateSystem = ToolsHTML.sdfShowConvert1.format(new Date());
		if (!wfExpires) {
			sql.append(" AND uw.wfActive = '").append(Constants.permission).append("'");
			switch (Constants.MANEJADOR_ACTUAL) {
			case Constants.MANEJADOR_MSSQL:
				sql.append(" AND (dateExpire >= CONVERT(datetime,'").append(dateSystem).append("',120) OR dateExpire IS NULL) ");
				break;
			case Constants.MANEJADOR_POSTGRES:
				sql.append(" AND (dateExpire >= CAST('").append(dateSystem).append("' as timestamp) OR dateExpire IS NULL) ");
				break;
			case Constants.MANEJADOR_MYSQL:
				sql.append(" AND (dateExpire >= TIMESTAMP('").append(dateSystem).append("') OR dateExpire IS NULL) ");
				break;
			}
		} else {
			switch (Constants.MANEJADOR_ACTUAL) {
			case Constants.MANEJADOR_MSSQL:
				sql.append(" AND dateExpire < CONVERT(datetime,'").append(dateSystem).append("',120)");
				break;
			case Constants.MANEJADOR_POSTGRES:
				sql.append(" AND dateExpire < CAST('").append(dateSystem).append("' as timestamp) ");
				break;
			case Constants.MANEJADOR_MYSQL:
				sql.append(" AND dateExpire < TIMESTAMP('").append(dateSystem).append("') ");
				break;
			}
			// sql.append(" AND pending = '").append(Constants.permission).append("'");
			if (!ToolsHTML.isEmptyOrNull(resultUser)) {
				sql.append(" AND uw.result  = '").append(resultUser.trim()).append("'");
			}
			if (!ToolsHTML.isEmptyOrNull(result)) {
				sql.append(" AND w.result  = '").append(result.trim()).append("'");
			}

			if (HandlerWorkFlows.wfuExpired.equals(statuUser)) {
				sql.append(" AND ((uw.idUser = w.owner AND includeOwner.include = '1') OR (uw.idUser <> w.owner AND includeOwner.include IS NULL))");
			}
		}

		sql.append(" GROUP BY w.idWorkFlow,w.type,w.dateExpire,uw.idUser,d.nameDocument,vd.MayorVer,vd.MinorVer, sAct.sAct_Name,uw.isOwner,");
		sql.append(" d.idNode,d.prefix,d.number, w.owner, w.datecompleted, flowWorker.idperson, flowWorker.nombres, flowWorker.apellidos,");
		sql.append(" flowWorker.email, askingPerson.idperson, askingPerson.nombres, askingPerson.apellidos, askingPerson.email,w.datecreation");
		sql.append(" ORDER BY w.dateCompleted ASC");

		Connection con = null;
		PreparedStatement st = null;
		ResultSet rs = null;
		try {
			con = JDBCUtil.getConnection(Thread.currentThread().getStackTrace()[1].getMethodName());
			log.debug("[getAllFlexWorkFlowsUserAndStatu] = " + sql);
			// //System.out.println("[getAllFlexWorkFlowsUserAndStatu] = " +
			// sql);
			st = con.prepareStatement(JDBCUtil.replaceCastMysql(sql.toString()));
			rs = st.executeQuery();
			String idNode = null;
			String prefixDoc = "";
			while (rs.next()) {
				idNode = "";
				prefixDoc = "";
				DataUserWorkFlowForm data = new DataUserWorkFlowForm();
				data.setIdWorkFlow(rs.getInt("idWorkFlow"));
				data.setNameDocument(rs.getString("nameDocument"));
				data.setIdUser(rs.getString("idUser"));
				data.setTypeWF(rs.getInt("type"));
				// ydavila Ticket 001-00-003023
				data.setsubTypeWFdesc("  ");
				data.setRow(String.valueOf(rs.getLong("idFlexWF")));
				data.setOwner(Constants.permission == rs.getByte("isOwner"));
				data.setNameUser(rs.getString("owner"));
				data.setFlexFlow(true);

				if (Constants.permission == rs.getByte("isOwner")) {
					data.setNameWorkFlow(rb.getString("wf.type.ftp").concat(" - ").concat(msgReinit));
				} else {
					data.setNameWorkFlow(rb.getString("wf.type.ftp").concat(" - ").concat(rs.getString("sAct_Name")));
				}
				java.sql.Timestamp date = rs.getTimestamp("dateExpire");
				if (date != null) {
					data.setDateExpire(ToolsHTML.formatDateShow(ToolsHTML.sdfShowConvert.format(date), true));
				} else {
					data.setDateExpire("N/A");
				}

				// Fecha de creacion del Flujo
				date = rs.getTimestamp("dateCreation");
				if (date != null) {
					data.setDateCreation(ToolsHTML.formatDateShow(ToolsHTML.sdfShowConvert.format(date), true));
				} else {
					data.setDateCompleted("N/A");
				}

				// YSA 16/07/08
				// Fecha de completacion del Flujo
				date = rs.getTimestamp("dateCompleted");
				if (date != null) {
					data.setDateCompleted(ToolsHTML.formatDateShow(ToolsHTML.sdfShowConvert.format(date), true));
				} else {
					data.setDateCompleted("N/A");
				}
				// FIN 16/07/08

				String minorVer = rs.getString("MinorVer");
				if (ToolsHTML.isEmptyOrNull(minorVer)) {
					minorVer = "";
				}
				String mayorVer = rs.getString("MayorVer");
				if (ToolsHTML.isEmptyOrNull(mayorVer)) {
					mayorVer = "";
				}
				String version = mayorVer.trim() + "." + minorVer.trim();
				data.setIdVersion(version);
				data.setNumber(rs.getString("number") != null ? rs.getString("number") : "");
				idNode = rs.getString("idNode");
				// Se Busca el Prefijo del Documento seg�n como se encuentre
				// Definido el Mismo
				if (struct != null) {
					if (!prefijos.containsKey(idNode)) {
						prefixDoc = ToolsHTML.getPrefixToDoc(struct, idNode);
						prefijos.put(idNode, prefixDoc);
					} else {
						prefixDoc = (String) prefijos.get(idNode);
					}

					if (!ToolsHTML.isEmptyOrNull(prefixDoc)) {
						data.setPrefix(prefixDoc.trim());
					} else {
						data.setPrefix("");
					}
				} else {
					data.setPrefix(rs.getString("prefix") != null ? rs.getString("prefix") : "");
				}
				if (ToolsHTML.isEmptyOrNull(data.getNumber())) {
					data.setNumber("");
				}
				if (ToolsHTML.isEmptyOrNull(data.getPrefix())) {
					data.setPrefix("");
				}

				// construimos el bean de persona asociado a este elemento
				Person flowWorker = new Person(rs.getInt("fwIdPerson"), rs.getString("fwNombres"), rs.getString("fwApellidos"), rs.getString("fwEmail"));
				data.setPersonBean(flowWorker);

				Person askingPerson = new Person(rs.getInt("akIdPerson"), rs.getString("akNombres"), rs.getString("akApellidos"), rs.getString("akEmail"));
				data.setFlowRequieredByPersonBean(askingPerson);

				resp.add(data);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			setFinally(con, st, rs);
		}
		return resp;
	}

	// SIMON 20 DE JUNIO 2005 INICIO

	/*
	 * Env�a correo de documentos expirados (borradores o aprobados dependiendo del par�metro). S�lo se est�n enviando correos a propietarios, para los
	 * administradores est� comentado. Toma en cuenta el campo expireDoc/expireDrafts de la tabla parameters (si est�n en 0). Actualmente se sustituy� ejecuci�n
	 * de este metodo por HandeletDocuments.getAllNotifDocumentsExpires() y se ejecuta como un hilo CheckDocvencThread.java
	 */
	// public static Collection getAllVersionForDocumentExpires() throws
	// Exception{
	public synchronized static void getAllVersionForDocumentExpires(HttpServletRequest request, String statusdocumento) throws Exception {
		String Status_Documento = null;
		String dateSystem = ToolsHTML.sdfShowConvert1.format(new Date());
		StringBuilder sql = new StringBuilder();
		String str = null;
		String expirado = null;
		String campo = "";
		String expireDoc = String.valueOf(HandlerParameters.PARAMETROS.getExpireDoc());
		String expireDrafts = String.valueOf(HandlerParameters.PARAMETROS.getExpireDrafts());
		int numVer = 0;
		ResourceBundle rb = ToolsHTML.getBundle(request);
		if ("0".equalsIgnoreCase(expireDoc) || "0".equalsIgnoreCase(expireDrafts)) {
			if (statusdocumento == HandlerDocuments.docApproved) {
				Status_Documento = rb.getString("Status_docApproved");
				campo = "statuaprovado";
				sql.append("SELECT admin.email as mailadmin,vdc.numVer,");
				switch (Constants.MANEJADOR_ACTUAL) {
				case Constants.MANEJADOR_MSSQL:
					sql.append("(p.Apellidos+' '+p.Nombres) AS namePerson,");
					break;
				case Constants.MANEJADOR_POSTGRES:
					sql.append("(p.Apellidos||' '||p.Nombres) AS namePerson,");
					break;
				case Constants.MANEJADOR_MYSQL:
					sql.append("CONCAT(p.Apellidos,' ',p.Nombres) AS namePerson,");
					break;
				}
				sql.append("doc.namedocument,p.nameuser,p.email,vdc.dateExpires ");
				sql.append(" ,doc.number as number1,doc.prefix ");
				sql.append("FROM versiondoc vdc,documents doc,person p,(select distinct(email),idgrupo from person  where accountactive='")
						.append(Constants.permission).append("'  ) admin ");
				sql.append("WHERE doc.owner=p.nameuser and vdc.numDoc = doc.numGen and ");
				sql.append("vdc.statuaprovado='0' ");
				switch (Constants.MANEJADOR_ACTUAL) {
				case Constants.MANEJADOR_MSSQL:
					sql.append("AND vdc.dateExpires < CONVERT(datetime,'").append(dateSystem).append("',120)  ");
					break;
				case Constants.MANEJADOR_POSTGRES:
					sql.append(" AND vdc.dateExpires < CAST('").append(dateSystem).append("' as timestamp) ");
					break;
				case Constants.MANEJADOR_MYSQL:
					sql.append(" AND vdc.dateExpires < TIMESTAMP('").append(dateSystem).append("') ");
					break;
				}
				sql.append("AND vdc.DateApproved is not null  ");
				sql.append(" AND vdc.numVer = (SELECT MAX(vdi.numVer) FROM versiondoc vdi WHERE vdi.numDoc = vdc.numDoc");
				sql.append(" AND vdi.statu=").append(HandlerDocuments.docApproved).append(")");
				sql.append("AND vdc.active = ").append(Constants.permission).append("  ");
				// simon 27 de octubre 2005 inicio
				sql.append(" AND doc.type <>").append(HandlerDocuments.TypeDocumentsImpresion).append("  ");
				sql.append("AND doc.active = ").append(Constants.permission).append("  ");
				// simon 27 de octubre 2005 fin
				sql.append("AND admin.idgrupo=").append(DesigeConf.getProperty("application.admon"));
				sql.append(" ORDER BY vdc.numVer DESC");
			} else if (statusdocumento == HandlerDocuments.docTrash) {
				Status_Documento = rb.getString("Status_docTrash");
				campo = "statudraft";
				sql.append("SELECT admin.email as mailadmin,vdc.numVer,");
				switch (Constants.MANEJADOR_ACTUAL) {
				case Constants.MANEJADOR_MSSQL:
					sql.append("(p.Apellidos+' '+p.Nombres) AS namePerson,");
					break;
				case Constants.MANEJADOR_POSTGRES:
					sql.append("(p.Apellidos||' '||p.Nombres) AS namePerson,");
					break;
				case Constants.MANEJADOR_MYSQL:
					sql.append("CONCAT(p.Apellidos,' ',p.Nombres) AS namePerson,");
					break;
				}
				sql.append("doc.namedocument,p.nameuser,p.email,vdc.dateExpiresDrafts ");
				sql.append(" ,doc.number as number1,doc.prefix ");
				sql.append("FROM versiondoc vdc,documents doc,person p,(select distinct(email),idgrupo from person  where accountactive='1'  )admin ");
				sql.append("WHERE doc.owner=p.nameuser and vdc.numDoc = doc.numGen and ");
				sql.append("vdc.statudraft='0' AND ");
				sql.append("doc.statu= '").append(HandlerDocuments.docTrash).append("'").append(" ");
				sql.append(" AND doc.type <> '").append(HandlerDocuments.TypeDocumentsImpresion).append("' ");
				switch (Constants.MANEJADOR_ACTUAL) {
				case Constants.MANEJADOR_MSSQL:
					sql.append("AND vdc.dateExpiresDrafts < CONVERT(datetime,'").append(dateSystem).append("',120)  ");
					break;
				case Constants.MANEJADOR_POSTGRES:
					sql.append(" AND vdc.dateExpiresDrafts < CAST('").append(dateSystem).append("' as timestamp) ");
					break;
				case Constants.MANEJADOR_MYSQL:
					sql.append(" AND vdc.dateExpiresDrafts < TIMESTAMP('").append(dateSystem).append("') ");
					break;
				}
				sql.append("AND vdc.dateExpiresDrafts is not null  ");
				sql.append(" AND vdc.numVer = (SELECT MAX(vdi.numVer) FROM versiondoc vdi WHERE vdi.numDoc = vdc.numDoc");
				sql.append(" AND vdi.statu='").append(HandlerDocuments.docTrash).append("')");
				sql.append("AND vdc.active = '").append(Constants.permission).append("'  ");
				// simon 27 de octubre 2005 inicio
				sql.append("AND doc.active = '").append(Constants.permission).append("'  ");
				// simon 27 de octubre 2005 fin
				sql.append("AND admin.idgrupo=").append(DesigeConf.getProperty("application.admon"));
				sql.append(" ORDER BY vdc.numVer DESC");
				str = HandlerParameters.PARAMETROS.getMsgWFBorrador();

			}
			Vector datos = JDBCUtil.doQueryVector(sql.toString(),Thread.currentThread().getStackTrace()[1].getMethodName());
			String SwMailUsuario = "";
			String SwDocumentoUser = "";
			String prefixNumber = "";
			for (int row = 0; row < datos.size(); row++) {
				Properties properties = (Properties) datos.elementAt(row);
				BeanNotifiedWF userMailExp = new BeanNotifiedWF();
				userMailExp.setEmail((properties.getProperty("email") != null ? properties.getProperty("email") : HandlerParameters.PARAMETROS.getMailAccount()));
				String nombre = properties.getProperty("namePerson") != null ? properties.getProperty("namePerson") : "";
				String namedocument = properties.getProperty("namedocument") != null ? properties.getProperty("namedocument") : "";
				String mailAdmin = (properties.getProperty("mailadmin") != null ? properties.getProperty("mailadmin") : HandlerParameters.PARAMETROS.getMailAccount());
				prefixNumber = properties.getProperty("prefix") != null ? properties.getProperty("prefix") : "";
				String number = properties.getProperty("number1") != null ? properties.getProperty("number1") : "";
				prefixNumber = prefixNumber + number;
				numVer = Integer.parseInt(properties.getProperty("numVer"));

				if (statusdocumento == HandlerDocuments.docApproved) {
					expirado = properties.getProperty("dateExpires") != null ? properties.getProperty("dateExpires") : "";
				} else if (statusdocumento == HandlerDocuments.docTrash) {
					expirado = properties.getProperty("dateExpiresDrafts") != null ? properties.getProperty("dateExpiresDrafts") : "";
				}
				StringBuilder mensaje = new StringBuilder(100);
				mensaje.append(nombre).append(" ").append("<br/>");
				mensaje.append(rb.getString("documento_expir")).append(" ").append(namedocument).append(" ").append(rb.getString("estado_expir"))
						.append(Status_Documento).append("<br/>");
				mensaje.append(rb.getString("expiro_expir")).append(" ").append(expirado).append("<br/>");

				if (str != null) {
					mensaje.append(str).append("<br/>");
				}
				userMailExp.setComments(mensaje.toString());

				// Si son iguales, entonces se le manda solo a los
				// administradores que todavia no se les ha mandado
				// mails de notificaci�n.
				// solamente el correo cuando un documento expire se le va a
				// mandar al propietario
				if (SwMailUsuario.equalsIgnoreCase(userMailExp.getEmail()) && (SwDocumentoUser.equalsIgnoreCase(namedocument))) {
					// a los administradores no c les va a mandar �para que sea
					// amigable el sistema y de esta manera
					// no llenalos de correos
					/*
					 * HandlerWorkFlows.notifiedUsers(rb.getString("title_expir") , rb.getString("mail.nameUser"), HandlerParameters.PARAMETROS.getMailAccount()
					 * ,mailAdmin, userMailExp.getComments());
					 */
				} else {
					// Si iguales, entonces se le manda mail al usuario y al
					// administrador
					// mail al usuario
					HandlerWorkFlows.notifiedUsers(rb.getString("title_expir") + " " + prefixNumber, rb.getString("mail.nameUser"),
							HandlerParameters.PARAMETROS.getMailAccount(), userMailExp.getEmail(), userMailExp.getComments());
					// mail al administrador
					/*
					 * HandlerWorkFlows.notifiedUsers(rb.getString("title_expir") , rb.getString("mail.nameUser"), HandlerParameters.PARAMETROS.getMailAccount()
					 * ,mailAdmin, userMailExp.getComments());
					 */
				}
				SwMailUsuario = userMailExp.getEmail();
				SwDocumentoUser = namedocument;
				HandlerStruct.actualizarVersionDoc(numVer, campo, 1);
			}
		} else {
			log.debug("Ninguno de los mensajes para enviar notificacion de expirados en borrador y aprovados esta activado ");
		}

	}

	// SIMON 20 DE JUNIO 2005 FIN

	public static Collection getAllWorkFlowsPendingsWithDate() throws Exception {
		Vector resp = new Vector();
		StringBuilder sql = new StringBuilder(100);
		Users usuario = new Users();
		// Se Coloca el Grupo Administrador
		usuario.setIdGroup(DesigeConf.getProperty("application.admon"));
		// Se Coloca el Id del Usuario Administrador del Sistema
		usuario.setIdPerson(Long.parseLong(DesigeConf.getProperty("application.userAdmonKey")));
		usuario.setUser(DesigeConf.getProperty("application.userAdmon"));
		Hashtable tree = ToolsHTML.checkTree(null, usuario);
		Hashtable prefijos = new Hashtable();

		sql.append("SELECT w.idWorkFlow,w.type,w.dateExpire,w.idDocument");
		sql.append(",d.number,d.nameDocument,d.prefix,d.idNode");
		sql.append(" FROM workflows w, documents d WHERE w.dateExpire is NOT NULL");
		sql.append(" AND w.statu = '").append(pending).append("' ");
		sql.append(" AND d.numGen = w.idDocument");
		Connection con = null;
		PreparedStatement st = null;
		ResultSet rs = null;
		try {
			con = JDBCUtil.getConnection(Thread.currentThread().getStackTrace()[1].getMethodName());
			st = con.prepareStatement(JDBCUtil.replaceCastMysql(sql.toString()));
			rs = st.executeQuery();
			while (rs.next()) {
				DataUserWorkFlowForm data = new DataUserWorkFlowForm();
				data.setIdWorkFlow(rs.getInt("idWorkFlow"));
				// java.sql.Date date = rs.getDate("dateExpire");
				java.sql.Timestamp date = rs.getTimestamp("dateExpire");
				if (date != null) {
					data.setDateExpire(ToolsHTML.sdfShowConvert1.format(date));
				} else {
					data.setDateExpire("N/A");
				}
				data.setIdDocument(rs.getInt("idDocument"));

				data.setNameDocument(rs.getString("nameDocument"));
				data.setNumber(rs.getString("number"));
				String idNode = String.valueOf(rs.getInt("idNode"));
				String prefixDoc = null;
				if (!prefijos.containsKey(idNode)) {
					prefixDoc = ToolsHTML.getPrefixToDoc(tree, idNode);
					prefijos.put(idNode, prefixDoc);
				} else {
					prefixDoc = (String) prefijos.get(idNode);
				}

				if (!ToolsHTML.isEmptyOrNull(prefixDoc)) {
					data.setPrefix(prefixDoc.trim());
				} else {
					data.setPrefix(rs.getString("prefix"));
				}

				resp.add(data);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			setFinally(con, st, rs);
		}
		return resp;
	}

	public static Collection getAllFlexWorkFlowsPendingsWithDate() throws Exception {
		Vector resp = new Vector();
		StringBuilder sql = new StringBuilder(100);
		Users usuario = new Users();
		// Se Coloca el Grupo Administrador
		usuario.setIdGroup(DesigeConf.getProperty("application.admon"));
		// Se Coloca el Id del Usuario Administrador del Sistema
		usuario.setIdPerson(Long.parseLong(DesigeConf.getProperty("application.userAdmonKey")));
		usuario.setUser(DesigeConf.getProperty("application.userAdmon"));
		Hashtable tree = ToolsHTML.checkTree(null, usuario);
		Hashtable prefijos = new Hashtable();

		sql.append("SELECT fw.idWorkFlow,fw.type,fw.dateExpire,fw.idDocument");
		sql.append(",d.number,d.nameDocument,d.prefix,d.idNode,fw.IDflexflow");
		sql.append(" FROM flexworkflow fw, documents d WHERE fw.dateExpire is NOT NULL");
		sql.append(" AND fw.statu = '").append(pending).append("' ");
		sql.append(" AND fw.result = '").append(wfuPending).append("' ");
		sql.append(" AND d.numGen = fw.idDocument");
		Connection con = null;
		PreparedStatement st = null;
		ResultSet rs = null;
		try {
			con = JDBCUtil.getConnection(Thread.currentThread().getStackTrace()[1].getMethodName());
			st = con.prepareStatement(JDBCUtil.replaceCastMysql(sql.toString()));
			rs = st.executeQuery();
			while (rs.next()) {
				DataUserWorkFlowForm data = new DataUserWorkFlowForm();
				data.setIdWorkFlow(rs.getInt("idWorkFlow"));
				// java.sql.Date date = rs.getDate("dateExpire");
				java.sql.Timestamp date = rs.getTimestamp("dateExpire");
				// //System.out.println("fecha expiracion FTP: " + date);
				if (date != null) {
					data.setDateExpire(ToolsHTML.sdfShowConvert1.format(date));
				} else {
					data.setDateExpire("N/A");
				}
				data.setIdDocument(rs.getInt("idDocument"));
				data.setIdFlexFlow(rs.getLong("IDflexflow"));
				data.setNameDocument(rs.getString("nameDocument"));
				data.setNumber(rs.getString("number"));
				String idNode = String.valueOf(rs.getInt("idNode"));
				String prefixDoc = null;
				if (!prefijos.containsKey(idNode)) {
					prefixDoc = ToolsHTML.getPrefixToDoc(tree, idNode);
					prefijos.put(idNode, prefixDoc);
				} else {
					prefixDoc = (String) prefijos.get(idNode);
				}

				if (!ToolsHTML.isEmptyOrNull(prefixDoc)) {
					data.setPrefix(prefixDoc.trim());
				} else {
					data.setPrefix(rs.getString("prefix"));
				}

				resp.add(data);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			setFinally(con, st, rs);
		}
		return resp;
	}

	public static int countAllWorkFlows(boolean wfExpires, String statu, String idUser, String statuUser, boolean owner) throws Exception {
		StringBuilder sql = new StringBuilder(100);
		sql.append("SELECT Count(w.idWorkFlow) AS cuantos");
		sql.append(" FROM workflows w,user_workflows uw,documents d WHERE w.idWorkFlow = uw.idWorkFlow");
		sql.append(" AND uw.idUser = '").append(idUser).append("'");
		sql.append(" AND d.numGen = w.idDocument");
		sql.append(" AND uw.reading = '").append(Constants.permission).append("'");
		sql.append(" AND uw.active = '").append(Constants.permission).append("'");
		if (!ToolsHTML.isEmptyOrNull(statuUser)) {
			sql.append(" AND uw.Statu  = '").append(statuUser.trim()).append("'");
		}
		if (!ToolsHTML.isEmptyOrNull(statu)) {
			sql.append(" AND w.Statu  = '").append(statu.trim()).append("'");
		}
		String dateSystem = ToolsHTML.sdfShowConvert1.format(new Date());
		if (!wfExpires) {
			switch (Constants.MANEJADOR_ACTUAL) {
			case Constants.MANEJADOR_MSSQL:
				sql.append(" AND (dateExpire >= CONVERT(datetime,'").append(dateSystem).append("',120) OR dateExpire IS NULL) ");
				break;
			case Constants.MANEJADOR_POSTGRES:
				sql.append(" AND (dateExpire >= CAST('").append(dateSystem).append("' as timestamp) OR dateExpire IS NULL) ");
				break;
			case Constants.MANEJADOR_MYSQL:
				sql.append(" AND (dateExpire >= TIMESTAMP('").append(dateSystem).append("') OR dateExpire IS NULL) ");
				break;
			}

		} else {
			switch (Constants.MANEJADOR_ACTUAL) {
			case Constants.MANEJADOR_MSSQL:
				sql.append(" AND dateExpire < CONVERT(datetime,'").append(dateSystem).append("',120)");
				break;
			case Constants.MANEJADOR_POSTGRES:
				sql.append(" AND dateExpire < CAST('").append(dateSystem).append("' as timestamp) ");
				break;
			case Constants.MANEJADOR_MYSQL:
				sql.append(" AND dateExpire < TIMESTAMP('").append(dateSystem).append("') ");
				break;
			}
		}
		if (owner) {
			sql.append(" AND isOwner = '1'");
		} else {
			sql.append(" AND isOwner = '0'");
		}
		Connection con = null;
		PreparedStatement st = null;
		ResultSet rs = null;
		int count = 0;
		try {
			con = JDBCUtil.getConnection(Thread.currentThread().getStackTrace()[1].getMethodName());
			st = con.prepareStatement(JDBCUtil.replaceCastMysql(sql.toString()));
			rs = st.executeQuery();
			if (rs.next()) {
				count = rs.getInt("cuantos");
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			setFinally(con, st, rs);
		}
		return count;
	}

	public static DataUserWorkFlowForm loadDataOwnerFlexFlow(int idDocument, String idUser) {
		StringBuilder sql = new StringBuilder(100);
		sql.append("SELECT w.idWorkFlow,w.type,w.dateExpire,uw.idUser,uw.idFlexWF,d.nameDocument,uw.result");
		sql.append("  FROM flexworkflow w,documents d,user_flexworkflows uw");
		sql.append(" WHERE d.numGen = w.idDocument");
		sql.append("   AND w.idWorkFlow = uw.idWorkFlow");
		sql.append("   AND d.numGen = ?");
		// sql.append(" AND w.owner = ?");
		sql.append("   AND w.statu = '").append(Constants.permission).append("' ");
		Connection con = null;
		PreparedStatement st = null;
		CachedRowSet rs = null;
		DataUserWorkFlowForm data = new DataUserWorkFlowForm();
		boolean exist = false;
		ArrayList parametros = new ArrayList();
		try {
			parametros.add(new Integer(idDocument));
			// parametros.add(new Integer(idUser));
			rs = JDBCUtil.executeQuery(sql, parametros, Thread.currentThread().getStackTrace()[1].getMethodName());
			if (rs.next()) {
				data.setIdWorkFlow(rs.getInt("idWorkFlow"));
				;
				data.setNameDocument(rs.getString("nameDocument"));
				data.setIdUser(rs.getString("idUser"));
				data.setTypeWF(rs.getInt("type"));
				data.setRow(rs.getString("idFlexWF"));

				java.sql.Date date = JDBCUtil.convertToJavaSqlDate(rs.getString("dateExpire"));
				if (date != null) {
					data.setDateExpire(ToolsHTML.sdfShow.format(date));
				} else {
					data.setDateExpire("N/A");
				}
				data.setFlexFlow(true);
				exist = true;
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			setFinally(con, st, rs);
		}
		if (exist) {
			return data;
		}
		return null;
	}

	public static DataUserWorkFlowForm loadDataOwnerWF(boolean wfExpires, String idDocument, String idUser) throws Exception {
		DataUserWorkFlowForm data = new DataUserWorkFlowForm();
		StringBuilder sql = new StringBuilder(100);
		sql.append("SELECT w.idWorkFlow,w.type,w.dateExpire,uw.idUser,uw.row,d.nameDocument,uw.result");
		sql.append(" FROM workflows w,user_workflows uw,documents d WHERE w.idWorkFlow = uw.idWorkFlow");
		sql.append(" AND uw.idUser = '").append(idUser).append("'");
		sql.append(" AND d.numGen = w.idDocument");
		sql.append(" AND w.statu NOT IN ('").append(canceled).append("','").append(closed).append("','").append(inQueued).append("')");
		String dateSystem = ToolsHTML.sdfShowConvert1.format(new Date());
		if (!wfExpires) {
			switch (Constants.MANEJADOR_ACTUAL) {
			case Constants.MANEJADOR_MSSQL:
				sql.append(" AND (dateExpire >= CONVERT(datetime,'").append(dateSystem).append("',120) OR dateExpire IS NULL) ");
				break;
			case Constants.MANEJADOR_POSTGRES:
				sql.append(" AND (dateExpire >= CAST('").append(dateSystem).append("' as timestamp) OR dateExpire IS NULL) ");
				break;
			case Constants.MANEJADOR_MYSQL:
				sql.append(" AND (dateExpire >= TIMESTAMP('").append(dateSystem).append("') OR dateExpire IS NULL) ");
				break;
			}

		} else {
			switch (Constants.MANEJADOR_ACTUAL) {
			case Constants.MANEJADOR_MSSQL:
				sql.append(" AND dateExpire < CONVERT(datetime,'").append(dateSystem).append("',120)");
				break;
			case Constants.MANEJADOR_POSTGRES:
				sql.append(" AND dateExpire < CAST('").append(dateSystem).append("' as timestamp) ");
				break;
			case Constants.MANEJADOR_MYSQL:
				sql.append(" AND dateExpire < TIMESTAMP('").append(dateSystem).append("') ");
				break;
			}
		}
		sql.append(" AND isOwner = '1'");
		sql.append(" AND w.idDocument = ").append(idDocument);
		sql.append(" AND uw.result = '").append(wfuPending).append("' ");
		log.debug("[loadDataOwnerWF] " + sql.toString());
		Connection con = null;
		PreparedStatement st = null;
		CachedRowSet rs = null;
		boolean exist = false;
		try {
			rs = JDBCUtil.executeQuery(sql, Thread.currentThread().getStackTrace()[1].getMethodName());
			while (rs.next()) {
				data.setIdWorkFlow(rs.getInt("idWorkFlow"));

				data.setNameDocument(rs.getString("nameDocument"));
				data.setIdUser(rs.getString("idUser"));
				data.setTypeWF(ToolsHTML.parseByte(rs.getString("type"), (byte) 0));
				data.setRow(rs.getString("row"));
				// String result = rs.getString("result");
				// boolean isResponse = false;
				// if
				// (result.equalsIgnoreCase(wfuAcepted)||result.equalsIgnoreCase(wfuCanceled))
				// {
				// isResponse = true;
				// }
				// data.setResponse(isResponse);
				java.sql.Date date = JDBCUtil.convertToJavaSqlDate(rs.getString("dateExpire"));
				if (date != null) {
					data.setDateExpire(ToolsHTML.sdfShow.format(date));
				} else {
					data.setDateExpire("N/A");
				}
				data.setFlexFlow(false);
				exist = true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			setFinally(con, st, rs);
		}
		if (exist) {
			return data;
		}
		return null;
	}

	public synchronized static Collection<DataUserWorkFlowForm> getAllDataOwnerWF(boolean wfExpires, String idUser, ResourceBundle rb, boolean searchAsAdmin)
			throws Exception {
		log.debug("[getAllDataOwnerWF]");
		StringBuilder sql = new StringBuilder(100);
		sql.append("SELECT w.idWorkFlow,w.type,w.dateExpire,uw.idUser,uw.row,d.nameDocument,uw.result,vd.MayorVer,");
		sql.append("vd.MinorVer,d.type as typedoc,d.number,d.prefix,");
		sql.append(" p.idperson, p.nombres, p.apellidos, p.email, ");
		sql.append(" askingPerson.idperson AS akIdPerson, askingPerson.nombres AS akNombres, askingPerson.apellidos AS akApellidos, askingPerson.email AS akEmail");
		sql.append(" FROM person p, person askingPerson, workflows w,user_workflows uw,documents d,versiondoc vd WHERE w.idWorkFlow = uw.idWorkFlow");
		sql.append(" AND w.idVersion = vd.numVer");
		sql.append(" AND d.numGen = w.idDocument");
		sql.append(" AND p.nameuser = uw.idUser");
		sql.append(" AND askingPerson.nameuser=w.owner");

		if (!searchAsAdmin) {
			// los usuarios que no sean del tipo administrador, solo podran ver
			// archivos y flujos
			// de los cuales ellos sean los propietarios
			sql.append(" AND uw.idUser = '").append(idUser).append("'");
			sql.append(" AND isOwner = '1'");
		}
		// quitamos pending porque ya es tomado en cuenta en un query anterior
		// para el modulo de principal
		sql.append(" AND w.statu NOT IN ('").append(pending).append("','").append(canceled).append("','").append(closed).append("','").append(expires)
				.append("')");
		String dateSystem = ToolsHTML.sdfShowConvert1.format(new Date());
		if (!wfExpires) {
			switch (Constants.MANEJADOR_ACTUAL) {
			case Constants.MANEJADOR_MSSQL:
				sql.append(" AND (dateExpire >= CONVERT(datetime,'").append(dateSystem).append("',120) OR dateExpire IS NULL) ");
				break;
			case Constants.MANEJADOR_POSTGRES:
				sql.append(" AND (dateExpire >= CAST('").append(dateSystem).append("' as timestamp) OR dateExpire IS NULL) ");
				break;
			case Constants.MANEJADOR_MYSQL:
				sql.append(" AND (dateExpire >= TIMESTAMP('").append(dateSystem).append("') OR dateExpire IS NULL) ");
				break;
			}

		} else {
			switch (Constants.MANEJADOR_ACTUAL) {
			case Constants.MANEJADOR_MSSQL:
				sql.append(" AND dateExpire < CONVERT(datetime,'").append(dateSystem).append("',120)");
				break;
			case Constants.MANEJADOR_POSTGRES:
				sql.append(" AND dateExpire < CAST('").append(dateSystem).append("' as timestamp) ");
				break;
			case Constants.MANEJADOR_MYSQL:
				sql.append(" AND dateExpire < TIMESTAMP('").append(dateSystem).append("') ");
				break;
			}
		}
		sql.append(" AND uw.result = '").append(wfuPending).append("'");
		sql.append(" AND uw.statu = '").append(wfuPending).append("'");
		sql.append(" AND uw.result not in (select uw2.result from  user_workflows uw2 where uw2.idWorkFlow=uw.idWorkFlow ");
		sql.append(" AND uw2.row<>uw.row and uw2.isowner<>'1' and uw2.result='").append(wfuPending).append("')");
		log.debug("[getAllDataOwnerWF]" + sql.toString());
		Connection con = null;
		PreparedStatement st = null;
		ResultSet rs = null;
		Vector result = new Vector();
		try {
			con = JDBCUtil.getConnection(Thread.currentThread().getStackTrace()[1].getMethodName());
			st = con.prepareStatement(JDBCUtil.replaceCastMysql(sql.toString()));
			rs = st.executeQuery();
			while (rs.next()) {
				DataUserWorkFlowForm data = new DataUserWorkFlowForm();
				data.setIdWorkFlow(rs.getInt("idWorkFlow"));
				String nameDoc = rs.getString("nameDocument");
				data.setNameDocument(nameDoc);
				data.setIdUser(rs.getString("idUser"));
				data.setTypeWF(rs.getInt("type"));
				data.setRow(rs.getString("row"));
				data.setTypeDOC(rs.getString("typedoc") != null ? Integer.parseInt(rs.getString("typedoc")) : 0);
				data.setOwner(true);
				log.debug("Search Name workflow '" + data.getTypeWF() + "'");
				data.setNameWorkFlow(rb.getString("wf.type" + data.getTypeWF()));
				java.sql.Timestamp date = rs.getTimestamp("dateExpire");
				if (date != null) {
					data.setDateExpire(ToolsHTML.formatDateShow(ToolsHTML.sdfShowConvert.format(date), true));
				} else {
					data.setDateExpire("N/A");
				}
				String minorVer = rs.getString("MinorVer");
				if (ToolsHTML.isEmptyOrNull(minorVer)) {
					minorVer = "";
				}
				String mayorVer = rs.getString("MayorVer");
				if (ToolsHTML.isEmptyOrNull(mayorVer)) {
					mayorVer = "";
				}
				String version = mayorVer.trim() + "." + minorVer.trim();
				data.setIdVersion(version);
				data.setNumber(rs.getString("number") != null ? rs.getString("number") : "");
				data.setPrefix(rs.getString("prefix") != null ? rs.getString("prefix") : "");

				if (ToolsHTML.isEmptyOrNull(data.getNumber())) {
					data.setNumber("");
				}
				if (ToolsHTML.isEmptyOrNull(data.getPrefix())) {
					data.setPrefix("");
				}

				Person p = new Person(rs.getInt("idperson"), rs.getString("nombres"), rs.getString("apellidos"), rs.getString("email"));
				data.setPersonBean(p);

				Person askingPerson = new Person(rs.getInt("akIdPerson"), rs.getString("akNombres"), rs.getString("akApellidos"), rs.getString("akEmail"));
				data.setFlowRequieredByPersonBean(askingPerson);

				result.add(data);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			setFinally(con, st, rs);
		}
		return result;
	}

	public static Collection getHistoryDocument(String idDocument) throws Exception {
		// StringBuilder sql = new
		// StringBuilder("SELECT cwf.idMovement,cwf.dateMovement,cwf.typeMovement,hwf.idHistory,d.nameDocument");
		// sql.append(",hwf.IdWorkFlow FROM historywf hwf,documents d,changeswf cwf");
		// sql.append(" WHERE d.numGen = hwf.idDocument AND hwf.idWorkFlow = cwf.idWorkFlow");
		// sql.append(" AND hwf.idDocument = ").append(idDocument);
		// sql.append(" ORDER BY idMovement,hwf.idWorkFlow");
		StringBuilder sql = new StringBuilder(
				// ydavila ydavila Ticket 001-00-003023
				// "SELECT h.* FROM workflows w,wfhistory h");
				"SELECT w.idworkflow, w.type, w.subtype, h.idhistory, h.nameuser, h.datechange, h.type as typew FROM workflows w,wfhistory h");
		sql.append(" WHERE h.idWorkFlow = w.idWorkFlow");
		sql.append(" AND w.idDocument = ").append(idDocument);
		ArrayList resp = new ArrayList();
		Vector datos = JDBCUtil.doQueryVector(sql.toString(),Thread.currentThread().getStackTrace()[1].getMethodName());
		for (int row = 0; row < datos.size(); row++) {
			Properties properties = (Properties) datos.elementAt(row);
			DataWorkFlowForm dataWF = new DataWorkFlowForm();
			// dataWF.setIdMovement(Integer.parseInt(properties.getProperty("idMovement")));
			// dataWF.setNameDocument(properties.getProperty("nameDocument"));
			dataWF.setNumDocument(Integer.parseInt(idDocument));
			dataWF.setDateBegin(ToolsHTML.formatDateShow(properties.getProperty("dateChange"), true));
			dataWF.setTypeMovement(Integer.parseInt(properties.getProperty(
					// ydavila ydavila Ticket 001-00-003023
					// "type").trim()));
					"typew").trim()));
			// ydavila Ticket 001-00-003023
			Locale defaultLocale = new Locale(DesigeConf.getProperty("language.Default"), DesigeConf.getProperty("country.Default"));
			ResourceBundle rb = ResourceBundle.getBundle("LoginBundle", defaultLocale);
			System.out.println("dataWF= " + dataWF);
			dataWF.setTypeWF(properties.getProperty("type"));
			dataWF.setSubtypeWF(properties.getProperty("subtype"));
			/*
			 * if (properties.getProperty("subtype").equals("3")) { dataWF.setsubtypeWFdesc=rb.getString("wf.titleWF3"); }else if
			 * (properties.getProperty("subtype").equals("4")) { dataWF.setsubtypeWFdesc=rb.getString("wf.titleWF4"); }
			 */
			dataWF.setIdWorkFlow(properties.getProperty("idWorkFlow"));
			dataWF.setIdHistory(properties.getProperty("idHistory"));
			dataWF.setFlexFlow(false);
			resp.add(dataWF);
		}
		return resp;
	}

	public static Collection getHistoryFlexDoc(String idDocument, String msgClosed, String msgCompleted, String msgExpired) throws Exception {
		HashMap<String, String> indice = new HashMap<String, String>();

		StringBuilder sql = new StringBuilder();
		sql.append("SELECT DISTINCT h.idHistory,h.idWorkFlow,h.nameUser,h.dateChange,h.type,a.Act_Name");
		sql.append(" FROM flexflowhistory h,flexworkflow w,activity a");
		sql.append(" WHERE w.IDFlexFlow = h.idWorkFlow");
		sql.append("   AND cast(h.type as int) = a.Act_Number");
		sql.append("   AND w.idDocument = ").append(idDocument);
		sql.append(" UNION ");
		sql.append("SELECT DISTINCT h.idHistory,h.idWorkFlow,h.nameUser,h.dateChange,h.type,'4'");
		sql.append(" FROM flexflowhistory h,flexworkflow w ");
		sql.append(" WHERE w.IDFlexFlow = h.idWorkFlow ");
		sql.append(" AND h.type in ('").append(canceled).append("','").append(completed).append("','").append(expires).append("') ");
		sql.append("   AND w.idDocument = ").append(idDocument);

		log.debug("[getHistoryFlexDoc] " + sql);
		// //System.out.println("[getHistoryFlexDoc] " + sql);
		ArrayList resp = new ArrayList();
		Vector datos = JDBCUtil.doQueryVector(sql.toString(),Thread.currentThread().getStackTrace()[1].getMethodName());
		for (int row = 0; row < datos.size(); row++) {
			Properties properties = (Properties) datos.elementAt(row);
			DataWorkFlowForm dataWF = new DataWorkFlowForm();
			String statu = properties.getProperty("type").trim();
			dataWF.setFlexFlow(true);
			dataWF.setNumDocument(Integer.parseInt(idDocument));
			dataWF.setDateBegin(ToolsHTML.formatDateShow(properties.getProperty("dateChange"), true));
			if (ToolsHTML.isNumeric(statu))
				dataWF.setTypeMovement(Integer.parseInt(statu));
			else
				dataWF.setTypeMovement(0);
			if (dataWF.getTypeMovement() == 4) {
				dataWF.setNameWF(msgClosed);
			} else if (dataWF.getTypeMovement() == 30) {
				dataWF.setNameWF(msgCompleted);
			} else if (dataWF.getTypeMovement() == 2 && ToolsHTML.isEmptyOrNull(properties.getProperty("nameUser"))) {
				dataWF.setNameWF(msgExpired);
			} else {
				dataWF.setNameWF(properties.getProperty("Act_Name"));
			}
			dataWF.setIdWorkFlow(properties.getProperty("idWorkFlow"));
			dataWF.setIdHistory(properties.getProperty("idHistory"));

			if (expires.equals(statu) && properties.getProperty("Act_Name") != null && properties.getProperty("Act_Name").equals("4")) {
				dataWF = null;
			} else if (indice.containsKey(properties.getProperty("idHistory"))) {
				dataWF = null;
			} else {
				indice.put(properties.getProperty("idHistory"), properties.getProperty("idHistory"));
				resp.add(dataWF);
			}
		}
		return resp;
	}

	// public static String getRowWFPending(String idUser) throws Exception{
	// // StringBuilder sql = new
	// StringBuilder("SELECT row FROM user_workflows uw");
	// // sql.append(" WHERE uw.idUser = '").append(idUser).append("'");
	// //
	// sql.append(" AND uw.row NOT IN (SELECT uwi.row FROM user_workflows uwi,changeswf cwf");
	// // sql.append(" WHERE cwf.idRow = uwi.row AND typeMovement = 1");
	// // sql.append(" AND uwi.idUser='").append(idUser).append("')");
	// // sql.append(" AND row NOT IN (SELECT uwi.idDepenOn");
	// //
	// sql.append(" FROM user_workflows uwi,changeswf cwf WHERE cwf.idRow = uwi.row AND typeMovement = 1");
	// // sql.append(" AND uwi.idUser='").append(idUser).append("')");
	// // sql.append(" AND idDepenOn <> 0 ORDER BY `Row`");
	// StringBuilder sql = new
	// StringBuilder("SELECT row FROM user_workflows uw");
	// sql.append(" WHERE uw.idUser = '").append(idUser).append("'");
	// sql.append(" AND uw.idWorkFlow NOT IN (SELECT IdWorkFlow");
	// sql.append(" FROM user_workflows WHERE comments IS NOT NULL AND idUser = '");
	// sql.append(idUser).append("') AND uw.statu = 1 ORDER BY `Row`");
	// Vector datos = JDBCUtil.doQueryVector(sql.toString(),Thread.currentThread().getStackTrace()[1].getMethodName());
	// if (datos.size() > 0){
	// StringBuilder resp = new StringBuilder("(");
	// for (int row = 0; row < datos.size(); row++) {
	// Properties properties = (Properties) datos.elementAt(row);
	// resp.append("").append(properties.getProperty("row")).append(",");
	// }
	// resp.replace(resp.length()-1,resp.length(),")");
	// return resp.toString();
	// } else {
	// return null;
	// }
	// }

	public static Collection getCommentsUser(DataWorkFlowForm dataWF, boolean all) throws Exception {
		StringBuilder sql = new StringBuilder(100);
		sql.append("SELECT uw.row,uw.idUser,comments,dateReplied,");
		switch (Constants.MANEJADOR_ACTUAL) {
		case Constants.MANEJADOR_MSSQL:
			sql.append("(p.Apellidos + ' ' + p.Nombres) AS nombre,");
			break;
		case Constants.MANEJADOR_POSTGRES:
			sql.append("(p.Apellidos || ' ' || p.Nombres) AS nombre,");
			break;
		case Constants.MANEJADOR_MYSQL:
			sql.append("CONCAT(p.Apellidos , ' ' , p.Nombres) AS nombre,");
			break;
		}
		sql.append("c.cargo");
		sql.append(" FROM user_workflows uw,person p,tbl_cargo c,tbl_area a");
		sql.append(" WHERE uw.idUser = p.nameUser");
		sql.append(" AND c.idcargo=cast(p.cargo as int) and c.idarea=a.idarea");
		sql.append(" AND uw.idWorkFlow = ").append(dataWF.getIdWorkFlow());
		/*
		 * ydavila ticket 001-00-003021 - se coloca en comentario la siguiente instrucci�n para que en el hist�rico de flujos de revisi�n y aprobaci�n se
		 * permita ver las respuestas de los usuarios que han participado en los mismos aunque hayan sido eliminados del sistema
		 */
		// sql.append("  AND p.accountActive = '").append(Constants.permission).append("'");

		sql.append(" AND comments IS NOT NULL");
		if (!all) {
			sql.append(" AND isOwner = '0'");
		}
		sql.append(" AND uw.statu IN ('").append(wfuAcepted).append("','").append(wfuCanceled);
		sql.append("','").append(rechazado).append("','").append(ownercancelcloseflow);
		sql.append("') ORDER BY dateReplied");
		Vector datos = JDBCUtil.doQueryVector(sql.toString(),Thread.currentThread().getStackTrace()[1].getMethodName());
		ArrayList resp = new ArrayList();
		String comments = null;
		for (int row = 0; row < datos.size(); row++) {
			Properties properties = (Properties) datos.elementAt(row);
			comments = properties.getProperty("comments");
			if (ToolsHTML.isEmptyOrNull(comments)) {
				continue;
			}
			String id = properties.getProperty("idUser") + "_" + properties.getProperty("row");
			Search3 bean = new Search3(id, properties.getProperty("nombre"), null, comments);
			bean.setAditionalInfo(properties.getProperty("cargo"));
			resp.add(bean);
		}
		return resp;
	}

	public static Collection getCommentsFlexFlowUser(DataWorkFlowForm dataWF) throws Exception {
		StringBuilder sql = new StringBuilder(100);
		sql.append("SELECT uw.idFlexWF,uw.idUser,comments,dateReplied,");
		switch (Constants.MANEJADOR_ACTUAL) {
		case Constants.MANEJADOR_MSSQL:
			sql.append("(p.Apellidos + ' ' + p.Nombres) AS nombre,");
			break;
		case Constants.MANEJADOR_POSTGRES:
			sql.append("(p.Apellidos || ' ' || p.Nombres) AS nombre,");
			break;
		case Constants.MANEJADOR_MYSQL:
			sql.append("CONCAT(p.Apellidos , ' ' , p.Nombres) AS nombre,");
			break;
		}
		sql.append("c.cargo");
		sql.append(" FROM user_flexworkflows uw,person p,tbl_cargo c");
		sql.append(" WHERE uw.idUser = p.nameUser AND cast(p.cargo as int) = c.idCargo");
		// sql.append(" AND uw.idWorkFlow = ").append(dataWF.getIdWorkFlow());
		sql.append(" AND uw.idWorkFlow IN (SELECT idWorkFlow FROM flexworkflow WHERE IDFlexFlow = ").append(dataWF.getIdFlexFlow());
		// AND uw.idWorkFlow IN (SELECT idWorkFlow FROM flexworkflow WHERE
		// IDFlexFlow = 2)
		sql.append(") AND p.accountActive = '").append(Constants.permission).append("'");
		sql.append(" AND comments IS NOT NULL");
		sql.append(" AND uw.statu IN ('").append(wfuAcepted).append("','").append(wfuCanceled).append("','").append(wfuCompleted).append("','")
				.append(wfuAnnulled); // NOTA: se incluyeron los
										// comentarios anulados
		sql.append("','").append(rechazado).append("','").append(wfuReInit);
		sql.append("') ORDER BY dateReplied");
		log.debug("[getCommentsFlexFlowUser] " + sql);
		Vector datos = JDBCUtil.doQueryVector(sql.toString(),Thread.currentThread().getStackTrace()[1].getMethodName());
		ArrayList resp = new ArrayList();
		for (int row = 0; row < datos.size(); row++) {
			Properties properties = (Properties) datos.elementAt(row);
			String id = properties.getProperty("idUser") + "_" + properties.getProperty("idFlexWF");
			Search3 bean = new Search3(id, properties.getProperty("nombre"), null, properties.getProperty("comments"));
			bean.setAditionalInfo(properties.getProperty("cargo"));
			resp.add(bean);
		}
		return resp;
	}

	public static void loadDataWorkFlow(String table, boolean history, DataWorkFlowForm dataWF) throws Exception {
		// 11 DE JULIO 2005 INICIO
		// Se agrego hwf.eliminar
		boolean isSubType = table.equals("workflows");
		
		StringBuilder sql = new StringBuilder(
				"SELECT vd.MayorVer,vd.MinorVer, hwf.idWorkFlow,hwf.idDocument,hwf.owner,secuential,conditional,notified,expire,hwf.dateExpire,hwf.dateCreation,");
		// ydavila Ticket 001-00-003023
		// sql.append(" hwf.dateCompleted,hwf.statu,hwf.result,hwf.type,hwf.comments,hwf.eliminar,d.nameDocument,");
		sql.append(" hwf.dateCompleted,hwf.statu,hwf.result,hwf.type,");
		sql.append(isSubType?"hwf.subtype,":"").append("hwf.comments,hwf.eliminar,hwf.cambiar,d.nameDocument,");
		switch (Constants.MANEJADOR_ACTUAL) {
		case Constants.MANEJADOR_MSSQL:
			sql.append("(p.Apellidos + ' ' + p.Nombres) AS nombre,");
			break;
		case Constants.MANEJADOR_POSTGRES:
			sql.append("(p.Apellidos || ' ' || p.Nombres) AS nombre,");
			break;
		case Constants.MANEJADOR_MYSQL:
			sql.append("CONCAT(p.Apellidos , ' ' , p.Nombres) AS nombre,");
			break;
		}
		sql.append("c.cargo,d.numGen,numVer");
		sql.append(" ,d.statu AS statuDoc,hwf.dateCompleted,idVersion,hwf.idLastVersion,d.nameFile,");
		sql.append(" d.type as typeDoc,d.IdNode,d.prefix,d.number,hwf.copy ");
		sql.append(" FROM documents d,person p,tbl_cargo c,tbl_area a,").append(table).append(" hwf,versiondoc vd");
		sql.append(" WHERE d.numGen = hwf.idDocument AND hwf.owner = p.nameUser");
		sql.append(" AND c.idcargo=cast(p.cargo as int) and c.idarea=a.idarea");
		sql.append(" AND vd.numDoc = d.NumGen AND vd.numVer = hwf.idVersion");
		sql.append(" AND hwf.idWorkFlow = ").append(dataWF.getIdWorkFlow());
		log.debug("[loadDataWorkFlow] = " + sql.toString());
		Properties prop = JDBCUtil.doQueryOneRow(sql.toString(),Thread.currentThread().getStackTrace()[1].getMethodName());
		if (prop.getProperty("isEmpty").equalsIgnoreCase("false")) {
			dataWF.setIdnode(Integer.parseInt(prop.getProperty("IdNode")));
			dataWF.setConditional(prop.getProperty("conditional"));
			dataWF.setCopy(prop.getProperty("copy"));
			dataWF.setSequential(prop.getProperty("secuential"));
			String eliminar = prop.getProperty("eliminar");
			dataWF.setEliminar(Constants.notPermission);
			if (ToolsHTML.isNumeric(eliminar)) {
				dataWF.setEliminar(Byte.parseByte(eliminar));
			}
			// ydavila Ticket 001-00-003023
			String cambiar = prop.getProperty("cambiar");
			if (cambiar == "1") {// || eliminar=="1") {
				dataWF.setConditional(prop.getProperty("true"));
				dataWF.setSequential(prop.getProperty("false"));
			}
			dataWF.setCambiar(Byte.parseByte(ToolsHTML.isEmptyOrNull(prop.getProperty("cambiar"),"0")));
			if (ToolsHTML.isNumeric(cambiar)) {
				dataWF.setCambiar(Byte.parseByte(cambiar));
			}
			dataWF.setNotified(prop.getProperty("notified"));
			dataWF.setDateBegin(ToolsHTML.formatDateShow(prop.getProperty("dateCreation"), true));
			String dateExpireWF = prop.getProperty("dateExpire");
			if (!ToolsHTML.isEmptyOrNull(dateExpireWF)) {
				dataWF.setDateExpire(ToolsHTML.formatDateShow(dateExpireWF, true));
			}
			String dateCompleted = prop.getProperty("dateCompleted");
			if (!ToolsHTML.isEmptyOrNull(dateCompleted)) {
				String dateEndWF = ToolsHTML.formatDateShow(dateCompleted, true);

				// String dateSystem = ToolsHTML.sdfShowConvert1.format(new
				// java.util.Date());
				// String dateExpireWF =
				// ToolsHTML.sdf.format(ToolsHTML.sdf.parse(prop.getProperty("dateCreation")));

				dataWF.setDateEnd(dateEndWF);
			}
			dataWF.setNumber(prop.getProperty("number") != null ? prop.getProperty("number") : "");
			dataWF.setPrefix(prop.getProperty("prefix") != null ? prop.getProperty("prefix") : "");

			dataWF.setNameDocument(prop.getProperty("nameDocument"));
			dataWF.setRequest(prop.getProperty("nombre"));
			dataWF.setTypeWF(prop.getProperty("type"));
			// ydavila Ticket 001-00-003023
			dataWF.setSubtypeWF(isSubType?prop.getProperty("subtype"):"");
			dataWF.setComments(prop.getProperty("comments"));
			dataWF.setStatu(prop.getProperty("statu").trim());
			dataWF.setResult(prop.getProperty("result").trim());
			dataWF.setNumDocument(Integer.parseInt(prop.getProperty("numGen")));
			// dataWF.setVersion(Integer.parseInt(prop.getProperty("numVer")));
			dataWF.setVersion(Integer.parseInt(prop.getProperty("idLastVersion")));

			dataWF.setStatuDoc(prop.getProperty("statuDoc").trim());
			dataWF.setNumVersion(Integer.parseInt(prop.getProperty("idVersion")));
			dataWF.setNameFile(prop.getProperty("nameFile"));
			dataWF.setTypeDoc(prop.getProperty("typeDoc"));
			dataWF.setCharge(prop.getProperty("cargo"));
			// idVersion
			String minorVer = prop.getProperty("minorVer") != null ? prop.getProperty("minorVer").trim() : "";
			String mayorVer = prop.getProperty("MayorVer") != null ? prop.getProperty("MayorVer").trim() : "";
			String version = mayorVer.trim() + "." + minorVer.trim();
			dataWF.setIdVersion(version.trim());
		}
	}

	// ydavila
	public static void loadDataWorkFlowFTP(String table, boolean history, DataWorkFlowForm dataWF) throws Exception {
		// 11 DE JULIO 2005 INICIO
		// Se agrego hwf.eliminar
		StringBuilder sql = new StringBuilder(
				"SELECT vd.MayorVer,vd.MinorVer, hwf.idWorkFlow,hwf.idDocument,hwf.owner,secuential,conditional,notified,expire,hwf.dateExpire,hwf.dateCreation,");
		sql.append(" hwf.dateCompleted,hwf.statu,hwf.result,hwf.type,hwf.comments,hwf.eliminar,d.nameDocument,");
		switch (Constants.MANEJADOR_ACTUAL) {
		case Constants.MANEJADOR_MSSQL:
			sql.append("(p.Apellidos + ' ' + p.Nombres) AS nombre,");
			break;
		case Constants.MANEJADOR_POSTGRES:
			sql.append("(p.Apellidos || ' ' || p.Nombres) AS nombre,");
			break;
		case Constants.MANEJADOR_MYSQL:
			sql.append("CONCAT(p.Apellidos,' ',p.Nombres) AS nombre,");
			break;
		}
		sql.append("c.cargo,d.numGen,numVer");
		sql.append(" ,d.statu AS statuDoc,hwf.dateCompleted,idVersion,hwf.idLastVersion,d.nameFile,");
		sql.append(" d.type as typeDoc,d.IdNode,d.prefix,d.number,hwf.copy ");
		sql.append(" FROM documents d,person p,tbl_cargo c,tbl_area a,").append(table).append(" hwf,versiondoc vd");
		sql.append(" WHERE d.numGen = hwf.idDocument AND hwf.owner = p.nameUser");
		sql.append(" AND c.idcargo=cast(p.cargo as int) and c.idarea=a.idarea");
		sql.append(" AND vd.numDoc = d.NumGen AND vd.numVer = hwf.idVersion");
		sql.append(" AND hwf.idWorkFlow = ").append(dataWF.getIdWorkFlow());
		log.debug("[loadDataWorkFlow] = " + sql.toString());
		Properties prop = JDBCUtil.doQueryOneRow(sql.toString(),Thread.currentThread().getStackTrace()[1].getMethodName());
		if (prop.getProperty("isEmpty").equalsIgnoreCase("false")) {
			dataWF.setIdnode(Integer.parseInt(prop.getProperty("IdNode")));
			dataWF.setConditional(prop.getProperty("conditional"));
			dataWF.setCopy(prop.getProperty("copy"));
			dataWF.setSequential(prop.getProperty("secuential"));
			dataWF.setEliminar(Constants.notPermission);
			dataWF.setNotified(prop.getProperty("notified"));
			dataWF.setDateBegin(ToolsHTML.formatDateShow(prop.getProperty("dateCreation"), true));
			String dateExpireWF = prop.getProperty("dateExpire");
			if (!ToolsHTML.isEmptyOrNull(dateExpireWF)) {
				dataWF.setDateExpire(ToolsHTML.formatDateShow(dateExpireWF, true));
			}
			String dateCompleted = prop.getProperty("dateCompleted");
			if (!ToolsHTML.isEmptyOrNull(dateCompleted)) {
				String dateEndWF = ToolsHTML.formatDateShow(dateCompleted, true);

				// String dateSystem = ToolsHTML.sdfShowConvert1.format(new
				// java.util.Date());
				// String dateExpireWF =
				// ToolsHTML.sdf.format(ToolsHTML.sdf.parse(prop.getProperty("dateCreation")));

				dataWF.setDateEnd(dateEndWF);
			}
			dataWF.setNumber(prop.getProperty("number") != null ? prop.getProperty("number") : "");
			dataWF.setPrefix(prop.getProperty("prefix") != null ? prop.getProperty("prefix") : "");

			dataWF.setNameDocument(prop.getProperty("nameDocument"));
			dataWF.setRequest(prop.getProperty("nombre"));
			dataWF.setTypeWF(prop.getProperty("type"));
			dataWF.setComments(prop.getProperty("comments"));
			dataWF.setStatu(prop.getProperty("statu").trim());
			dataWF.setResult(prop.getProperty("result").trim());
			dataWF.setNumDocument(Integer.parseInt(prop.getProperty("numGen")));
			// dataWF.setVersion(Integer.parseInt(prop.getProperty("numVer")));
			dataWF.setVersion(Integer.parseInt(prop.getProperty("idLastVersion")));

			dataWF.setStatuDoc(prop.getProperty("statuDoc").trim());
			dataWF.setNumVersion(Integer.parseInt(prop.getProperty("idVersion")));
			dataWF.setNameFile(prop.getProperty("nameFile"));
			dataWF.setTypeDoc(prop.getProperty("typeDoc"));
			dataWF.setCharge(prop.getProperty("cargo"));
			// idVersion
			String minorVer = prop.getProperty("minorVer") != null ? prop.getProperty("minorVer").trim() : "";
			String mayorVer = prop.getProperty("MayorVer") != null ? prop.getProperty("MayorVer").trim() : "";
			String version = mayorVer.trim() + "." + minorVer.trim();
			dataWF.setIdVersion(version.trim());
		}
	}

	public static Collection loadDataFlexFlow(String table, String idWF, HttpServletRequest request) throws Exception {
		Vector result = new Vector();
		StringBuilder sql = new StringBuilder(
				"SELECT vd.MayorVer,vd.MinorVer, hwf.idWorkFlow,hwf.idDocument,hwf.owner,secuential,conditional,notified,expire,hwf.dateExpire,hwf.dateCreation,");
		sql.append(" hwf.dateCompleted,hwf.statu,hwf.result,hwf.type,hwf.comments,hwf.eliminar,d.nameDocument,");
		switch (Constants.MANEJADOR_ACTUAL) {
		case Constants.MANEJADOR_MSSQL:
			sql.append("(p.Apellidos + ' ' + p.Nombres) AS nombre,");
			break;
		case Constants.MANEJADOR_POSTGRES:
			sql.append("(p.Apellidos || ' ' || p.Nombres) AS nombre,");
			break;
		case Constants.MANEJADOR_MYSQL:
			sql.append("CONCAT(p.Apellidos , ' ' , p.Nombres) AS nombre,");
			break;
		}
		sql.append("c.cargo,d.numGen,numVer");
		sql.append(
				" ,d.statu AS statuDoc,hwf.dateCompleted,idVersion,hwf.idLastVersion,d.nameFile,d.type as typeDoc,d.IdNode,sa.sAct_Name FROM documents d,person p,")
				.append(table).append(" hwf,versiondoc vd");
		sql.append(" ,subactivities sa,tbl_cargo c WHERE d.numGen = hwf.idDocument AND hwf.owner = p.nameUser");
		sql.append(" AND vd.numDoc = d.NumGen AND vd.numVer = hwf.idVersion");
		sql.append(" AND cast(p.cargo as int) = c.idCargo");
		sql.append(" AND hwf.IDFlexFlow = ").append(idWF);
		sql.append(" AND hwf.Act_Number = sa.Act_Number AND hwf.type = sa.sAct_Number");
		sql.append("  ORDER BY hwf.idWorkFlow");
		log.debug("[loadDataFlexFlow] = " + sql.toString());
		// //System.out.println("loadDataFlexFlow: " + sql.toString());
		Vector datos = JDBCUtil.doQueryVector(sql.toString(),Thread.currentThread().getStackTrace()[1].getMethodName());

		for (int row = 0; row < datos.size(); row++) {
			Properties prop = (Properties) datos.elementAt(row);
			DataWorkFlowForm dataWF = new DataWorkFlowForm();
			dataWF.setIdWorkFlow(prop.getProperty("idWorkFlow"));
			dataWF.setIdnode(Integer.parseInt(prop.getProperty("IdNode")));
			dataWF.setConditional(prop.getProperty("conditional"));
			dataWF.setSequential(prop.getProperty("secuential"));
			String eliminar = prop.getProperty("eliminar");
			dataWF.setEliminar(Constants.notPermission);
			if (ToolsHTML.isNumeric(eliminar)) {
				dataWF.setEliminar(Byte.parseByte(eliminar));
			}
			String cambiar = prop.getProperty("cambiar");
			// ydavila Ticket 001-00-003023
			dataWF.setCambiar(Constants.notPermission);
			if (ToolsHTML.isNumeric(cambiar)) {
				dataWF.setCambiar(Byte.parseByte(cambiar));
			}
			dataWF.setNotified(prop.getProperty("notified"));
			dataWF.setDateBegin(ToolsHTML.formatDateShow(prop.getProperty("dateCreation"), true));

			String dateExpireWF = prop.getProperty("dateExpire");
			if (!ToolsHTML.isEmptyOrNull(dateExpireWF)) {
				dataWF.setDateExpire(ToolsHTML.formatDateShow(dateExpireWF, true));
			}
			String dateCompleted = prop.getProperty("dateCompleted");
			if (!ToolsHTML.isEmptyOrNull(dateCompleted)) {
				String dateEndWF = ToolsHTML.formatDateShow(dateCompleted, true);
				dataWF.setDateEnd(dateEndWF);
			}
			dataWF.setNameDocument(prop.getProperty("nameDocument"));
			dataWF.setRequest(prop.getProperty("nombre"));
			dataWF.setTypeWF(prop.getProperty("type"));
			dataWF.setComments(ToolsHTML.updateURLVerDocumento(prop.getProperty("comments"), request, null));
			dataWF.setStatu(prop.getProperty("statu").trim());
			dataWF.setResult(prop.getProperty("result").trim());
			dataWF.setNumDocument(Integer.parseInt(prop.getProperty("numGen")));
			dataWF.setVersion(Integer.parseInt(prop.getProperty("idLastVersion")));
			dataWF.setStatuDoc(prop.getProperty("statuDoc").trim());
			dataWF.setNumVersion(Integer.parseInt(prop.getProperty("idVersion")));
			dataWF.setNameFile(prop.getProperty("nameFile"));
			dataWF.setTypeDoc(prop.getProperty("typeDoc"));
			dataWF.setCharge(prop.getProperty("cargo"));
			// idVersion
			String minorVer = prop.getProperty("minorVer") != null ? prop.getProperty("minorVer").trim() : "";
			String mayorVer = prop.getProperty("MayorVer") != null ? prop.getProperty("MayorVer").trim() : "";
			String version = mayorVer.trim() + "." + minorVer.trim();
			dataWF.setIdVersion(version.trim());
			dataWF.setNameWF(prop.getProperty("sAct_Name"));
			Collection comments = getAllUserInFlexFlow(Integer.parseInt(dataWF.getIdWorkFlow()));
			dataWF.setUsers(comments);
			dataWF.setCommentsUsrs(comments);
			result.add(dataWF);
		}
		return result;
	}

	public static Collection loadDataFlexFlowReasigne(String table, String idWF) throws Exception {
		Vector result = new Vector();
		StringBuilder sql = new StringBuilder(
				"SELECT vd.MayorVer,vd.MinorVer, hwf.idWorkFlow,hwf.idDocument,hwf.owner,secuential,conditional,notified,expire,hwf.dateExpire,hwf.dateCreation,");
		sql.append(" hwf.dateCompleted,hwf.statu,hwf.result,hwf.type,hwf.comments,hwf.eliminar,d.nameDocument,");
		switch (Constants.MANEJADOR_ACTUAL) {
		case Constants.MANEJADOR_MSSQL:
			sql.append("(p.Apellidos + ' ' + p.Nombres) AS nombre,");
			break;
		case Constants.MANEJADOR_POSTGRES:
			sql.append("(p.Apellidos || ' ' || p.Nombres) AS nombre,");
			break;
		case Constants.MANEJADOR_MYSQL:
			sql.append("CONCAT(p.Apellidos , ' ' , p.Nombres) AS nombre,");
			break;
		}
		sql.append("c.cargo,d.numGen,numVer");
		sql.append(
				" ,d.statu AS statuDoc,hwf.dateCompleted,idVersion,hwf.idLastVersion,d.nameFile,d.type as typeDoc,d.IdNode,sa.sAct_Name FROM documents d,person p,")
				.append(table).append(" hwf,versiondoc vd");
		sql.append(" ,subactivities sa,tbl_cargo c WHERE d.numGen = hwf.idDocument AND hwf.owner = p.nameUser");
		sql.append(" AND vd.numDoc = d.NumGen AND vd.numVer = hwf.idVersion");
		sql.append(" AND cast(p.cargo as int) = c.idCargo");
		sql.append(" AND hwf.IDFlexFlow = ").append(idWF);
		sql.append(" AND hwf.Act_Number = sa.Act_Number AND hwf.type = sa.sAct_Number");
		sql.append("  ORDER BY hwf.idWorkFlow");
		log.debug("[loadDataFlexFlowReasigne] = " + sql.toString());
		Vector datos = JDBCUtil.doQueryVector(sql.toString(),Thread.currentThread().getStackTrace()[1].getMethodName());

		for (int row = 0; row < datos.size(); row++) {
			Properties prop = (Properties) datos.elementAt(row);
			DataWorkFlowForm dataWF = new DataWorkFlowForm();
			dataWF.setIdWorkFlow(prop.getProperty("idWorkFlow"));
			dataWF.setIdnode(Integer.parseInt(prop.getProperty("IdNode")));
			dataWF.setConditional(prop.getProperty("conditional"));
			dataWF.setSequential(prop.getProperty("secuential"));
			String eliminar = prop.getProperty("eliminar");
			dataWF.setEliminar(Constants.notPermission);
			if (ToolsHTML.isNumeric(eliminar)) {
				dataWF.setEliminar(Byte.parseByte(eliminar));
			}
			// ydavila Ticket 001-00-003023
			String cambiar = prop.getProperty("cambiar");
			dataWF.setCambiar(Constants.notPermission);
			if (ToolsHTML.isNumeric(cambiar)) {
				dataWF.setCambiar(Byte.parseByte(cambiar));
			}
			dataWF.setNotified(prop.getProperty("notified"));
			dataWF.setDateBegin(ToolsHTML.formatDateShow(prop.getProperty("dateCreation"), true));

			String dateExpireWF = prop.getProperty("dateExpire");
			if (!ToolsHTML.isEmptyOrNull(dateExpireWF)) {
				dataWF.setDateExpire(ToolsHTML.formatDateShow(dateExpireWF, true));
			}
			String dateCompleted = prop.getProperty("dateCompleted");
			if (!ToolsHTML.isEmptyOrNull(dateCompleted)) {
				String dateEndWF = ToolsHTML.formatDateShow(dateCompleted, true);
				dataWF.setDateEnd(dateEndWF);
			}
			dataWF.setNameDocument(prop.getProperty("nameDocument"));
			dataWF.setRequest(prop.getProperty("nombre"));
			dataWF.setTypeWF(prop.getProperty("type"));
			dataWF.setComments(prop.getProperty("comments"));
			dataWF.setStatu(prop.getProperty("statu").trim());
			dataWF.setResult(prop.getProperty("result").trim());
			dataWF.setNumDocument(Integer.parseInt(prop.getProperty("numGen")));
			dataWF.setVersion(Integer.parseInt(prop.getProperty("idLastVersion")));
			dataWF.setStatuDoc(prop.getProperty("statuDoc").trim());
			dataWF.setNumVersion(Integer.parseInt(prop.getProperty("idVersion")));
			dataWF.setNameFile(prop.getProperty("nameFile"));
			dataWF.setTypeDoc(prop.getProperty("typeDoc"));
			dataWF.setCharge(prop.getProperty("cargo"));
			// idVersion
			String minorVer = prop.getProperty("minorVer") != null ? prop.getProperty("minorVer").trim() : "";
			String mayorVer = prop.getProperty("MayorVer") != null ? prop.getProperty("MayorVer").trim() : "";
			String version = mayorVer.trim() + "." + minorVer.trim();
			dataWF.setIdVersion(version.trim());
			dataWF.setNameWF(prop.getProperty("sAct_Name"));
			Collection comments = getUsersInFlexFlowForReasigner(Integer.parseInt(dataWF.getIdWorkFlow()));
			dataWF.setUsers(comments);
			dataWF.setCommentsUsrs(comments);
			result.add(dataWF);
		}
		return result;
	}

	/**
	 *
	 * @param table
	 * @param dataWF
	 * @throws Exception
	 */
	public static void loadDataWorkFlow(String table, DataWorkFlowForm dataWF) throws Exception {
		StringBuilder sql = new StringBuilder(
				"SELECT vd.MayorVer,vd.MinorVer, hwf.idWorkFlow,hwf.idDocument,hwf.owner,secuential,conditional,notified,expire,hwf.dateExpire,hwf.dateCreation,");
		// ydavila Ticket 001-00-003023
		if (table != "flexworkflow") {
			sql.append(" hwf.dateCompleted,hwf.statu,hwf.result,hwf.type,hwf.comments,hwf.eliminar,hwf.cambiar,d.nameDocument,");
		} else {
			sql.append(" hwf.dateCompleted,hwf.statu,hwf.result,hwf.type,hwf.comments,hwf.eliminar,d.nameDocument,");
		}
		switch (Constants.MANEJADOR_ACTUAL) {
		case Constants.MANEJADOR_MSSQL:
			sql.append("(p.Apellidos + ' ' + p.Nombres) AS nombre,");
			break;
		case Constants.MANEJADOR_POSTGRES:
			sql.append("(p.Apellidos || ' ' || p.Nombres) AS nombre,");
			break;
		case Constants.MANEJADOR_MYSQL:
			sql.append("CONCAT(p.Apellidos , ' ' , p.Nombres) AS nombre,");
			break;
		}
		sql.append("c.cargo,d.numGen,numVer")
				.append(" ,d.statu AS statuDoc,hwf.dateCompleted,idVersion,hwf.idLastVersion,d.nameFile,d.type as typeDoc,d.IdNode")
				.append(" ,sAct.sAct_Name,hwf.Act_Number,hwf.IDFlexFlow,d.prefix,d.number ").append(" FROM documents d,person p,tbl_cargo c,").append(table)
				.append(" hwf,versiondoc vd,subactivities sAct ").append(" WHERE d.numGen = hwf.idDocument AND hwf.owner = p.nameUser")
				.append(" AND vd.numDoc = d.NumGen AND vd.numVer = hwf.idVersion").append(" AND cast(p.cargo as int) = c.idcargo ")
				.append(" AND hwf.idWorkFlow = ").append(dataWF.getIdWorkFlow()).append(" AND hwf.type = sAct.sAct_Number");
		// sql.append(" AND sAct.Act_Number = a.Act_Number");
		// sql.append(" AND hwf.statu = ").append(pending);
		// 11 DE JULIO 2005 FIN
		log.debug("[loadDataWorkFlow] = " + sql.toString());
		Properties prop = JDBCUtil.doQueryOneRow(sql.toString(),Thread.currentThread().getStackTrace()[1].getMethodName());
		if (prop.getProperty("isEmpty").equalsIgnoreCase("false")) {
			dataWF.setIdnode(Integer.parseInt(prop.getProperty("IdNode")));
			dataWF.setConditional(prop.getProperty("conditional"));
			dataWF.setSequential(prop.getProperty("secuential"));
			String eliminar = prop.getProperty("eliminar");
			dataWF.setEliminar(Constants.notPermission);
			if (ToolsHTML.isNumeric(eliminar)) {
				dataWF.setEliminar(Byte.parseByte(eliminar));
			}
			// ydavila
			if (table != "flexworkflow") {
				String cambiar = prop.getProperty("cambiar");
				dataWF.setCambiar(Constants.notPermission);
				if (ToolsHTML.isNumeric(cambiar)) {
					dataWF.setCambiar(Byte.parseByte(cambiar));
				}
			}
			dataWF.setNotified(prop.getProperty("notified"));
			dataWF.setDateBegin(ToolsHTML.formatDateShow(prop.getProperty("dateCreation"), true));

			String dateExpireWF = prop.getProperty("dateExpire");
			if (!ToolsHTML.isEmptyOrNull(dateExpireWF)) {
				dataWF.setDateExpire(ToolsHTML.formatDateShow(dateExpireWF, true));
			}
			String dateCompleted = prop.getProperty("dateCompleted");
			if (!ToolsHTML.isEmptyOrNull(dateCompleted)) {
				String dateEndWF = ToolsHTML.formatDateShow(dateCompleted, true);
				dataWF.setDateEnd(dateEndWF);
			}
			dataWF.setNameDocument(prop.getProperty("nameDocument"));
			dataWF.setRequest(prop.getProperty("nombre"));
			dataWF.setTypeWF(prop.getProperty("type"));
			dataWF.setComments(prop.getProperty("comments"));
			dataWF.setStatu(prop.getProperty("statu").trim());
			dataWF.setResult(prop.getProperty("result").trim());
			dataWF.setNumDocument(Integer.parseInt(prop.getProperty("numGen")));
			dataWF.setVersion(Integer.parseInt(prop.getProperty("idLastVersion")));
			dataWF.setStatuDoc(prop.getProperty("statuDoc").trim());
			dataWF.setNumVersion(Integer.parseInt(prop.getProperty("idVersion")));
			dataWF.setNameFile(prop.getProperty("nameFile"));
			dataWF.setTypeDoc(prop.getProperty("typeDoc"));
			dataWF.setCharge(prop.getProperty("cargo"));
			dataWF.setPrefix(prop.getProperty("prefix") == null ? "" : prop.getProperty("prefix"));
			dataWF.setNumber(prop.getProperty("number") == null ? "" : prop.getProperty("number"));
			// idVersion
			String minorVer = prop.getProperty("minorVer") != null ? prop.getProperty("minorVer").trim() : "";
			String mayorVer = prop.getProperty("MayorVer") != null ? prop.getProperty("MayorVer").trim() : "";
			String version = mayorVer.trim() + "." + minorVer.trim();
			dataWF.setIdVersion(version.trim());
			dataWF.setNameWF(prop.getProperty("sAct_Name"));
			dataWF.setNumAct(Long.parseLong(prop.getProperty("Act_Number")));
			dataWF.setIdFlexFlow(Long.parseLong(prop.getProperty("IDFlexFlow"))); // ID
																					// �nico
																					// que
																					// relaciona
																					// todos
																					// las
																					// Actividades
		}
	}

	// public static boolean isUserInWF(String user) throws Exception {
	// boolean result = false;
	// StringBuilder sql = new StringBuilder(60);
	// sql.append("SELECT idWorkFlow FROM user_workflows WHERE idUser = '");
	// sql.append(user).append("'").append(" AND statu IN (").append(wfuPending).append(",").append(wfuQueued);
	// sql.append(")");
	// Vector datos = JDBCUtil.doQueryVector(sql.toString(),Thread.currentThread().getStackTrace()[1].getMethodName());
	// result = datos.size() > 0;
	// return result;
	// }

	/**
	 *
	 * @param idDocument
	 * @param statuFlow
	 * @param chkFlexFlow
	 * @return
	 * @throws Exception
	 */
	public static Collection getUserInWorkFlowPending(String idDocument, String statuFlow, boolean chkFlexFlow) throws Exception {
		return getUserInWorkFlowPending(null, idDocument, statuFlow, chkFlexFlow);
	}
	public static Collection getUserInWorkFlowPending(Connection con, String idDocument, String statuFlow, boolean chkFlexFlow) throws Exception {
		ArrayList resp = new ArrayList();
		StringBuilder sql = new StringBuilder(2048);
		if (!chkFlexFlow) {
			sql.append(" SELECT uwf.iduser,uwf.statu,");
			switch (Constants.MANEJADOR_ACTUAL) {
			case Constants.MANEJADOR_MSSQL:
				sql.append(" (p.Apellidos + ' ' + p.Nombres) AS nombre,");
				break;
			case Constants.MANEJADOR_POSTGRES:
				sql.append(" (p.Apellidos || ' ' || p.Nombres) AS nombre,");
				break;
			case Constants.MANEJADOR_MYSQL:
				sql.append(" CONCAT(p.Apellidos , ' ' , p.Nombres) AS nombre,");
				break;
			}
			sql.append(" c.cargo FROM user_workflows uwf,person p,tbl_cargo c,tbl_area a,workflows wf,documents d")
					.append(" WHERE uwf.idUser = p.nameUser AND uwf.idWorkFlow = wf.idWorkFlow")
					.append(" AND c.idcargo = cast(p.cargo as int) and c.idarea = a.idarea").append(" AND wf.idDocument = d.numgen and d.numgen = ")
					.append(idDocument).append(" AND p.accountActive = '1' AND ").append(" uwf.statu = '").append(statuFlow.trim()).append("'")
					.append(" AND wf.statu <> '").append(HandlerWorkFlows.ownercancelcloseflow).append("'").append(" AND wf.statu = '")
					.append(HandlerWorkFlows.pending).append("'").append(" ORDER BY uwf.Orden");
		} else {
			sql.append(" SELECT uf.statu,uf.iduser,");
			switch (Constants.MANEJADOR_ACTUAL) {
			case Constants.MANEJADOR_MSSQL:
				sql.append("(p.Apellidos + ' ' + p.Nombres) AS nombre, ");
				break;
			case Constants.MANEJADOR_POSTGRES:
				sql.append("(p.Apellidos || ' ' || p.Nombres) AS nombre, ");
				break;
			case Constants.MANEJADOR_MYSQL:
				sql.append("CONCAT(p.Apellidos , ' ' , p.Nombres) AS nombre, ");
				break;
			}
			sql.append("c.cargo,sa.sAct_Name").append(" FROM flexworkflow f,user_flexworkflows uf,person p,tbl_cargo c,subactivities sa")
					.append(" WHERE f.idWorkflow = uf.idWorkflow AND uf.idUser = p.nameUser ")
					.append(" AND f.type = sa.sAct_number AND c.idcargo = cast(p.cargo as int) ").append(" AND f.idDocument = ").append(idDocument)
					.append(" AND p.accountActive = '1' ").append(" AND f.statu = '").append(statuFlow).append("' AND uf.statu = '").append(statuFlow)
					.append("'")
					// sql.append(" AND f.statu <> '").append(HandlerWorkFlows.ownercancelcloseflow).append("'");
					// //Posiblemente se elimine esta L�nea
					.append(" AND f.statu <> '").append(HandlerWorkFlows.ownercancelcloseflow).append("'") // Posiblemente
																											// se
																											// elimine
																											// esta
																											// L�nea
					.append(" ORDER BY uf.idFlexWF");
		}
		log.debug("[getUserInWorkFlowPending] " + sql.toString());
		Vector datos = JDBCUtil.doQueryVector(con, sql.toString(),Thread.currentThread().getStackTrace()[1].getMethodName());
		// int row = 0;
		if ((datos != null) && (datos.size() > 0)) {
			for (int row = 0; row < datos.size(); row++) {
				Properties properties = (Properties) datos.elementAt(row);
				ParticipationForm bean = new ParticipationForm(properties.getProperty("idUser"), properties.getProperty("nombre"));
				bean.setCharge(properties.getProperty("cargo"));
				String statu = properties.getProperty("statu").trim();
				int statuInt = 0;
				if (ToolsHTML.isNumeric(statu)) {
					statuInt = Integer.parseInt(statu.trim());
					bean.setStatu(statuInt);
				}
				if ((statuInt == 3) || (statuInt == 4)) {
					bean.setDateReply(ToolsHTML.formatDateShow(properties.getProperty("dateReplied"), true));
				} else {
					bean.setDateReply("N/A");
				}
				if (chkFlexFlow) {
					bean.setActivity(properties.getProperty("sAct_Name"));
				} else {
					bean.setActivity(null);
				}
				resp.add(bean);
			}
		} else {
			resp = null;
		}
		return resp;
	}

	// SIMON 19 DE JULIO 2005 INICIO
	/**
	 *
	 * @param idDocument
	 * @param statuFlow
	 * @return
	 * @throws Exception
	 */
	public static Collection getUserInFlowPending(String idDocument, String statuFlow) throws Exception {
		ArrayList resp = new ArrayList();
		StringBuilder sql = new StringBuilder(100);

		sql.append(" SELECT uwf.iduser,uwf.statu,");
		switch (Constants.MANEJADOR_ACTUAL) {
		case Constants.MANEJADOR_MSSQL:
			sql.append("(p.Apellidos + ' ' + p.Nombres) AS nombre,");
			break;
		case Constants.MANEJADOR_POSTGRES:
			sql.append("(p.Apellidos || ' ' || p.Nombres) AS nombre,");
			break;
		case Constants.MANEJADOR_MYSQL:
			sql.append("CONCAT(p.Apellidos , ' ' , p.Nombres) AS nombre,");
			break;
		}
		sql.append("c.cargo FROM user_workflows uwf,person p,tbl_cargo c,tbl_area a,workflows wf,documents d,versiondoc vd");
		sql.append(" WHERE uwf.idUser = p.nameUser ");
		sql.append(" AND c.idcargo=cast(p.cargo as int) and c.idarea=a.idarea");
		// 12/03/07
		// Luis Cisneros
		// Se cambio el query para que obtenbga tano flujos de revision como de
		// aprobaci�n
		// sql.append(" AND uwf.idWorkFlow = (SELECT max(idworkflow) FROM workflows wf1 WHERE wf1.type='1' AND wf1.idDocument=").append(idDocument).append(")");
		sql.append(" AND uwf.idWorkFlow  =  (SELECT max(idworkflow) FROM workflows wf1 WHERE wf1.idDocument=").append(idDocument).append(")");
		// Fin Luis Cisneros 12/03/07

		sql.append(" AND wf.idDocument = vd.numDoc and vd.numDoc=").append(idDocument);

		// Luis Cisneros
		// 18-04-07
		// Si la version era un borrador, no se tomaba en cuenta para la
		// notificaci�n de que estaba en flujo
		// ahora se comprar tanto si es una version aprobada, una en borrador u
		// Obsoleta.

		// sql.append(" AND vd.statu = ").append(HandlerDocuments.docApproved);
		sql.append(" AND ");
		sql.append("( vd.statu = '").append(HandlerDocuments.docApproved).append("' ");
		sql.append(" OR vd.statu = '").append(HandlerDocuments.docTrash).append("' ");
		sql.append(" OR vd.statu = '").append(HandlerDocuments.docObs).append("' ");
		sql.append(" )");
		// FIN 18-04-07

		sql.append(" AND d.numGen=vd.numDoc AND p.accountActive = '1' AND ");
		sql.append(" uwf.statu='").append(statuFlow.trim()).append("'");
		sql.append(" ORDER BY Orden");

		// System.out.println("!!!!!!!!!!SQL:" + sql.toString());

		Vector datos = JDBCUtil.doQueryVector(sql.toString(),Thread.currentThread().getStackTrace()[1].getMethodName());
		// int row = 0;
		Hashtable yaExiste = new Hashtable();
		if ((datos != null) && (datos.size() > 0)) {
			for (int row = 0; row < datos.size(); row++) {
				Properties properties = (Properties) datos.elementAt(row);
				if (!yaExiste.containsKey(properties.getProperty("idUser"))) {
					yaExiste.put(properties.getProperty("idUser"), properties.getProperty("idUser"));
					ParticipationForm bean = new ParticipationForm(properties.getProperty("idUser"), properties.getProperty("nombre"));
					bean.setCharge(properties.getProperty("cargo"));
					String statu = properties.getProperty("statu").trim();
					int statuInt = 0;
					if (ToolsHTML.isNumeric(statu)) {
						statuInt = Integer.parseInt(statu.trim());
						bean.setStatu(statuInt);
					}
					if ((statuInt == 3) || (statuInt == 4)) {
						bean.setDateReply(ToolsHTML.formatDateShow(properties.getProperty("dateReplied"), true));
					} else {
						bean.setDateReply("N/A");
					}
					resp.add(bean);
				}
			}
		} else {
			resp = null;
		}
		yaExiste = null;
		return resp;
	}

	public static Collection getOwnerUserInWorkFlow(String idWorkFlow) throws Exception {
		ArrayList resp = new ArrayList();
		StringBuilder sql = new StringBuilder(100);
		sql.append("SELECT uwf.*,uwf.statu,");
		switch (Constants.MANEJADOR_ACTUAL) {
		case Constants.MANEJADOR_MSSQL:
			sql.append("(p.Apellidos + ' ' + p.Nombres) AS nombre,");
			break;
		case Constants.MANEJADOR_POSTGRES:
			sql.append("(p.Apellidos || ' ' || p.Nombres) AS nombre,");
			break;
		case Constants.MANEJADOR_MYSQL:
			sql.append("CONCAT(p.Apellidos , ' ' , p.Nombres) AS nombre,");
			break;
		}
		sql.append("dateReplied,c.cargo,p.email");
		sql.append(" FROM user_workflows uwf,person p,tbl_cargo c,tbl_area a,workflows wf");
		sql.append(" WHERE uwf.idUser = p.nameUser");
		sql.append(" AND c.idcargo=cast(p.cargo as int) and c.idarea=a.idarea");
		sql.append(" AND uwf.idWorkFlow = wf.idWorkFlow");
		sql.append(" AND wf.idWorkFlow = ").append(idWorkFlow);
		sql.append(" AND isOwner = '1' AND p.accountActive = '").append(Constants.permission).append("'");
		sql.append(" ORDER BY Orden");
		log.debug("[getOwnerUserInWorkFlow] = " + sql.toString());
		Vector datos = JDBCUtil.doQueryVector(sql.toString(),Thread.currentThread().getStackTrace()[1].getMethodName());
		for (int row = 0; row < datos.size(); row++) {
			Properties properties = (Properties) datos.elementAt(row);
			ParticipationForm bean = new ParticipationForm(properties.getProperty("idUser"), properties.getProperty("nombre"));
			bean.setCharge(properties.getProperty("cargo"));
			bean.setEmail(properties.getProperty("email"));
			String statu = properties.getProperty("statu").trim();
			int statuInt = 0;

			log.debug("statu = " + statu);
			if (ToolsHTML.isNumeric(statu)) {
				statuInt = Integer.parseInt(statu.trim());
				bean.setStatu(statuInt);
			}
			if ((statuInt == 3) || (statuInt == 4)) {
				bean.setDateReply(ToolsHTML.formatDateShow(properties.getProperty("dateReplied"), true));
			} else {
				bean.setDateReply("N/A");
			}
			bean.setRow(Integer.parseInt(properties.getProperty("row")));
			bean.setCommentsUser(properties.getProperty("comments"));
			bean.setResult(properties.getProperty("result"));
			resp.add(bean);
		}
		return resp;
	}

	public static Collection getOwnerUserInFlexWorkFlow(String idWorkFlow) throws Exception {
		return getOwnerUserInFlexWorkFlow(null, idWorkFlow);
	}

	public static Collection getOwnerUserInFlexWorkFlow(Connection con, String idWorkFlow) throws Exception {
		ArrayList resp = new ArrayList();
		StringBuilder sql = new StringBuilder(100);
		sql.append("SELECT wf.owner,");
		switch (Constants.MANEJADOR_ACTUAL) {
		case Constants.MANEJADOR_MSSQL:
			sql.append("(p.Apellidos + ' ' + p.Nombres) AS nombre,");
			break;
		case Constants.MANEJADOR_POSTGRES:
			sql.append("(p.Apellidos || ' ' || p.Nombres) AS nombre,");
			break;
		case Constants.MANEJADOR_MYSQL:
			sql.append("CONCAT(p.Apellidos , ' ' , p.Nombres) AS nombre,");
			break;
		}
		sql.append("c.cargo,p.email");
		sql.append(" FROM flexworkflow wf, person p,tbl_cargo c ");
		sql.append(" WHERE wf.owner = p.nameUser");
		sql.append(" AND c.idcargo=cast(p.cargo as int) ");
		sql.append(" AND wf.idWorkFlow = ").append(idWorkFlow);
		sql.append(" AND p.accountActive = '").append(Constants.permission).append("'");
		log.debug("[getOwnerUserInFlexWorkFlow] = " + sql.toString());
		Vector datos = JDBCUtil.doQueryVector(con, sql.toString(),Thread.currentThread().getStackTrace()[1].getMethodName());
		for (int row = 0; row < datos.size(); row++) {
			Properties properties = (Properties) datos.elementAt(row);
			ParticipationForm bean = new ParticipationForm(properties.getProperty("owner"), properties.getProperty("nombre"));
			bean.setCharge(properties.getProperty("cargo"));
			bean.setEmail(properties.getProperty("email"));
			resp.add(bean);
		}
		return resp;
	}

	public static Collection getAllUserInWorkFlow(String idWorkFlow) throws Exception {
		ArrayList resp = new ArrayList();
		StringBuilder sql = new StringBuilder(100);
		sql.append("SELECT uwf.*,uwf.statu,");
		switch (Constants.MANEJADOR_ACTUAL) {
		case Constants.MANEJADOR_MSSQL:
			sql.append("(p.Apellidos + ' ' + p.Nombres) AS nombre,");
			break;
		case Constants.MANEJADOR_POSTGRES:
			sql.append("(p.Apellidos || ' ' || p.Nombres) AS nombre,");
			break;
		case Constants.MANEJADOR_MYSQL:
			sql.append("CONCAT(p.Apellidos , ' ' , p.Nombres) AS nombre,");
			break;
		}
		sql.append("dateReplied,c.cargo");
		sql.append(" FROM user_workflows uwf,person p,tbl_cargo c,tbl_area a,workflows wf");
		sql.append(" WHERE uwf.idUser = p.nameUser");
		sql.append(" AND c.idcargo=cast(p.cargo as int) and c.idarea=a.idarea");
		sql.append(" AND uwf.idWorkFlow = wf.idWorkFlow");
		sql.append(" AND wf.idWorkFlow = ").append(idWorkFlow);
		// sql.append(" AND isOwner = '0' AND p.accountActive = '").append(Constants.permission).append("'");
		sql.append(" AND isOwner = '0' ");
		//sql.append(" ORDER BY Orden,`row`");
		switch (Constants.MANEJADOR_ACTUAL) {
		case Constants.MANEJADOR_MSSQL:
			sql.append(" ORDER BY Orden,row");
			break;
		case Constants.MANEJADOR_POSTGRES:
			sql.append(" ORDER BY Orden,row");
			break;
		case Constants.MANEJADOR_MYSQL:
			sql.append(" ORDER BY Orden,`row`");
			break;
		}
		
		
		
		log.debug("[getAllUserInWorkFlow] " + sql);
		Vector datos = JDBCUtil.doQueryVector(sql.toString(),Thread.currentThread().getStackTrace()[1].getMethodName());
		Vector items = new Vector();
		String idUser = null;
		for (int row = 0; row < datos.size(); row++) {
			Properties properties = (Properties) datos.elementAt(row);
			idUser = properties.getProperty("idUser");
			if (!items.contains(idUser)) {
				items.add(idUser);
				ParticipationForm bean = new ParticipationForm(idUser, properties.getProperty("nombre"));
				bean.setCharge(properties.getProperty("cargo"));
				String statu = properties.getProperty("statu").trim();
				int statuInt = 0;
				if (ToolsHTML.isNumeric(statu)) {
					statuInt = Integer.parseInt(statu.trim());
					bean.setStatu(statuInt);
				}
				if ((statuInt == 3) || (statuInt == 4)) {
					bean.setDateReply(ToolsHTML.formatDateShow(properties.getProperty("dateReplied"), true));
				} else {
					bean.setDateReply("N/A");
				}
				bean.setRow(Integer.parseInt(properties.getProperty("row")));
				bean.setCommentsUser(properties.getProperty("comments"));
				String result = properties.getProperty("result");
				if (!ToolsHTML.isEmptyOrNull(result)) {
					bean.setResult(result.trim());
				}
				bean.setIdUser(idUser);
				resp.add(bean);
			}
		}
		return resp;
	}

	// Usuarios en Flujo Param�trico
	public static Collection getAllUserInFlexFlow(long IDAct, String msgReinit) throws Exception {
		ArrayList resp = new ArrayList();
		StringBuilder sql = new StringBuilder(100);
		sql.append("SELECT DISTINCT uwf.*,wf.statu AS stWF,");
		switch (Constants.MANEJADOR_ACTUAL) {
		case Constants.MANEJADOR_MSSQL:
			sql.append("(p.Apellidos + ' ' + p.Nombres) AS nombre,");
			break;
		case Constants.MANEJADOR_POSTGRES:
			sql.append("(p.Apellidos || ' ' || p.Nombres) AS nombre,");
			break;
		case Constants.MANEJADOR_MYSQL:
			sql.append("CONCAT(p.Apellidos , ' ' , p.Nombres) AS nombre,");
			break;
		}
		sql.append("dateReplied,c.cargo,sa.sAct_Name,p.email,sa.sAct_number,uwf.idWorkFlow ");
		sql.append(" FROM user_flexworkflows uwf,person p,flexworkflow wf,subactivities sa,tbl_cargo c");
		sql.append(" WHERE uwf.idUser = p.nameUser AND cast(p.cargo as int) = c.idcargo");
		sql.append(" AND uwf.idWorkFlow = wf.idWorkFlow");
		sql.append(" AND wf.type = sa.sAct_Number");
		sql.append(" AND wf.IDFlexFlow IN (SELECT IDFlexFlow FROM flexworkflow WHERE idWorkFlow = ").append(IDAct).append(")");
		// sql.append(" AND p.accountActive = '").append(Constants.permission).append("'");//
		// .append(" AND uwf.active = '").append(Constants.permission).append("' ");
		// sql.append(" ORDER BY uwf.idWorkFlow,idFlexWF,uwf.Orden");
		// sql.append(" ORDER BY uwf.idWorkFlow,dateReplied,idFlexWF,uwf.Orden");
		// Luis Cisneros 15-03-07
		//sql.append(" ORDER BY uwf.idWorkFlow,idFlexWF,uwf.Orden"); // no ordena correctamente en FTP
		sql.append(" ORDER BY idFlexWF,uwf.idWorkFlow,uwf.Orden");
		// sql.append(" ORDER BY idFlexWF,uwf.Orden");
		log.debug("[getAllUserInFlexFlow] " + sql);
		Vector datos = JDBCUtil.doQueryVector(sql.toString(),Thread.currentThread().getStackTrace()[1].getMethodName());
		Vector items = new Vector();
		String idUser = null;
		for (int row = 0; row < datos.size(); row++) {
			Properties properties = (Properties) datos.elementAt(row);
			idUser = properties.getProperty("idUser");
			// if (!items.contains(idUser)) { //Luis Cisneros 15-03-07
			items.add(idUser);
			ParticipationForm bean = new ParticipationForm(idUser, properties.getProperty("nombre"));
			// si es qui�n inicio el flujo es porque el mismo ha sido reiniciado
			// o el Usuario qui�n responde lo va a reiniciar
			if (Constants.permissionSt.equalsIgnoreCase(properties.getProperty("isOwner"))) {
				bean.setActivity(msgReinit);
				bean.setNumAct(new Long(properties.getProperty("sAct_number")));
			} else {
				bean.setActivity(properties.getProperty("sAct_Name"));
				bean.setNumAct(new Long(properties.getProperty("sAct_number")));
			}
			bean.setIdWorkFlow(new Integer(properties.getProperty("idWorkFlow")));
			int idWorkFlow = new Integer(properties.getProperty("idWorkFlow")).intValue();

			bean.setCharge(properties.getProperty("cargo"));
			String statu = properties.getProperty("statu").trim();
			String stWF = properties.getProperty("stWF").trim();
			int statuInt = 0;
			// Si el Flujo se Encuentra en Cola => los Participantes se
			// encuentran en Cola
			// Siempre y cuando no haya sido un rechazo...
			if (statu != null && statu.compareTo(wfuReInit) == 0) {
				bean.setDateReply(ToolsHTML.formatDateShow(properties.getProperty("dateReplied"), true));
				bean.setStatu(wfReInit);
			} else {
				if (inQueued.equalsIgnoreCase(stWF) && !rechazado.equalsIgnoreCase(statu.trim()) && !wfuAnnulled.equals(statu) && !wfuCompleted.equals(statu)
						&& !wfuReplaced.equals(statu)) { // Luis Cisneros
															// 22-03-07, S'i el
															// flujo de trabajo
															// del Usuario fue
															// anulado por un
															// reinicio, este
															// debe mostrarse
															// asi, no como en
															// cola.
					bean.setStatu(Integer.parseInt(wfuQueued));
					bean.setDateReply("N/A");
				} else {
					if (ToolsHTML.isNumeric(statu)) {
						statuInt = Integer.parseInt(statu.trim());
						bean.setStatu(statuInt);
					}
					log.debug("[statuInt]" + statuInt);
					if ((statuInt == wfAcepted) || (statuInt == wfCanceled) || (statuInt == wfRechazado) || (statuInt == ownerCancelCloseFlow)
							|| (statuInt == wfReInit) || (statuInt == wfCompleted)) {
						bean.setDateReply(ToolsHTML.formatDateShow(properties.getProperty("dateReplied"), true));
					} else {
						bean.setDateReply("N/A");
					}
				}
			}
			bean.setRow(Integer.parseInt(properties.getProperty("idFlexWF")));
			bean.setCommentsUser(properties.getProperty("comments"));
			String result = properties.getProperty("result");
			if (!ToolsHTML.isEmptyOrNull(result)) {
				bean.setResult(result.trim());
			}
			bean.setEmail(properties.getProperty("email"));
			bean.setIdUser(idUser);
			resp.add(bean);
			// } //Luis Cisneros 15-03-07
		}
		return resp;
	}

	public static Collection getAllUserInFlexFlow(int idWorkFlow) throws Exception {
		ArrayList resp = new ArrayList();
		StringBuilder sql = new StringBuilder(100);
		sql.append("SELECT DISTINCT uwf.*,wf.statu AS stWF,");
		switch (Constants.MANEJADOR_ACTUAL) {
		case Constants.MANEJADOR_MSSQL:
			sql.append("(p.Apellidos + ' ' + p.Nombres) AS nombre,");
			break;
		case Constants.MANEJADOR_POSTGRES:
			sql.append("(p.Apellidos || ' ' || p.Nombres) AS nombre,");
			break;
		case Constants.MANEJADOR_MYSQL:
			sql.append("CONCAT(p.Apellidos , ' ' , p.Nombres) AS nombre,");
			break;
		}
		sql.append("dateReplied,c.cargo");
		sql.append(" FROM user_flexworkflows uwf,person p,flexworkflow wf,tbl_cargo c");
		sql.append(" WHERE uwf.idUser = p.nameUser AND cast(p.cargo as int) = c.idCargo ");
		sql.append(" AND uwf.idWorkFlow = wf.idWorkFlow");
		sql.append(" AND wf.idWorkFlow = ").append(idWorkFlow);
		// sql.append(" AND p.accountActive = '").append(Constants.permission).append("'");
		// sql.append(" ORDER BY uwf.Orden,idFlexWF");
		sql.append(" ORDER BY uwf.dateReplied, idFlexWF, uwf.Orden");
		log.debug("[getAllUserInFlexFlow] " + sql);
		Vector datos = JDBCUtil.doQueryVector(sql.toString(),Thread.currentThread().getStackTrace()[1].getMethodName());
		String comments = null;
		for (int row = 0; row < datos.size(); row++) {
			Properties properties = (Properties) datos.elementAt(row);
			ParticipationForm bean = new ParticipationForm(properties.getProperty("idUser"), properties.getProperty("nombre"));
			bean.setCharge(properties.getProperty("cargo"));
			String statu = properties.getProperty("statu").trim();
			String stWF = properties.getProperty("stWF").trim();
			int statuInt = 0;
			// Si el Flujo se Encuentra en Cola => los Participantes se
			// encuentran en Cola
			if (inQueued.equalsIgnoreCase(stWF)) {
				bean.setStatu(Integer.parseInt(wfuQueued));
				bean.setDateReply("N/A");
			} else {
				if (ToolsHTML.isNumeric(statu)) {
					statuInt = Integer.parseInt(statu.trim());
					bean.setStatu(statuInt);
				}
				log.debug("[statuInt] " + statuInt);
				if ((statuInt == 3) || (statuInt == 4) || (statuInt == 11) || (statuInt == 12) || (statuInt == 14) || (statuInt == wfCompleted)) {
					bean.setDateReply(ToolsHTML.formatDateShow(properties.getProperty("dateReplied"), true));
				} else {
					bean.setDateReply("N/A");
				}
			}
			bean.setRow(Integer.parseInt(properties.getProperty("idFlexWF")));
			comments = properties.getProperty("comments");
			if (!ToolsHTML.isEmptyOrNull(comments)) {
				bean.setCommentsUser(comments);
			} else {
				bean.setCommentsUser("null");
			}
			String result = properties.getProperty("result");
			if (!ToolsHTML.isEmptyOrNull(result)) {
				bean.setResult(result.trim());
			}
			resp.add(bean);
		}
		return resp;
	}

	public static Collection getUsersInFlexFlowForReasigner(int idWorkFlow) throws Exception {
		ArrayList resp = new ArrayList();
		StringBuilder sql = new StringBuilder(100);
		sql.append("SELECT uwf.*,wf.statu AS stWF,");
		switch (Constants.MANEJADOR_ACTUAL) {
		case Constants.MANEJADOR_MSSQL:
			sql.append("(p.Apellidos + ' ' + p.Nombres) AS nombre,");
			break;
		case Constants.MANEJADOR_POSTGRES:
			sql.append("(p.Apellidos || ' ' || p.Nombres) AS nombre,");
			break;
		case Constants.MANEJADOR_MYSQL:
			sql.append("CONCAT(p.Apellidos , ' ' , p.Nombres) AS nombre,");
			break;
		}
		sql.append("dateReplied,c.cargo");
		sql.append(" FROM user_flexworkflows uwf,person p,flexworkflow wf,tbl_cargo c");
		sql.append(" WHERE uwf.idUser = p.nameUser AND cast(p.cargo as int) = c.idCargo ");
		sql.append(" AND uwf.idWorkFlow = wf.idWorkFlow");
		sql.append(" AND wf.idWorkFlow = ").append(idWorkFlow);
		sql.append(" AND p.accountActive = '").append(Constants.permission).append("'");
		sql.append(" AND uwf.statu in ('" + wfuPending + "','" + wfuQueued + "','" + wfuAcepted + "')");
		// sql.append(" ORDER BY uwf.Orden,idFlexWF");
		sql.append(" ORDER BY idFlexWF asc,uwf.Orden asc");
		// System.out.println("[getUsersInFlexFlowForReasigner] " + sql);
		log.debug("[getUsersInFlexFlowForReasigner] " + sql);
		Vector datos = JDBCUtil.doQueryVector(sql.toString(),Thread.currentThread().getStackTrace()[1].getMethodName());
		String comments = null;
		for (int row = 0; row < datos.size(); row++) {
			Properties properties = (Properties) datos.elementAt(row);
			ParticipationForm bean = new ParticipationForm(properties.getProperty("idUser"), properties.getProperty("nombre"));
			bean.setCharge(properties.getProperty("cargo"));
			String statu = properties.getProperty("statu").trim();
			String stWF = properties.getProperty("stWF").trim();
			int statuInt = 0;
			// Si el Flujo se Encuentra en Cola => los Participantes se
			// encuentran en Cola
			if (inQueued.equalsIgnoreCase(stWF)) {
				bean.setStatu(Integer.parseInt(wfuQueued));
				bean.setDateReply("N/A");
			} else {
				if (ToolsHTML.isNumeric(statu)) {
					statuInt = Integer.parseInt(statu.trim());
					bean.setStatu(statuInt);
				}
				log.debug("[statuInt] " + statuInt);
				if ((statuInt == 3) || (statuInt == 4) || (statuInt == 11) || (statuInt == 12) || (statuInt == 14) || (statuInt == wfCompleted)) {
					bean.setDateReply(ToolsHTML.formatDateShow(properties.getProperty("dateReplied"), true));
				} else {
					bean.setDateReply("N/A");
				}
			}
			bean.setRow(Integer.parseInt(properties.getProperty("idFlexWF")));
			comments = properties.getProperty("comments");
			if (!ToolsHTML.isEmptyOrNull(comments)) {
				bean.setCommentsUser(comments);
			} else {
				bean.setCommentsUser("");
			}
			String result = properties.getProperty("result");
			if (!ToolsHTML.isEmptyOrNull(result)) {
				bean.setResult(result.trim());
			}
			resp.add(bean);
		}
		return resp;
	}

	// private static String getQueryUpdateStateWF(String table,boolean
	// history){
	// StringBuilder update = new StringBuilder("UPDATE ").append(table);
	// update.append(" SET statu = ? ");
	// if (history){
	// update.append(" WHERE idHistory = ?");
	// } else {
	// update.append(" WHERE idWorkFlow = ?");
	// }
	// return update.toString();
	// }

	// public static boolean updateStatuWorkFlow(String idWF,String
	// idHistory,String newStatu) throws Exception {
	// Connection con = null;
	// PreparedStatement st = null;
	// boolean resp = false;
	// try{
	// con = JDBCUtil.getConnection(Thread.currentThread().getStackTrace()[1].getMethodName());
	// con.setAutoCommit(false);
	// st = con.prepareStatement(getQueryUpdateStateWF("workflows",false));
	// st.setString(1,newStatu);
	// st.setInt(2,Integer.parseInt(idWF));
	// st.executeUpdate();
	// st = con.prepareStatement(getQueryUpdateStateWF("historystruct",true));
	// st.setString(1,newStatu);
	// st.setInt(2,Integer.parseInt(idHistory));
	// st.executeUpdate();
	// con.commit();
	// } catch(Exception e){
	// e.printStackTrace();
	// } finally{
	// setFinally(con,st);
	// }
	// return resp;
	// }

	public static synchronized boolean updateWFReading(String userRow) {
		StringBuilder edit = new StringBuilder(60);
		edit.append("UPDATE user_workflows SET Reading = '").append(Constants.notPermissionSt).append("'");
		edit.append(" WHERE row= ").append(userRow);
		boolean resp = false;
		try {
			resp = JDBCUtil.doUpdate(edit.toString()) > 0;
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return resp;
	}

	// 13 DE JULIO 2005 INICIO
	private static String getValue(int type, String value) {
		StringBuilder resp = new StringBuilder("");
		switch (type) {
		case 1: { // String
			resp.append("'").append(value).append("'");
			break;
		}
		case 2: { // Entero
			resp.append(value);
			break;
		}
		case 3: {
			resp.append(value);
			break;
		}
		case 4: { // Fecha
			switch (Constants.MANEJADOR_ACTUAL) {
			case Constants.MANEJADOR_MSSQL:
				resp.append("CONVERT(datetime,'").append(value).append("',120)");
			case Constants.MANEJADOR_POSTGRES:
				resp.append("CAST('").append(value).append("' as timestamp)");
				break;
			case Constants.MANEJADOR_MYSQL:
				resp.append("TIMESTAMP('").append(value).append("')");
				break;
			}
			break;
		}
		}
		return resp.toString();
	}

	private static String[] getFieldsFlows(String field, String table, String id, String valueId, String condition, int type) throws Exception {
		StringBuilder sql = new StringBuilder("SELECT ").append(field);
		String[] result;
		sql.append(" FROM ").append(table);
		if (!ToolsHTML.isEmptyOrNull(id)) {
			sql.append(" WHERE ").append(id).append(" ").append(condition).append(" ").append(getValue(type, valueId));
		}

		Vector datos = JDBCUtil.doQueryVector(sql.toString(),Thread.currentThread().getStackTrace()[1].getMethodName());
		result = new String[datos.size()];
		for (int row = 0; row < datos.size(); row++) {
			Properties properties = (Properties) datos.elementAt(row);
			result[row] = (properties.getProperty(field));
		}

		return result;
	}

	public static void eraseWorkFlowsDocuments(String idDoc) throws Exception {
		String[] flujos = HandlerWorkFlows.getFieldsFlows("idWorkFlow", "workflows", "idDocument", idDoc, "=", 2);
		for (int i = 0; i < flujos.length; i++) {
			String[] flujos_user = HandlerWorkFlows.getFieldsFlows("row", "user_workflows", "idWorkFlow", flujos[i], "=", 2);
			for (int j = 0; j < flujos_user.length; j++) {
				// //System.out.println("Erase Flujo User row:"+flujos_user[j]);
				HandlerWorkFlows.deleteWFReading(" (" + flujos_user[j] + " ) ");
			}
			// //System.out.println("Erase Flujo :"+flujos[i]);
			HandlerWorkFlows.deleteWFReadingOwner(" (" + flujos[i] + " ) ");
			deactivatePrintFlow(flujos[i]);
		}
	}

	// 13 DE JULIO 2005 FIN

	public static synchronized boolean deleteWFReadingOwner(String idWorkFlows) {
		StringBuilder edit = new StringBuilder(60);
		edit.append("UPDATE workflows SET activeWF = '").append(Constants.notPermissionSt).append("'");
		edit.append(" WHERE idWorkFlow IN  ").append(idWorkFlows).append(" ");
		boolean resp = false;
		try {
			resp = JDBCUtil.doUpdate(edit.toString()) > 0;
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return resp;
	}

	public static synchronized boolean deleteWFReadingOwner(String idWorkFlows, String table) {
		StringBuilder edit = new StringBuilder(60);
		edit.append("UPDATE ").append(table).append(" SET activeWF = '").append(Constants.notPermissionSt).append("'");
		edit.append(" WHERE idWorkFlow IN  ").append(idWorkFlows).append(" ");
		boolean resp = false;
		log.debug("[deleteWFReadingOwner]" + edit.toString());
		try {
			resp = JDBCUtil.doUpdate(edit.toString()) > 0;
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return resp;
	}

	public static synchronized boolean deleteWFReading(String userRow) {
		StringBuilder edit = new StringBuilder(60);
		edit.append("UPDATE user_workflows SET active = '").append(Constants.notPermissionSt).append("'");
		edit.append(" WHERE row IN  ").append(userRow).append(" ");
		boolean resp = false;
		try {
			resp = JDBCUtil.doUpdate(edit.toString()) > 0;
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return resp;
	}

	public static synchronized boolean deleteWFReading(String userRow, String table) {
		StringBuilder edit = new StringBuilder(60);
		edit.append("UPDATE ").append(table).append(" SET active = '").append(Constants.notPermissionSt).append("'");
		if (table.equals("user_flexworkflows"))
			edit.append(" WHERE idFlexWF IN  ").append(userRow).append(" ");
		else
			edit.append(" WHERE row IN  ").append(userRow).append(" ");
		boolean resp = false;
		try {
			resp = JDBCUtil.doUpdate(edit.toString()) > 0;
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return resp;
	}

	public static synchronized boolean updateWFReadingOwner(String idWorkFlow) {
		StringBuilder edit = new StringBuilder(60);
		edit.append("UPDATE workflows SET readingWF = '").append(Constants.notPermissionSt).append("'");
		edit.append(" WHERE idWorkFlow = ").append(idWorkFlow);
		boolean resp = false;
		try {
			resp = JDBCUtil.doUpdate(edit.toString()) > 0;
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return resp;
	}

	public static void loadParticipation(ParticipationForm form, boolean owner) throws Exception {
		StringBuilder sql = new StringBuilder(100);
		// ydavila Ticket 001-00-003023
		// sql.append("SELECT uw.*,uw.row,uw.statu,wf.secuential,wf.conditional,wf.expire,wf.expire,uw.isOwner");      AS typeWF         AS sybtypeWF   GRG 
		sql.append("SELECT uw.*,uw.row,uw.statu,wf.secuential,wf.conditional,wf.expire,wf.expire,uw.isOwner,wf.type AS typeWF,wf.subtype AS sybtypeWF");
		sql.append(" FROM user_workflows uw,workflows wf WHERE Wf.idWorkFlow = uw.IdWorkFlow");
		sql.append(" AND uw.idWorkFlow = ").append(form.getIdWorkFlow());
		sql.append(" AND uw.idUser='").append(form.getIdParticipation()).append("'");
		if (form.getRow() > 0) {
			//sql.append(" AND `row`= ").append(form.getRow());
			sql.append(" AND row = ").append(form.getRow());
		}
		if (owner) {
			sql.append(" AND uw.isOwner = '1'");
		} else {
			sql.append(" AND uisOwner = '0' 0");
		}
		Properties prop = JDBCUtil.doQueryOneRow(sql.toString(),Thread.currentThread().getStackTrace()[1].getMethodName());
		if (prop.getProperty("isEmpty").equalsIgnoreCase("false")) {
			String statu = prop.getProperty("statu").trim();
			if (ToolsHTML.isNumeric(statu)) {
				form.setStatu(Integer.parseInt(statu.trim()));
			}
			form.setRow(Integer.parseInt(prop.getProperty("row")));
			form.setDateReply(ToolsHTML.formatDateShow(prop.getProperty("dateReplied"), true));
			form.setCommentsUser(prop.getProperty("comments"));
			form.setResult(prop.getProperty("result").trim());
			form.setIdMovement(prop.getProperty("uw.row"));
			form.setConditional(prop.getProperty("conditional"));
			form.setSecuential(prop.getProperty("secuential"));
			form.setExpire(prop.getProperty("expire"));
			form.setNotified(prop.getProperty("expire"));
			if (prop.getProperty("isOwner").equalsIgnoreCase("1")) {
				form.setisOwner(true);
			} else {
				form.setisOwner(false);
			}
			// ydavila Ticket 001-00-003023
			form.setTypeWF(prop.getProperty("type"));
			form.setSubtypeWF(prop.getProperty("subtype"));
		}
	}

	// public static synchronized Collection getUser(String idMovement,String
	// idWorkFlow) throws Exception {
	// ArrayList resp = new ArrayList();
	// StringBuilder sql = new
	// StringBuilder("SELECT su.* FROM statususermov su,user_workflows uw");
	// sql.append(" WHERE su.idUser = uw.IdUser AND su.IdMovement = ").append(idMovement);
	// sql.append(" AND uw.idWorkFlow = ").append(idWorkFlow).append(" ORDER BY Orden");
	// //System.out.println("[getUser] = " + sql.toString());
	// Vector datos = JDBCUtil.doQueryVector(sql.toString(),Thread.currentThread().getStackTrace()[1].getMethodName());
	// for (int row = 0; row < datos.size(); row++) {
	// Properties properties = (Properties) datos.elementAt(row);
	// BeanStatusUserMov bean = new BeanStatusUserMov();
	// bean.setIdUser(properties.getProperty("IdUser"));
	// bean.setStatu(properties.getProperty("Statu").trim());
	// String date = properties.getProperty("dateReplied");
	// if (ToolsHTML.isEmptyOrNull(date)){
	// bean.setDateReplied(null);
	// } else {
	// bean.setDateReplied(date);
	// }
	// String comments = properties.getProperty("comments");
	// if (ToolsHTML.isEmptyOrNull(comments)){
	// bean.setComments(null);
	// } else {
	// bean.setComments(comments);
	// }
	// resp.add(bean);
	// }
	// return resp;
	// }

	// public static synchronized void update(Connection con,int
	// idMovement,Collection items,
	// String secuential,ParticipationForm form,Timestamp time) throws Exception
	// {
	// // boolean updateWF = true;
	// PreparedStatement st = null;
	// StringBuilder insert = new
	// StringBuilder("INSERT INTO statususermov(IdStatu,IdMovement,IdUser,Statu,dateReplied,comments,parent)");
	// insert.append(" VALUES(?,?,?,?,?,?,?) ");
	// boolean active = false;
	// for (Iterator iterator = items.iterator(); iterator.hasNext();) {
	// BeanStatusUserMov bean = (BeanStatusUserMov)iterator.next();
	// int statusUserMov = IDDBFactorySql.getNextID("statususermov");
	// st = con.prepareStatement(insert.toString());
	// st.setInt(1,statusUserMov);
	// st.setInt(2,idMovement);
	// st.setString(3,bean.getIdUser());
	// if (!ToolsHTML.isEmptyOrNull(bean.getDateReplied())){
	// java.util.Date fecha = ToolsHTML.sdf.parse(bean.getDateReplied());
	// st.setTimestamp(5,new Timestamp(fecha.getTime()));
	// } else {
	// st.setTimestamp(5,null);
	// }
	// if (!ToolsHTML.isEmptyOrNull(bean.getComments())) {
	// st.setString(6,bean.getComments());
	// } else {
	// st.setString(6,null);
	// }
	// // String statuActual = bean.getStatu();
	// //System.out.println("secuential = " + secuential);
	// if (secuential.equalsIgnoreCase("0")) {//Si el Flujo es Secuencial
	// if (active) { //Si la bandera de indicador de que es el siguiente usuario
	// en el Flujo esta activa
	// st.setString(4,wfuPending); //Se cambia el Estado del Usuario actual
	// // statuActual = wfuPending;
	// } else {
	// st.setString(4,bean.getStatu());
	// active = false;
	// }
	// } else { //En caso contrario se mantiene el statu del mismo......
	// active = false;
	// st.setString(4,bean.getStatu());
	// }
	// if (bean.getIdUser().equalsIgnoreCase(form.getIdParticipation())){
	// st.setString(4,String.valueOf(form.getStatu()));
	// st.setTimestamp(5,time);
	// st.setString(6,form.getCommentsUser());
	// active = true;
	// // statuActual = String.valueOf(form.getStatu());
	// }
	// st.setInt(7,1);
	// st.executeUpdate();
	// // if (updateWF){ //Si debo Actualizar el Flujo.... Chequeo.. en caso
	// contrario ya se q no debo actualizar el flujo
	// // if ((statuActual.trim().equalsIgnoreCase(wfuPending))||
	// // (statuActual.trim().equalsIgnoreCase(wfuCanceled))){
	// // updateWF = false;
	// // }
	// // }
	// }
	// // return updateWF;
	// }

	// private static synchronized void updateTopWorkFlows(int
	// idMovement,PreparedStatement st,Connection con) throws Exception {
	// String sql = "UPDATE statususermov SET parent = 0 WHERE idMovement = ?";
	// st = con.prepareStatement(sql);
	// st.setInt(1,idMovement);
	// st.executeUpdate();
	// }

	public static void notifiedUsers(String titleMail, String user, String account, String email, String comments) {
		MailForm formaMail = new MailForm();
		formaMail.setFrom(account);
		formaMail.setNameFrom(user);
		formaMail.setTo(email);
		formaMail.setSubject(titleMail);
		formaMail.setMensaje(comments);
		try {
			// SendMailAction.send(formaMail);
			SendMailTread mail = new SendMailTread(formaMail);
			mail.start();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	/**
	 *
	 * @param idWorkFlow
	 * @param row
	 * @return
	 */
	public static boolean getStatuWorkFlow(int idWorkFlow, int row) {
		// true, false
		boolean resp = true;
		try {
			StringBuilder sql = new StringBuilder(1024).append("SELECT uw.result, w.conditional, w.type FROM user_workflows uw,workflows w")
					.append(" WHERE w.idWorkFlow = uw.idWorkFlow AND w.idWorkFlow = ").append(idWorkFlow);
			if (row > 0) {
				
				//cambio con un switch para el manejador mysql que acepta comilla literal tanto pogresql con mssqlserver no aceptar comilla literal
				sql.append(" AND uw.row<> ").append(row);
				switch (Constants.MANEJADOR_ACTUAL) {
				case Constants.MANEJADOR_MYSQL:
					//sql.append(" AND uw.`row`<> ").append(row);
					sql.append(" AND uw.row<> ").append(row);
					break;
				}
				//sql.append(" AND uw.`row`<> ").append(row);
			}
			// System.out.println("[getStatuWorkFlow] = " + sql);
			Vector datos = JDBCUtil.doQueryVector(sql.toString(),Thread.currentThread().getStackTrace()[1].getMethodName());
			for (int index = 0; index < datos.size(); index++) {
				Properties properties = (Properties) datos.elementAt(index);
				if (index == 0) {
					if (properties.getProperty("conditional").trim().equalsIgnoreCase("1") && properties.getProperty("type").trim().equalsIgnoreCase("0")) {
						resp = true;
						break;
					}
				}
				if (!properties.getProperty("result").trim().equalsIgnoreCase(wfuAcepted)) {
					resp = false;
					break;
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			resp = false;
		}
		return resp;
	}

	/**
	 *
	 * @param idWorkFlow
	 * @param row
	 * @param beanNotified
	 * @throws ApplicationExceptionChecked
	 */
	public static void getNextUserInWF(int idWorkFlow, int row, BeanNotifiedWF beanNotified) throws ApplicationExceptionChecked {
		try {
			StringBuilder sql = new StringBuilder(1024).append("SELECT uw.row,uw.idUser,uw.isOwner,p.email,w.comments,w.owner,w.notified, ");
			switch (Constants.MANEJADOR_ACTUAL) {
			case Constants.MANEJADOR_MSSQL:
				sql.append(" (p.apellidos + ' ' + p.nombres) AS nombre,");
				break;
			case Constants.MANEJADOR_POSTGRES:
				sql.append(" (p.apellidos || ' ' || p.nombres) AS nombre,");
				break;
			case Constants.MANEJADOR_MYSQL:
				sql.append(" CONCAT(p.apellidos , ' ' , p.nombres) AS nombre,");
				break;
			}
			sql.append("d.nameDocument FROM user_workflows uw,workflows w,person p")
					.append(" ,documents d WHERE uw.idUser = p.nameUser AND w.idWorkFlow = uw.idWorkFlow AND uw.idWorkFlow = ").append(idWorkFlow)
					//.append(" AND w.idDocument = d.numGen AND `row`<>  ").append(row).append(" AND uw.result = '").append(pending).append("'")
					.append(" AND w.idDocument = d.numGen AND row<>  ").append(row).append(" AND uw.result = '").append(pending).append("'")
					//.append(" AND p.accountActive = '").append(Constants.permission).append("'").append(" ORDER BY `Row`");
			        .append(" AND p.accountActive = '").append(Constants.permission).append("'").append(" ORDER BY Row");
			// System.out.println("[getNextUserInWF] = " + sql);
			Vector datos = JDBCUtil.doQueryVector(sql.toString(),Thread.currentThread().getStackTrace()[1].getMethodName());
			if (datos.size() > 0) {
				Properties properties = (Properties) datos.elementAt(0);
				beanNotified.setEmail(properties.getProperty("email"));
				beanNotified.setRow(properties.getProperty("row"));
				beanNotified.setIdUser(properties.getProperty("idUser"));
				beanNotified.setComments(properties.getProperty("comments"));
				beanNotified.setOwner(properties.getProperty("owner"));
				beanNotified.setOwnerWF("1".equalsIgnoreCase(properties.getProperty("isOwner")));
				beanNotified.setNotified(properties.getProperty("notified").trim());
				beanNotified.setNameUser(properties.getProperty("nombre"));
				beanNotified.setNameDocument(properties.getProperty("nameDocument"));
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			throw new ApplicationExceptionChecked("E0015");
		}
	}

	/**
	 *
	 * @param idWorkFlow
	 * @param row
	 * @param idFather
	 * @return
	 */
	public static boolean getStatuFlexFlow(int idWorkFlow, int row, long idFather) {
		boolean resp = true;
		try {
			StringBuilder sql = new StringBuilder(1024).append("SELECT uw.result FROM user_flexworkflows uw,flexworkflow w")
					.append(" WHERE w.idWorkFlow = uw.idWorkFlow AND w.idWorkFlow = ").append(idWorkFlow);
			if (row > 0) {
				sql.append(" AND uw.idFlexWF <> ").append(row);
			}
			if (idFather > 0) {
				sql.append(" AND idFlexWF <> ").append(idFather);
			}
			sql.append(" AND isOwner = ").append(Constants.notPermission).append(" AND wfActive = '1'");
			log.debug("[getStatuWorkFlow] = " + sql);
			Vector datos = JDBCUtil.doQueryVector(sql.toString(),Thread.currentThread().getStackTrace()[1].getMethodName());
			for (int index = 0; index < datos.size(); index++) {
				Properties properties = (Properties) datos.elementAt(index);
				if (!properties.getProperty("result").trim().equalsIgnoreCase(wfuAcepted)) {
					resp = false;
					break;
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			resp = false;
		}
		return resp;
	}

	/**
	 *
	 * @param idWorkFlow
	 * @param row
	 * @param beanNotified
	 * @param statu
	 * @param comparator
	 * @throws ApplicationExceptionChecked
	 */
	public static void getNextUserInFlexWF(long idWorkFlow, long row, BeanNotifiedWF beanNotified, String statu, String comparator)
			throws ApplicationExceptionChecked {
		try {
			StringBuilder sql = new StringBuilder(2048).append("SELECT uw.idFlexWF,uw.idUser,uw.isOwner,p.email,w.comments,w.owner,w.notified,uw.idWorkFlow, ");
			switch (Constants.MANEJADOR_ACTUAL) {
			case Constants.MANEJADOR_MSSQL:
				sql.append(" (p.apellidos + ' ' + p.nombres) AS nombre,");
				break;
			case Constants.MANEJADOR_POSTGRES:
				sql.append(" (p.apellidos || ' ' || p.nombres) AS nombre,");
				break;
			case Constants.MANEJADOR_MYSQL:
				sql.append(" CONCAT(p.apellidos , ' ' , p.nombres) AS nombre,");
				break;
			}
			sql.append(" d.nameDocument FROM user_flexworkflows uw,flexworkflow w,person p")
					.append(" ,documents d WHERE uw.idUser = p.nameUser AND w.idWorkFlow = uw.idWorkFlow AND uw.idWorkFlow = ").append(idWorkFlow)
					.append(" AND w.idDocument = d.numGen AND idFlexWF ").append(comparator).append("  ").append(row).append(" AND uw.statu NOT IN ('")
					.append(HandlerWorkFlows.wfuAnnulled).append("','").append(HandlerWorkFlows.wfuReplaced).append("')").append(" AND uw.result = '")
					.append(statu).append("'").append(" AND p.accountActive = '").append(Constants.permission).append("'").append(" ORDER BY idFlexWF");
			log.debug("[getNextUserInFlexWF] " + sql);
			// //System.out.println("[getNextUserInFlexWF] " + sql);
			Vector datos = JDBCUtil.doQueryVector(sql.toString(),Thread.currentThread().getStackTrace()[1].getMethodName());
			if (datos.size() > 0) {
				Properties properties = (Properties) datos.elementAt(0);
				beanNotified.setEmail(properties.getProperty("email"));
				beanNotified.setRow(properties.getProperty("idFlexWF"));
				beanNotified.setIdUser(properties.getProperty("idUser"));
				beanNotified.setComments(properties.getProperty("comments"));
				beanNotified.setOwner(properties.getProperty("owner"));
				beanNotified.setOwnerWF(false);
				// System.out.println(properties.getProperty("notified").trim());
				beanNotified.setNotified(properties.getProperty("notified").trim());
				beanNotified.setNameUser(properties.getProperty("nombre"));
				beanNotified.setNameDocument(properties.getProperty("nameDocument"));
				beanNotified.setIdWorkFlow(Integer.parseInt(properties.getProperty("idWorkFlow")));
				beanNotified.setNotified(properties.getProperty("notified").trim());
				// //System.out.println("[beanNotified.getRow()] " +
				// beanNotified.getRow());
				// //System.out.println("[beanNotified.getIdUser() " +
				// beanNotified.getIdUser());
			} else {
				log.debug("No hay m�s usuarios para la Actividad...");
				beanNotified.setRow(null);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			throw new ApplicationExceptionChecked("E0015");
		}
	}

	/**
	 * M�todo utilizado para actualizar el estado de los flujos de trabajo una vez que ha sido cancelado por alguno de los usuarios del sistema
	 */
	// public static synchronized void updateUserWFStatu(String statu,String
	// result,int idWorkFlow,Connection con)
	// throws SQLException {
	// StringBuilder update = new StringBuilder(50);
	// update.append("UPDATE user_workflows SET statu = ").append(statu.trim());
	// update.append(",result = ").append(result).append(" WHERE idWorkFlow = ").append(idWorkFlow);
	// PreparedStatement st = null;
	// st = con.prepareStatement(update.toString());
	// st.executeUpdate();
	// }
	// updateStatuWF("workflows",form.getIdWorkFlow(),con,wfuAcepted,response,time);
	public static void updateStatuWF(String table, int idWorkFlow, Connection con, String statu, String result, Timestamp time) throws SQLException, Exception {

		StringBuilder update = new StringBuilder(1024).append("UPDATE  ").append(table).append(" SET statu = ").append(statu.trim()).append(",result = ")
				.append(result).append(",dateCompleted = ?").append(" WHERE idWorkFlow = ").append(idWorkFlow);
		log.debug("[updateStatuWF] = " + update.toString());
		Connection conn = (con != null) ? con : JDBCUtil.getConnection(Thread.currentThread().getStackTrace()[1].getMethodName());
		PreparedStatement st = conn.prepareStatement(JDBCUtil.replaceCastMysql(update.toString()));
		st.setTimestamp(1, time);
		st.executeUpdate();
		if (con == null) {
			setFinally(conn, st);
		} else {
			DbUtils.closeQuietly(st);
		}
	}

	/**
	 *
	 * @param IDFlexFlow
	 * @param con
	 * @param statu
	 * @param result
	 * @param time
	 * @throws Exception
	 */
	public static void updateStatuFlexFlow(long IDFlexFlow, Connection con, String statu, String result, Timestamp time) throws Exception {
		StringBuilder update = new StringBuilder(1024).append("UPDATE flexworkflow SET statu = ").append(statu.trim()).append(",result = ").append(result)
				.append(",dateCompleted = ?").append(" WHERE IDFlexFlow = ").append(IDFlexFlow);
		// //System.out.println("updateStatuFlexFlow sql : " +
		// update.toString());
		Connection conn = (con != null) ? con : JDBCUtil.getConnection(Thread.currentThread().getStackTrace()[1].getMethodName());
		PreparedStatement st = conn.prepareStatement(JDBCUtil.replaceCastMysql(update.toString()));
		st.setTimestamp(1, time);
		st.executeUpdate();
		if (con == null) {
			setFinally(conn, st);
		} else {
			DbUtils.closeQuietly(st);
		}
	}

	/**
	 * M�todo utilizado para Cerrar el Flujo indicado
	 */
	// public static synchronized boolean closedWF(int idWorkFlow) {
	// Connection con = null;
	// PreparedStatement st = null;
	// boolean resp = false;
	// try {
	// Timestamp time = new Timestamp(new java.util.Date().getTime());
	// con = JDBCUtil.getConnection(Thread.currentThread().getStackTrace()[1].getMethodName());
	// con.setAutoCommit(false);
	// updateStatuWF("workflows",idWorkFlow,con,wfuCanceled,canceled,time);
	// con.commit();
	// } catch (Exception ex) {
	// applyRollback(con);
	// ex.printStackTrace();
	// } finally{
	// setFinally(con,st);
	// }
	// return resp;
	// }
	// private static void setDataVersion(String newState,String lastOpe,String
	// typeMovHist
	// ,String dateExpire,CheckOutDocForm version,String monthExpireDoc,
	// String unit,java.util.Date date,Timestamp dateExpireEnd,Timestamp time)
	// throws Exception {
	// newState = HandlerDocuments.docApproved;
	// lastOpe = HandlerDocuments.lastApproved;
	// typeMovHist = Constants.historyApproved;
	// if (dateExpire==null) {
	// int value = 0;
	// if (ToolsHTML.isNumeric(monthExpireDoc.trim())){
	// value = Integer.parseInt(monthExpireDoc.trim());
	// }
	// java.util.Date dateExpireCalculated =
	// ToolsHTML.getDateExpireDocument(ToolsHTML.sdf.format(date),
	// null,value,unit);
	// if (dateExpireCalculated!=null) {
	// dateExpireEnd = new Timestamp(dateExpireCalculated.getTime());
	// }
	// } else {
	// dateExpireEnd = new Timestamp(ToolsHTML.sdf.parse(dateExpire).getTime());
	// }
	// version.setStatu(newState);
	// version.setApproved("0");
	// version.setDateApproved(ToolsHTML.sdfShowConvert.format(time));
	// if (ToolsHTML.isEmptyOrNull(version.getDateExpires())) {
	// version.setDateExpires(ToolsHTML.sdfShowConvert.format(dateExpireEnd));
	// }
	//
	// if (ToolsHTML.isNumeric(version.getMinorVer())) {
	// version.setMinorVer("0");
	// } else {
	// if (!ToolsHTML.isEmptyOrNull(version.getMinorVer().trim())) {
	// version.setMinorVer("A");
	// } else {
	// version.setMinorVer("");
	// }
	// }
	//
	// if (ToolsHTML.isNumeric(version.getMayorVer())) {
	// int mayorVer = Integer.parseInt(version.getMayorVer().trim()) + 1;
	// version.setMayorVer(String.valueOf(mayorVer));
	// } else {
	// version.setMayorVer(ToolsHTML.incVersion(version.getMayorVer().trim()));
	// }
	// }
	public static synchronized String getOwnerWF(String idWF) throws Exception {
		//StringBuilder sql = new StringBuilder(1024).append("SELECT row FROM user_workflows WHERE (isOwner = '1') AND (idWorkFlow = ").append(idWF).append(")");
		StringBuilder sql = new StringBuilder(1024).append("SELECT row FROM user_workflows WHERE (isOwner = '1') AND (idWorkFlow = ").append(idWF).append(")");
		Properties prop = JDBCUtil.doQueryOneRow(sql.toString(),Thread.currentThread().getStackTrace()[1].getMethodName());
		if (prop.getProperty("isEmpty").equalsIgnoreCase("false")) {
			return prop.getProperty("row").trim();
		}
		return "";
	}

	/*
	 * public static synchronized String getOwnerFlexFlow(String idWF) throws Exception { StringBuilder sql = new StringBuilder(50); sql.append(
	 * "SELECT row FROM user_workflows WHERE (isOwner = '1') AND (idWorkFlow = " ); sql.append(idWF).append(")"); Properties prop =
	 * JDBCUtil.doQueryOneRow(sql.toString(),Thread.currentThread().getStackTrace()[1].getMethodName()); if (prop.getProperty("isEmpty").equalsIgnoreCase("false")) { return prop.getProperty("row").trim(); } return "";
	 * }
	 */

	private static synchronized void disabledUserWF(int idWF, Connection con, String result, long row, String active) throws SQLException {
		StringBuilder upd = new StringBuilder(1024).append("UPDATE USER_WORKFLOWS SET wfActive = ").append(active).append(" WHERE isOwner = '0' ");
		if (!ToolsHTML.isEmptyOrNull(result)) {
			upd.append(" AND result = ").append(result);
		}
		upd.append(" AND idWorkFlow = ").append(idWF);
		if (row != 0) {
			//upd.append(" AND `row`= ").append(row);
			upd.append(" AND row = ").append(row);
		}
		log.debug("[disabledUserWF] upd = " + upd);
		PreparedStatement st = con.prepareStatement(JDBCUtil.replaceCastMysql(upd.toString()));
		st.executeUpdate();
		DbUtils.closeQuietly(st);
	}

	/**
	 * C�digo para Desactivar los Usuarios Pendientes en el Flujo Param�trico
	 *
	 * @param idWF
	 * @param con
	 * @param result
	 * @param row
	 * @param active
	 * @throws SQLException
	 */
	private static synchronized void disabledUserFlexFlow(int idWF, Connection con, String result, long row, String active) throws SQLException {
		StringBuilder upd = new StringBuilder(1024).append("UPDATE user_flexworkflows SET wfActive = '").append(active).append("'")
				.append(" WHERE isOwner = '0' ");
		if (!ToolsHTML.isEmptyOrNull(result)) {
			upd.append(" AND result = '").append(result).append("' ");
		}
		// upd.append(" AND idWorkFlow = ").append(idWF);
		if (wfuAcepted.compareTo(result) != 0) {
			upd.append(" AND idWorkFlow = ").append(idWF);
		} else {
			upd.append(" AND idWorkFlow IN (SELECT idWorkFlow FROM flexworkflow WHERE IDFlexFlow = ").append(row).append(")");
		}
		if (row != 0) {
			upd.append(" AND idFlexWF = ").append(row);
		}
		log.debug("[disabledUserWF] upd = " + upd);
		PreparedStatement st = con.prepareStatement(JDBCUtil.replaceCastMysql(upd.toString()));
		st.executeUpdate();
		DbUtils.closeQuietly(st);
	}

	/**
	 *
	 * @param con
	 * @param active
	 * @param idFlexFlow
	 * @param sActiveAct
	 * @param status
	 * @param isWF
	 * @throws SQLException
	 */
	private static void annulledUserFlexFlow(Connection con, byte active, long idFlexFlow, byte sActiveAct, String status, boolean isWF) throws SQLException {
		StringBuilder upd = new StringBuilder(1024).append("UPDATE user_flexworkflows SET wfActive = ").append(active).append(" WHERE isOwner = '0' ");
		if (isWF) {
			upd.append("   AND idWorkFlow = ").append(idFlexFlow);
		} else {
			upd.append("   AND idWorkFlow IN (SELECT idWorkFlow FROM flexworkflow WHERE IDFlexFlow = ");
			upd.append(idFlexFlow).append(" )");
		}
		upd.append("   AND wfActive = ").append(sActiveAct);
		if (status != null) {
			upd.append("   AND statu IN (").append(status).append(")");
		}
		PreparedStatement st = con.prepareStatement(JDBCUtil.replaceCastMysql(upd.toString()));
		st.executeUpdate();
		DbUtils.closeQuietly(st);
	}

	/**
	 *
	 * @param con
	 * @param active
	 * @param idFlexFlow
	 * @param sActiveAct
	 * @param status
	 * @param isWF
	 * @throws SQLException
	 */
	private static void enabledUserFlexFlow(Connection con, byte active, long idFlexFlow, byte sActiveAct, String status, boolean isWF) throws SQLException {
		StringBuilder upd = new StringBuilder(50);
		upd.append("UPDATE user_flexworkflows SET wfActive = '").append(active).append("'");
		upd.append(" WHERE isOwner = '0' ");
		if (isWF) {
			upd.append(" AND idWorkFlow = ").append(idFlexFlow);
		} else {
			upd.append(" AND idWorkFlow IN (SELECT idWorkFlow FROM flexworkflow WHERE IDFlexFlow = ");
			upd.append(idFlexFlow).append(" )");
		}
		upd.append(" AND wfActive = '").append(sActiveAct).append("' ");
		if (status != null) {
			upd.append(" AND statu IN ('").append(status.replaceAll("'", "").replaceAll(",", "','")).append("')");
		}
		PreparedStatement st = null;
		st = con.prepareStatement(JDBCUtil.replaceCastMysql(upd.toString()));
		st.executeUpdate();
	}

	/**
	 * Activando la Actividad en el Flujo Parametrico
	 *
	 * @param idWorkFlow
	 * @param con
	 * @param newStatu
	 * @throws SQLException
	 */
	private static synchronized void enableActivityInFlexFlow(long idWorkFlow, Connection con, String newStatu) throws SQLException {
		StringBuilder upd = new StringBuilder(50);
		upd.append("UPDATE flexworkflow SET statu = ").append(newStatu);
		upd.append(" WHERE idWorkFlow = ").append(idWorkFlow);
		log.debug("[enableActivityInFlexFlow] upd = " + upd);
		PreparedStatement st = null;
		st = con.prepareStatement(JDBCUtil.replaceCastMysql(upd.toString()));
		st.executeUpdate();
	}

	public static synchronized BeanWFRejectes loadDataRejectedWF(long idWF) {
		StringBuilder sql = new StringBuilder(50);
		sql.append("SELECT wp.statu,uw.row,uw.idUser,wp.idPending");
		sql.append("  FROM workflowspendings wp,user_workflows uw");
		sql.append(" WHERE wp.idWorkFlow = ").append(idWF);
		sql.append("   AND uw.idWorkFlow = wp.idWorkFlow");
		
		//cambio con un switch para el manejador mysql que acepta comilla literal tanto pogresql con mssqlserver no aceptar comilla literal
		sql.append("   AND wp.idRechazo  = uw.row AND uw_Circle = 1");
		switch (Constants.MANEJADOR_ACTUAL) {
		case Constants.MANEJADOR_MYSQL:
			//sql.append("   AND wp.idRechazo  = uw.`row`AND uw_Circle = 1");
			sql.append("   AND wp.idRechazo  = uw.row AND uw_Circle = 1");
			break;
		}
		BeanWFRejectes bean = null;
		try {
			Properties data = JDBCUtil.doQueryOneRow(sql.toString(),Thread.currentThread().getStackTrace()[1].getMethodName());
			if (data.getProperty("isEmpty").equalsIgnoreCase("false")) {
				bean = new BeanWFRejectes(Byte.parseByte(data.getProperty("statu")), Long.parseLong(data.getProperty("row")), data.getProperty("idUser"),
						Long.parseLong(data.getProperty("idPending")));

			}

		} catch (Exception ex) {
			log.error(ex.getMessage());
			ex.printStackTrace();
		}
		return bean;
	}

	public static synchronized boolean thereArePendingUsers(long idWF) {
		StringBuilder sql = new StringBuilder(50);
		sql.append("SELECT row FROM user_workflows");
		sql.append(" WHERE idWorkFlow = ").append(idWF);
		sql.append("   AND cast(result as int) IN (").append(wfuPending).append(",").append(wfuQueued);
		sql.append(")  AND (isOwner = '0')");
		try {
			Vector datos = JDBCUtil.doQueryVector(sql.toString(),Thread.currentThread().getStackTrace()[1].getMethodName());
			if (datos != null && !datos.isEmpty()) {
				return true;
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return false;
	}

	public static synchronized boolean isPendingUserWorkFlow(long idWF, String idUser) {
		StringBuilder sql = new StringBuilder(50);
		sql.append("SELECT idUser FROM user_workflows ");
		sql.append("WHERE idWorkFlow = ").append(idWF).append(" ");
		sql.append("AND cast(result as int) IN (").append(wfuPending).append(",").append(wfuQueued).append(") ");
		sql.append("AND isOwner = '0' ");
		sql.append("AND idUser = '").append(idUser).append("'");
		try {
			Vector datos = JDBCUtil.doQueryVector(sql.toString(),Thread.currentThread().getStackTrace()[1].getMethodName());
			if (datos != null && !datos.isEmpty()) {
				return true;
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return false;
	}

	/**
	 * Retorna si hay Usuarios pendientes en el Flujo de Trabajo
	 *
	 * @param idWF
	 */
	public static synchronized boolean thereArePendingUsersFlex(long idWF) {
		StringBuilder sql = new StringBuilder(50);
		sql.append("SELECT idFlexWF FROM user_flexworkflows");
		sql.append(" WHERE idWorkFlow = ").append(idWF);
		sql.append("   AND result IN (").append(wfuPending).append(",").append(wfuQueued);
		sql.append(")");
		try {
			Vector datos = JDBCUtil.doQueryVector(sql.toString(),Thread.currentThread().getStackTrace()[1].getMethodName());
			if (datos != null && !datos.isEmpty()) {
				return true;
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return false;
	}

	public static synchronized boolean setResponseWF(ParticipationForm form, BeanNotifiedWF beanNotified, int minorKeep, int mayorKeep,
			HttpServletRequest request, String docRelations) throws ApplicationExceptionChecked {
		Connection con = null;
		PreparedStatement st = null;
		String ActFactOwner = "";
		int ActFactNumGen = 0;
		boolean swActFactPubl = false;
		boolean resp = false;
		boolean CierraCiclo = false;
		java.util.Date date = new Date();
		// obtenemos el status del workflow, me devulve true si el flujo esta
		// aceptado
		boolean statuWF = getStatuWorkFlow(form.getIdWorkFlow(), form.getRow());
		String NumCorrelativo = null;
		String statuDocAnt = null;
		String numberxxx = null;
		String statuDoc = null;
		String statuVersion = null;
		int versionStatuhist = 0;
		try {
			// realizamos un sw en caso que no sea sequencial el flujo, de tal
			// manera de que si firma el due�o
			// y cierra el due�o antes que los firmantes,. no cierre el ciclo
			if (wfSecuential.equalsIgnoreCase(form.getSecuential())) {
				// cierra el duenio
				CierraCiclo = form.isOwner();
			} else {
				StringBuilder sql = new StringBuilder("");
				sql.append(" FROM user_workflows WHERE idworkflow=").append(form.getIdWorkFlow());
				//sql.append(" AND `row`<> ").append(form.getRow()).append(" AND result='").append(pending).append("' ");
				sql.append(" AND row<> ").append(form.getRow()).append(" AND result='").append(pending).append("' ");
				// si no hay ningun otro statu pendiente, entonces cerramos
				// ciclo
				if (ToolsHTML.isEmptyOrNull(HandlerDocuments.getField("result", sql.toString(),Thread.currentThread().getStackTrace()[1].getMethodName()))) {
					CierraCiclo = true;
				}
			}
			// obtiene el status de la ultima version mayor de la tabla
			// versiondoc, para compararla con el statuDocAnt del documento,
			// si son iguales y se rechaza, se actualiza la version anterior en
			// el sttau propio del documento a la version
			statuVersion = HandlerDocuments.getFieldMayorStatuVersion("statu", form.getIdDocument());
			String[] values = HandlerDocuments.getFields(new String[] { "number", "statu", "statuAnt", "owner" }, "documents", "numGen",
					String.valueOf(form.getIdDocument()));

			if (values != null) {
				numberxxx = values[0];
				statuDoc = values[1];
				statuDocAnt = values[2];
				// _______________________
				// se usa para activFactoy, generar la URL en caso que sea
				// publicado
				ActFactNumGen = form.getIdDocument();
				ActFactOwner = values[3];
				// _______________________
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		// agarra todos los usuarios pendientes en el flujo
		getNextUserInWF(form.getIdWorkFlow(), form.getRow(), beanNotified);
		String dateExpire = HandlerDocuments.getDateExpiresDoc(form.getIdDocument());
		Vector histReview = (Vector) HandlerDocuments.getAllVersionIdsToDocumentAndStatu(form.getIdDocument(), HandlerDocuments.docReview, false);
		StringBuilder std = new StringBuilder(50);
		std.append("('").append(HandlerDocuments.docApproved).append("',");
		std.append("'").append(HandlerDocuments.docObs).append("')");

		Vector histApproved = (Vector) HandlerDocuments.getAllVersionIdsToDocumentAndStatu(form.getIdDocument(), std.toString(), true);

		versionStatuhist = form.getNumVersion();
		int numVersionObsoBorrador = form.getNumVersion();
		String toImpresion = null;
		CheckOutDocForm version = new CheckOutDocForm();
		version.setNumVersion(String.valueOf(form.getNumVersion()));
		InputStream stream = HandlerStruct.recuperarFileBD(form.getNumVersion());
		try {
			// me busca si el idworkflow es para eliminar ... BUSCA SI ES PARA IMPRIMIR!!!!
			toImpresion = HandlerBD.getField("toImpresion", "workflows", "idworkflow", String.valueOf(form.getIdWorkFlow()), "=", 2,Thread.currentThread().getStackTrace()[1].getMethodName());
			form.setPrintWF(toImpresion.equals("1"));
			HandlerDocuments.loadVersion(version);
		} catch (Exception ex) {
			ex.printStackTrace();
			throw new ApplicationExceptionChecked("E0043");
		}

		log.debug("[setResponseWF:!ToolsHTML.isEmptyOrNull(statuDoc))" + (!ToolsHTML.isEmptyOrNull(statuDoc)));
		if (!ToolsHTML.isEmptyOrNull(statuDoc)) {
			try {
				String[] items = new String[] { "monthExpireDoc", "unitTimeExpire" };
				String[] values = HandlerParameters.getFieldsExpired(items, HandlerDocuments.getTypeDocument(String.valueOf(form.getIdDocument())));
				String monthExpireDoc = values != null ? values[0] : null;
				String unit = values != null ? values[1].trim() : null;
				String numberCompare = null;
				// buscamos el proximo n�mero correlativo en caso que sea
				// borrador y este alla sido aprobado.
				if (CierraCiclo) {
					if ((statuWF && form.getResult().equalsIgnoreCase(wfuAcepted)) && (statuDoc.trim().equalsIgnoreCase(HandlerDocuments.docInApproved))) {
						// conseguimos el numero de incializacion de un borrador
						ResourceBundle rb = ToolsHTML.getBundle(request);
						if ((rb.getString("numcorrelativoinicio").length()) > 9) {
							numberCompare = rb.getString("numcorrelativoinicio").substring(0, 9);
						} else {
							numberCompare = rb.getString("numcorrelativoinicio");
						}
						// si el numero del documento es de un borrador y ha
						// sido aprobado, le asignamos el numero
						if (!ToolsHTML.isEmptyOrNull(numberxxx)) {
							numberxxx = numberxxx.trim();
						}
						if ((ToolsHTML.isEmptyOrNull(numberxxx)) || (numberxxx.compareTo(numberCompare) == 0)) {
							if ("1".compareTo(toImpresion) != 0) {
								// NumCorrelativo =
								// EditDocumentAction.numCorrelativo(form.getIdNode(),form.getIdLocation());
								String numByLocation = null;
								String resetNumber = null;

								numByLocation = String.valueOf(HandlerParameters.PARAMETROS.getNumDocLoc());
								resetNumber = HandlerParameters.PARAMETROS.getResetDocNumber();
								if ("1".compareTo(numByLocation) == 0) {
									// Se Carga la Estructura si la Misma no se
									// encuentra Cargada....
									Hashtable tree = (Hashtable) request.getSession().getAttribute("tree");
									Users usuario = (Users) request.getSession().getAttribute("user");
									tree = ToolsHTML.checkTree(tree, usuario);
									NumCorrelativo = EditDocumentAction
											.numCorrelativo(form.getIdNode(), form.getIdLocation(), numByLocation, resetNumber, tree);
								} else {
									NumCorrelativo = EditDocumentAction
											.numCorrelativo(form.getIdNode(), form.getIdLocation(), numByLocation, resetNumber, null);
								}
							}
						}
					}
				}
				con = JDBCUtil.getConnection(Thread.currentThread().getStackTrace()[1].getMethodName());
				con.setAutoCommit(false);
				Timestamp time = new Timestamp(date.getTime());
				updateUserWorkFlows(con, time, form);
				// Se Verifica que los usuarios anteriores hayan respondido
				// aceptando el Flujo
				// y Adicionalmente se verifica que la respuesta actual tambi�n
				// sea afirmativa
				// Para Cerrar el Flujo de Trabajo

				log.debug("[setResponseWF:CierraCiclo II)" + CierraCiclo);

				if (CierraCiclo) {
					// me indica que el flujo acabo de finalizar
					form.setEnd("1");
					log.debug("[setResponseWF:statuWF&&form.getResult().equalsIgnoreCase(wfuAcepted)]"
							+ (statuWF && form.getResult().equalsIgnoreCase(wfuAcepted)));
					if (statuWF && form.getResult().equalsIgnoreCase(wfuAcepted)) {
						// SIMON 11 DE JULIO 2005 INICIO
						String eliminar = null;
						// me busca si el idworkflow es para eliminar
						eliminar = HandlerBD.getField(con, "eliminar", "workflows", "idworkflow", String.valueOf(form.getIdWorkFlow()), "=", 2,Thread.currentThread().getStackTrace()[1].getMethodName());
						form.setEliminar(eliminar);
						// SIMON 11 DE JULIO 2005 FIN
						// ydavila Ticket 001-00-003023
						String cambiar = null;
						// verifico si es solicitud de cambio
						cambiar = HandlerBD.getField(con, "cambiar", "workflows", "idworkflow", String.valueOf(form.getIdWorkFlow()), "=", 2,Thread.currentThread().getStackTrace()[1].getMethodName());
						form.setCambiar(cambiar != null?cambiar:(String) request.getSession().getAttribute("cambiar")); //GRG  
						updateStatuWF("workflows", form.getIdWorkFlow(), con, wfuAcepted, response, time);
						updateStatuWF("historywf", form.getIdWorkFlow(), con, wfuAcepted, response, time);
						String newState = HandlerDocuments.docReview;   
						log.debug("[statuDoc] = " + statuDoc);
						Timestamp dateExpireEnd = null;
						String lastOpe = HandlerDocuments.lastReview;
						String typeMovHist = Constants.historyRevised;

						// Si el documento fue aprobado, se crea una nueva
						// version.
						if (statuDoc.trim().equalsIgnoreCase(HandlerDocuments.docInApproved)) {
							newState = HandlerDocuments.docApproved;
							lastOpe = HandlerDocuments.lastApproved;
							typeMovHist = Constants.historyApproved;
							// __________________________________________________________________
							// El documento ha sido aprobado, generamos l url de
							// active Factory
							// procesamos wonderware para su actualizacion en
							// activefactory mas adelante con este swiche
							swActFactPubl = true;
							// __________________________________________________________________
							// el documento ha sido aprobado, hay una nueva
							// versi�n, buscamos en la table impresion todos los
							// documentos que han sido impresos para mandar un
							// mail actualizando que hay una nueva version de
							// dicho
							// documento
							// tratamos el documento en linea en caso de ser
							// aprobado la solicitud de impresi�n
							HandlerDocuments.verificarDocImpresosNuevaVersion(con, form.getIdDocument(),
									Integer.parseInt(loadsolicitudImpresion.impresoprintln));
							int value = 0;
							if (ToolsHTML.isNumeric(monthExpireDoc.trim())) {
								value = Integer.parseInt(monthExpireDoc.trim());
							}
							java.util.Date dateExpireCalculated = ToolsHTML.getDateExpireDocument(ToolsHTML.sdf.format(date), null, value, unit);
							if (dateExpireCalculated != null) {
								dateExpireEnd = new Timestamp(dateExpireCalculated.getTime());
							}
							// colocamos la fecha de expiracion actulizada con
							// la fecha en que se aprobo el documento nuevamente
							version.setDateExpires(String.valueOf(dateExpireEnd));

							version.setApproved("0");
							version.setDateApproved(ToolsHTML.sdfShowConvert.format(time));
							if (ToolsHTML.isEmptyOrNull(version.getDateExpires())) {
								version.setDateExpires(ToolsHTML.sdfShowConvert.format(dateExpireEnd));
							}

							if (ToolsHTML.isNumeric(version.getMinorVer())) {
								version.setMinorVer("0");
							} else {
								if (!ToolsHTML.isEmptyOrNull(version.getMinorVer().trim())) {
									// version.setMinorVer("A");
									version.setMinorVer("");
								} else {
									// version.setMinorVer("");
									// version.setMinorVer("A");
									version.setMinorVer("");
								}
							}

							if (ToolsHTML.isNumeric(version.getMayorVer())) {
								int mayorVer = 0;
								if ("0".compareTo(version.getMayorVer().trim()) != 0 || (histApproved != null && histApproved.size() > 0)) {
									mayorVer = Integer.parseInt(version.getMayorVer().trim()) + 1;
								} else {
									try {
										mayorVer = Integer.parseInt(DesigeConf.getProperty("begin.VersionApproved"));
									} catch (Exception ex) {
										mayorVer = 0;
									}
								}
								// int mayorVer =
								// Integer.parseInt(version.getMayorVer().trim())
								// + 1;
								version.setMayorVer(String.valueOf(mayorVer));
							} else {
								if (!isFirstVersion(form.getIdDocument())) {
									version.setMayorVer(ToolsHTML.incVersion(version.getMayorVer().trim()));
								}
							}
							version.setIdDocument(String.valueOf(form.getIdDocument()));
							if (HandlerDocuments.docTrash.equalsIgnoreCase(version.getStatu())
									|| HandlerDocuments.docReview.equalsIgnoreCase(version.getStatu())) {
								HandlerDocuments.updateStatuVersionDoc(form.getNumVersion(), con, newState, time, dateExpireEnd, null, version.getMayorVer(),
										version.getMinorVer(), false, Constants.notPermission, null);
							} else {
								log.debug("Se est� Creando una Nueva Versi�n del Documento");
								version.setStatu(newState);
								version.setTypeWF(Constants.notPermission);
								HandlerDocuments.createNewVersionDoc(version, con, time, stream);
								form.setNumVersion(version.getNumVer());
								log.debug("Se procede a Crear Documentos Enlazados a la Versi�n Anterior...");
								// Si el Documento en la versi�n Actual tiene
								// Documentos Relacionados
								// Se procede a Crear la Relaci�n con la Nueva
								// Versi�n
								if (!ToolsHTML.isEmptyOrNull(docRelations)) {
									log.debug("docRelations " + docRelations);
									log.debug("Document " + version.getIdDocument());
									log.debug("Versi�n  " + version.getNumVer());
									// HandlerStruct.deleteDocumentsLinks(Integer.parseInt(version.getIdDocument()),
									// version.getNumVer(),con);
									HandlerStruct.getPreparedStatementDocumentLinks(version.getIdDocument(), version.getNumVer(), docRelations, con);
								}
							}
							log.debug("Mantenimiento Hist�rico Docs Aprobados" + histApproved.size() + "_" + mayorKeep);
							// Se procede a Mantener actualizadas las versiones
							// mayores
							// seg�n la configuraci�n actual del Sistema
							if (histApproved.size() > 0 && mayorKeep <= histApproved.size()) {
								int limSup = histApproved.size() - mayorKeep;
								// for (int row = 0; row <= limSup; row++) {
								for (int row = 0; row < limSup; row++) {
									HandlerDocuments.deleteLogicVersion(Constants.notPermission, (String) histApproved.get(row), HandlerDocuments.docApproved,
											con);
								}
							}
							// Se cambia el statu de la �ltima versi�n a
							// Obsoleta
							if (histApproved != null && histApproved.size() > 0) {
								HandlerDocuments.updateStatuDoc(HandlerDocuments.docObs, (String) histApproved.get(histApproved.size() - 1),
										HandlerDocuments.docApproved, con);
							}
						} // Si el documento no esta aprobado.
						else {
							if (HandlerDocuments.docApproved.equalsIgnoreCase(version.getStatu())) {
								newState = HandlerDocuments.docReview;
								lastOpe = HandlerDocuments.lastReview;
								typeMovHist = Constants.historyRevised;
								if (dateExpire == null) {
									int value = 0;
									if (ToolsHTML.isNumeric(monthExpireDoc.trim())) {
										value = Integer.parseInt(monthExpireDoc.trim());
									}
									java.util.Date dateExpireCalculated = ToolsHTML.getDateExpireDocument(ToolsHTML.sdf.format(date), null, value, unit);
									if (dateExpireCalculated != null) {
										dateExpireEnd = new Timestamp(dateExpireCalculated.getTime());
									}
								} else {
									try {
										dateExpireEnd = new Timestamp(ToolsHTML.sdf.parse(dateExpire).getTime());
									} catch (Exception ex) {
										dateExpireEnd = new Timestamp(ToolsHTML.sdf1.parse(dateExpire).getTime());
									}
								}
								version.setStatu(newState);
								version.setApproved("1");
								version.setDateApproved(ToolsHTML.sdfShowConvert.format(time));
								if (ToolsHTML.isEmptyOrNull(version.getDateExpires())) {
									version.setDateExpires(ToolsHTML.sdfShowConvert.format(dateExpireEnd));
								}

								if (ToolsHTML.isNumeric(version.getMinorVer())) {
									version.setMinorVer("0");
								} else {
									if (!ToolsHTML.isEmptyOrNull(version.getMinorVer().trim())) {
										// version.setMinorVer("A");
										version.setMinorVer("");
									} else {
										// version.setMinorVer("");
										// version.setMinorVer("A");
										version.setMinorVer("");
									}
								}
								version.setIdDocument(String.valueOf(form.getIdDocument()));
								form.setNumVersion(version.getNumVer());
								// Se procede a Mantener actualizadas las
								// versiones menores
								// seg�n la configuraci�n actual del Sistema
								if (histReview.size() > 0 && minorKeep < histReview.size()) {
									HandlerDocuments.deleteLogicVersion(Constants.notPermission, (String) histReview.get(0), HandlerDocuments.docReview, con);
								}
							} else {
								// Aqui creo q tambi�n se deber�a Actualizar
								// tanto la versi�n mayor como la menor
								form.setNumVersion(version.getNumVer());
								HandlerDocuments.updateStatuVersionDoc(form.getNumVersion(), con, newState, time, dateExpireEnd, null, null, null, false,
										Constants.notChange, null);
							}
						}
						HandlerDocuments.updateStateDoc(con, form.getIdDocument(), newState, statuDoc, lastOpe, form.getNumVersion(), time);
						// si es borrador y es aprobado, colocamos el numero
						// correlativo especifico al documento.
						if (((numberxxx.equalsIgnoreCase(numberCompare) || (ToolsHTML.isEmptyOrNull(numberxxx))) && (newState
								.equalsIgnoreCase(HandlerDocuments.docApproved)))) {
							// si no es un documento en linea con solicitud de
							// impresion
							if (!"1".equalsIgnoreCase(toImpresion)) {
								HandlerDocuments.updateNumCorrelativo(con, NumCorrelativo, form.getIdDocument());
							}
						}
						// tratamos el documento en linea en caso de ser
						// aprobado la solicitud de impresion
						if ("1".equalsIgnoreCase(toImpresion)) {
							HandlerDocuments.updatetblSolicitudImpresion(con, numberxxx, form.getIdDocument(),
									Integer.parseInt(loadsolicitudImpresion.aprobadoprintln), Integer.parseInt(loadsolicitudImpresion.aprobadoprintln));
						}
						// si es obsoleto y el flujo de revision es aceptado,
						// actualizamos la version a borrador

						// TODO ydavila Ticket 000-00-003155
						if (form.isPrintWF() == false) {
							if (statuVersion.equalsIgnoreCase(HandlerDocuments.docObs) || statuDocAnt.equalsIgnoreCase(HandlerDocuments.docObs)) {
								HandlerDocuments.updateStatuVersionDocObsoletoAborrador(numVersionObsoBorrador, con, HandlerDocuments.docTrash);
							}

							// 25 DE JULIO 2005 FIN
							// int idMovement =
							// IDDBFactorySql.getNextID("changeswf");
							int idMovement = HandlerStruct.proximo("changeswf", "changeswf", "idMovement");
							st = con.prepareStatement(JDBCUtil.replaceCastMysql(getQueryChangesWFs(idMovement, form.getIdWorkFlow(), Integer.parseInt(newState))));
							st.setTimestamp(1, time);
							st.executeUpdate();
							updateWFHistory(con, form.getIdWorkFlow(), form.getNameUser(), time, typeMovHist, form.getIdDocument());
							// ydavila la siguiente llave cierra el if de isPrintWF
						}
						// ydavila Ticket 001-00-003023 Antes de finalizar el flujo de Solicitud de Cambio regresamos el documento a estatus aprobado
						// porque a pesar de que es un flujo de revisi�n, el documento no debe quedar estatus REVISADO sino APROBADO
						if ("1".equals(cambiar)) {
							HandlerDocuments.updateStatDocSolCambio(con, form.getIdDocument(), HandlerDocuments.docApproved, statuDoc);
						}
						;
						// lleva el historico de si fue revisado o aprobado para
						// cuaquier version del documento
						HandlerDocuments.updateStatuhistVersionDoc(versionStatuhist, con);
					} else {
						// Se Cancela el Flujo de Trabajo
						updateStatuWF("workflows", form.getIdWorkFlow(), con, wfuCanceled, canceled, time);
						// updateUserWFStatu(wfuCanceled,canceled,form.getIdWorkFlow(),con);
						HandlerDocuments.updateStateDoc(con, form.getIdDocument(), statuDocAnt, statuDocAnt, HandlerDocuments.lastCanceledWF, 0, null);
						canceledUserWorkFlows(wfuPending, con, form);
						canceledUserWorkFlows(wfuQueued, con, form);
						// NOTA:Historico de flujo de cancelado
						updateWFHistory(con, form.getIdWorkFlow(), form.getNameUser(), time, canceled, form.getIdDocument());
						// si es un documento en linea de solicitud de impresion
						// ... este es cancelado
						if ("1".equalsIgnoreCase(toImpresion)) {
							HandlerDocuments.updatetblSolicitudImpresion(con, numberxxx, form.getIdDocument(),
									Integer.parseInt(loadsolicitudImpresion.rechazadoprintln), Integer.parseInt(loadsolicitudImpresion.rechazadoprintln));
						}

					}
				} else {
					// Si el Flujo de Trabajo es Condicional y fu� rechazado o
					// es un flujo de Aprobaci�n
					// se cancela el mismo
					if ((wfConditional.equalsIgnoreCase(form.getConditional()) || statuDoc.trim().equalsIgnoreCase(HandlerDocuments.docInApproved))
							&& wfuCanceled.equalsIgnoreCase(form.getResult())) {
						if (wfConditional.equalsIgnoreCase(form.getConditional())) {
							// Se Cancela el Flujo de Trabajo
							updateStatuWF("workflows", form.getIdWorkFlow(), con, wfuCanceled, canceled, time);
							// Se cancela el Flujo para los Usuarios pendientes
							canceledUserWorkFlows(wfuPending, con, form);
							canceledUserWorkFlows(wfuQueued, con, form);
							updateWFHistory(con, form.getIdWorkFlow(), form.getNameUser(), time, canceled, form.getIdDocument());
							// actaulizamos el status rechazado en el documento
							// durante el flujo
							actualizaDocFlowRechazo(form, statuDoc, con, statuVersion, statuDocAnt);
							// ...................es condicional y rechazamos
							// tdoo...............................................................
						} else {
							// si esta en aprobacion y es rechazado...
							if (statuDoc.trim().equalsIgnoreCase(HandlerDocuments.docInApproved)) {
								// ...................esta en aprobacion y
								// rechazamos
								// tdoo...............................................................
								// Se Cancela el Flujo de Trabajo
								updateStatuWF("workflows", form.getIdWorkFlow(), con, wfuCanceled, canceled, time);
								// Se cancela el Flujo para los Usuarios
								// pendientes
								canceledUserWorkFlows(wfuPending, con, form);
								canceledUserWorkFlows(wfuQueued, con, form);
								updateWFHistory(con, form.getIdWorkFlow(), form.getNameUser(), time, canceled, form.getIdDocument());
								// actaulizamos el status rechazado en el
								// documento durante el flujo
								actualizaDocFlowRechazo(form, statuDoc, con, statuVersion, statuDocAnt);
							} else {
								// ...............no es condicional y rechazamos
								// uno
								// solo..............................................................
								// solo actualizamos el workflow en rechazo y
								// continuamos porque
								// el wf no es no condicional pero esta en
								// revision
								updateUserWorkFlows(con, time, form);
								// updateWFHistory(con,form.getIdWorkFlow(),form.getNameUser(),time,canceled);

							}
						}

					} else {
						if (wfSecuential.equalsIgnoreCase(form.getSecuential())) {
							updateUserWorkFlows(con, wfuPending, beanNotified.getRow());
						} else {
							if (beanNotified.isOwnerWF()) {
								updateUserWorkFlows(con, wfuPending, beanNotified.getRow());
							}
						}
						if (!wfConditional.equalsIgnoreCase(form.getConditional())) {
							// solo actualizamos el workflow en rechazo y
							// continuamos porque
							// el wf no es condicional
							updateUserWorkFlows(con, time, form);
							// updateWFHistory(con,form.getIdWorkFlow(),form.getNameUser(),time,canceled);
						}
					}
				}
				// ydavila Ticket 001-00-003023
				// Para el caso de Solicitud de Cambio, a pesar de que el documento se bloquea en un flujo de revisi�n,
				// el documento no debe quedar en estatus revisado. No se puede cambiar al inicio del bloque porque se interrummpe otros procesos
				// por eso se coloca la actualizaci�n en este punto, justo antes del commit
				if ("1".equals(form.getCambiar())) {
					HandlerDocuments.updateStatDocSolCambio(con, form.getIdDocument(), HandlerDocuments.docApproved, HandlerDocuments.docInReview);
					HandlerDocuments.updateStatuhistVersionDoc(form.getIdDocument(), con);
				}
				con.commit();
			} catch (Exception ex) {
				ex.printStackTrace();
				applyRollback(con);
				throw new ApplicationExceptionChecked("E0017");
			} finally {
				setFinally(con, st);
			}
		}
		try {
			// __________________________________________________________________
			// El documento ha sido aprobado, generamos l url de active Factory
			// procesamos wonderware para su actualizacion en activefactory
			if (swActFactPubl) {
				BaseDocumentForm forma = new BaseDocumentForm();
				forma.setIdDocument(String.valueOf(ActFactNumGen));
				forma.setNumberGen(String.valueOf(ActFactNumGen));
				HandlerStruct.loadDocument(forma, true, true, null, request);
				EditDocumentAction actActvefactory = new EditDocumentAction();
				Users usuario = new Users(); // getUserSession();
				usuario.setNameUser(ActFactOwner);
				// logica negada, cero significa que vamos a insertar o
				// modificar si existe en activeFactory
				forma.setCheckactiveFactory("0");
				if (Constants.ACTIVE_FACTORY) {
					actActvefactory.actualizarActiveFactory(forma, request, usuario);
				}
			}

			// __________________________________________________________________
		} catch (Exception e) {
			e.printStackTrace();
		}
		return resp;
	}

	public static boolean isFirstVersion(int numDoc) throws SQLException, Exception {
		StringBuilder query = new StringBuilder();
		ArrayList<Object> parametros = new ArrayList<Object>();

		parametros.add(new Integer(numDoc));
		query.append("SELECT numDoc FROM versiondoc WHERE numDoc=? and dateApproved IS NOT NULL");

		return !JDBCUtil.executeQuery(query, parametros, Thread.currentThread().getStackTrace()[1].getMethodName()).next();
	}

	// 27 DE JULIO 2005 SIMON FIN

	public static void actualizaDocFlowRechazo(ParticipationForm form, String statuDoc, Connection con, String statuVersion, String statuDocAnt) {
		try {
			// ........actualizaDocFlowRechazoactualizaDocFlowRechazo.......................
			// Se actualiza el estado del documento
			// actualizamos el documento en cancelado y era un flujo de revision
			HandlerDocuments.updateStateDoc(con, form.getIdDocument(), statuDocAnt, statuDocAnt, HandlerDocuments.lastCanceledWF, 0, null);
			// Si el documento estaba en un flujo de Aprobaci�n se coloca como
			// Rechazado
			if (statuDoc.trim().equalsIgnoreCase(HandlerDocuments.docInApproved)) {
				if (statuVersion.equalsIgnoreCase(statuDocAnt)) {
					// ........actualizaDocFlowRechazoactualizaDocFlowRechazo.......II................
					HandlerDocuments.updateStatuVersionDoc(form.getNumVersion(), con, statuDocAnt, null, null, null, null, null, false, Constants.notChange, null);
				}

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static synchronized boolean changeStatuWF(Timestamp time, DataUserWorkFlowForm forma, String msg, String resultUser, String statuUser, int rowUser,
			String comentsUser, String lastOperation, String email, String nameMail, String titleMail, String userName, String msgOwner)
			throws ApplicationExceptionChecked {
		Connection con = null;
		boolean resp = false;
		MailForm formaMail = new MailForm();
		MailForm formaMailOwner = null;
		String titleMail1 = titleMail;
		Locale defaultLocale = new Locale(DesigeConf.getProperty("language.Default"), DesigeConf.getProperty("country.Default"));
		ResourceBundle rb = ResourceBundle.getBundle("LoginBundle", defaultLocale);
		try {
			String users = "";
			titleMail = titleMail1 + " - " + rb.getString("wf.wfStatu2");
			if (resultUser != null && statuUser != null && HandlerWorkFlows.expires.equals(resultUser) && HandlerWorkFlows.wfuExpired.equals(statuUser)) {
				String titleMail2 = titleMail1 + " - " + rb.getString("wf.subjectExpire");
				// Expir� el flujo. Solo se env�a correo a los usuarios en
				// status y resultado Pendiente
				users = getAllUsersInWF(forma.getIdWorkFlow(), HandlerWorkFlows.pending, HandlerWorkFlows.wfuPending);
				// Se env�a correo al generador del flujo
				String emailOwner = "";
				Collection cOwner = getOwnerUserInWorkFlow(forma.getIdWorkFlow() + "");
				if (cOwner != null) {
					Iterator it = cOwner.iterator();
					if (it.hasNext()) {
						ParticipationForm user = (ParticipationForm) it.next();
						emailOwner = user.getEmail();
					}
				}
				formaMailOwner = new MailForm();
				formaMailOwner.setUserName(userName);
				formaMailOwner.setFrom(email);
				formaMailOwner.setNameFrom(nameMail);
				formaMailOwner.setTo(emailOwner);
				formaMailOwner.setSubject(titleMail2);
				formaMailOwner.setMensaje(msgOwner.toString());
			} else {
				// Se env�a correo a todos los usuarios del flujo
				users = getAllUsersInWF(forma.getIdWorkFlow());
			}
			ArrayList emailsUsers = new ArrayList();
			HandlerGrupo.getEmailForUsers(users, emailsUsers);
			con = JDBCUtil.getConnection(Thread.currentThread().getStackTrace()[1].getMethodName());
			String statuDocAnt = HandlerDocuments.getFieldToDocument("statuAnt", forma.getIdDocument());
			con.setAutoCommit(false);
			ParticipationForm form = new ParticipationForm();
			form.setCommentsUser(msg);
			form.setResult(resultUser);
			form.setStatu(Integer.parseInt(statuUser));
			form.setIdDocument(forma.getIdDocument());
			form.setIdWorkFlow(forma.getIdWorkFlow());
			form.setRow(-1);
			canceledUserWorkFlows(wfuPending, con, form);
			canceledUserWorkFlows(wfuQueued, con, form);
			if (rowUser != 0) {
				form.setRow(rowUser);
				form.setCommentsUser(comentsUser);
				updateUserWorkFlows(con, time, form);
			}
			updateStatuWF("workflows", forma.getIdWorkFlow(), con, resultUser, statuUser, time);
			// Se actualiza el estado del documento
			HandlerDocuments.updateStateDoc(con, form.getIdDocument(), statuDocAnt, statuDocAnt, lastOperation, 0, null);
			updateWFHistory(con, form.getIdWorkFlow(), form.getNameUser(), time, resultUser, form.getIdDocument());
			// Se cancela el Flujo para los Usuarios pendientes
			// updateUserWFStatu(wfuExpired,expires,form.getIdWorkFlow(),con);
			StringBuilder usuarios = new StringBuilder("");
			for (int row = 0; row < emailsUsers.size(); row++) {
				usuarios.append(emailsUsers.get(row)).append(";");
			}
			if (usuarios.length() > 0) {
				usuarios.deleteCharAt(usuarios.length() - 1);
			}

			formaMail.setUserName(userName);
			formaMail.setFrom(email);
			formaMail.setNameFrom(nameMail);
			formaMail.setTo(usuarios.toString());
			formaMail.setSubject(titleMail);
			formaMail.setMensaje(msg);

			con.commit();
			resp = true;
		} catch (ApplicationExceptionChecked ae) {
			ae.printStackTrace();
			throw ae;
		} catch (Exception ex) {
			applyRollback(con);
			ex.printStackTrace();
			throw new ApplicationExceptionChecked("E0046");
		} finally {
			setFinally(con);
		}
		try {
			// SendMailAction.send(formaMail);
			SendMailTread mail = new SendMailTread(formaMail);
			mail.start();
			if (formaMailOwner != null) {
				SendMailTread mail2 = new SendMailTread(formaMailOwner);
				mail2.start();
			}
		} catch (Exception ex) {
			log.debug("Exception");
			ex.printStackTrace();
		}
		return resp;
	}

	public static synchronized boolean canceledFlexFlow(Timestamp time, DataUserWorkFlowForm forma, String msg, String resultUser, String statuUser,
			int rowUser, String comentsUser, String lastOperation, String email, String nameMail, String titleMail, String userName, String msgOwner)
			throws ApplicationExceptionChecked {
		Connection con = null;
		boolean resp = false;
		MailForm formaMail = null;
		try {
			if (resultUser != null && statuUser != null && !HandlerWorkFlows.expires.equals(resultUser) && !HandlerWorkFlows.wfuExpired.equals(statuUser)) {
				Collection users = getAllUserInFlexFlow(forma.getIdFlexFlow(), null);
				// HandlerGrupo.getEmailForUsers(users,emailsUsers);
				con = JDBCUtil.getConnection(Thread.currentThread().getStackTrace()[1].getMethodName());
				// Se obtiene el Statu del Documento antes de ser sometido al
				// Flujo de Trabajo.
				String statuDocAnt = forma.getStatuAnt();// HandlerDocuments.getFieldToDocument("statuAnt",forma.getIdDocument());
				con.setAutoCommit(false);
				ParticipationForm form = new ParticipationForm();
				form.setCommentsUser(msg);
				form.setResult(resultUser);
				form.setStatu(Integer.parseInt(statuUser));
				form.setIdDocument(forma.getIdDocument());
				form.setIdWorkFlow(forma.getIdWorkFlow());
				form.setRow(-1);
				// Cancelado el Flujo para los Usuarios participantes en el
				// mismo
				canceledUserFlexFlows(wfuPending + "," + wfuQueued, con, form);
				// canceledUserFlexFlows(wfuQueued,con,form);
				if (rowUser != 0) {
					form.setRow(rowUser);
					form.setCommentsUser(comentsUser);
				}
				// Se Guarda el Comentario de qui�n Origina el Flujo de Trabajo
				updateUserFlexFlows(con, time, form);
				// Se Cancela el Flujo de Trabajo Param�trico
				updateStatuFlexFlow(forma.getIdFlexFlow(), con, resultUser, statuUser, time);
				// Se actualiza el estado del documento
				HandlerDocuments.updateStateDoc(con, form.getIdDocument(), statuDocAnt, statuDocAnt, lastOperation, 0, null);
				// Se actualiza el Hist�rico
				log.debug("Name User: " + form.getNameUser());
				updateWFHistoryFlexFlow(con, forma.getIdFlexFlow(), userName, time, resultUser, form.getIdDocument(), form.getStatu(), form.getResult());
				// updateWFHistoryFlexFlow(con,form.getIdFlexFlow(),form.getNameUser(),time,String.valueOf(form.getNumAct()));

				StringBuilder usuarios = new StringBuilder("");
				for (Iterator iterator = users.iterator(); iterator.hasNext();) {
					ParticipationForm participationForm = (ParticipationForm) iterator.next();
					usuarios.append(participationForm.getEmail()).append(";");
				}
				if (usuarios.length() > 0) {
					usuarios.deleteCharAt(usuarios.length() - 1);
				}
				log.debug("Usuarios: " + usuarios.toString());
				formaMail = new MailForm();
				formaMail.setUserName(userName);
				formaMail.setFrom(email);
				formaMail.setNameFrom(nameMail);
				formaMail.setTo(usuarios.toString());
				formaMail.setSubject(titleMail);
				formaMail.setMensaje(msg);
				con.commit();
				resp = true;
			}
		} catch (ApplicationExceptionChecked ae) {
			ae.printStackTrace();
			throw ae;
		} catch (Exception ex) {
			applyRollback(con);
			ex.printStackTrace();
			throw new ApplicationExceptionChecked("E0046");
		} finally {
			setFinally(con);
		}
		try {
			// SendMailAction.send(formaMail);
			if (formaMail != null) {
				SendMailTread mail = new SendMailTread(formaMail);
				mail.start();
			}
		} catch (Exception ex) {
			log.debug("Exception");
			log.error(ex.getMessage());
			ex.printStackTrace();
		}
		return resp;
	}

	public static synchronized boolean expiredFlexFlow(Timestamp time, DataUserWorkFlowForm forma, String msg, String resultUser, String statuUser,
			int rowUser, String comentsUser, String lastOperation, String email, String nameMail, String titleMail, String userName, String msgOwner)
			throws ApplicationExceptionChecked {
		Connection con = null;
		boolean resp = false;
		MailForm formaMail = null;
		MailForm formaMailOwner = null;
		StringBuilder usuarios = new StringBuilder();
		Locale defaultLocale = new Locale(DesigeConf.getProperty("language.Default"), DesigeConf.getProperty("country.Default"));
		ResourceBundle rb = ResourceBundle.getBundle("LoginBundle", defaultLocale);
		String titleMail1 = titleMail;
		titleMail = titleMail + " - " + rb.getString("wf.wfStatu2");
		try {
			if (resultUser != null && statuUser != null && HandlerWorkFlows.expires.equals(resultUser) && HandlerWorkFlows.wfuExpired.equals(statuUser)) {
				String titleMail2 = titleMail1 + " - " + rb.getString("wf.subjectExpire");
				// Expir� el flujo. Solo se env�a correo a los usuarios en
				// status y resultado Pendiente
				String users = getAllUsersInWFP(forma.getIdWorkFlow(), HandlerWorkFlows.pending, HandlerWorkFlows.wfuPending, true);
				if (!ToolsHTML.isEmptyOrNull(users)) {
					// Se env�a correo al generador del flujo
					String emailOwner = "";
					Collection cOwner = getOwnerUserInFlexWorkFlow(forma.getIdWorkFlow() + "");
					if (cOwner != null) {
						Iterator it = cOwner.iterator();
						if (it.hasNext()) {
							ParticipationForm user = (ParticipationForm) it.next();
							emailOwner = user.getEmail();
						}
					}
					formaMailOwner = new MailForm();
					formaMailOwner.setUserName(userName);
					formaMailOwner.setFrom(email);
					formaMailOwner.setNameFrom(nameMail);
					formaMailOwner.setTo(emailOwner);
					formaMailOwner.setSubject(titleMail2);
					formaMailOwner.setMensaje(msgOwner.toString());
					ArrayList emailsUsers = new ArrayList();
					HandlerGrupo.getEmailForUsers(users, emailsUsers);
					for (int row = 0; row < emailsUsers.size(); row++) {
						usuarios.append(emailsUsers.get(row)).append(";");
					}

					if (usuarios.length() > 0) {
						usuarios.deleteCharAt(usuarios.length() - 1);
					}

					// HandlerGrupo.getEmailForUsers(users,emailsUsers);
					con = JDBCUtil.getConnection(Thread.currentThread().getStackTrace()[1].getMethodName());
					// Se obtiene el Statu del Documento antes de ser sometido
					// al Flujo de Trabajo
					String statuDocAnt = HandlerDocuments.getFieldToDocument("statuAnt", forma.getIdDocument());
					con.setAutoCommit(false);
					ParticipationForm form = new ParticipationForm();
					form.setCommentsUser(msg);
					form.setResult(resultUser);
					form.setStatu(Integer.parseInt(statuUser));
					form.setIdDocument(forma.getIdDocument());
					form.setIdWorkFlow(forma.getIdWorkFlow());
					form.setIdFlexFlow(forma.getIdFlexFlow());
					form.setRow(-1);

					// Se expiran los usuarios de la actividad actual, que
					// tengan status y result Pendiente
					expiredUsersFlexFlow(con, form.getIdWorkFlow());
					// Se expira la actividad
					expiredFlexFlowsAct(con, form.getIdWorkFlow(), time);
					// Se anulan los usuarios de las siguientes actividades y
					// los que estan en cola de la actividad actual
					annulledUserFlexFlowsOtherAct(wfuQueued + "," + wfuPending, con, form);
					// Se anulan las siguientes actividades
					annulledFlexFlowsOtherAct(pending + "," + inQueued, con, form, time);

					// Se actualiza el estado del documento (ya no esta en FTP)
					HandlerDocuments.updateStateDoc(con, form.getIdDocument(), statuDocAnt, statuDocAnt, lastOperation, 0, null);

					// Se actualiza el Hist�rico
					log.debug("Name User: " + form.getNameUser());
					updateWFHistoryFlexFlow(con, forma.getIdFlexFlow(), userName, time, resultUser, form.getIdDocument(), form.getStatu(), form.getResult());
					// updateWFHistoryFlexFlow(con,form.getIdFlexFlow(),form.getNameUser(),time,String.valueOf(form.getNumAct()));

					log.debug("Usuarios: " + usuarios.toString());
					formaMail = new MailForm();
					formaMail.setUserName(userName);
					formaMail.setFrom(email);
					formaMail.setNameFrom(nameMail);
					formaMail.setTo(usuarios.toString());
					formaMail.setSubject(titleMail);
					formaMail.setMensaje(msg);
					con.commit();
				}
			}
			resp = true;
		} catch (ApplicationExceptionChecked ae) {
			ae.printStackTrace();
			throw ae;
		} catch (Exception ex) {
			applyRollback(con);
			ex.printStackTrace();
			throw new ApplicationExceptionChecked("E0046");
		} finally {
			setFinally(con);
		}
		try {
			// SendMailAction.send(formaMail);
			if (formaMail != null) {
				SendMailTread mail = new SendMailTread(formaMail);
				mail.start();
			}
			if (formaMailOwner != null) {
				SendMailTread mail2 = new SendMailTread(formaMailOwner);
				mail2.start();
			}
		} catch (Exception ex) {
			log.debug("Exception");
			log.error(ex.getMessage());
			ex.printStackTrace();
		}
		return resp;
	}

	public static synchronized boolean completedFlexFlow(Timestamp time, DataUserWorkFlowForm forma, String msg, String resultUser, String statuUser,
			int rowUser, String comentsUser, String lastOperation, String email, String nameMail, String titleMail, String userName, HttpServletRequest request)
			throws ApplicationExceptionChecked {
		Connection con = null;
		boolean resp = false;
		MailForm formaMail = new MailForm();

		try {
			// Obtengo los usuarios de FTP
			Collection users = getAllUserInFlexFlow(forma.getIdWorkFlow(), null);
			// HandlerGrupo.getEmailForUsers(users,emailsUsers);

			// / ini 1ra parte actualizar la nueva version
			Users usuario = (Users) request.getSession().getAttribute("user");
			if (usuario == null || !HandlerDBUser.isValidSessionUser(usuario.getUser(), request.getSession())) {
				throw new ApplicationExceptionChecked("E0035");
			}

			BeanNotifiedWF beanNotified = new BeanNotifiedWF();
			BaseDocumentForm docForm = new BaseDocumentForm();
			String idDoc = String.valueOf(forma.getIdDocument());
			docForm.setIdDocument(idDoc);
			docForm.setNumberGen(idDoc);

			Hashtable tree = (Hashtable) request.getSession().getAttribute("tree");
			if (tree == null) {
				// Hashtable security =
				// HandlerGrupo.getAllSecurityForGroupH(usuario.getIdGroup(),null);
				// HandlerDBUser.getAllSecurityForUserH(usuario.getIdPerson(),security);
				// tree =
				// HandlerStruct.loadAllNodesH(security,usuario.getIdGroup());
				Users usr = new Users();
				usr.setIdGroup(DesigeConf.getProperty("application.admon"));
				usr.setUser(usuario.getUser());
				tree = ToolsHTML.checkTree(null, usr);
			}
			HandlerStruct.loadDocument(docForm, true, false, tree, request);
			String idLocation = HandlerStruct.getIdLocationToNode(tree, docForm.getIdNode());
			int minorKeep = 0;
			int mayorKeep = 0;
			if (idLocation != null) {
				// BaseStructForm localidad = (BaseStructForm)tree.get(new
				// Long(idLocation));
				BaseStructForm localidad = (BaseStructForm) tree.get(idLocation);
				if (localidad != null) {
					if (ToolsHTML.isNumeric(localidad.getMinorKeep())) {
						minorKeep = Integer.parseInt(localidad.getMinorKeep().trim());
					}
					if (ToolsHTML.isNumeric(localidad.getMajorKeep())) {
						mayorKeep = Integer.parseInt(localidad.getMajorKeep().trim());
					}
				}
			}

			// / fin 1ra parte actualizar nueva version

			con = JDBCUtil.getConnection(Thread.currentThread().getStackTrace()[1].getMethodName());
			// Se obtiene el Statu del Documento antes de ser sometido al Flujo
			// de Trabajo
			String statuDocAnt = forma.getStatuAnt();// HandlerDocuments.getFieldToDocument("statuAnt",forma.getIdDocument());
			con.setAutoCommit(false);
			ParticipationForm form = new ParticipationForm();
			form.setCommentsUser(msg);
			form.setResult(resultUser);
			// form.setResult(usuario.getUser());

			form.setStatu(Integer.parseInt(statuUser));
			form.setIdDocument(forma.getIdDocument());
			form.setIdWorkFlow(forma.getIdWorkFlow());
			form.setRow(-1);

			// Completamos el Flujo para el usuario participante en el mismo

			// completedUserFlexWorkFlows(wfuPending + "," + wfuQueued, con,
			// time, form, usuario);
			// Anulamos los los Usuarios restantes, participantes en el flujo
			AnnulledUserFlexFlows(wfuPending + "," + wfuQueued, con, form);
			// canceledUserFlexFlows(wfuQueued,con,form);
			if (rowUser != 0) {
				form.setRow(rowUser);
				form.setCommentsUser(comentsUser);
				// Como guardar el comentario de la cancelaci�n del Flujo?
				// updateUserWorkFlows(con,time,form);
			}
			// Se Guarda el Comentario de qui�n Origina el Flujo de Trabajo y
			// actualizamos el status de anulado
			annulledUserFlexFlowsCompleted(forma.getIdFlexFlow(), con, time, form);
			// Se Cancela el Flujo de Trabajo Param�trico
			updateStatuFlexFlow(forma.getIdFlexFlow(), con, resultUser, statuUser, time);
			// Se actualiza el estado del documento
			HandlerDocuments.updateStateDoc(con, form.getIdDocument(), statuDocAnt, statuDocAnt, lastOperation, 0, null);
			// Se actualiza el Hist�rico
			log.debug("Name User: " + form.getNameUser());
			updateWFHistoryFlexFlow(con, forma.getIdFlexFlow(), userName, time, Constants.historyCompleted, form.getIdDocument(), form.getStatu(),
					form.getResult());
			// updateWFHistoryFlexFlow(con,form.getIdFlexFlow(),form.getNameUser(),time,String.valueOf(form.getNumAct()));

			StringBuilder usuarios = new StringBuilder("");
			for (Iterator iterator = users.iterator(); iterator.hasNext();) {
				ParticipationForm participationForm = (ParticipationForm) iterator.next();
				usuarios.append(participationForm.getEmail()).append(";");
			}

			// colocamos al generador del flujo dentro de la lista de correos
			Collection cOwner = getOwnerUserInFlexWorkFlow(con, String.valueOf(forma.getIdWorkFlow()));
			if (cOwner != null) {
				Iterator it = cOwner.iterator();
				if (it.hasNext()) {
					ParticipationForm user = (ParticipationForm) it.next();
					usuarios.append(user.getEmail()).append(";");
				}
			}

			if (usuarios.length() > 0) {
				usuarios.deleteCharAt(usuarios.length() - 1);
			}
			log.debug("Usuarios: " + usuarios);
			formaMail.setUserName(userName);
			formaMail.setFrom(email);
			formaMail.setNameFrom(nameMail);
			// System.out.println(usuarios.toString());
			formaMail.setTo(usuarios.toString());
			formaMail.setSubject(titleMail);
			formaMail.setMensaje(msg);

			con.commit();

			// / fin 2ra parte actualizar nueva version
			form.setEliminar("0");
			form.setCambiar("0");
			form.setEnd("0");
			form.setIdLocation(idLocation);
			form.setIdNode(docForm.getIdNode());
			form.setNumVersion(docForm.getNumVer());
			form.setStatuDoc(HandlerDocuments.inFlewFlow);
			form.setCompleteFlexFlow(true);

			HandlerWorkFlows.saveResponseUser(form, beanNotified, mayorKeep, request, usuario.getUser());
			// / fin 2ra parte actualizar nueva version

			resp = true;
		} catch (ApplicationExceptionChecked ae) {
			applyRollback(con);
			ae.printStackTrace();
			throw ae;
		} catch (Exception ex) {
			applyRollback(con);
			ex.printStackTrace();
			throw new ApplicationExceptionChecked("E0046");
		} finally {
			setFinally(con);
		}
		try {
			// Aqui se envian los mail a los usuarios involucrados en el flujo
			if (!ToolsHTML.isEmptyOrNull(formaMail.getTo()) || !ToolsHTML.isEmptyOrNull(formaMail.getCc())) {
				// SendMailAction.send(formaMail);
				SendMailTread mail = new SendMailTread(formaMail);
				mail.start();
			}
		} catch (Exception ex) {
			log.debug("Exception");
			log.error(ex.getMessage());
			ex.printStackTrace();
		}
		return resp;
	}

	/**
	 * Para Completar a los Usuarios Pendientes en el Flujo de Trabajo Param�trico
	 *
	 * @param statuToChange
	 * @param con
	 * @param form
	 * @throws Exception
	 */
	private static void AnnulledUserFlexFlows(String statuToChange, Connection con, ParticipationForm form) throws Exception {
		PreparedStatement st = null;
		StringBuilder update = new StringBuilder("UPDATE user_flexworkflows SET result = ? ");
		update.append(",statu = ?, comments=null ");
		if (wfuPending.equalsIgnoreCase(statuToChange)) {
			update.append(",pending = ").append(Constants.permission);
		}

		update.append(" WHERE idWorkFlow IN (SELECT idWorkFlow FROM flexworkflow WHERE IDFlexFlow = ").append(form.getIdFlexFlow()).append(") ");
		update.append(" AND statu IN ('").append(statuToChange.replaceAll(",", "','")).append("') ");

		log.debug("[AnnulledUserWorkFlows] Row = " + form.getRow());
		if (form.getRow() > 0) {
			update.append(" AND idFlexWF > ").append(form.getRow());
		}
		update.append(" AND result != '").append(response).append("' ");
		update.append(" AND statu != '").append(response).append("' ");

		st = con.prepareStatement(JDBCUtil.replaceCastMysql(update.toString()));
		log.debug("[AnnulledUserWorkFlows]: " + update.toString());

		st.setString(1, wfuAnnulled);
		st.setString(2, wfuAnnulled);
		// st.setLong(3,form.getIdFlexFlow());
		st.executeUpdate();
	}

	/**
	 * Para Anular a los Usuarios Pendientes en el Flujo de Trabajo Param�trico, de actividad(es) posterior(es)
	 *
	 * @param statuToChange
	 * @param con
	 * @param form
	 * @throws Exception
	 */
	private static void annulledUserFlexFlowsOtherAct(String statuToChange, Connection con, ParticipationForm form) throws Exception {
		PreparedStatement st = null;
		StringBuilder update = new StringBuilder("UPDATE user_flexworkflows SET result = ? ");
		update.append(",statu = ?, comments=null ");
		update.append(" WHERE idWorkFlow >= ").append(form.getIdWorkFlow());
		update.append(" AND idWorkFlow IN (SELECT idWorkFlow FROM flexworkflow WHERE IDFlexFlow = ").append(form.getIdFlexFlow()).append(") ");
		update.append(" AND statu IN ('").append(statuToChange.replaceAll(",", "','")).append("') ");
		update.append(" AND result != '").append(response).append("' ");
		update.append(" AND statu != '").append(response).append("' ");
		st = con.prepareStatement(JDBCUtil.replaceCastMysql(update.toString()));
		log.debug("[annulledUserFlexFlowsOtherAct]: " + update.toString());
		// System.out.println("[-----annulledUserFlexFlowsOtherAct]: " +
		// update.toString());
		st.setString(1, wfuAnnulled);
		st.setString(2, wfuAnnulled);
		st.executeUpdate();
	}

	/**
	 * Para Expirar a los Usuarios Pendientes en el Flujo de Trabajo Param�trico que estaban pendiente. Para actividad expirada
	 *
	 * @param statuToChange
	 * @param con
	 * @param form
	 * @throws Exception
	 */
	private static void expiredUsersFlexFlow(Connection con, int idWorkFlow) throws Exception {
		PreparedStatement st = null;
		StringBuilder update = new StringBuilder("UPDATE user_flexworkflows SET result = ? ");
		update.append(",statu = ?, comments=null ");
		update.append(" WHERE idWorkFlow = ").append(idWorkFlow);
		update.append(" AND statu = '").append(wfuPending).append("' ");
		update.append(" AND result = '").append(pending).append("' ");
		st = con.prepareStatement(JDBCUtil.replaceCastMysql(update.toString()));
		log.debug("[expiredUsersFlexFlow]: " + update.toString());
		st.setString(1, expires);
		st.setString(2, wfuExpired);
		st.executeUpdate();
	}

	private static void annulledFlexFlowsOtherAct(String statuToChange, Connection con, ParticipationForm form, Timestamp time) throws Exception {
		PreparedStatement st = null;
		StringBuilder update = new StringBuilder("UPDATE flexworkflow SET result = ? ");
		update.append(",statu = ?, dateCompleted = ? ");
		update.append(" WHERE idWorkFlow > ").append(form.getIdWorkFlow());
		update.append(" AND IDFlexFlow = ").append(form.getIdFlexFlow()).append(" ");
		update.append(" AND statu IN ('").append(statuToChange.replaceAll(",", "','")).append("') ");
		update.append(" AND result != '").append(response).append("' ");
		update.append(" AND statu != '").append(response).append("' ");
		st = con.prepareStatement(JDBCUtil.replaceCastMysql(update.toString()));
		log.debug("[annulledFlexFlowsOtherAct]: " + update.toString());
		st.setString(1, wfuAnnulled);
		st.setString(2, wfuAnnulled);
		st.setTimestamp(3, time);
		// //System.out.println("[annulledFlexFlowsOtherAct]: " +
		// update.toString());
		st.executeUpdate();
	}

	private static void expiredFlexFlowsAct(Connection con, int idWorkFlow, Timestamp time) throws Exception {
		PreparedStatement st = null;
		StringBuilder update = new StringBuilder("UPDATE flexworkflow SET result = ? ");
		update.append(", statu = ?, dateCompleted = ? ");
		update.append(" WHERE idWorkFlow = ").append(idWorkFlow);
		update.append(" AND result = '").append(wfuPending).append("' ");
		update.append(" AND statu = '").append(pending).append("' ");
		st = con.prepareStatement(JDBCUtil.replaceCastMysql(update.toString()));
		log.debug("[expiredFlexFlows]: " + update.toString());
		st.setString(1, wfuExpired);
		st.setString(2, expires);
		st.setTimestamp(3, time);
		st.executeUpdate();
	}

	private static void completedUserFlexWorkFlows(String statuToChange, Connection con, Timestamp time, ParticipationForm form, Users usuario)
			throws Exception {
		PreparedStatement st = null;
		StringBuilder update = new StringBuilder("UPDATE user_flexworkflows SET result = ? ");
		update.append(",statu = ?, dateReplied = ?, comments=? ");
		// update.append(",statu = ? WHERE idWorkFlow = ? AND statu IN (");
		// update.append(wfuPending).append(",").append(wfuQueued).append(")");
		if (wfuPending.equalsIgnoreCase(statuToChange)) {
			update.append(",pending = ").append(Constants.permission);
		}
		update.append(" WHERE idWorkFlow = ? AND statu IN (");
		update.append(statuToChange).append(") ");
		update.append(" AND idUser = '").append(usuario.getUser()).append("' ");

		log.debug("[canceledUserWorkFlows] Row = " + form.getRow());
		if (form.getRow() > 0) {
			//update.append(" AND `row`> ").append(form.getRow());
			update.append(" AND row> ").append(form.getRow());
		}

		// System.out.println(update.toString());
		st = con.prepareStatement(JDBCUtil.replaceCastMysql(update.toString()));
		st.setString(1, form.getResult());
		st.setInt(2, wfCompleted);
		st.setTimestamp(3, time);
		st.setString(4, form.getCommentsUser());
		st.setInt(5, form.getIdWorkFlow());
		st.executeUpdate();
	}

	public static Collection getAllWorkFlows(String nameUser, boolean isRequest, ResourceBundle rb) throws Exception {
		Vector result = new Vector();
		StringBuilder sql = new StringBuilder(100);
		switch (Constants.MANEJADOR_ACTUAL) {
		case Constants.MANEJADOR_MSSQL:
			sql.append("SELECT w.idWorkFlow,(p.Apellidos + ' ' + p.nombres) AS nameOwner ");
			break;
		case Constants.MANEJADOR_POSTGRES:
			sql.append("SELECT w.idWorkFlow,(p.Apellidos || ' ' || p.nombres) AS nameOwner ");
			break;
		case Constants.MANEJADOR_MYSQL:
			sql.append("SELECT w.idWorkFlow,CONCAT(p.Apellidos , ' ' , p.nombres) AS nameOwner ");
			break;
		}

		sql.append(",w.dateCreation,w.dateExpire,w.dateCompleted");
		// ydavila Ticket 001-00-003023
		// sql.append(",w.statu,d.nameDocument,w.type");
		sql.append(",w.statu,d.nameDocument,w.type,w.subtype");
		if (isRequest) {
			sql.append(",uw.reading,uw.row");
		} else {
			sql.append(",w.readingWF ");
		}
		sql.append(" FROM workflows w,documents d,person p");
		if (isRequest) {
			sql.append(",user_workflows uw");
		}
		sql.append(" WHERE d.numGen = w.idDocument AND w.Statu  <> '").append(wfuCanceled).append("' AND w.owner = p.nameUser");
		sql.append(" AND p.accountActive='").append(Constants.permissionSt).append("'");
		if (isRequest) {
			sql.append(" AND w.idWorkFlow = uw.idWorkFlow AND uw.isOwner = '0'");
			sql.append(" AND uw.idUser = '").append(nameUser).append("'");
			sql.append(" AND uw.statu IN ('1','3')");
			sql.append(" AND uw.active = '").append(Constants.permission).append("'");
		} else {
			sql.append(" AND w.activeWF = '").append(Constants.permission).append("'");
			sql.append(" AND w.owner = '").append(nameUser).append("'");
		}

		// sql.append(" ORDER BY w.dateCreation,w.statu,w.idWorkFlow asc");
		sql.append(" ORDER BY w.dateCreation desc");
		log.debug("[getAllWorkFlows] " + sql.toString());
		// //System.out.println("[getAllWorkFlows] " + sql.toString());

		Vector datos = JDBCUtil.doQueryVector(sql.toString(),Thread.currentThread().getStackTrace()[1].getMethodName());
		for (int row = 0; row < datos.size(); row++) {
			Properties properties = (Properties) datos.elementAt(row);
			BaseWorkFlow wfData = new BaseWorkFlow();
			wfData.setFlexFlow(false);
			wfData.setNumWF(ToolsHTML.parseId(properties.getProperty("idWorkFlow")));
			wfData.setNameOwner(properties.getProperty("nameOwner"));
			wfData.setDateCreationWF(ToolsHTML.formatDateShow(properties.getProperty("dateCreation"), true));
			wfData.setDateExpireWF(ToolsHTML.formatDateToShow(properties.getProperty("dateExpire"), true));
			wfData.setDateCompletion(ToolsHTML.formatDateToShow(properties.getProperty("dateCompleted"), true));
			StringBuilder msg = new StringBuilder(60);
			msg.append(rb.getString("wf.type" + properties.getProperty("type"))).append(" ").append(rb.getString("wf.request"));
			// ydavila Ticket 001-00-003023
			System.out.println("subtype= " + properties.getProperty("subtype"));
			if (properties.getProperty("subtype").equals("3")) {
				msg.append("-").append(rb.getString("mailWF.change"));
			} else {
				if (properties.getProperty("subtype").equals("4")) {
					msg.append("-").append(rb.getString("mailWF.delete"));
				}
			}
			msg.append(": ").append(properties.getProperty("nameDocument"));
			if (!ToolsHTML.isEmptyOrNull(properties.getProperty("dateExpire"))) {
				msg.append(" ").append(rb.getString("wf.dateExpire")).append(ToolsHTML.formatDateToShow(properties.getProperty("dateExpire"), true));
			}
			wfData.setTitleForMail(msg.toString());
			wfData.setNewWF(false);
			if (isRequest) {
				wfData.setRow(properties.getProperty("row"));
				if (Constants.permissionSt.equalsIgnoreCase(properties.getProperty("reading"))) {
					wfData.setNewWF(true);
				}
			} else {
				// Luis Para probar 27/04/07
				// wfData.setRow(properties.getProperty("row"));
				if (Constants.permissionSt.equalsIgnoreCase(properties.getProperty("readingWF"))) {
					wfData.setNewWF(true);
				}
			}
			result.add(wfData);
		}
		return result;
	}

	public static Collection getAllFlexWorkFlows(String nameUser, boolean isRequest, ResourceBundle rb) throws Exception {
		Vector result = new Vector();
		StringBuilder sql = new StringBuilder(100);
		switch (Constants.MANEJADOR_ACTUAL) {
		case Constants.MANEJADOR_MSSQL:
			sql.append("SELECT w.idWorkFlow,(p.Apellidos + ' ' + p.nombres) AS nameOwner,");
			break;
		case Constants.MANEJADOR_POSTGRES:
			sql.append("SELECT w.idWorkFlow,(p.Apellidos || ' ' || p.nombres) AS nameOwner,");
			break;
		case Constants.MANEJADOR_MYSQL:
			sql.append("SELECT w.idWorkFlow,CONCAT(p.Apellidos , ' ' , p.nombres) AS nameOwner,");
			break;
		}
		sql.append("w.owner,w.dateCreation,w.dateExpire,w.dateCompleted");
		sql.append(",w.statu,d.nameDocument,w.type,sa.sAct_Name,a.Act_Name,w.IDFlexFlow");
		if (isRequest) {
			sql.append(",uw.reading,uw.idFlexWF");
		} else {
			sql.append(",w.readingWF ");
		}
		sql.append(" FROM flexworkflow w,documents d,person p, subactivities sa,activity a");
		if (isRequest) {
			sql.append(",user_flexworkflows uw");
		}
		// sql.append(" WHERE d.numGen = w.idDocument AND w.Statu NOT IN(").append(canceled).append(",").append(inQueued);
		sql.append(" WHERE d.numGen = w.idDocument AND sa.Act_Number = a.Act_Number");
		sql.append(" AND w.Statu NOT IN('").append(canceled).append("','").append(inQueued).append("') ");
		sql.append(" AND w.owner = p.nameUser");

		if (isRequest) {
			sql.append(" AND w.idWorkFlow = uw.idWorkFlow");
			sql.append(" AND uw.idUser = '").append(nameUser).append("'");
			sql.append(" AND uw.statu IN ('1','3')");
			sql.append(" AND uw.active = '").append(Constants.permission).append("'");
		} else {
			sql.append(" AND w.activeWF = '").append(Constants.permission).append("'");
			sql.append(" AND w.owner = '").append(nameUser).append("'");
		}
		sql.append(" AND w.type = sa.sAct_Number ");
		// sql.append(" ORDER BY w.dateCreation,w.statu,w.idWorkFlow asc");
		sql.append(" ORDER BY w.dateCreation desc");
		log.debug("[getAllFlexWorkFlows] " + sql.toString());
		// //System.out.println("[getAllFlexWorkFlows] " + sql.toString());

		Vector datos = JDBCUtil.doQueryVector(sql.toString(),Thread.currentThread().getStackTrace()[1].getMethodName());
		long idFlexFlow = 0;
		for (int row = 0; row < datos.size(); row++) {
			Properties properties = (Properties) datos.elementAt(row);
			long IDFlex = Long.parseLong(properties.getProperty("IDFlexFlow"));
			if (IDFlex != idFlexFlow) {
				// Se comenta momentaneamente, si se desea que no aparezcan
				// flujos repetidos
				// esta condicion funciona si se ordena por IDFlexFlow
				// (w.IDFlexFlow)
				// idFlexFlow = IDFlex;
				BaseWorkFlow wfData = new BaseWorkFlow();
				wfData.setFlexFlow(true);
				wfData.setNumWF(ToolsHTML.parseId(properties.getProperty("idWorkFlow")));
				wfData.setNameOwner(properties.getProperty("nameOwner"));
				wfData.setOwner(properties.getProperty("owner"));
				wfData.setOwnerCurrent(wfData.getOwner().equals(nameUser));
				wfData.setDateCreationWF(ToolsHTML.formatDateShow(properties.getProperty("dateCreation"), true));
				wfData.setDateExpireWF(ToolsHTML.formatDateToShow(properties.getProperty("dateExpire"), true));
				wfData.setDateCompletion(ToolsHTML.formatDateToShow(properties.getProperty("dateCompleted"), true));
				StringBuilder msg = new StringBuilder(60);

				// msg.append(properties.getProperty("sAct_Name")).append(" ").append(rb.getString("wf.request"));
				msg.append(properties.getProperty("Act_Name")).append(" ").append(rb.getString("wf.request"));
				// rb.getString("wf.type"+properties.getProperty("type"))).append(" ").append(rb.getString("wf.request"));
				msg.append(": ").append(properties.getProperty("nameDocument"));
				if (!ToolsHTML.isEmptyOrNull(properties.getProperty("dateExpire"))) {
					msg.append(" ").append(rb.getString("wf.dateExpire")).append(ToolsHTML.formatDateToShow(properties.getProperty("dateExpire"), true));
				}
				if (!ToolsHTML.isEmptyOrNull(properties.getProperty("sAct_Name"))) {
					msg.append(" - ").append(properties.getProperty("sAct_Name"));
				}
				wfData.setTitleForMail(msg.toString());
				wfData.setNewWF(false);
				if (isRequest) {
					wfData.setRow(properties.getProperty("idFlexWF"));
					if (Constants.permissionSt.equalsIgnoreCase(properties.getProperty("reading"))) {
						wfData.setNewWF(true);
					}
				} else {
					wfData.setRow(properties.getProperty("row")); // probando
					if (Constants.permissionSt.equalsIgnoreCase(properties.getProperty("readingWF"))) {
						wfData.setNewWF(true);
					}
				}
				result.add(wfData);
			}
		}
		return result;
	}

	// manejo de la Respuesta en los Flujos Par�metricos
	public static synchronized boolean saveResponseUser(ParticipationForm form, BeanNotifiedWF beanNotified, int mayorKeep, HttpServletRequest request)
			throws ApplicationExceptionChecked {
		return saveResponseUser(form, beanNotified, mayorKeep, request, null);
	}

	/**
	 *
	 * @param form
	 * @param beanNotified
	 * @param mayorKeep
	 * @param request
	 * @param usuarioActual
	 * @return
	 * @throws ApplicationExceptionChecked
	 */
	public static synchronized boolean saveResponseUser(ParticipationForm form, BeanNotifiedWF beanNotified, int mayorKeep, HttpServletRequest request,
			String usuarioActual) throws ApplicationExceptionChecked {
		log.debug("ID Flex Flow: " + form.getIdWorkFlow());
		// //System.out.println("ID Flex Flow: " + form.getIdWorkFlow());
		Connection con = null;
		PreparedStatement st = null;
		boolean resp = false;
		java.util.Date date = new Date();

		// boolean statuWF =
		// getStatuFlexFlow(form.getIdWorkFlow(),form.getRow(),form.getIdFather());
		// boolean pendingUsers =
		// thereArePendingUsersFlex(form.getIdWorkFlow());
		String NumCorrelativo = null;
		Search idUserPrevious = null;
		String statuDocAnt = null;
		String numberxxx = null;
		String statuDoc = null;
		log.debug("Resultado" + form.getResult());
		// Se Guarda la Respuesta del Usuario
		try {
			String[] values = HandlerDocuments.getFields(new String[] { "number", "statu", "statuAnt" }, "documents", "numGen",
					String.valueOf(form.getIdDocument()));
			if (values != null) {
				numberxxx = values[0];
				statuDoc = values[1];
				statuDocAnt = values[2];
			}
			if (form.isCompleteFlexFlow()) {
				statuDoc = form.getStatuDoc();
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			throw new ApplicationExceptionChecked("");
		}
		// Variable para Manejar Cuando la Actividad en el Flujo debe ser
		// Activada
		boolean reinitLastActivityInFlex = false;
		// Si es el Primer usuario de los Participantes que rechaza el Flujo

		if (form.getIdFather() == 0 && wfuReInit.compareTo(form.getResult()) != 0) {
			log.debug("Primer Usuario en rechazar el Flujo de Trabajo ...");
			// idUserPrevious = getPreviousFlexResponse(form.getIdWorkFlow(),
			// wfuAcepted,form.getIdParticipation());
			// //Si no se consigue Usuario anterior para la Actividad se busca
			// la Actividad Anterior
			// //y all� se busca el �ltimo Usuario en Responder el Flujo de
			// Trabajo
			// if (idUserPrevious == null) {
			// idUserPrevious =
			// getPreviousFlexActivityResponse(form.getIdFlexFlow(),wfuAcepted,form.getIdParticipation());
			// if (idUserPrevious!=null) {
			// reinitLastActivityInFlex = true;
			// }
			// }
			// Cambio Realizado el 2007-01-12 si el Flujo es rechazado va al
			// Originador del Mismo
			reinitLastActivityInFlex = false;
			idUserPrevious = null;
			log.debug("idUserPrevious: " + idUserPrevious);
		} else {
			if (wfuReInit.compareTo(form.getResult()) == 0) {
				log.debug("Se esta reinciando el Flujo de Trabajo...");
				reinitLastActivityInFlex = true;
				// En comentario desde 2007-01-12
				// } else {
				// log.debug("Buscando Usuario Anterior en la Actividad........");
				// idUserPrevious =
				// getPreviousFlexResponse(form.getIdWorkFlow(),
				// wfuAcepted,form.getIdParticipation());
			}
		}
		Search idUserNext;// = null;
		idUserNext = getPreviousFlexResponse(form.getIdWorkFlow(), wfuRejected, form.getIdParticipation());
		log.debug("idUserNext = " + idUserNext);
		// //System.out.println("idUserNext = " + idUserNext);

		// BeanWFRejectes beanRejected = null;
		// if (idUserNext==null) {
		// beanRejected = loadDataRejectedWF(form.getIdWorkFlow());
		// }
		log.debug("Cargando datos del Pr�ximo usuario en Responder el Flex Flow ...");
		log.debug("form.getSecuential(): " + form.getSecuential());

		// Descomentar para habilitar proceso tomando en cuenta si la actividad
		// es secuencial o NO secuencial
		String secuential = "0";
		secuential = isFTPSecuential(form.getIdWorkFlow());

		form.setSecuential(secuential);

		if (Constants.permissionSt.compareTo(form.getSecuential()) == 0) {
			// Es no secuencial
			// System.out.println("es no secuencial, se busca por <>");
			// System.out.println("Se va a invocar getNextUserInFlexWF() con idworkflow: "
			// + form.getIdWorkFlow() + " y row: "+ form.getRow());
			getNextUserInFlexWF(form.getIdWorkFlow(), form.getRow(), beanNotified, pending, "<>");
		} else {
			// Es secuencial
			// System.out.println("es Secuencial, se busca por >");
			// System.out.println("Se va a invocar getNextUserInFlexWF() con idworkflow: "
			// + form.getIdWorkFlow() + " y row: "+ form.getRow());
			getNextUserInFlexWF(form.getIdWorkFlow(), form.getRow(), beanNotified, pending, ">");
		}

		// Comentar si se habilita c�digo anterior
		// getNextUserInFlexWF(form.getIdWorkFlow(), form.getRow(),
		// beanNotified, pending, ">");
		long newActToFlexFlow = -1;
		boolean cmpFlexFlow = false;
		boolean reinitFlexFlow = false;
		if (beanNotified != null && beanNotified.getRow() == null) {
			cmpFlexFlow = true;
			// Buscamos el ID de la Siguiente Actividad Relacionada al Flujo a
			// Activar
			newActToFlexFlow = getNewActFlexFlow(form.getIdWorkFlow(), form.getIdDocument());
			log.debug("newActToFlexFlow " + newActToFlexFlow);

			// Si hay una Actividad en el Flujo por Activar
			if (newActToFlexFlow != -1) {
				getNextUserInFlexWF(newActToFlexFlow, form.getRow(), beanNotified, pending, ">");
				if (beanNotified.getIdUser() != null && beanNotified.getIdUser().equals(Constants.ID_USER_TEMPORAL)) {
					throw new ApplicationExceptionChecked("E0119");
				} else {
					// tenemos a la mano la proxima actividad, notificamos al
					// ejecutante indicado
					// redactamos el correo de nuevo flujo
					log.info("La actividad anterior ya culmino, notificamos a los participantes de la nueva actividad");
					sendFTPNewFlowNotification(newActToFlexFlow, form);
				}
			}
			if (beanNotified != null) {
				log.debug("EMAIL: " + beanNotified.getEmail());
			}
			// Si no Consigue Usuario se Chequea si ocurri� un Posible Rechazo
			// previamente
			if (beanNotified != null && beanNotified.getRow() == null && newActToFlexFlow != -1) {
				if (form.getIdFather() != 0) {
					// getNextUserInFlexWF(newActToFlexFlow,form.getRow(),beanNotified,rejected);
					// } else {
					getNextUserInFlexWF(newActToFlexFlow, form.getIdFather(), beanNotified, rechazado, ">");
					if (beanNotified.getRow() != null) {
						reinitFlexFlow = true;
					}
				}
			}
		}
		// if (true) {
		// throw new ApplicationExceptionChecked("E0109");
		// }
		// String dateExpire =
		// HandlerDocuments.getDateExpiresDoc(form.getIdDocument());
		// Vector histReview =
		// (Vector)HandlerDocuments.getAllVersionIdsToDocumentAndStatu(form.getIdDocument(),
		// HandlerDocuments.docReview,false);
		StringBuilder std = new StringBuilder(50);
		std.append("('").append(HandlerDocuments.docApproved).append("','");
		std.append(HandlerDocuments.docObs).append("')");
		Vector histApproved = (Vector) HandlerDocuments.getAllVersionIdsToDocumentAndStatu(form.getIdDocument(), std.toString(), true);
		// int numVersionObsoBorrador = form.getNumVersion();
		String toImpresion = null;
		CheckOutDocForm version = new CheckOutDocForm();
		version.setNumVersion(String.valueOf(form.getNumVersion()));
		InputStream stream = HandlerStruct.recuperarFileBD(form.getNumVersion());
		try {
			HandlerDocuments.loadVersion(version);
			// rowOwner = getOwnerWF(String.valueOf(form.getIdWorkFlow()));
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		if (!ToolsHTML.isEmptyOrNull(statuDoc)) {
			try {
				String[] items = new String[] { "monthExpireDoc", "unitTimeExpire" };
				String[] values = HandlerParameters.getFieldsExpired(items, HandlerDocuments.getTypeDocument(String.valueOf(form.getIdDocument())));
				String monthExpireDoc = values != null ? values[0] : null;
				String unit = values != null ? values[1].trim() : null;
				String numberCompare = null;
				// buscamos el proximo n�mero correlativo en caso que sea
				// borrador y este alla sido aprobado.
				// TODO esta B�squeda no funciona ASI en FLujos PARAMETRICOS, se
				// debe MODIFICAR
				// if (form.isOwner()) {
				if (beanNotified != null) {
					log.debug("beanNotified.getRow(): " + beanNotified.getRow());
				}
				log.debug("newActToFlexFlow " + newActToFlexFlow);
				if (newActToFlexFlow == -1 && beanNotified != null && beanNotified.getRow() == null) {
					log.debug("******************************************");
					// log.debug("statuWF: " + statuWF);
					log.debug("form.getResult() " + form.getResult() + "'");
					log.debug("statuDoc " + statuDoc + "'");
					log.debug("******************************************");
					if ((form.getResult().equalsIgnoreCase(wfuAcepted)) && (statuDoc.trim().compareTo(HandlerDocuments.inFlewFlow) == 0)) {

						// conseguimos el numero de incializacion de un borrador
						ResourceBundle rb = ToolsHTML.getBundle(request);
						if ((rb.getString("numcorrelativoinicio").length()) > 9) {
							numberCompare = rb.getString("numcorrelativoinicio").substring(0, 9);
						} else {
							numberCompare = rb.getString("numcorrelativoinicio");
						}
						log.debug("Generando el N�mero para el Documento..." + numberCompare);
						log.debug("numberxxx " + numberxxx);
						log.debug("toImpresion " + toImpresion);
						// si el numero del documento es de un borrador y ha
						// sido aprobado, le asignamos el numero
						if ((numberxxx.equalsIgnoreCase(numberCompare)) || (ToolsHTML.isEmptyOrNull(numberxxx))) {
							if (!"1".equalsIgnoreCase(toImpresion)) {
								// NumCorrelativo =
								// EditDocumentAction.numCorrelativo(form.getIdNode(),form.getIdLocation());
								String numByLocation = null;
								String resetNumber = null;

								numByLocation = String.valueOf(HandlerParameters.PARAMETROS.getNumDocLoc());
								resetNumber = HandlerParameters.PARAMETROS.getResetDocNumber();
								if ("1".equalsIgnoreCase(numByLocation)) {
									// Se Carga la Estructura si la Misma no se
									// encuentra Cargada....
									// Hashtable tree =
									// (Hashtable)request.getSession().getAttribute("tree");
									// Users usuario =
									// (Users)request.getSession().getAttribute("user");
									// tree = ToolsHTML.checkTree(tree,usuario);

									Users usuario = (Users) request.getSession().getAttribute("user");
									Users usr = new Users();
									usr.setIdGroup(DesigeConf.getProperty("application.admon"));
									usr.setUser(usuario.getUser());
									Hashtable tree = ToolsHTML.checkTree(null, usr);

									NumCorrelativo = EditDocumentAction
											.numCorrelativo(form.getIdNode(), form.getIdLocation(), numByLocation, resetNumber, tree);
								} else {
									NumCorrelativo = EditDocumentAction
											.numCorrelativo(form.getIdNode(), form.getIdLocation(), numByLocation, resetNumber, null);
								}
							}
						}
					}
				}

				// Object[] usuarios = null;
				Vector usrs = null;
				if (form.isOwner() && wfuReInit.equalsIgnoreCase(form.getResult())) {

					//
					// UPDATE user_flexworkflows SET result = 20, statu=20 WHERE
					// idFlexWF in (
					// SELECT uwf.idFlexWF
					// FROM user_flexworkflows AS uwf INNER JOIN
					// flexworkflow AS ff ON uwf.idWorkFlow = ff.idWorkFlow
					// WHERE (ff.IDFlexFlow = 126) AND (uwf.IdFather = 0) AND
					// (uwf.isOwner = '0') AND (uwf.result = 1) AND
					// (ff.statu='6'));

					// subquery para ubicar los ids
					StringBuffer query = new StringBuffer();
					query.append("SELECT uwf.idFlexWF FROM user_flexworkflows AS uwf INNER JOIN flexworkflow AS ff ON uwf.idWorkFlow = ff.idWorkFlow ");
					query.append("WHERE (ff.IDFlexFlow = ?) AND (uwf.isOwner = '0') AND (uwf.result = '1') AND (ff.statu=? OR ff.statu=?)");
					ArrayList parametros = new ArrayList();
					parametros.add(form.getIdFlexFlow());
					parametros.add(inQueued);
					parametros.add(pending);

					StringBuilder actUser_FlexWF = new StringBuilder();
					actUser_FlexWF.append("UPDATE user_flexworkflows SET result = '").append(wfAnnulled).append("' ");
					actUser_FlexWF.append(", statu='").append(wfAnnulled).append("' WHERE ");
					actUser_FlexWF.append("idFlexWF in (");
					actUser_FlexWF.append(JDBCUtil.executeQueryRetornaIds(query, parametros, Thread.currentThread().getStackTrace()[1].getMethodName()));
					actUser_FlexWF.append(")");

					try {
						int regActualizado = JDBCUtil.executeUpdate(actUser_FlexWF);
						log.debug("[actUser_FlexWF idFlexFlow =" + form.getIdFlexFlow() + "] " + actUser_FlexWF.toString());
						log.debug("Flujos Modificados: " + regActualizado);
	
						// SE COLOCA EN ANULADO EL STATUS DE CADA USUARIO QUE
						// RESPONDIO
						// YSA 06/07/2007
						query.setLength(0);
						query.append(" SELECT uwf.idFlexWF FROM user_flexworkflows AS uwf INNER JOIN flexworkflow AS ff ON uwf.idWorkFlow = ff.idWorkFlow ");
						query.append(" WHERE ff.IDFlexFlow = ").append(form.getIdFlexFlow());
						query.append(" AND uwf.isOwner = '0' AND uwf.result = '").append(response).append("' ");
	
						StringBuilder anularUser_FlexWF = new StringBuilder();
						anularUser_FlexWF.append("UPDATE user_flexworkflows SET statu='").append(wfAnnulled).append("'  WHERE ");
						anularUser_FlexWF.append("idFlexWF in (");
						anularUser_FlexWF.append(JDBCUtil.executeQueryRetornaIds(query));
						anularUser_FlexWF.append(")");
	
						regActualizado = JDBCUtil.executeUpdate(anularUser_FlexWF);
						log.debug("[anularUser_FlexWF idFlexFlow =" + form.getIdFlexFlow() + "] " + anularUser_FlexWF.toString());
						log.debug("Flujos Modificados: " + regActualizado);
	
						// FIN CAMBIO YSA 06/07/2007
						query.setLength(0);
						query.append("SELECT IDFlexFlow FROM flexworkflow WHERE idWorkFlow = ").append(form.getIdWorkFlow());
						String idsFlow = JDBCUtil.executeQueryRetornaIds(query);
	
						query.setLength(0);
						query.append("SELECT idWorkFlow FROM flexworkflow WHERE IDFlexFlow IN  (");
						query.append(idsFlow).append(") AND (statu = '").append(inQueued);
						query.append("' OR statu = '").append(pending).append("' ) ");
						idsFlow = JDBCUtil.executeQueryRetornaIds(query);
	
						StringBuilder act_FlexWorkFlow = new StringBuilder();
						act_FlexWorkFlow.append("UPDATE flexworkflow SET statu='").append(wfuAnnulled).append("' ");
						act_FlexWorkFlow.append("WHERE idWorkFlow in (").append(idsFlow).append(") AND owner = '0'");
	
						log.debug("[act_FlexWorkFlow form.getIdWorkFlow()= " + form.getIdWorkFlow() + "] " + act_FlexWorkFlow.toString());
	
						regActualizado = JDBCUtil.executeUpdate(act_FlexWorkFlow);
						log.debug("act_FlexWorkFlow Modificados: " + regActualizado);

					} catch (Exception ex) {
						ex.printStackTrace();
						throw new ApplicationExceptionChecked("E0017");
					}

					// TODO Tengo que ver xq solo trae el primer wf.
					// toTODO Tengo que ver xq solo trae el primer wf.
					usrs = (Vector) getUserInFlexFlow(form.getIdFlexFlow());
					// Obtener todos los Usuarios Participantes en el Flujo por
					// c/u de las Actividades del mismo
					// usuarios = getParticipationsInWF(form.getIdWorkFlow());
					// reInitFlexFlow(form.getIdFlexFlow(),con);
				}

				// cargo el documento para el envio de correo
				BaseDocumentForm docForm = new BaseDocumentForm();
				String idDoc = String.valueOf(form.getIdDocument());
				docForm.setIdDocument(idDoc);
				docForm.setNumberGen(idDoc);

				HandlerStruct.loadDocument(docForm, true, false, null, request);

				con = JDBCUtil.getConnection(Thread.currentThread().getStackTrace()[1].getMethodName());
				con.setAutoCommit(false);
				// Se Guarda la Respuesta del Usuario
				Timestamp time = new Timestamp(date.getTime());
				// Se Guarda la Respuesta del Usuario

				if (usuarioActual != null) {
					updateUserFlexFlowsCompleted(usuarioActual, con, time, form);
				} else {
					updateUserFlexFlows(con, time, form);
				}
				// Se verifica si el Usuario qui�n responde es qui�n Origin� el
				// Ciclo y si est� Reiniciando el Mismo
				if (form.isOwner() && wfuReInit.equalsIgnoreCase(form.getResult())) {
					log.debug("Re-iniciando Flujo de Trabajo.....");
					// ResourceBundle rb = ToolsHTML.getBundle(request);
					// Users usuario =
					// (Users)request.getSession().getAttribute("user");
					log.debug("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
					log.debug("Usuarios: ");
					for (int usa = 0; usa < usrs.size(); usa++) {
						log.debug(usrs.get(usa).toString());
						// TODO assad
					}
					log.debug("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");

					// Antes de Reiniciar el Flujo de Trabajo, Coloco como
					// Anulados los flujos Pendientes
					// Para que se reinicien tembien.

					// Se duplican los usuarios para reiniciar el flujo
					reInitFlexFlow(form.getIdFlexFlow(), usrs, con);

					// Las actividades del flujo se colocan como pendientes
					reInitActFlexFlow(form.getIdFlexFlow(), false, "", 0, true);

					// se elimina la fecha de vencimiento si es un reinicio

					// buscamos los usuarios pendientes en el flujo para
					// enviarles la notificacion de reinicio
					String users = getAllUsersInWFP(form.getIdWorkFlow(), HandlerWorkFlows.pending, HandlerWorkFlows.wfuPending, true);
					beanNotified.setEmail(HandlerGrupo.getEmailForUsers(users));
					beanNotified.setNotified("0");

					// UPDATE flexworkflow SET statu = '20'
					// WHERE (idWorkFlow IN (SELECT wf.idWorkFlow AS Expr1 FROM
					// user_flexworkflows AS uwf INNER JOIN person AS p ON
					// uwf.idUser = p.nameUser INNER JOIN
					// tbl_cargo AS c ON p.cargo = c.idcargo INNER JOIN
					// flexworkflow AS wf ON uwf.idWorkFlow = wf.idWorkFlow
					// INNER JOIN
					// subactivities AS sa ON wf.type = sa.sAct_number WHERE
					// (wf.IDFlexFlow IN
					// (SELECT IDFlexFlow FROM flexworkflow AS flexworkflow_1
					// WHERE (idWorkFlow = 265))) AND (p.accountActive = '1')
					// AND (uwf.statu = '20') AND (uwf.result = '20')))
					//

					// StringBuilder act_FlexWorkFlow2 = new StringBuilder();
					// act_FlexWorkFlow2.append("UPDATE flexworkflow SET statu = ?");
					// act_FlexWorkFlow2.append("WHERE (idWorkFlow IN (SELECT wf.idWorkFlow AS Expr1 FROM user_flexworkflows AS uwf INNER JOIN person AS p ON uwf.idUser = p.nameUser INNER JOIN ");
					// act_FlexWorkFlow2.append("tbl_cargo AS c ON p.cargo = c.idcargo INNER JOIN flexworkflow AS wf ON uwf.idWorkFlow = wf.idWorkFlow INNER JOIN ");
					// act_FlexWorkFlow2.append("subactivities AS sa ON wf.type = sa.sAct_number WHERE (wf.IDFlexFlow IN");
					// act_FlexWorkFlow2.append("(SELECT IDFlexFlow FROM flexworkflow AS flexworkflow_1 ");
					// act_FlexWorkFlow2.append("WHERE (idWorkFlow = ?))) AND (p.accountActive = '1') AND (uwf.statu = ?) AND (uwf.result = ?)))");
					//
					// log.debug("[act_FlexWorkFlow2 form.getIdWorkFlow()= " +
					// form.getIdWorkFlow() + "] " +
					// act_FlexWorkFlow2.toString());
					//
					// con = JDBCUtil.getConnection(Thread.currentThread().getStackTrace()[1].getMethodName());
					// con.setAutoCommit(false);
					//
					// PreparedStatement pstm =
					// con.prepareStatement(act_FlexWorkFlow2.toString());
					//
					// pstm.setString(1,wfuAnnulled);
					// pstm.setLong(2,form.getIdWorkFlow());
					// pstm.setString(3,wfuAnnulled);
					// pstm.setString(4,wfuAnnulled);
					//
					// log.debug("act_FlexWorkFlow2 Modificados: " +
					// pstm.executeUpdate());

					// reInitWF(usuarios,form.getIdWorkFlow(),Integer.parseInt(form.getSecuential().trim()),
					// usuario,rb.getString("wf.reIWFTitle"),form.getCommentsUser(),con,form.getRow());
				} else {
					if (!form.isOwner() && wfuAcepted.equalsIgnoreCase(form.getResult())) {
						// Si los Usuarios ya completaron la Actividad
						// se procede a Activar la pr�xima Actividad si Existe
						if (newActToFlexFlow != -1) {
							log.info("Se activa la siguiente actividad del FTP");
							updateFlexFlows(con, "1", form.getIdDocument(), newActToFlexFlow, time);
						} else {
							log.info("Ya no quedan actividades en este FTP");
						}
						// Si el Flujo es Secuencial se activa el siguiente
						// usuario que debe responder.
						if (reinitFlexFlow && beanNotified.getIdUser() != null) {
							Object[] datos = { beanNotified.getIdUser() };
							// todo OJO
							log.debug("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
							for (int xy = 0; xy < datos.length; xy++) {
								log.debug("DATOS: " + datos[xy]);
							}
							log.debug("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
							processList(datos, st, con, beanNotified.getIdWorkFlow(), 0, Integer.parseInt(form.getSecuential()), Constants.notPermission,
									Long.parseLong(beanNotified.getRow()), 0);
							beanNotified.setEmail(idUserPrevious.getContact());
						} else {
							if (wfSecuential.equalsIgnoreCase(form.getSecuential())) {
								// //System.out.println("updateUserFlexWorkFlows con beanNotified.getRow(): "
								// + beanNotified.getRow());
								updateUserFlexWorkFlows(con, wfuPending, beanNotified.getRow());
							} else {
								// No es secuencial, se deben colocar como
								// pendiente todos los usuarios de la actividad
								// con status pendiente
								// //System.out.println("updateUserFlexWorkFlows con form.getIdWorkFlow(): "
								// + form.getIdWorkFlow());
								updateUsersFlexWorkFlows(con, wfuPending, form.getIdWorkFlow());
								// if (beanNotified.isOwnerWF()) {
								// System.out.println("-- se va a colocar el proximo usr en pendiente: "
								// + beanNotified.getRow());
								// updateUserFlexWorkFlows(con, wfuPending,
								// beanNotified.getRow());
								// }
							}
						}
						// Si se debe cambiar de Flujo se Actualiza la Actividad
						// Completada :D
						if (cmpFlexFlow) {
							updateStatuWF("flexworkflow", form.getIdWorkFlow(), con, wfuAcepted, response, time);
						}
						// Si no hay m�s Actividades
						// El ultimo Usuario Acept�
						// y no hay m�s Usuarios Pendientes se Actualiza el
						// Estado del Documento
						// Ya que el Flujo ha Sido Completado Exitosamente
						log.debug("newActToFlexFlow = " + newActToFlexFlow);
						if (newActToFlexFlow == -1 && beanNotified != null && beanNotified.getRow() == null) {
							/*
							 * HandlerDocuments.updateStateDoc(con,form. getIdDocument (),HandlerDocuments.docApproved,statuDoc, HandlerDocuments
							 * .lastApproved,form.getNumVersion(),time);
							 */
							String newState = HandlerDocuments.docApproved;
							// String lastOpe = HandlerDocuments.lastApproved;
							// String typeMovHist = Constants.historyApproved;
							Timestamp dateExpireEnd = null;
							int value = 0;
							if (ToolsHTML.isNumeric(monthExpireDoc.trim())) {
								value = Integer.parseInt(monthExpireDoc.trim());
							}
							java.util.Date dateExpireCalculated = ToolsHTML.getDateExpireDocument(ToolsHTML.sdf.format(date), null, value, unit);
							if (dateExpireCalculated != null) {
								dateExpireEnd = new Timestamp(dateExpireCalculated.getTime());
							}
							// colocamos la fecha de expiracion actulizada con
							// la fecha en que se aprobo el documento nuevamente
							version.setDateExpires(String.valueOf(dateExpireEnd));

							version.setApproved("0");
							version.setDateApproved(ToolsHTML.sdfShowConvert.format(time));
							if (ToolsHTML.isEmptyOrNull(version.getDateExpires())) {
								version.setDateExpires(ToolsHTML.sdfShowConvert.format(dateExpireEnd));
							}

							try {
								if (ToolsHTML.isNumeric(version.getMinorVer())) {
									version.setMinorVer("0");
								} else {
									if (!ToolsHTML.isEmptyOrNull(version.getMinorVer().trim())) {
										version.setMinorVer("A");
									} else {
										version.setMinorVer("");
									}
								}
							} catch (NullPointerException e) {
								version.setMinorVer("0");
							}

							if (ToolsHTML.isNumeric(version.getMayorVer())) {
								int mayorVer = Integer.parseInt(version.getMayorVer().trim());
								// Buscamos si es la primera version
								boolean isFirstVersion = HandlerDocuments.isFirstVersionDocument(form.getIdDocument());
								version.setMayorVer(String.valueOf(mayorVer == 0 && isFirstVersion ? mayorVer : mayorVer + 1));
							} else {
								// //System.out.println(version);
								// //System.out.println(version.getMayorVer());
								// //System.out.println(version.getMayorVer().trim());
								version.setMayorVer(ToolsHTML.incVersion(version.getMayorVer().trim()));
							}
							version.setIdDocument(String.valueOf(form.getIdDocument()));
							// Si la versi�n sometida a Flujo se encuentra en
							// Borrador
							// o Revisada la misma pasa a ser el Documento
							// Aprobado
							if (HandlerDocuments.docTrash.equalsIgnoreCase(version.getStatu())
									|| HandlerDocuments.docReview.equalsIgnoreCase(version.getStatu())) {
								HandlerDocuments.updateStatuVersionDoc(form.getNumVersion(), con, newState, time, dateExpireEnd, null, version.getMayorVer(),
										version.getMinorVer(), false, Constants.permission, null);
							} else {
								// Sino se crea una nueva versi�n del Documento
								version.setStatu(newState);
								version.setTypeWF(Constants.permission);
								HandlerDocuments.createNewVersionDoc(version, con, time, stream);
								form.setNumVersion(version.getNumVer());
							}

							// Se procede a Mantener actualizadas las versiones
							// mayores
							// seg�n la configuraci�n actual del Sistema
							if (histApproved.size() > 0 && mayorKeep <= histApproved.size()) {
								int limSup = histApproved.size() - mayorKeep;
								// for (int row = 0; row <= limSup; row++) {
								for (int row = 0; row < limSup; row++) {
									HandlerDocuments.deleteLogicVersion(Constants.notPermission, (String) histApproved.get(row), HandlerDocuments.docApproved,
											con);
								}
							}
							// Se cambia el statu de la �ltima versi�n Aprobada
							// a Obsoleta
							if (histApproved != null && histApproved.size() > 0) {
								HandlerDocuments.updateStatuDoc(HandlerDocuments.docObs, (String) histApproved.get(histApproved.size() - 1),
										HandlerDocuments.docApproved, con);
							}

							// si es borrador y es aprobado, colocamos el numero
							// correlativo especifico al documento.
							if (((numberxxx.equalsIgnoreCase(numberCompare) || (ToolsHTML.isEmptyOrNull(numberxxx))) && (newState
									.equalsIgnoreCase(HandlerDocuments.docApproved)))) {
								// si no es un documento en linea con solicitud
								// de impresion
								if (!"1".equalsIgnoreCase(toImpresion)) {
									log.debug("Colocando el N�mero al Documento");
									HandlerDocuments.updateNumCorrelativo(con, NumCorrelativo, form.getIdDocument());
								}
							}

							HandlerDocuments.updateStateDoc(con, form.getIdDocument(), HandlerDocuments.docApproved, statuDoc, HandlerDocuments.lastApproved,
									form.getNumVersion(), time);

							// Si el Documento en la versi�n Actual tiene
							// Documentos Relacionados
							// Se procede a Crear la Relaci�n con la Nueva
							// Versi�n

							String docRelations = docForm.getDocRelations();

							if (!ToolsHTML.isEmptyOrNull(docRelations)) {
								log.debug("docRelations " + docRelations);
								log.debug("Document " + version.getIdDocument());
								log.debug("Versi�n  " + version.getNumVer());
								// HandlerStruct.deleteDocumentsLinks(Integer.parseInt(version.getIdDocument()),
								// version.getNumVer(),con);
								HandlerStruct.getPreparedStatementDocumentLinks(version.getIdDocument(), version.getNumVer(), docRelations, con);
							}

							// Se Procede a Crear el Hist�rico del Flujo de
							// Trabajo
							log.info("Status del FTP antes de almacenar en el historico: " + form.getStatu());
							updateWFHistoryFlexFlow(con, form.getIdFlexFlow(), form.getNameUser(), time, String.valueOf(form.getNumAct()),
									form.getIdDocument(), form.getStatu(), form.getResult());
							// updateWFHistoryFlexFlow(con,form.getIdWorkFlow(),form.getNameUser(),time,String.valueOf(form.getIdFlexFlow()));
						} else {
							// if
							// (!(wfuCanceled.equalsIgnoreCase(form.getResult())))
							// {
							if (wfuRejected.equalsIgnoreCase(form.getResult())) {
								// vamos a notificar al iniciador del flujo
								Users usuario = (Users) request.getSession().getAttribute("user");
								Users usuarioDestino = HandlerDBUser.getUser(form.getOwnerWF());
								if (usuarioDestino.getIdPerson() > 0) {
									if (beanNotified == null) {
										beanNotified = new BeanNotifiedWF();
									}

									DataUserWorkFlowForm formaWF = new DataUserWorkFlowForm();
									formaWF.setIdDocument(form.getIdDocument());
									HandlerDocuments.loadDataDocument(formaWF);

									StringBuilder msg = new StringBuilder("");
									ResourceBundle rb = ToolsHTML.getBundle(request);
									String prefijo = "";
									String number = "";
									String prefixNumber = "";
									prefijo = ToolsHTML.isEmptyOrNull(formaWF.getPrefix()) ? "" : formaWF.getPrefix();
									number = ToolsHTML.isEmptyOrNull(formaWF.getNumber()) ? "" : formaWF.getNumber();
									prefixNumber = prefijo + number;
									String comments = String.valueOf(form.getCommentsUser());

									msg.append(rb.getString("wf.reasigned")).append(" ");
									msg.append(prefixNumber).append("<br/>");
									msg.append(rb.getString("wf.reasignedName")).append(" ").append(formaWF.getNameDocument()).append("<br/>");
									msg.append(rb.getString("wf.reasignedUser")).append(" ").append(usuario.getNamePerson()).append(" ")
											.append(rb.getString("wf.reasignedAgain")).append("<br/>");
									msg.append(rb.getString("wf.reasignedCause")).append(": ").append(comments);
									// System.out.println("---MENSAJE: " + msg);
									HandlerWorkFlows.notifiedUsers(rb.getString("wf.deniedTitle") + " " + prefixNumber, rb.getString("mail.nameUser"),
											HandlerParameters.PARAMETROS.getMailAccount(), usuarioDestino.getEmail(), msg.toString());
								}
							}

						}
					} else {
						// Si el Usuario Rechaza el Flujo
						if (wfuCanceled.equalsIgnoreCase(form.getResult())) {
							// Si No hay Usuarios pendientes se procede a darle
							// la opci�n
							// al originador del Flujo de Reiniciar el Mismo
							if (idUserPrevious == null) {
								log.debug("Creando nueva entrada");
							}
						} else {
							if (wfuRejected.equalsIgnoreCase(form.getResult())) {
								log.debug("Rechanzado el Flujo de Trabajo");
								log.debug("Deshabilitando Usuarios Pendientes y en Cola en el Flujo de Trabajo");
								// Deshabilitando Usuarios que respondieron el
								// Flujo de Trabajo
								disabledUserFlexFlow(form.getIdWorkFlow(), con, wfuAcepted, form.getIdFlexFlow(), Constants.notPermissionSt);
								// Usuarios en Cola..
								disabledUserFlexFlow(form.getIdWorkFlow(), con, wfuPending, 0, Constants.notPermissionSt);
								// Flujos en Cola...
								disabledUserFlexFlow(form.getIdWorkFlow(), con, wfuQueued, 0, Constants.notPermissionSt);
								// Si hay un usuario Previo
								// if
								// (ToolsHTML.isNumeric(beanNotified.getRow())&&
								// !"0".equalsIgnoreCase(beanNotified.getRow()))
								// {
								// updateWFPending(form.getIdWorkFlow(),form.getRow(),Integer.parseInt(beanNotified.getRow()),
								// Constants.notPermission,form.getIdFather()==0,con);
								// } else {
								// //Se debe notificar al Iniciador del Flujo
								// //
								// updateUserWorkFlows(con,wfuPending,rowOwner);
								// }
								log.debug("[idUserPrevious] " + idUserPrevious);
								if (idUserPrevious != null) {
									Object[] datos = { idUserPrevious.getDescript() };
									// Se crea la Nueva entrada para el �ltimo
									// usuario que hab�a aceptado el
									// Flujo de Trabajo
									int idWF = Integer.parseInt(idUserPrevious.getIdWorkFlow());
									processList(datos, st, con, idWF, 0, Integer.parseInt(form.getSecuential()), Constants.notPermission,
											Long.parseLong(idUserPrevious.getId()), Integer.parseInt(idUserPrevious.getAditionalInfo()));
									beanNotified.setEmail(idUserPrevious.getContact());
									// Se desactiva la respuesta Anterior del
									// Usuario que actualmente responde
									// el Flujo de Trabajo
									disabledUserFlexFlow(form.getIdWorkFlow(), con, null, Long.parseLong(idUserPrevious.getId()), Constants.notPermissionSt);
									// Se debe reiniciar la Actividad Anterior
									// en El Flujo
									if (reinitLastActivityInFlex) {
										enableActivityInFlexFlow(idWF, con, pending);
										enableActivityInFlexFlow(form.getIdWorkFlow(), con, inQueued);
									}
								} else {
									// Se crea una entrada al
									// Propietario del Flujo de Trabajo
									// Param�trico
									// updateUserWorkFlows(con,wfuPending,""+rowOwner);
									log.debug("Creando Entrada para el Iniciador del Flujo");
									Object[] datos = { form.getOwnerWF() };

									String userWF = processList(datos, st, con, form.getIdWorkFlow(), 0, Integer.parseInt(form.getSecuential()),
											Constants.permission, 0, 0);

									// vamos a notificar al iniciador del flujo
									Users usuario = (Users) request.getSession().getAttribute("user");
									Users usuarioDestino = HandlerDBUser.getUser(form.getOwnerWF());
									if (usuarioDestino.getIdPerson() > 0) {
										if (beanNotified == null) {
											beanNotified = new BeanNotifiedWF();
										}

										DataUserWorkFlowForm formaWF = new DataUserWorkFlowForm();
										formaWF.setIdDocument(form.getIdDocument());
										HandlerDocuments.loadDataDocument(formaWF);

										StringBuilder msg = new StringBuilder("");
										ResourceBundle rb = ToolsHTML.getBundle(request);
										String prefijo = "";
										String number = "";
										String prefixNumber = "";
										prefijo = ToolsHTML.isEmptyOrNull(formaWF.getPrefix()) ? "" : formaWF.getPrefix();
										number = ToolsHTML.isEmptyOrNull(formaWF.getNumber()) ? "" : formaWF.getNumber();
										prefixNumber = prefijo + number;
										String comments = String.valueOf(form.getCommentsUser());

										msg.append(rb.getString("wf.reasigned")).append(" ");
										msg.append(prefixNumber).append("<br/>");
										msg.append(rb.getString("wf.reasignedName")).append(" ").append(formaWF.getNameDocument()).append("<br/>");
										msg.append(rb.getString("wf.reasignedUser")).append(" ").append(usuario.getNamePerson()).append(" ")
												.append(rb.getString("wf.reasignedAgain")).append("<br/>");
										msg.append(rb.getString("wf.reasignedCause")).append(": ").append(comments);
										// System.out.println("---MENSAJE: " +
										// msg);
										HandlerWorkFlows
												.notifiedUsers(rb.getString("wf.deniedTitle") + " " + prefixNumber, rb.getString("mail.nameUser"),
														HandlerParameters.PARAMETROS.getMailAccount(), usuarioDestino.getEmail(),
														msg.toString());
									}
								}
							}
						}
					}
					// if (!form.isOwner()) {
					// //Si el Flujo de Trabajo es Condicional
					// //y fu� rechazado o es un flujo de Aprobaci�n se cancela
					// el mismo
					// //No Se USA
					// if (wfConditional.equalsIgnoreCase(form.getConditional())
					// &&(wfuCanceled.equalsIgnoreCase(form.getResult())
					// ||wfuRejected.equalsIgnoreCase(form.getResult())commentsBackCheck.jsp))
					// {
					// //Se Queda en Pendiente los Usuarios que no han
					// respondido el Flujo de Trabajo
					// disabledUserWF(form.getIdWorkFlow(),con,wfuPending,0,Constants.notPermissionSt);
					// } else {
					// //Si el Flujo es de Aprobaci�n se debe devolver al
					// Usuario
					// //Inmediatamente anterior al que respondi� el Flujo
					// if
					// (!wfConditional.equalsIgnoreCase(form.getConditional())&&
					// (statuDoc.trim().equalsIgnoreCase(HandlerDocuments.docInApproved))
					// &&(wfuCanceled.equalsIgnoreCase(form.getResult())
					// ||wfuRejected.equalsIgnoreCase(form.getResult()))) {
					// //Crear nueva entrada para el usuario anterior
					// //que haya respondido el flujo de trabajo
					// //se desactiva a todos los usuarios
					// //mientras se procede con el roll-back del Flujo
					// log.debug("Deshabilitando Usuarios Pendientes en el Flujo de Trabajo");
					// disabledUserWF(form.getIdWorkFlow(),con,wfuPending,0,Constants.notPermissionSt);
					// disabledUserWF(form.getIdWorkFlow(),con,wfuQueued,0,Constants.notPermissionSt);
					// //Si hay un usuario Previo
					// if (ToolsHTML.isNumeric(beanNotified.getRow())&&
					// !"0".equalsIgnoreCase(beanNotified.getRow())) {
					// updateWFPending(form.getIdWorkFlow(),form.getRow(),Integer.parseInt(beanNotified.getRow()),
					// Constants.notPermission,form.getIdFather()==0,con);
					// } else {
					// //Se debe notificar al Iniciador del Flujo
					// // updateUserWorkFlows(con,wfuPending,rowOwner);
					// }
					// if (idUserPrevious!=null) {
					// Object[] datos = {idUserPrevious.getDescript()};
					// //Se crea la Nueva entrada para el �ltimo usuario que
					// hab�a aceptado el
					// //Flujo de Trabajo
					// String userWF =
					// processList(datos,st,con,form.getIdWorkFlow(),0,
					// Integer.parseInt(form.getSecuential()),null,
					// Long.parseLong(idUserPrevious.getId()),
					// Integer.parseInt(idUserPrevious.getAditionalInfo()));
					// beanNotified.setEmail(idUserPrevious.getContact());
					// //Se desactiva la respuesta Anterior del Usuario que
					// actualmente responde
					// //el Flujo de Trabajo
					// disabledUserWF(form.getIdWorkFlow(),con,null,Long.parseLong(idUserPrevious.getId()),
					// Constants.notPermissionSt);
					// } else {
					// //Se Debe devolver el Flujo al Propietario �?
					// }
					// } else {
					// if
					// (!wfConditional.equalsIgnoreCase(form.getConditional())&&
					// (statuDoc.trim().equalsIgnoreCase(HandlerDocuments.docInApproved))
					// &&(wfuAcepted.equalsIgnoreCase(form.getResult()))) {
					// if (idUserNext!=null) {
					// log.debug("idUserNext " + idUserNext);
					// Object[] datos = {idUserNext.getDescript()};
					// //Se crea la Nueva entrada para el �ltimo usuario que
					// hab�a Rechazado el
					// //Flujo de Trabajo
					// String userWF =
					// processList(datos,st,con,form.getIdWorkFlow(),0,
					// Integer.parseInt(form.getSecuential()),null,
					// Long.parseLong(idUserNext.getId()),
					// Integer.parseInt(idUserNext.getAditionalInfo()));
					// //Se desactiva la respuesta Anterior del Usuario que
					// actualmente responde
					// //el Flujo de Trabajo
					// disabledUserWF(form.getIdWorkFlow(),con,null,form.getIdFather(),
					// Constants.notPermissionSt);
					// //Se Desactiva la Respuesta Anterior del Usuario
					// //que previamente hab�a rechazado el Flujo de Trabajo
					// disabledUserWF(form.getIdWorkFlow(),con,wfuRejected,Long.parseLong(idUserNext.getId()),
					// Constants.notPermissionSt);
					// beanNotified.setEmail(idUserNext.getContact());
					// } else {
					// if (beanRejected!=null&&
					// beanRejected.getIdUser().equalsIgnoreCase(form.getIdParticipation())
					// &&pendingUsers) {
					// //Si existen usuarios con Statu pendiente.
					// //se deben activar. si el flujo es secuencial se activa
					// el usuario q estaba
					// //en cola, sino se activan todos los usuarios que no
					// hab�an respondido
					// //el mismo
					// if (wfSecuential.equalsIgnoreCase(form.getSecuential()))
					// {
					// //
					// disabledUserWF(form.getIdWorkFlow(),con,wfuPending,beanRejected.getIdPending(),
					// // Constants.permissionSt);
					// disabledUserWF(form.getIdWorkFlow(),con,wfuQueued,beanRejected.getIdPending(),
					// Constants.permissionSt);
					// } else {
					// disabledUserWF(form.getIdWorkFlow(),con,wfuPending,0,
					// Constants.permissionSt);
					// }
					//
					// } else {
					// //Se debe notificar al iniciador del Flujo siempre y
					// //cuando el mismo haya sido previamente rechazado
					// log.debug("AQUI VA " + beanRejected);
					// if (beanRejected!=null) {
					// // updateUserWorkFlows(con,wfuPending,rowOwner);TODO
					// PENDIENTE
					// } else {
					// //Curso Normal de Flujo de Aprobaci�n Secuencial.
					// if (wfSecuential.equalsIgnoreCase(form.getSecuential())||
					// beanNotified.isOwnerWF()) {
					// updateUserFlexWorkFlows(con,wfuPending,beanNotified.getRow());
					// }
					// }
					// }
					// }
					// } else {
					// log.debug("Por aqui deber�a chequearse si el flujo es de revisi�n :((");
					// //Si el Flujo es Secuencial se activa el siguiente
					// //usuario que debe responder.
					// if (newActToFlexFlow!=-1) {
					// updateFlexFlows(con,"1",form.getIdDocument(),newActToFlexFlow,time);
					// }
					// if (wfSecuential.equalsIgnoreCase(form.getSecuential()))
					// {
					// updateUserFlexWorkFlows(con,wfuPending,beanNotified.getRow());
					// } else {
					// if (beanNotified.isOwnerWF()) {
					// updateUserFlexWorkFlows(con,wfuPending,beanNotified.getRow());
					// }
					// }
					// //Si se debe cambiar de Flujo se Actualiza la Actividad
					// Completada :D
					// if (cmpFlexFlow) {
					// updateStatuWF("flexworkflow",form.getIdWorkFlow(),con,wfuAcepted,response,time);
					// }
					// //Si no hay m�s Actividades
					// //El ultimo Usuario Acept�
					// //y no hay m�s Usuarios Pendientes se Actualiza el Estado
					// del Documento
					// //Ya que el Flujo ha Sido Completado Exitosamente
					// log.debug("newActToFlexFlow = " + newActToFlexFlow);
					// if (newActToFlexFlow==-1 &&
					// beanNotified!=null&&beanNotified.getRow()==null) {
					// /*HandlerDocuments.updateStateDoc(con,form.getIdDocument(),HandlerDocuments.docApproved,statuDoc,
					// HandlerDocuments.lastApproved,form.getNumVersion(),time);*/
					// String newState = HandlerDocuments.docApproved;
					// // String lastOpe = HandlerDocuments.lastApproved;
					// // String typeMovHist = Constants.historyApproved;
					// Timestamp dateExpireEnd = null;
					// int value = 0;
					// if (ToolsHTML.isNumeric(monthExpireDoc.trim())) {
					// value = Integer.parseInt(monthExpireDoc.trim());
					// }
					// java.util.Date dateExpireCalculated =
					// ToolsHTML.getDateExpireDocument(ToolsHTML.sdf.format(date),
					// null,value,unit);
					// if (dateExpireCalculated!=null) {
					// dateExpireEnd = new
					// Timestamp(dateExpireCalculated.getTime());
					// }
					// //colocamos la fecha de expiracion actulizada con la
					// fecha en que se aprobo el documento nuevamente
					// version.setDateExpires(String.valueOf(dateExpireEnd));
					//
					// version.setApproved("0");
					// version.setDateApproved(ToolsHTML.sdfShowConvert.format(time));
					// if (ToolsHTML.isEmptyOrNull(version.getDateExpires())) {
					// version.setDateExpires(ToolsHTML.sdfShowConvert.format(dateExpireEnd));
					// }
					//
					// if (ToolsHTML.isNumeric(version.getMinorVer())) {
					// version.setMinorVer("0");
					// } else {
					// if
					// (!ToolsHTML.isEmptyOrNull(version.getMinorVer().trim()))
					// {
					// version.setMinorVer("A");
					// } else {
					// version.setMinorVer("");
					// }
					// }
					//
					// if (ToolsHTML.isNumeric(version.getMayorVer())) {
					// int mayorVer =
					// Integer.parseInt(version.getMayorVer().trim()) + 1;
					// version.setMayorVer(String.valueOf(mayorVer));
					// } else {
					// version.setMayorVer(ToolsHTML.incVersion(version.getMayorVer().trim()));
					// }
					// version.setIdDocument(String.valueOf(form.getIdDocument()));
					// // Si la versi�n sometida a Flujo se encuentra en
					// Borrador
					// // o Revisada la misma pasa a ser el Documento Aprobado
					// if
					// (HandlerDocuments.docTrash.equalsIgnoreCase(version.getStatu())
					// ||HandlerDocuments.docReview.equalsIgnoreCase(version.getStatu()))
					// {
					// HandlerDocuments.updateStatuVersionDoc(form.getNumVersion(),con,newState,time,
					// dateExpireEnd,null,version.getMayorVer(),version.getMinorVer(),false);
					// } else {
					// //Sino se crea una nueva versi�n del Documento
					// version.setStatu(newState);
					// HandlerDocuments.createNewVersionDoc(version,con,time,stream);
					// form.setNumVersion(version.getNumVer());
					// }
					//
					// //Se procede a Mantener actualizadas las versiones
					// mayores
					// // seg�n la configuraci�n actual del Sistema
					// if (histApproved.size() > 0 && mayorKeep <=
					// histApproved.size()) {
					// int limSup = histApproved.size() - mayorKeep;
					// //for (int row = 0; row <= limSup; row++) {
					// for (int row = 0; row < limSup; row++) {
					// HandlerDocuments.deleteLogicVersion(Constants.notPermission,(String)histApproved.get(row),
					// HandlerDocuments.docApproved,con);
					// }
					// }
					// // Se cambia el statu de la �ltima versi�n Aprobada a
					// Obsoleta
					// if (histApproved!=null && histApproved.size()>0) {
					// HandlerDocuments.updateStatuDoc(HandlerDocuments.docObs,(String)histApproved.get(histApproved.size()-1),
					// HandlerDocuments.docApproved,con);
					// }
					// HandlerDocuments.updateStateDoc(con,form.getIdDocument(),HandlerDocuments.docApproved,statuDoc,
					// HandlerDocuments.lastApproved,
					// form.getNumVersion(),time);
					// //Se Procede a Crear el Hist�rico del Flujo de Trabajo
					// updateWFHistoryFlexFlow(con,form.getIdFlexFlow(),form.getNameUser(),time,String.valueOf(form.getNumAct()));
					// //
					// updateWFHistoryFlexFlow(con,form.getIdWorkFlow(),form.getNameUser(),time,String.valueOf(form.getIdFlexFlow()));
					// }
					// }
					// }
					// }
					// }
				}
				con.commit();
			} catch (Exception ex) {
				log.error("Error: " + ex.getLocalizedMessage(), ex);
				applyRollback(con);
				throw new ApplicationExceptionChecked("E0017");
			} finally {
				setFinally(con, st);
			}
		}
		// Retornando el Resultado
		return resp;
	}

	/*
	 * Metodo utilizado cuando se reinicia un FTP, para colocar todas las actividades como pendiente
	 */
	public static synchronized void reInitActFlexFlow(long IDFlexFlow, boolean filterIdWorkFlow, String compare, long idWorkFlow, boolean deleteExpire)
			throws ApplicationExceptionChecked {
		Connection con = null;
		try {
			// Se colocan todas las actividades con result Pendiente y status en
			// cola
			StringBuilder sUpdate = new StringBuilder();
			sUpdate.append("UPDATE flexworkflow SET statu=?, result=?, dateCompleted=NULL ");
			if (deleteExpire) {
				sUpdate.append(", expire=cast(0 as bit), dateexpire=NULL ");
			}
			sUpdate.append("WHERE IDFlexFlow=?");
			if (filterIdWorkFlow && !ToolsHTML.isEmptyOrNull(compare) && idWorkFlow > 0) {
				sUpdate.append(" AND idWorkFlow " + compare + " " + idWorkFlow);
			}

			con = JDBCUtil.getConnection(Thread.currentThread().getStackTrace()[1].getMethodName());
			con.setAutoCommit(false);

			PreparedStatement pstm = con.prepareStatement(JDBCUtil.replaceCastMysql(sUpdate.toString()));
			log.debug("[reInitActFlexFlow idFlexFlow =" + IDFlexFlow + "] " + sUpdate.toString());

			pstm.setString(1, inQueued);
			pstm.setString(2, wfuPending);
			pstm.setLong(3, IDFlexFlow);

			log.debug("Actividades Modificadas: " + pstm.executeUpdate());
			con.commit();

			// Solo se coloca la primera actividad con status pendiente
			con = JDBCUtil.getConnection(Thread.currentThread().getStackTrace()[1].getMethodName());
			con.setAutoCommit(false);
			PreparedStatement pstmPending = null;
			StringBuilder sUpdatePending = new StringBuilder(100);
			if (filterIdWorkFlow && idWorkFlow > 0) {
				sUpdatePending.append("UPDATE flexworkflow SET statu=? WHERE idWorkFlow = " + idWorkFlow);
				pstmPending = con.prepareStatement(JDBCUtil.replaceCastMysql(sUpdatePending.toString()));
				pstmPending.setString(1, pending);
			} else {
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

				StringBuffer query = new StringBuffer();
				query.append("Select ").append(limiteMsSql);
				query.append(" idWorkFlow from flexworkflow WHERE IDFlexFlow=");
				query.append(IDFlexFlow).append(" order by idWorkFlow asc ");
				query.append(limitePostgres);

				sUpdatePending.append("UPDATE flexworkflow SET statu=? WHERE idWorkFlow IN (");
				sUpdatePending.append(JDBCUtil.executeQueryRetornaIds(query));
				sUpdatePending.append(")");
				pstmPending = con.prepareStatement(JDBCUtil.replaceCastMysql(sUpdatePending.toString()));
				pstmPending.setString(1, pending);
			}

			log.debug("Primera actividad como pendiente: " + pstmPending.executeUpdate());
			con.commit();

		} catch (Exception ex) {
			ex.printStackTrace();
			applyRollback(con);
			throw new ApplicationExceptionChecked("E0017");
		} finally {
			setFinally(con);
		}
	}

	// public static synchronized void updateWFPending(int idWF,int
	// idRechazo,int idPending,
	// byte statu,boolean exist,Connection con) throws SQLException {
	// PreparedStatement st = null;
	// StringBuilder edit = new StringBuilder(50);
	// if (exist) {
	// edit.append("INSERT INTO workflowspendings (idWorkFlow,idRechazo,idPending,statu)");
	// edit.append(" VALUES (").append(idWF).append(",").append(idRechazo);
	// edit.append(",").append(idPending).append(",").append(statu).append(")");
	// } else {
	// edit.append("UPDATE WorkFlowsPendings SET idRechazo = ").append(idRechazo);
	// edit.append(" WHERE idWorkFlow = ").append(idWF);
	// }
	// log.debug("HandlerWorkFlows.updateWFPending " + edit);
	// st = con.prepareStatement(edit.toString());
	// st.executeUpdate();
	// }                                                                                          AS typeWF         AS sybtypeWF   GRG 1/10/2020               

	public static void loadParticipation(ParticipationForm form, String tableWF, String tableUserWF, boolean owner, boolean isFlexFlow, boolean byStatu)
			throws Exception {
		StringBuilder sql = new StringBuilder(100);
		sql.append("SELECT uw.*,wf.secuential,wf.conditional,wf.expire,wf.expire,wf.owner,wf.type AS typeWF,wf.subtype AS subtypeWF FROM ");
		sql.append(tableWF).append(" wf,");
		sql.append(tableUserWF).append(" uw ");
		sql.append(" WHERE wf.idWorkFlow = uw.IdWorkFlow");
		sql.append(" AND uw.idWorkFlow = ").append(form.getIdWorkFlow());
		sql.append(" AND uw.idUser='").append(form.getIdParticipation()).append("'");
		if (form.getRow() > 0) {
			if (isFlexFlow) {
				sql.append(" AND idFlexWF = ").append(form.getRow());
			} else {
				if (!byStatu) {
					//sql.append(" AND `row`= ").append(form.getRow());
					sql.append(" AND row = ").append(form.getRow());
				}
			}
		}
		// S�lo en los Flujos est�ticos se debe manejar si es el propietario o
		// No del Flujo
		// el Usuario actualmente Logeado
		if (!isFlexFlow) {
			if (byStatu) {
				sql.append(" AND uw.statu = '1' ");
			} else {
				if (owner) {
					sql.append(" AND uw.isOwner = '1'");
				} else {
					sql.append(" AND uw.isOwner = '0'");
				}
			}
		}
		log.debug("loadParticipation " + sql.toString());
		Properties prop = JDBCUtil.doQueryOneRow(sql.toString(),Thread.currentThread().getStackTrace()[1].getMethodName());
		if (prop.getProperty("isEmpty").equalsIgnoreCase("false")) {
			String statu = prop.getProperty("statu").trim();
			if (ToolsHTML.isNumeric(statu)) {
				form.setStatu(Integer.parseInt(statu.trim()));
			}
			if (isFlexFlow) {
				form.setRow(Integer.parseInt(prop.getProperty("idFlexWF")));
			} else {
				form.setRow(Integer.parseInt(prop.getProperty("row")));
			}
			form.setDateReply(ToolsHTML.formatDateShow(prop.getProperty("dateReplied"), true));
			form.setCommentsUser(prop.getProperty("comments"));
			form.setResult(prop.getProperty("result").trim());
			form.setIdMovement(prop.getProperty("uw.row"));
			// ydavila Ticket 001-00-003023
			form.setTypeWF(prop.getProperty("typewf"));
			form.setSubtypeWF(prop.getProperty("subtypewf"));
			form.setConditional(prop.getProperty("conditional"));
			form.setSecuential(prop.getProperty("secuential"));
			form.setExpire(prop.getProperty("expire"));
			form.setNotified(prop.getProperty("expire"));
			form.setisOwner("1".equalsIgnoreCase(prop.getProperty("isOwner")));
			if (!isFlexFlow) {
				if (prop.getProperty("isOwner").equalsIgnoreCase("1")) {
					form.setisOwner(true);
				}
			}
			form.setIdFather(0);
			form.setOwnerWF(prop.getProperty("owner"));
			String idFather = prop.getProperty("IdFather");
			if (ToolsHTML.isNumeric(idFather)) {
				form.setIdFather(Long.parseLong(idFather.trim()));
			}
		} else {
			if (byStatu) {
				loadParticipation(form, tableWF, tableUserWF, owner, isFlexFlow, false);
			}
		}
	}

	/**
	 *
	 * @param idWF
	 * @param tableUser
	 * @return
	 */
	public static synchronized ParticipationForm getStatuLastAnswer(int idWF, String tableUser) {
		ParticipationForm resulta = null;
		try {
			StringBuilder sql = new StringBuilder(2048).append("SELECT result,statu FROM ").append(tableUser).append(" WHERE idWorkFlow = ").append(idWF)
					.append(" AND wfActive   = '").append(Constants.permission).append("'").append(" AND isOwner = '").append(Constants.notPermission)
					.append("'").append(" AND datereplied is not null ").append(" ORDER BY dateReplied DESC ");
			log.debug("[getStatuLastAnswer]: " + sql.toString());
			Vector datos = JDBCUtil.doQueryVector(sql.toString(),Thread.currentThread().getStackTrace()[1].getMethodName());
			if (datos.size() > 0) {
				Properties prop = (Properties) datos.get(0);
				String result = prop.getProperty("result").trim();
				String statu = prop.getProperty("statu").trim();
				log.debug("Statu : " + statu);
				log.debug("Result: " + result);
				resulta = new ParticipationForm();
				resulta.setStatu(Integer.parseInt(statu));
				resulta.setResult(result);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return resulta;
	}

	/**
	 * Este m�todo permite obtener todos los usuarios que participan en el flujo de trabajo indicado
	 */
	public static synchronized Object[] getParticipationsInWF(int idWF) {
		StringBuilder sql = new StringBuilder(50);
		sql.append("SELECT uw.orden,uw.idUser,uw.type,");
		sql.append(" w.idDocument,w.owner,w.secuential,w.conditional,");
		sql.append(" w.notified,w.expire,w.statu,w.type,w.result,w.comments,w.idVersion,");
		sql.append(" w.idLastVersion FROM user_workflows uw,workflows w");
		sql.append(" WHERE w.idWorkflow = ").append(idWF);
		sql.append("   AND w.idWorkflow = uw.idWorkflow");
		sql.append("   AND IdFather = ").append(Constants.notPermission);
		sql.append("   AND isOwner  = ").append(Constants.notPermission);
		sql.append(" ORDER BY orden,row");
		Object[] result = null;
		try {
			log.debug("[getParticipationsInWF] " + sql.toString());
			Vector data = JDBCUtil.doQueryVector(sql.toString(),Thread.currentThread().getStackTrace()[1].getMethodName());
			if (data.size() > 0) {
				result = new Object[data.size()];
			}
			for (int row = 0; row < data.size(); row++) {
				Properties properties = (Properties) data.elementAt(row);
				result[row] = properties.getProperty("idUser");
			}
		} catch (Exception ex) {
			log.error(ex.getMessage());
			ex.printStackTrace();
		}
		return result;
	}

	/**
	 * Procedimiento que permite reiniciar el flujo de trabajo.
	 *
	 * @param idWorkFlow
	 */
	private static synchronized void reInitWF(Object[] usuarios, int idWorkFlow, int secuential, Users usuario, String titleMail, String message,
			Connection con, int row) {
		PreparedStatement st = null;
		ArrayList emailsUsers = new ArrayList();
		if (usuarios != null) {
			try {
				disableLastCircle(idWorkFlow, row, con);
				String userWF = processList(usuarios, st, con, idWorkFlow, 0, secuential, Constants.notPermission, 0, 0);
				// con.commit();
				if (!ToolsHTML.isEmptyOrNull(userWF)) {
					HandlerGrupo.getEmailForUsers(userWF, emailsUsers);
					SendMail.send(emailsUsers, usuario, titleMail, message);

				}
			} catch (Exception ex) {
				log.error(ex.getMessage());
				ex.printStackTrace();
			} finally {
				setFinally(null, st);
			}
		}

	}

	private static void disableLastCircle(int idWF, int idRow, Connection con) {
		StringBuilder edit = new StringBuilder(50);
		PreparedStatement st = null;
		edit.append("UPDATE user_workflows SET uw_Circle = ").append(Constants.notPermission);
		edit.append(" WHERE idWorkFlow = ").append(idWF);
		// .append(" AND `row`<= ").append(idRow);
		try {
			st = con.prepareStatement(JDBCUtil.replaceCastMysql(edit.toString()));
			st.executeUpdate();
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			setFinally(null, st);
		}
	}

	public static synchronized long getNewActFlexFlow(long idWorkFlow, long idDocument) {
		StringBuilder sql = new StringBuilder("SELECT idWorkFlow FROM flexworkflow WHERE cast(idDocument as int) = ");
		sql.append(idDocument).append(" AND idWorkFlow > ").append(idWorkFlow);
		sql.append(" AND statu = '6'");
		sql.append(" ORDER BY idWorkFlow");
		log.debug("[getNewActFlexFlow] " + sql);
		Vector datos = null;
		try {
			datos = JDBCUtil.doQueryVector(sql.toString(),Thread.currentThread().getStackTrace()[1].getMethodName());
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		if (datos != null && !datos.isEmpty()) {
			Properties prop = (Properties) datos.get(0);
			long result = Long.parseLong(prop.getProperty("idWorkFlow"));
			return result;
		}
		return -1;
	}

	/**
	 * Este m�todo permite obtener el id del �ltimo usuario que respondi� aceptando el flujo de Trabajo
	 *
	 * @param idWorkFlow
	 */
	public static synchronized Search getPreviousFlexResponse(int idWorkFlow, String result, String idUser) {
		StringBuilder sql = new StringBuilder("SELECT idFlexWF,idUser,orden,p.email,uw.idWorkFlow FROM user_flexworkflows uw, person p");
		sql.append(" WHERE idWorkFlow = ").append(idWorkFlow);
		sql.append(" AND result = '").append(result).append("' ");
		sql.append(" AND p.nameUser = uw.idUser AND p.accountActive = '1' AND uw_Circle = '1'");
		if (!ToolsHTML.isEmptyOrNull(idUser)) {
			sql.append(" AND idUser <> '").append(idUser).append("'");
			sql.append(" AND wfActive = '").append(Constants.permission).append("'");
		}
		sql.append(" ORDER BY dateReplied DESC");
		log.debug("[getPreviousRowResponse] " + sql);
		Vector datos = null;
		try {
			datos = JDBCUtil.doQueryVector(sql.toString(),Thread.currentThread().getStackTrace()[1].getMethodName());
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		if (datos != null && !datos.isEmpty()) {
			Properties prop = (Properties) datos.get(0);
			Search bean = new Search(prop.getProperty("idFlexWF"), prop.getProperty("idUser"));
			bean.setAditionalInfo(prop.getProperty("orden").trim());
			bean.setContact(prop.getProperty("email"));
			bean.setIdWorkFlow(prop.getProperty("idWorkFlow"));
			return bean;
		}
		return null;
	}

	public static synchronized Search getPreviousFlexActivityResponse(long idFlexFlow, String result, String idUser) {
		StringBuilder sql = new StringBuilder("SELECT idFlexWF,idUser,uw.orden,p.email,fwf.idFlexFlow,uw.idWorkFlow");
		sql.append(" FROM user_flexworkflows uw,flexworkflow fwf, person p WHERE fwf.idWorkFlow = uw.idWorkFlow");
		sql.append(" AND fwf.idFlexFlow = ").append(idFlexFlow);
		sql.append(" AND uw.result = ").append(result);
		sql.append(" AND p.nameUser = uw.idUser AND p.accountActive = '1' AND uw_Circle = '1'");
		if (!ToolsHTML.isEmptyOrNull(idUser)) {
			sql.append(" AND idUser <> '").append(idUser).append("'");
			sql.append(" AND wfActive = '").append(Constants.permission).append("'");
		}
		sql.append(" ORDER BY dateReplied DESC");
		log.debug("[getPreviousFlexActivityResponse] " + sql);
		Vector datos = null;
		try {
			datos = JDBCUtil.doQueryVector(sql.toString(),Thread.currentThread().getStackTrace()[1].getMethodName());
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		if (datos != null && !datos.isEmpty()) {
			Properties prop = (Properties) datos.get(0);
			Search bean = new Search(prop.getProperty("idFlexWF"), prop.getProperty("idUser"));
			bean.setAditionalInfo(prop.getProperty("orden").trim());
			bean.setContact(prop.getProperty("email"));
			bean.setIdWorkFlow(prop.getProperty("idWorkFlow"));
			return bean;
		}
		return null;
	}

	/**
	 * M�todo para Obtener todos los Usuarios en el Flujo Param�trico Se usa al Momento de Reiniciar un Flujo si el mismo Fu� Rechazado por el Primer Usuario
	 * del Mismo
	 *
	 * @param IDFlexFlow
	 * @return Collection
	 */
	public static Collection getUserInFlexFlow(long IDFlexFlow) throws Exception {
		StringBuilder sql = new StringBuilder(50);
		sql.append("SELECT uwf.idFlexWF,uwf.orden,uwf.idUser,uwf.idWorkFlow,uwf.type,uwf.result,uwf.statu,uwf.IdFather,ff.secuential");
		sql.append("  FROM user_flexworkflows uwf,flexworkflow ff");
		sql.append(" WHERE uwf.idWorkFlow = ff.idWorkFlow AND ff.IDFlexFlow  = ").append(IDFlexFlow);
		sql.append(" AND idFather = '0' AND uwf.result <> '").append(wfuPending).append("' ");
		sql.append("   AND uwf.isOwner = '0' ORDER BY uwf.idFlexWF");
		Vector result = new Vector();
		log.debug("[getUserInFlexFlow] query " + sql);
		Vector datos = JDBCUtil.doQueryVector(sql.toString(),Thread.currentThread().getStackTrace()[1].getMethodName());
		FlexFlow bean = null;
		for (int i = 0; i < datos.size(); i++) {
			Properties properties = (Properties) datos.elementAt(i);
			bean = new FlexFlow();
			bean.setIdFlexWF(Long.parseLong(properties.getProperty("idFlexWF")));
			log.debug("bean.getIdFlexWF(): " + bean.getIdFlexWF());
			bean.setOrden(Integer.parseInt(properties.getProperty("orden")));
			bean.setIdUser(properties.getProperty("idUser"));
			bean.setIdWorkFlow(Integer.parseInt(properties.getProperty("idWorkFlow")));
			bean.setType(Byte.parseByte(properties.getProperty("type")));
			bean.setResult(properties.getProperty("result"));
			bean.setStatu(properties.getProperty("statu"));
			bean.setIdFather(Long.parseLong(properties.getProperty("IdFather")));
			bean.setSecuential(Byte.parseByte(properties.getProperty("secuential")));
			result.add(bean);
		}
		log.debug("[RETURN] getUserInFlexFlow");
		return result;
	}

	private static void disableLastCircleFlex(long idWF, Connection con) {
		StringBuilder edit = new StringBuilder(50);
		PreparedStatement st = null;
		edit.append("UPDATE user_flexworkflows SET uw_Circle = ").append(Constants.notPermission);
		edit.append(" WHERE idWorkFlow = ").append(idWF);
		try {
			st = con.prepareStatement(JDBCUtil.replaceCastMysql(edit.toString()));
			st.executeUpdate();
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			setFinally(null, st);
		}
	}

	public static synchronized void updateWFViewInCanceled(String idDocument, String viewInCanceled, String type) {

		Connection con = null;
		PreparedStatement st = null;
		StringBuilder update = new StringBuilder("UPDATE workflows set viewInCanceled = CAST(? as bit)");
		update.append("WHERE (type = CAST(? as bit)) AND (statu = '4') AND (idDocument = cast(? as int))");

		log.debug("[updateWFViewInCanceled]" + update.toString());
		try {
			con = JDBCUtil.getConnection(Thread.currentThread().getStackTrace()[1].getMethodName());

			st = con.prepareStatement(JDBCUtil.replaceCastMysql(update.toString()));
			st.setInt(1, Integer.parseInt(viewInCanceled));
			st.setInt(2, Integer.parseInt(type));
			st.setInt(3, Integer.parseInt(idDocument));

			log.debug("[updateWFViewInCanceled]" + st.executeUpdate() + " registros modificados");

			con.close();
		} catch (SQLException e) {
			e.printStackTrace();
			log.debug("Error en SQL ");

		} catch (ApplicationExceptionChecked applicationExceptionChecked) {
			applicationExceptionChecked.printStackTrace(); // To change body of
															// catch statement
															// use File |
															// Settings | File
															// Templates.
		} finally { // PENDIENTE
			try {
				if (st != null) {
					st.close();
				}
				if (con != null && !con.isClosed()) {
					con.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}

		}
	}

	public static void reInitFlexFlow(long IDFlexFlow, Vector usrs, Connection con) throws Exception {
		Object[] datos; // TODO
		Vector items = new Vector();
		Vector actividades = new Vector();
		if (usrs != null) {
			log.debug("[Size] " + usrs.size());
			PreparedStatement st = null;
			// Para cada usuario en el flujo.
			for (int i = 0; i < usrs.size(); i++) {
				FlexFlow flexFlow = (FlexFlow) usrs.elementAt(i);

				// Si el flujo parametrico del usuario no esta entre las
				// actividades (El WF Ppal)
				if (flexFlow != null && !actividades.contains(new Long(flexFlow.getIdWorkFlow()))) {
					// Simple, para no duplicar
					actividades.add(new Long(flexFlow.getIdWorkFlow())); // Lo
																			// agrego
																			// como
																			// una
																			// actividad
																			// (
				}
				// Si no es el ultimo Y el siguiente flujo de usuario es de la
				// misma actividad
				if ((i + 1 < usrs.size()) && ((FlexFlow) usrs.elementAt(i + 1)).getIdWorkFlow() == flexFlow.getIdWorkFlow()) {
					// Agrego el Usuario (Flujo de Usuario?) a la lista de
					// flujos a crear
					log.debug("[Flex Flow]" + flexFlow.getIdUser());
					items.add(flexFlow.getIdUser());
					// enabledUserFlexFlow(con,Constants.notPermission,flexFlow.getIdFlexWF(),
					// Constants.permission,"'"+wfuPending + "','"+ wfuQueued +
					// "'");
				} else {
					items.add(flexFlow.getIdUser());
					// Se crea una Nueva Entrada para el Flujo de Trabajo x Cada
					// Usuario
					// que hab�a rechazado el Mismo
					datos = new Object[items.size()];
					for (int j = 0; j < items.size(); j++) {
						String usr = (String) items.elementAt(j);
						log.debug("[usr] " + usr);
						datos[j] = usr;
					}

					String userWF = processList(datos, st, con, flexFlow.getIdWorkFlow(), 0, flexFlow.getSecuential(), Constants.notPermission,
							flexFlow.getIdFlexWF(), flexFlow.getOrden());
					// Deshabilitando Respuesta Anterior sobre el Flujo de
					// Trabajo
					enabledUserFlexFlow(con, Constants.notPermission, flexFlow.getIdWorkFlow(), Constants.permission, "'" + wfuAcepted + "'", true);

					log.debug("[userWF] " + userWF);
					// Se reinicia el Vector para Guardar los Datos de la
					// Siguiente Actividad
					// Si Aplica
					items = new Vector();
				}
			}
			// Habilitando el Flujo para los Usuarios que no lo han respondido
			// Dichos Usuarios quedan con el Mismo Statu que ten�an en el Flujo
			// antes del Primer Rechazo

			// LUIS CISNEROS
			// Comentado el 16-03-07
			// Los flujos no se deben reutilizar, se les coloc� anulados, y as�
			// se quedan, para que queden en orden.
			// enabledUserFlexFlow(con,Constants.permission,IDFlexFlow,Constants.notPermission,"'"+wfuPending
			// + "','"+ wfuQueued + "'",false);

			// Habilitando de Nuevos las Actividades si las Mismas ya hab�an
			// sido completadas anteriormente
			// if (actividades.size()>0) {
			// long idWF = 0;
			// for (int row = 0; row < actividades.size(); row++) {
			// idWF = ((Long)actividades.get(row)).longValue();
			// if (row == 0) {
			// enableActivityInFlexFlow(idWF,con,pending);
			// } else {
			// enableActivityInFlexFlow(idWF,con,inQueued);
			// }
			// }
			// }
			// Fin 16-03-07
			con.commit();
		}
	}

	// public static Collection getAllWorkFlowsUserAndStatu(boolean
	// wfExpires,String statu,
	// String idUser,String statuUser,
	// boolean owner,ResourceBundle rb) throws Exception {
	// Vector resp = new Vector();
	// StringBuilder sql = new StringBuilder(100);
	// sql.append("SELECT w.idWorkFlow,w.type,w.dateExpire,uw.idUser,uw.row,d.nameDocument,vd.MayorVer,vd.MinorVer");
	// sql.append(" FROM workflows w,user_workflows uw,documents d,versiondoc vd WHERE w.idWorkFlow = uw.idWorkFlow");
	// sql.append(" AND w.idVersion = vd.numVer");
	// sql.append(" AND uw.idUser = '").append(idUser).append("'");
	// sql.append(" AND d.numGen = w.idDocument");
	// sql.append(" AND uw.wfActive = ").append(Constants.permission);
	// if (!ToolsHTML.isEmptyOrNull(statuUser)) {
	// sql.append(" AND uw.Statu = ").append(statuUser.trim());
	// }
	// if (!ToolsHTML.isEmptyOrNull(statu)){
	// sql.append(" AND w.Statu = ").append(statu.trim());
	// }
	// String dateSystem = ToolsHTML.sdfShowConvert1.format(new Date());
	// if (!wfExpires){
	// sql.append(" AND (dateExpire >= CONVERT(datetime,'");
	// sql.append(dateSystem).append("',120) OR dateExpire IS NULL) ");
	//
	// } else {
	// sql.append(" AND dateExpire < CONVERT(datetime,'");
	// sql.append(dateSystem).append("',120)");
	// sql.append(" AND pending = ").append(Constants.permission);
	// }
	// if (owner) {
	// sql.append(" AND isOwner = '1'");
	// } else {
	// sql.append(" AND isOwner = 0");
	// }
	// Connection con = null;
	// PreparedStatement st = null;
	// ResultSet rs = null;
	// try {
	// con = JDBCUtil.getConnection(Thread.currentThread().getStackTrace()[1].getMethodName());
	// log.debug("[getAllWorkFlowsUserAndStatu] = " + sql);
	// st = con.prepareStatement(JDBCUtil.replaceCastMysql(sql.toString()));
	// rs = st.executeQuery();
	// while(rs.next()) {
	// DataUserWorkFlowForm data = new DataUserWorkFlowForm();
	// data.setIdWorkFlow(rs.getInt("idWorkFlow"));;
	// data.setNameDocument(rs.getString("nameDocument"));
	// data.setIdUser(rs.getString("idUser"));
	// data.setTypeWF(rs.getInt("type"));
	// data.setRow(rs.getString("row"));
	// data.setOwner(false);
	// data.setFlexFlow(false);
	// data.setNameWorkFlow(rb.getString("wf.type"+data.getTypeWF()));
	// // java.sql.Date date = rs.getDate("dateExpire");
	// // if (date!=null) {
	// // log.debug("date = " + date);
	// //
	// data.setDateExpire(ToolsHTML.formatDateShow(ToolsHTML.sdfShowConvert.format(date),true));
	// // } else {
	// // data.setDateExpire("N/A");
	// // }
	//
	// java.sql.Timestamp date = rs.getTimestamp("dateExpire");
	// if (date!=null) {
	// data.setDateExpire(ToolsHTML.formatDateShow(ToolsHTML.sdfShowConvert.format(date),true));
	// } else {
	// data.setDateExpire("N/A");
	// }
	//
	// String minorVer = rs.getString("MinorVer");
	// if (ToolsHTML.isEmptyOrNull(minorVer)) {
	// minorVer = "";
	// }
	// String mayorVer = rs.getString("MayorVer");
	// if (ToolsHTML.isEmptyOrNull(mayorVer)) {
	// mayorVer = "";
	// }
	// String version = mayorVer.trim() + "." + minorVer.trim();
	// data.setIdVersion(version);
	// resp.add(data);
	// }
	// } catch(Exception e) {
	// e.printStackTrace();
	// } finally {
	// setFinally(con,st,rs);
	// }
	// return resp;
	// }

	/*
	 * Metodo que consulta todos los usuarios de la tabla user_flexworkflows filtrando por los campos idWorkFlow y statu
	 */
	public static ArrayList getUsersFlexWorkFlowsByIdWorkFlow(String compare, long idWorkFlow, String status, long IDFlexFlow) throws Exception {
		Connection con = JDBCUtil.getConnection(Thread.currentThread().getStackTrace()[1].getMethodName());
		PreparedStatement pstm = null;
		ResultSet rs = null;
		ArrayList usuarios = new ArrayList();
		StringBuilder sql = new StringBuilder();
		sql.append("select uwf.* from user_flexworkflows uwf, flexworkflow fwf where fwf.idWorkFlow=uwf.idworkflow and fwf.IDFlexFlow=" + IDFlexFlow
				+ " and wfactive = '" + Constants.permission + "' and uwf.idworkflow ");
		sql.append(" " + compare + " " + idWorkFlow);
		if (!ToolsHTML.isEmptyOrNull(status))
			sql.append(" and uwf.statu in ('" + status.replaceAll(",", "','") + "')");
		sql.append(" order by uwf.idFlexWF asc");

		try {
			pstm = con.prepareStatement(JDBCUtil.replaceCastMysql(sql.toString()));
			// //System.out.println("usuarios ByIdWorkFlow : " +
			// sql.toString());
			log.debug("[getUsersFlexWorkFlowsByIdWorkFlow] " + sql.toString());
			rs = pstm.executeQuery();

			while (rs.next()) {
				UserFlexWorkFlowsTO dato = new UserFlexWorkFlowsTO();
				dato.setIdWorkFlow(rs.getInt("idWorkFlow"));
				dato.setOrden(rs.getInt("orden"));
				dato.setIdUser(rs.getString("idUser"));
				dato.setType(rs.getByte("type"));
				dato.setResult(rs.getString("result"));
				dato.setStatu(rs.getString("statu"));
				dato.setIsOwner(rs.getByte("isOwner"));
				dato.setReading(rs.getByte("reading"));
				dato.setActive(rs.getByte("active"));
				dato.setPending(rs.getByte("pending"));
				dato.setWfActive(rs.getByte("wfActive"));
				// dato.setIdFather(rs.getLong("IdFather"));
				dato.setIdFather(rs.getLong("IdFlexWF"));
				dato.setUw_Circle(rs.getByte("uw_Circle"));
				usuarios.add(dato);
			}
		} catch (Exception e) {
			log.debug("Error al tratar de ejecutar query en getUsersFlexWorkFlowsByIdWorkFlow: " + e);
			e.printStackTrace();
		} finally {
			try {
				pstm.close();
				con.close();
			} catch (Exception e) {
				log.debug("Error al tratar de cerrar conexion en getUsersFlexWorkFlowsByIdWorkFlow: " + e);
			}
		}

		return usuarios;
	}

	/*
	 * Metodo que consulta todos los usuarios de la tabla user_flexworkflows filtrando por los campos idFlexWF y statu
	 */
	public static ArrayList getUsersFlexWorkFlowsByIdFlexWF(String compare, long idFlexWF, String status, long IDFlexFlow) throws Exception {
		Connection con = JDBCUtil.getConnection(Thread.currentThread().getStackTrace()[1].getMethodName());
		PreparedStatement pstm = null;
		ResultSet rs = null;
		ArrayList usuarios = new ArrayList();
		StringBuilder sql = new StringBuilder();
		// sql.append("select * from user_flexworkflows where idFlexWF");
		sql.append("select uwf.* from user_flexworkflows uwf, flexworkflow fwf where fwf.idWorkFlow=uwf.idworkflow and fwf.IDFlexFlow=" + IDFlexFlow
				+ " and wfactive='" + Constants.permission + "' and uwf.idFlexWF ");
		sql.append(" " + compare + " " + idFlexWF);
		if (!ToolsHTML.isEmptyOrNull(status))
			sql.append(" and uwf.statu in ('" + status.replaceAll(",", "','") + "')");
		sql.append(" order by uwf.idFlexWF asc");

		try {
			pstm = con.prepareStatement(JDBCUtil.replaceCastMysql(sql.toString()));
			// //System.out.println("usuarios ByIdFlexWF: " + sql.toString());
			log.debug("[getUsersFlexWorkFlowsByIdFlexWF] " + sql.toString());
			rs = pstm.executeQuery();

			while (rs.next()) {
				UserFlexWorkFlowsTO dato = new UserFlexWorkFlowsTO();
				dato.setIdWorkFlow(rs.getInt("idWorkFlow"));
				dato.setOrden(rs.getInt("orden"));
				dato.setIdUser(rs.getString("idUser"));
				dato.setType(rs.getByte("type"));
				dato.setResult(pending);
				dato.setStatu(wfuQueued);
				dato.setIsOwner(rs.getByte("isOwner"));
				dato.setReading(rs.getByte("reading"));
				dato.setActive(rs.getByte("active"));
				dato.setPending(rs.getByte("pending"));
				dato.setWfActive(rs.getByte("wfActive"));
				// dato.setIdFather(rs.getLong("IdFather"));
				dato.setIdFather(rs.getLong("IdFlexWF"));
				dato.setUw_Circle(rs.getByte("uw_Circle"));
				usuarios.add(dato);
			}
		} catch (Exception e) {
			log.debug("Error al tratar de ejecutar query en getUsersFlexWorkFlowsByIdFlexWF: " + e);
		} finally {
			try {
				pstm.close();
				con.close();
			} catch (Exception e) {
				log.debug("Error al tratar de cerrar conexion en getUsersFlexWorkFlowsByIdFlexWF: " + e);
			}
		}

		return usuarios;
	}

	/**
	 *
	 * @param form
	 * @param statu
	 * @param result
	 * @throws ApplicationExceptionChecked
	 */
	public static synchronized void saveResponseUser(ParticipationForm form, String statu, String result) throws ApplicationExceptionChecked {
		Connection con = null;
		java.util.Date date = new java.util.Date();
		Timestamp time = new Timestamp(date.getTime());
		try {
			// Se colocan todas las actividades con result Pendiente y status en
			// cola
			StringBuilder sUpdate = new StringBuilder();
			sUpdate.append("update user_flexworkflows SET statu=?, result=?, dateReplied=?, comments=? WHERE idFlexWF=?");

			con = JDBCUtil.getConnection(Thread.currentThread().getStackTrace()[1].getMethodName());
			con.setAutoCommit(false);

			PreparedStatement pstm = con.prepareStatement(JDBCUtil.replaceCastMysql(sUpdate.toString()));
			log.debug("[saveResponseUser] " + sUpdate.toString());

			pstm.setString(1, statu);
			pstm.setString(2, result);
			pstm.setTimestamp(3, time);
			pstm.setString(4, form.getCommentsUser());
			pstm.setLong(5, form.getRow());
			// //System.out.println("sql: update user_flexworkflows SET statu="
			// + statu + ", result=" + result + ", dateReplied=" + time +
			// ", comments=? WHERE idFlexWF=" + form.getRow());
			log.debug("Rechazo registrado: " + pstm.executeUpdate());
			con.commit();

		} catch (Exception ex) {
			ex.printStackTrace();
			applyRollback(con);
			throw new ApplicationExceptionChecked("E0017");
		} finally {
			setFinally(con);
		}
	}

	/**
	 *
	 * @param dataWF
	 * @return
	 * @throws Exception
	 */
	public static Collection getDocumentsFlexFlowOfRejection(DataWorkFlowForm dataWF) throws Exception {
		StringBuilder sql = new StringBuilder(2048)
				.append("SELECT a.datecreate,a.idflexflow,a.iduser,a.numgen,a.documentreject,(select max(numver) from versiondoc where numdoc=a.numgen) as numver ")
				.append("FROM docofrejection a, documents d ").append("WHERE a.numgen=d.numgen AND a.idflexflow=").append(dataWF.getIdFlexFlow()).append(" ")
				.append("ORDER BY a.datecreate ");

		log.debug("[getCommentsFlexFlowUser] " + sql);
		Vector datos = JDBCUtil.doQueryVector(sql.toString(),Thread.currentThread().getStackTrace()[1].getMethodName());
		ArrayList resp = new ArrayList();
		for (int row = 0; row < datos.size(); row++) {
			Properties properties = (Properties) datos.elementAt(row);
			DocOfRejection bean = new DocOfRejection();
			bean.setDatecreate(properties.getProperty("datecreate"));
			bean.setIdflexflow(properties.getProperty("idflexflow"));
			bean.setIduser(properties.getProperty("iduser"));
			bean.setNumgen(properties.getProperty("numgen"));
			bean.setDocumentreject(properties.getProperty("documentreject"));
			bean.setNumver(properties.getProperty("numver"));
			resp.add(bean);
		}
		return resp;
	}

	/**
	 *
	 * @param idWorkFlow
	 * @return
	 */
	public static String isFTPSecuential(int idWorkFlow) {
		String secuential = "1";
		PreparedStatement pstm = null;
		ResultSet rs = null;
		Connection con = null;
		try {
			StringBuilder sql = new StringBuilder(1024).append("SELECT secuential FROM flexworkflow WHERE idWorkFlow = '").append(idWorkFlow).append("' ");
			// //System.out.println("isFTPSecuential: " + sql.toString());
			con = JDBCUtil.getConnection(Thread.currentThread().getStackTrace()[1].getMethodName());
			pstm = con.prepareStatement(JDBCUtil.replaceCastMysql(sql.toString()));
			log.debug("[isFTPSecuential] " + sql.toString());
			rs = pstm.executeQuery();

			if (rs.next()) {
				secuential = rs.getString("secuential");
			}
		} catch (Exception e) {
			log.debug("Error al tratar de ejecutar query en isFTPSecuential: " + e);
		} finally {
			JDBCUtil.closeQuietly(con, pstm, rs);
		}
		return secuential;
	}

	/**
	 *
	 * @param idPerson
	 * @param status
	 * @param struct
	 * @param prefijos
	 * @param searchAsAdmin
	 * @param applyValidity
	 * @return
	 */
	public static synchronized Collection<DocumentsCheckOutsBean> getAllPrintingOwnerUserAndStatu(long idPerson, int status, Hashtable struct,
			Hashtable prefijos, boolean searchAsAdmin, boolean applyValidity) {
		StringBuilder sql = new StringBuilder(2048).append("SELECT d.numGen, d.nameDocument, v.MinorVer, v.MayorVer, s.datesolicitud, d.number, ")
				.append(" d.idnode, d.prefix, s.numSolicitud, v.numVer, wf.idworkflow, s.destinatarios, ")
				.append(" p.idperson, p.nombres, p.apellidos, p.email")
				.append(" FROM person p, tbl_solicitudimpresion s, documents d, versiondoc v, workflows wf ").append(" WHERE d.numgen = s.numgen ")
				.append(" AND wf.iddocument = s.numsolicitud ").append(" AND s.numver = v.numver ").append(" AND p.idperson = s.solicitante ");
		if (idPerson > 0 && !searchAsAdmin) {
			sql.append(" AND s.solicitante = '").append(idPerson).append("'");
		}
		sql.append(" AND s.active = '").append(Constants.permission).append("'").append(" AND d.active = '").append(Constants.permission).append("'");
		if (status > 0) {
			sql.append(" AND s.statusimpresion = '").append(status).append("'");
		}
		if (applyValidity) {
			int validity = 15;

			try {
				validity = HandlerParameters.PARAMETROS.getVigenciaFlujosCanceladosPrincipal();
			} catch (Exception e) {
				// TODO: handle exception
			}

			// verificamos el manejador
			switch (Constants.MANEJADOR_ACTUAL) {
			case Constants.MANEJADOR_MSSQL:
				sql.append(" AND DATEDIFF(DAY, s.datesolicitud, getdate()) <= ").append(validity);
				break;
			case Constants.MANEJADOR_POSTGRES:
				sql.append(" AND DATE_PART('day', now() - s.datesolicitud) <= ").append(validity);
				break;
			case Constants.MANEJADOR_MYSQL:
				sql.append(" AND DATEDIFF(now() , s.datesolicitud) <= ").append(validity);
				break;
			}
		}
		sql.append(" ORDER BY s.datesolicitud ASC ");

		log.debug("[getAllPrintingOwnerUserAndStatu] " + sql);
		// System.out.println("[getAllPrintingOwnerUserAndStatu] " + sql);
		Vector resp = new Vector();
		Vector datos = new Vector();
		try {
			datos = JDBCUtil.doQueryVector(sql.toString(),Thread.currentThread().getStackTrace()[1].getMethodName());
			for (int row = 0; row < datos.size(); row++) {
				Properties properties = (Properties) datos.elementAt(row);
				DocumentsCheckOutsBean bean = new DocumentsCheckOutsBean();
				String prefixDoc = null;
				String idNode = "";

				bean.setNumSolicitud(Integer.parseInt(properties.getProperty("numSolicitud")));
				bean.setIdDocument(properties.getProperty("numGen"));
				bean.setNameDocument(properties.getProperty("nameDocument"));
				bean.setNumVer(properties.getProperty("numVer"));
				bean.setDestinatarios(properties.getProperty("destinatarios"));
				bean.setIdWorkFlow(properties.getProperty("idWorkFlow"));
				// bean.setMayorVer(properties.getProperty("MayorVer").trim());
				// bean.setMinorVer(properties.getProperty("MinorVer").trim());
				String minorVer = properties.getProperty("MinorVer").trim();
				if (ToolsHTML.isEmptyOrNull(minorVer)) {
					minorVer = "";
				}
				String mayorVer = properties.getProperty("MayorVer").trim();
				if (ToolsHTML.isEmptyOrNull(mayorVer)) {
					mayorVer = "";
				}
				bean.setMayorVer(mayorVer);
				bean.setMinorVer(minorVer);

				bean.setNumber(properties.getProperty("number") != null ? properties.getProperty("number").trim() : "");
				// docCheck.setPrefix(properties.getProperty("prefix")!=null?properties.getProperty("prefix").trim():"");
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
					prefixDoc = properties.getProperty("prefix") != null ? properties.getProperty("prefix").trim() : "";
				}
				if (!ToolsHTML.isEmptyOrNull(prefixDoc)) {
					bean.setPrefix(prefixDoc.trim());
				} else {
					bean.setPrefix("");
				}

				// CALCULO FECHA DE EXPIRACION DE IMPRESION
				idNode = properties.getProperty("idNode");
				// Se Carga la Estructura para el manejo de los prefijos seg�n
				// la configuraci�n de la misma
				Users usuario = new Users();
				// Se Coloca el Grupo Administrador
				usuario.setIdGroup(DesigeConf.getProperty("application.admon"));
				// Se Coloca el Id del Usuario Administrador del Sistema
				usuario.setIdPerson(Long.parseLong(DesigeConf.getProperty("application.userAdmonKey")));
				usuario.setUser(DesigeConf.getProperty("application.userAdmon"));
				Hashtable tree = ToolsHTML.checkTree(null, usuario);

				String datesolicitud = properties.getProperty("datesolicitud");
				String idLocation = HandlerStruct.getIdLocationToNode(tree, idNode);
				BaseStructForm location = HandlerStruct.loadStruct(idLocation);
				java.util.Date fechalimite = new Date();
				java.util.Date FechaHoy1 = new Date();
				if (Constants.permission == location.getCheckvijenToprint()) {
					int nums = 0;
					// sumamos los dias para obtener la fecha que se debe vencer
					// dicho documento
					if (ToolsHTML.isNumeric(String.valueOf(location.getVijenToprint()))) {
						nums = location.getVijenToprint();
						fechalimite = ToolsHTML.sumUnitsToDate(Calendar.DAY_OF_MONTH, ToolsHTML.sdf.parse(datesolicitud), nums);
					}
				}
				// fechaImpresionlimite es igual a la fecha de solicitud + el
				// numero
				// de dias que se vencera
				// String fechaImpresionlimite =
				// ToolsHTML.date.format(fechalimite);
				String fechaImpresionlimite = ToolsHTML.sdfShowConvert1.format(fechalimite);
				// obtenemos la fecha de hoy
				// String FechaHoy = ToolsHTML.date.format(FechaHoy1);
				String FechaHoy = ToolsHTML.sdfShowConvert1.format(FechaHoy1);

				// if (fechaImpresionlimite.compareTo(FechaHoy) >= 0) {
				if (Constants.permission == location.getCheckvijenToprint()) {
					String fechalimiteFormat = ToolsHTML.sdfShow.format(fechalimite);
					bean.setDateCheckOut(fechalimiteFormat);
				} else {
					bean.setDateCheckOut("");
				}

				Person p = new Person(Integer.parseInt(properties.getProperty("idperson")), properties.getProperty("nombres"),
						properties.getProperty("apellidos"), properties.getProperty("email"));

				bean.setPersonBean(p);

				resp.add(bean);
				// } else {
				// //System.out.println("----fecha vencida: " +
				// fechaImpresionlimite);
				// }

			}
		} catch (Exception e) {
			// System.out.println("Error en getAllPrintingOwnerUserAndStatu: " +
			// e);
			e.printStackTrace();
		}
		return resp;
	}

	/**
	 *
	 * @param beaconsultamos
	 *            si el id del flujo debio ser notificadon
	 * @param idWorkFlow
	 * @param row
	 * @return
	 */
	public static BeanNotifiedWF getNotifiedFlexWF(BeanNotifiedWF bean, int idWorkFlow, int row) {
		try {
			StringBuilder query = new StringBuilder(1024).append("SELECT a.notified,b.comments,a.owner,c.nameDocument ")
					.append("FROM flexworkflow a,user_flexworkflows b, documents c ").append("WHERE a.idworkflow=b.idworkflow ")
					.append("AND c.numgen=a.idDocument ").append("AND a.idworkflow=? ").append("AND b.idFlexWF=?");

			ArrayList parametros = new ArrayList();
			parametros.add(idWorkFlow);
			parametros.add(row);
			CachedRowSet crs = JDBCUtil.executeQuery(query, parametros, Thread.currentThread().getStackTrace()[1].getMethodName());
			if (crs.next()) {
				if (crs.getString("notified").equals("false") || crs.getString("notified").equals("0")) {
					bean = new BeanNotifiedWF();
					bean.setOwner(crs.getString("owner"));
					bean.setComments(crs.getString("comments"));
					bean.setNameDocument(crs.getString("nameDocument"));
					bean.setNotified("0");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return bean;
	}

	/**
	 *
	 * @param st
	 * @param con
	 * @param idWorkFlow
	 * @param idFlexWF
	 * @return
	 * @throws Exception
	 */
	public static String updateExpireWF(PreparedStatement st, Connection con, long idWorkFlow, int idFlexWF) throws Exception {
		StringBuilder sb = new StringBuilder(1024);
		boolean isRow = false;
		if (idWorkFlow > 0) {
			sb.append("select min(dateExpireUser) As expire from user_flexworkflows ").append("where idWorkflow=").append(idWorkFlow).append(" and statu='1'");
		} else {
			isRow = true;
			sb.append("select dateExpireUser As expire, idWorkFlow from user_flexworkflows ").append("where idFlexWF=").append(idFlexWF);
		}
		st = con.prepareStatement(JDBCUtil.replaceCastMysql(sb.toString()));
		ResultSet rs = st.executeQuery();
		if (rs.next()) {
			// System.out.println(rs.getString("expire"));
			if (rs.getString("expire") != null) {
				try {
					Date date = ToolsHTML.sdfShowConvert1.parse(rs.getString(1));
					idWorkFlow = isRow ? rs.getLong("idWorkFlow") : idWorkFlow;

					st = con.prepareStatement(JDBCUtil.replaceCastMysql("update flexworkflow set dateExpire=? where idworkflow=?"));
					st.setTimestamp(1, new Timestamp(date.getTime()));
					st.setLong(2, idWorkFlow);
					st.execute();

					return ToolsHTML.date.format(date);
				} catch (java.text.ParseException ex) {
					// no hacemos nada, ya que la fecha no se puede parsear, no
					// es valida
				}
			}
		}
		return null;
	}

	/**
	 *
	 * @param idWorkFlow
	 * @param row
	 * @param beanNotified
	 * @throws ApplicationExceptionChecked
	 */
	public static void getPendingUsersInWF(int idWorkFlow, int row, BeanNotifiedWF beanNotified) throws ApplicationExceptionChecked {
		try {
			StringBuilder sql = new StringBuilder(1024).append("SELECT uw.row,uw.idUser,uw.isOwner,p.email,w.comments,w.owner,w.notified, ");

			switch (Constants.MANEJADOR_ACTUAL) {
			case Constants.MANEJADOR_MSSQL:
				sql.append(" (p.apellidos + ' ' + p.nombres) AS nombre,");
				break;
			case Constants.MANEJADOR_POSTGRES:
				sql.append(" (p.apellidos || ' ' || p.nombres) AS nombre,");
				break;
			case Constants.MANEJADOR_MYSQL:
				sql.append(" CONCAT(p.apellidos , ' ' , p.nombres) AS nombre,");
				break;
			}

			sql.append("d.nameDocument FROM user_workflows uw,workflows w,person p")
					.append(" ,documents d WHERE uw.idUser = p.nameUser AND w.idWorkFlow = uw.idWorkFlow AND uw.idWorkFlow = ").append(idWorkFlow)
					//.append(" AND w.idDocument = d.numGen AND `row`<>  ").append(row).append(" AND uw.result = '").append(pending).append("'")
					.append(" AND w.idDocument = d.numGen AND row<>  ").append(row).append(" AND uw.result = '").append(pending).append("'")
					//.append(" AND p.accountActive = '").append(Constants.permission).append("'").append(" ORDER BY `Row`");
					.append(" AND p.accountActive = '").append(Constants.permission).append("'").append(" ORDER BY Row");

			log.info("[getPendingUsersInWF] = " + sql);
			Vector<Properties> datos = JDBCUtil.doQueryVector(sql.toString(),Thread.currentThread().getStackTrace()[1].getMethodName());
			int index = 0;
			for (Properties properties : datos) {
				if (index == 0) {
					beanNotified.setEmail(properties.getProperty("email"));
					beanNotified.setRow(properties.getProperty("row"));
					beanNotified.setIdUser(properties.getProperty("idUser"));
					beanNotified.setComments(properties.getProperty("comments"));
					beanNotified.setOwner(properties.getProperty("owner"));
					beanNotified.setOwnerWF("1".equalsIgnoreCase(properties.getProperty("isOwner")));
					beanNotified.setNotified(properties.getProperty("notified").trim());
					beanNotified.setNameUser(properties.getProperty("nombre"));
					beanNotified.setNameDocument(properties.getProperty("nameDocument"));
					index++;
				} else {
					beanNotified.setEmail(beanNotified.getEmail() + "; " + properties.getProperty("email"));
				}
			}

			log.info("[getPendingUsersInWF] = " + beanNotified.getEmail());
		} catch (Exception ex) {
			ex.printStackTrace();
			throw new ApplicationExceptionChecked("E0015");
		}
	}

	/**
	 *
	 * @param idWorkFlow
	 * @param beanNotified
	 * @throws ApplicationExceptionChecked
	 */
	public static void getPendingUsersInFlexWF(int idWorkFlow, BeanNotifiedWF beanNotified) throws ApplicationExceptionChecked {
		try {
			StringBuilder sql = new StringBuilder(1024).append("SELECT p.email").append(" FROM user_flexworkflows uwf, person p")
					.append(" WHERE p.nameuser = uwf.iduser").append(" AND uwf.idworkflow = ").append(idWorkFlow);

			switch (Constants.MANEJADOR_ACTUAL) {
			case Constants.MANEJADOR_MSSQL:
				sql.append(" AND isowner = 0");
				break;
			case Constants.MANEJADOR_POSTGRES:
				sql.append(" AND isowner = CAST(0 AS bit)");
				break;
			case Constants.MANEJADOR_MYSQL:
				sql.append(" AND isowner = 0");
				break;
			}

			sql.append(" GROUP BY p.email");

			log.info("[getPendingUsersInFlexWF] = " + sql);
			Vector<Properties> datos = JDBCUtil.doQueryVector(sql.toString(),Thread.currentThread().getStackTrace()[1].getMethodName());
			int index = 0;
			for (Properties properties : datos) {
				if (index == 0) {
					beanNotified.setEmail(properties.getProperty("email"));
					index++;
				} else {
					beanNotified.setEmail(beanNotified.getEmail() + "; " + properties.getProperty("email"));
				}
			}

			log.info("[getPendingUsersInWF] = " + beanNotified.getEmail());
		} catch (Exception ex) {
			ex.printStackTrace();
			throw new ApplicationExceptionChecked("E0015");
		}
	}

	/**
	 *
	 * @param idWorkFlow
	 */
	public static void deactivatePrintFlow(String idWorkFlow) {
		StringBuilder query = new StringBuilder("");
		try {
			query.append("UPDATE tbl_solicitudimpresion").append(" SET active = 0")
					.append(" WHERE numsolicitud = (SELECT iddocument FROM workflows WHERE idworkflow = ").append(idWorkFlow).append(" AND toimpresion = 1)");
			JDBCUtil.executeUpdate(query);
			log.error("Ejecutado query: " + query);
		} catch (Exception e) {
			// TODO: handle exception
			log.error(e.getMessage() + ". Query: " + query, e);
		}
	}

	/**
	 * Metodo para enviar la notificacion de flujo a las actividades de los flujos parametricos Despues de la primera actividad.
	 *
	 * @param newActToFlexFlow
	 * @param row
	 * @param usuarioActual
	 */
	private static void sendFTPNewFlowNotification(long newActToFlexFlow, ParticipationForm form) {
		try {
			int row = form.getRow();
			String usuarioActual = form.getIdParticipation();

			//log.info("newActToFlexFlow='" + newActToFlexFlow + "', `row`='" + row + "', usuarioActual='" + usuarioActual + "'");
			log.info("newActToFlexFlow='" + newActToFlexFlow + "', row='" + row + "', usuarioActual='" + usuarioActual + "'");

			ArrayList<String> emailsUsers = new ArrayList<String>();
			String mensaje = "";
			String subject = "";

			StringBuilder sb = new StringBuilder(2048).append("SELECT p.email, fwf.comments, d.prefix, d.number, fwf.secuential")
					.append(" FROM person p, flexworkflow fwf, user_flexworkflows ufw, documents d").append(" WHERE p.nameuser = ufw.iduser")
					.append(" AND fwf.idworkflow = ufw.idworkflow").append(" AND d.numgen = fwf.iddocument").append(" AND ufw.idworkflow = ")
					.append(newActToFlexFlow).append(" AND ufw.statu = '").append(Constants.permissionSt).append("'").append(" ORDER BY ufw.orden");

			// revisamos los involucrados en la alerta de nuevo flujo
			CachedRowSet rows = JDBCUtil.executeQuery(sb, Thread.currentThread().getStackTrace()[1].getMethodName());
			while (rows.next()) {
				if (Constants.MANEJADOR_ACTUAL == Constants.MANEJADOR_MSSQL) {
					// campos de tipo text en SQLServer son del tipo CLob a
					// nivel de JDBC
					mensaje = new String(rows.getClob(2).getSubString(0, (int) rows.getClob(2).length()));
				} else {
					mensaje = rows.getString(2);
				}

				subject = " " + (rows.getString(3) == null ? "" : rows.getString(3)) + " " + (rows.getString(4) == null ? "" : rows.getString(4));
				emailsUsers.add(rows.getString(1));

				form.setSecuential(rows.getString(5));
				if (wfSecuential.equals(rows.getString(5))) {
					// es secuencial, notificamos al primero y cambiamos el
					// valor para que no sea enviada
					// la notificaci�n posterior
					form.setSecuential("1");
					log.info("La actividad bajo el flujo '" + newActToFlexFlow + "' es secuencial, solo notificamos al primer involucrado.");
					break;
				}
			}

			// si tenemos el mail list, enviamos el correo
			subject = ToolsHTML.getBundle(null).getString("wf.newWFTitle") + subject;
			Users user = HandlerDBUser.getUser(usuarioActual);
			SendMail.send(emailsUsers, user, subject, mensaje);
		} catch (Exception e) {
			// TODO: handle exception
			log.error("Error: " + e.getLocalizedMessage());
		}
	}

	// ydavila Ticket 001-00-003023 -
	public static Collection getRespCambElimDocEOC(String idDocument, int typeWF, String subtypeWF) {
		StringBuilder sql = new StringBuilder(100);
		Properties properties = null;
		sql.append("SELECT p.idPerson,p.nameUser, ");
		switch (Constants.MANEJADOR_ACTUAL) {
		case Constants.MANEJADOR_MSSQL:
			sql.append("(p.apellidos + ' ' + p.nombres) AS nombre, ");
			break;
		case Constants.MANEJADOR_POSTGRES:
			sql.append("(p.apellidos || ' ' || p.nombres) AS nombre, ");
			break;
		case Constants.MANEJADOR_MYSQL:
			sql.append("CONCAT(p.apellidos , ' ' , p.nombres) AS nombre, ");
			break;
		}
		sql.append(" c.cargo ");
		sql.append(" FROM person p,  documents d, tbl_cargo c ");
		sql.append(" WHERE cast(p.cargo as int) = c.idcargo AND d.numgen = ").append(idDocument);
		sql.append(" and p.nameuser=d.owner and p.accountactive = '1'");
		Vector result = new Vector();
		Search bean = null;
		try {
			Vector datos = JDBCUtil.doQueryVector(sql.toString(),Thread.currentThread().getStackTrace()[1].getMethodName());
			for (int row = 0; row < datos.size(); row++) {
				properties = (Properties) datos.elementAt(row);
				bean = new Search(properties.getProperty("nombre"), properties.getProperty("nombre"));
				bean.setAditionalInfo(properties.getProperty("cargo"));
				bean.setId(properties.getProperty("nameUser"));
				result.add(bean);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return result;
	}

	// ydavila Ticket 001-00-003023 -
	public static Collection getRespCambElimDocADM(Collection user) {
		StringBuilder sql = new StringBuilder(100);
		Properties properties = null;
		sql.append("SELECT p.idPerson,p.nameUser,p.cargo ");
		switch (Constants.MANEJADOR_ACTUAL) {
		case Constants.MANEJADOR_MSSQL:
			sql.append("(p.apellidos + ' ' + p.nombres) AS nombre ");
			break;
		case Constants.MANEJADOR_POSTGRES:
			sql.append("(p.apellidos || ' ' || p.nombres) AS nombre ");
			break;
		}
		sql.append(" FROM person p,  documents d ");

		sql.append(" and p.nameuser=d.owner and p.accountactive = '1'");
		Vector result = new Vector();
		Search bean = null;
		try {
			Vector datos = JDBCUtil.doQueryVector(sql.toString(),Thread.currentThread().getStackTrace()[1].getMethodName());
			for (int row = 0; row < datos.size(); row++) {
				properties = (Properties) datos.elementAt(row);
				bean = new Search(properties.getProperty("nameuser"), properties.getProperty("nombre"));
				bean.setAditionalInfo(properties.getProperty("cargo"));
				bean.setId(properties.getProperty("nameUser"));
				result.add(bean);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return result;
	}

}
