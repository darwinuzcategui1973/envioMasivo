package com.desige.webDocuments.area.forms;

import java.io.Serializable;

import com.desige.webDocuments.utils.beans.SuperActionForm;
import com.focus.qweb.to.AreaTO;

/**
 * Created by IntelliJ IDEA.
 * User: Administrador
 * Date: 20/03/2006
 * Time: 10:29:06 AM
 * To change this template use File | Settings | File Templates.
 */

public class Area extends SuperActionForm implements Serializable {
    private long idarea;
    private String id;
    private String area;
    private String prefijo;
    private Byte active;
    
    public Area() {
    	
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

    public long getIdarea() {
        return idarea;
    }

    public void setIdarea(long idarea) {
        this.idarea = idarea;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }
    
    public String getPrefijo() {
		return prefijo==null?"":prefijo;
	}

	public void setPrefijo(String prefijo) {
		this.prefijo = prefijo;
	}

	public void cleanForm(){
        setArea("");
        setPrefijo("");
        //setIdarea(0);
    }

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Area [idarea=").append(idarea).append(", id=")
				.append(id).append(", area=").append(area).append(", prefijo=")
				.append(prefijo).append(", active=").append(active).append("]");
		return builder.toString();
	}
	
	
}