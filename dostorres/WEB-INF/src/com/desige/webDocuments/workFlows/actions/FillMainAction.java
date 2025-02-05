package com.desige.webDocuments.workFlows.actions;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Hashtable;
import java.util.Iterator;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.desige.webDocuments.document.forms.DocumentsCheckOutsBean;
import com.desige.webDocuments.persistent.managers.HandlerDocuments;
import com.desige.webDocuments.utils.beans.Users;
import com.desige.webDocuments.workFlows.forms.DataUserWorkFlowForm;

public class FillMainAction extends Action {

	public synchronized ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {

		StringBuffer query = new StringBuffer();
		ArrayList<Object> parametros = new ArrayList<Object>();
		String cuadrante = "";
        Hashtable nodos = new Hashtable();

		try {
			Users usuario = (Users) request.getSession().getAttribute("user");

			cuadrante = String.valueOf(request.getParameter("cuadrante"));
			
			Collection lista = new ArrayList(); 
            
			//Collection docCheckOuts = HandlerDocuments.getAllDocumentsCheckOutsUser(usuario.getUser(),"0",null,nodos);
			
//            putObjectSession("wfPendings",wfPendings);
//            putObjectSession("wfExpires",wfExpires);
//            putObjectSession("docCheckOuts",docCheckOuts);
//            putObjectSession("docExpires",docExpires);
			
			lista = (Collection) request.getSession().getAttribute(cuadrante); 

			if(lista==null) {
				return null;
			}
            Iterator ite = lista.iterator();
            DocumentsCheckOutsBean docCheck = null;
            DataUserWorkFlowForm dataUser = null;
            String sep = "";
            StringBuffer cad = new StringBuffer();
            cad.append("[");
            //var arr = [{id:100,nombre:'jairo'},{id:101,nombre:'rivero'}];
            if(cuadrante.equals("docCheckOuts") || cuadrante.equals("wfPrintApproved")) {
	            while(ite.hasNext()) {
	            	docCheck = (DocumentsCheckOutsBean)ite.next();
	            	cad.append(sep);
	            	cad.append("{img:");
	            	cad.append("'img/chequeado.gif'");
	            	cad.append(",action:'");
	            	cad.append("showDocument(\"").append(docCheck.getIdDocument()).append("\")");
	            	cad.append("',col1:'");
	            	cad.append(docCheck.getMayorVer()).append(".").append(docCheck.getMinorVer());
	            	cad.append("',col2:'");
	            	cad.append(docCheck.getNameDocument()).append(" ").append(docCheck.getPrefix()).append(docCheck.getNumber());
	            	cad.append("',col3:'");
	            	cad.append(docCheck.getDateCheckOut());
	            	cad.append("'}");
	            	sep = ","; 
	            } 
            } else if(cuadrante.equals("wfPendings")) {
	            while(ite.hasNext()) {
	            	dataUser = (DataUserWorkFlowForm)ite.next();
	            	//HandlerDocuments.TypeDocumentsImpresion
	            	cad.append(sep);
	            	cad.append("{img:");
	            	cad.append(String.valueOf(dataUser.getTypeDOC()).equals(HandlerDocuments.TypeDocumentsImpresion)?"'icons/printer.png'":"'img/pendings.gif'");
	            	cad.append(",action:'");
	            	cad.append("showWF(\"").append(dataUser.getIdWorkFlow()).append("\",\"").append(dataUser.getRow()).append("\",").append(dataUser.isOwner()).append(",").append(dataUser.isFlexFlow()).append(")");
	            	cad.append("',col1:'");
	            	cad.append(dataUser.getIdVersion());
	            	cad.append("',col2:'");
	            	if(String.valueOf(dataUser.getTypeDOC()).equals(HandlerDocuments.TypeDocumentsImpresion)) {
	            		cad.append(dataUser.getNameDocument());
	            	} else {
	                	cad.append(dataUser.getNameDocument()).append(" ").append(dataUser.getPrefix()).append(dataUser.getNumber());
	            	}
	            	cad.append("',col3:'");
	            	cad.append(dataUser.getNameWorkFlow());
	            	cad.append("'}");
	            	sep = ","; 
	            } 
            }

            cad.append("]");
        	response.getWriter().print(cad);
		} catch (Exception e) {
			//System.out.println(e.getMessage());
			e.printStackTrace();
		} finally {
		}

		//System.out.println("Ejecutando codigo ajax");
		return null;
	}
}

// documentos pendientes

//            <logic:iterate id="docCheck" name="docCheckOuts" type="com.desige.webDocuments.document.forms.DocumentsCheckOutsBean" scope="session" >
//        	<% cont++; 
//        		if(cont==3) break;%>
//            <tr >
//                <td>
//        	<%=cont %>
//                    <img src="img/chequeado.gif">
//                </td>
//                <td class="txt_b">
//                    <%=docCheck.getMayorVer()+"."+docCheck.getMinorVer()%>
//                </td>
//                <td class="txt_b">
//                    <a href="javascript:showDocument('<%=docCheck.getIdDocument()%>')" class="ahref_b">
//                        <%=docCheck.getNameDocument()+" " + docCheck.getPrefix()+docCheck.getNumber()%>
//                    </a>
//                </td>
//                <td class="txt_b">
//                    <%=docCheck.getDateCheckOut()%>
//                </td>
//            </tr>
//        </logic:iterate>
            



// flujos pendientes 

//<td class="txt_b" >
//<logic:equal name="wfReq" property="typeDOC" value="<%=tipoImpresion%>">
//     <img src="img/print.gif" border="0">
//</logic:equal>
//<logic:notEqual name="wfReq" property="typeDOC" value="<%=tipoImpresion%>">
//      <img src="img/pendings.gif">
//</logic:notEqual>
//  <%=wfReq.getIdVersion()%>
//</td>
//<td>
//<a href="javascript:showWF('<%=wfReq.getIdWorkFlow()%>','<%=wfReq.getRow()%>',<%=wfReq.isOwner()%>,<%=wfReq.isFlexFlow()%>)" class="ahref_b">
//   <logic:notEqual name="wfReq" property="typeDOC" value="<%=tipoImpresion%>">
//      <%=wfReq.getNameDocument()+" " +wfReq.getPrefix()+wfReq.getNumber()%>
//   </logic:notEqual>
//   <logic:equal name="wfReq" property="typeDOC" value="<%=tipoImpresion%>">
//     <%=wfReq.getNameDocument()%>
//  </logic:equal>
//</a>
//</td>
//<td class="txt_b">
//<%=wfReq.getNameWorkFlow()%>
//
//}
//cad.append("]");
