/* -*- Mode: java; tab-width: 2; c-basic-offset: 2 -*- */
/*
  concat_pdf, version 1.0, adapted from the iText tools
  catenate input PDF files and write the results into a new PDF
  visit: www.pdfhacks.com/concat/

  This code is free software. It may only be copied or modified
  if you include the following copyright notice:

  This class by Mark Thompson. Copyright (c) 2002 Mark Thompson.

  This code is distributed in the hope that it will be useful,
  but WITHOUT ANY WARRANTY; without even the implied warranty of
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 */

import java.io.*;

import com.itextpdf.text.Document;
import com.itextpdf.text.pdf.PRAcroForm;
import com.itextpdf.text.pdf.PdfCopy;
import com.itextpdf.text.pdf.PdfImportedPage;
import com.itextpdf.text.pdf.PdfReader;

public class Concat extends java.lang.Object {

// comentario en la ramas ****
public static void main( String args[] ) {
		//args = new String[]{"c:/temp/uno.pdf","c:/temp/dos.pdf","c:/temp/temp.pdf"};
		//args = new String[]{"c:/temp/temp.pdf","c:/temp/tres.pdf","c:/temp/fin.pdf"};
	  System.out.println("iniciando sistema*****************************Argumentos2 " + args.length);
    if( 2<= args.length ) {
      try {
        int input_pdf_ii= 0;
        String outFile= args[ args.length-1 ];
        Document document= null;
        PdfCopy writer= null;

        while( input_pdf_ii < args.length- 1 ) {
          // we create a reader for a certain document
          PdfReader reader= new PdfReader( args[input_pdf_ii] );
          reader.consolidateNamedDestinations();

          // we retrieve the total number of pages
          int num_pages= reader.getNumberOfPages();
          ////System.out.println( "There are "+ num_pages+" pages in "+ args[input_pdf_ii] );

          if( input_pdf_ii== 0 ) {
            // step 1: creation of a document-object
            document= new Document( reader.getPageSizeWithRotation(1) );

            // step 2: we create a writer that listens to the document
            writer= new PdfCopy( document, new FileOutputStream(outFile) );

            // step 3: we open the document
            document.open();
          }

          // step 4: we add content
          PdfImportedPage page;
          for( int ii= 0; ii< num_pages; ) {
            ++ii;
            page= writer.getImportedPage( reader, ii );
            writer.addPage( page );
            ////System.out.println( "Processed page "+ ii );
          }

          PRAcroForm form= reader.getAcroForm();
          if( form!= null ) {
        	  writer.addDocument( reader );
          }

          ++input_pdf_ii;
        }

        // step 5: we close the document
        document.close();
      }
      catch( Exception ee ) {
        ee.printStackTrace();
      }
    }
    else { // input error
      System.err.println("arguments: file1 [file2 ...] destfile");
    }
  }
}
