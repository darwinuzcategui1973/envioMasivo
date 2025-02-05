package com.desige.webDocuments.sacop.actions;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

import javax.activation.MimetypesFileTypeMap;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.desige.webDocuments.persistent.managers.HandlerProcesosSacop;
import com.desige.webDocuments.sacop.forms.Plantilla1BD;
import com.desige.webDocuments.utils.ApplicationExceptionChecked;
import com.desige.webDocuments.utils.ToolsHTML;
import com.desige.webDocuments.utils.beans.SuperAction;
import com.focus.qweb.dao.PlanillaSacop1DAO;
import com.focus.qweb.to.PlanillaSacop1TO;

/**
 * Created by IntelliJ IDEA. User: srodriguez Date: 30/05/2006 Time: 03:36:18 PM To change this template use File | Settings | File Templates.
 */

public class showDocumentSacop extends SuperAction {

	private static ArrayList elementos = ToolsHTML.getProperties("visor.OpenBrowser");

	/**
	 * 
	 */
	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
		super.init(mapping, form, request, response);

		String id = request.getParameter("idplanillasacop1");

		// Chequea si se desea mostrar un archivo en la raiz o en el BAG
		int pos = id==null ? -1 : id.indexOf("/");
		if (pos != -1) {
			// Archivo en el bag
			processFile(response, request, id, pos);
			return null;
		} else {

			try {
				PlanillaSacop1DAO oPlanillaSacop1DAO = new PlanillaSacop1DAO();
				PlanillaSacop1TO oPlanillaSacop1TO = new PlanillaSacop1TO();

				
				if (id != null) {
					oPlanillaSacop1TO.setIdplanillasacop1(id);
					oPlanillaSacop1DAO.cargar(oPlanillaSacop1TO);

					String path = HandlerProcesosSacop.getSacopPlanillaFolder(id);

					File f = new File(path + File.separator + oPlanillaSacop1TO.getNameFile());
					if (f.exists()) {
						if (processFile(response, request, path, new Plantilla1BD(oPlanillaSacop1TO))) {
							// return goSucces();
							return null;
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

	}

	/**
	 * 
	 * @param nameFile
	 * @return
	 */
	private static String getExtensionFile(String nameFile) {
		return FilenameUtils.getExtension(nameFile).toUpperCase();
	}

	/**
	 * 
	 * @param in
	 * @param out
	 */
	private static void closeIOs(InputStream in, OutputStream out) {
		IOUtils.closeQuietly(in);
		IOUtils.closeQuietly(out);
	}

	/**
	 * 
	 * @param response
	 * @param request
	 * @param path
	 * @return
	 */
	public static synchronized boolean processFile(HttpServletResponse response, HttpServletRequest request, String path, int pos) {
		InputStream in = null;
		OutputStream out = null;

		try {
			StringBuilder sb = new StringBuilder(1024).append(HandlerProcesosSacop.getSacopPath()).append(File.separator).append(path.substring(0, pos))
					.append(File.separator);
			path = path.substring(pos + 1);
			sb.append("bag").append(File.separator).append(path);
			String fullPath = sb.toString();
			sb = null;

			String fileName = FilenameUtils.getName(fullPath);
			MimetypesFileTypeMap mimeTypesMap = new MimetypesFileTypeMap();
			String contentType = mimeTypesMap.getContentType(fileName);
			response.setContentType(contentType);
			response.setHeader("content-disposition", "attachment; filename=\"" + fileName + "\"");
			response.setHeader("cache-control", "no-cache");

			in = new FileInputStream(new File(fullPath));
			out = response.getOutputStream();
			IOUtils.copy(in, out);
			out.flush();

			return true;
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			closeIOs(in, out);
		}
		return false;
	}

	/**
	 * 
	 * @param response
	 * @param request
	 * @param path
	 * @param forma
	 * @return
	 */
	public static synchronized boolean processFile(HttpServletResponse response, HttpServletRequest request, String path, Plantilla1BD forma) {
		InputStream in = null;
		OutputStream out = null;

		try {
			String extension = getExtensionFile(forma.getNameFile());

			String initServer = ToolsHTML.getServerPath(ToolsHTML.getServerName(request), request.getServerPort(), request.getContextPath());
			//String fileURL = initServer.concat(File.separator) + HandlerProcesosSacop.EVIDENCIAS_NAME_DIR.concat(File.separator) + forma.getNameFile();
			String fileURL = HandlerProcesosSacop.getSacopPath() + File.separator + forma.getNameFile();

			boolean isVisioDoc = elementos.contains(extension);
			boolean onlyRead = request.getParameter("downFile") == null ? true : false;

			// System.out.println("onlyRead = " + onlyRead);
			if (onlyRead) {
				response.setHeader("content-disposition", "inline;open; filename=\"" + forma.getNameFile() + "\"");
			} else {
				response.setHeader("content-disposition", "attachment; filename=\"" + forma.getNameFile() + "\"");
			}

			response.setHeader("content-transfer-encoding", "binary");
			response.setContentType(forma.getContentType());

			if ((!isVisioDoc) || (!onlyRead)) {
				File fichero = new File(path.concat(File.separator) + forma.getNameFile());
				in = new FileInputStream(fichero);
				out = response.getOutputStream();
				IOUtils.copy(in, out);
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
