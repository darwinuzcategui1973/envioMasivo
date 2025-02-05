package com.desige.webDocuments.enlaces.actions;

import java.util.Collection;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.desige.webDocuments.enlaces.forms.UrlsActionForm;
import com.desige.webDocuments.perfil.forms.PerfilActionForm;
import com.desige.webDocuments.persistent.managers.HandlerUrls;
import com.desige.webDocuments.utils.ApplicationExceptionChecked;
import com.desige.webDocuments.utils.ToolsHTML;
import com.desige.webDocuments.utils.beans.SuperAction;
import com.desige.webDocuments.utils.beans.SuperActionForm;
import com.desige.webDocuments.utils.beans.Users;

/**
 * Title: LoadUrlsAction.java <br/> 
 * Copyright: (c) 2004 Focus Consulting C.A.<br/>
 * @author Ing. Nelson Crespo (NC)
 * @version WebDocuments v3.0
 * <br/>
 * Changes:<br/> 
 * <ul>
 *      <li>08/04/2004 (NC) Creation </li>
 *      <li>15/09/2006 (SR) se comento la linea 75   ((UrlsActionForm)form).setCmd(cmd); </li>
 </ul>
 */
public class LoadUrlsAction extends SuperAction {
    public ActionForward execute(ActionMapping actionMapping,
                                 ActionForm form, HttpServletRequest request,
                                 HttpServletResponse response) {
        super.init(actionMapping,form,request,response);
        try {
//            ActionForm formaPerfil = form;
            //Si encuentra la Forma la Carga en la session para mantener la misma...
            if (form!=null) {
                if (form instanceof PerfilActionForm && getParameter("apellidos")!=null) {
                    PerfilActionForm profile = (PerfilActionForm)form;
                    profile.setApellidos(getParameter("apellidos"));
                    profile.setNombres(getParameter("nombres"));
                    profile.setCargo(getParameter("cargo"));
                    profile.setEmail(getParameter("email"));
                    profile.setDireccion(getParameter("direccion"));
                    profile.setCiudad(getParameter("ciudad"));
                    profile.setNameCity(getParameter("nameCity"));
                    profile.setEstado(getParameter("estado"));
                    profile.setNameState(getParameter("nameState"));
                    profile.setPais(getParameter("pais"));
                    profile.setNamePais(getParameter("namePais"));
                    profile.setZip(getParameter("zip"));
                    profile.setTelefono(getParameter("telefono"));
                    putObjectSession("perfil",profile);
                }
            }
            Users user = getUserSession();
            Collection urlUser = HandlerUrls.getUrlsUser("('"+user.getUser()+"')");
            putObjectSession("userURLs",urlUser);
            putObjectSession("size",String.valueOf(urlUser.size()));
//            request.getSession().setAttribute("userURLs",urlUser);
//            request.getSession().setAttribute("size",String.valueOf(urlUser.size()));
            String cmd = getCmd(request,false);
//            UrlsActionForm forma = (UrlsActionForm)form;
            UrlsActionForm forma = new UrlsActionForm();
//            if (forma==null){
//                form = new UrlsActionForm();
//            }
            putObjectSession("newUrls",forma);
            if (ToolsHTML.checkValue(cmd)){
                forma.setCmd(cmd);
//                ((UrlsActionForm)form).setCmd(cmd);
                request.setAttribute("cmd",cmd);
            } else{
                request.setAttribute("cmd",SuperActionForm.cmdLoad);
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
