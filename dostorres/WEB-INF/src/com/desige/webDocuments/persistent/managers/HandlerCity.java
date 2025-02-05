package com.desige.webDocuments.persistent.managers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Properties;
import java.util.Vector;

import com.desige.webDocuments.city.forms.BaseCityForm;
import com.desige.webDocuments.persistent.utils.JDBCUtil;
import com.desige.webDocuments.utils.ToolsHTML;
import com.desige.webDocuments.utils.beans.Search;

/**
 * Title: HandlerCity.java <br/>
 * Copyright: (c) 2004 Focus Consulting C.A.<br/>
 * 
 * @author Ing. Nelson Crespo (NC)
 * @version WebDocuments v3.0 <br/>
 *          Changes:<br/>
 *          <ul>
 *          <li>25/04/2004 (NC) Creation</li>
 *          </ul>
 */
public class HandlerCity extends HandlerBD {
	/**
	 * 
	 */
	private static final long serialVersionUID = -3494447483621533901L;
	public static final String nameTable = "ciudad";

	/**
	 * 
	 * @param descript
	 * @return
	 * @throws Exception
	 */
	public static Collection getAllCitys(String descript) throws Exception {
		StringBuilder sql = new StringBuilder("SELECT IdCiudad, Nombre FROM ")
				.append(nameTable);
		if (ToolsHTML.checkValue(descript)) {
			sql.append(" WHERE Nombre LIKE '%").append(descript).append("%'");
		}
		sql.append(" ORDER BY lower(Nombre) ");
		ArrayList resp = new ArrayList();
		Vector datos = JDBCUtil.doQueryVector(sql.toString(),Thread.currentThread().getStackTrace()[1].getMethodName());
		for (int row = 0; row < datos.size(); row++) {
			Properties properties = (Properties) datos.elementAt(row);
			Search bean = new Search(properties.getProperty("IdCiudad"),
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
	public static String getFieldCity(String field, String cod)
			throws Exception {
		StringBuilder sql = new StringBuilder("SELECT ").append(field)
				.append(" FROM ").append(nameTable).append(" WHERE IdCiudad='")
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
	public static void load(BaseCityForm forma) throws Exception {
		StringBuilder sql = new StringBuilder("SELECT * FROM ")
				.append(nameTable).append(" WHERE IdCiudad = '")
				.append(forma.getId()).append("'");
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
	public synchronized static boolean edit(BaseCityForm forma) {
		try {
			StringBuilder edit = new StringBuilder("UPDATE ").append(nameTable)
					.append(" SET Nombre='").append(forma.getName())
					.append("'").append(" WHERE IdCiudad = '")
					.append(forma.getId()).append("'");
			return JDBCUtil.doUpdate(edit.toString()) > 0;
		} catch (Exception e) {
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
	public synchronized static boolean insert(BaseCityForm forma) {
		try {
			// int num = IDDBFactorySql.getNextID("city");
			int num = HandlerStruct.proximo("city", "ciudad", "IdCiudad");

			StringBuilder insert = new StringBuilder("INSERT INTO ")
					.append(nameTable).append(" (IdCiudad,Nombre) VALUES('")
					.append(num).append("','").append(forma.getName())
					.append("')");
			// System.out.println("insert = " + insert);
			return JDBCUtil.doUpdate(insert.toString()) > 0;
		} catch (Exception e) {
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
	public synchronized static boolean delete(BaseCityForm forma) {
		try {
			if (!HandlerPerfil.isCodInField("Ciudad", forma.getId())) {
				StringBuilder delete = new StringBuilder("DELETE FROM ")
						.append(nameTable).append(" WHERE IdCiudad = '")
						.append(forma.getId()).append("'");
				return JDBCUtil.doUpdate(delete.toString()) > 0;
			} else {
				setMensaje("");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
}
