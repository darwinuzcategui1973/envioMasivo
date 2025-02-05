package com.desige.webDocuments.structured.actions;

import java.util.Hashtable;
import java.util.ResourceBundle;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.desige.webDocuments.document.forms.MoveDocForm;
import com.desige.webDocuments.persistent.managers.HandlerStruct;
import com.desige.webDocuments.structured.forms.BaseStructForm;
import com.desige.webDocuments.utils.ApplicationExceptionChecked;
import com.desige.webDocuments.utils.ToolsHTML;
import com.desige.webDocuments.utils.beans.SuperAction;
import com.desige.webDocuments.utils.beans.Users;

/**
 * Title: MoveStructAction.java <br/>
 * Copyright: (c) 2004 Focus Consulting C.A.<br/>
 * @author Ing. Nelson Crespo
 * @version WebDocuments v3.0
 * <br/>
 *      Changes:<br/>
 * <ul>
 *      <li> 20/07/2005 (NC) Creation </li>
 *      <li> 14/08/2005 (NC) Cambios para Actualizar el Prefijo del Nodo
 * </ul>
 */
public class MoveStructAction extends SuperAction {
    public ActionForward execute(ActionMapping mapping,
                                 ActionForm form, HttpServletRequest request,
                                 HttpServletResponse response) {
        ResourceBundle rb = ToolsHTML.getBundle(request);
        super.init(mapping,form,request,response,rb);
        MoveDocForm formulario = null;
        try {
            if (form instanceof MoveDocForm) {
                formulario = (MoveDocForm)form;
                Users usuario = getUserSession();
                StringBuffer mensaje = new StringBuffer(100);
                if (HandlerStruct.moveNode(formulario.getIdDocument(),formulario.getToID(),formulario.getFromID(),
                                           usuario.getUser(),formulario.getComments())) {
                    Hashtable tree = (Hashtable) getSessionObject("tree");
                    putObjectSession("info",rb.getString("app.change"));

                    BaseStructForm forma = new BaseStructForm();
                    forma.setIdNode(formulario.getIdDocument());
                    HandlerStruct.load(usuario.getUser(),forma);
                    tree.put(forma.getIdNode().trim(),forma);
                    Hashtable arbol = (Hashtable)getSessionObject("arbol");
                    StringBuffer node = new StringBuffer(ToolsHTML.getSubNodes(formulario.getToID(),"itemPpal",tree,arbol));
                    node.append("parent.frames['code'].updateArbol(itemPpal,");
                    String nodeData = (String)arbol.get(formulario.getToID());
                    node.append(nodeData).append(");");
                    StringBuffer nodeII = new StringBuffer(ToolsHTML.getSubNodes(formulario.getFromID(),"itemPpalII",tree,arbol));
                    nodeII.append("parent.frames['code'].updateArbol(itemPpalII,");
                    nodeData = (String)arbol.get(formulario.getFromID());
                    nodeII.append(nodeData).append(");");
                    putObjectSession("updateNode",node.toString());
                    putObjectSession("updateNodeII",nodeII.toString());
                    putObjectSession("info",rb.getString("app.change"));
                    String next = "/loadStructMain.do?idNodeSelected=" + formulario.getToID();
                    ActionForward resp = new ActionForward(next,false);
                    return resp;
                } else{
                    mensaje = new StringBuffer(rb.getString("app.notChange"));
                    mensaje.append(" ").append(HandlerStruct.getMensaje());
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
