package com.desige.webDocuments.typeDocuments.actions;

import java.util.Collection;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.desige.webDocuments.persistent.managers.ActivityBD;
import com.desige.webDocuments.persistent.managers.HandlerDBUser;
import com.desige.webDocuments.persistent.managers.HandlerTypeDoc;
import com.desige.webDocuments.typeDocuments.forms.TypeDocumentsForm;
import com.desige.webDocuments.utils.ApplicationExceptionChecked;
import com.desige.webDocuments.utils.ToolsHTML;
import com.desige.webDocuments.utils.beans.SuperAction;
import com.desige.webDocuments.utils.beans.SuperActionForm;

/**
 * Title: ${name}.java<br>
 * Copyright: (c) 2003 Consis International<br>
 * Company: Consis International (CON)<br>
 * @author Nelson Crespo (NC)
 * @version Acsel-e v2.2
 * <br>
 * Changes:<br>
 * <ul>
 *      <li> 22/08/2004 (NC) Creation </li>
 * <ul>
 */
public class LoadTypeDocumentsAction extends SuperAction {
    public ActionForward execute(ActionMapping actionMapping,
                                 ActionForm form, HttpServletRequest request,
                                 HttpServletResponse response) {
        super.init(actionMapping,form,request,response);
        String descript = request.getParameter("SearchDescrip");
        try {
            getUserSession();
            removeObjectSession("input");
            removeObjectSession("value");
            removeObjectSession("descript");
            removeObjectSession("dataTable");
            removeObjectSession("sizeParam");
            removeObjectSession("editManager");
            removeObjectSession("newManager");
            removeObjectSession("loadManager");
            removeObjectSession("loadEditManager");
            removeObjectSession("formEdit");
            Collection typeDoc = HandlerTypeDoc.getAllTypeDocs(descript,false);
            //putObjectSession("descript",""); //declarada en cargar normas, por eso se declara aqui
            removeObjectSession("dataTable");
            putObjectSession("dataTable",typeDoc);
            putObjectSession("sizeParam",String.valueOf(typeDoc.size()));
            putObjectSession("editManager","/editTypeDoc.do");
            putObjectSession("newManager","/newTypeDoc.do");
            putObjectSession("loadManager","loadTypeDoc.do");
            putObjectSession("loadEditManager","/loadTypeDocEdit.do");
            putObjectSession("formEdit","editTypeDoc");
            String cmd = getCmd(request,false);
            TypeDocumentsForm forma = (TypeDocumentsForm)form;
            if (forma==null) {
                //System.out.println("Forma Nula........");
                form = new TypeDocumentsForm();
            }
            if (ToolsHTML.checkValue(cmd)){
                ((SuperActionForm)form).setCmd(cmd);
                putAtributte("cmd",cmd);
//                request.setAttribute("cmd",cmd);
            } else{
                putAtributte("cmd",SuperActionForm.cmdLoad);
//                request.setAttribute("cmd",SuperActionForm.cmdLoad);
            }
            
            //System.out.println(request.getParameter("id"));
            // cargamos las actividades
            if(request.getParameter("id")!=null) {
            	String id = request.getParameter("id");
            	Collection acti = ActivityBD.getAllActivities(id);
            	putObjectSession("activities",acti);
            	
            	// los usuarios
            	Collection users = HandlerDBUser.getAllUsersWithIdPerson();
            	putObjectSession("users",users);
            	
            } else {
            	removeObjectSession("activities");
            }
            

            /*
            activities activity = null;
            activity = null;
            //activity = (Activities)HibernateUtil.loadObject(Activities.class,new Long (number));
            activity = (Activities)HibernateUtil.loadObject(Activities.class,new Long (number));
            */
            
            return goSucces();
        } catch (ApplicationExceptionChecked ae) {
            return goError(ae.getKeyError());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return goError();
    }
}
