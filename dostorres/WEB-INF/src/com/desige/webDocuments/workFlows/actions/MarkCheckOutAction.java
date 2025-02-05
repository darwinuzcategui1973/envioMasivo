package com.desige.webDocuments.workFlows.actions;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.desige.webDocuments.persistent.managers.HandlerDBUser;
import com.desige.webDocuments.persistent.managers.HandlerParameters;
import com.desige.webDocuments.persistent.utils.JDBCUtil;
import com.desige.webDocuments.utils.ApplicationExceptionChecked;
import com.desige.webDocuments.utils.Constants;
import com.desige.webDocuments.utils.ToolsHTML;
import com.desige.webDocuments.utils.beans.Users;

public class MarkCheckOutAction extends Action {
	private static final Logger log = LoggerFactory.getLogger(MarkCheckOutAction.class);
	
	public synchronized ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {

		StringBuffer query = new StringBuffer();
		ArrayList<Object> parametros = new ArrayList<Object>();
		String sufijo;
		StringBuffer sb = new StringBuffer();
		boolean registro = false;
		boolean actualizarOriginal = false;
		
		try {
			Users usuario = (Users) request.getSession().getAttribute("user");

			registro = Boolean.parseBoolean(String.valueOf(request.getParameter("registro")));
			actualizarOriginal = Boolean.parseBoolean(String.valueOf(request.getParameter("actualizarOriginal")));

			if(actualizarOriginal) {
				request.getSession().setAttribute("actualizarOriginal", request.getParameter("idCheckOut") );
			}
			
			request.getSession().setAttribute("actualizarOriginal", request.getParameter("idCheckOut"));
			
			sufijo = (!registro ? "" : "Registro");

			if (usuario == null || !HandlerDBUser.isValidSessionUser(usuario.getUser(),request.getSession())) {
				throw new ApplicationExceptionChecked("E0035");
			}

			//System.out.println(request.getParameter("editar"));
			if (request.getParameter("editar") != null && String.valueOf(request.getParameter("editar")).equals("true")) {
				if (usuario.getIdPerson() > 0) {
					StringBuffer queryEditar = new StringBuffer();
					ArrayList<Object> parametrosEditar = new ArrayList<Object>();
					java.util.Calendar calendario = new java.util.GregorianCalendar();
					String timeExecution = ToolsHTML.sdfShowConvert1.format(calendario.getTime());
					// colocamos en 1 el valor edit de la tabla person
					parametrosEditar.add(usuario.getUser());
					queryEditar.append("update person set edit=1, dateLastPassEdit=");
					switch (Constants.MANEJADOR_ACTUAL) {
					case Constants.MANEJADOR_MSSQL:
						queryEditar.append("CONVERT(datetime,'").append(timeExecution).append("',120) ");
						break;
					case Constants.MANEJADOR_POSTGRES:
						queryEditar.append("CAST('").append(timeExecution).append("' AS timestamp) ");
						break;
					case Constants.MANEJADOR_MYSQL:
						queryEditar.append("TIMESTAMP('").append(timeExecution).append("') ");
						break;
					}
					queryEditar.append(" where nameUser=? and accountActive='1'");
					
					JDBCUtil.executeUpdate(queryEditar, parametrosEditar);
				}
				if (request.getParameter("salir") != null && String.valueOf(request.getParameter("salir")).equals("true")) {
					return null;
				}
			}
			
			//System.out.println(request.getParameter("noEditar"));
			if (request.getParameter("noEditar") != null && String.valueOf(request.getParameter("noEditar")).equals("true")) {
				if (usuario.getIdPerson() > 0) {
					// colocamos en 0 el valor edit de la tabla person
					StringBuffer queryNoEditar = new StringBuffer();
					ArrayList<Object> parametrosNoEditar = new ArrayList<Object>();
					parametrosNoEditar.add(usuario.getUser());
					queryNoEditar.append("update person set edit=0 where nameUser=? and accountActive='1'");
					//System.out.println(usuario.getUser());
					JDBCUtil.executeUpdate(queryNoEditar, parametrosNoEditar);
					//System.out.println("Reiniciamos en 0 edit para el usuario " + usuario.getUser());
				}
				if (request.getParameter("salir") != null && String.valueOf(request.getParameter("salir")).equals("true")) {
					return null;
				}
			}
			
			//System.out.println(request.getParameter("eliminar"));
			if (request.getParameter("eliminar") != null && String.valueOf(request.getParameter("eliminar")).equals("true")) {
				if (usuario.getIdPerson() > 0) {
					// eliminamos el archivo de jnlp
					String path = ToolsHTML.getPath();
					
					sb.setLength(0);
					sb.append(path);
					sb.append(Constants.FOLDER_JNLP);
					sb.append(File.separator);
					sb.append(Constants.FILE_JNLP);
					sb.append(sufijo);
					sb.append(usuario.getIdPerson());
					sb.append(".jnlp");
					//System.out.println("Eliminamos el archivo ".concat(sb.toString()));

					File f = new File(sb.toString());
					f.delete();
				}
				
				return null;
			}

			if (!registro) {
				parametros.add(new Integer(request.getParameter("idCheckOut")));
				parametros.add(usuario.getUser());
				query.append("update person set EditDocumentCheckOut=? where nameUser=?");

				//System.out.println(String.valueOf(request.getParameter("idCheckOut")));
				//System.out.println(usuario.getUser());
				JDBCUtil.executeUpdate(query, parametros);
			} else {
				//System.out.println(request.getParameter("idDocument"));
				//System.out.println(request.getParameter("idVersion"));
				if (request.getParameter("idDocument")!=null && request.getParameter("idVersion")!=null) {
					parametros = new ArrayList<Object>();
					parametros.add(usuario.getUser());
					query.setLength(0);
					query.append("update versiondoc set newRegisterToEdit=CAST(0 as bit) where createdBy=?");
					JDBCUtil.executeUpdate(query, parametros);

					parametros = new ArrayList<Object>();
					parametros.add(new Integer(String.valueOf(request.getParameter("idDocument"))));
					parametros.add(new Integer(String.valueOf(request.getParameter("idVersion"))));
					query.setLength(0);
					query.append("update versiondoc set newRegisterToEdit=").append(JDBCUtil.getCastAsBitString("1")).append(" where numDoc=? and numVer=?");
					JDBCUtil.executeUpdate(query, parametros);

					// colocamos el id de version que se esta editando en la tabla del usuario
					parametros = new ArrayList<Object>();
					parametros.add(new Integer(String.valueOf(request.getParameter("idVersion"))));
					parametros.add(usuario.getUser());
					query.setLength(0);
					query.append("update person set EditDocumentCheckOut=? where nameUser=?");
					JDBCUtil.executeUpdate(query, parametros);
				}
			}
			
			createEditionJNLP(request, response, usuario, sufijo);
		} catch (Exception e) {
			//System.out.println(e.getMessage());
			log.error("Error: " + e.getLocalizedMessage(), e);
		}
		
		//System.out.println("Ejecutando codigo ajax");
		return null;
	}
	
	/**
	 * 
	 * @param request
	 * @param response
	 * @param usuario
	 * @param sufijo
	 * @throws Exception
	 */
	private static final void createEditionJNLP(HttpServletRequest request, HttpServletResponse response,
			Users usuario, String sufijo) throws Exception{
		log.info("Iniciando creacion de archivo JNLP de edicion. sufijo=" + sufijo);
		
		FileReader entrada = null;
		FileWriter salida = null;
		StringBuffer sb = new StringBuffer();
		StringBuffer fileName = new StringBuffer(); 
		StringBuffer fileNameNew = new StringBuffer(); 
		
		try {
			
			String path = ToolsHTML.getPath().concat(Constants.FOLDER_JNLP);
			
			sb.setLength(0);
			sb.append(path);
			sb.append(File.separator);
			sb.append(Constants.FILE_JNLP);
			sb.append(sufijo);
			sb.append(".jnlp");
			
			fileName.setLength(0);
			fileName.append(Constants.FILE_JNLP); 
			fileName.append(sufijo);
			fileName.append(".jnlp");
			
			fileNameNew.setLength(0);
			fileNameNew.append(Constants.FILE_JNLP);
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

				String contenido = str.toString().replaceAll(fileName.toString(), fileNameNew.toString());
				contenido = contenido.replaceAll("urlQwebdocuments", basePathEditor.toString());
				contenido = contenido.replaceAll("xxxxxxxxxx", usuario.getUser());
				contenido = contenido.replaceAll("yyyyyyyyyy", usuario.getClave());
				contenido = contenido.replaceAll("zzzzzzzzzz", basePathOnDemand.toString());
				contenido = contenido.replaceAll("wwwwwwwwww", String.valueOf(HandlerParameters.PARAMETROS.getEndSessionEdit()));
				
				sb.setLength(0);
				sb.append(path);
				sb.append(File.separator);
				sb.append(fileNameNew);
				salida = new FileWriter(sb.toString());
				
				salida.write(contenido);
				salida.flush();
				salida.close();
				
				log.info("Escrito contenido del jnlp: " + sb.toString());
				
				final String rutaDestino = Constants.FOLDER_JNLP.concat(File.separator).concat(Constants.FILE_JNLP.concat(sufijo).concat(String.valueOf(usuario.getIdPerson())).concat(".jnlp"));
				response.getWriter().print(rutaDestino);
				response.getWriter().flush();
				response.getWriter().close();
				
				log.info("Escrito contenido del response: " + rutaDestino);
			} else {
				log.info("No existe el archivo " + f.getAbsolutePath() + ", no podemos crear el JNLP asociado.");
			}
		} catch (Exception e) {
			// TODO: handle exception
			log.error("Error: " + e.getLocalizedMessage(), e);
		} finally {
			if (entrada != null) {
				try {
					entrada.close();
				} catch (IOException ex) {
					/**/
				}
			}
			if (salida != null) {
				try {
					salida.close();
				} catch (IOException ex) {
					/**/
				}
			}
		}
		
		log.info("Terminada creacion de archivo JNLP de edicion. sufijo=" + sufijo);
	}
}
