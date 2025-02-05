package com.desige.webDocuments.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.desige.webDocuments.persistent.utils.JDBCUtil;
import com.desige.webDocuments.utils.ToolsHTML;

public final class DocumentDAO {
	private final static Logger log = LoggerFactory.getLogger(DocumentDAO.class);
	private final static String ERASER_STATU = "1";
	
	private DocumentDAO() {
		// TODO Auto-generated constructor stub
	}
	
	/**
	 * Debemos obtener informacion del documento y su ultima version
	 * para el proceso de mover dicho documento de un nodo de la estructura a otro
	 * s
	 * @param idDocument
	 * @return
	 */
	public static boolean isEraserWithoutCode(String idDocument){
		final StringBuilder query = new StringBuilder("SELECT number, statu FROM documents ");
		query.append("WHERE numgen = ").append(idDocument);
		
		boolean result = false;
		Connection con = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		try {
			con = JDBCUtil.getConnection(Thread.currentThread().getStackTrace()[1].getMethodName());
			ps = con.prepareStatement(JDBCUtil.replaceCastMysql(query.toString()));
			rs = ps.executeQuery();
			
			if(rs.next()){
				if(ToolsHTML.isEmptyOrNull(rs.getString(1))){
					if(ERASER_STATU.equals(rs.getString(2).trim())){
						result = true;
					}
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
			log.error("Error in query. Error was " + e.getLocalizedMessage(), e);
		} finally {
			try {
				rs.close();
			} catch (Exception e) {
				// TODO: handle exception
			}
			
			try {
				ps.close();
			} catch (Exception e) {
				// TODO: handle exception
			}
			
			try {
				con.close();
			} catch (Exception e) {
				// TODO: handle exception
			}
		}
		
		log.info("Documento con id '" + idDocument + "' "
				+ (result ? "si" : "no") + " tiene su estado actual como borrador sin codigo.");
		
		return result;
	}

	/**
	 * 
	 * @param idVersion
	 * @return
	 */
	public static String getNameFileFromVersionDoc(String idVersion) {
		return getNameFileFromVersionDoc(null,idVersion);
	}
	
	public static String getNameFileFromVersionDoc(Connection con, String idVersion) {		
		// TODO Auto-generated method stub
		final String query = "SELECT d.namefile "
				+ "FROM documents d, versiondoc vd "
				+ "WHERE vd.numdoc = d.numgen "
				+ "AND numver = ?";
		
		String nameFile = null;

		boolean closeConnection = false;
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		try {
			if(con==null) {
				con = JDBCUtil.getConnection(Thread.currentThread().getStackTrace()[1].getMethodName());
				closeConnection = true;
			}
			ps = con.prepareStatement(query.toString());
			ps.setInt(1, Integer.parseInt(idVersion));
			
			rs = ps.executeQuery();
			if(rs.next()){
				nameFile = rs.getString(1);
			}
			
		} catch (Exception e) {
			// TODO: handle exception
		} finally {
			try {
				rs.close();
			} catch (Exception e2) {
				// TODO: handle exception
			}
			
			try {
				ps.close();
			} catch (Exception e2) {
				// TODO: handle exception
			}
			
			try {
				if(closeConnection) {
					con.close();
				}
			} catch (Exception e2) {
				// TODO: handle exception
			}
		}
		
		log.info("El documento asociado a la version " + idVersion + " tiene como filename '"
				+ nameFile + "'");
		
		return nameFile;
	}
}
