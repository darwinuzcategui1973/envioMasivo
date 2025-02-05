package com.desige.webDocuments.files.forms;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.desige.webDocuments.persistent.managers.HandlerDBUser;
import com.desige.webDocuments.utils.beans.SuperActionForm;

/**
 * Title: grupoForm.java <br/>
 * Copyright: (c) 2004 Focus Consulting C.A.<br/>
 * @author Ing. Roger Rodríguez
 * @version WebDocuments v3.0
 * <br/>
 * Changes:<br/> 
 * <ul>
 *      <li>25/06/2004 (RR) Creation </li>
 </ul>
 */
public class ExpedienteForm extends SuperActionForm implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 501114552496376574L;
	
	private int numVer=0;
	private boolean printing=false;
	private int filesVersion = 0;
	
    private int f1;
    private Date f2;
    private String f3,f4,f5,f6,f7,f8,f9,f10;
    private String f11,f12,f13,f14,f15,f16,f17,f18,f19,f20;
    private String f21,f22,f23,f24,f25,f26,f27,f28,f29,f30;
    private String f31,f32,f33,f34,f35,f36,f37,f38,f39,f40;
    private String f41,f42,f43,f44,f45,f46,f47,f48,f49,f50;
    private String f51,f52,f53,f54,f55,f56,f57,f58,f59,f60;
    private String f61,f62,f63,f64,f65,f66,f67,f68,f69,f70;
    private String f71,f72,f73,f74,f75,f76,f77,f78,f79,f80;
    private String f81,f82,f83,f84,f85,f86,f87,f88,f89,f90;
    private String f91,f92,f93,f94,f95,f96,f97,f98,f99,f100;
    
    private String fecha;
    private String nameUser;
    
    public void reset(){
    	setF1(0);
    	setF2(new Date());
    	setF3("");setF4("");setF5("");setF6("");setF7("");setF8("");setF9("");setF10("");
    	setF11("");setF12("");setF13("");setF14("");setF15("");setF16("");setF17("");setF18("");setF19("");setF20("");
    	setF21("");setF22("");setF23("");setF24("");setF25("");setF26("");setF27("");setF28("");setF29("");setF30("");
    	setF31("");setF32("");setF33("");setF34("");setF35("");setF36("");setF37("");setF38("");setF39("");setF40("");
    	setF41("");setF42("");setF43("");setF44("");setF45("");setF46("");setF47("");setF48("");setF49("");setF50("");
    	setF51("");setF52("");setF53("");setF54("");setF55("");setF56("");setF57("");setF58("");setF59("");setF60("");
    	setF61("");setF62("");setF63("");setF64("");setF65("");setF66("");setF67("");setF68("");setF69("");setF70("");
    	setF61("");setF72("");setF73("");setF74("");setF75("");setF76("");setF77("");setF78("");setF79("");setF80("");
    	setF71("");setF82("");setF83("");setF84("");setF85("");setF86("");setF87("");setF88("");setF89("");setF90("");
    	setF81("");setF92("");setF93("");setF94("");setF95("");setF96("");setF97("");setF98("");setF99("");setF100("");
    }
    
    // reflexion
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

    public int getF1() {
		return f1;
	}

	public void setF1(int f1) {
		this.f1 = f1;
	}
	
	public void setF1(Integer f1) {
		this.f1 = f1.intValue();
	}
	

	public String getF10() {
		return f10;
	}

	public void setF10(String f10) {
		this.f10 = f10;
	}

	public String getF100() {
		return f100;
	}

	public void setF100(String f100) {
		this.f100 = f100;
	}

	public String getF11() {
		return f11;
	}

	public void setF11(String f11) {
		this.f11 = f11;
	}

	public String getF12() {
		return f12;
	}

	public void setF12(String f12) {
		this.f12 = f12;
	}

	public String getF13() {
		return f13;
	}

	public void setF13(String f13) {
		this.f13 = f13;
	}

	public String getF14() {
		return f14;
	}

	public void setF14(String f14) {
		this.f14 = f14;
	}

	public String getF15() {
		return f15;
	}

	public void setF15(String f15) {
		this.f15 = f15;
	}

	public String getF16() {
		return f16;
	}

	public void setF16(String f16) {
		this.f16 = f16;
	}

	public String getF17() {
		return f17;
	}

	public void setF17(String f17) {
		this.f17 = f17;
	}

	public String getF18() {
		return f18;
	}

	public void setF18(String f18) {
		this.f18 = f18;
	}

	public String getF19() {
		return f19;
	}

	public void setF19(String f19) {
		this.f19 = f19;
	}

	public Date getF2() {
		return f2;
	}

	public void setF2(Date f2) {
		this.f2 = f2;
	}

	public String getF20() {
		return f20;
	}

	public void setF20(String f20) {
		this.f20 = f20;
	}

	public String getF21() {
		return f21;
	}

	public void setF21(String f21) {
		this.f21 = f21;
	}

	public String getF22() {
		return f22;
	}

	public void setF22(String f22) {
		this.f22 = f22;
	}

	public String getF23() {
		return f23;
	}

	public void setF23(String f23) {
		this.f23 = f23;
	}

	public String getF24() {
		return f24;
	}

	public void setF24(String f24) {
		this.f24 = f24;
	}

	public String getF25() {
		return f25;
	}

	public void setF25(String f25) {
		this.f25 = f25;
	}

	public String getF26() {
		return f26;
	}

	public void setF26(String f26) {
		this.f26 = f26;
	}

	public String getF27() {
		return f27;
	}

	public void setF27(String f27) {
		this.f27 = f27;
	}

	public String getF28() {
		return f28;
	}

	public void setF28(String f28) {
		this.f28 = f28;
	}

	public String getF29() {
		return f29;
	}

	public void setF29(String f29) {
		this.f29 = f29;
	}

	public String getF3() {
		return f3;
	}

	public void setF3(String f3) {
		this.f3 = f3;
	}

	public String getF30() {
		return f30;
	}

	public void setF30(String f30) {
		this.f30 = f30;
	}

	public String getF31() {
		return f31;
	}

	public void setF31(String f31) {
		this.f31 = f31;
	}

	public String getF32() {
		return f32;
	}

	public void setF32(String f32) {
		this.f32 = f32;
	}

	public String getF33() {
		return f33;
	}

	public void setF33(String f33) {
		this.f33 = f33;
	}

	public String getF34() {
		return f34;
	}

	public void setF34(String f34) {
		this.f34 = f34;
	}

	public String getF35() {
		return f35;
	}

	public void setF35(String f35) {
		this.f35 = f35;
	}

	public String getF36() {
		return f36;
	}

	public void setF36(String f36) {
		this.f36 = f36;
	}

	public String getF37() {
		return f37;
	}

	public void setF37(String f37) {
		this.f37 = f37;
	}

	public String getF38() {
		return f38;
	}

	public void setF38(String f38) {
		this.f38 = f38;
	}

	public String getF39() {
		return f39;
	}

	public void setF39(String f39) {
		this.f39 = f39;
	}

	public String getF4() {
		return f4;
	}

	public void setF4(String f4) {
		this.f4 = f4;
	}

	public String getF40() {
		return f40;
	}

	public void setF40(String f40) {
		this.f40 = f40;
	}

	public String getF41() {
		return f41;
	}

	public void setF41(String f41) {
		this.f41 = f41;
	}

	public String getF42() {
		return f42;
	}

	public void setF42(String f42) {
		this.f42 = f42;
	}

	public String getF43() {
		return f43;
	}

	public void setF43(String f43) {
		this.f43 = f43;
	}

	public String getF44() {
		return f44;
	}

	public void setF44(String f44) {
		this.f44 = f44;
	}

	public String getF45() {
		return f45;
	}

	public void setF45(String f45) {
		this.f45 = f45;
	}

	public String getF46() {
		return f46;
	}

	public void setF46(String f46) {
		this.f46 = f46;
	}

	public String getF47() {
		return f47;
	}

	public void setF47(String f47) {
		this.f47 = f47;
	}

	public String getF48() {
		return f48;
	}

	public void setF48(String f48) {
		this.f48 = f48;
	}

	public String getF49() {
		return f49;
	}

	public void setF49(String f49) {
		this.f49 = f49;
	}

	public String getF5() {
		return f5;
	}

	public void setF5(String f5) {
		this.f5 = f5;
	}

	public String getF50() {
		return f50;
	}

	public void setF50(String f50) {
		this.f50 = f50;
	}

	public String getF51() {
		return f51;
	}

	public void setF51(String f51) {
		this.f51 = f51;
	}

	public String getF52() {
		return f52;
	}

	public void setF52(String f52) {
		this.f52 = f52;
	}

	public String getF53() {
		return f53;
	}

	public void setF53(String f53) {
		this.f53 = f53;
	}

	public String getF54() {
		return f54;
	}

	public void setF54(String f54) {
		this.f54 = f54;
	}

	public String getF55() {
		return f55;
	}

	public void setF55(String f55) {
		this.f55 = f55;
	}

	public String getF56() {
		return f56;
	}

	public void setF56(String f56) {
		this.f56 = f56;
	}

	public String getF57() {
		return f57;
	}

	public void setF57(String f57) {
		this.f57 = f57;
	}

	public String getF58() {
		return f58;
	}

	public void setF58(String f58) {
		this.f58 = f58;
	}

	public String getF59() {
		return f59;
	}

	public void setF59(String f59) {
		this.f59 = f59;
	}

	public String getF6() {
		return f6;
	}

	public void setF6(String f6) {
		this.f6 = f6;
	}

	public String getF60() {
		return f60;
	}

	public void setF60(String f60) {
		this.f60 = f60;
	}

	public String getF61() {
		return f61;
	}

	public void setF61(String f61) {
		this.f61 = f61;
	}

	public String getF62() {
		return f62;
	}

	public void setF62(String f62) {
		this.f62 = f62;
	}

	public String getF63() {
		return f63;
	}

	public void setF63(String f63) {
		this.f63 = f63;
	}

	public String getF64() {
		return f64;
	}

	public void setF64(String f64) {
		this.f64 = f64;
	}

	public String getF65() {
		return f65;
	}

	public void setF65(String f65) {
		this.f65 = f65;
	}

	public String getF66() {
		return f66;
	}

	public void setF66(String f66) {
		this.f66 = f66;
	}

	public String getF67() {
		return f67;
	}

	public void setF67(String f67) {
		this.f67 = f67;
	}

	public String getF68() {
		return f68;
	}

	public void setF68(String f68) {
		this.f68 = f68;
	}

	public String getF69() {
		return f69;
	}

	public void setF69(String f69) {
		this.f69 = f69;
	}

	public String getF7() {
		return f7;
	}

	public void setF7(String f7) {
		this.f7 = f7;
	}

	public String getF70() {
		return f70;
	}

	public void setF70(String f70) {
		this.f70 = f70;
	}

	public String getF71() {
		return f71;
	}

	public void setF71(String f71) {
		this.f71 = f71;
	}

	public String getF72() {
		return f72;
	}

	public void setF72(String f72) {
		this.f72 = f72;
	}

	public String getF73() {
		return f73;
	}

	public void setF73(String f73) {
		this.f73 = f73;
	}

	public String getF74() {
		return f74;
	}

	public void setF74(String f74) {
		this.f74 = f74;
	}

	public String getF75() {
		return f75;
	}

	public void setF75(String f75) {
		this.f75 = f75;
	}

	public String getF76() {
		return f76;
	}

	public void setF76(String f76) {
		this.f76 = f76;
	}

	public String getF77() {
		return f77;
	}

	public void setF77(String f77) {
		this.f77 = f77;
	}

	public String getF78() {
		return f78;
	}

	public void setF78(String f78) {
		this.f78 = f78;
	}

	public String getF79() {
		return f79;
	}

	public void setF79(String f79) {
		this.f79 = f79;
	}

	public String getF8() {
		return f8;
	}

	public void setF8(String f8) {
		this.f8 = f8;
	}

	public String getF80() {
		return f80;
	}

	public void setF80(String f80) {
		this.f80 = f80;
	}

	public String getF81() {
		return f81;
	}

	public void setF81(String f81) {
		this.f81 = f81;
	}

	public String getF82() {
		return f82;
	}

	public void setF82(String f82) {
		this.f82 = f82;
	}

	public String getF83() {
		return f83;
	}

	public void setF83(String f83) {
		this.f83 = f83;
	}

	public String getF84() {
		return f84;
	}

	public void setF84(String f84) {
		this.f84 = f84;
	}

	public String getF85() {
		return f85;
	}

	public void setF85(String f85) {
		this.f85 = f85;
	}

	public String getF86() {
		return f86;
	}

	public void setF86(String f86) {
		this.f86 = f86;
	}

	public String getF87() {
		return f87;
	}

	public void setF87(String f87) {
		this.f87 = f87;
	}

	public String getF88() {
		return f88;
	}

	public void setF88(String f88) {
		this.f88 = f88;
	}

	public String getF89() {
		return f89;
	}

	public void setF89(String f89) {
		this.f89 = f89;
	}

	public String getF9() {
		return f9;
	}

	public void setF9(String f9) {
		this.f9 = f9;
	}

	public String getF90() {
		return f90;
	}

	public void setF90(String f90) {
		this.f90 = f90;
	}

	public String getF91() {
		return f91;
	}

	public void setF91(String f91) {
		this.f91 = f91;
	}

	public String getF92() {
		return f92;
	}

	public void setF92(String f92) {
		this.f92 = f92;
	}

	public String getF93() {
		return f93;
	}

	public void setF93(String f93) {
		this.f93 = f93;
	}

	public String getF94() {
		return f94;
	}

	public void setF94(String f94) {
		this.f94 = f94;
	}

	public String getF95() {
		return f95;
	}

	public void setF95(String f95) {
		this.f95 = f95;
	}

	public String getF96() {
		return f96;
	}

	public void setF96(String f96) {
		this.f96 = f96;
	}

	public String getF97() {
		return f97;
	}

	public void setF97(String f97) {
		this.f97 = f97;
	}

	public String getF98() {
		return f98;
	}

	public void setF98(String f98) {
		this.f98 = f98;
	}

	public String getF99() {
		return f99;
	}

	public void setF99(String f99) {
		this.f99 = f99;
	}
    
	public String getFecha() {
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
			return sdf.format(getF2()!=null?getF2():"");
		}catch(Exception e) {
			return "";
		}
	}
	
	public int getNumVer() {
		return numVer;
	}

	public void setNumVer(int numVer) {
		this.numVer = numVer;
	}

	public boolean isPrinting() {
		return printing;
	}

	public void setPrinting(boolean printing) {
		this.printing = printing;
	}
    
	public String getNameUser() throws Exception{
		return HandlerDBUser.getNameUser(getF3());
	}

	public int getFilesVersion() {
		return filesVersion;
	}

	public void setFilesVersion(int filesVersion) {
		this.filesVersion = filesVersion;
	}    
}
