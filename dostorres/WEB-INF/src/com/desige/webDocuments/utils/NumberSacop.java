package com.desige.webDocuments.utils;

public class NumberSacop {

	private boolean prefijoSistema = true;
	private boolean prefijoArea = false;
	private boolean prefijoAno = false;
	private boolean prefijoMes = false;
	private boolean prefijoDia = false;
	private boolean correlativo = true;
	private boolean renumerar = true;
	private boolean renumerarSistema = true;
	private boolean renumerarArea = true;
	private boolean renumerarAno = true;
	private boolean renumerarMes = true;
	private boolean renumerarDia = true;
	
	public NumberSacop() {
		
	}

	public NumberSacop(String number) {
		char[] n = number.toCharArray();
		if(n.length>5) {
			setPrefijoSistema(n[0]=='1');
			setPrefijoArea(n[1]=='1');
			setPrefijoAno(n[2]=='1');
			setPrefijoMes(n[3]=='1');
			setPrefijoDia(n[4]=='1');
			setCorrelativo(n[5]=='1');
			setRenumerarSistema(n[6]=='1');
			setRenumerarArea(n[6]=='2');
			setRenumerarAno(n[6]=='3');
			setRenumerarMes(n[6]=='4');
			setRenumerarDia(n[6]=='5');
		}
	}
	
	public boolean isRenumerar() {
		return (isRenumerarSistema() || isRenumerarArea() || isRenumerarAno() || isRenumerarMes() || isRenumerarDia());
	}

	public void setRenumerar(boolean renumerar) {
		this.renumerar = renumerar;
	}

	public boolean isCorrelativo() {
		return correlativo;
	}
	public void setCorrelativo(boolean correlativo) {
		this.correlativo = correlativo;
	}
	public boolean isPrefijoAno() {
		return prefijoAno;
	}
	public void setPrefijoAno(boolean prefijoAno) {
		this.prefijoAno = prefijoAno;
	}
	public boolean isPrefijoArea() {
		return prefijoArea;
	}
	public void setPrefijoArea(boolean prefijoArea) {
		this.prefijoArea = prefijoArea;
	}
	public boolean isPrefijoDia() {
		return prefijoDia;
	}
	public void setPrefijoDia(boolean prefijoDia) {
		this.prefijoDia = prefijoDia;
	}
	public boolean isPrefijoMes() {
		return prefijoMes;
	}
	public void setPrefijoMes(boolean prefijoMes) {
		this.prefijoMes = prefijoMes;
	}
	public boolean isPrefijoSistema() {
		return prefijoSistema;
	}
	public void setPrefijoSistema(boolean prefijoSistema) {
		this.prefijoSistema = prefijoSistema;
	}

	public boolean isRenumerarAno() {
		return renumerarAno;
	}

	public void setRenumerarAno(boolean renumerarAno) {
		this.renumerarAno = renumerarAno;
	}

	public boolean isRenumerarDia() {
		return renumerarDia;
	}

	public void setRenumerarDia(boolean renumerarDia) {
		this.renumerarDia = renumerarDia;
	}

	public boolean isRenumerarMes() {
		return renumerarMes;
	}

	public void setRenumerarMes(boolean renumerarMes) {
		this.renumerarMes = renumerarMes;
	}

	public boolean isRenumerarArea() {
		return renumerarArea;
	}

	public void setRenumerarArea(boolean renumerarArea) {
		this.renumerarArea = renumerarArea;
	}

	public boolean isRenumerarSistema() {
		return renumerarSistema;
	}

	public void setRenumerarSistema(boolean renumerarSistema) {
		this.renumerarSistema = renumerarSistema;
	}

	
}
