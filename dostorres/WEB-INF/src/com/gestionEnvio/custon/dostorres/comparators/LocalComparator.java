package com.gestionEnvio.custon.dostorres.comparators;

import java.util.Comparator;

import com.gestionEnvio.custon.dostorres.forms.BaseReciboForm;

public class LocalComparator implements Comparator<Object> {
	
	private boolean isDesc=false;
	private BaseReciboForm rec2;
	
	public  LocalComparator(){
		
	}

	public  LocalComparator(boolean isDesc){
		this.isDesc=isDesc;
	}
	
    public int compare(Object o1, Object o2) {
    	BaseReciboForm rec1 = (BaseReciboForm)o1;
    	BaseReciboForm rec2 = (BaseReciboForm)o2;
    	//rec2 = (BaseReciboForm)o2;
        String localI = rec1.getNumlocal().trim();
        String localII = rec2.getNumlocal().trim();
        return (!isDesc?localI.compareToIgnoreCase(localII):localII.compareToIgnoreCase(localI));
    }

}
