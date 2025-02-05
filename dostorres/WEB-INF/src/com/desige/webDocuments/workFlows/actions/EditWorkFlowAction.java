package com.desige.webDocuments.workFlows.actions;

import java.util.Collection;
import java.util.Date;
import java.util.Hashtable;
import java.util.ResourceBundle;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.desige.webDocuments.document.forms.BaseDocumentForm;
import com.desige.webDocuments.persistent.managers.HandlerParameters;
import com.desige.webDocuments.persistent.managers.HandlerStruct;
import com.desige.webDocuments.persistent.managers.HandlerWorkFlows;
import com.desige.webDocuments.structured.forms.BaseStructForm;
import com.desige.webDocuments.utils.ApplicationExceptionChecked;
import com.desige.webDocuments.utils.ToolsHTML;
import com.desige.webDocuments.utils.UserWithoutPermissionToFlexFlowException;
import com.desige.webDocuments.utils.beans.SuperAction;
import com.desige.webDocuments.utils.beans.Users;
import com.desige.webDocuments.workFlows.forms.BaseWorkFlow;
import com.desige.webDocuments.workFlows.forms.DataWorkFlowForm;

/**
 * Title: EditWorkFlowAction.java<br>
 * Copyright: (c) 2004 Focus Consulting C.A.<br/>
 * @author Ing. Nelson Crespo (NC)
 * @author Ing. Sim�n Rodrigu�z(SR)
 * @version WebDocuments v3.0
 * <br>
 *     Changes:<br>
 * <ul>
 *      <li> 29/07/2004 (NC) Creation </li>
 *      <li> 14/05/2005 (NC) Se agregaron cambios en el manejo de los archivos y en el contro de la
 *                           estructura </li> 
 *      <li> 13/07/2006 (NC) Cambios para heredar o no los Prefijos de las Carpetas </li>           
 *      <li> 19/07/2006 (SR Se arego el prefijo y numero a los mails </li> 
 * </ul>
 */
public class EditWorkFlowAction extends SuperAction {
    public ActionForward execute(ActionMapping mapping,
                                 ActionForm form, HttpServletRequest request,
                                 HttpServletResponse response) {
        ResourceBundle rb = ToolsHTML.getBundle(request);
        super.init(mapping,form,request,response,rb);
        String comments = "";
        String str="";
        String comentarioOrigen;
        int subtypeWFInt=0;
        BaseWorkFlow forma = (BaseWorkFlow)form;
        //SIMON 26 MAYO 2005 INICIO
        comentarioOrigen=(forma.getComments()!=null?forma.getComments():"");
        try {
        	str = forma.getTypeWF()==1?HandlerParameters.PARAMETROS.getMsgWFAprobados():HandlerParameters.PARAMETROS.getMsgWFRevision();
            //ydavila Ticket 001-00-003023
            subtypeWFInt=forma.typeWF;
            if (subtypeWFInt==3) {
            	str=str.concat(rb.getString("wf.titleWFMail3"));
            }else if (subtypeWFInt==4) {
            	str=str.concat(rb.getString("wf.titleWFMail4"));
            }
        } catch(Exception e) {
        	e.printStackTrace();
        }
        if (ToolsHTML.isEmptyOrNull(forma.getComments())) {
            if (ToolsHTML.isEmptyOrNull(str)){
                forma.setComments(rb.getString("wf.newWFMessage"+forma.getTypeWF()));
            } else {
                forma.setComments(str);
           }
        } else {
            forma.setComments(str+" "+ forma.getComments());
            comments = forma.getComments();
        }
         //SIMON 26 MAYO FIN
     //   forma.setTitleForMail(rb.getString("wf.newWFTitle"));
        try {
            Users user = getUserSession();
            removeAttribute("usersWF");
            removeAttribute("showDataWF");
            removeAttribute("info");

            BaseDocumentForm docForm = new BaseDocumentForm();
            String idDoc = String.valueOf(forma.getNumDocument());
            docForm.setIdDocument(idDoc);
            docForm.setNumberGen(idDoc);
            Hashtable tree = (Hashtable)getSessionObject("tree");
            tree = ToolsHTML.checkTree(tree,user);
            HandlerStruct.loadDocument(docForm,true,false,null, request);//Se Carga La mayor Versi�n del Documento ya que es sobre la cual
                                                     // se est� Realizando el Cambio

//            docForm.setPrefix(ToolsHTML.getPrefixToDoc(getSession(),user,docForm.getIdNode()));
            forma.setTitleForMail(rb.getString("wf.newWFTitle")+" "+docForm.getPrefix() + " " + docForm.getNumber());
            forma.setComments(forma.getComments()+getRestDataNotified(request,docForm,rb,user.getNamePerson()));
            forma.setNumVersion(docForm.getNumVer());

            //Luis Cisneros
            //28-03-07
            //Antes de insertar el flujo, debemos indicarle a todos los demas flujos que al campo de MostarEnCacelados va a ser false
            //Para que no se muestre en el listado 

             HandlerWorkFlows.updateWFViewInCanceled(docForm.getIdDocument(), "0", "1");

            //Fin 28-03-07

            //insertamos el flujo de trabajo y los participantes del flujo.
            if (HandlerWorkFlows.insert(forma,user)) {
				removeAttribute("comments",request);
				removeAttribute("showDataWF",request);
				removeAttribute("usersWF",request);
                DataWorkFlowForm dataWF = new DataWorkFlowForm();
                dataWF.setNumDocument(forma.getNumDocument());
                dataWF.setIdWorkFlow(String.valueOf(forma.getNumWF()));
                HandlerWorkFlows.loadDataWorkFlow("workflows",false,dataWF);
                dataWF.setNameFile(docForm.getNameFile());
                Collection usersWF = HandlerWorkFlows.getAllUserInWorkFlow(String.valueOf(forma.getNumWF()));
                
                putObjectSession("usersWF",usersWF);
                putObjectSession("showDataWF",dataWF);
                putObjectSession("info",rb.getString("wf.ok"));
                //Code to show Charge User or Name User
//                Hashtable tree = (Hashtable)getSessionObject("tree");
//                tree = ToolsHTML.checkTree(tree,user);
                String idLocation = HandlerStruct.getIdLocationToNode(tree,docForm.getIdNode());
                removeObjectSession("showCharge");
                if (idLocation!=null) {
                    BaseStructForm localidad = (BaseStructForm)tree.get(idLocation);
                    if (localidad!=null) {
                        if (localidad.getShowCharge()==1) {
                            putObjectSession("showCharge","true");
                        }
                    }
                }
                //end
                return goSucces();
            }
		} catch (ApplicationExceptionChecked ap) {
            ap.printStackTrace();
            forma.setComments(comentarioOrigen);
           // forma.setComments("");
            return goError(ap.getKeyError());
		} catch(UserWithoutPermissionToFlexFlowException ex) {
            return goError("application.UserWithoutPermissionToFlexFlow");
        } catch (Exception ex) {
			putObjectSession("info",rb.getString("wf.Notok"));
            ex.printStackTrace();
        }
        forma.setComments(comentarioOrigen);
        return goError();
    }

    public static synchronized String getRestDataNotified(HttpServletRequest request,BaseDocumentForm docForm,
                                                          ResourceBundle rb,String usuario) {
        StringBuffer rest = new StringBuffer(100);
        String dirServer = ToolsHTML.getServerPath(ToolsHTML.getServerName(request),request.getServerPort(),request.getContextPath());

        rest.append("<br/>");
        rest.append(rb.getString("doc.name")).append(": ").append(docForm.getNameDocument());
        rest.append("<br/>");
        rest.append(rb.getString("doc.Ver")).append(": ").append(docForm.getMayorVer()).append(".").append(docForm.getMinorVer());
        rest.append("<br/>");
        rest.append(rb.getString("doc.number")).append(": ").append(docForm.getPrefix()).append(docForm.getNumber());        
        rest.append("<br/>");
        if (usuario!=null) {
            rest.append(rb.getString("wf.request")).append(": ").append(usuario);
            rest.append("<br/>");
            rest.append(rb.getString("doc.dateCreation")).append(": ").append(ToolsHTML.sdfShow.format(new Date()));
            rest.append("<br/>");
        }
        rest.append("<a href='");
        //rest.append(dirServer).append("/viewDocument.jsp?jairo=&nameFile=").append(docForm.getNameFile()).append("&idDocument=");
        //rest.append(docForm.getIdDocument()).append("&idVersion=").append(docForm.getNumVer());
        rest.append(dirServer).append(!dirServer.endsWith("/")?"/":"").append("loadDataDoc.do?nameFile=").append(docForm.getNameFile()).append("&idDocument=");
        rest.append(docForm.getIdDocument()).append("&idVersion=").append(docForm.getNumVer());
        rest.append("' target='_blank'>").append(rb.getString("wf.view")).append("</a>");
        
        return rest.toString();
    }
    

    public static synchronized String getMessageChangeName(BaseDocumentForm docForm,String oldName,ResourceBundle rb,String usuario) {
		StringBuffer rest = new StringBuffer(100);
		rest.append(rb.getString("doc.messageChangeName"));
		rest.append("<br/>");
		rest.append(rb.getString("doc.messageChangeNameAnt")).append(": ").append(oldName);
		rest.append("<br/>");
		rest.append(rb.getString("doc.messageChangeNameSig")).append(": ").append(docForm.getNameDocument());
		rest.append("<br/>");
		rest.append("<br/>");
		rest.append(rb.getString("wf.razondelcambio")).append(": ").append(docForm.getRazonDeCambio());
		rest.append("<br/>");
		rest.append(rb.getString("doc.Ver")).append(": ").append(docForm.getMayorVer()).append(".").append(docForm.getMinorVer());
		rest.append("<br/>");
		rest.append(rb.getString("doc.number")).append(": ").append(docForm.getPrefix()).append(docForm.getNumber());        
		rest.append("<br/>");
		if (usuario!=null) {
			rest.append(rb.getString("cbs.historyUser")).append(": ").append(usuario);
			rest.append("<br/>");
			rest.append(rb.getString("doc.dateCreation")).append(": ").append(ToolsHTML.sdfShow.format(new Date()));
			rest.append("<br/>");
		}
		
		return rest.toString();
    }
    
    public static synchronized String getMessageChangeNameHistory(BaseDocumentForm docForm,String oldName,ResourceBundle rb) {
		StringBuffer rest = new StringBuffer(100);
		rest.append(rb.getString("doc.messageChangeNameAnt")).append(": ").append(oldName);
		rest.append("<br/>");
		rest.append(rb.getString("doc.messageChangeNameSig")).append(": ").append(docForm.getNameDocument());
		rest.append("<br/>");
		rest.append("<br/>");
		rest.append(rb.getString("wf.razondelcambio")).append(": ").append(docForm.getRazonDeCambio());

		return rest.toString();
    }
    
    public static synchronized String getMessageChangeFiles(BaseDocumentForm docForm,ResourceBundle rb) {
		StringBuffer rest = new StringBuffer(100);
		if(docForm.getToForFiles().equals("0")) {
			rest.append(rb.getString("files.document.enabled"));
		} else {
			rest.append(rb.getString("files.document.disabled"));
		}


		return rest.toString();
    }
    
    
    public static synchronized String getMessageChangeOwner(BaseDocumentForm docForm,String oldName,ResourceBundle rb,String usuario) {
		StringBuffer rest = new StringBuffer(100);
		rest.append(rb.getString("doc.messageChangeOwner"));
		rest.append("<br/>");
		rest.append(rb.getString("doc.messageChangeOwnerAnt")).append(": ").append(oldName);
		rest.append("<br/>");
		rest.append(rb.getString("doc.messageChangeOwnerSig")).append(": ").append(docForm.getNameOwner());
		rest.append("<br/>");
		rest.append("<br/>");
		rest.append(rb.getString("wf.razondelcambio")).append(": ").append(docForm.getRazonDeCambio());
		rest.append("<br/>");
		rest.append(rb.getString("doc.Ver")).append(": ").append(docForm.getMayorVer()).append(".").append(docForm.getMinorVer());
		rest.append("<br/>");
		rest.append(rb.getString("doc.number")).append(": ").append(docForm.getPrefix()).append(docForm.getNumber());        
		rest.append("<br/>");
		if (usuario!=null) {
			rest.append(rb.getString("cbs.historyUser")).append(": ").append(usuario);
			rest.append("<br/>");
			rest.append(rb.getString("doc.dateCreation")).append(": ").append(ToolsHTML.sdfShow.format(new Date()));
			rest.append("<br/>");
		}
		
		return rest.toString();
    }

    public static synchronized String getMessageChangeOwnerHistory(BaseDocumentForm docForm,String oldName,ResourceBundle rb) {
		StringBuffer rest = new StringBuffer(100);
		rest.append(rb.getString("doc.messageChangeOwnerAnt")).append(": ").append(oldName);
		rest.append("<br/>");
		rest.append(rb.getString("doc.messageChangeOwnerSig")).append(": ").append(docForm.getNameOwner());
		rest.append("<br/>");
		rest.append("<br/>");
		rest.append(rb.getString("wf.razondelcambio")).append(": ").append(docForm.getRazonDeCambio());

		return rest.toString();
    }
}
