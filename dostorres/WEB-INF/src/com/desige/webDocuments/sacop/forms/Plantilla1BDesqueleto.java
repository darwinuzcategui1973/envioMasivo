package com.desige.webDocuments.sacop.forms;


import java.io.Serializable;
import java.text.ParseException;
import java.util.Date;

import com.desige.webDocuments.utils.ToolsHTML;
import com.desige.webDocuments.utils.beans.SuperActionForm;
import com.focus.qweb.to.PlanillaSacop1EsqueletoTO;

/**
 * Created by IntelliJ IDEA.
 * User: srodriguez
 * Date: 16/03/2007
 * Time: 04:23:59 PM
 * To change this template use File | Settings | File Templates.
 */
public class Plantilla1BDesqueleto extends SuperActionForm implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -3381257456925247096L;

	private long idplanillasacop1;
	private String sacopnum;
	private long emisor;
	private String emisortxt;
	private long respblearea;
	private String respbleareatxt;
	private int estado;
	private String estadotxt;
	private int origensacop;
	private java.util.Date fechaemision;
	private java.util.Date fechasacops1;
	private String requisitosaplicable;
	private String procesosafectados;
	private String solicitudinforma;
	private String accionesrecomendadas;
	private byte active;
	private String descripcion;
	private String causasnoconformidad;
	private String accionrecomendada;
	private String edodelsacop;
	private String planilla;
	private Object[] normsisoSelected;
	private Object[] proceafectadosSelected;
	private Object[] usersSelected;
	private String correcpreven;
	private String apellidos;
	private String nombres;
	private String cargo;
	private String id;
	private String user;
	private String noaceptada;
	private String rechazoapruebo;
	private String accionobservacion;
	private String fechaEstimada;
	private String fechaReal;
	private String  accionesEstablecidas;
	private String  accionesEstablecidastxt;
	private String  eliminarcausaraiz;
	private String  eliminarcausaraiztxt;
	//el usernotificado es el cargo del area afectada
	//private long  usernotificado;
	private long  idcargodelareaafectada;
	private String  usernotificadotxt;
	private String cargoEmisor;
	private boolean mostrarCargo;
	private boolean eliminarDeLista;
	private boolean isEmisor;
	private boolean isResponsable;
	private String contentType;
	private String nameFile;
	private String fechaculminar;
	private String comntresponsablecerrar;
	private String sacop_relacionadas;
	private boolean selected;
	private String noconformidades;
	private String noconformidadesref;
	private long idplanillasacop1esqueleto;
	private int idClasificacion;
	private Date fechaWhenDiscovered;
	private String nameRelatedDocument;
	private Date fechaVerificacion;
	private Date fechaCierre;
	private int idDocumentRelated;
	private int idDocumentAssociate;
	private int numVerDocumentAssociate;
	private String nameDocumentAssociate;
	private String requireTracking;
	private String requireTrackingDate;

	public Plantilla1BDesqueleto(){

	}
	
	public Plantilla1BDesqueleto(PlanillaSacop1EsqueletoTO o){
		setIdplanillasacop1(Long.parseLong(o.getIdplanillasacop1()));
		setSacopnum(o.getSacopnum());
		setEmisor(Long.parseLong(o.getEmisor()));
		setUsernotificado(Long.parseLong(o.getUsernotificado()));
		setRespblearea(Long.parseLong(o.getRespblearea()));
		setEstado(Integer.parseInt(o.getEstado()));
		setOrigensacop(Integer.parseInt(o.getOrigensacop()));
		try {
			setFechaemision(ToolsHTML.sdf1.parse(o.getFechaemision()));
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			setFechasacops1(ToolsHTML.sdf1.parse(o.getFechasacops1()));
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		setRequisitosaplicable(o.getRequisitosaplicable());
		setProcesosafectados(o.getProcesosafectados());
		setSolicitudinforma(o.getSolicitudinforma());
		setDescripcion(o.getDescripcion());
		setCausasnoconformidad(o.getCausasnoconformidad());
		setAccionesrecomendadas(o.getAccionesrecomendadas());
		setCorrecpreven(o.getCorrecpreven());
		setRechazoapruebo(o.getRechazoapruebo());
		setNoaceptada(o.getNoaceptada());
		setAccionobservacion(o.getAccionobservacion());
		setFechaEstimada(o.getFechaestimada());
		setFechaReal(o.getFechareal());
		setAccionesEstablecidas(o.getAccionesEstablecidas());
		setAccionesEstablecidastxt(o.getAccionesEstablecidastxt());
		setEliminarcausaraiz(o.getEliminarcausaraiz());
		setEliminarcausaraiztxt(o.getEliminarcausaraiz());
		setNameFile(o.getNameFile());
		setContentType(o.getContentType());
		//setActivecomntresponsablecerrar(o.getActivecomntresponsablecerrar());
		setActive(Byte.parseByte(o.getActive()));
		setFechaculminar(o.getFechaculminar());
		setComntresponsablecerrar(o.getComntresponsablecerrar());
		setSacop_relacionadas(o.getSacopRelacionadas());
		//setNumgenVersion("");
		setNoconformidades(o.getNoconformidades());
		setNoconformidadesref(o.getNoconformidadesref());
		//setEstadoEsqueletoConfiguradoSacop("");

	}
	
	public Plantilla1BDesqueleto (Plantilla1BD objbd){
		this.idplanillasacop1=objbd.getIdplanillasacop1();
		this.sacopnum=objbd.getSacopnum();
		this.emisor=objbd.getEmisor();
		this.emisortxt=objbd.getEmisortxt();
		this.respblearea=objbd.getRespblearea();
		this.respbleareatxt=objbd.getRespbleareatxt();
		this.estado=objbd.getEstado();
		this.estadotxt=objbd.getEstadotxt();
		this.origensacop=objbd.getOrigensacop();
		this.fechaemision=objbd.getFechaemision();
		this.fechasacops1=objbd.getFechasacops1();
		this.requisitosaplicable=objbd.getRequisitosaplicable();
		this.procesosafectados=objbd.getProcesosafectados();
		this.solicitudinforma=objbd.getSolicitudinforma();
		this.accionesrecomendadas=objbd.getAccionesrecomendadas();
		this.active=objbd.getActive();
		this.descripcion=objbd.getDescripcion();
		this.causasnoconformidad=objbd.getCausasnoconformidad();
		this.accionrecomendada=objbd.getAccionrecomendada();
		this.edodelsacop=objbd.getEdodelsacop();
		this.planilla=objbd.getPlanilla();
		this.normsisoSelected=objbd.getNormsisoSelected();
		this.proceafectadosSelected=objbd.getProceafectadosSelected();
		this.usersSelected=objbd.getUsersSelected();
		this.correcpreven=objbd.getCorrecpreven();
		this.apellidos=objbd.getApellidos();
		this.nombres=objbd.getNombres();
		this.cargo=objbd.getCargo();
		this.id=objbd.getId();
		this.user=objbd.getUser();
		this.noaceptada=objbd.getNoaceptada();
		this.rechazoapruebo=objbd.getNoaceptada();
		this.accionobservacion=objbd.getAccionobservacion();
		this.fechaEstimada=objbd.getFechaEstimada();
		this.fechaReal=objbd.getFechaReal();
		this.accionesEstablecidas=objbd.getAccionesEstablecidas();
		this.accionesEstablecidastxt=objbd.getAccionesEstablecidastxt();
		this.eliminarcausaraiz=objbd.getEliminarcausaraiz();
		this.eliminarcausaraiztxt=objbd.getEliminarcausaraiztxt();
		//el usernotificado es el cargo del area afectada
		//private long  usernotificado;
		this.idcargodelareaafectada=objbd.getUsernotificado();
		this.usernotificadotxt=objbd.getUsernotificadotxt();
		this.cargoEmisor=objbd.getCargoEmisor();
		this.mostrarCargo=objbd.getMostrarCargo();
		this.eliminarDeLista=objbd.getEliminarDeLista();
		this.isEmisor=objbd.getIsEmisor();
		this.isResponsable=objbd.getIsResponsable();
		this.contentType=objbd.getContentType();
		this.nameFile=objbd.getNameFile();
		this.fechaculminar=objbd.getFechaculminar();
		this.comntresponsablecerrar=objbd.getComntresponsablecerrar();
		this.sacop_relacionadas=objbd.getSacop_relacionadas();
		this.selected=objbd.isSelected();
		this.noconformidades=objbd.getNoconformidades();
		this.noconformidadesref=objbd.getNoconformidadesref();
		this.idplanillasacop1esqueleto=objbd.getIdplanillasacop1esqueleto();
		this.idClasificacion = objbd.getIdClasificacion();
		this.fechaWhenDiscovered = objbd.getFechaWhenDiscovered();
		this.nameRelatedDocument = objbd.getNameRelatedDocument();
		this.fechaVerificacion = objbd.getFechaVerificacion();
		this.fechaCierre = objbd.getFechaCierre();
	}

	public String getComntresponsablecerrar() {
		return comntresponsablecerrar;
	}

	public void setComntresponsablecerrar(String comntresponsablecerrar) {
		this.comntresponsablecerrar= comntresponsablecerrar;
	}

	public String getFechaculminar() {
		return fechaculminar;
	}
	public void setFechaculminar(String fechaculminar) {
		this.fechaculminar= fechaculminar;
	}

	//aqui se carga el archivo como tal
	public String getNameFile() {
		return nameFile;
	}

	public void setNameFile(String nameFile) {
		this.nameFile = nameFile;
	}
	//-----------------------------------------


	public String getContentType() {
		return contentType;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}




	public boolean getIsEmisor() {
		return isEmisor;
	}

	public void setIsEmisor(boolean isEmisor) {
		this.isEmisor  = isEmisor;
	}


	public boolean getIsResponsable() {
		return isResponsable;
	}

	public void setIsResponsable(boolean isResponsable) {
		this.isResponsable  = isResponsable;
	}

	public boolean getEliminarDeLista() {
		return eliminarDeLista;
	}

	public void setEliminarDeLista(boolean eliminarDeLista) {
		this.eliminarDeLista  = eliminarDeLista;
	}


	public String getCargoEmisor() {
		return cargoEmisor;
	}

	public void setCargoEmisor(String cargoEmisor) {
		this.cargoEmisor = cargoEmisor;
	}

	public boolean getMostrarCargo() {
		return mostrarCargo;
	}

	public void setMostrarCargo(boolean mostrarCargo) {
		this.mostrarCargo  = mostrarCargo;
	}



	public String getUsernotificadotxt() {
		return usernotificadotxt;
	}

	public void setUsernotificadotxt(String usernotificadotxt) {
		this.usernotificadotxt  = usernotificadotxt;
	}

	public long getUsernotificado() {
		return idcargodelareaafectada;
	}

	public void setUsernotificado(long idcargodelareaafectada) {
		this.idcargodelareaafectada  = idcargodelareaafectada;
	}

	public String getEliminarcausaraiz() {
		return eliminarcausaraiz;
	}

	public void setEliminarcausaraiz(String eliminarcausaraiz) {
		this.eliminarcausaraiz= eliminarcausaraiz;
	}
	public String getEliminarcausaraiztxt() {
		return eliminarcausaraiztxt;
	}

	public void setEliminarcausaraiztxt(String eliminarcausaraiztxt) {
		this.eliminarcausaraiztxt= eliminarcausaraiztxt;
	}


	public String getAccionesEstablecidas() {
		return accionesEstablecidas;
	}

	public void setAccionesEstablecidas(String accionesEstablecidas) {
		this.accionesEstablecidas= accionesEstablecidas;
	}
	public String getAccionesEstablecidastxt() {
		return accionesEstablecidastxt;
	}

	public void setAccionesEstablecidastxt(String accionesEstablecidastxt) {
		this.accionesEstablecidastxt= accionesEstablecidastxt;
	}
	public String getFechaReal() {
		return fechaReal;
	}

	public void setFechaReal(String fechaReal) {
		this.fechaReal= fechaReal;
	}

	public String getFechaEstimada() {
		return fechaEstimada;
	}

	public void setFechaEstimada(String fechaEstimada) {
		this.fechaEstimada= fechaEstimada;
	}
	public String getAccionobservacion() {
		return accionobservacion;
	}

	public void setAccionobservacion(String accionobservacion) {
		this.accionobservacion= accionobservacion;
	}
	public String getRechazoapruebo() {
		return rechazoapruebo;
	}

	public void setRechazoapruebo(String rechazoapruebo) {
		this.rechazoapruebo= rechazoapruebo;
	}

	public String getNoaceptada() {
		return noaceptada;
	}

	public void setNoaceptada(String noaceptada) {
		this.noaceptada= noaceptada;
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
	public String getCargo() {
		return cargo;
	}

	public void setCargo(String cargo) {
		this.cargo = cargo;
	}
	public String getNombres() {
		return nombres;
	}

	public void setNombres(String nombres) {
		this.nombres = nombres;
	}
	public String getApellidos() {
		return apellidos;
	}

	public void setApellidos(String apellidos) {
		this.apellidos = apellidos;
	}

	public String getCorrecpreven() {
		return correcpreven;
	}

	public void setCorrecpreven(String correcpreven) {
		this.correcpreven = correcpreven;
	}

	public String getPlanilla() {
		return planilla;
	}

	public void setPlanilla(String planilla) {
		this.planilla= planilla;
	}
	public String getEdodelsacop() {
		return edodelsacop;
	}

	public void setEdodelsacop(String edodelsacop) {
		this.edodelsacop= edodelsacop;
	}

	public String getAccionrecomendada() {
		return accionrecomendada;
	}

	public void setAccionrecomendada(String accionrecomendada) {
		this.accionrecomendada= accionrecomendada;
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


	/*
    descripcion
    causasnoconformidad
    accionrecomendada
    id
    correcpreven
    getNombres
    getApellidos
    nombres
    user
    cargo
    edodelsacop
    planilla
    fechaEmision
	 */



	 public long getIdplanillasacop1() {
		return idplanillasacop1;
	}

	public void setIdplanillasacop1(long idplanillasacop1) {
		this.idplanillasacop1  = idplanillasacop1;
	}

	public String getSacopnum() {
		return sacopnum;
	}

	public void setSacopnum(String sacopnum) {
		this.sacopnum  = sacopnum;
	}

	public long getEmisor() {
		return emisor;
	}
	public void setEmisor(long emisor) {
		this.emisor  = emisor;
	}

	public void setEmisortxt(String emisortxt) {
		this.emisortxt  = emisortxt;
	}
	public String getEmisortxt() {
		return emisortxt;
	}



	public long getRespblearea() {
		return respblearea;
	}

	public void setRespblearea(long respblearea) {
		this.respblearea  = respblearea;
	}

	public String getRespbleareatxt() {
		return respbleareatxt;
	}

	public void setRespbleareatxt(String respbleareatxt) {
		this.respbleareatxt  = respbleareatxt;
	}

	public int getEstado() {
		return estado;
	}

	public void setEstado(int estado) {
		this.estado  = estado;
	}


	public String getEstadotxt() {
		return estadotxt;
	}

	public void setEstadotxt(String estadotxt) {
		this.estadotxt  = estadotxt;
	}



	public int getOrigensacop() {
		return origensacop;
	}

	public void setOrigensacop (int origensacop) {
		this.origensacop   = origensacop;
	}

	public java.util.Date getFechaemision() {
		return fechaemision;
	}

	public void setFechaemision(java.util.Date fechaemision) {
		this.fechaemision   = fechaemision;
	}

	public java.util.Date getFechasacops1() {
		return fechasacops1;
	}

	public void setFechasacops1(java.util.Date fechasacops1) {
		this.fechasacops1 = fechasacops1;
	}

	public String getRequisitosaplicable() {
		return requisitosaplicable;
	}

	public void setRequisitosaplicable(String requisitosaplicable) {
		this.requisitosaplicable= requisitosaplicable;
	}

	public String getProcesosafectados() {
		return procesosafectados;
	}

	public void setProcesosafectados(String procesosafectados) {
		this.procesosafectados= procesosafectados;
	}

	public String getSolicitudinforma() {
		return solicitudinforma;
	}

	public void setSolicitudinforma(String solicitudinforma) {
		this.solicitudinforma= solicitudinforma;
	}

	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion= descripcion;
	}

	public String getCausasnoconformidad() {
		return causasnoconformidad;
	}

	public void setCausasnoconformidad(String causasnoconformidad) {
		this.causasnoconformidad= causasnoconformidad;
	}

	public String getAccionesrecomendadas() {
		return accionesrecomendadas;
	}

	public void setAccionesrecomendadas(String accionesrecomendadas) {
		this.accionesrecomendadas= accionesrecomendadas;
	}

	public byte getActive() {
		return active;
	}

	public void setActive(byte active) {
		this.active= active;
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

	public long getIdplanillasacop1esqueleto() {
		return idplanillasacop1esqueleto;
	}

	public void setIdplanillasacop1esqueleto(long idplanillasacop1esqueleto) {
		this.idplanillasacop1esqueleto = idplanillasacop1esqueleto;
	}

	public int getIdClasificacion() {
		return idClasificacion;
	}

	public void setIdClasificacion(int idClasificacion) {
		this.idClasificacion = idClasificacion;
	}

	public Date getFechaWhenDiscovered() {
		return fechaWhenDiscovered;
	}

	public void setFechaWhenDiscovered(java.util.Date fechaWhenDiscovered) {
		this.fechaWhenDiscovered = fechaWhenDiscovered;
	}

	public void setNameRelatedDocument(String nameRelatedDocument){
		this.nameRelatedDocument = nameRelatedDocument;
	}

	public String getNameRelatedDocument(){
		return nameRelatedDocument;
	}
	
	public Date getFechaVerificacion() {
		return fechaVerificacion;
	}
	
	public void setFechaVerificacion(Date fechaVerificacion) {
		this.fechaVerificacion = fechaVerificacion;
	}
	
	public Date getFechaCierre() {
		return fechaCierre;
	}
	
	public void setFechaCierre(Date fechaCierre) {
		this.fechaCierre = fechaCierre;
	}

	public int getIdDocumentRelated() {
		return idDocumentRelated;
	}

	public void setIdDocumentRelated(int idDocumentRelated) {
		this.idDocumentRelated = idDocumentRelated;
	}

	public int getIdDocumentAssociate() {
		return idDocumentAssociate;
	}

	public void setIdDocumentAssociate(int idDocumentAssociate) {
		this.idDocumentAssociate = idDocumentAssociate;
	}

	public int getNumVerDocumentAssociate() {
		return numVerDocumentAssociate;
	}

	public void setNumVerDocumentAssociate(int numVerDocumentAssociate) {
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
	
}
