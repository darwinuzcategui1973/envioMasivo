package com.focus.wonderware.intocuh_sacop.actions;

import java.io.File;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.upload.FormFile;

import com.desige.webDocuments.sacop.actions.CrearPlanillaSacop;
import com.desige.webDocuments.sacop.forms.plantilla1;
import com.desige.webDocuments.utils.ToolsHTML;
import com.desige.webDocuments.utils.beans.SuperAction;

/**
 * Created by IntelliJ IDEA.
 * User: srodriguez
 * Date: 15/03/2007
 * Time: 03:54:34 PM
 * To change this template use File | Settings | File Templates.
 */
public class LoadArchivoPlanoTagnameII extends SuperAction {
    static Logger log = LoggerFactory.getLogger(LoadArchivoPlanoTagname.class.getName());
    public ActionForward execute(ActionMapping mapping,
                                 ActionForm form, HttpServletRequest request,
                                 HttpServletResponse response) {

        super.init(mapping,form,request,response);

        try{
            plantilla1 forma = (plantilla1)form;
            CrearPlanillaSacop crearPlanillaSacop = new  CrearPlanillaSacop();
            boolean swBorrarArchivo=false;
            removeAttribute("archivoprocesado");
            if (forma.getNameFile()!=null){
                String path=ToolsHTML.getPath().concat("tmp"); // \\tmp
                crearPlanillaSacop.procesaFicheros(request,forma,swBorrarArchivo,path);
                //procesamos archivo plano
                String path1=forma.getPath()+File.separator+forma.getNameFile();
                //___________________________Cargamos los tags______________________________________________________________//
                ArchivoPlanoTagname archivoPlanoTagname = new ArchivoPlanoTagname();
              //  //System.out.println("............................");
              //  //System.out.println("path1 = " + path1);
                archivoPlanoTagname.procesarArchivoPlanoTagname(path1.toString().trim());
                //_________________________________________________________________________________________//
                String nameFile=forma.getNameFile().toString();
                putAtributte("archivoprocesado",nameFile);
                FormFile file = forma.getNameFile();
                file.destroy();
            }


            return goSucces();
        }catch(Exception e){
               log.error(e.getMessage());
            e.printStackTrace();
        } finally {
        }

        return goError();
    }
}
