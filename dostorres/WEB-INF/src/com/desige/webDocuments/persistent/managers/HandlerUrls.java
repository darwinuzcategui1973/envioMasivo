package com.desige.webDocuments.persistent.managers;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Properties;
import java.util.Vector;

import com.desige.webDocuments.enlaces.forms.UrlsActionForm;
import com.desige.webDocuments.persistent.utils.JDBCUtil;

/**
 * Title: HandlerUrls.java <br/>
 * Copyright: (c) 2004 Focus Consulting C.A.<br/>
 * 
 * @author Ing. Nelson Crespo (NC)
 * @version WebDocuments v3.0 <br/>
 *          Changes:<br/>
 *          <ul>
 *          <li>08/04/2004 (NC) Creation</li>
 *          </ul>
 */
public class HandlerUrls extends HandlerBD implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7757048532664124046L;

	/**
	 * 
	 * @param idUser
	 * @return
	 * @throws Exception
	 */
	public static Collection getUrlsUser(String idUser) throws Exception {
		ArrayList resp = new ArrayList();
		StringBuilder sql = new StringBuilder(
				"SELECT e.* FROM enlaces e,urlsusers uu").append(
				" WHERE e.idUrl = uu.idUrl AND uu.idUserGroup IN ").append(
				idUser);
		// System.out.println("[getUrlsUser] sql = " + sql);
		Vector datos = JDBCUtil.doQueryVector(sql.toString(),Thread.currentThread().getStackTrace()[1].getMethodName());
		for (int row = 0; row < datos.size(); row++) {
			Properties properties = (Properties) datos.elementAt(row);
			String id = properties.getProperty("IdUrl");
			String name = properties.getProperty("Name");
			String url = properties.getProperty("Url");
			String type = properties.getProperty("Type");
			UrlsActionForm bean = new UrlsActionForm(id, name, url, type);
			resp.add(bean);
		}
		return resp;
	}

	/**
	 * 
	 * @param forma
	 * @throws Exception
	 */
	public static void load(UrlsActionForm forma) throws Exception {
		StringBuilder sql = new StringBuilder(
				"SELECT Name, Url, Type  FROM enlaces WHERE IdUrl = '").append(
				forma.getId()).append("'");
		// System.out.println("[load] sql.toString() = " + sql.toString());
		Properties prop = JDBCUtil.doQueryOneRow(sql.toString(),Thread.currentThread().getStackTrace()[1].getMethodName());
		if ((prop != null)
				&& prop.getProperty("isEmpty").equalsIgnoreCase("false")) {
			forma.setName(prop.getProperty("Name"));
			forma.setUrl(prop.getProperty("Url"));
			forma.setType(prop.getProperty("Type"));
		}
	}

	/**
	 * 
	 * @param forma
	 * @return
	 */
	public synchronized static boolean edit(UrlsActionForm forma) {
		try {
			StringBuilder edit = new StringBuilder("UPDATE enlaces SET Name='")
					.append(forma.getName()).append("',Url='")
					.append(forma.getUrl()).append("',").append(" Type='")
					.append(forma.getType()).append("' WHERE IdUrl = '")
					.append(forma.getId()).append("'");
			return JDBCUtil.doUpdate(edit.toString()) > 0;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * 
	 * @param forma
	 * @return
	 */
	public synchronized static boolean delete(UrlsActionForm forma) {
		try {
			StringBuilder delete = new StringBuilder(
					"DELETE FROM enlaces  WHERE IdUrl = '").append(
					forma.getId()).append("'");
			if (forma.getId() != null && !forma.getId().trim().equals("")) {
				return JDBCUtil.doUpdate(delete.toString()) > 0;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * 
	 * @param forma
	 * @return
	 * @throws Exception
	 */
	public synchronized static boolean insert(UrlsActionForm forma)
			throws Exception {
		Connection con = null;
		PreparedStatement pst = null;
		boolean resp = false;
		try {
			// int num = IDDBFactorySql.getNextID("Links");
			int num = HandlerStruct.proximo("Links", "enlaces", "IdUrl");
			StringBuilder insert = new StringBuilder(
					"INSERT INTO enlaces (IdUrl,Name,Url,Type) VALUES(?,?,?,?)");

			con = JDBCUtil.getConnection(Thread.currentThread().getStackTrace()[1].getMethodName());
			con.setAutoCommit(false);
			pst = con.prepareStatement(JDBCUtil.replaceCastMysql(insert.toString()));
			pst.setString(1, String.valueOf(num));
			pst.setString(2, forma.getName());
			pst.setString(3, forma.getUrl());
			pst.setString(4, forma.getType());
			pst.executeUpdate();

			insert.setLength(0);
			insert.append("INSERT INTO urlsusers(IdUrl,idUserGroup) VALUES(?,?)");
			pst = con.prepareStatement(JDBCUtil.replaceCastMysql(insert.toString()));
			pst.setString(1, String.valueOf(num));
			pst.setString(2, forma.getUser());
			pst.executeUpdate();
			con.commit();
			resp = true;
		} catch (Exception e) {
			applyRollback(con);
			setMensaje(e.getMessage());
			resp = false;
		} finally {
			setFinally(con, pst);
		}
		return resp;
	}
}
