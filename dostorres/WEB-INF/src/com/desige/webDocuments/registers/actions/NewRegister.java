package com.desige.webDocuments.registers.actions;

import java.util.Collection;
import java.util.Hashtable;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.desige.webDocuments.document.forms.BaseDocumentForm;
import com.desige.webDocuments.persistent.managers.HandlerStruct;
import com.desige.webDocuments.persistent.managers.HandlerTypeDoc;
import com.desige.webDocuments.registers.forms.Register;
import com.desige.webDocuments.structured.forms.BaseStructForm;
import com.desige.webDocuments.typeDocuments.forms.TypeDocumentsForm;
import com.desige.webDocuments.utils.ApplicationExceptionChecked;
import com.desige.webDocuments.utils.ToolsHTML;
import com.desige.webDocuments.utils.beans.SuperAction;
import com.desige.webDocuments.utils.beans.Users;

/**
 * Title: NewRegister.java <br/>
 * Copyright: (c) 2004 Focus Consulting C.A.<br/>
 * @author Ing. Nelson Crespo
 * @version WebDocuments v3.0
 * <br/>
 *      Changes:<br/>
 * <ul>
 *      <li> 17/02/2005 (NC) Creation </li>
 * </ul>
 */
public class NewRegister extends SuperAction {
    static Logger log = LoggerFactory.getLogger("[V4.0 FTP] " + NewRegister.class.getName());
    public ActionForward execute(ActionMapping actionMapping,
                                 ActionForm form, HttpServletRequest request,
                                 HttpServletResponse response) {
        super.init(actionMapping,form,request,response);
        String idNodeActual = getParameter("idNodeSelected");
        
        try {
            Users usuario = getUserSession();
            
            String idDocument = getParameter("idDocument");
            String type = getParameter("type");
            log.debug("Type: " + type);
            
            if ("1".equalsIgnoreCase(type)) {
                Collection typesDocuments = HandlerTypeDoc.getAllTypeDocs(null,false);//HandlerWorkFlows.getAllTypesDocuments();
                if (typesDocuments!=null) {
                    putObjectSession("typesDocuments",typesDocuments);
                }
            }
            Register reg = new Register();
            reg.setIdDocument(idDocument);
            BaseDocumentForm forma = new BaseDocumentForm();
            forma.setIdDocument(reg.getIdDocument());
            forma.setNumberGen(reg.getIdDocument());
            try {
//                boolean debeEstarPublicado=true;
                HandlerStruct.loadDocument(forma,true,true,true);
                
                if (forma!=null && (idNodeActual==null || idNodeActual.trim().equals(""))) {
                	idNodeActual=forma.getIdNode();
                }
                
            } catch (Exception ex) {
                ex.printStackTrace();
                throw new ApplicationExceptionChecked("E0033");
            }
            if (!forma.isLoadDoc()) {
                throw new ApplicationExceptionChecked("E0055");
            }
            reg.setIdNodeActual(idNodeActual);
            
            // consultamos el tipo de registro
            TypeDocumentsForm tipo = new TypeDocumentsForm();
            tipo.setId(forma.getTypeDocument());
            HandlerTypeDoc.load(tipo);
            

            String idLocation = HandlerStruct.getIdLocation(idNodeActual);
            //Si no es Documento se Genera el número de Versión del Mismo...
            if ("1".equalsIgnoreCase(type)) {
                log.debug("idLocation = " + idLocation);
                Hashtable tree = (Hashtable)getSessionObject("tree");
                tree = ToolsHTML.checkTree(tree,usuario);
                //Según la localidad se define el formato para la versiones mayores y menores
                if (idLocation!=null) {
                    BaseStructForm localidad = (BaseStructForm)tree.get(idLocation);
                    if (localidad!=null) {
                        reg.setMayorVer(ToolsHTML.getInitValue(localidad.getMajorId(),false));
                        if (!ToolsHTML.isEmptyOrNull(localidad.getMinorKeep())) {
                            if (ToolsHTML.isNumeric(localidad.getMinorKeep())) {
                                int numHist = Integer.parseInt(localidad.getMinorKeep().trim());
                                log.debug("numHist = " + numHist);
                                if (numHist > 0) {
                                    reg.setMinorVer(ToolsHTML.getInitValue(localidad.getMinorId(),true));
                                } else {
                                    reg.setMinorVer("");
                                }
                            }
                        } else {
                            reg.setMinorVer(ToolsHTML.getInitValue(localidad.getMinorId(),true));
                        }

                    }
                }
            }

            log.debug("idDocument = " + idDocument);
            putObjectSession("register",reg);
            request.setAttribute("tipoDocumento", tipo);
//            return goSucces();
            return goTo("success"+type);
        } catch(ApplicationExceptionChecked ae) {
            log.error(ae.getMessage());
            ae.printStackTrace();
            putObjectSession("error",getMessage(ae.getKeyError()));
//            ActionForward resp = new ActionForward("/loadStructMain.do?idNodeSelected="+idNodeActual,false);
//            return resp;
        } catch (Exception ex) {
            log.error(ex.getMessage());
            ex.printStackTrace();
        }
        return  new ActionForward("/loadStructMain.do?idNodeSelected="+idNodeActual,false);
    }
}
