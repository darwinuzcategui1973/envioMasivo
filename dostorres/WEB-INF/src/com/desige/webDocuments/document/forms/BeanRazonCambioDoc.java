package com.desige.webDocuments.document.forms;

import java.io.Serializable;

public class BeanRazonCambioDoc implements Serializable {
    private String versionCambio;
    private String razonCambio;
    private String fechaCambio;
    private String usuarioCambio;
  
    public BeanRazonCambioDoc(String versionCambio,String razonCambio,String fechaCambio) {
        setVersionCambio(versionCambio);
        setRazonCambio(razonCambio);
        setFechaCambio(fechaCambio);
    }

   
    

    public BeanRazonCambioDoc() {

    }

    public String getVersionCambio() {
        return versionCambio;
    }

    public void setVersionCambio(String versionCambio) {
        this.versionCambio = versionCambio;
    }

    public String getRazonCambio() {
        return razonCambio;
    }

    public void setRazonCambio(String razonCambio) {
        this.razonCambio = razonCambio;
    }

    public String getFechaCambio() {
        return fechaCambio;
    }

    public void setFechaCambio(String fechaCambio) {
        this.fechaCambio = fechaCambio;
    }
    
    public String getUsuarioCambio() {
        return usuarioCambio;
    }

    public void setUsuarioCambio(String usuarioCambio) {
        this.usuarioCambio = usuarioCambio;
    }

     
}
