package com.focus.qweb.dao;

import java.sql.SQLException;
import java.util.ArrayList;

import sun.jdbc.rowset.CachedRowSet;

import com.desige.webDocuments.persistent.utils.JDBCUtil;
import com.focus.qweb.interfaces.ObjetoDAO;
import com.focus.qweb.interfaces.ObjetoTO;
import com.focus.qweb.to.CiudadTO;

/**
 * 
 * @author JRivero
 * 
 */
public class CiudadDAO implements ObjetoDAO {

	private StringBuffer query = new StringBuffer();
	private ArrayList<String> parametros = null;
	CiudadTO ciudadTO = null;

	/**
	 * 
	 */
	public int insertar(ObjetoTO oCiudadTO) throws Exception {
		ciudadTO = (CiudadTO) oCiudadTO;

		query.setLength(0);
		query.append("INSERT INTO ciudad (IdCiudad, nombre) ");
		query.append("VALUES(?,?)");
		
		parametros = new ArrayList<String>();
		parametros.add(ciudadTO.getId());
		parametros.add(ciudadTO.getName());

		return JDBCUtil.executeUpdate(query, parametros);
	}

	/**
	 * 
	 */
	public int actualizar(ObjetoTO oCiudadTO) throws Exception {
		ciudadTO = (CiudadTO) oCiudadTO;

		query.setLength(0);
		query.append("UPDATE ciudad SET ");
		query.append("nombre=? ");
		query.append("WHERE IdCiudad=?");

		parametros = new ArrayList<String>();
		parametros.add(ciudadTO.getName());
		parametros.add(ciudadTO.getId()); // primary key


		return JDBCUtil.executeUpdate(query, parametros);
	}

	/**
	 * 
	 */
	public void eliminar(ObjetoTO oCiudadTO) throws Exception {
		ciudadTO = (CiudadTO) oCiudadTO;

		query.setLength(0);
		query.append("DELETE FROM ciudad WHERE IdCiudad=?");

		parametros = new ArrayList<String>();
		parametros.add(ciudadTO.getId());

		JDBCUtil.executeUpdate(query, parametros);
	}

	/**
	 * 
	 */
	public boolean cargar(ObjetoTO oCiudadTO) throws Exception {
		ciudadTO = (CiudadTO) oCiudadTO;

		query.setLength(0);
		query.append("SELECT * FROM ciudad WHERE IdCiudad=").append(ciudadTO.getId());

		CachedRowSet crs = JDBCUtil.executeQuery(query, Thread.currentThread().getStackTrace()[1].getMethodName());

		if (crs.next()) {
			load(crs, ciudadTO);
			return true;
		}
		return false;
	}

	/**
	 * 
	 */
	public ArrayList<CiudadTO> listar() throws Exception {
		ArrayList<CiudadTO> lista = new ArrayList<CiudadTO>();

		query.setLength(0);
		query.append("SELECT * FROM ciudad ORDER BY IdCiudad");

		CachedRowSet crs = JDBCUtil.executeQuery(query, Thread.currentThread().getStackTrace()[1].getMethodName());

		while (crs.next()) {
			ciudadTO = new CiudadTO();
			load(crs, ciudadTO);
			lista.add(ciudadTO);
		}
		return lista;
	}
	
	private void load(CachedRowSet crs, CiudadTO ciudadTO) throws SQLException {
		ciudadTO.setId(crs.getString("IdCiudad"));
		ciudadTO.setName(crs.getString("nombre"));
	}

}
