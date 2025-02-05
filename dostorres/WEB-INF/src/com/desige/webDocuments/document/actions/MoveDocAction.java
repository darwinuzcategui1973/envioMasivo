package com.desige.webDocuments.document.actions;

import java.util.Hashtable;
import java.util.ResourceBundle;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.desige.webDocuments.dao.DocumentDAO;
import com.desige.webDocuments.document.forms.MoveDocForm;
import com.desige.webDocuments.persistent.managers.HandlerDocuments;
import com.desige.webDocuments.utils.ApplicationExceptionChecked;
import com.desige.webDocuments.utils.ToolsHTML;
import com.desige.webDocuments.utils.beans.SuperAction;
import com.desige.webDocuments.utils.beans.Users;

/**
 * Title: MoveDocAction.java <br/>
 * Copyright: (c) 2004 Focus Consulting C.A.<br/>
 * @author Ing. Nelson Crespo
 * @version WebDocuments v3.0
 * <br/>
 *       Changes:<br/>
 * <ul>
 *       <li> 06/07/2005 (NC) Creation </li>
 * </ul>
 */

public class MoveDocAction extends SuperAction {
    public ActionForward execute(ActionMapping mapping,
                                 ActionForm form, HttpServletRequest request,
                                 HttpServletResponse response) {
        ResourceBundle rb = ToolsHTML.getBundle(request);
        super.init(mapping,form,request,response,rb);
        MoveDocForm formulario = null;
        try {
            if (form instanceof MoveDocForm) {
            	formulario = (MoveDocForm) form;
            	
            	//debemos verificar que la version actual del documento que queremos mover
            	//no sea un borrador sin codigo
            	//ya que esa combinacion no debe ser tomada en cuenta a la hora de buscar codigos unicos
            	//por localidades, carpeta o sistema.
            	boolean isEraserWithoutCode = DocumentDAO.isEraserWithoutCode(formulario.getIdDocument());
            	
                Users usuario = getUserSession();
                Hashtable tree = (Hashtable)getSessionObject("tree");
                tree = ToolsHTML.checkTree(tree,usuario);

                if (HandlerDocuments.movementDocument(getSession(),usuario,formulario,formulario.getIdDocument(), formulario.getToID(),
                                                      formulario.getFromID(),usuario.getUser(),
                                                      formulario.getComments(),tree,isEraserWithoutCode)) {
                    putObjectSession("info",rb.getString("doc.change")); //+" "+ formulario.getComments());
                    String next = "/loadStructMain.do?idNodeSelected=" + formulario.getToID();
                    return new ActionForward(next,false);
                } else {
                    StringBuffer mensaje = new StringBuffer(rb.getString("app.notChange"));
                    mensaje.append(" ").append(HandlerDocuments.getMensaje());
                    putObjectSession("info",mensaje.toString());
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
}
