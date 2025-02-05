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

import com.desige.webDocuments.dao.DigitalDAO;
import com.desige.webDocuments.persistent.managers.HandlerDBUser;
import com.desige.webDocuments.to.DigitalTO;
import com.desige.webDocuments.utils.ApplicationExceptionChecked;
import com.desige.webDocuments.utils.Constants;
import com.desige.webDocuments.utils.StringUtil;
import com.desige.webDocuments.utils.ToolsHTML;
import com.desige.webDocuments.utils.beans.SuperAction;
import com.desige.webDocuments.utils.beans.Users;
import com.focus.util.Archivo;

/**
 * Title: DigitalizarLoadLoteAction.java <br/> Copyright: (c) 2004 Focus Consulting
 * C.A.<br/>
 * 
 */
public class LoadFileDigitalLoteAction extends SuperAction {
	private static Logger log = LoggerFactory.getLogger(LoadFileDigitalLoteAction.class.getName());
	
	
	public static final int BUFFER_SIZE = 4096;
	public static final int MAX_CHUNK_SIZE = 1000 * BUFFER_SIZE; // 4.1MB
	public static final String FILE_NAME_HEADER = "Transfer-File-Name";
	public static final String CLIENT_ID_HEADER = "Transfer-Client-ID";
	public static final String FILE_CHUNK_HEADER = "Transfer-File-Chunk";
	public static final String FILE_CHUNK_COUNT_HEADER = "Transfer-File-Chunk-Count";
	public static final String USER_ID_HEADER = "User-id-Chunk";
	
	private Users usuario = null;
	
	private boolean isScan = true;

	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
		try {
			super.init(mapping, form, request, response);
			
			if(request.getIntHeader(USER_ID_HEADER) != -1){
				//usuario = getUserSession();
				usuario = HandlerDBUser.load(request.getIntHeader(USER_ID_HEADER), true);
				log.info("Leido usuario: " + usuario);
				isScan = true;
				if(request.getParameter("isScan")!=null && request.getParameter("isScan").equals("false")) {
					isScan = false;
				}
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
		String extension = ".pdf";
		StringBuffer name = new StringBuffer();
		name = new StringBuffer(ToolsHTML.getPathTmp());
		name.append("file_scanner_").append(usuario.getIdPerson());
		if(!isScan) {
			StringBuffer sb = new StringBuffer(fileName);
			sb = sb.reverse();
			extension = sb.substring(0, sb.indexOf(".")+1);
			sb.setLength(0);
			sb.append(extension);
			extension = sb.reverse().toString();
			name.append(extension);
		} else {
			name.append(".pdf");
		}
		
		log.info("Nombre del archivo: " + name);

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
				log.info("Proceso finalizado con exito " + destFile);
			}
		} else {
			resp.setStatus(HttpServletResponse.SC_OK);
			log.info("Proceso finalizado con exito");
		}
		
		try {
			File destFile = new File(name.toString());
			
			if(destFile.exists()) {
				// registramos el archivo
				DigitalDAO digitalDAO = new DigitalDAO();
				DigitalTO digitalTO = new DigitalTO();
				digitalTO.setIdDigital(String.valueOf(digitalDAO.nextId()));
				if(!isScan) {
					digitalTO.setNameFile(fileName);
					digitalTO.setNameDocument(StringUtil.getOnlyNameFile(fileName));
					
					if(digitalTO.getNameDocument().indexOf(".")!=-1) {
						StringBuffer sb = new StringBuffer(digitalTO.getNameDocument()).reverse();
						String nameFile = sb.toString();
						nameFile = nameFile.substring(nameFile.indexOf(".")+1); 
						sb.setLength(0);
						sb.append(nameFile);
						digitalTO.setNameDocument(sb.reverse().toString());
					}			
				} else {
					//digitalTO.setNameFile(name.toString());
					digitalTO.setNameFile(digitalTO.getNameFileDisk());
				}

				digitalTO.setType(String.valueOf(usuario.getTypeDocuments()));
				digitalTO.setNumberTest("1");
				digitalTO.setIdPerson(String.valueOf(usuario.getIdPerson()));
				digitalTO.setIdStatusDigital(Constants.STATUS_DIGITAL_NUEVO);
				digitalTO.setOwnerTypeDoc(String.valueOf(usuario.getOwnerTypeDoc()));
				digitalTO.setIdNode(String.valueOf(usuario.getIdNodeDigital()));
				digitalTO.setTypesetter(String.valueOf(usuario.getTypesetter()!=0?usuario.getTypesetter():usuario.getIdPerson()));
				digitalTO.setChecker(String.valueOf(usuario.getChecker()!=0?usuario.getChecker():usuario.getIdPerson()));
				digitalTO.setLote(usuario.getLote());
				
				if(isScan) {
					if(usuario.getConsecutivo()!=null && usuario.getConsecutivo().size()>0) {
						String codigo = String.valueOf(usuario.getConsecutivo().get(0));
						digitalTO.setCodigo(codigo);
						usuario.getConsecutivo().remove(0);
					} else {
						try{
							digitalTO.setCodigo(usuario.getCorrelativo());
						}catch(Exception e) {
							digitalTO.setCodigo("");
						}
					}
					// consultamos si el correlativo existe para buscar el ultimo disponible
					digitalTO.setCodigo(digitalDAO.findNextNumber(digitalTO.getCodigo()));
				} else {
					digitalTO.setCodigo("");
				}
				
				if(digitalTO.getNameDocument()==null || digitalTO.getNameDocument().trim().equals("")) {
					digitalTO.setNameDocument(digitalTO.getCodigo());
				}
				
				digitalTO.setVisible("1");
				digitalDAO.save(digitalTO);

				// actualizamos datos del usuario
				try {
					if(digitalTO.getCodigo()!=null && !digitalTO.getCodigo().trim().equals("")) {
						usuario.setCorrelativo(digitalTO.getCodigo());
						int largo = usuario.getCorrelativo().length();
						usuario.setCorrelativo(ToolsHTML.zero(Integer.parseInt(usuario.getCorrelativo())+1,largo));
						HandlerDBUser.updateUserDigitalParameter(usuario);
					}
				} catch(Exception e) {
					e.printStackTrace();
				}
				
				// movemos el archivo a la carpeta temporal de digitalizados
				File fileNumber = new File(ToolsHTML.getPathDigitalizados().concat("dig_".concat(zero(digitalTO.getIdDigital(),15)).concat(extension)));
				Archivo.renameTo(destFile,fileNumber);
				
				// generamos el pdf del archivo
				ToolsHTML.getFileBasePathDigitalizados(req, digitalTO.getIdDigital());
				
				log.info("Creado DTO y almacenado en base de datos");
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.error("Error fue: " + e.getMessage(), e);
		}
	}
	
	public static String zero(int cad, int largo) {
		return zero(String.valueOf(cad),largo);
	}
	
	public static String zero(String cad, int largo) {
		if(cad.length()<largo) {
			cad="0".concat(cad);
			cad=zero(cad,largo);
		}
		return cad;
	}

	static String getTempFile(String clientID) {
		StringBuffer name = new StringBuffer(ToolsHTML.getPathTmp());
		name.append(clientID);
		name.append(".tmp");
		//name.append("file_scanner_").append(usuario.getIdPerson()).append(".pdf");
		return name.toString();
	}
}
