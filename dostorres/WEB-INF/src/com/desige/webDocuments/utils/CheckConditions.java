package com.desige.webDocuments.utils;

import java.sql.Timestamp;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.Vector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.desige.webDocuments.persistent.managers.HandlerBD;
import com.desige.webDocuments.persistent.managers.HandlerDocuments;
import com.desige.webDocuments.persistent.managers.HandlerParameters;
import com.desige.webDocuments.persistent.managers.HandlerWorkFlows;
import com.desige.webDocuments.utils.beans.CheckDocvencThread;
import com.desige.webDocuments.workFlows.forms.DataUserWorkFlowForm;

/**
 * Title: CheckConditions.java <br/> Copyright: (c) 2004 Focus Consulting C.A.<br/>
 * 
 * @author Ing. Nelson Crespo
 * @version WebDocuments v3.0 <br/> Changes:<br/>
 *          <ul>
 *          <li> 17/03/2005 (NC) Creation </li>
 *          <li> 30/06/2006 (NC) Mayor sincronización de Procesos </li>
 *          </ul>
 */
public class CheckConditions extends Thread {
	static Logger log = LoggerFactory.getLogger("[V4.0 FTP] " + CheckConditions.class.getName());
	private long timeSleep1Hr = Long.parseLong(DesigeConf.getProperty("timeSleep1Hr"));
	private long timeSleepShort = Long.parseLong(DesigeConf.getProperty("timeSleepShort"));
	public static boolean isRunning = false;

	public void run() {
		boolean shortSleep;
		if (log == null)
			log = LoggerFactory.getLogger("[V4.0 FTP] " + CheckConditions.class.getName());
		log.debug("Begin CheckConditions");
		try {
			while (true) {
				shortSleep = false;
				try {
					if (!CheckDocvencThread.isRunnig) {
						runTask();
					} else {
						try {
							log.debug("Short Sleep Active");
						} catch (Exception e) {
						}
						sleep(timeSleepShort);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				if (!shortSleep) {
					sleep(timeSleep1Hr); // 1 hora
				}
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
			log.debug("Run CheckConditions at Time: " + timeExecution);
			//System.out.println("--Run CheckConditions at Time: " + timeExecution);
		} catch (Exception e) {
		}
		try {
			isRunning = true;
			Vector datos = (Vector) HandlerWorkFlows.getAllWorkFlowsPendingsWithDate();
			Locale defaultLocale = new Locale(DesigeConf.getProperty("language.Default"), DesigeConf.getProperty("country.Default"));
			ResourceBundle rb = ResourceBundle.getBundle("LoginBundle", defaultLocale);
			if (timeExecution.length() > 15)
				timeExecution = timeExecution.substring(0,16);

			for (int row = 0; row < datos.size(); row++) {
				DataUserWorkFlowForm forma = (DataUserWorkFlowForm) datos.elementAt(row);
				String fechaExpiracion = forma.getDateExpire();
				String fechaExpiracionMail = "";
				if (forma.getDateExpire().length() > 15){
					fechaExpiracion = forma.getDateExpire().substring(0,16);
					String anio = fechaExpiracion.substring(0,4);
					String mes = fechaExpiracion.substring(5,7);
					String dia = fechaExpiracion.substring(8,10);
					fechaExpiracionMail = dia + "/" + mes + "/" + anio;
				}
				try {
					log.debug("DateExpire: " + fechaExpiracion);
					log.debug("timeExecution: " + timeExecution);
					////System.out.println("WF DateExpire: " + fechaExpiracion);
					////System.out.println("WF timeExecution: " + timeExecution);
				} catch (Exception e) {
				}

				if (timeExecution.compareTo(fechaExpiracion) >= 0) {
					try {
						log.debug("Send Notification of WorkFlow Expires in Document " + forma.getNameDocument());
					} catch (Exception e) {
					}
					StringBuffer msg = new StringBuffer(100);
					String msgWFExpires = HandlerParameters.PARAMETROS.getMsgWFExpires();
					String prefijo = forma.getPrefix();
					if (ToolsHTML.isEmptyOrNull(prefijo))
						prefijo = "";
					if (ToolsHTML.isEmptyOrNull(msgWFExpires)) {
						msg.append(rb.getString("mailWF.titleExp")).append(" ");
						msg.append(prefijo).append(forma.getNumber()).append("<br/>");
						msg.append(rb.getString("mailWF.nameDoc")).append(forma.getNameDocument()).append("<br/>");
						msg.append(rb.getString("mail.WFExp"));
					} else {
						msg.append(msgWFExpires).append("<br/>");
						msg.append(rb.getString("mailWF.docData")).append("<br/><strong>");
						msg.append(rb.getString("mailWF.exp.number")).append(":</strong> ").append(prefijo).append(forma.getNumber());
						msg.append("<br/><strong>");
						msg.append(rb.getString("mailWF.exp.name")).append(":</strong> ").append(forma.getNameDocument()).append("<br/>");
					}
					StringBuffer msgOwner = new StringBuffer();
					msgOwner.append(rb.getString("wf.newWFMessageExpired")).append("<br/><strong>");
					if(fechaExpiracionMail!=null && fechaExpiracionMail.length()>9)
						msgOwner.append(rb.getString("doc.dateExpire")).append(":</strong> ").append(fechaExpiracionMail).append("<br/><br/>");
					msgOwner.append(rb.getString("mailWF.docData")).append("<br/><strong>");
					msgOwner.append(rb.getString("mailWF.exp.number")).append(":</strong> ").append(prefijo).append(forma.getNumber());
					msgOwner.append("<br/><strong>");
					msgOwner.append(rb.getString("mailWF.exp.name")).append(":</strong> ").append(forma.getNameDocument()).append("<br/>");

					HandlerWorkFlows.changeStatuWF(time, forma, msg.toString(), HandlerWorkFlows.expires, HandlerWorkFlows.wfuExpired, 0, null, HandlerDocuments.lastExpireWF, HandlerParameters.PARAMETROS.getMailAccount(), rb.getString("mail.system"), rb.getString("mail.nameUser"), null, msgOwner.toString());
				}
			}
			
			//PROCESO PARA EXPIRACION DE FTP's
			if (ToolsHTML.showFTP()) {
				Vector datosFTP = (Vector) HandlerWorkFlows.getAllFlexWorkFlowsPendingsWithDate();
				if (timeExecution.length() > 15)
					timeExecution = timeExecution.substring(0, 16);
	
				for (int row = 0; row < datosFTP.size(); row++) {
					DataUserWorkFlowForm forma = (DataUserWorkFlowForm) datosFTP.elementAt(row);
					String fechaExpiracion = forma.getDateExpire();
					String fechaExpiracionMail = ""; 
					if (forma.getDateExpire().length() > 15){
						fechaExpiracion = forma.getDateExpire().substring(0, 16);
						String anio = fechaExpiracion.substring(0,4);
						String mes = fechaExpiracion.substring(5,7);
						String dia = fechaExpiracion.substring(8,10);
						fechaExpiracionMail = dia + "/" + mes + "/" + anio;
					}
					try {
						log.debug("FTP DateExpire: " + fechaExpiracion);
						log.debug("FTP timeExecution: " + timeExecution);
						////System.out.println("FTP DateExpire: " + fechaExpiracion);
						////System.out.println("FTP timeExecution: " + timeExecution);
					} catch (Exception e) {
					}
	
					if (timeExecution.compareTo(fechaExpiracion) >= 0) {
						try {
							log.debug("Send Notification of flexworkflow Expired at Document " + forma.getNameDocument());
							////System.out.println("Send Notification of flexworkflow Expired at Document " + forma.getNameDocument());
						} catch (Exception e) {
						}
						StringBuffer msg = new StringBuffer(100);
						String msgWFExpires = HandlerParameters.PARAMETROS.getMsgWFExpires();
						String prefijo = forma.getPrefix();
						if (ToolsHTML.isEmptyOrNull(prefijo))
							prefijo = "";
						if (ToolsHTML.isEmptyOrNull(msgWFExpires)) {
							msg.append(rb.getString("mailWF.titleExp")).append(" ");
							msg.append(prefijo).append(forma.getNumber()).append("<br/>");
							msg.append(rb.getString("mailWF.nameDoc")).append(forma.getNameDocument()).append("<br/>");
							msg.append(rb.getString("mail.WFExp"));
						} else {
							msg.append(msgWFExpires).append("<br/>");
							msg.append(rb.getString("mailWF.docData")).append("<br/><strong>");
							msg.append(rb.getString("mailWF.exp.number")).append(":</strong> ").append(prefijo).append(forma.getNumber());
							msg.append("<br/><strong>");
							msg.append(rb.getString("mailWF.exp.name")).append(":</strong> ").append(forma.getNameDocument()).append("<br/>");
						}
						StringBuffer msgOwner = new StringBuffer();
						msgOwner.append(rb.getString("wf.newWFMessageExpired")).append("<br/><strong>");
						if(fechaExpiracionMail!=null && fechaExpiracionMail.length()>9)
							msgOwner.append(rb.getString("doc.dateExpire")).append(":</strong> ").append(fechaExpiracionMail).append("<br/><br/>");
						msgOwner.append(rb.getString("mailWF.docData")).append("<br/><strong>");
						msgOwner.append(rb.getString("mailWF.exp.number")).append(":</strong> ").append(prefijo).append(forma.getNumber());
						msgOwner.append("<br/><strong>");
						msgOwner.append(rb.getString("mailWF.exp.name")).append(":</strong> ").append(forma.getNameDocument()).append("<br/>");

						HandlerWorkFlows.expiredFlexFlow(time, forma, msg.toString(), HandlerWorkFlows.expires, HandlerWorkFlows.wfuExpired, 0,null, HandlerDocuments.lastExpireWF,HandlerParameters.PARAMETROS.getMailAccount(), rb.getString("mail.system"), rb.getString("mail.nameUser"), null, msgOwner.toString());
					}
				}			
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			isRunning = false;
		}
	}

	public static void main(String[] args) {
		CheckConditions conditions = new CheckConditions();
		conditions.start();
	}

}
