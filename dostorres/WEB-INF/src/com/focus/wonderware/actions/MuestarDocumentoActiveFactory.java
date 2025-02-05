package com.focus.wonderware.actions;

import java.util.Hashtable;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.desige.webDocuments.document.forms.BaseDocumentForm;
import com.desige.webDocuments.persistent.managers.HandlerStruct;
import com.desige.webDocuments.utils.ToolsHTML;
import com.desige.webDocuments.utils.beans.SuperAction;
import com.desige.webDocuments.utils.beans.Users;

/**
 * Created by IntelliJ IDEA.
 * User: srodriguez
 * Date: Feb 14, 2007
 * Time: 10:36:18 AM
 * To change this template use File | Settings | File Templates.
 */
public class MuestarDocumentoActiveFactory extends SuperAction {
    static Logger log = LoggerFactory.getLogger(MuestarDocumentoActiveFactory.class.getName());
    public ActionForward execute(ActionMapping mapping,
                                 ActionForm form, HttpServletRequest request,
                                 HttpServletResponse response) {

        super.init(mapping,form,request,response);

        try{
             Users usuario = getUserSession();
            String documentoActiveFactory=(String)getSessionObject("documentoActiveFactory");
            BaseDocumentForm forma = new BaseDocumentForm();
            forma.setIdDocument(documentoActiveFactory);
            forma.setNumberGen(documentoActiveFactory);
            //cargamos la ultima version del documento en caso que venga de activefactory y
            //solo traiga el numgen del documento mas no la version
            Hashtable tree = (Hashtable)getSessionObject("tree");
            tree = ToolsHTML.checkTree(tree,usuario);
            HandlerStruct.loadDocument(forma,true,false,tree, request);
            StringBuffer item = new StringBuffer("");
            item.append("/viewDocument.jsp?nameFile=").append(forma.getNameFile()).append("&idDocument=");
            item.append(forma.getIdDocument()).append("&idVersion=").append(forma.getNumVer());
            ActionForward mostrarDoc = new ActionForward(item.toString(),false);
            return  mostrarDoc;
           // return goSucces();
        }catch(Exception e){
               log.error(e.getMessage());
            e.printStackTrace();
        } finally {
        }

        return goError();
    }
}
