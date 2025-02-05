package com.focus.qweb.to;

import java.sql.Timestamp;

import com.desige.webDocuments.sacop.forms.Plantilla1BD;
import com.desige.webDocuments.sacop.forms.plantilla1;
import com.desige.webDocuments.utils.ToolsHTML;
import com.focus.qweb.interfaces.ObjetoTO;

public class PlanillaSacop1TO implements ObjetoTO {

	private static final long serialVersionUID = 1L;

	private String idplanillasacop1;
	private String sacopnum;
	private String emisor;
	private String usernotificado;
	private String respblearea;
	private String estado;
	private String origensacop;
	private String fechaemision;
	private String fechasolicitud;
	private String requisitosaplicable;
	private String procesosafectados;
	private String solicitudinforma;
	private String descripcion;
	private String causasnoconformidad;
	private String accionesrecomendadas;
	private String correcpreven;
	private String rechazoapruebo;
	private String noaceptada;
	private String accionobservacion;
	private String fechaestimada;
	private String fechareal;
	private String accionesEstablecidas;
	private String accionesestablecidastxt;
	private String eliminarcausaraiz;
	private String eliminarcausaraiztxt;
	private String nameFile;
	private String contentType;
	private String data;
	private String activecomntresponsablecerrar;
	private String active;
	private String fechaculminar;
	private String comntresponsablecerrar;
	private String sacopRelacionadas;
	private String noconformidadesref;
	private String noconformidades;
	private String idplanillasacop1esqueleto;
	private String clasificacion;
	private String fechaWhenDiscovered;
	private String archivoTecnica;
	private String fechaVerificacion;
	private String fechaCierre;
	private String usuarioSacops1;
	private String fechaSacops1;
	private String idDocumentRelated;
	private String idDocumentAssociate;
	private String numVerDocumentAssociate;
	private String nameDocumentAssociate;
	private String requireTracking;
	private String requireTrackingDate;
	private String trackingSacop;
	private String numberTrackingSacop;
	
	private String idRegisterGenerated;
	private String descripcionAccionPrincipal;

	
	public PlanillaSacop1TO() {
		
	}
	
	@SuppressWarnings("unlikely-arg-type")
	public PlanillaSacop1TO(Plantilla1BD o) {
		this.idplanillasacop1 = String.valueOf(o.getIdplanillasacop1());
		this.sacopnum = o.getSacopnum();
		this.emisor = String.valueOf(o.getEmisor());
		this.usernotificado = String.valueOf(o.getUsernotificado());
		this.respblearea = String.valueOf(o.getRespblearea());
		this.estado = String.valueOf(o.getEstado());
		this.origensacop = String.valueOf(o.getOrigensacop());
		this.fechaemision = ToolsHTML.sdfShowConvert1.format(o.getFechaemision());
		this.fechasolicitud = ToolsHTML.sdfShowConvert1.format(o.getFechasacops1()==null || o.getFechasacops1().equals("")?o.getFechaemision():o.getFechasacops1());
		this.requisitosaplicable = o.getRequisitosaplicable();
		this.procesosafectados = o.getProcesosafectados();
		this.solicitudinforma = o.getSolicitudinforma();
		this.descripcion = o.getDescripcion();
		this.causasnoconformidad = o.getCausasnoconformidad();
		this.accionesrecomendadas = o.getAccionesrecomendadas();
		this.correcpreven = o.getCorrecpreven();
		this.rechazoapruebo = o.getRechazoapruebo();
		this.noaceptada = o.getNoaceptada();
		this.accionobservacion = o.getAccionobservacion();
		this.fechaestimada = o.getFechaEstimada();
		this.fechareal = o.getFechaReal();
		this.accionesEstablecidas = o.getAccionesEstablecidas();
		this.accionesestablecidastxt = o.getAccionesEstablecidastxt();
		this.eliminarcausaraiz = o.getEliminarcausaraiz();
		this.eliminarcausaraiztxt = o.getEliminarcausaraiztxt();
		this.nameFile = o.getNameFile();
		this.contentType = o.getContentType();
		this.activecomntresponsablecerrar = String.valueOf(o.getActive());
		this.active = String.valueOf(o.getActive());
		this.fechaculminar = o.getFechaculminar();
		this.comntresponsablecerrar = o.getComntresponsablecerrar();
		this.sacopRelacionadas = o.getSacop_relacionadas();
		this.noconformidadesref = o.getNoconformidadesref();
		this.noconformidades = o.getNoconformidades();
		this.idplanillasacop1esqueleto = String.valueOf(o.getIdplanillasacop1esqueleto());
		this.clasificacion = String.valueOf(o.getIdClasificacion());
		this.fechaWhenDiscovered = ToolsHTML.sdfShowConvert1.format(o.getFechaWhenDiscovered());
		this.archivoTecnica = o.getArchivoTecnica();
		this.fechaVerificacion = o.getFechaVerificacion()!=null?ToolsHTML.sdfShowConvert1.format(o.getFechaVerificacion()):null;
		this.fechaCierre = o.getFechaCierre()!=null?ToolsHTML.sdfShowConvert1.format(o.getFechaCierre()):null;
		this.idDocumentRelated = ToolsHTML.isEmptyOrNull(o.getIdDocumentRelated(),"0");
		this.usuarioSacops1 = ToolsHTML.isEmptyOrNull(o.getUsuarioSacops1(),"0");
		this.idDocumentAssociate = ToolsHTML.isEmptyOrNull(o.getIdDocumentAssociate(), "0");
		this.numVerDocumentAssociate = ToolsHTML.isEmptyOrNull(o.getNumVerDocumentAssociate(), "0");
		this.nameDocumentAssociate = ToolsHTML.isEmptyOrNull(o.getNameDocumentAssociate(), "");
		this.requireTracking = ToolsHTML.isEmptyOrNull(o.getRequireTracking(), "0");
		this.requireTrackingDate = o.getRequireTrackingDate()!=null?ToolsHTML.sdfShowConvert1.format(o.getRequireTrackingDate()):null;
		this.trackingSacop = ToolsHTML.isEmptyOrNull(o.getTrackingSacop(), "0");
		this.numberTrackingSacop = ToolsHTML.isEmptyOrNull(o.getNumberTrackingSacop(), "");
	}
	
	public PlanillaSacop1TO(plantilla1 o) {
		this.idplanillasacop1 = String.valueOf(o.getIdplanillasacop1());
		this.sacopnum = o.getSacopnum();
		//this.emisor = String.valueOf(o.getEmisor());
		this.usernotificado = String.valueOf(o.getUsernotificado());
		//this.respblearea = String.valueOf(o.getRespblearea());
		this.estado = String.valueOf(o.getEstado());
		this.origensacop = String.valueOf(o.getOrigensacop());
		//this.fechaemision = ToolsHTML.sdfShowConvert1.format(o.getFechaemision());
		//this.requisitosaplicable = o.getRequisitosaplicable();
		//this.procesosafectados = o.getProcesosafectados();
		//this.solicitudinforma = o.getSolicitudinforma();
		this.descripcion = o.getDescripcion();
		this.causasnoconformidad = o.getCausasnoconformidad();
		//this.accionesrecomendadas = o.getAccionesrecomendadas();
		this.correcpreven = o.getCorrecpreven();
		this.rechazoapruebo = o.getRechazoapruebo();
		this.noaceptada = o.getNoaceptada();
		this.accionobservacion = o.getAccionobservacion();
		this.fechaestimada = o.getFechaEstimada();
		this.fechareal = o.getFechaReal();
		this.accionesEstablecidas = o.getAccionesEstablecidas();
		this.accionesestablecidastxt = o.getAccionesEstablecidastxt();
		this.eliminarcausaraiz = o.getEliminarcausaraiz();
		this.eliminarcausaraiztxt = o.getEliminarcausaraiztxt();
		//this.nameFile = o.getNameFile();
		//this.contentType = o.getContentType();
		//this.data = String.valueOf(o.getData());
		//this.activecomntresponsablecerrar = String.valueOf(o.getActive());
		//this.active = String.valueOf(o.getActive());
		this.fechaculminar = o.getFechaculminar();
		this.comntresponsablecerrar = o.getComntresponsablecerrar();
		this.sacopRelacionadas = o.getSacop_relacionadas();
		this.noconformidadesref = o.getNoconformidadesref();
		this.noconformidades = o.getNoconformidades();
		this.idplanillasacop1esqueleto = String.valueOf(o.getIdplanillasacop1esqueleto());
		this.clasificacion = String.valueOf(o.getIdClasificacion());
		this.fechaWhenDiscovered = ToolsHTML.sdfShowConvert1.format(o.getFechaWhenDiscovered());
		//this.archivoTecnica = o.getArchivoTecnica();
		this.fechaVerificacion = ToolsHTML.sdfShowConvert1.format(o.getFechaVerificacion());
		this.fechaCierre = ToolsHTML.sdfShowConvert1.format(o.getFechaCierre());
		this.trackingSacop = ToolsHTML.isEmptyOrNull(o.getTrackingSacop(), "0");
		this.numberTrackingSacop = ToolsHTML.isEmptyOrNull(o.getNumberTrackingSacop(), "");
	}	
		
	/*
	public PlanillaSacop1TO(plantilla1 p) {
		setIdplanillasacop1(p.getIdplanillasacop1());
		setSacopnum(p.getSacopnum());
		setEmisor(p.getE);
		setUsernotificado(String.valueOf(p.getUsernotificado()));
		setRespblearea(p.getare);
		setEstado(p.getEstado());
		setOrigensacop(p.getOrigensacop());
		setFechaemision(p.getFechaEmision());
		setRequisitosaplicable(p.getR);
		setProcesosafectados(p.getProceafectadosSelected());
		setSolicitudinforma(p.get);
		setDescripcion(p);
		setCausasnoconformidad(p);
		setAccionesrecomendadas(p);
		setCorrecpreven(p);
		setRechazoapruebo(p);
		setNoaceptada(p);
		setAccionobservacion(p);
		setFechaestimada(p);
		setFechareal(p);
		setAccionesEstablecidas(p);
		setAccionesestablecidastxt(p);
		setEliminarcausaraiz(p);
		setEliminarcausaraiztxt(p);
		setNameFile(p);
		setContentType(p);
		setData(p);
		setActivecomntresponsablecerrar(p);
		setActive(p);
		setFechaculminar(p);
		setComntresponsablecerrar(p);
		setSacopRelacionadas(p);
		setNoconformidadesref(p);
		setNoconformidades(p);
		setIdplanillasacop1esqueleto(p);
		setClasificacion(p);
		setFechaWhenDiscovered(p);
		setArchivoTecnica(p);
		setFechaVerificacion(p);
		setFechaCierre(p);
	}
		*/

	
	public String getIdplanillasacop1() {
		return idplanillasacop1;
	}

	public int getIdplanillasacop1Int() {
		return ToolsHTML.parseInt(idplanillasacop1);
	}


	public void setIdplanillasacop1(String idplanillasacop1) {
		this.idplanillasacop1 = idplanillasacop1;
	}



	public String getSacopnum() {
		return sacopnum;
	}



	public void setSacopnum(String sacopnum) {
		this.sacopnum = sacopnum;
	}



	public String getEmisor() {
		return emisor;
	}

	public int getEmisorInt() {
		return ToolsHTML.parseInt(emisor);
	}


	public void setEmisor(String emisor) {
		this.emisor = emisor;
	}



	public String getUsernotificado() {
		return usernotificado;
	}

	public int getUsernotificadoInt() {
		return ToolsHTML.parseInt(usernotificado);
	}


	public void setUsernotificado(String usernotificado) {
		this.usernotificado = usernotificado;
	}



	public String getRespblearea() {
		return respblearea;
	}

	public int getRespbleareaInt() {
		return ToolsHTML.parseInt(respblearea);
	}


	public void setRespblearea(String respblearea) {
		this.respblearea = respblearea;
	}



	public String getEstado() {
		return estado;
	}

	public int getEstadoInt() {
		return ToolsHTML.parseInt(estado);
	}


	public void setEstado(String estado) {
		this.estado = estado;
	}



	public String getOrigensacop() {
		return origensacop;
	}

	public int getOrigensacopInt() {
		return ToolsHTML.parseInt(origensacop);
	}


	public void setOrigensacop(String origensacop) {
		this.origensacop = origensacop;
	}



	public String getFechaemision() {
		return fechaemision;
	}

	public Timestamp getFechaemisionTimestamp() {
		return ToolsHTML.parseTimestamp(fechaemision);
	}


	public void setFechaemision(String fechaemision) {
		this.fechaemision = fechaemision;
	}

	public String getFechasolicitud() {
		return fechasolicitud;
	}

	public void setFechasolicitud(String fechasolicitud) {
		this.fechasolicitud = fechasolicitud;
	}

	public String getRequisitosaplicable() {
		return requisitosaplicable;
	}



	public void setRequisitosaplicable(String requisitosaplicable) {
		this.requisitosaplicable = requisitosaplicable;
	}



	public String getProcesosafectados() {
		return procesosafectados;
	}



	public void setProcesosafectados(String procesosafectados) {
		this.procesosafectados = procesosafectados;
	}



	public String getSolicitudinforma() {
		return solicitudinforma;
	}



	public void setSolicitudinforma(String solicitudinforma) {
		this.solicitudinforma = solicitudinforma;
	}



	public String getDescripcion() {
		return descripcion;
	}



	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}



	public String getCausasnoconformidad() {
		return causasnoconformidad;
	}



	public void setCausasnoconformidad(String causasnoconformidad) {
		this.causasnoconformidad = causasnoconformidad;
	}



	public String getAccionesrecomendadas() {
		return accionesrecomendadas;
	}



	public void setAccionesrecomendadas(String accionesrecomendadas) {
		this.accionesrecomendadas = accionesrecomendadas;
	}



	public String getCorrecpreven() {
		return correcpreven;
	}



	public void setCorrecpreven(String correcpreven) {
		this.correcpreven = correcpreven;
	}



	public String getRechazoapruebo() {
		return rechazoapruebo;
	}



	public void setRechazoapruebo(String rechazoapruebo) {
		this.rechazoapruebo = rechazoapruebo;
	}



	public String getNoaceptada() {
		return noaceptada;
	}



	public void setNoaceptada(String noaceptada) {
		this.noaceptada = noaceptada;
	}



	public String getAccionobservacion() {
		return accionobservacion;
	}



	public void setAccionobservacion(String accionobservacion) {
		this.accionobservacion = accionobservacion;
	}



	public String getFechaestimada() {
		return fechaestimada;
	}

	public void setFechaestimada(String fechaestimada) {
		this.fechaestimada = fechaestimada;
	}



	public String getFechareal() {
		return fechareal;
	}



	public void setFechareal(String fechareal) {
		this.fechareal = fechareal;
	}



	public String getAccionesEstablecidas() {
		return accionesEstablecidas;
	}



	public void setAccionesEstablecidas(String accionesEstablecidas) {
		this.accionesEstablecidas = accionesEstablecidas;
	}



	public String getAccionesestablecidastxt() {
		return accionesestablecidastxt;
	}



	public void setAccionesestablecidastxt(String accionesestablecidastxt) {
		this.accionesestablecidastxt = accionesestablecidastxt;
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



	public String getNameFile() {
		return nameFile;
	}



	public void setNameFile(String nameFile) {
		this.nameFile = nameFile;
	}



	public String getContentType() {
		return contentType;
	}



	public void setContentType(String contentType) {
		this.contentType = contentType;
	}



	public String getData() {
		return data;
	}

	public byte[] getDataByte() {
		return data==null?"".getBytes():data.getBytes();
	}


	public void setData(String data) {
		this.data = data;
	}



	public String getActivecomntresponsablecerrar() {
		return activecomntresponsablecerrar;
	}



	public void setActivecomntresponsablecerrar(String activecomntresponsablecerrar) {
		this.activecomntresponsablecerrar = activecomntresponsablecerrar;
	}



	public String getActive() {
		return active;
	}

	public int getActiveInt() {
		return ToolsHTML.parseInt(active);
	}


	public void setActive(String active) {
		this.active = active;
	}



	public String getFechaculminar() {
		return fechaculminar;
	}



	public void setFechaculminar(String fechaculminar) {
		this.fechaculminar = fechaculminar;
	}



	public String getComntresponsablecerrar() {
		return comntresponsablecerrar;
	}



	public void setComntresponsablecerrar(String comntresponsablecerrar) {
		this.comntresponsablecerrar = comntresponsablecerrar;
	}



	public String getSacopRelacionadas() {
		return sacopRelacionadas;
	}



	public void setSacopRelacionadas(String sacopRelacionadas) {
		this.sacopRelacionadas = sacopRelacionadas;
	}



	public String getNoconformidadesref() {
		return noconformidadesref;
	}



	public void setNoconformidadesref(String noconformidadesref) {
		this.noconformidadesref = noconformidadesref;
	}



	public String getNoconformidades() {
		return noconformidades;
	}



	public void setNoconformidades(String noconformidades) {
		this.noconformidades = noconformidades;
	}



	public String getIdplanillasacop1esqueleto() {
		return idplanillasacop1esqueleto;
	}

	public int getIdplanillasacop1esqueletoInt() {
		return ToolsHTML.parseInt(idplanillasacop1esqueleto);
	}


	public void setIdplanillasacop1esqueleto(String idplanillasacop1esqueleto) {
		this.idplanillasacop1esqueleto = idplanillasacop1esqueleto;
	}



	public String getClasificacion() {
		return clasificacion;
	}

	public int getClasificacionInt() {
		return ToolsHTML.parseInt(clasificacion);
	}


	public void setClasificacion(String clasificacion) {
		this.clasificacion = clasificacion;
	}



	public String getFechaWhenDiscovered() {
		return fechaWhenDiscovered;
	}

	public Timestamp getFechaWhenDiscoveredTimestamp() {
		return ToolsHTML.parseTimestamp(fechaWhenDiscovered);
	}


	public void setFechaWhenDiscovered(String fechaWhenDiscovered) {
		this.fechaWhenDiscovered = fechaWhenDiscovered;
	}



	public String getArchivoTecnica() {
		return archivoTecnica;
	}



	public void setArchivoTecnica(String archivoTecnica) {
		this.archivoTecnica = archivoTecnica;
	}



	public String getFechaVerificacion() {
		return fechaVerificacion;
	}

	public Timestamp getFechaVerificacionTimestamp() {
		return ToolsHTML.parseTimestamp(fechaVerificacion);
	}


	public void setFechaVerificacion(String fechaVerificacion) {
		this.fechaVerificacion = fechaVerificacion;
	}



	public String getFechaCierre() {
		return fechaCierre;
	}

	public Timestamp getFechaCierreTimestamp() {
		return ToolsHTML.parseTimestamp(fechaCierre);
	}


	public void setFechaCierre(String fechaCierre) {
		this.fechaCierre = fechaCierre;
	}

	public String getUsuarioSacops1() {
		return usuarioSacops1;
	}

	public int getUsuarioSacops1Int() {
		return ToolsHTML.parseInt(usuarioSacops1);
	}

	public void setUsuarioSacops1(String usuarioSacops1) {
		this.usuarioSacops1 = usuarioSacops1;
	}

	public String getFechaSacops1() {
		return fechaSacops1;
	}

	public Timestamp getFechaSacops1Timestamp() {
		return ToolsHTML.parseTimestamp(fechaSacops1);
	}
	
	public void setFechaSacops1(String fechaSacops1) {
		this.fechaSacops1 = fechaSacops1;
	}

	public String getIdDocumentRelated() {
		return idDocumentRelated;
	}

	public int getIdDocumentRelatedInt() {
		return ToolsHTML.parseInt(idDocumentRelated);
	}

	public void setIdDocumentRelated(String idDocumentRelated) {
		this.idDocumentRelated = idDocumentRelated;
	}

	public String getIdDocumentAssociate() {
		return idDocumentAssociate;
	}

	public int getIdDocumentAssociateInt() {
		return ToolsHTML.parseInt(idDocumentAssociate);
	}

	public void setIdDocumentAssociate(String idDocumentAssociate) {
		this.idDocumentAssociate = idDocumentAssociate;
	}

	public String getNumVerDocumentAssociate() {
		return numVerDocumentAssociate;
	}

	public int getNumVerDocumentAssociateInt() {
		return ToolsHTML.parseInt(numVerDocumentAssociate);
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

	public int getRequireTrackingInt() {
		return ToolsHTML.parseInt(requireTracking);
	}
	
	public void setRequireTracking(String requireTracking) {
		this.requireTracking = requireTracking;
	}

	public String getRequireTrackingDate() {
		return requireTrackingDate;
	}

	public Timestamp getRequireTrackingDateTimestamp() {
		return ToolsHTML.parseTimestamp(requireTrackingDate);
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

	
	public String getIdRegisterGenerated() {
		return idRegisterGenerated;
	}

	public void setIdRegisterGenerated(String idRegisterGenerated) {
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
		builder.append("PlanillaSacop1TO [");
		builder.append("  idplanillasacop1=").append(idplanillasacop1);
		builder.append(", sacopnum=").append(sacopnum);
		builder.append(", emisor=").append(emisor);
		builder.append(", usernotificado=").append(usernotificado);
		builder.append(", respblearea=").append(respblearea);
		builder.append(", estado=").append(estado);
		builder.append(", origensacop=").append(origensacop);
		builder.append(", fechaemision=").append(fechaemision);
		builder.append(", requisitosaplicable=").append(requisitosaplicable);
		builder.append(", procesosafectados=").append(procesosafectados);
		builder.append(", solicitudinforma=").append(solicitudinforma);
		builder.append(", descripcion=").append(descripcion);
		builder.append(", causasnoconformidad=").append(causasnoconformidad);
		builder.append(", accionesrecomendadas=").append(accionesrecomendadas);
		builder.append(", correcpreven=").append(correcpreven);
		builder.append(", rechazoapruebo=").append(rechazoapruebo);
		builder.append(", noaceptada=").append(noaceptada);
		builder.append(", accionobservacion=").append(accionobservacion);
		builder.append(", fechaestimada=").append(fechaestimada);
		builder.append(", fechareal=").append(fechareal);
		builder.append(", accionesEstablecidas=").append(accionesEstablecidas);
		builder.append(", accionesestablecidastxt=").append(accionesestablecidastxt);
		builder.append(", eliminarcausaraiz=").append(eliminarcausaraiz);
		builder.append(", eliminarcausaraiztxt=").append(eliminarcausaraiztxt);
		builder.append(", nameFile=").append(nameFile);
		builder.append(", contentType=").append(contentType);
		builder.append(", data=").append(data);
		builder.append(", activecomntresponsablecerrar=").append(activecomntresponsablecerrar);
		builder.append(", active=").append(active);
		builder.append(", fechaculminar=").append(fechaculminar);
		builder.append(", comntresponsablecerrar=").append(comntresponsablecerrar);
		builder.append(", sacopRelacionadas=").append(sacopRelacionadas);
		builder.append(", noconformidadesref=").append(noconformidadesref);
		builder.append(", noconformidades=").append(noconformidades);
		builder.append(", idplanillasacop1esqueleto=").append(idplanillasacop1esqueleto);
		builder.append(", clasificacion=").append(clasificacion);
		builder.append(", fechaWhenDiscovered=").append(fechaWhenDiscovered);
		builder.append(", archivoTecnica=").append(archivoTecnica);
		builder.append(", fechaVerificacion=").append(fechaVerificacion);
		builder.append(", fechaCierre=").append(fechaCierre);
		builder.append(", usuarioSacops1=").append(usuarioSacops1);
		builder.append(", fechaSacops1=").append(fechaSacops1);
		builder.append(", idDocumentRelated=").append(idDocumentRelated);
		builder.append(", idDocumentAssociate=").append(idDocumentAssociate);
		builder.append(", numVerDocumentAssociate=").append(numVerDocumentAssociate);
		builder.append(", nameDocumentAssociate=").append(nameDocumentAssociate);
		builder.append(", requireTracking=").append(requireTracking);
		builder.append(", requireTrackingDate=").append(requireTrackingDate);
		builder.append(", trackingSacop=").append(trackingSacop);
		builder.append(", numberTrackingSacop=").append(numberTrackingSacop);
		builder.append(", idRegisterGenerated=").append(idRegisterGenerated);
		builder.append(", descripcionAccionPrincipal=").append(descripcionAccionPrincipal);
		builder.append("]");

		return builder.toString();
	}

}
