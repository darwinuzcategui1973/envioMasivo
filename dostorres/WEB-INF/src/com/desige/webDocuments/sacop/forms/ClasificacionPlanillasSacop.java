package com.desige.webDocuments.sacop.forms;

import java.io.Serializable;

import com.desige.webDocuments.utils.beans.SuperActionForm;
import com.focus.qweb.to.ClasificacionPlanillasSacopTO;

/**
 * 
 * Title: ClasificacionPlanillasSacop.java <br>
 * Copyright: (c) 2012 Focus Consulting C.A.<br>
 * @author Ing. Felipe Rojas (FJR)
 * 
 * @version WebDocuments v4.5.2
 * <br>
 *     Changes:<br>
 * <ul>
 *     <li> 23/04/2012 (FJR) Creation </li>
 * <ul>
 */
public class ClasificacionPlanillasSacop extends SuperActionForm implements Serializable {
    private long id;
    private String descripcion;
    private int active;

    /**
     * 
     */
    public ClasificacionPlanillasSacop() {
	}
    
    public int getActive() {
        return active;
    }

    public void setActive(int active) {
        this.active = active;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
    
    public void cleanForm(){
        setDescripcion("");
        setId(0);
        setActive(0);
    }
}