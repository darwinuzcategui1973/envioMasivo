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
public class LoadFileDigitalRechazadoAction extends SuperAction {
	private static Logger log = LoggerFactory.getLogger(LoadFileDigitalRechazadoAction.class.getName());
	
	
	public static final int BUFFER_SIZE = 4096;
	public static final int MAX_CHUNK_SIZE = 1000 * BUFFER_SIZE; // 4.1MB
	public static final String FILE_NAME_HEADER = "Transfer-File-Name";
	public static final String CLIENT_ID_HEADER = "Transfer-Client-ID";
	public static final String FILE_CHUNK_HEADER = "Transfer-File-Chunk";
	public static final String FILE_CHUNK_COUNT_HEADER = "Transfer-File-Chunk-Count";
	
	private Users usuario = null;
	

	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {

		super.init(mapping, form, request, response);
		
		try {
			usuario = getUserSession();
			doPut(request,response);
		} catch (ServletException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ApplicationExceptionChecked e) {
			e.printStackTrace();
		}
		
		return null;
	}

	public void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String fileName = req.getHeader(FILE_NAME_HEADER);
		if (fileName == null) {
			resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Filename not specified");
		}

		String clientID = req.getHeader(CLIENT_ID_HEADER);
		if (null == clientID) {
			resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Missing Client ID");
		}

		int nChunks = req.getIntHeader(FILE_CHUNK_COUNT_HEADER);
		int chunk = req.getIntHeader(FILE_CHUNK_HEADER);

		if (nChunks == -1 || chunk == -1) {
			resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Missing chunk information");
		}

		if (chunk == 0) {
			// check permission to create file here
		}

		OutputStream out = null;
		StringBuffer name = new StringBuffer();
		name = new StringBuffer(ToolsHTML.getPathTmp());
		name.append("file_scanner_").append(usuario.getIdPerson()).append(".pdf");
		/*
	    boolean isModuloDigital = req.getSession().getAttribute(Constants.MODULO_ACTIVO).equals(Constants.MODULO_DIGITALIZAR);
		String idDigital = req.getSession().getAttribute("idDigital")!=null?String.valueOf(req.getSession().getAttribute("idDigital")):"";
		DigitalTO digitalTO = (isModuloDigital?(DigitalTO)req.getSession().getAttribute("digitalTO"):null);
		
		if(digitalTO!=null) {
			name.setLength(0);
			name.append(digitalTO.getNameFileDiskFullPath());
		}
		*/

		if (nChunks == 1) {
			// escribimos el archivo a disco
			//out = new FileOutputStream(fileName);
			out = new FileOutputStream(name.toString());
		} else {
			out = new FileOutputStream(getTempFile(clientID), (chunk > 0));
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

		if (nChunks > 1 && chunk == nChunks - 1) {
			File tmpFile = new File(getTempFile(clientID));
			File destFile = new File(name.toString());
			if (destFile.exists()) {
				destFile.delete();
			}
			//if (!tmpFile.renameTo(destFile)) {
			if(!Archivo.renameTo(tmpFile, destFile)) {
				resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Unable to create file");
			} else {
				resp.setStatus(HttpServletResponse.SC_OK);
			}
		} else {
			resp.setStatus(HttpServletResponse.SC_OK);
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
