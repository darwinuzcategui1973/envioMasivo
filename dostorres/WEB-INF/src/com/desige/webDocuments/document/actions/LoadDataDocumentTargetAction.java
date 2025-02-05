package com.desige.webDocuments.document.actions;

import java.io.File;
import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.desige.webDocuments.document.forms.BaseDocumentForm;
import com.desige.webDocuments.persistent.managers.HandlerParameters;
import com.desige.webDocuments.persistent.managers.HandlerStruct;
import com.desige.webDocuments.utils.ApplicationExceptionChecked;
import com.desige.webDocuments.utils.ToolsHTML;
import com.desige.webDocuments.utils.beans.SuperAction;
import com.desige.webDocuments.utils.beans.Users;

public class LoadDataDocumentTargetAction extends SuperAction {
	private static Logger logger = LoggerFactory.getLogger(LoadDataDocumentTargetAction.class);

	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
		super.init(mapping, form, request, response);

		boolean isVisorNativo = false;
		String extensiones = null;

		Users usuario = null;
		try {
			usuario = getUserSession();
		} catch (ApplicationExceptionChecked e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		BaseDocumentForm forma = new BaseDocumentForm();

		forma.setIdDocument(request.getParameter("idDocument"));
		forma.setNumberGen(request.getParameter("idDocument"));
		forma.setNumVer(Integer.parseInt(request.getParameter("idVersion")));

		try {
			HandlerStruct.loadDocument(forma, request.getParameter("downFile") != null, false, null, request);

			// pedimos las extensiones que seran visualizadas en el navegador
			isVisorNativo = HandlerParameters.isExtensionNativeViewer(null, forma.getNameFile());

			if (isVisorNativo) {
				// preparamos el directorio temporal para el documento
				System.out.println("Nativo-----///////------"+isVisorNativo );
				String tipoFile = "nativo";
				String tmpFilesDir = ToolsHTML.getPath().concat("tmp") + File.separator + usuario.getUser() + File.separator + tipoFile; // \\tmp
				System.out.println("Nativo-----///////------"+isVisorNativo+" ------- "+tmpFilesDir );
				new File(tmpFilesDir).mkdirs();
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		try {
			response.getWriter().write(String.valueOf(isVisorNativo));
		} catch (IOException e) {
			e.printStackTrace();
		}

		return null;
	}

}
