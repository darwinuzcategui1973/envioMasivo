package com.focus.qweb.dao;

import java.util.ArrayList;

import sun.jdbc.rowset.CachedRowSet;

import com.desige.webDocuments.persistent.utils.JDBCUtil;
import com.focus.qweb.interfaces.ObjetoDAO;
import com.focus.qweb.interfaces.ObjetoTO;
import com.focus.qweb.to.EquivalenciasTO;

/**
 * 
 * @author JRivero
 *
 */
public class EquivalenciasDAO implements ObjetoDAO {

	private StringBuffer query = new StringBuffer();
	private ArrayList parametros = null;
	EquivalenciasTO equiTO = null;

	/**
	 * 
	 */
	public int insertar(ObjetoTO oEquivalenciasTO) throws Exception {
		equiTO = (EquivalenciasTO) oEquivalenciasTO;

		query.setLength(0);
		query.append("INSERT INTO equivalencias (indice,nombre,campo,posicion,columna,valor,indexar,activo) VALUES(?,?,?,?,?,?,?,?)");

		parametros = new ArrayList();
		parametros.add(new Integer(equiTO.getIndice()));
		parametros.add(equiTO.getNombre());
		parametros.add(equiTO.getCampo());
		parametros.add(new Integer(equiTO.getPosicion()));
		parametros.add(equiTO.getColumna());
		parametros.add(equiTO.getValor());
		parametros.add(new Integer(equiTO.getIndexar()));
		parametros.add(new Integer(equiTO.getActivo()));

		return JDBCUtil.executeUpdate(query, parametros);
	}

	/**
	 * 
	 */
	public int actualizar(ObjetoTO oEquivalenciasTO) throws Exception {
		equiTO = (EquivalenciasTO) oEquivalenciasTO;

		query.setLength(0);
		query.append("UPDATE equivalencias SET campo=?,posicion=?,columna=?,valor=?,indexar=?,activo=? WHERE indice=? and nombre=?");

		parametros = new ArrayList();
		parametros.add(equiTO.getCampo());
		parametros.add(new Integer(equiTO.getPosicion()));
		parametros.add(equiTO.getColumna());
		parametros.add(equiTO.getValor());
		parametros.add(new Integer(equiTO.getIndexar()));
		parametros.add(new Integer(equiTO.getActivo()));
		parametros.add(new Integer(equiTO.getIndice()));
		parametros.add(equiTO.getNombre());

		return JDBCUtil.executeUpdate(query, parametros);
	}

	/**
	 * 
	 * @param oEquivalenciasTO
	 * @return
	 * @throws Exception
	 */
	public int actualizarAdicionales(ObjetoTO oEquivalenciasTO)
			throws Exception {
		equiTO = (EquivalenciasTO) oEquivalenciasTO;

		query.setLength(0);
		query.append("UPDATE equivalencias SET campo=? ");
		query.append("WHERE campo like '")
				.append(equiTO.getCampo().substring(0,
						equiTO.getCampo().indexOf("-"))).append("%'");

		parametros = new ArrayList();
		parametros.add(equiTO.getCampo());

		return JDBCUtil.executeUpdate(query, parametros);
	}

	/**
	 * 
	 * @param oEquivalenciasTO
	 * @throws Exception
	 */
	public void activar(ObjetoTO oEquivalenciasTO) throws Exception {
		equiTO = (EquivalenciasTO) oEquivalenciasTO;

		query.setLength(0);
		query.append("UPDATE equivalencias SET activo=1 WHERE nombre=?");

		parametros = new ArrayList();
		parametros.add(equiTO.getNombre());

		JDBCUtil.executeUpdate(query, parametros);
	}

	/**
	 * 
	 * @param oEquivalenciasTO
	 * @throws Exception
	 */
	public void desactivar(ObjetoTO oEquivalenciasTO) throws Exception {
		query.setLength(0);
		query.append("UPDATE equivalencias SET activo=0");

		JDBCUtil.executeUpdate(query);
	}

	/**
	 * 
	 * @param oEquivalenciasTO
	 * @return
	 * @throws Exception
	 */
	public int getLastIndice(ObjetoTO oEquivalenciasTO) throws Exception {

		query.setLength(0);
		query.append("select max(indice) from equivalencias");

		CachedRowSet crs = JDBCUtil.executeQuery(query, Thread.currentThread().getStackTrace()[1].getMethodName());
		if (crs.next()) {
			return crs.getInt(1);
		}

		return 0;
	}

	/**
	 * 
	 */
	public void eliminar(ObjetoTO oEquivalenciasTO) throws Exception {
		equiTO = (EquivalenciasTO) oEquivalenciasTO;

		query.setLength(0);
		query.append("DELETE FROM equivalencias WHERE nombre=?");

		parametros = new ArrayList();
		parametros.add(equiTO.getNombre());

		JDBCUtil.executeUpdate(query, parametros);
	}

	/**
	 * 
	 * @param campo
	 * @throws Exception
	 */
	public void eliminarPorCampo(String campo) throws Exception {
		query.setLength(0);
		query.append("DELETE FROM equivalencias WHERE campo like '")
				.append(campo).append("-%'");

		JDBCUtil.executeUpdate(query, parametros);
	}

	/**
	 * 
	 */
	public boolean cargar(ObjetoTO oEquivalenciasTO) throws Exception {
		equiTO = (EquivalenciasTO) oEquivalenciasTO;

		query.setLength(0);
		query.append("SELECT * FROM equivalencias WHERE indice=").append(equiTO.getIndice()).append(" and nombre=").append(equiTO.getNombre());

		CachedRowSet crs = JDBCUtil.executeQuery(query, Thread.currentThread().getStackTrace()[1].getMethodName());
		if (crs.next()) {
			equiTO.setCampo(crs.getString("campo"));
			equiTO.setNombre(crs.getString("nombre"));
			equiTO.setPosicion(crs.getString("posicion"));
			equiTO.setColumna(crs.getString("columna"));
			equiTO.setValor(crs.getString("valor"));
			equiTO.setIndexar(crs.getString("indexar"));
			equiTO.setActivo(crs.getString("activo"));
			return true;
		}
		return false;
	}

	/**
	 * 
	 * @param oEquivalenciasTO
	 * @throws Exception
	 */
	public void cargarPorCampo(ObjetoTO oEquivalenciasTO) throws Exception {
		equiTO = (EquivalenciasTO) oEquivalenciasTO;

		query.setLength(0);
		query.append("SELECT * FROM equivalencias WHERE campo like '")
				.append(equiTO.getCampo()).append("%'");

		CachedRowSet crs = JDBCUtil.executeQuery(query, Thread.currentThread().getStackTrace()[1].getMethodName());
		if (crs.next()) {
			equiTO.setIndice(crs.getString("indice"));
			equiTO.setCampo(crs.getString("campo"));
			equiTO.setNombre(crs.getString("nombre"));
			equiTO.setPosicion(crs.getString("posicion"));
			equiTO.setColumna(crs.getString("columna"));
			equiTO.setValor(crs.getString("valor"));
			equiTO.setIndexar(crs.getString("indexar"));
			equiTO.setActivo(crs.getString("activo"));
		}
	}

	/**
	 * 
	 */
	public ArrayList listar() throws Exception {

		query.setLength(0);
		query.append("SELECT * FROM equivalencias WHERE activo=1 ORDER BY indice");

		ArrayList lista = new ArrayList();
		CachedRowSet crs = JDBCUtil.executeQuery(query, Thread.currentThread().getStackTrace()[1].getMethodName());
		while (crs.next()) {
			equiTO = new EquivalenciasTO();
			equiTO.setIndice(crs.getString("indice"));
			equiTO.setNombre(crs.getString("nombre"));
			equiTO.setCampo(crs.getString("campo"));
			equiTO.setPosicion(crs.getString("posicion"));
			equiTO.setColumna(crs.getString("columna"));
			equiTO.setValor(crs.getString("valor"));
			equiTO.setIndexar(crs.getString("indexar"));
			equiTO.setActivo(crs.getString("activo"));
			lista.add(equiTO);
		}
		return lista;
	}

	/**
	 * 
	 * @param nombre
	 * @return
	 * @throws Exception
	 */
	public ArrayList listar(String nombre) throws Exception {

		query.setLength(0);
		query.append("SELECT * FROM equivalencias WHERE nombre=? ORDER BY indice");

		parametros = new ArrayList();
		parametros.add(nombre);

		ArrayList lista = new ArrayList();
		CachedRowSet crs = JDBCUtil.executeQuery(query, parametros, Thread.currentThread().getStackTrace()[1].getMethodName());
		while (crs.next()) {
			equiTO = new EquivalenciasTO();
			equiTO.setIndice(crs.getString("indice"));
			equiTO.setNombre(crs.getString("nombre"));
			equiTO.setCampo(crs.getString("campo"));
			equiTO.setPosicion(crs.getString("posicion"));
			equiTO.setColumna(crs.getString("columna"));
			equiTO.setValor(crs.getString("valor"));
			equiTO.setIndexar(crs.getString("indexar"));
			equiTO.setActivo(crs.getString("activo"));
			lista.add(equiTO);
		}
		return lista;
	}

	/**
	 * 
	 * @return
	 * @throws Exception
	 */
	public ArrayList listarNombre() throws Exception {
		query.setLength(0);
		query.append("SELECT nombre,activo FROM equivalencias GROUP BY nombre,activo ORDER BY nombre");

		ArrayList lista = new ArrayList();
		CachedRowSet crs = JDBCUtil.executeQuery(query, Thread.currentThread().getStackTrace()[1].getMethodName());
		while (crs.next()) {
			equiTO = new EquivalenciasTO();
			equiTO.setNombre(crs.getString("nombre"));
			equiTO.setActivo(crs.getString("activo"));
			lista.add(equiTO);

		}
		return lista;
	}

}
