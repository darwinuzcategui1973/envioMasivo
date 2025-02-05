package com.desige.webDocuments.persistent.managers;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Locale;
import java.util.Properties;
import java.util.Vector;

import com.desige.webDocuments.persistent.utils.JDBCUtil;
import com.desige.webDocuments.utils.beans.Search;

/**
 * Title: HandlerIdiomas.java <br/>
 * Copyright: (c) 2004 Focus Consulting C.A.<br/>
 * 
 * @author Ing. Nelson Crespo (NC)
 * @version WebDocuments v3.0 <br/>
 *          Changes:<br/>
 *          <ul>
 *          <li>29/03/2004 (NC) Creation</li>
 *          </ul>
 */
public class HandlerIdiomas implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1279687675007168943L;

	/**
	 * 
	 * @return
	 * @throws Exception
	 */
	public static Collection getAllLanguage() throws Exception {
		String sql = "SELECT * FROM idiomas";
		// Se agrego filtro para obtener solo Español 19/11/2007
		// sql += " WHERE codigo = 'es' ";
		ArrayList resp = new ArrayList();
		Vector datos = JDBCUtil.doQueryVector(sql,Thread.currentThread().getStackTrace()[1].getMethodName());
		for (int row = 0; row < datos.size(); row++) {
			Properties properties = (Properties) datos.elementAt(row);
			Search bean = new Search(properties.getProperty("id"),
					properties.getProperty("description"));
			resp.add(bean);
		}
		return resp;
	}
}
