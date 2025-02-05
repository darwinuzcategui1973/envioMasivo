package com.focus.jndi;

/**
 * 
 * @author frojas
 *
 */
public class TestLoginActiveDirectory {
	private String ldapurl;
	private String dominio;
	private String usuario;
	private String clave;
	
	public TestLoginActiveDirectory(String ldapurl, String dominio,
			String usuario, String clave) {
		// TODO Auto-generated constructor stub
		this.ldapurl = ldapurl;
		this.dominio = dominio;
		this.usuario = usuario;
		this.clave = clave;
	}
	
	/**
	 * 
	 * @return
	 */
	public String executeConnnection() {
		String resultado = "";
		
		try {
			StringBuffer cadena = new StringBuffer(dominio).append("\\").append(usuario);
			boolean IsAuthenticated = false;
			ldapfastbind ctx = new ldapfastbind(ldapurl);
			
			resultado += "<br />Intentando conectarse a " + ldapurl + ", " + cadena;
			
			IsAuthenticated = ctx.AuthenticateV3(cadena.toString(), clave);
			if(!IsAuthenticated){
				resultado += "<br/>No se logro validar en ActiveDirectory a " + cadena;
			} else {
				resultado += "<br/>El usuario " + cadena + ", fue validado contra ActiveDirectory";
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			resultado += "<br />Error: " + e.getLocalizedMessage();
			/*
			resultado += "<br />[0]: " + e.getStackTrace()[0];
			resultado += "<br />[1]: " + e.getStackTrace()[1];
			resultado += "<br />[2]: " + e.getStackTrace()[2];
			resultado += "<br />[3]: " + e.getStackTrace()[3];
			*/
		}
		
		return resultado;
	}
}
