package com.focus.qweb.dao;

import java.util.ArrayList;
import java.util.HashMap;

import sun.jdbc.rowset.CachedRowSet;

import com.desige.webDocuments.persistent.utils.JDBCUtil;
import com.focus.qweb.interfaces.ObjetoDAO;
import com.focus.qweb.interfaces.ObjetoTO;
import com.focus.qweb.to.IndiceTO;

/**
 * 
 * @author JRivero
 *
 */
public class IndiceDAO implements ObjetoDAO {

	private StringBuffer query = new StringBuffer();
	private ArrayList parametros = null;
	IndiceTO indiceTO = null;

	/**
	 * 
	 */
	public int insertar(ObjetoTO oIndiceTO) throws Exception {
		indiceTO = (IndiceTO)oIndiceTO;

		query.setLength(0);
		query.append("INSERT INTO indice (clave,valor,indice) VALUES(?,?,?)");
		
		parametros = new ArrayList();
		parametros.add(indiceTO.getClave());
		parametros.add(indiceTO.getValor());
		parametros.add(new Integer(indiceTO.getIndice()));
		
		return JDBCUtil.executeUpdate(query, parametros);
	}
	
	/**
	 * 
	 */
	public int actualizar(ObjetoTO oIndiceTO) throws Exception  {
		indiceTO = (IndiceTO)oIndiceTO;

		query.setLength(0);
		query.append("UPDATE indice SET valor=? WHERE clave=? and indice=?");
		
		parametros = new ArrayList();
		parametros.add(indiceTO.getValor());
		parametros.add(indiceTO.getClave());
		parametros.add(new Integer(indiceTO.getIndice()));
		
		return JDBCUtil.executeUpdate(query, parametros);
	}

	/**
	 * 
	 */
	public void eliminar(ObjetoTO oIndiceTO) throws Exception  {
		indiceTO = (IndiceTO)oIndiceTO;

		query.setLength(0);
		query.append("DELETE FROM indice WHERE clave=? and indice=?");
		
		parametros = new ArrayList();
		parametros.add(indiceTO.getClave());
		parametros.add(new Integer(indiceTO.getIndice()));
		
		JDBCUtil.executeUpdate(query, parametros);
	}

	/**
	 * 
	 */
	public boolean cargar(ObjetoTO oIndiceTO) throws Exception  {
		indiceTO = (IndiceTO)oIndiceTO;

		query.setLength(0);
		query.append("SELECT valor FROM indice WHERE clave=").append(indiceTO.getClave()).append(" AND indice=").append(indiceTO.getIndice());
		
		CachedRowSet crs = JDBCUtil.executeQuery(query, Thread.currentThread().getStackTrace()[1].getMethodName());
		
		if(crs.next()) {
			indiceTO.setValor(crs.getString("valor"));
			return true;
		}
		return false;
	}

	/**
	 * 
	 */
	public ArrayList listar() throws Exception  {
		ArrayList lista = new ArrayList();
		
		query.setLength(0);
		query.append("SELECT * FROM indice ORDER BY clave");
		
		CachedRowSet crs = JDBCUtil.executeQuery(query, Thread.currentThread().getStackTrace()[1].getMethodName());
		
		while(crs.next()) {
			indiceTO = new IndiceTO();
			indiceTO.setClave(crs.getString("clave"));
			indiceTO.setValor(crs.getString("valor"));
			indiceTO.setIndice(crs.getString("indice"));
			lista.add(indiceTO);
		}
		return lista;
	}

	/**
	 * 
	 * @return
	 * @throws Exception
	 */
	public HashMap listarMapa() throws Exception  {
		HashMap mapa = new HashMap();

		query.setLength(0);
		query.append("SELECT * FROM indice ORDER BY clave");
		
		CachedRowSet crs = JDBCUtil.executeQuery(query, Thread.currentThread().getStackTrace()[1].getMethodName());
		
		while(crs.next()) {
			query.setLength(0);
			query.append(crs.getString("indice")).append("-").append(crs.getString("clave"));
			mapa.put(query.toString(), crs.getString("valor"));
		}
		return mapa;
	}
	

	/**
	 * 
	 * @param numero
	 * @return
	 * @throws Exception
	 */
	public ArrayList listar(String numero) throws Exception  {
		ArrayList lista = new ArrayList();

		query.setLength(0);
		query.append("SELECT * FROM indice WHERE indice=? ORDER BY clave ");
		
		parametros = new ArrayList();
		parametros.add(new Integer(numero));
		
		CachedRowSet crs = JDBCUtil.executeQuery(query,parametros, Thread.currentThread().getStackTrace()[1].getMethodName());
		
		while(crs.next()) {
			indiceTO = new IndiceTO();
			indiceTO.setClave(crs.getString("clave"));
			indiceTO.setValor(crs.getString("valor"));
			lista.add(indiceTO);
		}
		return lista;
	}
	

}
