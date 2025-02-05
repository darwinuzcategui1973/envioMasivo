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
import com.focus.qweb.to.CargoTO;

import sun.jdbc.rowset.CachedRowSet;

/**
 * 
 * @author JRivero
 * 
 */
public class CargoDAO implements ObjetoDAO {

	private StringBuffer query = new StringBuffer();
	private ArrayList<Object> parametros = null;

	/**
	 * 
	 */
	public int insertar(ObjetoTO oCargoTO) throws Exception {
		CargoTO cargoTO = null;
		cargoTO = (CargoTO) oCargoTO;
		
		if(cargoTO.getIdCargoInt()==0){
			int num = HandlerStruct.proximo("idcargo", "idcargo", "idcargo");
			cargoTO.setIdCargo(String.valueOf(num));
		}

		query.setLength(0);
		query.append("INSERT INTO tbl_cargo (idcargo,cargo,idarea,activec) ");
		query.append("VALUES(?,?,?,?)");
		
		parametros = new ArrayList<Object>();
		parametros.add(cargoTO.getIdCargoInt());
		parametros.add(cargoTO.getCargo());
		parametros.add(cargoTO.getIdAreaInt());
		parametros.add(cargoTO.getActivecInt());

		return JDBCUtil.executeUpdate(query, parametros);
	}

	/**
	 * 
	 */
	public int actualizar(ObjetoTO oCargoTO) throws Exception {
		CargoTO cargoTO = null;
		cargoTO = (CargoTO) oCargoTO;

		query.setLength(0);
		query.append("UPDATE tbl_cargo SET ");
		query.append("cargo=?,idarea=?,activec=? ");
		query.append("WHERE idcargo=?");

		parametros = new ArrayList<Object>();
		parametros.add(cargoTO.getCargo());
		parametros.add(cargoTO.getIdAreaInt());
		parametros.add(cargoTO.getActivecInt());
		parametros.add(cargoTO.getIdCargoInt()); // primary key


		return JDBCUtil.executeUpdate(query, parametros);
	}

	/**
	 * 
	 */
	public void eliminar(ObjetoTO oCargoTO) throws Exception {
		CargoTO cargoTO = null;
		cargoTO = (CargoTO) oCargoTO;

		query.setLength(0);
		query.append("DELETE FROM tbl_cargo WHERE idcargo=?");

		parametros = new ArrayList<Object>();
		parametros.add(cargoTO.getIdCargoInt());

		JDBCUtil.executeUpdate(query, parametros);
	}

	/**
	 * 
	 */
	public boolean cargar(ObjetoTO oCargoTO) throws Exception {
		CargoTO cargoTO = null;
		cargoTO = (CargoTO) oCargoTO;

		query.setLength(0);
		query.append("SELECT * FROM tbl_cargo WHERE idcargo=").append(cargoTO.getIdCargoInt());

		CachedRowSet crs = JDBCUtil.executeQuery(query, Thread.currentThread().getStackTrace()[1].getMethodName());

		if (crs.next()) {
			load(crs, cargoTO);
			
			AreaDAO oAreaDAO = new AreaDAO();
			cargoTO.setArea(new AreaTO());
			
			cargoTO.getArea().setIdarea(cargoTO.getIdArea());
			oAreaDAO.cargar(cargoTO.getArea());
			
			return true;
		}
		return false;
	}

	/**
	 * 
	 */
	public ArrayList<CargoTO> listar() throws Exception {
		CargoTO cargoTO = null;
		ArrayList<CargoTO> lista = new ArrayList<CargoTO>();

		query.setLength(0);
		query.append("SELECT * FROM tbl_cargo ORDER BY idcargo");

		CachedRowSet crs = JDBCUtil.executeQuery(query, Thread.currentThread().getStackTrace()[1].getMethodName());

		while (crs.next()) {
			cargoTO = new CargoTO();
			load(crs, cargoTO);
			lista.add(cargoTO);
		}
		return lista;
	}
	
	/**
	 * 
	 */
	public ArrayList<CargoTO> listarById(String id) throws Exception {
		CargoTO cargoTO = null;
		ArrayList<CargoTO> lista = new ArrayList<CargoTO>();

		query.setLength(0);
		query.append("SELECT * FROM tbl_cargo ");
		query.append("WHERE idcargo ");
		if(id.indexOf(",")==0) {
			query.append("=").append(id).append(" ");
		} else {
			query.append(" IN (").append(id).append(") ");
		}
		query.append("ORDER BY lower(cargo) ");

		CachedRowSet crs = JDBCUtil.executeQuery(query, Thread.currentThread().getStackTrace()[1].getMethodName());

		while (crs.next()) {
			cargoTO = new CargoTO();
			load(crs, cargoTO);
			lista.add(cargoTO);
		}
		return lista;
	}

	/**
	 * 
	 */
	public ArrayList<CargoTO> listarOrderByAreaCargo() throws Exception {
		CargoTO cargoTO = null;
		ArrayList<CargoTO> lista = new ArrayList<CargoTO>();

		query.setLength(0);
		query.append("SELECT * FROM tbl_cargo a, tbl_area b ");
		query.append("WHERE a.id_area=b.id_area ");
		query.append("ORDER BY lower(b.area), lower(a.cargo) ");

		CachedRowSet crs = JDBCUtil.executeQuery(query, Thread.currentThread().getStackTrace()[1].getMethodName());

		while (crs.next()) {
			cargoTO = new CargoTO();
			load(crs, cargoTO);
			lista.add(cargoTO);
		}
		return lista;
	}

	/**
	 * 
	 */
	public ArrayList<CargoTO> listarByIdArea(String idArea) throws Exception {
		CargoTO cargoTO = null;
		ArrayList<CargoTO> lista = new ArrayList<CargoTO>();

		query.setLength(0);
		query.append("SELECT * FROM tbl_cargo ");
		if(!ToolsHTML.isEmptyOrNull(idArea)) {
			if(idArea.indexOf(",")==0) {
				query.append("WHERE idarea=").append(idArea).append(" ");
			} else {
				query.append("WHERE idarea IN (").append(idArea).append(") ");
			}
		}
		query.append("ORDER BY lower(cargo) ");

		CachedRowSet crs = JDBCUtil.executeQuery(query, Thread.currentThread().getStackTrace()[1].getMethodName());

		while (crs.next()) {
			cargoTO = new CargoTO();
			load(crs, cargoTO);
			lista.add(cargoTO);
		}
		return lista;
	}

	/**
	 * 
	 */
	public Map<String,CargoTO> listarCargoAlls() throws Exception {
		CargoTO cargoTO = null;
		Map<String,CargoTO> lista = new HashMap<String,CargoTO>();

		query.setLength(0);
		query.append("SELECT * FROM tbl_cargo");

		CachedRowSet crs = JDBCUtil.executeQuery(query, Thread.currentThread().getStackTrace()[1].getMethodName());

		while (crs.next()) {
			cargoTO = new CargoTO();
			load(crs, cargoTO);
			lista.put(cargoTO.getIdCargo(),cargoTO);
		}
		return lista;
	}

	private void load(CachedRowSet crs, CargoTO cargoTO) throws SQLException {
		cargoTO.setIdCargo(crs.getString("idcargo"));
		cargoTO.setCargo(crs.getString("cargo"));
		cargoTO.setIdArea(crs.getString("idarea"));
		cargoTO.setActivec(crs.getString("activec"));
	}

}
