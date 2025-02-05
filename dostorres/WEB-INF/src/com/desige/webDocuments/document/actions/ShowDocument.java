package com.desige.webDocuments.document.actions;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Hashtable;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.desige.webDocuments.document.forms.BaseDocumentForm;
import com.desige.webDocuments.persistent.managers.HandlerStruct;
import com.desige.webDocuments.utils.ApplicationExceptionChecked;
import com.desige.webDocuments.utils.ToolsHTML;
import com.desige.webDocuments.utils.beans.SuperAction;
import com.desige.webDocuments.utils.beans.Users;

/**
 * Title: ShowDocument.java<br>
 * Copyright: (c) 2003 Consis International<br>
 * Company: Consis International (CON)<br>
 * @author Nelson Crespo (NC)
 * @author Simón Rodriguéz (SR)
 * @version Acsel-e v2.2
 * <br>
 * Changes:<br>
 * <ul>
 *      <li> 21/07/2004 (NC) Creation </li>
 *      <li> 14/05/2005 (NC) Cambios en el manejo de los documentos </li>
 *      <li> 30/06/2005 (SR) se comento getUserSession</li>
 *      <li> 13/07/2006 (NC) Cambios para heredar o no los Prefijos de las Carpetas </li>
  * <ul>
 */
public class ShowDocument extends SuperAction {

    private static ArrayList elementos = ToolsHTML.getProperties("visor.OpenBrowser");

    public ActionForward execute(ActionMapping mapping,
                                 ActionForm form, HttpServletRequest request,
                                 HttpServletResponse response) {
        super.init(mapping,form,request,response);
        BaseDocumentForm forma = new BaseDocumentForm();
        forma.setIdDocument(request.getParameter("idDocument"));
        forma.setNumberGen(request.getParameter("idDocument"));
        forma.setNumVer(Integer.parseInt(request.getParameter("idVersion")));
        try {
            Users usuario = getUserSession();
            boolean download = (request.getParameter("downFile")!=null && request.getParameter("downFile").equals("true") );
            boolean downloadLastApprovedVersion = Boolean.parseBoolean(request.getParameter("downloadLastApproved"));
            boolean downloadLastEraserVersion = Boolean.parseBoolean(request.getParameter("downloadLastEraser"));
            
            if (forma.getNumVer()!=0) {
                Hashtable tree = (Hashtable) getSessionObject("tree");
                tree = ToolsHTML.checkTree(tree,usuario);
                
                HandlerStruct.loadDocument(forma,
                		downloadLastEraserVersion ? false : download,
                		downloadLastApprovedVersion,
                		tree,
                		request);
                
                putObjectSession("showDocument",forma);
                String path = ToolsHTML.getPath().concat("tmp");  // \\tmp
                HandlerStruct.createFile(forma,path,false,download);
                if (forma.getNameFile() != null) {
                    if (processFile(response,request,path,forma)) {
                    	if(!download){
                    		return goSucces();
                    	}else{
                    		return null;
                    	}
                    }
                }
            } else {
                throw new ApplicationExceptionChecked("E0053");
            }
        } catch (ApplicationExceptionChecked ae) {
            ae.printStackTrace();
            return goError(ae.getKeyError());
        } catch (Exception e) {
            e.printStackTrace();
            return goError();
        }
        return goError();
    }

    public static String getExtensionFile(String nameFile){
        int pos = nameFile.indexOf(".");
        String extension = nameFile.substring(pos).toUpperCase();
        return extension;
    }

    public static void closeIOs(InputStream in,OutputStream out){
        if (in!=null){
            try {
                in.close();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        if (out!=null){
            try {
                out.close();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    public static synchronized boolean processFile(HttpServletResponse response,HttpServletRequest request,String path,
                                                   BaseDocumentForm forma){
        String extension = getExtensionFile(forma.getNameFile());
        response.setContentType(forma.getContextType());
        InputStream in = null;
        OutputStream out = null;


        String initServer = ToolsHTML.getServerPath(ToolsHTML.getServerName(request),request.getServerPort(),request.getContextPath());
        //String fileURL =  initServer + "/tmp/" + forma.getNameFile();
        String fileURL =  initServer + "/" + ToolsHTML.getFolderTmp() + "/" + forma.getNameFile();
        boolean isVisioDoc = elementos.contains(extension);
        try {
            boolean onlyRead = request.getParameter("downFile")==null;//?true:false;
            //System.out.println("onlyRead = " + onlyRead);
            response.setHeader("Pragma","no-cache");
            response.addHeader( "Cache-Control", "must-revalidate" );
            response.setHeader("Cache-control","no-cache");
            response.addHeader( "Cache-Control", "no-store" );
            response.setDateHeader ("Expires", 0);
            if ((!isVisioDoc)||(!onlyRead)) {
                if(onlyRead) {
                    response.setHeader("Content-disposition", "inline;open; filename=\"" + forma.getNameFile() + "\"");
                } else {
                    response.setHeader("Content-disposition", "attachment; filename=\"" + forma.getNameFile() + "\"");
                }
                response.setHeader("content-transfer-encoding", "binary");
                File fichero = new File(path.concat(File.separator).concat(forma.getNameFile()));
//                response.setHeader("Content-Length",""+fichero.length());
                response.setHeader("Content-Type",forma.getContextType());
                //System.out.println("bytes del Archivo = " + fichero.length());
                in = new FileInputStream(fichero);
                response.setHeader("Content-Length",""+in.available());
                out = response.getOutputStream();
                int bytesRead = 0;
                byte buffer[] = new byte[8192];
                while((bytesRead = in.read(buffer, 0, 8192)) != -1) {
                    out.write(buffer, 0, bytesRead);
                    response.flushBuffer();
                }
                closeIOs(in,out);
                try {
                    //System.out.println("DELETE");
                    fichero.delete();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                return true;
            } else {
                response.setHeader("Cache-control","no-cache");
                response.setHeader("Pragma","no-cache");
                response.setDateHeader ("Expires", 0);
                response.sendRedirect(fileURL);
                return true;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            closeIOs(in,out);
        }
        return false;
    }

}
