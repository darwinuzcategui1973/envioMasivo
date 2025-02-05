package com.focus.migracion.forms;

import java.io.Serializable;
import java.util.ResourceBundle;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;

import com.desige.webDocuments.utils.ToolsHTML;
import com.desige.webDocuments.utils.beans.SuperActionForm;


/**
 * Title: MigracionForm.java <br/>
 * Copyright: (c) 2007 Focus Consulting C.A.<br/>
 * @author YSA
 * @version WebDocuments v4.8
 * <br/>
 * Changes:<br/>
 * <ul>
 *      <li>18/09/2007 (YSA) Creation </li>
 </ul>
 */


public class MigracionForm extends SuperActionForm implements Serializable {
    static final long serialVersionUID = -4543694925489706834L;
    private String nombreArchivo;
    private String nombreLogs;

    public ActionErrors validate(ActionMapping actionMapping, HttpServletRequest httpServletRequest) {
        ResourceBundle rb = ToolsHTML.getBundle(httpServletRequest);
        
        if (!ToolsHTML.checkValue(this.nombreArchivo)){
            httpServletRequest.getSession().setAttribute("error",rb.getString("notUser"));
        }
        if (!ToolsHTML.checkValue(this.nombreLogs)){
            httpServletRequest.getSession().setAttribute("error,",rb.getString("notUser"));
            actionMapping.findForward("error");
        }
        return null;
    }
    public void reset(ActionMapping actionMapping, HttpServletRequest httpServletRequest) {
    }

    public String getNombreArchivo() {
        return nombreArchivo;
    }

    public void setNombreArchivo(String nombreArchivo) {
        this.nombreArchivo = nombreArchivo;
    }

    public String getNombreLogs() {
        return nombreLogs;
    }

    public void setNombreLogs(String nombreLogs) {
        this.nombreLogs = nombreLogs;
    }

    public void cleanForm(){
        setNombreArchivo("");
        setNombreLogs("");
    }

}
