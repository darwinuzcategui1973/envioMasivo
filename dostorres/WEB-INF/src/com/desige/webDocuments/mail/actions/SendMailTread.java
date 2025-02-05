package com.desige.webDocuments.mail.actions;

import java.sql.Connection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.desige.webDocuments.mail.forms.MailForm;
import com.desige.webDocuments.persistent.managers.HandlerWorkFlows;
import com.desige.webDocuments.utils.ApplicationExceptionChecked;
import com.desige.webDocuments.utils.mail.SendMail;


public class SendMailTread extends Thread {

	public static Logger log = LoggerFactory.getLogger(HandlerWorkFlows.class);
	public static String NOMBRE_HILO = "SendMailTread:Envio de Mail";
	

	private MailForm formaMail;
	private Connection con = null;

	public SendMailTread() {
		this.setName(SendMailTread.NOMBRE_HILO.concat(String.valueOf((new java.util.Date()).getTime())));
	}

	public SendMailTread(MailForm formaMail) {

		this.setName(SendMailTread.NOMBRE_HILO.concat(String.valueOf((new java.util.Date()).getTime())));
		this.formaMail = formaMail;
	}
	
	public SendMailTread(Connection con, MailForm formaMail) {
		this.setName(SendMailTread.NOMBRE_HILO.concat(String.valueOf((new java.util.Date()).getTime())));
		this.formaMail = formaMail;
		this.con = con;
	}
	

		public void run() {
			try {
				System.out.println("************************************");
				System.out.println(formaMail.getFrom());
				System.out.println(formaMail.getTo());
				System.out.println(formaMail.getSubject());
				System.out.println(formaMail.getMensaje());
				System.out.println(formaMail.getFileName());
				System.out.println("************************************");
				if (formaMail != null && formaMail.getMensaje()!=null)
					SendMailAction.send(con, formaMail);
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
	
		public MailForm getFormaMail() {
			return formaMail;
		}
	
		public void setFormaMail(MailForm formaMail) {
			this.formaMail = formaMail;
		}
	
	}
