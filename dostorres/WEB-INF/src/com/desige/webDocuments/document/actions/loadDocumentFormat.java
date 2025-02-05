package com.desige.webDocuments.document.actions;

import java.util.ArrayList;
import java.util.Hashtable;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.desige.webDocuments.document.forms.BaseDocumentForm;
import com.desige.webDocuments.persistent.managers.HandlerDocumentFormat;
import com.desige.webDocuments.persistent.managers.HandlerDocuments;
import com.desige.webDocuments.seguridad.forms.PermissionUserForm;
import com.desige.webDocuments.utils.ApplicationExceptionChecked;
import com.desige.webDocuments.utils.ToolsHTML;
import com.desige.webDocuments.utils.beans.Search;
import com.desige.webDocuments.utils.beans.SuperAction;
import com.desige.webDocuments.utils.beans.SuperActionForm;
import com.desige.webDocuments.utils.beans.Users;


/**
 * Title: LoadCityAction.java <br/> 
 * Copyright: (c) 2004 Focus Consulting C.A.<br/>
 * @author Ing. Nelson Crespo (NC)
 * @version WebDocuments v3.0
 * <br/>
 * Changes:<br/> 
 * <ul>
 *      <li>25/04/2004 (NC) Creation </li>
 </ul>
 */
public class loadDocumentFormat extends SuperAction {
    public ActionForward execute(ActionMapping actionMapping,
                                 ActionForm form, HttpServletRequest request,
                                 HttpServletResponse response) {
        super.init(actionMapping,form,request,response);
        String descript = request.getParameter("SearchDescrip");
        try {
			Users usuario = getUserSession();
			
            // colocamos en session que estamos ante un documento de rechazo
            removeObjectSession("rejectDocument");
            if (getParameter("idDocument")!=null) {
            	//System.out.println(getParameter("idDocument"));
            	putObjectSession("rejectDocument", getParameter("idDocument"));
            }
            removeObjectSession("rejectFlexFlow");
            if (getParameter("idFlexFlow")!=null) {
            	//System.out.println(getParameter("idFlexFlow"));
            	putObjectSession("rejectFlexFlow", getParameter("idFlexFlow"));
            }
			
            // buscamos los documentos tipo formato
            ArrayList documents = (ArrayList)HandlerDocumentFormat.getAllDocuments(HandlerDocuments.TYPE_DOCUMENT_FORMATO);
            // verificamos cuales estan disponibles para el usuario
            String sep = "";
            StringBuffer ids = new StringBuffer();
            for(int i=0; i < documents.size(); i++) {
            	Search bean = (Search) documents.get(i);
            	ids.append(sep);
            	ids.append(bean.getId());
            	sep = ",";
            }
			Hashtable securityDocs = null;
			if(!ids.toString().trim().equals("")) {
				securityDocs = ToolsHTML.checkDocsSecurity(securityDocs, usuario, ids.toString());
			}
			
			boolean hacer=true;
			boolean noSeguridad = true;
			
			while(hacer) {
				hacer = false;
	            for(int i=0; i < documents.size(); i++) {
	            	Search bean = (Search) documents.get(i);
            		PermissionUserForm forma = (PermissionUserForm)securityDocs.get(bean.getId());
	            	if ( forma==null ) {  // no encuentra lo que mando a buscar
	            		documents.remove(i);
	            		hacer=true;
	            		break;
	            	} else {
	            		if ( forma.getToViewDocs()==0 ) {
		            		documents.remove(i);
		            		hacer=true;
		            		break;
	            		} else {
	            			noSeguridad = false;
	            		}
	            	}
	            }
			}
            
			if(noSeguridad){
				request.setAttribute("info",
						ToolsHTML.getBundle(request).getString("wf.ftp.rejectdocument.security"));
			}
			
            request.getSession().setAttribute("dataTable",documents);
            request.getSession().setAttribute("sizeParam",String.valueOf(documents.size()));
//            String input = request.getParameter("input");
//            String value = request.getParameter("value");
//            String nameForm = request.getParameter("nameForma");

            String input = (String)ToolsHTML.getAttribute(request,"input");
            String value = (String)ToolsHTML.getAttribute(request,"value");
            String nameForm = (String)ToolsHTML.getAttribute(request,"nameForma");

            request.getSession().setAttribute("input",getDataFormResponse(nameForm,input,true));
            request.getSession().setAttribute("value",getDataFormResponse(nameForm,value,false));
            request.getSession().setAttribute("editManager","/editCity.do");
            request.getSession().setAttribute("newManager","/newCity.do");
            request.getSession().setAttribute("loadManager","loadDocumentFormat.do");
            request.getSession().setAttribute("loadEditManager","/loadCityEdit.do");
            request.getSession().setAttribute("formEdit","city");
            String cmd = getCmd(request,false);
            BaseDocumentForm forma = (BaseDocumentForm)form;
            if (forma==null){
                //System.out.println("Forma Nula........");
                form = new BaseDocumentForm();
            }
            if (ToolsHTML.checkValue(cmd)) {
                ((SuperActionForm)form).setCmd(cmd);
                request.setAttribute("cmd",cmd);
            } else{
                request.setAttribute("cmd",SuperActionForm.cmdLoad);
            }
            return goSucces();// (actionMapping.findForward("success"));
        } catch (ApplicationExceptionChecked ae) {
            ae.printStackTrace();
            return goError(ae.getKeyError());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return goError();//(actionMapping.findForward("error"));
    }
}
