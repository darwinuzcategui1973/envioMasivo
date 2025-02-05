package com.desige.webDocuments.record.actions;

import java.io.File;
import java.io.FileOutputStream;
import java.text.NumberFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.ResourceBundle;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.hssf.util.Region;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.desige.webDocuments.persistent.managers.HandlerDBUser;
import com.desige.webDocuments.persistent.managers.HandlerRecord;
import com.desige.webDocuments.record.forms.RecordFiltersForm;
import com.desige.webDocuments.utils.ApplicationExceptionChecked;
import com.desige.webDocuments.utils.ToolsHTML;
import com.desige.webDocuments.utils.beans.SuperAction;
import com.desige.webDocuments.utils.beans.UserRecord;
import com.desige.webDocuments.utils.beans.Users;

/**
 * Title: GenerateReportRecordAction.java<br>
 * Copyright: (c) 2007 Focus Consulting<br>
 * Company:Focus Consulting (CON)<br>
 * 
 * @author YSA <br>
 *         Changes:<br>
 *         <ul>
 *         <li>19/12/2007 (YSA) Creation</li>
 *         <ul>
 */

public class GenerateReportRecordAction extends SuperAction {

	/**
	 * 
	 */
	public ActionForward execute(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) {
		try {
			ResourceBundle rb = ToolsHTML.getBundle(request);
			super.init(mapping, form, request, response);
			Users usuario = getUserSession();
			// System.out.println("--- usuario: " + usuario.getIdPerson());
			RecordFiltersForm forma = (RecordFiltersForm) form;
			// HttpSession session = request.getSession();
			// //System.out.println("============== SESION1: " +
			// session.getId());
			String areasSel = request.getParameter("areasSel");
			String chargesSel = request.getParameter("chargesSel");
			String medianaGeneral = (String) request
					.getParameter("medianaGeneral");
			String recordPorUsuario = (String) request
					.getParameter("recordPorUsuario");
			String recordPromedio = (String) request
					.getParameter("recordPromedio");
			String desde = forma.getDesdeTime();
			String hasta = forma.getHastaTime();
			int intervalo = forma.getIntervalo();
			String usersSelected = request.getParameter("usersSelected");
			String indicatorsSelected = request
					.getParameter("indicatorsSelected");
			Vector vUsers = new Vector();
			Vector vUsersRecord = new Vector();
			int numUsers = 0;
			String areasTxt = "";
			String cargosTxt = "";

			// *****indicatorsSelect
			int docsCreatedSel = 0;
			int docsExpiredSel = 0;
			int docsObsoleteSel = 0;
			int draftsExpiredSel = 0;
			int workflowsApprovalSel = 0;
			int workflowsApprovalPendingSel = 0;
			int workflowsApprovalApprovedSel = 0;
			int workflowsApprovalCanceledSel = 0;
			int workflowsApprovalExpiredSel = 0;
			int workflowsReviewSel = 0;
			int workflowsReviewPendingSel = 0;
			int workflowsReviewApprovedSel = 0;
			int workflowsReviewCanceledSel = 0;
			int workflowsReviewExpiredSel = 0;
			int ftpSel = 0;
			int ftpPendingSel = 0;
			int ftpApprovedSel = 0;
			int ftpCanceledSel = 0;
			int ftpExpiredSel = 0;
			int ftpCompletedSel = 0;
			int sacopsCreatedSel = 0;
			int sacopsIsResponsibleSel = 0;
			int sacopsParticipateSel = 0;
			int sacopsClosedSel = 0;
			int printingSel = 0;
			int printingApprovedSel = 0;
			int printingRejectedSel = 0;
			int printingPendingSel = 0;

			// ********* Mediana General
			double docsCreatedMG = 0.0;
			double docsExpiredMG = 0.0;
			double docsObsoleteMG = 0.0;
			double draftsExpiredMG = 0.0;
			double workflowsApprovalMG = 0.0;
			double workflowsApprovalPendingMG = 0.0;
			double workflowsApprovalApprovedMG = 0.0;
			double workflowsApprovalCanceledMG = 0.0;
			double workflowsApprovalExpiredMG = 0.0;
			double workflowsReviewMG = 0.0;
			double workflowsReviewPendingMG = 0.0;
			double workflowsReviewApprovedMG = 0.0;
			double workflowsReviewCanceledMG = 0.0;
			double workflowsReviewExpiredMG = 0.0;
			double ftpMG = 0.0;
			double ftpPendingMG = 0.0;
			double ftpApprovedMG = 0.0;
			double ftpCanceledMG = 0.0;
			double ftpExpiredMG = 0.0;
			double ftpCompletedMG = 0.0;
			double sacopsCreatedMG = 0.0;
			double sacopsIsResponsibleMG = 0.0;
			double sacopsParticipateMG = 0.0;
			double sacopsClosedMG = 0.0;
			double printingMG = 0.0;
			double printingApprovedMG = 0.0;
			double printingRejectedMG = 0.0;
			double printingPendingMG = 0.0;

			// ******** Record Promedio
			double docsCreatedRP = 0.0;
			double docsExpiredRP = 0.0;
			double docsObsoleteRP = 0.0;
			double draftsExpiredRP = 0.0;
			double workflowsApprovalRP = 0.0;
			double workflowsApprovalPendingRP = 0.0;
			double workflowsApprovalApprovedRP = 0.0;
			double workflowsApprovalCanceledRP = 0.0;
			double workflowsApprovalExpiredRP = 0.0;
			double workflowsReviewRP = 0.0;
			double workflowsReviewPendingRP = 0.0;
			double workflowsReviewApprovedRP = 0.0;
			double workflowsReviewCanceledRP = 0.0;
			double workflowsReviewExpiredRP = 0.0;
			double ftpRP = 0.0;
			double ftpPendingRP = 0.0;
			double ftpApprovedRP = 0.0;
			double ftpCanceledRP = 0.0;
			double ftpExpiredRP = 0.0;
			double ftpCompletedRP = 0.0;
			double sacopsCreatedRP = 0.0;
			double sacopsIsResponsibleRP = 0.0;
			double sacopsParticipateRP = 0.0;
			double sacopsClosedRP = 0.0;
			double printingRP = 0.0;
			double printingApprovedRP = 0.0;
			double printingRejectedRP = 0.0;
			double printingPendingRP = 0.0;

			int cols = 4;

			String sAreas = rb.getString("lista.todas");
			String sCharges = rb.getString("lista.todos");

			NumberFormat nf = NumberFormat.getInstance();
			nf.setMaximumFractionDigits(2);
			nf.setMinimumFractionDigits(2);

			// System.out.println("++++ areasSel: " + areasSel);
			// System.out.println("++++ chargesSel: " + chargesSel);
			// System.out.println("++++ indicatorsSelected: " +
			// indicatorsSelected);
			// System.out.println("++++ usersSelected: " + usersSelected);
			// System.out.println("++++ getDesdeTime: " + desde);
			// System.out.println("++++ getHastaTime: " + hasta);
			// System.out.println("++++ getIntervalo: " + intervalo);
			// System.out.println("++++ getMedianaGeneral: " + medianaGeneral);
			// System.out.println("++++ getRecordPorUsuario: " +
			// recordPorUsuario);
			// System.out.println("++++ getRecordPromedio: " + recordPromedio);

			if (!ToolsHTML.isEmptyOrNull(areasSel)) {
				sAreas = HandlerDBUser.getAreasTxt(areasSel);
			}

			if (!ToolsHTML.isEmptyOrNull(chargesSel)) {
				sCharges = HandlerDBUser.getChargesTxt(chargesSel);
			}

			// Se consultan todos los usuarios que coinciden con áreas y/o
			// cargos
			// Si hay usuarios seleccionados no tomar en cuenta cargos ni áreas
			if (ToolsHTML.isEmptyOrNull(usersSelected)) {
				// Si no hay usuarios
				if (!ToolsHTML.isEmptyOrNull(chargesSel)) {
					// Si hay cargos NO tomar en cuenta áreas. Se consultan
					// usuarios que coincidan con los cargos
					vUsers = HandlerDBUser.getUsers(null, chargesSel, null, 2);
				} else {
					// Si no hay cargos
					if (!ToolsHTML.isEmptyOrNull(areasSel)) {
						// Solo tomar en cuenta áreas
						vUsers = HandlerDBUser
								.getUsers(areasSel, null, null, 2);
					} else {
						// Si no hay áreas buscar todos los usuarios activos
						vUsers = HandlerDBUser.getUsers(null, null, null, 2);
					}
				}

				/*
				 * usersSelected = ""; if(vUsers!=null && vUsers.size()>0){ for
				 * (int i = 0; i < vUsers.size(); i++) { Users usr = (Users)
				 * vUsers.elementAt(i); usersSelected+=usr.getIdPerson()+","; }
				 * if(!ToolsHTML.isEmptyOrNull(usersSelected) &&
				 * usersSelected.length()>1){ usersSelected =
				 * usersSelected.substring(0,usersSelected.length()-1); } }
				 */
			} else {
				// Se ordenan usuarios por area, cargo, apellido, nombre
				vUsers = HandlerDBUser.getUsers(null, null, usersSelected, 2);
			}

			// //System.out.println("------ usersSelected: " + usersSelected);

			// Se genera reporte. Ciclo por usuarios
			if (vUsers != null && vUsers.size() > 0) {
				java.util.Calendar cDesde = new java.util.GregorianCalendar();
				/*
				 * if(!ToolsHTML.isEmptyOrNull(desde)){ desde += " 00:00:00"; }
				 * if(!ToolsHTML.isEmptyOrNull(hasta)){ hasta += " 23:59:59"; }
				 */

				if (intervalo == 1) {
					// Última Semana
					cDesde.add(Calendar.DAY_OF_MONTH, -7);
				}
				if (intervalo == 2) {
					// Último Mes
					cDesde.add(Calendar.MONTH, -1);
				}
				if (intervalo == 3) {
					// Último Año
					cDesde.add(Calendar.YEAR, -1);
				}

				if (intervalo == 1 || intervalo == 2 || intervalo == 3) {
					desde = ToolsHTML.date.format(cDesde.getTime());
					hasta = ToolsHTML.date.format(new Date());
				}

				if (ToolsHTML.isEmptyOrNull(hasta)) {
					hasta = ToolsHTML.date.format(new Date());
				}

				if (!ToolsHTML.isEmptyOrNull(desde)) {
					desde += " 00:00:00";
				}
				if (!ToolsHTML.isEmptyOrNull(hasta)) {
					hasta += " 23:59:59";
				}
				// System.out.println("---- desde: " + desde);
				// System.out.println("---- hasta: " + hasta);

				int numUsersRecord = vUsers.size();
				// System.out.println("--- numUsersRecord: " + numUsersRecord);

				Vector vUsersTotal = HandlerDBUser
						.getUsers(null, null, null, 2);
				int numUsersTotal = 1;
				if (vUsersTotal != null && vUsersTotal.size() > 0)
					numUsersTotal = vUsersTotal.size();
				// System.out.println("--- numUsersTotal: " + numUsersTotal);

				for (int i = 0; i < vUsers.size(); i++) {
					numUsers++;
					Users usr = (Users) vUsers.elementAt(i);
					UserRecord usrRecord = new UserRecord();

					// Número de documentos revisados
					// int docsReviewed = 0;

					// Número de documentos aprobados
					// int docsApproved = 0;

					// Número de documentos creados
					int docsCreated = 0;

					// Número de documentos expirados
					int docsExpired = 0;

					// Número de documentos obsoletos
					int docsObsolete = 0;

					// Número de borradores expirados
					int draftsExpired = 0;

					// Número de flujos de trabajo de aprobación emitidos
					int workflowsApproval = 0;

					// Flujos de trabajo de aprobación pendientes
					int workflowsApprovalPending = 0;

					// Flujos de trabajo de aprobación aprobados
					int workflowsApprovalApproved = 0;

					// Flujos de trabajo de aprobación cancelados
					int workflowsApprovalCanceled = 0;

					// Flujos de trabajo de aprobación expirados
					int workflowsApprovalExpired = 0;

					// Número de flujos de trabajo de revisión emitidos
					int workflowsReview = 0;

					// Flujos de trabajo de revisión pendientes
					int workflowsReviewPending = 0;

					// Flujos de trabajo de revisión aprobados
					int workflowsReviewApproved = 0;

					// Flujos de trabajo de revisión cancelados
					int workflowsReviewCanceled = 0;

					// Flujos de trabajo de revisión expirados
					int workflowsReviewExpired = 0;

					// Número de flujos de trabajo de paramétricos emitidos
					int ftp = 0;

					// Flujos de trabajo paramétricos pendientes
					int ftpPending = 0;

					// Flujos de trabajo paramétricos aprobados
					int ftpApproved = 0;

					// Flujos de trabajo paramétricos cancelados
					int ftpCanceled = 0;

					// Flujos de trabajo paramétricos expirados
					int ftpExpired = 0;

					// Flujos de trabajo paramétricos completados
					int ftpCompleted = 0;

					// Número de SACOPs creadas
					int sacopsCreated = 0;

					// Número de SACOPs en las que es responsable
					int sacopsIsResponsible = 0;

					// Número de SACOPs en las que paticipa
					int sacopsParticipate = 0;

					// Número de SACOPs que cerró con éxito
					int sacopsClosed = 0;

					// Número de solicitudes de Impresión realizadas
					int printing = 0;

					// Número de solicitudes de Impresión pendientes para que le
					// aprueben
					// int printingPendingTo = 0;

					// Número de solicitudes de Impresión que le aprobaron
					// int printingApprovedTo = 0;

					// Número de solicitudes de Impresión que le rechazaron
					// int printingRejectedTo = 0;

					// Número de solicitudes de Impresión aprobadas
					int printingApproved = 0;

					// Número de solicitudes de Impresión rechazadas
					int printingRejected = 0;

					// Número de solicitudes de Impresión pendientes por aprobar
					int printingPending = 0;

					StringTokenizer tokens = new StringTokenizer(
							indicatorsSelected, ",");
					// System.out.println("--- usuario : " + usr.getNameUser());
					while (tokens.hasMoreTokens()) {
						String indicator = tokens.nextToken();
						if ("33".equals(indicator)) {
							docsCreatedSel = 1;
							docsCreated = HandlerRecord.getNumDocsCreated(
									usr.getNameUser(), desde, hasta);
							usrRecord.setDocsCreated(docsCreated);
							docsCreatedRP += docsCreated;
							// //System.out.println("--- docsCreated : " +
							// docsCreated);
						}

						if ("3".equals(indicator)) {
							docsExpiredSel = 1;
							docsExpired = HandlerRecord.getNumDocsExpired(
									usr.getIdPerson(), desde, hasta);
							usrRecord.setDocsExpired(docsExpired);
							docsExpiredRP += docsExpired;
							// //System.out.println("--- docsExpired : " +
							// docsExpired);
						}

						if ("4".equals(indicator)) {
							docsObsoleteSel = 1;
							docsObsolete = HandlerRecord.getNumDocsObsolete(
									usr.getIdPerson(), desde, hasta);
							usrRecord.setDocsObsolete(docsObsolete);
							docsObsoleteRP += docsObsolete;
							// //System.out.println("--- docsObsolete : " +
							// docsObsolete);
						}

						if ("5".equals(indicator)) {
							draftsExpiredSel = 1;
							draftsExpired = HandlerRecord.getNumDraftsExpired(
									usr.getIdPerson(), desde, hasta);
							usrRecord.setDraftsExpired(draftsExpired);
							draftsExpiredRP += draftsExpired;
							// //System.out.println("--- draftsExpired : " +
							// draftsExpired);
						}

						if ("6".equals(indicator)) {
							workflowsApprovalSel = 1;
							workflowsApproval = HandlerRecord
									.getNumWorkflowsApproval(usr.getNameUser(),
											desde, hasta);
							usrRecord.setWorkflowsApproval(workflowsApproval);
							workflowsApprovalRP += workflowsApproval;
							// //System.out.println("--- workflowsApproval : " +
							// workflowsApproval);
						}

						if ("7".equals(indicator)) {
							workflowsApprovalPendingSel = 1;
							workflowsApprovalPending = HandlerRecord
									.getNumWorkflowsApprovalPending(
											usr.getNameUser(), desde, hasta);
							usrRecord
									.setWorkflowsApprovalPending(workflowsApprovalPending);
							workflowsApprovalPendingRP += workflowsApprovalPending;
							// //System.out.println("--- workflowsApprovalPending : "
							// + workflowsApprovalPending);
						}

						if ("8".equals(indicator)) {
							workflowsApprovalApprovedSel = 1;
							workflowsApprovalApproved = HandlerRecord
									.getNumWorkflowsApprovalApproved(
											usr.getNameUser(), desde, hasta);
							usrRecord
									.setWorkflowsApprovalApproved(workflowsApprovalApproved);
							workflowsApprovalApprovedRP += workflowsApprovalApproved;
							// //System.out.println("--- workflowsApprovalApproved : "
							// + workflowsApprovalApproved);
						}

						if ("9".equals(indicator)) {
							workflowsApprovalCanceledSel = 1;
							workflowsApprovalCanceled = HandlerRecord
									.getNumWorkflowsApprovalCanceled(
											usr.getNameUser(), desde, hasta);
							usrRecord
									.setWorkflowsApprovalCanceled(workflowsApprovalCanceled);
							workflowsApprovalCanceledRP += workflowsApprovalCanceled;
							// //System.out.println("--- workflowsApprovalCanceled : "
							// + workflowsApprovalCanceled);
						}

						if ("10".equals(indicator)) {
							workflowsApprovalExpiredSel = 1;
							workflowsApprovalExpired = HandlerRecord
									.getNumWorkflowsApprovalExpired(
											usr.getNameUser(), desde, hasta);
							usrRecord
									.setWorkflowsApprovalExpired(workflowsApprovalExpired);
							workflowsApprovalExpiredRP += workflowsApprovalExpired;
							// //System.out.println("--- workflowsApprovalExpired : "
							// + workflowsApprovalExpired);
						}

						if ("11".equals(indicator)) {
							workflowsReviewSel = 1;
							workflowsReview = HandlerRecord
									.getNumWorkflowsReview(usr.getNameUser(),
											desde, hasta);
							usrRecord.setWorkflowsReview(workflowsReview);
							workflowsReviewRP += workflowsReview;
							// //System.out.println("--- workflowsReview : " +
							// workflowsReview);
						}

						if ("12".equals(indicator)) {
							workflowsReviewPendingSel = 1;
							workflowsReviewPending = HandlerRecord
									.getNumWorkflowsReviewPending(
											usr.getNameUser(), desde, hasta);
							usrRecord
									.setWorkflowsReviewPending(workflowsReviewPending);
							workflowsReviewPendingRP += workflowsReviewPending;
							// //System.out.println("--- workflowsReviewPending : "
							// + workflowsReviewPending);
						}

						if ("13".equals(indicator)) {
							workflowsReviewApprovedSel = 1;
							workflowsReviewApproved = HandlerRecord
									.getNumWorkflowsReviewApproved(
											usr.getNameUser(), desde, hasta);
							usrRecord
									.setWorkflowsReviewApproved(workflowsReviewApproved);
							workflowsReviewApprovedRP += workflowsReviewApproved;
							// //System.out.println("--- workflowsReviewApproved : "
							// + workflowsReviewApproved);
						}

						if ("14".equals(indicator)) {
							workflowsReviewCanceledSel = 1;
							workflowsReviewCanceled = HandlerRecord
									.getNumWorkflowsReviewCanceled(
											usr.getNameUser(), desde, hasta);
							usrRecord
									.setWorkflowsReviewCanceled(workflowsReviewCanceled);
							workflowsReviewCanceledRP += workflowsReviewCanceled;
							// //System.out.println("--- workflowsReviewCanceled : "
							// + workflowsReviewCanceled);
						}

						if ("15".equals(indicator)) {
							workflowsReviewExpiredSel = 1;
							workflowsReviewExpired = HandlerRecord
									.getNumWorkflowsReviewExpired(
											usr.getNameUser(), desde, hasta);
							usrRecord
									.setWorkflowsReviewExpired(workflowsReviewExpired);
							workflowsReviewExpiredRP += workflowsReviewExpired;
							// //System.out.println("--- workflowsReviewExpired : "
							// + workflowsReviewExpired);
						}

						if ("16".equals(indicator)) {
							ftpSel = 1;
							ftp = HandlerRecord.getNumFtp(usr.getNameUser(),
									desde, hasta);
							usrRecord.setFtp(ftp);
							ftpRP += ftp;
							// //System.out.println("--- ftp : " + ftp);
						}

						if ("17".equals(indicator)) {
							ftpPendingSel = 1;
							ftpPending = HandlerRecord.getNumFtpPending(
									usr.getNameUser(), desde, hasta);
							usrRecord.setFtpPending(ftpPending);
							ftpPendingRP += ftpPending;
							// //System.out.println("--- ftpPending : " +
							// ftpPending);
						}

						if ("18".equals(indicator)) {
							ftpApprovedSel = 1;
							ftpApproved = HandlerRecord.getNumFtpApproved(
									usr.getNameUser(), desde, hasta);
							usrRecord.setFtpApproved(ftpApproved);
							ftpApprovedRP += ftpApproved;
							// //System.out.println("--- ftpApproved : " +
							// ftpApproved);
						}

						if ("19".equals(indicator)) {
							ftpCanceledSel = 1;
							ftpCanceled = HandlerRecord.getNumFtpCanceled(
									usr.getNameUser(), desde, hasta);
							usrRecord.setFtpCanceled(ftpCanceled);
							ftpCanceledRP += ftpCanceled;
							// //System.out.println("--- ftpCanceled : " +
							// ftpCanceled);
						}

						if ("20".equals(indicator)) {
							ftpExpiredSel = 1;
							ftpExpired = HandlerRecord.getNumFtpExpired(
									usr.getNameUser(), desde, hasta);
							usrRecord.setFtpExpired(ftpExpired);
							ftpExpiredRP += ftpExpired;
							// //System.out.println("--- ftpExpired : " +
							// ftpExpired);
						}

						if ("21".equals(indicator)) {
							ftpCompletedSel = 1;
							ftpCompleted = HandlerRecord.getNumFtpCompleted(
									usr.getNameUser(), desde, hasta);
							usrRecord.setFtpCompleted(ftpCompleted);
							ftpCompletedRP += ftpCompleted;
							// //System.out.println("--- ftpCompleted : " +
							// ftpCompleted);
						}

						if ("22".equals(indicator)) {
							sacopsCreatedSel = 1;
							sacopsCreated = HandlerRecord.getNumSacopsCreated(
									usr.getIdPerson(), desde, hasta);
							usrRecord.setSacopsCreated(sacopsCreated);
							sacopsCreatedRP += sacopsCreated;
							// //System.out.println("--- sacopsCreated : " +
							// sacopsCreated);
						}

						if ("23".equals(indicator)) {
							sacopsIsResponsibleSel = 1;
							sacopsIsResponsible = HandlerRecord
									.getNumSacopsIsResponsible(
											usr.getIdPerson(), desde, hasta);
							usrRecord
									.setSacopsIsResponsible(sacopsIsResponsible);
							sacopsIsResponsibleRP += sacopsIsResponsible;
							// //System.out.println("--- sacopsIsResponsible : "
							// + sacopsIsResponsible);
						}

						if ("24".equals(indicator)) {
							sacopsParticipateSel = 1;
							sacopsParticipate = HandlerRecord
									.getNumSacopsParticipate(usr.getIdPerson(),
											desde, hasta);
							usrRecord.setSacopsParticipate(sacopsParticipate);
							sacopsParticipateRP += sacopsParticipate;
							// //System.out.println("--- sacopsParticipate : " +
							// sacopsParticipate);
						}

						if ("25".equals(indicator)) {
							sacopsClosedSel = 1;
							sacopsClosed = HandlerRecord.getNumSacopsClosed(
									usr.getIdPerson(), desde, hasta);
							usrRecord.setSacopsClosed(sacopsClosed);
							sacopsClosedRP += sacopsClosed;
							// //System.out.println("--- sacopsClosed : " +
							// sacopsClosed);
						}

						if ("26".equals(indicator)) {
							printingSel = 1;
							printing = HandlerRecord.getNumPrinting(
									usr.getNameUser(), desde, hasta);
							usrRecord.setPrinting(printing);
							printingRP += printing;
							// //System.out.println("--- printing : " +
							// printing);
						}

						if ("30".equals(indicator)) {
							printingApprovedSel = 1;
							printingApproved = HandlerRecord
									.getNumPrintingApproved(usr.getNameUser(),
											desde, hasta);
							usrRecord.setPrintingApproved(printingApproved);
							printingApprovedRP += printingApproved;
							// //System.out.println("--- printingApproved : " +
							// printingApproved);
						}

						if ("31".equals(indicator)) {
							printingRejectedSel = 1;
							printingRejected = HandlerRecord
									.getNumPrintingRejected(usr.getNameUser(),
											desde, hasta);
							usrRecord.setPrintingRejected(printingRejected);
							printingRejectedRP += printingRejected;
							// //System.out.println("--- printingRejected : " +
							// printingRejected);
						}

						if ("32".equals(indicator)) {
							printingPendingSel = 1;
							printingPending = HandlerRecord
									.getNumPrintingPending(usr.getNameUser(),
											desde, hasta);
							usrRecord.setPrintingPending(printingPending);
							printingPendingRP += printingPending;
							// //System.out.println("--- printingPending : " +
							// printingPending);
						}

					} // end while tokens

					usr.setUserRecord(usrRecord);
					vUsersRecord.add(usr);

				} // end for

				if (1 == docsCreatedSel) {
					cols++;
					if ("1".equals(medianaGeneral)) {
						double iDocsCreatedMG = HandlerRecord
								.getNumDocsCreated(null, desde, hasta);
						docsCreatedMG = iDocsCreatedMG / numUsersTotal;
					}
					docsCreatedRP = docsCreatedRP / numUsersRecord;
				}

				if (1 == docsExpiredSel) {
					cols++;
					if ("1".equals(medianaGeneral)) {
						double iDocsExpired = HandlerRecord.getNumDocsExpired(
								0, desde, hasta);
						docsExpiredMG = iDocsExpired / numUsersTotal;
					}
					docsExpiredRP = docsExpiredRP / numUsersRecord;
				}

				if (1 == docsObsoleteSel) {
					cols++;
					if ("1".equals(medianaGeneral)) {
						double iDocsObsolete = HandlerRecord
								.getNumDocsObsolete(0, desde, hasta);
						docsObsoleteMG = iDocsObsolete / numUsersTotal;
					}
					docsObsoleteRP = docsObsoleteRP / numUsersRecord;
				}

				if (1 == draftsExpiredSel) {
					cols++;
					if ("1".equals(medianaGeneral)) {
						double iDraftsExpired = HandlerRecord
								.getNumDraftsExpired(0, desde, hasta);
						draftsExpiredMG = iDraftsExpired / numUsersTotal;
					}
					draftsExpiredRP = draftsExpiredRP / numUsersRecord;
				}

				if (1 == workflowsApprovalSel) {
					cols++;
					if ("1".equals(medianaGeneral)) {
						double iWorkflowsApproval = HandlerRecord
								.getNumWorkflowsApproval(null, desde, hasta);
						workflowsApprovalMG = iWorkflowsApproval
								/ numUsersTotal;
					}
					workflowsApprovalRP = workflowsApprovalRP / numUsersRecord;
				}

				if (1 == workflowsApprovalPendingSel) {
					cols++;
					if ("1".equals(medianaGeneral)) {
						double iWorkflowsApprovalPending = HandlerRecord
								.getNumWorkflowsApprovalPending(null, desde,
										hasta);
						workflowsApprovalPendingMG = iWorkflowsApprovalPending
								/ numUsersTotal;
					}
					workflowsApprovalPendingRP = workflowsApprovalPendingRP
							/ numUsersRecord;
				}

				if (1 == workflowsApprovalApprovedSel) {
					cols++;
					if ("1".equals(medianaGeneral)) {
						double iWorkflowsApprovalApproved = HandlerRecord
								.getNumWorkflowsApprovalApproved(null, desde,
										hasta);
						workflowsApprovalApprovedMG = iWorkflowsApprovalApproved
								/ numUsersTotal;
					}
					workflowsApprovalApprovedRP = workflowsApprovalApprovedRP
							/ numUsersRecord;
				}

				if (1 == workflowsApprovalCanceledSel) {
					cols++;
					if ("1".equals(medianaGeneral)) {
						double iWorkflowsApprovalCanceled = HandlerRecord
								.getNumWorkflowsApprovalCanceled(null, desde,
										hasta);
						workflowsApprovalCanceledMG = iWorkflowsApprovalCanceled
								/ numUsersTotal;
					}
					workflowsApprovalCanceledRP = workflowsApprovalCanceledRP
							/ numUsersRecord;
				}

				if (1 == workflowsApprovalExpiredSel) {
					cols++;
					if ("1".equals(medianaGeneral)) {
						double iWorkflowsApprovalExpired = HandlerRecord
								.getNumWorkflowsApprovalExpired(null, desde,
										hasta);
						workflowsApprovalExpiredMG = iWorkflowsApprovalExpired
								/ numUsersTotal;
					}
					workflowsApprovalExpiredRP = workflowsApprovalExpiredRP
							/ numUsersRecord;
				}

				if (1 == workflowsReviewSel) {
					cols++;
					if ("1".equals(medianaGeneral)) {
						double iWorkflowsReview = HandlerRecord
								.getNumWorkflowsReview(null, desde, hasta);
						workflowsReviewMG = iWorkflowsReview / numUsersTotal;
					}
					workflowsReviewRP = workflowsReviewRP / numUsersRecord;
				}

				if (1 == workflowsReviewPendingSel) {
					cols++;
					if ("1".equals(medianaGeneral)) {
						double iWorkflowsReviewPending = HandlerRecord
								.getNumWorkflowsReviewPending(null, desde,
										hasta);
						workflowsReviewPendingMG = iWorkflowsReviewPending
								/ numUsersTotal;
					}
					workflowsReviewPendingRP = workflowsReviewPendingRP
							/ numUsersRecord;
				}

				if (1 == workflowsReviewApprovedSel) {
					cols++;
					if ("1".equals(medianaGeneral)) {
						double iWorkflowsReviewApproved = HandlerRecord
								.getNumWorkflowsReviewApproved(null, desde,
										hasta);
						workflowsReviewApprovedMG = iWorkflowsReviewApproved
								/ numUsersTotal;
					}
					workflowsReviewApprovedRP = workflowsReviewApprovedRP
							/ numUsersRecord;
				}

				if (1 == workflowsReviewCanceledSel) {
					cols++;
					if ("1".equals(medianaGeneral)) {
						double iWorkflowsReviewCanceled = HandlerRecord
								.getNumWorkflowsReviewCanceled(null, desde,
										hasta);
						workflowsReviewCanceledMG = iWorkflowsReviewCanceled
								/ numUsersTotal;
					}
					workflowsReviewCanceledRP = workflowsReviewCanceledRP
							/ numUsersRecord;
				}

				if (1 == workflowsReviewExpiredSel) {
					cols++;
					if ("1".equals(medianaGeneral)) {
						double iWorkflowsReviewExpired = HandlerRecord
								.getNumWorkflowsReviewExpired(null, desde,
										hasta);
						workflowsReviewExpiredMG = iWorkflowsReviewExpired
								/ numUsersTotal;
					}
					workflowsReviewExpiredRP = workflowsReviewExpiredRP
							/ numUsersRecord;
				}

				if (1 == ftpSel) {
					cols++;
					if ("1".equals(medianaGeneral)) {
						double iFtp = HandlerRecord.getNumFtp(null, desde,
								hasta);
						ftpMG = iFtp / numUsersTotal;
					}
					ftpRP = ftpRP / numUsersRecord;
				}

				if (1 == ftpPendingSel) {
					cols++;
					if ("1".equals(medianaGeneral)) {
						double iFtpPending = HandlerRecord.getNumFtpPending(
								null, desde, hasta);
						ftpPendingMG = iFtpPending / numUsersTotal;
					}
					ftpPendingRP = ftpPendingRP / numUsersRecord;
				}

				if (1 == ftpApprovedSel) {
					cols++;
					if ("1".equals(medianaGeneral)) {
						double iFtpApproved = HandlerRecord.getNumFtpApproved(
								null, desde, hasta);
						ftpApprovedMG = iFtpApproved / numUsersTotal;
					}
					ftpApprovedRP = ftpApprovedRP / numUsersRecord;
				}

				if (1 == ftpCanceledSel) {
					cols++;
					if ("1".equals(medianaGeneral)) {
						double iFtpCanceled = HandlerRecord.getNumFtpCanceled(
								null, desde, hasta);
						ftpCanceledMG = iFtpCanceled / numUsersTotal;
					}
					ftpCanceledRP = ftpCanceledRP / numUsersRecord;
				}

				if (1 == ftpExpiredSel) {
					cols++;
					if ("1".equals(medianaGeneral)) {
						double iFtpExpired = HandlerRecord.getNumFtpExpired(
								null, desde, hasta);
						ftpExpiredMG = iFtpExpired / numUsersTotal;
					}
					ftpExpiredRP = ftpExpiredRP / numUsersRecord;
				}

				if (1 == ftpCompletedSel) {
					cols++;
					if ("1".equals(medianaGeneral)) {
						double iFtpCompleted = HandlerRecord
								.getNumFtpCompleted(null, desde, hasta);
						ftpCompletedMG = iFtpCompleted / numUsersTotal;
					}
					ftpCompletedRP = ftpCompletedRP / numUsersRecord;
				}

				if (1 == sacopsCreatedSel) {
					cols++;
					if ("1".equals(medianaGeneral)) {
						double iSacopsCreated = HandlerRecord
								.getNumSacopsCreated(0, desde, hasta);
						sacopsCreatedMG = iSacopsCreated / numUsersTotal;
					}
					sacopsCreatedRP = sacopsCreatedRP / numUsersRecord;
				}

				if (1 == sacopsIsResponsibleSel) {
					cols++;
					if ("1".equals(medianaGeneral)) {
						double iSacopsIsResponsible = HandlerRecord
								.getNumSacopsIsResponsible(0, desde, hasta);
						sacopsIsResponsibleMG = iSacopsIsResponsible
								/ numUsersTotal;
					}
					sacopsIsResponsibleRP = sacopsIsResponsibleRP
							/ numUsersRecord;
				}

				if (1 == sacopsParticipateSel) {
					cols++;
					if ("1".equals(medianaGeneral)) {
						double iSacopsParticipate = HandlerRecord
								.getNumSacopsParticipate(0, desde, hasta);
						sacopsParticipateMG = iSacopsParticipate
								/ numUsersTotal;
					}
					sacopsParticipateRP = sacopsParticipateRP / numUsersRecord;
				}

				if (1 == sacopsClosedSel) {
					cols++;
					if ("1".equals(medianaGeneral)) {
						double iSacopsClosed = HandlerRecord
								.getNumSacopsClosed(0, desde, hasta);
						sacopsClosedMG = iSacopsClosed / numUsersTotal;
					}
					sacopsClosedRP = sacopsClosedRP / numUsersRecord;
				}

				if (1 == printingSel) {
					cols++;
					if ("1".equals(medianaGeneral)) {
						double iPrinting = HandlerRecord.getNumPrinting(null,
								desde, hasta);
						printingMG = iPrinting / numUsersTotal;
					}
					printingRP = printingRP / numUsersRecord;
				}

				if (1 == printingApprovedSel) {
					cols++;
					if ("1".equals(medianaGeneral)) {
						double iPrintingApproved = HandlerRecord
								.getNumPrintingApproved(null, desde, hasta);
						printingApprovedMG = iPrintingApproved / numUsersTotal;
					}
					printingApprovedRP = printingApprovedRP / numUsersRecord;
				}

				if (1 == printingRejectedSel) {
					cols++;
					if ("1".equals(medianaGeneral)) {
						double iPrintingRejected = HandlerRecord
								.getNumPrintingRejected(null, desde, hasta);
						printingRejectedMG = iPrintingRejected / numUsersTotal;
					}
					printingRejectedRP = printingRejectedRP / numUsersRecord;
				}

				if (1 == printingPendingSel) {
					cols++;
					if ("1".equals(medianaGeneral)) {
						double iPrintingPending = HandlerRecord
								.getNumPrintingPending(null, desde, hasta);
						printingPendingMG = iPrintingPending / numUsersTotal;
					}
					printingPendingRP = printingPendingRP / numUsersRecord;
				}

			}// end if
				// //System.out.println("============== SESION2: " +
				// session.getId());

			// request.getSession().removeAttribute("usersRecord");

			request.getSession().removeAttribute("usersRecord");
			request.getSession().removeAttribute("numUsers");
			request.getSession().removeAttribute("areasTxt");
			request.getSession().removeAttribute("cargosTxt");

			request.getSession().removeAttribute("docsCreatedMG");
			request.getSession().removeAttribute("docsExpiredMG");
			request.getSession().removeAttribute("docsObsoleteMG");
			request.getSession().removeAttribute("draftsExpiredMG");
			request.getSession().removeAttribute("workflowsApprovalMG");
			request.getSession().removeAttribute("workflowsApprovalPendingMG");
			request.getSession().removeAttribute("workflowsApprovalApprovedMG");
			request.getSession().removeAttribute("workflowsApprovalCanceledMG");
			request.getSession().removeAttribute("workflowsApprovalExpiredMG");
			request.getSession().removeAttribute("workflowsReviewMG");
			request.getSession().removeAttribute("workflowsReviewPendingMG");
			request.getSession().removeAttribute("workflowsReviewApprovedMG");
			request.getSession().removeAttribute("workflowsReviewCanceledMG");
			request.getSession().removeAttribute("workflowsReviewExpiredMG");
			request.getSession().removeAttribute("ftpMG");
			request.getSession().removeAttribute("ftpPendingMG");
			request.getSession().removeAttribute("ftpApprovedMG");
			request.getSession().removeAttribute("ftpCanceledMG");
			request.getSession().removeAttribute("ftpExpiredMG");
			request.getSession().removeAttribute("ftpCompletedMG");
			request.getSession().removeAttribute("sacopsCreatedMG");
			request.getSession().removeAttribute("sacopsIsResponsibleMG");
			request.getSession().removeAttribute("sacopsParticipateMG");
			request.getSession().removeAttribute("sacopsClosedMG");
			request.getSession().removeAttribute("printingMG");
			request.getSession().removeAttribute("printingApprovedMG");
			request.getSession().removeAttribute("printingRejectedMG");
			request.getSession().removeAttribute("printingPendingMG");

			request.getSession().removeAttribute("docsCreatedRP");
			request.getSession().removeAttribute("docsExpiredRP");
			request.getSession().removeAttribute("docsObsoleteRP");
			request.getSession().removeAttribute("draftsExpiredRP");
			request.getSession().removeAttribute("workflowsApprovalRP");
			request.getSession().removeAttribute("workflowsApprovalPendingRP");
			request.getSession().removeAttribute("workflowsApprovalApprovedRP");
			request.getSession().removeAttribute("workflowsApprovalCanceledRP");
			request.getSession().removeAttribute("workflowsApprovalExpiredRP");
			request.getSession().removeAttribute("workflowsReviewRP");
			request.getSession().removeAttribute("workflowsReviewPendingRP");
			request.getSession().removeAttribute("workflowsReviewApprovedRP");
			request.getSession().removeAttribute("workflowsReviewCanceledRP");
			request.getSession().removeAttribute("workflowsReviewExpiredRP");
			request.getSession().removeAttribute("ftpRP");
			request.getSession().removeAttribute("ftpPendingRP");
			request.getSession().removeAttribute("ftpApprovedRP");
			request.getSession().removeAttribute("ftpCanceledRP");
			request.getSession().removeAttribute("ftpExpiredRP");
			request.getSession().removeAttribute("ftpCompletedRP");
			request.getSession().removeAttribute("sacopsCreatedRP");
			request.getSession().removeAttribute("sacopsIsResponsibleRP");
			request.getSession().removeAttribute("sacopsParticipateRP");
			request.getSession().removeAttribute("sacopsClosedRP");
			request.getSession().removeAttribute("printingRP");
			request.getSession().removeAttribute("printingApprovedRP");
			request.getSession().removeAttribute("printingRejectedRP");
			request.getSession().removeAttribute("printingPendingRP");

			request.getSession().removeAttribute("medianaGeneral");
			request.getSession().removeAttribute("recordPorUsuario");
			request.getSession().removeAttribute("recordPromedio");

			request.getSession().removeAttribute("docsCreatedSel");
			request.getSession().removeAttribute("docsExpiredSel");
			request.getSession().removeAttribute("docsObsoleteSel");
			request.getSession().removeAttribute("draftsExpiredSel");
			request.getSession().removeAttribute("workflowsApprovalSel");
			request.getSession().removeAttribute("workflowsApprovalPendingSel");
			request.getSession()
					.removeAttribute("workflowsApprovalApprovedSel");
			request.getSession()
					.removeAttribute("workflowsApprovalCanceledSel");
			request.getSession().removeAttribute("workflowsApprovalExpiredSel");
			request.getSession().removeAttribute("workflowsReviewSel");
			request.getSession().removeAttribute("workflowsReviewPendingSel");
			request.getSession().removeAttribute("workflowsReviewApprovedSel");
			request.getSession().removeAttribute("workflowsReviewCanceledSel");
			request.getSession().removeAttribute("workflowsReviewExpiredSel");
			request.getSession().removeAttribute("ftpSel");
			request.getSession().removeAttribute("ftpPendingSel");
			request.getSession().removeAttribute("ftpApprovedSel");
			request.getSession().removeAttribute("ftpCanceledSel");
			request.getSession().removeAttribute("ftpExpiredSel");
			request.getSession().removeAttribute("ftpCompletedSel");
			request.getSession().removeAttribute("sacopsCreatedSel");
			request.getSession().removeAttribute("sacopsIsResponsibleSel");
			request.getSession().removeAttribute("sacopsParticipateSel");
			request.getSession().removeAttribute("sacopsClosedSel");
			request.getSession().removeAttribute("printingSel");
			request.getSession().removeAttribute("printingApprovedSel");
			request.getSession().removeAttribute("printingRejectedSel");
			request.getSession().removeAttribute("printingPendingSel");

			request.getSession().removeAttribute("sAreas");
			request.getSession().removeAttribute("sCharges");
			request.getSession().removeAttribute("cols");
			request.getSession().removeAttribute("nameFile");

			// /////////////////////////
			request.getSession().setAttribute("usersRecord", vUsersRecord);
			request.getSession().setAttribute("numUsers", numUsers);
			request.getSession().setAttribute("areasTxt", areasTxt);
			request.getSession().setAttribute("cargosTxt", cargosTxt);

			if ("1".equals(medianaGeneral)) {
				try {
					request.getSession().setAttribute("docsCreatedMG",
							new String(nf.format(docsCreatedMG)));
					request.getSession().setAttribute("docsExpiredMG",
							new String(nf.format(docsExpiredMG)));
					request.getSession().setAttribute("docsObsoleteMG",
							new String(nf.format(docsObsoleteMG)));
					request.getSession().setAttribute("draftsExpiredMG",
							new String(nf.format(draftsExpiredMG)));
					request.getSession().setAttribute("workflowsApprovalMG",
							new String(nf.format(workflowsApprovalMG)));
					request.getSession().setAttribute(
							"workflowsApprovalPendingMG",
							new String(nf.format(workflowsApprovalPendingMG)));
					request.getSession().setAttribute(
							"workflowsApprovalApprovedMG",
							new String(nf.format(workflowsApprovalApprovedMG)));
					request.getSession().setAttribute(
							"workflowsApprovalCanceledMG",
							new String(nf.format(workflowsApprovalCanceledMG)));
					request.getSession().setAttribute(
							"workflowsApprovalExpiredMG",
							new String(nf.format(workflowsApprovalExpiredMG)));
					request.getSession().setAttribute("workflowsReviewMG",
							new String(nf.format(workflowsReviewMG)));
					request.getSession().setAttribute(
							"workflowsReviewPendingMG",
							new String(nf.format(workflowsReviewPendingMG)));
					request.getSession().setAttribute(
							"workflowsReviewApprovedMG",
							new String(nf.format(workflowsReviewApprovedMG)));
					request.getSession().setAttribute(
							"workflowsReviewCanceledMG",
							new String(nf.format(workflowsReviewCanceledMG)));
					request.getSession().setAttribute(
							"workflowsReviewExpiredMG",
							new String(nf.format(workflowsReviewExpiredMG)));
					request.getSession().setAttribute("ftpMG",
							new String(nf.format(ftpMG)));
					request.getSession().setAttribute("ftpPendingMG",
							new String(nf.format(ftpPendingMG)));
					request.getSession().setAttribute("ftpApprovedMG",
							new String(nf.format(ftpApprovedMG)));
					request.getSession().setAttribute("ftpCanceledMG",
							new String(nf.format(ftpCanceledMG)));
					request.getSession().setAttribute("ftpExpiredMG",
							new String(nf.format(ftpExpiredMG)));
					request.getSession().setAttribute("ftpCompletedMG",
							new String(nf.format(ftpCompletedMG)));
					request.getSession().setAttribute("sacopsCreatedMG",
							new String(nf.format(sacopsCreatedMG)));
					request.getSession().setAttribute("sacopsIsResponsibleMG",
							new String(nf.format(sacopsIsResponsibleMG)));
					request.getSession().setAttribute("sacopsParticipateMG",
							new String(nf.format(sacopsParticipateMG)));
					request.getSession().setAttribute("sacopsClosedMG",
							new String(nf.format(sacopsClosedMG)));
					request.getSession().setAttribute("printingMG",
							new String(nf.format(printingMG)));
					request.getSession().setAttribute("printingApprovedMG",
							new String(nf.format(printingApprovedMG)));
					request.getSession().setAttribute("printingRejectedMG",
							new String(nf.format(printingRejectedMG)));
					request.getSession().setAttribute("printingPendingMG",
							new String(nf.format(printingPendingMG)));
				} catch (Exception e) {
					// System.out.println("Error al convertir en double Record Promedio: "
					// + e);
				}
			}

			if ("1".equals(recordPromedio)) {
				try {
					request.getSession().setAttribute("docsCreatedRP",
							new String(nf.format(docsCreatedRP)));
					request.getSession().setAttribute("docsExpiredRP",
							new String(nf.format(docsExpiredRP)));
					request.getSession().setAttribute("docsObsoleteRP",
							new String(nf.format(docsObsoleteRP)));
					request.getSession().setAttribute("draftsExpiredRP",
							new String(nf.format(draftsExpiredRP)));
					request.getSession().setAttribute("workflowsApprovalRP",
							new String(nf.format(workflowsApprovalRP)));
					request.getSession().setAttribute(
							"workflowsApprovalPendingRP",
							new String(nf.format(workflowsApprovalPendingRP)));
					request.getSession().setAttribute(
							"workflowsApprovalApprovedRP",
							new String(nf.format(workflowsApprovalApprovedRP)));
					request.getSession().setAttribute(
							"workflowsApprovalCanceledRP",
							new String(nf.format(workflowsApprovalCanceledRP)));
					request.getSession().setAttribute(
							"workflowsApprovalExpiredRP",
							new String(nf.format(workflowsApprovalExpiredRP)));
					request.getSession().setAttribute("workflowsReviewRP",
							new String(nf.format(workflowsReviewRP)));
					request.getSession().setAttribute(
							"workflowsReviewPendingRP",
							new String(nf.format(workflowsReviewPendingRP)));
					request.getSession().setAttribute(
							"workflowsReviewApprovedRP",
							new String(nf.format(workflowsReviewApprovedRP)));
					request.getSession().setAttribute(
							"workflowsReviewCanceledRP",
							new String(nf.format(workflowsReviewCanceledRP)));
					request.getSession().setAttribute(
							"workflowsReviewExpiredRP",
							new String(nf.format(workflowsReviewExpiredRP)));
					request.getSession().setAttribute("ftpRP",
							new String(nf.format(ftpRP)));
					request.getSession().setAttribute("ftpPendingRP",
							new String(nf.format(ftpPendingRP)));
					request.getSession().setAttribute("ftpApprovedRP",
							new String(nf.format(ftpApprovedRP)));
					request.getSession().setAttribute("ftpCanceledRP",
							new String(nf.format(ftpCanceledRP)));
					request.getSession().setAttribute("ftpExpiredRP",
							new String(nf.format(ftpExpiredRP)));
					request.getSession().setAttribute("ftpCompletedRP",
							new String(nf.format(ftpCompletedRP)));
					request.getSession().setAttribute("sacopsCreatedRP",
							new String(nf.format(sacopsCreatedRP)));
					request.getSession().setAttribute("sacopsIsResponsibleRP",
							new String(nf.format(sacopsIsResponsibleRP)));
					request.getSession().setAttribute("sacopsParticipateRP",
							new String(nf.format(sacopsParticipateRP)));
					request.getSession().setAttribute("sacopsClosedRP",
							new String(nf.format(sacopsClosedRP)));
					request.getSession().setAttribute("printingRP",
							new String(nf.format(printingRP)));
					request.getSession().setAttribute("printingApprovedRP",
							new String(nf.format(printingApprovedRP)));
					request.getSession().setAttribute("printingRejectedRP",
							new String(nf.format(printingRejectedRP)));
					request.getSession().setAttribute("printingPendingRP",
							new String(nf.format(printingPendingRP)));
				} catch (Exception e) {
					// System.out.println("Error al convertir en double Record Promedio: "
					// + e);
				}
			}

			request.getSession().setAttribute("medianaGeneral", medianaGeneral);
			request.getSession().setAttribute("recordPorUsuario",
					recordPorUsuario);
			request.getSession().setAttribute("recordPromedio", recordPromedio);

			request.getSession().setAttribute("docsCreatedSel", docsCreatedSel);
			request.getSession().setAttribute("docsExpiredSel", docsExpiredSel);
			request.getSession().setAttribute("docsObsoleteSel",
					docsObsoleteSel);
			request.getSession().setAttribute("draftsExpiredSel",
					draftsExpiredSel);
			request.getSession().setAttribute("workflowsApprovalSel",
					workflowsApprovalSel);
			request.getSession().setAttribute("workflowsApprovalPendingSel",
					workflowsApprovalPendingSel);
			request.getSession().setAttribute("workflowsApprovalApprovedSel",
					workflowsApprovalApprovedSel);
			request.getSession().setAttribute("workflowsApprovalCanceledSel",
					workflowsApprovalCanceledSel);
			request.getSession().setAttribute("workflowsApprovalExpiredSel",
					workflowsApprovalExpiredSel);
			request.getSession().setAttribute("workflowsReviewSel",
					workflowsReviewSel);
			request.getSession().setAttribute("workflowsReviewPendingSel",
					workflowsReviewPendingSel);
			request.getSession().setAttribute("workflowsReviewApprovedSel",
					workflowsReviewApprovedSel);
			request.getSession().setAttribute("workflowsReviewCanceledSel",
					workflowsReviewCanceledSel);
			request.getSession().setAttribute("workflowsReviewExpiredSel",
					workflowsReviewExpiredSel);
			request.getSession().setAttribute("ftpSel", ftpSel);
			request.getSession().setAttribute("ftpPendingSel", ftpPendingSel);
			request.getSession().setAttribute("ftpApprovedSel", ftpApprovedSel);
			request.getSession().setAttribute("ftpCanceledSel", ftpCanceledSel);
			request.getSession().setAttribute("ftpExpiredSel", ftpExpiredSel);
			request.getSession().setAttribute("ftpCompletedSel",
					ftpCompletedSel);
			request.getSession().setAttribute("sacopsCreatedSel",
					sacopsCreatedSel);
			request.getSession().setAttribute("sacopsIsResponsibleSel",
					sacopsIsResponsibleSel);
			request.getSession().setAttribute("sacopsParticipateSel",
					sacopsParticipateSel);
			request.getSession().setAttribute("sacopsClosedSel",
					sacopsClosedSel);
			request.getSession().setAttribute("printingSel", printingSel);
			request.getSession().setAttribute("printingApprovedSel",
					printingApprovedSel);
			request.getSession().setAttribute("printingRejectedSel",
					printingRejectedSel);
			request.getSession().setAttribute("printingPendingSel",
					printingPendingSel);

			request.getSession().setAttribute("sAreas", sAreas);
			request.getSession().setAttribute("sCharges", sCharges);
			request.getSession().setAttribute("cols", cols);
			// //System.out.println("============== SESION3: " +
			// session.getId());

			String path = ToolsHTML.getPath();
			String nameFile = path.concat("reportes").concat(File.separator)
					.concat(usuario.getIdPerson() + "Record.xls");
			String nameFileBtn = "reportes".concat("/").concat(
					usuario.getIdPerson() + "Record.xls");
			// //System.out.println("nameFile: " + nameFileBtn);

			request.getSession().setAttribute("nameFile", nameFileBtn);

			// ////////////////////////////////////////////////////
			// GENERACION DE EXCEL
			try {
				// Proceso la información y genero el xls
				HSSFWorkbook objWB = new HSSFWorkbook();

				// Creo la hoja
				HSSFSheet hoja1 = objWB.createSheet("Record");
				HSSFRow fila = hoja1.createRow((short) 0);
				HSSFCell celda = fila.createCell((short) 0);
				hoja1.setDefaultColumnWidth((short) 24);

				HSSFFont fuenteTitulo = objWB.createFont();
				fuenteTitulo.setFontName(HSSFFont.FONT_ARIAL);
				fuenteTitulo.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
				fuenteTitulo.setFontHeightInPoints((short) 14);

				HSSFFont fuenteNegrita = objWB.createFont();
				fuenteNegrita.setFontName(HSSFFont.FONT_ARIAL);
				fuenteNegrita.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);

				HSSFFont fuenteNegritaBlanca = objWB.createFont();
				fuenteNegritaBlanca.setColor(HSSFColor.WHITE.index);
				fuenteNegritaBlanca.setFontName(HSSFFont.FONT_ARIAL);
				fuenteNegritaBlanca.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);

				HSSFCellStyle celdaTitulo = objWB.createCellStyle();
				celdaTitulo.setFont(fuenteTitulo);

				HSSFCellStyle celdaNegrita = objWB.createCellStyle();
				celdaNegrita.setFont(fuenteNegrita);

				HSSFCellStyle celdaColor = objWB.createCellStyle();
				celdaColor.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
				celdaColor.setFillForegroundColor(HSSFColor.BLUE.index);
				celdaColor.setFillBackgroundColor(HSSFColor.BLUE.index);
				celdaColor.setBorderBottom(HSSFCellStyle.BORDER_THIN);
				celdaColor.setBorderLeft(HSSFCellStyle.BORDER_THIN);
				celdaColor.setBorderRight(HSSFCellStyle.BORDER_THIN);
				celdaColor.setBorderTop(HSSFCellStyle.BORDER_THIN);
				celdaColor.setFont(fuenteNegritaBlanca);
				celdaColor.setAlignment(HSSFCellStyle.ALIGN_CENTER);
				celdaColor.setVerticalAlignment(HSSFCellStyle.VERTICAL_TOP);
				celdaColor.setWrapText(true);

				// Creamos la celda, aplicamos el estilo y definimos
				// el tipo de dato que contendrá la celda
				fila = hoja1.createRow((short) 1);
				celda = fila.createCell((short) 0);
				celda.setCellType(HSSFCell.CELL_TYPE_STRING);
				celda.setCellStyle(celdaTitulo);
				celda.setCellValue(rb.getString("rcd.titleresult"));
				hoja1.addMergedRegion(new Region(1, (short) 0, 1, (short) 5));

				fila = hoja1.createRow((short) 3);
				celda = fila.createCell((short) 0);
				celda.setCellStyle(celdaNegrita);
				celda.setCellType(HSSFCell.CELL_TYPE_STRING);
				celda.setCellValue(rb.getString("rcd.areas") + ":");

				celda = fila.createCell((short) 1);
				celda.setCellType(HSSFCell.CELL_TYPE_STRING);
				celda.setCellValue(sAreas);

				fila = hoja1.createRow((short) 4);
				celda = fila.createCell((short) 0);
				celda.setCellStyle(celdaNegrita);
				celda.setCellType(HSSFCell.CELL_TYPE_STRING);
				celda.setCellValue(rb.getString("rcd.charges") + ":");

				celda = fila.createCell((short) 1);
				celda.setCellType(HSSFCell.CELL_TYPE_STRING);
				celda.setCellValue(sCharges);
				// hoja1.addMergedRegion(new Region(4,(short)1,4,(short)5));

				fila = hoja1.createRow((short) 6);
				celda = fila.createCell((short) 0);
				celda.setCellStyle(celdaColor);
				celda.setCellType(HSSFCell.CELL_TYPE_STRING);
				celda.setCellValue(rb.getString("rcd.usr"));

				celda = fila.createCell((short) 1);
				celda.setCellStyle(celdaColor);
				celda.setCellType(HSSFCell.CELL_TYPE_STRING);
				celda.setCellValue(rb.getString("rcd.name"));

				celda = fila.createCell((short) 2);
				celda.setCellStyle(celdaColor);
				celda.setCellType(HSSFCell.CELL_TYPE_STRING);
				celda.setCellValue(rb.getString("rcd.area"));

				celda = fila.createCell((short) 3);
				celda.setCellStyle(celdaColor);
				celda.setCellType(HSSFCell.CELL_TYPE_STRING);
				celda.setCellValue(rb.getString("rcd.charge"));

				int col = 3;
				if (docsCreatedSel == 1) {
					col++;
					celda = fila.createCell((short) col);
					celda.setCellStyle(celdaColor);
					celda.setCellType(HSSFCell.CELL_TYPE_STRING);
					celda.setCellValue(rb.getString("rcd.varDoc6"));
				}
				if (docsExpiredSel == 1) {
					col++;
					celda = fila.createCell((short) col);
					celda.setCellStyle(celdaColor);
					celda.setCellType(HSSFCell.CELL_TYPE_STRING);
					celda.setCellValue(rb.getString("rcd.varDoc3"));
				}
				if (docsObsoleteSel == 1) {
					col++;
					celda = fila.createCell((short) col);
					celda.setCellStyle(celdaColor);
					celda.setCellType(HSSFCell.CELL_TYPE_STRING);
					celda.setCellValue(rb.getString("rcd.varDoc4"));
				}
				if (draftsExpiredSel == 1) {
					col++;
					celda = fila.createCell((short) col);
					celda.setCellStyle(celdaColor);
					celda.setCellType(HSSFCell.CELL_TYPE_STRING);
					celda.setCellValue(rb.getString("rcd.varDoc5"));
				}
				if (workflowsApprovalSel == 1) {
					col++;
					celda = fila.createCell((short) col);
					celda.setCellStyle(celdaColor);
					celda.setCellType(HSSFCell.CELL_TYPE_STRING);
					celda.setCellValue(rb.getString("rcd.varFlow1"));
				}
				if (workflowsApprovalPendingSel == 1) {
					col++;
					celda = fila.createCell((short) col);
					celda.setCellStyle(celdaColor);
					celda.setCellType(HSSFCell.CELL_TYPE_STRING);
					celda.setCellValue(rb.getString("rcd.varFlow2"));
				}
				if (workflowsApprovalApprovedSel == 1) {
					col++;
					celda = fila.createCell((short) col);
					celda.setCellStyle(celdaColor);
					celda.setCellType(HSSFCell.CELL_TYPE_STRING);
					celda.setCellValue(rb.getString("rcd.varFlow3"));
				}
				if (workflowsApprovalCanceledSel == 1) {
					col++;
					celda = fila.createCell((short) col);
					celda.setCellStyle(celdaColor);
					celda.setCellType(HSSFCell.CELL_TYPE_STRING);
					celda.setCellValue(rb.getString("rcd.varFlow4"));
				}
				if (workflowsApprovalExpiredSel == 1) {
					col++;
					celda = fila.createCell((short) col);
					celda.setCellStyle(celdaColor);
					celda.setCellType(HSSFCell.CELL_TYPE_STRING);
					celda.setCellValue(rb.getString("rcd.varFlow5"));
				}
				if (workflowsReviewSel == 1) {
					col++;
					celda = fila.createCell((short) col);
					celda.setCellStyle(celdaColor);
					celda.setCellType(HSSFCell.CELL_TYPE_STRING);
					celda.setCellValue(rb.getString("rcd.varFlow6"));
				}
				if (workflowsReviewPendingSel == 1) {
					col++;
					celda = fila.createCell((short) col);
					celda.setCellStyle(celdaColor);
					celda.setCellType(HSSFCell.CELL_TYPE_STRING);
					celda.setCellValue(rb.getString("rcd.varFlow7"));
				}
				if (workflowsReviewApprovedSel == 1) {
					col++;
					celda = fila.createCell((short) col);
					celda.setCellStyle(celdaColor);
					celda.setCellType(HSSFCell.CELL_TYPE_STRING);
					celda.setCellValue(rb.getString("rcd.varFlow8"));
				}
				if (workflowsReviewCanceledSel == 1) {
					col++;
					celda = fila.createCell((short) col);
					celda.setCellStyle(celdaColor);
					celda.setCellType(HSSFCell.CELL_TYPE_STRING);
					celda.setCellValue(rb.getString("rcd.varFlow9"));
				}
				if (workflowsReviewExpiredSel == 1) {
					col++;
					celda = fila.createCell((short) col);
					celda.setCellStyle(celdaColor);
					celda.setCellType(HSSFCell.CELL_TYPE_STRING);
					celda.setCellValue(rb.getString("rcd.varFlow10"));
				}

				if (ToolsHTML.showFTP()) {
					if (ftpSel == 1) {
						col++;
						celda = fila.createCell((short) col);
						celda.setCellStyle(celdaColor);
						celda.setCellType(HSSFCell.CELL_TYPE_STRING);
						celda.setCellValue(rb.getString("rcd.varFTP1"));
					}
					if (ftpPendingSel == 1) {
						col++;
						celda = fila.createCell((short) col);
						celda.setCellStyle(celdaColor);
						celda.setCellType(HSSFCell.CELL_TYPE_STRING);
						celda.setCellValue(rb.getString("rcd.varFTP2"));
					}
					if (ftpApprovedSel == 1) {
						col++;
						celda = fila.createCell((short) col);
						celda.setCellStyle(celdaColor);
						celda.setCellType(HSSFCell.CELL_TYPE_STRING);
						celda.setCellValue(rb.getString("rcd.varFTP3"));
					}
					if (ftpCanceledSel == 1) {
						col++;
						celda = fila.createCell((short) col);
						celda.setCellStyle(celdaColor);
						celda.setCellType(HSSFCell.CELL_TYPE_STRING);
						celda.setCellValue(rb.getString("rcd.varFTP4"));
					}
					if (ftpExpiredSel == 1) {
						col++;
						celda = fila.createCell((short) col);
						celda.setCellStyle(celdaColor);
						celda.setCellType(HSSFCell.CELL_TYPE_STRING);
						celda.setCellValue(rb.getString("rcd.varFTP5"));
					}
					if (ftpCompletedSel == 1) {
						col++;
						celda = fila.createCell((short) col);
						celda.setCellStyle(celdaColor);
						celda.setCellType(HSSFCell.CELL_TYPE_STRING);
						celda.setCellValue(rb.getString("rcd.varFTP6"));
					}
				}

				if (ToolsHTML.showSACOP()) {
					if (sacopsCreatedSel == 1) {
						col++;
						celda = fila.createCell((short) col);
						celda.setCellStyle(celdaColor);
						celda.setCellType(HSSFCell.CELL_TYPE_STRING);
						celda.setCellValue(rb.getString("rcd.varSacop1"));
					}
					if (sacopsIsResponsibleSel == 1) {
						col++;
						celda = fila.createCell((short) col);
						celda.setCellStyle(celdaColor);
						celda.setCellType(HSSFCell.CELL_TYPE_STRING);
						celda.setCellValue(rb.getString("rcd.varSacop2"));
					}
					if (sacopsParticipateSel == 1) {
						col++;
						celda = fila.createCell((short) col);
						celda.setCellStyle(celdaColor);
						celda.setCellType(HSSFCell.CELL_TYPE_STRING);
						celda.setCellValue(rb.getString("rcd.varSacop3"));
					}
					if (sacopsClosedSel == 1) {
						col++;
						celda = fila.createCell((short) col);
						celda.setCellStyle(celdaColor);
						celda.setCellType(HSSFCell.CELL_TYPE_STRING);
						celda.setCellValue(rb.getString("rcd.varSacop4"));
					}
				}

				if (printingSel == 1) {
					col++;
					celda = fila.createCell((short) col);
					celda.setCellStyle(celdaColor);
					celda.setCellType(HSSFCell.CELL_TYPE_STRING);
					celda.setCellValue(rb.getString("rcd.varPrint1"));
				}
				if (printingApprovedSel == 1) {
					col++;
					celda = fila.createCell((short) col);
					celda.setCellStyle(celdaColor);
					celda.setCellType(HSSFCell.CELL_TYPE_STRING);
					celda.setCellValue(rb.getString("rcd.varPrint5"));
				}
				if (printingRejectedSel == 1) {
					col++;
					celda = fila.createCell((short) col);
					celda.setCellStyle(celdaColor);
					celda.setCellType(HSSFCell.CELL_TYPE_STRING);
					celda.setCellValue(rb.getString("rcd.varPrint6"));
				}
				if (printingPendingSel == 1) {
					col++;
					celda = fila.createCell((short) col);
					celda.setCellStyle(celdaColor);
					celda.setCellType(HSSFCell.CELL_TYPE_STRING);
					celda.setCellValue(rb.getString("rcd.varPrint7"));
				}

				int iFila = 6;
				if ("1".equals(recordPorUsuario)) {
					if (vUsersRecord != null && vUsersRecord.size() > 0) {
						for (int k = 0; k < vUsersRecord.size(); k++) {
							Users usrReport = (Users) vUsersRecord.get(k);
							iFila++;
							fila = hoja1.createRow((short) iFila);

							celda = fila.createCell((short) 0);
							celda.setCellType(HSSFCell.CELL_TYPE_STRING);
							celda.setCellValue(usrReport.getNameUser());

							celda = fila.createCell((short) 1);
							celda.setCellType(HSSFCell.CELL_TYPE_STRING);
							celda.setCellValue(usrReport.getNamePerson());

							celda = fila.createCell((short) 2);
							celda.setCellType(HSSFCell.CELL_TYPE_STRING);
							celda.setCellValue(usrReport.getArea().getArea());

							celda = fila.createCell((short) 3);
							celda.setCellType(HSSFCell.CELL_TYPE_STRING);
							celda.setCellValue(usrReport.getCargo().getCargo());

							int columna = 3;
							if (usrReport.getUserRecord() != null) {
								if (docsCreatedSel == 1) {
									columna++;
									celda = fila.createCell((short) columna);
									celda.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
									celda.setCellValue(usrReport
											.getUserRecord().getDocsCreated());
								}
								if (docsExpiredSel == 1) {
									columna++;
									celda = fila.createCell((short) columna);
									celda.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
									celda.setCellValue(usrReport
											.getUserRecord().getDocsExpired());
								}
								if (docsObsoleteSel == 1) {
									columna++;
									celda = fila.createCell((short) columna);
									celda.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
									celda.setCellValue(usrReport
											.getUserRecord().getDocsObsolete());
								}
								if (draftsExpiredSel == 1) {
									columna++;
									celda = fila.createCell((short) columna);
									celda.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
									celda.setCellValue(usrReport
											.getUserRecord().getDraftsExpired());
								}
								if (workflowsApprovalSel == 1) {
									columna++;
									celda = fila.createCell((short) columna);
									celda.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
									celda.setCellValue(usrReport
											.getUserRecord()
											.getWorkflowsApproval());
								}
								if (workflowsApprovalPendingSel == 1) {
									columna++;
									celda = fila.createCell((short) columna);
									celda.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
									celda.setCellValue(usrReport
											.getUserRecord()
											.getWorkflowsApprovalPending());
								}
								if (workflowsApprovalApprovedSel == 1) {
									columna++;
									celda = fila.createCell((short) columna);
									celda.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
									celda.setCellValue(usrReport
											.getUserRecord()
											.getWorkflowsApprovalApproved());
								}
								if (workflowsApprovalCanceledSel == 1) {
									columna++;
									celda = fila.createCell((short) columna);
									celda.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
									celda.setCellValue(usrReport
											.getUserRecord()
											.getWorkflowsApprovalCanceled());
								}
								if (workflowsApprovalExpiredSel == 1) {
									columna++;
									celda = fila.createCell((short) columna);
									celda.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
									celda.setCellValue(usrReport
											.getUserRecord()
											.getWorkflowsApprovalExpired());
								}
								if (workflowsReviewSel == 1) {
									columna++;
									celda = fila.createCell((short) columna);
									celda.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
									celda.setCellValue(usrReport
											.getUserRecord()
											.getWorkflowsReview());
								}
								if (workflowsReviewPendingSel == 1) {
									columna++;
									celda = fila.createCell((short) columna);
									celda.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
									celda.setCellValue(usrReport
											.getUserRecord()
											.getWorkflowsReviewPending());
								}
								if (workflowsReviewApprovedSel == 1) {
									columna++;
									celda = fila.createCell((short) columna);
									celda.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
									celda.setCellValue(usrReport
											.getUserRecord()
											.getWorkflowsReviewApproved());
								}
								if (workflowsReviewCanceledSel == 1) {
									columna++;
									celda = fila.createCell((short) columna);
									celda.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
									celda.setCellValue(usrReport
											.getUserRecord()
											.getWorkflowsReviewCanceled());
								}
								if (workflowsReviewExpiredSel == 1) {
									columna++;
									celda = fila.createCell((short) columna);
									celda.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
									celda.setCellValue(usrReport
											.getUserRecord()
											.getWorkflowsReviewExpired());
								}

								if (ToolsHTML.showFTP()) {
									if (ftpSel == 1) {
										columna++;
										celda = fila
												.createCell((short) columna);
										celda.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
										celda.setCellValue(usrReport
												.getUserRecord().getFtp());
									}
									if (ftpPendingSel == 1) {
										columna++;
										celda = fila
												.createCell((short) columna);
										celda.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
										celda.setCellValue(usrReport
												.getUserRecord()
												.getFtpPending());
									}
									if (ftpApprovedSel == 1) {
										columna++;
										celda = fila
												.createCell((short) columna);
										celda.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
										celda.setCellValue(usrReport
												.getUserRecord()
												.getFtpApproved());
									}
									if (ftpCanceledSel == 1) {
										columna++;
										celda = fila
												.createCell((short) columna);
										celda.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
										celda.setCellValue(usrReport
												.getUserRecord()
												.getFtpCanceled());
									}
									if (ftpExpiredSel == 1) {
										columna++;
										celda = fila
												.createCell((short) columna);
										celda.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
										celda.setCellValue(usrReport
												.getUserRecord()
												.getFtpExpired());
									}
									if (ftpCompletedSel == 1) {
										columna++;
										celda = fila
												.createCell((short) columna);
										celda.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
										celda.setCellValue(usrReport
												.getUserRecord()
												.getFtpCompleted());
									}
								}

								if (ToolsHTML.showSACOP()) {
									if (sacopsCreatedSel == 1) {
										columna++;
										celda = fila
												.createCell((short) columna);
										celda.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
										celda.setCellValue(usrReport
												.getUserRecord()
												.getSacopsCreated());
									}
									if (sacopsIsResponsibleSel == 1) {
										columna++;
										celda = fila
												.createCell((short) columna);
										celda.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
										celda.setCellValue(usrReport
												.getUserRecord()
												.getSacopsIsResponsible());
									}
									if (sacopsParticipateSel == 1) {
										columna++;
										celda = fila
												.createCell((short) columna);
										celda.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
										celda.setCellValue(usrReport
												.getUserRecord()
												.getSacopsParticipate());
									}
									if (sacopsClosedSel == 1) {
										columna++;
										celda = fila
												.createCell((short) columna);
										celda.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
										celda.setCellValue(usrReport
												.getUserRecord()
												.getSacopsClosed());
									}
								}

								if (printingSel == 1) {
									columna++;
									celda = fila.createCell((short) columna);
									celda.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
									celda.setCellValue(usrReport
											.getUserRecord().getPrinting());
								}
								if (printingApprovedSel == 1) {
									columna++;
									celda = fila.createCell((short) columna);
									celda.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
									celda.setCellValue(usrReport
											.getUserRecord()
											.getPrintingApproved());
								}
								if (printingRejectedSel == 1) {
									columna++;
									celda = fila.createCell((short) columna);
									celda.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
									celda.setCellValue(usrReport
											.getUserRecord()
											.getPrintingRejected());
								}
								if (printingPendingSel == 1) {
									columna++;
									celda = fila.createCell((short) columna);
									celda.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
									celda.setCellValue(usrReport
											.getUserRecord()
											.getPrintingPending());
								}
							}
						} // end for
					}
				}// end if("1".equals(recordPorUsuario))

				if ("1".equals(recordPromedio)) {
					iFila++;
					fila = hoja1.createRow((short) iFila);

					celda = fila.createCell((short) 3);
					celda.setCellType(HSSFCell.CELL_TYPE_STRING);
					celda.setCellStyle(celdaNegrita);
					celda.setCellValue(rb.getString("rcd.option1"));
					// hoja1.addMergedRegion(new
					// Region(iFila,(short)1,iFila,(short)4));

					int columna = 3;
					if (docsCreatedSel == 1) {
						columna++;
						celda = fila.createCell((short) columna);
						celda.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
						celda.setCellValue(docsCreatedRP);
					}
					if (docsExpiredSel == 1) {
						columna++;
						celda = fila.createCell((short) columna);
						celda.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
						celda.setCellValue(docsExpiredRP);
					}
					if (docsObsoleteSel == 1) {
						columna++;
						celda = fila.createCell((short) columna);
						celda.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
						celda.setCellValue(docsObsoleteRP);
					}
					if (draftsExpiredSel == 1) {
						columna++;
						celda = fila.createCell((short) columna);
						celda.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
						celda.setCellValue(draftsExpiredRP);
					}
					if (workflowsApprovalSel == 1) {
						columna++;
						celda = fila.createCell((short) columna);
						celda.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
						celda.setCellValue(workflowsApprovalRP);
					}
					if (workflowsApprovalPendingSel == 1) {
						columna++;
						celda = fila.createCell((short) columna);
						celda.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
						celda.setCellValue(workflowsApprovalPendingRP);
					}
					if (workflowsApprovalApprovedSel == 1) {
						columna++;
						celda = fila.createCell((short) columna);
						celda.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
						celda.setCellValue(workflowsApprovalApprovedRP);
					}
					if (workflowsApprovalCanceledSel == 1) {
						columna++;
						celda = fila.createCell((short) columna);
						celda.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
						celda.setCellValue(workflowsApprovalCanceledRP);
					}
					if (workflowsApprovalExpiredSel == 1) {
						columna++;
						celda = fila.createCell((short) columna);
						celda.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
						celda.setCellValue(workflowsApprovalExpiredRP);
					}
					if (workflowsReviewSel == 1) {
						columna++;
						celda = fila.createCell((short) columna);
						celda.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
						celda.setCellValue(workflowsReviewRP);
					}
					if (workflowsReviewPendingSel == 1) {
						columna++;
						celda = fila.createCell((short) columna);
						celda.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
						celda.setCellValue(workflowsReviewPendingRP);
					}
					if (workflowsReviewApprovedSel == 1) {
						columna++;
						celda = fila.createCell((short) columna);
						celda.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
						celda.setCellValue(workflowsReviewApprovedRP);
					}
					if (workflowsReviewCanceledSel == 1) {
						columna++;
						celda = fila.createCell((short) columna);
						celda.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
						celda.setCellValue(workflowsReviewCanceledRP);
					}
					if (workflowsReviewExpiredSel == 1) {
						columna++;
						celda = fila.createCell((short) columna);
						celda.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
						celda.setCellValue(workflowsReviewExpiredRP);
					}

					if (ToolsHTML.showFTP()) {
						if (ftpSel == 1) {
							columna++;
							celda = fila.createCell((short) columna);
							celda.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
							celda.setCellValue(ftpRP);
						}
						if (ftpPendingSel == 1) {
							columna++;
							celda = fila.createCell((short) columna);
							celda.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
							celda.setCellValue(ftpPendingRP);
						}
						if (ftpApprovedSel == 1) {
							columna++;
							celda = fila.createCell((short) columna);
							celda.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
							celda.setCellValue(ftpApprovedRP);
						}
						if (ftpCanceledSel == 1) {
							columna++;
							celda = fila.createCell((short) columna);
							celda.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
							celda.setCellValue(ftpCanceledRP);
						}
						if (ftpExpiredSel == 1) {
							columna++;
							celda = fila.createCell((short) columna);
							celda.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
							celda.setCellValue(ftpExpiredRP);
						}
						if (ftpCompletedSel == 1) {
							columna++;
							celda = fila.createCell((short) columna);
							celda.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
							celda.setCellValue(ftpCompletedRP);
						}
					}

					if (ToolsHTML.showSACOP()) {
						if (sacopsCreatedSel == 1) {
							columna++;
							celda = fila.createCell((short) columna);
							celda.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
							celda.setCellValue(sacopsCreatedRP);
						}
						if (sacopsIsResponsibleSel == 1) {
							columna++;
							celda = fila.createCell((short) columna);
							celda.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
							celda.setCellValue(sacopsIsResponsibleRP);
						}
						if (sacopsParticipateSel == 1) {
							columna++;
							celda = fila.createCell((short) columna);
							celda.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
							celda.setCellValue(sacopsParticipateRP);
						}
						if (sacopsClosedSel == 1) {
							columna++;
							celda = fila.createCell((short) columna);
							celda.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
							celda.setCellValue(sacopsClosedRP);
						}
					}

					if (printingSel == 1) {
						columna++;
						celda = fila.createCell((short) columna);
						celda.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
						celda.setCellValue(printingRP);
					}
					if (printingApprovedSel == 1) {
						columna++;
						celda = fila.createCell((short) columna);
						celda.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
						celda.setCellValue(printingRP);
					}
					if (printingRejectedSel == 1) {
						columna++;
						celda = fila.createCell((short) columna);
						celda.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
						celda.setCellValue(printingRejectedRP);
					}
					if (printingPendingSel == 1) {
						columna++;
						celda = fila.createCell((short) columna);
						celda.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
						celda.setCellValue(printingPendingRP);
					}
				} // end if("1".equals(recordPromedio))

				if ("1".equals(medianaGeneral)) {
					iFila++;
					fila = hoja1.createRow((short) iFila);

					celda = fila.createCell((short) 3);
					celda.setCellType(HSSFCell.CELL_TYPE_STRING);
					celda.setCellStyle(celdaNegrita);
					celda.setCellValue(rb.getString("rcd.option3"));
					// hoja1.addMergedRegion(new
					// Region(iFila,(short)1,iFila,(short)4));

					int columna = 3;
					if (docsCreatedSel == 1) {
						columna++;
						celda = fila.createCell((short) columna);
						celda.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
						celda.setCellValue(docsCreatedMG);
					}
					if (docsExpiredSel == 1) {
						columna++;
						celda = fila.createCell((short) columna);
						celda.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
						celda.setCellValue(docsExpiredMG);
					}
					if (docsObsoleteSel == 1) {
						columna++;
						celda = fila.createCell((short) columna);
						celda.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
						celda.setCellValue(docsObsoleteMG);
					}
					if (draftsExpiredSel == 1) {
						columna++;
						celda = fila.createCell((short) columna);
						celda.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
						celda.setCellValue(draftsExpiredMG);
					}
					if (workflowsApprovalSel == 1) {
						columna++;
						celda = fila.createCell((short) columna);
						celda.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
						celda.setCellValue(workflowsApprovalMG);
					}
					if (workflowsApprovalPendingSel == 1) {
						columna++;
						celda = fila.createCell((short) columna);
						celda.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
						celda.setCellValue(workflowsApprovalPendingMG);
					}
					if (workflowsApprovalApprovedSel == 1) {
						columna++;
						celda = fila.createCell((short) columna);
						celda.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
						celda.setCellValue(workflowsApprovalApprovedMG);
					}
					if (workflowsApprovalCanceledSel == 1) {
						columna++;
						celda = fila.createCell((short) columna);
						celda.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
						celda.setCellValue(workflowsApprovalCanceledMG);
					}
					if (workflowsApprovalExpiredSel == 1) {
						columna++;
						celda = fila.createCell((short) columna);
						celda.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
						celda.setCellValue(workflowsApprovalExpiredMG);
					}
					if (workflowsReviewSel == 1) {
						columna++;
						celda = fila.createCell((short) columna);
						celda.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
						celda.setCellValue(workflowsReviewMG);
					}
					if (workflowsReviewPendingSel == 1) {
						columna++;
						celda = fila.createCell((short) columna);
						celda.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
						celda.setCellValue(workflowsReviewPendingMG);
					}
					if (workflowsReviewApprovedSel == 1) {
						columna++;
						celda = fila.createCell((short) columna);
						celda.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
						celda.setCellValue(workflowsReviewApprovedMG);
					}
					if (workflowsReviewCanceledSel == 1) {
						columna++;
						celda = fila.createCell((short) columna);
						celda.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
						celda.setCellValue(workflowsReviewCanceledMG);
					}
					if (workflowsReviewExpiredSel == 1) {
						columna++;
						celda = fila.createCell((short) columna);
						celda.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
						celda.setCellValue(workflowsReviewExpiredMG);
					}

					if (ToolsHTML.showFTP()) {
						if (ftpSel == 1) {
							columna++;
							celda = fila.createCell((short) columna);
							celda.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
							celda.setCellValue(ftpMG);
						}
						if (ftpPendingSel == 1) {
							columna++;
							celda = fila.createCell((short) columna);
							celda.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
							celda.setCellValue(ftpPendingMG);
						}
						if (ftpApprovedSel == 1) {
							columna++;
							celda = fila.createCell((short) columna);
							celda.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
							celda.setCellValue(ftpApprovedMG);
						}
						if (ftpCanceledSel == 1) {
							columna++;
							celda = fila.createCell((short) columna);
							celda.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
							celda.setCellValue(ftpCanceledMG);
						}
						if (ftpExpiredSel == 1) {
							columna++;
							celda = fila.createCell((short) columna);
							celda.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
							celda.setCellValue(ftpExpiredMG);
						}
						if (ftpCompletedSel == 1) {
							columna++;
							celda = fila.createCell((short) columna);
							celda.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
							celda.setCellValue(ftpCompletedMG);
						}
					}

					if (ToolsHTML.showSACOP()) {
						if (sacopsCreatedSel == 1) {
							columna++;
							celda = fila.createCell((short) columna);
							celda.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
							celda.setCellValue(sacopsCreatedMG);
						}
						if (sacopsIsResponsibleSel == 1) {
							columna++;
							celda = fila.createCell((short) columna);
							celda.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
							celda.setCellValue(sacopsIsResponsibleMG);
						}
						if (sacopsParticipateSel == 1) {
							columna++;
							celda = fila.createCell((short) columna);
							celda.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
							celda.setCellValue(sacopsParticipateMG);
						}
						if (sacopsClosedSel == 1) {
							columna++;
							celda = fila.createCell((short) columna);
							celda.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
							celda.setCellValue(sacopsClosedMG);
						}
					}

					if (printingSel == 1) {
						columna++;
						celda = fila.createCell((short) columna);
						celda.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
						celda.setCellValue(printingMG);
					}
					if (printingApprovedSel == 1) {
						columna++;
						celda = fila.createCell((short) columna);
						celda.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
						celda.setCellValue(printingMG);
					}
					if (printingRejectedSel == 1) {
						columna++;
						celda = fila.createCell((short) columna);
						celda.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
						celda.setCellValue(printingRejectedMG);
					}
					if (printingPendingSel == 1) {
						columna++;
						celda = fila.createCell((short) columna);
						celda.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
						celda.setCellValue(printingPendingMG);
					}
				} // end if("1".equals(medianaGeneral))

				// Volcamos la información a un archivo.
				FileOutputStream archivoSalida = new FileOutputStream(nameFile);
				objWB.write(archivoSalida);
				archivoSalida.close();

			} catch (Exception e) {
				// System.out.println("Error al generar .xls de Record " + e);
			}
			// ////////////////////////////////////////////////////

			return goSucces();
		} catch (ApplicationExceptionChecked ae) {
			ae.printStackTrace();
			return goError(ae.getKeyError());
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return goError();
	}

}
