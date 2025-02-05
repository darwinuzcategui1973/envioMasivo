package com.focus.custom.delsur.dto;

import java.util.Calendar;
import java.util.List;

public class CreditoDTO {
	private int codigoRegion;
	private String nombreRegion;
	private int codigoAgencia;
	private String nombreAgencia;
	private int codigoProducto;
	private String nombreProducto;
	private int codigoModalidadProducto;
	private String nombreModalidadProducto;
	private int numeroSolicitud;
	private String codigoUsuarioAbanks;
	private int codigoTipoBanca;
	private String nombreTipoBanca;
	private String numeroIdentificacionCliente;
	private int codigoCliente;
	private String nombreSolicitante;
	private String apellidoSolicitante;
	private String codigoGerenteAbanks;
	private Calendar fechaSolicitud;
	
	private List<RecaudoCreditoDTO> listadoRecaudos;
	
	public CreditoDTO() {
		// TODO Auto-generated constructor stub
	}

	public int getCodigoRegion() {
		return codigoRegion;
	}

	public void setCodigoRegion(int codigoRegion) {
		this.codigoRegion = codigoRegion;
	}

	public String getNombreRegion() {
		return nombreRegion;
	}

	public void setNombreRegion(String nombreRegion) {
		this.nombreRegion = nombreRegion;
	}

	public int getCodigoAgencia() {
		return codigoAgencia;
	}

	public void setCodigoAgencia(int codigoAgencia) {
		this.codigoAgencia = codigoAgencia;
	}

	public String getNombreAgencia() {
		return nombreAgencia;
	}

	public void setNombreAgencia(String nombreAgencia) {
		this.nombreAgencia = nombreAgencia;
	}

	public int getCodigoProducto() {
		return codigoProducto;
	}

	public void setCodigoProducto(int codigoProducto) {
		this.codigoProducto = codigoProducto;
	}

	public String getNombreProducto() {
		return nombreProducto;
	}

	public void setNombreProducto(String nombreProducto) {
		this.nombreProducto = nombreProducto;
	}

	public int getCodigoModalidadProducto() {
		return codigoModalidadProducto;
	}

	public void setCodigoModalidadProducto(int codigoModalidadProducto) {
		this.codigoModalidadProducto = codigoModalidadProducto;
	}

	public String getNombreModalidadProducto() {
		return nombreModalidadProducto;
	}

	public void setNombreModalidadProducto(String nombreModalidadProducto) {
		this.nombreModalidadProducto = nombreModalidadProducto;
	}

	public int getNumeroSolicitud() {
		return numeroSolicitud;
	}

	public void setNumeroSolicitud(int numeroSolicitud) {
		this.numeroSolicitud = numeroSolicitud;
	}
	
	public String getCodigoUsuarioAbanks() {
		return codigoUsuarioAbanks;
	}
	
	public void setCodigoUsuarioAbanks(String codigoUsuarioAbanks) {
		this.codigoUsuarioAbanks = codigoUsuarioAbanks;
	}
	
	public int getCodigoTipoBanca() {
		return codigoTipoBanca;
	}

	public void setCodigoTipoBanca(int codigoTipoBanca) {
		this.codigoTipoBanca = codigoTipoBanca;
	}

	public String getNombreTipoBanca() {
		return nombreTipoBanca;
	}

	public void setNombreTipoBanca(String nombreTipoBanca) {
		this.nombreTipoBanca = nombreTipoBanca;
	}

	public String getNumeroIdentificacionCliente() {
		return numeroIdentificacionCliente;
	}

	public void setNumeroIdentificacionCliente(String numeroIdentificacionCliente) {
		this.numeroIdentificacionCliente = numeroIdentificacionCliente;
	}
	
	public int getCodigoCliente() {
		return codigoCliente;
	}
	
	public void setCodigoCliente(int codigoCliente) {
		this.codigoCliente = codigoCliente;
	}
	
	public String getNombreSolicitante() {
		return nombreSolicitante;
	}
	
	public void setNombreSolicitante(String nombreSolicitante) {
		this.nombreSolicitante = nombreSolicitante;
	}
	
	public String getApellidoSolicitante() {
		return apellidoSolicitante;
	}
	
	public void setApellidoSolicitante(String apellidoSolicitante) {
		this.apellidoSolicitante = apellidoSolicitante;
	}
	
	public String getCodigoGerenteAbanks() {
		return codigoGerenteAbanks;
	}
	
	public void setCodigoGerenteAbanks(String codigoGerenteAbanks) {
		this.codigoGerenteAbanks = codigoGerenteAbanks;
	}
	
	public Calendar getFechaSolicitud() {
		return fechaSolicitud;
	}

	public void setFechaSolicitud(Calendar fechaSolicitud) {
		this.fechaSolicitud = fechaSolicitud;
	}

	public List<RecaudoCreditoDTO> getListadoRecaudos() {
		return listadoRecaudos;
	}

	public void setListadoRecaudos(List<RecaudoCreditoDTO> listadoRecaudos) {
		this.listadoRecaudos = listadoRecaudos;
	}

	@Override
	public String toString() {
		return "CreditoDTO [codigoRegion=" + codigoRegion + ", nombreRegion="
				+ nombreRegion + ", codigoAgencia=" + codigoAgencia
				+ ", nombreAgencia=" + nombreAgencia + ", codigoProducto="
				+ codigoProducto + ", nombreProducto=" + nombreProducto
				+ ", codigoModalidadProducto=" + codigoModalidadProducto
				+ ", nombreModalidadProducto=" + nombreModalidadProducto
				+ ", numeroSolicitud=" + numeroSolicitud
				+ ", codigoUsuarioAbanks=" + codigoUsuarioAbanks
				+ ", codigoTipoBanca=" + codigoTipoBanca + ", nombreTipoBanca="
				+ nombreTipoBanca + ", numeroIdentificacionCliente="
				+ numeroIdentificacionCliente + ", codigoCliente="
				+ codigoCliente + ", nombreSolicitante=" + nombreSolicitante
				+ ", apellidoSolicitante=" + apellidoSolicitante
				+ ", codigoGerenteAbanks=" + codigoGerenteAbanks
				+ ", fechaSolicitud=" + fechaSolicitud.getTime() + ", listadoRecaudos="
				+ listadoRecaudos + "]";
	}	
}
