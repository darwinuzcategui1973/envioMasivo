package com.desige.webDocuments.utils.beans;

import java.util.ArrayList;
import java.util.Hashtable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.desige.webDocuments.persistent.managers.HandlerDocuments;
import com.desige.webDocuments.persistent.managers.HandlerParameters;
import com.desige.webDocuments.utils.CheckConditions;
import com.desige.webDocuments.utils.DesigeConf;
import com.desige.webDocuments.utils.ToolsHTML;

/**
 * Created by IntelliJ IDEA.
 * User: srodriguez
 * Date: 27/09/2005
 * Time: 04:34:16 PM
 * To change this template use File | Settings | File Templates.
 */


/**
 * Title: CheckDocvencThread.java <br/>
 * Copyright: (c) 2004 Focus Consulting C.A.<br/>
 * @author Ing. Simon Rodrigu�z (SR)
 * @version WebDocuments v3.0
 * <br/>
 *       Changes:<br/>
 * <ul>
 *       <li> 28/09/2005 (SR) Creation </li>
 *       <li> 30/06/2006 (NC) Mayor sincronizaci�n de Procesos </li> 
 * </ul>
 */
public class CheckDocvencThread extends Thread {
    static Logger log = LoggerFactory.getLogger("[V3.0] " + CheckDocvencThread.class.getName());
    //private long timeSleepCD = Long.parseLong(DesigeConf.getProperty("timeSleepCD"));
    private long timeSleep1Hr = Long.parseLong(DesigeConf.getProperty("timeSleep1Hr"));    
    private long timeSleepShort = Long.parseLong(DesigeConf.getProperty("timeSleepShort"));
    public static boolean isRunnig = false;

    public void run() {
        log.debug("[Begin] CheckDocvencThread");
        boolean shortSleep;
        try {
            while (true) {
                shortSleep = false;
                try {
                    if (!CheckConditions.isRunning) {
                        runTask();
                    } else {
                        log.debug("Short Sleep Active");
                        shortSleep = true;
                        sleep(timeSleepShort);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (!shortSleep) {
                    sleep(timeSleep1Hr);  //Se chequea cada hora
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
            return;
        }
    }

    private synchronized void runTask() {
        try {
            isRunnig = true;
            //Se Carga la Estructura para el manejo de los prefijos seg�n la configuraci�n de la misma
            Users usuario = new Users();
            //Se Coloca el Grupo Administrador
            usuario.setIdGroup(DesigeConf.getProperty("application.admon"));
            //Se Coloca el Id del Usuario Administrador del Sistema
            usuario.setIdPerson(Long.parseLong(DesigeConf.getProperty("application.userAdmonKey")));
            usuario.setUser(DesigeConf.getProperty("application.userAdmon"));
            Hashtable tree = ToolsHTML.checkTree(null,usuario);
            Hashtable prefijos = new Hashtable();
            //manda a notificar los usuarios que el documento se ha vencido
            //Se d� un Aproximado de diez minutos entre cada tarea para que se ejecuten sin ning�n tipo de problema :D
            boolean notifyEmail = ToolsHTML.isNotifyEmail();
            
            try{log.debug("getAllNotifDocumentsExpires BORRADORES");}catch(Exception e){}
            HandlerDocuments.getAllNotifDocumentsExpires(tree,prefijos,notifyEmail,HandlerDocuments.docTrash);
            
            sleep(timeSleepShort);
            
            try{log.debug("getAllNotifDocumentsExpires APROBADOS");}catch(Exception e){}
            HandlerDocuments.getAllNotifDocumentsExpires(tree,prefijos,notifyEmail,HandlerDocuments.docApproved);
            
            sleep(timeSleepShort);
            try{log.debug("getAllDocNearExpiration");}catch(Exception e){}
            ArrayList<Integer> result = HandlerDocuments.getAllDocNearExpiration(tree,prefijos,notifyEmail);
            
            // guardamos los documentos retornados en memoria para futuras consultas
            HandlerParameters.DOCUMENTOS_POR_VENCER.removeAll(HandlerParameters.DOCUMENTOS_POR_VENCER);
            HandlerParameters.DOCUMENTOS_POR_VENCER.addAll(result);
            System.out.println("Documentos por vencer : "+HandlerParameters.DOCUMENTOS_POR_VENCER.size());
            
            sleep(timeSleepShort);
            try{log.debug("getAllNotifDocumentsPrintlnExpires");}catch(Exception e){}
            HandlerDocuments.getAllNotifDocumentsPrintlnExpires(tree,prefijos,notifyEmail);
            
            sleep(timeSleepShort);
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
        	try{log.debug("End CheckDocvencThread");}catch(Exception e){}
            isRunnig = false;
        }
    }

    public static void main(String[] args) {
        CheckDocvencThread check = new CheckDocvencThread();
        check.run();
    }

}
