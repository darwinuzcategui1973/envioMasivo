package com.desige.webDocuments.document.actions;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.ResourceBundle;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.desige.webDocuments.dao.ConfExpedienteDAO;
import com.desige.webDocuments.dao.ExpedienteDAO;
import com.desige.webDocuments.dao.ExpedienteHistoryDAO;
import com.desige.webDocuments.files.forms.ExpedienteForm;
import com.desige.webDocuments.files.forms.FilesForm;
import com.desige.webDocuments.persistent.managers.HandlerParameters;
import com.desige.webDocuments.persistent.managers.PdfConvert;
import com.desige.webDocuments.utils.DesigeConf;
import com.desige.webDocuments.utils.ToolsHTML;
import com.desige.webDocuments.utils.beans.SuperAction;
import com.desige.webDocuments.utils.beans.Users;
import com.focus.util.Archivo;

/**
 * Title: FilesLoadData.java <br/> Copyright: (c) 2004 Focus Consulting C.A.<br/>
 * 
 * @author Ing. Nelson Crespo
 * @version WebDocuments v3.0 <br/> Changes:<br/>
 *          <ul>
 *          <li> 15/02/2005 (NC) Creation </li>
 *          </ul>
 */
public class FilesLoadData extends SuperAction {
	
	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, 
			HttpServletResponse response) {
		super.init(mapping, form, request, response);
		ExpedienteForm oExpedienteForm = new ExpedienteForm();
		ExpedienteDAO oExpedienteDAO = new ExpedienteDAO();
		ExpedienteHistoryDAO oExpedienteHistoryDAO = new ExpedienteHistoryDAO();
		
		try {
			Users usuario = (Users) getSessionObject("user");
			boolean swAdmin = false;

			try {
				swAdmin = DesigeConf.getProperty("application.admon").equalsIgnoreCase(usuario.getIdGroup());
			} catch (java.lang.NullPointerException e) {
				swAdmin = "2".equalsIgnoreCase(usuario.getIdGroup());
			}

			oExpedienteForm.setF1(Integer.parseInt(getParameter("idDocument")));
			oExpedienteForm.setNumVer(Integer.parseInt(getParameter("idVersion")));
			oExpedienteForm.setFilesVersion(oExpedienteForm.getNumVer());
			
			request.getSession().removeAttribute("numberCopies");			
			if (!ToolsHTML.isEmptyOrNull(getParameter("imprimir")) && ((String) getParameter("imprimir")).equals("true")) {
				oExpedienteForm.setPrinting(true);
				request.getSession().setAttribute("numberCopies","1");			
			}
			
			if(oExpedienteForm.getFilesVersion()==0) {
				oExpedienteDAO.findById(oExpedienteForm);
			} else {
				oExpedienteHistoryDAO.findById(oExpedienteForm);
			}

			//ToolsHTML tools = new ToolsHTML();
			//PermissionUserForm perm = new PermissionUserForm(); 
			//perm = tools.getSecurityUserInDoc(usuario,getParameter("idDocument"),"1");

			//if ((perm != null && perm.getToViewDocs() == Constants.permission) || swAdmin) {
			if (true) {
				// para usarlo en la lista de impresion en topdocument.jsp
				putObjectSession("idDocument", oExpedienteForm.getF1());
				putObjectSession("idUser", String.valueOf(usuario.getIdPerson()));

				// Si es el Dueño del Documento tiene libre impresión sobre el
				// Mismo
				boolean isAdmon = DesigeConf.getProperty("application.userAdmon").compareTo(usuario.getUser()) == 0;

				// Luis Cisneros
				// 24-03-07
				// La posibilidad de que el admin y el dueno del documento
				// impriman sin
				// someter a un flujo, es conrolado por un parametro en la
				// configuracion.

				boolean printOwnerAdmin = String.valueOf(HandlerParameters.PARAMETROS.getPrintOwnerAdmin()).equals("0");

				// Si es el Admin, es el propietario del Documento o tiene
				// permiso de Libre
				// Impresión se le asigna la Misma y el documento no es borrador
				// if (( (isAdmon || oExpedienteForm != null) &&
				// printOwnerAdmin) || (perm != null && perm.getToImpresion() ==
				// Constants.permission)) {
				// PENDIENTE: aqui se colocar un metodo para comprobar si el
				// expediente es imprimible
				// la funcion verificara que todos los documentos a imprimir
				// esten aprobados
				// boolean isPrintable = false;
				// //HandlerDocuments.isDocumentStatuVersionPrintable(Integer.parseInt(forma.getIdDocument()),forma.getNumVer());
				// if (isPrintable) {
				// oExpedienteForm.setPrinting(true);
				// putObjectSession("printFree", "true");
				// }
				// }
				// FIN 24-03-07

				// ini apertura de archivos en pdf
				String path = ToolsHTML.getPath();
				String pathFileHtml = path.concat("expediente.html");
				String nameFileOutput = path.concat("tmp").concat(File.separator).concat("files_").concat(String.valueOf(oExpedienteForm.getF1())).concat(".html");
				String servidor = request.getScheme() + "://" + ToolsHTML.getServerName(request) + ":" + request.getServerPort() + request.getContextPath() + "/";
				File fileHtml = new File(pathFileHtml);

				if (fileHtml.exists()) {
					// HandlerDocuments.isPrinting(usuario, forma);
					if (oExpedienteForm.isPrinting()) {
						ResourceBundle rb = ToolsHTML.getBundle(request);

						Archivo arc = new Archivo();
						String data = arc.leer(pathFileHtml).toString();
						String dataDetalle = "";
						String dataRevisores = "";
						String dataCreador = "";

						// cambio de datos a las etiquetas
						data = data.replaceAll("---lblCODIGO---", rb.getString("doc.number"));
						data = data.replaceAll("---lblVERSION---", rb.getString("doc.Ver"));
						data = data.replaceAll("---lblFECHA_DE_CREACION---", rb.getString("doc.dateCreation"));
						data = data.replaceAll("---lblSOLICITADO---", rb.getString("imp.solicitante"));
						data = data.replaceAll("---lblIMPRESO---", rb.getString("imp.datePrint"));
						
						data = data.replaceAll("---lblINFORMATION---", rb.getString("files.information"));
	
						// cambio de datos a la pagina
						data = data.replaceAll("---SERVIDOR---", servidor);
						data = data.replaceAll("---ENCABEZADO1---", rb.getString("files.files"));
						data = data.replaceAll("---CODIGO---", String.valueOf(oExpedienteForm.getF1()));
						data = data.replaceAll("---VERSION---", String.valueOf(oExpedienteForm.getNumVer()));
						data = data.replaceAll("---FECHA_DE_CREACION---", oExpedienteForm.getFecha());
						data = data.replaceAll("---SOLICITADO---", usuario.getNamePerson());
						data = data.replaceAll("---IMPRESO---", ToolsHTML.sdfShow.format(new java.util.Date()));
	
						
						FilesForm files = new FilesForm();
						ConfExpedienteDAO conf = new ConfExpedienteDAO();
						ArrayList campos = (ArrayList)conf.findAllByOrder();
	
						dataDetalle = data.substring(data.indexOf("<!--etiqueta1A-->"), data.indexOf("<!--etiqueta1B-->"));
						data = data.substring(0, data.indexOf("<!--etiqueta1A-->")).concat(data.substring(data.indexOf("<!--etiqueta1B-->")));
						
						data = cargarExpediente(data,campos,oExpedienteForm,dataDetalle,"<!--etiqueta1B-->",new String[] { "---ETIQUETA---", "---VALOR---" });
	
						// cargar ficha del expediente
						// data = cargarFirmantesRevisores(data, firmas,
						// impNameCharge, dataDetalle, "<!--etiqueta3_1B-->", new
						// String[] { "---DET_NOMBRE_USUARIO---",
						// "---DET_FECHA_FIRMA---" });
	
						arc.escribir(nameFileOutput, data);
	
						PdfConvert pdf = new PdfConvert();
						pdf.saveToPdf(nameFileOutput);
	
						File filenameFileOutput = new File(nameFileOutput);
						Archivo.delete(filenameFileOutput);
					}

				}
				// fin apertura de archivos en pdf

				putObjectSession("expediente", oExpedienteForm);
				StringBuffer item = new StringBuffer("/viewDocument.jsp?nameFile=").append(getParameter("nameFile"));
				item.append("&idDocument=").append(getParameter("idDocument"));
				item.append("&idVersion=").append(getParameter("idVersion"));
				// SIMON 25 DE JULIO 2005 INICIO
				item.append("&imprimir=").append(getParameter("imprimir"));
				item.append("&swAdmin=").append(swAdmin);
				item.append("&showFile=showExpediente.jsp");

				// SIMON 25 DE JULIO 2005 FIN

				ActionForward nextPage = new ActionForward(item.toString(), false);

				// end
				// Se chequea si el usuario está Logueado en el sistema.....
				// en caso contrario se almacena la dirección de petición
				// y se redirige el usuario a la página inicial del Sistema
				if (usuario == null) {
					putObjectSession("optionReturn", nextPage);
					return goTo("login");
				}

				request.setAttribute("filesPreview", true);

				return nextPage;
			} else {
				return goError("sch.notDocsView");
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			goError("sch.notDocsView");
		}
		return goError("E0052");
	}

	private String cargarExpediente(String data, Collection datos, ExpedienteForm file, String dataDetalle, String bloque, String[] etiquetas) throws IllegalArgumentException, SecurityException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		StringBuffer firmantes = new StringBuffer();
		String detalle = null;
		FilesForm campo = null;
		if (datos != null) {
			Iterator ite = datos.iterator();
			while (ite.hasNext()) {
				campo = (FilesForm) ite.next();
				if("f1_f2".indexOf(campo.getId())==-1 && campo.getImprimir()==1) {
					detalle = dataDetalle;
					detalle = detalle.replaceAll(etiquetas[0], campo.getEtiqueta02() );
					detalle = detalle.replaceAll(etiquetas[1], String.valueOf(file.get(campo.getId().toUpperCase())));
					firmantes.append(detalle);
				}
			}
			data = data.replaceAll(bloque, firmantes.toString());
		}
		return data;
	}

}
