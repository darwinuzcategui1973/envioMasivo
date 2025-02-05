package com.desige.webDocuments.document.actions;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.desige.webDocuments.dao.ConfDocumentoDAO;
import com.desige.webDocuments.document.forms.BaseDocumentForm;
import com.desige.webDocuments.files.forms.DocumentForm;
import com.desige.webDocuments.persistent.managers.HandlerBD;
import com.desige.webDocuments.persistent.managers.HandlerDBUser;
import com.desige.webDocuments.persistent.managers.HandlerDocuments;
import com.desige.webDocuments.persistent.managers.HandlerGrupo;
import com.desige.webDocuments.persistent.managers.HandlerNorms;
import com.desige.webDocuments.persistent.managers.HandlerStruct;
import com.desige.webDocuments.persistent.managers.HandlerTypeDoc;
import com.desige.webDocuments.persistent.managers.HandlerWorkFlows;
import com.desige.webDocuments.persistent.utils.JDBCUtil;
import com.desige.webDocuments.seguridad.forms.SeguridadUserForm;
import com.desige.webDocuments.structured.forms.BaseStructForm;
import com.desige.webDocuments.utils.ApplicationExceptionChecked;
import com.desige.webDocuments.utils.Constants;
import com.desige.webDocuments.utils.ToolsHTML;
import com.desige.webDocuments.utils.beans.Search;
import com.desige.webDocuments.utils.beans.SuperAction;
import com.desige.webDocuments.utils.beans.Users;
import com.focus.qweb.facade.AuditFacade;
import com.focus.request.NormAuditRequest;
import com.focus.request.PlanAuditRequest;
import com.focus.request.ProgramAuditRequest;

/**
 * Title: ShowDocsPublishedAction.java <br/>
 * Copyright: (c) 2004 Focus Consulting C.A.<br/>
 *
 * @author Ing. Nelson Crespo
 * @version WebDocuments v3.0
 * <br/>
 *       Changes:<br/>
 * <ul>
 *       <li> 13/02/2005 (NC) Creation </li>
 *       <li> 10/04/2006 (SR) Se ordeno por number y prefix </li>
 *       <li> 21/04/2006 (NC) Add field size in session </li>
 *       <li> 13/07/2006 (NC) Cambios para heredar o no los Prefijos de las Carpetas </li>
 * </ul>
 */
public class ShowDocsPublishedAction extends SuperAction {
    private static Logger log = LoggerFactory.getLogger("[V4.0] " + ShowDocsPublishedAction.class.getName());
    
    public synchronized ActionForward execute(ActionMapping mapping,
                                 ActionForm form, HttpServletRequest request,
                                 HttpServletResponse response) {
    	super.init(mapping,form,request,response);

    	// seteamos el modulo activo
   // 	request.getSession().setAttribute(Constants.MODULO_ACTIVO, Constants.MODULO_LISTA_MAESTRA);
         
    	try {
    		Users usuario = getUserSession();
            
        	BaseDocumentForm doc = null;
            
            String toSearch = getParameter("toSearch");
            log.debug("[ShowDocsPublishedAction] toSearch = " + toSearch);
            String accion = getParameter("accion")!=null?getParameter("accion"):"";
            //1 de junio del 2005 Sim�n Fin
            String TypesStatus = request.getParameter("TypesStatus");
            String propietario = request.getParameter("propietario");
            String version = request.getParameter("version");
            String keys = request.getParameter("keysDoc");
            String name = request.getParameter("nombre");
            String number = request.getParameter("number");
            String lote = request.getParameter("lote");
            
            if ("0".equalsIgnoreCase(number)){
                number=null;
            }
            String typeDoc = request.getParameter("typeDocument");
            String owner = request.getParameter("owner");
            String prefix = request.getParameter("prefix");
            String normISO = request.getParameter("normISO");
            //ydavila Ticket 001-00-003265
            String comment = request.getParameter("comment");
            String publicDoc = request.getParameter("public");
            String idRout= request.getParameter("idRout");
            String nameRout= request.getParameter("nameRout");
            //Permite buscar documentos publicados por fecha de publicacion
            String approvedTo= request.getParameter("approvedToHIDDEN");
            String approvedFrom= request.getParameter("approvedFromHIDDEN");

            String idProgramAudit= request.getParameter("idProgramAudit");
            String idPlanAudit= request.getParameter("idPlanAudit");
            
            //Luis Cisneros
            //Permite buscar documentos publicados por fecha de expiracion
            String expiredTo= request.getParameter("expiredToHIDDEN");
            String expiredFrom= request.getParameter("expiredFromHIDDEN");

            request.removeAttribute("cmd");
            request.getSession().removeAttribute("cmd");
            
			// los datos para los nombre de los campos
			ConfDocumentoDAO oConfDocumentoDAO = new ConfDocumentoDAO();
			ArrayList lista = (ArrayList) oConfDocumentoDAO.findAll();
			request.getSession().setAttribute("confDocument", lista);
			
			System.out.println("***************************************");
			System.out.println(expiredTo);
			System.out.println(expiredFrom);
			System.out.println(approvedTo);
			System.out.println(approvedFrom);
			
			System.out.println("***************************************");
            
            removeAttribute("size");
            if (ToolsHTML.isEmptyOrNull(nameRout)) {
                idRout = null;
            }
            //realizamos si manda a ordenar
            HandlerDocuments hd = new HandlerDocuments();
            String orderBy = hd.getOrdenPublicados(getParameter("ordenVersion"),getParameter("ordenNombre"),getParameter("ordenTypeDocument"),
                                  getParameter("ordenNumber"),getParameter("ordenPropietario"),getParameter("ordenApproved"));
            Collection datos = null;
			request.getSession().removeAttribute("orderBy");
            BaseDocumentForm frmReporte = new BaseDocumentForm();
            //Si se ejecuta una accion de buscar, pasa por aqui
            if ("1".equalsIgnoreCase(accion)) {
            	doc = new BaseDocumentForm();
            	
    			if(lista!=null) {
    				doc.setListaCampos(lista);
    				DocumentForm obj = null;
    				for(int i=0;i<lista.size();i++) {
    					obj = (DocumentForm)lista.get(i);
    					doc.set(obj.getId().toUpperCase(), request.getParameter(obj.getId()));
    				}
    			}
    			
    			datos = HandlerDocuments.getDocumentsPublished(propietario,typeDoc, number,
    					prefix, version,keys,name,usuario,
    					orderBy,idRout,null, expiredFrom, expiredTo, approvedFrom,
    					approvedTo, doc, lote, normISO, comment, idProgramAudit, idPlanAudit);
            }
            
            removeObjectSession("published");
            removeObjectSession("size");
            if (datos!=null&&datos.size() > 0) {
                putObjectSession("published",datos);
                putObjectSession("size",new Integer(datos.size()));
            }
            putAtributte("accion",accion);
            //putAtributte("queryReporte",frmReporte.getQueryReporte());
            putAtributte("idRout",request.getParameter("idRout")!=null?idRout:"");
            putAtributte("nameRout",request.getParameter("nameRout")!=null?nameRout:"");
            putAtributte("ordenApproved",request.getParameter("ordenApproved")!=null?"checked":"");
            putAtributte("ordenVersion",request.getParameter("ordenVersion")!=null?"checked":"");
            putAtributte("ordenNombre",request.getParameter("ordenNombre")!=null?"checked":"");
            putAtributte("ordenTypeDocument",request.getParameter("ordenTypeDocument")!=null?"checked":"");
            putAtributte("ordenNumber",request.getParameter("ordenNumber")!=null?"checked":"");
            putAtributte("ordenPropietario",request.getParameter("ordenPropietario")!=null?"checked":"");
            putAtributte("TypesStatus",TypesStatus);
            putAtributte("propietario",propietario);
            putAtributte("version",version);
            putAtributte("keysDoc",keys);
            putAtributte("nombre",name);
            putAtributte("number",number);
            putAtributte("lote",lote);
            putAtributte("typeDocument",typeDoc);
            putAtributte("public",publicDoc);
            putAtributte("prefix",prefix);
            putAtributte("normISO",normISO);
            //ydavila Ticket 001-00-003265
            putAtributte("comment",comment);
            putAtributte("owner",owner);
            putAtributte("approvedFromHIDDEN",approvedFrom);
            putAtributte("approvedToHIDDEN",approvedTo);
            putAtributte("idProgramAudit",idProgramAudit);
            putAtributte("idPlanAudit",idPlanAudit);
            
            // atributos que estaban en la pagina
            
        	request.getSession().setAttribute("isAttached",ToolsHTML.getAttachedField()!=null && !ToolsHTML.getAttachedField().equals(""));
        	request.getSession().setAttribute("isAttachedField0",ToolsHTML.getAttachedField()!=null && ToolsHTML.getAttachedField().equals("0"));
        	request.getSession().setAttribute("isAttachedField1",ToolsHTML.getAttachedField()!=null && ToolsHTML.getAttachedField().equals("1"));
            
            
			//Luis Cisneros
            //Busqueda por fecha de Expiracion
            putAtributte("expiredFromHIDDEN",expiredFrom);
            putAtributte("expiredToHIDDEN",expiredTo);

			if(lista!=null && doc!=null) {
				DocumentForm obj = null;
				for(int i=0;i<lista.size();i++) {
					obj = (DocumentForm)lista.get(i);
		            putAtributte(obj.getId(),doc.get(obj.getId().toUpperCase()));
				}
			}
			
            // datos de los programas
            AuditFacade oAuditFacade = new AuditFacade(request);
            List<ProgramAuditRequest> listProgram = oAuditFacade.listarProgramFacade(false); 
            List<PlanAuditRequest> listPlan = oAuditFacade.listarPlanFacade();
            List<NormAuditRequest> listNormasCero = oAuditFacade.listarNormaCeroFacade();
            putObjectSession("listProgramAudit",listProgram);
            putObjectSession("listPlanAudit",listPlan);
            putObjectSession("listNormasCeroAudit",listNormasCero);
            request.setAttribute("listProgramAudit",listProgram);
            request.setAttribute("listPlanAudit",listPlan);
            request.setAttribute("listNormasCeroAudit",listNormasCero);



			Connection conn = null;

			try {
				conn = JDBCUtil.getConnection(Thread.currentThread().getStackTrace()[1].getMethodName());

	            //Sim�n 31 mayo 2005 inicio
	            String idNodeDocument = HandlerBD.getField(conn, "numGen","documents","owner",usuario.getUser(),"=",HandlerBD.typeString,Thread.currentThread().getStackTrace()[1].getMethodName());
	            Hashtable tree = (Hashtable)getSessionObject("tree");
	            tree = ToolsHTML.checkTree(tree,usuario);
	            if (tree!=null) {
	                putObjectSession("tree",tree);
	            }
	            Enumeration e = tree.keys();
	            if (e.hasMoreElements()) {
	                idNodeDocument = String.valueOf(e.nextElement());
	            }
	            removeObjectSession("showCharge");
	            String idLocation = HandlerStruct.getIdLocationToNode(tree,idNodeDocument);
	            if (!ToolsHTML.isEmptyOrNull(idLocation)) {
	                 BaseStructForm location = HandlerStruct.loadStruct(conn, idLocation);
	                 if (location!=null) {
	                     if (location.getShowCharge()==1) {
	                         putObjectSession("showCharge","true");
	                     }
	                 }
	            }
	            Collection allUsers = HandlerDBUser.getAllUsers(conn);
	            Collection TypeStatuSession= HandlerWorkFlows.getAllTypesStatus(getRb());
	            Collection typesDocuments = HandlerTypeDoc.getAllTypeDocs(null,true);//HandlerWorkFlows.getAllTypesDocuments();
	            putObjectSession("typesDocuments",typesDocuments);
	            putObjectSession("allUsers",allUsers);
	            putObjectSession("TypeStatuSession",TypeStatuSession);
	            putObjectSession("moreP",request.getParameter("moreP"));
	            
	            //Validacion para determinar si aparece o no link desde Publicados
	            removeObjectSession("showLink");
	        	SeguridadUserForm forma = new SeguridadUserForm();
	        	SeguridadUserForm securityForGroup = new SeguridadUserForm();
	        	HandlerGrupo.getFieldUser(conn, forma, "seguridaduser", true, usuario.getUser());
	        	HandlerGrupo.getFieldUser(conn, securityForGroup, "seguridadgrupo", false, usuario.getIdGroup());
	        	String showLink = "false";
	        	if (forma.getEstructura() == 0) {
	        		showLink = "true";
	        	} else {
	        		if (forma.getEstructura() == 2) {
	        			if (securityForGroup.getEstructura() == 0) {
	        				showLink = "true";
	        			}
	        		}
	        	}
	            putObjectSession("showLink",showLink);
	                        
	            putObjectSession("doc",doc);
			} catch(Exception e) {
				e.printStackTrace();
			} finally {
				if(conn!=null) {
					conn.close();
				}
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
}
