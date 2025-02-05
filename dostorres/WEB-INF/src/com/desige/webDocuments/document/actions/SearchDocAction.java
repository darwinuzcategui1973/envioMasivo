package com.desige.webDocuments.document.actions;

import java.sql.Connection;
import java.sql.SQLException;
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

import com.desige.webDocuments.dao.ConfDocumentoDAO;
import com.desige.webDocuments.document.forms.BaseDocumentForm;
import com.desige.webDocuments.files.forms.DocumentForm;
import com.desige.webDocuments.persistent.managers.HandlerBD;
import com.desige.webDocuments.persistent.managers.HandlerDocuments;
import com.desige.webDocuments.persistent.managers.HandlerGrupo;
import com.desige.webDocuments.persistent.managers.HandlerStruct;
import com.desige.webDocuments.persistent.managers.HandlerTypeDoc;
import com.desige.webDocuments.persistent.managers.HandlerWorkFlows;
import com.desige.webDocuments.persistent.utils.JDBCUtil;
import com.desige.webDocuments.registers.forms.Register;
import com.desige.webDocuments.seguridad.forms.SeguridadUserForm;
import com.desige.webDocuments.structured.forms.BaseStructForm;
import com.desige.webDocuments.utils.ApplicationExceptionChecked;
import com.desige.webDocuments.utils.Constants;
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
public class SearchDocAction extends SuperAction {
	private static Logger log = LoggerFactory.getLogger(SearchDocAction.class.getName());
	
    public synchronized ActionForward execute(ActionMapping mapping,
                                 ActionForm form, HttpServletRequest request,
                                 HttpServletResponse response) {
        super.init(mapping,form,request,response);
    	
        // seteamos el modulo activo
      //  if(request.getParameter("activarModulo")!=null && request.getParameter("activarModulo").equals("true")) {
      //  	request.getSession().setAttribute(Constants.MODULO_ACTIVO, Constants.MODULO_BUSCAR);
      //  }

        try {
            
			Users usuario = getUserSession();
			
        	BaseDocumentForm doc = null;
			
       // 	request.getSession().setAttribute("querySearchLocation",null);
       // 	request.getSession().removeAttribute("querySearchLocation");
        	
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
			// los datos para los nombre de los campos
			ConfDocumentoDAO oConfDocumentoDAO = new ConfDocumentoDAO();
			ArrayList lista = (ArrayList) oConfDocumentoDAO.findAll();
			request.getSession().setAttribute("confDocument", lista);
			
            
            removeAttribute("size");
            //1 de junio del 2005 Sim�n Inicio
            String accion = request.getParameter("accion")!=null?request.getParameter("accion"):"";
            String cargo = request.getParameter("cargo");
            String idRout= request.getParameter("idRout");
            String nameRout= request.getParameter("nameRout");
            if (ToolsHTML.isEmptyOrNull(nameRout)){
                idRout = null;
            }
            //1 de junio del 2005 Sim�n Fin
            String TypesStatus = request.getParameter("TypesStatus");
            String keys = request.getParameter("keysDoc");
            String criterio = request.getParameter("criterio");
            String name = request.getParameter("nombre");
            String lote = request.getParameter("lote");
            String number = request.getParameter("number");
            String typeDoc = request.getParameter("typeDocument");
            String owner = request.getParameter("owner");
            String prefix = request.getParameter("prefix");
            String normISO = request.getParameter("normISO");
            String publicDoc = request.getParameter("public");
            // ydavila Ticket 001-00-003265 Comentarios para BUSCAR
            String comment = request.getParameter("Comment");

            StringBuilder orderBy=new StringBuilder("");
            boolean sworderBye = false;

             if (!ToolsHTML.isEmptyOrNull(request.getParameter("ordenNombre"))){
                if (sworderBye){
                    orderBy.append(",");
                }
                orderBy.append(" lower(d.nameDocument)");
                sworderBye=true;
            }

            if (!ToolsHTML.isEmptyOrNull(request.getParameter("ordenTipo"))){
                if (sworderBye){
                    orderBy.append(",");
                }
                orderBy.append(" lower(td.typeDoc)");
                sworderBye=true;
            }

            if (!ToolsHTML.isEmptyOrNull(request.getParameter("ordenNumero"))){
                if (sworderBye){
                    orderBy.append(",");
                }
                orderBy.append(" lower(d.prefix),d.number");
                sworderBye=true;
            }

            if (!ToolsHTML.isEmptyOrNull(request.getParameter("ordenISO"))) {
                if (sworderBye){
                    orderBy.append(",");
                }
                orderBy.append(" lower(d.normISO)");
                sworderBye=true;
            }

         // ydavila Ticket 001-00-003265 Comentarios para BUSCAR
            if (!ToolsHTML.isEmptyOrNull(request.getParameter("Comment"))) {
                if (sworderBye){
                    orderBy.append(",");
                }
                orderBy.append(" lower(d.normISO)");
                sworderBye=true;
            }
            
             if (!ToolsHTML.isEmptyOrNull(request.getParameter("ordenPropietario"))){
                if (sworderBye){
                    orderBy.append(",");
                }
                orderBy.append(" lower(p.apellidos), lower(p.nombres)");
                sworderBye=true;
            }

              if (!ToolsHTML.isEmptyOrNull(request.getParameter("ordenCreate"))) {
                if (sworderBye){
                    orderBy.append(",");
                }
                orderBy.append(" d.dateCreation");
                sworderBye=true;
            }
              
              if (!ToolsHTML.isEmptyOrNull(request.getParameter("ordenStatus"))) {
                  if (sworderBye){
                      orderBy.append(",");
                  }
                  orderBy.append(" vd.statu");
                  sworderBye=true;
              }
              
            //byte critery = 0;
            //inicializamod con criterio=3 para que siempre haga las busquedas
            // en HandlerDocuments.searchDocuments por clave en caso de que existan
            byte critery = 3;
            if (criterio!=null && ToolsHTML.isNumeric(criterio)) {
                critery = Byte.parseByte(criterio);
            }

            Collection documents=null;
			request.getSession().removeAttribute("orderBy");
            //Carga de los Nodos de la Estructura si los Mismos no han sido Cargado ya :D
            Hashtable tree = (Hashtable)getSessionObject("tree");
            tree = ToolsHTML.checkTree(tree,usuario);
            putObjectSession("tree",tree);
            
            
            if ("1".equalsIgnoreCase(accion)) {
            	doc = new BaseDocumentForm();
            	
    			if(lista!=null) {
    				doc.setListaCampos(lista);
    				for(int i=0;i<lista.size();i++) {
    					DocumentForm obj = (DocumentForm)lista.get(i);
    					doc.set(obj.getId().toUpperCase(), request.getParameter(obj.getId()));
    				}
    			}
            	
    			Connection con = null;
    			try {
        			// vamos a utilizar una sola conexion
        			if (con == null || con.isClosed()) {
        				con = JDBCUtil.getConnection(Thread.currentThread().getStackTrace()[1].getMethodName());
        			}
        			
	                documents = HandlerDocuments.searchDocumentsII(con, keys,
	                		critery,
	                		name,
	                		number,
	                		typeDoc,
	                		owner,
	                		publicDoc,
	                		normISO,
	                		prefix,
	                		TypesStatus,
	                		cargo,
	                		orderBy.toString(),
	                		idRout,
	                		null,
	                		request,
	                		doc,
	                		lote,
	                		//ydavila Ticket 001-00-003265
	                		comment);
    			} catch(Exception e) {
    				e.printStackTrace();
    			} finally {
    				if(con!=null) {
    					try {
    						con.close();
    					} catch (SQLException e) {
    						e.printStackTrace();
    					}
    					con = null;
    				}
    			}
	                
                
                if(documents.size()==0) {
                	request.getSession().removeAttribute("querySearchReport");
                	request.getSession().removeAttribute("querySearchLocation");
                }
            } else {
                /*query con todos los documentos*/
            	StringBuilder query = new StringBuilder(); 
            	query.append("SELECT DISTINCT(d.numGen),d.number,d.nameDocument,d.IdNode,d.normISO,d.type,d.owner,d.dateCreation,d.prefix,d.statu,td.typeDoc, vd.statu as statuVer, vd.dateExpiresDrafts, "); 
            	query.append("vd.dateExpires, ");
        		switch(Constants.MANEJADOR_ACTUAL) {
        		case Constants.MANEJADOR_MSSQL:
                	query.append("(p.apellidos + ' ' + p.nombres) AS Nombre, ");
        			break;
        		case Constants.MANEJADOR_POSTGRES:
                	query.append("(p.apellidos || ' ' || p.nombres) AS Nombre, ");
        			break;
        		}
            	query.append("vd.numVer, vd.MayorVer, vd.MinorVer, p.idPerson "); 
            	query.append("FROM documents d,person p,typedocuments td,versiondoc vd  ");
            	query.append("WHERE p.nameUser = d.owner  ");
            	//ydavila Ticket 001-00-003265 se hace casting a d.type
            	//query.append("AND td.idTypeDoc = d.type  ");
            	query.append("AND td.idTypeDoc = cast(d.type as int)  ");
            	query.append("AND vd.numDoc = d.numGen  ");
            	query.append("AND d.active = '1'   ");
            	//ydavila Ticket 001-00-003265 se compara d.type como tipo caracter
            	//query.append("AND d.type<>1001  ");
            	query.append("AND d.type<>'1001'  ");
            	query.append("AND p.accountActive='1' ");  
            	query.append("AND vd.numVer = (SELECT MAX(numVer) "); 
            	query.append("FROM versiondoc vdi WHERE vdi.numDoc = d.numGen ) ");            	
            	
            	request.getSession().setAttribute("querySearchReport",JDBCUtil.replaceCastMysql(query.toString()));
            }
            if (keys!=null&&(critery==1)) {
                name = keys;
            }
            if (keys!=null && critery==2) {
                number = keys;
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
            putAtributte("TypesStatus",TypesStatus);
            putAtributte("keysDoc",keys);
            putAtributte("nombre",name);
            putAtributte("lote",lote);
            putAtributte("number",number);
            putAtributte("typeDocument",typeDoc);
            putAtributte("public",publicDoc);
            putAtributte("prefix",prefix);
            putAtributte("normISO",normISO);
          //ydavila Ticket 001-00-003265
            putAtributte("comment",comment);
            putAtributte("owner",owner);
            
			if(lista!=null && doc!=null) {
				DocumentForm obj = null;
				for(int i=0;i<lista.size();i++) {
					obj = (DocumentForm)lista.get(i);
		            putAtributte(obj.getId(),doc.get(obj.getId().toUpperCase()));
				}
			}
            
			Connection con = null;
			try {
    			// vamos a utilizar una sola conexion
    			if (con == null || con.isClosed()) {
    				con = JDBCUtil.getConnection(Thread.currentThread().getStackTrace()[1].getMethodName());
    			}
	
	            //Sim�n 31 mayo 2005 inicio
	             String idNodeDocument = HandlerBD.getField(con, "numGen","documents","owner",usuario.getUser(),"=",HandlerBD.typeString,Thread.currentThread().getStackTrace()[1].getMethodName());
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
	                 BaseStructForm location = HandlerStruct.loadStruct(con, idLocation);
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
	            putObjectSession("more",request.getParameter("more"));
	
	            putObjectSession("doc",doc);
	            
	            removeObjectSession("showLink");
	            SeguridadUserForm forma = new SeguridadUserForm();
	        	SeguridadUserForm securityForGroup = new SeguridadUserForm();
	            HandlerGrupo.getFieldUser(forma, "seguridaduser", true, usuario.getUser());
	        	HandlerGrupo.getFieldUser(securityForGroup, "seguridadgrupo", false, usuario.getIdGroup());
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
	        	
			} catch(Exception e) {
				e.printStackTrace();
			} finally {
				if(con!=null) {
					try {
						con.close();
					} catch (SQLException e) {
						e.printStackTrace();
					}
					con = null;
				}
			}
	        	
        	
            return goSucces();
        } catch (ApplicationExceptionChecked ae){
            return goError(ae.getKeyError());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return goError();
    }
}
