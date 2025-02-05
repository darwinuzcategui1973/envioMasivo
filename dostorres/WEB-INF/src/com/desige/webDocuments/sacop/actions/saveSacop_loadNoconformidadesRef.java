package com.desige.webDocuments.sacop.actions;

import java.util.ArrayList;
import java.util.StringTokenizer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.desige.webDocuments.utils.ToolsHTML;
import com.desige.webDocuments.utils.beans.SuperAction;

/**
 * Created by IntelliJ IDEA.
 * User: srodriguez
 * Date: Feb 7, 2007
 * Time: 11:41:01 AM
 * To change this template use File | Settings | File Templates.
 */
public class saveSacop_loadNoconformidadesRef extends SuperAction {
    static Logger log = LoggerFactory.getLogger(SaveSacop_Relacionadas.class.getName());
    public ActionForward execute(ActionMapping mapping,
                                 ActionForm form,
                                 HttpServletRequest request,
                                 HttpServletResponse response) {
        log.debug("Begin");
        super.init(mapping,form,request,response);
        try {
        	 
             putAtributte("closeWindow","true");
             putObjectSession("closeWindow","true");

             if(request.getParameter("cambio")!=null && request.getParameter("cambio").equals("true")) {
            	 putAtributte("cambio",request.getParameter("cambio"));
             }
             
                String valuesSelecteds = getParameter("userAssociate");
                StringBuffer items = new StringBuffer(100);
                String value = (String)getSessionObject("value");
                if (!ToolsHTML.isEmptyOrNull(value)) {
                    int posI = value.indexOf("=");
                    items.append(value.substring(0,posI)).append("='");
                }
                if (!ToolsHTML.isEmptyOrNull(valuesSelecteds)) {
                    StringTokenizer st = new StringTokenizer(valuesSelecteds,",");
                    ArrayList arrlst= new ArrayList();
                    while (st.hasMoreTokens()) {
                        String item = st.nextToken();
                        if ((ToolsHTML.isNumeric(item))&& !"-1".equalsIgnoreCase(item)) {
                            if (!arrlst.contains(item)){
                                items.append(item).append(", ");
                                arrlst.add(item);
                            }
                        }
                    }

                    if (items.length() > 0) {
                    	if(items.toString().endsWith("='")) {
                    		items.append("';");
                    	} else {
                    		items.replace(items.length()-2,items.length(),"';");
                    	}
                        putObjectSession("value",items.toString());
                        log.debug("Items: " + items);
                    }

            }
            log.debug("End");


            //son variables originales, que se vuelven a utilizar en caso que se quiera ejecutar el formulario de busqueda
            String input = request.getParameter("input");
            String value1 = request.getParameter("value");
            String nameForm = request.getParameter("nameForma");
            String type1 = request.getParameter("type");
            String userAssociate1 = request.getParameter("userAssociate");
            putAtributte("inputb",input);
            putAtributte("valueb",value1);
            putAtributte("nameFormab",nameForm);
            putAtributte("typeb",type1);
            putAtributte("userAssociateb",userAssociate1);
            //solo para busqueda las varoiables arriba
            String number = getParameter("number");
            putAtributte("number",number);
            //me permitesaber si vienen las sacop ya relacionadas o si se va a crear una sacop y
            //se va a relacionar por  primera vez
            String relacionarBusqueda="1";
            if ("1".equalsIgnoreCase(number)){
                relacionarBusqueda="-1";
            }
                putAtributte("relacionarBusqueda",relacionarBusqueda);
            //-------------------------------fin var busquedas-----------------------------------------------------------------------------
            //System.out.println("fin satisfactorio");
            return goSucces();
        } catch (Exception ex) {
            log.error(ex.getMessage());
            ex.printStackTrace();
        } finally {
            try {
                //HibernateUtil.closeSession();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        log.debug("End");
        return goError();
    }
}
