package com.desige.webDocuments.persistent.managers;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.StringTokenizer;
import java.util.TreeMap;
import java.util.Vector;

import javax.mail.Session;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.transaction.Transaction;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.desige.webDocuments.area.forms.Area;
import com.desige.webDocuments.cargo.forms.Cargo;
import com.desige.webDocuments.mail.actions.SendMailTread;
import com.desige.webDocuments.mail.forms.AddressForm;
import com.desige.webDocuments.mail.forms.MailForm;
import com.desige.webDocuments.perfil.forms.PerfilActionForm;
import com.desige.webDocuments.persistent.utils.JDBCUtil;
import com.desige.webDocuments.sacop.actions.LoadSacop;
import com.desige.webDocuments.seguridad.forms.PermissionUserForm;
import com.desige.webDocuments.seguridad.forms.SeguridadUserForm;
import com.desige.webDocuments.users.forms.LoginUser;
import com.desige.webDocuments.util.Encryptor;
import com.desige.webDocuments.utils.ApplicationExceptionChecked;
import com.desige.webDocuments.utils.Constants;
import com.desige.webDocuments.utils.DesigeConf;
import com.desige.webDocuments.utils.IDDBFactorySql;
import com.desige.webDocuments.utils.ToolsHTML;
import com.desige.webDocuments.utils.beans.BeanCheckSec;
import com.desige.webDocuments.utils.beans.Search;
import com.desige.webDocuments.utils.beans.Users;
import com.focus.qweb.dao.AreaDAO;
import com.focus.qweb.dao.CargoDAO;
import com.focus.qweb.dao.PersonDAO;
import com.focus.qweb.dao.PlanillaSacop1DAO;
import com.focus.qweb.to.AreaTO;
import com.focus.qweb.to.CargoTO;
import com.focus.qweb.to.PersonTO;
import com.focus.qweb.to.PlanillaSacop1TO;
import com.focus.util.ModuloBean;

import sun.jdbc.rowset.CachedRowSet;

/**
 * Title: HandlerDBUser.java <br/>
 * Copyright: (c) 2004 Desige Servicios de Computaci�n<br/>
 *
 * @author Ing. Nelson Crespo (NC)
 * @author Ing. Simon Rodriguez (SR)
 * @version WebDocuments v1.0 <br/>
 *          Changes:<br/>
 *          <ul>
 *          <li>23/03/2004 (NC) Creation</li>
 *          <li>18/05/2005 (SR) Se inserto y actualizo getToEditRegister</li>
 *          <li>29/05/2005 (NC) Cambios en el manejo de la Seguridad</li>
 *          <li>02/06/2005 (SR) Se inserto y actualizo getToImpresion</li>
 *          <li>10/06/2005 (SR) Se inserto y actualizo toCheckTodos</li>
 *          <li>18/06/2005 (SR) Se creo el metodo getEraseUserCnonnect</li>
 *          <li>21/07/2005 (SR) Se creo el metodo getAllUsersFilter</li>
 *          <li>22/08/2005 (SR) Se creo el metodo HandlerDBUser.checkExistSecurityDocs(.. para heredar la permisologia de las carpetas en los documentos por
 *          usuarios</li>
 *          <li>23/08/2005 (SR) Se creo el metodo HandlerDBUser.checkExistSecurityDocsGrup(.. para heredar la permisologia de las carpetas en los documentos por
 *          grupos</li>
 *          <li>17/04/2006 (SR) Se valida que los usuarios tengan un solo user y email en sus cuentas</li>
 *          <Li>27/04/2006 (NC) Se agreg� el m�todo updateSecurityStructUser con 2 par�metros</li>
 *          <Li>03/05/2006 (SR) Valida que el mail no exista en editarlo al actualizarlo, no valla hacer que actualize con un mail existente</li>
 *          <Li>26/06/2006 (SR) Se ordeno alluser por nombre y apellido</li>
 *          <li>30/06/2006 (NC) Uso del Log y cambios menores en Seguridad</li>
 *          </ul>
 */
public class HandlerDBUser extends HandlerBD {
	/**
	 *
	 */
	private static final long serialVersionUID = 3362188770419075241L;
	private static final Logger log = LoggerFactory.getLogger(HandlerDBUser.class);
	public static final String nameUser = "person";
	public static final String namePerson = "person";
	public static Hashtable userConnect = null;
	public static Hashtable<String, String> userConnectSession = new Hashtable<String, String>();
	public static int limUser = -1;
	public static int limUserViewer = -1;
	public static String license = "";
	public static String licenseViewer = "";
	public static String endLicense = "";
	private static boolean isCheck = false;
	private static boolean isCheckViewer = false;
	private static java.util.Date dateExpireLicense = null;
	private static String keyGen = null;
	private static String num = null;
	private static String numViewer = null;
	private static String date = null;
	private static String activityProgress = null;
	//ydavila Ticket 001-00-003023
	private static String getClaveUser= "";

	public static void getDataUser(Users user) throws Exception {
		if (user != null) {
			StringBuilder sql = new StringBuilder("SELECT numRecordPages,");
			switch (Constants.MANEJADOR_ACTUAL) {
			case Constants.MANEJADOR_MSSQL:
				sql.append("(p.Apellidos+' '+p.Nombres) AS namePerson,");
				break;
			case Constants.MANEJADOR_POSTGRES:
				sql.append("(p.Apellidos||' '||p.Nombres) AS namePerson,");
				break;
			case Constants.MANEJADOR_MYSQL:
				sql.append("CONCAT(p.Apellidos , ' ' , p.Nombres) AS namePerson,");
				break;
			}
			sql.append(" IdLanguage FROM person p");
			sql.append(" WHERE nameUser = '").append(user.getNameUser()).append("'");
			Properties prop = JDBCUtil.doQueryOneRow(sql.toString(),Thread.currentThread().getStackTrace()[1].getMethodName());
			if ((prop != null) && prop.getProperty("isEmpty").equalsIgnoreCase("false")) {
				user.setNamePerson(prop.getProperty("namePerson"));
				user.setIdLanguaje(prop.getProperty("IdLanguage"));
				int valor = Integer.parseInt(prop.getProperty("numRecordPages"));
				if (valor > 0) {
					user.setNumRecord(valor);
				}
			} else {
				user.setNamePerson(" ");
			}
		}
	}

	// DEPRECATED: metodo desaprobado ya que no valida el id de la session en curso
	// solo valida si el usuario esta en el vector de usuarios logueados
	public static synchronized boolean isValidSessionUser(String userName) {
		if (userConnect != null && !ToolsHTML.isEmptyOrNull(userName)) {
			if (userConnect.containsKey(userName.trim())) {
				return true;
			}
		}
		return false;
	}

	public static synchronized boolean isValidSessionUser(String userName, HttpSession session) {
		if (userConnect != null && !ToolsHTML.isEmptyOrNull(userName)) {
			if (userConnect.containsKey(userName.trim())) {
				////System.out.println(session.getId());
				////System.out.println(userConnectSession.get(userName));
				if(session.getId().equals(userConnectSession.get(userName))) {
					return true;
				}
			}
		}
		return false;
	}


	public static void getLanguajeUser(Users user) throws Exception {
		if (user != null && !ToolsHTML.isEmptyOrNull(user.getIdLanguaje())) {
			StringBuffer sql = new StringBuffer("SELECT l.Codigo,l.country FROM idiomas l");
			sql.append(" WHERE l.Id ='").append(user.getIdLanguaje()).append("'");
			log.debug("sql = " + sql);
			Properties prop = JDBCUtil.doQueryOneRow(sql.toString(),Thread.currentThread().getStackTrace()[1].getMethodName());
			if ((prop != null) && prop.getProperty("isEmpty").equalsIgnoreCase("false")) {
				String language = prop.getProperty("Codigo");
				String country = prop.getProperty("country");
				if (ToolsHTML.checkValue(language)) {
					user.setLanguage(language);
				}
				if (ToolsHTML.checkValue(country)) {
					user.setCountry(country);
				}
			} else {
				String language = DesigeConf.getProperty("language.Default");
				String country = DesigeConf.getProperty("country.Default");
				if (ToolsHTML.checkValue(language)) {
					user.setLanguage(language);
				}
				if (ToolsHTML.checkValue(country)) {
					user.setCountry(country);
				}
			}
		}
	}

	public static boolean checkUser(Users nameUser, LoginUser login, boolean validUser) throws ApplicationExceptionChecked, Exception {
		return checkUser(nameUser, login,validUser,null);
	}

	public static boolean checkUser(Users nameUser, LoginUser login, boolean validUser, HttpServletRequest request) throws ApplicationExceptionChecked, Exception {
		boolean isNewUser = false;
		if (nameUser != null) {
			StringBuffer sql = new StringBuffer();
			switch (Constants.MANEJADOR_ACTUAL) {
			case Constants.MANEJADOR_MSSQL:
				sql.append("SELECT (p.Apellidos+' '+p.Nombres) AS namePerson,");
				break;
			case Constants.MANEJADOR_POSTGRES:
				sql.append("SELECT (p.Apellidos || ' ' || p.Nombres) AS namePerson,");
				break;
			case Constants.MANEJADOR_MYSQL:
				sql.append("SELECT CONCAT(p.Apellidos , ' ' , p.Nombres) AS namePerson,");
				break;
			}
			sql.append("p.idPerson,p.numRecordPages,p.IdLanguage,p.clave,p.idGrupo,p.email,p.nameUser,p.dateLastPass,");
			sql.append("p.dateLastPassEdit,p.edit,p.modo,p.lado,p.ppp,p.panel,p.separar,p.pagina,p.minimo,p.typeDocuments, ");
			sql.append("p.ownerTypeDoc,p.idNodeDigital,p.idNodeService,p.typesetter,p.checker,p.lote,p.correlativo,p.javaWebStart, ");
			sql.append("c.idCargo, c.cargo, a.idArea, a.area ");
			sql.append("FROM person p, tbl_cargo c, tbl_area a ");
			sql.append("WHERE CAST(p.cargo AS INT)=c.idCargo "); 
			sql.append("AND c.idArea=a.idArea ");
			sql.append("AND nameUser = '").append(nameUser.getUser()).append("' ");
			sql.append("AND accountActive = '1' ");
			
			Properties prop = JDBCUtil.doQueryOneRow(sql.toString(),Thread.currentThread().getStackTrace()[1].getMethodName());

			boolean isFoundUser = prop != null && prop.getProperty("isEmpty").equalsIgnoreCase("false");
			boolean isActiveDirectoryConfigurated = ToolsHTML.isActiveDirectoryConfigurated();
			boolean isValidActiveDirectory = false;
			boolean isUserAdmin = nameUser!=null && nameUser.getUser().equals("admin");
			boolean isUserViewer = false;
			
			if(isFoundUser) {
				isUserViewer = prop.getProperty("idGrupo").equals("1");
			}
			

			if(!isUserViewer && isActiveDirectoryConfigurated && !isUserAdmin ) {
				if ( isFoundUser ) {
					try {
						if(ToolsHTML.isValidActiveDirectory(nameUser.getUser(), nameUser.getPass())) {
							isValidActiveDirectory = true;
							
							int i = 2; 
							if(1==i) { // se deshabilita la opcion de crear el usuario // Ini creacion de usuario
								// creamos el usuario y hacemos la consulta nuevamente si esta en Active Directory
								StringBuffer sb = new StringBuffer();
								ArrayList<Object> parametros = new ArrayList<Object>();
			
								long idPerson = IDDBFactorySql.getNextIDLong("usuario");
								String idioma = HandlerParameters.PARAMETROS.getIdioma();
								int idNodeService = HandlerParameters.PARAMETROS.getIdNodeService();
										
								parametros.add(idPerson);
								parametros.add(nameUser.getUser());
								parametros.add("Escriba aqu� su nombre");
								parametros.add("Escriba aqu� su apellido");
								parametros.add(ToolsHTML.encripterPass(nameUser.getPass()));
								parametros.add(new Integer(1));
								parametros.add(new Integer(1));
								parametros.add(new Integer(1));
								parametros.add(idioma);
								parametros.add(idNodeService);
			
								StringBuffer insert = new StringBuffer("INSERT INTO person ");
								insert.append("(idPerson,nameUser,nombres,apellidos,clave,idGrupo,cargo,idarea,IdLanguage,idNodeService) ");
								insert.append("VALUES(?,?,?,?,?,?,?,?,?,?) ");
			
								int act = JDBCUtil.executeUpdate(insert,parametros);
			
								prop = JDBCUtil.doQueryOneRow(sql.toString(),Thread.currentThread().getStackTrace()[1].getMethodName());
			
								isNewUser = true;
								
								
			
								try {
									// le enviamos el mensaje al administrador
									Locale defaultLocale = new Locale(DesigeConf.getProperty("language.Default"), DesigeConf.getProperty("country.Default"));
									ResourceBundle rb = ResourceBundle.getBundle("LoginBundle", defaultLocale);
									StringBuilder msg = new StringBuilder(2048).append(rb.getString("mail.MsgNewUserViewerRegister").replaceAll("-LOGIN-",
											nameUser.getUser()));
			
									MailForm formaMail = new MailForm();
									formaMail.setFrom(HandlerParameters.PARAMETROS.getMailAccount());
									formaMail.setNameFrom(rb.getString("mail.system"));
									formaMail.setTo(formaMail.getFrom());
									formaMail.setSubject(rb.getString("mail.MsgNewUserViewerRegisterSubject"));
									formaMail.setMensaje(msg.toString());
									// SendMailAction.send(formaMail);
									 
								
									if (formaMail != null) {
										SendMailTread mail = new SendMailTread(formaMail);
										mail.start();
									}
								} catch (Exception ex) {
									log.debug("Exception");
									log.error(ex.getMessage());
									ex.printStackTrace();
								}
			
								//insertamos seguridad especifica del usuario
								SeguridadUserForm securityUser = new SeguridadUserForm(nameUser.getUser(), true);
								securityUser.setIdPerson(idPerson);
								HandlerSeguridad.insertUserSecurity(securityUser, null);
			
								log.info("Creado registro de Seguridad por defecto para el usuario '" + nameUser.getUser() + "'");
							} // fin creacion de usuario
							
						} else {
							log.info("Validacion de usuario en active directory dio false");
							if (login != null) {
								login.cleanForm();
							}
							nameUser.setMensaje("badUser");
							return false;
						}
					} catch(Exception e) {
						log.info("No hay conexion con active directory");
						if (login != null) {
							login.cleanForm();
						}
						nameUser.setMensaje("badUser.authenticate");
						return false;
					}
				} else {
					if (login != null) {
						login.cleanForm();
					}
					nameUser.setMensaje("badUser");
					return false;
				}
				
			}
			
			
			if ( isFoundUser && (!isActiveDirectoryConfigurated || isActiveDirectoryConfigurated && isValidActiveDirectory || isUserViewer || isUserAdmin ) ) {
				String clave = prop.getProperty("clave");
				Encryptor enc = new Encryptor();
				if (enc.isPasswordEqual(nameUser.getUser(), nameUser.getPass(), clave, request, isUserAdmin, isUserViewer)) {
					nameUser.setUser(prop.getProperty("nameUser"));
					nameUser.setNameUser(prop.getProperty("nameUser"));

					//validamos la licencia
					ModuloBean modulo = null;
					if (request != null && request.getSession() != null) {
						if (request.getSession().getAttribute("DEMO") != null && String.valueOf(request.getSession().getAttribute("DEMO")).equals("true")) {
							File fichero = new File(ToolsHTML.getPathKey());
							// if(fichero.exists()) {
							// fichero.delete();
							// }
							modulo = new ModuloBean();
						
						} else {
							modulo = ToolsHTML.validarLicencia();
						}
						request.getSession().setAttribute("LICENCIA", modulo);
						HandlerDBUser.limUser = Integer.parseInt(modulo.getUsuarios());
						HandlerDBUser.limUserViewer = Integer.parseInt(modulo.getViewers());
					}

					if (validUser && !userLogger(nameUser)) {
						// System.out.println(prop.getProperty("IdLanguage"));
						nameUser.setIdLanguaje(prop.getProperty("IdLanguage"));
						int valor = 0;
						try {
							if (!ToolsHTML.isEmptyOrNull(prop.getProperty("numRecordPages"))) {
								valor = Integer.parseInt(prop.getProperty("numRecordPages"));
							}
						} catch (Exception ex) {
							ex.printStackTrace();
						}
						if (valor > 0) {
							nameUser.setNumRecord(valor);
						} else {
							nameUser.setNumRecord(Integer.parseInt(DesigeConf.getProperty("application.numRecords")));
						}
						if (!ToolsHTML.isEmptyOrNull(prop.getProperty("namePerson"))) {
							nameUser.setNamePerson(prop.getProperty("namePerson"));
						}
						if (!ToolsHTML.isEmptyOrNull(prop.getProperty("email"))) {
							nameUser.setEmail(prop.getProperty("email"));
						}
						if (!ToolsHTML.isEmptyOrNull(prop.getProperty("clave"))) {
							nameUser.setClave(prop.getProperty("clave"));
						}
						nameUser.setIdGroup(prop.getProperty("idGrupo"));
						nameUser.setIdPerson(Long.parseLong(prop.getProperty("idPerson")));
						nameUser.setLastDatePass(null);
						if (!ToolsHTML.isEmptyOrNull(prop.getProperty("dateLastPass")) && !isNewUser) {
							nameUser.setLastDatePass(ToolsHTML.sdf.parse(prop.getProperty("dateLastPass")));
						}
						nameUser.setLastDatePassEdit(null);
						if (!ToolsHTML.isEmptyOrNull(prop.getProperty("dateLastPassEdit"))) {
							nameUser.setLastDatePassEdit(ToolsHTML.sdf.parse(prop.getProperty("dateLastPassEdit")));
						}
						nameUser.setEdit(0);
						try {
							if (!ToolsHTML.isEmptyOrNull(prop.getProperty("edit"))) {
								nameUser.setEdit(Integer.parseInt(prop.getProperty("edit")));
							}
						} catch (Exception ex) {
							ex.printStackTrace();
						}
						
						nameUser.setIdNodeService(ToolsHTML.parseInt(prop.getProperty("idNodeService")));
						nameUser.setModo(ToolsHTML.parseInt(prop.getProperty("modo")));
						nameUser.setLado(ToolsHTML.parseInt(prop.getProperty("lado")));
						nameUser.setPpp(ToolsHTML.parseInt(prop.getProperty("ppp")));
						nameUser.setPanel(ToolsHTML.parseInt(prop.getProperty("panel")));
						nameUser.setSeparar(ToolsHTML.parseInt(prop.getProperty("separar")));
						nameUser.setPagina(ToolsHTML.parseInt(prop.getProperty("pagina")));
						nameUser.setMinimo(ToolsHTML.parseInt(prop.getProperty("minimo")));
						nameUser.setTypeDocuments(ToolsHTML.parseInt(prop.getProperty("typedocuments")));
						nameUser.setOwnerTypeDoc(ToolsHTML.parseInt(prop.getProperty("ownerTypeDoc")));
						nameUser.setIdNodeDigital(ToolsHTML.parseInt(prop.getProperty("idNodeDigital")));
						nameUser.setTypesetter(ToolsHTML.parseInt(prop.getProperty("typesetter")));
						nameUser.setChecker(ToolsHTML.parseInt(prop.getProperty("checker")));
						nameUser.setLote(ToolsHTML.isEmptyOrNull(prop.getProperty("lote"), ""));
						nameUser.setCorrelativo(ToolsHTML.isEmptyOrNull(prop.getProperty("correlativo"), ""));
						nameUser.setJavaWebStart(ToolsHTML.isEmptyOrNull(prop.getProperty("javaWebStart"), ""));
						
						// Asignamos el cargo
						Cargo cargo = new Cargo();
						cargo.setIdcargo(ToolsHTML.parseInt(prop.getProperty("idCargo")));
						cargo.setCargo(prop.getProperty("cargo"));
						nameUser.setCargo(cargo);
						
						// Asignamos el area
						Area area = new Area();
						area.setIdarea(ToolsHTML.parseInt(prop.getProperty("idArea")));
						area.setArea(prop.getProperty("area"));
						nameUser.setArea(area);
						
						return true;
					} else {
						if (!validUser) {
							nameUser.setIdLanguaje(prop.getProperty("IdLanguage"));
							int valor = 0;
							try {
								if (!ToolsHTML.isEmptyOrNull(prop.getProperty("numRecordPages"))) {
									valor = Integer.parseInt(prop.getProperty("numRecordPages"));
								}
							} catch (Exception ex) {
								ex.printStackTrace();
							}
							if (valor > 0) {
								nameUser.setNumRecord(valor);
							} else {
								nameUser.setNumRecord(Integer.parseInt(DesigeConf.getProperty("application.numRecords")));
							}
							if (!ToolsHTML.isEmptyOrNull(prop.getProperty("namePerson"))) {
								nameUser.setNamePerson(prop.getProperty("namePerson"));
							}
							if (!ToolsHTML.isEmptyOrNull(prop.getProperty("email"))) {
								nameUser.setEmail(prop.getProperty("email"));
							}
							nameUser.setIdGroup(prop.getProperty("idGrupo"));
							nameUser.setIdPerson(Long.parseLong(prop.getProperty("idPerson")));
							return true;
						}
					}
				} else {
					if (login != null) {
						login.setClave("");
					}
					nameUser.setPass("");
					nameUser.setMensaje("badPass");
				}
			} else {
				if (login != null) {
					login.cleanForm();
				}
				nameUser.setMensaje("badUser");
			}
			

		}
		return false;
	}


	private synchronized static boolean userLogger(Users user) {
		if (userConnect == null) {
			userConnect = new Hashtable();
		}
		if (!userConnect.containsKey(user.getUser())) {
			if (limUser < 0 && !isCheck) {
				// Obtenemos la Clave
				if (keyGen == null) {
					try {
						keyGen = HandlerSeguridad.getKey();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				if (num == null) {
					num = Encryptor.decrypt(keyGen, license);
				}
				if (date == null) {
					date = Encryptor.decrypt(keyGen, endLicense);
				}

				if (limUser <= 0 && !isCheck) {
					isCheck = true;
					if (ToolsHTML.isNumeric(num)) {
						limUser = Integer.parseInt(num);
					} else {
						user.setMensaje("err.invalidLicense");
						return true;
					}
				}
				if (dateExpireLicense == null) {
					if (ToolsHTML.isNumeric(date)) {
						String year = date.substring(0, 4);
						String month = date.substring(4, 6);
						String day = date.substring(6, 8);
						dateExpireLicense = new java.util.GregorianCalendar(Integer.parseInt(year), Integer.parseInt(month), Integer.parseInt(day)).getTime();
					} else {
						user.setMensaje("err.invalidLicense");
						return true;
					}
				}
				if (!ToolsHTML.isDateValid(dateExpireLicense, true)) {
					user.setMensaje("err.licenseExpire");
					return true;
				}
			}

			// limUserViewer
			// contamos los usuarios viewer y los usuarios full
			int userFullCount = 0;
			int userViewerCount = 0;
			Iterator ite = userConnect.values().iterator();
			Users aux;
			while(ite.hasNext()){
				aux = (Users)ite.next();
				if(aux.getIdGroup().equals(Constants.ID_GROUP_VIEWER)) {
					userViewerCount++;
				} else {
					userFullCount++;
				}
			}

			ArrayList param = new ArrayList();
			StringBuffer sb = new StringBuffer("SELECT * FROM person WHERE accountactive='1' AND nameuser=? ");
			try {
				param.add(user.getUser());
				CachedRowSet crs = JDBCUtil.executeQuery(sb,param, Thread.currentThread().getStackTrace()[1].getMethodName());
				if(crs.next()) {
					user.setIdGroup(crs.getString("idGrupo"));
				}
			} catch (Exception e) {
				e.printStackTrace();
				user.setMensaje("err.userLogged");
				return true;
			}

			boolean isViewer = user.getIdGroup().equals(Constants.ID_GROUP_VIEWER);

			//if (userConnect.size() < limUser) {
			if ((!isViewer && userFullCount < limUser) || (isViewer && userViewerCount < limUserViewer) ) {
				//if (dateExpireLicense == null || !ToolsHTML.isDateValid(dateExpireLicense, true)) {
				//	user.setMensaje("err.licenseExpire");
				//	return true;
				//}
				userConnect.put(user.getUser().trim(), user);
				return false;
			} else {
				user.setMensaje("err.LimUser");
			}
		} else {
			user.setMensaje("err.userLogged");
		}
		return true;
	}

	public synchronized static void logoutUser(String user) {
		log.info("Intentando hacer logout virtual del usuario " + user);
		if (userConnect != null) {
			if (userConnect.containsKey(user)) {
				userConnect.remove(user);
				log.info("Realizado logout virtual del usuario " + user);
			}
		}
	}

	public synchronized static void checkUsersConnecteds(String time, String timeExecutionEdit) {
		if (userConnect != null) {
			Enumeration items = userConnect.keys();
			while (items.hasMoreElements()) {
				String key = (String) items.nextElement();
				Users user = (Users) userConnect.get(key);

				if (user!=null && user.getLastTime()!=null && time.compareTo(user.getLastTime()) > 0) {
					if (user != null && !user.isEspecialSession()) {
						log.debug("[checkUsersConnecteds] close session to User = " + key);
						userConnect.remove(key);
					} else {
						if (user == null) {
							log.debug("[checkUsersConnecteds] close session to User = " + key);
							Object obj = userConnect.remove(key);
							log.debug("obj: " + obj);
						}
					}
				}
			}
		}
	}

	public synchronized static void checkUsersConnecteds() {
		checkUsersConnecteds(null);
	}
	public synchronized static void checkUsersConnecteds(Connection con) {
		// El tiempo de session esta validado en checkUserInSession() del superaction
		// donde existe la formula 
		// getSession().setMaxInactiveInterval((sessionTimeout+sessionEditTimeout) * 60);
		// que dice el tiempo maximo que dura la session se suma el tiempo + la edicion 
		// y sera este metodo quien tome la decision de sacar el usuario antes de tiempo.
		
        java.util.Calendar calendario = new java.util.GregorianCalendar();
        String time = ToolsHTML.sdfShowConvert1.format(calendario.getTime());

        if (userConnect != null) {
			Enumeration items = userConnect.keys();

			int minSession = -5;
			int minSessionEdit = -5;
			try{
            	minSession = 1*HandlerParameters.PARAMETROS.getEndSession();
            	minSessionEdit = 1*HandlerParameters.PARAMETROS.getEndSessionEdit();
			}catch(Exception e){
				//System.out.println("Exception checkUsersConnecteds : " + e);
			}

			while (items.hasMoreElements()) {
				String key = (String) items.nextElement();
				Users user = (Users) userConnect.get(key);
				String sLastLogin = "";
				java.util.Calendar calendarioLogin= new java.util.GregorianCalendar();
				java.util.Date lastLogin = new java.util.Date();

				try{
					if(user.getLastLogin()!=null)
						lastLogin = ToolsHTML.sdfShowConvert1.parse(user.getLastLogin());
				}catch(Exception e){
					//System.out.println("Exception checkUsersConnecteds : " + e);
				}

				calendarioLogin.setTime(lastLogin);

				if(calendarioLogin!=null){
					calendarioLogin.add(calendarioLogin.MINUTE,minSession);
					sLastLogin = ToolsHTML.sdfShowConvert1.format(calendarioLogin.getTime());
				}

				if (user!=null && user.getLastLogin()!=null) {
					//if (user != null && !user.isEspecialSession()) {
					if (user != null) {
						//Se valida si el usuario esta editando y el tiempo de expiracion de edicion no ha culminado
						Users userEdit = (Users) getEditPerson(user.getIdPerson());
						boolean isHaceValidacionEdit = true;

						String sLastDatePassEdit = "";
						java.util.Date lastEdit = new java.util.Date();
						java.util.Calendar calendarioEdit = new java.util.GregorianCalendar();
						try{
							lastEdit = ToolsHTML.sdfShowConvert1.parse(userEdit.getEdit()==0?null:userEdit.getLastEdit());
						}catch(Exception e){
							//System.out.println("Exception checkUsersConnecteds : " + e);
							if(time.compareTo(sLastLogin) > 0 ) {
								// no esta editando, lo sacamos de session si se cumplio el tiempo de session
								Object obj = userConnect.remove(key);
							} 
							isHaceValidacionEdit = false;
						}
						
						if(isHaceValidacionEdit) {
							calendarioEdit.setTime(lastEdit);
							sLastDatePassEdit = ToolsHTML.sdfShowConvert1.format(calendarioEdit.getTime());

							if(calendarioEdit!=null){
								calendarioEdit.add(calendarioEdit.MINUTE,minSessionEdit+minSession); // sumamos minuto de edicion y de session
								sLastDatePassEdit = ToolsHTML.sdfShowConvert1.format(calendarioEdit.getTime());
							}
	
							if(userEdit!=null && userEdit.getEdit()==1 && time.compareTo(sLastDatePassEdit) < 0){
								//No se elimina usuario de sesion
								System.out.println("El usuario esta editando: " + user.getIdPerson());
							}else{
								//Se vuelve a colocar edit en 0 en la tabla Person
								System.out.println("El usuario no esta editando - lo sacamos de session: " + user.getIdPerson());
								log.debug("[checkUsersConnecteds] close session to User = " + key);
								actualizaEditPerson(user.getIdPerson(),0);
								//System.out.println("close session USR: " + user.getIdPerson());
								userConnect.remove(key);
							}
						}
					} else {
						if (user == null) {
							log.debug("[checkUsersConnecteds] close session to User = " + key);
							Object obj = userConnect.remove(key);
							log.debug("obj: " + obj);
						}
					}
				}
			}
		}
	}

	// SIMON 15 DE JULIO 2005 INICIO
	public synchronized static void getEraseUserConnect(String[] userSession) throws Exception {
		if (userConnect != null) {
			if (userSession != null && userSession.length > 0) {
				for (int row = 0; row < userSession.length; row++) {
					String nameUser = userSession[row];
					if (!ToolsHTML.isEmptyOrNull(nameUser)) {
						HandlerDBUser.logoutUser(nameUser.trim());
					}
				}
			}
			// Enumeration items = userConnect.keys();
			// while (items.hasMoreElements()) {
			// String key = (String)items.nextElement();
			// Users user = (Users) userConnect.get (key);
			// for(int i=0;i<userSession.length;i++) {
			// if ((!ToolsHTML.isEmptyOrNull(userSession[i]))&&(!ToolsHTML.isEmptyOrNull(user.getUser()))&&(user.getUser().equalsIgnoreCase(userSession[i]))){
			// HandlerDBUser.logoutUser(key);
			// user.setUser(null);
			// }
			// }
			// }
		}
	}

	public synchronized static Collection getAllUserConnect() throws Exception {
		ArrayList resp = new ArrayList();
		if (userConnect != null) {
			Enumeration items = userConnect.keys();
			while (items.hasMoreElements()) {
				String key = (String) items.nextElement();
				Users user = (Users) userConnect.get(key);
				Search bean = new Search(key, user.getNamePerson());
				bean.setAditionalInfo(user.getIdGroup()!=null && user.getIdGroup().equals(Constants.ID_GROUP_VIEWER)?"Viewer":"");
				resp.add(bean);
				/*
				 * if (time.compareTo(user.getLastTime()) > 0) { userConnect.remove(key); log.debug("userConnect = " + userConnect); }
				 */
			}
		}
		return resp;
	}

	// SIMON 15 DE JULIO 2005 FIN
	public synchronized static boolean updateUser(Users user) {
		if (userConnect != null && user != null) {
			if (userConnect.containsKey(user.getUser().trim())) {
				userConnect.put(user.getUser().trim(), user);
				return true;
			}
		}
		return false;
	}

	public static void load(LoginUser forma, boolean active) throws Exception {
		StringBuffer sql = new StringBuffer("SELECT * FROM ").append(nameUser);
		sql.append(" WHERE nameUser = '").append(forma.getUser()).append("' AND accountActive = ");
		sql.append(active ? "'1'" : "'0'");
		Properties prop = JDBCUtil.doQueryOneRow(sql.toString(),Thread.currentThread().getStackTrace()[1].getMethodName());
		if ((prop != null) && prop.getProperty("isEmpty").equalsIgnoreCase("false")) {
			forma.setIdPerson(Long.parseLong(prop.getProperty("idPerson")));
			forma.setNombres(prop.getProperty("Nombres"));
			forma.setApellidos(prop.getProperty("Apellidos"));
			forma.setEmail(prop.getProperty("email"));
			forma.setArea(prop.getProperty("idarea"));
			forma.setGrupo(prop.getProperty("idGrupo"));
			forma.setClave(prop.getProperty("clave"));
			forma.setRepclave(prop.getProperty("clave"));
			// modif por simon 02/05/2005
			forma.setCargo(getCargoAndArea(prop.getProperty("cargo")));
		}
	}

	public static void load(LoginUser forma, boolean active, boolean cargoID) throws Exception {
		StringBuffer sql = new StringBuffer("SELECT * FROM ").append(nameUser);
		sql.append(" WHERE nameUser = '").append(forma.getUser()).append("' AND accountActive = ");
		sql.append(active ? "'1'" : "'0'");
		Properties prop = JDBCUtil.doQueryOneRow(sql.toString(),Thread.currentThread().getStackTrace()[1].getMethodName());
		if ((prop != null) && prop.getProperty("isEmpty").equalsIgnoreCase("false")) {
			forma.setIdPerson(Long.parseLong(prop.getProperty("idPerson")));
			forma.setNombres(prop.getProperty("Nombres"));
			forma.setApellidos(prop.getProperty("Apellidos"));
			forma.setEmail(prop.getProperty("email"));
			forma.setArea(prop.getProperty("idarea"));
			forma.setGrupo(prop.getProperty("idGrupo"));
			forma.setClave(prop.getProperty("clave"));
			forma.setRepclave(prop.getProperty("clave"));
			if (cargoID) {
				forma.setCargo(prop.getProperty("cargo"));
			} else {
				forma.setCargo(getCargoAndArea(prop.getProperty("cargo")));
			}
			forma.setIdNodeService(Integer.parseInt(prop.getProperty("idNodeService")));
		}
	}
	public static Users load(long idPerson, boolean active) throws Exception {
		StringBuffer sql = new StringBuffer("SELECT u.nameUser,u.idGrupo,u.javaWebStart,u.typedocuments,u.ownertypedoc,u.typesetter,u.checker,u.idnodedigital,u.lote, u.email, ");
		sql.append("c.idCargo, c.cargo, c.idArea, a.area, ");
		switch (Constants.MANEJADOR_ACTUAL) {
		case Constants.MANEJADOR_MSSQL:
			sql.append("(u.Apellidos + ' ' + u.Nombres) AS namePerson ");
			break;
		case Constants.MANEJADOR_POSTGRES:
			sql.append("(u.Apellidos || ' ' || u.Nombres) AS namePerson ");
			break;
		case Constants.MANEJADOR_MYSQL:
			sql.append("CONCAT(u.Apellidos , ' ' , u.Nombres) AS namePerson ");
			break;
		}
		sql.append(" FROM ").append(nameUser).append(" u, tbl_cargo c, tbl_area a");
		sql.append(" WHERE CAST(u.cargo AS INT)=c.idCargo ");
		sql.append(" AND c.idArea=a.idArea ");
		sql.append(" AND u.idPerson = ").append(idPerson);
		sql.append(" AND u.accountActive = ").append(active ? "'1'" : "'0'");
		
		
		Properties prop = JDBCUtil.doQueryOneRow(sql.toString(),Thread.currentThread().getStackTrace()[1].getMethodName());
		Users usr = new Users();

		if ((prop != null) && prop.getProperty("isEmpty").equalsIgnoreCase("false")) {
			// usr.setIdPerson(Long.parseLong(prop.getProperty("idPerson")));
			usr.setIdPerson(idPerson);
			usr.setEmail(prop.getProperty("email"));
			usr.setUser(prop.getProperty("nameUser").trim());
			usr.setNameUser(prop.getProperty("nameUser").trim());
			usr.setIdGroup(prop.getProperty("idGrupo").trim());
			usr.setNamePerson(prop.getProperty("namePerson"));
			usr.setJavaWebStart(prop.getProperty("javaWebStart"));
			usr.setTypeDocuments(prop.getProperty("typedocuments") == null || prop.getProperty("typedocuments").equals("") ? 0 : Integer.parseInt(prop.getProperty("typedocuments")));
			usr.setOwnerTypeDoc(prop.getProperty("ownertypedoc") == null || prop.getProperty("ownertypedoc").equals("") ? 0 : Integer.parseInt(prop.getProperty("ownertypedoc")));
			usr.setTypesetter(prop.getProperty("typesetter") == null  || prop.getProperty("typesetter").equals("") ? 0 : Integer.parseInt(prop.getProperty("typesetter")));
			usr.setChecker(prop.getProperty("checker") == null  || prop.getProperty("checker").equals("") ? 0 : Integer.parseInt(prop.getProperty("checker")));
			usr.setIdNodeDigital(prop.getProperty("idnodedigital") == null || prop.getProperty("idnodedigital").equals("") ? 0 : Integer.parseInt(prop.getProperty("idnodedigital")));
			usr.setLote(prop.getProperty("lote") == null ? "" : prop.getProperty("lote"));
			
			// Asignamos el cargo
			Cargo cargo = new Cargo();
			cargo.setIdcargo(ToolsHTML.parseInt(prop.getProperty("idCargo")));
			cargo.setCargo(prop.getProperty("cargo"));
			usr.setCargo(cargo);
			
			// Asignamos el area
			Area area = new Area();
			area.setIdarea(ToolsHTML.parseInt(prop.getProperty("idArea")));
			area.setArea(prop.getProperty("area"));
			usr.setArea(area);
			
		}

		return usr;
	}

	public synchronized static boolean edit(LoginUser forma, HttpSession sessionn,
			Users usuario) throws ApplicationExceptionChecked {
		PreparedStatement st = null;
		Connection con = null;
		boolean result = false;
		try {
			StringBuffer check = new StringBuffer("SELECT nameUser FROM ").append(nameUser);
			check.append(" WHERE accountActive = '").append(Constants.permission).append("'");
			check.append(" AND  email='").append(forma.getEmail()).append("'");
			check.append(" AND  nameUser !='").append(forma.getUser()).append("'");

			log.debug("check=" + check.toString());
			con = JDBCUtil.getConnection(Thread.currentThread().getStackTrace()[1].getMethodName());
			st = con.prepareStatement(JDBCUtil.replaceCastMysql(check.toString()));
			ResultSet rs = st.executeQuery();
			if (rs.next()) {
				throw new ApplicationExceptionChecked("E0038");
			}
			rs.close();
			/*
			 * Nos preguntamos si el usuario cambio el cargo,en tal caso lo remplazamos por el usuario escojido con el cargo que tenia el usuario que va a ser modificado.
			 */
			con.setAutoCommit(false);

			if (forma.getCambioCargo()) {
				HandlerDBUser.addMessageToActivityProgress("Iniciando cambio de cargo...");
				log.debug("SE CAMBIO EL CARGO!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
				boolean eliminar = false;
				actualizaOwnerInWebDocuments(con, st, forma, eliminar, sessionn, usuario);
				HandlerDBUser.addMessageToActivityProgress("Cambio de cargo realizado...");
			}
			StringBuffer edit = new StringBuffer("UPDATE ").append(nameUser).append(" SET Nombres = ?");
			boolean changePass = false;
			if (!forma.getClave().equalsIgnoreCase(forma.getRepclave())) {
				edit.append(",clave = ? ");
				changePass = true;
			}

			edit.append(",apellidos = ?");
			edit.append(",email = ? ");
			edit.append(",idGrupo = ? ");
			edit.append(",cargo = ? ");
			edit.append(",idarea = ? ");
			edit.append(",idNodeService = ? ");
			edit.append(" WHERE nameUser = ? ");
			st = con.prepareStatement(JDBCUtil.replaceCastMysql(edit.toString()));
			st.setString(1, forma.getNombres());
			int pos = 2;
			if (changePass) {
				//System.out.println(forma.getClave());
				//System.out.println(ToolsHTML.encripterPass(forma.getClave()));
				st.setString(pos++, ToolsHTML.encripterPass(forma.getClave()));
			}
			st.setString(pos++, forma.getApellidos());
			st.setString(pos++, forma.getEmail());
			st.setInt(pos++, Integer.parseInt(forma.getGrupo()));
			st.setString(pos++, forma.getCargo());
			st.setInt(pos++, Integer.parseInt(forma.getArea()));
			st.setInt(pos++, forma.getIdNodeService());
			st.setString(pos++, forma.getUser());
			result = st.executeUpdate() > 0;
		} catch (ApplicationExceptionChecked ae) {
			throw ae;
		} catch (Exception e) {
			setMensaje(e.getMessage());
			e.printStackTrace();
			result = false;
		} finally {
			setFinally(con, st);
		}
		// TODO log de la Modificaci'on del Usuario

		return result;
	}

	// public synchronized static boolean edit1(LoginUser forma){
	// StringBuffer edit1 = new StringBuffer("UPDATE ").append(nameUser).append(" SET clave='");
	// edit1.append(forma.getClave()).append("'");
	// edit1.append(" WHERE nameUser = ").append(forma.getUser());
	// try {
	// return JDBCUtil.doUpdate(edit1.toString())>0;
	// } catch (Exception e) {
	// setMensaje(e.getMessage());
	// e.printStackTrace();
	// }
	// return false;
	// }

	public static Collection getAllSecurityForUsersAndIdNode(String idNode) throws Exception {
		PermissionUserForm forma = null;
		Vector result = new Vector();
		StringBuffer sql = new StringBuffer(100);
		sql.append("SELECT pu.*,p.nameUser FROM permissionstructuser pu,person p WHERE idStruct = ").append(idNode);
		sql.append(" AND p.idPerson = pu.idPerson");
		sql.append(" AND active = '").append(Constants.permission).append("'");
		log.debug("[getAllSecurityForUsersAndIdNode] = " + sql.toString());
		Vector datos = JDBCUtil.doQueryVector(sql.toString(),Thread.currentThread().getStackTrace()[1].getMethodName());
		for (int row = 0; row < datos.size(); row++) {
			Properties properties = (Properties) datos.elementAt(row);
			forma = new PermissionUserForm();
			forma.setIdStruct(idNode);
			forma.setIdPerson(Long.parseLong(properties.getProperty("idPerson")));
			forma.setIdUser(properties.getProperty("nameUser"));
			setSecurityInNode(properties, forma);
			result.add(forma);
		}
		return result;
	}

	/**
	 * Este m�todo permite Cargar la seguridad del usuario dado para la localidad indicada
	 *
	 * @param forma
	 * @throws Exception
	 */
	public static void loadSecurityStructUser(PermissionUserForm forma) throws Exception {
		StringBuffer sql = new StringBuffer(100);
		sql.append("SELECT * FROM permissionstructuser WHERE idStruct = ").append(forma.getIdStruct());
		sql.append(" AND idPerson = ").append(forma.getIdPerson());
		sql.append(" AND active = '").append(Constants.permission).append("'");
		Properties prop = JDBCUtil.doQueryOneRow(sql.toString(),Thread.currentThread().getStackTrace()[1].getMethodName());
		log.debug("[loadSecurityStructUser] = " + sql.toString());
		if (prop.getProperty("isEmpty").equalsIgnoreCase("false")) {
			setSecurityInNode(prop, forma);
		} else {
			HandlerGrupo.loadSecurityStructGroup(forma);
			// setEmptySecurity(forma);
		}
	}

	/**
	 * Este m�todo permite Cargar la seguridad del usuario dado para la localidad indicada
	 *
	 * @param forma
	 * @throws Exception
	 */
	public static void loadSecurityStructUserRecord(PermissionUserForm forma) throws Exception {
		StringBuffer sql = new StringBuffer(100);
		sql.append("SELECT * FROM permissionrecorduser ");
		sql.append(" WHERE idUser =  (select nameUser from person where idPerson=").append(forma.getIdPerson()).append(")");
		Properties prop = JDBCUtil.doQueryOneRow(sql.toString(),Thread.currentThread().getStackTrace()[1].getMethodName());
		log.debug("[loadSecurityStructUserRecord] = " + sql.toString());
		if (prop.getProperty("isEmpty").equalsIgnoreCase("false")) {
			setSecurityInRecord(prop, forma);
		} else {
			HandlerGrupo.loadSecurityStructGroupRecord(forma);
			// setEmptySecurity(forma);
		}
	}

	/**
	 * Este m�todo permite Cargar la seguridad del usuario dado para la localidad indicada
	 *
	 * @param forma
	 * @throws Exception
	 */
	public static void loadSecurityStructUserFiles(PermissionUserForm forma) throws Exception {
		StringBuffer sql = new StringBuffer(100);
		sql.append("SELECT * FROM permissionfilesuser ");
		sql.append(" WHERE idUser =  (select nameUser from person where idPerson=").append(forma.getIdPerson()).append(")");
		Properties prop = JDBCUtil.doQueryOneRow(sql.toString(),Thread.currentThread().getStackTrace()[1].getMethodName());
		log.debug("[loadSecurityStructUserFiles] = " + sql.toString());
		if (prop.getProperty("isEmpty").equalsIgnoreCase("false")) {
			setSecurityInFiles(prop, forma);
		} else {
			HandlerGrupo.loadSecurityStructGroupFiles(forma);
			// setEmptySecurity(forma);
		}
	}

	public static void getAllSecurityForUser(long idUser, Hashtable security, StringBuffer ids) throws Exception {
		StringBuffer sql = new StringBuffer(100);
		StringBuffer sql2 = new StringBuffer(100);
		// La Primera Parte Carga los Nodos en donde el Usuario tiene Permiso
		PermissionUserForm forma = null;
		sql2.append("SELECT idnodeparent FROM struct where ");
		sql2.append(" idnode IN (").append(ids.toString()).append(")");
		Vector datos1 = JDBCUtil.doQueryVector(sql2.toString(),Thread.currentThread().getStackTrace()[1].getMethodName());
		for (int row = 0; row < datos1.size(); row++) {

		}
		
		//ydavila jairo 18022016 no debe validar esto en esta primera vuelta
		sql.append("SELECT * FROM permissionstructuser WHERE ");
		sql.append(" idPerson = ").append(idUser);
		sql.append(" AND active = '").append(Constants.permission).append("'");
		sql.append(" AND toView = '").append(Constants.permission).append("'");
		
		/*jairo 18022016 este fue el quwery que hizo Jairo pero se pierden los permisos heredados
		sql.append("SELECT a.* FROM permissionstructuser a, struct b WHERE ");
		sql.append("a.idStruct=b.IdNode AND ");
		sql.append(" a.idPerson = ").append(idUser);
		sql.append(" AND a.active = '").append(Constants.permission).append("'");
		sql.append(" AND a.toView = '").append(Constants.permission).append("'");
		sql.append(" AND b.IdNode IN (").append(ids.toString()).append(")");
		*/
		
		// La segunda Parte Filtra los Nodos Obtenidos del Grupo
		// En caso de que el usuario haya sido Bloqueado a alguno en espec�fico
		// esta parte lo Filtrar� :D
		sql.append(" UNION SELECT * FROM permissionstructuser");
		sql.append(" WHERE idPerson = ").append(idUser);
		sql.append(" AND active = '").append(Constants.permission).append("'");
		sql.append(" AND idStruct IN (").append(ids.toString()).append(")");
		sql.append(" ORDER BY idStruct ");
		Vector datos = JDBCUtil.doQueryVector(sql.toString(),Thread.currentThread().getStackTrace()[1].getMethodName());
		
		for (int row = 0; row < datos.size(); row++) {
			Properties properties = (Properties) datos.elementAt(row);
			forma = new PermissionUserForm(properties.getProperty("idStruct"));
			setSecurityInNode(properties, forma);
			security.put(forma.getIdStruct(), forma);
			ids.append(",").append(forma.getIdStruct());
		}
	}

	public static synchronized boolean deleteSecurityDocGroup(PermissionUserForm forma) throws Exception {
		StringBuilder sql = new StringBuilder(1024).append("DELETE FROM permisiondocgroup WHERE idDocument = ").append(forma.getIdDocument()).append(" AND ")
				.append(JDBCUtil.getCastAsIntString("idGroup")).append(" = ").append(JDBCUtil.getCastAsIntString(String.valueOf(forma.getIdGroup())));
		/* sql.append(" AND active = ").append(Constants.permission); */
		log.debug("[deleteSecurityDocGroup] = " + sql.toString());
		return JDBCUtil.doUpdate(sql.toString()) > 0;
	}

	public static synchronized boolean deleteSecurityRecordGroup(PermissionUserForm forma) throws Exception {
		StringBuilder sql = new StringBuilder(1024).append("DELETE FROM permissionrecordgroup ").append("WHERE ")
				.append(JDBCUtil.getCastAsIntString("idGroup")).append(" = ").append(JDBCUtil.getCastAsIntString(String.valueOf(forma.getIdGroup())));
		log.debug("[deleteSecurityRecordGroup] = " + sql.toString());
		return JDBCUtil.doUpdate(sql.toString()) > 0;
	}

	public static synchronized boolean deleteSecurityFilesGroup(PermissionUserForm forma) throws Exception {
		StringBuilder sql = new StringBuilder(1024).append("DELETE FROM permissionfilesgroup ").append("WHERE ").append(JDBCUtil.getCastAsIntString("idGroup"))
				.append(" = ").append(JDBCUtil.getCastAsIntString(String.valueOf(forma.getIdGroup())));
		log.debug("[deleteSecurityFilesGroup] = " + sql.toString());
		return JDBCUtil.doUpdate(sql.toString()) > 0;
	}

	public static synchronized boolean deleteSecurityDocUser(PermissionUserForm forma) throws Exception {
		StringBuffer sql = new StringBuffer(60);
		sql.append("DELETE FROM permisiondocuser WHERE idDocument = ").append(forma.getIdDocument());
		sql.append(" AND idUser = (select nameuser from person where idperson=").append(forma.getIdPerson()).append(")");
		/* sql.append(" AND active = ").append(Constants.permission); */
		return JDBCUtil.doUpdate(sql.toString()) > 0;
	}

	public static synchronized boolean deleteSecurityRecordUser(PermissionUserForm forma) throws Exception {
		StringBuffer sql = new StringBuffer(60);
		sql.append("DELETE FROM permissionrecorduser ");
		sql.append("WHERE idUser = (select nameuser from person where idperson=").append(forma.getIdPerson()).append(")");
		return JDBCUtil.doUpdate(sql.toString()) > 0;
	}

	public static synchronized boolean deleteSecurityFilesUser(PermissionUserForm forma) throws Exception {
		StringBuffer sql = new StringBuffer(60);
		sql.append("DELETE FROM permissionfilesuser ");
		sql.append("WHERE idUser = (select nameuser from person where idperson=").append(forma.getIdPerson()).append(")");
		return JDBCUtil.doUpdate(sql.toString()) > 0;
	}

	// public static synchronized boolean deleteSecurityStructUser(PermissionUserForm forma) throws Exception {
	// StringBuffer sql = new StringBuffer(60);
	// sql.append("DELETE FROM permissionstructuser WHERE idStruct = ").append(forma.getIdStruct());
	// sql.append(" AND idPerson = ").append(forma.getIdPerson());
	// sql.append(" AND active = ").append(Constants.permission);
	// log.debug("[deleteSecurityStructUser] = " + sql.toString());
	// return JDBCUtil.doUpdate(sql.toString()) > 0;
	// }

	private static String getInsertSecurity(String idStruct, PermissionUserForm forma) {
		StringBuffer update = new StringBuffer(100);
		update.append("INSERT INTO permissionstructuser (idStruct,idPerson,toView,toRead,toAddFolder,toAddProcess,toDelete,toEdit,");
		update.append("toMove,toAddDocument,toAdmon,toViewDocs,toEditDocs,toAdmonDocs,toDelDocs");
		update.append(",toReview,toApproved,toMoveDocs,toCheckOut,toEditRegister,toImpresion,");
		update.append("toCheckTodos,toDoFlows,docinLine,toFlexFlow,toChangeUsr,toCompleteFlow,");
		update.append("permisoModificado,toPublicEraser,toDownload) VALUES(");
		update.append(idStruct).append(",").append(forma.getIdPerson()).append(",'");
		update.append(forma.getToView()).append("','");
		update.append(forma.getToRead()).append("','").append(forma.getToAddFolder()).append("','");
		update.append(forma.getToAddProcess()).append("','").append(forma.getToDelete()).append("','");
		update.append(forma.getToEdit()).append("','").append(forma.getToMove()).append("','");
		update.append(forma.getToAddDocument()).append("','").append(forma.getToAdmon()).append("','");
		update.append(forma.getToViewDocs()).append("','").append(forma.getToEditDocs()).append("','");
		update.append(forma.getToAdmonDocs()).append("','").append(forma.getToDelDocs()).append("','");
		update.append(forma.getToReview()).append("','").append(forma.getToAprove()).append("','");
		update.append(forma.getToMoveDocs()).append("','").append(forma.getCheckOut()).append("','").append(forma.getToEditRegister()).append("','");
		update.append(forma.getToImpresion()).append("','").append(forma.getToCheckTodos()).append("','");
		update.append(forma.getToDoFlows()).append("','").append(forma.getToDocinLine()).append("','");
		update.append(forma.getToFlexFlow()).append("','").append(forma.getToChangeUsr()).append("','");
		update.append(forma.getToCompleteFlow()).append("','");
		update.append(forma.getPermisoModificado()).append("','");
		update.append(forma.getToPublicEraser()).append("','");
		update.append(forma.getToDownload()).append("')");
		return update.toString();
	}

	public static void checkExistSecurityDeleteDocsGrup(String idStruct, long idGrupo, PermissionUserForm forma, PreparedStatement st, Connection con) throws Exception {
		String numgen = null;
		PreparedStatement select = null;
		StringBuffer sql = new StringBuffer("SELECT d.numgen FROM documents d,");
		sql.append(" permissionstructgroups psu ");
		sql.append(" WHERE psu.idStruct = d.idNode  ");
		sql.append("   AND psu.idstruct = ").append(idStruct);
		sql.append("   AND psu.idGroup=").append(forma.getIdGroup());

		log.debug("[checkExistSecurityDocsGrup]" + sql);
		select = con.prepareStatement(JDBCUtil.replaceCastMysql(sql.toString()));
		ResultSet res = select.executeQuery();

		while (res.next()) {
			numgen = res.getString("numgen");
			if (!ToolsHTML.isEmptyOrNull(numgen)) {
				forma.setIdDocument(numgen);
				HandlerDocuments.deleteSecurityDocumentGroup(forma);
			}
		}
		res.close();
	}

	// 23 de agosto 2005 inicio
	// hereda la permisologia de las carpetas en los documentos por grupo
	public static void checkExistSecurityDocsGrup(String idStruct, long idGrupo, PermissionUserForm forma, PreparedStatement st, Connection con) throws Exception {
		String numgen = null;
		PreparedStatement select = null;
		StringBuffer sql = new StringBuffer("SELECT d.numgen FROM documents d,");
		sql.append(" permissionstructgroups psu ");
		sql.append(" WHERE psu.idStruct = d.idNode AND ");
		sql.append(" psu.idGroup = ").append(forma.getIdGroup());
		sql.append(" AND psu.idstruct = ").append(idStruct);
		String numgenSw = "";

		log.debug("[checkExistSecurityDocsGrup]" + sql);
		select = con.prepareStatement(JDBCUtil.replaceCastMysql(sql.toString()));
		ResultSet res = select.executeQuery();
		while (res.next()) {
			numgen = res.getString("numgen");
			if (!ToolsHTML.isEmptyOrNull(numgen)) {
				forma.setIdDocument(numgen);
				if (!numgenSw.equalsIgnoreCase(numgen)) {
					numgenSw = numgen;
					// HandlerDocuments.updateSecurityDocumentGroup(forma, true);
				}
			}
		}
		res.close();
	}

	// 02 de septiembre 2005 inicio
	// hereda la permisologia de las carpetas en los documentos por usuarios
	public static void checkExistSecurityDeleteDocs(String idStruct, long idPerson, PermissionUserForm forma, PreparedStatement select, Connection con) throws Exception {
		String numgen = null;
		// PreparedStatement select = null;

		StringBuffer sql = new StringBuffer("select distinct d.numgen,p.nameuser from documents d,");
		sql.append(" permissionstructuser psu,");
		sql.append(" person p ");
		sql.append(" where psu.idStruct=d.idNode  ");
		// sql.append(" and p.nameUser=d.owner and psu.idstruct=").append(idStruct);
		sql.append(" and psu.idstruct=").append(idStruct);
		sql.append(" and p.idperson=").append(forma.getIdPerson());
		select = con.prepareStatement(JDBCUtil.replaceCastMysql(sql.toString()));
		ResultSet res = select.executeQuery();
		log.debug("[checkExistSecurity]" + sql);
		while (res.next()) {
			forma.setIdUser(res.getString("nameuser"));
			numgen = res.getString("numgen");
			if (!ToolsHTML.isEmptyOrNull(numgen)) {
				forma.setIdDocument(numgen);
				HandlerDocuments.deleteSecurityDocumentUser(forma);
			}
		}
		res.close();
	}

	// 02 de septiembre 2005 fin

	// hereda la permisologia de las carpetas en los documentos por usuarios
	public static void checkExistSecurityDocs(String idStruct, long idPerson, PermissionUserForm forma, PreparedStatement select, Connection con) throws Exception {
		String numgen = null;
		StringBuffer sql = new StringBuffer("select distinct(d.numgen) from documents d,");
		sql.append(" permissionstructuser psu");
		// sql.append(" person p ");
		sql.append(" where psu.idStruct=d.idNode and ");
		sql.append(" psu.idstruct=").append(idStruct);
		log.debug("[checkExistSecurityDocs] sql = " + sql);
		select = con.prepareStatement(JDBCUtil.replaceCastMysql(sql.toString()));
		ResultSet res = select.executeQuery();
		log.debug("[checkExistSecurity]" + sql);
		String idDocumentSw = "";
		while (res.next()) {
			numgen = res.getString("numgen");
			if (!ToolsHTML.isEmptyOrNull(numgen)) {
				forma.setIdDocument(numgen);
				if (!idDocumentSw.equalsIgnoreCase(forma.getIdDocument())) {
					HandlerDocuments.updateSecurityDocumentUser(forma, true);
					idDocumentSw = forma.getIdDocument();
				}
			}
		}
		res.close();
	}

	private static String getQueryDeleteSecurity(PermissionUserForm forma, String idStruct) {
		StringBuffer update = new StringBuffer(60);
		update.append("DELETE FROM permissionstructuser ");
		update.append(" WHERE idStruct = ").append(idStruct);
		update.append(" AND idPerson = ").append(forma.getIdPerson());
		update.append(" AND active = '").append(Constants.permission).append("'");
		return update.toString();
	}


	private static String getQueryUpdateSecurity(PermissionUserForm forma, String idStruct) {
		StringBuffer update = new StringBuffer(60);
		update.append("UPDATE permissionstructuser SET toRead = '").append(forma.getToRead()).append("'");
		update.append(",toView = '").append(forma.getToView()).append("'");
		update.append(",toAddFolder = '").append(forma.getToAddFolder()).append("'");
		update.append(",toAddProcess = '").append(forma.getToAddProcess()).append("'");
		update.append(",toDelete = '").append(forma.getToDelete()).append("'");
		update.append(",toEdit = '").append(forma.getToEdit()).append("'");
		update.append(",toMove = '").append(forma.getToMove()).append("'");
		update.append(",toAddDocument = '").append(forma.getToAddDocument()).append("'");
		update.append(",toAdmon = '").append(forma.getToAdmon()).append("'");

		update.append(",toViewDocs = '").append(forma.getToViewDocs()).append("'");
		update.append(",toEditDocs = '").append(forma.getToEditDocs()).append("'");
		update.append(",toAdmonDocs = '").append(forma.getToAdmonDocs()).append("'");
		update.append(",toDelDocs = '").append(forma.getToDelDocs()).append("'");
		update.append(",toReview = '").append(forma.getToReview()).append("'");
		update.append(",toApproved = '").append(forma.getToAprove()).append("'");
		update.append(",toMoveDocs = '").append(forma.getToMoveDocs()).append("'");
		update.append(",toCheckOut = '").append(forma.getCheckOut()).append("'");

		update.append(",toEditRegister = '").append(forma.getToEditRegister()).append("'");
		update.append(",toImpresion = '").append(forma.getToImpresion()).append("'");
		update.append(",toCheckTodos = '").append(forma.getToCheckTodos()).append("'");
		update.append(",toDoFlows = '").append(forma.getToDoFlows()).append("'");
		update.append(",docinLine = '").append(forma.getToDocinLine()).append("'");
		update.append(",toFlexFlow = '").append(forma.getToFlexFlow()).append("'");
		update.append(",toChangeUsr = '").append(forma.getToChangeUsr()).append("'");
		update.append(",toCompleteFlow = '").append(forma.getToCompleteFlow()).append("'");
		update.append(",permisoModificado = '").append(forma.getPermisoModificado()).append("'");
		update.append(",toPublicEraser = '").append(forma.getToPublicEraser()).append("'");
		update.append(",toDownload = '").append(forma.getToDownload()).append("'");

		update.append(" WHERE idStruct = ").append(idStruct);
		update.append(" AND idPerson = ").append(forma.getIdPerson());
		update.append(" AND active = '").append(Constants.permission).append("'");
		log.debug(update.toString());
		return update.toString();
	}

	private static void updateAudit(PermissionUserForm forma, Connection conn, String usr, String oper) throws Exception {
		StringBuffer query = new StringBuffer(50);
		query.append("INSERT INTO audpermstructuser (Aud_idStruct,Aud_idPerson,Aud_toView,Aud_toRead,Aud_toAddFolder,Aud_toAddProcess,");
		query.append("Aud_toDelete,Aud_toMove,Aud_toEdit,Aud_toAddDocument,Aud_toAdmon,Aud_toViewDocs,Aud_toEditDocs,");
		query.append("Aud_toAdmonDocs,Aud_toDelDocs,Aud_toReview,Aud_toApproved,Aud_toMoveDocs,Aud_toCheckOut,");
		query.append("Aud_toEditRegister,Aud_toImpresion,Aud_toCheckTodos,Aud_toDoFlows,Aud_docInline,Aud_User,");
		if (Constants.MANEJADOR_ACTUAL == Constants.MANEJADOR_POSTGRES || Constants.MANEJADOR_ACTUAL == Constants.MANEJADOR_MYSQL) {
			query.append("Aud_Fecha, ");
		}
		query.append("Aud_Evento) VALUES (?,?,");
		query.append("CAST(? as bit),CAST(? as bit),CAST(? as bit),CAST(? as bit),CAST(? as bit),CAST(? as bit),CAST(? as bit),");
		query.append("CAST(? as bit),CAST(? as bit),CAST(? as bit),CAST(? as bit),CAST(? as bit),CAST(? as bit),CAST(? as bit),");
		query.append("CAST(? as bit),CAST(? as bit),CAST(? as bit),CAST(? as bit),CAST(? as bit),CAST(? as bit),CAST(? as bit),");
		query.append("CAST(? as bit),");
		query.append("? ");
		if (Constants.MANEJADOR_ACTUAL == Constants.MANEJADOR_POSTGRES || Constants.MANEJADOR_ACTUAL == Constants.MANEJADOR_MYSQL) {
			query.append(",? ");
		}
		query.append(",?) ");

		PreparedStatement st = conn.prepareStatement(JDBCUtil.replaceCastMysql(query.toString()));
		st.setLong(1, Long.parseLong(forma.getIdStruct()));
		st.setLong(2, forma.getIdPerson());
		st.setInt(3, forma.getToView());
		st.setInt(4, forma.getToRead());
		st.setInt(5, forma.getToAddFolder());
		st.setInt(6, forma.getToAddProcess());
		st.setInt(7, forma.getToDelete());
		st.setInt(8, forma.getToMove());
		st.setInt(9, forma.getToEdit());
		st.setInt(10, forma.getToAddDocument());
		st.setInt(11, forma.getToAdmon());
		st.setInt(12, forma.getToViewDocs());
		st.setInt(13, forma.getToEditDocs());
		st.setInt(14, forma.getToAdmonDocs());
		st.setInt(15, forma.getToDelDocs());
		st.setInt(16, forma.getToReview());
		st.setInt(17, forma.getToAprove());
		st.setInt(18, forma.getToMoveDocs());
		st.setInt(19, forma.getCheckOut());
		st.setInt(20, forma.getToEditRegister());
		st.setInt(21, forma.getToImpresion());
		st.setInt(22, forma.getToCheckTodos());
		st.setInt(23, forma.getToDoFlows());
		st.setInt(24, forma.getToDocinLine());
		st.setString(25, usr);
		switch (Constants.MANEJADOR_ACTUAL) {
		case Constants.MANEJADOR_MSSQL:
			st.setString(26, oper);
			break;
		case Constants.MANEJADOR_POSTGRES:
			st.setTimestamp(26, new Timestamp(new java.util.Date().getTime()));
			st.setString(27, oper);
			break;
		case Constants.MANEJADOR_MYSQL:
			st.setTimestamp(26, new Timestamp(new java.util.Date().getTime()));
			st.setString(27, oper);
			break;
		}

		st.executeUpdate();
		setFinally(null, st);
	}

	public static boolean checkExistSecurity(String idStruct, long idPerson) throws Exception {
		StringBuffer sql = new StringBuffer(60);
		sql.append("SELECT idStruct FROM permissionstructuser WHERE idStruct = ").append(idStruct);
		sql.append(" AND idPerson = ").append(idPerson);
		log.debug("[checkExistSecurity] " + sql);
		Properties prop = JDBCUtil.doQueryOneRow(sql.toString(),Thread.currentThread().getStackTrace()[1].getMethodName());
		if (prop.getProperty("isEmpty").equalsIgnoreCase("false")) {
			return true;
		}
		return false;
	}

	public static ArrayList<String> checkExistSecurity(long idPerson) throws Exception {
		ArrayList<String> lista = new ArrayList<String>();

		StringBuffer sql = new StringBuffer(60);
		sql.append("SELECT idStruct FROM permissionstructuser ");
		sql.append("WHERE idPerson = ").append(idPerson);
		log.debug("[checkExistSecurity] " + sql);

		CachedRowSet crs = JDBCUtil.executeQuery(sql, Thread.currentThread().getStackTrace()[1].getMethodName());

		while(crs.next()) {
			lista.add(crs.getString(1));
		}

		return lista;
	}

	public static int checkModifySecurity(String idStruct, long idPerson) throws Exception {
		StringBuffer sql = new StringBuffer(60);
		int retorno = 0;
		sql.append("SELECT CASE WHEN permisoModificado is null THEN '0' ELSE permisoModificado END as permisoModificado FROM permissionstructuser WHERE idStruct = ").append(idStruct);
		sql.append(" AND idPerson = ").append(idPerson);
		log.debug("[checkModifySecurity] " + sql);
		CachedRowSet crs = JDBCUtil.executeQuery(sql, Thread.currentThread().getStackTrace()[1].getMethodName());
		if (crs.next()) {
			retorno = crs.getInt("permisoModificado");
		}
		return retorno;
	}

	public static HashMap<String,Integer> checkModifySecurity(long idPerson) throws Exception {
		HashMap<String,Integer> lista = new HashMap<String,Integer>();

		StringBuffer sql = new StringBuffer(60);
		sql.append("SELECT idStruct, CASE WHEN permisoModificado is null THEN '0' ELSE permisoModificado END as permisoModificado FROM permissionstructuser ");
		sql.append("WHERE idPerson = ").append(idPerson);
		log.debug("[checkModifySecurity] " + sql);

		CachedRowSet crs = JDBCUtil.executeQuery(sql, Thread.currentThread().getStackTrace()[1].getMethodName());

		while(crs.next()) {
			lista.put(crs.getString(1),crs.getInt(2));
		}

		return lista;
	}

	// private static void deleteTableSecurity(String id,Hashtable data,PermissionUserForm forma
	// ,PreparedStatement st,Connection con) throws Exception {
	// PreparedStatement select = null;
	// if (!ToolsHTML.isEmptyOrNull(id)) {
	// //obtenemos el nodo hijo del nodo en que estamos parados y actualizamos su permisologia para la carpeta
	// Collection childs = (Collection)data.get(id);
	// if (childs!=null&&!childs.isEmpty()) {
	// for (Iterator iterator = childs.iterator(); iterator.hasNext();) {
	// BeanCheckSec idChild = (BeanCheckSec) iterator.next();
	//
	// if (idChild.isExits()) {
	// //primero que nada borramos los documentos del nodo id hijo
	// HandlerDBUser.checkExistSecurityDeleteDocs(idChild.getIdNode(),forma.getIdPerson(),forma,st,con);
	// st = con.prepareStatement(getQueryDeleteSecurity(forma,idChild.getIdNode()));
	// st.executeUpdate();
	// }
	// //recursivamente realizamos el mismo paso pero esta vez con el nodo hijo..
	// deleteTableSecurity(idChild.getIdNode(),data,forma,st,con);
	//
	// }
	// }
	// }
	// }

	private static void updateTableSecurity(String id, Hashtable data, PermissionUserForm forma, PreparedStatement st, Connection con) throws Exception {
		PreparedStatement select = null;
		String idNodeSw = "";
		if (!ToolsHTML.isEmptyOrNull(id)) {
			// obtenemos el nodo hijo del nodo en que estamos parados y actualizamos su permisologia para la carpeta
			Collection childs = (Collection) data.get(id);
			if (childs != null && !childs.isEmpty()) {
				for (Iterator iterator = childs.iterator(); iterator.hasNext();) {
					BeanCheckSec idChild = (BeanCheckSec) iterator.next();

					//debo verificar si la seguridad de este nodo debe ser ajustada
					//el unico caso en el que no se ajusta es:
					// 1.- cuando el id del nodo, es igual al del permiso modificado
					boolean storeSecutiry = true;

					try {
						if(idChild.getIdNode().equals(Integer.toString(idChild.getPermisoModificado()))){
							storeSecutiry = false;
						}
					} catch (Exception e) {
						// TODO: handle exception
						log.error("Error verificando permisologia de usuario propia o no, en nodo " + idChild.getIdNode(), e);
					}

					if (storeSecutiry) {
						// if (idChild.getPermisoModificado() == 0 ||
						// idChild.getPermisoModificado() ==
						// Integer.parseInt(forma.getIdStruct()) ||
						// idChild.getPermisoModificado() ==
						// forma.getPermisoModificado()) {
						log.info("Almacenando seguridad de usuario para el nodo " + idChild.getIdNode());
						int permisoModificadoAnterior = forma.getPermisoModificado();
						forma.setPermisoModificado(Integer.parseInt(forma.getIdStruct()));
						if (idChild.isExits()) {
							if (!idNodeSw.equalsIgnoreCase(idChild.getIdNode())) {
								idNodeSw = idChild.getIdNode();
								st = con.prepareStatement(JDBCUtil.replaceCastMysql(getQueryUpdateSecurity(forma, idChild.getIdNode())));
								st.executeUpdate();
							}
						} else {
							select = con.prepareStatement(JDBCUtil.replaceCastMysql("SELECT idStruct FROM permissionstructuser WHERE idStruct ="
									+ idChild.getIdNode() + "  AND idPerson =" + forma.getIdPerson()));
							ResultSet res = select.executeQuery();
							if (!res.next()) {
								insertSecurityStructUser(forma, idChild.getIdNode(), con);
							}
							res.close();
						}
						forma.setPermisoModificado(permisoModificadoAnterior);
						// }
						// primero que nada actualizamos los documentos del nodo
						// id hijo
						// ESTO OCASIONA PROBLEMAS EN LA SEGURIDAD
						// ////HandlerDBUser.checkExistSecurityDocs(idChild.getIdNode(),
						// forma.getIdPerson(), forma, st, con);
						// recursivamente realizamos el mismo paso pero esta vez
						// con el nodo hijo..
						updateTableSecurity(idChild.getIdNode(), data, forma, st, con);
					} else {
						log.info("El nodo " + idChild.getIdNode() + " tiene seguridad propia de usuario, se frena la cascada aqui y se pasa al siguiente.");
					}
				}
			}
		}
	}

	// 02 de septiembre 2005 inicio
	public static synchronized boolean deleteSecurityStructUser(PermissionUserForm forma, String usr) throws Exception {

		Connection con = null;
		boolean resp = false;
		PreparedStatement st = null;
		ResultSet rs = null;
		CachedRowSet crs = new CachedRowSet();
		StringBuffer base = new StringBuffer();
		StringBuffer update = new StringBuffer();
		StringBuffer consulta = new StringBuffer();

		// Se Cargan los Ids de Todos los Nodos Hijos....
		String hijos = HandlerStruct.getAllNodesChilds(forma.getIdStruct());
		log.debug("Hijos: " + hijos);

		consulta.append(" SELECT a.*,b.idNode FROM permissionstructuser a, struct b ");
		consulta.append(" WHERE a.idStruct=b.idNode ");
		consulta.append(" and idPerson = ").append(forma.getIdPerson());
		consulta.append(" and cast(b.idNode as varchar) =  (select x.idNodeParent from struct x where x.idNode = ");
		consulta.append(forma.getIdStruct()).append(") ");

		try {
			crs = JDBCUtil.executeQuery(consulta, Thread.currentThread().getStackTrace()[1].getMethodName());
			if (crs.next()) { // si tiene un padre actualizamos este y los hijos con el codigo del padre para heredar los permisos

				update.append(" UPDATE permissionstructuser SET ");
				update.append(" toView='").append(JDBCUtil.getByte(crs.getString("toView"))).append("'");
				update.append(", toRead='").append(JDBCUtil.getByte(crs.getString("toRead"))).append("'");
				update.append(", toViewSub='").append(JDBCUtil.getByte(crs.getString("toViewSub"))).append("'");
				update.append(", toAddFolder='").append(JDBCUtil.getByte(crs.getString("toAddFolder"))).append("'");
				update.append(", toAddProcess='").append(JDBCUtil.getByte(crs.getString("toAddProcess"))).append("'");
				update.append(", toDelete='").append(JDBCUtil.getByte(crs.getString("toDelete"))).append("'");
				update.append(", toMove='").append(JDBCUtil.getByte(crs.getString("toMove"))).append("'");
				update.append(", toEdit='").append(JDBCUtil.getByte(crs.getString("toEdit"))).append("'");
				update.append(", toAddDocument='").append(JDBCUtil.getByte(crs.getString("toAddDocument"))).append("'");
				update.append(", toAdmon='").append(JDBCUtil.getByte(crs.getString("toAdmon"))).append("'");
				update.append(", active='").append(JDBCUtil.getByte(crs.getString("active"))).append("'");
				update.append(", toViewDocs='").append(JDBCUtil.getByte(crs.getString("toViewDocs"))).append("'");
				update.append(", toEditDocs='").append(JDBCUtil.getByte(crs.getString("toEditDocs"))).append("'");
				update.append(", toAdmonDocs='").append(JDBCUtil.getByte(crs.getString("toAdmonDocs"))).append("'");
				update.append(", toDelDocs='").append(JDBCUtil.getByte(crs.getString("toDelDocs"))).append("'");
				update.append(", toReview='").append(JDBCUtil.getByte(crs.getString("toReview"))).append("'");
				update.append(", toApproved='").append(JDBCUtil.getByte(crs.getString("toApproved"))).append("'");
				update.append(", toMoveDocs='").append(JDBCUtil.getByte(crs.getString("toMoveDocs"))).append("'");
				update.append(", toCheckOut='").append(JDBCUtil.getByte(crs.getString("toCheckOut"))).append("'");
				update.append(", toEditRegister='").append(JDBCUtil.getByte(crs.getString("toEditRegister"))).append("'");
				update.append(", toImpresion='").append(JDBCUtil.getByte(crs.getString("toImpresion"))).append("'");
				update.append(", toCheckTodos='").append(JDBCUtil.getByte(crs.getString("toCheckTodos"))).append("'");
				update.append(", toDoFlows='").append(JDBCUtil.getByte(crs.getString("toDoFlows"))).append("'");
				update.append(", docInline='").append(JDBCUtil.getByte(crs.getString("docInline"))).append("'");
				update.append(", toFlexFlow='").append(JDBCUtil.getByte(crs.getString("toFlexFlow"))).append("'");
				update.append(", toChangeUsr='").append(JDBCUtil.getByte(crs.getString("toChangeUsr"))).append("'");
				update.append(", toCompleteFlow='").append(JDBCUtil.getByte(crs.getString("toCompleteFlow"))).append("'");
				update.append(", permisoModificado='").append(crs.getInt("idNode")).append("'");
				update.append(", toPublicEraser='").append(JDBCUtil.getByte(crs.getString("toPublicEraser"))).append("'");
				update.append(", toDownload='").append(JDBCUtil.getByte(crs.getString("toDownload"))).append("'");
				update.append(" WHERE idPerson = ").append(forma.getIdPerson());
				update.append(" AND permisoModificado='").append(forma.getIdStruct()).append("'");
				update.append(" AND idStruct IN ('").append(forma.getIdStruct()).append("'");
				if (!ToolsHTML.isEmptyOrNull(hijos)) {// Si el Nodo Tiene Hijos se Concatenan los Mismos...
					update.append(",'");
					update.append(hijos.replaceAll(",","','"));
					update.append("'");
				}
				update.append(")");

				JDBCUtil.executeUpdate(update);

			} else { // en caso contrario eliminamos el grupo de los permisos otorgados

				update.setLength(0);
				update.append(" DELETE FROM permissionstructuser WHERE idPerson = '").append(forma.getIdPerson()).append("'");
				update.append(" AND permisoModificado='").append(forma.getIdStruct()).append("'");
				update.append(" AND idStruct IN ('").append(forma.getIdStruct()).append("'");
				if (!ToolsHTML.isEmptyOrNull(hijos)) {// Si el Nodo Tiene Hijos se Concatenan los Mismos...
					update.append(",'");
					update.append(hijos.replaceAll(",","','"));
					update.append("'");
				}
				update.append(") ");

				JDBCUtil.executeUpdate(update);

			}
			resp = true;
		} catch (Exception ex) {
			ex.printStackTrace();
			// applyRollback(con);
			setMensaje(ex.getMessage());
			resp = false;
		} finally {
			// setFinally(con, st);
		}

		// // Se Cargan los Ids de Todos los Nodos Hijos....
		// String hijos = HandlerStruct.getAllNodesChilds(forma.getIdStruct());
		// // Se Arma El Query...
		// StringBuffer update = new StringBuffer(60);
		// update.append("DELETE FROM permissionstructuser ");
		// update.append(" WHERE idPerson = ").append(forma.getIdPerson());
		// update.append(" AND idStruct IN (").append(forma.getIdStruct());
		// // Si el Nodo Tiene Hijos se Concatenan los Mismos...
		// if (!ToolsHTML.isEmptyOrNull(hijos)) {
		// update.append(",").append(hijos);
		// }
		// update.append(")");
		// Connection con = null;
		// boolean resp = false;
		// PreparedStatement st = null;
		// try {
		// con = JDBCUtil.getConnection(Thread.currentThread().getStackTrace()[1].getMethodName());
		// con.setAutoCommit(false);
		// // Eliminamos la Seguridad en todas las Carpetas y SubCarpetas del Nodo
		// st = con.prepareStatement(update.toString());
		// st.executeUpdate();
		// // Registro de Auditor�a
		// updateAudit(forma, con, usr, "DEL");
		// // Se Procede a Eliminar la Seguridad en Todos los Documentos
		// update = new StringBuffer(50);
		// update.append("DELETE FROM permisiondocuser WHERE idUser = '").append(forma.getIdUser()).append("'");
		// update.append(" AND idDocument IN (SELECT numGen FROM documents WHERE idNode IN (").append(forma.getIdStruct());
		// // Si el Nodo Tiene Hijos se Concatenan los Mismos...
		// if (!ToolsHTML.isEmptyOrNull(hijos)) {
		// update.append(",").append(hijos);
		// }
		// update.append("))");
		// st = con.prepareStatement(update.toString());
		// st.executeUpdate();
		// con.commit();
		// resp = true;
		// } catch (Exception ex) {
		// ex.printStackTrace();
		// applyRollback(con);
		// setMensaje(ex.getMessage());
		// resp = false;
		// } finally {
		// setFinally(con, st);
		// }

		return resp;
	}

	// 02 de septiembre 2005 fin

	public static synchronized boolean updateSecurityStructUser(PermissionUserForm forma, boolean isFolderOrProcess, boolean isSite) throws Exception {
		Hashtable childs = new Hashtable();
		// aqui obtengo la permisologia de todos los nodos hijos,si estan o no estan en la tabla permissionstructuser...
		// devuelve un hashtable con el node y su sw (verdaddero o falso)
		if (isFolderOrProcess) {
			childs = HandlerStruct.getStructChilds(forma.getIdStruct(), forma.getIdPerson(), true, true);
		}
		if (isSite) {
			childs = HandlerStruct.getStructChilds(forma.getIdStruct(), forma.getIdGroup(), false, false);
		}
		Connection con = null;
		boolean resp = false;
		PreparedStatement st = null;
		// se chequea si hay permisologia en la table permissionstructuser con este usuario y este node
		if (checkExistSecurity(forma.getIdStruct(), forma.getIdPerson())) {
			try {
				con = JDBCUtil.getConnection(Thread.currentThread().getStackTrace()[1].getMethodName());
				con.setAutoCommit(false);
				st = con.prepareStatement(JDBCUtil.replaceCastMysql(getQueryUpdateSecurity(forma, forma.getIdStruct())));
				st.executeUpdate();
				// primero que nada actualizamos los documentos del nodo id en que estamos parados
				HandlerDBUser.checkExistSecurityDocs(forma.getIdStruct(), forma.getIdPerson(), forma, st, con);
				// Se procede a actualizar la seguridad en los nodos hijos
				if (childs != null && !childs.isEmpty()) {
					updateTableSecurity(forma.getIdStruct(), childs, forma, st, con);
				}
				con.commit();
				resp = true;
			} catch (Exception e) {
				e.printStackTrace();
				applyRollback(con);
				setMensaje(e.getMessage());
				resp = false;
			} finally {
				setFinally(con, st);
			}
			return resp;
		} else {
			try {
				con = JDBCUtil.getConnection(Thread.currentThread().getStackTrace()[1].getMethodName());
				con.setAutoCommit(false);
				insertSecurityStructUser(forma, forma.getIdStruct(), con);
				// Se procede a actualizar la seguridad en los nodos hijos
				if (childs != null && !childs.isEmpty()) {
					updateTableSecurity(forma.getIdStruct(), childs, forma, st, con);
				}
				// 24 AGOSTO 2005 INICIO
				// modulo para actualizar los documentos
				HandlerDBUser.checkExistSecurityDocs(forma.getIdStruct(), forma.getIdPerson(), forma, st, con);
				// 24 AGOSTO 2005 FIN
				con.commit();
				resp = true;
			} catch (Exception ex) {
				ex.printStackTrace();
				applyRollback(con);
				resp = false;
			} finally {
				setFinally(con, st);
			}
		}
		return resp;
	}

	public static synchronized boolean updateSecurityStructUser(PermissionUserForm forma, String nodeType, String usr) throws Exception {
		Hashtable childs = new Hashtable();
		// aqui obtengo la permisologia de todos los nodos hijos,si estan o no estan en la tabla permissionstructuser...
		// devuelve un hashtable con el node y su sw (verdaddero o falso)
		boolean isFolderOrProcess = DesigeConf.getProperty("processType").equalsIgnoreCase(nodeType) || DesigeConf.getProperty("folderType").equalsIgnoreCase(nodeType);
		// boolean isSite = DesigeConf.getProperty("siteType").equalsIgnoreCase(nodeType);
		boolean isLocationType = DesigeConf.getProperty("locationType").equalsIgnoreCase(nodeType);
		// if (isFolderOrProcess||isSite||isLocationType) {
		if (isFolderOrProcess || isLocationType) {
			childs = HandlerStruct.getStructChilds(forma.getIdStruct(), forma.getIdPerson(), true, true);
		}
		Connection con = null;
		boolean resp = false;
		PreparedStatement st = null;
		boolean haySeguridad = checkExistSecurity(forma.getIdStruct(), forma.getIdPerson());
		int anteriorPermisoModificado = 0;
		// se chequea si hay permisologia en la table permissionstructuser con este usuario y este node
		try {
			forma.setPermisoModificado(HandlerDBUser.checkModifySecurity(forma.getIdStruct(), forma.getIdPerson()));

			con = JDBCUtil.getConnection(Thread.currentThread().getStackTrace()[1].getMethodName());
			con.setAutoCommit(false);

			anteriorPermisoModificado = forma.getPermisoModificado();
			forma.setPermisoModificado(Integer.parseInt(forma.getIdStruct())); // marcamos la seguridad para indicar que fue modificada
			if (haySeguridad) {
				st = con.prepareStatement(JDBCUtil.replaceCastMysql(getQueryUpdateSecurity(forma, forma.getIdStruct())));
				st.executeUpdate();
				// se Guarda el Registro de Auditoria
				updateAudit(forma, con, usr, "UPD");
			} else {
				insertSecurityStructUser(forma, forma.getIdStruct(), con);
				// se Guarda el Registro de Auditoria
				updateAudit(forma, con, usr, "INS");
			}

			// ESTO OCASIONA PROBLEMAS EN LA SEGURIDAD // primero que nada actualizamos los documentos del nodo id en que estamos parados
			// checkExistSecurityDocs(forma.getIdStruct(), forma.getIdPerson(), forma, st, con);

			// Se procede a actualizar la seguridad en los nodos hijos
			if (childs != null && !childs.isEmpty()) {
				forma.setPermisoModificado(anteriorPermisoModificado); // marcamos la seguridad para indicar que fue modificada
				updateTableSecurity(forma.getIdStruct(), childs, forma, st, con);
			}
			con.commit();
			resp = true;
		} catch (Exception e) {
			log.error(e.getMessage());
			e.printStackTrace();
			applyRollback(con);
			setMensaje(e.getMessage());
			resp = false;
		} finally {
			setFinally(con, st);
		}
		return resp;
	}

	public synchronized static boolean insertSecurityStructUser(PermissionUserForm forma, String idStruct, Connection conParam) throws Exception {
		Connection con = null;
		PreparedStatement st = null;
		int result = 0;
		con = conParam != null ? conParam : JDBCUtil.getConnection(Thread.currentThread().getStackTrace()[1].getMethodName());
		st = con.prepareStatement(JDBCUtil.replaceCastMysql(getInsertSecurity(idStruct, forma)));
		result = st.executeUpdate();
		if (conParam == null) {
			setFinally(con, st);
		}
		return result > 0;
	}

	// 11 agosto 2005 fin
	public static Collection getAllUserWithSecInDocs(String idDocument) throws ApplicationExceptionChecked {
		Vector result = new Vector();
		StringBuilder sql = new StringBuilder(100);
		sql.append("SELECT distinct p.idPerson,nameUser, ");
		switch (Constants.MANEJADOR_ACTUAL) {
		case Constants.MANEJADOR_MSSQL:
			sql.append("(p.Apellidos + ' ' + p.nombres) AS nombre, ");
			break;
		case Constants.MANEJADOR_POSTGRES:
			sql.append("(p.Apellidos || ' ' || p.nombres) AS nombre, ");
			break;
		case Constants.MANEJADOR_MYSQL:
			sql.append("CONCAT(p.Apellidos , ' ' , p.nombres) AS nombre, ");
			break;
		}
		sql.append(" gu.IdGrupo,gu.nombreGrupo, pd.idDocument FROM person p,groupusers gu,permisiondocuser pd ");
		sql.append(" WHERE gu.IdGrupo = p.IdGrupo AND p.accountActive = '1' ");
		sql.append(" AND p.nameuser = pd.iduser ");
		if (!ToolsHTML.isEmptyOrNull(idDocument)) {
			sql.append(" AND pd.idDocument = ").append(idDocument);
		}
		try {
			Vector datos = JDBCUtil.doQueryVector(sql.toString(),Thread.currentThread().getStackTrace()[1].getMethodName());
			SeguridadUserForm forma = null;
			for (int row = 0; row < datos.size(); row++) {
				Properties properties = (Properties) datos.elementAt(row);
				forma = new SeguridadUserForm();
				forma.setNombres(properties.getProperty("nombre"));
				forma.setNameUser(properties.getProperty("nameUser"));
				forma.setIdGrupo(properties.getProperty("IdGrupo"));
				forma.setNombreGrupo(properties.getProperty("nombreGrupo"));
				forma.setIdPerson(Long.parseLong(properties.getProperty("idPerson")));
				forma.setIsDocument(idDocument);
				result.add(forma);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			throw new ApplicationExceptionChecked("E0051");
		}

		return result;
	}

	public static Collection getAllUserWithSecInRecord() throws ApplicationExceptionChecked {
		Vector result = new Vector();
		StringBuffer sql = new StringBuffer(100);
		sql.append("SELECT distinct p.idPerson,nameUser, ");
		switch (Constants.MANEJADOR_ACTUAL) {
		case Constants.MANEJADOR_MSSQL:
			sql.append("(p.Apellidos + ' ' + p.nombres) AS nombre, ");
			break;
		case Constants.MANEJADOR_POSTGRES:
			sql.append("(p.Apellidos || ' ' || p.nombres) AS nombre, ");
			break;
		case Constants.MANEJADOR_MYSQL:
			sql.append("CONCAT(p.Apellidos , ' ' , p.nombres) AS nombre, ");
			break;
		}
		sql.append(" gu.IdGrupo,gu.nombreGrupo FROM person p,groupusers gu,permissionrecorduser pd ");
		sql.append(" WHERE gu.IdGrupo = p.IdGrupo AND p.accountActive = '1' ");
		sql.append(" AND p.nameuser = pd.iduser ");
		try {
			Vector datos = JDBCUtil.doQueryVector(sql.toString(),Thread.currentThread().getStackTrace()[1].getMethodName());
			SeguridadUserForm forma = null;
			for (int row = 0; row < datos.size(); row++) {
				Properties properties = (Properties) datos.elementAt(row);
				forma = new SeguridadUserForm();
				forma.setNombres(properties.getProperty("nombre"));
				forma.setNameUser(properties.getProperty("nameUser"));
				forma.setIdGrupo(properties.getProperty("IdGrupo"));
				forma.setNombreGrupo(properties.getProperty("nombreGrupo"));
				forma.setIdPerson(Long.parseLong(properties.getProperty("idPerson")));
				forma.setIsDocument("0");
				result.add(forma);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			throw new ApplicationExceptionChecked("E0051");
		}

		return result;
	}

	public static Collection getAllUserWithSecInFiles() throws ApplicationExceptionChecked {
		Vector result = new Vector();
		StringBuffer sql = new StringBuffer(100);
		sql.append("SELECT distinct p.idPerson,nameUser, ");
		switch (Constants.MANEJADOR_ACTUAL) {
		case Constants.MANEJADOR_MSSQL:
			sql.append("(p.Apellidos + ' ' + p.nombres) AS nombre, ");
			break;
		case Constants.MANEJADOR_POSTGRES:
			sql.append("(p.Apellidos || ' ' || p.nombres) AS nombre, ");
			break;
		case Constants.MANEJADOR_MYSQL:
			sql.append("CONCAT(p.Apellidos , ' ' , p.nombres) AS nombre, ");
			break;
		}
		sql.append(" gu.IdGrupo,gu.nombreGrupo FROM person p,groupusers gu,permissionfilesuser pd ");
		sql.append(" WHERE gu.IdGrupo = p.IdGrupo AND p.accountActive = '1' ");
		sql.append(" AND p.nameuser = pd.iduser ");
		try {
			Vector datos = JDBCUtil.doQueryVector(sql.toString(),Thread.currentThread().getStackTrace()[1].getMethodName());
			SeguridadUserForm forma = null;
			for (int row = 0; row < datos.size(); row++) {
				Properties properties = (Properties) datos.elementAt(row);
				forma = new SeguridadUserForm();
				forma.setNombres(properties.getProperty("nombre"));
				forma.setNameUser(properties.getProperty("nameUser"));
				forma.setIdGrupo(properties.getProperty("IdGrupo"));
				forma.setNombreGrupo(properties.getProperty("nombreGrupo"));
				forma.setIdPerson(Long.parseLong(properties.getProperty("idPerson")));
				forma.setIsDocument("0");
				result.add(forma);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			throw new ApplicationExceptionChecked("E0051");
		}

		return result;
	}

	// 11 agosto 2005 fin
	public static Collection getAllUserWithSecInStruct(String idNode) throws ApplicationExceptionChecked {
		Vector result = new Vector();
		StringBuffer sql = new StringBuffer(100);
		sql.append("SELECT p.idPerson,nameUser, ");
		switch (Constants.MANEJADOR_ACTUAL) {
		case Constants.MANEJADOR_MSSQL:
			sql.append("(p.Apellidos + ' ' + p.nombres) AS nombre,");
			break;
		case Constants.MANEJADOR_POSTGRES:
			sql.append("(p.Apellidos || ' ' || p.nombres) AS nombre,");
			break;
		case Constants.MANEJADOR_MYSQL:
			sql.append("CONCAT(p.Apellidos , ' ' , p.nombres) AS nombre,");
			break;
		}
		sql.append("gu.IdGrupo,gu.nombreGrupo FROM person p,groupusers gu,permissionstructuser pu");
		sql.append(" WHERE gu.IdGrupo = p.IdGrupo AND p.accountActive = '1' ");
		sql.append(" AND pu.idPerson = p.idPerson AND pu.idStruct = ").append(idNode);
		try {
			Vector datos = JDBCUtil.doQueryVector(sql.toString(),Thread.currentThread().getStackTrace()[1].getMethodName());
			SeguridadUserForm forma = null;
			for (int row = 0; row < datos.size(); row++) {
				Properties properties = (Properties) datos.elementAt(row);
				forma = new SeguridadUserForm();
				forma.setNombres(properties.getProperty("nombre"));
				forma.setNameUser(properties.getProperty("nameUser"));
				forma.setIdGrupo(properties.getProperty("IdGrupo"));
				forma.setNombreGrupo(properties.getProperty("nombreGrupo"));
				forma.setIdPerson(Long.parseLong(properties.getProperty("idPerson")));
				result.add(forma);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			throw new ApplicationExceptionChecked("E0051");
		}

		return result;
	}

	public static Collection getAllUserWithSecInStruct(String idNode, String apellido) throws ApplicationExceptionChecked {
		Vector result = new Vector();
		StringBuffer sql = new StringBuffer(100);
		sql.append(" SELECT p.idPerson,nameUser, ");
		switch (Constants.MANEJADOR_ACTUAL) {
		case Constants.MANEJADOR_MSSQL:
			sql.append("(p.Apellidos + ' ' + p.nombres) AS nombre,");
			break;
		case Constants.MANEJADOR_POSTGRES:
			sql.append("(p.Apellidos || ' ' || p.nombres) AS nombre,");
			break;
		case Constants.MANEJADOR_MYSQL:
			sql.append("CONCAT(p.Apellidos , ' ' , p.nombres) AS nombre,");
			break;
		}
		sql.append(" gu.IdGrupo,gu.nombreGrupo FROM person p,groupusers gu,permissionstructuser pu");
		sql.append(" WHERE gu.IdGrupo = p.IdGrupo AND p.accountActive = '1'");
		sql.append(" AND pu.permisoModificado=").append(idNode);
		sql.append(" AND pu.idPerson = p.idPerson AND pu.idStruct = ").append(idNode);
		if (!ToolsHTML.isEmptyOrNull(apellido)) {
			sql.append(" AND p.Apellidos LIKE '%").append(apellido).append("%'");
		}
		try {
			Vector datos = JDBCUtil.doQueryVector(sql.toString(),Thread.currentThread().getStackTrace()[1].getMethodName());
			SeguridadUserForm forma = null;
			for (int row = 0; row < datos.size(); row++) {
				Properties properties = (Properties) datos.elementAt(row);
				forma = new SeguridadUserForm();
				forma.setNombres(properties.getProperty("nombre"));
				forma.setNameUser(properties.getProperty("nameUser"));
				forma.setIdGrupo(properties.getProperty("IdGrupo"));
				forma.setNombreGrupo(properties.getProperty("nombreGrupo"));
				forma.setIdPerson(Long.parseLong(properties.getProperty("idPerson")));
				result.add(forma);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			throw new ApplicationExceptionChecked("E0051");
		}

		return result;
	}

	public static Collection getAllUsersForGrups(String toSearch, boolean first) throws Exception {
		Vector result = new Vector();
		StringBuffer sql = new StringBuffer(100);
		Vector datos;
		sql.append("SELECT p.idPerson,nameUser, ");
		switch (Constants.MANEJADOR_ACTUAL) {
		case Constants.MANEJADOR_MSSQL:
			sql.append("(p.Apellidos + ' ' + p.nombres) AS nombre ");
			break;
		case Constants.MANEJADOR_POSTGRES:
			sql.append("(p.Apellidos || ' ' || p.nombres) AS nombre ");
			break;
		case Constants.MANEJADOR_MYSQL:
			sql.append("CONCAT(p.Apellidos , ' ' , p.nombres) AS nombre ");
			break;
		}
		sql.append(",gu.IdGrupo,gu.nombreGrupo").append(" FROM person p,groupusers gu WHERE gu.IdGrupo = p.IdGrupo");
		if (toSearch != null) {
			sql.append(" AND ( p.Apellidos LIKE '").append(toSearch).append("%'");
			sql.append(" OR p.nombres LIKE '%").append(toSearch).append("%'");
			sql.append(" OR p.nameuser LIKE '").append(toSearch).append("%'").append(")");
		}
		sql.append(" AND p.accountActive = '").append(Constants.permission).append("'");
		sql.append(" ORDER BY lower(p.Apellidos) asc, lower(p.nombres) asc ");
		datos = JDBCUtil.doQueryVector(sql.toString(),Thread.currentThread().getStackTrace()[1].getMethodName());
		SeguridadUserForm forma = null;
		if (datos == null || datos.size() == 0) {
			return new Vector();
		}

		if (first) {
			if (datos != null && datos.size() > 0) {
				Properties properties = (Properties) datos.elementAt(0);
				forma = new SeguridadUserForm();
				forma.setNombres(properties.getProperty("nombre"));
				forma.setNameUser(properties.getProperty("nameUser"));
				forma.setIdGrupo(properties.getProperty("IdGrupo"));
				forma.setNombreGrupo(properties.getProperty("nombreGrupo"));
				forma.setIdPerson(Long.parseLong(properties.getProperty("idPerson")));
				result.add(forma);
			} else {
				throw new ApplicationExceptionChecked("E0116");
			}

		} else {
			for (int row = 0; row < datos.size(); row++) {
				Properties properties = (Properties) datos.elementAt(row);
				forma = new SeguridadUserForm();
				forma.setNombres(properties.getProperty("nombre"));
				forma.setNameUser(properties.getProperty("nameUser"));
				forma.setIdGrupo(properties.getProperty("IdGrupo"));
				forma.setNombreGrupo(properties.getProperty("nombreGrupo"));
				forma.setIdPerson(Long.parseLong(properties.getProperty("idPerson")));
				result.add(forma);
			}
		}
		return result;
	}

	public static Collection getAllUsers(String solicitudImpresion) throws Exception {
		ArrayList resp = new ArrayList();
		StringBuilder query = new StringBuilder("SELECT idperson,nameUser, ");
		switch (Constants.MANEJADOR_ACTUAL) {
		case Constants.MANEJADOR_MSSQL:
			query.append(" (p.Apellidos + ' ' + p.nombres) AS nombre, ");
			break;
		case Constants.MANEJADOR_POSTGRES:
			query.append(" (p.Apellidos || ' ' || p.nombres) AS nombre, ");
			break;
		case Constants.MANEJADOR_MYSQL:
			query.append(" CONCAT(p.Apellidos , ' ' , p.nombres) AS nombre, ");
			break;
		}
		query.append(" c.cargo FROM person p,tbl_cargo c,tbl_area a ");
		query.append(" WHERE accountActive = '1' ");
		query.append(" AND c.idcargo=cast(p.cargo as int) and c.idarea=a.idarea");
		query.append(" ORDER BY lower(p.apellidos) asc ");
		Vector datos = JDBCUtil.doQueryVector(query.toString(),Thread.currentThread().getStackTrace()[1].getMethodName());
		for (int row = 0; row < datos.size(); row++) {
			Properties properties = (Properties) datos.elementAt(row);
			Search bean = new Search(properties.getProperty("idperson"), properties.getProperty("nombre"));
			bean.setAditionalInfo(properties.getProperty("cargo"));
			resp.add(bean);
		}
		return resp;
	}

	public static Collection getAllUsers(String nombreUsuario, String nombreApellido) throws Exception {
		ArrayList resp = new ArrayList();
		StringBuilder query = new StringBuilder();

		query.append("SELECT idperson,nameUser,");
		switch (Constants.MANEJADOR_ACTUAL) {
		case Constants.MANEJADOR_MSSQL:
			query.append("(p.Apellidos + ' ' + p.nombres) AS nombre, ");
			break;
		case Constants.MANEJADOR_POSTGRES:
			query.append("(p.Apellidos || ' ' || p.nombres) AS nombre, ");
			break;
		case Constants.MANEJADOR_MYSQL:
			query.append("CONCAT(p.Apellidos , ' ' , p.nombres) AS nombre, ");
			break;
		}
		query.append("c.cargo FROM person p INNER JOIN tbl_cargo c ON(c.idcargo=cast(p.cargo as int)) INNER JOIN tbl_area a ON (c.idarea=a.idarea) ");
		query.append(" WHERE accountActive = '1'");

		if (nombreApellido != null && nombreApellido.length() > 0) {
			switch (Constants.MANEJADOR_ACTUAL) {
			case Constants.MANEJADOR_MSSQL:
				query.append(" AND ((p.Apellidos + ' ' + p.Nombres) LIKE '%" + nombreApellido + "%')");
				break;
			case Constants.MANEJADOR_POSTGRES:
				query.append(" AND ((p.Apellidos || ' ' || p.Nombres) LIKE '%" + nombreApellido + "%')");
				break;
			case Constants.MANEJADOR_MYSQL:
				query.append(" AND (CONCAT(p.Apellidos , ' ' , p.Nombres) LIKE '%" + nombreApellido + "%')");
				break;
			}
		}

		if (nombreUsuario != null && nombreUsuario.length() > 0) {
			query.append(" AND (p.nameUser = '" + nombreUsuario + "')");
		}

		query.append(" ORDER BY lower(p.apellidos) asc ");

		Vector datos = JDBCUtil.doQueryVector(query.toString(),Thread.currentThread().getStackTrace()[1].getMethodName());
		for (int row = 0; row < datos.size(); row++) {
			Properties properties = (Properties) datos.elementAt(row);
			Search bean = new Search(properties.getProperty("idperson"), properties.getProperty("nombre"));
			bean.setAditionalInfo(properties.getProperty("cargo"));
			resp.add(bean);
		}
		return resp;
	}

	public static Collection getAllUsersByCargo(String nombreUsuario, String nombreCargo) throws Exception {
		ArrayList resp = new ArrayList();
		StringBuilder query = new StringBuilder();

		query.append("SELECT idperson,nameUser,");
		switch (Constants.MANEJADOR_ACTUAL) {
		case Constants.MANEJADOR_MSSQL:
			query.append("(p.Apellidos + ' ' + p.nombres) AS nombre, ");
			break;
		case Constants.MANEJADOR_POSTGRES:
			query.append("(p.Apellidos || ' ' || p.nombres) AS nombre, ");
			break;
		case Constants.MANEJADOR_MYSQL:
			query.append("CONCAT(p.Apellidos , ' ' , p.nombres) AS nombre, ");
			break;
		}
		query.append("c.cargo FROM person p INNER JOIN tbl_cargo c ON(c.idcargo=cast(p.cargo as int)) INNER JOIN tbl_area a ON (c.idarea=a.idarea) ");
		query.append(" WHERE accountActive = '1'");

		if (nombreCargo != null && nombreCargo.length() > 0) {
			switch (Constants.MANEJADOR_ACTUAL) {
			case Constants.MANEJADOR_MSSQL:
				query.append(" AND c.cargo LIKE '%" + nombreCargo + "%' ");
				break;
			case Constants.MANEJADOR_POSTGRES:
				query.append(" AND c.cargo LIKE '%" + nombreCargo + "%' ");
				break;
			case Constants.MANEJADOR_MYSQL:
				query.append(" AND c.cargo LIKE '%" + nombreCargo + "%' ");
				break;
			}
		}

		if (nombreUsuario != null && nombreUsuario.length() > 0) {
			query.append(" AND (p.nameUser = '" + nombreUsuario + "')");
		}

		query.append(" ORDER BY lower(p.apellidos) asc ");

		Vector datos = JDBCUtil.doQueryVector(query.toString(),Thread.currentThread().getStackTrace()[1].getMethodName());
		for (int row = 0; row < datos.size(); row++) {
			Properties properties = (Properties) datos.elementAt(row);
			Search bean = new Search(properties.getProperty("idperson"), properties.getProperty("nombre"));
			bean.setAditionalInfo(properties.getProperty("cargo"));
			resp.add(bean);
		}
		return resp;
	}
	
	public synchronized static TreeMap getAllUsersMap() throws Exception {
		TreeMap<String, Users> lista = new TreeMap<String, Users>();
		StringBuilder query = new StringBuilder("select * from person WHERE accountActive = '1'");
		CachedRowSet crs = JDBCUtil.executeQuery(query, Thread.currentThread().getStackTrace()[1].getMethodName());
		Users usu = null;
		while(crs.next()) {
			usu = new Users();
			usu.setIdPerson(crs.getInt("idPerson"));
			usu.setNameUser(crs.getString("nameUser"));
			lista.put(usu.getNameUser(),usu);
		}
		return lista;
	}

	public synchronized static TreeMap getAllUsersNameMapById() throws Exception {
		TreeMap<String, String> lista = new TreeMap<String, String>();
		StringBuilder query = new StringBuilder("select * from person WHERE accountActive = '1'");
		CachedRowSet crs = JDBCUtil.executeQuery(query, Thread.currentThread().getStackTrace()[1].getMethodName());
		Users usu = null;
		while (crs.next()) {
			lista.put(crs.getString("idPerson"), crs.getString("nameUser"));
		}
		return lista;
	}

	public synchronized static Collection getAllUsers() throws Exception {
		return getAllUsers((Connection)null);
	}
	public synchronized static Collection getAllUsers(Connection con) throws Exception {
		ArrayList resp = new ArrayList();
		StringBuilder query = new StringBuilder(60);
		StringBuffer cad = new StringBuffer();

		switch (Constants.MANEJADOR_ACTUAL) {
		case Constants.MANEJADOR_MSSQL:
			query.append("SELECT nameUser,(p.Apellidos + ' ' + p.nombres) AS nombre, ");
			cad.append(" AND c.idcargo = cast(p.cargo as int) ");
			break;
		case Constants.MANEJADOR_POSTGRES:
			query.append("SELECT nameUser,(p.Apellidos || ' ' || p.nombres) AS nombre, ");
			cad.append(" AND c.idcargo = cast(p.cargo as int) ");
			break;
		case Constants.MANEJADOR_MYSQL:
			query.append("SELECT nameUser, CONCAT(p.Apellidos , ' ' , p.nombres) AS nombre, ");
			cad.append(" AND c.idcargo = p.cargo ");
			break;
		}
		query.append("c.cargo FROM person p,tbl_cargo c,tbl_area a ");
		query.append(" WHERE accountActive = '1' ");
		query.append(" AND nameUser != '").append(Constants.ID_USER_TEMPORAL).append("' ");

		query.append(cad);

		query.append(" and c.idarea=a.idarea");
		query.append(" ORDER BY lower(p.apellidos) asc ");
		Vector datos = JDBCUtil.doQueryVector(con, query.toString(),Thread.currentThread().getStackTrace()[1].getMethodName());
		for (int row = 0; row < datos.size(); row++) {
			Properties properties = (Properties) datos.elementAt(row);
			Search bean = new Search(properties.getProperty("nameUser"), properties.getProperty("nombre"));
			bean.setAditionalInfo(properties.getProperty("cargo"));
			resp.add(bean);
		}
		return resp;
	}
	
	
	public synchronized static CachedRowSet getListUsers() throws Exception {
		CachedRowSet crs = null;
		StringBuilder query = new StringBuilder();

		query.append("SELECT a.nameUser, a.apellidos, a.nombres, a.idArea, b.idCargo, b.cargo, c.area, a.idperson ");
		query.append("FROM person a, tbl_cargo b, tbl_area c ");
		query.append("WHERE accountActive = '1' ");
		query.append("AND b.idCargo=cast(a.cargo as int) ");
		query.append("AND c.idArea=a.idArea ");
		query.append("AND a.nameUser != '").append(Constants.ID_USER_TEMPORAL).append("' ");
		query.append("ORDER BY lower(a.apellidos) asc ");

		crs = JDBCUtil.executeQuery(query, Thread.currentThread().getStackTrace()[1].getMethodName());

		return crs;
	}
	
	public static Collection<Search> getListUserSelect() throws Exception {
		ArrayList<Search> resp = new ArrayList<Search>();
		CachedRowSet users = getListUsers();
		StringBuilder query = new StringBuilder();

		query.append("SELECT a.idperson, a.nameUser ");
		query.append("FROM person a ");
		query.append("WHERE a.accountActive = '1' ");
		query.append("AND a.nameUser != '").append(Constants.ID_USER_TEMPORAL).append("' ");
		query.append("ORDER BY lower(a.nameUser) ");

		users = JDBCUtil.executeQuery(query, Thread.currentThread().getStackTrace()[1].getMethodName());
		
		while(users.next()) {
			resp.add(new Search(users.getString(1),users.getString(2)));
		}
		return resp;
	}
	

	public synchronized static CachedRowSet getListArea() throws Exception {
		CachedRowSet crs = null;
		StringBuilder query = new StringBuilder();

		query.append("SELECT a.idArea, a.area ");
		query.append("FROM tbl_area a ");
		query.append("WHERE a.activea = '1' ");
		query.append("ORDER BY lower(a.area) asc ");

		crs = JDBCUtil.executeQuery(query, Thread.currentThread().getStackTrace()[1].getMethodName());

		return crs;
	}

	public synchronized static Collection getAllUsersWithIdPerson() throws Exception {
		ArrayList resp = new ArrayList();
		StringBuilder query = new StringBuilder(60);
		switch (Constants.MANEJADOR_ACTUAL) {
		case Constants.MANEJADOR_MSSQL:
			query.append("SELECT idPerson,nameUser,(p.Apellidos + ' ' + p.nombres) AS nombre, ");
			break;
		case Constants.MANEJADOR_POSTGRES:
			query.append("SELECT idPerson,nameUser,(p.Apellidos || ' ' || p.nombres) AS nombre, ");
			break;
		case Constants.MANEJADOR_MYSQL:
			query.append("SELECT idPerson,nameUser,CONCAT(p.Apellidos , ' ' , p.nombres) AS nombre, ");
			break;
		}
		query.append("c.cargo FROM person p,tbl_cargo c,tbl_area a ");
		query.append(" WHERE accountActive = '1' ");
		query.append(" AND nameUser != '").append(Constants.ID_USER_TEMPORAL).append("' ");
		query.append("   AND c.idcargo = cast(p.cargo as int) and c.idarea=a.idarea");
		query.append(" ORDER BY lower(p.apellidos) asc ");
		Vector datos = JDBCUtil.doQueryVector(query.toString(),Thread.currentThread().getStackTrace()[1].getMethodName());
		for (int row = 0; row < datos.size(); row++) {
			Properties properties = (Properties) datos.elementAt(row);
			Search bean = new Search(properties.getProperty("idPerson"), properties.getProperty("nombre"));
			bean.setAditionalInfo(properties.getProperty("cargo"));
			resp.add(bean);
		}
		return resp;
	}

	// SIMON 31 MAYO 2005 INICIO
	public static Collection getAllCargos() throws Exception {
		ArrayList resp = new ArrayList();
		// Simon 31 mayo 2005 inicio
		// String query = "SELECT nameUser, cargo FROM person p";
		String query = "SELECT distinct c.idcargo, c.cargo FROM person p,tbl_cargo c,tbl_area a";
		// Sim�n 31 mayo 2005 fin
		query += " WHERE accountActive = '1'";
		query += " AND c.idcargo=cast(p.cargo as int) and c.idarea=a.idarea";
		Vector datos = JDBCUtil.doQueryVector(query,Thread.currentThread().getStackTrace()[1].getMethodName());
		for (int row = 0; row < datos.size(); row++) {
			Properties properties = (Properties) datos.elementAt(row);
			Search bean = new Search(properties.getProperty("idcargo"), properties.getProperty("cargo"));
			resp.add(bean);
		}
		return resp;
	}

	// SIMON 31 MAYO 2005 FIN
	// 08 agosto 2005 inicio
	public static Collection getAllUsersFilterAccion(String user, boolean sinEscojer, boolean showCharge) throws Exception {
		Vector resp = new Vector();
		StringBuilder query = new StringBuilder(50);
		query.append("SELECT p.idPerson,nameUser,idGrupo, ");
		switch (Constants.MANEJADOR_ACTUAL) {
		case Constants.MANEJADOR_MSSQL:
			query.append("(p.Apellidos + ' ' + p.Nombres) AS nombre, ");
			break;
		case Constants.MANEJADOR_POSTGRES:
			query.append("(p.Apellidos || ' ' || p.Nombres) AS nombre, ");
			break;
		case Constants.MANEJADOR_MYSQL:
			query.append("CONCAT(p.Apellidos , ' ' , p.Nombres) AS nombre, ");
			break;
		}
		query.append(" c.cargo");
		query.append(" FROM person p,tbl_cargo c,tbl_area a WHERE accountActive = '1' ");
		query.append(" AND c.idcargo=cast(p.cargo as int) and c.idarea=a.idarea");
		if (!ToolsHTML.isEmptyOrNull(user)) {
			if (sinEscojer) {
				query.append(" AND p.idPerson not in (").append(user).append(")");
			} else {
				query.append(" AND p.idPerson  in (").append(user).append(")");
			}

		}
		if (showCharge) {
			query.append(" order by lower(c.cargo) ");
		} else {
			query.append(" order by lower(Apellidos) asc ");
		}
		Vector datos = JDBCUtil.doQueryVector(query.toString(),Thread.currentThread().getStackTrace()[1].getMethodName());
		String idPerson;
		String idGrupo;
		String userName;
		String cargoStr = "";
		for (int row = 0; row < datos.size(); row++) {
			Properties properties = (Properties) datos.elementAt(row);
			cargoStr = properties.getProperty("cargo");
			userName = properties.getProperty("nameUser");
			idGrupo = properties.getProperty("idGrupo");
			idPerson = properties.getProperty("idPerson");
			Search bean = null;
			if (showCharge) {
				bean = new Search(userName, cargoStr);
				bean.setAditionalInfo(properties.getProperty("nombre"));
			} else {
				bean = new Search(userName, properties.getProperty("nombre"));
				bean.setAditionalInfo(cargoStr);
			}

			resp.add(bean);
		}
		return resp;
	}

	public static String existeCargo(String cargo, String area, Connection con) {
		String cargo1 = cargo;
		boolean swCargo = false;
		boolean swArea = false;

		CargoTO oCargoTO = new CargoTO();
		AreaTO oAreaTO = new AreaTO();

		CargoDAO oCargoDAO = new CargoDAO();
		AreaDAO oAreaDAO = new AreaDAO();

		try {
			oCargoTO.setIdCargo(cargo);
			swCargo = oCargoDAO.cargar(oCargoTO);

			// si no exste el cargo
			if (!swCargo) {
				// Buscamos el area Global para introducir el nuevo cargo

				// Buscamos el area, e tal caso que no exista el area gobal, la
				// insertamos
				oAreaTO.setIdarea(area);
				swArea = oAreaDAO.cargar(oAreaTO);

				if (!swArea) {
					// Si no xiste el area, la introducimos
					oAreaTO.setArea(area);
					oAreaDAO.insertar(oAreaTO);
				}
				oCargoTO.setIdArea(oAreaTO.getIdarea());
				oCargoTO.setCargo(cargo);
				oCargoTO.setIdArea(oAreaTO.getIdarea());
				oCargoTO.setActivec(String.valueOf(Constants.permission));
				oCargoDAO.insertar(oCargoTO);

			}

			StringBuffer sql = new StringBuffer("update person set cargo='").append(oCargoTO.getIdCargo()).append("'");
			sql.append(" where cargo='").append(cargo).append("'");

			JDBCUtil.executeUpdate(sql);

		} catch (Exception e) {
			e.printStackTrace();
			// System.out.println(e);
		}

		return oCargoTO.getIdCargo();
	}

	public static String getCargoAndArea(String cargo, boolean swCargo) {

		String cargo2 = "";
		// Coprobamos qu exista elcargo , sino existe,creamos un area global
		// y dentro de ella colocamos su cargo

		CargoTO oCargoTO = new CargoTO();
		AreaTO oAreaTO = new AreaTO();

		CargoDAO oCargoDAO = new CargoDAO();
		AreaDAO oAreaDAO = new AreaDAO();

		oCargoTO.setIdCargo(cargo);

		// System.out.println("...cargo=" + cargo);
		try {

			if (oCargoDAO.cargar(oCargoTO)) {
				if (swCargo) {
					cargo2 = oCargoTO.getCargo();
				} else {
					oAreaTO.setIdarea(oCargoTO.getIdArea());
					if (oAreaDAO.cargar(oAreaTO)) {
						cargo2 = oAreaTO.getArea();
					}
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return cargo2;
	}

	public static String getAreaFromUser(String cargo, boolean swCargo) {

		String cargo2 = "";
		// Coprobamos qu exista elcargo , sino existe,creamos un area global
		// y dentro de ella colocamos su cargo

		CargoTO oCargoTO = new CargoTO();
		AreaTO oAreaTO = new AreaTO();

		CargoDAO oCargoDAO = new CargoDAO();
		AreaDAO oAreaDAO = new AreaDAO();

		oCargoTO.setIdCargo(cargo);

		// System.out.println("...cargo=" + cargo);
		try {

			if (oCargoDAO.cargar(oCargoTO)) {
				if (swCargo) {
					cargo2 = oCargoTO.getCargo();
				} else {
					oAreaTO.setIdarea(oCargoTO.getIdArea());
					if (oAreaDAO.cargar(oAreaTO)) {
						cargo2 = oAreaTO.getArea();
					}
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return cargo2;
	}

	
	public static String getCargoAndArea(String cargo) {

		CargoTO oCargoTO = new CargoTO();
		AreaTO oAreaTO = new AreaTO();

		CargoDAO oCargoDAO = new CargoDAO();
		AreaDAO oAreaDAO = new AreaDAO();

		oCargoTO.setIdCargo(cargo);

		try {

			if (oCargoDAO.cargar(oCargoTO)) {

				oAreaTO.setIdarea(oCargoTO.getIdArea());
				oAreaDAO.cargar(oAreaTO);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		StringBuffer cargo_area = new StringBuffer(oCargoTO.getCargo()).append(" [").append(oAreaTO.getArea()).append("]");

		return cargo_area.toString();
	}

	public static Collection getAllUsersFilter(String user, String idarea, boolean showViewer) throws Exception {
		Vector resp = new Vector();
		StringBuilder query = new StringBuilder(50);
		query.append("SELECT p.idPerson,nameUser,idGrupo, ");
		switch (Constants.MANEJADOR_ACTUAL) {
		case Constants.MANEJADOR_MSSQL:
			query.append("(p.Apellidos + ' ' + p.Nombres) AS nombre, ");
			break;
		case Constants.MANEJADOR_POSTGRES:
			query.append("(p.Apellidos || ' ' || p.Nombres) AS nombre, ");
			break;
		case Constants.MANEJADOR_MYSQL:
			query.append("CONCAT(p.Apellidos , ' ' , p.Nombres) AS nombre, ");
			break;
		}
		query.append("g.cargo");
		query.append(" FROM person p,tbl_cargo g WHERE accountActive = '1' and nameUser!='").append(Constants.ID_USER_TEMPORAL).append("'");
		query.append(" and  cast(p.cargo as int)=g.idcargo  ");
		if (!ToolsHTML.isEmptyOrNull(user)) {
			query.append(" AND p.nameUser <> '").append(user).append("'");
		}
		if (!ToolsHTML.isEmptyOrNull(idarea)) {
			// select * from tbl_cargo where idarea in (select idarea from
			// tbl_cargo where idcargo=1)
			// query.append(" AND p.cargo = '").append(cargo).append("'");
			query.append(" AND cast(p.cargo as int) ");
			// query.append(" in (select idcargo from tbl_cargo where idarea in (select idarea from tbl_cargo where idcargo=");
			query.append(" in (select idcargo from tbl_cargo where idarea=").append(idarea).append(") ");
		}
		if (!showViewer) {
			query.append("AND idGrupo!=").append(Constants.ID_GROUP_VIEWER).append(" ");
		}
		query.append(" order by lower(Apellidos) asc ");
		// System.out.println("query=" + query.toString());
		Vector datos = JDBCUtil.doQueryVector(query.toString(),Thread.currentThread().getStackTrace()[1].getMethodName());
		String idPerson;
		String idGrupo;
		String userName;
		for (int row = 0; row < datos.size(); row++) {
			Properties properties = (Properties) datos.elementAt(row);
			userName = properties.getProperty("nameUser");
			idGrupo = properties.getProperty("idGrupo");
			idPerson = properties.getProperty("idPerson");
			Search bean = new Search(userName, properties.getProperty("nombre"));
			bean.setAditionalInfo(properties.getProperty("cargo"));
			resp.add(bean);
		}
		return resp;
	}

	public synchronized static Collection getAllUsersFilter(String user, boolean showcargo) throws Exception {
		Vector resp = new Vector();
		StringBuilder query = new StringBuilder(50);
		query.append("SELECT p.idPerson,nameUser,idGrupo, ");
		switch (Constants.MANEJADOR_ACTUAL) {
		case Constants.MANEJADOR_MSSQL:
			query.append("(p.Apellidos + ' ' + p.Nombres) AS nombre, ");
			break;
		case Constants.MANEJADOR_POSTGRES:
			query.append("(p.Apellidos || ' ' || p.Nombres) AS nombre, ");
			break;
		case Constants.MANEJADOR_MYSQL:
			query.append("CONCAT(p.Apellidos , ' ' , p.Nombres) AS nombre, ");
			break;
		}
		query.append("c.cargo");
		query.append(" FROM person p,tbl_cargo c,tbl_area a WHERE accountActive = '1' ");
		query.append(" AND c.idcargo=cast(p.cargo as int) and c.idarea=a.idarea ");
		if (!ToolsHTML.isEmptyOrNull(user)) {
			query.append(" AND p.nameUser <> '").append(user).append("'");
		}
		if (!showcargo)
			query.append(" order by lower(p.apellidos) asc");
		else
			query.append(" order by lower(c.cargo) asc");
		// System.out.println("[getAllUsersFilterAccion]=" + query.toString());
		Vector datos = JDBCUtil.doQueryVector(query.toString(),Thread.currentThread().getStackTrace()[1].getMethodName());
		String idPerson;
		String idGrupo;
		String userName;
		for (int row = 0; row < datos.size(); row++) {
			Properties properties = (Properties) datos.elementAt(row);
			userName = properties.getProperty("nameUser");
			idGrupo = properties.getProperty("idGrupo");
			idPerson = properties.getProperty("idPerson");
			Search bean = null;
			if (!showcargo) {
				bean = new Search(userName, properties.getProperty("nombre"));
				bean.setAditionalInfo(properties.getProperty("cargo"));
			} else {
				bean = new Search(userName, properties.getProperty("cargo"));
				bean.setAditionalInfo(properties.getProperty("nombre"));
			}
			resp.add(bean);
		}
		return resp;
	}

	public static Collection getAllUsersFilter(String user) throws Exception {
		Vector resp = new Vector();
		StringBuilder query = new StringBuilder(50);
		query.append("SELECT p.idPerson,nameUser,idGrupo, ");
		switch (Constants.MANEJADOR_ACTUAL) {
		case Constants.MANEJADOR_MSSQL:
			query.append("(p.Apellidos + ' ' + p.Nombres) AS nombre, ");
			break;
		case Constants.MANEJADOR_POSTGRES:
			query.append("(p.Apellidos || ' ' || p.Nombres) AS nombre, ");
			break;
		case Constants.MANEJADOR_MYSQL:
			query.append("CONCAT(p.Apellidos , ' ' , p.Nombres) AS nombre, ");
			break;
		}
		query.append(" p.cargo");
		query.append(" FROM person p WHERE accountActive = '1' ");
		if (!ToolsHTML.isEmptyOrNull(user)) {
			query.append(" AND p.nameUser <> '").append(user).append("'");
		}
		query.append(" order by lower(Apellidos) asc ");
		Vector datos = JDBCUtil.doQueryVector(query.toString(),Thread.currentThread().getStackTrace()[1].getMethodName());
		String idPerson;
		String idGrupo;
		String userName;
		for (int row = 0; row < datos.size(); row++) {
			Properties properties = (Properties) datos.elementAt(row);
			userName = properties.getProperty("nameUser");
			idGrupo = properties.getProperty("idGrupo");
			idPerson = properties.getProperty("idPerson");
			Search bean = new Search(userName, properties.getProperty("nombre"));
			bean.setAditionalInfo(properties.getProperty("cargo"));
			resp.add(bean);
		}
		return resp;
	}

	public static Collection<Search> getAllUsers(Hashtable securityUser, Hashtable securityGroups, String owner) throws Exception {
		Vector resp = new Vector();
		StringBuilder query = new StringBuilder("SELECT nameUser, ");
		switch (Constants.MANEJADOR_ACTUAL) {
		case Constants.MANEJADOR_MSSQL:
			query.append(" (p.Apellidos + ' ' + p.nombres) AS nombre, ");
			break;
		case Constants.MANEJADOR_POSTGRES:
			query.append(" (p.Apellidos || ' ' || p.nombres) AS nombre, ");
			break;
		case Constants.MANEJADOR_MYSQL:
			query.append(" CONCAT(p.Apellidos , ' ' , p.nombres) AS nombre, ");
			break;
		}
		query.append(" idGrupo,cargo FROM person p");
		query.append(" WHERE accountActive = '1' order by lower(p.apellidos) asc, lower(p.nombres) asc ");
		Vector datos = JDBCUtil.doQueryVector(query.toString(),Thread.currentThread().getStackTrace()[1].getMethodName());
		for (int row = 0; row < datos.size(); row++) {
			Properties properties = (Properties) datos.elementAt(row);
			String userName = properties.getProperty("nameUser");
			Search bean = new Search(userName, properties.getProperty("nombre"));
			bean.setAditionalInfo(properties.getProperty("cargo"));
			resp.add(bean);
		}
		return resp;
	}

	public static String getNameUser(String nameUser) throws Exception {
		StringBuilder query = new StringBuilder("SELECT ");
		switch (Constants.MANEJADOR_ACTUAL) {
		case Constants.MANEJADOR_MSSQL:
			query.append(" (p.Apellidos + ' ' + p.nombres) AS nombre ");
			break;
		case Constants.MANEJADOR_POSTGRES:
			query.append(" (p.Apellidos || ' ' || p.nombres) AS nombre ");
			break;
		case Constants.MANEJADOR_MYSQL:
			query.append(" CONCAT(p.Apellidos , ' ' , p.nombres) AS nombre ");
			break;
		}
		query.append(" FROM person p");
		query.append(" WHERE nameUser='").append(nameUser).append("'");
		query.append(" ORDER BY idPerson DESC ");
		Properties prop = JDBCUtil.doQueryOneRow(query.toString(),Thread.currentThread().getStackTrace()[1].getMethodName());
		if (prop.getProperty("isEmpty").equalsIgnoreCase("false")) {
			return prop.getProperty("nombre");
		}
		return null;
	}

	public static String getNameUserOnly(String id) throws Exception {
		StringBuilder query = new StringBuilder("SELECT nameUser ");
		query.append(" FROM person p");
		query.append(" WHERE idPerson='").append(id).append("'");
		Properties prop = JDBCUtil.doQueryOneRow(query.toString(),Thread.currentThread().getStackTrace()[1].getMethodName());
		if (prop.getProperty("isEmpty").equalsIgnoreCase("false")) {
			return prop.getProperty("nameUser");
		}
		return null;
	}

	public static int getIdPerson(String nameUser) throws Exception {
		StringBuilder query = new StringBuilder("SELECT idPerson ");
		query.append(" FROM person p");
		query.append(" WHERE nameUser='").append(nameUser).append("'");
		Properties prop = JDBCUtil.doQueryOneRow(query.toString(),Thread.currentThread().getStackTrace()[1].getMethodName());
		if (prop.getProperty("isEmpty").equalsIgnoreCase("false")) {
			return ToolsHTML.parseInt(prop.getProperty("idPerson"));
		}
		return 0;
	}

	public static String getNameUserForMail(String email, String nameUser) throws Exception {
		StringBuilder query = new StringBuilder("SELECT ");
		switch (Constants.MANEJADOR_ACTUAL) {
		case Constants.MANEJADOR_MSSQL:
			query.append(" (p.Apellidos + ' ' + p.nombres) AS nombre ");
			break;
		case Constants.MANEJADOR_POSTGRES:
			query.append(" (p.Apellidos || ' ' || p.nombres) AS nombre ");
			break;
		case Constants.MANEJADOR_MYSQL:
			query.append(" CONCAT(p.Apellidos , ' ' , p.nombres) AS nombre ");
			break;
		}
		query.append(" FROM person p");
		query.append(" WHERE email='").append(email).append("'");
		query.append(" AND accountActive = '").append(Constants.permission).append("' ");
		// log.debug("[getNameUserForMail] = " + query.toString());
		Properties prop = JDBCUtil.doQueryOneRow(query.toString(),Thread.currentThread().getStackTrace()[1].getMethodName());
		if (prop.getProperty("isEmpty").equalsIgnoreCase("false")) {
			return prop.getProperty("nombre");
		} else {
			switch (Constants.MANEJADOR_ACTUAL) {
			case Constants.MANEJADOR_MSSQL:
				query = new StringBuilder("SELECT (a.Apellido + ' ' + a.nombre) AS nombre");
				break;
			case Constants.MANEJADOR_POSTGRES:
				query = new StringBuilder("SELECT (a.Apellido || ' ' || a.nombre) AS nombre");
				break;
			case Constants.MANEJADOR_MYSQL:
				query = new StringBuilder("SELECT CONCAT(a.Apellido , ' ' , a.nombre) AS nombre");
				break;
			}
			query.append(" FROM address a,contacts c");
			query.append(" WHERE c.idAddress = a.idAddress");
			query.append(" AND c.idUser = '").append(nameUser).append("'");
			query.append(" AND a.email = '").append(email).append("'");
			prop = JDBCUtil.doQueryOneRow(query.toString(),Thread.currentThread().getStackTrace()[1].getMethodName());
			if (prop.getProperty("isEmpty").equalsIgnoreCase("false")) {
				return prop.getProperty("nombre");
			}
		}
		return null;
	}

	public static String getMail(String nameUser) throws Exception {
		StringBuilder query = new StringBuilder("SELECT email ");
		query.append(" FROM person p");
		query.append(" WHERE nameUser='").append(nameUser).append("'");
		query.append(" ORDER BY idPerson DESC ");
		Properties prop = JDBCUtil.doQueryOneRow(query.toString(),Thread.currentThread().getStackTrace()[1].getMethodName());
		if (prop.getProperty("isEmpty").equalsIgnoreCase("false")) {
			return prop.getProperty("email");
		}
		return null;
	}

	public synchronized static boolean insert(LoginUser forma) throws ApplicationExceptionChecked {
		PreparedStatement st = null;
		Connection con = null;
		ResultSet rs = null;
		boolean result = false;
		try {
			StringBuilder check = new StringBuilder(1024).append("SELECT nameUser FROM ").append(nameUser).append(" WHERE accountActive = '")
					.append(Constants.permission).append("'").append(" AND ( nameUser = ? OR email='").append(forma.getEmail()).append("')");

			con = JDBCUtil.getConnection(Thread.currentThread().getStackTrace()[1].getMethodName());
			st = con.prepareStatement(JDBCUtil.replaceCastMysql(check.toString()));
			st.setString(1, forma.getUser());
			rs = st.executeQuery();
			if (rs.next()) {
				throw new ApplicationExceptionChecked("E0038");
			}

			check.setLength(0);
			check.append("SELECT nameUser FROM ").append(nameUser).append(" WHERE nameUser = ? ");

			// con = JDBCUtil.getConnection(Thread.currentThread().getStackTrace()[1].getMethodName());
			st = con.prepareStatement(JDBCUtil.replaceCastMysql(check.toString()));
			st.setString(1, forma.getUser());
			rs = st.executeQuery();
			if (rs.next()) {
				throw new ApplicationExceptionChecked("E0124");
			}

			long idPerson = IDDBFactorySql.getNextIDLong("usuario");
			String idioma = HandlerParameters.PARAMETROS.getIdioma();
			StringBuilder insert = new StringBuilder("INSERT INTO ").append(nameUser)
					.append(" (idPerson,nameUser,clave,email,idGrupo,Nombres,Apellidos,cargo,idarea,IdLanguage,idNodeService)").append(" VALUES(?,?,?,?,?,?,?,?,?,?,?) ");
			con.setAutoCommit(false);
			// Se Crea el Nuevo Registro
			st = con.prepareStatement(JDBCUtil.replaceCastMysql(insert.toString()));
			st.setLong(1, idPerson);
			st.setString(2, forma.getUser());
			st.setString(3, ToolsHTML.encripterPass(forma.getClave()));
			st.setString(4, forma.getEmail());
			st.setInt(5, Integer.parseInt(forma.getGrupo().trim()));
			st.setString(6, forma.getNombres());
			st.setString(7, forma.getApellidos());
			st.setString(8, forma.getCargo());
			st.setInt(9, Integer.parseInt(forma.getArea()));
			st.setString(10, idioma.trim());
			st.setInt(11,forma.getIdNodeService());
			SeguridadUserForm securityUser = new SeguridadUserForm(forma.getUser(), true);
			securityUser.setIdPerson(idPerson);
			HandlerSeguridad.insertUserSecurity(securityUser, con);
			st.executeUpdate();
			con.commit();
			result = true;
			
			// Ticket Administracion- Agregar nueva cuenta de usuario.
			// nuevo funcionalidad de envio de email a usuario nuevo
			
		
			 ModuloBean mod = new ModuloBean();
			

			try {
				// le enviamos el email al  USUARIO nuevo 
				Locale defaultLocale = new Locale(DesigeConf.getProperty("language.Default"), DesigeConf.getProperty("country.Default"));
				String empresa = mod.getEmpresa();
				ResourceBundle rb = ResourceBundle.getBundle("LoginBundle", defaultLocale);
				
				StringBuilder msg = new StringBuilder(2048).append(rb.getString("mail.MsgNewUserRegister").replaceAll("-EMPRESA-",empresa));

				MailForm formaMail = new MailForm();
				
				formaMail.setFrom(forma.getEmail());
				formaMail.setNameFrom(rb.getString("mail.system"));
				formaMail.setTo(formaMail.getFrom());
				formaMail.setSubject(rb.getString("mail.MsgNewUserRegisterTitle")+empresa);
				formaMail.setMensaje(msg.toString());
				
				if (formaMail != null) {
					SendMailTread mail = new SendMailTread(formaMail);
					mail.start();
				}
			} catch (Exception ex) {
				log.debug("Exception");
				log.error(ex.getMessage());
				ex.printStackTrace();
			}
			// FIN AQUI DE EMAIL
			
		} catch (ApplicationExceptionChecked ae) {
			if ("E0038".compareTo(ae.getMessage()) != 0 && "E0124".compareTo(ae.getMessage()) != 0) {
				applyRollback(con);
			}
			throw ae;
		} catch (Exception e) {
			applyRollback(con);
			setMensaje(e.getMessage());
			result = false;
			e.printStackTrace();
		} finally {
			setFinally(con, st, rs);
		}
		return result;
	}

	/**
	 *
	 * @param idoldOwner
	 * @param idnewUser
	 * @throws SQLException
	 */
	public synchronized static void cambiarUsuariosSacop(String idoldOwner, String idnewUser) throws SQLException {

		PlanillaSacop1TO oPlanillaSacop1TO = new PlanillaSacop1TO();

		Connection con = null;
		
		try {

			// iniciamos la transaccion
			con = JDBCUtil.getConnection(Thread.currentThread().getStackTrace()[1].getMethodName());
			con.setAutoCommit(false);

			PlanillaSacop1DAO oPlanillaSacop1DAO = new PlanillaSacop1DAO(con);

			StringBuffer queryStr = new StringBuffer("SELECT * FROM tbl_planillasacop1 ").append(" where estado not in (").append(LoadSacop.edoCerrado)
					.append(",").append(LoadSacop.edoRechazado).append(")");

			CachedRowSet crs = JDBCUtil.executeQuery(queryStr, null, con, Thread.currentThread().getStackTrace()[1].getMethodName());

			while (crs.next()) {
				// Plantilla1BD oPlanillaSacop1TO =
				oPlanillaSacop1DAO.load(crs, oPlanillaSacop1TO);
				

				// actualizamos el emisor por el nuevo a sustituir
				if (String.valueOf(oPlanillaSacop1TO.getEmisor()).equalsIgnoreCase(idoldOwner)) {
					oPlanillaSacop1TO.setEmisor(idnewUser);
				}
				// actualizamos el responsable por el nuevo a sustituir
				if (String.valueOf(oPlanillaSacop1TO.getRespblearea()).equalsIgnoreCase(idoldOwner)) {
					oPlanillaSacop1TO.setRespblearea(idnewUser);
				}
				// actualizamos el informa a por el nuevo a sustituir
				StringTokenizer h = new StringTokenizer(oPlanillaSacop1TO.getSolicitudinforma(), ",");
				Hashtable hash = new Hashtable();
				while (h.hasMoreTokens()) {
					String u = h.nextToken();
					hash.put(u, u);
				}
				if (hash.containsKey(idoldOwner)) {
					hash.remove(idoldOwner);
				}
				Enumeration it = hash.keys();
				StringBuilder nuevoInformaA = new StringBuilder("");
				while (it.hasMoreElements()) {
					String ele = (String) it.nextElement();
					nuevoInformaA.append(ele);
					if (it.hasMoreElements()) {
						nuevoInformaA.append(",");
					}
				}
				oPlanillaSacop1TO.setSolicitudinforma(nuevoInformaA.toString());

				oPlanillaSacop1DAO.actualizar(oPlanillaSacop1TO);
			}
			con.commit(); // hacemos el commit
		} catch (Exception e) {
			con.rollback(); // hacemos el rollback
			e.printStackTrace();
		} finally {
			con.close(); // cerramos la conexion
		}
	}

	/**
	 *
	 * @param oldOwner
	 * @param newUser
	 * @return
	 */
	private static String getUpdatetabladocCheckOut(String oldOwner, String newUser) {
		// aqui se modifico el ticket el nombre de la tabla debe ser minuscula *error 1**
		// EL NOMBRE DE LA TABLE ******* StringBuilder sql = new StringBuilder("UPDATE  docCheckOut set checkOutBY = '");
		StringBuilder sql = new StringBuilder("UPDATE  doccheckout set checkOutBY = '");
		sql.append(newUser).append("'");
		sql.append(" where  checkOutBY='").append(oldOwner).append("'");
		log.debug("[getUpdatetabladocCheckOut]" + sql.toString());
		return sql.toString();
	}

	// private static String getUpdatetabladocCheckOut(String oldOwner,String
	// newUser) {
	// StringBuilder sql = new
	// StringBuilder("UPDATE docCheckOut set checkOutBY = '");
	// sql.append(newUser).append("'");
	// sql.append(" where checkOutBY=(select nameuser from person where idPerson =").append(oldOwner).append(")");
	// log.debug("[getUpdatetabladocCheckOut]"+sql.toString());
	// return sql.toString();
	// }

	private static String getUpdateidUserUser_FlexWorkFlows(String oldOwner, String newUser) {
		StringBuilder sql = new StringBuilder("UPDATE  user_flexworkflows set idUser = '");
		sql.append(newUser).append("'");
		sql.append(" where  idUser='").append(oldOwner).append("'");
		sql.append(" and statu not in ('")
				.append(HandlerWorkFlows.wfuAcepted + "','" + HandlerWorkFlows.canceled + "','" + HandlerWorkFlows.wfuClosed + "','"
						+ HandlerWorkFlows.wfurechazado + "','" + HandlerWorkFlows.ownercancelcloseflow).append("')");
		log.debug("[getUpdateidUserUser_FlexWorkFlows]" + sql.toString());	
		return sql.toString();
	}

	private static String getUpdateidUserUser_WorkFlows(String oldOwner, String newUser) {
		StringBuilder sql = new StringBuilder("UPDATE  user_workflows set idUser = '");
		sql.append(newUser).append("'");
		sql.append(" where  idUser='").append(oldOwner).append("'");
		sql.append(" and statu not in ('")
				.append(HandlerWorkFlows.wfuAcepted + "','" + HandlerWorkFlows.canceled + "','" + HandlerWorkFlows.wfuClosed + "','"
						+ HandlerWorkFlows.wfurechazado + "','" + HandlerWorkFlows.ownercancelcloseflow).append("')");
		log.debug("[getUpdateidUserUser_WorkFlows]" + sql.toString());
		return sql.toString();
	}

	// private static String getUpdateidUserUser_WorkFlows(String
	// oldOwner,String newUser) {
	// StringBuilder sql = new
	// StringBuilder("UPDATE user_workflows SET idUser = '");
	// sql.append(newUser).append("'");
	// sql.append(" WHERE idUser = (SELECT nameuser FROM person WHERE idPerson = ").append(oldOwner).append(")");
	// sql.append(" AND statu IN(").append(HandlerWorkFlows.wfuPending).append(",").append(HandlerWorkFlows.wfuQueued);
	// sql.append(")");
	// log.debug("[getUpdateidUserUser_WorkFlows]"+sql.toString());
	// return sql.toString();
	// }
	private static String getDeleteUser_Doc(String oldOwner) {
		StringBuilder sqlDelete = new StringBuilder("");
		sqlDelete.append("delete from permisiondocuser where iduser =");
		sqlDelete.append("'").append(oldOwner).append("'");
		log.debug("[getDeleteUser_Doc]" + sqlDelete.toString());
		return sqlDelete.toString();
	}

	private static String getUpdateidUserUser_Struct(String oldOwner, String newUser) {
		StringBuilder sql = new StringBuilder("UPDATE  struct set owner = '");
		sql.append(newUser).append("'");
		sql.append(" where  owner='").append(oldOwner).append("'");
		log.debug("[getUpdateidUserUser_Struct]" + sql.toString());
		return sql.toString();
	}
	//ydavila Ticket 001-00-003023 - tabla STRUCT - RESPONSABLE SOLICITUD DE CAMBIO
	private static String getUpdateidrespsolcambioUser_Struct(String oldOwner, String newrespsolcambio) {
		StringBuffer sql = new StringBuffer("UPDATE  struct set respsolcambio = '");
		sql.append(newrespsolcambio).append("'");
		sql.append(" where  respsolcambio='").append(oldOwner).append("'");
		log.debug("[getUpdateidRespSolCambio_Struct]" + sql.toString());
		return sql.toString();
	}
	//ydavila Ticket 001-00-003023 - tabla STRUCT - RESPONSABLE ELIMINACI�N
	private static String getUpdateidrespsoleliminUser_Struct(String oldOwner, String newrespsolelimin) {
		StringBuffer sql = new StringBuffer("UPDATE  struct set respsolelimin = '");
		sql.append(newrespsolelimin).append("'");
		sql.append(" where  respsolelimin='").append(oldOwner).append("'");
		log.debug("[getUpdateidRespSolElimin_Struct]" + sql.toString());
		return sql.toString();
	}
	//ydavila Ticket 001-00-003023 - tabla STRUCT - RESPONSABLE IMPRESI�N
	private static String getUpdateidrespsolimpresUser_Struct(String oldOwner, String newrespsolimpres) {
		StringBuffer sql = new StringBuffer("UPDATE  struct set respsolimpres = '");
		sql.append(newrespsolimpres).append("'");
		sql.append(" where  respsolimpres='").append(oldOwner).append("'");
		log.debug("[getUpdateidrespsolimpres_Struct]" + sql.toString());
		return sql.toString();
	}


	private static void getUpdatePermisoStructUser_Struct(Connection con, PreparedStatement st, String oldOwner, String newUser, String idnewUser) {

		try {
			// Buscamos la seguridad de struc del usuario a elimnar y cuya estructura no este al usuario
			// que va a ser su sustituto
			StringBuffer sql1 = new StringBuffer("");
			sql1.append("Select idStruct ");
			sql1.append("from permissionstructuser p ");
			sql1.append("where p.idperson =").append(oldOwner);
			sql1.append("  and p.idStruct not in ");
			sql1.append("  (select idStruct from  permissionstructuser p2 where p2.idperson =");
			sql1.append("").append(idnewUser).append("");
			sql1.append(")");

			StringBuilder sql = new StringBuilder("");
			sql.append("update permissionstructuser ");
			sql.append(" set idperson=").append(idnewUser).append(" ");
			sql.append(" where idperson=").append(oldOwner).append(" and ");
			sql.append(" idStruct in (").append(JDBCUtil.executeQueryRetornaIds(sql1)).append(")");
			// System.out.println("sql=" + sql.toString());
			st = con.prepareStatement(JDBCUtil.replaceCastMysql(sql.toString()));
			st.executeUpdate();

		} catch (Exception e) {
			e.printStackTrace();
			// System.out.println("e=" + e);
		}

	}

	// private static String getUpdateidUserUser_tblsolicitudimpresion(String
	// oldOwner,String newUser) {
	// StringBuilder sql = new
	// StringBuilder("UPDATE tbl_solicitudimpresion set statusimpresion =").append(loadsolicitudImpresion.canceladoprintln);
	// sql.append(" where solicitante=").append(oldOwner);
	// log.debug("[getUpdateidUserUser_tblsolicitudimpresion]"+sql.toString());
	// return sql.toString();
	// }

	private static String getUpdateOwnerFlexWorkFlows(String oldOwner, String newUser) {
		StringBuilder sql = new StringBuilder("UPDATE  flexworkflow set owner='").append(newUser).append("'");
		sql.append(" where owner='").append(oldOwner).append("'");
		sql.append(" and statu not in ('")
				.append(HandlerWorkFlows.wfuAcepted + "','" + HandlerWorkFlows.canceled + "','" + HandlerWorkFlows.wfuClosed + "','"
						+ HandlerWorkFlows.wfurechazado + "','" + HandlerWorkFlows.ownercancelcloseflow).append("')");
		log.debug("[getUpdateOwnerFlexWorkFlows]" + sql.toString());
		return sql.toString();
	}

	private static String getUpdateOwnerWorkFlows(String oldOwner, String newUser) {
		StringBuilder sql = new StringBuilder("UPDATE  workflows set owner='").append(newUser).append("'");
		sql.append(" where owner='").append(oldOwner).append("'");
		sql.append(" and statu not in ('")
				.append(HandlerWorkFlows.wfuAcepted + "','" + HandlerWorkFlows.canceled + "','" + HandlerWorkFlows.wfuClosed + "','"
						+ HandlerWorkFlows.wfurechazado + "','" + HandlerWorkFlows.ownercancelcloseflow).append("')");
		log.debug("[getUpdateOwnerWorkFlows]" + sql.toString());
		return sql.toString();
	}

	private static void getSearchOwnerDocuments(Connection con, PreparedStatement st, long user, String newUser, boolean active, HttpSession sessionn,
			Users usuario, boolean sweliminar) {
		Locale defaultLocale = new Locale(DesigeConf.getProperty("language.Default"), DesigeConf.getProperty("country.Default"));
		ResourceBundle rb = ResourceBundle.getBundle("LoginBundle", defaultLocale);
		StringBuilder sql1 = new StringBuilder();
		sql1.append("select p.email,p.nombres,p.apellidos,d.owner,d.number,d.numgen,d.namedocument,d.idnode");
		sql1.append(" ,d.prefix from documents d,person p where ");
		sql1.append(" d.owner = ").append("(select nameuser from person where idPerson = ").append(user).append(")");
		sql1.append(" and d.owner=p.nameuser");
		// System.out.println("sql1=" + sql1.toString());
		log.debug("[getSearchOwnerDocuments]" + sql1.toString());
		try {
			StringBuilder comentariosMail = new StringBuilder("");
			StringBuilder soloComentariosDatosDocumento = new StringBuilder("");
			java.util.Date date = new java.util.Date();
			Timestamp time = new Timestamp(date.getTime());
			st = con.prepareStatement(JDBCUtil.replaceCastMysql(sql1.toString()));
			String emailOld = "";
			String nomOld = "";
			ResultSet rs;
			rs = st.executeQuery();
			Hashtable prefijos = new Hashtable();

			int i = 0;
			while (rs.next()) {
				// solo se recoje una sola vez
				if ("".equalsIgnoreCase(emailOld)) {
					emailOld = rs.getString("email");
				}
				if ("".equalsIgnoreCase(nomOld)) {
					nomOld = rs.getString("nombres");
					nomOld = nomOld + "  " + rs.getString("apellidos");
				}
				String nodo = rs.getString("idnode") != null ? rs.getString("idnode") : "";
				String PrefixNumber = "";
				if (prefijos.containsKey(nodo)) {
					PrefixNumber = (String) prefijos.get(nodo);
				} else {
					PrefixNumber = ToolsHTML.getPrefixToDoc(sessionn, usuario, nodo);
					prefijos.put(nodo, PrefixNumber);
				}
				if (ToolsHTML.isEmptyOrNull(PrefixNumber)) {
					PrefixNumber = "";
				}

				String number = rs.getString("number");
				String numgen = rs.getString("numgen");
				String nomDocument = rs.getString("namedocument");
				String owner = rs.getString("owner");

				// estos comentarios van al history docs tabla.................
				StringBuilder comentarios = new StringBuilder("");
				comentarios.append(rb.getString("crg.comentario1")).append(" ").append(nomDocument);
				comentarios.append(" ").append(PrefixNumber).append(number).append("<br>");
				comentarios.append(rb.getString("crg.comentario2")).append(" ").append(owner).append("<br>");
				comentarios.append(rb.getString("crg.comentario3")).append(" ").append(newUser).append("<br>");
				comentarios.append(rb.getString("crg.comentario5")).append(" ").append("<br>");
				String valorProperties = "";
				if (!sweliminar) {
					valorProperties = "12";
				} else {
					valorProperties = "13";
				}
				// el valorProperties es cambio de cargo en los properties
				// (cbs.historyType)
				HandlerDocuments.updateHistoryDocs(con, Integer.parseInt(numgen.toLowerCase()), 0, 0, newUser, time, valorProperties, comentarios.toString(),
						new String[] { "", "" });
				// estos comentarios van al history docs tabla.................
				// este comentario va al mail principal
				soloComentariosDatosDocumento.append(nomDocument).append(" ").append(PrefixNumber).append(number).append("<br>");

			}
			rs.close();

			if (nomOld.trim().equals("")) {
				sql1.setLength(0);
				sql1.append("select email,nombres,apellidos from person where idPerson = ").append(user);
				st = con.prepareStatement(JDBCUtil.replaceCastMysql(sql1.toString()));
				rs = st.executeQuery();
				if (rs.next()) {
					nomOld = rs.getString("nombres");
					nomOld = nomOld + "  " + rs.getString("apellidos");
					emailOld = rs.getString("email");
				}
				rs.close();
			}

			String email = "";
			String nombres = "";
			String apellidos = "";
			String values[] = HandlerDocuments.getFields(new String[] { "email", "nombres", "apellidos" }, "person", "nameUser", "'" + String.valueOf(newUser) + "'");

			if (values != null) {
				email = values[0];
				nombres = values[1];
				apellidos = values[2];

			}

			// el duenio anterior nomold
			// el nuevo duenio nombres + apellidos
			String cabezeraStr = rb.getString("crg.comentario2") + " " + nomOld + " " + "<br>";
			cabezeraStr = cabezeraStr + rb.getString("crg.comentario3") + " " + nombres + " " + apellidos + "<br>";

			// Se le notifica al duenio oiginal y al nuevo del documento del cambio de documentos al nuevo usuario
			comentariosMail.append(rb.getString("crg.comentario1")).append(" ").append(soloComentariosDatosDocumento.toString()).append("<br>");
			comentariosMail.append("");
			HandlerWorkFlows.notifiedUsers(rb.getString("crg.sendmail"), rb.getString("mail.nameUser"),
					HandlerParameters.PARAMETROS.getMailAccount(), HandlerParameters.PARAMETROS.getMailAccount()
							+ ";" + emailOld.toString() + ";" + email.toString(), cabezeraStr + "<br><br>" + comentariosMail.toString());

			String mensjFinal = "";

			if (!sweliminar) {
				// Se le notifica al administrador el usuario fue eliminado
				mensjFinal = rb.getString("crg.comentario4");
			} else {
				// Se le notifica al administrador colocar nueva permisologia al
				// nuevo usuario
				mensjFinal = rb.getString("user.elim3");
			}

			HandlerWorkFlows.notifiedUsers(rb.getString("crg.sendmail"), rb.getString("mail.nameUser"),
					HandlerParameters.PARAMETROS.getMailAccount(), HandlerParameters.PARAMETROS.getMailAccount()
							+ ";" + emailOld.toString(), mensjFinal + " " + nomOld.toString() + " " + emailOld.toString());

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static String getUpdateOwnerDocuments(String user, String newUser, boolean active) {
		StringBuilder sql = new StringBuilder("UPDATE documents SET owner = '");
		sql.append(newUser).append("'").append(" WHERE owner = '").append(user).append("'");
		log.debug("[getUpdateOwnerDocuments]" + sql.toString());
		return sql.toString();
	}

	private static void getUpdatePermisoDocuments(Connection con, PreparedStatement st, String user, String newUser, boolean active) {
		try {
			// Buscamos la seguridad de permisiondocuser del usuario a elimnar y
			// cuya estructura no este al usuario
			// que va a ser su sustituto
			StringBuffer sql = new StringBuffer("select ");
			sql.append(" idDocument ");
			sql.append(" ");
			sql.append(" from permisiondocuser p where p.iduser ='").append(user).append("'");
			sql.append("and p.iddocument not in (select iddocument from permisiondocuser where iduser =");
			sql.append("'").append(newUser).append("'").append(")");

			// actualizamos la permisologiadel nuevo usuario
			StringBuilder updatesql = new StringBuilder("update permisiondocuser ");
			updatesql.append(" set idUser='").append(newUser).append("'");
			updatesql.append(" where idUser='").append(user).append("'");
			updatesql.append(" and idDocument in (").append(JDBCUtil.executeQueryRetornaIds(sql)).append(")");
			// System.out.println("updatesql=" + updatesql.toString());
			st = con.prepareStatement(JDBCUtil.replaceCastMysql(updatesql.toString()));
			st.executeUpdate();

		} catch (Exception e) {
			e.printStackTrace();
			// System.out.println(e);
		}
	}

	public static boolean getUserSacop(Long user) throws Exception {
		String query = "SELECT  emisor FROM tbl_planillasacop1 p";
		query += " WHERE p.respblearea = " + user + " or p.emisor=" + user;
		Vector datos = JDBCUtil.doQueryVector(query,Thread.currentThread().getStackTrace()[1].getMethodName());
		if (datos.size() > 0) {
			return true;
		}
		return false;
	}

	public synchronized static boolean deleteLogic(LoginUser forma, HttpSession sessionn, Users usuario) throws ApplicationExceptionChecked {
		// eliminamos a los administradores tambien, el sistema se asegura que
		// por lo menos un administrador quede
		/*
		 * if (forma!=null&&forma.getUser().equalsIgnoreCase(DesigeConf.getProperty ("application.userAdmon"))) { throw new
		 * ApplicationExceptionChecked("E0036"); }
		 */
		Connection con = null;
		PreparedStatement st = null;
		boolean result = false;
		try {
			// sustituimos el usuario del flujo del trabajo por el suario que se
			// escojio para ser remplazado
			/*
			 * if (HandlerWorkFlows.isUserInWF(forma.getUser())) { throw new ApplicationExceptionChecked("E0037"); }
			 */
			con = JDBCUtil.getConnection(Thread.currentThread().getStackTrace()[1].getMethodName());
			boolean eliminar = true;
			con.setAutoCommit(false);
			actualizaOwnerInWebDocuments(con, st, forma, eliminar, sessionn, usuario);
			con.commit();
			result = true;
		} catch (ApplicationExceptionChecked ae) {
			applyRollback(con);
			throw ae;
		} catch (Exception e) {
			e.printStackTrace();
			applyRollback(con);
			result = false;
		} finally {
			setFinally(con, st);
		}
		return result;
	}

	public static void actualizaOwnerInWebDocuments(Connection con, PreparedStatement st, LoginUser forma, boolean swEliminar, HttpSession sessionn,
			Users usuario) throws Exception {
		Locale defaultLocale = new Locale(DesigeConf.getProperty("language.Default"), DesigeConf.getProperty("country.Default"));
		ResourceBundle rb = ResourceBundle.getBundle("LoginBundle", defaultLocale);
		
		boolean isGestionadores = false;
		boolean isResSolCambio = false;
		boolean isResSolElimin = false;
		boolean isResSolImpres = false;

		try {
			//System.out.println("forma.getIdPerson()=" + forma.getIdPerson());
			//System.out.println("forma.getUseremplazo()=" + forma.getUseremplazo());
			String idUserRemplazo = null;
			String nameUserOld = null;
			//ydavila Ticket 001-00-003023 sustituyo responsable de solicitudes de cambio
			String namerespsolcambioOld = null;
			String nameUserNew = null;
			//ydavila Ticket 001-00-003023 sustituyo responsable de solicitudes de cambio
			String namerespsoleliminOld = null;
			nameUserNew = null;
			//ydavila Ticket 001-00-003023 sustituyo responsable de solicitudes de cambio
			String namerespsolimpresOld = null;
			nameUserNew = null;

			try {
				idUserRemplazo = HandlerDocuments.getField("idperson", "person", "nameUser", forma.getUseremplazo(), "=", 1,Thread.currentThread().getStackTrace()[1].getMethodName());
				nameUserOld = HandlerDocuments.getField("nameUser", "person", "idperson", String.valueOf(forma.getIdPerson()), "=", 1,Thread.currentThread().getStackTrace()[1].getMethodName());
				nameUserNew = HandlerDocuments.getField("nameUser", "person", "idperson", String.valueOf(idUserRemplazo), "=", 1,Thread.currentThread().getStackTrace()[1].getMethodName());

				HandlerDBUser.addMessageToActivityProgress(rb.getString("userAdmin.getInfoFromUsers"));
			} catch (Exception e) {
				// TODO: handle exception
				HandlerDBUser.addMessageToActivityProgress("ERROR: " + rb.getString("userAdmin.getInfoFromUsers"));
				throw new Exception();
			}

			// ACTUALIZACIONES EN FLUJOS DE TRABAJO
			// actualizamos la tabla user_workflows con el nuevo usuario a
			// sustituir.
			// st =
			// con.prepareStatement(getUpdateidUserUser_WorkFlows(String.valueOf(forma.getIdPerson()),forma.getUseremplazo()));
			try {
				st = con.prepareStatement(JDBCUtil.replaceCastMysql(getUpdateidUserUser_WorkFlows(nameUserOld, forma.getUseremplazo())));
				st.executeUpdate();
				HandlerDBUser.addMessageToActivityProgress(rb.getString("userAdmin.updatePendingWorkFlows"));
			} catch (Exception e) {
				// TODO: handle exception
				HandlerDBUser.addMessageToActivityProgress("ERROR: " + rb.getString("userAdmin.updatePendingWorkFlows"));
				throw new Exception();
			}

			// actualizamos la tabla workflows con el nuevo usuario a sustituir.
			try {
				st = con.prepareStatement(JDBCUtil.replaceCastMysql(getUpdateOwnerWorkFlows(nameUserOld, forma.getUseremplazo())));
				st.executeUpdate();
				// HandlerDBUser.addMessageToActivityProgress(rb.getString("userAdmin.setNewOwnerWorkFlows"));
			} catch (Exception e) {
				HandlerDBUser.addMessageToActivityProgress("ERROR: " + rb.getString("userAdmin.setNewOwnerWorkFlows"));
				throw new Exception();
			}

			// ACTUALIZACIONES EN FTP (Flujo de Trabajo Parametrico)
			// actualizamos la tabla user_flexworkflows con el nuevo usuario a
			// sustituir.
			try {
				st = con.prepareStatement(JDBCUtil.replaceCastMysql(getUpdateidUserUser_FlexWorkFlows(nameUserOld, forma.getUseremplazo())));
				st.executeUpdate();
				// HandlerDBUser.addMessageToActivityProgress(rb.getString("userAdmin.updatePendingFlexWorkFlows"));
			} catch (Exception e) {
				// TODO: handle exception
				HandlerDBUser.addMessageToActivityProgress("ERROR: " + rb.getString("userAdmin.updatePendingFlexWorkFlows"));
				throw new Exception();
			}

			// actualizamos la tabla flexworkflow con el nuevo usuario a
			// sustituir.
			try {
				st = con.prepareStatement(JDBCUtil.replaceCastMysql(getUpdateOwnerFlexWorkFlows(nameUserOld, forma.getUseremplazo())));
				st.executeUpdate();
				// HandlerDBUser.addMessageToActivityProgress(rb.getString("userAdmin.setNewOwnerFlexWorkFlows"));
			} catch (Exception e) {
				// TODO: handle exception
				HandlerDBUser.addMessageToActivityProgress("ERROR: " + rb.getString("userAdmin.setNewOwnerFlexWorkFlows"));
				throw new Exception();
			}

			// Se sustituye usuario en las actividades relacionadas para flujos
			// de trabajo parametricos
			try {
				st = con.prepareStatement(JDBCUtil.replaceCastMysql(getUpdateActUserFTP(forma.getIdPerson(), Long.parseLong(idUserRemplazo))));
				st.executeUpdate();
				// HandlerDBUser.addMessageToActivityProgress(rb.getString("userAdmin.updatePendingParametricWorkFlows"));
				HandlerDBUser.addMessageToActivityProgress(rb.getString("userAdmin.setNewOwnerWorkFlows"));
			} catch (Exception e) {
				HandlerDBUser.addMessageToActivityProgress("ERROR: " + rb.getString("userAdmin.updatePendingParametricWorkFlows"));
				throw new Exception();
			}

			// actualizamos la tabla docCheckOut con el nuevo usuario a
			// sustituir.
			try {
				st = con.prepareStatement(JDBCUtil.replaceCastMysql(getUpdatetabladocCheckOut(nameUserOld, forma.getUseremplazo())));
				st.executeUpdate();
				HandlerDBUser.addMessageToActivityProgress(rb.getString("userAdmin.updateDocCheckOuts"));
			} catch (Exception e) {
				HandlerDBUser.addMessageToActivityProgress("ERROR: " + rb.getString("userAdmin.updateDocCheckOuts"));
				throw new Exception();
			}

			// antes de actualizar la tabla documentos, mandamos mails al viejo
			// y nuevo propietario
			// y lo registramosen el historico docs
			try {
				getSearchOwnerDocuments(con, st, forma.getIdPerson(), forma.getUseremplazo(), false, sessionn, usuario, swEliminar);
				// HandlerDBUser.addMessageToActivityProgress(rb.getString("userAdmin.notifyByEmailToNewUser"));
			} catch (Exception e) {
				// TODO: handle exception
				HandlerDBUser.addMessageToActivityProgress("ERROR: " + rb.getString("userAdmin.notifyByEmailToNewUser"));
				throw new Exception();
			}

			// actualizamos la tabla documents con el nuevo usuario a sustituir.
			try {
				st = con.prepareStatement(JDBCUtil.replaceCastMysql(getUpdateOwnerDocuments(nameUserOld, forma.getUseremplazo(), false)));
				st.executeUpdate();
				// HandlerDBUser.addMessageToActivityProgress(rb.getString("userAdmin.updateDocuments"));
			} catch (Exception e) {
				// TODO: handle exception
				HandlerDBUser.addMessageToActivityProgress("ERROR: " + rb.getString("userAdmin.updateDocuments"));
				throw new Exception();
			}

			// actualizamos la tabla la permisologia del documents con el nuevo
			// usuario a sustituir.
			try {
				getUpdatePermisoDocuments(con, st, nameUserOld, forma.getUseremplazo(), false);
				// HandlerDBUser.addMessageToActivityProgress(rb.getString("userAdmin.updateDocumentsPermission"));
			} catch (Exception e) {
				HandlerDBUser.addMessageToActivityProgress("ERROR: " + rb.getString("userAdmin.updateDocumentsPermission"));
				throw new Exception();
			}

			// borramos todos los permisos doc (permisiondocuser) del user viejo
			try {
				st = con.prepareStatement(JDBCUtil.replaceCastMysql(getDeleteUser_Doc(nameUserOld)));
				st.executeUpdate();
				// HandlerDBUser.addMessageToActivityProgress(rb.getString("userAdmin.deleteOldDocumentPermission"));
			} catch (Exception e) {
				HandlerDBUser.addMessageToActivityProgress("ERROR: " + rb.getString("userAdmin.deleteOldDocumentPermission"));
				throw new Exception();
			}

			// actualizamos la tabla struct con el nuevo usuario a sustituir.
			try {
				st = con.prepareStatement(JDBCUtil.replaceCastMysql(getUpdateidUserUser_Struct(nameUserOld, forma.getUseremplazo())));
				st.executeUpdate();
				HandlerDBUser.addMessageToActivityProgress(rb.getString("userAdmin.updateDocuments"));
				// HandlerDBUser.addMessageToActivityProgress(rb.getString("userAdmin.setDocumentsToNewOwner"));
			} catch (Exception e) {
				HandlerDBUser.addMessageToActivityProgress("ERROR: " + rb.getString("userAdmin.setDocumentsToNewOwner"));
				throw new Exception();
			}

			// ydavila Ticket 001-00-003023 actualiza RESPONSABLE SOLICITUDES DE CAMBIO de la tabla STRUCT con el nuevo usuario
			try {
				st = con.prepareStatement(getUpdateidrespsolcambioUser_Struct(nameUserOld, forma.getUseremplazo()));
				st.executeUpdate();
				//HandlerDBUser.addMessageToActivityProgress(rb.getString("userAdmin.setDocumentsToNewChangeResponsible"));
				//HandlerDBUser.addMessageToActivityProgress(rb.getString("userAdmin.setDocumentsToNewOwner"));
			} catch (Exception e) {
				// TODO: handle exception
				HandlerDBUser.addMessageToActivityProgress("ERROR: " + rb.getString("userAdmin.setDocumentsToNewChangeResponsible"));
				throw new Exception();
			}
			// ydavila Ticket 001-00-003023 actualiza RESPONSABLE SOLICITUDES ELIMINACI�N de la tabla STRUCT con el nuevo usuario
			try {
				st = con.prepareStatement(getUpdateidrespsoleliminUser_Struct(nameUserOld, forma.getUseremplazo()));
				st.executeUpdate();
				//HandlerDBUser.addMessageToActivityProgress(rb.getString("userAdmin.setDocumentsToNewDeleteResponsible"));
				//HandlerDBUser.addMessageToActivityProgress(rb.getString("userAdmin.setDocumentsToNewOwner"));
			} catch (Exception e) {
				// TODO: handle exception
				HandlerDBUser.addMessageToActivityProgress("ERROR: " + rb.getString("userAdmin.setDocumentsToNewDeleteResponsible"));
				throw new Exception();
			}

			// ydavila Ticket 001-00-003023 actualiza RESPONSABLE SOLICITUDES IMPRESI�N de la tabla STRUCT con el nuevo usuario
			try {
				st = con.prepareStatement(getUpdateidrespsolimpresUser_Struct(nameUserOld, forma.getUseremplazo()));
				st.executeUpdate();
				//HandlerDBUser.addMessageToActivityProgress(rb.getString("userAdmin.setDocumentsToNewImpresionResponsible"));
				//HandlerDBUser.addMessageToActivityProgress(rb.getString("userAdmin.setDocumentsToNewOwner"));
			} catch (Exception e) {
				// TODO: handle exception
				HandlerDBUser.addMessageToActivityProgress("ERROR: " + rb.getString("userAdmin.setDocumentsToNewImpresionResponsible"));
				throw new Exception();
			}

			// ydavila Ticket 001-00-003023 actualiza RESPONSABLE SOLICITUDES DE CAMBIO de la tabla PARAMETERS con el nuevo usuario
			try {
				st = con.prepareStatement(HandlerParameters.getUpdateidrespsolcambioUser_Admin(nameUserOld, forma.getUseremplazo()));
				isResSolCambio = st.executeUpdate()>0;
				//HandlerDBUser.addMessageToActivityProgress(rb.getString("userAdmin.setDocumentsToNewChangeResponsible"));
				//HandlerDBUser.addMessageToActivityProgress(rb.getString("userAdmin.setDocumentsToNewOwner"));
			} catch (Exception e) {
				// TODO: handle exception
				HandlerDBUser.addMessageToActivityProgress("ERROR: " + rb.getString("userAdmin.setDocumentsToNewChangeResponsible"));
				throw new Exception();
			}
			// ydavila Ticket 001-00-003023 actualiza RESPONSABLE SOLICITUDES ELIMINACI�N de la tabla PARAMETERS con el nuevo usuario
			try {
				st = con.prepareStatement(HandlerParameters.getUpdateidrespsoleliminUser_Admin(nameUserOld, forma.getUseremplazo()));
				isResSolElimin = st.executeUpdate()>0;
				//HandlerDBUser.addMessageToActivityProgress(rb.getString("userAdmin.setDocumentsToNewDeleteResponsible"));
				//HandlerDBUser.addMessageToActivityProgress(rb.getString("userAdmin.setDocumentsToNewOwner"));
			} catch (Exception e) {
				// TODO: handle exception
				HandlerDBUser.addMessageToActivityProgress("ERROR: " + rb.getString("userAdmin.setDocumentsToNewDeleteResponsible"));
				throw new Exception();
			}

			// ydavila Ticket 001-00-003023 actualiza RESPONSABLE SOLICITUDES IMPRESI�N de la tabla PARAMETERS con el nuevo usuario
			try {
				st = con.prepareStatement(HandlerParameters.getUpdateidrespsolimpresUser_Admin(nameUserOld, forma.getUseremplazo()));
				isResSolImpres = st.executeUpdate()>0;
				//HandlerDBUser.addMessageToActivityProgress(rb.getString("userAdmin.setDocumentsToNewImpresionResponsible"));
				//HandlerDBUser.addMessageToActivityProgress(rb.getString("userAdmin.setDocumentsToNewOwner"));
			} catch (Exception e) {
				// TODO: handle exception
				HandlerDBUser.addMessageToActivityProgress("ERROR: " + rb.getString("userAdmin.setDocumentsToNewImpresionResponsible"));
				throw new Exception();
			}

			// actualizamos la tabla permisionstructuser con el nuevo usuario a sustituir.
			try {
				getUpdatePermisoStructUser_Struct(con, st, String.valueOf(forma.getIdPerson()), forma.getUseremplazo(), idUserRemplazo);
				// HandlerDBUser.addMessageToActivityProgress(rb.getString("userAdmin.setStructurePermission"));
			} catch (Exception e) {
				// TODO: handle exception
				HandlerDBUser.addMessageToActivityProgress("ERROR: " + rb.getString("userAdmin.setStructurePermission"));
				throw new Exception();
			}

			// Borramos la seguridad del usuario en la estructura
			try {
				st = con.prepareStatement(JDBCUtil.replaceCastMysql(HandlerSeguridad.getQuerySecurityStructDelete(forma.getIdPerson(), false)));
				st.executeUpdate();
				// HandlerDBUser.addMessageToActivityProgress(rb.getString("userAdmin.deleteOldStructurePermission"));
			} catch (Exception e) {
				// TODO: handle exception
				HandlerDBUser.addMessageToActivityProgress("ERROR: " + rb.getString("userAdmin.deleteOldStructurePermission"));
				throw new Exception();
			}

			// Desactivamos la seguridad del usuario en elmenu principal
			try {
				HandlerSeguridad.getQueryNegarUpdateActive(con, st, forma.getIdPerson(), false, Long.parseLong(idUserRemplazo), swEliminar);
				HandlerDBUser.addMessageToActivityProgress(rb.getString("userAdmin.setStructurePermission"));
				// HandlerDBUser.addMessageToActivityProgress(rb.getString("userAdmin.updateSecurityToNewUser"));
			} catch (Exception e) {
				// TODO: handle exception
				HandlerDBUser.addMessageToActivityProgress("ERROR: " + rb.getString("userAdmin.updateSecurityToNewUser"));
				throw new Exception();
			}

			// sustituimos la primera planilla sacop principal
			try {
				// Cambio de metodo cambiarUsuariosSaop ->
				cambiarUsuariosSacopNuevo(String.valueOf(forma.getIdPerson()), idUserRemplazo);
				// Sustituimos el usuario en las acciones por persona de las sacops
				st = con.prepareStatement(JDBCUtil.replaceCastMysql(HandlerSeguridad.getUpdateplanillaSacopAccionPorPersona(forma.getIdPerson(),
						Long.parseLong(idUserRemplazo), false)));
				st.executeUpdate();

				// modificamos las evidencias de las SACOPS del usuario anterior
				updateSACOPEvidences(nameUserOld, nameUserNew);
				HandlerDBUser.addMessageToActivityProgress(rb.getString("userAdmin.updateSacopElements"));
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
				HandlerDBUser.addMessageToActivityProgress("ERROR: " + rb.getString("userAdmin.updateSacopElements"));
				throw new Exception();
			}

			// Cambiamos el usuario de los expedientes
			try {
				st = con.prepareStatement(JDBCUtil.replaceCastMysql(HandlerSeguridad.getUpdateFilesUser(nameUserOld, forma.getUseremplazo())));
				st.executeUpdate();
				// HandlerDBUser.addMessageToActivityProgress(rb.getString("userAdmin.updateRecordElements"));
			} catch (Exception e) {
				// TODO: handle exception
				HandlerDBUser.addMessageToActivityProgress("ERROR: " + rb.getString("userAdmin.updateRecordElements"));
				throw new Exception();
			}

			// Cambiamos el usuario de los flujos de impresion aprobados
			try {
				st = con.prepareStatement(JDBCUtil.replaceCastMysql(HandlerSeguridad.getUpdatePrintFlow(forma.getIdPerson(), Long.parseLong(idUserRemplazo))));
				st.executeUpdate();
				// HandlerDBUser.addMessageToActivityProgress(rb.getString("userAdmin.updateApprovedWorkFlows"));
			} catch (Exception e) {
				// TODO: handle exception
				HandlerDBUser.addMessageToActivityProgress("ERROR: " + rb.getString("userAdmin.updateApprovedWorkFlows"));
				throw new Exception();
			}

			// Cambiamos el usuario de los flujos de cancelados
			try {
				st = con.prepareStatement(JDBCUtil.replaceCastMysql(HandlerSeguridad.getUpdateCanceledFlow(nameUserOld, forma.getUseremplazo())));
				st.executeUpdate();
				// HandlerDBUser.addMessageToActivityProgress(rb.getString("userAdmin.updateCancelledWorkFlows"));
			} catch (Exception e) {
				// TODO: handle exception
				HandlerDBUser.addMessageToActivityProgress("ERROR: " + rb.getString("userAdmin.updateCancelledWorkFlows"));
				throw new Exception();
			}

			// Cambiamos el usuario de las versiones
			try {
				st = con.prepareStatement(JDBCUtil.replaceCastMysql(HandlerSeguridad.getUpdateVersionUser(nameUserOld, forma.getUseremplazo())));
				st.executeUpdate();
				HandlerDBUser.addMessageToActivityProgress(rb.getString("userAdmin.updateDocumentVersions"));
			} catch (Exception e) {
				// TODO: handle exception
				HandlerDBUser.addMessageToActivityProgress("ERROR: " + rb.getString("userAdmin.updateDocumentVersions"));
				throw new Exception();
			}

			// eliminamos el usuario que no se pudo sustituir en las acciones
			// por persona
			try {
				StringBuilder deletesql = new StringBuilder("");
				deletesql.append("delete from tbl_sacopaccionporpersona where idperson=").append(forma.getIdPerson());
				deletesql.append(" and firmo <> '").append(Constants.permission).append("'");
				// System.out.println("deletesql=" + deletesql.toString());
				st = con.prepareStatement(JDBCUtil.replaceCastMysql(deletesql.toString()));
				st.execute();

				//PENDIENTE:eliminar o sustituir de la lista de distribucion
				//ydavila Ticket 001-00-003228 usuarios eliinados quedan pendientes el lista de distribuci�n
				// Cambiamos el usuario de la lista de distribuci�n en el nodo
				try {
					// query que recupera los ids de la tabla
					StringBuffer ids = new StringBuffer("Select idnode from listdist p where p.nameuser = '").append(nameUserOld).append("'")
							.append(" and p.idnode not in   (select idnode from  listdist p2 where p2.nameuser = '").append(nameUserNew).append("')");

					StringBuilder updatesqlLD = new StringBuilder("");
					updatesqlLD.append("update listdist set nameuser = '").append(nameUserNew).append("' ").append(" where nameuser = '").append(nameUserOld)
							.append("' ").append(" and idnode in (").append(JDBCUtil.executeQueryRetornaIds(ids)).append(")");
					st = con.prepareStatement(JDBCUtil.replaceCastMysql(updatesqlLD.toString()));
					st.execute();
				} catch (Exception e) {
					HandlerDBUser.addMessageToActivityProgress("ERROR: " + rb.getString("userAdmin.updateDistributionListNode"));
					throw new Exception();
				}

				// Cuando el usuario que sustituye ya est� en la lista de distribuci�n del nodo
				// se debe hacer el paso adicional de eliminar el registro del usuario que se elimin�
				try {
					StringBuilder deletesqlLD = new StringBuilder("");
					deletesqlLD.append("delete from listdist where nameuser = '").append(nameUserOld).append("' ");
					st = con.prepareStatement(JDBCUtil.replaceCastMysql(deletesqlLD.toString()));
					st.execute();
				} catch (Exception e) {
					HandlerDBUser.addMessageToActivityProgress("ERROR: " + rb.getString("userAdmin.updateDistributionListNode"));
					throw new Exception();
				}

				// Cambiamos el usuario del la lista de distribuci�n en los documentos
				// TODO ydavila verificar si se deben eliminar todos estos registros o crear una columna en la tabla
				// y marcar como inactivo, Por el tema de los hist�ricos
				try {
					StringBuffer ids = new StringBuffer("");
					ids.append("SELECT iddocument FROM listdistdocument p WHERE p.idusuario = ");
					ids.append(forma.getIdPerson());
					ids.append(" AND p.iddocument NOT IN (SELECT iddocument FROM listdistdocument p2 WHERE p2.idusuario = ");
					ids.append(idUserRemplazo).append(")");

					StringBuilder updatesqlLDD = new StringBuilder("");
					updatesqlLDD.append("UPDATE listdistdocument set idusuario = ").append(idUserRemplazo).append(" where idusuario = ")
							.append(forma.getIdPerson()).append(" AND iddocument IN (").append(JDBCUtil.executeQueryRetornaIds(ids)).append(")");
					st = con.prepareStatement(JDBCUtil.replaceCastMysql(updatesqlLDD.toString()));
					st.execute();

				} catch (Exception e) {
					HandlerDBUser.addMessageToActivityProgress("ERROR: " + rb.getString("userAdmin.updateDistributionListDoc"));
					throw new Exception();
				}

				// Cuando el usuario que sustituye ya est� en la lista de distribuci�n de documentos
				// se debe hacer el paso adicional de eliminar el registro del usuario que se elimin�
				try {
					StringBuilder deletesqlLDD = new StringBuilder("");
					deletesqlLDD.append("delete from listdistdocument where idusuario = ").append(forma.getIdPerson());
					st = con.prepareStatement(JDBCUtil.replaceCastMysql(deletesqlLDD.toString()));
					st.execute();

				} catch (Exception e) {
					HandlerDBUser.addMessageToActivityProgress("ERROR: " + rb.getString("userAdmin.updateDistributionListDoc"));
					throw new Exception();
				}
				// ydavila

				// sacop accion por persona
				// st =
				// con.prepareStatement(HandlerSeguridad.getActualizaAccionPorPersonaSacop(String.valueOf(forma.getIdPerson()),idUserRemplazo));
				// st.executeUpdate();
				if (swEliminar) {
					// deshabilitamos el usuario en la table person
					st = con.prepareStatement(JDBCUtil.replaceCastMysql(HandlerSeguridad.getQueryPerson(forma.getIdPerson(), false)));
					st.executeUpdate();
				}
				
				// Cambiamos el usuario del la lista de GESTIONADORES de SACOPs forma.getIdPerson() -> idUserRemplazo 
				try {
					StringBuffer sb = new StringBuffer("select listUserAddressee from parameters");
					st = con.prepareStatement(JDBCUtil.replaceCastMysql(sb.toString()));
					ResultSet rs = st.executeQuery();
					
					String ids = null;
					if(rs.next()) {
						ids = rs.getString(1);
					}
					
					if(ids!=null && ids.length()>0) {
						String[] lista = ids.split(",");
						
						for(int i=0; i<lista.length; i++) {
							if(lista[i].equals(String.valueOf(forma.getIdPerson()))) {
								isGestionadores = true;
								lista[i]=idUserRemplazo;
							}
						}
						
						sb.setLength(0);
						sb.append("UPDATE parameters SET listUserAddressee = '").append(StringUtils.join(lista, ",")).append("' ");
						st = con.prepareStatement(JDBCUtil.replaceCastMysql(sb.toString()));
						st.execute();
						
						HandlerDBUser.addMessageToActivityProgress(rb.getString("userAdmin.updatelistUserAddressee"));
					}
				} catch (Exception e) {
					HandlerDBUser.addMessageToActivityProgress("ERROR: " + rb.getString("userAdmin.updatelistUserAddressee"));
					throw new Exception();
				}
				
				// si pertenece a la lista de gestionadores de sacop
				// sustituimos en otras tablas
				if(isGestionadores || isResSolCambio || isResSolImpres || isResSolElimin) {
					HandlerDBUser.addMessageToActivityProgress(rb.getString("userAdmin.messageForUserSpecial").replaceAll("NEW_USER_REPLACED",nameUserNew));
				}
				
				// Mensaje agregado para informar cambios en la auditoria
				HandlerDBUser.addMessageToActivityProgress(rb.getString("userAdmin.messageAuidtDeleteUser").replaceAll("NEW_USER_REPLACED",nameUserNew));
				

				// HandlerDBUser.addMessageToActivityProgress(rb.getString("userAdmin.deleteOtherPersonActions"));
			} catch (Exception e) {
				HandlerDBUser.addMessageToActivityProgress("ERROR: " + rb.getString("userAdmin.deleteOtherPersonActions"));
				throw new Exception();
			}
		} catch (Exception e) {
			e.printStackTrace();

			try {
				con.rollback();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}

			setMensaje(rb.getString("E0133"));
			throw new Exception(rb.getString("E0133"));
		}
	}

	public static String getUpdateActUserFTP(long idOldOwner, long idNewUser) {
		StringBuilder queryStr = new StringBuilder();
		queryStr.append("UPDATE act_user set idPerson = ");
		queryStr.append(idNewUser);
		queryStr.append(" WHERE idPerson = ");
		queryStr.append(idOldOwner);

		return queryStr.toString();
	}

	/**
	 *
	 * @param idoldOwner
	 * @param idnewUser
	 * @throws SQLException
	 */
	public static void cambiarUsuariosSacopNuevo(String idoldOwner, String idnewUser) throws SQLException {
		Transaction tx = null;
		Session session = null;
		StringBuffer queryStr = new StringBuffer();
		Connection con = null;

		PlanillaSacop1TO oPlanillaSacop1TO = new PlanillaSacop1TO();

		try {
			//queryStr.append("SELECT emisor, respblearea, solicitudinforma FROM tbl_planillasacop1 ");
			queryStr.append("SELECT * FROM tbl_planillasacop1 ");
			queryStr.append(" WHERE estado NOT IN (").append(LoadSacop.edoCerrado).append(",");
			queryStr.append(LoadSacop.edoRechazado).append(")");

			CachedRowSet crs = JDBCUtil.executeQuery(queryStr, Thread.currentThread().getStackTrace()[1].getMethodName());

			int i = 0;
			boolean borrado = false;

			// iniciamos la transaccion
			con = JDBCUtil.getConnection(Thread.currentThread().getStackTrace()[1].getMethodName());
			con.setAutoCommit(false);

			PlanillaSacop1DAO oPlanillaSacop1DAO = new PlanillaSacop1DAO(con);

			while (crs.next()) {
				oPlanillaSacop1DAO.load(crs, oPlanillaSacop1TO);

				// actualizamos el emisor por el nuevo a sustituir
				if (String.valueOf(oPlanillaSacop1TO.getEmisor()).equalsIgnoreCase(idoldOwner)) {
					oPlanillaSacop1TO.setEmisor(idnewUser);
				}

				// actualizamos el usuario que solicito la sacop por el nuevo a sustituir
				if (String.valueOf(oPlanillaSacop1TO.getUsuarioSacops1()).equalsIgnoreCase(idoldOwner)) {
					oPlanillaSacop1TO.setUsuarioSacops1(idnewUser);
				}

				// actualizamos el responsable por el nuevo a sustituir
				if (String.valueOf(oPlanillaSacop1TO.getRespblearea()).equalsIgnoreCase(idoldOwner)) {
					oPlanillaSacop1TO.setRespblearea(idnewUser);
				}

				// actualizamos el informa a por el nuevo a sustituir
				StringTokenizer h = new StringTokenizer(oPlanillaSacop1TO.getSolicitudinforma(), ",");
				Hashtable hash = new Hashtable();
				while (h.hasMoreTokens()) {
					String u = (String) h.nextToken();
					hash.put(u, u);
				}
				borrado = false;
				if (hash.containsKey(idoldOwner)) {
					hash.remove(idoldOwner);
					borrado = true;
				}

				Enumeration it = hash.keys();
				StringBuilder nuevoInformaA = new StringBuilder("");
				while (it.hasMoreElements()) {
					String ele = (String) it.nextElement();
					nuevoInformaA.append(ele);
					if (it.hasMoreElements()) {
						nuevoInformaA.append(",");
					}
				}
				// concatenamos el nuevo usuario
				if (borrado && (",".concat(nuevoInformaA.toString()).concat(",")).indexOf((",".concat(idnewUser).concat(","))) == -1) {
					if (!nuevoInformaA.toString().trim().equals("")) {
						nuevoInformaA.append(",");
					}
					nuevoInformaA.append(idnewUser);
				}

				oPlanillaSacop1TO.setSolicitudinforma(nuevoInformaA.toString());

				oPlanillaSacop1DAO.actualizar(oPlanillaSacop1TO);
			}

			con.commit();
		} catch (Exception e) {
			con.rollback();
			log.error("Error: " + e.getLocalizedMessage(), e);
		} finally {
			con.close();
		}
	}

	/**
	 * Busca la seguridad por documento en los permisos de estructura
	 */
	public static void getAllSecurityForStructUserDocs(long idUser, Hashtable security, String idDoc) throws Exception {
		log.debug("iniciando");
		StringBuilder sql = new StringBuilder(100);
		sql.append("SELECT pd.*, doc.numgen  ");
		sql.append("FROM person p,permissionstructuser pd, documents doc ");
		sql.append("WHERE pd.idPerson = p.idPerson ");
		sql.append("AND pd.idstruct = doc.idNode ");
		sql.append("AND  p.idperson=").append(idUser).append(" ");
		sql.append("AND doc.numgen IN ('").append(idDoc.replaceAll(",", "','")).append("')");

		log.debug(sql.toString());
		Vector datos = JDBCUtil.doQueryVector(sql.toString(),Thread.currentThread().getStackTrace()[1].getMethodName());
		PermissionUserForm forma = null;
		for (int row = 0; row < datos.size(); row++) {
			Properties properties = (Properties) datos.elementAt(row);
			forma = new PermissionUserForm(properties.getProperty("numgen"));
			setSecurityInDocs(properties, forma, true);
			// en realidad no es idStruct la que guardamos, es idDocument la que
			// guardamos
			security.put(forma.getIdStruct(), forma);
			// security.put(properties.getProperty("idDocument"),forma);
		}
		log.debug("Finalizando");
	}

	public static void getAllSecurityForUserDocs(long idUser, Hashtable security, String idDoc) throws Exception {
		log.debug("Iniciando");
		StringBuilder sql = new StringBuilder(100);
		sql.append("SELECT pd.* ");
		sql.append("  FROM person p,permisiondocuser pd ");
		sql.append(" WHERE pd.idUser = p.nameUser");
		sql.append("   AND  p.idperson=").append(idUser);
		sql.append(" AND idDocument IN ('").append(idDoc.replaceAll(",", "','")).append("')");

		Vector datos = JDBCUtil.doQueryVector(sql.toString(),Thread.currentThread().getStackTrace()[1].getMethodName());
		PermissionUserForm forma = null;
		for (int row = 0; row < datos.size(); row++) {
			Properties properties = (Properties) datos.elementAt(row);
			forma = new PermissionUserForm(properties.getProperty("idDocument"));
			setSecurityInDocs(properties, forma, false);
			// en realidad no es idStruct la que guardamos, es idDocument la que
			// guardamos
			security.put(forma.getIdStruct(), forma);
			// security.put(properties.getProperty("idDocument"),forma);
		}
		log.debug("Finalizando");
	}

	public static PermissionUserForm getAllSecurityRecord(long idUser) throws Exception {
		StringBuilder sql = new StringBuilder();
		StringBuilder sqlUser = new StringBuilder();
		StringBuilder sqlGroup = new StringBuilder();
		ArrayList parametros = new ArrayList();
		PermissionUserForm forma = new PermissionUserForm((byte) 0);
		CachedRowSet crs = null;
		String idGroup = "";

		sql.append("SELECT idGrupo FROM person WHERE idPerson=? AND accountActive='1'");
		sqlUser.append("SELECT * FROM permissionrecorduser WHERE idUser = (select nameUser from person where idPerson=? and accountActive='1')");
		sqlGroup.append("SELECT * FROM permissionrecordgroup WHERE cast(idGroup as int) = (select idGrupo from person where idPerson=? and accountActive='1')");

		parametros.add(idUser);

		crs = JDBCUtil.executeQuery(sql, parametros, Thread.currentThread().getStackTrace()[1].getMethodName());
		if (!crs.next()) {
			throw new Exception("Usuarion no existe o no esta activo");
		}
		idGroup = crs.getString("idGrupo");

		crs = JDBCUtil.executeQuery(sqlUser, parametros, Thread.currentThread().getStackTrace()[1].getMethodName());
		if (crs.size() == 0) {
			crs = JDBCUtil.executeQuery(sqlGroup, parametros, Thread.currentThread().getStackTrace()[1].getMethodName());
		}
		boolean isAdmon = DesigeConf.getProperty("application.admon").equalsIgnoreCase(idGroup);

		if (isAdmon) {
			forma = new PermissionUserForm((byte) 1); // todos los permisos para
														// el administrador
		} else if (crs.next()) {
			forma.setToGenerate(ToolsHTML.getByteValueFromBoolean(crs.getBoolean("toGenerate")));
			forma.setToUpdate(ToolsHTML.getByteValueFromBoolean(crs.getBoolean("toUpdate")));
			forma.setToSend(ToolsHTML.getByteValueFromBoolean(crs.getBoolean("toSend")));
			forma.setToExport(ToolsHTML.getByteValueFromBoolean(crs.getBoolean("toExport")));
			forma.setToPrint(ToolsHTML.getByteValueFromBoolean(crs.getBoolean("toPrint")));
		}

		// completamos los datos para la forma
		forma.setIdPerson(idUser);
		forma.setIdGroup(Long.parseLong(idGroup));

		return forma;
	}

	public static PermissionUserForm getAllSecurityFiles(long idUser) throws Exception {
		StringBuilder sql = new StringBuilder();
		StringBuilder sqlUser = new StringBuilder();
		StringBuilder sqlGroup = new StringBuilder();
		ArrayList parametros = new ArrayList();
		PermissionUserForm forma = new PermissionUserForm((byte) 0);
		CachedRowSet crs = null;
		String idGroup = "";

		sql.append("SELECT idGrupo FROM person WHERE idPerson=? AND accountActive='1'");
		sqlUser.append("SELECT * FROM permissionfilesuser WHERE idUser = (select nameUser from person where idPerson=? and accountActive='1')");
		sqlGroup.append("SELECT * FROM permissionfilesgroup WHERE cast(idGroup as int) = (select idGrupo from person where idPerson=? and accountActive='1')");

		parametros.add(idUser);

		crs = JDBCUtil.executeQuery(sql, parametros, Thread.currentThread().getStackTrace()[1].getMethodName());
		if (!crs.next()) {
			throw new Exception("Usuarion no existe o no esta activo");
		}
		idGroup = crs.getString("idGrupo");

		crs = JDBCUtil.executeQuery(sqlUser, parametros, Thread.currentThread().getStackTrace()[1].getMethodName());
		if (crs.size() == 0) {
			crs = JDBCUtil.executeQuery(sqlGroup, parametros, Thread.currentThread().getStackTrace()[1].getMethodName());
		}
		boolean isAdmon = DesigeConf.getProperty("application.admon").equalsIgnoreCase(idGroup);

		if (isAdmon) {
			forma = new PermissionUserForm((byte) 1); // todos los permisos para
														// el administrador
		} else if (crs.next()) {
			forma.setToFilesSecurity(ToolsHTML.getByteValueFromBoolean(crs.getBoolean("toFilesSecurity")));
			forma.setToFilesCreate(ToolsHTML.getByteValueFromBoolean(crs.getBoolean("toFilesCreate")));
			forma.setToFilesExport(ToolsHTML.getByteValueFromBoolean(crs.getBoolean("toFilesExport")));
			forma.setToFilesEdit(ToolsHTML.getByteValueFromBoolean(crs.getBoolean("toFilesEdit")));
			forma.setToFilesDelete(ToolsHTML.getByteValueFromBoolean(crs.getBoolean("toFilesDelete")));
			forma.setToFilesRelated(ToolsHTML.getByteValueFromBoolean(crs.getBoolean("toFilesRelated")));
			forma.setToFilesVersion(ToolsHTML.getByteValueFromBoolean(crs.getBoolean("toFilesVersion")));
			forma.setToFilesView(ToolsHTML.getByteValueFromBoolean(crs.getBoolean("toFilesView")));
			forma.setToFilesPrint(ToolsHTML.getByteValueFromBoolean(crs.getBoolean("toFilesPrint")));
			forma.setToFilesHistory(ToolsHTML.getByteValueFromBoolean(crs.getBoolean("toFilesHistory")));
			forma.setToFilesDownload(ToolsHTML.getByteValueFromBoolean(crs.getBoolean("toFilesDownload")));
			forma.setToFilesSave(ToolsHTML.getByteValueFromBoolean(crs.getBoolean("toFilesSave")));
		}

		// completamos los datos para la forma
		forma.setIdPerson(idUser);
		forma.setIdGroup(Long.parseLong(idGroup));

		return forma;
	}

	public static Collection getAllAddress(ArrayList itemsSelecteds, String searchName) throws Exception {
		Vector resp = new Vector();
		String query = "SELECT distinct nameUser,Nombres,Apellidos,email,lower(Apellidos),lower(Nombres)";
		query += " FROM person WHERE accountActive = '1' ";
		if (!ToolsHTML.isEmptyOrNull(searchName)) {
			query += " AND (Nombres like '%" + searchName.trim() + "%' ";
			query += " OR Apellidos like '%" + searchName.trim() + "%' ";
			switch (Constants.MANEJADOR_ACTUAL) {
			case Constants.MANEJADOR_MSSQL:
				query += " OR Nombres+' '+Apellidos like '%" + searchName.trim() + "%' ";
				query += " OR Apellidos+' '+Nombres like '%" + searchName.trim() + "%') ";
				break;
			case Constants.MANEJADOR_POSTGRES:
				query += " OR Nombres|| ' ' ||Apellidos like '%" + searchName.trim() + "%' ";
				query += " OR Apellidos|| ' ' ||Nombres like '%" + searchName.trim() + "%') ";
				break;
			case Constants.MANEJADOR_MYSQL:
				query += " OR CONCAT(Nombres, ' ' ,Apellidos) like '%" + searchName.trim() + "%' ";
				query += " OR CONCAT(Apellidos, ' ' ,Nombres) like '%" + searchName.trim() + "%') ";
				break;
			}
		}
		query += "  ORDER BY lower(Apellidos) asc, lower(Nombres) asc ";
		Vector datos = JDBCUtil.doQueryVector(query,Thread.currentThread().getStackTrace()[1].getMethodName());
		for (int row = 0; row < datos.size(); row++) {
			Properties properties = (Properties) datos.elementAt(row);
			String nameUser = properties.getProperty("nameUser");
			AddressForm bean = new AddressForm(nameUser, properties.getProperty("Nombres"), properties.getProperty("Apellidos"),
					properties.getProperty("email"));
			if (itemsSelecteds.contains(nameUser)) {
				bean.setSelected(true);
			} else {
				bean.setSelected(false);
			}
			resp.add(bean);
		}
		return resp;
	}

	public static Collection getAllsUsers(Hashtable userAct) {
		PersonDAO oPersonDAO = new PersonDAO();
		Vector datos = new Vector();

		ArrayList<PersonTO> lista;
		try {
			lista = oPersonDAO.listarPersonAll();

			PerfilActionForm usr = null;
			for (Iterator iter = lista.iterator(); iter.hasNext();) {
				usr = new PerfilActionForm((PersonTO) iter.next());

				if (userAct.containsKey(usr.getId())) {
					usr.setSelected(true);
				} else {
					usr.setSelected(false);
				}
				datos.add(usr);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return datos;
	}

	public static Collection getAllsUsersWithOutTemp(Hashtable userAct) {

		Vector datos = new Vector();
		PerfilActionForm usr = null;

		PersonTO oPersonTO = null;
		PersonDAO oPersonDAO = new PersonDAO();

		ArrayList<PersonTO> lista;
		try {
			lista = oPersonDAO.listarPersonWithOutTmpAll();

			for (Iterator iter = lista.iterator(); iter.hasNext();) {
				usr = new PerfilActionForm((PersonTO) iter.next());

				if (userAct.containsKey(usr.getId())) {
					usr.setSelected(true);
				} else {
					usr.setSelected(false);
				}
				datos.add(usr);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return datos;
	}

	public static Collection getAllsUsersInArea(String idArea, long subact) {
		Vector result = new Vector();
		StringBuilder sql = new StringBuilder(50);
		// sql.append("SELECT distinct p.idPerson,p.nameUser,p.Apellidos,p.Nombres,act_number ");
		sql.append("SELECT distinct p.idPerson,p.nameUser,p.Apellidos,p.Nombres,lower(p.Apellidos),lower(p.Nombres) ");
		sql.append(" FROM person p ");
		// sql.append(" left join act_user a on (p.idPerson = a.idPerson) ");
		sql.append(" WHERE p.accountactive = '").append(Constants.permission).append("'");
		if (idArea != null) {
			sql.append(" AND p.idarea = ").append(idArea);
		}
		sql.append(" ORDER BY lower(p.apellidos) asc, lower(p.nombres) asc ");
		// System.out.println("query getAllsUsersInArea: " + sql.toString());
		PerfilActionForm forma = null;
		try {
			Vector datos = JDBCUtil.doQueryVector(sql.toString(),Thread.currentThread().getStackTrace()[1].getMethodName());
			for (int i = 0; i < datos.size(); i++) {
				Properties properties = (Properties) datos.elementAt(i);
				forma = new PerfilActionForm();
				forma.setNombres(properties.getProperty("nombres"));
				forma.setApellidos(properties.getProperty("apellidos"));
				forma.setId(Long.parseLong(properties.getProperty("idPerson")));
				if (subact > 0 && forma.getId() > 0) {
					StringBuilder sqlAct = new StringBuilder(50);
					sqlAct.append("select distinct act_number from act_user where idPerson = " + forma.getId());
					Vector datosAct = JDBCUtil.doQueryVector(sqlAct.toString(),Thread.currentThread().getStackTrace()[1].getMethodName());
					for (int j = 0; j < datosAct.size(); j++) {
						Properties propertiesAct = (Properties) datosAct.elementAt(j);
						if (!ToolsHTML.isEmptyOrNull(propertiesAct.getProperty("act_number"))
								&& Long.parseLong(propertiesAct.getProperty("act_number")) == subact) {
							forma.setSelected(true);
							break;
						} else {
							forma.setSelected(false);
						}
					}
				}
				result.add(forma);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return result;
	}

	public static void getAllUserInGroup(String idGroup, Hashtable result) {
		if (result != null) {
			StringBuilder sql = new StringBuilder(2048).append("SELECT p.idPerson,p.nameUser, ");
			switch (Constants.MANEJADOR_ACTUAL) {
			case Constants.MANEJADOR_MSSQL:
				sql.append("(p.Apellidos + ' ' + p.Nombres) AS nombre, ");
				break;
			case Constants.MANEJADOR_POSTGRES:
				sql.append("(p.Apellidos || ' ' || p.Nombres) AS nombre, ");
				break;
			case Constants.MANEJADOR_MYSQL:
				sql.append("CONCAT(p.Apellidos , ' ' , p.Nombres) AS nombre, ");
				break;
			}
			sql.append("c.cargo, lower(p.Apellidos), lower(p.Nombres)").append("  FROM person p ,tbl_cargo c,tbl_area a WHERE p.idGrupo = ").append(idGroup)
					.append(" AND p.accountactive = '").append(Constants.permission).append("'")
					.append(" AND c.idcargo=cast(p.cargo as int) and c.idarea=a.idarea").append(" ORDER BY lower(p.Apellidos) asc, lower(p.Nombres) asc");
			try {
				Vector datos = JDBCUtil.doQueryVector(sql.toString(),Thread.currentThread().getStackTrace()[1].getMethodName());
				for (int i = 0; i < datos.size(); i++) {
					Properties properties = (Properties) datos.elementAt(i);
					PermissionUserForm forma = new PermissionUserForm(Constants.permission);
					forma.setIdUser(properties.getProperty("idPerson"));
					forma.setNameUser(properties.getProperty("nameUser"));
					forma.setIdGroup(Long.parseLong(idGroup));
					forma.setName(properties.getProperty("nombre"));
					forma.setCargo(properties.getProperty("cargo"));
					result.put(forma.getNameUser(), forma);
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}

	public static Collection getAllUserInSubActivities(String idWorkFlow, String idSub) {
		Vector datos = new Vector();
		PersonDAO oPersonDAO = new PersonDAO();

		ArrayList<PersonTO> listaPerson;
		try {
			listaPerson = oPersonDAO.listarPersonAll();

			TreeMap lista = getAllIdPersonInSubActivities(idSub);
			TreeMap userAct = getIdPersonFlowInSubActivities(idWorkFlow);

			PerfilActionForm usr = null;
			for (Iterator iter = listaPerson.iterator(); iter.hasNext();) {
				usr = new PerfilActionForm((PersonTO) iter.next());

				if (lista.containsKey(usr.getId())) {
					if (userAct.containsKey(usr.getId())) {
						usr.setSelected(true);
					} else {
						usr.setSelected(false);
					}
					datos.add(usr);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return datos;
	}

	public static Collection getAllUserInSubActivitiesWithOutTemp(String idWorkFlow, String idSub) {
		Vector datos = new Vector();

		PersonDAO oPersonDAO = new PersonDAO();

		ArrayList<PersonTO> listaPerson;
		try {
			listaPerson = oPersonDAO.listarPersonWithOutTmpAll();

			TreeMap lista = getAllIdPersonInSubActivities(idSub);
			TreeMap userAct = getIdPersonFlowInSubActivities(idWorkFlow);
			PerfilActionForm usr = null;

			for (Iterator iter = listaPerson.iterator(); iter.hasNext();) {

				usr = new PerfilActionForm((PersonTO) iter.next());

				if (lista.containsKey(usr.getId())) {
					if (userAct.containsKey(usr.getId())) {
						usr.setSelected(true);
					} else {
						usr.setSelected(false);
					}
					datos.add(usr);
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return datos;
	}

	public static TreeMap getAllIdPersonInSubActivities(String idSub) {
		Vector datos = new Vector();
		TreeMap lista = new TreeMap();
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT a.idPerson FROM person a, act_user b ");
		sql.append("WHERE a.idPerson=b.idPerson ");
		sql.append("AND accountActive = '1' ");
		sql.append("AND b.Act_Number=");
		sql.append(idSub);

		PerfilActionForm usr = null;
		try {
			datos = JDBCUtil.doQueryVector(sql.toString(),Thread.currentThread().getStackTrace()[1].getMethodName());
			for (int i = 0; i < datos.size(); i++) {
				Properties properties = (Properties) datos.elementAt(i);
				lista.put(new Long(properties.getProperty("idPerson")), properties.getProperty("idPerson"));
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return lista;
	}

	public static TreeMap getIdPersonFlowInSubActivities(String idWorkFlow) {
		Vector datos = new Vector();
		TreeMap lista = new TreeMap();
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT b.idPerson FROM user_flexworkflows a, person b ");
		sql.append("WHERE a.idUser=b.nameUser ");
		sql.append("AND a.statu IN ('");
		sql.append(HandlerWorkFlows.wfuPending);
		sql.append("','");
		sql.append(HandlerWorkFlows.inQueued);
		sql.append("') ");
		sql.append("AND a.idWorkFlow=");
		sql.append(idWorkFlow);

		PerfilActionForm usr = null;
		try {
			datos = JDBCUtil.doQueryVector(sql.toString(),Thread.currentThread().getStackTrace()[1].getMethodName());
			for (int i = 0; i < datos.size(); i++) {
				Properties properties = (Properties) datos.elementAt(i);
				lista.put(new Long(properties.getProperty("idPerson")), properties.getProperty("idPerson"));
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return lista;
	}

	public static void getSecurityForUserInDocs(String idUser, String idDocs, Hashtable result) {
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT toViewDocs,toImpresion,idDocument");
		sql.append(" FROM permisiondocuser");
		sql.append(" WHERE idUser = '").append(idUser).append("'");
		sql.append(" AND idDocument IN (").append(idDocs).append(")");
		log.debug("[getSecurityForUserInDocs] " + sql);
		try {
			Vector datos = JDBCUtil.doQueryVector(sql.toString(),Thread.currentThread().getStackTrace()[1].getMethodName());
			for (int i = 0; i < datos.size(); i++) {
				Properties properties = (Properties) datos.elementAt(i);
				PermissionUserForm perm = new PermissionUserForm();
				perm.setToViewDocs(Byte.parseByte(properties.getProperty("toViewDocs")));
				perm.setToImpresion(Byte.parseByte(properties.getProperty("toImpresion")));
				perm.setIdDocument(properties.getProperty("idDocument"));
				result.put(perm.getIdDocument(), perm);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public static PermissionUserForm getSecurityForIDUserInDocs(String idUser, String idDocs) {
		StringBuilder sql = new StringBuilder();
		// sql.append("SELECT toViewDocs,toImpresion,idDocument");
		sql.append("SELECT * ");
		sql.append(" FROM permisiondocuser");
		sql.append(" WHERE idUser = '").append(idUser).append("'");
		sql.append(" AND idDocument = ").append(idDocs);
		PermissionUserForm perm = new PermissionUserForm();
		log.debug("[getSecurityForIDUserInDocs] " + sql);
		// System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!![getSecurityForIDUserInDocs] "
		// + sql);
		try {
			Properties properties = JDBCUtil.doQueryOneRow(sql.toString(),Thread.currentThread().getStackTrace()[1].getMethodName());
			if (properties.getProperty("isEmpty").equalsIgnoreCase("false")) {
				// perm.setToViewDocs(Byte.parseByte(properties.getProperty("toViewDocs")));
				// perm.setToImpresion(Byte.parseByte(properties.getProperty("toImpresion")));
				// perm.setIdDocument(properties.getProperty("idDocument"));
				HandlerDocuments.setDataSecurityDoc(perm, properties, null);
				return perm;
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}

	public static String getSecurityForPintDocuments(String idUser, String idDocs) {
		StringBuilder sql = new StringBuilder();
		// sql.append("SELECT toViewDocs,toImpresion,idDocument");
		sql.append("SELECT toImpresion ");
		sql.append(" FROM permisiondocuser");
		sql.append(" WHERE idUser = '").append(idUser).append("'");
		sql.append(" AND idDocument = ").append(idDocs);
		PermissionUserForm perm = new PermissionUserForm();

		try {
			Properties properties = JDBCUtil.doQueryOneRow(sql.toString(),Thread.currentThread().getStackTrace()[1].getMethodName());
			if (properties.getProperty("isEmpty").equalsIgnoreCase("false")) {
				return properties.getProperty("toImpresion");
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}

	public static String getNumeroDeUsuarios() {
		System.out.println("Ini getNumeroDeUsuarios() ");
		if (num == null || num.equals("null")) {
			if (userConnect == null) {
				userConnect = new Hashtable();
			}
			if (limUser < 0 && !isCheck) {
				// Obtenemos la Clave
				if (keyGen == null) {
					try {
						keyGen = HandlerSeguridad.getKey();
					} catch (Exception e) {
						System.out.println("Ocurrio un error al pedir el keyGen ");
						e.printStackTrace();
					}
				}
				if (num == null) {
					System.out.println("Pidiendo el numero de usuarios ");
					num = Encryptor.decrypt(keyGen, license);
				}
			}

			System.out.println("Validando licencia ");
			ModuloBean modulo = ToolsHTML.validarLicencia();
			System.out.println("Licencia valida ");
			num = modulo.getUsuarios();
		}
		System.out.println("Fin getNumeroDeUsuarios() ");
		return num;
	}

	public static String getNumeroDeUsuariosViewer() {
		if (numViewer == null) {
			if (userConnect == null) {
				userConnect = new Hashtable();
			}
			if (limUserViewer < 0 && !isCheckViewer) {
				// Obtenemos la Clave
				isCheckViewer = true;
				if (keyGen == null) {
					try {
						keyGen = HandlerSeguridad.getKey();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				if (numViewer == null) {
					numViewer = Encryptor.decrypt(keyGen.concat("9"), licenseViewer);
					try {
						limUserViewer = Integer.parseInt(numViewer);
					} catch (NumberFormatException ex) {
						limUserViewer = -1;
					}
				}
			}
		}
		return (numViewer == null ? "-1" : numViewer);
	}

	public static void actualizaEditPerson(long idUser, int edit) {
		try {
			StringBuilder sqlUdpdate = new StringBuilder();
			sqlUdpdate.append("UPDATE person set edit = " + edit + " WHERE idPerson = " + idUser);
			JDBCUtil.executeUpdate(sqlUdpdate);
		} catch (Exception e) {
			// System.out.println("Error en actualizaEditPerson con usuario " +
			// idUser);
		}
	}

	public static Users getEditPerson(long idUser) {
		Users usr = new Users();
		try {
			StringBuilder sql = new StringBuilder();
			sql.append("SELECT nameUser,edit,dateLastPassEdit from person WHERE idPerson = " + idUser);
			Properties prop = JDBCUtil.doQueryOneRow(sql.toString(),Thread.currentThread().getStackTrace()[1].getMethodName());
			if ((prop != null) && prop.getProperty("isEmpty").equalsIgnoreCase("false")) {
				usr.setIdPerson(idUser);
				usr.setUser(prop.getProperty("nameUser").trim());
				usr.setEdit(Integer.parseInt(prop.getProperty("edit")));
				if (prop.getProperty("dateLastPassEdit") != null) {
					String fecha = prop.getProperty("dateLastPassEdit");
					if (!ToolsHTML.isEmptyOrNull(fecha)) {
						usr.setLastEdit(ToolsHTML.sdfShowConvert1.format(ToolsHTML.sdfShowConvert.parse(fecha)));
						usr.setLastDatePassEdit(ToolsHTML.sdfShowConvert.parse(fecha));
					}
				}
			}
		} catch (Exception e) {
			// System.out.println("Error en getEditPerson con usuario " + idUser
			// + ", " + e);
		}
		return usr;
	}

	public static Collection getCargosConAreas(String idarea) {
		Vector result = new Vector();
		StringBuilder query = new StringBuilder();
		try {
			if (!ToolsHTML.isEmptyOrNull(idarea)) {
				int indexOf = idarea.indexOf(",");
				if (indexOf > 0) {
					query.append(
							"Select c.idcargo, c.cargo, c.idarea, a.area from tbl_cargo c, tbl_area a where a.activea=1 and c.activec=1 and c.idarea=a.idarea and c.idarea in (")
							.append(idarea).append(")");
					query.append(" order by lower(a.area), lower(c.cargo) ");
				} else {
					query.append(
							"Select c.idcargo, c.cargo, c.idarea, a.area from tbl_cargo c, tbl_area a where a.activea=1 and c.activec=1 and c.idarea=a.idarea and c.idarea=")
							.append(idarea);
					query.append(" order by lower(a.area), lower(c.cargo) ");
				}
				Vector datos = JDBCUtil.doQueryVector(query.toString(),Thread.currentThread().getStackTrace()[1].getMethodName());
				for (int i = 0; i < datos.size(); i++) {
					Properties properties = (Properties) datos.elementAt(i);
					Cargo cargo = new Cargo();
					cargo.setIdcargo(Long.parseLong(properties.getProperty("idcargo")));
					cargo.setCargo(properties.getProperty("cargo"));
					Search bean = new Search(String.valueOf(cargo.getIdcargo()), cargo.getCargo());
					bean.setAditionalInfo(properties.getProperty("area"));
					result.add(bean);
				}
			} else {
				query.append("Select c.idcargo, c.cargo, c.idarea, a.area from tbl_cargo c, tbl_area a where a.activea=1 and c.activec=1 and c.idarea=a.idarea  ");
				query.append(" order by lower(a.area), lower(c.cargo) ");
				Vector datos = JDBCUtil.doQueryVector(query.toString(),Thread.currentThread().getStackTrace()[1].getMethodName());
				for (int i = 0; i < datos.size(); i++) {
					Properties properties = (Properties) datos.elementAt(i);
					Cargo cargo = new Cargo();
					cargo.setIdcargo(Long.parseLong(properties.getProperty("idcargo")));
					cargo.setCargo(properties.getProperty("cargo"));
					Search bean = new Search(String.valueOf(cargo.getIdcargo()), cargo.getCargo());
					bean.setAditionalInfo(properties.getProperty("area"));
					result.add(bean);
				}
			}
		} catch (Exception e) {
			// System.out.println("Error getCargosConAreas(): " + e);
		}
		return result;
	}

	public static Collection getCargosConAreasHib(String idarea) {
		Vector result = new Vector();
		Cargo cargo = null;
		List lista = null;
		CargoDAO oCargoDAO = new CargoDAO();

		if (!ToolsHTML.isEmptyOrNull(idarea)) {

			try {
				ArrayList ls = oCargoDAO.listarByIdArea(idarea);
				Iterator ite = ls.iterator();
				while (ite.hasNext()) {
					lista.add(new Cargo((CargoTO) ite.next()));
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

			if (lista != null) {
				for (Iterator iter = lista.iterator(); iter.hasNext();) {
					cargo = (Cargo) iter.next();
					Search bean = new Search(String.valueOf(cargo.getIdcargo()), cargo.getCargo());
					String descArea = "";
					if (cargo.getArea() != null)
						descArea = cargo.getArea().getArea();
					bean.setAditionalInfo(descArea);
					result.add(bean);
				}
			}
		} else {

			try {
				ArrayList ls = oCargoDAO.listarOrderByAreaCargo();
				Iterator ite = ls.iterator();
				while (ite.hasNext()) {
					lista.add(new Cargo((CargoTO) ite.next()));
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

			for (Iterator iter = lista.iterator(); iter.hasNext();) {
				cargo = (Cargo) iter.next();
				Search bean = new Search(String.valueOf(cargo.getIdcargo()), cargo.getCargo());
				String descArea = "";
				if (cargo.getArea() != null)
					descArea = cargo.getArea().getArea();
				bean.setAditionalInfo(descArea);
				result.add(bean);
			}
		}
		return result;
	}

	public static Vector getUsers(String areas, String cargos, String usuarios, int orden) {
		return getUsers(null, areas, cargos, usuarios, orden);
	}

	public static Vector getUsers(Connection con, String areas, String cargos, String usuarios, int orden) {
		Vector result = new Vector();
		StringBuilder sql = new StringBuilder();
		sql.append("select p.*, c.cargo as descCargo, a.area as descArea from person p, tbl_cargo c, tbl_area a where p.accountActive = '1' ");
		sql.append(" and p.idarea = a.idarea ");
		sql.append(" and cast(p.cargo as int) =  c.idcargo ");
		try {
			if (!ToolsHTML.isEmptyOrNull(areas)) {
				int indexOf = areas.indexOf(",");
				if (indexOf > 0) {
					sql.append(" and p.idarea in (").append(areas).append(")");
				} else {
					sql.append(" and p.idarea = ").append(areas);
				}
			}

			if (!ToolsHTML.isEmptyOrNull(cargos)) {
				int indexOf2 = cargos.indexOf(",");
				if (indexOf2 > 0) {
					sql.append(" and cast(p.cargo as int) in (").append(cargos).append(")");
				} else {
					sql.append(" and cast(p.cargo as int) = ").append(cargos);
				}
			}

			if (!ToolsHTML.isEmptyOrNull(usuarios)) {
				int indexOf3 = usuarios.indexOf(",");
				if (indexOf3 > 0) {
					sql.append(" and p.idPerson in (").append(usuarios).append(")");
				} else {
					sql.append(" and p.idPerson = ").append(usuarios);
				}
			}
			if (orden == 1 || orden == 0)
				sql.append(" order by lower(p.apellidos), lower(p.nombres), lower(p.nameUser) ");
			if (orden == 2)
				sql.append(" order by lower(a.area), lower(c.cargo), lower(p.apellidos), lower(p.nombres), lower(p.nameUser) ");
			if (orden == 3)
				sql.append(" order by lower(c.cargo), lower(p.apellidos), lower(p.nombres), lower(p.nameUser) ");
			if (orden == 4)
				sql.append(" order by lower(p.nameUser), lower(p.apellidos), lower(p.nombres) ");

			// //System.out.println("getUsers sql ("+areas+","+cargos+") : " +
			// sql);
			Vector datos = JDBCUtil.doQueryVector(con, sql.toString(),Thread.currentThread().getStackTrace()[1].getMethodName());
			for (int i = 0; i < datos.size(); i++) {
				Properties properties = (Properties) datos.elementAt(i);
				Users usr = new Users();
				Area area = new Area();
				Cargo cargo = new Cargo();
				usr.setIdPerson(Long.parseLong(properties.getProperty("idPerson")));
				usr.setNameUser(properties.getProperty("nameUser"));
				usr.setNamePerson(properties.getProperty("apellidos") + " " + properties.getProperty("nombres"));
				area.setIdarea(Long.parseLong(properties.getProperty("idarea")));
				area.setArea(properties.getProperty("descArea"));
				cargo.setIdcargo(Long.parseLong(properties.getProperty("cargo")));
				cargo.setCargo(properties.getProperty("descCargo"));
				cargo.setIdarea(Long.parseLong(properties.getProperty("idarea")));
				usr.setArea(area);
				usr.setCargo(cargo);
				usr.setEmail(properties.getProperty("email"));
				result.add(usr);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return result;
	}

	/**
	 *
	 * @param nameUser
	 * @return
	 */
	public static Users getUser(String nameUser) {
		Vector result = new Vector();
		Users usr = new Users();
		try {
			StringBuilder sql = new StringBuilder(2048)
					.append("select p.*, c.cargo as descCargo, a.area as descArea from person p, tbl_cargo c, tbl_area a where p.accountActive = '1' ")
					.append(" and p.idarea = a.idarea ").append(" and cast(p.cargo as int) =  c.idcargo ").append(" and p.nameUser = '").append(nameUser)
					.append("'");
			Vector datos = JDBCUtil.doQueryVector(sql.toString(),Thread.currentThread().getStackTrace()[1].getMethodName());
			if (datos.size() > 0) {
				Properties properties = (Properties) datos.elementAt(0);

				usr.setIdPerson(Long.parseLong(properties.getProperty("idPerson")));
				usr.setNameUser(properties.getProperty("nameUser"));
				usr.setNamePerson(properties.getProperty("apellidos") + " " + properties.getProperty("nombres"));
				usr.setEmail(properties.getProperty("email"));

				Area area = new Area();
				area.setIdarea(Long.parseLong(properties.getProperty("idarea")));
				area.setArea(properties.getProperty("descArea"));
				usr.setArea(area);

				Cargo cargo = new Cargo();
				cargo.setIdcargo(Long.parseLong(properties.getProperty("cargo")));
				cargo.setCargo(properties.getProperty("descCargo"));
				cargo.setIdarea(Long.parseLong(properties.getProperty("idarea")));
				usr.setCargo(cargo);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return usr;
	}

	/**
	 *
	 * @param idarea
	 * @return
	 */
	public static String getAreasTxt(String idarea) {
		AreaTO area = null;
		List lista = null;
		StringBuilder resultado = new StringBuilder("");
		String result = "";
		AreaDAO oAreaDAO = new AreaDAO();

		if (!ToolsHTML.isEmptyOrNull(idarea)) {
			try {
				lista = oAreaDAO.listarById(idarea);

				if (lista != null) {
					for (Iterator iter = lista.iterator(); iter.hasNext();) {
						area = (AreaTO) iter.next();
						resultado.append(area.getArea() + ", ");
					}
					result = resultado.toString();
					if (result.length() > 1) {
						result = result.substring(0, result.length() - 2);
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return result;
	}

	public static String getChargesTxt(String idcharge) {
		String result = "";

		if (!ToolsHTML.isEmptyOrNull(idcharge)) {

			try {
				CargoDAO oCargoDAO = new CargoDAO();
				CargoTO cargo = null;

				ArrayList lista = oCargoDAO.listarById(idcharge);

				if (lista != null) {
					StringBuilder resultado = new StringBuilder("");
					for (Iterator iter = lista.iterator(); iter.hasNext();) {
						cargo = (CargoTO) iter.next();
						resultado.append(cargo.getCargo() + ", ");
					}
					result = resultado.toString();
					if (result.length() > 1) {
						result = result.substring(0, result.length() - 2);
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return result;
	}

	/**
	 *
	 * @param user
	 * @return
	 * @throws Exception
	 */
	public static boolean updateUserScanner(Users user) throws Exception {
		StringBuilder query = new StringBuilder(2048)
				.append("UPDATE person SET modo=?, lado=?, ppp=?, panel=?, pagina=?, separar=?, minimo=? WHERE idPerson=?");

		ArrayList param = new ArrayList();
		param.add(user.getModo());
		param.add(user.getLado());
		param.add(user.getPpp());
		param.add(user.getPanel());
		param.add(user.getPagina());
		param.add(user.getSeparar());
		param.add(user.getMinimo());
		param.add(user.getIdPerson()); // clave

		int act = JDBCUtil.executeUpdate(query, param);

		return act > 0;
	}

	/**
	 *
	 * @param user
	 * @return
	 * @throws Exception
	 */
	public static boolean updateUserDigitalParameter(Users user) throws Exception {
		StringBuilder query = new StringBuilder();
		query.append("UPDATE person SET typeDocuments=? ,ownerTypeDoc=? ,idNodeDigital=? ,typesetter=? ,checker=?, lote=?, correlativo=? ").append(
				"WHERE idPerson=?");

		ArrayList param = new ArrayList();
		param.add(user.getTypeDocuments());
		param.add(user.getOwnerTypeDoc());
		param.add(user.getIdNodeDigital());
		param.add(user.getTypesetter());
		param.add(user.getChecker());
		param.add(user.getLote());
		param.add(user.getCorrelativo());
		param.add(user.getIdPerson()); // clave

		int act = JDBCUtil.executeUpdate(query, param);

		return act > 0;
	}

	/**
	 *
	 * @param idPerson
	 * @param marca
	 * @return
	 * @throws Exception
	 */
	public static boolean updateTimeMarkForJavaWebStart(long idPerson, String marca) throws Exception {
		StringBuilder query = new StringBuilder();
		query.append("UPDATE person SET javaWebStart=? WHERE idPerson=?");

		ArrayList param = new ArrayList();
		param.add(marca);
		param.add(idPerson);

		int act = JDBCUtil.executeUpdate(query, param);

		return act > 0;
	}

	/**
	 *
	 * @param activityProgress
	 */
	public static void addMessageToActivityProgress(String activityProgress) {
		if (HandlerDBUser.activityProgress != null) {
			HandlerDBUser.activityProgress += "\\n" + activityProgress;
		} else {
			HandlerDBUser.activityProgress = activityProgress;
		}

		// System.out.println("*** " + HandlerDBUser.activityProgress);
	}

	public static String getActivityProgress() {
		return activityProgress;
	}
	public static void startActivityProgress() {
		activityProgress = null;
	}

	/*
	 * Devuelve en numero de usuarios registrados en la tabla person que se encuentren activos.
	 */
	public int getUserRegisterInDatabase() {
		int reg = 0;
		try {
			StringBuilder sb = new StringBuilder("SELECT COUNT(idperson) FROM person WHERE accountActive = ").append(JDBCUtil.getCastAsBitString("1"));
			CachedRowSet crs = JDBCUtil.executeQuery(sb, Thread.currentThread().getStackTrace()[1].getMethodName());
			if (crs.next()) {
				reg = crs.getInt(1);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return reg;
	}

	/**
	 * Cambiamos la carpeta de evidencias de un usuario al nuevo debido a que el due�o inicial esta siendo eliminado del sistema.
	 *
	 * @param nameUserOld
	 * @param nameUserNew
	 */
	public static void updateSACOPEvidences(String nameUserOld, String nameUserNew) {
		final String evidencePath = HandlerProcesosSacop.getSacopPath();
		//
		// final String evidencePath =
		// ToolsHTML.getPath()
		// + HandlerProcesosSacop.EVIDENCIAS_NAME_DIR;

		log.info("Revisando directorio base de evidencias " + evidencePath);
		log.info("Seran ajustadas las evidencias en las acciones del usuario '" + nameUserOld + "' para el usuario '" + nameUserNew + "'");

		// estructura a leer
		// <directorioBase>/<SACOP>/<AccionSACOP>/<carpetaDeUsuario>
		// obtenemos los elementos contenidos en ese directorio base
		File[] contenidoBase = new File(evidencePath).listFiles();
		if(contenidoBase!=null) {
			for (File sacops : contenidoBase) {
				if (sacops.isDirectory()) {
					// estoy dentro del directorio de la sacop
					// reviso los subdirectorios(acciones) de la misma
					log.info("Verificando directorio de SACOP " + sacops.getAbsolutePath());
	
					File[] acciones = sacops.listFiles();
					for (File accionSacop : acciones) {
						// obtenemos los involucrados en las accion respectiva
						if (accionSacop.isDirectory()) {
							log.info("Verificando directorio de Accion de la SACOP " + accionSacop.getAbsolutePath());
							File[] involucrados = accionSacop.listFiles();
							for (File usuarioAccion : involucrados) {
								if (usuarioAccion.isDirectory()) {
									if (nameUserOld.equalsIgnoreCase(usuarioAccion.getName())) {
										// ubicamos una accion del usuario que
										// queremos eliminar
										// procedemos a asignarsela al sustituto
										File dest = new File(accionSacop.getAbsolutePath() + File.separator + nameUserNew);
										usuarioAccion.renameTo(dest);
										log.info("Renombrando directorio '" + usuarioAccion.getAbsolutePath() + "' a '" + dest.getAbsolutePath() + "'");
									}
								}
							}
						}
					}
				}
			}
		}
	}

	//ydavila Ticket 001-00-003023
	public static String getClaveUser(Users user) throws Exception, Exception {
		Encryptor enc = new Encryptor();
		PreparedStatement pst = null;
		String clave="";
		StringBuffer sb = new StringBuffer();	
		sb.append("SELECT clave FROM person WHERE nameuser='");
		sb.append(user.getNameUser());sb.append("'");sb.append(" AND accountactive='1'");			
		try {
			
			CachedRowSet crs = JDBCUtil.executeQuery(sb, Thread.currentThread().getStackTrace()[1].getMethodName());
			if (crs.next()) {
				if (!ToolsHTML.isEmptyOrNull(crs.getString("clave"))) {
					 
					clave=crs.getString("clave");
				}
			}	
		} catch (Exception e) {
			e.printStackTrace();
			user.setMensaje("");
			return clave;
		}
		return clave;
	}	

	//ydavila Ticket 001-00-003023
	public synchronized static CachedRowSet getListUsersByAreaCargo() throws Exception {
		CachedRowSet crs = null;
		StringBuffer query = new StringBuffer();
		
		
		query.append("SELECT a.nameUser, a.apellidos, a.nombres, a.idArea, b.idCargo, b.cargo, c.area, a.idperson ");
		query.append("FROM person a, tbl_cargo b, tbl_area c ");
		query.append("WHERE accountActive = '1' ");
		query.append("AND b.idCargo=cast(a.cargo as int) ");
		query.append("AND c.idArea=a.idArea ");
		query.append("AND a.nameUser != '").append(Constants.ID_USER_TEMPORAL).append("' ");
		query.append("AND a.idArea != 1 ");
		query.append("AND b.idCargo != 1 ");
		query.append("ORDER BY c.area, b.cargo, lower(a.apellidos) asc ");
		
		crs = JDBCUtil.executeQuery(query, Thread.currentThread().getStackTrace()[1].getMethodName());

		return crs;
	}
	
	public static void main(String[] args) {
        java.util.Calendar calendario = new java.util.GregorianCalendar();
        String ahora = ToolsHTML.sdfShowConvert1.format(calendario.getTime());

        calendario.add(Calendar.MINUTE,5);
        String despues = ToolsHTML.sdfShowConvert1.format(calendario.getTime());
        
        System.out.println(ahora);
        System.out.println(despues);
		
        System.out.println(ahora.compareTo(despues));
        System.out.println(despues.compareTo(ahora));
		
		
	}

}
