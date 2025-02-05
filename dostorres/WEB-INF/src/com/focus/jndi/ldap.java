package com.focus.jndi;

import java.util.Hashtable;

import javax.naming.Context;
import javax.naming.NamingException;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;

public class ldap {
	private String INITCTX = "com.sun.jndi.ldap.LdapCtxFactory";
	//private String MY_HOST = "ldap://ccsfocus01:389";
	private String MY_HOST = "ldap://focus-dc1:389";

	public ldap() {
	}

	public String Authenticate(String domain, String user, String pass) {
		Hashtable env = new Hashtable();
		if (pass.compareTo("") == 0 || user.compareTo("") == 0)
			return null;
		env.put(Context.INITIAL_CONTEXT_FACTORY, INITCTX);
		env.put(Context.PROVIDER_URL, MY_HOST);
		env.put(Context.SECURITY_AUTHENTICATION, "simple");
		env.put(Context.SECURITY_PRINCIPAL, new String(domain + "\\" + user));
		env.put(Context.SECURITY_CREDENTIALS, new String(pass));
		try {
			DirContext ctx = new InitialDirContext(env);
		} catch (NamingException e) {
			e.printStackTrace();
			return null;
		}

		return user;
	}

	public static void main(String[] argv) {
		ldap Aut = new ldap();
		if (Aut.Authenticate("Focus", "jrivero", "F0cus41") != null) {
			//System.out.println("Autenticado");
		} else {
			//System.out.println("No Auntenticado");
		}

	}
}
