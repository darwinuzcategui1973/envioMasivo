package com.desige.webDocuments.persistent.managers;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.util.Properties;
import java.util.Vector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.desige.webDocuments.perfil.forms.PerfilActionForm;
import com.desige.webDocuments.persistent.utils.JDBCUtil;
import com.desige.webDocuments.utils.Constants;
import com.desige.webDocuments.utils.DesigeConf;
import com.desige.webDocuments.utils.ToolsHTML;

/**
 * Title: HandlerPerfil.java <br/>
 * Copyright: (c) 2004 Focus Consulting C.A.<br/>
 * 
 * @author Ing. Nelson Crespo (NC)
 * @version WebDocuments v3.0 <br/>
 *          Changes:<br/>
 *          <ul>
 *          <li>28/03/2004 (NC) Creation</li>
 *          <li>03/08/2005 (SR) Se valido ( primeravez) que el usuario una vez
 *          creado, se le informara que cambiara su passwd por seguridad.</li>
 *          <li>08/12/2005 (NC) Uso del Log</li>
 *          </ul>
 */
public class HandlerPerfil extends HandlerBD implements Serializable {
	static Logger log = LoggerFactory.getLogger(HandlerPerfil.class.getName());
	private static final String tablePerson = "person";

	// public static final String tableUser = "person";
	// private static String mensaje = null;

	/**
	 * 
	 * @param perfil
	 * @throws Exception
	 */
	public static void load1(PerfilActionForm perfil) throws Exception {
		StringBuilder sql = new StringBuilder("SELECT * FROM ")
				.append(tablePerson);
		// sql.append(tablePerson).append(" p ,").append(tableUser).append(" u ");
		// sql.append(" WHERE p.nameUser=u.nameUser");
		// sql.append(" AND u.nameUser = '").append(perfil.getUser()).append("'");
		sql.append(" WHERE nameUser = '").append(perfil.getUser()).append("'");
		sql.append(" AND accountActive = '").append(Constants.permission)
				.append("'");
		Properties prop = JDBCUtil.doQueryOneRow(sql.toString(),Thread.currentThread().getStackTrace()[1].getMethodName());
		log.debug("sql.toString() = " + sql.toString());
		if ((prop != null)
				&& prop.getProperty("isEmpty").equalsIgnoreCase("false")) {
			perfil.setUser(prop.getProperty("nameUser"));
			perfil.setClave(prop.getProperty("clave"));
		}
	}

	public static void load(PerfilActionForm perfil) throws Exception {
		StringBuilder sql = new StringBuilder(
				"SELECT p.*,c.cargo AS descCargo FROM ").append(tablePerson)
				.append(" p , tbl_cargo c");
		sql.append(" WHERE ").append(JDBCUtil.getCastAsIntString("p.cargo")).append(" = c.idcargo ");
		sql.append(" AND nameUser = '").append(perfil.getUser()).append("'");
		sql.append(" AND accountActive = '").append(Constants.permission)
				.append("'");
		Properties prop = JDBCUtil.doQueryOneRow(sql.toString(),Thread.currentThread().getStackTrace()[1].getMethodName());
		log.debug("[load] = " + sql.toString());
		if ((prop != null)
				&& prop.getProperty("isEmpty").equalsIgnoreCase("false")) {
			perfil.setUser(prop.getProperty("nameUser"));
			perfil.setClave(prop.getProperty("clave"));
			perfil.setClavenueva(prop.getProperty("clave"));
			perfil.setRepclave(prop.getProperty("clave"));
			perfil.setIdGrupo(prop.getProperty("idGrupo"));
			perfil.setApellidos(prop.getProperty("Apellidos").trim());
			perfil.setNombres(prop.getProperty("Nombres").trim());
			perfil.setEmail(prop.getProperty("Email").trim());
			perfil.setDireccion(prop.getProperty("Direccion"));
			perfil.setCiudad(prop.getProperty("Ciudad"));
			perfil.setEstado(prop.getProperty("Estado"));
			perfil.setPais(prop.getProperty("CodPais"));
			perfil.setTelefono(prop.getProperty("Tlf"));
			perfil.setZip(prop.getProperty("CodigoPostal"));
			perfil.setIdioma(prop.getProperty("IdLanguage"));
			// perfil.setCargo(prop.getProperty("cargo"));
			perfil.setCargo(prop.getProperty("descCargo"));
			perfil.setArea(prop.getProperty("idarea") != null ? prop
					.getProperty("idarea") : "");
			// perfil.setNumRecordPages(prop.getProperty("numRecordPages"));
			String numRecords = prop.getProperty("numRecordPages");
			int valor = 0;
			if (!ToolsHTML.isEmptyOrNull(numRecords)) {
				valor = Integer.parseInt(numRecords);
			}
			if (valor <= 0) {
				String num = DesigeConf.getProperty("application.numRecords");
				valor = Integer.parseInt(num);
			}
			perfil.setNumRecordPages(String.valueOf(valor));
		}
	}

	public synchronized static boolean updatePassWord(PerfilActionForm perfil) {
		PreparedStatement st = null;
		Connection con = null;
		boolean result = false;
		StringBuilder edit = new StringBuilder("UPDATE ").append(tablePerson);
		edit.append(" SET clave = ? ");
		edit.append(" WHERE nameUser = ? ");
		edit.append(" AND accountActive = '").append(Constants.permission)
				.append("'");
		try {
			con = JDBCUtil.getConnection(Thread.currentThread().getStackTrace()[1].getMethodName());
			st = con.prepareStatement(JDBCUtil.replaceCastMysql(edit.toString()));
			st.setString(1, ToolsHTML.encripterPass(perfil.getClave()));
			st.setString(2, perfil.getUser());
			result = st.executeUpdate() > 0;
			if (con != null) {
				con.close();
			}
			if (st != null) {
				st.close();
			}
			perfil.cleanForm();
		} catch (Exception e) {
			log.error(e.getMessage());
			setMensaje(e.getMessage());
			e.printStackTrace();
			result = false;
		}
		return result;
	}

	// 03 agosto 2005 inicio
	/**
	 * 
	 * @param perfil
	 * @return
	 */
	public synchronized static boolean save(PerfilActionForm perfil) {
		boolean result = false;
		Connection con = null;
		PreparedStatement st = null;
		try {
			StringBuilder edit = new StringBuilder("UPDATE ")
					.append(tablePerson)
					.append(" SET Apellidos = ? , Nombres = ?,email = ?, Direccion = ?,Ciudad = ?,")
					.append(" Estado = ?,CodPais = ?,CodigoPostal = ?,Tlf = ?,IdLanguage = ? ,numRecordPages = ?");
			if (!perfil.getClave().equalsIgnoreCase(perfil.getRepclave())) {
				edit.append(",clave = ? ,primeravez = ").append(JDBCUtil.getCastAsBitString("1"));
				edit.append(", accountActive = ").append(JDBCUtil.getCastAsBitString("1")).append(", dateLastPass = ?");
			}
			// if (!ToolsHTML.isEmptyOrNull(perfil.getCargo())) {
			// edit.append(",cargo = ?");
			// }
			// if (!ToolsHTML.isEmptyOrNull(perfil.getArea())) {
			// edit.append(",idarea = ").append(perfil.getArea());
			// }
			edit.append(" WHERE nameUser = ? AND accountActive = '")
					.append(Constants.permission).append("'");
			con = JDBCUtil.getConnection(Thread.currentThread().getStackTrace()[1].getMethodName());
			st = con.prepareStatement(JDBCUtil.replaceCastMysql(edit.toString()));
			st.setString(1, perfil.getApellidos());
			st.setString(2, perfil.getNombres());
			st.setString(3, perfil.getEmail());
			st.setString(4, perfil.getDireccion());
			st.setString(5, perfil.getCiudad());
			st.setString(6, perfil.getEstado());
			st.setString(7, perfil.getPais());
			st.setString(8, perfil.getZip());
			st.setString(9, perfil.getTelefono());
			st.setString(10, perfil.getIdioma());
			st.setInt(11, Integer.parseInt(perfil.getNumRecordPages()));
			int pos = 12;
			if (!perfil.getClave().equalsIgnoreCase(perfil.getRepclave())) {
				st.setString(pos, ToolsHTML.encripterPass(perfil.getClave()));
				pos++;
				st.setTimestamp(pos,
						new Timestamp(new java.util.Date().getTime()));
				pos++;
			}
			// if (!ToolsHTML.isEmptyOrNull(perfil.getCargo())) {
			// st.setString(pos,perfil.getCargo());
			// pos++;
			// }
			// if (!ToolsHTML.isEmptyOrNull(perfil.getArea())) {
			// st.setLong(pos,Long.parseLong(perfil.getArea()));
			// pos++;
			// }
			st.setString(pos, perfil.getUser());
			result = st.executeUpdate() > 0;
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			setFinally(con, st);
		}
		return result;
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
				.append(tablePerson).append(" WHERE ").append(nameField)
				.append(" = '").append(value).append("'")
				.append(" AND accountActive = '").append(Constants.permission)
				.append("'");
		log.debug("[isCodInField] = " + sql.toString());
		Vector datos = JDBCUtil.doQueryVector(sql.toString(),Thread.currentThread().getStackTrace()[1].getMethodName());
		return (datos.size() > 0);
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
		StringBuilder sql = new StringBuilder("SELECT idAddress FROM address")
				.append(" WHERE ").append(nameField).append(" = '")
				.append(value).append("'");
		Vector datos = JDBCUtil.doQueryVector(sql.toString(),Thread.currentThread().getStackTrace()[1].getMethodName());
		return (datos.size() > 0);
	}
}
