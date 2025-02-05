package com.desige.webDocuments.persistent.managers;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Hashtable;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.Vector;

import org.apache.commons.dbutils.DbUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.desige.webDocuments.mail.forms.AddressForm;
import com.desige.webDocuments.mail.forms.MailForm;
import com.desige.webDocuments.persistent.utils.JDBCUtil;
import com.desige.webDocuments.utils.ApplicationExceptionChecked;
import com.desige.webDocuments.utils.Constants;
import com.desige.webDocuments.utils.ToolsHTML;

/**
 * Title: HandlerMessages.java<br>
 * Copyright: (c) 2004 Desige Servicios de Computación<br/>
 * 
 * @author Ing. Nelson Crespo (NC)
 * @author Ing. Simón Rodriguéz (SR)
 * @version WebDocuments v1.0 <br>
 *          Changes:<br>
 *          <ul>
 *          <li>07/08/2004 (NC) Creation</li>
 *          <li>11/04/2006, resultSet para userName en insertar mensaje,query
 *          para sacar los mensajes pendientes (SR)</li>
 *          <li>03/05/2006 La bandeja de entrada de los mails no funcionaba, era
 *          vulnerable en caso que un usuario cambiara su mail, y en cada mail
 *          se manda el numero del documento en el titulo (SR)</li>
 *          <ul>
 */
public class HandlerMessages extends HandlerBD implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -7003202921227729413L;
	private static final Logger log = LoggerFactory.getLogger(HandlerMessages.class
			.getName());

	/**
	 * Por intermedio de este método se almacena en base de datos la información
	 * pertinente a los mensajes enviados desde la aplicación
	 * 
	 * @param forma
	 * @return
	 */
	public static synchronized boolean insert(MailForm forma) {
		boolean resp = false;
		Connection con = null;
		PreparedStatement st = null;
		try {
			con = JDBCUtil.getConnection(Thread.currentThread().getStackTrace()[1].getMethodName());
			con.setAutoCommit(false);
			// int num = HandlerStruct.proximo(con, "messages", "messages",
			// "idMessage");
			int num = HandlerStruct
					.proximo("messages", "messages", "idMessage");

			processMail(con, forma, num);
			Vector to = new Vector();
			processMail(con, forma.getTo(), num, to);
			processMail(con, forma.getCc(), num, to);
			con.commit();
			resp = true;
		} catch (SQLException sqle) {
			log.error(sqle.getMessage());
			sqle.printStackTrace();
			applyRollback(con);
			resp = false;
		} catch (Exception ex) {
			log.error(ex.getMessage());
			applyRollback(con);
			resp = false;
			ex.printStackTrace();
		} finally {
			setFinally(con, st);
		}
		return resp;
	}

	/**
	 * 
	 * @param email2
	 * @return
	 */
	public static String userNameBuscar(String email2) {
		Connection con = null;
		PreparedStatement st = null;
		ResultSet rs = null;
		String resultado = "";

		try {
			// en caso que mande una cadena completa de mails, lo separamos y
			// obtendremos el primero
			// de ellos
			if (email2 != null && !email2.endsWith(";")) {
				email2 = email2.concat(";");
			}
			if (email2 != null) {
				StringTokenizer email1 = new StringTokenizer(email2, ";");
				String email = "";
				if (email1 != null && email1.hasMoreElements()) {
					email = email1.nextToken();
				}
				StringBuilder sqlUser = new StringBuilder("")
						.append("select distinct nameUser from person where email='")
						.append(email).append("'")
						.append(" and accountactive='")
						.append(Constants.permission).append("'");

				con = JDBCUtil.getConnection(Thread.currentThread().getStackTrace()[1].getMethodName());
				st = con.prepareStatement(JDBCUtil.replaceCastMysql(sqlUser.toString()));
				rs = st.executeQuery();
				if (rs.next()) {
					resultado = rs.getString("nameUser") != null ? rs
							.getString("nameUser") : "";
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			JDBCUtil.closeQuietly(con, st, rs);
		}

		return resultado;
	}

	/**
	 * 
	 * @param idMessage
	 * @param emailUser
	 * @return
	 */
	public static synchronized boolean updateStatuMailUser(int idMessage,
			String emailUser) {
		boolean resp = false;
		Connection con = null;
		PreparedStatement st = null;
		try {
			String sql = "UPDATE messagexuser SET statu = '0' WHERE idMessage = ? AND toMessage = ?";
			con = JDBCUtil.getConnection(Thread.currentThread().getStackTrace()[1].getMethodName());
			con.setAutoCommit(false);
			st = con.prepareStatement(JDBCUtil.replaceCastMysql(sql));
			st.setInt(1, idMessage);
			st.setString(2, emailUser);
			st.executeUpdate();
			con.commit();
			resp = true;
		} catch (SQLException sqle) {
			log.error(sqle.getMessage());
			sqle.printStackTrace();
			applyRollback(con);
			resp = false;
		} catch (Exception ex) {
			log.error(ex.getMessage());
			applyRollback(con);
			resp = false;
			ex.printStackTrace();
		} finally {
			setFinally(con, st);
		}
		return resp;
	}

	/**
	 * 
	 * @param con
	 * @param forma
	 * @param num
	 * @throws Exception
	 */
	private static void processMail(Connection con, MailForm forma, int num)
			throws Exception {
		PreparedStatement st = null;

		try {
			// en caso que el from sea el correo identificado en el properties,
			// me trae un username
			// null
			forma.setUserName(userNameBuscar(forma.getFrom()));
			if (ToolsHTML.isEmptyOrNull(forma.getUserName())) {
				// volvemos a buscar el username con otros correos
				forma.setUserName(userNameBuscar(forma.getTo()));
			}
			StringBuilder sql = new StringBuilder(
					"INSERT INTO messages (idMessage,FromMessage,Subject,dateEmail,Message,")
					.append("nameFrom,userName,toMessage,copyMessage) VALUES (?,?,?,?,?,?,?,?,?)");
			st = con.prepareStatement(JDBCUtil.replaceCastMysql(sql.toString()));
			st.setInt(1, num);
			st.setString(2, forma.getFrom());
			st.setString(3, forma.getSubject());
			Timestamp time = new Timestamp(new Date().getTime());
			st.setTimestamp(4, time);
			st.setString(5, forma.getMensaje());
			st.setString(6, forma.getNameFrom());
			st.setString(7, forma.getUserName());
			st.setString(8, forma.getTo());
			st.setString(9, forma.getCc());
			st.executeUpdate();
		} catch (Exception ex) {
			log.error(ex.getMessage());
			ex.printStackTrace();
		} finally {
			DbUtils.closeQuietly(st);
		}
	}

	/**
	 * 
	 * @param con
	 * @param dirMails
	 * @param id
	 * @param mails
	 * @throws Exception
	 */
	private static void processMail(Connection con, String dirMails, int id,
			Vector mails) throws Exception {
		if (!ToolsHTML.isEmptyOrNull(dirMails)) {
			for (StringTokenizer stk = new StringTokenizer(dirMails, ";"); stk
					.hasMoreTokens();) {
				String dir = stk.nextToken();
				if (!mails.contains(dir)) {
					saveMail(con, dir, id);
					mails.add(dir);
				}

			}
		}
	}

	private static synchronized void saveMail(Connection con, String to, int id)
			throws Exception {
		PreparedStatement st = null;

		try {
			// buscamos el usuario de dicho correo en el sistema
			String userName = "";
			userName = userNameBuscar(to);

			StringBuilder sql = new StringBuilder(
					"INSERT INTO messagexuser (idMessage,id,toMessage,userName) Values(?,?,?,?)");
			int num = HandlerStruct.proximo("messagesxUser", "messagexuser",
					"idMessage");
			st = con.prepareStatement(JDBCUtil.replaceCastMysql(sql.toString()));
			st.setInt(1, id);
			st.setInt(2, num);
			st.setString(3, to);
			st.setString(4, userName);
			st.executeUpdate();
		} catch (Exception ex) {
			log.error(ex.getMessage());
			ex.printStackTrace();
		} finally {
			DbUtils.closeQuietly(st);
		}
	}

	// private static void processMail(Connection con,String dirMails,int id)
	// throws Exception {
	// if (!ToolsHTML.isEmptyOrNull(dirMails)){
	// StringTokenizer stk = new StringTokenizer(dirMails,";");
	// while (stk.hasMoreTokens()){
	// String dir = stk.nextToken();
	// //System.out.println("dir = " + dir);
	// saveMail(con,dir,id);
	// }
	// }
	// }

	// private static void saveMail(Connection con,String to,int id) throws
	// Exception{
	// PreparedStatement st = null;
	// StringBuilder sql = new StringBuilder("INSERT INTO messagexuser
	// (idMessage,id,toMessage)
	// Values(?,?,?)");
	// try {
	// int num = IDDBFactorySql.getNextID("messagesxUser");
	// st = con.prepareStatement(sql.toString());
	// st.setInt(1,id);
	// st.setInt(2,num);
	// st.setString(3,to);
	// st.executeUpdate();
	// } catch (Exception ex) {
	// ex.printStackTrace();
	// }
	// }

	/**
	 * Método por medio del cual se cargan los mensajes enviados del usuario
	 * indicado
	 * 
	 * @param mailForm
	 * @throws Exception
	 */
	public static void loadMail(MailForm mailForm) throws Exception {
		StringBuilder sql = new StringBuilder(1024).append(
				"SELECT * FROM messages m WHERE m.idMessage = ").append(
				mailForm.getIdMessage());
		// sql.append("SELECT
		// nameFrom,fromMessage,Subject,dateEmail,Message,m.idMessage,toMessage");
		// sql.append(" FROM messagexuser mu,messages m");
		// sql.append(" WHERE m.idMessage = mu.idMessage");
		// sql.append(" AND m.idMessage = ").append(mailForm.getIdMessage());
		// sql.append(" AND mu.id = ").append(mailForm.getIdMessageUser());
		log.debug("[loadMail] = " + sql.toString());
		Properties prop = JDBCUtil.doQueryOneRow(sql.toString(),Thread.currentThread().getStackTrace()[1].getMethodName());
		if (prop.getProperty("isEmpty").equalsIgnoreCase("false")) {
			mailForm.setFrom(prop.getProperty("fromMessage"));
			mailForm.setSubject(prop.getProperty("Subject"));
			mailForm.setMensaje(prop.getProperty("Message"));
			mailForm.setNameFrom(prop.getProperty("nameFrom"));
			mailForm.setTo(prop.getProperty("toMessage"));
			mailForm.setDateMail(ToolsHTML.formatDateShow(
					prop.getProperty("dateEmail"), true));
			mailForm.setCc(prop.getProperty("copyMessage"));
		}
	}

	/**
	 * 
	 * @param emailUser
	 * @return
	 * @throws Exception
	 */
	public static int getTotalInbox(String emailUser) throws Exception {
		StringBuilder sql = new StringBuilder(1024)
				.append("select COUNT(nameFrom) As inbox  from messages m, messagexuser mu ")
				.append(" where  m.idMessage = mu.idMessage and mu.statu = '1' ")
				// sql.append(" and mu.active = '1' and m.username=mu.username and ");
				// sql.append(" m.username in ").append("(select nameuser from person where email='").append(emailUser).append("')");
				.append(" and mu.active = '1' and ").append(" mu.username in ")
				.append("(select nameuser from person where email='")
				.append(emailUser).append("')");
		log.debug("[getTotalInbox] = " + sql.toString());
		Properties prop = JDBCUtil.doQueryOneRow(sql.toString(),Thread.currentThread().getStackTrace()[1].getMethodName());
		int resp = 0;
		if (prop.getProperty("isEmpty").equalsIgnoreCase("false")) {
			resp = Integer.parseInt(prop.getProperty("inbox").trim());
		}
		return resp;
	}

	/**
	 * 
	 * @param idUser
	 * @return
	 * @throws ApplicationExceptionChecked
	 * @throws Exception
	 */
	public static Collection getAllSentsMessagesForUser(String idUser)
			throws ApplicationExceptionChecked, Exception {
		StringBuilder sql = new StringBuilder(1024)
				.append("SELECT m.* FROM messages m  WHERE userName = '")
				.append(idUser).append("'").append(" AND statuMail = '")
				.append(Constants.permission).append("'")
				.append(" AND Message is not null  ")
				.append(" AND toMessage is not null  ")
				.append("  ORDER BY dateEmail DESC");
		ArrayList result = new ArrayList();
		log.debug("[getAllSentsMessagesForUser] = " + sql.toString());
		Vector datos = JDBCUtil.doQueryVector(sql.toString(),Thread.currentThread().getStackTrace()[1].getMethodName());
		for (int row = 0; row < datos.size(); row++) {
			Properties properties = (Properties) datos.elementAt(row);
			MailForm bean = new MailForm(properties.getProperty("fromMessage"),
					properties.getProperty("Subject"),
					properties.getProperty("toMessage"),
					properties.getProperty("Message"));
			bean.setNameFrom(properties.getProperty("nameFrom"));
			bean.setIdMessage(properties.getProperty("idMessage"));
			bean.setCc(properties.getProperty("copyMessage"));
			bean.setDateMail(ToolsHTML.formatDateShow(
					properties.getProperty("dateEmail"), true));
			int pos = bean.getTo().indexOf(";");
			if (pos > 0) {
				bean.setNameTo(HandlerDBUser.getNameUserForMail(bean.getTo()
						.substring(0, pos), idUser));
				bean.setNameTo(bean.getNameTo() == null ? bean.getTo()
						.substring(0, pos) : bean.getNameTo());
			} else {
				bean.setNameTo(HandlerDBUser.getNameUserForMail(bean.getTo(),
						idUser));
				bean.setNameTo(bean.getNameTo() == null ? bean.getTo() : bean
						.getNameTo());
			}
			result.add(bean);
		}
		return result;
	}

	/**
	 * 
	 * @param user
	 * @return
	 * @throws Exception
	 */
	public static Collection getAllInBoxMessagesForUser(String user)
			throws Exception {
		StringBuilder sql = new StringBuilder(1024)
				.append(" SELECT m.*,mu.statu,mu.id FROM messages m,messagexuser mu  where ")
				.append(" mu.idmessage=m.idmessage AND mu.userName='")
				.append(user).append("'")
				// sql.append(" mu.idmessage=m.idmessage and (m.username='").append(user).append("'
				// or
				// mu.userName='").append(user).append("')");
				.append(" and active='1' ").append(" ORDER BY dateEmail DESC");
		log.debug("[getAllInBoxMessagesForUser] = " + sql.toString());
		ArrayList resp = new ArrayList();
		Vector datos = JDBCUtil.doQueryVector(sql.toString(),Thread.currentThread().getStackTrace()[1].getMethodName());
		Hashtable yaExiste = new Hashtable();
		for (int row = 0; row < datos.size(); row++) {
			Properties properties = (Properties) datos.elementAt(row);
			if (!yaExiste
					.containsKey(properties.getProperty("idMessage") != null ? properties
							.getProperty("idMessage") : "")) {
				yaExiste.put(
						properties.getProperty("idMessage") != null ? properties
								.getProperty("idMessage") : "",
						properties.getProperty("idMessage") != null ? properties
								.getProperty("idMessage") : "");
				MailForm bean = new MailForm(
						properties.getProperty("fromMessage"),
						properties.getProperty("Subject"),
						properties.getProperty("toMessage"),
						properties.getProperty("Message"));
				bean.setNameFrom(properties.getProperty("nameFrom"));
				bean.setIdMessage(properties.getProperty("idMessage"));
				bean.setDateMail(ToolsHTML.formatDateShow(
						properties.getProperty("dateEmail"), true));
				String id = properties.getProperty("id");
				bean.setIdMessageUser(id.trim());
				bean.setNew(false);
				if ("1".equalsIgnoreCase(properties.getProperty("statu").trim())) {
					bean.setNew(true);
				}
				resp.add(bean);
			}
		}
		return resp;
	}

	// MessageXUser

	/**
	 * 
	 * @param items
	 * @return
	 * @throws ApplicationExceptionChecked
	 */
	public static boolean deleteMessageUser(String[] items)
			throws ApplicationExceptionChecked {
		PreparedStatement st = null;
		boolean resp = false;
		Connection con = null;
		try {
			con = JDBCUtil.getConnection(Thread.currentThread().getStackTrace()[1].getMethodName());
			con.setAutoCommit(false);
			if (items != null && items.length > 0) {
				for (int row = 0; row < items.length; row++) {
					String ids = items[row];
					int pos = ids.indexOf(",");
					String idMessage = ids.substring(0, pos);
					String id = ids.substring(pos + 1, ids.length());
					StringBuilder sql = new StringBuilder(
							"UPDATE messagexuser SET active = '0' WHERE id = '");
					sql.append(id).append("' AND idMessage = ")
							.append(idMessage);
					log.debug("[deleteMessageUser] = " + sql.toString());
					st = con.prepareStatement(JDBCUtil.replaceCastMysql(sql.toString()));
					st.executeUpdate();
				}
			}
			con.commit();
			resp = true;
		} catch (SQLException e) {
			log.error(e.getMessage());
			applyRollback(con);
			e.printStackTrace();
		} finally {
			setFinally(con, st);
		}
		return resp;
	}

	/**
	 * 
	 * @param items
	 * @return
	 * @throws ApplicationExceptionChecked
	 */
	public static boolean deleteMessageSendUser(String[] items)
			throws ApplicationExceptionChecked {
		PreparedStatement pst = null;
		boolean resp = false;
		Connection con = null;
		try {
			if (items != null && items.length > 0) {
				con = JDBCUtil.getConnection(Thread.currentThread().getStackTrace()[1].getMethodName());
				con.setAutoCommit(false);
				pst = con
						.prepareStatement(JDBCUtil.replaceCastMysql("UPDATE messages SET statuMail = '0' WHERE idMessage = ?"));

				for (int row = 0; row < items.length; row++) {
					String ids = items[row];
					ids = ids.replace("'", "");
					pst.setInt(1, Integer.parseInt(ids));
					pst.executeUpdate();
				}
			}
			con.commit();
			resp = true;
		} catch (SQLException e) {
			log.error(e.getMessage());
			applyRollback(con);
			e.printStackTrace();
		} finally {
			setFinally(con, pst);
		}
		return resp;
	}

	/**
	 * 
	 * @param idAddress
	 * @param usuario
	 * @return
	 * @throws ApplicationExceptionChecked
	 */
	public synchronized static boolean delete(String idAddress, String usuario)
			throws ApplicationExceptionChecked {
		PreparedStatement pst = null;
		boolean resp = false;
		Connection con = null;
		try {
			con = JDBCUtil.getConnection(Thread.currentThread().getStackTrace()[1].getMethodName());
			con.setAutoCommit(false);

			pst = con
					.prepareStatement(JDBCUtil.replaceCastMysql("UPDATE address SET active = '0' WHERE idAddress = ?"));
			for (StringTokenizer tokens = new StringTokenizer(idAddress, ","); tokens
					.hasMoreTokens();) {
				String dir = tokens.nextToken();
				pst.setInt(1, Integer.parseInt(dir));
				pst.executeUpdate();
			}
			con.commit();
			resp = true;
		} catch (SQLException e) {
			log.error(e.getMessage());
			applyRollback(con);
			e.printStackTrace();
		} finally {
			setFinally(con, pst);
		}
		return resp;
	}

	/**
	 * 
	 * @param forma
	 * @return
	 */
	public static synchronized boolean editContact(AddressForm forma) {
		PreparedStatement st = null;
		boolean resp = false;
		Connection con = null;
		try {
			StringBuilder edit = new StringBuilder(1024)
					.append("UPDATE address SET nombre = ?,apellido = ?,email = ? WHERE idAddress = ")
					.append(forma.getIdAddress());
			con = JDBCUtil.getConnection(Thread.currentThread().getStackTrace()[1].getMethodName());
			st = con.prepareStatement(JDBCUtil.replaceCastMysql(edit.toString()));
			st.setString(1, forma.getNombre());
			st.setString(2, forma.getApellido());
			st.setString(3, forma.getEmail());
			resp = st.executeUpdate() > 0;
		} catch (Exception ex) {
			log.error(ex.getMessage());
			ex.printStackTrace();
		} finally {
			setFinally(con, st);
		}
		return resp;
	}

	/**
	 * 
	 * @param forma
	 * @param idUser
	 * @return
	 */
	public static synchronized boolean insertContacts(AddressForm forma,
			String idUser) {
		PreparedStatement st = null;
		boolean resp = false;
		Connection con = null;
		try {
			StringBuilder sql = new StringBuilder(
					"INSERT INTO address (idAddress,nombre,apellido,email) Values (?,?,?,?)");

			con = JDBCUtil.getConnection(Thread.currentThread().getStackTrace()[1].getMethodName());
			con.setAutoCommit(false);
			// int idAddress = IDDBFactorySql.getNextID("messagesxUser");
			int idAddress = HandlerStruct.proximo("messagesxUser",
					"messagexuser", "idMessage");
			st = con.prepareStatement(JDBCUtil.replaceCastMysql(sql.toString()));
			st.setInt(1, idAddress);
			st.setString(2, forma.getNombre());
			st.setString(3, forma.getApellido());
			st.setString(4, forma.getEmail());
			st.executeUpdate();

			sql.setLength(0);
			sql.append("INSERT INTO contacts (idAddress,idUser) Values (?,?)");
			st = con.prepareStatement(JDBCUtil.replaceCastMysql(sql.toString()));
			st.setInt(1, idAddress);
			st.setString(2, idUser);
			st.executeUpdate();
			con.commit();
			resp = true;
		} catch (Exception ex) {
			log.error(ex.getMessage());
			applyRollback(con);
			ex.printStackTrace();
		} finally {
			setFinally(con, st);
		}
		return resp;
	}

	/**
	 * 
	 * @param idUser
	 * @param itemsSelecteds
	 * @param searchName
	 * @return
	 * @throws Exception
	 */
	public static Collection getAllAddress(String idUser,
			ArrayList itemsSelecteds, String searchName) throws Exception {
		StringBuilder sql = new StringBuilder(1024)
				.append("SELECT distinct A.idAddress,nombre,apellido,email,lower(A.apellido) AS apellido_lower ")
				.append(" FROM address A,contacts C WHERE C.idUser = '")
				.append(idUser);
		sql.append("' AND C.idAddress=A.idAddress AND active = '1' ");
		if (!ToolsHTML.isEmptyOrNull(searchName)) {
			sql.append(" AND (LOWER(A.nombre) LIKE '%")
					.append(searchName.toLowerCase()).append("%' ")
					.append(" OR LOWER(A.apellido) LIKE '%")
					.append(searchName.toLowerCase()).append("%' ");
			switch (Constants.MANEJADOR_ACTUAL) {
			case Constants.MANEJADOR_MSSQL:
				sql.append(" OR LOWER(A.nombre)+' '+LOWER(A.apellido) LIKE '%")
						.append(searchName.toLowerCase())
						.append("%' ")
						.append(" OR LOWER(A.apellido)+' '+LOWER(A.nombre) LIKE '%")
						.append(searchName.toLowerCase()).append("%') ");
				break;
			case Constants.MANEJADOR_POSTGRES:
				sql.append(
						" OR LOWER(A.nombre)||' '||LOWER(A.apellido) LIKE '%")
						.append(searchName.toLowerCase())
						.append("%' ")
						.append(" OR LOWER(A.apellido)||' '||LOWER(A.nombre) LIKE '%")
						.append(searchName.toLowerCase()).append("%') ");
				break;
			case Constants.MANEJADOR_MYSQL:
				sql.append(
						" OR CONCAT(LOWER(A.nombre),' ',LOWER(A.apellido)) LIKE '%")
						.append(searchName.toLowerCase())
						.append("%' ")
						.append(" OR CONCAT(LOWER(A.apellido),' ',LOWER(A.nombre)) LIKE '%")
						.append(searchName.toLowerCase()).append("%') ");
				break;
			}
		}
		sql.append(" ORDER BY LOWER(A.Apellido) ");
		log.debug(sql.toString());
		log.debug("itemsSelecteds = " + itemsSelecteds);
		Vector resp = new Vector();
		Vector datos = JDBCUtil.doQueryVector(sql.toString(),Thread.currentThread().getStackTrace()[1].getMethodName());
		for (int row = 0; row < datos.size(); row++) {
			Properties properties = (Properties) datos.elementAt(row);
			String idAddress = properties.getProperty("idAddress");
			AddressForm bean = new AddressForm(idAddress,
					properties.getProperty("nombre"),
					properties.getProperty("apellido"),
					properties.getProperty("email"));
			if (itemsSelecteds.contains(idAddress)) {
				bean.setSelected(true);
			} else {
				bean.setSelected(false);
			}
			resp.add(bean);
		}
		return resp;
	}

	/**
	 * 
	 * @param idAddress
	 * @return
	 * @throws Exception
	 */
	public static AddressForm loadContact(String idAddress) throws Exception {
		StringBuilder sql = new StringBuilder(
				"SELECT * FROM address WHERE active = '1' AND idAddress = ")
				.append(idAddress);
		// System.out.println("[loadContact] = " + sql.toString());
		Properties prop = JDBCUtil.doQueryOneRow(sql.toString(),Thread.currentThread().getStackTrace()[1].getMethodName());
		if (prop.getProperty("isEmpty").equalsIgnoreCase("false")) {
			AddressForm forma = new AddressForm(idAddress,
					prop.getProperty("nombre"), prop.getProperty("apellido"),
					prop.getProperty("email"));
			return forma;
		}

		return null;
	}

}
