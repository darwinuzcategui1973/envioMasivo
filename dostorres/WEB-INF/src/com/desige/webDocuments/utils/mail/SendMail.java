package com.desige.webDocuments.utils.mail;

import java.io.File;
import java.net.URI;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Properties;
import java.util.StringTokenizer;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.Address;
import javax.mail.Authenticator;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Part;
import javax.mail.PasswordAuthentication;
import javax.mail.SendFailedException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.desige.webDocuments.mail.actions.SendMailTread;
import com.desige.webDocuments.mail.forms.MailForm;
import com.desige.webDocuments.persistent.managers.HandlerMessages;
import com.desige.webDocuments.persistent.managers.HandlerParameters;
import com.desige.webDocuments.utils.ApplicationExceptionChecked;
import com.desige.webDocuments.utils.ToolsHTML;
import com.desige.webDocuments.utils.beans.Users;

import microsoft.exchange.webservices.data.EmailMessage;
import microsoft.exchange.webservices.data.ExchangeCredentials;
import microsoft.exchange.webservices.data.ExchangeService;
import microsoft.exchange.webservices.data.ExchangeVersion;
import microsoft.exchange.webservices.data.MessageBody;
import microsoft.exchange.webservices.data.WebCredentials;

/**
 * Created by Focus Consulting C.A.
 */
public class SendMail {
	private static Logger log = LoggerFactory.getLogger(SendMail.class.getName());
	private static String MAIL_PROTOCOL_SSL = "SSL";
	private static String MAIL_PROTOCOL_TLS = "TLS";
	private static String MAIL_PROTOCOL_NO_AUTH = "NO_AUTH";

	private String message;

	/**
	 * 
	 * @param smtpMail
	 * @return
	 */
	private static Session getSessionObjectToSendMail(String smtpMail) {
		Session session = null;

		try {
			Properties propiedades = System.getProperties();
			propiedades.put("mail.smtp.host", smtpMail);

			// obtenemos los parametros asociados al correo

			// final String mailAccount = HandlerBD.getField("email","person","nameuser","demodoce","=",HandlerBD.typeString);
			final String mailAccount = HandlerParameters.PARAMETROS.getMailAccount();
			final String mailAuthPassword = HandlerParameters.PARAMETROS.getPasswordEmail();
			final String mailAuthProtocol = HandlerParameters.PARAMETROS.getAuthProtocolEmail();
			final String mailSmtpPort = HandlerParameters.PARAMETROS.getPortEmail();
			final String mailEnableDebug = HandlerParameters.PARAMETROS.getDebugEmail();
			log.info("finals(78) son: " + mailAccount + " ; ");
			// vemos que configuracion es necesaria implementar por los valores anteriores
			if (!ToolsHTML.isEmptyOrNull(mailSmtpPort)) {
				propiedades.put("mail.smtp.port", mailSmtpPort);
				propiedades.put("mail.smtp.socketFactory.port", mailSmtpPort);
				log.info("Puerto de salida de correos es: " + mailSmtpPort);
				propiedades.put("mail.smtp.socketFactory.fallback", "true"); // Should be true
			}

			// obtenemos la informacion asociada al protocolo de envio
			if (MAIL_PROTOCOL_SSL.equals(mailAuthProtocol) || MAIL_PROTOCOL_TLS.equals(mailAuthProtocol) || !ToolsHTML.isEmptyOrNull(mailAuthPassword)) {
				propiedades.put("mail.smtp.auth", "true");
				if (MAIL_PROTOCOL_SSL.equals(mailAuthProtocol)) {
					propiedades.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
					propiedades.put("mail.smtp.socketFactory.fallback", "false");
				} else if (MAIL_PROTOCOL_TLS.equals(mailAuthProtocol)) {
					propiedades.put("mail.smtp.starttls.enable", "true");

					// ydavila Ticket 001-00-003231 Office 365
					// if(smtpMail.equals ("smtp.office365.com")){
					propiedades.put("mail.smtp.auth", "true");
					propiedades.put("mail.smtp.starttls.enable", "true");
					propiedades.put("mail.smtps.socketFactory.class", "javax.net.tls.SSLSocketFactory");
					propiedades.put("mail.smtp.allow8bitmime", "true");
					// }
				} else {
					log.info("El pwd no es vacio, entonces lo usamos como autenticacion PLAIN");
				}

				session = Session.getInstance(propiedades, new Authenticator() {
					protected PasswordAuthentication getPasswordAuthentication() {
						return new PasswordAuthentication(mailAccount, mailAuthPassword);
					};
				});
			} else {
				session = Session.getInstance(propiedades);
			}

			// vemos si se habilitaran las trazas de debug
			if ("1".equals(mailEnableDebug)) {
				session.setDebug(true);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return session;
	}

	/**
	 * 
	 * @return
	 */
	private static Session getSessionObjectToSendMailForGMail() {
		Session session = null;

		try {
			Properties propiedades = System.getProperties();
			propiedades.put("mail.smtp.host", "smtp.gmail.com");

			// obtenemos los parametros asociados al correo
			final String mailAccount = "soporte@focus.com.ve";
			final String mailAuthProtocol = "SSL";
			final String mailSmtpPort = "465";
			final String mailAuthPassword = "F0cus99";
			final String mailEnableDebug = "0";
			log.info("finals(147) son: " + mailAccount + " ; " + mailAuthPassword);
			// vemos que configuracion es necesaria implementar por los valores anteriores
			if (!ToolsHTML.isEmptyOrNull(mailSmtpPort)) {
				propiedades.put("mail.smtp.port", mailSmtpPort);
				propiedades.put("mail.smtp.socketFactory.port", mailSmtpPort);
				log.info("Puerto de salida de correos es: " + mailSmtpPort);
			}

			// obtenemos la informacion asociada al protocolo de envio
			if (MAIL_PROTOCOL_SSL.equals(mailAuthProtocol) || MAIL_PROTOCOL_TLS.equals(mailAuthProtocol)) {
				propiedades.put("mail.smtp.auth", "true");

				if (MAIL_PROTOCOL_SSL.equals(mailAuthProtocol)) {
					propiedades.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
					propiedades.put("mail.smtp.socketFactory.fallback", "false");
				} else {
					propiedades.put("mail.smtp.starttls.enable", "true");
					propiedades.put("mail.smtp.allow8bitmime", "true");
				}

				session = Session.getInstance(propiedades, new Authenticator() {
					protected PasswordAuthentication getPasswordAuthentication() {
						return new PasswordAuthentication(mailAccount, mailAuthPassword);
					};
				});
			} else {
				session = Session.getInstance(propiedades);
			}

			// vemos si se habilitaran las trazas de debug
			if ("1".equals(mailEnableDebug)) {
				session.setDebug(true);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return session;
	}

	private static synchronized Address[] getEmails(String values) throws AddressException {
		Address[] emails = null;
		int items = 0;
		if (!ToolsHTML.isEmptyOrNull(values)) {
			StringTokenizer st = new StringTokenizer(values, ";");
			items = st.countTokens();
			emails = new Address[items];
			int cont = 0;
			while (st.hasMoreTokens()) {
				String dir = st.nextToken();
				emails[cont] = new InternetAddress(dir);
				cont++;
			}
		}
		return emails;
	}

	/**
	 * 
	 * @param smtpMail
	 * @param from
	 * @param nameUser
	 * @param to
	 * @param cc
	 * @param asunto
	 * @param texto
	 * @return
	 * @throws Exception
	 */
	public static boolean sendMail(String smtpMail, String from, String nameUser, String to, String cc, String asunto, String texto) throws Exception {
		try {
			if (!ToolsHTML.isNotifyEmail()) {
				return false;
			}
			Session sesion = getSessionObjectToSendMail(smtpMail);
			MimeMessage mensaje = new MimeMessage(sesion);

			Address dirFrom = null;
			if (ToolsHTML.isEmptyOrNull(nameUser)) {
				dirFrom = new InternetAddress(from);
			} else {
				dirFrom = new InternetAddress(from, nameUser);
			}
			// ydavila Ticket 001-00-003231 Office 365
			if (smtpMail.equals("smtp.office365.com")) {
				dirFrom = new InternetAddress(HandlerParameters.PARAMETROS.getMailAccount());
				final String mailAuthPassword = HandlerParameters.PARAMETROS.getPasswordEmail();
			}
			log.info("dirForm (236) es: " + dirFrom);
			log.info("sesion (237) es: " + sesion);
			Address[] dirTo = getEmails(to);
			Address[] dirCC = getEmails(cc);
			mensaje.setFrom(dirFrom);
			mensaje.addRecipients(Message.RecipientType.TO, dirTo);
			if (dirCC != null) {
				mensaje.addRecipients(Message.RecipientType.CC, dirCC);
			}
			mensaje.setSubject(ToolsHTML.getSufijoMail(asunto));
			mensaje.setContent(texto, "text/html");
			// mensaje.setText(texto);
			// // Create the message part
			// BodyPart messageBodyPart = new MimeBodyPart();
			// // Part two is attachment
			// messageBodyPart = new MimeBodyPart();
			// DataSource source = new FileDataSource(filename);
			// messageBodyPart.setDataHandler(new DataHandler(source));
			// messageBodyPart.setFileName(filename);
			// multipart.addBodyPart(messageBodyPart);
			// // Put parts in message
			// message.setContent(multipart);

			Transport.send(mensaje);
			return true;
		} catch (javax.mail.internet.AddressException ae) {
			ae.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public static synchronized boolean sendMail(String smtpMail, MailForm forma) throws ApplicationExceptionChecked, Exception {
		
		
		
		return sendMail(null, smtpMail, forma);
	}

	public static synchronized boolean sendMail(Connection con, String smtpMail, MailForm forma) throws ApplicationExceptionChecked, Exception {
		boolean resp = false;
		try {
			if (!ToolsHTML.isNotifyEmail(con)) {
				return false;
			}

			Session sesion = getSessionObjectToSendMail(smtpMail);
			MimeMessage mensaje = new MimeMessage(sesion);

			Address dirFrom = null;
			if (ToolsHTML.isEmptyOrNull(forma.getNameFrom())) {
				dirFrom = new InternetAddress(forma.getFrom());
			} else {
				dirFrom = new InternetAddress(forma.getFrom(), forma.getNameFrom());
			}
			// ydavila Ticket 001-00-003231 Office 365
			if (smtpMail.equals("smtp.office365.com")) {
				dirFrom = new InternetAddress(HandlerParameters.PARAMETROS.getMailAccount());
				final String mailAuthPassword = HandlerParameters.PARAMETROS.getPasswordEmail();
			}
			log.info("dirForm (299) es: " + dirFrom);
			Address[] dirTo = getEmails(forma.getTo());
			Address[] dirCC = getEmails(forma.getCc());
			mensaje.setFrom(dirFrom);
			mensaje.addRecipients(Message.RecipientType.TO, dirTo);
			if (dirCC != null) {
				mensaje.addRecipients(Message.RecipientType.CC, dirCC);
			}
			forma.setSubject(ToolsHTML.getSufijoMail(forma.getSubject()));
			mensaje.setSubject(forma.getSubject());
			mensaje.setContent(forma.getMensaje(), "text/html");
			resp = false;
			if (mensaje != null) {
				Transport.send(mensaje); // Comentada para revisar si guarda los valores en BD
				resp = true;
			}
		} catch (javax.mail.internet.AddressException ae) {
			System.out.println("[AddressException]");
			ae.printStackTrace();
			throw new ApplicationExceptionChecked("E0003");
		} catch (SendFailedException se) {
			System.out.println("[SendFailedException]");
			se.printStackTrace();
			String directions = processDirection(se.getInvalidAddresses());
			if (directions != null) {
				throw new ApplicationExceptionChecked("E0005:" + directions);
			} else {
				throw new ApplicationExceptionChecked("E0005");
			}
		} catch (MessagingException me) {
			System.out.println("[MessagingException]");
			me.printStackTrace();
			throw new ApplicationExceptionChecked("E0004");
		} finally {
			HandlerMessages.insert(forma);
		}
		return resp;
	}

	/**
	 * 
	 * @param con
	 * @param smtpMail
	 * @param forma
	 * @return
	 * @throws ApplicationExceptionChecked
	 * @throws Exception
	 * 
	 *             Envia un correo con la cuenta jrivero@focus.com.ve, sin pedir datos de configuracion de la base de datos, sin dejar traza en los correos y
	 *             sin preguntar si esta activo o no la notificacion de correos.
	 */
	public static synchronized boolean sendMailToGMail(MailForm forma) throws ApplicationExceptionChecked, Exception {
		boolean resp = false;
		try {
			Session sesion = getSessionObjectToSendMailForGMail();
			MimeMessage mensaje = new MimeMessage(sesion);

			Address dirFrom = null;
			if (ToolsHTML.isEmptyOrNull(forma.getNameFrom())) {
				dirFrom = new InternetAddress(forma.getFrom());
			} else {
				dirFrom = new InternetAddress(forma.getFrom(), forma.getNameFrom());
			}
			/*
			 * //ydavila Ticket 001-00-003231 Office 365 if(smtpMail.equals ("smtp.office365.com")){ dirFrom =new
			 * InternetAddress(HandlerParameters.PARAMETROS.getMailaccount()); final String mailAuthPassword =
			 * HandlerParameters.PARAMETROS.setPasswordEmail(); }
			 */
			log.info("dirForm (364) es: " + dirFrom);
			log.info("sesion (365) es: " + sesion);
			Address[] dirTo = getEmails(forma.getTo());
			Address[] dirCC = getEmails(forma.getCc());
			mensaje.setFrom(dirFrom);
			mensaje.addRecipients(Message.RecipientType.TO, dirTo);
			if (dirCC != null) {
				mensaje.addRecipients(Message.RecipientType.CC, dirCC);
			}
			forma.setSubject(ToolsHTML.getSufijoMail(forma.getSubject()));
			mensaje.setSubject(forma.getSubject());
			mensaje.setContent(forma.getMensaje(), "text/html");
			resp = false;
			if (mensaje != null) {
				Transport.send(mensaje); // Comentada para revisar si guarda los valores en BD
				resp = true;
			}
		} catch (javax.mail.internet.AddressException ae) {
			System.out.println("[AddressException]");
			ae.printStackTrace();
			throw new ApplicationExceptionChecked("E0003");
		} catch (SendFailedException se) {
			System.out.println("[SendFailedException]");
			se.printStackTrace();
			String directions = processDirection(se.getInvalidAddresses());
			if (directions != null) {
				throw new ApplicationExceptionChecked("E0005:" + directions);
			} else {
				throw new ApplicationExceptionChecked("E0005");
			}
		} catch (MessagingException me) {
			System.out.println("[MessagingException]");
			me.printStackTrace();
			throw new ApplicationExceptionChecked("E0004");
		}
		return resp;
	}

	private static String processDirection(Address[] directions) {
		if (directions != null) {
			StringBuffer resp = new StringBuffer(200);
			for (int row = 0; row < directions.length; row++) {
				resp.append(directions[row]).append(",");
			}
			return resp.toString();
		}
		return null;
	}

	public static void send(ArrayList<String> emailsUsers, Users usuario, String titleMail, String message) {
		MailForm formaMail = new MailForm();
		StringBuilder usuarios = new StringBuilder("");
		for (int row = 0; row < emailsUsers.size(); row++) {
			usuarios.append(emailsUsers.get(row)).append(";");
		}
		if (usuarios.length() > 0) {
			usuarios.deleteCharAt(usuarios.length() - 1);
		}
		formaMail.setFrom(usuario.getEmail());
		formaMail.setNameFrom(usuario.getNamePerson());
		formaMail.setTo(usuarios.toString());
		formaMail.setSubject(titleMail);
		formaMail.setMensaje(message);
		try {
			log.debug("Sending..... mails.....");
			// SendMailAction.send(formaMail);
			SendMailTread mail = new SendMailTread(formaMail);
			mail.start();
		} catch (Exception ex) {
			log.error(ex.getMessage());
			ex.printStackTrace();
		}
	}

	public static synchronized boolean sendMailWithAttach(String smtpMail, MailForm forma) throws ApplicationExceptionChecked, Exception {
		boolean resp = false;
		try {
			if (!ToolsHTML.isNotifyEmail()) {
				return false;
			}

			log.debug("Mensaje: " + forma.getMensaje());
			Session sesion = getSessionObjectToSendMail(smtpMail);
			MimeMessage mensaje = new MimeMessage(sesion);
			  
			Address dirFrom = null;
			if (ToolsHTML.isEmptyOrNull(forma.getNameFrom())) {
				dirFrom = new InternetAddress(forma.getFrom());
			} else {
				dirFrom = new InternetAddress(forma.getFrom(), forma.getNameFrom());
			}
			// ydavila office 365
			dirFrom = new InternetAddress(HandlerParameters.PARAMETROS.getMailAccount());
			final String mailAuthPassword = HandlerParameters.PARAMETROS.getPasswordEmail();
			log.info("dirForm (456) es: " + dirFrom);
			log.info("sesion (457) es: " + sesion);
			Address[] dirTo = getEmails(forma.getTo());
			Address[] dirCC = getEmails(forma.getCc());
			mensaje.setFrom(dirFrom);
			mensaje.addRecipients(Message.RecipientType.TO, dirTo);
			if (dirCC != null) {
				mensaje.addRecipients(Message.RecipientType.CC, dirCC);
			}
			forma.setSubject(ToolsHTML.getSufijoMail(forma.getSubject()));
			mensaje.setSubject(forma.getSubject());

			if (!ToolsHTML.isEmptyOrNull(forma.getFileName())) {
				MimeMultipart multipart = addAttach(forma.getFileName(), forma.getMensaje());
				mensaje.setContent(multipart);
			} else {
				mensaje.setContent(forma.getMensaje(), "text/html");
			}

			resp = false;
			if (mensaje != null) {
				Transport.send(mensaje); // Comentada para revisar si guarda los valores en BD
				resp = true;
			}
		} catch (javax.mail.internet.AddressException ae) {
			System.out.println("[AddressException]");
			ae.printStackTrace();
			throw new ApplicationExceptionChecked("E0003");
		} catch (SendFailedException se) {
			System.out.println("[SendFailedException]");
			se.printStackTrace();
			String directions = processDirection(se.getInvalidAddresses());
			if (directions != null) {
				throw new ApplicationExceptionChecked("E0005:" + directions);
			} else {
				throw new ApplicationExceptionChecked("E0005");
			}
		} catch (MessagingException me) {
			System.out.println("[MessagingException]");
			me.printStackTrace();
			throw new ApplicationExceptionChecked("E0004");
		} finally {
			HandlerMessages.insert(forma);
		}
		return resp;
	}

	/**
	 * A�ade un attachement al mensaje de email
	 * 
	 * @param pathname
	 *            ruta del fichero
	 * @param text
	 *            contenido
	 * @throws Exception
	 *             excepcion levantada en caso de error
	 */
	public static MimeMultipart addAttach(String pathname, String text) throws Exception {
		MimeMultipart multipart = new MimeMultipart();

		if (!ToolsHTML.isEmptyOrNull(pathname)) {
			File file = new File(pathname);
			BodyPart messageBodyPart = new MimeBodyPart();
			DataSource ds = new FileDataSource(file);
			messageBodyPart.setDataHandler(new DataHandler(ds));
			messageBodyPart.setFileName(file.getName());
			messageBodyPart.setDisposition(Part.ATTACHMENT);
			multipart.addBodyPart(messageBodyPart);
		}

		BodyPart messageBodyPart2 = new MimeBodyPart();
		String htmlText = text;
		messageBodyPart2.setContent(htmlText, "text/html");
		multipart.addBodyPart(messageBodyPart2);

		return multipart;
	}

	public static void main2(String[] args) throws Exception {

		ExchangeService service = new ExchangeService(ExchangeVersion.Exchange2010_SP1);// not sure if it should be SP1 or SP2
		ExchangeCredentials credentials = new WebCredentials("demo-once@intercaps.com.ve", "Demo1105");
		service.setCredentials(credentials);

		try {
			System.out.println("Check");
			service.setUrl(new URI("https://outlook.office365.com/EWS/Exchange.asmx"));
			service.setTraceEnabled(true);
			EmailMessage msg = new EmailMessage(service);
			msg.setSubject("Hello world! desde qwebdocuments Yajaira");
			msg.setBody(MessageBody.getMessageBodyFromText("Sent using the EWS Managed API xxxxxxxxxxxxxxx."));
			msg.getToRecipients().add("Demo-Doce@intercaps.com.ve");
			msg.send();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {

		System.out.println("Start");
		final String username = "enviodostorres@gmail.com";
		final String password = "omlghpimwvesbvak";

		Properties props = new Properties();
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.host", "smtp.gmail.com");
		props.put("mail.smtp.port", "465");
		props.put("mail.transport.protocol", "smtp");
		props.put("mail.smtp.starttls.enable", "true");
		props.put("mail.smtp.starttls.enable", "true");
		props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");

		Session session = Session.getInstance(props, new javax.mail.Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(username, password);
			}
		});

		try {
			Transport transport = session.getTransport();
			Message message = new MimeMessage(session);
			message.setFrom(new InternetAddress("enviodostorres@gmail.com"));// formBean.getString("fromEmail")
			message.setRecipients(Message.RecipientType.TO, InternetAddress.parse("darwin.uzcategui1973@gmail.com"));
			message.setSubject("subject");// formBean.getString(
			message.setText("datos1");
			//message.setText("mailBody");
			message.setText("datos2");
			message.setText("datos3");
			String path = ToolsHTML.getPath();
	        String fileName = path.concat("reportes").concat(File.separator)+"06Record.xls";
	        //String fileName = path.concat("reportes").concat(File.separator).concat(usuario.getIdPerson()+"06Record.xls");

            //forma.setFileName(fileName);
			///message.setFileName("C:\\proyectoDostorres\\dosTorres\\dostorres\\icons\\male.png");
			message.setFileName(fileName);
			//message.
			transport.connect();
			transport.send(message, InternetAddress.parse("darwin.uzcategui1973@gmail.com"));// (message);

			System.out.println("Done");
			System.out.println(path);
			System.out.println(fileName);
			System.out.println(message.getFileName());

		} catch (MessagingException e) {
			System.out.println("e=" + e);
			e.printStackTrace();
			throw new RuntimeException(e);

		}
	}

}
