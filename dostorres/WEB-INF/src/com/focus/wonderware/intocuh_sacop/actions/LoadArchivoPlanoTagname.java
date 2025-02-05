package com.focus.wonderware.intocuh_sacop.actions;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.desige.webDocuments.utils.beans.SuperAction;

/**
 * Created by IntelliJ IDEA.
 * User: srodriguez
 * Date: 15/03/2007
 * Time: 01:36:49 PM
 * To change this template use File | Settings | File Templates.
 */
public class LoadArchivoPlanoTagname  extends SuperAction {
    static Logger log = LoggerFactory.getLogger(LoadArchivoPlanoTagname.class.getName());
    public ActionForward execute(ActionMapping mapping,
                                 ActionForm form, HttpServletRequest request,
                                 HttpServletResponse response) {

        super.init(mapping,form,request,response);

        try{

                return goSucces();
        }catch(Exception e){
               log.error(e.getMessage());
            e.printStackTrace();
        } finally {
        }

        return goError();
    }
}
