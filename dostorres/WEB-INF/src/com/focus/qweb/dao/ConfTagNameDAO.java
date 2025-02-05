package com.focus.qweb.dao;

import java.sql.SQLException;
import java.util.ArrayList;

import sun.jdbc.rowset.CachedRowSet;

import com.desige.webDocuments.persistent.utils.JDBCUtil;
import com.focus.qweb.interfaces.ObjetoDAO;
import com.focus.qweb.interfaces.ObjetoTO;
import com.focus.qweb.to.ConfTagNameTO;

/**
 * 
 * @author JRivero
 * 
 */
public class ConfTagNameDAO implements ObjetoDAO {

	private StringBuffer query = new StringBuffer();
	private ArrayList<Object> parametros = null;
	ConfTagNameTO confTagNameTO = null;

	/**
	 * 
	 */
	public int insertar(ObjetoTO oConfTagNameTO) throws Exception {
		confTagNameTO = (ConfTagNameTO) oConfTagNameTO;

		query.setLength(0);
		query.append("INSERT INTO tbl_conftagname (idtagname,tipotag,tipoalarma,valor,active) ");
		query.append("VALUES(?,?,?,?)");
		
		parametros = new ArrayList<Object>();
		parametros.add(confTagNameTO.getIdTagNameInt());
		parametros.add(confTagNameTO.getTipoTag());
		parametros.add(confTagNameTO.getTipoAlarma());
		parametros.add(confTagNameTO.getValor());
		parametros.add(confTagNameTO.getActiveInt());

		return JDBCUtil.executeUpdate(query, parametros);
	}

	/**
	 * 
	 */
	public int actualizar(ObjetoTO oConfTagNameTO) throws Exception {
		confTagNameTO = (ConfTagNameTO) oConfTagNameTO;

		query.setLength(0);
		query.append("UPDATE tbl_conftagname SET ");
		query.append("tipotag=?,tipoalarma=?,valor=?,active=? ");
		query.append("WHERE idtagname=?");

		parametros = new ArrayList<Object>();
		parametros.add(confTagNameTO.getTipoTag());
		parametros.add(confTagNameTO.getTipoAlarma());
		parametros.add(confTagNameTO.getValor());
		parametros.add(confTagNameTO.getActiveInt());
		parametros.add(confTagNameTO.getIdTagNameInt()); // primary key


		return JDBCUtil.executeUpdate(query, parametros);
	}

	/**
	 * 
	 */
	public void eliminar(ObjetoTO oConfTagNameTO) throws Exception {
		confTagNameTO = (ConfTagNameTO) oConfTagNameTO;

		query.setLength(0);
		query.append("DELETE FROM tbl_conftagname WHERE idtagname=?");

		parametros = new ArrayList<Object>();
		parametros.add(confTagNameTO.getIdTagNameInt());

		JDBCUtil.executeUpdate(query, parametros);
	}

	/**
	 * 
	 */
	public boolean cargar(ObjetoTO oConfTagNameTO) throws Exception {
		confTagNameTO = (ConfTagNameTO) oConfTagNameTO;

		query.setLength(0);
		query.append("SELECT * FROM tbl_conftagname WHERE idtagname=").append(confTagNameTO.getIdTagNameInt());

		CachedRowSet crs = JDBCUtil.executeQuery(query, Thread.currentThread().getStackTrace()[1].getMethodName());

		if (crs.next()) {
			load(crs, confTagNameTO);
			return true;
		}
		return false;
	}

	/**
	 * 
	 */
	public ArrayList<ConfTagNameTO> listar() throws Exception {
		ArrayList<ConfTagNameTO> lista = new ArrayList<ConfTagNameTO>();

		query.setLength(0);
		query.append("SELECT * FROM tbl_conftagname ORDER BY idtagname");

		CachedRowSet crs = JDBCUtil.executeQuery(query, Thread.currentThread().getStackTrace()[1].getMethodName());

		while (crs.next()) {
			confTagNameTO = new ConfTagNameTO();
			load(crs, confTagNameTO);
			lista.add(confTagNameTO);
		}
		return lista;
	}
	
	/**
	 * 
	 */
	public ArrayList<ConfTagNameTO> listar(String active, String tipoTag, boolean ordenado) throws Exception {
		ArrayList<ConfTagNameTO> lista = new ArrayList<ConfTagNameTO>();

		query.setLength(0);
		query.append("SELECT * FROM tbl_conftagname ");
		query.append("WHERE active=").append(active).append(" ");
		query.append("AND tipoTag='").append(tipoTag).append("' ");
		if(ordenado) {
			query.append("ORDER BY tipoTag ");
		}

		CachedRowSet crs = JDBCUtil.executeQuery(query, Thread.currentThread().getStackTrace()[1].getMethodName());

		while (crs.next()) {
			confTagNameTO = new ConfTagNameTO();
			load(crs, confTagNameTO);
			lista.add(confTagNameTO);
		}
		return lista;
	}
	
	private void load(CachedRowSet crs, ConfTagNameTO confTagNameTO) throws SQLException {
		confTagNameTO.setIdTagName(crs.getString("idtagname"));
		confTagNameTO.setTipoTag(crs.getString("tipotag"));
		confTagNameTO.setTipoAlarma(crs.getString("tipoalarma"));
		confTagNameTO.setValor(crs.getString("valor"));
		confTagNameTO.setActive(crs.getString("active"));
	}

}
