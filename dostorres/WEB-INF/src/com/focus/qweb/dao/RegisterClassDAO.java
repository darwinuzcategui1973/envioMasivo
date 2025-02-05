package com.focus.qweb.dao;

import java.util.HashMap;

import com.desige.webDocuments.persistent.utils.JDBCUtil;
import com.desige.webDocuments.utils.Constants;

import sun.jdbc.rowset.CachedRowSet;

/**
 * 
 * @author JRivero
 * 
 */
public class RegisterClassDAO  {



	/**
	 * Recarga el vector estatico que esta en la clase Constants
	 */
	public static void recargarRegisterClass() {
		StringBuffer query = new StringBuffer();

		Constants.registerclassTable = new HashMap<Integer, String>(); 

		query.setLength(0);
		query.append("SELECT * FROM registerclass ORDER BY idRegisterclass");

		CachedRowSet crs;
		try {
		
			crs = JDBCUtil.executeQuery(query, Thread.currentThread().getStackTrace()[1].getMethodName());

			while (crs.next()) {
				Constants.registerclassTable.put(crs.getInt(1), crs.getString(2));
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	
}
