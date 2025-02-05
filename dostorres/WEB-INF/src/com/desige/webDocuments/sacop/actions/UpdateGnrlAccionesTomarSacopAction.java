package com.desige.webDocuments.sacop.actions;

import java.io.File;
import java.io.FileOutputStream;

import javax.mail.Session;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transaction;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.desige.webDocuments.persistent.managers.HandlerProcesosSacop;
import com.desige.webDocuments.persistent.utils.HibernateUtil;
import com.desige.webDocuments.sacop.forms.Plantilla1BD;
import com.desige.webDocuments.sacop.forms.plantilla1;
import com.desige.webDocuments.utils.ApplicationExceptionChecked;
import com.desige.webDocuments.utils.beans.SuperAction;
import com.focus.qweb.dao.PlanillaSacop1DAO;
import com.focus.qweb.to.PlanillaSacop1TO;

/**
 * Created by IntelliJ IDEA. User: srodriguez Date: 10/03/2006 Time: 08:32:11 AM To change this template use File | Settings | File Templates.
 */
public class UpdateGnrlAccionesTomarSacopAction extends SuperAction {
	private static final Logger log = LoggerFactory.getLogger(UpdateGnrlAccionesTomarSacopAction.class);

	/**
	 * 
	 */
	public synchronized ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
		super.init(mapping, form, request, response);
		try {
			getUserSession();
			plantilla1 forma = (plantilla1) form;
			putObjectSession("idplanillasacop", forma.getIdplanillasacop1());

			PlanillaSacop1DAO oPlanillaSacop1DAO = new PlanillaSacop1DAO();
			PlanillaSacop1TO oPlanillaSacop1TO = new PlanillaSacop1TO();

			oPlanillaSacop1TO.setIdplanillasacop1(forma.getIdplanillasacop1());
			oPlanillaSacop1DAO.cargar(oPlanillaSacop1TO);

			oPlanillaSacop1TO.setAccionobservacion(forma.getAccionobservacion());
			oPlanillaSacop1TO.setDescripcionAccionPrincipal(forma.getDescripcionAccionPrincipal());
			oPlanillaSacop1TO.setCausasnoconformidad(forma.getCausasnoconformidad());

			Plantilla1BD formaBD = new Plantilla1BD(oPlanillaSacop1TO);
			checkPreviousTechniqueFile(forma, formaBD);

			oPlanillaSacop1TO.setArchivoTecnica(formaBD.getArchivoTecnica());
			request.setAttribute("archivoTecnica", formaBD.getArchivoTecnica());

			oPlanillaSacop1TO.setIdDocumentAssociate(request.getParameter("idDocumentAssociate"));
			oPlanillaSacop1TO.setNumVerDocumentAssociate(request.getParameter("numVerDocumentAssociate"));
			oPlanillaSacop1TO.setNameDocumentAssociate(request.getParameter("nameDocumentAssociate"));
			
			oPlanillaSacop1TO.setRequireTracking(request.getParameter("requireTracking"));
			oPlanillaSacop1TO.setRequireTrackingDate(request.getParameter("requireTrackingDate"));
			
			oPlanillaSacop1DAO.actualizar(oPlanillaSacop1TO);

			return goSucces();
		} catch (ApplicationExceptionChecked ae) {
			ae.printStackTrace();
			return goError(ae.getKeyError());
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {

		}

		return goError();
	}

	/**
	 * 
	 * @param forma
	 * @param formaBD
	 */
	private void checkPreviousTechniqueFile(plantilla1 forma, Plantilla1BD formaBD) {
		log.info("Verificando archivo de tecnica para la sacop '" + forma.getIdplanillasacop1() + "'");

		// final String filePath = ToolsHTML.getPath()
		// + HandlerProcesosSacop.EVIDENCIAS_NAME_DIR
		// + File.separator
		// + forma.getIdplanillasacop1();
		final String folderPath = HandlerProcesosSacop.getSacopPlanillaBagFolder(forma.getIdplanillasacop1());
		File dir = new File(folderPath);
		if (!dir.exists()) {
			dir.mkdirs();
		}

		if (forma.isDeleteArchivoTecnica() || forma.getArchivoTecnica().getFileSize() > 0) {
			log.info("Debe actualizarce el valor del archivo de tecnica");
			formaBD.setArchivoTecnica("");

			File[] filesInDir = dir.listFiles();
			for (File file : filesInDir) {
				if (file.isFile()) {
					file.delete();
				}
			}

			if (forma.getArchivoTecnica().getFileSize() > 0) {
				try {
					File result = new File(dir.getAbsolutePath() + File.separator + forma.getArchivoTecnica().getFileName());
					FileOutputStream fos = new FileOutputStream(result);
					fos.write(forma.getArchivoTecnica().getFileData());
					fos.flush();
					fos.close();
					formaBD.setArchivoTecnica(forma.getArchivoTecnica().getFileName());
					log.info("Escrito archivo de tecnica " + result.getAbsolutePath());
				} catch (Exception e) {
					// TODO: handle exception
					log.error("Error creando archivo de tecnica en su ruta final. Error: " + e.getMessage(), e);
				}
			}
		} else {
			log.info("Se deja el valor original para el archivo de tecnica");
		}
	}
}
