package com.desige.webDocuments.users.actions;

import java.util.Collection;
import java.util.Iterator;
import java.util.ResourceBundle;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.desige.webDocuments.area.forms.Area;
import com.desige.webDocuments.persistent.managers.HandlerDBUser;
import com.desige.webDocuments.persistent.managers.HandlerGrupo;
import com.desige.webDocuments.persistent.managers.HandlerParameters;
import com.desige.webDocuments.persistent.managers.HandlerProcesosSacop;
import com.desige.webDocuments.persistent.managers.HandlerStruct;
import com.desige.webDocuments.users.forms.LoginUser;
import com.desige.webDocuments.utils.ApplicationExceptionChecked;
import com.desige.webDocuments.utils.Constants;
import com.desige.webDocuments.utils.ToolsHTML;
import com.desige.webDocuments.utils.beans.Search;
import com.desige.webDocuments.utils.beans.SuperAction;
import com.desige.webDocuments.utils.beans.SuperActionForm;
import com.desige.webDocuments.utils.beans.Users;
import com.focus.qweb.dao.CargoDAO;
import com.focus.qweb.to.CargoTO;

/**
 * Title: EditUserAction.java <br/>
 * Copyright: (c) 2004 Focus Consulting C.A.<br/>
 * 
 * @author Ing. Roger Rodríguez
 * @version WebDocuments v3.0 <br/>
 *          Changes:<br/>
 *          <ul>
 *          <li>06/06/2004 (RR) Creation</li>
 *          </ul>
 */
public class EditUserAction extends SuperAction {
	static Logger log = LoggerFactory.getLogger("[V4.0] "
			+ EditUserAction.class.getName());

	/**
     * 
     */
	public ActionForward execute(ActionMapping actionMapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) {
		super.init(actionMapping, form, request, response);
		try {
			String cmd = request.getParameter("cmd");
			String id = request.getParameter("user");
			String nextPage = request.getParameter("nextPage");
			String reload = request.getParameter("reload");
			String area = request.getParameter("area");
			String toSearch = getParameter("toSearch");
			log.debug("toSearch " + toSearch);
			// System.out.println("cmd = " + cmd);
			String areaFiltro = "";
			String nameNodeService = "";
			//int idNodeService = 0;
			Area areaClase = new Area();
			if (cmd == null) {
				cmd = SuperActionForm.cmdLoad;
			}

			if (SuperActionForm.cmdReLoad.equalsIgnoreCase(reload)) {
				if (SuperActionForm.cmdInsert.equalsIgnoreCase(cmd)) {
					cmd = SuperActionForm.cmdLoad;
					id = null;
				}
			}
			if (SuperActionForm.cmdReLoad.equalsIgnoreCase(reload)) {
				if (SuperActionForm.cmdEdit.equalsIgnoreCase(cmd)) {
					cmd = SuperActionForm.cmdLoad;
					nextPage = null;
				}
			}

			getUserSession();
			Users usuario = getUserSession();
			LoginUser forma = (LoginUser) form;
			
			// iniciamos los mensajes
			HandlerDBUser.startActivityProgress();

			if (ToolsHTML.checkValue(cmd)) {
				if ((cmd.equalsIgnoreCase(SuperActionForm.cmdNew))
						|| (cmd.equalsIgnoreCase(SuperActionForm.cmdLoad))) {
					if (form == null) {
						forma = new LoginUser();
					}
					if ((cmd.equalsIgnoreCase(SuperActionForm.cmdLoad))
							&& (id != null)) {
						forma.setUser(id);
						HandlerDBUser.load(forma, true, true);

						CargoDAO oCargoDAO = new CargoDAO();
						CargoTO oCargoTO = new CargoTO();
						
						oCargoTO.setIdCargo(forma.getCargo());
						oCargoDAO.cargar(oCargoTO);
						
						forma.setArea(String.valueOf(oCargoTO.getArea().getIdarea()));
						areaFiltro = forma.getArea();
						//idNodeService=forma.getIdNodeService();
						nameNodeService = HandlerStruct.getIdNodeName(forma.getIdNodeService());
						putAtributte("areaFiltro", areaFiltro);
						
						
						putAtributte("idRout",forma.getIdNodeService()+"");
						putAtributte("idNodeService",forma.getIdNodeService()+"");
						putAtributte("nameNodeService",nameNodeService);

						cmd = SuperActionForm.cmdEdit;
						
					} else {
						if (!SuperActionForm.cmdReLoad.equalsIgnoreCase(reload)) {
							forma = new LoginUser();
						}
						// se va insertar un usuario, se inicializa cargo==-1
						// para que en usuario.jsp no de
						// error en validacion de java script
						forma.setCargo("-1");
						cmd = SuperActionForm.cmdInsert;
					}
					// 08 agosto 2005 inicio
					/*
					 * String Owner =
					 * HandlerDocuments.getFieldToDocument("Owner"
					 * ,forma.getNumDocument()); Hashtable secGroups =
					 * HandlerDocuments
					 * .loadSecurityDocument(false,forma.getNumDocument());
					 * Hashtable secUsers =
					 * HandlerDocuments.loadSecurityDocument
					 * (true,forma.getNumDocument());
					 */
					// forma.getUser()
					// al eliminar un usuario, se llena estos usuarios para sr
					// susrtituido por uno de ellos.

					Collection usuarios = HandlerDBUser.getAllUsersFilter(
							forma.getUser(), areaFiltro, false);
					putAtributte("usuarios", usuarios);

					Collection usuariosAll = HandlerDBUser.getAllUsersFilter(
							forma.getUser(), null, false);
					putAtributte("usuariosAll", usuariosAll);

					// 08 agosto 2005 fin
					forma.setCmd(cmd);
					request.getSession().setAttribute("newUser", forma);
					String lengthPass = HandlerParameters.PARAMETROS.getLengthPass();
					forma.setLengthPass(lengthPass);
					Collection grupos = HandlerGrupo.getAllGrupos();

					if (HandlerGrupo.isOwnerDocuments(id)) {

						for (Iterator ite = grupos.iterator(); ite.hasNext();) {
							Search bean = (Search) ite.next();
							if (bean.getId().equals(Constants.ID_GROUP_VIEWER)) {
								grupos.remove(bean);
								break;
							}
						}
					}

					Collection desgrupos = HandlerGrupo.getDescriptionGrupos();
					request.getSession().setAttribute("datosGrupos", grupos);
					request.getSession().setAttribute("desGrupos", desgrupos);
					Collection tblareas = HandlerProcesosSacop.getAreas();
					Collection tblcargos = null;
					if (tblareas != null) {
						request.setAttribute("tblareas", tblareas);
						if (SuperActionForm.cmdReLoad.equalsIgnoreCase(reload)) {
							tblcargos = HandlerProcesosSacop.getCargo(area);
							forma.setArea(area);
						} else if (!SuperActionForm.cmdReLoad
								.equalsIgnoreCase(reload)) {
							if (!ToolsHTML.isEmptyOrNull(forma.getArea())) {
								tblcargos = HandlerProcesosSacop.getCargo(forma
										.getArea());
							}
						}
						if (tblcargos == null) {
							Iterator it = tblareas.iterator();
							if (it.hasNext()) {
								Search j = (Search) it.next();
								tblcargos = HandlerProcesosSacop.getCargo(j
										.getId());
							}
						}
						request.setAttribute("tblcargos", tblcargos);
					}
					updateParemeters(request);
					((LoginUser) form).setCmd(cmd);
					request.setAttribute("cmd", cmd);
					putAtributte("nextPage", nextPage);
					return (actionMapping.findForward("success"));
				} else {
					processCmd(forma, request, getSession(), usuario);
					updateParemeters(request);
					Collection tblareas = HandlerProcesosSacop.getAreas();
					Collection tblcargos;// = null;
					if (tblareas != null) {
						request.setAttribute("tblareas", tblareas);
						tblcargos = HandlerProcesosSacop.getCargo(area);
						request.setAttribute("tblcargos", tblcargos);
					}
					((LoginUser) form).setCmd(cmd);
					request.setAttribute("cmd", cmd);
					return goTo(nextPage);
				}
			}
		} catch (ApplicationExceptionChecked ae) {
			return goError(ae.getKeyError());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return goError();
	}

	/**
	 * 
	 * @param forma
	 * @param request
	 * @param sessionn
	 * @param usuario
	 * @return
	 * @throws ApplicationExceptionChecked
	 */
	public static boolean processCmd(LoginUser forma,
			HttpServletRequest request, HttpSession sessionn, Users usuario)
			throws ApplicationExceptionChecked {
		log.debug("forma = " + forma.getCmd());
		boolean resp = false;
		StringBuffer mensaje = null;
		ResourceBundle rb = ToolsHTML.getBundle(request);
		if (forma.getCmd().equalsIgnoreCase(SuperActionForm.cmdInsert)) {
			log.debug("Insertar Registro...");
			try {
				if (!ToolsHTML.showConcurrentUser()) {
					// consultamos los usuarios actualmente registrados
					HandlerDBUser hdu = new HandlerDBUser();
					int reg = hdu.getUserRegisterInDatabase();
					if (reg >= Constants.MAXIMO_USUARIOS_VERSION_LITE) {
						throw new ApplicationExceptionChecked("E0135");
					}
				}
				if (ToolsHTML.isNumeric(forma.getCargo())) {
					resp = HandlerDBUser.insert(forma);
				} else {
					throw new ApplicationExceptionChecked("E0071");
				}
			} catch (ApplicationExceptionChecked ae) {
				ae.printStackTrace();
				log.error(ae.getMessage());
				throw ae;
			} catch (Exception e) {
				e.printStackTrace();
			}
			if (resp) {
				request.setAttribute("info", rb.getString("app.editOk"));
				forma.cleanForm();
				return true;
			} else {
				mensaje = new StringBuffer(rb.getString("app.notEdit"));
				mensaje.append(" ").append(HandlerDBUser.getMensaje());
			}
		} else {
			if (forma.getCmd().equalsIgnoreCase(SuperActionForm.cmdEdit)) {
				log.debug("Editando Registro...: �sE CAMBIO EL cARGO?"
						+ request.getParameter("cambiocargo"));
				forma.setCambioCargo("true".equalsIgnoreCase(request
						.getParameter("cambiocargo")));
				if (HandlerDBUser.edit(forma, sessionn, usuario)) {
					request.setAttribute("info", rb.getString("app.editOk"));
					return true;
				} else {
					mensaje = new StringBuffer(rb.getString("app.notEdit"));
					mensaje.append(" ").append(HandlerDBUser.getMensaje());
				}
			} else {
				if (forma.getCmd().equalsIgnoreCase(SuperActionForm.cmdDelete)) {
					if (HandlerDBUser.deleteLogic(forma, sessionn, usuario)) {
						forma.cleanForm();
						// request.setAttribute("info",
						// rb.getString("app.delete"));
						HandlerDBUser.addMessageToActivityProgress(rb
								.getString("app.delete"));
						request.setAttribute("info",
								HandlerDBUser.getActivityProgress());
					} else {
						mensaje = new StringBuffer(
								rb.getString("app.notDelete"));
						mensaje.append(" ").append(HandlerDBUser.getMensaje());
					}

					System.out.println(HandlerDBUser.getActivityProgress());
				}
			}
		}
		if (mensaje != null) {
			log.debug("mensaje = " + mensaje);
			request.setAttribute("info", mensaje.toString());
		}
		return false;
	}
}
