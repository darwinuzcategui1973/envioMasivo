package com.desige.webDocuments.utils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.desige.webDocuments.persistent.managers.HandlerParameters;
import com.desige.webDocuments.persistent.managers.HandlerStruct;
import com.desige.webDocuments.persistent.utils.JDBCUtil;

/**
 * Title: IDDBFactorySql.java <br/> Copyright: (c) 2004 Focus Consulting C.A.<br/>
 * 
 * @author Ing. Nelson Crespo (NC)
 * @author Ing. Nelson Crespo (SR)
 * @version WebDocuments v3.0 <br/> Changes:<br/>
 *          <ul>
 *          <li>28/03/2004 (NC) Creation </li>
 *          <li> 15/06/2005 (NC) Se agreg� el m�todo getNextIDDocument </li>
 *          <li> 26/07/2005 (SR) Se agreg� la constante UPDATENUMDOCLOC_INIC </li>
 *          <li> 16/08/2005 (SR)En el metodo getNextIDDocument se valido que en caso de que no exista un nodo en la tabla location, lo agregara y nos diera el poximo numero correlativo de numDocs </li>
 *          </ul>
 */

public class IDDBFactorySql {// com.desige.webDocumentsimplements BodyIDDBFactory {
	static Logger log = LoggerFactory.getLogger("[V4.0 FTP] " + IDDBFactorySql.class.getName());
	// The SQL to insert a new sequence number in the table.
	static public final String INSERT = "INSERT INTO dual_seqgen (NAME, NEXT_SEQ) VALUES(?, 1)";
	//static public final String INSERT = "INSERT INTO dual_seqgen (name, next_seq) VALUES(?, 1)";

	// Selects the next sequence number from the database.
	static public final String SELECT = "SELECT NEXT_SEQ, CAMPO, TABLA FROM dual_seqgen WHERE lower(NAME) = lower(?)";
	//  static public final String SELECT = "SELECT next_seq, campo, tabla FROM dual_seqgen WHERE lower(NAME) = lower(?)";

	// The SQL to one-up the current sequence number.
	// solo MSSQL static public final String UPDATE = "UPDATE dual_seqgen WITH (ROWLOCK) SET NEXT_SEQ = NEXT_SEQ+1 WHERE NAME = ? ";
	static public final String UPDATE = "UPDATE dual_seqgen SET NEXT_SEQ = NEXT_SEQ+1 WHERE lower(NAME) = lower(?) ";

	// solo MSSQL static public final String UPDATENUMDOCLOC = "UPDATE location WITH (ROWLOCK) SET NUMBER = NUMBER + 1 WHERE IDNODE = ? ";
	static public final String UPDATENUMDOCLOC = "UPDATE location SET NUMBER = CAST(NUMBER as int) + 1 WHERE IDNODE = ? ";

	// static public final String UPDATENUMDOCLOC_VACIO = "INSERT INTO location (NUMBER,IDNODE) VALUES(1 , ?) ";
	static public final String UPDATENUMDOCLOC_INIC = "UPDATE location WITH (ROWLOCK) SET NUMBER = (SELECT NEXT_SEQ  FROM dual_seqgen where lower(NAME)='numDocs') WHERE IDNODE = ? ";
	// static public final String UPDATENUMDOCLOC_INICVACIO = "INSERT INTO location (NUMBER,IDNODE) VALUES((SELECT next_seq FROM dual_seqgen where NAME='numDocs'),?)";
	//static public final String SELECTNUMDOCLOC = "SELECT NUMBER FROM location WHERE idNode=? ";
	static public final String SELECTNUMDOCLOC = "SELECT NUMBER FROM location WHERE idNode= 0";



	public synchronized static void updateValue(String table, String value, Connection conn, PreparedStatement update) throws SQLException {
		//solo MSSQL StringBuffer sql = new StringBuffer("UPDATE dual_seqgen WITH (ROWLOCK) SET NEXT_SEQ = ");
		StringBuffer sql = new StringBuffer("UPDATE dual_seqgen SET NEXT_SEQ = ");
		//StringBuffer sql = new StringBuffer("UPDATE dual_seqgen SET next_seq = ");
		sql.append(value).append(" WHERE lower(Name) = lower(?)");
		update = conn.prepareStatement(JDBCUtil.replaceCastMysql(sql.toString()));
		update.setString(1, table);
		update.executeUpdate();
	}

	/*
	 * </pre>
	 * 
	 * To guarantee that the select-update process involved in generating the Id is done atomically starts a transaction with a SERIALIZABLE isolation level. It first updates the NEXT_SEQ to the next value, thus locking the row, and then retrieve it. If everything goes fine, it commit and finish the transaction, else it rollback and throws an exception. @param tableName Name of the sequence to retrieve @return a unique ID @throws SQLException if anything goes wrong
	 */
	public synchronized static int getNextID(String tableName) throws SQLException, ApplicationExceptionChecked {
		return getNextID(null, tableName);
	}
	public synchronized static int getNextID(Connection conn, String tableName) throws SQLException, ApplicationExceptionChecked {
		PreparedStatement update = null;
		PreparedStatement select = null;
		PreparedStatement insert = null;
		int id = -1;
		String campoName = null;
		String fullTableName = null;
		boolean cerrar = false;
		
		tableName = tableName.toLowerCase();
		
	
		
		try {
			if(conn==null || conn.isClosed()) {
				conn = JDBCUtil.getConnection(Thread.currentThread().getStackTrace()[1].getMethodName());
				conn.setAutoCommit(false);

				switch (Constants.MANEJADOR_ACTUAL) {
				case Constants.MANEJADOR_MSSQL:
					// Todo: queda por evaluar si funciona con esta ** TRANSACTION_READ_UNCOMMITTED
					conn.setTransactionIsolation(Connection.TRANSACTION_READ_UNCOMMITTED);
					break;
				case Constants.MANEJADOR_POSTGRES:
					conn.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
					break;
				case Constants.MANEJADOR_MYSQL:
					conn.setTransactionIsolation(Connection.TRANSACTION_READ_UNCOMMITTED);
					break;
				}
				
				//conn.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);

				// no soportada por pofresql
				//conn.setTransactionIsolation(Connection.TRANSACTION_READ_UNCOMMITTED);

				if(Constants.MANEJADOR_ACTUAL!=Constants.MANEJADOR_POSTGRES) {
					//conn.setTransactionIsolation(Connection.TRANSACTION_READ_UNCOMMITTED);
				}

				cerrar=true;
			}
			// agarramos el id del numero correlativo y luego incrementamos el id en uno
			select = conn.prepareStatement(JDBCUtil.replaceCastMysql(SELECT));
			select.setString(1, tableName.toLowerCase());
			ResultSet res = select.executeQuery();
			res.next();
			//idtypedoc modificacion puntual luego veo
			id = res.getInt("NEXT_SEQ");
			//  id = res.getInt("next_seq");
		
			campoName = res.getString("campo").toLowerCase();
			fullTableName = res.getString("TABLA").toLowerCase();
			update = conn.prepareStatement(JDBCUtil.replaceCastMysql(UPDATE));
			update.setString(1, tableName);
			System.out.println(campoName);
			update.executeUpdate();
			if(!conn.getAutoCommit()) {
				conn.commit();
			}
			
			// nos aseguramos que el id no exista
			// buscamos si existe el correlativo de la tabla
			if(id>0 && campoName!=null && !campoName.trim().equals("") && fullTableName!=null && !fullTableName.trim().equals("")) {
				PreparedStatement sel = null;
				PreparedStatement upd = null;
				boolean hasNext = false;
				
				try  {
					try {
						switch (Constants.MANEJADOR_ACTUAL) {
						case Constants.MANEJADOR_MSSQL:
							// Todo: queda por evaluar si IGUAL MYSQL** CUNADO SE PRUEBA CON MSSQL
							sel = conn.prepareStatement(JDBCUtil.replaceCastMysql("SELECT campoName FROM tablaName WHERE campoName = ".concat(String.valueOf(id)).replaceAll("campoName", campoName).replaceAll("tablaName", fullTableName)));
							break;
						case Constants.MANEJADOR_POSTGRES:
							sel = conn.prepareStatement(JDBCUtil.replaceCastMysql("SELECT campoName FROM tablaName WHERE campoName = ".concat(String.valueOf(id)).replaceAll("campoName", campoName).replaceAll("tablaName", fullTableName)));
							break;
						case Constants.MANEJADOR_MYSQL:
							sel = conn.prepareStatement(JDBCUtil.replaceCastMysql("SELECT campoName FROM tablaName WHERE campoName = ".concat(String.valueOf(id)).replaceAll("campoName", campoName).replaceAll("tablaName", fullTableName)));
							break;
						}
						
						//
						
						hasNext = sel.executeQuery().next();
					} catch (Exception e) {
						// TODO: handle exception
						//debido al modelo actual, hay campos usados como secuencia que estan declarados como varchar en la respectiva tabla
						log.info("Haciendo rollback e intentando obtener id como si el campo fuese del tipo varchar");
						conn.rollback();
						sel = conn.prepareStatement(JDBCUtil.replaceCastMysql("SELECT campoName FROM tablaName WHERE campoName = '".concat(String.valueOf(id)).concat("'").replaceAll("campoName", campoName).replaceAll("tablaName", fullTableName)));
						hasNext = sel.executeQuery().next();
					}
					
					if(hasNext) {
						// actualizamos el id de la tabla de secuencias
						//upd = conn.prepareStatement(JDBCUtil.replaceCastMysql("UPDATE dual_seqgen SET NEXT_SEQ=(SELECT MAX(`campoName`)+1 FROM tablaName) WHERE tabla='tablaName' AND campo='campoName'".replaceAll("campoName", campoName).replaceAll("tablaName", fullTableName)));
						upd = conn.prepareStatement(JDBCUtil.replaceCastMysql("UPDATE dual_seqgen SET NEXT_SEQ=(SELECT MAX(campoName)+1 FROM tablaName) WHERE tabla='tablaName' AND campo='campoName'".replaceAll("campoName", campoName).replaceAll("tablaName", fullTableName)));
						upd.executeUpdate();
						
						// pedimos el correlativo nuevamente
						id = getNextID(conn, tableName);
					}
				} finally {
					if(sel!=null) sel.close();
					if(upd!=null) upd.close();
				}
			}
		} catch (SQLException e) {
			if(!conn.getAutoCommit()) {
				conn.rollback();
			}
			throw e;
		} finally {
			try {
				if (update != null) {
					update.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
			try {
				if (select != null) {
					select.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
			try {
				if (insert != null) {
					insert.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
			if(cerrar) {
				JDBCUtil.closeConnection(null, null, conn, "");
			}
		}
		return id;
	}

	// AQUI 
	
	
	public synchronized static int getNextIDNoUpdate(String tableName) throws SQLException, ApplicationExceptionChecked {
		Connection conn = null;
		PreparedStatement select = null;
		int id = -1;
		tableName = tableName.toLowerCase();
		try {
			// agarramos el id del numero correlativo y luego incrementamos el id en uno
			conn = JDBCUtil.getConnection(Thread.currentThread().getStackTrace()[1].getMethodName());
			conn.setAutoCommit(false);
			conn.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
			select = conn.prepareStatement(JDBCUtil.replaceCastMysql(SELECT));
			select.setString(1, tableName);
			ResultSet res = select.executeQuery();
			res.next();
		    id = res.getInt("NEXT_SEQ");
			  //id = res.getInt("next_seq");
			conn.commit();
		} catch (SQLException e) {
			conn.rollback();
			throw e;
		} finally {
			try {
				if (select != null) {
					select.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
			JDBCUtil.closeConnection(null, null, conn, "");
		}
		return id;
	}
	
	public synchronized static long getNextIDLong(String tableName) throws SQLException, ApplicationExceptionChecked {
		return getNextIDLong(null, tableName);
	}
	public synchronized static long getNextIDLong(Connection conn, String tableName) throws SQLException, ApplicationExceptionChecked {
		PreparedStatement update = null;
		PreparedStatement select = null;
		PreparedStatement insert = null;
		boolean cerrar = false;
		long id = -1;
		try {
			if(conn==null || conn.isClosed()) {
				conn = JDBCUtil.getConnection(Thread.currentThread().getStackTrace()[1].getMethodName());
				conn.setAutoCommit(false);
				switch (Constants.MANEJADOR_ACTUAL) {
				case Constants.MANEJADOR_MSSQL:
					// Todo: queda por evaluar si funciona con esta ** TRANSACTION_READ_UNCOMMITTED
					conn.setTransactionIsolation(Connection.TRANSACTION_READ_UNCOMMITTED);
					break;
				case Constants.MANEJADOR_POSTGRES:
					conn.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
					break;
				case Constants.MANEJADOR_MYSQL:
					conn.setTransactionIsolation(Connection.TRANSACTION_READ_UNCOMMITTED);
					break;
				}
				
				
				//conn.setTransactionIsolation(Connection.TRANSACTION_READ_UNCOMMITTED);
				//conn.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
				cerrar = true;
			}

			update = conn.prepareStatement(JDBCUtil.replaceCastMysql(UPDATE));
			update.setString(1, tableName);
			update.executeUpdate();
			select = conn.prepareStatement(JDBCUtil.replaceCastMysql(SELECT));
			select.setString(1, tableName);
			ResultSet res = select.executeQuery();
			res.next();
			//id = res.getLong("NEXT_SEQ");
			id = res.getLong("next_seq");
			if(cerrar) {
				conn.commit();
			}
		} catch (SQLException e) {
			if(cerrar) {
				conn.rollback();
			}
			throw e;
		} finally {
			try {
				if (update != null) {
					update.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
			try {
				if (select != null) {
					select.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
			try {
				if (insert != null) {
					insert.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
			if(cerrar) {
				JDBCUtil.closeConnection(null, null, conn, "");
			}
		}
		return id;
	}

	/**
	 * As getNextID(), but doesn't increment the ID counter
	 * 
	 * @param tableName
	 * @return
	 * @throws SQLException
	 */
	public synchronized int getActualID(String tableName) throws SQLException, ApplicationExceptionChecked {

		Connection conn = null;
		PreparedStatement select = null;
		ResultSet res = null;

		int id = -1;

		try {
			conn = JDBCUtil.getConnection(Thread.currentThread().getStackTrace()[1].getMethodName());
			conn.setAutoCommit(false);
			conn.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);

			select = conn.prepareStatement(JDBCUtil.replaceCastMysql(SELECT));
			select.setString(1, tableName);
			res = select.executeQuery();

			res.next();
			id = res.getInt("NEXT_SEQ");
			conn.commit();
		} catch (SQLException e) {
			conn.rollback();
			throw e;
		} finally {
			try {
				if (select != null) {
					select.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
			JDBCUtil.closeConnection(res, null, conn, "Error Closing Connection");
		}
		return id - 1;
	}


	public synchronized static int getNextIDDocument(String idNode, String minValue, String maxValue, String lenNumber) throws SQLException, ApplicationExceptionChecked {
		return getNextIDDocument(null, idNode, minValue, maxValue, lenNumber);
	}

	public synchronized static int getNextIDDocument(Connection conn, String idNode, String minValue, String maxValue, String lenNumber) throws SQLException, ApplicationExceptionChecked {
		PreparedStatement select1 = null;
		PreparedStatement update = null;
		PreparedStatement insert = null;
		StringBuffer sqlActualizar = new StringBuffer("");
		int id = 1;
		String sId = "";
		String nodos = idNode;
		boolean cerrar = false;
		try {
			if(conn==null || conn.isClosed()) {
				conn = JDBCUtil.getConnection(Thread.currentThread().getStackTrace()[1].getMethodName());
				conn.setAutoCommit(false);
				conn.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
				cerrar = true;
			}
			// verificamos el numero actual de location en un especifico idnode y ese numero lo retornamos
			// y actualizamos location el number actual +1
			select1 = conn.prepareStatement(JDBCUtil.replaceCastMysql(SELECTNUMDOCLOC));
			select1.setInt(1, new Integer(idNode));
			ResultSet res = select1.executeQuery();
			boolean swExisteNumber = false;
			boolean swExisteLocalidad = false;
			if (res.next()) {
				if(res.getString("NUMBER")!=null) {
					sId = res.getString("NUMBER");
					if(ToolsHTML.isNumeric(sId.trim())){
						swExisteNumber = true;
						id = Integer.parseInt(sId.trim());
					}
				}
				swExisteLocalidad = true;
			}
			res.close();
			log.debug("id: " + id);
			// si no hay nada en base de datos, el id va a ser = a 1
			if (!swExisteNumber) {
				try {
					id = Integer.parseInt(HandlerParameters.PARAMETROS.getResetDocNumber());
				} catch (Exception ex) {
					id = 1;
				}
			}

			if (swExisteLocalidad) {
					try {
						nodos = HandlerStruct.getAllNodesXNodeParent(conn, idNode);
					}catch(Exception e){
						//System.out.println("Error en getNextIDDocument: " + e);
					}
					sqlActualizar.append("SELECT max(number) as number FROM documents WHERE (idNode = ").append(idNode);
					sqlActualizar.append(" or idNode in (").append(nodos).append("))");
					sqlActualizar.append(" AND active = '").append(Constants.permission).append("' ");
					switch (Constants.MANEJADOR_ACTUAL) {
					case Constants.MANEJADOR_MSSQL:
						sqlActualizar.append(" AND len(number) = ").append(lenNumber);
						break;
					case Constants.MANEJADOR_POSTGRES:
						sqlActualizar.append(" AND length(number) = ").append(lenNumber);
						break;
					case Constants.MANEJADOR_MYSQL:
						sqlActualizar.append(" AND length(number) = ").append(lenNumber);
						break;
					}
					if(!ToolsHTML.isEmptyOrNull(minValue)){
						sqlActualizar.append(" AND number >= '" + minValue + "'");
					}
					if(!ToolsHTML.isEmptyOrNull(maxValue)){
						sqlActualizar.append(" AND number <= '" + maxValue + "'");
					}
					sId = "";
					try{
						Properties prop = JDBCUtil.doQueryOneRow(conn, sqlActualizar.toString(),Thread.currentThread().getStackTrace()[1].getMethodName());
						if (prop.getProperty("isEmpty").equalsIgnoreCase("false")) {
							sId = prop.getProperty("number");
						}
					}catch(Exception e){
						//System.out.println("Error al consultar max correlativo: " + e);
						e.printStackTrace();
					}
		        	if(ToolsHTML.isNumeric(sId)){
			        	int iNumber = Integer.parseInt(sId)+1;
			        	id = iNumber;
		        	}
					// "UPDATE location WITH (ROWLOCK) SET NUMBER = (SELECT next_seq FROM dual_seqgen where NAME='numDocs') WHERE IDNODE = ? ";
					// solo MSSQL StringBuffer proximoLocation=new StringBuffer("UPDATE location WITH (ROWLOCK) SET NUMBER =").append(id2).append(" WHERE IDNODE = ? ");
					StringBuffer proximoLocation = new StringBuffer("UPDATE location SET NUMBER =").append(id).append(" WHERE IDNODE = ? ");
					update = conn.prepareStatement(JDBCUtil.replaceCastMysql(proximoLocation.toString()));
			} else {
				// INSERT INTO location (NUMBER,IDNODE) VALUES((SELECT next_seq FROM dual_seqgen where NAME='numDocs'),?)
				StringBuffer insert_nuevo = new StringBuffer("INSERT INTO location (NUMBER,IDNODE) VALUES(");
				insert_nuevo.append(id).append(",").append("?").append(")");
				log.debug("[insert_nuevo]" + insert_nuevo);
				update = conn.prepareStatement(JDBCUtil.replaceCastMysql(insert_nuevo.toString()));
			}
			update.setInt(1, Integer.parseInt(idNode));
			update.executeUpdate();
			if(cerrar) {
				conn.commit();
			}
		} catch (SQLException e) {
			if(cerrar) {
				conn.rollback();
			}
			throw e;
		} finally {
			try {
				if (update != null) {
					update.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
			try {
				if (select1 != null) {
					select1.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
			try {
				if (insert != null) {
					insert.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
			if(cerrar) {
				JDBCUtil.closeConnection(null, null, conn, "");
			}
		}
		return id;
	}
	

	public synchronized static int getNextIDDocumentXSistema(String minValue, String maxValue, String lenNumber, String name) throws SQLException, ApplicationExceptionChecked {
		return getNextIDDocumentXSistema(null ,  minValue,  maxValue,  lenNumber, name);
	}
	public synchronized static int getNextIDDocumentXSistema(Connection conn, String minValue, String maxValue, String lenNumber, String name) throws SQLException, ApplicationExceptionChecked {
		PreparedStatement select1 = null;
		PreparedStatement update = null;
		PreparedStatement insert = null;
		StringBuffer sqlActualizar = new StringBuffer("");
		int id = 1;
		int idReset = 1;
		String sId = "";
		boolean cerrar = false;
		try {
			if(conn==null || conn.isClosed()) {
				conn = JDBCUtil.getConnection(Thread.currentThread().getStackTrace()[1].getMethodName());
				conn.setAutoCommit(false);
				conn.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
				cerrar=true;
			}
			
			try {
				idReset = Integer.parseInt(HandlerParameters.PARAMETROS.getResetDocNumber());
			} catch (Exception ex) {
				idReset = 1;
			}
			

			sId = String.valueOf(HandlerStruct.proximoNoUpdate("numDocs"));
			if(ToolsHTML.isNumeric(sId)){
				id = Integer.parseInt(sId);
				if(id<idReset){
					id = idReset;
				}
			}
			log.debug("id: " + id);

			sqlActualizar.append("SELECT max(number) as number FROM documents WHERE active = '").append(Constants.permission).append("' ");
			switch (Constants.MANEJADOR_ACTUAL) {
			case Constants.MANEJADOR_MSSQL:
				sqlActualizar.append(" AND len(number) = ").append(lenNumber);
				break;
			case Constants.MANEJADOR_POSTGRES:
				sqlActualizar.append(" AND length(number) = ").append(lenNumber);
				break;
			case Constants.MANEJADOR_MYSQL:
				sqlActualizar.append(" AND length(number) = ").append(lenNumber);
				break;
			}
			if(!ToolsHTML.isEmptyOrNull(minValue)){
				sqlActualizar.append(" AND number >= '" + minValue + "'");
			}
			if(!ToolsHTML.isEmptyOrNull(maxValue)){
				sqlActualizar.append(" AND number <= '" + maxValue + "'");
			}
			sId = "";
			try{
				Properties prop = JDBCUtil.doQueryOneRow(sqlActualizar.toString(),Thread.currentThread().getStackTrace()[1].getMethodName());
				if (prop.getProperty("isEmpty").equalsIgnoreCase("false")) {
					sId = prop.getProperty("number");
				}
			}catch(Exception e){
				//System.out.println("Error al consultar max correlativo: " + e);
			}
		    if(ToolsHTML.isNumeric(sId)){
			   	int iNumber = Integer.parseInt(sId)+1;
			   	id = iNumber;
		    }
		    StringBuffer proximo = new StringBuffer("UPDATE dual_seqgen SET NEXT_SEQ = '").append(id).append("' WHERE lower(NAME) = lower(?) ");
			update = conn.prepareStatement(JDBCUtil.replaceCastMysql(proximo.toString()));
			update.setString(1, name);
			update.executeUpdate();
			if(cerrar) {
				conn.commit();
			}
		} catch (SQLException e) {
			if(cerrar) {
				conn.rollback();
			}
			throw e;
		} finally {
			try {
				if (update != null) {
					update.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
			try {
				if (select1 != null) {
					select1.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
			try {
				if (insert != null) {
					insert.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
			if(cerrar) {
				JDBCUtil.closeConnection(null, null, conn, "");
			}
		}
		return id;
	}
	
}
