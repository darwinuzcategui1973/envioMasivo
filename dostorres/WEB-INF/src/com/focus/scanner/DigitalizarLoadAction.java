package com.focus.scanner;


import java.io.PrintWriter;
import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.desige.webDocuments.persistent.managers.HandlerDBUser;
import com.desige.webDocuments.utils.ApplicationExceptionChecked;
import com.desige.webDocuments.utils.ToolsHTML;
import com.desige.webDocuments.utils.beans.SuperAction;
import com.desige.webDocuments.utils.beans.Users;

/**
 * Title: DigitalizarLoadAction.java <br/> Copyright: (c) 2004 Focus Consulting
 * C.A.<br/>
 * 
 */
public class DigitalizarLoadAction extends SuperAction {
	private static Logger log = LoggerFactory.getLogger(DigitalizarLoadAction.class.getName());

	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {

		super.init(mapping, form, request, response);

		try {
			Users usuario = getUserSession();
			
			if(request.getParameter("aplicar")!=null && request.getParameter("aplicar").equals("true")) {
				
				int modo = (request.getParameter("modo")!=null?ToolsHTML.parseInt(request.getParameter("modo")):usuario.getModo());
				int lado = (request.getParameter("lado")!=null?ToolsHTML.parseInt(request.getParameter("lado")):usuario.getLado());
				int ppp = (request.getParameter("ppp")!=null?ToolsHTML.parseInt(request.getParameter("ppp")):usuario.getPpp());
				int panel = (request.getParameter("panel")!=null?ToolsHTML.parseInt(request.getParameter("panel")):usuario.getPanel());
				int pagina = (request.getParameter("pagina")!=null?ToolsHTML.parseInt(request.getParameter("pagina")):usuario.getPagina());
				int separar = (request.getParameter("separar")!=null?ToolsHTML.parseInt(request.getParameter("separar")):usuario.getSeparar());
				int minimo = (request.getParameter("minimo")!=null?ToolsHTML.parseInt(request.getParameter("minimo")):usuario.getMinimo());
				
				usuario.setModo(modo);
				usuario.setLado(lado);
				usuario.setPpp(ppp);
				usuario.setPanel(panel);
				usuario.setPagina(pagina);
				usuario.setSeparar(separar);
				usuario.setMinimo(minimo);
				
				HandlerDBUser.updateUserScanner(usuario);
			} else if(request.getParameter("aplicarParameter")!=null && request.getParameter("aplicarParameter").equals("true")) {

				try {
				int typeDocuments = (request.getParameter("typedocuments")!=null?ToolsHTML.parseInt(request.getParameter("typedocuments")):usuario.getTypeDocuments());
				int ownerTypeDoc = (request.getParameter("ownerTypeDoc")!=null?ToolsHTML.parseInt(request.getParameter("ownerTypeDoc")):usuario.getOwnerTypeDoc());
				int idNode = (request.getParameter("idNodeTypeDoc")!=null?ToolsHTML.parseInt(request.getParameter("idNodeTypeDoc")):usuario.getIdNodeDigital());
				int typesetter = (request.getParameter("typesetter")!=null?ToolsHTML.parseInt(request.getParameter("typesetter")):usuario.getTypesetter());
				int checker = (request.getParameter("checker")!=null?ToolsHTML.parseInt(request.getParameter("checker")):usuario.getChecker());
				String lote = (request.getParameter("lote")!=null?request.getParameter("lote"):usuario.getLote());
				String prefijo = (request.getParameter("prefijo")!=null?request.getParameter("prefijo"):usuario.getLote());
				String correlativo = (request.getParameter("correlativo")!=null?request.getParameter("correlativo"):usuario.getLote());
				
				usuario.setTypeDocuments(typeDocuments);
				usuario.setOwnerTypeDoc(ownerTypeDoc);
				usuario.setIdNodeDigital(idNode);
				usuario.setTypesetter(typesetter);
				usuario.setChecker(checker);
				usuario.setLote(lote);
				usuario.setCorrelativo(correlativo);
				
				usuario.setConsecutivo(new ArrayList());
				
				HandlerDBUser.updateUserDigitalParameter(usuario);
				
				PrintWriter out = response.getWriter();
				out.write("true");
				} catch(Exception e) {
					PrintWriter out = response.getWriter();
					out.write(e.getMessage());
					e.printStackTrace();
				}
				return null;
			}

			if(request.getParameter("lote")!=null && request.getParameter("lote").equals("true")) {
				return goTo("successLote");
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
}
