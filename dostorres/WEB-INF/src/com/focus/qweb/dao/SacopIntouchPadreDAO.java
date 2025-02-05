package com.focus.qweb.dao;

import java.sql.SQLException;
import java.util.ArrayList;

import sun.jdbc.rowset.CachedRowSet;

import com.desige.webDocuments.persistent.managers.HandlerStruct;
import com.desige.webDocuments.persistent.utils.JDBCUtil;
import com.desige.webDocuments.utils.ToolsHTML;
import com.focus.qweb.interfaces.ObjetoDAO;
import com.focus.qweb.interfaces.ObjetoTO;
import com.focus.qweb.to.SacopIntouchPadreTO;

/**
 * 
 * @author JRivero
 * 
 */
public class SacopIntouchPadreDAO implements ObjetoDAO {

	private StringBuffer query = new StringBuffer();
	private ArrayList<Object> parametros = null;
	SacopIntouchPadreTO sacopIntouchPadreTO = null;

	/**
	 * 
	 */
	public int insertar(ObjetoTO oSacopIntouchPadreTO) throws Exception {
		sacopIntouchPadreTO = (SacopIntouchPadreTO) oSacopIntouchPadreTO;

		if(sacopIntouchPadreTO.getIdSacopIntouchPadreInt()==0) {
			long idsacopintouchpadre = HandlerStruct.proximo(
					"idsacopintouchpadre", "idsacopintouchpadre",
					"idsacopintouchpadre");
			
			sacopIntouchPadreTO.setIdSacopIntouchPadre(String.valueOf(idsacopintouchpadre));
		}
		
		
		query.setLength(0);
		query.append("INSERT INTO tbl_sacop_intouch_padre (idsacopintouchpadre,idplanillasacop1,active,enable) ");
		query.append("VALUES(?,?,?,?)");
		
		parametros = new ArrayList<Object>();
		parametros.add(sacopIntouchPadreTO.getIdSacopIntouchPadreInt());
		parametros.add(sacopIntouchPadreTO.getIdPlanillaSacop1Int());
		parametros.add(sacopIntouchPadreTO.getActiveInt());
		parametros.add(sacopIntouchPadreTO.getEnableInt());

		return JDBCUtil.executeUpdate(query, parametros);
	}

	/**
	 * 
	 */
	public int actualizar(ObjetoTO oSacopIntouchPadreTO) throws Exception {
		sacopIntouchPadreTO = (SacopIntouchPadreTO) oSacopIntouchPadreTO;

		query.setLength(0);
		query.append("UPDATE tbl_sacop_intouch_padre SET ");
		query.append("idplanillasacop1=?,active=?,enable=? ");
		query.append("WHERE idsacopintouchpadre=?");

		parametros = new ArrayList<Object>();
		parametros.add(sacopIntouchPadreTO.getIdPlanillaSacop1Int());
		parametros.add(sacopIntouchPadreTO.getActiveInt());
		parametros.add(sacopIntouchPadreTO.getEnableInt());
		parametros.add(sacopIntouchPadreTO.getIdSacopIntouchPadreInt()); // primary key


		return JDBCUtil.executeUpdate(query, parametros);
	}

	/**
	 * 
	 */
	public void eliminar(ObjetoTO oSacopIntouchPadreTO) throws Exception {
		sacopIntouchPadreTO = (SacopIntouchPadreTO) oSacopIntouchPadreTO;

		query.setLength(0);
		query.append("DELETE FROM tbl_sacop_intouch_padre WHERE idsacopintouchpadre=?");

		parametros = new ArrayList<Object>();
		parametros.add(sacopIntouchPadreTO.getIdSacopIntouchPadreInt());

		JDBCUtil.executeUpdate(query, parametros);
	}

	/**
	 * 
	 */
	public boolean cargar(ObjetoTO oSacopIntouchPadreTO) throws Exception {
		sacopIntouchPadreTO = (SacopIntouchPadreTO) oSacopIntouchPadreTO;

		query.setLength(0);
		query.append("SELECT * FROM tbl_sacop_intouch_padre WHERE idsacopintouchpadre=").append(sacopIntouchPadreTO.getIdSacopIntouchPadreInt());

		CachedRowSet crs = JDBCUtil.executeQuery(query, Thread.currentThread().getStackTrace()[1].getMethodName());

		if (crs.next()) {
			load(crs, sacopIntouchPadreTO);
			return true;
		}
		return false;
	}

	public boolean cargarByIdplanillasacop1(ObjetoTO oSacopIntouchPadreTO) throws Exception {
		sacopIntouchPadreTO = (SacopIntouchPadreTO) oSacopIntouchPadreTO;

		query.setLength(0);
		query.append("SELECT * FROM tbl_sacop_intouch_padre WHERE idplanillasacop1=?");

		parametros = new ArrayList<Object>();
		parametros.add(sacopIntouchPadreTO.getIdPlanillaSacop1Int());

		CachedRowSet crs = JDBCUtil.executeQuery(query, parametros, Thread.currentThread().getStackTrace()[1].getMethodName());

		if (crs.next()) {
			load(crs, sacopIntouchPadreTO);
			return true;
		}
		return false;
	}

	/**
	 * 
	 */
	public ArrayList<SacopIntouchPadreTO> listar() throws Exception {
		ArrayList<SacopIntouchPadreTO> lista = new ArrayList<SacopIntouchPadreTO>();

		query.setLength(0);
		query.append("SELECT * FROM tbl_sacop_intouch_padre ORDER BY idsacopintouchpadre");

		CachedRowSet crs = JDBCUtil.executeQuery(query, Thread.currentThread().getStackTrace()[1].getMethodName());

		while (crs.next()) {
			sacopIntouchPadreTO = new SacopIntouchPadreTO();
			load(crs, sacopIntouchPadreTO);
			lista.add(sacopIntouchPadreTO);
		}
		return lista;
	}
	
	/**
	 * 
	 */
	public ArrayList<SacopIntouchPadreTO> listar(String active, String idsacopintouchpadre) throws Exception {
		ArrayList<SacopIntouchPadreTO> lista = new ArrayList<SacopIntouchPadreTO>();

		query.setLength(0);
		query.append("SELECT * FROM tbl_sacop_intouch_padre ");
		query.append("WHERE active=").append(active).append(" ");
		if(idsacopintouchpadre!=null && !idsacopintouchpadre.equals("0")) {
			query.append("AND idsacopintouchpadre=").append(idsacopintouchpadre).append(" ");
		}

		CachedRowSet crs = JDBCUtil.executeQuery(query, Thread.currentThread().getStackTrace()[1].getMethodName());

		while (crs.next()) {
			sacopIntouchPadreTO = new SacopIntouchPadreTO();
			load(crs, sacopIntouchPadreTO);
			lista.add(sacopIntouchPadreTO);
		}
		return lista;
	}

	public ArrayList<SacopIntouchPadreTO> listarOrderByIdSacopIntouchPadre(String active) throws Exception {
		ArrayList<SacopIntouchPadreTO> lista = new ArrayList<SacopIntouchPadreTO>();

		query.setLength(0);
		query.append("SELECT * FROM tbl_sacop_intouch_padre ");
		query.append("WHERE active=").append(active).append(" ");
		query.append("ORDER BY idsacopintouchpadre ");

		CachedRowSet crs = JDBCUtil.executeQuery(query, Thread.currentThread().getStackTrace()[1].getMethodName());

		while (crs.next()) {
			sacopIntouchPadreTO = new SacopIntouchPadreTO();
			load(crs, sacopIntouchPadreTO);
			lista.add(sacopIntouchPadreTO);
		}
		return lista;
	}

	private void load(CachedRowSet crs, SacopIntouchPadreTO sacopIntouchPadreTO) throws SQLException {
		sacopIntouchPadreTO.setIdSacopIntouchPadre(crs.getString("idsacopintouchpadre"));
		sacopIntouchPadreTO.setIdPlanillaSacop1(crs.getString("idplanillasacop1"));
		sacopIntouchPadreTO.setActive(crs.getString("active"));
		sacopIntouchPadreTO.setEnable(crs.getString("enable"));
	}

}
