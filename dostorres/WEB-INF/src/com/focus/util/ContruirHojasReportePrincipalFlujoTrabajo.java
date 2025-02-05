package com.focus.util;

import java.util.Collection;
import java.util.ResourceBundle;

import com.desige.webDocuments.document.forms.DocumentsCheckOutsBean;
import com.desige.webDocuments.persistent.managers.HandlerDocuments;
import com.desige.webDocuments.workFlows.forms.DataUserWorkFlowForm;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;

/**
 * Clase utilitaria para encapsular la logica de creación de las Hojas("sheets") del Libro Reportes Flujo de Trabajo, Utilizando La Libreria apache.poi Este
 * reporte solo debe ser visible a los usuarios del grupo de administracion <br>
 * 
 * Title: ContruirHojasReportePrincipalFlujoTrabajo.java <br>
 * Copyright: (c) 2020 Focus Consulting C.A.<br/>
 * 
 * @author Darwin Felipe Uzcategui Gonzalez (DFUG)
 * 
 * @version WebDocuments v5.0 <br>
 *          Changes:<br>
 *          <ul>
 *          <li>16/11/2020 (DFUG) Creation</li>
 *          <ul>
 */
public class ContruirHojasReportePrincipalFlujoTrabajo {

	/**
	 * Metodo para construir la hoja "sheet" de excel de los documentos pendientes.
	 * 
	 * @param sheet
	 *            HSSFSheet
	 * @param rb
	 *            ResourceBundle
	 * @param docCheckOuts
	 *            Collection<DocumentsCheckOutsBean>
	 * @param dateSystem
	 *            String
	 */

	public static void contruirHojaDocPendiente(HSSFSheet sheet, ResourceBundle rb, Collection<DocumentsCheckOutsBean> docPendings, String dateSystem) {
		try {

			HSSFRow fila1;
			HSSFCell celda;
			// Estableciendo Titulo de Las Hojas del Libro de Excel

			// llenamos los titulos documentos pendientes
			fila1 = sheet.getRow(0);
			if (fila1 != null) {

				celda = fila1.getCell((short) 2);
				celda.setCellValue(rb.getString("pendingDocuments.ListTitle") + " " + dateSystem);

			}

			fila1 = sheet.getRow(2);
			// sheet.setn(rb.getString("pendingDocuments.sheetName"));
			// workbook.setSheetName(sheet-index, "my sheet name");
			if (fila1 != null) {

				celda = fila1.getCell((short) 0); // columna 0
				celda.setCellValue(rb.getString("label.name"));

				celda = fila1.getCell((short) 1); // columna 1
				celda.setCellValue(rb.getString("label.version"));

				celda = fila1.getCell((short) 2); // columna 2
				celda.setCellValue(rb.getString("label.document"));

				celda = fila1.getCell((short) 3); // columna 3
				celda.setCellValue(rb.getString("label.blockedAt"));
			}

			// iteramos en los resultados que tenemos

			if (docPendings != null) {
				short i = 3, j = 0; // , styleTemplatefila = 3;
				fila1 = sheet.getRow(i);

				int filaContador = 3;
				for (DocumentsCheckOutsBean docsCheckOutsBean : docPendings) {

					HSSFRow row = sheet.createRow(filaContador++);
					// Visualizamos nombre usuario en la Columna A (0)
					row.createCell((short) 0)
							.setCellValue(docsCheckOutsBean.getPersonBean().getApellido() + ", " + docsCheckOutsBean.getPersonBean().getNombre());
					// Visualizamos la version en la Columna B (1)
					row.createCell((short) 1).setCellValue(docsCheckOutsBean.getMayorVer() + "." + docsCheckOutsBean.getMinorVer());
					// Visializamos el nombre del documento C (2)
					row.createCell((short) 2)
							.setCellValue(docsCheckOutsBean.getNameDocument() + " " + docsCheckOutsBean.getPrefix() + docsCheckOutsBean.getNumber());
					// Visualizamos la fecha de bloqueo en la columna D (3)
					row.createCell((short) 3).setCellValue(docsCheckOutsBean.getDateCheckOut());

				}

			}

		} catch (Exception e) {

			e.printStackTrace();
		}
	}

	/**
	 * Metodo para construir la hoja "sheet" de excel de los documentos expirados.
	 * 
	 * @param sheet
	 * @param rb
	 * @param docCheckOuts
	 * @param dateSystem
	 * 
	 */
	public static void contruirHojaDocExpirados(HSSFSheet sheet, ResourceBundle rb, Collection<DocumentsCheckOutsBean> docExpires, String dateSystem) {
		try {

			HSSFRow fila1;
			HSSFCell celda;
			// Estableciendo Titulo de Las Hojas del Libro de Excel

			// llenamos los titulos documentos pendientes
			fila1 = sheet.getRow(0);
			if (fila1 != null) {

				celda = fila1.getCell((short) 2);
				celda.setCellValue(rb.getString("expiredDocuments.ListTitle") + " " + dateSystem);

			}

			fila1 = sheet.getRow(2);
			// sheet.setn(rb.getString("pendingDocuments.sheetName"));
			// workbook.setSheetName(sheet-index, "my sheet name");
			if (fila1 != null) {

				celda = fila1.getCell((short) 0); // columna 0
				celda.setCellValue(rb.getString("label.name"));

				celda = fila1.getCell((short) 1); // columna 1
				celda.setCellValue(rb.getString("label.version"));

				celda = fila1.getCell((short) 2); // columna 2
				celda.setCellValue(rb.getString("label.document"));

				celda = fila1.getCell((short) 3); // columna 3
				celda.setCellValue(rb.getString("label.blockedAt"));
			}

			// iteramos en los resultados que tenemos

			if (docExpires != null) {
				short i = 3, j = 0; // , styleTemplatefila = 3;
				fila1 = sheet.getRow(i);

				int filaContador = 3;
				for (DocumentsCheckOutsBean docsCheckOutsBean : docExpires) {

					HSSFRow row = sheet.createRow(filaContador++);
					// Visualizamos nombre usuario en la Columna A (0)
					row.createCell((short) 0)
							.setCellValue(docsCheckOutsBean.getPersonBean().getApellido() + ", " + docsCheckOutsBean.getPersonBean().getNombre());
					// Visualizamos la version en la Columna B (1)
					row.createCell((short) 1).setCellValue(docsCheckOutsBean.getMayorVer() + "." + docsCheckOutsBean.getMinorVer());
					// Visializamos el nombre del documento C (2)
					row.createCell((short) 2)
							.setCellValue(docsCheckOutsBean.getNameDocument() + " " + docsCheckOutsBean.getPrefix() + docsCheckOutsBean.getNumber());
					// Visualizamos la fecha de bloqueo en la columna D (3)
					row.createCell((short) 3).setCellValue(docsCheckOutsBean.getDateCheckOut());

				}

			}

		} catch (Exception e) {

			e.printStackTrace();
		}
	}

	/**
	 * Metodo para construir la hoja "sheet" de excel de los flujos de trabajo pendientes.
	 * 
	 * @param sheet
	 * @param rb
	 * @param wfPengings
	 * @param dateSystem
	 *            buildWFPendingsSheet contruirHojaDocExpirados(
	 */
	public static void contruirHojaFujoTrabajoPendientes(HSSFSheet sheet, ResourceBundle rb, Collection<DataUserWorkFlowForm> wfPengings, String dateSystem) {
		try {

			HSSFRow fila1;
			HSSFCell celda;

			// Estableciendo Titulo de Las Hojas del Libro de Excel

			fila1 = sheet.getRow(0);
			if (fila1 != null) {

				celda = fila1.getCell((short) 2);
				celda.setCellValue(rb.getString("pendingWorkFlows.ListTitle") + " " + dateSystem);

			}

			fila1 = sheet.getRow(2);

			if (fila1 != null) {

				celda = fila1.getCell((short) 0); // columna 0
				celda.setCellValue(rb.getString("label.name"));

				celda = fila1.getCell((short) 1); // columna 1
				celda.setCellValue(rb.getString("label.version"));

				celda = fila1.getCell((short) 2); // columna 2
				celda.setCellValue(rb.getString("label.document"));

				celda = fila1.getCell((short) 3); // columna 3
				celda.setCellValue(rb.getString("label.documentType"));

				celda = fila1.getCell((short) 3); // columna 3
				celda.setCellValue(rb.getString("label.requestedBy"));
			}

			// iteramos en los resultados que tenemos
			if (wfPengings != null) {
				short i = 3, j = 0; // , styleTemplatefila = 3;
				fila1 = sheet.getRow(i);

				int filaContador = 3;
				for (DataUserWorkFlowForm wfBean : wfPengings) {

					HSSFRow row = sheet.createRow(filaContador++);
					// Visualizamos nombre usuario en la Columna A (0)
					row.createCell((short) 0).setCellValue(wfBean.getPersonBean().getApellido() + ", " + wfBean.getPersonBean().getNombre());

					// Visualizamos la version en la Columna B (1)
					row.createCell((short) 1).setCellValue(wfBean.getIdVersion());

					// Visializamos el nombre del documento C (2)
					String docName = HandlerDocuments.TypeDocumentsImpresion.equals(Integer.toString(wfBean.getTypeDOC())) ? wfBean.getNameDocument()
							: wfBean.getNameDocument() + " " + wfBean.getPrefix() + wfBean.getNumber();
					row.createCell((short) 2).setCellValue(docName);

					/// Visualizamos el tipo de flujo en la columna D (3)
					row.createCell((short) 3).setCellValue(wfBean.getNameWorkFlow());

					/// Visualizamos el nombre de la persona que solicita la respuesta en el flujo la columna E (4)
					row.createCell((short) 4)
							.setCellValue(wfBean.getFlowRequieredByPersonBean().getApellido() + ", " + wfBean.getFlowRequieredByPersonBean().getNombre());

				}

			}

		} catch (Exception e) {

			e.printStackTrace();
		}
	}

	/**
	 * Metodo para construir la hoja "sheet" de excel de los flujos de trabajo expirados y recibidos.
	 * 
	 * @param sheet
	 * @param rb
	 * @param wfPengings
	 * @param dateSystem
	 */
	public static void contruirHojaFlujoTrabajoExpiradosRecibidos(HSSFSheet sheet, ResourceBundle rb, Collection<DataUserWorkFlowForm> wfPengings,
			String dateSystem) {

		try {

			HSSFRow fila1;
			HSSFCell celda;

			// Estableciendo Titulo de Las Hojas del Libro de Excel

			fila1 = sheet.getRow(0);
			if (fila1 != null) {

				celda = fila1.getCell((short) 2);
				celda.setCellValue(rb.getString("expiredWorkFlowsReceived.ListTitle") + " " + dateSystem);

			}

			fila1 = sheet.getRow(2);

			if (fila1 != null) {

				celda = fila1.getCell((short) 0); // columna 0
				celda.setCellValue(rb.getString("label.name"));

				celda = fila1.getCell((short) 1); // columna 1
				celda.setCellValue(rb.getString("label.version"));

				celda = fila1.getCell((short) 2); // columna 2
				celda.setCellValue(rb.getString("label.document"));

				celda = fila1.getCell((short) 3); // columna 3
				celda.setCellValue(rb.getString("label.documentType"));

				celda = fila1.getCell((short) 4); // columna 4
				celda.setCellValue(rb.getString("label.expiredAt"));

				celda = fila1.getCell((short) 5); // columna 5
				celda.setCellValue(rb.getString("label.requestedBy"));
			}

			// iteramos en los resultados que tenemos

			if (wfPengings != null) {
				short i = 3, j = 0; // , styleTemplatefila = 3;
				fila1 = sheet.getRow(i);

				int filaContador = 3;
				for (DataUserWorkFlowForm wfBean : wfPengings) {

					HSSFRow row = sheet.createRow(filaContador++);

					// Visualizamos nombre usuario en la Columna A (0)
					row.createCell((short) 0).setCellValue(wfBean.getPersonBean().getApellido() + ", " + wfBean.getPersonBean().getNombre());

					// Visualizamos la version en la Columna B (1)
					row.createCell((short) 1).setCellValue(wfBean.getIdVersion());

					// Visializamos el nombre del documento C (2)
					row.createCell((short) 2).setCellValue(wfBean.getNameDocument() + " " + wfBean.getPrefix() + wfBean.getNumber());

					// Visualizamos el tipo de flujo en la columna D (3)
					row.createCell((short) 3).setCellValue(wfBean.getNameWorkFlow());

					// Visualizamos la fecha de expiracion en la columna E (4)
					row.createCell((short) 4).setCellValue(wfBean.getDateExpire());

					// Visualizamos l nombre de la persona que solicita la respuesta en el flujo en la columna F (5)
					row.createCell((short) 5)
							.setCellValue(wfBean.getFlowRequieredByPersonBean().getApellido() + ", " + wfBean.getFlowRequieredByPersonBean().getNombre());

				}
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}

	}

	/**
	 * Metodo para construir la hoja "sheet" de excel de los flujos de trabajo expirados y Enviados.
	 * 
	 * @param sheet
	 * @param rb
	 * @param wfPengings
	 * @param dateSystem
	 */
	public static void contruirHojaFlujoTrabajoExpiradosEnviados(HSSFSheet sheet, ResourceBundle rb, Collection<DataUserWorkFlowForm> wfPengings,
			String dateSystem) {

		try {
			// llenamos el libro de flujos de trabajo expirados y enviados

			HSSFRow fila1;
			HSSFCell celda;

			// Estableciendo Titulo de Las Hojas del Libro de Excel

			fila1 = sheet.getRow(0);
			if (fila1 != null) {

				celda = fila1.getCell((short) 2);
				celda.setCellValue(rb.getString("expiredWorkFlowsSent.ListTitle") + " " + dateSystem);

			}

			fila1 = sheet.getRow(2);

			if (fila1 != null) {

				celda = fila1.getCell((short) 0); // columna 0
				celda.setCellValue(rb.getString("label.name"));

				celda = fila1.getCell((short) 1); // columna 1
				celda.setCellValue(rb.getString("label.version"));

				celda = fila1.getCell((short) 2); // columna 2
				celda.setCellValue(rb.getString("label.document"));

				celda = fila1.getCell((short) 3); // columna 3
				celda.setCellValue(rb.getString("label.documentType"));

				celda = fila1.getCell((short) 4); // columna 4
				celda.setCellValue(rb.getString("label.expiredAt"));

				celda = fila1.getCell((short) 5); // columna 5
				celda.setCellValue(rb.getString("label.sentTo"));
			}

			// iteramos en los resultados que tenemos

			if (wfPengings != null) {
				short i = 3, j = 0; // , styleTemplatefila = 3;
				fila1 = sheet.getRow(i);

				int filaContador = 3;
				for (DataUserWorkFlowForm wfBean : wfPengings) {

					HSSFRow row = sheet.createRow(filaContador++);

					// Visualizamos nombre usuario.En la Columna A (0)
					row.createCell((short) 0).setCellValue(wfBean.getPersonBean().getApellido() + ", " + wfBean.getPersonBean().getNombre());

					// Visualizamos la version.En la Columna B (1)
					row.createCell((short) 1).setCellValue(wfBean.getIdVersion());

					// Visializamos el nombre del documento.En la columna C (2)
					row.createCell((short) 2).setCellValue(wfBean.getNameDocument() + " " + wfBean.getPrefix() + wfBean.getNumber());

					// Visualizamos el tipo de flujo.En la columna D (3)
					row.createCell((short) 3).setCellValue(wfBean.getNameWorkFlow());

					// Visualizamos la fecha de expiracion.En la columna E (4)
					row.createCell((short) 4).setCellValue(wfBean.getDateExpire());

					// Visualizamos l nombre de la persona que solicita la respuesta en el flujo. En la columna F (5)
					row.createCell((short) 5)
							.setCellValue(wfBean.getFlowRequieredByPersonBean().getApellido() + ", " + wfBean.getFlowRequieredByPersonBean().getNombre());

				}
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}

	}

	/**
	 * Metodo para construir la hoja "sheet" de excel de los flujos de trabajo cancelados.
	 * 
	 * @param sheet
	 * @param rb
	 * @param wfPengings
	 * @param dateSystem
	 */
	public static void contruirHojaFlujoTrabajoCancelados(HSSFSheet sheet, ResourceBundle rb, Collection<DataUserWorkFlowForm> wfCancel, String dateSystem) {

		try {
			// llenamos el libro de flujos de trabajo cancelados

			HSSFRow fila1;
			HSSFCell celda;

			// Estableciendo Titulo de Las Hojas del Libro de Excel

			fila1 = sheet.getRow(0);
			if (fila1 != null) {

				celda = fila1.getCell((short) 2);
				celda.setCellValue(rb.getString("cancelledWorkFlows.ListTitle") + " " + dateSystem);

			}

			fila1 = sheet.getRow(2);

			if (fila1 != null) {

				celda = fila1.getCell((short) 0); // columna 0
				celda.setCellValue(rb.getString("label.name"));

				celda = fila1.getCell((short) 1); // columna 1
				celda.setCellValue(rb.getString("label.version"));

				celda = fila1.getCell((short) 2); // columna 2
				celda.setCellValue(rb.getString("label.document"));

				celda = fila1.getCell((short) 3); // columna 3
				celda.setCellValue(rb.getString("label.documentType"));

				celda = fila1.getCell((short) 4); // columna 3
				celda.setCellValue(rb.getString("label.cancelledAt"));
			}

			// iteramos en los resultados que tenemos

			if (wfCancel != null) {
				int filaContador = 3;
				for (DataUserWorkFlowForm wfBean : wfCancel) {
					HSSFRow row = sheet.createRow(filaContador++);

					// Visualizamos nombre usuario en la Columna A (0)
					row.createCell((short) 0).setCellValue(wfBean.getPersonBean().getApellido() + ", " + wfBean.getPersonBean().getNombre());

					// Visualizamos la version.En la Columna B (1)
					row.createCell((short) 1).setCellValue(wfBean.getIdVersion());

					// Visializamos el nombre del documento.En La Columna C (2)
					row.createCell((short) 2).setCellValue(wfBean.getNameDocument() + " " + wfBean.getPrefix() + wfBean.getNumber());

					// Visualizamos el tipo de flujo.En la columna D (3)
					row.createCell((short) 3).setCellValue(rb.getString("wf.type" + wfBean.getTypeWF()));

					// Visualizamos la fecha de cancelacion. En la columna E (4)
					row.createCell((short) 4).setCellValue(wfBean.getDateCompleted());

				}
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}

	}

	/**
	 * Metodo para construir la hoja "sheet" de excel de los flujos Impresiones Aprobadas.
	 * 
	 * @param sheet
	 * @param rb
	 * @param approvedPrints
	 * @param dateSystem
	 */
	public static void contruirHojaFlujoImpresionesAprobadas(HSSFSheet sheet, ResourceBundle rb, Collection<DocumentsCheckOutsBean> approvedPrints,
			String dateSystem) {
		try {
			// llenamos el libro de flujos de impresion aprobados

			HSSFRow fila1;
			HSSFCell celda;
			// Estableciendo Titulo de Las Hojas del Libro de Excel

			fila1 = sheet.getRow(0);
			if (fila1 != null) {

				celda = fila1.getCell((short) 2);
				celda.setCellValue(rb.getString("printWorkFlows.ListTitle") + " " + dateSystem);

			}

			fila1 = sheet.getRow(2);

			if (fila1 != null) {

				celda = fila1.getCell((short) 0); // columna 0
				celda.setCellValue(rb.getString("label.name"));

				celda = fila1.getCell((short) 1); // columna 1
				celda.setCellValue(rb.getString("label.version"));

				celda = fila1.getCell((short) 2); // columna 2
				celda.setCellValue(rb.getString("label.document"));

				celda = fila1.getCell((short) 3); // columna 3
				celda.setCellValue(rb.getString("label.expireAt"));
			}

			// iteramos en los resultados que tenemos

			if (approvedPrints != null) {
				short i = 3, j = 0; // , styleTemplatefila = 3;
				fila1 = sheet.getRow(i);

				int filaContador = 3;
				for (DocumentsCheckOutsBean docsCheckOutsBean : approvedPrints) {

					HSSFRow row = sheet.createRow(filaContador++);
					// Visualizamos nombre usuario.En la Columna A (0)
					row.createCell((short) 0)
							.setCellValue(docsCheckOutsBean.getPersonBean().getApellido() + ", " + docsCheckOutsBean.getPersonBean().getNombre());

					// Visualizamos la version.En la Columna B (1)
					row.createCell((short) 1).setCellValue(docsCheckOutsBean.getMayorVer() + "." + docsCheckOutsBean.getMinorVer());

					// Visializamos el nombre del documento.En la Columna C (2)
					row.createCell((short) 2)
							.setCellValue(docsCheckOutsBean.getNameDocument() + " " + docsCheckOutsBean.getPrefix() + docsCheckOutsBean.getNumber());

					// Visualizamos la fecha de bloqueo.En la columna D (3)
					row.createCell((short) 3).setCellValue(docsCheckOutsBean.getDateCheckOut());

				}

			}

		} catch (Exception e) {

			e.printStackTrace();
		}
	}

	/**
	 * Metodo para construir la hoja "sheet" de excel hoja de flujo de impresion canceladas
	 * 
	 * @param sheet
	 * @param rb
	 * @param canceladPrints
	 * @param dateSystem
	 */
	public static void contruirHojaFlujoImpresionCanceladas(HSSFSheet sheet, ResourceBundle rb, Collection<DocumentsCheckOutsBean> canceladPrints, String dateSystem) {
		try {
			
			// llenamos el libro de flujos de impresion rechazadas 

			HSSFRow fila1;
			HSSFCell celda;
			// Estableciendo Titulo de Las Hojas del Libro de Excel

			fila1 = sheet.getRow(0);
			if (fila1 != null) {

				celda = fila1.getCell((short) 2);
				celda.setCellValue(rb.getString("printWorkFlowsCancelled.ListTitle") + " " + dateSystem);

			}
			

			fila1 = sheet.getRow(2);
		

			if (fila1 != null) {

				celda = fila1.getCell((short) 0); // columna 0
				celda.setCellValue(rb.getString("label.name"));

				celda = fila1.getCell((short) 1); // columna 1
				celda.setCellValue(rb.getString("label.version"));

				celda = fila1.getCell((short) 2); // columna 2
				celda.setCellValue(rb.getString("label.document"));

				celda = fila1.getCell((short) 3); // columna 3
				celda.setCellValue(rb.getString("label.cancelledAt"));
			}
		

			// iteramos en los resultados que tenemos

			if ( canceladPrints != null) {
				short i = 3, j = 0; // , styleTemplatefila = 3;
				fila1 = sheet.getRow(i);

				int filaContador = 3;
				for (DocumentsCheckOutsBean docsCheckOutsBean : canceladPrints) {

					HSSFRow row = sheet.createRow(filaContador++);
					// Visualizamos nombre usuario.En la Columna A (0)
					row.createCell((short) 0)
							.setCellValue(docsCheckOutsBean.getPersonBean().getApellido() + ", " + docsCheckOutsBean.getPersonBean().getNombre());

					// Visualizamos la version.En la Columna B (1)
					row.createCell((short) 1).setCellValue(docsCheckOutsBean.getMayorVer() + "." + docsCheckOutsBean.getMinorVer());

					// Visializamos el nombre del documento.En la Columna C (2)
					row.createCell((short) 2)
							.setCellValue(docsCheckOutsBean.getNameDocument() + " " + docsCheckOutsBean.getPrefix() + docsCheckOutsBean.getNumber());

					// Visualizamos Codigo.En la columna D (3)
					row.createCell((short) 3).setCellValue(docsCheckOutsBean.getPrefix() + docsCheckOutsBean.getNumber());

				}

			}

			
		} catch (Exception e) {

			e.printStackTrace();
		}
	}
	
	/**
	 * Metodo para construir la hoja "sheet" de excel de la Listas de Divulgacion
	 * 
	 * @param sheet
	 * @param rb
	 * @param distList
	 * @param dateSystem
	 */
	public static void contruirHojaListDistribucion(HSSFSheet sheet, ResourceBundle rb, Collection<DocumentsCheckOutsBean> distList, String dateSystem) {
			try {
				// llenamos el libro de listas de distribucion

				HSSFRow fila1;
				HSSFCell celda;
				// Estableciendo Titulo de Las Hojas del Libro de Excel

				fila1 = sheet.getRow(0);
				if (fila1 != null) {

					celda = fila1.getCell((short) 2);
					celda.setCellValue(rb.getString("distributionList.ListTitle") + " " + dateSystem);

				}

				fila1 = sheet.getRow(2);

				if (fila1 != null) {

					celda = fila1.getCell((short) 0); // columna 0
					celda.setCellValue(rb.getString("label.name"));

					celda = fila1.getCell((short) 1); // columna 1
					celda.setCellValue(rb.getString("label.version"));

					celda = fila1.getCell((short) 2); // columna 2
					celda.setCellValue(rb.getString("label.document"));

					celda = fila1.getCell((short) 3); // columna 3
					celda.setCellValue(rb.getString("label.code"));
				}

				// iteramos en los resultados que tenemos

				if (distList != null) {
					short i = 3, j = 0; // , styleTemplatefila = 3;
					fila1 = sheet.getRow(i);

					int filaContador = 3;
					for (DocumentsCheckOutsBean docsCheckOutsBean : distList) {

						HSSFRow row = sheet.createRow(filaContador++);
						// Visualizamos nombre usuario.En la Columna A (0)
						row.createCell((short) 0)
								.setCellValue(docsCheckOutsBean.getPersonBean().getApellido() + ", " + docsCheckOutsBean.getPersonBean().getNombre());

						// Visualizamos la version.En la Columna B (1)
						row.createCell((short) 1).setCellValue(docsCheckOutsBean.getMayorVer() + "." + docsCheckOutsBean.getMinorVer());

						// Visializamos el nombre del documento.En la Columna C (2)
						row.createCell((short) 2)
								.setCellValue(docsCheckOutsBean.getNameDocument() + " " + docsCheckOutsBean.getPrefix() + docsCheckOutsBean.getNumber());

						// Visualizamos Codigo.En la columna D (3)
						row.createCell((short) 3).setCellValue(docsCheckOutsBean.getDateCheckOut());

					}

				}

			} catch (Exception e) {

				e.printStackTrace();
			}
		}
}