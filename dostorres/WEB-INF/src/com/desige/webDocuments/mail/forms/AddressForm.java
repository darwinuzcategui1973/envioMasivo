package com.desige.webDocuments.mail.forms;

import java.io.Serializable;

import com.desige.webDocuments.utils.beans.SuperActionForm;

/**
 * Created by IntelliJ IDEA.
 * User: ncrespo
 * Date: 05/10/2004
 * Time: 08:59:32 PM
 * To change this template use File | Settings | File Templates.
 */
public class AddressForm extends SuperActionForm implements Serializable {
    private String idAddress;
    private String nombre;
    private String apellido;
    private String email;
    private boolean selected;

    public AddressForm (){
        
    }

    public AddressForm (String idAddress, String nombre, String apellido, String email){
        setIdAddress(idAddress);
        setNombre(nombre);
        setEmail(email);
        setApellido(apellido);        
    }

    public String getIdAddress() {
        return idAddress;
    }

    public void setIdAddress(String idAddress) {
        this.idAddress = idAddress;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
    public void cleanForm(){
        setIdAddress("");
        setNombre("");
        setApellido("");
        setEmail("");
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }
}
