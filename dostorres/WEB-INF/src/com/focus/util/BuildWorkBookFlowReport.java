package com.focus.util;

import java.util.Collection;
import java.util.ResourceBundle;

import com.desige.webDocuments.document.forms.DocumentsCheckOutsBean;
import com.desige.webDocuments.persistent.managers.HandlerDocuments;
import com.desige.webDocuments.workFlows.forms.DataUserWorkFlowForm;

import jxl.write.Label;
import jxl.write.WritableSheet;

/**
 * Clase utilitaria para encapsular la logica de creacion de los libros "sheets"
 * asociados al reporte principal con la informacion de los flujos y usuarios del sistema.
 * Este reporte solo debe ser visible a los usuarios del grupo de administracion <br>
 * 
 * Title: BuildWorkBookFlowReport.java <br>
 * Copyright: (c) 2012 Focus Consulting C.A.<br/>
 * @author Ing. Felipe Rojas (FJR)
 * 
 * @version WebDocuments v4.5.2
 * <br>
 *     Changes:<br>
 * <ul>
 *     <li> 26/03/2012 (FJR) Creation </li>
 * <ul>
 */
public final class BuildWorkBookFlowReport {
	
	/**
	 * Metodo para construir el libro "sheet" de excel asociado a los documentos pendientes.
	 * 
	 * @param sheet
	 * @param rb
	 * @param docCheckOuts
	 * @param dateSystem
	 */
	public static void buildDocPendingSheet(WritableSheet sheet, ResourceBundle rb,
			Collection<DocumentsCheckOutsBean> docPendings, String dateSystem){
		try {
			//llenamos el libro de documentos pendientes
			sheet.setName(rb.getString("pendingDocuments.sheetName"));
			//la numeracion es columna, fila
			sheet.addCell(new Label(2, 0, rb.getString("pendingDocuments.ListTitle")
					+ " " + dateSystem, sheet.getCell(2, 0).getCellFormat()));
			sheet.addCell(new Label(0, 2, rb.getString("label.name"),
					sheet.getCell(0, 2).getCellFormat()));
			sheet.addCell(new Label(1, 2, rb.getString("label.version"), 
					sheet.getCell(1, 2).getCellFormat()));
			sheet.addCell(new Label(2, 2, rb.getString("label.document"), 
					sheet.getCell(2, 2).getCellFormat()));
			sheet.addCell(new Label(3, 2, rb.getString("label.blockedAt"), 
					sheet.getCell(3, 2).getCellFormat()));
			
			//iteramos en los resultados que tenemos
			if(docPendings != null){
				int i = 3, j = 0, styleTemplateRow = 3;
				for (DocumentsCheckOutsBean docsCheckOutsBean : docPendings) {
					//colocamos el nombre
					sheet.addCell(new Label(j, i, 
							docsCheckOutsBean.getPersonBean().getApellido() + ", " + docsCheckOutsBean.getPersonBean().getNombre(), 
							sheet.getCell(j, styleTemplateRow).getCellFormat()));
					j++;
					//colocamos la version
					sheet.addCell(new Label(j, i, 
							docsCheckOutsBean.getMayorVer() + "." + docsCheckOutsBean.getMinorVer(), 
							sheet.getCell(j, styleTemplateRow).getCellFormat()));
					j++;
					//colocamos el nombre del documento
					sheet.addCell(new Label(j, i, 
							docsCheckOutsBean.getNameDocument() + " " + docsCheckOutsBean.getPrefix() + docsCheckOutsBean.getNumber(), 
							sheet.getCell(j, styleTemplateRow).getCellFormat()));
					j++;
					//colocamos la fecha de bloqueo
					sheet.addCell(new Label(j, i, 
							docsCheckOutsBean.getDateCheckOut(), 
							sheet.getCell(j, styleTemplateRow).getCellFormat()));
					
					j = 0;
					i ++;
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}
	
	/**
	 * Metodo para construir el libro "sheet" de excel asociado a los documentos expirados.
	 * 
	 * @param sheet
	 * @param rb
	 * @param docCheckOuts
	 * @param dateSystem
	 */
	public static void buildDocExpiresSheet(WritableSheet sheet, ResourceBundle rb,
			Collection<DocumentsCheckOutsBean> docExpires, String dateSystem){
		try {
			//llenamos el libro de documentos expirados
			sheet.setName(rb.getString("expiredDocuments.sheetName"));
			//la numeracion es columna, fila
			sheet.addCell(new Label(2, 0, rb.getString("expiredDocuments.ListTitle")
					+ " " + dateSystem, sheet.getCell(2, 0).getCellFormat()));
			sheet.addCell(new Label(0, 2, rb.getString("label.name"),
					sheet.getCell(0, 2).getCellFormat()));
			sheet.addCell(new Label(1, 2, rb.getString("label.version"), 
					sheet.getCell(1, 2).getCellFormat()));
			sheet.addCell(new Label(2, 2, rb.getString("label.document"), 
					sheet.getCell(2, 2).getCellFormat()));
			sheet.addCell(new Label(3, 2, rb.getString("label.blockedAt"), 
					sheet.getCell(3, 2).getCellFormat()));
			
			//iteramos en los resultados que tenemos
			if(docExpires != null){
				int i = 3, j = 0, styleTemplateRow = 3;
				for (DocumentsCheckOutsBean docsCheckOutsBean : docExpires) {
					//colocamos el nombre
					sheet.addCell(new Label(j, i, 
							docsCheckOutsBean.getPersonBean().getApellido() + ", " + docsCheckOutsBean.getPersonBean().getNombre(), 
							sheet.getCell(j, styleTemplateRow).getCellFormat()));
					j++;
					//colocamos la version
					sheet.addCell(new Label(j, i, 
							docsCheckOutsBean.getMayorVer() + "." + docsCheckOutsBean.getMinorVer(), 
							sheet.getCell(j, styleTemplateRow).getCellFormat()));
					j++;
					//colocamos el nombre del documento
					sheet.addCell(new Label(j, i, 
							docsCheckOutsBean.getNameDocument() + " " + docsCheckOutsBean.getPrefix() + docsCheckOutsBean.getNumber(), 
							sheet.getCell(j, styleTemplateRow).getCellFormat()));
					j++;
					//colocamos la fecha de bloqueo
					sheet.addCell(new Label(j, i, 
							docsCheckOutsBean.getDateCheckOut(), 
							sheet.getCell(j, styleTemplateRow).getCellFormat()));
					
					j = 0;
					i ++;
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}
	
	/**
	 * Metodo para construir el "sheet" asociado a los flujos de trabajo pendientes.
	 * 
	 * @param sheet
	 * @param rb
	 * @param wfPengings
	 * @param dateSystem
	 */
	public static void buildWFPendingsSheet(WritableSheet sheet, ResourceBundle rb,
			Collection<DataUserWorkFlowForm> wfPengings, String dateSystem){
		try {
			//llenamos el libro de flujos de trabajo pendientes
			sheet.setName(rb.getString("pendingWorkFlows.sheetName"));
			//la numeracion es columna, fila
			sheet.addCell(new Label(2, 0, rb.getString("pendingWorkFlows.ListTitle")
					+ " " + dateSystem, sheet.getCell(2, 0).getCellFormat()));
			sheet.addCell(new Label(0, 2, rb.getString("label.name"),
					sheet.getCell(0, 2).getCellFormat()));
			sheet.addCell(new Label(1, 2, rb.getString("label.version"), 
					sheet.getCell(1, 2).getCellFormat()));
			sheet.addCell(new Label(2, 2, rb.getString("label.document"), 
					sheet.getCell(2, 2).getCellFormat()));
			sheet.addCell(new Label(3, 2, rb.getString("label.documentType"), 
					sheet.getCell(3, 2).getCellFormat()));
			sheet.addCell(new Label(4, 2, rb.getString("label.requestedBy"), 
					sheet.getCell(4, 2).getCellFormat()));
			
			//iteramos en los resultados que tenemos
			if(wfPengings != null){
				int i = 3, j = 0, styleTemplateRow = 3;
				for (DataUserWorkFlowForm wfBean : wfPengings) {
					//colocamos el nombre
					sheet.addCell(new Label(j, i, 
							wfBean.getPersonBean().getApellido() + ", " + wfBean.getPersonBean().getNombre(), 
							sheet.getCell(j, styleTemplateRow).getCellFormat()));
					j++;
					
					//colocamos la version
					sheet.addCell(new Label(j, i, 
							wfBean.getIdVersion(), 
							sheet.getCell(j, styleTemplateRow).getCellFormat()));
					j++;
					
					//colocamos el nombre del documento
					String docName = HandlerDocuments.TypeDocumentsImpresion.equals(Integer.toString(wfBean.getTypeDOC())) ?
							wfBean.getNameDocument() : wfBean.getNameDocument() + " "  + wfBean.getPrefix() + wfBean.getNumber();
					sheet.addCell(new Label(j, i, 
							docName, 
							sheet.getCell(j, styleTemplateRow).getCellFormat()));
					j++;
					
					//colocamos el tipo de flujo
					sheet.addCell(new Label(j, i, 
							wfBean.getNameWorkFlow(), 
							sheet.getCell(j, styleTemplateRow).getCellFormat()));
					j++;
					
					//colocamos el nombre de la persona que solicita la respuesta en el flujo
					sheet.addCell(new Label(j, i, 
							wfBean.getFlowRequieredByPersonBean().getApellido() + ", " + wfBean.getFlowRequieredByPersonBean().getNombre(), 
							sheet.getCell(j, styleTemplateRow).getCellFormat()));
					
					j = 0;
					i ++;
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}
	
	/**
	 * Metodo para construir el "sheet" asociado a los flujos de trabajo expirados y recibidos.
	 * 
	 * @param sheet
	 * @param rb
	 * @param wfPengings
	 * @param dateSystem
	 */
	public static void buildWFExpiredReceivedSheet(WritableSheet sheet, ResourceBundle rb,
			Collection<DataUserWorkFlowForm> wfPengings, String dateSystem){
		try {
			//llenamos el libro de flujos expirados recibidos
			sheet.setName(rb.getString("expiredWorkFlowsReceived.sheetName"));
			//la numeracion es columna, fila
			sheet.addCell(new Label(2, 0, rb.getString("expiredWorkFlowsReceived.ListTitle")
					+ " " + dateSystem, sheet.getCell(2, 0).getCellFormat()));
			sheet.addCell(new Label(0, 2, rb.getString("label.name"),
					sheet.getCell(0, 2).getCellFormat()));
			sheet.addCell(new Label(1, 2, rb.getString("label.version"), 
					sheet.getCell(1, 2).getCellFormat()));
			sheet.addCell(new Label(2, 2, rb.getString("label.document"), 
					sheet.getCell(2, 2).getCellFormat()));
			sheet.addCell(new Label(3, 2, rb.getString("label.documentType"), 
					sheet.getCell(3, 2).getCellFormat()));
			sheet.addCell(new Label(4, 2, rb.getString("label.expiredAt"), 
					sheet.getCell(4, 2).getCellFormat()));
			sheet.addCell(new Label(5, 2, rb.getString("label.requestedBy"), 
					sheet.getCell(5, 2).getCellFormat()));
			
			//iteramos en los resultados que tenemos
			if(wfPengings != null){
				int i = 3, j = 0, styleTemplateRow = 3;
				for (DataUserWorkFlowForm wfBean : wfPengings) {
					//colocamos el nombre
					sheet.addCell(new Label(j, i, 
							wfBean.getPersonBean().getApellido() + ", " + wfBean.getPersonBean().getNombre(), 
							sheet.getCell(j, styleTemplateRow).getCellFormat()));
					j++;
					
					//colocamos la version
					sheet.addCell(new Label(j, i, 
							wfBean.getIdVersion(), 
							sheet.getCell(j, styleTemplateRow).getCellFormat()));
					j++;
					
					//colocamos el nombre del documento
					sheet.addCell(new Label(j, i, 
							wfBean.getNameDocument() + " " + wfBean.getPrefix() + wfBean.getNumber(), 
							sheet.getCell(j, styleTemplateRow).getCellFormat()));
					j++;
					
					//colocamos el tipo de flujo
					sheet.addCell(new Label(j, i, 
							wfBean.getNameWorkFlow(), 
							sheet.getCell(j, styleTemplateRow).getCellFormat()));
					j++;
					
					//colocamos la fecha de expiracion
					sheet.addCell(new Label(j, i, 
							wfBean.getDateExpire(), 
							sheet.getCell(j, styleTemplateRow).getCellFormat()));
					j++;
					
					//colocamos el nombre de la persona que solicita la respuesta en el flujo
					sheet.addCell(new Label(j, i, 
							wfBean.getFlowRequieredByPersonBean().getApellido() + ", " + wfBean.getFlowRequieredByPersonBean().getNombre(), 
							sheet.getCell(j, styleTemplateRow).getCellFormat()));
					
					j = 0;
					i ++;
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}
	
	/**
	 * Metodo para construir el "sheet" asociado a los flujos de trabajo expirados y creados por los usuarios.
	 * 
	 * @param sheet
	 * @param rb
	 * @param wfPengings
	 * @param dateSystem
	 */
	public static void buildWFExpiredOriginatedSheet(WritableSheet sheet, ResourceBundle rb,
			Collection<DataUserWorkFlowForm> wfPengings, String dateSystem){
		try {
			//llenamos el libro de flujos de trabajo expirados y enviados
			sheet.setName(rb.getString("expiredWorkFlowsSent.sheetName"));
			//la numeracion es columna, fila
			sheet.addCell(new Label(2, 0, rb.getString("expiredWorkFlowsSent.ListTitle")
					+ " " + dateSystem, sheet.getCell(2, 0).getCellFormat()));
			sheet.addCell(new Label(0, 2, rb.getString("label.name"),
					sheet.getCell(0, 2).getCellFormat()));
			sheet.addCell(new Label(1, 2, rb.getString("label.version"), 
					sheet.getCell(1, 2).getCellFormat()));
			sheet.addCell(new Label(2, 2, rb.getString("label.document"), 
					sheet.getCell(2, 2).getCellFormat()));
			sheet.addCell(new Label(3, 2, rb.getString("label.documentType"), 
					sheet.getCell(3, 2).getCellFormat()));
			sheet.addCell(new Label(4, 2, rb.getString("label.expiredAt"), 
					sheet.getCell(4, 2).getCellFormat()));
			sheet.addCell(new Label(5, 2, rb.getString("label.sentTo"), 
					sheet.getCell(5, 2).getCellFormat()));
			
			//iteramos en los resultados que tenemos
			if(wfPengings != null){
				int i = 3, j = 0, styleTemplateRow = 3;
				for (DataUserWorkFlowForm wfBean : wfPengings) {
					//colocamos el nombre
					sheet.addCell(new Label(j, i, 
							wfBean.getPersonBean().getApellido() + ", " + wfBean.getPersonBean().getNombre(), 
							sheet.getCell(j, styleTemplateRow).getCellFormat()));
					j++;
					
					//colocamos la version
					sheet.addCell(new Label(j, i, 
							wfBean.getIdVersion(), 
							sheet.getCell(j, styleTemplateRow).getCellFormat()));
					j++;
					
					//colocamos el nombre del documento
					sheet.addCell(new Label(j, i, 
							wfBean.getNameDocument() + " " + wfBean.getPrefix() + wfBean.getNumber(), 
							sheet.getCell(j, styleTemplateRow).getCellFormat()));
					j++;
					
					//colocamos el tipo de flujo
					sheet.addCell(new Label(j, i, 
							wfBean.getNameWorkFlow(), 
							sheet.getCell(j, styleTemplateRow).getCellFormat()));
					j++;
					
					//colocamos la fecha de expiracion
					sheet.addCell(new Label(j, i, 
							wfBean.getDateExpire(), 
							sheet.getCell(j, styleTemplateRow).getCellFormat()));
					j++;
					
					//colocamos el nombre de la persona que solicita la respuesta en el flujo
					sheet.addCell(new Label(j, i, 
							wfBean.getFlowRequieredByPersonBean().getApellido() + ", " + wfBean.getFlowRequieredByPersonBean().getNombre(), 
							sheet.getCell(j, styleTemplateRow).getCellFormat()));
					
					j = 0;
					i ++;
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}
	
	/**
	 * Metodo para construir el "sheet" asociado a los flujos de trabajo cancelados.
	 * 
	 * @param sheet
	 * @param rb
	 * @param wfPengings
	 * @param dateSystem
	 */
	public static void buildWFCancelledSheet(WritableSheet sheet, ResourceBundle rb,
			Collection<DataUserWorkFlowForm> wfCancel, String dateSystem){
		try {
			//llenamos el libro de flujos de trabajo cancelados
			sheet.setName(rb.getString("cancelledWorkFlows.sheetName"));
			//la numeracion es columna, fila
			sheet.addCell(new Label(2, 0, rb.getString("cancelledWorkFlows.ListTitle")
					+ " " + dateSystem, sheet.getCell(2, 0).getCellFormat()));
			sheet.addCell(new Label(0, 2, rb.getString("label.name"),
					sheet.getCell(0, 2).getCellFormat()));
			sheet.addCell(new Label(1, 2, rb.getString("label.version"), 
					sheet.getCell(1, 2).getCellFormat()));
			sheet.addCell(new Label(2, 2, rb.getString("label.document"), 
					sheet.getCell(2, 2).getCellFormat()));
			sheet.addCell(new Label(3, 2, rb.getString("label.documentType"), 
					sheet.getCell(3, 2).getCellFormat()));
			sheet.addCell(new Label(4, 2, rb.getString("label.cancelledAt"), 
					sheet.getCell(4, 2).getCellFormat()));
			
			//iteramos en los resultados que tenemos
			if(wfCancel != null){
				int i = 3, j = 0, styleTemplateRow = 3;
				for (DataUserWorkFlowForm wfBean : wfCancel) {
					//colocamos el nombre
					sheet.addCell(new Label(j, i, 
							wfBean.getPersonBean().getApellido() + ", " + wfBean.getPersonBean().getNombre(), 
							sheet.getCell(j, styleTemplateRow).getCellFormat()));
					j++;
					
					//colocamos la version
					sheet.addCell(new Label(j, i, 
							wfBean.getIdVersion(), 
							sheet.getCell(j, styleTemplateRow).getCellFormat()));
					j++;
					
					//colocamos el nombre del documento
					sheet.addCell(new Label(j, i, 
							wfBean.getNameDocument() + " " + wfBean.getPrefix() + wfBean.getNumber(), 
							sheet.getCell(j, styleTemplateRow).getCellFormat()));
					j++;
					
					//colocamos el tipo de flujo
					sheet.addCell(new Label(j, i, 
							rb.getString("wf.type" + wfBean.getTypeWF()), 
							sheet.getCell(j, styleTemplateRow).getCellFormat()));
					j++;
					
					//colocamos la fecha de cancelacion
					sheet.addCell(new Label(j, i, 
							wfBean.getDateCompleted(), 
							sheet.getCell(j, styleTemplateRow).getCellFormat()));
					
					j = 0;
					i ++;
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}
	
	/**
	 * 
	 * @param sheet
	 * @param rb
	 * @param approvedPrints
	 * @param dateSystem
	 */
	public static void buildWFPrintApprovedSheet(WritableSheet sheet, ResourceBundle rb,
			Collection<DocumentsCheckOutsBean> approvedPrints, String dateSystem){
		try {
			//llenamos el libro de flujos de impresion aprovados
			sheet.setName(rb.getString("printWorkFlows.sheetName"));
			//la numeracion es columna, fila
			sheet.addCell(new Label(2, 0, rb.getString("printWorkFlows.ListTitle")
					+ " " + dateSystem, sheet.getCell(2, 0).getCellFormat()));
			sheet.addCell(new Label(0, 2, rb.getString("label.name"),
					sheet.getCell(0, 2).getCellFormat()));
			sheet.addCell(new Label(1, 2, rb.getString("label.version"), 
					sheet.getCell(1, 2).getCellFormat()));
			sheet.addCell(new Label(2, 2, rb.getString("label.document"), 
					sheet.getCell(2, 2).getCellFormat()));
			sheet.addCell(new Label(3, 2, rb.getString("label.expireAt"), 
					sheet.getCell(3, 2).getCellFormat()));
			
			//iteramos en los resultados que tenemos
			if(approvedPrints != null){
				int i = 3, j = 0, styleTemplateRow = 3;
				for (DocumentsCheckOutsBean docsCheckOutsBean : approvedPrints) {
					//colocamos el nombre
					sheet.addCell(new Label(j, i, 
							docsCheckOutsBean.getPersonBean().getApellido() + ", " + docsCheckOutsBean.getPersonBean().getNombre(), 
							sheet.getCell(j, styleTemplateRow).getCellFormat()));
					j++;
					//colocamos la version
					sheet.addCell(new Label(j, i, 
							docsCheckOutsBean.getMayorVer() + "." + docsCheckOutsBean.getMinorVer(), 
							sheet.getCell(j, styleTemplateRow).getCellFormat()));
					j++;
					//colocamos el nombre del documento
					sheet.addCell(new Label(j, i, 
							docsCheckOutsBean.getNameDocument() + " " + docsCheckOutsBean.getPrefix() + docsCheckOutsBean.getNumber(), 
							sheet.getCell(j, styleTemplateRow).getCellFormat()));
					j++;
					//colocamos la fecha de bloqueo
					sheet.addCell(new Label(j, i, 
							docsCheckOutsBean.getDateCheckOut(), 
							sheet.getCell(j, styleTemplateRow).getCellFormat()));
					
					j = 0;
					i ++;
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}
	
	public static void buildDistListSheet(WritableSheet sheet, ResourceBundle rb,
			Collection<DocumentsCheckOutsBean> distList, String dateSystem){
		try {
			//llenamos el libro de listas de distribucion
			sheet.setName(rb.getString("distributionList.sheetName"));
			//la numeracion es columna, fila
			sheet.addCell(new Label(2, 0, rb.getString("distributionList.ListTitle")
					+ " " + dateSystem, sheet.getCell(2, 0).getCellFormat()));
			sheet.addCell(new Label(0, 2, rb.getString("label.name"),
					sheet.getCell(0, 2).getCellFormat()));
			sheet.addCell(new Label(1, 2, rb.getString("label.version"), 
					sheet.getCell(1, 2).getCellFormat()));
			sheet.addCell(new Label(2, 2, rb.getString("label.document"), 
					sheet.getCell(2, 2).getCellFormat()));
			sheet.addCell(new Label(3, 2, rb.getString("label.code"), 
					sheet.getCell(3, 2).getCellFormat()));
			
			//iteramos en los resultados que tenemos
			if(distList != null){
				int i = 3, j = 0, styleTemplateRow = 3;
				for (DocumentsCheckOutsBean docsCheckOutsBean : distList) {
					//colocamos el nombre
					sheet.addCell(new Label(j, i, 
							docsCheckOutsBean.getPersonBean().getApellido() + ", " + docsCheckOutsBean.getPersonBean().getNombre(), 
							sheet.getCell(j, styleTemplateRow).getCellFormat()));
					j++;
					//colocamos la version
					sheet.addCell(new Label(j, i, 
							docsCheckOutsBean.getMayorVer() + "." + docsCheckOutsBean.getMinorVer(), 
							sheet.getCell(j, styleTemplateRow).getCellFormat()));
					j++;
					//colocamos el nombre del documento
					sheet.addCell(new Label(j, i, 
							docsCheckOutsBean.getNameDocument(), 
							sheet.getCell(j, styleTemplateRow).getCellFormat()));
					j++;
					//colocamos la fecha de bloqueo
					sheet.addCell(new Label(j, i, 
							docsCheckOutsBean.getPrefix() + docsCheckOutsBean.getNumber(), 
							sheet.getCell(j, styleTemplateRow).getCellFormat()));
					
					j = 0;
					i ++;
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}
}
