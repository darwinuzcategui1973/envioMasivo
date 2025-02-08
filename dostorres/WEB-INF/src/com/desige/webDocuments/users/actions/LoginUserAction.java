package com.desige.webDocuments.users.actions;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.desige.webDocuments.document.forms.BaseDocumentForm;
import com.desige.webDocuments.persistent.managers.HandlerBD;
import com.desige.webDocuments.persistent.managers.HandlerDBUser;
import com.desige.webDocuments.persistent.managers.HandlerParameters;
import com.desige.webDocuments.persistent.utils.JDBCUtil;
import com.desige.webDocuments.users.forms.LoginUser;
import com.desige.webDocuments.utils.ApplicationExceptionChecked;
import com.desige.webDocuments.utils.Constants;
import com.desige.webDocuments.utils.ToolsHTML;
import com.desige.webDocuments.utils.beans.SuperAction;
import com.desige.webDocuments.utils.beans.Users;

/**
 * Title: LoginUserAction.java <br/> Copyright: (c) 2004 Focus Consulting C.A.<br/>
 * 
 * @author Ing. Nelson Crespo (NC)
 * @version WebDocuments v3.0 <br/> Changes:<br/>
 *          <ul>
 *          <li> 23/03/2004 (NC) Creation </li>
 *          <li> 29/05/2005 (NC) Cambios en el manejo del prefijo del Documento </li>
 *          </ul>
 */

public class LoginUserAction extends SuperAction {
	static Logger log = LoggerFactory.getLogger(LoginUserAction.class.getName());

	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
		super.init(mapping, form, request, response);
		LoginUser login = (LoginUser) form;
		boolean logout=false;
		
		request.getSession().setAttribute("DEMO", request.getParameter("demo"));
		
		// leemos el parametro de visualizacion en pdf
		Constants.PRINTER_PDF = "1".equals(String.valueOf(HandlerParameters.PARAMETROS.getOpenoffice()));
		
		
		if (login != null) {
			// el usuario sera sacado de session antes de iniciar
			if(request.getParameter("logout")!=null && request.getParameter("logout").equals("true")) {
				login.setUser(String.valueOf(request.getSession().getAttribute("userKey")));
				login.setClave(String.valueOf(request.getSession().getAttribute("userValue")));
				logout=true;
			}
			request.getSession().removeAttribute("userKey");
			request.getSession().removeAttribute("userValue");

			removeObjectSession("usuario");
			Users usuario = new Users(login.getUser(), login.getClave(), null);

			boolean validUser;
			
			// validamos si los usuarios son nombrados o concurrentes
        	if(!ToolsHTML.showConcurrentUser()) {
            	// consultamos los usuarios actualmente registrados
        		HandlerDBUser hdu = new HandlerDBUser();
            	int reg = hdu.getUserRegisterInDatabase();
        		if(reg > Constants.MAXIMO_USUARIOS_VERSION_LITE) {
                    return goError("E0135");
        		}
        	}
			
			if(login.getUser()!=null && login.getUser().trim().equalsIgnoreCase(Constants.ID_USER_TEMPORAL)) {
				return goError("E0020");
			}
			
			try {
				String url = getParameter("url");
				boolean valUsr = ToolsHTML.isEmptyOrNull(url);// getSessionObject("url")==null;
				
				// Cargar el usuario en la variable usuario y valida la clave.
				validUser = HandlerDBUser.checkUser(usuario, login, valUsr, request);
				if(!validUser && logout) {
					HandlerDBUser.getEraseUserConnect(new String[]{login.getUser()});
					usuario.setMensaje(null);
					validUser = HandlerDBUser.checkUser(usuario, login, valUsr);
				}
				
				if(usuario.getMensaje()!=null && usuario.getMensaje().equals("err.userLogged")) {
					request.getSession().setAttribute("userKey", login.getUser());
					request.getSession().setAttribute("userValue", login.getClave());
					request.setAttribute("logout", "true");
				}
				
				// Si no estoy validando el Usuario es porque voy es a mostrar un Documento
				if (!valUsr) {
					putObjectSession("user", usuario);
					boolean openURLInNewWindow = false;
					
					if(url.contains(Constants.PARAMETER_ALLOW_SESSION_CONNECTION)){
						HandlerDBUser.userConnectSession.put(login.getUser(), request.getSession().getId());
						HandlerDBUser.checkUser(usuario, login, true, request);
					}
					if(url.contains(Constants.OPEN_URL_PETITION_IN_A_NEW_WINDOW)){
						openURLInNewWindow = true;
						usuario.setActionToInvokeAfterLogin(url);
					}
					
					if(! openURLInNewWindow){
						removeObjectSession("url");
						return new ActionForward(url);
					}
				}
				
				if (validUser) {
					// registramos la session valida para el usuario
					HandlerDBUser.userConnectSession.put(login.getUser(), request.getSession().getId());
					//request.getSession().setAttribute("documentosRegistrados",ToolsHTML.documentosRegistrados());
					
					HandlerDBUser.getLanguajeUser(usuario);
					putObjectSession("usuario", usuario.getNamePerson());
					usuario.setLastTime(ToolsHTML.sdfShowConvert.format(new java.util.Date()));
					usuario.setLastLogin(ToolsHTML.sdfShowConvert1.format(new java.util.Date()));
					putObjectSession("user", usuario);
					ToolsHTML.setLanguage(usuario, request);
					removeObjectSession("login");
					if (getSessionObject("optionReturn") != null) {
						ActionForward optionReturn = (ActionForward) getSessionObject("optionReturn");
						if (optionReturn != null) {
						//	BaseDocumentForm forma = (BaseDocumentForm) getSessionObject("showDocument");
						//	if (forma != null) {
						//		forma.setPrefix(ToolsHTML.getPrefixToDoc(getSession(), usuario, forma.getIdNode()));
						//	}
							removeObjectSession("optionReturn");
							return optionReturn;
						}
					}
					
					//Se elimina reporte de record de usuario si existe
			        try{
						String path = ToolsHTML.getPath();
						//despúes veo esto para eliminar recibos
				        String nameFile = path.concat("recibos").concat(File.separator).concat(usuario.getIdPerson()+"Record.xls");
				        File archivo = new File(nameFile);
				        if(archivo.exists()){
				        	archivo.delete();
				        }
			        }catch (Exception e) {
						//System.out.println("Error al tratar de eliminar reporte de record: " + e);
					}
			        
					//Colocamos en 0 el valor edit de la tabla person
					if (usuario.getIdPerson() > 0) {
						StringBuffer queryNoEditar = new StringBuffer();
						ArrayList<Object> parametrosNoEditar = new ArrayList<Object>();
						parametrosNoEditar.add(usuario.getUser());
						queryNoEditar.append("update person set edit=0 where nameUser=? and accountActive='1'");
						JDBCUtil.executeUpdate(queryNoEditar, parametrosNoEditar);
						//System.out.println("Reiniciamos en 0 edit para el usuario " + usuario.getUser());
					}
					
					// Validaci�n de la fecha de cambio de password
					if (usuario.getLastDatePass() != null) {
						log.info("Last Date Pass: " + usuario.getLastDatePass());
						String[] values = new String[2];
						// values[0] Indica si se maneja vencimiento de claves
						// values[1] Indica la cantidad de dias en que vence el password.
						values[0] = String.valueOf(HandlerParameters.PARAMETROS.getExpirePass());
						values[1] = HandlerParameters.PARAMETROS.getDaysEndPass();

						if (values != null) {
							// si se desea manejar la expiraci�n de Clave
							if ("0".equalsIgnoreCase(values[0].trim()) && ToolsHTML.isNumeric(values[1])) {

								java.util.Calendar calendario = new java.util.GregorianCalendar();

								// Hoy menos la cantidad de dias de vencimiento del password.
								// Nos da la fecha maxima que debeia tener el password.
								calendario.add(Calendar.DAY_OF_MONTH, -1 * Integer.parseInt(values[1].trim()));

								String timeExecution = ToolsHTML.date.format(calendario.getTime());

								
					            StringBuffer sql = new StringBuffer(" from person where nameUser='").append(usuario.getUser()).append("'");
					            sql.append(" AND accountActive = '1'");
					            String primeravez = HandlerBD.getField("primeravez",sql.toString(),Thread.currentThread().getStackTrace()[1].getMethodName());
								
								//System.out.println("timeExecution = " + timeExecution);
								if (timeExecution.compareToIgnoreCase(ToolsHTML.date.format(usuario.getLastDatePass())) >= 0 || ToolsHTML.isEmptyOrNull(primeravez) ) {
									putAtributte("info", getMessage("user.mustChange"));
									putObjectSession("mustChange", "true");
									return goTo("changePass");
								}
							}
						}
					} else {
						putAtributte("info", getMessage("user.mustChange"));
						putObjectSession("mustChange", "true");
						return goTo("changePass");
					}
					request.setAttribute("popup","yes");
					return goSucces();
				} else {
					removeObjectSession("login");
					return goError(usuario.getMensaje());
				}
			} catch (ApplicationExceptionChecked aec) {
				aec.printStackTrace();
				return goError(aec.getKeyError());
			} catch (Exception e) {
				e.printStackTrace();
				return goError("E0008");
			}
		} else {
			log.info("El objeto login es null...");
			return goError("E0008");
		}
	}
}
