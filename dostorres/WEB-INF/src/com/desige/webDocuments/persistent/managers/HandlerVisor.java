package com.desige.webDocuments.persistent.managers;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;
import java.util.Locale;
import java.util.Properties;
import java.util.ResourceBundle;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.desige.webDocuments.document.forms.BaseDocumentForm;
import com.desige.webDocuments.utils.Constants;
import com.desige.webDocuments.utils.DesigeConf;
import com.desige.webDocuments.utils.ToolsHTML;
import com.desige.webDocuments.utils.beans.Users;
import com.focus.util.Archivo;

public class HandlerVisor {
	
	private HttpServletRequest request;
	private HttpServletResponse response;
	private BaseDocumentForm forma;
	
	/**
	 * 
	 * @param request
	 * @param response
	 * @param forma
	 */
	public HandlerVisor(HttpServletRequest request, HttpServletResponse response,
			BaseDocumentForm forma) {
		this.request = request;
		this.response = response;
		this.forma = forma;
	}
	
	/**
	 * 
	 * @param pathContext
	 * @param isFirstPage
	 * @param toPrint
	 * @param forFiles implica que el visor mostrara un expediente si el valor es true
	 * @throws Exception
	 */
	public void generateJNLP(String pathContext, boolean isFirstPage, boolean toPrint, boolean forFiles, BaseDocumentForm forma) 
			throws Exception {
		
		FileReader entrada = null;
		FileWriter salida = null;
		StringBuffer sb = new StringBuffer();
		String sufijo = "";
		StringBuffer fileName = new StringBuffer(); 
		StringBuffer fileNameNew = new StringBuffer(); 
		
		String path = pathContext.concat(Constants.FOLDER_JNLP);
		
		try {
			Users usuario = (Users) request.getSession().getAttribute("user");
			
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
			
				entrada = null;
				StringBuffer str = new StringBuffer();
				entrada = new FileReader(sb.toString());
				int c;
				while ((c = entrada.read()) != -1) {
					str.append((char) c);
				}

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
				StringBuffer parametro = new StringBuffer();
			
				parametro.append("srShowDoc").append("-SIGNO-");
				parametro.append("idVersion").append("-IGUAL-").append(request.getParameter("idVersion"));
				parametro.append("-AMPER-").append("swAdmin").append("-IGUAL-").append(request.getParameter("swAdmin")==null?"false":request.getParameter("swAdmin"));
				parametro.append("-AMPER-").append("imprimir").append("-IGUAL-").append(toPrint?"1":"0");
				parametro.append("-AMPER-").append("showFile").append("-IGUAL-").append(request.getParameter("showFile")==null?"showFile.jsp":request.getParameter("showFile"));
				parametro.append("-AMPER-").append("firstPage").append("-IGUAL-").append(isFirstPage?"true":"false");
				if(forma!=null) {
					parametro.append("-AMPER-").append("copyContents").append("-IGUAL-").append(forma.isCopyContents());
				} else {
					parametro.append("-AMPER-").append("copyContents").append("-IGUAL-").append(request.getParameter("copyContents")==null?(request.getParameter("flujo")!=null?request.getParameter("flujo"):"false"):request.getParameter("copyContents"));
				}
				//parametro.append("-AMPER-").append("controlada").append("-IGUAL-").append(request.getParameter("controlada")==null?"true":request.getParameter("controlada"));
				parametro.append("-AMPER-").append("controlada").append("-IGUAL-").append(forma!=null?forma.isControlada():"1");
				parametro.append("-AMPER-").append("nameFile").append("-IGUAL-").append(request.getParameter("nameFile"));
				parametro.append("-AMPER-").append("idDocument").append("-IGUAL-").append(request.getParameter("idDocument"));
				//parametro.append("-AMPER-").append("numberCopies").append("-IGUAL-").append(request.getParameter("numberCopies")==null?"1":request.getParameter("numberCopies"));
				parametro.append("-AMPER-").append("numberCopies").append("-IGUAL-").append(forma!=null?forma.getNumberCopies():"1");
				parametro.append("-AMPER-").append("marca").append("-IGUAL-").append(marcaDeTiempo);
				//f1 solo aplica cuando estoy intentando visualizar un expediente
				parametro.append("-AMPER-").append("f1").append("-IGUAL-").append(request.getParameter("f1"));
				parametro.append("-AMPER-").append("username").append("-IGUAL-").append(usuario.getUser());
				parametro.append("-AMPER-").append("expedientePiecesNames").append("-IGUAL-").append(request.getAttribute("expedientePiecesNames"));
				
				// url de prueba
				//url = "srShowDoc-SIGNO-idVersion-IGUAL-1-AMPER-swAdmin-IGUAL-true-AMPER-imprimir-IGUAL-1-AMPER-showFile-IGUAL-showFile.jsp
				//-AMPER-firstPage-IGUAL-true-AMPER-copyContents-IGUAL-false-AMPER-controlada-IGUAL-false
				//-AMPER-nameFile-IGUAL-1-AMPER-idDocument-IGUAL-1-AMPER-numberCopies-IGUAL-1";
				//
				//url += url.concat("-AMPER-marca-IGUAL-").concat(marcaDeTiempo);
				
				String url = parametro.toString();
			
				Properties prop = getProperties(url);
				String extension = "null";
				boolean existeCache = false;
				
				if(!forFiles){
					// buscamos el documento en base de datos
					extension = ToolsHTML.getExtension(HandlerStruct.loadNameFileDocument(prop.getProperty("idDocument")));
					boolean isDobleVersion = ToolsHTML.extensionIsDobleVersion(extension);
					
					int idVersion = Integer.parseInt(prop.getProperty("idVersion"));
					int idDocument = Integer.parseInt(prop.getProperty("idDocument"));
					
					String nameFileCache = Archivo.getNameFileEncripted(null, "versiondocview", idVersion, null);
					File cache = new File(nameFileCache);
					existeCache = cache.exists();
					
					//si el archivo es de doble version no borramos la cache ya que ese archivo es de visualizacion
					//y no se genera con conversion corriente a PDF
					if(!isDobleVersion && ("1".equals(String.valueOf(HandlerParameters.PARAMETROS.getDisabledCache())) || cache.length() == Constants.FILE_ERROR_NOT_GENERATE_PDF)) { //&& !isDobleVersion) {
						// eliminamos el cache si existe
						cache.delete();
						existeCache = false;
					}
					
					// si el documento es borrador no deberia imprimir
					// comprobamos si el documento esta en borrador
					if(url.indexOf("imprimir-IGUAL-1")!=-1) {
						if(!HandlerDocuments.isDocumentStatuVersionPrintable(idDocument, idVersion)) {
							url = url.replaceAll("imprimir-IGUAL-1", "imprimir-IGUAL-0");
						}
					}
				} 
				
				boolean viewpdftool = String.valueOf(HandlerParameters.PARAMETROS.getViewpdftool()).equals("1");
	
				String contenido = str.toString().replaceAll(fileName.toString(), fileNameNew.toString());
				contenido = contenido.replaceAll("urlQwebdocuments", basePathEditor.toString());
				contenido = contenido.replaceAll("PARAMETRO1", String.valueOf(usuario.getIdPerson()));
				contenido = contenido.replaceAll("PARAMETRO2", basePathOnDemand.toString());
				contenido = contenido.replaceAll("PARAMETRO3", url);
				//contenido = contenido.replaceAll("PARAMETRO4", forma.getNameFile());
				contenido = contenido.replaceAll("PARAMETRO4", extension);
				contenido = contenido.replaceAll("PARAMETRO5", String.valueOf(existeCache));
				contenido = contenido.replaceAll("PARAMETRO6", String.valueOf(viewpdftool)); // convert with libreoffice
				contenido = contenido.replaceAll("PARAMETRO7", buildBackgroundId(forma)); // texto del pie de pagina
				//marca de agua para copias controladas y no controladas
				contenido = contenido.replaceAll("PARAMETRO8",
						Boolean.toString(
								"1".equals(HandlerParameters.PARAMETROS.getPiePaginaWaterMark())));
				
				contenido = contenido.replaceAll("PARAMETRO9", Boolean.toString(forFiles)); // flag para saber si se esta visualizando un expediente o no
				
				// grabamos la marca de tiempo en el usuario para validarlo al ejecutar el jnlp
				HandlerDBUser.updateTimeMarkForJavaWebStart(usuario.getIdPerson(), marcaDeTiempo);
				
				sb.setLength(0);
				sb.append(path);
				sb.append(File.separator);
				sb.append(fileNameNew);
	
				// escribe el archivo en disco
				salida = new FileWriter(sb.toString());
				salida.write(contenido);
	
				//response.getWriter().print(Constants.FOLDER_JNLP.concat(File.separator).concat(Constants.FILE_JNLP_VISOR.concat(sufijo).concat(String.valueOf(usuario.getIdPerson())).concat(".jnlp")));
				response.setHeader("Content-type","application/x-java-jnlp-file");
				response.setHeader("Content-Disposition","attachment; filename=".concat(fileNameNew.toString()));
				response.setContentType("text/plain;charset=ISO-8859-1");
				response.getWriter().print(contenido);
			}
		}catch(Exception e) {
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
		
	}
	
	private Properties getProperties(String url) {
		Properties prop = new Properties();
		url = url.replaceAll("srShowDoc-SIGNO-", "");
		String[] eti = url.replaceAll("-IGUAL-", "=").split("-AMPER-");
		String[] p;

		for(int i=0; i<eti.length; i++) {
			p = eti[i].split("=");
			//System.out.println(p[0]+"="+p[1]);
			prop.setProperty(p[0], p[1]);
		}
		
		return prop;
	}	
	
	/**
	 * 
	 * @param forma
	 * @return
	 */
	private String buildBackgroundId(BaseDocumentForm forma) {
		// creamos la imagen con el codigo del documento
		StringBuffer cadena = new StringBuffer("");
		
		if(forma!=null) {
			if(!HandlerDocuments.docBorrador.equals(Integer.toString(forma.getStatu()))){
				String version = String.valueOf(forma.getMayorVer()).concat(".").concat(
						String.valueOf(forma.getMinorVer()));
				String numero = (forma.getPrefix()!=null?forma.getPrefix():"").concat(forma.getNumber());
				
				if(numero!=null && "1".equals(String.valueOf(HandlerParameters.PARAMETROS.getPrintNumber()))) {
					cadena.append("Cod.:").append(numero);
				}
				
				if(version!=null && "1".equals(String.valueOf(HandlerParameters.PARAMETROS.getPrintVersion()))) {
					cadena.append(cadena.length()>0?" / ":"");
					cadena.append("Ver.:").append(version);
				}
				
				try {
					if("1".equals(String.valueOf(HandlerParameters.PARAMETROS.getPrintApprovedDate()))){
						ResourceBundle rb = ResourceBundle.getBundle("LoginBundle",
								new Locale(DesigeConf.getProperty("language.Default"), DesigeConf.getProperty("country.Default")));
						
						cadena.append(cadena.length()>0?" / ":"");
						cadena.append(rb.getString("showDoc.approvedAbv")).append(".:").append(forma.getDateApproved().substring(0, 
								forma.getDateApproved().indexOf(" ")));
					}
				} catch (Exception e) {
					// TODO: handle exception
				}
				
				//verifico al final si quedo algo en el resultado
				if(cadena.length() == 0){
					cadena.append("null");
				}
			} else {
				cadena.append("null");
			}
		} else {
			cadena.append("null");
		}
		
		return cadena.toString();
	}
}
