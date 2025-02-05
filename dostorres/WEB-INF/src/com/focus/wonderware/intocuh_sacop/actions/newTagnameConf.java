package com.focus.wonderware.intocuh_sacop.actions;

import java.util.ResourceBundle;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.desige.webDocuments.persistent.managers.HandlerStruct;
import com.desige.webDocuments.persistent.managers.HandlerTypeDoc;
import com.desige.webDocuments.persistent.utils.HibernateUtil;
import com.desige.webDocuments.utils.ApplicationExceptionChecked;
import com.desige.webDocuments.utils.Constants;
import com.desige.webDocuments.utils.ToolsHTML;
import com.desige.webDocuments.utils.beans.SuperAction;
import com.desige.webDocuments.utils.beans.SuperActionForm;
import com.focus.qweb.dao.ConfTagNameDAO;
import com.focus.qweb.to.ConfTagNameTO;
import com.focus.wonderware.intocuh_sacop.forms.Sacop_Intouch_Conftagname;

/**
 * Created by IntelliJ IDEA.
 * User: srodriguez
 * Date: Mar 9, 2007
 * Time: 3:12:51 PM
 * To change this template use File | Settings | File Templates.
 */
public class newTagnameConf extends SuperAction {

	public ActionForward execute(ActionMapping mapping,
                                 ActionForm form, HttpServletRequest request,
                                 HttpServletResponse response) {
        super.init(mapping,form,request,response);
        String cmd = request.getParameter("cmd");
        Sacop_Intouch_Conftagname forma = (Sacop_Intouch_Conftagname)form;
        request.getSession().setAttribute("otroProcesoSacop",form);
        try {
            getUserSession();
            if (ToolsHTML.checkValue(cmd)){
                if ((cmd.equalsIgnoreCase(SuperActionForm.cmdNew))||
                    (cmd.equalsIgnoreCase(SuperActionForm.cmdLoad))){
                    if (cmd.equalsIgnoreCase(SuperActionForm.cmdNew)){
                        removeObjectSession("valor");
                    }
                    form = new Sacop_Intouch_Conftagname();
                    cmd = SuperActionForm.cmdInsert;
                } else{
                    processCmd(forma,request);
                    cmd = SuperActionForm.cmdLoad;
                }
                ((SuperActionForm)form).setCmd(cmd);
                request.setAttribute("cmd",cmd);
            }

            return goSucces();
        } catch (ApplicationExceptionChecked ae) {
            ae.printStackTrace();
            return goError(ae.getKeyError());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return goError();
    }

    private static boolean processCmd (Sacop_Intouch_Conftagname forma,HttpServletRequest request) throws ApplicationExceptionChecked{
        //System.out.println("forma = " + forma.getCmd());
        boolean resp = false;
        StringBuffer mensaje = null;
        ResourceBundle rb = ToolsHTML.getBundle(request);

    	ConfTagNameDAO oConfTagNameDAO = new ConfTagNameDAO();
    	ConfTagNameTO oConfTagNameTO = new ConfTagNameTO();

        if (forma.getCmd().equalsIgnoreCase(SuperActionForm.cmdInsert)){
            //System.out.println("Insertar Registro...");
            try {

            	
                oConfTagNameDAO.insertar(oConfTagNameTO);
                
                forma.setIdtagname(Long.parseLong(oConfTagNameTO.getIdTagName()));
                
                resp = true;
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (resp){
                request.setAttribute("info",rb.getString("app.editOk"));
               // forma.cleanForm();
                return true;
            } else{
                mensaje = new StringBuffer(rb.getString("app.notEdit"));
                mensaje.append(" ").append(HandlerTypeDoc.getMensaje());
            }
        } else{
            if (forma.getCmd().equalsIgnoreCase(SuperActionForm.cmdEdit)){
            		//System.out.println("Editando Registro...");
            		oConfTagNameTO.setIdTagName(String.valueOf(forma.getIdtagname()));
            		try {
						oConfTagNameDAO.cargar(oConfTagNameTO);
            		
	            		oConfTagNameTO.setActive(Constants.permissionSt);
	            		
	            		oConfTagNameDAO.actualizar(oConfTagNameTO);
	            		
	                    request.setAttribute("info",rb.getString("app.editOk"));
					} catch (Exception e) {
	                    request.setAttribute("info",rb.getString("app.notEdit"));
						e.printStackTrace();
					}
                    return true;
            } else{
                if (forma.getCmd().equalsIgnoreCase(SuperActionForm.cmdDelete)){
                    //System.out.println("Elimando registro....");
                	try {
						oConfTagNameTO.setIdTagName(String.valueOf(forma.getIdtagname()));
						oConfTagNameDAO.eliminar(oConfTagNameTO);
					} catch (Exception e) {
	                    request.setAttribute("info",rb.getString("app.notEdit"));
						e.printStackTrace();
					}
                }
            }
        }
        if (mensaje!=null){
            //System.out.println("mensaje = " + mensaje);
            request.setAttribute("info",mensaje.toString());
        }
        return false;
    }





}
