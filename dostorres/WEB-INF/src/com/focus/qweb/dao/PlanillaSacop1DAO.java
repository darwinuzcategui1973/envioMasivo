package com.focus.qweb.dao;

import java.sql.Clob;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;

import com.desige.webDocuments.persistent.managers.HandlerStruct;
import com.desige.webDocuments.persistent.utils.JDBCUtil;
import com.desige.webDocuments.utils.Constants;
import com.desige.webDocuments.utils.ToolsHTML;
import com.focus.qweb.interfaces.ObjetoDAO;
import com.focus.qweb.interfaces.ObjetoTO;
import com.focus.qweb.to.PlanillaSacop1TO;

import sun.jdbc.rowset.CachedRowSet;

/**
 * 
 * @author JRivero
 * 
 */
public class PlanillaSacop1DAO implements ObjetoDAO {

	private StringBuffer query = new StringBuffer();
	private ArrayList<Object> parametros = null;
	PlanillaSacop1TO planillaSacop1TO = null;
	
	Connection con = null;
	
	public PlanillaSacop1DAO() {
		
	}

	public PlanillaSacop1DAO(Connection con) {
		this.con = con;
	}

	/**
	 * 
	 */
	public int insertar(ObjetoTO oPlanillaSacop1TO) throws Exception {
		int act = 0;
		try {
			
			planillaSacop1TO = (PlanillaSacop1TO) oPlanillaSacop1TO;
	
			if(planillaSacop1TO.getIdplanillasacop1Int()==0) {
				int idplanillasacop1 = HandlerStruct.proximo("idplanillasacop1",
						"idplanillasacop1",
						"idplanillasacop1");
				
				planillaSacop1TO.setIdplanillasacop1(String.valueOf(idplanillasacop1));
			}
	
	
			query.setLength(0);
			query.append("INSERT INTO tbl_planillasacop1 (");
			query.append("idplanillasacop1,sacopnum,emisor,usernotificado,respblearea,estado,origensacop,fechaemision, ");
			query.append("requisitosaplicable,procesosafectados,solicitudinforma,descripcion,causasnoconformidad, ");
			query.append("accionesrecomendadas,correcpreven,rechazoapruebo,noaceptada,accionobservacion, ");
			query.append("fechaestimada,fechareal,accionesEstablecidas,accionesestablecidastxt,eliminarcausaraiz, ");
			query.append("eliminarcausaraiztxt,nameFile,contentType,activecomntresponsablecerrar, ");
			query.append("active,fechaculminar,comntresponsablecerrar,sacop_relacionadas,noconformidadesref, ");
			query.append("noconformidades,idplanillasacop1esqueleto,clasificacion,fechaWhenDiscovered, ");
			query.append("archivo_tecnica,fecha_verificacion,fecha_cierre, ");
			query.append("usuarioSacops1,fechaSacops1,idDocumentRelated, ");
			query.append("idDocumentAssociate, numVerDocumentAssociate, nameDocumentAssociate, ");
			query.append("requireTracking,requireTrackingDate,trackingSacop,numberTrackingSacop,idRegisterGenerated,descripcionAccionPrincipal ");
			query.append(") ");
			query.append("VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
			
			
			parametros = new ArrayList<Object>();
			parametros.add(planillaSacop1TO.getIdplanillasacop1Int());
			parametros.add(planillaSacop1TO.getSacopnum());
			parametros.add(planillaSacop1TO.getEmisorInt());
			parametros.add(planillaSacop1TO.getUsernotificadoInt());
			parametros.add(planillaSacop1TO.getRespbleareaInt());
			parametros.add(planillaSacop1TO.getEstadoInt());
			parametros.add(planillaSacop1TO.getOrigensacopInt());
			// parametros.add(planillaSacop1TO.getFechaemisionTimestamp());
			if(Constants.MANEJADOR_POSTGRES == Constants.MANEJADOR_ACTUAL) {
				parametros.add(planillaSacop1TO.getFechaemisionTimestamp());
				
			} else { 
			parametros.add(planillaSacop1TO.getFechaemision());
			}
			parametros.add(planillaSacop1TO.getRequisitosaplicable());
			parametros.add(planillaSacop1TO.getProcesosafectados());
			parametros.add(planillaSacop1TO.getSolicitudinforma());
			parametros.add(planillaSacop1TO.getDescripcion());
			parametros.add(planillaSacop1TO.getCausasnoconformidad());
			parametros.add(planillaSacop1TO.getAccionesrecomendadas());
			parametros.add(planillaSacop1TO.getCorrecpreven());
			parametros.add(planillaSacop1TO.getRechazoapruebo());
			parametros.add(planillaSacop1TO.getNoaceptada());
			parametros.add(planillaSacop1TO.getAccionobservacion());
			parametros.add(planillaSacop1TO.getFechaestimada());
			parametros.add(planillaSacop1TO.getFechareal());
			parametros.add(planillaSacop1TO.getAccionesEstablecidas());
			parametros.add(planillaSacop1TO.getAccionesestablecidastxt());
			parametros.add(planillaSacop1TO.getEliminarcausaraiz());
			parametros.add(planillaSacop1TO.getEliminarcausaraiztxt());
			parametros.add(planillaSacop1TO.getNameFile());
			parametros.add(planillaSacop1TO.getContentType());
			parametros.add(planillaSacop1TO.getActivecomntresponsablecerrar());
			parametros.add(planillaSacop1TO.getActiveInt());
			parametros.add(planillaSacop1TO.getFechaculminar());
			parametros.add(planillaSacop1TO.getComntresponsablecerrar());
			parametros.add(planillaSacop1TO.getSacopRelacionadas());
			parametros.add(planillaSacop1TO.getNoconformidadesref());
			parametros.add(planillaSacop1TO.getNoconformidades());
			parametros.add(planillaSacop1TO.getIdplanillasacop1esqueletoInt());
			parametros.add(planillaSacop1TO.getClasificacionInt());
			parametros.add(planillaSacop1TO.getFechaWhenDiscoveredTimestamp());
			parametros.add(planillaSacop1TO.getArchivoTecnica());
			parametros.add(planillaSacop1TO.getFechaVerificacionTimestamp());
			parametros.add(planillaSacop1TO.getFechaCierreTimestamp());
			parametros.add(planillaSacop1TO.getUsuarioSacops1Int());
			parametros.add(planillaSacop1TO.getFechaSacops1Timestamp());
			parametros.add(planillaSacop1TO.getIdDocumentRelatedInt());
			parametros.add(planillaSacop1TO.getIdDocumentAssociateInt());
			parametros.add(planillaSacop1TO.getNumVerDocumentAssociateInt());
			parametros.add(planillaSacop1TO.getNameDocumentAssociate());
			parametros.add(planillaSacop1TO.getRequireTrackingInt());
			parametros.add(planillaSacop1TO.getRequireTrackingDateTimestamp());
			parametros.add(ToolsHTML.parseInt(planillaSacop1TO.getTrackingSacop(),0));
			parametros.add(ToolsHTML.isEmptyOrNull(planillaSacop1TO.getNumberTrackingSacop(),""));
			parametros.add(ToolsHTML.parseInt(planillaSacop1TO.getIdRegisterGenerated(),0));
			parametros.add(ToolsHTML.isEmptyOrNull(planillaSacop1TO.getDescripcionAccionPrincipal(),""));
			
			
		
			act = JDBCUtil.executeUpdate(query, parametros, con);
		} catch(Exception e) {
			e.printStackTrace();
			throw e;
		}
		return act;
	}

	/**
	 * 
	 */
	public int actualizar(ObjetoTO oPlanillaSacop1TO) throws Exception {
		planillaSacop1TO = (PlanillaSacop1TO) oPlanillaSacop1TO;

		query.setLength(0);
		query.append("UPDATE tbl_planillasacop1 SET ");
		query.append("sacopnum=?,emisor=?,usernotificado=?,respblearea=?,estado=?,origensacop=?,fechaemision=?, ");
		query.append("requisitosaplicable=?,procesosafectados=?,solicitudinforma=?,descripcion=?,causasnoconformidad=?, ");
		query.append("accionesrecomendadas=?,correcpreven=?,rechazoapruebo=?,noaceptada=?,accionobservacion=?, ");
		query.append("fechaestimada=?,fechareal=?,accionesEstablecidas=?,accionesestablecidastxt=?,eliminarcausaraiz=?, ");
		query.append("eliminarcausaraiztxt=?,nameFile=?,contentType=?,activecomntresponsablecerrar=?, ");
		query.append("active=?,fechaculminar=?,comntresponsablecerrar=?,sacop_relacionadas=?,noconformidadesref=?, ");
		query.append("noconformidades=?,idplanillasacop1esqueleto=?,clasificacion=?,fechaWhenDiscovered=?, ");
		query.append("archivo_tecnica=?,fecha_verificacion=?,fecha_cierre=?, ");
		query.append("usuarioSacops1=?,idDocumentRelated=?, "); //fechaSacops1=?,
		query.append("idDocumentAssociate=?, numVerDocumentAssociate=?, nameDocumentAssociate=?, ");
		query.append("requireTracking=?, requireTrackingDate=?,trackingSacop=?,numberTrackingSacop=?,idRegisterGenerated=?, ");
		query.append("descripcionAccionPrincipal=? ");
		query.append("WHERE idplanillasacop1=?");

		parametros = new ArrayList<Object>();
		parametros.add(planillaSacop1TO.getSacopnum());
		parametros.add(planillaSacop1TO.getEmisorInt());
		parametros.add(planillaSacop1TO.getUsernotificadoInt());
		parametros.add(planillaSacop1TO.getRespbleareaInt());
		parametros.add(planillaSacop1TO.getEstadoInt());
		parametros.add(planillaSacop1TO.getOrigensacopInt());
		if(Constants.MANEJADOR_MYSQL == Constants.MANEJADOR_ACTUAL)  {
			parametros.add(planillaSacop1TO.getFechaemision());
			
		} else { 
			parametros.add(planillaSacop1TO.getFechaemisionTimestamp());
		    //parametros.add(planillaSacop1TO.getFechaemision());
		}
		//parametros.add(planillaSacop1TO.getFechaemisionTimestamp());
		//parametros.add(planillaSacop1TO.getFechaemision());
		parametros.add(planillaSacop1TO.getRequisitosaplicable());
		parametros.add(planillaSacop1TO.getProcesosafectados());
		parametros.add(planillaSacop1TO.getSolicitudinforma());
		parametros.add(planillaSacop1TO.getDescripcion());
		parametros.add(planillaSacop1TO.getCausasnoconformidad());
		parametros.add(planillaSacop1TO.getAccionesrecomendadas());
		parametros.add(planillaSacop1TO.getCorrecpreven());
		parametros.add(planillaSacop1TO.getRechazoapruebo());
		parametros.add(planillaSacop1TO.getNoaceptada());
		parametros.add(planillaSacop1TO.getAccionobservacion());
		parametros.add(planillaSacop1TO.getFechaestimada());
		parametros.add(planillaSacop1TO.getFechareal());
		parametros.add(planillaSacop1TO.getAccionesEstablecidas());
		parametros.add(planillaSacop1TO.getAccionesestablecidastxt());
		parametros.add(planillaSacop1TO.getEliminarcausaraiz());
		parametros.add(planillaSacop1TO.getEliminarcausaraiztxt());
		parametros.add(planillaSacop1TO.getNameFile());
		parametros.add(planillaSacop1TO.getContentType());
		parametros.add(planillaSacop1TO.getActivecomntresponsablecerrar());
		parametros.add(planillaSacop1TO.getActiveInt());
		parametros.add(planillaSacop1TO.getFechaculminar());
		parametros.add(planillaSacop1TO.getComntresponsablecerrar());
		parametros.add(planillaSacop1TO.getSacopRelacionadas());
		parametros.add(planillaSacop1TO.getNoconformidadesref());
		parametros.add(planillaSacop1TO.getNoconformidades());
		parametros.add(planillaSacop1TO.getIdplanillasacop1esqueletoInt());
		parametros.add(planillaSacop1TO.getClasificacionInt());
		parametros.add(planillaSacop1TO.getFechaWhenDiscoveredTimestamp());
		parametros.add(planillaSacop1TO.getArchivoTecnica());
		parametros.add(planillaSacop1TO.getFechaVerificacionTimestamp());
		parametros.add(planillaSacop1TO.getFechaCierreTimestamp());
		parametros.add(planillaSacop1TO.getUsuarioSacops1Int());
		//parametros.add(planillaSacop1TO.getFechaSacops1Timestamp());
		parametros.add(planillaSacop1TO.getIdDocumentRelatedInt());
		parametros.add(planillaSacop1TO.getIdDocumentAssociateInt());
		parametros.add(planillaSacop1TO.getNumVerDocumentAssociateInt());
		parametros.add(planillaSacop1TO.getNameDocumentAssociate());
		parametros.add(planillaSacop1TO.getRequireTrackingInt());
		parametros.add(planillaSacop1TO.getRequireTrackingDateTimestamp());
		parametros.add(ToolsHTML.parseInt(planillaSacop1TO.getTrackingSacop()));
		parametros.add(ToolsHTML.isEmptyOrNull(planillaSacop1TO.getNumberTrackingSacop(),""));
		parametros.add(ToolsHTML.parseInt(planillaSacop1TO.getIdRegisterGenerated(),0));
		parametros.add(ToolsHTML.isEmptyOrNull(planillaSacop1TO.getDescripcionAccionPrincipal(),""));

		
		parametros.add(planillaSacop1TO.getIdplanillasacop1Int()); // primary key

		// si la sacop es de seguimiento actualizamos las relacionadas de la principal
		if(ToolsHTML.parseInt(planillaSacop1TO.getTrackingSacop())==1) {
			// relacionamos las sacops
			StringBuilder query1 = new StringBuilder();
			StringBuilder query2 = new StringBuilder();
			StringBuilder query3 = new StringBuilder();
			
			query1.append("select idplanillasacop1,sacop_relacionadas from tbl_planillasacop1 where sacopnum='").append(planillaSacop1TO.getNumberTrackingSacop()).append("'");
			query2.append("select idplanillasacop1 from tbl_planillasacop1 where numberTrackingSacop='").append(planillaSacop1TO.getNumberTrackingSacop()).append("'");
			
			CachedRowSet crs1 = JDBCUtil.executeQuery(query1, "actualizar()");
			CachedRowSet crs2 = JDBCUtil.executeQuery(query2, "actualizar()");
			
			String relacionadas = "";
			String relacionadasCompara = "";
			if(crs1!=null && crs1.next()) {
				relacionadas = crs1.getString("sacop_relacionadas");
				relacionadasCompara = ",".concat(relacionadas).concat(",");
			}
			
			if(crs2!=null && crs2.size()>0) {
				StringBuilder ids = new StringBuilder();
				String coma = ",";
				String sep = !relacionadas.trim().equals("")?coma:"";
				String id= "";
				while(crs2.next()) {
					id = crs2.getString("idplanillasacop1");
					if(relacionadasCompara.indexOf(",".concat(id).concat(","))==-1) {
						relacionadas = relacionadas.concat(sep).concat(id);
						sep = coma;
					}
				}
				// cambio aqui ticket sustituciion de sacop un update sin where
				query3.append("update tbl_planillasacop1 set sacop_relacionadas='").append(relacionadas).append("' where idplanillasacop1=").append(id);
				JDBCUtil.executeUpdate(query3);				
			}
			
		}

	
		return JDBCUtil.executeUpdate(query, parametros, con);
		
	}
	
	/**
	 * 
	 */
	public void eliminar(ObjetoTO oPlanillaSacop1TO) throws Exception {
		planillaSacop1TO = (PlanillaSacop1TO) oPlanillaSacop1TO;

		query.setLength(0);
		query.append("DELETE FROM tbl_planillasacop1 WHERE idplanillasacop1=?");

		parametros = new ArrayList<Object>();
		parametros.add(planillaSacop1TO.getIdplanillasacop1Int());

		JDBCUtil.executeUpdate(query, parametros, con);
	}

	/**
	 * 
	 */
	public boolean cargar(ObjetoTO oPlanillaSacop1TO) throws Exception {
		planillaSacop1TO = (PlanillaSacop1TO) oPlanillaSacop1TO;

		query.setLength(0);
		query.append("SELECT * FROM tbl_planillasacop1 WHERE idplanillasacop1=").append(planillaSacop1TO.getIdplanillasacop1Int());

		CachedRowSet crs = JDBCUtil.executeQuery(query, null, con, Thread.currentThread().getStackTrace()[1].getMethodName());

		if (crs.next()) {
			load(crs, planillaSacop1TO);
			return true;
		}
		return false;
	}

	/**
	 * 
	 */
	public boolean cargarSacopPendienteDelDocumento(ObjetoTO oPlanillaSacop1TO) throws Exception {
		planillaSacop1TO = (PlanillaSacop1TO) oPlanillaSacop1TO;

		query.setLength(0);
		query.append("SELECT * ");
		query.append("FROM tbl_planillasacop1 WHERE idDocumentRelated=? ");
		query.append("AND estado NOT IN ('6','7') ");  // 6 RECHAZADO - 7 CERRADO
		
		parametros = new ArrayList<Object>();
		parametros.add(new Integer(planillaSacop1TO.getIdDocumentRelated()));

		CachedRowSet sacop = JDBCUtil.executeQuery(query, parametros, con, Thread.currentThread().getStackTrace()[1].getMethodName());

		if (sacop.next()) {
			load(sacop, planillaSacop1TO);
			return true;
		}
		return false;
	}

	/**
	 * 
	 */
	public ArrayList<PlanillaSacop1TO> listar() throws Exception {
		ArrayList<PlanillaSacop1TO> lista = new ArrayList<PlanillaSacop1TO>();

		query.setLength(0);
		query.append("SELECT * FROM tbl_planillasacop1 ORDER BY idplanillasacop1");

		CachedRowSet crs = JDBCUtil.executeQuery(query, Thread.currentThread().getStackTrace()[1].getMethodName());

		while (crs.next()) {
			planillaSacop1TO = new PlanillaSacop1TO();
			load(crs, planillaSacop1TO);
			lista.add(planillaSacop1TO);
		}
		return lista;
	}
	
	public ArrayList<PlanillaSacop1TO> listarById(String id) throws Exception {
		ArrayList<PlanillaSacop1TO> lista = new ArrayList<PlanillaSacop1TO>();

		query.setLength(0);
		query.append("SELECT * FROM tbl_planillasacop1 ");
		if (!ToolsHTML.isEmptyOrNull(id.toString())) {
			query.append("WHERE idplanillasacop1=").append(id).append(" ");
		} else {
			query.append("ORDER BY idplanillasacop1 ");
		}

		CachedRowSet crs = JDBCUtil.executeQuery(query, Thread.currentThread().getStackTrace()[1].getMethodName());

		while (crs.next()) {
			planillaSacop1TO = new PlanillaSacop1TO();
			load(crs, planillaSacop1TO);
			lista.add(planillaSacop1TO);
		}
		return lista;
	}
	public ArrayList<PlanillaSacop1TO> listarActive(String active) throws Exception {
		ArrayList<PlanillaSacop1TO> lista = new ArrayList<PlanillaSacop1TO>();

		query.setLength(0);
		query.append("SELECT * FROM tbl_planillasacop1 ");
		query.append("WHERE active=").append(active).append(" ");
		query.append("ORDER BY idplanillasacop1 ");

		CachedRowSet crs = JDBCUtil.executeQuery(query, Thread.currentThread().getStackTrace()[1].getMethodName());

		while (crs.next()) {
			planillaSacop1TO = new PlanillaSacop1TO();
			load(crs, planillaSacop1TO);
			lista.add(planillaSacop1TO);
		}
		return lista;
	}

	public ArrayList<PlanillaSacop1TO> listar(PlanillaSacop1TO oPlanillaSacop1TO) throws Exception {
		ArrayList<PlanillaSacop1TO> lista = new ArrayList<PlanillaSacop1TO>();
		boolean procesar = false;

		query.setLength(0);
		query.append("SELECT * FROM tbl_planillasacop1 ");
		if (!ToolsHTML.isEmptyOrNull(oPlanillaSacop1TO.getRespblearea())) {
			query.append("WHERE respblearea=").append(oPlanillaSacop1TO.getRespblearea());
			procesar = true;
		}

		if(procesar) {
			CachedRowSet crs = JDBCUtil.executeQuery(query, Thread.currentThread().getStackTrace()[1].getMethodName());
			while (crs.next()) {
				planillaSacop1TO = new PlanillaSacop1TO();
				load(crs, planillaSacop1TO);
				lista.add(planillaSacop1TO);
			}
		}
		return lista;
	}
	
	public ArrayList<PlanillaSacop1TO> listarByIdEsqueleto(String active, String id) throws Exception {
		ArrayList<PlanillaSacop1TO> lista = new ArrayList<PlanillaSacop1TO>();

		query.setLength(0);
		query.append("SELECT * FROM tbl_planillasacop1 ");
		query.append("WHERE active=").append(active).append(" ");
		if (!ToolsHTML.isEmptyOrNull(id.toString())) {
			query.append("AND idplanillasacop1esqueleto=").append(id);
		}

		CachedRowSet crs = JDBCUtil.executeQuery(query, Thread.currentThread().getStackTrace()[1].getMethodName());

		while (crs.next()) {
			planillaSacop1TO = new PlanillaSacop1TO();
			load(crs, planillaSacop1TO);
			lista.add(planillaSacop1TO);
		}
		return lista;
	}
	
	public boolean isSacopDeSeguimientoPendiente(String sacopNum) throws Exception {

		query.setLength(0);
		query.append("SELECT idplanillasacop1 ");
		query.append("FROM tbl_planillasacop1 WHERE trackingSacop=1 AND numberTrackingSacop=? ");
		query.append("AND estado NOT IN ('6','7') ");  // 6 RECHAZADO - 7 CERRADO
		
		parametros = new ArrayList<Object>();
		parametros.add(sacopNum);

		CachedRowSet sacop = JDBCUtil.executeQuery(query, parametros, con, Thread.currentThread().getStackTrace()[1].getMethodName());

		if (sacop.next()) {
			return true;
		}
		return false;
	}
	

	public void load(CachedRowSet crs, PlanillaSacop1TO planillaSacop1TO) throws SQLException {
		planillaSacop1TO.setIdplanillasacop1(crs.getString("idplanillasacop1"));
		planillaSacop1TO.setSacopnum(crs.getString("sacopnum"));
		planillaSacop1TO.setEmisor(crs.getString("emisor"));
		planillaSacop1TO.setUsernotificado(crs.getString("usernotificado"));
		planillaSacop1TO.setRespblearea(crs.getString("respblearea"));
		planillaSacop1TO.setEstado(crs.getString("estado"));
		planillaSacop1TO.setOrigensacop(crs.getString("origensacop"));
		planillaSacop1TO.setFechaemision(crs.getString("fechaemision"));
		planillaSacop1TO.setRequisitosaplicable(crs.getString("requisitosaplicable"));
		planillaSacop1TO.setProcesosafectados(crs.getString("procesosafectados"));
		planillaSacop1TO.setSolicitudinforma(crs.getString("solicitudinforma"));
		planillaSacop1TO.setDescripcion(crs.getString("descripcion"));
		planillaSacop1TO.setCausasnoconformidad(crs.getString("causasnoconformidad"));
		planillaSacop1TO.setAccionesrecomendadas(crs.getString("accionesrecomendadas"));
		planillaSacop1TO.setCorrecpreven(crs.getString("correcpreven"));
		planillaSacop1TO.setRechazoapruebo(crs.getString("rechazoapruebo"));
		planillaSacop1TO.setNoaceptada(crs.getString("noaceptada"));
		planillaSacop1TO.setAccionobservacion(crs.getString("accionobservacion"));
		planillaSacop1TO.setFechaestimada(crs.getString("fechaestimada"));
		planillaSacop1TO.setFechareal(crs.getString("fechareal"));
		planillaSacop1TO.setAccionesEstablecidas(crs.getString("accionesEstablecidas"));
		planillaSacop1TO.setAccionesestablecidastxt(crs.getString("accionesestablecidastxt"));
		planillaSacop1TO.setEliminarcausaraiz(crs.getString("eliminarcausaraiz"));
		planillaSacop1TO.setEliminarcausaraiztxt(crs.getString("eliminarcausaraiztxt"));
		planillaSacop1TO.setNameFile(crs.getString("nameFile"));
		planillaSacop1TO.setContentType(crs.getString("contentType"));
		planillaSacop1TO.setActivecomntresponsablecerrar(crs.getString("activecomntresponsablecerrar"));
		planillaSacop1TO.setActive(crs.getString("active"));
		planillaSacop1TO.setFechaculminar(crs.getString("fechaculminar"));
		if(Constants.MANEJADOR_ACTUAL==Constants.MANEJADOR_MYSQL) {
			planillaSacop1TO.setComntresponsablecerrar(crs.getString("comntresponsablecerrar"));
			
		}
		if(Constants.MANEJADOR_ACTUAL==Constants.MANEJADOR_POSTGRES) {
			planillaSacop1TO.setComntresponsablecerrar(crs.getString("comntresponsablecerrar"));
			
		}
		if(Constants.MANEJADOR_ACTUAL==Constants.MANEJADOR_MSSQL) {
			planillaSacop1TO.setComntresponsablecerrar(getClob(crs.getClob("comntresponsablecerrar")));
		}
		planillaSacop1TO.setSacopRelacionadas(crs.getString("sacop_relacionadas"));
		planillaSacop1TO.setNoconformidadesref(crs.getString("noconformidadesref"));
		planillaSacop1TO.setNoconformidades(crs.getString("noconformidades"));
		planillaSacop1TO.setIdplanillasacop1esqueleto(crs.getString("idplanillasacop1esqueleto"));
		planillaSacop1TO.setClasificacion(crs.getString("clasificacion"));
		planillaSacop1TO.setFechaWhenDiscovered(crs.getString("fechaWhenDiscovered"));
		planillaSacop1TO.setArchivoTecnica(crs.getString("archivo_tecnica"));
		planillaSacop1TO.setFechaVerificacion(crs.getString("fecha_verificacion"));
		planillaSacop1TO.setFechaCierre(crs.getString("fecha_cierre"));
		planillaSacop1TO.setUsuarioSacops1(crs.getString("usuarioSacops1"));
		planillaSacop1TO.setFechaSacops1(crs.getString("fechaSacops1")==null || crs.getString("fechaSacops1").trim().equals("")?crs.getString("fechaemision"):crs.getString("fechaSacops1"));
		planillaSacop1TO.setIdDocumentRelated(crs.getString("idDocumentRelated"));
		planillaSacop1TO.setIdDocumentAssociate(crs.getString("idDocumentAssociate"));
		planillaSacop1TO.setNumVerDocumentAssociate(crs.getString("numVerDocumentAssociate"));
		planillaSacop1TO.setNameDocumentAssociate(crs.getString("nameDocumentAssociate"));
		planillaSacop1TO.setRequireTracking(crs.getString("requireTracking"));
		planillaSacop1TO.setRequireTrackingDate(crs.getString("requireTrackingDate"));
		planillaSacop1TO.setTrackingSacop(crs.getString("trackingSacop"));
		planillaSacop1TO.setNumberTrackingSacop(crs.getString("numberTrackingSacop"));
		planillaSacop1TO.setIdRegisterGenerated(crs.getString("idRegisterGenerated"));
		planillaSacop1TO.setDescripcionAccionPrincipal(crs.getString("descripcionAccionPrincipal"));
		
	}

	private String getClob(Clob data) {
		try {
			return data.getSubString((long)0,(int)data.length());
		} catch (Exception e) {
			return " ";
		}
	}
	
	// fecha mayor de seguimiento
	public Date fechaSacopDeSeguimientoMayor(String sacopNum) throws Exception {

		query.setLength(0);
		query.append("SELECT max(fechaSacops1) ");
		query.append("FROM tbl_planillasacop1 WHERE trackingSacop=1 AND numberTrackingSacop=? ");
		query.append("AND estado NOT IN ('6') ");  // 6 RECHAZADO - 7 CERRADO
		
		parametros = new ArrayList<Object>();
		parametros.add(sacopNum);

		CachedRowSet sacop = JDBCUtil.executeQuery(query, parametros, con, Thread.currentThread().getStackTrace()[1].getMethodName());

	
		if (sacop.next()) {
		   System.out.print( sacop.getFetchSize());
		  // System.out.print( sacop.getDate(1));
			
			return sacop.getDate(1);
		}
		
		return sacop.getDate(1);
	}
}
