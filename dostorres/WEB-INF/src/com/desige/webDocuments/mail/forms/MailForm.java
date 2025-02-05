package com.desige.webDocuments.mail.forms;

import java.io.Serializable;

import com.desige.webDocuments.utils.beans.SuperActionForm;

/**
 * Title: MailForm.java <br/>
 * Copyright: (c) 2004 Focus Consulting C.A.<br/>
 * @author Ing. Nelson Crespo
 * @version WebDocuments v3.0
 *<br/>
 *	Changes:<br/>
 *<ul>
 *	<li> 2004-08-01 (NC) Creation </li>
 *</ul>
 */
public class MailForm extends SuperActionForm implements Serializable {
    private String from;
    private String nameFrom;
	private String to;
	private String cc;
	private String subject;
	private String mensaje;
    private String dateMail;
    private String idMessage;
    private String userName;
    private String nameTo;
    private String idMessageUser;
    private String names;
    private String idTo;
    private String idCC;
    private String copyTo;
    private boolean isNew;
    private String fileName;

    public MailForm(){}

    public MailForm(String from,String subject,String to,String message){
        setFrom(from);
        setSubject(subject);
        setTo(to);
        setMensaje(message);
    }

	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public String getTo() {
		return to;
	}

	public void setTo(String to) {
		this.to = to;
	}

	public String getCc() {
		return cc;
	}

	public void setCc(String cc) {
		this.cc = cc;
	}

	public String getMensaje() {
		return mensaje;
	}

	public void setMensaje(String mensaje) {
		this.mensaje = mensaje;
	}

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getNameFrom() {
        return nameFrom;
    }

    public void setNameFrom(String nameFrom) {
        this.nameFrom = nameFrom;
    }

    public String getDateMail() {
        return dateMail;
    }

    public void setDateMail(String dateMail) {
        this.dateMail = dateMail;
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

    public String getCopyTo() {
        return copyTo;
    }

    public void setCopyTo(String copyTo) {
        this.copyTo = copyTo;
    }

    public boolean isNew() {
        return isNew;
    }

    public void setNew(boolean aNew) {
        isNew = aNew;
    }
    
    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }    
}
