package com.focus.migracion.bean;

import java.lang.reflect.InvocationTargetException;
import java.util.Date;

public class MigracionBean {

	private String numero;
	private String nombre;
	private String carpetaDestino;
	private String carpetaActual;
	private String aprobado;
	private String propietario;
	private String tipoDoc;
	private String norma;
	private String palabrasClaves;
	private String url;
	private String descripcion;
	private String comentarios;
	private String nombreFisico;
	private String status ;
	private String creado;
	private String vencimiento;
	private String versionMayor;
	private String versionMenor;
	private String documentoRelacionado;
	
	private String publicado;

	private String d1,d2,d3,d4,d5,d6,d7,d8,d9,d10;
    private String d11,d12,d13,d14,d15,d16,d17,d18,d19,d20;
    private String d21,d22,d23,d24,d25,d26,d27,d28,d29,d30;
    private String d31,d32,d33,d34,d35,d36,d37,d38,d39,d40;
    private String d41,d42,d43,d44,d45,d46,d47,d48,d49,d50;
    private String d51,d52,d53,d54,d55,d56,d57,d58,d59,d60;
    private String d61,d62,d63,d64,d65,d66,d67,d68,d69,d70;
    private String d71,d72,d73,d74,d75,d76,d77,d78,d79,d80;
    private String d81,d82,d83,d84,d85,d86,d87,d88,d89,d90;
    private String d91,d92,d93,d94,d95,d96,d97,d98,d99,d100;
	
    public MigracionBean() {
    	reset();
    }
    
    public void reset(){
    	setD1("");setD2("");setD3("");setD4("");setD5("");setD6("");setD7("");setD8("");setD9("");setD10("");
    	setD11("");setD12("");setD13("");setD14("");setD15("");setD16("");setD17("");setD18("");setD19("");setD20("");
    	setD21("");setD22("");setD23("");setD24("");setD25("");setD26("");setD27("");setD28("");setD29("");setD30("");
    	setD31("");setD32("");setD33("");setD34("");setD35("");setD36("");setD37("");setD38("");setD39("");setD40("");
    	setD41("");setD42("");setD43("");setD44("");setD45("");setD46("");setD47("");setD48("");setD49("");setD50("");
    	setD51("");setD52("");setD53("");setD54("");setD55("");setD56("");setD57("");setD58("");setD59("");setD60("");
    	setD61("");setD62("");setD63("");setD64("");setD65("");setD66("");setD67("");setD68("");setD69("");setD70("");
    	setD71("");setD72("");setD73("");setD74("");setD75("");setD76("");setD77("");setD78("");setD79("");setD80("");
    	setD81("");setD82("");setD83("");setD84("");setD85("");setD86("");setD87("");setD88("");setD89("");setD90("");
    	setD91("");setD92("");setD93("");setD94("");setD95("");setD96("");setD97("");setD98("");setD99("");setD100("");
    }    
	
    // ini reflexion
    public Object get(String name) throws IllegalArgumentException, SecurityException, IllegalAccessException, InvocationTargetException, NoSuchMethodException{
    	return this.getClass().getDeclaredMethod("get".concat(name), new Class[]{}).invoke(this, new Object[]{});
    }

    public Object set(String name, String valor) throws IllegalArgumentException, SecurityException, IllegalAccessException, InvocationTargetException, NoSuchMethodException{
    	return this.getClass().getDeclaredMethod("set".concat(name), new Class[]{String.class}).invoke(this, new Object[]{valor});
    }

    public Object set(String name, int valor) throws IllegalArgumentException, SecurityException, IllegalAccessException, InvocationTargetException, NoSuchMethodException{
    	return this.getClass().getDeclaredMethod("set".concat(name), new Class[]{Integer.class}).invoke(this, new Object[]{valor});
    }
    
    public Object set(String name, Date valor) throws IllegalArgumentException, SecurityException, IllegalAccessException, InvocationTargetException, NoSuchMethodException{
    	return this.getClass().getDeclaredMethod("set".concat(name), new Class[]{Date.class}).invoke(this, new Object[]{valor});
    }
    // fin reflexion
	
	public String getAprobado() {
		return aprobado;
	}
	public void setAprobado(String aprobado) {
		this.aprobado = aprobado;
	}
	public String getCarpetaActual() {
		return carpetaActual;
	}
	public void setCarpetaActual(String carpetaActual) {
		this.carpetaActual = carpetaActual;
	}
	public String getCarpetaDestino() {
		return carpetaDestino;
	}
	public void setCarpetaDestino(String carpetaDestino) {
		this.carpetaDestino = carpetaDestino;
	}
	public String getComentarios() {
		return comentarios;
	}
	public void setComentarios(String comentarios) {
		this.comentarios = comentarios;
	}
	public String getCreado() {
		return creado;
	}
	public void setCreado(String creado) {
		this.creado = creado;
	}
	public String getDescripcion() {
		return descripcion;
	}
	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}
	public String getDocumentoRelacionado() {
		return documentoRelacionado;
	}
	public void setDocumentoRelacionado(String documentoRelacionado) {
		this.documentoRelacionado = documentoRelacionado;
	}
	public String getNombre() {
		return nombre;
	}
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
	public String getNombreFisico() {
		return nombreFisico;
	}
	public void setNombreFisico(String nombreFisico) {
		this.nombreFisico = nombreFisico;
	}
	public String getNorma() {
		return norma;
	}
	public void setNorma(String norma) {
		this.norma = norma;
	}
	public String getNumero() {
		return numero;
	}
	public void setNumero(String numero) {
		this.numero = numero;
	}
	public String getPalabrasClaves() {
		return palabrasClaves;
	}
	public void setPalabrasClaves(String palabrasClaves) {
		this.palabrasClaves = palabrasClaves;
	}
	public String getPropietario() {
		return propietario;
	}
	public void setPropietario(String propietario) {
		this.propietario = propietario;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getTipoDoc() {
		return tipoDoc;
	}
	public void setTipoDoc(String tipoDoc) {
		this.tipoDoc = tipoDoc;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getVencimiento() {
		return vencimiento;
	}
	public void setVencimiento(String vencimiento) {
		this.vencimiento = vencimiento;
	}
	public String getVersionMayor() {
		return versionMayor;
	}
	public void setVersionMayor(String versionMayor) {
		this.versionMayor = versionMayor;
	}
	public String getVersionMenor() {
		return versionMenor;
	}
	public void setVersionMenor(String versionMenor) {
		this.versionMenor = versionMenor;
	}
	public String getPublicado() {
		return publicado;
	}
	public void setPublicado(String publicado) {
		this.publicado = publicado;
	}
	public String getD1() {
		return d1;
	}
	
	public void setD1(String d1) {
		this.d1 = d1;
	}
	public String getD10() {
		return d10;
	}
	public void setD10(String d10) {
		this.d10 = d10;
	}
	public String getD100() {
		return d100;
	}
	public void setD100(String d100) {
		this.d100 = d100;
	}
	public String getD11() {
		return d11;
	}
	public void setD11(String d11) {
		this.d11 = d11;
	}
	public String getD12() {
		return d12;
	}
	public void setD12(String d12) {
		this.d12 = d12;
	}
	public String getD13() {
		return d13;
	}
	public void setD13(String d13) {
		this.d13 = d13;
	}
	public String getD14() {
		return d14;
	}
	public void setD14(String d14) {
		this.d14 = d14;
	}
	public String getD15() {
		return d15;
	}
	public void setD15(String d15) {
		this.d15 = d15;
	}
	public String getD16() {
		return d16;
	}
	public void setD16(String d16) {
		this.d16 = d16;
	}
	public String getD17() {
		return d17;
	}
	public void setD17(String d17) {
		this.d17 = d17;
	}
	public String getD18() {
		return d18;
	}
	public void setD18(String d18) {
		this.d18 = d18;
	}
	public String getD19() {
		return d19;
	}
	public void setD19(String d19) {
		this.d19 = d19;
	}
	public String getD2() {
		return d2;
	}
	public void setD2(String d2) {
		this.d2 = d2;
	}
	public String getD20() {
		return d20;
	}
	public void setD20(String d20) {
		this.d20 = d20;
	}
	public String getD21() {
		return d21;
	}
	public void setD21(String d21) {
		this.d21 = d21;
	}
	public String getD22() {
		return d22;
	}
	public void setD22(String d22) {
		this.d22 = d22;
	}
	public String getD23() {
		return d23;
	}
	public void setD23(String d23) {
		this.d23 = d23;
	}
	public String getD24() {
		return d24;
	}
	public void setD24(String d24) {
		this.d24 = d24;
	}
	public String getD25() {
		return d25;
	}
	public void setD25(String d25) {
		this.d25 = d25;
	}
	public String getD26() {
		return d26;
	}
	public void setD26(String d26) {
		this.d26 = d26;
	}
	public String getD27() {
		return d27;
	}
	public void setD27(String d27) {
		this.d27 = d27;
	}
	public String getD28() {
		return d28;
	}
	public void setD28(String d28) {
		this.d28 = d28;
	}
	public String getD29() {
		return d29;
	}
	public void setD29(String d29) {
		this.d29 = d29;
	}
	public String getD3() {
		return d3;
	}
	public void setD3(String d3) {
		this.d3 = d3;
	}
	public String getD30() {
		return d30;
	}
	public void setD30(String d30) {
		this.d30 = d30;
	}
	public String getD31() {
		return d31;
	}
	public void setD31(String d31) {
		this.d31 = d31;
	}
	public String getD32() {
		return d32;
	}
	public void setD32(String d32) {
		this.d32 = d32;
	}
	public String getD33() {
		return d33;
	}
	public void setD33(String d33) {
		this.d33 = d33;
	}
	public String getD34() {
		return d34;
	}
	public void setD34(String d34) {
		this.d34 = d34;
	}
	public String getD35() {
		return d35;
	}
	public void setD35(String d35) {
		this.d35 = d35;
	}
	public String getD36() {
		return d36;
	}
	public void setD36(String d36) {
		this.d36 = d36;
	}
	public String getD37() {
		return d37;
	}
	public void setD37(String d37) {
		this.d37 = d37;
	}
	public String getD38() {
		return d38;
	}
	public void setD38(String d38) {
		this.d38 = d38;
	}
	public String getD39() {
		return d39;
	}
	public void setD39(String d39) {
		this.d39 = d39;
	}
	public String getD4() {
		return d4;
	}
	public void setD4(String d4) {
		this.d4 = d4;
	}
	public String getD40() {
		return d40;
	}
	public void setD40(String d40) {
		this.d40 = d40;
	}
	public String getD41() {
		return d41;
	}
	public void setD41(String d41) {
		this.d41 = d41;
	}
	public String getD42() {
		return d42;
	}
	public void setD42(String d42) {
		this.d42 = d42;
	}
	public String getD43() {
		return d43;
	}
	public void setD43(String d43) {
		this.d43 = d43;
	}
	public String getD44() {
		return d44;
	}
	public void setD44(String d44) {
		this.d44 = d44;
	}
	public String getD45() {
		return d45;
	}
	public void setD45(String d45) {
		this.d45 = d45;
	}
	public String getD46() {
		return d46;
	}
	public void setD46(String d46) {
		this.d46 = d46;
	}
	public String getD47() {
		return d47;
	}
	public void setD47(String d47) {
		this.d47 = d47;
	}
	public String getD48() {
		return d48;
	}
	public void setD48(String d48) {
		this.d48 = d48;
	}
	public String getD49() {
		return d49;
	}
	public void setD49(String d49) {
		this.d49 = d49;
	}
	public String getD5() {
		return d5;
	}
	public void setD5(String d5) {
		this.d5 = d5;
	}
	public String getD50() {
		return d50;
	}
	public void setD50(String d50) {
		this.d50 = d50;
	}
	public String getD51() {
		return d51;
	}
	public void setD51(String d51) {
		this.d51 = d51;
	}
	public String getD52() {
		return d52;
	}
	public void setD52(String d52) {
		this.d52 = d52;
	}
	public String getD53() {
		return d53;
	}
	public void setD53(String d53) {
		this.d53 = d53;
	}
	public String getD54() {
		return d54;
	}
	public void setD54(String d54) {
		this.d54 = d54;
	}
	public String getD55() {
		return d55;
	}
	public void setD55(String d55) {
		this.d55 = d55;
	}
	public String getD56() {
		return d56;
	}
	public void setD56(String d56) {
		this.d56 = d56;
	}
	public String getD57() {
		return d57;
	}
	public void setD57(String d57) {
		this.d57 = d57;
	}
	public String getD58() {
		return d58;
	}
	public void setD58(String d58) {
		this.d58 = d58;
	}
	public String getD59() {
		return d59;
	}
	public void setD59(String d59) {
		this.d59 = d59;
	}
	public String getD6() {
		return d6;
	}
	public void setD6(String d6) {
		this.d6 = d6;
	}
	public String getD60() {
		return d60;
	}
	public void setD60(String d60) {
		this.d60 = d60;
	}
	public String getD61() {
		return d61;
	}
	public void setD61(String d61) {
		this.d61 = d61;
	}
	public String getD62() {
		return d62;
	}
	public void setD62(String d62) {
		this.d62 = d62;
	}
	public String getD63() {
		return d63;
	}
	public void setD63(String d63) {
		this.d63 = d63;
	}
	public String getD64() {
		return d64;
	}
	public void setD64(String d64) {
		this.d64 = d64;
	}
	public String getD65() {
		return d65;
	}
	public void setD65(String d65) {
		this.d65 = d65;
	}
	public String getD66() {
		return d66;
	}
	public void setD66(String d66) {
		this.d66 = d66;
	}
	public String getD67() {
		return d67;
	}
	public void setD67(String d67) {
		this.d67 = d67;
	}
	public String getD68() {
		return d68;
	}
	public void setD68(String d68) {
		this.d68 = d68;
	}
	public String getD69() {
		return d69;
	}
	public void setD69(String d69) {
		this.d69 = d69;
	}
	public String getD7() {
		return d7;
	}
	public void setD7(String d7) {
		this.d7 = d7;
	}
	public String getD70() {
		return d70;
	}
	public void setD70(String d70) {
		this.d70 = d70;
	}
	public String getD71() {
		return d71;
	}
	public void setD71(String d71) {
		this.d71 = d71;
	}
	public String getD72() {
		return d72;
	}
	public void setD72(String d72) {
		this.d72 = d72;
	}
	public String getD73() {
		return d73;
	}
	public void setD73(String d73) {
		this.d73 = d73;
	}
	public String getD74() {
		return d74;
	}
	public void setD74(String d74) {
		this.d74 = d74;
	}
	public String getD75() {
		return d75;
	}
	public void setD75(String d75) {
		this.d75 = d75;
	}
	public String getD76() {
		return d76;
	}
	public void setD76(String d76) {
		this.d76 = d76;
	}
	public String getD77() {
		return d77;
	}
	public void setD77(String d77) {
		this.d77 = d77;
	}
	public String getD78() {
		return d78;
	}
	public void setD78(String d78) {
		this.d78 = d78;
	}
	public String getD79() {
		return d79;
	}
	public void setD79(String d79) {
		this.d79 = d79;
	}
	public String getD8() {
		return d8;
	}
	public void setD8(String d8) {
		this.d8 = d8;
	}
	public String getD80() {
		return d80;
	}
	public void setD80(String d80) {
		this.d80 = d80;
	}
	public String getD81() {
		return d81;
	}
	public void setD81(String d81) {
		this.d81 = d81;
	}
	public String getD82() {
		return d82;
	}
	public void setD82(String d82) {
		this.d82 = d82;
	}
	public String getD83() {
		return d83;
	}
	public void setD83(String d83) {
		this.d83 = d83;
	}
	public String getD84() {
		return d84;
	}
	public void setD84(String d84) {
		this.d84 = d84;
	}
	public String getD85() {
		return d85;
	}
	public void setD85(String d85) {
		this.d85 = d85;
	}
	public String getD86() {
		return d86;
	}
	public void setD86(String d86) {
		this.d86 = d86;
	}
	public String getD87() {
		return d87;
	}
	public void setD87(String d87) {
		this.d87 = d87;
	}
	public String getD88() {
		return d88;
	}
	public void setD88(String d88) {
		this.d88 = d88;
	}
	public String getD89() {
		return d89;
	}
	public void setD89(String d89) {
		this.d89 = d89;
	}
	public String getD9() {
		return d9;
	}
	public void setD9(String d9) {
		this.d9 = d9;
	}
	public String getD90() {
		return d90;
	}
	public void setD90(String d90) {
		this.d90 = d90;
	}
	public String getD91() {
		return d91;
	}
	public void setD91(String d91) {
		this.d91 = d91;
	}
	public String getD92() {
		return d92;
	}
	public void setD92(String d92) {
		this.d92 = d92;
	}
	public String getD93() {
		return d93;
	}
	public void setD93(String d93) {
		this.d93 = d93;
	}
	public String getD94() {
		return d94;
	}
	public void setD94(String d94) {
		this.d94 = d94;
	}
	public String getD95() {
		return d95;
	}
	public void setD95(String d95) {
		this.d95 = d95;
	}
	public String getD96() {
		return d96;
	}
	public void setD96(String d96) {
		this.d96 = d96;
	}
	public String getD97() {
		return d97;
	}
	public void setD97(String d97) {
		this.d97 = d97;
	}
	public String getD98() {
		return d98;
	}
	public void setD98(String d98) {
		this.d98 = d98;
	}
	public String getD99() {
		return d99;
	}
	public void setD99(String d99) {
		this.d99 = d99;
	}
	
	
}
