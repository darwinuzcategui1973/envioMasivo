package com.desige.webDocuments.sacop.actions;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.ResourceBundle;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.desige.webDocuments.persistent.managers.HandlerDBUser;
import com.desige.webDocuments.persistent.managers.HandlerProcesosSacop;
import com.desige.webDocuments.persistent.utils.HibernateUtil;
import com.desige.webDocuments.persistent.utils.JDBCUtil;
import com.desige.webDocuments.utils.Constants;
import com.desige.webDocuments.utils.ToolsHTML;
import com.desige.webDocuments.utils.beans.Search;
import com.desige.webDocuments.utils.beans.SuperAction;
import com.desige.webDocuments.utils.beans.Users;

/**
 * Created by IntelliJ IDEA.
 * User: srodriguez
 * Date: Jan 26, 2007
 * Time: 1:52:50 PM
 * To change this template use File | Settings | File Templates.
 */
public class Sacop_Relacionadas extends SuperAction {
    static Logger log = LoggerFactory.getLogger(Sacop_Relacionadas.class.getName());
    
    public ActionForward execute(ActionMapping mapping,
    		ActionForm form, HttpServletRequest request, HttpServletResponse response) {
    	
        super.init(mapping,form,request,response);
        ResourceBundle rb = ResourceBundle.getBundle("LoginBundle",defaultLocale);
        Sacop_Relacionadas sacop_Relacionadas = new Sacop_Relacionadas();
        
        try {
            HandlerProcesosSacop  handlerProcesosSacop=new HandlerProcesosSacop();
            Users user = getUserSession();
            
            //obtenemos los filtros de busqueda, para mantenerlos ciclando en las posibles paginaciones
            String userAssociate1 = request.getParameter("userAssociate");
            String idSacop = request.getParameter("idSacop");
            String emisorSacop = request.getParameter("emisorSacop");
        	String responsableSacop = request.getParameter("responsableSacop");
        	String areaResponsableSacop = request.getParameter("areaResponsableSacop");
        	String efectivaSacop = request.getParameter("efectivaSacop");
        	String idProceso = request.getParameter("idProceso");
        	String listRegisterUnique = request.getParameter("listRegisterUnique");
        	
            //son variables originales, que se vuelven a utilizar en caso que se quiera ejecutar el formulario de busqueda
            putAtributte("userAssociateb",userAssociate1);
            putAtributte("idSacopB", idSacop);
            putAtributte("emisorSacopB", emisorSacop);
            putAtributte("responsableSacopB", responsableSacop);
            putAtributte("areaResponsableSacopB", areaResponsableSacop);
            putAtributte("efectivaSacopB", efectivaSacop);
            putAtributte("idProceso", idProceso);
            putAtributte("listRegisterUnique", listRegisterUnique);
        	
            //solo para busqueda las varoiables arriba
            String number = getParameter("number");
            putAtributte("number",number);
            //me permitesaber si vienen las sacop ya relacionadas o si se va a crear una sacop y
            //se va a relacionar por  primera vez
            boolean swFiltrar=false;
            String relacionarBusqueda="";
            if ("1".equalsIgnoreCase(number)){
                swFiltrar=true;
                relacionarBusqueda="-1";
            }

            //----------------------------------------

            String type = getParameter("type");
            log.debug("type: " + type);
            Collection usuarios = null;
            log.debug("Ejecutante");
            Hashtable userAct = new Hashtable();
            usuarios = HandlerDBUser.getAllsUsers(userAct);
            String userAssociate = request.getParameter("userAssociate");

             StringBuffer  sqlFiltrar=new StringBuffer("");
             //variables para hacer filtro de busquedas
            
            boolean swFltr=false;

            Collection sacopsRelacionar=null;
            //si no es -1 implica que se va  a crear
            if (!"-1".equalsIgnoreCase(relacionarBusqueda)){
                 putAtributte("relacionarBusqueda","1");
            }
            
            sacopsRelacionar = handlerProcesosSacop.getSacops_ParaRelacionar(userAssociate.toString(),
            		swFiltrar,
            		request);
            
            //aqui no se hace nada con la variable ArrayList depuraCadena,solo se le coloca como parametro
            //  para regresar la cadena sepatrada por comas completamente limpia, sin repeticiones de numeros
            ArrayList  depuraCadena = new ArrayList();
            putAtributte("userAssociateLimpia",sacop_Relacionadas.AssociadosEnLimpio(depuraCadena,userAssociate.toString(),""));
            putObjectSession("sacops_relacionar", sacopsRelacionar);
            if (!sacopsRelacionar.isEmpty()) {
                putObjectSession("size",String.valueOf(sacopsRelacionar.size()));
            }

            putAtributte("type",type);
            log.debug("End");
            return goSucces();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        
        return goError();
    }

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
    
    //verificamos si el nodo es un proceso
    public boolean getEsUnProcesosSacop(String idnode) {
    	boolean swEsUnProceso=false;
        Vector result = new Vector();
        StringBuffer sql = new StringBuffer("select  idnode from struct where nodetype=").append(Constants.numToProcess);
        sql.append(" AND NameIcon='").append(Constants.imgToProcess).append("'").append(" AND idnode=").append(idnode);
        try{
        	Vector datos = JDBCUtil.doQueryVector(sql.toString(),Thread.currentThread().getStackTrace()[1].getMethodName());
        	if (datos.size()>0){
        		swEsUnProceso=true;
        	}
        }catch(Exception e){
           e.printStackTrace();
        }
        
        return swEsUnProceso;
    }
}
