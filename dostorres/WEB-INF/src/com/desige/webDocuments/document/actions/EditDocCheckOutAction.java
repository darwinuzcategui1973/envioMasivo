package com.desige.webDocuments.document.actions;

import java.util.Hashtable;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.desige.webDocuments.document.forms.BaseDocumentForm;
import com.desige.webDocuments.persistent.managers.HandlerDocuments;
import com.desige.webDocuments.persistent.managers.HandlerStruct;
import com.desige.webDocuments.utils.ApplicationExceptionChecked;
import com.desige.webDocuments.utils.ToolsHTML;
import com.desige.webDocuments.utils.beans.SuperAction;
import com.desige.webDocuments.utils.beans.Users;

/**
 * Title: EditDocCheckOutAction.java <br/>
 * Copyright: (c) 2004 Focus Consulting C.A.<br/>
 *
 * @author Ing. Nelson Crespo
 * @version WebDocuments v3.0
 * <br/>
 *      Changes:<br/>
 * <ul>
 *      <li> 18/07/2005 (NC) Creation </li>
 *      <li> 13/07/2006 (NC) Cambios para heredar o no los Prefijos de las Carpetas </li>
 * </ul>
 */
public class EditDocCheckOutAction extends SuperAction {
    static Logger log = LoggerFactory.getLogger("[V4.0] " + EditDocCheckOutAction.class.getName());
    public ActionForward execute(ActionMapping actionMapping,
                                 ActionForm form, HttpServletRequest request,
                                 HttpServletResponse response) {
        super.init(actionMapping,form,request,response,true);
        try {
            Users usuario = getUserSession();
            String path = ToolsHTML.getPath().concat("tmp"); // \\tmp
            log.debug("path = " + path);
            BaseDocumentForm forma = new BaseDocumentForm();
            //System.out.println("getParameter(\"idCheckOut\") ="+String.valueOf(getParameter("idCheckOut")));
            forma.setCheckOut(Integer.parseInt(getParameter("idCheckOut")));
            forma.setIdDocument(getParameter("idDocument"));
            forma.setNumberGen(getParameter("idDocument"));
            forma.setNumVer(Integer.parseInt(getParameter("numVersion")));
            Hashtable tree = (Hashtable)getSessionObject("tree");
            tree = ToolsHTML.checkTree(tree,usuario);

            //Se carga el documento en un archivo fisico
            HandlerStruct.loadDocument(forma,false,false,tree, request);

            //se coloca la ruta
            String rout = ToolsHTML.getPath();
            String routFiles = rout.replace('\\','/');
            log.debug("[routFiles] = " + routFiles);
            forma.setNameFile(getParameter("nameFile"));
            int pos = forma.getNameFile().indexOf(".");
            while (forma.getNameFile().indexOf(".",pos + 1)>0) {
                pos = forma.getNameFile().indexOf(".",pos + 1);
            }
            log.debug("[pos] = " + pos);
            if (pos > 0) {
                String nameFile = usuario.getUser();
                String ext = forma.getNameFile().substring(pos+1).trim();
                log.debug("[ext] = " + ext);
                forma.setNameFile(nameFile + "." +ext);
                HandlerDocuments.createFile(forma,path);
                String app = ToolsHTML.getApplicationStart(ext);
                log.debug("app = " + app);
                if (app!=null) {
                    putObjectSession("onLoad",app);
                }
                if (forma.getNameFile() != null) {
                     if (processFile(response,request,forma,routFiles)) {
                        return goSucces();
                    }
                }
//                }
            }
        } catch (ApplicationExceptionChecked ae) {
            ae.printStackTrace();
            return goError(ae.getKeyError());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return goError();
    }



    private synchronized boolean processFile(HttpServletResponse response,HttpServletRequest request, BaseDocumentForm forma,String path) {
//        String fileURL = DesigeConf.getProperty("dirServer") + forma.getNameFile();
        String fileURL = ToolsHTML.getServerPath(ToolsHTML.getServerName(request),request.getServerPort(),request.getContextPath()) + "/tmp/" + forma.getNameFile();
        log.debug("forma.getNameFile() = " + forma.getNameFile());
        response.setHeader("content-disposition", "inline;open; filename=\"" + forma.getNameFile() + "\"");
        response.setHeader("content-transfer-encoding", "binary");
        response.setHeader("Cache-control","no-cache");
        response.setHeader("Pragma","no-cache");
        //response.setContentType(forma.getContextType());
        //System.out.println(request.getSession().getServletContext().getMimeType(forma.getNameFile()));
        response.setContentType(request.getSession().getServletContext().getMimeType(forma.getNameFile()));
        log.debug("forma.getContextType() = " + forma.getContextType());
        response.setDateHeader ("Expires", 0);
        request.setAttribute("nameFileTest",fileURL);
        request.setAttribute("nameDoc",forma.getNameFile());
        request.setAttribute("nameServer",ToolsHTML.getPathFile(ToolsHTML.getServerName(request)));
        request.setAttribute("pathdocumentos",path);
        return true;
    }

}
