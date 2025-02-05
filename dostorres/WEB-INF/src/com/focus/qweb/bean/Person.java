package com.focus.qweb.bean;

import java.io.Serializable;

/**
 * 
 * Title: Person.java <br>
 * Copyright: (c) 2012 Focus Consulting C.A.<br/>
 * @author Ing. Felipe Rojas (FJR)
 * 
 * @version WebDocuments v4.5.2
 * <br>
 *     Changes:<br>
 * <ul>
 *     <li> 22/03/2012 (FJR) Creation </li>
 * <ul>
 */
public class Person implements Serializable {
	private int idPerson;
	private String nombre;
	private String apellido;
	private String email;
	
	public Person() {
		// TODO Auto-generated constructor stub
	}
	
	public Person(int idPerson, String nombre, String apellido,
			String email) {
		// TODO Auto-generated constructor stub
		this.idPerson = idPerson;
		this.nombre = nombre;
		this.apellido = apellido;
		this.email = email;
	}

	public int getIdPerson() {
		return idPerson;
	}

	public void setIdPerson(int idPerson) {
		this.idPerson = idPerson;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public String getApellido() {
		return apellido;
	}

	public void setApellido(String apellido) {
		this.apellido = apellido;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}
	
	@Override
	public String toString() {
		return "person [nombre=" + nombre + ", apellido=" + apellido + ", email=" + email + "]";
	}
}
