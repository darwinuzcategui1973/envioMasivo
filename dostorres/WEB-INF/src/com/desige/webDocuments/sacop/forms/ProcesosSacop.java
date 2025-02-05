package com.desige.webDocuments.sacop.forms;

import java.io.Serializable;

import com.desige.webDocuments.utils.beans.SuperActionForm;

/**
 * Created by IntelliJ IDEA.
 * User: srodriguez
 * Date: 07/12/2005
 * Time: 09:12:39 AM
 * To change this template use File | Settings | File Templates.
 */

public class ProcesosSacop extends SuperActionForm implements Serializable {
    private long numproceso;
    private String proceso;
    private byte active;
    private String id;
    private String name;
    private String type;
    private String nameFile;  
    public byte getActive() {
        return active;
    }

    public void setActive(byte active) {
        this.active = active;
    }

    public long getNumproceso() {
        return numproceso;
    }

    public void setNumproceso(long numproceso) {
        this.numproceso = numproceso;
    }

    public String getProceso() {
        return proceso;
    }

    public void setProceso(String proceso) {
        this.proceso = proceso;
    }
    public void cleanForm(){
        setProceso("");
        setNumproceso(0);
        setActive((byte)0);
    }

    public String getNameFile() {
        return nameFile;
    }

    public void setNameFile(String nameFile) {
        this.nameFile = nameFile;
    }

    //SIMON 21 DE JUNIO 2005 INICIO
       public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

    //SIMON 21 DE JUNIO 2005 FIN
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}