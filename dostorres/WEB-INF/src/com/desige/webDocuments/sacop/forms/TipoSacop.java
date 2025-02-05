package com.desige.webDocuments.sacop.forms;

import java.io.Serializable;

import com.desige.webDocuments.utils.beans.SuperActionForm;

@SuppressWarnings("serial")
public class TipoSacop extends SuperActionForm implements Serializable {
	private long idtipo;
    private String id;
    private String tipo;
    private String prefijo;
    private Byte active;
    
    public TipoSacop() {
    	
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
    public Byte getActive() {
        return active;
    }

    public void setActive(Byte active) {
        this.active = active;
    }

    public long getIdtipo() {
        return idtipo;
    }

    public void setIdtipo(long idtipo) {
        this.idtipo = idtipo;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }
    
    public String getPrefijo() {
		return prefijo==null?"":prefijo;
	}

	public void setPrefijo(String prefijo) {
		this.prefijo = prefijo;
	}

	public void cleanForm(){
        setTipo("");
        setPrefijo("");
        //setIdtipo(0);
    }

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Tipo [idtipo=").append(idtipo).append(", id=")
				.append(id).append(", tipo=").append(tipo).append(", prefijo=")
				.append(prefijo).append(", active=").append(active).append("]");
		return builder.toString();
	}
	
}
	
	
