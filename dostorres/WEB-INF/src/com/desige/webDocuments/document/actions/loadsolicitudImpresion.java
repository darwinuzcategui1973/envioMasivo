package com.desige.webDocuments.document.actions;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.ResourceBundle;
import java.util.StringTokenizer;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.desige.webDocuments.document.forms.BaseDocumentForm;
import com.desige.webDocuments.document.forms.frmsolicitudImpresion;
import com.desige.webDocuments.persistent.managers.HandlerBD;
import com.desige.webDocuments.persistent.managers.HandlerDBUser;
import com.desige.webDocuments.persistent.managers.HandlerDocuments;
import com.desige.webDocuments.persistent.managers.HandlerGrupo;
import com.desige.webDocuments.persistent.managers.HandlerParameters;
import com.desige.webDocuments.persistent.managers.HandlerStruct;
import com.desige.webDocuments.persistent.managers.HandlerWorkFlows;
import com.desige.webDocuments.persistent.utils.JDBCUtil;
import com.desige.webDocuments.seguridad.forms.PermissionUserForm;
import com.desige.webDocuments.seguridad.forms.SeguridadUserForm;
import com.desige.webDocuments.structured.forms.BaseStructForm;
import com.desige.webDocuments.users.forms.LoginUser;
import com.desige.webDocuments.utils.ApplicationExceptionChecked;
import com.desige.webDocuments.utils.Constants;
import com.desige.webDocuments.utils.DesigeConf;
import com.desige.webDocuments.utils.ToolsHTML;
import com.desige.webDocuments.utils.beans.SuperAction;
import com.desige.webDocuments.utils.beans.SuperActionForm;
import com.desige.webDocuments.utils.beans.Users;
import com.desige.webDocuments.workFlows.forms.BaseWorkFlow;
import com.desige.webDocuments.workFlows.forms.ParticipationForm;

import sun.jdbc.rowset.CachedRowSet;

/**
 * Title:loadsolicitudImpresion.java <br/>
 * Copyright: (c) 2004 Desige Servicios de Computación<br/>
 * @author Ing. Simón Rodriguéz
 * @version WebDocuments v1.0
 * <br/>
 *      Changes:<br/>
 * <ul>
 *      <li> 30/07/2005 (SR) Creation </li>
 *      <li> 11/05/2006 (SR) Validar que el num y el solicitante no vengan nulos en el metodo existepermisoImpresion</li>
 *      <li> 13/07/2006 (NC) Cambios para heredar o no los Prefijos de las Carpetas </li>
 * </ul>
 */
public class loadsolicitudImpresion  extends SuperAction {
	public static final String anuladoprintln = "6";
    public static final String rechazadoprintln = "7";
    public static final String folderPrintln = "1";
    public static final String solicitadoprintln = "1";
    public static final String impresoprintln = "2";
    public static final String canceladoprintln = "3";
    public static final String aprobadoprintln= "5";
    public static final int aprobadoprintlnInt = Integer.parseInt(aprobadoprintln);
    public static final int rechazadoprintlnInt = Integer.parseInt(rechazadoprintln);
    public static final int impresoprintlnInt = Integer.parseInt(impresoprintln);

    //TODO Poner un comentario aqui
    public static final String fileImpresion = "9";

    private StringBuffer mensajeImpresion = new StringBuffer("");
    
    private int idWorkFlow = 0;

    public int getIdWorkFlow() {
		return idWorkFlow;
	}

	public void setIdWorkFlow(int idWorkFlow) {
		this.idWorkFlow = idWorkFlow;
	}

	public synchronized ActionForward execute(ActionMapping mapping,
                                 ActionForm form, HttpServletRequest request,
                                 HttpServletResponse response) {
        super.init(mapping,form,request,response);

        try {
        	return cargarSolicitud(request, getServletContext(),false);
        } catch (ApplicationExceptionChecked ae) {
            return goError(ae.getKeyError());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return goError("E0052");
    }
    
    public ActionForward cargarSolicitud(HttpServletRequest request, ServletContext servletContext, boolean att) throws ApplicationExceptionChecked,Exception {
        Connection con;
        ResourceBundle rb = ToolsHTML.getBundle(request); 
        setRequest(request);
        setServletContext(servletContext);
        
        Users user = (Users)getSessionObject("user");
       
        
        request.getSession().removeAttribute("info");
        String cmd = getParameter("cmd",att)!=null?getParameter("cmd",att):"";
      
        if (cmd.equalsIgnoreCase("nuevo"))  { 
        	cmd="";
        
        }
        int acumCopias = 1;
        String NumCorrelativoImpresion;
        //cargamos toda la data del documento que se quiere imprimir en el bean BaseDocumentForm y algunos de estos datos los guardamos en
        //la tabla tbl_solicitudimpresion
        BaseDocumentForm forma = new BaseDocumentForm();
        boolean SwPermisologiaAutorizante = false;
        String respsolimpres = HandlerParameters.PARAMETROS.getrespsolimpres();
    	boolean isRespsolimpres = (respsolimpres!=null && !respsolimpres.trim().equals(""));
        //este bean se usa para crear el nuevo documento  en linea y llenarlo en las tablas documents y version doc
        BaseDocumentForm docnuevoforma = new BaseDocumentForm();
       //cargamos el id del documento que se quiere imprimir
        forma.setIdDocument(getParameter("idDocument",att)!=null?getParameter("idDocument",att):"0");
        forma.setNumberGen(getParameter("idDocument",att)!=null?getParameter("idDocument",att):"0");
        String queryCorrelativo = " FROM documents WHERE numgen = " + forma.getNumberGen();
        NumCorrelativoImpresion = HandlerDocuments.getField("number",queryCorrelativo,Thread.currentThread().getStackTrace()[1].getMethodName());
        //cargamos la version del documento que se quiere imprimir
        forma.setNumVer(0);
        if (getParameter("idVersion",att)!=null) {
            forma.setNumVer(Integer.parseInt(getParameter("idVersion",att)));
        }
        //cargamos toda la data del documento que se quiere imprimir
        Hashtable tree = (Hashtable)getSessionObject("tree");
        tree = ToolsHTML.checkTree(tree,user);
        HandlerStruct.loadDocument(forma,true,false,tree, request);

        //YSA 14/11/2007 Se agrega comentario de solicitud de impresion
        String mensaje = getParameter("mensaje",att);


        //Luis Cisneros
        //Necesito el tipo de Documento
        //19-04-07
        String typeDoc = HandlerBD.getField("TypeDoc","typedocuments", "idTypeDoc", forma.getTypeDocument(),"=",2,Thread.currentThread().getStackTrace()[1].getMethodName());

		            //Se chequea si tiene permisos de Impresi�n sobre el Documento
		//            if (ToolsHTML.isEmptyOrNull(cmd)) {
		//                SeguridadUserForm segUsr = new SeguridadUserForm();
		//                SeguridadUserForm segGroup = new SeguridadUserForm();
		//                HandlerGrupo.getFieldUser(segUsr,"seguridaduser",true,user.getUser());
		//                HandlerGrupo.getFieldUser(segGroup,"seguridadgrupo",false,user.getIdGroup());
		//                //Si el Usuario no tiene permiso de Impresi�n se lanza una Exception
		//                if (segUsr!=null&&segUsr.getToImpresion() == Constants.permission) {
		//                    throw new ApplicationExceptionChecked("E0076");
		//                } else {
		//                    if (segUsr.getToImpresion() == 2 && (segGroup!=null && segGroup.getToImpresion() != 0)) {
		//                        throw new ApplicationExceptionChecked("E0076");
		//                    }
		//                }
		
		//                ToolsHTML tools = new ToolsHTML();
		//                PermissionUserForm perm = tools.getSecurityUserInDoc(user,forma.getIdDocument(),forma.getIdNode());
		//                if (perm==null || perm.getToImpresion() == Constants.notPermission) {
		//                    throw new ApplicationExceptionChecked("E0076");
		//                }
		//            }

        if (cmd.equalsIgnoreCase(SuperActionForm.cmdUpdatePrint)) {
            //se hace el proceso de impresi�n
            String idDocumentptr = getParameter("idDocumentptr",att)!=null?getParameter("idDocumentptr",att):"0";
            String idUserptr = getParameter("idUserptr",att)!=null?getParameter("idUserptr",att):"0";
            //Si el Documento es de Libre Impresi�n
            frmsolicitudImpresion dataSolicitud = null;
            boolean existePermiso = loadsolicitudImpresion.existepermisoImpresion(idDocumentptr,idUserptr);
            int numberCopies = 1;
            if (("true".compareTo(getParameter("printFree",att)) == 0)&& (!existePermiso)) {
                //Se Verifica si hay una Solicitud de Copia Controlada para
                //Evitar Sobreescribirla
//                if (!loadsolicitudImpresion.existepermisoImpresion(idDocumentptr,idUserptr)) {
                    //Se crea el Registro si el Documento es de Libre Impresi�n
                    con = JDBCUtil.getConnection("cargarSolicitud");
                    dataSolicitud = insertPrint(forma,(int)user.getIdPerson(),con, typeDoc, mensaje);
                    dataSolicitud.setNameSolicitante(user.getNamePerson());
//                }
            } else {
                if (existePermiso) {
                	numberCopies = loadsolicitudImpresion.getNumberOfCopiesToPrint(idDocumentptr,idUserptr);
                    dataSolicitud = loadsolicitudImpresion.updatepermisoImpresion(idDocumentptr,idUserptr,String.valueOf(forma.getNumVer()));
                } else {
                	numberCopies = 0;
                    throw new ApplicationExceptionChecked("E0076");
                }
            }
            if (dataSolicitud!=null) {
                putObjectSession("dataSolicitud",dataSolicitud);
                putObjectSession("printOK","OK");
                putObjectSession("numberCopies",String.valueOf(numberCopies));
            }
            if(!att) {
            	return goTo("showInfo");
            } else {
            	return null;
            }
        } else {
            //en caso que el formulaio de solicitud de impresion venga con una peticion
            //cmd=SuperActionForm.cmdLoad es una variable inicializada en showSolicitudImpresion.jsp
            if (cmd.equalsIgnoreCase(SuperActionForm.cmdLoad)) {
                //se llena la forma que va para la tabla tbl_solicitudimpresion
                frmsolicitudImpresion frmsolicitud = new frmsolicitudImpresion();
                //llenamos datos de la solicitud de impresion en un bean y la llenaremos en la tabla tblsolicitudimpresion
                frmsolicitud.setPath(ToolsHTML.getPath().concat("tmp"));  // \\tmp
                
                frmsolicitud.setNameDocument(forma.getNameDocument());
                frmsolicitud.setCodigo(forma.getPrefix()+forma.getNumber());

                //la respuesta de controlada es (si/no)
                String controlada = getParameter("controlada",att)!=null?getParameter("controlada",att):"";
                // ydavila Elmor
                String solelimin = getParameter("paracambiar",att)!=null?getParameter("paracambiar",att):"";
                String solcambio = getParameter("paraeliminar",att)!=null?getParameter("paraeliminar",att):"";
                // el autorizante del flujo puede ser el propietario o el responsable de impresion
                String autorizante = !ToolsHTML.isEmptyOrNull(getParameter("nameResponsable",att))?getParameter("nameResponsable",att):getParameter("namePropietario",att);
                String propietario = !ToolsHTML.isEmptyOrNull(getParameter("namePropietario",att))?getParameter("namePropietario",att):"0";
                String idDocument = getParameter("idDocument",att)!=null?getParameter("idDocument",att):"0";
                String idVersion = getParameter("idVersion",att)!=null?getParameter("idVersion",att):"0";
                String solicitante = getParameter("solicitante",att)!=null?getParameter("solicitante",att):"0";
                String gerenciaSolicitante = null;//HandlerBD.getField("cargo","person","idperson",solicitante,"=",2);
                String usergerenciaSolicitante = null;//HandlerBD.getField("nameUser","person","idperson",solicitante,"=",2);
                String usergerenciaEmailSolicitante = null;//HandlerBD.getField("email","person","idperson",solicitante,"=",2);
                String[] values1 = HandlerDocuments.getFields(new String[] {"cargo","nameUser","email"},"person","idperson",String.valueOf(solicitante));
               
                
                if (values1!=null) {
                    gerenciaSolicitante = values1[0];
                    usergerenciaSolicitante = values1[1];
                    usergerenciaEmailSolicitante = values1[2];
                }

                String usergerenciaAutorizante = null;//HandlerBD.getField("nameUser","person","idperson",autorizante,"=",2);
                String grupogerenciaAutorizante = null;//HandlerBD.getField("idGrupo","person","idperson",autorizante,"=",2);
                String usergerenciaEmailAutorizante = null;//HandlerBD.getField("email","person","idperson",autorizante,"=",2);
                values1 = null;
                values1 = HandlerDocuments.getFields(new String[] {"nameUser","idGrupo","email"},"person",
                								"idperson",String.valueOf(autorizante));
                
                //   "idperson",String.valueOf(autorizante));
                if (values1!=null) {
                    usergerenciaAutorizante = values1[0];
                    grupogerenciaAutorizante = values1[1];
                    usergerenciaEmailAutorizante = values1[2];
                }
              
				frmsolicitud.setGerenciaSolicitante(gerenciaSolicitante);
				frmsolicitud.setSolicitante(Integer.parseInt(solicitante.toString()));
				frmsolicitud.setAutorizante(Integer.parseInt(autorizante));
				frmsolicitud.setStatusautorizante(Integer.parseInt(loadsolicitudImpresion.solicitadoprintln));
				frmsolicitud.setStatusimpresion(Integer.parseInt(loadsolicitudImpresion.solicitadoprintln));
				frmsolicitud.setNumberGen(Integer.parseInt(idDocument));
				frmsolicitud.setNumVer(Integer.parseInt(idVersion));
				ToolsHTML tools = new ToolsHTML();

				// chequeo si es solicitud con responasable el P1 seguridad del propietario
		/*
				if (isRespsolimpres && autorizante != propietario) {
					
					String usergerenciaPropietario = null;// HandlerBD.getField("nameUser","person","idperson",autorizante,"=",2);
					String grupogerenciaPropietario = null;// HandlerBD.getField("idGrupo","person","idperson",autorizante,"=",2);
					// String usergerenciaEmailPropietario = null;// HandlerBD.getField("email","person","idperson",autorizante,"=",2);
					values1 = null;
					values1 = HandlerDocuments.getFields(new String[] { "nameUser", "idGrupo" }, "person", "idperson", String.valueOf(propietario));

					// "idperson",String.valueOf(autorizante));
					if (values1 != null) {
						usergerenciaPropietario = values1[0];
						grupogerenciaPropietario = values1[1];
						// usergerenciaEmailPropietario = values1[2];
					}

					Users dataOwnerDoc = HandlerDBUser.load(Long.parseLong(propietario), true);
					PermissionUserForm perm = null;
					// Si el Documento es de Libre Impresi�n no se revisa la seguridad del Usuario
					// Ya que cualquiera puede imprimir
					boolean isFreeToPrint = false;
					perm = tools.getSecurityUserInDoc(dataOwnerDoc, idDocument, forma.getIdNode());

					forma.setImprimir("");
					if (perm != null && perm.getToImpresion() == Constants.permission) {
						forma.setImprimir("imprimir");
					}
					// buscamos la seguridad del autorizante para saber si tiene permiso de impresion a nivel standar
					SeguridadUserForm formaSeg = new SeguridadUserForm();
					if (!isFreeToPrint) {
						HandlerGrupo.getFieldUser(formaSeg, "seguridaduser", true, usergerenciaPropietario);
						// si es igual a dos, la seguridad la hereda del grupo
						if (formaSeg.getToImpresion() == 2) {
							HandlerGrupo.getFieldUser(formaSeg, "seguridadgrupo", false, grupogerenciaPropietario);
						}
						// confirmamos que tenga seguridad standar y que tenga permisologia para imprimir el documento y
						// si se cumple, puede permitir dar autorizacion para imprimir dicho documento
						if ((formaSeg.getToImpresion() == Constants.notPermission)) { // && "imprimir".equalsIgnoreCase(forma.getImprimir()) ) {
							SwPermisologiaAutorizante = true;
						}
					} else {
						SwPermisologiaAutorizante = true;
						formaSeg.setToImpresion(1);
					}
					// ----------------------------------------------------------------------------------------------------\\

				}
				*/
				
				String usergerenciaPropietario = null;// HandlerBD.getField("nameUser","person","idperson",autorizante,"=",2);
				String grupogerenciaPropietario = null;// HandlerBD.getField("idGrupo","person","idperson",autorizante,"=",2);
				// String usergerenciaEmailPropietario = null;// HandlerBD.getField("email","person","idperson",autorizante,"=",2);
				values1 = null;
				
           if (isRespsolimpres && autorizante != propietario) {
					
				
					values1 = HandlerDocuments.getFields(new String[] { "nameUser", "idGrupo" }, "person", "idperson", String.valueOf(propietario));

					// "idperson",String.valueOf(autorizante));
					if (values1 != null) {
						usergerenciaPropietario = values1[0];
						grupogerenciaPropietario = values1[1];
						// usergerenciaEmailPropietario = values1[2];
					}
	
           } 


				Users dataOwnerDoc = HandlerDBUser.load(Long.parseLong(isRespsolimpres && autorizante != propietario?propietario:autorizante), true);
			
				PermissionUserForm perm = null;// tools.getSecurityUserInDoc(dataOwnerDoc,idDocument,forma.getIdNode());
				// Si el Documento es de Libre Impresi�n no se revisa la seguridad del Usuario
				// Ya que cualquiera puede imprimir
				boolean isFreeToPrint = false;
				// if (forma!=null && !ToolsHTML.isEmptyOrNull(forma.getKeys()) &&
				// forma.getKeys().indexOf(DesigeConf.getProperty("typeDocs.docPrintFree"))>=0) {
				// perm = new PermissionUserForm(Constants.permission);
				// isFreeToPrint = true;
				// } else {
				perm = tools.getSecurityUserInDoc(dataOwnerDoc, idDocument, forma.getIdNode());
				// }

				forma.setImprimir("");
				if (perm != null && perm.getToImpresion() == Constants.permission) {
					System.out.println("********************* tiene permiso");
					forma.setImprimir("imprimir");
				}
				// buscamos la seguridad del autorizante para saber si tiene permiso de impresion a nivel standar
				SeguridadUserForm formaSeg = new SeguridadUserForm();
				if (!isFreeToPrint) {
					HandlerGrupo.getFieldUser(formaSeg, "seguridaduser", true,isRespsolimpres && autorizante != propietario?usergerenciaPropietario:usergerenciaAutorizante);
					// si es igual a dos, la seguridad la hereda del grupo
					if (formaSeg.getToImpresion() == 2) {
						HandlerGrupo.getFieldUser(formaSeg, "seguridadgrupo", false,isRespsolimpres && autorizante != propietario?grupogerenciaPropietario:grupogerenciaAutorizante);
					}
					// confirmamos que tenga seguridad standar y que tenga permisologia para imprimir el documento y
					// si se cumple, puede permitir dar autorizacion para imprimir dicho documento
					if ((formaSeg.getToImpresion() == Constants.notPermission)) { // && "imprimir".equalsIgnoreCase(forma.getImprimir()) ) {
						SwPermisologiaAutorizante = true;
					}
				} else {
					SwPermisologiaAutorizante = true;
					formaSeg.setToImpresion(1);
				}
				// ----------------------------------------------------------------------------------------------------\\
				// chequeamos todos los usuarios destinatarios escogidos
				StringBuffer usuarioDestinatarios = new StringBuffer();
				StringBuffer usuariosNomDest = new StringBuffer("");
				Hashtable usuariosSeleccionados = null;
				StringBuffer copias = new StringBuffer("");
				if ((getParameter("docRelations", att) != null) && ((getParameter("copiasRelations", att) != null))) {
					usuariosSeleccionados = copiasControladas(getParameter("docRelations", att), getParameter("copiasRelations", att));
				}
				if (usuariosSeleccionados != null) {
					Enumeration numKeys = usuariosSeleccionados.keys();
					acumCopias = 0;
					while (numKeys.hasMoreElements()) {
						String key1 = (String) numKeys.nextElement();
						copias.append((String) usuariosSeleccionados.get(key1));
						if (ToolsHTML.isNumeric((String) usuariosSeleccionados.get(key1))) {
							acumCopias = acumCopias + Integer.parseInt((String) usuariosSeleccionados.get(key1));
						}
						usuarioDestinatarios.append(key1);
						usuariosNomDest.append(buscarNomApellUser(key1));
						usuariosNomDest.append(" ").append(rb.getString("imp.copias")).append(" = ").append((String) usuariosSeleccionados.get(key1));
						if (numKeys.hasMoreElements()) {
							usuarioDestinatarios.append(",");
							usuariosNomDest.append(",");
							copias.append(",");
						}
					}
				}
				// por lo menos una copia se debe sacar
				if (acumCopias == 0) {
					acumCopias = 1;
				}
				frmsolicitud.setAcumCopias(String.valueOf(acumCopias));
				// en caso que sea copias controladas seria si y si es negativa pues
				// solamente colocamos una copia y no colocamos ningun destinatario
				if (!"si".equalsIgnoreCase(controlada)) {
					frmsolicitud.setDestinatarios(null);
					frmsolicitud.setCopias("1");
				} else {
					// ydavila Elmor Aqu� van las opciones de cambiar y eliminar
					frmsolicitud.setDestinatarios(usuarioDestinatarios.toString());
					frmsolicitud.setCopias(copias.toString());
				}
				int numberDoc = HandlerStruct.proximo("docs", "documents", "numGen");
				int impresionnum = HandlerStruct.proximo("numsolicitud", "tbl_solicitudimpresion", "numsolicitud");
				// el numero de solicitud es el numero de documento nuevo a crear.
				frmsolicitud.setNumsolicitud(numberDoc);

				// con frmsolicitud ya hemos completado el bean que llenara la tabla tblsolicitudimpresion
				// colocamos una fecha grande para que nunca expire
				java.util.Date dateExp = ToolsHTML.getDateExpireDocument("4005-11-09 08:36:47");
				String statuDoc = HandlerDocuments.docTrash;
				int num = 0;
				// si tiene permisologia el autorizante, generararemos el flujo de trabajo
				// y agarraremos el idWorkFlows
				if (SwPermisologiaAutorizante) {
					num = HandlerStruct.proximo("workflows", "workflows", "idWorkFlow");
					this.setIdWorkFlow(num);
				}
				// -----------------------------------------------------------------------------------------------------------------------------
				// realizamos la busqueda de la carpeta impresion de esa localidad, en caso que no exista, la creamos
				// para llenar el tree, colocamos el usuario como administrador,el grupo como administrador, la seguridad nula para
				// que carque toda la estructura
				Hashtable subNodos = new Hashtable();
				tree = HandlerStruct.loadAllNodes(null, DesigeConf.getProperty("application.admon"), DesigeConf.getProperty("application.admon"), subNodos,
						null);
				String idNodeDocument = forma.getIdNode() != null ? forma.getIdNode() : "0";
				String idLocation = HandlerStruct.getIdLocationToNode(tree, idNodeDocument);
				String idNodePrintln = null;
				idNodePrintln = crearCarpetaImpresion(idLocation);
				BaseStructForm frmCarpetaPrt = new BaseStructForm();
				frmCarpetaPrt.setIdNode(idNodePrintln);
				// Se Crea la Carpeta de Impresi�n si la Misma no Existe
				boolean existeCarpetaPrintln = true;
				if (ToolsHTML.isEmptyOrNull(idNodePrintln)) {
					frmCarpetaPrt.setNodeType(DesigeConf.getProperty("folderType"));
					frmCarpetaPrt.setIdNodeParent(idLocation);
					frmCarpetaPrt.setDescription(rb.getString("imp.descripcarp"));
					frmCarpetaPrt.setName(rb.getString("imp.namecarp"));
					frmCarpetaPrt.setNameIcon("menu_folder_closed.gif");
					// frmCarpetaPrt.setOwner(HandlerBD.getField("nameUser","person","idGrupo",DesigeConf.getProperty("application.admon"),"=",2)!=null?HandlerBD.getField("nameUser","person","idGrupo",DesigeConf.getProperty("application.admon"),"=",2):"0");
					frmCarpetaPrt.setOwner(HandlerBD.getField("nameUser", "person", "idGrupo", DesigeConf.getProperty("application.admon"), "=", 2,
							Thread.currentThread().getStackTrace()[1].getMethodName()));
					frmCarpetaPrt.setPrefix(rb.getString("imp.prefix"));
					try {
						// creamos la crpeta impreion
						existeCarpetaPrintln = HandlerStruct.insert(frmCarpetaPrt);
						con = JDBCUtil.getConnection("cargarSolicitud");
						con.setAutoCommit(false);
						// se ha creado la carpeta pintln, pero falta actualizar una variable
						// que la identifica
						// como una carpeta println
						updatecarpetaImpresion(con, frmCarpetaPrt);
						con.setAutoCommit(true);
						if (con != null) {
							con.close();
						}
					} catch (Exception e) {
						e.printStackTrace();
						existeCarpetaPrintln = false;
					}
				}
				// si existe la carpeta impresion
				if (existeCarpetaPrintln) {
					con = JDBCUtil.getConnection("cargarSolicitud");
					con.setAutoCommit(false);
					java.util.Date date = new java.util.Date();
					Timestamp time = new Timestamp(date.getTime());
					String[] items = new String[] { "monthExpireDoc", "unitTimeExpire" };
					String[] values = HandlerParameters.getFieldsExpired(items, forma.getTypeDocument());
					String monthExpireDoc = values != null ? values[0] : null;
					String unit = values != null ? values[1].trim() : null;
					int value = 0;
					if (ToolsHTML.isNumeric(monthExpireDoc.trim())) {
						value = Integer.parseInt(monthExpireDoc.trim());
					}
					String[] itemsDrafts = new String[] { "monthsExpireDrafts", "unitTimeExpireDrafts" };
					String[] valuesDrafts = HandlerParameters.getFieldsExpired(itemsDrafts, forma.getTypeDocument());
					String monthsExpireDrafts = valuesDrafts != null ? valuesDrafts[0] : null;
					String unitDrafts = valuesDrafts != null ? valuesDrafts[1].trim() : null;
					int valueDrafts = 0;
					if (ToolsHTML.isNumeric(monthsExpireDrafts.trim())) {
						valueDrafts = Integer.parseInt(monthsExpireDrafts.trim());
					}
					java.util.Date dateCreate = new java.util.Date();
					Timestamp timeCreate = new Timestamp(dateCreate.getTime());
					// llenamos todas las variables del nuevo documento en linea a craer con su version
					// el 1 significa que no va a ser publicado
					docnuevoforma.setDocPublic("1");
					docnuevoforma.setPrefix("");
					docnuevoforma.setNormISO("0");
					docnuevoforma.setDocProtected("");
					docnuevoforma.setDocOnline("0");
					docnuevoforma.setURL("");
					docnuevoforma.setKeys("");
					docnuevoforma.setDescript("");
					// esta variable va para la base de datos y la usamos en el historico
					StringBuffer commentsImpresion = new StringBuffer("");
					StringBuffer comments = new StringBuffer(rb.getString("imp.lineacomments0"));
					if (!ToolsHTML.isEmptyOrNull(usuariosNomDest.toString())) {
						comments.append(" ").append(rb.getString("imp.controlada0"));
					} else {
						comments.append(" ").append(rb.getString("imp.nocontrolada0"));
					}

					// <br>Esta solicitud debe ser aprobada por:
					comments.append(rb.getString("imp.lineacomments1"));

					// Nombre del usuario que aprueba
					comments.append(buscarNomApellUser(String.valueOf(frmsolicitud.getAutorizante())));

					// <br>Solicitud elaborada por:
					comments.append(rb.getString("imp.lineacomments2"));

					// Nombre del solicitante
					comments.append(buscarNomApellUser(String.valueOf(frmsolicitud.getSolicitante())));

					// Luis Cisneros
					// 19/04/07
					// El Tipo de documento a mostrar no debe ser Solicitud de Impresi�n
					// sino el del documento original:
					// Se cambio en el bundle la clave imp.lineacomments3 para que no tengo la parte final

					// //<br>quien esta solicitando una autorizaci�n de impresi�n para el tipo de documento:Solicitud de Impresi�n
					comments.append(rb.getString("imp.lineacomments3"));
					comments.append(typeDoc);

					// Fin 19/04/07

					// <br>Nombre:
					comments.append(rb.getString("imp.name"));

					// Nombre del Documento
					comments.append(frmsolicitud.getNameDocument());

					// <br>Codigo:
					comments.append(rb.getString("imp.codigo"));

					// Codigo del Documento
					// Luis Cisneros
					// 19/04/07
					// El C�digo a mostrar no debe ser el del Flujo de Impresi�n
					// sino el del documento original:
					// comments.append(frmsolicitud.getCodigo());
					comments.append(forma.getPrefix() + forma.getNumber());
					// fin 19/04/07

					if (!ToolsHTML.isEmptyOrNull(usuariosNomDest.toString())) {
						commentsImpresion.append(rb.getString("imp.destinatarios")).append(usuariosNomDest.toString());
						commentsImpresion.append(rb.getString("imp.controlada"));
						comments.append(rb.getString("imp.destinatarios")).append(usuariosNomDest.toString()).append(" ");
						comments.append(rb.getString("imp.controlada"));
					} else {
						commentsImpresion.append(rb.getString("imp.nocontrolada"));
						comments.append(rb.getString("imp.nocontrolada"));
					}

					// <br>Cantidad de Copias:
					commentsImpresion.append(rb.getString("imp.cantcopias")).append(frmsolicitud.getAcumCopias()).append("<br>");
					comments.append(rb.getString("imp.cantcopias")).append(frmsolicitud.getAcumCopias());

					// <br>Comentarios:
					if (!ToolsHTML.isEmptyOrNull(mensaje)) {
						comments.append(rb.getString("imp.comentariosSolicitud")).append(mensaje).append("<br>");
						commentsImpresion.append(rb.getString("imp.comentariosSolicitud")).append(mensaje).append("<br>");
					}

					frmsolicitud.setComments(commentsImpresion.toString());

					docnuevoforma.setComments(comments.toString());
					docnuevoforma.setNameDocument(forma.getNameDocument());
					docnuevoforma.setOwner(usergerenciaSolicitante);
					// elk codigo del documento de solicitud de impresion, no va a tener nada
					docnuevoforma.setNumber(String.valueOf(impresionnum));
					docnuevoforma.setTypeDocument(HandlerDocuments.TypeDocumentsImpresion.trim());
					docnuevoforma.setNumberGen(String.valueOf(frmsolicitud.getNumsolicitud()));
					docnuevoforma.setIdNode(frmCarpetaPrt.getIdNode() != null ? frmCarpetaPrt.getIdNode() : "0");
					docnuevoforma.setStatu(1);
					docnuevoforma.setMayorVer(forma.getMayorVer());
					docnuevoforma.setMinorVer(forma.getMinorVer());
					// Si es un Documento de Libre Impresi�n se procede colocar la versi�n como Aprobada
					if (isFreeToPrint) {
						docnuevoforma.setDateApproved(ToolsHTML.sdf.format(new java.util.Date()));
						docnuevoforma.setDocPublic("0");
						docnuevoforma.setStatu(Integer.parseInt(HandlerDocuments.docApproved));

						docnuevoforma.setDateExpires(null);
						docnuevoforma.setDateExpiresDrafts(null);
						docnuevoforma.setNameFile(null);
						frmsolicitud.setNuevaSolicitud(false);
					} else {
						docnuevoforma.setDateApproved(null);
						docnuevoforma.setDateExpires(null);
						docnuevoforma.setDateExpiresDrafts(null);
						docnuevoforma.setNameFile(null);
						frmsolicitud.setNuevaSolicitud(false);
					}
					boolean existeSolicitud = existefrmsolicitudImpresion(con, frmsolicitud);
					// hacemos las transacciones en la tabla tbl_solicitudimpresion
					if ((existeSolicitud) && (!frmsolicitud.getNuevaSolicitud())) {
						mensajeImpresion.setLength(0);
						mensajeImpresion.append("imp.pendiente");
					} else {
						int numVer = 0;
						BaseWorkFlow flujoForm = new BaseWorkFlow();
						BaseDocumentForm docForm = new BaseDocumentForm();
						// si el autorizante tiene permisologia para firmar la aprobacion de impresion,
						// se realiza el documento en linea y el flujo de trabajo
						if (SwPermisologiaAutorizante) {
							// generamos el documento en linea para su impresion
							numVer = HandlerStruct.proximo("versiondoc", "versiondoc", "numver");
							docnuevoforma.setNumVer(numVer);
							HandlerStruct.getPreparedStatementDocumentInsert(docnuevoforma, con, time, timeCreate, numVer);
							HandlerStruct.getPreparementStatementVersionInsert(docnuevoforma, con, value, unit, valueDrafts, unitDrafts, timeCreate, false,
									null);
							HandlerStruct.saveBD(con, docnuevoforma.getNameFile(), docnuevoforma.getComments(), frmsolicitud.getPath(),
									docnuevoforma.getNumVer(), false);
							// //esta herencia es automatica 2007-11-14
							// HandlerDocuments.permisosHeredadosDocs(docnuevoforma,Integer.parseInt(docnuevoforma.getNumberGen()) ,con);
							if (!isFreeToPrint) {
								// creamos el flujo ---------------------------------------------------------------------
								String idDoc = String.valueOf(docnuevoforma.getNumberGen());
								docForm.setIdDocument(idDoc);
								docForm.setNumberGen(idDoc);
								StringBuffer commentsflj = new StringBuffer(rb.getString("imp.lineacomments1"));
								commentsflj.append(buscarNomApellUser(String.valueOf(frmsolicitud.getAutorizante())));
								commentsflj.append(rb.getString("imp.lineacomments2"))
										.append(buscarNomApellUser(String.valueOf(frmsolicitud.getSolicitante())));

								// Luis Cisneros
								// 19/04/07
								// El Tipo de documento a mostrar no debe ser Solicitud de Impresi�n
								// sino el del documento original:
								// Se cambio en el bundle la clave imp.lineacomments3 para que no tengo la parte final

								// //<br>quien esta solicitando una autorizaci�n de impresi�n para el tipo de documento:Solicitud de Impresi�n
								commentsflj.append(rb.getString("imp.lineacomments3"));
								commentsflj.append(typeDoc);

								// commentsflj.append(rb.getString("imp.name")).append(frmsolicitud.getNameDocument()).append(rb.getString("imp.codigo")).append(frmsolicitud.getCodigo());
								commentsflj.append(rb.getString("imp.name")).append(frmsolicitud.getNameDocument());

								commentsflj.append(rb.getString("imp.codigo"));
								commentsflj.append(forma.getPrefix() + forma.getNumber());
								// Fin 19/04/07

								if (!ToolsHTML.isEmptyOrNull(usuariosNomDest.toString())) {
									commentsflj.append(rb.getString("imp.destinatarios")).append(usuariosNomDest.toString()).append(" ");
									commentsflj.append(rb.getString("imp.cantcopias")).append(frmsolicitud.getAcumCopias());
									//// System.out.println("frmsolicitud.getAcumCopias()="+frmsolicitud.getAcumCopias());
								}
								commentsflj.append("<br><br><br>");
								commentsflj.append(rb.getString("imp.solimpreaprobar"));

								// <br>Comentarios:
								if (!ToolsHTML.isEmptyOrNull(mensaje))
									commentsflj.append(rb.getString("imp.comentariosSolicitud")).append(mensaje).append("<br>");

								flujoForm.setTitleForMail(rb.getString("wf.newWFImpr"));
								flujoForm.setComments(commentsflj.toString());
								flujoForm.setNumDocument(Integer.parseInt(idDoc));
								flujoForm.setNumVersion(docForm.getNumVer());
								// no tiene expiracion del flujo, al menos que lleguemos vivos a esa fecha
								flujoForm.setDateExpireWF(Constants.FECHA_EXPIRACION_GENERICA);
								Object[] Autorizante = new Object[1];
								Autorizante[0] = usergerenciaAutorizante;
								flujoForm.setUsersSelected(Autorizante);
								Users usuarioAutorizante = new Users();
								usuarioAutorizante.setUser(usergerenciaAutorizante);
								usuarioAutorizante.setNameUser(usergerenciaAutorizante);
								usuarioAutorizante.setEmail(usergerenciaEmailAutorizante);
								// el flujo es secuencial
								flujoForm.setSecuential(0);
								// el flujo es condicional
								flujoForm.setConditional(0);
								// es notificado por mail el usuario
								flujoForm.setNotified(1);
								// nunca espira el flujo
								flujoForm.setExpire(0);
								// el tipo de flujo es de aprobacion
								flujoForm.setTypeWF(1);
								flujoForm.setNumVersion(numVer);
								flujoForm.setLastVersion(String.valueOf(numVer));
								flujoForm.setImprimir(Byte.parseByte(loadsolicitudImpresion.solicitadoprintln));
								HandlerWorkFlows.insert(flujoForm, user, usuarioAutorizante, con, statuDoc, dateExp, num); // el solicitante es el usuario que
																															// en session
								// En el userWorkFlows siempre firman dos usuarios , en este caso el due�o queda dos veces
								// por eso, forzamos la firma de flujo que sea solo el due�o que falte firmar nada mas ...
								ParticipationForm formWorkFlowUser = new ParticipationForm();
								formWorkFlowUser.setResult(HandlerWorkFlows.wfuAcepted);
								formWorkFlowUser.setStatu(Integer.parseInt(HandlerWorkFlows.wfuAcepted));
								formWorkFlowUser.setCommentsUser("");
								// firmamos todos los usuarios que sean diferentes al due�o de forma manual
								HandlerWorkFlows.updateUserWorkFlows(con, time, formWorkFlowUser, num);
							} else {
								frmsolicitud.setStatusautorizante(5);
								frmsolicitud.setStatusimpresion(5);
							}
							// fin de crear flujo------------------------------------------------------------------------
							// en caso que anteriormente alla pedido una solicitud yb esta este aprobada y vigente, sed cancela
							// para poder ingresar una nueva ................................
							if (frmsolicitud.getNuevaSolicitud()) {
								updatefrmsolicitudImpresionExiste(con, frmsolicitud);
								insertfrmsolicitudImpresion(con, frmsolicitud);
								mensajeImpresion.setLength(0);
								mensajeImpresion.append("imp.update");
							} else {
								insertfrmsolicitudImpresion(con, frmsolicitud);
								if (isFreeToPrint) {
									mensajeImpresion.setLength(0);
									mensajeImpresion.append("imp.freeOK");
								} else {
									mensajeImpresion.setLength(0);
									mensajeImpresion.append(isRespsolimpres ? "imp.okResponsable" : "imp.ok");
								}
							}
						} else {
							// el Autorizante no tiene permiso para aprobar este documento
							mensajeImpresion.setLength(0);
							mensajeImpresion.append("imp.noTienePermisologiaAutorizante");
						}
					}
					con.commit();
					if (con != null) {
						con.close();
					}
					java.util.Date hoy1 = new java.util.Date();
					hoy1 = new Timestamp(hoy1.getTime());
					frmsolicitud.setDatesolicitud(hoy1.toString());
					// ---------------------------------------------------
				}
				putAtributte("namePropietario", autorizante);
				putAtributte("idDocument", idDocument);
				putAtributte("idVersion", idVersion);
			}

			if (!att) {
				Users usuario = (Users) getSessionObject("user");
				LoginUser datossolicitante = new LoginUser(usuario.getUser(), usuario.getPass());
				HandlerDBUser.load(datossolicitante, true);
				putObjectSession("showDocument", forma);
				Collection usuarios = HandlerDBUser.getAllUsers("SolicitudImpresion");
				putObjectSession("usuarios", usuarios);
				putObjectSession("size", String.valueOf(usuarios.size()));
				putObjectSession("datossolicitante", datossolicitante);
				removeAttribute("cerrarventana");
			} else {
				request.getSession().removeAttribute("info");
			}
		}
		if (!att) {
			if (!ToolsHTML.isEmptyOrNull(mensajeImpresion.toString()) && !cmd.equals("")) {
				putAtributte("cerrarventana", "1");
				return goSucces(mensajeImpresion.toString());
			} else {
				return goSucces();
			}
		} else {
			return null;
		}
	}

	public frmsolicitudImpresion insertPrint(BaseDocumentForm forma, int solicitante, Connection con, String typeDoc, String mensaje) throws Exception {
		boolean result = false;
		// se llena la forma que va para la tabla tbl_solicitudimpresion
		frmsolicitudImpresion frmsolicitud = new frmsolicitudImpresion();
		// llenamos datos de la solicitud de impresion en un bean y la llenaremos en la tabla tblsolicitudimpresion
		frmsolicitud.setPath(ToolsHTML.getPath().concat("tmp")); // \\tmp
		frmsolicitud.setNameDocument(forma.getNameDocument());
		frmsolicitud.setCodigo(forma.getPrefix() + forma.getNumber());

		String usergerenciaAutorizante = null;
		String grupogerenciaAutorizante = null;
		String usergerenciaEmailAutorizante = null;
		String[] values1 = null;
		String autorizante = DesigeConf.getProperty("application.userAdmonKey");
		values1 = HandlerDocuments.getFields(new String[] { "nameUser", "idGrupo", "email" }, "person", "idperson", autorizante);
		if (values1 != null) {
			usergerenciaAutorizante = values1[0];
			grupogerenciaAutorizante = values1[1];
			usergerenciaEmailAutorizante = values1[2];
		}

		String gerenciaSolicitante = null;
		String usergerenciaSolicitante = null;
		String usergerenciaEmailSolicitante = null;
		values1 = HandlerDocuments.getFields(new String[] { "cargo", "nameUser", "email" }, "person", "idperson", String.valueOf(solicitante));
		if (values1 != null) {
			gerenciaSolicitante = values1[0];
			usergerenciaSolicitante = values1[1];
			usergerenciaEmailSolicitante = values1[2];
		}

		frmsolicitud.setGerenciaSolicitante(gerenciaSolicitante);
		frmsolicitud.setSolicitante(solicitante);
		frmsolicitud.setAutorizante(Integer.parseInt(autorizante));
		// frmsolicitud.setStatusautorizante(Integer.parseInt(loadsolicitudImpresion.solicitadoprintln));
		// frmsolicitud.setStatusimpresion(Integer.parseInt(loadsolicitudImpresion.solicitadoprintln));
		frmsolicitud.setNumberGen(Integer.parseInt(forma.getIdDocument()));
		frmsolicitud.setNumVer(forma.getNumVer());

		frmsolicitud.setAcumCopias("1");
		// Las copias Siempre van a ser no Controladas por el Sistema
		frmsolicitud.setDestinatarios(null);
		frmsolicitud.setCopias("1");
		int numberDoc = HandlerStruct.proximo("docs", "documents", "numGen");
		int impresionnum = HandlerStruct.proximo("numsolicitud", "tbl_solicitudimpresion", "numsolicitud");
		// el numero de solicitud es el numero de documento nuevo a crear.
		frmsolicitud.setNumsolicitud(numberDoc);

		StringBuffer commentsImpresion = new StringBuffer("");
		StringBuffer comments = new StringBuffer(getMessage("imp.lineacomments0"));
		comments.append(" ").append(getMessage("imp.nocontrolada0"));
		comments.append(getMessage("imp.lineacomments1"));
		comments.append(buscarNomApellUser(String.valueOf(frmsolicitud.getAutorizante())));
		comments.append(getMessage("imp.lineacomments2")).append(buscarNomApellUser(String.valueOf(frmsolicitud.getSolicitante())));

		// Luis Cisneros
		// 19/04/07
		// El Tipo de documento a mostrar no debe ser Solicitud de Impresi�n
		// sino el del documento original:
		// Se cambio en el bundle la clave imp.lineacomments3 para que no tengo la parte final

		// //<br>quien esta solicitando una autorizaci�n de impresi�n para el tipo de documento:Solicitud de Impresi�n
		comments.append(getMessage("imp.lineacomments3"));
		comments.append(typeDoc);

		// comments.append(getMessage("imp.name")).append(frmsolicitud.getNameDocument()).append(getMessage("imp.codigo")).append(frmsolicitud.getCodigo());
		comments.append(getMessage("imp.name")).append(frmsolicitud.getNameDocument());

		comments.append(getMessage("imp.codigo"));
		comments.append(forma.getPrefix() + forma.getNumber());

		// Fin 19/04/07

		commentsImpresion.append(getMessage("imp.nocontrolada"));
		comments.append(getMessage("imp.nocontrolada"));

		commentsImpresion.append(getMessage("imp.cantcopias")).append(frmsolicitud.getAcumCopias()).append("<br>");

		// <br>Comentarios:
		if (!ToolsHTML.isEmptyOrNull(mensaje)) {
			commentsImpresion.append(getMessage("imp.comentariosSolicitud")).append(mensaje).append("<br>");
			comments.append(getMessage("imp.comentariosSolicitud")).append(mensaje).append("<br>");
		}

		frmsolicitud.setComments(commentsImpresion.toString());

		frmsolicitud.setNuevaSolicitud(false);
		frmsolicitud.setStatusautorizante(loadsolicitudImpresion.aprobadoprintlnInt);
		frmsolicitud.setStatusimpresion(loadsolicitudImpresion.impresoprintlnInt);
		// Datos del Documento
		BaseDocumentForm docnuevoforma = new BaseDocumentForm();
		docnuevoforma.setDocPublic("1");
		docnuevoforma.setPrefix("");
		docnuevoforma.setNormISO("0");
		docnuevoforma.setDocProtected("");
		docnuevoforma.setDocOnline("0");
		docnuevoforma.setURL("");
		docnuevoforma.setKeys("");
		docnuevoforma.setDescript("");
		docnuevoforma.setComments(comments.toString());
		docnuevoforma.setNameDocument(forma.getNameDocument());
		docnuevoforma.setOwner(usergerenciaSolicitante);
		// el codigo del documento de solicitud de impresion, no va a tener nada
		docnuevoforma.setNumber(String.valueOf(impresionnum));
		docnuevoforma.setTypeDocument(HandlerDocuments.TypeDocumentsImpresion.trim());
		docnuevoforma.setNumberGen(String.valueOf(frmsolicitud.getNumsolicitud()));

		Hashtable subNodos = new Hashtable();
		Hashtable tree = HandlerStruct.loadAllNodes(null, DesigeConf.getProperty("application.admon"), DesigeConf.getProperty("application.admon"), subNodos,
				null);
		String idNodeDocument = forma.getIdNode() != null ? forma.getIdNode() : "0";
		String idLocation = HandlerStruct.getIdLocationToNode(tree, idNodeDocument);
		String idNodePrintln = null;
		idNodePrintln = crearCarpetaImpresion(idLocation);
		BaseStructForm frmCarpetaPrt = new BaseStructForm();
		frmCarpetaPrt.setIdNode(idNodePrintln);
		boolean existeCarpetaPrintln = true;
		if (ToolsHTML.isEmptyOrNull(idNodePrintln)) {
			frmCarpetaPrt.setNodeType(DesigeConf.getProperty("folderType"));
			frmCarpetaPrt.setIdNodeParent(idLocation);
			frmCarpetaPrt.setDescription(getMessage("imp.descripcarp"));
			frmCarpetaPrt.setName(getMessage("imp.namecarp"));
			frmCarpetaPrt.setNameIcon("menu_folder_closed.gif");
			frmCarpetaPrt.setOwner(HandlerBD.getField("nameUser", "person", "idGrupo", DesigeConf.getProperty("application.admon"), "=", 2,
					Thread.currentThread().getStackTrace()[1].getMethodName()));
			frmCarpetaPrt.setPrefix(getMessage("imp.prefix"));
			try {
				// creamos la carpeta impresion
				existeCarpetaPrintln = HandlerStruct.insert(frmCarpetaPrt);
				// se ha creado la carpeta pintln, pero falta actualizar una variable
				// que la identifica
				// como una carpeta println
				updatecarpetaImpresion(con, frmCarpetaPrt);
			} catch (Exception e) {
				e.printStackTrace();
				existeCarpetaPrintln = false;
			}
		}

		docnuevoforma.setIdNode(frmCarpetaPrt.getIdNode() != null ? frmCarpetaPrt.getIdNode() : "0");
		docnuevoforma.setStatu(1);
		docnuevoforma.setMayorVer(forma.getMayorVer());
		docnuevoforma.setMinorVer(forma.getMinorVer());
		// Si es un Documento de Libre Impresi�n se procede colocar la versi�n como Aprobada
		docnuevoforma.setDateApproved(ToolsHTML.sdf.format(new java.util.Date()));
		docnuevoforma.setDocPublic("0");
		docnuevoforma.setStatu(Integer.parseInt(HandlerDocuments.docApproved));
		docnuevoforma.setDateExpires(null);
		docnuevoforma.setDateExpiresDrafts(null);
		docnuevoforma.setNameFile(null);
		int numVer = HandlerStruct.proximo("versiondoc", "versiondoc", "numver");
		docnuevoforma.setNumVer(numVer);

		java.util.Date date = new java.util.Date();
		Timestamp time = new Timestamp(date.getTime());

		String monthExpireDoc = HandlerParameters.PARAMETROS.getMonthsExpireDocs();
		String unit = HandlerParameters.PARAMETROS.getUnitTimeExpire();

		int value = 0;
		if (ToolsHTML.isNumeric(monthExpireDoc.trim())) {
			value = Integer.parseInt(monthExpireDoc.trim());
		}

		String monthsExpireDrafts = HandlerParameters.PARAMETROS.getMonthsExpireDrafts();
		String unitDrafts = HandlerParameters.PARAMETROS.getUnitTimeExpireDrafts();

		int valueDrafts = 0;
		if (ToolsHTML.isNumeric(monthsExpireDrafts.trim())) {
			valueDrafts = Integer.parseInt(monthsExpireDrafts.trim());
		}
		java.util.Date dateCreate = new java.util.Date();
		Timestamp timeCreate = new Timestamp(dateCreate.getTime());

		HandlerStruct.getPreparedStatementDocumentInsert(docnuevoforma, con, time, timeCreate, numVer);
		HandlerStruct.getPreparementStatementVersionInsert(docnuevoforma, con, value, unit, valueDrafts, unitDrafts, timeCreate, false, null);
		HandlerStruct.saveBD(con, docnuevoforma.getNameFile(), docnuevoforma.getComments(), frmsolicitud.getPath(), docnuevoforma.getNumVer());
		// esta herencia es automatica 2007-11-14 HandlerDocuments.permisosHeredadosDocs(docnuevoforma,Integer.parseInt(docnuevoforma.getNumberGen()) ,con);

		if (frmsolicitud.getNuevaSolicitud()) {
			updatefrmsolicitudImpresionExiste(con, frmsolicitud);
			insertfrmsolicitudImpresion(con, frmsolicitud);
		} else {
			insertfrmsolicitudImpresion(con, frmsolicitud);
		}
		return frmsolicitud;
	}

	public static Hashtable copiasControladas(String usuario, String copia) {
		Hashtable copiasControlada = new Hashtable();
		StringTokenizer usuarios = new StringTokenizer(usuario, ",");
		StringTokenizer copias = new StringTokenizer(copia, ",");
		// System.out.println("!!!!!!!!!!!!!!!" + usuario);
		// System.out.println("!!!!!!!!!!!!!!!" + usuario);
		int i = 0;
		while ((copias.hasMoreElements()) && (usuarios.hasMoreElements())) {
			copiasControlada.put(usuarios.nextToken(), copias.nextToken());
		}
		return copiasControlada;
	}

	public static boolean existepermisoImpresion(String numgen, String solicitante) {
		StringBuffer edit = new StringBuffer();
		boolean swa = false;
		if (!ToolsHTML.isEmptyOrNull(solicitante) && (!ToolsHTML.isEmptyOrNull(numgen))) {
			edit.append("SELECT statusimpresion FROM tbl_solicitudimpresion ");
			edit.append(" WHERE numgen = ").append("?");
			edit.append(" AND solicitante = ").append(solicitante);
			edit.append(" AND statusimpresion = ").append(loadsolicitudImpresion.aprobadoprintln);
			edit.append(" AND active = '").append(Constants.permission).append("'");
			PreparedStatement st = null;
			try {
				Connection con = JDBCUtil.getConnection("existepermisoImpresion");
				st = con.prepareStatement(JDBCUtil.replaceCastMysql(edit.toString()));
				ResultSet rs;
				st.setInt(1, Integer.parseInt(numgen));
				rs = st.executeQuery();
				if (rs.next()) {
					swa = true;
				}
				rs.close();
				st.close();
				con.close();
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
		return swa;
	}

	public static boolean enabledPermisoImpresion(String numgen, String solicitante) {
		StringBuffer edit = new StringBuffer();
		boolean swa = false;
		if (!ToolsHTML.isEmptyOrNull(solicitante) && (!ToolsHTML.isEmptyOrNull(numgen))) {
			edit.append("UPDATE tbl_solicitudimpresion SET statusimpresion=").append(loadsolicitudImpresion.aprobadoprintln);
			edit.append(", statusautorizante=").append(loadsolicitudImpresion.aprobadoprintln);
			edit.append(" WHERE numgen = ? ");
			edit.append(" AND solicitante = ").append(solicitante);
			edit.append(" AND statusimpresion = ").append(loadsolicitudImpresion.solicitadoprintln);
			edit.append(" AND active = '").append(Constants.permission).append("'");
			PreparedStatement st = null;
			try {
				Connection con = JDBCUtil.getConnection("enabledPermisoImpresion");
				st = con.prepareStatement(JDBCUtil.replaceCastMysql(edit.toString()));
				st.setInt(1, Integer.parseInt(numgen));
				int act = st.executeUpdate();
				if (act > 0) {
					swa = true;
				}
				st.close();
				con.close();
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
		return swa;
	}

	public static boolean responseWFPrint(int idWorkFlow) {
		StringBuffer master = new StringBuffer();
		StringBuffer detail = new StringBuffer();
		StringBuffer document = new StringBuffer();
		StringBuffer version = new StringBuffer();
		boolean swa = false;
		if (idWorkFlow > 0) {
			Timestamp fecha = new Timestamp((new java.util.Date()).getTime());
			Timestamp expira = fecha;
			try {
				expira = new Timestamp(ToolsHTML.sdfShowConvert1.parse(Constants.FECHA_EXPIRACION_GENERICA).getTime());
			} catch (ParseException e1) {
				e1.printStackTrace();
			}

			// querys
			master.append("update workflows set statu=?, result=?, datecompleted=? where idworkflow= ? ");
			detail.append("update user_workflows set statu=?, result=?, datereplied=?, comments='Desde expediente/From files' where idworkflow= ? ");

			document.append("update documents set versionPublic=(select max(numver) from versiondoc where numdoc=?), ");
			document.append("datePublic=?,docPublic='0',statu=?,statuant=?,lastoperation=?, ");
			document.append("lastversionapproved=(select max(numver) from versiondoc where numdoc=?) ");
			document.append("where numgen=? ");

			version.append("update versiondoc set dateApproved=?,dateExpires=?,statu=?,statuHist=? where numver=?");
			//

			PreparedStatement st = null;
			Connection con = null;
			try {
				con = JDBCUtil.getConnection("responseWFPrint");
				// consultamos el id del documento
				st = con.prepareStatement(JDBCUtil.replaceCastMysql("select idDocument from workflows where idworkflow=?"));
				st.setInt(1, idWorkFlow);
				ResultSet rs = st.executeQuery();
				if (rs.next()) {
					String numgen = rs.getString("idDocument");

					con.setAutoCommit(false);

					st = con.prepareStatement(JDBCUtil.replaceCastMysql(master.toString()));
					st.setInt(1, Integer.parseInt(HandlerWorkFlows.response));
					st.setInt(2, Integer.parseInt(HandlerWorkFlows.wfuAcepted));
					st.setTimestamp(3, fecha);
					st.setInt(4, idWorkFlow);
					st.executeUpdate();

					st = con.prepareStatement(JDBCUtil.replaceCastMysql(detail.toString()));
					st.setInt(1, Integer.parseInt(HandlerWorkFlows.response));
					st.setInt(2, Integer.parseInt(HandlerWorkFlows.wfuAcepted));
					st.setTimestamp(3, fecha);
					st.setInt(4, idWorkFlow);
					st.executeUpdate();

					st = con.prepareStatement(JDBCUtil.replaceCastMysql(document.toString()));
					st.setInt(1, Integer.parseInt(numgen));
					st.setTimestamp(2, fecha);
					st.setInt(3, Integer.parseInt(HandlerDocuments.docApproved));
					st.setInt(4, Integer.parseInt(HandlerDocuments.docInApproved));
					st.setInt(5, Integer.parseInt(HandlerDocuments.lastApproved));
					st.setInt(6, Integer.parseInt(numgen));
					st.setInt(7, Integer.parseInt(numgen));
					st.executeUpdate();

					// recuperamos la version maxima de la tabla versiondoc
					String idMaxVersionDoc = JDBCUtil
							.executeQueryRetornaIds(new StringBuffer("select max(numver) from versiondoc where numdoc=").append(numgen));

					st = con.prepareStatement(JDBCUtil.replaceCastMysql(version.toString()));
					st.setTimestamp(1, fecha);
					st.setTimestamp(2, expira);
					st.setInt(3, Integer.parseInt(HandlerDocuments.docApproved));
					st.setInt(4, Integer.parseInt(HandlerDocuments.docApproved));
					st.setInt(5, Integer.parseInt(idMaxVersionDoc));
					st.executeUpdate();

					con.commit();
					swa = true;
				}
			} catch (Exception ex) {
				try {
					con.rollback();
				} catch (SQLException e) {
					e.printStackTrace();
				}
				ex.printStackTrace();
			} finally {
				try {
					if (st != null)
						st.close();
				} catch (Exception e) {
				}
				try {
					if (con != null)
						con.close();
				} catch (Exception e) {
				}
			}
		}
		return swa;
	}

	public static int getNumberOfCopiesToPrint(String numgen, String solicitante) {
		StringBuffer edit = new StringBuffer();
		String[] copies;
		int numberCopies = 1;
		if (!ToolsHTML.isEmptyOrNull(solicitante) && (!ToolsHTML.isEmptyOrNull(numgen))) {
			edit.append("SELECT copias FROM tbl_solicitudimpresion ");
			edit.append(" WHERE numgen = ").append("?");
			edit.append(" AND solicitante = ").append(solicitante);
			edit.append(" AND statusimpresion = ").append(loadsolicitudImpresion.aprobadoprintln);
			edit.append(" AND active = ").append(Constants.permission);
			PreparedStatement st = null;
			try {
				Connection con = JDBCUtil.getConnection("getNumberOfCopiesToPrint");
				st = con.prepareStatement(JDBCUtil.replaceCastMysql(edit.toString()));
				ResultSet rs;
				st.setInt(1, Integer.parseInt(numgen));
				rs = st.executeQuery();
				if (rs.next()) {
					numberCopies = 0;
					copies = rs.getString("copias").split(",");
					for (int i = 0; i < copies.length; i++) {
						if (copies[i] != null && !copies[i].trim().equals("")) {
							try {
								numberCopies += Integer.parseInt(copies[i]);
							} catch (Exception e) {
								// System.out.println("Error al convertir el numero de copias");
								e.printStackTrace();
							}
						}
					}
				}
				rs.close();
				st.close();
				con.close();
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
		return numberCopies;
	}

	public static int getNumberOfCopiesToPrintAutorizante(String numgen, String autorizante) {
		StringBuffer edit = new StringBuffer();
		String[] copies;
		int numberCopies = 1;
		if (!ToolsHTML.isEmptyOrNull(autorizante) && (!ToolsHTML.isEmptyOrNull(numgen))) {
			edit.append("SELECT copias FROM tbl_solicitudimpresion ");
			edit.append(" WHERE numgen = ").append("?");
			edit.append(" AND autorizante = ").append(autorizante);
			edit.append(" AND statusimpresion = ").append(loadsolicitudImpresion.aprobadoprintln);
			edit.append(" AND active = ").append(Constants.permission);
			PreparedStatement st = null;
			try {
				Connection con = JDBCUtil.getConnection("getNumberOfCopiesToPrintAutorizante");
				st = con.prepareStatement(JDBCUtil.replaceCastMysql(edit.toString()));
				ResultSet rs;
				st.setInt(1, Integer.parseInt(numgen));
				rs = st.executeQuery();
				if (rs.next()) {
					numberCopies = 0;
					copies = rs.getString("copias").split(",");
					for (int i = 0; i < copies.length; i++) {
						if (copies[i] != null && !copies[i].trim().equals("")) {
							try {
								numberCopies += Integer.parseInt(copies[i]);
							} catch (Exception e) {
								// System.out.println("Error al convertir el numero de copias");
								e.printStackTrace();
							}
						}
					}
				}
				rs.close();
				st.close();
				con.close();
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
		return numberCopies;
	}

	public static String getIdSolicitante(String numgen, String autorizante) {
		StringBuffer edit = new StringBuffer();
		String idSolicitante = "";
		if (!ToolsHTML.isEmptyOrNull(autorizante) && (!ToolsHTML.isEmptyOrNull(numgen))) {
			edit.append("SELECT solicitante FROM tbl_solicitudimpresion ");
			edit.append(" WHERE numgen = ").append("?");
			edit.append(" AND autorizante = ").append(autorizante);
			edit.append(" AND statusimpresion = ").append(loadsolicitudImpresion.aprobadoprintln);
			edit.append(" AND active = ").append(Constants.permission);
			PreparedStatement st = null;
			try {
				Connection con = JDBCUtil.getConnection("getIdSolicitante");
				st = con.prepareStatement(JDBCUtil.replaceCastMysql(edit.toString()));
				ResultSet rs;
				st.setInt(1, Integer.parseInt(numgen));
				rs = st.executeQuery();
				if (rs.next()) {
					idSolicitante = rs.getString(1);
				}
				rs.close();
				st.close();
				con.close();
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
		return idSolicitante;
	}

	public static String getCommentsToPrint(String numgen, String solicitante) {
		StringBuffer edit = new StringBuffer();
		String comments = "";
		if (!ToolsHTML.isEmptyOrNull(solicitante) && (!ToolsHTML.isEmptyOrNull(numgen))) {
			edit.append("SELECT comments FROM tbl_solicitudimpresion ");
			edit.append(" WHERE numgen = ").append("?");
			edit.append(" AND solicitante = ").append(solicitante);
			edit.append(" AND statusimpresion = ").append(loadsolicitudImpresion.aprobadoprintln);
			edit.append(" AND active = ").append(Constants.permission);
			PreparedStatement st = null;
			try {
				Connection con = JDBCUtil.getConnection("getCommentsToPrint");
				st = con.prepareStatement(JDBCUtil.replaceCastMysql(edit.toString()));
				ResultSet rs;
				st.setInt(1, Integer.parseInt(numgen));
				rs = st.executeQuery();
				if (rs.next()) {
					comments = rs.getString(1);
				}
				rs.close();
				st.close();
				con.close();
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
		return comments;
	}

	public static String getCommentsToPrintAutorizante(String numgen, String autorizante) {
		StringBuffer edit = new StringBuffer();
		String comments = "";
		if (!ToolsHTML.isEmptyOrNull(autorizante) && (!ToolsHTML.isEmptyOrNull(numgen))) {
			edit.append("SELECT comments FROM tbl_solicitudimpresion ");
			edit.append(" WHERE numgen = ").append("?");
			edit.append(" AND autorizante = ").append(autorizante);
			edit.append(" AND statusimpresion = ").append(loadsolicitudImpresion.aprobadoprintln);
			edit.append(" AND active = ").append(Constants.permission);
			PreparedStatement st = null;
			try {
				Connection con = JDBCUtil.getConnection("getCommentsToPrintAutorizante");
				st = con.prepareStatement(JDBCUtil.replaceCastMysql(edit.toString()));
				ResultSet rs;
				st.setInt(1, Integer.parseInt(numgen));
				rs = st.executeQuery();
				if (rs.next()) {
					comments = rs.getString(1);
				}
				rs.close();
				st.close();
				con.close();
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
		return comments;
	}

	public static frmsolicitudImpresion updatepermisoImpresion(String numgen, String solicitante, String numver) {
		frmsolicitudImpresion forma = null;
		StringBuffer edit = new StringBuffer();
		StringBuffer edit2 = new StringBuffer();

		edit.append("SELECT datesolicitud,solicitante,copias,destinatarios,comments,");
		switch (Constants.MANEJADOR_ACTUAL) {
		case Constants.MANEJADOR_MSSQL:
			edit.append("(p.apellidos + ' ' + p.nombres) AS nombre ");
			break;
		case Constants.MANEJADOR_POSTGRES:
			edit.append("(p.apellidos || ' ' || p.nombres) AS nombre ");
			break;
		case Constants.MANEJADOR_MYSQL:
			edit.append("CONCAT(p.apellidos , ' ' , p.nombres) AS nombre ");
			break;
		}
		edit.append(" FROM tbl_solicitudimpresion,person p ");
		edit.append(" WHERE numgen = ").append(numgen);
		edit.append(" AND p.idPerson = solicitante ");
		edit.append(" AND statusimpresion = ").append(loadsolicitudImpresion.aprobadoprintln);
		edit.append(" AND active = '").append(Constants.permission).append("'");

		edit2.append(edit.toString()).append(" AND numVer=").append(numver);
		edit.append(" AND solicitante = ").append(solicitante);

		PreparedStatement st = null;
		try {
			CachedRowSet crsSolicitud = JDBCUtil.executeQuery(edit, Thread.currentThread().getStackTrace()[1].getMethodName());
			if (!crsSolicitud.next()) {
				edit.setLength(0);
				edit.append(edit2.toString());
			}

			Connection con = JDBCUtil.getConnection(Thread.currentThread().getStackTrace()[1].getMethodName());
			con.setAutoCommit(false);
			st = con.prepareStatement(JDBCUtil.replaceCastMysql(edit.toString()));
			ResultSet rs;
			rs = st.executeQuery();
			if (rs.next()) {
				forma = new frmsolicitudImpresion();
				forma.setCopias(rs.getString("copias"));
				String dateSolicitud = ToolsHTML.sdf.format(rs.getTimestamp("datesolicitud"));
				forma.setDatesolicitud(ToolsHTML.formatDateShow(dateSolicitud, true));
				forma.setNameSolicitante(rs.getString("nombre"));
				forma.setComments(rs.getString("comments"));
				// String destinatarios = rs.getString("destinatarios");
				// //Es una Copia Controlada
				// if (!ToolsHTML.isEmptyOrNull(destinatarios)) {
				//
				// }
				// el usuario tiene permisologia para imprimir
				// actualizamos su base de datos
				StringBuffer update = new StringBuffer("UPDATE tbl_solicitudimpresion SET statusimpresion=");
				update.append(loadsolicitudImpresion.impresoprintln);
				update.append(" WHERE numgen = ").append(numgen);
				update.append(" AND solicitante = ").append(rs.getString("solicitante"));
				update.append(" AND statusimpresion = ").append(loadsolicitudImpresion.aprobadoprintln);
				update.append(" AND active = '").append(Constants.permission).append("'");
				st = con.prepareStatement(JDBCUtil.replaceCastMysql(update.toString()));
				st.execute();
			}
			con.setAutoCommit(true);
			st.close();
			con.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return forma;
	}

	public synchronized static boolean updatefrmsolicitudImpresion(Connection con, frmsolicitudImpresion forma) {
		StringBuffer edit = new StringBuffer();
		edit.append("UPDATE tbl_solicitudimpresion ")
				.append(" SET datesolicitud = ?, autorizante = ?,statusautorizante=?,statusimpresion=?,destinatarios=?,active=?, copias=?");
		edit.append(" WHERE solicitante = ").append(forma.getSolicitante()).append(" AND ").append(" numgen=").append(forma.getNumberGen());
		edit.append(" AND ").append(" numVer=").append(forma.getNumVer());
		edit.append(" AND active = ").append(Constants.permission);
		PreparedStatement st = null;
		boolean swa = false;
		// System.out.println("[updatefrmsolicitudImpresion]"+edit.toString());
		try {
			st = con.prepareStatement(JDBCUtil.replaceCastMysql(edit.toString()));
			java.util.Date hoy = new java.util.Date();
			st.setTimestamp(1, new Timestamp(hoy.getTime()));
			st.setInt(2, forma.getAutorizante());
			st.setInt(3, forma.getStatusautorizante());
			st.setInt(4, forma.getStatusimpresion());
			st.setString(5, forma.getDestinatarios());
			st.setInt(6, Constants.permission);
			st.setString(7, forma.getCopias());
			swa = st.executeUpdate() > 0;
			// return swa;

		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return swa;
	}

	public synchronized static boolean updatecarpetaImpresion(Connection con, BaseStructForm forma) {
		StringBuffer updateExiste = new StringBuffer("UPDATE struct  ");
		updateExiste.append(" set toImpresion =").append(loadsolicitudImpresion.folderPrintln);
		updateExiste.append(" WHERE idNode = ").append(forma.getIdNode());
		PreparedStatement st = null;
		boolean result = false;
		try {
			// System.out.println("[updatecarpetaImpresion]"+updateExiste.toString());
			st = con.prepareStatement(JDBCUtil.replaceCastMysql(updateExiste.toString()));
			result = st.executeUpdate() > 0;
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return result;
	}

	public synchronized static boolean updatefrmsolicitudImpresionExiste(Connection con, frmsolicitudImpresion forma) {
		StringBuffer updateExiste = new StringBuffer("UPDATE tbl_solicitudimpresion  ");
		updateExiste.append(" set statusimpresion=").append(loadsolicitudImpresion.canceladoprintln);
		updateExiste.append(" WHERE solicitante = ").append(forma.getSolicitante()).append(" AND ").append(" numgen=").append(forma.getNumberGen());
		updateExiste.append(" AND ").append(" numVer=").append(forma.getNumVer());
		updateExiste.append(" AND active = ").append(Constants.permission);
		PreparedStatement st = null;
		boolean result = false;
		try {
			// System.out.println("[updatefrmsolicitudImpresionExiste]"+updateExiste.toString());
			st = con.prepareStatement(JDBCUtil.replaceCastMysql(updateExiste.toString()));
			result = st.executeUpdate() > 0;
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return result;
	}

	public synchronized static boolean insertfrmsolicitudImpresion(Connection con, frmsolicitudImpresion forma) {
		StringBuffer insert = new StringBuffer("INSERT INTO tbl_solicitudimpresion  ");
		insert.append(
				"(numsolicitud,datesolicitud,solicitante,autorizante,statusautorizante,statusimpresion,numgen,numVer,destinatarios,active,copias,comments)");
		insert.append(" VALUES( ?, ? , ? , ? , ? , ? , ? , ? , ?, ?, ? , ?  )");
		PreparedStatement st = null;
		boolean result = false;
		try {
			// System.out.println("[insertfrmsolicitudImpresion]"+insert.toString());
			st = con.prepareStatement(JDBCUtil.replaceCastMysql(insert.toString()));
			st.setInt(1, forma.getNumsolicitud());
			java.util.Date hoy = new java.util.Date();
			st.setTimestamp(2, new Timestamp(hoy.getTime()));
			st.setInt(3, forma.getSolicitante());
			st.setInt(4, forma.getAutorizante());
			st.setInt(5, forma.getStatusautorizante());
			st.setInt(6, forma.getStatusimpresion());
			st.setInt(7, forma.getNumberGen());
			st.setInt(8, forma.getNumVer());
			st.setString(9, forma.getDestinatarios());
			st.setInt(10, Constants.permission);
			st.setString(11, forma.getCopias());
			st.setString(12, forma.getComments());
			result = st.executeUpdate() > 0;
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return result;
	}

	private boolean existefrmsolicitudImpresion(Connection con, frmsolicitudImpresion forma) {
		boolean sw = false;
		try {
			PreparedStatement st;
			ResultSet rs;
			StringBuffer edit = new StringBuffer("");
			edit.append("SELECT  statusimpresion FROM tbl_solicitudimpresion ");
			edit.append(" WHERE solicitante = ").append(forma.getSolicitante()).append(" AND ").append(" numgen=").append(forma.getNumberGen());
			// edit.append(" AND ").append(" numVer=").append(forma.getNumVer());
			edit.append(" AND active = ").append(Constants.permission);
			edit.append(" AND ");
			edit.append("( ");
			edit.append("   statusimpresion=").append(loadsolicitudImpresion.solicitadoprintln);
			edit.append(" OR ");
			edit.append("   statusimpresion=").append(loadsolicitudImpresion.aprobadoprintln);
			edit.append(" ) ");
			st = con.prepareStatement(JDBCUtil.replaceCastMysql(edit.toString()));
			// st.setInt(1,forma.getNumberGen());
			rs = st.executeQuery();
			if (rs.next()) {
				if (rs.getString("statusimpresion").equalsIgnoreCase(String.valueOf(loadsolicitudImpresion.aprobadoprintln))) {
					// aqui se cancelara la existente aprobada y se mandara hacer una nueva solicitud.
					forma.setNuevaSolicitud(true);
				} else if (rs.getString("statusimpresion").equalsIgnoreCase(String.valueOf(loadsolicitudImpresion.solicitadoprintln))) {
					// ya existe una solicitud pendiente que necesita ser aprobada o rechazad.., no le dejara hacer
					// nada al usuario
					sw = true;
				}
				// throw new ApplicationExceptionChecked("E0038");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return sw;
	}

	public static void saveBDFilePrint(frmsolicitudImpresion forma) throws Exception {
		// saveBD(con,forma.getNameFile(),forma.getComments(),path,forma.getNumVer());
		Connection con = JDBCUtil.getConnection("saveBDFilePrint");
		try {
			// con.setAutoCommit(false);
			String nameFile;
			String[] usuarios;

			// usuarios=buscarNomApellUser(String.valueOf(forma.getSolicitante()));
			StringBuffer comentarios = new StringBuffer("");
			comentarios.append("Fecha de la solicitud de Impresi�n:").append(forma.getDatesolicitud()).append("\n\r");
			comentarios.append("Solicitado por:").append(buscarNomApellUser(String.valueOf(forma.getSolicitante()))).append("\n\r");
			comentarios.append("Tipo de Documento:").append("Solicitud de Impresi�n").append("\n\r");
			comentarios.append("Codigo:").append(forma.getCodigo()).append("\n\r");
			comentarios.append("Nombre:").append(forma.getNameDocument()).append("\n\r");
			// en caso de ser controladas
			comentarios.append("Solicitante:").append(forma.getGerenciaSolicitante()).append("");
			comentarios.append("Destinatarios:").append(buscarNomApellUser(String.valueOf(forma.getDestinatarios()))).append("\n");
			comentarios.append("Cantidad de Copias:").append(forma.getAcumCopias()).append("\n");

			FileOutputStream out = new FileOutputStream(forma.getPath() + File.separator + DesigeConf.getProperty("enlinea"));
			// Connect print stream to the output stream
			PrintStream p = new PrintStream(out);
			p.println(comentarios.toString());
			p.close();
			nameFile = DesigeConf.getProperty("enlinea");

			// System.out.println("Salvando Archivo");
			File fichero = new File(forma.getPath() + File.separator + nameFile);
			FileInputStream streamEntrada = new FileInputStream(fichero);

			// en caso de habilitarse esta opcion
			// recordar que los archivos se almacenan comprimidos
			/*
			 * PreparedStatement pstmt = con.prepareStatement("UPDATE tbl_solicitudimpresion   SET docenlinea  = ? WHERE numsolicitud  = ?");
			 * pstmt.setBinaryStream(1, streamEntrada, (int)fichero.length()); pstmt.executeUpdate(); pstmt.close(); streamEntrada.close(); fichero.delete();
			 */
			// con.setAutoCommit(true);
		} catch (Exception e) {
			e.printStackTrace();
			con.rollback();
		} finally {
			if (con != null) {
				con.close();
			}
		}
	}

	public static String getFieldSolicitudImpresion(String field, int idDocument, Connection con, String usuario) {
		String salida = "";
		PreparedStatement stsql = null;
		ResultSet rs = null;
		try {
			StringBuffer sql = new StringBuffer(50);
			sql.append("SELECT ").append(field).append(" FROM tbl_solicitudimpresion WHERE numgen = ").append(idDocument);
			sql.append(" AND solicitante = ").append(usuario);
			sql.append(" AND statusimpresion = ").append(loadsolicitudImpresion.aprobadoprintln);
			stsql = con.prepareStatement(JDBCUtil.replaceCastMysql(sql.toString()));
			rs = stsql.executeQuery();
			if (rs != null) {
				if (rs.next()) {
					salida = rs.getString(field) != null ? rs.getString(field) : "";
					return salida;
				}
				rs.close();
			}
			if (stsql != null) {
				stsql.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return salida;
	}

	public static String buscarNomApellUser(String usuarios) {
		StringTokenizer stk = new StringTokenizer(usuarios, ",");
		String[] retornar = new String[stk.countTokens()];
		StringBuffer str = new StringBuffer("");
		try {
			String idPerson = "";
			String nombres;
			int i = 0;
			String[] values1 = null;
			while (stk.hasMoreTokens()) {
				idPerson = stk.nextToken();
				values1 = HandlerDocuments.getFields(new String[] { "nombres", "apellidos" }, "person", "idperson", String.valueOf(idPerson), false);

				if (values1 != null) {
					nombres = values1[0] + " " + values1[1];
					retornar[i++] = nombres;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		for (int i = 0; i < retornar.length; i++) {
			if (i + 1 < retornar.length)
				str.append(retornar[i]).append(",");
			else
				str.append(retornar[i]);
		}
		return str.toString();

	}

	public static void tienePermisoImpresion(BaseDocumentForm forma, Connection conn, Users usuario, String idNode, Hashtable security) {
		// buscamos la permisologia del usuario si tiene derecho a mprimir el documento
		String autorizadoImprimir1 = loadsolicitudImpresion.getFieldSolicitudImpresion("statusimpresion", Integer.parseInt(forma.getIdDocument()), conn,
				String.valueOf(usuario.getIdPerson()));
		int autorizadoImprimir = 0;
		if (!ToolsHTML.isEmptyOrNull(autorizadoImprimir1)) {
			autorizadoImprimir = Integer.parseInt(autorizadoImprimir1);
		}
		forma.setIdNode(idNode);
		// PermissionUserForm perm = (PermissionUserForm)security.get(forma.getIdNode());
		forma.setImprimir("");
		if (autorizadoImprimir == Integer.parseInt(loadsolicitudImpresion.aprobadoprintln)) {
			forma.setImprimir("imprimir");
		} else {
			forma.setImprimir("");
		}
	}

	public static String crearCarpetaImpresion(String idLocation) {
		String salida = null;
		Connection con = null;
		PreparedStatement stsql = null;
		ResultSet rs = null;
		try {
			con = JDBCUtil.getConnection(Thread.currentThread().getStackTrace()[1].getMethodName());
			 /// System.out.println(usuario.toString()+"=usuario*2005-11-07");
			//// saymon*************************************************************************************************");
			StringBuffer sql = new StringBuffer(100);
			sql.append("SELECT IdNode FROM struct WHERE IdNodeParent='").append(idLocation).append("'");
			sql.append(" AND toImpresion ='").append(loadsolicitudImpresion.folderPrintln).append("'");
			// System.out.println("[crearCarpetaImpresion]"+sql.toString());
			stsql = con.prepareStatement(JDBCUtil.replaceCastMysql(sql.toString()));
			rs = stsql.executeQuery();
			if (rs != null) {
				if (rs.next()) {
					salida = rs.getString("IdNode");
				}
				if (rs != null) {
					rs.close();
				}

			}
			if (stsql != null) {
				stsql.close();
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (con != null) {
				try {
					con.close();
				} catch (Exception e) {
					e.printStackTrace();
				}

			}
		}
		return salida;
	}
}
