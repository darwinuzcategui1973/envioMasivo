package com.gestionEnvio.custon.dostorres.comparators;

import java.util.Comparator;

import com.desige.webDocuments.utils.ToolsHTML;
import com.gestionEnvio.custon.dostorres.forms.BaseReciboForm;

public class FechaEmisionComparator implements Comparator {

	private boolean isDesc=false;
	
	public FechaEmisionComparator() {
	}

	public FechaEmisionComparator(boolean isDesc) {
		this.isDesc=isDesc;
	}

	public int compare(Object o1, Object o2) {
		BaseReciboForm rec1 = (BaseReciboForm)o1;
		BaseReciboForm rec2 = (BaseReciboForm)o2;
    	//rec2 = (BaseReciboForm)o2;
        String fechaI = null;
        String fechaII = null;
        try {
        	fechaI = rec1.getDateEmitida().trim();
            fechaII = rec2.getDateEmitida().trim();
            //fechaI = ToolsHTML.sdf.format(ToolsHTML.sdfShowWithoutHour.parse(rec1.getDateEmitida()));
            //fechaII = ToolsHTML.sdf.format(ToolsHTML.sdfShowWithoutHour.parse(rec2.getDateEmitida()));
            //fechaI = ToolsHTML.sdf.format(ToolsHTML.sdf.parse(rec1.getDateEmitida()));
            //fechaII = ToolsHTML.sdf.format(ToolsHTML.sdf.parse(rec2.getDateEmitida()));
            //System.out.println(fechaI);
            //System.out.println(fechaII);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return (!isDesc?fechaI.compareTo(fechaII):fechaII.compareTo(fechaI));
    }
}
