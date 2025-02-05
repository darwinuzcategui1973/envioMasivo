package com.desige.webDocuments.document.actions;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.ResourceBundle;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.desige.webDocuments.dao.PreviewDAO;
import com.desige.webDocuments.document.forms.BaseDocumentForm;
import com.desige.webDocuments.document.forms.BeanFirmsDoc;
import com.desige.webDocuments.document.forms.BeanRazonCambioDoc;
import com.desige.webDocuments.persistent.managers.HandlerBD;
import com.desige.webDocuments.persistent.managers.HandlerDBUser;
import com.desige.webDocuments.persistent.managers.HandlerDocuments;
import com.desige.webDocuments.persistent.managers.HandlerParameters;
import com.desige.webDocuments.persistent.managers.HandlerSacop;
import com.desige.webDocuments.persistent.managers.HandlerStruct;
import com.desige.webDocuments.persistent.managers.HandlerVisor;
import com.desige.webDocuments.seguridad.forms.PermissionUserForm;
import com.desige.webDocuments.typeDocuments.actions.UpdateTypeDocumentsAction;
import com.desige.webDocuments.utils.Constants;
import com.desige.webDocuments.utils.DesigeConf;
import com.desige.webDocuments.utils.ToolsHTML;
import com.desige.webDocuments.utils.beans.SuperAction;
import com.desige.webDocuments.utils.beans.Users;
import com.focus.util.Archivo;

/**
 * Title: LoadDataDocument.java <br/> Copyright: (c) 2004 Focus Consulting C.A.<br/>
 * 
 * @author Ing. Nelson Crespo
 * @version WebDocuments v3.0 <br/> Changes:<br/>
 *          <ul>
 *          <li> 15/02/2005 (NC) Creation </li>
 *          </ul>
 */
public class LoadDataDocument extends SuperAction {
	private static Logger logger = LoggerFactory.getLogger(LoadDataDocument.class);
	
	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
		super.init(mapping, form, request, response);

		BaseDocumentForm forma = new BaseDocumentForm();
		forma.setIdDocument(request.getParameter("idDocument"));
		forma.setNumberGen(request.getParameter("idDocument"));
		forma.setNumVer(Integer.parseInt(request.getParameter("idVersion")));
		int numberCopies = 0;
		int imprimir = 1 ;
		
		if (request.getParameter("imprimir")  !=null) {
			imprimir = Integer.parseInt( request.getParameter("imprimir") );
			}
		
		boolean isVisorNativo = false;
		// solo va ser si viene de solictud de impresion 
		String respsolimpres = "";
		if (5==imprimir) respsolimpres = HandlerParameters.PARAMETROS.getrespsolimpres();
		boolean isRespsolimpres = (respsolimpres!=null && !respsolimpres.trim().equals(""));
		try {
			Users usuario = (Users) getSessionObject("user");
			
			if(usuario == null){
				//no se encontro informacion del usuario en session
				//debemos redirigir esta peticion a la pagina de login
				//indicando el parametro url
				getSession().setAttribute("url",
						request.getServletPath() + "?" + request.getQueryString()
						+ Constants.PARAMETER_ALLOW_SESSION_CONNECTION
						+ Constants.OPEN_URL_PETITION_IN_A_NEW_WINDOW);
				
				return mapping.findForward("forcedLoginPage");
			}
			
			boolean swAdmin = false;
			try {
				swAdmin = DesigeConf.getProperty("application.admon").equalsIgnoreCase(usuario.getIdGroup());
			} catch (java.lang.NullPointerException e) {
				swAdmin = "2".equalsIgnoreCase(usuario.getIdGroup());
			}
			// Hashtable tree = (Hashtable)getSessionObject("tree");
			// tree = ToolsHTML.checkTree(tree,usuario);
			// HandlerStruct.loadDocument(forma,getParameter("downFile")!=null,false,tree);
			HandlerStruct.loadDocument(forma, request.getParameter("downFile") != null, false, null, request);
			
			// pedimos las extensiones que seran visualizadas en el navegador
			try {
				isVisorNativo = HandlerParameters.isExtensionNativeViewer(null,forma.getNameFile());
				//System.out.println(" en LoadDocumnet Nativo-----///////------"+isVisorNativo+" ------- ");
			}catch(Exception e) {
				System.out.println("ERROR: NO SE PUDO CONSULTAR SI ES UNA EXTENSION NATIVA");
				e.printStackTrace();
			}


			forma.setIdUser((int)usuario.getIdPerson());
			// Se Procede a Verificar los Permisos sobre el Documento
			// PermissionUserForm perm =
			// HandlerGrupo.getSecurityForIDGroupInDoc(usuario.getIdGroup(),forma.getIdDocument());
			ToolsHTML tools = new ToolsHTML();
			PermissionUserForm perm = tools.getSecurityUserInDoc(usuario, forma.getIdDocument(), forma.getIdNode());

			if ((perm != null && perm.getToViewDocs() == Constants.permission) || swAdmin) {
				// para usarlo en la lista de impresion en topdocument.jsp
				putObjectSession("idDocument", forma.getIdDocument());
				putObjectSession("idUser", String.valueOf(usuario.getIdPerson()));

				// Si es el Due�o del Documento tiene libre impresi�n sobre el
				// Mismo
				boolean isAdmon = DesigeConf.getProperty("application.userAdmon").compareTo(usuario.getUser()) == 0;

				// Luis Cisneros
				// 24-03-07
				// La posibilidad de que el admin y el dueno del documento
				// impriman sin
				// someter a un flujo, es conrolado por un parametro en la
				// configuracion.

				// Si es el Admin, es el propietario del Documento o tiene
				// permiso de Libre
				// Impresi�n se le asigna la Misma
				// if (isAdmon || (forma!=null &&
				// (forma.getOwner().trim().compareTo(usuario.getUser())==0))||

				boolean printOwnerAdmin = String.valueOf(HandlerParameters.PARAMETROS.getPrintOwnerAdmin()).equals("0");

				// Si es el Admin, es el propietario del Documento o tiene
				// permiso de Libre
				// Impresi�n se le asigna la Misma y el documento no es borrador
				forma.setPrinting(false);
				if (((isAdmon || (forma != null && forma.getOwner()!=null && usuario!=null && usuario.getUser()!=null && (forma.getOwner().trim().compareTo(usuario.getUser()) == 0))) && printOwnerAdmin) || (perm != null && perm.getToImpresion() == Constants.permission)) {
					boolean isPrintable = HandlerDocuments.isDocumentStatuVersionPrintable(Integer.parseInt(forma.getIdDocument()), forma.getNumVer());
					if (isPrintable) {
						forma.setPrinting(true);
						putObjectSession("printFree", "true");
					}
				}
				// FIN 24-03-07
				
				forma.setCopyContents(false);
				if((request.getParameter("flujo")!=null && request.getParameter("flujo").equals("true")) && (request.getParameter("copyContents")==null || request.getParameter("copyContents").equals("true"))) {
					// preguntamos si esta en flujo de trabajo
					boolean isCopyContents = HandlerDocuments.isDocumentStatuVersionCopyContentsWF(Integer.parseInt(forma.getIdDocument()), forma.getNumVer(), usuario.getUser());
					if(!isCopyContents) {
						// preguntamos si esta en flujo de trabajo parametrico
						isCopyContents = HandlerDocuments.isDocumentStatuVersionCopyContentsFlexWF(Integer.parseInt(forma.getIdDocument()), forma.getNumVer(), usuario.getUser());
					}
					if (isCopyContents) {
						//System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!SI SE PUEDE COPIAR");
						forma.setCopyContents(true);
					}
				}
								

				// ini apertura de archivos en pdf
				logger.info("Constants.PRINTER_PDF = " + Constants.PRINTER_PDF);
				if (Constants.PRINTER_PDF) {
					HandlerDocuments.isPrinting(usuario, forma);
					logger.info("Permiso de Impresión:.. " + forma.isPrinting());
					//if (forma.isPrinting()) {
						String path = ToolsHTML.getPath();
						String pathFileHtml = path.concat("inicio.html");
						String nameFileOutput = path.concat("tmp").concat(File.separator).concat("pag").concat(String.valueOf(forma.getIdUser())).concat("_").concat(forma.getIdDocument()).concat("_").concat(String.valueOf(forma.getNumVer())).concat(".html");
						String servidor = request.getScheme() + "://" + ToolsHTML.getServerName(request) + ":" + request.getServerPort() + request.getContextPath() + "/";
						File fileHtml = new File(pathFileHtml);

						PdfPortada pdf = new PdfPortada();
						String nameFilePdf = path.concat("tmp").concat(File.separator).concat("pag").concat(String.valueOf(forma.getIdUser())).concat("_").concat(forma.getIdDocument()).concat("_").concat(String.valueOf(forma.getNumVer())).concat(".pdf");

						/*
						// Si es la extension para ser visualizada en el navegador
						// se coloca la extension original del archivo
						if(isVisorNativo) {
							nameFilePdf = path.concat("tmp").concat(File.separator).concat("pag").concat(String.valueOf(forma.getIdUser())).concat("_").concat(forma.getIdDocument()).concat("_").concat(String.valueOf(forma.getNumVer())).concat(".mp4");
						}
						*/
						
						if (fileHtml.exists()) {
						//if (!fileHtml.exists()) {	
							ResourceBundle rb = ToolsHTML.getBundle(request);

							Archivo arc = new Archivo();
							String data = arc.leer(pathFileHtml).toString();
							String dataDetalle = "";
							String dataRevisores = "";
							String dataColaboradores = "";
							String dataCreador = "";
							String[] aux = null;
							String sacopNumero="";
							if(	forma.getNameDocument().indexOf(":")!=-1){
								aux =forma.getNameDocument().toString().split(":");
								}
							if (aux != null){
								sacopNumero=aux[1];
							}
							// System.out.println(sacopNumero);

							numberCopies = 1;
							try {
								numberCopies =loadsolicitudImpresion.getNumberOfCopiesToPrint(forma.getIdDocument(), String.valueOf(usuario.getIdPerson()));
								if (isRespsolimpres) {
									numberCopies =loadsolicitudImpresion.getNumberOfCopiesToPrintAutorizante(forma.getIdDocument(), String.valueOf(usuario.getIdPerson()));
								}
							} catch (Exception e) {
								e.printStackTrace();
							}

							String comments = "";
							try {
								comments = String.valueOf(loadsolicitudImpresion.getCommentsToPrint(forma.getIdDocument(), String.valueOf(usuario.getIdPerson()))).trim();
								if (isRespsolimpres) {
									comments = String.valueOf(loadsolicitudImpresion.getCommentsToPrintAutorizante(forma.getIdDocument(), String.valueOf(usuario.getIdPerson()))).trim();
									
								}
								
								forma.setControlada(!comments.equals(""));
								//comments = comments.equals("") ? "Impresión No Controlada" : comments;
								comments = comments.equals("") ? "" : comments;
								if(comments.indexOf("No Controlada")!=-1) {
									forma.setControlada(false);
								}
							} catch (Exception e) {
								comments = "Impresión No Controlada";
								forma.setControlada(false);
								e.printStackTrace();
							}

							// buscamos los firmantes del documento
							Collection<BeanFirmsDoc> firmas = null;
							Collection firmasRevisores = null;
							BeanFirmsDoc firmaElaborador = null;
							Collection firmasColaboradores = null;
							// HandlerDocuments.loadFirmsToVersionDoc(request.getParameter("idDocument"),
							// request.getParameter("idVersion"),String.valueOf(forma.getStatu()));

							boolean viewReaders = String.valueOf(HandlerParameters.PARAMETROS.getViewReaders()).equals("1");
							boolean viewCreator = String.valueOf(HandlerParameters.PARAMETROS.getViewCreator()).equals("1");
							boolean viewCollaborator = String.valueOf(HandlerParameters.PARAMETROS.getViewCollaborator()).equals("1");

							if (forma != null && forma.getTypeWF() == Constants.permission) {
								firmas = HandlerDocuments.loadFirmsToVersionDoc(forma.getIdDocument(), String.valueOf(forma.getNumVer()), String.valueOf(forma.getStatu()), true);
							} else {
								firmas = HandlerDocuments.loadFirmsToVersionDoc(forma.getIdDocument(), String.valueOf(forma.getNumVer()), String.valueOf(forma.getStatu()), false);
								if (viewReaders) {
									firmasRevisores = HandlerDocuments.loadFirmsReviewsToVersionDoc(forma.getIdDocument(), String.valueOf(forma.getNumVer()), String.valueOf(forma.getStatu()));
								}
							}

							if (viewCreator) {
								firmaElaborador = HandlerDocuments.getCreatorDocument(forma.getIdDocument(), String.valueOf(forma.getNumVer()));
							}

							if (viewCollaborator) {
								firmasColaboradores = HandlerDocuments.getCollaboratorDocument(forma.getIdDocument(), String.valueOf(forma.getNumVer()));
							}

							if (firmas == null || firmas.isEmpty()) {
								firmas = HandlerDocuments.getSignatureOwnerDocument(forma.getIdDocument(), String.valueOf(forma.getNumVer()), String.valueOf(forma.getStatu()));
								BeanFirmsDoc respArea = HandlerDocuments.getSignatureResponsibleSacop(forma.getIdDocument(), String.valueOf(forma.getNumVer()), String.valueOf(forma.getStatu()));
								boolean repetido = false;
								if(respArea!=null) {
									for(BeanFirmsDoc bean: firmas) {
										if(bean.getNameUser().equals(respArea.getNameUser())) {
											repetido = true;
											break;
										}
									}
									if(!repetido) {
										firmas.add(respArea);
									}
								}
							}

							String sImpNameCharge = String.valueOf(HandlerParameters.PARAMETROS.getImpNameCharge());
							int impNameCharge = 0;
							if (!ToolsHTML.isEmptyOrNull(sImpNameCharge) && ToolsHTML.isNumeric(sImpNameCharge)) {
								impNameCharge = Integer.parseInt(sImpNameCharge);
							}

							String lblDetNombreUsuario;
							switch (impNameCharge) {
							case 0:
								lblDetNombreUsuario = rb.getString("charge.userName");
								break;
							case 1:
								lblDetNombreUsuario = rb.getString("charge.charge");
								break;
							case 2:
								lblDetNombreUsuario = rb.getString("charge.nameCharge");
								break;
							default:
								lblDetNombreUsuario = rb.getString("charge.userName");
								break;
							}

							// fin firmantes del documento

							pdf.setPrinting(forma.isPrinting());

							// cambio de datos a las etiquetas
							pdf.setLblCodigo(rb.getString("doc.number"));
							pdf.setLblNombre(rb.getString("cbs.name"));
							pdf.setLblVersion(rb.getString("doc.Ver"));
							pdf.setLblFechaDeAprobacion(rb.getString("doc.dateApprovedAvr"));
							pdf.setLblFechaDeExpiracion(rb.getString("doc.dateExpireAvr"));
							if (aux != null){
							pdf.setLblFechaDeCierre(rb.getString("doc.dateCierreAvr"));
							}
							
							
							pdf.setLblPropietario(rb.getString("cbs.owner"));
							pdf.setLblOrigen(rb.getString("cbs.origin"));
							pdf.setLblSolicitado(rb.getString("imp.solicitante"));
							// portada con resposable de impresion
							if (isRespsolimpres) {
								pdf.setLblResponsableImpresion(rb.getString("imp.responsableImpresion"));
								
							}
							pdf.setLblImpreso(rb.getString("imp.datePrint"));
							pdf.setLblNumeroDeCopias(rb.getString("imp.numCopy"));
							pdf.setLblSoloPropietario(rb.getString("sch.noSignature"));

							// Datos de la pagina
							pdf.setDatEncabezado1(HandlerParameters.PARAMETROS.getHeadImp1());
							pdf.setDatEncabezado2(HandlerParameters.PARAMETROS.getHeadImp2());
							pdf.setDatEncabezado3(HandlerParameters.PARAMETROS.getHeadImp3());
							pdf.setDatCodigo(forma.getPrefix().concat(forma.getNumber()));
							pdf.setDatNombre(forma.getNameDocument());
							
							pdf.setDatVersion(String.valueOf(forma.getMayorVer()).concat(".").concat(forma.getMinorVer()));
							pdf.setDatFechaDeAprobacion(forma.getDateApproved());
							pdf.setDatFechaDeExpiracion(forma.getDateExpires());
							if (aux != null){
								pdf.setDatFechaDeCierre(ToolsHTML.sdfShow.format(HandlerSacop.fechaCierreSacop(sacopNumero)));
							}
							pdf.setDatPropietario(forma.getNameOwner()); 
							pdf.setDatOrigen(HandlerStruct.loadNameAndVersionFromDocument(forma.getIdDocumentOrigen(),forma.getIdVersionOrigen()));
							if(isRespsolimpres) {
								String idSolicitante = String.valueOf(loadsolicitudImpresion.getIdSolicitante(forma.getIdDocument(), String.valueOf(usuario.getIdPerson()))).trim();
								String nombreSolicitante = 	HandlerDBUser.getNameUserOnly(idSolicitante);
								pdf.setDatSolicitado(nombreSolicitante);
								pdf.setDatResponsableImpresion(usuario.getNamePerson());	
							} else {
								pdf.setDatSolicitado(usuario.getNamePerson());
								
							}
							pdf.setDatImpreso(ToolsHTML.sdfShow.format(new java.util.Date()));
							pdf.setDatNumeroDeCopias(String.valueOf(numberCopies));
							pdf.setDatDestinatario(comments);
							

							// Elaborador
							if (viewCreator && firmaElaborador != null) {
								if (viewCreator && firmaElaborador != null) {
									pdf.setLblElaborado(new TituloDetalle(rb.getString("imp.elaborado"),lblDetNombreUsuario,rb.getString("doc.dateTimeMake")));
									pdf.setDetElaborado(cargarFirmanteElaborador(firmaElaborador, impNameCharge, dataCreador));
								}
							}
							
							// Colaboradores
							if (viewCollaborator && firmasColaboradores != null) {
								if ((firmasColaboradores != null && firmasColaboradores.size() > 0)) {
									pdf.setLblEditado(new TituloDetalle(rb.getString("imp.collaborator"),lblDetNombreUsuario,rb.getString("doc.dateTimeEdit")));
									pdf.setDetEditado(cargarFirmantesRevisores(firmasColaboradores, impNameCharge));
								}
							}
							
							// Revisores
							if ((firmasRevisores != null && firmasRevisores.size() > 0)) {
								if ((firmasRevisores != null && firmasRevisores.size() > 0)) {
									pdf.setLblRevisado(new TituloDetalle(rb.getString("imp.revisado"),lblDetNombreUsuario,rb.getString("doc.dateTimeFirm")));
									pdf.setDetRevisado(cargarFirmantesRevisores(firmasRevisores, impNameCharge));
								}
							}

							// Aprobadores
							if ((firmas != null && firmas.size() > 0)) {
								if (firmas != null && firmas.size() > 0) {
									pdf.setLblAprobado(new TituloDetalle(rb.getString("imp.aprobado"),lblDetNombreUsuario,rb.getString("doc.dateTimeFirm")));
									pdf.setDetAprobado(cargarFirmantesRevisores(firmas, impNameCharge));
								}
							}
							
							// razones de cambio
							String razonDeCambio = HandlerDocuments.getReasonToChange(forma.getIdDocument(), String.valueOf(forma.getNumVer()));
							if(!razonDeCambio.trim().equals("")) {
							
								pdf.setLblCambio(new TituloDetalle1(rb.getString("pb.version"),rb.getString("imp.reasonToChange"),rb.getString("imp.dateTimeReasonToChange"),rb.getString("user.title")));
								BeanRazonCambioDoc cambios = HandlerDocuments.getRazonCambio(forma.getIdDocument(), String.valueOf(forma.getNumVer()));
								pdf.setDetCambio(cargarRazonCambio(cambios));
								
							} if(razonDeCambio.trim().equals("") && forma.getMinorVer().equals("0")) {
								Collection cambiosDoc = null;
								cambiosDoc = HandlerDocuments.getRazonesDeCambioDocumentAcual(forma.getIdDocument());
								pdf.setLblCambio(new TituloDetalle1(rb.getString("pb.version"),rb.getString("imp.reasonToChange"),rb.getString("imp.dateTimeReasonToChange"),rb.getString("user.title")));
								pdf.setDetCambio(cargarRazonCambioDocActual(cambiosDoc));
								
							}

							logger.info("Iniciando creacion de hoja de firmantes: " + nameFilePdf);
							pdf.createPdf(nameFilePdf);
							logger.info("Creada hoja de firmantes: " + nameFilePdf);
							// duplicamos el documento y le asignamos seguridad
							//String nameFilePdfSecure = nameFilePdf.replaceAll("pag", "sec");
							//Archivo.copyfile(nameFilePdf,nameFilePdfSecure);
							//PdfSecurity pdfs = new PdfSecurity(nameFilePdfSecure);
							//pdfs.applySecurity();

						}
					//}
				}
				// fin apertura de archivos en pdf
				
				// preguntamos si la primera pagina sera impresa segun el tipo de documento
				boolean isFirstPage = UpdateTypeDocumentsAction.isPrinterFirstPageTypeDocs(ToolsHTML.parseInt(request.getParameter("idDocument"),0));

				//putObjectSession("showDocumentI", forma);
				putObjectSession("showDocument", forma);
				StringBuffer item = new StringBuffer("");
				if(Constants.PRINTER_PDF) {
				//if(true) {	
					item = new StringBuffer("/viewDocumentPdf.jsp");
				} else {
					item = new StringBuffer("/viewDocument.jsp");
				}
				
				if(isVisorNativo) { 
					item = new StringBuffer("/viewDocument.jsp");
				}
				
				item.append("?nameFile=").append(request.getParameter("nameFile"));
				item.append("&idDocument=").append(request.getParameter("idDocument"));
				item.append("&idVersion=").append(request.getParameter("idVersion"));
				// SIMON 25 DE JULIO 2005 INICIO
				//item.append("&imprimir=").append(perm.getToPrint()); //request.getParameter("imprimir"));
				item.append("&imprimir=").append(request.getParameter("imprimir"));
				item.append("&copyContents=").append(forma.isCopyContents());
				item.append("&nameFileToPrint=").append(request.getParameter("toPrint"));
				item.append("&swAdmin=").append(swAdmin);
				item.append("&showFile=").append("showFile.jsp");
				item.append("&controlada=").append(forma.isControlada());
				item.append("&numberCopies=").append(numberCopies);
				item.append("&firstPage=").append(isFirstPage);
				
				
				
				// Valores para la pagina top del pdf
				request.getSession().removeAttribute("numberCopies");
				if(forma.isPrinting()) {
					request.getSession().setAttribute("numberCopies",numberCopies);
				}
				
				// SIMON 25 DE JULIO 2005 FIN
				
				// auditamos la cantidad de veces que ha visto el documento
				PreviewDAO previewDAO = new PreviewDAO();
				previewDAO.save(request.getParameter("idDocument"),request.getParameter("idVersion"),String.valueOf(usuario.getIdPerson()));

				ActionForward nextPage = new ActionForward(item.toString(), false);

				// end
				// Se chequea si el usuario est� Logueado en el sistema.....
				// en caso contrario se almacena la direcci�n de petici�n
				// y se redirige el usuario a la p�gina inicial del Sistema
				if (usuario == null) {
					putObjectSession("optionReturn", nextPage);
					return goTo("login");
				}
				forma.setPrefix(ToolsHTML.getPrefixToDoc(getSession(), usuario, forma.getIdNode()));

				if(isVisorNativo) { 
					// opcion vieja donde se ve el documento con el navegador
					return nextPage;
				} else {
					// devuelve un jnlp para ver el documento
					forma.setNumberCopies(numberCopies);
					HandlerVisor handlerVisor = new HandlerVisor(request, response, forma);
					HandlerDocuments.isPrinting(usuario,forma);
					logger.info("Permiso de Impresión:.. " + forma.isPrinting());
					/*
					
					handlerVisor.generateJNLP(ToolsHTML.getPath(),
							isFirstPage, 
							false,
							false,forma);
						*/
					
					
					handlerVisor.generateJNLP(ToolsHTML.getPath(),
							isFirstPage, 
							5==imprimir && isRespsolimpres ?true:forma.isPrinting(),
							false,forma);
											
					return null;
				}
			} else {
				// putObjectSession("error",getMessage("sch.notDocsView"));
				return goError("sch.notDocsView");
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			goError("sch.notDocsView");
		}
		return goError("E0052");
	}

	private ArrayList cargarFirmantesRevisores(Collection firmas, int impNameCharge) {
		ArrayList<String> lista = null;
		
		StringBuffer firmantes = new StringBuffer();
		String detalle = null;
		BeanFirmsDoc firma = null;
		if (firmas != null) {
			lista = new ArrayList<String>();
			Iterator ite = firmas.iterator();
			while (ite.hasNext()) {
				firma = (BeanFirmsDoc) ite.next();
				switch (impNameCharge) {
				case 0:
					lista.add(firma.getNameUser());
					break;
				case 1:
					lista.add(firma.getCharge());
					break;
				case 2:
					lista.add(firma.getNameUser().concat(" - ").concat(firma.getCharge()));
					break;
				default:
					lista.add(firma.getNameUser());
					break;
				}
				lista.add(firma.getDateReplied());
			}
		}
		return lista;
	}

	private ArrayList cargarFirmanteElaborador(BeanFirmsDoc firma, int impNameCharge, String dataDetalle) {
		ArrayList<String> lista = null;
		if (firma != null && (!ToolsHTML.isEmptyOrNull(firma.getNameUser()) || !ToolsHTML.isEmptyOrNull(firma.getCharge()))) {
			lista = new ArrayList<String>();
			switch (impNameCharge) {
			case 0:
				lista.add(firma.getNameUser());
				break;
			case 1:
				lista.add(firma.getCharge());
				break;
			case 2:
				lista.add(firma.getNameUser().concat(" - ").concat(firma.getCharge()));
				break;
			default:
				lista.add(firma.getNameUser());
				break;
			}
			lista.add(firma.getDateReplied());
		}
		return lista;
	}
	
	private ArrayList cargarRazonCambio( BeanRazonCambioDoc cambio) {
		ArrayList<String> lista = null;
	
		if (cambio != null && (!ToolsHTML.isEmptyOrNull(cambio.getRazonCambio()) || !ToolsHTML.isEmptyOrNull(cambio.getVersionCambio()))) {
			lista = new ArrayList<String>();
			
			lista.add(cambio.getVersionCambio());
			lista.add(cambio.getRazonCambio());
			lista.add(cambio.getFechaCambio());
			lista.add(cambio.getUsuarioCambio());
		}

		return lista;
	}
	
	private ArrayList cargarRazonCambioDocActual(Collection cambios) {
		ArrayList<String> lista = null;
		
		BeanRazonCambioDoc cambio = null;
		if (cambios != null) {
			lista = new ArrayList<String>();
			Iterator ite = cambios.iterator();
			while (ite.hasNext()) {
				cambio = (BeanRazonCambioDoc) ite.next();		
				lista.add(cambio.getVersionCambio().concat(".0"));
				lista.add(cambio.getRazonCambio());		
				lista.add(cambio.getFechaCambio());
				lista.add(cambio.getUsuarioCambio());		
				
			}
		}
		return lista;
	}



}
