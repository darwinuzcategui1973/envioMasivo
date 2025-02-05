package com.focus.qweb.dao;

import java.sql.SQLException;
import java.util.ArrayList;

import sun.jdbc.rowset.CachedRowSet;

import com.desige.webDocuments.persistent.managers.HandlerStruct;
import com.desige.webDocuments.persistent.utils.JDBCUtil;
import com.desige.webDocuments.utils.Constants;
import com.desige.webDocuments.utils.ToolsHTML;
import com.focus.qweb.interfaces.ObjetoDAO;
import com.focus.qweb.interfaces.ObjetoTO;
import com.focus.qweb.to.PlanillaSacop1EsqueletoTO;

/**
 * 
 * @author JRivero
 * 
 */
public class PlanillaSacop1EsqueletoDAO implements ObjetoDAO {

	private StringBuffer query = new StringBuffer();
	private ArrayList<Object> parametros = null;
	PlanillaSacop1EsqueletoTO planillaSacop1EsqueletoTO = null;

	/**
	 * 
	 */
	public int insertar(ObjetoTO oPlanillaSacop1EsqueletoTO) throws Exception {
		planillaSacop1EsqueletoTO = (PlanillaSacop1EsqueletoTO) oPlanillaSacop1EsqueletoTO;

		if(planillaSacop1EsqueletoTO.getIdplanillasacop1Int()==0) {
			long idplanillasacop1 = HandlerStruct.proximo("idplanillasacop1esqueleto", "idplanillasacop1esqueleto", "idplanillasacop1esqueleto");
			
			planillaSacop1EsqueletoTO.setIdplanillasacop1(String.valueOf(idplanillasacop1));
		}
		
		
		query.setLength(0);
		query.append("INSERT INTO tbl_planillasacop1esqueleto (");
		query.append("idplanillasacop1,sacopnum,emisor,usernotificado,respblearea,estado, ");
		query.append("origensacop,fechaemision,requisitosaplicable,procesosafectados,solicitudinforma, ");
		query.append("descripcion,causasnoconformidad,accionesrecomendadas,correcpreven,rechazoapruebo, ");
		query.append("noaceptada,accionobservacion,fechaestimada,fechareal,accionesEstablecidas,accionesEstablecidastxt, ");
		query.append("eliminarcausaraiz,eliminarcausaraiztxt,nameFile,contentType,activecomntresponsablecerrar, ");
		query.append("active,fechaculminar,comntresponsablecerrar,sacop_relacionadas,numgen_version,noconformidades, ");
		query.append("noconformidadesref,estadoEsqueletoConfiguradoSacop,usuarioSacops1,fechaSacops1,idDocumentRelated, ");
		query.append("actionType,idDocumentAssociate,numVerDocumentAssociate,nameDocumentAssociate,requireTracking, ");
		query.append("requireTrackingDate,trackingSacop,numberTrackingSacop ");
		query.append(") ");
		query.append("VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
		
		parametros = new ArrayList<Object>();
		parametros.add(planillaSacop1EsqueletoTO.getIdplanillasacop1Int());
		parametros.add(planillaSacop1EsqueletoTO.getSacopnum());
		parametros.add(planillaSacop1EsqueletoTO.getEmisorInt());
		parametros.add(planillaSacop1EsqueletoTO.getUsernotificadoInt());
		parametros.add(planillaSacop1EsqueletoTO.getRespbleareaInt());
		parametros.add(planillaSacop1EsqueletoTO.getEstadoInt());
		parametros.add(planillaSacop1EsqueletoTO.getOrigensacopInt());
		if(Constants.MANEJADOR_POSTGRES == Constants.MANEJADOR_ACTUAL) {
		  parametros.add(planillaSacop1EsqueletoTO.getFechaemisionTimestamp());
		} else {
			parametros.add(planillaSacop1EsqueletoTO.getFechaemision());
			
		}
		parametros.add(planillaSacop1EsqueletoTO.getRequisitosaplicable());
		parametros.add(planillaSacop1EsqueletoTO.getProcesosafectados());
		parametros.add(planillaSacop1EsqueletoTO.getSolicitudinforma());
		parametros.add(planillaSacop1EsqueletoTO.getDescripcion());
		parametros.add(planillaSacop1EsqueletoTO.getCausasnoconformidad());
		parametros.add(planillaSacop1EsqueletoTO.getAccionesrecomendadas());
		parametros.add(planillaSacop1EsqueletoTO.getCorrecpreven());
		parametros.add(planillaSacop1EsqueletoTO.getRechazoapruebo());
		parametros.add(planillaSacop1EsqueletoTO.getNoaceptada());
		parametros.add(planillaSacop1EsqueletoTO.getAccionobservacion());
		parametros.add(planillaSacop1EsqueletoTO.getFechaestimada());
		parametros.add(planillaSacop1EsqueletoTO.getFechareal());
		parametros.add(planillaSacop1EsqueletoTO.getAccionesEstablecidas());
		parametros.add(planillaSacop1EsqueletoTO.getAccionesEstablecidastxt());
		parametros.add(planillaSacop1EsqueletoTO.getEliminarcausaraiz());
		parametros.add(planillaSacop1EsqueletoTO.getEliminarcausaraiztxt());
		parametros.add(planillaSacop1EsqueletoTO.getNameFile());
		parametros.add(planillaSacop1EsqueletoTO.getContentType());
		parametros.add(planillaSacop1EsqueletoTO.getActivecomntresponsablecerrar());
		parametros.add(planillaSacop1EsqueletoTO.getActiveInt());
		parametros.add(planillaSacop1EsqueletoTO.getFechaculminar());
		parametros.add(planillaSacop1EsqueletoTO.getComntresponsablecerrar());
		parametros.add(planillaSacop1EsqueletoTO.getSacopRelacionadas());
		parametros.add(planillaSacop1EsqueletoTO.getNumgenVersion());
		parametros.add(planillaSacop1EsqueletoTO.getNoconformidades());
		parametros.add(planillaSacop1EsqueletoTO.getNoconformidadesref());
		parametros.add(planillaSacop1EsqueletoTO.getEstadoEsqueletoConfiguradoSacop());
	    parametros.add(planillaSacop1EsqueletoTO.getUsuarioSacops1Int());
	    parametros.add(planillaSacop1EsqueletoTO.getFechaSacops1Timestamp());
	    parametros.add(planillaSacop1EsqueletoTO.getIdDocumentRelatedInt());
	    parametros.add(planillaSacop1EsqueletoTO.getActionType());
	    parametros.add(planillaSacop1EsqueletoTO.getIdDocumentAssociateInt());
	    parametros.add(planillaSacop1EsqueletoTO.getNumVerDocumentAssociateInt());
	    parametros.add(planillaSacop1EsqueletoTO.getNameDocumentAssociate());
	    parametros.add(planillaSacop1EsqueletoTO.getRequireTrackingInt());
	    parametros.add(planillaSacop1EsqueletoTO.getRequireTrackingDateTimestamp());
		parametros.add(ToolsHTML.parseInt(planillaSacop1EsqueletoTO.getTrackingSacop(),0));
		parametros.add(ToolsHTML.isEmptyOrNull(planillaSacop1EsqueletoTO.getNumberTrackingSacop(),""));
		
		return JDBCUtil.executeUpdate(query, parametros);
	}

	/**
	 * 
	 */
	public int actualizar(ObjetoTO oPlanillaSacop1EsqueletoTO) throws Exception {
		planillaSacop1EsqueletoTO = (PlanillaSacop1EsqueletoTO) oPlanillaSacop1EsqueletoTO;

		query.setLength(0);
		query.append("UPDATE tbl_planillasacop1esqueleto SET ");
		query.append("sacopnum=?,emisor=?,usernotificado=?,respblearea=?, ");
		query.append("estado=?,origensacop=?,fechaemision=?,requisitosaplicable=?,procesosafectados=?, ");
		query.append("solicitudinforma=?,descripcion=?,causasnoconformidad=?,accionesrecomendadas=?,correcpreven=?, ");
		query.append("rechazoapruebo=?,noaceptada=?,accionobservacion=?,fechaestimada=?,fechareal=?,accionesEstablecidas=?, ");
		query.append("accionesEstablecidastxt=?,eliminarcausaraiz=?,eliminarcausaraiztxt=?,nameFile=?,contentType=?, ");
		query.append("activecomntresponsablecerrar=?,active=?,fechaculminar=?,comntresponsablecerrar=?, ");
		query.append("sacop_relacionadas=?,numgen_version=?,noconformidades=?,noconformidadesref=?, ");
		query.append("estadoEsqueletoConfiguradoSacop=?,usuarioSacops1=?,fechaSacops1=?,idDocumentRelated=?, ");
		query.append("actionType=?,idDocumentAssociate=?,numVerDocumentAssociate=?, ");
		query.append("nameDocumentAssociate=?,requireTracking=?,requireTrackingDate=?, trackingSacop=?, numberTrackingSacop=? ");
		
		
		query.append("WHERE idplanillasacop1=?");

		parametros = new ArrayList<Object>();
		parametros.add(planillaSacop1EsqueletoTO.getSacopnum());
		parametros.add(planillaSacop1EsqueletoTO.getEmisorInt());
		parametros.add(planillaSacop1EsqueletoTO.getUsernotificadoInt());
		parametros.add(planillaSacop1EsqueletoTO.getRespbleareaInt());
		parametros.add(planillaSacop1EsqueletoTO.getEstadoInt());
		parametros.add(planillaSacop1EsqueletoTO.getOrigensacopInt());
		parametros.add(planillaSacop1EsqueletoTO.getFechaemisionTimestamp());
		//parametros.add(planillaSacop1EsqueletoTO.getFechaemision());
		parametros.add(planillaSacop1EsqueletoTO.getRequisitosaplicable());
		parametros.add(planillaSacop1EsqueletoTO.getProcesosafectados());
		parametros.add(planillaSacop1EsqueletoTO.getSolicitudinforma());
		parametros.add(planillaSacop1EsqueletoTO.getDescripcion());
		parametros.add(planillaSacop1EsqueletoTO.getCausasnoconformidad());
		parametros.add(planillaSacop1EsqueletoTO.getAccionesrecomendadas());
		parametros.add(planillaSacop1EsqueletoTO.getCorrecpreven());
		parametros.add(planillaSacop1EsqueletoTO.getRechazoapruebo());
		parametros.add(planillaSacop1EsqueletoTO.getNoaceptada());
		parametros.add(planillaSacop1EsqueletoTO.getAccionobservacion());
		parametros.add(planillaSacop1EsqueletoTO.getFechaestimada());
		parametros.add(planillaSacop1EsqueletoTO.getFechareal());
		parametros.add(planillaSacop1EsqueletoTO.getAccionesEstablecidas());
		parametros.add(planillaSacop1EsqueletoTO.getAccionesEstablecidastxt());
		parametros.add(planillaSacop1EsqueletoTO.getEliminarcausaraiz());
		parametros.add(planillaSacop1EsqueletoTO.getEliminarcausaraiztxt());
		parametros.add(planillaSacop1EsqueletoTO.getNameFile());
		parametros.add(planillaSacop1EsqueletoTO.getContentType());
		parametros.add(planillaSacop1EsqueletoTO.getActivecomntresponsablecerrar());
		parametros.add(planillaSacop1EsqueletoTO.getActiveInt());
		parametros.add(planillaSacop1EsqueletoTO.getFechaculminar());
		parametros.add(planillaSacop1EsqueletoTO.getComntresponsablecerrar());
		parametros.add(planillaSacop1EsqueletoTO.getSacopRelacionadas());
		parametros.add(planillaSacop1EsqueletoTO.getNumgenVersion());
		parametros.add(planillaSacop1EsqueletoTO.getNoconformidades());
		parametros.add(planillaSacop1EsqueletoTO.getNoconformidadesref());
		parametros.add(planillaSacop1EsqueletoTO.getEstadoEsqueletoConfiguradoSacop());
	    parametros.add(planillaSacop1EsqueletoTO.getUsuarioSacops1Int());
	    parametros.add(planillaSacop1EsqueletoTO.getFechaSacops1Timestamp());
	    parametros.add(planillaSacop1EsqueletoTO.getIdDocumentRelatedInt());
	    parametros.add(planillaSacop1EsqueletoTO.getActionType());
	    parametros.add(planillaSacop1EsqueletoTO.getIdDocumentAssociateInt());
	    parametros.add(planillaSacop1EsqueletoTO.getNumVerDocumentAssociateInt());
	    parametros.add(planillaSacop1EsqueletoTO.getNameDocumentAssociate());
	    parametros.add(planillaSacop1EsqueletoTO.getRequireTrackingInt());
	    parametros.add(planillaSacop1EsqueletoTO.getRequireTrackingDateTimestamp());
		parametros.add(planillaSacop1EsqueletoTO.getIdplanillasacop1Int()); // primary key


		return JDBCUtil.executeUpdate(query, parametros);
	}

	/**
	 * 
	 */
	public void eliminar(ObjetoTO oPlanillaSacop1EsqueletoTO) throws Exception {
		planillaSacop1EsqueletoTO = (PlanillaSacop1EsqueletoTO) oPlanillaSacop1EsqueletoTO;

		query.setLength(0);
		query.append("DELETE FROM tbl_planillasacop1esqueleto WHERE idplanillasacop1=?");

		parametros = new ArrayList<Object>();
		parametros.add(planillaSacop1EsqueletoTO.getIdplanillasacop1Int());

		JDBCUtil.executeUpdate(query, parametros);
	}

	/**
	 * 
	 */
	public boolean cargar(ObjetoTO oPlanillaSacop1EsqueletoTO) throws Exception {
		planillaSacop1EsqueletoTO = (PlanillaSacop1EsqueletoTO) oPlanillaSacop1EsqueletoTO;

		query.setLength(0);
		query.append("SELECT * FROM tbl_planillasacop1esqueleto WHERE idplanillasacop1=").append(planillaSacop1EsqueletoTO.getIdplanillasacop1Int());

		CachedRowSet crs = JDBCUtil.executeQuery(query, Thread.currentThread().getStackTrace()[1].getMethodName());

		if (crs.next()) {
			load(crs, planillaSacop1EsqueletoTO);
			return true;
		}
		return false;
	}

	/**
	 * 
	 */
	public ArrayList<PlanillaSacop1EsqueletoTO> listar() throws Exception {
		ArrayList<PlanillaSacop1EsqueletoTO> lista = new ArrayList<PlanillaSacop1EsqueletoTO>();

		query.setLength(0);
		query.append("SELECT * FROM tbl_planillasacop1esqueleto ORDER BY idplanillasacop1");

		CachedRowSet crs = JDBCUtil.executeQuery(query, Thread.currentThread().getStackTrace()[1].getMethodName());

		while (crs.next()) {
			planillaSacop1EsqueletoTO = new PlanillaSacop1EsqueletoTO();
			load(crs, planillaSacop1EsqueletoTO);
			lista.add(planillaSacop1EsqueletoTO);
		}
		return lista;
	}
	
	/**
	 * 
	 */
	public ArrayList<PlanillaSacop1EsqueletoTO> listar(String active, String estado, String id) throws Exception {
		ArrayList<PlanillaSacop1EsqueletoTO> lista = new ArrayList<PlanillaSacop1EsqueletoTO>();

		query.setLength(0);
		query.append("SELECT * FROM tbl_planillasacop1esqueleto ");
		query.append("WHERE active=").append(active).append(" ");
		query.append("AND estado<>").append(estado).append(" ");
		query.append("AND idplanillasacop1esqueleto=").append(id).append(" ");
		query.append("ORDER BY sacopnum");

		CachedRowSet crs = JDBCUtil.executeQuery(query, Thread.currentThread().getStackTrace()[1].getMethodName());

		while (crs.next()) {
			planillaSacop1EsqueletoTO = new PlanillaSacop1EsqueletoTO();
			load(crs, planillaSacop1EsqueletoTO);
			lista.add(planillaSacop1EsqueletoTO);
		}
		return lista;
	}
	
	/**
	 * 
	 */
	public ArrayList<PlanillaSacop1EsqueletoTO> listarById(String id) throws Exception {
		ArrayList<PlanillaSacop1EsqueletoTO> lista = new ArrayList<PlanillaSacop1EsqueletoTO>();

		query.setLength(0);
		query.append("SELECT * FROM tbl_planillasacop1esqueleto ");
		query.append("WHERE idplanillasacop1esqueleto=").append(id).append(" ");
		query.append("ORDER BY sacopnum");

		CachedRowSet crs = JDBCUtil.executeQuery(query, Thread.currentThread().getStackTrace()[1].getMethodName());

		while (crs.next()) {
			planillaSacop1EsqueletoTO = new PlanillaSacop1EsqueletoTO();
			load(crs, planillaSacop1EsqueletoTO);
			lista.add(planillaSacop1EsqueletoTO);
		}
		return lista;
	}

	/**
	 * 
	 */
	public ArrayList<PlanillaSacop1EsqueletoTO> listar(String active, String[] estado, String id) throws Exception {
		ArrayList<PlanillaSacop1EsqueletoTO> lista = new ArrayList<PlanillaSacop1EsqueletoTO>();
		String sep = "";

		query.setLength(0);
		query.append("SELECT * FROM tbl_planillasacop1esqueleto ");
		query.append("WHERE active=").append(active).append(" ");
		query.append("AND (");
		for(int i=0; i<estado.length; i++) {
			query.append(sep).append(" estado=").append(estado[i]).append(" ");
			sep = "OR";
		}
		query.append(") ");
		query.append("AND idplanillasacop1=").append(id).append(" ");
		query.append("ORDER BY sacopnum");

		CachedRowSet crs = JDBCUtil.executeQuery(query, Thread.currentThread().getStackTrace()[1].getMethodName());

		while (crs.next()) {
			planillaSacop1EsqueletoTO = new PlanillaSacop1EsqueletoTO();
			load(crs, planillaSacop1EsqueletoTO);
			lista.add(planillaSacop1EsqueletoTO);
		}
		return lista;
	}

	private void load(CachedRowSet crs, PlanillaSacop1EsqueletoTO planillaSacop1EsqueletoTO) throws SQLException {
		planillaSacop1EsqueletoTO.setIdplanillasacop1(crs.getString("idplanillasacop1"));
		planillaSacop1EsqueletoTO.setSacopnum(crs.getString("sacopnum"));
		planillaSacop1EsqueletoTO.setEmisor(crs.getString("emisor"));
		planillaSacop1EsqueletoTO.setUsernotificado(crs.getString("usernotificado"));
		planillaSacop1EsqueletoTO.setRespblearea(crs.getString("respblearea"));
		planillaSacop1EsqueletoTO.setEstado(crs.getString("estado"));
		planillaSacop1EsqueletoTO.setOrigensacop(crs.getString("origensacop"));
		planillaSacop1EsqueletoTO.setFechaemision(crs.getString("fechaemision"));
		planillaSacop1EsqueletoTO.setRequisitosaplicable(crs.getString("requisitosaplicable"));
		planillaSacop1EsqueletoTO.setProcesosafectados(crs.getString("procesosafectados"));
		planillaSacop1EsqueletoTO.setSolicitudinforma(crs.getString("solicitudinforma"));
		planillaSacop1EsqueletoTO.setDescripcion(crs.getString("descripcion"));
		planillaSacop1EsqueletoTO.setCausasnoconformidad(crs.getString("causasnoconformidad"));
		planillaSacop1EsqueletoTO.setAccionesrecomendadas(crs.getString("accionesrecomendadas"));
		planillaSacop1EsqueletoTO.setCorrecpreven(crs.getString("correcpreven"));
		planillaSacop1EsqueletoTO.setRechazoapruebo(crs.getString("rechazoapruebo"));
		planillaSacop1EsqueletoTO.setNoaceptada(crs.getString("noaceptada"));
		planillaSacop1EsqueletoTO.setAccionobservacion(crs.getString("accionobservacion"));
		planillaSacop1EsqueletoTO.setFechaestimada(crs.getString("fechaestimada"));
		planillaSacop1EsqueletoTO.setFechareal(crs.getString("fechareal"));
		planillaSacop1EsqueletoTO.setAccionesEstablecidas(crs.getString("accionesEstablecidas"));
		planillaSacop1EsqueletoTO.setAccionesEstablecidastxt(crs.getString("accionesEstablecidastxt"));
		planillaSacop1EsqueletoTO.setEliminarcausaraiz(crs.getString("eliminarcausaraiz"));
		planillaSacop1EsqueletoTO.setEliminarcausaraiztxt(crs.getString("eliminarcausaraiztxt"));
		planillaSacop1EsqueletoTO.setNameFile(crs.getString("nameFile"));
		planillaSacop1EsqueletoTO.setContentType(crs.getString("contentType"));
		planillaSacop1EsqueletoTO.setActivecomntresponsablecerrar(crs.getString("activecomntresponsablecerrar"));
		planillaSacop1EsqueletoTO.setActive(crs.getString("active"));
		planillaSacop1EsqueletoTO.setFechaculminar(crs.getString("fechaculminar"));
		planillaSacop1EsqueletoTO.setComntresponsablecerrar(crs.getString("comntresponsablecerrar"));
		planillaSacop1EsqueletoTO.setSacopRelacionadas(crs.getString("sacop_relacionadas"));
		planillaSacop1EsqueletoTO.setNumgenVersion(crs.getString("numgen_version"));
		planillaSacop1EsqueletoTO.setNoconformidades(crs.getString("noconformidades"));
		planillaSacop1EsqueletoTO.setNoconformidadesref(crs.getString("noconformidadesref"));
		planillaSacop1EsqueletoTO.setEstadoEsqueletoConfiguradoSacop(crs.getString("estadoEsqueletoConfiguradoSacop"));
	    planillaSacop1EsqueletoTO.setUsuarioSacops1(crs.getString("usuarioSacops1"));
	    planillaSacop1EsqueletoTO.setFechaSacops1(crs.getString("fechaSacops1"));
	    planillaSacop1EsqueletoTO.setIdDocumentRelated(crs.getString("idDocumentRelated"));
	    planillaSacop1EsqueletoTO.setActionType(crs.getString("actionType"));
	    planillaSacop1EsqueletoTO.setIdDocumentAssociate(crs.getString("idDocumentAssociate"));
	    planillaSacop1EsqueletoTO.setNumVerDocumentAssociate(crs.getString("numVerDocumentAssociate"));
	    planillaSacop1EsqueletoTO.setNameDocumentAssociate(crs.getString("nameDocumentAssociate"));
	    planillaSacop1EsqueletoTO.setRequireTracking(crs.getString("requireTracking"));
	    planillaSacop1EsqueletoTO.setRequireTrackingDate(crs.getString("requireTrackingDate"));
	    planillaSacop1EsqueletoTO.setTrackingSacop(crs.getString("trackingSacop"));
	    planillaSacop1EsqueletoTO.setNumberTrackingSacop(crs.getString("numberTrackingSacop"));
		
	}

}
