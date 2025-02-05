package com.focus.qweb.dao;

import java.sql.SQLException;
import java.util.ArrayList;

import com.desige.webDocuments.persistent.managers.HandlerStruct;
import com.desige.webDocuments.persistent.utils.JDBCUtil;
import com.focus.qweb.interfaces.ObjetoDAO;
import com.focus.qweb.interfaces.ObjetoTO;
import com.focus.qweb.to.ActiveFactoryTO;

import sun.jdbc.rowset.CachedRowSet;

/**
 * 
 * @author JRivero
 * 
 */
public class ActiveFactoryDAO implements ObjetoDAO {

	private StringBuffer query = new StringBuffer();
	private ArrayList<Object> parametros = null;
	ActiveFactoryTO activeFactoryTO = null;

	/**
	 * 
	 */
	public int insertar(ObjetoTO oActiveFactoryTO) throws Exception {
		activeFactoryTO = (ActiveFactoryTO) oActiveFactoryTO;
		
		int id = activeFactoryTO.getIdActivefactorydocumentInt();

		if(id==0) {
			int num = HandlerStruct.proximo("idactivefactorydocument", "idactivefactorydocument", "idactivefactorydocument");
			activeFactoryTO.setIdActivefactorydocument(String.valueOf(num));
		}

		query.setLength(0);
		query.append("INSERT INTO tbl_activefactory (idactivefactorydocument,descripcion,url,active,numgen) ");
		query.append("VALUES(?,?,?,?,?)");
		
		parametros = new ArrayList<Object>();
		parametros.add(activeFactoryTO.getIdActivefactorydocumentInt());
		parametros.add(activeFactoryTO.getDescripcion());
		parametros.add(activeFactoryTO.getUrl());
		parametros.add(activeFactoryTO.getActiveInt());
		parametros.add(activeFactoryTO.getNumgen());

		return JDBCUtil.executeUpdate(query, parametros);
	}

	/**
	 * 
	 */
	public int actualizar(ObjetoTO oActiveFactoryTO) throws Exception {
		activeFactoryTO = (ActiveFactoryTO) oActiveFactoryTO;

		query.setLength(0);
		query.append("UPDATE tbl_activefactory SET ");
		query.append("descripcion=?,url=?,active=?,numgen=? ");
		query.append("WHERE idactivefactorydocument=?");

		parametros = new ArrayList<Object>();
		parametros.add(activeFactoryTO.getDescripcion());
		parametros.add(activeFactoryTO.getUrl());
		parametros.add(activeFactoryTO.getActiveInt());
		parametros.add(activeFactoryTO.getNumgen());
		parametros.add(activeFactoryTO.getIdActivefactorydocumentInt()); // primary key


		return JDBCUtil.executeUpdate(query, parametros);
	}

	/**
	 * 
	 */
	public void eliminar(ObjetoTO oActiveFactoryTO) throws Exception {
		activeFactoryTO = (ActiveFactoryTO) oActiveFactoryTO;

		query.setLength(0);
		query.append("DELETE FROM tbl_activefactory WHERE idactivefactorydocument=?");

		parametros = new ArrayList<Object>();
		parametros.add(activeFactoryTO.getIdActivefactorydocumentInt());

		JDBCUtil.executeUpdate(query, parametros);
	}

	/**
	 * 
	 */
	public boolean cargar(ObjetoTO oActiveFactoryTO) throws Exception {
		activeFactoryTO = (ActiveFactoryTO) oActiveFactoryTO;

		query.setLength(0);
		query.append("SELECT * FROM tbl_activefactory WHERE idactivefactorydocument=").append(activeFactoryTO.getIdActivefactorydocumentInt());

		CachedRowSet crs = JDBCUtil.executeQuery(query, Thread.currentThread().getStackTrace()[1].getMethodName());

		if (crs.next()) {
			load(crs, activeFactoryTO);
			return true;
		}
		return false;
	}

	public boolean cargarByNumGen(ObjetoTO oActiveFactoryTO) throws Exception {
		activeFactoryTO = (ActiveFactoryTO) oActiveFactoryTO;

		query.setLength(0);
		query.append("SELECT * FROM tbl_activefactory WHERE numgen=?");

		parametros = new ArrayList<Object>();
		parametros.add(activeFactoryTO.getNumgen());

		CachedRowSet crs = JDBCUtil.executeQuery(query, parametros, Thread.currentThread().getStackTrace()[1].getMethodName());

		if (crs.next()) {
			load(crs, activeFactoryTO);
			return true;
		}
		return false;
	}

	/**
	 * 
	 */
	public ArrayList<ActiveFactoryTO> listar() throws Exception {
		ArrayList<ActiveFactoryTO> lista = new ArrayList<ActiveFactoryTO>();

		query.setLength(0);
		query.append("SELECT * FROM tbl_activefactory ORDER BY idactivefactorydocument");

		CachedRowSet crs = JDBCUtil.executeQuery(query, Thread.currentThread().getStackTrace()[1].getMethodName());

		while (crs.next()) {
			activeFactoryTO = new ActiveFactoryTO();
			load(crs, activeFactoryTO);
			lista.add(activeFactoryTO);
		}
		return lista;
	}
	
	/**
	 * 
	 */
	public ArrayList<ActiveFactoryTO> listarAlls() throws Exception {
		ArrayList<ActiveFactoryTO> lista = new ArrayList<ActiveFactoryTO>();

		query.setLength(0);
		query.append("SELECT * FROM tbl_activefactory ORDER BY lower(descripcion)");

		CachedRowSet crs = JDBCUtil.executeQuery(query, Thread.currentThread().getStackTrace()[1].getMethodName());

		while (crs.next()) {
			activeFactoryTO = new ActiveFactoryTO();
			load(crs, activeFactoryTO);
			lista.add(activeFactoryTO);
		}
		return lista;
	}

	/**
	 * 
	 */
	public ArrayList<ActiveFactoryTO> listarAlls(String numgen) throws Exception {
		ArrayList<ActiveFactoryTO> lista = new ArrayList<ActiveFactoryTO>();

		query.setLength(0);
		query.append("SELECT * FROM tbl_activefactory ");
		query.append("WHERE numgen='").append(numgen.trim()).append("' ");
		query.append("ORDER BY lower(descripcion)");

		CachedRowSet crs = JDBCUtil.executeQuery(query, Thread.currentThread().getStackTrace()[1].getMethodName());

		while (crs.next()) {
			activeFactoryTO = new ActiveFactoryTO();
			load(crs, activeFactoryTO);
			lista.add(activeFactoryTO);
		}
		return lista;
	}

	private void load(CachedRowSet crs, ActiveFactoryTO activeFactoryTO) throws SQLException {
		activeFactoryTO.setIdActivefactorydocument(crs.getString("idactivefactorydocument"));
		activeFactoryTO.setDescripcion(crs.getString("descripcion"));
		activeFactoryTO.setUrl(crs.getString("url"));
		activeFactoryTO.setActive(crs.getString("active"));
		activeFactoryTO.setNumgen(crs.getString("numgen"));
	}

}
