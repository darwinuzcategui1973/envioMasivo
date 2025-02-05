package com.desige.webDocuments.excel;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;

import java.util.Collection;
import java.util.Date;
import java.util.ResourceBundle;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import com.desige.webDocuments.document.forms.DocumentsCheckOutsBean;
import com.desige.webDocuments.utils.ToolsHTML;
import com.desige.webDocuments.workFlows.forms.DataUserWorkFlowForm;
import com.focus.util.BuildWorkBookFlowReport;
import com.focus.util.ContruirHojasReportePrincipalFlujoTrabajo;

/**
 * Clase Para Crear Reporte Principal de Flujo de Trabajo Este reporte solo debe ser visible a los usuarios del grupo de administracion <br>
 * 
 * Title:GenerarLibroExcelPrincipal.java <br>
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
public class GenerarLibroExcelPrincipal {

	public String error;
	private static Logger log = LoggerFactory.getLogger(CrearReportePrincipalAction.class.getName());

	// private ResourceBundle rb = null;

	/**
	 * Metodo para Generar Libro de Excel
	 * 
	 * @param request
	 * @param response
	 */
	public static void generarLibroExcel(HttpServletRequest request, HttpServletResponse response) throws Exception {

		ResourceBundle rb = null;
		String dateSystem = ToolsHTML.sdf.format(new Date());

		String path = ToolsHTML.getPath().concat("estilo");
		String nameFile = path.concat(File.separator).concat("AFormatoListaFlujosDocumentos.xls");
		InputStream inputfile = new FileInputStream(nameFile);
		HSSFWorkbook workbook = new HSSFWorkbook(inputfile, true);

		rb = ToolsHTML.getBundle(request);

		HttpSession session = request.getSession();

		log.info("Iniciamos metodo Contruir Las hojas de Libro de Excel");

		// HOJA 0
		ContruirHojasReportePrincipalFlujoTrabajo.contruirHojaDocPendiente(workbook.getSheetAt(0), rb,
				(Collection<DocumentsCheckOutsBean>) session.getAttribute("docCheckOuts"), dateSystem);

		workbook.setSheetName(0, rb.getString("pendingDocuments.sheetName"));
		log.info("Construido sheet para documentos pendientes");

		// HOJA 1
		ContruirHojasReportePrincipalFlujoTrabajo.contruirHojaDocExpirados(workbook.getSheetAt(1), rb,
				(Collection<DocumentsCheckOutsBean>) session.getAttribute("docExpires"), dateSystem);

		workbook.setSheetName(1, rb.getString("expiredDocuments.sheetName"));
		log.info("Construido sheet para documentos expirados");

		// HOJA 2
		ContruirHojasReportePrincipalFlujoTrabajo.contruirHojaFujoTrabajoPendientes(workbook.getSheetAt(2), rb,
				(Collection<DataUserWorkFlowForm>) session.getAttribute("wfPendings"), dateSystem);

		workbook.setSheetName(2, rb.getString("pendingWorkFlows.sheetName"));
		log.info("Construido sheet para flujos de trabajos pendientes");

		// HOJA 3

		ContruirHojasReportePrincipalFlujoTrabajo.contruirHojaFlujoTrabajoExpiradosRecibidos(workbook.getSheetAt(3), rb,
				(Collection<DataUserWorkFlowForm>) session.getAttribute("wfExpires"), dateSystem);

		workbook.setSheetName(3, rb.getString("expiredWorkFlowsReceived.sheetName"));
		log.info("Construido sheet para flujos de trabajos expirados recibidos");

		// HOJA 4

		ContruirHojasReportePrincipalFlujoTrabajo.contruirHojaFlujoTrabajoExpiradosEnviados(workbook.getSheetAt(4), rb,
				(Collection<DataUserWorkFlowForm>) session.getAttribute("wfExpiresOwner"), dateSystem);

		workbook.setSheetName(4, rb.getString("expiredWorkFlowsSent.sheetName"));
		log.info("Construido sheet para flujos de trabajos expirados enviados");

		// HOJA 5
	
		ContruirHojasReportePrincipalFlujoTrabajo.contruirHojaFlujoTrabajoCancelados(workbook.getSheetAt(5), rb,
				(Collection<DataUserWorkFlowForm>) session.getAttribute("wfCanceled"), dateSystem);

		workbook.setSheetName(5, rb.getString("cancelledWorkFlows.sheetName"));
		log.info("Construido sheet para flujos de trabajos cancelados");

		// HOJA 6

		ContruirHojasReportePrincipalFlujoTrabajo.contruirHojaFlujoImpresionesAprobadas(workbook.getSheetAt(6), rb,
				(Collection<DocumentsCheckOutsBean>) session.getAttribute("wfPrintApproved"), dateSystem);
		workbook.setSheetName(6, rb.getString("printWorkFlows.sheetName"));
		log.info("Construido sheet para flujos de impresion aprobados");

		// HOJA 7
		

		ContruirHojasReportePrincipalFlujoTrabajo.contruirHojaFlujoImpresionCanceladas(workbook.getSheetAt(7), rb,
				(Collection<DocumentsCheckOutsBean>) session.getAttribute("wfPrintCanceled"), dateSystem);
		workbook.setSheetName(7, rb.getString("printWorkFlowsCancelled.sheetName"));
		log.info("Construido sheet para Flujo de impresión cancelados");
		
	
		// HOJA 8

		ContruirHojasReportePrincipalFlujoTrabajo.contruirHojaListDistribucion(workbook.getSheetAt(8), rb,
				(Collection<DocumentsCheckOutsBean>) session.getAttribute("docVersionApproved"), dateSystem);

		workbook.setSheetName(8, rb.getString("distributionList.sheetName"));
		
		log.info("Construido sheet para listas de divulgacion");
		log.info("Fin de metodo contruirHojaReportesPrincipalFlujoTrabajo()");

		// Enviando el Archivo al Cliente
		log.debug("Envio Reportes de Excel al Cliente");
		response.setContentType("application/vnd.ms-excel");
		response.setHeader("Content-disposition", "attachment; filename=AListaFlujosDocumentos.xls");
		response.setHeader("content-transfer-encoding", "binary");
		OutputStream out = response.getOutputStream();
		workbook.write(out);
		out.flush();
		out.close();

	}

}
