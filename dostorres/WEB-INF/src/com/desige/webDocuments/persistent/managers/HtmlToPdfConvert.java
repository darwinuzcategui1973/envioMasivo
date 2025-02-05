package com.desige.webDocuments.persistent.managers;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;

import org.xhtmlrenderer.pdf.ITextRenderer;


public class HtmlToPdfConvert {

	public static void generatePDF(String inputHtmlPath, String outputPdfPath)
	{
	    try {
	        String url = new File(inputHtmlPath).toURI().toURL().toString();
	        System.out.println("URL: " + url);

	        OutputStream out = new FileOutputStream(outputPdfPath);
	        
	        OutputStreamWriter osw = new OutputStreamWriter(out, StandardCharsets.UTF_8);

	        //Flying Saucer part
	        ITextRenderer renderer = new ITextRenderer();

	        renderer.setDocument(url);
	        renderer.layout();
	        renderer.createPDF(out);

	        out.close();
	    } catch (IOException e) {
	        // TODO Auto-generated catch block
	        e.printStackTrace();
	    } catch (com.lowagie.text.DocumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args){
	    System.out.println("Start!");
	    String inputFile = "/home/linuxlitedarwin2/Desktop/appproyectos/qweb5/workspaces/qweb/tmp/admin/prueba/index2.html";
	    String outputFile = "/home/linuxlitedarwin2/Desktop/appproyectos/qweb5/workspaces/qweb/tmp/admin/prueba/testpdf.pdf";
	    //String inputFile = "D:/appproyectos/qweb5/workspace/qweb/tmp/admin/expediente6/Portada6.html";
	    //String outputFile = "D:/appproyectos/qweb5/workspace/qweb/tmp/admin/expediente6/TestPdf.pdf";

	    generatePDF(inputFile, outputFile);

	    System.out.println("Done!");
	}
	
}
