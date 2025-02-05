package com.desige.webDocuments.persistent.managers;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Date;
import java.util.Properties;
import java.util.Vector;

import org.apache.commons.dbutils.DbUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.desige.webDocuments.document.forms.BaseDocumentForm;
import com.desige.webDocuments.grupo.forms.grupoForm;
import com.desige.webDocuments.persistent.utils.JDBCUtil;
import com.desige.webDocuments.seguridad.forms.SeguridadUserForm;
import com.desige.webDocuments.utils.ApplicationExceptionChecked;
import com.desige.webDocuments.utils.Constants;
import com.desige.webDocuments.utils.ToolsHTML;

import sun.jdbc.rowset.CachedRowSet;

/**
 * Title: HandlerSeguridad.java <br/>
 * Copyright: (c) 2004 Focus Consulting C.A.<br/>
 * 
 * @author Ing. Roger Rodríguez
 * @version WebDocuments v3.0 <br/>
 *          Changes:<br/>
 *          <ul>
 *          <li>09/08/2004 (RR) Creation</li>
 *          <li>21/04/2006 (NC) Add field search in the security profile user
 *          and group</li>
 *          </ul>
 */
public class HandlerSeguridad extends HandlerBD implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -432085435547221637L;

	private static final Logger log = LoggerFactory.getLogger(HandlerSeguridad.class
			.getName());

	private static final String tableGru = "seguridadgrupo";
	public static final String tableUser = "seguridaduser";
	public static final String tablePer = "person";
	public static final String tableGrupo = "groupusers";

	public synchronized static boolean insert1(SeguridadUserForm forma) {
		try {
			StringBuilder insert = new StringBuilder("INSERT INTO ")
					.append(tableGru);
			insert.append(" (idGrupo,estructura,flujos,administracion,perfil,mensajes,toimpresion,search,sacop,record,files,digital) VALUES(");
			insert.append(forma.getIdGrupo()).append(",")
					.append(forma.getEstructura()).append(",")
					.append(forma.getFlujos());
			insert.append(",").append(forma.getAdministracion()).append(",")
					.append(forma.getPerfil()).append(",")
					.append(forma.getMensajes());
			insert.append(",").append(forma.getToImpresion()).append(",'")
					.append(forma.getSearch()).append("'");
			insert.append(",'").append(forma.getSacop()).append("','")
					.append(forma.getRecord()).append("','")
					.append(forma.getFiles()).append("'");
			;
			insert.append(",'").append(forma.getDigital());
			insert.append("')");
			return JDBCUtil.doUpdate(insert.toString()) > 0;
		} catch (Exception e) {
			log.error(e.getMessage());
			setMensaje(e.getMessage());
			e.printStackTrace();
		}
		return false;
	}

	public synchronized static boolean edit1(SeguridadUserForm forma) {
		StringBuilder check = new StringBuilder("SELECT idGrupo FROM ")
				.append(tableGru);
		check.append(" WHERE idGrupo = ").append(forma.getIdGrupo());
		check.append(" AND active = '").append(Constants.permission)
				.append("'");

		StringBuilder edit = new StringBuilder("UPDATE ").append(tableGru);
		edit.append(" SET estructura = ").append(forma.getEstructura());
		edit.append(",administracion = ").append(forma.getAdministracion());
		edit.append(",perfil = ").append(forma.getPerfil());
		edit.append(",flujos = ").append(forma.getFlujos());
		edit.append(",mensajes = ").append(forma.getMensajes());
		edit.append(",toimpresion = ").append(forma.getToImpresion());
		edit.append(",search = '").append(forma.getSearch()).append("'");
		edit.append(",sacop = '").append(forma.getSacop()).append("'");
		edit.append(",record = '").append(forma.getRecord()).append("'");
		edit.append(",files = '").append(forma.getFiles()).append("'");
		edit.append(",digital = '").append(forma.getDigital()).append("'");
		edit.append(" WHERE idGrupo = ").append(forma.getIdGrupo());
		edit.append(" AND active = '").append(Constants.permission).append("'");
		log.debug("[edit1] = " + edit.toString());
		try {
			Properties prop = JDBCUtil.doQueryOneRow(check.toString(),Thread.currentThread().getStackTrace()[1].getMethodName());
			if (prop.getProperty("isEmpty").equalsIgnoreCase("false")) {
				return JDBCUtil.doUpdate(edit.toString()) > 0;
			} else {
				return insert1(forma);
			}
		} catch (Exception e) {
			log.error(e.getMessage());
			setMensaje(e.getMessage());
			e.printStackTrace();
		}
		return false;
	}

	public static boolean checkUserSecurity(String nameUser)
			throws ApplicationExceptionChecked {
		boolean result = false;
		StringBuilder sql = new StringBuilder(
				"SELECT idPerson FROM seguridaduser WHERE nameUser = '");
		sql.append(nameUser).append("' AND active = '")
				.append(Constants.permission).append("'");
		try {
			Properties prop = JDBCUtil.doQueryOneRow(sql.toString(),Thread.currentThread().getStackTrace()[1].getMethodName());
			if (prop.getProperty("isEmpty").equalsIgnoreCase("false")) {
				result = true;
			}
		} catch (Exception ex) {
			log.error(ex.getMessage());
			ex.printStackTrace();
			throw new ApplicationExceptionChecked("E0042");
		}

		return result;
	}

	public synchronized static void getQueryNegarUpdateActive(Connection con,
			PreparedStatement st, long idPerson, boolean active,
			long userremplazo, boolean swEliminar) {
		try {
			ResultSet rs = null;
			StringBuilder sql0 = new StringBuilder("");
			sql0.append("select estructura,flujos,administracion,perfil,mensajes,toImpresion,search,sacop,record,files,digital ");
			sql0.append(" from seguridaduser where idPerson = ").append(
					idPerson);
			st = con.prepareStatement(JDBCUtil.replaceCastMysql(sql0.toString()));
			rs = st.executeQuery();
			boolean swComa = false;
			StringBuilder sqlActDestino = new StringBuilder(
					"UPDATE seguridaduser set ");
			if (rs.next()) {
				if ("0".equalsIgnoreCase(rs.getString("estructura"))) {
					sqlActDestino.append(" estructura = 0");
					swComa = true;
				}
				if ("0".equalsIgnoreCase(rs.getString("flujos"))) {
					if (swComa) {
						sqlActDestino.append(",");
					}
					sqlActDestino.append(" flujos = 0");
					swComa = true;
				}
				if ("0".equalsIgnoreCase(rs.getString("administracion"))) {
					if (swComa) {
						sqlActDestino.append(",");
					}
					sqlActDestino.append(" administracion = 0");
					swComa = true;
				}
				if ("0".equalsIgnoreCase(rs.getString("perfil"))) {
					if (swComa) {
						sqlActDestino.append(",");
					}
					sqlActDestino.append(" perfil = 0");
					swComa = true;
				}
				if ("0".equalsIgnoreCase(rs.getString("mensajes"))) {
					if (swComa) {
						sqlActDestino.append(",");
					}
					sqlActDestino.append(" mensajes = 0");
					swComa = true;
				}
				if ("0".equalsIgnoreCase(rs.getString("toImpresion"))) {
					if (swComa) {
						sqlActDestino.append(",");
					}
					sqlActDestino.append(" toImpresion = 0");
					swComa = true;
				}
				if ("0".equalsIgnoreCase(rs.getString("search"))) {
					if (swComa) {
						sqlActDestino.append(",");
					}
					sqlActDestino.append(" search = 0");
					swComa = true;
				}
				if ("0".equalsIgnoreCase(rs.getString("sacop"))) {
					if (swComa) {
						sqlActDestino.append(",");
					}
					sqlActDestino.append(" sacop = 0");
					swComa = true;
				}
				if ("0".equalsIgnoreCase(rs.getString("record"))) {
					if (swComa) {
						sqlActDestino.append(",");
					}
					sqlActDestino.append(" record = 0");
					swComa = true;
				}
				if ("0".equalsIgnoreCase(rs.getString("files"))) {
					if (swComa) {
						sqlActDestino.append(",");
					}
					sqlActDestino.append(" files = 0");
					swComa = true;
				}
				if ("0".equalsIgnoreCase(rs.getString("digital"))) {
					if (swComa) {
						sqlActDestino.append(",");
					}
					sqlActDestino.append(" digital = 0");
					swComa = true;
				}
			}

			if (swComa) {
				sqlActDestino.append(" WHERE idPerson = ").append(userremplazo);
				// System.out.println("sqlActDestino="+sqlActDestino.toString());
				st = con.prepareStatement(JDBCUtil.replaceCastMysql(sqlActDestino.toString()));
				st.execute();
			}
			StringBuilder sql = new StringBuilder();
			if (swEliminar) {
				sql.append("delete from seguridaduser ");
				sql.append(" WHERE idPerson = ").append(idPerson);
			} else {
				sql.append("UPDATE seguridaduser SET estructura = 1 ");
				sql.append(",flujos=1");
				sql.append(",administracion=1");
				sql.append(",perfil=1");
				sql.append(",mensajes=1");
				sql.append(",toImpresion=1");
				sql.append(",search=1");
				sql.append(",sacop=1");
				sql.append(",record=1");
				sql.append(",files=1");
				sql.append(",digital=1");
				sql.append(" WHERE idPerson = ").append(idPerson);
			}
			// System.out.println("sql="+sql.toString());
			st = con.prepareStatement(JDBCUtil.replaceCastMysql(sql.toString()));
			st.execute();
		} catch (Exception e) {
		}
	}

	/**
	 * 
	 * @param idPerson
	 * @param active
	 * @return
	 */
	public synchronized static String getQueryUpdateActive(long idPerson,
			boolean active) {
		StringBuilder sql = new StringBuilder(
				"UPDATE seguridaduser SET active = CAST(")
				.append(active ? "1" : "0")
				.append(" as bit) WHERE idPerson = ").append(idPerson);
		return sql.toString();
	}

	/**
	 * 
	 * @param idGroup
	 * @param active
	 * @return
	 */
	public synchronized static String getQuerySeguridadGrupoActive(
			String idGroup, boolean active) {
		StringBuilder sql = new StringBuilder(
				"UPDATE seguridadgrupo SET active = CAST(")
				.append(active ? "1" : "0").append(" as bit) WHERE idGrupo = ")
				.append(idGroup);
		return JDBCUtil.replaceCastMysql(sql.toString());
	}

	/**
	 * 
	 * @param idoldOwner
	 * @param idnewUser
	 * @return
	 */
	public synchronized static String getActualizaAccionPorPersonaSacop(
			String idoldOwner, String idnewUser) {
		StringBuilder sql = new StringBuilder(
				"UPDATE tbl_sacopaccionporpersona SET idperson = ")
				.append(idnewUser).append(" WHERE idperson = ")
				.append(idoldOwner);
		// System.out.println("sql person="+sql.toString());
		return sql.toString();
	}

	/**
	 * 
	 * @param idPerson
	 * @param active
	 * @return
	 */
	public synchronized static String getQuerySecurityStructActive(
			long idPerson, boolean active) {
		StringBuilder sql = new StringBuilder(
				"UPDATE permissionstructuser SET active = CAST(")
				.append(active ? "1" : "0")
				.append(" as bit) WHERE idPerson = ").append(idPerson);
		return sql.toString();
	}

	/**
	 * 
	 * @param idPerson
	 * @param active
	 * @return
	 */
	public synchronized static String getQuerySecurityStructGroupActive(
			String idPerson, boolean active) {
		StringBuilder sql = new StringBuilder(
				"UPDATE permissionstructgroups SET active = CAST(")
				.append(active ? "1" : "0").append(" as bit) WHERE idGroup = ")
				.append(idPerson);
		return JDBCUtil.replaceCastMysql(sql.toString());
	}

	/**
	 * 
	 * @param idPerson
	 * @param iduseremplazo
	 * @param active
	 * @return
	 * @throws Exception 
	 */
	public synchronized static String getUpdateplanillaSacopAccionPorPersona(
			long idPerson, long iduseremplazo, boolean active) throws Exception {
		
		StringBuffer sql1 = new StringBuffer();
		sql1.append("select idplanillasacopaccion from tbl_sacopaccionporpersona where ");
		sql1.append(" idPerson=").append(iduseremplazo);

		StringBuilder updatesql = new StringBuilder(
				"update tbl_sacopaccionporpersona ")
				.append(" set idPerson=")
				.append(iduseremplazo)
				.append("")
				.append(" where idPerson = ")
				.append(idPerson)
				.append("")
				.append(" and firmo <> '")
				.append(Constants.permission)
				.append("'")
				.append(" and idplanillasacopaccion not in ")
				.append("(").append(JDBCUtil.executeQueryRetornaIds(sql1)).append(")");
		
		return updatesql.toString();
	}

	/**
	 * 
	 * @param oldName
	 * @param newName
	 * @return
	 */
	public synchronized static String getUpdateFilesUser(String oldName,
			String newName) {
		StringBuilder updatesql = new StringBuilder("update expediente ")
				.append(" set f3='").append(newName).append("' ")
				.append(" where f3='").append(oldName).append("' ");
		// System.out.println(" updatesql.toString() = " +
		// updatesql.toString());
		return updatesql.toString();
	}

	/**
	 * 
	 * @param oldName
	 * @param newName
	 * @return
	 */
	public synchronized static String getUpdateVersionUser(String oldName,
			String newName) {
		StringBuilder updatesql = new StringBuilder("update versiondoc ")
				.append(" set ownerVersion='").append(newName).append("' ")
				.append(" where ownerVersion='").append(oldName).append("' ");
		// System.out.println(" updatesql.toString() = " +
		// updatesql.toString());
		return updatesql.toString();
	}

	/**
	 * 
	 * @param oldIdUser
	 * @param newIdUser
	 * @return
	 */
	public synchronized static String getUpdatePrintFlow(long oldIdUser,
			long newIdUser) {
		StringBuilder updatesql = new StringBuilder(
				"update tbl_solicitudimpresion ").append(" set solicitante='")
				.append(newIdUser).append("' ").append(" where solicitante='")
				.append(oldIdUser).append("'  ")
				.append(" and active = '1' and statusimpresion = '")
				.append(BaseDocumentForm.statuApproved).append("' ");
		// System.out.println(" updatesql.toString() = " +
		// updatesql.toString());
		return updatesql.toString();
	}

	/**
	 * 
	 * @param oldName
	 * @param newName
	 * @return
	 * @throws Exception 
	 */
	public synchronized static String getUpdateCanceledFlow(String oldName,
			String newName) throws Exception {
		String dateSystem = ToolsHTML.sdf.format(new Date());

		StringBuffer sql1 = new StringBuffer();
		sql1.append(" SELECT w.idWorkFlow ");
		sql1.append(" FROM workflows w,documents d ");
		sql1.append(" WHERE d.active = '1'  ").append(" AND w.owner = '");
		sql1.append(oldName).append("'  ");
		sql1.append(" AND d.numGen = w.idDocument  ");
		sql1.append(" AND w.Statu  = '4'  ");
		switch (Constants.MANEJADOR_ACTUAL) {
		case Constants.MANEJADOR_MSSQL:
			sql1.append(" AND (dateExpire >= CONVERT(datetime,'")
					.append(dateSystem)
					.append("',120) OR dateExpire IS NULL) ");
			break;
		case Constants.MANEJADOR_POSTGRES:
			sql1.append(" AND (dateExpire >= CAST('").append(dateSystem)
					.append("' as timestamp) OR dateExpire IS NULL) ");
			break;
		case Constants.MANEJADOR_MYSQL:
			sql1.append(" AND (dateExpire >= TIMESTAMP('").append(dateSystem)
					.append("') OR dateExpire IS NULL ) ");
			break;
		}
		sql1.append(" AND w.type='1' ");
		sql1.append(" AND w.viewInCanceled = '1' ");

		
		StringBuilder updatesql = new StringBuilder("update workflows ");
		updatesql.append(" set owner='").append(newName).append("' ");
		updatesql.append(" WHERE idWorkFlow ");
		updatesql.append(" IN (").append(JDBCUtil.executeQueryRetornaIds(sql1)).append(")");

		return updatesql.toString();
	}

	/**
	 * 
	 * @param idPerson
	 * @param active
	 * @return
	 */
	public synchronized static String getQuerySecurityStructDelete(
			long idPerson, boolean active) {
		StringBuilder sql = new StringBuilder(
				"DELETE FROM permissionstructuser ").append(
				" WHERE idPerson = ").append(idPerson);
		return sql.toString();
	}

	public synchronized static String getQueryPerson(long idPerson,
			boolean active) {
		StringBuilder sql = new StringBuilder(
				"UPDATE person SET accountActive = CAST(")
				.append(active ? "1" : "0")
				.append(" as bit) WHERE idPerson = ").append(idPerson);
		log.debug("[getQueryPerson]" + sql);
		return sql.toString();
	}

	/**
	 * 
	 * @param forma
	 * @param con
	 * @return
	 * @throws ApplicationExceptionChecked
	 */
	public synchronized static boolean insertUserSecurity(
			SeguridadUserForm forma, Connection con)
			throws ApplicationExceptionChecked {
		PreparedStatement pst = null;
		boolean result = false;
		try {
			if (!checkUserSecurity(forma.getNameUser())) {
				StringBuilder insert = new StringBuilder("INSERT INTO ")
						.append(tableUser)
						.append(" (nameUser,estructura,flujos,administracion,perfil,mensajes,idPerson,toImpresion,search,sacop,record,files,digital) VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?) ");
				if (con == null) {
					con = JDBCUtil.getConnection(Thread.currentThread().getStackTrace()[1].getMethodName());
				}

				pst = con.prepareStatement(JDBCUtil.replaceCastMysql(insert.toString()));
				pst.setString(1, forma.getNameUser());
				pst.setInt(2, forma.getEstructura());
				pst.setInt(3, forma.getFlujos());
				pst.setInt(4, forma.getAdministracion());
				pst.setInt(5, forma.getPerfil());
				pst.setInt(6, forma.getMensajes());
				pst.setLong(7, forma.getIdPerson());
				pst.setInt(8, forma.getToImpresion());
				pst.setInt(9, forma.getSearch());
				pst.setInt(10, forma.getSacop());
				pst.setInt(11, forma.getRecord());
				pst.setInt(12, forma.getFiles());
				pst.setInt(13, forma.getDigital());

				log.debug("[insertUserSecurity]" + insert.toString());

				result = pst.executeUpdate() > 0;
			} else {
				result = save(forma);
			}
		} catch (Exception e) {
			log.error(e.getMessage());
			result = false;
			throw new ApplicationExceptionChecked("E0041");
		} finally {
			DbUtils.closeQuietly(pst);

		}
		return result;
	}

	public synchronized static boolean edit(SeguridadUserForm forma)
			throws Exception {
		return insertUserSecurity(forma, null);
		// StringBuilder sql = new StringBuilder(50);
		// sql.append("SELECT estructura FROM ").append(tableUser).append(" WHERE nameUser='");
		// sql.append("' AND active = ").append(Constants.permission);
		//
		// Properties prop = JDBCUtil.doQueryOneRow(sql.toString(),Thread.currentThread().getStackTrace()[1].getMethodName());
		// StringBuilder edit = new StringBuilder(50);
		// if ("false".equalsIgnoreCase(prop.getProperty("isEmpty"))) {
		// edit.append("UPDATE ").append(tableUser).append(" SET nameUser='");
		// edit.append(forma.getNameUser().trim());
		// edit.append("',estructura = ").append(forma.getEstructura());
		// edit.append(",administracion = ").append(forma.getAdministracion());
		// edit.append(",perfil = ").append(forma.getPerfil());
		// edit.append(",flujos = ").append(forma.getFlujos());
		// edit.append(",mensajes = ").append(forma.getMensajes());
		// edit.append(",toImpresion = ").append(forma.getToImpresion());
		// edit.append(",search = ").append(forma.getSearch());
		// edit.append(",sacop = ").append(forma.getSacop());
		// edit.append(",record = ").append(forma.getRecord());
		// edit.append(" WHERE nameUser = '").append(forma.getNameUser().trim()).append("'");
		// edit.append(" AND active = ").append(Constants.permission);
		// log.debug("edit.toString() = " + edit.toString());
		// try {
		// return JDBCUtil.doUpdate(edit.toString())>0;
		// } catch (Exception e) {
		// log.error(e.getMessage());
		// setMensaje(e.getMessage());
		// e.printStackTrace();
		// }
		// } else {
		// return insertUserSecurity(forma,null);
		// }
		// return false;
	}

	/**
	 * 
	 * @param seguridad
	 * @throws Exception
	 */
	public static void load1(SeguridadUserForm seguridad) throws Exception {
		StringBuilder sql = new StringBuilder("SELECT p.*,g.* FROM ")
				.append(tableGru).append(" p , groupusers g")
				.append(" WHERE p.idGrupo = g.idGrupo AND p.idGrupo = ")
				.append(seguridad.getIdGrupo().trim())
				.append(" AND p.active = '").append(Constants.permission)
				.append("'").append(" AND g.accountActive = '")
				.append(Constants.permission).append("'");
		// System.out.println("sql.toString() = " + sql.toString());
		Properties prop = JDBCUtil.doQueryOneRow(sql.toString(),Thread.currentThread().getStackTrace()[1].getMethodName());
		if ((prop != null)
				&& prop.getProperty("isEmpty").equalsIgnoreCase("false")) {
			seguridad.setDescripcionGrupo(prop.getProperty("descripcionGrupo"));
			seguridad.setNombreGrupo(prop.getProperty("nombreGrupo"));
			seguridad.setAdministracion(Byte.parseByte(prop
					.getProperty("administracion")));
			seguridad.setPerfil(Byte.parseByte(prop.getProperty("perfil")));
			seguridad.setFlujos(Byte.parseByte(prop.getProperty("flujos")));
			seguridad.setMensajes(Byte.parseByte(prop.getProperty("mensajes")));
			seguridad.setEstructura(Byte.parseByte(prop
					.getProperty("estructura")));
			seguridad.setToImpresion(Byte.parseByte(prop
					.getProperty("toImpresion")));
			seguridad.setSearch(Byte.parseByte(prop.getProperty("search")));
			seguridad.setSacop(Byte.parseByte(prop.getProperty("sacop")));
			seguridad
					.setRecord(ToolsHTML.isNumeric(prop.getProperty("record")) ? Byte
							.parseByte(prop.getProperty("record")) : 1);
			seguridad
					.setFiles(ToolsHTML.isNumeric(prop.getProperty("files")) ? Byte
							.parseByte(prop.getProperty("files")) : 1);
			seguridad.setDigital(ToolsHTML.isNumeric(prop
					.getProperty("digital")) ? Byte.parseByte(prop
					.getProperty("digital")) : 1);
		} else {
			grupoForm forma = new grupoForm();
			forma.setIdGrupo(seguridad.getIdGrupo());
			HandlerGrupo.load(forma);
			seguridad.setDescripcionGrupo(forma.getDescripcionGrupo());
			seguridad.setNombreGrupo(forma.getNombreGrupo());
			seguridad.setAdministracion(1);
			seguridad.setPerfil(1);
			seguridad.setFlujos(1);
			seguridad.setMensajes(1);
			seguridad.setEstructura(1);
			seguridad.setToImpresion(1);
			seguridad.setSearch(1);
			seguridad.setSacop(1);
			seguridad.setRecord(1);
			seguridad.setFiles(1);
			seguridad.setDigital(1);
		}
	}

	/**
	 * 
	 * @param seguridad
	 * @throws Exception
	 */
	public static void load(SeguridadUserForm seguridad) throws Exception {
		StringBuilder sql = new StringBuilder(
				"SELECT p.*,u.*,gu.nombreGrupo FROM ").append(tableUser)
				.append(" p , groupusers gu, ").append(tablePer).append(" u ")
				.append(" WHERE p.nameUser=u.nameUser")
				.append(" AND u.idGrupo = gu.idGrupo")
				.append(" AND p.nameUser ='")
				.append(seguridad.getNameUser().trim()).append("'")
				.append(" AND p.active = '").append(Constants.permission)
				.append("'").append(" AND gu.accountActive = '")
				.append(Constants.permission).append("'")
				.append(" AND u.accountActive = '")
				.append(Constants.permission).append("'");
		// System.out.println("[load] = " + sql.toString());
		Properties prop = JDBCUtil.doQueryOneRow(sql.toString(),Thread.currentThread().getStackTrace()[1].getMethodName());
		if ((prop != null)
				&& prop.getProperty("isEmpty").equalsIgnoreCase("false")) {
			seguridad.setIdPerson(Long.parseLong(prop.getProperty("idPerson")));
			seguridad.setMail(prop.getProperty("email"));
			seguridad.setIdGrupo(prop.getProperty("idGrupo"));
			seguridad.setNombreGrupo(prop.getProperty("nombreGrupo"));
			seguridad.setAdministracion(Integer.parseInt(prop
					.getProperty("administracion")));
			seguridad.setPerfil(Integer.parseInt(prop.getProperty("perfil")));
			seguridad.setFlujos(Integer.parseInt(prop.getProperty("flujos")));
			seguridad
					.setMensajes(Integer.parseInt(prop.getProperty("mensajes")));
			seguridad.setEstructura(Integer.parseInt(prop
					.getProperty("estructura")));
			seguridad.setToImpresion(Integer.parseInt(prop
					.getProperty("toImpresion")));
			seguridad.setSearch(Integer.parseInt(ToolsHTML.isEmptyOrNull(
					prop.getProperty("search"), "0")));
			seguridad.setSacop(Integer.parseInt(prop.getProperty("sacop")));
			seguridad
					.setRecord(ToolsHTML.isNumeric(prop.getProperty("record")) ? Integer
							.parseInt(prop.getProperty("record")) : 1);
			seguridad
					.setFiles(ToolsHTML.isNumeric(prop.getProperty("files")) ? Integer
							.parseInt(prop.getProperty("files")) : 1);
			seguridad.setDigital(ToolsHTML.isNumeric(prop
					.getProperty("digital")) ? Integer.parseInt(prop
					.getProperty("digital")) : 1);
		}
	}

	// public synchronized static boolean save2(SeguridadUserForm seguridad) {
	// StringBuilder edit = new StringBuilder("UPDATE ").append(tableGru);
	// edit.append(" SET nombreGrupo='").append(seguridad.getNombreGrupo()).append("',");
	// edit.append(" flujos='").append(seguridad.getFlujos()).append("',");
	// edit.append(" mensajes='").append(seguridad.getMensajes()).append("',");
	// edit.append(" administracion='").append(seguridad.getAdministracion()).append("',");
	// edit.append(" perfil='").append(seguridad.getPerfil()).append("',");
	// edit.append(" estructura='").append(seguridad.getEstructura());
	// edit.append("' WHERE idGrupo='").append(seguridad.getIdGrupo()).append("'");
	// //System.out.println("[save] edit = " + edit);
	// try {
	// return (JDBCUtil.doUpdate(edit.toString()) > 0);
	// } catch (Exception e) {
	// setMensaje(e.getMessage());
	// e.printStackTrace();
	// }
	// return false;
	// }

	/**
	 * 
	 * @param forma
	 * @return
	 */
	public synchronized static boolean delete1(SeguridadUserForm forma) {
		try {
			if (!HandlerPerfil.isCodInField("idGrupo", forma.getIdGrupo())) {
				StringBuilder delete = new StringBuilder("DELETE FROM ")
						.append(tableUser).append(" WHERE idGrupo = '")
						.append(forma.getIdGrupo()).append("'");
				return JDBCUtil.doUpdate(delete.toString()) > 0;
			} else {
				setMensaje("");
			}
		} catch (Exception e) {
			log.error(e.getMessage());
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * 
	 * @param nameField
	 * @param value
	 * @return
	 * @throws Exception
	 */
	public static boolean isCodInField1(String nameField, String value)
			throws Exception {
		StringBuilder sql = new StringBuilder("SELECT idGrupo FROM ")
				.append(tableGru).append(" WHERE ").append(nameField)
				.append(" = '").append(value).append("'")
				.append(" AND active = '").append(Constants.permission)
				.append("'");
		Vector<Properties> datos = JDBCUtil.doQueryVector(sql.toString(),Thread.currentThread().getStackTrace()[1].getMethodName());
		return (datos.size() > 0);
	}

	// public synchronized static boolean save1(SeguridadUserForm perfil) {
	// StringBuilder edit = new StringBuilder("UPDATE ").append(tableUser);
	// edit.append(" SET administracion='").append(perfil.getAdministracion());
	// edit.append("' WHERE nameUser='").append(perfil.getNameUser()).append("'");
	// edit.append()
	// //System.out.println("[save1] edit = " + edit);
	// try {
	// return (JDBCUtil.doUpdate(edit.toString()) > 0);
	// } catch (Exception e) {
	// setMensaje(e.getMessage());
	// e.printStackTrace();
	// }
	// //System.out.println("paso por aqui");
	// return false;
	// }

	/**
	 * 
	 * @param seguridad
	 * @return
	 */
	public synchronized static boolean save(SeguridadUserForm seguridad) {
		try {
			StringBuilder edit = new StringBuilder("UPDATE ").append(tableUser)
					.append(" SET nameUser='").append(seguridad.getNameUser())
					.append("',").append(" flujos=")
					.append(seguridad.getFlujos()).append(",")
					.append(" mensajes=").append(seguridad.getMensajes())
					.append(",").append(" administracion=")
					.append(seguridad.getAdministracion()).append(",")
					.append(" perfil=").append(seguridad.getPerfil())
					.append(",").append(" toImpresion=")
					.append(seguridad.getToImpresion()).append(",")
					.append(" estructura=").append(seguridad.getEstructura())
					.append(" ,search=").append(seguridad.getSearch())
					.append(" ,sacop=").append(seguridad.getSacop())
					.append(" ,record=").append(seguridad.getRecord())
					.append(" ,files=").append(seguridad.getFiles())
					.append(" ,digital=").append(seguridad.getDigital())
					.append(" WHERE nameUser='")
					.append(seguridad.getNameUser()).append("'")
					.append(" AND active = '").append(Constants.permission)
					.append("'");

			log.debug("[save] edit = " + edit);

			return (JDBCUtil.doUpdate(edit.toString()) > 0);
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
	 */
	public synchronized static boolean delete(SeguridadUserForm forma) {
		try {
			if (!HandlerPerfil.isCodInField("user", forma.getNameUser())) {
				StringBuilder delete = new StringBuilder("DELETE FROM ")
						.append(tableUser).append(" WHERE nameUser = '")
						.append(forma.getNameUser()).append("'");
				return JDBCUtil.doUpdate(delete.toString()) > 0;
			} else {
				setMensaje("");
			}
		} catch (Exception e) {
			log.error(e.getMessage());
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * 
	 * @param nameField
	 * @param value
	 * @return
	 * @throws Exception
	 */
	public static boolean isCodInField(String nameField, String value)
			throws Exception {
		StringBuilder sql = new StringBuilder("SELECT nameUser FROM ")
				.append(tableUser).append(" WHERE ").append(nameField)
				.append(" = '").append(value).append("'")
				.append(" AND active = '").append(Constants.permission)
				.append("'");
		Vector<Properties> datos = JDBCUtil.doQueryVector(sql.toString(),Thread.currentThread().getStackTrace()[1].getMethodName());
		return (datos.size() > 0);
	}

	/**
	 * 
	 * @return
	 * @throws Exception
	 */
	public static String getKey() throws Exception {
		StringBuilder sql = new StringBuilder("SELECT * FROM security");
		Properties prop = JDBCUtil.doQueryOneRow(sql.toString(),Thread.currentThread().getStackTrace()[1].getMethodName());
		if (prop.getProperty("isEmpty").equalsIgnoreCase("false")) {
			return prop.getProperty("keyGen");
		}
		return null;
	}

}
