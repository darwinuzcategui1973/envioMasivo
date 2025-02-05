package com.desige.webDocuments.document.actions;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.swing.text.html.HTMLDocument.HTMLReader.FormAction;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.desige.webDocuments.dao.ConfDocumentoDAO;
import com.desige.webDocuments.document.forms.BaseDocumentForm;
import com.desige.webDocuments.mail.actions.SendMailTread;
import com.desige.webDocuments.mail.forms.MailForm;
import com.desige.webDocuments.persistent.managers.HandlerBD;
import com.desige.webDocuments.persistent.managers.HandlerDBUser;
import com.desige.webDocuments.persistent.managers.HandlerDocuments;
import com.desige.webDocuments.persistent.managers.HandlerGrupo;
import com.desige.webDocuments.persistent.managers.HandlerNorms;
import com.desige.webDocuments.persistent.managers.HandlerParameters;
import com.desige.webDocuments.persistent.managers.HandlerStruct;
import com.desige.webDocuments.persistent.managers.HandlerTypeDoc;
import com.desige.webDocuments.persistent.managers.HandlerWorkFlows;
import com.desige.webDocuments.persistent.utils.JDBCUtil;
import com.desige.webDocuments.seguridad.forms.PermissionUserForm;
import com.desige.webDocuments.structured.forms.BaseStructForm;
import com.desige.webDocuments.utils.ApplicationExceptionChecked;
import com.desige.webDocuments.utils.Constants;
import com.desige.webDocuments.utils.DesigeConf;
import com.desige.webDocuments.utils.IDDBFactorySql;
import com.desige.webDocuments.utils.ToolsHTML;
import com.desige.webDocuments.utils.beans.Search;
import com.desige.webDocuments.utils.beans.SuperAction;
import com.desige.webDocuments.utils.beans.SuperActionForm;
import com.desige.webDocuments.utils.beans.Users;
import com.focus.qweb.dao.PlanAuditDAO;
import com.focus.qweb.facade.AuditFacade;
import com.focus.qweb.to.PlanAuditTO;
import com.focus.request.PlanAuditRequest;
import com.focus.request.ProgramAuditRequest;
import com.focus.wonderware.actions.ActiveFactory;
import com.focus.wonderware.actions.HandlerProcesosWonderWare;
import com.focus.wonderware.forms.ActiveFactory_frm;

/**
 * Title: EditDocumentAction.java<br>
 * Copyright: (c) 2003 Consis International<br>
 * Company: Consis International (CON)<br>
 * @author Nelson Crespo (NC)
 * @version Acsel-e v2.2
 * <br>
 * Changes:<br>
 * <ul>
 *      <li> 01/07/2004 (NC) Creation </li>
 *      <li> 14/05/2005 (NC) Cambios varios para el manejo de Registros </li>
 *      <li> 22/05/2005 (NC) Se modific� para el manejo din�mico de los prefijos </li>
 *      <li> 01/07/2005 (SR) Se valido, nodeActive en caso que la session tree este nula, se sustituye por la session arbol creada en editDocumentAction </li>
 *      <li> 01/07/2005 (SR) Se valido, en caso que la session tree este nula, se sustituye por la session arbol creada en editDocumentAction </li>
 *      <li> 06/07/2005 (SR) Se elimino la session arbol, y se creo la session tree. </li>
 *      <li> 27/07/2005 (SR) en el metodo  getNumberToDocStatic se actualizo la forma de obtener el numero correlativo ya sea por localidad
 *           o genera. </li>
 *      <li> 06/07/2005 (SR) Se crea el metodo numCorrelativo para sacar un numero correlativo no repetido. </li>
 *      <li> 30/06/2006 (NC) se agreg� el uso del Log y cambios para mostrar los documentos vinculados.</li>
 * <ul>
 */
public class EditDocumentAction extends SuperAction {
    static Logger log = LoggerFactory.getLogger("[V3.0] " + EditDocumentAction.class.getName());
    
    public ActionForward execute(ActionMapping actionMapping,
                                 ActionForm form, HttpServletRequest request,
                                 HttpServletResponse response) {
        super.init(actionMapping,form,request,response);
        String target = request.getParameter("target");
        String next = request.getParameter("nexPage");
        HandlerStruct handlerStruct = new HandlerStruct();
        String idNode_buscqueda=request.getParameter("idNode_buscqueda");
        try {
            Users usuario = getUserSession();
            
            String qualitySystem = String.valueOf(HandlerParameters.PARAMETROS.getQualitySystem());
            removeAttribute("showNorms");
            removeAttribute("showPDFs");
            putObjectSession("showPDFs","false");
            try {
	            if (Constants.permissionSt.equalsIgnoreCase(qualitySystem)) {
	                // putObjectSession("showNorms","false");
	            } else {
	            	putObjectSession("showPDFs","false");
	                putObjectSession("showNorms","true");
	                // datos de los programas
	                AuditFacade oAuditFacade = new AuditFacade(request);
	                String norma = null;
	                if (ToolsHTML.isEmptyOrNull( norma=((BaseDocumentForm) form).getNormISO())) {
	                	norma="";
	                	
	                } else {
	                norma=((BaseDocumentForm) form).getNormISO();
	                }
	                
	                List<ProgramAuditRequest> listProgram = oAuditFacade.listarProgramFacadeId(true,norma); 
	                List<PlanAuditRequest> listPlan = oAuditFacade.listarPlanFacadeId(norma);
	                //List<PlanAuditRequest> listPlan = oAuditFacade.listarPlanFacade();
	                Collection<Search> listNormAudit = HandlerNorms.getAllNormsForAudit();
	                putObjectSession("listNormAudit",listNormAudit);
	                putObjectSession("listProgramAudit",listProgram);
	                putObjectSession("listPlanAudit",listPlan);
	                request.setAttribute("listProgramAudit",listProgram);
	                request.setAttribute("listPlanAudit",listPlan);
	                request.setAttribute("listNormAudit",listNormAudit);
	                
	                // llamado al boton suprimir
	                boolean isShowPDFs  = ToolsHTML.showPDF(usuario);
	                request.setAttribute("showPDFs", (isShowPDFs ? "true" : "false"));
	                
	            }
            } catch(Exception e) {
            	// no se pudo verificar si esta en la norma, yo lo valide en la pagina con toolsHTML
            }
            String cmd = request.getParameter("cmd");
            
            
            // pondremos en session la configuracion de campos adicionales
    		// los datos para los nombre de los campos
			ConfDocumentoDAO oConfDocumentoDAO = new ConfDocumentoDAO();
			ArrayList confDocument = (ArrayList) oConfDocumentoDAO.findAll();
            putObjectSession("confDocument",confDocument);
            
            
            Hashtable tree = (Hashtable)getSessionObject("tree");
            tree = ToolsHTML.checkTree(tree,usuario);
            putObjectSession("tree",tree);
            if (ToolsHTML.isEmptyOrNull((String)getSessionObject("nodeActive"))) {
                putObjectSession("nodeActive",idNode_buscqueda);
                putObjectSession("arbol",tree);
                putObjectSession("tree",tree);
            }
            String nodeActive = (String)request.getSession().getAttribute("nodeActive");
            if(nodeActive==null && request.getSession().getAttribute("idNodeSelected")!=null) {
            	nodeActive = (String)request.getSession().getAttribute("idNodeSelected");
            } else if(nodeActive==null) {
        		request.getSession().removeAttribute("idNodeSelected");
        		request.getSession().removeAttribute("idNodeSelectedName");
            	nodeActive="1";
            }
            putObjectSession("nodeActive", nodeActive);
            BaseStructForm carpeta = (BaseStructForm)tree.get(nodeActive);
            String idNode = (String)ToolsHTML.getAttribute(request,"idNodeSelected");

            //para ver si los permisos de localidad permiten colocar documentos en linea
            //27 de septiembre 2005 inicio
            StringBuffer idStructs = new StringBuffer(50);
            idStructs.append("1");
            Hashtable security = HandlerGrupo.getAllSecurityForGroup(usuario.getIdGroup(),idStructs);
            HandlerDBUser.getAllSecurityForUser(usuario.getIdPerson(),security,idStructs);
            PermissionUserForm formadocIn = (PermissionUserForm)security.get(nodeActive);
            //27 de septiembre 2005 fin

            String idLocation = HandlerStruct.getIdLocationToNode(tree,nodeActive);
            removeObjectSession("docInline");
            if (formadocIn!=null&& !"0".equalsIgnoreCase(String.valueOf(formadocIn.getToDocinLine()))) {
                putObjectSession("docInline","true");
            }
            
            removeObjectSession("publicEraser");
            if (formadocIn!=null&& !"0".equalsIgnoreCase(String.valueOf(formadocIn.getToPublicEraser()))) {
                putObjectSession("publicEraser","true");
            }
            
            if (!ToolsHTML.checkValue(target)) {
                target = "";
            }
            if (!ToolsHTML.checkValue(next)) {
                next = "";
            }
            if (!ToolsHTML.checkValue(idNode)) {
                idNode="";
            }
            if (cmd==null) {
                cmd = SuperActionForm.cmdNew;
            }

            Collection typesDocuments = null;//HandlerTypeDoc.getAllTypeDocs(null,false);
            log.debug("cmd " + cmd);
            if (SuperActionForm.cmdLoad.compareTo(cmd)==0) {
                typesDocuments = HandlerTypeDoc.getAllTypeDocs(null,true);
            } else {
                typesDocuments = HandlerTypeDoc.getAllTypeDocs(null,false);
            }
            if (typesDocuments!=null) {
                putObjectSession("typesDocuments",typesDocuments);
            }
            
            Collection typesDocumentsDoc = HandlerTypeDoc.getAllTypeDocsTypeDoc();
            putObjectSession("typesDocumentsDoc",typesDocumentsDoc);
            

            if ((cmd.equalsIgnoreCase(SuperActionForm.cmdNew))|| (cmd.equalsIgnoreCase(SuperActionForm.cmdLoad))) {
                if (ToolsHTML.checkValue(nodeActive)){
                    String nodeType = HandlerStruct.getTypeNode(nodeActive);
                    if (nodeType!=null) {
                        putAtributte("nodeType",nodeType);
                    }
                }

                String nodeType = (String)ToolsHTML.getAttribute(request,"nodeType");
                log.debug("[EditDocumentAction] nodeType = " + nodeType);
                if (nodeType!=null){
                    putAtributte("nodeTypeNew",nodeType);
                }
                BaseDocumentForm forma = new BaseDocumentForm();
                forma.setDocPublic(forma.getDocPublic()==null?"1":forma.getDocPublic());
                forma.setApproved(forma.getApproved()==null?"1":forma.getApproved());
                forma.setOwner(usuario.getUser());
                
                //String idLocation = HandlerStruct.getIdLocationToNode(tree,nodeActive);
                log.debug("idLocation = " + idLocation);
                byte typePrefix = 0;
                if (idLocation!=null) {
                    BaseStructForm localidad = (BaseStructForm)tree.get(idLocation);
                    if (localidad!=null) {
                        //Para Indicar si se muestran los Cargos o el Nombre del Usuario....
                        if (localidad.getShowCharge() == Constants.permission) {
                            putObjectSession("showCharge","true");
                        } else {
                            removeObjectSession("showCharge");
                        }
                        typePrefix = localidad.getTypePrefix();
                        log.debug("typePrefix Location] = " + typePrefix);
                        ////System.out.println("localidad.getMajorId() ="+localidad.getMajorId());
                        forma.setMayorVer(ToolsHTML.getInitValue(localidad.getMajorId(),false));
                        if (!ToolsHTML.isEmptyOrNull(localidad.getMinorKeep())) {
                            if (ToolsHTML.isNumeric(localidad.getMinorKeep())) {
                                int numHist = Integer.parseInt(localidad.getMinorKeep().trim());
                                if (numHist > 0) {
                                    forma.setMinorVer(ToolsHTML.getInitValue(localidad.getMinorId(),true));
                                } else {
                                    forma.setMinorVer(ToolsHTML.getInitValue(localidad.getMinorId(),true));
                                }
                            }
                        } else {
                            forma.setMinorVer(ToolsHTML.getInitValue(localidad.getMinorId(),true));
                        }
                        ////System.out.println("mayorVer = "+forma.getMayorVer());
                        ////System.out.println("minorVer = "+forma.getMinorVer());
                    }
                }
                if (carpeta!=null) {
                    log.debug("[carpeta.getPrefix()] = " + carpeta.getPrefix());
                    if (carpeta.getHeredarPrefijo() == Constants.permission) {
                        String prefix = handlerStruct.getParentPrefixinst(tree,nodeActive,0==typePrefix);
                        log.debug("[prefix]" + prefix);
                        if (0==typePrefix) {
                            forma.setPrefix(carpeta.getPrefix()+prefix);
                        } else {
                            forma.setPrefix(prefix+carpeta.getPrefix());
                        }
                    } else {
                        forma.setPrefix(carpeta.getPrefix());
                    }
                }
                Collection norms = HandlerNorms.getAllNorms();
                Collection users = HandlerDBUser.getAllUsers();

                cmd = SuperActionForm.cmdInsert;
                forma.setCmd(cmd);

                //26 DE JULIO 2005 INICIO
                //String numberDoc = getNumberToDoc(idLocation,nodeActive);
                forma.setIdLocation(idLocation);
                forma.setNodeActive(nodeActive);
                String numberDoc = null;
                ResourceBundle rb = ToolsHTML.getBundle(request);
                if ((rb.getString("numcorrelativoinicio").length())>9){
                    numberDoc = rb.getString("numcorrelativoinicio").substring(0,9);
                } else {
                    numberDoc = rb.getString("numcorrelativoinicio");
                }
                forma.setNumber(numberDoc);
                putObjectSession("newDocument",forma);
                if (norms!=null){
                    putObjectSession("norms",norms);
                }
                if (users!=null) {
                    putObjectSession("userSystem",users);
                }
                
                removeObjectSession("borradorCorrelativo");
                String borradorCorrelativo="0";
                //que no sea publico
                if (idLocation!=null && (!forma.getDocPublic().equalsIgnoreCase("0"))) {
                    BaseStructForm localidad = (BaseStructForm)tree.get(idLocation);
                    if (localidad!=null) {
                        borradorCorrelativo = String.valueOf(localidad.getCheckborradorCorrelativo());
                        if(borradorCorrelativo.equals("1")) {
                            //si es uno, se le asigna y respeta el numerto correlativo al borrador.
                            putObjectSession("borradorCorrelativo",borradorCorrelativo);
                        }
                    }
                }
                
            } else {
                BaseDocumentForm forma = (BaseDocumentForm)form;


                //Luis Cisneros.
                //No se porque, pero no estan llegando bien los getDocRelations.
                //Pero en la Session si estan los correctos.
                if(request.getParameter("docRelations")!=null) {
                	forma.setDocRelations(request.getParameter("docRelations"));
                } else {
                	forma.setDocRelations((String)getSession().getAttribute("docRelations"));
                	removeAttribute("docRelations");
                }
                log.debug("[forma.getDocRelations()]" + forma.getDocRelations());

                if (!ToolsHTML.isEmptyOrNull(forma.getDocPublic())) {
                    //obtenemos por configuracion de propiedades de la estructura si un borrador pueda tener un numero
                    //correlativo ya asignado  si en propiedades de la localidad lo exigen.
                    removeObjectSession("borradorCorrelativo");
                    String borradorCorrelativo="0";
                    //que no sea publico
                    if (idLocation!=null&&(!forma.getDocPublic().equalsIgnoreCase("0"))) {
                        BaseStructForm localidad = (BaseStructForm)tree.get(idLocation);
                        if (localidad!=null) {
                            borradorCorrelativo = String.valueOf(localidad.getCheckborradorCorrelativo());
                        }
                    }
                    //si es uno, se le asigna y respeta el numerto correlativo al borrador.
                    putObjectSession("borradorCorrelativo",borradorCorrelativo);
                }
                if(carpeta == null ) carpeta = new BaseStructForm();
                //ydavila Elmor
                //BaseStructForm struct = new BaseStructForm(null,forma.getNameDocument(),carpeta.getIdNode(),null);
                BaseStructForm struct = new BaseStructForm(null,forma.getNameDocument(),carpeta.getIdNode(),null, null, null, null);
                String nameUser = HandlerDBUser.getNameUser(forma.getOwner());
                //ydavila Elmor
                /*
                String respsolcambio = HandlerDBUser.getrespsolcambio(forma.getrespsolcambio());
                forma.setrespsolcambio(respsolcambio);
                String respsolelimin = HandlerDBUser.getrespsolelimin(forma.getrespsolelimin());
                forma.setrespsolelimin(respsolelimin);
                String respsolimpres = HandlerDBUser.getrespsolimpres(forma.getrespsolimpres());
                forma.setrespsolimpres(respsolimpres);
                */
                struct.setNodeType("5");
                struct.setNameIcon(DesigeConf.getProperty("imgDocument"));
                forma.setIdNode(carpeta.getIdNode());
                forma.setNameOwner(nameUser);
                String descrip = HandlerBD.getField("TypeDoc","typedocuments","idTypeDoc",forma.getTypeDocument(),"=",2,Thread.currentThread().getStackTrace()[1].getMethodName());
                forma.setDescriptTypeDoc(descrip);
                //validamos el numero correltivo en caso de que no repita
                String numByLocation = String.valueOf(HandlerParameters.PARAMETROS.getNumDocLoc());
                if (!ToolsHTML.isEmptyOrNull(numByLocation)) {
                    numByLocation = numByLocation.trim();
                }
                String editando = request.getParameter("editando")!=null?request.getParameter("editando"):"";
                forma.setNumber(forma.getNumber()!=null?forma.getNumber().trim():forma.getNumber());
                //validamos que el numero correlativo no exista....
                String numCorrelativo = null;
                //busca para ver si se repite este numero creado po el usuario
                //, si se repite te manda un null en respuesta.
                if (!ToolsHTML.isEmptyOrNull(forma.getNumber())) {
                     numCorrelativo = chkNumCorrelativo(forma.getIdNode(),forma.getNumber(),numByLocation,tree,forma);
                    //esta repetido el numero si es nullo numCorrelativo
                    if (ToolsHTML.isEmptyOrNull(numCorrelativo)) {
                        //El N�mero debe ser �nico para todo el Sistema
                        if ("0".equalsIgnoreCase(numByLocation)) {
                             forma.setNumCorrelativoExiste(forma.getNumber());
                             if ("true".equalsIgnoreCase(editando)) {
                                 throw new ApplicationExceptionChecked("doc.numRelativoExiste");
                             }
                        } else  if (("1".equalsIgnoreCase(numByLocation)) ||(("2".equalsIgnoreCase(numByLocation)))) {
                             forma.setNumCorrelativoExiste(forma.getPrefix()+forma.getNumber());
                            if ("true".equalsIgnoreCase(editando)){
                                throw new ApplicationExceptionChecked("doc.numRelativoExiste");
                             }
                        }
                     } else {
                        //si no es nulo, es  que el num no existe...
                        //aui se guardan las variables de las propiedades del documento
                        if (!forma.getCmd().equalsIgnoreCase(SuperActionForm.cmdInsert)) {
                            processCmd(forma,request, usuario);
                        }
                     }
                } else {
                    //si no es nulo, es  que el num no existe...
                    if (!forma.getCmd().equalsIgnoreCase(SuperActionForm.cmdInsert)) {
                        //aui se guardan las variables de las propiedades del documento
                        processCmd(forma,request, usuario);
                    }
                }
            }
            next += "?idNodeSelected=" + idNode;
            // PENDIENTE: grabar forfiles para expedientes jairo
            ////System.out.println((BaseDocumentForm)form).setToForFiles("0"));
            ////System.out.println(((BaseDocumentForm)form).getToForFiles());
            ((BaseDocumentForm)form).setCmd(cmd);
            if(cmd!=null) {
            	putObjectSession("cmd",cmd);
            }
            
            // Colocaremos una variable que nos indique si tiene activo el proceso de cambiar nombre y propietario
            boolean completeFTP = String.valueOf(HandlerParameters.PARAMETROS.getChangeProperties()).equals("1");
           	
            System.out.println((completeFTP?"true":"false"));
            request.setAttribute("changeProperties", (completeFTP?"true":"false") );

        } catch (ApplicationExceptionChecked ae) {
            ae.printStackTrace();
            return goError(ae.getKeyError());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        
        request.setAttribute("target",target);
        request.setAttribute("redirect",next);

        if(false) { 
        	// Este codigo esta en evaluacion para eliminarlo
	        //_________________________ WonderWare __ Active Factory _____inicio_________________________________________________________________________________//
	        //enn processCmd(forma,request); , se hixo la actualizacion de actvefactory
	        //verificamos si el documento va a ser asociado con wonderware...
	        //actualizamos la session, para colocar el check de activeFactoryWonderware
	        BaseDocumentForm baseDocumentForm = (BaseDocumentForm)getSessionObject("showDocument");
	        if (baseDocumentForm!=null){
	            HandlerProcesosWonderWare handlerProcesosWonderWare=new HandlerProcesosWonderWare();
	            ActiveFactory_frm activeFactory_frm = null;
	            if(baseDocumentForm.getNumberGen()!=null){
	            	try {
						activeFactory_frm=handlerProcesosWonderWare.getActiveFactoryDocuments(baseDocumentForm.getNumberGen().trim());
					} catch (Exception e) {
						e.printStackTrace();
					}
	            }
	            if ((activeFactory_frm!=null)&&(activeFactory_frm.getNumgen()!=null)){
	                 baseDocumentForm.setDescripcionActiveFactory(activeFactory_frm.getDescripcion()!=null?activeFactory_frm.getDescripcion():"");
	                 // el cero esta activo
	                 baseDocumentForm.setCheckactiveFactory("0");
	            }else{
	                 // el uno esta inactivo
	                 baseDocumentForm.setCheckactiveFactory("1");
	            }
	            //baseDocumentForm.setDocRelations(docRelations)
	            ////System.out.println(baseDocumentForm.getDocRelations());
	            try {
	            	putObjectSession("showDocument",baseDocumentForm);
	            } catch(Exception e) {
	            	e.printStackTrace();
	            }
	
	        }
	        //_________________________ WonderWare __ Active Factory ___________fin___________________________________________________________________________//
        }

        return goSucces();
    }

    private char[] form(String string) {
		// TODO Auto-generated method stub
		return null;
	}

	public static String chkNumCorrelativo(String idFoder,String NumCorrelativo,
            String typeNumber,Hashtable struct)throws Exception{
    	return chkNumCorrelativo(null, idFoder, NumCorrelativo, typeNumber, struct);
    }
    
    public static String chkNumCorrelativo(Connection con, String idFoder,String NumCorrelativo,String typeNumber,Hashtable struct) throws Exception{
        boolean swExist = true;
        if (!ToolsHTML.isEmptyOrNull(NumCorrelativo)) {
            NumCorrelativo = NumCorrelativo.trim();
        }
        String numCorre = NumCorrelativo;
        try{
            //Si el N�mero es �nico para toda la Estructura...
            if (!ToolsHTML.isEmptyOrNull(typeNumber)) {
                typeNumber = typeNumber.trim();
            }
            if ("0".compareTo(typeNumber)==0) {
                swExist = HandlerDocuments.exitsDocInLocationByNumberUnico(con, NumCorrelativo);
            }
            //El N�mero debe Ser �nico por Localidad...
            if ("1".compareTo(typeNumber)==0) {
                //Se Obtiene el Id de la Carpeta si existe alg�n Documento
                //con el mismo n�mero.
                String idNode = HandlerDocuments.exitsDocInLocation(con, NumCorrelativo,null);
                //swExist = idNode!=null; //HandlerDocuments.exitsDocInLocation(NumCorrelativo,null);
                //Si existe un Documento con el mismo N�mero
                //Se verifica que el mismo no se encuentra en la Misma Localidad
                //Para ellos Buscamos el id de la Localidad de la Carpeta en donde se encuentra el Documento
                //y Procedemos a compararlos....
                if (idNode!=null) {
                    //Se Busca la Localidad a la cual pertenece la Carpeta
                    //en donde se est� insertando el Documento
                    String idLocation = HandlerStruct.getIdLocationToNode(struct,idFoder);
                    //Se Busca el Id de la Localidad de la Carpeta que contiene el Documento a Verificar...
                    String idLocationII = HandlerStruct.getIdLocationToNode(struct,idNode);
                    log.debug("idLocationII " + idLocationII);
                    log.debug("idLocation   " + idLocation);
                    swExist = idLocation.equalsIgnoreCase(idLocationII);
                    //Si no existe en la Localidad se verifica en la Carpeta como �ltima instancia
                    if (!swExist) {
                        idNode = HandlerDocuments.exitsDocInLocation(con, NumCorrelativo,idFoder);
                        if (idNode!=null) {
                            swExist = true;
                        }
                    }
                } else {
                    swExist = false;
                }
            }
            //El N�mero debe Ser Unico por Carpeta/Proceso...
            if ("2".compareTo(typeNumber)==0) {
                swExist = HandlerDocuments.exitsDocInLocation(con, NumCorrelativo,idFoder)!=null;
                ////System.out.println("[chkNumCorrelativo] swExist: " + swExist);
                log.debug("[chkNumCorrelativo] swExist: " + swExist);
            }
            if (swExist) {
                numCorre = null;
            }
        } catch(Exception e) {
             e.printStackTrace();
        }
        return numCorre;
    }


    public static String chkNumCorrelativo(String idFoder,String NumCorrelativo,
                                           String typeNumber,Hashtable struct,BaseDocumentForm forma)throws Exception{
            boolean swExist = true;
            String numCorre = NumCorrelativo;
            try{

                //Si el N�mero es �nico para toda la Estructura...
                if (!ToolsHTML.isEmptyOrNull(typeNumber)) {
                    typeNumber = typeNumber.trim();
                }
                if ("0".equalsIgnoreCase(typeNumber)) {
                    swExist = HandlerDocuments.exitsDocInLocationByNumberUnico(NumCorrelativo,forma);
                }
                //El N�mero debe Ser �nico por Localidad...
                if ("1".equalsIgnoreCase(typeNumber)) {
                    //Se Obtiene el Id de la Carpeta si existe alg�n Documento
                    //con el mismo n�mero.
                    String idNode = HandlerDocuments.exitsDocInLocation(NumCorrelativo,null,forma);
                    //swExist = idNode!=null; //HandlerDocuments.exitsDocInLocation(NumCorrelativo,null);
                    //Si existe un Documento con el mismo N�mero
                    //Se verifica que el mismo no se encuentra en la Misma Localidad
                    //Para ellos Buscamos el id de la Localidad de la Carpeta en donde se encuentra el Documento
                    //y Procedemos a compararlos....
                    if (idNode!=null) {
                        //Se Busca la Localidad a la cual pertenece la Carpeta
                        //en donde se est� insertando el Documento
                        String idLocation = HandlerStruct.getIdLocationToNode(struct,idFoder);
                        //System.out.println("idLocation = " + idLocation);
                        //Se Busca el Id de la Localidad de la Carpeta que contiene el Documento a Verificar...
                        String idLocationII = HandlerStruct.getIdLocationToNode(struct,idNode);
                        log.debug("idLocationII " + idLocationII);
                        log.debug("idLocation   " + idLocation);
                        swExist = idLocation.equalsIgnoreCase(idLocationII);
                        //Si no existe en la Localidad se verifica en la Carpeta como �ltima instancia
                        if (!swExist) {
                            idNode = HandlerDocuments.exitsDocInLocation(NumCorrelativo,idFoder,forma);
                            if (idNode!=null) {
                                swExist = true;
                            }
                        }
                    } else {
                        swExist = false;
                    }
                }
                //El N�mero debe Ser Unico por Carpeta/Proceso...
                if ("2".equalsIgnoreCase(typeNumber)) {
                    swExist = HandlerDocuments.exitsDocInLocation(NumCorrelativo,idFoder,forma)!=null;
                    log.debug("[chkNumCorrelativo] swExist: " + swExist);
                }
                if (swExist) {
                    numCorre = null;
                }
            } catch(Exception e) {
                 e.printStackTrace();
            }
            return numCorre;
        }

    public static String numCorrelativo(String idNode,String IdLocation,String numByLocation,String resetNumber,Hashtable tree) throws Exception{
    	return numCorrelativo(null,idNode,IdLocation,numByLocation,resetNumber,tree);
    }
    public static String numCorrelativo(Connection con, String idNode,String IdLocation,String numByLocation,String resetNumber,
                                        Hashtable tree)throws Exception{
        //String NumCorrelativo = null;
        //String numCorrelativoAnterior = null;
        String NumCorrelativo = "1";
        String numCorrelativoAnterior = "1";
        try {
            //si numbylocation=1 entonces el correlativo es general
            //si es diferente a 1, el numero correlativo debe ser unico para

            boolean swExist = false;
            do {
                //Se Busca el N�mero del Documento...
                //Seg�n la Configuraci�n del Usuario...
//                NumCorrelativo = EditDocumentAction.getNumberToDocStatic(idNode,IdLocation).trim();
            	numCorrelativoAnterior = NumCorrelativo;
                NumCorrelativo = EditDocumentAction.getNumberToDocStatic(con, idNode,IdLocation,numByLocation,resetNumber,swExist,numCorrelativoAnterior).trim();
                log.debug("N�mero Generado..." + NumCorrelativo);
                swExist = ToolsHTML.isEmptyOrNull(EditDocumentAction.chkNumCorrelativo(con, idNode,NumCorrelativo,numByLocation,tree));
//                swExist = EditDocumentAction.chkNumCorrelativo(idNode,NumCorrelativo,numByLocation,tree)==null;
                log.debug("N�mero Generado Anterior ..." + numCorrelativoAnterior);
                log.debug("swExist " + swExist);
//                if ("0".equalsIgnoreCase(numByLocation)) {
                //
//
//                }
//                if (corrGen.equalsIgnoreCase("1")) {
//                    swExist = HandlerDocuments.exitsDocInLocationByNumber(NumCorrelativo,idNode);
//                } else {
//                    swExist = HandlerDocuments.exitsDocInLocationByNumberUnico(NumCorrelativo);
//                }
            } while(swExist);
        } catch(Exception e) {
            //log.error(e.getMessage());
            e.printStackTrace();
        }
        return NumCorrelativo;
    }

    public  String numCorrelativoinst(String idNode,String IdLocation,String numByLocation,String resetNumber,
                                      Hashtable tree)throws Exception{
        //String NumCorrelativo = null;
        //String numCorrelativoAnterior = null;
        String NumCorrelativo = "1";
        String numCorrelativoAnterior = "1";
        try {
            boolean swExist = false;
            do {
            	numCorrelativoAnterior = NumCorrelativo;
            	NumCorrelativo = EditDocumentAction.getNumberToDocStatic(idNode,IdLocation,numByLocation,resetNumber,swExist,numCorrelativoAnterior).trim();
                //log.debug("N�mero Generado..." + NumCorrelativo);
                swExist = ToolsHTML.isEmptyOrNull(EditDocumentAction.chkNumCorrelativo(idNode,NumCorrelativo,numByLocation,tree));
                //log.debug("N�mero Generado Anterior..." + numCorrelativoAnterior);
                //log.debug("swExist " + swExist);
            } while(swExist);
        } catch(Exception e) {
            log.error(e.getMessage());
            e.printStackTrace();
        }
        return NumCorrelativo;
    }

    static public String getNumberToDocStatic(String idFolder,String idLocation,String numByLocation,String resetNumber, boolean existNumber, String pastNumber) throws Exception {
    	return getNumberToDocStatic(null,idFolder,idLocation,numByLocation,resetNumber,existNumber,pastNumber);
    }
    static public String getNumberToDocStatic(Connection con, String idFolder,String idLocation,String numByLocation,String resetNumber, boolean existNumber, String pastNumber) throws Exception {
        String number = HandlerParameters.getLengthNumberDocuments(con);
        String numberDoc = "";
		StringBuffer minValue = new StringBuffer("");
		StringBuffer maxValue = new StringBuffer("");
		int lenValue = 1;
		
		if(ToolsHTML.isNumeric(number)){
			lenValue = Integer.parseInt(number);
		}
		
		//Se arman valores dependiendo de la longitud de correlativos (parametro lenNumber)
        if (minValue != null) {
            for (int cont = minValue.toString().length(); cont < lenValue; cont++) {
            	minValue.append("0");
            }
        }else{
        	minValue = new StringBuffer("");
        	minValue.append("0");
        }
		
        if (maxValue != null) {
            for (int cont = maxValue.toString().length(); cont < lenValue; cont++) {
            	maxValue.append("9");
            }
        }else{
        	maxValue = new StringBuffer("");
        	maxValue.append("9");
        }
        
        if(existNumber){
        	if(ToolsHTML.isNumeric(pastNumber)){
	        	int iNumber = Integer.parseInt(pastNumber)+1;
	        	numberDoc = iNumber+"";
        	}else{
        		numberDoc = "1";
        	}
        	//Numeraci�n �nica para todo el Sistema, se actualiza tabla dual_seqgen
	        if ("0".compareTo(numByLocation)==0) {
	        	//String numSist = String.valueOf(HandlerStruct.proximo("numDocs","documents","number"));
	        	String numSist = numberDoc = String.valueOf(IDDBFactorySql.getNextIDDocumentXSistema(con,minValue.toString(), maxValue.toString(),number,"numDocs"));
	        } else {
	            //Numeraci�n �nica por Localidad, se actualiza tabla location
	            if ("1".compareTo(numByLocation)==0) {
	            	String numLoc = String.valueOf(IDDBFactorySql.getNextIDDocument(con, idLocation, minValue.toString(), maxValue.toString(),number));
	            }
	        }
        }else{
	        //Numeraci�n �nica para todo el Sistema
	        if (!ToolsHTML.isEmptyOrNull(numByLocation)) {
	            numByLocation = numByLocation.trim();
	        }
	        if ("0".compareTo(numByLocation)==0) {
	            //numberDoc = String.valueOf(HandlerStruct.proximo("numDocs","documents","number"));
	        	numberDoc = String.valueOf(IDDBFactorySql.getNextIDDocumentXSistema(con, minValue.toString(), maxValue.toString(),number,"numDocs"));
	        } else {
	            //Numeraci�n �nica por Localidad...
	            if ("1".compareTo(numByLocation)==0) {
	                numberDoc = String.valueOf(IDDBFactorySql.getNextIDDocument(con, idLocation, minValue.toString(), maxValue.toString(),number));
	            } else {
	                numberDoc = HandlerStruct.getNumDocInFolder(con, idFolder,number,resetNumber,minValue.toString(),maxValue.toString());
	            }
	        }
	        if (!ToolsHTML.isNumeric(numberDoc)) {
	            if (ToolsHTML.isNumeric(resetNumber)) {
	                numberDoc = resetNumber.trim();
	            } else {
	                numberDoc = "1";
	            }
	        }
        }

        StringBuffer numero = new StringBuffer("");
        if (number != null) {
            for (int cont = numberDoc.length(); cont < Integer.parseInt(number); cont++) {
                numero.append("0");
            }
            numero.append(numberDoc);
        } else {
            numero.append(numberDoc);
        }

        //Si el numero excede al valor maximo segun la longitud de correlativos se incrementa la longitud de correlativos en la tabla parameters, 
        //se agrega un cero a la izquierda y se envia correo
        StringBuffer cero = new StringBuffer("0");
        if(numero.toString().equals(maxValue.toString())){
        	cero.append(numero.toString());
        	numero = cero;
        	//Se incrementa parameteres
        	HandlerParameters.updateLengthDocNumber(con);
        	
        	//Se envia email de aviso
        	Users uAdmin = null;
        	Vector vUsers = HandlerDBUser.getUsers(con, "", "", DesigeConf.getProperty("application.userAdmonKey"), 1);
        	if(vUsers!=null && vUsers.size()>0){
        		uAdmin = (Users) vUsers.elementAt(0);
        	}
        	String emailTo = "";
        	if(uAdmin!=null && !ToolsHTML.isEmptyOrNull(uAdmin.getEmail())){
        		emailTo = uAdmin.getEmail() + ";";
        	}
        	emailTo += HandlerParameters.PARAMETROS.getMailAccount();
        	
    		Locale defaultLocale = new Locale(DesigeConf.getProperty("language.Default"), DesigeConf.getProperty("country.Default"));
    		ResourceBundle rb = ResourceBundle.getBundle("LoginBundle", defaultLocale);
        	MailForm formaMail = new MailForm();
			formaMail.setFrom(HandlerParameters.PARAMETROS.getMailAccount());
			formaMail.setNameFrom(rb.getString("mail.system"));
			formaMail.setTo(emailTo);
			formaMail.setSubject(rb.getString("mail.nameUser")+ " - " + rb.getString("mail.TitleMaxLengthDocNumber"));
			formaMail.setMensaje(rb.getString("mail.MsgMaxLengthDocNumber"));
			try {
    			//SendMailAction.send(formaMail);
    			if(formaMail!=null){
    				SendMailTread mail = new SendMailTread(formaMail);
    				mail.start();
    			}
    		} catch (Exception ex) {
    			log.debug("Exception");
    			log.error(ex.getMessage());
    			ex.printStackTrace();
    		}
        }
        
        return numero.toString();
    }



    private boolean processCmd (BaseDocumentForm forma,HttpServletRequest request,Users usuario) throws ApplicationExceptionChecked {
        log.debug("forma = " + forma.getCmd());
        boolean resp = false;
        StringBuffer mensaje = null;
        ResourceBundle rb = ToolsHTML.getBundle(request);
        if (forma.getCmd().equalsIgnoreCase(SuperActionForm.cmdEdit)) {
            log.debug("Editando Registro...");
            forma.setRazonDeCambioName(request.getParameter("razonNameDocument"));
            forma.setRazonDeCambioOwner(request.getParameter("razonOwner"));
            forma.setRazonDeCambioFiles(request.getParameter("razonFiles"));
            if (HandlerStruct.editDocument(forma, usuario, rb)) {
            	
            	// abrimos el plan asociado al documento
            	PlanAuditDAO oPlanAuditDAO = new PlanAuditDAO();
            	PlanAuditTO oPlanAuditTO = new PlanAuditTO();
            	oPlanAuditTO.setIdPlanAudit(String.valueOf(forma.getIdPlanAudit()));
            	try {
					if(oPlanAuditDAO.cargar(oPlanAuditTO)) {
						if(oPlanAuditTO.getStatusPlan().equals("B")) {
							oPlanAuditTO.setStatusPlan("A");
							oPlanAuditDAO.actualizar(oPlanAuditTO);
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
            	
            	if(Constants.ACTIVE_FACTORY) {
            		//procesamos wonderware para su actualizacion
            		EditDocumentAction actActvefactory=new EditDocumentAction();
            		actActvefactory.actualizarActiveFactory(forma,request,usuario);
            	}
                putObjectSession("info",rb.getString("app.editOk"));
                return true;
            } else {
                mensaje = new StringBuffer(rb.getString("app.notEdit"));
                mensaje.append("\n").append(HandlerStruct.getMensaje());
            }
        }
        if (mensaje!=null) {
            putObjectSession("info",mensaje.toString());
        }
        return false;
    }
    public void mandarCorreoUrl(String numgen,String URL,String Descripcion){

           Connection con=null;
           PreparedStatement pst=null;
           try{
               con= JDBCUtil.getConnection(Thread.currentThread().getStackTrace()[1].getMethodName());
               StringBuffer sql = new StringBuffer();
               sql.append("select p.email,p.nombres,p.apellidos from person p,documents d where d.numgen=").append(numgen).append("");
               sql.append(" and d.owner=p.nameUser");
               //System.out.println("sql.toString() = " + sql.toString());
               pst=con.prepareStatement(JDBCUtil.replaceCastMysql(sql.toString()));
               ResultSet rs=pst.executeQuery();
               if (rs.next()){
                   String mail=rs.getString("email")!=null?rs.getString("email"):"";

                   HandlerWorkFlows.notifiedUsers(  Descripcion,
                                   "",
                                   "ActiveFactory",
                                    mail.toString(),URL.toString());
               }
               rs.close();
           }catch(Exception e){
                  e.printStackTrace();
               //System.out.println("e = " + e);
           }finally{
               try{
                   if (con!=null){
                       con.close();
                   }
                   if (pst!=null){
                       pst.close();
                   }

               }catch(Exception e){
                   e.printStackTrace();
                   //System.out.println("e = " + e);
               }

           }

    }
    public void actualizarActiveFactory(BaseDocumentForm forma,HttpServletRequest request,Users usuario ){
        try{
            //_________________________ WonderWare __ Active Factory _____inicio_________________________________________________________________________________//
                //insertando en wonderware
                forma.setDescripcionActiveFactory(forma.getNameDocument()+ " "+forma.getPrefix()+forma.getNumber()+" " + forma.getDescript());

                HandlerProcesosWonderWare handlerProcesosWonderWare=new HandlerProcesosWonderWare();
                ActiveFactory_frm activeFactory_frm = new ActiveFactory_frm();
                //verificamos si el documento se encuentra registrado en activeFactory
                activeFactory_frm=handlerProcesosWonderWare.getActiveFactoryDocuments(forma.getNumberGen());
                //Construimos la URL de QWebDocumnts
                ActiveFactory activeFactory = new ActiveFactory();
                boolean swhacerLaURLdeWonderware=true;
                String urlWonderWareActiveFactory= activeFactory.ConstruyendoURL( swhacerLaURLdeWonderware,  forma, request, forma.getNumberGen() );
                //si el documento esta cargado en activeFactory, procedemos a eliminar o a editar
                if ((activeFactory_frm!=null)&&(activeFactory_frm.getNumgen()!=null)){
                     if ("0".equalsIgnoreCase(forma.getCheckactiveFactory())){
                        //estamos modificando
                        activeFactory_frm.setDescripcion(forma.getDescripcionActiveFactory());
                        activeFactory_frm.setActive((byte)1);
                        activeFactory_frm.setUrl(urlWonderWareActiveFactory);
                        activeFactory_frm.setNumgen(forma.getNumberGen());
                        //si es cero, implica que es positivo en modificar activeFactoryel documento.
                        handlerProcesosWonderWare.update(activeFactory_frm);
                        //mandamos a mail...
                        //mandarCorreoUrl( forma.getNumberGen(),activeFactory_frm.getUrl(),activeFactory_frm.getDescripcion());
                     }else if("1".equalsIgnoreCase(forma.getCheckactiveFactory())){
                        //el lo borra `por idactivefactorydocument que viene cargado a travez de getActiveFactoryDocuments
                        //si es 1, implica que es negativo en eliminar activeFactoryel documento.
                        handlerProcesosWonderWare.deleted(activeFactory_frm);
                        forma.setDescripcionActiveFactory("");
                     }
                }else{
                        //si numgen es null, procedemos a ingresarlo si el checkbox getActiveFactory esta a cero (Positivo)
                        if (("0".equalsIgnoreCase(forma.getCheckactiveFactory()))){
                           //estamos insertando
                           //activeFactory_frm.setDescripcion();
                           ActiveFactory_frm activeFactory_save = new ActiveFactory_frm();
                           activeFactory_save.setDescripcion(forma.getDescripcionActiveFactory()!=null?forma.getDescripcionActiveFactory():"");
                           activeFactory_save.setActive((byte)1);
                           activeFactory_save.setUrl(urlWonderWareActiveFactory);

                           activeFactory_save.setNumgen(forma.getNumberGen());
                           //try{
                           //        mandarCorreoUrl( forma.getNumberGen(),activeFactory_save.getUrl(),activeFactory_save.getDescripcion());
                           //}catch(Exception e){
                           //    e.printStackTrace();
                           //}
                           handlerProcesosWonderWare.insert(activeFactory_save);
                           //mandamos a mail...
                        }
                }
                //ahora desfcargamosel archivo plano .. donde estara incluida la informacion de
               // los documentos actveFactory relacionados con wondereware.
              String ActveFactoryWonderware="ActveFactoryWonderware.txt";
              String directorioActiveFactory=DesigeConf.getProperty("dirActiveFactory");
              boolean exists = (new File(directorioActiveFactory)).exists();
              if (!exists) {
                    // File or directory does not exist
                    // Create a directory; all ancestor directories must exist
                    boolean success = (new File(directorioActiveFactory)).mkdir();
                    if (!success) {
                      // Directory creation failed
                      log.debug("Fallo la creacion de directorio ActiveFactory");
                    }
              }


              FileOutputStream out= new FileOutputStream(directorioActiveFactory+File.separator+ActveFactoryWonderware);
              StringBuffer comentarios = new StringBuffer();
              // Connect print stream to the output stream
              PrintStream p = new PrintStream(out);
              ActiveFactory_frm activeFactory_NoSeUsaAqui=new ActiveFactory_frm();
              Collection activeFactoryDocuments = handlerProcesosWonderWare.getActiveFactoryDocuments(null,activeFactory_NoSeUsaAqui);
              File fichero = new File(directorioActiveFactory + File.separator + ActveFactoryWonderware);
              fichero.delete();
              Iterator it =  activeFactoryDocuments.iterator();
              while(it.hasNext()){
                   Search bean = (Search)it.next();
                   comentarios.append(bean.getDescript());
                   comentarios.append("    [").append(bean.getAditionalInfo()).append("]    ");
                   comentarios.append("\t\r\n").append("\t\r\n");
              }
              p.println (comentarios);
              p.close();
              //System.out.println("Salvando Archivo");
              fichero = new File(directorioActiveFactory + File.separator + ActveFactoryWonderware);


                 //_________________________ WonderWare __ Active Factory _____fin_________________________________________________________________________________//

        }catch(Exception e){

        }

    }
}