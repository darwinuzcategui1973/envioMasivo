package com.focus.qweb.dao;

import java.util.ArrayList;

import sun.jdbc.rowset.CachedRowSet;

import com.desige.webDocuments.persistent.utils.JDBCUtil;
import com.focus.qweb.interfaces.ObjetoDAO;
import com.focus.qweb.interfaces.ObjetoTO;
import com.focus.qweb.to.TitulosTO;

/**
 * 
 * @author ybracho
 * 
 */
public class TitulosDAO implements ObjetoDAO {

	private StringBuffer query = new StringBuffer();
	private ArrayList<Object> parametros = null;
	TitulosTO tituloTO = null;

	/**
	 * 
	 */
	public int insertar(ObjetoTO oTitulosTO) throws Exception {
		tituloTO = (TitulosTO) oTitulosTO;

		query.setLength(0);
		query.append("INSERT INTO titulos (posicion,titulo) VALUES(?,?)");

		parametros = new ArrayList<Object>();
		parametros.add(tituloTO.getPosicionInt());
		parametros.add(tituloTO.getTitulo());

		return JDBCUtil.executeUpdate(query, parametros);
	}

	/**
	 * 
	 */
	public int actualizar(ObjetoTO oTitulosTO) throws Exception {
		tituloTO = (TitulosTO) oTitulosTO;

		query.setLength(0);
		query.append("UPDATE titulos SET titulo=? WHERE posicion=CAST(? AS INT)");

		parametros = new ArrayList<Object>();
		parametros.add(tituloTO.getTitulo());
		parametros.add(tituloTO.getPosicionInt());

		return JDBCUtil.executeUpdate(query, parametros);
	}

	/**
	 * 
	 */
	public void eliminar(ObjetoTO oTitulosTO) throws Exception {
		tituloTO = (TitulosTO) oTitulosTO;

		query.setLength(0);
		query.append("DELETE FROM titulo WHERE posicion=?");

		parametros = new ArrayList<Object>();
		parametros.add(tituloTO.getPosicionInt());

		JDBCUtil.executeUpdate(query, parametros);
	}

	/**
	 * 
	 */
	public boolean cargar(ObjetoTO oTitulosTO) throws Exception {
		tituloTO = (TitulosTO) oTitulosTO;

		query.setLength(0);
		query.append("SELECT * FROM titulo WHERE posicion=").append(tituloTO.getPosicionInt());

		CachedRowSet crs = JDBCUtil.executeQuery(query, Thread.currentThread().getStackTrace()[1].getMethodName());

		if (crs.next()) {
			tituloTO.setTitulo(crs.getString("titulo"));
			return true;
		}
		return false;
	}

	/**
	 * 
	 */
	public ArrayList listar() throws Exception {
		ArrayList lista = new ArrayList();

		query.setLength(0);
		query.append("SELECT * FROM titulo ORDER BY posicion");

		CachedRowSet crs = JDBCUtil.executeQuery(query, Thread.currentThread().getStackTrace()[1].getMethodName());

		while (crs.next()) {
			tituloTO = new TitulosTO();
			tituloTO.setPosicion(crs.getString("posicion"));
			tituloTO.setTitulo(crs.getString("titulo"));
			lista.add(tituloTO);
		}
		return lista;
	}

}
