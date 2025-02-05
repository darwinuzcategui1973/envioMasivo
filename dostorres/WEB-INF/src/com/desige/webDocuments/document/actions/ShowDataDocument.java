package com.desige.webDocuments.document.actions;

import java.sql.Connection;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.ResourceBundle;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.desige.webDocuments.document.forms.BaseDocumentForm;
import com.desige.webDocuments.document.forms.BeanVersionForms;
import com.desige.webDocuments.document.forms.DocumentsCheckOutsBean;
import com.desige.webDocuments.persistent.managers.HandlerBD;
import com.desige.webDocuments.persistent.managers.HandlerDBUser;
import com.desige.webDocuments.persistent.managers.HandlerDocuments;
import com.desige.webDocuments.persistent.managers.HandlerGrupo;
import com.desige.webDocuments.persistent.managers.HandlerMessages;
import com.desige.webDocuments.persistent.managers.HandlerParameters;
import com.desige.webDocuments.persistent.managers.HandlerStruct;
import com.desige.webDocuments.persistent.managers.HandlerWorkFlows;
import com.desige.webDocuments.persistent.utils.JDBCUtil;
import com.desige.webDocuments.seguridad.forms.PermissionUserForm;
import com.desige.webDocuments.structured.forms.BaseStructForm;
import com.desige.webDocuments.utils.ApplicationExceptionChecked;
import com.desige.webDocuments.utils.Constants;
import com.desige.webDocuments.utils.DesigeConf;
import com.desige.webDocuments.utils.ToolsHTML;
import com.desige.webDocuments.utils.beans.SuperAction;
import com.desige.webDocuments.utils.beans.Users;
import com.desige.webDocuments.workFlows.forms.DataUserWorkFlowForm;
import com.focus.qweb.facade.AuditFacade;
import com.focus.qweb.to.AuditTO;
import com.focus.qweb.to.TransactionTO;

/**
 * Title: ShowDataDocument.java<br>
 * Copyright: (c) 2003 Consis International<br>
 * Company: Consis International (CON)<br>
 * @author Nelson Crespo (NC)
 * @author Sim�n Rodrigu�z (SR)
 * @version Acsel-e v2.2
 * <br>
 * Changes:<br>
 * <ul>
 *      <li> 23/07/2004 (NC) Creation </li>
 *      <li> 14/05/2005 (NC) Uso del nuevo m�todo ToolsHTML.checkTree </li>
 *      <li> 22/05/2005 (NC) Se modific� para el manejo din�mico de los prefijos </li>
 *      <li> 06/06/2005 (SR) Se modific� para la session showImpReg, para poder imprimir.</li>
 *      <li> 19/07/2005 (SR) Se creo la sessi�n userPending.</li>
 *      <li> 20/07/2005 (NC) Se coment� l�nea para evitar descargar el documento al hacer check Out</li>
 *      <li> 30/05/2006 (NC) Se agregaron cambios para mostrar la carpeta contentiva del Documento que se visualiza </li>
 *      <li> 29/06/2006 (SR) Se valida el numero de versiones a mostrar </li>
 *      <li> 30/06/2006 (NC) se Agreg� el uso del Log y cambios para mostrar los documentos vinculados.</li> 
 *      <li> 13/07/2006 (NC) Cambios para heredar o no los Prefijos de las Carpetas </li>
 * <ul>
 */
public class ShowDataDocument extends SuperAction {
    private static final Logger log = LoggerFactory.getLogger(ShowDataDocument.class);
    
	public synchronized ActionForward execute(ActionMapping mapping,
                                 ActionForm form, HttpServletRequest request,
                                 HttpServletResponse response) {

        super.init(mapping,form,request,response);
        BaseDocumentForm forma = new BaseDocumentForm();
        forma.setIdDocument(getParameter("idDocument"));
        forma.setNumberGen(getParameter("idDocument"));
        try {
            Users user = getUserSession();
            String paramFrom = getParameter("from");
            //revisamos si la variable que viene es de ActiveFactory en Administracion.
            String vengoDeActiveFactory=getParameter("vengoDeActiveFactory");
            //para regresar en cancelar en el form del jsp.
            if (!ToolsHTML.isEmptyOrNull(paramFrom)) {
                if ("1".equalsIgnoreCase(vengoDeActiveFactory)){
                    putAtributte("toBack","loadActiveFactory.do");
                    putObjectSession("toBack","loadActiveFactory.do");
                }else{
                    putAtributte("toBack",paramFrom);
                    putObjectSession("toBack",paramFrom);
                }
            }
            String vengoDeBuscar = getParameter("from");
           // if(getSessionObject(Constants.MODULO_ACTIVO)!=null && getSessionObject(Constants.MODULO_ACTIVO).equals(Constants.MODULO_LISTA_MAESTRA)) {
	       //     if(vengoDeBuscar==null && getSessionObject("vengoDeBuscar")!=null) {
	        //    	vengoDeBuscar = (String)getSessionObject("vengoDeBuscar");
	         //   }
           // }
            
            putObjectSession("vengoDeBuscar", vengoDeBuscar);
            
            HandlerStruct.loadDocument(forma,true,false,null, request);
            //System.out.println(forma.getIdDocument());
            
            boolean isAdmon = user.getIdGroup().equalsIgnoreCase(DesigeConf.getProperty("application.admon"));
            //Se Revisa la Seguridad del Usuario sobre el Documento
            //Para verificar si tiene permisos para ver...
            PermissionUserForm perm = null;
            ToolsHTML tools = new ToolsHTML();
            perm = tools.getSecurityUserInDoc(user,forma.getIdDocument(),forma.getIdNode());
//            perm = HandlerDBUser.getSecurityForIDUserInDocs(user.getUser(),forma.getIdDocument());
//                if (perm==null) {
//                    //Se Carga la Seguridad para el Grupo
//                    //System.out.println("Seguridad del Grupo sobre el Documento");
//                    perm = HandlerGrupo.getSecurityForIDGroupInDoc(user.getIdGroup(),forma.getIdDocument());
//                    if (perm==null) {
//                        //Se Carga la Seguridad para el Usuario a Nivel de Carpeta
//                        //System.out.println("Seguridad del Usuario sobre la Estructura");
//                        perm = HandlerStruct.getSecurityForIDUserInStruct(user.getIdPerson(),forma.getIdNode());
//                        if (perm==null) {
//                            //System.out.println("Seguridad del Grupo sobre la Estructura");
//                            perm = HandlerStruct.getSecurityForIDGroupInStruct(user.getIdGroup(),forma.getIdNode());
//                            //Se Carga la Seguridad para el Usuario a Nivel de Grupos
//                        }
//                    }
//                }
                //obtenemos toda la seguridad del los documentos de dicho usuario
//                Hashtable securityDocs = (Hashtable)getSessionObject("securityDocs");
//                securityDocs = ToolsHTML.checkDocsSecurity(securityDocs,user,null);
//                if (securityDocs!=null) {
//                    putObjectSession("securityDocs",securityDocs);
//                }
                //comprobamos la seguridad del documento
//                BaseDocumentForm formaSearch = new BaseDocumentForm();
//                PermissionUserForm securityDocsfrm = null;
//                if (!ToolsHTML.isEmptyOrNull(forma.getIdDocument())) {
//                    securityDocsfrm=securityDocs.get(forma.getIdDocument())!=null?(PermissionUserForm)securityDocs.get(forma.getIdDocument()):null;
//                }
//                if (securityDocsfrm!=null) {
//                    //cargamos toda la seguridad del documento
//                    ToolsHTML.checkDocsSecurityLoad(securityDocsfrm,formaSearch);
//                    if (!"1".equalsIgnoreCase(String.valueOf(securityDocsfrm.getToViewDocs()))) {
//                        putObjectSession("error",rb.getString("sch.notDocsView"));
//                        return goTo("searchdocindividual");
//                    }
//                }
            if (vengoDeBuscar!=null && (("searchDocument.jsp?expand=false".equalsIgnoreCase(vengoDeBuscar)) || ("published.jsp".equalsIgnoreCase(vengoDeBuscar)) || vengoDeBuscar.startsWith("searchFiles.jsp"))) {
//                BaseDocumentForm formaSearch = new BaseDocumentForm();
                if (perm!=null){
//                    ToolsHTML.checkDocsSecurityLoad(perm,formaSearch);
                    if (!"1".equalsIgnoreCase(String.valueOf(perm.getToViewDocs()))&&(!isAdmon)) {
                        putObjectSession("error",getMessage("sch.notDocsView"));
                        if (vengoDeBuscar.startsWith("published.jsp")){
                        	return goTo("searchpublicdoc");
                        }else if (vengoDeBuscar.startsWith("searchDocument.jsp")){
                        	return goTo("searchdocindividual");
                        }else if (vengoDeBuscar.startsWith("searchFiles.jsp")){
                        	return goTo("error");
                        }
                    }
                }
            }
            if ("true".equalsIgnoreCase(getParameter("isFlexFlow"))) {
                putAtributte("isFlexFlow","true");
            }
//            String paramFrom = getParameter("from");
//            if (!ToolsHTML.isEmptyOrNull(paramFrom)) {
//                putAtributte("toBack",paramFrom);
//            }
            if (getParameter("idWF")==null) {
                removeObjectSession("idWF");
            }
            // a veces los flujos quedan incompletos o no terminados y el status de la version del documento queda aprobada
            //o queda revisado y el statu del documento queda en revision o aprobacion , los suuarios del flujo
            //ya cerraron o cancelaron el workflow y sin embargo el workflow esta como pendiente
            //este parch resolvera el problema
            HandlerDocuments.verifWokflowCerroConExito(forma);
              //forma.getNumberGen()
            //--------------------------------------------------------------------------------------------------------------

            boolean downLoadFile = Boolean.parseBoolean(getParameter("downLast"));
            boolean downLoadEraser = Boolean.parseBoolean(getParameter("downLastEraser"));
			boolean loadMayorVersion = getParameter("downFile")!=null;
            Hashtable tree = (Hashtable)getSessionObject("tree");
            Hashtable subNodos = (Hashtable)getSessionObject("subNodos");
            if (subNodos==null) {
                subNodos = new Hashtable();
            }
//            tree = ToolsHTML.checkTree(tree,user,subNodos);
//            if (subNodos!=null&&subNodos.size() > 0) {
//                putObjectSession("subNodos",subNodos);
//            }
//            HandlerStruct.loadDocument(forma,true,false,tree);
            //correccion, en caso que checkout y el documents tengan sttaus daniados
            HandlerDocuments.BuscarCheckOutDaniados(forma);
            //Si La estructuta no se encuentra Cargada se procede a Carga la Informaci�n de la Misma
//            Hashtable tree = (Hashtable)getSessionObject("tree");
            boolean showStruct = false;
            if (tree==null||"true".equalsIgnoreCase(getParameter("showStruct"))) {
//                if (tree==null){
//                    showStruct = true;
//                }
                //System.out.println("Cargando estructura para visualizar el Documento....");
//                StringBuffer idStructs = new StringBuffer(50);
//                idStructs.append("1");
                tree = new Hashtable();
                subNodos = new Hashtable();
//                Hashtable security = HandlerGrupo.getAllSecurityForGroup(user.getIdGroup(),idStructs);
//                HandlerDBUser.getAllSecurityForUser(user.getIdPerson(),security,idStructs);
//                putObjectSession("security",security);
//                tree = HandlerStruct.loadAllNodes(security,user.getUser(),user.getIdGroup(),subNodos,idStructs.toString());
                Hashtable security = null;
                if (!isAdmon) {
                    StringBuffer idStructs = new StringBuffer(50);
                    idStructs.append("1");
                    security = HandlerGrupo.getAllSecurityForGroup(user.getIdGroup(),idStructs);
                    //Se Carga la Seguridad por Usuario Filtrando aquellos Nodos en Donde el Usuario no
                    //puede ver Carpetas
                    HandlerDBUser.getAllSecurityForUser(user.getIdPerson(),security,idStructs);
                    tree = HandlerStruct.loadAllNodes(security,user.getUser(),user.getIdGroup(),subNodos,idStructs.toString());
                    putObjectSession("security",security);
                } else {
                    tree = HandlerStruct.loadAllNodes(security,user.getUser(),user.getIdGroup(),subNodos,null);
                }
                putObjectSession("tree",tree);
                putObjectSession("idNodeRoot",forma.getIdNode());
                putAtributte("pagesInfo","showDataDocument.jsp");
                putObjectSession("subNodos",subNodos);
                showStruct = true;
            }
            String idLocation = HandlerStruct.getIdLocationToNode(tree,forma.getIdNode());
            removeObjectSession("showCharge");
            int numVersionesMostrar = 0;
            if (idLocation!=null) {
                BaseStructForm localidad = (BaseStructForm)tree.get(idLocation);
                if (localidad!=null) {
                    if (localidad.getShowCharge()==1) {
                        putObjectSession("showCharge","true");
                    }
                    if ((!ToolsHTML.isEmptyOrNull(localidad.getMajorKeep())) && (ToolsHTML.isNumeric(localidad.getMajorKeep()))) {
                        numVersionesMostrar = Integer.parseInt(localidad.getMajorKeep()!=null?localidad.getMajorKeep().trim():"0");
                    } else {
                        numVersionesMostrar=0; 
                    }
                }
            }
            String qualitySystem = String.valueOf(HandlerParameters.PARAMETROS.getQualitySystem());
            removeAttribute("showNorms");
            removeAttribute("showPDFs");
            if (Constants.permissionSt.equalsIgnoreCase(qualitySystem)) {
                putObjectSession("showNorms","false");
            } else {
                putObjectSession("showNorms","true");
            }
            
            // nuevo codigo de prueba de parametro de seccion Erasepdf
            putObjectSession("showPDFs","true");
            
            
            putObjectSession("nodeActive",forma.getIdNode());
            putObjectSession("idNodeSelected",forma.getIdNode());
            int idStructoReg = ToolsHTML.parseInt(forma.getIdNode());
            Users usuario = getUserSession();
            removeAttribute("showImpReg");
//            boolean Sw_Register=false;
            Hashtable security = (Hashtable)getSessionObject("security");
//             if (tree==null) {
//                 tree = HandlerStruct.loadAllNodesH(security,usuario.getUser(),usuario.getIdGroup(),false);
//                 tree = HandlerStruct.loadAllNodesH(security,usuario.getIdGroup());
//             }
            if (security==null) {
                StringBuffer idStructs = new StringBuffer(50);
                idStructs.append("1");
                security = HandlerGrupo.getAllSecurityForGroup(usuario.getIdGroup(),idStructs);
                HandlerDBUser.getAllSecurityForUser(usuario.getIdPerson(),security,idStructs);
            }
            Connection conn = JDBCUtil.getConnection(Thread.currentThread().getStackTrace()[1].getMethodName());
            loadsolicitudImpresion.tienePermisoImpresion(forma,conn,user,String.valueOf(idStructoReg),security );
            //para usarlo en la lista de impresion en topducmnet.jsp
            putObjectSession("idDocument",forma.getIdDocument());
            putObjectSession("idUser",String.valueOf(usuario.getIdPerson()));
//---------------------------------------------fin para usarlo en la lista de impresion en topducmnet.jsp---------------------------------------------
            conn.close();
            if (!ToolsHTML.isEmptyOrNull(forma.getImprimir())) {
                putObjectSession("showImpReg","true");
            }
            DataUserWorkFlowForm duWF = HandlerWorkFlows.loadDataOwnerWF(false,forma.getIdDocument(),user.getUser());
            removeObjectSession("isOwner");
            if (duWF!=null) {
                putObjectSession("isOwner",duWF);
                boolean isPendingEnd = HandlerWorkFlows.getStatuWorkFlow(duWF.getIdWorkFlow(),Integer.parseInt(duWF.getRow()));
                duWF.setResponse(isPendingEnd);
            } else {
                //Se revisa si el Documento tiene Flujo Param�trico
                duWF = HandlerWorkFlows.loadDataOwnerFlexFlow(Integer.parseInt(forma.getIdDocument()),user.getUser());
                if (duWF!=null) {
                    putObjectSession("isOwner",duWF);
                    duWF.setResponse(false);
                }
            }
            //************************************************************//
            //obtenemos toda la seguridad del los documentos de dicho usuario
//              Hashtable securityDocs = null;
//              securityDocs = ToolsHTML.checkDocsSecurity(securityDocs,usuario,forma.getIdDocument());
//              PermissionUserForm securityDocsfrm = null;
//              if (!ToolsHTML.isEmptyOrNull(forma.getIdDocument())) {
//                  securityDocsfrm = (PermissionUserForm)securityDocs.get(forma.getIdDocument());
//              }
            //************************************************************//
            String idVersion = getParameter("idVersion");
            //Se chequean las Versiones del Documento
            HandlerStruct.checkVersionDocs(forma.getIdDocument());
//            Collection versiones0 = HandlerStruct.getAllVersionForDocument(forma.getIdDocument(),idVersion,securityDocs);
            //ydavila Elmor 
            //validar que si se entra por Lista Maestra, Buscar o Principal s�lo se muestre la �ltima versi�n aprobada. No borradores, No obsoletos.
            Collection versiones0;
            if (vengoDeBuscar!=null) {
            	 versiones0 = HandlerStruct.getPublishedVersionForDocument(forma.getIdDocument(), idVersion, perm);
            }else{
            	 versiones0 = HandlerStruct.getAllVersionForDocument(forma.getIdDocument(), idVersion, perm);
            }
            //cuantos a mostrar
            Iterator version = versiones0.iterator();
            int k=0;
            ArrayList resp = new ArrayList();
            while ((k<numVersionesMostrar+1) && version.hasNext()) {
                BeanVersionForms bean =(BeanVersionForms)version.next();
                resp.add(bean);
                ++k;
            }
            Collection versiones=resp;
            String descrip = HandlerBD.getField("TypeDoc","typedocuments","idTypeDoc",forma.getTypeDocument(),"=",2,Thread.currentThread().getStackTrace()[1].getMethodName());
            forma.setDescriptTypeDoc(descrip);

            //19 de JULIO 2005 INICIO
//            Collection userPending = HandlerWorkFlows.getUserInWorkFlowPending(forma.getIdDocument(),HandlerWorkFlows.wfuPending,false);
            removeObjectSession("userPending");
            Collection userPending;
            if ((HandlerDocuments.docInReview.compareTo(forma.getStatuDoc())==0) ||
                 (HandlerDocuments.docInApproved.compareTo(forma.getStatuDoc())==0) ||
                 (HandlerDocuments.docObs.compareTo(forma.getStatuDoc())==0)) {    //Luis Cisneros 19-04-07 Si el documento estaba obsoleto no se tomaba en cuenta.
                //System.out.println("NO ES PARAMETRICO!!!!!!!!!!!!");
//                Collection userPending = HandlerWorkFlows.getUserInWorkFlowPending(forma.getIdDocument(),HandlerWorkFlows.wfuPending);
                userPending = HandlerWorkFlows.getUserInFlowPending(forma.getIdDocument(),HandlerWorkFlows.wfuPending);

                if(userPending!=null) {
                    putObjectSession("userPending",userPending);
                }
            } else {
                //Se Verifica Flujos de Trabajo Param�tricos
//                userPending = HandlerWorkFlows.getUserInFlowPending(forma.getIdDocument(),HandlerWorkFlows.wfuPending,true);
                userPending = HandlerWorkFlows.getUserInWorkFlowPending(forma.getIdDocument(),HandlerWorkFlows.wfuPending,true);
                if (userPending!=null) {
                    putObjectSession("userPending",userPending);
                }
            }
            
            // data del documento origen
            String documentoOrigen = HandlerStruct.loadNameAndVersionFromDocument(forma.getIdDocumentOrigen(),forma.getIdVersionOrigen());
            
            //19 DE JULIO 2005 FIN
            putObjectSession("showDocument",forma);
            putObjectSession("versiones",versiones);
            putObjectSession("nameFile",forma.getNameFile());
            putObjectSession("dataDocumentOrigen",documentoOrigen);
            
            //Code to show Charge User or Name User
            //Se Busca el C�digo del Documento
            //Para ello se Verifica si el mismo Hereda o No los Prefijos...
            //TODO Revisar
//            forma.setPrefix(ToolsHTML.getPrefixToDoc(getSession(),user,forma.getIdNode()));

            removeObjectSession("isCheckOut");
//            PermissionUserForm permission = new PermissionUserForm();
            if (forma.getLastOperation().trim().equalsIgnoreCase(HandlerDocuments.lastCheckOut)||
                forma.getLastOperation().equalsIgnoreCase(HandlerDocuments.lastBackCheckIn)) {
                DocumentsCheckOutsBean isCheckOut = HandlerDocuments.isCheckOutDoc(forma.getIdDocument());
                putObjectSession("isCheckOut",isCheckOut);
            } else {
                if (forma.getLastOperation().trim().equalsIgnoreCase(HandlerDocuments.lastCheckIn)) {
                    DocumentsCheckOutsBean isCheckOut = HandlerDocuments.loadDataLastCheckOutDoc(forma.getIdDocument());
                    putObjectSession("isCheckOut",isCheckOut);
                }
            }
//            PermissionUserForm permission = new PermissionUserForm();
//            permission.setIdDocument(forma.getIdDocument());
//            permission.setIdUser(user.getUser());
            int emails = HandlerMessages.getTotalInbox(user.getEmail());
            int wfPendings = HandlerWorkFlows.countAllWorkFlows(false,HandlerWorkFlows.pending,
                                                                user.getUser(),HandlerWorkFlows.wfuPending,false);
            int wfExpires = HandlerWorkFlows.countAllWorkFlows(true,HandlerWorkFlows.expires,
                                                               user.getUser(),HandlerWorkFlows.wfuPending,false);
            int wfs = wfPendings + wfExpires;

            putObjectSession("emails",new Integer(emails));
            putObjectSession("wfs",new Integer(wfs));
            //Verificamos si existe alguna seguridad definida
            //para el usuario sobre el documento
//            boolean haveSecurity = HandlerDocuments.getSecurityForDocAndId(true,permission);
//            //Si no existe seguridad para el usuario
//            //se chequea para el grupo del usuario
//            if (!haveSecurity) {
//                permission.setIdUser(user.getIdGroup());
//                haveSecurity = HandlerDocuments.getSecurityForDocAndId(false,permission);
//            }
            //Si no existe ninguna seguridad definida, ni para el usuario ni para el grupo del usuario
            //Cargamos la seguridad definida para el nodo Padre del documento
//            if (!haveSecurity) {
//                security = (Hashtable)getSessionObject("security");
//                if (security==null) {
//                    StringBuffer idStructs = new StringBuffer(50);
//                    idStructs.append("1");
//                    security = HandlerGrupo.getAllSecurityForGroup(user.getIdGroup(),idStructs);
//                    HandlerDBUser.getAllSecurityForUser(user.getIdPerson(),security,idStructs);
//                }
//                if (security==null) {
//                    throw new ApplicationExceptionChecked("E0049");
//                }
//                permission = (PermissionUserForm)security.get(forma.getIdNode());
//            }
             //carga la seguridad de la estructura con la seguridad del documento
//             if (securityDocsfrm!=null) {
//                //carga toda la permisologia al bean
//                 ToolsHTML.checkDocsSecurityLoad(securityDocsfrm,permission);
//             }
//            putObjectSession("permission",permission);
            putObjectSession("permission",perm);            
            //Se Bloquea el Documento
            if (loadMayorVersion) {
				if (HandlerDocuments.checkOutDocument(Integer.parseInt(forma.getIdDocument()),HandlerDocuments.docCheckOut,
						  user.getUser(),forma.getNumVer())>0) {
                    DocumentsCheckOutsBean isCheckOut = HandlerDocuments.isCheckOutDoc(forma.getIdDocument());
                    if (isCheckOut!=null) {
                        putObjectSession("isCheckOut",isCheckOut);
                    }
					forma.setCheckOut(Integer.parseInt(HandlerDocuments.docCheckOut));
                    forma.setCheck(true);
				} else {
                    forma.setCheck(false);
				}
			} else {
                if (downLoadFile || downLoadEraser) {
                    downLoadFile(request, super.getRb(), forma, downLoadEraser, downLoadFile);
                    // actualizamos el historico del documento
        			Connection con = null;
        			try {
        				con = JDBCUtil.getConnection(Thread.currentThread().getStackTrace()[1].getMethodName());
        				Timestamp timeDownload = new Timestamp(new java.util.Date().getTime());

        				HandlerDocuments.updateHistoryDocs(con, Integer.parseInt(forma.getIdDocument()), 0, 0, user.getUser(), timeDownload, HandlerDocuments.versionDownload, null, new String[] { forma.getMayorVer(), forma.getMinorVer() });
        				con.close();
        				
        				AuditFacade.insertarAuditFacade(ToolsHTML.getBundle(request), TransactionTO.DESCARGAR_DOCUMENTO, "", user.getIdPerson(),user.getNameUser(), "", "", AuditTO.getClientIpAddress(request) );
        			} catch(Exception e) {
        				//System.out.println("No pude actualizar el historico del documento");
        				e.printStackTrace();
        			}
                }
            }
            //buscando si tiene documentos relacionados
            //Moficiar el Query para s�lo contar si el Documento tiene Documentos Vinculados => Mejoras en Performance
            Collection haveDocRel = HandlerDocuments.getDocumentsLinks(forma.getIdDocument(),null,user.getIdGroup(),user.getUser(),false);
            removeObjectSession("haveDocRel");
            if (!haveDocRel.isEmpty()){
                putObjectSession("haveDocRel","1");
            }

            if (showStruct) {
                return goTo("struct");
            }
            return goSucces();
        } catch (ApplicationExceptionChecked ae) {
            ae.printStackTrace();
            return goError(ae.getKeyError());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return goError();
    }

	private void downLoadFile(HttpServletRequest request, ResourceBundle rb,
			BaseDocumentForm forma, boolean downLoadEraser, boolean downLoadFile) {
		// TODO Auto-generated method stub
		StringBuffer resp = new StringBuffer(100);
        if(downLoadEraser){
        	//se desea bajar el ultimo borrador
        	//ubicamos si existe un borrador activo para este documento
        	int numLastVerApproved = Integer.parseInt(forma.getLastVersionApproved());
        	// si la version es mayor que la aprobada, hay un borrador
        	if(numLastVerApproved==0 || forma.getNumVer()>numLastVerApproved) { //if(forma.getStatu() == Integer.parseInt(HandlerDocuments.docBorrador)){
        		//la ultima version es un borrador, podemos descargarla
        		resp.append("location.href='showDocument.do?downFile=true&downloadLastEraser=true&idDocument=").append(forma.getIdDocument());
	            resp.append("&idVersion=").append(forma.getNumVer()).append("'");
        		log.info("[borrador] resp=" + resp.toString());
        	} else {
        		resp.append("alert('");
	        	resp.append(rb.getString("E0139"));
	        	resp.append("')");
        	}
        } else if(downLoadFile){
        	//se desea descargar el ultimo aprobado
        	int numLastVerApproved = Integer.parseInt(forma.getLastVersionApproved());
        	if(numLastVerApproved > 0){
	        	resp.append("location.href='showDocument.do?downFile=true&downloadLastApproved=true&idDocument=").append(forma.getIdDocument());
	            resp.append("&idVersion=").append(numLastVerApproved).append("'");
	            log.info("[aprobado] resp=" + resp.toString());
        	}else{
	        	resp.append("alert('");
	        	resp.append(rb.getString("E0138"));
	        	resp.append("')");
	        }
        }
        
        request.getSession().setAttribute("downFile",resp.toString());
	}

	/**
	 * 
	 * @param request
	 * @param rb
	 * @param idDocument
	 * @param numLastVerApproved
	 */
    public static synchronized void downLoadFile(HttpServletRequest request, ResourceBundle rb, int idDocument,
    		int numLastVerApproved, boolean downloadLastEraser) {
        
    }
}
