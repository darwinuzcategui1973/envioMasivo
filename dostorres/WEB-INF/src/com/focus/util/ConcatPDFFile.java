package com.focus.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.itextpdf.text.Document;
import com.itextpdf.text.pdf.PdfCopy;
import com.itextpdf.text.pdf.PdfImportedPage;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.SimpleBookmark;

/**
 * Clase utilitaria para concatenar varios PDFs
 * 
 * @author Usuario
 *
 */
public class ConcatPDFFile {
	private static final Logger log = LoggerFactory.getLogger(ConcatPDFFile.class);
	
	/**
	 * 
	 * @param filesInput
	 * @param output
	 */
	public static void concat(String[] filesInput, String output) {
		List<String> listInput = Arrays.asList(filesInput);
		
		concat(listInput, output);
	}
	
	
	/**
	 * 
	 * @param filesInput
	 * @param output
	 */
	public static void concat(List<String> filesInput, String output) {
		try {
			
			log.info("Intentando unir " + filesInput.size() 
					+ " PDFs en " + output);
			
			if (filesInput.size() < 1 || output == null) {
				throw new Exception("PdfSecurity: No hay archivos para concatenar");
			}
			
			File destinationFile = new File(output);
			if(destinationFile.exists()) {
				FileUtils.forceDelete(destinationFile);
			}

			if (filesInput.size() == 1 || output == null) {
				File source = new File(filesInput.get(0));
				File destination = new File(output);
				Archivo.renameTo(source, destination);
				return;
			}
			String[] files = new String[filesInput.size() + 1];
			for (int i = 0; i < filesInput.size(); i++) {
				files[i] = filesInput.get(i);
			}
			files[files.length - 1] = output;

			int pageOffset = 0;
			ArrayList<HashMap<String, Object>> master = new ArrayList<HashMap<String, Object>>();
			int f = 0;
			String outFile = files[files.length - 1];
			Document document = null;
			PdfCopy writer = null;
			while (f < files.length - 1) {
				// we create a reader for a certain document
				log.info("Procesando (concatenando) " + files[f]);
				Archivo.waitCreationFile(files[f], 20);
				PdfReader reader = null;
				
				for (int xx = 0; xx < 10; xx++) {
					try {
						reader = new PdfReader(files[f]);
						break;
					} catch (IOException ex) {
						log.error("Error: " + ex.getLocalizedMessage());
						if ((xx + 1) == 10){
							throw ex;
						}
						//System.out.println("Intentamos nuevamente " + xx);
						Thread.sleep(500);
					}
				}
				reader.consolidateNamedDestinations();
				// we retrieve the total number of pages
				int n = reader.getNumberOfPages();
				List<HashMap<String, Object>> bookmarks = SimpleBookmark.getBookmark(reader);
				if (bookmarks != null) {
					if (pageOffset != 0)
						SimpleBookmark.shiftPageNumbers(bookmarks, pageOffset, null);
					master.addAll(bookmarks);
				}
				pageOffset += n;
				// //System.out.println("There are " + n + " pages in " +
				// files[f]);

				if (f == 0) {
					// step 1: creation of a document-object
					document = new Document(reader.getPageSizeWithRotation(1));
					// step 2: we create a writer that listens to the document
					writer = new PdfCopy(document, new FileOutputStream(outFile));
					// step 3: we open the document
					document.open();
				}
				
				// step 4: we add content
				PdfImportedPage page;
				for (int i = 0; i < n;) {
					++i;
					page = writer.getImportedPage(reader, i);
					writer.addPage(page);
					// //System.out.println("Processed page " + i);
				}
				f++;
				
				reader.close();
				reader = null;
			}
			
			if (master.size() > 0){
				writer.setOutlines(master);
			}
			
			// step 5: we close the document
			writer.flush();
			writer.close();
			document.close();
			
			System.gc();
		} catch (Exception e) {
			log.error("Error: " + e.getLocalizedMessage(), e);
		}
	}
}
