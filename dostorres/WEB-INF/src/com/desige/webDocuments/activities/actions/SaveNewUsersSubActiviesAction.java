package com.desige.webDocuments.activities.actions;

import java.util.ArrayList;
import java.util.Date;
import java.util.ResourceBundle;
import java.util.TreeMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import sun.jdbc.rowset.CachedRowSet;

import com.desige.webDocuments.document.forms.BaseDocumentForm;
import com.desige.webDocuments.persistent.managers.HandlerStruct;
import com.desige.webDocuments.persistent.managers.HandlerWorkFlows;
import com.desige.webDocuments.persistent.utils.HibernateUtil;
import com.desige.webDocuments.persistent.utils.JDBCUtil;
import com.desige.webDocuments.to.UserFlexWorkFlowsTO;
import com.desige.webDocuments.utils.Constants;
import com.desige.webDocuments.utils.IDDBFactorySql;
import com.desige.webDocuments.utils.ToolsHTML;
import com.desige.webDocuments.utils.beans.SuperAction;
import com.desige.webDocuments.utils.beans.Users;
import com.desige.webDocuments.workFlows.actions.EditWorkFlowAction;

/**
 * Title: SaveNewUsersSubActiviesAction.java <br/> Copyright: (c) 2004 Focus Consulting<br/>
 * 
 * @author Ing. Nelson Crespo
 * @version WebDocuments v1.0 <br/> Changes:<br/>
 *          <ul>
 *          <li> 21/11/2005 (NC) Creation </li>
 *          </ul>
 */
public class SaveNewUsersSubActiviesAction extends SuperAction {
	static Logger log = LoggerFactory.getLogger(SaveNewUsersSubActiviesAction.class.getName());

	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
		super.init(mapping, form, request, response);
		StringBuffer query = new StringBuffer();
		ArrayList parametros = new ArrayList();
		CachedRowSet cache = null;
		CachedRowSet cacheWF = null;
		CachedRowSet cacheSacadosDelFlujo = null;
		@SuppressWarnings("unused")
		int actualizados = 0;
		ArrayList listaUsuarios = new ArrayList();
		TreeMap listaOrdenada = new TreeMap();
		TreeMap personas = new TreeMap();
		TreeMap<String, String> listaRepondieron = new TreeMap<String, String>();

		String coma = ",";
		String sep = "";

		boolean isSecuencial = false;
		boolean isPendiente = false;
		boolean isPrimero = true;

		int ultimo = 0;

		try {
            Users userInSession = getUserSession();

			// Consultamos la subactividad que se va a reemplazar con nuevos usuarios
			UserFlexWorkFlowsTO userFlexWorkFlowsTO = new UserFlexWorkFlowsTO();
			UserFlexWorkFlowsTO user;

			userFlexWorkFlowsTO.setIdWorkFlow(Integer.parseInt(request.getParameter("idWorkFlow")));

			// iniciamos los objetos para el query
			query.setLength(0);
			parametros.removeAll(parametros);

			// preparamos la consulta del ftp
			parametros.add(new Integer(userFlexWorkFlowsTO.getIdWorkFlow()));
			query.append("SELECT * FROM flexworkflow WHERE idWorkFlow=? ");

			cacheWF = JDBCUtil.executeQuery(query, parametros, Thread.currentThread().getStackTrace()[1].getMethodName());
			cacheWF.next();
			isSecuencial = !cacheWF.getBoolean("secuential");
			log.debug(cacheWF.getString("statu"));
			log.debug(String.valueOf(JDBCUtil.getByte(cacheWF.getString("statu"))));
			log.debug(String.valueOf(JDBCUtil.getByte(cacheWF.getString("statu"))));
			isPendiente = String.valueOf(JDBCUtil.getByte(cacheWF.getString("statu"))).equals(HandlerWorkFlows.wfuPending);

			// iniciamos los objetos para el query
			query.setLength(0);
			parametros.removeAll(parametros);

			// preparamos la consulta del los usuarios del ftp
			parametros.add(new Integer(userFlexWorkFlowsTO.getIdWorkFlow()));
			query.append("SELECT * FROM user_flexworkflows WHERE idWorkFlow=? ");

			cache = JDBCUtil.executeQuery(query, parametros, Thread.currentThread().getStackTrace()[1].getMethodName());

			// buscamos el orden mayor en los participantes
			ultimo = cache.size();
			// tomamos una muestra de la configuracion actual de un participante
			if (cache.next()) {
				userFlexWorkFlowsTO.setType(JDBCUtil.getByte(cache.getString("type")));
				userFlexWorkFlowsTO.setResult(HandlerWorkFlows.wfuPending);
				userFlexWorkFlowsTO.setStatu(HandlerWorkFlows.wfuPending);
				userFlexWorkFlowsTO.setIsOwner(JDBCUtil.getByte(cache.getString("isOwner")));
				userFlexWorkFlowsTO.setReading(JDBCUtil.getByte(cache.getString("reading")));
				userFlexWorkFlowsTO.setActive(JDBCUtil.getByte(cache.getString("active")));
				userFlexWorkFlowsTO.setPending(JDBCUtil.getByte(cache.getString("pending")));
				userFlexWorkFlowsTO.setWfActive(JDBCUtil.getByte(cache.getString("wfActive")));
				userFlexWorkFlowsTO.setIdFather(cache.getLong("IdFather"));
				userFlexWorkFlowsTO.setUw_Circle(JDBCUtil.getByte(cache.getString("uw_Circle")));
			}
			cache.beforeFirst();
			while (cache.next()) {
				if (cache.getString("statu").trim().equals(HandlerWorkFlows.wfuAcepted)) {
					listaRepondieron.put(cache.getString("idUser"), cache.getString("idUser"));
				}
			}

			// localizamos los usuarios nuevos
			// iniciamos los objetos para el query
			query.setLength(0);
			parametros.removeAll(parametros);
			query.append("SELECT * FROM person");

			cache = JDBCUtil.executeQuery(query, parametros, Thread.currentThread().getStackTrace()[1].getMethodName());

			while (cache.next()) {
				personas.put(cache.getString("nameUser"), cache.getString("email"));
			}
			
			
			// localizamos los usuarios nuevos
			// iniciamos los objetos para el query
			query.setLength(0);
			parametros.removeAll(parametros);

			String cadena = String.valueOf(request.getParameter("userAssociate"));
			cadena = cadena.replaceAll("'", "");
			String[] usuariosNuevos = cadena.split(",");
			for (int i = 0; i < usuariosNuevos.length; i++) {
				parametros.add(new Integer(usuariosNuevos[i]));
			}

			query.append("SELECT * FROM person WHERE idPerson IN (");
			query.append(JDBCUtil.replicate("?", usuariosNuevos.length, ","));
			query.append(")");

			cache = JDBCUtil.executeQuery(query, parametros, Thread.currentThread().getStackTrace()[1].getMethodName());

			while (cache.next()) {
				user = (UserFlexWorkFlowsTO) userFlexWorkFlowsTO.clone();
				user.setIdUser(cache.getString("nameUser"));
				listaOrdenada.put(new Integer(cache.getString("idPerson")), user);
			}

			int posicion = ultimo;
			for (int i = 0; i < usuariosNuevos.length; i++) {
				user = (UserFlexWorkFlowsTO) listaOrdenada.get(new Integer(usuariosNuevos[i]));
				if (!listaRepondieron.containsKey(user.getIdUser())) {
					user.setOrden(posicion);
					if (isSecuencial) {
						if (isPendiente && posicion == ultimo) {
							user.setStatu(HandlerWorkFlows.wfuPending);
						} else {
							user.setStatu(HandlerWorkFlows.wfuQueued);
						}
					}
					posicion++;
					// userFlexWorkFlowsTO.setStatu(HandlerWorkFlows.wfuPending);
					listaUsuarios.add(user);
				}
			}

			for (int i = 0; i < listaUsuarios.size(); i++) {
				UserFlexWorkFlowsTO a = (UserFlexWorkFlowsTO) listaUsuarios.get(i);
			}
			
			// iniciamos los objetos para el query 
			query.setLength(0);
			parametros.removeAll(parametros);
			
			// consultamos los usuarios que seran sacados del flujo que estan pendientes
			// por responder
			parametros.add(new Integer(userFlexWorkFlowsTO.getIdWorkFlow()));
			parametros.add(HandlerWorkFlows.wfuPending);
			
			query.append("SELECT * FROM user_flexworkflows WHERE idWorkFlow=? AND statu=?");
			
			cacheSacadosDelFlujo = JDBCUtil.executeQuery(query, parametros, Thread.currentThread().getStackTrace()[1].getMethodName());


			// iniciamos los objetos para el query
			query.setLength(0);
			parametros.removeAll(parametros);

			if (listaUsuarios.size() > 0) {
				ResourceBundle rb = ToolsHTML.getBundle(request);

				// borramos el usuario temporal
				ArrayList para = new ArrayList();
				para.add(new Integer(userFlexWorkFlowsTO.getIdWorkFlow()));
				para.add(HandlerWorkFlows.wfuPending);
				para.add(HandlerWorkFlows.wfuQueued);
				para.add(Constants.ID_USER_TEMPORAL);
				query.setLength(0);
				query.append("DELETE FROM user_flexworkflows WHERE idWorkFlow=? AND (statu=? OR statu=?) AND iduser=?");
				JDBCUtil.executeUpdate(query, para);

				// anulamos los usuarios del ftp que esten pendientes.
				parametros.add(HandlerWorkFlows.replaced);
				parametros.add(HandlerWorkFlows.wfuAnnulled);
				parametros.add(new Integer(0));
				parametros.add(rb.getString("wf.newWFMessageReplacedWho").concat(userInSession.getNamePerson()).concat(" ").concat(rb.getString("wf.newWFMessageReplacedDate")).concat(ToolsHTML.sdfShow.format(new Date())));
				parametros.add(new Integer(userFlexWorkFlowsTO.getIdWorkFlow()));
				parametros.add(HandlerWorkFlows.wfuPending);
				parametros.add(HandlerWorkFlows.wfuQueued);

				
				query.setLength(0);
				query.append("UPDATE user_flexworkflows SET statu=?, result=?, wfactive=").append(JDBCUtil.getCastAsBitString("?")).append(", comments=?  WHERE idWorkFlow=? AND (statu=? OR statu=?)");

				actualizados = JDBCUtil.executeUpdate(query, parametros);

				// insertamos los usuarios para el ftp
				HandlerWorkFlows.addUsersFlexWorkFlows(listaUsuarios);
				
				// a los usuarios restantes del las siguientes actividades les actualizamos
				// el id para que sean mayores al recien insertado
				// primero los requperamos
				ArrayList userNext = new ArrayList();
				para = new ArrayList();
				StringBuffer update = new StringBuffer("UPDATE user_flexworkflows SET idflexwf=? WHERE idflexwf=?");
				query.setLength(0);
				
				para.add(new Integer(userFlexWorkFlowsTO.getIdWorkFlow()));
				para.add(new Integer(userFlexWorkFlowsTO.getIdWorkFlow()));
				query.append("select idflexwf from user_flexworkflows where idworkflow in ( ");
				query.append("select idworkflow from flexworkflow where idflexflow in ( ");
				query.append("select idflexflow from flexworkflow x where x.idworkflow = ?) and idworkflow > ? ");
				query.append(") order by idworkflow,orden ");
				
				CachedRowSet crs = JDBCUtil.executeQuery(query,para, Thread.currentThread().getStackTrace()[1].getMethodName());
				long numRecord = 0;
				while(crs.next()) {
					para = new ArrayList();
					numRecord = IDDBFactorySql.getNextIDLong("user_flexworkflows");
					para.add(numRecord);
					para.add(crs.getInt(1));
					JDBCUtil.executeUpdate(update, para);
				}

				// enviamos el correo a los nuevos usuarios para notificarlos del flujo
				// // cargamos el documento
				BaseDocumentForm docForm = new BaseDocumentForm();
				String idDoc = cacheWF.getString("idDocument");
				docForm.setIdDocument(idDoc);
				docForm.setNumberGen(idDoc);
				HandlerStruct.loadDocument(docForm, true, false, null, request);// Cargamos La mayor Versi�n ya que es sobre la cual se hace el cambio
				Users usuario = getUserSession();

				// enviamos mensajes a los que fueron sacados del flujo
				// pendientes por responder
				while(cacheSacadosDelFlujo.next()) {
					String mensaje = EditWorkFlowAction.getRestDataNotified(request, docForm, rb, cacheSacadosDelFlujo.getString("idUser"));
					
					HandlerWorkFlows.notifiedUsers(rb.getString("wf.newWFMessageReplaced").concat(docForm.getPrefix()).concat(docForm.getNumber()), rb.getString("mail.nameUser"), personas.get(cacheSacadosDelFlujo.getString("idUser")).toString(), personas.get(cacheSacadosDelFlujo.getString("idUser")).toString(), mensaje);
				}

				for (int i = 0; i < listaUsuarios.size(); i++) {
					UserFlexWorkFlowsTO actual = (UserFlexWorkFlowsTO) listaUsuarios.get(i);
					String mensaje = EditWorkFlowAction.getRestDataNotified(request, docForm, rb, actual.getIdUser());

					HandlerWorkFlows.notifiedUsers(rb.getString("wf.newWFTitle").concat(docForm.getPrefix()).concat(docForm.getNumber()), rb.getString("mail.nameUser"), personas.get(actual.getIdUser()).toString(), personas.get(actual.getIdUser()).toString(), mensaje);
				}

				putAtributte("main", "alert('Los firmantes han sido agregados al flujo');\nwindow.opener.document.location.reload();\nwindow.close();");
			} else {
				// no hay usuarios por actualizar, no hacemos nada
				putAtributte("main", "alert('No se han podido cambiar los firmantes del flujo, ya que \\nlos usuarios seleccionados ya respondieron la actividad');\nwindow.close();");
			}

			return goSucces();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return goError();
	}
}
