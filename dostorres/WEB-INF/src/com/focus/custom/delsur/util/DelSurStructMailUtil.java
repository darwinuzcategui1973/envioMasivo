package com.focus.custom.delsur.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Calendar;

import com.desige.webDocuments.persistent.managers.HandlerDBUser;
import com.desige.webDocuments.utils.beans.Users;
import com.desige.webDocuments.utils.mail.SendMail;

public final class DelSurStructMailUtil {
	private DelSurStructMailUtil() {
		// TODO Auto-generated constructor stub
	}
	
	/**
	 * 
	 * @param ejecutivoAbanks
	 * @param gerenteAbanks
	 */
	public static void sendNotReadedDocumentsStatus(String ejecutivoAbanks,
			String gerenteAbanks, String codigoSolicitud, String ruta){
		ArrayList<String> emailsUsers = new ArrayList<String>();
		
		Users ejecutivo = HandlerDBUser.getUser(ejecutivoAbanks);
		if(ejecutivo != null){
			emailsUsers.add(ejecutivo.getEmail());
		}
		Users gerente = HandlerDBUser.getUser(gerenteAbanks);
		if(gerente != null){
			emailsUsers.add(gerente.getEmail());
		}
		
		Users adminUser = HandlerDBUser.getUser("admin");
		
		String message = "";
		BufferedReader br = null;
		try {
			br = new BufferedReader(
					new InputStreamReader(
							DelSurStructMailUtil.class.getResourceAsStream("customProperties/notReadedDocuments.html")));
			
			String line = null;
			while((line = br.readLine()) != null){
				message += line;
			}
			
			MessageFormat.format(message, new Object[]{codigoSolicitud,
					ruta});
		} catch (Exception e) {
			// TODO Auto-generated catch block
		} finally {
			try {
				br.close();
			} catch (Exception e2) {
				// TODO: handle exception
			}
		}
		
		SendMail.send(emailsUsers,
				adminUser,
				"No pudo leerse ningun documento de la solicitud " + codigoSolicitud,
				message);
	}
	
	/**
	 * 
	 * @param ejecutivoAbanks
	 * @param gerenteAbanks
	 */
	public static void sendLessFileThanRecords(String ejecutivoAbanks,
			String gerenteAbanks, String codigoSolicitud, String ruta,
			int files, int records){
		ArrayList<String> emailsUsers = new ArrayList<String>();
		
		Users ejecutivo = HandlerDBUser.getUser(ejecutivoAbanks);
		if(ejecutivo != null){
			emailsUsers.add(ejecutivo.getEmail());
		}
		Users gerente = HandlerDBUser.getUser(gerenteAbanks);
		if(gerente != null){
			emailsUsers.add(gerente.getEmail());
		}
		
		Users adminUser = HandlerDBUser.getUser("admin");
		
		String message = "";
		BufferedReader br = null;
		try {
			br = new BufferedReader(
					new InputStreamReader(
							DelSurStructMailUtil.class.getResourceAsStream("customProperties/lessFilesThanRecords.html")));
			
			String line = null;
			while((line = br.readLine()) != null){
				message += line;
			}
			
			MessageFormat.format(message, new Object[]{records
					, codigoSolicitud
					, files
					, ruta});
		} catch (Exception e) {
			// TODO Auto-generated catch block
		} finally {
			try {
				br.close();
			} catch (Exception e2) {
				// TODO: handle exception
			}
			
		}
		
		SendMail.send(emailsUsers,
				adminUser,
				"Faltan recaudos digitalizados en la solicitud " + codigoSolicitud,
				message);
	}
	
	/**
	 * 
	 * @param ejecutivoAbanks
	 * @param gerenteAbanks
	 * @throws IOException 
	 */
	public static void sendAbanksPKGError(String cuentasDeCorreo,
			String messageException){
		ArrayList<String> emailsUsers = new ArrayList<String>();
		String[] cuentas = cuentasDeCorreo.split(",");
		for (String c : cuentas) {
			emailsUsers.add(c);
		}
		
		Users adminUser = HandlerDBUser.getUser("admin");
		
		String message = "";
		BufferedReader br = null;
		try {
			br = new BufferedReader(
					new InputStreamReader(
							DelSurStructMailUtil.class.getResourceAsStream("customProperties/ABanksPKGError.html")));
			
			String line = null;
			while((line = br.readLine()) != null){
				message += line;
			}
			
			MessageFormat.format(message, new Object[]{Calendar.getInstance().getTime()
					, messageException});
		} catch (Exception e) {
			// TODO Auto-generated catch block
		} finally {
			try {
				br.close();
			} catch (Exception e2) {
				// TODO: handle exception
			}
			
		}
		
		SendMail.send(emailsUsers,
				adminUser,
				"Error de conexion con BD Abanks",
				message);
	}
}
