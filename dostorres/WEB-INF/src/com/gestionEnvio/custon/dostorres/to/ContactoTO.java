package com.gestionEnvio.custon.dostorres.to;

import com.desige.webDocuments.utils.ToolsHTML;
import com.gestionEnvio.custon.dostorres.interfaces.ObjetoDosTorresTO;

public class ContactoTO implements ObjetoDosTorresTO {

	private static final long serialVersionUID = 1L;
	
	private String id; //  id
	private String origen; // Codigo
	private String nombre; // Nombre
	private String codigo; // Codigo
	private String telefono1; // 
	private String telefono2; // 
	private String fax; // 
	private String celular; // 
	private String direccion; //
	private String email; //
	private String email2; //
	private String email3; //
	

	public String getId() {
		return id;
	}
	public int getIdInt() {
		return ToolsHTML.parseInt(id);
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getOrigen() {
		return origen;
	}
	public void setOrigen(String origen) {
		this.origen = origen;
	}
	public String getNombre() {
		return nombre;
	}
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
	public String getCodigo() {
		return codigo;
	}
	public void setCodigo(String codigo) {
		this.codigo = codigo;
	}
	public String getTelefono1() {
		return telefono1;
	}
	public void setTelefono1(String telefono1) {
		this.telefono1 = telefono1;
	}
	public String getTelefono2() {
		return telefono2;
	}
	public void setTelefono2(String telefono2) {
		this.telefono2 = telefono2;
	}
	public String getFax() {
		return fax;
	}
	public void setFax(String fax) {
		this.fax = fax;
	}
	public String getCelular() {
		return celular;
	}
	public void setCelular(String celular) {
		this.celular = celular;
	}
	public String getDireccion() {
		return direccion;
	}
	public void setDireccion(String direccion) {
		this.direccion = direccion;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getEmail2() {
		return email2;
	}
	public void setEmail2(String email2) {
		this.email2 = email2;
	}
	public String getEmail3() {
		return email3;
	}
	public void setEmail3(String email3) {
		this.email3 = email3;
	}
	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	@Override
	public String toString() {
		return "ContactoTO [id=" + id + ", origen=" + origen + ", nombre=" + nombre + ", codigo=" + codigo
				+ ", telefono1=" + telefono1 + ", telefono2=" + telefono2 + ", fax=" + fax + ", celular=" + celular
				+ ", direccion=" + direccion + ", email=" + email + ", email2=" + email2 + ", email3=" + email3 + "]";
	}
	
	
}