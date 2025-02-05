package com.focus.custom.delsur;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Timer;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.focus.custom.delsur.util.CustomProperties;

/**
 * Clase con la que se ejecutara el proceso batch de consumo de la base de datos
 * y lectura de archivos remotos
 *
 */
public class DelSurBatchProcess {
	private static final Logger log = LoggerFactory.getLogger(DelSurBatchProcess.class);
	private static DataSource dataSource;
	private static CustomProperties props;
	private static String carpetaParaDigitalizados;
	
	/**
	 * Obtenemos una conexion hacia el core bancario
	 * 
	 * @return
	 */
	public static Connection getAbanksConnection(){
		try {
			return dataSource.getConnection();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
		}
		
		return null;
	}
	
	/**
	 * 
	 * @param dataSource
	 * @param props
	 * @param string 
	 */
	public DelSurBatchProcess(DataSource dataSource, CustomProperties props,
			String carpetaParaDigitalizados) {
		// TODO Auto-generated constructor stub
		DelSurBatchProcess.dataSource = dataSource;
		DelSurBatchProcess.props = props;
		DelSurBatchProcess.carpetaParaDigitalizados = carpetaParaDigitalizados;
		
		int horaDeEjecucion = props.getHoraEjecucionProcesoBatch();
		long delay = 0;
		Calendar fechaActual = Calendar.getInstance();
		Calendar fechaCorrida = Calendar.getInstance();
		
		//delay = fechaCorrida.getTimeInMillis() - fechaActual.getTimeInMillis();
		delay = props.getInitialDelayEjecucionesBatch();
		log.info("Delay leido del archivo de propiedades = '"
				+ delay + "'");
		
		if(delay == 0){
			if(fechaActual.get(Calendar.HOUR_OF_DAY) > horaDeEjecucion){
				//la proxima corrida es el proximo dia
				fechaCorrida.add(Calendar.DAY_OF_MONTH, 1);
			}
			
			fechaCorrida.set(Calendar.HOUR_OF_DAY, horaDeEjecucion);
			fechaCorrida.set(Calendar.MINUTE, 0);
			fechaCorrida.set(Calendar.SECOND, 0);
			
			delay = fechaCorrida.getTimeInMillis() - fechaActual.getTimeInMillis();
		}else{
			fechaCorrida.setTimeInMillis(fechaCorrida.getTimeInMillis() + delay);
		}
		
		log.info("Proxima ejecucion del proceso batch sera el: " + fechaCorrida.getTime());
		
		new Timer().schedule(new DelSurBatchProcessTask(dataSource, props, carpetaParaDigitalizados), 
				delay,    //initial delay (esperamos hasta las proximas 1am)
				props.getDelayEjecucionesBatch());  //subsequent rate (un dia)
	}
	
	/**
	 * Metodo a ser invocado para la ejecución en demanda del JOB
	 * 
	 */
	public static void executeJobOnDemand(){
		try {
			log.info("EJECUCION DEL JOB EN DEMANDA!!!");
			Thread t = new Thread(new DelSurBatchProcessTask(dataSource, 
					props, 
					carpetaParaDigitalizados,
					true));
			t.start();
		} catch (Throwable e) {
			// TODO: handle exception
			log.error("Error: " + e.getMessage(), e);
		}
	}
}
