package com.desige.webDocuments.files.facade;

import java.util.ArrayList;
import java.util.Collection;

import javax.servlet.http.HttpServletRequest;

import com.desige.webDocuments.dao.DigitalDAO;
import com.desige.webDocuments.to.DigitalTO;
import com.desige.webDocuments.utils.beans.Users;

public class DigitalFacade {

	private HttpServletRequest request;
	private Users usuario = null;

	public DigitalFacade() {
	}

	public DigitalFacade(HttpServletRequest request) {
		this.request = request;
		usuario= (Users)request.getSession().getAttribute("user");
	}


	/**
	 *
	 * @param form
	 * @throws Exception
	 * Almacena la ficha de expedientes en la tabla correspondiente
	 */
	public void save(DigitalTO digitalTO) throws Exception {
		DigitalDAO digitalDAO = new DigitalDAO();

		digitalDAO.save(digitalTO);
	}


	public void delete(DigitalTO digitalTO) throws Exception {
		DigitalDAO digitalDAO = new DigitalDAO();

		digitalDAO.delete(digitalTO);
	}

	public void reject(DigitalTO digitalTO) throws Exception {
		DigitalDAO digitalDAO = new DigitalDAO();

		digitalDAO.reject(digitalTO);
	}

	
	public Collection findAllDigital() throws Exception{
		DigitalDAO digitalDAO = new DigitalDAO();

		ArrayList lista = (ArrayList)digitalDAO.findAllByOrder();

		return lista;
	}
	
	public Collection findAllDigital(int idPerson) throws Exception{
		DigitalDAO digitalDAO = new DigitalDAO();

		ArrayList lista = (ArrayList)digitalDAO.findAllByOrder(idPerson);

		return lista;
	}
	
	public Collection findAllDocumentDigital(int idPerson) throws Exception{
		DigitalDAO digitalDAO = new DigitalDAO();

		ArrayList lista = (ArrayList)digitalDAO.findAllDocByOrder(idPerson);

		return lista;
	}
	
	public DigitalTO findById(DigitalTO digitalTO) throws Exception{
		DigitalDAO digitalDAO = new DigitalDAO();

		digitalTO.setUsuario(usuario);
		digitalTO = digitalDAO.findById(digitalTO);
		
		return digitalTO;
	}
	
	
	public String getPrevious(DigitalTO digitalTO) throws Exception{
		DigitalDAO digitalDAO = new DigitalDAO();
		
		digitalTO.setUsuario(usuario);
		return digitalDAO.findPreviousByOrder(digitalTO);
	}

	public String getNext(DigitalTO digitalTO) throws Exception{
		DigitalDAO digitalDAO = new DigitalDAO();

		digitalTO.setUsuario(usuario);
		return digitalDAO.findNextByOrder(digitalTO);
	}
}
