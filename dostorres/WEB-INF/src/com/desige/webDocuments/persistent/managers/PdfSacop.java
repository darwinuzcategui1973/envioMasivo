package com.desige.webDocuments.persistent.managers;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import com.desige.webDocuments.bean.PdfDocumentDetailRequest;
import com.desige.webDocuments.bean.PdfSacopDetailRequest;
import com.desige.webDocuments.bean.PdfSacopRequest;
import com.desige.webDocuments.utils.DesigeConf;
import com.desige.webDocuments.utils.ToolsHTML;
import com.itextpdf.text.BadElementException;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

public class PdfSacop {

	private Font font = FontFactory.getFont(FontFactory.HELVETICA, 16, BaseColor.BLACK);
	private Font fontHead = FontFactory.getFont(FontFactory.HELVETICA, 10, Font.BOLD, BaseColor.LIGHT_GRAY);
	private Font fontBody = FontFactory.getFont(FontFactory.HELVETICA, 10, Font.NORMAL, BaseColor.BLACK);
	private Font fontSacop = FontFactory.getFont(FontFactory.HELVETICA, 8, Font.NORMAL, BaseColor.BLACK);

	public PdfSacop() {
		
	}

	@SuppressWarnings("deprecation")
	public InputStream generatePdf(PdfSacopRequest pdfData) throws Exception {

		Document document = new Document(PageSize.LETTER, 20, 20, 20, 20);
		//PdfWriter.getInstance(document, new FileOutputStream("C:\\warfile\\iTextHelloWorld.pdf"));
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		PdfWriter.getInstance(document, outputStream);

		document.open();

		Image img = Image.getInstance(ToolsHTML.getPathImgLogos().concat("empresa.gif"));
		document.add(img);
		
		//titulos tomados de  propiedades
		Locale defaultLocale = new Locale(DesigeConf.getProperty("language.Default"),DesigeConf.getProperty("country.Default"));
        ResourceBundle rb = ResourceBundle.getBundle("LoginBundle",defaultLocale);
        

		// document.add(new Paragraph(Chunk.NEWLINE));
		Paragraph par1 = new Paragraph(new Chunk(pdfData.getTitleSacop(), font));
		par1.setAlignment(Element.ALIGN_CENTER);
		document.add(par1);

		document.add(new Paragraph(Chunk.NEWLINE));

		// table
		PdfPTable table = new PdfPTable(3);
		table.setWidthPercentage(100);
		
		//addTableHeader(table);
		//addRows(table);
		addCustomRowsHead(table, new String[]{rb.getString("scp.emisor").concat(":"),rb.getString("scp.edo").concat(":"),rb.getString("scp.origin").concat(":")}, Element.ALIGN_LEFT,fontHead);
		//addCustomRowsHead(table, new String[]{"Emisor:","Estado:","Tipo de Gestión:"}, Element.ALIGN_LEFT,fontHead);
		addCustomRows(table, new String[]{pdfData.getEmisor(),pdfData.getEstado(),pdfData.getTipo()}, Element.ALIGN_RIGHT,fontBody,20);

		
		addCustomRowsHead(table, new String[]{rb.getString("scp.fechae").concat(":"),rb.getString("scp.afctresponsable").concat(":"),rb.getString("scp.receivingArea").concat(":")}, Element.ALIGN_LEFT,fontHead);
		//addCustomRowsHead(table, new String[]{"Fecha Emisión:","Responsable del proceso","Area del Responsable :"}, Element.ALIGN_LEFT,fontHead);
		addCustomRows(table, new String[]{pdfData.getFechaEmision(),pdfData.getResponsableArea(),pdfData.getAreaAfectada()}, Element.ALIGN_RIGHT,fontBody,20);

		
		addCustomRowsHead(table, new String[]{rb.getString("scp.fechastimada").concat(":"),rb.getString("scp.fechareal").concat(":"),rb.getString("scp.fechaWhenDiscovered").concat(":")}, Element.ALIGN_LEFT,fontHead);
		//addCustomRowsHead(table, new String[]{"Fecha Estimada:","F. Completación de acciones:","Fecha de hallazgo:"}, Element.ALIGN_LEFT,fontHead);
		addCustomRows(table, new String[]{pdfData.getFechaEstimada(),pdfData.getFechaReal(),pdfData.getFechaHallazgo()}, Element.ALIGN_RIGHT,fontBody,20);
		
		addCustomRowsHead(table, new String[]{rb.getString("scp.clasificacionsacop").concat(":"),rb.getString("scp.fechaverificacion").concat(":"),rb.getString("scp.fechacierre").concat(":")}, Element.ALIGN_LEFT,fontHead);
		//addCustomRowsHead(table, new String[]{"Clasificación SACOP:","Fecha de Verificación de la Acción:","Fecha de Cierre:"}, Element.ALIGN_LEFT,fontHead);
		addCustomRows(table, new String[]{pdfData.getClasificacion(),pdfData.getFechaVerificacion(),pdfData.getFechaCierre()}, Element.ALIGN_RIGHT,fontBody,20);

		
		
		addCustomRowsHead(table, new String[]{"","",rb.getString("scp.applicant").concat(":")}, Element.ALIGN_LEFT,fontHead);
		//addCustomRowsHead(table, new String[]{"","","Solicitante:"}, Element.ALIGN_LEFT,fontHead);
		addCustomRows(table, new String[]{"","",pdfData.getSolicitante()}, Element.ALIGN_RIGHT,fontBody,20);
		document.add(table);

		if(pdfData.getNameFile()!=null && !pdfData.getNameFile().isEmpty() || pdfData.getNameRelatedDocument()!=null && !pdfData.getNameRelatedDocument().isEmpty() ) {
			document.add(new Paragraph(new Chunk(rb.getString("doc.link").concat(": "), fontHead)));
			//document.add(new Paragraph(new Chunk("Documento Relacionado:", fontHead)));
			if(pdfData.getNameFile()!=null && !pdfData.getNameFile().isEmpty()) {
				document.add(new Paragraph(new Chunk(pdfData.getNameFile(), fontBody)));
			}
			
		}

		if(pdfData.getListSacops()!=null) {
			PdfPTable tableSacop = new PdfPTable(new float[] { 15, 70, 15 });
			tableSacop.setWidthPercentage(100);
			Paragraph parSacop = new Paragraph(new Chunk(rb.getString("scp.sacoprelacionados").concat(": "), fontHead));
			//Paragraph parSacop = new Paragraph(new Chunk("Sacops Relacionados:", fontHead));
			document.add(parSacop);
			
			for(PdfSacopDetailRequest detail : pdfData.getListSacops() ) {
				addCustomRows(tableSacop, new String[]{detail.getSacopsNumber(),detail.getSacopsDescription(),detail.getSacopsDate()}, Element.ALIGN_LEFT,fontSacop,0);
			}
			document.add(tableSacop);
		}
		
		
		if(pdfData.getListRelationDocs()!=null) {
			PdfPTable tableDocument = new PdfPTable(new float[] { 100 });
			tableDocument.setWidthPercentage(100);
			Paragraph parDocument = new Paragraph(new Chunk(rb.getString("scp.sourceDocument").concat(": "), fontHead));
			// Paragraph parDocument = new Paragraph(new Chunk("Documento(s) relacionado(s) por tipo de gestión:", fontHead));
			document.add(parDocument);
			
			StringBuilder sb = new StringBuilder();
			for(PdfDocumentDetailRequest detail : pdfData.getListRelationDocs() ) {
				sb.setLength(0);
				sb.append("(").append(detail.getTypeRelation()).append(")-> ").append(detail.getPrefixNumber()).append(" ").append(detail.getNameDocument()).append(" ").append(detail.getTypeDoc());
				addCustomRows(tableDocument, new String[]{sb.toString()}, Element.ALIGN_LEFT,fontSacop,0);
			}
			document.add(tableDocument);
		}
		

		document.add(new Paragraph(new Chunk(rb.getString("scp.desc").concat(": "),fontHead)));
		//document.add(new Paragraph(new Chunk("Comentarios adicionales:", fontHead)));
		document.add(new Paragraph(new Chunk(pdfData.getComentario(), fontBody)));
		
		document.add(new Paragraph(new Chunk(pdfData.getPosibleCausaTitle(), fontHead)));
		document.add(new Paragraph(new Chunk(pdfData.getPosibleCausa(), fontBody)));

		document.add(new Paragraph(new Chunk(rb.getString("scp.acrecomendadas").concat(": "), fontHead)));
		//document.add(new Paragraph(new Chunk("Acciones Recomendadas:", fontHead)));
		document.add(new Paragraph(new Chunk(pdfData.getAccionesRecomendadas(), fontBody)));
		
		document.add(new Paragraph(new Chunk(pdfData.getAccionesCorrectivaInformaTitle(), fontHead)));
		document.add(new Paragraph(new Chunk(pdfData.getAccionesCorrectivaInforma(), fontBody)));
		
		document.add(new Paragraph(new Chunk(rb.getString("requestSacop.registerTitle5").concat(": "), fontHead)));
		// document.add(new Paragraph(new Chunk("Requisitos aplicables:", fontHead)));
		document.add(new Paragraph(new Chunk(pdfData.getRequisitoAplicable(), fontBody)));
	
		document.add(new Paragraph(new Chunk(rb.getString("requestSacop.registerTitle3").concat(": "), fontHead)));
		//document.add(new Paragraph(new Chunk("Procesos:", fontHead)));
		document.add(new Paragraph(new Chunk(pdfData.getProcesos(), fontBody)));
		
		document.add(new Paragraph(new Chunk(rb.getString("scp.obs").concat(": "), fontHead)));
		//document.add(new Paragraph(new Chunk("Definición de causa raíz:", fontHead)));
		document.add(new Paragraph(new Chunk(pdfData.getCausaRaiz(), fontBody)));
		/* archivos de la causa raiz */

		if(pdfData.getCausaRaizTres()!=null && !pdfData.getCausaRaizTres().isEmpty()) {
			document.add(new Paragraph(new Chunk(rb.getString("scp.tecnica").concat(": ").concat(pdfData.getCausaRaizTres()), fontBody)));
		}
		
		if(pdfData.getNameRelatedDocument()!=null && !pdfData.getNameRelatedDocument().isEmpty()) {
				document.add(new Paragraph(new Chunk(pdfData.getNameRelatedDocument(), fontBody)));
		}
		
				
		document.add(new Paragraph(new Chunk(rb.getString("scp.descriptionActionMainTitle").concat(": "),  fontHead)));
		// document.add(new Paragraph(new Chunk("Definición de acción general a realizar:", fontHead)));
		document.add(new Paragraph(new Chunk(pdfData.getDecripcionAccionPrincipal(), fontBody)));
		/*
		if(pdfData.getNameRelatedDocument()!=null && !pdfData.getNameRelatedDocument().isEmpty()) {
			document.add(new Paragraph(new Chunk(pdfData.getNameRelatedDocument(), fontBody)));
		}
		*/
	
		document.add(new Paragraph(new Chunk(rb.getString("scp.acciones").concat(": "), fontHead)));
		//document.add(new Paragraph(new Chunk("Actividades:", fontHead)));
		String[] actividades = getArrayFromActivities(pdfData.getAcciones());
		for(String cad: actividades) {
			document.add(new Paragraph(new Chunk(cad, fontBody)));
		}
		
		document.add(new Paragraph(new Chunk(rb.getString("scp.observacion").concat(": "), fontHead)));
		//document.add(new Paragraph(new Chunk("    Observación:", fontHead)));
		document.add(new Paragraph(new Chunk(pdfData.getAccionesObservaciones(), fontBody)));
		
		
		
		document.add(new Paragraph(new Chunk(rb.getString("scp.cumplaccstable").concat(" :").concat(pdfData.getCumplieron()), fontHead)));
		//document.add(new Paragraph(new Chunk("Se Cumplieron las actividades Establecidas?: ".concat(pdfData.getCumplieron()), fontHead)));
		document.add(new Paragraph(new Chunk(pdfData.getCumplieronDetalle(), fontBody)));
		
		document.add(new Paragraph(new Chunk(rb.getString("scp.accefecraiz").concat(" :").concat(pdfData.getEficaz()), fontHead)));
		//document.add(new Paragraph(new Chunk("La acción fue eficaz para eliminar la causa raíz? :".concat(pdfData.getEficaz()), fontHead)));
		document.add(new Paragraph(new Chunk(pdfData.getEficazDetalle(), fontBody)));

			String RequirioSeguimiento ="SI";
			String sacopnum;
			String[] sacopnuma = pdfData.getTitleSacop().split(":");
			sacopnum = "'"+sacopnuma[1]+"'";
			if(pdfData.getSeguimiento().equalsIgnoreCase("no")) {
				if( ToolsHTML.isSacopPadre(sacopnum).equalsIgnoreCase("no")){
					RequirioSeguimiento ="no";
				}
				
				
			}
			 if(!ToolsHTML.isEmptyOrNull(pdfData.getSeguimiento())) {
				 
				 document.add(new Paragraph(new Chunk(rb.getString("requestSacop.trackingTitle0").concat(" :").concat(RequirioSeguimiento), fontHead)));
					//document.add(new Paragraph(new Chunk("Requiere seguimiento?: ".concat(pdfData.getSeguimiento()), fontHead)));
				document.add(new Paragraph(new Chunk(pdfData.getSeguimientoFecha(), fontBody)));
				 
			 }
		document.close();
		
		byte[] bytes = outputStream.toByteArray();
		InputStream inputStream = new ByteArrayInputStream(bytes);		

		return inputStream;
	}

	private String[] getArrayFromActivities(String acciones) {
		acciones = acciones.trim();
		acciones = acciones.replaceAll(" Actividad:", "<br/> <br/>Actividad:");
		acciones = acciones.replaceAll("Fecha :", "<br/>Fecha :");
		acciones = acciones.replaceAll("Fecha:", "<br/>Fecha:");
		acciones = acciones.replaceAll("Sr\\(a\\)::", "<br/> <br/>Sr(a)::");
		acciones = acciones.replaceAll("Comentarios:", "<br/>Comentarios:");
		
		return acciones.split("<br/>");
	}
	
	private void addRows(PdfPTable table) {
		table.addCell(new Paragraph(new Chunk("Emisor", fontHead)));
		table.addCell("Estado");
		table.addCell("Tipo de Gestión");
	}
	
	private PdfPCell addCellHead(String text, int align, Font font) {
		PdfPCell cell = new PdfPCell(new Phrase(text, font));
		cell.setHorizontalAlignment(align);
		cell.setBorder(0);
		cell.setPadding(5);
		cell.setPaddingBottom(2);
		return cell;
	}
	
	private PdfPCell addCell(String text, int align, Font font, int paddingLeft) {
		PdfPCell cell = new PdfPCell(new Phrase(text, font));
		cell.setBorder(0);
		cell.setPadding(2);
		cell.setPaddingBottom(5);
		cell.setPaddingLeft(paddingLeft);
		return cell;
	}

	private void addCustomRowsHead(PdfPTable table, String[] data, int align, Font font) throws URISyntaxException, BadElementException, IOException {
		for (int i = 0; i < data.length; i++) {
			table.addCell(addCellHead(data[i],align,font));
		}
	}
	
	private void addCustomRows(PdfPTable table, String[] data, int align, Font font, int paddingLeft) throws URISyntaxException, BadElementException, IOException {
		for (int i = 0; i < data.length; i++) {
			table.addCell(addCell(data[i],align,font,paddingLeft));
		}
	}

	
}
