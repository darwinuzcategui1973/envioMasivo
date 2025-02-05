package com.desige.webDocuments.persistent.managers;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Properties;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.dbutils.DbUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

import com.desige.webDocuments.activities.forms.Activities;
import com.desige.webDocuments.persistent.utils.JDBCUtil;
import com.desige.webDocuments.seguridad.forms.PermissionUserForm;
import com.desige.webDocuments.utils.ApplicationExceptionChecked;
import com.desige.webDocuments.utils.Constants;
import com.desige.webDocuments.utils.ToolsHTML;
import com.focus.qweb.dao.ActivityDAO;
import com.focus.qweb.to.ActivityTO;

import sun.jdbc.rowset.CachedRowSet;

/**
 * Title: HandlerBD.java <br/>
 * Copyright: (c) 2004 Focus Consulting C.A.<br/>
 * 
 * @author Ing. Nelson Crespo (NC)
 * @author Ing. Simon Rodriguez (SR)
 * @version WebDocuments v3.0 <br/>
 *          Changes:<br/>
 *          <ul>
 *          <li>09/04/2004 (NC) Creation</li>
 *          <li>18/05/2005 setSecurityInNode se le coloco la carga a toEditRegister(SR)</li>
 *          <li>02/06/2005 setSecurityInNode se le coloco la carga a setToImpresion(SR)</li>
 *          <li>10/06/2005 se setEmptySecurity, setSecurityInNode se les coloco toCheckTodos (SR)</li>
 *          <li>22/11/2005 (NC) method getFields Added</li>
 *          </ul>
 */
public class HandlerBD implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 2391787547640297902L;
	static Logger log = LoggerFactory.getLogger("[V4.0 FTP] " + HandlerBD.class.getName());
	public static int typeString = 1;
	public static int typeInt = 2;
	public static int typeBoolean = 3;
	public static int typeDate = 4;

	protected static String mensaje = null;

	/**
	 * M�todo que permite cerrar la conexi�n a la Base de Datos una vez utilizada la misma
	 * 
	 * @param conn
	 */
	protected static void closeConection(Connection conn) {
		if (conn != null) {
			try {
				conn.close();
			} catch (Exception e) {
				setMensaje(e.getMessage());
				e.printStackTrace();
			}
		}
	}

	/**
	 * 
	 * @param mensaje
	 * @return
	 */
	public static String formatMessage(String mensaje) {
		StringBuilder result = new StringBuilder(1024);
		int pos = mensaje.indexOf("\n");
		while (pos > 0) {
			result.append(mensaje.substring(0, pos));
			if (mensaje.charAt(pos - 1) != ' ') {
				result.append(" ");
			}
			log.debug("pos = " + pos);
			mensaje = mensaje.substring(pos + 1);
			log.debug("mensaje = " + mensaje);
			pos = mensaje.indexOf("\n");
		}
		result.append(mensaje);
		return result.toString();
	}

	/**
	 * 
	 * @param conn
	 */
	protected static void applyRollback(Connection conn) {
		if (conn != null) {
			try {
				conn.rollback();
			} catch (Exception e) {
				setMensaje(formatMessage(e.getMessage()));
				e.printStackTrace();
			}
		}
	}

	/**
	 * 
	 * @param conn
	 */
	protected static void setFinally(Connection conn) {
		if (conn != null) {
			try {
				conn.setAutoCommit(true);
			} catch (Exception e) {
				setMensaje(formatMessage(e.getMessage()));
				e.printStackTrace();
			} finally {
				DbUtils.closeQuietly(conn);
			}
			// try {
			// closeConection(conn);
			// } catch (Exception e) {
			// setMensaje(formatMessage(e.getMessage()));
			// e.printStackTrace();
			// }
		}
	}

	/**
	 * Este m�todo permite cerrar la setencia y la conexi�n a la base de datos en caso de que ambas se encuentren abiertas
	 * 
	 * @param conn
	 * @param stm
	 */
	protected static void setFinally(Connection conn, PreparedStatement stm) {
		DbUtils.closeQuietly(stm);
		// if (stm != null) {
		// try {
		// stm.close();
		// log.debug("Statement Close");
		// } catch (Exception e) {
		// setMensaje(formatMessage(e.getMessage()));
		// log.debug("Error cerrando Statement");
		// }
		// }

		if (conn != null) {
			try {
				conn.setAutoCommit(true);
				log.debug("connection setAutoCommit");
			} catch (Exception e) {
				setMensaje(formatMessage(e.getMessage()));
				log.debug("Error in setAutoCommit connection");
			} finally {
				DbUtils.closeQuietly(conn);
			}
			// try {
			// closeConection(conn);
			// log.debug("Connection cerrado");
			// } catch (Exception e) {
			// setMensaje(formatMessage(e.getMessage()));
			// log.debug("Error cerrando Connection...");
			// }
		}
	}

	/**
	 * 
	 * @param conn
	 * @param stm
	 * @param rs
	 */
	protected static void setFinally(Connection conn, PreparedStatement stm, ResultSet rs) {

		JDBCUtil.closeQuietly(stm, rs);
		// if (rs != null) {
		// try {
		// rs.close();
		// } catch (Exception e) {
		// setMensaje(formatMessage(e.getMessage()));
		// log.debug("Error cerrando ResultSet");
		// }
		// }
		//
		// if (stm != null) {
		// try {
		// stm.close();
		// log.debug("Statement Cerrada");
		// } catch (Exception e) {
		// setMensaje(formatMessage(e.getMessage()));
		// log.debug("Error cerrando Statement");
		// }
		// }

		if (conn != null) {
			try {
				conn.setAutoCommit(true);
				log.debug("connection re-establecido");
			} catch (Exception e) {
				setMensaje(formatMessage(e.getMessage()));
				log.debug("Error re=estableciendo connection");
			} finally {
				DbUtils.closeQuietly(conn);
			}
			// try {
			// closeConection(conn);
			// log.debug("Connection cerrado");
			// } catch (Exception e) {
			// setMensaje(formatMessage(e.getMessage()));
			// log.debug("Error cerrando Connection...");
			// }
		}
	}

	/**
	 * 
	 * @return
	 */
	public static String getMensaje() {
		return mensaje;
	}

	/**
	 * 
	 * @param mensaje
	 */
	public static void setMensaje(String mensaje) {
		StringBuilder sms = new StringBuilder(mensaje);
		int pos = 0;
		while ((pos = sms.indexOf("'")) > 0) {
			sms.replace(pos, pos + 1, "");
		}
		HandlerBD.mensaje = sms.toString();
	}

	/**
	 * 
	 * @param type
	 * @param value
	 * @return
	 */
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
			resp.append("CONVERT(datetime,'").append(value).append("',120)");
			break;
		}
		}
		return resp.toString();
	}

	// getField("type_Formato","typedocuments","idTypeDoc",doc.getTypeDocument(),"=",2);
	/**
	 * 
	 * Obtiene un valor de la base de datos.
	 * 
	 * @param field
	 * @param table
	 * @param id
	 * @param valueId
	 * @param condition
	 * @param type
	 * @return
	 * @throws Exception
	 */
	public static String getField(String field, String table, String id, String valueId, String condition, int type, String nameCurrentMethod) throws Exception {
		return getField(null, field, table, id, valueId, condition, type, true, nameCurrentMethod);
	}

	/**
	 * 
	 * @param field
	 * @param table
	 * @param id
	 * @param valueId
	 * @param condition
	 * @param type
	 * @param checkOnlyActives
	 * @return
	 * @throws Exception
	 */
	public static String getField(String field, String table, String id, String valueId, String condition, int type, boolean checkOnlyActives, String nameCurrentMethod)
			throws Exception {
		return getField(null, field, table, id, valueId, condition, type, checkOnlyActives, nameCurrentMethod);
	}

	/**
	 * 
	 * @param con
	 * @param field
	 * @param table
	 * @param id
	 * @param valueId
	 * @param condition
	 * @param type
	 * @return
	 * @throws Exception
	 */
	public static String getField(Connection con, String field, String table, String id, String valueId, String condition, int type, String nameCurrentMethod) throws Exception {
		return getField(con, field, table, id, valueId, condition, type, true, nameCurrentMethod);
	}

	/**
	 * 
	 * @param con
	 * @param field
	 * @param table
	 * @param id
	 * @param valueId
	 * @param condition
	 * @param type
	 * @param checkOnlyActives
	 * @return
	 * @throws Exception
	 */
	public static String getField(Connection con, String field, String table, String id, String valueId, String condition, int type, boolean checkOnlyActives, String nameCurrentMethod)
			throws Exception {
		StringBuilder sql = new StringBuilder("SELECT ").append(field).append(" FROM ").append(table);
		if (!ToolsHTML.isEmptyOrNull(id)) {
			sql.append(" WHERE ").append(id).append(" ").append(condition).append(" ").append(getValue(type, valueId));
			if ("person".equalsIgnoreCase(table) && checkOnlyActives) {
				sql.append(" and accountActive='").append(Constants.permission).append("'");

			}
		} else {
			if ("person".equalsIgnoreCase(table) && checkOnlyActives) {
				sql.append(" WHERE ").append("  accountActive='").append(Constants.permission).append("'");
			}
		}

		Vector datos = JDBCUtil.doQueryVector(con, sql.toString(), nameCurrentMethod);
		if ((datos != null) && (datos.size() > 0)) {
			Properties prop = (Properties) datos.get(0);
			return prop.getProperty(field).trim();
		}

		return "";
	}

	// SIMON 22 DE JUNIO 2005 INICIO
	/**
	 * 
	 * @param field
	 * @param Query
	 * @return
	 * @throws Exception
	 */
	public static String getField2(String field, String Query) throws Exception {
		StringBuilder sql = new StringBuilder("SELECT distinct ").append(field).append(" ").append(Query);

		log.debug("[getField] = " + sql.toString());
		Vector datos = JDBCUtil.doQueryVector(sql.toString(),Thread.currentThread().getStackTrace()[1].getMethodName());
		if ((datos != null) && (datos.size() > 0)) {
			Properties prop = (Properties) datos.get(0);
			return prop.getProperty(field).trim();
		}
		return "";
	}

	/**
	 * 
	 * @param field
	 * @param Query
	 * @return
	 * @throws Exception
	 */
	public static String getField(String field, String Query, String nameCurrentMethod) throws Exception {
		StringBuilder sql = new StringBuilder("SELECT ").append(field).append(" ").append(Query);
		Vector datos = JDBCUtil.doQueryVector(sql.toString(), nameCurrentMethod);
		if ((datos != null) && (datos.size() > 0)) {
			Properties prop = (Properties) datos.get(0);
			return prop.getProperty(field).trim();
		}
		return "";
	}

	/**
	 * 
	 * @param field
	 * @return
	 */
	public static String getParameter(String field, String nameCurrentMethod) {
		String retorno = null;
		try {
			retorno = getField(field, "parameters", null, null, null, 1,nameCurrentMethod);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return (retorno == null ? "" : retorno);
	}

	/**
	 * 
	 * @param con
	 * @param field
	 * @return
	 */
	public static String getParameter(Connection con, String field, String nameCurrentMethod) {
		String retorno = null;
		try {
			retorno = getField(con, field, "parameters", null, null, null, 1, true,nameCurrentMethod);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return (retorno == null ? "" : retorno);
	}

	/**
	 * 
	 * @param idPerson
	 * @return
	 */
	public static String getOptionSearch(int idPerson) {
		String retorno = null;
		try {
			retorno = getField(Constants.COLUMN_SEARCH_NAME, "FROM person WHERE idPerson=" + idPerson,Thread.currentThread().getStackTrace()[1].getMethodName());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ToolsHTML.isEmptyOrNull(retorno, Constants.COLUMN_SEARCH_DEFAULT);
	}

	/**
	 * 
	 * @param idPerson
	 * @return
	 */
	public static String getOptionPublished(int idPerson) {
		String retorno = null;
		try {
			retorno = getField(Constants.COLUMN_PUBLISHED_NAME, "FROM person WHERE idPerson=" + idPerson,Thread.currentThread().getStackTrace()[1].getMethodName());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ToolsHTML.isEmptyOrNull(retorno, Constants.COLUMN_PUBLISHED_DEFAULT);
	}

	/**
	 * 
	 * @param idPerson
	 * @return
	 */
	public static String getOptionFiles(int idPerson) {
		String retorno = null;
		try {
			retorno = getField(Constants.COLUMN_FILES_NAME, "FROM person WHERE idPerson=" + idPerson,Thread.currentThread().getStackTrace()[1].getMethodName());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ToolsHTML.isEmptyOrNull(retorno, Constants.COLUMN_FILES_DEFAULT);
	}

	/**
	 * 
	 * @param idPerson
	 * @return
	 */
	public static String getOptionMenuHead(int idPerson) {
		String retorno = null;
		try {
			retorno = getField(Constants.COLUMN_MENUHEAD_NAME, "FROM person WHERE idPerson=" + idPerson,Thread.currentThread().getStackTrace()[1].getMethodName());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ToolsHTML.isEmptyOrNull(retorno, Constants.COLUMN_MENUHEAD_DEFAULT);
	}
	
	/**
	 * @author DARWIN UZCATEGUI 
	 * @param idPerson
	 * @return
	 */
	public static String getOptionSacop(int idPerson) {
		String retorno = null;
		try {
			retorno = getField(Constants.COLUMN_SACOP_NAME, "FROM person WHERE idPerson=" + idPerson,Thread.currentThread().getStackTrace()[1].getMethodName());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ToolsHTML.isEmptyOrNull(retorno, Constants.COLUMN_SACOP_DEFAULT);
	}
	
	public static String findOrigenSacop(String idOrigen) {
		String retorno = null;
		idOrigen = idOrigen.substring(0,3);
		try {
			retorno = getField(Constants.COLUMN_TIPO_SACOP_NAME, "FROM tbl_tipo WHERE prefijo = '" +idOrigen+"'",Thread.currentThread().getStackTrace()[1].getMethodName());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ToolsHTML.isEmptyOrNull(retorno, "no definida");
	}
	
	public static String pruebaOtraBaseDatos(String idCOD) {
		String retorno = null;
		idCOD = idCOD.substring(0,8);
		//APP_DB_PROFIT.dbo.EMPRESA
		try {
			retorno = getField(Constants.COLUMN_PRUEBA,"FROM CONDT.dbo.CTAARTICULOS WHERE CODIGO = '" +idCOD+"'",Thread.currentThread().getStackTrace()[1].getMethodName());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ToolsHTML.isEmptyOrNull(retorno, "no definida");
	}

	/**
	 * 
	 * @param prop
	 * @param forma
	 * @param isStruct
	 */
	protected static void setSecurityInDocs(Properties prop, PermissionUserForm forma, boolean isStruct) {
		if (!isStruct) {
			forma.setToRead(Byte.parseByte(prop.getProperty("toRead")));
		}

		forma.setToCheckTodos(Byte.parseByte(prop.getProperty("toCheckTodos")));
		forma.setToViewDocs(Byte.parseByte(prop.getProperty("toViewDocs")));
		forma.setToEdit(Byte.parseByte(prop.getProperty(isStruct ? "toEditDocs" : "toEdit")));
		forma.setToAdmon(Byte.parseByte(prop.getProperty(isStruct ? "toAdmonDocs" : "toAdmon")));
		forma.setToDelete(Byte.parseByte(prop.getProperty(isStruct ? "toDelDocs" : "toDelete")));
		forma.setToReview(Byte.parseByte(prop.getProperty("toReview")));
		forma.setToAprove(Byte.parseByte(prop.getProperty(isStruct ? "toApproved" : "toAprove")));
		forma.setToMoveDocs(Byte.parseByte(prop.getProperty("toMoveDocs")));
		forma.setCheckOut(Byte.parseByte(prop.getProperty("toCheckOut")));
		forma.setToEditRegister(Byte.parseByte(prop.getProperty("toEditRegister")));
		forma.setToImpresion(Byte.parseByte(prop.getProperty("toImpresion")));
		String toFlexFlow = prop.getProperty("toFlexFlow");
		if (!ToolsHTML.isEmptyOrNull(toFlexFlow)) {
			forma.setToFlexFlow(Byte.parseByte(toFlexFlow));
			forma.setToChangeUsr(Byte.parseByte(prop.getProperty("toChangeUsr")));
			if (!ToolsHTML.isEmptyOrNull(prop.getProperty("toCompleteFlow"))) {
				forma.setToCompleteFlow(Byte.parseByte(prop.getProperty("toCompleteFlow")));
			}
		}
		if (!ToolsHTML.isEmptyOrNull(prop.getProperty("toPublicEraser"))) {
			forma.setToPublicEraser(Byte.parseByte(prop.getProperty("toPublicEraser")));
		}
		if (!ToolsHTML.isEmptyOrNull(prop.getProperty("toDownload"))) {
			forma.setToDownload(Byte.parseByte(prop.getProperty("toDownload")));
		}

		if (isStruct) {
			forma.setToMove(Byte.parseByte(prop.getProperty("toMove")));
			forma.setToDoFlows(Byte.parseByte(prop.getProperty("toDoFlows")));
		}
	}

	/**
	 * 
	 * @param prop
	 * @param forma
	 */
	protected static void setSecurityInNode(Properties prop, PermissionUserForm forma) {
		String toView = prop.getProperty("toView");
		if (!ToolsHTML.isEmptyOrNull(toView)) {
			forma.setToView(Byte.parseByte(toView));
		}

		forma.setToRead(Byte.parseByte(prop.getProperty("toRead")));
		String toAddFolder = prop.getProperty("toAddFolder");
		if (!ToolsHTML.isEmptyOrNull(toAddFolder)) {
			forma.setToAddFolder(Byte.parseByte(toAddFolder));
		}
		String toAddProcess = prop.getProperty("toAddProcess");
		if (!ToolsHTML.isEmptyOrNull(toAddProcess)) {
			forma.setToAddProcess(Byte.parseByte(toAddProcess));
		}

		forma.setToDelete(Byte.parseByte(prop.getProperty("toDelete")));
		forma.setToEdit(Byte.parseByte(prop.getProperty("toEdit")));
		forma.setToMove(Byte.parseByte(prop.getProperty("toMove")));
		forma.setToAdmon(Byte.parseByte(prop.getProperty("toAdmon")));
		String toAddDocument = prop.getProperty("toAddDocument");
		if (!ToolsHTML.isEmptyOrNull(toAddDocument)) {
			forma.setToAddDocument(Byte.parseByte(toAddDocument));
		}
		// forma.setToAddDocument(Byte.parseByte(prop.getProperty("toAddDocument")));
		// Opciones Documentos
		String toViewDocs = prop.getProperty("toViewDocs");
		if (!ToolsHTML.isEmptyOrNull(toViewDocs)) {
			forma.setToViewDocs(Byte.parseByte(toViewDocs));
		}
		String toEditDocs = prop.getProperty("toEditDocs");
		if (!ToolsHTML.isEmptyOrNull(toEditDocs)) {
			forma.setToEditDocs(Byte.parseByte(toEditDocs));
		}
		// forma.setToEditDocs(Byte.parseByte(prop.getProperty("toEditDocs")));
		String toAdmonDocs = prop.getProperty("toAdmonDocs");
		if (!ToolsHTML.isEmptyOrNull(toAdmonDocs)) {
			forma.setToAdmonDocs(Byte.parseByte(toAdmonDocs));
		}
		// forma.setToAdmonDocs(Byte.parseByte(prop.getProperty("toAdmonDocs")));
		String toDelDocs = prop.getProperty("toDelDocs");
		if (!ToolsHTML.isEmptyOrNull(toDelDocs)) {
			forma.setToDelDocs(Byte.parseByte(toDelDocs));
		}
		// forma.setToDelDocs(Byte.parseByte(prop.getProperty("toDelDocs")));
		forma.setToReview(Byte.parseByte(prop.getProperty("toReview")));
		String toApproved = prop.getProperty("toApproved");
		if (!ToolsHTML.isEmptyOrNull(toApproved)) {
			forma.setToAprove(Byte.parseByte(toApproved));
		} else {
			toApproved = prop.getProperty("toAprove");
			if (!ToolsHTML.isEmptyOrNull(toApproved)) {
				forma.setToAprove(Byte.parseByte(toApproved));
			}
		}
		// forma.setToAprove(Byte.parseByte(prop.getProperty("toApproved")));
		String toMoveDocs = prop.getProperty("toMoveDocs");
		if (!ToolsHTML.isEmptyOrNull(toMoveDocs)) {
			forma.setToMoveDocs(Byte.parseByte(toMoveDocs));
		}
		// forma.setToMoveDocs(Byte.parseByte(prop.getProperty("toMoveDocs")));
		forma.setCheckOut(Byte.parseByte(prop.getProperty("toCheckOut")));
		forma.setToEditRegister(Byte.parseByte(prop.getProperty("toEditRegister")));
		forma.setToImpresion(Byte.parseByte(prop.getProperty("toImpresion")));
		forma.setToCheckTodos(Byte.parseByte(prop.getProperty("toCheckTodos")));
		String toDoFlows = prop.getProperty("toDoFlows");
		if (!ToolsHTML.isEmptyOrNull(toDoFlows)) {
			forma.setToDoFlows(Byte.parseByte(toDoFlows));
		}
		String docInline = prop.getProperty("docInline");
		if (!ToolsHTML.isEmptyOrNull(docInline)) {
			forma.setToDocinLine(Byte.parseByte(docInline));
		}
		// Para Crear Flujos de Trabajo Parametrico
		docInline = prop.getProperty("toFlexFlow");
		if (!ToolsHTML.isEmptyOrNull(docInline)) {
			forma.setToFlexFlow(Byte.parseByte(docInline));
		}
		// Para Cambiar los Usuarios sugeridos en los Flujos de Trabajo
		// Param�tricos
		docInline = prop.getProperty("toChangeUsr");
		if (!ToolsHTML.isEmptyOrNull(docInline)) {
			forma.setToChangeUsr(Byte.parseByte(docInline));
		}
		// Para Completar flujos de Trabajo Parametrico sin terminar las
		// actividades
		docInline = prop.getProperty("toCompleteFlow");
		if (!ToolsHTML.isEmptyOrNull(docInline)) {
			forma.setToCompleteFlow(Byte.parseByte(docInline));
		}
		// Para Publicar archivos borradores
		docInline = prop.getProperty("toPublicEraser");
		if (!ToolsHTML.isEmptyOrNull(docInline)) {
			forma.setToPublicEraser(Byte.parseByte(docInline));
		}
		// Para Descargar ultima version
		docInline = prop.getProperty("toDownload");
		if (!ToolsHTML.isEmptyOrNull(docInline)) {
			forma.setToDownload(Byte.parseByte(docInline));
		}
	}

	/**
	 * 
	 * @param prop
	 * @param forma
	 */
	protected static void setSecurityInRecord(Properties prop, PermissionUserForm forma) {
		if (!ToolsHTML.isEmptyOrNull(prop.getProperty("toGenerate"))) {
			forma.setToGenerate(Byte.parseByte(prop.getProperty("toGenerate")));
		}
		if (!ToolsHTML.isEmptyOrNull(prop.getProperty("toUpdate"))) {
			forma.setToUpdate(Byte.parseByte(prop.getProperty("toUpdate")));
		}
		if (!ToolsHTML.isEmptyOrNull(prop.getProperty("toSend"))) {
			forma.setToSend(Byte.parseByte(prop.getProperty("toSend")));
		}
		if (!ToolsHTML.isEmptyOrNull(prop.getProperty("toExport"))) {
			forma.setToExport(Byte.parseByte(prop.getProperty("toExport")));
		}
		if (!ToolsHTML.isEmptyOrNull(prop.getProperty("toPrint"))) {
			forma.setToPrint(Byte.parseByte(prop.getProperty("toPrint")));
		}
	}

	/**
	 * 
	 * @param prop
	 * @param forma
	 */
	protected static void setSecurityInFiles(Properties prop, PermissionUserForm forma) {
		forma.setToFilesSecurity(Byte.parseByte(ToolsHTML.isEmptyOrNull(prop.getProperty("toFilesSecurity"), String.valueOf(Constants.notPermission))));
		forma.setToFilesCreate(Byte.parseByte(ToolsHTML.isEmptyOrNull(prop.getProperty("toFilesCreate"), String.valueOf(Constants.notPermission))));
		forma.setToFilesExport(Byte.parseByte(ToolsHTML.isEmptyOrNull(prop.getProperty("toFilesExport"), String.valueOf(Constants.notPermission))));
		forma.setToFilesEdit(Byte.parseByte(ToolsHTML.isEmptyOrNull(prop.getProperty("toFilesEdit"), String.valueOf(Constants.notPermission))));
		forma.setToFilesDelete(Byte.parseByte(ToolsHTML.isEmptyOrNull(prop.getProperty("toFilesDelete"), String.valueOf(Constants.notPermission))));
		forma.setToFilesRelated(Byte.parseByte(ToolsHTML.isEmptyOrNull(prop.getProperty("toFilesRelated"), String.valueOf(Constants.notPermission))));
		forma.setToFilesVersion(Byte.parseByte(ToolsHTML.isEmptyOrNull(prop.getProperty("toFilesVersion"), String.valueOf(Constants.notPermission))));
		forma.setToFilesView(Byte.parseByte(ToolsHTML.isEmptyOrNull(prop.getProperty("toFilesView"), String.valueOf(Constants.notPermission))));
		forma.setToFilesPrint(Byte.parseByte(ToolsHTML.isEmptyOrNull(prop.getProperty("toFilesPrint"), String.valueOf(Constants.notPermission))));
		forma.setToFilesHistory(Byte.parseByte(ToolsHTML.isEmptyOrNull(prop.getProperty("toFilesHistory"), String.valueOf(Constants.notPermission))));
		forma.setToFilesDownload(Byte.parseByte(ToolsHTML.isEmptyOrNull(prop.getProperty("toFilesDownload"), String.valueOf(Constants.notPermission))));
		forma.setToFilesSave(Byte.parseByte(ToolsHTML.isEmptyOrNull(prop.getProperty("toFilesSave"), String.valueOf(Constants.notPermission))));
	}

	/**
	 * 
	 * @param forma
	 */
	protected static void setEmptySecurity(PermissionUserForm forma) {
		byte initValue = Constants.notPermission;
		forma.setToRead(initValue);
		forma.setToAddFolder(initValue);
		forma.setToAddProcess(initValue);
		forma.setToDelete(initValue);
		forma.setToEdit(initValue);
		forma.setToMove(initValue);
		forma.setToAdmon(initValue);
		forma.setToAddDocument(initValue);
		forma.setToDoFlows(initValue);
		forma.setToDocinLine(initValue);
		// Opciones Documentos
		forma.setToViewDocs(initValue);
		forma.setToEditDocs(initValue);
		forma.setToAdmonDocs(initValue);
		forma.setToDelDocs(initValue);
		forma.setToReview(initValue);
		forma.setToAprove(initValue);
		forma.setToMoveDocs(initValue);
		forma.setCheckOut(initValue);
		forma.setToEditRegister(initValue);
		forma.setToImpresion(initValue);
		forma.setToCheckTodos(initValue);
		forma.setToGenerate(initValue);
		forma.setToUpdate(initValue);
		forma.setToSend(initValue);
		forma.setToExport(initValue);
		forma.setToPrint(initValue);
		forma.setToDownload(initValue);

		// para seguridad de expedientes
		forma.setToFilesSecurity(initValue);
		forma.setToFilesCreate(initValue);
		forma.setToFilesExport(initValue);
		forma.setToFilesEdit(initValue);
		forma.setToFilesDelete(initValue);
		forma.setToFilesRelated(initValue);
		forma.setToFilesVersion(initValue);
		forma.setToFilesView(initValue);
		forma.setToFilesPrint(initValue);
		forma.setToFilesHistory(initValue);
		forma.setToFilesDownload(initValue);
		forma.setToFilesSave(initValue);

	}

	/**
	 * 
	 * @param fields
	 * @param tableName
	 * @param nameID
	 * @param valueID
	 * @return
	 * @throws Exception
	 */
	public static String[] getFields(String[] fields, String tableName, String nameID, String valueID) throws Exception {
		return getFields(fields, tableName, nameID, valueID, true);
	}

	/**
	 * 
	 * @param fields
	 * @param tableName
	 * @param nameID
	 * @param valueID
	 * @param onlyCheckActives
	 * @return
	 * @throws Exception
	 */
	public static String[] getFields(String[] fields, String tableName, String nameID, String valueID, boolean onlyCheckActives) throws Exception {
		if (fields != null && fields.length >= 0) {
			boolean first = true;
			StringBuilder sql = new StringBuilder("SELECT ");
			String[] result = new String[fields.length];
			for (int row = 0; row < fields.length; row++) {
				if (first) {
					first = false;
				} else {
					sql.append(",");
				}
				sql.append(fields[row]);
			}
			sql.append(" FROM ").append(tableName);
			if (valueID != null) {
				sql.append(" WHERE ").append(nameID).append("=").append(valueID);
				if ("person".equalsIgnoreCase(tableName) && onlyCheckActives) {
					sql.append(" and accountActive='").append(Constants.permission).append("'");
				}
			} else {
				if ("person".equalsIgnoreCase(tableName) && onlyCheckActives) {
					sql.append(" WHERE ").append("  accountActive='").append(Constants.permission).append("'");
				}
			}

			log.debug("String[] getFields = " + sql.toString());
			Properties prop = JDBCUtil.doQueryOneRow(sql.toString(),Thread.currentThread().getStackTrace()[1].getMethodName());
			if (prop.getProperty("isEmpty").equalsIgnoreCase("false")) {
				for (int row = 0; row < fields.length; row++) {
					result[row] = prop.getProperty(fields[row]);
				}
				return result;
			}
		}
		return null;
	}

	/**
	 * 
	 * @param field
	 * @param idDocument
	 * @return
	 */
	public static String getFieldMayorStatuVersion(String field, int idDocument) {
		String salida = "";
		PreparedStatement pst = null;
		ResultSet rs = null;
		Connection con = null;
		try {
			StringBuilder sql = new StringBuilder(1024);
			sql.append("SELECT ").append(field);
			sql.append(" FROM versiondoc WHERE numdoc = ").append(idDocument);
			sql.append(" AND active = '1' AND numVer = ");
			sql.append(JDBCUtil.executeQueryRetornaIds(new StringBuffer("SELECT MAX(numver) FROM versiondoc WHERE numdoc= ").append(idDocument)));

			log.debug("[getFieldMayorStatuVersion] = " + sql);
			con = JDBCUtil.getConnection(Thread.currentThread().getStackTrace()[1].getMethodName());
			pst = con.prepareStatement(JDBCUtil.replaceCastMysql(sql.toString()));
			rs = pst.executeQuery();
			if (rs != null) {
				if (rs.next()) {
					salida = rs.getString(field) != null ? rs.getString(field) : "";
					return salida;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			JDBCUtil.closeQuietly(con, pst, rs);
		}
		return salida;
	}

	// C�digo Usado en Hibernate
	public static void saveOrUpdate(Activities forma) throws ApplicationExceptionChecked {
		try {
			ActivityDAO oActivityDAO = new ActivityDAO();

			ActivityTO oActivityTO = new ActivityTO(forma);
			oActivityDAO.actualizar(oActivityTO);

		} catch (Exception ex) {
			ex.printStackTrace();
			throw new ApplicationExceptionChecked("E0100");
		}
	}

	/**
	 * 
	 * @param obj
	 * @throws ApplicationExceptionChecked
	 */
	public static void delete(Object obj) throws ApplicationExceptionChecked {
		try {
			// TODO: hacer las correcciones para este metodo segun la clase
			System.out.println(obj.getClass());
			int i = 0;
			// HibernateUtil.delete(obj);
		} catch (Exception ex) {
			ex.printStackTrace();
			throw new ApplicationExceptionChecked("E0100");
		}
	}

	/**
	 * 
	 * @param fields
	 * @param table
	 * @param filters
	 * @param values
	 * @param conditions
	 * @param types
	 * @return
	 * @throws Exception
	 */
	public static String[] getField(String[] fields, String table, String[] filters, String[] values, String[] conditions, Object[] types) throws Exception {
		return getField(null, fields, table, filters, values, conditions, types);
	}

	/**
	 * 
	 * @param con
	 * @param fields
	 * @param table
	 * @param filters
	 * @param values
	 * @param conditions
	 * @param types
	 * @return
	 * @throws Exception
	 */
	public static String[] getField(Connection con, String[] fields, String table, String[] filters, String[] values, String[] conditions, Object[] types)
			throws Exception {
		if (fields != null) {
			StringBuilder sql = new StringBuilder("SELECT ");
			String sep = "";

			String[] result = new String[fields.length];
			boolean isFirst = true;
			for (int cont = 0; cont < fields.length; cont++) {
				if (!isFirst) {
					sql.append(",");
				}
				sql.append(fields[cont]);
				isFirst = false;
			}
			sql.append(" FROM ").append(table);
			if (filters != null && values != null && conditions != null) {
				if (filters.length == values.length && values.length == conditions.length) {
					isFirst = true;
					for (int cont = 0; cont < filters.length; cont++) {
						if (isFirst) {
							sql.append(" WHERE ");
							isFirst = false;
						} else {
							sql.append(" AND ");
						}
						if (types[cont] instanceof Byte) {
							sql.append(filters[cont].replace("[", "").replace("]", "")).append(conditions[cont]).append("'").append(values[cont].trim())
									.append("'");
						} else if (types[cont] instanceof Number) {
							sep = "";
							try {
								int n = Integer.parseInt(values[cont]);
								sep = "'";
							} catch (NumberFormatException e) {
								// no importa
							}
							sql.append(filters[cont].replace("[", "").replace("]", "")).append(conditions[cont]).append(sep).append(values[cont]).append(sep);
						} else {
							if (types[cont] instanceof String) {
								sql.append(filters[cont].replace("[", "").replace("]", "")).append(conditions[cont]).append("'").append(values[cont])
										.append("'");
							}
						}
					}
				} else {
					throw new ApplicationExceptionChecked("E0056");
				}
				// log.debug("HandlerBD.getField Query..." + sql);
			}
			log.debug("[getField] " + sql);
			if (sql.length() > 0) {
				Vector datos = JDBCUtil.doQueryVector(con, sql.toString(),Thread.currentThread().getStackTrace()[1].getMethodName());
				if (datos.size() > 0) {
					Properties prop = (Properties) datos.get(0);
					for (int cont = 0; cont < fields.length; cont++) {
						result[cont] = prop.getProperty(fields[cont]);
					}
				} else {
					return null;
				}
			}
			return result;
		}
		return null;
	}

	/**
	 * 
	 * @param fields
	 * @param table
	 * @param filters
	 * @param values
	 * @param conditions
	 * @param comparator
	 * @param types
	 * @return
	 * @throws Exception
	 */
	public static String[] getField(String[] fields, String table, String[] filters, String[] values, String[] conditions, String[] comparator, Object[] types)
			throws Exception {
		if (fields != null) {
			StringBuilder sql = new StringBuilder("SELECT ");
			String[] result = new String[fields.length];
			boolean isFirst = true;
			for (int cont = 0; cont < fields.length; cont++) {
				if (!isFirst) {
					sql.append(",");
				}
				sql.append(fields[cont].replace('[', ' ').replace(']', ' ').trim());
				isFirst = false;
			}
			sql.append(" FROM ").append(table);
			if (filters != null && values != null && conditions != null) {
				if (filters.length == values.length && values.length == comparator.length) {
					isFirst = true;
					for (int cont = 0; cont < filters.length; cont++) {
						if (isFirst) {
							sql.append(" WHERE ");
							isFirst = false;
						} else {
							sql.append(" ").append(conditions[cont - 1]).append(" ");
						}
						if (types[cont] instanceof Number) {
							sql.append(filters[cont].replace('[', ' ').replace(']', ' ').trim()).append(comparator[cont]).append(values[cont]);
						} else {
							if (types[cont] instanceof String) {
								sql.append(filters[cont].replace('[', ' ').replace(']', ' ').trim()).append(comparator[cont]).append("'").append(values[cont])
										.append("'");
							}
						}
						if (cont > 0 && "OR".equalsIgnoreCase(conditions[cont - 1])) {
							sql.append(")");
						}
					}
				} else {
					throw new ApplicationExceptionChecked("E0056");
				}
				log.debug("HandlerBD.getField Query..." + sql);
				Vector datos = JDBCUtil.doQueryVector(sql.toString(),Thread.currentThread().getStackTrace()[1].getMethodName());
				if (datos.size() > 0) {
					Properties prop = (Properties) datos.get(0);
					for (int cont = 0; cont < fields.length; cont++) {
						result[cont] = prop.getProperty(fields[0]);
					}
				} else {
					return null;
				}
			}
			return result;
		}
		return null;
	}

	// ydavila Elmor
	public void init(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
		// TODO Auto-generated method stub

	}

	// ydavila Ticket 001-00-003023
	public static String getCargoArea(String usuario) throws Exception {
		String result = null;
		PreparedStatement pst = null;
		Connection con = null;
		String nombrecargo = null;
		con = JDBCUtil.getConnection(Thread.currentThread().getStackTrace()[1].getMethodName());
		StringBuffer sql = new StringBuffer("");
		StringBuffer sql2 = new StringBuffer("");
		StringBuffer sql3 = new StringBuffer("");
		sql.append("SELECT apellidos,nombres,cargo,idarea FROM person WHERE nameuser='").append(usuario).append("'");
		sql.append(" AND accountactive = '1'");
		log.debug("SQL HandlerDBUser.getNombreCargo (usuario)= " + sql);
		Properties prop = JDBCUtil.doQueryOneRow(sql.toString(),Thread.currentThread().getStackTrace()[1].getMethodName());
		if (!ToolsHTML.isEmptyOrNull(prop.getProperty("cargo"))) {
			nombrecargo = prop.getProperty("apellidos") + " " + prop.getProperty("nombres") + " - ";
			sql2.append("SELECT cargo FROM tbl_cargo WHERE idcargo=").append(prop.getProperty("cargo"));
			log.debug("SQL HandlerDBUser.getNombreCargo (cargo)= " + sql2);
			Properties prop2 = JDBCUtil.doQueryOneRow(sql2.toString(),Thread.currentThread().getStackTrace()[1].getMethodName());
			if (!ToolsHTML.isEmptyOrNull(prop2.getProperty("cargo"))) {
				nombrecargo = nombrecargo + prop2.getProperty("cargo") + "(";
				sql3.append("SELECT area FROM tbl_area WHERE idarea=").append(prop.getProperty("idarea"));
				log.debug("SQL HandlerDBUser.getNombreCargo (usuario)= " + sql3);
				Properties prop3 = JDBCUtil.doQueryOneRow(sql3.toString(),Thread.currentThread().getStackTrace()[1].getMethodName());
				if (!ToolsHTML.isEmptyOrNull(prop3.getProperty("area"))) {
					nombrecargo = nombrecargo + prop3.getProperty("area") + ")";
				}
			}
		}
		return nombrecargo;
	}

	// ydavila Ticket 001-00-003023
	public static String getNombreApellido(String usuario) throws Exception {
		String result = null;
		PreparedStatement pst = null;
		Connection con = null;
		String nombrecargo = null;
		con = JDBCUtil.getConnection(Thread.currentThread().getStackTrace()[1].getMethodName());
		StringBuffer sql = new StringBuffer("");
		StringBuffer sql2 = new StringBuffer("");
		StringBuffer sql3 = new StringBuffer("");
		sql.append("SELECT apellidos,nombres,cargo,idarea FROM person WHERE nameuser='").append(usuario).append("'");
		sql.append(" AND accountactive = '1'");
		log.debug("SQL HandlerDBUser.getNombreCargo (usuario)= " + sql);
		Properties prop = JDBCUtil.doQueryOneRow(sql.toString(),Thread.currentThread().getStackTrace()[1].getMethodName());
		if (!ToolsHTML.isEmptyOrNull(prop.getProperty("cargo"))) {
			nombrecargo = prop.getProperty("apellidos") + " " + prop.getProperty("nombres") + " - ";
		}
		return nombrecargo;
	}
	
	@SuppressWarnings("unlikely-arg-type")
	public static String getVersionSql() {
		String version = "";

		try {
			StringBuffer sql = new StringBuffer();
			switch (Constants.MANEJADOR_ACTUAL) {
			case Constants.MANEJADOR_MSSQL:
				sql.append("SELECT @@VERSION");
				break;
			case Constants.MANEJADOR_POSTGRES:
				sql.append("select version()");
				break;
			case Constants.MANEJADOR_MYSQL:
				sql.append("SHOW VARIABLES LIKE 'version'");
				version = "MySql ";
				break;
			}
			if(!sql.equals("")) {
				CachedRowSet crs = JDBCUtil.executeQuery(sql, Thread.currentThread().getStackTrace()[1].getMethodName());
				if (crs.next()) {
					version = version + crs.getString(1);
				}
			}
		} catch (Exception e) {
			version = "n/a";
			e.printStackTrace();
		}
		
		return version;
	}

}
