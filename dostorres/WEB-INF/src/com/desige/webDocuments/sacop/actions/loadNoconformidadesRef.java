package com.desige.webDocuments.sacop.actions;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.desige.webDocuments.activities.forms.SubActivities;
import com.desige.webDocuments.persistent.managers.HandlerDBUser;
import com.desige.webDocuments.persistent.managers.HandlerDocuments;
import com.desige.webDocuments.persistent.managers.HandlerProcesosSacop;
import com.desige.webDocuments.persistent.utils.JDBCUtil;
import com.desige.webDocuments.utils.ToolsHTML;
import com.desige.webDocuments.utils.beans.SuperAction;

/**
 * Created by IntelliJ IDEA.
 * User: srodriguez
 * Date: Feb 7, 2007
 * Time: 11:16:08 AM
 * To change this template use File | Settings | File Templates.
 */
public class loadNoconformidadesRef extends SuperAction {
    private static Logger log = LoggerFactory.getLogger(Sacop_Relacionadas.class.getName());
    
    public ActionForward execute(ActionMapping mapping,
    		ActionForm form, HttpServletRequest request, HttpServletResponse response) {
    	
    	super.init(mapping,form,request,response);
        Sacop_Relacionadas sacop_Relacionadas = new Sacop_Relacionadas();
        SubActivities activity = null;
        
        try {
        	HandlerProcesosSacop  handlerProcesosSacop=new HandlerProcesosSacop();
            String input = request.getParameter("input");
            String value = request.getParameter("value");
            String nameForm = request.getParameter("nameForma");
            String type1 = request.getParameter("type");
            String nameRout = request.getParameter("nameRout") !=null ? request.getParameter("nameRout") : "";
            String idRout = request.getParameter("idRout") !=null ? request.getParameter("idRout") : "";
            String numero = request.getParameter("buscar1")!=null ? request.getParameter("buscar1"): "";
            
            //son los checks seleccionados
            String userAssociate1 = request.getParameter("userAssociate");
            //son variables originales, que se vuelven a utilizar en caso que se quiera ejecutar el formulario de busqueda
            putAtributte("inputb",input);
            putAtributte("valueb",value);
            putAtributte("nameFormab",nameForm);
            putAtributte("typeb",type1);
            putAtributte("userAssociateb",userAssociate1);
            putAtributte("nameRout",nameRout);
            putAtributte("buscar1",numero);
            putAtributte("idRout",idRout);
            
            //solo para busqueda las variables arriba
            //me permite saber si vienen las sacop ya relacionadas o si se va a crear una sacop y
            //se va a relacionar por  primera vez
            //la variable number viene de la propia funcion del java script
            String number = getParameter("number");
            putAtributte("number",number);
            boolean swFiltrar=false;
            String relacionarBusqueda="";
            if ("1".equalsIgnoreCase(number)){
                relacionarBusqueda="-1";
            }

            //----------------------------------------

            String type = getParameter("type");
            log.debug("type: " + type);

            log.debug("Ejecutante");
            //son lo que se h           a hecho check en las formas
            String userAssociate = request.getParameter("userAssociate");

            //variables para filtrar las busquedas
            String keys="";
            byte critery=0;//variable ordenar en busquedas
            String name="";//nombre del documento
            String typeDoc=""; // todos los documentos //HandlerDocuments.TypeDocumentsRegistro;//tipo del documento
            String owner ="";//duenio dl documento
            String publicDoc="";//status publico del documento
            String normISO ="";//normas iso del documento
            String prefix="";//prefijo
            String TypesStatus = HandlerDocuments.docApproved;
            String cargo="";
            String orderBy="";

            boolean swVieneDeBD=false;
            Collection sacops_relacionar=null;
            //si  es -1 implica que se va  a crear
            if (!"-1".equalsIgnoreCase(relacionarBusqueda)){
            	putAtributte("relacionarBusqueda","1");
            	
            	
    			Connection con = null;
    			try {
        			// vamos a utilizar una sola conexion
        			if (con == null || con.isClosed()) {
        				con = JDBCUtil.getConnection(Thread.currentThread().getStackTrace()[1].getMethodName());
        			}
	            	 sacops_relacionar = HandlerDocuments.searchDocumentsII(con, keys,
	            			 critery,
	            			 name,
	            			 numero,
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
	            			 null,
	            			 null,
	            			 null,
	            			 null);
	            	 //comprobamos que con el filtro o sin filtro me traiga registros   de la busqueda
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
	            	 
            	 
//				 if (sacops_relacionar.isEmpty()){
//               	putAtributte("relacionarBusqueda","0");
//                  idRout="";
//                  numero="";
//                  sacops_relacionar = HandlerDocuments.searchDocumentsII(keys,critery,name,numero,typeDoc,owner,publicDoc,
//                                                                    normISO,prefix,TypesStatus,cargo,
//                                                                    orderBy.toString(),idRout,null);
//
//                  }
            }else{
            	//si es -1 implica que se esta observando la sacop ya almacenada en la base de datos.
    			Connection con = null;
    			try {
        			// vamos a utilizar una sola conexion
        			if (con == null || con.isClosed()) {
        				con = JDBCUtil.getConnection(Thread.currentThread().getStackTrace()[1].getMethodName());
        			}
        			sacops_relacionar = HandlerDocuments.searchDocumentsII(con, keys,critery,name,numero,typeDoc,owner,publicDoc,
                                                               normISO,prefix,TypesStatus,cargo,
                                                               orderBy.toString(),idRout,null,null,null,null,null);
                    swVieneDeBD=true;
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
            }

            ArrayList  depuraCadena = new ArrayList();
            //con este metodo devolvemos el depuraCadena lleno y sin  repeticipones de los valores que fueron escojidos en el
            //formulario, se compara de nuevo con la coleccionpara saber si es seleccionado o no en los checks
            //  del formulario sacop_relacionadas.jsp
            //y me devuelve una que sera utilizada aqui
            String quitarValor="";
            String userAssociateLimpia= sacop_Relacionadas.AssociadosEnLimpio(depuraCadena,userAssociate.toString(),quitarValor);

            loadNoconformidadesRef_Puente loadnoconformidadesRef_Puente= new loadNoconformidadesRef_Puente();

            Vector vector = new Vector();
            loadnoconformidadesRef_Puente.buscarSiestanSeleccionadas( sacops_relacionar,  vector,  depuraCadena ,swVieneDeBD);
            putAtributte("userAssociateLimpia",userAssociateLimpia);
            putObjectSession("sacops_relacionar",vector);
            if (!sacops_relacionar.isEmpty()) {
            	putObjectSession("size",String.valueOf(sacops_relacionar.size()));
            }else{
            	putObjectSession("size","0");
            }
            
            Hashtable userAct = new Hashtable();
            Collection usuarios = null;
            usuarios = HandlerDBUser.getAllsUsers(userAct);

            putObjectSession("usuarios",usuarios);
            putObjectSession("input",getDataFormResponse(nameForm,input,true));
            putObjectSession("value",getDataFormResponse(nameForm,value,false));
            putAtributte("type",type);
            log.debug("End");
            
            return goSucces();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return goError();
    }
    
    /**
     * 
     * @param hash
     * @param userAssociate
     * @param quitarValor
     * @return
     */
    public String  AssociadosEnLimpio(ArrayList hash,String userAssociate,String quitarValor){
           //agarramos la cadena que viene del formulario y creamos un arreglo separado por comas
           //para llenar el aray list totalmente depurado, sin repetir valores
             StringTokenizer strk = new StringTokenizer(userAssociate,",");
                     while (strk.hasMoreTokens()){
                         String id = (String)strk.nextToken();
                         if (!hash.contains(id.trim()))  {
                             hash.add(id.trim());
                         }
                     }
           //si mandaron un valor al deseleccionar un checkbox con el submit del java script en sacop_relacionadas.jsp
           //esta viene en la variable quitar valor de la cadena, y asi no incluirla al guardar la sacop
            if (!ToolsHTML.isEmptyOrNull(quitarValor)){
                hash.remove(quitarValor);
            }
            StringBuffer userAssociateLimpia= new StringBuffer();
            Iterator iu = hash.iterator();
            for (;iu.hasNext();){
                 String u = (String)iu.next();
                     userAssociateLimpia.append(u);
                     if (iu.hasNext()){
                         userAssociateLimpia.append(",");
                     }
           }
           //regresmos la càdena separada por comas pero sin repeticiones y sin espacios en blanco
           return userAssociateLimpia.toString();
    }
}
