package com.desige.webDocuments.persistent.managers;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;

import com.desige.webDocuments.accion.forms.ActionRecommended;
import com.desige.webDocuments.area.forms.Area;
import com.desige.webDocuments.cargo.forms.Cargo;
import com.desige.webDocuments.causa.forms.PossibleCause;
import com.desige.webDocuments.document.actions.loadsolicitudImpresion;
import com.desige.webDocuments.persistent.utils.CaseInsensitiveProperties;
import com.desige.webDocuments.persistent.utils.JDBCUtil;
import com.desige.webDocuments.sacop.actions.LoadSacop;
import com.desige.webDocuments.sacop.actions.Sacop_Relacionadas;
import com.desige.webDocuments.sacop.forms.ClasificacionPlanillasSacop;
import com.desige.webDocuments.sacop.forms.DeleteSacop;
import com.desige.webDocuments.sacop.forms.InformarPorMail;
import com.desige.webDocuments.sacop.forms.Plantilla1BD;
import com.desige.webDocuments.sacop.forms.PlantillaAccion;
import com.desige.webDocuments.sacop.forms.ProcesosSacop;
import com.desige.webDocuments.sacop.forms.TipoSacop;
import com.desige.webDocuments.sacop.forms.TitulosPlanillasSacop;
import com.desige.webDocuments.sacop.forms.plantilla1;
import com.desige.webDocuments.seguridad.forms.PermissionUserForm;
import com.desige.webDocuments.utils.ApplicationExceptionChecked;
import com.desige.webDocuments.utils.Constants;
import com.desige.webDocuments.utils.DesigeConf;
import com.desige.webDocuments.utils.ToolsHTML;
import com.desige.webDocuments.utils.beans.Search;
import com.desige.webDocuments.utils.beans.Users;
import com.focus.qwds.ondemand.server.excepciones.ErrorDeAplicaqcion;
import com.focus.qweb.dao.ActionRecommendedDAO;
import com.focus.qweb.dao.AreaDAO;
import com.focus.qweb.dao.CargoDAO;
import com.focus.qweb.dao.ClasificacionPlanillasSacopDAO;
import com.focus.qweb.dao.PersonDAO;
import com.focus.qweb.dao.PlanillaSacop1DAO;
import com.focus.qweb.dao.PlanillaSacopAccionDAO;
import com.focus.qweb.dao.PossibleCauseDAO;
import com.focus.qweb.dao.TitulosPlanillasSacopDAO;
import com.focus.qweb.to.ActionRecommendedTO;
import com.focus.qweb.to.AreaTO;
import com.focus.qweb.to.CargoTO;
import com.focus.qweb.to.ClasificacionPlanillasSacopTO;
import com.focus.qweb.to.PersonTO;
import com.focus.qweb.to.PlanillaSacop1TO;
import com.focus.qweb.to.PlanillaSacopAccionTO;
import com.focus.qweb.to.PossibleCauseTO;
import com.focus.qweb.to.TitulosPlanillasSacopTO;
import com.focus.util.PerfilAdministrador;
import com.focus.wonderware.actions.HandlerProcesosWonderWare;
import com.focus.wonderware.intocuh_sacop.forms.Sacop_Intouch_Conftagname;
import com.focus.wonderware.intocuh_sacop.forms.Tagname;
import com.focus.wonderware.intocuh_sacop.forms.sacop_intouch_padre_frm;
import com.gestionEnvio.custon.dostorres.dao.ContactoDAO;
import com.gestionEnvio.custon.dostorres.to.ContactoTO;

import sun.jdbc.rowset.CachedRowSet;

//
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;

/**
 * Created by IntelliJ IDEA. User: srodriguez Date: 07/12/2005 Time: 09:04:25 AM To change this template use File | Settings | File Templates.
 */
public class HandlerProcesosSacop extends HandlerBD {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8342847543487880199L;
	public static final String EVIDENCIAS_NAME_DIR = "evidenciasSacops";

	/**
	 * Retorna directorio dentro de SACOP donde se encuentra el archivo
	 * 
	 * @param idPlanillaSacop
	 * @return
	 */
	public static String getSacopPlanillaFolder(String idPlanillaSacop) {
		return new StringBuilder(2024).append(getSacopPath()).append(File.separator).append(idPlanillaSacop).toString();
	}

	/**
	 * Retorna carpeta donde se guardan los anexos de la planilla SACOP
	 * 
	 * @param idPlanillaSacop
	 * @return
	 */
	public static String getSacopPlanillaBagFolder(String idPlanillaSacop) {
		return new StringBuilder(2024).append(getSacopPath()).append(File.separator).append(idPlanillaSacop).append(File.separator).append("bag").toString();

	}

	/**
	 * Retorna Directorio padre donde se colocaron todos los archivos que viene de SACOP
	 * 
	 * @return
	 */
	public static String getSacopPath() {
		String result = "";
		try {
			result = new StringBuilder(2048).append(ToolsHTML.getRepository()).append(File.separator).append(EVIDENCIAS_NAME_DIR).toString();
		} catch (ErrorDeAplicaqcion e) {
			e.printStackTrace();
		}

		return result;
	}

	/**
	 * 
	 * @param forma
	 * @throws Exception
	 */
	public static void loadUserAccioneSacop(PlantillaAccion forma) throws Exception {
		StringBuilder sql = new StringBuilder("SELECT * FROM tbl_sacopaccionporpersona").append(" WHERE idplanillasacopaccion = ")
				.append(forma.getIdplanillasacopaccion()).append(" and active= '").append(Constants.permission).append("'");

		// System.out.println(forma.getIdplanillasacopaccion() +
		// "=forma.getIdplanillasacopaccion().........Observar...fin...............................................");
		StringBuilder responsables = new StringBuilder("");
		Vector datos = JDBCUtil.doQueryVector(sql.toString(),Thread.currentThread().getStackTrace()[1].getMethodName());
		// //System.out.println("[getProcesosSacop]" + sql.toString());
		for (int row = 0; row < datos.size(); row++) {
			Properties properties = (Properties) datos.elementAt(row);
			/*
			 * Search bean = new Search(properties.getProperty("numproceso"),properties .getProperty("proceso"));//,
			 */
			responsables.append(properties.getProperty("idperson"));
			if ((row + 1) < datos.size()) {
				responsables.append(",");
			}

		}
		forma.setResponsables(responsables.toString());
	}

	/**
	 * 
	 * @param forma
	 * @throws Exception
	 */
	public static void loadDeleteSacop(DeleteSacop forma) throws Exception {

		StringBuilder sql = new StringBuilder("SELECT * FROM  tbl_deletesacop").append(" WHERE idplanillasacop1 = ").append(forma.getIdplanillasacop1())
				.append(" and active= '").append(Constants.permission).append("'");
		// System.out.println("SQL=" + sql.toString());
		Properties prop = JDBCUtil.doQueryOneRow(sql.toString(),Thread.currentThread().getStackTrace()[1].getMethodName());

		if ((prop != null) && prop.getProperty("isEmpty").equalsIgnoreCase("false")) {
			forma.setId(prop.getProperty("id") != null ? Long.parseLong(prop.getProperty("id")) : 0);
			forma.setIdperson(prop.getProperty("idperson") != null ? prop.getProperty("idperson") : "");
			forma.setIdplanillasacop1(prop.getProperty("idplanillasacop1") != null ? prop.getProperty("idplanillasacop1") : "");
			forma.setTrackingSacop(prop.getProperty("trackingSacop") != null ? prop.getProperty("trackingSacop") : "");
		}
	}
	
	public static boolean isTrackinSacop(DeleteSacop forma) throws Exception {

		StringBuilder sql = new StringBuilder("SELECT trackingSacop FROM  tbl_planillasacop1 WHERE idplanillasacop1 = ").append(forma.getIdplanillasacop1())
				.append(" and active= '").append(Constants.permission).append("'");
		
		Properties prop = JDBCUtil.doQueryOneRow(sql.toString(),Thread.currentThread().getStackTrace()[1].getMethodName());

		if ((prop != null) && prop.getProperty("isEmpty").equalsIgnoreCase("false")) {
			if(prop.getProperty("trackingSacop") != null) {
				return prop.getProperty("trackingSacop").equals("1");
			}
		}
		return false;
	}

	public static boolean isHasTrackinSacop(DeleteSacop forma) throws Exception {

		StringBuilder sql = new StringBuilder("SELECT sacopnum FROM  tbl_planillasacop1 WHERE idplanillasacop1 = ").append(forma.getIdplanillasacop1())
				.append(" and active= '").append(Constants.permission).append("'");
		// System.out.println("SQL=" + sql.toString());
		Properties prop = JDBCUtil.doQueryOneRow(sql.toString(),Thread.currentThread().getStackTrace()[1].getMethodName());

		if ((prop != null) && prop.getProperty("isEmpty").equalsIgnoreCase("false")) {
			if(prop.getProperty("sacopnum") != null) {
				StringBuilder sb = new StringBuilder();
				sb.append("SELECT * FROM tbl_planillasacop1 ");
				sb.append("WHERE numberTrackingSacop='").append(prop.getProperty("sacopnum")).append("' ");
				sb.append("AND active='").append(Constants.permission).append("' ");
				sb.append("AND estado!='").append(LoadSacop.edoCerrado).append("' ");
				CachedRowSet crs = JDBCUtil.executeQuery(sb,"isHasTrackinSacop()");

				return crs.next();
			}
		}
		return false;
	}
	
	public static CachedRowSet listTrackinSacop(DeleteSacop forma) throws Exception {

		StringBuilder sql = new StringBuilder();
		sql.append("SELECT idplanillasacop1 ");
		sql.append("FROM  tbl_planillasacop1 ");
		sql.append("WHERE numberTrackingSacop = (select sacopnum from tbl_planillasacop1 where idplanillasacop1=").append(forma.getIdplanillasacop1()).append(") ");
		sql.append("AND active= '").append(Constants.permission).append("'");
		
		return JDBCUtil.executeQuery(sql,"listTrackinSacop()");
	}
	

	/**
	 * 
	 * @param forma
	 * @throws Exception
	 */
	public static void load(Area forma) throws Exception {
		StringBuilder sql = new StringBuilder("SELECT idarea, area, prefijo FROM tbl_area").append(" WHERE idarea = ").append(forma.getIdarea())
				.append(" and activea= '").append(Constants.permission).append("'");
		// System.out.println("SQL=" + sql.toString());
		Properties prop = JDBCUtil.doQueryOneRow(sql.toString(),Thread.currentThread().getStackTrace()[1].getMethodName());

		if ((prop != null) && prop.getProperty("isEmpty").equalsIgnoreCase("false")) {
			forma.setArea(prop.getProperty("area"));
			forma.setPrefijo(prop.getProperty("prefijo"));
			forma.setIdarea(Long.parseLong(prop.getProperty("idarea")));
		}
	}

	/**
	 * 
	 * @param forma
	 * @throws Exception
	 */
	public static void load(PossibleCause forma) throws Exception {
		StringBuilder sql = new StringBuilder("SELECT idPossibleCause, descPossibleCause FROM possiblecause ");
		sql.append(" WHERE idPossibleCause = ").append(forma.getIdPossibleCause());
		// System.out.println("SQL=" + sql.toString());
		Properties prop = JDBCUtil.doQueryOneRow(sql.toString(),Thread.currentThread().getStackTrace()[1].getMethodName());

		if ((prop != null) && prop.getProperty("isEmpty").equalsIgnoreCase("false")) {
			forma.setIdPossibleCause(Long.parseLong(prop.getProperty("idPossibleCause")));
			forma.setDescPossibleCause(prop.getProperty("descPossibleCause"));
		}
	}

	/**
	 * 
	 * @param forma
	 * @throws Exception
	 */
	public static void load(ActionRecommended forma) throws Exception {
		StringBuilder sql = new StringBuilder("SELECT * FROM actionrecommended ");
		sql.append(" WHERE idActionRecommended = ").append(forma.getIdActionRecommended());
		// System.out.println("SQL=" + sql.toString());
		Properties prop = JDBCUtil.doQueryOneRow(sql.toString(),Thread.currentThread().getStackTrace()[1].getMethodName());

		if ((prop != null) && prop.getProperty("isEmpty").equalsIgnoreCase("false")) {
			forma.setIdActionRecommended(Long.parseLong(prop.getProperty("idActionRecommended")));
			forma.setDescActionRecommended(prop.getProperty("descActionRecommended"));
			forma.setIdRegisterClass(Integer.parseInt(prop.getProperty("idRegisterClass")));
		}
	}

	/**
	 * 
	 * @return
	 * @throws Exception
	 */
	public static Collection<Area> loadAllAreas() throws Exception {
		List<Area> areas = new LinkedList<Area>();
		Vector<Properties> dbResult = new Vector<Properties>();
		StringBuilder sql = new StringBuilder("SELECT idarea, area, activea, prefijo ").append("FROM tbl_area ").append("ORDER BY UPPER(area) ");

		// System.out.println("SQL=" + sql.toString());

		dbResult = JDBCUtil.doQueryVector(sql.toString(),Thread.currentThread().getStackTrace()[1].getMethodName());
		for (int row = 0; row < dbResult.size(); row++) {
			Properties props = dbResult.elementAt(row);
			Area tmp = new Area();
			tmp.setActive(Byte.valueOf(props.getProperty("activea")));
			tmp.setArea(props.getProperty("area"));
			tmp.setIdarea(Long.parseLong(props.getProperty("idarea")));
			tmp.setPrefijo(props.getProperty("prefijo"));

			areas.add(tmp);
		}

		return areas;
	}
	
	public static Collection<TipoSacop> loadAllTipos() throws Exception {
		List<TipoSacop> tipos = new LinkedList<TipoSacop>();
		Vector<Properties> dbResult = new Vector<Properties>();
		StringBuilder sql = new StringBuilder("SELECT idtipo, tipo, activea, prefijo ").append("FROM tbl_tipo ").append("ORDER BY UPPER(tipo) ");

		
		dbResult = JDBCUtil.doQueryVector(sql.toString(),Thread.currentThread().getStackTrace()[1].getMethodName());
		for (int row = 0; row < dbResult.size(); row++) {
			Properties props = dbResult.elementAt(row);
			TipoSacop tmp = new TipoSacop();
			tmp.setActive(Byte.valueOf(props.getProperty("activea")));
			tmp.setTipo(props.getProperty("tipo"));
			tmp.setIdtipo(Long.parseLong(props.getProperty("idtipo")));
			tmp.setPrefijo(props.getProperty("prefijo"));

			tipos.add(tmp);
		}

		return tipos;
	}
	
	public static Collection<TitulosPlanillasSacop> loadAllOrigens() throws Exception {
		List<TitulosPlanillasSacop> origens = new LinkedList<TitulosPlanillasSacop>();
		Vector<Properties> dbResult = new Vector<Properties>();
		StringBuilder sql = new StringBuilder("SELECT numtitulosplanillas, titulosplanillas , active ").append("FROM tbl_titulosplanillassacop ").append("ORDER BY UPPER(titulosplanillas) ");

		
		dbResult = JDBCUtil.doQueryVector(sql.toString(),Thread.currentThread().getStackTrace()[1].getMethodName());
		for (int row = 0; row < dbResult.size(); row++) {
			Properties props = dbResult.elementAt(row);
			TitulosPlanillasSacop tmp = new TitulosPlanillasSacop();
			tmp.setActive(Byte.valueOf(props.getProperty("active")));
			//tmp.setTitulosplanillas(titulosplanillas);
			tmp.setTitulosplanillas(props.getProperty("titulosplanillas"));
			tmp.setNumtitulosplanillas(Long.parseLong(props.getProperty("numtitulosplanillas")));
			//tmp.setPrefijo(props.getProperty("prefijo"));

			origens.add(tmp);
		}

		return origens;
	}


	/**
	 * 
	 * @param forma
	 * @throws Exception
	 */
	public static void load(Tagname forma) throws Exception {
		StringBuilder sql = new StringBuilder("SELECT idtagname2, tagname  FROM tbl_tagname ").append(" WHERE idtagname2 = ").append(forma.getIdtagname2())
				.append(" and active= '").append(Constants.permission).append("'");
		// System.out.println("SQL=" + sql.toString());
		Properties prop = JDBCUtil.doQueryOneRow(sql.toString(),Thread.currentThread().getStackTrace()[1].getMethodName());

		if ((prop != null) && prop.getProperty("isEmpty").equalsIgnoreCase("false")) {
			forma.setTagname(prop.getProperty("tagname"));
			forma.setIdtagname2(Long.parseLong(prop.getProperty("idtagname2")));
		}
	}

	/**
	 * 
	 * @param forma
	 * @throws Exception
	 */
	public static void load(Sacop_Intouch_Conftagname forma) throws Exception {
		StringBuilder sql = new StringBuilder("SELECT tipotag,idtagname  FROM tbl_conftagname ").append(" WHERE idtagname = ").append(forma.getIdtagname())
				.append(" and active= '").append(Constants.permission).append("'");
		// System.out.println("SQL=" + sql.toString());
		Properties prop = JDBCUtil.doQueryOneRow(sql.toString(),Thread.currentThread().getStackTrace()[1].getMethodName());

		if ((prop != null) && prop.getProperty("isEmpty").equalsIgnoreCase("false")) {
			forma.setTipotag(prop.getProperty("tipotag"));
			forma.setIdtagname(Long.parseLong(prop.getProperty("idtagname")));
		}
	}

	/**
	 * 
	 * @param forma
	 * @throws Exception
	 */
	public static void load(Cargo forma) throws Exception {
		CargoDAO oCargoDAO = new CargoDAO();
		CargoTO oCargoTO = new CargoTO();

		oCargoTO.setIdCargo(String.valueOf(forma.getIdcargo()));
		oCargoDAO.cargar(oCargoTO);

		forma.load(oCargoTO);
	}

	/**
	 * 
	 * @param forma
	 * @throws Exception
	 */
	public static void load(PlantillaAccion forma) throws Exception {
		StringBuilder sql = new StringBuilder("SELECT accion,fecha,responsables FROM tbl_planillasacopaccion").append(" WHERE idplanillasacopaccion = ")
				.append(forma.getIdplanillasacopaccion()).append(" and active= '").append(Constants.permission).append("'");
		Properties prop = JDBCUtil.doQueryOneRow(sql.toString(),Thread.currentThread().getStackTrace()[1].getMethodName());
		if ((prop != null) && prop.getProperty("isEmpty").equalsIgnoreCase("false")) {
			forma.setAccion(prop.getProperty("accion"));
			forma.setFecha(prop.getProperty("fecha"));
			forma.setResponsables(prop.getProperty("responsables"));
		}
	}

	/**
	 * 
	 * @param forma
	 * @throws Exception
	 */
	public static void loadUserAccionSacop(PlantillaAccion forma) throws Exception {
		StringBuilder sql = new StringBuilder("SELECT accion,fecha,responsables  FROM tbl_sacopaccionporpersona").append(" WHERE idplanillasacopaccion = ")
				.append(forma.getIdplanillasacopaccion()).append(" and active= '").append(Constants.permission).append("'");
		Properties prop = JDBCUtil.doQueryOneRow(sql.toString(),Thread.currentThread().getStackTrace()[1].getMethodName());
		if ((prop != null) && prop.getProperty("isEmpty").equalsIgnoreCase("false")) {
			forma.setAccion(prop.getProperty("accion"));
			forma.setFecha(prop.getProperty("fecha"));
			forma.setResponsables(prop.getProperty("responsables"));
		}
	}

	/**
	 * 
	 * @param forma
	 * @throws Exception
	 */
	public static void load(TitulosPlanillasSacop forma) throws Exception {
		StringBuilder sql = new StringBuilder("SELECT titulosplanillas FROM tbl_titulosplanillassacop").append(" WHERE numtitulosplanillas = ")
				.append(forma.getNumtitulosplanillas());
		Properties prop = JDBCUtil.doQueryOneRow(sql.toString(),Thread.currentThread().getStackTrace()[1].getMethodName());
		if ((prop != null) && prop.getProperty("isEmpty").equalsIgnoreCase("false")) {
			forma.setTitulosplanillas(prop.getProperty("titulosplanillas"));
		}
	}

	/**
	 * 
	 * @param forma
	 * @throws Exception
	 */
	public static void load(ClasificacionPlanillasSacop forma) throws Exception {
		StringBuilder sql = new StringBuilder("SELECT descripcion FROM tbl_clasificacionplanillassacop").append(" WHERE id = ").append(forma.getId());
		Properties prop = JDBCUtil.doQueryOneRow(sql.toString(),Thread.currentThread().getStackTrace()[1].getMethodName());
		if ((prop != null) && prop.getProperty("isEmpty").equalsIgnoreCase("false")) {
			forma.setDescripcion(prop.getProperty("descripcion"));
		}
	}

	public static String findAreaByCargo(String idCargo) throws Exception {
		// StringBuffer sql = new
		// StringBuffer(" select area from tbl_area ta ");
		// sql.append(" where ta.idarea= ").append(idArea);
		// String area="";
		// //System.out.println("[AreaUsuario]"+sql.toString());
		// Properties prop = JDBCUtil.doQueryOneRow(sql.toString(),Thread.currentThread().getStackTrace()[1].getMethodName());
		// if
		// ((prop!=null)&&prop.getProperty("isEmpty").equalsIgnoreCase("false")){
		// area=prop.getProperty("area");
		// }
		return HandlerDBUser.getCargoAndArea(idCargo, false);
	}
	
	public static String findAreaByUsuario(String idPerson) throws Exception {
		StringBuffer sql = new StringBuffer("SELECT area FROM tbl_area a, person p ");
		sql.append("WHERE a.idarea = p.idarea ");
		sql.append("AND p.idPerson=").append(idPerson);
		
		CachedRowSet crs = JDBCUtil.executeQuery(sql,Thread.currentThread().getStackTrace()[1].getMethodName());
		if(crs.next()) {
			return crs.getString(1);
		}
		return null;
	}
	

	/**
	 * 
	 * @param forma
	 * @throws Exception
	 */
	public static void load(ProcesosSacop forma) throws Exception {
		StringBuilder sql = new StringBuilder("SELECT proceso FROM tbl_procesosacop").append(" WHERE numproceso = ").append(forma.getNumproceso());
		Properties prop = JDBCUtil.doQueryOneRow(sql.toString(),Thread.currentThread().getStackTrace()[1].getMethodName());
		if ((prop != null) && prop.getProperty("isEmpty").equalsIgnoreCase("false")) {
			forma.setProceso(prop.getProperty("proceso"));
		}
	}

	/**
	 * 
	 * @param idarea
	 * @return
	 */
	public static Collection getCargo(String idarea) {
		CargoDAO oCargoDAO = new CargoDAO();

		Vector result = new Vector();
		CargoTO oCargoTO = null;

		ArrayList<CargoTO> lista;
		try {
			lista = oCargoDAO.listarByIdArea(idarea);

			for (Iterator iter = lista.iterator(); iter.hasNext();) {
				oCargoTO = (CargoTO) iter.next();

				Search bean = new Search(oCargoTO.getIdCargo(), oCargoTO.getCargo());
				result.add(bean);
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return result;
	}

	/**
	 * 
	 * @param name
	 * @return
	 */
	public static Collection getArea(String name) {
		Vector result = new Vector();
		List lista = null;

		AreaDAO oAreaDAO = new AreaDAO();

		try {
			lista = oAreaDAO.listarAreaAlls(name);

			for (Iterator iter = lista.iterator(); iter.hasNext();) {
				AreaTO area = (AreaTO) iter.next();
				Search bean = new Search(String.valueOf(area.getIdarea()), area.getArea());
				result.add(bean);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return result;
	}

	/**
	 * 
	 * @param name
	 * @return
	 */
	public static Map<String,PersonTO> listarPersonAlls() {
		Map<String,PersonTO> lista = null;

		PersonDAO oPersonDAO = new PersonDAO();

		try {
			lista = oPersonDAO.listarPersonAlls();

		} catch (Exception e) {
			e.printStackTrace();
		}

		return lista;
	}
	
	/**
	 * 
	 * @param name
	 * @return
	 */
	public static Map<String,AreaTO> listarAreaAlls() {
		Map<String,AreaTO> lista = null;

		AreaDAO oAreaDAO = new AreaDAO();

		try {
			lista = oAreaDAO.listarAreaAlls();

		} catch (Exception e) {
			e.printStackTrace();
		}

		return lista;
	}

	/**
	 * 
	 * @param name
	 * @return
	 */
	public static Map<String,CargoTO> listarCargoAlls() {
		Map<String,CargoTO> lista = null;

		CargoDAO oCargoDAO = new CargoDAO();

		try {
			lista = oCargoDAO.listarCargoAlls();

		} catch (Exception e) {
			e.printStackTrace();
		}

		return lista;
	}

	/**
	 * 
	 * @param name
	 * @return
	 */
	public static Collection getPossibleCause(String name) {
		Vector result = new Vector();
		List lista = null;

		PossibleCauseDAO oPossibleCauseDAO = new PossibleCauseDAO();

		try {
			lista = oPossibleCauseDAO.listarPossibleCauseAlls(name);

			for (Iterator iter = lista.iterator(); iter.hasNext();) {
				PossibleCauseTO possibleCause = (PossibleCauseTO) iter.next();
				Search bean = new Search(String.valueOf(possibleCause.getIdPossibleCause()), possibleCause.getDescPossibleCause());
				result.add(bean);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return result;
	}

	/**
	 * 
	 * @param name
	 * @return
	 */
	public static Collection getActionRecommended(String name) {
		Vector result = new Vector();
		List lista = null;

		ActionRecommendedDAO oActionRecommendedDAO = new ActionRecommendedDAO();

		try {
			lista = oActionRecommendedDAO.listarActionRecommendedAlls(name);

			for (Iterator iter = lista.iterator(); iter.hasNext();) {
				ActionRecommendedTO ActionRecommended = (ActionRecommendedTO) iter.next();
				Search bean = new Search(String.valueOf(ActionRecommended.getIdActionRecommended()), ActionRecommended.getDescActionRecommended(), ActionRecommended.getIdRegisterClass() );
				result.add(bean);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return result;
	}

	/**
	 * 
	 * @param security
	 * @param userOwner
	 * @param idGroup
	 * @return
	 */
	public static Collection getProcesosSacop(Hashtable security, String userOwner, String idGroup) {
		Vector result = new Vector();
		try {
			StringBuilder sql = new StringBuilder();
			sql.append("SELECT  idnode,  name, description ");
			sql.append("FROM struct ");
			sql.append("WHERE nodetype='").append(Constants.numToProcess).append("' ");
			sql.append("AND NameIcon='").append(Constants.imgToProcess).append("' ");
			if(!ToolsHTML.isEmptyOrNull(userOwner)) {
				sql.append("AND ownerResponsible='").append(userOwner).append("' ");
			}
			sql.append("ORDER BY LOWER(name) ");
			
			Vector datos = JDBCUtil.doQueryVector(sql.toString(),Thread.currentThread().getStackTrace()[1].getMethodName());
			for (int row = 0; row < datos.size(); row++) {
				PermissionUserForm forma = null;
				Properties properties = (Properties) datos.elementAt(row);
				String id = properties.getProperty("IdNode");
				if (security != null) {
					forma = (PermissionUserForm) security.get(id);
				}
				if ((forma != null && forma.getToView() == Constants.permission)) {
					Search bean = new Search(String.valueOf(properties.getProperty("idnode")), properties.getProperty("name"));
					result.add(bean);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * 
	 * @param idPlanilla
	 * @param idPerson
	 * @return
	 * @throws Exception
	 */
	public static Collection getPlanillasacopaccionFilterUser(String idPlanilla, String idPerson) throws Exception {
		Vector result = new Vector();
		StringBuffer sql = new StringBuffer(
				" SELECT ta.idplanillasacopaccion,ta.accion,ta.fecha  FROM tbl_planillasacopaccion ta,tbl_sacopaccionporpersona tp ");
		sql.append(" where ta.idplanillasacopaccion=tp.idplanillasacopaccion and ");
		sql.append(" ta.idplanillasacop1=").append(idPlanilla).append(" and ");
		sql.append(" tp.idperson=").append(idPerson);
		sql.append(" and ta.active= '").append(Constants.permission).append("'");
		sql.append(" and tp.firmo != '").append(Constants.permission).append("'");
		sql.append(" order by ta.fecha");
		Vector datos = JDBCUtil.doQueryVector(sql.toString(),Thread.currentThread().getStackTrace()[1].getMethodName());
		// System.out.println("[getPlanillasacopaccionFilterUser]" +
		// sql.toString());
		for (int row = 0; row < datos.size(); row++) {
			Properties properties = (Properties) datos.elementAt(row);
			String id = properties.getProperty("idplanillasacopaccion") != null ? properties.getProperty("idplanillasacopaccion") : "0";
			String desc = properties.getProperty("fecha") != null ? ToolsHTML.sdfShowWithoutHour.format(ToolsHTML.date.parse(properties.getProperty("fecha")))
					: "";
			desc = desc + "                    " + properties.getProperty("accion") != null ? properties.getProperty("accion") : "";
			Search bean = new Search(id, desc);
			result.add(bean);
		}
		return result;
	}

	public static Collection getPlanillasacopaccion(String idplanilla) {
		Vector result = new Vector();
		PlantillaAccion plantillaAccion = null;

		try {
			StringBuilder sql = new StringBuilder(" SELECT * FROM tbl_planillasacopaccion ").append(" where active= '").append(Constants.permission)
					.append("'");
			if (!ToolsHTML.isEmptyOrNull(idplanilla)) {
				sql.append(" and idplanillasacop1=").append(idplanilla);
			}
			sql.append(" order by fecha");
			Vector datos = JDBCUtil.doQueryVector(sql.toString(),Thread.currentThread().getStackTrace()[1].getMethodName());
			// System.out.println("[getPlanillasacopaccion]" + sql.toString());
			for (int row = 0; row < datos.size(); row++) {
				Properties properties = (Properties) datos.elementAt(row);
				String id = properties.getProperty("idplanillasacopaccion") != null ? properties.getProperty("idplanillasacopaccion") : "0";
				String fecha = properties.getProperty("fecha") != null
						? ToolsHTML.sdfShowWithoutHour.format(ToolsHTML.date.parse(properties.getProperty("fecha"))) : "";
				StringBuffer desc = new StringBuffer();
				desc.append(fecha).append("                 ").append("                 ").append("                 ");
				desc.append(properties.getProperty("accion") != null ? properties.getProperty("accion") : "");
				// String desc = fecha + " " +
				// properties.getProperty("accion")!=null?properties.getProperty("accion"):"";
				Search bean = new Search(id, desc.toString());
				result.add(bean);
			}

			/*
			 * if (!ToolsHTML.isEmptyOrNull(idplanilla)) { List lista = HibernateUtil .createCriteriaEq(PlantillaAccion.class,"idplanillasacop1",new
			 * Long(idplanilla)); for (Iterator iter = lista.iterator(); iter.hasNext();) { plantillaAccion = (PlantillaAccion)iter.next(); desc =
			 * ToolsHTML.sdfShowWithoutHour. format(ToolsHTML.date.parse(plantillaAccion.getFecha())); desc = desc + " " + plantillaAccion.getAccion(); Search
			 * bean = new Search (String.valueOf(plantillaAccion.getIdplanillasacopaccion()), desc); result.add(bean); } } else { Query query =
			 * HibernateUtil.getQuery("Planillasacopaccion.alls"); for (Iterator iter = query.list().iterator(); iter.hasNext();) { plantillaAccion =
			 * (PlantillaAccion)iter.next(); desc = ToolsHTML.sdfShowWithoutHour .format(ToolsHTML.date.parse(plantillaAccion.getFecha())); desc = desc + " " +
			 * plantillaAccion.getAccion(); ToolsHTML.sdfShowWithoutHour .format(ToolsHTML.date.parse(plantillaAccion.getFecha())) + " " +
			 * plantillaAccion.getAccion() Search bean = new Search(String.valueOf (plantillaAccion.getIdplanillasacopaccion()),desc); result.add(bean); } }
			 */
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	public static Collection getTitulosPlanillasSacop(String name) {
		TitulosPlanillasSacopDAO oTitulosPlanillasSacopDAO = new TitulosPlanillasSacopDAO();
		TitulosPlanillasSacopTO obj = null;
		Vector result = new Vector();

		ArrayList lista;
		try {
			lista = oTitulosPlanillasSacopDAO.listar(name);

			for (Iterator iter = lista.iterator(); iter.hasNext();) {
				obj = (TitulosPlanillasSacopTO) iter.next();
				Search bean = new Search(obj.getNumtitulosplanillas(), obj.getTitulosplanillas());
				result.add(bean);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return result;
	}

	/**
	 * 
	 * @param name
	 * @return
	 */
	public static Collection<Search> getClasificacionPlanillasSacop(String name) {
		ClasificacionPlanillasSacopDAO oClasificacionPlanillasSacopDAO = new ClasificacionPlanillasSacopDAO();
		ClasificacionPlanillasSacopTO obj = null;
		Vector result = new Vector();

		try {
			ArrayList lista = oClasificacionPlanillasSacopDAO.listar(name);
	
			for (Iterator<ClasificacionPlanillasSacopTO> iter = lista.iterator(); iter.hasNext();) {
				obj = (ClasificacionPlanillasSacopTO) iter.next();
				Search bean = new Search(String.valueOf(obj.getId()), obj.getDescripcion());
				result.add(bean);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return result;
	}

	public static String getOrigen(String id) {
		ArrayList resp = new ArrayList();
		String origen = "";
		try {
			StringBuffer sql = new StringBuffer("SELECT * FROM tbl_titulosplanillassacop WHERE active= '").append(Constants.permission).append("'");
			sql.append(" and numtitulosplanillas=").append(id);
			Vector datos = JDBCUtil.doQueryVector(sql.toString(),Thread.currentThread().getStackTrace()[1].getMethodName());
			// //System.out.println("[getOrigen]" + sql.toString());
			for (int row = 0; row < datos.size(); row++) {
				Properties properties = (Properties) datos.elementAt(row);
				origen = properties.getProperty("titulosplanillas");
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return origen;
	}

	public static String getAreas(String id) {
		ArrayList resp = new ArrayList();
		String Area = "";
		try {
			StringBuffer sql = new StringBuffer("SELECT idarea,area,activea FROM tbl_area WHERE activea= '").append(Constants.permission).append("'");
			sql.append(" and idarea=").append(id);
			Vector datos = JDBCUtil.doQueryVector(sql.toString(),Thread.currentThread().getStackTrace()[1].getMethodName());
			// //System.out.println("[getAreas]" + sql.toString());
			for (int row = 0; row < datos.size(); row++) {
				Properties properties = (Properties) datos.elementAt(row);
				Area = properties.getProperty("area");
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return Area;
	}

	public static Collection getAreas() {
		ArrayList resp = new ArrayList();
		try {
			StringBuffer sql = new StringBuffer("SELECT idarea,area,activea FROM tbl_area WHERE activea= '").append(Constants.permission).append("'");
			sql.append(" order by lower(area) ");
			Vector datos = JDBCUtil.doQueryVector(sql.toString(),Thread.currentThread().getStackTrace()[1].getMethodName());
			// //System.out.println("[getAreas]" + sql.toString());
			for (int row = 0; row < datos.size(); row++) {
				Properties properties = (Properties) datos.elementAt(row);
				Search bean = new Search(properties.getProperty("idarea"), properties.getProperty("area"));// ,
				resp.add(bean);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return resp;
	}

	public static Collection getBusquedaStatus() {
		ArrayList resp = new ArrayList();
		try {
			Search bean = new Search(LoadSacop.edoBorrador, LoadSacop.obtenerEdoBorrador(LoadSacop.edoBorrador));
			resp.add(bean);
			bean = new Search(LoadSacop.edoPendienteVerifSeg, LoadSacop.obtenerEdoBorrador(LoadSacop.edoPendienteVerifSeg));
			resp.add(bean);
			bean = new Search(LoadSacop.edoEmitida, LoadSacop.obtenerEdoBorrador(LoadSacop.edoEmitida));
			resp.add(bean);
			bean = new Search(LoadSacop.edoCerrado, LoadSacop.obtenerEdoBorrador(LoadSacop.edoCerrado));
			resp.add(bean);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return resp;
	}

	public static Collection getProcesosSacopNotIn(String campo, String id) {
		ArrayList resp = new ArrayList();
		try {
			StringBuffer sql = new StringBuffer("SELECT idnode,name,description FROM struct WHERE nodetype= '").append(Constants.numToProcess).append("'");
			sql.append(" AND NameIcon='").append(Constants.imgToProcess).append("'");

			if (ToolsHTML.checkValue(id)) {
				sql.append(" AND idnode ").append(" not in ('").append(id.replaceAll(",", "','")).append("')");
			}
			sql.append(" order by lower(name) ");
			Vector datos = JDBCUtil.doQueryVector(sql.toString(),Thread.currentThread().getStackTrace()[1].getMethodName());
			// //System.out.println("[getProcesosSacop]" + sql.toString());
			for (int row = 0; row < datos.size(); row++) {
				Properties properties = (Properties) datos.elementAt(row);
				Search bean = new Search(properties.getProperty("idnode"), properties.getProperty("name"));// +":"+properties.getProperty("description"));//,
				resp.add(bean);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return resp;
	}

	@SuppressWarnings("unchecked")
	public static Collection getAllProcesosSacop() {
		ArrayList resp = new ArrayList();
		try {
			StringBuffer sql = new StringBuffer();
			sql.append("SELECT a.idNode, a.Name, b.Name FROM struct a, struct b ");
			sql.append("WHERE CAST(a.idNodeParent AS INT)=b.idNode AND a.nodeType='").append(Constants.numToProcess).append("' ");
			sql.append("AND a.NameIcon='").append(Constants.imgToProcess).append("' ");
			sql.append("ORDER BY lower(a.Name) ");

			CachedRowSet datos = JDBCUtil.executeQuery(sql, Thread.currentThread().getStackTrace()[1].getMethodName());
 
			while(datos.next()) {
				Search bean = new Search(datos.getString(1), datos.getString(2), datos.getString(3));
				resp.add(bean);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return resp;
	}

	public static Collection getProcesosSacopNotIn(Hashtable security, String idGroup, boolean noesta, String id1) {
		//
		// //Hashtable security,String userOwner,String idGroup
		Vector result = new Vector();
		StringBuffer sql = new StringBuffer("select  idnode,  name, description from struct where nodetype=").append(Constants.numToProcess);
		sql.append(" AND NameIcon='").append(Constants.imgToProcess).append("'");
		if (noesta) {
			sql.append(" AND idnode ").append(" not in (").append(id1).append(")");
		} else {
			sql.append(" AND idnode ").append(" in (").append(id1).append(")");
		}
		try {

			// para obtener la localidada o ruta complta del proceso
			PermissionUserForm forma = null;
			boolean isAdmon = idGroup.equalsIgnoreCase(DesigeConf.getProperty("application.admon"));
			sql.append(" order by lower(name) ");
			Vector datos = JDBCUtil.doQueryVector(sql.toString(),Thread.currentThread().getStackTrace()[1].getMethodName());
			for (int row = 0; row < datos.size(); row++) {
				Properties properties = (Properties) datos.elementAt(row);
				String id = properties.getProperty("IdNode");
				if (security != null) {
					forma = (PermissionUserForm) security.get(id);
				}
				// if ((forma!=null&&forma.getToView() ==Constants.permission)
				// || isAdmon ) {
				if ((forma != null && forma.getToView() == Constants.permission)) {
					Search bean = new Search(String.valueOf(properties.getProperty("idnode")),
							properties.getProperty("name") + ":" + properties.getProperty("description"));
					result.add(bean);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;

	}

	public static Collection<Search> getProcesosSacop(String id) {
		ArrayList<Search> resp = new ArrayList<Search>();
		try {
			StringBuffer sql = new StringBuffer("SELECT idnode,name,description FROM struct WHERE nodetype= '").append(Constants.numToProcess).append("' ");
			sql.append(" AND NameIcon='").append(Constants.imgToProcess).append("'");
			if (ToolsHTML.checkValue(id)) {
				sql.append(" AND idnode ").append(" in ('").append(id.replaceAll(",", "','")).append("')");
			}
			sql.append(" order by lower(name) ");
			// System.out.println("sql = " + sql);
			Vector<Properties> datos = JDBCUtil.doQueryVector(sql.toString(),Thread.currentThread().getStackTrace()[1].getMethodName());
			for (int row = 0; row < datos.size(); row++) {
				Properties properties = (Properties) datos.elementAt(row);
				Search bean = new Search(properties.getProperty("idnode"), properties.getProperty("name"));
				resp.add(bean);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return resp;
	}

	public static boolean getProcesoOrigenEnUsoSacop(String id) {
		ArrayList resp = new ArrayList();
		Connection con = null;
		PreparedStatement st = null;
		ResultSet rs = null;
		boolean swEncontroProceso = false;
		Hashtable procesos = new Hashtable();

		// separamos todos los procesos que traemos en una coleccion
		StringTokenizer strp = new StringTokenizer(id, ",");
		while (strp.hasMoreElements()) {
			String proc1 = (String) strp.nextToken();
			if (!procesos.containsKey(proc1)) {
				procesos.put(proc1, proc1);
			}
		}
		try {
			// buscamos todos los proicesos que hay en la sacop
			StringBuffer sql = new StringBuffer("SELECT procesosafectados  FROM tbl_planillasacop1 WHERE active = '").append(Constants.permission).append("'");
			con = JDBCUtil.getConnection(Thread.currentThread().getStackTrace()[1].getMethodName());
			// System.out.println("sql.toString()=" + sql.toString());
			st = con.prepareStatement(JDBCUtil.replaceCastMysql(sql.toString()));
			rs = st.executeQuery();

			// si esos procesos existen en sacop, mandamos un verdadero , no c
			// puede eliminar esos procesos.
			while ((rs.next()) && (!swEncontroProceso)) {
				StringTokenizer strk = new StringTokenizer(rs.getString("procesosafectados"), ",");
				while ((strk.hasMoreElements()) && (!swEncontroProceso)) {
					String procesoinSacop = (String) strk.nextToken();
					swEncontroProceso = procesos.containsKey(procesoinSacop);
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {

			try {
				if (con != null) {
					con.close();
				}
				if (st != null) {
					st.close();
				}
				if (rs != null) {
					rs.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return swEncontroProceso;
	}

	public static Collection getSolicitudinformaNotIn(String campo, String id, boolean showcargo) throws Exception {
		Vector resp = new Vector();
		StringBuffer query = new StringBuffer(50);
		query.append("SELECT p.idPerson,nameUser,idGrupo,");
		switch (Constants.MANEJADOR_ACTUAL) {
		case Constants.MANEJADOR_MSSQL:
			query.append("(p.Apellidos + ' ' + p.Nombres) AS nombre,");
			break;
		case Constants.MANEJADOR_POSTGRES:
			query.append("(p.Apellidos || ' ' || p.Nombres) AS nombre,");
			break;
		case Constants.MANEJADOR_MYSQL:
			query.append("CONCAT(p.Apellidos , ' ' , p.Nombres) AS nombre,");
			break;
		}
		query.append("c.cargo,a.area");
		query.append(" FROM person p ");
		query.append(",tbl_cargo c,tbl_area a");
		query.append(" WHERE c.idarea=a.idarea ");
		query.append(" AND c.idcargo=cast(p.cargo as int) ");
		if (!ToolsHTML.isEmptyOrNull(id)) {
			query.append(" AND ").append(campo).append(" not in (").append(id).append(")");
		}
		if (showcargo) {
			query.append(" order by lower(c.cargo) ");
		} else {
			query.append(" order by lower(p.Apellidos) ");
		}
		Vector datos = JDBCUtil.doQueryVector(query.toString(),Thread.currentThread().getStackTrace()[1].getMethodName());
		String idPerson;
		String idGrupo;
		String userName;
		for (int row = 0; row < datos.size(); row++) {
			Properties properties = (Properties) datos.elementAt(row);
			userName = properties.getProperty("nameUser");
			idGrupo = properties.getProperty("idGrupo");
			idPerson = properties.getProperty("idPerson");
			Search bean = null;
			if (showcargo) {
				bean = new Search(userName, properties.getProperty("cargo"));
				bean.setAditionalInfo(properties.getProperty("nombre"));
			} else {
				bean = new Search(userName, properties.getProperty("nombre"));
				bean.setAditionalInfo(properties.getProperty("cargo"));
			}

			resp.add(bean);
		}

		return resp;
	}

	/**
	 * 
	 * @param campo
	 * @param id
	 * @param showcargo
	 * @return
	 * @throws Exception
	 */
	public static Collection getSolicitudinforma(String campo, String id, boolean showcargo) throws Exception {
		return getSolicitudinforma(campo, id, showcargo, false);
	}

	/**
	 * 
	 * @param campo
	 * @param id
	 * @param showcargo
	 * @param checkJustActives
	 * @return
	 * @throws Exception
	 */
	public static Collection getSolicitudResponsable(String campo, String id, boolean showcargo, boolean checkJustActives, String idNodeProceso) throws Exception {
		
		idNodeProceso = idNodeProceso==null || idNodeProceso.trim().equals("") ? "0" : idNodeProceso;
		
		Vector resp = new Vector();
		StringBuffer query = new StringBuffer(50);
		query.append("SELECT p.idPerson,nameUser,idGrupo,");
		switch (Constants.MANEJADOR_ACTUAL) {
		case Constants.MANEJADOR_MSSQL:
			query.append("(p.Apellidos + ' ' + p.Nombres) AS nombre,");
			break;
		case Constants.MANEJADOR_POSTGRES:
			query.append("(p.Apellidos || ' ' || p.Nombres) AS nombre,");
			break;
		case Constants.MANEJADOR_MYSQL:
			query.append("CONCAT(p.Apellidos , ' ' , p.Nombres) AS nombre,");
			break;
		}
		query.append(" c.cargo,a.area ");
		query.append(" FROM person p ,tbl_cargo c,tbl_area a, ");
		if(Constants.MANEJADOR_ACTUAL==Constants.MANEJADOR_POSTGRES) {
			query.append(" (select s.ownerResponsible from struct s where s.nodeType='2' ");
		} else {
			query.append(" (select s.ownerResponsible from struct s where s.nodeType=2 ");
			
		}
		if(idNodeProceso!=null) {
			query.append(" and s.idNode in (").append(idNodeProceso).append(") ");
		}
		query.append(" group by (ownerResponsible)) s ");
		query.append(" WHERE c.idarea=a.idarea ");
		query.append(" AND c.idcargo=cast(p.cargo as int) ");
		query.append(" AND s.ownerResponsible=p.nameUser ");
		if (checkJustActives) {
			query.append(" AND p.accountActive='").append(Constants.permission).append("' ");
		}
		if (!ToolsHTML.isEmptyOrNull(id)) {
			query.append(" AND ").append(campo).append(" in (").append(id).append(")");
		}
		if (showcargo) {
			query.append(" order by lower(c.cargo) ");
		} else {
			query.append(" order by lower(p.Apellidos) ");
		}

		Vector datos = JDBCUtil.doQueryVector(query.toString(),Thread.currentThread().getStackTrace()[1].getMethodName());
		String idPerson;
		String idGrupo;
		String userName;
		for (int row = 0; row < datos.size(); row++) {
			Properties properties = (Properties) datos.elementAt(row);
			userName = properties.getProperty("nameUser");
			idGrupo = properties.getProperty("idGrupo");
			idPerson = properties.getProperty("idPerson");
			Search bean = null;
			if (showcargo) {
				bean = new Search(userName, properties.getProperty("cargo"));
				bean.setAditionalInfo(properties.getProperty("nombre"));
			} else {
				bean = new Search(userName, properties.getProperty("nombre"));
				bean.setAditionalInfo(properties.getProperty("cargo"));
			}

			resp.add(bean);
		}

		return resp;
	}
	
	public static Collection getSolicitudResponsableAll(String campo, String id, boolean showcargo, boolean checkJustActives) throws Exception {
		Vector resp = new Vector();
		StringBuffer query = new StringBuffer(50);
		query.append("SELECT p.idPerson,nameUser,idGrupo,");
		switch (Constants.MANEJADOR_ACTUAL) {
		case Constants.MANEJADOR_MSSQL:
			query.append("(p.Apellidos + ' ' + p.Nombres) AS nombre,");
			break;
		case Constants.MANEJADOR_POSTGRES:
			query.append("(p.Apellidos || ' ' || p.Nombres) AS nombre,");
			break;
		case Constants.MANEJADOR_MYSQL:
			query.append("CONCAT(p.Apellidos , ' ' , p.Nombres) AS nombre,");
			break;
		}
		query.append(" c.cargo,a.area, s.idNode ");
		query.append(" FROM person p ,tbl_cargo c,tbl_area a, struct s ");
		query.append(" WHERE c.idarea=a.idarea ");
		query.append(" AND c.idcargo=cast(p.cargo as int) ");
		query.append(" AND s.ownerResponsible=p.nameUser ");
		if (checkJustActives) {
			query.append(" AND p.accountActive='").append(Constants.permission).append("' ");
		}
		if (!ToolsHTML.isEmptyOrNull(id)) {
			query.append(" AND ").append(campo).append(" in (").append(id).append(")");
		}
		if (showcargo) {
			query.append(" order by lower(c.cargo) ");
		} else {
			query.append(" order by lower(p.Apellidos) ");
		}

		Vector datos = JDBCUtil.doQueryVector(query.toString(),Thread.currentThread().getStackTrace()[1].getMethodName());
		String idPerson;
		String idGrupo;
		String userName;
		for (int row = 0; row < datos.size(); row++) {
			Properties properties = (Properties) datos.elementAt(row);
			userName = properties.getProperty("nameUser");
			idGrupo = properties.getProperty("idGrupo");
			idPerson = properties.getProperty("idPerson");
			Search bean = null;
			if (showcargo) {
				bean = new Search(userName, properties.getProperty("cargo"));
				bean.setAditionalInfo(properties.getProperty("nombre"));
			} else {
				bean = new Search(userName, properties.getProperty("nombre"));
				bean.setAditionalInfo(properties.getProperty("cargo"));
			}
			bean.setField1(properties.getProperty("idNode"));

			resp.add(bean);
		}

		return resp;
	}

	
	public static Collection getSolicitudinforma(String campo, String id, boolean showcargo, boolean checkJustActives) throws Exception {
		Vector resp = new Vector();
		StringBuffer query = new StringBuffer(50);
		query.append("SELECT p.idPerson,nameUser,idGrupo,");
		switch (Constants.MANEJADOR_ACTUAL) {
		case Constants.MANEJADOR_MSSQL:
			query.append("(p.Apellidos + ' ' + p.Nombres) AS nombre,");
			break;
		case Constants.MANEJADOR_POSTGRES:
			query.append("(p.Apellidos || ' ' || p.Nombres) AS nombre,");
			break;
		case Constants.MANEJADOR_MYSQL:
			query.append("CONCAT(p.Apellidos , ' ' , p.Nombres) AS nombre,");
			break;
		}
		query.append("c.cargo,a.area ");
		query.append(" FROM person p ");
		query.append(",tbl_cargo c,tbl_area a");
		query.append(" WHERE c.idarea=a.idarea ");
		query.append(" AND c.idcargo=cast(p.cargo as int) ");
		if (checkJustActives) {
			query.append(" AND p.accountActive='").append(Constants.permission).append("' ");
		}
		if (!ToolsHTML.isEmptyOrNull(id)) {
			query.append(" AND ").append(campo).append(" in (").append(id).append(")");
		}
		if (showcargo) {
			query.append(" order by lower(c.cargo) ");
		} else {
			query.append(" order by lower(p.Apellidos) ");
		}

		Vector datos = JDBCUtil.doQueryVector(query.toString(),Thread.currentThread().getStackTrace()[1].getMethodName());
		String idPerson;
		String idGrupo;
		String userName;
		for (int row = 0; row < datos.size(); row++) {
			Properties properties = (Properties) datos.elementAt(row);
			userName = properties.getProperty("nameUser");
			idGrupo = properties.getProperty("idGrupo");
			idPerson = properties.getProperty("idPerson");
			Search bean = null;
			if (showcargo) {
				bean = new Search(userName, properties.getProperty("cargo"));
				bean.setAditionalInfo(properties.getProperty("nombre"));
			} else {
				bean = new Search(userName, properties.getProperty("nombre"));
				bean.setAditionalInfo(properties.getProperty("cargo"));
			}

			resp.add(bean);
		}

		return resp;
	}

	public static Collection getSolicitudinforma(String campo, String id) throws Exception {
		Vector resp = new Vector();
		StringBuffer query = new StringBuffer(50);
		query.append("SELECT p.idPerson,nameUser,idGrupo,");
		switch (Constants.MANEJADOR_ACTUAL) {
		case Constants.MANEJADOR_MSSQL:
			query.append("(p.Apellidos + ' ' + p.Nombres) AS nombre,");
			break;
		case Constants.MANEJADOR_POSTGRES:
			query.append("(p.Apellidos || ' ' || p.Nombres) AS nombre,");
			break;
		case Constants.MANEJADOR_MYSQL:
			query.append("CONCAT(p.Apellidos , ' ' , p.Nombres) AS nombre,");
			break;
		}
		query.append("c.cargo");
		query.append(" FROM person p,tbl_cargo c,tbl_area a WHERE c.idarea=a.idarea ");
		query.append(" AND c.idcargo=cast(p.cargo as int)");
		if (!ToolsHTML.isEmptyOrNull(id)) {
			query.append(" AND ").append(campo).append(" in (").append(id).append(")");

			query.append(" order by lower(Apellidos) ");
			Vector datos = JDBCUtil.doQueryVector(query.toString(),Thread.currentThread().getStackTrace()[1].getMethodName());
			String idPerson;
			String idGrupo;
			String userName;
			for (int row = 0; row < datos.size(); row++) {
				Properties properties = (Properties) datos.elementAt(row);
				userName = properties.getProperty("nameUser");
				idGrupo = properties.getProperty("idGrupo");
				idPerson = properties.getProperty("idPerson");
				Search bean = new Search(userName, properties.getProperty("nombre"));
				bean.setAditionalInfo(properties.getProperty("cargo"));
				resp.add(bean);
			}
		}
		return resp;
	}

	public static Collection getTomarAcciones() throws Exception {
		Vector resp = new Vector();
		StringBuffer query = new StringBuffer(50);
		String idPerson;
		String idGrupo;
		String userName;
		for (int row = 1; row < 50; row++) {
			// Properties properties = (Properties) datos.elementAt(row);
			/*
			 * userName = properties.getProperty("nameUser"); idGrupo = properties.getProperty("idGrupo"); idPerson = properties.getProperty("idPerson");
			 */
			Search bean = new Search(String.valueOf(row), String.valueOf(row));
			// bean.setAditionalInfo(properties.getProperty("cargo"));
			resp.add(bean);
		}
		// }
		return resp;
	}

	public static Collection getRequisitosAplicableSacopNotIn(String campo, String id) {
		ArrayList resp = new ArrayList();
		Connection con = null;
		PreparedStatement st = null;
		ResultSet rs = null;
		try {
			StringBuffer sql = new StringBuffer("SELECT idNorm,description  FROM norms WHERE active = '").append(Constants.permission).append("'");
			if (ToolsHTML.checkValue(id)) {
				sql.append(" AND ").append(campo).append(" not in (").append(id).append(")");
			}
			sql.append(" order by lower(description) ");
			// //System.out.println("[getRequisitosAplicableSacop]" +
			// sql.toString());
			con = JDBCUtil.getConnection(Thread.currentThread().getStackTrace()[1].getMethodName());
			st = con.prepareStatement(JDBCUtil.replaceCastMysql(sql.toString()));
			rs = st.executeQuery();
			while (rs.next()) {
				Search bean = new Search(rs.getString("idNorm") != null ? rs.getString("idNorm") : "",
						rs.getString("description") != null ? rs.getString("description") : "");// ,
				resp.add(bean);
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {

			try {
				if (con != null) {
					con.close();
				}
				if (st != null) {
					st.close();
				}
				if (rs != null) {
					rs.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return resp;
	}

	public static Collection getRequisitosAplicableSacop(String campo, String id) {
		ArrayList resp = new ArrayList();
		Connection con = null;
		PreparedStatement st = null;
		ResultSet rs = null;
		try {
			StringBuilder sb = new StringBuilder("SELECT idNorm,sistema_gestion,indice,description ").append("FROM norms ").append("WHERE active = '")
					.append(String.valueOf(Constants.permission)).append("' ");
			if (ToolsHTML.checkValue(id)) {
				sb.append(" AND ").append(campo).append(" in (").append(id).append(")");
			}
			sb.append(" ORDER BY lower(sistema_gestion),lower(indice)");
			// System.out.println("[getRequisitosAplicableSacop]" +
			// sql.toString());
			con = JDBCUtil.getConnection(Thread.currentThread().getStackTrace()[1].getMethodName());
			st = con.prepareStatement(JDBCUtil.replaceCastMysql(sb.toString()));
			rs = st.executeQuery();
			while (rs.next()) {
				// for (int row = 0; row < datos.size(); row++) {
				// Properties properties = (Properties) datos.elementAt(row);
				if ((!ToolsHTML.isEmptyOrNull(rs.getString("idNorm"))) && (!ToolsHTML.isEmptyOrNull(rs.getString("description")))) {

					String idNorm = rs.getString("idNorm") != null ? rs.getString("idNorm") : "";
					sb.setLength(0);
					sb.append("");
					if (rs.getString("description") != null) {
						sb.append(rs.getString("sistema_gestion")).append(" ").append(rs.getString("indice")).append("- ").append(rs.getString("description"));
					}

					Search bean = new Search(idNorm, sb.toString());
					resp.add(bean);
				}
				//
				//
				//
				// Search bean = new Search(
				// rs.getString("idNorm") != null ? rs.getString("idNorm")
				// : "",
				// rs.getString("titleNorm") != null ? rs
				// .getString("titleNorm") : "");// ,
				// resp.add(bean);
				//prueba
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			JDBCUtil.closeQuietly(con, st, rs);
		}
		return resp;
	}

	public static boolean upDate(PlantillaAccion forma) {
		try {
			StringBuffer delete = new StringBuffer("delete from tbl_sacopaccionporpersona ");
			delete.append(" where idplanillasacopaccion=").append(forma.getIdplanillasacopaccion());
			// //System.out.println("delete = " + delete);
			JDBCUtil.doUpdate(delete.toString());

			StringTokenizer str = new StringTokenizer(forma.getResponsables(), ",");
			String accionperson = "";
			boolean salio = false;
			while (str.hasMoreElements()) {
				accionperson = (String) str.nextToken();
				StringBuffer insertaccion = new StringBuffer("Insert into tbl_sacopaccionporpersona (idplanillasacopaccion,idperson,firmo,active)");
				insertaccion.append(" VALUES(").append(forma.getIdplanillasacopaccion()).append(",").append(accionperson.toString());
				insertaccion.append(",'0','1')");
				// //System.out.println("insertaccion = " + insertaccion);
				salio = JDBCUtil.doUpdate(insertaccion.toString()) > 0;
			}
			return salio;

		} catch (Exception e) {
			// setMensaje(e.getMessage());
			e.printStackTrace();
		}
		return false;
	}

	public static boolean deleteAccion(PlantillaAccion forma) throws ApplicationExceptionChecked {
		try {
			// Borramos todos los usuarios de las acciones , para introducir los
			// nuevos usuarios escojidos
			StringBuffer delete = new StringBuffer("");
			delete.append("DELETE FROM tbl_sacopaccionporpersona").append(" WHERE Idplanillasacopaccion=").append(forma.getIdplanillasacopaccion());
			JDBCUtil.doUpdate(delete.toString());
			StringBuffer delete2 = new StringBuffer("DELETE FROM tbl_planillasacopaccion WHERE idplanillasacopaccion = ")
					.append(forma.getIdplanillasacopaccion());
			// delete2.append(" AND idplanillasacop1=").append(forma.getIdplanillasacop1());
			JDBCUtil.doUpdate(delete2.toString());
			// actualizamos la fecha estimada de la sacop, esta debe ser igual a
			// la fecha superior de todas las acciones
			StringBuffer actualizaFechaPendiente = new StringBuffer("");
			actualizaFechaPendiente.append("UPDATE tbl_planillasacop1 ");
			actualizaFechaPendiente.append("SET fechaestimada=(SELECT max(fecha) FROM tbl_planillasacopaccion ");
			actualizaFechaPendiente.append("WHERE idplanillasacop1=").append(forma.getIdplanillasacop1()).append(")");
			actualizaFechaPendiente.append("where idplanillasacop1=").append(forma.getIdplanillasacop1());
			// //System.out.println("actualizaFechaPendiente="+actualizaFechaPendiente.toString());
			return JDBCUtil.doUpdate(actualizaFechaPendiente.toString()) > 0;
		} catch (ApplicationExceptionChecked ae) {
			throw new ApplicationExceptionChecked("E0022");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	public static boolean updateAccionPorPersona(PlantillaAccion forma, String idPerson, String firma) {
		Connection con = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		Locale defaultLocale = new Locale(DesigeConf.getProperty("language.Default"), DesigeConf.getProperty("country.Default"));
		ResourceBundle rb = ResourceBundle.getBundle("LoginBundle", defaultLocale);
		try {
			con = JDBCUtil.getConnection(Thread.currentThread().getStackTrace()[1].getMethodName());
			ps = null;
			rs = null;
			Date fecha = new Date();
			String fechastr = ToolsHTML.date.format(fecha);
			StringBuffer update = new StringBuffer("UPDATE  tbl_sacopaccionporpersona");
			update.append(" set comentario=").append("'").append(forma.getComentario()).append("'").append(",");
			update.append(" fecha=").append("'").append(fechastr.toString()).append("',");

			if (forma.isUpdateEvidenceField()) {
				update.append(" evidencia=").append("'").append(forma.getEvidencia().getFileName()).append("',");
			}

			update.append(" firmo='").append("").append(firma).append("'");
			update.append(" WHERE Idplanillasacopaccion=").append(forma.getIdplanillasacopaccion());
			update.append(" AND idPerson=").append(idPerson);
			update.append(" AND active='").append(Constants.permission).append("'");
			// //System.out.println("updateAccionPorPersona = " +
			// update.toString());
			ps = con.prepareStatement(JDBCUtil.replaceCastMysql(update.toString()));
			ps.executeUpdate();
			if ("1".equalsIgnoreCase(firma)) {
				StringBuffer sqlInfResponsable = new StringBuffer("");
				// los primeros datos del query son los responsables
				// los nomfrm etc, son los datos del firmante
				sqlInfResponsable.append(" select d.nameuser,d.nombres,d.apellidos,d.email,a.sacopnum, ");
				sqlInfResponsable.append(" c.idperson,b.accion,p.nombres as nomfrm , p.apellidos as apellfrm ,p.email as emailfrm from ");
				sqlInfResponsable.append(" person p, ");
				sqlInfResponsable.append(" tbl_planillasacop1 a, ");
				sqlInfResponsable.append(" tbl_planillasacopaccion b, ");
				sqlInfResponsable.append(" tbl_sacopaccionporpersona c, ");
				sqlInfResponsable.append(" (select nameuser,nombres,apellidos,email,idperson from person) d ");
				sqlInfResponsable.append(" where ");
				sqlInfResponsable.append(" c.Idplanillasacopaccion=b.Idplanillasacopaccion and  ");
				sqlInfResponsable.append(" b.idplanillasacop1=a.idplanillasacop1 and ");
				sqlInfResponsable.append(" c.idperson=p.idperson and ");
				sqlInfResponsable.append(" c.idperson= ").append(idPerson);
				sqlInfResponsable.append(" and c.Idplanillasacopaccion= ").append(forma.getIdplanillasacopaccion());
				sqlInfResponsable.append(" and d.idperson=a.respblearea ");
				// //System.out.println("sqlInfResponsable.toString()="+sqlInfResponsable.toString());
				ps = con.prepareStatement(JDBCUtil.replaceCastMysql(sqlInfResponsable.toString()));
				rs = ps.executeQuery();
				// informamos al responsable de que la sacop fue firmada por un
				// usuario asignado a la accion
				if (rs.next()) {
					// -------------------------datos del
					// responsable----------------------------------------
					String nom = rs.getString("nombres") != null ? rs.getString("nombres") : "";
					String apell = rs.getString("apellidos") != null ? rs.getString("apellidos") : "";
					String email = rs.getString("email") != null ? rs.getString("email") : "";
					String nameUser = rs.getString("nameuser") != null ? rs.getString("nameuser") : "";
					// ---------------------------datos del
					// firmante----------------------------------------
					String nomfrm = rs.getString("nomfrm") != null ? rs.getString("nomfrm") : "";
					String apellfrm = rs.getString("apellfrm") != null ? rs.getString("apellfrm") : "";
					String emailfrm = rs.getString("emailfrm") != null ? rs.getString("emailfrm") : "";
					// ----------------------------datos
					// extras---------------------------------------------
					String sacopNum = rs.getString("sacopnum") != null ? rs.getString("sacopnum") : "";
					String accion = rs.getString("accion") != null ? rs.getString("accion") : "";

					StringBuffer comentarios = new StringBuffer("");
					comentarios.append(rb.getString("imp.sr_a")).append(nom).append(" ").append(apell);
					comentarios.append("<br>").append(rb.getString("scp.mailacc1")).append(" ").append(rb.getString("imp.sr_a")).append(nomfrm).append(" ")
							.append(apellfrm);
					comentarios.append("<br>").append(rb.getString("scp.mailacc2")).append(" ").append(accion);
					comentarios.append("<br>").append(rb.getString("scp.mailacc3")).append(" ").append(sacopNum);
					HandlerWorkFlows.notifiedUsers(rb.getString("scp.accfirmada") + " " + sacopNum, rb.getString("mail.nameUser"),
							HandlerParameters.PARAMETROS.getMailAccount(), email.toString(), comentarios.toString());
				}
			}

			return true;
		} catch (Exception e) {
			// setMensaje(e.getMessage());
			e.printStackTrace();
		} finally {
			if (con != null) {
				try {
					con.close();
					if (ps != null) {
						ps.close();
					}
				} catch (Exception e) {
				}

			}
		}
		return false;
	}

	/**
	 * 
	 * @param forma
	 * @return
	 */
	public static boolean updateAccion(PlantillaAccion forma) {
		try {
			StringBuffer update = new StringBuffer("UPDATE  tbl_planillasacopaccion");
			update.append(" set accion=").append("'").append(forma.getAccion()).append("'").append(",");
			update.append(" fecha=").append("'").append(forma.getFecha()).append("'");
			update.append(" WHERE Idplanillasacopaccion=").append(forma.getIdplanillasacopaccion());
			update.append(" AND active='").append(Constants.permission).append("'");
			// //System.out.println("updateAccion = " + update.toString());
			JDBCUtil.doUpdate(update.toString());
			// actualizamos la fecha estimada de la sacop, esta debe ser igual a
			// la fecha superior de todas las acciones
			StringBuffer actualizaFechaPendiente = new StringBuffer("");
			actualizaFechaPendiente.append("UPDATE tbl_planillasacop1 ");
			actualizaFechaPendiente.append("SET fechaestimada=(SELECT max(fecha) FROM tbl_planillasacopaccion ");
			actualizaFechaPendiente.append("WHERE idplanillasacop1=").append(forma.getIdplanillasacop1()).append(")");
			actualizaFechaPendiente.append("where idplanillasacop1=").append(forma.getIdplanillasacop1());
			// //System.out.println("actualizaFechaPendiente="+actualizaFechaPendiente.toString());
			JDBCUtil.doUpdate(actualizaFechaPendiente.toString());

			// Borramos todos los usuarios de las acciones , para introducir los
			// nuevos usuarios escojidos
			StringBuffer delete = new StringBuffer("");
			delete.append("DELETE FROM tbl_sacopaccionporpersona").append(" WHERE Idplanillasacopaccion=").append(forma.getIdplanillasacopaccion());

			// //System.out.println("deletePersonaAccion = " +
			// delete.toString());
			JDBCUtil.doUpdate(delete.toString());

			StringTokenizer str = new StringTokenizer(forma.getResponsables(), ",");
			String accionperson = "";
			boolean salio = false;
			while (str.hasMoreElements()) {
				accionperson = (String) str.nextToken();
				StringBuffer insertaccion = new StringBuffer("Insert into tbl_sacopaccionporpersona (idplanillasacopaccion,idperson,firmo,active)");
				insertaccion.append(" VALUES(").append(forma.getIdplanillasacopaccion()).append(",").append(accionperson.toString());
				insertaccion.append(",'").append(Constants.notPermission).append("','").append(Constants.permission).append("')");
				// //System.out.println("insertaccion = " + insertaccion);
				salio = JDBCUtil.doUpdate(insertaccion.toString()) > 0;
			}
			return salio;

		} catch (Exception e) {
			// setMensaje(e.getMessage());
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * 
	 * @param forma
	 * @return
	 */
	public static boolean insert(Cargo forma) {
		try {
			CargoDAO oCargoDAO = new CargoDAO();
			
			CargoTO oCargoTO = new CargoTO();
			
			oCargoTO.setIdCargo(null);
			oCargoTO.setCargo(forma.getCargo());
			oCargoTO.setIdArea(String.valueOf(forma.getIdarea()));
			oCargoTO.setActivec(Constants.permissionSt);
			oCargoDAO.insertar(oCargoTO);
			
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	public static boolean insert(PossibleCause forma) {
		try {
			PossibleCauseDAO oPossibleCauseDAO = new PossibleCauseDAO();
			
			PossibleCauseTO oPossibleCauseTO = new PossibleCauseTO();
			
			oPossibleCauseTO.setIdPossibleCause(null);
			oPossibleCauseTO.setDescPossibleCause(forma.getDescPossibleCause());
			oPossibleCauseDAO.insertar(oPossibleCauseTO);
			
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	public static boolean insert(ActionRecommended forma) {
		try {
			ActionRecommendedDAO oActionRecommendedDAO = new ActionRecommendedDAO();
			
			ActionRecommendedTO oActionRecommendedTO = new ActionRecommendedTO();
			
			oActionRecommendedTO.setIdActionRecommended(null);
			oActionRecommendedTO.setDescActionRecommended(forma.getDescActionRecommended());
			oActionRecommendedTO.setIdRegisterClass(String.valueOf(forma.getIdRegisterClass()));
			oActionRecommendedDAO.insertar(oActionRecommendedTO);
			
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	public static boolean insertA(Area forma) {
		try {
			AreaDAO oAreaDAO = new AreaDAO();
			AreaTO oAreaTO = new AreaTO();
			
			oAreaTO.setIdarea(null);
			oAreaTO.setArea(forma.getArea());
			oAreaTO.setActivea(Constants.permissionSt);
			oAreaTO.setPrefijo(forma.getPrefijo());
			
			oAreaDAO.insertar(oAreaTO);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	public static int insertVison(Area forma) {
		int num1 = 0;
		try {
			AreaDAO oAreaDAO = new AreaDAO();
			AreaTO oAreaTO = new AreaTO();
			
			oAreaTO.setIdarea(null);
			oAreaTO.setArea(forma.getArea());
			oAreaTO.setActivea(Constants.permissionSt);
			oAreaTO.setPrefijo(forma.getPrefijo());
			
			oAreaDAO.insertar(oAreaTO);

			num1 = Integer.parseInt(oAreaTO.getIdarea());
		} catch (Exception e) {
			// setMensaje(e.getMessage());
			e.printStackTrace();
		}
		return num1;
	}

	public static boolean insert(Area forma) {
		try {
			AreaDAO oAreaDAO = new AreaDAO();
			AreaTO oAreaTO = new AreaTO();
			
			oAreaTO.setIdarea(null);
			oAreaTO.setArea(forma.getArea());
			oAreaTO.setActivea(Constants.permissionSt);
			oAreaTO.setPrefijo(forma.getPrefijo());
			
			return oAreaDAO.insertar(oAreaTO) > 0;

		} catch (Exception e) {
			// setMensaje(e.getMessage());
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * 
	 * @param forma
	 * @return
	 */
	public static boolean insert(PlantillaAccion forma) {
		try {
			int idplanillasacopaccion = HandlerStruct.proximo("idplanillasacopaccion", "idplanillasacopaccion", "idplanillasacopaccion");
			forma.setIdplanillasacopaccion(idplanillasacopaccion);

			// int num =
			// HandlerStruct.proximo("numtitulosplanillas","numtitulosplanillas","numtitulosplanillas");
			StringBuilder sql = new StringBuilder("INSERT INTO tbl_planillasacopaccion")
					.append(" (idplanillasacopaccion,idplanillasacop1,accion,fecha,active) VALUES(").append(forma.getIdplanillasacopaccion()).append(",")
					.append(forma.getIdplanillasacop1()).append(",'").append(forma.getAccion()).append("','").append(forma.getFecha()).append("','")
					.append(forma.getActive()).append("')");
			// System.out.println("insert = " + insert);
			JDBCUtil.doUpdate(sql.toString());

			// actualizamos la fecha estimada de la sacop, esta debe ser igual a
			// la fecha superior de todas las acciones
			sql.setLength(0);
			sql.append("UPDATE tbl_planillasacop1 ").append("SET fechaestimada=(SELECT max(fecha) FROM tbl_planillasacopaccion ")
					.append("WHERE idplanillasacop1=").append(forma.getIdplanillasacop1()).append(")").append("where idplanillasacop1=")
					.append(forma.getIdplanillasacop1());
			// //System.out.println("actualizaFechaPendiente="+actualizaFechaPendiente.toString());
			JDBCUtil.doUpdate(sql.toString());

			String accionperson = "";
			boolean salio = false;
			for (StringTokenizer stk = new StringTokenizer(forma.getResponsables(), ","); stk.hasMoreElements();) {

				accionperson = (String) stk.nextToken();
				sql.setLength(0);
				sql.append("INSERT INTO tbl_sacopaccionporpersona (idplanillasacopaccion, idperson, firmo, active)").append(" VALUES(")
						.append(forma.getIdplanillasacopaccion()).append(",").append(accionperson.toString()).append(",'0','1')");
				salio = JDBCUtil.doUpdate(sql.toString()) > 0;
			}
			return salio;

		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * 
	 * @param forma
	 * @return
	 */
	public static boolean insert(TitulosPlanillasSacop forma) {
		try {
			int num = HandlerStruct.proximo("numtitulosplanillas", "numtitulosplanillas", "numtitulosplanillas");
			StringBuilder sql = new StringBuilder("INSERT INTO tbl_titulosplanillassacop").append(" (numtitulosplanillas,titulosplanillas,active) VALUES(")
					.append(num).append(",'").append(forma.getTitulosplanillas()).append("'").append(",'1')");
			// //System.out.println("insert = " + insert);
			return JDBCUtil.doUpdate(sql.toString()) > 0;
		} catch (Exception e) {
			// setMensaje(e.getMessage());
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * 
	 * @param forma
	 * @return
	 */
	public static boolean insert(ClasificacionPlanillasSacop forma) {
		try {
			int num = HandlerStruct.proximo("clasificacion", "clasificacion", "clasificacion");
			StringBuilder sql = new StringBuilder("INSERT INTO tbl_clasificacionplanillassacop").append(" (id, descripcion, active) VALUES(").append(num)
					.append(",'").append(forma.getDescripcion()).append("'").append(",'1')");
			// //System.out.println("insert = " + insert);
			return JDBCUtil.doUpdate(sql.toString()) > 0;
		} catch (Exception e) {
			// setMensaje(e.getMessage());
			e.printStackTrace();
		}

		return false;
	}

	/**
	 * 
	 * @param forma
	 * @return
	 */
	public static boolean insert(ProcesosSacop forma) {
		try {
			int num = HandlerStruct.proximo("numproceso", "numproceso", "numproceso");
			StringBuilder sql = new StringBuilder("INSERT INTO tbl_procesosacop").append(" (numproceso,proceso,active) VALUES(").append(num).append(",'")
					.append(forma.getProceso()).append("'").append(",'1')");
			// //System.out.println("insert = " + insert);
			return JDBCUtil.doUpdate(sql.toString()) > 0;
		} catch (Exception e) {
			// setMensaje(e.getMessage());
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * 
	 * @param forma
	 * @return
	 */
	public synchronized static boolean edit(ProcesosSacop forma) {
		try {
			StringBuilder sql = new StringBuilder("UPDATE tbl_procesosacop").append(" SET proceso='").append(forma.getProceso()).append("'")
					.append(" WHERE numproceso = ").append(forma.getNumproceso()).append(" AND active='1'");
			return JDBCUtil.doUpdate(sql.toString()) > 0;
		} catch (Exception e) {
			// setMensaje(e.getMessage());
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * 
	 * @param respblearea
	 * @return
	 * @throws Exception
	 */
	public static Collection getInfResponsable(String respblearea) throws Exception {
		PlanillaSacop1DAO oPlanillaSacop1DAO = new PlanillaSacop1DAO();
		PlanillaSacop1TO oPlanillaSacop1TO = new PlanillaSacop1TO();
		
		Vector result = new Vector();

		if (!ToolsHTML.isEmptyOrNull(respblearea)) {
			
			oPlanillaSacop1TO.setRespblearea(respblearea);
			ArrayList lista = oPlanillaSacop1DAO.listar(oPlanillaSacop1TO);
			
			Plantilla1BD planilla2Sacop = null;
			for (Iterator iter = lista.iterator(); iter.hasNext();) {
				planilla2Sacop = new Plantilla1BD((PlanillaSacop1TO) iter.next());
				
				planilla2Sacop.setEmisortxt(HandlerDocuments.getField("nombres", "person", "idperson", String.valueOf(planilla2Sacop.getEmisor()), "=", 1,Thread.currentThread().getStackTrace()[1].getMethodName()) + " "
						+ HandlerDocuments.getField("apellidos", "person", "idperson", String.valueOf(planilla2Sacop.getEmisor()), "=", 1,Thread.currentThread().getStackTrace()[1].getMethodName()));
				planilla2Sacop
						.setRespbleareatxt(HandlerDocuments.getField("nombres", "person", "idperson", String.valueOf(planilla2Sacop.getRespblearea()), "=", 1,Thread.currentThread().getStackTrace()[1].getMethodName())
								+ " " + HandlerDocuments.getField("apellidos", "person", "idperson", String.valueOf(planilla2Sacop.getRespblearea()), "=", 1,Thread.currentThread().getStackTrace()[1].getMethodName()));
				planilla2Sacop.setEstadotxt(LoadSacop.obtenerEdoBorrador(String.valueOf(planilla2Sacop.getEstado())));
				result.add(planilla2Sacop);
			}
		} else {
			
			ArrayList lista = oPlanillaSacop1DAO.listarActive(Constants.permissionSt);
			
			Plantilla1BD planilla2Sacop = null;
			for (Iterator iter = lista.iterator(); iter.hasNext();) {
				planilla2Sacop = new Plantilla1BD((PlanillaSacop1TO) iter.next());
				result.add(planilla2Sacop);
			}
		}
		return result;
	}

	public static boolean delete(Cargo forma) throws ApplicationExceptionChecked {
		Connection con = null;
		PreparedStatement st = null;
		ResultSet rs = null;
		try {
			con = JDBCUtil.getConnection(Thread.currentThread().getStackTrace()[1].getMethodName());
			StringBuffer sql = new StringBuffer("select cargo from person where accountactive='").append(Constants.permission).append("'");
			sql.append(" and cargo='").append(forma.getIdcargo()).append("'");
			st = con.prepareStatement(JDBCUtil.replaceCastMysql(sql.toString()));
			rs = st.executeQuery();
			if (rs.next()) {
				throw new ApplicationExceptionChecked("scp.existeareanuso");
			} else {
				StringBuffer delete = new StringBuffer("DELETE FROM tbl_cargo WHERE idcargo = '").append(forma.getIdcargo()).append("'");
				return JDBCUtil.doUpdate(delete.toString()) > 0;
			}
		} catch (ApplicationExceptionChecked ae) {
			throw new ApplicationExceptionChecked("E0022");
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (con != null) {
					con.close();
				}
				if (st != null) {
					st.close();
				}
				if (rs != null) {
					rs.close();
				}
			} catch (Exception e) {

			}
		}
		return false;
	}

	public static boolean delete(Area forma) throws ApplicationExceptionChecked {
		Connection con = null;
		PreparedStatement st = null;
		ResultSet rs = null;
		try {
			con = JDBCUtil.getConnection(Thread.currentThread().getStackTrace()[1].getMethodName());
			StringBuffer sql = new StringBuffer("select idarea from tbl_cargo where ");
			sql.append("  idarea=").append(forma.getIdarea());
			// System.out.println("sql = " + sql);
			st = con.prepareStatement(JDBCUtil.replaceCastMysql(sql.toString()));
			rs = st.executeQuery();
			if (rs.next()) {
				throw new ApplicationExceptionChecked("scp.existeareanuso");
			} else {
				StringBuffer delete = new StringBuffer("DELETE FROM tbl_area WHERE idarea = ").append(forma.getIdarea());
				return JDBCUtil.doUpdate(delete.toString()) > 0;
			}
		} catch (ApplicationExceptionChecked ae) {
			// if ("scp.existeareanuso".compareTo(ae.getKeyError())==0) {
			throw ae;
			// }
			// throw new ApplicationExceptionChecked("E0022");
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (con != null) {
					con.close();
				}
				if (st != null) {
					st.close();
				}
				if (rs != null) {
					rs.close();
				}
			} catch (Exception e) {

			}
		}
		return false;
	}

	public static boolean delete(PossibleCause forma) throws ApplicationExceptionChecked {
		try {
			PossibleCauseDAO oPossibleCauseDAO = new PossibleCauseDAO();
			PossibleCauseTO oPossibleCauseTO = new PossibleCauseTO();
			
			oPossibleCauseTO.setIdPossibleCause(String.valueOf(forma.getIdPossibleCause()));
			
			oPossibleCauseDAO.eliminar(oPossibleCauseTO);
			
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public static boolean delete(ActionRecommended forma) throws ApplicationExceptionChecked {
		try {
			ActionRecommendedDAO oActionRecommendedDAO = new ActionRecommendedDAO();
			ActionRecommendedTO oActionRecommendedTO = new ActionRecommendedTO();
			
			oActionRecommendedTO.setIdActionRecommended(String.valueOf(forma.getIdActionRecommended()));
			
			oActionRecommendedDAO.eliminar(oActionRecommendedTO);
			
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public static boolean delete(TitulosPlanillasSacop forma) throws ApplicationExceptionChecked {
		Connection con = null;
		PreparedStatement st = null;
		ResultSet rs = null;
		try {
			con = JDBCUtil.getConnection(Thread.currentThread().getStackTrace()[1].getMethodName());
			// con.setAutoCommit(false);
			StringBuffer sql = new StringBuffer("select origensacop from tbl_planillasacop1  where origensacop=").append(forma.getNumtitulosplanillas());
			sql.append(" and active='").append(Constants.permission).append("'");
			// //System.out.println("sql="+sql.toString());
			st = con.prepareStatement(JDBCUtil.replaceCastMysql(sql.toString()));
			rs = st.executeQuery();
			if (rs.next()) {
				throw new ApplicationExceptionChecked("scp.existeorigenuso");
			} else {
				StringBuffer delete = new StringBuffer("DELETE FROM tbl_titulosplanillassacop WHERE numtitulosplanillas = ")
						.append(forma.getNumtitulosplanillas());
				// System.out.println("delete=" + delete);
				// con.prepareStatement(delete.toString());

				return JDBCUtil.doUpdate(delete.toString()) > 0;
				/*
				 * st.execute(); con.commit(); con.setAutoCommit(true);
				 * 
				 * return true;
				 */

			}

		} catch (ApplicationExceptionChecked ae) {
			throw ae;
			// throw new ApplicationExceptionChecked("E0022");
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (con != null) {
					// //System.out.println("cerrando conexion ............");
					con.close();
				}
				if (st != null) {
					st.close();
				}
				if (rs != null) {
					rs.close();
				}
			} catch (Exception e) {

			}
		}
		return false;
	}

	/**
	 * 
	 * @param forma
	 * @return
	 * @throws ApplicationExceptionChecked
	 */
	public static boolean delete(ClasificacionPlanillasSacop forma) throws ApplicationExceptionChecked {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;

		try {
			con = JDBCUtil.getConnection(Thread.currentThread().getStackTrace()[1].getMethodName());
			// con.setAutoCommit(false);
			StringBuilder sql = new StringBuilder("SELECT clasificacion FROM tbl_planillasacop1  WHERE clasificacion=").append(forma.getId())
					.append(" AND active='").append(Constants.permission).append("'");
			// //System.out.println("sql="+sql.toString());
			pst = con.prepareStatement(JDBCUtil.replaceCastMysql(sql.toString()));
			rs = pst.executeQuery();
			if (rs.next()) {
				throw new ApplicationExceptionChecked("scp.existeorigenuso");
			} else {
				sql.setLength(0);
				sql.append("DELETE FROM tbl_clasificacionplanillassacop WHERE id = ").append(forma.getId());
				// System.out.println("delete=" + delete);
				// con.prepareStatement(delete.toString());

				return JDBCUtil.doUpdate(sql.toString()) > 0;
				/*
				 * st.execute(); con.commit(); con.setAutoCommit(true);
				 * 
				 * return true;
				 */

			}

		} catch (ApplicationExceptionChecked ae) {
			throw ae;
			// throw new ApplicationExceptionChecked("E0022");
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			JDBCUtil.closeQuietly(con, pst, rs);
		}

		return false;
	}

	/**
	 * 
	 * @param forma
	 * @return
	 * @throws ApplicationExceptionChecked
	 */
	public static boolean delete(ProcesosSacop forma) throws ApplicationExceptionChecked {
		try {
			StringBuilder sql = new StringBuilder("DELETE FROM tbl_procesosacop WHERE numproceso = ").append(forma.getNumproceso());
			return JDBCUtil.doUpdate(sql.toString()) > 0;
		} catch (ApplicationExceptionChecked ae) {
			throw new ApplicationExceptionChecked("E0022");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * 
	 * @param usuario
	 * @param valorporReferencia
	 * @param idplanillasacop1
	 */
	public static void getUsuarioSacopPendientes(String usuario, plantilla1 valorporReferencia, String idplanillasacop1) {
		String salida = "";
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			StringBuilder sql = new StringBuilder("SELECT estado,respblearea,emisor FROM tbl_planillasacop1 WHERE idplanillasacop1=")
					.append(idplanillasacop1.toString());
			con = JDBCUtil.getConnection(Thread.currentThread().getStackTrace()[1].getMethodName());
			pst = con.prepareStatement(JDBCUtil.replaceCastMysql(sql.toString()));
			rs = pst.executeQuery();

			while (rs.next()) {
				if (usuario.equalsIgnoreCase(rs.getString("respblearea") != null ? rs.getString("respblearea") : "-1")) {
					if (!rs.getString("estado").equalsIgnoreCase(LoadSacop.edoVerificacion)
							&& !rs.getString("estado").equalsIgnoreCase(LoadSacop.edoVerificado)) {
						// si es uno, se crea un valor por session que el
						// ususario pueda modificar
						valorporReferencia.setCmd("1");
					}
				}
				if (usuario.equalsIgnoreCase(rs.getString("emisor") != null ? rs.getString("emisor") : "-1")) {
					if ((rs.getString("estado").equalsIgnoreCase(LoadSacop.edoVerificacion))
							|| rs.getString("estado").equalsIgnoreCase(LoadSacop.edoVerificado)) {
						// si es uno, se crea un valor por session y significa
						// que el ususario puede modificar
						valorporReferencia.setCmd("1");
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			setFinally(con, pst);
		}
	}

	/**
	 * 
	 * @param estasSacop
	 * @param swYaSeleccionadas
	 * @param request
	 * @return
	 */
	public Collection getSacops_ParaRelacionar(String estasSacop, boolean swYaSeleccionadas, HttpServletRequest request) {
		Vector vectorAux = new Vector();
		Vector vector = new Vector();
		Connection con = null;
		PreparedStatement stsql = null;
		ResultSet rs = null;
		ArrayList hash = new ArrayList();

		try {
			// el query pincipal del las sacop relacionadas (queryTotal)
			StringBuffer queryTotal = new StringBuffer();
			String query = retorrnarQuery(estasSacop, swYaSeleccionadas, hash, request);
			queryTotal.append(query.toString());
			queryTotal.append(" ORDER BY LOWER(sacopnum) ");

			// _________________________INICIO_____________________________________________________________//
			// es solo para filtrar si hay procesos para esta rutina
			// se realiza la condicion para procesos en el query....
			HandlerProcesosSacop handlerProcesosSacop = new HandlerProcesosSacop();
			// ______________________________________________________________________________________//
			log.debug("query=" + queryTotal);
			log.debug("estasSacop=" + estasSacop.toString());
			log.debug("queryTotal.toString()=" + queryTotal.toString());
			con = JDBCUtil.getConnection(Thread.currentThread().getStackTrace()[1].getMethodName());
			stsql = con.prepareStatement(JDBCUtil.replaceCastMysql(queryTotal.toString()));
			rs = stsql.executeQuery();
			while (rs.next()) {
				Plantilla1BD planilla2Sacop = new Plantilla1BD();
				long id = rs.getString("idplanillasacop1") != null ? Long.parseLong(rs.getString("idplanillasacop1")) : 0l;
				int idh = hash.indexOf(String.valueOf(id));

				if (id != 0) {
					if (!swYaSeleccionadas) {
						if (hash.contains(String.valueOf(id))) {
							planilla2Sacop.setSelected(true);
						} else {
							planilla2Sacop.setSelected(false);
						}
					} else {
						planilla2Sacop.setSelected(true);
					}

					planilla2Sacop.setIdplanillasacop1(id);
					planilla2Sacop.setSacopnum(rs.getString("sacopnum") != null ? rs.getString("sacopnum") : "");
					planilla2Sacop.setEstado(rs.getString("estado") != null ? Integer.parseInt(rs.getString("estado")) : 0);
					planilla2Sacop.setEstadotxt(LoadSacop.obtenerEdoBorrador(String.valueOf(planilla2Sacop.getEstado())));
					String respblearea = rs.getString("respblearea") != null ? rs.getString("respblearea") : "0";
					// usernotificado es el id del cargo, y de aqui sale el area
					// afectada
					long usernotificado = rs.getString("usernotificado") != null ? Long.parseLong(rs.getString("usernotificado")) : 0L;
					planilla2Sacop.setUsernotificado(usernotificado);
					// es el id del usuario como tal, que representa el area
					// afectada
					planilla2Sacop.setRespblearea(Long.parseLong(respblearea));
					// buscamos todos los procesos relacionados con esta
					// planilla, buscamos su nombre a travez de su id
					planilla2Sacop.setDescripcion(rs.getString("descripcion"));
					planilla2Sacop.setProcesosafectados("");

					StringBuffer proceso = new StringBuffer();
					Collection procesosSacop = HandlerProcesosSacop
							.getProcesosSacop(rs.getString("procesosafectados") != null ? rs.getString("procesosafectados") : "");
					Iterator it = procesosSacop.iterator();
					while (it.hasNext()) {
						Search h = (Search) it.next();
						proceso.append(h.getDescript() != null ? ToolsHTML.cortarCadena(h.getDescript()) : "");
						proceso.append(" ").append("<br>");
					}

					planilla2Sacop.setProcesosafectados(proceso.toString());
					vectorAux.add(planilla2Sacop);
				}
			}
			rs.close();
			stsql.close();
			// hacemos un recorrido por el vector auxiliar para agregar el
			// responsable del proceso con sus nombres
			// o sus cargo, segun parametros del sistema...
			// buscamos si se muestra el area o el nombre del responsable
			boolean showCharge = false;
			String cargosacop = String.valueOf(HandlerParameters.PARAMETROS.getCargosacop());
			if (cargosacop.equalsIgnoreCase(Constants.typeNumByLocationVacio) || (ToolsHTML.isEmptyOrNull(cargosacop))) {
				showCharge = true;
			}
			// recorremos nuevamente la coleccion, y en el id area,
			// buscamos el Responsable del Proceso y el nombre y apellido

			// cargamos los usuario
            Map<String,PersonTO> listPersons = HandlerProcesosSacop.listarPersonAlls();
			PersonTO personTO = null;
			
			Iterator it = vectorAux.iterator();
			while (it.hasNext()) {
				Plantilla1BD planilla2Sacop = (Plantilla1BD) it.next();

				String nombre = "";
				String apellido = "";
				personTO = listPersons.get(String.valueOf(planilla2Sacop.getRespblearea()));
				String responsable = "";
				String area = "";
				area = HandlerDBUser.getCargoAndArea(String.valueOf(planilla2Sacop.getUsernotificado()), true);
				if (personTO != null) {
					nombre = personTO.getNombres() != null ? personTO.getNombres() : "";
					apellido = personTO.getApellidos() != null ? personTO.getApellidos() : "";
					responsable = nombre + "  " + apellido;
				}
				if (showCharge) {
					// me devuelve el cargo del area responsable, el area
					// responsable es la variable usuarionotificado
					planilla2Sacop.setRespbleareatxt(area);
				} else {
					planilla2Sacop.setRespbleareatxt(nombre + " " + apellido);
				}

				// me devuelve el area responsable, el area responsable es la
				// variable usuarionotificado
				planilla2Sacop.setUsernotificadotxt(HandlerDBUser.getCargoAndArea(String.valueOf(planilla2Sacop.getUsernotificado()), false));
				vector.add(planilla2Sacop);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (con != null) {
				try {
					con.close();
				} catch (Exception e) {
					e.printStackTrace();
				}

			}
		}

		return vector;
	}

	/**
	 * 
	 * @param estasSacop
	 * @param swYaSeleccionadas
	 * @param hash
	 * @param request
	 * @return
	 */
	public String retorrnarQuery(String estasSacop, boolean swYaSeleccionadas, ArrayList hash, HttpServletRequest request) {
		boolean swWhere = false;
		// el estado sacop viene del formulario sacop_relacionadas.jsp o viene
		// de ;la base de datos que ya fueron
		// agregados
		StringBuilder query = new StringBuilder(
				"SELECT idplanillasacop1, sacopnum, sacop.estado, respblearea, usernotificado, procesosafectados, descripcion ");
		query.append("FROM tbl_planillasacop1 sacop, person em, person re, tbl_cargo cargo, tbl_area area ");
		query.append("WHERE em.idperson= emisor ");
		query.append("AND re.idperson = respblearea ");
		query.append("AND cargo.idarea = area.idarea ");
		query.append("AND usernotificado = cargo.idcargo ");

		if (swYaSeleccionadas) {
			if (!ToolsHTML.isEmptyOrNull(estasSacop)) {
				query.append("AND idplanillasacop1 in (").append(estasSacop).append(")");
				swWhere = true;
			}
		} else {
			if (!ToolsHTML.isEmptyOrNull(estasSacop)) {
				Sacop_Relacionadas sacop_Relacionadas = new Sacop_Relacionadas();
				// con este metodo devolvemos el hash lleno y sin repeticipones
				// de los valores que fueron escojidos en el
				// formulario, se compara de nuevo con la coleccionpara saber si
				// es seleccionado o no en los checks
				// del formulario con la variable estasSacop.toString() de la
				// pagina sacop_relacionadas.jsp
				// y me devuelve una cad que no sera utilizada aqui
				String cadNoHaceNadaAqui = sacop_Relacionadas.AssociadosEnLimpio(hash, estasSacop.toString(), "");
			}
		}

		String extraFilters = "";
		if (request != null) {
			if (request.getParameter("idSacop") != null && !"".equals(request.getParameter("idSacop"))) {
				extraFilters += "AND sacopnum LIKE UPPER('%" + request.getParameter("idSacop").toUpperCase() + "%') ";
			}
			if (request.getParameter("emisorSacop") != null && !"".equals(request.getParameter("emisorSacop"))) {
				extraFilters += "AND em.nameuser = '" + request.getParameter("emisorSacop") + "' ";
			}
			if (request.getParameter("responsableSacop") != null && !"".equals(request.getParameter("responsableSacop"))) {
				extraFilters += "AND re.nameuser = '" + request.getParameter("responsableSacop") + "' ";
			}
			if (request.getParameter("areaResponsableSacop") != null && !"".equals(request.getParameter("areaResponsableSacop"))) {
				extraFilters += "AND area.idarea = '" + request.getParameter("areaResponsableSacop") + "' ";
			}
			//aqui nuevo filtro extra filtro ojo verificar retornar query
			if (request.getParameter("tipoSacop") != null && !"".equals(request.getParameter("tipoSacop"))) {
				extraFilters += "AND tipo.prefijo = '" + request.getParameter("tipoSacop") + "' ";
			}
			// verifico despues uzcateguidarwinfelipe
			if (request.getParameter("origenSacop") != null && !"".equals(request.getParameter("origenSacop"))) {
				extraFilters += "AND origen.titulosplanillas = '" + request.getParameter("origenSacop") + "' ";
			}
			if (request.getParameter("efectivaSacop") != null && !"".equals(request.getParameter("efectivaSacop"))) {
				extraFilters += "AND eliminarcausaraiz = '" + request.getParameter("efectivaSacop") + "' ";
			}
			if (request.getParameter("idProceso") != null && !"".equals(request.getParameter("idProceso"))) {
				String sep = "";
				extraFilters += "AND (";
				for(int i=1;i<21;i++) {
					extraFilters += sep+" substring_index(substring_index(procesosafectados,',',"+i+"),',',-1) = " + request.getParameter("idProceso");
					sep = " OR ";
				}
				extraFilters += ") ";
			}
			if (request.getParameter("listRegisterUnique") != null && !"".equals(request.getParameter("listRegisterUnique"))) {
				Users user = (Users) request.getSession().getAttribute("user");

				if(user!=null && user.getPlanillaSacop()!=null) {
					String id = user.getPlanillaSacop().getIdDocumentRelated();
					extraFilters += "AND sacop.idDocumentRelated = " + id + " ";
				}
			}
		}

		// AHORA VIENE LA CADENA DE FILTRO
		query.append("AND sacop.estado=").append(LoadSacop.edoCerrado).append(" ");
		query.append(extraFilters);

		return query.toString();
	}

	// trabajamos aqui los procesos afectados.
	// como viene un solo valor y el campo procesosafectados puede venir
	// valor,valor, valor..
	// se hace un resultset y separando cada procesosafectados en un
	// StringTokenizer y compararlo con el valor
	// que trae el proceso de busqueda , hacemos un selec con union
	public String procesarProcesosFiltroenQuery(String procesofiltro) {
		StringBuffer retornarQueryConProcesos = new StringBuffer("");
		StringBuffer estasPlanillasSacop = new StringBuffer("");
		StringBuffer procesosPlanillaSacop = new StringBuffer("");
		boolean comaPrimeravez = false;
		try {
			if (procesofiltro != "") {
				// hacemos un recorrido de tooa la tabla tbl_planillasacop1
				// descomponiendo los procesos
				StringBuffer sql = new StringBuffer("select idplanillasacop1,procesosafectados from tbl_planillasacop1 ");
				sql.append(" where estado=").append(LoadSacop.edoCerrado).append(" ");
				Connection con = JDBCUtil.getConnection(Thread.currentThread().getStackTrace()[1].getMethodName());
				PreparedStatement st = con.prepareStatement(JDBCUtil.replaceCastMysql(sql.toString()));
				ResultSet rs = st.executeQuery();
				while (rs.next()) {
					if (rs.getString("procesosafectados") != null) {

						String descomponerProccesosAfectados = rs.getString("procesosafectados") != null ? rs.getString("procesosafectados") : "";
						log.debug("descomponerProccesosAfectados=" + descomponerProccesosAfectados);
						StringTokenizer stk = new StringTokenizer(descomponerProccesosAfectados, ",");
						boolean swEncontro = false;
						while (stk.hasMoreTokens() && !swEncontro) {
							String proceso = stk.nextToken();
							if (proceso.equalsIgnoreCase(procesofiltro)) {
								swEncontro = true;
								String idplanilla = rs.getString("idplanillasacop1");
								if (comaPrimeravez) {
									estasPlanillasSacop.append(",");
								}
								comaPrimeravez = true;
								estasPlanillasSacop.append(idplanilla);
							}
						}
					}
				}
				if (rs != null) {
					rs.close();
				}
				if (st != null) {
					st.close();
				}
				if (con != null) {
					con.close();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		// por lo menos me encontro un valor
		if (comaPrimeravez) {
			// retornarQueryConProcesos.append("select idplanillasacop1,sacopnum,estado,respblearea,usernotificado from tbl_planillasacop1 ");
			retornarQueryConProcesos.append("  idplanillasacop1 in (").append(estasPlanillasSacop.toString()).append(")");
		}
		return retornarQueryConProcesos.toString();
	}

	public static Collection getUsuarioSacopPendientes(HttpServletRequest request, Users user, String usuario, String estadosacop, boolean verHistoricosTambien, String idDocumentRelatedFilter, boolean saveQueryForExcel) {
		String salida = "";
		Connection con = null;	
		PreparedStatement stsql = null;
		ResultSet rs = null;
		Vector result = new Vector();
		try {
			boolean userIsLikeAnAdmin = PerfilAdministrador.userIsInAdminGroup(user);

			con = JDBCUtil.getConnection(Thread.currentThread().getStackTrace()[1].getMethodName());
			StringBuffer sql = new StringBuffer("");
			/*
			sql = new StringBuffer("SELECT DISTINCT usernotificado, tbl_planillasacop1.idplanillasacop1, estado, ");
			sql.append("sacopnum, respblearea, emisor, solicitudinforma, tblaccionporpersona.idperson as responsables, ");
			sql.append("tblacciones.idplanillasacopaccion, tbl_planillasacop1.idplanillasacop1esqueleto, ");
			sql.append("tbl_planillasacop1.descripcion, tbl_planillasacop1.noconformidades, tbl_planillasacop1.fechaemision,tbl_planillasacop1.fecha_cierre, ");
			sql.append("tbl_planillasacop1.origensacop, tit.titulosplanillas, tbl_planillasacop1.usuarioSacops1 ");
			sql.append("FROM tbl_planillasacop1 ");
			sql.append("LEFT OUTER JOIN tbl_titulosplanillassacop tit ON tbl_planillasacop1.origensacop=tit.numtitulosplanillas, ");
			sql.append("(SELECT idplanillasacopaccion, idplanillasacop1 FROM tbl_planillasacopaccion) tblacciones, ");
			sql.append("(SELECT idperson, idplanillasacopaccion FROM tbl_sacopaccionporpersona) tblaccionporpersona ");
			//sql.append("tbl_titulosplanillassacop tit ");
			sql.append("WHERE tbl_planillasacop1.idplanillasacop1=tblacciones.idplanillasacop1 ");
			//sql.append("AND tbl_planillasacop1.origensacop=tit.numtitulosplanillas ");
			sql.append("AND tbl_planillasacop1.active='").append(Constants.permission).append("' ");
			sql.append("AND tblaccionporpersona.idplanillasacopaccion=tblacciones.idplanillasacopaccion ");
			
			if(idDocumentRelatedFilter!=null && idDocumentRelatedFilter.trim().length()>0) {
				sql.append("AND tbl_planillasacop1.idDocumentRelated=").append(idDocumentRelatedFilter).append(" ");
				userIsLikeAnAdmin = true; // colocaremos el perfil admin para que liste todos los relacionados
			}
			
			if (!ToolsHTML.isEmptyOrNull(estadosacop)) {
				if (estadosacop.equalsIgnoreCase(LoadSacop.edoPendienteVerifSeg)) {
					sql.append("AND (tbl_planillasacop1.estado='").append(estadosacop).append("'");
					sql.append(" OR tbl_planillasacop1.estado='").append(LoadSacop.edoVerificacion).append("'");
					sql.append(" OR tbl_planillasacop1.estado='").append(LoadSacop.edoEnEjecucion).append("'");
					sql.append(" OR tbl_planillasacop1.estado='").append(LoadSacop.edoEmitida).append("'");
					sql.append(" OR tbl_planillasacop1.estado='").append(LoadSacop.edoBorrador).append("'");
					sql.append(" OR tbl_planillasacop1.estado='").append(LoadSacop.edoVerificado).append("'");
					sql.append(" OR tbl_planillasacop1.estado='").append(LoadSacop.edoAprobado).append("')");
				} else if (estadosacop.equalsIgnoreCase(LoadSacop.edoEmitida)) {
					sql.append(" AND( ");
					sql.append("tbl_planillasacop1.estado='").append(LoadSacop.edoEmitida).append("'");
					sql.append(" OR tbl_planillasacop1.estado='").append(LoadSacop.edoRechazado).append("'");
					sql.append(" ) ");
				} else {
					sql.append(" AND tbl_planillasacop1.estado='").append(estadosacop).append("'");
				}
			}
			sql.append(" UNION ");
			sql.append("SELECT usernotificado, idplanillasacop1, estado, sacopnum, respblearea, emisor, solicitudinforma, ");
			sql.append("'0' as responsables, '0' as idplanillasacopaccion, tbl_planillasacop1.idplanillasacop1esqueleto, ");
			sql.append("tbl_planillasacop1.descripcion, tbl_planillasacop1.noconformidades, tbl_planillasacop1.fechaemision,tbl_planillasacop1.fecha_cierre, ");
			sql.append("tbl_planillasacop1.origensacop, tit.titulosplanillas, tbl_planillasacop1.usuarioSacops1 ");
			sql.append("FROM tbl_planillasacop1 ");
			sql.append("LEFT OUTER JOIN tbl_titulosplanillassacop tit ON tbl_planillasacop1.origensacop=tit.numtitulosplanillas ");
			
			boolean isWhere = false;
			if (!ToolsHTML.isEmptyOrNull(estadosacop)) {
				sql.append("WHERE ");
				isWhere = true;
				if (estadosacop.equalsIgnoreCase(LoadSacop.edoPendienteVerifSeg)) {
					sql.append(" (tbl_planillasacop1.estado='").append(estadosacop).append("'");
					sql.append(" or tbl_planillasacop1.estado='").append(LoadSacop.edoVerificacion).append("'");
					sql.append(" or tbl_planillasacop1.estado='").append(LoadSacop.edoEnEjecucion).append("'");
					sql.append(" or tbl_planillasacop1.estado='").append(LoadSacop.edoAprobado).append("'");
					sql.append(" or tbl_planillasacop1.estado='").append(LoadSacop.edoEmitida).append("'");
					sql.append(" or tbl_planillasacop1.estado='").append(LoadSacop.edoBorrador).append("')");
					sql.append(" and tbl_planillasacop1.active='").append(Constants.permission).append("'");
				} else if (estadosacop.equalsIgnoreCase(LoadSacop.edoEmitida)) {
					sql.append(" ( tbl_planillasacop1.estado='").append(LoadSacop.edoEmitida).append("'");
					sql.append(" or tbl_planillasacop1.estado='").append(LoadSacop.edoRechazado).append("'");
					sql.append("   )");
					sql.append(" and tbl_planillasacop1.active='").append(Constants.permission).append("'");
				} else {
					sql.append(" tbl_planillasacop1.estado='").append(estadosacop).append("'");
					sql.append(" and tbl_planillasacop1.active='").append(Constants.permission).append("'");
				}
			}
			
			if(idDocumentRelatedFilter!=null && idDocumentRelatedFilter.trim().length()>0) {
				sql.append(isWhere?"AND ":"WHERE ").append(" tbl_planillasacop1.idDocumentRelated=").append(idDocumentRelatedFilter).append(" ");
			}
			

			sql.append(" order by tbl_planillasacop1.sacopnum, tbl_planillasacop1.idplanillasacop1  desc");
			*/
			
			sql.append("SELECT DISTINCT fecha_cierre,usernotificado, ps1.idplanillasacop1, ps1.estado, ps1.sacopnum, respblearea, emisor, solicitudinforma, tblaccionporpersona.idperson as responsables, tblacciones.idplanillasacopaccion, ps1.idplanillasacop1esqueleto, ps1.descripcion, ps1.noconformidades, ps1.fechaemision, ps1.origensacop, tit.titulosplanillas, ps1.usuarioSacops1 "); 
			sql.append(",em.nombres AS enombre, em.apellidos AS eapellidos, re.nombres AS rnombre, re.apellidos AS rapellidos, area.area, ps1.estado, tit.titulosplanillas, ps1.fechaemision, ps1.descripcion, procesosafectados, eliminarcausaraiz, fechaWhenDiscovered, ps1.correcpreven , tbl_c.descripcion AS clasificacion, ps1.fechareal, ps1.noconformidades, ps1.accionesrecomendadas , ps1.fecha_verificacion, ps1.fecha_cierre, ");
			sql.append("ps1.trackingSacop,ps1.numberTrackingSacop ");
			sql.append("FROM tbl_planillasacop1 ps1, person em, person re, tbl_cargo cargo, tbl_area area, tbl_titulosplanillassacop tit, (SELECT idplanillasacopaccion, idplanillasacop1 FROM tbl_planillasacopaccion) tblacciones, (SELECT idperson, idplanillasacopaccion FROM tbl_sacopaccionporpersona) tblaccionporpersona ");
			sql.append(",tbl_clasificacionplanillassacop tbl_c ");
			sql.append("WHERE ps1.idplanillasacop1=tblacciones.idplanillasacop1 "); 
			sql.append("AND ps1.origensacop=tit.numtitulosplanillas ");
			sql.append("AND tbl_c.id = ps1.clasificacion "); 
			sql.append("AND ps1.active='1' ");
			sql.append("AND tblaccionporpersona.idplanillasacopaccion=tblacciones.idplanillasacopaccion "); 
			sql.append("AND em.idperson= emisor ");
			sql.append("AND re.idperson = respblearea "); 
			sql.append("AND cargo.idarea = area.idarea ");
			sql.append("AND usernotificado = cargo.idcargo ");  
			if (!ToolsHTML.isEmptyOrNull(estadosacop)) {
				sql.append("AND ");
				if (estadosacop.equalsIgnoreCase(LoadSacop.edoPendienteVerifSeg)) {
					sql.append(" (ps1.estado='").append(estadosacop).append("' ");
					sql.append(" or ps1.estado='").append(LoadSacop.edoVerificacion).append("' ");
					sql.append(" or ps1.estado='").append(LoadSacop.edoEnEjecucion).append("' ");
					sql.append(" or ps1.estado='").append(LoadSacop.edoAprobado).append("' ");
					sql.append(" or ps1.estado='").append(LoadSacop.edoEmitida).append("' ");
					sql.append(" or ps1.estado='").append(LoadSacop.edoVerificado).append("' ");
					sql.append(" or ps1.estado='").append(LoadSacop.edoBorrador).append("') ");
				} else if (estadosacop.equalsIgnoreCase(LoadSacop.edoEmitida)) {
					sql.append(" ( ps1.estado='").append(LoadSacop.edoEmitida).append("' ");
					sql.append(" or ps1.estado='").append(LoadSacop.edoRechazado).append("' ");
					sql.append("   )");
				} else {
					sql.append(" ps1.estado='").append(estadosacop).append("' ");
				}
			}
			if(idDocumentRelatedFilter!=null && idDocumentRelatedFilter.trim().length()>0) {
				sql.append("AND ").append(" ps1.idDocumentRelated=").append(idDocumentRelatedFilter).append(" ");
			}		

			sql.append("UNION ");

			sql.append("SELECT fecha_cierre,usernotificado, idplanillasacop1, ps1.estado, ps1.sacopnum, respblearea, emisor, solicitudinforma, '0' as responsables, '0' as idplanillasacopaccion, ps1.idplanillasacop1esqueleto, ps1.descripcion, ps1.noconformidades, ps1.fechaemision, ps1.origensacop, tit.titulosplanillas, ps1.usuarioSacops1 "); 
			sql.append(",em.nombres AS enombre, em.apellidos AS eapellidos, re.nombres AS rnombre, re.apellidos AS rapellidos, area.area, ps1.estado, tit.titulosplanillas, ps1.fechaemision, ps1.descripcion, procesosafectados, eliminarcausaraiz, fechaWhenDiscovered, ps1.correcpreven , tbl_c.descripcion AS clasificacion, ps1.fechareal, ps1.noconformidades, ps1.accionesrecomendadas , ps1.fecha_verificacion, ps1.fecha_cierre, ");
			sql.append("ps1.trackingSacop,ps1.numberTrackingSacop ");
			sql.append("FROM tbl_planillasacop1 ps1 "); 
			sql.append("LEFT JOIN tbl_clasificacionplanillassacop tbl_c ON tbl_c.id = ps1.clasificacion "); 
			sql.append("LEFT JOIN person em ON em.idperson = ps1.emisor ");
			sql.append("LEFT JOIN person re ON re.idperson = ps1.respblearea "); 
			sql.append("LEFT JOIN tbl_cargo cargo ON ps1.usernotificado = cargo.idcargo "); 
			sql.append("LEFT JOIN tbl_area area ON area.idarea = cargo.idarea ");
			sql.append("LEFT JOIN tbl_titulosplanillassacop tit ON ps1.origensacop=tit.numtitulosplanillas "); 
			sql.append("WHERE ps1.active='1' ");
			if (!ToolsHTML.isEmptyOrNull(estadosacop)) {
				sql.append("AND ");
				if (estadosacop.equalsIgnoreCase(LoadSacop.edoPendienteVerifSeg)) {
					sql.append(" (ps1.estado='").append(estadosacop).append("' ");
					sql.append(" or ps1.estado='").append(LoadSacop.edoVerificacion).append("' ");
					sql.append(" or ps1.estado='").append(LoadSacop.edoEnEjecucion).append("' ");
					sql.append(" or ps1.estado='").append(LoadSacop.edoAprobado).append("' ");
					sql.append(" or ps1.estado='").append(LoadSacop.edoEmitida).append("' ");
					sql.append(" or ps1.estado='").append(LoadSacop.edoVerificado).append("' ");
					sql.append(" or ps1.estado='").append(LoadSacop.edoBorrador).append("') ");
				} else if (estadosacop.equalsIgnoreCase(LoadSacop.edoEmitida)) {
					sql.append(" ( ps1.estado='").append(LoadSacop.edoEmitida).append("' ");
					sql.append(" or ps1.estado='").append(LoadSacop.edoRechazado).append("' ");
					sql.append("   )");
				} else {
					sql.append(" ps1.estado='").append(estadosacop).append("' ");
				}
			}
			// GRG se a�ade validacion de documento origen solo si ese parametro <> NULL 06 agosto 2020 
			if(idDocumentRelatedFilter!=null && idDocumentRelatedFilter.trim().length()>0) {
				sql.append("AND ").append(" ps1.idDocumentRelated=").append(idDocumentRelatedFilter).append(" ");
			}		
			// fin de validacion GRG
			sql.append("ORDER BY sacopnum, idplanillasacop1  DESC ");
			

			// //System.out.println("getUsuarioSacopPendientes="+sql.toString());
			
			if(saveQueryForExcel) {
				request.getSession().setAttribute("LAST_QUERY_SEARCH_SACOP", sql.toString());
			}
			stsql = con.prepareStatement(JDBCUtil.replaceCastMysql(sql.toString()));
			rs = stsql.executeQuery();
			String emisor = null;
			String respblearea = null;
			String solicitante = null;
			String usernotificado = null;
			String sacopnum;
			String estado;
			boolean soloBorrador = false;
			String planillasacop = null;
			Hashtable unicaPlanillaAccion = new Hashtable();
			Hashtable unicaPlanilla = new Hashtable();
			Hashtable listaDeleteSacops = null;

			if (rs != null) {
				
				// vamos a filtrar las sacops cerradas por fecha de vigencia
				boolean isValiditySacop = false;
				int validitySacopMonth = 0;
				int validitySacopType = 0;
				Date validityDate = null;
				if(estadosacop!=null && estadosacop.equals(LoadSacop.edoCerrado)) {
					// preguntamos si esta activo el campo de vigencia
					isValiditySacop = HandlerParameters.PARAMETROS.getValiditySacop()==1;
					validitySacopType = HandlerParameters.PARAMETROS.getValiditySacopType();
					validitySacopMonth =  HandlerParameters.PARAMETROS.getValiditySacopMonth();
					
					switch(validitySacopType) { 
					case HandlerParameters.validitySacopType0: // vigencia por cierre de ejercicio
						validityDate = finPeriodoAnterior(validitySacopMonth);
						break;
					case HandlerParameters.validitySacopType1: // vigencia por meses 
						validityDate = restarMeses(new Date(), validitySacopMonth);
						break;
					}
				}
				
				// cargamos los usuario
	            Map<String,PersonTO> listPersons = HandlerProcesosSacop.listarPersonAlls();

	            // cargamos las areas
	            Map<String,AreaTO> listAreas = HandlerProcesosSacop.listarAreaAlls();
				
				// cargamos los cargos
	            Map<String,CargoTO> listCargos = HandlerProcesosSacop.listarCargoAlls();
				
				String mismaPlanilla = "";
				while (rs.next()) {
					
					if(isValiditySacop) {
						if(rs.getTimestamp("fecha_cierre")!=null) {
							Date fechaCierre = rs.getTimestamp("fecha_cierre");
							
							if(fechaCierre.getTime() < validityDate.getTime()) {
								continue;
							}
						}
					}
					
					
					// cada planilla tiene su grupo de acciones que pertenecen a
					// esa planilla
					// se sacan los usuarios por cada una de las planillas y por
					// sus respectivas acciones de esa planilla
					planillasacop = rs.getString("idplanillasacop1") != null ? rs.getString("idplanillasacop1") : "";
					if (mismaPlanilla.equalsIgnoreCase(planillasacop)) {
						// es la misma planilla y continua
					} else {
						// es diferente planilla, borra los hijos d la planilla
						// anterior que son las acciones
						mismaPlanilla = planillasacop;
						Enumeration enumu = unicaPlanillaAccion.keys();
						while (enumu.hasMoreElements()) {
							String values = (String) enumu.nextElement();
							unicaPlanillaAccion.remove(values);
						}
					}
					if (!unicaPlanillaAccion.containsKey(rs.getString("idplanillasacopaccion"))) {
						boolean ejecutar = false;
						Plantilla1BD planilla2Sacop = new Plantilla1BD();
						Hashtable usuariosInformados = new Hashtable();
						StringTokenizer stk = new StringTokenizer(rs.getString("solicitudinforma"), ",");
						StringTokenizer stk2 = new StringTokenizer(rs.getString("responsables") != null ? rs.getString("responsables") : "", ",");
						emisor = rs.getString("emisor") != null ? rs.getString("emisor") : "";
						usernotificado = rs.getString("usernotificado") != null ? rs.getString("usernotificado") : "";
						respblearea = rs.getString("respblearea") != null ? rs.getString("respblearea") : "";
						solicitante = rs.getString("usuarioSacops1") != null ? rs.getString("usuarioSacops1") : "";
						sacopnum = rs.getString("sacopnum") != null ? rs.getString("sacopnum") : "";
						estado = rs.getString("estado") != null ? rs.getString("estado") : "";
						// este id es para saber si iene de intouych o
						// auditorias

						String nombre = null;
						String cargo = null;
						// si es el responsable, el emisor o el
						// usuarionotificado, muestra la planilla con el sw
						// ejecutar
						// if ((usuario.equalsIgnoreCase(usernotificado)) ||
						// (usuario.equalsIgnoreCase(emisor)) ||
						// (usuario.equalsIgnoreCase(respblearea))){
						planilla2Sacop.setIsEmisor(false);
						planilla2Sacop.setIsResponsable(false);
						
						planilla2Sacop.setOrigensacop(rs.getInt("origensacop"));
						planilla2Sacop.setTitulosplanillas(rs.getString("titulosplanillas"));
						
						planilla2Sacop.setTrackingSacop(rs.getString("trackingSacop")!=null?rs.getString("trackingSacop"):"");
						planilla2Sacop.setNumberTrackingSacop(rs.getString("numberTrackingSacop")!=null?rs.getString("numberTrackingSacop"):"");

						
						if ((usuario.equalsIgnoreCase(emisor)) || (usuario.equalsIgnoreCase(respblearea)) || userIsLikeAnAdmin) {
							ejecutar = true;
							// verificamos si es emisor o respoonsable para
							// aplicar en pantalla p`rincipalsacop1.jsp que tipo
							// de
							// eliminacion aplicara
							if (usuario.equalsIgnoreCase(emisor)) {
								planilla2Sacop.setIsEmisor(true);
							}
							if (usuario.equalsIgnoreCase(respblearea)) {
								planilla2Sacop.setIsResponsable(true);
							}
							
						} else {
							// en caso que el usuario logueado sea uno de los
							// usuarios solicitudinforma,mostrar el registro del
							// sacop
							boolean sw = false;
							while ((!sw) && (stk.hasMoreTokens())) {
								String idPerson = stk.nextToken();
								if (usuario.equalsIgnoreCase(idPerson)) {
									sw = true;
									ejecutar = true;
								}
							}
							if (!ejecutar) {
								// en caso que el usuario logueado sea uno de
								// los usuarios responsables
								while ((!sw) && (stk2.hasMoreTokens())) {
									String idPerson = stk2.nextToken();
									if (usuario.equalsIgnoreCase(idPerson)) {
										sw = true;
										ejecutar = true;
									}
								}
							}
						}
						// si la planilla es borrador, entonces para que se
						// pueda mostrar esa planilla
						// solo debe ser que el usuario logueado sea el emisor o
						// del grupo administrador
						if (LoadSacop.edoBorrador.equalsIgnoreCase(estado)) {
							if (usuario.equalsIgnoreCase(emisor) || userIsLikeAnAdmin) {
								ejecutar = true;
							} else {
								ejecutar = false;
							}
						}

						// el ejecutar puede que venga verdadero, pero si esa
						// planilla esta logicamente borrada, el
						// ejecutar se coloca en false para que no se vea
						DeleteSacop formaDelete = new DeleteSacop();
						formaDelete.setIdplanillasacop1(planillasacop);
						// ______________BUSCA SI ESTA ELIMINADO
						// LOGICAMENTE______________________________
						if (listaDeleteSacops == null) {
							listaDeleteSacops = HandlerProcesosSacop.loadDeleteSacops();
						}
						if (listaDeleteSacops.containsKey(formaDelete.getIdplanillasacop1())) {
							CaseInsensitiveProperties prop = (CaseInsensitiveProperties) listaDeleteSacops.get(formaDelete.getIdplanillasacop1());
							formaDelete.setIdplanillasacop1(prop.getProperty("idplanillasacop1"));
							formaDelete.setIdperson(prop.getProperty("idperson"));
						}

						// HandlerProcesosSacop.loadDeleteSacop(formaDelete);
						if (!verHistoricosTambien) {
							// INICIO si el usuario elimino esta planilla de su
							// listas de sacop, entonces no se mostrara
							if (!ToolsHTML.isEmptyOrNull(formaDelete.getIdperson())) {
								StringTokenizer stkeliminados = new StringTokenizer(formaDelete.getIdperson(), ",");
								boolean sw = false;
								while ((!sw) && (stkeliminados.hasMoreTokens())) {
									// se valida, si el usuario esta en la tabla
									// deletelogico,
									// entonces quiere decirque no quiere ver
									// esta planilla
									String idPerson = stkeliminados.nextToken();
									if (usuario.equalsIgnoreCase(idPerson)) {
										sw = true;
										ejecutar = false;
									}
								}
							}
							// FIN si el usuario elimino esta planilla de su
							// listas de sacop, entonces no se mostrara
						} else {
							// si se quieren ver los historicos...,se muestran
							// solo los historicosa,
							// mas no los borradores
							if (!ToolsHTML.isEmptyOrNull(formaDelete.getIdperson())) {
								StringTokenizer stkeliminados = new StringTokenizer(formaDelete.getIdperson(), ",");
								boolean sw = false;
								while ((!sw) && (stkeliminados.hasMoreTokens())) {
									String idPerson = stkeliminados.nextToken();
									if (usuario.equalsIgnoreCase(idPerson)) {
										if (LoadSacop.edoBorrador.equalsIgnoreCase(estado)) {
											sw = true;
											// ejecutar false es que no me lo
											// muestra, es decir, si viene de
											// arriba en true, aqui
											// se le da un parado.
											ejecutar = false;
										}

									}
								}
							}

						}

						if (ejecutar) {
							// unicaPlanillaAccion.put(rs.getString("idplanillasacopaccion"),rs.getString("idplanillasacopaccion"));
							// (!unicaPlanillaAccion.containsKey(rs.getString("idplanillasacopaccion")
							if (!unicaPlanilla.containsKey(planillasacop)) {
								boolean showCharge = false;
								String cargosacop = String.valueOf(HandlerParameters.PARAMETROS.getCargosacop());
								if (cargosacop.equalsIgnoreCase(Constants.typeNumByLocationVacio) || (ToolsHTML.isEmptyOrNull(cargosacop))) {
									showCharge = true;
								}

								unicaPlanilla.put(planillasacop, "usuarioLoad");
								planilla2Sacop.setSacopnum(sacopnum);

								planilla2Sacop.setIdplanillasacop1(planillasacop != null ? Long.parseLong(planillasacop) : 0);
								planilla2Sacop.setEstado(estado != null ? Integer.parseInt(estado) : 0);
								PersonTO personTO = null;
								personTO = listPersons.get(String.valueOf(emisor));
								// datos del emisor
								if (!showCharge) {
									if (personTO != null) {
										nombre = personTO.getNombres() + " " + personTO.getApellidos();
										cargo = personTO.getCargo();
										planilla2Sacop.setMostrarCargo(false);
									}
								} else {
									if (personTO != null) {
										nombre = personTO.getNombres() + " " + personTO.getApellidos();
										cargo = personTO.getCargo();
										planilla2Sacop.setMostrarCargo(true);
									}
								}

								if (cargo == null || personTO == null) {
									if (personTO != null) {
										System.out.println("showCharge[" + showCharge + "]" + " IDSacop=[" + planilla2Sacop.getIdplanillasacop1() + "] emisor["
												+ emisor + "] " + " personTO.getNombres()=" + personTO.getNombres() + " personTO.getApellidos()=" + personTO.getApellidos() + " personTO.getCargo()=" + personTO.getCargo());
									} else {
										System.out.println("showCharge[" + showCharge + "]" + " IDSacop=[" + planilla2Sacop.getIdplanillasacop1() + "] emisor["
												+ emisor + "]");
									}
								}

								CargoTO oCargoTO = listCargos.get(cargo);
								AreaTO oAreaTO = listAreas.get(oCargoTO.getIdArea());
								
								planilla2Sacop.setEmisortxt(nombre);
								planilla2Sacop.setCargoEmisor(oCargoTO.getCargo() + "<br>(" + oAreaTO.getArea() + ")");

								// responsable
								personTO = listPersons.get(String.valueOf(respblearea));

								// datos del responsable
								if (!showCharge) {
									if (personTO != null) {
										nombre = personTO.getNombres() + " " + personTO.getApellidos();
										cargo = personTO.getCargo();
										planilla2Sacop.setMostrarCargo(false);
									}
								} else {
									if (personTO != null) {
										nombre = personTO.getNombres() + " " + personTO.getApellidos();
										cargo = personTO.getCargo();
										planilla2Sacop.setMostrarCargo(true);
									}
								}

								oCargoTO = listCargos.get(cargo);
								oAreaTO = listAreas.get(oCargoTO.getIdArea());
								
								planilla2Sacop.setRespbleareatxt(nombre);

								planilla2Sacop.setCargo(oCargoTO.getCargo() + "<br>(" + oAreaTO.getArea() + ")");
								planilla2Sacop.setEstadotxt(LoadSacop.obtenerEdoBorrador(String.valueOf(planilla2Sacop.getEstado())));
								planilla2Sacop.setEstado(estado != null ? Integer.parseInt(estado) : 0);

								
								// solicitante
								if(solicitante!=null && solicitante.trim().length()>0) {
									personTO = listPersons.get(String.valueOf(solicitante));
									
									if (!showCharge) {
										if (personTO != null) {
											nombre = personTO.getNombres() + " " + personTO.getApellidos();
											cargo = personTO.getCargo();
											planilla2Sacop.setMostrarCargo(false);
										}
									} else {
										if (personTO != null) {
											nombre = personTO.getNombres() + " " + personTO.getApellidos();
											cargo = personTO.getCargo();
											planilla2Sacop.setMostrarCargo(true);
										}
									}

									oCargoTO = listCargos.get(cargo);
									oAreaTO = listAreas.get(oCargoTO.getIdArea());
									
									planilla2Sacop.setSolicitantetxt(nombre);
									planilla2Sacop.setCargoSolicitante(oCargoTO.getCargo() + "<br>(" + oAreaTO.getArea() + ")");
									System.out.println(planilla2Sacop.getSacopnum()+" "+planilla2Sacop.getCargoSolicitante());
								} else {
									planilla2Sacop.setSolicitantetxt("N/A");
									planilla2Sacop.setCargoSolicitante("N/A");
								}
								
								
								// ___________Sacop-Intouch_____________________________________________________________//

								int idplanillasacop1esqueleto = rs.getString("idplanillasacop1esqueleto") != null
										? Integer.parseInt(rs.getString("idplanillasacop1esqueleto")) : -1;
								planilla2Sacop.setIdplanillasacop1esqueleto(idplanillasacop1esqueleto);
								HandlerProcesosSacop handlerProcesosSacop = new HandlerProcesosSacop();
								handlerProcesosSacop.VerificarsiEsDeSacop_Intouch(planilla2Sacop);
								// por si acaso viene cero, la colocamos en -1
								if (0 == planilla2Sacop.getIdplanillasacop1esqueleto()) {
									planilla2Sacop.setIdplanillasacop1esqueleto(-1);
								}
								// ______________________________________________________________________________________________________//
								planilla2Sacop.setDescripcion(rs.getString("descripcion"));
								planilla2Sacop.setNoconformidades(rs.getString("noconformidades"));
								planilla2Sacop.setFechaemision(rs.getTimestamp("fechaemision"));
								planilla2Sacop.setFechaCierre(rs.getTimestamp("fecha_cierre"));
								
								result.add(planilla2Sacop);
							}
						}
					}
				} // end Hashtable
			} // end while
			rs.close();
			// liberamos memoria
			unicaPlanillaAccion = null;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			setFinally(con, stsql);
		}

		return result;
	}

	/**
	 * 
	 * @param idplanillasacop1
	 * @param numsacop
	 * @param estado
	 */
	public static void mandarMailsUsuariosdelSacop(String idplanillasacop1, String numsacop, String estado) {
		String salida = "";
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;

		try {
			Locale defaultLocale = new Locale(DesigeConf.getProperty("language.Default"), DesigeConf.getProperty("country.Default"));
			ResourceBundle rb = ResourceBundle.getBundle("LoginBundle", defaultLocale);

			StringBuilder sql = new StringBuilder(" select tbl_planillasacop1.usernotificado,tbl_planillasacop1.idplanillasacop1,estado,sacopnum")
					.append(",tbl_planillasacop1.correcpreven,respblearea,emisor,solicitudinforma,tblaccionporpersona.idperson as responsables ,tblacciones.idplanillasacopaccion  ")
					.append(" from tbl_planillasacop1, ").append("(select idplanillasacopaccion,idplanillasacop1 from tbl_planillasacopaccion) tblacciones ")
					.append(", (select idperson,idplanillasacopaccion from tbl_sacopaccionporpersona  ) tblaccionporpersona  ")
					.append(" where tbl_planillasacop1.idplanillasacop1=tblacciones.idplanillasacop1 ").append(" and tbl_planillasacop1.idplanillasacop1=")
					.append(idplanillasacop1).append(" and tblaccionporpersona.idplanillasacopaccion=tblacciones.idplanillasacopaccion").append(" union ")
					.append(" select  tbl_planillasacop1.usernotificado,idplanillasacop1 ,estado,sacopnum,correcpreven,respblearea,emisor,solicitudinforma, ")
					.append(" '0' as responsables,'0' as idplanillasacopaccion   from tbl_planillasacop1 ")
					.append(" where tbl_planillasacop1.idplanillasacop1=").append(idplanillasacop1);
			// //System.out.println("[mandarMailsUsuariosdelSacop]"+sql.toString());

			con = JDBCUtil.getConnection(Thread.currentThread().getStackTrace()[1].getMethodName());
			pst = con.prepareStatement(JDBCUtil.replaceCastMysql(sql.toString()));
			rs = pst.executeQuery();

			if (rs != null) {
				Hashtable unSoloMailMandar = new Hashtable();
				ArrayList dataUnSoloMailMandar = new ArrayList();

				StringTokenizer stk = null;
				StringTokenizer stk2 = null;
				String emisor = null;
				String respblearea = null;
				String usernotificado = null;
				String correcpreven = null;
				String sacopnum = null;
				String emisortxt = null;
				String correoEmisor = "";
				String respbleareatxt = null;
				// String usernotificadotxt=null;
				StringBuffer SolicitudInforma = new StringBuffer("");
				StringBuffer AccionInforma = new StringBuffer("");
				String correo = null;
				String usuario = null;
				String nombre = null;

				// cargamos los usuario
	            Map<String,PersonTO> listPersons = HandlerProcesosSacop.listarPersonAlls();
				PersonTO personTO = null;
				
				while (rs.next()) {
					stk = new StringTokenizer(rs.getString("solicitudinforma"), ",");
					stk2 = new StringTokenizer(rs.getString("responsables"), ",");
					emisor = rs.getString("emisor") != null ? rs.getString("emisor") : "";

					respblearea = rs.getString("respblearea") != null ? rs.getString("respblearea") : "";
					usernotificado = rs.getString("usernotificado") != null ? rs.getString("usernotificado") : "";
					correcpreven = rs.getString("correcpreven") != null ? rs.getString("correcpreven") : "";
					sacopnum = rs.getString("sacopnum") != null ? rs.getString("sacopnum") : "";
					if (LoadSacop.Correctiva.equalsIgnoreCase(correcpreven)) {
						correcpreven = rb.getString("scp.ac");
					} else if (LoadSacop.Preventiva.equalsIgnoreCase(correcpreven)) {
						correcpreven = rb.getString("scp.ap");
					}

					// mandamos correo al emisor
					boolean swtienequepasar = true;
					if (!unSoloMailMandar.containsKey(emisor)) {
						personTO = listPersons.get(String.valueOf(emisor));

						if (personTO != null) {
							nombre = personTO.getNombres() + " " + personTO.getApellidos();
							correo = personTO.getEmail();
							InformarPorMail Informarpormail = new InformarPorMail();
							Informarpormail.setNombre(nombre);
							Informarpormail.setCorreo(correo);
							Informarpormail.setId("0");// emisor
							correoEmisor = correo;
							unSoloMailMandar.put(emisor, Informarpormail);
						}
					} else {
						InformarPorMail Informarpormail = (InformarPorMail) unSoloMailMandar.get(emisor);
						emisortxt = Informarpormail.getNombre();

					} // end if

					// mandamos correo al responsable del proceso
					if (!unSoloMailMandar.containsKey(respblearea)) {
						personTO = listPersons.get(String.valueOf(respblearea));

						if (personTO != null) {
							nombre = personTO.getNombres() + " " + personTO.getApellidos();
							correo = personTO.getEmail();
							InformarPorMail Informarpormail = new InformarPorMail();
							Informarpormail.setNombre(nombre);
							Informarpormail.setCorreo(correo);
							Informarpormail.setId("1");// responsable
							unSoloMailMandar.put(respblearea, Informarpormail);
						}
					} else {
						InformarPorMail Informarpormail = (InformarPorMail) unSoloMailMandar.get(respblearea);
						respbleareatxt = Informarpormail.getNombre();

					} // end if
					
					
					// Mandamos mails solicitudinforma
					while (stk.hasMoreTokens()) {
						String idPerson = stk.nextToken();
						if (!unSoloMailMandar.containsKey(idPerson)) {
							personTO = listPersons.get(String.valueOf(idPerson));

							if (personTO != null) {
								nombre = personTO.getNombres() + " " + personTO.getApellidos();
								correo = personTO.getEmail();
								InformarPorMail Informarpormail = new InformarPorMail();
								Informarpormail.setNombre(nombre);
								Informarpormail.setCorreo(correo);
								Informarpormail.setId("3");// solicitudinforma
								unSoloMailMandar.put(idPerson, Informarpormail);

								// //System.out.println("idPerson="+idPerson);
							}
						} // end if
					} // end while
						// estos mails lo mandamos aparte
				} // end while externo
				rs.close();

				Enumeration enume = unSoloMailMandar.keys();
				while (enume.hasMoreElements()) {
					String clave = (String) enume.nextElement();
					InformarPorMail dataUserMail = (InformarPorMail) unSoloMailMandar.get(clave);
					String values[];
					if ("0".equalsIgnoreCase(dataUserMail.getId())) {
						emisortxt = dataUserMail.getNombre();
					}
					if ("1".equalsIgnoreCase(dataUserMail.getId())) {
						respbleareatxt = dataUserMail.getNombre();
					}
					/*
					 * if ("2".equalsIgnoreCase(dataUserMail.getId())){ usernotificadotxt=dataUserMail.getNombre(); }
					 */
					if ("3".equalsIgnoreCase(dataUserMail.getId())) {
						if (enume.hasMoreElements())
							SolicitudInforma.append(dataUserMail.getNombre()).append(",");
						else
							SolicitudInforma.append(dataUserMail.getNombre());
					}
				}
				// ahora a mandar correos
				StringBuffer comentarios = new StringBuffer("");
				comentarios.append(rb.getString("scp.mail1")).append(" ").append(numsacop);
				comentarios.append(rb.getString("scp.mail2")).append(" ").append(estado).append("");
				comentarios.append(rb.getString("scp.mail3")).append(" ").append(emisortxt).append("");
				comentarios.append(rb.getString("scp.mail4")).append(" ").append(respbleareatxt).append("");
				// comentarios.append("<br>Usuarios Notificados:").append(usernotificadotxt).append("");
				/*
				 * if (!ToolsHTML.isEmptyOrNull(SolicitudInforma.toString())){ comentarios .append("<br>La solicitud informa a:").append(SolicitudInforma
				 * .toString()).append(""); }
				 */
				if (!ToolsHTML.isEmptyOrNull(AccionInforma.toString())) {
					if (AccionInforma != null) {
						comentarios.append(rb.getString("scp.mail5")).append(AccionInforma.toString()).append("");
					}

				}
				enume = unSoloMailMandar.keys();
				StringBuffer emailInformadoCad = new StringBuffer();
				StringBuffer nombresc = new StringBuffer();
				while (enume.hasMoreElements()) {
					String clave = (String) enume.nextElement();
					InformarPorMail dataUserMail = (InformarPorMail) unSoloMailMandar.get(clave);
					emailInformadoCad.append(dataUserMail.getCorreo()).append(";");
					nombresc.append(dataUserMail.getNombre()).append(",");
					unSoloMailMandar.remove(clave);
				}

				HandlerWorkFlows.notifiedUsers(correcpreven + " " + sacopnum, rb.getString("mail.nameUser"),
						HandlerParameters.PARAMETROS.getMailAccount(),
						emailInformadoCad.substring(0, emailInformadoCad.lastIndexOf(";")),
						nombresc.substring(0, nombresc.lastIndexOf(",")) + "<br>" + comentarios.toString());
				unSoloMailMandar = null;

			} // end if rs !=null
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			setFinally(con, pst);
		}
	}

	public static void mandarMailsUsuariosdelSacopPorAccion(String idplanillasacop1) {
		String salida = "";
		Connection con = null;
		PreparedStatement stsql = null;
		ResultSet rs = null;
		Locale defaultLocale = new Locale(DesigeConf.getProperty("language.Default"), DesigeConf.getProperty("country.Default"));
		ResourceBundle rb = ResourceBundle.getBundle("LoginBundle", defaultLocale);

		try {
			con = JDBCUtil.getConnection(Thread.currentThread().getStackTrace()[1].getMethodName());
			StringBuffer sql = new StringBuffer("");
			sql.append(" select a.sacopnum,c.idperson,b.accion,p.nombres , p.apellidos,p.email from ");
			sql.append(" person p, ");
			sql.append(" tbl_planillasacop1 a,");
			sql.append(" tbl_planillasacopaccion b,");
			sql.append(" tbl_sacopaccionporpersona c ");
			sql.append(" where ");
			sql.append(" a.idplanillasacop1=").append(idplanillasacop1).append(" and ");
			sql.append(" a.idplanillasacop1=b.idplanillasacop1 and ");
			sql.append(" b.idplanillasacopaccion=c.idplanillasacopaccion and ");
			sql.append(" p.idperson=c.idperson ");
			// //System.out.println("[mandarMailsUsuariosdelSacopPorAccion]"+sql.toString());
			stsql = con.prepareStatement(JDBCUtil.replaceCastMysql(sql.toString()));
			rs = stsql.executeQuery();
			if (rs != null) {
				StringBuffer emailInformadoCad = new StringBuffer();
				StringBuffer comentarios = new StringBuffer("");
				String Sacopnum = "";
				while (rs.next()) {

					String nombre = rs.getString("nombres") != null ? rs.getString("nombres")
							: "" + " " + rs.getString("apellidos") != null ? rs.getString("apellidos") : "";
					comentarios.append(rb.getString("imp.sr_a")).append(" ").append(nombre).append("<br>");
					comentarios.append(rb.getString("scp.accpendiente")).append(" ").append(rs.getString("accion") != null ? rs.getString("accion") : "")
							.append("<br>");
					comentarios.append(rb.getString("scp.accpendiente1")).append(" ").append(rs.getString("sacopnum") != null ? rs.getString("sacopnum") : "");
					comentarios.append("<br><br>");

					emailInformadoCad.append(rs.getString("email") != null ? rs.getString("email") : "").append(";");
					Sacopnum = rs.getString("sacopnum");

				} // end while externo

				HandlerWorkFlows.notifiedUsers(rb.getString("scp.acciones") + " " + Sacopnum, rb.getString("mail.nameUser"),
						HandlerParameters.PARAMETROS.getMailAccount(),
						emailInformadoCad.substring(0, emailInformadoCad.lastIndexOf(";")), comentarios.toString());

				rs.close();
			} // end if rs !=null
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			setFinally(con, stsql);
		}
	}

	/**
	 * Leemos la info completa de una SACOP desde la base de datos.
	 * 
	 * @param campo
	 * @param id
	 * @param showCharge
	 * @param porCodigo
	 * @param esDe_tbl_planillasacop1esqueleto
	 * @return
	 * @throws Exception
	 */
	public static Collection getInfResponsable(String campo, String id, boolean showCharge, boolean porCodigo, boolean esDe_tbl_planillasacop1esqueleto)
			throws Exception {
		Vector result = new Vector();
		Connection con = null;
		PreparedStatement stsql = null;
		ResultSet rs = null;
		StringBuffer sql = new StringBuffer("");

		boolean swLlevaEsqueleto = false;
		if (esDe_tbl_planillasacop1esqueleto) {
			sql.append("select * from tbl_planillasacop1esqueleto");
		} else {
			sql.append("select * from tbl_planillasacop1");
			// sw quew me indica si viene de una sacop precponfigurada
			swLlevaEsqueleto = true;
		}
		if (!ToolsHTML.isEmptyOrNull(id)) {
			if (!porCodigo) {
				sql.append(" where idplanillasacop1=").append(id);

			} else {
				sql.append(" where sacopnum='").append(id).append("'");
			}
		}
		try {
			// cargamos los usuario
            Map<String,PersonTO> listPersons = HandlerProcesosSacop.listarPersonAlls();
            // cargamos las areas
            Map<String,AreaTO> listAreas = HandlerProcesosSacop.listarAreaAlls();
			// cargamos los cargos
            Map<String,CargoTO> listCargos = HandlerProcesosSacop.listarCargoAlls();
			PersonTO personTO = null;
			
			String values[];
			String nombresApellido = null;
			con = JDBCUtil.getConnection(Thread.currentThread().getStackTrace()[1].getMethodName());
			stsql = con.prepareStatement(JDBCUtil.replaceCastMysql(sql.toString()));
			rs = stsql.executeQuery();
			while (rs.next()) {
				Plantilla1BD planilla2Sacop = new Plantilla1BD();
				planilla2Sacop.setIdplanillasacop1(rs.getString("idplanillasacop1") != null ? Long.parseLong(rs.getString("idplanillasacop1")) : 0);
				planilla2Sacop.setSacopnum(rs.getString("sacopnum") != null ? rs.getString("sacopnum") : "");
				planilla2Sacop.setEmisor(rs.getString("emisor") != null ? Long.parseLong(rs.getString("emisor")) : 0);
				planilla2Sacop.setRespblearea(rs.getString("respblearea") != null ? Long.parseLong(rs.getString("respblearea")) : 0);
				planilla2Sacop.setEstado(rs.getString("estado") != null ? Integer.parseInt(rs.getString("estado")) : 0);
				planilla2Sacop.setOrigensacop(rs.getString("origensacop") != null ? Integer.parseInt(rs.getString("origensacop")) : 0);
				planilla2Sacop.setFechaemision(rs.getString("fechaemision") != null ? ToolsHTML.date.parse(rs.getString("fechaemision")) : new Date());
				planilla2Sacop.setFechasacops1(rs.getString("fechasacops1") != null ? ToolsHTML.date.parse(rs.getString("fechasacops1")) : new Date());
				planilla2Sacop.setFechaculminar(rs.getString("fechaculminar") != null ? rs.getString("fechaculminar") : "");
				planilla2Sacop.setRequisitosaplicable(rs.getString("requisitosaplicable") != null ? rs.getString("requisitosaplicable") : "");
				planilla2Sacop.setProcesosafectados(rs.getString("procesosafectados") != null ? rs.getString("procesosafectados") : "");
				planilla2Sacop.setSolicitudinforma(rs.getString("solicitudinforma") != null ? rs.getString("solicitudinforma") : "");
				planilla2Sacop.setDescripcion(rs.getString("descripcion") != null ? rs.getString("descripcion") : "");

				planilla2Sacop.setCausasnoconformidad(rs.getString("causasnoconformidad") != null ? rs.getString("causasnoconformidad") : "");
				planilla2Sacop.setAccionesrecomendadas(rs.getString("accionesrecomendadas") != null ? rs.getString("accionesrecomendadas") : "");
				planilla2Sacop.setCorrecpreven(rs.getString("correcpreven") != null ? rs.getString("correcpreven") : "");
				planilla2Sacop.setRechazoapruebo(rs.getString("rechazoapruebo") != null ? rs.getString("rechazoapruebo") : "");
				planilla2Sacop.setNoaceptada(rs.getString("noaceptada") != null ? rs.getString("noaceptada") : "");
				planilla2Sacop.setAccionobservacion(rs.getString("accionobservacion") != null ? rs.getString("accionobservacion") : "");
				planilla2Sacop.setFechaEstimada(rs.getString("fechaEstimada") != null ? rs.getString("fechaEstimada") : "");
				planilla2Sacop.setFechaReal(rs.getString("fechaReal") != null ? rs.getString("fechaReal") : "");
				planilla2Sacop.setAccionesEstablecidas(rs.getString("accionesEstablecidas") != null ? rs.getString("accionesEstablecidas") : "");
				planilla2Sacop.setAccionesEstablecidastxt(rs.getString("accionesEstablecidastxt") != null ? rs.getString("accionesEstablecidastxt") : "");
				planilla2Sacop.setEliminarcausaraiz(rs.getString("eliminarcausaraiz") != null ? rs.getString("eliminarcausaraiz") : "");
				planilla2Sacop.setEliminarcausaraiztxt(rs.getString("eliminarcausaraiztxt") != null ? rs.getString("eliminarcausaraiztxt") : "");
				planilla2Sacop.setUsernotificado(rs.getString("usernotificado") != null ? Long.parseLong(rs.getString("usernotificado")) : 0);
				planilla2Sacop.setActive(rs.getString("active") != null ? Byte.parseByte(rs.getString("active")) : (byte) 0);
				planilla2Sacop.setContentType(rs.getString("contentType") != null ? rs.getString("contentType") : "");
				planilla2Sacop.setComntresponsablecerrar(rs.getString("comntresponsablecerrar") != null ? rs.getString("comntresponsablecerrar") : "");
				planilla2Sacop.setSacop_relacionadas(rs.getString("sacop_relacionadas") != null ? rs.getString("sacop_relacionadas") : "");
				planilla2Sacop.setNoconformidades(rs.getString("noconformidades") != null ? rs.getString("noconformidades") : "");
				planilla2Sacop.setNoconformidadesref(rs.getString("noconformidadesref") != null ? rs.getString("noconformidadesref") : "");
				//planilla2Sacop.setNoConformidadesDetail(getNoConformidadesDetail(planilla2Sacop.getNoconformidadesref()));
				planilla2Sacop.setIdClasificacion(rs.getInt("clasificacion"));
				planilla2Sacop.setFechaWhenDiscovered(
						rs.getTimestamp("fechaWhenDiscovered") == null ? null : new Date(rs.getTimestamp("fechaWhenDiscovered").getTime()));
				planilla2Sacop.setNameRelatedDocument((rs.getString("nameFile") == null ? null : rs.getString("nameFile")));
				planilla2Sacop.setArchivoTecnica((rs.getString("archivo_tecnica") == null ? null : rs.getString("archivo_tecnica")));
				planilla2Sacop
						.setFechaVerificacion(rs.getTimestamp("fecha_verificacion") == null ? null : new Date(rs.getTimestamp("fecha_verificacion").getTime()));
				planilla2Sacop.setFechaCierre(rs.getTimestamp("fecha_cierre") == null ? null : new Date(rs.getTimestamp("fecha_cierre").getTime()));
				planilla2Sacop.setIdDocumentRelated(rs.getString("idDocumentRelated")!=null?rs.getInt("idDocumentRelated"):0);
				
				planilla2Sacop.setIdDocumentAssociate(rs.getString("idDocumentAssociate")!=null?rs.getInt("idDocumentAssociate"):0);
				planilla2Sacop.setNumVerDocumentAssociate(rs.getString("numVerDocumentAssociate")!=null?rs.getInt("numVerDocumentAssociate"):0);
				planilla2Sacop.setNameDocumentAssociate(rs.getString("nameDocumentAssociate")!=null?rs.getString("nameDocumentAssociate"):"");
				
				planilla2Sacop.setRequireTracking(rs.getString("requireTracking")!=null?rs.getString("requireTracking"):"");
				planilla2Sacop.setRequireTrackingDate(rs.getString("requireTrackingDate")!=null?rs.getString("requireTrackingDate"):"");

				planilla2Sacop.setUsuarioSacops1(rs.getString("usuarioSacops1")!=null?rs.getInt("usuarioSacops1"):0);
				
				planilla2Sacop.setTrackingSacop(rs.getString("trackingSacop")!=null?rs.getString("trackingSacop"):"");
				planilla2Sacop.setNumberTrackingSacop(rs.getString("numberTrackingSacop")!=null?rs.getString("numberTrackingSacop"):"");
				
				planilla2Sacop.setIdRegisterGenerated(rs.getInt("idRegisterGenerated")!=0?rs.getInt("idRegisterGenerated"):0);
				
				planilla2Sacop.setDescripcionAccionPrincipal(rs.getString("descripcionAccionPrincipal") != null ? rs.getString("descripcionAccionPrincipal") : "");

				// System.out.println("..................1......verificar...............................");
				// System.out.println("sql.toString() = " + sql.toString());
				// System.out.println("..................2.....................................");
				if (swLlevaEsqueleto) {
					// viene de la tabla tbl_planillasacop1
					planilla2Sacop.setIdplanillasacop1esqueleto(
							rs.getString("idplanillasacop1esqueleto") != null ? Long.parseLong(rs.getString("idplanillasacop1esqueleto")) : -1);
				} else {
					// viene de la tabla tbl_planillasacop1esqueleto
					planilla2Sacop
							.setIdplanillasacop1esqueleto(rs.getString("idplanillasacop1") != null ? Long.parseLong(rs.getString("idplanillasacop1")) : -1);
				}

				personTO = listPersons.get(String.valueOf(planilla2Sacop.getUsernotificado()));

				if (showCharge) {
					if (personTO != null) {
						nombresApellido = "";
						nombresApellido = personTO.getCargo();
					}
				} else {
					if (personTO != null) {
						nombresApellido = "";
						nombresApellido = personTO.getNombres() + " " + personTO.getApellidos();
					}
				}
				planilla2Sacop.setUsernotificadotxt(nombresApellido);

				personTO = listPersons.get(String.valueOf(planilla2Sacop.getEmisor()));

				if (personTO != null) {
					nombresApellido = "";
					nombresApellido = personTO.getNombres() + " " + personTO.getApellidos();
				}
				planilla2Sacop.setEmisortxt(nombresApellido);

				personTO = listPersons.get(String.valueOf(planilla2Sacop.getRespblearea()));

				if (personTO != null) {
					nombresApellido = "";
					nombresApellido = personTO.getNombres() + " " + personTO.getApellidos();
					planilla2Sacop.setNombres(personTO.getNombres());
					planilla2Sacop.setApellidos(personTO.getApellidos());
				}
				planilla2Sacop.setRespbleareatxt(nombresApellido);
				planilla2Sacop.setEstadotxt(LoadSacop.obtenerEdoBorrador(String.valueOf(planilla2Sacop.getEstado())));
				

				// solicitante
				if(planilla2Sacop.getUsuarioSacops1()!=null && planilla2Sacop.getUsuarioSacops1()>0) {
					personTO = listPersons.get(String.valueOf(planilla2Sacop.getUsuarioSacops1()));
					
					String nombre = null;
					String cargo = null;
					if (!showCharge) {
						if (personTO != null) {
							nombre = personTO.getNombres() + " " + personTO.getApellidos();
							cargo = personTO.getCargo();
						}
					} else {
						if (personTO != null) {
							nombre = personTO.getNombres() + " " + personTO.getApellidos();
							cargo = personTO.getCargo();
						}
					}

					CargoTO oCargoTO = listCargos.get(cargo);
					AreaTO oAreaTO = listAreas.get(oCargoTO.getIdArea());
				
					planilla2Sacop.setSolicitantetxt(nombre);
					planilla2Sacop.setCargoSolicitante(oCargoTO.getCargo() + "<br>(" + oAreaTO.getArea() + ")");
				} else {
					planilla2Sacop.setSolicitantetxt("N/A");
					planilla2Sacop.setCargoSolicitante("N/A");
				}
				

				// ___________Sacop-Intouch_____________________________________________________________//
				HandlerProcesosSacop handlerProcesosSacop = new HandlerProcesosSacop();
				handlerProcesosSacop.VerificarsiEsDeSacop_Intouch(planilla2Sacop);
				// ______________________________________________________________________________________________________//

				result.add(planilla2Sacop);
			}
			rs.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			setFinally(con, stsql);
		}
		return result;
	}

	public void VerificarsiEsDeSacop_Intouch(Plantilla1BD planilla2Sacop) {
		try {
			// cargamos el id de la planilla que deseamos buscar para ver si
			// viene de intouch o auditorias
			HandlerProcesosWonderWare handlerProcesosWonderWare = new HandlerProcesosWonderWare();
			sacop_intouch_padre_frm ssacop_intouch_padre_frm = null;

			boolean activo = true;
			// buscamos por todos los activos, si no hay, buscamos por todos los
			// inactivos
			ssacop_intouch_padre_frm = handlerProcesosWonderWare.getSacopIntouchWonderwarePadre(String.valueOf(planilla2Sacop.getIdplanillasacop1esqueleto()),
					activo);
			if (ssacop_intouch_padre_frm != null) {
				planilla2Sacop.setIdplanillasacop1esqueleto(ssacop_intouch_padre_frm.getIdplanillasacop1());
				// aqui vemmos si la sacop esta dehabilitada o habilitada
				String habilitado = String.valueOf(ssacop_intouch_padre_frm.getEnable());
				planilla2Sacop.setHabilitadoEsqueleto(habilitado);
			} else {
				// buscamos por todos los Inactivos ahora
				activo = false;
				ssacop_intouch_padre_frm = handlerProcesosWonderWare
						.getSacopIntouchWonderwarePadre(String.valueOf(planilla2Sacop.getIdplanillasacop1esqueleto()), activo);
				if (ssacop_intouch_padre_frm != null) {
					planilla2Sacop.setIdplanillasacop1esqueleto(ssacop_intouch_padre_frm.getIdplanillasacop1());
				} else {
					// definitiva,mente no es una sacop_intouch
					planilla2Sacop.setIdplanillasacop1esqueleto(-1);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	// proceso de responsable
	// acciones------------------------------------------------------------------------------------
	public static Collection getInfResponsableAccion(String campo, String id) throws Exception {
		PlanillaSacopAccionDAO oPlanillaSacopAccionDAO = new PlanillaSacopAccionDAO();
		
		Vector result = new Vector();
		ArrayList lista = null;
		
		if (!ToolsHTML.isEmptyOrNull(id)) {
			lista = oPlanillaSacopAccionDAO.listarByIdPlanillaSacop1(id);
		} else {
			lista = oPlanillaSacopAccionDAO.listarOrderByFecha();
		}
		
		PlantillaAccion planilla4Sacop = null;
		for (Iterator iter = lista.iterator(); iter.hasNext();) {
			
			planilla4Sacop = new PlantillaAccion((PlanillaSacopAccionTO) iter.next());
			result.add(planilla4Sacop);
			
		}

		return result;
	}

	public static Date getDateFirstAccion(String id) throws Exception {
		PlanillaSacopAccionDAO oPlanillaSacopAccionDAO = new PlanillaSacopAccionDAO();
		
		Date result = null;
		
		ArrayList lista = oPlanillaSacopAccionDAO.listarByIdPlanillaSacop1(id);
		
		PlantillaAccion planilla4Sacop = null;
		for (Iterator iter = lista.iterator(); iter.hasNext();) {
			
			planilla4Sacop = new PlantillaAccion((PlanillaSacopAccionTO) iter.next());
			result = ToolsHTML.getDateFromString(planilla4Sacop.getFecha());
			break;
		}

		return result;
	}
	
	/**
	 * 
	 * @param idPerson
	 * @param idplanillasacopaccion
	 * @param evidenciaPrefix
	 * @return
	 * @throws Exception
	 */
	public static String getInfAccionPorPersona(String idPerson, String idplanillasacopaccion, String evidenciaPrefix) throws Exception {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		StringBuilder evidencia = new StringBuilder("");
		StringBuilder datosuseraccion = new StringBuilder("");
		Locale defaultLocale = new Locale(DesigeConf.getProperty("language.Default"), DesigeConf.getProperty("country.Default"));
		ResourceBundle rb = ResourceBundle.getBundle("LoginBundle", defaultLocale);

		try {
			Vector result = new Vector();
			StringBuilder sql = new StringBuilder("SELECT comentario,firmo,idperson,fecha,evidencia ").append("FROM tbl_sacopaccionporpersona");
			boolean sw = false;
			if (!ToolsHTML.isEmptyOrNull(idplanillasacopaccion)) {
				sql.append(" WHERE idplanillasacopaccion=").append(idplanillasacopaccion);
				sw = true;
			}
			if (!ToolsHTML.isEmptyOrNull(idPerson)) {
				if (sw) {
					sql.append(" AND idperson=").append(idPerson);
				} else {
					sql.append(" WHERE idperson=").append(idPerson);
					sw = true;
				}
			}

			if (sw) {
				sql.append(" AND active='").append(Constants.permission).append("'");
			} else {
				sql.append("  active='").append(Constants.permission).append("'");
			}

			CachedRowSet users = HandlerDBUser.getListUsers();

			con = JDBCUtil.getConnection(Thread.currentThread().getStackTrace()[1].getMethodName());
			pst = con.prepareStatement(JDBCUtil.replaceCastMysql(sql.toString()));
			rs = pst.executeQuery();

			while (rs.next()) {
				datosuseraccion.append(" <br> ").append(rb.getString("imp.sr_a")).append(": ")
						.append(loadsolicitudImpresion.buscarNomApellUser(rs.getString("idperson"))).append(" <br> ");

				if ("0".equals(rs.getString("firmo"))) {
					// no ha firmado la accion a�n, indicamos eso
					datosuseraccion.append(rb.getString("scp.pendiente.firmar"));
				} else {
					// ya la accion esta firmada, colocamos la fecha de la firma
					datosuseraccion.append(rb.getString("scp.fecha")).append(": ")
							.append(rs.getString("fecha") != null ? ToolsHTML.sdfShowWithoutHour.format(ToolsHTML.date.parse(rs.getString("fecha"))) : "");
				}

				datosuseraccion.append(" <br> ").append(rb.getString("scp.comm")).append(": ")
						.append(rs.getString("comentario") != null ? rs.getString("comentario") : "").append("<br>  ");
				if (!ToolsHTML.isEmptyOrNull(rs.getString("evidencia"))) {
					String userName = "";
					while (users.next()) {
						if (users.getInt("idperson") == rs.getInt("idperson")) {
							userName = users.getString("nameUser");
							users.first();
							break;
						}
					}

					// evidencia.append("<a target=\"_blank\" href=\"")
					evidencia.append("<b>").append(userName).append(":</b>&nbsp;<a href=\"").append("javascript:showFileSacop('").append(evidenciaPrefix).append(idplanillasacopaccion).append("/")
							.append(userName).append("/").append(rs.getString("evidencia")).append("')\">").append(rs.getString("evidencia")).append("</a>")
							.append("<br />");
				}
			}
			rs.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			setFinally(con, pst);
		}

		if (evidencia.length() > 0) {
			// if (!ToolsHTML.isEmptyOrNull(evidencia)) {
			datosuseraccion.append(rb.getString("scp.evidencia") + ": &nbsp;").append(evidencia);
		}

		return datosuseraccion.toString();
	}

	/**
	 * 
	 * @param idplanillasacop1
	 * @return
	 */
	public static String obtenerDatosResponsablesAccionesSacop(String idplanillasacop1) {
		StringBuilder str = new StringBuilder("");
		try {
			Collection responsable = HandlerProcesosSacop.getInfResponsableAccion("idplanillasacop1", idplanillasacop1);
			Locale defaultLocale = new Locale(DesigeConf.getProperty("language.Default"), DesigeConf.getProperty("country.Default"));
			ResourceBundle rb = ResourceBundle.getBundle("LoginBundle", defaultLocale);
			// //System.out.println("idplanillasacop1="+idplanillasacop1);
			// Iterator it = responsable.iterator();
			// while (it.hasNext()) {
			for (Iterator it = responsable.iterator(); it.hasNext();) {
				PlantillaAccion obj = (PlantillaAccion) it.next();
				str.append(rb.getString("scp.accion")).append(":").append(" ").append(obj.getAccion()).append("<br>");
				str.append(rb.getString("scp.fecha"));
				str.append((" : "));
				if(obj.getFecha()!=null) {
					str.append(ToolsHTML.sdfShowWithoutHour.format(ToolsHTML.date.parse(obj.getFecha())));
				}
				str.append(" <br>");
				str.append(getInfAccionPorPersona(null, String.valueOf(obj.getIdplanillasacopaccion()),String.valueOf(obj.getIdplanillasacop1()) + "/"));
				str.append(" <br>  ").append(" <br>  ");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return str.toString();
	}

	/**
	 * 
	 * @return
	 * @throws Exception
	 */
	public static Hashtable loadDeleteSacops() throws Exception {

		Hashtable lista = new Hashtable();
		StringBuilder sql = new StringBuilder("SELECT id, idperson, idplanillasacop1  FROM  tbl_deletesacop").append(" WHERE active= '")
				.append(Constants.permission).append("'");

		System.out.println("SQL=" + sql.toString());

		lista = JDBCUtil.doQueryHash(sql.toString(), "idplanillasacop1");

		return lista;
	}

	/**
	 * 
	 * @param request
	 * @param user
	 * @param usuario
	 * @param verHistoricosTambien
	 * @return
	 */
	public static Collection searchUsuarioSacop(HttpServletRequest request, Users user, String usuario, boolean verHistoricosTambien) {
		String salida = "";
		Connection con = null;
		PreparedStatement stsql = null;
		ResultSet rs = null;
		Vector result = new Vector();
		try {
			boolean userIsLikeAnAdmin = PerfilAdministrador.userIsInAdminGroup(user);
			String extraFilters = "";
			if (request.getParameter("idSacop") != null && !"".equals(request.getParameter("idSacop"))) {
				extraFilters += "AND sacopnum LIKE UPPER('%" + request.getParameter("idSacop").toUpperCase() + "%') ";
			}
			
			// nuevo filtro
		    
			if (request.getParameter("tipoSacop") != null && !"".equals(request.getParameter("tipoSacop"))) {
				extraFilters += "AND sacopnum LIKE UPPER('%" + request.getParameter("tipoSacop").toUpperCase() + "%') ";
			}
			
			// filtro de origen
			
			if (request.getParameter("origenSacop") != null && !"".equals(request.getParameter("origenSacop"))) {
				extraFilters += "AND tit.titulosplanillas LIKE UPPER('%" + request.getParameter("origenSacop").toUpperCase() + "%') ";
			}
		
			if (request.getParameter("emisorSacop") != null && !"".equals(request.getParameter("emisorSacop"))) {
				extraFilters += "AND em.nameuser = '" + request.getParameter("emisorSacop") + "' ";
			}
			if (request.getParameter("responsableSacop") != null && !"".equals(request.getParameter("responsableSacop"))) {
				extraFilters += "AND re.nameuser = '" + request.getParameter("responsableSacop") + "' ";
			}
			if (request.getParameter("areaResponsableSacop") != null && !"".equals(request.getParameter("areaResponsableSacop"))) {
				extraFilters += "AND area.idarea = '" + request.getParameter("areaResponsableSacop") + "' ";
			}
			if (request.getParameter("efectivaSacop") != null && !"".equals(request.getParameter("efectivaSacop"))) {
				extraFilters += "AND eliminarcausaraiz = '" + request.getParameter("efectivaSacop") + "' ";
			}
			if (request.getParameter("estadoSacop") != null && !"".equals(request.getParameter("estadoSacop"))) {
				extraFilters += "AND ps1.estado = " + request.getParameter("estadoSacop") + " ";
			}
			/*
			if (request.getParameter("fechaEmisionSacop") != null && !"".equals(request.getParameter("fechaEmisionSacop"))) {
				String emision = ToolsHTML.date.format(ToolsHTML.sdfShowWithoutHour.parse(request.getParameter("fechaEmisionSacop")));
				String FechaIniMes = ToolsHTML.date(emision)
				String FechaFinMes = 
				System.out.println("uzcateguidarwin");
				System.out.println(emision);
				System.out.println(FechaIniMes);
				System.out.println(FechaFinMes);
				System.out.println("uzcateguidarwin");
				extraFilters += "AND ps1.fechaemision >= '" + emision + " 00:00:00' " ;
				extraFilters += "AND ps1.fechaemision <= '" + emision + " 23:59:59' " ;
			}
			*/
			if (request.getParameter("fechaEmisionSacop") != null && !"".equals(request.getParameter("fechaEmisionSacop"))) {
			    String emision = ToolsHTML.date.format(ToolsHTML.sdfShowWithoutHour.parse(request.getParameter("fechaEmisionSacop")));
			    
			    // Convertir la fecha a LocalDate
			    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
			    LocalDate fecha = LocalDate.parse(emision, formatter);
		    
			    // Obtener el primer y último día del mes
			    LocalDate inicioMes = fecha.with(TemporalAdjusters.firstDayOfMonth());
			    LocalDate finMes = fecha.with(TemporalAdjusters.lastDayOfMonth());
		    
			    // Convertir las fechas a String
			    String FechaIniMes = inicioMes.format(formatter);
			    String FechaFinMes = finMes.format(formatter);
		    
			    
			    extraFilters += "AND ps1.fechaemision >= '" + FechaIniMes + " 00:00:00' " ;
			    extraFilters += "AND ps1.fechaemision <= '" + FechaFinMes + " 23:59:59' " ;
		}
			
		if (request.getParameter("fechaEstimadaSacop") != null && !"".equals(request.getParameter("fechaEstimadaSacop"))) {
			    
				String estimada = ToolsHTML.date.format(ToolsHTML.sdfShowWithoutHour.parse(request.getParameter("fechaEstimadaSacop")));
			    
			    // Convertir la fecha a LocalDate
			    DateTimeFormatter formatterEstimada = DateTimeFormatter.ofPattern("yyyy-MM-dd");
			    LocalDate fechaEstimada = LocalDate.parse(estimada, formatterEstimada);
		    
			    // Obtener el primer y último día del mes
			    LocalDate inicioMesEstimado = fechaEstimada.with(TemporalAdjusters.firstDayOfMonth());
			    LocalDate finMesEstimado = fechaEstimada.with(TemporalAdjusters.lastDayOfMonth());
		    
			    // Convertir las fechas a String
			    String FechaIniMesEstimado = inicioMesEstimado.format(formatterEstimada);
			    String FechaFinMesEstimado = finMesEstimado.format(formatterEstimada);
		    
			    
			    extraFilters += "AND ps1.fechaestimada >= '" + FechaIniMesEstimado + " 00:00:00' " ;
			    extraFilters += "AND ps1.fechaestimada <= '" + FechaFinMesEstimado + " 23:59:59' " ;
		}


			con = JDBCUtil.getConnection(Thread.currentThread().getStackTrace()[1].getMethodName());
			StringBuilder sql = new StringBuilder("");
			sql.append("SELECT DISTINCT fecha_cierre,usernotificado, ps1.idplanillasacop1, ps1.estado, ps1.sacopnum, respblearea, emisor, solicitudinforma, tblaccionporpersona.idperson as responsables, tblacciones.idplanillasacopaccion, ps1.idplanillasacop1esqueleto, ps1.descripcion, ps1.noconformidades, ps1.fechaemision, ps1.origensacop, tit.titulosplanillas, ps1.usuarioSacops1 ");
			sql.append(",em.nombres AS enombre, em.apellidos AS eapellidos, re.nombres AS rnombre, re.apellidos AS rapellidos, area.area, ps1.estado, tit.titulosplanillas, ps1.fechaemision, ps1.descripcion, procesosafectados, eliminarcausaraiz, fechaWhenDiscovered, ps1.correcpreven , tbl_c.descripcion AS clasificacion, ps1.fechareal, ps1.noconformidades, ps1.accionesrecomendadas , ps1.fecha_verificacion, ps1.fecha_cierre ");
			sql.append("FROM tbl_planillasacop1 ps1, person em, person re, tbl_cargo cargo, tbl_area area, tbl_titulosplanillassacop tit, ");
			sql.append("(SELECT idplanillasacopaccion, idplanillasacop1 FROM tbl_planillasacopaccion) tblacciones, ");
			sql.append("(SELECT idperson, idplanillasacopaccion FROM tbl_sacopaccionporpersona) tblaccionporpersona, ");
			sql.append("tbl_clasificacionplanillassacop tbl_c ");
			sql.append("WHERE ps1.idplanillasacop1=tblacciones.idplanillasacop1 ");
			sql.append("AND ps1.origensacop=tit.numtitulosplanillas ");
			sql.append("AND tbl_c.id = ps1.clasificacion ");
			sql.append("AND ps1.active='").append(Constants.permission).append("' ");
			sql.append("AND tblaccionporpersona.idplanillasacopaccion=tblacciones.idplanillasacopaccion ");
			sql.append("AND em.idperson= emisor ");
			sql.append("AND re.idperson = respblearea ");
			sql.append("AND cargo.idarea = area.idarea ");
			sql.append("AND usernotificado = cargo.idcargo ");
			sql.append(extraFilters);

			if (!userIsLikeAnAdmin) {
				// como el usuario no pertenece al grupo de administradores,
				// solo podra ver las sacops en las que este relacionado
				log.info("El usuario " + user.getNameUser() + ", no es del grupo administrador, se le filtraran las sacops.");
				sql.append(" AND (em.idperson = ").append(user.getIdPerson());
				sql.append("    OR re.idperson = ").append(user.getIdPerson());
				sql.append("    OR solicitudinforma = '").append(user.getIdPerson()).append("' ");
				sql.append("    OR solicitudinforma LIKE '%,").append(user.getIdPerson()).append("' ");
				sql.append("    OR solicitudinforma LIKE '").append(user.getIdPerson()).append(",%' ");
				sql.append("    OR solicitudinforma LIKE '%,").append(user.getIdPerson()).append(",%') ");
			}

			sql.append(" UNION ");
			sql.append("SELECT fecha_cierre,usernotificado, idplanillasacop1, ps1.estado, ps1.sacopnum, respblearea, emisor, solicitudinforma, '0' as responsables, '0' as idplanillasacopaccion, ps1.idplanillasacop1esqueleto, ps1.descripcion, ps1.noconformidades, ps1.fechaemision, ps1.origensacop, tit.titulosplanillas, ps1.usuarioSacops1 "); 
			sql.append(",em.nombres AS enombre, em.apellidos AS eapellidos, re.nombres AS rnombre, re.apellidos AS rapellidos, area.area, ps1.estado, tit.titulosplanillas, ps1.fechaemision, ps1.descripcion, procesosafectados, eliminarcausaraiz, fechaWhenDiscovered, ps1.correcpreven , tbl_c.descripcion AS clasificacion, ps1.fechareal, ps1.noconformidades, ps1.accionesrecomendadas , ps1.fecha_verificacion, ps1.fecha_cierre ");
			sql.append("FROM tbl_planillasacop1 ps1 ");
			sql.append("LEFT JOIN tbl_clasificacionplanillassacop tbl_c ON tbl_c.id = ps1.clasificacion ");
			sql.append("LEFT JOIN person em ON em.idperson = ps1.emisor ");
			sql.append("LEFT JOIN person re ON re.idperson = ps1.respblearea ");
			sql.append("LEFT JOIN tbl_cargo cargo ON ps1.usernotificado = cargo.idcargo ");
			sql.append("LEFT JOIN tbl_area area ON area.idarea = cargo.idarea ");
			sql.append("LEFT JOIN tbl_titulosplanillassacop tit ON ps1.origensacop=tit.numtitulosplanillas ");
			sql.append("WHERE ps1.active='").append(Constants.permission).append("' ");
			sql.append(extraFilters);

			if (!userIsLikeAnAdmin) {
				// como el usuario no pertenece al grupo de administradores,
				// solo podra ver las sacops en las que este relacionado
				log.info("El usuario " + user.getNameUser() + ", no es del grupo administrador, se le filtraran las sacops.");
				sql.append(" AND (em.idperson = ").append(user.getIdPerson());
				sql.append("    OR re.idperson = ").append(user.getIdPerson());
				sql.append("    OR ps1.solicitudinforma = '").append(user.getIdPerson()).append("'");
				sql.append("    OR ps1.solicitudinforma LIKE '%,").append(user.getIdPerson()).append("'");
				sql.append("    OR ps1.solicitudinforma LIKE '").append(user.getIdPerson()).append(",%'");
				sql.append("    OR ps1.solicitudinforma LIKE '%,").append(user.getIdPerson()).append(",%') ");
			}

			// sql.append(" ORDER BY tbl_planillasacop1.sacopnum, tbl_planillasacop1.idplanillasacop1 DESC");
			switch (Constants.MANEJADOR_ACTUAL) {
			case Constants.MANEJADOR_MSSQL:
				sql.append(" ORDER BY ps1.sacopnum, ps1.idplanillasacop1  DESC");
				break;
			case Constants.MANEJADOR_POSTGRES:
				sql.append(" ORDER BY sacopnum, idplanillasacop1  DESC");
				break;
			case Constants.MANEJADOR_MYSQL:
				sql.append(" ORDER BY sacopnum, idplanillasacop1  DESC");
				break;
			}

			System.out.println("searchUsuarioSacop=" + sql.toString());

			request.getSession().setAttribute("LAST_QUERY_SEARCH_SACOP", sql.toString());
			stsql = con.prepareStatement(JDBCUtil.replaceCastMysql(sql.toString()));
			rs = stsql.executeQuery();
			String emisor = null;
			String solicitante = null;
			String respblearea = null;
			String usernotificado = null;
			String sacopnum;
			String estado;
			boolean soloBorrador = false;
			String planillasacop = null;
			Hashtable unicaPlanillaAccion = new Hashtable();
			Hashtable unicaPlanilla = new Hashtable();
			Hashtable listaDeleteSacops = null;

			if (rs != null) {

				// cargamos los usuario
	            Map<String,PersonTO> listPersons = HandlerProcesosSacop.listarPersonAlls();
	            // cargamos las areas
	            Map<String,AreaTO> listAreas = HandlerProcesosSacop.listarAreaAlls();
				// cargamos los cargos
	            Map<String,CargoTO> listCargos = HandlerProcesosSacop.listarCargoAlls();
				
				PersonTO personTO = null;

				// vamos a filtrar las sacops cerradas por fecha de vigencia
				boolean isValiditySacop = false;
				int validitySacopMonth = 0;
				int validitySacopType = 0;
				Date validityDate = null;

				// preguntamos si esta activo el campo de vigencia
				isValiditySacop = HandlerParameters.PARAMETROS.getValiditySacop()==1;
				validitySacopType = HandlerParameters.PARAMETROS.getValiditySacopType();
				validitySacopMonth =  HandlerParameters.PARAMETROS.getValiditySacopMonth();
				
				switch(validitySacopType) { 
				case HandlerParameters.validitySacopType0: // vigencia por cierre de ejercicio
					validityDate = finPeriodoAnterior(validitySacopMonth);
					break;
				case HandlerParameters.validitySacopType1: // vigencia por meses 
					validityDate = restarMeses(new Date(), validitySacopMonth);
					break;
				}

				String mismaPlanilla = "";
				
				while (rs.next()) {

					estado = rs.getString("estado") != null ? rs.getString("estado") : "";					
					if(estado!=null && estado.equals(LoadSacop.edoCerrado)) {
						if(isValiditySacop) {
							if(rs.getTimestamp("fecha_cierre")!=null) {
								Date fechaCierre = rs.getTimestamp("fecha_cierre");
								
								if(fechaCierre.getTime() < validityDate.getTime()) {
									continue;
								}
							}
						}
					}

					// cada planilla tiene su grupo de acciones que pertenecen a
					// esa planilla
					// se sacan los usuarios por cada una de las planillas y por
					// sus respectivas acciones de esa planilla
					planillasacop = rs.getString("idplanillasacop1") != null ? rs.getString("idplanillasacop1") : "";
					if (mismaPlanilla.equalsIgnoreCase(planillasacop)) {
						// es la misma planilla y continua
					} else {
						// es diferente planilla, borra los hijos d la planilla
						// anterior que son las acciones
						mismaPlanilla = planillasacop;
						Enumeration enumu = unicaPlanillaAccion.keys();
						while (enumu.hasMoreElements()) {
							String values = (String) enumu.nextElement();
							unicaPlanillaAccion.remove(values);
						}
					}
					if (!unicaPlanillaAccion.containsKey(rs.getString("idplanillasacopaccion"))) {
						boolean ejecutar = false;
						Plantilla1BD planilla2Sacop = new Plantilla1BD();
						Hashtable usuariosInformados = new Hashtable();
						StringTokenizer stk = new StringTokenizer(rs.getString("solicitudinforma"), ",");
						StringTokenizer stk2 = new StringTokenizer(rs.getString("responsables") != null ? rs.getString("responsables") : "", ",");
						emisor = rs.getString("emisor") != null ? rs.getString("emisor") : "";
						//solicitante = rs.getString("origensacop") != null ? rs.getString("origensacop") : "";
						solicitante = rs.getString("usuarioSacops1") != null ? rs.getString("usuarioSacops1") : "";
						usernotificado = rs.getString("usernotificado") != null ? rs.getString("usernotificado") : "";
						respblearea = rs.getString("respblearea") != null ? rs.getString("respblearea") : "";
						sacopnum = rs.getString("sacopnum") != null ? rs.getString("sacopnum") : "";
						estado = rs.getString("estado") != null ? rs.getString("estado") : "";
						// este id es para saber si iene de intouych o
						// auditorias

						String nombre = null;
						String cargo = null;
						// si es el responsable, el emisor o el
						// usuarionotificado, muestra la planilla con el sw
						// ejecutar
						// if ((usuario.equalsIgnoreCase(usernotificado)) ||
						// (usuario.equalsIgnoreCase(emisor)) ||
						// (usuario.equalsIgnoreCase(respblearea))){
						planilla2Sacop.setDescripcion(rs.getString("descripcion"));
						planilla2Sacop.setIsEmisor(false);
						planilla2Sacop.setIsResponsable(false);
						
						planilla2Sacop.setOrigensacop(rs.getInt("origensacop"));
						planilla2Sacop.setTitulosplanillas(rs.getString("titulosplanillas"));
						
						if ((usuario.equalsIgnoreCase(emisor)) || (usuario.equalsIgnoreCase(respblearea)) || userIsLikeAnAdmin) {
							ejecutar = true;
							// verificamos si es emisor o respoonsable para
							// aplicar en pantalla p`rincipalsacop1.jsp que tipo
							// de
							// eliminacion aplicara
							if (usuario.equalsIgnoreCase(emisor)) {
								planilla2Sacop.setIsEmisor(true);
							}
							if (usuario.equalsIgnoreCase(respblearea)) {
								planilla2Sacop.setIsResponsable(true);
							}
						} else {
							// en caso que el usuario logueado sea uno de los
							// usuarios solicitudinforma,mostrar el registro del
							// sacop
							boolean sw = false;
							while ((!sw) && (stk.hasMoreTokens())) {
								String idPerson = stk.nextToken();
								if (usuario.equalsIgnoreCase(idPerson)) {
									sw = true;
									ejecutar = true;
								}
							}
							if (!ejecutar) {
								// en caso que el usuario logueado sea uno de
								// los usuarios responsables
								while ((!sw) && (stk2.hasMoreTokens())) {
									String idPerson = stk2.nextToken();

									if (usuario.equalsIgnoreCase(idPerson)) {

										sw = true;
										ejecutar = true;
									}
								}
							}
						}
						// si la planilla es borrador, entonces para que se
						// pueda mostrar esa planilla
						// solo debe ser que el usuario logueado sea el emisor
						if (LoadSacop.edoBorrador.equalsIgnoreCase(estado)) {
							if (usuario.equalsIgnoreCase(emisor)) {
								ejecutar = true;
							} else {
								ejecutar = false;
							}
						}

						// el ejecutar puede que venga verdadero, pero si esa
						// planilla esta logicamente borrada, el
						// ejecutar se coloca en false para que no se vea
						DeleteSacop formaDelete = new DeleteSacop();
						formaDelete.setIdplanillasacop1(planillasacop);
						// ______________BUSCA SI ESTA ELIMINADO
						// LOGICAMENTE______________________________
						if (listaDeleteSacops == null) {
							listaDeleteSacops = HandlerProcesosSacop.loadDeleteSacops();
						}
						if (listaDeleteSacops.containsKey(formaDelete.getIdplanillasacop1())) {
							CaseInsensitiveProperties prop = (CaseInsensitiveProperties) listaDeleteSacops.get(formaDelete.getIdplanillasacop1());
							formaDelete.setIdplanillasacop1(prop.getProperty("idplanillasacop1"));
							formaDelete.setIdperson(prop.getProperty("idperson"));
						}

						// HandlerProcesosSacop.loadDeleteSacop(formaDelete);
						if (!verHistoricosTambien) {
							// INICIO si el usuario elimino esta planilla de su
							// listas de sacop, entonces no se mostrara
							if (!ToolsHTML.isEmptyOrNull(formaDelete.getIdperson())) {
								StringTokenizer stkeliminados = new StringTokenizer(formaDelete.getIdperson(), ",");
								boolean sw = false;
								while ((!sw) && (stkeliminados.hasMoreTokens())) {
									// se valida, si el usuario esta en la tabla
									// deletelogico,
									// entonces quiere decirque no quiere ver
									// esta planilla
									String idPerson = stkeliminados.nextToken();
									if (usuario.equalsIgnoreCase(idPerson)) {
										sw = true;
										ejecutar = false;
									}
								}
							}
							// FIN si el usuario elimino esta planilla de su
							// listas de sacop, entonces no se mostrara
						} else {
							// si se quieren ver los historicos...,se muestran
							// solo los historicosa,
							// mas no los borradores
							if (!ToolsHTML.isEmptyOrNull(formaDelete.getIdperson())) {
								StringTokenizer stkeliminados = new StringTokenizer(formaDelete.getIdperson(), ",");
								boolean sw = false;
								while ((!sw) && (stkeliminados.hasMoreTokens())) {
									String idPerson = stkeliminados.nextToken();
									if (usuario.equalsIgnoreCase(idPerson)) {
										if (LoadSacop.edoBorrador.equalsIgnoreCase(estado)) {
											sw = true;
											// ejecutar false es que no me lo
											// muestra, es decir, si viene de
											// arriba en true, aqui
											// se le da un parado.
											ejecutar = false;
										}
									}
								}
							}
						}

						if (ejecutar) {
							// unicaPlanillaAccion.put(rs.getString("idplanillasacopaccion"),rs.getString("idplanillasacopaccion"));
							// (!unicaPlanillaAccion.containsKey(rs.getString("idplanillasacopaccion")
							if (!unicaPlanilla.containsKey(planillasacop)) {
								boolean showCharge = false;
								String cargosacop = String.valueOf(HandlerParameters.PARAMETROS.getCargosacop());
								if (cargosacop.equalsIgnoreCase(Constants.typeNumByLocationVacio) || (ToolsHTML.isEmptyOrNull(cargosacop))) {
									showCharge = true;
								}

								unicaPlanilla.put(planillasacop, "usuarioLoad");
								planilla2Sacop.setSacopnum(sacopnum);

								planilla2Sacop.setIdplanillasacop1(planillasacop != null ? Long.parseLong(planillasacop) : 0);
								planilla2Sacop.setEstado(estado != null ? Integer.parseInt(estado) : 0);
								
								personTO = listPersons.get(String.valueOf(emisor));

								// datos del emisor
								if (!showCharge) {
									if (personTO != null) {
										nombre = personTO.getNombres() + " " + personTO.getApellidos();
										cargo = personTO.getCargo();
										planilla2Sacop.setMostrarCargo(false);
									}
								} else {
									if (personTO != null) {
										nombre = personTO.getNombres() + " " + personTO.getApellidos();
										cargo = personTO.getCargo();
										planilla2Sacop.setMostrarCargo(true);
									}
								}
								
								CargoTO oCargoTO = listCargos.get(cargo);
								AreaTO oAreaTO = listAreas.get(oCargoTO.getIdArea());

								planilla2Sacop.setEmisortxt(nombre);
								planilla2Sacop.setCargoEmisor(oCargoTO.getCargo() + "<br>(" + oAreaTO.getArea() + ")");
								
								personTO = listPersons.get(String.valueOf(respblearea));

								// datos del responsable
								if (!showCharge) {
									if (personTO != null) {
										nombre = personTO.getNombres() + " " + personTO.getApellidos();
										cargo = personTO.getCargo();
										planilla2Sacop.setMostrarCargo(false);
									}
								} else {
									if (personTO != null) {
										nombre = personTO.getNombres() + " " + personTO.getApellidos();
										cargo = personTO.getCargo();
										planilla2Sacop.setMostrarCargo(true);
									}
								}
								
								oCargoTO = listCargos.get(cargo);
								oAreaTO = listAreas.get(oCargoTO.getIdArea());

								planilla2Sacop.setRespbleareatxt(nombre);

								planilla2Sacop.setCargo(oCargoTO.getCargo() + "<br>(" + oAreaTO.getArea() + ")");
								planilla2Sacop.setEstadotxt(LoadSacop.obtenerEdoBorrador(String.valueOf(planilla2Sacop.getEstado())));
								planilla2Sacop.setEstado(estado != null ? Integer.parseInt(estado) : 0);

								
								// solicitante
								if(solicitante!=null && solicitante.trim().length()>0) {
									personTO = listPersons.get(String.valueOf(solicitante));
									
									if (!showCharge) {
										if (personTO != null) {
											nombre = personTO.getNombres() + " " + personTO.getApellidos();
											cargo = personTO.getCargo();
											planilla2Sacop.setMostrarCargo(false);
										}
									} else {
										if (personTO != null) {
											nombre = personTO.getNombres() + " " + personTO.getApellidos();
											cargo = personTO.getCargo();
											planilla2Sacop.setMostrarCargo(true);
										}
									}
									
									oCargoTO = listCargos.get(cargo);
									oAreaTO = listAreas.get(oCargoTO.getIdArea());

									planilla2Sacop.setSolicitantetxt(nombre);
									planilla2Sacop.setCargoSolicitante(oCargoTO.getCargo() + "<br>(" + oAreaTO.getArea() + ")");
								} else {
									planilla2Sacop.setSolicitantetxt("N/A");
									planilla2Sacop.setCargoSolicitante("N/A");
								}
								
								// ___________Sacop-Intouch_____________________________________________________________//

								int idplanillasacop1esqueleto = rs.getString("idplanillasacop1esqueleto") != null
										? Integer.parseInt(rs.getString("idplanillasacop1esqueleto")) : -1;
								planilla2Sacop.setIdplanillasacop1esqueleto(idplanillasacop1esqueleto);
								HandlerProcesosSacop handlerProcesosSacop = new HandlerProcesosSacop();
								handlerProcesosSacop.VerificarsiEsDeSacop_Intouch(planilla2Sacop);
								// por si acaso viene cero, la colocamos en -1
								if (0 == planilla2Sacop.getIdplanillasacop1esqueleto()) {
									planilla2Sacop.setIdplanillasacop1esqueleto(-1);
								}
								// ______________________________________________________________________________________________________//
								planilla2Sacop.setNoconformidades(rs.getString("noconformidades"));
								planilla2Sacop.setFechaemision(rs.getTimestamp("fechaemision"));
								result.add(planilla2Sacop);
							}
						}
					}
				} // end Hashtable
			} // end while
			rs.close();
			// liberamos memoria
			unicaPlanillaAccion = null;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			setFinally(con, stsql);
		}

		log.info("Obtenidas " + result.size() + " SACOPs segun filtros de busqueda");
		// System.out.println("result.size(): " + result.size());
		return result;
	}

	public static Date finPeriodoAnterior(int mes) {
		Calendar c = Calendar.getInstance();
		c.set(Calendar.DATE, 1);
		do {
			System.out.println(c.get(Calendar.MONTH));
			c.add(Calendar.MONTH, -1);
		} while(c.get(Calendar.MONTH)!=(mes-1));
		c.add(Calendar.MONTH,  1);
		c.add(Calendar.DATE, -1);
		return c.getTime();
	}

	public static Date restarMeses(Date fecha, int mes) {
		Calendar c = Calendar.getInstance();
		c.setTime(fecha);
		c.add(Calendar.MONTH, -1*mes);
		return c.getTime();
	}

	public static boolean isSacopOpenByNameUser(String nameUser) {
		StringBuilder sb = new StringBuilder();

		sb.append("SELECT a.idplanillasacop1, b.nameUser ");
		sb.append("FROM tbl_planillasacop1 a, person b "); 
		sb.append("WHERE a.respblearea = b.idPerson ");
		sb.append("AND a.estado!=").append(LoadSacop.edoCerrado).append(" ");
		sb.append("AND a.estado!=").append(LoadSacop.edoRechazado).append(" ");
		sb.append("AND b.nameUser = '").append(nameUser).append("' ");
		
		try {
			CachedRowSet crs = JDBCUtil.executeQuery(sb, Thread.currentThread().getStackTrace()[1].getMethodName());
			return crs.next();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return false;
	}

	public static void main(String[] args) {
		Date cierre = new Date(118,0,25);
		Date validity = finPeriodoAnterior(1);
		//System.out.println(finPeriodoAnterior(6));
		//System.out.println(cierre);
		System.out.println(validity);
		
		if(cierre.getTime() > validity.getTime()) {
			System.out.println("si");
		} else {
			System.out.println("no");
		}
		//System.out.println(restarMeses(new Date(),1));
	}
	
	

	
}
