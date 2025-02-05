package com.focus.jndi;

import java.util.Hashtable;

import javax.naming.AuthenticationException;
import javax.naming.Context;
import javax.naming.Name;
import javax.naming.NamingException;
import javax.naming.directory.BasicAttribute;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import javax.naming.directory.ModificationItem;
import javax.naming.ldap.InitialLdapContext;
import javax.naming.ldap.LdapContext;

import com.sun.jndi.ldap.LdapCtx;

public class AutenticazioneActiveDirectory {

	/**
	 * Effettua la validazione delle credenziali di accesso di un utente tramite
	 * un server active directory. In caso di credenziali valide l'accesso
	 * all'applicazione viene eseguito tramite i servizi di
	 * jtech.httpUtil.JtSessionManager usati anche dalla validazione locale. I
	 * dati per la connessione al server a.d. vengono definiti tramite parametri
	 * dell'applicazione valorizzati in web.xml
	 * 
	 * @param user
	 *            String userid dell'utente
	 * @param password
	 *            String password associata all'utente
	 * @return boolean true se le credenziali sono valide, false altrimenti.
	 * @throws javax.naming.NamingException
	 */
	public boolean autenticaUtenteAD(String user, String password) throws Exception {
		boolean toReturn = false;

		try {
			Hashtable serverEnvironment = new Hashtable();
			String loginName = user + "@" + getDominioAD();
			//String keystore = "/opt/jdk1.5.0_14/jre/lib/security/cacerts";
			//System.setProperty("javax.net.ssl.trustStore", keystore);
			serverEnvironment.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
			serverEnvironment.put(Context.PROVIDER_URL, getServerAD());
			serverEnvironment.put(Context.SECURITY_AUTHENTICATION, "simple");
			serverEnvironment.put(Context.SECURITY_PRINCIPAL, loginName);
			serverEnvironment.put(Context.SECURITY_CREDENTIALS, password);
			//serverEnvironment.put(Context.SECURITY_PROTOCOL, "ssl");
			new InitialDirContext(serverEnvironment);
			toReturn = true;

		} catch (AuthenticationException ae) {
			if (ae.getMessage().contains("comment: AcceptSecurityContext error, data 773,"))
				throw new Exception("Password scaduta");
			else
				throw ae;
		}

		toReturn = true;

		return toReturn;
	}

	public boolean cambioPasswordAD(String user, String oldPassword, String newPassword) throws Exception {
		boolean toReturn = false;

		Hashtable serverEnvironment = new Hashtable();
		String loginName = user + "@" + getDominioAD();
		//String keystore = "/opt/jdk1.5.0_14/jre/lib/security/cacerts";
		//System.setProperty("javax.net.ssl.trustStore", keystore);

		serverEnvironment.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
		serverEnvironment.put(Context.PROVIDER_URL, getServerAD());
		serverEnvironment.put(Context.SECURITY_AUTHENTICATION, "simple");
		serverEnvironment.put(Context.SECURITY_PRINCIPAL, loginName);
		serverEnvironment.put(Context.SECURITY_CREDENTIALS, oldPassword);
		//serverEnvironment.put(Context.SECURITY_PROTOCOL, "ssl");
		LdapContext ldapContext = new InitialLdapContext(serverEnvironment, null);
		if (ldapContext != null) {
			// codifica la password secondo le specifiche di A.D.
			String oldPasswordQuoted = "\"" + oldPassword + "\"";
			byte[] oldUnicodePassword = oldPasswordQuoted.getBytes("UTF-16LE");
			String passwordQuoted = "\"" + newPassword + "\"";
			byte[] newUnicodePassword = passwordQuoted.getBytes("UTF-16LE");
			ModificationItem[] mods = new ModificationItem[2];
			mods[0] = new ModificationItem(DirContext.REMOVE_ATTRIBUTE, new BasicAttribute("unicodePwd", oldUnicodePassword));
			mods[1] = new ModificationItem(DirContext.ADD_ATTRIBUTE, new BasicAttribute("unicodePwd", newUnicodePassword));

			ldapContext.modifyAttributes(loginName, mods);
			try {
				ldapContext.close();
			} catch (NamingException ne) {
			}
			toReturn = true; // se fallisce eleva eccezione prima di arrivare
								// qua
		}

		return toReturn;
	}

	private String getServerAD() {

		//return "ldaps://10.0.1.179:636";// ApplicationServices.getInstance().getProperty(ApplicationServices.SERVER_AD);
		return "ldap://ccsfocus01:389";// ApplicationServices.getInstance().getProperty(ApplicationServices.SERVER_AD);
	}

	private String getDominioAD() {

		return "focus";// ApplicationServices.getInstance().getProperty(ApplicationServices.DOMAIN_AD);
	}

	public static void main(String[] args) {
		try {
			//System.out.println("--> inizio <--");
			AutenticazioneActiveDirectory aad = new AutenticazioneActiveDirectory();
			if (aad.autenticaUtenteAD("jrivero", "F0cus41")) {
				//System.out.println("dentro");
			} else {
				//System.out.println("fuera");
			}
			//System.out.println("provo a cambiare");
			aad.cambioPasswordAD("jrivero", "F0cus41", "F0cus4141");
		} catch (Throwable t) {
			t.printStackTrace();
		} finally {
			//System.out.println("--> finito <--");
		}
	}
}
