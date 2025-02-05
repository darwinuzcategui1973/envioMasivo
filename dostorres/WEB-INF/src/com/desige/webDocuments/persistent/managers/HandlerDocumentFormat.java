package com.desige.webDocuments.persistent.managers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Properties;
import java.util.Vector;

import com.desige.webDocuments.persistent.utils.JDBCUtil;
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

public class HandlerDocumentFormat extends HandlerBD {
	public static final String nameTable = "documents";

	/**
	 * 
	 * @param type
	 * @return
	 * @throws Exception
	 */
	public static Collection getAllDocuments(String type) throws Exception {
		StringBuilder sql = new StringBuilder(
				"SELECT numGen, nameDocument FROM ")
				.append(nameTable)
				.append(" WHERE statu = '")
				.append(HandlerDocuments.docApproved)
				.append("'")
				.append(" AND active = '")
				.append(HandlerDocuments.docActives)
				.append("'")
				.append(" AND CAST(type AS INT) IN (select idTypeDoc from typedocuments where type_Formato=")
				.append(type).append(")")
				.append(" ORDER BY lower(nameDocument) ");
		ArrayList resp = new ArrayList();
		Vector datos = JDBCUtil.doQueryVector(sql.toString(),Thread.currentThread().getStackTrace()[1].getMethodName());
		for (int row = 0; row < datos.size(); row++) {
			Properties properties = (Properties) datos.elementAt(row);
			Search bean = new Search(properties.getProperty("numGen"),
					properties.getProperty("nameDocument"));
			resp.add(bean);
		}
		return resp;
	}
}
