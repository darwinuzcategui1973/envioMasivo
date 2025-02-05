package com.desige.webDocuments.mail.actions;

import java.util.ResourceBundle;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.desige.webDocuments.mail.forms.AddressForm;
import com.desige.webDocuments.persistent.managers.HandlerBD;
import com.desige.webDocuments.persistent.managers.HandlerMessages;
import com.desige.webDocuments.utils.ApplicationExceptionChecked;
import com.desige.webDocuments.utils.ToolsHTML;
import com.desige.webDocuments.utils.beans.SuperAction;
import com.desige.webDocuments.utils.beans.SuperActionForm;
import com.desige.webDocuments.utils.beans.Users;

public class EditContactsAction extends SuperAction {
    static Logger log = LoggerFactory.getLogger("[V4.0] " + EditContactsAction.class.getName());
	public ActionForward execute(ActionMapping mapping,
                                 ActionForm form, HttpServletRequest request,
                                 HttpServletResponse response) {
        super.init(mapping,form,request,response);
        String cmd = request.getParameter("cmd");
        //System.out.println("cmd = " + cmd);
        try {
            getUserSession();
            return processCMD(cmd,request,form);
        } catch (ApplicationExceptionChecked ae) {
            log.error(ae.getMessage());
            ae.printStackTrace();
            return goError(ae.getKeyError());
        } catch (Exception ex) {
            log.error(ex.getMessage());
            ex.printStackTrace();
        }
        return goError();
    }

    public ActionForward processCMD(String cmd,HttpServletRequest request,ActionForm form) throws ApplicationExceptionChecked,Exception {
        if (cmd==null) {
            AddressForm forma = new AddressForm();
            request.getSession().setAttribute("addressMail",forma);
            return goSucces("success");// mapping.findForward("success");
        } else {
            String user =  ((Users)request.getSession().getAttribute("user")).getUser();
            AddressForm forma = (AddressForm)form;
            if (SuperActionForm.cmdInsert.equalsIgnoreCase(cmd)) {
                String[] items = HandlerBD.getField(new String[]{"c.idAddress"},"address a,contacts c",
                                                    new String[]{"a.idAddress","a.active","c.idUser","a.email"},
                                                    new String[]{"c.idAddress","1",user,forma.getEmail()},new String[]{"=","=","=","="},
                                                    new Object[]{new Integer(1),new Byte("1"),"",""});
                if (items!=null) {
                    throw new ApplicationExceptionChecked("E0075");
                }
                boolean resp = HandlerMessages.insertContacts(forma,user);
                ResourceBundle rb = ToolsHTML.getBundle(request);
                if (resp) {
                    request.setAttribute("info",rb.getString("app.editOk"));
                    forma.cleanForm();
                    return goSucces();
                } else {
                    StringBuffer mensaje = new StringBuffer(rb.getString("app.notEdit"));
                    mensaje.append("").append(HandlerMessages.getMensaje());
                }
                return goError();
            } else {
                if (SuperActionForm.cmdDelete.equalsIgnoreCase(cmd)) {
                    try {
                        String idAddress = request.getParameter("ids");
                        boolean resp = HandlerMessages.delete(idAddress,user);
                        if (resp) {
                            return goSucces();
                        }
                    } catch (ApplicationExceptionChecked ex) {
                        ex.printStackTrace();
                    }
                } else {
                    if (SuperActionForm.cmdEdit.equalsIgnoreCase(cmd)) {
                        try {
                            String[] items = HandlerBD.getField(new String[]{"c.idAddress"},"address a,contacts c",
                                                    new String[]{"a.idAddress","a.active","c.idUser","a.email","a.idAddress"},
                                                    new String[]{"c.idAddress","1",user,forma.getEmail(),forma.getIdAddress()},new String[]{"=","=","=","=","<>"},
                                                    new Object[]{new Integer(1),new Integer(1),"","",new Integer(1)});
                            if (items!=null) {
                                throw new ApplicationExceptionChecked("E0075");
                            }
                            boolean resp = HandlerMessages.editContact(forma);
                            if (resp) {
                                request.setAttribute("info",getMessage("app.editOk"));
                                return goSucces();
                            }
                        } catch (ApplicationExceptionChecked ae) {
                            throw ae;
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }
                }
            }
        }
        return goError();
    }

}
