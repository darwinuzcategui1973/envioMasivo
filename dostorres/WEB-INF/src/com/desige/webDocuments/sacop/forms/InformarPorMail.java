package com.desige.webDocuments.sacop.forms;

import java.io.Serializable;

import com.desige.webDocuments.utils.beans.SuperActionForm;

/**
 * Created by IntelliJ IDEA.
 * User: srodriguez
 * Date: 27/01/2006
 * Time: 08:56:44 AM
 * To change this template use File | Settings | File Templates.
 */
public class InformarPorMail  extends SuperActionForm implements Serializable {
private String id;
private String correo;
private String comentarios;
private String nombre;
    public String getId(){
           return id;
    }
    public void setId(String id){
           this.id=id;
    }

    public String getCorreo(){
           return correo;
    }
    public void setCorreo(String correo){
           this.correo=correo;
    }

    public String getComentarios(){
           return comentarios;
    }
    public void setComentarios(String comentarios){
           this.comentarios=comentarios;
    }

    public String getNombre(){
           return nombre;
    }
    public void setNombre(String nombre){
           this.nombre=nombre;
    }

}
