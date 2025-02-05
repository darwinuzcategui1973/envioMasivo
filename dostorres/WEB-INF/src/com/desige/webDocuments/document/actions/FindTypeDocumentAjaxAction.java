package com.desige.webDocuments.document.actions;

import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.desige.webDocuments.files.facade.DigitalFacade;
import com.desige.webDocuments.to.DigitalTO;
import com.desige.webDocuments.typeDocuments.forms.TypeDocumentsForm;
import com.desige.webDocuments.utils.ToolsHTML;

/**
 * Title: FuncionesEndosos.java<br>
 * Copyright: (c) 2003 Consis International<br>
 * Company: Consis International (CON)<br>
 * 
 * @author Nelson Crespo (NC)
 * @version Acsel-e v2.2 <br>
 *          Changes:<br>
 *          <ul>
 *          <li> 05/07/2004 (NC) Creation </li>
 *          <ul>
 */
public class FindTypeDocumentAjaxAction extends Action {

	public ActionForward execute(ActionMapping actionMapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
		try {
			PrintWriter out = response.getWriter();
			
			try{
				Integer.parseInt(request.getParameter("idTypeDoc"));
			} catch(NumberFormatException np){
				out.write("");
				return null;
			}
			
			TypeDocumentsForm td = ToolsHTML.getTypeDocuments(request.getParameter("idTypeDoc"));
			
			
			/*
			private String id;
			private String name;
			private String type;

			private String nameFile;
			
		    private byte expireDoc;
		    private byte expireDrafts;
		    private String monthsExpireDrafts;
		    private String unitTimeExpireDrafts;
		    private String monthsExpireDocs;
		    private String unitTimeExpire;
		    private byte firstPage;
		    private byte sendToFlexWF;
		    private int actNumber;
		    private int typesetter;
		    private int ownerTypeDoc;
		    private int idNode;
		    */			
			
			StringBuffer sb = new StringBuffer();
			sb.append("({");
			sb.append("id:'").append(td.getId()).append("'");
			sb.append(",name:'").append(td.getName()).append("'");
			sb.append(",type:'").append(td.getType()).append("'");
			sb.append(",typesetter:'").append(td.getTypesetter()).append("'");
			sb.append(",ownerTypeDoc:'").append(td.getOwnerTypeDoc()).append("'");
			sb.append(",ownerTypeDocUserName:'").append(ToolsHTML.getUserName(String.valueOf(td.getOwnerTypeDoc()))).append("'");
			sb.append(",idNodeTypeDoc:'").append(td.getIdNodeTypeDoc()).append("'");
			sb.append(",idNodeName:'").append(ToolsHTML.getNodeName(String.valueOf(td.getIdNodeTypeDoc()))).append("'");
			sb.append(",checker:'").append(td.getChecker()).append("'");
			sb.append(",checkerUserName:'").append(ToolsHTML.getUserName(String.valueOf(td.getChecker()))).append("'");
			sb.append(",publicDoc:'").append(td.getPublicDoc()).append("'");
			sb.append("})");
			
			out.write(sb.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public void String () {
	
	}

}
