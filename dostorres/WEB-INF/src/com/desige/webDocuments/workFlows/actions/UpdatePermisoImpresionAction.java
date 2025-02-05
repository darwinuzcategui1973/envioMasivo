package com.desige.webDocuments.workFlows.actions;

import java.io.File;
import java.util.Hashtable;
import java.util.TreeMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.desige.webDocuments.document.actions.loadsolicitudImpresion;
import com.desige.webDocuments.document.forms.BaseDocumentForm;
import com.desige.webDocuments.document.forms.frmsolicitudImpresion;
import com.desige.webDocuments.persistent.managers.HandlerDBUser;
import com.desige.webDocuments.persistent.managers.HandlerStruct;
import com.desige.webDocuments.utils.ToolsHTML;
import com.desige.webDocuments.utils.beans.SuperActionForm;
import com.desige.webDocuments.utils.beans.Users;

public class UpdatePermisoImpresionAction extends Action {

	public synchronized ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {

		String idDocument = request.getParameter("idDocument");
		String idVersion = request.getParameter("idVersion");
		String idPerson = request.getParameter("idPerson");

		// borramos los archivos de impresion
		// primera pagina
		File fileTmp = null;
		try {
			fileTmp = new File(ToolsHTML.getPathTmp().concat("pag").concat(String.valueOf(idPerson)).concat("_").concat(idDocument).concat("_").concat(idVersion).concat(".pdf"));
			if(fileTmp.exists()) {
				System.gc();
				fileTmp.delete();
			}
		} catch(Exception e) {
			System.out.println(e.getMessage());
			// no hacemos nada
		}

		// primera pagina con seguridad
		try {
			fileTmp = new File(ToolsHTML.getPathTmp().concat("sec").concat(String.valueOf(idPerson)).concat("_").concat(idDocument).concat("_").concat(idVersion).concat(".pdf"));
			if(fileTmp.exists()) {
				System.gc();
				fileTmp.delete();
			}
		} catch(Exception e) {
			System.out.println(e.getMessage());
		}
			
		// documento
		try {
			fileTmp = new File(ToolsHTML.getPathTmp().concat("job").concat(String.valueOf(idPerson)).concat("_").concat(idDocument).concat("_").concat(idVersion).concat(".pdf"));
		} catch(Exception e) {
			System.out.println(e.getMessage());
		}
		
		if(fileTmp!=null && fileTmp.exists()) {
			System.gc();
			fileTmp.delete();
		} else {
			// borramos si son las imagenes las que se generaron
			// documento imagen gif
			try {
				fileTmp = new File(ToolsHTML.getPathTmp().concat("job").concat(String.valueOf(idPerson)).concat("_").concat(idDocument).concat("_").concat(idVersion).concat(".gif"));
				if(fileTmp!=null && fileTmp.exists()) {
					System.gc();
					fileTmp.delete();
				}
			} catch(Exception e) {
				System.out.println(e.getMessage());
			}
			// documento imagen jpg
			try {
				fileTmp = new File(ToolsHTML.getPathTmp().concat("job").concat(String.valueOf(idPerson)).concat("_").concat(idDocument).concat("_").concat(idVersion).concat(".jpg"));
				if(fileTmp!=null && fileTmp.exists()) {
					System.gc();
					fileTmp.delete();
				}
			} catch(Exception e) {
				System.out.println(e.getMessage());
			}
			// documento imagen jpeg
			try {
				fileTmp = new File(ToolsHTML.getPathTmp().concat("job").concat(String.valueOf(idPerson)).concat("_").concat(idDocument).concat("_").concat(idVersion).concat(".jpeg"));
				if(fileTmp!=null && fileTmp.exists()) {
					System.gc();
					fileTmp.delete();
				}
			} catch(Exception e) {
				System.out.println(e.getMessage());
			}
		}
		
		if(request.getParameter("eliminarFile")!=null && request.getParameter("eliminarFile").equals("true")) {
			System.out.println("Si el parametro eliminarFile existe... no seguimos con el proceso ");
			return null;
		}

		frmsolicitudImpresion frm = loadsolicitudImpresion.updatepermisoImpresion(idDocument, String.valueOf(idPerson), idVersion);

		if (frm == null) { // es una impresion no controlada
			try {
				loadsolicitudImpresion solicitud = new loadsolicitudImpresion();
				frmsolicitudImpresion s = null;
				BaseDocumentForm f = null;
				Users u = null;
				TreeMap indice = HandlerDBUser.getAllUsersMap();

				f = new BaseDocumentForm();
				f.setIdDocument(idDocument);
				f.setNumberGen(idDocument);
				f.setNumVer(Integer.parseInt(idVersion));

				// colocaremos el usuario en session si no existe 
				Users usuario = (Users)request.getSession().getAttribute("user");
				if(usuario==null) {
					usuario = HandlerDBUser.load(Long.parseLong(idPerson), true);
					request.getSession().setAttribute("user", usuario);
				}
				
				// Se carga la información del documento en el bean forma,
				// sacandola de la base de datos.
				Hashtable tree = (Hashtable) request.getSession().getAttribute("tree");
				HandlerStruct.loadDocument(f, true, false, tree, request);

				if(f.getLoadDoc()) {
					u = (Users) indice.get(f.getOwner());
	
					request.setAttribute("cmd", SuperActionForm.cmdLoad);
					request.setAttribute("mensaje", "");
					request.setAttribute("controlada", "no");
					request.setAttribute("namePropietario", String.valueOf(u.getIdPerson()));
					request.setAttribute("idDocument", f.getIdDocument());
					request.setAttribute("idVersion", String.valueOf(f.getNumVer()));
					request.setAttribute("solicitante", String.valueOf(idPerson));
	
					request.setAttribute("docRelations", "");
					request.setAttribute("copiasRelations", "");
	
					solicitud.cargarSolicitud(request, request.getSession().getServletContext(), true);
	
					request.setAttribute("cmd", SuperActionForm.cmdUpdatePrint);
					request.setAttribute("idDocumentptr", f.getIdDocument());
					request.setAttribute("idUserptr", String.valueOf(idPerson));
					request.setAttribute("printFree", String.valueOf("false"));
	
					// aprobamos el flujo de trabajo generado automaticamente
					loadsolicitudImpresion.responseWFPrint(solicitud.getIdWorkFlow());
	
					// habilitamos el permiso de impresion
					loadsolicitudImpresion.enabledPermisoImpresion(f.getIdDocument(), String.valueOf(idPerson));
	
					// marcamos como impresa la solicitud
					solicitud.cargarSolicitud(request, request.getSession().getServletContext(), true);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

		}

		return null;
	}
}
