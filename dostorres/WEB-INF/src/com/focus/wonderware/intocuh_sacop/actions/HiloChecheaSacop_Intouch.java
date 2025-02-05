package com.focus.wonderware.intocuh_sacop.actions;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.transaction.Transaction;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.desige.webDocuments.persistent.managers.HandlerParameters;
import com.desige.webDocuments.persistent.managers.HandlerProcesosSacop;
import com.desige.webDocuments.persistent.managers.HandlerStruct;
import com.desige.webDocuments.sacop.actions.CrearPlanillaSacop;
import com.desige.webDocuments.sacop.actions.LoadSacop;
import com.desige.webDocuments.sacop.forms.Plantilla1BD;
import com.desige.webDocuments.sacop.forms.Plantilla1BDesqueleto;
import com.desige.webDocuments.utils.DesigeConf;
import com.desige.webDocuments.utils.ToolsHTML;
import com.desige.webDocuments.utils.beans.Search;
import com.focus.qweb.dao.PlanillaSacop1DAO;
import com.focus.qweb.to.PlanillaSacop1TO;
import com.focus.wonderware.actions.HandlerProcesosWonderWare;
import com.focus.wonderware.intocuh_sacop.forms.Sacop_Intouchh_frm;
import com.focus.wonderware.intocuh_sacop.forms.sacop_intouch_padre_frm;

/**
 * Created by IntelliJ IDEA.
 * User: srodriguez
 * Date: 14/03/2007
 * Time: 02:01:12 PM
 * To change this template use File | Settings | File Templates.
 */
public class HiloChecheaSacop_Intouch extends Thread {
    Locale defaultLocale = new Locale(DesigeConf.getProperty("language.Default"),DesigeConf.getProperty("country.Default"));
    ResourceBundle rb = ResourceBundle.getBundle("LoginBundle",defaultLocale);
    static Logger log = LoggerFactory.getLogger("[V4.0 FTP] " + HiloChecheaSacop_Intouch.class.getName());
    private long timeSleepCC = Long.parseLong(DesigeConf.getProperty("timeSleepCC"));
    private long timeSleepShort = Long.parseLong(DesigeConf.getProperty("timeSleepShort"));
    private long timeSleep5segundo = Long.parseLong(DesigeConf.getProperty("timeSleep5segundo"));
    public static boolean isRunning = false;

    public void run() {
        boolean shortSleep;
        log.debug("Begin HiloChecheaSacop_Intouch");
        //java.sql.SQLException
        if (DesigeConf.getProperty("wonderwareactive")!=null && DesigeConf.getProperty("wonderwareactive").equals("0")) {
        	return;
        }
        try {
            while (true) {
                //shortSleep = false;
                try {
                    //if (!HiloChecheaSacop_Intouch.isRunning) {
                        runTask();
                    //} else {
                      //  log.debug("Short Sleep Active");
                     //   sleep(timeSleep5segundo);
                  //  }
                } catch (Exception e) {
                    e.printStackTrace();
                }
              //  if (!shortSleep) {
                    sleep(timeSleep5segundo); //24 horas
               // }
            }
        } catch (InterruptedException e) {
            return;
        }
    }

    private synchronized void runTask() {
        java.util.Date date = new java.util.Date();
        String timeExecution = ToolsHTML.sdfShowConvert1.format(date);
        Timestamp time = new Timestamp(date.getTime());
        try {
        	log.debug("Run HiloChecheaSacop_Intouch at Time: " + timeExecution);
        } catch(Exception e) {
        	
        }

        try {
            isRunning = true;
            HandlerProcesosWonderWare handlerProcesosWonderWare = new HandlerProcesosWonderWare();
            //obtenemos los tags preconfiurados con los tags de intouch y sacop_intouch
            Collection nuevasDisparosSacop=handlerProcesosWonderWare.getTagnameWonderwareQueStanPreconfiguradosInSacopIntouch();
            Iterator it = nuevasDisparosSacop.iterator();
            ArrayList unicos = new ArrayList();
            while(it.hasNext()){
                Search tagnames = (Search)it.next();
                if (!unicos.contains(tagnames.getId())){
                    unicos.add(tagnames.getId());
                }
                ////System.out.println("tagnames.getId()="+tagnames.getId());
                ////System.out.println("tagnames.getDescript() = " + tagnames.getDescript());
            }
            //aqui empieza a buscar tagnames en las sacops preconfiguradas
            if (unicos.size()>0){
                DisparadaPorAlarmaSacop_Intouch(unicos);
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            try {
                log.debug("End HiloChecheaSacop_Intouch");
            } catch(Exception e) {
            	
            }
            isRunning = false;


        }
    }

    public static void main(String[] args) {
        HiloChecheaSacop_Intouch conditions = new HiloChecheaSacop_Intouch();
        conditions.start();
    }

    public void  DisparadaPorAlarmaSacop_Intouch(Collection tagnames){


        try{
           HandlerProcesosWonderWare handlerProcesosWonderWare = new HandlerProcesosWonderWare();
           Transaction tx=null;
            //agarramos todos los registros de la tabla hijo que tengan ese tagname
            Iterator it = tagnames.iterator();
            //tenemos todos los tags, ahora, solo se enviara la planilla que solo tenga por lo menos un tag en su
            //configuracion
            //en varios registros de tbl_sacop_intouch_hijo, pueden haber diferentes tags con la misma planilla
            //solo se emite una sola sacop que contenga dicha planilla en un tag, se resuelve con  unicoPlanilla
            ArrayList unicoPlanilla = new ArrayList();
            while(it.hasNext()){
                String tagname ="'" + (String)it.next() + "'";
                String noSeUsa="";
                boolean soloLosNoActivadosEnSacop=true;
                Collection Actualizasacop_intouch_hijo=handlerProcesosWonderWare.HiloChecSacopIntouchWonderwareHijo(noSeUsa,tagname,soloLosNoActivadosEnSacop);
                Iterator it1 =  Actualizasacop_intouch_hijo.iterator();
               while(it1.hasNext()){
                     Sacop_Intouchh_frm sacop_Intouchh_hijo = (Sacop_Intouchh_frm)it1.next();
                     //verificamos que en la tabla padre esta habilkitada
                     boolean swActivado=true;
                     sacop_intouch_padre_frm intouch_padre=handlerProcesosWonderWare.getSacopIntouchWonderwarePadre(String.valueOf(sacop_Intouchh_hijo.getIdplanillasacop1()),swActivado);
                      if  (intouch_padre!=null){
                        if ("1".equals(String.valueOf(intouch_padre.getEnable()))){
                            //la tabla padre esta habilitadsa y la planilla hijo solo se ejcuta una sola vez
                                if(!unicoPlanilla.contains(new Long(sacop_Intouchh_hijo.getIdplanillasacop1()))){
                                       //verificamos que dicha planilla de esqueleto en tbl_planillasacop1 no exista o este cerrado
                                       //para generar nueva planilla
                                       unicoPlanilla.add(new Long(sacop_Intouchh_hijo.getIdplanillasacop1()));
                                       //si esta cerrada la planilla con el id esqueleto, o no existe la planilla idesqueleto,
                                       //generamos una
                                       //en negacion significa que esta cerrda o no existe y generamos una planilla nueva.
                                       if (!handlerProcesosWonderWare.getExisteIdEsqueletoEn_Planillasacop1(sacop_Intouchh_hijo.getIdplanillasacop1())){

                                           //cargamos la sacop esqueleto, y  de hay realizamos la nueva sacop
                                           //Session  session = HibernateUtil.getSession();
                                           //session.clear();
                                           //session.flush();
                                           Plantilla1BDesqueleto formabdesqueleto=new Plantilla1BDesqueleto();
                                         //  session.load(formabdesqueleto,sacop_Intouchh_hijo.getIdplanillasacop1());

                                           formabdesqueleto= handlerProcesosWonderWare.getPlantilla1BDesqueleto(sacop_Intouchh_hijo.getIdplanillasacop1());
                                           if(formabdesqueleto!=null){
                                               Plantilla1BD formaBD = new  Plantilla1BD(formabdesqueleto);

                                               int idplanillasacop1 = HandlerStruct.proximo("idplanillasacop1","idplanillasacop1","idplanillasacop1");

                                               String numByLocation = HandlerParameters.PARAMETROS.getLengthDocNumber();
                                               
                                               String siglasNum=null;

                                               if(LoadSacop.Correctiva.equalsIgnoreCase(formabdesqueleto.getCorrecpreven())){
                                                   //Solicitud de Acción Correctiva ( SACOP )
                                            	   siglasNum=rb.getString("scp.sac");
                                               }else if (LoadSacop.Preventiva.equalsIgnoreCase(formabdesqueleto.getCorrecpreven())){
                                                   // Solicitud de Acción Preventiva ( SACOP )
                                                   siglasNum=rb.getString("scp.sap");
                                               }




                                               String idNumero="";
                                               //si no es borrador
                                               if (formaBD.getEstado()!=0){
                                                   //este numero es de la sacop verdadera
                                                   long numerosacop= HandlerStruct.proximo("numerosacop","numerosacop","numerosacop");

                                                   idNumero=String.valueOf(numerosacop);
                                               }
                                               String numeroSacop= CrearPlanillaSacop.numSacopDo(siglasNum,idNumero.toString(),numByLocation);
                                               formaBD.setIdplanillasacop1esqueleto(formabdesqueleto.getIdplanillasacop1());
                                               formaBD.setSacopnum(numeroSacop);
                                               formaBD.setIdplanillasacop1(idplanillasacop1);
                                               
                                               
                                               PlanillaSacop1TO oPlanillaSacop1TO = new PlanillaSacop1TO(formaBD);
                                               PlanillaSacop1DAO oPlanillaSacop1DAO = new PlanillaSacop1DAO();
                                               
                                               oPlanillaSacop1DAO.insertar(oPlanillaSacop1TO);
                                               
                                               String estado ="";
                                               if   (formaBD.getEstado()!=0){
                                                     estado=rb.getString("scp.emitido");
                                               }else{
                                                     estado=rb.getString("scp.borrador");
                                               }
                                               //emitimos sacops, mandamos mails y colocamos el status de la planilla en edoEmitida
                                               HandlerProcesosSacop.mandarMailsUsuariosdelSacop(String.valueOf(formaBD.getIdplanillasacop1()),formaBD.getSacopnum(),estado.toString());
                                           }

                                       }
                                }
                        }
                      }
               }
            }
        }catch(Exception e){
            //System.out.println("Error="+e);

        }

    }




}
