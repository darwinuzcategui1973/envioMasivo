package com.focus.qweb.dao;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Map;
import java.util.TreeMap;
import java.util.Vector;

import com.desige.webDocuments.persistent.managers.HandlerNorms;
import com.desige.webDocuments.persistent.managers.HandlerStruct;
import com.desige.webDocuments.persistent.utils.JDBCUtil;
import com.desige.webDocuments.utils.ToolsHTML;
import com.focus.qweb.interfaces.ObjetoDAO;
import com.focus.qweb.interfaces.ObjetoTO;
import com.focus.qweb.to.PlanAuditTO;

import sun.jdbc.rowset.CachedRowSet;

/**
 * 
 * @author JRivero
 * 
 */
public class PlanAuditDAO implements ObjetoDAO {

	private StringBuffer query = new StringBuffer();
	private ArrayList<Object> parametros = null;
	PlanAuditTO PlanAuditTO = null;

	/**
	 * 
	 */
	public int insertar(ObjetoTO oPlanAuditTO) throws Exception {
		PlanAuditTO = (PlanAuditTO) oPlanAuditTO;
		
		PlanAuditTO.setStatusPlan(PlanAuditTO.getStatusPlan().equals("")?"B":PlanAuditTO.getStatusPlan());

		if(ToolsHTML.parseInt(PlanAuditTO.getIdPlanAudit())<=0) {
			int idPlanAudit = HandlerStruct.proximo("planaudit", "planaudit", "idPlanAudit");
			PlanAuditTO.setIdPlanAudit(String.valueOf(idPlanAudit));
		}
		
		query.setLength(0);
		query.append("INSERT INTO planaudit (idPlanAudit,namePlan,idProgramAudit,idPersonPlan,typeAudit,dateFromPlan,dateUntilPlan,statusPlan,idNorm,idNormPlanCheck,dateCreatedPlan,idPersonCreatorPlan) ");
		query.append("VALUES(?,?,?,?,?,?,?,?,?,?,?,?)");
		
		Calendar c = Calendar.getInstance();
		c.add(Calendar.YEAR, 1);

		
		parametros = new ArrayList<Object>();
		parametros.add(PlanAuditTO.getIdPlanAuditInt());
		parametros.add(PlanAuditTO.getNamePlan());
		parametros.add(PlanAuditTO.getIdProgramAuditInt());
		parametros.add(PlanAuditTO.getIdPersonPlanInt());
		parametros.add(PlanAuditTO.getTypeAuditInt());
		parametros.add(new java.sql.Date(PlanAuditTO.getDateFromPlanDATE().getTime()));
		parametros.add(new java.sql.Date(PlanAuditTO.getDateUntilPlanDATE().getTime()));
		//parametros.add(PlanAuditTO.getDateFromPlan());
		//parametros.add(PlanAuditTO.getDateUntilPlan());
		parametros.add(PlanAuditTO.getStatusPlan());
		parametros.add(PlanAuditTO.getIdNormInt());
		parametros.add(PlanAuditTO.getIdNormPlanCheck());
		parametros.add(new java.sql.Date(PlanAuditTO.getDateCreatedPlanDATE().getTime()));
		//parametros.add(PlanAuditTO.getDateCreatedPlan());
		parametros.add(PlanAuditTO.getIdPersonCreatorPlanInt());

		return JDBCUtil.executeUpdate(query, parametros);
	}

	/**
	 * 
	 */
	public int actualizar(ObjetoTO oPlanAuditTO) throws Exception {
		PlanAuditTO = (PlanAuditTO) oPlanAuditTO;

		query.setLength(0);
		query.append("UPDATE planaudit SET namePlan=?,idProgramAudit=?,idPersonPlan=?,typeAudit=?,dateFromPlan=?, ");
		query.append("dateUntilPlan=?,statusPlan=?,idNorm=?,idNormPlanCheck=?,dateCreatedPlan=?,idPersonCreatorPlan=? ");
		query.append("WHERE idPlanAudit=?");

		parametros = new ArrayList<Object>();
		parametros.add(PlanAuditTO.getNamePlan());
		parametros.add(PlanAuditTO.getIdProgramAuditInt());
		parametros.add(PlanAuditTO.getIdPersonPlanInt());
		parametros.add(PlanAuditTO.getTypeAuditInt());
		parametros.add(new java.sql.Date(PlanAuditTO.getDateFromPlanDATE().getTime()));
		parametros.add(new java.sql.Date(PlanAuditTO.getDateUntilPlanDATE().getTime()));
		//parametros.add(PlanAuditTO.getDateFromPlan());
		//parametros.add(PlanAuditTO.getDateUntilPlan());
		parametros.add(PlanAuditTO.getStatusPlan());
		parametros.add(PlanAuditTO.getIdNormInt());
		parametros.add(PlanAuditTO.getIdNormPlanCheck());
		parametros.add(new java.sql.Date(PlanAuditTO.getDateCreatedPlanDATE().getTime()));
		//parametros.add(PlanAuditTO.getDateCreatedPlan());
		parametros.add(PlanAuditTO.getIdPersonCreatorPlanInt());

		parametros.add(PlanAuditTO.getIdPlanAuditInt()); // primary key

		return JDBCUtil.executeUpdate(query, parametros);
	}

	/**
	 * 
	 */
	public void eliminar(ObjetoTO oPlanAuditTO) throws Exception {
		PlanAuditTO = (PlanAuditTO) oPlanAuditTO;

		query.setLength(0);
		query.append("DELETE FROM planaudit WHERE idPlanAudit=?");

		parametros = new ArrayList<Object>();
		parametros.add(PlanAuditTO.getIdPlanAuditInt());

		JDBCUtil.executeUpdate(query, parametros);
	}

	/**
	 * 
	 */
	public boolean cargar(ObjetoTO oPlanAuditTO) throws Exception {
		PlanAuditTO = (PlanAuditTO) oPlanAuditTO;
		
		query.setLength(0);
		query.append("SELECT * FROM planaudit WHERE idPlanAudit=").append(PlanAuditTO.getIdPlanAuditInt());

		CachedRowSet crs = JDBCUtil.executeQuery(query, Thread.currentThread().getStackTrace()[1].getMethodName());

		if (crs.next()) {
			load(crs, PlanAuditTO);
			return true;
		}
		return false;
	}

	/**
	 * 
	 */
	public ArrayList<PlanAuditTO> listar() throws Exception {
		ArrayList<PlanAuditTO> lista = new ArrayList<PlanAuditTO>();
		
		query.setLength(0);
		query.append("SELECT * FROM planaudit ORDER BY idPlanAudit");

		CachedRowSet crs = JDBCUtil.executeQuery(query, Thread.currentThread().getStackTrace()[1].getMethodName());

		while (crs.next()) {
			PlanAuditTO = new PlanAuditTO();
			load(crs, PlanAuditTO);
			lista.add(PlanAuditTO);
		}
		return lista;
	}
	
	/**
	 * 
	 */
	public ArrayList<PlanAuditTO> listarById(String id) throws Exception {
		ArrayList<PlanAuditTO> lista = new ArrayList<PlanAuditTO>();

		query.setLength(0);
		query.append("SELECT * FROM planaudit ");
		query.append("WHERE idPlanAudit ");
		if(id.indexOf(",")==0) {
			query.append("=").append(id).append(" ");
		} else {
			query.append(" IN (").append(id).append(") ");
		}
		query.append("ORDER BY dateFrom ");

		CachedRowSet crs = JDBCUtil.executeQuery(query, Thread.currentThread().getStackTrace()[1].getMethodName());

		while (crs.next()) {
			PlanAuditTO = new PlanAuditTO();
			load(crs, PlanAuditTO);
			lista.add(PlanAuditTO);
		}
		return lista;
	}
	
	public ArrayList<PlanAuditTO> listarByIdNorma(String id) throws Exception {
		ArrayList<PlanAuditTO> lista = new ArrayList<PlanAuditTO>();

		String normas ="";
		String norma ="";
		
		query.setLength(0);
		query.append("SELECT * FROM planaudit ");
		query.append("WHERE idNorm ");

		if(id.indexOf(",")<0) {
			norma=HandlerNorms.getNormasPrincipalesPlanDeLosProgramasAuditoria(null,id);
			query.append("=").append(norma).append(" ");
		} else {
			String s=HandlerNorms.getAllNormasPrincipalesPlanDeLosProgramasAuditoria(null,id).toString();
			normas= s.replace("[", "(");
			normas= normas.replace("]", ")");
			//query.append(" IN (").append(norma).append(") ");
			query.append(" IN ").append(normas);
		}
		query.append(" ORDER BY idNorm ");

		CachedRowSet crs = JDBCUtil.executeQuery(query, Thread.currentThread().getStackTrace()[1].getMethodName());

		while (crs.next()) {
			PlanAuditTO = new PlanAuditTO();
			load(crs, PlanAuditTO);
			lista.add(PlanAuditTO);
		}
		return lista;
	}


	/**
	 * 
	 */
	public ArrayList<PlanAuditTO> listarPlanAuditAlls(Long idProgramAudit) throws Exception {
		ArrayList<PlanAuditTO> lista = new ArrayList<PlanAuditTO>();
		
		query.setLength(0);
		query.append("SELECT * FROM planaudit ");
		if (idProgramAudit>0) {
			query.append("WHERE idProgramAudit=").append(idProgramAudit).append(" ");
		}
		query.append("ORDER BY dateFromPlan ");

		CachedRowSet crs = JDBCUtil.executeQuery(query, Thread.currentThread().getStackTrace()[1].getMethodName());

		while (crs.next()) {
			PlanAuditTO = new PlanAuditTO();
			load(crs, PlanAuditTO);
			lista.add(PlanAuditTO);
		}
		return lista;
	}


	/**
	 * 
	 */
	public Map<String,PlanAuditTO> listarOrderPlanAuditAlls(String idNorm) throws Exception {
		Map<String,PlanAuditTO> lista = new TreeMap<String,PlanAuditTO>();
		

		String norma =idNorm;
		query.setLength(0);
		query.append("SELECT * FROM planaudit ");
		if (!ToolsHTML.isEmptyOrNull(idNorm)) {
			query.append("WHERE idNorm='").append(idNorm).append("' ");
		}
		query.append("ORDER BY idNorm ");

		CachedRowSet crs = JDBCUtil.executeQuery(query, Thread.currentThread().getStackTrace()[1].getMethodName());

		while (crs.next()) {
			PlanAuditTO = new PlanAuditTO();
			load(crs, PlanAuditTO);
			lista.put(PlanAuditTO.getIdPlanAudit(),PlanAuditTO);
		}
		return lista;
	}
	
	public static boolean isProgramInPlan(long idProgramAudit) throws Exception {
		StringBuilder sql = new StringBuilder(1024);
		sql.append("select idPlanAudit from planaudit where idProgramAudit = ").append(idProgramAudit);
		Vector datos = JDBCUtil.doQueryVector(sql.toString(),Thread.currentThread().getStackTrace()[1].getMethodName());
		return datos.size() > 0;
	}


	public static boolean isProgramHasPlanOpen(long idProgramAudit) throws Exception {
		StringBuilder sql = new StringBuilder(1024);
		sql.append("select idPlanAudit from planaudit where statusPlan != 'C' AND idProgramAudit = ").append(idProgramAudit);
		Vector datos = JDBCUtil.doQueryVector(sql.toString(),Thread.currentThread().getStackTrace()[1].getMethodName());
		return datos.size() > 0;
	}
	
	private void load(CachedRowSet crs, PlanAuditTO PlanAuditTO) throws SQLException {
		PlanAuditTO.setIdPlanAudit(crs.getString("idPlanAudit"));
		PlanAuditTO.setNamePlan(crs.getString("namePlan"));
		PlanAuditTO.setIdProgramAudit(crs.getString("idProgramAudit"));
		PlanAuditTO.setIdPersonPlan(crs.getString("idPersonPlan"));
		PlanAuditTO.setTypeAudit(crs.getString("typeAudit"));
		PlanAuditTO.setDateFromPlan(crs.getString("dateFromPlan"));
		PlanAuditTO.setDateUntilPlan(crs.getString("dateUntilPlan"));
		PlanAuditTO.setStatusPlan(crs.getString("statusPlan"));
		PlanAuditTO.setIdNorm(crs.getString("idNorm"));
		PlanAuditTO.setIdNormPlanCheck(crs.getString("idNormPlanCheck"));
		PlanAuditTO.setDateCreatedPlan(crs.getString("dateCreatedPlan"));
		PlanAuditTO.setIdPersonCreatorPlan(crs.getString("idPersonCreatorPlan"));
		
	}

}
