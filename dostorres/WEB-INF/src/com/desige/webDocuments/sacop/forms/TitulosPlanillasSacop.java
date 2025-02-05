package com.desige.webDocuments.sacop.forms;

import java.io.Serializable;

import com.desige.webDocuments.utils.beans.SuperActionForm;

/**
 * Created by IntelliJ IDEA.
 * User: srodriguez
 * Date: 30/01/2006
 * Time: 11:38:48 AM
 * To change this template use File | Settings | File Templates.
 */

public class TitulosPlanillasSacop extends SuperActionForm implements Serializable {
    private long numtitulosplanillas;
    private String titulosplanillas;
    private byte active;
    private String id;
    private String name;
    private String type;
    private String nameFile;
    
    public TitulosPlanillasSacop() {
    	
    }

    public byte getActive() {
        return active;
    }

    public void setActive(byte active) {
        this.active = active;
    }

    public long getNumtitulosplanillas() {
        return numtitulosplanillas;
    }

    public void setNumtitulosplanillas(long numtitulosplanillas) {
        this.numtitulosplanillas = numtitulosplanillas;
    }

    public String getTitulosplanillas() {
        return titulosplanillas;
    }

    public void setTitulosplanillas(String titulosplanillas) {
        this.titulosplanillas = titulosplanillas;
    }
    public void cleanForm(){
        setTitulosplanillas("");
        setNumtitulosplanillas(0);
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