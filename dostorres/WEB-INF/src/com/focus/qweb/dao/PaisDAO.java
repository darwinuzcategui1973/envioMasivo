package com.focus.qweb.dao;

import java.sql.SQLException;
import java.util.ArrayList;

import sun.jdbc.rowset.CachedRowSet;

import com.desige.webDocuments.persistent.utils.JDBCUtil;
import com.focus.qweb.interfaces.ObjetoDAO;
import com.focus.qweb.interfaces.ObjetoTO;
import com.focus.qweb.to.PaisTO;

/**
 * 
 * @author JRivero
 * 
 */
public class PaisDAO implements ObjetoDAO {

	private StringBuffer query = new StringBuffer();
	private ArrayList<String> parametros = null;
	PaisTO paisTO = null;

	/**
	 * 
	 */
	public int insertar(ObjetoTO oPaisTO) throws Exception {
		paisTO = (PaisTO) oPaisTO;
		query.setLength(0);
		query.append("INSERT INTO pais (CodPais,Nombre,Codigo) ");
		query.append("VALUES(?,?,?)");
		
		parametros = new ArrayList<String>();
		parametros.add(paisTO.getId());
		parametros.add(paisTO.getName());
		parametros.add(paisTO.getCodigo());

		return JDBCUtil.executeUpdate(query, parametros);
	}

	/**
	 * 
	 */
	public int actualizar(ObjetoTO oPaisTO) throws Exception {
		paisTO = (PaisTO) oPaisTO;

		query.setLength(0);
		query.append("UPDATE pais SET ");
		query.append("Nombre=?,Codigo=? ");
		query.append("WHERE CodPais=?");

		parametros = new ArrayList<String>();
		parametros.add(paisTO.getName());
		parametros.add(paisTO.getCodigo());
		parametros.add(paisTO.getId()); // primary key


		return JDBCUtil.executeUpdate(query, parametros);
	}

	/**
	 * 
	 */
	public void eliminar(ObjetoTO oPaisTO) throws Exception {
		paisTO = (PaisTO) oPaisTO;

		query.setLength(0);
		query.append("DELETE FROM pais WHERE CodPais=?");

		parametros = new ArrayList<String>();
		parametros.add(paisTO.getId());

		JDBCUtil.executeUpdate(query, parametros);
	}

	/**
	 * 
	 */
	public boolean cargar(ObjetoTO oPaisTO) throws Exception {
		paisTO = (PaisTO) oPaisTO;

		query.setLength(0);
		query.append("SELECT * FROM pais WHERE CodPais=").append(paisTO.getId());

		CachedRowSet crs = JDBCUtil.executeQuery(query, Thread.currentThread().getStackTrace()[1].getMethodName());

		if (crs.next()) {
			load(crs, paisTO);
			return true;
		}
		return false;
	}

	/**
	 * 
	 */
	public ArrayList<PaisTO> listar() throws Exception {
		ArrayList<PaisTO> lista = new ArrayList<PaisTO>();

		query.setLength(0);
		query.append("SELECT * FROM pais ORDER BY CodPais");

		CachedRowSet crs = JDBCUtil.executeQuery(query, Thread.currentThread().getStackTrace()[1].getMethodName());

		while (crs.next()) {
			paisTO = new PaisTO();
			load(crs, paisTO);
			lista.add(paisTO);
		}
		return lista;
	}
	
	private void load(CachedRowSet crs, PaisTO paisTO) throws SQLException {
		paisTO.setId(crs.getString("CodPais"));
		paisTO.setName(crs.getString("Nombre"));
		paisTO.setCodigo(crs.getString("Codigo"));
	}

}
