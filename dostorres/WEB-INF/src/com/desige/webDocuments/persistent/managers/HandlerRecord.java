package com.desige.webDocuments.persistent.managers;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.desige.webDocuments.persistent.utils.JDBCUtil;
import com.desige.webDocuments.sacop.actions.LoadSacop;
import com.desige.webDocuments.utils.Constants;
import com.desige.webDocuments.utils.ToolsHTML;

/**
 * Title: HandlerRecord.java <br/>
 * Copyright: (c) 2007 Focus Consulting C.A.<br/>
 * @author Focus
 * @version WebDocuments v4.3
 * <br/>
 * Changes:<br/>
 * <ul>
 *      <li>26/12/2007 (YSA) Creation </li>
 </ul>
 */
public class HandlerRecord extends HandlerBD {
    static Logger log = LoggerFactory.getLogger(HandlerRecord.class.getName());

    public static int getNumDocsCreated(String nameUser, String dateFrom, String dateTo) throws Exception{
    	int num = 0;
        StringBuffer sql = new StringBuffer();
        sql.append("SELECT count(DISTINCT(d.numGen)) as num FROM documents d,person p,versiondoc vd ");
        sql.append(" WHERE d.active = '1' ");
        if(!ToolsHTML.isEmptyOrNull(nameUser)){
        	//sql.append(" AND p.idPerson = " + idPerson);
        	sql.append(" AND vd.createdBy = '" + nameUser + "'");
        }
        sql.append(" AND p.nameUser = d.owner ");
        sql.append(" AND p.accountActive='1' ");
        sql.append(" AND vd.numDoc = d.numGen ");
        sql.append(" AND d.type <> '").append(HandlerDocuments.TypeDocumentsImpresion).append("'");
        if(!ToolsHTML.isEmptyOrNull(dateFrom) && dateFrom.length()>9){
    		switch (Constants.MANEJADOR_ACTUAL) {
    		case Constants.MANEJADOR_MSSQL:
    			sql.append(" AND vd.dateCreated >= CONVERT(datetime,'").append(dateFrom).append("',120)");
    			break;
    		case Constants.MANEJADOR_POSTGRES:
    			sql.append(" AND vd.dateCreated >= CAST('").append(dateFrom).append("' AS timestamp)  ");
    			break;
    		case Constants.MANEJADOR_MYSQL:
    			sql.append(" AND vd.dateCreated >= TIMESTAMP('").append(dateFrom).append("')  ");
    			break;
    		}
        }
        if(!ToolsHTML.isEmptyOrNull(dateTo) && dateTo.length()>9){
    		switch (Constants.MANEJADOR_ACTUAL) {
    		case Constants.MANEJADOR_MSSQL:
            	sql.append(" AND vd.dateCreated <= Convert(datetime,'").append(dateTo).append("',120) ");
    			break;
    		case Constants.MANEJADOR_POSTGRES:
    			sql.append(" AND vd.dateCreated <= CAST('").append(dateTo).append("' AS timestamp)  ");
    			break;
    		case Constants.MANEJADOR_MYSQL:
    			sql.append(" AND vd.dateCreated <= TIMESTAMP('").append(dateTo).append("')  ");
    			break;
    		}
        }
        //log.debug("getNumDocsCreated sql = " + sql.toString());
        ////System.out.println("---getNumDocsCreated sql = " + sql.toString());
        Connection con = null;
        PreparedStatement st = null;
		ResultSet rs = null;
		
        try{
        	con = JDBCUtil.getConnection(Thread.currentThread().getStackTrace()[1].getMethodName());
	        st = con.prepareStatement(JDBCUtil.replaceCastMysql(sql.toString()));
			rs = st.executeQuery();
			
			if (rs.next()) {
				num = rs.getInt("num");
			}
        }catch(Exception e){
        	//System.out.println("Error en getNumDocsCreated: " + e);
        	log.error(e.getMessage() + " SQL: " + sql, e);
        }finally{
        	try {
				rs.close();
			} catch (Exception ee) {
				// TODO: handle exception
			}
        	try {
				st.close();
			} catch (Exception ee) {
				// TODO: handle exception
			}
        	try {
				con.close();
			} catch (Exception ee) {
				// TODO: handle exception
			}
        }
        return num;
    }

    public static int getNumDocsExpired(long idPerson, String dateFrom, String dateTo) throws Exception{
    	int num = 0;
        StringBuffer sql = new StringBuffer();
        sql.append("SELECT count(DISTINCT(d.numGen)) as num FROM documents d,person p,versiondoc vd ");
        sql.append(" WHERE d.active = '1' ");
        if(idPerson>0){
        	sql.append(" AND p.idPerson = " + idPerson);
        }
        sql.append(" AND p.nameUser = d.owner ");
        sql.append(" AND p.accountActive='1' ");
        sql.append(" AND vd.numDoc = d.numGen ");
        sql.append(" AND d.type <> '").append(HandlerDocuments.TypeDocumentsImpresion).append("'");
        //No se toma en cuenta la ultima version ni status
        //sql.append(" AND vd.numVer = (SELECT MAX(numVer) FROM versiondoc vdi WHERE vdi.numDoc = d.numGen  AND vdi.statu = '"+HandlerDocuments.docApproved+"')  ");
        //sql.append(" AND vd.statu = '"+HandlerDocuments.docApproved+"'  ");

        if(!ToolsHTML.isEmptyOrNull(dateFrom) && dateFrom.length()>9){
    		switch (Constants.MANEJADOR_ACTUAL) {
    		case Constants.MANEJADOR_MSSQL:
            	sql.append(" AND vd.dateExpires >= Convert(datetime,'").append(dateFrom).append("',120) ");
    			break;
    		case Constants.MANEJADOR_POSTGRES:
    			sql.append(" AND vd.dateExpires >= CAST('").append(dateFrom).append("' AS timestamp)  ");
    			break;
    		case Constants.MANEJADOR_MYSQL:
    			sql.append(" AND vd.dateExpires >= TIMESTAMP('").append(dateFrom).append("')  ");
    			break;
    		}
    	}
        if(!ToolsHTML.isEmptyOrNull(dateTo) && dateTo.length()>9){
    		switch (Constants.MANEJADOR_ACTUAL) {
    		case Constants.MANEJADOR_MSSQL:
            	sql.append(" AND vd.dateExpires <= Convert(datetime,'").append(dateTo).append("',120) ");
    			break;
    		case Constants.MANEJADOR_POSTGRES:
    			sql.append(" AND vd.dateExpires <= CAST('").append(dateTo).append("' AS timestamp)  ");
    			break;
    		case Constants.MANEJADOR_MYSQL:
    			sql.append(" AND vd.dateExpires <= TIMESTAMP('").append(dateTo).append("')  ");
    			break;
    		}
    	}
        //log.debug("getNumDocsExpired sql = " + sql.toString());
        ////System.out.println("---getNumDocsExpired sql = " + sql.toString());
        Connection con = null;
        PreparedStatement st = null;
		ResultSet rs = null;
		
		try{
        	con = JDBCUtil.getConnection(Thread.currentThread().getStackTrace()[1].getMethodName());
	        st = con.prepareStatement(JDBCUtil.replaceCastMysql(sql.toString()));
			rs = st.executeQuery();
			
			if (rs.next()) {
				num = rs.getInt("num");
			}
        }catch(Exception e){
        	//System.out.println("Error en getNumDocsExpired: " + e);
        	log.error(e.getMessage() + " SQL: " + sql, e);
        }finally{
        	try {
				rs.close();
			} catch (Exception ee) {
				// TODO: handle exception
			}
        	try {
				st.close();
			} catch (Exception ee) {
				// TODO: handle exception
			}
        	try {
				con.close();
			} catch (Exception ee) {
				// TODO: handle exception
			}
        }
        return num;
    }

    public static int getNumDocsObsolete(long idPerson, String dateFrom, String dateTo) throws Exception{
    	int num = 0;
        StringBuffer sql = new StringBuffer();
        sql.append("SELECT count(DISTINCT(d.numGen)) as num FROM documents d,person p,versiondoc vd,workflows wf ");
        sql.append(" WHERE d.active = '1' ");
        if(idPerson>0){
        	sql.append(" AND p.idPerson = " + idPerson);
        }
        sql.append(" AND p.nameUser = d.owner ");
        sql.append(" AND p.accountActive='1' ");
        sql.append(" AND vd.numDoc = d.numGen ");
        //sql.append(" AND d.statu='"+HandlerDocuments.docObs+"' ");
        sql.append(" AND d.type <> '").append(HandlerDocuments.TypeDocumentsImpresion).append("'");
        //No se toma en cuenta ultima version, sino que este en obsoleta
        //sql.append(" AND vd.numVer = (SELECT MAX(numVer) FROM versiondoc vdi WHERE vdi.numDoc = d.numGen  AND vdi.statu = '"+HandlerDocuments.docObs+"') ");
        sql.append(" AND vd.statu = '"+HandlerDocuments.docObs+"' ");
        sql.append(" AND wf.idWorkFlow = (SELECT MAX(idWorkFlow) FROM workflows wf1 WHERE wf1.idDocument = d.numGen ) ");
        sql.append(" AND wf.idVersion = vd.numVer ");
        if(!ToolsHTML.isEmptyOrNull(dateFrom) && dateFrom.length()>9){
    		switch (Constants.MANEJADOR_ACTUAL) {
    		case Constants.MANEJADOR_MSSQL:
    			sql.append(" AND wf.dateCompleted >= Convert(datetime,'").append(dateFrom).append("',120) ");
    			break;
    		case Constants.MANEJADOR_POSTGRES:
    			sql.append(" AND wf.dateCompleted >= CAST('").append(dateFrom).append("' AS timestamp)  ");
    			break;
    		case Constants.MANEJADOR_MYSQL:
    			sql.append(" AND wf.dateCompleted >= TIMESTAMP('").append(dateFrom).append("')  ");
    			break;
    		}
    	}
        if(!ToolsHTML.isEmptyOrNull(dateTo) && dateTo.length()>9){
    		switch (Constants.MANEJADOR_ACTUAL) {
    		case Constants.MANEJADOR_MSSQL:
            	sql.append(" AND wf.dateCompleted <= Convert(datetime,'").append(dateTo).append("',120) ");
    			break;
    		case Constants.MANEJADOR_POSTGRES:
    			sql.append(" AND wf.dateCompleted <= CAST('").append(dateTo).append("' AS timestamp)  ");
    			break;
    		case Constants.MANEJADOR_MYSQL:
    			sql.append(" AND wf.dateCompleted <= TIMESTAMP('").append(dateTo).append("')  ");
    			break;
    		}
    	}
        //log.debug("getNumDocsObsolete sql = " + sql.toString());
        ////System.out.println("---getNumDocsObsolete sql = " + sql.toString());
        Connection con = null;
        PreparedStatement st = null;
        ResultSet rs = null;
        
        try{
        	con = JDBCUtil.getConnection(Thread.currentThread().getStackTrace()[1].getMethodName());
	        st = con.prepareStatement(JDBCUtil.replaceCastMysql(sql.toString()));
			rs = st.executeQuery();
			
			if (rs.next()) {
				num = rs.getInt("num");
			}
        }catch(Exception e){
        	//System.out.println("Error en getNumDocsObsolete: " + e);
        	log.error(e.getMessage() + " SQL: " + sql, e);
        }finally{
        	try {
				rs.close();
			} catch (Exception ee) {
				// TODO: handle exception
			}
        	try {
				st.close();
			} catch (Exception ee) {
				// TODO: handle exception
			}
        	try {
				con.close();
			} catch (Exception ee) {
				// TODO: handle exception
			}
        }
        return num;
    }

    public static int getNumDraftsExpired(long idPerson, String dateFrom, String dateTo) throws Exception{
    	int num = 0;
        StringBuffer sql = new StringBuffer();
        sql.append("SELECT count(DISTINCT(d.numGen)) as num FROM documents d,person p,versiondoc vd ");
        sql.append(" WHERE d.active = '1' ");
        if(idPerson>0){
        	sql.append(" AND p.idPerson = " + idPerson);
        }
        sql.append(" AND p.nameUser = d.owner ");
        sql.append(" AND p.accountActive='1' ");
        sql.append(" AND vd.numDoc = d.numGen ");
        sql.append(" AND d.type <> '").append(HandlerDocuments.TypeDocumentsImpresion).append("'");
        //No se toma en cuenta la ultima version ni status
        //sql.append(" AND vd.numVer = (SELECT MAX(numVer) FROM versiondoc vdi WHERE vdi.numDoc = d.numGen)  ");
        //sql.append(" AND vd.statu = '"+HandlerDocuments.docBorrador+"' ");
        if(!ToolsHTML.isEmptyOrNull(dateFrom) && dateFrom.length()>9){
    		switch (Constants.MANEJADOR_ACTUAL) {
    		case Constants.MANEJADOR_MSSQL:
            	sql.append(" AND vd.dateExpiresDrafts >= Convert(datetime,'").append(dateFrom).append("',120) ");
    			break;
    		case Constants.MANEJADOR_POSTGRES:
    			sql.append(" AND vd.dateExpiresDrafts >= CAST('").append(dateFrom).append("' AS timestamp) ");
    			break;
    		case Constants.MANEJADOR_MYSQL:
    			sql.append(" AND vd.dateExpiresDrafts >= TIMESTAMP('").append(dateFrom).append("') ");
    			break;
    		}
    	}
        if(!ToolsHTML.isEmptyOrNull(dateTo) && dateTo.length()>9){
    		switch (Constants.MANEJADOR_ACTUAL) {
    		case Constants.MANEJADOR_MSSQL:
            	sql.append(" AND vd.dateExpiresDrafts <= Convert(datetime,'").append(dateTo).append("',120) ");
    			break;
    		case Constants.MANEJADOR_POSTGRES:
    			sql.append(" AND vd.dateExpiresDrafts <= CAST('").append(dateTo).append("' AS timestamp) ");
    			break;
    		case Constants.MANEJADOR_MYSQL:
    			sql.append(" AND vd.dateExpiresDrafts <= TIMESTAMP('").append(dateTo).append("') ");
    			break;
    		}
        }
        //Fecha de expiracion de borrador en un intervalo si no tiene fecha de aprobacion ó fecha de aprobación mayor a la fecha de expiracion borrador
        sql.append(" AND ((vd.dateExpires is null) OR (vd.dateApproved > vd.dateExpiresDrafts))");
        //log.debug("getNumDraftsExpired sql = " + sql.toString());
        ////System.out.println("---getNumDraftsExpired sql = " + sql.toString());
        Connection con = null;
        PreparedStatement st = null;
        ResultSet rs = null;
        
        try{
        	con = JDBCUtil.getConnection(Thread.currentThread().getStackTrace()[1].getMethodName());
	        st = con.prepareStatement(JDBCUtil.replaceCastMysql(sql.toString()));
			rs = st.executeQuery();
			
			if (rs.next()) {
				num = rs.getInt("num");
			}
        }catch(Exception e){
        	//System.out.println("Error en getNumDraftsExpired: " + e);
        	log.error(e.getMessage() + " SQL: " + sql, e);
        }finally{
        	try {
				rs.close();
			} catch (Exception ee) {
				// TODO: handle exception
			}
        	try {
				st.close();
			} catch (Exception ee) {
				// TODO: handle exception
			}
        	try {
				con.close();
			} catch (Exception ee) {
				// TODO: handle exception
			}
        }
        
        return num;
    }

    public static int getNumWorkflowsApproval(String nameUser, String dateFrom, String dateTo) throws Exception{
    	int num = 0;
        StringBuffer sql = new StringBuffer();
        sql.append("SELECT count(DISTINCT(wf.idWorkFlow)) as num FROM documents d, workflows wf ");
        sql.append(" WHERE d.active = '1' ");
        if(!ToolsHTML.isEmptyOrNull(nameUser)){
        	sql.append(" AND wf.owner = '" + nameUser + "' ");
        }
        sql.append(" AND wf.idDocument = d.numGen ");
        sql.append(" AND d.type <> '").append(HandlerDocuments.TypeDocumentsImpresion).append("'");
        sql.append(" AND wf.type = '1' "); //Flujo de aprobacion
        if(!ToolsHTML.isEmptyOrNull(dateFrom) && dateFrom.length()>9){
    		switch (Constants.MANEJADOR_ACTUAL) {
    		case Constants.MANEJADOR_MSSQL:
            	sql.append(" AND wf.DateCreation >= Convert(datetime,'").append(dateFrom).append("',120) ");
    			break;
    		case Constants.MANEJADOR_POSTGRES:
    			sql.append(" AND wf.DateCreation >= CAST('").append(dateFrom).append("' AS timestamp) ");
    			break;
    		case Constants.MANEJADOR_MYSQL:
    			sql.append(" AND wf.DateCreation >= TIMESTAMP('").append(dateFrom).append("') ");
    			break;
    		}
        }
        if(!ToolsHTML.isEmptyOrNull(dateTo) && dateTo.length()>9){
    		switch (Constants.MANEJADOR_ACTUAL) {
    		case Constants.MANEJADOR_MSSQL:
            	sql.append(" AND wf.DateCreation <= Convert(datetime,'").append(dateTo).append("',120) ");
    			break;
    		case Constants.MANEJADOR_POSTGRES:
    			sql.append(" AND wf.DateCreation <= CAST('").append(dateTo).append("' AS timestamp) ");
    			break;
    		case Constants.MANEJADOR_MYSQL:
    			sql.append(" AND wf.DateCreation <= TIMESTAMP('").append(dateTo).append("') ");
    			break;
    		}
        }
        //log.debug("getNumWorkflowsApproval sql = " + sql.toString());
        ////System.out.println("---getNumWorkflowsApproval sql = " + sql.toString());
        Connection con = null;
        PreparedStatement st = null;
        ResultSet rs = null;
        
        try{
        	con = JDBCUtil.getConnection(Thread.currentThread().getStackTrace()[1].getMethodName());
	        st = con.prepareStatement(JDBCUtil.replaceCastMysql(sql.toString()));
			rs = st.executeQuery();
			
			if (rs.next()) {
				num = rs.getInt("num");
			}
        }catch(Exception e){
        	//System.out.println("Error en getNumWorkflowsApproval: " + e);
        	log.error(e.getMessage() + " SQL: " + sql, e);
        }finally{
        	try {
				rs.close();
			} catch (Exception ee) {
				// TODO: handle exception
			}
        	try {
				st.close();
			} catch (Exception ee) {
				// TODO: handle exception
			}
        	try {
				con.close();
			} catch (Exception ee) {
				// TODO: handle exception
			}
        }
        
        return num;
    }

    public static int getNumWorkflowsApprovalPending(String nameUser, String dateFrom, String dateTo) throws Exception{
    	int num = 0;
        StringBuffer sql = new StringBuffer();
        sql.append("SELECT count(DISTINCT(uw.row)) as num FROM documents d, workflows wf ,user_workflows uw ");
        sql.append(" WHERE d.active = '1' ");
        sql.append(" AND wf.idWorkFlow = uw.idWorkFlow ");
        if(!ToolsHTML.isEmptyOrNull(nameUser)){
        	sql.append(" AND uw.idUser = '" + nameUser + "' ");
        }
        sql.append(" AND wf.idDocument = d.numGen ");
        sql.append(" AND d.type <> '").append(HandlerDocuments.TypeDocumentsImpresion).append("'");
        sql.append(" AND wf.type = '1' "); //Flujo de aprobacion
        //Fecha de creación de flujo menor a hasta y status pendiente ó fecha de respuesta en el intervalo
        sql.append(" AND ((uw.statu =  '"+HandlerWorkFlows.wfuPending+"'");
        if(!ToolsHTML.isEmptyOrNull(dateTo) && dateTo.length()>9){
    		switch (Constants.MANEJADOR_ACTUAL) {
    		case Constants.MANEJADOR_MSSQL:
            	sql.append(" AND wf.DateCreation <= Convert(datetime,'").append(dateTo).append("',120) ");
    			break;
    		case Constants.MANEJADOR_POSTGRES:
    			sql.append(" AND wf.DateCreation <= CAST('").append(dateTo).append("' AS timestamp) ");
    			break;
    		case Constants.MANEJADOR_MYSQL:
    			sql.append(" AND wf.DateCreation <= TIMESTAMP('").append(dateTo).append("') ");
    			break;
    		}
        }
        sql.append(" ) OR (1=1 ");
        if(!ToolsHTML.isEmptyOrNull(dateFrom) && dateFrom.length()>9){
    		switch (Constants.MANEJADOR_ACTUAL) {
    		case Constants.MANEJADOR_MSSQL:
            	sql.append(" AND uw.dateReplied >= Convert(datetime,'").append(dateFrom).append("',120) ");
    			break;
    		case Constants.MANEJADOR_POSTGRES:
    			sql.append(" AND uw.dateReplied >= CAST('").append(dateFrom).append("' AS timestamp) ");
    			break;
    		case Constants.MANEJADOR_MYSQL:
    			sql.append(" AND uw.dateReplied >= TIMESTAMP('").append(dateFrom).append("') ");
    			break;
    		}
        }
        if(!ToolsHTML.isEmptyOrNull(dateTo) && dateTo.length()>9){
    		switch (Constants.MANEJADOR_ACTUAL) {
    		case Constants.MANEJADOR_MSSQL:
            	sql.append(" AND uw.dateReplied <= Convert(datetime,'").append(dateTo).append("',120) ");
    			break;
    		case Constants.MANEJADOR_POSTGRES:
    			sql.append(" AND uw.dateReplied <= CAST('").append(dateTo).append("' AS timestamp) ");
    			break;
    		case Constants.MANEJADOR_MYSQL:
    			sql.append(" AND uw.dateReplied <= TIMESTAMP('").append(dateTo).append("') ");
    			break;
    		}
        }
        sql.append(" ))");
        //log.debug("getNumWorkflowsApprovalPending sql = " + sql.toString());
        ////System.out.println("---getNumWorkflowsApprovalPending sql = " + sql.toString());
        Connection con = null;
        PreparedStatement st = null;
        ResultSet rs = null;
        
        try{
        	con = JDBCUtil.getConnection(Thread.currentThread().getStackTrace()[1].getMethodName());
	        st = con.prepareStatement(JDBCUtil.replaceCastMysql(sql.toString()));
			rs = st.executeQuery();
			
			if (rs.next()) {
				num = rs.getInt("num");
			}
        }catch(Exception e){
        	//System.out.println("Error en getNumWorkflowsApprovalPending: " + e);
        	log.error(e.getMessage() + " SQL: " + sql, e);
        }finally{
        	try {
				rs.close();
			} catch (Exception ee) {
				// TODO: handle exception
			}
        	try {
				st.close();
			} catch (Exception ee) {
				// TODO: handle exception
			}
        	try {
				con.close();
			} catch (Exception ee) {
				// TODO: handle exception
			}
        }
        
        return num;
    }

    public static int getNumWorkflowsApprovalApproved(String nameUser, String dateFrom, String dateTo) throws Exception{
    	int num = 0;
        StringBuffer sql = new StringBuffer();
        sql.append("SELECT count(DISTINCT(uw.row)) as num FROM documents d, workflows wf ,user_workflows uw ");
        sql.append(" WHERE d.active = '1' ");
        sql.append(" AND uw.statu =  '"+HandlerWorkFlows.wfuAcepted+"'");
        sql.append(" AND wf.idWorkFlow = uw.idWorkFlow ");
        if(!ToolsHTML.isEmptyOrNull(nameUser)){
        	sql.append(" AND uw.idUser = '" + nameUser + "' ");
        }
        sql.append(" AND wf.idDocument = d.numGen ");
        sql.append(" AND d.type <> '").append(HandlerDocuments.TypeDocumentsImpresion).append("'");
        sql.append(" AND wf.type = '1' "); //Flujo de aprobacion
        if(!ToolsHTML.isEmptyOrNull(dateFrom) && dateFrom.length()>9 ){
       		switch (Constants.MANEJADOR_ACTUAL) {
       		case Constants.MANEJADOR_MSSQL:
               	sql.append(" AND uw.dateReplied >= Convert(datetime,'").append(dateFrom).append("',120) ");
       			break;
       		case Constants.MANEJADOR_POSTGRES:
       			sql.append(" AND uw.dateReplied >= CAST('").append(dateFrom).append("' AS timestamp) ");
       			break;
       		case Constants.MANEJADOR_MYSQL:
       			sql.append(" AND uw.dateReplied >= TIMESTAMP('").append(dateFrom).append("') ");
       			break;
       		}
        }
        if(!ToolsHTML.isEmptyOrNull(dateTo) && dateTo.length()>9){
        	switch (Constants.MANEJADOR_ACTUAL) {
        	case Constants.MANEJADOR_MSSQL:
           		sql.append(" AND uw.dateReplied <= Convert(datetime,'").append(dateTo).append("',120) ");
        		break;
        	case Constants.MANEJADOR_POSTGRES:
        		sql.append(" AND uw.dateReplied <= CAST('").append(dateTo).append("' AS timestamp) ");
        		break;
        	case Constants.MANEJADOR_MYSQL:
        		sql.append(" AND uw.dateReplied <= TIMESTAMP('").append(dateTo).append("') ");
        		break;
        	}
        }
        //log.debug("getNumWorkflowsApprovalApproved sql = " + sql.toString());
        ////System.out.println("---getNumWorkflowsApprovalApproved sql = " + sql.toString());
        Connection con = null;
        PreparedStatement st = null;
        ResultSet rs = null;
        
        try{
        	con = JDBCUtil.getConnection(Thread.currentThread().getStackTrace()[1].getMethodName());
	        st = con.prepareStatement(JDBCUtil.replaceCastMysql(sql.toString()));
			rs = st.executeQuery();

			if (rs.next()) {
				num = rs.getInt("num");
			}
        }catch(Exception e){
        	//System.out.println("Error en getNumWorkflowsApprovalApproved: " + e);
        	log.error(e.getMessage() + " SQL: " + sql, e);
        }finally{
        	try {
				rs.close();
			} catch (Exception ee) {
				// TODO: handle exception
			}
        	try {
				st.close();
			} catch (Exception ee) {
				// TODO: handle exception
			}
        	try {
				con.close();
			} catch (Exception ee) {
				// TODO: handle exception
			}
        }
        
        return num;
    }

    //Consulta numero de flujos de aprobacion cancelados
    public static int getNumWorkflowsApprovalCanceled(String nameUser, String dateFrom, String dateTo) throws Exception{
    	int num = 0;
        StringBuffer sql = new StringBuffer();
        sql.append("SELECT count(DISTINCT(uw.row)) as num FROM documents d, workflows wf ,user_workflows uw ");
        sql.append(" WHERE d.active = '1' ");
        sql.append(" AND (uw.statu =  '"+HandlerWorkFlows.wfuCanceled+"' or uw.statu =  '"+HandlerWorkFlows.wfuCanceled2+"') ");
        //sql.append(" AND (uw.statu =  '"+HandlerWorkFlows.wfuCanceled+"' or uw.statu =  '"+HandlerWorkFlows.wfuCanceled2+"'" );
        //sql.append(" or uw.statu =  '"+HandlerWorkFlows.wfurechazado+"' or uw.statu =  '"+HandlerWorkFlows.wfuRejected+"') ");
        sql.append(" AND wf.idWorkFlow = uw.idWorkFlow ");
        if(!ToolsHTML.isEmptyOrNull(nameUser)){
        	sql.append(" AND uw.idUser = '" + nameUser + "' ");
        }
        sql.append(" AND wf.idDocument = d.numGen ");
        sql.append(" AND d.type <> '").append(HandlerDocuments.TypeDocumentsImpresion).append("'");
        sql.append(" AND wf.type = '1' "); //Flujo de aprobacion
        if(!ToolsHTML.isEmptyOrNull(dateFrom) && dateFrom.length()>9 ){
       		switch (Constants.MANEJADOR_ACTUAL) {
       		case Constants.MANEJADOR_MSSQL:
               	sql.append(" AND uw.dateReplied >= Convert(datetime,'").append(dateFrom).append("',120) ");
       			break;
       		case Constants.MANEJADOR_POSTGRES:
       			sql.append(" AND uw.dateReplied >= CAST('").append(dateFrom).append("' AS timestamp) ");
       			break;
       		case Constants.MANEJADOR_MYSQL:
       			sql.append(" AND uw.dateReplied >= TIMESTAMP('").append(dateFrom).append("') ");
       			break;
       		}
        }
        if(!ToolsHTML.isEmptyOrNull(dateTo) && dateTo.length()>9){
       		switch (Constants.MANEJADOR_ACTUAL) {
       		case Constants.MANEJADOR_MSSQL:
           		sql.append(" AND uw.dateReplied <= Convert(datetime,'").append(dateTo).append("',120) ");
       			break;
       		case Constants.MANEJADOR_POSTGRES:
       			sql.append(" AND uw.dateReplied <= CAST('").append(dateTo).append("' AS timestamp) ");
       			break;
       		case Constants.MANEJADOR_MYSQL:
       			sql.append(" AND uw.dateReplied <= TIMESTAMP('").append(dateTo).append("') ");
       			break;
       		}
        }
        //log.debug("getNumWorkflowsApprovalCanceled sql = " + sql.toString());
        ////System.out.println("---getNumWorkflowsApprovalCanceled sql = " + sql.toString());
        Connection con = null;
        PreparedStatement st = null;
        ResultSet rs = null;
        
        try{
        	con = JDBCUtil.getConnection(Thread.currentThread().getStackTrace()[1].getMethodName());
	        st = con.prepareStatement(JDBCUtil.replaceCastMysql(sql.toString()));
			rs = st.executeQuery();
		
			if (rs.next()) {
				num = rs.getInt("num");
			}
        }catch(Exception e){
        	//System.out.println("Error en getNumWorkflowsApprovalCanceled: " + e);
        	log.error(e.getMessage() + " SQL: " + sql, e);
        }finally{
        	try {
				rs.close();
			} catch (Exception ee) {
				// TODO: handle exception
			}
        	try {
				st.close();
			} catch (Exception ee) {
				// TODO: handle exception
			}
        	try {
				con.close();
			} catch (Exception ee) {
				// TODO: handle exception
			}
        }
        
        return num;
    }

    //Consulta numero de flujos de aprobacion expirados
    public static int getNumWorkflowsApprovalExpired(String nameUser, String dateFrom, String dateTo) throws Exception{
    	int num = 0;
        StringBuffer sql = new StringBuffer();
        sql.append("SELECT count(DISTINCT(uw.row)) as num FROM documents d, workflows wf ,user_workflows uw ");
        sql.append(" WHERE d.active = '1' ");
        sql.append(" AND wf.idWorkFlow = uw.idWorkFlow ");
        if(!ToolsHTML.isEmptyOrNull(nameUser)){
        	sql.append(" AND uw.idUser = '" + nameUser + "' ");
        }
        sql.append(" AND wf.idDocument = d.numGen ");
        sql.append(" AND d.type <> '").append(HandlerDocuments.TypeDocumentsImpresion).append("'");
       	sql.append(" AND ((wf.statu = '"+HandlerWorkFlows.vencido+"' ");//el flujo debe estar vencido
        sql.append(" AND uw.statu =  '"+HandlerWorkFlows.wfuVencido+"')");
       	sql.append(" OR (wf.statu = '"+HandlerWorkFlows.expires+"' ");//el flujo debe estar expirado
        sql.append(" AND uw.statu =  '"+HandlerWorkFlows.wfuExpired+"'))");
        sql.append(" AND wf.type = '1' "); //Flujo de aprobacion
        if(!ToolsHTML.isEmptyOrNull(dateFrom) && dateFrom.length()>9 ){
    		switch (Constants.MANEJADOR_ACTUAL) {
    		case Constants.MANEJADOR_MSSQL:
               	sql.append(" AND wf.dateExpire >= Convert(datetime,'").append(dateFrom).append("',120) ");
    			break;
    		case Constants.MANEJADOR_POSTGRES:
    			sql.append(" AND wf.dateExpire >= CAST('").append(dateFrom).append("' AS timestamp) ");
    			break;
    		case Constants.MANEJADOR_MYSQL:
    			sql.append(" AND wf.dateExpire >= TIMESTAMP('").append(dateFrom).append("') ");
    			break;
    		}
        }
        if(!ToolsHTML.isEmptyOrNull(dateTo) && dateTo.length()>9){
    		switch (Constants.MANEJADOR_ACTUAL) {
    		case Constants.MANEJADOR_MSSQL:
           		sql.append(" AND wf.dateExpire <= Convert(datetime,'").append(dateTo).append("',120) ");
    			break;
    		case Constants.MANEJADOR_POSTGRES:
    			sql.append(" AND wf.dateExpire <= CAST('").append(dateTo).append("' AS timestamp) ");
    			break;
    		case Constants.MANEJADOR_MYSQL:
    			sql.append(" AND wf.dateExpire <= TIMESTAMP('").append(dateTo).append("') ");
    			break;
    		}
        }
        //log.debug("getNumWorkflowsApprovalExpired sql = " + sql.toString());
        ////System.out.println("---getNumWorkflowsApprovalExpired sql = " + sql.toString());
        Connection con = null;
        PreparedStatement st = null;
        ResultSet rs = null;
        
        try{
        	con = JDBCUtil.getConnection(Thread.currentThread().getStackTrace()[1].getMethodName());
	        st = con.prepareStatement(JDBCUtil.replaceCastMysql(sql.toString()));
			rs = st.executeQuery();
			
			if (rs.next()) {
				num = rs.getInt("num");
			}
        }catch(Exception e){
        	//System.out.println("Error en getNumWorkflowsApprovalExpired: " + e);
        	log.error(e.getMessage() + " SQL: " + sql, e);
        } finally{
        	try {
				rs.close();
			} catch (Exception ee) {
				// TODO: handle exception
			}
        	try {
				st.close();
			} catch (Exception ee) {
				// TODO: handle exception
			}
        	try {
				con.close();
			} catch (Exception ee) {
				// TODO: handle exception
			}
        }
        return num;
    }

    public static int getNumWorkflowsReview(String nameUser, String dateFrom, String dateTo) throws Exception{
    	int num = 0;
        StringBuffer sql = new StringBuffer();
        sql.append("SELECT count(DISTINCT(wf.idWorkFlow)) as num FROM documents d, workflows wf ");
        sql.append(" WHERE d.active = '1' ");
        if(!ToolsHTML.isEmptyOrNull(nameUser)){
        	sql.append(" AND wf.owner = '" + nameUser + "' ");
        }
        sql.append(" AND wf.idDocument = d.numGen ");
        sql.append(" AND d.type <> '").append(HandlerDocuments.TypeDocumentsImpresion).append("'");
        sql.append(" AND wf.type = '0' "); //Flujo de revision
        if(!ToolsHTML.isEmptyOrNull(dateFrom) && dateFrom.length()>9){
    		switch (Constants.MANEJADOR_ACTUAL) {
    		case Constants.MANEJADOR_MSSQL:
            	sql.append(" AND wf.DateCreation >= Convert(datetime,'").append(dateFrom).append("',120) ");
    			break;
    		case Constants.MANEJADOR_POSTGRES:
    			sql.append(" AND wf.DateCreation >= CAST('").append(dateFrom).append("' AS timestamp) ");
    			break;
    		case Constants.MANEJADOR_MYSQL:
    			sql.append(" AND wf.DateCreation >= TIMESTAMP('").append(dateFrom).append("') ");
    			break;
    		}
        }
        if(!ToolsHTML.isEmptyOrNull(dateTo) && dateTo.length()>9){
    		switch (Constants.MANEJADOR_ACTUAL) {
    		case Constants.MANEJADOR_MSSQL:
            	sql.append(" AND wf.DateCreation <= Convert(datetime,'").append(dateTo).append("',120) ");
    			break;
    		case Constants.MANEJADOR_POSTGRES:
    			sql.append(" AND wf.DateCreation <= CAST('").append(dateTo).append("' AS timestamp) ");
    			break;
    		case Constants.MANEJADOR_MYSQL:
    			sql.append(" AND wf.DateCreation <= TIMESTAMP('").append(dateTo).append("') ");
    			break;
    		}
        }
        //log.debug("getNumWorkflowsReview sql = " + sql.toString());
        ////System.out.println("---getNumWorkflowsReview sql = " + sql.toString());
        Connection con = null;
        PreparedStatement st = null;
        ResultSet rs = null;
        
        try{
        	con = JDBCUtil.getConnection(Thread.currentThread().getStackTrace()[1].getMethodName());
	        st = con.prepareStatement(JDBCUtil.replaceCastMysql(sql.toString()));
			rs = st.executeQuery();
			
			if (rs.next()) {
				num = rs.getInt("num");
			}
        }catch(Exception e){
        	//System.out.println("Error en getNumWorkflowsApprovalExpired: " + e);
        	log.error(e.getMessage() + " SQL: " + sql, e);
        } finally{
        	try {
				rs.close();
			} catch (Exception ee) {
				// TODO: handle exception
			}
        	try {
				st.close();
			} catch (Exception ee) {
				// TODO: handle exception
			}
        	try {
				con.close();
			} catch (Exception ee) {
				// TODO: handle exception
			}
        }
        return num;
    }

    public static int getNumWorkflowsReviewPending(String nameUser, String dateFrom, String dateTo) throws Exception{
    	int num = 0;
        StringBuffer sql = new StringBuffer();
        sql.append("SELECT count(DISTINCT(uw.row)) as num FROM documents d, workflows wf ,user_workflows uw ");
        sql.append(" WHERE d.active = '1' ");
        sql.append(" AND wf.idWorkFlow = uw.idWorkFlow ");
        if(!ToolsHTML.isEmptyOrNull(nameUser)){
        	sql.append(" AND uw.idUser = '" + nameUser + "' ");
        }
        sql.append(" AND wf.idDocument = d.numGen ");
        sql.append(" AND d.type <> '").append(HandlerDocuments.TypeDocumentsImpresion).append("'");
        sql.append(" AND wf.type = '0' "); //Flujo de revision
        //Fecha de creación de flujo menor a hasta y status pendiente ó fecha de respuesta en el intervalo
        sql.append(" AND ((uw.statu =  '"+HandlerWorkFlows.wfuPending+"'");
        if(!ToolsHTML.isEmptyOrNull(dateTo) && dateTo.length()>9){
    		switch (Constants.MANEJADOR_ACTUAL) {
    		case Constants.MANEJADOR_MSSQL:
            	sql.append(" AND wf.DateCreation <= Convert(datetime,'").append(dateTo).append("',120) ");
    			break;
    		case Constants.MANEJADOR_POSTGRES:
    			sql.append(" AND wf.DateCreation <= CAST('").append(dateTo).append("' AS timestamp) ");
    			break;
    		case Constants.MANEJADOR_MYSQL:
    			sql.append(" AND wf.DateCreation <= TIMESTAMP('").append(dateTo).append("') ");
    			break;
    		}
        }
        sql.append(" ) OR (1=1 ");
        if(!ToolsHTML.isEmptyOrNull(dateFrom) && dateFrom.length()>9){
    		switch (Constants.MANEJADOR_ACTUAL) {
    		case Constants.MANEJADOR_MSSQL:
            	sql.append(" AND uw.dateReplied >= Convert(datetime,'").append(dateFrom).append("',120) ");
    			break;
    		case Constants.MANEJADOR_POSTGRES:
    			sql.append(" AND uw.dateReplied >= CAST('").append(dateFrom).append("' AS timestamp) ");
    			break;
    		case Constants.MANEJADOR_MYSQL:
    			sql.append(" AND uw.dateReplied >= TIMESTAMP('").append(dateFrom).append("') ");
    			break;
    		}
        }
        if(!ToolsHTML.isEmptyOrNull(dateTo) && dateTo.length()>9){
    		switch (Constants.MANEJADOR_ACTUAL) {
    		case Constants.MANEJADOR_MSSQL:
            	sql.append(" AND uw.dateReplied <= Convert(datetime,'").append(dateTo).append("',120) ");
    			break;
    		case Constants.MANEJADOR_POSTGRES:
    			sql.append(" AND uw.dateReplied <= CAST('").append(dateTo).append("' AS timestamp) ");
    			break;
    		case Constants.MANEJADOR_MYSQL:
    			sql.append(" AND uw.dateReplied <= TIMESTAMP('").append(dateTo).append("') ");
    			break;
    		}
        }
        sql.append(" ))");
        //log.debug("getNumWorkflowsReviewPending sql = " + sql.toString());
        ////System.out.println("---getNumWorkflowsReviewPending sql = " + sql.toString());
        Connection con = null;
        PreparedStatement st = null;
        ResultSet rs = null;
        
        try{
        	con = JDBCUtil.getConnection(Thread.currentThread().getStackTrace()[1].getMethodName());
	        st = con.prepareStatement(JDBCUtil.replaceCastMysql(sql.toString()));
			rs = st.executeQuery();
			
			if (rs.next()) {
				num = rs.getInt("num");
			}
        }catch(Exception e){
        	//System.out.println("Error en getNumWorkflowsApprovalExpired: " + e);
        	log.error(e.getMessage() + " SQL: " + sql, e);
        } finally{
        	try {
				rs.close();
			} catch (Exception ee) {
				// TODO: handle exception
			}
        	try {
				st.close();
			} catch (Exception ee) {
				// TODO: handle exception
			}
        	try {
				con.close();
			} catch (Exception ee) {
				// TODO: handle exception
			}
        }
        
        return num;
    }

    public static int getNumWorkflowsReviewApproved(String nameUser, String dateFrom, String dateTo) throws Exception{
    	int num = 0;
        StringBuffer sql = new StringBuffer();
        sql.append("SELECT count(DISTINCT(uw.row)) as num FROM documents d, workflows wf ,user_workflows uw ");
        sql.append(" WHERE d.active = '1' ");
        sql.append(" AND uw.statu =  '"+HandlerWorkFlows.wfuAcepted+"'");
        sql.append(" AND wf.idWorkFlow = uw.idWorkFlow ");
        if(!ToolsHTML.isEmptyOrNull(nameUser)){
        	sql.append(" AND uw.idUser = '" + nameUser + "' ");
        }
        sql.append(" AND wf.idDocument = d.numGen ");
        sql.append(" AND d.type <> '").append(HandlerDocuments.TypeDocumentsImpresion).append("'");
        sql.append(" AND wf.type = '0' "); //Flujo de revision
        if(!ToolsHTML.isEmptyOrNull(dateFrom) && dateFrom.length()>9 ){
       		switch (Constants.MANEJADOR_ACTUAL) {
       		case Constants.MANEJADOR_MSSQL:
               	sql.append(" AND uw.dateReplied >= Convert(datetime,'").append(dateFrom).append("',120) ");
       			break;
       		case Constants.MANEJADOR_POSTGRES:
       			sql.append(" AND uw.dateReplied >= CAST('").append(dateFrom).append("' AS timestamp) ");
       			break;
       		case Constants.MANEJADOR_MYSQL:
       			sql.append(" AND uw.dateReplied >= TIMESTAMP('").append(dateFrom).append("') ");
       			break;
       		}
        }
        if(!ToolsHTML.isEmptyOrNull(dateTo) && dateTo.length()>9){
        	switch (Constants.MANEJADOR_ACTUAL) {
        	case Constants.MANEJADOR_MSSQL:
           		sql.append(" AND uw.dateReplied <= Convert(datetime,'").append(dateTo).append("',120) ");
        		break;
        	case Constants.MANEJADOR_POSTGRES:
        		sql.append(" AND uw.dateReplied <= CAST('").append(dateTo).append("' AS timestamp) ");
        		break;
        	case Constants.MANEJADOR_MYSQL:
        		sql.append(" AND uw.dateReplied <= TIMESTAMP('").append(dateTo).append("') ");
        		break;
        	}
        }
        //log.debug("getNumWorkflowsReviewApproved sql = " + sql.toString());
        ////System.out.println("---getNumWorkflowsReviewApproved sql = " + sql.toString());
        Connection con = null;
        PreparedStatement st = null;
        ResultSet rs = null;
        
        try{
        	con = JDBCUtil.getConnection(Thread.currentThread().getStackTrace()[1].getMethodName());
	        st = con.prepareStatement(JDBCUtil.replaceCastMysql(sql.toString()));
			rs = st.executeQuery();
			
			if (rs.next()) {
				num = rs.getInt("num");
			}
        }catch(Exception e){
        	//System.out.println("Error en getNumWorkflowsApprovalExpired: " + e);
        	log.error(e.getMessage() + " SQL: " + sql, e);
        } finally{
        	try {
				rs.close();
			} catch (Exception ee) {
				// TODO: handle exception
			}
        	try {
				st.close();
			} catch (Exception ee) {
				// TODO: handle exception
			}
        	try {
				con.close();
			} catch (Exception ee) {
				// TODO: handle exception
			}
        }
        
        return num;
    }

    //Consulta numero de flujos de revision cancelados
    public static int getNumWorkflowsReviewCanceled(String nameUser, String dateFrom, String dateTo) throws Exception{
    	int num = 0;
        StringBuffer sql = new StringBuffer();
        sql.append("SELECT count(DISTINCT(uw.row)) as num FROM documents d, workflows wf ,user_workflows uw ");
        sql.append(" WHERE d.active = '1' ");
        sql.append(" AND (uw.statu =  '"+HandlerWorkFlows.wfuCanceled+"' or uw.statu =  '"+HandlerWorkFlows.wfuCanceled2+"') ");
        //sql.append(" AND (uw.statu =  '"+HandlerWorkFlows.wfuCanceled+"' or uw.statu =  '"+HandlerWorkFlows.wfuCanceled2+"'" );
        //sql.append(" or uw.statu =  '"+HandlerWorkFlows.wfurechazado+"' or uw.statu =  '"+HandlerWorkFlows.wfuRejected+"') ");
        sql.append(" AND wf.idWorkFlow = uw.idWorkFlow ");
        if(!ToolsHTML.isEmptyOrNull(nameUser)){
        	sql.append(" AND uw.idUser = '" + nameUser + "' ");
        }
        sql.append(" AND wf.idDocument = d.numGen ");
        sql.append(" AND d.type <> '").append(HandlerDocuments.TypeDocumentsImpresion).append("'");
        sql.append(" AND wf.type = '0' "); //Flujo de revision
        if(!ToolsHTML.isEmptyOrNull(dateFrom) && dateFrom.length()>9 ){
       		switch (Constants.MANEJADOR_ACTUAL) {
       		case Constants.MANEJADOR_MSSQL:
               	sql.append(" AND uw.dateReplied >= Convert(datetime,'").append(dateFrom).append("',120) ");
       			break;
       		case Constants.MANEJADOR_POSTGRES:
       			sql.append(" AND uw.dateReplied >= CAST('").append(dateFrom).append("' AS timestamp) ");
       			break;
       		case Constants.MANEJADOR_MYSQL:
       			sql.append(" AND uw.dateReplied >= TIMESTAMP('").append(dateFrom).append("') ");
       			break;
       		}
        }
        if(!ToolsHTML.isEmptyOrNull(dateTo) && dateTo.length()>9){
       		switch (Constants.MANEJADOR_ACTUAL) {
       		case Constants.MANEJADOR_MSSQL:
           		sql.append(" AND uw.dateReplied <= Convert(datetime,'").append(dateTo).append("',120) ");
       			break;
       		case Constants.MANEJADOR_POSTGRES:
       			sql.append(" AND uw.dateReplied <= CAST('").append(dateTo).append("' AS timestamp) ");
       			break;
       		case Constants.MANEJADOR_MYSQL:
       			sql.append(" AND uw.dateReplied <= TIMESTAMP('").append(dateTo).append("') ");
       			break;
       		}
        }
        //log.debug("getNumWorkflowsReviewCanceled sql = " + sql.toString());
        ////System.out.println("---getNumWorkflowsReviewCanceled sql = " + sql.toString());
        Connection con = null;
        PreparedStatement st = null;
        ResultSet rs = null;
        
        try{
        	con = JDBCUtil.getConnection(Thread.currentThread().getStackTrace()[1].getMethodName());
	        st = con.prepareStatement(JDBCUtil.replaceCastMysql(sql.toString()));
			rs = st.executeQuery();
			
			if (rs.next()) {
				num = rs.getInt("num");
			}
        }catch(Exception e){
        	//System.out.println("Error en getNumWorkflowsApprovalExpired: " + e);
        	log.error(e.getMessage() + " SQL: " + sql, e);
        } finally{
        	try {
				rs.close();
			} catch (Exception ee) {
				// TODO: handle exception
			}
        	try {
				st.close();
			} catch (Exception ee) {
				// TODO: handle exception
			}
        	try {
				con.close();
			} catch (Exception ee) {
				// TODO: handle exception
			}
        }
        
        return num;
    }

    //Consulta numero de flujos de revision expirados
    public static int getNumWorkflowsReviewExpired(String nameUser, String dateFrom, String dateTo) throws Exception{
    	int num = 0;
        StringBuffer sql = new StringBuffer();
        sql.append("SELECT count(DISTINCT(uw.row)) as num FROM documents d, workflows wf ,user_workflows uw ");
        sql.append(" WHERE d.active = '1' ");
        sql.append(" AND wf.idWorkFlow = uw.idWorkFlow ");
        if(!ToolsHTML.isEmptyOrNull(nameUser)){
        	sql.append(" AND uw.idUser = '" + nameUser + "' ");
        }
        sql.append(" AND wf.idDocument = d.numGen ");
        sql.append(" AND d.type <> '").append(HandlerDocuments.TypeDocumentsImpresion).append("'");
       	sql.append(" AND ((wf.statu = '"+HandlerWorkFlows.vencido+"' ");//el flujo debe estar vencido
        sql.append(" AND uw.statu =  '"+HandlerWorkFlows.wfuVencido+"')");
       	sql.append(" OR (wf.statu = '"+HandlerWorkFlows.expires+"' ");//el flujo debe estar expirado
        sql.append(" AND uw.statu =  '"+HandlerWorkFlows.wfuExpired+"'))");
        sql.append(" AND wf.type = '0' "); //Flujo de revision
        if(!ToolsHTML.isEmptyOrNull(dateFrom) && dateFrom.length()>9 ){
    		switch (Constants.MANEJADOR_ACTUAL) {
    		case Constants.MANEJADOR_MSSQL:
               	sql.append(" AND wf.dateExpire >= Convert(datetime,'").append(dateFrom).append("',120) ");
    			break;
    		case Constants.MANEJADOR_POSTGRES:
    			sql.append(" AND wf.dateExpire >= CAST('").append(dateFrom).append("' AS timestamp) ");
    			break;
    		case Constants.MANEJADOR_MYSQL:
    			sql.append(" AND wf.dateExpire >= TIMESTAMP('").append(dateFrom).append("') ");
    			break;
    		}
        }
        if(!ToolsHTML.isEmptyOrNull(dateTo) && dateTo.length()>9){
    		switch (Constants.MANEJADOR_ACTUAL) {
    		case Constants.MANEJADOR_MSSQL:
           		sql.append(" AND wf.dateExpire <= Convert(datetime,'").append(dateTo).append("',120) ");
    			break;
    		case Constants.MANEJADOR_POSTGRES:
    			sql.append(" AND wf.dateExpire <= CAST('").append(dateTo).append("' AS timestamp) ");
    			break;
    		case Constants.MANEJADOR_MYSQL:
    			sql.append(" AND wf.dateExpire <= TIMESTAMP('").append(dateTo).append("') ");
    			break;
    		}
        }
        //log.debug("getNumWorkflowsReviewExpired sql = " + sql.toString());
        ////System.out.println("---getNumWorkflowsReviewExpired sql = " + sql.toString());
        Connection con = null;
        PreparedStatement st = null;
        ResultSet rs = null;
        
        try{
        	con = JDBCUtil.getConnection(Thread.currentThread().getStackTrace()[1].getMethodName());
	        st = con.prepareStatement(JDBCUtil.replaceCastMysql(sql.toString()));
			rs = st.executeQuery();
			
			if (rs.next()) {
				num = rs.getInt("num");
			}
        }catch(Exception e){
        	//System.out.println("Error en getNumWorkflowsApprovalExpired: " + e);
        	log.error(e.getMessage() + " SQL: " + sql, e);
        } finally{
        	try {
				rs.close();
			} catch (Exception ee) {
				// TODO: handle exception
			}
        	try {
				st.close();
			} catch (Exception ee) {
				// TODO: handle exception
			}
        	try {
				con.close();
			} catch (Exception ee) {
				// TODO: handle exception
			}
        }
        return num;
    }

    //Consulta numero de FTPs emitidos
    public static int getNumFtp(String nameUser, String dateFrom, String dateTo) throws Exception{
    	int num = 0;
        StringBuffer sql = new StringBuffer();
        //Cuenta por cada actividad de FTP generado
        //sql.append("SELECT count(DISTINCT(fw.idworkflow)) as num FROM documents d, flexworkflow fw ");
        //Cuenta por cada FTP generado, independientemente del numero de actividades que incluya
        sql.append("SELECT count(DISTINCT(fw.idflexflow)) as num FROM documents d, flexworkflow fw ");
        sql.append(" WHERE d.active = '1' ");
        if(!ToolsHTML.isEmptyOrNull(nameUser)){
        	sql.append(" AND fw.owner = '" + nameUser + "' ");
        }
        sql.append(" AND d.type <> '").append(HandlerDocuments.TypeDocumentsImpresion).append("'");
        sql.append(" AND fw.idDocument = d.numGen ");
        if(!ToolsHTML.isEmptyOrNull(dateFrom) && dateFrom.length()>9){
    		switch (Constants.MANEJADOR_ACTUAL) {
    		case Constants.MANEJADOR_MSSQL:
            	sql.append(" AND fw.DateCreation >= Convert(datetime,'").append(dateFrom).append("',120) ");
    			break;
    		case Constants.MANEJADOR_POSTGRES:
    			sql.append(" AND fw.DateCreation >= CAST('").append(dateFrom).append("' AS timestamp) ");
    			break;
    		case Constants.MANEJADOR_MYSQL:
    			sql.append(" AND fw.DateCreation >= TIMESTAMP('").append(dateFrom).append("') ");
    			break;
    		}
        }
        if(!ToolsHTML.isEmptyOrNull(dateTo) && dateTo.length()>9){
    		switch (Constants.MANEJADOR_ACTUAL) {
    		case Constants.MANEJADOR_MSSQL:
            	sql.append(" AND fw.DateCreation <= Convert(datetime,'").append(dateTo).append("',120) ");
    			break;
    		case Constants.MANEJADOR_POSTGRES:
    			sql.append(" AND fw.DateCreation <= CAST('").append(dateTo).append("' AS timestamp) ");
    			break;
    		case Constants.MANEJADOR_MYSQL:
    			sql.append(" AND fw.DateCreation <= TIMESTAMP('").append(dateTo).append("') ");
    			break;
    		}
        }
        //log.debug("getNumFtp sql = " + sql.toString());
        ////System.out.println("---getNumFtp sql = " + sql.toString());
        Connection con = null;
        PreparedStatement st = null;
        ResultSet rs = null;
        
        try{
        	con = JDBCUtil.getConnection(Thread.currentThread().getStackTrace()[1].getMethodName());
	        st = con.prepareStatement(JDBCUtil.replaceCastMysql(sql.toString()));
			rs = st.executeQuery();
			
			if (rs.next()) {
				num = rs.getInt("num");
			}
        }catch(Exception e){
        	//System.out.println("Error en getNumWorkflowsApprovalExpired: " + e);
        	log.error(e.getMessage() + " SQL: " + sql, e);
        } finally{
        	try {
				rs.close();
			} catch (Exception ee) {
				// TODO: handle exception
			}
        	try {
				st.close();
			} catch (Exception ee) {
				// TODO: handle exception
			}
        	try {
				con.close();
			} catch (Exception ee) {
				// TODO: handle exception
			}
        }
        
        return num;
    }

    //Consulta numero de FTPs pendientes
    public static int getNumFtpPending(String nameUser, String dateFrom, String dateTo) throws Exception{
    	int num = 0;
        StringBuffer sql = new StringBuffer();
        sql.append("SELECT count(DISTINCT(ufw.idFlexWF)) as num FROM documents d, flexworkflow fw, user_flexworkflows ufw ");
        sql.append(" WHERE d.active = '1' ");
        sql.append(" AND fw.idDocument = d.numGen ");
        sql.append(" AND fw.idWorkFlow = ufw.idWorkFlow ");
        if(!ToolsHTML.isEmptyOrNull(nameUser)){
        	sql.append(" AND ufw.idUser = '" + nameUser + "' ");
        }
        sql.append(" AND d.type <> '").append(HandlerDocuments.TypeDocumentsImpresion).append("'");
        sql.append(" AND ( (ufw.statu =  '"+HandlerWorkFlows.wfuPending+"'");
        if(!ToolsHTML.isEmptyOrNull(dateTo) && dateTo.length()>9){
    		switch (Constants.MANEJADOR_ACTUAL) {
    		case Constants.MANEJADOR_MSSQL:
            	sql.append(" AND fw.DateCreation <= Convert(datetime,'").append(dateTo).append("',120) ");
    			break;
    		case Constants.MANEJADOR_POSTGRES:
    			sql.append(" AND fw.DateCreation <= CAST('").append(dateTo).append("' AS timestamp) ");
    			break;
    		case Constants.MANEJADOR_MYSQL:
    			sql.append(" AND fw.DateCreation <= TIMESTAMP('").append(dateTo).append("') ");
    			break;
    		}
        }
        sql.append(" ) OR ( 1=1 ");
        if(!ToolsHTML.isEmptyOrNull(dateFrom) && dateFrom.length()>9){
    		switch (Constants.MANEJADOR_ACTUAL) {
    		case Constants.MANEJADOR_MSSQL:
            	sql.append(" AND ufw.dateReplied >= Convert(datetime,'").append(dateFrom).append("',120) ");
    			break;
    		case Constants.MANEJADOR_POSTGRES:
    			sql.append(" AND ufw.dateReplied >= CAST('").append(dateFrom).append("' AS timestamp) ");
    			break;
    		case Constants.MANEJADOR_MYSQL:
    			sql.append(" AND ufw.dateReplied >= TIMESTAMP('").append(dateFrom).append("') ");
    			break;
    		}
        }
        if(!ToolsHTML.isEmptyOrNull(dateTo) && dateTo.length()>9){
    		switch (Constants.MANEJADOR_ACTUAL) {
    		case Constants.MANEJADOR_MSSQL:
            	sql.append(" AND ufw.dateReplied <= Convert(datetime,'").append(dateTo).append("',120) ");
    			break;
    		case Constants.MANEJADOR_POSTGRES:
    			sql.append(" AND ufw.dateReplied <= CAST('").append(dateTo).append("' AS timestamp) ");
    			break;
    		case Constants.MANEJADOR_MYSQL:
    			sql.append(" AND ufw.dateReplied <= TIMESTAMP('").append(dateTo).append("') ");
    			break;
    		}
        }
        sql.append(" ) )");

        //log.debug("getNumFtpPending sql = " + sql.toString());
        ////System.out.println("---getNumFtpPending sql = " + sql.toString());
        Connection con = null;
        PreparedStatement st = null;
        ResultSet rs = null;
        
        try{
        	con = JDBCUtil.getConnection(Thread.currentThread().getStackTrace()[1].getMethodName());
	        st = con.prepareStatement(JDBCUtil.replaceCastMysql(sql.toString()));
			rs = st.executeQuery();
			
			if (rs.next()) {
				num = rs.getInt("num");
			}
        }catch(Exception e){
        	//System.out.println("Error en getNumWorkflowsApprovalExpired: " + e);
        	log.error(e.getMessage() + " SQL: " + sql, e);
        } finally{
        	try {
				rs.close();
			} catch (Exception ee) {
				// TODO: handle exception
			}
        	try {
				st.close();
			} catch (Exception ee) {
				// TODO: handle exception
			}
        	try {
				con.close();
			} catch (Exception ee) {
				// TODO: handle exception
			}
        }
        
        return num;
    }

    //Consulta numero de FTPs aprobados
    public static int getNumFtpApproved(String nameUser, String dateFrom, String dateTo) throws Exception{
    	int num = 0;
        StringBuffer sql = new StringBuffer();
        sql.append("SELECT count(DISTINCT(ufw.idFlexWF)) as num FROM documents d, flexworkflow fw, user_flexworkflows ufw ");
        sql.append(" WHERE d.active = '1' ");
        sql.append(" AND ufw.statu =  '"+HandlerWorkFlows.wfuAcepted+"'");
        sql.append(" AND fw.idWorkFlow = ufw.idWorkFlow ");
        sql.append(" AND fw.idDocument = d.numGen ");
        if(!ToolsHTML.isEmptyOrNull(nameUser)){
        	sql.append(" AND ufw.idUser = '" + nameUser + "' ");
        }
        sql.append(" AND d.type <> '").append(HandlerDocuments.TypeDocumentsImpresion).append("'");
        if(!ToolsHTML.isEmptyOrNull(dateFrom) && dateFrom.length()>9 ){
       		switch (Constants.MANEJADOR_ACTUAL) {
       		case Constants.MANEJADOR_MSSQL:
               	sql.append(" AND ufw.dateReplied >= Convert(datetime,'").append(dateFrom).append("',120) ");
       			break;
       		case Constants.MANEJADOR_POSTGRES:
       			sql.append(" AND ufw.dateReplied >= CAST('").append(dateFrom).append("' AS timestamp) ");
       			break;
       		case Constants.MANEJADOR_MYSQL:
       			sql.append(" AND ufw.dateReplied >= TIMESTAMP('").append(dateFrom).append("') ");
       			break;
       		}
        }
        if(!ToolsHTML.isEmptyOrNull(dateTo) && dateTo.length()>9){
       		switch (Constants.MANEJADOR_ACTUAL) {
       		case Constants.MANEJADOR_MSSQL:
           		sql.append(" AND ufw.dateReplied <= Convert(datetime,'").append(dateTo).append("',120) ");
       			break;
       		case Constants.MANEJADOR_POSTGRES:
       			sql.append(" AND ufw.dateReplied <= CAST('").append(dateTo).append("' AS timestamp) ");
       			break;
       		case Constants.MANEJADOR_MYSQL:
       			sql.append(" AND ufw.dateReplied <= TIMESTAMP('").append(dateTo).append("') ");
       			break;
       		}
        }
        //log.debug("getNumFtpApproved sql = " + sql.toString());
        ////System.out.println("---getNumFtpApproved sql = " + sql.toString());
        Connection con = null;
        PreparedStatement st = null;
        ResultSet rs = null;
        
        try{
        	con = JDBCUtil.getConnection(Thread.currentThread().getStackTrace()[1].getMethodName());
	        st = con.prepareStatement(JDBCUtil.replaceCastMysql(sql.toString()));
			rs = st.executeQuery();
			
			if (rs.next()) {
				num = rs.getInt("num");
			}
        }catch(Exception e){
        	//System.out.println("Error en getNumWorkflowsApprovalExpired: " + e);
        	log.error(e.getMessage() + " SQL: " + sql, e);
        } finally{
        	try {
				rs.close();
			} catch (Exception ee) {
				// TODO: handle exception
			}
        	try {
				st.close();
			} catch (Exception ee) {
				// TODO: handle exception
			}
        	try {
				con.close();
			} catch (Exception ee) {
				// TODO: handle exception
			}
        }
        
        return num;
    }

    //Consulta numero de FTPs cancelados / rechazados
    public static int getNumFtpCanceled(String nameUser, String dateFrom, String dateTo) throws Exception{
    	int num = 0;
        StringBuffer sql = new StringBuffer();
        sql.append("SELECT count(DISTINCT(ufw.idFlexWF)) as num FROM documents d, flexworkflow fw, user_flexworkflows ufw ");
        sql.append(" WHERE d.active = '1' ");
        //sql.append(" AND (ufw.statu =  '"+HandlerWorkFlows.wfuCanceled+"' or uw.statu =  '"+HandlerWorkFlows.wfuCanceled2+"') ");
        sql.append(" AND (ufw.statu =  '"+HandlerWorkFlows.wfuCanceled+"' or ufw.statu =  '"+HandlerWorkFlows.wfuCanceled2+"'" );
        sql.append(" or ufw.statu =  '"+HandlerWorkFlows.wfurechazado+"' or ufw.statu =  '"+HandlerWorkFlows.wfuRejected+"') ");
        sql.append(" AND fw.idWorkFlow = ufw.idWorkFlow ");
        sql.append(" AND fw.idDocument = d.numGen ");
        if(!ToolsHTML.isEmptyOrNull(nameUser)){
        	sql.append(" AND ufw.idUser = '" + nameUser + "' ");
        }
        sql.append(" AND d.type <> '").append(HandlerDocuments.TypeDocumentsImpresion).append("'");
        if(!ToolsHTML.isEmptyOrNull(dateFrom) && dateFrom.length()>9 ){
       		switch (Constants.MANEJADOR_ACTUAL) {
       		case Constants.MANEJADOR_MSSQL:
               	sql.append(" AND ufw.dateReplied >= Convert(datetime,'").append(dateFrom).append("',120) ");
       			break;
       		case Constants.MANEJADOR_POSTGRES:
       			sql.append(" AND ufw.dateReplied >= CAST('").append(dateFrom).append("' AS timestamp) ");
       			break;
       		case Constants.MANEJADOR_MYSQL:
       			sql.append(" AND ufw.dateReplied >= TIMESTAMP('").append(dateFrom).append("') ");
       			break;
       		}
        }
        if(!ToolsHTML.isEmptyOrNull(dateTo) && dateTo.length()>9){
       		switch (Constants.MANEJADOR_ACTUAL) {
       		case Constants.MANEJADOR_MSSQL:
           		sql.append(" AND ufw.dateReplied <= Convert(datetime,'").append(dateTo).append("',120) ");
       			break;
       		case Constants.MANEJADOR_POSTGRES:
       			sql.append(" AND ufw.dateReplied <= CAST('").append(dateTo).append("' AS timestamp) ");
       			break;
       		case Constants.MANEJADOR_MYSQL:
       			sql.append(" AND ufw.dateReplied <= TIMESTAMP('").append(dateTo).append("') ");
       			break;
       		}
        }
        //log.debug("getNumFtpCanceled sql = " + sql.toString());
        ////System.out.println("---getNumFtpCanceled sql = " + sql.toString());
        Connection con = null;
        PreparedStatement st = null;
        ResultSet rs = null;
        
        try{
        	con = JDBCUtil.getConnection(Thread.currentThread().getStackTrace()[1].getMethodName());
	        st = con.prepareStatement(JDBCUtil.replaceCastMysql(sql.toString()));
			rs = st.executeQuery();
			
			if (rs.next()) {
				num = rs.getInt("num");
			}
        }catch(Exception e){
        	//System.out.println("Error en getNumWorkflowsApprovalExpired: " + e);
        	log.error(e.getMessage() + " SQL: " + sql, e);
        } finally{
        	try {
				rs.close();
			} catch (Exception ee) {
				// TODO: handle exception
			}
        	try {
				st.close();
			} catch (Exception ee) {
				// TODO: handle exception
			}
        	try {
				con.close();
			} catch (Exception ee) {
				// TODO: handle exception
			}
        }
        
        return num;
    }

    //Consulta numero de FTPs expirados
    public static int getNumFtpExpired(String nameUser, String dateFrom, String dateTo) throws Exception{
    	int num = 0;
        StringBuffer sql = new StringBuffer();
        sql.append("SELECT count(DISTINCT(ufw.idFlexWF)) as num FROM documents d, flexworkflow fw, user_flexworkflows ufw ");
        sql.append(" WHERE d.active = '1' ");
        sql.append(" AND ((ufw.statu = '"+HandlerWorkFlows.wfuExpired+"' and ufw.result = '"+HandlerWorkFlows.expires+"') or (ufw.statu = '"+HandlerWorkFlows.wfuVencido + "' and ufw.result = '"+HandlerWorkFlows.wfuVencido+"'))");
        sql.append(" AND fw.idWorkFlow = ufw.idWorkFlow ");
        sql.append(" AND fw.idDocument = d.numGen ");
        if(!ToolsHTML.isEmptyOrNull(nameUser)){
        	sql.append(" AND ufw.idUser = '" + nameUser + "' ");
        }
        sql.append(" AND d.type <> '").append(HandlerDocuments.TypeDocumentsImpresion).append("'");
        if(!ToolsHTML.isEmptyOrNull(dateFrom) && dateFrom.length()>9 ){
    		switch (Constants.MANEJADOR_ACTUAL) {
    		case Constants.MANEJADOR_MSSQL:
               	sql.append(" AND fw.dateExpire >= Convert(datetime,'").append(dateFrom).append("',120) ");
    			break;
    		case Constants.MANEJADOR_POSTGRES:
    			sql.append(" AND fw.dateExpire >= CAST('").append(dateFrom).append("' AS timestamp) ");
    			break;
    		case Constants.MANEJADOR_MYSQL:
    			sql.append(" AND fw.dateExpire >= TIMESTAMP('").append(dateFrom).append("') ");
    			break;
    		}
        }
        if(!ToolsHTML.isEmptyOrNull(dateTo) && dateTo.length()>9){
    		switch (Constants.MANEJADOR_ACTUAL) {
    		case Constants.MANEJADOR_MSSQL:
           		sql.append(" AND fw.dateExpire <= Convert(datetime,'").append(dateTo).append("',120) ");
    			break;
    		case Constants.MANEJADOR_POSTGRES:
    			sql.append(" AND fw.dateExpire <= CAST('").append(dateTo).append("' AS timestamp) ");
    			break;
    		case Constants.MANEJADOR_MYSQL:
    			sql.append(" AND fw.dateExpire <= TIMESTAMP('").append(dateTo).append("') ");
    			break;
    		}
        }
        //log.debug("getNumFtpExpired sql = " + sql.toString());
        ////System.out.println("---getNumFtpExpired sql = " + sql.toString());
        Connection con = null;
        PreparedStatement st = null;
        ResultSet rs = null;
        
        try{
        	con = JDBCUtil.getConnection(Thread.currentThread().getStackTrace()[1].getMethodName());
	        st = con.prepareStatement(JDBCUtil.replaceCastMysql(sql.toString()));
			rs = st.executeQuery();
			
			if (rs.next()) {
				num = rs.getInt("num");
			}
        }catch(Exception e){
        	//System.out.println("Error en getNumWorkflowsApprovalExpired: " + e);
        	log.error(e.getMessage() + " SQL: " + sql, e);
        } finally{
        	try {
				rs.close();
			} catch (Exception ee) {
				// TODO: handle exception
			}
        	try {
				st.close();
			} catch (Exception ee) {
				// TODO: handle exception
			}
        	try {
				con.close();
			} catch (Exception ee) {
				// TODO: handle exception
			}
        }
        
        return num;
    }

    //Consulta numero de FTPs completados
    public static int getNumFtpCompleted(String nameUser, String dateFrom, String dateTo) throws Exception{
    	int num = 0;
        StringBuffer sql = new StringBuffer();
        sql.append("SELECT count(DISTINCT(ufw.idFlexWF)) as num FROM documents d, flexworkflow fw, user_flexworkflows ufw ");
        sql.append(" WHERE d.active = '1' ");
        sql.append(" AND (ufw.result =  '"+HandlerWorkFlows.wfuCompleted+"') ");
        sql.append(" AND fw.idWorkFlow = ufw.idWorkFlow ");
        sql.append(" AND fw.idDocument = d.numGen ");
        if(!ToolsHTML.isEmptyOrNull(nameUser)){
        	sql.append(" AND ufw.idUser = '" + nameUser + "' ");
        }
        sql.append(" AND d.type <> '").append(HandlerDocuments.TypeDocumentsImpresion).append("'");
        if(!ToolsHTML.isEmptyOrNull(dateFrom) && dateFrom.length()>9 ){
    		switch (Constants.MANEJADOR_ACTUAL) {
    		case Constants.MANEJADOR_MSSQL:
               	sql.append(" AND fw.dateCompleted >= Convert(datetime,'").append(dateFrom).append("',120) ");
    			break;
    		case Constants.MANEJADOR_POSTGRES:
    			sql.append(" AND fw.dateCompleted >= CAST('").append(dateFrom).append("' AS timestamp) ");
    			break;
    		case Constants.MANEJADOR_MYSQL:
    			sql.append(" AND fw.dateCompleted >= TIMESTAMP('").append(dateFrom).append("') ");
    			break;
    		}
        }
        if(!ToolsHTML.isEmptyOrNull(dateTo) && dateTo.length()>9){
    		switch (Constants.MANEJADOR_ACTUAL) {
    		case Constants.MANEJADOR_MSSQL:
           		sql.append(" AND fw.dateCompleted <= Convert(datetime,'").append(dateTo).append("',120) ");
    			break;
    		case Constants.MANEJADOR_POSTGRES:
    			sql.append(" AND fw.dateCompleted <= CAST('").append(dateTo).append("' AS timestamp) ");
    			break;
    		case Constants.MANEJADOR_MYSQL:
    			sql.append(" AND fw.dateCompleted <= TIMESTAMP('").append(dateTo).append("') ");
    			break;
    		}
        }
        //log.debug("getNumFtpCompleted sql = " + sql.toString());
        ////System.out.println("---getNumFtpCompleted sql = " + sql.toString());
        Connection con = null;
        PreparedStatement st = null;
        ResultSet rs = null;
        
        try{
        	con = JDBCUtil.getConnection(Thread.currentThread().getStackTrace()[1].getMethodName());
	        st = con.prepareStatement(JDBCUtil.replaceCastMysql(sql.toString()));
			rs = st.executeQuery();
			
			if (rs.next()) {
				num = rs.getInt("num");
			}
        }catch(Exception e){
        	//System.out.println("Error en getNumWorkflowsApprovalExpired: " + e);
        	log.error(e.getMessage() + " SQL: " + sql, e);
        } finally{
        	try {
				rs.close();
			} catch (Exception ee) {
				// TODO: handle exception
			}
        	try {
				st.close();
			} catch (Exception ee) {
				// TODO: handle exception
			}
        	try {
				con.close();
			} catch (Exception ee) {
				// TODO: handle exception
			}
        }
        
        return num;
    }

    //Consulta numero de SACOPs creadas
    public static int getNumSacopsCreated(long idPerson, String dateFrom, String dateTo) throws Exception{
    	int num = 0;
        StringBuffer sql = new StringBuffer();
        sql.append("SELECT count(DISTINCT(s.idplanillasacop1)) as num FROM tbl_planillasacop1 s ");
        sql.append(" WHERE s.active = '1' ");
        if(idPerson>0){
        	sql.append(" AND s.emisor = " + idPerson );
        }

    	sql.append(" AND s.estado != '" + LoadSacop.edoBorrador + "'");
        if(!ToolsHTML.isEmptyOrNull(dateFrom) && dateFrom.length()>9 ){
    		switch (Constants.MANEJADOR_ACTUAL) {
    		case Constants.MANEJADOR_MSSQL:
               	sql.append(" AND s.fechaemision >= Convert(datetime,'").append(dateFrom).append("',120) ");
    			break;
    		case Constants.MANEJADOR_POSTGRES:
    			sql.append(" AND s.fechaemision >= CAST('").append(dateFrom).append("' AS timestamp) ");
    			break;
    		case Constants.MANEJADOR_MYSQL:
    			sql.append(" AND s.fechaemision >= TIMESTAMP('").append(dateFrom).append("') ");
    			break;
    		}
        }
        if(!ToolsHTML.isEmptyOrNull(dateTo) && dateTo.length()>9){
    		switch (Constants.MANEJADOR_ACTUAL) {
    		case Constants.MANEJADOR_MSSQL:
           		sql.append(" AND s.fechaemision <= Convert(datetime,'").append(dateTo).append("',120) ");
    			break;
    		case Constants.MANEJADOR_POSTGRES:
    			sql.append(" AND s.fechaemision <= CAST('").append(dateTo).append("' AS timestamp) ");
    			break;
    		case Constants.MANEJADOR_MYSQL:
    			sql.append(" AND s.fechaemision <= TIMESTAMP('").append(dateTo).append("') ");
    			break;
    		}
        }
        //log.debug("getNumSacopsCreated sql = " + sql.toString());
        ////System.out.println("---getNumSacopsCreated sql = " + sql.toString());
        Connection con = null;
        PreparedStatement st = null;
        ResultSet rs = null;
        
        try{
        	con = JDBCUtil.getConnection(Thread.currentThread().getStackTrace()[1].getMethodName());
	        st = con.prepareStatement(JDBCUtil.replaceCastMysql(sql.toString()));
			rs = st.executeQuery();
			
			if (rs.next()) {
				num = rs.getInt("num");
			}
        }catch(Exception e){
        	//System.out.println("Error en getNumWorkflowsApprovalExpired: " + e);
        	log.error(e.getMessage() + " SQL: " + sql, e);
        } finally{
        	try {
				rs.close();
			} catch (Exception ee) {
				// TODO: handle exception
			}
        	try {
				st.close();
			} catch (Exception ee) {
				// TODO: handle exception
			}
        	try {
				con.close();
			} catch (Exception ee) {
				// TODO: handle exception
			}
        }
        
        return num;
    }

    //Consulta numero de SACOPs con responsable de area
    public static int getNumSacopsIsResponsible(long idPerson, String dateFrom, String dateTo) throws Exception{
    	int num = 0;
        StringBuffer sql = new StringBuffer();
        sql.append("SELECT count(DISTINCT(s.idplanillasacop1)) as num FROM tbl_planillasacop1 s ");
        sql.append(" WHERE s.active = '1' ");
        if(idPerson>0){
        	sql.append(" AND s.respblearea = " + idPerson );
        }

    	sql.append(" AND s.estado != '" + LoadSacop.edoBorrador + "'");
        if(!ToolsHTML.isEmptyOrNull(dateFrom) && dateFrom.length()>9 ){
    		switch (Constants.MANEJADOR_ACTUAL) {
    		case Constants.MANEJADOR_MSSQL:
               	sql.append(" AND s.fechaemision >= Convert(datetime,'").append(dateFrom).append("',120) ");
    			break;
    		case Constants.MANEJADOR_POSTGRES:
    			sql.append(" AND s.fechaemision >= CAST('").append(dateFrom).append("' AS timestamp) ");
    			break;
    		case Constants.MANEJADOR_MYSQL:
    			sql.append(" AND s.fechaemision >= TIMESTAMP('").append(dateFrom).append("') ");
    			break;
    		}
    	}
        if(!ToolsHTML.isEmptyOrNull(dateTo) && dateTo.length()>9){
    		switch (Constants.MANEJADOR_ACTUAL) {
    		case Constants.MANEJADOR_MSSQL:
           		sql.append(" AND s.fechaemision <= Convert(datetime,'").append(dateTo).append("',120) ");
    			break;
    		case Constants.MANEJADOR_POSTGRES:
    			sql.append(" AND s.fechaemision <= CAST('").append(dateTo).append("' AS timestamp) ");
    			break;
    		case Constants.MANEJADOR_MYSQL:
    			sql.append(" AND s.fechaemision <= TIMESTAMP('").append(dateTo).append("') ");
    			break;
    		}
        }
        //log.debug("getNumSacopsIsResponsible sql = " + sql.toString());
        ////System.out.println("---getNumSacopsIsResponsible sql = " + sql.toString());
        Connection con = null;
        PreparedStatement st = null;
        ResultSet rs = null;
        
        try{
        	con = JDBCUtil.getConnection(Thread.currentThread().getStackTrace()[1].getMethodName());
	        st = con.prepareStatement(JDBCUtil.replaceCastMysql(sql.toString()));
			rs = st.executeQuery();
			
			if (rs.next()) {
				num = rs.getInt("num");
			}
        }catch(Exception e){
        	//System.out.println("Error en getNumWorkflowsApprovalExpired: " + e);
        	log.error(e.getMessage() + " SQL: " + sql, e);
        } finally{
        	try {
				rs.close();
			} catch (Exception ee) {
				// TODO: handle exception
			}
        	try {
				st.close();
			} catch (Exception ee) {
				// TODO: handle exception
			}
        	try {
				con.close();
			} catch (Exception ee) {
				// TODO: handle exception
			}
        }
        
        return num;
    }

    //Consulta numero de SACOPs en las que participa
    public static int getNumSacopsParticipate(long idPerson, String dateFrom, String dateTo) throws Exception{
    	int num = 0;
        StringBuffer sql = new StringBuffer();
        sql.append("SELECT count(sp.idPerson) as num FROM tbl_planillasacop1 s, tbl_planillasacopaccion sa, tbl_sacopaccionporpersona sp, person p ");
        sql.append(" WHERE s.active = '1' ");
        if(idPerson>0){
        	sql.append(" AND sp.idPerson = '" + idPerson + "' " );
        }
        sql.append(" AND s.estado != '" + LoadSacop.edoBorrador + "'");
    	sql.append(" AND s.idplanillasacop1 = sa.idplanillasacop1 ");
    	sql.append(" AND sa.idplanillasacopaccion = sp.idplanillasacopaccion ");
    	sql.append(" AND sp.idPerson = p.idPerson ");
    	sql.append(" AND p.accountActive = '1' ");
        if(!ToolsHTML.isEmptyOrNull(dateFrom) && dateFrom.length()>9 ){
    		switch (Constants.MANEJADOR_ACTUAL) {
    		case Constants.MANEJADOR_MSSQL:
               	sql.append(" AND sa.fecha >= Convert(datetime,'").append(dateFrom).append("',120) ");
    			break;
    		case Constants.MANEJADOR_POSTGRES:
    			sql.append(" AND CAST(sa.fecha AS timestamp) >= CAST('").append(dateFrom).append("' AS timestamp) ");
    			break;
    		case Constants.MANEJADOR_MYSQL:
    			sql.append(" AND CAST(sa.fecha AS timestamp) >= TIMESTAMP('").append(dateFrom).append("') ");
    			break;
    		}
        }
        if(!ToolsHTML.isEmptyOrNull(dateTo) && dateTo.length()>9){
    		switch (Constants.MANEJADOR_ACTUAL) {
    		case Constants.MANEJADOR_MSSQL:
           		sql.append(" AND sa.fecha <= Convert(datetime,'").append(dateTo).append("',120) ");
    			break;
    		case Constants.MANEJADOR_POSTGRES:
    			sql.append(" AND CAST(sa.fecha AS timestamp) <= CAST('").append(dateTo).append("' AS timestamp) ");
    			break;
    		case Constants.MANEJADOR_MYSQL:
    			sql.append(" AND sa.fecha <= '").append(dateTo).append("' ");
    			break;
    		}
        }
        //log.debug("getNumSacopsParticipate sql = " + sql.toString());
        ////System.out.println("---getNumSacopsParticipate sql = " + sql.toString());
        Connection con = null;
        PreparedStatement st = null;
        ResultSet rs = null;
        
        try{
        	con = JDBCUtil.getConnection(Thread.currentThread().getStackTrace()[1].getMethodName());
	        st = con.prepareStatement(JDBCUtil.replaceCastMysql(sql.toString()));
			rs = st.executeQuery();
			
			if (rs.next()) {
				num = rs.getInt("num");
			}
        }catch(Exception e){
        	//System.out.println("Error en getNumWorkflowsApprovalExpired: " + e);
        	log.error(e.getMessage() + " SQL: " + sql, e);
        } finally{
        	try {
				rs.close();
			} catch (Exception ee) {
				// TODO: handle exception
			}
        	try {
				st.close();
			} catch (Exception ee) {
				// TODO: handle exception
			}
        	try {
				con.close();
			} catch (Exception ee) {
				// TODO: handle exception
			}
        }
        return num;
    }

    //Consulta numero de SACOPs cerradas por responsable de area
    public static int getNumSacopsClosed(long idPerson, String dateFrom, String dateTo) throws Exception{
    	int num = 0;
        StringBuffer sql = new StringBuffer();
        sql.append("SELECT count(DISTINCT(s.idplanillasacop1)) as num FROM tbl_planillasacop1 s ");
        sql.append(" WHERE s.active = '1' ");
        if(idPerson>0){
        	sql.append(" AND s.emisor = " + idPerson );
        }
        //sacop culminada exitosamente
    	sql.append(" AND s.rechazoapruebo = '" + LoadSacop.acpetadoSi + "' ");
    	sql.append(" AND s.accionesEstablecidas = '" + LoadSacop.acpetadoSi + "' ");
    	sql.append(" AND s.estado = '" + LoadSacop.edoCerrado + "' ");

        if(!ToolsHTML.isEmptyOrNull(dateFrom) && dateFrom.length()>9 ){
    		switch (Constants.MANEJADOR_ACTUAL) {
    		case Constants.MANEJADOR_MSSQL:
               	sql.append(" AND s.fechaemision >= Convert(datetime,'").append(dateFrom).append("',120) ");
    			break;
    		case Constants.MANEJADOR_POSTGRES:
    			sql.append(" AND s.fechaemision >= CAST('").append(dateFrom).append("' AS timestamp) ");
    			break;
    		case Constants.MANEJADOR_MYSQL:
    			sql.append(" AND s.fechaemision >= TIMESTAMP('").append(dateFrom).append("') ");
    			break;
    		}
        }
        if(!ToolsHTML.isEmptyOrNull(dateTo) && dateTo.length()>9){
    		switch (Constants.MANEJADOR_ACTUAL) {
    		case Constants.MANEJADOR_MSSQL:
           		sql.append(" AND s.fechaemision <= Convert(datetime,'").append(dateTo).append("',120) ");
    			break;
    		case Constants.MANEJADOR_POSTGRES:
    			sql.append(" AND s.fechaemision <= CAST('").append(dateTo).append("' AS timestamp) ");
    			break;
    		case Constants.MANEJADOR_MYSQL:
    			sql.append(" AND s.fechaemision <= TIMESTAMP('").append(dateTo).append("') ");
    			break;
    		}
        }
        //log.debug("getNumSacopsClosed sql = " + sql.toString());
        ////System.out.println("---getNumSacopsClosed sql = " + sql.toString());
        Connection con = null;
        PreparedStatement st = null;
        ResultSet rs = null;
        
        try{
        	con = JDBCUtil.getConnection(Thread.currentThread().getStackTrace()[1].getMethodName());
	        st = con.prepareStatement(JDBCUtil.replaceCastMysql(sql.toString()));
			rs = st.executeQuery();
			
			if (rs.next()) {
				num = rs.getInt("num");
			}
        }catch(Exception e){
        	//System.out.println("Error en getNumWorkflowsApprovalExpired: " + e);
        	log.error(e.getMessage() + " SQL: " + sql, e);
        } finally{
        	try {
				rs.close();
			} catch (Exception ee) {
				// TODO: handle exception
			}
        	try {
				st.close();
			} catch (Exception ee) {
				// TODO: handle exception
			}
        	try {
				con.close();
			} catch (Exception ee) {
				// TODO: handle exception
			}
        }
        
        return num;
    }

    //Consulta número de solicitudes de impresión
    public static int getNumPrinting(String nameUser, String dateFrom, String dateTo) throws Exception{
    	int num = 0;
        StringBuffer sql = new StringBuffer();
        sql.append("select count(DISTINCT(numsolicitud)) as num from documents d, person p, tbl_solicitudimpresion si ");
        sql.append(" WHERE d.active = '1' ");
        sql.append(" AND si.solicitante = p.idPerson");
        if(!ToolsHTML.isEmptyOrNull(nameUser)){
        	sql.append(" AND p.nameUser = '" + nameUser + "'");
        }
        sql.append(" AND si.numgen = d.numGen");
        sql.append(" AND p.accountActive='1' ");
        if(!ToolsHTML.isEmptyOrNull(dateFrom) && dateFrom.length()>9){
    		switch (Constants.MANEJADOR_ACTUAL) {
    		case Constants.MANEJADOR_MSSQL:
            	sql.append(" AND si.datesolicitud >= Convert(datetime,'").append(dateFrom).append("',120) ");
    			break;
    		case Constants.MANEJADOR_POSTGRES:
    			sql.append(" AND si.datesolicitud >= CAST('").append(dateFrom).append("' AS timestamp) ");
    			break;
    		case Constants.MANEJADOR_MYSQL:
    			sql.append(" AND si.datesolicitud >= TIMESTAMP('").append(dateFrom).append("') ");
    			break;
    		}
    	}
        if(!ToolsHTML.isEmptyOrNull(dateTo) && dateTo.length()>9){
    		switch (Constants.MANEJADOR_ACTUAL) {
    		case Constants.MANEJADOR_MSSQL:
            	sql.append(" AND si.datesolicitud <= Convert(datetime,'").append(dateTo).append("',120) ");
    			break;
    		case Constants.MANEJADOR_POSTGRES:
    			sql.append(" AND si.datesolicitud <= CAST('").append(dateTo).append("' AS timestamp) ");
    			break;
    		case Constants.MANEJADOR_MYSQL:
    			sql.append(" AND si.datesolicitud <= TIMESTAMP('").append(dateTo).append("') ");
    			break;
    		}
        }

        //log.debug("getNumPrinting sql = " + sql.toString());
        ////System.out.println("---getNumPrinting sql = " + sql.toString());
        Connection con = null;
        PreparedStatement st = null;
        ResultSet rs = null;
        
        try{
        	con = JDBCUtil.getConnection(Thread.currentThread().getStackTrace()[1].getMethodName());
	        st = con.prepareStatement(JDBCUtil.replaceCastMysql(sql.toString()));
			rs = st.executeQuery();
			
			if (rs.next()) {
				num = rs.getInt("num");
			}
        }catch(Exception e){
        	//System.out.println("Error en getNumWorkflowsApprovalExpired: " + e);
        	log.error(e.getMessage() + " SQL: " + sql, e);
        } finally{
        	try {
				rs.close();
			} catch (Exception ee) {
				// TODO: handle exception
			}
        	try {
				st.close();
			} catch (Exception ee) {
				// TODO: handle exception
			}
        	try {
				con.close();
			} catch (Exception ee) {
				// TODO: handle exception
			}
        }
        
        return num;
    }

    //Consulta número de solicitudes de impresión aprobadas
    public static int getNumPrintingApproved(String nameUser, String dateFrom, String dateTo) throws Exception{
    	int num = 0;
        StringBuffer sql = new StringBuffer();
        sql.append("SELECT count(DISTINCT(d.numGen)) as num FROM documents d,person p,versiondoc vd, workflows wf ");
        sql.append(" WHERE d.active = '1' ");
        if(!ToolsHTML.isEmptyOrNull(nameUser)){
        	sql.append(" AND d.owner = '" + nameUser + "'");
        	}
        sql.append(" AND p.nameUser = d.owner ");
        sql.append(" AND p.accountActive='1' ");
        sql.append(" AND vd.numDoc = d.numGen ");
        sql.append(" AND d.type = '").append(HandlerDocuments.TypeDocumentsImpresion).append("'");
        sql.append(" AND vd.numVer = (SELECT MAX(numVer) FROM versiondoc vdi WHERE vdi.numDoc = d.numGen)  ");
        sql.append(" AND wf.idWorkFlow = (SELECT MAX(idWorkFlow) FROM workflows wf1 WHERE wf1.idDocument = d.numGen ) ");
        sql.append(" AND wf.idVersion = vd.numVer ");
        sql.append(" AND wf.statu = '"+HandlerWorkFlows.response+"'"); // solicitud aceptada
        if(!ToolsHTML.isEmptyOrNull(dateFrom) && dateFrom.length()>9){
    		switch (Constants.MANEJADOR_ACTUAL) {
    		case Constants.MANEJADOR_MSSQL:
            	sql.append(" AND wf.dateCompleted >= Convert(datetime,'").append(dateFrom).append("',120) ");
    			break;
    		case Constants.MANEJADOR_POSTGRES:
    			sql.append(" AND wf.dateCompleted >= CAST('").append(dateFrom).append("' AS timestamp) ");
    			break;
    		case Constants.MANEJADOR_MYSQL:
    			sql.append(" AND wf.dateCompleted >= TIMESTAMP('").append(dateFrom).append("') ");
    			break;
    		}
        }
        if(!ToolsHTML.isEmptyOrNull(dateTo) && dateTo.length()>9){
    		switch (Constants.MANEJADOR_ACTUAL) {
    		case Constants.MANEJADOR_MSSQL:
            	sql.append(" AND wf.dateCompleted <= Convert(datetime,'").append(dateTo).append("',120) ");
    			break;
    		case Constants.MANEJADOR_POSTGRES:
    			sql.append(" AND wf.dateCompleted <= CAST('").append(dateTo).append("' AS timestamp) ");
    			break;
    		case Constants.MANEJADOR_MYSQL:
    			sql.append(" AND wf.dateCompleted <= TIMESTAMP('").append(dateTo).append("') ");
    			break;
    		}
        }
        //log.debug("getNumPrintingApproved sql = " + sql.toString());
        ////System.out.println("---getNumPrintingApproved sql = " + sql.toString());
        Connection con = null;
        PreparedStatement st = null;
        ResultSet rs = null;
        
        try{
        	con = JDBCUtil.getConnection(Thread.currentThread().getStackTrace()[1].getMethodName());
	        st = con.prepareStatement(JDBCUtil.replaceCastMysql(sql.toString()));
			rs = st.executeQuery();
			
			if (rs.next()) {
				num = rs.getInt("num");
			}
        }catch(Exception e){
        	//System.out.println("Error en getNumWorkflowsApprovalExpired: " + e);
        	log.error(e.getMessage() + " SQL: " + sql, e);
        } finally{
        	try {
				rs.close();
			} catch (Exception ee) {
				// TODO: handle exception
			}
        	try {
				st.close();
			} catch (Exception ee) {
				// TODO: handle exception
			}
        	try {
				con.close();
			} catch (Exception ee) {
				// TODO: handle exception
			}
        }
        
        return num;
    }

    //Consulta número de solicitudes de impresión rechazadas
    public static int getNumPrintingRejected(String nameUser, String dateFrom, String dateTo) throws Exception{
    	int num = 0;
        StringBuffer sql = new StringBuffer();
        sql.append("SELECT count(DISTINCT(d.numGen)) as num FROM documents d,person p,versiondoc vd, workflows wf ");
        sql.append(" WHERE d.active = '1' ");
        if(!ToolsHTML.isEmptyOrNull(nameUser)){
        	sql.append(" AND d.owner = '" + nameUser + "'");
        	}
        sql.append(" AND p.nameUser = d.owner ");
        sql.append(" AND p.accountActive='1' ");
        sql.append(" AND vd.numDoc = d.numGen ");
        sql.append(" AND d.type = '").append(HandlerDocuments.TypeDocumentsImpresion).append("'");
        sql.append(" AND vd.numVer = (SELECT MAX(numVer) FROM versiondoc vdi WHERE vdi.numDoc = d.numGen)  ");
        sql.append(" AND wf.idWorkFlow = (SELECT MAX(idWorkFlow) FROM workflows wf1 WHERE wf1.idDocument = d.numGen ) ");
        sql.append(" AND wf.idVersion = vd.numVer ");
        sql.append(" AND wf.statu = '"+HandlerWorkFlows.canceled+"'"); // solicitud rechazada
        if(!ToolsHTML.isEmptyOrNull(dateFrom) && dateFrom.length()>9){
    		switch (Constants.MANEJADOR_ACTUAL) {
    		case Constants.MANEJADOR_MSSQL:
            	sql.append(" AND wf.dateCompleted >= Convert(datetime,'").append(dateFrom).append("',120) ");
    			break;
    		case Constants.MANEJADOR_POSTGRES:
    			sql.append(" AND wf.dateCompleted >= CAST('").append(dateFrom).append("' AS timestamp) ");
    			break;
    		case Constants.MANEJADOR_MYSQL:
    			sql.append(" AND wf.dateCompleted >= TIMESTAMP('").append(dateFrom).append("') ");
    			break;
    		}
        }
        if(!ToolsHTML.isEmptyOrNull(dateTo) && dateTo.length()>9){
    		switch (Constants.MANEJADOR_ACTUAL) {
    		case Constants.MANEJADOR_MSSQL:
            	sql.append(" AND wf.dateCompleted <= Convert(datetime,'").append(dateTo).append("',120) ");
    			break;
    		case Constants.MANEJADOR_POSTGRES:
    			sql.append(" AND wf.dateCompleted <= CAST('").append(dateTo).append("' AS timestamp) ");
    			break;
    		case Constants.MANEJADOR_MYSQL:
    			sql.append(" AND wf.dateCompleted <= TIMESTAMP('").append(dateTo).append("') ");
    			break;
    		}
        }
        //log.debug("getNumPrintingRejected sql = " + sql.toString());
        ////System.out.println("---getNumPrintingRejected sql = " + sql.toString());
        Connection con = null;
        PreparedStatement st = null;
        ResultSet rs = null;
        
        try{
        	con = JDBCUtil.getConnection(Thread.currentThread().getStackTrace()[1].getMethodName());
	        st = con.prepareStatement(JDBCUtil.replaceCastMysql(sql.toString()));
			rs = st.executeQuery();
			
			if (rs.next()) {
				num = rs.getInt("num");
			}
        }catch(Exception e){
        	//System.out.println("Error en getNumWorkflowsApprovalExpired: " + e);
        	log.error(e.getMessage() + " SQL: " + sql, e);
        } finally{
        	try {
				rs.close();
			} catch (Exception ee) {
				// TODO: handle exception
			}
        	try {
				st.close();
			} catch (Exception ee) {
				// TODO: handle exception
			}
        	try {
				con.close();
			} catch (Exception ee) {
				// TODO: handle exception
			}
        }
        
        return num;
    }

    //Consulta número de solicitudes de impresión pendientes por aprobar
    public static int getNumPrintingPending(String nameUser, String dateFrom, String dateTo) throws Exception{
    	int num = 0;
        StringBuffer sql = new StringBuffer();
        sql.append("SELECT count(DISTINCT(d.numGen)) as num FROM documents d,person p,versiondoc vd, workflows wf ");
        if(!ToolsHTML.isEmptyOrNull(nameUser)){
        	sql.append(",user_workflows uw ");
        }
        sql.append(" WHERE d.active = '1' ");
        if(!ToolsHTML.isEmptyOrNull(nameUser)){
        	sql.append(" AND uw.idUser = '" + nameUser + "' ");
            //sql.append(" AND uw.statu =  '"+HandlerWorkFlows.wfuPending+"'");
            sql.append(" AND wf.idWorkFlow = uw.idWorkFlow ");
        }
        sql.append(" AND p.nameUser = d.owner ");
        sql.append(" AND p.accountActive='1' ");
        sql.append(" AND vd.numDoc = d.numGen ");
        sql.append(" AND d.type = '").append(HandlerDocuments.TypeDocumentsImpresion).append("'");
        sql.append(" AND vd.numVer = (SELECT MAX(numVer) FROM versiondoc vdi WHERE vdi.numDoc = d.numGen)  ");
        sql.append(" AND wf.idWorkFlow = (SELECT MAX(idWorkFlow) FROM workflows wf1 WHERE wf1.idDocument = d.numGen ) ");
        sql.append(" AND wf.idVersion = vd.numVer ");
        //sql.append(" AND wf.statu = '"+HandlerWorkFlows.pending+"'"); // solicitud pendiente
        if(!ToolsHTML.isEmptyOrNull(dateFrom) && dateFrom.length()>9){
    		switch (Constants.MANEJADOR_ACTUAL) {
    		case Constants.MANEJADOR_MSSQL:
            	sql.append(" AND wf.DateCreation >= Convert(datetime,'").append(dateFrom).append("',120) ");
    			break;
    		case Constants.MANEJADOR_POSTGRES:
    			sql.append(" AND wf.DateCreation >= CAST('").append(dateFrom).append("' AS timestamp) ");
    			break;
    		case Constants.MANEJADOR_MYSQL:
    			sql.append(" AND wf.DateCreation >= TIMESTAMP('").append(dateFrom).append("') ");
    			break;
    		}
        }
        if(!ToolsHTML.isEmptyOrNull(dateTo) && dateTo.length()>9){
    		switch (Constants.MANEJADOR_ACTUAL) {
    		case Constants.MANEJADOR_MSSQL:
            	sql.append(" AND wf.DateCreation <= Convert(datetime,'").append(dateTo).append("',120) ");
    			break;
    		case Constants.MANEJADOR_POSTGRES:
    			sql.append(" AND wf.DateCreation <= CAST('").append(dateTo).append("' AS timestamp) ");
    			break;
    		case Constants.MANEJADOR_MYSQL:
    			sql.append(" AND wf.DateCreation <= TIMESTAMP('").append(dateTo).append("') ");
    			break;
    		}
        }
        //log.debug("getNumPrintingPending sql = " + sql.toString());
        ////System.out.println("---getNumPrintingPending sql = " + sql.toString());
        Connection con = null;
        PreparedStatement st = null;
        ResultSet rs = null;
        
        try{
        	con = JDBCUtil.getConnection(Thread.currentThread().getStackTrace()[1].getMethodName());
	        st = con.prepareStatement(JDBCUtil.replaceCastMysql(sql.toString()));
			rs = st.executeQuery();
			
			if (rs.next()) {
				num = rs.getInt("num");
			}
        }catch(Exception e){
        	//System.out.println("Error en getNumWorkflowsApprovalExpired: " + e);
        	log.error(e.getMessage() + " SQL: " + sql, e);
        } finally{
        	try {
				rs.close();
			} catch (Exception ee) {
				// TODO: handle exception
			}
        	try {
				st.close();
			} catch (Exception ee) {
				// TODO: handle exception
			}
        	try {
				con.close();
			} catch (Exception ee) {
				// TODO: handle exception
			}
        }
        
        return num;
    }
}
