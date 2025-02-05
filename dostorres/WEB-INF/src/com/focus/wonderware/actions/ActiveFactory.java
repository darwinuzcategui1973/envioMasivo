package com.focus.wonderware.actions;

import java.util.Calendar;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.desige.webDocuments.document.forms.BaseDocumentForm;
import com.desige.webDocuments.persistent.managers.HandlerDBUser;
import com.desige.webDocuments.persistent.managers.HandlerDocuments;
import com.desige.webDocuments.persistent.managers.HandlerParameters;
import com.desige.webDocuments.persistent.managers.HandlerStruct;
import com.desige.webDocuments.users.forms.LoginUser;
import com.desige.webDocuments.utils.ApplicationExceptionChecked;
import com.desige.webDocuments.utils.ToolsHTML;
import com.desige.webDocuments.utils.beans.SuperAction;
import com.desige.webDocuments.utils.beans.Users;
import com.focus.wonderware.forms.ActiveFactory_frm;

/**
 * Created by IntelliJ IDEA.
 * User: srodriguez
 * Date: Feb 14, 2007
 * Time: 10:31:04 AM
 * To change this template use File | Settings | File Templates.
 */
public class ActiveFactory extends SuperAction {
    static Logger log = LoggerFactory.getLogger(ActiveFactory.class.getName());

    public ActionForward execute(ActionMapping mapping,
                                 ActionForm form, HttpServletRequest request,
                                 HttpServletResponse response) {
        super.init(mapping,form,request,response);
        LoginUser login = (LoginUser) form;
        //System.out.println("login.getUser() = " + login.getUser());
        //System.out.println("login.getClave() = " + login.getClave());

        String documentoActiveFactory=request.getParameter("documentoActiveFactory")!=null?request.getParameter("documentoActiveFactory"):"";
        String usuarioActFactory=request.getParameter("usuarioActFactory")!=null?request.getParameter("usuarioActFactory"):"";
         String passwordActFactory=request.getParameter("passwordActFactory")!=null?request.getParameter("passwordActFactory"):"";
        if (!ToolsHTML.isEmptyOrNull(usuarioActFactory)){
            login.setUser(usuarioActFactory.toString().trim());
            login.setClave(passwordActFactory.toString().trim());
        }
        if (!ToolsHTML.isEmptyOrNull(documentoActiveFactory)){
             putAtributte("documentoActiveFactory",documentoActiveFactory);
        }

      //  if ("true".equalsIgnoreCase(paseCortesia)){
                     //colocamos una session , donde nos lleva el documentoActiveFactory para mostrarlo
                    //porque se cumplio  la seguridad
            //        putObjectSession("documentoActiveFactory",documentoActiveFactory);
            //        boolean swhacerLaURLdeWonderware=false;
           //         String numGenWoderware="";
          //          return mostrarDocumento(documentoActiveFactory,swhacerLaURLdeWonderware, request);
        //}

        if (login!=null) {
            removeObjectSession("usuario");
            Users usuario = new Users(login.getUser(),login.getClave(),null);
            boolean validUser;
            try {
                String url = getParameter("url");
                //si viene una url, implica que el usuario ya esta logueado.
                boolean valUsr = ToolsHTML.isEmptyOrNull(url);//getSessionObject("url")==null;




                //si el usuario esta logueado, no lo va  avalidar en este metodo
                //se hace todo el proceso de chequear licencias y fecha de expiracion..
                validUser = HandlerDBUser.checkUser(usuario,login,valUsr);
                //Si no estoy validando el Usuario es porque voy es a mostrar un Documento
               // if (!valUsr) {
                 //   removeObjectSession("url");
                 //   putObjectSession("user",usuario);
                 //   return new ActionForward(url);
               // }
                if (validUser) {
                    HandlerDBUser.getLanguajeUser(usuario);
                    putObjectSession("usuario",usuario.getNamePerson());
                    usuario.setLastTime(ToolsHTML.sdfShowConvert.format(new java.util.Date()));
					usuario.setLastLogin(ToolsHTML.sdfShowConvert1.format(new java.util.Date()));
                    putObjectSession("user",usuario);
                    ToolsHTML.setLanguage(usuario,request);
                    removeObjectSession("login");
                    //_______________________Validamos la el usario que viene de otra formulario______________________//
                    //_______________________y trae su url para ir a determinado archiov______________________________//
                    //en caso que venga de una pagina y el usario este null, cae por aca
                    //optionReturn me trae un ActionForward con una URL ya preestablecida .
                    if (getSessionObject("optionReturn")!=null) {
                        ActionForward optionReturn = (ActionForward)getSessionObject("optionReturn");
                        if (optionReturn!=null) {
                            BaseDocumentForm forma = (BaseDocumentForm)getSessionObject("showDocument");
                            if (forma!=null) {
                                forma.setPrefix(ToolsHTML.getPrefixToDoc(getSession(),usuario,forma.getIdNode()));
                            }
                            removeObjectSession("optionReturn");
                            return optionReturn;
                        }
                    }
                    //_______________________fin____________________________________________________________________//

                   //_______________________Validamos la si la fecha vencio o si el usuario es______________________//
                  //_______________________nuevo y debe cambiar su contrasenia______________________________________//
                    if (usuario.getLastDatePass()!=null) {
                        log.debug("Last Date Pass: " + usuario.getLastDatePass());
                        String[] values = new String[2];
                        
                        values[0] = String.valueOf(HandlerParameters.PARAMETROS.getExpirePass());
                        values[1] = HandlerParameters.PARAMETROS.getDaysEndPass();

                        if (values!=null) {
                            //si se desea manejar la expiración de Clave
                            if ("0".equalsIgnoreCase(values[0].trim())&&ToolsHTML.isNumeric(values[1])) {
                                java.util.Calendar calendario = new java.util.GregorianCalendar();
                                calendario.add(Calendar.DAY_OF_MONTH,-1 * Integer.parseInt(values[1].trim()));
                                String timeExecution = ToolsHTML.date.format(calendario.getTime());
                                //System.out.println("timeExecution = " + timeExecution);
                                if (timeExecution.compareToIgnoreCase(ToolsHTML.date.format(usuario.getLastDatePass())) >= 0) {
                                    putAtributte("info",getMessage("user.mustChange"));
                                    putObjectSession("mustChange","true");
                                    return goTo("changePass");
                                }
                            }
                        }
                    } else {
                        putAtributte("info",getMessage("user.mustChange"));
                        putObjectSession("mustChange","true");
                        return goTo("changePass");
                    }
                   //_______________________fin____________________________________________________________________//
                     //colocamos una session , donde nos lleva el documentoActiveFactory para mostrarlo
                    //porque se cumplio  la seguridad
                    putObjectSession("documentoActiveFactory",documentoActiveFactory);
                    boolean swhacerLaURLdeWonderware=false;
                    String numGenWoderware="";
                    return mostrarDocumento(documentoActiveFactory,swhacerLaURLdeWonderware, request);
                    //return goSucces();
                } else {
                    removeObjectSession("login");
                    return goError(usuario.getMensaje());
                }
            } catch (ApplicationExceptionChecked aec) {
                aec.printStackTrace();
                return goError(aec.getKeyError());
            } catch (Exception e) {
                e.printStackTrace();
                return goError("E0008");
            }
        } else {
            return goError("E0008");
        }
    }

    public ActionForward mostrarDocumento(String documentoActiveFactory,boolean swhacerLaURLdeWonderware,HttpServletRequest request){
           ActionForward mostrarDoc = new ActionForward("activefactory");
      try{
           BaseDocumentForm forma = new BaseDocumentForm();
           forma.setIdDocument(documentoActiveFactory);
           forma.setNumberGen(documentoActiveFactory);
           //cargamos la ultima version del documento en caso que venga de activefactory y
           //solo traiga el numgen del documento mas no la version
           //    Hashtable tree = (Hashtable)getSessionObject("tree");
           //  tree = ToolsHTML.checkTree(tree,usuario);
           HandlerStruct.loadDocument(forma,true,true,null, request);

           String item = ConstruyendoURL( swhacerLaURLdeWonderware,  forma,request,documentoActiveFactory);
           mostrarDoc = new ActionForward(item.toString(),false);

      }catch(Exception e){
           e.printStackTrace();
      }
        return  mostrarDoc;
    }

    public String ConstruyendoURL(boolean swhacerLaURLdeWonderware, BaseDocumentForm forma,HttpServletRequest request,String numGenWoderware ) throws Exception{
        StringBuffer item = new StringBuffer("");
        String dirServer = ToolsHTML.getServerPath(ToolsHTML.getServerName(request),request.getServerPort(),request.getContextPath());
        if (!swhacerLaURLdeWonderware){
            HandlerProcesosWonderWare handlerProcesosWonderWare=new HandlerProcesosWonderWare();
            ActiveFactory_frm activeFactory = handlerProcesosWonderWare.getActiveFactoryDocuments(forma.getIdDocument());
            if (activeFactory!=null){
                //esto es url para ver el documento como tal..
                item.append("/viewDocument.jsp?nameFile=").append(forma.getNameFile()).append("&idDocument=");
                item.append(forma.getIdDocument()).append("&idVersion=").append(forma.getNumVer());
            }else{
                item.append("/logoutActiveFactory.do?urlActiveFactorynoExiste=1");
            }

        }else{
            //esto es ya darle la url para bque coiloque su login y password, si e ssuministrado, no se le pregunta
            item.append(dirServer).append("/activefactory.do");
            item.append("?usuarioActFactory=&passwordActFactory=").append("&documentoActiveFactory=").append(numGenWoderware);
        }
        return item.toString();
    }
}
