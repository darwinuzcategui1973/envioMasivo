package com.desige.webDocuments.norms.actions;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.desige.webDocuments.norms.forms.BaseNormsForm;
import com.desige.webDocuments.persistent.managers.HandlerStruct;
import com.desige.webDocuments.utils.ApplicationExceptionChecked;
import com.desige.webDocuments.utils.ToolsHTML;
import com.desige.webDocuments.utils.beans.SuperAction;

/**
 * Created by IntelliJ IDEA. User: srodriguez Date: 01/09/2005 Time: 11:08:20 AM
 * To change this template use File | Settings | File Templates.
 */

public class showDocumentNorms extends SuperAction {

	private static ArrayList elementos = ToolsHTML
			.getProperties("visor.OpenBrowser");

	public ActionForward execute(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) {
		super.init(mapping, form, request, response);

		try {
			BaseNormsForm forma = new BaseNormsForm();
			String id = request.getParameter("idNorms");
			forma.setId(id);
			if (forma.getId() != null) {
				// SIMON 30 DE JUNIO 2005 INICIO
				// getUserSession();
				// SIMON 30 DE JUNIO 2005 FIN
				HandlerStruct.loadDocumentNorms(forma);
				// putObjectSession("showDocument",forma);
				String path = ToolsHTML.getPath().concat("tmp"); // \\tmp
				HandlerStruct.createFileNorms(forma, path);
				if (forma.getFileNameFisico() != null) {
					if (processFile(response, request, path, forma)) {
						return goSucces();
					}
				}
			} else {
				throw new ApplicationExceptionChecked("E0053");
			}
		} catch (ApplicationExceptionChecked ae) {
			ae.printStackTrace();
			return goError(ae.getKeyError());
		} catch (Exception e) {
			e.printStackTrace();
			return goError();
		}
		return goError();
	}

	private static String getExtensionFile(String nameFile) {
		int pos = nameFile.indexOf(".");
		String extension = nameFile.substring(pos).toUpperCase();
		return extension;
	}

	private static void closeIOs(InputStream in, OutputStream out) {
		if (in != null) {
			try {
				in.close();
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
		if (out != null) {
			try {
				out.close();
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}

	public static synchronized boolean processFile(
			HttpServletResponse response, HttpServletRequest request,
			String path, BaseNormsForm forma) {
		String extension = getExtensionFile(forma.getFileNameFisico());
		response.setContentType(forma.getContextType());
		InputStream in = null;
		OutputStream out = null;

		String initServer = ToolsHTML.getServerPath(
				ToolsHTML.getServerName(request), request.getServerPort(),
				request.getContextPath());
		String fileURL = initServer.concat(File.separator)
				+ "tmp".concat(File.separator) + forma.getFileNameFisico();
		boolean isVisioDoc = elementos.contains(extension);
		try {
			boolean onlyRead = request.getParameter("downFile") == null ? true
					: false;
			// System.out.println("onlyRead = " + onlyRead);
			if (onlyRead) {
				response.setHeader("content-disposition",
						"inline;open; filename=\"" + forma.getFileNameFisico()
								+ "\"");
			} else {
				response.setHeader("content-disposition",
						"attachment; filename=\"" + forma.getFileNameFisico()
								+ "\"");
			}
			response.setHeader("content-transfer-encoding", "binary");
			if ((!isVisioDoc) || (!onlyRead)) {
				File fichero = new File(path.concat(File.separator)
						+ forma.getFileNameFisico());
				in = new FileInputStream(fichero);
				out = response.getOutputStream();
				int bytesRead = 0;
				byte buffer[] = new byte[8192];
				while ((bytesRead = in.read(buffer, 0, 8192)) != -1) {
					out.write(buffer, 0, bytesRead);
					response.flushBuffer();
				}
				closeIOs(in, out);
				try {
					// System.out.println("DELETE");
					fichero.delete();
				} catch (Exception ex) {
					ex.printStackTrace();
				}
				return true;
			} else {
				response.setHeader("Cache-control", "no-cache");
				response.setHeader("Pragma", "no-cache");
				response.setDateHeader("Expires", 0);
				response.sendRedirect(fileURL);

			}
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			closeIOs(in, out);
		}
		return false;
	}

}
