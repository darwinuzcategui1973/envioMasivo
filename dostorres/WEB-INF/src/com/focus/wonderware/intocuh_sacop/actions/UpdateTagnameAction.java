package com.focus.wonderware.intocuh_sacop.actions;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.ResourceBundle;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.desige.webDocuments.utils.ApplicationExceptionChecked;
import com.desige.webDocuments.utils.Constants;
import com.desige.webDocuments.utils.ToolsHTML;
import com.desige.webDocuments.utils.beans.SuperAction;
import com.desige.webDocuments.utils.beans.SuperActionForm;
import com.focus.qweb.dao.TagNameDAO;
import com.focus.qweb.to.TagNameTO;
import com.focus.wonderware.intocuh_sacop.forms.Tagname;

/**
 * Created by IntelliJ IDEA. User: srodriguez Date: Mar 12, 2007 Time: 10:45:46
 * AM To change this template use File | Settings | File Templates.
 */
public class UpdateTagnameAction extends SuperAction {

	/**
	 * 
	 */
	public ActionForward execute(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) {
		super.init(mapping, form, request, response);

		try {
			String cmd = request.getParameter("cmd");
			Tagname forma = (Tagname) form;
			request.getSession().setAttribute("otroProcesoSacop", form);
			getUserSession();
			if (ToolsHTML.checkValue(cmd)) {
				if ((cmd.equalsIgnoreCase(SuperActionForm.cmdNew))
						|| (cmd.equalsIgnoreCase(SuperActionForm.cmdLoad))) {
					if (cmd.equalsIgnoreCase(SuperActionForm.cmdNew)) {
						removeObjectSession("valor");

					}
					form = new Tagname();
					forma = new Tagname();
					// en editar tambien lo blanqueamos porque vamos a editar
					removeObjectSession("editTypeDoc");

					putObjectSession("editTypeDoc", form);
					cmd = SuperActionForm.cmdInsert;
				} else {
					processCmd(forma, request);
					cmd = SuperActionForm.cmdLoad;
				}
				((SuperActionForm) form).setCmd(cmd);
				request.setAttribute("cmd", cmd);
			}

			return goSucces();
		} catch (ApplicationExceptionChecked ae) {
			ae.printStackTrace();
			return goError(ae.getKeyError());
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return goError();
	}

	/**
	 * 
	 * @param forma
	 * @param request
	 * @return
	 * @throws ApplicationExceptionChecked
	 */
	private static boolean processCmd(Tagname forma, HttpServletRequest request)
			throws ApplicationExceptionChecked {
		// System.out.println("forma = " + forma.getCmd());
		boolean resp = false;
		StringBuilder mensaje = null;
		ResourceBundle rb = ToolsHTML.getBundle(request);

		if (forma.getCmd().equalsIgnoreCase(SuperActionForm.cmdInsert)) {
			// System.out.println("Insertar Registro...");
			try {
				TagNameDAO oTagNameDAO = new TagNameDAO();
				
				ArrayList lista = oTagNameDAO.listarByTagName(forma.getTagname().toString().trim());

				Iterator iter = lista.iterator();
				if (iter.hasNext()) {
					// ya existe en la bd
					// resp = false;
					request.setAttribute("info", rb.getString("reg.existe")
							+ ": " + forma.getTagname());
					return true;
				} else {
					TagNameTO oTagNameTO = new TagNameTO();

					oTagNameTO.setIdTagName2(null);
					oTagNameTO.setActive(Constants.permissionSt);
					
					oTagNameDAO.insertar(oTagNameTO);
					
					// resp = true;
					request.setAttribute("info", rb.getString("app.editOk"));
					return true;
				}

			} catch (Exception e) {
				e.printStackTrace();
			}

		} else {
			if (forma.getCmd().equalsIgnoreCase(SuperActionForm.cmdEdit)) {
				// System.out.println("Editando Registro...");
				// forma.setActive(Constants.permission);
				// forma.setActive(Constants.permission);
				// Session session = HibernateUtil.getSession();

				TagNameDAO oTagNameDAO = new TagNameDAO();
				
				ArrayList lista;
				try {
					lista = oTagNameDAO.listarByTagNameDiferentId(forma.getTagname().toString().trim(), String.valueOf(forma.getIdtagname2()));
					
					Iterator iter = lista.iterator();
					if (iter.hasNext()) {
						request.setAttribute("info", rb.getString("reg.existe")
								+ ": " + forma.getTagname());
						return true;
					} else {
						TagNameTO oTagNameTO = new TagNameTO();
						
						oTagNameTO.setIdTagName2(String.valueOf(forma.getIdtagname2()));
						oTagNameDAO.eliminar(oTagNameTO);
						
						request.setAttribute("info", rb.getString("app.editOk"));
						return true;
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else {
				if (forma.getCmd().equalsIgnoreCase(SuperActionForm.cmdDelete)) {
					try {
						TagNameDAO oTagNameDAO = new TagNameDAO();
						TagNameTO oTagNameTO = new TagNameTO();
						
						oTagNameTO.setIdTagName2(String.valueOf(forma.getIdtagname2()));
						oTagNameDAO.eliminar(oTagNameTO);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}
		if (mensaje != null) {
			// //System.out.println("mensaje = " + mensaje);
			request.setAttribute("info", mensaje.toString());
		}
		return false;
	}

}
