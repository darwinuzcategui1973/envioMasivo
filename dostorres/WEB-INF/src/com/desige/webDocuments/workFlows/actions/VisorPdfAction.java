package com.desige.webDocuments.workFlows.actions;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.desige.webDocuments.persistent.managers.HandlerBD;
import com.desige.webDocuments.persistent.managers.HandlerDBUser;
import com.desige.webDocuments.persistent.managers.HandlerDocuments;
import com.desige.webDocuments.persistent.managers.HandlerParameters;
import com.desige.webDocuments.persistent.managers.HandlerStruct;
import com.desige.webDocuments.utils.Constants;
import com.desige.webDocuments.utils.ToolsHTML;
import com.desige.webDocuments.utils.beans.Users;
import com.focus.util.Archivo;

public class VisorPdfAction extends Action {

	public synchronized ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {

		StringBuffer query = new StringBuffer();
		ArrayList<Object> parametros = new ArrayList<Object>();
		FileReader entrada = null;
		FileWriter salida = null;
		StringBuffer sb = new StringBuffer();
		boolean registro = false;
		String sufijo = "";
		StringBuffer fileName = new StringBuffer(); 
		StringBuffer fileNameNew = new StringBuffer(); 

		try {
			Users usuario = (Users) request.getSession().getAttribute("user");



			String path = ToolsHTML.getPath().concat(Constants.FOLDER_JNLP);
			
			sb.setLength(0);
			sb.append(path);
			sb.append(File.separator);
			sb.append(Constants.FILE_JNLP_VISOR);
			sb.append(sufijo);
			sb.append(".jnlp");
			
			fileName.setLength(0);
			fileName.append(Constants.FILE_JNLP_VISOR); 
			fileName.append(sufijo);
			fileName.append(".jnlp");
			
			fileNameNew.setLength(0);
			fileNameNew.append(Constants.FILE_JNLP_VISOR);
			fileNameNew.append(sufijo);
			fileNameNew.append(usuario.getIdPerson());
			fileNameNew.append(".jnlp");
			

			//System.out.println(sb.toString());
			File f = new File(sb.toString());
			//System.out.println("Existe " + f.exists());

			if (f.exists()) {
				File fileJnlUsuario = new File(path.concat(File.separator).concat(fileNameNew.toString()));
				if(fileJnlUsuario.exists()) {
					//System.out.println("Eliminamos "+fileJnlUsuario.getAbsolutePath());
					fileJnlUsuario.delete();
				}
				
				Date fecha = new Date();
				SimpleDateFormat sdf = new SimpleDateFormat("yyyymmdd");

				entrada = null;
				StringBuffer str = new StringBuffer();
				entrada = new FileReader(sb.toString());
				int c;
				while ((c = entrada.read()) != -1) {
					str.append((char) c);
				}
				String source = sdf.format(fecha);
				
				
				// buscamos la ruta completa del domimio para colocarla en el jnlp
				StringBuffer basePath = new StringBuffer();
				StringBuffer basePathEditor = new StringBuffer();
				StringBuffer basePathOnDemand = new StringBuffer();
				basePath.append(request.getScheme());
				basePath.append("://");
				if(request.getParameter("server")!=null) {
					basePath.append(request.getParameter("server"));
				} else {
					basePath.append(ToolsHTML.getServerName(request));
				}
				basePath.append(":");
				basePath.append(request.getServerPort());
				
				basePathEditor.append(basePath);
				basePathEditor.append(request.getContextPath());
				basePathEditor.append("/");
				basePathEditor.append(Constants.FOLDER_JNLP);
				
				basePathOnDemand.append(basePath);
				basePathOnDemand.append(request.getContextPath());
				//basePathOnDemand.append("OnDemand");
				basePathOnDemand.append("/");

				String marcaDeTiempo = String.valueOf(new Date().getTime());
				String url = new String(request.getParameter("urlDocumento")).concat("-AMPER-marca-IGUAL-").concat(marcaDeTiempo);
				
				
				Properties prop = getProperties(url);
				int idVersion = Integer.parseInt(prop.getProperty("idVersion"));
				int idDocument = Integer.parseInt(prop.getProperty("idDocument"));
				
				// buscamos el documento en base de datos
				String extension = ToolsHTML.getExtension(HandlerStruct.loadNameFileDocument(prop.getProperty("idDocument")));
				
				String nameFileCache = Archivo.getNameFileEncripted(null, "versiondocview", idVersion, null);
				File cache = new File(nameFileCache);
				boolean existeCache = cache.exists();
				
				if("1".equals( String.valueOf(HandlerParameters.PARAMETROS.getDisabledCache()) )) { //&& !isDobleVersion) {
					// eliminamos el cache si existe
					cache.delete();
					existeCache = false;
				}				
				
				boolean viewpdftool = String.valueOf(HandlerParameters.PARAMETROS.getViewpdftool()).equals("1");

				// comprobamos si el documento esta en borrador
				
				// si el documento es borrador no deberia imprimir
				if(url.indexOf("imprimir-IGUAL-1")!=-1) {
					if(!HandlerDocuments.isDocumentStatuVersionPrintable(idDocument,idVersion)) {
						url = url.replaceAll("imprimir-IGUAL-1", "imprimir-IGUAL-0");
					}
				}
				
				String contenido = str.toString().replaceAll(fileName.toString(), fileNameNew.toString());
				contenido = contenido.replaceAll("urlQwebdocuments", basePathEditor.toString());
				contenido = contenido.replaceAll("PARAMETRO1", String.valueOf(usuario.getIdPerson()));
				contenido = contenido.replaceAll("PARAMETRO2", basePathOnDemand.toString());
				contenido = contenido.replaceAll("PARAMETRO3", url);
				contenido = contenido.replaceAll("PARAMETRO4", extension);
				contenido = contenido.replaceAll("PARAMETRO5", String.valueOf(existeCache));
				contenido = contenido.replaceAll("PARAMETRO6", String.valueOf(viewpdftool)); // convert with libreoffice
				
				// grabamos la marca de tiempo en el usuario para validarlo al ejecutar el jnlp
				HandlerDBUser.updateTimeMarkForJavaWebStart(usuario.getIdPerson(), marcaDeTiempo);

				sb.setLength(0);
				sb.append(path);
				sb.append(File.separator);
				sb.append(fileNameNew);
				salida = new FileWriter(sb.toString());

				salida.write(contenido);

				response.getWriter().print(Constants.FOLDER_JNLP.concat("/").concat(Constants.FILE_JNLP_VISOR.concat(sufijo).concat(String.valueOf(usuario.getIdPerson())).concat(".jnlp")));
			}

		} catch (Exception e) {
			//System.out.println(e.getMessage());
			e.printStackTrace();
		} finally {
			if (entrada != null) {
				try {
					entrada.close();
				} catch (IOException ex) {
				}
			}
			if (salida != null) {
				try {
					salida.close();
				} catch (IOException ex) {
				}
			}
		}

		//System.out.println("Ejecutando codigo ajax");
		return null;
	}
	
	private Properties getProperties(String url) {
		Properties prop = new Properties();
		url = url.replaceAll("srShowDoc-SIGNO-", "");
		String[] eti = url.replaceAll("-IGUAL-", "=").split("-AMPER-");
		String[] p;

		for(int i=0; i<eti.length; i++) {
			p = eti[i].split("=");
			System.out.println(p[0]+"="+p[1]);
			prop.setProperty(p[0], p[1]);
		}
		
		return prop;
	}
}
