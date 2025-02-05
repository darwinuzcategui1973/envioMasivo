package com.desige.webDocuments.parameters.actions;

import java.util.Collection;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.desige.webDocuments.persistent.managers.HandlerDBUser;
import com.desige.webDocuments.utils.ApplicationExceptionChecked;
import com.desige.webDocuments.utils.Constants;
import com.desige.webDocuments.utils.ToolsHTML;
import com.desige.webDocuments.utils.beans.SuperAction;
import com.desige.webDocuments.utils.beans.Users;
import com.focus.qweb.dao.TransactionDAO;

import sun.jdbc.rowset.CachedRowSet;

/**
 * Title: LoadParametersAction.java <br/> Copyright: (c) 2004 Focus Consulting
 * C.A.<br/>
 * 
 * @author Ing. Nelson Crespo (NC)
 * @version WebDocuments v3.0 <br/> Changes:<br/>
 *          <ul>
 *          <li>20/05/2004 (NC) Creation </li>
 *          </ul>
 */
public class ListTransactionConsulta extends SuperAction {
	private static Logger log = LoggerFactory.getLogger(ListTransactionConsulta.class.getName());

	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {

		super.init(mapping, form, request, response);

        // seteamos el modulo activo
		request.getSession().setAttribute(Constants.MODULO_ACTIVO, Constants.MODULO_ADMINISTRACION);
		
		try {
			
			Users usuario = getUserSession();

            String propietario = request.getParameter("propietario");
            String expiredTo= request.getParameter("expiredToHIDDEN");
            String expiredFrom= request.getParameter("expiredFromHIDDEN");
            String orderBy= request.getParameter("orderBy");
            
        	expiredFrom = (expiredFrom==null || expiredFrom.trim().equals("")?ToolsHTML.getDateShortSql():expiredFrom);
        	expiredTo = (expiredTo==null || expiredTo.trim().equals("")?ToolsHTML.getDateShortSql():expiredTo);

        	// hacemos la consulta
        	TransactionDAO oTransactionDAO = new TransactionDAO();
        	CachedRowSet listTransaction = oTransactionDAO.listarByDateAndUser(expiredFrom, expiredTo, propietario, orderBy);
        	
            //
            Collection allUsers = HandlerDBUser.getAllUsers();

            putObjectSession("allUsers",allUsers);
            putAtributte("propietario",propietario);
            putAtributte("expiredFromHIDDEN",expiredFrom);
            putAtributte("expiredToHIDDEN",expiredTo);
            putAtributte("listTransaction",listTransaction);

			
			return goSucces();
		} catch (ApplicationExceptionChecked ae) {
			ae.printStackTrace();
			return goError(ae.getKeyError());
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return goError();
	}
}
