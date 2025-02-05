package com.desige.webDocuments.sacop.actions;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.desige.webDocuments.document.forms.BaseDocumentForm;
import com.desige.webDocuments.utils.beans.SuperAction;

/**
 * Created by IntelliJ IDEA.
 * User: srodriguez
 * Date: Feb 7, 2007
 * Time: 11:36:38 AM
 * To change this template use File | Settings | File Templates.
 */
public class loadNoconformidadesRef_Puente extends SuperAction {
    static Logger log = LoggerFactory.getLogger(CrearSacop.class.getName());
    public ActionForward execute(ActionMapping mapping,
                                 ActionForm form, HttpServletRequest request,
                                 HttpServletResponse response) {

        super.init(mapping,form,request,response);

        try{
                //en caso de haber deseleccionado un check
                String quitarValor = request.getParameter("quitarValor")!=null?request.getParameter("quitarValor"):"";
                // toda la lista de lochecks seleccionados
                String userAssociate= request.getParameter("userAssociate")!=null?request.getParameter("userAssociate"):"";


                 //si number es 1 , vienen las sacop de  la base de datos
                String number = getParameter("number");
                putAtributte("number",number);
                boolean swVieneDeBD=false;
                String relacionarBusqueda="1";
                if ("1".equalsIgnoreCase(number)){
                    relacionarBusqueda="-1";
                    swVieneDeBD=true;
                }
                putAtributte("relacionarBusqueda",relacionarBusqueda);
                 //fin me permitesaber si vienen las sacop ya relacionadas o si se va a crear una sacop

                Sacop_Relacionadas sacop_Relacionadas = new Sacop_Relacionadas();
                //con este metodo devolvemos el hash lleno y sin  repeticipones de los valores que fueron escojidos en el
                //formulario, se compara de nuevo con la coleccionpara saber si es seleccionado o no en los checks
                //  del formulario sacop_relacionadas.jsp
                //y me devuelve una que sera utilizada aqui
                ArrayList elementosSeleccionados = new ArrayList();
                String userAssociateLimpia= sacop_Relacionadas.AssociadosEnLimpio(elementosSeleccionados,userAssociate.toString(),quitarValor);

                //todas la coleccion que se puede seleccionar
                Collection sacops_relacionar= (Collection)getSessionObject("sacops_relacionar");
                //que no queden comas ni que esten repetidos en el hash
                Vector vector = new Vector();
                buscarSiestanSeleccionadas( sacops_relacionar,  vector,  elementosSeleccionados,swVieneDeBD );


             putObjectSession("sacops_relacionar",vector);

             putAtributte("type", request.getParameter("type")!=null?request.getParameter("type"):"");
             putAtributte("from", request.getParameter("from")!=null?request.getParameter("from"):"");
             putAtributte("cmd", request.getParameter("cmd")!=null?request.getParameter("cmd"):"");

             //son variables originales, que se vuelven a utilizar en caso que se quiera ejecutar el formulario de busqueda
             //se usan solo parabusquedas estas variables
             String input = request.getParameter("input");
             String value = request.getParameter("value");
             String nameForm = request.getParameter("nameForma");
             String type1 = request.getParameter("type");
             putAtributte("inputb",input);
             putAtributte("valueb",value);
             putAtributte("nameFormab",nameForm);
             putAtributte("typeb",type1);
             putAtributte("userAssociate",userAssociateLimpia.toString());
             putAtributte("userAssociateLimpia",userAssociateLimpia.toString());
             putAtributte("userAssociateb",userAssociateLimpia);

             // Plantilla1BD
                return goSucces();
        }catch(Exception e){
               log.error(e.getMessage());
            e.printStackTrace();
        } finally {
        }

        return goError();
    }
    public void buscarSiestanSeleccionadas(Collection sacops_relacionar, Vector vector, ArrayList hash,boolean swVieneDeBD ){
        Iterator it  = sacops_relacionar.iterator();
        while(it.hasNext()){
             BaseDocumentForm planilla = (BaseDocumentForm)it.next();
             if (hash.contains(String.valueOf(planilla.getIdDocument()))){
                planilla.setSelected(true);
             }else{
                planilla.setSelected(false);
             }
             if(!(swVieneDeBD)){
                   vector.add(planilla);
             }else if((swVieneDeBD)&&(planilla.isSelected())){
                   vector.add(planilla);
             }

        }


    }
}
