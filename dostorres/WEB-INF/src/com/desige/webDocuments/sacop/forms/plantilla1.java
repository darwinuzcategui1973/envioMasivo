package com.desige.webDocuments.sacop.forms;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collection;

import org.apache.struts.upload.FormFile;

import com.desige.webDocuments.document.forms.BaseDocumentForm;
import com.desige.webDocuments.utils.beans.SuperActionForm;

/**
 * Title: plantilla1.java <br/>
 * Copyright: (c) 2004 Desige Servicios de Computaci�n<br/>
 * 
 * @author Ing. Nelson Crespo (NC)
 * @author Ing. Sim�n Rodriguez. (SR)
 * @version WebDocuments v1.0 <br/>
 *          Changes:<br/>
 *          <ul>
 *          <li>28/03/2004 (NC) Creation</li>
 *          <li>02/08/2005 (SR) Se creo bean repclave</li>
 * 
 *          </ul>
 */
public class plantilla1 extends SuperActionForm implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -346204570031374L;

	private String id;
	private String idplanillasacop1;
	private String idplanillasacop;
	private String user;
	private String apellidos;
	private String nombres;
	private String cargo;
	private String correcpreven;
	private String descripcion;
	private String causasnoconformidad;
	private String accionrecomendada;
	private String edodelsacop;
	private String edodelsacoptxt;
	private String origensacop;
	private String origensacoptxt;
	private String fechaEmision;
	private String fechaSacops1;
	private String fechaEstimada;
	private String fechaReal;
	private String noaceptada;
	private String rechazoapruebo;
	private String numerodeaccion;
	private String accionobservacion;
	private String accionobservaciontxt;
	private FormFile archivoTecnica;
	private Object[] normsisoSelected;
	//modificacion normISO para Setear
	private Object[] normISO;
	private Object[] proceafectadosSelected;
	private Object[] usersSelected;
	private String accionesEstablecidas;
	private String accionesEstablecidastxt;
	private String eliminarcausaraiz;
	private String eliminarcausaraiztxt;
	private String sacopnum;
	private long usernotificado;
	private String usernotificadotxt;
	private String areafectadatxt;
	// areaResponsable
	private String contentType;
	private FormFile nameFile;
	private String fileNameFisico;
	private String path;
	private String fechaculminar;
	private String sacop_relacionadas;
	private boolean selected;
	private String noconformidades;
	private String noconformidadesref;
	private long idplanillasacop1esqueleto;
	private String continuar_Sacop_Intouch;
	private String habilitadoEsqueleto;
	private int estado;
	private int idClasificacion;
	private String fechaWhenDiscovered;
	private String nameRelatedDocument;
	private Collection<BaseDocumentForm> noConformidadesDetail;
	private Collection<Plantilla1BD> sacopRelacionadasDetail;
	private String comntresponsablecerrar;
	private boolean deleteArchivoTecnica;
	private String nombreClasificacionSACOP;
	private String fechaVerificacion;
	private String fechaCierre;
	private String idDocumentRelated;
	private String idDocumentAssociate;
	private String numVerDocumentAssociate;
	private String nameDocumentAssociate;
	private String requireTracking;
	private String requireTrackingDate;
	private String trackingSacop;
	private String numberTrackingSacop;
	private Integer idRegisterGenerated;
	private String descripcionAccionPrincipal;

	private String usuarioSacops1;
	private String solicitantetxt;
	private String cargoSolicitante;
	
	
	// vriable que se usa en sacop_intouch
	private Object[] sacop_intouch;

	public String getFechaculminar() {
		return fechaculminar;
	}

	public void setFechaculminar(String fechaculminar) {
		this.fechaculminar = fechaculminar;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getFileNameFisico() {
		return fileNameFisico;
	}

	public void setFileNameFisico(String fileNameFisico) {
		this.fileNameFisico = fileNameFisico;
	}

	// aqui se carga el archivo como tal
	public FormFile getNameFile() {
		return nameFile;
	}

	public void setNameFile(FormFile nameFile) {
		this.nameFile = nameFile;
	}

	public void setNameRelatedDocument(String nameRelatedDocument) {
		this.nameRelatedDocument = nameRelatedDocument;
	}

	public String getNameRelatedDocument() {
		return nameRelatedDocument;
	}

	// -----------------------------------------

	public String getContextType() {
		return contentType;
	}

	public void setContextType(String contentType) {
		this.contentType = contentType;
	}

	public String getAreafectadatxt() {
		return areafectadatxt;
	}

	public void setAreafectadatxt(String areafectadatxt) {
		this.areafectadatxt = areafectadatxt;
	}

	public String getUsernotificadotxt() {
		return usernotificadotxt;
	}

	public void setUsernotificadotxt(String usernotificadotxt) {
		this.usernotificadotxt = usernotificadotxt;
	}

	public long getUsernotificado() {
		return usernotificado;
	}

	public void setUsernotificado(long usernotificado) {
		this.usernotificado = usernotificado;
	}

	public String getSacopnum() {
		return sacopnum;
	}

	public void setSacopnum(String sacopnum) {
		this.sacopnum = sacopnum;
	}

	public String getEliminarcausaraiz() {
		return eliminarcausaraiz;
	}

	public void setEliminarcausaraiz(String eliminarcausaraiz) {
		this.eliminarcausaraiz = eliminarcausaraiz;
	}

	public String getEliminarcausaraiztxt() {
		return eliminarcausaraiztxt;
	}

	public void setEliminarcausaraiztxt(String eliminarcausaraiztxt) {
		this.eliminarcausaraiztxt = eliminarcausaraiztxt;
	}

	public String getAccionesEstablecidas() {
		return accionesEstablecidas;
	}

	public void setAccionesEstablecidas(String accionesEstablecidas) {
		this.accionesEstablecidas = accionesEstablecidas;
	}

	public String getAccionesEstablecidastxt() {
		return accionesEstablecidastxt;
	}

	public void setAccionesEstablecidastxt(String accionesEstablecidastxt) {
		this.accionesEstablecidastxt = accionesEstablecidastxt;
	}

	public String getAccionobservaciontxt() {
		return accionobservaciontxt;
	}

	public void setAccionobservaciontxt(String accionobservaciontxt) {
		this.accionobservaciontxt = accionobservaciontxt;
	}

	public String getFechaReal() {
		return fechaReal;
	}
	
	
	public void setFechaReal(String fechaReal) {
		this.fechaReal = fechaReal;
	}

	public String getAccionobservacion() {
		return accionobservacion;
	}

	public void setAccionobservacion(String accionobservacion) {
		this.accionobservacion = accionobservacion;
	}

	public FormFile getArchivoTecnica() {
		return archivoTecnica;
	}

	public void setArchivoTecnica(FormFile archivoTecnica) {
		this.archivoTecnica = archivoTecnica;
	}

	public String getNumerodeaccion() {
		return numerodeaccion;
	}

	public void setNumerodeaccion(String numerodeaccion) {
		this.numerodeaccion = numerodeaccion;
	}

	public Object[] getUsersSelected() {
		return usersSelected;
	}

	public void setUsersSelected(Object[] usersSelected) {
		this.usersSelected = usersSelected;
	}

	public Object[] getProceafectadosSelected() {
		return proceafectadosSelected;
	}

	public void setProceafectadosSelected(Object[] proceafectadosSelected) {
		this.proceafectadosSelected = proceafectadosSelected;
	}

	public Object[] getNormsisoSelected() {
		return normsisoSelected;
	}

	public void setNormsisoSelected(Object[] normsisoSelected) {
		this.normsisoSelected = normsisoSelected;
	}
	
	public Object[] getNormISO() {
		return normISO;
	}

	public void setNormISO(Object[] normISO) {
		this.normISO = normISO;
	}


	public String getRechazoapruebo() {
		return rechazoapruebo;
	}

	public void setRechazoapruebo(String rechazoapruebo) {
		this.rechazoapruebo = rechazoapruebo;
	}

	public String getFechaEmision() {
		return fechaEmision;
	}

	public void setFechaEmision(String fechaEmision) {
		this.fechaEmision = fechaEmision;
	}

	
	public String getFechaSacops1() {
		return fechaSacops1;
	}

	public void setFechaSacops1(String fechaSacops1) {
		this.fechaSacops1 = fechaSacops1;
	}

	public String getFechaEstimada() {
		return fechaEstimada;
	}

	public void setFechaEstimada(String fechaEstimada) {
		this.fechaEstimada = fechaEstimada;
	}

	public String getNoaceptada() {
		return noaceptada;
	}

	public void setNoaceptada(String noaceptada) {
		this.noaceptada = noaceptada;
	}

	public String getOrigensacop() {
		return origensacop;
	}

	public void setOrigensacop(String origensacop) {
		this.origensacop = origensacop;
	}

	public String getOrigensacoptxt() {
		return origensacoptxt;
	}

	public void setOrigensacoptxt(String origensacoptxt) {
		this.origensacoptxt = origensacoptxt;
	}

	public String getEdodelsacop() {
		return edodelsacop;
	}

	public void setEdodelsacop(String edodelsacop) {
		this.edodelsacop = edodelsacop;
	}

	public String getEdodelsacoptxt() {
		return edodelsacoptxt;
	}

	public void setEdodelsacoptxt(String edodelsacoptxt) {
		this.edodelsacoptxt = edodelsacoptxt;
	}

	public String getAccionrecomendada() {
		return accionrecomendada;
	}

	public void setAccionrecomendada(String accionrecomendada) {
		this.accionrecomendada = accionrecomendada;
	}

	public String getCausasnoconformidad() {
		return causasnoconformidad;
	}

	public void setCausasnoconformidad(String causasnoconformidad) {
		this.causasnoconformidad = causasnoconformidad;
	}

	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	public String getCorrecpreven() {
		return correcpreven;
	}

	public void setCorrecpreven(String correcpreven) {
		this.correcpreven = correcpreven;
	}

	public String getApellidos() {
		return apellidos;
	}

	public void setApellidos(String apellidos) {
		this.apellidos = apellidos;
	}

	public String getNombres() {
		return nombres;
	}

	public void setNombres(String nombres) {
		this.nombres = nombres;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getIdplanillasacop1() {
		return idplanillasacop1;
	}

	public void setIdplanillasacop1(String idplanillasacop1) {
		this.idplanillasacop1 = idplanillasacop1;
	}

	public String getIdplanillasacop() {
		return idplanillasacop;
	}

	public void setIdplanillasacop(String idplanillasacop) {
		this.idplanillasacop = idplanillasacop;
	}

	public void cleanForm() {
		setId("");
		setIdplanillasacop("");
		setUser("");
		setIdplanillasacop1("");
		setNormsisoSelected(null);
		setNormISO(null);
		setProceafectadosSelected(null);
		setUsersSelected(null);
		setCorrecpreven("");
		setEdodelsacop("");
		setNombres("");
		setApellidos("");
		setCargo("");
		setUser("");
		setOrigensacop("");
		setOrigensacoptxt("");
		setFechaEmision("");
		setFechaSacops1("");
		setDescripcion("");
		setCausasnoconformidad("");
		setAccionrecomendada("");
	}

	public String getCargo() {
		return cargo;
	}

	public void setCargo(String cargo) {
		this.cargo = cargo;
	}

	public String getSacop_relacionadas() {
		return sacop_relacionadas;
	}

	public void setSacop_relacionadas(String sacop_relacionadas) {
		this.sacop_relacionadas = sacop_relacionadas;
	}

	public boolean isSelected() {
		return selected;
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
	}

	public String getNoconformidades() {
		return noconformidades;
	}

	public void setNoconformidades(String noconformidades) {
		this.noconformidades = noconformidades;
	}

	public String getNoconformidadesref() {
		return noconformidadesref;
	}

	public void setNoconformidadesref(String noconformidadesref) {
		this.noconformidadesref = noconformidadesref;
	}

	public Object[] getSacop_intouch() {
		return sacop_intouch;
	}

	public void setSacop_intouch(Object[] sacop_intouch) {
		this.sacop_intouch = sacop_intouch;
	}

	public long getIdplanillasacop1esqueleto() {
		return idplanillasacop1esqueleto;
	}

	public void setIdplanillasacop1esqueleto(long idplanillasacop1esqueleto) {
		this.idplanillasacop1esqueleto = idplanillasacop1esqueleto;
	}

	public String getContinuar_Sacop_Intouch() {
		return continuar_Sacop_Intouch;
	}

	public void setContinuar_Sacop_Intouch(String continuar_Sacop_Intouch) {
		this.continuar_Sacop_Intouch = continuar_Sacop_Intouch;
	}

	public String getHabilitadoEsqueleto() {
		return habilitadoEsqueleto;
	}

	public void setHabilitadoEsqueleto(String habilitadoEsqueleto) {
		this.habilitadoEsqueleto = habilitadoEsqueleto;
	}

	public int getEstado() {
		return estado;
	}

	public void setEstado(int estado) {
		this.estado = estado;
	}

	public int getIdClasificacion() {
		return idClasificacion;
	}

	public void setIdClasificacion(int idClasificacion) {
		this.idClasificacion = idClasificacion;
	}

	public String getFechaWhenDiscovered() {
		return fechaWhenDiscovered;
	}

	public void setFechaWhenDiscovered(String fechaWhenDiscovered) {
		this.fechaWhenDiscovered = fechaWhenDiscovered;
	}

	public void setNoConformidadesDetail(
			Collection<BaseDocumentForm> noConformidadesDetail) {
		this.noConformidadesDetail = noConformidadesDetail;
	}

	public Collection<BaseDocumentForm> getNoConformidadesDetail() {
		return noConformidadesDetail;
	}

	public void setSacopRelacionadasDetail(
			Collection<Plantilla1BD> sacopRelacionadasDetail) {
		this.sacopRelacionadasDetail = sacopRelacionadasDetail;
	}

	public Collection<Plantilla1BD> getSacopRelacionadasDetail() {
		return sacopRelacionadasDetail;
	}

	public String getComntresponsablecerrar() {
		return comntresponsablecerrar;
	}

	public void setComntresponsablecerrar(String comntresponsablecerrar) {
		this.comntresponsablecerrar = comntresponsablecerrar;
	}

	public void setDeleteArchivoTecnica(boolean deleteArchivoTecnica) {
		this.deleteArchivoTecnica = deleteArchivoTecnica;
	}

	public boolean isDeleteArchivoTecnica() {
		return deleteArchivoTecnica;
	}

	public void setNombreClasificacionSACOP(String nombreClasificacionSACOP) {
		this.nombreClasificacionSACOP = nombreClasificacionSACOP;
	}

	public String getNombreClasificacionSACOP() {
		return nombreClasificacionSACOP;
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
	
	public String getIdDocumentRelated() {
		return idDocumentRelated;
	}

	public void setIdDocumentRelated(String idDocumentRelated) {
		this.idDocumentRelated = idDocumentRelated;
	}
	
	public String getUsuarioSacops1() {
		return usuarioSacops1;
	}

	public void setUsuarioSacops1(String usuarioSacops1) {
		this.usuarioSacops1 = usuarioSacops1;
	}

	public String getSolicitantetxt() {
		return solicitantetxt;
	}

	public void setSolicitantetxt(String solicitantetxt) {
		this.solicitantetxt = solicitantetxt;
	}
	
	

	public String getCargoSolicitante() {
		return cargoSolicitante;
	}

	public void setCargoSolicitante(String cargoSolicitante) {
		this.cargoSolicitante = cargoSolicitante;
	}

	public String getIdDocumentAssociate() {
		return idDocumentAssociate;
	}

	public void setIdDocumentAssociate(String idDocumentAssociate) {
		this.idDocumentAssociate = idDocumentAssociate;
	}

	public String getNumVerDocumentAssociate() {
		return numVerDocumentAssociate;
	}

	public void setNumVerDocumentAssociate(String numVerDocumentAssociate) {
		this.numVerDocumentAssociate = numVerDocumentAssociate;
	}

	public String getNameDocumentAssociate() {
		return nameDocumentAssociate;
	}

	public void setNameDocumentAssociate(String nameDocumentAssociate) {
		this.nameDocumentAssociate = nameDocumentAssociate;
	}

	public String getRequireTracking() {
		return requireTracking;
	}

	public void setRequireTracking(String requireTracking) {
		this.requireTracking = requireTracking;
	}

	public String getRequireTrackingDate() {
		return requireTrackingDate;
	}

	public void setRequireTrackingDate(String requireTrackingDate) {
		this.requireTrackingDate = requireTrackingDate;
	}

	
	public String getTrackingSacop() {
		return trackingSacop;
	}

	public void setTrackingSacop(String trackingSacop) {
		this.trackingSacop = trackingSacop;
	}

	public String getNumberTrackingSacop() {
		return numberTrackingSacop;
	}

	public void setNumberTrackingSacop(String numberTrackingSacop) {
		this.numberTrackingSacop = numberTrackingSacop;
	}
	
	public Integer getIdRegisterGenerated() {
		return idRegisterGenerated;
	}

	public void setIdRegisterGenerated(Integer idRegisterGenerated) {
		this.idRegisterGenerated = idRegisterGenerated;
	}
	
	public String getDescripcionAccionPrincipal() {
		return descripcionAccionPrincipal;
	}

	public void setDescripcionAccionPrincipal(String descripcionAccionPrincipal) {
		this.descripcionAccionPrincipal = descripcionAccionPrincipal;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("plantilla1 [id=").append(id)
				.append(", idplanillasacop1=").append(idplanillasacop1)
				.append(", idplanillasacop=").append(idplanillasacop)
				.append(", user=").append(user).append(", apellidos=")
				.append(apellidos).append(", nombres=").append(nombres)
				.append(", cargo=").append(cargo).append(", correcpreven=")
				.append(correcpreven).append(", descripcion=")
				.append(descripcion).append(", causasnoconformidad=")
				.append(causasnoconformidad).append(", accionrecomendada=")
				.append(accionrecomendada).append(", edodelsacop=")
				.append(edodelsacop).append(", edodelsacoptxt=")
				.append(edodelsacoptxt).append(", origensacop=")
				.append(origensacop).append(", origensacoptxt=")
				.append(origensacoptxt).append(", fechaEmision=")
				.append(fechaEmision).append(", fechaEstimada=")
				.append(fechaEstimada).append(", fechaReal=").append(fechaReal)
				.append(", noaceptada=").append(noaceptada)
				.append(", rechazoapruebo=").append(rechazoapruebo)
				.append(", numerodeaccion=").append(numerodeaccion)
				.append(", accionobservacion=").append(accionobservacion)
				.append(", accionobservaciontxt=").append(accionobservaciontxt)
				.append(", archivoTecnica=").append(archivoTecnica)
				.append(", normsisoSelected=")
				.append(Arrays.toString(normsisoSelected))
				.append(", normISO=")
				.append(Arrays.toString(normISO))
				.append(", proceafectadosSelected=")
				.append(Arrays.toString(proceafectadosSelected))
				.append(", usersSelected=")
				.append(Arrays.toString(usersSelected))
				.append(", accionesEstablecidas=").append(accionesEstablecidas)
				.append(", accionesEstablecidastxt=")
				.append(accionesEstablecidastxt).append(", eliminarcausaraiz=")
				.append(eliminarcausaraiz).append(", eliminarcausaraiztxt=")
				.append(eliminarcausaraiztxt).append(", sacopnum=")
				.append(sacopnum).append(", usernotificado=")
				.append(usernotificado).append(", usernotificadotxt=")
				.append(usernotificadotxt).append(", areafectadatxt=")
				.append(areafectadatxt).append(", contentType=")
				.append(contentType).append(", nameFile=").append(nameFile)
				.append(", fileNameFisico=").append(fileNameFisico)
				.append(", path=").append(path).append(", fechaculminar=")
				.append(fechaculminar).append(", sacop_relacionadas=")
				.append(sacop_relacionadas).append(", selected=")
				.append(selected).append(", noconformidades=")
				.append(noconformidades).append(", noconformidadesref=")
				.append(noconformidadesref)
				.append(", idplanillasacop1esqueleto=")
				.append(idplanillasacop1esqueleto)
				.append(", continuar_Sacop_Intouch=")
				.append(continuar_Sacop_Intouch)
				.append(", habilitadoEsqueleto=").append(habilitadoEsqueleto)
				.append(", estado=").append(estado)
				.append(", idClasificacion=").append(idClasificacion)
				.append(", fechaWhenDiscovered=").append(fechaWhenDiscovered)
				.append(", nameRelatedDocument=").append(nameRelatedDocument)
				.append(", noConformidadesDetail=")
				.append(noConformidadesDetail)
				.append(", sacopRelacionadasDetail=")
				.append(sacopRelacionadasDetail)
				.append(", comntresponsablecerrar=")
				.append(comntresponsablecerrar)
				.append(", deleteArchivoTecnica=").append(deleteArchivoTecnica)
				.append(", nombreClasificacionSACOP=")
				.append(nombreClasificacionSACOP)
				.append(", fechaVerificacion=").append(fechaVerificacion)
				.append(", fechaCierre=").append(fechaCierre)
				.append(", sacop_intouch=")
				.append(Arrays.toString(sacop_intouch)).append("]");
		return builder.toString();
	}

}
