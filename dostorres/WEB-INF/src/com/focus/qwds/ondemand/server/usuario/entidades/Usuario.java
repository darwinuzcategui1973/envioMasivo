package com.focus.qwds.ondemand.server.usuario.entidades;

import java.io.Serializable;

public class Usuario implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3030092335457579590L;

	private int id;
	private String nombreUsuario;
	private String nombres;
	private String Apellidos;
	private String email;
	private String clave;
	private String token;
	private int idNode;

	public String getApellidos() {
		return Apellidos;
	}

	public void setApellidos(String apellidos) {
		Apellidos = apellidos;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getNombres() {
		return nombres;
	}

	public void setNombres(String nombres) {
		this.nombres = nombres;
	}

	public String getNombreUsuario() {
		return nombreUsuario;
	}

	public void setNombreUsuario(String nombreUsuario) {
		this.nombreUsuario = nombreUsuario;
	}
	// node de documento borrador
	public int getIdNode() {
		return idNode;
	}

	public void setIdNode(int idNode) {
		this.idNode = idNode;
	}

	@Override
	public int hashCode() {
		final int PRIME = 31;
		int result = 1;
		result = PRIME * result + ((Apellidos == null) ? 0 : Apellidos.hashCode());
		result = PRIME * result + ((email == null) ? 0 : email.hashCode());
		result = PRIME * result + id;
		result = PRIME * result + ((nombreUsuario == null) ? 0 : nombreUsuario.hashCode());
		result = PRIME * result + ((nombres == null) ? 0 : nombres.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final Usuario other = (Usuario) obj;
		if (Apellidos == null) {
			if (other.Apellidos != null)
				return false;
		} else if (!Apellidos.equals(other.Apellidos))
			return false;
		if (email == null) {
			if (other.email != null)
				return false;
		} else if (!email.equals(other.email))
			return false;
		if (id != other.id)
			return false;
		if (idNode != other.idNode)
			return false;
		if (nombreUsuario == null) {
			if (other.nombreUsuario != null)
				return false;
		} else if (!nombreUsuario.equals(other.nombreUsuario))
			return false;
		if (nombres == null) {
			if (other.nombres != null)
				return false;
		} else if (!nombres.equals(other.nombres))
			return false;
		return true;
	}

	public String getClave() {
		return clave;
	}

	public void setClave(String clave) {
		this.clave = clave;
	}
	
	

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	/**
	 * toString methode: creates a String representation of the object
	 * 
	 * @return the String representation
	 * @author info.vancauwenberge.tostring plugin
	 * 
	 */
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("Usuario[");
		buffer.append("serialVersionUID = ").append(serialVersionUID);
		buffer.append(", id = ").append(id);
		buffer.append(", idNode = ").append(idNode);
		buffer.append(", nombreUsuario = ").append(nombreUsuario);
		buffer.append(", nombres = ").append(nombres);
		buffer.append(", Apellidos = ").append(Apellidos);
		buffer.append(", email = ").append(email);
		buffer.append(", clave = ").append(clave);
		buffer.append("]");
		return buffer.toString();
	}

}
