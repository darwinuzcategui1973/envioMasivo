package com.gestionEnvio.custon.dostorres.actions;

import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.desige.webDocuments.utils.beans.SuperAction;
import com.gestionEnvio.custon.dostorres.comparators.AvisoComparator;
import com.gestionEnvio.custon.dostorres.comparators.CodigoComparator;
import com.gestionEnvio.custon.dostorres.comparators.FechaEmisionComparator;
import com.gestionEnvio.custon.dostorres.comparators.LocalComparator;
import com.gestionEnvio.custon.dostorres.comparators.NombreComparator;

public class OrdenRecibosPendientesAction extends SuperAction  {

    public synchronized ActionForward execute(ActionMapping mapping,
                                                ActionForm form, HttpServletRequest request,
                                                HttpServletResponse response) {
        super.init(mapping,form,request,response);
        Date inicio = new Date();
        System.out.println("Iniciando Ordenamiento: !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!! hora: " + inicio.toString());
        boolean isRecibo = false;
        if (getParameter("pages")!=null&&"recibo".compareTo(getParameter("pages"))==0) {
            isRecibo = true;
        }
        List recibos;
        if (isRecibo) {
            recibos = (List) getSessionObject("recibo");
        } else {
        	recibos = (List)super.getSessionObject("searchDocs");
        }
        String orderBy = request.getParameter("orderBy");
        if (orderBy == null || recibos == null) {
            if (isRecibo) {
                return goTo("successRec");
            }
            return goSucces();
        }

        boolean isOrderDesc = request.getSession().getAttribute("orderBy")!=null && request.getSession().getAttribute("orderBy").toString().equals(orderBy); 

        request.getSession().setAttribute("orderBy",isOrderDesc?null:orderBy);

        if (orderBy.equals("nombre")) {
            Collections.sort(recibos, new NombreComparator(isOrderDesc));
        } else if (orderBy.equals("aviso")) {
            Collections.sort(recibos, new AvisoComparator(isOrderDesc));
        } else if (orderBy.equals("codigo")) {
            Collections.sort(recibos, new CodigoComparator(isOrderDesc));
        } else if (orderBy.equals("local")) {
            Collections.sort(recibos, new LocalComparator(isOrderDesc));
        } else if (orderBy.equals("emision")) {
            Collections.sort(recibos, new FechaEmisionComparator(isOrderDesc));
        } 
    
        
        
        Date fin = new Date();
        System.out.println("FIN Ordenamiento Avisos Pendientes: !!!!!!!!!!!!!!!!!!!! hora: " + fin.toString());
        setParameters();
        if (isRecibo) {                
            return goTo("successRec");
        }
        return goSucces();
  }

private void setParameters() {
    String aviso = getParameter("aviso");
    String codigo = getParameter("codigo");
    String local = getParameter("local");
    String nombre = getParameter("nombre");
    String periodo = getParameter("periodo");
    String unNombre = getParameter("unNombre");
    String unLocal = getParameter("unLocal");
    
    //Luis Cisneros 18-04-07
    //La fecha de vencimiento en publicados no se estaba tomando en cuenta
    String emisionFrom = getParameter("emisionFromHIDDEN");
    String emisionTo= getParameter("emisionToHIDDEN");
    //System.out.println("expiredFromHIDDEN!!!!!!!!!!!!!!!!" + expiredFrom);
    
    

    String accion = getParameter("accion")!=null?getParameter("accion"):"";
    
  
    String criterio = getParameter("criterio");
    putAtributte("aviso",aviso);
    putAtributte("codigo",codigo);
    putAtributte("local",local);
    putAtributte("nombre",nombre);
    putAtributte("periodo",periodo);
    putAtributte("unNombre",unNombre);
    putAtributte("unLocal",unLocal);
        //La fecha de vencimiento en publicados no se estaba tomando en cuenta
    putAtributte("emisionFromHIDDEN", emisionFrom);
    putAtributte("emisionToHIDDEN", emisionTo);

}

}
