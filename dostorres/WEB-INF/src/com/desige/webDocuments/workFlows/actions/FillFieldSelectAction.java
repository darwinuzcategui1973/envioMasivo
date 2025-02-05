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

import com.desige.webDocuments.dao.ConfDocumentoDAO;
import com.desige.webDocuments.document.forms.DocumentsCheckOutsBean;
import com.desige.webDocuments.utils.beans.Users;

public class FillFieldSelectAction extends Action {

	public synchronized ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {

		StringBuffer query = new StringBuffer();
		ArrayList<Object> parametros = new ArrayList<Object>();
		String cuadrante = "";
		Hashtable nodos = new Hashtable();

		try {
			Users usuario = (Users) request.getSession().getAttribute("user");

			if (request.getParameter("save") != null && request.getParameter("save").equals("true")) {
				// salvamos los cambios
				String[] campos = request.getParameter("ids").split("-");
				String idTypeDoc = request.getParameter("idTypeDoc");
				
				ConfDocumentoDAO conf = new ConfDocumentoDAO();
				
				conf.updateFieldSelectByIndex(idTypeDoc);
				for(int i=0; i<campos.length;i++) {
					conf.updateFieldSelectByIndex(idTypeDoc,campos[i],i+1);
				}
				
				response.getWriter().print("Indices registrados satisfactoriamente.");
			} else {
				String idTypeDoc = request.getParameter("idTypeDoc");

				ConfDocumentoDAO conf = new ConfDocumentoDAO();

				CachedRowSet crs = conf.findSelectByIdTypeDoc(idTypeDoc);

				String sep = "";
				StringBuffer cad = new StringBuffer();
				cad.append("[");
				// var arr = [{id:100,nombre:'jairo'},{id:101,nombre:'rivero'}];
				if (crs != null) {
					while (crs.next()) {
						cad.append(sep);
						cad.append("{id:");
						cad.append("'").append(crs.getString("id")).append("'");
						cad.append(",etiqueta:");
						cad.append("'").append(crs.getString("etiqueta01")).append("-(").append(crs.getString("id")).append(")'");
						cad.append("}");
						sep = ",";
					}
				}
				cad.append("]");

				response.getWriter().print(cad);
			}
		} catch (Exception e) {
			// System.out.println(e.getMessage());
			e.printStackTrace();
		} finally {
			
		}

		// System.out.println("Ejecutando codigo ajax");
		return null;
	}
}
