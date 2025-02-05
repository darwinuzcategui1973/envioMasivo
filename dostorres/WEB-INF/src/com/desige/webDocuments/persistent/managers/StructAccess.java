package com.desige.webDocuments.persistent.managers;

import java.util.ArrayList;
import java.util.TreeMap;

import sun.jdbc.rowset.CachedRowSet;

import com.desige.webDocuments.persistent.utils.JDBCUtil;

public class StructAccess {

	private TreeMap<String, StructWeb> struct = new TreeMap<String, StructWeb>();
	private String idGroup;
	private String idPerson;
	private StringBuffer query;
	private ArrayList parametros;

	public StructAccess() {

	}

	public StructAccess(String idGroup, String idPerson) {
		setIdGroup(idGroup);
		setIdPerson(idPerson);
		iniciar();
	}

	public void iniciar() {
		CachedRowSet crs = null;
		query = new StringBuffer();
		parametros = new ArrayList();

		try {
			if (idPerson != null) {
				parametros.add(idPerson);
				query.append("select idPerson from person where nameUser=?");
				crs = JDBCUtil.executeQuery(query, parametros, Thread.currentThread().getStackTrace()[1].getMethodName());
				crs.next();
				idPerson = crs.getString(1);
			}

			query = new StringBuffer();
			parametros = new ArrayList();

			parametros.add(idGroup != null ? idGroup : "0");
			parametros.add(idPerson != null ? idPerson : "0");

			query.append("select a.IdNode, a.IdNodeParent, b.idStruct grupo, c.idStruct usuario ");
			query.append("from (struct a left outer join permissionstructgroups b ON a.IdNode=b.idStruct and ?=b.idGroup and '1'=b.active and '1'=b.toView ) ");
			query.append("left outer join permissionstructuser c ON a.IdNode=c.idStruct and ?=c.idPerson and '1'=c.active and '1'=c.toView ");

			crs = JDBCUtil.executeQuery(query, parametros, Thread.currentThread().getStackTrace()[1].getMethodName());

			struct = new TreeMap<String, StructWeb>();
			while (crs.next()) {
				struct.put(crs.getString(1), new StructWeb(crs.getString(2), crs.getString(2), crs.getString(3), crs.getString(4)));
				//System.out.println(crs.getString(1) + " " + crs.getString(2) + " " + crs.getString(3) + " " + crs.getString(4));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public String getIdNodeSecurityGroup(String idNode) {
		if (struct.size() > 0) {
			while (!idNode.equals("0")) {
				StructWeb sw = (StructWeb) struct.get(idNode);
				if (sw.getIdGroup() != null) {
					break;
				}
				idNode = sw.getIdNodeParent();
			}
		} else {
			idNode = "0";
		}
		return idNode;
	}

	// Method get and set
	public String getIdGroup() {
		return idGroup;
	}

	public void setIdGroup(String idGroup) {
		this.idGroup = idGroup;
	}

	public String getIdPerson() {
		return idPerson;
	}

	public void setIdPerson(String idPerson) {
		this.idPerson = idPerson;
	}

	public static void main(String[] args) {
		StructAccess sa = new StructAccess("9", "24");

		System.out.print(sa.getIdNodeSecurityGroup("48"));

	}

}
