package com.focus.qweb.dao;

import java.sql.SQLException;
import java.util.ArrayList;

import sun.jdbc.rowset.CachedRowSet;

import com.desige.webDocuments.persistent.utils.JDBCUtil;
import com.focus.qweb.interfaces.ObjetoDAO;
import com.focus.qweb.interfaces.ObjetoTO;
import com.focus.qweb.to.ProcesoSacopTO;

/**
 * 
 * @author JRivero
 * 
 */
public class ProcesoSacopDAO implements ObjetoDAO {

	private StringBuffer query = new StringBuffer();
	private ArrayList<Object> parametros = null;
	ProcesoSacopTO procesoSacopTO = null;

	/**
	 * 
	 */
	public int insertar(ObjetoTO oProcesoSacopTO) throws Exception {
		procesoSacopTO = (ProcesoSacopTO) oProcesoSacopTO;

		query.setLength(0);
		query.append("INSERT INTO tbl_procesosacop (numproceso,proceso,active) ");
		query.append("VALUES(?,?,?)");
		
		parametros = new ArrayList<Object>();
		parametros.add(procesoSacopTO.getNumprocesoInt());
		parametros.add(procesoSacopTO.getProceso());
		parametros.add(procesoSacopTO.getActiveInt());

		return JDBCUtil.executeUpdate(query, parametros);
	}

	/**
	 * 
	 */
	public int actualizar(ObjetoTO oProcesoSacopTO) throws Exception {
		procesoSacopTO = (ProcesoSacopTO) oProcesoSacopTO;

		query.setLength(0);
		query.append("UPDATE tbl_procesosacop SET ");
		query.append("proceso=?,active=? ");
		query.append("WHERE numproceso=?");

		parametros = new ArrayList<Object>();
		parametros.add(procesoSacopTO.getProceso());
		parametros.add(procesoSacopTO.getActiveInt());
		parametros.add(procesoSacopTO.getNumprocesoInt()); // primary key


		return JDBCUtil.executeUpdate(query, parametros);
	}

	/**
	 * 
	 */
	public void eliminar(ObjetoTO oProcesoSacopTO) throws Exception {
		procesoSacopTO = (ProcesoSacopTO) oProcesoSacopTO;

		query.setLength(0);
		query.append("DELETE FROM tbl_procesosacop WHERE numproceso=?");

		parametros = new ArrayList<Object>();
		parametros.add(procesoSacopTO.getNumprocesoInt());

		JDBCUtil.executeUpdate(query, parametros);
	}

	/**
	 * 
	 */
	public boolean cargar(ObjetoTO oProcesoSacopTO) throws Exception {
		procesoSacopTO = (ProcesoSacopTO) oProcesoSacopTO;

		query.setLength(0);
		query.append("SELECT * FROM tbl_procesosacop WHERE numproceso=").append(procesoSacopTO.getNumprocesoInt());

		CachedRowSet crs = JDBCUtil.executeQuery(query, Thread.currentThread().getStackTrace()[1].getMethodName());

		if (crs.next()) {
			load(crs, procesoSacopTO);
			return true;
		}
		return false;
	}

	/**
	 * 
	 */
	public ArrayList<ProcesoSacopTO> listar() throws Exception {
		ArrayList<ProcesoSacopTO> lista = new ArrayList<ProcesoSacopTO>();

		query.setLength(0);
		query.append("SELECT * FROM tbl_procesosacop ORDER BY IdProcesoSacop");

		CachedRowSet crs = JDBCUtil.executeQuery(query, Thread.currentThread().getStackTrace()[1].getMethodName());

		while (crs.next()) {
			procesoSacopTO = new ProcesoSacopTO();
			load(crs, procesoSacopTO);
			lista.add(procesoSacopTO);
		}
		return lista;
	}
	
	private void load(CachedRowSet crs, ProcesoSacopTO procesoSacopTO) throws SQLException {
		procesoSacopTO.setNumproceso(crs.getString("numproceso"));
		procesoSacopTO.setProceso(crs.getString("proceso"));
		procesoSacopTO.setActive(crs.getString("active"));
	}

}
