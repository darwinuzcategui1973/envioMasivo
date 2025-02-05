package com.focus.custom.delsur.util;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.desige.webDocuments.utils.beans.Users;
import com.focus.custom.delsur.DelSurBatchProcess;
import com.focus.custom.delsur.dto.CreditoDTO;
import com.focus.custom.delsur.dto.RecaudoCreditoDTO;

public final class DelSurRemoteFileUtil {
	private static final Logger log = LoggerFactory.getLogger(DelSurRemoteFileUtil.class);
	
	/**
	 * 
	 * @param maxWaitToRead
	 * @param prefixPath
	 * @param destinationPath
	 * @param remotePathSeparator
	 * @param palabrasClaves
	 * @param usuario
	 * @param creditoDTO
	 */
	public static void readRemoteFiles(long maxWaitToRead, String prefixPath, 
			String destinationPath, String remotePathSeparator,
			String palabrasClaves, Users usuario, CreditoDTO creditoDTO){
		
		final String basePath = prefixPath + "Agencia" 
				+ creditoDTO.getCodigoAgencia();
		
		try {
			ThreadReadRemoteFiles target = new ThreadReadRemoteFiles(maxWaitToRead, 
					prefixPath, 
					destinationPath, 
					remotePathSeparator, 
					palabrasClaves, 
					usuario, 
					creditoDTO);
			
			Thread t = new Thread(target);
			t.start();
			
			boolean continueThread = true;
			while(continueThread){
				//sigo esperando la ejecucion del thread
				if(!target.isProcessEnd()
						&& !target.isTimeOutReached()){
					continueThread = false;
					
					if(target.isTimeOutReached()){
						log.info("El thread de lectura remota de la solicitud "
								+ creditoDTO.getNumeroSolicitud() 
								+ " alcanzo el tiempo maximo de espera, se procede a finalizarlo y continuar.");
						actualizarRecaudosComoNoLeidos(creditoDTO.getNumeroSolicitud(),
								creditoDTO.getListadoRecaudos(),
								creditoDTO.getCodigoUsuarioAbanks(),
								creditoDTO.getCodigoGerenteAbanks(),
								basePath);
					}
				}
			}
			
			t.interrupt();
			target = null;
			t = null;
			System.gc();
		} catch (Throwable e) {
			// TODO: handle exception
			log.error("Error: " + e.getMessage(), e);
		}
	}

	/**
	 * Por algun motivo no se pudo leer el directorio remoto
	 * Debemos indicar esto por correo y tambien 
	 * actualizar los insumos en la base de datos.
	 * 
	 * @param nroSolicitud
	 * @param recaudos
	 * @param ejecutivoAbanks 
	 * @param gerenteAbanks 
	 * @param codigoSolicitud 
	 * @param ruta 
	 */
	private static void actualizarRecaudosComoNoLeidos(int nroSolicitud,
			List<RecaudoCreditoDTO> recaudos, String ejecutivoAbanks,
			String gerenteAbanks, String ruta) {
		// TODO Auto-generated method stub
		//enviamos el correo que corresponda
		DelSurStructMailUtil.sendNotReadedDocumentsStatus(ejecutivoAbanks,
				gerenteAbanks, 
				Integer.toString(nroSolicitud), 
				ruta);
		
		for (RecaudoCreditoDTO recaudoCreditoDTO : recaudos) {
			updateStatusRecaudo(nroSolicitud, 
					recaudoCreditoDTO.getCodigoDocumento(), 
					StatusRecaudos.NO_LEIDO);
		}
	}
	
	/**
	 * 
	 * @param nroSolicitud
	 * @param codigoDocumento
	 * @param newStatus
	 */
	public static void updateStatusRecaudo(int nroSolicitud,
			int codigoDocumento, StatusRecaudos newStatus){
		final String query = "CALL ws_k_interface.ws_p_actualizar_recaudo(?,?,?)";
		
		Connection con = null;
		CallableStatement cs = null;
		
		try {
			con = DelSurBatchProcess.getAbanksConnection();
			cs = con.prepareCall(query);
			cs.setInt(1, nroSolicitud);
			cs.setInt(2, codigoDocumento);
			cs.setString(3, newStatus.getValue());
			
			cs.execute();
			log.info("Actualizado correctamente estatus del documento '"
					+ codigoDocumento + "' en la solicitud '"
					+ nroSolicitud + "' con el valor " + newStatus.getValue());
		} catch (Exception e) {
			// TODO: handle exception
			log.error("Error actualizando estatus del documento '"
					+ codigoDocumento + "' en la solicitud '"
					+ nroSolicitud + "' con el valor " + newStatus.getValue(), e);
		} finally {
			try {
				cs.close();
			} catch (Exception e2) {
				// TODO: handle exception
			}
			
			try {
				con.close();
			} catch (Exception e2) {
				// TODO: handle exception
			}
		}
	}
}
