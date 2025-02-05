package com.gestionEnvio.custon.dostorres.bean;

import java.util.List;

public class PdfReciboRequest {
	private String avisoId;
	private String fechaEmision;
	private String fechaVencimiento;
	private String periodo;
	private String alicuota;
	
	private String nombre;
	private String direccion;
	private String codigo;
	private String rif;
	private String local;
	private String titulo;
	private List<PdfReciboDetalleRequest> listGastos;
	
	
	public String getAvisoId() {
		return avisoId;
	}
	public void setAvisoId(String avisoId) {
		this.avisoId = avisoId;
	}
	public String getFechaEmision() {
		return fechaEmision;
	}
	public void setFechaEmision(String fechaEmision) {
		this.fechaEmision = fechaEmision;
	}
	public String getFechaVencimiento() {
		return fechaVencimiento;
	}
	public void setFechaVencimiento(String fechaVencimiento) {
		this.fechaVencimiento = fechaVencimiento;
	}
	public String getPeriodo() {
		return periodo;
	}
	public void setPeriodo(String periodo) {
		this.periodo = periodo;
	}
	public String getAlicuota() {
		return alicuota;
	}
	public void setAlicuota(String alicuota) {
		this.alicuota = alicuota;
	}
	public String getNombre() {
		return nombre;
	}
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
	public String getDireccion() {
		return direccion;
	}
	public void setDireccion(String direccion) {
		this.direccion = direccion;
	}
	public String getCodigo() {
		return codigo;
	}
	public void setCodigo(String codigo) {
		this.codigo = codigo;
	}
	public String getRif() {
		return rif;
	}
	public void setRif(String rif) {
		this.rif = rif;
	}
	public String getLocal() {
		return local;
	}
	public void setLocal(String local) {
		this.local = local;
	}
	public String getTitulo() {
		return titulo;
	}
	public void setTitulo(String titulo) {
		this.titulo = titulo;
	}
	public List<PdfReciboDetalleRequest> getListGastos() {
		return listGastos;
	}
	public void setListGastos(List<PdfReciboDetalleRequest> listGastos) {
		this.listGastos = listGastos;
	}
	


}
