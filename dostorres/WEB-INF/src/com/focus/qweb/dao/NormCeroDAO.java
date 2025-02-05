package com.focus.qweb.dao;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;
import java.util.Vector;

import com.desige.webDocuments.persistent.managers.HandlerNorms;
import com.desige.webDocuments.persistent.managers.HandlerStruct;
import com.desige.webDocuments.persistent.utils.JDBCUtil;
import com.desige.webDocuments.utils.ToolsHTML;
import com.focus.qweb.interfaces.ObjetoDAO;
import com.focus.qweb.interfaces.ObjetoTO;
import com.focus.qweb.to.NormCeroAuditTO;

import sun.jdbc.rowset.CachedRowSet;

/**
 * @author Darwin Uzcategui
 * 
 */
public class NormCeroDAO implements ObjetoDAO {

	private StringBuffer query = new StringBuffer();
	private ArrayList<Object> parametros = null;
	NormCeroAuditTO NormCeroAuditTO = null;
	
	@Override
	public int insertar(ObjetoTO objetoTO) throws Exception {
		// TODO Auto-generated method stub
		return 0;
	}


	@Override
	public int actualizar(ObjetoTO objetoTO) throws Exception {
		// TODO Auto-generated method stub
		return 0;
	}


	@Override
	public void eliminar(ObjetoTO objetoTO) throws Exception {
		// TODO Auto-generated method stub
		
	}


	@Override
	public ArrayList<NormCeroAuditTO> listar() throws Exception {
		// TODO Auto-generated method stub
		ArrayList<NormCeroAuditTO> lista = new ArrayList<NormCeroAuditTO>();
		System.out.println("****NormCeroAuditTO metodo listar");
		query.setLength(0);
		query.append("SELECT idNorm ,titleNorm ,(SELECT DISTINCT (idNorm) from norms where idNorm=A.idNorm and indice=0) as idNormcero FROM norms A ORDER BY idNorm");

		CachedRowSet crs = JDBCUtil.executeQuery(query, Thread.currentThread().getStackTrace()[1].getMethodName());

		while (crs.next()) {
			NormCeroAuditTO = new NormCeroAuditTO();
			load(crs, NormCeroAuditTO);
			lista.add(NormCeroAuditTO);
		}
		System.out.println(lista);
		return lista;
	}
	
	
	public boolean cargar(ObjetoTO oNormCeroAuditTO) throws Exception {
		NormCeroAuditTO = (NormCeroAuditTO) oNormCeroAuditTO;
		System.out.println("NormCeroAuditDAO metodo cargar");

		query.setLength(0);
		query.append("SELECT * FROM norms WHERE idNorm=").append(NormCeroAuditTO.getIdNormAudit());

		CachedRowSet crs = JDBCUtil.executeQuery(query, Thread.currentThread().getStackTrace()[1].getMethodName());

		if (crs.next()) {
			load(crs, NormCeroAuditTO);
			return true;
		}
		return false;
	}

	String normaIdcero="";
	private void load(CachedRowSet crs, NormCeroAuditTO NormCeroAuditTO) throws SQLException {
	
		if (crs.getString("idNormcero")!= null) {
			normaIdcero=crs.getString("idNormcero");
			
		}
		//System.out.println(normaIdcero);
		NormCeroAuditTO.setIdNormAudit(crs.getString("idNorm"));
		NormCeroAuditTO.setNameNorma(crs.getString("titleNorm"));
		NormCeroAuditTO.setIdNormIndiceCero(normaIdcero);
				
	}


	

}
