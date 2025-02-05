package com.focus.custom.delsur.util;

import java.util.StringTokenizer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.desige.webDocuments.to.DigitalTO;
import com.desige.webDocuments.utils.AfterDigitalDocument;
import com.desige.webDocuments.utils.ToolsHTML;

public class DelSurProcesoUsuarioDigitalizado extends AfterDigitalDocument{
	private static final Logger log = LoggerFactory.getLogger(DelSurProcesoUsuarioDigitalizado.class);
	
	public DelSurProcesoUsuarioDigitalizado() {
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public void executeProcessAfterApproved(DigitalTO digitalTO) {
		// TODO Auto-generated method stub
		log.info("Iniciando actualizacion de recaudo aprobado");
		int nroSolicitud = 0;
		int codigoDocumento = 0;
		
		if(ToolsHTML.isEmptyOrNull(digitalTO.getInternalMetaData())){
			log.warn("Se esta importando un recaudo. Importante que las palabras clave contenga lo siguiente.");
			log.warn("<nroSolicitud>,<codigoDocumento>");
			
			if(! ToolsHTML.isEmptyOrNull(digitalTO.getPalabrasClaves())){
				StringTokenizer strTok = new StringTokenizer(digitalTO.getPalabrasClaves(), ",");
				nroSolicitud = Integer.parseInt(strTok.nextToken().trim());
				codigoDocumento = Integer.parseInt(strTok.nextToken().trim());
				
				DelSurRemoteFileUtil.updateStatusRecaudo(nroSolicitud,
						codigoDocumento,
						StatusRecaudos.APROBADO);
			} else {
				log.error("ERROR, se importo un documento sin el debido formato en las palabras clave."
						+ " Recordemos que el formato minimo requerido es: <nroSolicitud>,<codigoDocumento>");
			}
		} else {
			StringTokenizer strTok = new StringTokenizer(digitalTO.getInternalMetaData(), "|");
			codigoDocumento = Integer.parseInt(strTok.nextToken());
			nroSolicitud = Integer.parseInt(strTok.nextToken());
			
			DelSurRemoteFileUtil.updateStatusRecaudo(nroSolicitud,
					codigoDocumento,
					StatusRecaudos.APROBADO);
		}
		
		log.info("Terminada actualizacion de recaudo aprobado "
				+ nroSolicitud + "/" + codigoDocumento);
	}
	
	@Override
	public void executeProcessAfterReject(DigitalTO digitalTO) {
		// TODO Auto-generated method stub
		log.info("Iniciando actualizacion de recaudo rechazado");
		int nroSolicitud = 0;
		int codigoDocumento = 0;
		
		if(ToolsHTML.isEmptyOrNull(digitalTO.getInternalMetaData())){
			log.warn("Se esta importando un recaudo. Importante que las palabras clave contenga lo siguiente.");
			log.warn("<nroSolicitud>,<codigoDocumento>");
			
			if(! ToolsHTML.isEmptyOrNull(digitalTO.getPalabrasClaves())){
				StringTokenizer strTok = new StringTokenizer(digitalTO.getPalabrasClaves(), ",");
				nroSolicitud = Integer.parseInt(strTok.nextToken().trim());
				codigoDocumento = Integer.parseInt(strTok.nextToken().trim());
				
				DelSurRemoteFileUtil.updateStatusRecaudo(nroSolicitud,
						codigoDocumento,
						StatusRecaudos.RECHAZADO);
			} else {
				log.error("ERROR, se importo un documento sin el debido formato en las palabras clave."
						+ " Recordemos que el formato minimo requerido es: <nroSolicitud>,<codigoDocumento>");
			}
		} else {
			StringTokenizer strTok = new StringTokenizer(digitalTO.getInternalMetaData(), "|");
			codigoDocumento = Integer.parseInt(strTok.nextToken());
			nroSolicitud = Integer.parseInt(strTok.nextToken());
			
			DelSurRemoteFileUtil.updateStatusRecaudo(nroSolicitud,
					codigoDocumento,
					StatusRecaudos.RECHAZADO);
		}
		
		log.info("Terminando actualizacion de recaudo rechazado"
				+ nroSolicitud + "/" + codigoDocumento);
	}
}
