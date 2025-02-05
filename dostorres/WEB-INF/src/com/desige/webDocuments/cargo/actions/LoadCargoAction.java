package com.desige.webDocuments.cargo.actions;

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

import com.desige.webDocuments.cargo.forms.Cargo;
import com.desige.webDocuments.persistent.managers.HandlerBD;
import com.desige.webDocuments.persistent.managers.HandlerProcesosSacop;
import com.desige.webDocuments.persistent.utils.HibernateUtil;
import com.desige.webDocuments.utils.ApplicationExceptionChecked;
import com.desige.webDocuments.utils.ToolsHTML;
import com.desige.webDocuments.utils.beans.Search;
import com.desige.webDocuments.utils.beans.SuperAction;
import com.desige.webDocuments.utils.beans.SuperActionForm;

/**
 * Created by IntelliJ IDEA.
 * User: srodriguez
 * Date: 02-ago-2006
 * Time: 15:17:30
 * To change this template use File | Settings | File Templates.
 */



/**
 * Created by IntelliJ IDEA.
 * User: Administrador
 * Date: 20/03/2006
 * Time: 10:26:54 AM
 * To change this template use File | Settings | File Templates.
 */

public class LoadCargoAction extends SuperAction {
    static Logger log = LoggerFactory.getLogger(LoadCargoAction.class.getName());
    public ActionForward execute(ActionMapping actionMapping,
                                 ActionForm form, HttpServletRequest request,
                                 HttpServletResponse response) {
        super.init(actionMapping,form,request,response);

        try {
            getUserSession();
            Cargo forma=null;
            forma = (Cargo)form;
            if (forma==null){
                //System.out.println("Forma Nula........");
                form = new Cargo();
            }

            removeDataFieldEdit();
            Collection area = HandlerProcesosSacop.getArea(null);
            if (area.size()<=0){
              throw new ApplicationExceptionChecked("scp.noexistearea");

            }
            putObjectSession("area",area);
            Collection cargo =null;
            String idareaAtr=null;
            //String areaAt=null;request.getAttribute("idarea")!=null?(String)request.getAttribute("idarea"):null;
             if ((!ToolsHTML.isEmptyOrNull(String.valueOf(forma.getIdarea()))) &&
                       (forma.getIdarea()!=0)){
                      cargo = HandlerProcesosSacop.getCargo(String.valueOf(forma.getIdarea()));
                      idareaAtr=  String.valueOf(forma.getIdarea());
            }else if (!ToolsHTML.isEmptyOrNull(request.getParameter("idarea"))) {
                      cargo = HandlerProcesosSacop.getCargo(request.getParameter("idarea"));
                       idareaAtr=  String.valueOf(request.getParameter("idarea"));
            }else if (!ToolsHTML.isEmptyOrNull((String)request.getAttribute("idarea"))) {
                      cargo = HandlerProcesosSacop.getCargo((String)request.getAttribute("idarea"));
                       idareaAtr=  String.valueOf((String)request.getAttribute("idarea"));
            }

           if (cargo==null){
                 Iterator  itArea=area.iterator();
                 boolean swHay=false;
                 while ((itArea.hasNext())&&(!swHay)){
                     Search arq = (Search)itArea.next();
                      cargo = HandlerProcesosSacop.getCargo(arq.getId());
                      if (cargo!=null){
                          idareaAtr=  String.valueOf(arq.getId());
                          swHay=true;
                      }
                 }
           }
            //comprobamos que el cargo traiga valor
            if (cargo.size()<=0){
                Vector h=new Vector();
                Search sh = new Search("","");
                h.add(sh);
                cargo=h;
            }
            removeAttribute("idarea");
            putAtributte("idarea",idareaAtr);

            removeObjectSession("dataTable");
            putObjectSession("dataTable",cargo);

//            Iterator c= cargo.iterator();
//            while(c.hasNext()){
//                 Search d = (Search)c.next();
//                //System.out.println("d.getId() = " + d.getId());
//                //System.out.println("......cargo es igual ..................................................................................");
//            }

            putObjectSession("sizeParam",String.valueOf(cargo.size()));
            putObjectSession("editManager","/editPCargo.do");
            putObjectSession("newManager","/newCargo.do");
            putObjectSession("loadManager","loadCargo.do");
            putObjectSession("loadEditManager","/loadCargoEdit.do");
            putObjectSession("formEdit","frmCargo1");
            String cmd = getCmd(request,false);

            removeAttribute("usuarioconcargo");
            if (ToolsHTML.checkValue(cmd)){
                ((SuperActionForm)form).setCmd(cmd);
                //buscamos que este cargo no este en la tabla de usuarios, en tal caso que este, no se podra
                if (SuperActionForm.cmdEdit.equalsIgnoreCase(cmd.trim())){
                    String usertienecargo = HandlerBD.getField("cargo","person","cargo",String.valueOf(forma.getIdcargo()),"=",1,Thread.currentThread().getStackTrace()[1].getMethodName());
                    if (!usertienecargo.trim().equalsIgnoreCase("")){
                        putAtributte("usuarioconcargo","1");
                    }
                }
                request.setAttribute("cmd",cmd);
               // request.setAttribute("cmd",SuperActionForm.cmdLoad);

            } else{
                //System.out.println("cmd 2006-03-20 ="+cmd);
                request.setAttribute("cmd",SuperActionForm.cmdLoad);
            }
            return goSucces();
        } catch (ApplicationExceptionChecked ae) {
            log.error(ae.getMessage());
            return goError(ae.getKeyError());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return goError();
    }
}
