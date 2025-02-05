package com.focus.custom.delsur.util;

/**
 * Tipo de dato numerado para fijar la lista de posibles estatus de un recaudo.
 * 
 * @author felipe.rojas
 *
 */
public enum StatusRecaudos {
	APROBADO("A"),
	RECHAZADO("E"),
	NO_LEIDO("N"),
	RECIBIDO("R");
	
	private String value;
	
	private StatusRecaudos(String value) {
		// TODO Auto-generated constructor stub
		this.value = value;
	}
	
	public String getValue() {
		return value;
	}
}
