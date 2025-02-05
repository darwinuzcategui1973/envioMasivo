package com.focus.qweb.dao;

import java.sql.SQLException;
import java.util.ArrayList;

import sun.jdbc.rowset.CachedRowSet;

import com.desige.webDocuments.persistent.utils.JDBCUtil;
import com.desige.webDocuments.utils.ToolsHTML;
import com.focus.qweb.interfaces.ObjetoDAO;
import com.focus.qweb.interfaces.ObjetoTO;
import com.focus.qweb.to.SacopIntouchHijoTO;

/**
 * 
 * @author JRivero
 * 
 */
public class SacopIntouchHijoDAO implements ObjetoDAO {

	private StringBuffer query = new StringBuffer();
	private ArrayList<Object> parametros = null;
	SacopIntouchHijoTO sacopIntouchHijoTO = null;

	/**
	 * 
	 */
	public int insertar(ObjetoTO oSacopIntouchHijoTO) throws Exception {
		sacopIntouchHijoTO = (SacopIntouchHijoTO) oSacopIntouchHijoTO;

		query.setLength(0);
		query.append("INSERT INTO tbl_sacop_intouch_hijo (idtagname,tagname,disparadasacop,active,idplanillasacop1) ");
		query.append("VALUES(?,?,?,?)");
		
		parametros = new ArrayList<Object>();
		parametros.add(sacopIntouchHijoTO.getIdTagNameInt());
		parametros.add(sacopIntouchHijoTO.getTagName());
		parametros.add(sacopIntouchHijoTO.getDisparadaSacopInt());
		parametros.add(sacopIntouchHijoTO.getActiveInt());
		parametros.add(sacopIntouchHijoTO.getIdPlanillaSacop1());

		return JDBCUtil.executeUpdate(query, parametros);
	}

	/**
	 * 
	 */
	public int actualizar(ObjetoTO oSacopIntouchHijoTO) throws Exception {
		sacopIntouchHijoTO = (SacopIntouchHijoTO) oSacopIntouchHijoTO;

		query.setLength(0);
		query.append("UPDATE tbl_sacop_intouch_hijo SET ");
		query.append("tagname=?,disparadasacop=?,active=?,idplanillasacop1=? ");
		query.append("WHERE idtagname=?");

		parametros = new ArrayList<Object>();
		parametros.add(sacopIntouchHijoTO.getTagName());
		parametros.add(sacopIntouchHijoTO.getDisparadaSacopInt());
		parametros.add(sacopIntouchHijoTO.getActiveInt());
		parametros.add(sacopIntouchHijoTO.getIdPlanillaSacop1());
		parametros.add(sacopIntouchHijoTO.getIdTagNameInt()); // primary key


		return JDBCUtil.executeUpdate(query, parametros);
	}

	/**
	 * 
	 */
	public void eliminar(ObjetoTO oSacopIntouchHijoTO) throws Exception {
		sacopIntouchHijoTO = (SacopIntouchHijoTO) oSacopIntouchHijoTO;

		query.setLength(0);
		query.append("DELETE FROM tbl_sacop_intouch_hijo WHERE idtagname=?");

		parametros = new ArrayList<Object>();
		parametros.add(sacopIntouchHijoTO.getIdTagNameInt());

		JDBCUtil.executeUpdate(query, parametros);
	}

	/**
	 * 
	 */
	public boolean cargar(ObjetoTO oSacopIntouchHijoTO) throws Exception {
		sacopIntouchHijoTO = (SacopIntouchHijoTO) oSacopIntouchHijoTO;

		query.setLength(0);
		query.append("SELECT * FROM tbl_sacop_intouch_hijo WHERE idtagname=").append(sacopIntouchHijoTO.getIdTagNameInt());

		CachedRowSet crs = JDBCUtil.executeQuery(query, Thread.currentThread().getStackTrace()[1].getMethodName());

		if (crs.next()) {
			load(crs, sacopIntouchHijoTO);
			return true;
		}
		return false;
	}

	/**
	 * 
	 */
	public ArrayList<SacopIntouchHijoTO> listar() throws Exception {
		ArrayList<SacopIntouchHijoTO> lista = new ArrayList<SacopIntouchHijoTO>();

		query.setLength(0);
		query.append("SELECT * FROM tbl_sacop_intouch_hijo ORDER BY idtagname");

		CachedRowSet crs = JDBCUtil.executeQuery(query, Thread.currentThread().getStackTrace()[1].getMethodName());

		while (crs.next()) {
			sacopIntouchHijoTO = new SacopIntouchHijoTO();
			load(crs, sacopIntouchHijoTO);
			lista.add(sacopIntouchHijoTO);
		}
		return lista;
	}
	
	/**
	 * 
	 */
	public ArrayList<SacopIntouchHijoTO> listar(String active, String idplanillasacop1) throws Exception {
		return listar(active, idplanillasacop1, null); 
	}

	/**
	 * 
	 */
	public ArrayList<SacopIntouchHijoTO> listar(String active, String idplanillasacop1, String tagname) throws Exception {
		ArrayList<SacopIntouchHijoTO> lista = new ArrayList<SacopIntouchHijoTO>();

		query.setLength(0);
		query.append("SELECT * FROM tbl_sacop_intouch_hijo ");
		query.append("WHERE active=").append(active).append(" ");
		if(idplanillasacop1!=null && !idplanillasacop1.equals("0")) {
			query.append("AND idplanillasacop1=").append(idplanillasacop1).append(" ");
		}
		if (!ToolsHTML.isEmptyOrNull(tagname.trim())) {
			query.append("AND tagname=").append(tagname.trim()).append(" ");
		}

		CachedRowSet crs = JDBCUtil.executeQuery(query, Thread.currentThread().getStackTrace()[1].getMethodName());

		while (crs.next()) {
			sacopIntouchHijoTO = new SacopIntouchHijoTO();
			load(crs, sacopIntouchHijoTO);
			lista.add(sacopIntouchHijoTO);
		}
		return lista;
	}

	/**
	 * 
	 */
	public ArrayList<SacopIntouchHijoTO> listarByTagNameActive(String active, String idplanillasacop1) throws Exception {
		ArrayList<SacopIntouchHijoTO> lista = new ArrayList<SacopIntouchHijoTO>();

		query.setLength(0);
		query.append("SELECT a.* FROM tbl_sacop_intouch_hijo a, tbl_tagname b ");
		query.append("WHERE b.tagname=a.tagname ");
		query.append("AND a.active=").append(active).append(" ");
		query.append("AND b.active=").append(active).append(" ");
		if(idplanillasacop1!=null && !idplanillasacop1.equals("0")) {
			query.append("AND a.idplanillasacop1=").append(idplanillasacop1).append(" ");
		}

		CachedRowSet crs = JDBCUtil.executeQuery(query, Thread.currentThread().getStackTrace()[1].getMethodName());

		while (crs.next()) {
			sacopIntouchHijoTO = new SacopIntouchHijoTO();
			load(crs, sacopIntouchHijoTO);
			lista.add(sacopIntouchHijoTO);
		}
		return lista;
	}

	private void load(CachedRowSet crs, SacopIntouchHijoTO sacopIntouchHijoTO) throws SQLException {
		sacopIntouchHijoTO.setIdTagName(crs.getString("idtagname"));
		sacopIntouchHijoTO.setTagName(crs.getString("tagname"));
		sacopIntouchHijoTO.setDisparadaSacop(crs.getString("disparadasacop"));
		sacopIntouchHijoTO.setActive(crs.getString("active"));
		sacopIntouchHijoTO.setIdPlanillaSacop1(crs.getString("idplanillasacop1"));
	}

}
