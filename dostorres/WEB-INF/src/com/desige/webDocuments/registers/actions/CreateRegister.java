package com.desige.webDocuments.registers.actions;

import java.util.Hashtable;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.desige.webDocuments.document.actions.EditDocumentAction;
import com.desige.webDocuments.document.forms.BaseDocumentForm;
import com.desige.webDocuments.persistent.managers.HandlerBD;
import com.desige.webDocuments.persistent.managers.HandlerDocuments;
import com.desige.webDocuments.persistent.managers.HandlerParameters;
import com.desige.webDocuments.persistent.managers.HandlerStruct;
import com.desige.webDocuments.registers.forms.Register;
import com.desige.webDocuments.structured.forms.BaseStructForm;
import com.desige.webDocuments.utils.ApplicationExceptionChecked;
import com.desige.webDocuments.utils.Constants;
import com.desige.webDocuments.utils.DesigeConf;
import com.desige.webDocuments.utils.ToolsHTML;
import com.desige.webDocuments.utils.beans.SuperAction;
import com.desige.webDocuments.utils.beans.Users;

/**
 * Title: CreateRegister.java <br/>
 * Copyright: (c) 2004 Focus Consulting C.A.<br/>
 * @author Ing. Nelson Crespo
 * @version WebDocuments v3.0
 * <br/>
 *      Changes:<br/>
 * <ul>
 *      <li> 17/02/2005 (NC) Creation </li>
 *      <li> 13/07/2006 (NC) Cambios para heredar o no los Prefijos de las Carpetas </li>
 * </ul>
 */
public class CreateRegister extends SuperAction {
    static Logger log = LoggerFactory.getLogger("[V4.0] " + CreateRegister.class.getName());
    public ActionForward execute(ActionMapping actionMapping,
                                 ActionForm form, HttpServletRequest request,
                                 HttpServletResponse response) {
        super.init(actionMapping,form,request,response);
        boolean isDocument = getParameter("typeDocument")!=null;
        try {
        	// borramos el attributo que guarda el ultimo documento editado
			request.getSession().removeAttribute("documentRejectVebose");
        	
            Users usuario = getUserSession();
            Register register = (Register)form;
            BaseDocumentForm forma = new BaseDocumentForm();
            BaseDocumentForm formulario = new BaseDocumentForm();
            
            formulario.setIdDocument(register.getIdDocument());
		    formulario.setNumberGen(register.getIdDocument());
            Hashtable tree = (Hashtable)getSessionObject("tree");
            tree = ToolsHTML.checkTree(tree,usuario);
            HandlerStruct.loadDocument(formulario,true,true,tree, request);

            // iniciamos con los datos del registro con los del formulario
            forma.setIdDocument(formulario.getIdDocument());
            forma.setNumberGen(formulario.getNumberGen());
            forma.setApproved(formulario.getApproved());
            forma.setCharge(formulario.getCharge());
            forma.setCheck(formulario.getCheck());
            forma.setContextType(formulario.getContextType());
            forma.setDateApproved(formulario.getDateApproved());
            //forma.setDateDead(formulario.getDateDead()); // fecha de retencion
            forma.setDateExpires(formulario.getDateExpires());
            forma.setDatePublic(formulario.getDatePublic());
            forma.setDocOnline(formulario.getDocOnline());
            forma.setDocPublic(formulario.getDocPublic());
            forma.setExpires(formulario.getExpires());
            forma.setIdNode(formulario.getIdNode());
            forma.setIdUser(formulario.getIdUser());
            forma.setLastOperation(formulario.getLastOperation());
            forma.setLastVersionApproved(formulario.getLastVersionApproved());
            forma.setNameDocument(formulario.getNameDocument());
            forma.setNameFile(formulario.getNameFile());
            forma.setNameOwner(formulario.getNameOwner());
            forma.setNumber(formulario.getNumber());
            forma.setOwner(formulario.getOwner());
            forma.setStatu(formulario.getStatu());
            forma.setStatuDoc(formulario.getStatuDoc());
            forma.setToForFiles(formulario.getToForFiles());
            forma.setTypeFormat(formulario.getTypeFormat());
            forma.setIdDocumentOrigen(formulario.getIdDocument());
            forma.setIdVersionOrigen(String.valueOf(formulario.getNumVer()));
  
            //Se Coloca el Tipo del Documento...
//            if (getParameter("typeDocument")!=null) {
            if (isDocument) {
                forma.setTypeDocument(getParameter("typeDocument"));
            } else {
                forma.setTypeDocument(DesigeConf.getProperty("typeDocs.docRegister"));
            }
            String idLocation = HandlerStruct.getIdLocation(register.getIdRout());
            log.debug("isDocument " + isDocument);
            //Si no es Documento se Genera el número de Versión del Mismo...
            tree = ToolsHTML.checkTree(tree,usuario);
            //Si el Usuario indico como carpeta destino una localidad se valida
            if (idLocation!=null&&idLocation.compareTo(register.getIdRout())==0) {
                throw new ApplicationExceptionChecked("E0074");
            }
            BaseStructForm localidad = null;
            if (idLocation!=null) {
            	localidad = (BaseStructForm)tree.get(idLocation);
            }
            if (!isDocument) {
                log.debug("idLocation = " + idLocation);
                //Según la localidad se define el formato para la versiones mayores y menores
                    if (idLocation!=null && localidad!=null) {
                        forma.setMayorVer(ToolsHTML.getInitValue(localidad.getMajorId(),false));
    //                    forma.setMinorVer(ToolsHTML.getInitValue(localidad.getMinorId(),true));
                        if (!ToolsHTML.isEmptyOrNull(localidad.getMinorKeep())) {
                            if (ToolsHTML.isNumeric(localidad.getMinorKeep())) {
                                int numHist = Integer.parseInt(localidad.getMinorKeep().trim());
                                log.debug("numHist = " + numHist);
                                if (numHist > 0) {
                                    forma.setMinorVer(ToolsHTML.getInitValue(localidad.getMinorId(),true));
                                } else {
                                    forma.setMinorVer("");
                                }
                            }
                        } else {
                            forma.setMinorVer(ToolsHTML.getInitValue(localidad.getMinorId(),true));
                        }
                    }
            } else {
                //Si el Usuario no colocó ninguna Versión se lanza una Exception
                if (ToolsHTML.isEmptyOrNull(register.getMayorVer()) && ToolsHTML.isEmptyOrNull(register.getMinorVer())) {
                    throw new ApplicationExceptionChecked("E0064");
                }
                //validando que el número de Versión se encuentre acorde a la localidad....
                    if (localidad!=null) {
                        log.debug("localidad.getMajorId(): " + localidad.getMajorId());
                        if (!ToolsHTML.chkInitValue(localidad.getMajorId(),register.getMayorVer(),false)) {
                            throw new ApplicationExceptionChecked("E0065");
                        } else {
                            log.debug("localidad.getMinorId(): " + localidad.getMinorId());
                            if (!ToolsHTML.chkInitValue(localidad.getMinorId(),register.getMinorVer(),true)) {
                                throw new ApplicationExceptionChecked("E0066");
                            }
                        }
                    }
                forma.setMayorVer(register.getMayorVer());
                forma.setMinorVer(register.getMinorVer());
            }
            log.debug("Mayor Ver. " + forma.getMayorVer());
            log.debug("Minor Ver. " + forma.getMinorVer());
            //Establecemos el prefijo para el Documento
            BaseStructForm carpeta = (BaseStructForm)tree.get(register.getIdRout());
    		byte typePrefix = 0;
			if (localidad != null) {
				typePrefix = localidad.getTypePrefix();
			}
            if (carpeta!=null) {
                log.debug("[carpeta.getPrefix()] = " + carpeta.getPrefix());
                String nodeActive = carpeta.getIdNode();
                HandlerStruct handlerStruct = new HandlerStruct();
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
            
            // Se comento el 31/08/2007
            /*
            if (carpeta!=null) {
            	//System.out.println("---- carpeta.getPrefix(): " + carpeta.getPrefix());
                log.debug("[carpeta.getPrefix()] = " + carpeta.getPrefix());
                if (!ToolsHTML.isEmptyOrNull(carpeta.getParentPrefix())) {
                    forma.setPrefix(carpeta.getParentPrefix()+carpeta.getPrefix());
                	//System.out.println("---- prefijo formado por parent + carpeta.getPrefix");
                	//System.out.println("---- carpeta.getParentPrefix(): " + carpeta.getParentPrefix());
                	//System.out.println("---- carpeta.getPrefix(): " + carpeta.getPrefix());
                } else {
                    forma.setPrefix(carpeta.getPrefix());
                	//System.out.println("---- prefijo solo formado por carpeta.getPrefix");
                	//System.out.println("---- carpeta.getPrefix(): " + carpeta.getPrefix());
                }
            }*/
            String number = HandlerParameters.getLengthNumberDocuments();
            StringBuffer numero = new StringBuffer("");
            //se genera el número del documento
            if (number!=null) {
                String numByLocation = String.valueOf(HandlerParameters.PARAMETROS.getNumDocLoc());
                String numberDoc = EditDocumentAction.numCorrelativo(register.getIdRout(),idLocation,numByLocation,null,tree);
//                String numberDoc = EditDocumentAction.numCorrelativo(register.getIdRout(),idLocation);
//                String numberDoc = String.valueOf(IDDBFactorySql.getNextID("numDocs"));
                for (int cont = numberDoc.length(); cont < Integer.parseInt(number); cont++){
                    numero.append("0");
                }
                numero.append(numberDoc);
            }
            forma.setNumber(numero.toString());
            forma.setNameDocument(register.getNameDocument());
            forma.setIdNode(register.getIdRout());
            forma.setDateApproved(null);//El registro obviamente no puede estar aprobado ni publicado
            forma.setDocPublic(null);
            forma.setDatePublic(null);
            forma.setDateExpires(null);
            forma.setApproved(null);
            forma.setOwner(usuario.getUser());
            forma.setNameOwner(usuario.getNamePerson());
            forma.setIdRegisterClass(ToolsHTML.parseInt(register.getIdRegisterClass(),0));
            log.debug("Salvando Registro");

            HandlerStruct.createRegister(forma,formulario.getNumVer(), super.getUserSession().getUser());

            putObjectSession("info",getMessage("doc.upOK"));
            
            // Colocamos el documento en el request
            request.setAttribute("registroGenerado", forma);

            // almacenamos en memoria el numero de version del documento recien creado
			request.getSession().setAttribute("documentRejectVebose",String.valueOf(forma.getNumVer()));
            
            log.debug("register.getIdNodeActual() = " + register.getIdNodeActual());
            
            //Luis Cisneros, abrir con JWS o de la manera tradicional. 
            if (ToolsHTML.abrirEditorWebStart(request)){
            	StringBuffer dem = new StringBuffer("/invokeJNPL.jsp?idDocument=").append(forma.getIdDocument());
            	dem.append("&newIdDocument=").append(forma.getNumberGen());
            	dem.append("&newNumVer=").append(forma.getNumVer());
	            ActionForward ret = new ActionForward(dem.toString(),false);
	            log.debug("ret = " + ret);
	            return ret;
            }else{
	            StringBuffer dem = new StringBuffer("/editRegister.jsp?idNodeSelected=").append(register.getIdRout());
	            dem.append("&idVersion=").append(forma.getNumVer());
	            dem.append("&idDocument=").append(register.getIdDocument());
	            String nameFile = ToolsHTML.changeNameFile(usuario.getUser(),forma.getNameFile());
	            dem.append("&nameFile=").append(nameFile);
	            ActionForward ret = new ActionForward(dem.toString(),false);
	            log.debug("ret = " + ret);
	            return ret;
            }
        } catch (ApplicationExceptionChecked ae) {
            ae.printStackTrace();
            putObjectSession("error",getMessage(ae.getKeyError()));
            if (isDocument) {
                return goTo("errorCHK");
            }
            return goTo("errorReg");
//            return goError(ae.getKeyError());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return goError();
    }
}
