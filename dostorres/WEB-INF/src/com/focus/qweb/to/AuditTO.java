package com.focus.qweb.to;

import java.util.StringTokenizer;

import javax.servlet.http.HttpServletRequest;

import com.focus.qweb.interfaces.ObjetoTO;

public class AuditTO implements ObjetoTO {

	private static final long serialVersionUID = 1L;

	private String idAudit;
	private String idTransaction;
	private String fecha;
	private String detalle;
	private String idUsuario;
	private String valorInicial;
	private String valorFinal;
	private String terminal;

	public AuditTO() {
		
	}

	public String getIdAudit() {
		return idAudit;
	}

	public void setIdAudit(String idAudit) {
		this.idAudit = idAudit;
	}

	public String getIdTransaction() {
		return idTransaction;
	}

	public void setIdTransaction(String idTransaction) {
		this.idTransaction = idTransaction;
	}

	public String getFecha() {
		return fecha;
	}

	public void setFecha(String fecha) {
		this.fecha = fecha;
	}

	public String getDetalle() {
		return detalle;
	}

	public void setDetalle(String detalle) {
		this.detalle = detalle;
	}

	public String getIdUsuario() {
		return idUsuario;
	}

	public void setIdUsuario(String idUsuario) {
		this.idUsuario = idUsuario;
	}

	public String getValorInicial() {
		return valorInicial;
	}

	public void setValorInicial(String valorInicial) {
		this.valorInicial = valorInicial;
	}

	public String getValorFinal() {
		return valorFinal;
	}

	public void setValorFinal(String valorFinal) {
		this.valorFinal = valorFinal;
	}

	public String getTerminal() {
		return terminal;
	}

	public void setTerminal(String terminal) {
		this.terminal = terminal;
	}

	public static String getClientIpAddress(HttpServletRequest request) {
	    String xForwardedForHeader = request.getHeader("X-Forwarded-For");
	    if (xForwardedForHeader == null) {
	        return request.getRemoteAddr();
	    } else {
	        // As of https://en.wikipedia.org/wiki/X-Forwarded-For
	        // The general format of the field is: X-Forwarded-For: client, proxy1, proxy2 ...
	        // we only want the client
	        return new StringTokenizer(xForwardedForHeader, ",").nextToken().trim();
	    }
	}	
}
