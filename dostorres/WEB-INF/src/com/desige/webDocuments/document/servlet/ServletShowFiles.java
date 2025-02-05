package com.desige.webDocuments.document.servlet;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.ResourceBundle;
import java.util.TreeMap;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sun.jdbc.rowset.CachedRowSet;

import com.desige.webDocuments.dao.ExpedienteDetalleDAO;
import com.desige.webDocuments.dao.ExpedienteVersionDAO;
import com.desige.webDocuments.document.actions.loadsolicitudImpresion;
import com.desige.webDocuments.document.forms.BaseDocumentForm;
import com.desige.webDocuments.document.forms.frmsolicitudImpresion;
import com.desige.webDocuments.files.forms.ExpedienteForm;
import com.desige.webDocuments.persistent.managers.HandlerDBUser;
import com.desige.webDocuments.persistent.managers.HandlerStruct;
import com.desige.webDocuments.utils.ApplicationExceptionChecked;
import com.desige.webDocuments.utils.Constants;
import com.desige.webDocuments.utils.ToolsHTML;
import com.desige.webDocuments.utils.beans.SuperActionForm;
import com.desige.webDocuments.utils.beans.Users;

/**
 * Title: ServletShowDoc.java <br/> Copyright: (c) 2004 Focus Consulting C.A.<br/>
 * 
 * @author Ing. Nelson Crespo
 * @version WebDocuments v3.0 <br/> Changes:<br/>
 *          <ul>
 *          <li> 24/01/2006 (NC) Creation </li>
 *          <li> 30/06/2006 (NC) Se agregó el log y se formatearon valores de
 *          Cabecera para la Visualización de los documentos </li>
 *          <li> 13/07/2006 (NC) Cambios para heredar o no los Prefijos de las
 *          Carpetas </li>
 *          </ul>
 * 
 * Muestra el documento
 */
public class ServletShowFiles extends HttpServlet {
	/**
	 * 
	 */
	private static final long serialVersionUID = -484038077611766345L;
	static Logger log = LoggerFactory.getLogger("[V3.0] " + ServletShowDoc.class.getName());
	private static ArrayList elementos = ToolsHTML.getProperties("visor.OpenBrowser");

	public void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		log.info("Iniciando service del servlet");
		response.resetBuffer();

		BaseDocumentForm forma = null;
		ExpedienteDetalleDAO oExpedienteDetalleDAO = new ExpedienteDetalleDAO();
		ExpedienteVersionDAO oExpedienteVersionDAO = new ExpedienteVersionDAO();
		CachedRowSet crs = null;
		boolean isVersion = false;
		try {
			// vamos a buscar los documentos del expediente y los
			// convertimos en archivos fisicos en pdf
			ExpedienteForm files = (ExpedienteForm) request.getSession().getAttribute("expediente"); //new ExpedienteForm();
			//files.setF1(Integer.parseInt(ToolsHTML.isEmptyOrNull(request.getParameter("idDocument"), "0")));
			if (files.getF1() == 0) {
				throw new ApplicationExceptionChecked("E0120");
			}
			files.setNumVer(Integer.parseInt(request.getParameter("idVersion")));

			if(files.getNumVer()!=0) {
				isVersion=true;
				crs = oExpedienteVersionDAO.findAll(files);
			} else {
				isVersion=false;
				crs = oExpedienteDetalleDAO.findAll(files);
			}

			String path = ToolsHTML.getPath().concat("tmp");

			String fileInicio="";
			if(files.isPrinting()) {
				fileInicio="files_".concat(String.valueOf(files.getF1())).concat(".pdf");
			}

			Hashtable tree = (Hashtable) request.getSession().getAttribute("tree");
			Users usuario = (Users) request.getSession().getAttribute("user");
			if (usuario != null) {
				tree = ToolsHTML.checkTree(tree, usuario);
			}
			ArrayList<BaseDocumentForm> documentos = new ArrayList<BaseDocumentForm>();
			while (crs.next()) {
				if(crs.getString("toForFiles")!=null && (crs.getString("toForFiles").equals("0") || crs.getString("toForFiles").equals("false"))) {
					forma = new BaseDocumentForm();
					forma.setIdDocument(crs.getString("numgen"));
					forma.setNumberGen(crs.getString("numgen"));
					forma.setNumVer(isVersion?crs.getInt("numver"):0);
					forma.setIdFiles(files.getF1());
					forma.setNumVerFiles(files.getNumVer());
					forma.setExpediente(true);
					forma.setToForFiles(crs.getString("toForFiles"));
					// Se carga la información del documento en el bean forma,
					// sacandola de la base de datos.
					HandlerStruct.loadDocument(forma, !isVersion, false, tree, request);
				
					//request.getSession().setAttribute("showDocumentI", forma);
					forma.setPrinting(files.isPrinting());

					// verificamos si se puede imprimir el documento
					//HandlerDocuments.isPrinting(usuario, forma);

					forma.setFileInicio(fileInicio); // archivo que le antecede
					
					HandlerStruct.createFilePDF(forma, path);

					fileInicio=forma.getNameFile();
					
					documentos.add(forma);
				}
			}

			// AQUI se procede a mostrar el documento
			if (forma!=null && forma.getNameFile() != null) {
				forma.setContextType("application/pdf");
				forma.setNameFile(forma.getNameFile().substring(0, forma.getNameFile().indexOf(".")).concat(".pdf"));
				
				//if(files.getNumVer()==0) {
				//	PdfSecurity.setBackgroundImage(path.concat(File.separator).concat(getOnlyNameFile(forma.getNameFile())), PdfSecurity.BG_ERASER);
				//}
				if (processFile(response, request, path, forma)) {
					log.debug("Mostrando Archivo " + forma.getNameDocument());
				}
				if (Constants.PRINTER_PDF) {
					// Cancelamos la orden de impresion if estamos ejecutando en
					// modo pdf
					//loadsolicitudImpresion.updatepermisoImpresion(forma.getIdDocument(), String.valueOf(usuario.getIdPerson()));
				}
				
				if(files.isPrinting()) {
					loadsolicitudImpresion solicitud = new loadsolicitudImpresion();
					frmsolicitudImpresion s = null;
					BaseDocumentForm f = null;
					Users u = null;
					TreeMap indice = HandlerDBUser.getAllUsersMap();
					
					for(int i=0; i<documentos.size(); i++) {
						f = documentos.get(i);
						u = (Users)indice.get(f.getOwner()); 
						
						request.setAttribute("cmd",SuperActionForm.cmdLoad);
						request.setAttribute("mensaje","Impreso desde el modulo expedientes");
						request.setAttribute("controlada","no");
						request.setAttribute("namePropietario",String.valueOf(u.getIdPerson()));
						request.setAttribute("idDocument",f.getIdDocument());
						request.setAttribute("idVersion",String.valueOf(f.getNumVer()));
						request.setAttribute("solicitante",String.valueOf(usuario.getIdPerson()));
	
						request.setAttribute("docRelations","");
						request.setAttribute("copiasRelations","");
						
						
						solicitud.cargarSolicitud(request, this.getServletContext(),true);
						
						
						request.setAttribute("cmd",SuperActionForm.cmdUpdatePrint);
						request.setAttribute("idDocumentptr",f.getIdDocument());
						request.setAttribute("idUserptr",String.valueOf(usuario.getIdPerson()));
						request.setAttribute("printFree",String.valueOf("false"));
						
						// aprobamos el flujo de trabajo generado automaticamente
						loadsolicitudImpresion.responseWFPrint(solicitud.getIdWorkFlow());
						
						
						// habilitamos el permiso de impresion
						loadsolicitudImpresion.enabledPermisoImpresion(f.getIdDocument(),String.valueOf(usuario.getIdPerson()));
						
						// marcamos como impresa la solicitud
						solicitud.cargarSolicitud(request, this.getServletContext(),true);						
					}
				}
				/*
				// creamos las solicitudes de impresion para los documentos
				if(files.isPrinting()) {
					frmsolicitudImpresion s = null;
					BaseDocumentForm f = null;
					Users u = null;
					TreeMap indice = HandlerDBUser.getAllUsersMap();
					Connection con = null;
					try{
						con = JDBCUtil.getConnection();
						con.setAutoCommit(false);
						for(int i=0; i<documentos.size(); i++) {
							f = documentos.get(i);
							u = (Users)indice.get(f.getOwner()); 
							s = new frmsolicitudImpresion();
							s.setNumsolicitud(0); // pendiente
							s.setDatesolicitud(""); // pendiente
							s.setSolicitante((int)usuario.getIdPerson());
							s.setAutorizante((int)u.getIdPerson());
							s.setStatusautorizante(5);
							s.setStatusimpresion(2);
							s.setNumberGen(Integer.parseInt(f.getNumberGen()));
							s.setNumVer(f.getNumVer());
							s.setCopias("1");
							s.setDestinatarios(null);
							s.setComments("Impreso desde el modulo expedientes");
							loadsolicitudImpresion.insertfrmsolicitudImpresion(con, s);
						}
						con.commit();
					} catch(Exception e) {
						con.rollback();
						e.printStackTrace();
						throw new ApplicationExceptionChecked("E0100");
					} finally {
						if(con!=null) {
							try{
								con.close();
							}catch(Exception ex) {
								// no hacemos nada
							}
						}
					}
				}
				*/
			}
			
		} catch (ApplicationExceptionChecked ae) {
			ae.printStackTrace();
			ResourceBundle rb = ToolsHTML.getBundle(request);
            request.setAttribute("error",rb.getString(ae.getKeyError()));

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static synchronized boolean processFile(HttpServletResponse response, HttpServletRequest request, String path, BaseDocumentForm forma) {
		// Extensión del archivo
		String extension = getExtensionFile(forma.getNameFile());

		// El tipo de documento a mostrar (El MimeTipe)
		response.setContentType(forma.getContextType());

		InputStream in = null;
		OutputStream out = null;

		String initServer = ToolsHTML.getServerPath(ToolsHTML.getServerName(request), request.getServerPort(), request.getContextPath());
		String fileURL = initServer + "/tmp/" + forma.getNameFile();

		boolean isVisioDoc = elementos.contains(extension);
		response.resetBuffer();
		try {

			boolean onlyRead = request.getParameter("downFile") == null;// request.getParameter("downFile")==null?true:false;
			boolean sendURL = request.getParameter("sendURL") != null;
			log.debug("onlyRead = " + onlyRead);
			log.debug("isVisioDoc = " + isVisioDoc);
			log.debug("extension = " + extension);

			if (onlyRead) {
				response.setHeader("Content-disposition", "inline; filename=\"" + forma.getNameFile() + "\" ");
			} else {
				response.setHeader("Content-disposition", "attachment; filename=\"" + forma.getNameFile() + "\"");
			}

			response.setHeader("Pragma", "no-cache");
			response.addHeader("Cache-Control", "must-revalidate");

			response.setDateHeader("Expires", 0);
			log.debug("forma.getContextType() = " + forma.getContextType());
			log.debug("sendURL = " + sendURL);
			response.setHeader("content-transfer-encoding", "binary");
			if (!sendURL && ((!isVisioDoc) || (!onlyRead))) {
				File fichero = new File(path + File.separator + getOnlyNameFile(forma.getNameFile()));
				response.setHeader("Content-Type", forma.getContextType());
				log.debug("bytes del Archivo = " + fichero.length());
				for (int i = 0; i < 10; i++) {
					fichero.exists();
				}
				in = new FileInputStream(fichero);
				response.setHeader("Content-Length", "" + in.available());
				out = response.getOutputStream();
				int bytesRead = 0;
				byte buffer[] = new byte[8192];
				while ((bytesRead = in.read(buffer, 0, 8192)) != -1) {
					out.write(buffer, 0, bytesRead);
					response.flushBuffer();
				}
				closeIOs(in, out);
				try {
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
				//System.out.println("fileURL=" + fileURL);
				String url = fileURL.toString();
				if ((".x-cdr".equalsIgnoreCase(extension)) || (".cdr".equalsIgnoreCase(extension)) || (".ai".equalsIgnoreCase(extension))

				) {
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
