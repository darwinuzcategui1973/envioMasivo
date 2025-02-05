package com.desige.webDocuments.document.actions;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import com.desige.webDocuments.utils.ToolsHTML;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

public class PdfPortada {
	
	private String logoEmpresa;

	private String lblEncabezado1;
	private String lblEncabezado2;
	private String lblEncabezado3;
	private String lblCodigo;
	private String lblNombre;
	private String lblVersion;
	private String lblFechaDeAprobacion;
	private String lblFechaDeExpiracion;
	private String lblFechaDeCierre;
	
	private String lblPropietario;
	private String lblOrigen;
	private String lblSolicitado;
	private String lblResponsableImpresion;
	private String lblImpreso;
	private String lblNumeroDeCopias;
	private String lblSoloPropietario;
	private String lblRazonCambio;
	
	private String datEncabezado1;
	private String datEncabezado2;
	private String datEncabezado3;
	private String datCodigo;
	private String datNombre;
	private String datVersion;
	private String datFechaDeAprobacion;
	private String datFechaDeExpiracion;
	private String datFechaDeCierre;
	private String datPropietario;
	private String datOrigen;
	private String datSolicitado;
	private String datResponsableImpresion;
	private String datImpreso;
	private String datNumeroDeCopias;
	private String datSoloPropietario;
	private String datDestinatario;
	private String datRazonCambio;
	
	private boolean printing = false;
	
	private TituloDetalle lblElaborado = null;
	private TituloDetalle lblEditado = null;
	private TituloDetalle lblRevisado = null;
	private TituloDetalle lblAprobado = null;
	
	private TituloDetalle1 lblCambio = null;
	
	
	private ArrayList detElaborado = null;
	private ArrayList detEditado = null;
	private ArrayList detRevisado = null;
	private ArrayList detAprobado = null;
	private ArrayList detCambio = null;

	private PdfPCell cell = null;
	private PdfPTable table = null;
	private Font fontLabel = null;
	private Font fontData = null;

	public void createPdf(String nameFilePdf) {
		Document documento = null;
		PdfPTable table2 = null;
		fontLabel = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12);
		fontData = FontFactory.getFont(FontFactory.TIMES, 12);
		try {
			documento = new Document(PageSize.LETTER, 0, 0, 50, 50);
			PdfWriter.getInstance(documento, new FileOutputStream(nameFilePdf));

			Image headerImage = Image.getInstance(ToolsHTML.getPathImgLogos().concat("Logo-Dos-Torres.png"));
			
			table = new PdfPTable(4);
			table.getDefaultCell().setBorderWidth(0);
			

			// logo
			//table.addCell(new Phrase(new Chunk(headerImage, 0, 0)));
			PdfPCell cellLogo = new PdfPCell(headerImage);
			cellLogo.setBorder(PdfPCell.NO_BORDER);
			table.addCell(cellLogo);
			
			// encabezado 1
			cell = new PdfPCell(new Paragraph(getDatEncabezado1(),FontFactory.getFont(FontFactory.TIMES_BOLD, 12)));
			cell.setColspan(2);
			cell.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
			cell.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE);
			cell.setBorder(0);
			table.addCell(cell);

			
			// encabezado 2 y 3
			table2 = new PdfPTable(1);
			cell = new PdfPCell(new Paragraph(getDatEncabezado2(),fontData));
			cell.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
			cell.setVerticalAlignment(PdfPCell.ALIGN_BOTTOM);
			cell.setBorder(0);
			table2.addCell(cell);
			cell = new PdfPCell(new Paragraph(getDatEncabezado3(),fontData));
			cell.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
			cell.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE);
			cell.setBorder(0);
			table2.addCell(cell);
			table.addCell(table2);
			

			// etiquetas
			addLineLabel(getLblCodigo(), getDatCodigo());
			addLineLabel(getLblNombre(), getDatNombre());
			addLineLabel(getLblVersion(), getDatVersion());
			addLineLabel(getLblFechaDeAprobacion(), getDatFechaDeAprobacion());
			addLineLabel(getLblFechaDeExpiracion(), getDatFechaDeExpiracion());
			if(getDatFechaDeCierre()!=null && !getDatFechaDeCierre().trim().equals("")) {
				addLineLabel(getLblFechaDeCierre(), getDatFechaDeCierre());
			}
			
			addLineLabel(getLblPropietario(), getDatPropietario());
			if(getDatOrigen()!=null && !getDatOrigen().trim().equals("")) {
				addLineLabel(getLblOrigen(), getDatOrigen());
			}
			if (isPrinting()) {
				addLineLabel(getLblSolicitado(), getDatSolicitado());
				if (getDatResponsableImpresion() != null) {
					addLineLabel(getLblResponsableImpresion(), getDatResponsableImpresion());
				}
				addLineLabel(getLblImpreso(), getDatImpreso());
				addLineLabel(getLblNumeroDeCopias(), getDatNumeroDeCopias());
				addLineFull(getDatDestinatario());
			}
			
			if(getDetElaborado()!=null) {
				addTitleDetail(getLblElaborado());
				addDataDetail(getDetElaborado());
			}
			if(getDetEditado()!=null) {
				addTitleDetail(getLblEditado());
				addDataDetail(getDetEditado());
			}
			if(getDetRevisado()!=null) {
				addTitleDetail(getLblRevisado());
				addDataDetail(getDetRevisado());
			}
			if(getDetAprobado()!=null) {
				addTitleDetail(getLblAprobado());
				addDataDetail(getDetAprobado());
			}
			
			//if(getDatRazonCambio()!=null && getDatRazonCambio().trim().length()>0) {
			if(getDetCambio()!=null) {
			//if(true) {
				addLineLabel(" "," ");
				//String reason = getDatRazonCambio();
				//reason = reason.replaceAll("\"","”");
				//reason = reason.replaceAll("<br>","\n\n");
				//reason = reason.replaceAll("&nbsp;"," ");
				//reason = reason.replaceAll("\\<[^>]*>","");
				
				//addTitleDetail1(getLblCambio());
				
			//	addDataDetail1(getDetAprobado());

			//	addLineLabel1("1.0", reason,"aqui va la fecha");
				
			//	addTitleDetail1(getLblCambio());
				
		
			//	addDataDetail1(getDetCambio());
				addTitleDetail1(getLblCambio());
				addDataDetail1(getDetCambio());
		
			//	addLineLabel(" "," ");
			//	addDataDetail1(getDatRazonCambio());

				//addLineLabel1("1.0", reason,"aqui va la fecha");
			}
			
			// incluimos la tabla al documento
			documento.open();
			documento.add(table);
			
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
	
	private void addLineLabel(String label, String data) {
		if (!ToolsHTML.isEmptyOrNull(data) ) {
		cell = new PdfPCell(new Paragraph(label,fontLabel));
		cell.setBorder(0);
		table.addCell(cell);
		System.out.println("********************************data************************************");
		System.out.println(label);
		System.out.println(data);
		System.out.println("********************************data************************************");

		cell = new PdfPCell(new Paragraph((label.trim().length()>0?": ":" ").concat(data),fontData));
		cell.setColspan(3);
		cell.setBorder(0);
		table.addCell(cell);
		}
	}
	private void addLineFull(String data) {
		if(data.indexOf("<br>")!=-1) {
			String[] linea = data.split("<br>");
			for(int i=0;i<linea.length;i++) {
				cell = new PdfPCell(new Paragraph(linea[i],fontLabel));
				cell.setColspan(4);
				cell.setBorder(0);
				table.addCell(cell);
			}
		} else {
			cell = new PdfPCell(new Paragraph(data,fontLabel));
			cell.setColspan(4);
			cell.setBorder(0);
			table.addCell(cell);
		}
	}
	
	private void addLineLabel1(String label, String data, String data1 ) {
		cell = new PdfPCell(new Paragraph(label,fontLabel));
		cell.setBorder(0);
		table.addCell(cell);

		cell = new PdfPCell(new Paragraph((label.trim().length()>0?" ":" ").concat(data),fontData));
		cell.setColspan(3);
		cell.setBorder(0);
		table.addCell(cell);
	}
	
	private void addTitleDetail(TituloDetalle tituloDetalle) {
		cell = new PdfPCell(new Paragraph(tituloDetalle.getTitulo(),fontLabel));
		cell.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
		cell.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE);
		cell.setColspan(4);
		cell.setBorder(0);
		table.addCell(cell);

		cell = new PdfPCell(new Paragraph(tituloDetalle.getColumna1(),fontLabel));
		cell.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
		cell.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE);
		cell.setColspan(2);
		cell.setBorder(0);
		table.addCell(cell);

		cell = new PdfPCell(new Paragraph(tituloDetalle.getColumna2(),fontLabel));
		cell.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
		cell.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE);
		cell.setColspan(2);
		cell.setBorder(0);
		table.addCell(cell);
	}
	
	// TITULO TRES COLUMNA
	private void addTitleDetail1(TituloDetalle1 tituloDetalle1) {
		cell = new PdfPCell(new Paragraph(tituloDetalle1.getColumna1(),fontLabel));
	//	cell.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
	//	cell.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE);
	//	cell.setColspan(4);
	//	cell.setBorder(0);
		table.addCell(cell);

		cell = new PdfPCell(new Paragraph(tituloDetalle1.getColumna2(),fontLabel));
	//	cell.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
	//	cell.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE);
	//	cell.setColspan(2);
	//	cell.setBorder(0);
		table.addCell(cell);

		cell = new PdfPCell(new Paragraph(tituloDetalle1.getColumna3(),fontLabel));
	//  cell.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
	//	cell.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE);
	//	cell.setColspan(2);
	//	cell.setBorder(0);
		table.addCell(cell);
	
		cell = new PdfPCell(new Paragraph(tituloDetalle1.getColumna4(),fontLabel));
	//	cell.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
	//	cell.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE);
	//	cell.setColspan(2);
	//	cell.setBorder(0);
		table.addCell(cell);
	
	}
	
	private void addDataDetail(ArrayList dataDetalle) {
		for(int i=0; i<dataDetalle.size();i++) {
			cell = new PdfPCell(new Paragraph((String)dataDetalle.get(i),fontData));
			cell.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
			cell.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE);
			cell.setColspan(2);
			cell.setBorder(0);
			table.addCell(cell);
		}
	}
	
	private void addDataDetail1(ArrayList dataDetalle) {
		for(int i=0; i<dataDetalle.size();i++) {
			cell = new PdfPCell(new Paragraph((String)dataDetalle.get(i),fontData));
			//cell.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
			//cell.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE);
		//	if (i>0){
		//		cell.setColspan(2);
		//	}
			//cell.setColspan(2);
			//cell.setBorder(0);
			table.addCell(cell);
		}
	}
	
	
	
	// getters and setters
	public String getLblCodigo() {
		return lblCodigo;
	}
	public void setLblCodigo(String lblCodigo) {
		this.lblCodigo = lblCodigo;
	}
	public String getLblFechaDeAprobacion() {
		return lblFechaDeAprobacion;
	}
	public void setLblFechaDeAprobacion(String lblFechaDeAprobacion) {
		this.lblFechaDeAprobacion = lblFechaDeAprobacion;
	}
	public String getLblFechaDeExpiracion() {
		return lblFechaDeExpiracion;
	}
	public void setLblFechaDeExpiracion(String lblFechaDeExpiracion) {
		this.lblFechaDeExpiracion = lblFechaDeExpiracion;
	}
	public String getLblFechaDeCierre() {
		return lblFechaDeCierre;
	}
	public void setLblFechaDeCierre(String lblFechaDeCierre) {
		this.lblFechaDeCierre = lblFechaDeCierre;
	}
	
	public String getLblImpreso() {
		return lblImpreso;
	}
	public void setLblImpreso(String lblImpreso) {
		this.lblImpreso = lblImpreso;
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
	public String getLblPropietario() {
		return lblPropietario;
	}
	public void setLblPropietario(String lblPropietario) {
		this.lblPropietario = lblPropietario;
	}
	public String getLblSolicitado() {
		return lblSolicitado;
	}
	public void setLblSolicitado(String lblSolicitado) {
		this.lblSolicitado = lblSolicitado;
	}
	public String getLblResponsableImpresion() {
		return lblResponsableImpresion;
	}
	public void setLblResponsableImpresion(String lblResponsableImpresion) {
		this.lblResponsableImpresion = lblResponsableImpresion;
	}
	
	public String getLblSoloPropietario() {
		return lblSoloPropietario;
	}
	public void setLblSoloPropietario(String lblSoloPropietario) {
		this.lblSoloPropietario = lblSoloPropietario;
	}
	public String getLblVersion() {
		return lblVersion;
	}
	public void setLblVersion(String lblVersion) {
		this.lblVersion = lblVersion;
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

	public String getDatFechaDeAprobacion() {
		return datFechaDeAprobacion;
	}

	public void setDatFechaDeAprobacion(String datFechaDeAprobacion) {
		this.datFechaDeAprobacion = datFechaDeAprobacion;
	}

	public String getDatFechaDeExpiracion() {
		return datFechaDeExpiracion;
	}

	public void setDatFechaDeExpiracion(String datFechaDeExpiracion) {
		this.datFechaDeExpiracion = datFechaDeExpiracion;
	}
	public String getDatFechaDeCierre() {
		return datFechaDeCierre;
	}

	public void setDatFechaDeCierre(String datFechaDeCierre) {
		this.datFechaDeCierre = datFechaDeCierre;
	}

	public String getDatImpreso() {
		return datImpreso;
	}

	public void setDatImpreso(String datImpreso) {
		this.datImpreso = datImpreso;
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

	public String getDatPropietario() {
		return datPropietario;
	}

	public void setDatPropietario(String datPropietario) {
		this.datPropietario = datPropietario;
	}

	public String getDatSolicitado() {
		return datSolicitado;
	}

	public void setDatSolicitado(String datSolicitado) {
		this.datSolicitado = datSolicitado;
	}
	public String getDatResponsableImpresion() {
		return datResponsableImpresion;
	}

	public void setDatResponsableImpresion(String datResponsableImpresion) {
		this.datResponsableImpresion = datResponsableImpresion;
	}

	public String getDatSoloPropietario() {
		return datSoloPropietario;
	}

	public void setDatSoloPropietario(String datSoloPropietario) {
		this.datSoloPropietario = datSoloPropietario;
	}

	public String getDatVersion() {
		return datVersion;
	}

	public void setDatVersion(String datVersion) {
		this.datVersion = datVersion;
	}

	public TituloDetalle getLblAprobado() {
		return lblAprobado;
	}

	public void setLblAprobado(TituloDetalle lblAprobado) {
		this.lblAprobado = lblAprobado;
	}

	public TituloDetalle getLblEditado() {
		return lblEditado;
	}

	public void setLblEditado(TituloDetalle lblEditado) {
		this.lblEditado = lblEditado;
	}

	public TituloDetalle getLblElaborado() {
		return lblElaborado;
	}

	public void setLblElaborado(TituloDetalle lblElaborado) {
		this.lblElaborado = lblElaborado;
	}

	public TituloDetalle getLblRevisado() {
		return lblRevisado;
	}

	public void setLblRevisado(TituloDetalle lblRevisado) {
		this.lblRevisado = lblRevisado;
	}
	
	// nuevo titulo razon de cambio
	public TituloDetalle1 getLblCambio() {
		return lblCambio;
	}

	public void setLblCambio(TituloDetalle1 lblCambio) {
		this.lblCambio = lblCambio;
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

	public ArrayList getDetRevisado() {
		return detRevisado;
	}

	public void setDetRevisado(ArrayList detRevisado) {
		this.detRevisado = detRevisado;
	}

	public boolean isPrinting() {
		return printing;
	}

	public void setPrinting(boolean printing) {
		this.printing = printing;
	}

	public String getLblOrigen() {
		return lblOrigen;
	}

	public void setLblOrigen(String lblOrigen) {
		this.lblOrigen = lblOrigen;
	}

	public String getDatOrigen() {
		return datOrigen;
	}

	public void setDatOrigen(String datOrigen) {
		this.datOrigen = datOrigen;
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
	
	
}
