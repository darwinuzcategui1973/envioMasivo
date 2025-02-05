package com.desige.webDocuments.mail.actions;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.desige.webDocuments.persistent.managers.HandlerDBUser;
import com.desige.webDocuments.persistent.managers.HandlerMessages;
import com.desige.webDocuments.utils.ApplicationExceptionChecked;
import com.desige.webDocuments.utils.ToolsHTML;
import com.desige.webDocuments.utils.beans.SuperAction;

/**
 * Title: LoadContactsAction.java<br>
  * Copyright: (c) 2004 Focus Consulting C.A.<br/>
 * @author Ing. Nelson Crespo (NC)
 * @version WebDocuments v3.0
 * <br>
 * Changes:<br>
 * <ul>
 *      <li> 07/08/2004 (NC) Creation </li>
 * <ul>
 */
public class LoadContactsAction extends SuperAction {
	public ActionForward execute(ActionMapping mapping,
                                 ActionForm form, HttpServletRequest request,
                                 HttpServletResponse response) {
        super.init(mapping,form,request,response);

        try {
            String idUser = getUserSession().getUser();
            String selected = request.getParameter("selected");
            removeObjectSession("addressMail");
            ArrayList docLinks = ToolsHTML.createList(selected);
            String loadContactSystems = request.getParameter("contactSystems");
            //System.out.println("loadContactSystems = " + loadContactSystems);

            String searchName = request.getParameter("search");
            //System.out.println("[searchName] = " + searchName);
            
            Collection AllAddress = new Vector();
            if ("true".equalsIgnoreCase(loadContactSystems)) {
                AllAddress = HandlerDBUser.getAllAddress(docLinks,searchName);
            } else {
                AllAddress = HandlerMessages.getAllAddress(idUser,docLinks,searchName);
            }
            String showLinkSystem = request.getParameter("showLinkSystem");
            //System.out.println("[showLinkSystem] = " + showLinkSystem);
            if (!ToolsHTML.isEmptyOrNull(showLinkSystem)) {
                putAtributte("showLinkSystem",showLinkSystem);
            } else {
                removeAttribute("showLinkSystem");
            }
            String showAddressBook = request.getParameter("showAddressBook");
            //System.out.println("[showAddressBook] = " + showAddressBook);
            if (!ToolsHTML.isEmptyOrNull(showAddressBook)) {
                putAtributte("showAddressBook",showAddressBook);
            } else {
                removeAttribute("showAddressBook");
            }
            putObjectSession("Lista",AllAddress);
            String input = request.getParameter("input");
            String value = request.getParameter("value");
            String ids = request.getParameter("ids");

            //System.out.println("input = " + input);
            //System.out.println("value = " + value);
            //System.out.println("ids = " + ids);

            if (!ToolsHTML.isEmptyOrNull(searchName)) {
                putAtributte("search",searchName);
            }
            
            if (!ToolsHTML.isEmptyOrNull(input)) {
                putAtributte("notShow","notShow");
            }
            String nameForm = request.getParameter("nameForm");
            putAtributte("input",input);
            putAtributte("value",value);
            putAtributte("nameForm",nameForm);

            String emails = request.getParameter("emails");
            String names = request.getParameter("names");
//            String selected = request.getParameter("selected");
            putAtributte("emails",emails);
            putAtributte("names",names);
            putAtributte("selected",selected);

            putObjectSession("input",getDataFormResponse(nameForm,input,true));
            putObjectSession("value",getDataFormResponse(nameForm,value,false));
            if ((!ToolsHTML.isEmptyOrNull(nameForm))&&(!ToolsHTML.isEmptyOrNull(ids))) {
                String result = "opener.document."+nameForm+"."+ids+".value = ids";
                putObjectSession("ids",result);
            }
            return goSucces();
        } catch (ApplicationExceptionChecked ae) {
            return goError(ae.getKeyError());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return goError();
    }

}
