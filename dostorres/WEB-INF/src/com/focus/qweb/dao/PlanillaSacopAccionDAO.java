package com.focus.qweb.dao;

import java.sql.SQLException;
import java.util.ArrayList;

import sun.jdbc.rowset.CachedRowSet;

import com.desige.webDocuments.persistent.utils.JDBCUtil;
import com.focus.qweb.interfaces.ObjetoDAO;
import com.focus.qweb.interfaces.ObjetoTO;
import com.focus.qweb.to.PlanillaSacopAccionTO;

/**
 * 
 * @author JRivero
 * 
 */
public class PlanillaSacopAccionDAO implements ObjetoDAO {

	private StringBuffer query = new StringBuffer();
	private ArrayList<Object> parametros = null;
	PlanillaSacopAccionTO planillaSacopAccionTO = null;

	/**
	 * 
	 */
	public int insertar(ObjetoTO oPlanillaSacopAccionTO) throws Exception {
		planillaSacopAccionTO = (PlanillaSacopAccionTO) oPlanillaSacopAccionTO;

		query.setLength(0);
		query.append("INSERT INTO tbl_planillasacopaccion (idplanillasacopaccion,idplanillasacop1,accion,fecha,responsables,active) ");
		query.append("VALUES(?,?,?,?,?,?)");
		
		parametros = new ArrayList<Object>();
		parametros.add(planillaSacopAccionTO.getIdplanillasacopaccionInt());
		parametros.add(planillaSacopAccionTO.getIdplanillasacop1Int());
		parametros.add(planillaSacopAccionTO.getAccion());
		parametros.add(planillaSacopAccionTO.getFecha());
		parametros.add(planillaSacopAccionTO.getResponsables());
		parametros.add(planillaSacopAccionTO.getActiveInt());

		return JDBCUtil.executeUpdate(query, parametros);
	}

	/**
	 * 
	 */
	public int actualizar(ObjetoTO oPlanillaSacopAccionTO) throws Exception {
		planillaSacopAccionTO = (PlanillaSacopAccionTO) oPlanillaSacopAccionTO;

		query.setLength(0);
		query.append("UPDATE tbl_planillasacopaccion SET ");
		query.append("idplanillasacop1=?,accion=?,fecha,responsables=?,active=? ");
		query.append("WHERE idplanillasacopaccion=?");

		parametros = new ArrayList<Object>();
		parametros.add(planillaSacopAccionTO.getIdplanillasacop1Int());
		parametros.add(planillaSacopAccionTO.getAccion());
		parametros.add(planillaSacopAccionTO.getFecha());
		parametros.add(planillaSacopAccionTO.getResponsables());
		parametros.add(planillaSacopAccionTO.getActiveInt());
		parametros.add(planillaSacopAccionTO.getIdplanillasacopaccionInt()); // primary key


		return JDBCUtil.executeUpdate(query, parametros);
	}

	/**
	 * 
	 */
	public void eliminar(ObjetoTO oPlanillaSacopAccionTO) throws Exception {
		planillaSacopAccionTO = (PlanillaSacopAccionTO) oPlanillaSacopAccionTO;

		query.setLength(0);
		query.append("DELETE FROM tbl_planillasacopaccion WHERE idplanillasacopaccion=?");

		parametros = new ArrayList<Object>();
		parametros.add(planillaSacopAccionTO.getIdplanillasacopaccionInt());

		JDBCUtil.executeUpdate(query, parametros);
	}

	/**
	 * 
	 */
	public boolean cargar(ObjetoTO oPlanillaSacopAccionTO) throws Exception {
		planillaSacopAccionTO = (PlanillaSacopAccionTO) oPlanillaSacopAccionTO;

		query.setLength(0);
		query.append("SELECT * FROM tbl_planillasacopaccion WHERE idplanillasacopaccion=").append(planillaSacopAccionTO.getIdplanillasacopaccionInt());

		CachedRowSet crs = JDBCUtil.executeQuery(query, Thread.currentThread().getStackTrace()[1].getMethodName());

		if (crs.next()) {
			load(crs, planillaSacopAccionTO);
			return true;
		}
		return false;
	}

	/**
	 * 
	 */
	public ArrayList<PlanillaSacopAccionTO> listar() throws Exception {
		ArrayList<PlanillaSacopAccionTO> lista = new ArrayList<PlanillaSacopAccionTO>();

		query.setLength(0);
		query.append("SELECT * FROM tbl_planillasacopaccion ORDER BY idplanillasacopaccion");

		CachedRowSet crs = JDBCUtil.executeQuery(query, Thread.currentThread().getStackTrace()[1].getMethodName());

		while (crs.next()) {
			planillaSacopAccionTO = new PlanillaSacopAccionTO();
			load(crs, planillaSacopAccionTO);
			lista.add(planillaSacopAccionTO);
		}
		return lista;
	}
	
	public ArrayList<PlanillaSacopAccionTO> listarByIdPlanillaSacop1(String idPlanillaSacop1) throws Exception {
		ArrayList<PlanillaSacopAccionTO> lista = new ArrayList<PlanillaSacopAccionTO>();

		query.setLength(0);
		query.append("SELECT * FROM tbl_planillasacopaccion WHERE idplanillasacop1=");
		query.append(idPlanillaSacop1);
		query.append(" ORDER BY idplanillasacopaccion");

		CachedRowSet crs = JDBCUtil.executeQuery(query, Thread.currentThread().getStackTrace()[1].getMethodName());

		while (crs.next()) {
			planillaSacopAccionTO = new PlanillaSacopAccionTO();
			load(crs, planillaSacopAccionTO);
			lista.add(planillaSacopAccionTO);
		}
		return lista;
	}

	public ArrayList<PlanillaSacopAccionTO> listarOrderByFecha() throws Exception {
		ArrayList<PlanillaSacopAccionTO> lista = new ArrayList<PlanillaSacopAccionTO>();

		query.setLength(0);
		query.append("SELECT * FROM tbl_planillasacopaccion ORDER BY fecha");

		CachedRowSet crs = JDBCUtil.executeQuery(query, Thread.currentThread().getStackTrace()[1].getMethodName());

		while (crs.next()) {
			planillaSacopAccionTO = new PlanillaSacopAccionTO();
			load(crs, planillaSacopAccionTO);
			lista.add(planillaSacopAccionTO);
		}
		return lista;
	}

	private void load(CachedRowSet crs, PlanillaSacopAccionTO planillaSacopAccionTO) throws SQLException {
		planillaSacopAccionTO.setIdplanillasacopaccion(crs.getString("idplanillasacopaccion"));
		planillaSacopAccionTO.setIdplanillasacop1(crs.getString("idplanillasacop1"));
		planillaSacopAccionTO.setAccion(crs.getString("accion"));
		planillaSacopAccionTO.setFecha(crs.getString("fecha"));
		planillaSacopAccionTO.setResponsables(crs.getString("responsables"));
		planillaSacopAccionTO.setActive(crs.getString("active"));
	}

}
