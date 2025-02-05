package com.desige.webDocuments.document.servlet;

import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.DiskFileUpload;
import org.apache.commons.fileupload.FileItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.desige.webDocuments.document.forms.CheckOutDocForm;
import com.desige.webDocuments.persistent.managers.HandlerDBUser;
import com.desige.webDocuments.persistent.managers.HandlerDocuments;
import com.desige.webDocuments.persistent.managers.HandlerParameters;
import com.desige.webDocuments.utils.ApplicationExceptionChecked;
import com.desige.webDocuments.utils.DesigeConf;
import com.desige.webDocuments.utils.StringUtil;
import com.desige.webDocuments.utils.ToolsHTML;
import com.desige.webDocuments.utils.beans.Users;
import com.focus.qwds.ondemand.server.usuario.excepciones.SessionNotValidException;

/**
 * Title: ServletVersionDocuments.java<br>
 * Copyright: (c) 2004 Focus Consulting C.A. <br/>
 *
 * @author Ing. Nelson Crespo (NC)
 * @version WebDocuments v3.0 <br>
 *          Changes:<br>
 *          <ul>
 *          <li> 06-09-2004 (NC) Creation </li>
 *          <li> 05-07-2005 (NC) Bugs Fixed </li>
 *          <li> 24-07-2005 (NC) Bug al redirigir una vez realizado el Check In </li>
 *          <ul>
 */
public class ServletVersionDocuments extends HttpServlet {
	/**
	 * 
	 */
	private static final long serialVersionUID = 8583056521623821183L;
	
	private static final Logger log = LoggerFactory.getLogger(ServletVersionDocuments.class);
	
	private String target = null;

	public void init() throws ServletException {
		//System.out.println("Begin ServletVersionDocuments");
		log.info("Iniciando servlet");
	}

	private Object readData(HttpServletRequest request) {
		Object data = null;
		try {
			ObjectInputStream ios = new ObjectInputStream(request.getInputStream());
			data = ios.readObject();
			ios.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return data;
	}

	private void checkUser(Object data, ObjectOutputStream objStream) throws Exception {
		boolean validUser = false;
		Hashtable resp = new Hashtable();
		if (data instanceof Users) {
			Users usuario = (Users) data;
			try {
				validUser = HandlerDBUser.checkUser(usuario, null, true);
				if (validUser) {
					HandlerDBUser.getDataUser(usuario);
					HandlerDBUser.getLanguajeUser(usuario);
					resp.put("nameUser", usuario.getNamePerson());
					if ((ToolsHTML.checkValue(usuario.getLanguage())) && (ToolsHTML.checkValue(usuario.getCountry()))) {
						Locale local = new Locale(usuario.getLanguage(), usuario.getCountry());
						resp.put("userLocale", local);
					}
				} else {
					resp.put("error", usuario.getMensaje());
				}
				resp.put("userValid", new Boolean(validUser));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		objStream.writeObject(resp);
	}

	public void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		//System.out.println("Procesando service:" + request.getQueryString());
		String cmd = request.getParameter("cmd");
		OutputStream out;
		ObjectOutputStream objStream;
		ResourceBundle rb = ToolsHTML.getBundle(request);
		try {
			CheckOutDocForm forma = (CheckOutDocForm) request.getSession().getAttribute("checkInDoc");

			if (cmd.equalsIgnoreCase("checkUser")) {
				out = response.getOutputStream();
				objStream = new ObjectOutputStream(out);
				Object data = readData(request);
				checkUser(data, objStream);
				objStream.close();
				out.close();
			}
			if (cmd.equalsIgnoreCase("upFile")) {
				//System.out.println("Subiendo Archivo");
				boolean resp = true;
				resp = procesaFicheros(request, forma);
				//System.out.println("resp = " + resp);
				if (resp) {
					request.getSession().setAttribute("info", rb.getString("doc.verOk"));
					if (!ToolsHTML.isEmptyOrNull(target)) {
						response.sendRedirect("loadStructMain.do");
					} else {
						response.sendRedirect("loadAllStruct.do");
					}
				} else {
					request.getSession().setAttribute("info", rb.getString("doc.upNotOK"));
					response.sendRedirect("checkIn.jsp");
				}
			}
		} catch (SessionNotValidException e) {
			System.out.println("La session de usuario ha finalizado... revise los tiempos de session");
			response.sendRedirect("logout.do");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void destroy() {

	}

	public synchronized boolean procesaFicheros(HttpServletRequest req, CheckOutDocForm forma) throws SessionNotValidException {
		log.info("Iniciando procesaFicheros");
		boolean resp = false;
		try {
			Users usuario = (Users) req.getSession().getAttribute("user");
			
			if(usuario==null || usuario.getUser()==null) {
				throw new SessionNotValidException("El tiempo de session ha finalizado");
			}
			
			DiskFileUpload fu = new DiskFileUpload();
			int lim = Integer.parseInt(DesigeConf.getProperty("application.limFiles"));
			fu.setSizeMax(1024 * 1024 * lim);
			fu.setSizeThreshold(4096);
			String path = ToolsHTML.getPath().concat("tmp") + File.separator + usuario.getUser(); // \\tmp
			new File(path).mkdirs();

			List fileItems = fu.parseRequest(req);
			if (fileItems == null) {
				return false;
			}
			Iterator i = fileItems.iterator();
			FileItem actual = null;

			log.info("iniciando ciclo de lectura de parametros");
			while (i.hasNext()) {
				actual = (FileItem) i.next();
				String name = actual.getFieldName();
				log.info(name);
				// TODO: CAMBIO PARA DOBLE VERSION
				if (name.equalsIgnoreCase("nameFile") || name.equalsIgnoreCase("nameFileParalelo")) {
					if (name.equalsIgnoreCase("nameFileParalelo")) {
						if (actual.getName() != null && !actual.getName().trim().equals("")) {
							String fileName = actual.getName();
							File fichero = new File(fileName);
							fichero = new File(path + File.separator + fichero.getName());
							actual.write(fichero);
							long datos = fichero.length();
							//System.out.println("datos = " + datos);
							if (datos == 0) {
								throw new ApplicationExceptionChecked("E0048");
							}
							forma.setNameFileParalelo(fichero.getName());
							forma.setContextTypeParalelo(actual.getContentType());
						}
					} else if (name.equalsIgnoreCase("nameFile")) {
						if ("1".equalsIgnoreCase(DesigeConf.getProperty("serverType"))) {
							String fileName = actual.getName();
							File fichero = new File(fileName);
							fichero = new File(path + File.separator + fichero.getName());
							actual.write(fichero);
							long datos = fichero.length();
							//System.out.println("datos = " + datos);
							if (datos == 0) {
								throw new ApplicationExceptionChecked("E0048");
							}
							forma.setNameFile(fichero.getName());
						}
					}
				} else {
					if (forma != null) {
						if ((actual != null) && (actual.getFieldName() != null)) {
							if (isExistField(actual, "numVersion")) {
								forma.setNumVersion(actual.getString());
							}
							if (isExistField(actual, "mayorVer")) {
								forma.setMayorVer(actual.getString());
							}
							if (isExistField(actual, "minorVer")) {
								forma.setMinorVer(actual.getString());
							}
							if (isExistField(actual, "expires")) {
								forma.setExpires(actual.getString());
							}
							if (isExistField(actual, "dateExpires")) {
								forma.setDateExpires(actual.getString());
							}
							if (isExistField(actual, "comments")) {
								forma.setComments(StringUtil.cleanTextContent(actual.getString()));
							}
							if (isExistField(actual, "target")) {
								target = actual.getString();
							}
							if (isExistField(actual, "changeMinorVersion") && actual.getString().trim().equalsIgnoreCase("0")) {
								forma.setChangeMinor(true);
							}
							if (isExistField(actual, "cmd")) {
								forma.setCmd(actual.getString());
							}
							if (isExistField(actual, "idCheckOut")) {
								forma.setIdCheckOut(actual.getString());
							}
						}
					}
				}
			}
			log.info("finalizando ciclo de lectura de parametros");
			if (forma != null) {
				log.info("iniciando bloqueo");
				//System.out.println(forma.getComments() + "=forma.getComments()**************************************************************************************");
				//System.out.println("Actualizando registro en la Base de datos..........." + forma.getIdCheckOut());
				resp = HandlerDocuments.checkInDocumen(forma, path, usuario.getUser());
				int borrarVariable = 0;
				checkUserInSession(req);
				log.info("finalizando bloqueo");
			}
		} catch (SessionNotValidException e) {
			e.printStackTrace();
			throw e;
		} catch (Exception e) {
			//System.out.println("e.getMessage() = " + e.getMessage());
			e.printStackTrace();
			resp = false;
		}
		log.info("Finalizando procesaFicheros");
		return resp;
	}
	
	private boolean isExistField(FileItem actual,String campo) {
		return actual.getFieldName().equalsIgnoreCase(campo) && !ToolsHTML.isEmptyOrNull(actual.getString());
	}

	public synchronized void checkUserInSession(HttpServletRequest req) {
		try {
			Users usuario = (Users) req.getSession().getAttribute("user");
			if (usuario != null) {
				usuario.setEspecialSession(false);
				usuario.setLastTime(ToolsHTML.sdfShowConvert.format(new java.util.Date()));
				usuario.setLastLogin(ToolsHTML.sdfShowConvert1.format(new java.util.Date()));
				if (!HandlerDBUser.updateUser(usuario)) {
					req.getSession().invalidate();
				} else {
					int sessionTimeout = -1;
	    			try {
	    				sessionTimeout = HandlerParameters.PARAMETROS.getEndSession();
	    				
	    				req.getSession().setMaxInactiveInterval(sessionTimeout * 60);
	    			} catch (Exception e) {
	    				// TODO: handle exception
	    			}
				}
			} else {
				req.getSession().invalidate();
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

}
