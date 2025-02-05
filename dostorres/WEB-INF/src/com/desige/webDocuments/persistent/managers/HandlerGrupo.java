package com.desige.webDocuments.persistent.managers;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.Vector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sun.jdbc.rowset.CachedRowSet;

import com.desige.webDocuments.document.comparators.DescriptComparator;
import com.desige.webDocuments.grupo.forms.grupoForm;
import com.desige.webDocuments.persistent.utils.JDBCUtil;
import com.desige.webDocuments.seguridad.forms.PermissionUserForm;
import com.desige.webDocuments.seguridad.forms.SeguridadUserForm;
import com.desige.webDocuments.utils.ApplicationExceptionChecked;
import com.desige.webDocuments.utils.Constants;
import com.desige.webDocuments.utils.DesigeConf;
import com.desige.webDocuments.utils.ToolsHTML;
import com.desige.webDocuments.utils.beans.BeanCheckSec;
import com.desige.webDocuments.utils.beans.Search;
import com.desige.webDocuments.utils.beans.Search3;

/**
 * Title: HandlerGrupo.java <br/>
 * Copyright: (c) 2004 Focus Consulting C.A.<br/>
 * 
 * @author Ing. Roger Rodríguez (RR)
 * @author Ing. Simon Rodríguez (SR)
 * @author Ing. Nelson Crespo (NC)
 * @version WebDocuments v3.0 <br/>
 *          Changes:<br/>
 *          <ul>
 *          <li>23/06/2004 (RR) Creation</li>
 *          <li>19/05/2005 (SR) Se inserto y actualizo editregister</li>
 *          <li>29/05/2005 (NC) Cambios en el manejo de la Seguridad</li>
 *          <li>02/06/2005 (SR) Se inserto y actualizo getToImpresion</li>
 *          <li>10/06/2005 (SR) Se inserto y actualizo toCheckTodos</li>
 *          <li>21/04/2006 (NC) Add field search in the security profile user and group</li>
 *          <Li>27/04/2006 (NC) Se agregó el método updateSecurityStructUser con 2 parámetros</li>
 *          <li>30/06/2006 (NC) Uso del Log y cambios menores en Seguridad</li>
 *          </ul>
 */

public class HandlerGrupo extends HandlerBD {
	/**
	 * 
	 */
	private static final long serialVersionUID = -8993913949616274686L;
	static Logger log = LoggerFactory.getLogger(HandlerGrupo.class.getName());
	public static final String nameTable = "groupusers";
	public static final String nameTable1 = "seguridadgrupo";
	public static final String nameTable2 = "seguridaduser";
	public static final String userTable = "person";

	/**
	 * 
	 * @return
	 * @throws Exception
	 */
	public static Collection getAllGrupos() throws Exception {
		StringBuilder sql = new StringBuilder("SELECT idGrupo,nombreGrupo FROM groupusers WHERE accountActive = '").append(Constants.permission).append("'");
		ArrayList resp = new ArrayList();
		Vector datos = JDBCUtil.doQueryVector(sql.toString(),Thread.currentThread().getStackTrace()[1].getMethodName());
		for (int row = 0; row < datos.size(); row++) {
			Properties properties = (Properties) datos.elementAt(row);
			Search bean = new Search(properties.getProperty("idGrupo"), properties.getProperty("nombreGrupo"));
			resp.add(bean);
		}
		return resp;
	}

	/**
	 * 
	 * @return
	 * @throws Exception
	 */
	public static boolean isOwnerDocuments(String nameUser) throws Exception {
		StringBuilder sql = new StringBuilder("SELECT count(*) As total FROM documents WHERE owner IN (select nameUser from person where nameUser=?) ");
		ArrayList param = new ArrayList();
		param.add(nameUser);
		CachedRowSet datos = JDBCUtil.executeQuery(sql, param, Thread.currentThread().getStackTrace()[1].getMethodName());
		if (datos.next()) {
			return (datos.getInt("total") > 0);
		}
		return false;
	}

	/**
	 * 
	 * @param securityGroups
	 * @return
	 * @throws Exception
	 */
	public static Collection getAllGroupsWithUsers(Hashtable securityGroups) throws Exception {
		StringBuilder sql = new StringBuilder("SELECT * FROM groupusers WHERE accountActive = '").append(Constants.permission).append("'");
		ArrayList resp = new ArrayList();
		Vector datos = JDBCUtil.doQueryVector(sql.toString(),Thread.currentThread().getStackTrace()[1].getMethodName());
		for (int row = 0; row < datos.size(); row++) {
			Properties properties = (Properties) datos.elementAt(row);
			String idGroup = properties.getProperty("idGrupo");
			log.debug("securityGroups = " + securityGroups);
			// if ( securityGroups!=null) {
			// PermissionUserForm forma =
			// (PermissionUserForm)securityGroups.get(idGroup);
			// if (forma!=null) {
			// if (forma.getToRead()==Constants.permission) {
			Search bean = new Search(properties.getProperty("idGrupo"), properties.getProperty("nombreGrupo"));
			resp.add(bean);
			// }
			// }
			// } else {
			// Search bean = new
			// Search(properties.getProperty("idGrupo"),properties.getProperty("nombreGrupo"));
			// resp.add(bean);
			// }
		}
		return resp;
	}

	/**
	 * 
	 * @param nameUsers
	 * @return
	 * @throws Exception
	 */
	public static String getEmailGrupos(String nameUsers) throws Exception {
		StringBuilder sql = new StringBuilder("SELECT email FROM person WHERE accountActive='1' ");
		if (!ToolsHTML.isEmptyOrNull(nameUsers)) {
			sql.append(" and nameUser IN ").append(nameUsers);
		}
		StringBuilder resp = new StringBuilder("");
		Vector datos = JDBCUtil.doQueryVector(sql.toString(),Thread.currentThread().getStackTrace()[1].getMethodName());
		for (int row = 0; row < datos.size(); row++) {
			Properties properties = (Properties) datos.elementAt(row);
			String bean = properties.getProperty("email");
			if (row == 0) {
				log.debug("row = " + row);
				resp.append(bean);
			} else {
				resp = resp.append(";").append(bean);
			}
		}
		return resp.toString();
	}

	/**
	 * Este método permite obtener los emails de todos los usuarios que pertenecen a un grupo en específico
	 * 
	 * @param idGroup
	 * @throws Exception
	 */
	public static void getEmailForGroup(String idGroup, ArrayList resp) throws Exception {
		if (resp == null) {
			resp = new ArrayList();
		}
		StringBuilder sql = new StringBuilder("SELECT DISTINCT(Email) FROM person");
		if (!ToolsHTML.isEmptyOrNull(idGroup)) {
			sql.append(" WHERE idGrupo IN ").append(idGroup);
		}
		Vector datos = JDBCUtil.doQueryVector(sql.toString(),Thread.currentThread().getStackTrace()[1].getMethodName());
		for (int row = 0; row < datos.size(); row++) {
			Properties properties = (Properties) datos.elementAt(row);
			String email = properties.getProperty("email");
			if (!resp.contains(email)) {
				resp.add(email);
			}
		}
	}

	/**
	 * 
	 * @param nameUsers
	 * @param resp
	 * @throws Exception
	 */
	public static void getEmailForUsers(String nameUsers, ArrayList resp) throws Exception {
		getEmailForUsers(null, nameUsers, resp);
	}

	public static void getEmailForUsers(Connection con, String nameUsers, ArrayList resp) throws Exception {
		if (resp == null) {
			resp = new ArrayList();
		}
		boolean setWhere = false;
		StringBuilder sql = new StringBuilder("SELECT email FROM person");
		if (!ToolsHTML.isEmptyOrNull(nameUsers)) {
			sql.append(" WHERE nameUser IN");
			if (nameUsers.trim().startsWith("(")) {
				sql.append(nameUsers);
			} else {
				sql.append("(").append(nameUsers).append(")");
			}
			setWhere = true;
		}
		if (setWhere) {
			sql.append(" AND accountActive = '").append(Constants.permission).append("'");
		} else {
			sql.append(" WHERE accountActive = '").append(Constants.permission).append("'");
		}
		log.debug("[getEmailForUsers] = " + sql.toString());
		Vector datos = JDBCUtil.doQueryVector(con, sql.toString(),Thread.currentThread().getStackTrace()[1].getMethodName());
		for (int row = 0; row < datos.size(); row++) {
			Properties properties = (Properties) datos.elementAt(row);
			String email = properties.getProperty("email");
			if (!resp.contains(email)) {
				resp.add(email);
			}
		}
	}

	/**
	 * 
	 * @param nameUsers
	 * @param resp
	 * @throws Exception
	 */
	public static String getEmailForUsers(String nameUsers) throws Exception {
		boolean setWhere = false;
		String sep = ";";

		StringBuilder sql = new StringBuilder("SELECT email FROM person");
		if (!ToolsHTML.isEmptyOrNull(nameUsers)) {
			sql.append(" WHERE nameUser IN");
			if (nameUsers.trim().startsWith("(")) {
				sql.append(nameUsers);
			} else {
				sql.append("(").append(nameUsers).append(")");
			}
			setWhere = true;
		}
		if (setWhere) {
			sql.append(" AND accountActive = '").append(Constants.permission).append("'");
		} else {
			sql.append(" WHERE accountActive = '").append(Constants.permission).append("'");
		}
		log.debug("[getEmailForUsers] = " + sql.toString());
		Vector datos = JDBCUtil.doQueryVector(sql.toString(),Thread.currentThread().getStackTrace()[1].getMethodName());
		sql.setLength(0);
		for (int row = 0; row < datos.size(); row++) {
			Properties properties = (Properties) datos.elementAt(row);
			String email = properties.getProperty("email");
			sql.append(sep);
			sql.append(email);
			sep = ";";
		}

		return sql.toString();
	}

	/**
	 * 
	 * @param nameUsers
	 * @param resp
	 * @throws Exception
	 */
	public static void getEmailForUsers(String[] nameUsers, ArrayList resp) throws Exception {
		String sep = "";
		String coma = ",";
		if (resp == null) {
			resp = new ArrayList();
		}
		boolean setWhere = false;
		StringBuilder sql = new StringBuilder("SELECT email FROM person");
		if (nameUsers != null && nameUsers.length > 0) {

			for (int i = 0; i < nameUsers.length; i++) {
				if (!ToolsHTML.isEmptyOrNull(nameUsers[i])) {
					if (!setWhere) {
						sql.append(" WHERE nameUser IN (");
					}
					sql.append(sep);
					sql.append("'").append(nameUsers[i]).append("'");

					setWhere = true;
					sep = coma;
				}
			}
			if (setWhere) {
				sql.append(") ");
			}
		}
		if (setWhere) {
			sql.append(" AND accountActive = '").append(Constants.permission).append("'");
		} else {
			sql.append(" WHERE accountActive = '").append(Constants.permission).append("'");
		}
		log.debug("[getEmailForUsers] = " + sql.toString());
		Vector datos = JDBCUtil.doQueryVector(sql.toString(),Thread.currentThread().getStackTrace()[1].getMethodName());
		for (int row = 0; row < datos.size(); row++) {
			Properties properties = (Properties) datos.elementAt(row);
			String email = properties.getProperty("email");
			if (!resp.contains(email)) {
				resp.add(email);
			}
		}
	}

	/**
	 * 
	 * @param id
	 * @return
	 * @throws Exception
	 */
	public static Collection getUsersGrupos(String id) throws Exception {
		StringBuilder sql = new StringBuilder("SELECT nameUser,Nombres,Apellidos,Email FROM person WHERE accountActive = '").append(Constants.permission)
				.append("' AND idGrupo = ").append(id).append(" ORDER BY lower(nombres) asc, lower(apellidos) asc");
		Vector resp = new Vector();
		Vector datos = JDBCUtil.doQueryVector(sql.toString(),Thread.currentThread().getStackTrace()[1].getMethodName());
		for (int row = 0; row < datos.size(); row++) {
			Properties properties = (Properties) datos.elementAt(row);
			Search3 bean = new Search3(properties.getProperty("nameUser"), properties.getProperty("Nombres"), properties.getProperty("Apellidos"),
					properties.getProperty("Email"));
			resp.add(bean);
		}
		return resp;
	}

	/**
	 * 
	 * @return
	 * @throws Exception
	 */
	public static Collection getDescriptionGrupos() throws Exception {
		StringBuilder sql = new StringBuilder("SELECT * FROM groupusers WHERE accountActive = '").append(String.valueOf(Constants.permission)).append("'");
		ArrayList resp = new ArrayList();
		Vector datos = JDBCUtil.doQueryVector(sql.toString(),Thread.currentThread().getStackTrace()[1].getMethodName());
		for (int row = 0; row < datos.size(); row++) {
			Properties properties = (Properties) datos.elementAt(row);
			Search3 bean = new Search3(properties.getProperty("idGrupo"), properties.getProperty("nombreGrupo"), "", properties.getProperty("descripcionGrupo"));
			resp.add(bean);
		}
		return resp;
	}

	/**
	 * 
	 * @param field
	 * @param cod
	 * @return
	 * @throws Exception
	 */
	public static String getFieldGrupo(String field, String cod) throws Exception {
		StringBuilder sql = new StringBuilder("SELECT ").append(field).append(" FROM ").append(nameTable).append(" WHERE idGrupo='").append(cod).append("'")
				.append(" AND accountActive = '").append(Constants.permission).append("'");
		Properties properties = JDBCUtil.doQueryOneRow(sql.toString(),Thread.currentThread().getStackTrace()[1].getMethodName());
		if (properties.getProperty("isEmpty").equalsIgnoreCase("false")) {
			return properties.getProperty(field);
		}
		return null;
	}

	/**
	 * 
	 * @param field
	 * @param cod
	 * @return
	 * @throws Exception
	 */
	public static String getFieldGrupo1(String field, String cod) throws Exception {
		StringBuilder sql = new StringBuilder("SELECT ").append(field).append(" FROM ").append(nameTable1).append(" WHERE idGrupo= ").append(cod);
		log.debug(sql.toString());
		Properties properties = JDBCUtil.doQueryOneRow(sql.toString(),Thread.currentThread().getStackTrace()[1].getMethodName());
		if (properties.getProperty("isEmpty").equalsIgnoreCase("false")) {
			return properties.getProperty(field);
		}
		return null;
	}

	/**
	 * 
	 * @param forma
	 * @param table
	 * @param isUser
	 * @param valueId
	 * @throws Exception
	 */
	public static void getFieldUser(SeguridadUserForm forma, String table, boolean isUser, String valueId) throws Exception {
		getFieldUser(null, forma, table, isUser, valueId);
	}
	public static void getFieldUser(Connection con, SeguridadUserForm forma, String table, boolean isUser, String valueId) throws Exception {
		StringBuilder sql = new StringBuilder("SELECT * ").append(" FROM ").append(table);
		if (isUser) {
			sql.append(" WHERE nameUser = '").append(valueId).append("'");
		} else {
			sql.append(" WHERE idGrupo = ").append(valueId);
		}
		sql.append(" AND active = '").append(Constants.permission).append("' ");
		Properties properties = JDBCUtil.doQueryOneRow(con, sql.toString(),Thread.currentThread().getStackTrace()[1].getMethodName());
		if (properties.getProperty("isEmpty").equalsIgnoreCase("false")) {
			forma.setAdministracion(Integer.parseInt(properties.getProperty("administracion")));
			forma.setMensajes(Integer.parseInt(properties.getProperty("mensajes")));
			forma.setEstructura(Integer.parseInt(properties.getProperty("estructura")));
			forma.setFlujos(Integer.parseInt(properties.getProperty("flujos")));
			forma.setPerfil(Integer.parseInt(properties.getProperty("perfil")));
			forma.setToImpresion(Integer.parseInt(properties.getProperty("toImpresion")));
			forma.setSearch(Integer.parseInt(properties.getProperty("search")));
			forma.setSacop(Integer.parseInt(properties.getProperty("sacop")));
			forma.setRecord(ToolsHTML.isNumeric(properties.getProperty("record")) ? Integer.parseInt(properties.getProperty("record")) : 1);
			forma.setFiles(ToolsHTML.isNumeric(properties.getProperty("files")) ? Integer.parseInt(properties.getProperty("files")) : 1);
			forma.setDigital(ToolsHTML.isNumeric(properties.getProperty("digital")) ? Integer.parseInt(properties.getProperty("digital")) : 1);
		}
	}

	/**
	 * 
	 * @param field
	 * @param cod
	 * @return
	 * @throws Exception
	 */
	public static String getFieldUser1(String field, String cod) throws Exception {
		StringBuilder sql = new StringBuilder("SELECT ").append(field).append(" FROM ").append(userTable).append(" p, seguridadgrupo sg WHERE nameUser= '")
				.append(cod).append("' AND p.idGrupo = sg.idGrupo").append(" AND p.accountActive = '").append(Constants.permission).append("' ")
				.append(" AND sg.active = '").append(Constants.permission).append("' ");
		log.debug("[getFieldUser1] = " + sql);
		Properties properties = JDBCUtil.doQueryOneRow(sql.toString(),Thread.currentThread().getStackTrace()[1].getMethodName());
		if (properties.getProperty("isEmpty").equalsIgnoreCase("false")) {
			return properties.getProperty(field);
		}
		return null;

	}

	/**
	 * 
	 * @param forma
	 * @throws Exception
	 */
	public static void load(grupoForm forma) throws Exception {
		StringBuilder sql = new StringBuilder("SELECT * FROM ").append(nameTable).append(" WHERE idGrupo = ").append(forma.getIdGrupo())
				.append(" AND accountActive = '").append(Constants.permission).append("' ");
		Properties prop = JDBCUtil.doQueryOneRow(sql.toString(),Thread.currentThread().getStackTrace()[1].getMethodName());
		if ((prop != null) && prop.getProperty("isEmpty").equalsIgnoreCase("false")) {
			forma.setNombreGrupo(prop.getProperty("nombreGrupo"));
			forma.setDescripcionGrupo(prop.getProperty("descripcionGrupo"));
		}
	}

	/**
	 * 
	 * @param forma
	 * @return
	 */
	public synchronized static boolean edit(grupoForm forma) {
		try {
			StringBuilder sql = new StringBuilder("UPDATE ").append(nameTable).append(" SET nombreGrupo='").append(forma.getNombreGrupo()).append("'")
					.append(",descripcionGrupo = '").append(forma.getDescripcionGrupo()).append("' WHERE idGrupo = '").append(forma.getIdGrupo()).append("'");
			log.debug(sql.toString());
			return JDBCUtil.doUpdate(sql.toString()) > 0;
		} catch (Exception e) {
			log.error(e.getMessage());
			setMensaje(e.getMessage());
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * 
	 * @param forma
	 */
	public synchronized static boolean insert(grupoForm forma) throws ApplicationExceptionChecked {

		StringBuilder sql = new StringBuilder("");
		try {
			sql.setLength(0);
			sql.append("SELECT nombreGrupo FROM ").append(nameTable).append(" WHERE nombreGrupo = '").append(forma.getNombreGrupo()).append("'")
					.append(" AND accountactive='").append(Constants.permission).append("'");
			log.debug("check=" + sql.toString());
			Properties prop = JDBCUtil.doQueryOneRow(sql.toString(),Thread.currentThread().getStackTrace()[1].getMethodName());
			// if (prop.getProperty("isEmpty").equalsIgnoreCase("false")) {
			if (prop.getProperty("isEmpty").equalsIgnoreCase("false")) {
				throw new ApplicationExceptionChecked("E0040");
			}
		} catch (ApplicationExceptionChecked ae) {
			log.error(ae.getMessage());
			throw ae;
		} catch (Exception ex) {
			log.error(ex.getMessage());
			ex.printStackTrace();
		}

		try {
			// int num = IDDBFactorySql.getNextID("grupo");
			int num = HandlerStruct.proximo("grupo", "groupusers", "idGrupo");

			sql.setLength(0);
			sql.append("INSERT INTO ").append(nameTable).append(" (idGrupo,nombreGrupo,descripcionGrupo) VALUES('").append(num).append("','")
					.append(forma.getNombreGrupo()).append("','").append(forma.getDescripcionGrupo()).append("')");
			log.debug("insert = " + sql);
			return JDBCUtil.doUpdate(sql.toString()) > 0;
		} catch (Exception e) {
			log.error(e.getMessage());
			setMensaje(e.getMessage());
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * 
	 * @param forma
	 * @return
	 * @throws ApplicationExceptionChecked
	 */
	public synchronized static boolean delete(grupoForm forma) throws ApplicationExceptionChecked {
		if (forma != null && forma.getIdGrupo().equalsIgnoreCase(DesigeConf.getProperty("application.admon"))) {
			throw new ApplicationExceptionChecked("E0034");
		}
		Connection con = null;
		PreparedStatement st = null;
		boolean result = false;
		try {
			if (HandlerPerfil.isCodInField("idGrupo", forma.getIdGrupo())) {
				throw new ApplicationExceptionChecked("E0042");
			}
			con = JDBCUtil.getConnection(Thread.currentThread().getStackTrace()[1].getMethodName());
			con.setAutoCommit(false);
			StringBuilder delete = new StringBuilder("UPDATE ").append(nameTable).append(" SET accountActive = '").append(Constants.notPermission).append("'")
					.append(" WHERE idGrupo = ").append(forma.getIdGrupo());
			st = con.prepareStatement(JDBCUtil.replaceCastMysql(delete.toString()));
			st.executeUpdate();
			st = con.prepareStatement(JDBCUtil.replaceCastMysql(HandlerSeguridad.getQuerySeguridadGrupoActive(forma.getIdGrupo(), false)));
			st.executeUpdate();
			st = con.prepareStatement(JDBCUtil.replaceCastMysql(HandlerSeguridad.getQuerySecurityStructGroupActive(forma.getIdGrupo(), false)));
			st.executeUpdate();
			con.commit();
			result = true;
		} catch (ApplicationExceptionChecked ae) {
			log.error(ae.getMessage());
			applyRollback(con);
			throw ae;
		} catch (Exception e) {
			log.error(e.getMessage());
			applyRollback(con);
			e.printStackTrace();
			result = false;
		} finally {
			setFinally(con, st);
		}
		return result;
	}

	/**
	 * 
	 * @param toSearch
	 * @return
	 * @throws Exception
	 */
	public static Collection getAllGroups(String toSearch) throws Exception {
		Vector result = new Vector();
		StringBuilder sql = new StringBuilder(1024).append("SELECT * FROM groupusers");
		boolean isWhere = false;
		if (toSearch != null) {
			sql.append(" WHERE nombreGrupo LIKE '%").append(toSearch).append("%'");
			isWhere = true;
		}
		if (isWhere) {
			sql.append(" AND ");
		} else {
			sql.append(" WHERE ");
		}
		sql.append(" accountActive = '").append(Constants.permission).append("'").append(" ORDER BY lower(nombreGrupo) ");
		Vector datos = JDBCUtil.doQueryVector(sql.toString(),Thread.currentThread().getStackTrace()[1].getMethodName());
		SeguridadUserForm forma = null;
		for (int row = 0; row < datos.size(); row++) {
			Properties properties = (Properties) datos.elementAt(row);
			forma = new SeguridadUserForm();
			forma.setNombres(properties.getProperty("nombreGrupo"));
			forma.setIdGrupo(properties.getProperty("IdGrupo"));
			forma.setNombreGrupo(properties.getProperty("descripcionGrupo"));
			result.add(forma);
		}
		return result;
	}

	/**
	 * 
	 * @param idGroup
	 * @return
	 * @throws Exception
	 */
	public static String getNameGroup(long idGroup) throws Exception {
		Vector result = new Vector();
		StringBuilder sql = new StringBuilder(1024).append("SELECT nombreGrupo FROM groupusers ").append("WHERE idGrupo = '").append(idGroup).append("' ")
				.append("AND accountActive = '").append(Constants.permission).append("' ");
		Vector datos = JDBCUtil.doQueryVector(sql.toString(),Thread.currentThread().getStackTrace()[1].getMethodName());
		SeguridadUserForm forma = null;
		if (datos.size() > 0) {
			Properties properties = (Properties) datos.elementAt(0);
			return properties.getProperty("nombreGrupo");
		}
		return null;
	}

	/**
	 * 
	 * @param idDocument
	 * @return
	 * @throws ApplicationExceptionChecked
	 */
	public static Collection getSecutityForAllGroupsInDocs(String idDocument) throws ApplicationExceptionChecked {
		Vector result = new Vector();
		try {
			StringBuilder sql = new StringBuilder(1024).append("SELECT distinct gu.*,pg.idDocument FROM permisiondocgroup pg,groupusers gu,documents d ")
					.append("WHERE gu.idGrupo = cast(pg.idGroup as int) AND gu.accountActive = '1' ").append("AND d.numgen = pg.idDocument ")
					.append("AND pg.idDocument=").append(idDocument);
			Vector datos = JDBCUtil.doQueryVector(sql.toString(),Thread.currentThread().getStackTrace()[1].getMethodName());
			SeguridadUserForm forma = null;
			for (int row = 0; row < datos.size(); row++) {
				Properties properties = (Properties) datos.elementAt(row);
				forma = new SeguridadUserForm();
				forma.setNombres(properties.getProperty("nombreGrupo"));
				forma.setIdGrupo(properties.getProperty("IdGrupo"));
				forma.setNombreGrupo(properties.getProperty("descripcionGrupo"));
				forma.setIsDocument(properties.getProperty("idDocument"));
				result.add(forma);
			}
		} catch (Exception ex) {
			log.error(ex.getMessage());
			ex.printStackTrace();
			throw new ApplicationExceptionChecked("E0050");
		}
		return result;
	}

	/**
	 * 
	 * @return
	 * @throws ApplicationExceptionChecked
	 */
	public static Collection getSecutityForAllGroupsInRecord() throws ApplicationExceptionChecked {
		Vector result = new Vector();
		try {
			StringBuilder sql = new StringBuilder(1024).append("SELECT distinct gu.* FROM permissionrecordgroup pg,groupusers gu ").append(
					"WHERE gu.idGrupo = cast(pg.idGroup as int) AND gu.accountActive = '1' ");
			Vector datos = JDBCUtil.doQueryVector(sql.toString(),Thread.currentThread().getStackTrace()[1].getMethodName());
			SeguridadUserForm forma = null;
			for (int row = 0; row < datos.size(); row++) {
				Properties properties = (Properties) datos.elementAt(row);
				forma = new SeguridadUserForm();
				forma.setNombres(properties.getProperty("nombreGrupo"));
				forma.setIdGrupo(properties.getProperty("IdGrupo"));
				forma.setNombreGrupo(properties.getProperty("descripcionGrupo"));
				result.add(forma);
			}
		} catch (Exception ex) {
			log.error(ex.getMessage());
			ex.printStackTrace();
			throw new ApplicationExceptionChecked("E0050");
		}
		return result;
	}

	/**
	 * 
	 * @return
	 * @throws ApplicationExceptionChecked
	 */
	public static Collection getSecutityForAllGroupsInFiles() throws ApplicationExceptionChecked {
		Vector result = new Vector();
		try {
			StringBuilder sql = new StringBuilder(1024).append("SELECT distinct gu.* FROM permissionfilesgroup pg,groupusers gu ").append(
					"WHERE cast(gu.idGrupo as int) = cast(pg.idGroup as int) AND gu.accountActive = '1' ");
			Vector datos = JDBCUtil.doQueryVector(sql.toString(),Thread.currentThread().getStackTrace()[1].getMethodName());
			SeguridadUserForm forma = null;
			for (int row = 0; row < datos.size(); row++) {
				Properties properties = (Properties) datos.elementAt(row);
				forma = new SeguridadUserForm();
				forma.setNombres(properties.getProperty("nombreGrupo"));
				forma.setIdGrupo(properties.getProperty("IdGrupo"));
				forma.setNombreGrupo(properties.getProperty("descripcionGrupo"));
				result.add(forma);
			}
		} catch (Exception ex) {
			log.error(ex.getMessage());
			ex.printStackTrace();
			throw new ApplicationExceptionChecked("E0050");
		}
		return result;
	}

	/**
	 * 
	 * @param idGroup
	 * @param security
	 * @param idDoc
	 * @return
	 * @throws Exception
	 */
	public static Hashtable getAllSecurityForStructGroupDocs(String idGroup, Hashtable security, String idDoc) throws Exception {
		log.debug("Iniciando");
		StringBuilder sql = new StringBuilder(1024).append("SELECT distinct pg.*, d.numgen  FROM permissionstructgroups pg,groupusers gu,documents d ")
				.append("WHERE gu.idGrupo = pg.idGroup ").append("AND gu.accountActive = '1' ").append("AND pg.idGroup=").append(idGroup).append(" ")
				.append("AND d.idNode = pg.idstruct ").append(" AND d.numgen IN ('").append(idDoc.replaceAll(",", "','")).append("')");

		log.debug("[getSecutityForAllGroupsInDocs] sql = " + sql);
		Vector datos = JDBCUtil.doQueryVector(sql.toString(),Thread.currentThread().getStackTrace()[1].getMethodName());
		PermissionUserForm forma = null;
		for (int row = 0; row < datos.size(); row++) {
			Properties properties = (Properties) datos.elementAt(row);
			forma = new PermissionUserForm(properties.getProperty("numgen"));
			forma.setIdUser(idGroup);
			forma.setIdGroup(Long.parseLong(idGroup));
			setSecurityInDocs(properties, forma, true);
			// result.put(forma.getIdStruct(),forma);
			security.put(forma.getIdStruct().trim(), forma);
		}
		log.debug("Finalizando");
		return security;
	}

	/**
	 * 
	 * @param idGroup
	 * @param security
	 * @param idDoc
	 * @return
	 * @throws Exception
	 */
	public static Hashtable getAllSecurityForGroupDocs(String idGroup, Hashtable security, String idDoc) throws Exception {
		log.debug("Iniciando");
		StringBuilder sql = new StringBuilder(1024).append("SELECT distinct pg.* FROM permisiondocgroup pg,groupusers gu,documents d ")
				.append("WHERE gu.idGrupo = cast(pg.idGroup as int) AND gu.accountActive = '1' ").append("AND d.numgen = pg.idDocument ")
				.append("AND pg.idGroup='").append(idGroup).append("' ").append("AND idDocument IN ('").append(idDoc.replaceAll(",", "','")).append("')");

		log.debug("[getSecutityForAllGroupsInDocs] sql = " + sql);
		Vector datos = JDBCUtil.doQueryVector(sql.toString(),Thread.currentThread().getStackTrace()[1].getMethodName());
		PermissionUserForm forma = null;
		for (int row = 0; row < datos.size(); row++) {
			Properties properties = (Properties) datos.elementAt(row);
			forma = new PermissionUserForm(properties.getProperty("idDocument"));
			forma.setIdUser(idGroup);
			forma.setIdGroup(Long.parseLong(idGroup));
			setSecurityInDocs(properties, forma, false);
			// result.put(forma.getIdStruct(),forma);
			security.put(forma.getIdStruct().trim(), forma);
		}
		log.debug("Finalizando");
		return security;
	}

	/**
	 * 
	 * @param idNode
	 * @param toSearch
	 * @return
	 * @throws ApplicationExceptionChecked
	 */
	public static Collection getSecutityForAllGroupsInNode(String idNode, String toSearch) throws ApplicationExceptionChecked {
		Vector result = new Vector();
		try {
			StringBuilder sql = new StringBuilder(1024).append("SELECT gu.* FROM permissionstructgroups pg,groupusers gu")
					.append(" WHERE gu.idGrupo = pg.idGroup AND gu.accountActive = '").append(Constants.permission).append("'").append(" AND idStruct = ")
					.append(idNode).append(" AND permisoModificado = ").append(idNode);
			if (!ToolsHTML.isEmptyOrNull(toSearch)) {
				sql.append(" AND nombreGrupo LIKE '%").append(toSearch).append("%'");
			}
			Vector datos = JDBCUtil.doQueryVector(sql.toString(),Thread.currentThread().getStackTrace()[1].getMethodName());
			SeguridadUserForm forma = null;
			for (int row = 0; row < datos.size(); row++) {
				Properties properties = (Properties) datos.elementAt(row);
				forma = new SeguridadUserForm();
				forma.setNombres(properties.getProperty("nombreGrupo"));
				forma.setIdGrupo(properties.getProperty("IdGrupo"));
				forma.setNombreGrupo(properties.getProperty("descripcionGrupo"));
				result.add(forma);
			}
		} catch (Exception ex) {
			log.error(ex.getMessage());
			ex.printStackTrace();
			throw new ApplicationExceptionChecked("E0050");
		}
		return result;
	}

	/**
	 * 
	 * @param forma
	 * @throws Exception
	 */
	public static void loadSecurityStructGroup(PermissionUserForm forma) throws Exception {
		StringBuilder sql = new StringBuilder(1024).append("SELECT * FROM permissionstructgroups WHERE idStruct = ").append(forma.getIdStruct())
				.append(" AND idGroup = ").append(forma.getIdGroup()).append(" AND active = '").append(Constants.permission).append("'");
		Properties prop = JDBCUtil.doQueryOneRow(sql.toString(),Thread.currentThread().getStackTrace()[1].getMethodName());
		log.debug("[loadSecurityStructGroup] = " + sql.toString());
		if (prop.getProperty("isEmpty").equalsIgnoreCase("false")) {
			setSecurityInNode(prop, forma);
		} else {
			setEmptySecurity(forma);
		}
	}

	/**
	 * 
	 * @param forma
	 * @throws Exception
	 */
	public static void loadSecurityStructGroupRecord(PermissionUserForm forma) throws Exception {
		StringBuilder sql = new StringBuilder(100);
		sql.append("SELECT * FROM permissionrecordgroup ");
		sql.append(" WHERE cast(idGroup as int) = cast(").append(forma.getIdGroup()).append(" as int) ");
		Properties prop = JDBCUtil.doQueryOneRow(sql.toString(),Thread.currentThread().getStackTrace()[1].getMethodName());
		log.debug("[loadSecurityStructGroup] = " + sql.toString());
		if (prop.getProperty("isEmpty").equalsIgnoreCase("false")) {
			setSecurityInRecord(prop, forma);
		} else {
			setEmptySecurity(forma);
		}
	}

	/**
	 * 
	 * @param forma
	 * @throws Exception
	 */
	public static void loadSecurityStructGroupFiles(PermissionUserForm forma) throws Exception {
		StringBuilder sql = new StringBuilder(1024).append("SELECT * FROM permissionfilesgroup ").append(" WHERE cast(idGroup as int) = cast(")
				.append(forma.getIdGroup()).append(" as int) ");
		Properties prop = JDBCUtil.doQueryOneRow(sql.toString(),Thread.currentThread().getStackTrace()[1].getMethodName());
		log.debug("[loadSecurityStructGroupFiles] = " + sql.toString());
		if (prop.getProperty("isEmpty").equalsIgnoreCase("false")) {
			setSecurityInFiles(prop, forma);
		} else {
			setEmptySecurity(forma);
		}
	}

	/**
	 * 
	 * @param idNode
	 * @return
	 * @throws Exception
	 */
	public static Collection getAllSecurityForGroupAndIdNode(String idNode) throws Exception {
		PermissionUserForm forma = null;
		Vector result = new Vector();
		StringBuilder sql = new StringBuilder(1024).append("SELECT * FROM permissionstructgroups WHERE idStruct = ").append(idNode);
		sql.append(" AND active = '").append(Constants.permission).append("'");
		log.debug("[getAllSecurityForGroupAndIdNode] = " + sql.toString());
		Vector datos = JDBCUtil.doQueryVector(sql.toString(),Thread.currentThread().getStackTrace()[1].getMethodName());
		for (int row = 0; row < datos.size(); row++) {
			Properties properties = (Properties) datos.elementAt(row);
			forma = new PermissionUserForm();
			forma.setIdUser(properties.getProperty("idGroup"));
			forma.setIdGroup(Long.parseLong(properties.getProperty("idGroup")));
			setSecurityInNode(properties, forma);
			result.add(forma);
		}
		return result;
	}

	/**
	 * 
	 * @param forma
	 * @return
	 * @throws Exception
	 */
	public static synchronized boolean deleteSecurityStructGroup(PermissionUserForm forma) throws Exception {
		StringBuilder sql = new StringBuilder(1024).append("DELETE FROM permissionstructgroups WHERE idStruct = ").append(forma.getIdStruct())
				.append(" AND idGroup = ").append(forma.getIdGroup()).append(" AND active = '").append(Constants.permission).append("'");
		return JDBCUtil.doUpdate(sql.toString()) > 0;
	}

	/**
	 * 
	 * @param idStruct
	 * @param idGroup
	 * @return
	 * @throws Exception
	 */
	public static boolean checkExistSecurity(String idStruct, long idGroup) throws Exception {
		StringBuilder sql = new StringBuilder(1024).append("SELECT idStruct FROM permissionstructgroups WHERE idStruct = ").append(idStruct)
				.append(" AND idGroup = ").append(idGroup).append(" AND active = '").append(Constants.permission).append("'");
		Properties prop = JDBCUtil.doQueryOneRow(sql.toString(),Thread.currentThread().getStackTrace()[1].getMethodName());
		if (prop.getProperty("isEmpty").equalsIgnoreCase("false")) {
			return true;
		}
		return false;
	}

	/**
	 * 
	 * @param idStruct
	 * @param idGroup
	 * @return
	 * @throws Exception
	 */

	public static ArrayList<String> checkExistSecurity(long idGroup) throws Exception {
		ArrayList<String> lista = new ArrayList<String>();
		
		StringBuffer sql = new StringBuffer(60);
		sql.append("SELECT idStruct FROM permissionstructgroups ");
		sql.append("WHERE idGroup = ").append(idGroup);
		sql.append(" AND active = '").append(Constants.permission).append("'");
		
		CachedRowSet crs = JDBCUtil.executeQuery(sql, Thread.currentThread().getStackTrace()[1].getMethodName());
		
		while(crs.next()) {
			lista.add(crs.getString(1));
		}
				
		return lista;
	}

	public static int checkModifySecurity(String idStruct, long idGroup) throws Exception {
		int retorno = 0;
		StringBuilder sql = new StringBuilder(1024)
				.append("SELECT CASE WHEN permisoModificado is null THEN '0' ELSE permisoModificado END as permisoModificado  FROM permissionstructgroups WHERE idStruct = ")
				.append(idStruct).append(" AND idGroup = ").append(idGroup).append(" AND active = '").append(Constants.permission).append("'");
		log.debug("[checkModifySecurity] " + sql);
		CachedRowSet crs = JDBCUtil.executeQuery(sql, Thread.currentThread().getStackTrace()[1].getMethodName());
		if (crs.next()) {
			retorno = crs.getInt("permisoModificado");
		}
		return retorno;
	}

	public static HashMap<String, Integer> checkModifySecurity(long idGroup) throws Exception {
		HashMap<String, Integer> lista = new HashMap<String, Integer>();

		StringBuffer sql = new StringBuffer(60);
		sql.append("SELECT idStruct, CASE WHEN permisoModificado is null THEN '0' ELSE permisoModificado END as permisoModificado  FROM permissionstructgroups ");
		sql.append("WHERE idGroup = ").append(idGroup);
		sql.append(" AND active = '").append(Constants.permission).append("'");
		log.debug("[checkModifySecurity] " + sql);

		CachedRowSet crs = JDBCUtil.executeQuery(sql, Thread.currentThread().getStackTrace()[1].getMethodName());

		while (crs.next()) {
			lista.put(crs.getString(1), crs.getInt(2));
		}

		return lista;
	}

	/**
	 * 
	 * @param id
	 * @param data
	 * @param idGroup
	 * @throws Exception
	 */
	private static void getChields(String id, Hashtable data, long idGroup) throws Exception {
		if (!ToolsHTML.isEmptyOrNull(id)) {
			Collection datos = HandlerStruct.getChildsNode(id);
			if (datos != null && !datos.isEmpty()) {
				Vector resultado = new Vector();
				for (Iterator iterator = datos.iterator(); iterator.hasNext();) {
					String idNode = (String) iterator.next();
					boolean exits = checkExistSecurity(idNode, idGroup);
					int modify = checkModifySecurity(idNode, idGroup);
					BeanCheckSec bean = new BeanCheckSec(idNode, exits, modify);
					resultado.add(bean);
					getChields(idNode, data, idGroup);
				}
				if (!resultado.isEmpty()) {
					data.put(id, resultado);
				}
			}
		}
	}

	// public static Hashtable getStructChilds(String id,long idPerson) throws
	// Exception {
	// Hashtable result = new Hashtable();
	// if (!ToolsHTML.isEmptyOrNull(id)) {
	// Collection datos = HandlerStruct.getChildsNode(id);
	// if (datos!=null&&!datos.isEmpty()) {
	// Vector resultado = new Vector();
	// for (Iterator iterator = datos.iterator(); iterator.hasNext();) {
	// String node = (String) iterator.next();
	// boolean exits = HandlerDBUser.checkExistSecurity(node,idPerson);
	// BeanCheckSec bean = new BeanCheckSec(node,exits);
	// resultado.add(bean);
	// getChields(node,result,idPerson);
	// }
	// if (!resultado.isEmpty()) {
	// result.put(id,resultado);
	// }
	// }
	// }
	// return result;
	// }

	private static String getQueryDeleteSecurity(PermissionUserForm forma, String idStruct) {
		return new StringBuilder(1024).append("DELETE FROM permissionstructgroups ").append(" WHERE idStruct = ").append(idStruct).append(" AND idGroup = ")
				.append(forma.getIdGroup()).append(" AND active = '").append(Constants.permission).append("'").toString();
	}

	private static String getQueryUpdateSecurity(PermissionUserForm forma, String idStruct) {
		StringBuffer update = new StringBuffer(100);
		update.append("UPDATE permissionstructgroups SET toRead = '").append(forma.getToRead()).append("'");
		update.append(",toView = '").append(forma.getToView()).append("'");
		update.append(",toAddFolder = '").append(forma.getToAddFolder()).append("'");
		update.append(",toAddProcess = '").append(forma.getToAddProcess()).append("'");
		update.append(",toDelete = '").append(forma.getToDelete()).append("'");
		update.append(",toEdit = '").append(forma.getToEdit()).append("'");
		update.append(",toMove = '").append(forma.getToMove()).append("'");
		update.append(",toAddDocument = '").append(forma.getToAddDocument()).append("'");
		update.append(",toAdmon = '").append(forma.getToAdmon()).append("'");
		update.append(",toViewDocs = '").append(forma.getToViewDocs()).append("'");
		update.append(",toEditDocs = '").append(forma.getToEditDocs()).append("'");
		update.append(",toAdmonDocs = '").append(forma.getToAdmonDocs()).append("'");
		update.append(",toDelDocs = '").append(forma.getToDelDocs()).append("'");
		update.append(",toReview = '").append(forma.getToReview()).append("'");
		update.append(",toApproved = '").append(forma.getToAprove()).append("'");
		update.append(",toMoveDocs = '").append(forma.getToMoveDocs()).append("'");
		update.append(",toCheckOut = '").append(forma.getCheckOut()).append("'");
		update.append(",toEditRegister = '").append(forma.getToEditRegister()).append("'");
		update.append(",toImpresion = '").append(forma.getToImpresion()).append("'");
		update.append(",toCheckTodos = '").append(forma.getToCheckTodos()).append("'");
		update.append(",toDoFlows = '").append(forma.getToDoFlows()).append("'");
		update.append(",docinLine = '").append(forma.getToDocinLine()).append("'");
		update.append(",toFlexFlow = '").append(forma.getToFlexFlow()).append("'");
		update.append(",toChangeUsr = '").append(forma.getToChangeUsr()).append("'");
		update.append(",toCompleteFlow = '").append(forma.getToCompleteFlow()).append("'");
		update.append(",permisoModificado = '").append(forma.getPermisoModificado()).append("'");
		update.append(",toPublicEraser = '").append(forma.getToPublicEraser()).append("'");
		update.append(",toDownload = '").append(forma.getToDownload()).append("'");
		update.append(" WHERE idStruct = ").append(idStruct);
		update.append(" AND idGroup = ").append(forma.getIdGroup());
		update.append(" AND active = '").append(Constants.permission).append("'");
		return update.toString();
	}

	private static String getQueryUpdateSecurity(PermissionUserForm forma, StringBuffer idStruct) {
		StringBuffer update = new StringBuffer(100);
		update.append("UPDATE permissionstructgroups SET toRead = '").append(forma.getToRead()).append("'");
		update.append(",toView = '").append(forma.getToView()).append("'");
		update.append(",toAddFolder = '").append(forma.getToAddFolder()).append("'");
		update.append(",toAddProcess = '").append(forma.getToAddProcess()).append("'");
		update.append(",toDelete = '").append(forma.getToDelete()).append("'");
		update.append(",toEdit = '").append(forma.getToEdit()).append("'");
		update.append(",toMove = '").append(forma.getToMove()).append("'");
		update.append(",toAddDocument = '").append(forma.getToAddDocument()).append("'");
		update.append(",toAdmon = '").append(forma.getToAdmon()).append("'");
		update.append(",toViewDocs = '").append(forma.getToViewDocs()).append("'");
		update.append(",toEditDocs = '").append(forma.getToEditDocs()).append("'");
		update.append(",toAdmonDocs = '").append(forma.getToAdmonDocs()).append("'");
		update.append(",toDelDocs = '").append(forma.getToDelDocs()).append("'");
		update.append(",toReview = '").append(forma.getToReview()).append("'");
		update.append(",toApproved = '").append(forma.getToAprove()).append("'");
		update.append(",toMoveDocs = '").append(forma.getToMoveDocs()).append("'");
		update.append(",toCheckOut = '").append(forma.getCheckOut()).append("'");
		update.append(",toEditRegister = '").append(forma.getToEditRegister()).append("'");
		update.append(",toImpresion = '").append(forma.getToImpresion()).append("'");
		update.append(",toCheckTodos = '").append(forma.getToCheckTodos()).append("'");
		update.append(",toDoFlows = '").append(forma.getToDoFlows()).append("'");
		update.append(",docinLine = '").append(forma.getToDocinLine()).append("'");
		update.append(",toFlexFlow = '").append(forma.getToFlexFlow()).append("'");
		update.append(",toChangeUsr = '").append(forma.getToChangeUsr()).append("'");
		update.append(",toCompleteFlow = '").append(forma.getToCompleteFlow()).append("'");
		update.append(",permisoModificado = '").append(forma.getPermisoModificado()).append("'");
		update.append(",toPublicEraser = '").append(forma.getToPublicEraser()).append("'");
		update.append(",toDownload = '").append(forma.getToDownload()).append("'");
		update.append(" WHERE idStruct IN (").append(idStruct).append(")");
		update.append(" AND idGroup = ").append(forma.getIdGroup());
		update.append(" AND active = '").append(Constants.permission).append("'");
		return update.toString();
	}

	/**
	 * 
	 * @param id
	 * @param data
	 * @param forma
	 * @param st
	 * @param con
	 * @throws Exception
	 */
	private static void deleteTableSecurity(String id, Hashtable data, PermissionUserForm forma, PreparedStatement st, Connection con) throws Exception {
		if (!ToolsHTML.isEmptyOrNull(id)) {
			HandlerDBUser.checkExistSecurityDeleteDocsGrup(id, forma.getIdGroup(), forma, st, con);
			Collection childs = (Collection) data.get(id);
			if (childs != null && !childs.isEmpty()) {
				for (Iterator iterator = childs.iterator(); iterator.hasNext();) {
					BeanCheckSec idChild = (BeanCheckSec) iterator.next();
					// primero que nada delete los documentos del nodo id hijo
					HandlerDBUser.checkExistSecurityDeleteDocsGrup(idChild.getIdNode(), forma.getIdGroup(), forma, st, con);
					if (idChild.isExits()) {
						st = con.prepareStatement(JDBCUtil.replaceCastMysql(getQueryDeleteSecurity(forma, idChild.getIdNode())));
						st.executeUpdate();
					}
					deleteTableSecurity(idChild.getIdNode(), data, forma, st, con);
				}
			}
		}
	}

	/**
	 * 
	 * @param id
	 * @param data
	 * @param forma
	 * @param st
	 * @param con
	 * @throws Exception
	 */
	private static void updateTableSecurityDeprecate(String id, Hashtable data, PermissionUserForm forma, PreparedStatement st, Connection con) throws Exception {
		if (!ToolsHTML.isEmptyOrNull(id)) {
			// HandlerDBUser.checkExistSecurityDocsGrup(id,forma.getIdGroup(),forma,st,con);
			Collection childs = (Collection) data.get(id);
			if (childs != null && !childs.isEmpty()) {
				for (Iterator iterator = childs.iterator(); iterator.hasNext();) {
					BeanCheckSec idChild = (BeanCheckSec) iterator.next();
					
					//debo verificar si la seguridad de este nodo debe ser ajustada
					//el unico caso en el que no se ajusta es:
					// 1.- cuando el id del nodo, es igual al del permiso modificado
					boolean storeSecutiry = true;
					
					try {
						if(idChild.getIdNode().equals(Integer.toString(idChild.getPermisoModificado()))){
							storeSecutiry = false;
						}
					} catch (Exception e) {
						// TODO: handle exception
						log.error("Error verificando permisologia de grupo propia o no, en nodo " + idChild.getIdNode(), e);
					}
					
					if(storeSecutiry){
						//if (idChild.getPermisoModificado() == 0 || idChild.getPermisoModificado() == Integer.parseInt(forma.getIdStruct()) || idChild.getPermisoModificado() == forma.getPermisoModificado()) {
							log.info("Almacenando seguridad de grupo para el nodo " + idChild.getIdNode());
							int permisoModificadoAnterior = forma.getPermisoModificado();
							forma.setPermisoModificado(Integer.parseInt(forma.getIdStruct()));
							if (idChild.isExits()) {
								st = con.prepareStatement(getQueryUpdateSecurity(forma, idChild.getIdNode()));
								st.executeUpdate();
							} else {
								insertSecurityStructGroup(forma, idChild.getIdNode(), con);
							}
							forma.setPermisoModificado(permisoModificadoAnterior);
						//}
						// primero que nada actualizamos los documentos del nodo id hijo
						//HandlerDBUser.checkExistSecurityDocsGrup(idChild.getIdNode(), forma.getIdGroup(), forma, st, con); // NO SE DEBEN ACTUALIZAR LOS PERMISOS EN LOS DOCUMENTOS

						// recursivo
						updateTableSecurity(idChild.getIdNode(), data, forma, st, con);
					} else {
						log.info("El nodo " + idChild.getIdNode() + " tiene seguridad propia de grupo, se frena la cascada aqui y se pasa al siguiente.");
					}
				}
			}
		}
	}

	private static void updateTableSecurity(String id, Hashtable data, PermissionUserForm forma, PreparedStatement st, Connection con) throws Exception {
		if (!ToolsHTML.isEmptyOrNull(id)) {
			StringBuffer insert = new StringBuffer();
			StringBuffer update = new StringBuffer();
			String sepInsert = "";
			String sepUpdate = "";
			String coma=",";
			
			
			// HandlerDBUser.checkExistSecurityDocsGrup(id,forma.getIdGroup(),forma,st,con);
			Collection childs = (Collection) data.get(id);
			if (childs != null && !childs.isEmpty()) {
				for (Iterator iterator = childs.iterator(); iterator.hasNext();) {
					BeanCheckSec idChild = (BeanCheckSec) iterator.next();
					
					//debo verificar si la seguridad de este nodo debe ser ajustada
					//el unico caso en el que no se ajusta es:
					// 1.- cuando el id del nodo, es igual al del permiso modificado
					boolean storeSecutiry = true;
					
					try {
						if(idChild.getIdNode().equals(Integer.toString(idChild.getPermisoModificado()))){
							storeSecutiry = false;
						}
					} catch (Exception e) {
						// TODO: handle exception
						log.error("Error verificando permisologia de grupo propia o no, en nodo " + idChild.getIdNode(), e);
					}

					if (storeSecutiry) {
						// if (idChild.getPermisoModificado() == 0 ||
						// idChild.getPermisoModificado() ==
						// Integer.parseInt(forma.getIdStruct()) ||
						// idChild.getPermisoModificado() ==
						// forma.getPermisoModificado()) {
						log.info("Almacenando seguridad de grupo para el nodo " + idChild.getIdNode());
						int permisoModificadoAnterior = forma.getPermisoModificado();
						forma.setPermisoModificado(Integer.parseInt(forma.getIdStruct()));
						if (idChild.isExits()) {
							st = con.prepareStatement(JDBCUtil.replaceCastMysql(getQueryUpdateSecurity(forma, idChild.getIdNode())));
							st.executeUpdate();
						} else {
							insertSecurityStructGroup(forma, idChild.getIdNode(), con);
						}
						forma.setPermisoModificado(permisoModificadoAnterior);
						// }
						// primero que nada actualizamos los documentos del nodo
						// id hijo
						// HandlerDBUser.checkExistSecurityDocsGrup(idChild.getIdNode(),
						// forma.getIdGroup(), forma, st, con);

						// recursivo
						updateTableSecurity(idChild.getIdNode(), data, forma, st, con);
					} else {
						log.info("El nodo " + idChild.getIdNode() + " tiene seguridad propia de grupo, se frena la cascada aqui y se pasa al siguiente.");
					}
				}
				
				// ejecutaremos los query de actualizacion
				if(update.length()>0) {
					st = con.prepareStatement(getQueryUpdateSecurity(forma, update));
					int act = st.executeUpdate();
					//System.out.println("Registro de estructura actualizados act = ".concat(String.valueOf(act)));
				}
				// ejecutaremos los query de insercion 
				if(insert.length()>0) {
					st = con.prepareStatement(insert.toString());
					int act = st.executeUpdate();
					//System.out.println("Registro de estructura insertados act = ".concat(String.valueOf(act)));
				}
				
			}
		}
	}

	public static synchronized boolean deleteSecurityStructGroups(PermissionUserForm forma) throws Exception {

		Connection con = null;
		boolean resp = false;
		PreparedStatement st = null;
		ResultSet rs = null;
		CachedRowSet crs = new CachedRowSet();
		StringBuffer base = new StringBuffer();
		StringBuffer update = new StringBuffer();
		StringBuffer consulta = new StringBuffer();

		// Se Cargan los Ids de Todos los Nodos Hijos....
		String hijos = HandlerStruct.getAllNodesChilds(forma.getIdStruct());
		log.debug("Hijos: " + hijos);

		consulta.append(" SELECT a.*,b.idNode FROM permissionstructgroups a, struct b ");
		consulta.append(" WHERE a.idStruct=b.idNode ");
		consulta.append(" and idGroup = ").append(forma.getIdGroup());
		consulta.append(" and b.idNode =  cast((select x.idNodeParent from struct x where x.idNode = ");
		consulta.append(forma.getIdStruct()).append(") as int) ");

		try {
			crs = JDBCUtil.executeQuery(consulta, Thread.currentThread().getStackTrace()[1].getMethodName());
			if (crs.next()) { // si tiene un padre actualizamos este y los hijos con el codigo del padre para heredar los permisos

				update.append(" UPDATE permissionstructgroups SET ");
				update.append("  toView='").append(JDBCUtil.getByte(crs.getString("toView"))).append("'");
				update.append(", toRead='").append(JDBCUtil.getByte(crs.getString("toRead"))).append("'");
				update.append(", toAddFolder='").append(JDBCUtil.getByte(crs.getString("toAddFolder"))).append("'");
				update.append(", toAddProcess='").append(JDBCUtil.getByte(crs.getString("toAddProcess"))).append("'");
				update.append(", toDelete='").append(JDBCUtil.getByte(crs.getString("toDelete"))).append("'");
				update.append(", toMove='").append(JDBCUtil.getByte(crs.getString("toMove"))).append("'");
				update.append(", toEdit='").append(JDBCUtil.getByte(crs.getString("toEdit"))).append("'");
				update.append(", toAddDocument='").append(JDBCUtil.getByte(crs.getString("toAddDocument"))).append("'");
				update.append(", toAdmon='").append(JDBCUtil.getByte(crs.getString("toAdmon"))).append("'");
				update.append(", active='").append(JDBCUtil.getByte(crs.getString("active"))).append("'");
				update.append(", toViewDocs='").append(JDBCUtil.getByte(crs.getString("toViewDocs"))).append("'");
				update.append(", toEditDocs='").append(JDBCUtil.getByte(crs.getString("toEditDocs"))).append("'");
				update.append(", toAdmonDocs='").append(JDBCUtil.getByte(crs.getString("toAdmonDocs"))).append("'");
				update.append(", toDelDocs='").append(JDBCUtil.getByte(crs.getString("toDelDocs"))).append("'");
				update.append(", toReview='").append(JDBCUtil.getByte(crs.getString("toReview"))).append("'");
				update.append(", toApproved='").append(JDBCUtil.getByte(crs.getString("toApproved"))).append("'");
				update.append(", toMoveDocs='").append(JDBCUtil.getByte(crs.getString("toMoveDocs"))).append("'");
				update.append(", toCheckOut='").append(JDBCUtil.getByte(crs.getString("toCheckOut"))).append("'");
				update.append(", toEditRegister='").append(JDBCUtil.getByte(crs.getString("toEditRegister"))).append("'");
				update.append(", toImpresion='").append(JDBCUtil.getByte(crs.getString("toImpresion"))).append("'");
				update.append(", toCheckTodos='").append(JDBCUtil.getByte(crs.getString("toCheckTodos"))).append("'");
				update.append(", toDoFlows='").append(JDBCUtil.getByte(crs.getString("toDoFlows"))).append("'");
				update.append(", docInline='").append(JDBCUtil.getByte(crs.getString("docInline"))).append("'");
				update.append(", toFlexFlow='").append(JDBCUtil.getByte(crs.getString("toFlexFlow"))).append("'");
				update.append(", toChangeUsr='").append(JDBCUtil.getByte(crs.getString("toChangeUsr"))).append("'");
				update.append(", toCompleteFlow='").append(JDBCUtil.getByte(crs.getString("toCompleteFlow"))).append("'");
				update.append(", permisoModificado=").append(crs.getInt("idNode"));
				update.append(", toPublicEraser='").append(JDBCUtil.getByte(crs.getString("toPublicEraser"))).append("'");
				update.append(", toDownload='").append(JDBCUtil.getByte(crs.getString("toDownload"))).append("'");
				update.append(" WHERE idGroup = ").append(forma.getIdGroup());
				update.append(" AND permisoModificado=").append(forma.getIdStruct());
				update.append(" AND idStruct IN (").append(forma.getIdStruct());
				if (!ToolsHTML.isEmptyOrNull(hijos)) {
					update.append(",").append(hijos);// Si el Nodo Tiene Hijos se Concatenan los Mismos...
				}
				update.append(")");

				JDBCUtil.executeUpdate(update);

			} else { // en caso contrario eliminamos el grupo de los permisos otorgados

				update.setLength(0);
				update.append(" DELETE FROM permissionstructgroups WHERE idGroup = '").append(forma.getIdGroup()).append("'");
				update.append(" AND permisoModificado=").append(forma.getIdStruct());
				update.append(" AND idStruct IN (").append(forma.getIdStruct());
				if (!ToolsHTML.isEmptyOrNull(hijos)) {// Si el Nodo Tiene Hijos se Concatenan los Mismos...
					update.append(",").append(hijos);
				}
				update.append(") ");

				JDBCUtil.executeUpdate(update);

			}
			resp = true;
		} catch (Exception ex) {
			ex.printStackTrace();
			// applyRollback(con);
			setMensaje(ex.getMessage());
			resp = false;
		} finally {
			// setFinally(con, st);
		}

		// Hashtable childs = new Hashtable();
		// if (isFolderOrProcess) {
		// childs = HandlerStruct.getStructChilds(forma.getIdStruct(),forma.getIdGroup(),false,true);
		// }
		// if (isSite) {
		// childs = HandlerStruct.getStructChilds(forma.getIdStruct(),forma.getIdGroup(),false,false);
		// }
		// Connection con = null;
		// boolean resp = false;
		// PreparedStatement st = null;
		// if (checkExistSecurity(forma.getIdStruct(),forma.getIdGroup())) {
		// try {
		// con = JDBCUtil.getConnection(Thread.currentThread().getStackTrace()[1].getMethodName());
		// con.setAutoCommit(false);
		// //primero que nada actualizamos los documentos del nodo id en que estamos parados
		// HandlerDBUser.checkExistSecurityDeleteDocsGrup(forma.getIdStruct(),forma.getIdGroup(),forma,st,con);
		// // Se procede a actualizar la seguridad en los nodos hijos
		// if (childs!=null&&!childs.isEmpty()) {
		// deleteTableSecurity(forma.getIdStruct(),childs,forma,st,con);
		// }
		// st = con.prepareStatement(getQueryDeleteSecurity(forma,forma.getIdStruct()));
		// st.executeUpdate();
		// con.commit();
		// resp = true;
		// } catch (Exception ex) {
		// log.error(ex.getMessage());
		// ex.printStackTrace();
		// applyRollback(con);
		// resp = false;
		// } finally {
		// setFinally(con,st);
		// }
		return resp;
	}

	public static synchronized boolean updateSecurityStructGroup(PermissionUserForm forma, boolean isFolderOrProcess, boolean isSite) throws Exception {
		Hashtable childs = new Hashtable();
		if (isFolderOrProcess) {
			childs = HandlerStruct.getStructChilds(forma.getIdStruct(), forma.getIdGroup(), false, true);
		}
		if (isSite) {
			childs = HandlerStruct.getStructChilds(forma.getIdStruct(), forma.getIdGroup(), false, false);
		}
		Connection con = null;
		boolean resp = false;
		PreparedStatement st = null;
		if (checkExistSecurity(forma.getIdStruct(), forma.getIdGroup())) {
			try {
				con = JDBCUtil.getConnection(Thread.currentThread().getStackTrace()[1].getMethodName());
				con.setAutoCommit(false);
				st = con.prepareStatement(JDBCUtil.replaceCastMysql(getQueryUpdateSecurity(forma, forma.getIdStruct())));
				st.executeUpdate();
				// primero que nada actualizamos los documentos del nodo id en
				// que estamos parados
				HandlerDBUser.checkExistSecurityDocsGrup(forma.getIdStruct(), forma.getIdGroup(), forma, st, con);
				// Se procede a actualizar la seguridad en los nodos hijos
				if (childs != null && !childs.isEmpty()) {
					updateTableSecurity(forma.getIdStruct(), childs, forma, st, con);
				}
				// primero que nada actualizamos los documentos del nodo id en
				// que estamos parados
				// HandlerDBUser.checkExistSecurityDocsGrup(forma.getIdStruct(),forma.getIdGroup(),forma,st,con);
				con.commit();
				resp = true;
			} catch (Exception ex) {
				log.error(ex.getMessage());
				ex.printStackTrace();
				applyRollback(con);
				resp = false;
			} finally {
				setFinally(con, st);
			}
		} else {
			try {
				con = JDBCUtil.getConnection(Thread.currentThread().getStackTrace()[1].getMethodName());
				con.setAutoCommit(false);
				insertSecurityStructGroup(forma, forma.getIdStruct(), con);
				// primero que nada actualizamos los documentos del nodo id en
				// que estamos parados
				HandlerDBUser.checkExistSecurityDocsGrup(forma.getIdStruct(), forma.getIdGroup(), forma, st, con);
				// Se procede a actualizar la seguridad en los nodos hijos
				if (childs != null && !childs.isEmpty()) {
					updateTableSecurity(forma.getIdStruct(), childs, forma, st, con);
				}
				con.commit();
				resp = true;
			} catch (Exception ex) {
				ex.printStackTrace();
				applyRollback(con);
				resp = false;
			} finally {
				setFinally(con, st);
			}
		}
		// } else {
		// try {
		// con = JDBCUtil.getConnection(Thread.currentThread().getStackTrace()[1].getMethodName());
		// con.setAutoCommit(false);
		// insertSecurityStructGroup(forma,forma.getIdStruct(),con);
		// //primero que nada actualizamos los documentos del nodo id en que
		// estamos parados
		// HandlerDBUser.checkExistSecurityDocsGrup(forma.getIdStruct(),forma.getIdGroup(),forma,st,con);
		// // Se procede a actualizar la seguridad en los nodos hijos
		// if (childs!=null&&!childs.isEmpty()) {
		// updateTableSecurity(forma.getIdStruct(),childs,forma,st,con);
		// }
		// con.commit();
		// resp = true;
		// } catch (Exception ex) {
		// ex.printStackTrace();
		// applyRollback(con);
		// resp = false;
		// } finally {
		// setFinally(con,st);
		// }
		// }
		return resp;
	}

	public static synchronized boolean updateSecurityStructGroup(PermissionUserForm forma, String nodeType) throws Exception {
		Hashtable childs = new Hashtable();
		log.debug("[updateSecurityStructGroup] idStruct = " + forma.getIdStruct());
		boolean isFolderOrProcess = DesigeConf.getProperty("processType").equalsIgnoreCase(nodeType)
				|| DesigeConf.getProperty("folderType").equalsIgnoreCase(nodeType);
		// boolean isSite =
		// DesigeConf.getProperty("siteType").equalsIgnoreCase(nodeType);
		boolean isLocationType = DesigeConf.getProperty("locationType").equalsIgnoreCase(nodeType);
		if (isFolderOrProcess || isLocationType) {
			childs = HandlerStruct.getStructChilds(forma.getIdStruct(), forma.getIdGroup(), false, true);
		}

		log.debug("[updateSecurityStructGroup] isFolderOrProcess = " + isFolderOrProcess);
		log.debug("[updateSecurityStructGroup] childs = " + childs);
		Connection con = null;
		boolean resp = false;
		PreparedStatement st = null;
		boolean haySeguridad = checkExistSecurity(forma.getIdStruct(), forma.getIdGroup());
		int anteriorPermisoModificado = 0;
		try {
			forma.setPermisoModificado(HandlerGrupo.checkModifySecurity(forma.getIdStruct(), forma.getIdGroup()));

			con = JDBCUtil.getConnection(Thread.currentThread().getStackTrace()[1].getMethodName());
			con.setAutoCommit(false);

			anteriorPermisoModificado = forma.getPermisoModificado();
			forma.setPermisoModificado(Integer.parseInt(forma.getIdStruct())); // marcamos
																				// la
																				// seguridad
																				// para
																				// indicar
																				// que
																				// fue
																				// modificada
			if (haySeguridad) {
				st = con.prepareStatement(JDBCUtil.replaceCastMysql(getQueryUpdateSecurity(forma, forma.getIdStruct())));
				st.executeUpdate();
			} else {
				insertSecurityStructGroup(forma, forma.getIdStruct(), con);
			}

			
			// primero que nada actualizamos los documentos del nodo id en que estamos parados
			//HandlerDBUser.checkExistSecurityDocsGrup(forma.getIdStruct(), forma.getIdGroup(), forma, st, con); //Este proceso no hace mas que una consulta - INUTILIZADO JAIRO

			// OPTIMIZAR
			// Se procede a actualizar la seguridad en los nodos hijos
			if (childs != null && !childs.isEmpty()) { // la seguridad se debe buscar en el padre
				forma.setPermisoModificado(anteriorPermisoModificado); // marcamos la seguridad para indicar que fue modificada
				updateTableSecurity(forma.getIdStruct(), childs, forma, st, con);
			}

			// NO SE DEBE ACTUALIZAR LOS PERMISOS DE DOCUMENTOS // primero que
			// nada actualizamos los documentos del nodo id en que estamos
			// parados
			// HandlerDBUser.checkExistSecurityDocsGrup(forma.getIdStruct(),forma.getIdGroup(),forma,st,con);

			con.commit();
			resp = true;
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

	public synchronized static boolean insertSecurityStructGroup(PermissionUserForm forma, String idStruct, Connection conParam) throws Exception {
		StringBuilder update = new StringBuilder(100);
		update.append("INSERT INTO permissionstructgroups (idStruct,idGroup,toView,toRead,toAddFolder,toAddProcess,toDelete,");
		update.append("toEdit,toMove,toAddDocument,toAdmon,toViewDocs,toEditDocs,toAdmonDocs,toDelDocs");
		update.append(",toReview,toApproved,toMoveDocs,toCheckOut,toEditRegister,toImpresion,toCheckTodos,toDoFlows,docinLine,");
		update.append("toFlexFlow,toChangeUsr,toCompleteFlow,permisoModificado,toPublicEraser,toDownload) VALUES(");
		update.append(idStruct).append(",'").append(forma.getIdGroup()).append("','");
		update.append(forma.getToView()).append("','");
		update.append(forma.getToRead()).append("','").append(forma.getToAddFolder()).append("','");
		update.append(forma.getToAddProcess()).append("','").append(forma.getToDelete()).append("','");
		update.append(forma.getToEdit()).append("','").append(forma.getToMove()).append("','");
		update.append(forma.getToAddDocument()).append("','").append(forma.getToAdmon()).append("','");
		update.append(forma.getToViewDocs()).append("','").append(forma.getToEditDocs()).append("','");
		update.append(forma.getToAdmonDocs()).append("','").append(forma.getToDelDocs()).append("','");
		update.append(forma.getToReview()).append("','").append(forma.getToAprove()).append("','");
		// update.append(forma.getToMoveDocs()).append("','").append(forma.getCheckOut()).append("','").append(forma.getToEditRegister()).append("','").append(forma.getToImpresion()).append(")");
		update.append(forma.getToMoveDocs()).append("','").append(forma.getCheckOut()).append("','").append(forma.getToEditRegister()).append("','");
		update.append(forma.getToImpresion()).append("','").append(forma.getToCheckTodos()).append("','");
		update.append(forma.getToDoFlows()).append("','").append(forma.getToDocinLine()).append("','");
		update.append(forma.getToFlexFlow()).append("','").append(forma.getToChangeUsr()).append("','");
		update.append(forma.getToCompleteFlow()).append("','").append(forma.getPermisoModificado()).append("','");
		update.append(forma.getToPublicEraser()).append("','");
		update.append(forma.getToDownload()).append("'");
		update.append(")");
		Connection con = null;
		PreparedStatement st = null;
		int result = 0;
		con = conParam != null ? conParam : JDBCUtil.getConnection(Thread.currentThread().getStackTrace()[1].getMethodName());
		st = con.prepareStatement(JDBCUtil.replaceCastMysql(update.toString()));
		result = st.executeUpdate();
		if (conParam == null) {
			setFinally(con, st);
		}
		return result > 0;
	}

	public static Hashtable getAllSecurityForGroup(String idGroup, StringBuffer ids) throws Exception {
		StringBuilder sql = new StringBuilder(2048).append("SELECT * FROM permissionstructgroups");
		if (idGroup != null) {
			sql.append(" WHERE idGroup = ").append(idGroup);
			sql.append(" AND active = '").append(Constants.permission).append("'");
		} else {
			sql.append(" WHERE active = '").append(Constants.permission).append("'");
		}
		sql.append(" AND toView = '").append(Constants.permission).append("'").append(" ORDER BY idStruct ");
		Hashtable result = new Hashtable();
		Vector datos = JDBCUtil.doQueryVector(sql.toString(),Thread.currentThread().getStackTrace()[1].getMethodName());
		PermissionUserForm forma = null;
		for (int row = 0; row < datos.size(); row++) {
			Properties properties = (Properties) datos.elementAt(row);
			forma = new PermissionUserForm(properties.getProperty("idStruct"));
			forma.setIdUser(idGroup);
			forma.setIdGroup(Long.parseLong(idGroup));
			setSecurityInNode(properties, forma);
			result.put(forma.getIdStruct(), forma);
			ids.append(",").append(forma.getIdStruct());
		}

		return result;
	}

	/**
	 * 
	 * @param idNode
	 * @return
	 */
	public static Hashtable getAllGroupsToStruct(String idNode) {
		Hashtable groups = new Hashtable();
		try {
			StringBuilder query = new StringBuilder(2048).append("SELECT psg.*,g.nombreGrupo FROM groupusers g,permissionstructgroups psg")
					.append(" WHERE accountActive = '1' AND g.idGrupo = psg.idGroup").append(" AND psg.idStruct = ").append(idNode);
			log.debug("[getAllGroupsToStruct] = " + query);
			Vector users = JDBCUtil.doQueryVector(query.toString(),Thread.currentThread().getStackTrace()[1].getMethodName());
			PermissionUserForm forma = null;
			for (int i = 0; i < users.size(); i++) {
				Properties properties = (Properties) users.elementAt(i);
				forma = new PermissionUserForm(properties.getProperty("idStruct"));
				String idGroup = properties.getProperty("idGroup");
				forma.setIdGroup(Long.parseLong(idGroup));
				forma.setName(properties.getProperty("nombreGrupo"));
				setSecurityInNode(properties, forma);
				groups.put(String.valueOf(forma.getIdGroup()), forma);
			}
		} catch (Exception ex) {
			log.error(ex.getMessage());
			ex.printStackTrace();
		}
		return groups;
	}

	/**
	 * 
	 * @param idNode
	 * @param isFlexFlow
	 * @return
	 */
	public static Collection getAllGroupsWithPerm(String idNode, boolean isFlexFlow) {
		Vector result = new Vector();
		try {
			StringBuilder sql = new StringBuilder(2048).append("SELECT g.idGrupo,nombreGrupo,idGrupo,psg.*")
					.append("  FROM permissionstructgroups psg,groupusers g").append(" WHERE g.idGrupo = psg.idGroup").append("   AND accountActive = '")
					.append(Constants.permission).append("'").append("   AND psg.idStruct = ").append(idNode);
			Vector users = JDBCUtil.doQueryVector(sql.toString(),Thread.currentThread().getStackTrace()[1].getMethodName());
			PermissionUserForm forma = null;
			for (int i = 0; i < users.size(); i++) {
				Properties properties = (Properties) users.elementAt(i);
				forma = new PermissionUserForm(properties.getProperty("idStruct"));
				String idGroup = properties.getProperty("idGroup");
				forma.setIdGroup(Long.parseLong(idGroup));
				forma.setName(properties.getProperty("nombreGrupo"));
				setSecurityInNode(properties, forma);
				// Para Crear Flujos de Trabajo
				String toDoFlows = properties.getProperty("toDoFlows");
				if (!ToolsHTML.isEmptyOrNull(toDoFlows)) {
					forma.setToDoFlows(Byte.parseByte(toDoFlows));
				}
				// Para Crear Flujos de Trabajo Parametrico
				String docInline = properties.getProperty("toFlexFlow");
				if (!ToolsHTML.isEmptyOrNull(docInline)) {
					forma.setToFlexFlow(Byte.parseByte(docInline));
				}
				// Para Cambiar los Usuarios sugeridos en los Flujos de Trabajo
				// Paramétricos
				docInline = properties.getProperty("toChangeUsr");
				if (!ToolsHTML.isEmptyOrNull(docInline)) {
					forma.setToChangeUsr(Byte.parseByte(docInline));
				}
				// Para completar flujos de trabajos parametricos
				docInline = properties.getProperty("toCompleteFlow");
				if (!ToolsHTML.isEmptyOrNull(docInline)) {
					forma.setToCompleteFlow(Byte.parseByte(docInline));
				}
				// Para publicar borradores
				docInline = properties.getProperty("toPublicEraser");
				if (!ToolsHTML.isEmptyOrNull(docInline)) {
					forma.setToPublicEraser(Byte.parseByte(docInline));
				}

				if (isFlexFlow) {
					if (forma.getToFlexFlow() == Constants.permission) {
						Search bean = new Search(idGroup, forma.getName());
						result.add(bean);
					}
				} else {
					if (forma.getToDoFlows() == Constants.permission) {
						Search bean = new Search(idGroup, forma.getName());
						bean.setAditionalInfo("");
						result.add(bean);
					}
				}
			}
		} catch (Exception ex) {
			log.equals(ex);
		}
		return result;
	}

	/**
	 * 
	 * @param idNode
	 * @param idDocumento
	 * @param typeWF
	 * @param isFlexFlow
	 * @return
	 */
	public static Collection getAllGroupsToDoc(String idNode, String idDocumento, String typeWF, boolean isFlexFlow) {
		Hashtable pstruct = getAllGroupsToStruct(idNode);
		try {
			StringBuilder query = new StringBuilder(2048).append(" SELECT psg.*,g.nombreGrupo FROM groupusers g,permisiondocgroup psg")
					.append(" WHERE accountActive = '1' AND g.idGrupo = cast(psg.idGroup as int)").append(" AND psg.idDocument = ").append(idDocumento);
			log.debug("[getAllGroupsToDoc] = " + query);
			Vector users = JDBCUtil.doQueryVector(query.toString(),Thread.currentThread().getStackTrace()[1].getMethodName());
			for (int i = 0; i < users.size(); i++) {
				Properties properties = (Properties) users.elementAt(i);
				PermissionUserForm forma = new PermissionUserForm(properties.getProperty("idStruct"));
				String idGroup = properties.getProperty("idGroup");
				forma.setIdGroup(Long.parseLong(idGroup));
				forma.setName(properties.getProperty("nombreGrupo"));
				setSecurityInNode(properties, forma);
				pstruct.put(String.valueOf(forma.getIdGroup()), forma);
			}
		} catch (Exception ex) {
			log.error(ex.getMessage());
			ex.printStackTrace();
		}

		Vector resp = new Vector();
		for (Enumeration nameUsers = pstruct.keys(); nameUsers.hasMoreElements();) {
			String userName = (String) nameUsers.nextElement();
			PermissionUserForm forma = (PermissionUserForm) pstruct.get(userName);
			log.debug("forma.getToAprove() = " + forma.getToAprove() + "_" + forma.getIdGroup());
			if ("1".equalsIgnoreCase(typeWF)) {
				if (forma.getToAprove() == Constants.permission) {
					Search bean = new Search(userName, forma.getName());
					resp.add(bean);
				}
			} else {
				// if (forma.getToReview() == Constants.permission) {
				if (forma.getToDoFlows() == Constants.permission) {
					Search bean = new Search(userName, forma.getName());
					resp.add(bean);
				}
			}

			// if (forma.getToAprove()==Constants.permission) {
			// Search bean = new Search(userName,forma.getName());
			// resp.add(bean);
			// }
		}

		return resp;
	}

	/**
	 * 
	 * @param idNode
	 * @param isFlexFlow
	 * @return
	 */
	public static Hashtable loadAllUsersWithPermission(String idNode, boolean isFlexFlow) {
		StringBuilder query = new StringBuilder(2048)
		// Carga de Todos los Usuarios que tienen permiso en la estructura dada
				.append("SELECT p.idPerson,nameUser,idGrupo,");
		switch (Constants.MANEJADOR_ACTUAL) {
		case Constants.MANEJADOR_MSSQL:
			query.append("(p.Apellidos + ' ' + p.Nombres) AS nombre,");
			break;
		case Constants.MANEJADOR_POSTGRES:
			query.append("(p.Apellidos || ' ' || p.Nombres) AS nombre,");
			break;
		case Constants.MANEJADOR_MYSQL:
			query.append("CONCAT(p.Apellidos , ' ' , p.Nombres) AS nombre,");
			break;
		}
		query.append("c.cargo,psg.* ").append(" FROM person p,tbl_cargo c,tbl_area a,permissionstructgroups psg WHERE p.accountActive = '1' ")
				.append(" AND p.idGrupo = psg.idGroup AND psg.idStruct = ").append(idNode);
		if (!isFlexFlow) {
			query.append(" AND p.nameUser!='").append(Constants.ID_USER_TEMPORAL).append("'");
		}
		query.append(" AND c.idcargo=cast(p.cargo as int) and c.idarea=a.idarea").append(" ORDER BY lower(p.Apellidos) asc, lower(p.Nombres) asc");
		// query.append(" ORDER BY nombre asc");
		Hashtable usuarios = new Hashtable();
		try {
			log.debug("[loadAllUsersWithPermission] = " + query);
			Vector users = JDBCUtil.doQueryVector(query.toString(),Thread.currentThread().getStackTrace()[1].getMethodName());
			PermissionUserForm forma = null;
			for (int i = 0; i < users.size(); i++) {
				Properties properties = (Properties) users.elementAt(i);
				forma = new PermissionUserForm(properties.getProperty("idStruct"));
				String idGroup = properties.getProperty("idGroup");
				forma.setIdUser(properties.getProperty("idPerson"));
				forma.setNameUser(properties.getProperty("nameUser"));
				forma.setIdGroup(Long.parseLong(idGroup));
				forma.setName(properties.getProperty("nombre"));
				forma.setCargo(properties.getProperty("cargo"));
				setSecurityInNode(properties, forma);
				usuarios.put(forma.getNameUser(), forma);
			}
		} catch (Exception ex) {
			log.error(ex.getMessage());
			ex.printStackTrace();
		}
		return usuarios;
	}

	/**
	 * 
	 * @param idNode
	 * @param secInGroup
	 */
	public static void loadAllUsersWithPermission(String idNode, Hashtable secInGroup) {
		try {
			StringBuilder query = new StringBuilder(2048)
			// Carga de Todos los Usuarios que tienen permiso en la estructura
			// dada
					.append("SELECT nameUser,idGrupo,");
			switch (Constants.MANEJADOR_ACTUAL) {
			case Constants.MANEJADOR_MSSQL:
				query.append("(p.Apellidos + ' ' + p.Nombres) AS nombre,");
				break;
			case Constants.MANEJADOR_POSTGRES:
				query.append("(p.Apellidos || ' ' || p.Nombres) AS nombre,");
				break;
			case Constants.MANEJADOR_MYSQL:
				query.append("CONCAT(p.Apellidos , ' ' , p.Nombres) AS nombre,");
				break;
			}
			query.append("c.cargo,psg.* ").append(" FROM person p,tbl_cargo c,tbl_area a,permissionstructuser psg WHERE p.accountActive = '1'")
					.append("  AND  p.idPerson = psg.idPerson AND psg.idStruct = ").append(idNode)
					.append(" AND c.idcargo=cast(p.cargo as int) and c.idarea=a.idarea").append(" ORDER BY lower(p.Apellidos) asc, lower(p.Nombres) asc");
			// query.append(" ORDER BY nombre asc");
			log.debug("[loadAllUsersWithPermission] query = " + query);
			Vector users = JDBCUtil.doQueryVector(query.toString(),Thread.currentThread().getStackTrace()[1].getMethodName());
			for (int i = 0; i < users.size(); i++) {
				Properties properties = (Properties) users.elementAt(i);
				PermissionUserForm forma = new PermissionUserForm(properties.getProperty("idStruct"));
				String idGroup = properties.getProperty("idGrupo");
				forma.setIdUser(properties.getProperty("idPerson"));
				forma.setNameUser(properties.getProperty("nameUser"));
				forma.setName(properties.getProperty("nombre"));
				forma.setIdGroup(Long.parseLong(idGroup));
				forma.setCargo(properties.getProperty("cargo"));
				setSecurityInNode(properties, forma);
				secInGroup.put(forma.getNameUser(), forma);
			}
		} catch (Exception ex) {
			log.error(ex.getMessage());
			ex.printStackTrace();
		}
	}

	/**
	 * 
	 * @param idDocument
	 * @param secInGroup
	 */
	public static void loadAllGroupWithPermissionDoc(String idDocument, Hashtable secInGroup) {
		try {
			StringBuilder query = new StringBuilder(1024)
			// Carga de Todos los Usuarios que tienen permiso en la estructura
			// dada
					.append("SELECT p.idPerson,nameUser,idGrupo,");
			switch (Constants.MANEJADOR_ACTUAL) {
			case Constants.MANEJADOR_MSSQL:
				query.append("(p.Apellidos + ' ' + p.Nombres) AS nombre,");
				break;
			case Constants.MANEJADOR_POSTGRES:
				query.append("(p.Apellidos || ' ' || p.Nombres) AS nombre,");
				break;
			case Constants.MANEJADOR_MYSQL:
				query.append("CONCAT(p.Apellidos , ' ' , p.Nombres) AS nombre,");
				break;
			}
			query.append("c.cargo,psg.* ").append(" FROM person p,tbl_cargo c,tbl_area a,permisiondocgroup psg WHERE accountActive = '1' ")
					.append(" AND  p.idGrupo = psg.idGroup AND  psg.idDocument = ").append(idDocument)
					.append(" AND c.idcargo=cast(p.cargo as int) and c.idarea=a.idarea");
			log.debug("[loadAllGroupWithPermissionDoc] = " + query);
			Vector users = JDBCUtil.doQueryVector(query.toString(),Thread.currentThread().getStackTrace()[1].getMethodName());
			for (int i = 0; i < users.size(); i++) {
				Properties properties = (Properties) users.elementAt(i);
				PermissionUserForm forma = new PermissionUserForm(properties.getProperty("idStruct"));
				String idGroup = properties.getProperty("idGrupo");
				forma.setIdUser(properties.getProperty("idPerson"));
				forma.setNameUser(properties.getProperty("nameUser"));
				forma.setIdGroup(Long.parseLong(idGroup));
				forma.setName(properties.getProperty("nombre"));
				forma.setCargo(properties.getProperty("cargo"));
				setSecurityInNode(properties, forma);
				secInGroup.put(forma.getNameUser(), forma);
			}
		} catch (Exception ex) {
			log.error(ex.getMessage());
			ex.printStackTrace();
		}
	}

	public static void loadAllUsersWithPermissionDoc(Hashtable secInGroup, String idDocument) {
		StringBuilder query = new StringBuilder(50);
		// Carga de Todos los Usuarios que tienen permiso en la estructura dada
		query.append("SELECT nameUser,idGrupo,");
		switch (Constants.MANEJADOR_ACTUAL) {
		case Constants.MANEJADOR_MSSQL:
			query.append("(p.Apellidos + ' ' + p.Nombres) AS nombre,");
			break;
		case Constants.MANEJADOR_POSTGRES:
			query.append("(p.Apellidos || ' ' || p.Nombres) AS nombre,");
			break;
		case Constants.MANEJADOR_MYSQL:
			query.append("CONCAT(p.Apellidos , ' ' , p.Nombres) AS nombre,");
			break;
		}
		query.append("c.cargo,psg.*");
		query.append(" FROM person p,tbl_cargo c,tbl_area a,permisiondocuser psg WHERE accountActive = '1' ");
		query.append("  AND p.nameUser = psg.idUser AND psg.idDocument = ").append(idDocument);
		query.append(" AND c.idcargo=cast(p.cargo as int) and c.idarea=a.idarea");
		try {
			log.debug("[loadAllUsersWithPermissionDoc] = " + query);
			Vector users = JDBCUtil.doQueryVector(query.toString(),Thread.currentThread().getStackTrace()[1].getMethodName());
			PermissionUserForm forma = null;
			for (int i = 0; i < users.size(); i++) {
				Properties properties = (Properties) users.elementAt(i);
				forma = new PermissionUserForm(properties.getProperty("idStruct"));
				String idGroup = properties.getProperty("idGrupo");
				forma.setIdUser(properties.getProperty("idPerson"));
				forma.setNameUser(properties.getProperty("nameUser"));
				forma.setIdGroup(Long.parseLong(idGroup));
				forma.setName(properties.getProperty("nombre"));
				forma.setCargo(properties.getProperty("cargo"));
				setSecurityInNode(properties, forma);
				secInGroup.put(forma.getNameUser(), forma);
			}
		} catch (Exception ex) {
			log.error(ex.getMessage());
			ex.printStackTrace();
		}
	}

	/**
	 * 
	 * @param idNode
	 * @param isFlexFlow
	 * @param typeWF
	 * @return
	 */
	public static Collection getSecurityForNode(String idNode, boolean isFlexFlow, String typeWF) {
		Hashtable usuarios = loadAllUsersWithPermission(idNode, isFlexFlow);
		loadAllUsersWithPermission(idNode, usuarios);
		HandlerDBUser.getAllUserInGroup(DesigeConf.getProperty("application.admon"), usuarios);

		// solo a nivel standar de seguridad se va a permitir enviar flujos ,..
		// el nivel de seguridad en documentos se ignora
		// loadAllGroupWithPermissionDoc(idDocument,usuarios);
		// loadAllUsersWithPermissionDoc(usuarios,idDocument);
		Vector resp = new Vector();
		for (Enumeration nameUsers = usuarios.keys(); nameUsers.hasMoreElements();) {
			String userName = (String) nameUsers.nextElement();
			PermissionUserForm forma = (PermissionUserForm) usuarios.get(userName);
			if (isFlexFlow) {
				// if (forma.getToFlexFlow() == Constants.permission) {
				if (forma.getToDoFlows() == Constants.permission) {
					Search bean = new Search(userName, forma.getName());
					bean.setAditionalInfo(forma.getCargo());
					resp.add(bean);
				}
			} else {
				if ("1".equalsIgnoreCase(typeWF)) {
					if (forma.getToDoFlows() == Constants.permission) {
						Search bean = new Search(userName, forma.getName());
						bean.setAditionalInfo(forma.getCargo());
						resp.add(bean);
					}
				} else {
					if (forma.getToDoFlows() == Constants.permission) {
						Search bean = new Search(userName, forma.getName());
						bean.setAditionalInfo(forma.getCargo());
						resp.add(bean);
					}
				}
			}
		}

		Vector resp2 = new Vector(0);
		List usuariosList = new ArrayList(resp);
		Collections.sort(usuariosList, new DescriptComparator());
		for (Iterator i = usuariosList.iterator(); i.hasNext();) {
			Search usr = (Search) i.next();
			resp2.add(usr);
		}

		return resp2;
	}

	/**
	 * 
	 * @param isFlexFlow
	 * @param typeWF
	 * @return
	 */
	public static Collection getUsersViewers(boolean isFlexFlow, String typeWF) {
		Hashtable usuarios = new Hashtable();
		HandlerDBUser.getAllUserInGroup(Constants.ID_GROUP_VIEWER, usuarios);

		Vector resp = new Vector();
		Enumeration nameUsers = usuarios.keys();
		PermissionUserForm forma = null;
		while (nameUsers.hasMoreElements()) {
			String userName = (String) nameUsers.nextElement();
			forma = (PermissionUserForm) usuarios.get(userName);
			if (isFlexFlow) {
				// if (forma.getToFlexFlow() == Constants.permission) {
				if (forma.getToDoFlows() == Constants.permission) {
					Search bean = new Search(userName, forma.getName());
					bean.setAditionalInfo(forma.getCargo());
					resp.add(bean);
				}
			} else {
				if ("1".equalsIgnoreCase(typeWF)) {
					if (forma.getToDoFlows() == Constants.permission) {
						Search bean = new Search(userName, forma.getName());
						bean.setAditionalInfo(forma.getCargo());
						resp.add(bean);
					}
				} else {
					if (forma.getToDoFlows() == Constants.permission) {
						Search bean = new Search(userName, forma.getName());
						bean.setAditionalInfo(forma.getCargo());
						resp.add(bean);
					}
				}
			}
		}

		Vector resp2 = new Vector(0);
		List usuariosList = new ArrayList(resp);
		Collections.sort(usuariosList, new DescriptComparator());
		for (Iterator i = usuariosList.iterator(); i.hasNext();) {
			Search usr = (Search) i.next();
			resp2.add(usr);
		}

		return resp2;
	}

	/**
	 * 
	 * @param idGroup
	 * @param idDocs
	 * @return
	 */
	public static Hashtable getSecurityForGroupInDoc(String idGroup, String idDocs) {
		Hashtable result = new Hashtable();
		try {
			StringBuilder sql = new StringBuilder(1024).append("SELECT toViewDocs,toImpresion,idDocument").append(" FROM permisiondocgroup")
					.append(" WHERE idGroup = '").append(idGroup).append("'").append(" AND idDocument IN (").append(idDocs).append(")");
			log.debug("[getSecurityForGroupInDoc] " + sql);
			Vector datos = JDBCUtil.doQueryVector(sql.toString(),Thread.currentThread().getStackTrace()[1].getMethodName());
			for (int i = 0; i < datos.size(); i++) {
				Properties properties = (Properties) datos.elementAt(i);
				PermissionUserForm perm = new PermissionUserForm();
				perm.setToViewDocs(Byte.parseByte(properties.getProperty("toViewDocs")));
				perm.setToImpresion(Byte.parseByte(properties.getProperty("toImpresion")));
				perm.setIdDocument(properties.getProperty("idDocument"));
				result.put(perm.getIdDocument(), perm);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return result;
	}

	/**
	 * 
	 * @param idGroup
	 * @param idDocs
	 * @return
	 */
	public static PermissionUserForm getSecurityForIDGroupInDoc(String idGroup, String idDocs) {
		PermissionUserForm perm = new PermissionUserForm();
		try {
			StringBuilder sql = new StringBuilder(1024)
					// sql.append("SELECT toViewDocs,toImpresion,idDocument");
					.append("SELECT * ").append(" FROM permisiondocgroup").append(" WHERE idGroup = '").append(idGroup).append("'")
					.append(" AND idDocument = ").append(idDocs);
			log.debug("[getSecurityForGroupInDoc] " + sql);
			Properties properties = JDBCUtil.doQueryOneRow(sql.toString(),Thread.currentThread().getStackTrace()[1].getMethodName());
			if (properties != null && properties.getProperty("isEmpty").equalsIgnoreCase("false")) {
				// perm.setToViewDocs(Byte.parseByte(properties.getProperty("toViewDocs")));
				// perm.setToImpresion(Byte.parseByte(properties.getProperty("toImpresion")));
				// perm.setIdDocument(properties.getProperty("idDocument"));
				HandlerDocuments.setDataSecurityDoc(perm, properties, null);
				return perm;
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}

	/**
	 * 
	 * @param con
	 * @param idDoc
	 * @throws SQLException
	 */
	public static synchronized void deleteSecurityDoc(Connection con, String idDoc) throws SQLException {
		StringBuilder sql = new StringBuilder(1024).append("DELETE FROM permisiondocuser WHERE idDocument = ").append(idDoc);
		PreparedStatement st = con.prepareStatement(JDBCUtil.replaceCastMysql(sql.toString()));
		st.executeUpdate();

		// Eliminando Seguridad para los Grupos sobre el Documento...
		sql.setLength(0);
		sql.append("DELETE FROM permisiondocgroup WHERE idDocument = ").append(idDoc);
		st = con.prepareStatement(JDBCUtil.replaceCastMysql(sql.toString()));
		st.executeUpdate();
	}

}
