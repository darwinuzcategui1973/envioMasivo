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

import com.desige.webDocuments.sacop.forms.Plantilla1BD;
import com.desige.webDocuments.utils.beans.SuperAction;

/**
 * Created by IntelliJ IDEA.
 * User: srodriguez
 * Date: Jan 30, 2007
 * Time: 9:10:54 AM
 * To change this template use File | Settings | File Templates.
 */
public class Sacop_Relacionadas_Puente extends SuperAction {
	static Logger log = LoggerFactory.getLogger(CrearSacop.class.getName());
	public ActionForward execute(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) {

		super.init(mapping,form,request,response);

		try{
			String quitarValor = request.getParameter("quitarValor")!=null?request.getParameter("quitarValor"):"";
			String userAssociate= request.getParameter("userAssociate")!=null?request.getParameter("userAssociate"):"";

			String number = getParameter("number");
			putAtributte("number",number);
			//me permitesaber si vienen las sacop ya relacionadas o si se va a crear una sacop y
			//se va a relacionar por  primera vez
			String relacionarBusqueda="1";
			if ("1".equalsIgnoreCase(number)){
				relacionarBusqueda="-1";
			}
			putAtributte("relacionarBusqueda",relacionarBusqueda);


			Sacop_Relacionadas sacop_Relacionadas = new Sacop_Relacionadas();
			//con este metodo devolvemos el hash lleno y sin  repeticipones de los valores que fueron escojidos en el
			//formulario, se compara de nuevo con la coleccionpara saber si es seleccionado o no en los checks
			//  del formulario sacop_relacionadas.jsp
			//y me devuelve una que sera utilizada aqui
			ArrayList hash = new ArrayList();
			String userAssociateLimpia= sacop_Relacionadas.AssociadosEnLimpio(hash,userAssociate.toString(),quitarValor);
			//  StringTokenizer strk = new StringTokenizer(userAssociate,",");

			//                while (strk.hasMoreTokens()){
			//                     String id = (String)strk.nextToken();
			//                     if (!hash.contains(id))  {
			//                          hash.add(id);
			//                     }
			//                }



			// hash.remove(quitarValor);
			//                Iterator iu = hash.iterator();
			//                for (;iu.hasNext();){
			//                     String u = (String)iu.next();
			//                         userAssociateLimpia.append(u);
			//                         if (iu.hasNext()){
			//                             userAssociateLimpia.append(",");
			//                         }
			//               }

			Collection sacops_relacionar= (Collection)getSessionObject("sacops_relacionar");
			Iterator it  = sacops_relacionar.iterator();
			Vector vector = new Vector();

			while(it.hasNext()){
				Plantilla1BD planilla = (Plantilla1BD)it.next();
				//System.out.println("verdad="+hash.contains(String.valueOf(planilla.getIdplanillasacop1())));
				//System.out.println( "planilla.getIdplanillasacop1()="+planilla.getIdplanillasacop1());
				if (hash.contains(String.valueOf(planilla.getIdplanillasacop1()))){
					planilla.setSelected(true);
				}else{
					planilla.setSelected(false);
				}

				vector.add(planilla);
			}

			putObjectSession("sacops_relacionar",vector);

			putAtributte("userAssociate",userAssociateLimpia.toString());
			putAtributte("userAssociateLimpia",userAssociateLimpia.toString());
			putAtributte("type", request.getParameter("type")!=null?request.getParameter("type"):"");
			putAtributte("from", request.getParameter("from")!=null?request.getParameter("from"):"");
			putAtributte("cmd", request.getParameter("cmd")!=null?request.getParameter("cmd"):"");

			//son variables originales, que se vuelven a utilizar en caso que se quiera ejecutar el formulario de busqueda
			//se usan solo parabusquedas estas variables
			String input = request.getParameter("input");
			String value = request.getParameter("value");
			String nameForm = request.getParameter("nameForma");
			String type1 = request.getParameter("type");
			String userAssociate1 = request.getParameter("userAssociate");
			putAtributte("inputb",input);
			putAtributte("valueb",value);
			putAtributte("nameFormab",nameForm);
			putAtributte("typeb",type1);
			putAtributte("userAssociateb",userAssociate1);
			// Plantilla1BD
			return goSucces();
		}catch(Exception e){
			log.error(e.getMessage());
			e.printStackTrace();
		} finally {
		}

		return goError();
	}
}
