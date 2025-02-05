package com.desige.webDocuments.bean;

import java.util.List;

public class PdfSacopRequest {

	private String titleSacop;
	
	private String idSacop;

	private String emisor;
	private String estado;
	private String tipo;

	private String fechaEmision;
	private String responsableArea;
	private String areaAfectada;

	private String fechaEstimada;
	private String fechaReal;
	private String fechaHallazgo;

	private String clasificacion;
	private String fechaVerificacion;
	private String fechaCierre;

	private String solicitante;
	private String nameRelatedDocument;
	private String nameFile;

	private String normIso;

	private String relacionado;

	private List<PdfSacopDetailRequest> listSacops;

	private List<PdfDocumentDetailRequest> listRelationDocs;

	private String noconformidadesref;
	private String noconformidades;

	private String comentario;
	private String posibleCausaTitle;
	private String posibleCausa;
	private String accionesRecomendadas;
	private String accionesCorrectivaInformaTitle;
	private String accionesCorrectivaInforma;
	private String requisitoAplicable;
	private String procesos;
	private String causaRaiz;
	private String causaRaizUno;
	private String causaRaizDos;
	private String causaRaizTres;
	private String acciones;
	private String accionesObservaciones;
	private String cumplieron;
	private String cumplieronDetalle;
	private String eficaz;
	private String eficazDetalle;
	private String seguimiento;
	private String seguimientoFecha;
	private String decripcionAccionPrincipal;
	private String requireTracking;

	public String getTitleSacop() {
		return titleSacop;
	}

	public void setTitleSacop(String titleSacop) {
		this.titleSacop = titleSacop;
	}

	public String getEmisor() {
		return emisor;
	}

	public void setEmisor(String emisor) {
		this.emisor = emisor;
	}

	public String getEstado() {
		return estado;
	}

	public void setEstado(String estado) {
		this.estado = estado;
	}

	public String getTipo() {
		return tipo;
	}

	public void setTipo(String tipo) {
		this.tipo = tipo;
	}

	public String getFechaEmision() {
		return fechaEmision;
	}

	public void setFechaEmision(String fechaEmision) {
		this.fechaEmision = fechaEmision;
	}

	public String getResponsableArea() {
		return responsableArea;
	}

	public void setResponsableArea(String responsableArea) {
		this.responsableArea = responsableArea;
	}

	public String getAreaAfectada() {
		return areaAfectada;
	}

	public void setAreaAfectada(String areaAfectada) {
		this.areaAfectada = areaAfectada;
	}

	public String getFechaEstimada() {
		return fechaEstimada;
	}

	public void setFechaEstimada(String fechaEstimada) {
		this.fechaEstimada = fechaEstimada;
	}

	public String getFechaReal() {
		return fechaReal;
	}

	public void setFechaReal(String fechaReal) {
		this.fechaReal = fechaReal;
	}

	public String getFechaHallazgo() {
		return fechaHallazgo;
	}

	public void setFechaHallazgo(String fechaHallazgo) {
		this.fechaHallazgo = fechaHallazgo;
	}

	public String getClasificacion() {
		return replaceText(clasificacion);
	}

	public void setClasificacion(String clasificacion) {
		this.clasificacion = clasificacion;
	}

	public String getFechaVerificacion() {
		return fechaVerificacion;
	}

	public void setFechaVerificacion(String fechaVerificacion) {
		this.fechaVerificacion = fechaVerificacion;
	}

	public String getFechaCierre() {
		return fechaCierre;
	}

	public void setFechaCierre(String fechaCierre) {
		this.fechaCierre = fechaCierre;
	}

	public String getSolicitante() {
		return replaceText(solicitante);
	}

	public void setSolicitante(String solicitante) {
		this.solicitante = solicitante;
	}

	public String getRelacionado() {
		return replaceText(relacionado);
	}

	public void setRelacionado(String relacionado) {
		this.relacionado = relacionado;
	}

	public List<PdfSacopDetailRequest> getListSacops() {
		return listSacops;
	}

	public void setListSacops(List<PdfSacopDetailRequest> listSacops) {
		this.listSacops = listSacops;
	}

	public String getNoconformidadesref() {
		return replaceText(noconformidadesref);
	}

	public void setNoconformidadesref(String noconformidadesref) {
		this.noconformidadesref = noconformidadesref;
	}

	public String getNoconformidades() {
		return replaceText(noconformidades);
	}

	public void setNoconformidades(String noconformidades) {
		this.noconformidades = noconformidades;
	}

	public String getComentario() {
		return replaceText(comentario);
	}

	public void setComentario(String comentario) {
		this.comentario = comentario;
	}

	public String getPosibleCausaTitle() {
		return replaceText(posibleCausaTitle);
	}

	public void setPosibleCausaTitle(String posibleCausaTitle) {
		this.posibleCausaTitle = posibleCausaTitle;
	}

	public String getPosibleCausa() {
		return replaceText(posibleCausa);
	}

	public void setPosibleCausa(String posibleCausa) {
		this.posibleCausa = posibleCausa;
	}

	public String getAccionesRecomendadas() {
		return replaceText(accionesRecomendadas);
	}

	public void setAccionesRecomendadas(String accionesRecomendadas) {
		this.accionesRecomendadas = accionesRecomendadas;
	}

	public String getAccionesCorrectivaInformaTitle() {
		return replaceText(accionesCorrectivaInformaTitle);
	}

	public void setAccionesCorrectivaInformaTitle(String accionesCorrectivaInformaTitle) {
		this.accionesCorrectivaInformaTitle = accionesCorrectivaInformaTitle;
	}

	public String getAccionesCorrectivaInforma() {
		//return replaceText(accionesCorrectivaInforma);
		return replaceTextEspacios(accionesCorrectivaInforma);
	}

	public void setAccionesCorrectivaInforma(String accionesCorrectivaInforma) {
		this.accionesCorrectivaInforma = accionesCorrectivaInforma;
	}

	public String getRequisitoAplicable() {
		return replaceText(requisitoAplicable);
	}

	public void setRequisitoAplicable(String requisitoAplicable) {
		this.requisitoAplicable = requisitoAplicable;
	}

	public String getProcesos() {
		return replaceText(procesos);
	}

	public void setProcesos(String procesos) {
		this.procesos = procesos;
	}

	public String getCausaRaizUno() {
		return replaceText(causaRaizUno);
	}

	public void setCausaRaizUno(String causaRaizUno) {
		this.causaRaizUno = causaRaizUno;
	}

	public String getCausaRaizDos() {
		return replaceText(causaRaizDos);
	}

	public void setCausaRaizDos(String causaRaizDos) {
		this.causaRaizDos = causaRaizDos;
	}

	public String getCausaRaizTres() {
		return replaceText(causaRaizTres);
	}

	public void setCausaRaizTres(String causaRaizTres) {
		this.causaRaizTres = causaRaizTres;
	}

	public String getAcciones() {
		return acciones;
	}

	public void setAcciones(String acciones) {
		this.acciones = acciones;
	}

	public String getAccionesObservaciones() {
		return replaceText(accionesObservaciones);
	}

	public void setAccionesObservaciones(String accionesObservaciones) {
		this.accionesObservaciones = accionesObservaciones;
	}

	public String getCumplieron() {
		return replaceText(cumplieron);
	}

	public void setCumplieron(String cumplieron) {
		this.cumplieron = cumplieron;
	}

	public String getCumplieronDetalle() {
		return replaceText(cumplieronDetalle);
	}

	public void setCumplieronDetalle(String cumplieronDetalle) {
		this.cumplieronDetalle = cumplieronDetalle;
	}

	public String getEficaz() {
		return replaceText(eficaz);
	}

	public void setEficaz(String eficaz) {
		this.eficaz = eficaz;
	}

	public String getEficazDetalle() {
		return replaceText(eficazDetalle);
	}

	public void setEficazDetalle(String eficazDetalle) {
		this.eficazDetalle = eficazDetalle;
	}

	public String getSeguimiento() {
		return replaceText(seguimiento);
	}

	public void setSeguimiento(String seguimiento) {
		this.seguimiento = seguimiento;
	}

	public String getSeguimientoFecha() {
		return replaceText(seguimientoFecha);
	}

	public void setSeguimientoFecha(String seguimientoFecha) {
		this.seguimientoFecha = seguimientoFecha;
	}
	
	public String getIdSacop() {
		return idSacop;
	}

	public void setIdSacop(String idSacop) {
		this.idSacop = idSacop;
	}

	
	
	public List<PdfDocumentDetailRequest> getListRelationDocs() {
		return listRelationDocs;
	}

	public void setListRelationDocs(List<PdfDocumentDetailRequest> listRelationDocs) {
		this.listRelationDocs = listRelationDocs;
	}
	
	public String getDecripcionAccionPrincipal() {
		return decripcionAccionPrincipal;
	}

	public void setDecripcionAccionPrincipal(String decripcionAccionPrincipal) {
		this.decripcionAccionPrincipal = decripcionAccionPrincipal;
	}
	
	public String getNameRelatedDocument() {
		return nameRelatedDocument;
	}

	public void setNameRelatedDocument(String nameRelatedDocument) {
		this.nameRelatedDocument = nameRelatedDocument;
	}

	public String getCausaRaiz() {
		return causaRaiz;
	}

	public void setCausaRaiz(String causaRaiz) {
		this.causaRaiz = causaRaiz;
	}
	
	public String getNameFile() {
		return nameFile;
	}

	public void setNameFile(String nameFile) {
		this.nameFile = nameFile;
	}
	
	public String getRequireTracking() {
		return requireTracking;
	}

	public void setRequireTracking(String requireTracking) {
		this.requireTracking = requireTracking;
	}
	
	public String getNormIso() {
		return normIso;
	}

	public void setNormIso(String normIso) {
		this.normIso = normIso;
	}

	private String replaceText(String value) {
		try {
			value = value.replaceAll("\n", " ");
			value = value.replaceAll("\t", " ");
			while(value.indexOf("  ")!=-1) {
				value = value.replaceAll("  ", " ");
			}
			return value;
		} catch(Exception e) {
			return " ";
		}
	}
	

	private String replaceTextEspacios(String value) {
		try {
			value = value.replaceAll("\n", ",");
			value = value.replaceAll("\t", " ");
			
			while(value.indexOf("  ")!=-1) {
				value = value.replaceAll("  ", " ");
			}
				value = value.replaceAll(", , ", ".\n");
				value = value.replaceAll(",, ", " ");
			return value.substring(4,value.length());
		} catch(Exception e) {
			return " ";
		}
	}
	
}
