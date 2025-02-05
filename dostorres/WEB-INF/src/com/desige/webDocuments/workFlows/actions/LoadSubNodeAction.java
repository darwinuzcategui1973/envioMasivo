package com.desige.webDocuments.workFlows.actions;

import java.io.IOException;
import java.sql.SQLException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.desige.webDocuments.persistent.managers.HandlerStruct;
import com.desige.webDocuments.utils.beans.Users;

import sun.jdbc.rowset.CachedRowSet;

public class LoadSubNodeAction extends Action {

	private HttpServletRequest request;
	private HttpServletResponse response;

	public synchronized ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
		this.response = response;
		this.request = request;
		
		String idNode = request.getParameter("idNode");
		String indice = request.getParameter("indice");
		StringBuffer data = new StringBuffer();
		StringBuffer item = new StringBuffer();
		StringBuffer itemParent = new StringBuffer();
		
		int subIndice = 0;

		try {
			Users user = (Users) request.getSession().getAttribute("user");
			
			String idNodeParent = HandlerStruct.getIdNodeParent(idNode);

			CachedRowSet nodes = HandlerStruct.getAllChildsNode(idNode, user);
			itemParent.append("item").append(idNodeParent);
			item.append("item").append(idNode);

			data.append(item).append(" = new MTMenu();");
			while(nodes.next()) {
				data.append(item).append(".MTMAddItem(new MTMenuItem('").append(nodes.getString("name")).append("', ");
				data.append("'").append(idNode).append("','', ");
				data.append("'").append(nodes.getString("nameIcon")).append("', ");
				data.append("'").append(nodes.getString("idNode")).append("', ");
				data.append("'").append(nodes.getString("majorId")==null?"0":nodes.getString("majorId")).append("', ");
				data.append("'").append(nodes.getString("minorId")==null?"0":nodes.getString("minorId")).append("', ");
				data.append("'").append(subIndice++).append("' ");
				data.append("));");
			}
			data.append(itemParent).append(".items[").append(indice).append("].MTMakeSubmenu(").append(item).append(");");

			//System.out.println(data.toString());
			response.getWriter().print(data.toString());
		} catch (IOException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return null;
	}

	
}
