package com.focus.qweb.dao;

import java.sql.SQLException;
import java.util.ArrayList;

import sun.jdbc.rowset.CachedRowSet;

import com.desige.webDocuments.persistent.utils.JDBCUtil;
import com.desige.webDocuments.utils.Constants;
import com.desige.webDocuments.utils.ToolsHTML;
import com.focus.qweb.interfaces.ObjetoDAO;
import com.focus.qweb.interfaces.ObjetoTO;
import com.focus.qweb.to.TitulosPlanillasSacopTO;

/**
 * 
 * @author JRivero
 * 
 */
public class TitulosPlanillasSacopDAO implements ObjetoDAO {

	private StringBuffer query = new StringBuffer();
	private ArrayList<Object> parametros = null;
	TitulosPlanillasSacopTO titulosPlanillasSacopTO = null;

	/**
	 * 
	 */
	public int insertar(ObjetoTO oTitulosPlanillasSacopTO) throws Exception {
		titulosPlanillasSacopTO = (TitulosPlanillasSacopTO) oTitulosPlanillasSacopTO;

		query.setLength(0);
		query.append("INSERT INTO tbl_titulosplanillassacop (numtitulosplanillas,titulosplanillas,active) ");
		query.append("VALUES(?,?,?)");
		
		parametros = new ArrayList<Object>();
		parametros.add(titulosPlanillasSacopTO.getNumtitulosplanillasInt());
		parametros.add(titulosPlanillasSacopTO.getTitulosplanillas());
		parametros.add(titulosPlanillasSacopTO.getActiveInt());

		return JDBCUtil.executeUpdate(query, parametros);
	}

	/**
	 * 
	 */
	public int actualizar(ObjetoTO oTitulosPlanillasSacopTO) throws Exception {
		titulosPlanillasSacopTO = (TitulosPlanillasSacopTO) oTitulosPlanillasSacopTO;

		query.setLength(0);
		query.append("UPDATE tbl_titulosplanillassacop SET ");
		query.append("titulosplanillas=?,active=? ");
		query.append("WHERE numtitulosplanillas=?");

		parametros = new ArrayList<Object>();
		parametros.add(titulosPlanillasSacopTO.getTitulosplanillas());
		parametros.add(titulosPlanillasSacopTO.getActiveInt());
		parametros.add(titulosPlanillasSacopTO.getNumtitulosplanillasInt()); // Primary Key


		return JDBCUtil.executeUpdate(query, parametros);
	}

	/**
	 * 
	 */
	public void eliminar(ObjetoTO oTitulosPlanillasSacopTO) throws Exception {
		titulosPlanillasSacopTO = (TitulosPlanillasSacopTO) oTitulosPlanillasSacopTO;

		query.setLength(0);
		query.append("DELETE FROM tbl_titulosplanillassacop WHERE numtitulosplanillas=?");

		parametros = new ArrayList<Object>();
		parametros.add(titulosPlanillasSacopTO.getNumtitulosplanillasInt());

		JDBCUtil.executeUpdate(query, parametros);
	}

	/**
	 * 
	 */
	public boolean cargar(ObjetoTO oTitulosPlanillasSacopTO) throws Exception {
		titulosPlanillasSacopTO = (TitulosPlanillasSacopTO) oTitulosPlanillasSacopTO;

		query.setLength(0);
		query.append("SELECT * FROM tbl_titulosplanillassacop WHERE numtitulosplanillas=").append(titulosPlanillasSacopTO.getNumtitulosplanillasInt());

		CachedRowSet crs = JDBCUtil.executeQuery(query, Thread.currentThread().getStackTrace()[1].getMethodName());

		if (crs.next()) {
			load(crs, titulosPlanillasSacopTO);
			return true;
		}
		return false;
	}

	/**
	 * 
	 */
	public ArrayList<TitulosPlanillasSacopTO> listar() throws Exception {
		ArrayList<TitulosPlanillasSacopTO> lista = new ArrayList<TitulosPlanillasSacopTO>();

		query.setLength(0);
		query.append("SELECT * FROM tbl_titulosplanillassacop ORDER BY numtitulosplanillas");

		CachedRowSet crs = JDBCUtil.executeQuery(query, Thread.currentThread().getStackTrace()[1].getMethodName());

		while (crs.next()) {
			titulosPlanillasSacopTO = new TitulosPlanillasSacopTO();
			load(crs, titulosPlanillasSacopTO);
			lista.add(titulosPlanillasSacopTO);
		}
		return lista;
	}
	
	/**
	 * 
	 */
	public ArrayList<TitulosPlanillasSacopTO> listarActive() throws Exception {
		ArrayList<TitulosPlanillasSacopTO> lista = new ArrayList<TitulosPlanillasSacopTO>();

		query.setLength(0);
		query.append("SELECT * FROM tbl_titulosplanillassacop WHERE active=1 ORDER BY titulosplanillas");

		CachedRowSet crs = JDBCUtil.executeQuery(query, Thread.currentThread().getStackTrace()[1].getMethodName());

		while (crs.next()) {
			titulosPlanillasSacopTO = new TitulosPlanillasSacopTO();
			load(crs, titulosPlanillasSacopTO);
			lista.add(titulosPlanillasSacopTO);
		}
		return lista;
	}
	
	
	
	/**
	 * 
	 */
	public ArrayList<TitulosPlanillasSacopTO> listar(String titulo) throws Exception {
		ArrayList<TitulosPlanillasSacopTO> lista = new ArrayList<TitulosPlanillasSacopTO>();

		query.setLength(0);
		query.append("SELECT * FROM tbl_titulosplanillassacop ");
		if(!ToolsHTML.isEmptyOrNull(titulo)) {
			query.append("WHERE titulosplanillas='").append(titulo).append("' ");
		} else {
			query.append("WHERE active=").append(Constants.permissionSt).append(" ");
		}
		query.append("ORDER BY lower(titulosplanillas) ");

		CachedRowSet crs = JDBCUtil.executeQuery(query, Thread.currentThread().getStackTrace()[1].getMethodName());

		while (crs.next()) {
			titulosPlanillasSacopTO = new TitulosPlanillasSacopTO();
			load(crs, titulosPlanillasSacopTO);
			lista.add(titulosPlanillasSacopTO);
		}
		return lista;
	}
	
	
	private void load(CachedRowSet crs, TitulosPlanillasSacopTO titulosPlanillasSacopTO) throws SQLException {
		titulosPlanillasSacopTO.setNumtitulosplanillas(crs.getString("numtitulosplanillas"));
		titulosPlanillasSacopTO.setTitulosplanillas(crs.getString("titulosplanillas"));
		titulosPlanillasSacopTO.setActive(crs.getString("active"));
	}

}
