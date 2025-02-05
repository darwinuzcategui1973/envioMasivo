package com.desige.webDocuments.sacop.forms;

import java.io.Serializable;

import org.apache.struts.upload.FormFile;

import com.desige.webDocuments.utils.beans.SuperActionForm;
import com.focus.qweb.to.PlanillaSacopAccionTO;

/**
 * Created by IntelliJ IDEA.
 * User: srodriguez
 * Date: 21/12/2005
 * Time: 10:44:38 AM
 * To change this template use File | Settings | File Templates.
 */
public class PlantillaAccion extends SuperActionForm implements Serializable {
    /**
	 * 
	 */
	private static final long serialVersionUID = -6151849836344957874L;
	
	private long idplanillasacopaccion;
    private long idplanillasacop1;
    private String accion;
    private String fecha;
    private String responsables;
    private byte active;
    private String id;
    private String numerodeaccion;
    private String comentario;
    private FormFile evidencia;
    private boolean delPreviousEvidence;
    private boolean updateEvidenceField;
    
    public PlantillaAccion() {
    	
    }
    public PlantillaAccion(PlanillaSacopAccionTO o) {
    	setIdplanillasacopaccion(Long.parseLong(o.getIdplanillasacopaccion()!=null?o.getIdplanillasacopaccion():"0"));
    	setIdplanillasacop1(Long.parseLong(o.getIdplanillasacop1()!=null?o.getIdplanillasacop1():"0"));
    	setAccion(o.getAccion());
    	setFecha(o.getFecha());
    	setResponsables(o.getResponsables());
    	setActive(Byte.parseByte(o.getActive()!=null?o.getActive():"0"));
    }
    
    public String getComentario() {
        return comentario;
    }

    public void setComentario(String comentario) {
        this.comentario = comentario;
    }

     public String getNumerodeaccion() {
        return numerodeaccion;
    }

    public void setNumerodeaccion(String numerodeaccion) {
        this.numerodeaccion  = numerodeaccion;
    }

    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public void setIdplanillasacopaccion(long idplanillasacopaccion) {
         this.idplanillasacopaccion  = idplanillasacopaccion;
    }
     public long getIdplanillasacopaccion() {
             return idplanillasacopaccion;
     }

    public long getIdplanillasacop1() {
        return idplanillasacop1;
    }
    public void setIdplanillasacop1(long idplanillasacop1) {
        this.idplanillasacop1  = idplanillasacop1;
    }


     public String getResponsables() {
        return responsables;
    }

    public void setResponsables(String responsables) {
        this.responsables  = responsables;
    }

     public String getAccion() {
        return accion;
    }

    public void setAccion(String accion) {
        this.accion  = accion;
    }



    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha   = fecha;
    }



    public byte getActive() {
        return active;
    }

    public void setActive(byte active) {
        this.active= active;
    }

    public FormFile getEvidencia() {
		return evidencia;
	}
    
    public void setEvidencia(FormFile evidencia) {
		this.evidencia = evidencia;
	}
    
    public void setDelPreviousEvidence(boolean delPreviousEvidence) {
		this.delPreviousEvidence = delPreviousEvidence;
	}
    
    public boolean isDelPreviousEvidence() {
		return delPreviousEvidence;
	}
    
    public void setUpdateEvidenceField(boolean updateEvidenceField) {
		this.updateEvidenceField = updateEvidenceField;
	}
    
    public boolean isUpdateEvidenceField() {
		return updateEvidenceField;
	}
}
