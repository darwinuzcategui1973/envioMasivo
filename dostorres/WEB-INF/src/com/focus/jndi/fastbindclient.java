package com.focus.jndi;

/**
 * fastbindclient.java
 * 
 * Sample JNDI application to use LDAP_SERVER_FAST_BIND connection control
 * 
 * This is just a test harness to invoke the ldapfastbind methods
 */

public class fastbindclient {
	public static void main(String[] args) {
		// Could also use ldaps over port 636 to protect the communication to the
		// Active Directory domain controller. Would also need to add
		// env.put(Context.SECURITY_PROTOCOL,"ssl") to the "server" code
		// String ldapurl = "ldap://servidor:389";
		// String ldapurl = "ldap://mydc.antipodes.com:389";
		String ldapurl = "ldap://focusenlinea.myvnc.com:3268"; // "ldap://ccsfocus01:389";
		String dominio = "focus";
		boolean IsAuthenticated = false;
		ldapfastbind ctx = new ldapfastbind(ldapurl);
		// IsAuthenticated = ctx.Authenticate("dominio\\usuario","clave");
		/*
		IsAuthenticated = ctx.Authenticate("Focus.local\\jrivero01", "F0cus41");
		IsAuthenticated = ctx.Authenticate("Focus\\jrivero", "F0cusvision91");
		IsAuthenticated = ctx.Authenticate("Focus\\jrivero", "F0cus41");
		IsAuthenticated = ctx.Authenticate("Focus\\drodriguez", "F0cus45");
		IsAuthenticated = ctx.Authenticate("Focus\\ggamez", "F0cus12");
		IsAuthenticated = ctx.Authenticate("Focus\\jpalomino", "F0cus34");
		IsAuthenticated = ctx.Authenticate("Focus\\sa", "sasa");
		IsAuthenticated = ctx.Authenticate("Focus.local\\jpalomino", "F0cus34");
		IsAuthenticated = ctx.Authenticate("Focus.local\\jrivero", "F0cus41");
		IsAuthenticated = ctx.Authenticate("Focus\\administrador", "F0cusvision91");
		*/
		//IsAuthenticated = ctx.Authenticate("focus\\admin", "123456");
		IsAuthenticated = ctx.Authenticate("focus\\jpalominio", "123456");

		// IsAuthenticated = ctx.Authenticate("ANTIPODES\\alberte","GoodPassword");
		// IsAuthenticated = ctx.Authenticate("ANTIPODES\\alberte","BadPassword");
		// IsAuthenticated = ctx.Authenticate("ANTIPODES\\charlesd","GoodPassword");
		// IsAuthenticated = ctx.Authenticate("ANTIPODES\\charlesd","BadPassword");
		// IsAuthenticated = ctx.Authenticate("ANTIPODES\\isaacn","GoodPassword");
		// IsAuthenticated = ctx.Authenticate("ANTIPODES\\isaacn","BadPassword");
		ctx.finito();

	}
}

