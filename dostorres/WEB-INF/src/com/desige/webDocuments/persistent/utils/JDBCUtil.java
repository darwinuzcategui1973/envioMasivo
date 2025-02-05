package com.desige.webDocuments.persistent.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;
import java.util.Properties;
import java.util.Vector;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NameNotFoundException;
import javax.sql.DataSource;

import org.apache.commons.dbutils.DbUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.desige.webDocuments.utils.ApplicationExceptionChecked;
import com.desige.webDocuments.utils.Constants;
import com.desige.webDocuments.utils.DesigeConf;
import com.desige.webDocuments.utils.StringUtil;
import com.desige.webDocuments.utils.ToolsHTML;
import com.focus.util.Archivo;

import sun.jdbc.rowset.CachedRowSet;

/**
 * Title: JDBCUtil.java <br/>
 * Copyright: (c) 2004 Focus Consulting C.A.<br/>
 * 
 * @author Ing. Nelson Crespo (NC)
 * @version WebDocuments v3.0 <br/>
 *          Changes:<br/>
 *          <ul>
 *          <li>23/03/2004 (NC) Creation</li>
 *          <li>30/05/2006 Cambios para Correcto Formato de Fechas</li>
 *          </ul>
 */

public class JDBCUtil extends Object {
	/**
	 * 2 segundos para considerar que un query se ejecuta dentro de los
	 * parametros aceptados, mas de este valor debe optimizarse.
	 */
	private static final long MILISEGUNDOS_QUERYS_NO_ALERTAS = 2000;
	private static final Logger log = LoggerFactory.getLogger("[V4.0 FTP] "
			+ JDBCUtil.class.getName());
	private static final Logger dbLogger = LoggerFactory.getLogger(JDBCUtil.class);


	/**
	 * To avoid the instatiation
	 */
	public JDBCUtil() {
	}

	/**
	 * Get the database connection
	 * 
	 * @return Connection object
	 */
	public static Connection connect() throws ApplicationExceptionChecked {
		try {
			//System.out.println("*** Abriendo nueva conexion ");
			String server = DesigeConf.getProperty("nameServer");
			// System.out.println("Contenedor -> "+server);
			try {
				if (Constants.MANEJADOR_ACTUAL == 0) {
					String nameContext = "";
					try {
						nameContext = ToolsHTML.getPath().concat("META-INF").concat(File.separator).concat("context.xml");
					} catch (java.lang.NullPointerException ex) {
						// aun no se ha iniciado completamente la aplicacion
						// los query que se ejecutan tienen sql standar
						// aplicable
						// a mssql y posgtresql
					}
					if (new File(nameContext).exists()) {
						Archivo archivo = new Archivo();
						StringBuffer texto = archivo.leer(nameContext);
						if (texto.indexOf("sqlserver") != -1) {
							Constants.MANEJADOR_ACTUAL = Constants.MANEJADOR_MSSQL;
						} else if (texto.indexOf("postgresql") != -1) {
							Constants.MANEJADOR_ACTUAL = Constants.MANEJADOR_POSTGRES;
						} else if (texto.indexOf("mysql") != -1) {
							Constants.MANEJADOR_ACTUAL = Constants.MANEJADOR_MYSQL;
						}
					}
				}
			} catch (NumberFormatException e) {
				if (Constants.MANEJADOR_ACTUAL == 0) {
					e.printStackTrace();
				}
			}
			if (ToolsHTML.isEmptyOrNull(server)) {
				server = "tomcat";
			}
			if (server.equalsIgnoreCase("webLogic")) {
				Properties prop = new Properties();
				prop.put(Context.INITIAL_CONTEXT_FACTORY,
						DesigeConf.getProperty("initialContext"));
				prop.put(Context.PROVIDER_URL,
						DesigeConf.getProperty("providerurl"));
				InitialContext ic = new InitialContext(prop);
				DataSource ds = (DataSource) ic.lookup(DesigeConf
						.getProperty("DataSource"));
				return ds.getConnection();
			}
			if ("tomcat".equalsIgnoreCase(server.trim())) {
				Context ctx = new InitialContext();

				Context envCtx;

				try {
					envCtx = (Context) ctx.lookup("java:comp/env");
				} catch (NameNotFoundException e) {
					// //System.out.println("Error en Lookup de java:comp");
					envCtx = ctx;
				}

				String dataSource = DesigeConf.getProperty("DataSource");
				if (dataSource == null) {
					dataSource = "jdbc/WebDocs";
				}
				DataSource ds = (DataSource) envCtx.lookup(dataSource);
				Connection cone = ds.getConnection();

				Constants.JDBC_CONNECTION = true;

				return cone;
			}
			return null;
		} catch (javax.naming.NameNotFoundException e) {
			// System.out.println(e.getMessage());
			throw new ApplicationExceptionChecked("E0007");
		} catch (Exception e) {
			log.error(e.getMessage());
			e.printStackTrace();
			throw new ApplicationExceptionChecked("E0007");
		}
	}

	/**
	 * Get the database connection
	 * 
	 * @return Connection object
	 */
	public static Connection getConnection(String metodo) throws ApplicationExceptionChecked {
		log.debug("**** NUEVA CONEXION *** ---> ".concat(metodo).concat("()"));
		return connect();
	}

	/**
	 * 
	 * @param timeExecution
	 * @param query
	 */
	private static void verifyTimeExecution(long timeExecution, String query) {
		if (timeExecution > MILISEGUNDOS_QUERYS_NO_ALERTAS) {
			dbLogger.info("Query: " + query + ". tardo " + timeExecution
					+ " ms");
		}
	}

	/**
	 * 
	 * @param update
	 * @return
	 * @throws java.lang.Exception
	 */
	public static int doUpdate(String update) throws Exception {
		Connection con = null;
		Statement sta = null;
		int rowsUpdated;

		long t0 = System.currentTimeMillis();
		try {
			log.debug(update);
			con = getConnection(Thread.currentThread().getStackTrace()[1].getMethodName());
			sta = con.createStatement();
			rowsUpdated = sta.executeUpdate(replaceCastMysql(update));
		} finally {
			closeConnection(sta, con, "Exception in JDBCUtil.doUpdate");
			verifyTimeExecution(System.currentTimeMillis() - t0, update);
		}

		return rowsUpdated;
	}

	/**
	 * 
	 * @param query
	 * @param keyField
	 * @return
	 * @throws java.lang.Exception
	 */
	public static Hashtable doQueryHash(String query, String keyField)
			throws Exception {
		Connection con = null;
		Hashtable hash = null;
		Statement stmt = null;
		ResultSet rs = null;

		long t0 = System.currentTimeMillis();
		try {
			con = getConnection(Thread.currentThread().getStackTrace()[1].getMethodName());
			stmt = con.createStatement();
			rs = stmt.executeQuery(replaceCastMysql(query));
			verifyTimeExecution(System.currentTimeMillis() - t0, query);
			hash = JDBCUtil.ResultSet2Hash(rs, keyField);
		} catch (Exception e) {
			throw e;
		} finally {
			closeConnection(rs, stmt, con, "Exception in JDBCUtil.doQueryHash");
		}

		return hash;
	}

	/**
	 * 
	 * @param rs
	 * @param keyField
	 * @return
	 * @throws java.sql.SQLException
	 */
	public static Hashtable ResultSet2Hash(ResultSet rs, String keyField)
			throws SQLException {
		Hashtable hash = new Hashtable();
		ResultSetMetaData rsmd = rs.getMetaData();
		int columnCount = rsmd.getColumnCount();

		while (rs.next()) {
			CaseInsensitiveProperties prop = new CaseInsensitiveProperties();
			for (int i = 1; i <= columnCount; i++) {
				String value = rs.getString(i);
				if (value == null) {
					value = "";
				}
				prop.setProperty(rsmd.getColumnName(i), value);
				prop.setProperty(Integer.toString(i), value);
			}
			hash.put(prop.getProperty(keyField), prop);
		}

		return hash;
	}

	/**
	 * Nunca retorna null. Si el resulset esta vacio, retorna algo con la
	 * propiedad isEmpty=true
	 * 
	 * @param query
	 * @return
	 * @throws java.lang.Exception
	 */
	public static Properties doQueryOneRow(String query, String nameMethodCurrent)
			throws ApplicationExceptionChecked, Exception {
		return doQueryOneRow(null, query, nameMethodCurrent);
	}

	/**
	 * 
	 * @param con
	 * @param query
	 * @return
	 * @throws ApplicationExceptionChecked
	 * @throws Exception
	 */
	public static Properties doQueryOneRow(Connection con, String query, String nameMethodCurrent)
			throws ApplicationExceptionChecked, Exception {
		Properties prop = null;
		Statement stmt = null;
		ResultSet rs = null;
		boolean cerrar = false;
		long t0 = System.currentTimeMillis();

		try {
			if (con == null || con.isClosed()) {
				con = getConnection(nameMethodCurrent);
				cerrar = true;
			}
			stmt = con.createStatement();
			// //System.out.println(query);
			
			rs = stmt.executeQuery(replaceCastMysql(query));
			verifyTimeExecution(System.currentTimeMillis() - t0, query);
			prop = JDBCUtil.ResultSet2Property(rs);
		} catch (ApplicationExceptionChecked ae) {
			throw new ApplicationExceptionChecked(ae.getKeyError());
		} catch (Exception e) {
			throw e;
		} finally {
			if (cerrar) {
				closeConnection(rs, stmt, con,
						"Exception in JDBCUtil.doQueryOneRow");
			}
		}
		return prop;
	}

	/**
	 * 
	 * @param query
	 * @return
	 * @throws java.lang.Exception
	 */
	public static Vector<Properties> doQueryVector(String query, String nameMethodCurrent) throws Exception {
		return doQueryVector(null, query, nameMethodCurrent);
	}

	public static Vector<Properties> doQueryVector(Connection con, String query, String nameMethodCurrent)
			throws Exception {
		Vector<Properties> wres = null;
		Statement sta = null;
		ResultSet rs = null;
		boolean cerrar = false;
		long t0 = System.currentTimeMillis();

		try {
			if (con == null || con.isClosed()) {
				con = getConnection(nameMethodCurrent);
				cerrar = true;
			}
			sta = con.createStatement();
			log.debug(query.toString());
			rs = sta.executeQuery(replaceCastMysql(query));
			verifyTimeExecution(System.currentTimeMillis() - t0, query);
			wres = JDBCUtil.ResultSet2Vector(rs);
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			if (cerrar) {
				closeConnection(rs, sta, con,
						"Exception in JDBCUtil.doQueryVector");
			}
		}

		return wres;
	}

	public static CachedRowSet executeQuery(StringBuilder query, String nameMethodCurrent)
			throws Exception {
		return executeQuery(query, null, nameMethodCurrent);
	}

	public static CachedRowSet executeQuery(StringBuilder query, ArrayList parametros, String nameMethodCurrent) throws Exception {
		return executeQuery(query, parametros, null, nameMethodCurrent);
	}

	public static CachedRowSet executeQuery(StringBuilder query, ArrayList parametros, Connection con, String nameMethodCurrent) throws Exception {
		PreparedStatement psta = null;
		ResultSet rs = null;
		CachedRowSet crs = new CachedRowSet();
		boolean isConeTemp = (con == null || con.isClosed());
		long t0 = System.currentTimeMillis();

		try {
			if (isConeTemp) {
				con = getConnection(nameMethodCurrent);
			}
			psta = con.prepareStatement(replaceCastMysql(query.toString()));
			if (parametros != null) {
				for (int i = 0; i < parametros.size(); i++) {
					// //System.out.println(parametros.get(i));
					psta.setObject(i + 1, parametros.get(i));
				}
			}
			log.debug(query.toString());
			// //System.out.println(psta.toString());
			rs = psta.executeQuery();
			verifyTimeExecution(System.currentTimeMillis() - t0,
					query.toString());
			crs.populate(rs);
		} catch (Exception e) {
			log.debug("".concat(query.toString()));
			throw e;
		} finally {
			if (isConeTemp) {
				closeConnection(rs, psta, con,
						"Exception in JDBCUtil.doQueryVector");
			}
		}

		return crs;
	}

	/**
	 * @author jrivero 21/06/2007
	 * @param query
	 *            : Una sentencia sql completa
	 * @return CachedRowSet : Un objeto estilo resultset, pero que funciona aun
	 *         con la conexion cerrada.
	 * @throws java.lang.Exception
	 */
	public static CachedRowSet executeQuery(StringBuffer query, String nameMethodCurrent)
			throws Exception {
		return executeQuery(query, null, nameMethodCurrent);
	}

	/**
	 * Metodo que retorna los ids separados por coma encontrados en una consulta a una tabla
	 * @param query Select que agrupa los id relacionados, debe contener un solo campo de consulta
	 * @return los ids encontrados por el query, si no retorna 0
	 * @throws Exception
	 */
	public static String executeQueryRetornaIds(StringBuffer query)
			throws Exception {
		return executeQueryRetornaIds(query,null, Thread.currentThread().getStackTrace()[1].getMethodName());
	}
	
	public static String executeQueryRetornaIds(StringBuffer query, ArrayList parametros, String nameMethodCurrent)
			throws Exception {
		return executeQueryRetornaIds(query, parametros, null, nameMethodCurrent);
	}

		public static String executeQueryRetornaIds(StringBuffer query, ArrayList parametros,Connection con, String nameMethodCurrent)
			throws Exception {
		// identificamos los id a utilizar
		StringBuffer q = new StringBuffer();
		CachedRowSet crs = executeQuery(query, parametros, con, nameMethodCurrent);
		
		if(crs.size()>0) {
			String coma = ",";
			String sep = "";
			while(crs.next()) {
				q.append(sep).append(crs.getInt(1));
				sep = coma;
			}
		} else {
			q.append("0");
		}
		
		return q.toString();
	}

	/**
	 * @author jrivero 21/06/2007
	 * @param query
	 *            : Una sentencia sql, puede ser completa o con signos de
	 *            interrogacion
	 * @param parametros
	 *            : Una lista de parametros en el mismo orden de los signos de
	 *            interrogacion
	 * @return CachedRowSet : Un objeto estilo resultset, pero que funciona aun
	 *         con la conexion cerrada.
	 * @throws java.lang.Exception
	 */
	public static CachedRowSet executeQuery(StringBuffer query,	ArrayList parametros, String nameMethodCurrrent) throws Exception {
		return executeQuery(query, parametros, null, nameMethodCurrrent);
	}

	/**
	 * 
	 * @param query
	 * @param parametros
	 * @param con
	 * @return
	 * @throws Exception
	 */
	public static CachedRowSet executeQuery(StringBuffer query,	ArrayList parametros, Connection con, String nameMethodCurrrent) throws Exception {
		PreparedStatement psta = null;
		ResultSet rs = null;
		CachedRowSet crs = new CachedRowSet();
		boolean isConeTemp = (con == null || con.isClosed());
		long t0 = System.currentTimeMillis();

		try {
			if (isConeTemp) {
				con = getConnection(nameMethodCurrrent);
			}
			psta = con.prepareStatement(replaceCastMysql(query.toString()));
			if (parametros != null) {
				for (int i = 0; i < parametros.size(); i++) {
					// //System.out.println(parametros.get(i));
					psta.setObject(i + 1, parametros.get(i));
				}
			}
			log.debug(query.toString());
			// //System.out.println(psta.toString());
			rs = psta.executeQuery();
			verifyTimeExecution(System.currentTimeMillis() - t0,
					query.toString());
			
			crs.populate(rs);
		} catch (Exception e) {
			log.debug("".concat(query.toString()));
			e.printStackTrace();
			throw e;
		} finally {
			if (isConeTemp) {
				closeConnection(rs, psta, con,
						"Exception in JDBCUtil.doQueryVector");
			}
		}

		return crs;
	}

	/**
	 * 
	 * @param query
	 * @param parametros
	 * @return
	 * @throws Exception
	 */
	public static InputStream getInputStream(StringBuffer query,
			ArrayList parametros) throws Exception {
		Connection con = null;
		PreparedStatement psta = null;
		ResultSet rs = null;
		InputStream in = null;
		long t0 = System.currentTimeMillis();

		try {
			con = getConnection(Thread.currentThread().getStackTrace()[1].getMethodName());
			psta = con.prepareStatement(replaceCastMysql(query.toString()));
			if (parametros != null) {
				for (int i = 0; i < parametros.size(); i++) {
					// //System.out.println(parametros.get(i));
					psta.setObject(i + 1, parametros.get(i));
				}
			}
			log.debug(query.toString());
			rs = psta.executeQuery();
			verifyTimeExecution(System.currentTimeMillis() - t0,
					query.toString());
			if (rs.next()) {
				in = rs.getBinaryStream(1);
			}
		} catch (Exception e) {
			log.debug("".concat(query.toString()));
			throw e;
		} finally {
			closeConnection(rs, psta, con,
					"Exception in JDBCUtil.doQueryVector");
		}

		return in;
	}

	/**
	 * @author jrivero 21/06/2007
	 * @param query
	 *            : Una sentencia sql completa
	 * @param parametros
	 *            : Una lista de parametros en el mismo orden de los signos de
	 *            interrogacion
	 * @return int : La cantidad de registros actualizados en el insert,update o
	 *         delete
	 * @throws java.lang.Exception
	 */
	public static int executeUpdate(StringBuffer query) throws Exception {
		return executeUpdate(query, null);
	}

	public static int executeUpdate(StringBuilder query) throws Exception {
		return executeUpdate(query, null);
	}

	/**
	 * @author jrivero 21/06/2007
	 * @param query
	 *            : Una sentencia sql, puede ser completa o con signos de
	 *            interrogacion
	 * @param parametros
	 *            : Una lista de parametros en el mismo orden de los signos de
	 *            interrogacion
	 * @return int : La cantidad de registros actualizados en el insert,update o
	 *         delete
	 * @throws java.lang.Exception
	 */
	public static int executeUpdate(StringBuffer query, ArrayList parametros)
			throws Exception {
		return executeUpdate(query, parametros, null);
	}

	public static int executeUpdate(StringBuilder query, ArrayList parametros)
			throws Exception {
		return executeUpdate(query, parametros, null);
	}

	/**
	 * 
	 * @param query
	 * @param parametros
	 * @param con
	 * @return
	 * @throws Exception
	 */
	public static int executeUpdate(StringBuffer query, ArrayList parametros,
			Connection con) throws Exception {
		ResultSet rs = null;
		PreparedStatement psta = null;
		boolean isConeTemp = (con == null);
		int act = 0;
		String q = "";
		long t0 = System.currentTimeMillis();

		try {
			if (isConeTemp) {
				con = getConnection(Thread.currentThread().getStackTrace()[1].getMethodName());
			}
			psta = con.prepareStatement(replaceCastMysql(query.toString()));
			if (parametros != null) {
				for (int i = 0; i < parametros.size(); i++) {
					if (parametros.get(i) instanceof FileInputStream) {
						psta.setBinaryStream(i + 1,
								(FileInputStream) parametros.get(i),
								((FileInputStream) parametros.get(i))
										.available());
					} else if (parametros.get(i) instanceof InputStream) {
						psta.setBinaryStream(i + 1,
								(InputStream) parametros.get(i),
								((InputStream) parametros.get(i)).available());
					} else {
						psta.setObject(i + 1, parametros.get(i));
					}
				}
			}
			log.debug(query.toString());
			q = query.toString();
			for (int i = 0; parametros != null && i < parametros.size(); i++) {
				// q = q.replace('?', '^');
				q = q.replaceFirst(
						"\\?",
						"'".concat(String.valueOf(parametros.get(i))).concat(
								"'"));
			}
			
			act = psta.executeUpdate();
			verifyTimeExecution(System.currentTimeMillis() - t0,
					query.toString());
		} catch (Exception e) {
			// System.out.println(q);
			log.debug("".concat(query.toString()));
			throw e;
		} finally {
			if (isConeTemp) {
				closeConnection(rs, psta, con,
						"Exception in JDBCUtil.doQueryVector");
			}
		}

		return act;
	}

	/**
	 * 
	 * @param query
	 * @param parametros
	 * @param con
	 * @return
	 * @throws Exception
	 */
	public static int executeUpdate(StringBuilder query, ArrayList parametros,
			Connection con) throws Exception {
		ResultSet rs = null;
		PreparedStatement psta = null;
		boolean isConeTemp = (con == null);
		int act = 0;
		String q = "";
		long t0 = System.currentTimeMillis();

		try {
			if (isConeTemp) {
				con = getConnection(Thread.currentThread().getStackTrace()[1].getMethodName());
			}
			psta = con.prepareStatement(replaceCastMysql(query.toString()));
			if (parametros != null) {
				for (int i = 0; i < parametros.size(); i++) {
					if (parametros.get(i) instanceof FileInputStream) {
						psta.setBinaryStream(i + 1,
								(FileInputStream) parametros.get(i),
								((FileInputStream) parametros.get(i))
										.available());
					} else if (parametros.get(i) instanceof InputStream) {
						psta.setBinaryStream(i + 1,
								(InputStream) parametros.get(i),
								((InputStream) parametros.get(i)).available());
					} else {
						psta.setObject(i + 1, parametros.get(i));
					}
				}
			}
			log.debug(query.toString());
			q = query.toString();
			for (int i = 0; parametros != null && i < parametros.size(); i++) {
				// q = q.replace('?', '^');
				q = q.replaceFirst(
						"\\?",
						"'".concat(String.valueOf(parametros.get(i))).concat(
								"'"));
			}
			act = psta.executeUpdate();
			verifyTimeExecution(System.currentTimeMillis() - t0,
					query.toString());
		} catch (Exception e) {
			// System.out.println(q);
			log.debug("".concat(query.toString()));
			throw e;
		} finally {
			if (isConeTemp) {
				closeConnection(rs, psta, con,
						"Exception in JDBCUtil.doQueryVector");
			}
		}

		return act;
	}

	/**
	 * 
	 * @param String
	 *            "false" or "true"
	 * @return byte 0=false 1=true
	 * @throws java.sql.SQLException
	 */
	public static byte getByte(String valor) {
		return getByte(valor, false);
	}

	/**
	 * 
	 * @param String
	 *            "false" or "true"
	 * @return byte 0=false 1=true
	 * @throws java.sql.SQLException
	 */
	public static Byte getByte(String valor, boolean devolverNull) {
		if (valor == null || valor.trim().equals("")) {
			if (devolverNull)
				return null;
			return (byte) 0;
		}
		if ("1".equals(valor.trim()))
			return (byte) 1;
		if ("true".equals(valor.trim()))
			return (byte) 1;
		return (byte) 0;
	}

	/**
	 * 
	 * @param String
	 *            "false" or "true"
	 * @return byte 0=false 1=true
	 * @throws java.sql.SQLException
	 */
	public static String getByte(byte bValor) {
		String valor = String.valueOf(bValor);
		if (valor == null)
			return "0";
		if ("1".equals(valor.trim()))
			return "1";
		if ("true".equals(valor.trim()))
			return "1";
		return "0";
	}

	/**
	 * 
	 */
	public static String replicate(String cad, int cant, String sep) {
		StringBuilder retorno = new StringBuilder(1024);

		for (int i = 0; i < cant; i++) {
			retorno.append(i == 0 ? "" : sep).append(cad);
		}

		return retorno.toString();
	}

	/**
	 * 
	 * @param rs
	 * @return
	 * @throws java.sql.SQLException
	 */
	public static Properties ResultSet2Property(ResultSet rs)
			throws SQLException {
		ResultSetMetaData rsmd = rs.getMetaData();
		int columnCount = rsmd.getColumnCount();
		CaseInsensitiveProperties prop = new CaseInsensitiveProperties();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss a");
		if (rs.next()) {
			for (int i = 1; i <= columnCount; i++) {
				int tipo = rsmd.getColumnType(i);
				String st = rsmd.getColumnClassName(i);
				// Se Revisa el Tipo de Dato
				// Si el Mismo es TimeStamp se d� un Trato especial al Mismo :D
				//System.out.println("columna "+i+" -> "+rsmd.getColumnName(i));
				if ("java.sql.Timestamp".equalsIgnoreCase(st)) {
					Timestamp ts = rs.getTimestamp(i);
					if (ts != null) {
						String fecha = sdf.format(new java.util.Date(ts
								.getTime()));
						prop.setProperty(rsmd.getColumnName(i), fecha);
						prop.setProperty(Integer.toString(i), fecha);
					} else {
						prop.setProperty(rsmd.getColumnName(i), "");
						prop.setProperty(Integer.toString(i), "");
					}
				} else {
					String value = rs.getString(i);
					if (value == null) {
						value = "";
					}
					prop.setProperty(rsmd.getColumnName(i), value);
					prop.setProperty(Integer.toString(i), value);
				}
			}
			prop.setProperty("isEmpty", "false");
		} else {
			prop.setProperty("isEmpty", "true");
		}
		return prop;
	}

	/**
	 * 
	 * @param rs
	 * @return
	 * @throws java.sql.SQLException
	 */
	public static Vector<Properties> ResultSet2Vector(ResultSet rs)
			throws SQLException {
		Vector<Properties> vector = new Vector<Properties>();
		ResultSetMetaData rsmd = rs.getMetaData();
		int columnCount = rsmd.getColumnCount();
		CaseInsensitiveProperties prop;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss a");
		while (rs.next()) {
			prop = new CaseInsensitiveProperties();

			for (int i = 1; i <= columnCount; i++) {
				String st = rsmd.getColumnClassName(i);
				// Se Revisa el Tipo de Dato
				// Si el Mismo es TimeStamp se d� un Trato especial al Mismo :D
				if ("java.sql.Timestamp".equalsIgnoreCase(st)) {
					Timestamp ts = null;
					try {
						ts = rs.getTimestamp(i);
					} catch(SQLException e) {
						System.out.println("la columna "+rsmd.getColumnName(i)+" error:"+e.getMessage());
						ts=null;
						e.printStackTrace();
					}
					if (ts != null) {
						String fecha = sdf.format(new java.util.Date(ts
								.getTime()));
						prop.setProperty(rsmd.getColumnName(i), fecha);
						prop.setProperty(Integer.toString(i), fecha);
					} else {
						prop.setProperty(rsmd.getColumnName(i), "");
						prop.setProperty(Integer.toString(i), "");
					}
				} else {
					String value = rs.getString(i);
					if (value == null) {
						value = "";
					}
					prop.setProperty(rsmd.getColumnName(i), value);
					prop.setProperty(Integer.toString(i), value);
				}
			}
			vector.add(prop);
		}
		return vector;
	}

	/**
	 * 
	 */
	public static void initPool() {
		// Do nothing. Pool initialization is done at class loading time.
	}

	/**
	 * Free the database resources in SQL instructions of type SELECT
	 * 
	 * @param rs
	 * @param stmt
	 * @param conn
	 * @param exceptionMessage
	 */
	public static void closeConnection(ResultSet rs, Statement stmt,
			Connection conn, String exceptionMessage) {
		DbUtils.closeQuietly(rs);
		DbUtils.closeQuietly(stmt);
		//System.out.println("*** close conection");
		DbUtils.closeQuietly(conn);
	}

	/**
	 * Free the database resources in SQL instructions of type SELECT
	 * 
	 * @param rs
	 * @param stmt
	 * @param conn
	 * @param exceptionMessage
	 */
	public static void closeConnection(ResultSet rs, PreparedStatement stmt,
			Connection conn, String exceptionMessage) {
		DbUtils.closeQuietly(rs);
		DbUtils.closeQuietly(stmt);
		//System.out.println("*** close conection");
		DbUtils.closeQuietly(conn);
	}

	/**
	 * Free the database resources in SQL instructions of type INSERT, DELETE or
	 * UPDATE
	 * 
	 * @param stmt
	 * @param conn
	 * @param exceptionMessage
	 */
	public static void closeConnection(Statement stmt, Connection conn,
			String exceptionMessage) {
		closeConnection(null, stmt, conn, exceptionMessage);
	}

	/**
	 * Free the database resources in SQL instructions of type INSERT, DELETE or
	 * UPDATE
	 * 
	 * @param conn
	 * @param exceptionMessage
	 */
	public static void closeConnection(Connection conn, String exceptionMessage) {
		closeConnection(null, null, conn, exceptionMessage);
	}

	/**
	 * 
	 * @return
	 * @throws ApplicationExceptionChecked
	 */
	public Connection connectwonderware() throws ApplicationExceptionChecked {
		try {
			Class.forName("net.sourceforge.jtds.jdbc.Driver").newInstance();
			Connection conn;
			String bd = DesigeConf.getProperty("bdwonderware");
			String user = DesigeConf.getProperty("userwonderware");
			String passwd = DesigeConf.getProperty("passwdwonderware");
			String puerto = DesigeConf.getProperty("puertowonderware");
			StringBuffer url = new StringBuffer();
			url.append("jdbc:jtds:sqlserver://").append(
					DesigeConf.getProperty("serverwonderware"));
			url.append(":").append(puerto).append(";")
					.append("SelectMethod=cursor;DatabaseName=").append(bd)
					.append(";").append(",");
			conn = DriverManager.getConnection(url.toString(), user, passwd);
			return conn;
		} catch (Exception e) {
			e.printStackTrace();
			throw new ApplicationExceptionChecked("E0007");
		}
	}

	/**
	 * 
	 * @param query
	 * @return
	 * @throws ApplicationExceptionChecked
	 * @throws Exception
	 */
	public static String getScalar(Connection con, String query)
			throws ApplicationExceptionChecked, Exception {
		Properties prop = null;
		Statement stmt = null;
		ResultSet rs = null;
		long t0 = System.currentTimeMillis();
		boolean isNewConnect = false;

		try {
			if(con==null) {
				con = getConnection(Thread.currentThread().getStackTrace()[1].getMethodName());
				isNewConnect = true;
			}
			stmt = con.createStatement();
			rs = stmt.executeQuery(replaceCastMysql(query));
			verifyTimeExecution(System.currentTimeMillis() - t0, query);
			if (rs.next()) {
				return rs.getString(1);
			} else {
				// System.out.println("No Hay Resultados: " + query);
				return "";
			}
		} catch (ApplicationExceptionChecked ae) {
			throw new ApplicationExceptionChecked(ae.getKeyError());
		} catch (Exception e) {
			throw e;
		} finally {
			if(isNewConnect) {
				closeConnection(rs, stmt, con,"Exception in JDBCUtil.doQueryOneRow");
			}
		}

	}

	/**
	 * 
	 * @param table
	 * @param col
	 * @param longitud
	 * @return
	 */
	public static String alterTableAdd(String table, String col, String longitud) {
		return new StringBuffer(1024).append(
				StringUtil.replace("alter table P1 add P2 varchar(P3)",
						new String[] { table, col, longitud })).toString();
	}

	/**
	 * 
	 * @param table
	 * @param col
	 * @param longitud
	 * @return
	 */
	public static String alterTableModify(String table, String col,
			String longitud) {
		StringBuilder query = new StringBuilder(1024);
		switch (Constants.MANEJADOR_ACTUAL) {
		case Constants.MANEJADOR_MSSQL:
			query.append(StringUtil.replace(
					"ALTER TABLE P1 ALTER COLUMN P2 varchar(P3)", new String[] {
							table, col, longitud }));
			break;
		case Constants.MANEJADOR_POSTGRES:
			query.append(StringUtil.replace(
					"ALTER TABLE P1 ALTER COLUMN P2 type varchar(P3)",
					new String[] { table, col, longitud }));
			break;
		case Constants.MANEJADOR_MYSQL:
			query.append(StringUtil.replace(
					"ALTER TABLE P1 MODIFY P2 varchar(P3)",
					new String[] { table, col, longitud }));
			break;
		}
		return query.toString();
	}

	/**
	 * 
	 * @param table
	 * @param col
	 * @return
	 */
	public static String alterTableDrop(String table, String col) {
		return new StringBuilder(104).append(
				StringUtil.replace("ALTER TABLE  P1 DROP COLUMN P2",
						new String[] { table, col })).toString();
	}

	// alter table temp ADD CONSTRAINT name_key UNIQUE(name)
	// alter table temp DROP CONSTRAINT name_key
	/**
	 * 
	 * @param table
	 * @param col
	 * @return
	 */
	public static String alterTableAddUnique(String table, String col) {
		return new StringBuilder(1024).append(
				StringUtil.replace(
						"ALTER TABLE P1 ADD CONSTRAINT P2_key UNIQUE(P2)",
						new String[] { table, col })).toString();
	}

	/**
	 * 
	 * @param table
	 * @param col
	 * @return
	 */
	public static String alterTableDropUnique(String table, String col) {
		return new StringBuilder(1024).append(
				StringUtil.replace("ALTER TABLE P1 DROP CONSTRAINT P2_key",
						new String[] { table, col })).toString();
	}
	
	public static String getCastAsBitString(String num) {
		switch (Constants.MANEJADOR_ACTUAL) {
		case Constants.MANEJADOR_MSSQL:
			return " CAST(".concat(num).concat(" as bit) ");
		case Constants.MANEJADOR_POSTGRES:
			return " CAST(".concat(num).concat(" as bit) ");
		case Constants.MANEJADOR_MYSQL:
			return " ".concat(num).concat(" ");
		}
		return num;
	}

	public static String getCastAsIntString(String num) {
		switch (Constants.MANEJADOR_ACTUAL) {
		case Constants.MANEJADOR_MSSQL:
			return " CAST(".concat(num).concat(" as int) ");
		case Constants.MANEJADOR_POSTGRES:
			return " CAST(".concat(num).concat(" as int) ");
		case Constants.MANEJADOR_MYSQL:
			return " ".concat(num).concat(" ");
		}
		return num;
	}
	
	/**
	 * 
	 * @param table
	 * @return
	 */
	public static String describeTable(String table) {
		StringBuilder query = new StringBuilder(1024);
		switch (Constants.MANEJADOR_ACTUAL) {
		case Constants.MANEJADOR_MSSQL:
			query.append(StringUtil.replace("sp_columns P1",
					new String[] { table }));
			break;
		case Constants.MANEJADOR_POSTGRES:
			query.append(StringUtil
					.replace(
							"SELECT column_name AS COLUMN_NAME FROM information_schema.columns WHERE table_name ='P1'",
							new String[] { table }));
			break;
		case Constants.MANEJADOR_MYSQL:
			query.append(StringUtil
					.replace(
							"SELECT column_name AS COLUMN_NAME FROM information_schema.columns WHERE table_name ='P1'",
							new String[] { table }));
			break;
		}
		return query.toString();
	}

	/**
	 * 
	 * @param cadena
	 * @return
	 */
	public static String changeSqlSignal(String cadena) {
		switch (Constants.MANEJADOR_ACTUAL) {
		case Constants.MANEJADOR_MSSQL:
			return cadena;
		case Constants.MANEJADOR_POSTGRES:
			return cadena.replaceAll("\\+", "||");
		case Constants.MANEJADOR_MYSQL:
			return " CONCAT".concat(cadena.replaceAll("\\+", ",").trim());
		default:
			return cadena;
		}
	}

	/**
	 * 
	 * @param campo
	 *            el campo al cual se le va a estraer la fecha sin hora
	 *            yyyy-mm-dd
	 * @return
	 */
	public static String changeSqlDate(String campo) {
		StringBuilder q = new StringBuilder(1024);
		switch (Constants.MANEJADOR_ACTUAL) {
		case Constants.MANEJADOR_MSSQL:
			q.append("CONVERT(varchar(10),P1,120)".replaceAll("P1", campo));
			break;
		case Constants.MANEJADOR_POSTGRES:
			q.append("substring(cast(P1 as varchar),1,10) ".replaceAll("P1",
					campo));
			break;
		case Constants.MANEJADOR_MYSQL:
			q.append("substring(cast(P1 as char),1,10) ".replaceAll("P1",
					campo));
			break;
		}
		return q.toString();
	}

	/**
	 * 
	 * @param date
	 * @return
	 */
	public static String convertDateSql(Date date) {
		return convertDateSql(ToolsHTML.sdfShowConvert1.format(date));
	}

	/**
	 * 
	 * @param date
	 * @return
	 */
	public static String convertDateSql(String date) {
		StringBuilder sql = new StringBuilder(1024);
		switch (Constants.MANEJADOR_ACTUAL) {
		case Constants.MANEJADOR_MSSQL:
			sql.append("CONVERT(datetime,'").append(date).append("',120))");
			break;
		case Constants.MANEJADOR_POSTGRES:
			sql.append("CAST('").append(date).append("' AS timestamp) ) ");
			break;
		case Constants.MANEJADOR_MYSQL:
			sql.append("TIMESTAMP('").append(date).append("') ) ");
			break;
		}
		return sql.toString();
	}

	/**
	 * 
	 * @param fields
	 * @param alias
	 * @return
	 */
	public static String concatSql(String[] fields, String alias) {
		StringBuilder sql = new StringBuilder(1024);
		String sep = "";
		switch (Constants.MANEJADOR_ACTUAL) {
		case Constants.MANEJADOR_MSSQL:
			for (int i = 0; i < fields.length; i++) {
				sql.append(sep).append(" ").append(fields[i]).append(" ");
				sep = "+";
			}
			break;
		case Constants.MANEJADOR_POSTGRES:
			for (int i = 0; i < fields.length; i++) {
				sql.append(sep).append(" ").append(fields[i]).append(" ");
				sep = "||";
			}
			break;
		case Constants.MANEJADOR_MYSQL:
			sql.append("CONCAT(");
			for (int i = 0; i < fields.length; i++) {
				sql.append(sep).append(" ").append(fields[i]).append(" ");
				sep = ",";
			}
			sql.append(")");
			break;
		}
		if (alias != null) {
			sql.append(" AS ").append(alias).append(" , ");
		}
		return sql.toString();
	}

	// ----------------------------------------------------------------
	// Otras Rutinas
	// Yamil Bracho
	// Abr 07, 2015
	// ----------------------------------------------------------------
	/**
	 * Cierra Conexion, Statement y resultset
	 * 
	 * @param conn
	 * @param stmt
	 * @param rs
	 */
	public static void closeQuietly(Connection conn, Statement stmt,
			ResultSet rs) {
		DbUtils.closeQuietly(rs);
		DbUtils.closeQuietly(stmt);
		//System.out.println("*** close conection");
		DbUtils.closeQuietly(conn);
	}

	/**
	 * Cierra conexion y Statement
	 * 
	 * @param conn
	 * @param stmt
	 */
	public static void closeQuietly(Connection conn, Statement stmt) {
		DbUtils.closeQuietly(stmt);
		//System.out.println("*** close conection");
		DbUtils.closeQuietly(conn);
	}

	/**
	 * Cierra Statement y ResultSet
	 * 
	 * @param stmt
	 * @param rs
	 */
	public static void closeQuietly(Statement stmt, ResultSet rs) {
		DbUtils.closeQuietly(rs);
		DbUtils.closeQuietly(stmt);
	}
	
	public static String replaceCastMysql(String sql) {
		sql = sql.replaceAll("'null'", "null");
		if(Constants.MANEJADOR_ACTUAL==Constants.MANEJADOR_MYSQL) {
			sql = sql.replaceAll("CAST\\(", "").replaceAll("cast\\(", "").replaceAll("AS bit\\)", "").replaceAll("as bit\\)", "").replaceAll("AS BIT\\)", "").replaceAll("AS VARCHAR\\)", "").replaceAll("AS varchar\\)", "").replaceAll("as varchar\\)", "").replaceAll("AS INT\\)", "").replaceAll("As INT\\)", "").replaceAll("as int\\)", "").replaceAll("AS int\\)", "");
			sql = sql.replaceAll("AS VARCHAR\\(4000\\)\\)", "");
			//sql = sql.replaceAll(" TIMESTAMP\\(", " (");
			
		} else if(Constants.MANEJADOR_ACTUAL==Constants.MANEJADOR_POSTGRES) {
			sql = sql.replaceAll("`", "");
		}
		System.out.println(sql);
		return sql;
	}

	/**
	 * Convierte un String en formato fecha larga yyyy-MM-dd hh:mm:ss en un objeto java.sql.Date
	 * @param fechaLarga
	 * @return el nuevo objeto en java.sql.Date
	 * @throws ParseException
	 */
	public static java.sql.Date convertToJavaSqlDate(String fechaLarga) throws ParseException {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		if(fechaLarga!=null) {
			return new java.sql.Date(sdf.parse(fechaLarga).getTime());
		} else {
			return null;
		}
	}
	
	public static void main(String[] args) {
		String cad = "CAST(? as bit),CAST(? as bit),CAST(? as bit),cast(? as bit),CAST(? as bit), CAST('ASDF' AS VARCHAR)";
		
		cad = "INSERT INTO location (IdNode,MajorID,MinorID,AllowUserWF,copy,Secuential,Conditional,AutomaticNotified,showCharge,typePrefix,timeDocVenc,txttimeDocVenc,HistAprob,cantExpDoc,unitExpDoc,vijenToprint,checkvijenToprint,checkborradorCorrelativo) VALUES (?,?,?,CAST(? AS bit),CAST(? AS bit),CAST(? AS bit),CAST(? AS bit),CAST(? AS bit),CAST(? AS bit),CAST(? AS bit),CAST(? AS bit),?,?,?,?,?,CAST(? AS bit),CAST(? AS bit))";
		
		Constants.MANEJADOR_ACTUAL=Constants.MANEJADOR_MYSQL;
		System.out.println(cad);
		System.out.println(replaceCastMysql(cad));
	}
	

}
