/**
 * Title: FileAttachDocumentsDefaultDBStore.java<br>
 * Copyright: (c) 2003 Consis International<br>
 * Company: Consis International<br>
 * @author Consis International (CON)
 * @version Acsel-e v2.2
 * <br>
 * Changes:<br>
 * <ul>
 *     <li> 2003-05-16 (CON) Applied Jalopy on this source
 * </ul>
 **/


// Copyright (c) 2000 Consis International
package com.desige.webDocuments.utils.upload;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Hashtable;

import com.desige.webDocuments.persistent.utils.JDBCUtil;


/**
 * This class represents Default Database Store implementation
 * @author  Consis International
 * @version 1.0
 * @since   JDK1.3
 */
public class FileAttachDocumentsDefaultDBStore
   extends FileAttachDocumentsDBStore {
   /**
    * Constructs a new Database Store
    */
   public FileAttachDocumentsDefaultDBStore() {}

   /**
    * Return a  connection
    * @return Connection as connection
    */
   public Connection getConnection() {
      if (super.getConnection() == null) {
         try {
            Connection c = DriverManager.getConnection("jdbc:jdc:jdcpool");
            super.setConnection(c);
         } catch (Exception e) {
            //System.out.println("No se pudo establecer la conexion: " + e.getMessage());
         }
      }

      return super.getConnection();
   }

   /**
    * Create a new conection to Database.
    * @param p1 represent a String driver of propietary DBMS
    * @param p2 represent a String URL of propietary DBMS
    * @param p3 represent a Properties credentials
    */
   public void connect(Object p1, Object p2, Object p3)
      throws Exception {
      if (p1 instanceof Connection) {
         super.setConnection((Connection) p1);
      } else {
         Connection c = DriverManager.getConnection("jdbc:jdc:jdcpool");
         super.setConnection(c);
      }
   }

   /**
    * Represent a generic form to interact with Dabase Storage.
    * @param fis  represent a FileInputStream
    * @param name represent a name of file
    * @param length represent size of file
    */
   public void store(FileInputStream is,
      UploadedWorkflowFile uploadedWorkflowFile, long length)
      throws Exception {
      insertFileSql(is, uploadedWorkflowFile, length);
   }

   /**
    * Update the operation Id  of a file attached
    * @param uploadedWorkflowId  the uploaded to update
    * @param operationId the operation to assing
    */
   public void assingOperationId(String uploadedWorkflowId, String operationId)
      throws Exception {
      this.updateOperId(uploadedWorkflowId, operationId);
   }

   /**
    * Represent a generic form to interact with Dabase Storage.
    * @param fis  represent a FileInputStream
    * @param name represent a name of file
    * @param length represent size of file
    */
   public ResultSet load(UploadedWorkflowFile uploadedWorkflowFile)
      throws Exception {
      return loadFileSql(uploadedWorkflowFile);
   }

   /**
    * Insert a new file into a Database
    * @param fis  represent a FileInputStream
    * @param name represent a name of file
    * @param length represent size of file
    */
   private void insertFileSql(FileInputStream fis,
      UploadedWorkflowFile uploadedWorkflowFile, long length)
      throws Exception {
      String sql = "INSERT INTO " + FileAttachDocumentsServices.SQLUPLOADTABLE
         + " (" + FileAttachDocumentsServices.SQLUPLOADDOCUMENTID + ","
         + FileAttachDocumentsServices.SQLUPLOADDOCUMENTFILENAME + ","
         + FileAttachDocumentsServices.SQLUPLOADCONTENTTYPE + ","
         + FileAttachDocumentsServices.SQLUPLOADOPERATIONID + ","
         + FileAttachDocumentsServices.SQLUPLOADDOCUMENTDESCRIPTION + ","
         + FileAttachDocumentsServices.SQLUPLOADDOCUMENTDATE + ","
         + FileAttachDocumentsServices.SQLUPLOADFILE + ") "
         + " VALUES (?,?,?,?,?,?,?)";
      PreparedStatement ps = getConnection().prepareStatement(JDBCUtil.replaceCastMysql(sql));
      ps.setInt(1, uploadedWorkflowFile.getDocumentID());
      ps.setString(2, uploadedWorkflowFile.getDocumentFileName());
      ps.setString(3, uploadedWorkflowFile.getContentType());
      ps.setString(4, uploadedWorkflowFile.getOperationID());
      ps.setString(5, uploadedWorkflowFile.getDocumentDescription());
      ps.setDate(6, uploadedWorkflowFile.getDocumentDate());
      
      //INFO:ZIP - NO SE COMPRIME ESTE POR NO SABER CUAL ES SU UTILIDAD DENTRO DE QWEBDS
      ps.setBinaryStream(7, fis, (int) length);

      int modified = ps.executeUpdate();
      ps.close();
      fis.close();
      this.closeDB(); ////////added by Alden////////
   }

   /**
    * Update the operation Id  of a file attached
    * @param uploadedWorkflowFileId  the uploaded to update
    * @param operationId the operation to assing
    */
   private void updateOperId(String uploadedWorkflowFileId, String operationId)
      throws Exception {
      String sql = "UPDATE " + FileAttachDocumentsServices.SQLUPLOADTABLE
         + " SET " + FileAttachDocumentsServices.SQLUPLOADOPERATIONID
         + " =  ? " + " WHERE "
         + FileAttachDocumentsServices.SQLUPLOADDOCUMENTID + " =  ? ";

      PreparedStatement ps = getConnection().prepareStatement(JDBCUtil.replaceCastMysql(sql));
      ps.setString(1, operationId);
      ps.setString(2, uploadedWorkflowFileId);

      int modified = ps.executeUpdate();
      ps.close();
      this.closeDB(); ////////added by Alden////////
   }

   /**
    * Load a file from Database
    * @param file represent filename
    */
   public void loadToFileSql(UploadedWorkflowFile uploadedWorkflowFile)
      throws Exception {
      byte[] data;
      data = null;

      Statement myStatement = null;
      String sql = "SELECT BINARYFILE FROM UPLOADS WHERE FILENAME='"
         + uploadedWorkflowFile.getDocumentFileName() + "'";
      myStatement = getConnection().createStatement();

      ResultSet result = myStatement.executeQuery(sql);

      if (result != null) {
         while (result.next()) {
            data = result.getBytes("BINARYFILE");

            FileOutputStream fos = null;
            fos = new FileOutputStream(String.valueOf(String.valueOf(
                        (new StringBuffer(String.valueOf(String.valueOf(
                                 "c:\\tmp\\uploadbean2")))).append("\\").append(uploadedWorkflowFile
                           .getDocumentFileName()))));
            fos.write(data);
            fos.close();
         }

         result.close();
      }

      myStatement.close();

      this.closeDB(); ////////added by Alden////////
   }

   /**
    * Load a file from Database
    * @param file represent filename
    */
   public Hashtable loadSqlFileList(String operId) throws Exception {
      Hashtable fileList = new Hashtable(); // list of all - UploadedFiles
      Statement myStatement = null;
      String sql =
         "SELECT DOCUMENTID,DOCUMENTFILENAME,CONTENTTYPE,OPERATIONID,DOCUMENTDESCRIPTION,DOCUMENTDATE FROM UPLOADS WHERE OPERATIONID='"
         + operId + "'";
      myStatement = getConnection().createStatement();

      ResultSet result = myStatement.executeQuery(sql);

      while (result.next()) {
         fileList.put(result.getString("DOCUMENTFILENAME"),
            new UploadedWorkflowFile(null, result.getInt("DOCUMENTID"),
               result.getString("DOCUMENTFILENAME"),
               result.getString("CONTENTTYPE"),
               result.getString("OPERATIONID"),
               result.getString("DOCUMENTDESCRIPTION"),
               result.getDate("DOCUMENTDATE")));
      }

      result.close();
      myStatement.close();

      this.closeDB(); ////////added by Alden////////

      return fileList;
   }

   /**
    * Load a file from Database
    * @param file represent filename
    */
   public ResultSet loadFileSql(UploadedWorkflowFile uploadedWorkflowFile)
      throws Exception {
      Statement myStatement = null;
      String sql = "SELECT BINARYFILE FROM UPLOADS WHERE DOCUMENTID="
         + uploadedWorkflowFile.getDocumentID();
      myStatement = getConnection().createStatement();

      return myStatement.executeQuery(sql);
   }

   public byte[] loadFileSql(int fileId) throws Exception {
      byte[] data = null;
      Statement myStatement = null;
      String sql = "SELECT BINARYFILE FROM UPLOADS WHERE uploadid='" + fileId
         + "'";
      myStatement = getConnection().createStatement();

      ResultSet rs = myStatement.executeQuery(sql);

      if (rs.next())
         data = rs.getBytes("BINARYFILE");

      rs.close();
      myStatement.close();
      this.closeDB(); ////////added by Alden////////

      return data;
   }
}
