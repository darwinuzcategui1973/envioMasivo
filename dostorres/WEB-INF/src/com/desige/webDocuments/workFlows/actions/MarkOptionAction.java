package com.desige.webDocuments.workFlows.actions;

import java.util.ArrayList;
import java.util.Hashtable;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import sun.jdbc.rowset.CachedRowSet;

import com.desige.webDocuments.persistent.utils.JDBCUtil;
import com.desige.webDocuments.utils.Constants;
import com.desige.webDocuments.utils.ToolsHTML;
import com.desige.webDocuments.utils.beans.Users;

public class MarkOptionAction extends Action {

	public synchronized ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {

		////System.out.println("Ejecutando codigo ajax ini");
		
		StringBuffer query = new StringBuffer();
		ArrayList<Object> parametros = new ArrayList<Object>();
		String campo = "";
		String columna = "";
        Hashtable nodos = new Hashtable();

		try {
			Users usuario = (Users) request.getSession().getAttribute("user");
			if(usuario==null)
				return null;

			campo = String.valueOf(request.getParameter("campo"));
			columna = String.valueOf(request.getParameter("columna"));
			
			if(campo==null || columna==null) {
				return null;
			}
			/*
			if(!campo.equals(Constants.COLUMN_SEARCH_NAME) && !campo.equals(Constants.COLUMN_PUBLISHED_NAME) && !campo.equals(Constants.COLUMN_FILES_NAME) && !campo.equals(Constants.COLUMN_MENUHEAD_NAME)) {
				return null;
			}
			*/
			if(!campo.equals(Constants.COLUMN_SEARCH_NAME) && !campo.equals(Constants.COLUMN_PUBLISHED_NAME) && !campo.equals(Constants.COLUMN_FILES_NAME) && !campo.equals(Constants.COLUMN_MENUHEAD_NAME) && !campo.equals(Constants.COLUMN_SACOP_NAME) ) {
				return null;
			}

			StringBuffer queryEditar = new StringBuffer();
			ArrayList<Object> parametrosEditar = new ArrayList<Object>();
			
			if(campo.equals(Constants.COLUMN_MENUHEAD_NAME)) {
				parametrosEditar.add(usuario.getIdPerson());
				queryEditar.setLength(0);
				queryEditar.append("update person set ");
				queryEditar.append(campo);
				queryEditar.append("='");
				queryEditar.append(columna);
				queryEditar.append("' WHERE idPerson=? and accountActive='1'");
				JDBCUtil.executeUpdate(queryEditar, parametrosEditar);
				
				return null;
			}

			
			// primero consultamos las opciones que tiene
			parametrosEditar.add(usuario.getIdPerson());
			queryEditar.append("SELECT ");
			queryEditar.append(campo);
			queryEditar.append(" FROM person WHERE idPerson=? and accountActive='1'");
			
			CachedRowSet crs = JDBCUtil.executeQuery(queryEditar, parametrosEditar, Thread.currentThread().getStackTrace()[1].getMethodName());
			
			String opciones = "";
			if(crs.next()) {
				opciones = crs.getString(1);
				String separador="";
				/*
				if(ToolsHTML.isEmptyOrNull(opciones)) {
					if(campo.equals(Constants.COLUMN_SEARCH_NAME)) {
						opciones=Constants.COLUMN_SEARCH_DEFAULT;
					} else if(campo.equals(Constants.COLUMN_PUBLISHED_NAME)) {
						opciones=Constants.COLUMN_PUBLISHED_DEFAULT;
					} else if(campo.equals(Constants.COLUMN_FILES_NAME)) {
						opciones=Constants.COLUMN_FILES_DEFAULT;
					} else if(campo.equals(Constants.COLUMN_MENUHEAD_NAME)) {
						opciones=Constants.COLUMN_MENUHEAD_DEFAULT;
					} 
				}
				*/
				if(ToolsHTML.isEmptyOrNull(opciones)) {
					if(campo.equals(Constants.COLUMN_SEARCH_NAME)) {
						opciones=Constants.COLUMN_SEARCH_DEFAULT;
					} else if(campo.equals(Constants.COLUMN_PUBLISHED_NAME)) {
						opciones=Constants.COLUMN_PUBLISHED_DEFAULT;
					} else if(campo.equals(Constants.COLUMN_FILES_NAME)) {
						opciones=Constants.COLUMN_FILES_DEFAULT;
					} else if(campo.equals(Constants.COLUMN_MENUHEAD_NAME)) {
						opciones=Constants.COLUMN_MENUHEAD_DEFAULT;
					} else if(campo.equals(Constants.COLUMN_SACOP_NAME)) {
						opciones=Constants.COLUMN_SACOP_DEFAULT;
					} 
				}
				
				if(campo.equals(Constants.COLUMN_FILES_NAME)) {
					separador=",";
				}

				if(opciones.indexOf(columna)!=-1) {
					opciones=opciones.replaceAll(columna, separador);
				} else {
					opciones=opciones.concat(columna);
				}
				
				if(campo.equals(Constants.COLUMN_FILES_NAME)) {
					opciones=opciones.replaceAll(",,", ",");
					opciones=(opciones.startsWith(",")?opciones:",".concat(opciones));
					opciones=(opciones.endsWith(",")?opciones:opciones.concat(","));
				}

				queryEditar.setLength(0);
				queryEditar.append("update person set ");
				queryEditar.append(campo);
				queryEditar.append("='");
				queryEditar.append(opciones);
				queryEditar.append("' WHERE idPerson=? and accountActive='1'");
				JDBCUtil.executeUpdate(queryEditar, parametrosEditar);

			}

        	response.getWriter().print(opciones);
		} catch (Exception e) {
			////System.out.println(e.getMessage());
			e.printStackTrace();
		} finally {
		}

		////System.out.println("Ejecutando codigo ajax fin");
		return null;
	}
}

