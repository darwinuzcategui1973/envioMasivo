package com.desige.webDocuments.files.forms;

import java.io.Serializable;
import java.util.ArrayList;

import com.desige.webDocuments.utils.beans.SuperActionForm;

/**
 * Title: grupoForm.java <br/>
 * Copyright: (c) 2004 Focus Consulting C.A.<br/>
 * @author Ing. Roger Rodríguez
 * @version WebDocuments v3.0
 * <br/>
 * Changes:<br/> 
 * <ul>
 *      <li>25/06/2004 (RR) Creation </li>
 </ul>
 */
public class DocumentForm extends SuperActionForm implements Serializable {
	
	public static final int TYPE_STRING = 1;
	public static final int TYPE_NUMERIC = 2;
	public static final int TYPE_DATE = 3;
	
    private String id;
    private String etiqueta01;
    private String etiqueta02;
    private int tipo;
    private int longitud;
    private String entrada;
    private String  valores;
    private String  condicion;
    private int editable;
    private int visible;
    private int criterio;
    private int auditable;
    private int orden;
    private int imprimir;
    private int location;
    
    public void reset(){
    	setId("");
    	setEtiqueta01("");
    	setEtiqueta02("");
    	setTipo(1);
    	setLongitud(0);
    	setEntrada("");
    	setValores("");
    	setCondicion("");
    	setEditable(1);
    	setVisible(1);
    	setCriterio(0);
    	setAuditable(0);
    	setOrden(0);
    	setImprimir(0);
    	setLocation(0);
    }

	public String getCondicion() {
		return condicion;
	}

	public void setCondicion(String condicion) {
		this.condicion = condicion;
	}

	public int getEditable() {
		return editable;
	}

	public void setEditable(int editable) {
		this.editable = editable;
	}

	public String getEntrada() {
		return entrada==null?"":entrada;
	}

	public void setEntrada(String entrada) {
		this.entrada = entrada;
	}

	public String getEtiqueta(String language) {
		return (language.equals("es")?etiqueta02:etiqueta01);
	}
	
	public String getEtiqueta01() {
		return etiqueta01;
	}

	public void setEtiqueta01(String etiqueta01) {
		this.etiqueta01 = etiqueta01;
	}

	public String getEtiqueta02() {
		return etiqueta02;
	}

	public void setEtiqueta02(String etiqueta02) {
		this.etiqueta02 = etiqueta02;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public int getLongitud() {
		return longitud;
	}

	public void setLongitud(int longitud) {
		this.longitud = longitud;
	}

	public int getTipo() {
		return tipo==0?1:tipo;
	}

	public void setTipo(int tipo) {
		this.tipo = tipo;
	}

	public int getVisible() {
		return visible;
	}

	public void setVisible(int visible) {
		this.visible = visible;
	}

	public String getValores() {
		return valores;
	}

	public void setValores(String valores) {
		this.valores = valores;
	}

	public int getAuditable() {
		return auditable;
	}

	public void setAuditable(int auditable) {
		this.auditable = auditable;
	}

	public int getCriterio() {
		return criterio;
	}

	public void setCriterio(int criterio) {
		this.criterio = criterio;
	}

	public int getOrden() {
		return orden;
	}

	public void setOrden(int orden) {
		this.orden = orden;
	}

	public int getImprimir() {
		return imprimir;
	}

	public void setImprimir(int imprimir) {
		this.imprimir = imprimir;
	}

	public int getLocation() {
		return location;
	}

	public void setLocation(int location) {
		this.location = location;
	}

    
}
