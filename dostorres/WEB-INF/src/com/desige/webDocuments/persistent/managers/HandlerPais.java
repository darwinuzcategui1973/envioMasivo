package com.desige.webDocuments.persistent.managers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Properties;
import java.util.Vector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.desige.webDocuments.pais.forms.BaseCountryForm;
import com.desige.webDocuments.persistent.utils.JDBCUtil;
import com.desige.webDocuments.utils.ToolsHTML;
import com.desige.webDocuments.utils.beans.Search;

/**
 * Title: HandlerPais.java <br/>
 * Copyright: (c) 2004 Focus Consulting C.A.<br/>
 * 
 * @author Ing. Nelson Crespo (NC)
 * @version WebDocuments v3.0 <br/>
 *          Changes:<br/>
 *          <ul>
 *          <li>08/04/2004 (NC) Creation</li>
 *          <li>08/12/2005 (NC) Uso del Log</li>
 *          </ul>
 */
public class HandlerPais extends HandlerBD {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1951205751406616424L;
	private static Logger log = LoggerFactory.getLogger(HandlerPais.class.getName());
	private  static final String nameTable = "pais";

	/**
	 * 
	 * @param descript
	 * @return
	 * @throws Exception
	 */
	public static Collection getAllPaises(String descript) throws Exception {
		StringBuilder sql = new StringBuilder("SELECT * FROM ")
				.append(nameTable);
		if (ToolsHTML.checkValue(descript)) {
			sql.append(" WHERE Nombre LIKE '%").append(descript).append("%'");
		}
		sql.append(" ORDER BY lower(Nombre) ");
		ArrayList resp = new ArrayList();
		Vector datos = JDBCUtil.doQueryVector(sql.toString(),Thread.currentThread().getStackTrace()[1].getMethodName());
		log.debug("sql.toString() = " + sql.toString());
		for (int row = 0; row < datos.size(); row++) {
			Properties properties = (Properties) datos.elementAt(row);
			Search bean = new Search(properties.getProperty("CodPais"),
					properties.getProperty("Nombre"));
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
	public static String getFieldPais(String field, String cod)
			throws Exception {
		StringBuilder sql = new StringBuilder("SELECT ").append(field)
				.append(" FROM ").append(nameTable).append(" WHERE CodPais='")
				.append(cod).append("'");
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
	public static void load(BaseCountryForm forma) throws Exception {
		StringBuilder sql = new StringBuilder("SELECT * FROM ")
				.append(nameTable).append(" WHERE CodPais = '")
				.append(forma.getId()).append("'");
		log.debug("[load] sql.toString() = " + sql.toString());
		Properties prop = JDBCUtil.doQueryOneRow(sql.toString(),Thread.currentThread().getStackTrace()[1].getMethodName());
		if ((prop != null)
				&& prop.getProperty("isEmpty").equalsIgnoreCase("false")) {
			forma.setName(prop.getProperty("Nombre"));
		}
	}

	/**
	 * 
	 * @param forma
	 * @return
	 */
	public synchronized static boolean edit(BaseCountryForm forma) {
		try {
			StringBuilder edit = new StringBuilder("UPDATE ").append(nameTable)
					.append(" SET Nombre='").append(forma.getName())
					.append("'").append(" WHERE CodPais = '")
					.append(forma.getId()).append("'");
			return JDBCUtil.doUpdate(edit.toString()) > 0;
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
	public synchronized static boolean insert(BaseCountryForm forma) {
		try {
			// int num = IDDBFactorySql.getNextID("pais");
			int num = HandlerStruct.proximo("pais", "pais", "CodPais");
			StringBuilder insert = new StringBuilder("INSERT INTO ")
					.append(nameTable)
					.append(" (CodPais,Nombre,Codigo) VALUES('").append(num)
					.append("','").append(forma.getName()).append("',''")
					.append(")");
			log.debug("insert = " + insert);
			return JDBCUtil.doUpdate(insert.toString()) > 0;
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
	public synchronized static boolean delete(BaseCountryForm forma) {
		try {
			if (!HandlerPerfil.isCodInField("CodPais", forma.getId())) {
				StringBuilder delete = new StringBuilder("DELETE FROM ")
						.append(nameTable).append(" WHERE CodPais = '")
						.append(forma.getId()).append("'");
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
}
