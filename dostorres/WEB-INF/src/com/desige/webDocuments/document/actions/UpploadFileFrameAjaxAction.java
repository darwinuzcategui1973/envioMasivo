package com.desige.webDocuments.document.actions;

import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.desige.webDocuments.files.facade.DigitalFacade;
import com.desige.webDocuments.persistent.managers.HandlerDBUser;
import com.desige.webDocuments.persistent.managers.HandlerTypeDoc;
import com.desige.webDocuments.to.DigitalTO;
import com.desige.webDocuments.typeDocuments.forms.TypeDocumentsForm;
import com.desige.webDocuments.utils.ApplicationExceptionChecked;
import com.desige.webDocuments.utils.Constants;
import com.desige.webDocuments.utils.ToolsHTML;
import com.desige.webDocuments.utils.beans.Users;

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
public class UpploadFileFrameAjaxAction extends Action {

	public ActionForward execute(ActionMapping actionMapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
		try {
	        Users usuario = (Users)request.getSession().getAttribute("user");
	        
	        if (usuario==null || !HandlerDBUser.isValidSessionUser(usuario.getUser(),request.getSession())) {
	            throw new ApplicationExceptionChecked("E0035");
	        }

			
			PrintWriter out = response.getWriter();

			// buscamos el anterior y el proximo del id dado
			DigitalFacade digital = new DigitalFacade(request);
			DigitalTO digitalTO = new DigitalTO();

			boolean eliminar = false;
			boolean rechazar = false;
			String comentario = "";
			String status = "0";

			try {
				Integer.parseInt(request.getParameter("idDigital"));
			} catch (NumberFormatException np) {
				out.write("");
				return null;
			}

			try {
				eliminar = request.getParameter("eliminar").equals("true");
			} catch (Exception np) {
			}
			try {
				rechazar = request.getParameter("rechazar").equals("true");
			} catch (Exception np) {
			}
			try {
				comentario = request.getParameter("comentario");
			} catch (Exception np) {
			}
			try {
				status = request.getParameter("status");
			} catch (Exception np) {
			}
			
			digitalTO.setIdDigital(request.getParameter("idDigital"));

			String previous = digital.getPrevious(digitalTO);
			String next = digital.getNext(digitalTO);

			if (eliminar) {
				digitalTO.setIdPersonDelete(String.valueOf(usuario.getIdPerson()));
				digital.delete(digitalTO);

				if (next != null && !next.equals("")) {
					digitalTO.setIdDigital(next);
					previous = digital.getPrevious(digitalTO);
					next = digital.getNext(digitalTO);
				} else if (previous != null && !previous.equals("")) {
					digitalTO.setIdDigital(previous);
					previous = digital.getPrevious(digitalTO);
					next = digital.getNext(digitalTO);
				} else {
					digitalTO.setIdDigital("0");
					previous = "";
					next = "";
				}
			}
			
			if (rechazar) {
				digitalTO.setComentario(comentario!=null?comentario:"");
				digitalTO.setIdStatusDigital(status!=null && status.equals("0")?Constants.STATUS_DIGITAL_RECHAZADO_TO_SCAN:Constants.STATUS_DIGITAL_RECHAZADO_TO_TYPESETTER);
				digital.reject(digitalTO);

				if (next != null && !next.equals("")) {
					digitalTO.setIdDigital(next);
					previous = digital.getPrevious(digitalTO);
					next = digital.getNext(digitalTO);
				} else if (previous != null && !previous.equals("")) {
					digitalTO.setIdDigital(previous);
					previous = digital.getPrevious(digitalTO);
					next = digital.getNext(digitalTO);
				} else {
					digitalTO.setIdDigital("0");
					previous = "";
					next = "";
				}
			}
			

			digitalTO = digital.findById(digitalTO);
			
			request.getSession().setAttribute("idDigital", digitalTO.getIdDigital());
			request.getSession().setAttribute("anterior", previous);
			request.getSession().setAttribute("siguiente", next);
			request.getSession().setAttribute("idDigitalName", digitalTO.getNameFile());

			if(request.getSession().getAttribute("idDigital")!=null) {
				StringBuffer sb = new StringBuffer();
				sb.append("({");
				sb.append("anterior:'").append((String) request.getSession().getAttribute("anterior")).append("'");
				sb.append(",siguiente:'").append((String) request.getSession().getAttribute("siguiente")).append("'");
				sb.append(",idDigitalName:'").append((String) request.getSession().getAttribute("idDigitalName")).append("'");
				
				sb.append(",idDigital:'").append(digitalTO.getIdDigital()).append("'");
				sb.append(",nameFile:'").append(digitalTO.getNameFile()).append("'");
				sb.append(",nameDocument:'").append(digitalTO.getNameDocument()).append("'");
				sb.append(",type:'").append(digitalTO.getType()).append("'");
				sb.append(",dateCreation:'").append(digitalTO.getDateCreation()).append("'");
				sb.append(",numberTest:'").append(digitalTO.getNumberTest()).append("'");
				sb.append(",idPerson:'").append(digitalTO.getIdPerson()).append("'");
			    sb.append(",idPersonDelete:'").append(digitalTO.getIdPersonDelete()).append("'");
			    sb.append(",dateDelete:'").append(digitalTO.getDateDelete()).append("'");
				sb.append(",idStatusDigital:'").append(digitalTO.getIdStatusDigital()).append("'");
				sb.append(",comentario:'").append(digitalTO.getComentario()).append("'");
				sb.append(",lote:'").append(digitalTO.getLote()).append("'");
				sb.append(",ownerTypeDoc:'").append(digitalTO.getOwnerTypeDoc()).append("'");
				sb.append(",idNode:'").append(digitalTO.getIdNode()).append("'");
				sb.append(",typesetter:'").append(digitalTO.getTypesetter()).append("'");
				sb.append(",checker:'").append(digitalTO.getChecker()).append("'");
				sb.append(",visible:'").append(digitalTO.getVisible()).append("'");
				sb.append(",versionMayor:'").append(digitalTO.getVersionMayor()).append("'");
				sb.append(",versionMenor:'").append(digitalTO.getVersionMenor()).append("'");
				sb.append(",codigo:'").append(digitalTO.getCodigo()).append("'");
				sb.append(",publicado:'").append(digitalTO.getPublicado()).append("'");
				sb.append(",expira:'").append(digitalTO.getExpira()).append("'");
				sb.append(",fechaPublicacion:'").append(digitalTO.getFechaPublicacion()).append("'");
				sb.append(",fechaVencimiento:'").append(digitalTO.getFechaVencimiento()).append("'");
				sb.append(",comentarios:'").append(digitalTO.getComentarios()).append("'");
				sb.append(",url:'").append(digitalTO.getUrl().replaceAll("[\n\r]", ", ")).append("'");
				sb.append(",palabrasClaves:'").append(digitalTO.getPalabrasClaves().replaceAll("[\n\r]", ", ")).append("'");
				sb.append(",descripcion:'").append(digitalTO.getDescripcion().replaceAll("[\n\r]", ", ")).append("'");
				sb.append(",otrosDatos:'").append(digitalTO.getOtrosDatos()).append("'");
				
				// tipo de documento
				TypeDocumentsForm forma = new TypeDocumentsForm();
				forma.setId(digitalTO.getType());
				HandlerTypeDoc.load(forma);
				sb.append(",publicDoc:'").append(forma.getPublicDoc()).append("'");
				
				sb.append(",nameRout:'").append(ToolsHTML.getNodeName(String.valueOf(digitalTO.getIdNode()))).append("'");
				sb.append(",owner:'").append(ToolsHTML.getUserName(String.valueOf(digitalTO.getOwnerTypeDoc()))).append("'");
				sb.append(",fechaPublicacionToShow:'").append(digitalTO.getFechaPublicacion()).append("'");
				sb.append(",fechaVencimientoToShow:'").append(digitalTO.getFechaPublicacionToShow()).append("'");
				
				
				sb.append("})");

				out.write(sb.toString());
				/*
				out.write((String) request.getSession().getAttribute("idDigital"));
				out.write(",");
				out.write((String) request.getSession().getAttribute("anterior"));
				out.write(",");
				out.write((String) request.getSession().getAttribute("siguiente"));
				out.write(",");
				out.write((String) request.getSession().getAttribute("idDigitalName"));
				*/
			} else {
				out.write("null");
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

}
