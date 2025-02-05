package com.gestionEnvio.custon.dostorres.persistent.managers;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;
import java.util.ResourceBundle;

import com.desige.webDocuments.document.actions.TituloDetalle;
import com.desige.webDocuments.document.actions.TituloDetalle1;
import com.desige.webDocuments.document.actions.TotalTablaDetalle;
import com.gestionEnvio.custon.dostorres.bean.TituloDetalleRecibo;
import com.desige.webDocuments.utils.DesigeConf;
import com.desige.webDocuments.utils.ToolsHTML;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

public class PdfPortadaRecibo {
	
	private String logoEmpresa;

	private String lblEncabezado1;
	private String lblEncabezado2;
	private String lblEncabezado3;
	private String lblCodigo;
	private String lblNombre;
	private String lblEmision;
	private String lblFechaDeVencimiento;
	private String lblPeriodoRecibo;
	private String lblLocal;
	
	private String lblDireccion;
	private String lblAviso;
	private String lblRIF;
	private String lblAlicuota;
	private String lblTasaCambiaria;
	private String lblNumeroDeCopias;
	private String lblSoloDireccion;
	private String lblRazonCambio;
	private String lblTotalLetrasBolivares;
	
	private String datEncabezado1;
	private String datEncabezado2;
	private String datEncabezado3;
	private String datCodigo;
	private String datNombre;
	private String datEmision;
	private String datFechaDeVencimiento;
	private String datPeriodoRecibo;
	private String datLocal;
	private String datDireccion;
	private String datAviso;
	private String datRIF;
	private String datAlicuota;
	private String datTasaCambiaria;
	private String datNumeroDeCopias;
	private String datSoloDireccion;
	private String datDestinatario;
	private String datRazonCambio;
	
	private boolean printing = false;
	
	
	private TituloDetalleRecibo lblRecibo = null;
	private TituloDetalleRecibo lblDistGasto = null;
	
	private TituloDetalleRecibo lblFondoReserva = null;
	private TituloDetalleRecibo lblEdoCta = null;
	
	private TotalTablaDetalle lblTotalFondoReserva = null;
	private TotalTablaDetalle lblTotalRecibo = null;
	private TotalTablaDetalle lblTotalDistGasto = null;
	private TotalTablaDetalle lblTotalEdoCta = null;
	
	
	private ArrayList detElaborado = null;
	private ArrayList detEditado = null;
	private ArrayList<?> detRecibo = null;
	private ArrayList<?> detDistGasto = null;
	private ArrayList detAprobado = null;
	private ArrayList detCambio = null;

	private PdfPCell cell = null;
	private PdfPTable table = null;
	private PdfPTable table2 = null;
	private PdfPTable table3 = null;
	private PdfPTable table4 = null;
	private PdfPTable table5 = null;
	private Font fontTitulo = null;
	private Font fontTitulo1 = null;
	private Font fontLabel = null;
	private Font fontLabel1 = null;
	private Font fontLabel2 = null;
	private Font fontData1 = null;
	private Font fontData = null;

	public void createPdf(String nameFilePdf) {
		Document documento = null;
		fontTitulo= FontFactory.getFont(FontFactory.HELVETICA_BOLDOBLIQUE, 9);
		fontTitulo1= FontFactory.getFont(FontFactory.HELVETICA_BOLDOBLIQUE, 8);
		fontLabel = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 8);
		fontLabel1 = FontFactory.getFont(FontFactory.COURIER_BOLD, 8);
		fontLabel2 = FontFactory.getFont(FontFactory.COURIER_BOLD, 7);
		fontData = FontFactory.getFont(FontFactory.TIMES, 8);
		fontData1 = FontFactory.getFont(FontFactory.COURIER, 8);
		try {
			documento = new Document(PageSize.LETTER, 25, 25, 10, 10);
			PdfWriter.getInstance(documento, new FileOutputStream(nameFilePdf));

			Image headerImage = Image.getInstance(ToolsHTML.getPathImgLogos().concat("Logo-Dos-Torres.png"));
			
			// document.add(new Paragraph(Chunk.NEWLINE));
			Paragraph totalBolivares = new Paragraph(new Chunk(getLblTotalLetrasBolivares(),  fontData));
			totalBolivares.setAlignment(Element.ALIGN_LEFT);
						
			//table = new PdfPTable(6);
			table = new PdfPTable(new float[] { 11, 54,15, 15,15,15 });
			table.getDefaultCell().setBorderWidth(0);
			table.setWidthPercentage(100);
			
			table2 = new PdfPTable(new float[] { 25, 15,15, 15,15,15 });
			table2.getDefaultCell().setBorderWidth(0);
			table2.setWidthPercentage(100);
			
			table3 = new PdfPTable(new float[] { 25, 5,15, 15,15,15 });
			table3.getDefaultCell().setBorderWidth(0);
			table3.setWidthPercentage(100);
			
			table4 = new PdfPTable(new float[] { 22, 13,15,8,10,8 });
			table4.getDefaultCell().setBorderWidth(0);
			table4.setWidthPercentage(100);
			
			table5 = new PdfPTable(new float[] { 4, 17,15,5 });
			table5.getDefaultCell().setBorderWidth(0);
			table5.setWidthPercentage(100);
			
			PdfPCell cellLogo = new PdfPCell(headerImage);
			cellLogo.setBorder(PdfPCell.BOTTOM);
			table.addCell(cellLogo);
			
			// encabezado 1 cell = new PdfPCell(new Paragraph(getDatEncabezado1(),FontFactory.getFont(FontFactory.TIMES_BOLD, 12)));
			cell = new PdfPCell(new Paragraph("Calle  Sur  Sector B,  Parcela A, Edif. Dos  Torres, Piso PB,\r\n"
											+ "Ofic.  S/N, Urb.  La   Lagunita  Country Club Caracas  1083.\r\n"
											+ "Telf:(0424) 143.42.57 /e-mail:condominiodostorres@gmail.com",FontFactory.getFont(FontFactory.TIMES_ROMAN, 9)));
			cell.setColspan(3);
			cell.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
			cell.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE);
			cell.setBorder(0);
			table.addCell(cell);

			
			// encabezado 2 y 3 cell = new PdfPCell(new Paragraph("getDatEncabezado2()",fontData));
			//table2 = new PdfPTable(1);
			cell = new PdfPCell(new Paragraph("CONDOMINIO DOS TORRES\r\n"
					+ "RIF: J-412082930\r\n"
					+ "No DE CONTROL: ",FontFactory.getFont(FontFactory.TIMES_ROMAN, 9)));
			cell.setHorizontalAlignment(PdfPCell.ALIGN_JUSTIFIED);
			cell.setVerticalAlignment(PdfPCell.ALIGN_BOTTOM);
			cell.setColspan(2);
			cell.setBorder(0);
		    table.addCell(cell);
		
			// etiquetas
			//addLineLabel("DIRECCIÓN", "AV. SUR, EDIF CENTRO EMPRESARIAL LAGUNITA, PISO PH, OFIC PH-01, URB. LA LAGUNITA COUNTRY CLUB, EL HATILLO, EDO MIRANDA.",table); //
			addLineLabel(lblDireccion, !ToolsHTML.isEmptyOrNull(datDireccion) ? datDireccion : "PDFPRTADA***S/N COLOQUE EN LA BASE DATOS",table); //
			addLineLabelBOLD(lblAviso, getDatAviso(),table);
			addLineLabel(lblRIF, getDatRIF(),table);
			addLineLabel(lblEmision, getDatEmision(),table);
			
			addLineLabel(lblLocal,getDatLocal(),table);
			addLineLabel(lblFechaDeVencimiento, getDatFechaDeVencimiento(),table);
			addLineLabel(lblNombre, getDatNombre(),table);
			addLineLabel(lblPeriodoRecibo, getDatPeriodoRecibo(),table);
		//	
			addLineFull(" ",table);
			addLineLabel(lblAlicuota, getDatAlicuota(),table);
		
			if(getDetRecibo()!=null) {
				addTitleDDetalleReci(getLblRecibo(),table,fontLabel1);
				addDataDetalleReci(getDetRecibo());
				addTotalTabla(getlblTotalRecibo(),table,fontLabel1);
			}
			
			if(getDetDistGasto()!=null) {
				addTitleDistribucionGasto(getLblDistGasto());
				addDataDistribucionGasto(getDetDistGasto());
				addTotalTabla1(getlblTotalDistGasto(),table2,fontLabel1);
				
				
				                           //lblDistGasto
				
			}
			
			if(getlblFondoReserva()!=null) {
				addTitleDDetalleReci(getlblFondoReserva(),table3,fontLabel1);
				addTotalTabla(getlblTotalFondoReserva(),table3,fontData1);
				
				                           //lblDistGasto
				
			}
			
			if(getLblEdoCta()!=null) {
				addTitleDDetalleReci(getLblEdoCta(),table4,fontLabel2);
				//addDataDistribucionGasto(getDetDistGasto());
				addTotalTabla1(getlblTotalEdoCta(),table4,fontData1);
				
			}
			
			
			Paragraph linea1 = new Paragraph(new Chunk("Segun Convenio Cambiario N.01 resolución de BCV 19-05-01",  fontData));
			linea1.setAlignment(Element.ALIGN_LEFT);
			Paragraph linea2 = new Paragraph(new Chunk("Valor promedio de las tasas informativas del sistema bancario",  fontData));
			linea2.setAlignment(Element.ALIGN_LEFT);
			Paragraph linea3 = new Paragraph(new Chunk("fecha: "+getDatEmision()+" valor: "+getDatTasaCambiaria(),  fontData));
			linea3.setAlignment(Element.ALIGN_LEFT);
			addLineLabel1("Para su seguridad, favor hacer transferencia de RECUPERO DE GASTO a nombre de:", "CONDOMINIO DOS TORRES",table5);
			addLineLabel2("a través de", "Cta Corriente Nro 01040026150260109537",table5);
			addLineFull("En caso de deposito bancario debe ser notificado para aplicarlo a la deuda. El pago de esta factura no implica la cancelacion de las anteriores.",table5);
			addLineFull(" ",table5);
			addLineFull("Ingresa a https://dostorres.com.ve en el enlace Ir a pagos, donde podrá visualizar su estado de cuenta, con los siguientes datos:",table5);
			addLineLabel0("Usuario:", getDatCodigo(),table5);
			addLineFull(" ",table5);
			addLineFull("Clave: 123456",table5);
			
			
		
		
		
			
			//addLineFull("Segun Convenio Cambiario N.01 y\r\n"
			//		+ "resolución de BCV 19-05-01",table4);
			//addLineFull("Valor promedio de las tasas informativas del sistema",table4);
			//addLineLabel("bancario", "fecha valor",table4);
			//addLineFull("03-01-2025 51.77",table4);
			//addLineLabel1("a través de", "Cta Corriente Nro 01040026150260109537",table4);
			//addLineFull("En caso de deposito bancario debe ser notificado para aplicarlo a la deuda. El pago de esta factura no implica la cancelacion de las anteriores.",table4);
			//addLineFull("Ingresa a https://dostorres.com.ve en el enlace Ir a pagos, donde podrá visualizar su estado de cuenta, con los siguientes datos:",table4);
			//addLineLabel1("Usuario: :", "J404649387",table4);
			//addLineLabelBOLD("Clave:", "123456",table4);
			
			// incluimos la tabla al documento
			documento.open();
			//documento.add(headerImage);
			//documento.add(totalBolivares);
			documento.add(table);
			documento.add(table2);
			documento.add(new Paragraph(Chunk.NEWLINE));
			documento.add(totalBolivares);
			documento.add(new Paragraph(Chunk.NEWLINE));
			documento.add(new Paragraph(Chunk.NEWLINE));
			documento.add(table3);
			documento.add(new Paragraph(Chunk.NEWLINE));
			documento.add(table4);
			documento.add(new Paragraph(Chunk.NEWLINE));
			documento.add(linea1);
			documento.add(linea2);
			documento.add(linea3);
			documento.add(new Paragraph(Chunk.NEWLINE));
			documento.add(table5);
			//documento.add(totalBolivares);
			//documento.add(totalBolivares);
			
			
			
		} catch (DocumentException de) {
			System.err.println(de.getMessage());
		} catch (IOException ioe) {
			System.err.println(ioe.getMessage());
		} catch (Throwable e) {
			System.err.println(e.getMessage());
			e.printStackTrace();
		} finally {
			documento.close();
		}
	}
	
	private void addLineLabel(String label, String data,PdfPTable cualTabla) {
		if (!ToolsHTML.isEmptyOrNull(data) ) {
			cell = new PdfPCell(new Paragraph(label,fontLabel));
			cell.setBorder(0);
			cualTabla.addCell(cell);

			cell = new PdfPCell(new Paragraph((label.trim().length()>0?": ":" ").concat(data),fontData));
			cell.setColspan(3);
			cell.setBorder(0);
			cualTabla.addCell(cell);
		}
	}
	private void addLineLabelBOLD(String label, String data,PdfPTable cualTabla) {
		if (!ToolsHTML.isEmptyOrNull(data) ) {
			cell = new PdfPCell(new Paragraph(label,fontLabel));
			//cell.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
			cell.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE);
			cell.setBorder(0);
			cualTabla.addCell(cell);

			cell = new PdfPCell(new Paragraph((label.trim().length()>0?": ":" ").concat(data),fontLabel));
			cell.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE);
			cell.setColspan(3);
			cell.setBorder(0);
			cualTabla.addCell(cell);
		}
	}
	private void addLineFull(String data,PdfPTable cualTabla) {
		if(data.indexOf("<br>")!=-1) {
			String[] linea = data.split("<br>");
			for(int i=0;i<linea.length;i++) {
				cell = new PdfPCell(new Paragraph(linea[i],fontData));
				cell.setColspan(4);
				cell.setBorder(0);
				cualTabla.addCell(cell);
			}
		} else {
			cell = new PdfPCell(new Paragraph(data,fontData));
			cell.setColspan(4);
			cell.setBorder(0);
			cualTabla.addCell(cell);
		}
	}
	
	private void addLineLabel1(String label, String data, PdfPTable cualTabla ) {
		cell = new PdfPCell(new Paragraph(label,fontData));
		cell.setColspan(2);
		cell.setBorder(0);
		cualTabla.addCell(cell);

		cell = new PdfPCell(new Paragraph((data.trim().length()>0?" ":" ").concat(data),fontLabel));
		cell.setColspan(2);
		cell.setHorizontalAlignment(PdfPCell.ALIGN_LEFT);
		cell.setBorder(0);
		cualTabla.addCell(cell);
	}
	
	private void addLineLabel2(String label, String data, PdfPTable cualTabla ) {
		cell = new PdfPCell(new Paragraph(label,fontData));
		cell.setColspan(1);
		cell.setBorder(0);
		cualTabla.addCell(cell);

		cell = new PdfPCell(new Paragraph((data.trim().length()>0?" ":" ").concat(data),fontLabel));
		cell.setColspan(3);
		cell.setHorizontalAlignment(PdfPCell.ALIGN_LEFT);
		cell.setBorder(0);
		cualTabla.addCell(cell);
	}
	
	private void addLineLabel0(String label, String data, PdfPTable cualTabla ) {
		cell = new PdfPCell(new Paragraph(label,fontData));
		cell.setColspan(1);
		cell.setHorizontalAlignment(PdfPCell.ALIGN_LEFT);
		cell.setBorder(0);
		cualTabla.addCell(cell);

		cell = new PdfPCell(new Paragraph((data.trim().length()>0?" ":" ").concat(data),fontLabel));
		cell.setColspan(1);
		cell.setHorizontalAlignment(PdfPCell.ALIGN_LEFT);
		cell.setBorder(0);
		cualTabla.addCell(cell);
	}
	
	private void addTitleDetail(TituloDetalle tituloDetalle) {
		cell = new PdfPCell(new Paragraph(tituloDetalle.getTitulo(),fontLabel1));
		cell.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
		cell.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE);
		cell.setColspan(2);
		cell.setBorder(0);
		table.addCell(cell);

		cell = new PdfPCell(new Paragraph(tituloDetalle.getColumna1(),fontLabel1));
		cell.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
		cell.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE);
		cell.setColspan(2);
		cell.setBorder(0);
		table.addCell(cell);

		cell = new PdfPCell(new Paragraph(tituloDetalle.getColumna2(),fontLabel1));
		cell.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
		cell.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE);
		cell.setColspan(2);
		cell.setBorder(0);
		table.addCell(cell);
	}
	
	private void addTotalTabla(TotalTablaDetalle totalTablaDetalle,PdfPTable cualtabla,Font cualFont) {
		
		cell = new PdfPCell(new Paragraph(totalTablaDetalle.getTitulo(),cualFont));
		cell.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
		cell.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE);
		cell.setColspan(2);
		cell.setBorder(1);
		cualtabla.addCell(cell);

		cell = new PdfPCell(new Paragraph(totalTablaDetalle.getColumna1(),cualFont));
		cell.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
		cell.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE);
		cell.setColspan(1);
		cell.setBorder(1);
		cualtabla.addCell(cell);

		cell = new PdfPCell(new Paragraph(totalTablaDetalle.getColumna2(),cualFont));
		cell.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
		cell.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE);
		cell.setColspan(1);
		cell.setBorder(1);
		cualtabla.addCell(cell);

		cell = new PdfPCell(new Paragraph(totalTablaDetalle.getColumna3(),cualFont));
		cell.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
		cell.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE);
		cell.setColspan(1);
		cell.setBorder(1);
		cualtabla.addCell(cell);
		
		cell = new PdfPCell(new Paragraph(totalTablaDetalle.getColumna4(),cualFont));
		cell.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
		cell.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE);
		cell.setColspan(1);
		cell.setBorder(1);
		cualtabla.addCell(cell);
	}
	
	private void addTotalTabla1(TotalTablaDetalle totalTablaDetalle,PdfPTable cualtabla,Font cualFont) {
		
		cell = new PdfPCell(new Paragraph(totalTablaDetalle.getTitulo(),cualFont));
		cell.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
		cell.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE);
		cell.setColspan(1);
		cell.setBorder(1);
		cualtabla.addCell(cell);

		cell = new PdfPCell(new Paragraph(totalTablaDetalle.getColumna1(),cualFont));
		cell.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
		cell.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE);
		cell.setColspan(1);
		cell.setBorder(1);
		cualtabla.addCell(cell);

		cell = new PdfPCell(new Paragraph(totalTablaDetalle.getColumna2(),cualFont));
		cell.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
		cell.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE);
		cell.setColspan(1);
		cell.setBorder(1);
		cualtabla.addCell(cell);

		cell = new PdfPCell(new Paragraph(totalTablaDetalle.getColumna3(),cualFont));
		cell.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
		cell.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE);
		cell.setColspan(1);
		cell.setBorder(1);
		cualtabla.addCell(cell);
		
		cell = new PdfPCell(new Paragraph(totalTablaDetalle.getColumna4(),cualFont));
		cell.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
		cell.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE);
		cell.setColspan(1);
		cell.setBorder(1);
		cualtabla.addCell(cell);
		
		cell = new PdfPCell(new Paragraph(totalTablaDetalle.getColumna5(),cualFont));
		cell.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
		cell.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE);
		cell.setColspan(1);
		cell.setBorder(1);
		cualtabla.addCell(cell);
	}


	
	// TITULO 6 COLUMNA
	//"CÓDIGO","DESCRIPCIÓN","MONTO","CUOTA PARTE Bs.","IVA","CUOTA PARTE $"));
	//	private void addTitleDetail(TituloDetalle tituloDetalle) {
	//cell = new PdfPCell(new Paragraph(tituloDetalle.getTitulo(),fontLabel));
	private void addTitleDDetalleReci(TituloDetalleRecibo tituloDetalleRecibo,PdfPTable cualtabla,Font cualfont) {
		
		if(tituloDetalleRecibo.getTituloCentrado()!=null && tituloDetalleRecibo.getTituloCentrado().trim().length()>0) {
			//cell = new PdfPCell(new Paragraph("REEMBOLSO DE GASTOS EFECTUADOS POR SU CUENTA",fontTitulo));
			cell = new PdfPCell(new Paragraph(tituloDetalleRecibo.getTituloCentrado().trim(),fontTitulo));
			cell.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
			cell.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE);
			cell.setColspan(6);
			cell.setBorder(0);
			cualtabla.addCell(cell);
		}
		
		if(tituloDetalleRecibo.getSubTituloEsquina()!=null && tituloDetalleRecibo.getSubTituloEsquina().trim().length()>0) {
			//cell = new PdfPCell(new Paragraph("GASTOS COMUNES",fontLabel));
			cell = new PdfPCell(new Paragraph(tituloDetalleRecibo.getSubTituloEsquina().trim(),fontLabel));
			cell.setHorizontalAlignment(PdfPCell.ALIGN_JUSTIFIED);
			cell.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE);
			cell.setColspan(2);
			cell.setBorder(1);
			cualtabla.addCell(cell);
			
			cell = new PdfPCell(new Paragraph(" ",fontLabel));
			cell.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
			cell.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE);
			cell.setColspan(4);
			cell.setBorder(1);
			cualtabla.addCell(cell);

		}
		
		
		cell = new PdfPCell(new Paragraph(tituloDetalleRecibo.getColumna1(),cualfont));
		cell.setHorizontalAlignment(PdfPCell.ALIGN_LEFT);
		cell.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE);
		cell.setColspan(1);
		cell.setBorder( PdfPCell.BOTTOM);
		cualtabla.addCell(cell);

		cell = new PdfPCell(new Paragraph(tituloDetalleRecibo.getColumna2(),cualfont));
		//cell = new PdfPCell(new Paragraph("DESCRIPCIÓN",fontLabel1));
	//	cell.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
	//	cell.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE);
		cell.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE);
		cell.setColspan(1);
		cell.setBorder( PdfPCell.BOTTOM);
		cualtabla.addCell(cell);

		cell = new PdfPCell(new Paragraph(tituloDetalleRecibo.getColumna3(),cualfont));
		//cell = new PdfPCell(new Paragraph("MONTO",fontLabel1));
	    cell.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
	//	cell.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE);
		cell.setColspan(1);
		cell.setBorder( PdfPCell.BOTTOM);
		cualtabla.addCell(cell);
	
		cell = new PdfPCell(new Paragraph(tituloDetalleRecibo.getColumna4(),cualfont));
		//cell = new PdfPCell(new Paragraph("CUOTA PARTE Bs",fontLabel1));
		cell.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
	//	cell.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE);
		cell.setColspan(1);
		cell.setBorder( PdfPCell.BOTTOM);
		cualtabla.addCell(cell);
		
		cell = new PdfPCell(new Paragraph(tituloDetalleRecibo.getColumna5(),cualfont));
		//cell = new PdfPCell(new Paragraph("IVA",fontLabel1));
		cell.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
	//	cell.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE);
		cell.setColspan(1);
		cell.setBorder( PdfPCell.BOTTOM);
		cualtabla.addCell(cell);
		
		cell = new PdfPCell(new Paragraph(tituloDetalleRecibo.getColumna6(),cualfont));
		//cell = new PdfPCell(new Paragraph("Cuota Parte $",fontLabel1));
		cell.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
	//	cell.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE);
		cell.setColspan(1);
		cell.setBorder( PdfPCell.BOTTOM);
		cualtabla.addCell(cell);
	
	}
	
	private void addDataDetail(ArrayList dataDetalle) {
		for(int i=0; i<dataDetalle.size();i++) {
			cell = new PdfPCell(new Paragraph((String)dataDetalle.get(i),fontData));
			cell.setHorizontalAlignment(PdfPCell.ALIGN_JUSTIFIED);
			cell.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE);
			cell.setColspan(2);
			cell.setBorder(0);
			table.addCell(cell);
		}
	}
	
	private void addDataDetalleReci(ArrayList dataDetalle) {
		//System.out.println(dataDetalle.size());
		int contar = 1; // contador de vuelta
		for (int i = 0; i < dataDetalle.size(); i++) {
			cell = new PdfPCell(new Paragraph((String) dataDetalle.get(i), fontData1));
			//cell.setHorizontalAlignment(PdfPCell.ALIGN_JUSTIFIED);
			//cell.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE);
			//System.out.println(i);
			//System.out.println(dataDetalle.size());
			
			if (i>6*contar){
				 contar=contar+1;
				    //System.out.println("entro al if*****i"+i);
					//System.out.println(i);
					//System.out.println(contar);
					//System.out.println("entro al fin*****i"+contar);
					//cell = new PdfPCell(new Paragraph((String) dataDetalle.get(i), fontData1));
					//cell.setHorizontalAlignment(PdfPCell.ALIGN_JUSTIFIED);
					//cell.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE);
			}
			//System.out.println("vueltas*****i"+contar);
			//System.out.println(i);
			//System.out.println(contar);
			
			//int contar=i-2;

			if (i == 1) {
				cell = new PdfPCell(new Paragraph((String) dataDetalle.get(i), fontData1));
				cell.setHorizontalAlignment(PdfPCell.ALIGN_JUSTIFIED);
				cell.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE);

			} else {
				cell = new PdfPCell(new Paragraph((String) dataDetalle.get(i), fontData1));
				cell.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
				cell.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE);
				 int resultado = (6*contar);
				 // resultado = resultado;
				if ((i+5) == resultado   )  {
					cell = new PdfPCell(new Paragraph((String) dataDetalle.get(i), fontData1));
					cell.setHorizontalAlignment(PdfPCell.ALIGN_LEFT);
					cell.setVerticalAlignment(PdfPCell.ALIGN_BOTTOM);
					//System.out.println("valor dentro del if de vueltas*****contar == "+contar);
					//System.out.println("valor dentro del if de vueltas*****i == "+i);
					//System.out.println("resultado  == "+resultado);
					

				}


			} //fin if
			
						
			//else {
			//	cell = new PdfPCell(new Paragraph((String) dataDetalle.get(i), fontData1));
			//	cell.setHorizontalAlignment(PdfPCell.ALIGN_JUSTIFIED);
			//	cell.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE);

			//	
		//	} //fin if
			
			// cell.setColspan(2);
			// cell.setBorder(1);
			// }
			// cell.setColspan(2);
			cell.setBorder(0);
			table.addCell(cell);
		} // fin for
	} // fin metodo
	
	// TITULO TABLAS DE DISTRIBUCION DE GASTO
	//"CÓDIGO","DESCRIPCIÓN","MONTO","CUOTA PARTE Bs.","IVA","CUOTA PARTE $"));
	//	private void addTitleDetail(TituloDetalle tituloDetalle) {
	//cell = new PdfPCell(new Paragraph(tituloDetalle.getTitulo(),fontLabel));
	private void addTitleDistribucionGasto(TituloDetalleRecibo tituloDetalleRecibo) {
		cell = new PdfPCell(new Paragraph("DISTRIBUCION DEL REEMBOLSO DEL GASTO",fontTitulo1));
		cell.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
		cell.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE);
		cell.setColspan(6);
		cell.setBorder(0);
		cell.setBorder(PdfPCell.TOP);
		table2.addCell(cell);
		//table.addCell(table2);

		
		// table.addCell(cell);
		//table.addCell(cell);
		
		cell = new PdfPCell(new Paragraph(" ",fontLabel));
		cell.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
		cell.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE);
		cell.setColspan(0);
		cell.setBorder( PdfPCell.BOTTOM);
		table2.addCell(cell);

		//cell = new PdfPCell(new Paragraph(tituloDetalleRecibo.getColumna1(),fontLabel1));
		cell = new PdfPCell(new Paragraph("REEMBOLSOS DE GASTOS CON IVA (PORCION GRAVABLE)",fontLabel1));
		cell.setHorizontalAlignment(PdfPCell.ALIGN_LEFT);
		cell.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE);
		cell.setColspan(0);
		cell.setBorder( PdfPCell.BOTTOM);
		table2.addCell(cell);
		//table.addCell(table2);


		//cell = new PdfPCell(new Paragraph(tituloDetalleRecibo.getColumna2(),fontLabel));
		cell = new PdfPCell(new Paragraph("REEMBOLSOS DE GASTOS SIN IVA (PORCION EXENTA)",fontLabel1));
		cell.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
		cell.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE);
		cell.setColspan(0);
		cell.setBorder( PdfPCell.BOTTOM);
		table2.addCell(cell);
		//table.addCell(table2);


		cell = new PdfPCell(new Paragraph("% de IVA",fontLabel1));
	    cell.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
		cell.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE);
		cell.setColspan(0);
		cell.setBorder( PdfPCell.BOTTOM);
		table2.addCell(cell);
		//table.addCell(table2);

	
		cell = new PdfPCell(new Paragraph("REEMBOLSO DE\r\n"
										+ "IVA",fontLabel1));
		cell.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
		cell.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE);
		cell.setColspan(0);
		cell.setBorder( PdfPCell.BOTTOM);
		table2.addCell(cell);
		//table.addCell(table2);

		
	
		//cell = new PdfPCell(new Paragraph(tituloDetalleRecibo.getColumna6(),fontLabel));
		cell = new PdfPCell(new Paragraph("REEMBOLSO DE GASTOS TOTAL",fontLabel1));
		cell.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
	//	cell.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE);
		cell.setColspan(0);
		cell.setBorder( PdfPCell.BOTTOM);
		table2.addCell(cell);
		//table.addCell(table2);

	
	}
	
	private void addDataDistribucionGasto(ArrayList dataDetalle) {
		//System.out.println(dataDetalle.size());
		int contar = 1; // contador de vuelta
		for (int i = 0; i < dataDetalle.size(); i++) {
			cell = new PdfPCell(new Paragraph((String) dataDetalle.get(i), fontData1));
		
			if (i>6*contar){
				 contar=contar+1;
			}
		
			if (i == 0) {
				cell = new PdfPCell(new Paragraph((String) dataDetalle.get(i), fontLabel1));
				cell.setHorizontalAlignment(PdfPCell.ALIGN_LEFT);
				cell.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE);

			} else {
				cell = new PdfPCell(new Paragraph((String) dataDetalle.get(i), fontData1));
				cell.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
				cell.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE);
				 int resultado = (6*contar);
				 // resultado = resultado;
				if ((i) == resultado   )  {
					cell = new PdfPCell(new Paragraph((String) dataDetalle.get(i), fontLabel1));
					cell.setHorizontalAlignment(PdfPCell.ALIGN_LEFT);
					cell.setVerticalAlignment(PdfPCell.ALIGN_BOTTOM);
				}


			} //fin if
			
						
			cell.setBorder(0);
			table2.addCell(cell);
		} // fin for
	} // fin metodo


	
	
	// getters and setters
	public String getLblCodigo() {
		return lblCodigo;
	}
	public void setLblCodigo(String lblCodigo) {
		this.lblCodigo = lblCodigo;
	}
	public String getLblFechaDeVencimiento() {
		return lblFechaDeVencimiento;
	}
	public void setLblFechaDeVencimiento(String lblFechaDeVencimiento) {
		this.lblFechaDeVencimiento = lblFechaDeVencimiento;
	}
	public String getLblPeriodoRecibo() {
		return lblPeriodoRecibo;
	}
	public void setLblPeriodoRecibo(String lblPeriodoRecibo) {
		this.lblPeriodoRecibo = lblPeriodoRecibo;
	}
	public String getLblLocal() {
		return lblLocal;
	}
	public void setLblLocal(String lblLocal) {
		this.lblLocal = lblLocal;
	}
	
	public String getLblTasaCambiaria() {
		return lblTasaCambiaria;
	}
	public void setLblTasaCambiaria(String lblTasaCambiaria) {
		this.lblTasaCambiaria = lblTasaCambiaria;
	}
	public String getLblNombre() {
		return lblNombre;
	}
	public void setLblNombre(String lblNombre) {
		this.lblNombre = lblNombre;
	}
	public String getLblNumeroDeCopias() {
		return lblNumeroDeCopias;
	}
	public void setLblNumeroDeCopias(String lblNumeroDeCopias) {
		this.lblNumeroDeCopias = lblNumeroDeCopias;
	}
	public String getLblDireccion() {
		return lblDireccion;
	}
	public void setLblDireccion(String lblDireccion) {
		this.lblDireccion = lblDireccion;
	}
	public String getLblRIF() {
		return lblRIF;
	}
	public void setLblRIF(String lblRIF) {
		this.lblRIF = lblRIF;
	}
	public String getLblAlicuota() {
		return lblAlicuota;
	}
	public void setLblAlicuota(String lblAlicuota) {
		this.lblAlicuota = lblAlicuota;
	}
	
	public String getLblSoloDireccion() {
		return lblSoloDireccion;
	}
	public void setLblSoloDireccion(String lblSoloDireccion) {
		this.lblSoloDireccion = lblSoloDireccion;
	}
	public String getLblEmision() {
		return lblEmision;
	}
	public void setLblEmision(String lblEmision) {
		this.lblEmision = lblEmision;
	}

	public String getLblEncabezado1() {
		return lblEncabezado1;
	}

	public void setLblEncabezado1(String lblEncabezado1) {
		this.lblEncabezado1 = lblEncabezado1;
	}

	public String getLblEncabezado2() {
		return lblEncabezado2;
	}

	public void setLblEncabezado2(String lblEncabezado2) {
		this.lblEncabezado2 = lblEncabezado2;
	}

	public String getLblEncabezado3() {
		return lblEncabezado3;
	}

	public void setLblEncabezado3(String lblEncabezado3) {
		this.lblEncabezado3 = lblEncabezado3;
	}

	public String getLogoEmpresa() {
		return logoEmpresa;
	}

	public void setLogoEmpresa(String logoEmpresa) {
		this.logoEmpresa = logoEmpresa;
	}

	public String getDatCodigo() {
		return datCodigo;
	}

	public void setDatCodigo(String datCodigo) {
		this.datCodigo = datCodigo;
	}

	public String getDatDestinatario() {
		return datDestinatario;
	}

	public void setDatDestinatario(String datDestinatario) {
		this.datDestinatario = datDestinatario;
	}

	public String getDatEncabezado1() {
		return datEncabezado1;
	}

	public void setDatEncabezado1(String datEncabezado1) {
		this.datEncabezado1 = datEncabezado1;
	}

	public String getDatEncabezado2() {
		return datEncabezado2;
	}

	public void setDatEncabezado2(String datEncabezado2) {
		this.datEncabezado2 = datEncabezado2;
	}

	public String getDatEncabezado3() {
		return datEncabezado3;
	}

	public void setDatEncabezado3(String datEncabezado3) {
		this.datEncabezado3 = datEncabezado3;
	}

	public String getDatFechaDeVencimiento() {
		return datFechaDeVencimiento;
	}

	public void setDatFechaDeVencimiento(String datFechaDeVencimiento) {
		this.datFechaDeVencimiento = datFechaDeVencimiento;
	}

	public String getDatPeriodoRecibo() {
		return datPeriodoRecibo;
	}

	public void setDatPeriodoRecibo(String datPeriodoRecibo) {
		this.datPeriodoRecibo = datPeriodoRecibo;
	}
	public String getDatLocal() {
		return datLocal;
	}

	public void setDatLocal(String datLocal) {
		this.datLocal = datLocal;
	}

	public String getDatTasaCambiaria() {
		return datTasaCambiaria;
	}

	public void setDatTasaCambiaria(String datTasaCambiaria) {
		this.datTasaCambiaria = datTasaCambiaria;
	}

	public String getDatNombre() {
		return datNombre;
	}

	public void setDatNombre(String datNombre) {
		this.datNombre = datNombre;
	}

	public String getDatNumeroDeCopias() {
		return datNumeroDeCopias;
	}

	public void setDatNumeroDeCopias(String datNumeroDeCopias) {
		this.datNumeroDeCopias = datNumeroDeCopias;
	}

	public String getDatDireccion() {
		return datDireccion;
	}

	public void setDatDireccion(String datDireccion) {
		this.datDireccion = datDireccion;
	}

	public String getDatRIF() {
		return datRIF;
	}

	public void setDatRIF(String datRIF) {
		this.datRIF = datRIF;
	}
	public String getDatAlicuota() {
		return datAlicuota;
	}

	public void setDatAlicuota(String datAlicuota) {
		this.datAlicuota = datAlicuota;
	}

	public String getDatSoloDireccion() {
		return datSoloDireccion;
	}

	public void setDatSoloDireccion(String datSoloDireccion) {
		this.datSoloDireccion = datSoloDireccion;
	}

	public String getDatEmision() {
		return datEmision;
	}

	public void setDatEmision(String datEmision) {
		this.datEmision = datEmision;
	}

	public TotalTablaDetalle getlblTotalFondoReserva() {
		return lblTotalFondoReserva;
	}

	public void setlblTotalFondoReserva(TotalTablaDetalle lblTotalFondoReserva) {
		this.lblTotalFondoReserva = lblTotalFondoReserva;
	}

	public TituloDetalleRecibo getLblEdoCta() {
		return lblEdoCta;
	}

	public void setLblEdoCta(TituloDetalleRecibo lblEdoCta) {
		this.lblEdoCta = lblEdoCta;
	}

	public TituloDetalleRecibo getlblFondoReserva() {
		return lblFondoReserva;
	}

	public void setlblFondoReserva(TituloDetalleRecibo lblFondoReserva) {
		this.lblFondoReserva = lblFondoReserva;
	}

	public TituloDetalleRecibo getLblRecibo() {
		return lblRecibo;
	}

	public void setLblRecibo(TituloDetalleRecibo lblRecibo) {
		this.lblRecibo = lblRecibo;
	}
	
	public TotalTablaDetalle getlblTotalRecibo() {
		return lblTotalRecibo;
	}

	public void setlblTotalRecibo(TotalTablaDetalle lblTotalRecibo) {
		this.lblTotalRecibo = lblTotalRecibo;
	}
	
	public TotalTablaDetalle getlblTotalDistGasto() {
		return lblTotalDistGasto;
	}
	public void setlblTotalDistGasto(TotalTablaDetalle lblTotalDistGasto ) {
		this.lblTotalDistGasto  = lblTotalDistGasto ;
	}
	// nuevo titulo total EdoCta
		public TotalTablaDetalle getlblTotalEdoCta() {
			return lblTotalEdoCta;
		}

		public void setlblTotalEdoCta(TotalTablaDetalle lblTotalEdoCta) {
			this.lblTotalEdoCta = lblTotalEdoCta;
		}

	
	//
	public TituloDetalleRecibo getLblDistGasto() {
		return lblDistGasto;
	}

	public void setLblDistGasto(TituloDetalleRecibo lblDistGasto) {
		this.lblDistGasto = lblDistGasto;
	}
	

	
		
	// arrayList 
	public ArrayList getDetCambio() {
		return detCambio;
	}

	public void setDetCambio(ArrayList detCambio) {
		this.detCambio = detCambio;
	}


	public ArrayList getDetAprobado() {
		return detAprobado;
	}

	public void setDetAprobado(ArrayList detAprobado) {
		this.detAprobado = detAprobado;
	}

	public ArrayList getDetEditado() {
		return detEditado;
	}

	public void setDetEditado(ArrayList detEditado) {
		this.detEditado = detEditado;
	}

	public ArrayList getDetElaborado() {
		return detElaborado;
	}

	public void setDetElaborado(ArrayList detElaborado) {
		this.detElaborado = detElaborado;
	}

	public ArrayList getDetRecibo() {
		return detRecibo;
	}

	public void setDetRecibo(ArrayList detRecibo) {
		this.detRecibo = detRecibo;
	}
	//Detalle
	public ArrayList getDetDistGasto() {
		return detDistGasto;
	}

	public void setDetDistGasto(ArrayList detDistGasto) {
		this.detDistGasto = detDistGasto;
	}


	public boolean isPrinting() {
		return printing;
	}

	public void setPrinting(boolean printing) {
		this.printing = printing;
	}

	public String getLblAviso() {
		return lblAviso;
	}

	public void setLblAviso(String lblAviso) {
		this.lblAviso = lblAviso;
	}

	public String getDatAviso() {
		return datAviso;
	}

	public void setDatAviso(String datAviso) {
		this.datAviso = datAviso;
	}

	public String getLblRazonCambio() {
		return lblRazonCambio;
	}

	public void setLblRazonCambio(String lblRazonCambio) {
		this.lblRazonCambio = lblRazonCambio;
	}

	public String getDatRazonCambio() {
		return datRazonCambio;
	}

	public void setDatRazonCambio(String datRazonCambio) {
		this.datRazonCambio = datRazonCambio;
	}
	
	public String getLblTotalLetrasBolivares() {
		return lblTotalLetrasBolivares;
	}

	public void setLblTotalLetrasBolivares(String lblTotalLetrasBolivares) {
		this.lblTotalLetrasBolivares = lblTotalLetrasBolivares;
	}
	
	
}
