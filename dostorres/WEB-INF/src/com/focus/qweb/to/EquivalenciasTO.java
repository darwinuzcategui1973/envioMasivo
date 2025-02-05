package com.focus.qweb.to;

import java.util.Arrays;

import com.focus.qweb.interfaces.ObjetoTO;

public class EquivalenciasTO implements ObjetoTO {

	private static final long serialVersionUID = 1L;

	public static String EQUIVALENCIA_QWEB = "Archivo en formato xls sugerido por Qwebdocuments";
	public static int TOTAL_EQUIVALENCIAS_DEFINIDAS = 19; // sin contar los
															// campos
															// adicionales

	private String indice;
	private String nombre;
	private String campo;
	private String posicion;
	private String columna;
	private String valor;
	private String indexar;
	private String activo;

	private String[] indiceArray;
	private String[] campoArray;
	private String[] posicionArray;
	private String[] columnaArray;
	private String[] valorArray;
	private String[] indexarArray;

	EquivalenciasTO equivalenciasTO;

	/**
     * 
     */
	public EquivalenciasTO() {
	}

	/**
	 * 
	 * @param nombre
	 * @param activo
	 */
	public EquivalenciasTO(String nombre, String activo) {
		this.nombre = nombre;
		this.activo = activo;
	}

	/**
	 * 
	 * @param ind
	 * @return
	 */
	public EquivalenciasTO get(int ind) {
		equivalenciasTO = null;
		if (ind < indiceArray.length) {
			equivalenciasTO = new EquivalenciasTO();
			equivalenciasTO.setIndice(indiceArray[ind]);
			equivalenciasTO.setNombre(this.getNombre());
			equivalenciasTO.setCampo(campoArray[ind]);
			equivalenciasTO.setPosicion(posicionArray[ind]);
			equivalenciasTO.setColumna(columnaArray[ind]);
			equivalenciasTO.setValor(valorArray[ind]);
			equivalenciasTO.setIndexar(indexarArray[ind]);
			equivalenciasTO.setActivo(this.getActivo());
		}
		return equivalenciasTO;
	}

	public String getCampo() {
		return campo;
	}

	public void setCampo(String campo) {
		this.campo = campo;
	}

	public String getColumna() {
		return columna;
	}

	public void setColumna(String columna) {
		this.columna = columna;
	}

	public String getIndexar() {
		return indexar == null || indexar.trim().equals("") ? "0" : indexar;
	}

	public void setIndexar(String indexar) {
		this.indexar = indexar;
	}

	public String getIndice() {
		return indice == null || indice.trim().equals("") ? "0" : indice;
	}

	public void setIndice(String indice) {
		this.indice = indice;
	}

	public String getPosicion() {
		return posicion == null || posicion.trim().equals("") ? "0" : posicion;
	}

	public void setPosicion(String posicion) {
		this.posicion = posicion;
	}

	public String getValor() {
		return valor;
	}

	public void setValor(String valor) {
		this.valor = valor;
	}

	public String[] getCampoArray() {
		return campoArray;
	}

	public void setCampoArray(String[] campoArray) {
		this.campoArray = campoArray;
	}

	public String[] getColumnaArray() {
		return columnaArray;
	}

	public void setColumnaArray(String[] columnaArray) {
		this.columnaArray = columnaArray;
	}

	public String[] getIndexarArray() {
		return indexarArray;
	}

	public void setIndexarArray(String[] indexarArray) {
		this.indexarArray = indexarArray;
	}

	public String[] getIndiceArray() {
		return indiceArray;
	}

	public void setIndiceArray(String[] indiceArray) {
		this.indiceArray = indiceArray;
	}

	public String[] getPosicionArray() {
		return posicionArray;
	}

	public void setPosicionArray(String[] posicionArray) {
		this.posicionArray = posicionArray;
	}

	public String[] getValorArray() {
		return valorArray;
	}

	public void setValorArray(String[] valorArray) {
		this.valorArray = valorArray;
	}

	public String getActivo() {
		return activo == null || activo.trim().equals("") ? "0" : activo;
	}

	public void setActivo(String activo) {
		this.activo = activo;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("EquivalenciasTO [indice=").append(indice)
				.append(", nombre=").append(nombre).append(", campo=")
				.append(campo).append(", posicion=").append(posicion)
				.append(", columna=").append(columna).append(", valor=")
				.append(valor).append(", indexar=").append(indexar)
				.append(", activo=").append(activo).append(", indiceArray=")
				.append(Arrays.toString(indiceArray)).append(", campoArray=")
				.append(Arrays.toString(campoArray)).append(", posicionArray=")
				.append(Arrays.toString(posicionArray))
				.append(", columnaArray=")
				.append(Arrays.toString(columnaArray)).append(", valorArray=")
				.append(Arrays.toString(valorArray)).append(", indexarArray=")
				.append(Arrays.toString(indexarArray))
				.append(", equivalenciasTO=").append(equivalenciasTO)
				.append("]");
		return builder.toString();
	}

}
