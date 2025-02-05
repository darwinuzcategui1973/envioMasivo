package com.focus.qweb.dao;

import java.math.BigInteger;
import java.sql.SQLException;
import java.util.ArrayList;

import sun.jdbc.rowset.CachedRowSet;

import com.desige.webDocuments.persistent.utils.JDBCUtil;
import com.desige.webDocuments.utils.IDDBFactorySql;
import com.focus.qweb.interfaces.ObjetoDAO;
import com.focus.qweb.interfaces.ObjetoTO;
import com.focus.qweb.to.AuditTO;

/**
 * 
 * @author JRivero
 * 
 */
public class AuditDAO implements ObjetoDAO {

	private StringBuffer query = new StringBuffer();
	private ArrayList<String> parametros = null;
	AuditTO auditTO = null;
	public static final String NOMBRE_TABLA = "audit";

	/**
	 * 
	 */
	public int insertar(ObjetoTO oAuditTO) throws Exception {
		auditTO = (AuditTO) oAuditTO;
		query.setLength(0);
		query.append("INSERT INTO audit (id_audit,id_transaction,fecha,detalle,id_usuario,valor_inicial,valor_final,terminal) ");
		query.append("VALUES(?,?,now(),?,?,?,?,?)");
		
		ArrayList<Object> parametros = new ArrayList<>();
		parametros.add(IDDBFactorySql.getNextID(NOMBRE_TABLA)); 
		parametros.add(Integer.parseInt(auditTO.getIdTransaction()));
		//parametros.add(auditTO.getFecha());
		parametros.add(auditTO.getDetalle());
		parametros.add(Integer.parseInt(auditTO.getIdUsuario()));
		parametros.add(auditTO.getValorInicial());
		parametros.add(auditTO.getValorFinal());
		parametros.add(auditTO.getTerminal());
		
		return JDBCUtil.executeUpdate(query, parametros);
	}

	/**
	 * 
	 */
	public int actualizar(ObjetoTO oAuditTO) throws Exception {
		auditTO = (AuditTO) oAuditTO;

		query.setLength(0);
		query.append("UPDATE audit SET ");
		query.append("id_transaction=?,fecha=?,detalle=?,id_usuario=?,valor_inicial=?,valor_final=?,terminal=? ");
		query.append("WHERE id_audit=?");

		parametros = new ArrayList<String>();
		parametros.add(auditTO.getIdTransaction());
		parametros.add(auditTO.getFecha());
		parametros.add(auditTO.getDetalle());
		parametros.add(auditTO.getIdUsuario());
		parametros.add(auditTO.getValorInicial());
		parametros.add(auditTO.getValorFinal());
		parametros.add(auditTO.getTerminal());
		parametros.add(auditTO.getIdAudit()); // primary


		return JDBCUtil.executeUpdate(query, parametros);
	}

	/**
	 * 
	 */
	public void eliminar(ObjetoTO oAuditTO) throws Exception {
		auditTO = (AuditTO) oAuditTO;

		query.setLength(0);
		query.append("DELETE FROM audit WHERE CodPais=?");

		parametros = new ArrayList<String>();
		parametros.add(auditTO.getIdAudit());

		JDBCUtil.executeUpdate(query, parametros);
	}

	/**
	 * 
	 */
	public boolean cargar(ObjetoTO oAuditTO) throws Exception {
		auditTO = (AuditTO) oAuditTO;

		query.setLength(0);
		query.append("SELECT * FROM audit WHERE CodPais=").append(auditTO.getIdAudit());

		CachedRowSet crs = JDBCUtil.executeQuery(query, Thread.currentThread().getStackTrace()[1].getMethodName());

		if (crs.next()) {
			load(crs, auditTO);
			return true;
		}
		return false;
	}

	/**
	 * 
	 */
	public ArrayList<AuditTO> listar() throws Exception {
		ArrayList<AuditTO> lista = new ArrayList<AuditTO>();

		query.setLength(0);
		query.append("SELECT * FROM audit ORDER BY CodPais");

		CachedRowSet crs = JDBCUtil.executeQuery(query, Thread.currentThread().getStackTrace()[1].getMethodName());

		while (crs.next()) {
			auditTO = new AuditTO();
			load(crs, auditTO);
			lista.add(auditTO);
		}
		return lista;
	}
	
	private void load(CachedRowSet crs, AuditTO auditTO) throws SQLException {
		auditTO.setIdAudit(crs.getString("id_audit"));
		auditTO.setIdTransaction(crs.getString("id_transaction"));
		auditTO.setFecha(crs.getString("fecha"));
		auditTO.setDetalle(crs.getString("detalle"));
		auditTO.setIdUsuario(crs.getString("id_usuario"));
		auditTO.setValorInicial(crs.getString("valor_inicial"));
		auditTO.setValorFinal(crs.getString("valor_final"));
		auditTO.setTerminal(crs.getString("terminal"));
	}

}
