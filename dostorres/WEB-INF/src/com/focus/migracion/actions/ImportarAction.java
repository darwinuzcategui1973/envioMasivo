package com.focus.migracion.actions;

import java.io.File;
import java.util.ArrayList;
import java.util.Properties;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.desige.webDocuments.dao.ConfDocumentoDAO;
import com.desige.webDocuments.document.actions.FileUtil;
import com.desige.webDocuments.persistent.managers.HandlerTypeDoc;
import com.desige.webDocuments.utils.ApplicationExceptionChecked;
import com.desige.webDocuments.utils.StringUtil;
import com.desige.webDocuments.utils.ToolsHTML;
import com.desige.webDocuments.utils.beans.SuperAction;
import com.desige.webDocuments.utils.beans.Users;
import com.focus.qweb.bean.ExcelReader;
import com.focus.qweb.bean.Migracion;
import com.focus.qweb.bean.Reader;
import com.focus.qweb.bean.TextoReader;
import com.focus.qweb.bean.XMLReader;
import com.focus.qweb.facade.MigracionFacade;
import com.focus.qweb.to.EquivalenciasTO;


public class ImportarAction extends SuperAction {
	static Logger log = LoggerFactory.getLogger(ImportarAction.class.getName());

	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
		super.init(mapping, form, request, response);
		
        try {
        	Users user = getUserSession();
            if (!ToolsHTML.checkValue(user.getUser())) {
            	return goError();
            }
            if(request.getParameter("cmd")!=null) {
            	String cmd = request.getParameter("cmd");
            	String nombre = request.getParameter("nombre");
            	
    			// los datos para los nombre de los campos
    			ConfDocumentoDAO oConfDocumentoDAO = new ConfDocumentoDAO();
    			ArrayList lista = (ArrayList) oConfDocumentoDAO.findAll();
    			request.getSession().setAttribute("confDocument", lista);

    			MigracionFacade mf = new MigracionFacade();
            	if(cmd.equals("archivo")) {
            		request.getSession().removeAttribute("equivalencias");
		        	FileUtil fu = new FileUtil();
		        	Properties campos = fu.procesaFicheros(request);
		        	if(campos.size()!=0) {
		        		String nombreArchivo = campos.getProperty("nombreArchivo");
		        		Reader datos = null;
		                if(nombreArchivo!=null) {
		                	nombreArchivo = StringUtil.getOnlyNameFile(campos.getProperty("nombreArchivo"));
		                	nombreArchivo = ToolsHTML.getPathTmp().concat(nombreArchivo);
		                	if(nombreArchivo.toLowerCase().endsWith(".txt")) {
		                		// lee el archivo txt
		                		datos = new TextoReader();
		                		datos.load(nombreArchivo);
		                	} else if(nombreArchivo.toLowerCase().endsWith(".xls")) {
		                		// lee el archivo de excel
		                		datos = new ExcelReader();
		                		datos.load(nombreArchivo);
		                	} else if(nombreArchivo.toLowerCase().endsWith(".xml")) {
		                		// lee el archivo xml
		                		datos = new XMLReader();
		                		datos.load(nombreArchivo);
		                	} else {
		                		throw new Exception("Formato no soportado de archivo");
		                	}
		                	request.getSession().setAttribute("nombreArchivo", nombreArchivo);
		                	request.getSession().setAttribute("equivalencias",mf.listarEquivalencias());
		                	request.getSession().setAttribute("equivalenciasNombres",mf.listarEquivalenciasNombres());
		                	request.getSession().setAttribute("datos", datos);
		                   	return goTo("success1");
		                }
		        	}
            	} else if(cmd.equals("retornarEquivalencias")) {
            		//request.setAttribute("equivalenciaActiva",request.getSession().getAttribute("equivalenciaActiva"));
                	request.getSession().setAttribute("equivalencias",mf.listarEquivalencias());
                	request.getSession().setAttribute("equivalenciasNombres",mf.listarEquivalenciasNombres());
                   	return goTo("success1");
            	} else if(cmd.equals("listarEquivalencias")) {
        			request.getSession().setAttribute("equivalencias",mf.listarEquivalencias(nombre));
                	request.getSession().setAttribute("equivalenciasNombres",mf.listarEquivalenciasNombres());
                	request.setAttribute("equivalenciaActiva",nombre);
                	return goTo("success1");
            	} else if(cmd.equals("eliminarEquivalencias")) {
            		if(nombre.equals(EquivalenciasTO.EQUIVALENCIA_QWEB)) {
                    	request.setAttribute("equivalenciaActiva",nombre);
                    	request.setAttribute("info","No se puede eliminar esta equivalencia");
                    	return goTo("success1");
            		} else {
            			mf.eliminarEquivalencias(nombre);
                    	request.getSession().setAttribute("equivalencias",mf.listarEquivalencias(EquivalenciasTO.EQUIVALENCIA_QWEB));
                    	request.getSession().setAttribute("equivalenciasNombres",mf.listarEquivalenciasNombres());
                    	request.setAttribute("equivalenciaActiva",EquivalenciasTO.EQUIVALENCIA_QWEB);
            		}
                	return goTo("success1");
            	} else if(cmd.equals("equivalencias")) {
        			EquivalenciasTO oEquiTO = new EquivalenciasTO();
            		oEquiTO.setNombre(nombre);
            		oEquiTO.setIndiceArray(request.getParameterValues("indice"));
            		oEquiTO.setCampoArray(request.getParameterValues("campo"));
            		oEquiTO.setPosicionArray(request.getParameterValues("posicion"));
            		oEquiTO.setColumnaArray(request.getParameterValues("columna"));
            		oEquiTO.setValorArray(request.getParameterValues("valor"));
            		oEquiTO.setIndexarArray(request.getParameterValues("indexar"));
            		oEquiTO.setActivo("1");
            		mf.insertarListaEquivalencias(oEquiTO);
            		
            		request.getSession().setAttribute("equivalenciaActiva", nombre);
                   	return goTo("success2");
            	} else if(cmd.equals("validacion")) {
            		request.getSession().setAttribute("porcentajeBarra","0");
            		Migracion mi = new Migracion();
            		String nombreArchivo = (String)request.getSession().getAttribute("nombreArchivo");
            		ArrayList equivalencias  = (ArrayList)mf.listarEquivalencias();
            		Reader datos = (Reader)request.getSession().getAttribute("datos");
    	            String path = ToolsHTML.getPath();
    				mi.inicio(request, nombreArchivo, datos, equivalencias, path, true);
            		response.getWriter().print(mi.getNameFileLog());
                   	return null;
            	} else if(cmd.equals("migracion")) {
            		request.getSession().setAttribute("porcentajeBarra","0");
            		Migracion mi = new Migracion();
            		String nombreArchivo = (String)request.getSession().getAttribute("nombreArchivo");
            		ArrayList equivalencias  = (ArrayList)mf.listarEquivalencias();
            		Reader datos = (Reader)request.getSession().getAttribute("datos");
    	            String path = ToolsHTML.getPath();
    				mi.inicio(request, nombreArchivo, datos, equivalencias, path, false);
            		response.getWriter().print(mi.getNameFileLog());
                   	return null;
            	} else if(cmd.equals("porcentaje")) {
            		response.getWriter().print(request.getSession().getAttribute("porcentajeBarra"));
            		return null;
            	}
            } else {
            	// recolectamos los tipos de documentos
                request.setAttribute("listaTypeDoc",(ArrayList)HandlerTypeDoc.getAllTypeDocs(null,false));
                
                // recolectamos los campos adicionales
                ConfDocumentoDAO cd = new ConfDocumentoDAO();
                request.setAttribute("listaFields",cd.findAllByField());
            	
            	
            }
            	
        	return goSucces();
        } catch (ApplicationExceptionChecked ae) {
            return goError(ae.getKeyError());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return goError();
	}
}
