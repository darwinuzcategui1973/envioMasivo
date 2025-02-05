package com.focus.qweb.dao;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;

import com.desige.webDocuments.perfil.forms.PerfilActionForm;
import com.desige.webDocuments.persistent.utils.JDBCUtil;
import com.desige.webDocuments.utils.IDDBFactorySql;
import com.desige.webDocuments.utils.ToolsHTML;
import com.focus.qweb.interfaces.ObjetoDAO;
import com.focus.qweb.interfaces.ObjetoTO;
import com.focus.qweb.to.PersonTO;
import com.focus.qweb.to.SubActivitiesTO;

import sun.jdbc.rowset.CachedRowSet;

/**
 * 
 * @author JRivero
 * 
 */
public class SubActivitiesDAO implements ObjetoDAO {

	private StringBuffer query = new StringBuffer();
	private ArrayList<Object> parametros = null;
	SubActivitiesTO subActivitiesTO = null;

	/**
	 * 
	 */
	public int insertar(ObjetoTO oSubActivitiesTO) throws Exception {
		subActivitiesTO = (SubActivitiesTO) oSubActivitiesTO;

		int procesado = 0;
		
		if(subActivitiesTO.getNumberInt()==0) {
			long id = IDDBFactorySql.getNextIDLong("SubActivity");
			subActivitiesTO.setNumber(String.valueOf(id));
			subActivitiesTO.setActive("1");
		}
		
		query.setLength(0);
		query.append("INSERT INTO subactivities (sAct_number,Act_Number,sAct_Name,sAct_Description,sAct_Active,sAct_Order) ");
		query.append("VALUES(?,?,?,?,?,?)");
		
		parametros = new ArrayList<Object>();
		parametros.add(subActivitiesTO.getNumberInt());
		parametros.add(subActivitiesTO.getActivityIDInt());
		parametros.add(subActivitiesTO.getNameAct());
		parametros.add(subActivitiesTO.getDescription());
		parametros.add(subActivitiesTO.getActiveInt());
		parametros.add(subActivitiesTO.getOrdenInt());
		
		procesado = JDBCUtil.executeUpdate(query, parametros);
		
		actualizarUsuariosRelacionados();
		
		return procesado;
	}

	/**
	 * 
	 */
	public int actualizar(ObjetoTO oSubActivitiesTO) throws Exception {
		subActivitiesTO = (SubActivitiesTO) oSubActivitiesTO;
		
		int procesado = 0;

		query.setLength(0);
		query.append("UPDATE subactivities SET ");
		query.append("Act_Number=?,sAct_Name=?,sAct_Description=?,sAct_Active=?,sAct_Order=? ");
		query.append("WHERE sAct_number=?");

		parametros = new ArrayList<Object>();
		parametros.add(subActivitiesTO.getActivityIDInt());
		parametros.add(subActivitiesTO.getNameAct());
		parametros.add(subActivitiesTO.getDescription());
		parametros.add(subActivitiesTO.getActiveInt());
		parametros.add(subActivitiesTO.getOrdenInt());
		parametros.add(subActivitiesTO.getNumberInt()); // primary key
		

		procesado = JDBCUtil.executeUpdate(query, parametros);

		actualizarUsuariosRelacionados();

		return procesado;
	}

	/**
	 * 
	 */
	public void eliminar(ObjetoTO oSubActivitiesTO) throws Exception {
		subActivitiesTO = (SubActivitiesTO) oSubActivitiesTO;

		query.setLength(0);
		query.append("DELETE FROM subactivities WHERE Act_Number=?");

		parametros = new ArrayList<Object>();
		parametros.add(subActivitiesTO.getNumberInt());

		JDBCUtil.executeUpdate(query, parametros);
	}

	/**
	 * 
	 */
	public boolean cargar(ObjetoTO oSubActivitiesTO) throws Exception {
		subActivitiesTO = (SubActivitiesTO) oSubActivitiesTO;

		query.setLength(0);
		query.append("SELECT * FROM subactivities WHERE sAct_number=").append(subActivitiesTO.getNumberInt());

		CachedRowSet crs = JDBCUtil.executeQuery(query, Thread.currentThread().getStackTrace()[1].getMethodName());
		CachedRowSet users = null;

		if (crs.next()) {
			load(crs, subActivitiesTO);
			
			return true;
		}
		return false;
	}

	/**
	 * 
	 */
	public ArrayList<SubActivitiesTO> listar() throws Exception {
		ArrayList<SubActivitiesTO> lista = new ArrayList<SubActivitiesTO>();

		query.setLength(0);
		query.append("SELECT * FROM subactivities ORDER BY sAct_number");

		CachedRowSet crs = JDBCUtil.executeQuery(query, Thread.currentThread().getStackTrace()[1].getMethodName());

		while (crs.next()) {
			subActivitiesTO = new SubActivitiesTO();
			load(crs, subActivitiesTO);
			lista.add(subActivitiesTO);
		}
		return lista;
	}
	
	/**
	 * 
	 */
	public ArrayList<SubActivitiesTO> listarByIdActivity(String idActivity) throws Exception {
		ArrayList<SubActivitiesTO> lista = new ArrayList<SubActivitiesTO>();

		query.setLength(0);
		query.append("SELECT * FROM subactivities WHERE Act_number=").append(idActivity);
		query.append(" AND sAct_active = 1 ");
		query.append("ORDER BY sAct_Order ");

		CachedRowSet crs = JDBCUtil.executeQuery(query, Thread.currentThread().getStackTrace()[1].getMethodName());

		while (crs.next()) {
			SubActivitiesTO s = new SubActivitiesTO();
			load(crs, s);
			lista.add(s);
		}
		return lista;
	}

	
	/**
	 * 
	 */
	public ArrayList<SubActivitiesTO> listarByNumberAndName(String number, String name) throws Exception {
		ArrayList<SubActivitiesTO> lista = new ArrayList<SubActivitiesTO>();

		query.setLength(0);
		query.append("SELECT * FROM subactivities WHERE Act_number=").append(number);
		query.append(" AND sAct_name='").append(name).append("' ");

		CachedRowSet crs = JDBCUtil.executeQuery(query, Thread.currentThread().getStackTrace()[1].getMethodName());

		while (crs.next()) {
			subActivitiesTO = new SubActivitiesTO();
			load(crs, subActivitiesTO);
			lista.add(subActivitiesTO);
		}
		return lista;
	}

	/**
	 * 
	 */
	public ArrayList<SubActivitiesTO> listarByNameAndId(String actName, String actNumber, String sActNumber)  throws Exception {
		ArrayList<SubActivitiesTO> lista = new ArrayList<SubActivitiesTO>();

		query.setLength(0);
		query.append("SELECT * FROM subactivities ");
		query.append(" WHERE sAct_Name = '").append(actName).append("' ");
		query.append(" AND Act_Number = ").append(actNumber);
		query.append(" AND sAct_number <> ").append(sActNumber);

		CachedRowSet crs = JDBCUtil.executeQuery(query, Thread.currentThread().getStackTrace()[1].getMethodName());

		while (crs.next()) {
			subActivitiesTO = new SubActivitiesTO();
			load(crs, subActivitiesTO);
			lista.add(subActivitiesTO);
		}
		return lista;
	}
	
	private void actualizarUsuariosRelacionados() throws Exception {
		// actualizamos los id de usuario por actividad
		if(subActivitiesTO.getIdPersons()!=null && subActivitiesTO.getIdPersons().size()>0) {
			Iterator<PerfilActionForm> ite = subActivitiesTO.getIdPersons().iterator();
			
			// Eliminamos los ids anteriores de la subactividad
			query.setLength(0);
			query.append("DELETE FROM act_user WHERE Act_Number= ").append(subActivitiesTO.getNumberInt());
			JDBCUtil.executeUpdate(query);

			// insertamos los usuarios actuales relacionados con la subactividad
			query.setLength(0);
			query.append("INSERT INTO act_user (Act_Number, idPerson) VALUES(?,?) ");
			
			ArrayList<Long> p = new ArrayList<Long>();
			p.add(Long.parseLong("0"));
			p.add(Long.parseLong("0"));
			
			Long id = Long.parseLong(subActivitiesTO.getNumber());
			
			PerfilActionForm paf = null;
			while(ite.hasNext()) {
				p.set(0, id);

				paf = (PerfilActionForm)ite.next();
				p.set(1, paf.getId());

				JDBCUtil.executeUpdate(query, p);
			}
		}
		
	}

	private void load(CachedRowSet crs, SubActivitiesTO subActivitiesTO) throws Exception {
		subActivitiesTO.setNumber(crs.getString("sAct_number"));
		subActivitiesTO.setActivityID(crs.getString("Act_Number"));
		subActivitiesTO.setNameAct(crs.getString("sAct_Name"));
		subActivitiesTO.setDescription(crs.getString("sAct_Description"));
		subActivitiesTO.setActive(crs.getString("sAct_Active"));
		subActivitiesTO.setOrden(crs.getString("sAct_Order")); 
		
		// cargamos los usuarios por actividad
		StringBuffer sql = new StringBuffer();;
		sql.append("SELECT b.* FROM act_user a, person b ");
		sql.append("WHERE b.idPerson=a.idPerson ");
		sql.append("AND Act_Number=").append(subActivitiesTO.getNumberInt()).append(" ");
		
		CachedRowSet users = JDBCUtil.executeQuery(sql, Thread.currentThread().getStackTrace()[1].getMethodName());

		PerfilActionForm p = null;
		PersonTO personTO = new PersonTO();
		PersonDAO personDAO = new PersonDAO();
		while(users.next()) {
			personDAO.load(users, personTO);
			
			subActivitiesTO.getIdPersons().add(new PerfilActionForm(personTO));
		}
		
	}

}
