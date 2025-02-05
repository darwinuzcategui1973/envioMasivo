package com.gestionEnvio.custon.dostorres.bean;

public class PdfReciboDetalleGastos {
	private String reciboDetalleTipoGastos;
	private String reciboDetalleIVAtasa;
	private double reciboDetalleConIVA;
	private double reciboDetalleSinIVA;
	private double reciboDetalleIVA;
	private double reciboDetalleTotal;
	
	public String getReciboDetalleTipoGastos() {
		return reciboDetalleTipoGastos;
	}
	public void setReciboDetalleTipoGastos(String reciboDetalleTipoGastos) {
		this.reciboDetalleTipoGastos = reciboDetalleTipoGastos;
	}
	public String getReciboDetalleIVAtasa() {
		return reciboDetalleIVAtasa;
	}
	public void setReciboDetalleIVAtasa(String reciboDetalleIVAtasa) {
		this.reciboDetalleIVAtasa = reciboDetalleIVAtasa;
	}
	
	public double getReciboDetalleConIVA() {
		return reciboDetalleConIVA;
	}
	public String getReciboDetalleConIVASTR() {
		return String.format("%1$,.2f",reciboDetalleConIVA);
	}
	public void setReciboDetalleConIVASTR(String reciboDetalleConIVA) {
		this.reciboDetalleConIVA =  Double.parseDouble(reciboDetalleConIVA);
	}
	public void setReciboDetalleConIVA(double reciboDetalleConIVA) {
		this.reciboDetalleConIVA = reciboDetalleConIVA;
	}
	
	public double getReciboDetalleSinIVA() {
		return reciboDetalleSinIVA;
	}
	public String getReciboDetalleSinIVASTR() {
		return String.format("%1$,.2f",reciboDetalleSinIVA);
	}
	public void setReciboDetalleSinIVASTR(String reciboDetalleSinIVA) {
		this.reciboDetalleSinIVA =  Double.parseDouble(reciboDetalleSinIVA);
	}
	public void setReciboDetalleSinIVA(double reciboDetalleSinIVA) {
		this.reciboDetalleSinIVA = reciboDetalleSinIVA;
	}
	
	public double getReciboDetalleIVA() {
		return reciboDetalleIVA;
	}
	
	public String getReciboDetalleIVASTR() {
		return String.format("%1$,.2f",reciboDetalleIVA);
	}
	public void setReciboDetalleIVASTR(String reciboDetalleIVA) {
		this.reciboDetalleIVA =  Double.parseDouble(reciboDetalleIVA);
	}
	public void setReciboDetalleIVA(double reciboDetalleIVA) {
		this.reciboDetalleIVA = reciboDetalleIVA;
	}
	public double getReciboDetalleTotal() {
		return reciboDetalleTotal;
	}
	
	public String getReciboDetalleTotalSTR() {
		return String.format("%1$,.2f",reciboDetalleTotal);
	}
	public void setReciboDetalleTotalSTR(String reciboDetalleTotal) {
		this.reciboDetalleTotal =  Double.parseDouble(reciboDetalleTotal);
	}


	public void setReciboDetalleTotal(double reciboDetalleTotal) {
		this.reciboDetalleTotal = reciboDetalleTotal;
	}
	

}
