package com.desige.webDocuments.files.actions;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Hashtable;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.desige.webDocuments.dao.ConfExpedienteDAO;
import com.desige.webDocuments.dao.ExpedienteDAO;
import com.desige.webDocuments.files.facade.FilesFacade;
import com.desige.webDocuments.files.forms.ExpedienteForm;
import com.desige.webDocuments.files.forms.FilesForm;
import com.desige.webDocuments.persistent.managers.HandlerBD;
import com.desige.webDocuments.persistent.managers.HandlerDBUser;
import com.desige.webDocuments.persistent.managers.HandlerStruct;
import com.desige.webDocuments.persistent.managers.HandlerTypeDoc;
import com.desige.webDocuments.persistent.managers.HandlerWorkFlows;
import com.desige.webDocuments.registers.forms.Register;
import com.desige.webDocuments.seguridad.forms.PermissionUserForm;
import com.desige.webDocuments.structured.forms.BaseStructForm;
import com.desige.webDocuments.utils.ApplicationExceptionChecked;
import com.desige.webDocuments.utils.Constants;
import com.desige.webDocuments.utils.DesigeConf;
import com.desige.webDocuments.utils.ToolsHTML;
import com.desige.webDocuments.utils.beans.SuperAction;
import com.desige.webDocuments.utils.beans.Users;

/**
 * Title: SearchDocAction.java <br/>
 * Copyright: (c) 2004 Focus Consulting C.A. <br/>
 * @author Ing. Nelson Crespo
 * @author Ing. Sim�n Rodrigu�z
 * @version WebDocuments v3.0
 * <br/>
 *      Changes:<br/>
 * <ul>
 *      <li> 28/10/2004 (NC) Creation </li>
 *      <li> 27/05/2004 (SR) Variables y crear la session para la busqueda status </li>
 *      <li> 21/04/2006 (NC) Add field size in session </li>
 *      <li> 13/07/2006 (NC) Cambios para heredar o no los Prefijos de las Carpetas </li>
 * </ul>
 */
public class SearchFilesAction extends SuperAction {
	private static Logger log = LoggerFactory.getLogger(SearchFilesAction.class.getName());
	
    public synchronized ActionForward execute(ActionMapping mapping,
                                 ActionForm form, HttpServletRequest request,
                                 HttpServletResponse response) {
        super.init(mapping,form,request,response);

        // seteamos el modulo activo
//        request.getSession().setAttribute(Constants.MODULO_ACTIVO, Constants.MODULO_EXPEDIENTE);

        try {
            
			Users usuario = getUserSession();

			/*
			SeguridadUserForm securityForUser = ToolsHTML.getSeguridadModuloUser(usuario);
			SeguridadUserForm securityForGroup = ToolsHTML.getSeguridadModuloGroup(usuario);

			if (securityForUser.getSearch() == 0) {
				putAtributte("visible", "true");
			} else if (securityForUser.getSearch() == 2) {
				if (securityForGroup.getSearch() == 0) {
					putAtributte("visible", "true");
				} else {
					log.error("ACCESO ILEGAL:searchDocument.do user:"+usuario.getIdPerson()+" / nombre:"+usuario.getNamePerson());
					return mapping.findForward("errorNotAuthorized");
				}
			} else {
				log.error("ACCESO ILEGAL:searchDocument.do user:"+usuario.getIdPerson()+" / nombre:"+usuario.getNamePerson());
				return mapping.findForward("errorNotAuthorized");
			}
			*/
			

			// cargamos la seguridad del usuario
			PermissionUserForm security = HandlerDBUser.getAllSecurityFiles(usuario.getIdPerson());

			request.getSession().setAttribute("securityFiles",security);

			// los datos para los nombre de los campos
			ConfExpedienteDAO oConfExpedienteDAO = new ConfExpedienteDAO();
			ArrayList conf = (ArrayList)oConfExpedienteDAO.findAll();
			request.getSession().setAttribute("conf", conf);
            
            removeAttribute("size");
            //1 de junio del 2005 Sim�n Inicio
            String accion = request.getParameter("accion")!=null?request.getParameter("accion"):"";
            String cargo = request.getParameter("cargo");
            String idRout= request.getParameter("idRout");
            String nameRout= request.getParameter("nameRout");
            if (ToolsHTML.isEmptyOrNull(nameRout)){
                idRout = null;
            }
            // agregamos los paramatros al objeto
            ExpedienteForm criterios = new ExpedienteForm();
            

            StringBuffer orderBy=new StringBuffer("");
            boolean sworderBye = false;

            FilesForm obj;
            if(request.getParameter("start")==null ) {
	            for(int i=0; i<conf.size(); i++) {
	            	obj = (FilesForm)conf.get(i);
	            	if(!ToolsHTML.isEmptyOrNull(request.getParameter(obj.getId()))) {
	            		try {
	            			//System.out.println(request.getParameter(obj.getId()));
	            			if(obj.getId().equals("f1")) {
	            				criterios.set(obj.getId().toUpperCase(), Integer.parseInt(request.getParameter(obj.getId())));
	            			} else if(obj.getId().equals("f2")) {
	            				criterios.set(obj.getId().toUpperCase(),ToolsHTML.sdfShowWithoutHour.parse(request.getParameter(obj.getId())));
	            			} else {
	            				criterios.set(obj.getId().toUpperCase(), request.getParameter(obj.getId()));
	            			}
	               			putAtributte(obj.getId(), request.getParameter(obj.getId()));
	            		} catch(java.lang.NoSuchMethodException ex) {
	            			ex.printStackTrace();
	               			putAtributte(obj.getId(), "");
	            		}
	            	}
	            	
		            if (!ToolsHTML.isEmptyOrNull(request.getParameter(obj.getId()))){
	                    orderBy.append(sworderBye?",":"");
		                orderBy.append(" lower(").append(obj.getId()).append(")");
		                sworderBye=true;
		            }
	            }
            }
              
            //byte critery = 0;
            //inicializamod con criterio=3 para que siempre haga las busquedas
            // en HandlerDocuments.searchDocuments por clave en caso de que existan
            byte critery = 3;
            //if (criterio!=null && ToolsHTML.isNumeric(criterio)) {
            //    critery = Byte.parseByte(criterio);
            //}

         

            Collection documents=null;
            //Carga de los Nodos de la Estructura si los Mismos no han sido Cargado ya :D
            Hashtable tree = (Hashtable)getSessionObject("tree");
            tree = ToolsHTML.checkTree(tree,usuario);
            putObjectSession("tree",tree);
            
            
            if ("1".equalsIgnoreCase(accion)) {
            	FilesFacade oFilesFacade = new FilesFacade(request);
            	if (DesigeConf.getProperty("application.userAdmon").compareTo(usuario.getUser()) == 0) {
                    documents = oFilesFacade.findAllFiles(criterios, conf, request);
            	} else {
                    documents = oFilesFacade.findAllFilesByUser(criterios, conf, request, usuario);
            	}
                
            } else {
                /*query con todos los documentos*/
            	StringBuffer query = new StringBuffer(); 
            	if (DesigeConf.getProperty("application.userAdmon").compareTo(usuario.getUser()) == 0) {
                	query.append("SELECT * FROM expediente ORDER BY f1"); 
            	} else {
                	query.append("SELECT * FROM expediente ");
                	query.append("WHERE f3 = '").append(usuario.getUser()).append("' ");
                	query.append("ORDER BY f1 "); 
            	}
            	
            	request.getSession().setAttribute("queryFilesReport",query.toString());
            }

            Register reg = new Register();
            
            if (documents!=null&&documents.size()>0) {
                putObjectSession("size",new Integer(documents.size()));
            }
            
            putObjectSession("register",reg);
            
            putAtributte("idRout",request.getParameter("idRout")!=null?idRout:"");
            putAtributte("nameRout",request.getParameter("nameRout")!=null?nameRout:"");
            putAtributte("ordenTipo",request.getParameter("ordenTipo")!=null?"checked":"");
            putAtributte("ordenNombre",request.getParameter("ordenNombre")!=null?"checked":"");
            putAtributte("ordenNumero",request.getParameter("ordenNumero")!=null?"checked":"");
            putAtributte("ordenPropietario",request.getParameter("ordenPropietario")!=null?"checked":"");
            putAtributte("ordenCreate",request.getParameter("ordenCreate")!=null?"checked":"");
            putAtributte("ordenISO",request.getParameter("ordenISO")!=null?"checked":"");
            putAtributte("ordenStatus",request.getParameter("ordenStatus")!=null?"checked":"");
            
            //Sim�n 31 mayo 2005 inicio
             String idNodeDocument = HandlerBD.getField("numGen","documents","owner",usuario.getUser(),"=",HandlerBD.typeString,Thread.currentThread().getStackTrace()[1].getMethodName());
//             Hashtable tree = (Hashtable)getSessionObject("tree");
//             tree = ToolsHTML.checkTree(tree,user);
             Enumeration e=tree.keys();
             if (e.hasMoreElements()) {
                idNodeDocument=String.valueOf(e.nextElement());
             }
             removeObjectSession("showCharge");
             String idLocation = HandlerStruct.getIdLocationToNode(tree,idNodeDocument);
            if ((idLocation.equalsIgnoreCase(""))||(idLocation==null)){
//                 //System.out.println("idLocation = ***************************Nulo*****************************************");
            }else{
                 BaseStructForm location = HandlerStruct.loadStruct(idLocation);
                 if (location!=null) {
                   //  //System.out.println("localidad.getShowCharge() = " + location.getShowCharge());
                     if (location.getShowCharge()==1) {
                         putObjectSession("showCharge","true");
                     }
                 }
             }
            putObjectSession("showCharge","true"); // colocado fijo para que siempre aparezca el cargo

            //Sim�n 31 mayo 2005 fin
            Collection TypeStatuSession= HandlerWorkFlows.getAllTypesStatus(getRb());
            Collection typesDocuments = HandlerTypeDoc.getAllTypeDocs(null,true);//HandlerWorkFlows.getAllTypesDocuments();
            putObjectSession("typesDocuments",typesDocuments);
            putObjectSession("searchDocs",documents);
            putObjectSession("TypeStatuSession",TypeStatuSession);
            
            ExpedienteDAO oExpedienteDAO = new ExpedienteDAO();
            putObjectSession("fieldSize",oExpedienteDAO.getCampos());
            
            return goSucces();
        } catch (ApplicationExceptionChecked ae){
            return goError(ae.getKeyError());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return goError();
    }
}
