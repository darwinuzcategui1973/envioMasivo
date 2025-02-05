package com.desige.webDocuments.document.actions;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.StringTokenizer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.desige.webDocuments.document.forms.DocumentsRelation;
import com.desige.webDocuments.persistent.managers.HandlerDBUser;
import com.desige.webDocuments.persistent.managers.HandlerDocuments;
import com.desige.webDocuments.persistent.managers.HandlerGrupo;
import com.desige.webDocuments.utils.ApplicationExceptionChecked;
import com.desige.webDocuments.utils.DesigeConf;
import com.desige.webDocuments.utils.ToolsHTML;
import com.desige.webDocuments.utils.beans.SuperAction;
import com.desige.webDocuments.utils.beans.Users;

/**
 * Title: LoadAllDocuments.java <br/>
 * Copyright: (c) 2004 Focus Consulting C.A.<br/>
 * @author Ing. Nelson Crespo
 * @version WebDocuments v3.0
 * <br/>
 * Changes:<br/>
 * <ul>
 *      <li> 10-10-2004 (NC) Creation </li>
 *      <li> 30/06/2006 (NC) se agregó el uso del Log y cambios para mostrar los documentos vinculados.</li> 
 </ul>
 */
public class LoadAllDocuments extends SuperAction {
    static Logger log = LoggerFactory.getLogger("[V3.0] " + LoadAllDocuments.class.getName());
    public ActionForward execute(ActionMapping mapping,
                                 ActionForm form, HttpServletRequest request,
                                 HttpServletResponse response) {
        try {
            super.init(mapping,form,request,response);
            Users usr = getUserSession();
            String numero = getParameter("numero")!=null?getParameter("numero"):"";

            //Luis Cisneros
            //24-03-07
            //Busqueda por prefijos y nombre
            String nombre = getParameter("nombre")!=null?getParameter("nombre"):"";
            String prefijo = getParameter("prefijo")!=null?getParameter("prefijo"):"";

            String read   = getParameter("read");
            String aux    = getParameter("aux");
            log.debug("aux: " + aux);
            removeObjectSession("genXLS");
            if (!ToolsHTML.isEmptyOrNull(aux)) {
                putAtributte("aux",aux);
            }
            Collection documents = null;
            if (!ToolsHTML.isEmptyOrNull(read)) {
                log.debug("Cargando Documentos Relacionados para Su Visualización");
                String idDoc = getParameter("idDoc");
                String genXLS = getParameter("value");
                if (!ToolsHTML.isEmptyOrNull(genXLS)) {
                    putObjectSession("genXLS","true");
                }
                putAtributte("read",read);
                putAtributte("idDoc",idDoc);
                documents = HandlerDocuments.getDocumentsLinks(idDoc,null,usr.getIdGroup(),usr.getUser(),true);
                putObjectSession("onlyRead","true");
            } else {
                removeObjectSession("onlyRead");
                String docRelations = request.getParameter("docRelations");
                log.debug("docRelations " + docRelations);
                //Documentos Relacionados Anteriormente
                String valueLinks = request.getParameter("value");
                log.debug("valueLinks = " + valueLinks);
                ArrayList docLinks = null;
                //Si es la Primera Vez docRelations Viene Nulo
                if (ToolsHTML.isEmptyOrNull(docRelations)) {
                    docLinks = ToolsHTML.createList(valueLinks);
                } else {
                    docLinks = ToolsHTML.createList(docRelations);
                }

                log.debug("docLinks = " + docLinks);

                Hashtable seleccionados = (Hashtable)getSessionObject("seleccionados");
                if (seleccionados==null) {
                    seleccionados = new Hashtable();
                }

                log.debug("seleccionados = " + seleccionados);
                //Si no Hay Documentos Relacionados
                //Se Carga el Hashtable inicial con los documentos previamente Seleccionados en Caso que existan
                if (ToolsHTML.isEmptyOrNull(docRelations)) {
                    if (!ToolsHTML.isEmptyOrNull(valueLinks)) {
                        StringTokenizer st = new StringTokenizer(valueLinks,",");
                        while (st.hasMoreTokens()){
                            String valor = st.nextToken();
                            String[] item = valor.split("_");
                            if (!seleccionados.containsKey(item[0])) {
                                seleccionados.put(item[0],valor);
                            }
                        }
                    }
                } else {
                    StringTokenizer st = new StringTokenizer(docRelations,",");
                    while (st.hasMoreTokens()){
                        String valor = st.nextToken();
                        String[] item = valor.split("_");
                        if (!seleccionados.containsKey(item[0])) {
                            seleccionados.put(item[0],valor);
                        }
                    }
                }
                //System.out.println("seleccionados:" + seleccionados);

                //Quintando Elemento no Desmarcados por el usuario
                String desSelect = getParameter("desSelect");
                if (!ToolsHTML.isEmptyOrNull(desSelect)) {
                    StringTokenizer st = new StringTokenizer(desSelect,",");
                    while (st.hasMoreTokens()) {
                        String valor = st.nextToken();
                        String[] item = valor.split("_");
                        if (seleccionados.containsKey(item[0])) {
                            seleccionados.remove(item[0]);
                        }
                    }
                }
                log.debug("[Seleccionados] " + seleccionados);
                StringBuffer docSelecteds = new StringBuffer(50);
                Enumeration keys = seleccionados.keys();
                boolean isFirst = true;
                while (keys.hasMoreElements()) {
                    String s = (String) keys.nextElement();
                    if (!isFirst) {
                        docSelecteds.append(",");
                    }
                    docSelecteds.append(seleccionados.get(s));
                    isFirst = false;
                }
                putAtributte("docRelations",docSelecteds.toString());
                putObjectSession("docRelations",docSelecteds.toString()); //Luis Cisneros 20/04/07, para que al guardar, se guarden correctamente
                putObjectSession("seleccionados",seleccionados);
                //Se Carga la Seguridad
                StringBuffer idStructs = new StringBuffer(50);
                idStructs.append("1");
                Hashtable security = HandlerGrupo.getAllSecurityForGroup(usr.getIdGroup(),idStructs);
                HandlerDBUser.getAllSecurityForUser(usr.getIdPerson(),security,idStructs);
                Hashtable secDocs = HandlerDocuments.getAllSecurity(usr.getIdGroup(),usr.getUser());
                boolean isAdmon = usr.getIdGroup().compareTo(DesigeConf.getProperty("application.admon")) == 0;
                documents = HandlerDocuments.getAllDocuments(docLinks,numero, prefijo, nombre,security,secDocs,isAdmon,true);
            }
            //si se manda a buscar un correlativo, y este no lo consigue, se manda un
            //attributo exitoBusqueda para utilizarlo en documentslinks.jsp en mandar un mensaje.
            removeAttribute("noHayBusqueda");
            Iterator i = documents.iterator();
            if (i.hasNext()){
             DocumentsRelation bean1 = (DocumentsRelation)i.next();
                 if (!bean1.getExitoBusqueda()){
                     putAtributte("noHayBusqueda","0");
                 }
            }
            putAtributte("numero",numero);
            putAtributte("prefijo",prefijo);
            putAtributte("nombre",nombre);

            putObjectSession("documentsLinks",documents);
            putObjectSession("size",String.valueOf(documents.size()));
            String inputSearch=request.getParameter("input");
            String valueSearch=request.getParameter("value");
            String nameFormaSearch=request.getParameter("nameForma");
            String input = request.getParameter("input");
            String value = request.getParameter("value");
            String nameForm = request.getParameter("nameForma");
            putObjectSession("input",getDataFormResponse(nameForm,input,true));
            putObjectSession("value",getDataFormResponse(nameForm,value,false));
            return goSucces();
        } catch (ApplicationExceptionChecked ae) {
                return goError(ae.getKeyError());
        } catch (Exception ex){
            ex.printStackTrace();
        }
        return mapping.findForward("error");
    }

}
