package com.desige.webDocuments.persistent.managers;

import java.awt.print.PageFormat;
import java.awt.print.Paper;
import java.awt.print.PrinterJob;
import javax.print.PrintService;
import org.jpedal.PdfDecoder;


public class PrintPDF {

	//...
	public final void imprimir(final String fileName) {
	    PdfDecoder pdf = null;
	    try {
	        // Obtengo el servicio de impresión, asigno el 0
	        // aunque habría que ir mirando una a una si es la
	        // impresora que queremos
	        PrintService[] service =
	            PrinterJob.lookupPrintServices();
	        PrinterJob printJob = PrinterJob.getPrinterJob();
	        printJob.setPrintService(service[0]);

	        for(int i=0;i<service.length;i++){
	        	//System.out.println(service[i]);
	        }
	        //if(true)return;
	        
	        // Asigno el tamaño del papel (A4)
	        Paper paper = new Paper();
	        paper.setSize(595, 842);
	        paper.setImageableArea(0, 0, 595, 842);
	        PageFormat pf = printJob.defaultPage();
	        pf.setPaper(paper);

	        // Cargo el PDF para imprimir
	        pdf = new PdfDecoder(true);
	        pdf.openPdfFile(fileName);
	        pdf.setPageFormat(pf);
	            
	        // Mando imprimir
	        printJob.setPageable(pdf);
	        printJob.print();
	    } catch (Exception e) {
	        e.printStackTrace();
	    } finally {
	    	if(pdf!=null)
	        pdf.closePdfFile();
	    }
	}
	
	public static void main(String[] args) {
		PrintPDF p = new PrintPDF();
		p.imprimir("c:/temp/test.pdf");
		//for(int i=0;i<2;i++) 
			//p.imprimir("c:/temp/test.pdf");
	}
}
