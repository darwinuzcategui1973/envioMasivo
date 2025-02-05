package com.focus.qweb.facade;

import java.util.ArrayList;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.focus.qweb.dao.TransactionDAO;
import com.focus.qweb.to.TransactionTO;

public class TransactionFacade {
	
	private HttpServletRequest request;
	
	public TransactionFacade(HttpServletRequest request) {
		this.request = request;
	}

	public ArrayList<TransactionTO> listarTransactionFacade() throws Exception {
		ArrayList<TransactionTO> lista = null;
		TransactionDAO oTransactionDAO = new TransactionDAO();
		
		lista = oTransactionDAO.listar();
		
		return lista;
	}

	public Map<Integer,TransactionTO> listarTransactionMapFacade() throws Exception {
		Map<Integer,TransactionTO> lista = null;
		TransactionDAO oTransactionDAO = new TransactionDAO();
		
		lista = oTransactionDAO.listarMap();
		
		return lista;
	}

}
