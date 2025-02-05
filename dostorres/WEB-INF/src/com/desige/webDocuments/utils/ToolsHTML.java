package com.desige.webDocuments.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Enumeration;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.desige.webDocuments.dao.DigitalDAO;
import com.desige.webDocuments.dao.DocumentDAO;
import com.desige.webDocuments.document.forms.BaseDocumentForm;
import com.desige.webDocuments.document.forms.BeanVersionForms;
import com.desige.webDocuments.document.forms.DocumentsCheckOutsBean;
import com.desige.webDocuments.files.facade.DigitalFacade;
import com.desige.webDocuments.perfil.forms.PerfilActionForm;
import com.desige.webDocuments.persistent.managers.HandlerDBUser;
import com.desige.webDocuments.persistent.managers.HandlerDocuments;
import com.desige.webDocuments.persistent.managers.HandlerGrupo;
import com.desige.webDocuments.persistent.managers.HandlerParameters;
import com.desige.webDocuments.persistent.managers.HandlerStruct;
import com.desige.webDocuments.persistent.managers.HandlerTypeDoc;
import com.desige.webDocuments.persistent.managers.PdfConvert;
import com.desige.webDocuments.persistent.utils.JDBCUtil;
import com.desige.webDocuments.seguridad.forms.PermissionUserForm;
import com.desige.webDocuments.seguridad.forms.SeguridadUserForm;
import com.desige.webDocuments.structured.forms.BaseStructForm;
import com.desige.webDocuments.to.DigitalTO;
import com.desige.webDocuments.to.UserFlexWorkFlowsTO;
import com.desige.webDocuments.typeDocuments.forms.TypeDocumentsForm;
import com.desige.webDocuments.util.Encryptor;
import com.desige.webDocuments.utils.beans.BeanMenu;
import com.desige.webDocuments.utils.beans.PaginPage;
import com.desige.webDocuments.utils.beans.Search;
import com.desige.webDocuments.utils.beans.Users;
import com.desige.webDocuments.workFlows.forms.BaseWorkFlow;
import com.focus.jndi.ldapfastbind;
import com.focus.qwds.ondemand.server.excepciones.ErrorDeAplicaqcion;
import com.focus.qwds.ondemand.server.usuario.entidades.Usuario;
import com.focus.qweb.dao.PlanillaSacop1DAO;
import com.focus.qweb.dao.RegisterClassDAO;
import com.focus.qweb.to.PlanillaSacop1TO;
import com.focus.util.Archivo;
import com.focus.util.BaseConverterUtil;
import com.focus.util.Encriptor;
import com.focus.util.ModuloBean;
import com.focus.util.NetworkInfo;

import sun.jdbc.rowset.CachedRowSet;

/**
 * Title: ToolsHTML.java <br/>
 * Copyright: (c) 2004 Focus Consulting C.A.<br/>
 * 
 * @author Ing. Nelson Crespo (NC)
 * @author Ing. Sim�n Rodriguez (SR)
 * @version WebDocuments v3.0 <br/>
 *          Changes:<br/>
 *          <ul>
 *          <li>25/03/2004 (NC) Creation</li>
 *          <li>14/05/2005 (NC) Se agregaron nuevos m�todos "getPathFile" ,"changeNameFile" & "checkTree"</li>
 *          <li>05/07/2005 (NC) Cambios en el m�todo isEmptyOrNull</li>
 *          <li>30/06/2005 (SR) Se cambio getServerPath</li>
 *          <li>12/07/2005 (SR) Se valido que la opcion eliminar del link no aparesca si el documentos esta apobado</li>
 *          <li>12/07/2005 (SR) Se valido que documento obsoleto no viera el link (Propiedades,registro y checkOut)</li>
 *          <li>25/07/2005 (SR) Se valido que carpetaDoc en el metodo getPrefixToDoc no sea igual a null.</li>
 *          <li>21/04/2006 (NC) Add field search in the security profile user and group</li>
 *          <li>30/05/2006 Cambios para Correcto Formato de Fecha y para evitar Mostrar la Opci�n "eliminar Versi�n Borrador" si dicha Versi�n se encuentra
 *          Bloqueada</li>
 *          <li>03/04/2012 Se modifico el menu de flujos asociados al documento para que independientemente de si el documento este bloqueado o no se vea al
 *          menos, la opcion de ver el historico de flujos asociado al documento</li>
 *          </ul>
 */
public class ToolsHTML {

	public static HashMap<String, String> mime = new HashMap<String, String>();
	static {
		mime.put("doc", "application/msword");
		mime.put("dot", "application/msword");
		mime.put("docx", "application/vnd.openxmlformats-officedocument.wordprocessingml.document");
		mime.put("dotx", "application/vnd.openxmlformats-officedocument.wordprocessingml.template");
		mime.put("docm", "application/vnd.ms-word.document.macroEnabled.12");
		mime.put("dotm", "application/vnd.ms-word.template.macroEnabled.12");
		mime.put("xls", "application/vnd.ms-excel");
		mime.put("xlt", "application/vnd.ms-excel");
		mime.put("xla", "application/vnd.ms-excel");
		mime.put("xlsx", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
		mime.put("xltx", "application/vnd.openxmlformats-officedocument.spreadsheetml.template");
		mime.put("xlsm", "application/vnd.ms-excel.sheet.macroEnabled.12");
		mime.put("xltm", "application/vnd.ms-excel.template.macroEnabled.12");
		mime.put("xlam", "application/vnd.ms-excel.addin.macroEnabled.12");
		mime.put("xlsb", "application/vnd.ms-excel.sheet.binary.macroEnabled.12");
		mime.put("ppt", "application/vnd.ms-powerpoint");
		mime.put("pot", "application/vnd.ms-powerpoint");
		mime.put("pps", "application/vnd.ms-powerpoint");
		mime.put("ppa", "application/vnd.ms-powerpoint");
		mime.put("pptx", "application/vnd.openxmlformats-officedocument.presentationml.presentation");
		mime.put("potx", "application/vnd.openxmlformats-officedocument.presentationml.template");
		mime.put("ppsx", "application/vnd.openxmlformats-officedocument.presentationml.slideshow");
		mime.put("ppam", "application/vnd.ms-powerpoint.addin.macroEnabled.12");
		mime.put("pptm", "application/vnd.ms-powerpoint.presentation.macroEnabled.12");
		mime.put("potm", "application/vnd.ms-powerpoint.presentation.macroEnabled.12");
		mime.put("ppsm", "application/vnd.ms-powerpoint.slideshow.macroEnabled.12");
		mime.put("odt", "application/vnd.oasis.opendocument.text");
		mime.put("ott", "application/vnd.oasis.opendocument.text-template");
		mime.put("oth", "application/vnd.oasis.opendocument.text-web");
		mime.put("odm", "application/vnd.oasis.opendocument.text-master");
		mime.put("odg", "application/vnd.oasis.opendocument.graphics");
		mime.put("otg", "application/vnd.oasis.opendocument.graphics-template");
		mime.put("odp", "application/vnd.oasis.opendocument.presentation");
		mime.put("otp", "application/vnd.oasis.opendocument.presentation-template");
		mime.put("ods", "application/vnd.oasis.opendocument.spreadsheet");
		mime.put("ots", "application/vnd.oasis.opendocument.spreadsheet-template");
		mime.put("odc", "application/vnd.oasis.opendocument.chart");
		mime.put("odf", "application/vnd.oasis.opendocument.formula");
		mime.put("odb", "pplication/vnd.oasis.opendocument.database");
		mime.put("odi", "application/vnd.oasis.opendocument.image");
		mime.put("oxt", "application/vnd.openofficeorg.extension");
		mime.put("vsd", "application/vnd.ms-visio.viewer");
		mime.put("jnlp", "application/x-java-jnlp-file");
		mime.put("cdr", "application/coreldraw");
		mime.put("coreldraw", "application/coreldraw");
		mime.put("x-cdr", "application/x-cdr");
		mime.put("x-coreldraw", "application/x-coreldraw");
		mime.put("mpp", "application/vnd.ms-project"); // MSProject 2015-04-24
		mime.put("cdr", "image/cdr");
		mime.put("x-cdr", "image/x-cdr");
		mime.put("ai", "application/pdf");
		mime.put("pdf", "application/pdf");
		mime.put("bmp", "image/bmp");
		mime.put("class", "application/java");
		mime.put("exe", "application/octet-stream");
		mime.put("gif", "image/gif");
		mime.put("gz ", "application/x-gzip");
		mime.put("jar", "application/java-archive");
		mime.put("jpg", "image/jpeg");
		mime.put("jpeg", "image/jpeg");
		mime.put("pdf", "application/pdf");
		mime.put("png", "image/png");
		mime.put("tgz", "application/octet-stream");
		mime.put("tif", "image/tiff");
		mime.put("tiff", "image/tiff");
		mime.put("zip", "application/zip");
		mime.put("txt", "text/plain");
		mime.put("xml", "text/xml");
		mime.put("xsd", "text/xml");
		mime.put("xsl", "text/xml");
		mime.put("wsdl", "text/xml");
		mime.put("htm", "text/html");
		mime.put("html", "text/html");
		mime.put("css", "text/css");
		mime.put("js ", "text/plain");
		mime.put("jsp", "text/plain");
		mime.put("txt", "text/plain");
		mime.put("java", "text/plain");
		mime.put("properties", "text/plain");
		mime.put("sql", "text/plain");
	}

	public static final int ANCHO_MENU = 235;
	
	public static boolean LICENCIA_VENCIDA = false;
	public static boolean LICENCIA_GRACIA_VENCIDA = false;

	public static final String local = org.apache.struts.Globals.LOCALE_KEY;
	public static StringBuffer aux = new StringBuffer();
	private static final String defaultRoot = "parent.frames['code'].menu.items[";
	public static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss a");
	public static final SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
	public static final SimpleDateFormat sdfShowConvert1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	public static final SimpleDateFormat sdfShowConvert2 = new SimpleDateFormat("yyyy-MM-dd HH:mm");
	public static final SimpleDateFormat sdfShowConvert = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss a");
	public static final SimpleDateFormat sdfShowWithoutHour = new SimpleDateFormat("dd/MM/yyyy");
	public static final SimpleDateFormat sdfShow = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss a");
	public static final SimpleDateFormat sdfTime = new SimpleDateFormat("hh:mm:ss a");
	public static final SimpleDateFormat sdfTime24 = new SimpleDateFormat("HH:mm");
	public static final SimpleDateFormat sdfTime24_2 = new SimpleDateFormat("HH:mm:ss");
	public static final SimpleDateFormat date = new SimpleDateFormat("yyyy-MM-dd");

	private static NumberFormat numero = NumberFormat.getInstance(new Locale("ES"));

	public static Logger log = LoggerFactory.getLogger(ToolsHTML.class);

	private static ServletContext SERVLET_CONTEXT;

	private static StringBuffer ubicac = null;

	private static String licenseOwner = "";

	public static void setServletContext(ServletContext ctx) {
		SERVLET_CONTEXT = ctx;
		validarLicencia();
	}
	
	public static ServletContext getServletContext() {
		return SERVLET_CONTEXT;
	}

	// Luis Cisneros
	// Sacop y FTP son configurables.
	public static boolean showFTP() {
		boolean result = false;
		Boolean enableFTP = (Boolean) SERVLET_CONTEXT.getAttribute("enableFTP");
		if (enableFTP != null) {
			result = enableFTP.booleanValue();
		}
		return false;
		//return result;
	}

	public static boolean showFlujo() {
		boolean result = false;
		Boolean enableFlujo = (Boolean) SERVLET_CONTEXT.getAttribute("enableFlujo");
		if (enableFlujo != null) {
			result = enableFlujo.booleanValue();
		}
		//return result;
		return false;
	}

	public static boolean showAdmin() {
		boolean result = false;
		Boolean enableAdmin = (Boolean) SERVLET_CONTEXT.getAttribute("enableAdmin");
		if (enableAdmin != null) {
			result = enableAdmin.booleanValue();
		}
		return result;
	}

	public static boolean showActiveDirectory() {
		boolean result = false;
		Boolean enableAD = (Boolean) SERVLET_CONTEXT.getAttribute("enableActiveDirectory");
		if (enableAD != null) {
			result = enableAD.booleanValue();
		}
		return result;
	}

	public static boolean showDistributionList() {
		boolean result = false;
		Boolean enable = (Boolean) SERVLET_CONTEXT.getAttribute("enableDistributionList");
		if (enable != null) {
			result = enable.booleanValue();
		}
		return result;
	}

	public static boolean showConcurrentUser() {
		boolean result = false;
		Boolean enable = (Boolean) SERVLET_CONTEXT.getAttribute("enableConcurrentUser");
		if (enable != null) {
			result = enable.booleanValue();
		}
		return result;
	}

	public static boolean showCreateRegister() {
		boolean result = false;
		Boolean enableCreateRegister = (Boolean) SERVLET_CONTEXT.getAttribute("enableCreateRegister");
		if (enableCreateRegister != null) {
			result = enableCreateRegister.booleanValue();
		}
		return result;
	}

	public static String getLicenceOwner() {
		return licenseOwner;
	}

	public static void setLicenceOwner(String licenseOwner) {
		ToolsHTML.licenseOwner = licenseOwner;
	}

	public static boolean showSACOP() {
		boolean result = false;

		Boolean enableSACOP = (Boolean) SERVLET_CONTEXT.getAttribute("enableSACOP");

		if (enableSACOP != null) {
			result = enableSACOP.booleanValue();
		}

		//return result;
		return false;
	}

	public static boolean showRecord() {
		boolean result = false;

		Boolean enableRecord = (Boolean) SERVLET_CONTEXT.getAttribute("enableRecord");

		if (enableRecord != null) {
			result = enableRecord.booleanValue();
		}

		//return result;
		return false;
	}

	public static boolean showFiles() {
		boolean result = false;

		Boolean enableFiles = (Boolean) SERVLET_CONTEXT.getAttribute("enableFiles");

		if (enableFiles != null) {
			result = enableFiles.booleanValue();
		}

		//return result;
		return false;
	}

	public static boolean showDigital() {
		boolean result = false;

		Boolean enableDigital = (Boolean) SERVLET_CONTEXT.getAttribute("enableDigital");

		if (enableDigital != null) {
			result = enableDigital.booleanValue();
		}

		//return result;
		return false;
	}

	public static boolean showInTouch() {
		boolean result = false;

		Boolean enableSACOP = (Boolean) SERVLET_CONTEXT.getAttribute("enableInTouch");

		if (enableSACOP != null) {
			result = enableSACOP.booleanValue();
		}

		return result;
	}

	public static String menuFTP(ServletContext ctx, ResourceBundle rb) {
		StringBuffer result = new StringBuffer();

		if (showFTP()) {
			result.append("<img src=\"img/parametrico33.jpg\">");
			result.append("<a href=\"createAct.do\" onclick=\"javascript:window.parent.hacer('opc_16');\"  class=\"ahreMenu\">&nbsp;");
			result.append(rb.getString("admin.activities"));
			result.append("</a>");
		}

		return result.toString();
	}

	public static String menuSACOP(ServletContext ctx, ResourceBundle rb) {
		StringBuffer result = new StringBuffer();

		if (showSACOP()) {
			result.append("<img src=\"img/sacop33.jpg\">");
			result.append(" <a href=\"javascript:abrirVentana('LoadTitulosPlanillasSacop.do',700,500)\" onclick=\"javascript:window.parent.hacer('opc_14');\"  class=\"ahreMenu\">");
			result.append(rb.getString("admin.scpplanillas"));
			result.append("</a>");
		}

		return result.toString();
	}

	public static String menuClasificacionSACOP(ServletContext ctx, ResourceBundle rb) {
		StringBuffer result = new StringBuffer();

		if (showSACOP()) {
			result.append("<img src=\"img/sacop34.jpg\">");
			result.append(" <a href=\"javascript:abrirVentana('LoadClasificacionPlanillasSacop.do',700,500)\" onclick=\"javascript:window.parent.hacer('opc_21');\"  class=\"ahreMenu\">");
			result.append(rb.getString("scp.clasificacionsacop"));
			result.append("</a>");
		}

		return result.toString();
	}

	public static String menuInTouch(ServletContext ctx, ResourceBundle rb) {
		StringBuffer result = new StringBuffer();

		if (showInTouch()) {
			result.append("<img src=\"img/wonderware.gif\" width=\"18\" height=\"18\">");
			result.append("<a href=\"loadsacop_intouch.do\" class=\"ahreMenu\">");
			result.append(rb.getString("scpintouch.nombre"));
			result.append("</a>");
		}

		return result.toString();
	}

	public static String formatDate(Date date, Locale local) {
		ResourceBundle rb = ResourceBundle.getBundle("LoginBundle", local);
		StringBuffer format = new StringBuffer("EEEEEEEEE, dd '").append(rb.getString("application.of"));
		format.append("'").append(" MMMMMM '").append(rb.getString("application.of")).append("' yyyy");
		SimpleDateFormat sdf = new SimpleDateFormat(format.toString(), local);
		return sdf.format(date);
	}

	public static String getInitValue(int valor, boolean isMinor) {
		if (isMinor) {
			try {
				switch (valor) {
				case 0:
					return "1";
				case 1:
					return "A";
				}
				return DesigeConf.getProperty("begin.MinorVersion");
			} catch (Exception ex) {
				switch (valor) {
				case 0:
					return "1";
				case 1:
					return "A";
				}
			}
		} else {
			try {
				switch (valor) {
				case 0:
					return "0";
				case 1:
					return "A";
				}
				return DesigeConf.getProperty("begin.VersionApproved");
			} catch (Exception ex) {
				switch (valor) {
				case 0:
					return "0";
				case 1:
					return "A";
				}
			}
		}
		return "0";
	}

	public static boolean chkInitValue(int type, String value, boolean isMinor) {
		boolean result = false;
		if (isMinor) {
			// Si es Num�rico debe contener un N�mero
			if (type == 0) {
				result = isNumeric(value);
			} else {
				// Debe ser Letra...
				result = !isNumeric(value);
			}
		} else {
			// Si es Num�rico debe contener un N�mero
			if (type == 0) {
				result = isNumeric(value);
			} else {
				// Debe ser Letra...
				if (value != null && "".equalsIgnoreCase(value)) {
					result = false;
				} else {
					result = !isNumeric(value);
				}
			}
		}
		// System.out.println("[chkInitValue] value = " + value);
		// System.out.println("[chkInitValue] result = " + result);
		return result;
	}

	public static SeguridadUserForm getSeguridadModuloUser(Users usuario) {
		SeguridadUserForm forma = null;
		try {
			forma = new SeguridadUserForm();
			HandlerGrupo.getFieldUser(forma, "seguridaduser", true, usuario.getUser());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return forma;
	}

	public static SeguridadUserForm getSeguridadModuloGroup(Users usuario) {
		SeguridadUserForm forma = null;
		try {
			forma = new SeguridadUserForm(0);
			HandlerGrupo.getFieldUser(forma, "seguridadgrupo", false, usuario.getIdGroup());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return forma;
	}

	public static String printMenu(ResourceBundle rb, String nameUser, String idGroup) {
		return printMenu(rb, nameUser, idGroup, false);
	}

	public static String printMenu(ResourceBundle rb, String nameUser, String idGroup, boolean vertical) {
		return printMenu(rb, nameUser, idGroup, vertical, null);
	}

	private static void addMenuItem(Vector menu, BeanMenu options, boolean isActive) {
		if (isActive) {
			menu.add(options);
		}

	}

	public static String printMenu(ResourceBundle rb, String nameUser, String idGroup, boolean vertical, HttpServletRequest request) {
		Vector menu = new Vector();
		BeanMenu options = null;
		if (nameUser != null) {
			try {
				SeguridadUserForm forma = new SeguridadUserForm();
				SeguridadUserForm securityForGroup = new SeguridadUserForm();
				HandlerGrupo.getFieldUser(forma, "seguridaduser", true, nameUser);
				HandlerGrupo.getFieldUser(securityForGroup, "seguridadgrupo", false, idGroup);
				String linkWfs = rb.getString("href.home") + "?goTo=reload";
				String sacop = "loadSACOP.do?goTo=reload&mainAccess=true";
				boolean isValidRepository = true;
				if (request != null) {
					 isValidRepository = isRepositoryDefine(request);
				}
				isValidRepository = isNumberUserValidForLicence();

				options = new BeanMenu(rb.getString("enl.home"), linkWfs, "info", "icons/house.png");
				addMenuItem(menu, options, true);
				/*
				if (forma.getEstructura() == 0) {
					options = new BeanMenu(rb.getString("enl.cbs"), rb.getString("href.cbs").concat("?activarModulo=true"), "info",
							"icons/chart_organisation.png");
					addMenuItem(menu, options, isValidRepository);
				} else {
					if (forma.getEstructura() == 2) {
						if (securityForGroup.getEstructura() == 0) {
							options = new BeanMenu(rb.getString("enl.cbs"), rb.getString("href.cbs").concat("?activarModulo=true"), "info",
									"icons/chart_organisation.png");
							addMenuItem(menu, options, isValidRepository);
						}
					}
				}
				*/
				if (showFlujo()) {
					if (forma.getFlujos() == 0) {
						options = new BeanMenu(rb.getString("enl.workFlow"), rb.getString("href.workFlow"), "info", "icons/arrow_divide.png");
						addMenuItem(menu, options, isValidRepository);
					} else {
						if (forma.getFlujos() == 2) {
							if (securityForGroup.getFlujos() == 0) {
								options = new BeanMenu(rb.getString("enl.workFlow"), rb.getString("href.workFlow"), "info", "icons/arrow_divide.png");
								addMenuItem(menu, options, isValidRepository);
							}
						}
					}
				}

				// Expedientes
				// Modulo habilitado solo si lo compraron
				if (showFiles()) {
					if (forma.getFiles() == 0) {
						options = new BeanMenu(rb.getString("enl.files"), rb.getString("href.files"), "info", "icons/folder_page.png");
						addMenuItem(menu, options, isValidRepository);
					} else {
						if (forma.getFiles() == 2) {
							if (securityForGroup.getFiles() == 0) {
								options = new BeanMenu(rb.getString("enl.files"), rb.getString("href.files"), "info", "icons/folder_page.png");
								addMenuItem(menu, options, isValidRepository);
							}
						}
					}
				}

				// lista maestra
				options = new BeanMenu(rb.getString("enl.docPublic"), "showRecibosPendiente.do", "info", "icons/page_white_stack.png");
				addMenuItem(menu, options, true);
				// lista gestion de envio recibo DARWINUZCATEGUI
				options = new BeanMenu(rb.getString("principal.title"), "showRecibos.do", "info", "icons/email_link.png");
				addMenuItem(menu, options, true);
				//addMenuItem(menu, options, isValidRepository);

				// seguridad en sacop
				// Luis Cisneros
				// 19/04/07
				// SACOP es habilitado solo si lo compraron
				if (showSACOP()) {
					if (forma.getSacop() == 0) {
						options = new BeanMenu(rb.getString("scp.sacop"), sacop, "info", "icons/control_equalizer_blue.png");
						addMenuItem(menu, options, isValidRepository);
					} else {
						if (forma.getSacop() == 2) {
							if (securityForGroup.getSacop() == 0) {
								options = new BeanMenu(rb.getString("scp.sacop"), sacop, "info", "icons/control_equalizer_blue.png");
								addMenuItem(menu, options, isValidRepository);
							}
						}
					}
				}
				/*
				// Seguridad para Buscar Documentos
				if (forma.getSearch() == 0) {
					options = new BeanMenu(rb.getString("enl.searchs"), "searchDocument.do?expand=false&activarModulo=true", "info", "icons/find.png");
					addMenuItem(menu, options, isValidRepository);
				} else {
					if (forma.getSearch() == 2) {
						if (securityForGroup.getSearch() == 0) {
							options = new BeanMenu(rb.getString("enl.searchs"), "searchDocument.do?expand=false&activarModulo=true", "info", "icons/find.png");
							addMenuItem(menu, options, isValidRepository);
						}
					}
				}
				*/
				if (forma.getMensajes() == 0) {
					options = new BeanMenu(rb.getString("enl.messages"), rb.getString("href.messages"), "info", "icons/email.png");
					addMenuItem(menu, options, isValidRepository);
				} else {
					if (forma.getMensajes() == 2) {
						// String mensajes =
						// HandlerGrupo.getFieldUser1("mensajes",nameUser);
						// if (mensajes.equalsIgnoreCase("0")){
						if (securityForGroup.getMensajes() == 0) {
							options = new BeanMenu(rb.getString("enl.messages"), rb.getString("href.messages"), "info", "icons/email.png");
							addMenuItem(menu, options, isValidRepository);
						}
					}

				}

				// Record de usuarios
				// YSA 05/12/07
				// Modulo habilitado solo si lo compraron
				if (showRecord()) {
					if (forma.getRecord() == 0) {
						options = new BeanMenu(rb.getString("enl.record"), rb.getString("href.record"), "info", "icons/chart_curve.png");
						addMenuItem(menu, options, isValidRepository);
					} else {
						if (forma.getRecord() == 2) {
							if (securityForGroup.getRecord() == 0) {
								options = new BeanMenu(rb.getString("enl.record"), rb.getString("href.record"), "info", "icons/chart_curve.png");
								addMenuItem(menu, options, isValidRepository);
							}
						}
					}
				}

				// Digitalizar
				// Modulo habilitado solo si lo compraron
				if (showDigital()) {
					if (forma.getDigital() == 0) {
						options = new BeanMenu(rb.getString("enl.digital"), rb.getString("href.digital"), "info", "icons/scan.png");
						addMenuItem(menu, options, isValidRepository);
					} else {
						if (forma.getDigital() == 2) {
							if (securityForGroup.getDigital() == 0) {
								options = new BeanMenu(rb.getString("enl.digital"), rb.getString("href.digital"), "info", "icons/scan.png");
								addMenuItem(menu, options, isValidRepository);
							}
						}
					}
				}

				if (forma.getAdministracion() == 0) {
					options = new BeanMenu(rb.getString("enl.Admin"), rb.getString("href.Admin"), "info", "icons/database_gear.png");
					menu.add(options);
				} else {
					if (forma.getAdministracion() == 2) {
						if (securityForGroup.getAdministracion() == 0) {
							options = new BeanMenu(rb.getString("enl.Admin"), rb.getString("href.Admin"), "info", "icons/database_gear.png");
							menu.add(options);
						}
					}
				}
				if (forma.getPerfil() == 0) {
					options = new BeanMenu(rb.getString("enl.perfil"), rb.getString("href.perfil"), "info", "icons/vcard.png");
					menu.add(options);
				} else {
					if (forma.getPerfil() == 2) {
						if (securityForGroup.getPerfil() == 0) {
							options = new BeanMenu(rb.getString("enl.perfil"), rb.getString("href.perfil"), "info", "icons/vcard.png");
							menu.add(options);
						}
					}
				}
			} catch (Exception e) {
				e.printStackTrace(); // To change body of catch statement use
				// Options | File Templates.
			}
		}
		boolean nuevo = true;
		StringBuffer data = new StringBuffer();
		if (request != null) {
			request.getSession().setAttribute("numeroDeModulos", menu.size());
		}
		if (vertical) {
			int items = menu.size();
			float lenDiv = (float) (100.0 - (items - 1)) / items;
			for (int row = 0; row < items; row++) {
				options = (BeanMenu) menu.get(row);
				data.append("<tr>");
				data.append("<td height=\"20px\" class=\"ppalBotonItem\" ");
				// data.append("onclick=\"document.location='").append(options.getLink()).append("'\"
				// ");
				// data.append("target='").append(options.getTarget()).append("'
				// ");
				data.append("onclick=\"window.open('").append(options.getLink()).append("','").append(options.getTarget()).append("')\" ");
				data.append("onmouseover=\"this.className='ppalBotonItem ppalBotonItem2'\" onmouseout=\"this.className='ppalBotonItem'\">");
				data.append("<table border='0' cellspacing='0' cellpadding='0'><tr><td style='padding-top:3;'>");
				data.append("<img src=\"").append(options.getIcono()).append("\" border=\"0\" >&nbsp;");
				data.append("</td><td class=\"ppalBotonItemText\">");
				data.append(options.getTxtLink());
				data.append("</td></tr></table>");
				data.append("</td>");
				data.append("</tr>");
			}
		} else if (nuevo) {
			// <li class="top"><a href="http://www.cssplay.co.uk" id="home"
			// class="top_link"><span>Home</span></a></li>
			int items = menu.size();
			float lenDiv = (float) (100.0 - (items - 1)) / items;
			data.append("\n");
			for (int row = 0; row < items; row++) {
				options = (BeanMenu) menu.get(row);
				data.append("<li class=\"top\"><a ");
				data.append("href='#' onclick='moduloActivo(\"").append(options.getLink()).append("\",\"").append(options.getTarget()).append("\")' ");
				// data.append("href='javascript:moduloActivo(\"").append(options.getLink()).append("\")'
				// ");
				data.append("id=\"M").append(row).append("\" class=\"top_link\" ");
				// data.append("target='").append(options.getTarget()).append("'
				// ");
				data.append("><span>").append(options.getTxtLink()).append("</span></a>");
				data.append("</li>\n");
			}
		} else {
			data.append("<table width=\"100%\" align=center border=0 cellSpacing=0 cellPadding=0>\n");
			int items = menu.size();
			float lenDiv = (float) (100.0 - (items - 1)) / items;
			data.append("   <tr>\n");
			for (int row = 0; row < items; row++) {
				options = (BeanMenu) menu.get(row);
				data.append("       <td width=\"").append(lenDiv).append("%\" align='center'>");
				data.append("           <a class=\"ahreMenu\" href='").append(options.getLink()).append("' target='").append(options.getTarget());
				data.append("'>").append(options.getTxtLink()).append("</a>\n");
				data.append("</td>");
				if (row + 1 < items) {
					data.append("           <td width='1%'>");
					data.append("               <img src='img/SeparadorMenu.gif' width='1' height='10'/>");
					data.append("           </td>");
				}
			}
			data.append("   </tr>\n");
			data.append("</table>\n");
		}
		return data.toString();
	}

	public static String printMenuInnerToolBar(Vector menu, String title, boolean iniciarTabla, boolean finalizarTabla, ResourceBundle rb) {
		if (menu != null && menu.size() > 0) {
			StringBuffer data = new StringBuffer(100);
			if (!isEmptyOrNull(title)) {
				// data.append("<table class=\"clsTableTitle\" width=\"100%\"
				// cellSpacing=0 cellPadding=2 align=center border=0>\n");
				// data.append(" <tbody>\n");
				// data.append(" <tr>\n");
				// data.append(" <td class=\"td_title_bc\" height=\"21
				// px\">\n");
				// data.append(" ").append(title).append("\n");
				// data.append(" </td>\n");
				// data.append(" </tr>\n");
				// data.append(" </tbody>\n");
				// data.append("</table>\n");
			}
			if (iniciarTabla) {
				data.append("<table class=\"toolBar borderTopBlack\" cellpadding=\"0\" bgcolor=\"#efefef\"><tr>");
			}
			data.append("<td width=\"1px\">");
			data.append("<img src=\"menu-images/barraini.gif\">");
			data.append("</td>");
			int items = menu.size();
			float lenDiv = (float) (100.0 - (items - 1)) / items;
			BeanMenu options = null;
			boolean colocar = false;

			for (int row = 0; row < items; row++) {
				if (colocar) {
					data.append("<td width=\"1px\">");
					data.append("<img src=\"menu-images/barrasep.gif\">");
					data.append("</td>");
				}
				colocar = true;
				data.append("<td class=\"toolBarMiddle\"> ");
				options = (BeanMenu) menu.get(row);
				data.append("<a class=\"toolBarMiddleText\" href='").append(options.getLink()).append("' target='").append(options.getTarget());
				data.append("'>");

				if (options.getTxtLink().trim().equals(rb.getString("btn.addFolder").trim())) {
					data.append(
							"<img src=\"menu-images/addFolder.jpg\" style=\"border:1px solid #efefef\" onmouseover=\"this.style.border='1px solid #afafaf'\" onmouseout=\"this.style.border='1px solid #efefef'\" title=\"")
							.append(options.getTxtLink()).append("\">");
				} else if (options.getTxtLink().trim().equals(rb.getString("btn.addLocation").trim())) {
					data.append(
							"<img src=\"menu-images/addLocale.jpg\" style=\"border:1px solid #efefef\" onmouseover=\"this.style.border='1px solid #afafaf'\" onmouseout=\"this.style.border='1px solid #efefef'\" title=\"")
							.append(options.getTxtLink()).append("\">");
				} else if (options.getTxtLink().trim().trim().equals(rb.getString("btn.addDocument").trim())) {
					data.append(
							"<img src=\"menu-images/addDocument.jpg\" style=\"border:1px solid #efefef\" onmouseover=\"this.style.border='1px solid #afafaf'\" onmouseout=\"this.style.border='1px solid #efefef'\" title=\"")
							.append(options.getTxtLink()).append("\">");
				} else if (options.getTxtLink().trim().equals(rb.getString("btn.copy").trim())) {
					data.append(
							"<img src=\"menu-images/mover.jpg\" style=\"border:1px solid #efefef\" onmouseover=\"this.style.border='1px solid #afafaf'\" onmouseout=\"this.style.border='1px solid #efefef'\" title=\"")
							.append(options.getTxtLink()).append("\">");
				} else if (options.getTxtLink().trim().equals(rb.getString("btn.paste").trim())) {
					data.append(
							"<img src=\"menu-images/paste.jpg\" style=\"border:1px solid #efefef\" onmouseover=\"this.style.border='1px solid #afafaf'\" onmouseout=\"this.style.border='1px solid #efefef'\" title=\"")
							.append(options.getTxtLink()).append("\">");
				} else if (options.getTxtLink().trim().equals(rb.getString("btn.cancel").trim())) {
					data.append(
							"<img src=\"menu-images/cancel.jpg\" style=\"border:1px solid #efefef\" onmouseover=\"this.style.border='1px solid #afafaf'\" onmouseout=\"this.style.border='1px solid #efefef'\" title=\"")
							.append(options.getTxtLink()).append("\">");
				} else if (options.getTxtLink().trim().equals(rb.getString("btn.delete").trim())) {
					data.append(
							"<img src=\"menu-images/delete.jpg\" style=\"border:1px solid #efefef\" onmouseover=\"this.style.border='1px solid #afafaf'\" onmouseout=\"this.style.border='1px solid #efefef'\" title=\"")
							.append(options.getTxtLink()).append("\">");
				} else if (options.getTxtLink().trim().equals(rb.getString("btn.wfHistory").trim())) {
					data.append(
							"<img src=\"menu-images/history.jpg\" style=\"border:1px solid #efefef\" onmouseover=\"this.style.border='1px solid #afafaf'\" onmouseout=\"this.style.border='1px solid #efefef'\" title=\"")
							.append(options.getTxtLink()).append("\">");
				} else if (options.getTxtLink().trim().equals(rb.getString("btn.addProcess").trim())) {
					data.append(
							"<img src=\"menu-images/addProcess.jpg\" style=\"border:1px solid #efefef\" onmouseover=\"this.style.border='1px solid #afafaf'\" onmouseout=\"this.style.border='1px solid #efefef'\" title=\"")
							.append(options.getTxtLink()).append("\">");
				} else if (options.getTxtLink().trim().equals(rb.getString("btn.edit").trim())) {
					data.append(
							"<img src=\"menu-images/properties.jpg\" style=\"border:1px solid #efefef\" onmouseover=\"this.style.border='1px solid #afafaf'\" onmouseout=\"this.style.border='1px solid #efefef'\" title=\"")
							.append(options.getTxtLink()).append("\">");
				} else if (options.getTxtLink().trim().equals(rb.getString("btn.permission").trim())) {
					data.append(
							"<img src=\"menu-images/addUserCheck.jpg\" style=\"border:1px solid #efefef\" onmouseover=\"this.style.border='1px solid #afafaf'\" onmouseout=\"this.style.border='1px solid #efefef'\" title=\"")
							.append(options.getTxtLink()).append("\">");
				} else if (options.getTxtLink().trim().equals(rb.getString("btn.permission1").trim())) {
					data.append(
							"<img src=\"menu-images/addGroupCheck.jpg\" style=\"border:1px solid #efefef\" onmouseover=\"this.style.border='1px solid #afafaf'\" onmouseout=\"this.style.border='1px solid #efefef'\" title=\"")
							.append(options.getTxtLink()).append("\">");
				} else if (options.getTxtLink().trim().indexOf(rb.getString("cbs.pendingMails").trim()) != -1) {
					data.append(
							"<img src=\"menu-images/mail.jpg\" style=\"border:1px solid #efefef\" onmouseover=\"this.style.border='1px solid #afafaf'\" onmouseout=\"this.style.border='1px solid #efefef'\" title=\"")
							.append(options.getTxtLink()).append("\"  >");
				} else if (options.getTxtLink().trim().indexOf(rb.getString("cbs.pendingWF").trim()) != -1) {
					data.append(
							"<img src=\"menu-images/flujo.jpg\" style=\"border:1px solid #efefef\" onmouseover=\"this.style.border='1px solid #afafaf'\" onmouseout=\"this.style.border='1px solid #efefef'\" title=\"")
							.append(options.getTxtLink()).append("\">");

				} else if (options.getTxtLink().trim().indexOf(rb.getString("mail.new").trim()) != -1) {
					data.append(
							"<img src=\"menu-images/mailNew.jpg\" style=\"border:1px solid #efefef\" onmouseover=\"this.style.border='1px solid #afafaf'\" onmouseout=\"this.style.border='1px solid #efefef'\" title=\"")
							.append(options.getTxtLink()).append("\">");
				} else if (options.getTxtLink().trim().indexOf(rb.getString("mail.inbox").trim()) != -1) {
					data.append(
							"<img src=\"menu-images/mailInbox.jpg\" style=\"border:1px solid #efefef\" onmouseover=\"this.style.border='1px solid #afafaf'\" onmouseout=\"this.style.border='1px solid #efefef'\" title=\"")
							.append(options.getTxtLink()).append("\">");
				} else if (options.getTxtLink().trim().indexOf(rb.getString("mail.mailsSends").trim()) != -1) {
					data.append(
							"<img src=\"menu-images/mailSended.jpg\" style=\"border:1px solid #efefef\" onmouseover=\"this.style.border='1px solid #afafaf'\" onmouseout=\"this.style.border='1px solid #efefef'\" title=\"")
							.append(options.getTxtLink()).append("\">");
				} else if (options.getTxtLink().trim().indexOf(rb.getString("mail.delete").trim()) != -1) {
					data.append(
							"<img src=\"menu-images/delete.jpg\" style=\"border:1px solid #efefef\" onmouseover=\"this.style.border='1px solid #afafaf'\" onmouseout=\"this.style.border='1px solid #efefef'\" title=\"")
							.append(options.getTxtLink()).append("\">");
				} else if (options.getTxtLink().trim().indexOf(rb.getString("contacts.lista").trim()) != -1) {
					data.append(
							"<img src=\"menu-images/mailContact.jpg\" style=\"border:1px solid #efefef\" onmouseover=\"this.style.border='1px solid #afafaf'\" onmouseout=\"this.style.border='1px solid #efefef'\" title=\"")
							.append(options.getTxtLink()).append("\">");

				} else {
					data.append(options.getTxtLink());
				}
				data.append("</a>\n");
				data.append("</td>");
			}
			if (finalizarTabla) {
				data.append("<td width='100%'>&nbsp;");
				data.append("</td>");
				data.append("</tr></table>");
			}

			return data.toString();
		}
		return "";
	}

	public static String printMenuInner(Vector menu, String title) {
		if (menu != null && menu.size() > 0) {
			StringBuffer data = new StringBuffer(100);
			if (!isEmptyOrNull(title)) {
				data.append("<table class=\"clsTableTitle\" width=\"100%\" cellSpacing=0 cellPadding=2 align=center border=0>\n");
				data.append("   <tbody>\n");
				data.append("       <tr>\n");
				data.append("           <td class=\"td_title_bc\" height=\"21 px\">\n");
				data.append("               ").append(title).append("\n");
				data.append("           </td>\n");
				data.append("       </tr>\n");
				data.append("   </tbody>\n");
				data.append("</table>\n");
			}
			data.append("<table width=\"100%\" align=center border=0 cellSpacing=0 cellPadding=0>\n");
			int items = menu.size();
			float lenDiv = (float) (100.0 - (items - 1)) / items;
			data.append("   <tr>\n");
			data.append("       <td style=\"background: url(img/BgComments.gif); background-repeat: no-repeat\" height=\"26\">\n");
			data.append("           <table width=\"100%\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\">\n");
			data.append("               <tr>\n");
			BeanMenu options = null;
			for (int row = 0; row < items; row++) {
				options = (BeanMenu) menu.get(row);
				data.append("       <td width=\"").append(lenDiv).append("%\" align='center'>");
				data.append("           <a class=\"ahreMenuInner\" href='").append(options.getLink()).append("' target='").append(options.getTarget());
				data.append("'>").append(options.getTxtLink()).append("</a>\n");
				data.append("       </td>\n");
				if (row + 1 < items) {
					data.append("           <td width='1%'>\n");
					data.append("               <img src='img/separadorInternas.gif' width='1' height='10'/>\n");
					data.append("           </td>\n");
				}
			}
			data.append("              </tr>\n");
			data.append("           </table>\n");
			data.append("       </td>\n");
			data.append("   </tr>\n");
			data.append("</table>\n");
			return data.toString();
		}
		return "";
	}

	public static String printHeadPages(ResourceBundle rb, Locale local) {
		StringBuffer data = new StringBuffer("<table width=\"100%\" cellSpacing=0 cellPadding=0 align=center border=0>\n");
		data.append("   <tr>\n");
		data.append("       <td class=\"").append(rb.getString("headPage.css")).append("\" width=\"120\">&nbsp;</td>\n");
		data.append("       <td class=\"").append(rb.getString("headPage.css")).append("\" width=\"40%\" align=\"center\">\n");
		data.append("           ").append(rb.getString("application.name")).append("\n");
		data.append("       </td>\n");
		data.append("       <td class=\"").append(rb.getString("headPageDate.css")).append("\" width=\"*\" align=\"right\">\n");
		data.append("           ").append(formatDate(new java.util.Date(), local)).append("\n");
		data.append("       </td>\n");
		data.append("   </tr>\n");
		data.append("   <tr bgcolor=\"#003366\">\n");
		data.append("       <td bgcolor=\"#003366\" colspan=\"3\" align=\"center\">&nbsp;</td>");
		data.append("   </tr>\n");
		data.append("   <tr>\n");
		data.append("       <td bgcolor=\"#003366\">\n");
		data.append("       </td>\n");
		data.append("       <td colspan=\"2\" class='td_gris_l' valign=\"top\" align=\"left\">\n");
		data.append("           <img src=\"img/esquina.gif\" width=\"20\" height=\"20\">");
		data.append("       </td>\n");
		data.append("   </tr>\n");
		data.append("</table>\n");
		// //System.out.println("Loading Head page.......");
		return data.toString();
	}

	public static boolean checkValue(String value) {
		return ((value != null) && value.length() > 0);
	}

	/**
	 * Genera un n�mero entero del par�metro que se le envia
	 * 
	 * @param obj
	 * @return Un n�mero entero
	 */
	public static int getNumber(Object obj) {
		int lnRet = 0;
		//
		if (obj != null) {
			String lsNum = obj.toString();
			//
			if (lsNum != null) {
				try {
					lnRet = Integer.parseInt(lsNum);
				} catch (Exception e) {
					lnRet = 0;
				}
			}
		}
		return lnRet;
	}

	public static PaginPage getBeanPaginPage(String from, int size, String displayRows) {
		// int len = size;
		int numRecordShow = Integer.parseInt(displayRows);
		int numPages = 1;
		if (numRecordShow > 0) {
			numPages = size / numRecordShow;
		}
		if ((size % numRecordShow) != 0) {
			numPages++;
		}
		PaginPage bean = new PaginPage();
		String pagina = "0";
		if (!ToolsHTML.isNumeric(from)) {
			from = "0";
		}
		if (from != null) {
			int val = Integer.parseInt(from);
			if (val < 0)
				from = "0";
			else if (val >= numPages)
				from = String.valueOf(numPages - 1).trim();
			pagina = from;
		}
		bean.setFrom(from);
		bean.setCuantos(displayRows);
		bean.setPages(pagina);
		bean.setDesde(String.valueOf(Integer.parseInt(pagina) * numRecordShow));
		bean.setNumPages(numPages);
		return bean;
	}

	private static ArrayList getDefaults() {
		ArrayList elementos = new ArrayList();
		StringTokenizer st = new StringTokenizer(DesigeConf.getProperty("application.defaults"), ",");
		while (st.hasMoreTokens()) {
			String item = st.nextToken();
			elementos.add(item);
		}
		// elementos.add("usuario");
		// elementos.add("user");
		elementos.add(local);
		return elementos;
	}

	/**
	 * Este m�todo genera un Arraylist con los valores dados separados por comas
	 * 
	 * @param property
	 * @return
	 */
	public static ArrayList getList(String property) {
		return createList(DesigeConf.getProperty(property));
	}

	public static ArrayList createList(String elements) {
		ArrayList elementos = new ArrayList();
		if (!isEmptyOrNull(elements)) {
			StringTokenizer st = new StringTokenizer(elements, ",");
			while (st.hasMoreTokens()) {
				String[] item = st.nextToken().split("_");
				if (!elementos.contains(item[0])) {
					elementos.add(item[0]);
				}
			}
		}
		return elementos;
	}

	public static ArrayList createList(String elements, String selecteds) {
		ArrayList elementos = new ArrayList();
		if (!isEmptyOrNull(elements)) {
			StringTokenizer st = new StringTokenizer(elements, ",");
			while (st.hasMoreTokens()) {
				String[] item = st.nextToken().split("_");
				if (!elementos.contains(item[0])) {
					elementos.add(item[0]);
				}
			}
		}
		if (!isEmptyOrNull(selecteds)) {
			StringTokenizer st = new StringTokenizer(selecteds, ",");
			while (st.hasMoreTokens()) {
				String[] item = st.nextToken().split("_");
				if (!elementos.contains(item[0])) {
					elementos.add(item[0]);
				}
			}
		}
		return elementos;
	}

	public static void printObjectInSession(HttpSession session) {
		Enumeration atributteInSession = session.getAttributeNames();
		while (atributteInSession.hasMoreElements()) {
			String param = (String) atributteInSession.nextElement();
			log.debug("Object In Session.... param = " + param);
		}
	}

	public static void clearSession(HttpSession session, String property) {
		StringTokenizer st = new StringTokenizer(DesigeConf.getProperty(property), ",");
		// System.out.println("por eliminar = "+property);
		ArrayList elementos = getDefaults();
		while (st.hasMoreTokens()) {
			String item = st.nextToken();
			elementos.add(item);
		}
		Enumeration atributteInSession = session.getAttributeNames();
		while (atributteInSession.hasMoreElements()) {
			String param = (String) atributteInSession.nextElement();
			if (!elementos.contains(param)) {
				// System.out.println("removiendo attributos ="+param);
				session.removeAttribute(param);
			}
		}
	}

	public static void clearSession(HttpSession session) {
		Enumeration atributteInSession = session.getAttributeNames();
		while (atributteInSession.hasMoreElements()) {
			String param = (String) atributteInSession.nextElement();
			log.debug("Removiendo.... param = " + param);
			session.removeAttribute(param);
		}
	}

	public static Object getAttribute(HttpServletRequest request, String att) {
		Object result = request.getParameter(att);
		if (result == null) {
			result = request.getAttribute(att);
			if (result == null) {
				result = request.getSession().getAttribute(att);
			}
			return result;
		}
		return result;
	}

	/**
	 * Este m�todo permite obtener el atributo indicando bien sea si el mismo es
	 * un par�metro o se encuentra en el request como un atributo,
	 * adicionalmente el usuario puee indicar si desea remover dicho par�metro
	 * del request en caso de encontrarse como un atributo
	 * 
	 * @param request
	 * @param att
	 * @param remove
	 * @return
	 */
	public static Object getAttribute(HttpServletRequest request, String att, boolean remove) {
		Object result = request.getParameter(att);
		if (result == null) {
			log.debug("Atributo " + result);
			result = request.getAttribute(att);
			if (result != null && remove) {
				request.removeAttribute(att);
			}
		}
		
		return result;
	}

	// public static String processNode(String idNode,Hashtable
	// struct,HttpSession session){
	// try {
	// Hashtable nodes = new Hashtable();
	// Collection items = HandlerStruct.getChildsNode(idNode);
	// StringBuffer data = new StringBuffer();
	// // aux = new StringBuffer();
	// // aux.append("items = new Array(1000);\n");
	// int cont = 0;
	// for (Iterator iterator = items.iterator(); iterator.hasNext();) {
	// String id = (String) iterator.next();
	// BaseStructForm bean = (BaseStructForm)struct.get(id);
	// if (bean!=null) {
	// data.append("menu.MTMAddItem(new MTMenuItem(\"").append(bean.getName());
	// data.append("\",\"").append(idNode).append("\",\"\",\"").append(bean.getNameIcon()).append("\",\"");
	// data.append(id).append("\"));\n");
	// String dataNode = defaultRoot + cont + "]";
	// nodes.put(bean.getIdNode(),dataNode);
	// cont++;
	// }
	// }
	// cont = 0;
	// for (Iterator iterator = items.iterator(); iterator.hasNext();) {
	// String id = (String) iterator.next();
	// String father = defaultRoot + cont + "].";
	// Collection childs = HandlerStruct.getChildsNode(id);
	// data.append(getSubNodes(childs,"menu",cont,struct,cont,father,nodes,id));
	// cont++;
	// }
	// session.setAttribute("arbol",nodes);
	// return data.toString();
	// } catch (Exception e) {
	// e.printStackTrace();
	// }
	// return null;
	// }

	public static String processNode(String idNode, Hashtable struct, HttpSession session) {
		try {
			Hashtable nodes = new Hashtable();
			Hashtable subNodos = (Hashtable) session.getAttribute("subNodos");
			Collection items = (Vector) subNodos.get(idNode);
			StringBuffer data = new StringBuffer();
			int cont = 0;
			int indice = 0;
			if (items != null) {
				for (Iterator iterator = items.iterator(); iterator.hasNext();) {
					String id = (String) iterator.next();
					BaseStructForm bean = (BaseStructForm) struct.get(id);
					if (bean != null) {
						data.append("menu.MTMAddItem(new MTMenuItem(\"").append(bean.getName());
						data.append("\",\"").append(idNode).append("\",\"\",\"").append(bean.getNameIcon()).append("\",\"");
						data.append(id).append("\",").append(bean.getMajorId()).append(", ").append(bean.getMinorId()).append(", ").append(indice++)
								.append("));\n");
						//data.append(bean.getNodeType()).append("\"));\n");
						String dataNode = defaultRoot + cont + "]";
						nodes.put(bean.getIdNode(), dataNode);
						cont++;
					}
				}
				cont = 0;
				for (Iterator iterator = items.iterator(); iterator.hasNext();) {
					String id = (String) iterator.next();
					String father = defaultRoot + cont + "].";
					Collection childs = (Vector) subNodos.get(id);// HandlerStruct.getChildsNode(id);
					// data.append(getSubNodes(childs, "menu", cont, struct, cont, father, nodes, id, subNodos));
					data.append(getSubNodes(childs, "menu", cont, struct, Integer.parseInt(id), father, nodes, id, subNodos));
					cont++;
				}
				session.setAttribute("arbol", nodes);
				return data.toString();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	private static String getSubNodesII(Collection items, String nameNode, int pos, Hashtable datos, int num, String father, Hashtable arbol) {
		StringBuffer data = new StringBuffer();
		String var = "item" + num;
		int cont = (num + 1) * 20;
		int newCont = 0;
		boolean isSet = false;
		for (Iterator iterator = items.iterator(); iterator.hasNext();) {
			String id = (String) iterator.next();
			// System.out.println("[getSubNodesII] ID: " + id);

			BaseStructForm bean = (BaseStructForm) datos.get(id);

			if (bean != null && !bean.getNodeType().equalsIgnoreCase("5")) {
				if (!isSet) {
					data.append(var).append(" = new parent.frames['code'].MTMenu();\n");
					isSet = true;
				}
				data.append(var).append(".MTMAddItem(new parent.frames['code'].MTMenuItem(\"").append(bean.getName());
				Collection childs = HandlerStruct.getChildsNode(id);
				data.append("\",\"\",\"\",\"").append(bean.getNameIcon()).append("\",\"").append(id);
				data.append("\", ").append(bean.getMajorId()).append(", ").append(bean.getMinorId()).append("));\n");
				//data.append(bean.getNodeType()).append("\"));\n");
				StringBuffer actual = new StringBuffer(father).append("submenu.items[").append(newCont).append("]");
				arbol.put(id, actual.toString());
				// aux.append("items[").append(bean.getIdNode()).append("] =
				// ").append(actual).append("\n");
				actual.append(".");
				data.append(getSubNodesII(childs, var, newCont, datos, cont, actual.toString(), arbol));
				newCont++;
			}
		}
		if (items.size() > 0) {
			data.append(nameNode).append(".items[").append(pos).append("].");
			data.append("MTMakeSubmenu(").append(var).append(");\n");
		}
		return data.toString();
	}

	// private static String getSubNodes(Collection items,String nameNode,int
	// pos,
	// Hashtable datos,int num,String father,StringBuffer aux){
	// StringBuffer data = new StringBuffer();
	// String var = "item" + num;
	// int cont = (num+1)*20;
	// int newCont = 0;
	// boolean isSet = false;
	// for (Iterator iterator = items.iterator(); iterator.hasNext();) {
	// String id = (String) iterator.next();
	// BaseStructForm bean = (BaseStructForm)datos.get(id);
	// if (!bean.getNodeType().equalsIgnoreCase("5")){
	// if (!isSet){
	// data.append(var).append(" = new MTMenu();\n");
	// isSet = true;
	// }
	// data.append(var).append(".MTMAddItem(new
	// MTMenuItem(\"").append(bean.getName());
	// Collection childs = HandlerStruct.getChildsNode(id);
	// data.append("\",\"\",\"\",\"").append(bean.getNameIcon()).append("\",\"").append(id).append("\"));\n");
	// StringBuffer actual = new
	// StringBuffer(father).append("submenu.items[").append(newCont).append("]");
	// aux.append("items[").append(bean.getIdNode()).append("] =
	// ").append(actual).append("\n");
	// actual.append(".");
	// data.append(getSubNodes(childs,var,newCont,datos,cont,actual.toString(),aux));
	// newCont++;
	// }
	// }
	// if (items.size() > 0){
	// data.append(nameNode).append(".items[").append(pos).append("].");
	// data.append("MTMakeSubmenu(").append(var).append(");\n");
	// }
	// return data.toString();
	// }

	private static String getSubNodes(Collection items, String nameNode, int pos, Hashtable datos, int num, String father, Hashtable dataNodes, String idFather) {
		StringBuffer data = new StringBuffer();
		// String var = "item" + num;
		String var = "item" + String.valueOf(num).replaceAll("-", "_");
		int cont = (num + 1) * 20;
		int newCont = 0;
		boolean isSet = false;
		for (Iterator iterator = items.iterator(); iterator.hasNext();) {
			String id = (String) iterator.next();
			BaseStructForm bean = (BaseStructForm) datos.get(id);
			if (bean != null) {
				if (!bean.getNodeType().equalsIgnoreCase("5")) {
					if (!isSet) {
						data.append(var).append(" = new MTMenu();\n");
						isSet = true;
					}
					data.append(var).append(".MTMAddItem(new MTMenuItem(\"").append(bean.getName());
					Collection childs = HandlerStruct.getChildsNode(id);
					data.append("\",\"").append(idFather).append("\",\"\",\"").append(bean.getNameIcon()).append("\",\"").append(id);
					data.append("\", ").append(bean.getMajorId()).append(", ").append(bean.getMinorId()).append("));\n");
					//data.append(bean.getNodeType()).append("\"));\n");
					StringBuffer actual = new StringBuffer(father).append("submenu.items[").append(newCont).append("]");
					dataNodes.put(bean.getIdNode(), actual.toString());
					actual.append(".");
					data.append(getSubNodes(childs, var, newCont, datos, cont, actual.toString(), dataNodes, id));
					newCont++;
				}
			}
		}
		if (items.size() > 0 && isSet) {
			data.append(nameNode).append(".items[").append(pos).append("].");
			data.append("MTMakeSubmenu(").append(var).append(");\n");
		}
		return data.toString();
	}

	private static String getSubNodes(Collection items, String nameNode, int pos, Hashtable datos, int num, String father, Hashtable dataNodes,
			String idFather, Hashtable subNodos) {

		StringBuffer data = new StringBuffer();
		// String var = "item" + num;
		String var = "item" + String.valueOf(num).replaceAll("-", "_");

		int cont = (num + 1) * 20;
		int newCont = 0;
		boolean isSet = false;
		try {
			if (items != null) {
				for (Iterator iterator = items.iterator(); iterator.hasNext();) {
					String id = (String) iterator.next();
					BaseStructForm bean = (BaseStructForm) datos.get(id);
					if (bean != null) {
						if (!bean.getNodeType().equalsIgnoreCase("5")) {
							if (!isSet) {
								data.append(var).append(" = new MTMenu();\n");
								isSet = true;
							}
							BaseStructForm location = HandlerStruct.getLocationFromNode(datos, id);

							data.append(var).append(".MTMAddItem(new MTMenuItem(\"").append(bean.getName());
							Collection childs = (Vector) subNodos.get(id);// HandlerStruct.getChildsNode(id);
							data.append("\",\"").append(idFather).append("\",\"\",\"").append(bean.getNameIcon()).append("\",\"").append(id);
							data.append("\", ").append(location.getMajorId()).append(", ").append(location.getMinorId()).append(", ").append(newCont)
									.append("));\n");
							//data.append(bean.getNodeType()).append("\"));\n");
							StringBuffer actual = new StringBuffer(father).append("submenu.items[").append(newCont).append("]");
							dataNodes.put(bean.getIdNode(), actual.toString());
							actual.append(".");
							// data.append(getSubNodes(childs, var, newCont,datos, cont, actual.toString(), dataNodes,id, subNodos));
							data.append(getSubNodes(childs, var, newCont, datos, Integer.parseInt(id), actual.toString(), dataNodes, id, subNodos));
							newCont++;
						}
					}
					// break; // PENDIENTE:TEMPORAL PARA LOGRAR UN SOLO NODO
				}
			}
			if (items != null && items.size() > 0 && isSet) {
				// data.append(nameNode).append(".items[").append(pos).append("].");
				data.append(nameNode).append(".items[").append(nameNode).append(".items.length-1].");
				data.append("MTMakeSubmenu(").append(var).append(");\n");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return data.toString();
	}

	/**
	 * 
	 * @param idNode
	 * @param name
	 * @param struct
	 * @param arbol
	 * @return
	 */
	public static String getSubNodes(String idNode, String name, Hashtable struct, Hashtable arbol) {
		return getSubNodes(idNode, name, struct, arbol, true);
	}

	/**
	 * 
	 * @param idNode
	 * @param name
	 * @param struct
	 * @param arbol
	 * @param realodAllStruct
	 * @return
	 */
	public static String getSubNodes(String idNode, String name, Hashtable struct, Hashtable arbol, boolean realodAllStruct) {
		StringBuffer nodes = new StringBuffer(name);
		nodes.append(" = new parent.frames['code'].MTMenu();\n");
		Collection items = HandlerStruct.getChildsNode(idNode);
		int cont = 0;

		String raiz = (String) arbol.get(idNode);
		// //System.out.println("raiz = " + raiz);
		//ydavila Ticket Me emite el siguiente mensaje a pesar de que me mueve la carpeta al nuevo destino
		// incio de codigo descomentado 05-04-2022 DARWINUZCATEGUI
		
		for (Iterator iterator = items.iterator(); iterator.hasNext();) {
			String idChildNode = (String) iterator.next();
			BaseStructForm bean = (BaseStructForm) struct.get(idChildNode);
			nodes.append(name).append(".MTMAddItem(new parent.frames['code'].MTMenuItem(\"").append(bean.getName());
			nodes.append("\",\"\",\"\",\"").append(bean.getNameIcon()).append("\",\"");
			nodes.append(bean.getIdNode()).append("\", ");
			nodes.append(bean.getMajorId()).append(", ").append(bean.getMinorId()).append("));\n");
			//nodes.append(bean.getNodeType()).append("\"));\n");
			String dataNode = raiz + ".submenu.items[" + cont + "]";
			// //System.out.println("dataNode = " + dataNode);
			arbol.put(bean.getIdNode(), dataNode);
			// String father = "[]";
			Collection childs = HandlerStruct.getChildsNode(idChildNode);
			nodes.append(getSubNodesII(childs, name, cont, struct, cont, dataNode + ".", arbol));
			cont++;
	    	}
		// FIN CODIGO DESCOMNETDO 05-04-2022 DARWINUZCATEGUI
	    // ydavila Ticket b.	Me emite el siguiente mensaje a pesar de que me mueve la carpeta al nuevo destino
		return nodes.toString();
	}

	/**
	 * M�todo que permite obtener el Bundle para el manejo de los mensajes
	 * internacionalizados revisa si el usuario tiene un lenguaje definido en
	 * cuyo caso lo utiliza como lenguaje para mostrar la informaci�n, en caso
	 * contrario toma el lenguaje definido por el usuario en su equipo
	 * 
	 * @param request
	 * @return
	 */
	public static ResourceBundle getBundle(HttpServletRequest request) {
		ResourceBundle rb = null;
		Locale defaultLocale = null;
	    

		if (request != null) {
			// debemos buscar el locale configurado en el archivo de propiedades
			// Se busca el locale definido por el usuario
			String idRout = request.getParameter("idRout") !=null ? request.getParameter("idRout") : "";
		    defaultLocale = getLocale(request);
			rb = ResourceBundle.getBundle("LoginBundle", defaultLocale);
		}

		// Si el Locale definido por el usuario no se encuentra definido.......
		if (rb == null) {
			defaultLocale = new Locale(DesigeConf.getProperty("language.Default"), DesigeConf.getProperty("country.Default"));
			rb = ResourceBundle.getBundle("LoginBundle", defaultLocale);
		}

		return rb;
	}

	public static Locale getLocale(HttpServletRequest request) {
		Locale defaultLocale = (Locale) (request.getSession().getAttribute(local));
		if (defaultLocale == null) {
			defaultLocale = request.getLocale();
		}
		// log.debug("Using Locale : " + defaultLocale);
		return defaultLocale;
	}

	public static void setLanguage(Users usuario, HttpServletRequest request) {
		if (ToolsHTML.checkValue(usuario.getLanguage())) {
			log.debug("Idioma = ".concat(usuario.getLanguage()));
			request.getSession().setAttribute(ToolsHTML.local, new Locale(usuario.getLanguage(), usuario.getCountry()));
		}
	}

	public static String formatDateToShow(String date, boolean withHour) throws ParseException {
		if (isEmptyOrNull(date)) {
			return "N/A";
		} else {
			return formatDateShow(date, withHour);
		}
	}

	public static String cortarCadena(String cadena) throws ParseException {
		String cad = "";
		if (cadena != null) {
			int len = cadena.length();
			if (len > Constants.maxCaracteresEnCadena) {
				cad = cadena.substring(0, Constants.maxCaracteresEnCadena);
			} else {
				cad = cadena;
			}
			cad = cad.trim();
		} else {
			cad = cadena;
		}
		return cad;
	}

	/**
	 * 
	 * @param cadena
	 * @param nameDoc
	 * @return
	 * @throws ParseException
	 */
	public static String deleteNameDoc(String cadena, String nameDoc) throws ParseException {
		if (!isEmptyOrNull(cadena) && !isEmptyOrNull(nameDoc)) {
			if (cadena.startsWith("<a href=")) {
				cadena = deleteNameDocHref(cadena, nameDoc);
			} else {
				int indexName = cadena.indexOf("\\" + nameDoc);
				if (indexName != -1) {
					cadena = cadena.substring(0, indexName);
				} else {
					indexName = cadena.indexOf("/" + nameDoc);
					cadena = cadena.substring(0, indexName);
				}
			}
		}

		return cadena;
	}

	/**
	 * 
	 * @param cadena
	 * @param nameDoc
	 * @return
	 */
	public static String deleteNameDocHref(String cadena, String nameDoc) {
		if (cadena.contains(nameDoc)) {
			// debo eliminar el ultimo <a...></a>
			int indexName = cadena.lastIndexOf("<a href=");
			if (indexName != -1) {
				cadena = cadena.substring(0, indexName);
			}
		}

		return cadena;
	}

	/**
	 * Convertimos un String en un objeto fecha.
	 * 
	 * @param dateString
	 * @return
	 * @throws ParseException
	 */
	public static Date getDateFromString(String dateString) throws ParseException {
		Date date = null;

		try {
			if(dateString==null) {
				return null;
			}
			if(dateString.length()==10) {
				date = ToolsHTML.date.parse(dateString);
			} else {
				date = ToolsHTML.sdfShowConvert.parse(dateString);
			}
		} catch (Exception ex) {
			try {
				date = ToolsHTML.sdfShowConvert1.parse(dateString);
			} catch (Exception e) {
				date = null;
			}
		}

		return date;
	}

	/**
	 * 
	 * @param value
	 * @param withHour
	 * @return
	 * @throws ParseException
	 */
	public static String formatDateShow(String value, boolean withHour) throws ParseException {
		if (!isEmptyOrNull(value)) {
			java.util.Date date = null;
			String dateCreation = null;
			if (withHour) {
				try {
					date = ToolsHTML.sdfShowConvert.parse(value);
					dateCreation = ToolsHTML.sdfShow.format(date);
				} catch (Exception ex) {
					try {
						date = ToolsHTML.sdfShowConvert1.parse(value);
						dateCreation = ToolsHTML.sdfShow.format(date);
					} catch (Exception e) {
						return "";
					}
				}
			} else {
				try {
					date = ToolsHTML.date.parse(value);
					dateCreation = ToolsHTML.sdfShowWithoutHour.format(date);
				} catch (Exception ex) {
					date = ToolsHTML.sdfShowConvert1.parse(value);
					dateCreation = ToolsHTML.sdfShow.format(date);
				}
			}
			return dateCreation;
		}
		return "";
	}

	public static String formatDateShow(Date date, boolean withHour) throws ParseException {
		if (date!=null) {
			String dateCreation = null;
			if (withHour) {
				try {
					dateCreation = ToolsHTML.sdfShow.format(date);
				} catch (Exception ex) {
					try {
						dateCreation = ToolsHTML.sdfShow.format(date);
					} catch (Exception e) {
						return "";
					}
				}
			} else {
				try {
					dateCreation = ToolsHTML.sdfShowWithoutHour.format(date);
				} catch (Exception ex) {
					dateCreation = ToolsHTML.sdfShow.format(date);
				}
			}
			return dateCreation;
		}
		return "";
	}

	public static boolean setNodes(String idNode, Hashtable securityStruct, String user, String idGroup, HttpServletRequest request, String prefix)
			throws Exception {
		boolean setData = false;
		removeAttribute("documents", request);
		removeAttribute("nodes", request);
		removeAttribute("emptyNodes", request);
		removeAttribute("nodeActive", request);
		Collection documents = HandlerStruct.getAllDocuments(idNode, user, idGroup, "1", securityStruct, prefix);
		boolean isAdmon = idGroup.equalsIgnoreCase(DesigeConf.getProperty("application.admon"));
		if ((documents != null) && (documents.size() > 0)) {
			request.getSession().setAttribute("documents", documents);
			request.getSession().setAttribute("sizeDocs", String.valueOf(documents.size()));
			setData = true;
		}
		Collection struct = HandlerStruct.getAllNodes(idNode, "<>", securityStruct, user, isAdmon, idGroup);
		if ((struct != null) && (struct.size() > 0)) {
			request.getSession().setAttribute("nodes", struct);
			request.getSession().setAttribute("size", String.valueOf(struct.size()));
			setData = true;
		}
		return setData;
	}

	public static boolean setNodes(String idNode, Hashtable securityStruct, String user, String idGroup, HttpServletRequest request, String prefix,
			String typeNode) throws Exception {
		log.info("Iniciando ToolsHTML.setNodes()");
		boolean setData = false;
		removeAttribute("documents", request);
		removeAttribute("nodes", request);
		removeAttribute("emptyNodes", request);
		removeAttribute("nodeActive", request);
		boolean isAdmon = idGroup.equalsIgnoreCase(DesigeConf.getProperty("application.admon"));
		// Los Nodos Tipo Localidad no tienen Documentos
		if (typeNode != null && "1".compareTo(typeNode) != 0) {
			Collection documents = HandlerStruct.getAllDocuments(idNode, user, idGroup, "1", securityStruct, prefix);
			if ((documents != null) && (documents.size() > 0)) {
				request.getSession().setAttribute("documents", documents);
				request.getSession().setAttribute("sizeDocs", String.valueOf(documents.size()));
				setData = true;
			}
		}
		Collection struct = HandlerStruct.getAllNodes(idNode, "<>", securityStruct, user, isAdmon, idGroup);
		if ((struct != null) && (struct.size() > 0)) {
			request.getSession().setAttribute("nodes", struct);
			request.getSession().setAttribute("size", String.valueOf(struct.size()));
			setData = true;
		}
		log.info("Finalizando ToolsHTML.setNodes()");
		return setData;
	}

	private static void removeAttribute(String id, HttpServletRequest request) {
		Object obj = request.getSession().getAttribute(id);
		if (obj != null) {
			request.getSession().removeAttribute(id);
			log.debug("Eliminando " + id);
		}
	}

	private static void setLink(StringBuffer menu, String callTo, String text) {
		// menu.append(" <tr onMouseover=\"this.bgColor='#E8E8D0'\"
		// onMouseout=\"this.bgColor='#003366'\">\n");
		menu.append("           <tr >\n");
		menu.append("               <td align=\"center\" class=\"td_btn\" onmouseover=\"this.className='menuOver'\" onmouseout=\"this.className='td_btn'\" >\n");
		menu.append("                   <a href=\"").append(callTo).append("\" class=\"ahref_g\" style=\"width:100%\">\n");
		menu.append("                       ").append(text).append("\n");
		menu.append("                   </a>\n");
		menu.append("               </td>\n");
		menu.append("           </tr>\n");
	}

	private static void setLink(StringBuffer menu, String callTo, String text, String target) {
		// menu.append(" <tr onMouseover=\"this.bgColor='#E8E8D0'\"
		// onMouseout=\"this.bgColor='#003366'\">\n");
		menu.append("           <tr >\n");
		menu.append("               <td align=\"center\" class=\"td_btn\" onmouseover=\"this.className='menuOver'\" onmouseout=\"this.className='td_btn'\">\n");
		menu.append("                   <a href=\"").append(callTo).append("\" target=\"").append(target)
				.append("\" class=\"ahref_g\"  style=\"width:100%\">\n");
		menu.append("                       ").append(text).append("\n");
		menu.append("                   </a>\n");
		menu.append("               </td>\n");
		menu.append("           </tr>\n");
	}

	public static Vector getMenuMailsII(ResourceBundle rb) {
		BeanMenu options = null;
		Vector result = new Vector();
		options = new BeanMenu(rb.getString("mail.new"), rb.getString("href.inbox"), "_self");
		result.add(options);
		options = new BeanMenu(rb.getString("mail.inbox"), rb.getString("href.messages"), "_self");
		result.add(options);
		options = new BeanMenu(rb.getString("mail.mailsSends"), rb.getString("href.mailSents"), "_self");
		result.add(options);
		options = new BeanMenu(rb.getString("mail.delete"), "javascript:youAreSure();", "_self");
		result.add(options);
		options = new BeanMenu(rb.getString("contacts.lista"), rb.getString("href.contacts"), "_self");
		result.add(options);
		return result;
	}

	public static String getMenuMails(ResourceBundle rb) {
		StringBuffer menu = new StringBuffer("<br/>\n");
		menu.append("<table bgColor=\"#003366\" width=\"100%\" border=\"0\">\n");
		setLink(menu, rb.getString("href.inbox"), rb.getString("mail.new"));
		setLink(menu, rb.getString("href.messages"), rb.getString("mail.inbox"));
		setLink(menu, rb.getString("href.mailSents"), rb.getString("mail.mailsSends"));
		setLink(menu, "javascript:youAreSure();", rb.getString("mail.delete"));
		setLink(menu, rb.getString("href.contacts"), rb.getString("contacts.lista"));
		menu.append("</table>\n");
		return menu.toString();
	}

	// public static String getMenuDocuments(ResourceBundle rb,int
	// statu,DocumentsCheckOutsBean check,String usuario
	// ,boolean copySelected,String statuDoc,int idWF,int row,
	// boolean isResponse,PermissionUserForm permission,String idGroup
	// ,int mails,int wfs,String lastOperationApply
	// ,boolean showCreateReg) {
	// boolean pendingTask = false;
	// boolean isAdmon =
	// idGroup.equalsIgnoreCase(DesigeConf.getProperty("application.admon"));
	// StringBuffer menu = new StringBuffer("<table class=\"clsTableTitle\"
	// width=\"100%\" cellSpacing=0 cellPadding=0 align=center border=0>\n");
	// menu.append("<table bgColor=\"#003366\" width=\"100%\" border=\"0\">\n");
	// menu.append(" <tr>\n");
	// menu.append(" <td class=\"td_title_bwc\">\n");
	// menu.append(" ").append(rb.getString("doc.title")).append("\n");
	// menu.append(" </td>\n");
	// menu.append(" </tr>\n");
	// boolean isOwner = false;//owner.equalsIgnoreCase(usuario);
	//
	// if
	// (permission!=null&&permission.getToEditDocs()==Constants.permission||isOwner||isAdmon)
	// {
	// setLink(menu,"javascript:edit();",rb.getString("btn.edit"));
	// }
	// if
	// (permission!=null&&permission.getToDelDocs()==Constants.permission||isOwner||isAdmon)
	// {
	// setLink(menu,"javascript:youAreSure();",rb.getString("btn.delete"));
	// }
	//
	// if (copySelected) {
	// //Menu Pegar Nodo
	// setLink(menu,"javascript:pasteNode();",rb.getString("btn.paste"));
	// } else {
	// //Menu Copiar Nodo
	// if
	// (permission!=null&&permission.getToMoveDocs()==Constants.permission||isOwner||isAdmon)
	// {
	// setLink(menu,"javascript:isCopy();",rb.getString("btn.copy"));
	// }
	// }
	// if (check==null||!check.isCheckOut()) {
	// if ((!statuDoc.equalsIgnoreCase(HandlerDocuments.docInReview)
	// || !statuDoc.equalsIgnoreCase(HandlerDocuments.docInApproved))&&
	// isOwner||isAdmon||(permission!=null&&permission.getCheckOut()==Constants.permission))
	// {
	// setLink(menu,"javascript:loadCheckOut();",rb.getString("btn.checkOut"));
	// if (!isEmptyOrNull(lastOperationApply)) {
	// lastOperationApply.trim();
	// }
	// if (HandlerDocuments.lastCheckIn.equalsIgnoreCase(lastOperationApply)&&
	// (check!=null&&!check.isCheckOut()&&usuario.equalsIgnoreCase(check.getDoneBy())))
	// {
	// String option = "javascript:backCheckIn(" + check.getIdCheckOut() + ");";
	// setLink(menu,option,rb.getString("btn.checkInBack"));
	// }
	// }
	// } else {
	// if
	// ((isOwner||(permission!=null&&permission.getCheckOut()==Constants.permission)||isAdmon)&&
	// (check!=null&&check.isCheckOut()&&usuario.equalsIgnoreCase(check.getDoneBy())))
	// {
	// setLink(menu,"javascript:loadCheckIn();",rb.getString("btn.checkIn"));
	// String option = "javascript:backCheckOut(" + check.getIdCheckOut() +
	// ");";
	// setLink(menu,option,rb.getString("btn.undoCheckOut"));
	// }
	// }
	// if (idWF!=0&&row!=0) {
	// setLink(menu,"javascript:loadCloseWF("+ idWF + ","+ row +
	// ");",rb.getString("btn.closeTask"));
	// if ((statuDoc.equalsIgnoreCase(HandlerDocuments.docInReview)
	// || statuDoc.equalsIgnoreCase(HandlerDocuments.docInApproved)) &&
	// isResponse) {
	// setLink(menu,"javascript:showWF("+ idWF + ","+ row +
	// ");",rb.getString("btn.endTask"));
	// pendingTask = true;
	// }
	// }
	// setLink(menu,"javascript:loadHistory(0);",rb.getString("btn.wfHistory"));
	// menu.append(" <tr>\n");
	// menu.append(" <td class=\"td_title_bwc\">\n");
	// menu.append(" ").append(rb.getString("wf.title")).append("\n");
	// menu.append(" </td>\n");
	// menu.append(" </tr>\n");
	// if
	// (((statu==BaseDocumentForm.statuDraft)||(statu==BaseDocumentForm.statuApproved)
	// ||(statu==BaseDocumentForm.statuReview))&&!pendingTask){
	// if
	// ((permission!=null&&permission.getToReview()==Constants.permission)||isOwner||isAdmon)
	// {
	// setLink(menu,"javascript:createWF(0);",rb.getString("btn.wfReview"));
	// }
	// if
	// (permission!=null&&permission.getToAprove()==Constants.permission||isOwner||isAdmon)
	// {
	// setLink(menu,"javascript:createWF(1);",rb.getString("btn.wfAproved"));
	// }
	// }
	// // else {
	// // if (statu==BaseDocumentForm.statuReview) {
	// // setLink(menu,"javascript:createWF(1);",rb.getString("btn.wfAproved"));
	// // }
	// // }
	// setLink(menu,"javascript:loadHistory(1);",rb.getString("btn.wfHistory"));
	// if
	// (isOwner||isAdmon||(permission!=null&&permission.getToAdmonDocs()==Constants.permission))
	// {
	// menu.append(" <tr>\n");
	// menu.append(" <td class=\"td_title_bwc\">\n");
	// menu.append("
	// ").append(rb.getString("seguridad.permision")).append("\n");
	// menu.append(" </td>\n");
	// menu.append(" </tr>\n");
	// setLink(menu,"javascript:permission(0);",rb.getString("btn.permission"));
	// setLink(menu,"javascript:permission(1);",rb.getString("btn.permission1"));
	// setLink(menu,"javascript:downLoadLast();",rb.getString("btn.downLoadLast"));
	// setLink(menu,"javascript:sendAsLink();",rb.getString("btn.sendAsLink"));
	//
	// if (check!=null&&check.isCheckOut()) {
	// if
	// (isOwner||(permission!=null&&permission.getCheckOut()==Constants.permission))
	// {
	// String option = "javascript:backCheckOut(" + check.getIdCheckOut() +
	// ");";
	// setLink(menu,option,rb.getString("btn.undoCheckOut"));
	// }
	// }
	// }
	// setOptionsMsg(rb,menu,mails,wfs);
	// menu.append("</table>\n");
	// return menu.toString();
	// }
	// getStatuDoc
	public static String getMenuDocuments(ResourceBundle rb, DocumentsCheckOutsBean check, String usuario, boolean copySelected, int idWF, int row,
			boolean isResponse, PermissionUserForm permission, String idGroup, int mails, int wfs, BaseDocumentForm forma, boolean isFlexFlow) {
		try {

			boolean deshacerBloqueo = false;
			String statuDoc = forma.getStatuDoc().trim();
			int statu = Integer.parseInt(forma.getStatuDoc().trim());
			String lastOperationApply = forma.getLastOperation();
			String StatuVersion = String.valueOf(forma.getStatu());
			boolean pendingTask = false;
			// Se verifica si el Documento se encuentra
			if (statuDoc.equalsIgnoreCase(HandlerDocuments.docInReview) || statuDoc.equalsIgnoreCase(HandlerDocuments.docInApproved)
					|| statuDoc.equalsIgnoreCase(HandlerDocuments.inFlewFlow)) {
				pendingTask = true;
			}
			boolean canDelete = false;
			// el link borrar documento, debe aparecer si y solo si no hay
			// ninguna version aprobada
			// deben star todas en borrador o todas obsoletas
			StringBuffer sqlElim = new StringBuffer(" FROM versiondoc WHERE numDoc =").append(forma.getNumberGen());
			sqlElim.append(" AND statu='").append(HandlerDocuments.docApproved).append("'");
			sqlElim.append(" AND active = '").append(Constants.permission).append("'");
			if (ToolsHTML.isEmptyOrNull(HandlerDocuments.getField("statu", sqlElim.toString(),Thread.currentThread().getStackTrace()[1].getMethodName()))) {
				canDelete = true;
			}
			boolean isTypePrint = HandlerDocuments.TypeDocumentsImpresion.equalsIgnoreCase(forma.getTypeDocument());
			boolean isAdmon = idGroup.equalsIgnoreCase(DesigeConf.getProperty("application.admon"));
			StringBuffer menu = new StringBuffer("<table class=\"clsTableTitle\" width=\"100%\" cellSpacing=0 cellPadding=0 align=center border=0>\n");
			menu.append("<table bgColor=\"#003366\" width=\"100%\" border=\"0\">\n");
			menu.append("           <tr>\n");
			menu.append("               <td class=\"td_title_bwc\">\n");
			menu.append("                       ").append(rb.getString("doc.title")).append("\n");
			menu.append("               </td>\n");
			menu.append("           </tr>\n");

			// Propiedades del Documento
			if ((permission != null && permission.getToEditDocs() == Constants.permission || isAdmon)
					&& (!HandlerDocuments.docObs.equalsIgnoreCase(String.valueOf(statu)))
					&& (HandlerDocuments.docInApproved.compareTo(statuDoc) != 0 && HandlerDocuments.docInReview.compareTo(statuDoc) != 0) && !isTypePrint) {
				if (!pendingTask) {
					setLink(menu, "javascript:edit();", rb.getString("btn.edit"));
				}
			}

			String statuST = statuDoc;// String.valueOf(statu);
			boolean isDocumentStatuVersionEraser = HandlerDocuments.isDocumentStatuVersionEraser(Integer.parseInt(forma.getIdDocument()));

			// Si tiene permiso para modificar propiedades puede publicar y si es propietario
			if ((permission != null && permission.getToPublicEraser() == Constants.permission) && forma.getOwner() != null && forma.getOwner().equals(usuario)
					&& isDocumentStatuVersionEraser ) {
				if (check == null || !check.isCheckOut()) {
					if ((idWF == 0 && row == 0) && (!HandlerDocuments.docObs.equalsIgnoreCase(statuST))) { // si
						// no puede cerrar el flujo puede ver el publicar
						setLink(menu, "javascript:public();", rb.getString("btn.public"));
					}
				}
			}

			if ((((permission != null && permission.getToDelDocs() == Constants.permission) || isAdmon) && ((!HandlerDocuments.docInApproved
					.equalsIgnoreCase(statuST))
					&& (!HandlerDocuments.docInReview.equalsIgnoreCase(statuST))
					&& (!HandlerDocuments.docReview.equalsIgnoreCase(statuST))
					&& (!HandlerDocuments.docRejected.equalsIgnoreCase(statuST))
					&& (!HandlerDocuments.docApproved.equalsIgnoreCase(statuST))
			// && (canDelete)//si no existe ninguna versioanterior n aprobada
			|| (((canDelete) && HandlerDocuments.docTrash.equalsIgnoreCase(StatuVersion) && !HandlerDocuments.docInReview.equalsIgnoreCase(statuST) && !HandlerDocuments.docInApproved
					.equalsIgnoreCase(statuST)) || ((canDelete) && HandlerDocuments.docObs.equalsIgnoreCase(StatuVersion)
			// si es un archivo de impresion, se le permite eliminar el
			// archivo asi este aprobado,
			// pero que no este en aprobacion o en revision
			|| (HandlerDocuments.TypeDocumentsImpresion.equalsIgnoreCase(forma.getTypeDocument())
					&& (!HandlerDocuments.docInApproved.equalsIgnoreCase(statuST)) && (!HandlerDocuments.docInReview.equalsIgnoreCase(statuST)))))))
					&& !pendingTask) {

				if (check == null || !check.isCheckOut()) {
					setLink(menu, "javascript:youAreSure(0);", rb.getString("btn.delete"));
				}
			}
			// Si el Usuario tiene permiso para eliminar el Documento
			// y la �ltima Versi�n es un Borrador y Existe una Versi�n Aprobada,
			// se procede a Eliminar
			// Dicha Versi�n, siempre y cuando el Documento no se encuentre
			// sometido a un Flujo de Trabajo
			if (((permission != null && permission.getToDelDocs() == Constants.permission) || isAdmon)
					&& ((HandlerDocuments.docApproved.equalsIgnoreCase(forma.getStatuDoc()) && HandlerDocuments.docTrash.equalsIgnoreCase(String.valueOf(forma
							.getStatu()))) || (HandlerDocuments.docTrash.equalsIgnoreCase(String.valueOf(forma.getStatu())))
							&& !"0".equalsIgnoreCase(forma.getLastVersionApproved()))
					&& (!HandlerDocuments.docInReview.equalsIgnoreCase(statuST) && !HandlerDocuments.docInApproved.equalsIgnoreCase(statuST))) {
				// no puede eliminarse una version borrador, estando ya el
				// documento en borrador
				if (!StatuVersion.equalsIgnoreCase(statuDoc) && !(check != null && check.isCheckOut()) && !pendingTask) {
					setLink(menu, "javascript:youAreSure(1);", rb.getString("btn.deleteVer"));
				}
			}
			

			if ((permission != null && permission.getToMoveDocs() == Constants.permission || isAdmon)
					&& (!HandlerDocuments.docInApproved.equalsIgnoreCase(statuST)) && (!HandlerDocuments.docInReview.equalsIgnoreCase(statuST))
					// si es un archivo de impresion, no se le permite ver esta
					// opcion
					&& (!HandlerDocuments.TypeDocumentsImpresion.equalsIgnoreCase(forma.getTypeDocument()))) {
				if (copySelected) {
					// Menu Pegar Nodo
					setLink(menu, "javascript:pasteNode();", rb.getString("btn.paste"));
				} else {
					// Menu Copiar Nodo
					if ((!pendingTask) && ((permission != null && permission.getToMoveDocs() == Constants.permission) || isAdmon)) {
						setLink(menu, "javascript:isCopy();", rb.getString("btn.copy"));
					}
				}
			}

			// se permite bloquear al usuario propietario aun sin tener permiso de bloquear 2017-02-16 
			boolean isOwner = forma.getOwner() != null && forma.getOwner().equals(usuario);
			if ((permission != null && permission.getCheckOut() == Constants.permission || isAdmon || isOwner)
					&& (!HandlerDocuments.docInApproved.equalsIgnoreCase(statuST)) && (!HandlerDocuments.docInReview.equalsIgnoreCase(statuST))
					// si es un archivo de impresion, no se le permite ver esta
					&& (!HandlerDocuments.TypeDocumentsImpresion.equalsIgnoreCase(forma.getTypeDocument()) )) {	
						if (check == null || !check.isCheckOut()) {
					if ((!statuDoc.equalsIgnoreCase(HandlerDocuments.docInReview)  || !statuDoc.equalsIgnoreCase(HandlerDocuments.docInApproved)) && isAdmon
							|| (permission != null && permission.getCheckOut() == Constants.permission)) {
						if ((idWF == 0 && row == 0) && (!HandlerDocuments.docObs.equalsIgnoreCase(statuST))
								// opcion  GRG 26 AGOSTO 2020 se agrego tipo de documento REGISTRO TIPO REGISTRO a la condicion de NO visualizacion del boton Bloquear y este aprobado
								// darwinUzcategui opcion  bloquear registro 21 abril 2022 se agrego tipo de documento REGISTRO TIPO REGISTRO a la condicion de NO visualizacion del boton Bloquear y este aprobado
								// && (HandlerDocuments.TypeDocumentsRegistro.equalsIgnoreCase(forma.getTypeDocument()) &&  HandlerDocuments.docBorrador.equalsIgnoreCase(statuST))
								) {
							if (!HandlerDocuments.TypeDocumentsRegistro.equalsIgnoreCase(forma.getTypeDocument())) {
								
								setLink(menu, "javascript:loadCheckOut();", rb.getString("btn.checkOut"));
								
								
							} 
							if (HandlerDocuments.TypeDocumentsRegistro.equalsIgnoreCase(forma.getTypeDocument()) &&  HandlerDocuments.docBorrador.equalsIgnoreCase(statuST)) {
								
								setLink(menu, "javascript:loadCheckOut();", rb.getString("btn.checkOut"));
								
							}
							
							
						}
						if (!isEmptyOrNull(lastOperationApply)) {
							lastOperationApply.trim();
						}
						//ydavila Ticket 001-00-003023
						//aqu� va el bot�n de ratificar versi�n
						String numVer = HandlerDocuments.getMayorVersion(forma.getIdDocument());
						String isUpd = HandlerDocuments.checkIsUpd(numVer); // busca si el documento es una actualizacion, solo asi se podra deshacer los cambios
						if ("1".equalsIgnoreCase(isUpd) && HandlerDocuments.lastCheckIn.equalsIgnoreCase(lastOperationApply)
								&& (check != null && !check.isCheckOut() && usuario.equalsIgnoreCase(check.getDoneBy()))) {
							String option = "javascript:backCheckIn(" + check.getIdCheckOut() + ");";
							setLink(menu, option, rb.getString("btn.checkInBack"));
						}

						// si es un registro para sacop y es el propietario del documento
						if (!pendingTask && forma.getOwner() != null && forma.getOwner().equals(usuario) && forma.getIdRegisterClass()>1 && statuDoc.equalsIgnoreCase(HandlerDocuments.docApproved)) {
							// consultamos si tiene una sacop pendiente
							PlanillaSacop1TO oPlanillaSacop1TO = new PlanillaSacop1TO();
							oPlanillaSacop1TO.setIdDocumentRelated(forma.getNumberGen());
							
							PlanillaSacop1DAO oPlanillaSacop1DAO = new PlanillaSacop1DAO();
							
							if(oPlanillaSacop1DAO.cargarSacopPendienteDelDocumento(oPlanillaSacop1TO)) {
								/*
								StringBuffer sb = new StringBuffer();
								sb.append("javascript:showSacopPendiente(");
								sb.append(oPlanillaSacop1TO.getIdplanillasacop1());
								sb.append(",'").append(oPlanillaSacop1TO.getSacopnum());
								sb.append("','").append(formatDateShow(oPlanillaSacop1TO.getFechaSacops1(),false));
								sb.append("','").append(formatDateShow(oPlanillaSacop1TO.getFechaemision(),false));
								sb.append("','").append(formatDateShow(oPlanillaSacop1TO.getFechaWhenDiscovered(),false));
								sb.append("');");
								*/
								setLink(menu, "javascript:loadSacop();", rb.getString("btn.wfSacopInProcess"));
							} else {
								setLink(menu, "javascript:loadSacop();", rb.getString("btn.wfSacopRequest"));
							}
						}

					}
				} else {
					if (((permission != null && permission.getCheckOut() == Constants.permission) || isAdmon) // || isOwner
							&& (check != null && check.isCheckOut() && usuario.equalsIgnoreCase(check.getDoneBy()))) {
						setLink(menu, "javascript:loadCheckIn();", rb.getString("btn.checkIn"));
						String option = "javascript:backCheckOut(" + check.getIdCheckOut() + ");";
						deshacerBloqueo = true;
						setLink(menu, option, rb.getString("btn.undoCheckOut"));
					}
				}
			}

			// TODO: aqui esta donde se debe colocar el boton de completar flujo
			if (idWF != 0 && row != 0) {
				// si no es un archivo de impresion, pueden cerrar el flujo.
				if (!HandlerDocuments.TypeDocumentsImpresion.equalsIgnoreCase(forma.getTypeDocument())) {
					// tiene que ser un flujo normal para poder cerrar el flujo
					if (isFlexFlow) { // HandlerDocuments.isFlexWorkFlowPending(forma.getIdDocument()
						boolean completeFTP = String.valueOf(HandlerParameters.PARAMETROS.getCompleteFTP()).equals("1");
						// consultamos si en este momento el usuario tiene un
						// flujo pendiente
						// jairo
						UserFlexWorkFlowsTO userFlexWorkFlowsTO = new UserFlexWorkFlowsTO();
						userFlexWorkFlowsTO.setIdUser(usuario);
						userFlexWorkFlowsTO.setIdWorkFlow(idWF);
						if (completeFTP && HandlerDocuments.isUserFlexFlowPendding(userFlexWorkFlowsTO)) {
							if ((permission != null && permission.getToCompleteFlow() == Constants.permission || isAdmon)
									&& (!HandlerDocuments.docInApproved.equalsIgnoreCase(statuST)) && (!HandlerDocuments.docInReview.equalsIgnoreCase(statuST))) {
								setLink(menu, "javascript:loadCloseWF(" + idWF + "," + row + "," + isFlexFlow + ");", rb.getString("btn.completeFTP"));
							}
						}
					} else {
						setLink(menu, "javascript:loadCloseWF(" + idWF + "," + row + "," + isFlexFlow + ");", rb.getString("btn.closeTask"));
					}
				}

				if ((statuDoc.equalsIgnoreCase(HandlerDocuments.docInReview) || statuDoc.equalsIgnoreCase(HandlerDocuments.docInApproved)) && isResponse) {
					// setLink(menu,"javascript:showWF("+ idWF + ","+ row +
					// ");",rb.getString("btn.endTask"));
					pendingTask = true;
				}
				setLink(menu, "javascript:showWF(" + idWF + "," + row + ");", rb.getString("btn.endTask"));
			}
			String statuVer = String.valueOf(forma.getStatu()).trim();
			boolean showCreateReg = Constants.tipoFormato.equalsIgnoreCase(forma.getTypeFormat());
		 
 			boolean showTipoR =     Constants.tipoRegistro.equalsIgnoreCase(forma.getTypeDocument());
			boolean showSubTipoR = (Constants.subtipoRegistro == forma.getIdRegisterClass());
			boolean havePreviousApprovedVersion = !"0".equals(forma.getLastVersionApproved());
			// if (showCreateReg &&
			// ((HandlerDocuments.docApproved.equalsIgnoreCase(statuST)) ||
			// (pendingTask && HandlerDocuments.docApproved.compareTo(statuVer)
			// == 0)) && ((permission != null && permission.getToEditRegister()
			// == Constants.permission) || isAdmon)) {
			// es un documento tipo formato, verificamos si debemos mostrar
			// el boton de crear registro
			/// if (showCreateReg || showCreateReg1002)  { 
			   if (showCreateReg ||showSubTipoR)  {                                                     
				if (!havePreviousApprovedVersion) {
					log.info("El documento (de tipo formato) '" + forma.getNameDocument()
							+ "', no tiene version aprobada previa, por lo tanto, no se puede crear un registro a partir de el");
				} else {
					//modificacion aqui
					
					if (showCreateRegister()) {
						//if ((HandlerDocuments.docApproved.equalsIgnoreCase(statuST) || HandlerDocuments.docReview.equalsIgnoreCase(statuST))
					//	System.out.println();                                                                         docInReview
						if ((HandlerDocuments.docApproved.equalsIgnoreCase(statuST) || HandlerDocuments.docReview.equalsIgnoreCase(statuST)  )
								|| (pendingTask && HandlerDocuments.docApproved.compareTo(statuVer) == 0)) {
							if( !HandlerDocuments.docInReview.equals(statuST)   ) {
								setLink(menu, "javascript:createRegister(0);", rb.getString(showCreateReg ? "btn.createReg" : "btn.copyReg"));
														
							}
							
							 // setLink(menu, "javascript:createRegister(0);", rb.getString(showCreateReg ? "btn.createReg" : "btn.copyReg"));
						}
					} else {
						log.info("No esta habilitada en el sistema la opcion de crear registros");
					}
				}
				 
			}
			
			
			
			showCreateReg = Constants.tipoPlantilla.equalsIgnoreCase(forma.getTypeFormat());
			if (showCreateReg) {
				// es un documento tipo plantilla, verificamos si debemos
				// mostrar el boton de crear Documento
				if (!havePreviousApprovedVersion) {
					log.info("El documento (tipo plantilla) '" + forma.getNameDocument()
							+ "', no tiene version aprobada previa, por lo tanto, no se puede crear un registro a partir de el");
				} else {
					if (showCreateRegister()) {
						if ((HandlerDocuments.docApproved.equalsIgnoreCase(statuST) || HandlerDocuments.docReview.equalsIgnoreCase(statuST)) || pendingTask
								&& HandlerDocuments.docApproved.compareTo(statuVer) == 0) {
							setLink(menu, "javascript:createRegister(1);", rb.getString("btn.createDoc"));
						}
					} else {
						log.info("No esta habilitada en el sistema la opcion de crear Documento");
					}
				}
			}

			// if
			// (isAdmon||((showCreateReg)&&(!HandlerDocuments.docObs.equalsIgnoreCase(statuST))
			// //si es un archivo de impresion, no se le permite ver esta opcion
			// &&(!HandlerDocuments.TypeDocumentsImpresion.equalsIgnoreCase(forma.getTypeDocument()))
			// )) {
			// setLink(menu,"javascript:createRegister();",rb.getString("btn.createReg"));
			// }

			if (isAdmon || (!HandlerDocuments.TypeDocumentsImpresion.equalsIgnoreCase(forma.getTypeDocument()))) {
				// si es un archivo de impresion, no se le permite ver esta
				// opcion
				setLink(menu, "javascript:loadHistory(0);", rb.getString("btn.wfHistory"));
			}

			// inicio de la seccion de flujo de trabajo
			// dibujamos la cabecera
			menu.append("           <tr>\n");
			menu.append("               <td class=\"td_title_bwc\">\n");
			// si es un archivo de impresion, no se le permite ver esta
			// opcion
			if (!HandlerDocuments.TypeDocumentsImpresion.equalsIgnoreCase(forma.getTypeDocument())) {
				menu.append("                       ").append(rb.getString("wf.title")).append("\n");
			} else {
				menu.append("                       ").append(rb.getString("imp.title")).append("\n");
			}
			menu.append("               </td>\n");
			menu.append("           </tr>\n");
			if (showFlujo() && !deshacerBloqueo) {
				if ((statu == BaseDocumentForm.statuDraft || (statu == BaseDocumentForm.statuApproved) || (statu == BaseDocumentForm.statuReview)
						|| (statu == BaseDocumentForm.docRejected) || (statu == BaseDocumentForm.statudocObs))
						&& !pendingTask) {
					if ((permission != null && permission.getToReview() == Constants.permission) || isAdmon) {
						// si es un archivo de impresion, no se le permite ver
						// esta
						// opcion
						if (!HandlerDocuments.TypeDocumentsImpresion.equalsIgnoreCase(forma.getTypeDocument())) {
							//ydavila Ticket 001-00-003023
							// GRG 3 sep 2020  statu se cambiara por StatuVersion ya que aunque tenga un APROBADO se toma en cuenta el estado de la ultima version
 						if (Integer.parseInt(StatuVersion) != BaseDocumentForm.statuApproved) {	
							setLink(menu, "javascript:createWF(0);", rb.getString("btn.wfReview"));
							//setLink(menu, "javascript:createWF(0);", rb.getString("btn.wfRevEliCam"));
 						}  
						}
					}
				}
				

				if (((statu == BaseDocumentForm.statuDraft) || (statu == BaseDocumentForm.docRejected) || (statu == BaseDocumentForm.statuApproved) || (statu == BaseDocumentForm.statuReview))
						&& !pendingTask) {
					if (permission != null && permission.getToAprove() == Constants.permission || isAdmon) {
						// si es un archivo de impresion, no se le permite ver
						// esta
						// opcion
						if (!HandlerDocuments.TypeDocumentsImpresion.equalsIgnoreCase(forma.getTypeDocument())) {
							// GRG 3 sep 2020  statu se cambiara por StatuVersion ya que aunque tenga un APROBADO se toma en cuenta el estado de la ultima version
 						if (Integer.parseInt(StatuVersion) != BaseDocumentForm.statuApproved) {	
								setLink(menu, "javascript:createWF(1);", rb.getString("btn.wfAproved"));
 							}
						}
					}
				}
				log.debug("pendingTask = " + pendingTask);

				//ydavila Ticket 001-00-003023 Flujo para solicitud de Cambio
				//if ((statu == BaseDocumentForm.statuDraft || (statu == BaseDocumentForm.statuApproved) || (statu == BaseDocumentForm.statuReview)
				//		|| (statu == BaseDocumentForm.docRejected) || (statu == BaseDocumentForm.statudocObs))
				//		&& !pendingTask) {
			  if (statu == BaseDocumentForm.statuApproved && !pendingTask){
					//if ((permission != null && permission.getToReview() == Constants.permission) || isAdmon) {
					if ((permission != null) || isAdmon) {
					  if (check == null || !check.isCheckOut()) {			
						// si es un archivo de impresion, no se le permite ver
						// esta
						// opcion						
						if (!HandlerDocuments.TypeDocumentsImpresion.equalsIgnoreCase(forma.getTypeDocument()) 
								//ydavila 001-00-003023 para ver este bot�n el documento no debe tener un borrador
								&& StatuVersion.equalsIgnoreCase(statuDoc) && !showTipoR ) {
							setLink(menu, "javascript:createWF(3);", rb.getString("btn.wfSolCambio"));
						}
					  }
				    }		
				}		  
				// ratificar   
				if( permission != null && permission.getCheckOut() == Constants.permission) {  // si tiene permiso para bloquear/modificar
					if(!pendingTask) { // si no tiene tareas pendientes
						if(!isDocumentStatuVersionEraser) { // si el documento no tiene un borrador
							if ( statu == BaseDocumentForm.statuApproved || statu == BaseDocumentForm.statuReview || statu == BaseDocumentForm.docRejected || statu == BaseDocumentForm.statudocObs ) { // si esta aprobado,revisado,rechazado,obsoleto
								if (!HandlerDocuments.TypeDocumentsImpresion.equalsIgnoreCase(forma.getTypeDocument())) { // si no es documento de impresion
									if (check == null || !check.isCheckOut()) { // si no esta bloqueado
										boolean isNearExpire = HandlerParameters.DOCUMENTOS_POR_VENCER.contains(Integer.parseInt(forma.getNumberGen())); // si el documento esta por vencer
										
										long dateExpireTime = sdfShowWithoutHour.parse(forma.getDateExpires()).getTime();
										long dateNowTime = sdfShowWithoutHour.parse(sdfShowWithoutHour.format(new Date())).getTime();
										
										boolean isExpire = (dateNowTime >= dateExpireTime);  // si esta expirado
										if(isExpire || isNearExpire) { // si esta expirado o por expirar  
											setLink(menu, "javascript:ratify();", rb.getString("btn.versionRatify"));
										}
									}
								}
							}
						}
					}
				}

				  
				  //ydavila Ticket 001-00-003023 Flujo para Solicitud de Eliminaci�n
				//if ((statu == BaseDocumentForm.statuDraft || (statu == BaseDocumentForm.statuApproved) || (statu == BaseDocumentForm.statuReview)
				//		|| (statu == BaseDocumentForm.docRejected) || (statu == BaseDocumentForm.statudocObs))
				//		&& !pendingTask) {
			 if ((statu == BaseDocumentForm.statuApproved) && !pendingTask) {
					//if ((permission != null && permission.getToReview() == Constants.permission) || isAdmon) {
			   if ((permission != null) || isAdmon) {
				   if (check == null || !check.isCheckOut()) {	 
						// si es un archivo de impresion, no se le permite ver
						// esta
						// opcion
					if (!HandlerDocuments.TypeDocumentsImpresion.equalsIgnoreCase(forma.getTypeDocument()) 
								//ydavila 001-00-003023 para ver este bot�n el documento no debe tener un borrador
						&& StatuVersion.equalsIgnoreCase(statuDoc)) {
						setLink(menu, "javascript:createWF(4);", rb.getString("btn.wfSolElimin"));
						}
					  }	
					}
				}
				
				// Flujo Param�trico
				// Luis Cisneros
				// 02/03/07
				// La Opcion de flujo parametrico es opcional, dependiendo de la
				// licencia.
				if (showFTP()
						&& ((statu == BaseDocumentForm.statuDraft) || (statu == BaseDocumentForm.docRejected) || (statu == BaseDocumentForm.statuApproved) || (statu == BaseDocumentForm.statuReview))
						&& !pendingTask) {
					if (isAdmon || (permission != null && permission.getToFlexFlow() == Constants.permission)) {
						// si es un archivo de impresion, no se le permite ver esta opcion
						if (!HandlerDocuments.TypeDocumentsImpresion.equalsIgnoreCase(forma.getTypeDocument())) {
							// GRG 3 sep 2020  statu se cambiara por StatuVersion ya que aunque tenga un APROBADO se toma en cuenta el estado de la ultima version
 							if (Integer.parseInt(StatuVersion) != BaseDocumentForm.statuApproved) {	
							setLink(menu, "javascript:createWF(2);", rb.getString("btn.wfFlexFlow"));
 						}
						}	
					}
				}
				// el historico de flujos debe verse independientemente de si el
				// documento
				// esta bloqueado o no
				// setLink(menu, "javascript:loadHistory(1);",
				// rb.getString("btn.wfHistory"));
			}

			// flujo de solicitud de impresion
			// Todo: Por lo momento la seguridad es solos los aprobados 
		  if (statu == BaseDocumentForm.statuApproved && !pendingTask){
				if ((permission != null) || isAdmon) {
				  if (check == null || !check.isCheckOut()) {			
					// si es un archivo de impresion, no se le permite ver
					// esta
					// opcion						
					if (!HandlerDocuments.TypeDocumentsImpresion.equalsIgnoreCase(forma.getTypeDocument()) 
							&& StatuVersion.equalsIgnoreCase(forma.getStatu()+"") && !showTipoR ) {
						setLink(menu, "javascript:solicitudImpresion();", rb.getString("btn.solicitudImpresion"));
					}
				  }
			    }		
			}
			// historico
			setLink(menu, "javascript:loadHistory(1);", rb.getString("btn.wfHistory"));
			// fin de la seccion de flujo de trabajo

			// si es un archivo de impresion, no se le permite ver esta opcion
			if (!HandlerDocuments.TypeDocumentsImpresion.equalsIgnoreCase(forma.getTypeDocument())) {
				boolean crearMenu = false;
				if (isAdmon || (permission != null && permission.getToAdmonDocs() == Constants.permission)) {
					crearMenu = true;
				}
				if (isAdmon || (permission != null && permission.getToDownload() == Constants.permission)) {
					crearMenu = true;
				}
				if (crearMenu) {
					menu.append("           <tr>\n");
					menu.append("               <td class=\"td_title_bwc\">\n");
					menu.append("                       ").append(rb.getString("seguridad.permision")).append("\n");
					menu.append("               </td>\n");
					menu.append("           </tr>\n");
				}

				if (isAdmon || (permission != null && permission.getToAdmonDocs() == Constants.permission)) {
					setLink(menu, "javascript:permission(0);", rb.getString("btn.permission"));
					setLink(menu, "javascript:permission(1);", rb.getString("btn.permission1"));

					// YSA 30-01-2008 Se comenta opcion Enviar como Link
					// setLink(menu, "javascript:sendAsLink();",
					// rb.getString("btn.sendAsLink"));

					// if (check!=null&&check.isCheckOut()) {
					// if
					// (permission!=null&&permission.getCheckOut()==Constants.permission)
					// {
					// String option = "javascript:backCheckOut(" +
					// check.getIdCheckOut() + ");";
					// setLink(menu,option,rb.getString("btn.undoCheckOut"));
					// }
					// }
				}

				if (isAdmon || (permission != null && permission.getToDownload() == Constants.permission)) {
					boolean downloadLastVer = String.valueOf(HandlerParameters.PARAMETROS.getDownloadLastVer()).equals("1");
					// YSA: consultamos si esta opcion habilitada para Bajar
					// ultima version
					if (downloadLastVer) {
						setLink(menu, "javascript:downLoadLast();", rb.getString("btn.downLoadLast"));
						setLink(menu, "javascript:downLoadLastEraser();", rb.getString("btn.downLoadLastEraser"));
					}
				}
			}

			setOptionsMsg(rb, menu, mails, wfs);
			menu.append("</table>\n");
			return menu.toString();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return "";
	}

	/*
	 * public static String getOptionStructs(ResourceBundle rb,String nodeType,boolean isNodeParent, boolean copySelected,String idGroup,int mails,int wfs,
	 * PermissionUserForm permision,String showOptionPublic) { ArrayList options = getList("addDoc"); String siteType = DesigeConf.getProperty("siteType");
	 * nodeType = nodeType.trim(); StringBuffer menu = new StringBuffer(100); menu.append("<table class=\"clsTableTitle\" width=\"100%\" cellSpacing=0
	 * cellPadding=0 align=center border=0>\n"); menu.append(" <tr>\n <td>\n"); menu.append(" <table bgColor=\"#003366\" width=\"100%\" border=\"0\">\n");
	 * String locationType = DesigeConf.getProperty("locationType"); boolean isFolderOrProcess =
	 * (!nodeType.equalsIgnoreCase(siteType)&&!nodeType.equalsIgnoreCase (locationType)); byte valueTrue = Constants.permission; boolean optionsActives = false;
	 * boolean isAdmon = idGroup.equalsIgnoreCase(DesigeConf.getProperty("application.admon")); boolean isSiteType = nodeType.equalsIgnoreCase(siteType); if
	 * (!copySelected) { //S�lo se pueden Agregar Localidades, ya que se encuentran en el nodo principal if (isSiteType && (isAdmon || permision != null &&
	 * permision.getToAddFolder()==valueTrue) ) { setLink(menu,"javascript:incluir(1);",rb.getString("btn.addLocation")); optionsActives = true; } else{//El
	 * Nodo es una Localidad, una Carpeta o Un Proceso if ((permision != null && permision.getToAddFolder()==valueTrue)||isAdmon) {
	 * setLink(menu,"javascript:incluir(3);",rb.getString("btn.addFolder")); optionsActives = true; } if ((permision != null &&
	 * permision.getToAddProcess()==valueTrue)||isAdmon) { setLink(menu,"javascript:incluir(2);",rb.getString("btn.addProcess"));// Menu Agregar Proceso
	 * optionsActives = true; } } // if ((!isSiteType)&&((permision != null && permision.getToEdit()==valueTrue)||isAdmon)) { if ((permision != null &&
	 * permision.getToEdit()==valueTrue)||isAdmon) { setLink(menu,"javascript:edit();",rb.getString("btn.edit")); optionsActives = true; } if
	 * ((!isSiteType)&&(!isNodeParent) && ((permision!=null&&permision.getToDelete()==valueTrue)||isAdmon)) {
	 * setLink(menu,"javascript:youAreSure();",rb.getString("btn.delete")); optionsActives = true; } if (!nodeType.equalsIgnoreCase(locationType)) { if
	 * ((!isSiteType)&&(permision != null && permision.getToMove()==valueTrue||isAdmon)) { setLink(menu,"javascript:isCopy();",rb.getString("btn.copy"));
	 * optionsActives = true; } } if (isFolderOrProcess) { setLink(menu,"javascript:loadHistory();",rb.getString("btn.wfHistory")); optionsActives = true; }
	 * //System.out.println("options = " + options); //System.out.println("nodeType = " + nodeType); //System.out.println("permision = " + permision);
	 * 
	 * if ((!isSiteType)&&(options.contains(nodeType)&&(permision!=null&&permision .getToAddDocument()==valueTrue) ||isAdmon)) { menu.append(" <tr>\n");
	 * menu.append(" <td class=\"td_title_bwc\">\n"); menu.append(" ").append(rb.getString("doc.title")).append("\n"); menu.append(" </td>\n"); menu.append("
	 * </tr>\n"); setLink(menu,"javascript:addDocument();" ,rb.getString("btn.addDocument")); if ("true".equalsIgnoreCase(showOptionPublic)) {
	 * setLink(menu,"javascript:public();",rb.getString("btn.public")); } optionsActives = true; } } else {
	 * setLink(menu,"javascript:pasteNode();",rb.getString("btn.paste")); optionsActives = true; } if
	 * (isAdmon||(permision!=null&&permision.getToAdmon()==valueTrue)) { menu.append(" <tr>\n"); menu.append(" <td class=\"td_title_bwc\">\n"); menu.append("
	 * ").append(rb.getString("seguridad.permision")).append("\n"); menu.append(" </td>\n"); menu.append(" </tr>\n"); setLink(menu,"javascript:permission(0," +
	 * nodeType +");",rb.getString("btn.permission")); setLink(menu,"javascript:permission(1," + nodeType + ");",rb.getString("btn.permission1"));
	 * optionsActives = true; } setOptionsMsg(rb,menu,mails,wfs); optionsActives = true; menu.append(" </table>\n"); menu.append(" </tr>\n
	 * </td>\n"); menu.append("</table>\n"); if (optionsActives) { return menu.toString(); } else { return ""; } }
	 */
	/*
	 * BeanMenu options = null; Vector result = new Vector(); options = new BeanMenu(rb.getString("mail.new"),rb.getString("href.inbox"),"_self");
	 * result.add(options); options = new BeanMenu(rb.getString("mail.inbox"),rb. getString("href.messages"),"_self"); result.add(options); options = new
	 * BeanMenu (rb.getString("mail.mailsSends"),rb.getString("href.mailSents"),"_self"); result.add(options); options = new
	 * BeanMenu(rb.getString("mail.delete"),"javascript:youAreSure();","_self"); result.add(options); options = new BeanMenu(rb.getString("contacts.lista")
	 * ,rb.getString("href.contacts"),"_self"); result.add(options); return result; }
	 */
	public static String getOptionStructsII(ResourceBundle rb, String nodeType, boolean isNodeParent, boolean copySelected, String idGroup, int mails, int wfs,
			PermissionUserForm permision, String showOptionPublic, BaseStructForm forma) {
		byte limTabs = 6;
		ArrayList options = getList("addDoc");
		String siteType = DesigeConf.getProperty("siteType");
		nodeType = nodeType.trim();
		boolean isSiteType = nodeType.equalsIgnoreCase(siteType);
		boolean isAdmon = idGroup.equalsIgnoreCase(DesigeConf.getProperty("application.admon"));
		byte valueTrue = Constants.permission;
		boolean optionsActives = false;
		String locationType = DesigeConf.getProperty("locationType");
		boolean isLocation = nodeType.equalsIgnoreCase(locationType);
		boolean isFolderOrProcess = (!isSiteType && !isLocation);
		Vector optionStruct = new Vector();
		Vector optionStructII = new Vector();
		Vector optionAdmon = new Vector();
		Vector optionMsg = new Vector();
		BeanMenu option = null;
		log.debug("nodeType = " + nodeType);

		if (!copySelected) {
			if (isSiteType && (isAdmon || permision != null && permision.getToAddFolder() == valueTrue)) {
				if (forma.getToImpresion() != 1) {
					option = new BeanMenu(rb.getString("btn.addLocation"), "javascript:incluir(1);", "_self");
					optionStruct.add(option);
					optionsActives = true;
				}

			} else {// El Nodo es una Localidad, una Carpeta o Un Proceso
				if ((permision != null && permision.getToAddFolder() == valueTrue) || isAdmon) {
					if (forma.getToImpresion() != 1) {
						option = new BeanMenu(rb.getString("btn.addFolder"), "javascript:incluir(3);", "_self");
						optionStruct.add(option);
						optionsActives = true;
					}
				}
				if ((permision != null && permision.getToAddProcess() == valueTrue) || isAdmon) {
					if (forma.getToImpresion() != 1) {
						option = new BeanMenu(rb.getString("btn.addProcess"), "javascript:incluir(2);", "_self");
						optionStruct.add(option);
						optionsActives = true;
					}
				}
			}
			if ((permision != null && permision.getToEdit() == valueTrue) || isAdmon) {
				option = new BeanMenu(rb.getString("btn.edit"), "javascript:edit();", "_self");
				optionStruct.add(option);
				optionsActives = true;
			}
			// Eliminar Carpeta/Proceso
			if (isFolderOrProcess && (!isNodeParent) && ((permision != null && permision.getToDelete() == valueTrue) || isAdmon)) {
				if (forma.getToImpresion() != 1) {
					try {
						// en caso que la carpeta tenga documentos, no puede ser
						// borrada.
						if (!HandlerDocuments.isNodeParent(forma.getIdNode())) {
							option = new BeanMenu(rb.getString("btn.delete"), "javascript:youAreSure();", "_self");
							optionStruct.add(option);
							optionsActives = true;
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
			// Eliminar Localidad
			log.debug("isLocation = " + isLocation);
			if (isLocation && !isNodeParent && ((permision != null && permision.getToDelete() == valueTrue) || isAdmon)) {
				if (forma.getToImpresion() != 1) {
					try {
						// en caso que la carpeta tenga documentos, no puede ser
						// borrada.
						if (!HandlerDocuments.isNodeParent(forma.getIdNode())) {
							option = new BeanMenu(rb.getString("btn.deleteloc"), "javascript:youAreSure();", "_self");
							optionStruct.add(option);
							optionsActives = true;
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
			if (!isLocation && !isSiteType) {
				if (permision != null && permision.getToMove() == valueTrue || isAdmon) {
					if (forma.getToImpresion() != 1) {
						option = new BeanMenu(rb.getString("btn.copy"), "javascript:isCopy();", "_self");
						optionStruct.add(option);
						optionsActives = true;
					}
				}
			}
			if (isFolderOrProcess) {
				option = new BeanMenu(rb.getString("btn.wfHistory"), "javascript:loadHistory();", "_self");
				if (optionStruct.size() < limTabs) {
					optionStruct.add(option);
				} else {
					optionStructII.add(option);
				}
				optionsActives = true;
			}

			/*
			 * if (isFolderOrProcess && (options.contains(nodeType)&&(permision!= null&&permision.getToAddDocument()==valueTrue) ||isAdmon)) {
			 */
			log.debug("isSiteType = " + isSiteType);
			if(!nodeType.equals("2")) {
				if (isFolderOrProcess
						&& (isAdmon || (isFolderOrProcess && (options.contains(nodeType) && (permision != null && ("1".equalsIgnoreCase(String.valueOf(permision
								.getToAddDocument())))))))) {
					if (forma.getToImpresion() != 1) {
						option = new BeanMenu(rb.getString("btn.addDocument"), "javascript:addDocument();", "_self");
						if (optionStruct.size() < limTabs) {
							optionStruct.add(option);
						} else {
							optionStructII.add(option);
						}
						optionsActives = true;
					}
				}
			}
			/*
			 * else{ if (forma.getToImpresion()!=1){ option = new BeanMenu(rb.getString ("btn.addDocument"),"javascript:noaddDocument();","_self"); if
			 * (optionStruct.size() < limTabs) { optionStruct.add(option); } else { optionStructII.add(option); } optionsActives = true; } }
			 */
			if (isFolderOrProcess && "true".equalsIgnoreCase(showOptionPublic)) {
				if (forma.getToImpresion() != 1) {
					option = new BeanMenu(rb.getString("btn.public"), "javascript:public();", "_self");
					if (optionStruct.size() < limTabs) {
						optionStruct.add(option);
					} else {
						optionStructII.add(option);
					}
					optionsActives = true;
				}
			}
		
			if (isAdmon || (permision != null && permision.getToAdmon() == valueTrue)) {
				option = new BeanMenu(rb.getString("btn.permission"), "javascript:permission(0," + nodeType + ");", "_self");
				optionAdmon.add(option);
				option = new BeanMenu(rb.getString("btn.permission1"), "javascript:permission(1," + nodeType + ");", "_self");
				optionAdmon.add(option);
				optionsActives = true;
			}

		} else {
			if (forma.getToImpresion() != 1) {
				option = new BeanMenu(rb.getString("btn.paste"), "javascript:pasteNode();", "_self");
				optionStruct.add(option);
				optionsActives = true;
				option = new BeanMenu(rb.getString("btn.cancel"), "javascript:cancelarNodee();", "_self");
				optionStruct.add(option);
			}
		}

		String linkWfs = rb.getString("href.workFlow") + "?goTo=reload";
		option = new BeanMenu(mails + " " + rb.getString("cbs.pendingMails"), rb.getString("href.messages"), "_parent");
		optionMsg.add(option);
		option = new BeanMenu(wfs + " " + rb.getString("cbs.pendingWF"), linkWfs, "_parent");
		optionMsg.add(option);
		StringBuffer result = new StringBuffer(100);

		boolean barra = true;
		if (barra) {
			result.append(printMenuInnerToolBar(optionStruct, "", true, false, rb)).append(printMenuInnerToolBar(optionStructII, "", false, false, rb));
			result.append(printMenuInnerToolBar(optionAdmon, rb.getString("seguridad.permision"), false, false, rb));
			result.append(printMenuInnerToolBar(optionMsg, rb.getString("cbs.pendingTitle"), false, true, rb));
		} else {
			result.append(printMenuInner(optionStruct, "")).append(printMenuInner(optionStructII, ""));
			result.append(printMenuInner(optionAdmon, rb.getString("seguridad.permision")));
			result.append(printMenuInner(optionMsg, rb.getString("cbs.pendingTitle")));
		}
		return result.toString();
	}

	public static void setOptionsMsg(ResourceBundle rb, StringBuffer menu, int mails, int wfs) {
		menu.append("           <tr>\n");
		menu.append("               <td class=\"td_title_bwc\">\n");
		menu.append("                       ").append(rb.getString("cbs.pendingTitle")).append("\n");
		menu.append("               </td>\n");
		menu.append("           </tr>\n");
		String linkWfs = rb.getString("href.workFlow") + "?goTo=reload";
		setLink(menu, rb.getString("href.messages"), mails + " " + rb.getString("cbs.pendingMails"), "_parent");
		setLink(menu, linkWfs, wfs + " " + rb.getString("cbs.pendingWF"), "_parent");
	}

	private static void getIconImg(StringBuffer imgs, String nameClose, String valueSelect, boolean enabled) {
		if (nameClose.equalsIgnoreCase(valueSelect)) {
			imgs.append("<input type=\"radio\" name=\"nameIcon\" value=\"").append(nameClose).append("\" checked ");
		} else {
			imgs.append("<input type=\"radio\" name=\"nameIcon\" value=\"").append(nameClose).append("\" ");
		}
		if (!enabled) {
			imgs.append(" disabled ");
		}
		imgs.append(">\n");
		imgs.append("<img src=\"menu-images/").append(nameClose).append("\" border=\"0\" width=\"18\" height=\"18\"><br/>\n");
	}

	public static String getImgs(String valueActual) {
		StringBuffer imgs = new StringBuffer();
		String valueSelect = DesigeConf.getProperty("imgClose0");
		if (ToolsHTML.checkValue(valueActual)) {
			valueSelect = valueActual;
		}
		int numImgs = Integer.parseInt(DesigeConf.getProperty("numImgsClose"));
		for (int cont = 0; cont < numImgs; cont++) {
			String imgClose = DesigeConf.getProperty("imgClose" + cont);
			getIconImg(imgs, imgClose, valueSelect, true);
		}
		return imgs.toString();
	}

	public static String getImgs(String valueActual, String nodeType, boolean enabled) {
		StringBuffer imgs = new StringBuffer();
		String property = "numImgs" + nodeType;
		String valueSelect = DesigeConf.getProperty(property + "0");
		if (ToolsHTML.checkValue(valueActual)) {
			valueSelect = valueActual;
		}
		// System.out.println("property = " + property);
		int numImgs = Integer.parseInt(DesigeConf.getProperty(property));
		for (int cont = 0; cont < numImgs; cont++) {
			String imgClose = DesigeConf.getProperty(property + cont);
			getIconImg(imgs, imgClose, valueSelect, enabled);
		}
		return imgs.toString();
	}

	public static boolean getBooleanValue(String value, String trueValue) {
		return value.equalsIgnoreCase(trueValue);
	}

	public static byte getByteValueFromBoolean(boolean value) {
		if (value) {
			return 1; // verdadero es uno
		}
		return 0; // falso es cero
	}

	public static String getRadioButton(ResourceBundle rb, String nameProperty, String property, int value, String onClick) {
		return getRadioButton(rb, nameProperty, property, value, onClick, 1);
	}

	public static String getRadioButton(ResourceBundle rb, String nameProperty, String property, int value, String onClick, int salto) {
		StringBuffer items = new StringBuffer();
		// int valueSelect = value;
		int numItems = Integer.parseInt(rb.getString(nameProperty));
		String inicio = "<input type=\"radio\" name=\"";
		if (ToolsHTML.checkValue(onClick)) {
			inicio = "<input type=\"radio\" onClick=\"" + onClick + "\" name=\"";
		}
		for (int cont = 0; cont < numItems; cont++) {
			String name = rb.getString(nameProperty + cont);
			items.append(inicio).append(property).append("\" value=\"").append(cont).append("\" ");
			if (cont == value) {
				items.append(" checked ");
			}
			items.append(">").append(name).append(salto == 1 ? "<br/>\n" : "&nbsp;");
		}
		return items.toString();
	}

	public static String getRadioButton(ResourceBundle rb, String nameProperty, String property, int value, String onClick, boolean onlyRead) {
		StringBuffer items = new StringBuffer();
		int valueSelect = value;
		int numItems = Integer.parseInt(rb.getString(nameProperty));
		String inicio = "<input type=\"radio\" name=\"";
		if (ToolsHTML.checkValue(onClick)) {
			inicio = "<input type=\"radio\" onClick=\"" + onClick + "\" name=\"";
		}
		for (int cont = 0; cont < numItems; cont++) {
			String name = rb.getString(nameProperty + cont);
			items.append(inicio).append(property).append("\" value=\"").append(cont).append("\" ");
			if (cont == valueSelect && !onlyRead) {
				items.append(" checked ");
			}
			if (cont == valueSelect && onlyRead) {
				items.append(" checked  disabled ");
			} else {
				if (onlyRead) {
					items.append(" disabled ");
				}
			}

			items.append(">").append(name).append("<br/>\n");
		}
		return items.toString();
	}

	public static String getRadioButton(String property, int value, int valortrue, String onClick) {
		StringBuffer items = new StringBuffer();
		String inicio = "<input type=\"radio\" name=\"";
		if (ToolsHTML.checkValue(onClick)) {
			inicio = "<input type=\"radio\" onClick=\"" + onClick + "\" name=\"";
		}
		items.append(inicio).append(property).append("\" value=\"").append(valortrue).append("\" ");
		if (valortrue == value) {
			items.append(" checked ");
		}
		items.append(">");
		return items.toString();
	}

	// <%=ToolsHTML.getSelectList(rb,"param.monthExpireDoc","monthsExpireDocs",data.getMonthsExpireDocs())%>
	public static String getSelectList(ResourceBundle rb, String nameProperty, String property, String selected) {
		StringBuffer select = new StringBuffer("<select name=\"").append(property).append("\">");
		if (!checkValue(selected)) {
			selected = "";
		}
		log.debug("selected = " + selected);
		int numItems = Integer.parseInt(rb.getString(nameProperty));
		for (int row = 0; row < numItems; row++) {
			String value = rb.getString(nameProperty + row);
			select.append("<option value=\"").append(value).append("\"");
			if (selected.equalsIgnoreCase(value)) {
				select.append(" selected");
			}
			select.append(">").append(value).append("</option>");
		}
		select.append("</select>");
		return select.toString();
	}

	// <%=ToolsHTML.getSelectList(rb,"param.vigItems","param.vigItemsValue","unitTimeExpireDrafts",data.getUnitTimeExpireDrafts())%>
	public static String getSelectList(ResourceBundle rb, String nameProperty, String nameValues, String property, String selected) {
		StringBuffer select = new StringBuffer("<select name=\"").append(property).append("\">");
		if (!checkValue(selected)) {
			selected = "";
		}
		log.debug("selected = " + selected);
		int numItems = Integer.parseInt(rb.getString(nameProperty));
		for (int row = 0; row < numItems; row++) {
			String desc = rb.getString(nameProperty + row);
			String value = rb.getString(nameValues + row);

			select.append("<option value=\"").append(value).append("\"");
			if (selected.equalsIgnoreCase(value)) {
				select.append(" selected");
			}
			select.append(">").append(desc).append("</option>");
		}
		select.append("</select>");
		return select.toString();
	}

	private static String getMensaje(String key, HttpServletRequest request) {
		String result = (String) request.getAttribute(key);
		if (result == null) {
			result = (String) request.getSession().getAttribute(key);
			if (result != null) {
				request.getSession().removeAttribute(key);
				return result;
			}
		} else {
			return result;
		}
		return null;
	}

	/**
	 * Este m�todo permite crear la informaci�n a ser colocada en la p�gina
	 * cuando la misma es cargada
	 * 
	 * @param request
	 * @param rb
	 * @return
	 */
	public static String getAlertInfo(HttpServletRequest request, ResourceBundle rb) {
		String info = getMensaje("info", request);
		StringBuffer sb = new StringBuffer();
		if (info != null) {
			sb.append("alert('");
			sb.append(info);
			sb.append("');");
		}
		return sb.toString();
	}

	public static String getMensajePages(HttpServletRequest request, ResourceBundle rb) {
		StringBuffer onLoad = new StringBuffer("");
		String ok = (String) request.getSession().getAttribute("usuario");
		String info = getMensaje("info", request);// (String)request.getAttribute("info");
		if (info == null) {
			info = getMensaje("error", request);
			// info = (String)request.getSession().getAttribute("info");
			// if (info!=null) {
			// request.getSession().removeAttribute("info");
			// }
		} else {
			String info2 = getMensaje("error", request);
		}
		if (ToolsHTML.checkValue(ok)) {
			onLoad.append(" onLoad=\"self.status='").append(rb.getString("pagin.welcome")).append(" ");
			onLoad.append(ok).append("'");
		} else {
			return null;
		}
		if (ToolsHTML.checkValue(info)) {
			onLoad.append(";alert('").append(info.replaceAll("\"", "\\\"")).append("')");
		}
		if (onLoad.length() > 0) {
			onLoad.append("\"");
		}
		return onLoad.toString();
	}

	public static String getMensajePages(HttpServletRequest request, ResourceBundle rb, String nameFunction) {
		StringBuffer onLoad = new StringBuffer("");
		String ok = (String) request.getSession().getAttribute("usuario");
		String info = (String) request.getAttribute("info");
		if (info == null) {
			info = (String) request.getSession().getAttribute("info");
			if (info != null) {
				request.getSession().removeAttribute("info");
			}
		}
		if (ToolsHTML.checkValue(ok)) {
			onLoad.append(" onLoad=\"self.status='").append(rb.getString("pagin.welcome")).append(" ");
			onLoad.append(ok).append("'");
		} else {
			return null;
		}
		if (ToolsHTML.checkValue(info)) {
			onLoad.append(";alert('").append(info).append("')");
		}

		// Manejo del Error
		info = (String) request.getAttribute("error");
		if (info == null) {
			info = (String) request.getSession().getAttribute("error");
			if (info != null) {
				request.getSession().removeAttribute("error");
			}
		}

		if (ToolsHTML.checkValue(info)) {
			onLoad.append(";alert('").append(info).append("')");
		}

		if (!isEmptyOrNull(nameFunction)) {
			onLoad.append(";").append(nameFunction);
		}
		if (onLoad.length() > 0) {
			onLoad.append("\"");
		}
		return onLoad.toString();
	}

	/*
	 * <%=ToolsHTML.showCheckBox("typeI", "updateCheck(this.form.typeI,this.form.type)",data.getType(),"0")%> <input name=typeI type=checkbox value=7
	 */
	public static String showCheckBox(String nameField, String onChange, String value, String valueTrue, boolean habilitar) {
		StringBuffer check = new StringBuffer("<input name=\"").append(nameField).append("\" type=\"checkbox\" value=\"");
		check.append(value).append("\"");
		if (checkValue(onChange)) {
			check.append(" onClick=\"").append(onChange).append("\" ");
		}
		check.append(" disabled=\"").append(habilitar).append("\"").append(" ");
		check.append(value.equalsIgnoreCase(valueTrue) ? "checked>" : ">");
		return check.toString();
	}

	public static String showCheckBox(String nameField, String onChange, String value, String valueTrue) {
		value = value != null ? value : "";
		StringBuffer check = new StringBuffer("<input name=\"").append(nameField).append("\" type=\"checkbox\" value=\"");
		check.append(value).append("\"");
		if (checkValue(onChange)) {
			check.append(" onClick=\"").append(onChange).append("\" ");
		}
		check.append(value.equalsIgnoreCase(valueTrue) ? "checked>" : ">");
		return check.toString();
	}

	// SIMON 09 DE JUNIO 2005 INICIO
	public static String showCheckBoxTodos(String nameField, String onChange, String value, String valueTrue) {
		StringBuffer check = new StringBuffer("<input name=\"").append(nameField).append("\" type=\"checkbox\" value=\"");
		check.append(value).append("\"");
		if (checkValue(onChange)) {
			check.append(" onClick=\"").append(onChange).append("\" ");
		}
		check.append(value.equalsIgnoreCase(valueTrue) ? "checked>" : ">");
		return check.toString();
	}

	// SIMON 9 DE JUNIO 2005 FIN

	public static String showCheckBox(String nameField, String onChange, int value, int valueTrue, boolean readOnly) {
		StringBuffer check = new StringBuffer("<input name=\"").append(nameField).append("\" type=\"checkbox\" value=\"");
		check.append(value).append("\"");
		if (checkValue(onChange)) {
			check.append(" onClick=\"").append(onChange).append("\" ");
		}
		check.append(readOnly ? " disabled " : "");
		check.append(value == valueTrue ? "checked>" : ">");
		return check.toString();
	}

	public static boolean isEmptyOrNull(String valor) {
		return (valor == null || valor.trim().length() == 0 || valor.trim().equalsIgnoreCase("null"));
	}

	public static String isEmptyOrNull(String valor, String nuevoValor) {
		return (valor == null || valor.trim().length() == 0 || valor.trim().equalsIgnoreCase("") || valor.trim().equalsIgnoreCase("null") ? nuevoValor : valor);
	}

	public static String isEmptyOrNull(Object valor, String nuevoValor) {
		return (valor == null || String.valueOf(valor).trim().length() == 0 || String.valueOf(valor).trim().equalsIgnoreCase("")
				|| String.valueOf(valor).trim().equalsIgnoreCase("null") ? nuevoValor : String.valueOf(valor));
	}

	public static String dibujarCheck(String classTxt, String texto, String classInput, String valor, int valorTrue, String adicional) {
		if (!isEmptyOrNull(valor)) {
			int porcia = 0;
			try {
				porcia = Integer.parseInt(valor);
			} catch (Exception ex) {
				ex.printStackTrace();
				porcia = -1;
			}
			if (porcia == valorTrue) {
				StringBuffer check = new StringBuffer("<tr>");
				check.append("  <td class=\"").append(classTxt)
						.append("\" height=\"26\" style=\"background: url(img/btn160.png); background-repeat: no-repeat\" valign=\"middle\">");
				check.append("      ").append(texto);
				check.append("  </td>");
				check.append("  <td class=\"").append(classInput).append("\">");
				check.append("      ").append("<input type='checkbox' name='caja' checked disabled>");
				if (!isEmptyOrNull(adicional)) {
					check.append(adicional);
				}
				check.append("  </td>");
				check.append("</tr>");
				return check.toString();
			}
		}
		return "";
	}

	public static String getDataColumn(String classTxt, String texto, String classInput, String valor, int valorTrue, String adicional) {
		if (!isEmptyOrNull(valor)) {
			int porcia = 0;
			try {
				porcia = Integer.parseInt(valor);
			} catch (Exception ex) {
				ex.printStackTrace();
				porcia = -1;
			}
			if (porcia == valorTrue) {
				StringBuffer check = new StringBuffer(100);
				check.append("  <td class=\"").append(classTxt)
						.append("\" height=\"26\" style=\"background: url(img/btn160.gif); background-repeat: no-repeat\" valign=\"middle\">");
				check.append("      ").append(texto);
				check.append("  </td>");
				check.append("  <td class=\"").append(classInput).append("\">");
				check.append("      ").append("<input type='checkbox' name='caja' checked disabled>");
				if (!isEmptyOrNull(adicional)) {
					check.append(adicional);
				}
				check.append("  </td>");
				return check.toString();
			}
		}
		return "";
	}

	/**
	 * Este m�todo permite obtener el Atributo indicado por el par�metro att en la sesi�n del usurio y retorna el valor del mismo como un Objeto
	 * 
	 * @param session
	 *            : Sesi�n actual del usuario
	 * @param att
	 *            : Nombre del Atributo cuyo valor ser� buscado en la sesi�n
	 * @param remove
	 *            : Par�metro booleano que indica si el atributo ser� removido de la sesi�n
	 * @return
	 */
	public static Object getAttributeSession(HttpSession session, String att, boolean remove) {
		Object obj = session.getAttribute(att);
		if (obj != null) {
			if (remove) {
				session.removeAttribute(att);
			}
			return obj;
		}
		return "";
	}

	/* Obtener de la fecha de la base de datos por la forma dia/mm/yyyy */
	// public static String fechaddmmyyyy(String dateSystem) {
	// String ddmmyyyy
	// =dateSystem.substring(8,10)+"/"+dateSystem.substring(5,7)+"/"+dateSystem.substring(0,4);
	// return ddmmyyyy;
	// }
	/**/
	public static boolean isDateValid(java.util.Date date, boolean futura) {
		String dateStr = sdf.format(date);
		String dateSytem = sdf.format(new Date());
		if (futura) {
			return (dateStr.compareTo(dateSytem) > 0);
		}
		return (dateStr.compareTo(dateSytem) <= 0);
	}

	public static int compareDates(java.util.Date date1, java.util.Date date2) {
		String sDate1 = sdf.format(date1);
		String sDate2 = sdf.format(date2);
		// System.out.println("compareDates");
		// System.out.println(sDate1);
		// System.out.println(sDate2);

		int result = 0;
		if (sDate1.compareTo(sDate2) > 0)
			result = 1;
		if (sDate1.compareTo(sDate2) == 0)
			result = 0;
		if (sDate1.compareTo(sDate2) < 0)
			result = -1;
		return result;
	}

	/*
	 * Compara fechas en formato corto dd/MM/yyyy
	 */
	public static int compareDates(String date1, String date2) {
		int result = 0;
		try {
			Date dDate1;
			dDate1 = sdfShowWithoutHour.parse(date1);
			Date dDate2 = sdfShowWithoutHour.parse(date2);
			if (dDate1.getTime() > dDate2.getTime())
				result = 1;
			else if (dDate1.getTime() < dDate2.getTime())
				result = -1;
		} catch (ParseException e) {
			// e.printStackTrace();
		}
		return result;
	}
	
	public static String getSdfShowWithoutHour(java.util.Date fecha) {
		if(fecha!=null) {
			return ToolsHTML.sdfShowWithoutHour.format(fecha);
		}
		return "";
	}

	/*
	 * Compara fechas en formato corto dd/MM/yyyy
	 */
	public static int compareDatesWithNow(String date2) {
		int result = 0;
		try {
			Date dDate1 = new Date(); // la fecha de hoy
			String date1 = sdfShowWithoutHour.format(dDate1);
			dDate1 = sdfShowWithoutHour.parse(date1);
			Date dDate2 = sdfShowWithoutHour.parse(date2);
			if (dDate1.getTime() > dDate2.getTime())
				result = 1;
			else if (dDate1.getTime() < dDate2.getTime())
				result = -1;
		} catch (ParseException e) {
			// e.printStackTrace();
		}
		return result;
	}

	public static java.util.Date getDateCreationMovements(String valor) throws ApplicationExceptionChecked {
		java.util.Date date = null;
		if (!isEmptyOrNull(valor)) {
			if (valor.length() == 10) {
				String systemTime = sdfTime.format(new Date());
				valor = valor + " " + systemTime;
			}
			try {
				date = sdf.parse(valor);
			} catch (Exception ex) {
				ex.printStackTrace();
				throw new ApplicationExceptionChecked("E0010");
			}

		} else {
			date = new Date();
		}
		if (isDateValid(date, false)) {
			log.debug("date = " + date);
			return date;
		} else {
			new Exception().printStackTrace();
			throw new ApplicationExceptionChecked("E0010");
		}
	}

	public static java.util.Date sumUnitsToDate(int unit, java.util.Date date, int cant) {
		Calendar cal = GregorianCalendar.getInstance();
		cal.setTime(date);
		cal.add(unit, cant);
		return cal.getTime();
	}

	public static String getDateLargeSql() {
		return sdfShowConvert1.format(new java.util.Date());
	}
	public static String getDateShortSql() {
		return date.format(new java.util.Date());
	}
	
	// ToolsHTML.getDateExpireDocument(forma.getDateApproved(),forma.getDateExpires(),value,unit);
	public static java.util.Date getDateExpireDocument(String dateAproved, String dateExpire, int cant, String unit) throws Exception {
		java.util.Date date = null;
		if (!isEmptyOrNull(dateExpire)) {
			if (dateExpire.length() == 10) {
				String systemTime = sdfTime.format(new Date());
				dateExpire = dateExpire + " " + systemTime;
			}
			try {
				date = sdf.parse(dateExpire);
			} catch (Exception ex) {
				date = sdf1.parse(dateExpire);
			}
		} else {
			if (!isEmptyOrNull(dateAproved)) {
				if (dateAproved.length() == 10) {
					String systemTime = sdfTime.format(new Date());
					dateAproved = dateAproved + " " + systemTime;
				}
				try {
					date = sdf.parse(dateAproved);
				} catch (Exception ex) {
					date = sdfShowConvert1.parse(dateAproved);
				}
				// date = sdf.parse(dateAproved);
				if (unit != null) {
					if (Constants.months.equalsIgnoreCase(unit)) {
						date = sumUnitsToDate(Calendar.MONTH, date, cant);
					} else {
						date = sumUnitsToDate(Calendar.YEAR, date, cant);
					}
				} else {
					date = sumUnitsToDate(Calendar.MONTH, date, cant);
				}
			} else {
				return null;
			}
		}
		// if (isDateValid(date,true)) {
		return date;
		// } else {
		// throw new Exception("Fecha Inv�lida");
		// }
	}

	public static java.util.Date getDateExpireDocument(String dateExpire) throws Exception {
		java.util.Date date = null;
		if (!isEmptyOrNull(dateExpire)) {
			if (dateExpire.trim().length() == 10) {
				String systemTime = sdfTime.format(new Date());
				dateExpire = dateExpire + " " + systemTime;
			}
			try {
				date = sdf.parse(dateExpire);
			} catch (Exception ex) {
				date = sdf1.parse(dateExpire);
			}
		} else {
			return null;
		}
		if (isDateValid(date, true)) {// La Fecha es Invalida
			return date;
		} else {
			throw new ApplicationExceptionChecked("E0002");
		}
	}

	public static int setValueInQuery(PreparedStatement st, String value, int dataType, int pos) throws Exception {
		if (st != null) {
			if (!isEmptyOrNull(value)) {
				switch (dataType) {
				case 1: {
					st.setString(pos, value);
					pos++;
					break;
				}
				case 2: {
					st.setInt(pos, Integer.parseInt(value));
					pos++;
					break;
				}
				case 3: {
					st.setBoolean(pos, (new Boolean(value)).booleanValue());
					pos++;
					break;
				}
				case 4: {
					if (value.length() == 10) {
						String systemTime = sdfTime.format(new Date());
						value = value + " " + systemTime;
					}
					java.util.Date date = sdf.parse(value);
					Timestamp time = new Timestamp(date.getTime());
					st.setTimestamp(pos, time);
					pos++;
					break;
				}
				}
			}
		}
		return pos;
	}

	// pos = ToolsHTML.setValueInQuery(st,forma.getMayorVer(),pos);
	public static int setValueInQuery(PreparedStatement st, Object value, int pos) throws SQLException {
		if (st != null) {
			if (value != null) {
				if (value instanceof String) {
					String valor = (String) value;
					if (valor.length() > 0) {
						st.setString(pos, valor);
						pos++;
					}
				} else if (value instanceof Byte) {
					log.debug("value = " + value);
					int valor = ((Byte) value).intValue();
					st.setInt(pos, valor);
					pos++;
				} else if (value instanceof Number) {
					int valor = ((Integer) value).intValue();
					st.setInt(pos, valor);
					pos++;
				} else {
					st.setObject(pos, value);
					pos++;
				}
			}
		}
		return pos;
	}

	public static int setValueInQueryAcceptNull(PreparedStatement st, Object value, int pos) throws SQLException {
		if (st != null) {
			if (value != null) {
				if (value instanceof String) {
					String valor = (String) value;
					st.setString(pos, valor);
					pos++;
				} else if (value instanceof Byte) {
					log.debug("value = " + value);
					int valor = ((Byte) value).intValue();
					st.setInt(pos, valor);
					pos++;
				} else if (value instanceof Number) {
					int valor = ((Integer) value).intValue();
					st.setInt(pos, valor);
					pos++;
				}
			} else {
				String valor = "";
				st.setString(pos, valor);
				pos++;
			}
		}
		return pos;
	}

	private static void setField(StringBuffer query, StringBuffer datos, String field) {
		setField(query, datos, field, false);
	}

	private static void setField(StringBuffer query, StringBuffer datos, String field, boolean castingBit) {
		String ultimo = query.substring(query.length() - 1);
		if (!ultimo.equalsIgnoreCase(",")) {
			query.append(",");
		}
		if (castingBit) {
			datos.append(",CAST(? AS bit)");
		} else {
			datos.append(",?");
		}
		query.append(field);
	}

	// ToolsHTML.setFieldInQuery(update,fields,forma.getMayorVer(),"MayorVer");
	public static void setFieldInQuery(StringBuffer query, StringBuffer datos, Object value, String field) {
		if (query != null) {
			if (value != null) {
				if (value instanceof String) {
					if (((String) value).length() > 0) {
						setField(query, datos, field);
						return;
					}
				} else if (value instanceof Integer) {
					setField(query, datos, field);
					return;
				} else if (value instanceof Byte) {
					setField(query, datos, field, true);
					return;
				} else if (value instanceof Number) {
					setField(query, datos, field);
					return;
				} else {
					setField(query, datos, field);
					return;
				}
			}
		}
	}

	public static void setFieldInQueryEdit(StringBuffer query, Object value, String field) {
		if (query != null) {
			if (value != null) {
				if (value instanceof String) {
					if (((String) value).length() > 0) {
						query.append(",").append(field).append(" = ? ");
					}
				} else if (value instanceof Byte) {
					query.append(",").append(field).append(" = CAST(? as bit) ");
				} else if (value instanceof Number) {
					query.append(",").append(field).append(" = ? ");
				}
			}
		}
	}

	public static void setFieldInQueryEditAcceptNull(StringBuffer query, Object value, String field) {
		if (query != null) {
			if (value != null) {
				if (value instanceof String) {
					query.append(",").append(field).append(" = ? ");
				} else if (value instanceof Byte) {
					query.append(",").append(field).append(" = CAST(? as bit) ");
				} else if (value instanceof Number) {
					query.append(",").append(field).append(" = ? ");
				}
			} else {
				query.append(",").append(field).append(" = ? ");
			}
		}
	}

	public static char movementChar(char car) {
		int asciiValue = car;
		asciiValue++;
		return (char) asciiValue;
	}

	/**
	 * M�todo que permite incrementar la versi�n del documento seg�n sea el valor del mismo
	 * 
	 * @param version
	 */
	public static String incVersion(String version) {
		char caracter;
		StringBuffer result = new StringBuffer(version);
		// Si est� vac�o o es Nulo => la Versi�n es Alfabetica y arranco en ""
		if (!isEmptyOrNull(version)) {
			if (version.length() > 1) {
				caracter = version.charAt(version.length() - 1);
				if (caracter == 'Z') {
					String subCad = version.substring(0, version.length() - 1);
					subCad = incVersion(subCad);
					result = new StringBuffer(subCad);
					result.append('A');
				} else {
					caracter = movementChar(caracter);
					result.setCharAt(result.length() - 1, caracter);
				}
			} else {
				caracter = version.charAt(0);
				caracter = movementChar(caracter);
				result = new StringBuffer("").append(caracter);
			}
		} else {
			return "A";
		}
		return result.toString();
	}

	/**
	 * Este m�todo se utiliza para verificar si el valor dado es num�rico
	 * 
	 * @param valor
	 * @return
	 */
	public static boolean isNumeric(String valor) {
		if (!isEmptyOrNull(valor)) {
			try {
				Integer.parseInt(valor.trim());
				return true;
			} catch (Exception ex) {
				log.debug("The value indicated not is Numeric: " + valor);
				// ex.printStackTrace();
			}
		}
		return false;
	}

	/**
	 * Este m�todo se devolver un cero si el valor es null
	 * 
	 * @param valor
	 * @return
	 */
	public static int parseInt(String valor) {
		if (!isEmptyOrNull(valor)) {
			try {
				return Integer.parseInt(valor.trim());
			} catch (Exception ex) {
				// no hacemos nada
			}
		}
		return 0;
	}

	/**
	 * Este m�todo se devolver un cero si el valor es null
	 * 
	 * @param valor
	 * @return
	 */
	public static Timestamp parseTimestamp(String valor, SimpleDateFormat simpleDateFormat) {
		if (!isEmptyOrNull(valor)) {
			try {
				return new Timestamp(simpleDateFormat.parse(valor).getTime());
			} catch (Exception ex) {
				// no hacemos nada
			}
		}
		return null;
	}
	
	public static void main(String[] args) {
		String cad = "01/02/2017 12:59:59 AM";
	    		
		System.out.println(parseTimestamp(cad));
	}

	public static Timestamp parseTimestamp(String valor) {
		if (!isEmptyOrNull(valor)) {
			Date fecha = null;
			try {
				if(valor.length()>10) {
					if(valor.indexOf("-")!=-1) {
						try {
							fecha = ToolsHTML.sdfShowConvert.parse(valor);
						} catch (Exception ex) {
							try {
								fecha = ToolsHTML.sdfShowConvert1.parse(valor);
							} catch (Exception e) {
								fecha = null;
							}
						}
					} else if(valor.indexOf("/")!=-1) {
						fecha = ToolsHTML.sdfShow.parse(valor);
					}
				} else {
					if(valor.indexOf("-")!=-1) {
						fecha = date.parse(valor);
					} else if(valor.indexOf("/")!=-1) {
						fecha = sdfShowWithoutHour.parse(valor);
					}
				}
				
				return new Timestamp(fecha.getTime());
			} catch (Exception ex) {
				// no hacemos nada
			}
		}
		return null;
	}
	
	
	

	/**
	 * Este m�todo se devolver un false si el valor es null
	 * 
	 * @param valor
	 * @return
	 */
	public static boolean parseBoolean(String valor) {
		if (!isEmptyOrNull(valor)) {
			try {
				return Boolean.parseBoolean(valor.trim());
			} catch (Exception ex) {
				// no hacemos nada
			}
		}
		return false;
	}

	public static ArrayList<String> getProperties(String nameProperties) {
		ArrayList resp = new ArrayList();
		StringTokenizer st = new StringTokenizer(DesigeConf.getProperty(nameProperties), ",");
		while (st.hasMoreTokens()) {
			String token = st.nextToken();
			resp.add(token);
		}
		return resp;
	}

	public static String parseParametersWonderWare(HttpServletRequest request) {
		log.debug("ToolsHTML.parseParameters");
		StringBuffer result = new StringBuffer(100);
		Enumeration parameters = request.getParameterNames();
		while (parameters.hasMoreElements()) {
			String name = (String) parameters.nextElement();
			String value = request.getParameter(name);
			if (!isEmptyOrNull(value)) {
				if (ToolsHTML.isNumeric(value)) {
					result.append(value);
					return result.toString();
				}

			}
		}
		return result.toString();
	}

	/**
	 * 
	 * @param request
	 * @return
	 */
	public static String parseParameters(HttpServletRequest request) {
		log.debug("ToolsHTML.parseParameters");
		StringBuffer result = new StringBuffer(100);
		boolean first = true;

		// Enumeration parameters = request.getParameterNames();
		// while (parameters.hasMoreElements()) {
		for (Enumeration parameters = request.getParameterNames(); parameters.hasMoreElements();) {
			String name = (String) parameters.nextElement();
			String value = request.getParameter(name);
			if (!isEmptyOrNull(value)) {
				if (first) {
					result.append("?").append(name).append("=").append(value);
					first = false;
				} else {
					result.append("&").append(name).append("=").append(value);
				}
			}
		}
		return result.toString();
	}

	/**
	 * 
	 * @param serverName
	 * @param serverPort
	 * @param contextPath
	 * @return
	 */
	public static String getServerPath(String serverName, int serverPort, String contextPath) {
		StringBuilder action = new StringBuilder(1024).append("http://").append(serverName).append(":").append(serverPort).append(contextPath);

		String result = action.toString();
		log.debug(result);
		return result;
	}

	/**
	 * 
	 * @param request
	 * @return
	 */
	public static String getServerName(HttpServletRequest request) {
		return request.getServerName();
	}

	/**
	 * 
	 * @param pass
	 * @return
	 */
	public static String encripterPass(String pass) {
		Encryptor enc = new Encryptor();
		String result = enc.getEncryptedPassword(pass);
		return result;
	}

	public static int parseId(String id) {
		return Integer.parseInt(id);
	}

	/**
	 * 
	 * @param ids
	 * @return
	 */
	public static String getIds(String[] ids) {
		if (ids != null && ids.length > 0) {
			StringBuilder result = new StringBuilder(1024);
			boolean first = true;
			for (int row = 0; row < ids.length; row++) {
				if (first) {
					result.append("(");
					first = false;
				} else {
					result.append(",");
				}
				result.append(ids[row]);
			}
			result.append(")");
			return result.toString();
		} else {
			return null;
		}
	}

	/**
	 * 
	 * @param ids
	 * @param isFlexFlow
	 * @return
	 */
	public static String getIds(String[] ids, String isFlexFlow) {
		if (ids != null && ids.length > 0) {
			StringBuffer result = new StringBuffer();
			boolean first = true;
			String[] info;
			for (int row = 0; row < ids.length; row++) {
				info = ids[row].split(",");
				log.debug("[info] " + Arrays.toString(info));

				// Luis Cisneros
				// 18/04/07
				// Hay que preguntar si esta bien armada la clave.
				// if (info!=null && isFlexFlow.compareTo(info[1])==0) {
				if (info != null && info.length > 1 && isFlexFlow.compareTo(info[1]) == 0) {
					if (first) {
						result.append("(");
						first = false;
					} else {
						result.append(",");
					}
					// result.append(ids[row]);
					result.append(info[0]);
				}
			}
			if (result.length() > 0) {
				result.append(")");

				log.debug("getIds" + result.toString());

				return result.toString();
			} else {
				return null;
			}
		} else {
			return null;
		}
	}

	/**
	 * 
	 * @param serverName
	 * @return
	 */
	public static String getPathFile(String serverName) {
		StringBuffer data = new StringBuffer(50);
		data.append("\\\\\\\\").append(serverName).append("\\\\" + ToolsHTML.getFolderCmp() + "\\\\");
		return data.toString();
	}

	/**
	 * 
	 * @param tree
	 * @param usuario
	 * @param idDoc
	 * @return
	 * @throws ApplicationExceptionChecked
	 */
	public static Hashtable checkDocsSecurity(Hashtable tree, Users usuario, String idDoc) throws ApplicationExceptionChecked {
		// :SEGURIDAD: Es necesario que pase por las cuatro opciones, por eso no
		// esta condicionado con if...
		if (tree == null) {
			try {
				Hashtable security = new Hashtable();

				// obtenemos la seguridad del documento por grupo en la
				// estructura
				HandlerGrupo.getAllSecurityForStructGroupDocs(usuario.getIdGroup(), security, idDoc);

				// obtenemos la seguridad del documento por usuario en la
				// estructura
				HandlerDBUser.getAllSecurityForStructUserDocs(usuario.getIdPerson(), security, idDoc);

				// obtenemos la seguridad del documento por grupo
				HandlerGrupo.getAllSecurityForGroupDocs(usuario.getIdGroup(), security, idDoc);

				// obtenemos la seguridad del documento por usuario
				HandlerDBUser.getAllSecurityForUserDocs(usuario.getIdPerson(), security, idDoc);

				return security;
			} catch (Exception ex) {
				ex.printStackTrace();
				throw new ApplicationExceptionChecked("E0054");
			}
		} else {
			return tree;
		}
	}

	// BeanVersionForms

	public static void checkDocsSecurityLoad(PermissionUserForm perm, PermissionUserForm forma) throws ApplicationExceptionChecked {
		try {
			forma.setToRead(perm.getToRead());
			forma.setToEdit(perm.getToEdit());
			forma.setToDelete(perm.getToDelete());
			forma.setToViewDocs(perm.getToViewDocs());
			forma.setToMove(perm.getToMove());
			forma.setToAdmon(perm.getToAdmon());
			forma.setToViewDocs(perm.getToViewDocs());
			forma.setToReview(perm.getToReview());
			forma.setToAprove(perm.getToAprove());
			forma.setToMoveDocs(perm.getToMoveDocs());
			forma.setCheckOut(perm.getCheckOut());
			forma.setToEditRegister(perm.getToEditRegister());
			forma.setToImpresion(perm.getToImpresion());
			forma.setToCheckTodos(perm.getToCheckTodos());
			forma.setToFlexFlow(perm.getToFlexFlow());
			forma.setToChangeUsr(perm.getToChangeUsr());
			forma.setToDownload(perm.getToDownload());
			log.debug("[checkDocsSecurityLoad] perm.getToChangeUsr() " + perm.getToChangeUsr());
		} catch (Exception ex) {
			ex.printStackTrace();
			throw new ApplicationExceptionChecked("E0054");
		}
	}

	public static void checkDocsSecurityLoad(PermissionUserForm perm, BeanVersionForms forma) throws ApplicationExceptionChecked {
		byte initValue = Constants.notPermission;
		try {
			forma.setToRead(perm.getToRead());
			forma.setToEdit(perm.getToEdit());
			forma.setToDelete(perm.getToDelete());
			forma.setToViewDocs(perm.getToViewDocs());
			forma.setToMove(perm.getToMove());
			forma.setToAdmon(perm.getToAdmon());
			forma.setToViewDocs(perm.getToViewDocs());
			forma.setToReview(perm.getToReview());
			forma.setToApproved(perm.getToAprove());
			forma.setToMoveDocs(perm.getToMoveDocs());
			forma.setToCheckOut(perm.getCheckOut());
			forma.setToEditRegister(perm.getToEditRegister());
			forma.setToImpresion(perm.getToImpresion());
			forma.setToCheckTodos(perm.getToCheckTodos());
			forma.setToDownload(perm.getToDownload());
		} catch (Exception ex) {
			ex.printStackTrace();
			throw new ApplicationExceptionChecked("E0054");
		}
	}

	public static void checkDocsSecurityLoad(PermissionUserForm perm, BaseDocumentForm forma) throws ApplicationExceptionChecked {
		byte initValue = Constants.notPermission;
		try {
			forma.setToRead(perm.getToRead());
			forma.setToEdit(perm.getToEdit());
			forma.setToDelete(perm.getToDelete());
			forma.setToViewDocs(perm.getToViewDocs());
			forma.setToMove(perm.getToMove());
			forma.setToAdmon(perm.getToAdmon());
			forma.setToViewDocs(perm.getToViewDocs());
			forma.setToReview(perm.getToReview());
			forma.setToApproved(perm.getToAprove());
			forma.setToMoveDocs(perm.getToMoveDocs());
			forma.setToCheckOut(perm.getCheckOut());
			forma.setToEditRegister(perm.getToEditRegister());
			forma.setToImpresion(perm.getToImpresion());
			forma.setToCheckTodos(perm.getToCheckTodos());
			forma.setToDownload(perm.getToDownload());
		} catch (Exception ex) {
			ex.printStackTrace();
			throw new ApplicationExceptionChecked("E0054");
		}
	}

	public static Hashtable checkTree(Hashtable tree, Users usuario) throws ApplicationExceptionChecked {
		if (log == null) {
			try {
				log = LoggerFactory.getLogger("[V4.0 FTP] " + ToolsHTML.class.getName());
			} catch (Exception e) {
			}
		}
		if (tree == null) {
			Hashtable security = null;
			Hashtable subNodos = new Hashtable();
			try {
				try {
					log.debug("Load Tree ...");
				} catch (Exception e) {
				}
				boolean isAdmon = usuario.getIdGroup().equalsIgnoreCase(DesigeConf.getProperty("application.admon"));
				if (!isAdmon) {
					StringBuffer idStructs = new StringBuffer(50);
					idStructs.append("1");
					// obtenemos la seguridad del grupo
					security = HandlerGrupo.getAllSecurityForGroup(usuario.getIdGroup(), idStructs);
					// obtenemos la seguridad del usuario
					HandlerDBUser.getAllSecurityForUser(usuario.getIdPerson(), security, idStructs);
					// obtenemos todos los nodos dependiendo de la permisologia
					// del grupo y del usuario al que pertenece dicho usuario
					return HandlerStruct.loadAllNodes(security, usuario.getUser(), usuario.getIdGroup(), subNodos, idStructs.toString());
				} else {
					return HandlerStruct.loadAllNodes(security, usuario.getUser(), usuario.getIdGroup(), subNodos, null);
				}
			} catch (Exception ex) {
				ex.printStackTrace();
				throw new ApplicationExceptionChecked("E0054");
			}
		} else {
			return tree;
		}
	}

	public static Hashtable checkTree(Hashtable tree, Users usuario, Hashtable subNodos) throws ApplicationExceptionChecked {
		if (tree == null) {
			try {
				Hashtable security = null;
				boolean isAdmon = usuario.getIdGroup().equalsIgnoreCase(DesigeConf.getProperty("application.admon"));
				if (!isAdmon) {
					// obtenemos la seguridad del grupo
					StringBuffer idStructs = new StringBuffer(50);
					idStructs.append("1");
					security = HandlerGrupo.getAllSecurityForGroup(usuario.getIdGroup(), idStructs);
					// obtenemos la seguridad del usuario
					HandlerDBUser.getAllSecurityForUser(usuario.getIdPerson(), security, idStructs);
					// obtenemos todos los nodos dependiendo de la permisologia
					// del grupo y del usuario al que pertenece dicho usuario
					return HandlerStruct.loadAllNodes(security, usuario.getUser(), usuario.getIdGroup(), subNodos, idStructs.toString());
				} else {
					return HandlerStruct.loadAllNodes(security, usuario.getUser(), usuario.getIdGroup(), subNodos, null);
				}
			} catch (Exception ex) {
				ex.printStackTrace();
				throw new ApplicationExceptionChecked("E0054");
			}
		} else {
			return tree;
		}
	}

	public static String changeNameFile(String newName, String oldName) {
		if (!ToolsHTML.isEmptyOrNull(oldName)) {
			int pos = oldName.indexOf(".");
			if (pos > 0) {
				String ext = oldName.substring(pos + 1, oldName.length());
				ext.trim();
				StringBuffer result = new StringBuffer(50);
				result.append(newName).append(".").append(ext);
				return result.toString();
			}
		}
		return oldName;
	}

	public static String getPrefixToDoc(HttpSession session, Users usuario, String idNode) throws ApplicationExceptionChecked {
		Hashtable tree = (Hashtable) session.getAttribute("tree");
		tree = checkTree(tree, usuario);
		String idLocation = HandlerStruct.getIdLocationToNode(tree, idNode);
		byte typePrefix = 0;
		byte heredarPrefijo = 1;
		if (idLocation != null) {
			BaseStructForm localidad = (BaseStructForm) tree.get(idLocation);
			if (localidad != null) {
				typePrefix = localidad.getTypePrefix();
				// heredarPrefijo = localidad.getHeredarPrefijo();
				if (localidad.getShowCharge() == 1) {
					session.setAttribute("showCharge", "true");
				}
			}
		}
		String prefix = "";
		// Si Hereda Prefijos se Busca
		BaseStructForm carpetaDoc = (BaseStructForm) tree.get(idNode);
		if (carpetaDoc != null) {
			if (Constants.permission == carpetaDoc.getHeredarPrefijo()) {
				if (Constants.permission == heredarPrefijo) {
					prefix = HandlerStruct.getParentPrefix(tree, idNode, 0 == typePrefix);
				}
				// Si la Carpeta Hereda Prefijo se Agrega el Mismo
				if (Constants.notPermission == typePrefix) {
					prefix = carpetaDoc.getPrefix() + prefix;
				} else {
					prefix = prefix + carpetaDoc.getPrefix();
				}
			} else {
				prefix = carpetaDoc.getPrefix();
			}
		}
		return prefix;
	}

	public static String getPrefixToDoc(Hashtable tree, String idNode) {
		String idLocation = HandlerStruct.getIdLocationToNode(tree, idNode);
		byte typePrefix = 0;
		byte heredarPrefijo = 1;
		if (idLocation != null) {
			BaseStructForm localidad = (BaseStructForm) tree.get(idLocation);
			if (localidad != null) {
				typePrefix = localidad.getTypePrefix();
				// heredarPrefijo = localidad.getHeredarPrefijo();
			}
		}
		String prefix = "";
		// Si Hereda Prefijos se Busca
		BaseStructForm carpetaDoc = (BaseStructForm) tree.get(idNode);
		if (carpetaDoc != null) {
			// Si la Carpeta en donde se encuentra el Documento hereda el
			// Prefijo de su Padre
			// se Procede a Reconstruir el Mismo
			// En caso Contrario el Prefijo del Documento s�lo es el prefijo de
			// la Carpeta
			// Contentiva del Documento
			if (carpetaDoc.getHeredarPrefijo() == Constants.permission) {
				prefix = HandlerStruct.getParentPrefix(tree, idNode, 0 == typePrefix);
				if (0 == typePrefix) {
					prefix = carpetaDoc.getPrefix() + prefix;
				} else {
					prefix = prefix + carpetaDoc.getPrefix();
				}
			} else {
				prefix = carpetaDoc.getPrefix();
			}
		}
		return prefix;
	}

	public static String calculateDateExpires(String dateApproved, String docPublic, String typeDoc) {
		String[] itemsDrafts = new String[] { "monthsExpireDrafts", "unitTimeExpireDrafts", "expireDrafts" };
		if ("0".equalsIgnoreCase(docPublic)) {
			itemsDrafts = new String[] { "monthExpireDoc", "unitTimeExpire", "expireDoc" };
		}
		try {
			String[] valuesDrafts = HandlerParameters.getFieldsExpired(itemsDrafts, typeDoc);
			// Si se debe Calcular la Fecha de Vencimiento se procede a Calcular
			// en Caso contrario retorna null
			// => que la Fecha de Vencimiento del Documento no ser� validada
			// hasta un tope
			if (valuesDrafts != null && ("0".equalsIgnoreCase(valuesDrafts[2]) || "false".equalsIgnoreCase(valuesDrafts[2]))) {
				String monthsExpireDrafts = valuesDrafts != null ? valuesDrafts[0] : null;
				String unitDrafts = valuesDrafts != null ? valuesDrafts[1].trim() : null;
				int valueDrafts = 0;
				if (ToolsHTML.isNumeric(monthsExpireDrafts.trim())) {
					valueDrafts = Integer.parseInt(monthsExpireDrafts.trim());
				}
				java.util.Date dateExpire = ToolsHTML.getDateExpireDocument(dateApproved, null, valueDrafts, unitDrafts);
				String dateExp = null;
				if (dateExpire != null) {
					dateExp = ToolsHTML.sdf.format(dateExpire);
					return dateExp;
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}

	public static Date calculateDateMinor(Hashtable dataLocation) {
		String dateSystem = sdfShowConvert.format(new Date());
		String dateMinor = sdfShowConvert.format(new Date());
		java.util.Date hoy = new Date();
		java.util.Date calculada = new Date();
		try {
			hoy = sdfShowConvert.parse(dateSystem);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		if (dataLocation != null && !dataLocation.isEmpty()) {
			int units = 0;
			Enumeration keys = dataLocation.keys();
			while (keys.hasMoreElements()) {
				String idNode = (String) keys.nextElement();
				BaseStructForm baseStructForm = (BaseStructForm) dataLocation.get(idNode);
				units = getUnits(baseStructForm.getUnitExpDoc());
				calculada = sumUnitsToDate(units, hoy, -1 * Integer.parseInt(baseStructForm.getCantExpDoc()));
				if (dateMinor.compareTo(ToolsHTML.sdfShowConvert.format(calculada)) > 0) {
					dateMinor = ToolsHTML.sdfShowConvert.format(calculada);
				}
			}
		}
		log.debug("dateMinor = " + String.valueOf(dateMinor));
		try {
			calculada = sdfShowConvert.parse(dateMinor);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return calculada;
	}

	public static Date calculateDateMayor(Hashtable dataLocation) {
		String dateSystem = sdfShowConvert.format(new Date());
		String dateMinor = sdfShowConvert.format(new Date());
		java.util.Date hoy = new Date();
		java.util.Date calculada = new Date();
		try {
			hoy = sdfShowConvert.parse(dateSystem);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		if (dataLocation != null && !dataLocation.isEmpty()) {
			int units = 0;
			Enumeration keys = dataLocation.keys();
			while (keys.hasMoreElements()) {
				String idNode = (String) keys.nextElement();
				BaseStructForm baseStructForm = (BaseStructForm) dataLocation.get(idNode);
				units = getUnits(baseStructForm.getUnitExpDoc());
				calculada = sumUnitsToDate(units, hoy, Integer.parseInt(baseStructForm.getCantExpDoc()));
				if (dateMinor.compareTo(ToolsHTML.sdfShowConvert.format(calculada)) < 0) {
					dateMinor = ToolsHTML.sdfShowConvert.format(calculada);
				}
			}
		}
		log.debug("dateMinor = " + dateMinor);
		try {
			calculada = sdfShowConvert.parse(dateMinor);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return calculada;
	}

	public static int getUnits(String unit) {
		int units = 0;
		if (Constants.minutes.equalsIgnoreCase(unit)) {
			units = Calendar.MINUTE;
		}
		if (Constants.hour.equalsIgnoreCase(unit)) {
			units = Calendar.HOUR_OF_DAY;
		}
		if (Constants.days.equalsIgnoreCase(unit)) {
			units = Calendar.DAY_OF_MONTH;
		}
		if (Constants.months.equalsIgnoreCase(unit)) {
			units = Calendar.MONTH;
		}
		if (Constants.years.equalsIgnoreCase(unit)) {
			units = Calendar.YEAR;
		}
		return units;
	}

	public static String calendarToTimeStampString(Calendar c) {
		StringBuffer s = new StringBuffer();

		int m = c.get(Calendar.MONTH) + 1;
		int d = c.get(Calendar.DATE);
		int hh = c.get(Calendar.HOUR_OF_DAY);
		int mm = c.get(Calendar.MINUTE);
		int ss = c.get(Calendar.SECOND);

		s.append(c.get(Calendar.YEAR)).append("-");
		s.append(m < 9 ? "0" : "").append(m).append("-");
		s.append(d < 9 ? "0" : "").append(d).append(" ");

		s.append(hh < 9 ? "0" : "").append(hh).append(":");
		s.append(mm < 9 ? "0" : "").append(mm).append(":");
		s.append(ss < 9 ? "0" : "").append(ss);

		return s.toString();
	}

	public static void mains(String[] args) {
		System.out.println(calendarToTimeStampString(Calendar.getInstance()));
	}

	public static String getNameFile(String ruta) {
		int ultimo = 0;
		while (ruta.indexOf(File.separator) != -1) {
			ruta = ruta.substring(ruta.indexOf(File.separator) + 1);
			// //System.out.println(ruta);
		}
		return ruta;
	}

	public static String beforeVersion(String version) {
		try {
			int i = Integer.parseInt(version) - 1;
			return String.valueOf(i < 0 ? 0 : i);
		} catch (NumberFormatException e) {
			char c = version.toCharArray()[0];
			c = (char) (((int) c) - 1);
			return String.valueOf(c == '@' ? 'A' : c);
		}
	}

	public static String ponerEnterInCadena(String cad) {
		int i = 0;
		int j = 0;
		StringBuffer cadNueva = new StringBuffer("");
		while (i < cad.length()) {
			cadNueva.append(cad.charAt(i));
			j = (int) cad.charAt(i);
			if (j == 10) { // si es \n
				cadNueva.append("<br>");
			}
			i++;
		}
		return cadNueva.toString();
	}

	public static String getApplicationStart(String ext) {
		try {
			return DesigeConf.getProperty("doc." + ext);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}

	public static String getUsers(Set set) {
		log.debug("[set]" + set);
		if (set != null && !set.isEmpty()) {
			Iterator usuarios = set.iterator();
			StringBuffer result = new StringBuffer(100);
			PerfilActionForm perfilActionForm = null;
			boolean isFirts = true;
			while (usuarios.hasNext()) {
				perfilActionForm = (PerfilActionForm) usuarios.next();
				if (isFirts) {
					isFirts = false;
				} else {
					result.append(",");
				}
				result.append(perfilActionForm.getApellidos()).append(" ").append(perfilActionForm.getNombres());
				if (isFirts) {
					isFirts = false;
				}
			}
			return result.toString();
		}
		return "";
	}

	public static Hashtable setUserToHash(Set set) {
		Hashtable result = new Hashtable();
		if (set != null && !set.isEmpty()) {
			Iterator usuarios = set.iterator();
			PerfilActionForm perfilActionForm = null;
			boolean isFirts = true;
			while (usuarios.hasNext()) {
				perfilActionForm = (PerfilActionForm) usuarios.next();
				result.put(perfilActionForm.getId(), perfilActionForm);
				if (isFirts) {
					isFirts = false;
				}
			}
		}
		return result;
	}

	public static String getUsrs(Collection usrs) {
		StringBuffer usr = new StringBuffer(50);
		for (Iterator iterator = usrs.iterator(); iterator.hasNext();) {
			Search search = (Search) iterator.next();
			if (usr.length() > 0) {
				usr.append(",");
			}
			usr.append(search.getId());
		}
		return usr.toString();
	}

	public static Collection getUsersCollection(Set set) {
		Vector resultado = new Vector();
		if (set != null && !set.isEmpty()) {
			Iterator usuarios = set.iterator();
			PerfilActionForm perfilActionForm = null;
			while (usuarios.hasNext()) {
				perfilActionForm = (PerfilActionForm) usuarios.next();
				Search bean = new Search(perfilActionForm.getUser(), perfilActionForm.getApellidos() + " " + perfilActionForm.getNombres());
				bean.setAditionalInfo(perfilActionForm.getCargo());
				resultado.add(bean);
			}
		}
		return resultado;
	}

	public static Collection copyCollectionItems(Collection source) {
		log.debug("[copyCollectionItems]");
		Vector result = new Vector();
		if (source != null) {
			for (Iterator iterator = source.iterator(); iterator.hasNext();) {
				Search bean = (Search) iterator.next();
				// Se Hace una copia del Bean xq sino se pierde la Informaci�n
				// al Aplicar el filtro....
				Search copy = new Search(bean.getId(), bean.getDescript());
				log.debug("Descript: " + bean.getDescript());
				copy.setAditionalInfo(bean.getAditionalInfo());
				result.add(copy);
			}
		}
		return result;
	}

	public static BaseWorkFlow getCopyBean(BaseWorkFlow origen) {
		BaseWorkFlow copia = new BaseWorkFlow();
		if (origen != null) {
			copia.setConditional(origen.getConditional());
			copia.setSecuential(origen.getSecuential());
			copia.setNumDocument(origen.getNumDocument());
			copia.setDateCreationWF(origen.getDateCreationWF());
			// copia.setDateExpireWF(origen.getDateExpireWF());
			copia.setMayorVersionDocument(origen.getMayorVersionDocument());
			copia.setNotified(origen.getNotified());
			copia.setLastVersion(origen.getLastVersion());
			copia.setMayorVersionDocument(origen.getMayorVersionDocument());
			copia.setComments("");
			copia.setDateExpireWF(origen.getDateExpireWF());
			copia.setExpire(origen.getExpire());
			copia.setNumVersion(origen.getNumVersion());
			log.debug("origen.getNumDocument(): " + origen.getNumDocument());
		}
		return copia;
	}

	public PermissionUserForm getSecurityUserInDoc(Users usuario, String idDocument, String idFolder) {
		PermissionUserForm perm = null;
		if (usuario == null)
			return perm;
		// SI ES EL ADMINISTRADOR, POR DEFECTO TIENE TODOS EN
		// Constants.permission
		if (DesigeConf.getProperty("application.userAdmon").compareTo(usuario.getUser()) == 0) {
			perm = new PermissionUserForm(Constants.permission);

			// Luis Cisneros
			// 24-03-07
			// Asi sea DiosDedo no puede imprimir si en la configuraci�n indica
			// que necesita un flujo
			boolean printOwnerAdmin = false;
			try {
				printOwnerAdmin = String.valueOf(HandlerParameters.PARAMETROS.getPrintOwnerAdmin()).equals("0");
				if (!printOwnerAdmin) {
					// Ahora, antes de terminar de Negar, me falta verificar si
					// el tipo tiene explicitamente en el documento permiso para
					// imprimirlo.
					String permiso = HandlerDBUser.getSecurityForPintDocuments(usuario.getUser(), idDocument);
					permiso = permiso == null ? "0" : permiso;
					perm.setToImpresion(Byte.parseByte(permiso));

					// Si el par�metro de libre impresi�n no est� seleccionado,
					// el
					// administrador no va a poder
					// imprimir a menos que solicite la impresi�n y el
					// propietario se la autorice.
					// Tampoco puede otorgar permiso de impresion
					// perm.setToImpresion(Byte.parseByte("0"));
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			// FIN 24-03-07

			return perm;
		}
		// :SEGURIDAD:
		if (idDocument != null) {
			// Se Carga la Seguridad para el documento
			perm = HandlerDBUser.getSecurityForIDUserInDocs(usuario.getUser(), idDocument);
			if (perm == null) {
				// Se Carga la Seguridad para el Grupo
				perm = HandlerGrupo.getSecurityForIDGroupInDoc(usuario.getIdGroup(), idDocument);
				if (perm == null) {
					// Se Carga la Seguridad para el Usuario a Nivel de Carpeta
					perm = HandlerStruct.getSecurityForIDUserInStruct(usuario.getIdPerson(), idFolder);
					if (perm == null) {
						// Se Carga la Seguridad para el Usuario a Nivel de
						// Grupos
						perm = HandlerStruct.getSecurityForIDGroupInStruct(usuario.getIdGroup(), idFolder);
					}
				}
			}
		} else {
			if (idFolder != null) {
				// Se Carga la Seguridad para el Usuario a Nivel de Carpeta
				perm = HandlerStruct.getSecurityForIDUserInStruct(usuario.getIdPerson(), idFolder);
				if (perm == null) {
					perm = HandlerStruct.getSecurityForIDGroupInStruct(usuario.getIdGroup(), idFolder);
					// Se Carga la Seguridad para el Usuario a Nivel de Grupos
				}
			}
		}
		return perm;
	}

	public static boolean abrirEditorWebStart(HttpServletRequest request) {
		String editorOnDemand = String.valueOf(HandlerParameters.PARAMETROS.getEditorOnDemand());

		return editorOnDemand.equals("1");
	}

	public static boolean editOriginatorWF() {
		return String.valueOf(HandlerParameters.PARAMETROS.getEditOriginatorWF()).equals("1");
	}

	public static boolean copyContents() {
		return String.valueOf(HandlerParameters.PARAMETROS.getCopyContents()).equals("1");
	}

	public static NumberSacop numberSacop() {
		String number = String.valueOf(HandlerParameters.PARAMETROS.getNumberSacop());
		if (number == null || number.length() < 7)
			number = "1000010";
		NumberSacop ns = new NumberSacop(number);
		return ns;
	}

	public static boolean isNotifyEmail() {
		if (Constants.notifyEmail == null) {
			Constants.notifyEmail = String.valueOf(HandlerParameters.PARAMETROS.getNotifyEmail());
		}

		return Constants.notifyEmail.equals("0");
	}

	public static boolean isNotifyEmail(Connection con) {
		if (Constants.notifyEmail == null) {
			Constants.notifyEmail = String.valueOf(HandlerParameters.PARAMETROS.getNotifyEmail());
		}

		return Constants.notifyEmail.equals("0");
	}

	public static String getFolderTmp() {
		return DesigeConf.getProperty("folderTmp"); 
	}

	public static String getFolderCmp() {
		return HandlerParameters.PARAMETROS.getFolderCmp();
	}

	public static String getAttachedField() {
		return HandlerParameters.PARAMETROS.getAttachedField();
	}

	public static String getAttachedFolder0() {
		return HandlerParameters.PARAMETROS.getAttachedFolder0();
	}

	public static String getAttachedFolder1() {
		return HandlerParameters.PARAMETROS.getAttachedFolder1();
	}

	public static String getDomain() {
		return HandlerParameters.PARAMETROS.getDomain();
	}

	public static String getLdapUrl() {
		return HandlerParameters.PARAMETROS.getLdapUrl();
	}

	/**
	 * 
	 * @return
	 * @throws ErrorDeAplicaqcion
	 */
	public static String getRepository() throws ErrorDeAplicaqcion {
		return getRepository(null);
	}

	/**
	 * 
	 * @param con
	 * @return
	 * @throws ErrorDeAplicaqcion
	 */
	public static String getRepository(Connection con) throws ErrorDeAplicaqcion {
		// if(Constants.PATH_REPOSITORY==null ||
		// Constants.PATH_REPOSITORY.trim().equals("")) {
		Constants.PATH_REPOSITORY = HandlerParameters.PARAMETROS.getRepository();
		// if(Constants.PATH_REPOSITORY!=null) {
		// Constants.PATH_REPOSITORY = Constants.PATH_REPOSITORY.replaceAll("/",
		// File.separator);
		// }
		// }
		if (Constants.PATH_REPOSITORY == null || Constants.PATH_REPOSITORY.trim().equals("")) {
			throw new ErrorDeAplicaqcion("La ruta del repositorio de archivos no se ha indicado en la configuraci�n");
		}
		return Constants.PATH_REPOSITORY;
	}

	public static boolean isRepositoryDefine(HttpServletRequest request) {
		try {
			String path = getRepository();
			File f = new File(path);
			log.info(path);
			log.info(String.valueOf(f.exists()));
			if (!f.exists()) {
				return false;
			}
			log.info("repositorio="+path);
			log.info("request.getContextPath()="+request.getContextPath());
			if (!path.endsWith(request.getContextPath())) {
				return false;
			}

		} catch (Exception e) {
			return false;
		}

		return true;
	}

	public static String getStatusDocumento(String sStatu, String sStatuVer, ResourceBundle rb, String dateSystem, String dateExpiresDrafts, String dateExpires) {
		String statusDocumento = "";
		int statuDocuments = 0;
		int statuVer = 0;

		if (!ToolsHTML.isEmptyOrNull(sStatu) && ToolsHTML.isNumeric(sStatu))
			statuVer = Integer.parseInt(sStatu.trim());
		if (!ToolsHTML.isEmptyOrNull(sStatuVer) && ToolsHTML.isNumeric(sStatuVer))
			statuDocuments = Integer.parseInt(sStatuVer.trim());

		switch (statuDocuments) {
		case 1: // Borrador 1
			statusDocumento = rb.getString("wf.statuDoc1");
			break;
		case 3: // Revisado 3
			statusDocumento = rb.getString("wf.statuDoc5");
			break;
		case 5: // Aprobado 5
			statusDocumento = rb.getString("wf.statuDoc5");
			break;
		case 6: // Obsoleto 6
			statusDocumento = rb.getString("wf.statuDoc6");
			break;
		default:
			break;
		}

		if (!ToolsHTML.isEmptyOrNull(dateSystem) && dateSystem.length() > 9) {
			if (statuDocuments == 1 && !ToolsHTML.isEmptyOrNull(dateExpiresDrafts) && dateExpiresDrafts.length() > 9
					&& (dateSystem.substring(0, 10)).compareTo(dateExpiresDrafts.substring(0, 10)) >= 0) {
				// Borradores expirados
				statusDocumento += " Expirado";

			} else if (statuDocuments == 5 && !ToolsHTML.isEmptyOrNull(dateExpires) && dateExpires.length() > 9
					&& dateSystem.substring(0, 10).compareTo(dateExpires.substring(0, 10)) >= 0) {
				// Aprobados expirados
				statusDocumento += " Expirado";
			}
		}

		switch (statuVer) {
		case 2: // En Revision
			statusDocumento += " / " + rb.getString("wf.statuDoc2");
			break;
		case 4: // En Aprobacion
			statusDocumento += " / " + rb.getString("wf.statuDoc4");
			break;
		case 15: // En FTP
			statusDocumento += " / " + rb.getString("wf.statuDoc9");
			break;
		default:
			break;
		}
		return statusDocumento;

	}

	public static String updateURLVerDocumento(String coment, HttpServletRequest request, String path) {
		StringBuffer url = new StringBuffer();
		String clave = "<otnemucoD reV>";
		String clave2 = "<tnemucoD weiV>";
		StringBuffer r = new StringBuffer();
		String cad;
		try {
			if (coment == null) {
				return coment;
			}
			if (request != null) {
				url.append(request.getServerName()).append(":").append(request.getServerPort()).append(request.getContextPath());
			} else {
				url.append(path);
			}

			r = new StringBuffer(coment);
			r.reverse();
			if (r.toString().indexOf(clave) == -1 && r.toString().indexOf(clave2) == -1) {
				return coment;
			}

			if (r.toString().indexOf(clave) != -1) {
				cad = r.toString().substring(r.toString().indexOf(clave) + clave.length());
			} else {
				cad = r.toString().substring(r.toString().indexOf(clave2) + clave2.length());
			}
			cad = cad.substring(0, cad.indexOf("<"));

			r.setLength(0);
			r.append(cad);
			r = r.reverse();
			cad = r.toString();

			cad = cad.substring(cad.indexOf("http://") + 7);
			String[] palabras = cad.split("/");
			cad = palabras[0] + "/" + palabras[1];

			String comentario = coment.toString();
			comentario = comentario.replaceAll("' target='", "&flujo=true' target='");
			return comentario.replaceAll(cad, url.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}

		return coment;
	}

	public static boolean isValid(String typeKey, HttpServletRequest request) {
		if (typeKey == null) {
			return false;
		}
		if (request == null) {
			return false;
		}
		try {
			ResourceBundle rb = ToolsHTML.getBundle(request);

			char[] tmp = { 42, 70, 48, 99, 117, 115 };
			Date fecha = new Date();
			SimpleDateFormat sdf = new SimpleDateFormat("HHMMddmm");

			StringBuffer sb = new StringBuffer(rb.getString("lic.version").replaceAll("\\.", ""));
			int version = Integer.parseInt(sb.reverse().toString());

			System.out.println("Usuarios registrados");
			int usu = Integer.parseInt(HandlerDBUser.getNumeroDeUsuarios());
			System.out.println(usu);

			// System.out.println(String.valueOf(tmp).concat(sdf.format(fecha)).concat(String.valueOf(version
			// * usu)).concat(String.valueOf((char) 42)));
			return typeKey.equals(String.valueOf(tmp).concat(sdf.format(fecha)).concat(String.valueOf(version * usu)).concat(String.valueOf((char) 42)));
		} catch (Exception e) {
			return false;
		}
	}

	public static boolean isActiveDirectoryConfigurated() {
		return !ToolsHTML.isEmptyOrNull(ToolsHTML.getDomain()) && !ToolsHTML.isEmptyOrNull(ToolsHTML.getLdapUrl());
	}
	
	public static boolean isValidActiveDirectory(String user, String typeKey) throws NullPointerException {
		if (user == null || typeKey == null || ToolsHTML.isEmptyOrNull(ToolsHTML.getDomain()) || ToolsHTML.isEmptyOrNull(ToolsHTML.getLdapUrl())) {
			return false;
		}
		try {
			String ldapurl = ToolsHTML.getLdapUrl();// "ldap://ccsfocus01:389";
			String dominio = ToolsHTML.getDomain();// "Focus";

			if (ToolsHTML.isEmptyOrNull(ldapurl) || ToolsHTML.isEmptyOrNull(dominio)) {
				return false;
			}

			StringBuffer cadena = new StringBuffer(dominio).append("\\").append(user);
			boolean IsAuthenticated = false;
						
			ldapfastbind ctx = new ldapfastbind(ldapurl);

			log.info("Intentando conectarse a " + ldapurl + ", " + cadena);

			// IsAuthenticated = ctx.Authenticate("dominio\\usuario","clave");
			// IsAuthenticated = ctx.Authenticate("Focus\\jrivero","F0cus41");
			IsAuthenticated = ctx.Authenticate(cadena.toString(), typeKey);

			if (!IsAuthenticated) {
				log.info("No se logro validar en ActiveDirectory a " + cadena);
			} else {
				log.info("El usuario " + cadena + ", fue validado contra ActiveDirectory");
			}
			return IsAuthenticated;
		} catch (NullPointerException e) {
			throw new NullPointerException();
		} catch (Exception e) {
			log.error(e.getLocalizedMessage(), e);
			return false;
		}
	}
	
	public static String getStatus(String url) throws IOException {
		 
		String result = "";
		int code = 200;
		try {
			URL siteURL = new URL(url);
			HttpURLConnection connection = (HttpURLConnection) siteURL.openConnection();
			connection.setRequestMethod("GET");
			connection.setConnectTimeout(3000);
			connection.connect();
 
			code = connection.getResponseCode();
			if (code == 200) {
				result = "-> Green <-\t" + "Code: " + code;
				;
			} else {
				result = "-> Yellow <-\t" + "Code: " + code;
			}
		} catch (Exception e) {
			result = "-> Red <-\t" + "Wrong domain - Exception: " + e.getMessage();
 
		}
		System.out.println(url + "\t\tStatus:" + result);
		return result;
	}	

	public static String estructuraWrap(String valor) {
		StringBuffer cad = new StringBuffer();
		String[] arr = valor.split("\\\\");
		StringBuffer spa = new StringBuffer();
		for (int k = 0; k < arr.length - 1; k++) {
			cad.append(spa);
			cad.append("<font style='color:blue'>&gt;</font>");
			cad.append(arr[k]);
			cad.append("<br/>");
			spa.append("&nbsp;&nbsp;&nbsp;&nbsp;");
		}
		return cad.toString();
	}

	public static String estructura(String valor) {
		StringBuffer cad = new StringBuffer();
		String[] arr = valor.split("\\\\");
		StringBuffer spa = new StringBuffer();
		for (int k = 0; k < arr.length - 1; k++) {
			cad.append(arr[k]);
			cad.append("\\");
		}
		return (cad.toString().trim().length() == 0 ? valor : cad.toString());
	}

	public static String formatNumberInteger(String numeroLetra) {
		String result = "?";
		try {
			numero.setGroupingUsed(true);
			numero.setMinimumFractionDigits(0);
			numero.setMaximumFractionDigits(0);

			numeroLetra = numeroLetra.replace(',', '.');
			result = numero.format(Double.parseDouble(numeroLetra));
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}

		return result;
	}

	public static boolean orden(HttpSession session, String name) {
		return !(session.getAttribute("orderBy") != null && session.getAttribute("orderBy").toString().equals(name));
	}

	public static String getPathDocPrint() {
		if (ubicac == null) {
			ubicac = new StringBuffer(getPath());
			ubicac.append("WEB-INF").append(File.separatorChar);
			ubicac.append("classes").append(File.separatorChar);
			ubicac.append("com").append(File.separatorChar);
			ubicac.append("focus").append(File.separatorChar);
			ubicac.append("docprint").append(File.separatorChar);
		}
		return ubicac.toString();
	}

	/*
	 * Carpeta donde se almacenan documentos temporales solo para uso del sistema
	 */
	public static String getPathCache() {
		StringBuffer dir = new StringBuffer(getPath());
		dir.append("WEB-INF").append(File.separatorChar);
		dir.append("classes").append(File.separatorChar);
		dir.append("com").append(File.separatorChar);
		dir.append("desige").append(File.separatorChar);
		dir.append("webDocuments").append(File.separatorChar);
		dir.append("userLink").append(File.separatorChar);
		return dir.toString();
	}

	public static String getPathTmp() {
		StringBuffer dir = new StringBuffer(getPath());
		dir.append("tmp").append(File.separatorChar);
		return dir.toString();
	}

	public static String getPathDigitalizados() {
		StringBuffer dir = new StringBuffer(getPath());
		dir.append("digitalizados").append(File.separatorChar);
		return dir.toString();
	}

	public static String getPathTmpURL(HttpServletRequest request) {
		StringBuffer dir = new StringBuffer();
		dir.append(request.getScheme());
		dir.append("://");
		dir.append(request.getServerName());
		dir.append(":");
		dir.append(request.getServerPort());
		dir.append(request.getContextPath());
		dir.append("/tmp/");
		return dir.toString();
	}

	public static String getBasePath(HttpServletRequest request) {
		StringBuffer path = new StringBuffer();
		path.append(request.getScheme()).append("://").append(request.getServerName()).append(":").append(request.getServerPort());
		path.append(request.getContextPath()).append("/");
		return path.toString();
	}

	public static String getBasePathDigitalizados(HttpServletRequest request) {
		StringBuffer path = new StringBuffer();
		path.append(request.getScheme()).append("://").append(request.getServerName()).append(":").append(request.getServerPort());
		path.append(request.getContextPath()).append("/");
		path.append("digitalizados/");
		return path.toString();
	}

	public static String getBasePathDigitalizados(HttpServletRequest request, String numero) {
		DigitalTO digitalTO = new DigitalTO();
		digitalTO.setIdDigital(numero);
		StringBuffer path = new StringBuffer();
		path.append(request.getScheme()).append("://").append(request.getServerName()).append(":").append(request.getServerPort());
		path.append(request.getContextPath()).append("/");
		path.append("digitalizados/").append(digitalTO.getNameFileDisk());
		return path.toString();
	}

	public static String getFileBasePathDigitalizados(HttpServletRequest request, String numero) {
		StringBuffer pathRealPdf = new StringBuffer();
		StringBuffer pathPdf = new StringBuffer();
		StringBuffer path = new StringBuffer();
		StringBuffer pathReal = new StringBuffer();
		try {
			DigitalTO digitalTO = new DigitalTO();
			digitalTO.setIdDigital(numero);

			pathRealPdf.append(getPathDigitalizados()).append(digitalTO.getNameFileDisk());

			File file = new File(pathRealPdf.toString());
			if (!file.exists()) {
				DigitalDAO digitalDAO = new DigitalDAO();
				digitalTO = digitalDAO.findById(digitalTO);
				pathReal.append(getPathDigitalizados()).append(digitalTO.getNameFileDiskSinExt());

				// extraemos la extension del archivo
				StringBuffer name = new StringBuffer(StringUtil.getOnlyNameFile(digitalTO.getNameFile()));
				name = name.reverse();
				StringBuffer ext = new StringBuffer(name.substring(0, name.indexOf(".") + 1));
				ext = ext.reverse();

				pathReal.append(ext);

				// los convertimos a pdf

				PdfConvert pdf = new PdfConvert();
				pdf.convert(pathReal.toString(), pathRealPdf.toString());
			}
			// url del archivo
			pathPdf.append(request.getScheme()).append("://").append(request.getServerName()).append(":").append(request.getServerPort());
			pathPdf.append(request.getContextPath()).append("/");
			pathPdf.append("digitalizados/").append(digitalTO.getNameFileDisk());

		} catch (Exception e) {
			e.printStackTrace();
		}

		return pathPdf.toString();
	}

	public static String getPath() {
		StringBuffer dir = new StringBuffer();
		dir.append(SERVLET_CONTEXT.getRealPath(File.separator));
		dir.append(!dir.toString().endsWith(File.separator)?File.separator:"");
		return dir.toString();
	}

	public static String getPathKey() {
		StringBuffer dir = new StringBuffer(getPath());
		dir.append("WEB-INF").append(File.separator).append("key.dat");
		return dir.toString();
	}

	public static String getPathImg() {
		StringBuffer dir = new StringBuffer(getPath());
		dir.append("img").append(File.separatorChar);
		return dir.toString();
	}

	public static String getPathImgLogos() {
		StringBuffer dir = new StringBuffer(getPath());
		dir.append("img").append(File.separatorChar);
		dir.append("logos").append(File.separatorChar);
		return dir.toString();
	}

	public static String getPathImages() {
		StringBuffer dir = new StringBuffer(getPath());
		dir.append("images").append(File.separatorChar);
		return dir.toString();
	}

	public static void setQualitySystem(HttpSession session) {
		String qualitySystem = null;
		try {
			qualitySystem = String.valueOf(HandlerParameters.PARAMETROS.getQualitySystem());
			session.setAttribute("showNorms", (Constants.permissionSt.equalsIgnoreCase(qualitySystem)) ? "false" : "true");
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public static String getMimeType(String nameFile) {
		int pos = nameFile.indexOf(".") + 1;
		String extension = nameFile.substring(pos).toLowerCase();

		if (mime.containsKey(extension)) {
			return mime.get(extension);
		}

		/*
		 * //Magic parser = new Magic(); MagicMatch match; String nombreArchivo =nameFile; try { match = Magic.getMagicMatch(fichero,true);
		 * //System.out.println(match.getMimeType()); if(match.getMimeType().equals("application/zip")) { nombreArchivo="archivo.xlsx"; }
		 * if(match.getMimeType().equals("application/msword")) { nombreArchivo="archivo.zip"; } } catch (MagicParseException e) { e.printStackTrace(); }
		 */

		return "";
	}

	public static String getPathAdvertenciaPdf() {
		StringBuffer dir = new StringBuffer(getPath());
		dir.append("advertencia.pdf");
		return dir.toString();
	}

	public static void main2(String[] args) {
		File f = new File("C:\\temp\\test.doc");
		File f2 = new File("C:\\temp\\testX2.doc");
		File f3 = new File("file:\\\\ubuntu-sun\\opt");

		// System.out.println(new Date(f.lastModified()));

		// f.createNewFile();
		// f.renameTo(f2);
		// try {
		// Thread.currentThread().sleep(2000);
		// } catch (InterruptedException e) {
		// e.printStackTrace();
		// }
		// f2.renameTo(f);
		//
		// f = new File("C:\\temp\\test.doc");
		// //System.out.println(new Date(f.lastModified()));

		// Archivo a = new Archivo();
		// String lic = "5XX2-844-5EBZ-3083-106X408";
		// String lic = "52BE-0975-4XDX-5473-106X467";
		// String lic = "52XX-08XX-5XZX-2E8F-17149F6";
		// String lic = "1D4125-5F16X6-1D54X4-0362DE-17149F5";
		// //58-2N-6S-4M-2G-0N
		// String lic = "18B8F4-0389F5-1D412F-8ZB7E8-14382D1";
		// //3W-3T-4L-3D-52-4U
		String lic = "35285C-5E7F-16775-433Z-07E3-171742D";
		String[] p = lic.replace("X", "A").replace("Z", "C").split("-");
		String cad = "";
		for (int i = 0; i < p.length; i++) {
			String piece = "";
			if (p[i].startsWith("0")) {
				piece = "0" + Long.parseLong(p[i], 16);
			} else {
				piece = "" + Long.parseLong(p[i], 16);
			}
			log.info("Long.parseLong(p[" + i + "], 16)=" + p[i] + "/" + Long.parseLong(p[i], 16) + ", piece='" + piece + "'");
			cad += piece;
		}

		String num = new ToolsHTML().xor2(cad.toString());
		log.info("cad=" + cad.toString());
		log.info("num=" + num);

		try {
			System.out.println(Archivo.getNameFileEncripted("versiondoc", 34621, "E:/qweb_repositorios/produccion"));
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ErrorDeAplicaqcion e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void deleteVersionCache(String idVersion) {
		deleteVersionCache(null,idVersion);
	}
	
	public static void deleteVersionCache(Connection con, String idVersion) {
		// como en este punto no sabemos si el archivo es doble version o no
		// respaldaremos la cache del mismo ante cualquier eventualidad
		String nameFile = DocumentDAO.getNameFileFromVersionDoc(con,idVersion);
		boolean esDobleVersion = false;
		if (extensionIsDobleVersion(con, getExtension(nameFile))) {
			esDobleVersion = true;
		}
		String nameFileCache = ToolsHTML.getPathCache().concat("datos").concat(idVersion).concat(".dat");
		File cache = new File(nameFileCache);
		if (cache.exists()) {
			if (esDobleVersion) {
				// respaldamos la cache
				//respaldarVersionCache(idVersion);
			} else {
				cache.delete();
			}
		} else {
			try {
				nameFileCache = Archivo.getNameFileEncripted("versiondocview", Integer.parseInt(idVersion), null);
				cache = new File(nameFileCache);

				if (cache.exists()) {
					if (esDobleVersion) {
						// respaldamos la cache
						//respaldarVersionCache(idVersion);
					} else {
						//cache.delete();   // IMPORTANTE: no borramos el cache ya que no se genera automaticamente
					}
					log.info("nameFileCache='" + nameFileCache + "' para version # " + idVersion + " fue eliminado");
				}
			} catch (NumberFormatException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (NoSuchAlgorithmException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ErrorDeAplicaqcion e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	/**
	 * Solo deberia ser usado con documentos de doble version.
	 * 
	 * @param idVersion
	 */
	/*
	public static void respaldarVersionCache(String idVersion) {
		// en edicion se deshizo el cambio modificado, por lo tanto hay que
		// regresar a la cache anterior
		try {
			String nameFileCache = Archivo.getNameFileEncripted("versiondocview", Integer.parseInt(idVersion), null);
			File cache = new File(nameFileCache);

			if (cache.exists()) {
				//ydavila Ticket  001-00-003196 No conformidad de visualizaci�n de documentos doble versi�n
				File cache1 = new File(nameFileCache); //+Archivo.CACHE_DOBLE_VERSION_SUFIJO);
				if (cache1.exists()) {
					cache1.delete();
				}
				//ydavila Ticket  001-00-003196 No conformidad de visualizaci�n de documentos doble versi�n
				cache.renameTo(new File(nameFileCache)); // + Archivo.CACHE_DOBLE_VERSION_SUFIJO));
				log.info("nameFileCache='" + nameFileCache + "' para version # " + idVersion + " fue respaldado");
			} else {
				log.info("No existe la cache que se desea respaldar");
			}
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ErrorDeAplicaqcion e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	*/
	

	/**
	 * 
	 * @param value
	 * @param defaultValue
	 * @return
	 */
	public static int parseInt(String value, int defaultValue) {
		try {
			return Integer.parseInt(value);
		} catch (NumberFormatException e) {
			return defaultValue;
		}
	}

	/**
	 * 
	 * @param value
	 * @param defaultValue
	 * @return
	 */
	public static byte parseByte(String value, byte defaultValue) {
		try {
			return Byte.parseByte(value);
		} catch (NumberFormatException e) {
			return defaultValue;
		}
	}

	/**
	 * 
	 * @param texto
	 * @return
	 */
	public static String codificar(String texto) {
		char[] abc = "abcdefghijklmnopqrstuvwxyz".toCharArray();
		byte[] bytes = texto.getBytes();
		StringBuilder sb = new StringBuilder(1024);
		String sep = "";
		for (int i = 0; i < bytes.length; i++) {
			sb.append(sep).append((bytes[i] ^ 33));
			sep = String.valueOf(abc[(int) (Math.random() * 25 + 1)]);
		}
		return sb.toString();
	}

	/**
	 * 
	 * @param texto
	 * @return
	 */
	public static String decodificar(String texto) {
		texto = texto.replaceAll("[^0-9]", ",");
		String[] arr = texto.split(",");
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < arr.length; i++) {
			sb.append((char) (Byte.parseByte(arr[i]) ^ 33));
		}
		return sb.toString();
	}

	/**
	 * 
	 * @param cad
	 * @param largo
	 * @return
	 */
	public static String zero(int cad, int largo) {
		return zero(String.valueOf(cad), largo);
	}

	/**
	 * 
	 * @param cad
	 * @param largo
	 * @return
	 */
	public static String zero(String cad, int largo) {
		if (cad.length() < largo) {
			cad = "0".concat(cad);
			cad = zero(cad, largo);
		}
		return cad;
	}

	/**
	 * 
	 * @return
	 */
	public static String getFecha() {
		return sdfShowWithoutHour.format(new Date());
	}

	/**
	 * 
	 * @return
	 */
	public static String getFechaAMD() {
		return date.format(new Date());
	}
	
	public static String getFechaAMD(Date fecha) {
		return date.format(fecha);
	}

	/**
	 * 
	 * @param request
	 * @return
	 */
	public static String nextDigitalFile(HttpServletRequest request) {
		try {
			DigitalFacade df = new DigitalFacade(request);
			DigitalTO digitalTO = new DigitalTO();
			digitalTO.setIdDigital((String) request.getSession().getAttribute("idDigital"));
			String id;
			id = df.getNext(digitalTO);
			if (id.equals("")) {
				id = df.getPrevious(digitalTO);
				if (id.equals("")) {
					return "searchDigital.do?accion=1";
				}
			}
			return "upploadFileFrame.do?idDigital=" + id;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "searchDigital.do?accion=1";
	}

	/**
	 * 
	 * @param id
	 * @return
	 */
	public static String getNodeName(String id) {
		try {
			return HandlerStruct.getNameNodeString(id);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}

	/**
	 * 
	 * @param id
	 * @return
	 */
	public static String getUserName(String id) {
		try {
			return HandlerDBUser.getNameUserOnly(id);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}

	/**
	 * 
	 * @param id
	 * @return
	 */
	public static TypeDocumentsForm getTypeDocuments(String id) {
		try {
			TypeDocumentsForm forma = new TypeDocumentsForm();
			forma.setId(id);
			HandlerTypeDoc.load(forma);
			return forma;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new TypeDocumentsForm();
	}

	/**
	 * 
	 * @param con
	 * @param time
	 * @param typeDocument
	 * @param dateDeadForm
	 * @return
	 */
	public static String getDateDeadDoc(Connection con, Timestamp time, String typeDocument, String dateDeadForm) {
		String dateDeads = null;
		try {
			// Archivo muerto para este documento
			String[] itemsDead = new String[] { "monthsDeadDocs", "unitTimeDead" };
			String[] valuesDead = HandlerParameters.getFieldsExpired(con, itemsDead, typeDocument);
			String monthDeadDoc = valuesDead != null ? valuesDead[0] : null;
			String unitDead = valuesDead != null ? valuesDead[1].trim() : null;
			int valueDead = 0;
			if (ToolsHTML.isNumeric(monthDeadDoc.trim())) {
				valueDead = Integer.parseInt(monthDeadDoc.trim());
			}

			String dateTime = (String) time.toString();
			java.util.Date dateDead = ToolsHTML.getDateExpireDocument(dateTime, dateDeadForm, valueDead, unitDead);
			if (dateDead != null) {
				dateDeads = ToolsHTML.sdf.format(dateDead);
			}
		} catch (Exception e) {

		}
		return dateDeads;
	}

	/**
	 * 
	 * @return
	 */
	public static int documentosRegistrados() {
		int ret = 0;
		try {
			CachedRowSet crs = JDBCUtil.executeQuery(new StringBuffer("select count(numgen) from documents"), Thread.currentThread().getStackTrace()[1].getMethodName());
			if (crs.next()) {
				ret = crs.getInt(1);
			}
		} catch (Exception e) {
			e.printStackTrace();
			ret = -1;
		}
		return ret;
	}

	public static boolean isNumberUserValidForLicence() {
		int usuarios = 0;
		
		try {
			CachedRowSet crs = JDBCUtil.executeQuery(new StringBuffer("select count(idPerson) from person"), Thread.currentThread().getStackTrace()[1].getMethodName());
			if (crs.next()) {
				usuarios = crs.getInt(1);
			}
			ModuloBean mod = validarLicencia();
			if(mod.getEdicion().equals(ModuloBean.EDICION_LITE)) {
				if(usuarios>Constants.MAXIMO_USUARIOS_VERSION_LITE) {
					return false;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return true;
	}
	/**
	 * 
	 * @param lic
	 * @param empresa
	 * @return
	 * @throws IOException
	 * @throws Exception
	 */
	public String decifrar(String lic, String empresa) throws IOException, Exception {
		return decifrar(lic,empresa,null);
	}
	
	public String decifrar(String lic, String empresa, String macPrueba) throws IOException, Exception {
		// 250837708152144
		ResourceBundle rb = ResourceBundle.getBundle("LoginBundle");
		System.out.println(NetworkInfo.getSerialMac());
		String mac = BaseConverterUtil.MacFromBase36(NetworkInfo.getSerialMac());
		if(macPrueba!=null) {
			mac = macPrueba;
		}
		String suma = String.valueOf(NetworkInfo.getMacAddressSuma(mac));
		StringBuffer cad = new StringBuffer();
		int version = Integer.parseInt(rb.getString("lic.version").replaceAll("\\.", ""));
		
		//version = 452; //prueba
		//lic = "0868B6-1343R03-142569904-6836153S4";
		
		// validamos el nombre de la empresa
		String nombreEmp1 = lic.substring(0, 6).toUpperCase();
		String nombreEmp2 = Encriptor.getMD5(empresa).substring(0, 6).toUpperCase();

		if (!nombreEmp1.equals(nombreEmp2)) {
			throw new Exception("El nombre de la empresa no es valido. Licencia invalida");
		}

		lic = lic.substring(7);
		
		String fechaInicio = lic.substring(0,lic.indexOf("-"));
		lic = lic.substring(fechaInicio.length()+1);

		fechaInicio = String.valueOf(BaseConverterUtil.fromBase16(toConvertLetter(fechaInicio,false)));
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		Date activacion = sdf.parse(fechaInicio);
		Date vence = sumUnitsToDate(Calendar.YEAR,activacion,1);
		Date gracia = sumUnitsToDate(Calendar.MONTH,vence,3);
		Date hoy = new Date();
		
		ToolsHTML.LICENCIA_VENCIDA = false;
		ToolsHTML.LICENCIA_GRACIA_VENCIDA = false;
		if(hoy.compareTo(vence)>0) {
			ToolsHTML.LICENCIA_VENCIDA = true;
			//throw new Exception("La Licencia esta vencida");
		}
		if(hoy.compareTo(gracia)>0) {
			ToolsHTML.LICENCIA_GRACIA_VENCIDA = true;
			//throw new Exception("La Licencia esta vencida");
		}		
		
		String[] p = toConvertLetter(lic,false).split("-");
		for (int i = 0; i < p.length; i++) {
			String piece = "";
			if (p[i].startsWith("0")) {
				piece = "0" + Long.parseLong(p[i], 16);
			} else {
				piece = "" + Long.parseLong(p[i], 16);
			}
			// log.info("Long.parseLong(p[" + i + "], 16)=" + p[i] + "/" +
			// Long.parseLong(p[i], 16));
			cad.append(piece);
		}

		//String num = xor2(cad.toString());
		String num = cad.toString();

		BigInteger val = new BigInteger(num);

		BigInteger numero = val.subtract(new BigInteger(String.valueOf(version)));

		num = numero.toString();

		if (!num.endsWith(suma)) {
			throw new Exception("La Licencia no es valida (suma=" + suma + ")");
		}

		String sumaVersion = String.valueOf(Integer.parseInt(suma) + version);

		num = num.substring(0, num.length() - sumaVersion.length());

		String red = String.valueOf(Long.parseLong(mac.replaceAll("-", "").replaceAll(":", ""), 16));

		BigInteger nNum = new BigInteger(num);
		BigInteger nRed = new BigInteger(zeroRight(red, num.length()));
		// if(nRed<nNum) {
		if (nRed.compareTo(nNum) == -1) {
			// num = String.valueOf(Long.parseLong(num) -
			// Long.parseLong(zeroRight(red, num.length())));
			num = new BigInteger(num).subtract(new BigInteger(zeroRight(red, num.length()))).toString();
		} else {
			// num = String.valueOf(Long.parseLong(num) -
			// Long.parseLong(zeroRight(red, num.length()-1)));
			num = new BigInteger(num).subtract(new BigInteger(red.substring(0, num.length() - 1))).toString();
		}

		if (num == null || num.length() != 11) {
			throw new Exception("La Licencia no es valida");
		}

		return num.concat("-").concat(fechaInicio);
	}

	public String toConvertLetter(String cadena, boolean isEncriptar) {
		if(isEncriptar) {
			return cadena.replace("A", "X").replace("B", "M").replace("C", "Z").replace("D", "R").replace("E", "S").replace("F", "W");
		} else {
			return cadena.replace("X", "A").replace("M", "B").replace("Z", "C").replace("R", "D").replace("S", "E").replace("W", "F");
		}
	}

	/*
	public String decifrar(String lic, String empresa) throws IOException, Exception {
		// 250837708152144
		ResourceBundle rb = ResourceBundle.getBundle("LoginBundle");
		String mac = BaseConverterUtil.MacFromBase36(NetworkInfo.getSerialMac());
		String suma = String.valueOf(NetworkInfo.getMacAddressSuma(mac));
		StringBuffer cad = new StringBuffer();
		int version = Integer.parseInt(rb.getString("lic.version").replaceAll("\\.", ""));

		// validamos el nombre de la empresa
		String nombreEmp1 = lic.substring(0, 6).toUpperCase();
		String nombreEmp2 = Encriptor.getMD5(empresa).substring(0, 6).toUpperCase();

		if (!nombreEmp1.equals(nombreEmp2)) {
			throw new Exception("El nombre de la empresa no es valido. Licencia invalida");
		}

		lic = lic.substring(7);

		String[] p = lic.replace("X", "A").replace("Z", "C").split("-");
		for (int i = 0; i < p.length; i++) {
			String piece = "";
			if (p[i].startsWith("0")) {
				piece = "0" + Long.parseLong(p[i], 16);
			} else {
				piece = "" + Long.parseLong(p[i], 16);
			}
			// log.info("Long.parseLong(p[" + i + "], 16)=" + p[i] + "/" +
			// Long.parseLong(p[i], 16));
			cad.append(piece);
		}

		String num = xor2(cad.toString());
		// log.info("cad=" + cad.toString());
		// log.info("num=" + num);
		// log.info("version=" + version);

		BigInteger val = new BigInteger(num);

		BigInteger numero = val.subtract(new BigInteger(String.valueOf(version)));

		num = numero.toString();

		if (!num.endsWith(suma)) {
			throw new Exception("La Licencia no es valida (suma=" + suma + ")");
		}

		String sumaVersion = String.valueOf(Integer.parseInt(suma) + version);

		num = num.substring(0, num.length() - sumaVersion.length());

		String red = String.valueOf(Long.parseLong(mac.replaceAll("-", "").replaceAll(":", ""), 16));

		BigInteger nNum = new BigInteger(num);
		BigInteger nRed = new BigInteger(zeroRight(red, num.length()));
		// if(nRed<nNum) {
		if (nRed.compareTo(nNum) == -1) {
			// num = String.valueOf(Long.parseLong(num) -
			// Long.parseLong(zeroRight(red, num.length())));
			num = new BigInteger(num).subtract(new BigInteger(zeroRight(red, num.length()))).toString();
		} else {
			// num = String.valueOf(Long.parseLong(num) -
			// Long.parseLong(zeroRight(red, num.length()-1)));
			num = new BigInteger(num).subtract(new BigInteger(red.substring(0, num.length() - 1))).toString();
		}

		if (num == null || num.length() != 11) {
			throw new Exception("La Licencia no es valida");
		}

		return num;
	}
	*/

	/**
	 * 
	 * @param cad
	 * @param largo
	 * @return
	 */
	private String zeroRight(String cad, int largo) {
		if (cad.length() < largo) {
			cad = cad.concat("0");
			cad = zero(cad, largo);
		}
		return cad;
	}

	/**
	 * 
	 * @param cad
	 * @return
	 */
	public String xor2(String cad) {
		int nCad = cad.length();
		byte[] valor = new byte[cad.length()/2];
		char[] letra = cad.toCharArray();
		StringBuffer ret = new StringBuffer();
		
		String a=null;
		String b=null;
		for(int i=0; i<nCad;i++) {
			if(a==null) {
				a = String.valueOf(letra[i]);
			} else if (b==null) {
				b = String.valueOf(letra[i]);
				String c = a+b;
				ret.append((char)(Integer.parseInt(c)^33));
				a=null;
				b=null;
			}
			
		}
		
		return ret.toString();
	}
	

	/**
	 * 
	 * @param numero
	 * @return
	 */
	public String toHex(String numero) {
		StringBuffer cad = new StringBuffer();
		StringBuffer ret = new StringBuffer();
		char[] det = numero.toCharArray();
		String sep = "";
		int n = 0;
		// ret.append(Long.toString(Long.parseLong(numero), 16).toUpperCase());

		// partimos el numero
		int mitad = numero.length() / 5;
		String p1 = numero.substring(0, mitad);
		String p2 = numero.substring(mitad, mitad * 2);
		String p3 = numero.substring((mitad * 2), mitad * 3);
		String p4 = numero.substring((mitad * 3), mitad * 4);
		String p5 = numero.substring((mitad * 4));

		ret.append(Long.toString(Long.parseLong(p1), 16).toUpperCase());
		ret.append("-");
		ret.append(Long.toString(Long.parseLong(p2), 16).toUpperCase());
		ret.append("-");
		ret.append(Long.toString(Long.parseLong(p3), 16).toUpperCase());
		ret.append("-");
		ret.append(Long.toString(Long.parseLong(p4), 16).toUpperCase());
		ret.append("-");
		ret.append(Long.toString(Long.parseLong(p5), 16).toUpperCase());

		return ret.toString();
	}

	/**
	 * 
	 * @return
	 */
	public static ModuloBean validarLicencia() {
		ToolsHTML tool = new ToolsHTML();
		ModuloBean mod = new ModuloBean();

		File f = new File(getPathKey());
		if (true || f.exists()) {
			try {
				if (f.exists()) {
					Archivo arc = new Archivo();
					String key = arc.leer(getPathKey()).toString();
					String[] keys = key.split("\\|");

					mod.setEmpresa(keys[0]);
					mod.setRif(keys[1]);
					mod.setMac(keys[2]);
					mod.setLicencia(keys[3]);
					mod.load(tool.decifrar(keys[3], keys[0]));
				}

				boolean isLite = mod.getEdicion().equals(ModuloBean.EDICION_LITE);
				boolean isMic = mod.getEdicion().equals(ModuloBean.EDICION_MICROEMPRESA);
				boolean isPym = mod.getEdicion().equals(ModuloBean.EDICION_PYME);
				boolean isPro = mod.getEdicion().equals(ModuloBean.EDICION_PROFESIONAL);
				boolean isGob = mod.getEdicion().equals(ModuloBean.EDICION_E_GOB);
				
				ModuloBean.IS_EDICION_LITE = isLite;
				ModuloBean.IS_EDICION_MICROEMPRESA = isMic;
				ModuloBean.IS_EDICION_PYME = isPym;
				ModuloBean.IS_EDICION_PROFESIONAL = isPro;
				ModuloBean.IS_EDICION_E_GOB = isGob;
				

				// "Estadisticas","Flujos de Trabajo Avanzado","Expedientes","SACOPs","Digitalizar"};
				// // de conformocionTO
				boolean isModRec = mod.getModulo().equals("01");
				boolean isModFTP = mod.getModulo().equals("02");
				boolean isModExp = mod.getModulo().equals("03");
				boolean isModSacop = mod.getModulo().equals("04");
				;
				boolean isModDig = mod.getModulo().equals("05");
				

				// activamos o desactivamos los modulos segun la edicion
				getServletContext().setAttribute("enableFlujo", new Boolean(isLite || isMic || isPym || isPro || isGob));
				getServletContext().setAttribute("enableFTP", new Boolean(isPym || isPro || isGob || isModFTP));
				getServletContext().setAttribute("enableSACOP", new Boolean(isPro || isGob || isModSacop));
				getServletContext().setAttribute("enableInTouch", new Boolean(false));
				getServletContext().setAttribute("enableRecord", false); //new Boolean(isPym || isPro || isGob || isModRec));
				getServletContext().setAttribute("enableFiles", new Boolean(isPro || isGob || isModExp));
				getServletContext().setAttribute("enableDigital", false); // new Boolean(isPro || isGob || isModDig));

				getServletContext().setAttribute("enableAdmin", new Boolean(isLite || isMic || isPym || isPro || isGob));
				getServletContext().setAttribute("enableActiveDirectory", new Boolean(isMic || isPym || isPro || isGob));
				getServletContext().setAttribute("enableCreateRegister", new Boolean(isLite || isMic || isPym || isPro || isGob));
				getServletContext().setAttribute("enableDistributionList", new Boolean(isLite || isMic || isPym || isPro || isGob)); // se coloco para todas
				getServletContext().setAttribute("enableConcurrentUser", new Boolean(isMic || isPym || isPro || isGob));
			} catch (IOException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {

		}

		return mod;
	}

	/**
	 * 
	 * @param asunto
	 * @return
	 */
	public static String getSufijoMail(String asunto) {
		if (asunto != null) {
			String post = HandlerParameters.PARAMETROS.getPostfixMail();
			if (post != null && !post.trim().equals("")) {
				StringBuilder sb = new StringBuilder(asunto).append(" - ").append(post);
				asunto = sb.toString();
			}
		} else {
			asunto = "";
		}
		return asunto;
	}

	/**
	 * Se busca la imagen indicada en el parametro elementPath del proyecto, encaso de existir se retorna el tag HTML para esa imagen sino, se retorna un Stirng
	 * vacio.
	 * 
	 * @param request
	 * @param elementPath
	 * @return
	 */
	public static String getHTMLImgElementIfExists(HttpServletRequest request, String elementPath) {
		String htmlImgTag = "<img border=\"0\" alt=\"" + elementPath + "\"" + " src=\"" + elementPath + "\"" + " />";

		String basePath = getPath().concat(elementPath);

		if (new File(basePath).exists()) {
			return htmlImgTag;
		} else {
			return "<!-- Este elemento no existe: " + htmlImgTag + "-->";
		}

	}

	/**
	 * 
	 * @param name
	 * @return
	 */
	public static String getExtension(String name) {
		if (name != null && name.indexOf(".") != -1) {
			String[] part = name.split("\\.");
			return part[part.length - 1];
		} else {
			return "";
		}
	}

	/**
	 * 
	 * @param forma
	 * @param securityForGroup
	 * @return
	 */
	public static boolean userCanSeeSacop(SeguridadUserForm forma, SeguridadUserForm securityForGroup) {
		boolean canSee = false;

		log.info("forma.getSacop(): " + forma.getSacop());
		log.info("securityForGroup.getSacop(): " + securityForGroup.getSacop());

		if (forma.getSacop() == 0) {
			canSee = true;
		} else {
			if (forma.getSacop() == 2) {
				if (securityForGroup.getSacop() == 0) {
					canSee = true;
				}
			}
		}

		return canSee;
	}

	/**
	 * 
	 * @param forma
	 * @param securityForGroup
	 * @return
	 */
	public static boolean userCanSeeFlujos(SeguridadUserForm forma, SeguridadUserForm securityForGroup) {
		boolean canSee = false;

		log.info("forma.getFlujos(): " + forma.getFlujos());
		log.info("securityForGroup.getFlujos(): " + securityForGroup.getFlujos());

		if (forma.getFlujos() == 0) {
			canSee = true;
		} else {
			if (forma.getFlujos() == 2) {
				if (securityForGroup.getFlujos() == 0) {
					canSee = true;
				}
			}
		}

		return canSee;
	}

	/**
	 * Metodo para saber si determinada extension es considerada como doble version en el sistema.
	 * 
	 * @param fileExt
	 *            (puede ser la extension con punto o sin punto al inicio)
	 * 
	 * @return
	 */
	public static boolean extensionIsDobleVersion(String fileExt) {
		return extensionIsDobleVersion(null, fileExt);
		
	}
	public static boolean extensionIsDobleVersion(Connection con, String fileExt) {
		boolean isDobleVersion = false;

		// verificamos si la extension del archivo principal es doble version o
		// no
		try {
			if(ModuloBean.IS_EDICION_LITE) {
				return true; // edicion lite todo es doble version
			}
			
			
			String extensions = HandlerParameters.PARAMETROS.getFileDoubleVersion();
			if (!fileExt.startsWith(".")) {
				fileExt = "." + fileExt;
			}

			if (!isEmptyOrNull(extensions) && (extensions.contains(fileExt))) {
				// la extension del archivo representa una de doble version
				isDobleVersion = true;
			}
		} catch (Exception e) {
			// TODO: handle exception
		}

		log.info("Extension '" + fileExt + "' verificada como doble version = " + isDobleVersion);
		return isDobleVersion;
	}
	
	public static String getRegisterClassActionRecommended(String idRegisterClass) {
		return getRegisterClassActionRecommended(Integer.parseInt(idRegisterClass));
	}
	public static String getRegisterClassActionRecommended(int idRegisterClass) {
		if(Constants.registerclassTable==null) {
			// cargamos la tabla
			//Constants.registerclassTable = new HashMap<Integer, String>();
			RegisterClassDAO.recargarRegisterClass();
		}
		
		return Constants.registerclassTable.get(idRegisterClass);
	}
	
	public static String getActionType(String actionType) {
		
		if(actionType.equals("SAC")) {
			return "Accion Correctiva";
		} else if(actionType.equals("SCR")) {
			return "Accion Correctora";
		} else if(actionType.equals("SAP")) {
			return "Accion Correctora";
		} else {
			return "";
		}
	}
	
	public static String getTimestampConvert(String dateSystem) {
		StringBuffer sql = new StringBuffer();
		
		switch (Constants.MANEJADOR_ACTUAL) {
		case Constants.MANEJADOR_MSSQL:
			sql.append("CONVERT(datetime,'").append(dateSystem)
					.append("',120) ");
			break;
		case Constants.MANEJADOR_POSTGRES:
			sql.append("CAST('").append(dateSystem)
					.append("' AS timestamp)  ");
			break;
		case Constants.MANEJADOR_MYSQL:
			sql.append("TIMESTAMP('").append(dateSystem).append("') ");
			break;
		}
		
		return sql.toString();
	}
	
	public static boolean isFoundValueIntoString(String cadena, String valor) {
		String[] arr = cadena.split(",");
		for(int i=0; i<arr.length; i++) {
			if(arr[i].equals(valor)) {
				return true;
			}
		}
		return false;
	}
	
    public static String replaceAcentos(String input) {
        // Cadena de caracteres original a sustituir.
        String original = "���������������������������������";
        // Cadena de caracteres ASCII que reemplazar�n los originales.
        String ascii = "aaaeeeiiiooouuunAAAEEEIIIOOOUUUNcC";
        String[] letras = new String[]{"&aacute;","&aacute;","&aacute;","&eacute;","&eacute;","&eacute;","&iacute;","&iacute;","&iacute;","&oacute;","&oacute;","&oacute;","&uacute;","&uacute;","&ntilde;","&Aacute;","&Aacute;","&Aacute;","&Eacute;","&Eacute;","&Eacute;","&Iacute;","&Iacute;","&Iacute;","&Oacute;","&Oacute;","&Oacute;","&Uacute;","&Uacute;","&Uacute;","&Ntilde;","c","C"};
        
        String output = input;
        for (int i=0; i<original.length(); i++) {
            // Reemplazamos los caracteres especiales.
            output = output.replaceAll(String.valueOf(original.charAt(i)), letras[i]);
            System.out.println(output);
        }//for i
        return output;
    }    

    public static Iterator<Search> sortedIteratorSearch(Iterator<Search> it, Comparator<Search> comparator) {
    	
    	List list = new ArrayList();
        while (it.hasNext()) {
            list.add(it.next());
        }

        Collections.sort(list, comparator);
        return list.iterator();
    }
    
    public static Usuario getUserToken(HttpServletRequest request) throws Exception {
		String token = request.getHeader("Authorization");
		if(!Constants.TOKEN_USUARIOS_ONDEMAND.containsKey(token)) {
			throw new Exception("token not valid!");
		}
		return Constants.TOKEN_USUARIOS_ONDEMAND.get(token);
    }

    public static String getBody(HttpServletRequest request) throws IOException {

    	   String body = null;
    	   StringBuilder stringBuilder = new StringBuilder();
    	   BufferedReader bufferedReader = null;

    	   try {
    	       InputStream inputStream = request.getInputStream();
    	       if (inputStream != null) {
    	           bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
    	           char[] charBuffer = new char[128];
    	           int bytesRead = -1;
    	           while ((bytesRead = bufferedReader.read(charBuffer)) > 0) {
    	               stringBuilder.append(charBuffer, 0, bytesRead);
    	           }
    	       } else {
    	           stringBuilder.append("");
    	       }
    	   } catch (IOException ex) {
    	       throw ex;
    	   } finally {
    	       if (bufferedReader != null) {
    	           try {
    	               bufferedReader.close();
    	           } catch (IOException ex) {
    	               throw ex;
    	           }
    	       }
    	   }

    	   body = stringBuilder.toString();
    	   return body;
    }
    
    public static void sendResponse(HttpServletResponse response, int codeStatus, String object) {
    	PrintWriter out;
		try {
			out = response.getWriter();
			response.setStatus(codeStatus);
			response.setCharacterEncoding("UTF-8");
			response.setContentType("application/json");
			if(object!=null) {
				out.write(object);
			}
			out.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
    }


    public static Date removeTime(Date date) {    
        Calendar cal = Calendar.getInstance();  
        cal.setTime(date);  
        cal.set(Calendar.HOUR_OF_DAY, 0);  
        cal.set(Calendar.MINUTE, 0);  
        cal.set(Calendar.SECOND, 0);  
        cal.set(Calendar.MILLISECOND, 0);  
        return cal.getTime(); 
    }
    
    public static boolean showPDF (Users usuario) {
    	//TODO: listo usuario es de administarddor   	
    	boolean result = false;
       	if (usuario.getIdGroup().equals("2")) {
    		// Listo la  VALIDACION CORRECTA
			result = true;
		}

       	return result;
    	
    }
    
    /**
	 *  metodos determina si es sacop Padre
	 * @return String SI o NO
	 */
	public static String isSacopPadre(String sacopnum) {
		String respuesta = "NO";
		int ret =0;
		try {
			CachedRowSet crs = JDBCUtil.executeQuery(new StringBuffer("select count(numberTrackingSacop) from tbl_planillasacop1 where  estado='7' AND numberTrackingSacop=").append(sacopnum), Thread.currentThread().getStackTrace()[1].getMethodName());
			if (crs.next()) {
				ret = crs.
						getInt(1);
				respuesta=ret>0?"SI":"NO";
			}
		} catch (Exception e) {
			e.printStackTrace();
			ret = -1;
		}
		return respuesta;
	}
	

	/**
	 * Metodo para saber si determinada extension es considerada como una extesion no editable en el sistema.
	 * 
	 * @param fileExt
	 *            (puede ser la extension con punto o sin punto al inicio)
	 * 
	 * @return
	 */
	public static boolean extensionIsNoEditable(String fileExt) {
		return extensionIsNoEditable(null, fileExt);
		
	}
	public static boolean extensionIsNoEditable(Connection con, String fileExt) {
		boolean isNoEditable = false;

		// verificamos si la extension del archivo es no editable o
		// no
		try {
			
			
			String extensions = HandlerParameters.PARAMETROS.getFileUploadVersion();
			if (!fileExt.startsWith(".")) {
				fileExt = "." + fileExt;
			}

			if (!isEmptyOrNull(extensions) && (extensions.contains(fileExt))) {
				// la extension del archivo representa una no editable
				isNoEditable = true;
			}
		} catch (Exception e) {
			// TODO: handle exception
		}

		log.info("Extension '" + fileExt + "' verificada no editable = " + isNoEditable);
		return isNoEditable;
	}
	
	/**
	 * Metodo para saber si determinada extension es considerada como una extesion visor nativo en el sistema.
	 * 
	 * @param fileExt
	 *            (puede ser la extension con punto o sin punto al inicio)
	 * 
	 * @return
	 */
	public static boolean extensionIsNativeViewer(String fileExt) {
		return extensionIsNativeViewer(null, fileExt);
		
	}
	public static boolean extensionIsNativeViewer(Connection con, String fileExt) {
		boolean isNativeViewer = false;

		// verificamos si la extension del archivo es extesion visor natiavo o
		// no
		try {
			
			
			String extensions = HandlerParameters.PARAMETROS.getFileNativeViewer();
			if (!fileExt.startsWith(".")) {
				fileExt = "." + fileExt;
			}

			if (!isEmptyOrNull(extensions) && (extensions.contains(fileExt))) {
				// la extension del archivo representa version nativa
				isNativeViewer = true;
			}
		} catch (Exception e) {
			// TODO: handle exception
		}

		log.info("Extension '" + fileExt + "' verificada visor nativa  = " + isNativeViewer);
		return isNativeViewer;
	}
	
	/**
	 * 
	 * @param name
	 * @return
	 */
	public static String pdfSuprimir(String version) {
		
		String res ="no existe Pdf";
		int versionN = Integer.parseInt(version);
			log.info("Begin..." );
		
		try {
			String nameFileCache = Archivo.getNameFileEncripted("versiondocview",versionN,null);
			File cache = new File(nameFileCache);
			boolean existeCache = cache.exists();
			if(existeCache) {
				cache.delete();
				res= "Archivo de Visualizacion Pdf Eliminado! ";
			} else {
				res= "Archivo PDF No existe!!! ";
			}
				//existeCache = false;
					
		} catch (NoSuchAlgorithmException | ErrorDeAplicaqcion e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		log.info("end ....." );
		return res;
		
	}
	
	
}