package com.focus.qweb.dao;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;

import com.desige.webDocuments.persistent.utils.JDBCUtil;
import com.desige.webDocuments.utils.IDDBFactorySql;
import com.desige.webDocuments.utils.ToolsHTML;
import com.focus.qweb.interfaces.ObjetoDAO;
import com.focus.qweb.interfaces.ObjetoTO;
import com.focus.qweb.to.ActivityTO;
import com.focus.qweb.to.SubActivitiesTO;

import sun.jdbc.rowset.CachedRowSet;

/**
 * 
 * @author JRivero
 * 
 */
public class ActivityDAO implements ObjetoDAO {

	private StringBuffer query = new StringBuffer();
	private ArrayList<Object> parametros = null;
	ActivityTO activityTO = null;

	/**
	 * 
	 */
	public int insertar(ObjetoTO oActivityTO) throws Exception {
		activityTO = (ActivityTO) oActivityTO;

		if(activityTO.getActNumberInt()==0) {
			long id = IDDBFactorySql.getNextIDLong("activity");
			activityTO.setActNumber(String.valueOf(id));
			activityTO.setActActive("1");
		}
		
		query.setLength(0);
		query.append("INSERT INTO activity (Act_Number,Act_Name,Act_Description,Act_Active,Act_TypeDocument) ");
		query.append("VALUES(?,?,?,?,?)");
		
		parametros = new ArrayList<Object>();
		parametros.add(activityTO.getActNumberInt());
		parametros.add(activityTO.getActName());
		parametros.add(activityTO.getActDescription());
		parametros.add(activityTO.getActActiveInt());
		parametros.add(activityTO.getActTypeDocumentInt());

		return JDBCUtil.executeUpdate(query, parametros);
	}

	/**
	 * 
	 */
	public int actualizar(ObjetoTO oActivityTO) throws Exception {
		activityTO = (ActivityTO) oActivityTO;

		query.setLength(0);
		query.append("UPDATE activity SET ");
		query.append("Act_Name=?,Act_Description=?,Act_Active=?,Act_TypeDocument=? ");
		query.append("WHERE Act_Number=?");

		parametros = new ArrayList<Object>();
		parametros.add(activityTO.getActName());
		parametros.add(activityTO.getActDescription());
		parametros.add(activityTO.getActActiveInt());
		parametros.add(activityTO.getActTypeDocumentInt());
		parametros.add(activityTO.getActNumberInt()); // primary key


		return JDBCUtil.executeUpdate(query, parametros);
	}

	/**
	 * 
	 */
	public void eliminar(ObjetoTO oActivityTO) throws Exception {
		activityTO = (ActivityTO) oActivityTO;

		query.setLength(0);
		query.append("DELETE FROM activity WHERE Act_Number=?");

		parametros = new ArrayList<Object>();
		parametros.add(activityTO.getActNumberInt());

		JDBCUtil.executeUpdate(query, parametros);
	}

	/**
	 * 
	 */
	public boolean cargar(ObjetoTO oActivityTO) throws Exception {
		activityTO = (ActivityTO) oActivityTO;

		query.setLength(0);
		query.append("SELECT * FROM activity WHERE Act_Number=").append(activityTO.getActNumberInt());

		CachedRowSet crs = JDBCUtil.executeQuery(query, Thread.currentThread().getStackTrace()[1].getMethodName());
		
		CachedRowSet users = null;

		if (crs.next()) {
			load(crs, activityTO);
			
			// cargamos las subactividades
			activityTO.setSubActivitiesTO(new ArrayList<SubActivitiesTO>());
			
			SubActivitiesDAO oSubActivitiesDAO = new SubActivitiesDAO();
			ArrayList lista = oSubActivitiesDAO.listarByIdActivity(activityTO.getActNumber());
			
			if(lista.size()>0) {
				SubActivitiesTO sub = null;
				for (Iterator it = lista.iterator(); it.hasNext();) {
					sub = (SubActivitiesTO)it.next();
					
					activityTO.getSubActivitiesTO().add(sub);
				}
			}
			
			return true;
		}
		return false;
	}

	/**
	 * 
	 */
	public ArrayList<ActivityTO> listar() throws Exception {
		ArrayList<ActivityTO> lista = new ArrayList<ActivityTO>();

		query.setLength(0);
		query.append("SELECT * FROM activity ORDER BY Act_Number");

		CachedRowSet crs = JDBCUtil.executeQuery(query, Thread.currentThread().getStackTrace()[1].getMethodName());

		while (crs.next()) {
			activityTO = new ActivityTO();
			load(crs, activityTO);
			lista.add(activityTO);
		}
		return lista;
	}
	
	/**
	 * 
	 */
	public ArrayList<ActivityTO> listarActAlls() throws Exception {
		ArrayList<ActivityTO> lista = new ArrayList<ActivityTO>();

		query.setLength(0);
		query.append("SELECT * FROM activity WHERE Act_Active = '1' ORDER BY Act_Number");

		CachedRowSet crs = JDBCUtil.executeQuery(query, Thread.currentThread().getStackTrace()[1].getMethodName());

		while (crs.next()) {
			activityTO = new ActivityTO();
			load(crs, activityTO);
			lista.add(activityTO);
		}
		return lista;
	}

	/**
	 * 
	 */
	public ArrayList<ActivityTO> listarActByTypeDoc(String idTypeDoc) throws Exception {
		ArrayList<ActivityTO> lista = new ArrayList<ActivityTO>();

		query.setLength(0);
		query.append("SELECT * FROM activity WHERE Act_Active = '1' AND Act_TypeDocument=");
		query.append(idTypeDoc).append(" ORDER BY Act_Number ");

		CachedRowSet crs = JDBCUtil.executeQuery(query, Thread.currentThread().getStackTrace()[1].getMethodName());

		while (crs.next()) {
			activityTO = new ActivityTO();
			load(crs, activityTO);
			lista.add(activityTO);
		}
		return lista;
	}
	
	/**
	 * 
	 */
	public ArrayList<ActivityTO> listarFtpByName(String name) throws Exception {
		ArrayList<ActivityTO> lista = new ArrayList<ActivityTO>();

		query.setLength(0);
		query.append("SELECT * FROM activity WHERE Act_Active = '1' AND act_name='").append(name).append("'");

		CachedRowSet crs = JDBCUtil.executeQuery(query, Thread.currentThread().getStackTrace()[1].getMethodName());

		while (crs.next()) {
			activityTO = new ActivityTO();
			load(crs, activityTO);
			lista.add(activityTO);
		}
		return lista;
	}


	/**
	 * 
	 */
	public ArrayList<ActivityTO> listarAllActivitys() throws Exception {
		ArrayList<ActivityTO> lista = new ArrayList<ActivityTO>();

		query.setLength(0);
		query.append("SELECT a.* FROM activity a ORDER BY lower(a.Act_Name)");

		CachedRowSet crs = JDBCUtil.executeQuery(query, Thread.currentThread().getStackTrace()[1].getMethodName());

		while (crs.next()) {
			activityTO = new ActivityTO();
			load(crs, activityTO);
			lista.add(activityTO);
		}
		return lista;
	}
	

	private void load(CachedRowSet crs, ActivityTO activityTO) throws SQLException {
		activityTO.setActNumber(crs.getString("Act_Number"));
		activityTO.setActName(crs.getString("Act_Name"));
		activityTO.setActDescription(crs.getString("Act_Description"));
		activityTO.setActActive(crs.getString("Act_Active"));
		activityTO.setActTypeDocument(crs.getString("Act_TypeDocument"));
	}

}
