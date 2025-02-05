package com.desige.webDocuments.registers.actions;

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
 * Title: EditRegister.java <br/>
 * Copyright: (c) 2004 Focus Consulting C.A.<br/>
 * @author Ing. Nelson Crespo
 * @author Ing. Simón Rodriguéz
 * @version WebDocuments v3.0
 * <br/>
 *      Changes:<br/>
 * <ul>
 *      <li> 18/02/2005 (NC) Creation </li>
 *      <li> 08/06/2005 (SR) Se agrego una validación para que aceptara un archivo .txt y lo abriera en word. </li>
 *      <li> 08/06/2005 (SR) Se manda una url del archivo fisico, cambiando las ("\") por ("/"). </li>
 *      <li> 13/07/2006 (NC) Cambios para heredar o no los Prefijos de las Carpetas </li>
 * </ul>
 */
public class EditRegister extends SuperAction {
    public ActionForward execute(ActionMapping actionMapping,
                                 ActionForm form, HttpServletRequest request,
                                 HttpServletResponse response) {
        super.init(actionMapping,form,request,response);
        try {
            Users usuario = getUserSession();
            BaseDocumentForm forma = new BaseDocumentForm();
            forma.setIdDocument(getParameter("idDocument"));
            forma.setNumberGen(getParameter("idDocument"));
            forma.setNumVer(Integer.parseInt(getParameter("idVersion")));
            String path = ToolsHTML.getPath().concat("tmp"); // \\tmp
            //junio 7 del 2005 inicio
            String nu= ToolsHTML.getPath();
            String pathenvio =nu.replace('\\','/');
            //junio 7 del 2005 fin
            Hashtable tree = (Hashtable)getSessionObject("tree");
            tree = ToolsHTML.checkTree(tree,usuario);
            HandlerStruct.loadDocument(forma,false,false,tree, request);
            forma.setNameFile(getParameter("nameFile"));
//            try {
//                File file = new File("C:\\tmpWDoc\\"+forma.getNameFile());
//                //System.out.println("file = " + file);
//                //System.out.println("forma.getNameFile() = " + forma.getNameFile());
//                if (file!=null&&file.exists()) {
//                    //System.out.println("Check File.....");
//                    file.delete();
//                }
//            } catch (Exception ex) {
//                ex.printStackTrace();
//            }
            int pos = forma.getNameFile().indexOf(".");
            while (forma.getNameFile().indexOf(".",pos + 1)>0) {
                pos = forma.getNameFile().indexOf(".",pos + 1);
            }

            //System.out.println("pos = " + pos);
            if (pos > 0) {
                //String nameFile = usuario.getNameUser();
                //simon 6 de junio 2005 inicio
//                String nameFile = forma.getNameFile().substring(0,pos);
                //Simon 6 de junio 2005 fin
                String ext = forma.getNameFile().substring(pos+1,forma.getNameFile().length());
                ext = ext.trim();
                String app = ToolsHTML.getApplicationStart(ext);
                if (app!=null) {
                    putObjectSession("onLoad",app);
                }

//                if (ext.equalsIgnoreCase("xls")) {
//                    putObjectSession("onLoad","javascript:startExcel();");
//                }
//                if (ext.equalsIgnoreCase("doc")) {
//                    putObjectSession("onLoad","javascript:startWord();");
//                }
//                if (ext.equalsIgnoreCase("txt")) {
//                      putObjectSession("onLoad","javascript:startWord();");
//                }
//
//                if ((!(ext.equalsIgnoreCase("doc")))&&(!(ext.equalsIgnoreCase("xls")))&&(!(ext.equalsIgnoreCase("txt")))) {
//                      putObjectSession("onLoad","javascript:extNoseEdita();");
//                }
            }
            HandlerStruct.createFile(forma,path,true);
            //System.out.println("[path] = " + path);
            if (forma.getNameFile() != null) {
                 if (processFile(response,request,forma,pathenvio)) {
                    return goSucces();
                }
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
        //System.out.println("forma.getNameFile() = " + forma.getNameFile());
        response.setHeader("content-disposition", "inline;open; filename=\"" + forma.getNameFile() + "\"");
        response.setHeader("content-transfer-encoding", "binary");
        response.setHeader("Cache-control","no-cache");
        response.setHeader("Pragma","no-cache");
        response.setContentType(forma.getContextType());
        //System.out.println("forma.getContextType() = " + forma.getContextType());
        response.setDateHeader ("Expires", 0);
        request.setAttribute("nameFileTest",fileURL);
        request.setAttribute("nameDoc",forma.getNameFile());
        request.setAttribute("nameServer",ToolsHTML.getPathFile(ToolsHTML.getServerName(request)));
        //junio 7 del 2005 inicio
        request.setAttribute("pathdocumentos",path);
        //junio 7 del 2005 fin
        // DesigeConf.getProperty("nameDirServer"));
        return true;
    }

}
