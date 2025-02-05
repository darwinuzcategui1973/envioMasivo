package com.desige.webDocuments.files.actions;

import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.desige.webDocuments.bean.ExpedienteRequest;
import com.desige.webDocuments.dao.ExpedienteDAO;
import com.desige.webDocuments.dao.ExpedienteDetalleDAO;
import com.desige.webDocuments.files.forms.ExpedienteForm;
import com.desige.webDocuments.utils.ApplicationExceptionChecked;
import com.desige.webDocuments.utils.ToolsHTML;
import com.desige.webDocuments.utils.beans.SuperAction;
import com.desige.webDocuments.utils.beans.Users;

/**
 * Title: FilesNewAction.java <br/> Copyright: (c) 2008 Focus Consulting C.A.
 * <br/>
 * 
 * @author JRivero <br/> Changes:<br/>
 *         <ul>
 *         <li> 05/11/2008 (JR) Creation </li>
 *         </ul>
 */
public class FilesRelationAction extends SuperAction {
	private static Logger log = LoggerFactory.getLogger(FilesRelationAction.class.getName());

	public synchronized ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
		try {
			super.init(mapping, form, request, response);

			Users usuario = getUserSession();

			List<ExpedienteRequest> lista = null;
			ExpedienteForm files = new ExpedienteForm();
			files.setF2(new Date());
			if (!ToolsHTML.isEmptyOrNull(request.getParameter("f1")) || !ToolsHTML.isEmptyOrNull(String.valueOf(request.getAttribute("f1")))) {
				// obtenemos la lista de campos definidos
				ExpedienteDAO oExpedienteDAO = new ExpedienteDAO();
				if (!ToolsHTML.isEmptyOrNull(String.valueOf(request.getAttribute("f1")))) {
					files.setF1(Integer.parseInt(String.valueOf(request.getAttribute("f1"))));
				} else {
					files.setF1(Integer.parseInt(String.valueOf(request.getParameter("f1"))));
				}
				request.setAttribute("f1", files.getF1());
				
				oExpedienteDAO.findById(files);
				request.setAttribute("isOwnerFile", files.getF3().equals(usuario.getUser()));
				
				ExpedienteDetalleDAO oExpedienteDetalleDAO = new ExpedienteDetalleDAO();
				lista = oExpedienteDetalleDAO.findAllExpedientes(files);
			}
			StringBuffer sb = new StringBuffer();
			lista.forEach((item)->{ 
				sb.append(sb.length()==0?"":",").append(item.getNumgen());
			});
			request.setAttribute("listDocument", sb.toString());
			request.setAttribute("lista", lista);
			return goSucces();
		} catch (ApplicationExceptionChecked ae) {
			return goError(ae.getKeyError());
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return goError();
	}
}
