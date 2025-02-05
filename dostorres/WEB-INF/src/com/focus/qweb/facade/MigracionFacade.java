package com.focus.qweb.facade;

import java.util.ArrayList;

import com.desige.webDocuments.utils.Constants;
import com.focus.qweb.dao.EquivalenciasDAO;
import com.focus.qweb.to.EquivalenciasTO;

public class MigracionFacade {

	public ArrayList listarEquivalencias() throws Exception {
		ArrayList lista = null;
		EquivalenciasDAO oEquivalenciasDAO = new EquivalenciasDAO();
		
		lista = oEquivalenciasDAO.listar();
		
		return lista;
	}
	
	public ArrayList listarEquivalencias(String nombre) throws Exception {
		ArrayList lista = null;
		EquivalenciasDAO oEquivalenciasDAO = new EquivalenciasDAO();
		
		lista = oEquivalenciasDAO.listar(nombre);
		
		return lista;
	}
	
	public ArrayList eliminarEquivalencias(String nombre) throws Exception {
		ArrayList lista = null;
		EquivalenciasDAO oEquivalenciasDAO = new EquivalenciasDAO();
		EquivalenciasTO equivalenciasTO = new EquivalenciasTO();
		
		equivalenciasTO.setNombre(nombre);
		oEquivalenciasDAO.eliminar(equivalenciasTO);
		
		oEquivalenciasDAO.desactivar(equivalenciasTO);
		equivalenciasTO.setNombre(EquivalenciasTO.EQUIVALENCIA_QWEB);
		oEquivalenciasDAO.activar(equivalenciasTO);
		
		
		return lista;
	}
	
	
	
	public ArrayList listarEquivalenciasNombres() throws Exception {
		ArrayList lista = null;
		EquivalenciasDAO oEquivalenciasDAO = new EquivalenciasDAO();
		
		lista = oEquivalenciasDAO.listarNombre();
		
		return lista;
	}

	public void insertarListaEquivalencias(EquivalenciasTO equivalenciasTO) throws Exception {
		EquivalenciasDAO equivalenciasDAO = new EquivalenciasDAO();
		EquivalenciasTO equiTO;
		if(equivalenciasTO!=null) {
			equivalenciasDAO.desactivar(equivalenciasTO);
			for(int i=0; i<equivalenciasTO.getIndiceArray().length; i++) {
				if(equivalenciasDAO.actualizar(equivalenciasTO.get(i))==0) {
					equivalenciasDAO.insertar(equivalenciasTO.get(i));
				}
			}
		}
	}
	
}
 