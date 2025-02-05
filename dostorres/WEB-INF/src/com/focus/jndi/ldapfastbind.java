package com.focus.jndi;

/**
 * ldapfastbind.java
 * 
 * Sample JNDI application to use Active Directory LDAP_SERVER_FAST_BIND connection control
 * 
 */
 
import java.util.Hashtable;

import javax.naming.AuthenticationException;
import javax.naming.Context;
import javax.naming.NamingException;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import javax.naming.ldap.Control;
import javax.naming.ldap.InitialLdapContext;
import javax.naming.ldap.LdapContext;

import com.desige.webDocuments.utils.beans.Users;
 
class FastBindConnectionControl implements Control {
	/**
	 * 
	 */
	private static final long serialVersionUID = 8196641591477027085L;
	public byte[] getEncodedValue() {
        	return null;
	}
  	public String getID() {
		return "1.2.840.113556.1.4.1781";
	}
 	public boolean isCritical() {
		return true;
	}
}
 
public class ldapfastbind {
	public Hashtable env = null;
	public static LdapContext ctx = null;
	public static Control[] connCtls = null;
 
	/**
	 * 
	 * @param ldapurl
	 */
	public ldapfastbind(String ldapurl) {
		env = new Hashtable();
		env.put(Context.INITIAL_CONTEXT_FACTORY,"com.sun.jndi.ldap.LdapCtxFactory");
		env.put(Context.SECURITY_AUTHENTICATION,"simple");
		env.put(Context.PROVIDER_URL,ldapurl);
 
		connCtls = new Control[] {new FastBindConnectionControl()};
 
		//first time we initialize the context, no credentials are supplied
		//therefore it is an anonymous bind.		
 
		try {
			ctx = new InitialLdapContext(env,connCtls);
 
		}
		catch (NamingException e) {
			//System.out.println("Naming exception " + e);
		}
	}
	
	public static boolean Authenticate(String username, String password) {
		try {
			ctx.addToEnvironment(Context.SECURITY_PRINCIPAL,username);
			ctx.addToEnvironment(Context.SECURITY_CREDENTIALS,password);
			ctx.reconnect(connCtls);
			//System.out.println(username + " is authenticated");
			return true;
		} catch (AuthenticationException e) {
			e.printStackTrace();
			//System.out.println(username + " is not authenticated");
			return false;
		}
		catch (NamingException e) {
			//System.out.println(username + " is not authenticated");
			e.printStackTrace();
			return false;
		}
	}
	
	/**
	 * Metodo solo usado para debug de la funcionalidad de ActiveDirectory
	 * 
	 * @param username
	 * @param password
	 * @return
	 * @throws Exception
	 */
	public boolean AuthenticateV2(String username, String password) throws Exception{
		ctx.addToEnvironment(Context.SECURITY_PRINCIPAL, username);
		ctx.addToEnvironment(Context.SECURITY_CREDENTIALS, password);
		ctx.reconnect(connCtls);
		//System.out.println(username + " is authenticated");
		return true;
	}
	
	/**
	 * 
	 * @param username
	 * @param password
	 * @return
	 * @throws Exception
	 */
	public boolean AuthenticateV3(String username, String password) throws Exception{
		// The value of Context.SECURITY_PRINCIPAL must be the logon username with the domain name
		this.ctx.addToEnvironment(Context.SECURITY_PRINCIPAL, username);
		// The value of the Context.SECURITY_CREDENTIALS should be the user's password
		this.ctx.addToEnvironment(Context.SECURITY_CREDENTIALS, password);
		
		// Authenticate the logon user
		DirContext ctx = new InitialDirContext(this.ctx.getEnvironment());
		//ctx.search(", matchingAttributes)
		/**
		 * Once the above line was executed successfully, the user is said to be
		 * authenticated and the InitialDirContext object will be created.
		 */
		return true;
	}
	
	public void finito() {
		try {
			ctx.close();
			//System.out.println("Context is closed");
		}
		catch (NamingException e) {
			//System.out.println("Context close failure " + e);
		}
	}

	public static boolean Authenticate(Users username, String password) {
		try {
			ctx.addToEnvironment(Context.SECURITY_PRINCIPAL,username);
			ctx.addToEnvironment(Context.SECURITY_CREDENTIALS,password);
			ctx.reconnect(connCtls);
			//System.out.println(username + " is authenticated");
			return true;
		} catch (AuthenticationException e) {
			e.printStackTrace();
			//System.out.println(username + " is not authenticated");
			return false;
		}
		catch (NamingException e) {
			//System.out.println(username + " is not authenticated");
			e.printStackTrace();
			return false;
		}
	}
}
