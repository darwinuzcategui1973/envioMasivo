package com.focus.qweb.dao;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;

import com.desige.webDocuments.persistent.managers.HandlerStruct;
import com.desige.webDocuments.persistent.utils.JDBCUtil;
import com.desige.webDocuments.utils.ToolsHTML;
import com.focus.qweb.interfaces.ObjetoDAO;
import com.focus.qweb.interfaces.ObjetoTO;
import com.focus.qweb.to.ActionRecommendedTO;

import sun.jdbc.rowset.CachedRowSet;

/**
 * 
 * @author JRivero
 * 
 */
public class ActionRecommendedDAO implements ObjetoDAO {

	private StringBuffer query = new StringBuffer();
	private ArrayList<Object> parametros = null;
	ActionRecommendedTO actionRecommendedTO = null;

	/**
	 * 
	 */
	public int insertar(ObjetoTO oActionRecommendedTO) throws Exception {
		actionRecommendedTO = (ActionRecommendedTO) oActionRecommendedTO;

		if(ToolsHTML.isEmptyOrNull(actionRecommendedTO.getIdActionRecommended())) {
			int idActionRecommended = HandlerStruct.proximo("actionrecommended", "actionrecommended", "idActionRecommended");
			actionRecommendedTO.setIdActionRecommended(String.valueOf(idActionRecommended));
		}
		
		query.setLength(0);
		query.append("INSERT INTO actionrecommended (idActionRecommended,descActionRecommended,idRegisterClass) ");
		query.append("VALUES(?,?,?)");

		parametros = new ArrayList<Object>();
		parametros.add(actionRecommendedTO.getIdActionRecommendedInt());
		parametros.add(actionRecommendedTO.getDescActionRecommended());
		parametros.add(actionRecommendedTO.getIdRegisterClassInt());

		return JDBCUtil.executeUpdate(query, parametros);
	}

	/**
	 * 
	 */
	public int actualizar(ObjetoTO oActionRecommendedTO) throws Exception {
		actionRecommendedTO = (ActionRecommendedTO) oActionRecommendedTO;

		query.setLength(0);
		query.append("UPDATE actionrecommended SET descActionRecommended=?, idRegisterClass=? ");
		query.append("WHERE idActionRecommended=?");

		parametros = new ArrayList<Object>();
		parametros.add(actionRecommendedTO.getDescActionRecommended());
		parametros.add(actionRecommendedTO.getIdRegisterClassInt());
		parametros.add(actionRecommendedTO.getIdActionRecommendedInt()); // primary key

		return JDBCUtil.executeUpdate(query, parametros);
	}

	/**
	 * 
	 */
	public void eliminar(ObjetoTO oActionRecommendedTO) throws Exception {
		actionRecommendedTO = (ActionRecommendedTO) oActionRecommendedTO;

		// antes de eliminar la causa preguntamos si esta en una sacop incluida
		query.setLength(0);
		query.append("SELECT idPlanillaSacop1, accionesrecomendadas ");
		query.append("FROM tbl_planillasacop1 ");
		query.append("WHERE accionesrecomendadas like '").append(actionRecommendedTO.getIdActionRecommendedInt()).append(",%' ");
		query.append("OR accionesrecomendadas like '%,").append(actionRecommendedTO.getIdActionRecommendedInt()).append("' ");
		query.append("OR accionesrecomendadas like '%,").append(actionRecommendedTO.getIdActionRecommendedInt()).append(",%' ");
				
		CachedRowSet crs = JDBCUtil.executeQuery(query, Thread.currentThread().getStackTrace()[1].getMethodName());
		
		if(crs.next()) {
			throw new Exception("La registro no puede ser eliminado porque ya esta incluido en una SACOP");
		}
		
		query.setLength(0);
		query.append("DELETE FROM actionrecommended WHERE idActionRecommended=?");

		parametros = new ArrayList<Object>();
		parametros.add(actionRecommendedTO.getIdActionRecommendedInt());

		JDBCUtil.executeUpdate(query, parametros);
	}

	/**
	 * 
	 */
	public boolean cargar(ObjetoTO oActionRecommendedTO) throws Exception {
		actionRecommendedTO = (ActionRecommendedTO) oActionRecommendedTO;

		query.setLength(0);
		query.append("SELECT * FROM actionrecommended WHERE idActionRecommended=").append(actionRecommendedTO.getIdActionRecommendedInt());

		CachedRowSet crs = JDBCUtil.executeQuery(query, Thread.currentThread().getStackTrace()[1].getMethodName());

		if (crs.next()) {
			load(crs, actionRecommendedTO);
			return true;
		}
		return false;
	}

	/**
	 * 
	 */
	public ArrayList<ActionRecommendedTO> listar() throws Exception {
		ArrayList<ActionRecommendedTO> lista = new ArrayList<ActionRecommendedTO>();

		query.setLength(0);
		query.append("SELECT * FROM actionrecommended ORDER BY idActionRecommended");

		CachedRowSet crs = JDBCUtil.executeQuery(query, Thread.currentThread().getStackTrace()[1].getMethodName());

		while (crs.next()) {
			actionRecommendedTO = new ActionRecommendedTO();
			load(crs, actionRecommendedTO);
			lista.add(actionRecommendedTO);
		}
		return lista;
	}
	
	/**
	 * 
	 */
	public ArrayList<ActionRecommendedTO> listarById(String id) throws Exception {
		ArrayList<ActionRecommendedTO> lista = new ArrayList<ActionRecommendedTO>();

		query.setLength(0);
		query.append("SELECT * FROM actionrecommended ");
		query.append("WHERE idActionRecommended ");
		if(id.indexOf(",")==0) {
			query.append("=").append(id).append(" ");
		} else {
			query.append(" IN (").append(id).append(") ");
		}
		query.append("ORDER BY lower(descActionRecommended)");

		CachedRowSet crs = JDBCUtil.executeQuery(query, Thread.currentThread().getStackTrace()[1].getMethodName());

		while (crs.next()) {
			actionRecommendedTO = new ActionRecommendedTO();
			load(crs, actionRecommendedTO);
			lista.add(actionRecommendedTO);
		}
		return lista;
	}

	/**
	 * 
	 */
	public ArrayList<ActionRecommendedTO> listarActionRecommendedAlls(String descActionRecommended) throws Exception {
		ArrayList<ActionRecommendedTO> lista = new ArrayList<ActionRecommendedTO>();

		query.setLength(0);
		query.append("SELECT * FROM actionrecommended ");
		if (!ToolsHTML.isEmptyOrNull(descActionRecommended)) {
			query.append("WHERE descActionRecommended='").append(descActionRecommended).append("' ");
		}
		query.append("ORDER BY lower(descActionRecommended)");

		CachedRowSet crs = JDBCUtil.executeQuery(query, Thread.currentThread().getStackTrace()[1].getMethodName());

		while (crs.next()) {
			actionRecommendedTO = new ActionRecommendedTO();
			load(crs, actionRecommendedTO);
			lista.add(actionRecommendedTO);
		}
		return lista;
	}


	/**
	 * 
	 */
	public Map<String,ActionRecommendedTO> listarOrderActionRecommendedAlls(String descActionRecommended) throws Exception {
		Map<String,ActionRecommendedTO> lista = new TreeMap<String,ActionRecommendedTO>();

		query.setLength(0);
		query.append("SELECT * FROM actionrecommended ");
		if (!ToolsHTML.isEmptyOrNull(descActionRecommended)) {
			query.append("WHERE descActionRecommended='").append(descActionRecommended).append("' ");
		}
		query.append("ORDER BY lower(descActionRecommended)");

		CachedRowSet crs = JDBCUtil.executeQuery(query, Thread.currentThread().getStackTrace()[1].getMethodName());

		while (crs.next()) {
			actionRecommendedTO = new ActionRecommendedTO();
			load(crs, actionRecommendedTO);
			lista.put(actionRecommendedTO.getIdActionRecommended(),actionRecommendedTO);
		}
		return lista;
	}
	
	private void load(CachedRowSet crs, ActionRecommendedTO actionRecommendedTO) throws SQLException {
		actionRecommendedTO.setIdActionRecommended(crs.getString("idActionRecommended"));
		actionRecommendedTO.setDescActionRecommended(crs.getString("descActionRecommended"));
		actionRecommendedTO.setIdRegisterClass(crs.getString("idRegisterClass"));
	}

}
