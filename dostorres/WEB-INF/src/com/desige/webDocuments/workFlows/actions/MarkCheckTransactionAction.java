package com.desige.webDocuments.workFlows.actions;

import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.desige.webDocuments.persistent.managers.HandlerDBUser;
import com.desige.webDocuments.persistent.utils.JDBCUtil;
import com.desige.webDocuments.utils.ApplicationExceptionChecked;
import com.desige.webDocuments.utils.ToolsHTML;
import com.desige.webDocuments.utils.beans.Users;

public class MarkCheckTransactionAction extends Action {
	private static final Logger log = LoggerFactory.getLogger(MarkCheckTransactionAction.class);
	
	public synchronized ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {

		try {
			Users usuario = (Users) request.getSession().getAttribute("user");

			if (usuario == null || !HandlerDBUser.isValidSessionUser(usuario.getUser(),request.getSession())) {
				throw new ApplicationExceptionChecked("E0035");
			}
			
			int id = Integer.parseInt(request.getParameter("id"));
			int value = Integer.valueOf(request.getParameter("value"));
			boolean save = Boolean.valueOf(request.getParameter("save"));
			boolean notify = Boolean.valueOf(request.getParameter("notify"));

			if (id>0) {
				StringBuffer queryEditar = new StringBuffer();
				ArrayList<Object> parametrosEditar = new ArrayList<Object>();
				java.util.Calendar calendario = new java.util.GregorianCalendar();
				String timeExecution = ToolsHTML.sdfShowConvert1.format(calendario.getTime());

				parametrosEditar.add(value);
				parametrosEditar.add(id);

				queryEditar.append("UPDATE transaction_qweb SET ");
				if(save) {
					queryEditar.append("tra_save=? ");
				} else if(notify) {
					queryEditar.append("tra_notify=? ");
				}
				queryEditar.append("WHERE id_transaction=? ");
				
				JDBCUtil.executeUpdate(queryEditar, parametrosEditar);
			}
			
		} catch (Exception e) {
			log.error("Error: " + e.getLocalizedMessage(), e);
		}
		
		return null;
	}
	
}
