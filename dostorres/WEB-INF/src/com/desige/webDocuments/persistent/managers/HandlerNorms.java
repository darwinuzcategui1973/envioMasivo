package com.desige.webDocuments.persistent.managers;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Vector;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.desige.webDocuments.accion.forms.PlanAudit;
import com.desige.webDocuments.accion.forms.ProgramAudit;
import com.desige.webDocuments.norms.forms.BaseNormsForm;
import com.desige.webDocuments.persistent.utils.JDBCUtil;
import com.desige.webDocuments.utils.ApplicationExceptionChecked;
import com.desige.webDocuments.utils.Constants;
import com.desige.webDocuments.utils.ToolsHTML;
import com.desige.webDocuments.utils.beans.Search;
import com.focus.qwds.ondemand.server.excepciones.ErrorDeAplicaqcion;
import com.focus.qweb.dao.PlanAuditDAO;
import com.focus.qweb.dao.ProgramAuditDAO;
import com.focus.qweb.to.PlanAuditTO;
import com.focus.qweb.to.ProgramAuditTO;
import com.sun.jna.Library.Handler;

import sun.jdbc.rowset.CachedRowSet;

/**
 * Title: HandlerNorms.java <br/>
 * Copyright: (c) 2004 Desige Servicios de Computaci�n<br/>
 * 
 * @author Ing. Nelson Crespo (NC)
 * @author Ing. Sim�n Rodrigu�z(SR)
 * @version WebDocuments v1.0 <br/>
 *          Changes: <br/>
 *          <ul>
 *          <li>17-08-2004 (NC) Creation</li>
 *          <li>01/07/2005 (SR) Se creo un procedimiento getAllNormas para
 *          obetner todas las normas</li>
 *          <li>20/04/2006 (SR) Se modifico variable para subir archivos...</li>
 *          <li>20/04/2006 (SR) Se remplazo properties por resultset, daban
 *          algunas veces error los properties (java.lang.OutOfMemoryError: Java
 *          heap space)...</li>
 *          <ul>
 */
public class HandlerNorms extends HandlerBD {
	/**
	 * 
	 */
	private static final long serialVersionUID = -2808841909987558075L;
	private static Logger log = LoggerFactory.getLogger(HandlerNorms.class.getName());

	public static final String NORMAS_DIR = "normas";

	public static final Map<String,String> NORMAS = new HashMap<String,String>(); 
	
	/**
	 * Retorna directorio dentro del repositorio donde se guardaran los archivos
	 * asociados a las normas
	 * 
	 * @return
	 */
	public static String getNormasPath() {
		String result = "";
		try {
			result = new StringBuilder(2048).append(ToolsHTML.getRepository())
					.append(File.separator).append(NORMAS_DIR).toString();
			File f = new File(result);
			if (!f.exists()) {
				f.mkdirs();
			}

		} catch (ErrorDeAplicaqcion e) {
			e.printStackTrace();
		}

		return result;
	}

	/**
	 * Guarda archivo a la carpeta de normas en el repositorio
	 * 
	 * @param fis
	 */
	private static void saveFile(String fileName, FileInputStream fis) {
		FileOutputStream out = null;
		try {
			String destino = new StringBuilder(2048).append(getNormasPath())
					.append(File.separator).append(fileName).toString();
			out = new FileOutputStream(new File(destino));
			IOUtils.copy(fis, out);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			IOUtils.closeQuietly(out);
		}
	}

	/**
	 * 
	 * @param descript
	 * @return
	 * @throws Exception
	 */
	public static Collection<Search> getAllNorms(String descript)
			throws Exception {
		Connection con = null;
		PreparedStatement st = null;
		ResultSet rs = null;
		ArrayList<Search> resp = new ArrayList<Search>();
		try {
			StringBuilder sql = new StringBuilder(
					"SELECT idNorm,titleNorm,indice,sistema_gestion,nameFile,documentRequired,auditProcess FROM norms WHERE active = '")
					.append(Constants.permission).append("'");
			if (ToolsHTML.checkValue(descript)) {
				sql.append(" AND titleNorm LIKE '%").append(descript)
						.append("%'");
			}
			//ydavila 001-00-003218 Las normas no se est�n ordenando en forma ascendente 
			//Por aqu� pasa cuando viene de Administraci�n-Ver Normas
	     	//sql.append(" ORDER BY LOWER(sistema_gestion), LOWER(indice), LOWER(titleNorm), idNorm ");
	          sql.append(" ORDER BY idnorm ");
						con = JDBCUtil.getConnection(Thread.currentThread().getStackTrace()[1].getMethodName());
			st = con.prepareStatement(JDBCUtil.replaceCastMysql(sql.toString()));
			rs = st.executeQuery();

			StringBuilder sb = new StringBuilder("");
			while (rs.next()) {
				sb.setLength(0);
				sb.append(rs.getString("sistema_gestion")).append(" ")
						.append(rs.getString("indice")).append("- ")
						.append(rs.getString("titleNorm"));

				Search bean = new Search(rs.getString("idNorm"), sb.toString());
				bean.setAditionalInfo(rs.getString("nameFile"));
				bean.setDocumentRequired(rs.getString("documentRequired"));
				bean.setAuditProcess(rs.getString("auditProcess"));
				bean.setIndice(rs.getString("indice"));
				resp.add(bean);
				
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			JDBCUtil.closeQuietly(con, st, rs);
		}
		return resp;
	}
	
	/**
	 * 
	 * @param descript
	 * @return
	 * @throws Exception
	 */
	public static Collection<Search> getAllNormsForAudit()
			throws Exception {
		Connection con = null;
		PreparedStatement st = null;
		ResultSet rs = null;
		ArrayList<Search> resp = new ArrayList<Search>();
		try {
			StringBuilder sql = new StringBuilder();
			sql.append("SELECT idNorm,titleNorm,indice,sistema_gestion,nameFile,documentRequired,auditProcess ")
				.append("FROM norms WHERE active = '")
				.append(Constants.permission).append("'")
				.append("AND auditProcess=1 ")
	          	.append(" ORDER by idnorm ");
			
			con = JDBCUtil.getConnection(Thread.currentThread().getStackTrace()[1].getMethodName());
			st = con.prepareStatement(JDBCUtil.replaceCastMysql(sql.toString()));
			rs = st.executeQuery();

			StringBuilder sb = new StringBuilder("");
			while (rs.next()) {
				sb.setLength(0);
				sb.append(rs.getString("sistema_gestion")).append(" ")
						.append(rs.getString("indice")).append("- ")
						.append(rs.getString("titleNorm"));

				Search bean = new Search(rs.getString("idNorm"), sb.toString());
			
				//Search bean = new Search(getNormasPrincipalesPlanDeLosProgramasAuditoria(con,"4001"),sb.toString());
				bean.setAditionalInfo(rs.getString("nameFile"));
				bean.setDocumentRequired(rs.getString("documentRequired"));
				bean.setAuditProcess(rs.getString("auditProcess"));
				bean.setIndice(rs.getString("indice"));
				bean.setActNumber(getNormasPrincipalesPlanDeLosProgramasAuditoria(con,rs.getString("idNorm")));
			
				resp.add(bean);
				
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			JDBCUtil.closeQuietly(con, st, rs);
		}
		System.out.println("************************resp**************************");
		System.out.println(resp);
		return resp;
	}
	

	/**
	 * 
	 * @param forma
	 * @param permission
	 * @throws Exception
	 */
	public static void load(BaseNormsForm forma, byte permission)
			throws Exception {
		Connection con = null;
		PreparedStatement st = null;
		ResultSet rs = null;
		try {
			StringBuilder sql = new StringBuilder(
					"SELECT sistema_gestion, indice, titleNorm,Description,documentRequired,auditProcess FROM norms")
					.append(" WHERE idNorm = ").append(forma.getId())
					.append(" AND active = '").append(permission).append("'");
			// Properties prop = JDBCUtil.doQueryOneRow(sql.toString(),Thread.currentThread().getStackTrace()[1].getMethodName());
			con = JDBCUtil.getConnection(Thread.currentThread().getStackTrace()[1].getMethodName());
			st = con.prepareStatement(JDBCUtil.replaceCastMysql(sql.toString()));
			rs = st.executeQuery();
			// if
			// ((prop!=null)&&prop.getProperty("isEmpty").equalsIgnoreCase("false")){
			if (rs.next()) {
				forma.setSistemaGestion(rs.getString("sistema_gestion"));
				forma.setIndice(rs.getString("indice"));
				forma.setName(rs.getString("titleNorm"));
				forma.setDescript(rs.getString("Description"));
				forma.setDocumentRequired(rs.getInt("documentRequired"));
				forma.setAuditProcess(rs.getInt("auditProcess"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			JDBCUtil.closeQuietly(con, st, rs);
		}
	}

	/**
	 * 
	 * @param forma
	 * @return
	 */
	public synchronized static boolean edit(BaseNormsForm forma) {
		PreparedStatement st = null;
		Connection con = null;
		try {
			StringBuilder edit = new StringBuilder(1024)
					.append("UPDATE norms")
					.append(" SET titleNorm=?, Description=?, nameFile=?, contextType=?")
					.append(",key_search=?,indice=?,sistema_gestion=?, documentRequired=?, auditProcess=? ")
					.append(" WHERE idNorm = ").append(forma.getId())
					.append(" AND active = '").append(Constants.permission)
					.append("'");
			con = JDBCUtil.getConnection(Thread.currentThread().getStackTrace()[1].getMethodName());
			con.setAutoCommit(false);
			st = con.prepareStatement(JDBCUtil.replaceCastMysql(edit.toString()));
			st.setString(1, forma.getName());
			st.setString(2, forma.getDescript());
			st.setString(3, forma.getFileNameFisico());
			st.setString(4, forma.getContextType());
			st.setString(5, forma.getKeySearch());
			st.setString(6, forma.getIndice());
			st.setString(7, forma.getSistemaGestion());
			st.setInt(8, forma.getDocumentRequired() );
			st.setInt(9, forma.getAuditProcess() );

			boolean swa = st.executeUpdate() > 0;
			if (swa) {
				File fichero = null;
				FileInputStream streamEntrada = null;
				String nameFile = null;
				if ((forma.getFileNameFisico() != null)
						&& (forma.getContextType() != null)
						&& (forma.getPath() != null)) {
					fichero = new File(forma.getPath() + File.separator
							+ forma.getFileNameFisico());
				} else {
					nameFile = "vacio.txt";
					FileOutputStream out = new FileOutputStream(forma.getPath()
							+ File.separator + nameFile);
					PrintStream p = new PrintStream(out);
					p.println("");
					p.close();
					fichero = new File(forma.getPath() + File.separator
							+ nameFile);
				}
				// Guarda archivo de Normas
				streamEntrada = new FileInputStream(fichero);
				saveFile(forma.getFileNameFisico(), streamEntrada);
				IOUtils.closeQuietly(streamEntrada);

				// INFO:ZIP
				// FileZip comprimido = new FileZip();
				// st.setBinaryStream(3, streamEntrada, (int) fichero.length());
				// st.setBinaryStream(3, comprimido.zip(streamEntrada),
				// comprimido.available());
				if ((forma.getFileNameFisico() != null)
						&& (forma.getContextType() != null)
						&& (forma.getPath() != null)) {
					streamEntrada.close();
					FileUtils.deleteQuietly(fichero);
					// comprimido.deleteFile(fichero);
				}
			}
			con.setAutoCommit(true);

			return swa;

		} catch (Exception ex) {
			log.error(ex.getMessage());
			ex.printStackTrace();
		} finally {
			setFinally(con, st);
		}
		return false;
	}

	/**
	 * 
	 * @param forma
	 * @return
	 */
	public synchronized static boolean insert(BaseNormsForm forma) {
		PreparedStatement st = null;
		Connection con = null;
		boolean result = false;
		try {
			StringBuilder insert = new StringBuilder("INSERT INTO norms");
			// if
			// ((forma.getFileNameFisico()!=null)&&(forma.getContextType()!=null)&&(forma.getPath()!=null)){
			insert.append(" (idNorm,titleNorm,Description,data,nameFile,contextType,key_search,indice,sistema_gestion,documentRequired,auditProcess) VALUES(?,?,?,?,?,?,?,?,?,?,?)");
			// }else{
			// insert.append(" (idNorm,titleNorm,Description) VALUES( ?, ? ,? )");
			// }
			con = JDBCUtil.getConnection(Thread.currentThread().getStackTrace()[1].getMethodName());
			con.setAutoCommit(false);
			// int num = IDDBFactorySql.getNextID("norms");
			int num = HandlerStruct.proximo("norms", "norms", "idNorm");
			st = con.prepareStatement(JDBCUtil.replaceCastMysql(insert.toString()));
			st.setInt(1, num);
			st.setString(2, forma.getName());
			st.setString(3, forma.getDescript());

			// INFO:ZIP
			// FileZip comprimido = new FileZip();
			// st.setBinaryStream(3, streamEntrada, (int) fichero.length());
			// st.setBinaryStream(3, comprimido.zip(streamEntrada),
			// comprimido.available());
			//st.setBinaryStream(3, null);
			st.setString(5, forma.getFileNameFisico());
			st.setString(6, forma.getContextType());
			st.setString(7, forma.getKeySearch());
			st.setString(8, forma.getIndice());
			st.setString(9, forma.getSistemaGestion());
			st.setInt(10, forma.getDocumentRequired());
			st.setInt(11, forma.getAuditProcess());

			result = st.executeUpdate() > 0;
			if (result) {
				// Gaurda archivo de Normas
				File fichero = null;
				FileInputStream streamEntrada = null;
				String nameFile = null;
				if ((forma.getFileNameFisico() != null)
						&& (forma.getContextType() != null)
						&& (forma.getPath() != null)) {
					fichero = new File(forma.getPath() + File.separator
							+ forma.getFileNameFisico());
				} else {
					nameFile = "vacio.txt";
					FileOutputStream out = new FileOutputStream(forma.getPath()
							+ File.separator + nameFile);
					PrintStream p = new PrintStream(out);
					p.println("");
					p.close();
					fichero = new File(forma.getPath() + File.separator
							+ nameFile);
				}

				streamEntrada = new FileInputStream(fichero);
				saveFile(forma.getFileNameFisico(), streamEntrada);
				IOUtils.closeQuietly(streamEntrada);
			}
			con.setAutoCommit(true);
			// comprimido.deleteFile(fichero);
		} catch (Exception ex) {
			log.error(ex.getMessage());
			ex.printStackTrace();
		} finally {
			setFinally(con, st);
		}
		return result;
	}

	/**
	 * 
	 * @param forma
	 * @return
	 * @throws ApplicationExceptionChecked
	 */
	public synchronized static boolean delete(BaseNormsForm forma)
			throws ApplicationExceptionChecked {
		try {
			if (!HandlerDocuments.isDocumentNorms(forma.getId())) {
				StringBuilder delete = new StringBuilder(
						"UPDATE norms SET active = '0' WHERE idNorm = ")
						.append(forma.getId());
				// StringBuilder delete = new
				// StringBuilder("DELETE FROM norms WHERE idNorm = ").append(forma.getId());
				return JDBCUtil.doUpdate(delete.toString()) > 0;
			} else {
				throw new ApplicationExceptionChecked("E0023");
			}
		} catch (ApplicationExceptionChecked ae) {
			log.error(ae.getMessage());
			throw new ApplicationExceptionChecked("E0023");
		} catch (Exception e) {
			log.error(e.getMessage());
			e.printStackTrace();
		}
		return false;
	}


	public synchronized static boolean delete(PlanAudit forma)
			throws ApplicationExceptionChecked {
		try {
			StringBuilder delete = new StringBuilder("DELETE FROM planaudit WHERE idPlanAudit = ").append(forma.getId());
			return JDBCUtil.doUpdate(delete.toString()) > 0;
		} catch (ApplicationExceptionChecked ae) {
			log.error(ae.getMessage());
			throw new ApplicationExceptionChecked("E0023");
		} catch (Exception e) {
			log.error(e.getMessage());
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * 
	 * @param forma
	 * @return
	 * @throws ApplicationExceptionChecked
	 */
	public synchronized static boolean documentRequired(BaseNormsForm forma)
			throws ApplicationExceptionChecked {
		try {
			StringBuilder query = new StringBuilder("UPDATE norms SET documentRequired = ").append(forma.getDocumentRequired());
			query.append(" WHERE idNorm = ").append(forma.getId());

			return JDBCUtil.doUpdate(query.toString()) > 0;
		} catch (ApplicationExceptionChecked ae) {
			log.error(ae.getMessage());
			throw new ApplicationExceptionChecked("E0023");
		} catch (Exception e) {
			log.error(e.getMessage());
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * 
	 * @param forma
	 * @return
	 * @throws ApplicationExceptionChecked
	 */
	public synchronized static boolean auditProcess(BaseNormsForm forma)
			throws ApplicationExceptionChecked {
		try {
			StringBuilder query = new StringBuilder("UPDATE norms SET auditProcess = ").append(forma.getAuditProcess());
			query.append(" WHERE idNorm = ").append(forma.getId());

			return JDBCUtil.doUpdate(query.toString()) > 0;
		} catch (ApplicationExceptionChecked ae) {
			log.error(ae.getMessage());
			throw new ApplicationExceptionChecked("E0023");
		} catch (Exception e) {
			log.error(e.getMessage());
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * 
	 * @return
	 * @throws Exception
	 */
	public static Collection<Search> getSelectedNorms(String normList,
			boolean inFlag) throws Exception {
		Connection con = null;
		PreparedStatement st = null;
		ResultSet rs = null;

		ArrayList<Search> resp = new ArrayList<Search>();

		try {
			StringBuilder sb = new StringBuilder(2048)
					.append("SELECT idNorm,sistema_gestion,indice,titleNorm,documentRequired,auditProcess ")
					.append("FROM norms ").append("WHERE active = '")
					.append(String.valueOf(Constants.permission)).append("' ");
			if (normList.length() > 0) {
				sb.append("AND idNorm ").append((!inFlag) ? "NOT " : "")
						.append(" IN (").append(normList).append(") ");
			} else {
				if (inFlag) {
					sb.append("AND 1=2 ");
				}
			}
//
			//ydavila 001-00-003218 Las normas no se est�n ordenando en forma ascendente
			// pasa por aqu� cuando se asgina requisitos el documento (creaci�n y Propiedades)
			//sb.append("ORDER BY lower(sistema_gestion),lower(indice)"); // ,lower(titleNorm),
	          sb.append(" ORDER bY idnorm ");
			con = JDBCUtil.getConnection(Thread.currentThread().getStackTrace()[1].getMethodName());
			st = con.prepareStatement(JDBCUtil.replaceCastMysql(sb.toString()));
			rs = st.executeQuery();

			while (rs.next()) {
				// Properties properties = (Properties) datos.elementAt(row);
				if ((!ToolsHTML.isEmptyOrNull(rs.getString("idNorm")))
						&& (!ToolsHTML.isEmptyOrNull(rs.getString("titleNorm")))) {

					String id = rs.getString("idNorm") != null ? rs
							.getString("idNorm") : "";
					sb.setLength(0);
					sb.append("");
					if (rs.getString("titleNorm") != null) {
						sb.append(rs.getString("sistema_gestion")).append(" ")
								.append(rs.getString("indice")).append("- ")
								.append(rs.getString("titleNorm"));
								//.append(rs.getString("documentRequired"));
								//.append(rs.getString("auditProcess"));
					}

					Search bean = new Search(id, sb.toString());
					resp.add(bean);
				}
			}
		} catch (Exception e) {
			log.error("Error: " + e.getLocalizedMessage(), e);
		} finally {
			JDBCUtil.closeQuietly(con, st, rs);
		}

		return resp;
	}

	/**
	 * 
	 * @return
	 * @throws Exception
	 */
	public static Collection<Search> getAllNorms() throws Exception {
		Connection con = null;
		PreparedStatement st = null;
		ResultSet rs = null;
		ArrayList<Search> resp = new ArrayList<Search>();

		try {
			StringBuilder sb = new StringBuilder(2048)
					.append("SELECT idNorm,sistema_gestion,indice,titleNorm,documentRequired,auditProcess ")
					.append("FROM norms ")
					.append("WHERE active = '")
					.append(String.valueOf(Constants.permission))
					//ydavila 001-00-003218 Las normas no se est�n ordenando en forma ascendente
					//Por aqu� pasa cuando viene de Lista Maestra, Buscar y Creaci�n de Documento 
					//.append("' ORDER BY lower(sistema_gestion),lower(indice),lower(titleNorm), idNorm");
			        .append("' ORDER BY (idNorm)");
			con = JDBCUtil.getConnection(Thread.currentThread().getStackTrace()[1].getMethodName());
			st = con.prepareStatement(JDBCUtil.replaceCastMysql(sb.toString()));
			rs = st.executeQuery();

			while (rs.next()) {
				// Properties properties = (Properties) datos.elementAt(row);
				if ((!ToolsHTML.isEmptyOrNull(rs.getString("idNorm")))
						&& (!ToolsHTML.isEmptyOrNull(rs.getString("titleNorm")))) {

					String id = rs.getString("idNorm") != null ? rs
							.getString("idNorm") : "";
					sb.setLength(0);
					sb.append("");
					if (rs.getString("titleNorm") != null) {
						sb.append(rs.getString("sistema_gestion")).append(" ")
								.append(rs.getString("indice")).append("- ")
								.append(rs.getString("titleNorm"));
					}

					Search bean = new Search(id, sb.toString());
					resp.add(bean);
				}
			}
		} catch (Exception e) {
			log.error("Error: " + e.getLocalizedMessage(), e);
		} finally {
			JDBCUtil.closeQuietly(con, st, rs);
		}

		return resp;
	}

	// SIMON 01 DE JULIO 2005 INICIO
	/**
	 * 
	 * @return
	 * @throws Exception
	 */
	public static Hashtable<String, Search> getAllNormas(Connection con) throws Exception {
		StringBuilder query = new StringBuilder(1024)
				.append("SELECT idNorm, titleNorm, documentRequired,auditProcess ").append("FROM norms ")
				.append("WHERE active = '")
				.append(String.valueOf(Constants.permission))
				.append("' ORDER BY lower(titleNorm), idNorm");
		Vector<Properties> datos = JDBCUtil.doQueryVector(con, query.toString(),Thread.currentThread().getStackTrace()[1].getMethodName());
		Hashtable<String, Search> result = new Hashtable<String, Search>();
		for (int row = 0; row < datos.size(); row++) {
			Properties properties = (Properties) datos.elementAt(row);
			Search bean = new Search(properties.getProperty("idNorm"),
					properties.getProperty("titleNorm"),properties.getProperty("documentRequired"));
			bean.setAuditProcess(properties.getProperty("auditProcess"));
			result.put(bean.getId(), bean);
		}
		return result;
	}

	/**
	 * 
	 * @return
	 * @throws Exception
	 */
	public static Hashtable<String, Search> getAllNormasPrincipales(Connection con) throws Exception {
		StringBuilder query = new StringBuilder(1024)
				.append("SELECT idNorm, titleNorm, documentRequired, sistema_gestion, auditProcess ").append("FROM norms ")
				.append("WHERE active = '").append(String.valueOf(Constants.permission)).append("' ")
				.append("AND indice = '0' ")
				.append("ORDER BY lower(titleNorm)");
		Vector<Properties> datos = JDBCUtil.doQueryVector(con, query.toString(),Thread.currentThread().getStackTrace()[1].getMethodName());
		Hashtable<String, Search> result = new Hashtable<String, Search>();
		for (int row = 0; row < datos.size(); row++) {
			Properties properties = (Properties) datos.elementAt(row);
			Search bean = new Search(properties.getProperty("idNorm"),
					properties.getProperty("titleNorm"),properties.getProperty("documentRequired"));
			bean.setField3(properties.getProperty("sistema_gestion"));
			bean.setAuditProcess(properties.getProperty("auditProcess"));
			result.put(bean.getId(), bean);
		}
		return result;
	}

	/**
	 * 
	 * @return
	 * @throws Exception
	 */
	public static Hashtable<String, Search> getAllNormasPrincipalesProgram(Connection con, String idNorms) throws Exception {
		String normaGuiaAudit = String.valueOf(HandlerParameters.PARAMETROS.getIdNormAudit());
		StringBuilder query = new StringBuilder(1024);
		query.append("SELECT idNorm, titleNorm, documentRequired, sistema_gestion, auditProcess ").append("FROM norms  ");
		query.append("WHERE active = '").append(String.valueOf(Constants.permission)).append("' ");
		query.append("AND indice = '0' ");
		query.append("AND idNorm < ").append(String.valueOf(normaGuiaAudit)).append(" "); // menor que la normas guia
		//query.append("AND idNorm < 19001 "); // menor que la normas guia
		if(idNorms!=null && idNorms.length()>0) {
			query.append("AND idNorm IN (").append(idNorms).append(") ");
		}
		query.append("ORDER BY lower(titleNorm)");

		Vector<Properties> datos = JDBCUtil.doQueryVector(con, query.toString(),Thread.currentThread().getStackTrace()[1].getMethodName());
		Hashtable<String, Search> result = new Hashtable<String, Search>();
		for (int row = 0; row < datos.size(); row++) {
			Properties properties = (Properties) datos.elementAt(row);
			Search bean = new Search(properties.getProperty("idNorm"),
					properties.getProperty("titleNorm"),properties.getProperty("documentRequired"));
			bean.setField3(properties.getProperty("sistema_gestion"));
			bean.setAuditProcess(properties.getProperty("auditProcess"));
			result.put(bean.getId(), bean);
		}
		return result;
	}

	/**
	 * 
	 * @return
	 * @throws Exception
	 */
	public static Hashtable<String, Search> getAllNormasPrincipalesProgramDetalle(Connection con, String idNorms) throws Exception {
		StringBuilder query = new StringBuilder(0);
		query.append("SELECT sistema_gestion FROM norms  ");
		if(idNorms!=null && idNorms.length()>0) {
			query.append("WHERE idNorm IN (").append(idNorms).append(") ");
		}
		CachedRowSet crs = JDBCUtil.executeQuery(query, Thread.currentThread().getStackTrace()[1].getMethodName());
		
		StringBuilder sistemaGestion = new StringBuilder();
		String sep = "";
		String coma = ",";
		while(crs.next()) {
			sistemaGestion.append(sep).append("'").append(crs.getString(1)).append("'");
			sep = coma;
		}
		
		query = new StringBuilder(0);
		query.append("SELECT idNorm, titleNorm, documentRequired,auditProcess, sistema_gestion, indice ");
		query.append("FROM norms ");
		query.append("WHERE active = '").append(String.valueOf(Constants.permission)).append("' ");
		query.append("AND indice != '0' ");
		query.append("AND sistema_gestion IN (").append(sistemaGestion.toString()).append(") ");
		query.append("ORDER BY sistema_gestion, idNorm, lower(titleNorm) ");
		Vector<Properties> datos = JDBCUtil.doQueryVector(con, query.toString(),Thread.currentThread().getStackTrace()[1].getMethodName());
		Hashtable<String, Search> result = new Hashtable<String, Search>();
		for (int row = 0; row < datos.size(); row++) {
			Properties properties = (Properties) datos.elementAt(row);
			Search bean = new Search(properties.getProperty("idNorm"),
					properties.getProperty("indice").concat(" - ").concat(properties.getProperty("titleNorm")),properties.getProperty("documentRequired"));
			bean.setField3(properties.getProperty("sistema_gestion"));
			bean.setAuditProcess(properties.getProperty("auditProcess"));
			result.put(bean.getId(), bean);
		}
		return result;
	}
	
	
	public static void loadMasterNorms() throws Exception {	
		log.info("CARGANDO MAESTRO DE NORMAS ****");
		
		StringBuilder sb = new StringBuilder("");

		sb = new StringBuilder("SELECT idNorm, sistema_gestion, indice, titleNorm FROM norms WHERE active = '");
		sb.append(Constants.permission).append("'");
		
		Vector<Properties> props = JDBCUtil.doQueryVector(sb.toString(),Thread.currentThread().getStackTrace()[1].getMethodName());
		sb.setLength(0);
		for (Properties norma : props) {
			sb.setLength(0);
			sb.append(norma.getProperty("sistema_gestion")).append(" ");
			sb.append(norma.getProperty("indice")).append("- ");
			sb.append(norma.getProperty("titleNorm"));
			sb.append("<br />");
			HandlerNorms.NORMAS.put(norma.getProperty("idNorm"), sb.toString());
		}
		
	}

	// SIMON 01 DE JULIO 2005 FIN
	/**
	 * Este metodo permite la carga de la Descripcion de la Norma ISO
	 * 
	 * @param id
	 * @return
	 * @throws Exception
	 */

	public static String getDescriptNormIso(String id) throws Exception {
		if (!ToolsHTML.isEmptyOrNull(id)) {
			if(id.contains(",")) {
				String[] ids = id.split(",");
				StringBuilder sb = new StringBuilder();
				for(int i=0; i<ids.length; i++ ) {
					if(HandlerNorms.NORMAS.containsKey(ids[i])) {
						sb.append(HandlerNorms.NORMAS.get(ids[i]));
					}
				}
				return sb.toString();
			} else {
				if(HandlerNorms.NORMAS.containsKey(id)) {
					return HandlerNorms.NORMAS.get(id);
				}
			}
		}

		return "";
	}

	/**
	 * 
	 * @param id
	 * @return
	 * @throws Exception
	 */
	public static String getFileNormIso(String id) throws Exception {
		if (ToolsHTML.isEmptyOrNull(id)) {
			return "";
		}
		StringBuilder sql = new StringBuilder(
				"SELECT nameFile FROM norms WHERE idNorm IN(").append(id)
				.append(") AND active = '").append(Constants.permission)
				.append("'");
		Properties prop = JDBCUtil.doQueryOneRow(sql.toString(),Thread.currentThread().getStackTrace()[1].getMethodName());
		if (prop.getProperty("isEmpty").equalsIgnoreCase("false")) {
			return prop.getProperty("nameFile");
		}
		return null;
	}

	/**
	 * 
	 * @param idNorm
	 * @return
	 */
	public static List<String> getNormsBySistemaGestion(String idNorm) {
		return getNormsBySistemaGestion(null, idNorm);
	}
	
	public static List<String> getNormsBySistemaGestion(Connection conn, String idNorm) {
		PreparedStatement pst = null;
		ResultSet rs = null;
		List<String> result = new ArrayList<String>();
		boolean isNewConnect = false;
		try {
			// Chequea si la norma es cabecera
			StringBuilder sql = new StringBuilder(
					"SELECT sistema_gestion, indice FROM norms WHERE idnorm=")
					.append(idNorm);
			
			if(conn==null) {
				conn = JDBCUtil.getConnection(Thread.currentThread().getStackTrace()[1].getMethodName());
				isNewConnect = true; 
			}
			
			pst = conn.prepareStatement(JDBCUtil.replaceCastMysql(sql.toString()));
			rs = pst.executeQuery();
			String sistemaGestion = "";
			if (rs.next()) {
				if ("0".equals(rs.getString("indice").trim())) {
					sistemaGestion = rs.getString("sistema_gestion").trim();
				}
			}
			//ydavila puse el cierre de conexi�n en el finally
			//JDBCUtil.closeQuietly(pst, rs);

			if (sistemaGestion.length() > 0) {
				sql.setLength(0);
				sql.append("SELECT idnorm FROM norms WHERE sistema_gestion='")
						.append(sistemaGestion).append("' AND idnorm <>")
						.append(idNorm).append(" ORDER BY idnorm");
				pst = conn.prepareStatement(JDBCUtil.replaceCastMysql(sql.toString()));
				rs = pst.executeQuery();
							while (rs.next()) {
					result.add(String.valueOf(rs.getInt("idnorm")));
							}
			}			
		} catch (Exception e) {

		} finally {
			if(isNewConnect) {
				JDBCUtil.closeQuietly(conn, pst, rs);
			}
		}

		return result;

	}
	
	
	public static List<String> getIndiceBySistemaGestion(Connection conn, String idNorm) {
		PreparedStatement pst = null;
		ResultSet rs = null;
		List<String> result = new ArrayList<String>();
		boolean isNewConnect = false;
		try {
			// Chequea si la norma es cabecera
			StringBuilder sql = new StringBuilder(
					"SELECT indice FROM norms WHERE idnorm=")
					.append(idNorm);
			if(conn==null) {
				conn = JDBCUtil.getConnection(Thread.currentThread().getStackTrace()[1].getMethodName());
				isNewConnect = true;
			}
			pst = conn.prepareStatement(JDBCUtil.replaceCastMysql(sql.toString()));
			rs = pst.executeQuery();
			String indice = "";
			if (rs.next()) {
				if ("0".equals(rs.getString("indice").trim())) {
					result.add(String.valueOf(rs.getInt("indice")));
				}
			}		
		} catch (Exception e) {

		} finally {
			if(isNewConnect) {
				JDBCUtil.closeQuietly(conn, pst, rs);
			}
		}

		return result;

	}

	/**
	 * M�todo para obtener el id y la descripci�n de todas las normas existentes
	 * en el sistema
	 * 
	 * @return
	 * @throws Exception
	 */
	public static Hashtable<String, Search> getDescriptNorms() throws Exception {
		Hashtable<String, Search> result = new Hashtable<String, Search>();
		StringBuilder sql = new StringBuilder(
				"SELECT idNorm,titleNorm FROM norms")
				.append(" WHERE active = '").append(Constants.permission)
				.append("' ORDER BY lower(titleNorm), idNorm ");
		Properties prop = JDBCUtil.doQueryOneRow(sql.toString(),Thread.currentThread().getStackTrace()[1].getMethodName());
		if (prop.getProperty("isEmpty").equalsIgnoreCase("false")) {
			Search bean = new Search(prop.getProperty("idNorm").trim(),
					prop.getProperty("titleNorm"));
			result.put(bean.getId(), bean);
		}
		return result;
	}

	public static String getCantNorms(String normISO) {
		// TODO Auto-generated method stub
		
			Connection conn = null;
			PreparedStatement pst = null;
			ResultSet rs = null;
			String result = "";
			try {
				StringBuilder sql = new StringBuilder(
						"SELECT count() FROM norms WHERE idnorm=").append(normISO);
				conn = JDBCUtil.getConnection(Thread.currentThread().getStackTrace()[1].getMethodName());
				pst = conn.prepareStatement(JDBCUtil.replaceCastMysql(sql.toString()));
				rs = pst.executeQuery();
				
							
			} catch (Exception e) {

			} finally {
				JDBCUtil.closeQuietly(null, pst, rs);
			}

			return result;
		

	}

	/**
	 * 
	 * @param forma
	 * @throws Exception
	 */
	public static void load(ProgramAudit forma) throws Exception {
		ProgramAuditTO oProgramAuditTO = new ProgramAuditTO();
		ProgramAuditDAO oProgramAuditDAO = new ProgramAuditDAO();
		
		oProgramAuditTO.setIdProgramAudit(String.valueOf(forma.getIdProgramAudit()));
		
		if(oProgramAuditDAO.cargar(oProgramAuditTO)) {
			forma.load(oProgramAuditTO);
		}
	}

	public static void load(PlanAudit forma) throws Exception {
		PlanAuditTO oPlanAuditTO = new PlanAuditTO();
		PlanAuditDAO oPlanAuditDAO = new PlanAuditDAO();
		
		oPlanAuditTO.setIdPlanAudit(String.valueOf(forma.getIdPlanAudit()));
		
		if(oPlanAuditDAO.cargar(oPlanAuditTO)) {
			forma.load(oPlanAuditTO);
		}
	}
	

	public static Collection getPlanAudit(Long idProgramAudit) {
		Vector result = new Vector();
		List lista = null;

		PlanAuditDAO oPlanAuditDAO = new PlanAuditDAO();

		Map<String, Search> listaNorma = null;
		Search beanNorma = null;
		try {
			listaNorma = HandlerNorms.getAllNormas(null);
		} catch (Exception e1) {
			e1.printStackTrace();
		}

		try {
			lista = oPlanAuditDAO.listarPlanAuditAlls(idProgramAudit);

			for (Iterator iter = lista.iterator(); iter.hasNext();) {
				PlanAuditTO planAudit = (PlanAuditTO) iter.next();
				Search bean = new Search( String.valueOf(planAudit.getIdPlanAudit()) , planAudit.getNamePlan());
				bean.setField1(ToolsHTML.formatDateShow(planAudit.getDateFromPlan(),false));
				bean.setField2(ToolsHTML.formatDateShow(planAudit.getDateUntilPlan(),false));
				bean.setField3(getDescriptNormIso(planAudit.getIdNorm()));
				bean.setField4(planAudit.getStatusPlan());
				
				// buscaremos los sitemas de gestions para no dejar solo ids de tabla
				if(listaNorma!=null && planAudit.getIdNormPlanCheck()!=null && !planAudit.getIdNormPlanCheck().trim().equals("")) {
					String[] ls = planAudit.getIdNormPlanCheck().split(",");
					String sep = "";
					String coma = " / ";
					StringBuilder sb = new StringBuilder();
					for(int i=0; i < ls.length; i++) {
						beanNorma = listaNorma.get(ls[i]);
						sb.append(sep).append(beanNorma.getDescript());
						sep = coma;
					}
					bean.setField3(sb.toString());
				}
				
				result.add(bean);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return result;
	}
	
	public static Collection getProgramAudit(String name) {
		Vector result = new Vector();
		List lista = null;

		ProgramAuditDAO oProgramAuditDAO = new ProgramAuditDAO();

		Map<String, Search> listaNorma = null;
		Search beanNorma = null;
		try {
			listaNorma = HandlerNorms.getAllNormasPrincipales(null);
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		
		try {
			lista = oProgramAuditDAO.listarProgramAuditAlls(name);

			for (Iterator iter = lista.iterator(); iter.hasNext();) {
				ProgramAuditTO programAudit = (ProgramAuditTO) iter.next();
				Search bean = new Search( String.valueOf(programAudit.getIdProgramAudit()) , programAudit.getNameProgram());
				bean.setField1(ToolsHTML.formatDateShow(programAudit.getDateFrom(),false));
				bean.setField2(ToolsHTML.formatDateShow(programAudit.getDateUntil(),false));
				bean.setField3(programAudit.getIdNormCheck());
				bean.setField4(programAudit.getStatus());
				
				// buscaremos los sitemas de gestions para no dejar solo ids de tabla
				if(listaNorma!=null && programAudit.getIdNormCheck()!=null && !programAudit.getIdNormCheck().trim().equals("")) {
					String[] ls = programAudit.getIdNormCheck().split(",");
					String sep = "";
					String coma = " / ";
					StringBuilder sb = new StringBuilder();
					for(int i=0; i < ls.length; i++) {
						beanNorma = listaNorma.get(ls[i]);
						sb.append(sep).append(beanNorma.getField3());
						sep = coma;
					}
					bean.setField3(sb.toString());
				}
				
				result.add(bean);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return result;
	}

	/**
	 * 
	 * @param forma
	 * @return
	 */
	public static boolean insert(ProgramAudit forma) {
		try {
			ProgramAuditDAO oProgramAuditDAO = new ProgramAuditDAO();
			
			forma.setDateCreated(forma.getDateCreated()==null || forma.getDateCreated().equals("")?ToolsHTML.date.format(new Date()):forma.getDateCreated());
			
			ProgramAuditTO oProgramAuditTO = new ProgramAuditTO(forma);
			
			oProgramAuditDAO.insertar(oProgramAuditTO);
			
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	public static boolean insert(PlanAudit forma) {
		try {
			PlanAuditDAO oPlanAuditDAO = new PlanAuditDAO();
			
			forma.setDateCreatedPlan(forma.getDateCreatedPlan()==null || forma.getDateCreatedPlan().equals("")?ToolsHTML.date.format(new Date()):forma.getDateCreatedPlan());
			
			PlanAuditTO oPlanAuditTO = new PlanAuditTO(forma);
			
			oPlanAuditDAO.insertar(oPlanAuditTO);
			
			// cambiamos el estado del programa
			ProgramAuditTO oProgramAuditTO = new ProgramAuditTO();
			oProgramAuditTO.setIdProgramAudit(String.valueOf(forma.getIdProgramAudit()));
			
			ProgramAuditDAO oProgramAuditDAO = new ProgramAuditDAO();
			if(oProgramAuditDAO.cargar(oProgramAuditTO)) {
				if(oProgramAuditTO.getStatus().equals("P") || oProgramAuditTO.getStatus().equals("B")) {
					oProgramAuditTO.setStatus("A");
					oProgramAuditDAO.actualizar(oProgramAuditTO);
				}
			}
			
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	

	public static Collection<Search>  getAllNormasPrincipalesPlanDeLosProgramasAuditoria(Connection con, String idNorms) throws Exception {
		System.err.println("getAllNormasPrincipalesPlanDeLosProgramasAuditoria");
		
		StringBuilder query = new StringBuilder(0);
		query.append("SELECT sistema_gestion FROM norms  ");
		if(idNorms!=null && idNorms.length()>0) {
			query.append("WHERE idNorm IN (").append(idNorms).append(") ");
		}
		CachedRowSet crs = JDBCUtil.executeQuery(query, Thread.currentThread().getStackTrace()[1].getMethodName());
		
		StringBuilder sistemaGestion = new StringBuilder();
		String sep = "";
		String coma = ",";
		while(crs.next()) {
			sistemaGestion.append(sep).append("'").append(crs.getString(1)).append("'");
			sep = coma;
		}
		
		query = new StringBuilder(0);
		query.append("SELECT idNorm, titleNorm, documentRequired,auditProcess, sistema_gestion, indice ");
		query.append("FROM norms ");
		query.append("WHERE active = '").append(String.valueOf(Constants.permission)).append("' ");
		query.append("AND indice = '0' ");
		query.append("AND sistema_gestion IN (").append(sistemaGestion.toString()).append(") ");
		query.append("ORDER BY sistema_gestion, idNorm, lower(titleNorm) ");
		Vector<Properties> datos = JDBCUtil.doQueryVector(con, query.toString(),Thread.currentThread().getStackTrace()[1].getMethodName());
		ArrayList<Search> resp = new ArrayList<Search>();

		for (int row = 0; row < datos.size(); row++) {
			Properties properties = (Properties) datos.elementAt(row);
	
			    Search bean = new Search(properties.getProperty("idNorm"), coma);
				resp.add(bean);
			
				
		}
		return resp;
	}
	
	public static String  getNormasPrincipalesPlanDeLosProgramasAuditoria(Connection con, String idNorm) throws Exception {
		System.err.println("getNormasPrincipalesPlanDeLosProgramasAuditoria");
		StringBuilder query = new StringBuilder(0);
		query.append("SELECT idNorm FROM norms  ");
		if(idNorm!=null && idNorm.length()>0) {
			query.append("WHERE  sistema_gestion =  (SELECT  sistema_gestion  from norms where idnorm =").append(idNorm).append(") ");
			query.append("and indice ='0' ");
		}
		
		Vector<Properties> datos = JDBCUtil.doQueryVector(con, query.toString(),Thread.currentThread().getStackTrace()[1].getMethodName());
		String resp = "";
		for (int row = 0; row < datos.size(); row++) {
			Properties properties = (Properties) datos.elementAt(row);
			resp= properties.getProperty("idNorm");			
		}
		System.err.println(resp);
		return resp;
	}
	
	public static Collection<Search> getAllNormsCero() throws Exception {
		    System.err.println("*************************************************getAllNormsCero");
		    System.err.println("getAllNormsCero");
			System.err.println("*************************************************getAllNormsCero");
			Connection con = null;
			PreparedStatement st = null;
			ResultSet rs = null;
			ArrayList<Search> resp = new ArrayList<Search>();
			try {
				StringBuilder sql = new StringBuilder();
				sql.append("SELECT idNorm,titleNorm,indice,sistema_gestion,nameFile,documentRequired,auditProcess ")
					.append("FROM norms WHERE active = '")
					.append(Constants.permission).append("'")
		          	.append(" ORDER by idnorm ");
				
				con = JDBCUtil.getConnection(Thread.currentThread().getStackTrace()[1].getMethodName());
				st = con.prepareStatement(JDBCUtil.replaceCastMysql(sql.toString()));
				rs = st.executeQuery();

				StringBuilder sb = new StringBuilder("");
				while (rs.next()) {
					sb.setLength(0);
					sb.append(rs.getString("sistema_gestion")).append(" ")
							.append(rs.getString("indice")).append("- ")
							.append(rs.getString("titleNorm"));

					Search bean = new Search(rs.getString("idNorm"), sb.toString());
					bean.setAditionalInfo(rs.getString("nameFile"));
					bean.setDocumentRequired(rs.getString("documentRequired"));
					bean.setAuditProcess(rs.getString("auditProcess"));
					bean.setIndice(rs.getString("indice"));
					resp.add(bean);
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				JDBCUtil.closeQuietly(con, st, rs);
			}
			System.out.println(resp);
			return resp;
		}

}


