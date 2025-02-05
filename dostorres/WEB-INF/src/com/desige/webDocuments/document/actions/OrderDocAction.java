package com.desige.webDocuments.document.actions;

import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.desige.webDocuments.document.comparators.ApprovedComparator;
import com.desige.webDocuments.document.comparators.CreationDateComparator;
import com.desige.webDocuments.document.comparators.NameComparator;
import com.desige.webDocuments.document.comparators.NormISOComparator;
import com.desige.webDocuments.document.comparators.NumberComparator;
import com.desige.webDocuments.document.comparators.OwnerComparator;
import com.desige.webDocuments.document.comparators.StatusComparator;
import com.desige.webDocuments.document.comparators.TypeComparator;
import com.desige.webDocuments.document.comparators.VersionComparator;
import com.desige.webDocuments.utils.ToolsHTML;
import com.desige.webDocuments.utils.beans.SuperAction;

/**
 * Created by IntelliJ IDEA.
 * User: ydavila
 * Date: 26/12/2006
 * Time: 10:08:21 AM
 * To change this template use File | Settings | File Templates.
 */
public class OrderDocAction  extends SuperAction  {

        public synchronized ActionForward execute(ActionMapping mapping,
                                                    ActionForm form, HttpServletRequest request,
                                                    HttpServletResponse response) {
            super.init(mapping,form,request,response);
            Date inicio = new Date();
            //System.out.println("Iniciando Ordenamiento: !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!! hora: " + inicio.toString());
            boolean isPublished = false;
            if (getParameter("pages")!=null&&"published".compareTo(getParameter("pages"))==0) {
                isPublished = true;
            }
            List documentos;// = (List)super.getSessionObject("searchDocs");
            if (isPublished) {
                documentos = (List) getSessionObject("published");
            } else {
                documentos = (List)super.getSessionObject("searchDocs");
            }
            String orderBy = request.getParameter("orderBy");
            if (orderBy == null || documentos == null) {
                if (isPublished) {
                    return goTo("successPub");
                }
                return goSucces();
            }

            boolean isOrderDesc = request.getSession().getAttribute("orderBy")!=null && request.getSession().getAttribute("orderBy").toString().equals(orderBy); 

            request.getSession().setAttribute("orderBy",isOrderDesc?null:orderBy);

            if (orderBy.equals("name")) {
                Collections.sort(documentos, new NameComparator(isOrderDesc));
            } else if (orderBy.equals("type")) {
                Collections.sort(documentos, new TypeComparator(isOrderDesc));
            } else if (orderBy.equals("number")) {
                Collections.sort(documentos, new NumberComparator(isOrderDesc));
            } else if (orderBy.equals("normISO")) {
                Collections.sort(documentos, new NormISOComparator(isOrderDesc));
            } else if (orderBy.equals("owner")) {
                Collections.sort(documentos, new OwnerComparator(isOrderDesc));
            }  else if (orderBy.equals("create")) { //Módulo de Buscar
                Collections.sort(documentos, new CreationDateComparator(isOrderDesc));
            } else if (orderBy.equals("ver")) { //Módulo de Publicados
                Collections.sort(documentos, new VersionComparator(isOrderDesc));
            } else if (orderBy.equals("approved")) { //Módulo de Publicados
                Collections.sort(documentos, new ApprovedComparator(isOrderDesc));
            }  else if (orderBy.equals("status")) { //Módulo de Buscar
                Collections.sort(documentos, new StatusComparator(isOrderDesc));
            }
            
            Date fin = new Date();
            //System.out.println("FIN Ordenamiento: !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!! hora: " + fin.toString());
            setParameters();
            if (isPublished) {                
                return goTo("successPub");
            }
            return goSucces();
      }

    private void setParameters() {
        String TypesStatus = getParameter("TypesStatus");
        String propietario = getParameter("propietario");
        String version = getParameter("version");
        String keys = getParameter("keysDoc");
        String name = getParameter("nombre");
        String number = getParameter("number");
        String typeDoc = getParameter("typeDocument");
        String owner = getParameter("owner");
        String prefix = getParameter("prefix");
        String normISO = getParameter("normISO");
        String publicDoc = getParameter("public");
        String idRout= getParameter("idRout");
        String nameRout= getParameter("nameRout");

        //Luis Cisneros 18-04-07
        //La fecha de vencimiento en publicados no se estaba tomando en cuenta
        String expiredFrom = getParameter("expiredFromHIDDEN");
        String expiredTo= getParameter("expiredToHIDDEN");
        //System.out.println("expiredFromHIDDEN!!!!!!!!!!!!!!!!" + expiredFrom);
        
        putAtributte("idRout",idRout!=null?idRout:"");
        putAtributte("nameRout",nameRout!=null?nameRout:"");


        String accion = getParameter("accion")!=null?getParameter("accion"):"";
        String cargo = getParameter("cargo");
        if (ToolsHTML.isEmptyOrNull(nameRout)) {
            idRout = null;
        }
        String criterio = getParameter("criterio");
        putAtributte("TypesStatus",TypesStatus);
        putAtributte("propietario",propietario);
        putAtributte("version",version);
        putAtributte("keysDoc",keys);
        putAtributte("nombre",name);
        putAtributte("number",number);
        putAtributte("typeDocument",typeDoc);
        putAtributte("public",publicDoc);
        putAtributte("prefix",prefix);
        putAtributte("normISO",normISO);
        putAtributte("owner",owner);

        //Luis Cisneros 18-04-07
        //La fecha de vencimiento en publicados no se estaba tomando en cuenta
        putAtributte("expiredFromHIDDEN", expiredFrom);
        putAtributte("expiredToHIDDEN", expiredTo);

    }

}
