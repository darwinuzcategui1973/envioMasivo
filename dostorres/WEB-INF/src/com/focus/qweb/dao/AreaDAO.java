package com.focus.qweb.dao;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.desige.webDocuments.persistent.managers.HandlerStruct;
import com.desige.webDocuments.persistent.utils.JDBCUtil;
import com.desige.webDocuments.utils.ToolsHTML;
import com.focus.qweb.interfaces.ObjetoDAO;
import com.focus.qweb.interfaces.ObjetoTO;
import com.focus.qweb.to.AreaTO;

import sun.jdbc.rowset.CachedRowSet;

/**
 * 
 * @author JRivero
 * 
 */
public class AreaDAO implements ObjetoDAO {

	private StringBuffer query = new StringBuffer();
	private ArrayList<Object> parametros = null;
	AreaTO areaTO = null;

	/**
	 * 
	 */
	public int insertar(ObjetoTO oAreaTO) throws Exception {
		areaTO = (AreaTO) oAreaTO;

		if(areaTO.getIdareaInt()==0) {
			int idarea = HandlerStruct.proximo("idarea", "idarea", "idarea");
			areaTO.setIdarea(String.valueOf(idarea));
		}
		
		query.setLength(0);
		query.append("INSERT INTO tbl_area (idarea,area,activea,prefijo) ");
		query.append("VALUES(?,?,?,?)");

		parametros = new ArrayList<Object>();
		parametros.add(areaTO.getIdareaInt());
		parametros.add(areaTO.getArea());
		parametros.add(areaTO.getActiveaInt());
		parametros.add(String.valueOf(areaTO.getPrefijo().concat("     ")).substring(0,5).trim());

		return JDBCUtil.executeUpdate(query, parametros);
	}

	/**
	 * 
	 */
	public int actualizar(ObjetoTO oAreaTO) throws Exception {
		areaTO = (AreaTO) oAreaTO;

		query.setLength(0);
		query.append("UPDATE tbl_area SET ");
		query.append("area=?,activea=?,prefijo=? ");
		query.append("WHERE idarea=?");

		parametros = new ArrayList<Object>();
		parametros.add(areaTO.getArea());
		parametros.add(areaTO.getActiveaInt());
		parametros.add(String.valueOf(areaTO.getPrefijo().concat("     ")).substring(0,5).trim());
		parametros.add(areaTO.getIdareaInt()); // primary key

		return JDBCUtil.executeUpdate(query, parametros);
	}

	/**
	 * 
	 */
	public void eliminar(ObjetoTO oAreaTO) throws Exception {
		areaTO = (AreaTO) oAreaTO;

		query.setLength(0);
		query.append("DELETE FROM tbl_area WHERE idarea=?");

		parametros = new ArrayList<Object>();
		parametros.add(areaTO.getIdareaInt());

		JDBCUtil.executeUpdate(query, parametros);
	}

	/**
	 * 
	 */
	public boolean cargar(ObjetoTO oAreaTO) throws Exception {
		areaTO = (AreaTO) oAreaTO;

		query.setLength(0);
		query.append("SELECT * FROM tbl_area WHERE idarea=").append(areaTO.getIdareaInt());

		CachedRowSet crs = JDBCUtil.executeQuery(query, Thread.currentThread().getStackTrace()[1].getMethodName());

		if (crs.next()) {
			load(crs, areaTO);
			return true;
		}
		return false;
	}

	/**
	 * 
	 */
	public ArrayList<AreaTO> listar() throws Exception {
		ArrayList<AreaTO> lista = new ArrayList<AreaTO>();

		query.setLength(0);
		query.append("SELECT * FROM tbl_area ORDER BY idarea");

		CachedRowSet crs = JDBCUtil.executeQuery(query, Thread.currentThread().getStackTrace()[1].getMethodName());

		while (crs.next()) {
			areaTO = new AreaTO();
			load(crs, areaTO);
			lista.add(areaTO);
		}
		return lista;
	}
	
	/**
	 * 
	 */
	public ArrayList<AreaTO> listarById(String id) throws Exception {
		ArrayList<AreaTO> lista = new ArrayList<AreaTO>();

		query.setLength(0);
		query.append("SELECT * FROM tbl_area ");
		query.append("WHERE idarea ");
		if(id.indexOf(",")==0) {
			query.append("=").append(id).append(" ");
		} else {
			query.append(" IN (").append(id).append(") ");
		}
		query.append("ORDER BY lower(area)");

		CachedRowSet crs = JDBCUtil.executeQuery(query, Thread.currentThread().getStackTrace()[1].getMethodName());

		while (crs.next()) {
			areaTO = new AreaTO();
			load(crs, areaTO);
			lista.add(areaTO);
		}
		return lista;
	}

	/**
	 * 
	 */
	public ArrayList<AreaTO> listarAreaAlls(String nameArea) throws Exception {
		ArrayList<AreaTO> lista = new ArrayList<AreaTO>();

		query.setLength(0);
		query.append("SELECT * FROM tbl_area ");
		if (!ToolsHTML.isEmptyOrNull(nameArea)) {
			query.append("WHERE area='").append(nameArea).append("' ");
		}
		query.append("ORDER BY lower(area)");

		CachedRowSet crs = JDBCUtil.executeQuery(query, Thread.currentThread().getStackTrace()[1].getMethodName());

		while (crs.next()) {
			areaTO = new AreaTO();
			load(crs, areaTO);
			lista.add(areaTO);
		}
		return lista;
	}

	/**
	 * 
	 */
	public Map<String,AreaTO> listarAreaAlls() throws Exception {
		Map<String,AreaTO> lista = new HashMap<String,AreaTO>();

		query.setLength(0);
		query.append("SELECT * FROM tbl_area");

		CachedRowSet crs = JDBCUtil.executeQuery(query, Thread.currentThread().getStackTrace()[1].getMethodName());

		while (crs.next()) {
			areaTO = new AreaTO();
			load(crs, areaTO);
			lista.put(areaTO.getIdarea(),areaTO);
		}
		return lista;
	}
	
	private void load(CachedRowSet crs, AreaTO areaTO) throws SQLException {
		areaTO.setIdarea(crs.getString("idarea"));
		areaTO.setArea(crs.getString("area"));
		areaTO.setActivea(crs.getString("activea"));
		areaTO.setPrefijo(crs.getString("prefijo"));
	}

}
