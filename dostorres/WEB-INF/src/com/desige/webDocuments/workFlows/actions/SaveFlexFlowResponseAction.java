package com.desige.webDocuments.workFlows.actions;

import java.util.Hashtable;
import java.util.ResourceBundle;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.desige.webDocuments.document.forms.BaseDocumentForm;
import com.desige.webDocuments.persistent.managers.HandlerBD;
import com.desige.webDocuments.persistent.managers.HandlerDBUser;
import com.desige.webDocuments.persistent.managers.HandlerParameters;
import com.desige.webDocuments.persistent.managers.HandlerStruct;
import com.desige.webDocuments.persistent.managers.HandlerWorkFlows;
import com.desige.webDocuments.structured.forms.BaseStructForm;
import com.desige.webDocuments.users.forms.LoginUser;
import com.desige.webDocuments.utils.ApplicationExceptionChecked;
import com.desige.webDocuments.utils.DesigeConf;
import com.desige.webDocuments.utils.ToolsHTML;
import com.desige.webDocuments.utils.beans.BeanNotifiedWF;
import com.desige.webDocuments.utils.beans.SuperAction;
import com.desige.webDocuments.utils.beans.Users;
import com.desige.webDocuments.workFlows.forms.ParticipationForm;

/**
 * Title: SaveFlexFlowResponseAction.java <br/>
 * Copyright: (c) 2004 Desige Servicios de Computaci�n<br/>
 * 
 * @author Ing. Nelson Crespo
 * @version WebDocuments v1.0 <br/>
 *          Changes:<br/>
 *          <ul>
 *          <li>10/03/2006 (NC) Creation</li>
 *          </ul>
 */
public class SaveFlexFlowResponseAction extends SuperAction {
	static Logger log = LoggerFactory.getLogger(SaveFlexFlowResponseAction.class
			.getName());

	public synchronized ActionForward execute(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) {
		log.debug("[BEGIN]");
		super.init(mapping, form, request, response);
		try {
			ResourceBundle rb = ToolsHTML.getBundle(request);
			Users usuario = getUserSession();
			ParticipationForm forma = (ParticipationForm) form;
			BeanNotifiedWF beanNotified = new BeanNotifiedWF();
			BaseDocumentForm docForm = new BaseDocumentForm();
			String idDoc = String.valueOf(forma.getIdDocument());
			docForm.setIdDocument(idDoc);
			docForm.setNumberGen(idDoc);

			Hashtable tree = (Hashtable) getSessionObject("tree");
			if (tree == null) {
				// Hashtable security =
				// HandlerGrupo.getAllSecurityForGroupH(usuario.getIdGroup(),null);
				// HandlerDBUser.getAllSecurityForUserH(usuario.getIdPerson(),security);
				// tree =
				// HandlerStruct.loadAllNodesH(security,usuario.getIdGroup());
				Users usr = new Users();
				usr.setIdGroup(DesigeConf.getProperty("application.admon"));
				usr.setUser(usuario.getUser());
				tree = ToolsHTML.checkTree(null, usr);
			}
			HandlerStruct.loadDocument(docForm, true, false, tree, request);
			String idLocation = HandlerStruct.getIdLocationToNode(tree,
					docForm.getIdNode());
			int minorKeep = 0;
			int mayorKeep = 0;
			if (idLocation != null) {
				// BaseStructForm localidad = (BaseStructForm)tree.get(new
				// Long(idLocation));
				BaseStructForm localidad = (BaseStructForm) tree
						.get(idLocation);
				if (localidad != null) {
					if (ToolsHTML.isNumeric(localidad.getMinorKeep())) {
						minorKeep = Integer.parseInt(localidad.getMinorKeep()
								.trim());
					}
					if (ToolsHTML.isNumeric(localidad.getMajorKeep())) {
						mayorKeep = Integer.parseInt(localidad.getMajorKeep()
								.trim());
					}
				}
			}
			forma.setEliminar("0");
			forma.setEnd("0");
			forma.setIdLocation(idLocation);
			forma.setIdNode(docForm.getIdNode());
			// hacemos todo el proceso en la respuesta del flujo.
			// HandlerWorkFlows.setResponseFlexFlow(forma,beanNotified,minorKeep,mayorKeep,request);
			HandlerWorkFlows.saveResponseUser(forma, beanNotified, mayorKeep,
					request);
			putAtributte("idWorkFlow", String.valueOf(forma.getIdWorkFlow()));
			putAtributte("row", String.valueOf(forma.getRow()));
			log.info("forma.getResult(): " + forma.getResult());
			log.info("beanNotified.getNotified(): "
					+ beanNotified.getNotified());
			log.info("beanNotified.getEmail(): " + beanNotified.getEmail());
			/*
			 * if
			 * (!HandlerWorkFlows.wfuCanceled.equalsIgnoreCase(forma.getResult
			 * ())|| (!forma.getConditional().equalsIgnoreCase(HandlerWorkFlows.
			 * wfConditional))) { if
			 * ("0".equalsIgnoreCase(beanNotified.getNotified())) {
			 * HandlerWorkFlows.notifiedUsers(rb.getString("wf.newWFTitle"),
			 * rb.getString("mail.nameUser"),
			 * HandlerParameters.PARAMETROS.getMailAccount(),
			 * beanNotified.getEmail(), beanNotified.getComments()); } }
			 */

			if (!HandlerWorkFlows.wfuCanceled.equalsIgnoreCase(forma
					.getResult())) {
				log.info("Secuencial: '" + forma.getSecuential() + "'");
				if (HandlerWorkFlows.wfSecuential.equals(forma.getSecuential())) {
					HandlerWorkFlows
							.notifiedUsers(rb.getString("wf.newWFTitle"), rb
									.getString("mail.nameUser"), HandlerParameters.PARAMETROS.getMailAccount(), beanNotified
									.getEmail(), beanNotified.getComments());
				}
			}

			// --------------------------------------------------------
			if (!HandlerWorkFlows.wfuCanceled.equalsIgnoreCase(forma
					.getResult())) {
				// Env�o de Notificaci�n de Respuesta al Usuario que inici� el
				// Flujo de Trabajo
				if (String.valueOf(beanNotified.getNotified()).toLowerCase()
						.equals("null")) {
					// consultamos el flujo si tiene que ser notificado
					beanNotified = HandlerWorkFlows
							.getNotifiedFlexWF(beanNotified,
									forma.getIdWorkFlow(), forma.getRow());
					// if(!bean.getComments()) {
					// beanNotified.setNotified("0");
					// beanNotified.setComments(comments);
					// }
				}
				
				if ("0".equalsIgnoreCase(beanNotified.getNotified())) {
					LoginUser user = new LoginUser();
					StringBuilder mensaje = new StringBuilder(2048);

					if (HandlerWorkFlows.wfuReInit.equals(Integer
							.toString(forma.getStatu()))) {
						HandlerWorkFlows.getPendingUsersInFlexWF(
								(int) forma.getIdFlexFlow(), beanNotified);
						user.setEmail(beanNotified.getEmail());

						// mensaje de reinicio de flujo parametrico (puede ser
						// el mismo de nuevo flujo)
						mensaje.append(rb.getString("wf.reasignedTitle"))
								.append("<br/>")
								.append(rb.getString("wf.reasigned"))
								.append(" ").append(docForm.getIdDocument())
								.append("<br/>")
								.append(rb.getString("wf.overDoc")).append(" ")
								.append(docForm.getNameDocument())
								.append("<br/>")
								.append(rb.getString("wf.reasignedWF"));
					} else {
						user.setUser(beanNotified.getOwner());
						HandlerDBUser.load(user, true);

						mensaje.append(rb.getString("wf.user")).append(" ")
								.append(forma.getNameUser()).append("<br/>")
								.append(rb.getString("wf.response"))
								.append("<br/>")
								.append(rb.getString("wf.overDoc")).append(" ")
								.append(docForm.getNameDocument())
								.append("<br/>");
					}

					// System.out.println("mensaje.toString() = " +
					// mensaje.toString());
					HandlerWorkFlows.notifiedUsers(rb
							.getString("wf.newWFTitle"), rb
							.getString("mail.nameUser"), HandlerParameters.PARAMETROS.getMailAccount(),
							user.getEmail(), mensaje.toString());
				}
			} else {
				LoginUser user = new LoginUser();
				user.setUser(beanNotified.getOwner());
				HandlerDBUser.load(user, true);
				StringBuilder mensaje = new StringBuilder(2048)
						.append(rb.getString("wf.user")).append(" ")
						.append(forma.getNameUser()).append("<br/>")
						.append(rb.getString("wf.respCanceled"))
						.append("<br/>").append(rb.getString("wf.overDoc"))
						.append(" ").append(beanNotified.getNameDocument())
						.append("<br/>");
				HandlerWorkFlows.notifiedUsers(rb.getString("wf.newWFTitle"),
						rb.getString("mail.nameUser"), HandlerParameters.PARAMETROS.getMailAccount(), user.getEmail(), mensaje.toString());
			}
			removeAttribute("showEditReg");
			// SIMON 12 DE JULIO 2005 INICIO
			String str = null;
			if (forma.getEnd().equalsIgnoreCase("1")) { // se finaliza el
														// flujo..
				str = "doc.workflows";
				return goSucces(str);
			} else {
				return goSucces();
			}
		} catch (ApplicationExceptionChecked ap) {
			ap.printStackTrace();
			return goError(ap.getKeyError());
		} catch (Exception ex) {

		}
		return goError();
	}
}
