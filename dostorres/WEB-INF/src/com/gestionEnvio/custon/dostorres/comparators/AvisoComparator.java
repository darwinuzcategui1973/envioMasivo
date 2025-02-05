package com.gestionEnvio.custon.dostorres.comparators;

import java.util.Comparator;

import com.gestionEnvio.custon.dostorres.forms.BaseReciboForm;

public class AvisoComparator implements Comparator {
	
	private boolean isDesc=false;
	
	public  AvisoComparator(){
		
	}

	public  AvisoComparator(boolean isDesc){
		this.isDesc=isDesc;
	}
	
	//com.gestionEnvio.custon.dostorres.forms.BaseReciboForm,

    public int compare(Object o1, Object o2) {
    	BaseReciboForm rec1 = (BaseReciboForm)o1;
    	BaseReciboForm rec2 = (BaseReciboForm)o2;
        String nameI = rec1.getNumero().trim();
        String nameII = rec2.getNumero().trim();
        return (!isDesc?nameI.compareToIgnoreCase(nameII):nameII.compareToIgnoreCase(nameI));
    }

}
