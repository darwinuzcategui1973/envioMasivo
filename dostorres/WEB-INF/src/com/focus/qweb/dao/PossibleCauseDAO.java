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
import com.focus.qweb.to.PossibleCauseTO;

import sun.jdbc.rowset.CachedRowSet;

/**
 * 
 * @author JRivero
 * 
 */
public class PossibleCauseDAO implements ObjetoDAO {

	private StringBuffer query = new StringBuffer();
	private ArrayList<Object> parametros = null;
	PossibleCauseTO possibleCauseTO = null;

	/**
	 * 
	 */
	public int insertar(ObjetoTO oPossibleCauseTO) throws Exception {
		possibleCauseTO = (PossibleCauseTO) oPossibleCauseTO;

		if(possibleCauseTO.getIdPossibleCauseInt()==0) {
			int idPossibleCause = HandlerStruct.proximo("possiblecause", "possiblecause", "idPossibleCause");
			possibleCauseTO.setIdPossibleCause(String.valueOf(idPossibleCause));
		}
		
		query.setLength(0);
		query.append("INSERT INTO possiblecause (idPossibleCause,descPossibleCause) ");
		query.append("VALUES(?,?)");

		parametros = new ArrayList<Object>();
		parametros.add(possibleCauseTO.getIdPossibleCauseInt());
		parametros.add(possibleCauseTO.getDescPossibleCause());

		return JDBCUtil.executeUpdate(query, parametros);
	}

	/**
	 * 
	 */
	public int actualizar(ObjetoTO oPossibleCauseTO) throws Exception {
		possibleCauseTO = (PossibleCauseTO) oPossibleCauseTO;

		query.setLength(0);
		query.append("UPDATE possiblecause SET descPossibleCause=? ");
		query.append("WHERE idPossibleCause=?");

		parametros = new ArrayList<Object>();
		parametros.add(possibleCauseTO.getDescPossibleCause());
		parametros.add(possibleCauseTO.getIdPossibleCauseInt()); // primary key

		return JDBCUtil.executeUpdate(query, parametros);
	}

	/**
	 * 
	 */
	public void eliminar(ObjetoTO oPossibleCauseTO) throws Exception {
		possibleCauseTO = (PossibleCauseTO) oPossibleCauseTO;
		
		// antes de eliminar la causa preguntamos si esta en una sacop incluida
		query.setLength(0);
		query.append("SELECT idPlanillaSacop1, causasnoconformidad ");
		query.append("FROM tbl_planillasacop1 ");
		query.append("WHERE causasnoconformidad like '").append(possibleCauseTO.getIdPossibleCauseInt()).append(",%' ");
		query.append("OR causasnoconformidad like '%,").append(possibleCauseTO.getIdPossibleCauseInt()).append("' ");
		query.append("OR causasnoconformidad like '%,").append(possibleCauseTO.getIdPossibleCauseInt()).append(",%' ");
				
		CachedRowSet crs = JDBCUtil.executeQuery(query, Thread.currentThread().getStackTrace()[1].getMethodName());
		
		if(crs.next()) {
			throw new Exception("La registro no puede ser eliminado porque ya esta incluido en una SACOP");
		}

		query.setLength(0);
		query.append("DELETE FROM possiblecause WHERE idPossibleCause=?");

		parametros = new ArrayList<Object>();
		parametros.add(possibleCauseTO.getIdPossibleCauseInt());

		JDBCUtil.executeUpdate(query, parametros);
	}

	/**
	 * 
	 */
	public boolean cargar(ObjetoTO oPossibleCauseTO) throws Exception {
		possibleCauseTO = (PossibleCauseTO) oPossibleCauseTO;

		query.setLength(0);
		query.append("SELECT * FROM possiblecause WHERE idPossibleCause=").append(possibleCauseTO.getIdPossibleCauseInt());

		CachedRowSet crs = JDBCUtil.executeQuery(query, Thread.currentThread().getStackTrace()[1].getMethodName());

		if (crs.next()) {
			load(crs, possibleCauseTO);
			return true;
		}
		return false;
	}

	/**
	 * 
	 */
	public ArrayList<PossibleCauseTO> listar() throws Exception {
		ArrayList<PossibleCauseTO> lista = new ArrayList<PossibleCauseTO>();

		query.setLength(0);
		query.append("SELECT * FROM possiblecause ORDER BY idPossibleCause");

		CachedRowSet crs = JDBCUtil.executeQuery(query, Thread.currentThread().getStackTrace()[1].getMethodName());

		while (crs.next()) {
			possibleCauseTO = new PossibleCauseTO();
			load(crs, possibleCauseTO);
			lista.add(possibleCauseTO);
		}
		return lista;
	}
	
	/**
	 * 
	 */
	public ArrayList<PossibleCauseTO> listarById(String id) throws Exception {
		ArrayList<PossibleCauseTO> lista = new ArrayList<PossibleCauseTO>();

		query.setLength(0);
		query.append("SELECT * FROM possiblecause ");
		query.append("WHERE idPossibleCause ");
		if(id.indexOf(",")==0) {
			query.append("=").append(id).append(" ");
		} else {
			query.append(" IN (").append(id).append(") ");
		}
		query.append("ORDER BY lower(descPossibleCause)");

		CachedRowSet crs = JDBCUtil.executeQuery(query, Thread.currentThread().getStackTrace()[1].getMethodName());

		while (crs.next()) {
			possibleCauseTO = new PossibleCauseTO();
			load(crs, possibleCauseTO);
			lista.add(possibleCauseTO);
		}
		return lista;
	}

	/**
	 * 
	 */
	public ArrayList<PossibleCauseTO> listarPossibleCauseAlls(String descPossibleCause) throws Exception {
		ArrayList<PossibleCauseTO> lista = new ArrayList<PossibleCauseTO>();

		query.setLength(0);
		query.append("SELECT * FROM possiblecause ");
		if (!ToolsHTML.isEmptyOrNull(descPossibleCause)) {
			query.append("WHERE descPossibleCause='").append(descPossibleCause).append("' ");
		}
		query.append("ORDER BY lower(descPossibleCause)");

		CachedRowSet crs = JDBCUtil.executeQuery(query, Thread.currentThread().getStackTrace()[1].getMethodName());

		while (crs.next()) {
			possibleCauseTO = new PossibleCauseTO();
			load(crs, possibleCauseTO);
			lista.add(possibleCauseTO);
		}
		return lista;
	}


	/**
	 * 
	 */
	public Map<String, PossibleCauseTO> listarOrderPossibleCauseAlls(String descPossibleCause) throws Exception {
		Map<String, PossibleCauseTO> lista = new TreeMap<String, PossibleCauseTO>();

		query.setLength(0);
		query.append("SELECT * FROM possiblecause ");
		if (!ToolsHTML.isEmptyOrNull(descPossibleCause)) {
			query.append("WHERE descPossibleCause='").append(descPossibleCause).append("' ");
		}
		query.append("ORDER BY lower(descPossibleCause)");

		CachedRowSet crs = JDBCUtil.executeQuery(query, Thread.currentThread().getStackTrace()[1].getMethodName());

		while (crs.next()) {
			possibleCauseTO = new PossibleCauseTO();
			load(crs, possibleCauseTO);
			lista.put(possibleCauseTO.getIdPossibleCause(), possibleCauseTO);
		}
		return lista;
	}
	
	private void load(CachedRowSet crs, PossibleCauseTO possibleCauseTO) throws SQLException {
		possibleCauseTO.setIdPossibleCause(crs.getString("idPossibleCause"));
		possibleCauseTO.setDescPossibleCause(crs.getString("descPossibleCause"));
	}

}
