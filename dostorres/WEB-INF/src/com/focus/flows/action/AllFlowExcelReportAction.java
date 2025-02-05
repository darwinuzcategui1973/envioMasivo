package com.focus.flows.action;

import java.io.File;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.Collection;
import java.util.Date;
import java.util.ResourceBundle;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import jxl.Workbook;
import jxl.write.WritableWorkbook;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.desige.webDocuments.document.forms.DocumentsCheckOutsBean;
import com.desige.webDocuments.utils.ToolsHTML;
import com.desige.webDocuments.utils.beans.SuperAction;
import com.desige.webDocuments.workFlows.forms.DataUserWorkFlowForm;
import com.focus.util.BuildWorkBookFlowReport;


/**
 * Implementacion que permitira obtener un archivo excel con toda la informacion relacionada
 * a los distintos flujos de trabajo del sistema y a los usuarios del mismo.
 * <br>
 * Title: AllFlowExcelReport.java <br>
 * Copyright: (c) 2012 Focus Consulting C.A.<br>
 * @author Ing. Felipe Rojas (FJR)
 * 
 * @version WebDocuments v4.5.2
 * <br>
 *     Changes:<br>
 * <ul>
 *     <li> 26/03/2012 (FJR) Creation </li>
 * <ul>
 */
public final class AllFlowExcelReportAction extends SuperAction{
	private static Logger log = LoggerFactory.getLogger(AllFlowExcelReportAction.class.getName());
    private ResourceBundle rb = null;
    private String dateSystem = ToolsHTML.sdf.format(new Date());
    
	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
		// TODO Auto-generated method stub
		log.info("Iniciamos metodo execute()");
		log.info("Mapa de caracteres por defecto: '" + Charset.defaultCharset().name() 
				+ "', este mapa debe ser compatible con el mapa de caracteres del template de Excel para este reporte");
		
		super.init(mapping,form,request,response);
		OutputStream out = null;
		
		try {
			
			//obtenemos el bundle en base a la preferencia de lenguaje del usuario
    		rb = ToolsHTML.getBundle(request);
    		WritableWorkbook workbook = getFinalWorkBook(request, response);
    		out = response.getOutputStream();
    		
    		if(workbook != null){
    			log.info("Iniciando envio de archivo excel generado dinamicamente rb=" + rb);
    			
    			response.setContentType("application/vnd.ms-excel");
    			String name = (rb == null ? "ListadoFlujosDelSistema.xls" : rb.getString("label.fileResult"));
    			response.setHeader("Content-disposition", "attachment; filename=".concat(name));
    			response.setHeader("content-transfer-encoding", "binary");
    			
    			workbook.write();
    			workbook.close();
    			out.flush();
    			
    			log.info("Finalizado envio de archivo excel generado dinamicamente");
    		}
		} catch (Exception e) {
			// TODO: handle exception
			log.error("Error al generar archivo excel descargable. Error fue: "
					+ e.getLocalizedMessage(), e);
			e.printStackTrace();
		} finally {
			try {
				out.close();
			} catch (Exception e2) {
				// TODO: handle exception
			}
		}
		
		return null;
	}
	
	/**
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	private WritableWorkbook getFinalWorkBook(HttpServletRequest request,
			HttpServletResponse response){
		final String path = ToolsHTML.getPath().concat("estilo");
        final String nameFile = path.concat(File.separator).concat("FormatoListaFlujosDocumentos.xls");
        System.out.println("XLS: "+nameFile);
        WritableWorkbook workbook;
        
        try {
            workbook = Workbook.createWorkbook(response.getOutputStream(),
       				Workbook.getWorkbook(new File(nameFile)));  // jxl.read.biff.BiffException: Unable to recognize OLE stream
            
       		workbook = buildWorkBook(request, workbook);
		} catch (Exception e) {
			// TODO: handle exception
			log.error("Error: " + e.getMessage(), e);
			e.printStackTrace();
			workbook = null;
		} 
        
        return workbook;
	}
	
	/**
	 * 
	 * @param request
	 * @param workbook
	 * @return workbook creado en base a los insumos actuales o null si hubo algun error.
	 * 
	 */
	private WritableWorkbook buildWorkBook(HttpServletRequest request, WritableWorkbook workbook){
		try {
			HttpSession session = request.getSession();
			log.info("Iniciamos metodo buildWorkBook()");
			
			BuildWorkBookFlowReport.buildDocPendingSheet(workbook.getSheet(0),
					rb,
					(Collection<DocumentsCheckOutsBean>) session.getAttribute("docCheckOuts"),
					dateSystem);
			log.info("Construido sheet para documentos pendientes");
			
			BuildWorkBookFlowReport.buildDocExpiresSheet(workbook.getSheet(1),
					rb,
					(Collection<DocumentsCheckOutsBean>) session.getAttribute("docExpires"),
					dateSystem);
			log.info("Construido sheet para documentos expirados");
			
			BuildWorkBookFlowReport.buildWFPendingsSheet(workbook.getSheet(2),
					rb,
					(Collection<DataUserWorkFlowForm>) session.getAttribute("wfPendings"),
					dateSystem);
			log.info("Construido sheet para flujos de trabajos pendientes");
			
			BuildWorkBookFlowReport.buildWFExpiredReceivedSheet(workbook.getSheet(3),
					rb,
					(Collection<DataUserWorkFlowForm>) session.getAttribute("wfExpires"),
					dateSystem);
			log.info("Construido sheet para flujos de trabajos expirados recibidos");
			
			BuildWorkBookFlowReport.buildWFExpiredOriginatedSheet(workbook.getSheet(4),
					rb,
					(Collection<DataUserWorkFlowForm>) session.getAttribute("wfExpiresOwner"),
					dateSystem);
			log.info("Construido sheet para flujos de trabajos expirados enviados");
					
			BuildWorkBookFlowReport.buildWFCancelledSheet(workbook.getSheet(5),
					rb,
					(Collection<DataUserWorkFlowForm>) session.getAttribute("wfCanceled"),
					dateSystem);
			log.info("Construido sheet para flujos de trabajos cancelados");
			
			BuildWorkBookFlowReport.buildWFPrintApprovedSheet(workbook.getSheet(6),
					rb,
					(Collection<DocumentsCheckOutsBean>) session.getAttribute("wfPrintApproved"),
					dateSystem);
			log.info("Construido sheet para flujos de impresion aprobados");
			
			BuildWorkBookFlowReport.buildDistListSheet(workbook.getSheet(7),
					rb,
					(Collection<DocumentsCheckOutsBean>) session.getAttribute("docVersionApproved"),
					dateSystem);
			log.info("Construido sheet para listas de divulgacion");
			
			log.info("Fin de metodo buildWorkBook()");
		} catch (Exception e) {
			// TODO: handle exception
			log.error("Error construyendo el reporte de la seccion principal. Error: " + e.getLocalizedMessage(), e);
			workbook = null;
		}
		
		return workbook;
	}
}
