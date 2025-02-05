package com.desige.webDocuments.utils.beans;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.desige.webDocuments.persistent.managers.HandlerBD;
import com.desige.webDocuments.persistent.managers.HandlerDBUser;
import com.desige.webDocuments.persistent.managers.HandlerParameters;
import com.desige.webDocuments.persistent.utils.JDBCUtil;
import com.desige.webDocuments.utils.DesigeConf;
import com.desige.webDocuments.utils.ToolsHTML;

/**
 * Title: CheckUsersThread.java <br/>
 * Copyright: (c) 2004 Focus Consulting C.A.<br/>
 * @author Ing. Nelson Crespo
 * @version WebDocuments v3.0
 * <br/>
 *       Changes:<br/>
 * <ul>
 *       <li> 06/03/2005 (NC) Creation </li>
 *       <li> 30/06/2006 (NC) Mayor sincronización de Procesos </li> 
 * </ul>
 */
public class CheckUsersThread extends Thread {
    static Logger log = LoggerFactory.getLogger(CheckUsersThread.class);
    private int timeSession = -5;
    private int timeSessionEdit = -5;
    private long timeCHKUser = Long.parseLong(DesigeConf.getProperty("timeCHKUser"));
    private Connection con = null;

    public CheckUsersThread() {    //10
        log.debug("[BEGIN] CheckUsersThread V3.0 ");
        try {
            String propertyTime= String.valueOf(HandlerParameters.PARAMETROS.getEndSession());
            if (ToolsHTML.isNumeric(propertyTime)) {
                timeSession = -1*Integer.parseInt(propertyTime);
            }
            String propertyTimeEdit=String.valueOf(HandlerParameters.PARAMETROS.getEndSessionEdit());
            if (ToolsHTML.isNumeric(propertyTimeEdit)) {
                timeSessionEdit = -1*Integer.parseInt(propertyTimeEdit);
            }
        } catch(Exception e) {
            e.printStackTrace();
            System.out.print(e);
        }
    }

    public void run() {
        try {
            while (true) {
                try {
                    runTask();
                } catch (Exception e) {
                    e.printStackTrace();
                    System.out.print(e);
                }
                sleep(timeCHKUser);
//                sleep(300000);  //Se chequea cada 5 min
//                sleep(1800000); //30 minutos
            }
        } catch (InterruptedException e) {
        	System.out.print(e);
            return;
        }
    }

    private synchronized void runTask() {
        //java.util.Calendar calendario = new java.util.GregorianCalendar();
        //calendario.add(Calendar.MINUTE,timeSession);
        //String timeExecution = ToolsHTML.sdfShowConvert1.format(calendario.getTime());

        //java.util.Calendar calendarioEdit = new java.util.GregorianCalendar();
        //calendarioEdit.add(Calendar.MINUTE,timeSessionEdit);
        //String timeExecutionEdit = ToolsHTML.sdfShowConvert1.format(calendarioEdit.getTime());

        //log.debug("[V4.0] Run CheckUsersThread at Time: " + String.valueOf(timeExecution));
        try {
            HandlerDBUser.checkUsersConnecteds(con);
            
            
            /*
             * solo para la version 4.5 para atras (versiones viejas)
            if ("1".equalsIgnoreCase(DesigeConf.getProperty("solounavezarea_cargo"))){
               Area_Cargo(con);
            }
            */
        } catch (Exception ex) {
            ex.printStackTrace();
            System.out.print(ex);
        }
    }
    
    public  static  void Area_Cargo(){
    	Area_Cargo(null);
    }
    public  static  void Area_Cargo(Connection con){
    	boolean cerrar=false;
        try{
			if(con==null || con.isClosed()) {
	            con= JDBCUtil.getConnection(Thread.currentThread().getStackTrace()[1].getMethodName());
				cerrar=true;
			}
            StringBuffer sql=new StringBuffer("");
            sql.append(" select nombregrupo,cargo from person p,groupusers g where ");
            sql.append(" g.idgrupo=p.idgrupo  order by cargo");
            PreparedStatement st = con.prepareStatement(JDBCUtil.replaceCastMysql(sql.toString()));
            ResultSet rs = st.executeQuery();
            String h="";
            while(rs.next()){
                 String cargo=rs.getString("cargo")!=null?rs.getString("cargo"):"";
                 String areaa=rs.getString("nombregrupo")!=null?rs.getString("nombregrupo"):"";

                 h=HandlerDBUser.existeCargo(cargo,areaa,con);

                 //System.out.println("h = " + h);
            }
        }catch(Exception e){
            e.printStackTrace();
            System.out.print(e);
        } finally {
        	if(cerrar) {
        		JDBCUtil.closeConnection(con, "Error en Area_Cargo()");
        	}
        }
    }
    
    public static void main(String[] args) {
        CheckUsersThread check = new CheckUsersThread();
        check.run();
    }

	public Connection getCon() {
		return con;
	}

	public void setCon(Connection con) {
		this.con = con;
	}
    
    

}
