package com.desige.webDocuments.document.servlet;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.desige.webDocuments.document.forms.BaseDocumentForm;
import com.desige.webDocuments.persistent.managers.HandlerDBUser;
import com.desige.webDocuments.persistent.managers.HandlerDocuments;
import com.desige.webDocuments.persistent.managers.HandlerParameters;
import com.desige.webDocuments.persistent.managers.HandlerStruct;
import com.desige.webDocuments.utils.ApplicationExceptionChecked;
import com.desige.webDocuments.utils.Constants;
import com.desige.webDocuments.utils.ToolsHTML;
import com.desige.webDocuments.utils.beans.Users;

/**
 * Title: ServletShowDoc.java <br/>
 * Copyright: (c) 2004 Focus Consulting C.A.<br/>
 * 
 * @author Ing. Nelson Crespo
 * @version WebDocuments v3.0 <br/>
 *          Changes:<br/>
 *          <ul>
 *          <li>24/01/2006 (NC) Creation</li>
 *          <li>30/06/2006 (NC) Se agreg� el log y se formatearon valores de Cabecera para la Visualizaci�n de los documentos</li>
 *          <li>13/07/2006 (NC) Cambios para heredar o no los Prefijos de las Carpetas</li>
 *          </ul>
 * 
 *          Muestra el documento
 */
public class ServletShowDoc extends HttpServlet {
	/**
	 * 
	 */
	private static final long serialVersionUID = -708980992493775918L;
	static Logger log = LoggerFactory.getLogger("[V3.0] " + ServletShowDoc.class.getName());
	private static ArrayList elementos = ToolsHTML.getProperties("visor.OpenBrowser");

	public void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// response.resetBuffer();
		log.info("Iniciando service del servlet");
		log.debug("_________________________________________________________");
		log.debug("Mostrando Documento " + request.getParameter("idDocument"));
		log.debug("Versi�n " + request.getParameter("idDocument"));
		log.debug("_________________________________________________________");
		BaseDocumentForm forma = new BaseDocumentForm();
		forma.setIdDocument(request.getParameter("idDocument"));
		forma.setNumberGen(request.getParameter("idDocument"));
		if ((request.getParameter("idVersion") != null) && (!"null".equalsIgnoreCase(request.getParameter("idVersion")))) {
			forma.setNumVer(Integer.parseInt(request.getParameter("idVersion")));
		} else {
			forma.setNumVer(Integer.parseInt("0"));
		}

		try {
			if (forma.getNumVer() != 0) {
				Hashtable tree = (Hashtable) request.getSession().getAttribute("tree");
				Users usuario = (Users) request.getSession().getAttribute("user");
				if (usuario != null) {
					tree = ToolsHTML.checkTree(tree, usuario);
					forma.setIdUser((int) usuario.getIdPerson()); // asignamos el usuario en session
				} else if (request.getParameter("idPerson") != null) {
					usuario = HandlerDBUser.load(Integer.parseInt(request.getParameter("idPerson")), true);
					forma.setIdUser((int) usuario.getIdPerson());

					System.out.println("marca=" + request.getParameter("marca"));
					if (!request.getParameter("marca").equals(usuario.getJavaWebStart())) {
						if (!request.getParameter("marca").equals("F0cusvision91")) {
							throw new ApplicationExceptionChecked("E0009");
						}
					}

					// eliminamos la marca de tiempo
					HandlerDBUser.updateTimeMarkForJavaWebStart(usuario.getIdPerson(), "");
				}

				// Se carga la informaci�n del documento en el bean forma,
				// sacandola de la base de datos.
				HandlerStruct.loadDocument(forma, request.getParameter("downFile") != null, false, tree, request);
				request.getSession().setAttribute("showDocumentI", forma);
				if (request.getParameter("controlada") != null) {
					forma.setControlada(Boolean.parseBoolean(request.getParameter("controlada")));
				}
				if (request.getParameter("numberCopies") != null) {
					forma.setNumberCopies(Integer.parseInt(request.getParameter("numberCopies")));
				}
				if (request.getParameter("copyContents") != null) {
					forma.setCopyContents(Boolean.parseBoolean(request.getParameter("copyContents")));
				}

				String tipoFile ="nativo";
				String path = ToolsHTML.getPath().concat("tmp") + File.separator + usuario.getUser() + File.separator + tipoFile; // \\tmp
				new File(path).mkdirs();

				// ToolsHTML.getPath().concat("tmp"); //
				// \\tmp

				// verificamos si se puede imprimir el documento
				if (usuario != null) {
					HandlerDocuments.isPrinting(usuario, forma);
					forma.setIdUser((int) usuario.getIdPerson()); // asignamos el usuario en session
				}

				// aqui condicionamos si es una imagen
				String nameFile = forma.getNameFile().toLowerCase();
				// pedimos las extensiones que seran visualizadas en el navegador
				boolean isVisorNativo = false;
				try {
					isVisorNativo = HandlerParameters.isExtensionNativeViewer(null, forma.getNameFile());
				} catch (Exception e) {
					System.out.println("ERROR: NO SE PUDO CONSULTAR SI ES UNA EXTENSION NATIVA");
					e.printStackTrace();
				}

				if (isVisorNativo) {
					// si es una imagen cambiamos el nombre del archivo
					String ext = forma.getNameFile().substring(forma.getNameFile().indexOf("."));
					StringBuffer name = new StringBuffer();
					// name.append("job").append(usuario.getIdPerson()).append("_").append(forma.getIdDocument()).append("_").append(forma.getNumVer()).append(ext);
					name.append("trabajo").append(usuario.getIdPerson()).append("_").append(forma.getIdDocument()).append("_").append(forma.getNumVer()).append(ext);
					forma.setNameFile(name.toString());
					HandlerStruct.createFile(forma, path);
				} else {
					HandlerStruct.createFile(forma, path);
				}
				if (forma.getNameFile() != null) {
					// PDF: cambios para pdf
					if (Constants.PRINTER_PDF) {
						if (!isVisorNativo) {
							if (forma.getNameFile().endsWith(".pdf") && forma.getContextType().equals("application/pdf")) {
								forma.setNameFile(forma.getNameFile().substring(0, forma.getNameFile().indexOf(".")).concat(".pdf"));
							}
						}
					}
					// PDF: fin
					if (!isVisorNativo) {
						if (processFile(response, request, path, forma, usuario)) {
							log.debug("Mostrando Archivo " + forma.getNameDocument());
						}
					} else {
						// redireccionamos a la pagina que contiene el applet para ver imagenes gif y jpg
						// request.setAttribute("nombreArchivo", ToolsHTML.getPathTmpURL(request).concat(forma.getNameFile()));
						// request.getRequestDispatcher("visor.jsp").forward(request, response);
						// Colocamos la misma rutina para la imagen
						if (processFile(response, request, path, forma, usuario)) {
							log.debug("Mostrando Archivo " + forma.getNameDocument());
						}
					}
					// PDF: cambios para pdf
					if (Constants.PRINTER_PDF) {
						// Cancelamos la orden de impresion if estamos
						// ejecutando en modo pdf
						// loadsolicitudImpresion.updatepermisoImpresion(forma.getIdDocument(), String.valueOf(usuario.getIdPerson()));
					}
					// PDF: fin
				}

			} else {
				throw new ApplicationExceptionChecked("E0053");
			}
		} catch (ApplicationExceptionChecked ae) {
			ae.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static byte[] readBytes(InputStream in) throws IOException {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		int bytesRead = 0;
		byte b[] = new byte[32768];
		while (bytesRead >= 0) {
			bytesRead = in.read(b); // each read will read a chunk of some unknown size until it is -1 bytes
			if (bytesRead >= 0)
				out.write(b, 0, bytesRead);
		}
		return out.toByteArray(); // returns an array of all the bytes read concatenated together.
	}

	public static synchronized boolean processFile(HttpServletResponse response, HttpServletRequest request, String path, BaseDocumentForm forma,
			Users usuario) {
		// Extensi�n del archivo
		String extension = getExtensionFile(forma.getNameFile());

		// El tipo de documento a mostrar (El MimeTipe)
		// response.setContentType(forma.getContextType());
		// response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
		// System.out.println(request.getSession().getServletContext().getMimeType(forma.getNameFile()));
		response.setContentType(request.getSession().getServletContext().getMimeType(forma.getNameFile()));

		InputStream in = null;
		OutputStream out = null;

		String initServer = ToolsHTML.getServerPath(ToolsHTML.getServerName(request), request.getServerPort(), request.getContextPath());
		// String fileURL = initServer + "/tmp/" + usuario.getUser() + "/" + forma.getNameFile();
		// String tipoFile ="video";
		String tipoFile ="nativo";
		String fileURL = initServer + "/tmp/" + usuario.getUser() + "/"+tipoFile+"/" + forma.getNameFile();

		boolean isVisioDoc = elementos.contains(extension);
		// --response.resetBuffer();
		try {

			boolean onlyRead = request.getParameter("downFile") == null;// request.getParameter("downFile")==null?true:false;
			boolean sendURL = request.getParameter("sendURL") != null;
			log.debug("onlyRead = " + onlyRead);
			log.debug("isVisioDoc = " + isVisioDoc);
			log.debug("extension = " + extension);

			sendURL = HandlerParameters.isExtensionNativeViewer(null, forma.getNameFile());
			
			if (onlyRead) {
				response.setHeader("Content-disposition", "inline; Filename=\"" + forma.getNameFile() + "\" ");
			} else {
				response.setHeader("Content-disposition", "attachment; filename=\"" + forma.getNameFile() + "\"");
			}
			
			
			response.setContentType( "text/html" );
			//response.setContentType("video/mp4");

			response.setHeader("Pragma", "no-cache");
			response.addHeader("Cache-Control", "must-revalidate");

			response.setDateHeader("Expires", 0);
			log.debug("forma.getContextType() = " + forma.getContextType());
			log.debug("sendURL = " + sendURL);
			response.setHeader("content-transfer-encoding", "binary");
			if (!sendURL && ((!isVisioDoc) || (!onlyRead))) {
				File fichero = new File(path + File.separator + getOnlyNameFile(forma.getNameFile()));
				// response.setHeader("Content-Type", forma.getContextType());
				response.setHeader("Content-Type", request.getSession().getServletContext().getMimeType(forma.getNameFile()));
				log.debug("bytes del Archivo = " + fichero.length());
				for (int i = 0; i < 10; i++) {
					fichero.exists();
				}
				in = new FileInputStream(fichero);
				System.out.println(in.available());
				//response.setHeader("Content-Length", "" + in.available());
				response.addHeader( "Content-Length", Long.toString( fichero.length() ) );
				out = response.getOutputStream();/*
				int bytesRead = 0;
				byte buffer[] = new byte[8192];
				while ((bytesRead = in.read(buffer, 0, 8192)) != -1) {
					out.write(buffer, 0, bytesRead);
				}
				
				response.flushBuffer();
				*/
				byte[] salida = readBytes(in);
				out.write(salida);
				closeIOs(in, out);
				try {
					System.gc();
					fichero.delete();
				} catch (Exception ex) {
					ex.printStackTrace();
				}
				return true;
			} else {

				log.debug("send Redirect: " + fileURL);
				response.setHeader("Cache-control", "no-cache");
				response.setHeader("Pragma", "no-cache");
				response.setDateHeader("Expires", 0);
				// System.out.println("fileURL=" + fileURL);
				String url = fileURL.toString();
				if ((".x-cdr".equalsIgnoreCase(extension)) || (".cdr".equalsIgnoreCase(extension))) { // || (".ai".equalsIgnoreCase(extension))) {
					response.sendRedirect(initServer.toString() + "/showcdr.jsp?url=" + url.toString() + "&name=" + forma.getNameFile());
				} else {
					response.sendRedirect(url.toString());
				}

				return true;
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			closeIOs(in, out);
		}
		return false;
	}

	private static String getExtensionFile(String nameFile) {
		int pos = nameFile.indexOf(".");
		String extension = nameFile.substring(pos).toUpperCase();
		return extension;
	}

	private static void closeIOs(InputStream in, OutputStream out) {
		if (in != null) {
			try {
				in.close();
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
		if (out != null) {
			try {
				out.close();
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}

	public static String getOnlyNameFile(String nameFile) {
		if (nameFile == null || nameFile.trim().equals(""))
			return nameFile;

		char back = (char) 92;
		char slash = '/';

		StringBuffer name = new StringBuffer(nameFile);
		nameFile = name.reverse().toString();

		if (nameFile.indexOf(String.valueOf(back)) != -1) {
			nameFile = nameFile.substring(0, nameFile.indexOf(String.valueOf(back)));
		}
		if (nameFile.indexOf(String.valueOf(slash)) != -1) {
			nameFile = nameFile.substring(0, nameFile.indexOf(String.valueOf(slash)));
		}

		name.setLength(0);
		name.append(nameFile);
		return name.reverse().toString();

	}

}
