package com.desige.webDocuments.dao;

import java.io.Serializable;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Collection;
import java.util.TreeMap;

import sun.jdbc.rowset.CachedRowSet;

import com.desige.webDocuments.files.forms.ExpedienteForm;
import com.desige.webDocuments.files.forms.FilesForm;
import com.desige.webDocuments.persistent.utils.JDBCUtil;
import com.desige.webDocuments.utils.StringUtil;
import com.desige.webDocuments.utils.ToolsHTML;
import com.desige.webDocuments.utils.beans.Users;

public class ExpedienteHistoryDAO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7180819915786294061L;
	private CachedRowSet crs = null;
	private StringBuffer query = new StringBuffer();
	private StringBuffer insert1 = new StringBuffer();
	private StringBuffer insert2 = new StringBuffer();
	private ConfExpedienteDAO conf;

	private static int campos = 0;

	public ExpedienteHistoryDAO() {
		conf = new ConfExpedienteDAO();
	}

	/**
	 * 
	 * @return El numero de campos configurados por el usuario
	 * @throws Exception
	 */
	public int getCampos() throws Exception {
		campos = conf.countRegister();
		return campos;
	}

	public void save(ExpedienteForm files, Users usuario, Connection cone) throws Exception {
		String sep = "";
		String coma = ",";
		ArrayList<Object> parametro = new ArrayList<Object>();
		ArrayList lista = (ArrayList) conf.findAll();
		TreeMap<String, FilesForm> mapa = new TreeMap<String, FilesForm>();
		FilesForm obj;
		String col = "";
		Object valor = null;
		boolean cambios = false;

		for (int i = 0; i < lista.size(); i++) {
			obj = (FilesForm) lista.get(i);
			mapa.put(obj.getId(), obj);
		}

		int c = getCampos();
		for (int i = 3; i <= c; i++) {
			col = "f".concat(String.valueOf(i));
			valor = ToolsHTML.isEmptyOrNull(files.get(col.toUpperCase()), "");
			;
			obj = mapa.get(col);

			insert1.append(sep);
			insert2.append(sep);
			insert1.append("f").append(i);
			insert2.append("?");

			parametro.add(valor);
			sep = coma;
		}

		int act = -1;

		parametro.add(files.getF1());
		parametro.add(files.getFilesVersion());

		query.setLength(0);
		query.append(StringUtil.replace("insert into expediente_history (P1,f1,filesVersion) values(P2,?,?)", new String[] { insert1.toString(), insert2.toString() }));

		act = JDBCUtil.executeUpdate(query, parametro, cone);

	}

	public ExpedienteForm findById(ExpedienteForm files) throws Exception {
		ArrayList<Integer> parametro = new ArrayList<Integer>();

		parametro.add(files.getF1());
		parametro.add(files.getFilesVersion());
		query.setLength(0);
		query.append("select * from expediente_history where f1=? and filesVersion=?");

		crs = JDBCUtil.executeQuery(query, parametro, Thread.currentThread().getStackTrace()[1].getMethodName());

		while (crs.next()) {
			files.setF2(crs.getDate("f2"));
			int c = getCampos();
			for (int i = 3; i <= c; i++) {
				files.set("F".concat(String.valueOf(i)), crs.getString("f".concat(String.valueOf(i))));
			}
		}

		return files;
	}

	public Collection findAll(ExpedienteForm files) throws Exception {
		Collection<ExpedienteForm> lista = new ArrayList<ExpedienteForm>();
		ArrayList<Object> parametros = new ArrayList<Object>();
		parametros.add(files.getF1());

		query.setLength(0);
		query.append("select * from expediente_history where f1=? order by f1");
		crs = JDBCUtil.executeQuery(query, Thread.currentThread().getStackTrace()[1].getMethodName());

		ExpedienteForm f = new ExpedienteForm();
		while (crs.next()) {
			f = new ExpedienteForm();
			f.setF1(crs.getInt("f1"));
			f.setFilesVersion(crs.getInt("filesVersion"));
			f.setF2(crs.getDate("f2"));
			int c = getCampos();
			for (int i = 3; i <= c; i++) {
				f.set("F".concat(String.valueOf(i)), crs.getString("f".concat(String.valueOf(i))));
			}
			lista.add(f);
		}

		return lista;
	}

}
