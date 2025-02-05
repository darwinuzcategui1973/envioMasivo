package com.desige.webDocuments.document.actions;

import java.io.File;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import javax.mail.Address;
import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.DiskFileUpload;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

/**
 * Title: FuncionesEndosos.java<br>
 * Copyright: (c) 2003 Consis International<br>
 * Company: Consis International (CON)<br>
 * 
 * @author Nelson Crespo (NC)
 * @version Acsel-e v2.2 <br>
 *          Changes:<br>
 *          <ul>
 *          <li>05/07/2004 (NC) Creation</li>
 *          <ul>
 */
public class UpploadFileAction extends Action {
	
    // location to store file uploaded
    private static final String UPLOAD_DIRECTORY = "upload";
 
    // upload settings
    private static final int MEMORY_THRESHOLD   = 1024 * 1024 * 3;  // 3MB
    private static final int MAX_FILE_SIZE      = 1024 * 1024 * 20; // 20MB
    private static final int MAX_REQUEST_SIZE   = 1024 * 1024 * 30; // 30MB
	
	public ActionForward execute(ActionMapping actionMapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
		procesaFicheros(request, response);
		try {
			// prueba();
		} catch (Exception e) {

		}
		return (actionMapping.findForward("error"));
	}

	public boolean procesaFicherosXXX(HttpServletRequest request, HttpServletResponse response) {
		try {

			// checks if the request actually contains upload file
	        if (!ServletFileUpload.isMultipartContent(request)) {
	            // if not, we stop here
	            PrintWriter writer = response.getWriter();
	            writer.println("Error: el formulario debe ser de tipo=multipart/form-data.");
	            writer.flush();
	            return false;
	        }
	 
	        // configures upload settings
	        DiskFileItemFactory factory = new DiskFileItemFactory();
	        // sets memory threshold - beyond which files are stored in disk
	        factory.setSizeThreshold(MEMORY_THRESHOLD);
	        // sets temporary location to store files
	        factory.setRepository(new File("/tmp")); //System.getProperty("java.io.tmpdir")));
	 
	        ServletFileUpload upload = new ServletFileUpload(factory);
	         
	        // sets maximum size of upload file
	        upload.setFileSizeMax(MAX_FILE_SIZE);
	         
	        // sets maximum size of request (include file + form data)
	        upload.setSizeMax(MAX_REQUEST_SIZE);
	 
	        // constructs the directory path to store upload file
	        // this path is relative to application's directory
	        //String uploadPath = ToolsHTML.getPath()
	        //        + File.separator + UPLOAD_DIRECTORY;
	        String uploadPath = "C:\\Program Files\\up\\";
	         
	        // creates the directory if it does not exist
	        File uploadDir = new File(uploadPath);
	        if (!uploadDir.exists()) {
	            uploadDir.mkdir();
	        }
	 
	        try {
	            // parses the request's content to extract file data
	            @SuppressWarnings("unchecked")
	            List<FileItem> formItems = upload.parseRequest(request);
	 
	            if (formItems != null && formItems.size() > 0) {
	                // iterates over form's fields
	                for (FileItem item : formItems) {
	                    // processes only fields that are not form fields
	                    if (!item.isFormField()) {
	                        String fileName = new File(item.getName()).getName();
	                        String filePath = uploadPath + File.separator + fileName;
	                        File storeFile = new File(filePath);
	 
	                        // saves the file on disk
	                        item.write(storeFile);
	                        request.setAttribute("message", "El archivo fue procesado exitosamente!");
	                    }
	                }
	            }
	        } catch (Exception ex) {
	            request.setAttribute("message","Ocurrio un error al subir el archivo: " + ex.getMessage());
	        }			
			
		} catch (Exception e) {
			return false;
		}
		return true;
	}

	public boolean procesaFicheros(HttpServletRequest req, HttpServletResponse response) {
		try {
			DiskFileUpload fu = new DiskFileUpload();
			// System.out.println("Procesando requerimiento.....");
			fu.setSizeMax(1024 * 512); // 512 K
			fu.setSizeThreshold(4096);
			fu.setRepositoryPath("/tmp");
			List fileItems = fu.parseRequest(req);
			// System.out.println("fileItems = " + fileItems);
			if (fileItems == null) {
				// System.out.println("La lista es nula");
				return false;
			}
			Iterator i = fileItems.iterator();
			FileItem actual = null;
			while (i.hasNext()) {
				actual = (FileItem) i.next();
				String fileName = actual.getName();
				File fichero = new File(fileName);
				fichero = new File("C:\\Program Files\\up\\" + fichero.getName());
				actual.write(fichero);
			}

		} catch (Exception e) {
			return false;
		}
		return true;
	}

	private void prueba() throws Exception {
		MimeMessage mensaje;
		Properties propiedades = System.getProperties();
		propiedades.put("mail.smtp.host", "mail.consisint.com");// IPcorreo);
		Session sesion = Session.getDefaultInstance(propiedades, null);

		mensaje = new MimeMessage(sesion);
		Address origen = new InternetAddress("ncrespo@consisint.com");// eMailOrigen);
		Address destino = new InternetAddress("nmatheus@hotmail.com");// eMailReceptor);
		mensaje.setFrom(origen);
		mensaje.addRecipient(Message.RecipientType.TO, destino);
		mensaje.setSubject("Esto es una prueba desde mi Aplicacion");// asunto);
		mensaje.setText("Si mi calculos son correctos esto es el texto que va a llegar");// respuesta);
		Transport.send(mensaje);

	}
}
