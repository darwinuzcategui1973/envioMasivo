/**
 * Title: FileAttachDocumentsDBStore.java<br>
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
import java.sql.Connection;
import java.sql.ResultSet;
import java.util.Hashtable;


/**
 * This class represents abstract Database Store
 * @author  Consis International
 * @version 1.0
 * @since   JDK1.3
 */
public abstract class FileAttachDocumentsDBStore {
   /**
    * Constructs a new Database Store
    */
   public FileAttachDocumentsDBStore() {
      connection = null;
   }

   /**
    * Return a  connection
    * @return Connection as connection
    */
   public Connection getConnection() {
      return connection;
   }

   /**
    * Set a  connection
    * @param c  of type Connection
    */
   public void setConnection(Connection c) {
      connection = c;
   }

   /**
    * Create a new conection to Database. Must be implemented by any
    * subclass
    * @param obj  represent a String driver of propietary DBMS
    * @param obj1 represent a String URL of propietary DBMS
    * @param obj2 represent a Properties credentials
    */
   public abstract void connect(Object obj, Object obj1, Object obj2)
      throws Exception;

   /**
    * Represent a generic form to interact with Dabase Storage.
    * Must be implemented by any subclass
    * @param fis  represent a FileInputStream
    * @param length represent size of file
    */
   public abstract void store(FileInputStream fis,
      UploadedWorkflowFile uploadedWorkflowFile, long length)
      throws Exception;

   /**
    * Update the operation Id  of a file attached
    * @param uploadedWorkflowId  the uploaded to update
    * @param operationId the operation to assing
    */
   public abstract void assingOperationId(String uploadedWorkflowId,
      String operationId) throws Exception;

   /**
    * Represent a generic form to interact with Dabase Storage.
    */
   public abstract ResultSet load(UploadedWorkflowFile uploadedWorkflowFile)
      throws Exception;

   /**
    * Load a file from Database
    */
   public abstract Hashtable loadSqlFileList(String operId)
      throws Exception;

   /**
    *  Closes the DB
    */
   public void closeDB() {
      try {
         if (getConnection() != null) {
            connection.close();
         }
      } catch (Exception e) {
         //System.out.println("Could not close the current connection.");
         e.printStackTrace();
      }
   }

   private Connection connection;
}
