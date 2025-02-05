package com.desige.webDocuments.document.actions;

import java.io.File;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.fileupload.DiskFileUpload;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadBase.InvalidContentTypeException;

import com.desige.webDocuments.utils.DesigeConf;
import com.desige.webDocuments.utils.StringUtil;
import com.desige.webDocuments.utils.ToolsHTML;


/*
 * Clase para subir el manejo de algunas funciones de archivo
 */
public class FileUtil {

	public FileUtil() {

	}

	/*
	 * Permite subir los ficheros que estan en el request a la carpeta temporal
	 */
	public Properties procesaFicheros(HttpServletRequest req) {
		return procesaFicheros(req, null);
	}

	/*
	 * Permite subir los ficheros que estan en el request a la carpeta temporal
	 */
	public Properties procesaFicheros(HttpServletRequest req, String newNameFile) {
		Properties campos = new Properties();
		try {
			DiskFileUpload fu = new DiskFileUpload();
			//System.out.println("Procesando requerimiento.....");
			int lim = Integer.parseInt(DesigeConf.getProperty("application.limFiles"));
			fu.setSizeMax(1024 * 1024 * lim);
			fu.setSizeThreshold(4096);
			String path = ToolsHTML.getPathTmp();
			fu.setRepositoryPath(path);
			List fileItems = fu.parseRequest(req);
			//System.out.println("fileItems = " + fileItems);
			if (fileItems == null) {
				//System.out.println("La lista es nula");
				return campos;
			}
			Iterator i = fileItems.iterator();
			FileItem actual = null;
			
			while (i.hasNext()) {
				actual = (FileItem) i.next();
				String name = actual.getFieldName();
				String fileName = actual.getName();
				if (fileName != null && !fileName.equals("")) {
					File fichero = new File(fileName);
					campos.put(name, newNameFile!=null ? newNameFile : fichero.getName() );
					campos.put(name.concat("-").concat("ContextType"),actual.getContentType());
					// fichero = new File("C:\\Program Files\\up\\" + fichero.getName());
					//fichero = new File(path.concat("/").concat(newNameFile!=null ? newNameFile : fichero.getName()));
					fichero = new File(path.concat(StringUtil.getOnlyNameFile(newNameFile!=null ? newNameFile : fichero.getName())));
					if(fichero.exists()) {
						System.gc();
						fichero.delete();
					}
					actual.write(fichero);
				} else {
					if(name.equals("fileName")) {
						newNameFile = actual.getString();
					}
					campos.put(name, actual.getString());
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return campos;
	}
	
	/*
	 * Permite subir los ficheros que estan en el request a la carpeta temporal
	 */
	public Properties leerParametros(HttpServletRequest req) {
		Properties campos = new Properties();
		try {
			DiskFileUpload fu = new DiskFileUpload();
			List fileItems = fu.parseRequest(req);
			if (fileItems == null) {
				//System.out.println("La lista es nula");
				return campos;
			}
			Iterator i = fileItems.iterator();
			FileItem actual = null;
			
			while (i.hasNext()) {
				actual = (FileItem) i.next();
				String name = actual.getFieldName();
				String fileName = actual.getName();
				campos.put(name, actual.getString());
			}
		} catch (InvalidContentTypeException e) {
			//System.out.println("La lista es nula");
			return campos;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return campos;
	}

}
