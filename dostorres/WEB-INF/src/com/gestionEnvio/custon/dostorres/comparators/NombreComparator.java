package com.gestionEnvio.custon.dostorres.comparators;

import java.util.Comparator;

import com.gestionEnvio.custon.dostorres.forms.BaseReciboForm;

public class NombreComparator implements Comparator {
	
	private boolean isDesc=false;
	
	public  NombreComparator(){
		
	}

	public  NombreComparator(boolean isDesc){
		this.isDesc=isDesc;
	}
	
	//com.gestionEnvio.custon.dostorres.forms.BaseReciboForm,

    public int compare(Object o1, Object o2) {
    	BaseReciboForm rec1 = (BaseReciboForm)o1;
    	BaseReciboForm rec2 = (BaseReciboForm)o2;
        String nameI = rec1.getNomcli().trim();
        String nameII = rec2.getNomcli().trim();
        return (!isDesc?nameI.compareToIgnoreCase(nameII):nameII.compareToIgnoreCase(nameI));
    }

}
