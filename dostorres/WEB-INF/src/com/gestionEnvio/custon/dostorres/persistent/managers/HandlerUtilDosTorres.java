package com.gestionEnvio.custon.dostorres.persistent.managers;

import java.io.Serializable;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.desige.webDocuments.mail.actions.SendMailTread;
import com.desige.webDocuments.mail.forms.MailForm;
import com.desige.webDocuments.persistent.managers.HandlerBD;
import com.gestionEnvio.custon.dostorres.dao.ContactoDAO;
import com.gestionEnvio.custon.dostorres.to.ContactoTO;

public class HandlerUtilDosTorres extends HandlerBD implements Serializable {
	
	private static Logger logger = LoggerFactory.getLogger(HandlerBD.class);
	
	private static final long serialVersionUID = 6449214471594561469L;
	static Logger log = LoggerFactory.getLogger("[V1.0 ] " + HandlerUtilDosTorres.class.getName());
	
	public static void enviarEmailContactos(String titleMail, String user, String account, String email, String comments) {
		MailForm formaMail = new MailForm();
		formaMail.setFrom(account);
		formaMail.setNameFrom(user);
		formaMail.setTo(email);
		formaMail.setSubject(titleMail);
		formaMail.setMensaje(comments);
		formaMail.setFileName("/icons/email.png");
		try {
			// SendMailAction.send(formaMail);
			log.debug("Email enviado: " + formaMail.getTo());
			//logger.debug("Email enviado: " + formaMail.getTo());
			SendMailTread mail = new SendMailTread(formaMail);
			mail.start();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}


	
	public static Map<String,ContactoTO> listarContactoAlls() {
		Map<String,ContactoTO> lista = null;

		ContactoDAO oContactoDAO = new ContactoDAO();

		try {
			lista = oContactoDAO.listarContactoAlls();

		} catch (Exception e) {
			e.printStackTrace();
		}

		return lista;
	}



}// cierre de la clases

