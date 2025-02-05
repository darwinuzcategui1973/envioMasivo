package com.desige.webDocuments.record.forms;

import java.io.Serializable;

import com.desige.webDocuments.utils.beans.SuperActionForm;

/**
 * Title: RecordFiltersForm.java<br>
 * Copyright: (c) 2007 Focus Consulting<br>
 * Company:Focus Consulting (CON)<br>
 * @author YSA
 * <br>
 * Changes:<br>
 * <ul>
 *      <li> 10/12/2007 (YSA) Creation </li>
 * <ul>
 */
public class RecordFiltersForm extends SuperActionForm implements Serializable {
    private int recordPromedio;
    private int recordPorUsuario;
    private int medianaGeneral;
    private int intervalo;
    private String desde;
    private String hasta;
    private String desdeTime;
    private String hastaTime;
    private String indicatorsSelected;
    private String usersSelected;
    private String areasSel;
    private String chargesSel;
    private Object[] areas;
    private Object[] charges;
    private Object[] areasSelected;
    private Object[] chargesSelected;

    public RecordFiltersForm(){
    	recordPromedio = 0;
    	recordPorUsuario = 0;
    	medianaGeneral = 0;
    	intervalo = 0;
    	desde = "";
    	hasta = "";
    	desdeTime = "";
    	hastaTime = "";
    	areas = null;
    	charges = null;
    	areasSelected = null;
    	chargesSelected = null;
    	usersSelected = "";
    	indicatorsSelected = "";
    	areasSel = "";
    	chargesSel = "";
    }
    
    public int getRecordPromedio() {
        return recordPromedio;
    }

    public void setRecordPromedio(int recordPromedio) {
        this.recordPromedio = recordPromedio;
    }

    public int getRecordPorUsuario() {
        return recordPorUsuario;
    }

    public void setRecordPorUsuario(int recordPorUsuario) {
        this.recordPorUsuario = recordPorUsuario;
    }

    public int getMedianaGeneral() {
        return medianaGeneral;
    }

    public void setMedianaGeneral(int medianaGeneral) {
        this.medianaGeneral = medianaGeneral;
    }

    public int getIntervalo() {
        return intervalo;
    }

    public void setIntervalo(int intervalo) {
        this.intervalo = intervalo;
    }
    
    public String getDesde() {
        return desde;
    }

    public void setDesde(String desde) {
        this.desde = desde;
    }
    
    public String getHasta() {
        return hasta;
    }

    public void setHasta(String hasta) {
        this.hasta = hasta;
    }
    
    public String getDesdeTime() {
        return desdeTime;
    }

    public void setDesdeTime(String desdeTime) {
        this.desdeTime = desdeTime;
    }
    
    public String getHastaTime() {
        return hastaTime;
    }

    public void setHastaTime(String hastaTime) {
        this.hastaTime = hastaTime;
    }
    
    public Object[] getAreas() {
        return areas;
    }

    public void setAreas(Object[] areas) {
        this.areas = areas;
    }

    public Object[] getCharges() {
        return charges;
    }

    public void setCharges(Object[] charges) {
        this.charges = charges;
    }
    
    public Object[] getAreasSelected() {
        return areasSelected;
    }

    public void setAreasSelected(Object[] areasSelected) {
        this.areasSelected = areasSelected;
    }

    public Object[] getChargesSelected() {
        return chargesSelected;
    }

    public void setChargesSelected(Object[] chargesSelected) {
        this.chargesSelected = chargesSelected;
    }

    public String getUsersSelected() {
        return usersSelected;
    }

    public void setUsersSelected(String usersSelected) {
        this.usersSelected = usersSelected;
    }
    
    public String getIndicatorsSelected() {
        return indicatorsSelected;
    }

    public void setIndicatorsSelected(String indicatorsSelected) {
        this.indicatorsSelected = indicatorsSelected;
    }
    
    public String getAreasSel() {
        return areasSel;
    }

    public void setAreasSel(String areasSel) {
        this.areasSel = areasSel;
    }   

    public String getChargesSel() {
        return chargesSel;
    }

    public void setChargesSel(String chargesSel) {
        this.chargesSel = chargesSel;
    } 
}
