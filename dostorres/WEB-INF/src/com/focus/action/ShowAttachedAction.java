package com.focus.action;

import java.io.File;
import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.desige.webDocuments.parameters.actions.AdministracionAction;
import com.desige.webDocuments.utils.ToolsHTML;
import com.desige.webDocuments.utils.beans.SuperAction;

public class ShowAttachedAction extends SuperAction {
	private static Logger log = LoggerFactory.getLogger(AdministracionAction.class.getName());
	private static final String letras = "ABCDEFGHIJKLMNOPQRSTUVW";

	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {

		super.init(mapping, form, request, response);

		String prefijo = request.getParameter("prefijo");
		String numero = request.getParameter("numero");

		try {
			prefijo = prefijo.trim();
			if (numero != null && numero.equals("0")) {
				StringBuffer ruta = new StringBuffer(ToolsHTML.getAttachedFolder0());
				ruta.append("/").append(prefijo.substring(0, 4));
				ruta.append("/").append(prefijo).append(".doc");

				File file = new File(ruta.toString());
				System.out.println(ruta.toString());
				if (file.exists()) {
					request.setAttribute("archivo", ruta.toString());
					System.out.println("si existe el anexo");
					return mapping.findForward("success0");
				} else {
					// buscaremos un document docx
					ruta.setLength(0);
					ruta.append(ToolsHTML.getAttachedFolder0());
					ruta.append("/").append(prefijo.substring(0, 4));
					ruta.append("/").append(prefijo).append(".docx");

					file = new File(ruta.toString());
					System.out.println(ruta.toString());
					if (file.exists()) {
						System.out.println("si existe el anexo");
						request.setAttribute("archivo", ruta.toString());
						return mapping.findForward("success0");
					}
				}
			} else if (numero != null && numero.equals("1")) {
				StringBuffer base = new StringBuffer(ToolsHTML.getAttachedFolder1()).append("/").append(prefijo.substring(0, 4)).append("/").append(prefijo);
				StringBuffer ruta = new StringBuffer();
				File file = null;
				ArrayList<String> lista = new ArrayList<String>();

				for (int i = 0; i < 10; i++) {
					ruta.setLength(0);
					ruta.append(base).append(letras.charAt(i)).append(".jpg");
					file = new File(ruta.toString());
					if (file.exists()) {
						lista.add(ruta.toString());
					} else {
						ruta.setLength(0);
						ruta.append(base).append(letras.charAt(i)).append(".gif");
						file = new File(ruta.toString());
						if (file.exists()) {
							lista.add(ruta.toString());
						} else {
							break;
						}
					}
				}
				if (lista.size() > 0) {
					request.setAttribute("lista", lista);
					return mapping.findForward("success1");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return mapping.findForward("success");
	}

}
