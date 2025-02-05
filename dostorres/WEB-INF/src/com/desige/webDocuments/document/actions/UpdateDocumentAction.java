package com.desige.webDocuments.document.actions;

import java.io.File;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.desige.webDocuments.persistent.managers.HandlerDocuments;
import com.desige.webDocuments.persistent.utils.JDBCUtil;
import com.desige.webDocuments.utils.ApplicationExceptionChecked;
import com.desige.webDocuments.utils.ToolsHTML;
import com.desige.webDocuments.utils.beans.SuperAction;
import com.desige.webDocuments.utils.beans.Users;
import com.focus.util.Archivo;

/**
 * Title: UpdateDocumentAction.java <br/>
 * Copyright: (c) 2004 Focus Consulting C.A.<br/>
 * @author Ing. Nelson Crespo
 * @version WebDocuments v3.0
 * <br/>
 *      Changes:<br/>
 * <ul>
 *      <li> 19/07/2005 (NC) Creation </li>
 * </ul>
 */
public class UpdateDocumentAction extends SuperAction {
    public ActionForward execute(ActionMapping actionMapping,
                                 ActionForm form, HttpServletRequest request,
                                 HttpServletResponse response) {
        super.init(actionMapping,form,request,response);
        try {
        	Users usuario = getUserSession();
            
            String nameFile = null;
            	
    		FileUtil fileUtil = new FileUtil();
            Properties parametros = new Properties(); 
            if(getParameter("idCheckOut")==null) {
            	parametros = fileUtil.procesaFicheros(request);
            }

        	//System.out.println(parametros.getProperty("isFileScanned"));
            boolean isFileScanned = ToolsHTML.parseBoolean(parametros.getProperty("isFileScanned"));
            
            nameFile = (getParameter("fileName")==null?parametros.getProperty("fileName"):getParameter("fileName"));
            
			if(isFileScanned) {
				StringBuffer fileName = new StringBuffer(ToolsHTML.getPathTmp()).append(usuario.getNameUser()).append(File.separator).append("file_scanner_").append(usuario.getIdPerson()).append(".pdf");
				StringBuffer fileNameNuevo = new StringBuffer(ToolsHTML.getPathTmp()).append(File.separator).append(nameFile);

				//System.out.println("Fichero Ori:"+fileName);
				//System.out.println("Fichero Des:"+fileNameNuevo);

				File ori = new File(fileName.toString());
				File des = new File(fileNameNuevo.toString());
				
				Archivo.renameTo(ori,des);
			} else {
				StringBuffer fileName = new StringBuffer(ToolsHTML.getPathTmp()).append(File.separator).append("file_scanner_").append(usuario.getIdPerson()).append(".pdf");
				File scanned = new File(fileName.toString());
				if(scanned.exists()) {
					scanned.delete();
				}
			}
            
            int idCheckOut = Integer.parseInt(getParameter("idCheckOut")==null?parametros.getProperty("idCheckOut"):getParameter("idCheckOut"));
            //System.out.println("idCheckOut = " + idCheckOut);
            
            // si es un archivo en linea lo escribimos al disco antes de leerlo
            if(!isFileScanned && nameFile!=null && nameFile.endsWith("html")) {
	            Archivo a = new Archivo();
	            //System.out.println(ToolsHTML.getPathTmp().concat(nameFile));
	            //System.out.println(String.valueOf(request.getParameter("contenido")));
	            a.escribir(ToolsHTML.getPathTmp().concat(nameFile), String.valueOf(request.getParameter("contenido")));
            }

            
            try {
                HandlerDocuments.saveBD(nameFile,ToolsHTML.getPathTmp(),idCheckOut);
                
                // eliminamos el archivo del cache
                //ToolsHTML.deleteVersionCache(getParameter("idVersion"));
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            String closeWindow = getParameter("closeWindow")==null?parametros.getProperty("closeWindow"):getParameter("closeWindow");
            //System.out.println("closeWindow = " + closeWindow);
            if (!ToolsHTML.isEmptyOrNull(closeWindow)) {
                putAtributte("closeWindow","true");
                putObjectSession("info", " ");
                return goSucces();
            }

            String idNode = (String)ToolsHTML.getAttribute(request,"idNodeSelected");
            if (ToolsHTML.isEmptyOrNull(idNode)) {
                idNode = (String)getSessionObject("nodeActive");
            }

            ActionForward toStruct = null;
            if (ToolsHTML.isEmptyOrNull(idNode)) {
                toStruct = new ActionForward("/loadAllStruct.do",false);
            } else {
                toStruct = new ActionForward("/loadStructMain.do?idNodeSelected="+idNode,false);
            }
            return toStruct;
        } catch (ApplicationExceptionChecked ae) {
            ae.printStackTrace();
            return goError(ae.getKeyError());
        }
    }
}
