package com.focus.qweb.to;

import java.sql.Timestamp;

import com.desige.webDocuments.sacop.forms.Plantilla1BD;
import com.desige.webDocuments.utils.ToolsHTML;
import com.focus.qweb.interfaces.ObjetoTO;

public class PlanillaSacop1EsqueletoTO implements ObjetoTO {

	private static final long serialVersionUID = 1L;

	private String idplanillasacop1;
	private String sacopnum;
	private String emisor;
	private String usernotificado;
	private String respblearea;
	private String estado;
	private String origensacop;
	private String fechaemision;
	private String fechasacops1;
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
	private String accionesEstablecidastxt;
	private String eliminarcausaraiz;
	private String eliminarcausaraiztxt;
	private String nameFile;
	private String contentType;
	private String activecomntresponsablecerrar;
	private String active;
	private String fechaculminar;
	private String comntresponsablecerrar;
	private String sacopRelacionadas;
	private String numgenVersion;
	private String noconformidades;
	private String noconformidadesref;
	private String estadoEsqueletoConfiguradoSacop;
	
    private String usuarioSacops1;
    private String fechaSacops1;
    private String idDocumentRelated;
    private String actionType;
    private String idDocumentAssociate;
    private String numVerDocumentAssociate;
    private String nameDocumentAssociate;
    private String requireTracking;
    private String requireTrackingDate;
	private String trackingSacop;
	private String numberTrackingSacop;
	
	
	public PlanillaSacop1EsqueletoTO() {
		
	}
	
	public PlanillaSacop1EsqueletoTO(Plantilla1BD p) {
		this.idplanillasacop1 = String.valueOf(p.getIdplanillasacop1());
		this.sacopnum = p.getSacopnum();
		this.emisor = String.valueOf(p.getEmisor());
		this.usernotificado = String.valueOf(p.getUsernotificado());
		this.respblearea = String.valueOf(p.getRespblearea());
		this.estado = String.valueOf(p.getEstado());
		this.origensacop = String.valueOf(p.getOrigensacop());
		this.fechaemision = ToolsHTML.sdf.format(p.getFechaemision());
		this.fechasacops1 = ToolsHTML.sdf.format(p.getFechasacops1());
		this.requisitosaplicable = p.getRequisitosaplicable();
		this.procesosafectados = p.getProcesosafectados();
		this.solicitudinforma = p.getSolicitudinforma();
		this.descripcion = p.getDescripcion();
		this.causasnoconformidad = p.getCausasnoconformidad();
		this.accionesrecomendadas = p.getAccionesrecomendadas();
		this.correcpreven = p.getCorrecpreven();
		this.rechazoapruebo = p.getRechazoapruebo();
		this.noaceptada = p.getNoaceptada();
		this.accionobservacion = p.getAccionobservacion();
		this.fechaestimada = p.getFechaEstimada();
		this.fechareal = p.getFechaReal();
		this.accionesEstablecidas = p.getAccionesEstablecidas();
		this.accionesEstablecidastxt = p.getAccionesEstablecidastxt();
		this.eliminarcausaraiz = p.getEliminarcausaraiz();
		this.eliminarcausaraiztxt = p.getEliminarcausaraiztxt();
		this.nameFile = p.getNameFile();
		this.contentType = p.getContentType();
		this.activecomntresponsablecerrar = p.getComntresponsablecerrar();
		this.active = String.valueOf(p.getActive());
		this.fechaculminar = p.getFechaculminar();
		this.comntresponsablecerrar = p.getComntresponsablecerrar();
		this.sacopRelacionadas = p.getSacop_relacionadas();
		//this.numgenVersion = p.get;
		this.noconformidades = p.getNoconformidades();
		this.noconformidadesref = p.getNoconformidadesref();
		//this.estadoEsqueletoConfiguradoSacop = p.get;
	}
	


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

	
	public String getFechasacops1() {
		return fechasacops1;
	}

	public void setFechasacops1(String fechasacops1) {
		this.fechasacops1 = fechasacops1;
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




	public String getAccionesEstablecidastxt() {
		return accionesEstablecidastxt;
	}




	public void setAccionesEstablecidastxt(String accionesEstablecidastxt) {
		this.accionesEstablecidastxt = accionesEstablecidastxt;
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




	public String getNumgenVersion() {
		return numgenVersion;
	}




	public void setNumgenVersion(String numgenVersion) {
		this.numgenVersion = numgenVersion;
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




	public String getEstadoEsqueletoConfiguradoSacop() {
		return estadoEsqueletoConfiguradoSacop;
	}




	public void setEstadoEsqueletoConfiguradoSacop(String estadoEsqueletoConfiguradoSacop) {
		this.estadoEsqueletoConfiguradoSacop = estadoEsqueletoConfiguradoSacop;
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

	public String getActionType() {
		return actionType;
	}

	public void setActionType(String actionType) {
		this.actionType = actionType;
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

	@Override
	public String toString() {

		StringBuilder builder = new StringBuilder();
		builder.append("PlanillaSacop1EsqueletoTO [");
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
		builder.append(", accionesEstablecidastxt=").append(accionesEstablecidastxt);
		builder.append(", eliminarcausaraiz=").append(eliminarcausaraiz);
		builder.append(", eliminarcausaraiztxt=").append(eliminarcausaraiztxt);
		builder.append(", nameFile=").append(nameFile);
		builder.append(", contentType=").append(contentType);
		builder.append(", activecomntresponsablecerrar=").append(activecomntresponsablecerrar);
		builder.append(", active=").append(active);
		builder.append(", fechaculminar=").append(fechaculminar);
		builder.append(", comntresponsablecerrar=").append(comntresponsablecerrar);
		builder.append(", sacop_relacionadas=").append(sacopRelacionadas);
		builder.append(", numgen_version=").append(numgenVersion);
		builder.append(", noconformidades=").append(noconformidades);
		builder.append(", noconformidadesref=").append(noconformidadesref);
		builder.append(", estadoEsqueletoConfiguradoSacop=").append(estadoEsqueletoConfiguradoSacop);
	    builder.append(", usuarioSacops1=").append(usuarioSacops1);
	    builder.append(", fechaSacops1=").append(fechaSacops1);
	    builder.append(", idDocumentRelated=").append(idDocumentRelated);
	    builder.append(", actionType=").append(actionType);
	    builder.append(", idDocumentAssociate=").append(idDocumentAssociate);
	    builder.append(", numVerDocumentAssociate=").append(numVerDocumentAssociate);
	    builder.append(", nameDocumentAssociate=").append(nameDocumentAssociate);
	    builder.append(", requireTracking=").append(requireTracking);
	    builder.append(", requireTrackingDate=").append(requireTrackingDate);

		builder.append("]");

		return builder.toString();
	}

}
