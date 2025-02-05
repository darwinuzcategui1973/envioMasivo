package com.focus.qweb.dao;

import java.sql.SQLException;
import java.util.ArrayList;

import sun.jdbc.rowset.CachedRowSet;

import com.desige.webDocuments.persistent.utils.JDBCUtil;
import com.focus.qweb.interfaces.ObjetoDAO;
import com.focus.qweb.interfaces.ObjetoTO;
import com.focus.qweb.to.DeleteSacopTO;

/**
 * 
 * @author JRivero
 * 
 */
public class DeleteSacopDAO implements ObjetoDAO {

	private StringBuffer query = new StringBuffer();
	private ArrayList<Object> parametros = null;
	DeleteSacopTO deleteSacopTO = null;

	/**
	 * 
	 */
	public int insertar(ObjetoTO oDeleteSacopTO) throws Exception {
		deleteSacopTO = (DeleteSacopTO) oDeleteSacopTO;

		query.setLength(0);
		query.append("INSERT INTO tbl_deletesacop (id,idplanillasacop1,idperson,active) ");
		query.append("VALUES(?,?,?,?)");

		parametros = new ArrayList<Object>();
		parametros.add(deleteSacopTO.getId());
		parametros.add(deleteSacopTO.getIdplanillasacop1());
		parametros.add(deleteSacopTO.getIdperson());
		parametros.add(deleteSacopTO.getActive());

		return JDBCUtil.executeUpdate(query, parametros);
	}

	/**
	 * 
	 */
	public int actualizar(ObjetoTO oDeleteSacopTO) throws Exception {
		deleteSacopTO = (DeleteSacopTO) oDeleteSacopTO;

		query.setLength(0);
		query.append("UPDATE tbl_deletesacop SET ");
		query.append("idplanillasacop1=?,idperson=?,active=? ");
		query.append("WHERE id=?");

		parametros = new ArrayList<Object>();
		parametros.add(deleteSacopTO.getIdplanillasacop1());
		parametros.add(deleteSacopTO.getIdperson());
		parametros.add(deleteSacopTO.getActive());
		parametros.add(deleteSacopTO.getId()); // primary key

		return JDBCUtil.executeUpdate(query, parametros);
	}

	/**
	 * 
	 */
	public void eliminar(ObjetoTO oDeleteSacopTO) throws Exception {
		deleteSacopTO = (DeleteSacopTO) oDeleteSacopTO;

		query.setLength(0);
		query.append("DELETE FROM tbl_deletesacop WHERE id=?");

		parametros = new ArrayList<Object>();
		parametros.add(deleteSacopTO.getId());

		JDBCUtil.executeUpdate(query, parametros);
	}

	/**
	 * 
	 */
	public boolean cargar(ObjetoTO oDeleteSacopTO) throws Exception {
		deleteSacopTO = (DeleteSacopTO) oDeleteSacopTO;

		query.setLength(0);
		query.append("SELECT * FROM tbl_deletesacop WHERE id=").append(deleteSacopTO.getId());

		CachedRowSet crs = JDBCUtil.executeQuery(query, Thread.currentThread().getStackTrace()[1].getMethodName());

		if (crs.next()) {
			load(crs, deleteSacopTO);
			return true;
		}
		return false;
	}

	/**
	 * 
	 */
	public ArrayList<DeleteSacopTO> listar() throws Exception {
		ArrayList<DeleteSacopTO> lista = new ArrayList<DeleteSacopTO>();

		query.setLength(0);
		query.append("SELECT * FROM tbl_deletesacop ORDER BY id");

		CachedRowSet crs = JDBCUtil.executeQuery(query, Thread.currentThread().getStackTrace()[1].getMethodName());

		while (crs.next()) {
			deleteSacopTO = new DeleteSacopTO();
			load(crs, deleteSacopTO);
			lista.add(deleteSacopTO);
		}
		return lista;
	}
	
	private void load(CachedRowSet crs, DeleteSacopTO deleteSacopTO) throws SQLException {
		deleteSacopTO.setId(crs.getString("id"));
		deleteSacopTO.setIdplanillasacop1(crs.getString("idplanillasacop1"));
		deleteSacopTO.setIdperson(crs.getString("idperson"));
		deleteSacopTO.setActive(crs.getString("active"));
	}

}
