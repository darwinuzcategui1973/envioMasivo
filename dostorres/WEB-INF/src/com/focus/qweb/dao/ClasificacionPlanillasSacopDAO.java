package com.focus.qweb.dao;

import java.sql.SQLException;
import java.util.ArrayList;

import sun.jdbc.rowset.CachedRowSet;

import com.desige.webDocuments.persistent.utils.JDBCUtil;
import com.desige.webDocuments.utils.Constants;
import com.desige.webDocuments.utils.ToolsHTML;
import com.focus.qweb.interfaces.ObjetoDAO;
import com.focus.qweb.interfaces.ObjetoTO;
import com.focus.qweb.to.ClasificacionPlanillasSacopTO;
import com.focus.qweb.to.TitulosPlanillasSacopTO;

/**
 * 
 * @author JRivero
 * 
 */
public class ClasificacionPlanillasSacopDAO implements ObjetoDAO {

	private StringBuffer query = new StringBuffer();
	private ArrayList<Object> parametros = null;
	ClasificacionPlanillasSacopTO clasificacionPlanillasSacopTO = null;

	/**
	 * 
	 */
	public int insertar(ObjetoTO oClasificacionPlanillasSacopTO) throws Exception {
		clasificacionPlanillasSacopTO = (ClasificacionPlanillasSacopTO) oClasificacionPlanillasSacopTO;

		query.setLength(0);
		query.append("INSERT INTO tbl_clasificacionplanillassacop (id,descripcion,active) ");
		query.append("VALUES(?,?,?)");
		
		parametros = new ArrayList<Object>();
		parametros.add(clasificacionPlanillasSacopTO.getIdInt()); // primary key
		parametros.add(clasificacionPlanillasSacopTO.getDescripcion());
		parametros.add(clasificacionPlanillasSacopTO.getActiveInt());

		return JDBCUtil.executeUpdate(query, parametros);
	}

	/**
	 * 
	 */
	public int actualizar(ObjetoTO oClasificacionPlanillasSacopTO) throws Exception {
		clasificacionPlanillasSacopTO = (ClasificacionPlanillasSacopTO) oClasificacionPlanillasSacopTO;

		query.setLength(0);
		query.append("UPDATE tbl_clasificacionplanillassacop SET ");
		query.append("descripcion=?,active=? ");
		query.append("WHERE id=?");

		parametros = new ArrayList<Object>();
		parametros.add(clasificacionPlanillasSacopTO.getDescripcion());
		parametros.add(clasificacionPlanillasSacopTO.getActiveInt());
		parametros.add(clasificacionPlanillasSacopTO.getIdInt()); // primary key


		return JDBCUtil.executeUpdate(query, parametros);
	}

	/**
	 * 
	 */
	public void eliminar(ObjetoTO oClasificacionPlanillasSacopTO) throws Exception {
		clasificacionPlanillasSacopTO = (ClasificacionPlanillasSacopTO) oClasificacionPlanillasSacopTO;

		query.setLength(0);
		query.append("DELETE FROM tbl_clasificacionplanillassacop WHERE id=?");

		parametros = new ArrayList<Object>();
		parametros.add(clasificacionPlanillasSacopTO.getIdInt());

		JDBCUtil.executeUpdate(query, parametros);
	}

	/**
	 * 
	 */
	public boolean cargar(ObjetoTO oClasificacionPlanillasSacopTO) throws Exception {
		clasificacionPlanillasSacopTO = (ClasificacionPlanillasSacopTO) oClasificacionPlanillasSacopTO;

		query.setLength(0);
		query.append("SELECT * FROM tbl_clasificacionplanillassacop WHERE id=").append(clasificacionPlanillasSacopTO.getIdInt());

		CachedRowSet crs = JDBCUtil.executeQuery(query, Thread.currentThread().getStackTrace()[1].getMethodName());

		if (crs.next()) {
			load(crs, clasificacionPlanillasSacopTO);
			return true;
		}
		return false;
	}

	/**
	 * 
	 */
	public ArrayList<ClasificacionPlanillasSacopTO> listar() throws Exception {
		ArrayList<ClasificacionPlanillasSacopTO> lista = new ArrayList<ClasificacionPlanillasSacopTO>();

		query.setLength(0);
		query.append("SELECT * FROM tbl_clasificacionplanillassacop ORDER BY id");

		CachedRowSet crs = JDBCUtil.executeQuery(query, Thread.currentThread().getStackTrace()[1].getMethodName());

		while (crs.next()) {
			clasificacionPlanillasSacopTO = new ClasificacionPlanillasSacopTO();
			load(crs, clasificacionPlanillasSacopTO);
			lista.add(clasificacionPlanillasSacopTO);
		}
		return lista;
	}
	
	/**
	 * 
	 */
	public ArrayList<ClasificacionPlanillasSacopTO> listar(String descripcion) throws Exception {
		ArrayList<ClasificacionPlanillasSacopTO> lista = new ArrayList<ClasificacionPlanillasSacopTO>();

		query.setLength(0);
		query.append("SELECT * FROM tbl_clasificacionplanillassacop ");
		if(!ToolsHTML.isEmptyOrNull(descripcion)) {
			query.append("WHERE descripcion='").append(descripcion).append("' ");
		} else {
			query.append("WHERE active=").append(Constants.permissionSt).append(" ");
		}
		//query.append("ORDER BY lower(descripcion) ");
		query.append("ORDER BY id ");

		CachedRowSet crs = JDBCUtil.executeQuery(query, Thread.currentThread().getStackTrace()[1].getMethodName());

		while (crs.next()) {
			clasificacionPlanillasSacopTO = new ClasificacionPlanillasSacopTO();
			load(crs, clasificacionPlanillasSacopTO);
			lista.add(clasificacionPlanillasSacopTO);
		}
		return lista;
	}

	
	private void load(CachedRowSet crs, ClasificacionPlanillasSacopTO clasificacionPlanillasSacopTO) throws SQLException {
		clasificacionPlanillasSacopTO.setId(crs.getString("id"));
		clasificacionPlanillasSacopTO.setDescripcion(crs.getString("descripcion"));
		clasificacionPlanillasSacopTO.setActive(crs.getString("active"));
	}

}
