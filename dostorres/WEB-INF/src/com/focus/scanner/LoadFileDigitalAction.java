package com.focus.scanner;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.desige.webDocuments.persistent.managers.HandlerDBUser;
import com.desige.webDocuments.to.DigitalTO;
import com.desige.webDocuments.utils.ApplicationExceptionChecked;
import com.desige.webDocuments.utils.Constants;
import com.desige.webDocuments.utils.ToolsHTML;
import com.desige.webDocuments.utils.beans.SuperAction;
import com.desige.webDocuments.utils.beans.Users;
import com.focus.util.Archivo;

/**
 * Title: DigitalizarLoadAction.java <br/> Copyright: (c) 2004 Focus Consulting
 * C.A.<br/>
 * 
 */
public class LoadFileDigitalAction extends SuperAction {
	private static Logger log = LoggerFactory.getLogger(LoadFileDigitalAction.class.getName());
	
	public static final int BUFFER_SIZE = 4096;
	public static final int MAX_CHUNK_SIZE = 1000 * BUFFER_SIZE; // 4.1MB
	public static final String FILE_NAME_HEADER = "Transfer-File-Name";
	public static final String CLIENT_ID_HEADER = "Transfer-Client-ID";
	public static final String FILE_CHUNK_HEADER = "Transfer-File-Chunk";
	public static final String FILE_CHUNK_COUNT_HEADER = "Transfer-File-Chunk-Count";
	public static final String USER_ID_HEADER = "User-id-Chunk";
	public static final String ID_DIGITAL = "idDigital";
	
	private Users usuario = null;
	
	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
		try {
			super.init(mapping, form, request, response);
			if(request.getIntHeader(USER_ID_HEADER) != -1){
				usuario = HandlerDBUser.load(request.getIntHeader(USER_ID_HEADER), true);
				//usuario = getUserSession();
				doPut(request,response);
			} else {
				log.info("No llego a esta peticion HTTP el id del usuario, rechazamos la peticion");
			}
		} catch (ServletException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ApplicationExceptionChecked e) {
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
	}

	@SuppressWarnings("unlikely-arg-type")
	public void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String fileName = req.getHeader(FILE_NAME_HEADER);
		if (fileName == null) {
			log.info("Filename not specified");
			resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Filename not specified");
		}

		String clientID = req.getHeader(CLIENT_ID_HEADER);
		if (null == clientID) {
			log.info("Missing Client ID");
			resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Missing Client ID");
		}

		int nChunks = req.getIntHeader(FILE_CHUNK_COUNT_HEADER);
		int chunk = req.getIntHeader(FILE_CHUNK_HEADER);

		if (nChunks == -1 || chunk == -1) {
			log.info("Missing chunk information");
			resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Missing chunk information");
		}

		if (chunk == 0) {
			// check permission to create file here
		}

		OutputStream out = null;
		StringBuffer name = new StringBuffer();
		name = new StringBuffer(ToolsHTML.getPathTmp()).append(usuario.getUser()).append(File.separatorChar);
		name.append("file_scanner_").append(usuario.getIdPerson()).append(".pdf");
		
		// si estoy en el modulo de digitalizacion el archivo ira al digitalizado
	 //   @SuppressWarnings("unused")
	//	boolean isModuloDigital = req.getHeader(Constants.MODULO_ACTIVO).equals(Constants.MODULO_DIGITALIZAR);
	//	String idDigital = req.getHeader(ID_DIGITAL);
		
/*
	    if(ToolsHTML.isNumeric(idDigital) && Integer.parseInt(idDigital)>0){
			DigitalTO digitalTO = new DigitalTO();
			digitalTO.setIdDigital(idDigital);
			
			name.setLength(0);
			name.append(digitalTO.getNameFileDiskFullPath());
			log.info("Estoy en el modulo de digitalizar: " + digitalTO.getNameFileDiskFullPath());
		}
		*/
		
	    /*
	    DigitalTO digitalTO = null; //(isModuloDigital?(DigitalTO)req.getSession().getAttribute("digitalTO"):null);
		if(digitalTO!=null) {
			name.setLength(0);
			name.append(digitalTO.getNameFileDiskFullPath());
			log.info("Estoy en el modulo de digitalizar: " + digitalTO.getNameFileDiskFullPath());
		}
		*/
		
		if (nChunks == 1) {
			// escribimos el archivo a disco
			//out = new FileOutputStream(fileName);
			out = new FileOutputStream(name.toString());
			log.info("Se escribira el archivo destino en: " + name.toString());
		} else {
			String path = getTempFile(clientID);
			out = new FileOutputStream(path, (chunk > 0));
			log.info("Se escribira el archivo destino en: " + path);
		}

		InputStream in = req.getInputStream();
		byte[] buf = new byte[BUFFER_SIZE];
		int bytesRead = 0;
		while (true) {
			int read = in.read(buf);
			if (read == -1) {
				break;
			} else if (read > 0) {
				bytesRead += read;
				out.write(buf, 0, read);
			}
		}
		in.close();
		out.close();
		log.info("Finalizado proceso de copia desde request.getInputStream()");
		
		if (nChunks > 1 && chunk == nChunks - 1) {
			File tmpFile = new File(getTempFile(clientID));
			File destFile = new File(name.toString());
			if (destFile.exists()) {
				destFile.delete();
			}
			//if (!tmpFile.renameTo(destFile)) {
			if(!Archivo.renameTo(tmpFile, destFile)) {
				resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Unable to create file");
				log.info("Unable to create file " + destFile);
			} else {
				resp.setStatus(HttpServletResponse.SC_OK);
				log.info("Proceso finalizado con exito");
			}
		} else {
			resp.setStatus(HttpServletResponse.SC_OK);
			log.info("Proceso finalizado con exito");
		}
	}

	static String getTempFile(String clientID) {
		StringBuffer name = new StringBuffer(ToolsHTML.getPathTmp());
		name.append(clientID);
		name.append(".tmp");
		//name.append("file_scanner_").append(usuario.getIdPerson()).append(".pdf");
		return name.toString();
	}

}
