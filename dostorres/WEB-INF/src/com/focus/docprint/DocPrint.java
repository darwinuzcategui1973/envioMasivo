package com.focus.docprint;

import java.io.File;

import org.jawin.COMException;
import org.jawin.FuncPtr;
import org.jawin.ReturnFlags;

public class DocPrint {

	public static void documentConverter(String fileIn, String fileOut) throws COMException {
		FuncPtr metodo = null;
		int g = 0;
		
		char[] cad = {'h','o','l','a'};
		
		metodo = new FuncPtr("docPrintQWeb.dll", "documentConverter");
		//g = metodo.invoke_I(fileIn, fileOut, ReturnFlags.CHECK_HRESULT);
		g = metodo.invoke_I("abc", ReturnFlags.CHECK_HRESULT);
		//g = metodo.invoke_I(ReturnFlags.CHECK_HRESULT);
		
		File f = new File("C:/temp/test.pdf");
		if (f.exists()) {
			//System.out.println("Archivo creado");
		} else {
			//System.out.println("Error.");
		}

	}

	public static void main(String[] args) throws COMException {
		
		//DocPrint.documentConverter("jC:/temp/test.doc", "C:/temp/test.pdf");
		//DocPrint.documentConverter("C:/temp/test.doc", "C:/temp/test.pdf");
		
		//System.out.println(System.getenv("JAVA_HOME"));
		//System.out.println(System.getProperty("java.library.path"));
		File a = new File("a.txt");

		StringBuffer ubicac = new StringBuffer();
		ubicac.append(a.getAbsolutePath().substring(0,a.getAbsolutePath().length()-5));
		ubicac.append("WEB-INF").append(File.separatorChar);
		ubicac.append("classes").append(File.separatorChar);
		ubicac.append("com").append(File.separatorChar);
		ubicac.append("focus").append(File.separatorChar);
		ubicac.append("docprint").append(File.separatorChar);
		
		//System.out.println(ubicac.toString());
		
	}

}
