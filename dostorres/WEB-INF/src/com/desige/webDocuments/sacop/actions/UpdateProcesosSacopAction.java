package com.desige.webDocuments.sacop.actions;

import java.util.ResourceBundle;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.desige.webDocuments.persistent.managers.HandlerProcesosSacop;
import com.desige.webDocuments.persistent.managers.HandlerTypeDoc;
import com.desige.webDocuments.persistent.utils.HibernateUtil;
import com.desige.webDocuments.sacop.forms.ProcesosSacop;
import com.desige.webDocuments.utils.ApplicationExceptionChecked;
import com.desige.webDocuments.utils.ToolsHTML;
import com.desige.webDocuments.utils.beans.SuperAction;
import com.desige.webDocuments.utils.beans.SuperActionForm;
import com.focus.qweb.dao.ProcesoSacopDAO;
import com.focus.qweb.to.ProcesoSacopTO;

/**
 * Created by IntelliJ IDEA.
 * User: srodriguez
 * Date: 07/12/2005
 * Time: 04:12:50 PM
 * To change this template use File | Settings | File Templates.
 */


/**
 * Created by IntelliJ IDEA.
 * User: srodriguez
 * Date: 07/12/2005
 * Time: 10:26:13 AM
 * To change this template use File | Settings | File Templates.
 */

public class UpdateProcesosSacopAction extends SuperAction {
	
	/**
	 * 
	 */
    public synchronized ActionForward execute(ActionMapping mapping,
                                 ActionForm form, HttpServletRequest request,
                                 HttpServletResponse response) {
        super.init(mapping,form,request,response);
        try {
            String cmd = request.getParameter("cmd");
            ProcesosSacop forma = (ProcesosSacop)form;
            request.getSession().setAttribute("otroProcesoSacop",form);

            getUserSession();
            if (ToolsHTML.checkValue(cmd)){
                if ((cmd.equalsIgnoreCase(SuperActionForm.cmdNew))||
                    (cmd.equalsIgnoreCase(SuperActionForm.cmdLoad))){
                    if (cmd.equalsIgnoreCase(SuperActionForm.cmdNew)){
                        removeObjectSession("valor");
                    }
                    form = new ProcesosSacop();
                    cmd = SuperActionForm.cmdInsert;
                } else{
                    processCmd(forma,request);
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

    /**
     * 
     * @param forma
     * @param request
     * @return
     * @throws ApplicationExceptionChecked
     */
     private static boolean processCmd (ProcesosSacop forma,HttpServletRequest request) throws ApplicationExceptionChecked{
        //System.out.println("forma = " + forma.getCmd());
        boolean resp = false;
        StringBuffer mensaje = null;
        ResourceBundle rb = ToolsHTML.getBundle(request);
        if (forma.getCmd().equalsIgnoreCase(SuperActionForm.cmdInsert)){
            //System.out.println("Insertar Registro...");
            try {
                resp = HandlerProcesosSacop.insert(forma);
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (resp){
                request.setAttribute("info",rb.getString("app.editOk"));
                forma.cleanForm();
                return true;
            } else{
                mensaje = new StringBuffer(rb.getString("app.notEdit"));
                mensaje.append(" ").append(HandlerTypeDoc.getMensaje());
            }
        } else{
            if (forma.getCmd().equalsIgnoreCase(SuperActionForm.cmdEdit)){
                //System.out.println("Editando Registro...");
            	ProcesoSacopTO oProcesoSacopTO = new ProcesoSacopTO(forma);
            	ProcesoSacopDAO oProcesoSacopDAO = new ProcesoSacopDAO();
            	
            	try {
					oProcesoSacopDAO.actualizar(oProcesoSacopTO);
				} catch (Exception e) {
					e.printStackTrace();
				}
                
                request.setAttribute("info",rb.getString("app.editOk"));
                return true;
            } else{
                if (forma.getCmd().equalsIgnoreCase(SuperActionForm.cmdDelete)){
                    //System.out.println("Elimando registro....");
                    if (HandlerProcesosSacop.delete(forma)){
                        forma.cleanForm();
                        request.setAttribute("info",rb.getString("app.delete"));
                    } else{
                        mensaje = new StringBuffer(rb.getString("app.notDelete"));
                        mensaje.append(" ").append(HandlerTypeDoc.getMensaje());
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
