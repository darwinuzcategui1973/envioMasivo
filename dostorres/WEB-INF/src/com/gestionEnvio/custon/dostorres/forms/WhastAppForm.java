package com.gestionEnvio.custon.dostorres.forms;

import java.io.Serializable;

import com.desige.webDocuments.utils.beans.SuperActionForm;

public class WhastAppForm extends SuperActionForm implements Serializable {
	
	  	private String from;
	    private String nameFrom;
		private String  telefono;//to;
		private String  telefonocc;
		private String subject;
		private String mensaje;
	    private String dateWhastApp;
	    private String idMessage;
	    private String userName;
	    private String nameTo;
	    private String idMessageUser;
	    private String names;
	    private String idTo;
	    private String idCC;
	    //private String copyTo;
	    //private boolean isNew;
	   // private String fileName;
	    
	    public WhastAppForm(){}
	    
	    public WhastAppForm(String from,String subject,String telefo,String mensaje){
	        setFrom(from);
	        setSubject(subject);
	        setTelefono(telefono);
	        setMensaje(mensaje);
	    }


		public String getFrom() {
			return from;
		}

		public void setFrom(String from) {
			this.from = from;
		}

		public String getNameFrom() {
			return nameFrom;
		}

		public void setNameFrom(String nameFrom) {
			this.nameFrom = nameFrom;
		}

		public String getTelefono() {
			return telefono;
		}

		public void setTelefono(String telefono) {
			this.telefono = telefono;
		}

		public String getTelefonocc() {
			return telefonocc;
		}

		public void setTelefonocc(String telefonocc) {
			this.telefonocc = telefonocc;
		}

		public String getSubject() {
			return subject;
		}

		public void setSubject(String subject) {
			this.subject = subject;
		}

		public String getMensaje() {
			return mensaje;
		}

		public void setMensaje(String mensaje) {
			this.mensaje = mensaje;
		}

		public String getDateWhastApp() {
			return dateWhastApp;
		}

		public void setDateWhastApp(String dateWhastApp) {
			this.dateWhastApp = dateWhastApp;
		}

		public String getIdMessage() {
			return idMessage;
		}

		public void setIdMessage(String idMessage) {
			this.idMessage = idMessage;
		}

		public String getUserName() {
			return userName;
		}

		public void setUserName(String userName) {
			this.userName = userName;
		}

		public String getNameTo() {
			return nameTo;
		}

		public void setNameTo(String nameTo) {
			this.nameTo = nameTo;
		}

		public String getIdMessageUser() {
			return idMessageUser;
		}

		public void setIdMessageUser(String idMessageUser) {
			this.idMessageUser = idMessageUser;
		}

		public String getNames() {
			return names;
		}

		public void setNames(String names) {
			this.names = names;
		}

		public String getIdTo() {
			return idTo;
		}

		public void setIdTo(String idTo) {
			this.idTo = idTo;
		}

		public String getIdCC() {
			return idCC;
		}

		public void setIdCC(String idCC) {
			this.idCC = idCC;
		}
	     

	
}
