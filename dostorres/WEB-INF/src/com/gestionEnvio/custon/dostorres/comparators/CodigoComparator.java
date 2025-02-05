package com.gestionEnvio.custon.dostorres.comparators;

import java.util.Comparator;

import com.gestionEnvio.custon.dostorres.forms.BaseReciboForm;

public class CodigoComparator implements Comparator {
	
	private boolean isDesc=false;
	
	public  CodigoComparator(){
		
	}

	public  CodigoComparator(boolean isDesc){
		this.isDesc=isDesc;
	}
	
    public int compare(Object o1, Object o2) {
    	BaseReciboForm rec1 = (BaseReciboForm)o1;
    	BaseReciboForm rec2 = (BaseReciboForm)o2;
        String nameI = rec1.getCodcli().trim();
        String nameII = rec2.getCodcli().trim();
        return (!isDesc?nameI.compareToIgnoreCase(nameII):nameII.compareToIgnoreCase(nameI));
    }

}
