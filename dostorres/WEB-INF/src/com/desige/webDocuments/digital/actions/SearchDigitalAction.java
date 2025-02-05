package com.desige.webDocuments.digital.actions;

import java.util.Collection;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.TreeMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.desige.webDocuments.files.facade.DigitalFacade;
import com.desige.webDocuments.persistent.managers.HandlerDBUser;
import com.desige.webDocuments.persistent.managers.HandlerGrupo;
import com.desige.webDocuments.persistent.managers.HandlerStruct;
import com.desige.webDocuments.persistent.managers.HandlerTypeDoc;
import com.desige.webDocuments.registers.forms.Register;
import com.desige.webDocuments.to.DigitalTO;
import com.desige.webDocuments.utils.ApplicationExceptionChecked;
import com.desige.webDocuments.utils.Constants;
import com.desige.webDocuments.utils.DesigeConf;
import com.desige.webDocuments.utils.beans.SuperAction;
import com.desige.webDocuments.utils.beans.Users;

/**
 * Title: SearchDocAction.java <br/> Copyright: (c) 2004 Focus Consulting C.A.
 * <br/>
 * 
 * @author Ing. Nelson Crespo
 * @author Ing. Simón Rodríguez
 * @version WebDocuments v3.0 <br/> Changes:<br/>
 *          <ul>
 *          <li> 28/10/2004 (NC) Creation </li>
 *          <li> 27/05/2004 (SR) Variables y crear la session para la busqueda
 *          status </li>
 *          <li> 21/04/2006 (NC) Add field size in session </li>
 *          <li> 13/07/2006 (NC) Cambios para heredar o no los Prefijos de las
 *          Carpetas </li>
 *          </ul>
 */
public class SearchDigitalAction extends SuperAction {
	private static Logger log = LoggerFactory.getLogger(SearchDigitalAction.class.getName());

	public synchronized ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
		super.init(mapping, form, request, response);

		// seteamos el modulo activo
	//	request.getSession().setAttribute(Constants.MODULO_ACTIVO, Constants.MODULO_DIGITALIZAR);

		try {

			Users usuario = getUserSession();
			DigitalFacade digitalFacade = new DigitalFacade(request);
			String accion = request.getParameter("accion") != null ? request.getParameter("accion") : "";
			Collection documents = null;

			removeAttribute("size");

			if (request.getParameter("eliminar") != null && request.getParameter("eliminar").equals("true")) {
				DigitalTO digitalTO = new DigitalTO();
				StringBuffer ids = new StringBuffer();
				String sep = "";
				String coma = ",";

				for (int i = 0; i < request.getParameterValues("seleccionados").length; i++) {
					ids.append(sep).append(request.getParameterValues("seleccionados")[i]);
					sep = coma;
				}
				if (ids.length() > 0) {
					digitalTO.setIdDigital(ids.toString());
					digitalTO.setIdPersonDelete(String.valueOf(usuario.getIdPerson()));
					digitalFacade.delete(digitalTO);
				}
				accion = "1";
			}

			
			request.getSession().removeAttribute("isAutoSave"); // variable de autosalvado de documentos
			if (true || "1".equalsIgnoreCase(accion)) {  // habilitar si se quiere abrir en blanco el modulo
				if(request.getParameter("all")!=null && request.getParameter("all").equals("true")) {
					documents = digitalFacade.findAllDocumentDigital((int)usuario.getIdPerson());
					request.setAttribute("all", "true");
				} else {
					documents = digitalFacade.findAllDigital((int)usuario.getIdPerson());
				}
			} else {
				/* query con todos los documentos */
				StringBuffer query = new StringBuffer();
				query.append("SELECT * FROM digital ORDER BY idDigital");

				request.getSession().setAttribute("queryDigitalReport", query.toString());
			}

			// cargamos la estructura si no existe
			if (request.getSession().getAttribute("tree") == null) {
				boolean isAdmon = usuario.getIdGroup().equalsIgnoreCase(DesigeConf.getProperty("application.admon"));
				Hashtable subNodos = new Hashtable();
				Hashtable tree = null;// new Hashtable();
				Hashtable security = null;
				if (!isAdmon) {
					StringBuffer idStructs = new StringBuffer(50);
					idStructs.append("1");
					security = HandlerGrupo.getAllSecurityForGroup(usuario.getIdGroup(), idStructs);
					// Se Carga la Seguridad por Usuario Filtrando aquellos
					// Nodos en Donde el Usuario no
					// puede ver Carpetas
					HandlerDBUser.getAllSecurityForUser(usuario.getIdPerson(), security, idStructs);
					tree = HandlerStruct.loadAllNodes(security, usuario.getUser(), usuario.getIdGroup(), subNodos, idStructs.toString());
					putObjectSession("security", security);
				} else {
					tree = HandlerStruct.loadAllNodes(security, usuario.getUser(), usuario.getIdGroup(), subNodos, null);
				}
				request.getSession().setAttribute("tree", tree);
			}

			Register reg = new Register();

			if (documents != null && documents.size() > 0) {
				putObjectSession("size", new Integer(documents.size()));
			}

			putObjectSession("register", reg);

			HashMap typesDocuments = HandlerTypeDoc.getAllTypeDocs();// HandlerWorkFlows.getAllTypesDocuments();
			putObjectSession("typesDocuments", typesDocuments);
			
            Collection tiposDoc = HandlerTypeDoc.getAllTypeDocs(null,true);//HandlerWorkFlows.getAllTypesDocuments();
            putObjectSession("tiposDoc",tiposDoc);
			
        	// los usuarios
        	Collection users = HandlerDBUser.getAllUsersWithIdPerson();
        	putObjectSession("users",users);
        	
			TreeMap listUsers = HandlerDBUser.getAllUsersNameMapById();// HandlerWorkFlows.getAllTypesDocuments();
			putObjectSession("listUsers", listUsers);

			
			putObjectSession("searchDigital", documents);

			return goSucces();
		} catch (ApplicationExceptionChecked ae) {
			return goError(ae.getKeyError());
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return goError();
	}
}
