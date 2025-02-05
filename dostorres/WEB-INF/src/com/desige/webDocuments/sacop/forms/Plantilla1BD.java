package com.desige.webDocuments.sacop.forms;

import java.io.Serializable;
import java.text.ParseException;
import java.util.Arrays;
import java.util.Date;
import java.util.Map;

import com.desige.webDocuments.utils.ToolsHTML;
import com.desige.webDocuments.utils.beans.SuperActionForm;
import com.focus.qweb.to.ActionRecommendedTO;
import com.focus.qweb.to.PlanillaSacop1EsqueletoTO;
import com.focus.qweb.to.PlanillaSacop1TO;

/**
 * Created by IntelliJ IDEA.
 * User: srodriguez
 * Date: 05/12/2005
 * Time: 02:05:48 PM
 * To change this template use File | Settings | File Templates.
 */

/**
 * Title: Plantilla1BD.java <br/>
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
public class Plantilla1BD extends SuperActionForm implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 2778153873688298067L;

	private long idplanillasacop1;
	private String sacopnum;
	private long emisor;
	private String emisortxt;
	private String solicitantetxt;
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
	private String accionesEstablecidas;
	private String accionesEstablecidastxt;
	private String eliminarcausaraiz;
	private String eliminarcausaraiztxt;
	// el usernotificado es el cargo del area afectada
	// private long usernotificado;
	private long idcargodelareaafectada;
	private String usernotificadotxt;
	private String cargoEmisor;
	private String cargoSolicitante;
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
	private String continuar_Sacop_Intouch;
	private String habilitadoEsqueleto;
	private Integer idClasificacion;
	private Date fechaWhenDiscovered;
	private String nameRelatedDocument;
	private String archivoTecnica;
	private String nombreClasificacionSACOP;
	private Date fechaVerificacion;
	private Date fechaCierre;
	private Integer idDocumentRelated;
	private Integer usuarioSacops1;
	private Integer idDocumentAssociate;
	private Integer numVerDocumentAssociate;
	private String nameDocumentAssociate;
	private String requireTracking;
	private String requireTrackingDate;
	private String trackingSacop;
	private String numberTrackingSacop;
	
	private Integer idRegisterGenerated;
	private String descripcionAccionPrincipal;
	
	// no field in db
	private String titulosplanillas;

	/**
	 * 
	 */
	public Plantilla1BD(PlanillaSacop1TO o) {
		this.setIdplanillasacop1(Long.parseLong(o.getIdplanillasacop1()));
		this.setSacopnum(o.getSacopnum());
		this.setEmisor(Long.parseLong(o.getEmisor()));
		this.setUsernotificado(Long.parseLong(o.getUsernotificado()));
		this.setRespblearea(Long.parseLong(o.getRespblearea()));
		this.setEstado(Integer.parseInt(o.getEstado()));
		this.setOrigensacop(Integer.parseInt(o.getOrigensacop()));
		try {
			if(o.getFechaemision()!=null) {
				this.setFechaemision(ToolsHTML.sdf1.parse(o.getFechaemision()));
			}
		} catch (ParseException e1) {
			e1.printStackTrace();
		}
		try {
			if(o.getFechaSacops1()!=null) {
				this.setFechasacops1(ToolsHTML.sdf1.parse(o.getFechaSacops1()));
			}
		} catch (ParseException e1) {
			e1.printStackTrace();
		}
		this.setRequisitosaplicable(o.getRequisitosaplicable());
		this.setProcesosafectados(o.getProcesosafectados());
		this.setSolicitudinforma(o.getSolicitudinforma());
		this.setDescripcion(o.getDescripcion());
		this.setCausasnoconformidad(o.getCausasnoconformidad());
		this.setAccionesrecomendadas(o.getAccionesrecomendadas());
		this.setCorrecpreven(o.getCorrecpreven());
		this.setRechazoapruebo(o.getRechazoapruebo());
		this.setNoaceptada(o.getNoaceptada());
		this.setAccionobservacion(o.getAccionobservacion());
		this.setDescripcionAccionPrincipal(o.getDescripcionAccionPrincipal());
		this.setFechaEstimada(o.getFechaestimada());
		this.setFechaReal(o.getFechareal());
		this.setAccionesEstablecidas(o.getAccionesEstablecidas());
		this.setAccionesEstablecidastxt(o.getAccionesestablecidastxt());
		this.setEliminarcausaraiz(o.getEliminarcausaraiz());
		this.setEliminarcausaraiztxt(o.getEliminarcausaraiztxt());
		this.setNameFile(o.getNameFile());
		this.setContentType(o.getContentType());
		//activecomntresponsablecerrar;
		this.setActive(Byte.parseByte(o.getActive()));
		this.setFechaculminar(o.getFechaculminar());
		this.setComntresponsablecerrar(o.getComntresponsablecerrar());
		this.setSacop_relacionadas(o.getSacopRelacionadas());
		this.setNoconformidadesref(o.getNoconformidadesref());
		this.setNoconformidades(o.getNoconformidades());
		this.setIdplanillasacop1esqueleto(Long.parseLong(o.getIdplanillasacop1esqueleto()));
		this.setIdClasificacion(Integer.parseInt(o.getClasificacion()));
		try {
			this.setFechaWhenDiscovered(ToolsHTML.sdf1.parse(o.getFechaWhenDiscovered()));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		this.setArchivoTecnica(o.getArchivoTecnica());
		try {
			if(o.getFechaVerificacion()!=null) {
				this.setFechaVerificacion(ToolsHTML.sdf1.parse(o.getFechaVerificacion()));
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}
		try {
			if(o.getFechaCierre()!=null) {
				this.setFechaCierre(ToolsHTML.sdf1.parse(o.getFechaCierre()));
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}
		this.setIdDocumentRelated(ToolsHTML.parseInt(o.getIdDocumentRelated()));
		this.setIdDocumentAssociate(ToolsHTML.parseInt(o.getIdDocumentAssociate()));
		this.setNumVerDocumentAssociate(ToolsHTML.parseInt(o.getNumVerDocumentAssociate()));
		this.setNameDocumentAssociate(o.getNameDocumentAssociate());
		//xxx
	}

	/**
	 * 
	 * @param objbd
	 */
	public Plantilla1BD(Plantilla1BDesqueleto objbd) {
		this.idplanillasacop1 = objbd.getIdplanillasacop1();
		this.sacopnum = objbd.getSacopnum();
		this.emisor = objbd.getEmisor();
		this.emisortxt = objbd.getEmisortxt();
		this.respblearea = objbd.getRespblearea();
		this.respbleareatxt = objbd.getRespbleareatxt();
		this.estado = objbd.getEstado();
		this.estadotxt = objbd.getEstadotxt();
		this.origensacop = objbd.getOrigensacop();
		this.fechaemision = objbd.getFechaemision();
		this.fechasacops1 = objbd.getFechasacops1();
		this.requisitosaplicable = objbd.getRequisitosaplicable();
		this.procesosafectados = objbd.getProcesosafectados();
		this.solicitudinforma = objbd.getSolicitudinforma();
		this.accionesrecomendadas = objbd.getAccionesrecomendadas();
		this.active = objbd.getActive();
		this.descripcion = objbd.getDescripcion();
		this.causasnoconformidad = objbd.getCausasnoconformidad();
		this.accionrecomendada = objbd.getAccionrecomendada();
		this.edodelsacop = objbd.getEdodelsacop();
		this.planilla = objbd.getPlanilla();
		this.normsisoSelected = objbd.getNormsisoSelected();
		this.proceafectadosSelected = objbd.getProceafectadosSelected();
		this.usersSelected = objbd.getUsersSelected();
		this.correcpreven = objbd.getCorrecpreven();
		this.apellidos = objbd.getApellidos();
		this.nombres = objbd.getNombres();
		this.cargo = objbd.getCargo();
		this.id = objbd.getId();
		this.user = objbd.getUser();
		this.noaceptada = objbd.getNoaceptada();
		this.rechazoapruebo = objbd.getNoaceptada();
		this.accionobservacion = objbd.getAccionobservacion();
		this.fechaEstimada = objbd.getFechaEstimada();
		this.fechaReal = objbd.getFechaReal();
		this.accionesEstablecidas = objbd.getAccionesEstablecidas();
		this.accionesEstablecidastxt = objbd.getAccionesEstablecidastxt();
		this.eliminarcausaraiz = objbd.getEliminarcausaraiz();
		this.eliminarcausaraiztxt = objbd.getEliminarcausaraiztxt();
		// el usernotificado es el cargo del area afectada
		// private long usernotificado;
		this.idcargodelareaafectada = objbd.getUsernotificado();
		this.usernotificadotxt = objbd.getUsernotificadotxt();
		this.cargoEmisor = objbd.getCargoEmisor();
		this.mostrarCargo = objbd.getMostrarCargo();
		this.eliminarDeLista = objbd.getEliminarDeLista();
		this.isEmisor = objbd.getIsEmisor();
		this.isResponsable = objbd.getIsResponsable();
		this.contentType = objbd.getContentType();
		this.nameFile = objbd.getNameFile();
		this.fechaculminar = objbd.getFechaculminar();
		this.comntresponsablecerrar = objbd.getComntresponsablecerrar();
		this.sacop_relacionadas = objbd.getSacop_relacionadas();
		this.selected = objbd.isSelected();
		this.noconformidades = objbd.getNoconformidades();
		this.noconformidadesref = objbd.getNoconformidadesref();
		this.setIdplanillasacop1esqueleto(objbd.getIdplanillasacop1esqueleto());
		this.setIdClasificacion(objbd.getIdClasificacion());
		this.setFechaWhenDiscovered(objbd.getFechaWhenDiscovered());
		this.setNameRelatedDocument(objbd.getNameRelatedDocument());
		this.setFechaVerificacion(objbd.getFechaVerificacion());
		this.setFechaCierre(objbd.getFechaCierre());
		this.setIdDocumentRelated(objbd.getIdDocumentRelated());
	}

	public Plantilla1BD(PlanillaSacop1EsqueletoTO o) {
		
		this.idplanillasacop1 = Long.parseLong(o.getIdplanillasacop1());
		this.sacopnum = o.getSacopnum();
		this.emisor = Long.parseLong(o.getEmisor());
		this.usernotificadotxt = o.getUsernotificado();
		this.respblearea = Long.parseLong(o.getRespblearea());
		this.estado = Integer.parseInt(o.getEstado());
		this.origensacop = Integer.parseInt(o.getOrigensacop());
		try {
			this.fechaemision = ToolsHTML.sdf1.parse(o.getFechaemision());
		} catch (ParseException e) {
			e.printStackTrace();
		}
		try {
			this.fechasacops1 = ToolsHTML.sdf1.parse(o.getFechasacops1());
		} catch (ParseException e) {
			e.printStackTrace();
		}
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
		this.fechaEstimada = o.getFechaestimada();
		this.fechaReal = o.getFechareal();
		this.accionesEstablecidas = o.getAccionesEstablecidas();
		this.accionesEstablecidastxt = o.getAccionesEstablecidastxt();
		this.eliminarcausaraiz = o.getEliminarcausaraiz();
		this.eliminarcausaraiztxt = o.getEliminarcausaraiztxt();
		this.nameFile = o.getNameFile();
		this.contentType = o.getContentType();
		this.active = Byte.parseByte(o.getActive());
		this.fechaculminar = o.getFechaculminar();
		this.comntresponsablecerrar = o.getComntresponsablecerrar();
		this.sacop_relacionadas = o.getSacopRelacionadas();
		//this.sacopnumnumgenVersion = o.getNumgenVersion();
		this.noconformidades = o.getNoconformidades();
		this.noconformidadesref = o.getNoconformidadesref();
		//this.estadoEsqueletoConfiguradoSacop = o.getEstadoEsqueletoConfiguradoSacop();
		
	}
	
	public Plantilla1BD() {
		
	}

	public String getComntresponsablecerrar() {
		return comntresponsablecerrar;
	}

	public void setComntresponsablecerrar(String comntresponsablecerrar) {
		this.comntresponsablecerrar = comntresponsablecerrar;
	}

	public String getFechaculminar() {
		return fechaculminar;
	}

	public void setFechaculminar(String fechaculminar) {
		this.fechaculminar = fechaculminar;
	}

	// aqui se carga el archivo como tal
	public String getNameFile() {
		return nameFile;
	}

	public void setNameFile(String nameFile) {
		this.nameFile = nameFile;
	}

	// -----------------------------------------

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
		this.isEmisor = isEmisor;
	}

	public boolean getIsResponsable() {
		return isResponsable;
	}

	public void setIsResponsable(boolean isResponsable) {
		this.isResponsable = isResponsable;
	}

	public boolean getEliminarDeLista() {
		return eliminarDeLista;
	}

	public void setEliminarDeLista(boolean eliminarDeLista) {
		this.eliminarDeLista = eliminarDeLista;
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
		this.mostrarCargo = mostrarCargo;
	}

	public String getUsernotificadotxt() {
		return usernotificadotxt;
	}

	public void setUsernotificadotxt(String usernotificadotxt) {
		this.usernotificadotxt = usernotificadotxt;
	}

	public long getUsernotificado() {
		return idcargodelareaafectada;
	}

	public void setUsernotificado(long idcargodelareaafectada) {
		this.idcargodelareaafectada = idcargodelareaafectada;
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

	public String getFechaReal() {
		return fechaReal;
	}

	public void setFechaReal(String fechaReal) {
		this.fechaReal = fechaReal;
	}

	public String getFechaEstimada() {
		return fechaEstimada;
	}

	public void setFechaEstimada(String fechaEstimada) {
		this.fechaEstimada = fechaEstimada;
	}

	public String getAccionobservacion() {
		return accionobservacion;
	}

	public void setAccionobservacion(String accionobservacion) {
		this.accionobservacion = accionobservacion;
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
		this.planilla = planilla;
	}

	public String getEdodelsacop() {
		return edodelsacop;
	}

	public void setEdodelsacop(String edodelsacop) {
		this.edodelsacop = edodelsacop;
	}

	public String getAccionrecomendada() {
		return accionrecomendada;
	}

	public void setAccionrecomendada(String accionrecomendada) {
		this.accionrecomendada = accionrecomendada;
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
	 * descripcion causasnoconformidad accionrecomendada id correcpreven
	 * getNombres getApellidos nombres user cargo edodelsacop planilla
	 * fechaEmision
	 */

	public long getIdplanillasacop1() {
		return idplanillasacop1;
	}

	public void setIdplanillasacop1(long idplanillasacop1) {
		this.idplanillasacop1 = idplanillasacop1;
	}

	public String getSacopnum() {
		return sacopnum;
	}

	public void setSacopnum(String sacopnum) {
		this.sacopnum = sacopnum;
	}

	public long getEmisor() {
		return emisor;
	}

	public void setEmisor(long emisor) {
		this.emisor = emisor;
	}

	public void setEmisortxt(String emisortxt) {
		this.emisortxt = emisortxt;
	}

	public String getEmisortxt() {
		return emisortxt;
	}

	public long getRespblearea() {
		return respblearea;
	}

	public void setRespblearea(long respblearea) {
		this.respblearea = respblearea;
	}

	public String getRespbleareatxt() {
		return respbleareatxt;
	}

	public void setRespbleareatxt(String respbleareatxt) {
		this.respbleareatxt = respbleareatxt;
	}

	public int getEstado() {
		return estado;
	}

	public void setEstado(int estado) {
		this.estado = estado;
	}

	public String getEstadotxt() {
		return estadotxt;
	}

	public void setEstadotxt(String estadotxt) {
		this.estadotxt = estadotxt;
	}

	public int getOrigensacop() {
		return origensacop;
	}

	public void setOrigensacop(int origensacop) {
		this.origensacop = origensacop;
	}

	public java.util.Date getFechaemision() {
		return fechaemision;
	}

	public void setFechaemision(java.util.Date fechaemision) {
		this.fechaemision = fechaemision;
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

	public String getAccionesrecomendadasString(Map<String,ActionRecommendedTO> lista) {
		StringBuffer sb = new StringBuffer();
		
		if(accionesrecomendadas!=null) {
			String[] arr = accionesrecomendadas.split(",");
			String sep = "";
			String coma = ",";
			
			for (int i = 0; i < arr.length; i++) {
				if(lista.containsKey(arr[i])) {
					sb.append(sep).append(lista.get(arr[i]).getDescActionRecommended());
					sep = coma;
				}
			}
		}
		
		return sb.toString();
	}
	
	public void setAccionesrecomendadas(String accionesrecomendadas) {
		this.accionesrecomendadas = accionesrecomendadas;
	}

	public byte getActive() {
		return active;
	}

	public void setActive(byte active) {
		this.active = active;
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

	public String getHabilitadoEsqueleto() {
		return habilitadoEsqueleto;
	}

	public void setHabilitadoEsqueleto(String habilitadoEsqueleto) {
		this.habilitadoEsqueleto = habilitadoEsqueleto;
	}

	public String getContinuar_Sacop_Intouch() {
		return continuar_Sacop_Intouch;
	}

	public void setContinuar_Sacop_Intouch(String continuar_Sacop_Intouch) {
		this.continuar_Sacop_Intouch = continuar_Sacop_Intouch;
	}

	public Integer getIdClasificacion() {
		return idClasificacion;
	}

	public void setIdClasificacion(Integer idClasificacion) {
		this.idClasificacion = idClasificacion;
	}

	public java.util.Date getFechaWhenDiscovered() {
		return fechaWhenDiscovered;
	}

	public void setFechaWhenDiscovered(java.util.Date fechaWhenDiscovered) {
		this.fechaWhenDiscovered = fechaWhenDiscovered;
	}

	public void setNameRelatedDocument(String nameRelatedDocument) {
		this.nameRelatedDocument = nameRelatedDocument;
	}

	public String getNameRelatedDocument() {
		return nameRelatedDocument;
	}

	public void setArchivoTecnica(String archivoTecnica) {
		this.archivoTecnica = archivoTecnica;
	}

	public String getArchivoTecnica() {
		return archivoTecnica;
	}

	public void setNombreClasificacionSACOP(String nombreClasificacionSACOP) {
		this.nombreClasificacionSACOP = nombreClasificacionSACOP;
	}

	public String getNombreClasificacionSACOP() {
		return nombreClasificacionSACOP;
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

	
	public String getTitulosplanillas() {
		return titulosplanillas;
	}

	public void setTitulosplanillas(String titulosplanillas) {
		this.titulosplanillas = titulosplanillas;
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
	
	

	public Integer getUsuarioSacops1() {
		return usuarioSacops1;
	}

	public void setUsuarioSacops1(Integer usuarioSacops1) {
		this.usuarioSacops1 = usuarioSacops1;
	}

	public Integer getIdDocumentAssociate() {
		return idDocumentAssociate;
	}

	public void setIdDocumentAssociate(Integer idDocumentAssociate) {
		this.idDocumentAssociate = idDocumentAssociate;
	}

	public Integer getNumVerDocumentAssociate() {
		return numVerDocumentAssociate;
	}

	public void setNumVerDocumentAssociate(Integer numVerDocumentAssociate) {
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

	public void setIdDocumentRelated(Integer idDocumentRelated) {
		this.idDocumentRelated = idDocumentRelated;
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
		builder.append("Plantilla1BD [idplanillasacop1=")
				.append(idplanillasacop1).append(", sacopnum=")
				.append(sacopnum).append(", emisor=").append(emisor)
				.append(", emisortxt=").append(emisortxt)
				.append(", respblearea=").append(respblearea)
				.append(", respbleareatxt=").append(respbleareatxt)
				.append(", estado=").append(estado).append(", estadotxt=")
				.append(estadotxt).append(", origensacop=").append(origensacop)
				.append(", fechaemision=").append(fechaemision)
				.append(", requisitosaplicable=").append(requisitosaplicable)
				.append(", procesosafectados=").append(procesosafectados)
				.append(", solicitudinforma=").append(solicitudinforma)
				.append(", accionesrecomendadas=").append(accionesrecomendadas)
				.append(", active=").append(active).append(", descripcion=")
				.append(descripcion).append(", causasnoconformidad=")
				.append(causasnoconformidad).append(", accionrecomendada=")
				.append(accionrecomendada).append(", edodelsacop=")
				.append(edodelsacop).append(", planilla=").append(planilla)
				.append(", normsisoSelected=")
				.append(Arrays.toString(normsisoSelected))
				.append(", proceafectadosSelected=")
				.append(Arrays.toString(proceafectadosSelected))
				.append(", usersSelected=")
				.append(Arrays.toString(usersSelected))
				.append(", correcpreven=").append(correcpreven)
				.append(", apellidos=").append(apellidos).append(", nombres=")
				.append(nombres).append(", cargo=").append(cargo)
				.append(", id=").append(id).append(", user=").append(user)
				.append(", noaceptada=").append(noaceptada)
				.append(", rechazoapruebo=").append(rechazoapruebo)
				.append(", accionobservacion=").append(accionobservacion)
				.append(", fechaEstimada=").append(fechaEstimada)
				.append(", fechaReal=").append(fechaReal)
				.append(", accionesEstablecidas=").append(accionesEstablecidas)
				.append(", accionesEstablecidastxt=")
				.append(accionesEstablecidastxt).append(", eliminarcausaraiz=")
				.append(eliminarcausaraiz).append(", eliminarcausaraiztxt=")
				.append(eliminarcausaraiztxt)
				.append(", idcargodelareaafectada=")
				.append(idcargodelareaafectada).append(", usernotificadotxt=")
				.append(usernotificadotxt).append(", cargoEmisor=")
				.append(cargoEmisor).append(", mostrarCargo=")
				.append(mostrarCargo).append(", eliminarDeLista=")
				.append(eliminarDeLista).append(", isEmisor=").append(isEmisor)
				.append(", isResponsable=").append(isResponsable)
				.append(", contentType=").append(contentType)
				.append(", nameFile=").append(nameFile).append(", fechaculminar=")
				.append(fechaculminar).append(", comntresponsablecerrar=")
				.append(comntresponsablecerrar).append(", sacop_relacionadas=")
				.append(sacop_relacionadas).append(", selected=")
				.append(selected).append(", noconformidades=")
				.append(noconformidades).append(", noconformidadesref=")
				.append(noconformidadesref)
				.append(", idplanillasacop1esqueleto=")
				.append(idplanillasacop1esqueleto)
				.append(", continuar_Sacop_Intouch=")
				.append(continuar_Sacop_Intouch)
				.append(", habilitadoEsqueleto=").append(habilitadoEsqueleto)
				.append(", idClasificacion=").append(idClasificacion)
				.append(", fechaWhenDiscovered=").append(fechaWhenDiscovered)
				.append(", nameRelatedDocument=").append(nameRelatedDocument)
				.append(", archivoTecnica=").append(archivoTecnica)
				.append(", nombreClasificacionSACOP=")
				.append(nombreClasificacionSACOP)
				.append(", fechaVerificacion=").append(fechaVerificacion)
				.append(", fechaCierre=").append(fechaCierre).append("]");
		return builder.toString();
	}

}
