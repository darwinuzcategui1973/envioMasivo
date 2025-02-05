package com.focus.qweb.dao;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;

import com.desige.webDocuments.persistent.utils.JDBCUtil;
import com.focus.qweb.interfaces.ObjetoDAO;
import com.focus.qweb.interfaces.ObjetoTO;
import com.focus.qweb.to.TransactionTO;

import sun.jdbc.rowset.CachedRowSet;

/**
 * 
 * @author JRivero
 * 
 */
public class TransactionDAO implements ObjetoDAO {

	private StringBuffer query = new StringBuffer();
	private ArrayList<Object> parametros = null;
	TransactionTO transactionTO = null;

	/**
	 * 
	 */
	public int insertar(ObjetoTO oTransactionTO) throws Exception {
		return 0;
	}

	/**
	 * 
	 */
	public int actualizar(ObjetoTO oTransactionTO) throws Exception {
		transactionTO = (TransactionTO) oTransactionTO;

		query.setLength(0);
		query.append("UPDATE transaction_qweb SET tra_activate=? ");
		query.append("WHERE id_transaction=?");

		parametros = new ArrayList<Object>();
		parametros.add(transactionTO.getTraActivate());
		parametros.add(transactionTO.getIdTransaction());

		return JDBCUtil.executeUpdate(query, parametros);
	}

	public void eliminar(ObjetoTO oTransactionTO) throws Exception {
	}

	/**
	 * 
	 */
	@Override
	public boolean cargar(ObjetoTO oTransactionTO) throws Exception {
		try {
			transactionTO = (TransactionTO) oTransactionTO;

			parametros = new ArrayList<Object>();
			parametros.add(transactionTO.getIdTransaction());
			
			query.setLength(0);
			query.append("SELECT * FROM transaction_qweb WHERE id_transaction=? ");

			CachedRowSet crs = JDBCUtil.executeQuery(query, parametros, Thread.currentThread().getStackTrace()[1].getMethodName());

			if(crs.next()) {
				load(crs, transactionTO);
				return true;
			}
			
		} catch(Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * 
	 */
	public ArrayList<TransactionTO> listar() throws Exception {
		ArrayList<TransactionTO> lista = new ArrayList<TransactionTO>();

		query.setLength(0);
		query.append("SELECT * FROM transaction_qweb ORDER BY id_transaction");

		CachedRowSet crs = JDBCUtil.executeQuery(query, Thread.currentThread().getStackTrace()[1].getMethodName());

		while (crs.next()) {
			transactionTO = new TransactionTO();
			load(crs, transactionTO);
			lista.add(transactionTO);
		}
		return lista;
	}
	
	/**
	 * 
	 */
	public CachedRowSet listarByDateAndUser(String dateFrom, String dateUntil, String nameUser, String orderBy) throws Exception {

		query.setLength(0);
		query.append("SELECT a.fecha, c.nameUser, b.tra_descrip, a.valor_inicial, a.valor_final, a.terminal ");
		query.append("FROM audit a, transaction_qweb b, person c ");
		query.append("WHERE a.id_transaction=b.id_transaction ");
		query.append("AND a.id_usuario=c.idPerson ");
		
		if(dateFrom!=null && !dateFrom.trim().equals("")) {
			query.append("AND a.fecha >= '").append(dateFrom).append(" 00:00:00' ");
		}
		if(dateUntil!=null && !dateUntil.trim().equals("")) {
			query.append("AND a.fecha <= '").append(dateUntil).append(" 23:59:59' ");
		}
		if(nameUser!=null && !nameUser.trim().equals("")) {
			query.append("AND c.nameUser = '").append(nameUser).append("' ");
		}
		
		if(orderBy==null || orderBy.trim().equals("")) {
			query.append("ORDER BY a.fecha, c.nameUser ");
		} else {
			if(orderBy.equals("date")) {
				query.append("ORDER BY a.fecha, c.nameUser ");
			} else if(orderBy.equals("nameUser")) {
				query.append("ORDER BY c.nameUser ");
			} else if(orderBy.equals("descrip")) {
				query.append("ORDER BY b.tra_descrip ");
			} else if(orderBy.equals("valueStart")) {
				query.append("ORDER BY a.valor_inicial ");
			} else if(orderBy.equals("valueEnd")) {
				query.append("ORDER BY a.valor_final ");
			} else if(orderBy.equals("ip")) {
				query.append("ORDER BY a.terminal ");
			}
		}

		CachedRowSet crs = JDBCUtil.executeQuery(query, Thread.currentThread().getStackTrace()[1].getMethodName());

		return crs;
	}

	/**
	 * 
	 */
	public Map<Integer,TransactionTO> listarMap() throws Exception {
		Map<Integer,TransactionTO> lista = new TreeMap<Integer,TransactionTO>();

		query.setLength(0);
		query.append("SELECT * FROM transaction_qweb ORDER BY id_transaction");

		CachedRowSet crs = JDBCUtil.executeQuery(query, Thread.currentThread().getStackTrace()[1].getMethodName());

		while (crs.next()) {
			transactionTO = new TransactionTO();
			load(crs, transactionTO);
			lista.put(transactionTO.getIdTransaction(),transactionTO);
		}
		return lista;
	}

	private void load(CachedRowSet crs, TransactionTO transactionTO) throws SQLException {
		transactionTO.setIdTransaction(crs.getInt("id_transaction"));
		transactionTO.setTraDescrip(crs.getString("tra_descrip"));
		transactionTO.setTraGravity(crs.getInt("tra_gravity"));
		transactionTO.setTraSave(crs.getInt("tra_save"));
		transactionTO.setTraNotify(crs.getInt("tra_notify"));
		transactionTO.setTraTextHistory(crs.getString("tra_text_history"));
		transactionTO.setTraTextNotify(crs.getString("tra_text_notify"));
		transactionTO.setTraPerformer(crs.getString("tra_performer"));
		transactionTO.setTraCaseException(crs.getString("tra_case_exception"));
		transactionTO.setTraEmail(crs.getString("tra_email"));
		transactionTO.setTraJsp(crs.getString("tra_jsp"));
		transactionTO.setTraLocation(crs.getString("tra_location"));
		transactionTO.setTraClass(crs.getString("tra_class"));
		transactionTO.setTraClassLocation(crs.getString("tra_class_location"));
		transactionTO.setTraActivate(crs.getInt("tra_activate"));
		transactionTO.setTraLevel(crs.getInt("tra_level"));
	}


}
