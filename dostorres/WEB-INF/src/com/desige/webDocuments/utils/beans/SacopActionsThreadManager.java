package com.desige.webDocuments.utils.beans;

import java.util.Calendar;
import java.util.Timer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.desige.webDocuments.utils.ToolsHTML;

/**
 * Clase para gestionar la ejecución
 * 
 * @author frojas
 *
 */
public class SacopActionsThreadManager {
	private static final int HOUR_OF_DAY = 10;
	private static final Logger log = LoggerFactory.getLogger(SacopActionsThreadManager.class);
	private static final long ONE_DAY = 86400000; //24horas * 60minutos * 60000milisegundos
	private static Timer t = new Timer();
	
	/**
	 * 
	 */
	private SacopActionsThreadManager() {
		// TODO Auto-generated constructor stub
	}
	
	/**
	 * 
	 */
	public static void prepareSacopsThreadJob(){
		Calendar cal = Calendar.getInstance();
		Calendar temp = Calendar.getInstance();
		temp.set(Calendar.HOUR_OF_DAY, HOUR_OF_DAY);
		temp.set(Calendar.MINUTE, 0);
		
		//vemos la hora actual, para saber cuando agendar el timer
		if(cal.get(Calendar.HOUR_OF_DAY) > HOUR_OF_DAY){
			//son mas de las 10am, agendamos para mañana.
			temp.add(Calendar.DAY_OF_MONTH, 1);
		}
		
		long delay = temp.getTimeInMillis() - cal.getTimeInMillis();
		delay = (delay < 0 ? 0 : delay);
		
		log.info("El job de las alertas de acciones de las SACOPs se ejecutara a las: "
				+ ToolsHTML.sdfShowConvert.format(temp.getTime()));
		
		//debemos preparar los thread de alertas por accion de las SACOPs
		log.info("Se registra el job diario de revision de acciones por vencer");
		t.schedule(new SacopToExpireActionsAlerts(), delay, ONE_DAY);
		
		log.info("Se registra el job diario de revision de acciones vencidas");
		t.schedule(new SacopExpiredActionsAlerts(), delay, ONE_DAY);
	}
}
