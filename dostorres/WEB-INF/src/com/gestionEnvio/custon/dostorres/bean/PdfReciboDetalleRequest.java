package com.gestionEnvio.custon.dostorres.bean;

public class PdfReciboDetalleRequest {

	private String reciboNumero;
	private String recibocodigo;
	private String reciboDescipcion;
	private String reciboTipoIva;// EX A
	private String reciboAnioMes;
	private double reciboMonto;
	private double reciboCuotaParte;
	private double reciboCuotaParteConIVA;
	private double reciboCuotaParteSinIVA;
	private double reciboIVA;
	private double reciboCuotaParteDolares;
	private double reciboTotalGeneral;
	private double reciboFondoReserva;

	public String getReciboNumero() {
		return reciboNumero;
	}

	public void setReciboNumero(String reciboNumero) {
		this.reciboNumero = reciboNumero;
	}

	public String getRecibocodigo() {
		return recibocodigo;
	}

	public void setRecibocodigo(String recibocodigo) {
		this.recibocodigo = recibocodigo;
	}

	public String getReciboDescipcion() {
		return reciboDescipcion;
	}

	public void setReciboDescipcion(String reciboDescipcion) {
		this.reciboDescipcion = reciboDescipcion;
	}

	public String getReciboTipoIva() {
		return reciboTipoIva;
	}

	public void setReciboTipoIva(String reciboTipoIva) {
		this.reciboTipoIva = reciboTipoIva;
	}

	public double getReciboMonto() {
		return reciboMonto;
	}

	public String getReciboMontoSTR() {
		return String.format("%1$,.2f", reciboMonto);
	}

	public void setReciboMontoSTR(String reciboMonto) {
		// String.format("%1$,.2f", myDouble)
		this.reciboMonto = Double.parseDouble(reciboMonto);
		// this.reciboMonto = String.format("%1$,.2f", reciboMonto);
		// double result = Double.parseDouble("4.56");
	}

	public void setReciboMonto(double reciboMonto) {
		this.reciboMonto = reciboMonto;
	}

	public double getReciboCuotaParte() {
		return reciboCuotaParte;
	}

	public String getReciboCuotaParteSTR() {
		return String.format("%1$,.2f", reciboCuotaParte);
	}

	public void setReciboCuotaParteSTR(String reciboCuotaParte) {
		this.reciboCuotaParte = Double.parseDouble(reciboCuotaParte);
		// double result = Double.parseDouble("4.56");
	}

	public void setReciboCuotaParte(double reciboCuotaParte) {
		this.reciboCuotaParte = reciboCuotaParte;
	}

	// con iva
	public double getReciboCuotaParteConIVA() {
		return reciboCuotaParteConIVA;
	}

	public String getReciboCuotaParteConIVASTR() {
		return String.format("%1$,.2f", reciboCuotaParteConIVA);
	}

	public void setReciboCuotaParteConIVASTR(String reciboCuotaParteConIVA) {
		this.reciboCuotaParteConIVA = Double.parseDouble(reciboCuotaParteConIVA);
		// double result = Double.parseDouble("4.56");
	}

	public void setReciboCuotaParteConIVA(double reciboCuotaParteConIVA) {
		this.reciboCuotaParteConIVA = reciboCuotaParteConIVA;
	}

	// con totalgeneral
	public double getReciboTotalGeneral() {
		return reciboTotalGeneral;
	}

	public String getReciboTotalGeneralSTR() {
		return String.format("%1$,.2f", reciboTotalGeneral);
	}

	public void setReciboTotalGeneralSTR(String reciboTotalGeneral) {
		this.reciboTotalGeneral = Double.parseDouble(reciboTotalGeneral);
		// double result = Double.parseDouble("4.56");
	}

	public void setReciboTotalGeneral(double reciboTotalGeneral) {
		this.reciboTotalGeneral = reciboTotalGeneral;
	}

	// sin iva
	public double getReciboCuotaParteSinIVA() {
		return reciboCuotaParteSinIVA;
	}

	public String getReciboCuotaParteSinIVASTR() {
		return String.format("%1$,.2f", reciboCuotaParteSinIVA);
	}

	public void setReciboCuotaParteSinIVASTR(String reciboCuotaParteSinIVA) {
		this.reciboCuotaParteSinIVA = Double.parseDouble(reciboCuotaParteSinIVA);
		// double result = Double.parseDouble("4.56");
	}

	public void setReciboCuotaParteSinIVA(double reciboCuotaParteSinIVA) {
		this.reciboCuotaParteSinIVA = reciboCuotaParteSinIVA;
	}

	public double getReciboIVA() {
		return reciboIVA;
	}

	public String getReciboIVASTR() {
		return String.format("%1$,.2f", reciboIVA);
	}

	public void setReciboIVASTR(String reciboIVA) {
		this.reciboIVA = Double.parseDouble(reciboIVA);
		// double result = Double.parseDouble("4.56");
	}

	public void setReciboIVA(double reciboIVA) {
		this.reciboIVA = reciboIVA;
	}

	public double getReciboCuotaParteDolares() {
		return reciboCuotaParteDolares;
	}

	public String getReciboCuotaParteDolaresSTR() {
		return String.format("%1$,.2f", reciboCuotaParteDolares);
	}

	public void setReciboCuotaParteDolaresSTR(String reciboCuotaParteDolares) {
		this.reciboCuotaParteDolares = Double.parseDouble(reciboCuotaParteDolares);
	}

	public void setReciboCuotaParteDolares(double reciboCuotaParteDolares) {
		this.reciboCuotaParteDolares = reciboCuotaParteDolares;
	}

	// con totalFondoReserva
	public double getReciboFondoReserva() {
		return reciboFondoReserva;
	}

	public String getReciboFondoReservaSTR() {
		return String.format("%1$,.2f", reciboFondoReserva);
	}

	public void setReciboFondoReservaSTR(String reciboFondoReserva) {
		this.reciboFondoReserva = Double.parseDouble(reciboFondoReserva);
		// double result = Double.parseDouble("4.56");
	}

	public void seciboFondoReserva(double reciboFondoReserva) {
		this.reciboFondoReserva = reciboFondoReserva;
	}
	
	public String getReciboAnioMes() {
		return reciboAnioMes;
	}

	public void setReciboAnioMes(String reciboAnioMes) {
		this.reciboAnioMes = reciboAnioMes;
	}

}
