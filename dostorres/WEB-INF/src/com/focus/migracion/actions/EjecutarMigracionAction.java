package com.focus.migracion.actions;

import java.io.File;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.desige.webDocuments.utils.ApplicationExceptionChecked;
import com.desige.webDocuments.utils.DesigeConf;
import com.desige.webDocuments.utils.StringUtil;
import com.desige.webDocuments.utils.ToolsHTML;
import com.desige.webDocuments.utils.beans.SuperAction;
import com.desige.webDocuments.utils.beans.Users;
import com.focus.migracion.forms.MigracionForm;

/**
 * Title: EjecutarMigracionAction.java <br/> Copyright: (c) 2007 Focus Consulting C.A.<br/>
 * 
 * @author YSA
 * @version WebDocuments v4.8
 * <br/>
 * Changes:<br/>
 * <ul>
 *      <li>18/09/2007 (YSA) Creation </li>
 * </ul>
 */

public class EjecutarMigracionAction extends SuperAction {
	static Logger log = LoggerFactory.getLogger(MigracionAction.class.getName());

	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
		super.init(mapping, form, request, response);
		
        try {
            Users user = getUserSession(); 
            //System.out.println("---- entro en EjecutarMigracionAction ");
            if (ToolsHTML.checkValue(user.getUser())) {
            	MigracionForm forma = (MigracionForm) form;
            	if (forma != null) {
        			String archivo = forma.getNombreArchivo();
        			String nombreArchivo = StringUtil.getOnlyNameFile(archivo); 
        			//System.out.println("Archivo a procesar: "+archivo);

        			File temp = new File(archivo);
        			if(!temp.exists()) {
        				//System.out.println("Busque y no encontre el archivo. "+archivo);
        				archivo = DesigeConf.getProperty("rutaArchivoMigracionWindow").concat(nombreArchivo);
        				temp = new File(archivo);
            			if(!temp.exists()) {
            				//System.out.println("Busque y no encontre el archivo. "+archivo);
            				archivo = DesigeConf.getProperty("rutaArchivoMigracionLinux").concat(nombreArchivo);
            				temp = new File(archivo);
                			if(!temp.exists()) {
                				//System.out.println("Busque y no encontre el archivo. "+archivo);
                				throw new Exception("Archivo no encontrado");
                			} else {
                				forma.setNombreArchivo(archivo);
                			}
            			} else {
            				forma.setNombreArchivo(archivo);
            			}
        			}
    				archivo=forma.getNombreArchivo();
        			//System.out.println("Archivo a procesar luego de la busqueda: "+archivo);
        				
        			String logs = forma.getNombreLogs();
        			String type = (String)request.getSession().getAttribute("typeMigration");
        			String tipo = (String)request.getParameter("tipo");
        			try{
        				removeObjectSession("statusMigracion");
        	            //System.out.println("---- archivo: " + archivo);
        	            //System.out.println("---- logs: " + logs);
        	            //System.out.println("---- type: " + type);
        	            if (ToolsHTML.isEmptyOrNull(logs)){
        	            	logs = "logMigracion";
        	            }
        	            
        	            String path = ToolsHTML.getPath();
        	            //MigracionTxt.inicio(archivo,logs,path);
        				////System.out.println("---- termino MigracionTxt.inicio ");
        				if(archivo!=null){
	        	            int extensionTxt = archivo.indexOf(".txt");
	        				int extensionXls = archivo.indexOf(".xls");
	        				
        	            	String sMigracionValidacion = (String)DesigeConf.getProperty("migracionValidacionOn");
        	            	sMigracionValidacion = tipo!=null?tipo:sMigracionValidacion;

        	            	//if(type!=null && "txt".equals(type)){
	        				if(extensionTxt>0){
	        	            	if(sMigracionValidacion!=null && "1".equals(sMigracionValidacion)){
	        	            		// validacion del archivo a migrar
	        	            	}else{
	        	            		// Migracion de archivos a la base de datos
			        				MigracionTxt.inicio(archivo,logs,path);
	                				request.setAttribute("nameFileLog", MigracionTxt.getNameFileLog());
			        				//System.out.println("---- termino MigracionTxt.inicio ");
	        	            	}
	        	            }else{
	        	            	if(sMigracionValidacion!=null && "1".equals(sMigracionValidacion)){
	        	            		// validacion del archivo a migrar
	                				MigracionValidacionExcel.inicio(archivo,logs,path);
	                				request.setAttribute("nameFileLog", MigracionValidacionExcel.getNameFileLog());
	                	            //System.out.println("---- termino MigracionValidacionExcel.inicio ");
	        	            	}else{
	        	            		// Migracion de archivos a la base de datos
	                				MigracionExcel.inicio(archivo,logs,path);
	                				request.setAttribute("nameFileLog", MigracionExcel.getNameFileLog());
	                	            //System.out.println("---- termino MigracionExcel.inicio ");
	        	            	}
	        	            }
	        	            removeObjectSession("typeMigration");
	        				putObjectSession("statusMigracion","1");
	        				request.setAttribute("tipo", tipo);
        				}
        			}catch(Exception e){
       				 	putObjectSession("statusMigracion","0");
        				//System.out.println("Error en la Migracion: " + e);
        			}
        			return goSucces();
            	}
            }
        } catch (ApplicationExceptionChecked ae) {
            return goError(ae.getKeyError());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return goError();

	}
}
