package com.desige.webDocuments.perfil.actions;

import java.util.Collection;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.desige.webDocuments.perfil.forms.PerfilActionForm;
import com.desige.webDocuments.persistent.managers.HandlerCity;
import com.desige.webDocuments.persistent.managers.HandlerIdiomas;
import com.desige.webDocuments.persistent.managers.HandlerPais;
import com.desige.webDocuments.persistent.managers.HandlerParameters;
import com.desige.webDocuments.persistent.managers.HandlerPerfil;
import com.desige.webDocuments.persistent.managers.HandlerProcesosSacop;
import com.desige.webDocuments.persistent.managers.HandlerState;
import com.desige.webDocuments.seguridad.forms.SeguridadUserForm;
import com.desige.webDocuments.utils.ApplicationExceptionChecked;
import com.desige.webDocuments.utils.Constants;
import com.desige.webDocuments.utils.ToolsHTML;
import com.desige.webDocuments.utils.beans.SuperAction;
import com.desige.webDocuments.utils.beans.Users;

/**
 * Title: EditPerfilAction.java <br/>
 * Copyright: (c) 2004 Focus Consulting C.A.<br/>
 * @author Ing. Nelson Crespo (NC)
 * @version WebDocuments v3.0
 * <br/>
 * Changes:<br/> 
 * <ul>
 *      <li>28/03/2004 (NC) Creation </li>
 </ul>
 */
public class LoadPerfilAction extends SuperAction {
	private static Logger log = LoggerFactory.getLogger(LoadPerfilAction.class.getName());
	
	/**
	 * 
	 */
    public ActionForward execute(ActionMapping mapping,
                                 ActionForm form, HttpServletRequest request,
                                 HttpServletResponse httpServletResponse) {
        super.init(mapping,form,request,httpServletResponse);
        try {
            //Users user = getUserSession();
        	Users user = (Users) request.getSession().getAttribute("user");
        	
            // seteamos el modulo activo
           	request.getSession().setAttribute(Constants.MODULO_ACTIVO, Constants.MODULO_PERFIL);
            
			Users usuario = user;
			SeguridadUserForm securityForUser = ToolsHTML.getSeguridadModuloUser(usuario);
			SeguridadUserForm securityForGroup = ToolsHTML.getSeguridadModuloGroup(usuario);

			log.info("Leida seguridad de usuario y su grupo");
			if (securityForUser.getPerfil() == 0 || user.getIdGroup().equals(Constants.ID_GROUP_VIEWER)) {
				log.info("El usuario tiene permiso a perfil o es un viewer (puede ver su perfil)");
				putAtributte("visible", "true");
			} else if (securityForUser.getPerfil() == 2) {
				log.info("El usuario hereda de su grupo el acceso a perfil");
				if (securityForGroup.getPerfil() == 0) {
					log.info("El grupo del usuario tiene acceso a perfil");
					putAtributte("visible", "true");
				} else {
					log.error("ACCESO ILEGAL:loadPerfil.do user:"+usuario.getIdPerson()+" / nombre:"+usuario.getNamePerson());
					return mapping.findForward("errorNotAuthorized");
				}
			} else {
				log.error("ACCESO ILEGAL:loadPerfil.do user:"+usuario.getIdPerson()+" / nombre:"+usuario.getNamePerson());
				return mapping.findForward("errorNotAuthorized");
			}
            
            if (ToolsHTML.checkValue(user.getUser())) {
                if (form == null){
                    form = new PerfilActionForm();
                }
                ((PerfilActionForm)form).setUser(user.getUser());
//                ((PerfilActionForm)form).setId(String.valueOf(user.getIdPerson()));
                ((PerfilActionForm)form).setId(new Long(user.getIdPerson()));
                //System.out.println(getParameter("backURL"));
                //System.out.println(getSessionObject("perfil"));
                if (!"true".equalsIgnoreCase(getParameter("backURL"))&& getSessionObject("perfil")!=null) {
                    PerfilActionForm forma = (PerfilActionForm)form;
//                    forma = (PerfilActionForm)HibernateUtil.loadObject(PerfilActionForm.class,new Long(forma.getId()));
                    HandlerPerfil.load(forma);
                    //02 agosto 2005 inicio
                     String lengthPass = HandlerParameters.PARAMETROS.getLengthPass();
                     forma.setLengthPass(lengthPass);
                     //02 agosto 2005 fin
                    String codPais = forma.getPais();
                    if (ToolsHTML.checkValue(codPais)) {
                        forma.setNamePais(HandlerPais.getFieldPais("Nombre",codPais));
                    }
                    if (ToolsHTML.checkValue(forma.getCiudad())) {
                        forma.setNameCity(HandlerCity.getFieldCity("Nombre",forma.getCiudad()));
                    }
                    if (ToolsHTML.checkValue(forma.getEstado())) {
                        forma.setNameState(HandlerState.getFieldState("Nombre",forma.getEstado()));
                    }
                    putObjectSession("perfil",forma);
                }
                Collection idiomas = HandlerIdiomas.getAllLanguage();
                if (idiomas!=null) {
                    request.setAttribute("idiomas",idiomas);
                }

                Collection tblareas = HandlerProcesosSacop.getAreas();
                if (tblareas!=null) {
                    request.setAttribute("tblareas",tblareas);
                }

                return goSucces();
            } else {
            	log.info("La instruccion ToolsHTML.checkValue('" + user.getUser() + "') dio false");
            }
        } catch (ApplicationExceptionChecked ae) {
        	log.error("Error: " + ae.getLocalizedMessage(), ae);
            return goError(ae.getKeyError());
        } catch (Exception ex) {
        	log.error("Error: " + ex.getLocalizedMessage(), ex);
        }
        
        return goError();
//        Users user = (Users)request.getSession().getAttribute("user");
//        //System.out.println("user = " + user);
//        if (user!=null){
//            if (ToolsHTML.checkValue(user.getUser())){
//                ((PerfilActionForm)form).setUser(user.getUser());
//                try {
//                    PerfilActionForm forma = (PerfilActionForm)form;
//                    HandlerPerfil.load(forma);
//                    String codPais = forma.getPais();
//                    if (ToolsHTML.checkValue(codPais)){
//                        forma.setNamePais(HandlerPais.getFieldPais("Nombre",codPais));
//                    }
//                    if (ToolsHTML.checkValue(forma.getCiudad())){
//                        forma.setNameCity(HandlerCity.getFieldCity("Nombre",forma.getCiudad()));
//                    }
//                    if (ToolsHTML.checkValue(forma.getEstado())){
//                        forma.setNameState(HandlerState.getFieldState("Nombre",forma.getEstado()));
//                    }
//                    Collection idiomas = HandlerIdiomas.getAllLanguage();
//                    if (idiomas!=null){
//                        request.setAttribute("idiomas",idiomas);
//                    }
//                    return actionMapping.findForward("success");
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }else{
//
//            }
//        }
//        return actionMapping.findForward("error");
    }
}
