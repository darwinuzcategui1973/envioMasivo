package com.gestionEnvio.custon.dostorres.forms;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Date;

import com.desige.webDocuments.utils.beans.SuperActionForm;

public class BaseReciboForm extends SuperActionForm implements Serializable {
		/**
		 * 
		 */
		private static final long serialVersionUID = -3846248759442774077L;
		
		// datos de encabezado de aviso /recibo
		
		private String numero;
		private String codcli;
		private String nomcli;
		private String mes;
		private String anio;
		private String origen;
		private String status;
		private String tipodoc;//TIPODOC
		private String numlocal;//NUMREPORTEZ
		private String dateEmitida; //EMITIDA
		private String dateVence;  //VENCE
		private float total;
		private float total_mm;
		private float tasa;
		private float alicuota;
		private ArrayList listaCampos;
		private String id;
		
		
		
		
		// ini reflexion
		public Object get(String name) throws IllegalArgumentException,
				SecurityException, IllegalAccessException,
				InvocationTargetException, NoSuchMethodException {
			return this.getClass()
					.getDeclaredMethod("get".concat(name), new Class[] {})
					.invoke(this, new Object[] {});
		}

		public Object set(String name, String valor)
				throws IllegalArgumentException, SecurityException,
				IllegalAccessException, InvocationTargetException,
				NoSuchMethodException {
			return this
					.getClass()
					.getDeclaredMethod("set".concat(name),
							new Class[] { String.class })
					.invoke(this, new Object[] { valor });
		}

		public Object set(String name, int valor) throws IllegalArgumentException,
				SecurityException, IllegalAccessException,
				InvocationTargetException, NoSuchMethodException {
			return this
					.getClass()
					.getDeclaredMethod("set".concat(name),
							new Class[] { Integer.class })
					.invoke(this, new Object[] { valor });
		}

		public Object set(String name, Date valor) throws IllegalArgumentException,
				SecurityException, IllegalAccessException,
				InvocationTargetException, NoSuchMethodException {
			return this
					.getClass()
					.getDeclaredMethod("set".concat(name),
							new Class[] { Date.class })
					.invoke(this, new Object[] { valor });
		}

		// fin reflexion


		
	
		public String getNumero() {
			return numero;
		}
		public void setNumero(String numero) {
			this.numero = numero;
		}
		public String getCodcli() {
			return codcli;
		}
		public void setCodcli(String codcli) {
			this.codcli = codcli;
		}
		public String getNomcli() {
			return nomcli;
		}
		public void setNomcli(String nomcli) {
			this.nomcli = nomcli;
		}
		public String getMes() {
			return mes;
		}
		public void setMes(String mes) {
			this.mes = mes;
		}
		public String getAnio() {
			return anio;
		}
		public void setAnio(String anio) {
			this.anio = anio;
		}
		public String getOrigen() {
			return origen;
		}
		public void setOrigen(String origen) {
			this.origen = origen;
		}
		public String getStatus() {
			return status;
		}
		public void setStatus(String status) {
			this.status = status;
		}
		public String getTipodoc() {
			return tipodoc;
		}
		public void setTipodoc(String tipodoc) {
			this.tipodoc = tipodoc;
		}
		public String getNumlocal() {
			return numlocal;
		}
		public void setNumlocal(String numlocal) {
			this.numlocal = numlocal;
		}
		public String getDateEmitida() {
			return dateEmitida;
		}
		public void setDateEmitida(String dateEmitida) {
			this.dateEmitida = dateEmitida;
		}
		public String getDateVence() {
			return dateVence;
		}
		public void setDateVence(String dateVence) {
			this.dateVence = dateVence;
		}
		public float getTotal() {
			return total;
		}
		public void setTotal(float total) {
			this.total = total;
		}
		public float getTotal_mm() {
			return total_mm;
		}
		public void setTotal_mm(float total_mm) {
			this.total_mm = total_mm;
		}
		public float getTasa() {
			return tasa;
		}
		public void setTasa(float tasa) {
			this.tasa = tasa;
		}
		public float getAlicuota() {
			return alicuota;
		}
		public void setAlicuota(float alicuota) {
			this.alicuota = alicuota;
		}
		public static long getSerialversionuid() {
			return serialVersionUID;
		}
		
		public ArrayList getListaCampos() {
			return listaCampos;
		}

		public void setListaCampos(ArrayList listaCampos) {
			this.listaCampos = listaCampos;
		}
		public String getId() {
			return id;
		}

		public void setId(String id) {
			this.id = id;
		}
		@Override
		public String toString() {
			return "BasesReciboForm [numero=" + numero + ", codcli=" + codcli + ", nomcli=" + nomcli + ", mes=" + mes
					+ ", anio=" + anio + ", origen=" + origen + ", status=" + status + ", tipodoc=" + tipodoc
					+ ", numlocal=" + numlocal + ", dateEmitida=" + dateEmitida + ", dateVence=" + dateVence
					+ ", total=" + total + ", total_mm=" + total_mm + ", tasa=" + tasa + ", alicuota=" + alicuota +", listaCampos=" + listaCampos + "]";
		}
		
		//.append(", listaCampos=").append(listaCampos)	.append("]");
	}