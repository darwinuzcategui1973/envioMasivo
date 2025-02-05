package com.focus.wonderware.intocuh_sacop.actions;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Hashtable;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.desige.webDocuments.persistent.managers.HandlerProcesosSacop;
import com.desige.webDocuments.persistent.utils.HibernateUtil;
import com.desige.webDocuments.sacop.forms.Plantilla1BDesqueleto;
import com.desige.webDocuments.utils.ApplicationExceptionChecked;
import com.desige.webDocuments.utils.beans.SuperAction;
import com.focus.qweb.dao.PlanillaSacop1EsqueletoDAO;
import com.focus.qweb.to.PlanillaSacop1EsqueletoTO;
import com.focus.wonderware.actions.HandlerProcesosWonderWare;

/**
 * Created by IntelliJ IDEA.
 * User: srodriguez
 * Date: Mar 5, 2007
 * Time: 11:21:17 AM
 * To change this template use File | Settings | File Templates.
 */
public class NewSacop_Intouch extends SuperAction {
    public ActionForward execute(ActionMapping mapping,
                                 ActionForm form, HttpServletRequest request,
                                 HttpServletResponse response) {
        super.init(mapping,form,request,response);
        try {

            getUserSession();
            String  editar = request.getParameter("nuevo")!=null?request.getParameter("nuevo"):"";
            //se obtienen los origen de la SACOP
            Collection titulosplanillassacop = HandlerProcesosSacop.getTitulosPlanillasSacop(null);
            putAtributte("titulosplanillassacop",titulosplanillassacop);
            ArrayList a = new ArrayList();

            HandlerProcesosWonderWare handlerProcesosWonderWare = new HandlerProcesosWonderWare();
            Hashtable noEstenSacopIntocuh = new Hashtable();

            Collection sacop_intouch=null;
            if (!"-1".equalsIgnoreCase(editar.toString())){

                //Plantilla1BD planillasacop1Wonderware =  new Plantilla1BD();
                Plantilla1BDesqueleto planillasacop1Wonderware =  new Plantilla1BDesqueleto();
                //encontramos el idplanilasacop1 de tabla padre
                sacop_intouch=handlerProcesosWonderWare.getSacopIntouchWonderwareEditamosTagName(editar.toString(),planillasacop1Wonderware);
                //buscamos todos los tag que se encuentran en sacop_intouch
                handlerProcesosWonderWare.getSacopIntouchWonderwareHijo(noEstenSacopIntocuh,String.valueOf(planillasacop1Wonderware.getIdplanillasacop1()));

                //obtenemos si es borrador o emitido
                PlanillaSacop1EsqueletoDAO oPlanillaSacop1EsqueletoDAO = new PlanillaSacop1EsqueletoDAO();
                PlanillaSacop1EsqueletoTO oPlanillaSacop1EsqueletoTO = new PlanillaSacop1EsqueletoTO();
                
                oPlanillaSacop1EsqueletoTO.setIdplanillasacop1(String.valueOf(planillasacop1Wonderware.getIdplanillasacop1()));
                oPlanillaSacop1EsqueletoDAO.cargar(oPlanillaSacop1EsqueletoTO);
                
                 putAtributte("origenSacop",String.valueOf(oPlanillaSacop1EsqueletoTO.getOrigensacop()).trim());
                 putAtributte("correcpreven",oPlanillaSacop1EsqueletoTO.getCorrecpreven());
                 putAtributte("estado",String.valueOf(oPlanillaSacop1EsqueletoTO.getEstado()));

                //planillasacop1Wonderware.getIdplanillasacop1()
               //si vamos a editar
                putAtributte("sacop_intouch",sacop_intouch);
                putObjectSession("planillasacop1Wonderware",planillasacop1Wonderware);
            }

            //solo seleccionamos de intouch, aquellos tagname que no esten en sacop_intouch
            Collection intouch= handlerProcesosWonderWare.getIntouch(noEstenSacopIntocuh);
            putAtributte("intouch",intouch);



            putAtributte("editar","1");

            return goSucces();
        } catch (ApplicationExceptionChecked ae) {
            ae.printStackTrace();
            return goError(ae.getKeyError());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return goError();
    }


}
