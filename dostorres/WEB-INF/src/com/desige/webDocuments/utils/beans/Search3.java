package com.desige.webDocuments.utils.beans;

import java.io.Serializable;

/**
 * Title: Search3.java <br/>
 * Copyright: (c) 2004 Focus Consulting C.A.<br/>
 * @author Ing. Roger Rodríguez
 * @version WebDocuments v3.0
 * <br/>
 * Changes:<br/> 
 * <ul>
 *      <li>14/07/2004 (RR) Creation </li>
 </ul>
 */
public class Search3 implements Serializable {
    private String id;
    private String name;
    private String apellido;
    private String descript;
    private String aditionalInfo;

    public Search3(){

    }
    public Search3(String id,String name, String apellido, String descrip){
        this.id = id;
        this.name = name;
        this.apellido = apellido;
        this.descript = descrip;
    }
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDescript() {
        return descript;
    }

    public void setDescript(String descript) {
        this.descript = descript;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public String toString() {
        StringBuffer data = new StringBuffer(60);
        data.append("Id: ").append(getId()).append(" Apellido:  ").append(getApellido());
        data.append(" Nombre: ").append(getName()).append(" Descript: ").append(getDescript());
        return data.toString();
    }

    public String getAditionalInfo() {
        return aditionalInfo;
    }

    public void setAditionalInfo(String aditionalInfo) {
        this.aditionalInfo = aditionalInfo;
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Search3 search3 = (Search3) o;

        if (aditionalInfo != null ? !aditionalInfo.equals(search3.aditionalInfo) : search3.aditionalInfo != null)
            return false;
        if (apellido != null ? !apellido.equals(search3.apellido) : search3.apellido != null) return false;
        if (descript != null ? !descript.equals(search3.descript) : search3.descript != null) return false;
        if (id != null ? !id.equals(search3.id) : search3.id != null) return false;
        if (name != null ? !name.equals(search3.name) : search3.name != null) return false;

        return true;
    }

    public int hashCode() {
        int result;
        result = (id != null ? id.hashCode() : 0);
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (apellido != null ? apellido.hashCode() : 0);
        result = 31 * result + (descript != null ? descript.hashCode() : 0);
        result = 31 * result + (aditionalInfo != null ? aditionalInfo.hashCode() : 0);
        return result;
    }
}
