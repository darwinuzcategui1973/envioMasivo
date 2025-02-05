package com.desige.webDocuments.document.servlet;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.desige.webDocuments.util.Encryptor;
import com.desige.webDocuments.util.EncryptorMD5;

public class SupportServlet extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4289813572285783242L;

	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("text/html");
		PrintWriter out = response.getWriter();
		
		String data1 = request.getParameter("data1");
		String data2 = request.getParameter("data2");
		String data3 = request.getParameter("data3");
		

		if(data3!=null && data3.equals("*F0cusvision91*")) {
			out.println("<title>Resultados</title>" + "<body bgcolor=FFFFFF>");
	
			out.println("<ul>");
			
			try {
				int numero = Integer.parseInt(data1);
				int numeroViewer = Integer.parseInt(data2);
				
				out.println("<h2>Licencia para " + numero + " usuarios y " + (numeroViewer>=1000000?"ilimitados":numeroViewer) + " viewers </h2>");
				String plaintext = data1;
				String plaintextViewer = data2;
				
				String key = "16-171-151-56-196-97-64-7";
				String license = Encryptor.encrypt(key, plaintext);
				String licenseViewer = Encryptor.encrypt(key.concat("9"), plaintextViewer);
				
				out.println("<li>license = " + license + "</li>");
				out.println("<li>license viewer = " + licenseViewer + "</li>");
				out.println("<li>FTP     = " + EncryptorMD5.getMD5(license+":FTP") + "</li>");
				out.println("<li>SACOP   = " + EncryptorMD5.getMD5(license+":SACOP") + "</li>");
				out.println("<li>InTouch = " + EncryptorMD5.getMD5(license+":InTouch") + "</li>");
				out.println("<li>Record  = " + EncryptorMD5.getMD5(license+":Record") + "</li>");
				out.println("<li>Files   = " + EncryptorMD5.getMD5(license+":Files") + "</li>");
				out.println("<li>Digital = " + EncryptorMD5.getMD5(license+":Digital") + "</li>");
				out.println("<li>Edicion:</li>");
				out.println("<ul>");
				out.println("<li> OSP= " + EncryptorMD5.getMD5(license+":osp") + "</li>");
				out.println("<li> ME= " + EncryptorMD5.getMD5(license+":me") + "</li>");
				out.println("<li> PYME= " + EncryptorMD5.getMD5(license+":pyme") + "</li>");
				out.println("<li> Profesional = " + EncryptorMD5.getMD5(license+":profesional") + "</li>");
				out.println("<li> Gobierno = " + EncryptorMD5.getMD5(license+":gobierno") + "</li>");
				out.println("</ul>");
				
			} catch (Exception ex) {
				//System.out.println("Error: se esperaba un entero");
				System.exit(1);
			}
			out.println("</ul>");
		} else {
			out.print("<h2>Error</h2>");
		}

		out.println("<P>Return to <a href='support.jsp'>form</a>");
		out.close();
	}

}
