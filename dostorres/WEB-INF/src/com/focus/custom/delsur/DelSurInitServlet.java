package com.focus.custom.delsur;

import java.sql.Connection;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.desige.webDocuments.utils.AfterDigitalDocument;
import com.desige.webDocuments.utils.ToolsHTML;
import com.focus.custom.delsur.util.CustomProperties;
import com.focus.custom.delsur.util.DelSurProcesoUsuarioDigitalizado;

import oracle.jdbc.pool.OracleDataSource;

public class DelSurInitServlet extends HttpServlet {
	private static boolean servletWasStarted = false;
	private static OracleDataSource ods;
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 684661533496191228L;
	private boolean prepararJobEnDemanda;
	
	/**
	 * 
	 */
	private static final Logger log = LoggerFactory.getLogger(DelSurInitServlet.class);
	
	public void init(ServletConfig config) throws ServletException {
		log.info("Iniciando servlet bancario");
		
		try {
			if(config.getInitParameter("prepararJobEnDemanda") != null){
				prepararJobEnDemanda = Boolean.parseBoolean(
						config.getInitParameter("prepararJobEnDemanda"));
			} else {
				prepararJobEnDemanda = true;
			}
			
			//leemos el archivo de propiedades
			log.info("Iniciando lectura de archivo de propiedades");
			CustomProperties props = new CustomProperties();
			log.info("Finalizada lectura de archivo de propiedades");
			
			//conectamos a la base de datos
			log.info("Intentando conexion a la base de datos");
			ods = new OracleDataSource();
			ods.setURL(props.getDelSurCoreJdbcUrl());
			Connection con = null;
			
			try {
				con = ods.getConnection();
				log.info("Conexion a la base de datos realizada satisfactoriamente");
			} catch (Throwable e) {
				// TODO: handle exception
				log.error("No se pudo obtener una conexion contra la base de datos");
			}finally {
				try {
					con.close();
				} catch (Exception e2) {
					// TODO: handle exception
				}
			}
			
			//iniciamos la tarea automatica de lectura de batch
			if(prepararJobEnDemanda){
				log.info("Iniciando configuracion de la tarea repetitiva del batch");
				new DelSurBatchProcess(ods,
						props,
						ToolsHTML.getPath().concat("/digitalizados"));
				log.info("Configuracion de la tarea repetitiva del batch realizada de manera exitosa");
			} else {
				log.info("El job Qweb-Abanks no fue configurado, por el valor indicado en el web.xml");
			}
			
			AfterDigitalDocument.registrarImplementacion(
					new DelSurProcesoUsuarioDigitalizado());
			
			servletWasStarted = true;
		} catch (Throwable e) {
			// TODO Auto-generated catch block
			log.error("Hubo un error durante el inicio del servlet. "
					+ "El error fue: " + e.getMessage(), e);
		}
	}
	
	@Override
	public void destroy() {
		// TODO Auto-generated method stub
		super.destroy();
	}
	
	/**
	 * Para saber si el servlet esta operativo o no.
	 * 
	 * @return
	 */
	public static boolean isServletWasStarted() {
		return servletWasStarted;
	}
}
