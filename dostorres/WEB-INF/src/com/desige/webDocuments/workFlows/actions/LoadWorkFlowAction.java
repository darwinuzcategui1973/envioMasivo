package com.desige.webDocuments.workFlows.actions;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Hashtable;
import java.util.ResourceBundle;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import sun.jdbc.rowset.CachedRowSet;

import com.desige.webDocuments.document.forms.BaseDocumentForm;
import com.desige.webDocuments.persistent.managers.HandlerDBUser;
import com.desige.webDocuments.persistent.managers.HandlerGrupo;
import com.desige.webDocuments.persistent.managers.HandlerStruct;
import com.desige.webDocuments.persistent.managers.HandlerWorkFlows;
import com.desige.webDocuments.persistent.utils.JDBCUtil;
import com.desige.webDocuments.seguridad.forms.PermissionUserForm;
import com.desige.webDocuments.structured.forms.BaseStructForm;
import com.desige.webDocuments.utils.ApplicationExceptionChecked;
import com.desige.webDocuments.utils.Constants;
import com.desige.webDocuments.utils.DesigeConf;
import com.desige.webDocuments.utils.ToolsHTML;
import com.desige.webDocuments.utils.beans.SuperAction;
import com.desige.webDocuments.utils.beans.Users;
import com.desige.webDocuments.workFlows.forms.DataWorkFlowForm;
import com.desige.webDocuments.workFlows.forms.ParticipationForm;

/**
 * Title: LoadWorkFlowAction.java<br>
 * Copyright: (c) 2004 Focus Consulting C.A.<br/>
 * 
 * @author Ing. Nelson Crespo (NC)
 * @author Ing. Simon Rodriguez (SR)
 * @version WebDocuments v3.0 <br>
 *          Changes:<br>
 *          <ul>
 *          <li>06-09-2004 (NC) Creation</li>
 *          <li>14-05-2005 (NC) Cambios para el control de Registros</li>
 *          <li>19/05/2005 (SR) se agrego Sw_Register para valiudar si
 *          tienepermisos o no para ver el archivo de esa carpeta</li>
 *          <li>19/07/2005 (SR) se creo la busqueda del dueño del flujo
 *          usersWFOwner y se creo una session usersWFOwner</li>
 *          <li>13/07/2006 (NC) Cambios para heredar o no los Prefijos de las
 *          Carpetas</li> *
 *          <ul>
 */

public class LoadWorkFlowAction extends SuperAction {
	static Logger log = LoggerFactory.getLogger("[V4.0 FTP] "
			+ LoadWorkFlowAction.class.getName());

	public ActionForward execute(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) {
		super.init(mapping, form, request, response);

		// loadWF.do?idWorkFlow=239&row=0&owner=false&isFlexFlow=true";

		try {
			log.debug("[LoadWorkFlowAction] getRequestURI="
					+ request.getRequestURI());
			log.debug("[LoadWorkFlowAction] getRequestURL="
					+ request.getRequestURL());
			log.debug("[LoadWorkFlowAction] getQueryString="
					+ request.getQueryString());

			String idWorkFlow = (String) ToolsHTML.getAttribute(request,
					"idWorkFlow", true);
			String idRow = (String) ToolsHTML
					.getAttribute(request, "row", true);
			String origen = (String) ToolsHTML.getAttribute(request, "origen",
					true);
			ResourceBundle rb = ToolsHTML.getBundle(request);

			boolean isFlexFlow = "true"
					.equalsIgnoreCase(getParameter("isFlexFlow"));
			boolean isCompleted = "true"
					.equalsIgnoreCase(getParameter("toCompleted"));

			log.debug("isFlexFlow: " + isFlexFlow);

			if (ToolsHTML.isEmptyOrNull(idWorkFlow)) {
				DataWorkFlowForm showDataWF = (DataWorkFlowForm) getSessionObject("showDataWF");
				if (showDataWF != null) {
					idWorkFlow = showDataWF.getIdWorkFlow();
					idRow = String.valueOf(showDataWF.getIdMovement());
				}
			}
			Users usuario = getUserSession();
			removeAttribute("toCanceled");
			removeAttribute("toCompleted");
			DataWorkFlowForm dataWF = new DataWorkFlowForm();
			dataWF.setIdWorkFlow(idWorkFlow);
			// Si es un Flujo Par�metrico se carga la Informaci�n
			// del Mismo de la Tabla de Flujo Param�tricos
			if (isFlexFlow) {
				HandlerWorkFlows.loadDataWorkFlow("flexworkflow", dataWF);
			} else {
				HandlerWorkFlows.loadDataWorkFlow("workflows", false, dataWF);
			}

			String nameFile = ToolsHTML.changeNameFile(usuario.getUser(),
					dataWF.getNameFile());
			dataWF.setNameFile(nameFile);

			dataWF.setComments(ToolsHTML.updateURLVerDocumento(
					dataWF.getComments(), request, null));

			putObjectSession("showDataWF", dataWF);
			String toCanceled = getParameter("toCanceled");
			String toCompleted = getParameter("toCompleted");
			putAtributte("toCanceled", toCanceled);
			putAtributte("toCompleted", toCompleted);
			boolean isNew = "true".equalsIgnoreCase(getParameter("isNew"));
			// Se debe marcar aqui cuando el Usuario lea un Flujo Param�trico
			if (isNew) {
				String rowUser = getParameter("rowUser");
				if (!"0".equalsIgnoreCase(rowUser)) {
					HandlerWorkFlows.updateWFReading(rowUser);
				} else {
					HandlerWorkFlows.updateWFReadingOwner(idWorkFlow);
				}
			}

			// colocamos en session la variable que nos dira si se puede
			// rechazar con un documento
			String rejectAndCreate = HandlerDBUser
					.getParameter("documentReject",Thread.currentThread().getStackTrace()[1].getMethodName());
			removeAttribute("rejectAndCreate");
			if (rejectAndCreate != null && rejectAndCreate.equals("1")) {
				putObjectSession("rejectAndCreate", rejectAndCreate);
			}

			// removeAttribute("showEditReg");
			// log.debug("docForm.getTypeDocument() = " + dataWF.getTypeDoc());
			// int idStructoReg=dataWF.getIdnode();
			// if (dataWF.getTypeDoc()!=null&&
			// DesigeConf.getProperty("typeDocs.docRegister").equalsIgnoreCase(dataWF.getTypeDoc()))
			// {
			// log.debug("SHOW edit Registers");
			// putObjectSession("showEditReg","true");
			// }

			// System.out.println(getParameter("owner"));
			boolean isOwnerWF = "true".equalsIgnoreCase(getParameter("owner"));
			boolean isPrintWF = "true".equalsIgnoreCase(getParameter("isPrintWF"));

			// Luis Cisneros
			// 14/03/07
			// Necesito ese valor de owner para pasarlo a la pagina de detalles,
			// lo guardo en el request como un atributo
			request.setAttribute("owner", new Boolean(isOwnerWF));
			request.setAttribute("isPrintWF", new Boolean(isPrintWF));
			request.setAttribute("row", idRow);
			request.setAttribute("isFlexFlow", Boolean.toString(isFlexFlow));
			// fin 14/03/07
        // GRG	
		   if(dataWF.getSubtypeWF()!=null) {
			   if (dataWF.getSubtypeWF().equals(HandlerWorkFlows.wfSubtypeSolcambio)){;
         			request.setAttribute("cambiar", isOwnerWF?"1":"0");   
			   }
			   if (dataWF.getSubtypeWF().equals(HandlerWorkFlows.wfSubtypeSolelimin)){;
				request.setAttribute("eliminar", isOwnerWF?"1":"0");  
	           }
		   }
		// GRG
			// Collection usersWFOwner =
			// HandlerWorkFlows.getOwnerUserInWorkFlow(idWorkFlow);
			Collection usersWF = null;
			if (isFlexFlow) {
				usersWF = HandlerWorkFlows.getAllUserInFlexFlow(
						Long.parseLong(dataWF.getIdWorkFlow()),
						getMessage("act.reinit"));
			} else {
				usersWF = HandlerWorkFlows.getAllUserInWorkFlow(idWorkFlow);
			}
			// verificamos si el usuario actual participa en el flujo
			boolean participante = false;
			for (Object obj : usersWF) {
				ParticipationForm pf = (ParticipationForm) obj;
				// System.out.println(usuario.getNamePerson() + "  -> " +
				// pf.getNameUser());
				if (usuario.getNamePerson().equals(pf.getNameUser())) {
					participante = true;
					break;
				}
			}

			ParticipationForm forma = new ParticipationForm(usuario.getUser(),
					usuario.getNamePerson());
			if (ToolsHTML.isNumeric(idRow)) {
				forma.setRow(Integer.parseInt(idRow));
			}
			forma.setIdDocument(dataWF.getNumDocument());
			forma.setIdWorkFlow(Integer.parseInt(idWorkFlow));
			forma.setNumVersion(dataWF.getNumVersion());
			forma.setIdFlexFlow(dataWF.getIdFlexFlow());
			forma.setNumAct(dataWF.getNumAct());

			if (isFlexFlow) {
				HandlerWorkFlows.loadParticipation(forma, "flexworkflow",
						"user_flexworkflows", isOwnerWF, true, false);
			} else {
				/*
				 * HandlerWorkFlows.loadParticipation(forma,isOwnerWF);
				 * putObjectSession("responseWF",forma);
				 * putObjectSession("usersWFOwner",usersWFOwner);
				 */
				HandlerWorkFlows.loadParticipation(forma, "workflows",
						"user_workflows", isOwnerWF, false, true);

				// verificamos que no hay usuarios pendientes en el flujo
				// PENDIENTE: verificar que cuando el propietario es
				// participante en el flujo no funciona
				if (isOwnerWF) {
					if (HandlerWorkFlows.thereArePendingUsers(forma
							.getIdWorkFlow())) {
						if (!HandlerWorkFlows.isPendingUserWorkFlow(
								forma.getIdWorkFlow(), usuario.getUser())) {
							forma.setStatu(0);
						}
					}
				}

				Collection usersWFOwner = HandlerWorkFlows
						.getOwnerUserInWorkFlow(idWorkFlow);
				putObjectSession("usersWFOwner", usersWFOwner);
			}
			forma.setPrintWF(isPrintWF);

			log.debug("Participation Statu " + forma.getStatu());
			removeAttribute("rejected");
			removeAttribute("rejectedDocument");
			removeAttribute("rejectedFlexFlow");
			if (isOwnerWF) {
				ParticipationForm resultAnt = HandlerWorkFlows
						.getStatuLastAnswer(forma.getIdWorkFlow(),
								isFlexFlow ? "user_flexworkflows"
										: "user_workflows");
				if (resultAnt != null
						&& HandlerWorkFlows.wfuRejected
								.equalsIgnoreCase(resultAnt.getResult())) {
					putObjectSession("rejected", "true");
					dataWF.setNameWF(rb.getString("wf.restar"));
				}
			}
			// buscamos los documentos de rechazo relacionados
			Collection documentOfRejection = new ArrayList();
			removeAttribute("documentOfRejection");
			if (isFlexFlow) {
				documentOfRejection = HandlerWorkFlows
						.getDocumentsFlexFlowOfRejection(dataWF);
				putObjectSession("documentOfRejection", documentOfRejection);
			}

			putObjectSession("responseWF", forma);
			putObjectSession("usersWF", usersWF);
			putObjectSession("idRowResp", String.valueOf(forma.getRow()));
			putObjectSession("idWF", String.valueOf(forma.getIdWorkFlow()));
			putObjectSession("participante", String.valueOf(participante));

			// Se comenta la condicion para mostrar comentarios, en flujos
			// cancelados
			// String loadToShow = getParameter("toShow");
			// if (ToolsHTML.isNumeric(idRow) ||
			// !ToolsHTML.isEmptyOrNull(loadToShow)) {
			Collection comments;// = new Vector();
			if (isFlexFlow) {
				comments = HandlerWorkFlows.getCommentsFlexFlowUser(dataWF);
			} else {
				comments = HandlerWorkFlows.getCommentsUser(dataWF, true);
			}
			putObjectSession("comments", comments);
			// }

			// Code to show Charge User or Name User
			Hashtable tree = (Hashtable) getSessionObject("tree");
			// simon 19 mayo 2005

			boolean Sw_Register = false;
			// simon 19 mayo 2005
			Hashtable security = (Hashtable) getSessionObject("security");
			StringBuffer idStructs = new StringBuffer(50);
			if (security == null) {
				idStructs.append("1");
				security = HandlerGrupo.getAllSecurityForGroup(
						usuario.getIdGroup(), idStructs);
				HandlerDBUser.getAllSecurityForUser(usuario.getIdPerson(),
						security, idStructs);
			}
			if (tree == null) {
				// tree =
				// HandlerStruct.loadAllNodes(security,usuario.getUser(),usuario.getIdGroup(),false);
				Hashtable subNodos = new Hashtable();
				tree = HandlerStruct.loadAllNodes(security,
						DesigeConf.getProperty("application.userAdmon"),
						DesigeConf.getProperty("application.admon"), subNodos,
						idStructs.length() > 0 ? idStructs.toString() : null);
			}
			BaseDocumentForm docForm = new BaseDocumentForm();
			docForm.setIdDocument(String.valueOf(dataWF.getNumDocument()));
			docForm.setNumberGen(String.valueOf(dataWF.getNumDocument()));
			// Hashtable tree = (Hashtable)getSessionObject("tree");
			// tree = ToolsHTML.checkTree(tree,usuario);
			HandlerStruct.loadDocument(docForm, true, false, tree, request);
			String idLocation = HandlerStruct.getIdLocationToNode(tree,
					docForm.getIdNode());
			putAtributte("nodeActive", docForm.getIdNode());
			// obtenemos toda la seguridad del los documentos de dicho usuario
			// ************************************************************//
			Hashtable securityDocs = null;
			securityDocs = ToolsHTML.checkDocsSecurity(securityDocs, usuario,
					docForm.getIdDocument());
			PermissionUserForm securityDocsfrm = null;
			if (!ToolsHTML.isEmptyOrNull(docForm.getIdDocument())) {
				securityDocsfrm = (PermissionUserForm) securityDocs.get(docForm
						.getIdDocument());
			}
			// ************************************************************//
			// permisos que le permiten editar o no el registro
			// removeAttribute("showEditReg");
			// PermissionUserForm perm =
			// (PermissionUserForm)security.get(String.valueOf(idStructoReg));
			// if (perm!=null&&perm.getToEditRegister()==Constants.permission) {
			// if
			// (docForm.getTypeDocument().equalsIgnoreCase(DesigeConf.getProperty("typeDocs.docRegister"))){
			// if (securityDocsfrm!=null){
			// if
			// ("1".equalsIgnoreCase(String.valueOf(securityDocsfrm.getToEditRegister()))){
			// putObjectSession("showEditReg","true");
			// }
			// }
			// }
			// }
			// permisos que le permiten editar o no el registro
			removeAttribute("showEditReg");
			ToolsHTML tools = new ToolsHTML();
			if (forma.getIdNode() == null || forma.getIdNode().equals("")
					|| forma.getIdNode().equals("0")) {
				// buscamos el nodo donde esta el documento
				ArrayList<Object> parametros = new ArrayList<Object>();
				parametros.add(new Integer(forma.getIdDocument()));
				StringBuffer sb = new StringBuffer(
						"SELECT IdNode FROM documents WHERE numGen=?");
				CachedRowSet crs = JDBCUtil.executeQuery(sb, parametros, Thread.currentThread().getStackTrace()[1].getMethodName());
				if (crs.next()) {
					forma.setIdNode(crs.getString("IdNode"));
				}
			}
			PermissionUserForm perm = tools.getSecurityUserInDoc(usuario,
					String.valueOf(forma.getIdDocument()), forma.getIdNode());
			putObjectSession("permission", perm);

			// perm =
			// HandlerDBUser.getSecurityForIDUserInDocs(usuario.getUser(),String.valueOf(forma.getIdDocument()));
			// if (perm==null) {
			// //Se Carga la Seguridad para el Grupo
			// log.debug("Seguridad del Grupo sobre el Documento");
			// perm =
			// HandlerGrupo.getSecurityForIDGroupInDoc(usuario.getIdGroup(),String.valueOf(forma.getIdDocument()));
			// if (perm==null) {
			// //Se Carga la Seguridad para el Usuario a Nivel de Carpeta
			// log.debug("Seguridad del Usuario sobre la Estructura");
			// perm =
			// HandlerStruct.getSecurityForIDUserInStruct(usuario.getIdPerson(),forma.getIdNode());
			// if (perm==null) {
			// log.debug("Seguridad del Grupo sobre la Estructura");
			// //Se Carga la Seguridad para el Usuario a Nivel de Grupos
			// perm =
			// HandlerStruct.getSecurityForIDGroupInStruct(usuario.getIdGroup(),forma.getIdNode());
			// }
			// }
			// }
			if (perm != null
					&& perm.getToEditRegister() == Constants.permission) {
				// solo son editables archivos tipo registro
				if (docForm.getTypeDocument() != null
						&& DesigeConf.getProperty("typeDocs.docRegister") != null
						&& docForm.getTypeDocument().equalsIgnoreCase(
								DesigeConf.getProperty("typeDocs.docRegister"))) {
					if (securityDocsfrm != null) {
						if ("1".equalsIgnoreCase(String.valueOf(securityDocsfrm
								.getToEditRegister()))) {
							putObjectSession("showEditReg", "true");
						}
					}
				}
			}
			log.debug("idLocation = " + idLocation);
			removeObjectSession("showCharge");
			if (idLocation != null) {
				BaseStructForm localidad = (BaseStructForm) tree
						.get(idLocation);
				if (localidad != null) {
					log.debug("localidad.getShowCharge() = "
							+ localidad.getShowCharge());
					if (localidad.getShowCharge() == 1) {
						putObjectSession("showCharge", "true");
					}
				}
			}
			// end

			putAtributte("origen", origen);

			boolean showStruct = false;
			log.debug("tree = " + tree);
			if ("true".equalsIgnoreCase(getParameter("showStruct"))) {
				putObjectSession("security", security);
				putObjectSession("tree", tree);
				putObjectSession("idNodeRoot", docForm.getIdNode());
				putAtributte("pagesInfo", "editWorkFlow.jsp");
				showStruct = true;
			}
			if (showStruct) {
				return goTo("struct");
			}
			if (isFlexFlow) {
				if (isCompleted) {
					forma.setStatu(0);
					putObjectSession("responseWF", forma);
				}
				// System.out.println(request.getAttribute("toCanceled"));
				// System.out.println(request.getAttribute("toCompleted"));
				// System.out.println(forma.getStatu());
				return goTo("editFlexFlow");
			}
			return goSucces();
		} catch (ApplicationExceptionChecked ap) {
			log.error(ap.getMessage());
			return goError(ap.getKeyError());
		} catch (Exception ex) {
			log.error(ex.getMessage());
			ex.printStackTrace();
		}
		return goError();
	}
}
