package com.desige.webDocuments.files.actions;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.desige.webDocuments.bean.ExpedienteRequest;
import com.desige.webDocuments.dao.ExpedienteDetalleDAO;
import com.desige.webDocuments.dao.ExpedienteVersionDAO;
import com.desige.webDocuments.document.forms.BaseDocumentForm;
import com.desige.webDocuments.files.facade.FilesFacade;
import com.desige.webDocuments.files.forms.ExpedienteForm;
import com.desige.webDocuments.persistent.managers.HandlerDocuments;
import com.desige.webDocuments.persistent.managers.HandlerStruct;
import com.desige.webDocuments.persistent.utils.JDBCUtil;
import com.desige.webDocuments.utils.ApplicationExceptionChecked;
import com.desige.webDocuments.utils.DesigeConf;
import com.desige.webDocuments.utils.FilesDocumentNotValidException;
import com.desige.webDocuments.utils.ToolsHTML;
import com.desige.webDocuments.utils.beans.SuperAction;
import com.desige.webDocuments.utils.beans.Users;
import com.focus.util.Archivo;

import sun.jdbc.rowset.CachedRowSet;

/**
 * Created by IntelliJ IDEA. User: srodriguez Date: 03/04/2006 Time: 11:29:28 AM
 * To change this template use File | Settings | File Templates.
 */

public class CrearExpedienteZipAction extends SuperAction {
	// private static ArrayList elementos =
	// ToolsHTML.getProperties("visor.OpenBrowser");
	// private POIFSFileSystem fs;
	public String error;
	private static Logger log = LoggerFactory.getLogger(CrearExpedienteZipAction.class.getName());
	private ResourceBundle rb = null;
	private String dateSystem = ToolsHTML.sdf.format(new Date());
	private Users usuario = null;

	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
		super.init(mapping, form, request, response);

		try {
			usuario = getUserSession();

			Locale defaultLocale = new Locale(DesigeConf.getProperty("language.Default"), DesigeConf.getProperty("country.Default"));
			rb = ResourceBundle.getBundle("LoginBundle", defaultLocale);

			int modeloReporte = 2;

			generarZip(request, response);
			
			return null; //goSucces();
		} catch (Exception e) {
			log.error(e.getMessage());
			e.printStackTrace();
		} finally {
		}
		return goError();
	}

	public void generarZip(HttpServletRequest request, HttpServletResponse response) throws Exception {
		// ubicamos la carpeta temporal
		String path = ToolsHTML.getPath().concat("tmp");
		String deleteFolder = "";

		// creamos la carpeta donde descargaremos el expediente
		ExpedienteForm files = new ExpedienteForm();
		if (!ToolsHTML.isEmptyOrNull(request.getParameter("f1"))) {
			files.setF1(Integer.parseInt(request.getParameter("f1")));
		}
		if (files.getF1() == 0) {
			throw new ApplicationExceptionChecked("El codigo del expediente a exportar es cero");
		}
		String expediente = "expediente-".concat(String.valueOf(files.getF1()));
		String folderExpediente = path.concat(File.separator).concat(expediente);
		File folder = new File(folderExpediente);
		if (!folder.exists()) {
			folder.mkdir();
		}

		String pathFolder = path.concat(File.separator).concat(expediente);
		String pathFolderDoc = null;

		// descargamos uno a uno los archivos pertenecientes al expediente
		// pasandolos a pdf
		ExpedienteDetalleDAO oExpedienteDetalleDAO = new ExpedienteDetalleDAO();

		CachedRowSet crs = oExpedienteDetalleDAO.findAll(files);		
		String fileInicio = "";
		ArrayList archivos = new ArrayList();
		BaseDocumentForm forma = null;
		List<ExpedienteRequest> listDoc = null;
		ExpedienteVersionDAO oExpedienteVersionDAO = new ExpedienteVersionDAO();		
		
		listDoc = oExpedienteVersionDAO.findByVersionExpedientes(files);
		
		boolean incluirDocumento = false;
		while (crs.next()) {
			forma = new BaseDocumentForm();
			forma.setIdDocument(crs.getString("numgen"));
			forma.setNumberGen(crs.getString("numgen"));
			forma.setNumVer(0); // crs.getInt("numver"));
			// forma.setFileInicio("p_2");
			
			incluirDocumento = false;
			for(ExpedienteRequest doc: listDoc) {
				if(doc.getNumgen().equals(crs.getString("numgen"))) {
					try {
						if(doc.getStatuVer().equals(HandlerDocuments.docBorrador) || !doc.getStatuVer().equals(HandlerDocuments.docApproved) || !doc.getStatuVer().equals(doc.getStatu()) ) {
							throw new FilesDocumentNotValidException("El documento no esta aprobado para ser guardado en una version");
						}
						if(!doc.isActive()) {
							throw new FilesDocumentNotValidException("El documento no existe en el sistema");
						}
						if(!doc.isPdfGenerated()) {
							throw new FilesDocumentNotValidException("El documento no tiene pdf generado");
						}
						if(doc.getToForFiles()==null || doc.getToForFiles().equals("true") || doc.getToForFiles().equals("1") ) {
							throw new FilesDocumentNotValidException("El documento no esta marcado como expediente");
						}
						incluirDocumento = true;
					} catch(FilesDocumentNotValidException e) {
						incluirDocumento = false;
					}
					break;
				}
            }	

			if (!incluirDocumento) {
				throw new ApplicationExceptionChecked("E0053");
			}
			
			Hashtable tree = (Hashtable) request.getSession().getAttribute("tree");
			Users usuario = (Users) request.getSession().getAttribute("user");
			if (usuario != null) {
				tree = ToolsHTML.checkTree(tree, usuario);
			}

			// Se carga la información del documento en el bean forma,
			// sacandola de la base de datos.
			HandlerStruct.loadDocument(forma, true, false, tree, request);

			if (forma.getNumVer()<=0) {
				throw new ApplicationExceptionChecked("E0053");
			}
			
			// request.getSession().setAttribute("showDocumentI", forma);
			forma.setPrinting(files.isPrinting());

			// verificamos si se puede imprimir el documento
			// HandlerDocuments.isPrinting(usuario, forma);

			// forma.setFileInicio(fileInicio); // archivo que le antecede
			forma.setFileInicio("");
			//ydavila Cielemca Colocar como nombre de carpeta la ruta completa del EOC y no sólo la última carpeta
			
			boolean isMultipleCarpeta = true; // y nombres de documento
			if(isMultipleCarpeta) {
				// ruta completa
				pathFolderDoc = pathFolder.concat(File.separator).concat(HandlerStruct.getRout("", forma.getIdNode(),forma.getIdNode(), true));
				folder = new File(pathFolderDoc);
				folder.mkdirs();
				
				String[] ext = forma.getNameFile().split("\\.");
				forma.setNameFile(forma.getNameDocument().concat(".").concat(ext[ext.length-1]));

				if(forma.getNameFileParalelo()!=null && !forma.getNameFileParalelo().trim().equals("")) {
					ext = forma.getNameFileParalelo().split("\\.");
					forma.setNameFileParalelo(forma.getNameDocument().concat(".").concat(ext[ext.length-1]));
				}
			} else {
				// una sola carpeta
				pathFolderDoc = pathFolder.concat(File.separator).concat(crs.getString("folder"));
				folder = new File(pathFolderDoc);
				folder.mkdir();
			}
			
			//pathFolderDoc = pathFolder.concat(File.separator).concat(HandlerStruct.loadRutaEOC(forma, tree));
			//Connection con;
			//String rutaPdf = PdfConvert.saveToPdf(con, pathFolderDoc, (forma.getIdDocument()), false);
			

			String nameFilePdf = HandlerStruct.createFilePDFForFiles(forma, pathFolderDoc);
			//ydavila Cielemca Colocar nombre de documento en vez de nombre de archivo
			if(nameFilePdf==null) {
				throw new Exception("No existe el archivo pdf");
			}
			archivos.add(nameFilePdf);

		}

		// ahora comprimimos los archivos
		String filename = path.concat(File.separator).concat(expediente.concat(".zip")); 
		File fileZip = new File(filename);
		try {
			int BUFFER_SIZE = 2048;
			// Reference to the file we will be adding to the zipfile
			BufferedInputStream origin = null;
			// Reference to our zip file
			FileOutputStream dest = new FileOutputStream(filename);
			// Wrap our destination zipfile with a ZipOutputStream
			ZipOutputStream out = new ZipOutputStream(

			new BufferedOutputStream(dest));
			// Create a byte[] buffer that we will read data

			// from the source
			// archivos into and then transfer it to the zip file
			byte[] data = new byte[BUFFER_SIZE];
			// Iterate over all of the archivos in our list
			for (Iterator i = archivos.iterator(); i.hasNext();) {
				// Get a BufferedInputStream that we can use to read the

				// source file
				filename = (String) i.next();
				//System.out.println("Adding: " + filename);
				FileInputStream fi = new FileInputStream(filename);
				origin = new BufferedInputStream(fi, BUFFER_SIZE);
				// Setup the entry in the zip file
				//System.out.println(filename.substring(path.length()+1));
				ZipEntry entry = new ZipEntry(filename.substring(path.length()+1));
				out.putNextEntry(entry);
				// Read data from the source file and write it out to the
				// zip file
				int count;
				while ((count = origin.read(data, 0, BUFFER_SIZE)) != -1) {
					out.write(data, 0, count);
				}
				// Close the source file
				origin.close();
			}
			// Close the zip file
			out.close();

			// Enviando el Archivo al Cliente
			response.reset();
			response.setContentType("application/zip");
			response.setHeader("Content-disposition", "attachment; filename="+expediente+".zip");
			response.setHeader("content-transfer-encoding", "binary");
			OutputStream salida = response.getOutputStream();
			
			Archivo archivo = new Archivo();
			filename = path.concat(File.separator).concat(expediente.concat(".zip")); 
			if(archivo.leerBinary(filename, response)){
				//System.out.println("Se ha enviado el archivo");
			} else {
				//System.out.println("Fallo la lectura del archivo zip");
			}
			salida.flush();
			salida.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			//guardamos la info en el historico del expediente
			FilesFacade.updateHistoryFiles(JDBCUtil.getConnection(Thread.currentThread().getStackTrace()[1].getMethodName()),
					files.getF1(), 
					usuario.getUser(), null, "21", "", new String[]{"0","0"});
		}
		
		// eliminamos los archivos del temporal
		if (fileZip.exists()) {
			Archivo.deleteFile(fileZip);
		}
		folder = new File(folderExpediente);
		if (folder.exists()) {
			Archivo.deleteFile(folder);
		}		
	}
}
