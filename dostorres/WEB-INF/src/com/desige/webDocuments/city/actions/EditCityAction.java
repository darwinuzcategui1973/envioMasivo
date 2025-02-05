package com.desige.webDocuments.city.actions;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ResourceBundle;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.desige.webDocuments.city.forms.BaseCityForm;
import com.desige.webDocuments.city.forms.InsertCityForm;
import com.desige.webDocuments.persistent.managers.HandlerBD;
import com.desige.webDocuments.persistent.managers.HandlerCity;
import com.desige.webDocuments.persistent.utils.JDBCUtil;
import com.desige.webDocuments.utils.ApplicationExceptionChecked;
import com.desige.webDocuments.utils.ToolsHTML;
import com.desige.webDocuments.utils.beans.SuperAction;
import com.desige.webDocuments.utils.beans.SuperActionForm;

/**
 * Title: EditCityAction.java <br/>
 * Copyright: (c) 2004 Focus Consulting C.A.<br/>
 * 
 * @author Ing. Nelson Crespo (NC)
 * @version WebDocuments v3.0 <br/>
 *          Changes:<br/>
 *          <ul>
 *          <li>25/04/2004 (NC) Creation</li>
 *          <li>26/06/2006 (SR) Se valido que insertara o actualizara sin
 *          repetir el contenido del registro</li>
 *          </ul>
 */
public class EditCityAction extends SuperAction {
	static Logger log = LoggerFactory.getLogger("[V4.0 FTP] "
			+ EditCityAction.class.getName());

	/**
	 * 
	 */
	public ActionForward execute(ActionMapping actionMapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) {
		String cmd = request.getParameter("cmd");
		super.init(actionMapping, form, request, response);
		try {
			getUserSession();
			BaseCityForm forma = (BaseCityForm) form;
			if (ToolsHTML.checkValue(cmd)) {
				if ((cmd.equalsIgnoreCase(SuperActionForm.cmdNew))
						|| (cmd.equalsIgnoreCase(SuperActionForm.cmdLoad))) {
					log.debug("Se va a agregar una nueva Ciudad....");
					form = new InsertCityForm();
					request.getSession().setAttribute("insertCity", form);
					cmd = SuperActionForm.cmdInsert;
				} else {
					log.debug("[EditCityAction] Procesando comando " + cmd);
					processCmd(forma, request);
					cmd = SuperActionForm.cmdLoad;
				}
				log.debug("cmd [II] = " + cmd);
				((BaseCityForm) form).setCmd(cmd);
				request.setAttribute("cmd", cmd);
			}
			String input = request.getParameter("input");
			String value = request.getParameter("value");
			String nameForm = request.getParameter("nameForma");
			if (!ToolsHTML.isEmptyOrNull(input)
					&& !ToolsHTML.isEmptyOrNull(value)
					&& !ToolsHTML.isEmptyOrNull(nameForm)) {
				putAtributte("input", input);
				putAtributte("value", value);
				putAtributte("nameForma", nameForm);
			}
			return goSucces();
		} catch (ApplicationExceptionChecked ae) {
			log.error(ae.getMessage());
			return goError(ae.getKeyError());
		} catch (Exception e) {
			log.error(e.getMessage());
			e.printStackTrace();
		}
		return goError();
	}

	/**
	 * 
	 * @param forma
	 * @param request
	 * @return
	 * @throws Exception
	 */
	private static boolean processCmd(BaseCityForm forma,
			HttpServletRequest request) throws Exception {
		log.debug("forma = " + forma.getCmd());
		boolean resp = false;
		StringBuilder mensaje = null;
		ResourceBundle rb = ToolsHTML.getBundle(request);
		if (forma.getCmd().equalsIgnoreCase(SuperActionForm.cmdInsert)) {
			log.debug("Insertar Registro...");
			try {
				if (noExisteCiudad(forma)) {
					resp = HandlerCity.insert(forma);
				} else {
					request.setAttribute("info", rb.getString("reg.existe"));
					forma.cleanForm();
					return true;
				}

			} catch (Exception e) {
				e.printStackTrace();
			}
			if (resp) {
				request.setAttribute("info", rb.getString("app.editOk"));
				forma.cleanForm();
				return true;
			} else {
				mensaje = new StringBuilder(rb.getString("app.notEdit"))
						.append(" ").append(HandlerCity.getMensaje());
			}
		} else {
			if (forma.getCmd().equalsIgnoreCase(SuperActionForm.cmdEdit)) {
				log.debug("Editando Registro...");
				if (noExisteCiudad(forma)) {
					if (HandlerCity.edit(forma)) {
						request.setAttribute("info", rb.getString("app.editOk"));
						return true;
					} else {
						mensaje = new StringBuilder(rb.getString("app.notEdit"))
								.append(" ").append(HandlerCity.getMensaje());
					}
				} else {
					request.setAttribute("info", rb.getString("reg.existe"));
					forma.cleanForm();
					return true;
				}

			} else {
				if (forma.getCmd().equalsIgnoreCase(SuperActionForm.cmdDelete)) {
					log.debug("Elimando registro....");
					// Se verifica que la Ciudad no esté asociada a ningún
					// Usuario...
					String[] items = HandlerBD.getField(
							new String[] { "idPerson" }, "person",
							new String[] { "Ciudad", "accountActive" },
							new String[] { forma.getId(), "1" }, new String[] {
									"=", "=" }, new Object[] { "",
									new Integer(1) });
					if (items != null) {
						throw new ApplicationExceptionChecked("E0059");
					}
					if (HandlerCity.delete(forma)) {
						forma.cleanForm();
						request.setAttribute("info", rb.getString("app.delete"));
					} else {
						mensaje = new StringBuilder(
								rb.getString("app.notDelete")).append(" ")
								.append(HandlerCity.getMensaje());
					}
				}
			}
		}
		if (mensaje != null) {
			log.debug("mensaje = " + mensaje);
			request.setAttribute("info", mensaje.toString());
		}
		return false;
	}

	/**
	 * 
	 * @param forma
	 * @return
	 */
	private static boolean noExisteCiudad(BaseCityForm forma) {
		Connection con = null;
		PreparedStatement st = null;
		ResultSet rs = null;
		boolean swNoExiste = true;
		try {
			con = JDBCUtil.getConnection(Thread.currentThread().getStackTrace()[1].getMethodName());
			StringBuilder sql = new StringBuilder(1024)
					.append("SELECT NOMBRE FROM ciudad WHERE nombre='")
					.append(forma.getName().trim()).append("'");
			log.debug("sql=" + sql.toString());
			st = con.prepareStatement(JDBCUtil.replaceCastMysql(sql.toString()));
			rs = st.executeQuery();
			if (rs.next()) {
				swNoExiste = false;
			} else {
				swNoExiste = true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			JDBCUtil.closeQuietly(con, st, rs);
		}
		return swNoExiste;
	}

}
