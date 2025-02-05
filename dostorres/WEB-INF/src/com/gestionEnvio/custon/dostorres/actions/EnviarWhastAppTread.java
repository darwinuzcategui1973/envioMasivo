package com.gestionEnvio.custon.dostorres.actions;

import java.sql.Connection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.desige.webDocuments.mail.actions.SendMailAction;
import com.desige.webDocuments.mail.actions.SendMailTread;
import com.desige.webDocuments.mail.forms.MailForm;
import com.desige.webDocuments.persistent.managers.HandlerWorkFlows;
import com.desige.webDocuments.utils.ApplicationExceptionChecked;
import com.gestionEnvio.custon.dostorres.forms.WhastAppForm;
import com.gestionEnvio.custon.dostorres.persistent.managers.HandlerDBRecibos;
import com.gestionEnvio.custon.dostorres.util.EnviarWhastApp;


public class EnviarWhastAppTread extends Thread {
	
	
	public static Logger log = LoggerFactory.getLogger(HandlerWorkFlows.class);
	public static String NOMBRE_HILO = "EnviarWhastAppTread:Envio de WhastApp";

    private WhastAppForm formaWhastApp;
	private Connection con = null;
	
	public EnviarWhastAppTread() {
		this.setName(EnviarWhastAppTread.NOMBRE_HILO.concat(String.valueOf((new java.util.Date()).getTime())));
	}
	
	 public EnviarWhastAppTread(WhastAppForm formaWhastApp) {

		this.setName(EnviarWhastAppTread.NOMBRE_HILO.concat(String.valueOf((new java.util.Date()).getTime())));
		this.formaWhastApp = formaWhastApp;
	}
	
	
	
	public void run() {
		try {
			System.out.println("************************************");
			System.out.println( formaWhastApp.getIdMessage());
			System.out.println(formaWhastApp.getTelefono());
			System.out.println("************************************");
			if (formaWhastApp != null && formaWhastApp.getMensaje()!=null)
				EnviarWhastAppAction.send( formaWhastApp);
		} catch (ApplicationExceptionChecked e) {
			log.debug("Exception");
			log.error(e.getMessage());
			e.printStackTrace();
		} catch (Exception e) {
			log.debug("Exception");
			log.error(e.getMessage());
			e.printStackTrace();
		}
	}

	public WhastAppForm getFormaWhastApp() {
		return formaWhastApp;
	}

	public void setFormaWhastApp(WhastAppForm formaWhastApp) {
		this.formaWhastApp = formaWhastApp;
	}

}
