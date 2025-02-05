package com.desige.webDocuments.structured.actions;

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

import com.desige.webDocuments.document.forms.MoveDocForm;
import com.desige.webDocuments.persistent.managers.HandlerBD;
import com.desige.webDocuments.persistent.managers.HandlerDBUser;
import com.desige.webDocuments.persistent.managers.HandlerDocuments;
import com.desige.webDocuments.persistent.managers.HandlerGrupo;
import com.desige.webDocuments.persistent.managers.HandlerStruct;
import com.desige.webDocuments.structured.forms.BaseStructForm;
import com.desige.webDocuments.structured.forms.InsertStructForm;
import com.desige.webDocuments.utils.ApplicationExceptionChecked;
import com.desige.webDocuments.utils.Constants;
import com.desige.webDocuments.utils.DesigeConf;
import com.desige.webDocuments.utils.ToolsHTML;
import com.desige.webDocuments.utils.beans.SuperAction;
import com.desige.webDocuments.utils.beans.SuperActionForm;
import com.desige.webDocuments.utils.beans.Users;
import com.focus.qweb.facade.AuditFacade;
import com.focus.qweb.to.AuditTO;
import com.focus.qweb.to.TransactionTO;

/**
 * Title: EditStructAction.java <br/>
 * Copyright: (c) 2004 Focus Consulting C.A.<br/>
 * 
 * @author Ing. Nelson Crespo (NC)
 * @version WebDocuments v3.0 <br/>
 *          Changes:<br/>
 *          <ul>
 *          <li>18/04/2004 (NC) Creation</li>
 *          <li>22/05/2005 (NC) Se modific� para el manejo din�mico de los
 *          prefijos</li>
 *          <li>24/07/2005 Cambios para Solicitar comentarios al mover una
 *          carpeta o proceso
 *          <li>13/07/2006 (NC) Cambios para heredar o no los Prefijos de las
 *          Carpetas</li>
 *          </ul>
 */
public class EditStructAction extends SuperAction {
	String next = "";
	String idNodeFat = "";
	static Logger log = LoggerFactory.getLogger("[V4.0] "
			+ EditStructAction.class.getName());

	
	private ResourceBundle rb;

	/**
	 * 
	 */
	public ActionForward execute(ActionMapping actionMapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) {
		super.init(actionMapping, form, request, response);
		final long t0 = System.currentTimeMillis();
		String cmd = getParameter("cmd");
		rb = ToolsHTML.getBundle(request);

		try {
			HandlerStruct handlerStruct = new HandlerStruct();
			String target = getParameter("target");
			next = getParameter("nexPage");

			if (!ToolsHTML.checkValue(target)) {
				target = "";
			}
			if (!ToolsHTML.checkValue(next)) {
				next = "";
			}
			Users user = getUserSession();
			String nameUser = user.getUser();
			BaseStructForm forma = (BaseStructForm) form;
			if (ToolsHTML.checkValue(cmd)) {
				if ((cmd.equalsIgnoreCase(SuperActionForm.cmdNew))
						|| (cmd.equalsIgnoreCase(SuperActionForm.cmdLoad))) {
					String nodeActive = (String) getSessionObject("nodeActive");
					if (ToolsHTML.checkValue(nodeActive)) {
						String nodeType = HandlerStruct.getTypeNode(nodeActive);
						if (nodeType != null) {
							request.setAttribute("nodeType", nodeType);
						}
					}
					String nodeType = getParameter("nodeType");
					if ("1".equalsIgnoreCase(nodeType)) {
						// el nodo padre siempre va a ser creada por primera vez
						// una localidad.
						putObjectSession("hayDocs", new Boolean(false));
					}
					log.debug("[EditStructAction] nodeType = " + nodeType);
					if (nodeType != null) {
						request.setAttribute("nodeTypeNew", nodeType);
					}
					form = new InsertStructForm();
					((InsertStructForm) form).initFields();
					Hashtable tree = (Hashtable) getSessionObject("tree");
					tree = ToolsHTML.checkTree(tree, user);
					BaseStructForm dataForm = (BaseStructForm) tree.get(nodeActive==null?"0":nodeActive);
					// Se Chequea como se debe construir los prefijos
					byte typePrefix = 0;
					String idLocation = HandlerStruct.getIdLocationToNode(tree,
							dataForm.getIdNode());
					if (idLocation != null) {
						BaseStructForm localidad = (BaseStructForm) tree
								.get(idLocation);
						if (localidad != null) {
							typePrefix = localidad.getTypePrefix();
							// heredarPrefijo = localidad.getHeredarPrefijo();
						}
					}
					// Se maneja si la Carpeta debe o No heredar el Prefijo
					// dataForm.setHeredarPrefijo(heredarPrefijo);
					((BaseStructForm) form)
							.setHeredarPrefijo(Constants.notPermission);
					// if (Constants.notPermission==heredarPrefijo) {
					// dataForm.setParentPrefix("");
					// } else {
					String prefix = handlerStruct.getParentPrefixinst(tree,
							dataForm.getIdNode(), 0 == typePrefix);
					log.debug("ParentPrefix: " + prefix);
					log.debug("Prefix: " + dataForm.getPrefix());
					log.debug("Heredar Prefijo? "
							+ dataForm.getHeredarPrefijo());
					if (dataForm.getHeredarPrefijo() == Constants.permission) {
						if (0 == typePrefix) {
							dataForm.setParentPrefix(dataForm.getPrefix()
									+ prefix);
						} else {
							dataForm.setParentPrefix(prefix
									+ dataForm.getPrefix());
						}
					} else {
						log.debug("Set Prefix...");
						dataForm.setParentPrefix(dataForm.getPrefix());
					}
					// }
					((BaseStructForm) form).setParentPrefix(dataForm
							.getParentPrefix());
					putObjectSession("insertStruct", form);
					cmd = SuperActionForm.cmdInsert;
					// Cambios para Cargar el Propietario de la Carpeta seg�n
					// Selecci�n del Usuario
					long idNodo = Long.parseLong(nodeActive);
					Hashtable secUsers = HandlerStruct.loadSecurityStruct(true,
							idNodo);
					Hashtable secGroups = HandlerStruct.loadSecurityStruct(
							false, idNodo);
					Collection allUsers = HandlerDBUser.getAllUsers(secUsers,
							secGroups, user.getUser());
					putObjectSession("allUsers", allUsers);
					// Fin
					log.debug("[EditStructAction] se va a Insertar");
				} else {
					log.debug("[EditStructAction] Procesando comando " + cmd);
					putObjectSession("idNodeSelected", forma.getIdNode());
					// if (SuperActionForm.cmdInsert.equalsIgnoreCase(cmd)) {
					// forma.setOwner(nameUser);
					// }
					if (cmd.equalsIgnoreCase(SuperActionForm.cmdPaste)) {
						forma.setIdNode(request.getParameter("idNodeChange"));
					}
					String nodeSelected = HandlerStruct.getValueField("IdNode",
							"0");
					// boolean movStruct =
					// request.getParameter("isCopy")!=null?request.getParameter("isCopy").equalsIgnoreCase("1"):false;
					boolean movStruct = "1".equalsIgnoreCase(request
							.getParameter("isCopy"));
					boolean movDocument = "1".equalsIgnoreCase(request
							.getParameter("movDocument"));
					// boolean movDocument =
					// request.getParameter("movDocument")!=null?request.getParameter("movDocument").equalsIgnoreCase("1"):false;
					//ydavila Ticket 001-00-003023
					String respsolcambio = forma.getrespsolcambio();
					String respsolelimin = forma.getrespsolelimin();
					String respsolimpres = forma.getrespsolimpres();
					boolean hacer = false;
					if (cmd.equalsIgnoreCase(SuperActionForm.cmdInsert)
							&& forma.getName().indexOf(";") != -1) {
						String owner = forma.getOwner();
						String[] lista = forma.getName().split(";");
						String[] ruta = forma.getRout().split("\\\\");
						String padre = ruta[ruta.length - 1];
						String pre = null;
						StringBuilder b = new StringBuilder();
						int pos = 0;
						int cant = 0;

						BaseStructForm copia = (BaseStructForm) forma.clone();
						for (int i = 0; i < lista.length; i++) {
							if (lista[i] != null
									&& lista[i].trim().length() > 0) {
								forma = (BaseStructForm) copia.clone();
								forma.setName(lista[i]);
								forma.setName(forma.getName()
										.replace('\'', ' ').replace('"', ' '));

								try {
									if (forma.getName().indexOf('?') != -1) {
										pos = forma.getName().indexOf('?');
										pre = forma.getName()
												.substring(pos + 1);
										pre = pre.substring(0, 1);
										cant = Integer.parseInt(pre);
										forma.setName(forma
												.getName()
												.replaceAll(
														"\\?".concat(pre),
														padre.substring(0, cant)));
									}
								} catch (Exception e) {
									e.printStackTrace();
									throw e;
								}
								// forma.setOwner(owner);
								hacer = processCmd(forma, request, nameUser,
										movStruct, movDocument, user);
							}
						}
					} else {
						if (forma.getName() != null) {
							forma.setName(forma.getName().replace('\'', ' ')
									.replace('"', ' '));
						}
						hacer = processCmd(forma, request, nameUser, movStruct,
								movDocument, user);
					}

					if (hacer) {
						if (!cmd.equalsIgnoreCase(SuperActionForm.cmdDelete)) {
							// aqui carga todo lo que mostrara en el formulario
							HandlerStruct.load(user.getUser(), forma);
							if (forma.getCmd().equalsIgnoreCase(
									SuperActionForm.cmdEdit)) {
								Hashtable arbol = (Hashtable) getSessionObject("arbol");
								StringBuilder node = new StringBuilder(
										"parent.frames['code'].refreshText('");
								node.append(forma.getName()).append("',")
										.append(arbol.get(forma.getIdNode()));
								node.append(");");
								putObjectSession("updateNode", node.toString());
							}
							Hashtable tree = (Hashtable) getSessionObject("tree");
							tree.put(forma.getIdNode().trim(), forma);
							request.setAttribute("idNodeSelected",
									forma.getIdNode());
							nodeSelected = forma.getIdNode();
						} else {
							log.debug("[DELETE] " + idNodeFat);
							nodeSelected = idNodeFat;
							request.setAttribute("idNodeSelected", idNodeFat);
							putObjectSession("nodeActive", idNodeFat);
						}
						putObjectSession("cmd", SuperActionForm.cmdLoad);
					}
					next += "?idNodeSelected=" + nodeSelected;
				}
				((BaseStructForm) form).setCmd(cmd);
				putObjectSession("cmd", cmd);
			}

			putAtributte("target", target);
			putAtributte("redirect", next);

			return goSucces();
		} catch (ApplicationExceptionChecked ae) {
			ae.printStackTrace();
			putAtributte("target", "_self");
			putAtributte("redirect", next);
			return goError(ae.getKeyError());
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			log.info("Operacion [" + cmd + "] en la estructura tardo: "
					+ (System.currentTimeMillis() - t0) + " ms");
		}

		request.setAttribute("target", "_self");
		request.setAttribute("redirect", "main-tree.jsp");
		return goError();
	}

	/**
	 * 
	 * @param forma
	 * @param request
	 * @param nameUser
	 * @param movStruct
	 * @param movDocuments
	 * @param user
	 * @return
	 * @throws ApplicationExceptionChecked
	 * @throws Exception
	 */
	private boolean processCmd(BaseStructForm forma,
			HttpServletRequest request, String nameUser, boolean movStruct,
			boolean movDocuments, Users user)
			throws ApplicationExceptionChecked, Exception {
		log.debug("forma = " + forma.getCmd());
		boolean resp = false;
		StringBuilder mensaje = null;
		// Si el Comando no es Delete
		if (!SuperActionForm.cmdDelete.equalsIgnoreCase(forma.getCmd())) {
			String[] items = null;
			if (SuperActionForm.cmdInsert.equalsIgnoreCase(forma.getCmd())) {
				items = HandlerBD
						.getField(
								new String[] { "idNode" },
								"struct",
								new String[] { "[NAME]", "idNodeParent" },
								new String[] { forma.getName(),
										forma.getIdNodeParent() },
								new String[] { "=", "=" }, new Object[] { "",
										new Integer(1) });
			} else {
				if (SuperActionForm.cmdEdit.equalsIgnoreCase(forma.getCmd())) {
					items = HandlerBD
							.getField(
									new String[] { "idNode" },
									"struct",
									new String[] { "[NAME]", "idNodeParent",
											"IdNode" },
									new String[] { forma.getName(),
											forma.getIdNodeParent(),
											forma.getIdNode() }, new String[] {
											"=", "=", "<>" },
									new Object[] { "", new Integer(1),
											new Integer(1) });
				}
			}
			if (items != null) {
				throw new ApplicationExceptionChecked("E0062");
			}
		}
		if (forma.getCmd().equalsIgnoreCase(SuperActionForm.cmdInsert)) {
			log.debug("Insertar Registro...");
			try {
				resp = HandlerStruct.insert(forma);
				// PENDIENTE: toListDist si tiene usuarios en la lista es 1
			} catch (Exception e) {
				e.printStackTrace();
			}
			if (resp) {
				StringBuffer idStructs = new StringBuffer(1024).append("1");
				Hashtable security = HandlerGrupo.getAllSecurityForGroup(
						user.getIdGroup(), idStructs);
				HandlerDBUser.getAllSecurityForUser(user.getIdPerson(),
						security, idStructs);
				putObjectSession("security", security);
				putObjectSession("info", rb.getString("app.editOk"));
				Hashtable tree = (Hashtable) getSessionObject("tree");// request.getSession().getAttribute("tree");
				tree.put(forma.getIdNode(), forma);
				// AQUI se activo el actulizacion 5-4-2022  "DARWINUZCATEGUI"
				// 2014-10-17 Comentado para evitar que al agregar un nodo, la
				// EOC, en el lado izquierdo se actualize tambien
				// esto ocasionaba un delay muy prolongado en casos muy
				// particulares
				// INCIO DEL des-COMNETARIO AQUI DARWINUZCATEGUI
				
				  Hashtable arbol = (Hashtable) getSessionObject("arbol");
				  StringBuilder node = new
				  StringBuilder(ToolsHTML.getSubNodes(forma.getIdNodeParent(),"itemPpal",tree,arbol));
				  node.append("parent.frames['code'].updateArbol(itemPpal,");
				  String nodeData = (String)arbol.get(forma.getIdNodeParent());
				  node.append(nodeData).append(");");
				  putObjectSession("updateNode",node.toString());
				// FIN DEL codigo descomebtado 05-04-2022 DARWINUZCATEGUI
				  
				// Users user = getUserSession();
				forma.setOwner(user.getNamePerson());
				return true;
			} else {
				mensaje = new StringBuilder(rb.getString("app.notEdit"));
				mensaje.append(" ").append(HandlerStruct.getMensaje());
			}
		} else {
			if (forma.getCmd().equalsIgnoreCase(SuperActionForm.cmdEdit)) {
				// PENDIENTE: toListDist si tiene usuarios en la lista es 1
				if (HandlerStruct.edit(forma)) {
					putObjectSession("info", rb.getString("app.editOk"));
					return true;
				} else {
					mensaje = new StringBuilder(rb.getString("app.notEdit"));
					mensaje.append("\n").append(HandlerStruct.getMensaje());
				}
			} else {
				if (forma.getCmd().equalsIgnoreCase(SuperActionForm.cmdDelete)) {
					try {
						HandlerStruct.load(user.getUser(), forma);
					} catch (Exception e) {
						e.printStackTrace();
					}
					idNodeFat = forma.getIdNodeParent();
					String nodeType = forma.getNodeType();
					if (HandlerStruct.delete(forma)) {
						Hashtable tree = (Hashtable) getSessionObject("tree");
						tree.remove(forma.getIdNode());
						Hashtable arbol = (Hashtable) request.getSession()
								.getAttribute("arbol");
						//AQUI ESTOY PROBANDO DARWINUZCATEGUI
						// 2014-10-17 Comentado para evitar que al agregar un
						// nodo, la EOC, en el lado izquierdo se actualize
						// tambien
						// esto ocasionaba un delay muy prolongado en casos muy
						// particulares
						//INICIO codigo descomentado 05-04-2022 DARWINUZCATEGUI
						
						  StringBuilder node = new
						  StringBuilder(ToolsHTML.getSubNodes
						  (forma.getIdNodeParent(),"itemPpal",tree,arbol));
						  node.append("parent.frames['code'].updateArbol(itemPpal,");
						  
						  String nodeData =(String)arbol.get(forma.getIdNodeParent());
						  node.append(nodeData).append(");");
						  putObjectSession("updateNode",node.toString());
						 
						// FIN code descomnetado 05-04-2022 DARWINUZCATEGUI
						  
						arbol.remove(forma.getIdNode());
						forma.clearForm();
						putObjectSession("info",rb.getString("app.delete".concat(nodeType)));
						
						AuditFacade.insertarAuditFacade(rb, TransactionTO.ELIMINAR_CARPETA, "", user.getIdPerson(),user.getNameUser(), "", "", AuditTO.getClientIpAddress(request));
						return true;
					} else {
						mensaje = new StringBuilder(
								rb.getString("app.notDelete".concat(nodeType)));
						mensaje.append(" ").append(HandlerStruct.getMensaje());
					}
				} else {
					if (forma.getCmd().equalsIgnoreCase(SuperActionForm.cmdPaste)) {
						String idNodeChange = request.getParameter("idNodeChange");
						String newFather = request.getParameter("idNodeSelected");
						if (idNodeChange.equalsIgnoreCase(newFather)) {
							throw new ApplicationExceptionChecked(
									"err.invalidNewFather");
						}
						if (movStruct) {
							if (!HandlerStruct.isNodeRelated(idNodeChange,
									newFather)) {
								Hashtable tree = (Hashtable) getSessionObject("tree");
								if (forma == null) {
									forma = new BaseStructForm();
								}
								forma = (BaseStructForm) tree.get(idNodeChange);
								if (isValidNewFather(tree,
										forma.getIdNodeParent(), newFather,
										forma.getNodeType())) {
									String idNodeFatherAnt = forma
											.getIdNodeParent();
									next = "commentsToMoveSt.jsp";
									MoveDocForm formaMov = new MoveDocForm(
											idNodeChange, idNodeFatherAnt,
											newFather);
									// formaMov.setTo(forma.getRout());
									BaseStructForm fatherAnt = (BaseStructForm) tree
											.get(idNodeFatherAnt);
									formaMov.setFrom(fatherAnt.getRout());
									BaseStructForm fatherNew = (BaseStructForm) tree
											.get(newFather);
									formaMov.setTo(fatherNew.getRout());
									putObjectSession("moveStructForm", formaMov);

									AuditFacade.insertarAuditFacade(rb, TransactionTO.MOVER_CARPETA, "", user.getIdPerson(),user.getNameUser(), fatherAnt.getName(), fatherNew.getName(), AuditTO.getClientIpAddress(request));
									
									// inicio de codigo descomentado 05-04-2022 DARWINUZCATEGUI
/*
									 if (HandlerStruct.moveNode(idNodeChange,newFather,idNodeFatherAnt,nameUser, idNodeFatherAnt))							 
									 {
										 putObjectSession("info",rb.getString("app.change"));
										 Hashtable arbol = (Hashtable)request.getSession().getAttribute("arbol");
										 StringBuilder node = new StringBuilder(ToolsHTML.getSubNodes(newFather,"itemPpal",tree,arbol));
										 node.append("parent.frames['code'].updateArbol(itemPpal,");
										 String nodeData = (String)arbol.get(newFather);
										 node.append(nodeData).append(");");
										 StringBuilder nodeII = new StringBuilder(ToolsHTML.getSubNodes(idNodeFatherAnt,"itemPpalII",tree,arbol));
										 nodeII.append("parent.frames['code'].updateArbol(itemPpalII,");
										 nodeData =(String)arbol.get(idNodeFatherAnt);
										 nodeII.append(nodeData).append(");");
										 putObjectSession("updateNode",node.toString());
										 putObjectSession("updateNodeII",nodeII.toString());
										 return true;
										 
									 } else{
										 mensaje = new StringBuilder(rb.getString("app.notChange"));
										 mensaje.append(" ").append(HandlerStruct.getMensaje());
									 }
									 
									 */
									
									// FIN codigo descomentado  05-04-2022 DARWINUZCATEGUI
								} else {
									mensaje = new StringBuilder(
											rb.getString("app.invalidNewFather"));
								}
							} else {
								mensaje = new StringBuilder(
										rb.getString("app.invalidNewFather"));
							}
						} else {
							if (movDocuments) {
								Hashtable tree = (Hashtable) request
										.getSession().getAttribute("tree");
								// BaseStructForm oldFatherStruct =
								// (BaseStructForm)tree.get(idNodeChange);
								BaseStructForm newFatherStruct = (BaseStructForm) tree
										.get(newFather);
								if (!newFatherStruct
										.getNodeType()
										.equalsIgnoreCase(
												DesigeConf
														.getProperty("locationType"))) {
									// String idNodeFatherAnt =
									// forma.getIdNodeParent();
									String idDocument = getParameter("idDocument");
									MoveDocForm formaMov = new MoveDocForm(
											idDocument, idNodeChange, newFather);
									formaMov.setTo(newFatherStruct.getRout());
									BaseStructForm fatherAnt = (BaseStructForm) tree
											.get(idNodeChange);
									formaMov.setFrom(fatherAnt.getRout());

									BaseStructForm fatherNew = (BaseStructForm) tree
											.get(newFather);
									
									String[] numDoc = HandlerDocuments
											.getFields(
													new String[] { "number" },
													"documents", "numGen",
													idDocument);
									if (numDoc != null && numDoc.length > 0) {
										formaMov.setNumberDocAct(numDoc[0]);
									}
									putObjectSession("moveDocForm", formaMov);
									next = "commentsToMove.jsp";
									
									AuditFacade.insertarAuditFacade(rb , TransactionTO.MOVER_DOCUMENTO, "", user.getIdPerson(),user.getNameUser(), fatherAnt.getName(), fatherNew.getName(), AuditTO.getClientIpAddress(request));
									// exist =
									// HandlerDocuments.exitsDocInLocationByNumber(numero.toString(),idNode);
									// if
									// (HandlerDocuments.movementDocument(idDocument,newFather,idNodeChange,nameUser))
									// {
									// putObjectSession("info",rb.getString("app.change"));
									// } else {
									// mensaje = new
									// StringBuilder(rb.getString("app.notChange"));
									// mensaje.append(" ").append(HandlerStruct.getMensaje());
									// }
									//aqui fin del comentario 
								} else {
									mensaje = new StringBuilder(
											rb.getString("app.invalidNewFather"));
								}
							} else {
								mensaje = new StringBuilder(
										rb.getString("app.invalidNewFather"));
							}
						}
					}
				}

			}
		}
		if (mensaje != null) {
			log.debug("mensaje = " + mensaje);
			request.getSession().setAttribute("info", mensaje.toString());
		}
		return false;
	}

	/**
	 * 
	 * @param struct
	 * @param idOldFather
	 * @param idNewFather
	 * @param nodeType
	 * @return
	 */
	private boolean isValidNewFather(Hashtable struct, String idOldFather,
			String idNewFather, String nodeType) {
		BaseStructForm oldFather = (BaseStructForm) struct.get(idOldFather);
		BaseStructForm newFather = (BaseStructForm) struct.get(idNewFather);
		String oldType = oldFather.getNodeType();
		log.debug("idOldFather = " + idOldFather);
		log.debug("idNewFather = " + idNewFather);
		log.debug("oldType = " + oldType);
		log.debug("newFather.getNodeType() = " + newFather.getNodeType());
		log.debug("nodeType = " + nodeType);
		ArrayList options = ToolsHTML.getList("addDoc");
		String locationType = DesigeConf.getProperty("locationType");
		// Se esta moviendo una carpeta o proceso a una localidad
		if ((options.contains(nodeType))
				&& (newFather.getNodeType().equalsIgnoreCase(locationType))) {
			return true;
		}
		// Se esta moviendo un nodo entre nodos del mismo Tipo
		if (oldType.equalsIgnoreCase(newFather.getNodeType())) {
			return true;
		}
		// Se esta moviendo el modulo (desde una carpeta o proceso) a una
		// carpeta o proceso
		if ((options.contains(oldType))
				&& (options.contains(newFather.getNodeType()))) {
			return true;
		}
		// Se esta moviendo un Proceso o carpeta
		if ((options.contains(oldType))
				&& (!newFather.getNodeType().equalsIgnoreCase(locationType))) {
			return true;
		}
		if (oldType.equalsIgnoreCase(locationType)
				&& options.contains(newFather.getNodeType())) {
			return true;
		}
		return false;
	}
}
