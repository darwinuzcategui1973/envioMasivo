package com.focus.jndi;

import java.util.Hashtable;

import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;

public class RecordatorioBean {

	private String pregunta;
	private String respuesta;
	private String email;
	private String contrasena;

	public static void main(String[] args) {
		RecordatorioBean r = new RecordatorioBean();
		//LDAP encripta con el algoritmo SHA-1
		String domain = "ldap://ccsfocus01:389";
		String user = "Focus\\administrador";
		String pass = "F0cusvision91";
		String inictx = "com.sun.jndi.ldap.LdapCtxFactory";
		String login = "jrivero";
		String security = "simple";
		String base	= "uid=jrivero,o=usuarios";	
		//String base	= "uid=jrivero,o=usuarios, dc=orizom,dc=com";	
		r.getObtenerUsuario(domain, user, pass, inictx, login, security, base);
	}

	public RecordatorioBean getObtenerUsuario(String domain, String user, String pass, String inictx, String login, String security, String base) {

		Hashtable<String,String> env = new Hashtable<String,String>();

		RecordatorioBean recordatorio = null;

		if (pass.compareTo("") == 0 || user.compareTo("") == 0)

			return null;

		env.put(Context.INITIAL_CONTEXT_FACTORY, inictx);
		env.put(Context.PROVIDER_URL, domain);
		env.put(Context.SECURITY_AUTHENTICATION, security);
		env.put(Context.SECURITY_PRINCIPAL, new String(user));
		env.put(Context.SECURITY_CREDENTIALS, new String(pass));

		try {

			DirContext ctx = new InitialDirContext(env);
			//User/ObjectClass=inetorgperson
			String filter = "(&(uid=" + login + ")(objectclass=Person))";
			//System.out.println(filter);
			SearchControls constraints = new SearchControls();
			constraints.setSearchScope(SearchControls.SUBTREE_SCOPE);
			NamingEnumeration results = ctx.search(base, filter, constraints);
			SearchResult sr = null;
			Attributes att = null;

			if (results.hasMore()) {

				sr = (SearchResult) results.next();
				att = (Attributes) sr.getAttributes();

				NamingEnumeration ne = null;

				recordatorio = new RecordatorioBean();
				recordatorio.setPregunta((String) this.atributo("PIFPregunta", att, ne));
				recordatorio.setRespuesta((String) this.atributo("PIFRespuesta", att, ne));
				recordatorio.setEmail((String) this.atributo("mail", att, ne));
				recordatorio.setContrasena(this.atributo("userPass word", att, ne).toString());

				//System.out.println("String givenname: " + this.atributo("givenname", att, ne));
				//System.out.println("String uid: " + this.atributo("uid", att, ne));
				//System.out.println("String ibm-primaryEmail: " + this.atributo("mail", att, ne));
				//System.out.println("String PIFPregunta: " + this.atributo("PIFPregunta", att, ne));
				//System.out.println("String PIFRespuesta: " + this.atributo("PIFRespuesta", att, ne));
				//System.out.println("String userPassword: " + this.atributo("userPassword", att, ne));
				ctx.close();
			}
		} catch (NamingException e) {
			e.printStackTrace();
			return null;
		} catch (Exception ex) {
			//System.out.println("Error en la búsqueda: " + ex.getMessage());
		}
		return recordatorio;
	}

	public Object atributo(String campo, Attributes att, NamingEnumeration ne) throws NamingException {
		if (att.get(campo) != null) {
			ne = att.get(campo).getAll();
			while (ne.hasMore()) {
				return ne.next();
			}
		}
		return "";
	}
	

	public String getContrasena() {
		return contrasena;
	}

	public void setContrasena(String contrasena) {
		this.contrasena = contrasena;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPregunta() {
		return pregunta;
	}

	public void setPregunta(String pregunta) {
		this.pregunta = pregunta;
	}

	public String getRespuesta() {
		return respuesta;
	}

	public void setRespuesta(String respuesta) {
		this.respuesta = respuesta;
	}
	
	
}
